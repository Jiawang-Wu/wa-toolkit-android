package com.windowsazure.samples.android.storageclient;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URISyntaxException;
import java.security.InvalidKeyException;
import java.util.HashMap;
import java.util.StringTokenizer;

import org.apache.http.HttpStatus;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpHead;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.entity.InputStreamEntity;
import org.apache.http.message.AbstractHttpMessage;

public abstract class CloudBlob implements IListBlobItem {

	HashMap<String, String> m_Metadata;

	BlobProperties m_Properties;

	URI m_Uri;

	String m_SnapshotID;

	private CloudBlobContainer m_Container;

	private String m_Name;

	protected CloudBlobClient m_ServiceClient;

	AbstractBlobRequest m_BlobRequest = new BlobWASServiceRequest();

	protected CloudBlob() {
		m_Metadata = new HashMap<String, String>();
		m_Properties = new BlobProperties();
	}

	public CloudBlob(CloudBlob blob) {
		m_Metadata = new HashMap<String, String>();
		m_Properties = new BlobProperties(blob.m_Properties);
		if (blob.m_Metadata != null) {
			for (HashMap.Entry<String, String> entry : blob.m_Metadata
					.entrySet()) {
				m_Metadata.put(entry.getKey(), entry.getValue());
			}
		}
		m_SnapshotID = blob.m_SnapshotID;
		m_Uri = blob.m_Uri;
		m_Container = blob.m_Container;
		m_ServiceClient = blob.m_ServiceClient;
		m_Name = blob.m_Name;
	}

	/**
	* Initializes a new instance of the CloudBlob class.
	*
	*@param uri
	*          the address for the blob
	*@param client
	*          the associated service client.
	* @throws StorageException
	*             an exception representing any error which occurred during the
	*             operation.
	* @throws URISyntaxException
	*             if the resource URI is invalid.
	*/
	public CloudBlob(URI uri, CloudBlobClient client)
			throws StorageException {
		this();
		Utility.assertNotNull("blobAbsoluteUri", uri);
		Utility.assertNotNull("serviceClient", client);
		m_ServiceClient = client;
		m_Uri = uri;
		parseURIQueryStringAndVerify(uri, client);
	}

	/**
	* Initializes a new instance of the CloudBlob class.
	*
	*@param uri
	*          the address for the blob
	*@param client
	*          the associated service client.
	*@param container
	*          the parent container for the object.
	* @throws StorageException
	*             an exception representing any error which occurred during the
	*             operation.
	* @throws URISyntaxException
	*             if the resource URI is invalid.
	*/
	public CloudBlob(URI uri, CloudBlobClient client,
			CloudBlobContainer container) throws StorageException {
		this(uri, client);
		m_Container = container;
	}

	/**
	* Initializes a new instance of the CloudBlob class.
	*
	*@param uri
	*          the address for the blob
	*@param snapshotID
	*          the snapshot version if applicable.
	*@param client
	*          the associated service client.
	* @throws StorageException
	*             an exception representing any error which occurred during the
	*             operation.
	* @throws URISyntaxException
	*             if the resource URI is invalid.
	*/
	public CloudBlob(URI uri, String snapshotId,
			CloudBlobClient client) throws StorageException,
			NotImplementedException {
		throw new NotImplementedException();
	}

	public String acquireLease() throws NotImplementedException,
			StorageException {
		throw new NotImplementedException();
	}

	protected void assertCorrectBlobType() throws StorageException {
		if ((this instanceof CloudBlockBlob)
				&& m_Properties.blobType != BlobType.BLOCK_BLOB) {
			throw new StorageException(
					"IncorrectBlobType",
					String.format(
							"Incorrect Blob type, please use the correct Blob type to access a blob on the server. Expected %s, actual %s",
							BlobType.BLOCK_BLOB, m_Properties.blobType), 306,
					null, null);
		}
		if ((this instanceof CloudPageBlob)
				&& m_Properties.blobType != BlobType.PAGE_BLOB) {
			throw new StorageException(
					"IncorrectBlobType",
					String.format(
							"Incorrect Blob type, please use the correct Blob type to access a blob on the server. Expected %s, actual %s",
							BlobType.PAGE_BLOB, m_Properties.blobType), 306,
					null, null);
		}
	}

	public long breakLease() throws NotImplementedException, StorageException {
		throw new NotImplementedException();
	}

