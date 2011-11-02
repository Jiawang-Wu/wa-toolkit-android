package com.windowsazure.samples.android.storageclient.tests;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Random;

import junit.framework.Assert;

import com.windowsazure.samples.android.storageclient.CloudBlob;
import com.windowsazure.samples.android.storageclient.CloudBlobContainer;
import com.windowsazure.samples.android.storageclient.CloudBlockBlob;
import com.windowsazure.samples.android.storageclient.NotImplementedException;
import com.windowsazure.samples.android.storageclient.StorageException;
import com.windowsazure.samples.android.storageclient.StorageInnerException;

public abstract class CloudBlockBlobTests<T extends CloudClientAccountProvider> extends CloudBlobClientBasedTest<T> 
{
	public void testCreateBlobInPrivateContainer() throws StorageInnerException, Exception
	{
		CloudBlobContainer container = this.createContainer("testcreateblobinprivatecontainer");
		String blobName = "someblob";
		CloudBlockBlob blob = container.getBlockBlobReference(blobName);
		ByteArrayInputStream contentsStream = new ByteArrayInputStream("".getBytes()); 
		blob.upload(contentsStream, 0);
		ArrayList<String> blobNames = this.getBlobNames(container.listBlobs());
		this.AssertHaveSameElements(blobNames, Arrays.asList(new String[]{blobName}));
	}

	public void testReadCreatedBlob() throws StorageInnerException, Exception
	{
		CloudBlobContainer container = this.createContainer("testreadcreatedblob");
		CloudBlockBlob blob = container.getBlockBlobReference("someblob");
		String sampleContent = "SampleContent";
		
		ByteArrayInputStream contentsStream = new ByteArrayInputStream(sampleContent.getBytes()); 
		blob.upload(contentsStream, sampleContent.length());

		ByteArrayOutputStream downloadedContentsStream = new ByteArrayOutputStream(); 
		blob.download(downloadedContentsStream);
		 
		Assert.assertEquals(sampleContent, downloadedContentsStream.toString());
	}
	
	public void testReadPropertiesOfCreatedBlob() throws StorageInnerException, Exception
	{
		CloudBlobContainer container = this.createContainer("testreadpropertiesofcreatedblob");
		CloudBlockBlob blob = container.getBlockBlobReference("someblob");
		String sampleContent = "SampleContent";
		String contentType = "text/plain";

		ByteArrayInputStream contentsStream = new ByteArrayInputStream(sampleContent.getBytes());
		blob.getProperties().contentType = "text/plain";
		blob.upload(contentsStream, sampleContent.length());

		CloudBlockBlob sameBlob = container.getBlockBlobReference("someblob");

		ByteArrayOutputStream downloadedContentsStream = new ByteArrayOutputStream(); 
		sameBlob.download(downloadedContentsStream);
		Assert.assertEquals(contentType, sameBlob.getProperties().contentType);
	}

	public void testDeletingBlobTwiceThrowsException() throws StorageInnerException, Exception
	{
		CloudBlobContainer container = this.createContainer("testdeletingblobtwicethrowsexception");
		final CloudBlob blob = this.createEmptyBlob(container, "someBlob");

		blob.delete();

		this.assertThrows(new RunnableWithExpectedException() {
			@Override
			public void run() throws Exception {
				blob.delete();
			}
		}, StorageException.class);
	}

	public void testBlobIsntListedAfterBeingDeleted() throws StorageInnerException, Exception
	{
		CloudBlobContainer container = this.createContainer("testblobisntlistedafterbeingdeleted");
		String blobName = "someBlob";
		final CloudBlob blob = this.createEmptyBlob(container, blobName);

		this.AssertHaveSameElements(this.getBlobNames(container.listBlobs()), Arrays.asList(new String[]{ blobName }));

		blob.delete();

		this.AssertHaveSameElements(this.getBlobNames(container.listBlobs()), Arrays.asList(new String[]{}));
	}

