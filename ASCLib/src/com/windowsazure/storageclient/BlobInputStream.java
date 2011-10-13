package com.windowsazure.storageclient;

import java.io.*;
import java.net.HttpURLConnection;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.ParseException;
import java.util.ArrayList;

final class BlobInputStream extends InputStream
{

    protected synchronized boolean getValidateBlobMd5() throws NotImplementedException
    {
    	throw new NotImplementedException();
    }

    protected synchronized void setValidateBlobMd5(boolean flag) throws NotImplementedException
    {
    	throw new NotImplementedException();
    }

    protected BlobInputStream(CloudBlob cloudblob)
        throws NotImplementedException, StorageException
    {
    	throw new NotImplementedException();
    }

    public synchronized int available()
        throws IOException
    {
    	return 0;
    }

    public synchronized void close()
        throws IOException
    {
    }

    public synchronized void mark(int i)
    {
    }

    public boolean markSupported()
    {
    	return false;
    }

    public int read()
        throws IOException
    {
        	return 0;
    }

    public int read(byte abyte0[])
        throws IOException
    {
    	return 0;
    }

    public int read(byte abyte0[], int i, int j)
        throws IOException
    {
    	return 0;
    }

    public synchronized void reset() throws IOException
    {
    }

    public synchronized long skip(long l)
        throws IOException
    {
    	return 0;
    }

    protected long writeTo(OutputStream outputstream)
        throws IOException
    {
    	return 0;
    }

    private synchronized void checkStreamState()
        throws NotImplementedException, IOException
    {
    	throw new NotImplementedException();
    }

    private synchronized void dispatchRead(int i)
        throws NotImplementedException, IOException
    {
        	throw new NotImplementedException();
    }

    private PageRange getCurrentRange() throws NotImplementedException
    {
    	throw new NotImplementedException();
    }

    private synchronized int readInternal(byte abyte0[], int i, int j)
        throws NotImplementedException, IOException
    {
        	throw new NotImplementedException();
    }

    private synchronized void reposition(long l) throws NotImplementedException
    {
    	throw new NotImplementedException();
    }

    private CloudBlob m_ParentBlobRef;
    private MessageDigest m_Md5Digest;
    private volatile boolean m_StreamFaulted;
    private IOException m_LastError;
    private long m_StreamLength;
    private int m_ReadSize;
    private boolean m_ValidateBlobMd5;
    private ByteArrayInputStream m_CurrentBuffer;
    private long m_MarkedPosition;
    private int m_MarkExpiry;
    private ArrayList m_PageBlobRanges;
    private int m_CurrentPageRangeIndex;
    private long m_CurrentAbsoluteReadPosition;
    private long m_BufferStartOffset;
    private int m_BufferSize;
}
