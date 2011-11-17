package com.windowsazure.samples.android.storageclient.tests;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import junit.framework.Assert;

import com.windowsazure.samples.android.storageclient.CloudQueue;
import com.windowsazure.samples.android.storageclient.CloudQueueMessage;
import com.windowsazure.samples.android.storageclient.NotImplementedException;
import com.windowsazure.samples.android.storageclient.StorageException;

public class CloudQueueTests extends CloudQueueClientBasedTest<CloudStorageAccountProvider> {

	@Override
	protected void setUp() {
		super.setUp();
		thisTest = this;
	}

	private CloudQueueTests thisTest;
	public void testCreatedQueueIncludesMetadata() throws StorageException, UnsupportedEncodingException, IOException, URISyntaxException
	{
		Map<String, String> metadata = new HashMap<String, String>();
		metadata.put("someKey", "someString");
		metadata.put("otherKey", "otherSteram");

		CloudQueue queue = cloudQueueClient.getQueueReference("testcreatedqueueincludesmetadata");
		queue.getMetadata().putAll(metadata);
		queue.create();
		this.addResourceCleaner(queue, cleanerFor(queue));

		CloudQueue sameQueue = cloudQueueClient.getQueueReference(queue.getName());
		sameQueue.downloadMetadata();
		this.AssertHashMapsAreEquivalent(metadata, sameQueue.getMetadata());
	}

	public void testUploadingAndDownloadingQueueMetadataWorksAsExpected()
			throws Exception {
		CloudQueue queue = this.createQueue("testuploadinganddownloadingqueuemetadataworksasexpected");
		CloudQueue sameQueue = cloudQueueClient.getQueueReference(queue.getName());

		Assert.assertFalse(queue == sameQueue);

		this.AssertHashMapsAreEquivalent(queue.getMetadata(), new HashMap<String, String>());

		Map<String, String> metadata = queue.getMetadata();
		metadata.put("key1", "value1");
		queue.uploadMetadata();
		sameQueue.downloadMetadata();
		this.AssertHashMapsAreEquivalent(queue.getMetadata(), sameQueue.getMetadata());

		metadata.clear();
		metadata.put("key3", "otherValue&-/\\@");
		metadata.put("key2", "345654");
		queue.uploadMetadata();
		sameQueue.downloadMetadata();
		this.AssertHashMapsAreEquivalent(queue.getMetadata(), sameQueue.getMetadata());

		metadata.clear();
		queue.uploadMetadata();
		sameQueue.downloadMetadata();
		this.AssertHashMapsAreEquivalent(sameQueue.getMetadata(), new HashMap<String, String>());
	}

	public void testListingQueuesIncludesMetadata() throws URISyntaxException, UnsupportedEncodingException, StorageException, IOException, NotImplementedException
	{
		Map<String, String> metadata = new HashMap<String, String>();
		metadata.put("someKey", "someString");
		metadata.put("otherKey", "otherSteram");

		CloudQueue queue = this.createQueue("testlistingqueuesincludesmetadata");
		queue.getMetadata().putAll(metadata);
		queue.uploadMetadata();
		
		CloudQueue sameQueue = cloudQueueClient.listQueues().iterator().next();
		this.AssertHashMapsAreEquivalent(metadata, sameQueue.getMetadata());
	}

	public void testCreateQueueObjectWithNullNameThrowsException()
			throws Exception {
		this.assertThrows(new RunnableWithExpectedException() {
			@Override
			public void run() throws Exception {
				cloudQueueClient.getQueueReference(null);
			}
		}, IllegalArgumentException.class);
	}

	public void testCreateQueueeObjectWithEmptyNameThrowsException()
			throws Exception {
		this.assertThrows(new RunnableWithExpectedException() {
			@Override
			public void run() throws Exception {
				cloudQueueClient.getQueueReference("");
			}
		}, IllegalArgumentException.class);
	}

	public void testCanDeleteQueueFromOtherObject()
			throws Exception {
		
		Assert.assertFalse(cloudQueueClient.listQueues().iterator().hasNext());

		CloudQueue queue = this.createQueue("testcandeletequeuefromotherobject");
		Assert.assertTrue(cloudQueueClient.listQueues().iterator().hasNext());

		CloudQueue sameQueue = cloudQueueClient.getQueueReference(queue.getName());
		sameQueue.delete();
		Assert.assertFalse(cloudQueueClient.listQueues().iterator().hasNext());
	}

