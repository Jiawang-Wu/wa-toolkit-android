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
import java.util.concurrent.ExecutorCompletionService;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public final class BlobOutputStream extends OutputStream {

	private static Random m_BlockSequenceGenerator = new Random();

	private CloudBlob m_ParentBlobRef;

	private BlobType m_StreamType;

	volatile boolean m_StreamFaulted;

	Object m_LastErrorLock;

	IOException m_LastError;

	private long m_BlockIdSequenceNumber;

	private ArrayList<BlockEntry> m_BlockList;

	private ByteArrayOutputStream m_OutBuffer;

	private int m_CurrentBufferedBytes;

	private int m_InternalWriteThreshold;

	private volatile int m_OutstandingRequests;
	private ExecutorService m_ThreadExecutor;
	private CompletionService<Void> m_CompletionService;

	protected BlobOutputStream(CloudBlob blob)
			throws StorageException, NotImplementedException {
		m_StreamType = BlobType.UNSPECIFIED;
		m_LastErrorLock = new Object();
		m_BlockIdSequenceNumber = -1L;
		m_InternalWriteThreshold = -1;
		m_ParentBlobRef = blob;
		m_ParentBlobRef.assertCorrectBlobType();
		m_OutBuffer = new ByteArrayOutputStream();
		m_StreamFaulted = false;
		m_ThreadExecutor = Executors.newFixedThreadPool(1);
		m_CompletionService = new ExecutorCompletionService<Void>(m_ThreadExecutor);
	}
	protected BlobOutputStream(CloudBlockBlob blob)
			throws StorageException, NotImplementedException {
		this((CloudBlob) (blob));
		m_BlockIdSequenceNumber = (long) m_BlockSequenceGenerator.nextInt(0x7fffffff)
				+ (long) m_BlockSequenceGenerator.nextInt(0x7ffe795f);
		m_BlockList = new ArrayList<BlockEntry>();
		m_StreamType = BlobType.BLOCK_BLOB;
		m_InternalWriteThreshold = 0x400000;
	}
	private void checkStreamState() throws IOException {
		synchronized (m_LastErrorLock) {
			if (m_StreamFaulted)
			{
				throw m_LastError;
			}
		}
	}
	@Override
	public void close() throws IOException {
		flush();
		checkStreamState();
		IOException ioexception = null;
		synchronized (m_LastErrorLock) {
			m_StreamFaulted = true;
			ioexception = m_LastError = new IOException("Stream is already closed.");
		}
		while (m_OutstandingRequests > 0)
		{
			waitForTaskToComplete();
		}
		m_ThreadExecutor.shutdown();
		synchronized (m_LastErrorLock) {
			if (ioexception != m_LastError)
			{
				throw m_LastError;
			}
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
		if (m_StreamType == BlobType.BLOCK_BLOB) 
		{
			CloudBlockBlob cloudblockblob = (CloudBlockBlob) m_ParentBlobRef;
			cloudblockblob.commitBlockList(m_BlockList);
		}
	}
	private synchronized void dispatchWrite(final int writeLength) throws IOException {
		if (writeLength == 0)
		{
			return;
		}
		if (m_OutstandingRequests > 2)
		{
			waitForTaskToComplete();
		}
		final ByteArrayInputStream bufferRef = new ByteArrayInputStream(m_OutBuffer.toByteArray());
			final CloudBlockBlob blob = (CloudBlockBlob) m_ParentBlobRef;
			final String blockID = CloudBlockBlob.encodedBlockId(Utility.getBytesFromLong(m_BlockIdSequenceNumber++));

			m_BlockList.add(new BlockEntry(blockID, BlockSearchMode.UNCOMMITTED));
			Callable<Void> callable = new Callable<Void>() {
				public Void call() {
					try {
						blob.uploadBlock(blockID, bufferRef,writeLength);
					} 
					catch (IOException ioexception) {
						synchronized (m_LastErrorLock) {
							m_StreamFaulted = true;
							m_LastError = ioexception;
						}
					} 
					catch (StorageException storageexception) {
						synchronized (m_LastErrorLock) {
							m_StreamFaulted = true;
							m_LastError = Utility.initIOException(storageexception);
						}
					}
					return null;
				}
			};
		m_CompletionService.submit(callable);
		m_OutstandingRequests++;
		m_CurrentBufferedBytes = 0;
		m_OutBuffer = new ByteArrayOutputStream();
	}
	public synchronized void flush()
			throws IOException {
		checkStreamState();
		dispatchWrite(m_CurrentBufferedBytes);
	}
	private void waitForTaskToComplete() throws IOException {
		try {
			Future<Void> future = m_CompletionService.take();
			future.get();
		} catch (Exception exception) {
			throw Utility.initIOException(exception);
		}
		m_OutstandingRequests--;
	}
	@Override
	public void write(byte buffer[]) throws IOException {
		write(buffer, 0, buffer.length);
	}
	public void write(byte buffer[], int bufferOffset, int length)
			throws IOException {
		if (bufferOffset < 0 || length < 0 || length > buffer.length - bufferOffset) {
			throw new IndexOutOfBoundsException();
		} else {
			writeInternal(buffer, bufferOffset, length);
			return;
		}
	}
	public void write(InputStream inputStream, long length) throws IOException,
			StorageException {
		byte buffer[] = new byte[(int) length];
		if (inputStream.read(buffer) != length) {
			throw new IOException("The inputStream didn't have enough content");
		}
		this.write(buffer);
	}
	@Override
	public void write(int value) throws IOException {
		write(new byte[] { (byte) (value & 0xff) });
	}
	private synchronized void writeInternal(byte buffer[], int bufferOffset, int length) throws IOException {
		while (length > 0)
		{
			checkStreamState();
			int bufferToFillLength = m_InternalWriteThreshold - m_CurrentBufferedBytes;
			int nextWriteLength = Math.min(bufferToFillLength, length);
			m_OutBuffer.write(buffer, bufferOffset, nextWriteLength);
			m_CurrentBufferedBytes += nextWriteLength;
			bufferOffset += nextWriteLength;
			length -= nextWriteLength;
			if (m_CurrentBufferedBytes == m_InternalWriteThreshold)
			{
				dispatchWrite(m_InternalWriteThreshold);
			}
		}
	}

}
