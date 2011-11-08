package com.windowsazure.samples.android.storageclient;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Random;
import java.util.concurrent.Callable;
import java.util.concurrent.CompletionService;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorCompletionService;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

final class BlobOutputStream extends OutputStream {

	private static Random m_BlockSequenceGenerator = new Random();

	private CloudBlob m_ParentBlobRef;

	private BlobType m_StreamType;

	volatile boolean m_StreamFaulted;

	Object m_LastErrorLock;

	IOException m_LastError;

	private long m_BlockIdSequenceNumber;

	private ArrayList m_BlockList;

	private long m_CurrentPageOffset;

	private long m_FirstNonZeroBufferedByte;

	private long m_LastNonZeroBufferedByte;

	private ByteArrayOutputStream m_OutBuffer;

	private int m_CurrentBufferedBytes;

	private int m_InternalWriteThreshold;

	private volatile int m_OutstandingRequests;
	private ExecutorService m_ThreadExecutor;
	private CompletionService m_CompletionService;
	String m_LeaseID;
	protected BlobOutputStream(CloudBlob cloudBlob, String s)
			throws StorageException, NotImplementedException {
		m_StreamType = BlobType.UNSPECIFIED;
		m_LastErrorLock = new Object();
		m_BlockIdSequenceNumber = -1L;
		m_FirstNonZeroBufferedByte = -1L;
		m_LastNonZeroBufferedByte = -1L;
		m_InternalWriteThreshold = -1;
		m_LeaseID = s;
		m_ParentBlobRef = cloudBlob;
		m_ParentBlobRef.assertCorrectBlobType();
		m_OutBuffer = new ByteArrayOutputStream();
		m_StreamFaulted = false;
		m_ThreadExecutor = Executors.newFixedThreadPool(1);
		m_CompletionService = new ExecutorCompletionService(m_ThreadExecutor);
	}
	protected BlobOutputStream(CloudBlockBlob cloudblockblob, String s)
			throws StorageException, NotImplementedException {
		this(((CloudBlob) (cloudblockblob)), s);
		m_BlockIdSequenceNumber = (long) m_BlockSequenceGenerator
				.nextInt(0x7fffffff)
				+ (long) m_BlockSequenceGenerator.nextInt(0x7ffe795f);
		m_BlockList = new ArrayList();
		m_StreamType = BlobType.BLOCK_BLOB;
		m_InternalWriteThreshold = m_ParentBlobRef.m_ServiceClient
				.getWriteBlockSizeInBytes();
	}
	protected BlobOutputStream(CloudPageBlob cloudpageblob, long l, String s)
			throws StorageException, IllegalArgumentException,
			NotImplementedException {
		this((cloudpageblob), s);
		m_StreamType = BlobType.PAGE_BLOB;
		m_InternalWriteThreshold = (int) Math.min(
				m_ParentBlobRef.m_ServiceClient
						.getPageBlobStreamWriteSizeInBytes(), l);
		if (l % 512L != 0L)
			throw new IllegalArgumentException(
					"Page blob length must be multiple of 512.");
		cloudpageblob.create(l, s);
	}
	private void checkStreamState() throws IOException {
		synchronized (m_LastErrorLock) {
			if (m_StreamFaulted)
				throw m_LastError;
		}
	}
	@Override
	public void close() throws IOException {
		flush();
		checkStreamState();
		IOException ioexception = null;
		synchronized (m_LastErrorLock) {
			m_StreamFaulted = true;
			ioexception = m_LastError = new IOException(
					"Stream is already closed.");
		}
		while (m_OutstandingRequests > 0)
			waitForTaskToComplete();
		m_ThreadExecutor.shutdown();
		synchronized (m_LastErrorLock) {
			if (ioexception != m_LastError)
				throw m_LastError;
		}
		try {
			commit();
		} catch (StorageException storageexception) {
			throw Utility.initIOException(storageexception);
		} catch (NotImplementedException e) {
			throw Utility.initIOException(e);
		}
	}
	private void commit() throws StorageException, IOException,
			NotImplementedException {
		if (m_StreamType == BlobType.BLOCK_BLOB) {
			CloudBlockBlob cloudblockblob = (CloudBlockBlob) m_ParentBlobRef;
			cloudblockblob.commitBlockList(m_BlockList, m_LeaseID);
		} else if (m_StreamType == BlobType.PAGE_BLOB)
			m_ParentBlobRef.uploadProperties(m_LeaseID);
	}
	private synchronized void dispatchWrite(final int writeLength,
			boolean useSparsePageBlob) throws IOException {
		if (writeLength == 0)
			return;
		Callable callable = null;
		if (m_OutstandingRequests > 2)
			waitForTaskToComplete();
		final ByteArrayInputStream bufferRef = new ByteArrayInputStream(
				m_OutBuffer.toByteArray());
		if (m_StreamType == BlobType.BLOCK_BLOB) {
			final CloudBlockBlob blobRef = (CloudBlockBlob) m_ParentBlobRef;
			final String blockID = Base64.encode(Utility
					.getBytesFromLong(m_BlockIdSequenceNumber++));
			m_BlockList
					.add(new BlockEntry(blockID, BlockSearchMode.UNCOMMITTED));
			callable = new Callable() {

				@Override
				public Void call() {
					try {
						blobRef.uploadBlock(blockID, m_LeaseID, bufferRef,
								writeLength);
					} catch (IOException ioexception) {
						synchronized (m_LastErrorLock) {
							m_StreamFaulted = true;
							m_LastError = ioexception;
						}
					} catch (StorageException storageexception) {
						synchronized (m_LastErrorLock) {
							m_StreamFaulted = true;
							m_LastError = Utility
									.initIOException(storageexception);
						}
					} catch (NotImplementedException e) {
						synchronized (m_LastErrorLock) {
							m_StreamFaulted = true;
							m_LastError = Utility.initIOException(e);
						}
					}
					return null;
				}
			};
		} else if (m_StreamType == BlobType.PAGE_BLOB) {
			final CloudPageBlob blobRef = (CloudPageBlob) m_ParentBlobRef;
			long l = m_CurrentPageOffset;
			long l1 = writeLength;
			if (useSparsePageBlob) {
				if (m_LastNonZeroBufferedByte == -1L) {
					m_FirstNonZeroBufferedByte = -1L;
					m_LastNonZeroBufferedByte = -1L;
					m_CurrentBufferedBytes = 0;
					m_CurrentPageOffset += writeLength;
					m_OutBuffer = new ByteArrayOutputStream();
					return;
				}
				long l2 = m_FirstNonZeroBufferedByte
						- m_FirstNonZeroBufferedByte % 512L;
				l = m_CurrentPageOffset + l2;
				l1 = (m_LastNonZeroBufferedByte - l2)
						+ (512L - m_LastNonZeroBufferedByte % 512L);
				m_FirstNonZeroBufferedByte = -1L;
				m_LastNonZeroBufferedByte = -1L;
				if (l2 > 0L && l2 != bufferRef.skip(l2))
					throw Utility.initIOException(Utility
							.generateNewUnexpectedStorageException(null));
			}
			final long opWriteLength = l1;
			final long opOffset = l;
			m_CurrentPageOffset += writeLength;

			final CloudPageBlob val$blobRef;
			final ByteArrayInputStream val$bufferRef;
			final long val$opOffset;
			final long val$opWriteLength;
			final BlobOutputStream this$0;

			{
				this$0 = BlobOutputStream.this;
				val$blobRef = blobRef;
				val$bufferRef = bufferRef;
				val$opOffset = opOffset;
				val$opWriteLength = opWriteLength;
			}

			callable = new Callable() {

				@Override
				public Void call() {
					try {
						blobRef.uploadPages(bufferRef, opOffset, opWriteLength,
								m_LeaseID);
					} catch (IOException ioexception) {
						synchronized (m_LastErrorLock) {
							m_StreamFaulted = true;
							m_LastError = ioexception;
						}
					} catch (StorageException storageexception) {
						synchronized (m_LastErrorLock) {
							m_StreamFaulted = true;
							m_LastError = Utility
									.initIOException(storageexception);
						}
					} catch (IllegalArgumentException e) {
						synchronized (m_LastErrorLock) {
							m_StreamFaulted = true;
							m_LastError = Utility.initIOException(e);
						}
					} catch (NotImplementedException e) {
						synchronized (m_LastErrorLock) {
							m_StreamFaulted = true;
							m_LastError = Utility.initIOException(e);
						}
					}
					return null;
				}

			};
		}
		m_CompletionService.submit(callable);
		m_OutstandingRequests++;
		m_CurrentBufferedBytes = 0;
		m_OutBuffer = new ByteArrayOutputStream();
	}
	public synchronized void flush(boolean useSparsePageBlob)
			throws IOException {
		checkStreamState();
		if (m_StreamType == BlobType.PAGE_BLOB && m_CurrentBufferedBytes > 0
				&& m_CurrentBufferedBytes % 512 != 0) {
			throw new IOException(
					String.format(
							"Page data must be a multiple of 512 bytes, buffer currently contains %d bytes.",
							new Object[] { Integer
									.valueOf(m_CurrentBufferedBytes) }));
		} else {
			dispatchWrite(m_CurrentBufferedBytes, useSparsePageBlob);
			return;
		}
	}
	private void waitForTaskToComplete() throws IOException {
		try {
			Future future = m_CompletionService.take();
			future.get();
		} catch (InterruptedException interruptedexception) {
			throw Utility.initIOException(interruptedexception);
		} catch (ExecutionException executionexception) {
			throw Utility.initIOException(executionexception);
		}
		m_OutstandingRequests--;
	}
	@Override
	public void write(byte abyte0[]) throws IOException {
		write(abyte0, 0, abyte0.length);
	}
	public void write(byte abyte0[], int i, int j, boolean useSparsePageBlob)
			throws IOException {
		if (i < 0 || j < 0 || j > abyte0.length - i) {
			throw new IndexOutOfBoundsException();
		} else {
			writeInternal(abyte0, i, j, useSparsePageBlob);
			return;
		}
	}
	public void write(InputStream inputstream, long l) throws IOException,
			StorageException {
		byte buffer[] = new byte[(int) l];
		if (inputstream.read(buffer) != l) {
			throw new IOException("The inputStream didn't have enough content");
		}
		this.write(buffer);
	}
	@Override
	public void write(int i) throws IOException {
		write(new byte[] { (byte) (i & 0xff) });
	}
	private synchronized void writeInternal(byte abyte0[], int i, int j,
			boolean useSparsePageBlob) throws IOException {
		do {
			if (j <= 0)
				break;
			checkStreamState();
			int k = m_InternalWriteThreshold - m_CurrentBufferedBytes;
			int l = Math.min(k, j);
			if (useSparsePageBlob) {
				for (int i1 = 0; i1 < l; i1++) {
					if (abyte0[i1 + i] == 0)
						continue;
					if (m_FirstNonZeroBufferedByte == -1L)
						m_FirstNonZeroBufferedByte = m_CurrentBufferedBytes
								+ i1;
					m_LastNonZeroBufferedByte = m_CurrentBufferedBytes + i1;
				}

			}
			m_OutBuffer.write(abyte0, i, l);
			m_CurrentBufferedBytes += l;
			i += l;
			j -= l;
			if (m_CurrentBufferedBytes == m_InternalWriteThreshold)
				dispatchWrite(m_InternalWriteThreshold, useSparsePageBlob);
		} while (true);
	}

}
