package com.windowsazure.samples.android.storageclient;

import java.io.*;
import java.net.*;
import java.security.InvalidKeyException;
import java.util.ArrayList;

public final class CloudBlockBlob extends CloudBlob
{

    public CloudBlockBlob(CloudBlockBlob cloudblockblob)
        throws NotImplementedException, StorageException
    {
    	throw new NotImplementedException();
    }

    public CloudBlockBlob(URI uri, CloudBlobClient cloudblobclient)
        throws NotImplementedException, StorageException
    {
    	throw new NotImplementedException();
    }

    public CloudBlockBlob(URI uri, CloudBlobClient cloudblobclient, CloudBlobContainer cloudblobcontainer)
        throws NotImplementedException, StorageException
    {
    	throw new NotImplementedException();
    }

    public CloudBlockBlob(URI uri, String s, CloudBlobClient cloudblobclient)
        throws NotImplementedException, StorageException
    {
    	throw new NotImplementedException();
    }

    public void commitBlockList(Iterable iterable)
        throws NotImplementedException, StorageException
    {
    	throw new NotImplementedException();
    }

    public void commitBlockList(Iterable iterable, final String leaseID)
        throws NotImplementedException, StorageException
    {
    	throw new NotImplementedException();
    }

    public ArrayList downloadBlockList()
        throws NotImplementedException, StorageException
    {
    	throw new NotImplementedException();
    }

    public ArrayList downloadBlockList(BlockListingFilter blocklistingfilter)
        throws NotImplementedException, StorageException
    {
    	throw new NotImplementedException();
    }

    public BlobOutputStream openOutputStream()
        throws NotImplementedException, StorageException
    {
    	throw new NotImplementedException();
    }

    public BlobOutputStream openOutputStream(String s)
        throws NotImplementedException, StorageException
    {
    	throw new NotImplementedException();
    }

    public void upload(InputStream inputstream, long l)
        throws NotImplementedException, StorageException, IOException
    {
    	throw new NotImplementedException();
    }

    public void upload(InputStream inputstream, long l, String s)
        throws NotImplementedException, StorageException, IOException
    {
    	throw new NotImplementedException();
    }

    public void uploadBlock(String s, String s1, InputStream inputstream, long l)
        throws NotImplementedException, StorageException, IOException
    {
    	throw new NotImplementedException();
    }

    private void uploadBlockInternal(final String blockId, final String leaseID, final String md5, InputStream inputstream, final long length)
        throws NotImplementedException, StorageException, IOException
    {
    	throw new NotImplementedException();
    }
}
