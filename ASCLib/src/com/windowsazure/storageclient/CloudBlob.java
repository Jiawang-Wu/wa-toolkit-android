package com.windowsazure.storageclient;

import java.io.*;
import java.net.*;
import java.security.*;
import java.util.*;

public abstract class CloudBlob
    implements IListBlobItem
{

    protected CloudBlob() throws NotImplementedException, NotImplementedException
    {
    	throw new NotImplementedException();
    }

    public CloudBlob(CloudBlob cloudblob) throws NotImplementedException, NotImplementedException
    {
    	throw new NotImplementedException();
    }

    public CloudBlob(URI uri, CloudBlobClient cloudblobclient)
        throws NotImplementedException, StorageException
    {
    	throw new NotImplementedException();
    }

    public CloudBlob(URI uri, CloudBlobClient cloudblobclient, CloudBlobContainer cloudblobcontainer)
        throws NotImplementedException, StorageException
    {
    	throw new NotImplementedException();
    }

    public CloudBlob(URI uri, String s, CloudBlobClient cloudblobclient)
        throws NotImplementedException, StorageException
    {
    	throw new NotImplementedException();
    }

    public String acquireLease()
        throws NotImplementedException, StorageException
    {
    	throw new NotImplementedException();
    }

    protected void assertCorrectBlobType()
        throws NotImplementedException, StorageException
    {
    	throw new NotImplementedException();
    }

    public long breakLease()
        throws NotImplementedException, StorageException
    {
    	throw new NotImplementedException();
    }

    public void copyFromBlob(CloudBlob cloudblob)
        throws NotImplementedException, StorageException
    {
    	throw new NotImplementedException();
    }

    public void copyFromBlob(final CloudBlob sourceBlob, String s)
        throws NotImplementedException, StorageException
    {
    	throw new NotImplementedException();
    }

    public CloudBlob createSnapshot()
        throws NotImplementedException, StorageException
    {
    	throw new NotImplementedException();
    }

    public CloudBlob createSnapshot(String s)
        throws NotImplementedException, StorageException
    {
    	throw new NotImplementedException();
    }

    public void delete()
        throws NotImplementedException, StorageException
    {
    	throw new NotImplementedException();
    }

    public void delete(final String s)
        throws NotImplementedException, StorageException
    {
    	throw new NotImplementedException();
    }

    public Boolean deleteIfExists()
        throws NotImplementedException, StorageException
    {
    	throw new NotImplementedException();
    }

    public Boolean deleteIfExists(String s)
        throws NotImplementedException, StorageException
    {
    	throw new NotImplementedException();
    }

    public void download(OutputStream outputstream)
        throws NotImplementedException, StorageException, IOException
    {
    	throw new NotImplementedException();
    }

    public void downloadAttributes()
        throws NotImplementedException, StorageException
    {
    	throw new NotImplementedException();
    }

    public void downloadRange(long l, int i, byte abyte0[], int j)
        throws NotImplementedException, StorageException
    {
    	throw new NotImplementedException();
    }

    protected void downloadRangeInternal(final long blobOffset, final int length, final byte buffer[], int i)
        throws NotImplementedException, StorageException
    {
    	throw new NotImplementedException();
    }

    public boolean exists()
        throws NotImplementedException, StorageException
    {
    	throw new NotImplementedException();
    }

    public String generateSharedAccessSignature(SharedAccessPolicy sharedaccesspolicy)
        throws NotImplementedException, InvalidKeyException, IllegalArgumentException, StorageException
    {
    	throw new NotImplementedException();
    }

    public String generateSharedAccessSignature(String s)
        throws NotImplementedException, InvalidKeyException, IllegalArgumentException, StorageException
    {
    	throw new NotImplementedException();
    }

    private String generateSharedAccessSignatureCore(SharedAccessPolicy sharedaccesspolicy, String s)
        throws NotImplementedException, InvalidKeyException, IllegalArgumentException, StorageException
    {
    	throw new NotImplementedException();
    }

    String getCanonicalName(boolean flag) throws NotImplementedException, NotImplementedException
    {
    	throw new NotImplementedException();
    }

    public CloudBlobContainer getContainer()
        throws NotImplementedException, StorageException, URISyntaxException
    {
    	throw new NotImplementedException();
    }

    public HashMap getMetadata() throws NotImplementedException, NotImplementedException
    {
    	throw new NotImplementedException();
    }

    public String getName()
        throws NotImplementedException, URISyntaxException
    {
    	throw new NotImplementedException();
    }

    public CloudBlobDirectory getParent()
        throws NotImplementedException, URISyntaxException, StorageException
    {
    	throw new NotImplementedException();
    }

    public BlobProperties getProperties() throws NotImplementedException, NotImplementedException
    {
    	throw new NotImplementedException();
    }

    public URI getQualifiedUri()
        throws NotImplementedException, URISyntaxException, StorageException
    {
    	throw new NotImplementedException();
    }

    public CloudBlobClient getServiceClient() throws NotImplementedException, NotImplementedException
    {
    	throw new NotImplementedException();
    }

    public String getSnapshotID() throws NotImplementedException, NotImplementedException
    {
    	throw new NotImplementedException();
    }

    protected URI getTransformedAddress()
        throws NotImplementedException, IllegalArgumentException, URISyntaxException, StorageException
    {
    	throw new NotImplementedException();
    }

    public URI getUri() throws NotImplementedException
    {
    	throw new NotImplementedException();
    }

    public boolean isSnapshot() throws NotImplementedException, NotImplementedException
    {
    	throw new NotImplementedException();
    }

    public BlobInputStream openInputStream()
        throws NotImplementedException, StorageException
    {
    	throw new NotImplementedException();
    }

    private void parseURIQueryStringAndVerify(URI uri, CloudBlobClient cloudblobclient, boolean flag)
        throws NotImplementedException, StorageException
    {
    	throw new NotImplementedException();
    }

    public void releaseLease(String s)
        throws NotImplementedException, StorageException
    {
    	throw new NotImplementedException();
    }

    public void renewLease(String s)
        throws NotImplementedException, StorageException
    {
    	throw new NotImplementedException();
    }

    protected void setContainer(CloudBlobContainer cloudblobcontainer) throws NotImplementedException, NotImplementedException
    {
    	throw new NotImplementedException();
    }

    public void setMetadata(HashMap hashmap) throws NotImplementedException, NotImplementedException
    {
    	throw new NotImplementedException();
    }

    protected void setProperties(BlobProperties blobproperties) throws NotImplementedException, NotImplementedException
    {
    	throw new NotImplementedException();
    }

    public void setSnapshotID(String s) throws NotImplementedException, NotImplementedException
    {
    	throw new NotImplementedException();
    }

    public long tryBreakLease()
        throws NotImplementedException, StorageException
    {
    	throw new NotImplementedException();
    }

    public abstract void upload(InputStream inputstream, long l)
        throws NotImplementedException, StorageException, IOException;

    public abstract void upload(InputStream inputstream, long l, String s)
        throws NotImplementedException, StorageException, IOException;

    protected void uploadFullBlob(final InputStream inputstream, final long length, final String leaseID)
        throws NotImplementedException, StorageException, IOException
    {
    	throw new NotImplementedException();
    }

    public void uploadMetadata()
        throws NotImplementedException, StorageException
    {
    	throw new NotImplementedException();
    }

    public void uploadMetadata(String s)
        throws NotImplementedException, StorageException
    {
    	throw new NotImplementedException();
    }

    public void uploadProperties()
        throws NotImplementedException, StorageException
    {
    	throw new NotImplementedException();
    }

    public void uploadProperties(String s)
        throws NotImplementedException, StorageException
    {
    	throw new NotImplementedException();
    }

    HashMap m_Metadata;
    BlobProperties m_Properties;
    URI m_Uri;
    String m_SnapshotID;
    private CloudBlobContainer m_Container;
    protected CloudBlobDirectory m_Parent;
    private String m_Name;
    protected CloudBlobClient m_ServiceClient;
}
