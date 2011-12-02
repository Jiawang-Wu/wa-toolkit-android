package com.windowsazure.samples.android.storageclient;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URISyntaxException;

import org.apache.http.HttpStatus;
import org.apache.http.client.methods.HttpGet;

public final class CloudBlobClient {
	
	private URI m_Endpoint;

	private StorageCredentials m_Credentials;

	private int m_SingleBlobPutThresholdInBytes;

	private int m_WriteBlockSizeInBytes;

	private int m_PageBlobStreamWriteSizeInBytes;

	private int m_StreamMinimumReadSizeInBytes;

	private String m_DirectoryDelimiter;

	private int m_TimeoutInMs;

	private AbstractContainerRequest containerRequest;

	/**
	* Initializes a new instance of the CloudBlobClient class using the specified Blob service endpoint
	* 
	* @param baseUri
	*            The Blob service endpoint to use to create the client
	*/
	public CloudBlobClient(URI baseUri) throws NotImplementedException,
			NotImplementedException {
		throw new NotImplementedException();
	}

	/**
	* Initializes a new instance of the CloudBlobClient class using the specified Blob service endpoint and
	* account credentials.
	* 
	* @param baseUri
	*            The Blob service endpoint to use to create the client
	* @param storageCredentials
	*            The account credentials.
	*/
	public CloudBlobClient(URI baseUri,
			StorageCredentials storageCredentials) {
		Utility.assertNotNull("baseUri", baseUri);
		Utility.assertNotNull("storagecredentials", storageCredentials);
		m_SingleBlobPutThresholdInBytes = 0x2000000;
		m_WriteBlockSizeInBytes = 0x400000;
		m_PageBlobStreamWriteSizeInBytes = 0x400000;
		m_StreamMinimumReadSizeInBytes = 0x400000;
		m_DirectoryDelimiter = "/";
		m_TimeoutInMs = 0x15f90;
		m_DirectoryDelimiter = "/";
		m_StreamMinimumReadSizeInBytes = 0x400000;

		if (!baseUri.isAbsolute()) {
			throw new IllegalArgumentException(
					String.format(
							"Address '%s' is not an absolute address. Relative addresses are not permitted in here.",
							baseUri));
		}

		m_Endpoint = baseUri;
		m_Credentials = storageCredentials;
		containerRequest = storageCredentials.getContainerRequest();
	}

	CloudBlobClient clientForBlobOf(CloudBlobContainer container)
			throws UnsupportedEncodingException, StorageException,
			IOException, URISyntaxException {
		StorageCredentials credentials = this.getCredentials()
				.credentialsForBlobOf(container);
		if (credentials == this.getCredentials()) {
			return this;
		} else {
			URI containerUri = container.getUri();
			URI baseUri = new URI(containerUri.getScheme() + "://"
					+ containerUri.getAuthority());
			return new CloudBlobClient(baseUri, credentials);
		}
	}

	/**
	* Gets a reference to a block blob in this container.
	* @param blobName the name of the blob
	* @return a reference to a block blob in this container.
	* @throws URISyntaxException
	* @throws StorageException
	*/
	public CloudBlockBlob getBlockBlobReference(String blobName)
			throws NotImplementedException, URISyntaxException,
			StorageException {
		throw new NotImplementedException();
	}

	/**
	* Gets a reference to a block blob in this container.
	* 
	* @param blobName
	*            the name of the blob
	* @param snapshotId
	*            the snapshot ID of the blob
	* @return a reference to a block blob in this container.
	* @throws URISyntaxException
	* @throws StorageException
	*/
	public CloudBlockBlob getBlockBlobReference(String blobName, String snapshotId)
			throws NotImplementedException, StorageException,
			URISyntaxException {
		throw new NotImplementedException();
	}

	public URI getContainerEndpoint() throws URISyntaxException {
		return PathUtility.appendPathToUri(this.getBaseURI(), this
				.getCredentials().containerEndpointPostfix());
	}

	/**
	* Returns a reference to a CloudBlobContainer object with the specified address.
	* 
	* @param containerName
	*            the name of the container
	* @throws URISyntaxException
	* @throws StorageException
	*/
	public CloudBlobContainer getContainerReference(String containerName)
			throws NotImplementedException, URISyntaxException,
			StorageException {
		Utility.assertNotNullOrEmpty("containerAddress", containerName);
		return new CloudBlobContainer(containerName, this);
	}

	/**
	* @return the Storage Credentials Associated with this Client
	*/
	public StorageCredentials getCredentials() {
		return m_Credentials;
	}

	public String getDirectoryDelimiter() {
		return m_DirectoryDelimiter;
	}

	/**
	* @return Gets the base URI for the Blob service client.
	*/

	public URI getBaseURI() {
		return m_Endpoint;
	}

	/**
	* Gets a reference to a page blob in this container.
	* @param blobName the name of the blob
	* @return a reference to a page blob in this container.
	* @throws URISyntaxException
	* @throws StorageException
	*/
	public CloudPageBlob getPageBlobReference(String blobName)
			throws NotImplementedException, URISyntaxException,
			StorageException {
		throw new NotImplementedException();
	}

	/**
	* Gets a reference to a page blob in this container.
	* 
	* @param blobName
	*            the name of the blob
	* @param snapshotId
	*            the snapshot ID of the blob
	* @return a reference to a page blob in this container.
	* @throws URISyntaxException
	* @throws StorageException
	*/
	public CloudPageBlob getPageBlobReference(String blobName, String snapshotId)
			throws NotImplementedException, URISyntaxException,
			StorageException {
		throw new NotImplementedException();
	}

