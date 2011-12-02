package com.windowsazure.samples.android.storageclient;

import org.apache.http.HttpStatus;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URISyntaxException;
import java.security.InvalidKeyException;
import java.util.HashMap;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpHead;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.message.AbstractHttpMessage;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

public final class CloudBlobContainer {

    static BlobContainerPermissions getContainerAcl(String aclString)
            throws IllegalArgumentException {
            BlobContainerPublicAccessType publicAccessType = BlobContainerPublicAccessType.OFF;
            if(!Utility.isNullOrEmpty(aclString)) {
                String aclStringAsLowerCase = aclString.toLowerCase();
                if("container".equals(aclStringAsLowerCase)) {
                    publicAccessType = BlobContainerPublicAccessType.CONTAINER;
                }
                else if("blob".equals(aclStringAsLowerCase)) {
                    publicAccessType = BlobContainerPublicAccessType.BLOB;
                }
                else {
                    throw new IllegalArgumentException(String.format("Invalid acl public access type returned '%s'. Expected blob or container.", aclString));
                }
            }
            BlobContainerPermissions permissions = new BlobContainerPermissions();
            permissions.publicAccess = publicAccessType;
            return permissions;
        }

	protected HashMap<String, String> m_Metadata;

	BlobContainerProperties m_Properties;

	String m_Name;

	URI m_ContainerOperationsUri;

	private CloudBlobClient m_ServiceClient;

	AbstractContainerRequest m_ContainerRequest;

	AbstractBlobRequest m_BlobRequest;

