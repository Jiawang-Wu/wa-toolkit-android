package com.windowsazure.samples.android.storageclient;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;

public final class CloudPageBlob extends CloudBlob {

	public CloudPageBlob(CloudPageBlob serviceClient)
			throws NotImplementedException, StorageException {
		throw new NotImplementedException();
	}

	public CloudPageBlob(URI uri, CloudBlobClient serviceClient)
			throws NotImplementedException, StorageException {
		throw new NotImplementedException();
	}

	public CloudPageBlob(URI uri, CloudBlobClient serviceClient,
			CloudBlobContainer cloudblobcontainer)
			throws NotImplementedException, StorageException {
		throw new NotImplementedException();
	}

	public CloudPageBlob(URI uri, String snapshotId, CloudBlobClient serviceClient)
			throws NotImplementedException, StorageException {
		throw new NotImplementedException();
	}

	public void clearPages(long startOffset, long length) throws NotImplementedException,
			StorageException, IOException {
		throw new NotImplementedException();
	}

	public void clearPages(long startOffset, long length, String s)
			throws NotImplementedException, StorageException, IOException {
		throw new NotImplementedException();
	}

	public void create(long length) throws NotImplementedException,
			StorageException, IllegalArgumentException {
		throw new NotImplementedException();
	}

	public void create(long length, final String leaseID)
			throws NotImplementedException, StorageException,
			IllegalArgumentException {
		throw new NotImplementedException();
	}

	public BlobOutputStream openOutputStream(long length)
			throws NotImplementedException, StorageException {
		throw new NotImplementedException();
	}

	public BlobOutputStream openOutputStream(long length, String leaseID)
			throws NotImplementedException, StorageException {
		throw new NotImplementedException();
	}

	@Override
	public void upload(InputStream inputStream, long length)
			throws NotImplementedException, StorageException, IOException {
		throw new NotImplementedException();
	}

	@Override
	public void upload(InputStream inputStream, long length, String s)
			throws NotImplementedException, StorageException, IOException {
		throw new NotImplementedException();
	}

	public void uploadPages(InputStream inputstream, long startOffset, long length)
			throws NotImplementedException, StorageException, IOException,
			IllegalArgumentException {
		throw new NotImplementedException();
	}

	public void uploadPages(InputStream inputstream, long startOffset, long length, String s)
			throws NotImplementedException, StorageException, IOException,
			IllegalArgumentException {
		throw new NotImplementedException();
	}
}
