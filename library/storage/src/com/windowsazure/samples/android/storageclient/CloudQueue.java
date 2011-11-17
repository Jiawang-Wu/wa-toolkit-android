package com.windowsazure.samples.android.storageclient;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.Map;

import org.apache.http.HttpStatus;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpHead;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;

public class CloudQueue {
	Map<String, String> m_Metadata;
	protected CloudQueueClient m_ServiceClient;
	private URI m_Uri;
	private String m_Name;
	private int m_ApproximateMessageCount;
	
	public CloudQueue(String queueName, StorageCredentials credentials) throws URISyntaxException {
		this(queueName, new CloudQueueClient(credentials));
	}

	public CloudQueue(String queueName, CloudQueueClient serviceClient) throws URISyntaxException {
		m_Name = queueName;
		Utility.assertNotNullOrEmpty("queueName", queueName);
		m_Uri = PathUtility.appendPathToUri(serviceClient.getBaseUri(), queueName);
		m_ServiceClient = serviceClient;
		m_Metadata = new HashMap<String, String>();
		m_ApproximateMessageCount = 0;
	}

	public CloudQueueClient getServiceClient() {
		return null;
	}

	public String getName() {
		return m_Name;
	}

	public URI getUri() {
		return m_Uri;
	}

	public QueueAttributes getAttributes() throws UnsupportedEncodingException, StorageException, IOException {
		this.downloadMetadata();
		return new QueueAttributes(this.getUri(), this.getMetadata());
	}

	public Map<String, String> getMetadata() {
		return m_Metadata;
	}

	public int getApproximateMessageCount() throws UnsupportedEncodingException, StorageException, IOException {
		return m_ApproximateMessageCount;
	}

	public boolean getEncodeMessage() {
		return false;
	}

	public boolean setEncodeMessage(boolean encodeMessage) {
		return false;
	}

	public boolean create(final boolean createIfNotExist) throws UnsupportedEncodingException, StorageException, IOException {
		final CloudQueue queue = this;
		StorageOperation<Boolean> storageOperation = new StorageOperation<Boolean>() {
			public Boolean execute() throws Exception {
				HttpPut request = QueueRequest.create(queue.getUri());
				QueueRequest.addMetadata(request, queue.m_Metadata);
				m_ServiceClient.getCredentials().signRequest(request, 0L);
				this.processRequest(request);

				switch (result.statusCode)
				{
					case HttpStatus.SC_CREATED:
						return true;
					case HttpStatus.SC_NO_CONTENT:
					case HttpStatus.SC_CONFLICT:
						if (createIfNotExist)
						{
							return  false;
						}
						else
						{
							throw new StorageInnerException("Couldn't create a queue: a similar queue already exists");
						}
					default:
						throw new StorageInnerException("Couldn't create a queue");
				}
			}
		};

        return storageOperation.executeTranslatingExceptions();
    }

	public void create() throws UnsupportedEncodingException, StorageException, IOException {
		this.create(false);
	}

	public boolean createIfNotExist() throws UnsupportedEncodingException, StorageException, IOException {
		return this.create(true);
	}

	public void delete() throws UnsupportedEncodingException, StorageException, IOException {
		final CloudQueue queue = this;
		StorageOperation<Void> storageOperation = new StorageOperation<Void>() {
			public Void execute() throws Exception {
				HttpDelete request = QueueRequest.delete(queue.getUri());
				m_ServiceClient.getCredentials().signRequest(request, -1L);
				this.processRequest(request);

				switch (result.statusCode)
				{
					case HttpStatus.SC_NO_CONTENT:
						return null;
					default:
						throw new StorageInnerException("Couldn't delete a queue");
				}
			}
		};

        storageOperation.executeTranslatingExceptions();
	}

	public boolean exists() {
		return false;
	}

	public void downloadMetadata() throws UnsupportedEncodingException, StorageException, IOException {
		this.downloadAttributes(true, false);
	}

	private void downloadAttributes(final boolean updateMetadata, final boolean updateApproximateMessageCount) throws UnsupportedEncodingException, StorageException, IOException {
		final CloudQueue queue = this;
		StorageOperation<Integer> storageOperation = new StorageOperation<Integer>() {
			public Integer execute() throws Exception {
				HttpHead request = QueueRequest.getProperties(queue.getUri());
				m_ServiceClient.getCredentials().signRequest(request, -1L);
				this.processRequest(request);

				if (updateMetadata)
				{
					m_Metadata.clear();
					m_Metadata.putAll(QueueResponse.getMetadata(result.httpResponse));
				}
				if (updateApproximateMessageCount)
				{
					String messageCount = result.httpResponse.getFirstHeader("x-ms-approximate-messages-count").getValue();
					m_ApproximateMessageCount = Integer.parseInt(messageCount);
				}
				switch (result.statusCode)
				{
					case HttpStatus.SC_OK:
						return null;
					default:
						throw new StorageInnerException("Couldn't fetch queue attributes");
				}
			}
		};

        storageOperation.executeTranslatingExceptions();
	}

