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

	AbstractContainerRequest containerRequest;

	AbstractBlobRequest blobRequest;

	private CloudBlobContainer(CloudBlobClient cloudBlobClient) {
		m_Metadata = new HashMap();
		m_Properties = new BlobContainerProperties();
		m_ServiceClient = cloudBlobClient;
		containerRequest = cloudBlobClient.getCredentials()
				.getContainerRequest();
		blobRequest = cloudBlobClient.getCredentials().getBlobRequest();
	}

	public CloudBlobContainer(String containerName,
			CloudBlobClient cloudBlobClient) throws URISyntaxException,
			StorageException {
		this(cloudBlobClient);
		Utility.assertNotNullOrEmpty("containerName", containerName);
		URI uri = PathUtility.appendPathToUri(
				cloudBlobClient.getContainerEndpoint(), containerName);
		m_ContainerOperationsUri = uri;
		m_Name = containerName;
		parseQueryAndVerify(m_ContainerOperationsUri, cloudBlobClient);
	}

	public void create() throws StorageException, NotImplementedException,
			UnsupportedEncodingException, IOException {
		this.create(false);
	}

	public boolean create(final boolean createIfNotExist)
			throws StorageException, NotImplementedException,
			UnsupportedEncodingException, IOException {
		StorageOperation storageoperation = new StorageOperation() {
			public Boolean execute(CloudBlobClient cloudBlobClient,
					CloudBlobContainer cloudBlobContainer) throws Exception {
				HttpPut request = containerRequest.create(
						cloudBlobContainer.m_ContainerOperationsUri,
						createIfNotExist);
				ContainerRequest.addMetadata(request,
						cloudBlobContainer.m_Metadata);
				cloudBlobClient.getCredentials().signRequest(request, 0L);
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
					if (containerRequest.isUsingWasServiceDirectly()) {
						BlobContainerAttributes blobcontainerattributes = ContainerResponse
								.getAttributes(request.getURI(), result);
						cloudBlobContainer
								.setMetadata(blobcontainerattributes.metadata);
						cloudBlobContainer.m_Properties = blobcontainerattributes.properties;
						cloudBlobContainer.m_Name = blobcontainerattributes.name;
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
				storageoperation);
	}

	public boolean createIfNotExist() throws NotImplementedException,
			StorageException, UnsupportedEncodingException, IOException {
		return this.create(true);
	}

	public void delete() throws NotImplementedException, StorageException,
			UnsupportedEncodingException, IOException, StorageInnerException {
		StorageOperation storageoperation = new StorageOperation() {
			public Void execute(CloudBlobClient cloudblobclient,
					CloudBlobContainer cloudblobcontainer) throws Exception {
				HttpDelete request = containerRequest.delete(
						cloudblobcontainer.m_ContainerOperationsUri,
						cloudblobclient.getTimeoutInMs());
				cloudblobclient.getCredentials().signRequest(request, -1L);
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

		ExecutionEngine.execute(m_ServiceClient, this, storageoperation);
	}

	public void downloadAttributes() throws NotImplementedException,
			StorageException, UnsupportedEncodingException, IOException {
		StorageOperation storageoperation = new StorageOperation() {

			public Void execute(CloudBlobClient cloudblobclient,
					CloudBlobContainer cloudblobcontainer) throws Exception {
				HttpHead request = ContainerRequest
						.getProperties(cloudblobcontainer
								.getTransformedAddress());
				cloudblobclient.getCredentials().signRequest(request, -1L);
				result = ExecutionEngine.processRequest(request);
				if (result.statusCode != 200) {
					throw new StorageInnerException(
							"Couldn't download container's attributes");
				} else {
					BlobContainerAttributes blobcontainerattributes = ContainerResponse
							.getAttributes(cloudblobcontainer.getUri(), result);
					cloudblobcontainer.m_Metadata = blobcontainerattributes.metadata;
					cloudblobcontainer.m_Properties = blobcontainerattributes.properties;
					cloudblobcontainer.m_Name = blobcontainerattributes.name;
					return null;
				}
			}

			@Override
			public Object execute(Object obj, Object obj1) throws Exception {
				return execute((CloudBlobClient) obj, (CloudBlobContainer) obj1);
			}
		};
		ExecutionEngine.execute(m_ServiceClient, this, storageoperation);
	}

	public BlobContainerPermissions downloadPermissions()
			throws NotImplementedException, StorageException {
		throw new NotImplementedException();
	}

	public boolean exists() throws NotImplementedException, StorageException,
			UnsupportedEncodingException, IOException {
		StorageOperation storageoperation = new StorageOperation() {
			public Boolean execute(CloudBlobClient cloudblobclient,
					CloudBlobContainer cloudblobcontainer) throws Exception {

				URI uri;
				try
				{
					// The SAS service will throw an exception if we
					// try to get the uri of a non existent container
					uri = cloudblobcontainer.getTransformedAddress();
				}
				catch (StorageException exception)
				{
					return false;
				}
				HttpHead request = ContainerRequest
						.getProperties(uri);
				cloudblobclient.getCredentials().signRequest(request, -1L);
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
				storageoperation));
	}

	public String generateSharedAccessSignature(
			SharedAccessPolicy sharedaccesspolicy)
			throws NotImplementedException, InvalidKeyException,
			IllegalArgumentException, StorageException {
		return generateSharedAccessSignatureCore(sharedaccesspolicy, null);
	}

	public String generateSharedAccessSignature(String s)
			throws NotImplementedException, InvalidKeyException,
			IllegalArgumentException, StorageException {
		return generateSharedAccessSignatureCore(null, s);
	}

	private String generateSharedAccessSignatureCore(
			SharedAccessPolicy sharedaccesspolicy, String s)
			throws NotImplementedException, InvalidKeyException,
			IllegalArgumentException, StorageException {
		if (!m_ServiceClient.getCredentials().canCredentialsSignRequest()
				.booleanValue()) {
			String s1 = "Cannot create Shared Access Signature unless the Account Key credentials are used by the BlobServiceClient.";
			throw new IllegalArgumentException(s1);
		} else {
			String s2 = getSharedAccessCanonicalName();
			String s3 = SharedAccessSignatureHelper
					.generateSharedAccessSignatureHash(sharedaccesspolicy, s,
							s2, m_ServiceClient);
			UriQueryBuilder uriquerybuilder = SharedAccessSignatureHelper
					.generateSharedAccessSignature(sharedaccesspolicy, s, "c",
							s3);
			return uriquerybuilder.toString();
		}
	}

	public CloudBlockBlob getBlockBlobReference(String s)
			throws NotImplementedException, URISyntaxException,
			StorageException, UnsupportedEncodingException, IOException {
		Utility.assertNotNullOrEmpty("blobAddressUri", s);
		URI uri = PathUtility.appendPathToUri(this.getUri(), s);
		return new CloudBlockBlob(uri, m_ServiceClient.clientForBlobOf(this),
				this);
	}

	public CloudBlockBlob getBlockBlobReference(String s, String s1)
			throws NotImplementedException, URISyntaxException,
			StorageException {
		throw new NotImplementedException();
	}

	public CloudBlobDirectory getDirectoryReference(String s)
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

	public CloudPageBlob getPageBlobReference(String s)
			throws NotImplementedException, URISyntaxException,
			StorageException {
		throw new NotImplementedException();
	}

	public CloudPageBlob getPageBlobReference(String s, String s1)
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
		if (!containerRequest.isUsingWasServiceDirectly()) {
			return this.getUriWithSas();
		}

		URI uri = this.getUri();
		if (m_ServiceClient.getCredentials().doCredentialsNeedTransformUri()
				.booleanValue()) {
			if (uri.isAbsolute()) {
				return m_ServiceClient.getCredentials().transformUri(uri);
			} else {
				StorageException storageexception = Utility
						.generateNewUnexpectedStorageException(null);
				storageexception.extendedErrorInformation.errorMessage = "Blob Object relative URIs not supported.";
				throw storageexception;
			}
		} else {
			return uri;
		}
	}

	public URI getUri() throws StorageException, NotImplementedException,
			UnsupportedEncodingException, IOException,
			IllegalArgumentException, URISyntaxException {
		if (containerRequest.isUsingWasServiceDirectly()) {
			return this.m_ContainerOperationsUri;
		} else {
			return PathUtility.stripURIQueryAndFragment(this
					.getTransformedAddress());
		}
	}

	private URI getUriWithSas() throws StorageException,
			NotImplementedException, UnsupportedEncodingException, IOException,
			IllegalArgumentException, URISyntaxException {
		if (containerRequest.isUsingWasServiceDirectly()) {
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

		StorageOperation storageoperation = new StorageOperation() {
			public URI execute(CloudBlobClient cloudblobclient,
					CloudBlobContainer cloudblobcontainer) throws Exception {
				HttpGet request = containerRequest.getUri(
						cloudblobcontainer.m_ContainerOperationsUri,
						cloudblobclient.getTimeoutInMs());
				cloudblobclient.getCredentials().signRequest(request, -1L);
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
				storageoperation);
	}

	public Iterable<CloudBlob> listBlobs() throws StorageInnerException,
			Exception {
		return this.listBlobs(null);
	}

	public Iterable<CloudBlob> listBlobs(String s)
			throws StorageInnerException, Exception {
		return this.listBlobs(s, false);
	}

	public Iterable<CloudBlob> listBlobs(String s, boolean useFlatBlobListing)
			throws StorageInnerException, Exception {
		HttpGet request = blobRequest.list(this.getServiceClient()
				.getEndpoint(), this, s, useFlatBlobListing);
		this.getServiceClient().getCredentials().signRequest(request, -1L);
		RequestResult result = ExecutionEngine.processRequest(request);
		if (HttpStatusCode.fromInt(result.statusCode) != HttpStatusCode.OK) {
			throw new StorageInnerException("Couldn't list container blobs");
		}
		ListBlobsResponse listcontainersresponse = new ListBlobsResponse(
				result.httpResponse.getEntity().getContent());
		return listcontainersresponse.getBlobs(this.getServiceClient(), this);
	}

	public Iterable listContainers() throws Exception {
		return m_ServiceClient.listContainers();
	}

	public Iterable listContainers(String s) throws Exception {
		return m_ServiceClient.listContainers(s);
	}

	public Iterable listContainers(String s,
			ContainerListingDetails containerlistingdetails) throws Exception {
		return m_ServiceClient.listContainers(s, containerlistingdetails);
	}

	private void parseQueryAndVerify(URI completeUri,
			CloudBlobClient cloudBlobClient) throws URISyntaxException,
			StorageException {
		Utility.assertNotNull("completeUri", completeUri);
		if (!completeUri.isAbsolute()) {
			String s = String
					.format("Address '%s' is not an absolute address. Relative addresses are not permitted in here.",
							new Object[] { completeUri.toString() });
			throw new IllegalArgumentException(s);
		}
		m_ContainerOperationsUri = PathUtility
				.stripURIQueryAndFragment(completeUri);
		HashMap hashmap = PathUtility.parseQueryString(completeUri.getQuery());
		StorageCredentialsSharedAccessSignature storagecredentialssharedaccesssignature = SharedAccessSignatureHelper
				.parseQuery(hashmap);
		if (storagecredentialssharedaccesssignature == null)
			return;
		Boolean boolean1 = Boolean.valueOf(cloudBlobClient != null ? Utility
				.areCredentialsEqual(storagecredentialssharedaccesssignature,
						cloudBlobClient.getCredentials()) : false);
		if (cloudBlobClient == null || !boolean1.booleanValue())
			m_ServiceClient = new CloudBlobClient(
					new URI(
							PathUtility
									.getServiceClientBaseAddress(m_ContainerOperationsUri)),
					storagecredentialssharedaccesssignature);
		if (cloudBlobClient != null && !boolean1.booleanValue()) {
			m_ServiceClient.setPageBlobStreamWriteSizeInBytes(cloudBlobClient
					.getPageBlobStreamWriteSizeInBytes());
			m_ServiceClient.setSingleBlobPutThresholdInBytes(cloudBlobClient
					.getSingleBlobPutThresholdInBytes());
			m_ServiceClient.setStreamMinimumReadSizeInBytes(cloudBlobClient
					.getStreamMinimumReadSizeInBytes());
			m_ServiceClient.setWriteBlockSizeInBytes(cloudBlobClient
					.getWriteBlockSizeInBytes());
			m_ServiceClient.setConcurrentRequestCount(cloudBlobClient
					.getConcurrentRequestCount());
			m_ServiceClient.setDirectoryDelimiter(cloudBlobClient
					.getDirectoryDelimiter());
			m_ServiceClient.setTimeoutInMs(cloudBlobClient.getTimeoutInMs());
		}
	}
	public void setMetadata(HashMap hashmap) throws NotImplementedException,
			NotImplementedException {
		m_Metadata = hashmap;
	}
	protected void setName(String s) throws NotImplementedException,
			NotImplementedException {
		m_Name = s;
	}
	protected void setProperties(BlobContainerProperties blobcontainerproperties)
			throws NotImplementedException, NotImplementedException {
		m_Properties = blobcontainerproperties;
	}
	protected void setUri(URI uri) throws NotImplementedException,
			NotImplementedException {
		throw new NotImplementedException();
	}
	public void uploadMetadata() throws NotImplementedException,
			StorageException, UnsupportedEncodingException, IOException {
		StorageOperation storageoperation = new StorageOperation() {

			public Void execute(CloudBlobClient cloudblobclient,
					CloudBlobContainer cloudblobcontainer) throws Exception {
				HttpPut request = ContainerRequest
						.setMetadata(cloudblobcontainer.getTransformedAddress());
				ContainerRequest.addMetadata(request,
						cloudblobcontainer.m_Metadata);
				cloudblobclient.getCredentials().signRequest(request, 0L);
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
		ExecutionEngine.execute(m_ServiceClient, this, storageoperation);
	}
	public void uploadPermissions(final BlobContainerPermissions permissions)
			throws NotImplementedException, StorageException,
			UnsupportedEncodingException, IOException {
		StorageOperation storageoperation = new StorageOperation() {
			public Void execute(CloudBlobClient cloudBlobClient,
					CloudBlobContainer cloudBlobContainer) throws Exception {
				HttpPut request = containerRequest.setAcl(
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

		ExecutionEngine.execute(m_ServiceClient, this, storageoperation);
	}
}
