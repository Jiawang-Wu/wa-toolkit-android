package com.windowsazure.samples.android.storageclient.tests;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Random;
import junit.framework.Assert;

import com.windowsazure.samples.android.storageclient.BlobProperties;
import com.windowsazure.samples.android.storageclient.BlobType;
import com.windowsazure.samples.android.storageclient.BlockEntry;
import com.windowsazure.samples.android.storageclient.BlockSearchMode;
import com.windowsazure.samples.android.storageclient.CloudBlob;
import com.windowsazure.samples.android.storageclient.CloudBlobContainer;
import com.windowsazure.samples.android.storageclient.CloudBlockBlob;
import com.windowsazure.samples.android.storageclient.LeaseStatus;
import com.windowsazure.samples.android.storageclient.NotImplementedException;
import com.windowsazure.samples.android.storageclient.StorageException;
import com.windowsazure.samples.android.storageclient.StorageInnerException;
import com.windowsazure.samples.android.storageclient.Utility;

public abstract class CloudBlockBlobTests<T extends CloudClientAccountProvider>
		extends CloudBlobClientBasedTest<T> {
	public void testCreateBlobInPrivateContainer()
			throws StorageInnerException, Exception {
		CloudBlobContainer container = this
				.createQueue("testcreateblobinprivatecontainer");
		String blobName = "someblob";
		CloudBlockBlob blob = container.getBlockBlobReference(blobName);
		ByteArrayInputStream contentsStream = new ByteArrayInputStream(
				"".getBytes());
		blob.upload(contentsStream, 0);
		ArrayList<String> blobNames = this.getBlobNames(container.listBlobs());
		this.assertHaveSameElements(blobNames,
				Arrays.asList(new String[] { blobName }));
	}

	public void testReadCreatedBlob() throws StorageInnerException, Exception {
		CloudBlobContainer container = this
				.createQueue("testreadcreatedblob");
		CloudBlockBlob blob = container.getBlockBlobReference("someblob");
		String sampleContent = "SampleContent";

		ByteArrayInputStream contentsStream = new ByteArrayInputStream(
				sampleContent.getBytes());
		blob.upload(contentsStream, sampleContent.length());

		ByteArrayOutputStream downloadedContentsStream = new ByteArrayOutputStream();
		blob.download(downloadedContentsStream);

		Assert.assertEquals(sampleContent, downloadedContentsStream.toString());
	}

	public void testReadCreatedBlobUsingStream() throws StorageInnerException, Exception {
		CloudBlobContainer container = this.createQueue("testreadcreatedblobusingstream");
		CloudBlockBlob blob = container.getBlockBlobReference("someblob");
		String sampleContent = "SampleContent";

		ByteArrayInputStream contentsStream = new ByteArrayInputStream(
				sampleContent.getBytes());
		blob.upload(contentsStream, sampleContent.length());

		String content = Utility.readStringFromStream(blob.openInputStream());
		Assert.assertEquals(sampleContent, content);

		ByteArrayOutputStream downloadedContentsStream = new ByteArrayOutputStream();
		blob.download(downloadedContentsStream);
		Assert.assertEquals(downloadedContentsStream.toString(), content);
	}

	public void testWriteBlobUsingStreams() throws StorageInnerException, Exception {
		CloudBlobContainer container = this.createQueue("testwriteblobusingstreams");
		CloudBlockBlob blob = container.getBlockBlobReference("someblob");
		String sampleContent = "SampleContent";

		OutputStream outputStream = blob.openOutputStream();
		outputStream.write(sampleContent.getBytes(), 0, sampleContent.length());
		outputStream.close();

		ByteArrayOutputStream downloadedContentsStream = new ByteArrayOutputStream();
		blob.download(downloadedContentsStream);
		Assert.assertEquals(sampleContent, downloadedContentsStream.toString());
	}

	public void testSettingAndGettingBlobProperties() throws StorageInnerException, Exception {
		CloudBlobContainer container = this.createQueue("testsettingandgettingblobproperties");
		CloudBlob blob = this.createEmptyBlob(container, "someblob");
		CloudBlob sameBlob = container.getBlockBlobReference(blob.getName());
		
		sameBlob.downloadAttributes();
		BlobProperties sameBlobProperties = sameBlob.getProperties();

		Assert.assertEquals(BlobType.BLOCK_BLOB, sameBlobProperties.getBlobType());
		Assert.assertEquals(null, sameBlobProperties.cacheControl);
		Assert.assertEquals("", sameBlobProperties.contentEncoding);
		Assert.assertEquals("", sameBlobProperties.contentLanguage);
		Assert.assertEquals(null, sameBlobProperties.contentMD5);
		Assert.assertEquals("application/octet-stream", sameBlobProperties.contentType);
		Assert.assertNotNull(sameBlobProperties.eTag);
		long secondsAlive = Calendar.getInstance(Locale.US).getTimeInMillis() - sameBlobProperties.lastModified.getTime();
		Assert.assertTrue(secondsAlive <= 10 * 1000); // It was created at most 10 seconds ago
		Assert.assertEquals(LeaseStatus.UNLOCKED, sameBlobProperties.leaseStatus);
		Assert.assertEquals(0, sameBlobProperties.length);

	
		String contentEncoding = "someEncoding";
		String contentLanguage = "english";
		String contentType = "customType";

		BlobProperties blobProperties = blob.getProperties();
		
		blobProperties.contentType = contentType;
		blobProperties.contentEncoding = contentEncoding;
		blobProperties.contentLanguage = contentLanguage;
		blob.uploadProperties();
		
		sameBlob.downloadAttributes();
		Assert.assertEquals(contentType, sameBlobProperties.contentType);
		Assert.assertEquals(contentEncoding, sameBlobProperties.contentEncoding);
		Assert.assertEquals(contentLanguage, sameBlobProperties.contentLanguage);
	}

	public void testReadPropertiesOfCreatedBlob() throws StorageInnerException,
			Exception {
		CloudBlobContainer container = this
				.createQueue("testreadpropertiesofcreatedblob");
		CloudBlockBlob blob = container.getBlockBlobReference("someblob");
		String sampleContent = "SampleContent";
		String contentType = "text/plain";

		ByteArrayInputStream contentsStream = new ByteArrayInputStream(
				sampleContent.getBytes());
		blob.getProperties().contentType = "text/plain";
		blob.upload(contentsStream, sampleContent.length());

		CloudBlockBlob sameBlob = container.getBlockBlobReference("someblob");

		ByteArrayOutputStream downloadedContentsStream = new ByteArrayOutputStream();
		sameBlob.download(downloadedContentsStream);
		Assert.assertEquals(contentType, sameBlob.getProperties().contentType);
	}

	public void testDeletingBlobTwiceThrowsException()
			throws StorageInnerException, Exception {
		CloudBlobContainer container = this
				.createQueue("testdeletingblobtwicethrowsexception");
		final CloudBlob blob = this.createEmptyBlob(container, "someBlob");

		blob.delete();

		this.assertThrows(new RunnableWithExpectedException() {
			public void run() throws Exception {
				blob.delete();
			}
		}, StorageException.class);
	}

	public void testBlobIsntListedAfterBeingDeleted()
			throws StorageInnerException, Exception {
		CloudBlobContainer container = this
				.createQueue("testblobisntlistedafterbeingdeleted");
		String blobName = "someBlob";
		final CloudBlob blob = this.createEmptyBlob(container, blobName);

		this.assertHaveSameElements(this.getBlobNames(container.listBlobs()),
				Arrays.asList(new String[] { blobName }));

		blob.delete();

		this.assertHaveSameElements(this.getBlobNames(container.listBlobs()),
				Arrays.asList(new String[] {}));
	}

	public void testListingIsAccurateWhileCreatingAndDeletingSeveralBlobs()
			throws Exception {
		final CloudBlobContainer container = this
				.createQueue("testlistingisaccuratewhilecreatinganddeletingseveralblobs");
		final ArrayList<String> expectedBlobNames = new ArrayList<String>();
		class CreateAndDeleteBlobsHelper {
			void upload(String blobName) throws Exception {
				CloudBlob blob = container.getBlockBlobReference(blobName);
				ByteArrayInputStream inputStream = new ByteArrayInputStream(
						this.contentFor(blobName).getBytes());
				blob.upload(inputStream, inputStream.available());
				expectedBlobNames.add(blobName);
				this.assertListingGivesExpectedBlobs();
			}

			String contentFor(String blobName) {
				return "Content for blob " + blobName;
			}

			void commit(String blobName) throws Exception {
				CloudBlob blob = container.getBlockBlobReference(blobName);
				ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
				blob.download(outputStream);
				Assert.assertEquals(this.contentFor(blobName),
						outputStream.toString());
				blob.delete();
				expectedBlobNames.remove(blobName);
				this.assertListingGivesExpectedBlobs();
			}

			void assertListingGivesExpectedBlobs()
					throws NotImplementedException, Exception {
				ArrayList<String> blobNames = thisTest.getBlobNames(container
						.listBlobs());
				thisTest.assertHaveSameElements(expectedBlobNames, blobNames);
			}
		}
		;

		CreateAndDeleteBlobsHelper helper = new CreateAndDeleteBlobsHelper();
		helper.upload("0");
		helper.commit("0");
		helper.upload("1");
		helper.upload("2");
		helper.commit("1");
		helper.commit("2");
		helper.upload("3");
		helper.upload("4");
		helper.commit("4");
		helper.commit("3");
		helper.upload("5");
		helper.upload("6");
		helper.upload("7");
		helper.upload("8");
		helper.commit("5");
		helper.commit("7");
		helper.upload("9");
		helper.upload("10");
		helper.commit("6");
		helper.upload("11");
		helper.commit("11");
		helper.commit("9");
		helper.commit("10");
		helper.commit("8");
		helper.upload("12");
		helper.commit("12");
		helper.upload("13");
		helper.upload("14");
		helper.commit("13");
		helper.commit("14");
	}

	public void testWriteAndReadLargeBlob() throws StorageInnerException,
			Exception {
		int largeSize = 16 * 1024; // 16 KB
		byte sampleContent[] = new byte[largeSize];
		Random generator = new Random();
		generator.nextBytes(sampleContent);

		CloudBlobContainer container = this
				.createQueue("testwriteandreadlargeblob");
		CloudBlockBlob blob = container.getBlockBlobReference("someblob");

		ByteArrayInputStream contentsStream = new ByteArrayInputStream(
				sampleContent);
		blob.upload(contentsStream, sampleContent.length);

		ByteArrayOutputStream downloadedContentsStream = new ByteArrayOutputStream();
		blob.download(downloadedContentsStream);

		Assert.assertTrue(Arrays.equals(sampleContent,
				downloadedContentsStream.toByteArray()));
	}

	public void testWriteBlobWithStreamOfUnknownLength()
			throws StorageInnerException, Exception {
		int largeSize = 512; // 512 Bytes
		byte sampleContent[] = new byte[largeSize];
		Random generator = new Random();
		generator.nextBytes(sampleContent);

		CloudBlobContainer container = this
				.createQueue("testwriteblobwithstreamofunknownlength");
		CloudBlockBlob blob = container.getBlockBlobReference("someblob");

		ByteArrayInputStream contentsStream = new ByteArrayInputStream(
				sampleContent);
		blob.upload(contentsStream, -1);

		ByteArrayOutputStream downloadedContentsStream = new ByteArrayOutputStream();
		blob.download(downloadedContentsStream);

		Assert.assertTrue(Arrays.equals(sampleContent,
				downloadedContentsStream.toByteArray()));
	}

	public void testCanCreateBlockBlobsUsingSingleBlock()
			throws StorageInnerException, Exception {
		CloudBlobContainer container = this
				.createQueue("testcancreateblockblobsusingsingleblock");
		final CloudBlockBlob blob = container
				.getBlockBlobReference("singleBlockBlob");
		String sampleContent = "sampleContent";
		String encodedBlockId = CloudBlockBlob.encodedBlockId("block1");

		blob.uploadBlock(encodedBlockId, new ByteArrayInputStream(
				sampleContent.getBytes()), sampleContent.length());

		this.assertThrows(new RunnableWithExpectedException() {
			public void run() throws Exception {
				thisTest.contentsOf(blob);  // We can't download the blob,
											// because it's not committed yet.
			}
		}, StorageException.class);

		List<BlockEntry> blockEntries = Arrays
				.asList(new BlockEntry[] { new BlockEntry(encodedBlockId,
						BlockSearchMode.LATEST) });
		blob.commitBlockList(blockEntries);
		Assert.assertEquals(sampleContent, this.contentsOf(blob));
	}

	public void testUpdatingBlobByBlocksWorksAsExpected()
			throws StorageInnerException, Exception {
		final CloudBlobContainer container = this
				.createQueue("testupdatingblobbyblocksworksasexpected");
		final CloudBlockBlob blob = container
				.getBlockBlobReference("someBlockBlob");

		// blocks ids must have the same length
		final String[] blockIds = new String[] { "block1", "block2", "block3",
				"block4", "block5", "block6" };
		final int[] blockLengths = new int[] { 10, 1, 20, 230, 1, 1024 };
		final BlockSearchMode[] blockSearchModes = new BlockSearchMode[] {
				BlockSearchMode.LATEST, BlockSearchMode.LATEST,
				BlockSearchMode.LATEST, BlockSearchMode.LATEST,
				BlockSearchMode.LATEST, BlockSearchMode.LATEST };

		Assert.assertEquals(blockIds.length, blockLengths.length);
		Assert.assertEquals(blockIds.length, blockSearchModes.length);

		class UploadAndCommitBlocksHelper {
			ArrayList<String> encodedBlockIds = new ArrayList<String>();
			byte[][] blockContents = new byte[blockIds.length][];
			int[] commitedBlobIndexes;

			UploadAndCommitBlocksHelper() {
				Random random = new Random();
				for (int i = 0; i < blockContents.length; ++i) {
					byte[] contents = new byte[blockLengths[i]];
					random.nextBytes(contents);
					blockContents[i] = contents;
				}
				for (String blockId : blockIds) {
					encodedBlockIds.add(CloudBlockBlob.encodedBlockId(blockId));
				}
			}

			void upload(int blobIndex) throws Exception {
				blob.uploadBlock(encodedBlockIds.get(blobIndex), new ByteArrayInputStream(blockContents[blobIndex]),
						blockContents[blobIndex].length);

				this.assertExpectedBlobIsDownloaded();
			}

			void commit(int[] blobIndexes) throws Exception {
				ArrayList<BlockEntry> blockEntries = new ArrayList<BlockEntry>();
				for (int blobIndex : blobIndexes) {
					blockEntries.add(new BlockEntry(encodedBlockIds
							.get(blobIndex), blockSearchModes[blobIndex]));
				}
				blob.commitBlockList(blockEntries);
				commitedBlobIndexes = blobIndexes;
				this.assertExpectedBlobIsDownloaded();
			}

			void assertExpectedBlobIsDownloaded()
					throws NotImplementedException, Exception {
				if (commitedBlobIndexes != null) {
					ByteArrayOutputStream downloadedContentsStream = new ByteArrayOutputStream();
					blob.download(downloadedContentsStream);
					downloadedContentsStream.toByteArray();

					ByteArrayOutputStream expectedStream = new ByteArrayOutputStream();
					for (int index : commitedBlobIndexes) {
						expectedStream.write(blockContents[index]);
					}

					Assert.assertTrue(Arrays.equals(
							expectedStream.toByteArray(),
							downloadedContentsStream.toByteArray()));
				}
			}
		}
		;

		UploadAndCommitBlocksHelper helper = new UploadAndCommitBlocksHelper();
		helper.upload(3);
		helper.upload(1);
		helper.upload(4);
		helper.commit(new int[] { 3, 1, 4 });
		helper.upload(0);
		helper.commit(new int[] { 0, 1 });
		helper.upload(3);
		helper.commit(new int[] { 3 });
		helper.upload(1);
		helper.commit(new int[] { 3, 1 });
		helper.commit(new int[] { 1, 3 });
		helper.upload(0);
		helper.upload(1);
		helper.commit(new int[] { 3, 1 });
		helper.upload(0);
		helper.commit(new int[] { 0, 3 });
		helper.commit(new int[] { 3, 3 });
		helper.upload(1);
		helper.commit(new int[] { 3, 3, 1 });
		helper.upload(0);
		helper.commit(new int[] { 3, 1, 0 });
		helper.upload(5);
		helper.upload(1);
		helper.commit(new int[] { 3, 1, 5 });
		helper.upload(4);
		helper.commit(new int[] { 3, 1, 4 });
		helper.commit(new int[] { 3, 1 });
		helper.upload(0);
		helper.commit(new int[] { 0 });
	}

	public void testUploadingAndDownloadingBlobMetadataWorksAsExpected()
			throws Exception {
		CloudBlobContainer container = this
				.createQueue("testuploadinganddownloadingblobmetadataworksasexpected");
		String blobName = "someBlob";
		CloudBlob blob = this.createEmptyBlob(container, blobName);
		CloudBlob sameBlob = container.getBlockBlobReference("someBlob");

		Assert.assertFalse(blob == sameBlob);

		this.AssertHashMapsAreEquivalent(blob.getMetadata(),
				new HashMap<String, String>());

		HashMap<String, String> metadata = blob.getMetadata();
		metadata.put("key1", "value1");
		blob.uploadMetadata();
		sameBlob.downloadAttributes();
		this.AssertHashMapsAreEquivalent(blob.getMetadata(),
				sameBlob.getMetadata());

		metadata.clear();
		metadata.put("key3", "otherValue&-/\\@");
		metadata.put("key2", "345654");
		blob.uploadMetadata();
		sameBlob.downloadAttributes();
		this.AssertHashMapsAreEquivalent(blob.getMetadata(),
				sameBlob.getMetadata());

		metadata.clear();
		blob.uploadMetadata();
		sameBlob.downloadAttributes();
		this.AssertHashMapsAreEquivalent(sameBlob.getMetadata(),
				new HashMap<String, String>());
	}

	public void testUploadingEmptyOrNullValueForBlobMetadataThrowsException()
			throws Exception {
		final CloudBlobContainer container = this
				.createQueue("testuploadingemptyornullvalueforblobmetadatathrowsexception");

		container.getMetadata().put("key2", "");
		this.assertThrows(new RunnableWithExpectedException() {
			public void run() throws Exception {
				container.uploadMetadata();
			}
		}, StorageException.class);

		container.getMetadata().clear();
		container.getMetadata().put("key2", null);
		this.assertThrows(new RunnableWithExpectedException() {
			public void run() throws Exception {
				container.uploadMetadata();
			}
		}, StorageException.class);
	}

	String contentsOf(CloudBlob blob) throws NotImplementedException,
			StorageException, IOException {
		ByteArrayOutputStream downloadedContentsStream = new ByteArrayOutputStream();
		blob.download(downloadedContentsStream);
		return downloadedContentsStream.toString();
	}

	@Override
	protected void setUp() {
		super.setUp();
		thisTest = this;
	}

	private CloudBlockBlobTests<T> thisTest;
}
