package com.windowsazure.storageclient;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.EnumSet;

public final class CloudBlobDirectory
    implements IListBlobItem
{

    protected CloudBlobDirectory(String s, CloudBlobClient cloudblobclient)
        throws NotImplementedException, URISyntaxException, StorageException
    {
    	throw new NotImplementedException();
    }

    protected CloudBlobDirectory(URI uri, CloudBlobDirectory cloudblobdirectory, CloudBlobClient cloudblobclient) throws NotImplementedException, NotImplementedException
    {
    	throw new NotImplementedException();
    }

    public CloudBlockBlob getBlockBlobReference(String s)
        throws NotImplementedException, URISyntaxException, StorageException
    {
    	throw new NotImplementedException();
    }

    public CloudBlockBlob getBlockBlobReference(String s, String s1)
        throws NotImplementedException, URISyntaxException, StorageException
    {
    	throw new NotImplementedException();
    }

    public CloudBlobContainer getContainer()
        throws NotImplementedException, StorageException, URISyntaxException
    {
    	throw new NotImplementedException();
    }

    public CloudPageBlob getPageBlobReference(String s)
        throws NotImplementedException, URISyntaxException, StorageException
    {
    	throw new NotImplementedException();
    }

    public CloudPageBlob getPageBlobReference(String s, String s1)
        throws NotImplementedException, URISyntaxException, StorageException
    {
    	throw new NotImplementedException();
    }

    public CloudBlobDirectory getParent()
        throws NotImplementedException, URISyntaxException, StorageException
    {
    	throw new NotImplementedException();
    }

    protected String getPrefix()
        throws NotImplementedException, StorageException
    {
    	throw new NotImplementedException();
    }

    public CloudBlobClient getServiceClient() throws NotImplementedException, NotImplementedException
    {
    	throw new NotImplementedException();
    }

    public CloudBlobDirectory getSubDirectoryReference(String s)
        throws NotImplementedException, StorageException, URISyntaxException
    {
    	throw new NotImplementedException();
    }

    public URI getUri() throws NotImplementedException
    {
    	throw new NotImplementedException();
    }

    public Iterable listBlobs()
        throws NotImplementedException, StorageException, URISyntaxException
    {
    	throw new NotImplementedException();
    }

    public Iterable listBlobs(String s)
        throws NotImplementedException, URISyntaxException, StorageException
    {
    	throw new NotImplementedException();
    }

    public Iterable listBlobs(String s, boolean flag, EnumSet enumset)
        throws NotImplementedException, URISyntaxException, StorageException
    {
    	throw new NotImplementedException();
    }

    private CloudBlobContainer m_Container;
    private CloudBlobDirectory m_Parent;
    private CloudBlobClient m_ServiceClient;
    private URI m_Uri;
    private String m_Prefix;
}
