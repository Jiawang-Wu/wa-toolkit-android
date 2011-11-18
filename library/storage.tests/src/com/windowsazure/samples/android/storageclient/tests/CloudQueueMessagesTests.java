package com.windowsazure.samples.android.storageclient.tests;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.concurrent.Callable;

import junit.framework.Assert;

import android.util.Base64;

import com.windowsazure.samples.android.storageclient.CloudQueue;
import com.windowsazure.samples.android.storageclient.CloudQueueMessage;
import com.windowsazure.samples.android.storageclient.NotImplementedException;
import com.windowsazure.samples.android.storageclient.StorageException;

public abstract class CloudQueueMessagesTests<T extends CloudClientAccountProvider> extends CloudQueueClientBasedTest<T> {

		@Override
		protected void setUp() {
			super.setUp();
			thisTest = this;
		}

		private CloudQueueMessagesTests thisTest;

		public void testBase64Encoding() throws UnsupportedEncodingException, StorageException, NotImplementedException, URISyntaxException, IOException
		{
			String content = "Sample Content";
			String otherContent = "Other sample Content";

			CloudQueue queueWithEncoding = this.createQueue("testbase64encoding");
			queueWithEncoding.setEncodeMessage(true);

			CloudQueue queueWithoutEncoding = cloudQueueClient.getQueueReference(queueWithEncoding.getName());
			queueWithoutEncoding.setEncodeMessage(false);

			queueWithEncoding.addMessage(new CloudQueueMessage(content));
			CloudQueueMessage message = queueWithoutEncoding.getMessage();
			Assert.assertEquals(Base64.encodeToString(content.getBytes(), Base64.DEFAULT), message.getAsString());
			queueWithEncoding.deleteMessage(message);

			queueWithoutEncoding.addMessage(new CloudQueueMessage(Base64.encode(otherContent.getBytes(), Base64.DEFAULT)));
			message = queueWithEncoding.getMessage();
			Assert.assertEquals(otherContent, message.getAsString());
		}
		
		public void testAddedMessageIsListedAndDeletedIsNot()
				throws Exception {
			final CloudQueue queue = this.createQueue("testaddedmessageislistedanddeletedisnot2");
			Assert.assertFalse(queue.peekMessages(1).iterator().hasNext());
			Assert.assertFalse(queue.getMessages(1).iterator().hasNext());

			String someContent = "some text";
			queue.addMessage(new CloudQueueMessage(someContent));
			
			ArrayList<CloudQueueMessage> messages = this.toList(queue.getMessages(2, 1));
			Assert.assertEquals(1, messages.size());
			Assert.assertEquals(someContent, messages.get(0).getAsString());
			Thread.sleep(1); //Wait for the message to reappear

			this.assertEventuallyTrue(new Callable<Boolean>() {
				@Override
				public Boolean call() throws Exception {
					ArrayList<CloudQueueMessage> peekedMessages = thisTest.toList(queue.peekMessages(2));
					return peekedMessages.size() == 1;
				}
			}, 10000);
			
			ArrayList<CloudQueueMessage> peekedMessages = this.toList(queue.peekMessages(2));
			Assert.assertEquals(1, peekedMessages.size());
			Assert.assertEquals(someContent, peekedMessages.get(0).getAsString());

			queue.deleteMessage(messages.get(0));
			Assert.assertFalse(queue.peekMessages(1).iterator().hasNext());
			Assert.assertFalse(queue.getMessages(1).iterator().hasNext());
		}
		
		public void testClearDeletesAllMessages()
				throws Exception {
			final CloudQueue queue = this.createQueue("testcleardeletesallmessages");
			String content1 = "some content";
			String content2 = "other content";
			String content3 = "extra content";
			queue.addMessage(new CloudQueueMessage(content1));
			queue.addMessage(new CloudQueueMessage(content2));
			queue.addMessage(new CloudQueueMessage(content3));
			
			Assert.assertEquals(3, this.toList(queue.peekMessages(4)).size());
			queue.clear();
			
			queue.downloadApproximateMessageCount();
			Assert.assertEquals(0, queue.getApproximateMessageCount());
			Assert.assertEquals(0, this.toList(queue.peekMessages(1)).size());
		}
		
