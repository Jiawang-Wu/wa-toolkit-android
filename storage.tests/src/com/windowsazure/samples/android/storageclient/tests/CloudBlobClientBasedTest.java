package com.windowsazure.samples.android.storageclient.tests;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URISyntaxException;
import java.util.ArrayList;

import junit.framework.Assert;

import com.windowsazure.samples.android.storageclient.CloudBlob;
import com.windowsazure.samples.android.storageclient.CloudBlobClient;
import com.windowsazure.samples.android.storageclient.CloudBlobContainer;
import com.windowsazure.samples.android.storageclient.CloudBlockBlob;
import com.windowsazure.samples.android.storageclient.CloudQueue;
import com.windowsazure.samples.android.storageclient.NotImplementedException;
import com.windowsazure.samples.android.storageclient.StorageException;
import com.windowsazure.samples.android.storageclient.StorageInnerException;

public abstract class CloudBlobClientBasedTest<T extends CloudClientAccountProvider>
		extends TestCaseWithManagedResources {
	@Override
	protected void setUp() {
		try {
			super.setUp();
			T accountProvider = SuperClassTypeParameterCreator.create(this, 0);
			cloudBlobClient = accountProvider.getCloudBlobClient();
		} catch (Exception e) {
			e.printStackTrace();
			Assert.fail();
		}
	}

	protected CloudBlobContainer createQueue(String containerName)
			throws StorageException, NotImplementedException,
			URISyntaxException, UnsupportedEncodingException, IOException {
		final CloudBlobContainer container = new CloudBlobContainer(
				containerName, cloudBlobClient);
		container.create();
		this.addResourceCleaner(container, cleanerFor(container));
		return container;
	}

	protected void deleteContainer(CloudBlobContainer container)
			throws StorageException, NotImplementedException,
			URISyntaxException, UnsupportedEncodingException, IOException,
			StorageInnerException {
		container.delete();
		this.removeResourceCleaner(container);
	}

	protected ResourceCleaner cleanerFor(final CloudBlobContainer container) {
		return new ResourceCleaner() {
			public void clean() throws NotImplementedException,
					StorageException, UnsupportedEncodingException, IOException {
				try {
					container.delete();
				} catch (Exception e) {
				}
			}
		};
	}

	protected ResourceCleaner cleanerFor(final CloudQueue queue) {
		return new ResourceCleaner() {
			public void clean() throws NotImplementedException,
					StorageException, UnsupportedEncodingException, IOException {
				try {
					queue.delete();
				} catch (Exception e) {
				}
			}
		};
	}

	protected CloudBlob createEmptyBlob(CloudBlobContainer container,
			String blobName) throws UnsupportedEncodingException,
			NotImplementedException, URISyntaxException, StorageException,
			IOException {
		CloudBlockBlob blob = container.getBlockBlobReference(blobName);
		blob.upload(new ByteArrayInputStream("".getBytes()), 0);
		return blob;
	}

	protected ArrayList<String> getContainerNames(
			Iterable<CloudBlobContainer> containers)
			throws NotImplementedException {
		ArrayList<String> names = new ArrayList<String>();
		for (CloudBlobContainer container : containers) {
			names.add(container.getName());
		}
		return names;
	}

	protected ArrayList<String> getBlobNames(Iterable<CloudBlob> blobs)
			throws NotImplementedException, URISyntaxException {
		ArrayList<String> names = new ArrayList<String>();
		for (CloudBlob blob : blobs) {
			names.add(blob.getName());
		}
		return names;
	}

	protected CloudBlobClient cloudBlobClient;
}
