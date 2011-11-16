package com.windowsazure.samples.android.storageclient.tests;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URISyntaxException;
import java.util.Calendar;
import java.util.HashMap;
import java.util.concurrent.Callable;

import junit.framework.Assert;

import com.windowsazure.samples.android.storageclient.BlobContainerPermissions;
import com.windowsazure.samples.android.storageclient.BlobContainerProperties;
import com.windowsazure.samples.android.storageclient.BlobContainerPublicAccessType;
import com.windowsazure.samples.android.storageclient.CloudBlobContainer;
import com.windowsazure.samples.android.storageclient.NotImplementedException;
import com.windowsazure.samples.android.storageclient.StorageException;

public class CloudBlobContainerUsingAccountAndKeyTests extends
		CloudBlobContainerTests<CloudStorageAccountProvider> {
	public void testContainerProperties() throws UnsupportedEncodingException, NotImplementedException, StorageException, IOException, URISyntaxException
	{
		CloudBlobContainer container1 = this.createQueue("testcontainerproperties-1");
		CloudBlobContainer container2 = this.createQueue("testcontainerproperties-2");
		
		container1.downloadAttributes();
		BlobContainerProperties properties = container1.getProperties();
		Assert.assertNotNull(properties.eTag);

		Calendar calendar = Calendar.getInstance();
		long difference = calendar.getTime().getTime() - properties.lastModified.getTime();
		Assert.assertTrue(difference <= 10 * 1000); //10 seconds difference max
		
		container2.downloadAttributes();
		Assert.assertFalse(properties.eTag.equals(container2.getProperties().eTag));
	}
	
	public void testCreateExistingContainerReturnsFalse() throws Exception {
		final CloudBlobContainer container = new CloudBlobContainer(
				"testcreateexistingcontainerreturnsfalse", cloudBlobClient);
		this.addResourceCleaner(container, cleanerFor(container));
		Assert.assertTrue(container.createIfNotExist());
		Assert.assertFalse(container.createIfNotExist());
	}

	public void testExists() throws Exception {
		final CloudBlobContainer container = new CloudBlobContainer(
				"testexists", cloudBlobClient);
		this.addResourceCleaner(container, cleanerFor(container));
		Assert.assertFalse(container.exists());
		container.create();
		Assert.assertTrue(container.exists());
	}

	public void testChangingContainerPermssions() throws Exception
	{
		final CloudBlobContainer container = this.createQueue("testchangingcontainerpermssions");
		final CloudBlobContainer sameContainer = new CloudBlobContainer(container.getName(), cloudBlobClient);
		BlobContainerPermissions permissions = new BlobContainerPermissions();

		Assert.assertEquals(container.downloadPermissions().publicAccess, BlobContainerPublicAccessType.OFF);
		Assert.assertEquals(sameContainer.downloadPermissions().publicAccess, BlobContainerPublicAccessType.OFF);

		permissions.publicAccess = BlobContainerPublicAccessType.CONTAINER;
		container.uploadPermissions(permissions);

		this.assertEventuallyTrue( new Callable<Boolean>()
		{
			public Boolean call() throws Exception
			{
				return container.downloadPermissions().publicAccess == BlobContainerPublicAccessType.CONTAINER;
			}
		}, 10);
		this.assertEventuallyTrue( new Callable<Boolean>()
		{
			public Boolean call() throws Exception
			{
				return sameContainer.downloadPermissions().publicAccess == BlobContainerPublicAccessType.CONTAINER;
			}
		}, 10);

		permissions.publicAccess = BlobContainerPublicAccessType.BLOB;
		container.uploadPermissions(permissions);
		this.assertEventuallyTrue( new Callable<Boolean>()
		{
			public Boolean call() throws Exception
			{
				return container.downloadPermissions().publicAccess == BlobContainerPublicAccessType.BLOB;
			}
		}, 10);
		this.assertEventuallyTrue( new Callable<Boolean>()
		{
			public Boolean call() throws Exception
			{
				return sameContainer.downloadPermissions().publicAccess == BlobContainerPublicAccessType.BLOB;
			}
		}, 10);

		permissions.publicAccess = BlobContainerPublicAccessType.OFF;
		container.uploadPermissions(permissions);
		this.assertEventuallyTrue( new Callable<Boolean>()
		{
			public Boolean call() throws Exception
			{
				return container.downloadPermissions().publicAccess == BlobContainerPublicAccessType.OFF;
			}
		}, 10);
		this.assertEventuallyTrue( new Callable<Boolean>()
		{
			public Boolean call() throws Exception
			{
				return sameContainer.downloadPermissions().publicAccess == BlobContainerPublicAccessType.OFF;
			}
		}, 10);
	}
	
	public void testUploadingAndDownloadingMetadataWorksAsExpected()
			throws Exception {
		CloudBlobContainer container = this
				.createQueue("testuploadinganddownloadingmetadataworksasexpected");
		CloudBlobContainer sameContainer = new CloudBlobContainer(
				container.getName(), cloudBlobClient);

		this.AssertHashMapsAreEquivalent(container.getMetadata(),
				new HashMap<String, String>());

		HashMap<String, String> metadata = container.getMetadata();
		metadata.put("key1", "value1");
		container.uploadMetadata();
		sameContainer.downloadAttributes();
		this.AssertHashMapsAreEquivalent(container.getMetadata(),
				sameContainer.getMetadata());

		metadata.clear();
		metadata.put("key3", "otherValue&-/\\@");
		metadata.put("key2", "345654");
		container.uploadMetadata();
		sameContainer.downloadAttributes();
		this.AssertHashMapsAreEquivalent(container.getMetadata(),
				sameContainer.getMetadata());

		metadata.clear();
		container.uploadMetadata();
		sameContainer.downloadAttributes();
		this.AssertHashMapsAreEquivalent(sameContainer.getMetadata(),
				new HashMap<String, String>());
	}
	
	public void testDeleteContainerTwiceShouldNotThrowException()
			throws Exception {
		final CloudBlobContainer container = this
				.createQueue("testdeletecontainertwicethrowsexception");
		this.deleteContainer(container);

		// Windows Azure works this way. Two deletes inside the 30 seconds
		// margin don't throw an exception.
		container.delete();
	}
}