		public void testAddSeveralMessageAndListThem()
				throws Exception {
			final CloudQueue queue = this.createQueue("testaddseveralmessageandlistthem");
			String content1 = "some content";
			String content2 = "other content";
			String content3 = "extra content";
			queue.addMessage(new CloudQueueMessage(content1));
			queue.addMessage(new CloudQueueMessage(content2));
			queue.addMessage(new CloudQueueMessage(content3));
			
			queue.downloadApproximateMessageCount();
			Assert.assertEquals(3, queue.getApproximateMessageCount());

			CloudQueueMessage message = queue.peekMessage();
			Assert.assertEquals(content1, message.getAsString());
			queue.downloadApproximateMessageCount();
			Assert.assertEquals(3, queue.getApproximateMessageCount());

			message = queue.getMessage();
			Assert.assertEquals(content1, message.getAsString());
			queue.deleteMessage(message);
			Assert.assertEquals(3, queue.getApproximateMessageCount());
			
			message = queue.peekMessage();
			Assert.assertEquals(content2, message.getAsString());
			queue.downloadApproximateMessageCount();
			Assert.assertEquals(2, queue.getApproximateMessageCount());

			message = queue.getMessage();
			Assert.assertEquals(content2, message.getAsString());
			queue.deleteMessage(message);
			Assert.assertEquals(2, queue.getApproximateMessageCount());
			queue.downloadApproximateMessageCount();
			Assert.assertEquals(1, queue.getApproximateMessageCount());

			message = queue.peekMessage();
			Assert.assertEquals(content3, message.getAsString());
			queue.downloadApproximateMessageCount();
			Assert.assertEquals(1, queue.getApproximateMessageCount());

			message = queue.getMessage();
			Assert.assertEquals(content3, message.getAsString());
			queue.deleteMessage(message);
			Assert.assertEquals(1, queue.getApproximateMessageCount());
			queue.downloadApproximateMessageCount();
			Assert.assertEquals(0, queue.getApproximateMessageCount());
		}

		public void testGetMessagesErasesMessagesButPeekMessagesDoesNot()
				throws Exception {
			CloudQueue queue = this.createQueue("testgetmessageserasesmessagesbutpeekmessagesdoesnot");
			Assert.assertFalse(queue.peekMessages(1).iterator().hasNext());
			Assert.assertFalse(queue.getMessages(1).iterator().hasNext());

			String someContent = "some text";
			queue.addMessage(new CloudQueueMessage(someContent));
			ArrayList<CloudQueueMessage> messages = this.toList(queue.peekMessages(2));
			Assert.assertEquals(1, messages.size());

			messages = this.toList(queue.peekMessages(2));
			Assert.assertEquals(1, messages.size());

			messages = this.toList(queue.getMessages(2));
			Assert.assertEquals(1, messages.size());

			messages = this.toList(queue.getMessages(2));
			Assert.assertEquals(0, messages.size());

			messages = this.toList(queue.peekMessages(2));
			Assert.assertEquals(0, messages.size());
		}

