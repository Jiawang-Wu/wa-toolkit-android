package com.windowsazure.samples.android.storageclient;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;

public final class BlobInputStream extends InputStream {

	private CloudBlob m_ParentBlobRef;

	private volatile boolean m_StreamFaulted;

	private IOException m_LastError;

	private long m_StreamLength;

	private int m_ReadSize;

	private ByteArrayInputStream m_CurrentBuffer;

	private long m_MarkedPosition;

	private int m_MarkExpiry;

	private long m_CurrentAbsoluteReadPosition;

	private long m_BufferStartOffset;

	private int m_BufferSize;

	protected BlobInputStream(CloudBlob blob) throws StorageException,
			UnsupportedEncodingException, IOException, StorageInnerException {
		m_StreamLength = -1L;
		m_ParentBlobRef = blob;
		m_ParentBlobRef.assertCorrectBlobType();
		m_StreamFaulted = false;
		m_CurrentAbsoluteReadPosition = 0L;
		m_ReadSize = 0x400000;
		blob.downloadAttributes();
		m_StreamLength = blob.getProperties().length;
		if (m_ParentBlobRef.getProperties().blobType == BlobType.PAGE_BLOB)
		{
			throw new StorageInnerException("Page blob's aren't supported");
		}
		reposition(0L);
	}

	@Override
	public synchronized int available() throws IOException {
		return m_BufferSize - (int) (m_CurrentAbsoluteReadPosition - m_BufferStartOffset);
	}

	private synchronized void checkStreamState() throws IOException {
		if (m_StreamFaulted)
		{
			throw m_LastError;
		}
	}

	@Override
	public synchronized void close() throws IOException {
		m_CurrentBuffer = null;
		m_StreamFaulted = true;
		m_LastError = new IOException("Stream is closed");
	}
	
	private synchronized void dispatchRead(int length)
			throws IOException {
		try {
			byte buffer[] = new byte[length];
			m_ParentBlobRef.downloadRangeInternal(m_CurrentAbsoluteReadPosition, length, buffer);
			m_CurrentBuffer = new ByteArrayInputStream(buffer);
			m_BufferSize = length;
			m_BufferStartOffset = m_CurrentAbsoluteReadPosition;
		} catch (StorageException exception) {
			m_StreamFaulted = true;
			m_LastError = new IOException();
			m_LastError.initCause(exception);
			throw m_LastError;
		}
	}

	@Override
	public synchronized void mark(int markExpiry) {
		m_MarkedPosition = m_CurrentAbsoluteReadPosition;
		m_MarkExpiry = markExpiry;
	}
	@Override
	public boolean markSupported() {
		return true;
	}
	@Override
	public int read() throws IOException {
		byte buffer[] = new byte[1];
		read(buffer, 0, 1);
		return buffer[0];
	}
	@Override
	public int read(byte buffer[]) throws IOException {
		return read(buffer, 0, buffer.length);
	}
	public int read(byte buffer[], int length, int startingOffset)
			throws IOException {
		if (length < 0 || startingOffset < 0 || startingOffset > buffer.length - length)
		{
			throw new IndexOutOfBoundsException();
		}
		else
		{
			return readInternal(buffer, length, startingOffset);
		}
	}
	
	private synchronized int readInternal(byte buffer[], int length, int startingOffset) throws IOException
			{
		checkStreamState();
		if ((m_CurrentBuffer == null || m_CurrentBuffer.available() == 0)
				&& m_CurrentAbsoluteReadPosition < m_StreamLength)
		{
			dispatchRead((int) Math.min(m_ReadSize, m_StreamLength
							- m_CurrentAbsoluteReadPosition));
		}
		startingOffset = Math.min(startingOffset, m_ReadSize);
		int bytesRead = m_CurrentBuffer.read(buffer, length, startingOffset);
		if (bytesRead > 0) {
			m_CurrentAbsoluteReadPosition += bytesRead;
		}
		if (m_MarkExpiry > 0
				&& m_MarkedPosition + m_MarkExpiry < m_CurrentAbsoluteReadPosition) {
			m_MarkedPosition = 0L;
			m_MarkExpiry = 0;
		}
		return bytesRead;
	}
	private synchronized void reposition(long offset) {
		m_CurrentAbsoluteReadPosition = offset;
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
	public synchronized long skip(long length) throws IOException {
		if (length == 0L)
		{
			return 0L;
		}
		if (length < 0L || m_CurrentAbsoluteReadPosition + length > m_StreamLength) {
			throw new IndexOutOfBoundsException();
		} else {
			reposition(m_CurrentAbsoluteReadPosition + length);
			return length;
		}
	}
	protected long writeTo(OutputStream outputStream) throws IOException {
		byte buffer[] = new byte[8192];
		long offset = 0L;
		for (int bytesRead = read(buffer); bytesRead != -1; bytesRead = read(buffer)) {
			outputStream.write(buffer, 0, bytesRead);
			offset += bytesRead;
		}

		return offset;
	}
}
