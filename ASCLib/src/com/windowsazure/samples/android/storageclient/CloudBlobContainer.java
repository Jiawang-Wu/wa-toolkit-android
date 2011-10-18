package com.windowsazure.samples.android.storageclient;

import java.io.*;
import java.net.*;
import java.security.InvalidKeyException;
import java.text.ParseException;
import java.util.*;

public final class CloudBlobContainer
{

    static BlobContainerPermissions getContainerAcl(String s)
        throws NotImplementedException, IllegalArgumentException
    {
    	throw new NotImplementedException();
    }

    private CloudBlobContainer(CloudBlobClient cloudblobclient) throws NotImplementedException, NotImplementedException
    {
    	throw new NotImplementedException();
    }

    public CloudBlobContainer(String s, CloudBlobClient cloudblobclient)
        throws NotImplementedException, URISyntaxException, StorageException
    {
    	throw new NotImplementedException();
    }

    public CloudBlobContainer(URI uri, CloudBlobClient cloudblobclient)
        throws NotImplementedException, URISyntaxException, StorageException
    {
    	throw new NotImplementedException();
    }

    public void create()
        throws NotImplementedException, StorageException
    {
    	throw new NotImplementedException();
    }

    public Boolean createIfNotExist()
        throws NotImplementedException, StorageException
    {
    	throw new NotImplementedException();
    }

    public void delete()
        throws NotImplementedException, StorageException
    {
    	throw new NotImplementedException();
    }

    public Boolean deleteIfExists()
        throws NotImplementedException, StorageException
    {
    	throw new NotImplementedException();
    }

    public void downloadAttributes()
        throws NotImplementedException, StorageException
    {
    	throw new NotImplementedException();
    }

    public BlobContainerPermissions downloadPermissions()
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

    public CloudBlobDirectory getDirectoryReference(String s)
        throws NotImplementedException, URISyntaxException, StorageException
    {
    	throw new NotImplementedException();
    }

    public HashMap getMetadata() throws NotImplementedException, NotImplementedException
    {
    	throw new NotImplementedException();
    }

    public String getName() throws NotImplementedException, NotImplementedException
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

    public BlobContainerProperties getProperties() throws NotImplementedException, NotImplementedException
    {
    	throw new NotImplementedException();
    }

    public CloudBlobClient getServiceClient() throws NotImplementedException, NotImplementedException
    {
    	throw new NotImplementedException();
    }

    private String getSharedAccessCanonicalName() throws NotImplementedException, NotImplementedException
    {
    	throw new NotImplementedException();
    }

    protected URI getTransformedAddress()
        throws NotImplementedException, IllegalArgumentException, URISyntaxException, StorageException
    {
    	throw new NotImplementedException();
    }

    public URI getUri() throws NotImplementedException, NotImplementedException
    {
    	throw new NotImplementedException();
    }

    public Iterable listBlobs() throws NotImplementedException, NotImplementedException
    {
    	throw new NotImplementedException();
    }

    public Iterable listBlobs(String s) throws NotImplementedException, NotImplementedException
    {
    	throw new NotImplementedException();
    }

    public Iterable listBlobs(String s, boolean flag, EnumSet enumset) throws NotImplementedException, NotImplementedException
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

    private void parseQueryAndVerify(URI uri, CloudBlobClient cloudblobclient, boolean flag)
        throws NotImplementedException, URISyntaxException, StorageException
    {
    	throw new NotImplementedException();
    }

    public void setMetadata(HashMap hashmap) throws NotImplementedException, NotImplementedException
    {
    	throw new NotImplementedException();
    }

    protected void setName(String s) throws NotImplementedException, NotImplementedException
    {
    	throw new NotImplementedException();
    }

    protected void setProperties(BlobContainerProperties blobcontainerproperties) throws NotImplementedException, NotImplementedException
    {
    	throw new NotImplementedException();
    }

    protected void setUri(URI uri) throws NotImplementedException, NotImplementedException
    {
    	throw new NotImplementedException();
    }

    public void uploadMetadata()
        throws NotImplementedException, StorageException
    {
    	throw new NotImplementedException();
    }

    public void uploadPermissions(BlobContainerPermissions blobcontainerpermissions)
        throws NotImplementedException, StorageException
    {
    	throw new NotImplementedException();
    }

    protected HashMap m_Metadata;
    BlobContainerProperties m_Properties;
    String m_Name;
    URI m_Uri;
    private CloudBlobClient m_ServiceClient;
}
