package com.windowsazure.samples.android.storageclient;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URISyntaxException;
import java.security.InvalidKeyException;
import java.util.HashMap;

import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpHead;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.entity.InputStreamEntity;
import org.apache.http.message.AbstractHttpMessage;

import com.windowsazure.samples.android.storageclient.internal.web.HttpStatusCode;

public abstract class CloudBlob implements IListBlobItem {

	HashMap<String, String> m_Metadata;

	BlobProperties m_Properties;

	URI m_Uri;

	String m_SnapshotID;

	private CloudBlobContainer m_Container;

	protected CloudBlobDirectory m_Parent;

	private String m_Name;

	protected CloudBlobClient m_ServiceClient;

	AbstractBlobRequest m_BlobRequest = new BlobWASServiceRequest();

	protected CloudBlob() throws NotImplementedException,
			NotImplementedException {
		m_Metadata = new HashMap<String, String>();
		m_Properties = new BlobProperties();
	}

	public CloudBlob(CloudBlob cloudBlob) throws NotImplementedException,
			NotImplementedException {
		throw new NotImplementedException();
	}

	public CloudBlob(URI blobAbsoluteUri, CloudBlobClient serviceClient)
			throws NotImplementedException, StorageException {
		this();
		Utility.assertNotNull("blobAbsoluteUri", blobAbsoluteUri);
		Utility.assertNotNull("serviceClient", serviceClient);
		m_ServiceClient = serviceClient;
		m_Uri = blobAbsoluteUri;
		parseURIQueryStringAndVerify(blobAbsoluteUri, serviceClient);
	}

	public CloudBlob(URI blobAbsoluteUri, CloudBlobClient serviceClient,
			CloudBlobContainer container)
			throws NotImplementedException, StorageException {
		this(blobAbsoluteUri, serviceClient);
		m_Container = container;
	}

	public CloudBlob(URI blobAbsoluteUri, String snapshotId, CloudBlobClient serviceClient)
			throws NotImplementedException, StorageException {
		throw new NotImplementedException();
	}

	public String acquireLease() throws NotImplementedException,
			StorageException {
		throw new NotImplementedException();
	}

	protected void assertCorrectBlobType() throws NotImplementedException,
			StorageException {
		throw new NotImplementedException();
	}

	public long breakLease() throws NotImplementedException, StorageException {
		throw new NotImplementedException();
	}

	public void copyFromBlob(CloudBlob sourceBlob)
			throws NotImplementedException, StorageException {
		throw new NotImplementedException();
	}

	public void copyFromBlob(final CloudBlob sourceBlob, String snapshotId)
			throws NotImplementedException, StorageException {
		throw new NotImplementedException();
	}

	public CloudBlob createSnapshot() throws NotImplementedException,
			StorageException {
		throw new NotImplementedException();
	}

	public CloudBlob createSnapshot(String snapshotId) throws NotImplementedException,
			StorageException {
		throw new NotImplementedException();
	}

	public void delete() throws NotImplementedException, StorageException,
			UnsupportedEncodingException, IOException {
		StorageOperation storageOperation = new StorageOperation() {
			public Void execute(CloudBlobClient serviceClient,
					CloudBlob blob) throws Exception {
				HttpDelete request = BlobRequest.delete(blob
						.getTransformedAddress());
				serviceClient.getCredentials().signRequest(request, -1L);
				result = ExecutionEngine.processRequest(request);
				if (result.statusCode != HttpStatusCode.Accepted.getStatus()) {
					throw new StorageInnerException("Couldn't delete a blob");
				}
				return null;
			}

			@Override
			public Object execute(Object serviceClient, Object blob) throws Exception {
				return execute((CloudBlobClient) serviceClient, (CloudBlob) blob);
			}
		};
		ExecutionEngine.execute(m_ServiceClient, this, storageOperation);
	}

	public void delete(final String snapshotId) throws NotImplementedException,
			StorageException {
		throw new NotImplementedException();
	}

	public boolean deleteIfExists() throws NotImplementedException,
			StorageException {
		throw new NotImplementedException();
	}

	public boolean deleteIfExists(String snapshotId) throws NotImplementedException,
			StorageException {
		throw new NotImplementedException();
	}