	/**
	*  Copies an existing blob's contents, properties, and metadata to a new
	*  blob.
	*  
	*  @param sourceBlob
	*             The source blob.
	*  @throws StorageException
	*              an exception representing any error which occurred during the
	*              operation.
	*/
	public void copyFromBlob(final CloudBlob sourceBlob)
			throws NotImplementedException, StorageException,
			UnsupportedEncodingException, IOException {
		final CloudBlob blob = this;
		StorageOperation<Void> storageoperation = new StorageOperation<Void>() {
			public Void execute() throws Exception {
				HttpPut request = BlobRequest.copyFrom(
						blob.getTransformedAddress(),
						sourceBlob.getCanonicalName());
				BlobRequest.addMetadata(request, sourceBlob.m_Metadata);
				m_ServiceClient.getCredentials().signRequest(request, 0L);
				this.processRequest(request);
				if (result.statusCode != HttpStatus.SC_CREATED) {
					throw new StorageInnerException("Couldn't delete a blob");
				}
				return null;
			}
		};
		storageoperation.executeTranslatingExceptions();
	}

	public void copyFromBlob(final CloudBlob sourceBlob, String snapshotId)
			throws NotImplementedException, StorageException {
		throw new NotImplementedException();
	}

	public CloudBlob createSnapshot() throws NotImplementedException,
			StorageException {
		throw new NotImplementedException();
	}

	public CloudBlob createSnapshot(String snapshotId)
			throws NotImplementedException, StorageException {
		throw new NotImplementedException();
	}

	/**
	*  Deletes the blob.
	*  
	*  @throws StorageException
	*              an exception representing any error which occurred during the
	*              operation.
	*/
	public void delete() throws StorageException, UnsupportedEncodingException,
			IOException {
		final CloudBlob blob = this;
		StorageOperation<Void> storageOperation = new StorageOperation<Void>() {
			public Void execute() throws Exception {
				HttpDelete request = BlobRequest.delete(blob
						.getTransformedAddress());
				m_ServiceClient.getCredentials().signRequest(request, -1L);
				this.processRequest(request);
				if (result.statusCode != HttpStatus.SC_ACCEPTED) {
					throw new StorageInnerException("Couldn't delete a blob");
				}
				return null;
			}
		};
		storageOperation.executeTranslatingExceptions();
	}

	public void delete(final String snapshotId) throws NotImplementedException,
			StorageException {
		throw new NotImplementedException();
	}

	/**
	*  Deletes the blob if it exists.
	*  
	*  @throws StorageException
	*              an exception representing any error which occurred during the
	*              operation.
	*/
	public boolean deleteIfExists() throws NotImplementedException,
			StorageException {
		throw new NotImplementedException();
	}

	public boolean deleteIfExists(String snapshotId)
			throws NotImplementedException, StorageException {
		throw new NotImplementedException();
	}

	/**
	*  Downloads the contents of a blob to a stream.
	*  
	*  @param outStream
	*             the OutputStream to write the blob to.
	*  @throws StorageException
	*              an exception representing any error which occurred during the
	*              operation.
	*/
	public void download(final OutputStream outStream)
			throws StorageException, IOException {
		final CloudBlob blob = this;
		StorageOperation<Void> storageOperation = new StorageOperation<Void>() {
			public Void execute() throws Exception {
				HttpGet request = BlobRequest.get(blob.getTransformedAddress());
				m_ServiceClient.getCredentials().signRequest(request, -1L);
				this.processRequest(request);
				BlobAttributes attributes = BlobResponse.getAttributes(
						(AbstractHttpMessage) result.httpResponse,
						blob.getUri(), null);
				m_Properties.copyFrom(attributes.properties);
				blob.m_Metadata = attributes.metadata;
				if (result.statusCode == HttpStatus.SC_OK) {
					result.httpResponse.getEntity().writeTo(outStream);
				} else {
					throw new StorageInnerException("Couldn't download a blob");
				}
				return null;
			}
		};
		storageOperation.executeTranslatingExceptions();
	}

