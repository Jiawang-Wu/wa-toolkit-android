package com.windowsazure.samples.android.storageclient;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.EnumSet;

public final class CloudBlobDirectory implements IListBlobItem {

	private CloudBlobContainer m_Container;

	private CloudBlobDirectory m_Parent;

	private CloudBlobClient m_ServiceClient;

	private URI m_Uri;

	private String m_Prefix;

	protected CloudBlobDirectory(String s, CloudBlobClient serviceClient)
			throws NotImplementedException, URISyntaxException,
			StorageException {
		throw new NotImplementedException();
	}

	protected CloudBlobDirectory(URI uri,
			CloudBlobDirectory cloudblobdirectory,
			CloudBlobClient cloudblobclient) throws NotImplementedException,
			NotImplementedException {
		throw new NotImplementedException();
	}

	public CloudBlockBlob getBlockBlobReference(String s)
			throws NotImplementedException, URISyntaxException,
			StorageException {
		throw new NotImplementedException();
	}

	public CloudBlockBlob getBlockBlobReference(String s, String s1)
			throws NotImplementedException, URISyntaxException,
			StorageException {
		throw new NotImplementedException();
	}

	@Override
	public CloudBlobContainer getContainer() throws NotImplementedException,
			StorageException, URISyntaxException {
		throw new NotImplementedException();
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

	@Override
	public CloudBlobDirectory getParent() throws NotImplementedException,
			URISyntaxException, StorageException {
		throw new NotImplementedException();
	}

	protected String getPrefix() throws NotImplementedException,
			StorageException {
		throw new NotImplementedException();
	}

	public CloudBlobClient getServiceClient() throws NotImplementedException,
			NotImplementedException {
		throw new NotImplementedException();
	}

	public CloudBlobDirectory getSubDirectoryReference(String s)
			throws NotImplementedException, StorageException,
			URISyntaxException {
		throw new NotImplementedException();
	}
	@Override
	public URI getUri() throws NotImplementedException {
		throw new NotImplementedException();
	}
	public Iterable<CloudBlob> listBlobs() throws NotImplementedException,
			StorageException, URISyntaxException {
		throw new NotImplementedException();
	}
	public Iterable<CloudBlob> listBlobs(String s) throws NotImplementedException,
			URISyntaxException, StorageException {
		throw new NotImplementedException();
	}
	public Iterable<CloudBlob> listBlobs(String s, boolean flag, EnumSet<BlobListingDetails> enumset)
			throws NotImplementedException, URISyntaxException,
			StorageException {
		throw new NotImplementedException();
	}
}