	public void download(final OutputStream outputStream)
			throws NotImplementedException, StorageException, IOException {
		StorageOperation storageOperation = new StorageOperation() {
			public Void execute(CloudBlobClient serviceClient,
					CloudBlob blob) throws Exception {
				HttpGet request = BlobRequest.get(blob
						.getTransformedAddress());
				serviceClient.getCredentials().signRequest(request, -1L);
				RequestResult result = ExecutionEngine.processRequest(request);
				BlobAttributes attributes = BlobResponse.getAttributes(
						(AbstractHttpMessage) result.httpResponse,
						blob.getUri(), null);
				m_Properties = attributes.properties;
				result.httpResponse.getEntity().writeTo(outputStream);
				if (result.statusCode != 200) {
					throw new StorageInnerException("Couldn't download a blob");
				}
				return null;
			}

			@Override
			public Object execute(Object obj, Object obj1) throws Exception {
				return execute((CloudBlobClient) obj, (CloudBlob) obj1);
			}

		};
		ExecutionEngine.execute(m_ServiceClient, this, storageOperation);
	}

	public void downloadAttributes() throws NotImplementedException,
			StorageException, UnsupportedEncodingException, IOException {
		StorageOperation storageOperation = new StorageOperation() {

			public Void execute(CloudBlobClient serviceClient,
					CloudBlob blob) throws Exception {
				HttpHead request = BlobRequest.getProperties(
						blob.getTransformedAddress(),
						blob.m_SnapshotID, null);
				serviceClient.getCredentials().signRequest(request, -1L);
				result = ExecutionEngine.processRequest(request);
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
									new Object[] {
											blob.m_Properties.blobType,
											attributes.properties.blobType }),
							306, null, null);
				} else {
					blob.m_Properties = attributes.properties;
					blob.m_Metadata = attributes.metadata;
					return null;
				}
			}

			@Override
			public Object execute(Object obj, Object obj1) throws Exception {
				return execute((CloudBlobClient) obj, (CloudBlob) obj1);
			}
		};
		ExecutionEngine.execute(m_ServiceClient, this, storageOperation);
	}

	public void downloadRange(long blobOffset, int length, byte buffer[], int j)
			throws NotImplementedException, StorageException {
		throw new NotImplementedException();
	}

	protected void downloadRangeInternal(final long blobOffset,
			final int length, final byte buffer[], int i)
			throws NotImplementedException, StorageException {
		throw new NotImplementedException();
	}

	public boolean exists() throws NotImplementedException, StorageException {
		throw new NotImplementedException();
	}

	public String generateSharedAccessSignature(
			SharedAccessPolicy policy)
			throws NotImplementedException, InvalidKeyException,
			IllegalArgumentException, StorageException {
		throw new NotImplementedException();
	}

	public String generateSharedAccessSignature(String s)
			throws NotImplementedException, InvalidKeyException,
			IllegalArgumentException, StorageException {
		throw new NotImplementedException();
	}

	private String generateSharedAccessSignatureCore(
			SharedAccessPolicy policy, String s)
			throws NotImplementedException, InvalidKeyException,
			IllegalArgumentException, StorageException {
		throw new NotImplementedException();
	}

	String getCanonicalName(boolean flag) throws NotImplementedException,
			NotImplementedException {
		throw new NotImplementedException();
	}

	@Override
	public CloudBlobContainer getContainer() throws NotImplementedException,
			StorageException, URISyntaxException {
		if (m_Container == null) {
			String name = PathUtility.getContainerNameFromUri(getUri());
			m_Container = new CloudBlobContainer(name, this.getServiceClient());
		}
		return m_Container;
	}

	public StorageCredentials getCredentials() {
		return m_ServiceClient.getCredentials();
	}

	public HashMap<String, String> getMetadata() throws NotImplementedException,
			NotImplementedException {
		return m_Metadata;
	}

	public String getName() throws NotImplementedException, URISyntaxException {
		if (Utility.isNullOrEmpty(m_Name))
			m_Name = PathUtility.getBlobNameFromURI(getUri());
		return m_Name;
	}

	@Override
	public CloudBlobDirectory getParent() throws NotImplementedException,
			URISyntaxException, StorageException {
		throw new NotImplementedException();
	}

	public BlobProperties getProperties() throws NotImplementedException,
			NotImplementedException {
		return m_Properties;
	}

	public URI getQualifiedUri() throws NotImplementedException,
			URISyntaxException, StorageException {
		throw new NotImplementedException();
	}

	public CloudBlobClient getServiceClient() throws NotImplementedException,
			NotImplementedException {
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

	protected URI getTransformedAddress() throws NotImplementedException,
			IllegalArgumentException, URISyntaxException, StorageException {
		if (m_ServiceClient.getCredentials().doCredentialsNeedTransformUri()) {
			if (getUri().isAbsolute()) {
				return m_ServiceClient.getCredentials().transformUri(getUri());
			} else {
				StorageException storageexception = Utility
						.generateNewUnexpectedStorageException(null);
				storageexception.extendedErrorInformation.errorMessage = "Blob Object relative URIs not supported.";
				throw storageexception;
			}
		} else {
			return getUri();
		}
	}

	@Override
	public URI getUri() throws NotImplementedException {
		return m_Uri;
	}

	public boolean isSnapshot() throws NotImplementedException,
			NotImplementedException {
		throw new NotImplementedException();
	}

	public BlobInputStream openInputStream() throws NotImplementedException,
			StorageException {
		throw new NotImplementedException();
	}

	private void parseURIQueryStringAndVerify(URI resourceUri,
			CloudBlobClient serviceClient) throws NotImplementedException,
			StorageException {
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
		boolean useServiceClientCredentials = serviceClient != null ? Utility
				.areCredentialsEqual(credentialsSAS,
						serviceClient.getCredentials()) : false;
		if (serviceClient == null || !useServiceClientCredentials)
			try {
				m_ServiceClient = new CloudBlobClient(new URI(
						PathUtility.getServiceClientBaseAddress(getUri())),
						credentialsSAS);
			} catch (URISyntaxException urisyntaxexception) {
				throw Utility
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

	protected void setContainer(CloudBlobContainer container)
			throws NotImplementedException, NotImplementedException {
		throw new NotImplementedException();
	}

	public void setMetadata(HashMap<String, String> metadata) throws NotImplementedException,
			NotImplementedException {
		throw new NotImplementedException();
	}

	protected void setProperties(BlobProperties properties)
			throws NotImplementedException, NotImplementedException {
		throw new NotImplementedException();
	}

	public void setSnapshotID(String snapshotId) throws NotImplementedException,
			NotImplementedException {
		throw new NotImplementedException();
	}
	public long tryBreakLease() throws NotImplementedException,
			StorageException {
		throw new NotImplementedException();
	}
	public abstract void upload(InputStream inputStream, long length)
			throws NotImplementedException, StorageException, IOException;
	public abstract void upload(InputStream inputStream, long length, String snapshotId)
			throws NotImplementedException, StorageException, IOException;
	protected void uploadFullBlob(final InputStream inputStream,
			final long length, final String leaseID)
			throws NotImplementedException, StorageException, IOException {
		if (length < 0L) {
			throw new IllegalArgumentException(
					"Invalid stream length, specify a positive number of bytes");
		} else {
			StorageOperation storageOperation = new StorageOperation() {
				public Void execute(CloudBlobClient serviceClient,
						CloudBlob blob) throws Exception {
					HttpPut request = BlobRequest.put(
							blob.getTransformedAddress(),
							blob.m_Properties,
							blob.m_Properties.blobType, leaseID, 0L);
					BlobRequest.addMetadata(request, blob.m_Metadata);
					serviceClient.getCredentials().signRequest(request,
							length);
					InputStreamEntity contentEntity = new InputStreamEntity(
							inputStream, length);
					request.setEntity(contentEntity);
					result = ExecutionEngine.processRequest(request);
					if (result.statusCode != HttpStatusCode.Created.getStatus()) {
						throw new StorageInnerException(
								"Couldn't upload a blob's data");
					}
					return null;
				}

				@Override
				public Object execute(Object firstArgument,
						Object secondArgument) throws Exception {
					return execute((CloudBlobClient) firstArgument,
							(CloudBlob) secondArgument);
				}

			};

			ExecutionEngine.execute(m_ServiceClient, this, storageOperation);
			return;
		}
	}
	public void uploadMetadata() throws NotImplementedException,
			StorageException, UnsupportedEncodingException, IOException {
		final String leaseId = "";
		StorageOperation storageOperation = new StorageOperation() {
			public Void execute(CloudBlobClient serviceClient,
					CloudBlob blob) throws Exception {
				HttpPut request = BlobRequest.setMetadata(
						blob.getTransformedAddress(), leaseId);
				BlobRequest.addMetadata(request, blob.m_Metadata);
				serviceClient.getCredentials().signRequest(request, 0L);
				result = ExecutionEngine.processRequest(request);
				if (result.statusCode != 200) {
					throw new StorageInnerException(
							"Couldn't upload a blob's data");
				}
				return null;
			}

			@Override
			public Object execute(Object obj, Object obj1) throws Exception {
				return execute((CloudBlobClient) obj, (CloudBlob) obj1);
			}

		};
		ExecutionEngine.execute(m_ServiceClient, this, storageOperation);
	}
	public void uploadMetadata(String snapshotId) throws NotImplementedException,
			StorageException {
		throw new NotImplementedException();
	}
	public void uploadProperties() throws NotImplementedException,
			StorageException {
		throw new NotImplementedException();
	}
	public void uploadProperties(String snapshotId) throws NotImplementedException,
			StorageException {
		throw new NotImplementedException();
	}
}