	/**
	*  Populates a blob's properties and metadata.
	*  
	*  @throws StorageException
	*              an exception representing any error which occurred during the
	*              operation.
	*/
	public void downloadAttributes() throws StorageException,
			UnsupportedEncodingException, IOException {
		final CloudBlob blob = this;
		StorageOperation<Void> storageOperation = new StorageOperation<Void>() {
			public Void execute() throws Exception {
				HttpHead request = BlobRequest.getProperties(
						blob.getTransformedAddress(), blob.m_SnapshotID, null);
				m_ServiceClient.getCredentials().signRequest(request, -1L);
				this.processRequest(request);
				if (result.statusCode != 200) {
					throw new StorageInnerException(
							"Couldn't download blob attributes");
				}
				BlobAttributes attributes = BlobResponse.getAttributes(
						(AbstractHttpMessage) result.httpResponse,
						blob.getUri(), blob.m_SnapshotID);
				if (attributes.properties.blobType != blob.m_Properties.blobType) {
					throw new StorageException(
							"IncorrectBlobType",
							String.format(
									"Incorrect Blob type, please use the correct Blob type to access a blob on the server. Expected %s, actual %s",
									new Object[] { blob.m_Properties.blobType,
											attributes.properties.blobType }),
							306, null, null);
				} else {
					blob.m_Properties.copyFrom(attributes.properties);
					blob.m_Metadata = attributes.metadata;
					return null;
				}
			}
		};
		storageOperation.executeTranslatingExceptions();
	}

	public void downloadRange(long blobOffset, int length, byte buffer[], int j)
			throws NotImplementedException, StorageException {
		throw new NotImplementedException();
	}

	protected void downloadRangeInternal(final long rangeStart,
			final int length, final byte buffer[]) throws StorageException,
			UnsupportedEncodingException, IOException {
		final CloudBlob blob = this;
		StorageOperation<Void> storageOperation = new StorageOperation<Void>() {
			public Void execute() throws Exception {
				HttpGet request = BlobRequest.get(blob.getTransformedAddress(),
						rangeStart, length);
				m_ServiceClient.getCredentials().signRequest(request, -1L);
				this.processRequest(request);

				if (result.statusCode != 206) {
					throw new StorageInnerException(
							"Couldn't read Blob's content");
				}

				InputStream inputStream = result.httpResponse.getEntity()
						.getContent();

				int totalBytesRead = 0;
				int bytesRead;
				do {
					bytesRead = inputStream.read(buffer, totalBytesRead,
							buffer.length - totalBytesRead);
					totalBytesRead += bytesRead;
				} while (totalBytesRead < buffer.length && bytesRead != 0);

				bytesRead = inputStream.read(new byte[1], 0, 20000);
				if (buffer.length == totalBytesRead && bytesRead != -1) {
					throw new StorageException(
							"OutOfRangeInput",
							"An incorrect number of bytes was read from the connection. The connection may have been closed",
							306, null, null);
				}

				long contentLength = result.httpResponse.getEntity()
						.getContentLength();

				if ((long) totalBytesRead != contentLength) {
					throw new StorageException(
							"OutOfRangeInput",
							"An incorrect number of bytes was read from the connection. The connection may have been closed",
							306, null, null);
				}

				return null;
			}
		};
		storageOperation.executeTranslatingExceptions();
	}

	public boolean exists() throws NotImplementedException, StorageException {
		throw new NotImplementedException();
	}

	public String generateSharedAccessSignature(SharedAccessPolicy policy)
			throws NotImplementedException, InvalidKeyException,
			IllegalArgumentException, StorageException {
		throw new NotImplementedException();
	}

	public String generateSharedAccessSignature(String s)
			throws NotImplementedException, InvalidKeyException,
			IllegalArgumentException, StorageException {
		throw new NotImplementedException();
	}

	String getCanonicalName() {
		// Blob in named container: /accountName/containerName/blobName
		String accountName = new StringTokenizer(this.getUri().getAuthority(),
				".").nextToken();
		String containerNameAndBlobName = this.getUri().getPath();
		return String.format("/%s%s", accountName, containerNameAndBlobName);
	}

	/**
	* Gets the blob item's container.
	* 
	* @return the blob item's container.
	*/
	public CloudBlobContainer getContainer() throws StorageException,
			URISyntaxException {
		if (m_Container == null) {
			String name = PathUtility.getContainerNameFromUri(getUri());
			m_Container = new CloudBlobContainer(name, this.getServiceClient());
		}
		return m_Container;
	}

	public StorageCredentials getCredentials() {
		return m_ServiceClient.getCredentials();
	}

	public HashMap<String, String> getMetadata()
			throws NotImplementedException, NotImplementedException {
		return m_Metadata;
	}

	public String getName() throws NotImplementedException, URISyntaxException {
		if (Utility.isNullOrEmpty(m_Name))
			m_Name = PathUtility.getBlobNameFromURI(getUri());
		return m_Name;
	}

	public BlobProperties getProperties() {
		return m_Properties;
	}