	public int getPageBlobStreamWriteSizeInBytes() {
		return m_PageBlobStreamWriteSizeInBytes;
	}

	/**
	* @return the maximum size of a blob in bytes that may be uploaded as a single blob.
	*/
	public int getSingleBlobPutThresholdInBytes() {
		return m_SingleBlobPutThresholdInBytes;
	}

	/**
	* 
	* @return the minimum read size when using BlobReadStreams
	*/
	public int getStreamMinimumReadSizeInBytes() {
		return m_StreamMinimumReadSizeInBytes;
	}

	public int getTimeoutInMs() {
		return m_TimeoutInMs;
	}

	/**
	* @return the maximum block size for writing to a block blob.
	*/
	public int getWriteBlockSizeInBytes() {
		return m_WriteBlockSizeInBytes;
	}

	/**
	* Returns an Iterable collection of containers.
	* 
	* @return an Iterable collection of containers.
	*/
	public Iterable<CloudBlobContainer> listContainers() throws Exception,
			NotImplementedException {
		return listContainersWithPrefix(null, ContainerListingDetails.NONE);
	}

	/**
	* Returns an Iterable Collection of containers whose names begin with the specified prefix.
	* 
	* @param prefix
	*            The container name prefix.
	* @return an Iterable Collection of containers whose names begin with the specified prefix.
	*/
	public Iterable<CloudBlobContainer> listContainers(String prefix)
			throws Exception, NotImplementedException {
		return listContainersWithPrefix(prefix, ContainerListingDetails.NONE);
	}

	/**
	* Returns an Iterable Collection of containers whose names begin with the specified prefix.
	* 
	* @param prefix
	*            The container name prefix.
	* @param detailsIncluded
	*            a value that indicates whether to return container metadata with the listing.
	* @return an Iterable Collection of containers whose names begin with the specified prefix.
	*/
	public Iterable<CloudBlobContainer> listContainers(String prefix,
			ContainerListingDetails detailsIncluded) throws Exception,
			NotImplementedException {
		return listContainersWithPrefix(prefix, detailsIncluded);
	}
	
	protected Iterable<CloudBlobContainer> listContainersWithPrefix(final String prefix,
			final ContainerListingDetails listingDetails)
			throws StorageInnerException, Exception {
		final CloudBlobClient client = this;
		StorageOperation<Iterable<CloudBlobContainer>> storageOperation = new StorageOperation<Iterable<CloudBlobContainer>>() {
			public Iterable<CloudBlobContainer> execute() throws Exception {
		HttpGet request = containerRequest.list(getBaseURI(), prefix,
				listingDetails);
		getCredentials().signRequest(request, -1L);
		this.processRequest(request);
		if (result.statusCode != HttpStatus.SC_OK) {
			throw new StorageInnerException("Couldn't list blob's containers");
		}
		ListContainersResponse response = new ListContainersResponse(
				result.httpResponse.getEntity().getContent());
		return response.getContainers(client);
			}
		};
		return storageOperation.executeTranslatingExceptions();
	}
	protected void setBaseURI(URI baseUri) throws NotImplementedException,
			NotImplementedException {
		throw new NotImplementedException();
	}
	protected void setCredentials(StorageCredentials storageCredentials) {
		m_Credentials = storageCredentials;
	}
	public void setDirectoryDelimiter(String directoryDelimiter) {
		m_DirectoryDelimiter = directoryDelimiter;
	}
	public void setPageBlobStreamWriteSizeInBytes(int writeSizeInBytes) {
		if (writeSizeInBytes > 0x400000 || writeSizeInBytes < 512 || writeSizeInBytes % 512 != 0) {
			throw new IllegalArgumentException("PageBlobStreamWriteSizeInBytes");
		} else {
			m_PageBlobStreamWriteSizeInBytes = writeSizeInBytes;
		}
	}
	public void setSingleBlobPutThresholdInBytes(int putThresholdInBytes)
			throws IllegalArgumentException {
		if (putThresholdInBytes > 0x4000000 || putThresholdInBytes < 0x100000) {
			throw new IllegalArgumentException(
					"SingleBlobUploadThresholdInBytes");
		} else {
			m_SingleBlobPutThresholdInBytes = putThresholdInBytes;
			return;
		}
	}

	/**
	* @param minimumReadSize
	*            the minimum read size when using BlobReadStreams
	*/
	public void setStreamMinimumReadSizeInBytes(int minimumReadSize) {
		if (minimumReadSize > 0x4000000 || minimumReadSize < 512) {
			throw new IllegalArgumentException("MinimumReadSize");
		} else {
			m_StreamMinimumReadSizeInBytes = minimumReadSize;
			return;
		}
	}
	
	public void setTimeoutInMs(int timeoutInMs) {
		m_TimeoutInMs = timeoutInMs;
	}

	public void setWriteBlockSizeInBytes(int blockSizeInBytes) throws IllegalArgumentException {
		if (blockSizeInBytes > 0x400000 || blockSizeInBytes < 0x100000) {
			throw new IllegalArgumentException("WriteBlockSizeInBytes");
		} else {
			m_WriteBlockSizeInBytes = blockSizeInBytes;
			return;
		}
	}
	
}
