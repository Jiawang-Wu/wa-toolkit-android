package com.windowsazure.samples.android.storageclient;

import java.io.*;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Random;
import java.util.concurrent.*;

final class BlobOutputStream extends OutputStream
{

    protected BlobOutputStream(CloudBlob cloudblob, String s)
        throws NotImplementedException, StorageException
    {
    	throw new NotImplementedException();
    }

    protected BlobOutputStream(CloudBlockBlob cloudblockblob, String s)
        throws NotImplementedException, StorageException
    {
    	throw new NotImplementedException();
    }

    protected BlobOutputStream(CloudPageBlob cloudpageblob, long l, String s)
        throws NotImplementedException, StorageException
    {
    	throw new NotImplementedException();
    }

    public void close()
        throws IOException
    {
    }

    public synchronized void flush()
        throws IOException
    {
    }

    public void write(byte abyte0[])
        throws IOException
    {
    }

    public void write(byte abyte0[], int i, int j)
        throws IOException
    {
    }

    public void write(InputStream inputstream, long l)
        throws NotImplementedException, IOException, StorageException
    {
    	throw new NotImplementedException();
    }

    public void write(int i)
        throws IOException
    {
    }

    private void checkStreamState()
        throws NotImplementedException, IOException
    {
    }

    private void commit()
        throws NotImplementedException, StorageException, IOException
    {
    }

    private synchronized void dispatchWrite(final int writeLength)
        throws NotImplementedException, IOException
    {
    }

    private void waitForTaskToComplete()
        throws NotImplementedException, IOException
    {
    }

    private synchronized void writeInternal(byte abyte0[], int i, int j)
        throws NotImplementedException, IOException
    {
    }

    private static Random m_BlockSequenceGenerator = new Random();
    private CloudBlob m_ParentBlobRef;
    private BlobType m_StreamType;
    volatile boolean m_StreamFaulted;
    Object m_LastErrorLock;
    IOException m_LastError;
    private MessageDigest m_Md5Digest;
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
}