	public URI getQualifiedUri() throws NotImplementedException,
			URISyntaxException, StorageException {
		throw new NotImplementedException();
	}

	/**
	* Returns the CloudBlobClient service client associated with the blob.
	* 
	* @return the CloudBlobClient service client associated with the blob.
	*/
	public CloudBlobClient getServiceClient() {
		if (m_Container != null) {
			return m_Container.getServiceClient();
		} else {
			return m_ServiceClient;
		}
	}

	public String getSnapshotID() throws NotImplementedException,
			NotImplementedException {
		throw new NotImplementedException();
	}

	protected URI getTransformedAddress() throws IllegalArgumentException,
			URISyntaxException, StorageException {
		if (m_ServiceClient.getCredentials().doCredentialsNeedTransformUri()) {
			if (getUri().isAbsolute()) {
				return m_ServiceClient.getCredentials().transformUri(getUri());
			} else {
				StorageException storageexception = StorageException
						.generateNewUnexpectedStorageException(null);
				storageexception.m_ExtendedErrorInformation.errorMessage = "Blob Object relative URIs not supported.";
				throw storageexception;
			}
		} else {
			return getUri();
		}
	}

	/**
	* Gets the uri for this blob.
	*/
	public URI getUri() {
		return m_Uri;
	}

	/**
	* Indicates if this blob is a snapshot or not.
	* 
	* @return <Code>True</Code> if the blob is a snapshot, otherwise
	*         <CODE>False</CODE>
	*/
	public boolean isSnapshot() throws NotImplementedException,
			NotImplementedException {
		throw new NotImplementedException();
	}

	/**
	*  Opens a BlobInputStream to download the Blob. See
	*  CloudBlobClient.setMinimumReadSize to configure read size.
	*  
	*  @return a BlobInputStream to download the Blob.
	*  @throws StorageException
	*              an exception representing any error which occurred during the
	*              operation.
	*/
	public BlobInputStream openInputStream() throws StorageException,
			UnsupportedEncodingException, IOException, StorageInnerException {
		return new BlobInputStream(this);
	}

	private void parseURIQueryStringAndVerify(URI resourceUri,
			CloudBlobClient serviceClient) throws StorageException {
		Utility.assertNotNull("resourceUri", resourceUri);
		if (!resourceUri.isAbsolute()) {
			String errorMessage = String
					.format("Address '%s' is not an absolute address. Relative addresses are not permitted in here.",
							resourceUri);
			throw new IllegalArgumentException(errorMessage);
		}

		m_Uri = PathUtility.stripURIQueryAndFragment(resourceUri);
		HashMap<String, String[]> hashmap = PathUtility
				.parseQueryString(resourceUri.getQuery());

		StorageCredentialsSharedAccessSignature credentialsSAS = SharedAccessSignatureHelper
				.parseQuery(hashmap);
		String as[] = hashmap.get("snapshot");
		if (as != null && as.length > 0) {
			m_SnapshotID = as[0];
		}
		if (credentialsSAS == null) {
			return;
		}
		boolean useServiceClientCredentials = serviceClient != null ? credentialsSAS
				.equals(serviceClient.getCredentials()) : false;
		if (serviceClient == null || !useServiceClientCredentials)
			try {
				m_ServiceClient = new CloudBlobClient(new URI(
						PathUtility.getServiceClientBaseAddress(getUri())),
						credentialsSAS);
			} catch (URISyntaxException urisyntaxexception) {
				throw StorageException
						.generateNewUnexpectedStorageException(urisyntaxexception);
			}
		if (serviceClient != null && !useServiceClientCredentials) {
			m_ServiceClient.setPageBlobStreamWriteSizeInBytes(serviceClient
					.getPageBlobStreamWriteSizeInBytes());
			m_ServiceClient.setSingleBlobPutThresholdInBytes(serviceClient
					.getSingleBlobPutThresholdInBytes());
			m_ServiceClient.setStreamMinimumReadSizeInBytes(serviceClient
					.getStreamMinimumReadSizeInBytes());
			m_ServiceClient.setWriteBlockSizeInBytes(serviceClient
					.getWriteBlockSizeInBytes());
			m_ServiceClient.setDirectoryDelimiter(serviceClient
					.getDirectoryDelimiter());
			m_ServiceClient.setTimeoutInMs(serviceClient.getTimeoutInMs());
		}
	}

	public void releaseLease(String leaseId) throws NotImplementedException,
			StorageException {
		throw new NotImplementedException();
	}

