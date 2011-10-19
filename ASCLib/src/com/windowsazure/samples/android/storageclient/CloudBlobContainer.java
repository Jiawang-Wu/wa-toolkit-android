package com.windowsazure.samples.android.storageclient;

import java.io.*;
import java.net.*;
import java.security.InvalidKeyException;
import java.text.ParseException;
import java.util.*;

import com.windowsazure.samples.android.storageclient.internal.web.HttpStatusCode;

public final class CloudBlobContainer
{

    static BlobContainerPermissions getContainerAcl(String s)
        throws NotImplementedException, IllegalArgumentException
    {
    	throw new NotImplementedException();
    }

    private CloudBlobContainer(CloudBlobClient cloudBlobClient)
    {
        m_Metadata = new HashMap();
        m_Properties = new BlobContainerProperties();
        m_ServiceClient = cloudBlobClient;
        containerRequest = new ContainerWASServiceRequest();
    }

    public CloudBlobContainer(String containerName, CloudBlobClient cloudBlobClient)
        throws URISyntaxException, StorageException
    {
        this(cloudBlobClient);
        URI uri = PathUtility.appendPathToUri(cloudBlobClient.getContainerEndpoint(), containerName);
        m_ContainerOperationsUri = uri;
        m_Name = PathUtility.getContainerNameFromUri(uri, cloudBlobClient.m_UsePathStyleUris);
        parseQueryAndVerify(m_ContainerOperationsUri, cloudBlobClient, cloudBlobClient.m_UsePathStyleUris);
    }

    private void parseQueryAndVerify(URI completeUri, CloudBlobClient cloudBlobClient, boolean usePathStyleUris)
            throws URISyntaxException, StorageException
        {
            Utility.assertNotNull("completeUri", completeUri);
            if(!completeUri.isAbsolute())
            {
                String s = String.format("Address '%s' is not an absolute address. Relative addresses are not permitted in here.", new Object[] {
                    completeUri.toString()
                });
                throw new IllegalArgumentException(s);
            }
            m_ContainerOperationsUri = PathUtility.stripURIQueryAndFragment(completeUri);
            HashMap hashmap = PathUtility.parseQueryString(completeUri.getQuery());
            StorageCredentialsSharedAccessSignature storagecredentialssharedaccesssignature = SharedAccessSignatureHelper.parseQuery(hashmap);
            if(storagecredentialssharedaccesssignature == null)
                return;
            Boolean boolean1 = Boolean.valueOf(cloudBlobClient != null ? Utility.areCredentialsEqual(storagecredentialssharedaccesssignature, cloudBlobClient.getCredentials()) : false);
            if(cloudBlobClient == null || !boolean1.booleanValue())
                m_ServiceClient = new CloudBlobClient(new URI(PathUtility.getServiceClientBaseAddress(m_ContainerOperationsUri, usePathStyleUris)), storagecredentialssharedaccesssignature);
            if(cloudBlobClient != null && !boolean1.booleanValue())
            {
                m_ServiceClient.setPageBlobStreamWriteSizeInBytes(cloudBlobClient.getPageBlobStreamWriteSizeInBytes());
                m_ServiceClient.setSingleBlobPutThresholdInBytes(cloudBlobClient.getSingleBlobPutThresholdInBytes());
                m_ServiceClient.setStreamMinimumReadSizeInBytes(cloudBlobClient.getStreamMinimumReadSizeInBytes());
                m_ServiceClient.setWriteBlockSizeInBytes(cloudBlobClient.getWriteBlockSizeInBytes());
                m_ServiceClient.setConcurrentRequestCount(cloudBlobClient.getConcurrentRequestCount());
                m_ServiceClient.setDirectoryDelimiter(cloudBlobClient.getDirectoryDelimiter());
                m_ServiceClient.setTimeoutInMs(cloudBlobClient.getTimeoutInMs());
            }
        }

    public CloudBlobContainer(URI uri, CloudBlobClient cloudBlobClient)
        throws URISyntaxException, StorageException , NotImplementedException
    {
        this(cloudBlobClient);
        m_ContainerOperationsUri = uri;
        m_Name = PathUtility.getContainerNameFromUri(uri, cloudBlobClient.m_UsePathStyleUris);
        parseQueryAndVerify(m_ContainerOperationsUri, cloudBlobClient, cloudBlobClient.m_UsePathStyleUris);
    }

    public void create() throws StorageException, NotImplementedException
    {
        StorageOperation storageoperation = new StorageOperation() {
            public Void execute(CloudBlobClient cloudBlobClient, CloudBlobContainer cloudBlobContainer) throws Exception
            {
                httpurlconnection = containerRequest.create(cloudBlobContainer.m_ContainerOperationsUri, cloudBlobClient.getTimeoutInMs());
                containerRequest.addMetadata(httpurlconnection, cloudBlobContainer.m_Metadata);
                cloudBlobClient.getCredentials().signRequest(httpurlconnection, 0L);
                result = ExecutionEngine.processRequest(httpurlconnection);

                if (result.statusCode != HttpStatusCode.Created.ordinal())
                {
                    return null;
                } 
                else
                {
                    BlobContainerAttributes blobcontainerattributes = ContainerResponse.getAttributes(httpurlconnection,
                    		cloudBlobClient.m_UsePathStyleUris);
                    cloudBlobContainer.setMetadata(blobcontainerattributes.metadata);
                    cloudBlobContainer.m_Properties = blobcontainerattributes.properties;
                    cloudBlobContainer.m_Name = blobcontainerattributes.name;
                    return null;
                }
            }

            public Object execute(Object cloudBlobClient, Object cloudBlobContainer) throws Exception
            {
                return execute((CloudBlobClient)cloudBlobClient, (CloudBlobContainer)cloudBlobContainer);
            }
        };

        ExecutionEngine.execute(m_ServiceClient, this, storageoperation);
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

    public URI getUri() throws NotImplementedException
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
    URI m_ContainerOperationsUri;
    private CloudBlobClient m_ServiceClient;
    AbstractContainerRequest containerRequest;
}
