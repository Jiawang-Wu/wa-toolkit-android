package com.windowsazure.samples.android.storageclient;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URISyntaxException;

import org.apache.http.HttpStatus;
import org.apache.http.client.methods.HttpGet;

public class CloudQueueClient {
	
	private URI m_BaseUri;
	private StorageCredentials m_Credentials;

	/**
	Initializes a new instance of the CloudQueueClient class using the specified Queue service endpoint and account credentials.
	@param endpoint The base URI used to construct the Queue service client.
	@param credentials The account credentials used to create the Queue service client.
	@see StorageCredentials 
	*/
	public CloudQueueClient(String endpoint, StorageCredentials credentials) throws URISyntaxException {
		this(new URI(endpoint), credentials);
	}

	/**
	Initializes a new instance of the CloudQueueClient class using the specified Queue service endpoint and account credentials.
	@param endpoint The base URI used to construct the Queue service client.
	@param credentials The account credentials used to create the Queue service client.
	@see StorageCredentials 
	*/
	public CloudQueueClient(URI endpoint, StorageCredentials credentials) throws URISyntaxException {
		m_BaseUri = endpoint;
		m_Credentials = credentials;
	}

	/**
	Initializes a new instance of the CloudQueueClient class using the default Queue service endpoint and the specified account credentials.
	@param credentials The account credentials used to create the Queue service client.
	@see StorageCredentials 
	*/
	public CloudQueueClient(StorageCredentials credentials) throws URISyntaxException {
		this(CloudStorageAccount.getDefaultQueueEndpoint(CloudStorageAccount.getDefaultScheme(),
				credentials.getAccountName()), credentials);
	}

	/**
	Gets the account credentials used to create the Queue service client.
	@return StorageCredentials 
	@see StorageCredentials 
	*/
	public StorageCredentials getCredentials() {
		return m_Credentials;

	}

	/**
	The base URI used to construct the Queue service client.
	@return URI 
	*/
	public URI getBaseUri() {
		return m_BaseUri;
	}

	/**
	Gets a reference to the queue at the specified address.
	@param queueAddress the address referencing the queue to be get
	@return CloudQueue 
	@see CloudQueue 
	*/
	public CloudQueue getQueueReference(String queueAddress) throws URISyntaxException {
		return new CloudQueue(queueAddress, this);
	}

	/**
	Returns an enumerable collection of the queues in the storage account.
	@return Iterable<CloudQueue> 
	@see CloudQueue 
	*/
	public Iterable<CloudQueue> listQueues() throws UnsupportedEncodingException, StorageException, IOException {
		return this.listQueues(null);
	}

	/**
	Returns an enumerable collection of the queues in the storage account whose names begin with the specified prefix.
	@param prefix prefix that will be used to filter which queues are returned
	@return Iterable<CloudQueue> 
	@see CloudQueue 
	*/
	public Iterable<CloudQueue> listQueues(String prefix) throws UnsupportedEncodingException, StorageException, IOException {
		return this.listQueues(prefix, QueueListingDetails.All);
	}

	/**
	Returns an enumerable collection of the queues in the storage account whose names begin with the specified prefix.
	@param prefix prefix that will be used to filter which queues are returned
	@param detailsIncluded controls which details of the queues listed will be retrieved
	@return Iterable<CloudQueue> 
	@see CloudQueue 
	*/
	public Iterable<CloudQueue> listQueues(final String prefix,
			final QueueListingDetails detailsIncluded) throws UnsupportedEncodingException, StorageException, IOException {
		final CloudQueueClient client = this;
		StorageOperation<Iterable<CloudQueue>> storageOperation = new StorageOperation<Iterable<CloudQueue>>() {
			public Iterable<CloudQueue> execute() throws Exception {
				HttpGet request = QueueRequest.list(client.getBaseUri(), prefix, detailsIncluded);
				client.getCredentials().signRequest(request, -1L);
				this.processRequest(request);
				
				if (result.statusCode != HttpStatus.SC_OK) {
					throw new StorageInnerException(
							"Couldn't list queues");
				}

				return QueueResponse.getList(result.httpResponse.getEntity().getContent(), client);
			}
		};

        return storageOperation.executeTranslatingExceptions();
	}
	
}