	public void testApproximateMessageCount()
			throws Exception {
		CloudQueue queue = this.createQueue("testapproximatemessagecount");
		CloudQueueMessage message = new CloudQueueMessage("someContent");

		Assert.assertEquals(0, queue.downloadApproximateMessageCount());
		Assert.assertEquals(0, queue.getApproximateMessageCount());

		queue.addMessage(message);
		Assert.assertEquals(1, queue.downloadApproximateMessageCount());
		Assert.assertEquals(1, queue.getApproximateMessageCount());

		queue.addMessage(message);
		Assert.assertEquals(1, queue.getApproximateMessageCount());
		Assert.assertEquals(2, queue.downloadApproximateMessageCount());
		Assert.assertEquals(2, queue.getApproximateMessageCount());

		queue.addMessage(message);
		Assert.assertEquals(2, queue.getApproximateMessageCount());
		Assert.assertEquals(3, queue.downloadApproximateMessageCount());
		Assert.assertEquals(3, queue.getApproximateMessageCount());
	}

	public void testCreateQueueWithInvalidNameThrowsException()
			throws Exception {
		this.assertThrows(new RunnableWithExpectedException() {
			@Override
			public void run() throws Exception {
				thisTest.createQueue("some/queue");
			}
		}, StorageException.class);
	}

	public void testDeleteQueueWithInvalidNameThrowsException()
			throws Exception {
		final CloudQueue queue = cloudQueueClient.getQueueReference("some/queue");
		this.assertThrows(new RunnableWithExpectedException() {
			@Override
			public void run() throws Exception {
				thisTest.deleteQueue(queue);
			}
		}, StorageException.class);
	}

	public void testCreateQueueTwiceThrowsException()
			throws Exception {
		final String queueName = "testcreatequeuetwicethrowsexception";
		final CloudQueue queue = this.createQueue(queueName);
		this.assertThrows(new RunnableWithExpectedException() {
			@Override
			public void run() throws Exception {
				thisTest.createQueue(queueName);
			}
		}, StorageException.class);
		
		this.assertThrows(new RunnableWithExpectedException() {
			@Override
			public void run() throws Exception {
				queue.create();
			}
		}, StorageException.class);
	}
	
	public void testCreateQueueIfNotExists()
			throws Exception {

		CloudQueue queue = cloudQueueClient.getQueueReference("testcreatequeueifnotexists");

		Assert.assertTrue(queue.createIfNotExist());
		this.addResourceCleaner(queue, cleanerFor(queue));
		Assert.assertFalse(queue.createIfNotExist());

		CloudQueue sameQueue = cloudQueueClient.getQueueReference(queue.getName());
		Assert.assertFalse(sameQueue.createIfNotExist());
	}

	public void testCreateQueueTwiceWithDifferentMetadataThrowsException()
			throws Exception {
		final String queueName = "testcreatequeuetwicewithdifferentmetadatathrowsexception";
		final CloudQueue queue = this.createQueue(queueName);
		this.addResourceCleaner(queue, cleanerFor(queue));
		
		final CloudQueue sameQueue = cloudQueueClient.getQueueReference(queueName);
		sameQueue.getMetadata().put("someKey", "someValue");
		this.assertThrows(new RunnableWithExpectedException() {
			@Override
			public void run() throws Exception {
				sameQueue.create();
			}
		}, StorageException.class);

		queue.getMetadata().put("someKey", "someValue");
		this.assertThrows(new RunnableWithExpectedException() {
			@Override
			public void run() throws Exception {
				queue.create();
			}
		}, StorageException.class);
	}

	public void testCreateQueueIfNotExistsWithDifferentMetadata()
			throws Exception {
		CloudQueue queue = cloudQueueClient.getQueueReference("testcreatequeueifnotexistswithdifferentmetadata");
		Assert.assertTrue(queue.createIfNotExist());
		this.addResourceCleaner(queue, cleanerFor(queue));
		queue.getMetadata().put("someKey", "someValue");
		Assert.assertFalse(queue.createIfNotExist());

		final CloudQueue sameQueue = cloudQueueClient.getQueueReference(queue.getName());
		sameQueue.getMetadata().put("someKey", "someValue");
		Assert.assertFalse(sameQueue.createIfNotExist());
	}

