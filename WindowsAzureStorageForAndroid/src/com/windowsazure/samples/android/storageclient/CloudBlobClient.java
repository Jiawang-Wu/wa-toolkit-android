package com.windowsazure.samples.android.storageclient;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URISyntaxException;

import org.apache.http.client.methods.HttpGet;

import com.windowsazure.samples.android.storageclient.internal.web.HttpStatusCode;

public final class CloudBlobClient {
	private URI m_Endpoint;

	private StorageCredentials m_Credentials;

	private int m_SingleBlobPutThresholdInBytes;

	private int m_WriteBlockSizeInBytes;

	private int m_PageBlobStreamWriteSizeInBytes;

	private int m_StreamMinimumReadSizeInBytes;

	private int m_ConcurrentRequestCount;

	private String m_DirectoryDelimiter;

	private int m_TimeoutInMs;

	private AbstractContainerRequest containerRequest;

	public CloudBlobClient(URI baseUri) throws NotImplementedException,
			NotImplementedException {
		throw new NotImplementedException();
	}

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
			NotImplementedException, IOException, URISyntaxException {
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

	public CloudBlockBlob getBlockBlobReference(String blobName)
			throws NotImplementedException, URISyntaxException,
			StorageException {
		throw new NotImplementedException();
	}

	public CloudBlockBlob getBlockBlobReference(String blobName, String snapshotId)
			throws NotImplementedException, StorageException,
			URISyntaxException {
		throw new NotImplementedException();
	}

	public URI getContainerEndpoint() throws URISyntaxException {
		return PathUtility.appendPathToUri(this.getEndpoint(), this
				.getCredentials().containerEndpointPostfix());
	}

	public CloudBlobContainer getContainerReference(String containerName)
			throws NotImplementedException, URISyntaxException,
			StorageException {
		Utility.assertNotNullOrEmpty("containerAddress", containerName);
		return new CloudBlobContainer(containerName, this);
	}

	public StorageCredentials getCredentials() {
		return m_Credentials;
	}

	public String getDirectoryDelimiter() {
		return m_DirectoryDelimiter;
	}

	public CloudBlobDirectory getDirectoryReference(String directoryName)
			throws NotImplementedException, URISyntaxException,
			StorageException {
		throw new NotImplementedException();
	}

	public URI getEndpoint() {
		return m_Endpoint;
	}

	public CloudPageBlob getPageBlobReference(String blobName)
			throws NotImplementedException, URISyntaxException,
			StorageException {
		throw new NotImplementedException();
	}

	public CloudPageBlob getPageBlobReference(String blobName, String snapshotId)
			throws NotImplementedException, URISyntaxException,
			StorageException {
		throw new NotImplementedException();
	}

	public int getPageBlobStreamWriteSizeInBytes() {
		return m_PageBlobStreamWriteSizeInBytes;
	}

	public int getSingleBlobPutThresholdInBytes() {
		return m_SingleBlobPutThresholdInBytes;
	}

	public int getStreamMinimumReadSizeInBytes() {
		return m_StreamMinimumReadSizeInBytes;
	}

	public int getTimeoutInMs() {
		return m_TimeoutInMs;
	}

	public int getWriteBlockSizeInBytes() {
		return m_WriteBlockSizeInBytes;
	}

	public Iterable<CloudBlobContainer> listContainers() throws Exception,
			NotImplementedException {
		return listContainersWithPrefix(null, ContainerListingDetails.NONE);
	}

	public Iterable<CloudBlobContainer> listContainers(String prefix)
			throws Exception, NotImplementedException {
		return listContainersWithPrefix(prefix, ContainerListingDetails.NONE);
	}

	public Iterable<CloudBlobContainer> listContainers(String prefix,
			ContainerListingDetails listingDetails) throws Exception,
			NotImplementedException {
		return listContainersWithPrefix(prefix, listingDetails);
	}
	protected Iterable<CloudBlobContainer> listContainersWithPrefix(String prefix,
			ContainerListingDetails listingDetails)
			throws StorageInnerException, Exception {
		HttpGet request = containerRequest.list(getEndpoint(), prefix,
				listingDetails);
		getCredentials().signRequest(request, -1L);
		RequestResult result = ExecutionEngine.processRequest(request);
		if (HttpStatusCode.fromInt(result.statusCode) != HttpStatusCode.OK) {
			throw new StorageInnerException("Couldn't list blob's containers");
		}
		ListContainersResponse response = new ListContainersResponse(
				result.httpResponse.getEntity().getContent());
		return response.getContainers(this);
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
