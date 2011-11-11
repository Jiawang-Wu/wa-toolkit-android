package com.windowsazure.samples.android.storageclient.tests;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import junit.framework.Assert;

import com.windowsazure.samples.android.storageclient.BlobContainerPermissions;
import com.windowsazure.samples.android.storageclient.BlobContainerPublicAccessType;
import com.windowsazure.samples.android.storageclient.CloudBlob;
import com.windowsazure.samples.android.storageclient.CloudBlobClient;
import com.windowsazure.samples.android.storageclient.CloudBlobContainer;
import com.windowsazure.samples.android.storageclient.StorageCredentialsSharedAccessSignature;
import com.windowsazure.samples.android.storageclient.StorageException;

public abstract class CloudBlobContainerUsingSASServiceTests<T extends WAZServiceAccountProvider>
		extends CloudBlobContainerTests<T> {
	@Override
	protected void setUp() {
		try {
			super.setUp();
			T accountProvider = SuperClassTypeParameterCreator.create(this, 0);
			otherCloudBlobClient = accountProvider
					.getCloudBlobClientWithDifferentAccount();
		} catch (Exception e) {
			e.printStackTrace();
			Assert.fail();
		}
	}

	public void testCreateExistingContainerReturnsAlwaysTrue() throws Exception {
		/** The SAS service always returns OK/200 **/
		final CloudBlobContainer container = new CloudBlobContainer(
				"testcreateexistingcontainerreturnsfalse", cloudBlobClient);
		this.addResourceCleaner(container, cleanerFor(container));
		Assert.assertTrue(container.createIfNotExist());
		Assert.assertTrue(container.createIfNotExist());
	}

	public void testAccessingContainerPropertiesThrowsException() throws Exception
	{
		final CloudBlobContainer container = this.createContainer("testaccessingcontainerpropertiesthrowsexception");
		
		this.assertThrows(new RunnableWithExpectedException() {
			
			@Override
			public void run() throws Exception {
				container.downloadAttributes();
			}
		}, StorageException.class);
	}
	
	public void testExistsMethodThrowsException() throws Exception
	{
		final CloudBlobContainer container = this.createContainer("testexistsmethodthrowsexception");
		
		this.assertThrows(new RunnableWithExpectedException() {
			@Override
			public void run() throws Exception {
				container.exists();
			}
		}, StorageException.class);
	}

	public void testListedBlobsInPrivateContainerHaveSASCredentialsButSameClient()
			throws Exception {
		CloudBlobContainer container = this
				.createContainer("testlistedblobshavesascredentials");
		this.createEmptyBlob(container, "blob1");
		this.createEmptyBlob(container, "blob2");

		ArrayList<CloudBlob> blobs = this.toList(container.listBlobs());
		Assert.assertEquals(2, blobs.size());
		for (CloudBlob blob : blobs) {
			Assert.assertTrue(blob.getCredentials() instanceof StorageCredentialsSharedAccessSignature);
			Assert.assertSame(blob.getServiceClient(),
					container.getServiceClient());
			Assert.assertSame(blob.getContainer(), container);
		}
	}

	public void testCreateContainerCreatedByOtherAccountThrowsException()
			throws Exception {
		final CloudBlobContainer container = this.createContainer("testcreatecontainercreatedbyotheraccountthrowsexception");
		final CloudBlobContainer sameContainer = new CloudBlobContainer(
				container.getName(), otherCloudBlobClient);
		this.assertThrows(new RunnableWithExpectedException() {
			@Override
			public void run() throws Exception {
				sameContainer.create();
			}
		}, StorageException.class);
	}

	/** Invalid attempts to delete containers - START **/
	public void testDeleteContainerWithoutPermissionsDoesntThrowException()
			throws Exception {
		String containerName = "testdeletecontainerwithoutpermissionsthrowsexception";
		final CloudBlobContainer container = this
				.createContainer(containerName);
		final CloudBlobContainer sameContainer = new CloudBlobContainer(
				containerName, otherCloudBlobClient);
		this.assertThrows(new RunnableWithExpectedException() {
			@Override
			public void run() throws Exception {
				sameContainer.delete();
			}
		}, StorageException.class);

		List<CloudBlobContainer> containers = this.toList(this.cloudBlobClient
				.listContainers());
		Assert.assertEquals(containers.size(), 1);
		container.delete();
		Assert.assertFalse(this.cloudBlobClient.listContainers().iterator()
				.hasNext());
	}

	/** Invalid attempts to delete containers - END **/
	public void testUsersCanSeeOtherUsersContainers() throws Exception {
		String privateContainerName = "testuserscanseeotheruserscontainers-private";
		String publicContainerName = "testuserscanseeotheruserscontainers-public";
		this.createContainer(privateContainerName);
		final CloudBlobContainer publicContainer = this
				.createContainer(publicContainerName);

		BlobContainerPermissions permissions = new BlobContainerPermissions();
		permissions.publicAccess = BlobContainerPublicAccessType.CONTAINER;
		publicContainer.uploadPermissions(permissions);

		this.AssertHaveSameElements(
				this.getContainerNames(otherCloudBlobClient.listContainers()),
				Arrays.asList(new String[] { privateContainerName,
						publicContainerName }));
	}

	protected CloudBlobClient otherCloudBlobClient;
}
