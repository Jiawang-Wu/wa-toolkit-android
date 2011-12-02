package com.windowsazure.samples.android.storageclient;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.apache.http.HttpStatus;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;

public class CloudQueue {
	Map<String, String> m_Metadata;
	protected CloudQueueClient m_ServiceClient;
	private URI m_Uri;
	private String m_Name;
	private int m_ApproximateMessageCount;
	private boolean m_EncodeMessage;

	/**
	Initializes a new instance of the CloudQueue class.
	@param queueName the name of the queue that will be referenced by this instance
	@param credentials The account credentials used to create the Queue service client.
	@see StorageCredentials, CloudQueueClient 
	*/
	public CloudQueue(String queueName, StorageCredentials credentials) throws URISyntaxException {
		this(queueName, new CloudQueueClient(credentials));
	}

	/**
	Initializes a new instance of the CloudQueue class.
	@param queueName the name of the queue that will be referenced by this instance
	@param serviceClient the Queue service instance that will be used to perform the operations of this instance
	@see CloudQueueClient 
	*/
	public CloudQueue(String queueName, CloudQueueClient serviceClient) throws URISyntaxException {
		m_Name = queueName;
		Utility.assertNotNullOrEmpty("queueName", queueName);
		m_Uri = PathUtility.appendPathToUri(serviceClient.getBaseUri(), queueName);
		m_ServiceClient = serviceClient;
		m_Metadata = new HashMap<String, String>();
		m_ApproximateMessageCount = 0;
		m_EncodeMessage = true;
	}

	/**
	Gets the CloudQueueClient object that represents the Queue service.
	@return CloudQueueClient
	@see CloudQueueClient 
	*/
	public CloudQueueClient getServiceClient() {
		return null;
	}

	/**
	Gets the queue name.
	@return String
	*/
	public String getName() {
		return m_Name;
	}

	/**
	Gets the URI that identifies the queue.
	@return URI
	*/
	public URI getUri() {
		return m_Uri;
	}

	/**
	Gets the queue's attributes, including its user-defined metadata.
	@return QueueAttributes
	@see QueueAttributes
	*/
	public QueueAttributes getAttributes() throws UnsupportedEncodingException, StorageException, IOException {
		this.downloadMetadata();
		return new QueueAttributes(this.getUri(), this.getMetadata());
	}

	/**
	Gets the queue's user-defined metadata.
	@return Map<String, String>
	*/
	public Map<String, String> getMetadata() {
		return m_Metadata;
	}

	/**
	Gets the approximate message count for the queue.
	@return int
	*/
	public int getApproximateMessageCount() throws UnsupportedEncodingException, StorageException, IOException {
		return m_ApproximateMessageCount;
	}

	/**
	Gets a value indicating whether to apply Base64 encoding when adding or retrieving messages.
	@return boolean
	*/
	public boolean getEncodeMessage() {
		return m_EncodeMessage;
	}

	/**
	Sets a value indicating whether to apply Base64 encoding when adding or retrieving messages.
	@param encodeMessage true to apply Base64 encoding, false to not apply it
	*/
	public void setEncodeMessage(boolean encodeMessage) {
		m_EncodeMessage = encodeMessage;
	}

	private boolean create(final boolean createIfNotExist) throws UnsupportedEncodingException, StorageException, IOException {
		final CloudQueue queue = this;
		StorageOperation<Boolean> storageOperation = new StorageOperation<Boolean>() {
			public Boolean execute() throws Exception {
				HttpPut request = QueueRequest.create(queue.getUri());
				QueueRequest.addMetadata(request, queue.m_Metadata);
				m_ServiceClient.getCredentials().signRequest(request, 0L);
				this.processRequest(request);

				switch (result.statusCode) {
					case HttpStatus.SC_CREATED:
						return true;
					case HttpStatus.SC_NO_CONTENT:
					case HttpStatus.SC_CONFLICT:
						if (createIfNotExist) {
							return  false;
						}
						else {
							throw new StorageInnerException("Couldn't create a queue: a similar queue already exists");
						}
					default:
						throw new StorageInnerException("Couldn't create a queue");
				}
			}
		};

        return storageOperation.executeTranslatingExceptions();
    }

	/**
	Creates a queue.
	*/
	public void create() throws UnsupportedEncodingException, StorageException, IOException {
		this.create(false);
	}

	/**
	Creates the queue if it does not exist.
	@return boolean true if the queue was created, false if the queue already existed
	*/
	public boolean createIfNotExist() throws UnsupportedEncodingException, StorageException, IOException {
		return this.create(true);
	}

	/**
	Deletes the queue.
	*/
	public void delete() throws UnsupportedEncodingException, StorageException, IOException {
		final CloudQueue queue = this;
		StorageOperation<Void> storageOperation = new StorageOperation<Void>() {
			public Void execute() throws Exception {
				HttpDelete request = QueueRequest.delete(queue.getUri());
				m_ServiceClient.getCredentials().signRequest(request, -1L);
				this.processRequest(request);

				switch (result.statusCode) {
					case HttpStatus.SC_NO_CONTENT:
						return null;
					default:
						throw new StorageInnerException("Couldn't delete a queue");
				}
			}
		};

        storageOperation.executeTranslatingExceptions();
	}