	public void testListingIsAccurateWhileCreatingAndDeletingSeveralBlobs()
			throws Exception {
		final CloudBlobContainer container = this.createContainer("testlistingisaccuratewhilecreatinganddeletingseveralblobs");
		final ArrayList<String> expectedBlobNames = new ArrayList<String>(); 
		class CreateAndDeleteBlobsHelper
		{
			void create(String blobName) throws Exception
			{
				CloudBlob blob = container.getBlockBlobReference(blobName);
				ByteArrayInputStream inputStream = new ByteArrayInputStream(this.content(blobName).getBytes());
				blob.upload(inputStream, inputStream.available());
				expectedBlobNames.add(blobName);
				this.assertListingGivesExpectedBlobs();
			}
			String content(String blobName)
			{
				return "Content for blob " + blobName; 
			}
			void delete(String blobName) throws Exception
			{
				CloudBlob blob = container.getBlockBlobReference(blobName);
				ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
				blob.download(outputStream);
				Assert.assertEquals(this.content(blobName), outputStream.toString());
				blob.delete();
				expectedBlobNames.remove(blobName);
				this.assertListingGivesExpectedBlobs();
			}
			void assertListingGivesExpectedBlobs() throws NotImplementedException, Exception
			{
				ArrayList<String> blobNames = thisTest.getBlobNames(container.listBlobs());
				thisTest.AssertHaveSameElements(expectedBlobNames, blobNames);
			}
		};
		
		CreateAndDeleteBlobsHelper helper = new CreateAndDeleteBlobsHelper();
		helper.create("0");
		helper.delete("0");
		helper.create("1");
		helper.create("2");
		helper.delete("1");
		helper.delete("2");
		helper.create("3");
		helper.create("4");
		helper.delete("4");
		helper.delete("3");
		helper.create("5");
		helper.create("6");
		helper.create("7");
		helper.create("8");
		helper.delete("5");
		helper.delete("7");
		helper.create("9");
		helper.create("10");
		helper.delete("6");
		helper.create("11");
		helper.delete("11");
		helper.delete("9");
		helper.delete("10");
		helper.delete("8");
		helper.create("12");
		helper.delete("12");
		helper.create("13");
		helper.create("14");
		helper.delete("13");
		helper.delete("14");
	}

	public void testWriteAndReadLargeBlob() throws StorageInnerException, Exception
	{
		int largeSize = 16 * 1024; // 16 KB
		byte sampleContent[] = new byte[largeSize];
		Random generator = new Random();
		generator.nextBytes(sampleContent);
		
		CloudBlobContainer container = this.createContainer("testwriteandreadlargeblob");
		CloudBlockBlob blob = container.getBlockBlobReference("someblob");
		
		ByteArrayInputStream contentsStream = new ByteArrayInputStream(sampleContent);
		blob.upload(contentsStream, sampleContent.length);

		ByteArrayOutputStream downloadedContentsStream = new ByteArrayOutputStream(); 
		blob.download(downloadedContentsStream);
		 
		Assert.assertTrue(Arrays.equals(sampleContent, downloadedContentsStream.toByteArray()));
	}

	public void testWriteBlobWithStreamOfUnknownLength() throws StorageInnerException, Exception
	{
		int largeSize = 512; // 512 Bytes
		byte sampleContent[] = new byte[largeSize];
		Random generator = new Random();
		generator.nextBytes(sampleContent);
		
		CloudBlobContainer container = this.createContainer("testwriteblobwithstreamofunknownlength");
		CloudBlockBlob blob = container.getBlockBlobReference("someblob");
		
		ByteArrayInputStream contentsStream = new ByteArrayInputStream(sampleContent);
		blob.upload(contentsStream, -1);

		ByteArrayOutputStream downloadedContentsStream = new ByteArrayOutputStream(); 
		blob.download(downloadedContentsStream);
		 
		Assert.assertTrue(Arrays.equals(sampleContent, downloadedContentsStream.toByteArray()));
	}

	protected void setUp()
	{
		super.setUp();
		thisTest = this;
	}
	private CloudBlockBlobTests<T> thisTest;
}
