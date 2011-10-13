package com.windowsazure.storageclient;

import java.io.IOException;
import java.net.*;
import java.security.InvalidKeyException;

public final class CloudBlobClient
{
    public CloudBlobClient(URI uri) throws NotImplementedException, NotImplementedException
    {
    	throw new NotImplementedException();
    }

    public CloudBlobClient(URI uri, StorageCredentials storagecredentials) throws NotImplementedException, NotImplementedException
    {
    	throw new NotImplementedException();
    }

    public CloudBlockBlob getBlockBlobReference(String s)
        throws NotImplementedException, URISyntaxException, StorageException
    {
    	throw new NotImplementedException();
    }

    public CloudBlockBlob getBlockBlobReference(String s, String s1)
        throws NotImplementedException, StorageException, URISyntaxException
    {
    	throw new NotImplementedException();
    }

    public int getConcurrentRequestCount() throws NotImplementedException, NotImplementedException
    {
    	throw new NotImplementedException();
    }

    public CloudBlobContainer getContainerReference(String s)
        throws NotImplementedException, URISyntaxException, StorageException
    {
    	throw new NotImplementedException();
    }

    public StorageCredentials getCredentials() throws NotImplementedException, NotImplementedException
    {
    	throw new NotImplementedException();
    }

    public String getDirectoryDelimiter() throws NotImplementedException, NotImplementedException
    {
    	throw new NotImplementedException();
    }

    public CloudBlobDirectory getDirectoryReference(String s)
        throws NotImplementedException, URISyntaxException, StorageException
    {
    	throw new NotImplementedException();
    }

    public URI getEndpoint() throws NotImplementedException, NotImplementedException
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

    public int getPageBlobStreamWriteSizeInBytes() throws NotImplementedException, NotImplementedException
    {
    	throw new NotImplementedException();
    }

    public int getSingleBlobPutThresholdInBytes() throws NotImplementedException, NotImplementedException
    {
    	throw new NotImplementedException();
    }

    public int getStreamMinimumReadSizeInBytes() throws NotImplementedException, NotImplementedException
    {
    	throw new NotImplementedException();
    }

    public int getTimeoutInMs() throws NotImplementedException, NotImplementedException
    {
    	throw new NotImplementedException();
    }

    public int getWriteBlockSizeInBytes() throws NotImplementedException, NotImplementedException
    {
    	throw new NotImplementedException();
    }

    public Iterable listContainers() throws NotImplementedException, NotImplementedException
    {
    	throw new NotImplementedException();
    }

    public Iterable listContainers(String s) throws NotImplementedException, NotImplementedException
    {
    	throw new NotImplementedException();
    }

    public Iterable listContainers(String s, ContainerListingDetails containerlistingdetails) throws NotImplementedException, NotImplementedException
    {
    	throw new NotImplementedException();
    }

    protected Iterable listContainersWithPrefix(String s, ContainerListingDetails containerlistingdetails) throws NotImplementedException, NotImplementedException
    {
    	throw new NotImplementedException();
    }

    protected void setBaseURI(URI uri) throws NotImplementedException, NotImplementedException
    {
    	throw new NotImplementedException();
    }

    public void setConcurrentRequestCount(int i) throws NotImplementedException, NotImplementedException
    {
    	throw new NotImplementedException();
    }

    protected void setCredentials(StorageCredentials storagecredentials) throws NotImplementedException, NotImplementedException
    {
    	throw new NotImplementedException();
    }

    public void setDirectoryDelimiter(String s) throws NotImplementedException, NotImplementedException
    {
    	throw new NotImplementedException();
    }

    public void setPageBlobStreamWriteSizeInBytes(int i) throws NotImplementedException, NotImplementedException
    {
    	throw new NotImplementedException();
    }

    public void setSingleBlobPutThresholdInBytes(int i)
        throws NotImplementedException, IllegalArgumentException
    {
    	throw new NotImplementedException();
    }

    public void setStreamMinimumReadSizeInBytes(int i) throws NotImplementedException, NotImplementedException
    {
    	throw new NotImplementedException();
    }

    public void setTimeoutInMs(int i) throws NotImplementedException, NotImplementedException
    {
    	throw new NotImplementedException();
    }

    public void setWriteBlockSizeInBytes(int i)
        throws NotImplementedException, IllegalArgumentException
    {
    	throw new NotImplementedException();
    }

    private URI m_Endpoint;
    private StorageCredentials m_Credentials;
    private int m_SingleBlobPutThresholdInBytes;
    private int m_WriteBlockSizeInBytes;
    private int m_PageBlobStreamWriteSizeInBytes;
    private int m_StreamMinimumReadSizeInBytes;
    protected boolean m_UsePathStyleUris;
    private int m_ConcurrentRequestCount;
    private String m_DirectoryDelimiter;
    private int m_TimeoutInMs;
}