		public void testListingMessagesIsAccurateWhileCreatingAndDeletingSeveralMessages()
				throws Exception {
			final int messagesLength[] = new int[]{ 4, 8, 0, 230, 512, 513, 5, 4, 3, 10 };
			final CloudQueue queue = this.createQueue("testlistingmessagesisaccuratewhilecreatinganddeletingseveral");
			queue.setEncodeMessage(true);
			
			class CreateAndDeleteMessagesHelper {
				
				ArrayList<Integer> expectedMessagesInQueue = new ArrayList<Integer>();
				ArrayList<byte[]> messageContents = new ArrayList<byte[]>();
				ArrayList<CloudQueueMessage> currentQueueMessages;
				Map<Integer, String> indexToIdMapping = new HashMap<Integer, String>();
				Set<String> knownMessageIds = new HashSet<String>();

				CreateAndDeleteMessagesHelper()
				{
					Random random = new Random();
					for (int length : messagesLength)
					{
						byte[] content = new byte[length];
						random.nextBytes(content);
						messageContents.add(content);
					}
				}

				void add(int index) throws Exception {
					queue.addMessage(new CloudQueueMessage(messageContents.get(index)));
					expectedMessagesInQueue.add(index);
					ArrayList<CloudQueueMessage> messages = thisTest.toList(queue.peekMessages(expectedMessagesInQueue.size() + 1));
					for (CloudQueueMessage message : messages)
					{
						if (!knownMessageIds.contains(message.getId()))
						{
							knownMessageIds.add(message.getId());
							indexToIdMapping.put(index, message.getId());
							this.assertListingGivesExpectedMessages();
							return;
						}
					}
					Assert.fail("The new message id wasn't found");
				}

				void delete(int index) throws Exception {
					String messageId = indexToIdMapping.get(index);
					for (int messageIndex = 0; messageIndex < currentQueueMessages.size(); ++messageIndex)
					{
						String otherMessageId = currentQueueMessages.get(messageIndex).getId();
						if (messageId.equals(otherMessageId))
						{
							queue.deleteMessage(currentQueueMessages.get(messageIndex));
							for (int i = 0; i < expectedMessagesInQueue.size(); ++i)
							{
								if (expectedMessagesInQueue.get(i).intValue() == index)
								{
									expectedMessagesInQueue.remove(i);
									this.assertListingGivesExpectedMessages();
									return;
								}
							}
						}
					}
					
					Assert.fail("The expected message wasn't found");
				}

				Comparator<byte[]> getComparator()
				{
					return new Comparator<byte[]>() {
						@Override
						public int compare(byte[] left, byte[] right) {
							if (left.length == right.length)
							{
								for (int index = 0; index < left.length; ++index)
								{
									if (left[index] != right[index])
									{
										return left[index] < right[index] ? 1 : -1;
									}
								}
								return 0;
							}
							else
							{
								return left.length < right.length ? 1 : -1;
							}
						}
						
					};
				}
				
				void assertListingGivesExpectedMessages()
						throws NotImplementedException, Exception {
					currentQueueMessages = thisTest.toList(queue.getMessages(expectedMessagesInQueue.size() + 1, 1));

					List<byte[]> retrievedMessageContents = thisTest.getMessageContents(currentQueueMessages);
					Assert.assertEquals(expectedMessagesInQueue.size(), retrievedMessageContents.size());
					
					ArrayList<byte[]> expectedMessageContents = new ArrayList<byte[]>();
					
					for (int index = 0; index < expectedMessagesInQueue.size(); ++index)
					{
						expectedMessageContents.add(messageContents.get(expectedMessagesInQueue.get(index)));
					}

					Collections.sort(retrievedMessageContents, this.getComparator());
					Collections.sort(expectedMessageContents, this.getComparator());
					
					for (int index = 0; index < expectedMessageContents.size(); ++index)
					{
						Assert.assertTrue(Arrays.equals(expectedMessageContents.get(index), retrievedMessageContents.get(index)));
					}

				}
			};

			CreateAndDeleteMessagesHelper helper = new CreateAndDeleteMessagesHelper();
			
			helper.add(0);
			helper.delete(0);
			helper.add(0);
			helper.add(1);
			helper.delete(0);
			helper.delete(1);
			helper.add(1);
			helper.add(0);
			helper.delete(0);
			helper.delete(1);
			helper.add(0);
			helper.add(1);
			helper.add(3);
			helper.add(4);
			helper.add(5);
			helper.delete(3);
			helper.delete(4);
			helper.add(6);
			helper.add(7);
			helper.add(8);
			helper.add(1);
			helper.add(2);
			helper.add(3);
			helper.add(9);
		}

		public ArrayList<byte[]> getMessageContents(
				Iterable<CloudQueueMessage> messages) {
			ArrayList<byte[]> contents = new ArrayList<byte[]>();
			for (CloudQueueMessage message : messages)
			{
				contents.add(message.getAsBytes());
			}
			return contents;
		}		
}
