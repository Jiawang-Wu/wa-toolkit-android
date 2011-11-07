package com.windowsazure.samples.android.storageclient;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URISyntaxException;
import java.security.InvalidKeyException;
import java.util.HashMap;

import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpHead;
import org.apache.http.client.methods.HttpPut;

import android.net.Uri;

import com.windowsazure.samples.android.storageclient.internal.web.HttpStatusCode;
import com.windowsazure.samples.android.storageclient.internal.xml.DOMAdapter;

public final class CloudBlobContainer {

	static BlobContainerPermissions getContainerAcl(String s)
			throws NotImplementedException, IllegalArgumentException {
		throw new NotImplementedException();
	}

	protected HashMap m_Metadata;

	BlobContainerProperties m_Properties;

	String m_Name;

	URI m_ContainerOperationsUri;

	private CloudBlobClient m_ServiceClient;

	AbstractContainerRequest m_ContainerRequest;

	AbstractBlobRequest m_BlobRequest;

	private CloudBlobContainer(CloudBlobClient serviceClient) {
		m_Metadata = new HashMap();
		m_Properties = new BlobContainerProperties();
		m_ServiceClient = serviceClient;
		m_ContainerRequest = serviceClient.getCredentials()
				.getContainerRequest();
		m_BlobRequest = serviceClient.getCredentials().getBlobRequest();
	}

	public CloudBlobContainer(String containerName,
			CloudBlobClient serviceClient) throws URISyntaxException,
			StorageException {
		this(serviceClient);
		Utility.assertNotNullOrEmpty("containerName", containerName);
		URI uri = PathUtility.appendPathToUri(
				serviceClient.getContainerEndpoint(), containerName);
		m_ContainerOperationsUri = uri;
		m_Name = containerName;
		parseQueryAndVerify(m_ContainerOperationsUri, serviceClient);
	}

	public void create() throws StorageException, NotImplementedException,
			UnsupportedEncodingException, IOException {
		this.create(false);
	}

	public boolean create(final boolean createIfNotExist)
			throws StorageException, NotImplementedException,
			UnsupportedEncodingException, IOException {
		StorageOperation storageOperation = new StorageOperation() {
			public boolean execute(CloudBlobClient serviceClient,
					CloudBlobContainer container) throws Exception {
				HttpPut request = m_ContainerRequest.create(
						container.m_ContainerOperationsUri,
						createIfNotExist);
				ContainerRequest.addMetadata(request,
						container.m_Metadata);
				serviceClient.getCredentials().signRequest(request, 0L);
				result = ExecutionEngine.processRequest(request);

				if (result.statusCode == HttpStatusCode.Conflict.getStatus()
						&& createIfNotExist) {
					return false;
				} else if (result.statusCode != HttpStatusCode.OK.getStatus()
						&& result.statusCode != HttpStatusCode.Created
								.getStatus()) {
					throw new StorageInnerException(
							"Couldn't create a blob container");
				} else {
					if (m_ContainerRequest.isUsingWasServiceDirectly()) {
						BlobContainerAttributes blobcontainerattributes = ContainerResponse
								.getAttributes(request.getURI(), result);
						container
								.setMetadata(blobcontainerattributes.metadata);
						container.m_Properties = blobcontainerattributes.properties;
						container.m_Name = blobcontainerattributes.name;
					}
					return true;
				}
			}

			@Override
			public Object execute(Object cloudBlobClient,
					Object cloudBlobContainer) throws Exception {
				return execute((CloudBlobClient) cloudBlobClient,
						(CloudBlobContainer) cloudBlobContainer);
			}
		};

		return (Boolean) ExecutionEngine.execute(m_ServiceClient, this,
				storageOperation);
	}

	public boolean createIfNotExist() throws NotImplementedException,
			StorageException, UnsupportedEncodingException, IOException {
		return this.create(true);
	}

	public void delete() throws NotImplementedException, StorageException,
			UnsupportedEncodingException, IOException, StorageInnerException {
		StorageOperation storageOperation = new StorageOperation() {
			public Void execute(CloudBlobClient serviceClient,
					CloudBlobContainer container) throws Exception {
				HttpDelete request = m_ContainerRequest.delete(
						container.m_ContainerOperationsUri,
						serviceClient.getTimeoutInMs());
				serviceClient.getCredentials().signRequest(request, -1L);
				result = ExecutionEngine.processRequest(request);
				if (HttpStatusCode.fromInt(result.statusCode) != HttpStatusCode.OK
						&& HttpStatusCode.fromInt(result.statusCode) != HttpStatusCode.Accepted) {
					throw new StorageInnerException(
							"Couldn't delete a blob container");
				}
				return null;
			}

			@Override
			public Object execute(Object obj, Object obj1) throws Exception {
				return execute((CloudBlobClient) obj, (CloudBlobContainer) obj1);
			}
		};

		ExecutionEngine.execute(m_ServiceClient, this, storageOperation);
	}