	public void uploadMetadata() throws UnsupportedEncodingException, StorageException, IOException {
		final CloudQueue queue = this;
		StorageOperation<Void> storageOperation = new StorageOperation<Void>() {
			public Void execute() throws Exception {
				HttpPut request = QueueRequest.setMetadata(queue.getUri());
				QueueRequest.addMetadata(request, queue.m_Metadata);
				m_ServiceClient.getCredentials().signRequest(request, 0L);
				this.processRequest(request);

				switch (result.statusCode)
				{
					case HttpStatus.SC_NO_CONTENT:
						return null;
					default:
						throw new StorageInnerException("Couldn't upload queue metadata");
				}
			}
		};

        storageOperation.executeTranslatingExceptions();
	}

	public int downloadApproximateMessageCount() throws UnsupportedEncodingException, StorageException, IOException {
		this.downloadAttributes(false, true);
		return m_ApproximateMessageCount;
	}

	public void addMessage(CloudQueueMessage message) throws UnsupportedEncodingException, StorageException, IOException {
		this.addMessage(message, this.getDefaultTimeToLiveInSeconds());
	}

	private int getDefaultTimeToLiveInSeconds() {
		return CloudQueueMessage.MaxTimeToLiveInSeconds;
	}

	public void addMessage(final CloudQueueMessage message,
			final int timeToLiveInSeconds) throws UnsupportedEncodingException, StorageException, IOException {
		final CloudQueue queue = this;
		StorageOperation<Void> storageOperation = new StorageOperation<Void>() {
			public Void execute() throws Exception {
				HttpPost request = QueueRequest.addMessage(queue.getUri(), timeToLiveInSeconds, message);
				m_ServiceClient.getCredentials().signRequest(request, request.getEntity().getContentLength());
				this.processRequest(request);

				switch (result.statusCode)
				{
					case HttpStatus.SC_CREATED:
						return null;
					default:
						throw new StorageInnerException("Couldn't add queue message");
				}
			}
		};

        storageOperation.executeTranslatingExceptions();
	}

	public Iterable<CloudQueueMessage> getMessages(int messageCount) throws UnsupportedEncodingException, StorageException, IOException {
		return this.getMessages(messageCount, this.getDefaultVisibilityTimeoutInSeconds());
	}

	private int getDefaultVisibilityTimeoutInSeconds() {
		return 60;
	}

	public Iterable<CloudQueueMessage> getMessages(int messageCount,
			int visibilityTimeoutInSeconds) throws UnsupportedEncodingException, StorageException, IOException {
		return this.getOrPeekMessages(messageCount, false, visibilityTimeoutInSeconds);
	}

	public CloudQueueMessage getMessage() throws UnsupportedEncodingException, StorageException, IOException {
		return this.getMessages(1).iterator().next();
	}

	public CloudQueueMessage getMessage(int visibilityTimeoutInSeconds) throws UnsupportedEncodingException, StorageException, IOException {
		return this.getMessages(1, visibilityTimeoutInSeconds).iterator().next();
	}

	public CloudQueueMessage peekMessage() throws UnsupportedEncodingException, StorageException, IOException {
		return this.peekMessages(1).iterator().next();
	}

	public Iterable<CloudQueueMessage> peekMessages(final int messageCount) throws UnsupportedEncodingException, StorageException, IOException {
		return this.getOrPeekMessages(messageCount, true, 0);
	}
	
	public Iterable<CloudQueueMessage> getOrPeekMessages(final int messageCount, final boolean peekMessages, final int visibilityTimeoutInSeconds) throws UnsupportedEncodingException, StorageException, IOException {
		final CloudQueue queue = this;
		StorageOperation<Iterable<CloudQueueMessage>> storageOperation = new StorageOperation<Iterable<CloudQueueMessage>>() {
			public Iterable<CloudQueueMessage> execute() throws Exception {
				HttpGet request = QueueRequest.getMessages(queue.getUri(), messageCount, peekMessages, visibilityTimeoutInSeconds);
				m_ServiceClient.getCredentials().signRequest(request, -1L);
				this.processRequest(request);

				switch (result.statusCode)
				{
					case HttpStatus.SC_OK:
						return QueueResponse.getMessagesList(result.httpResponse.getEntity().getContent());
					default:
						throw new StorageInnerException("Couldn't peek queue messages");
				}
			}
		};

        return storageOperation.executeTranslatingExceptions();	
    }

	public void deleteMessage(CloudQueueMessage message) throws UnsupportedEncodingException, StorageException, IOException {
		this.deleteMessage(message.getId(), message.getPopReceipt());
	}

	public void deleteMessage(String messageId, final String popReceipt) throws UnsupportedEncodingException, StorageException, IOException {
		final CloudQueue queue = this;
		StorageOperation<Void> storageOperation = new StorageOperation<Void>() {
			public Void execute() throws Exception {
				HttpDelete request = QueueRequest.deleteMessage(queue.getUri(), popReceipt);
				m_ServiceClient.getCredentials().signRequest(request, -1L);
				this.processRequest(request);

				switch (result.statusCode)
				{
					case HttpStatus.SC_NO_CONTENT:
						return null;
					default:
						throw new StorageInnerException("Couldn't delete queue message");
				}
			}
		};

        storageOperation.executeTranslatingExceptions();	
	}

	public void clear() {
	}
}