	public void renewLease(String leaseId) throws NotImplementedException,
			StorageException {
		throw new NotImplementedException();
	}

	protected void setContainer(CloudBlobContainer container) {
		m_Container = container;
	}

	public void setMetadata(HashMap<String, String> metadata)
			throws NotImplementedException, NotImplementedException {
		m_Metadata = metadata;
	}

	protected void setProperties(BlobProperties properties) {
		m_Properties = properties;
	}

	public void setSnapshotID(String snapshotId)
			throws NotImplementedException, NotImplementedException {
		throw new NotImplementedException();
	}

	public long tryBreakLease() throws NotImplementedException,
			StorageException {
		throw new NotImplementedException();
	}

	/**
	*  Uploads the sourceStream data to the blob.
	*  
	*  @param sourceStream
	*             the IntputStream to read from.
	*  @param length
	*             the length of the Stream data, -1 if uknown.
	*  @throws StorageException
	*              an exception representing any error which occurred during the
	*              operation.
	*  @throws IOException
	*/
	public abstract void upload(InputStream sourceStream, long length)
			throws NotImplementedException, StorageException, IOException;

	public abstract void upload(InputStream inputStream, long length,
			String snapshotId) throws NotImplementedException,
			StorageException, IOException;

	protected void uploadFullBlob(final InputStream inputStream,
			final long length, final String leaseID) throws StorageException,
			IOException {
		if (length < 0L) {
			throw new IllegalArgumentException(
					"Invalid stream length, specify a positive number of bytes");
		} else {
			final CloudBlob blob = this;
			StorageOperation<Void> storageOperation = new StorageOperation<Void>() {
				public Void execute() throws Exception {
					HttpPut request = BlobRequest.put(
							blob.getTransformedAddress(), blob.m_Properties,
							blob.m_Properties.blobType, leaseID, 0L);
					BlobRequest.addMetadata(request, blob.m_Metadata);
					m_ServiceClient.getCredentials().signRequest(request,
							length);
					InputStreamEntity contentEntity = new InputStreamEntity(
							inputStream, length);
					request.setEntity(contentEntity);
					this.processRequest(request);
					if (result.statusCode != HttpStatus.SC_CREATED) {
						throw new StorageInnerException(
								"Couldn't upload a blob's data");
					}
					return null;
				}
			};

			storageOperation.executeTranslatingExceptions();
		}
	}

	/**
	*  Sets the blobs Metadata on the service.
	*  
	*  @throws StorageException
	*              an exception representing any error which occurred during the
	*              operation.
	*/
	public void uploadMetadata() throws StorageException,
			UnsupportedEncodingException, IOException {
		final String leaseId = "";
		final CloudBlob blob = this;
		StorageOperation<Void> storageOperation = new StorageOperation<Void>() {
			public Void execute() throws Exception {
				HttpPut request = BlobRequest.setMetadata(
						blob.getTransformedAddress(), leaseId);
				BlobRequest.addMetadata(request, blob.m_Metadata);
				m_ServiceClient.getCredentials().signRequest(request, 0L);
				this.processRequest(request);
				if (result.statusCode != 200) {
					throw new StorageInnerException(
							"Couldn't upload a blob's data");
				}
				return null;
			}
		};
		storageOperation.executeTranslatingExceptions();
	}

	public void uploadMetadata(String snapshotId)
			throws NotImplementedException, StorageException {
		throw new NotImplementedException();
	}

	/**
	*  Updates the blob's properties.
	*  
	*  @throws StorageException
	*              an exception representing any error which occurred during the
	*              operation.
	*/
	public void uploadProperties() throws StorageException,
			UnsupportedEncodingException, IOException {
		final CloudBlob blob = this;
		StorageOperation<Void> storageOperation = new StorageOperation<Void>() {
			public Void execute() throws Exception {
				HttpPut request = BlobRequest.setProperties(
						blob.getTransformedAddress(), blob.m_Properties);
				BlobRequest.addMetadata(request, blob.m_Metadata);
				m_ServiceClient.getCredentials().signRequest(request, 0L);
				this.processRequest(request);
				if (result.statusCode != 200) {
					throw new StorageInnerException(
							"Couldn't upload a blob's properties");
				}
				return null;
			}
		};
		storageOperation.executeTranslatingExceptions();
	}

	public void uploadProperties(String snapshotId)
			throws NotImplementedException, StorageException {
		throw new NotImplementedException();
	}
	
}
