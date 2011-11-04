package com.windowsazure.samples.android.storageclient;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;

import org.apache.http.client.methods.HttpGet;

final class BlobInputStream extends InputStream {

	private CloudBlob m_ParentBlobRef;

	private volatile boolean m_StreamFaulted;

	private IOException m_LastError;

	private long m_StreamLength;

	private int m_ReadSize;

	private ByteArrayInputStream m_CurrentBuffer;

	private long m_MarkedPosition;

	private int m_MarkExpiry;

	private ArrayList m_PageBlobRanges;

	private int m_CurrentPageRangeIndex;

	private long m_CurrentAbsoluteReadPosition;

	private long m_BufferStartOffset;

	private int m_BufferSize;

	protected BlobInputStream(CloudBlob cloudblob) throws StorageException,
			NotImplementedException, UnsupportedEncodingException, IOException {
		m_StreamLength = -1L;
		m_ParentBlobRef = cloudblob;
		m_ParentBlobRef.assertCorrectBlobType();
		m_StreamFaulted = false;
		m_CurrentAbsoluteReadPosition = 0L;
		m_ReadSize = cloudblob.m_ServiceClient
				.getStreamMinimumReadSizeInBytes();
		cloudblob.downloadAttributes();
		HttpGet httpurlconnection = new HttpGet();
		m_StreamLength = cloudblob.getProperties().length;
		if (m_ParentBlobRef.getProperties().blobType == BlobType.PAGE_BLOB)
			m_PageBlobRanges = ((CloudPageBlob) cloudblob).downloadPageRanges();
		else if (m_ParentBlobRef.getProperties().blobType == BlobType.BLOCK_BLOB)
			throw new IllegalArgumentException(
					"The UseSparsePageBlob option is not applicable of Block Blob streams.");
		reposition(0L);
	}

	@Override
	public synchronized int available() throws IOException {
		return m_BufferSize
				- (int) (m_CurrentAbsoluteReadPosition - m_BufferStartOffset);
	}

	private synchronized void checkStreamState() throws IOException {
		if (m_StreamFaulted)
			throw m_LastError;
		else
			return;
	}

	@Override
	public synchronized void close() throws IOException {
		m_CurrentBuffer = null;
		m_StreamFaulted = true;
		m_LastError = new IOException("Stream is closed");
	}
	private synchronized void dispatchRead(int i, boolean useSparsePageBlob)
			throws IOException, NotImplementedException {
		try {
			byte abyte0[] = new byte[i];
			if (useSparsePageBlob) {
				long l = m_CurrentAbsoluteReadPosition;
				long l1 = m_CurrentAbsoluteReadPosition + i;
				PageRange pagerange = getCurrentRange();
				if (pagerange != null) {
					l = pagerange.startOffset;
					l1 = pagerange.endOffset + 1L;
					do {
						if (m_CurrentAbsoluteReadPosition <= pagerange.endOffset)
							break;
						m_CurrentPageRangeIndex++;
						pagerange = getCurrentRange();
						if (pagerange == null)
							break;
						l = pagerange.startOffset;
						l1 = pagerange.endOffset + 1L;
					} while (true);
				}
				if (pagerange != null) {
					int j = m_CurrentPageRangeIndex + 1;
					for (PageRange pagerange1 = j >= m_PageBlobRanges.size() ? null
							: (PageRange) m_PageBlobRanges.get(j); j < m_PageBlobRanges
							.size() - 1
							&& m_CurrentAbsoluteReadPosition + i >= pagerange1.endOffset;) {
						j++;
						pagerange1 = (PageRange) m_PageBlobRanges.get(j);
						l1 = pagerange1.endOffset + 1L;
					}

					l1 = Math.min(l1, m_CurrentAbsoluteReadPosition + i);
					int k = (int) (l - m_CurrentAbsoluteReadPosition);
					int i1 = (int) Math.min(i - k, l1 - l);
					if (i1 > 0)
						m_ParentBlobRef.downloadRangeInternal(l, i1, abyte0, k);
				}
			} else {
				m_ParentBlobRef.downloadRangeInternal(
						m_CurrentAbsoluteReadPosition, i, abyte0, 0);
			}
			m_CurrentBuffer = new ByteArrayInputStream(abyte0);
			m_BufferSize = i;
			m_BufferStartOffset = m_CurrentAbsoluteReadPosition;
		} catch (StorageException storageexception) {
			m_StreamFaulted = true;
			m_LastError = Utility.initIOException(storageexception);
			throw m_LastError;
		}
	}
	private PageRange getCurrentRange() {
		if (m_CurrentPageRangeIndex >= m_PageBlobRanges.size())
			return null;
		else
			return (PageRange) m_PageBlobRanges.get(m_CurrentPageRangeIndex);
	}
	@Override
	public synchronized void mark(int i) {
		m_MarkedPosition = m_CurrentAbsoluteReadPosition;
		m_MarkExpiry = i;
	}
	@Override
	public boolean markSupported() {
		return true;
	}
	@Override
	public int read() throws IOException {
		byte abyte0[] = new byte[1];
		read(abyte0, 0, 1);
		return abyte0[0];
	}
	@Override
	public int read(byte abyte0[]) throws IOException {
		return read(abyte0, 0, abyte0.length);
	}
	public int read(byte abyte0[], int i, int j, boolean useSparsePageBlob)
			throws IOException, NotImplementedException {
		if (i < 0 || j < 0 || j > abyte0.length - i)
			throw new IndexOutOfBoundsException();
		else
			return readInternal(abyte0, i, j, useSparsePageBlob);
	}
	private synchronized int readInternal(byte abyte0[], int i, int j,
			boolean useSparsePageBlob) throws IOException,
			NotImplementedException {
		checkStreamState();
		if ((m_CurrentBuffer == null || m_CurrentBuffer.available() == 0)
				&& m_CurrentAbsoluteReadPosition < m_StreamLength)
			dispatchRead(
					(int) Math.min(m_ReadSize, m_StreamLength
							- m_CurrentAbsoluteReadPosition), useSparsePageBlob);
		j = Math.min(j, m_ReadSize);
		int k = m_CurrentBuffer.read(abyte0, i, j);
		if (k > 0) {
			m_CurrentAbsoluteReadPosition += k;
		}
		if (m_MarkExpiry > 0
				&& m_MarkedPosition + m_MarkExpiry < m_CurrentAbsoluteReadPosition) {
			m_MarkedPosition = 0L;
			m_MarkExpiry = 0;
		}
		return k;
	}
	private synchronized void reposition(long l) {
		m_CurrentAbsoluteReadPosition = l;
		m_CurrentBuffer = new ByteArrayInputStream(new byte[0]);
	}
	@Override
	public synchronized void reset() throws IOException {
		if (m_MarkedPosition + m_MarkExpiry < m_CurrentAbsoluteReadPosition) {
			throw new IOException("Mark expired!");
		} else {
			reposition(m_MarkedPosition);
			return;
		}
	}
	@Override
	public synchronized long skip(long l) throws IOException {
		if (l == 0L)
			return 0L;
		if (l < 0L || m_CurrentAbsoluteReadPosition + l > m_StreamLength) {
			throw new IndexOutOfBoundsException();
		} else {
			reposition(m_CurrentAbsoluteReadPosition + l);
			return l;
		}
	}
	protected long writeTo(OutputStream outputstream) throws IOException {
		byte abyte0[] = new byte[8192];
		long l = 0L;
		for (int i = read(abyte0); i != -1; i = read(abyte0)) {
			outputstream.write(abyte0, 0, i);
			l += i;
		}

		return l;
	}
}