	/**
	Fetches the queue's metadata.
	*/
	public void downloadMetadata() throws UnsupportedEncodingException, StorageException, IOException {
		this.downloadAttributes(true, false);
	}

	private void downloadAttributes(final boolean updateMetadata, final boolean updateApproximateMessageCount) throws UnsupportedEncodingException, StorageException, IOException {
		final CloudQueue queue = this;
		StorageOperation<Integer> storageOperation = new StorageOperation<Integer>() {
			public Integer execute() throws Exception {
				HttpGet request = QueueRequest.getProperties(queue.getUri());
				m_ServiceClient.getCredentials().signRequest(request, -1L);
				this.processRequest(request);

				if (updateMetadata) {
					m_Metadata.clear();
					m_Metadata.putAll(QueueResponse.getMetadata(result.httpResponse));
				}
				if (updateApproximateMessageCount) {
					String messageCount = result.httpResponse.getFirstHeader("x-ms-approximate-messages-count").getValue();
					m_ApproximateMessageCount = Integer.parseInt(messageCount);
				}
				switch (result.statusCode) {
					case HttpStatus.SC_OK:
						return null;
					default:
						throw new StorageInnerException("Couldn't fetch queue attributes");
				}
			}
		};

        storageOperation.executeTranslatingExceptions();
	}

	/**
	Sets the queue's metadata.
	*/
	public void uploadMetadata() throws UnsupportedEncodingException, StorageException, IOException {
		final CloudQueue queue = this;
		StorageOperation<Void> storageOperation = new StorageOperation<Void>() {
			public Void execute() throws Exception {
				HttpPut request = QueueRequest.setMetadata(queue.getUri());
				QueueRequest.addMetadata(request, queue.m_Metadata);
				m_ServiceClient.getCredentials().signRequest(request, 0L);
				this.processRequest(request);

				switch (result.statusCode) {
					case HttpStatus.SC_NO_CONTENT:
						return null;
					default:
						throw new StorageInnerException("Couldn't upload queue metadata");
				}
			}
		};

        storageOperation.executeTranslatingExceptions();
	}

	/**
	Retrieves the approximate message count for the queue.
	@return int the approximate message count
	*/
	public int downloadApproximateMessageCount() throws UnsupportedEncodingException, StorageException, IOException {
		this.downloadAttributes(false, true);
		return m_ApproximateMessageCount;
	}

	/**
	Adds a message to the queue.
	@param message message to be added to the queue
	*/
	public void addMessage(CloudQueueMessage message) throws UnsupportedEncodingException, StorageException, IOException {
		this.addMessage(message, this.getDefaultTimeToLiveInSeconds());
	}

	private int getDefaultTimeToLiveInSeconds() {
		return CloudQueueMessage.MaxTimeToLiveInSeconds;
	}

	/**
	Adds a message to the queue.
	@param message message to be added to the queue
	@param timeToLiveInSeconds how much time in seconds will the message exist before being automatically deleted
	*/
	public void addMessage(final CloudQueueMessage message,
			final int timeToLiveInSeconds) throws UnsupportedEncodingException, StorageException, IOException {
		final CloudQueue queue = this;
		StorageOperation<Void> storageOperation = new StorageOperation<Void>() {
			public Void execute() throws Exception {
				HttpPost request = QueueRequest.addMessage(queue.getUri(), timeToLiveInSeconds, message, m_EncodeMessage);
				m_ServiceClient.getCredentials().signRequest(request, request.getEntity().getContentLength());
				this.processRequest(request);

				switch (result.statusCode) {
					case HttpStatus.SC_CREATED:
						return null;
					default:
						throw new StorageInnerException("Couldn't add queue message");
				}
			}
		};

        storageOperation.executeTranslatingExceptions();
	}

	/**
	Gets a list of messages from the queue.
	@param messageCount the amount of messageCount at maximum that will be retrieved
	@return Iterable<CloudQueueMessage> the messages retrieved from the queue
	@see CloudQueueMessage
	*/
	public Iterable<CloudQueueMessage> getMessages(int messageCount) throws UnsupportedEncodingException, StorageException, IOException {
		return this.getMessages(messageCount, this.getDefaultVisibilityTimeoutInSeconds());
	}

	private int getDefaultVisibilityTimeoutInSeconds() {
		return 6000;
	}

	/**
	Gets a list of messages from the queue.
	@param messageCount the amount of messageCount at maximum that will be retrieved
	@param visibilityTimeoutInSeconds how much time will the messages retrieved will be marked as "invisible" before reappearing in the queue
	@return Iterable<CloudQueueMessage> the messages retrieved from the queue
	@see CloudQueueMessage
	*/
	public Iterable<CloudQueueMessage> getMessages(int messageCount,
			int visibilityTimeoutInSeconds) throws UnsupportedEncodingException, StorageException, IOException {
		return this.getOrPeekMessages(messageCount, false, visibilityTimeoutInSeconds);
	}

