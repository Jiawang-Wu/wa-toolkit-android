package com.windowsazure.samples.android.storageclient;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.*;
import java.security.InvalidKeyException;

import org.apache.http.client.methods.HttpGet;

import com.windowsazure.samples.android.storageclient.internal.web.HttpStatusCode;

public final class CloudBlobClient
{
    public CloudBlobClient(URI endpointUri) throws NotImplementedException, NotImplementedException
    {
    	throw new NotImplementedException();
    }

    public CloudBlobClient(URI endpointUri, StorageCredentials storageCredentials)
    {
        Utility.assertNotNull("baseUri", endpointUri);
        Utility.assertNotNull("storagecredentials", storageCredentials);
        m_SingleBlobPutThresholdInBytes = 0x2000000;
        m_WriteBlockSizeInBytes = 0x400000;
        m_PageBlobStreamWriteSizeInBytes = 0x400000;
        m_StreamMinimumReadSizeInBytes = 0x400000;
        m_ConcurrentRequestCount = 1;
        m_DirectoryDelimiter = "/";
        m_TimeoutInMs = 0x15f90;
        m_DirectoryDelimiter = "/";
        m_StreamMinimumReadSizeInBytes = 0x400000;

        if(!endpointUri.isAbsolute())
        {
            throw new IllegalArgumentException(String.format("Address '%s' is not an absolute address. Relative addresses are not permitted in here.", endpointUri));
        } 

        m_UsePathStyleUris = Utility.determinePathStyleFromUri(endpointUri, storageCredentials.getAccountName());
        m_Endpoint = endpointUri;
        m_Credentials = storageCredentials;
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

    public int getConcurrentRequestCount()
    {
        return m_ConcurrentRequestCount;
    }

    public CloudBlobContainer getContainerReference(String s)
        throws NotImplementedException, URISyntaxException, StorageException
    {
        Utility.assertNotNullOrEmpty("containerAddress", s);
        return new CloudBlobContainer(s, this);
    }

    public StorageCredentials getCredentials()
    {
        return m_Credentials;
    }

    public String getDirectoryDelimiter()
    {
    	return m_DirectoryDelimiter;
    }

    public CloudBlobDirectory getDirectoryReference(String s)
        throws NotImplementedException, URISyntaxException, StorageException
    {
    	throw new NotImplementedException();
    }

    public URI getEndpoint()
    {
        return m_Endpoint;
    }

    public URI getContainerEndpoint() throws URISyntaxException
    {
    	return PathUtility.appendPathToUri(this.getEndpoint(), this.getCredentials().containerEndpointPostfix());
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

    public int getPageBlobStreamWriteSizeInBytes()
    {
        return m_PageBlobStreamWriteSizeInBytes;
    }

    public int getSingleBlobPutThresholdInBytes()
    {
        return m_SingleBlobPutThresholdInBytes;
    }

    public int getStreamMinimumReadSizeInBytes()
    {
        return m_StreamMinimumReadSizeInBytes;
    }

    public int getTimeoutInMs()
    {
        return m_TimeoutInMs;
    }

    public int getWriteBlockSizeInBytes()
    {
        return m_WriteBlockSizeInBytes;
    }

    public Iterable<CloudBlobContainer> listContainers() throws Exception, NotImplementedException
    {
        return listContainersWithPrefix(null, ContainerListingDetails.NONE);
    }

    public Iterable<CloudBlobContainer> listContainers(String s) throws Exception, NotImplementedException
    {
        return listContainersWithPrefix(s, ContainerListingDetails.NONE);
    }

    public Iterable<CloudBlobContainer> listContainers(String s, ContainerListingDetails containerlistingdetails) throws Exception, NotImplementedException
    {
        return listContainersWithPrefix(s, containerlistingdetails);
    }

    protected Iterable<CloudBlobContainer> listContainersWithPrefix(String s, ContainerListingDetails containerlistingdetails) throws StorageInnerException, Exception
    {
        HttpGet request = containerRequest.list(getEndpoint(), s, containerlistingdetails);
        getCredentials().signRequest(request, -1L);
        RequestResult result = ExecutionEngine.processRequest(request);
        if (HttpStatusCode.fromInt(result.statusCode) != HttpStatusCode.OK)
        {
        	throw new StorageInnerException("Couldn't list blob's containers");
        }
        ListContainersResponse listcontainersresponse = new ListContainersResponse(result.httpResponse.getEntity().getContent());
        return listcontainersresponse.getContainers(this);
    }

    protected void setBaseURI(URI uri) throws NotImplementedException, NotImplementedException
    {
    	throw new NotImplementedException();
    }

    public void setConcurrentRequestCount(int i)
    {
        m_ConcurrentRequestCount = i;
    }

    protected void setCredentials(StorageCredentials storagecredentials)
    {
        m_Credentials = storagecredentials;
    }

    public void setDirectoryDelimiter(String s)
    {
        m_DirectoryDelimiter = s;
    }

    public void setPageBlobStreamWriteSizeInBytes(int i)
    {
        if(i > 0x400000 || i < 512 || i % 512 != 0)
        {
            throw new IllegalArgumentException("PageBlobStreamWriteSizeInBytes");
        } else
        {
            m_PageBlobStreamWriteSizeInBytes = i;
        }
    }

    public void setSingleBlobPutThresholdInBytes(int i)
            throws IllegalArgumentException
        {
            if(i > 0x4000000 || i < 0x100000)
            {
                throw new IllegalArgumentException("SingleBlobUploadThresholdInBytes");
            } else
            {
                m_SingleBlobPutThresholdInBytes = i;
                return;
            }
        }

        public void setStreamMinimumReadSizeInBytes(int i)
        {
            if(i > 0x4000000 || i < 512)
            {
                throw new IllegalArgumentException("MinimumReadSize");
            } else
            {
                m_StreamMinimumReadSizeInBytes = i;
                return;
            }
        }

        public void setTimeoutInMs(int i)
        {
            m_TimeoutInMs = i;
        }

        public void setWriteBlockSizeInBytes(int i)
                throws IllegalArgumentException
            {
                if(i > 0x400000 || i < 0x100000)
                {
                    throw new IllegalArgumentException("WriteBlockSizeInBytes");
                } else
                {
                    m_WriteBlockSizeInBytes = i;
                    return;
                }
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
    private AbstractContainerRequest containerRequest = new ContainerWASServiceRequest();
	CloudBlobClient clientForBlobOf(CloudBlobContainer cloudBlobContainer) throws UnsupportedEncodingException, StorageException, NotImplementedException, IOException, URISyntaxException
	{
		StorageCredentials credentials = this.getCredentials().credentialsForBlobOf(cloudBlobContainer);
		if (credentials == this.getCredentials())
		{
			return this;
		}
		else
		{
			URI containerUri = cloudBlobContainer.getUri();
			URI uri = new URI(containerUri.getScheme() + "://" + containerUri.getAuthority());
			return new CloudBlobClient(uri, credentials);
		}
	}
}