	public void downloadAttributes() throws NotImplementedException,
			StorageException, UnsupportedEncodingException, IOException {
		StorageOperation storageOperation = new StorageOperation() {

			public Void execute(CloudBlobClient serviceClient,
					CloudBlobContainer container) throws Exception {
				HttpHead request = ContainerRequest
						.getProperties(container
								.getTransformedAddress());
				serviceClient.getCredentials().signRequest(request, -1L);
				result = ExecutionEngine.processRequest(request);
				if (result.statusCode != 200) {
					throw new StorageInnerException(
							"Couldn't download container's attributes");
				} else {
					BlobContainerAttributes blobcontainerattributes = ContainerResponse
							.getAttributes(container.getUri(), result);
					container.m_Metadata = blobcontainerattributes.metadata;
					container.m_Properties = blobcontainerattributes.properties;
					container.m_Name = blobcontainerattributes.name;
					return null;
				}
			}

			@Override
			public Object execute(Object obj, Object obj1) throws Exception {
				return execute((CloudBlobClient) obj, (CloudBlobContainer) obj1);
			}
		};
		ExecutionEngine.execute(m_ServiceClient, this, storageOperation);
	}

	public BlobContainerPermissions downloadPermissions()
			throws NotImplementedException, StorageException {
		throw new NotImplementedException();
	}

	public boolean exists() throws NotImplementedException, StorageException,
			UnsupportedEncodingException, IOException {
		StorageOperation storageOperation = new StorageOperation() {
			public boolean execute(CloudBlobClient serviceClient,
					CloudBlobContainer container) throws Exception {

				URI transformedAddress;
				try
				{
					// The SAS service will throw an exception if we
					// try to get the uri of a non existent container
					transformedAddress = container.getTransformedAddress();
				}
				catch (StorageException exception)
				{
					return false;
				}
				HttpHead request = ContainerRequest
						.getProperties(transformedAddress);
				serviceClient.getCredentials().signRequest(request, -1L);
				result = ExecutionEngine.processRequest(request);
				if (result.statusCode == 200) {
					return true;
				} else if (result.statusCode == 404) {
					return false;
				} else {
					throw new StorageInnerException(
							"Couldn't check if a blob's container exists");
				}
			}

			@Override
			public Object execute(Object obj, Object obj1) throws Exception {
				return execute((CloudBlobClient) obj, (CloudBlobContainer) obj1);
			}
		};

		return ((Boolean) ExecutionEngine.execute(m_ServiceClient, this,
				storageOperation));
	}

	public String generateSharedAccessSignature(
			SharedAccessPolicy policy)
			throws NotImplementedException, InvalidKeyException,
			IllegalArgumentException, StorageException {
		return generateSharedAccessSignatureCore(policy, null);
	}

	public String generateSharedAccessSignature(String s)
			throws NotImplementedException, InvalidKeyException,
			IllegalArgumentException, StorageException {
		return generateSharedAccessSignatureCore(null, s);
	}

	private String generateSharedAccessSignatureCore(
			SharedAccessPolicy policy, String signedIdentifier)
			throws NotImplementedException, InvalidKeyException,
			IllegalArgumentException, StorageException {
		if (!m_ServiceClient.getCredentials().canCredentialsSignRequest()) {
			String errorMessage = "Cannot create Shared Access Signature unless the Account Key credentials are used by the BlobServiceClient.";
			throw new IllegalArgumentException(errorMessage);
		} else {
			String saCanonicalName = getSharedAccessCanonicalName();
			String sasHash = SharedAccessSignatureHelper
					.generateSharedAccessSignatureHash(policy, signedIdentifier,
							saCanonicalName, m_ServiceClient);
			UriQueryBuilder uriquerybuilder = SharedAccessSignatureHelper
					.generateSharedAccessSignature(policy, signedIdentifier, "c",
							sasHash);
			return uriquerybuilder.toString();
		}
	}

	public CloudBlockBlob getBlockBlobReference(String blobName)
			throws NotImplementedException, URISyntaxException,
			StorageException, UnsupportedEncodingException, IOException {
		Utility.assertNotNullOrEmpty("blobAddressUri", blobName);
		URI blobUri = PathUtility.appendPathToUri(this.getUri(), blobName);
		return new CloudBlockBlob(blobUri, m_ServiceClient.clientForBlobOf(this),
				this);
	}