	/**
	Gets a single message from the queue.
	@return CloudQueueMessage the message retrieved from the queue
	@see CloudQueueMessage
	*/
	public CloudQueueMessage getMessage() throws UnsupportedEncodingException, StorageException, IOException {
		Iterator<CloudQueueMessage> iterator = this.getMessages(1).iterator();
		CloudQueueMessage message = iterator.next();
		if (iterator.hasNext()) {
			throw new StorageException("Internal error", "More than one message was get, but only one was expected", 0, null, null);
		}
		return message;
	}

	/**
	Gets a single message from the queue.
	@param visibilityTimeoutInSeconds how much time will the message retrieved will be marked as "invisible" before reappearing in the queue
	@return CloudQueueMessage the message retrieved from the queue
	@see CloudQueueMessage
	*/
	public CloudQueueMessage getMessage(int visibilityTimeoutInSeconds) throws UnsupportedEncodingException, StorageException, IOException {
		return this.getMessages(1, visibilityTimeoutInSeconds).iterator().next();
	}

	/**
	Peeks a message from the queue.
	@return CloudQueueMessage the message peeked from the queue
	@see CloudQueueMessage
	*/
	public CloudQueueMessage peekMessage() throws UnsupportedEncodingException, StorageException, IOException {
		return this.peekMessages(1).iterator().next();
	}

	/**
	Peeks a message from the queue.
	@param messageCount the amount of messages at maximum that will be peeked from the queue
	@return CloudQueueMessage the message peeked from the queue
	@see CloudQueueMessage
	*/
	public Iterable<CloudQueueMessage> peekMessages(final int messageCount) throws UnsupportedEncodingException, StorageException, IOException {
		return this.getOrPeekMessages(messageCount, true, 0);
	}

	private Iterable<CloudQueueMessage> getOrPeekMessages(final int messageCount, final boolean peekMessages, final int visibilityTimeoutInSeconds) throws UnsupportedEncodingException, StorageException, IOException {
		final CloudQueue queue = this;
		StorageOperation<Iterable<CloudQueueMessage>> storageOperation = new StorageOperation<Iterable<CloudQueueMessage>>() {
			public Iterable<CloudQueueMessage> execute() throws Exception {
				HttpGet request = QueueRequest.getMessages(queue.getUri(), messageCount, peekMessages, visibilityTimeoutInSeconds);
				m_ServiceClient.getCredentials().signRequest(request, -1L);
				this.processRequest(request);

				switch (result.statusCode) {
					case HttpStatus.SC_OK:
						return QueueResponse.getMessagesList(result.httpResponse.getEntity().getContent(), m_EncodeMessage);
					default:
						throw new StorageInnerException("Couldn't peek queue messages");
				}
			}
		};

        return storageOperation.executeTranslatingExceptions();
    }

	/**
	Deletes a message.
	@param message the message that will be deleted. This message needs to have been obtained with the getMessage(s) methods
	@see CloudQueueMessage
	*/
	public void deleteMessage(CloudQueueMessage message) throws UnsupportedEncodingException, StorageException, IOException {
		this.deleteMessage(message.getId(), message.getPopReceipt());
	}

	/**
	Deletes a message.
	@param messageId the messageId of the message that will be deleted.
	@param popReceipt the popReceipt of the message that will be deleted.
	@see CloudQueueMessage
	*/
	public void deleteMessage(final String messageId, final String popReceipt) throws UnsupportedEncodingException, StorageException, IOException {
		final CloudQueue queue = this;
		StorageOperation<Void> storageOperation = new StorageOperation<Void>() {
			public Void execute() throws Exception {
				HttpDelete request = QueueRequest.deleteMessage(queue.getUri(), messageId, popReceipt);
				m_ServiceClient.getCredentials().signRequest(request, -1L);
				this.processRequest(request);

				switch (result.statusCode) {
					case HttpStatus.SC_NO_CONTENT:
						return null;
					default:
						throw new StorageInnerException("Couldn't delete queue message");
				}
			}
		};

        storageOperation.executeTranslatingExceptions();
	}

	/**
	Clears all messages from the queue.
	*/
	public void clear() throws UnsupportedEncodingException, StorageException, IOException {
		final CloudQueue queue = this;
		StorageOperation<Void> storageOperation = new StorageOperation<Void>() {
			public Void execute() throws Exception {
				HttpDelete request = QueueRequest.clear(queue.getUri());
				m_ServiceClient.getCredentials().signRequest(request, -1L);
				this.processRequest(request);

				switch (result.statusCode) {
					case HttpStatus.SC_NO_CONTENT:
						return null;
					default:
						throw new StorageInnerException("Couldn't clear queue messages");
				}
			}
		};

        storageOperation.executeTranslatingExceptions();
	}
}