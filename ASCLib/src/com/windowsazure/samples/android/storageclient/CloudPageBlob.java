package com.windowsazure.samples.android.storageclient;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.util.ArrayList;

public final class CloudPageBlob extends CloudBlob {

	public CloudPageBlob(CloudPageBlob cloudpageblob)
			throws NotImplementedException, StorageException {
		throw new NotImplementedException();
	}

	public CloudPageBlob(URI uri, CloudBlobClient cloudblobclient)
			throws NotImplementedException, StorageException {
		throw new NotImplementedException();
	}

	public CloudPageBlob(URI uri, CloudBlobClient cloudblobclient,
			CloudBlobContainer cloudblobcontainer)
			throws NotImplementedException, StorageException {
		throw new NotImplementedException();
	}

	public CloudPageBlob(URI uri, String s, CloudBlobClient cloudblobclient)
			throws NotImplementedException, StorageException {
		throw new NotImplementedException();
	}

	public void clearPages(long l, long l1) throws NotImplementedException,
			StorageException, IOException {
		throw new NotImplementedException();
	}

	public void clearPages(long l, long l1, String s)
			throws NotImplementedException, StorageException, IOException {
		throw new NotImplementedException();
	}

	public void create(long l) throws NotImplementedException,
			StorageException, IllegalArgumentException {
		throw new NotImplementedException();
	}

	public void create(long l, final String leaseID)
			throws NotImplementedException, StorageException,
			IllegalArgumentException {
		throw new NotImplementedException();
	}

	public ArrayList downloadPageRanges() throws NotImplementedException,
			StorageException {
		throw new NotImplementedException();
	}

	public BlobOutputStream openOutputStream(long l)
			throws NotImplementedException, StorageException {
		throw new NotImplementedException();
	}

	public BlobOutputStream openOutputStream(long l, String s)
			throws NotImplementedException, StorageException {
		throw new NotImplementedException();
	}

	private void putPagesInternal(final PageProperties pageProperties,
			byte abyte0[], final long length, final String leaseID,
			final String md5) throws NotImplementedException, StorageException {
		throw new NotImplementedException();
	}

	@Override
	public void upload(InputStream inputstream, long l)
			throws NotImplementedException, StorageException, IOException {
		throw new NotImplementedException();
	}

	@Override
	public void upload(InputStream inputstream, long l, String s)
			throws NotImplementedException, StorageException, IOException {
		throw new NotImplementedException();
	}

	public void uploadPages(InputStream inputstream, long l, long l1)
			throws NotImplementedException, StorageException, IOException,
			IllegalArgumentException {
		throw new NotImplementedException();
	}

	public void uploadPages(InputStream inputstream, long l, long l1, String s)
			throws NotImplementedException, StorageException, IOException,
			IllegalArgumentException {
		throw new NotImplementedException();

	}
}