	private CloudBlobContainer(CloudBlobClient serviceClient) {
		m_Metadata = new HashMap<String, String>();
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

	public void create() throws StorageException, UnsupportedEncodingException, IOException {
		this.create(false);
	}

	public boolean create(final boolean createIfNotExist)
			throws StorageException, UnsupportedEncodingException, IOException {
		final CloudBlobContainer container = this;
		StorageOperation<Boolean> storageOperation = new StorageOperation<Boolean>() {
			public Boolean execute() throws Exception {
				HttpPut request = m_ContainerRequest.create(
						container.m_ContainerOperationsUri,
						createIfNotExist);
				ContainerRequest.addMetadata(request,
						container.m_Metadata);
				m_ServiceClient.getCredentials().signRequest(request, 0L);
				this.processRequest(request);

				if (result.statusCode == HttpStatus.SC_OK
						&& createIfNotExist) {
					return true;
				} else if (result.statusCode == HttpStatus.SC_CONFLICT
							&& createIfNotExist) {
						return false;
				} else if (result.statusCode != HttpStatus.SC_OK
						&& result.statusCode != HttpStatus.SC_CREATED) {
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
		};

        return storageOperation.executeTranslatingExceptions();
	}

	public boolean createIfNotExist() throws StorageException, UnsupportedEncodingException, IOException {
		return this.create(true);
	}

	public void delete() throws StorageException,
			UnsupportedEncodingException, IOException, StorageInnerException {
		final CloudBlobContainer container = this;
		StorageOperation<Void> storageOperation = new StorageOperation<Void>() {
			public Void execute() throws Exception {
				HttpDelete request = m_ContainerRequest.delete(
						container.m_ContainerOperationsUri);
				m_ServiceClient.getCredentials().signRequest(request, -1L);
				this.processRequest(request);
				if (result.statusCode != HttpStatus.SC_OK
						&& result.statusCode != HttpStatus.SC_ACCEPTED) {
					throw new StorageInnerException(
							"Couldn't delete a blob container");
				}
				return null;
			}
		};

        storageOperation.executeTranslatingExceptions();
	}

	public void downloadAttributes() throws StorageException, UnsupportedEncodingException, IOException {
		final CloudBlobContainer container = this;
		StorageOperation<Void>  storageOperation = new StorageOperation<Void>() {
			public Void execute() throws Exception {
				HttpHead request = m_ContainerRequest
						.getProperties(container
								.getTransformedAddress());
				m_ServiceClient.getCredentials().signRequest(request, -1L);
				this.processRequest(request);
				if (result.statusCode != 200) {
					throw new StorageInnerException("Couldn't download container's attributes");
				} else {
					BlobContainerAttributes blobcontainerattributes = ContainerResponse
							.getAttributes(container.getUri(), result);
					container.m_Metadata = blobcontainerattributes.metadata;
					container.m_Properties = blobcontainerattributes.properties;
					container.m_Name = blobcontainerattributes.name;
					return null;
				}
			}
		};
        storageOperation.executeTranslatingExceptions();
	}

	public BlobContainerPermissions downloadPermissions()
			throws StorageException, UnsupportedEncodingException, IOException {
		final CloudBlobContainer container = this;
        StorageOperation<BlobContainerPermissions> storageOperation = new StorageOperation<BlobContainerPermissions> () {
            public BlobContainerPermissions execute()
                throws Exception {
            	HttpGet request = ContainerRequest.getAcl(container.getUri());
            	m_ServiceClient.getCredentials().signRequest(request, -1L);
				this.processRequest(request);
                if(result.statusCode != 200) {
					throw new StorageInnerException("Couldn't download container's permissions");
                }
                String s = ContainerResponse.getAcl((AbstractHttpMessage) result.httpResponse);
                BlobContainerPermissions permissions = CloudBlobContainer.getContainerAcl(s);
                return permissions;
            }
        };
        return storageOperation.executeTranslatingExceptions();
	}

	public boolean exists() throws StorageException,
			UnsupportedEncodingException, IOException {
		final CloudBlobContainer container = this;
		StorageOperation<Boolean> storageOperation = new StorageOperation<Boolean>() {
			public Boolean execute() throws Exception {

					// If we are using WA directly, we try to get the properties
					HttpHead request = m_ContainerRequest
							.getProperties(container.getTransformedAddress());
					m_ServiceClient.getCredentials().signRequest(request, -1L);
					this.processRequest(request);
					if (result.statusCode == 200) {
						return true;
					} else if (result.statusCode == 404) {
						return false;
					} else {
						throw new StorageInnerException(
								"Couldn't check if a blob's container exists");
					}
			}
		};

        return storageOperation.executeTranslatingExceptions();
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
			throws URISyntaxException,
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

	public HashMap<String, String> getMetadata() {
		return m_Metadata;
	}

	public String getName() {
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

	public BlobContainerProperties getProperties() {
        return m_Properties;
	}

	public CloudBlobClient getServiceClient(){
		return m_ServiceClient;
	}

	private String getSharedAccessCanonicalName()
			throws NotImplementedException, NotImplementedException {
		throw new NotImplementedException();
	}

	protected URI getTransformedAddress() throws IllegalArgumentException, URISyntaxException, StorageException,
			UnsupportedEncodingException, IOException {
		if (!m_ContainerRequest.isUsingWasServiceDirectly()) {
			return this.getUriWithSas();
		}

		URI containerUri = this.getUri();
		if (m_ServiceClient.getCredentials().doCredentialsNeedTransformUri()) {
			if (containerUri.isAbsolute()) {
				return m_ServiceClient.getCredentials().transformUri(containerUri);
			} else {
				StorageException storageexception = StorageException
						.generateNewUnexpectedStorageException(null);
				storageexception.m_ExtendedErrorInformation.errorMessage = "Blob Object relative URIs not supported.";
				throw storageexception;
			}
		} else {
			return containerUri;
		}
	}

	public URI getUri() throws StorageException, UnsupportedEncodingException, IOException,
			IllegalArgumentException, URISyntaxException {
		if (m_ContainerRequest.isUsingWasServiceDirectly()) {
			return this.m_ContainerOperationsUri;
		} else {
			return PathUtility.stripURIQueryAndFragment(this
					.getTransformedAddress());
		}
	}

	private URI getUriWithSas() throws StorageException,
			UnsupportedEncodingException, IOException,
			IllegalArgumentException, URISyntaxException {
		if (m_ContainerRequest.isUsingWasServiceDirectly()) {
			return this.getTransformedAddress();
		}

		final CloudBlobContainer container = this;
		StorageOperation<URI> storageOperation = new StorageOperation<URI>() {
			public URI execute() throws Exception {
				HttpGet request = m_ContainerRequest.getUri(
						container.m_ContainerOperationsUri);
				m_ServiceClient.getCredentials().signRequest(request, -1L);
				this.processRequest(request);
				if (result.statusCode == HttpStatus.SC_OK) {
					DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
					DocumentBuilder builder = factory.newDocumentBuilder();
					Document dom = builder.parse(result.httpResponse.getEntity().getContent());
					Element root = dom.getDocumentElement();
					return new URI(root.getTextContent());

				} else {
					throw new StorageInnerException(
							"Couldn't get a blob container uri");
				}
			}
		};

        return storageOperation.executeTranslatingExceptions();
	}

	public Iterable<CloudBlob> listBlobs() throws StorageInnerException,
			Exception {
		return this.listBlobs(null);
	}

	public Iterable<CloudBlob> listBlobs(String prefix)
			throws StorageInnerException, Exception {
		return this.listBlobs(prefix, false);
	}

	public Iterable<CloudBlob> listBlobs(final String prefix, final boolean useFlatBlobListing)
			throws StorageInnerException, Exception {
		final CloudBlobContainer container = this;
		StorageOperation<Iterable<CloudBlob>> storageOperation = new StorageOperation<Iterable<CloudBlob>>() {
			public Iterable<CloudBlob> execute() throws Exception {
		HttpGet request = m_BlobRequest.list(container.getServiceClient()
				.getBaseURI(), container, prefix, useFlatBlobListing);
		container.getServiceClient().getCredentials().signRequest(request, -1L);
		this.processRequest(request);
		if (result.statusCode != HttpStatus.SC_OK) {
			throw new StorageInnerException("Couldn't list container blobs");
		}
		ListBlobsResponse response = new ListBlobsResponse(
				result.httpResponse.getEntity().getContent());
		return response.getBlobs(container.getServiceClient(), container);
			}
		};
		return storageOperation.executeTranslatingExceptions();
	}

	public Iterable<CloudBlobContainer> listContainers() throws Exception {
		return m_ServiceClient.listContainers();
	}

	public Iterable<CloudBlobContainer> listContainers(String prefix) throws Exception {
		return m_ServiceClient.listContainers(prefix);
	}

	public Iterable<CloudBlobContainer> listContainers(String prefix,
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
		HashMap<String, String[]> queryArguments = PathUtility.parseQueryString(completeUri.getQuery());
		StorageCredentialsSharedAccessSignature sasCredentials = SharedAccessSignatureHelper
				.parseQuery(queryArguments);
		if (sasCredentials == null)
			return;
		boolean serviceClientUsesSAS = serviceClient != null ? sasCredentials.equals(serviceClient.getCredentials()) : false;
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
	public void setMetadata(HashMap<String, String> metadata) {
		m_Metadata = metadata;
	}
	protected void setName(String containerName) {
		m_Name = containerName;
	}
	protected void setProperties(BlobContainerProperties properties) {
		m_Properties = properties;
	}
	protected void setUri(URI containerUri) throws NotImplementedException,
			NotImplementedException {
		throw new NotImplementedException();
	}
	public void uploadMetadata() throws StorageException, UnsupportedEncodingException, IOException {
		final CloudBlobContainer container = this;
		StorageOperation<Void> storageOperation = new StorageOperation<Void>() {
			public Void execute() throws Exception {
				HttpPut request = ContainerRequest
						.setMetadata(container.getTransformedAddress());
				ContainerRequest.addMetadata(request,
						container.m_Metadata);
				m_ServiceClient.getCredentials().signRequest(request, 0L);
				this.processRequest(request);
				if (result.statusCode != 200) {
					throw new StorageInnerException(
							"Couldn't upload container's metadata");
				}
				return null;
			}
		};
        storageOperation.executeTranslatingExceptions();
	}
	public void uploadPermissions(final BlobContainerPermissions permissions)
			throws StorageException,
			UnsupportedEncodingException, IOException {
		final CloudBlobContainer container = this;
		StorageOperation<Void> storageOperation = new StorageOperation<Void>() {
			public Void execute() throws Exception {
				HttpPut request = m_ContainerRequest.setAcl(
						container.m_ContainerOperationsUri,
						permissions.publicAccess);
				m_ServiceClient.getCredentials().signRequest(request, 0);
				this.processRequest(request);
				if (result.statusCode != HttpStatus.SC_OK) {
					throw new StorageInnerException(
							"Couldn't upload permissions to a blob's container");
				}
				return null;
			}
		};

        storageOperation.executeTranslatingExceptions();
	}
}