	public CloudBlockBlob getBlockBlobReference(String blobName, String snapshotId)
			throws NotImplementedException, URISyntaxException,
			StorageException {
		throw new NotImplementedException();
	}

	public CloudBlobDirectory getDirectoryReference(String directoryName)
			throws NotImplementedException, URISyntaxException,
			StorageException {
		throw new NotImplementedException();
	}

	public HashMap getMetadata() throws NotImplementedException,
			NotImplementedException {
		return m_Metadata;
	}

	public String getName() throws NotImplementedException,
			NotImplementedException {
		return m_Name;
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

	public BlobContainerProperties getProperties()
			throws NotImplementedException, NotImplementedException {
		throw new NotImplementedException();
	}

	public CloudBlobClient getServiceClient() throws NotImplementedException,
			NotImplementedException {
		return m_ServiceClient;
	}

	private String getSharedAccessCanonicalName()
			throws NotImplementedException, NotImplementedException {
		throw new NotImplementedException();
	}

	protected URI getTransformedAddress() throws NotImplementedException,
			IllegalArgumentException, URISyntaxException, StorageException,
			UnsupportedEncodingException, IOException {
		if (!m_ContainerRequest.isUsingWasServiceDirectly()) {
			return this.getUriWithSas();
		}

		URI containerUri = this.getUri();
		if (m_ServiceClient.getCredentials().doCredentialsNeedTransformUri()) {
			if (containerUri.isAbsolute()) {
				return m_ServiceClient.getCredentials().transformUri(containerUri);
			} else {
				StorageException storageexception = Utility
						.generateNewUnexpectedStorageException(null);
				storageexception.extendedErrorInformation.errorMessage = "Blob Object relative URIs not supported.";
				throw storageexception;
			}
		} else {
			return containerUri;
		}
	}

	public URI getUri() throws StorageException, NotImplementedException,
			UnsupportedEncodingException, IOException,
			IllegalArgumentException, URISyntaxException {
		if (m_ContainerRequest.isUsingWasServiceDirectly()) {
			return this.m_ContainerOperationsUri;
		} else {
			return PathUtility.stripURIQueryAndFragment(this
					.getTransformedAddress());
		}
	}

	private URI getUriWithSas() throws StorageException,
			NotImplementedException, UnsupportedEncodingException, IOException,
			IllegalArgumentException, URISyntaxException {
		if (m_ContainerRequest.isUsingWasServiceDirectly()) {
			return this.getTransformedAddress();
		}

		class GetSASResponseDOMAdapter extends DOMAdapter<URI> {

			public GetSASResponseDOMAdapter(String xmlString) {
				super(xmlString);
			}

			@Override
			public URI build() throws URISyntaxException {
				return new URI(getRootNode().getInnerText());
			}
		}

		StorageOperation storageOperation = new StorageOperation() {
			public URI execute(CloudBlobClient serviceClient,
					CloudBlobContainer container) throws Exception {
				HttpGet request = m_ContainerRequest.getUri(
						container.m_ContainerOperationsUri,
						serviceClient.getTimeoutInMs());
				serviceClient.getCredentials().signRequest(request, -1L);
				result = ExecutionEngine.processRequest(request);
				if (result.statusCode == HttpStatusCode.OK.getStatus()) {
					return new GetSASResponseDOMAdapter(
							Utility.getHttpResponseBody(result.httpResponse))
							.build();
				} else {
					throw new StorageInnerException(
							"Couldn't get a blob container uri");
				}
			}

			@Override
			public Object execute(Object firstArgument, Object secondArgument)
					throws Exception {
				return execute((CloudBlobClient) firstArgument,
						(CloudBlobContainer) secondArgument);
			}
		};

		return (URI) ExecutionEngine.execute(m_ServiceClient, this,
				storageOperation);
	}

	public Iterable<CloudBlob> listBlobs() throws StorageInnerException,
			Exception {
		return this.listBlobs(null);
	}

	public Iterable<CloudBlob> listBlobs(String prefix)
			throws StorageInnerException, Exception {
		return this.listBlobs(prefix, false);
	}

	public Iterable<CloudBlob> listBlobs(String prefix, boolean useFlatBlobListing)
			throws StorageInnerException, Exception {
		HttpGet request = m_BlobRequest.list(this.getServiceClient()
				.getEndpoint(), this, prefix, useFlatBlobListing);
		this.getServiceClient().getCredentials().signRequest(request, -1L);
		RequestResult result = ExecutionEngine.processRequest(request);
		if (HttpStatusCode.fromInt(result.statusCode) != HttpStatusCode.OK) {
			throw new StorageInnerException("Couldn't list container blobs");
		}
		ListBlobsResponse response = new ListBlobsResponse(
				result.httpResponse.getEntity().getContent());
		return response.getBlobs(this.getServiceClient(), this);
	}

	public Iterable listContainers() throws Exception {
		return m_ServiceClient.listContainers();
	}

	public Iterable listContainers(String prefix) throws Exception {
		return m_ServiceClient.listContainers(prefix);
	}

	public Iterable listContainers(String prefix,
			ContainerListingDetails listingDetails) throws Exception {
		return m_ServiceClient.listContainers(prefix, listingDetails);
	}

	private void parseQueryAndVerify(URI completeUri,
			CloudBlobClient serviceClient) throws URISyntaxException,
			StorageException {
		Utility.assertNotNull("completeUri", completeUri);
		if (!completeUri.isAbsolute()) {
			String errorMessage = String
					.format("Address '%s' is not an absolute address. Relative addresses are not permitted in here.",
							new Object[] { completeUri.toString() });
			throw new IllegalArgumentException(errorMessage);
		}
		m_ContainerOperationsUri = PathUtility
				.stripURIQueryAndFragment(completeUri);
		HashMap queryArguments = PathUtility.parseQueryString(completeUri.getQuery());
		StorageCredentialsSharedAccessSignature sasCredentials = SharedAccessSignatureHelper
				.parseQuery(queryArguments);
		if (sasCredentials == null)
			return;
		boolean serviceClientUsesSAS = serviceClient != null ? Utility
				.areCredentialsEqual(sasCredentials,
						serviceClient.getCredentials()) : false;
		if (serviceClient == null || !serviceClientUsesSAS)
			m_ServiceClient = new CloudBlobClient(
					new URI(
							PathUtility
									.getServiceClientBaseAddress(m_ContainerOperationsUri)),
					sasCredentials);
		if (serviceClient != null && !serviceClientUsesSAS) {
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
	public void setMetadata(HashMap metadata) throws NotImplementedException,
			NotImplementedException {
		m_Metadata = metadata;
	}
	protected void setName(String containerName) throws NotImplementedException,
			NotImplementedException {
		m_Name = containerName;
	}
	protected void setProperties(BlobContainerProperties properties)
			throws NotImplementedException, NotImplementedException {
		m_Properties = properties;
	}
	protected void setUri(URI containerUri) throws NotImplementedException,
			NotImplementedException {
		throw new NotImplementedException();
	}
	public void uploadMetadata() throws NotImplementedException,
			StorageException, UnsupportedEncodingException, IOException {
		StorageOperation storageOperation = new StorageOperation() {

			public Void execute(CloudBlobClient serviceClient,
					CloudBlobContainer container) throws Exception {
				HttpPut request = ContainerRequest
						.setMetadata(container.getTransformedAddress());
				ContainerRequest.addMetadata(request,
						container.m_Metadata);
				serviceClient.getCredentials().signRequest(request, 0L);
				result = ExecutionEngine.processRequest(request);
				if (result.statusCode != 200) {
					throw new StorageInnerException(
							"Couldn't upload container's metadata");
				}
				return null;
			}

			@Override
			public Object execute(Object obj, Object obj1) throws Exception {
				return execute((CloudBlobClient) obj, (CloudBlobContainer) obj1);
			}
		};
		ExecutionEngine.execute(m_ServiceClient, this, storageOperation);
	}
	public void uploadPermissions(final BlobContainerPermissions permissions)
			throws NotImplementedException, StorageException,
			UnsupportedEncodingException, IOException {
		StorageOperation storageOperation = new StorageOperation() {
			public Void execute(CloudBlobClient cloudBlobClient,
					CloudBlobContainer cloudBlobContainer) throws Exception {
				HttpPut request = m_ContainerRequest.setAcl(
						cloudBlobContainer.m_ContainerOperationsUri,
						permissions.publicAccess);
				cloudBlobClient.getCredentials().signRequest(request, 0);
				result = ExecutionEngine.processRequest(request);
				if (result.statusCode != HttpStatusCode.OK.getStatus()) {
					throw new StorageInnerException(
							"Couldn't upload permissions to a blob's container");
				}
				return null;
			}

			@Override
			public Object execute(Object cloudBlobClient,
					Object cloudBlobContainer) throws Exception {
				return execute((CloudBlobClient) cloudBlobClient,
						(CloudBlobContainer) cloudBlobContainer);
			}
		};

		ExecutionEngine.execute(m_ServiceClient, this, storageOperation);
	}
}