	public void testCreatedQueuesAreListedProperlyByPrefixes()
			throws Exception {
		this.createQueue("abc-def-jkl");
		this.createQueue("abc-0");
		this.createQueue("abc-1");
		this.createQueue("abc-2");
		this.createQueue("ab-3");
		this.createQueue("a-4");
		this.createQueue("def-5");
		this.createQueue("def-6");

		Assert.assertFalse(cloudQueueClient.listQueues("nothing").iterator().hasNext());
		ArrayList<CloudQueue> queues = this.toList(cloudQueueClient.listQueues("abc"));
		this.assertHaveSameElements(
				this.getQueuesNames(queues),
				Arrays.asList(new String[] { "abc-0", "abc-1", "abc-2",
						"abc-def-jkl" }));
		this.assertHaveSameElements(
				this.getQueuesNames(cloudQueueClient.listQueues("abc-")),
				Arrays.asList(new String[] { "abc-0", "abc-1", "abc-2",
						"abc-def-jkl" }));
		this.assertHaveSameElements(
				this.getQueuesNames(cloudQueueClient.listQueues("abc-0")),
				Arrays.asList(new String[] { "abc-0" }));
		this.assertHaveSameElements(
				this.getQueuesNames(cloudQueueClient.listQueues("ab")),
				Arrays.asList(new String[] { "ab-3", "abc-0", "abc-1", "abc-2",
						"abc-def-jkl" }));
		this.assertHaveSameElements(
				this.getQueuesNames(cloudQueueClient.listQueues("ab-")),
				Arrays.asList(new String[] { "ab-3" }));
		this.assertHaveSameElements(
				this.getQueuesNames(cloudQueueClient.listQueues("a")),
				Arrays.asList(new String[] { "ab-3", "abc-0", "abc-1", "abc-2",
						"a-4", "abc-def-jkl" }));
		this.assertHaveSameElements(
				this.getQueuesNames(cloudQueueClient.listQueues("d")),
				Arrays.asList(new String[] { "def-5", "def-6" }));
		this.assertHaveSameElements(
				this.getQueuesNames(cloudQueueClient.listQueues("def-55")),
				Arrays.asList(new String[] {}));
		this.assertHaveSameElements(
				this.getQueuesNames(cloudQueueClient.listQueues("blah")),
				Arrays.asList(new String[] {}));
		this.assertHaveSameElements(
				this.getQueuesNames(cloudQueueClient.listQueues("")),
				Arrays.asList(new String[] { "ab-3", "abc-0", "abc-1", "abc-2",
						"a-4", "def-5", "def-6", "abc-def-jkl" }));
	}

	public void testListingIsAccurateWhileCreatingAndDeletingSeveralQueues()
			throws Exception {
		final String queueBaseName = "listingisaccuratewhilecreatinganddeletingseveralqueues-";
		final ArrayList<String> expectedQueuesNames = new ArrayList<String>();
		class CreateAndDeleteQueuesHelper {
			void create(String suffix) throws Exception {
				String queueName = queueBaseName + suffix;
				thisTest.createQueue(queueName);
				expectedQueuesNames.add(queueName);
				this.assertListingGivesExpectedContainers();
			}

			void delete(String suffix) throws Exception {
				String queueName = queueBaseName + suffix;
				CloudQueue queue = cloudQueueClient.getQueueReference(queueName);
				thisTest.deleteQueue(queue);
				expectedQueuesNames.remove(queueName);
				this.assertListingGivesExpectedContainers();
			}

			void assertListingGivesExpectedContainers()
					throws NotImplementedException, Exception {
				ArrayList<String> queuesNames = thisTest
						.getQueuesNames(cloudQueueClient.listQueues());
				thisTest.assertHaveSameElements(expectedQueuesNames,
						queuesNames);
			}
		};

		CreateAndDeleteQueuesHelper helper = new CreateAndDeleteQueuesHelper();
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
}
