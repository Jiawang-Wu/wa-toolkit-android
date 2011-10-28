package com.windowsazure.samples.android.storageclient;

import java.io.IOException;
import java.io.OutputStream;
import java.io.StringWriter;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URISyntaxException;
import java.security.InvalidKeyException;
import java.util.EnumSet;
import java.util.HashMap;

import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPut;

import com.windowsazure.samples.android.storageclient.internal.web.HttpStatusCode;
import com.windowsazure.samples.android.storageclient.internal.xml.DOMAdapter;

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
        Utility.assertNotNullOrEmpty("containerName", containerName);
        URI uri = PathUtility.appendPathToUri(cloudBlobClient.getContainerEndpoint(), containerName);
        m_ContainerOperationsUri = uri;
        m_Name = containerName;
        parseQueryAndVerify(m_ContainerOperationsUri, cloudBlobClient);
    }

    private void parseQueryAndVerify(URI completeUri, CloudBlobClient cloudBlobClient)
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
                m_ServiceClient = new CloudBlobClient(new URI(PathUtility.getServiceClientBaseAddress(m_ContainerOperationsUri)), storagecredentialssharedaccesssignature);
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

    public void create() throws StorageException, NotImplementedException, UnsupportedEncodingException, IOException
    {
        StorageOperation storageoperation = new StorageOperation() {
            public Void execute(CloudBlobClient cloudBlobClient, CloudBlobContainer cloudBlobContainer) throws Exception
            {
                HttpPut request = containerRequest.create(cloudBlobContainer.m_ContainerOperationsUri, cloudBlobClient.getTimeoutInMs());
                containerRequest.addMetadata(request, cloudBlobContainer.m_Metadata);
                cloudBlobClient.getCredentials().signRequest(request, 0L);
                result = ExecutionEngine.processRequest(request);

                if (result.statusCode != HttpStatusCode.OK.getStatus())
                {
                    throw new StorageInnerException("Couldn't create a blob container");
                } 
                else
                {
                	if (containerRequest.isUsingWasServiceDirectly())
                	{
                		BlobContainerAttributes blobcontainerattributes = ContainerResponse.getAttributes(request.getURI(), result);
	                    cloudBlobContainer.setMetadata(blobcontainerattributes.metadata);
	                    cloudBlobContainer.m_Properties = blobcontainerattributes.properties;
	                    cloudBlobContainer.m_Name = blobcontainerattributes.name;
                	}
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
        throws NotImplementedException, StorageException, UnsupportedEncodingException, IOException, StorageInnerException
    {
        StorageOperation storageoperation = new StorageOperation() {
            public Void execute(CloudBlobClient cloudblobclient, CloudBlobContainer cloudblobcontainer)
                throws Exception
            {
                HttpDelete request = containerRequest.delete(cloudblobcontainer.m_ContainerOperationsUri, cloudblobclient.getTimeoutInMs());
                cloudblobclient.getCredentials().signRequest(request, -1L);
                result = ExecutionEngine.processRequest(request);
                if (HttpStatusCode.fromInt(result.statusCode) != HttpStatusCode.OK)
                {
                	throw new StorageInnerException("Couldn't delete a blob container");
                }
                return null;
            }

            public Object execute(Object obj, Object obj1)
                throws Exception
            {
                return execute((CloudBlobClient)obj, (CloudBlobContainer)obj1);
            }
        };
        
        ExecutionEngine.execute(m_ServiceClient, this, storageoperation);
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
        throws NotImplementedException, URISyntaxException, StorageException, UnsupportedEncodingException, IOException
    {
        Utility.assertNotNullOrEmpty("blobAddressUri", s);
        URI uri = PathUtility.appendPathToUri(this.getUri(), s);
        return new CloudBlockBlob(uri, m_ServiceClient.clientForBlobOf(this), this);
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
    	return m_Metadata;
    }

    public String getName() throws NotImplementedException, NotImplementedException
    {
        return m_Name;
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
    	return m_ServiceClient;
    }

    private String getSharedAccessCanonicalName() throws NotImplementedException, NotImplementedException
    {
    	throw new NotImplementedException();
    }

    protected URI getTransformedAddress()
        throws NotImplementedException, IllegalArgumentException, URISyntaxException, StorageException, UnsupportedEncodingException, IOException
    {
    	if (!containerRequest.isUsingWasServiceDirectly())
    	{
    		return this.getUriWithSas();
    	}
    	
    	URI uri = this.getUri();
        if(m_ServiceClient.getCredentials().doCredentialsNeedTransformUri().booleanValue())
        {
            if(uri.isAbsolute())
            {
                return m_ServiceClient.getCredentials().transformUri(uri);
            } 
            else
            {
                StorageException storageexception = Utility.generateNewUnexpectedStorageException(null);
                storageexception.extendedErrorInformation.errorMessage = "Blob Object relative URIs not supported.";
                throw storageexception;
            }
        } 
        else
        {
            return uri;
        }
    }

    private URI getUriWithSas() throws StorageException, NotImplementedException, UnsupportedEncodingException, IOException, IllegalArgumentException, URISyntaxException
    {
    	if (containerRequest.isUsingWasServiceDirectly())
    	{
    		return this.getTransformedAddress();
    	}

    	class GetSASResponseDOMAdapter extends DOMAdapter<URI> {
    		
    		public GetSASResponseDOMAdapter(String xmlString) {
    			super(xmlString);
    		}

    		@Override
    		public URI build() throws URISyntaxException
    		{
    			return new URI(getRootNode().getInnerText());
    		}
    	}
   	
        StorageOperation storageoperation = new StorageOperation()
        {
            public URI execute(CloudBlobClient cloudblobclient, CloudBlobContainer cloudblobcontainer)
                throws Exception
            {
                HttpGet request = containerRequest.getUri(cloudblobcontainer.m_ContainerOperationsUri,
                		cloudblobclient.getTimeoutInMs());
                cloudblobclient.getCredentials().signRequest(request, -1L);
                result = ExecutionEngine.processRequest(request);
                if(result.statusCode == HttpStatusCode.OK.getStatus())
                {
                    return new GetSASResponseDOMAdapter(Utility.getHttpResponseBody(result.httpResponse)).build();
                }
                else
                {
                    throw new StorageInnerException("Couldn't get a blob container uri");
                }
            }

			@Override
			public Object execute(Object firstArgument, Object secondArgument)
					throws Exception {
                return execute((CloudBlobClient)firstArgument, (CloudBlobContainer)secondArgument);
			}
        };
        
        return (URI)ExecutionEngine.execute(m_ServiceClient, this, storageoperation);
    }
    
    public URI getUri() throws StorageException, NotImplementedException, UnsupportedEncodingException, IOException, IllegalArgumentException, URISyntaxException
    {
    	if (containerRequest.isUsingWasServiceDirectly())
    	{
    		return this.m_ContainerOperationsUri;
    	}
    	else
    	{
    		return PathUtility.stripURIQueryAndFragment(this.getTransformedAddress());
    	}
    }

    public Iterable<CloudBlob> listBlobs() throws StorageInnerException, Exception
    {
    	return this.listBlobs("");
    }

    public Iterable<CloudBlob> listBlobs(String s) throws StorageInnerException, Exception
    {
    	return this.listBlobs(s, false);
    }

    public Iterable<CloudBlob> listBlobs(String s, boolean useFlatBlobListing) throws StorageInnerException, Exception
    {
        HttpGet request = blobRequest.list(this.getServiceClient().getEndpoint(), this, s, useFlatBlobListing);
        this.getServiceClient().getCredentials().signRequest(request, -1L);
        RequestResult result = ExecutionEngine.processRequest(request);
        if (HttpStatusCode.fromInt(result.statusCode) != HttpStatusCode.OK)
        {
        	throw new StorageInnerException("Couldn't list container blobs");
        }
        ListBlobsResponse listcontainersresponse = new ListBlobsResponse(result.httpResponse.getEntity().getContent());
        return listcontainersresponse.getBlobs(this.getServiceClient(), this);
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

    public void uploadPermissions(final BlobContainerPermissions permissions)
        throws NotImplementedException, StorageException, UnsupportedEncodingException, IOException
    {
        StorageOperation storageoperation = new StorageOperation() {
            public Void execute(CloudBlobClient cloudBlobClient, CloudBlobContainer cloudBlobContainer) throws Exception
            {
                HttpPut request = containerRequest.setAcl(cloudBlobContainer.m_ContainerOperationsUri, permissions.publicAccess);
                cloudBlobClient.getCredentials().signRequest(request, 0);
                result = ExecutionEngine.processRequest(request);
                if (result.statusCode != HttpStatusCode.OK.getStatus())
                {
                    throw new StorageInnerException("Couldn't upload permissions to a blob's container");
                } 
                return null;
            }

            public Object execute(Object cloudBlobClient, Object cloudBlobContainer) throws Exception
            {
                return execute((CloudBlobClient)cloudBlobClient, (CloudBlobContainer)cloudBlobContainer);
            }
        };

        ExecutionEngine.execute(m_ServiceClient, this, storageoperation);
    }

    protected HashMap m_Metadata;
    BlobContainerProperties m_Properties;
    String m_Name;
    URI m_ContainerOperationsUri;
    private CloudBlobClient m_ServiceClient;
    AbstractContainerRequest containerRequest;
    AbstractBlobRequest blobRequest = new BlobWASServiceRequest();
}
