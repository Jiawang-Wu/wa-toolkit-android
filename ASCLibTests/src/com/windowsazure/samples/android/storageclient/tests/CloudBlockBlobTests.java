package com.windowsazure.samples.android.storageclient.tests;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Random;

import junit.framework.Assert;
import android.util.Base64;

import com.windowsazure.samples.android.storageclient.BlockEntry;
import com.windowsazure.samples.android.storageclient.BlockSearchMode;
import com.windowsazure.samples.android.storageclient.CloudBlob;
import com.windowsazure.samples.android.storageclient.CloudBlobContainer;
import com.windowsazure.samples.android.storageclient.CloudBlockBlob;
import com.windowsazure.samples.android.storageclient.NotImplementedException;
import com.windowsazure.samples.android.storageclient.StorageException;
import com.windowsazure.samples.android.storageclient.StorageInnerException;

public abstract class CloudBlockBlobTests<T extends CloudClientAccountProvider>
		extends CloudBlobClientBasedTest<T> {
	public void testCreateBlobInPrivateContainer()
			throws StorageInnerException, Exception {
		CloudBlobContainer container = this
				.createContainer("testcreateblobinprivatecontainer");
		String blobName = "someblob";
		CloudBlockBlob blob = container.getBlockBlobReference(blobName);
		ByteArrayInputStream contentsStream = new ByteArrayInputStream(
				"".getBytes());
		blob.upload(contentsStream, 0);
		ArrayList<String> blobNames = this.getBlobNames(container.listBlobs());
		this.AssertHaveSameElements(blobNames,
				Arrays.asList(new String[] { blobName }));
	}

	public void testReadCreatedBlob() throws StorageInnerException, Exception {
		CloudBlobContainer container = this
				.createContainer("testreadcreatedblob");
		CloudBlockBlob blob = container.getBlockBlobReference("someblob");
		String sampleContent = "SampleContent";

		ByteArrayInputStream contentsStream = new ByteArrayInputStream(
				sampleContent.getBytes());
		blob.upload(contentsStream, sampleContent.length());

		ByteArrayOutputStream downloadedContentsStream = new ByteArrayOutputStream();
		blob.download(downloadedContentsStream);

		Assert.assertEquals(sampleContent, downloadedContentsStream.toString());
	}

	public void testReadPropertiesOfCreatedBlob() throws StorageInnerException,
			Exception {
		CloudBlobContainer container = this
				.createContainer("testreadpropertiesofcreatedblob");
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
				.createContainer("testdeletingblobtwicethrowsexception");
		final CloudBlob blob = this.createEmptyBlob(container, "someBlob");

		blob.delete();

		this.assertThrows(new RunnableWithExpectedException() {
			@Override
			public void run() throws Exception {
				blob.delete();
			}
		}, StorageException.class);
	}

	public void testBlobIsntListedAfterBeingDeleted()
			throws StorageInnerException, Exception {
		CloudBlobContainer container = this
				.createContainer("testblobisntlistedafterbeingdeleted");
		String blobName = "someBlob";
		final CloudBlob blob = this.createEmptyBlob(container, blobName);

		this.AssertHaveSameElements(this.getBlobNames(container.listBlobs()),
				Arrays.asList(new String[] { blobName }));

		blob.delete();

		this.AssertHaveSameElements(this.getBlobNames(container.listBlobs()),
				Arrays.asList(new String[] {}));
	}

	public void testListingIsAccurateWhileCreatingAndDeletingSeveralBlobs()
			throws Exception {
		final CloudBlobContainer container = this
				.createContainer("testlistingisaccuratewhilecreatinganddeletingseveralblobs");
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
				thisTest.AssertHaveSameElements(expectedBlobNames, blobNames);
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
				.createContainer("testwriteandreadlargeblob");
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
				.createContainer("testwriteblobwithstreamofunknownlength");
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
				.createContainer("testcancreateblockblobsusingsingleblock");
		final CloudBlockBlob blob = container
				.getBlockBlobReference("singleBlockBlob");
		String sampleContent = "sampleContent";
		String encodedBlockId = encodedBlockId("block1");

		blob.uploadBlock(encodedBlockId, null, new ByteArrayInputStream(
				sampleContent.getBytes()), sampleContent.length());

		this.assertThrows(new RunnableWithExpectedException() {
			@Override
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
				.createContainer("testupdatingblobbyblocksworksasexpected");
		final CloudBlockBlob blob = container
				.getBlockBlobReference("someBlockBlob");

		final String[] blockIds2 = new String[] { "block1             ",
				"sOMEBlock          ", "otherData          ",
				"otherBlock         ", "moreBL345345453Name" };
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
					encodedBlockIds.add(thisTest.encodedBlockId(blockId));
				}
			}

			void upload(int blobIndex) throws Exception {
				blob.uploadBlock(encodedBlockIds.get(blobIndex), null,
						new ByteArrayInputStream(blockContents[blobIndex]),
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

	private String encodedBlockId(String blockId) {
		return Base64.encodeToString(blockId.getBytes(), Base64.URL_SAFE
				| Base64.NO_WRAP | Base64.NO_PADDING);
	}

	public void testUploadingAndDownloadingBlobMetadataWorksAsExpected()
			throws Exception {
		CloudBlobContainer container = this
				.createContainer("testuploadinganddownloadingblobmetadataworksasexpected");
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
				.createContainer("testuploadingemptyornullvalueforblobmetadatathrowsexception");

		container.getMetadata().put("key2", "");
		this.assertThrows(new RunnableWithExpectedException() {
			@Override
			public void run() throws Exception {
				container.uploadMetadata();
			}
		}, StorageException.class);

		container.getMetadata().clear();
		container.getMetadata().put("key2", null);
		this.assertThrows(new RunnableWithExpectedException() {
			@Override
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
