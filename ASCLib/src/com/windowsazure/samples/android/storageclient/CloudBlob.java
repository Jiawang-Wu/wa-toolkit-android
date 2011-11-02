package com.windowsazure.samples.android.storageclient;

import java.io.*;
import java.net.*;
import java.security.*;
import java.util.*;

import org.apache.http.HttpEntity;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.entity.InputStreamEntity;
import org.apache.http.message.AbstractHttpMessage;

import com.windowsazure.samples.android.storageclient.internal.web.HttpStatusCode;

public abstract class CloudBlob
    implements IListBlobItem
{

    protected CloudBlob() throws NotImplementedException, NotImplementedException
    {
        m_Metadata = new HashMap();
        m_Properties = new BlobProperties();
    }

    public CloudBlob(CloudBlob cloudblob) throws NotImplementedException, NotImplementedException
    {
    	throw new NotImplementedException();
    }

    public CloudBlob(URI blobAbsoluteUri, CloudBlobClient serviceClient)
        throws NotImplementedException, StorageException
    {
        this();
        Utility.assertNotNull("blobAbsoluteUri", blobAbsoluteUri);
        Utility.assertNotNull("serviceClient", serviceClient);
        m_ServiceClient = serviceClient;
        m_Uri = blobAbsoluteUri;
        parseURIQueryStringAndVerify(blobAbsoluteUri, serviceClient);
    }

    public CloudBlob(URI uri, CloudBlobClient cloudblobclient, CloudBlobContainer cloudblobcontainer)
        throws NotImplementedException, StorageException
    {
        this(uri, cloudblobclient);
        m_Container = cloudblobcontainer;
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
        throws NotImplementedException, StorageException, UnsupportedEncodingException, IOException
    {
        StorageOperation storageoperation = new StorageOperation() {
            public Void execute(CloudBlobClient cloudblobclient, CloudBlob cloudblob)
                throws Exception
            {
                HttpDelete request = BlobRequest.delete(cloudblob.getTransformedAddress());
                cloudblobclient.getCredentials().signRequest(request, -1L);
                result = ExecutionEngine.processRequest(request);
                if(result.statusCode != HttpStatusCode.Accepted.getStatus())
                {
                    throw new StorageInnerException("Couldn't delete a blob");
                }
                return null;
            }

            public Object execute(Object obj, Object obj1)
                throws Exception
            {
                return execute((CloudBlobClient)obj, (CloudBlob)obj1);
            }
        };
        ExecutionEngine.execute(m_ServiceClient, this, storageoperation);
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

    public void download(final OutputStream outputstream)
        throws NotImplementedException, StorageException, IOException
    {
        StorageOperation storageoperation = new StorageOperation() {
            public Void execute(CloudBlobClient cloudblobclient, CloudBlob cloudblob)
                throws Exception
            {
                HttpGet request = BlobRequest.get(cloudblob.getTransformedAddress());
                cloudblobclient.getCredentials().signRequest(request, -1L);
                RequestResult result = ExecutionEngine.processRequest(request);
                BlobAttributes attributes = BlobResponse.getAttributes((AbstractHttpMessage) result.httpResponse, cloudblob.getUri(), null);
                m_Properties = attributes.properties;
                result.httpResponse.getEntity().writeTo(outputstream);
                /*
                result = operationcontext1.getLastResult();
                String s = Utility.getStandardHeaderValue(result.httpResponse, "Content-MD5");
                String s1 = httpurlconnection.getHeaderField("Content-Length");
                long l = Long.parseLong(s1);
                ExecutionEngine.getResponseCode(result, httpurlconnection, operationcontext1);
                */
                if(result.statusCode != 200)
                {
                    throw new StorageInnerException("Couldn't download a blob");
                }
                return null;
            }

            public Object execute(Object obj, Object obj1)
                throws Exception
            {
                return execute((CloudBlobClient)obj, (CloudBlob)obj1);
            }

        };
        ExecutionEngine.execute(m_ServiceClient, this, storageoperation);
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
        if(m_Container == null)
        {
            String name = PathUtility.getContainerNameFromUri(getUri());
            m_Container = new CloudBlobContainer(name, this.getServiceClient());
        }
        return m_Container;
    }

    public HashMap getMetadata() throws NotImplementedException, NotImplementedException
    {
    	throw new NotImplementedException();
    }

    public String getName()
        throws NotImplementedException, URISyntaxException
    {
        if(Utility.isNullOrEmpty(m_Name))
            m_Name = PathUtility.getBlobNameFromURI(getUri());
        return m_Name;
    }

    public CloudBlobDirectory getParent()
        throws NotImplementedException, URISyntaxException, StorageException
    {
    	throw new NotImplementedException();
    }

    public BlobProperties getProperties() throws NotImplementedException, NotImplementedException
    {
        return m_Properties;
    }

    public URI getQualifiedUri()
        throws NotImplementedException, URISyntaxException, StorageException
    {
    	throw new NotImplementedException();
    }

    public CloudBlobClient getServiceClient() throws NotImplementedException, NotImplementedException
    {
    	if (m_Container != null)
    	{
    		return m_Container.getServiceClient();
    	}
    	else
    	{
    		return m_ServiceClient;
    	}
    }

    public StorageCredentials getCredentials()
    {
    	return m_ServiceClient.getCredentials();
    }

    public String getSnapshotID() throws NotImplementedException, NotImplementedException
    {
    	throw new NotImplementedException();
    }

    protected URI getTransformedAddress()
        throws NotImplementedException, IllegalArgumentException, URISyntaxException, StorageException
    {
        if(m_ServiceClient.getCredentials().doCredentialsNeedTransformUri().booleanValue())
        {
            if(getUri().isAbsolute())
            {
                return m_ServiceClient.getCredentials().transformUri(getUri());
            } else
            {
                StorageException storageexception = Utility.generateNewUnexpectedStorageException(null);
                storageexception.extendedErrorInformation.errorMessage = "Blob Object relative URIs not supported.";
                throw storageexception;
            }
        } 
        else
        {
            return getUri();
        }
    }

    public URI getUri() throws NotImplementedException
    {
    	return m_Uri;
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

    private void parseURIQueryStringAndVerify(URI resourceUri, CloudBlobClient serviceClient)
        throws NotImplementedException, StorageException
    {
        Utility.assertNotNull("resourceUri", resourceUri);
        if(!resourceUri.isAbsolute())
        {
            String s = String.format("Address '%s' is not an absolute address. Relative addresses are not permitted in here.", resourceUri);
            throw new IllegalArgumentException(s);
        }
        
        m_Uri = PathUtility.stripURIQueryAndFragment(resourceUri);
        HashMap<String, String[]> hashmap = PathUtility.parseQueryString(resourceUri.getQuery());
        
        StorageCredentialsSharedAccessSignature credentialsSAS = SharedAccessSignatureHelper.parseQuery(hashmap);
        String as[] = (String[])hashmap.get("snapshot");
        if(as != null && as.length > 0)
        {
            m_SnapshotID = as[0];
        }
        if(credentialsSAS == null)
        {
            return;
        }
        boolean useServiceClientCredentials = serviceClient != null ? Utility.areCredentialsEqual(credentialsSAS, serviceClient.getCredentials()) : false;
        if(serviceClient == null || !useServiceClientCredentials)
            try
            {
                m_ServiceClient = new CloudBlobClient(new URI(PathUtility.getServiceClientBaseAddress(getUri())), credentialsSAS);
            }
            catch(URISyntaxException urisyntaxexception)
            {
                throw Utility.generateNewUnexpectedStorageException(urisyntaxexception);
            }
        if(serviceClient != null && !useServiceClientCredentials)
        {
            m_ServiceClient.setPageBlobStreamWriteSizeInBytes(serviceClient.getPageBlobStreamWriteSizeInBytes());
            m_ServiceClient.setSingleBlobPutThresholdInBytes(serviceClient.getSingleBlobPutThresholdInBytes());
            m_ServiceClient.setStreamMinimumReadSizeInBytes(serviceClient.getStreamMinimumReadSizeInBytes());
            m_ServiceClient.setWriteBlockSizeInBytes(serviceClient.getWriteBlockSizeInBytes());
            m_ServiceClient.setConcurrentRequestCount(serviceClient.getConcurrentRequestCount());
            m_ServiceClient.setDirectoryDelimiter(serviceClient.getDirectoryDelimiter());
            m_ServiceClient.setTimeoutInMs(serviceClient.getTimeoutInMs());
        }
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
        if(length < 0L)
        {
            throw new IllegalArgumentException("Invalid stream length, specify a positive number of bytes");
        } 
        else
        {
        	StorageOperation storageoperation = new StorageOperation() {
                public Void execute(CloudBlobClient cloudblobclient, CloudBlob cloudblob)
                    throws Exception
                {
                    HttpPut request = BlobRequest.put(cloudblob.getTransformedAddress(), cloudblobclient.getTimeoutInMs(), cloudblob.m_Properties, cloudblob.m_Properties.blobType, leaseID, 0L);
                    BlobRequest.addMetadata(request, cloudblob.m_Metadata);
                    cloudblobclient.getCredentials().signRequest(request, length);
                    InputStreamEntity entity = new InputStreamEntity(inputstream, length);
                    request.setEntity(entity);
                    result = ExecutionEngine.processRequest(request);
                    if (result.statusCode != HttpStatusCode.Created.getStatus())
                    {
                        throw new StorageInnerException("Couldn't upload a blob's data");
                    } 
                    return null;
                }

				@Override
				public Object execute(Object firstArgument,
						Object secondArgument) throws Exception {
                    return execute((CloudBlobClient)firstArgument, (CloudBlob)secondArgument);
				}

            };

            ExecutionEngine.execute(m_ServiceClient, this, storageoperation);
            return;
        }
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
    AbstractBlobRequest blobRequest = new BlobWASServiceRequest();
}
