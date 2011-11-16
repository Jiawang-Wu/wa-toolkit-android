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

	public CloudQueueClient(String baseAddress, StorageCredentials credentials) throws URISyntaxException {
		this(new URI(baseAddress), credentials);
	}
	public CloudQueueClient(URI baseAddress, StorageCredentials credentials) throws URISyntaxException {
		m_BaseUri = baseAddress;
		m_Credentials = credentials;
	}
	public CloudQueueClient(StorageCredentials credentials) throws URISyntaxException {
		this(CloudStorageAccount.getDefaultQueueEndpoint(CloudStorageAccount.getDefaultScheme(),
				credentials.getAccountName()), credentials);
	}
	public StorageCredentials getCredentials() {
		return m_Credentials;

	}

	public URI getBaseUri() {
		return m_BaseUri;
	}

	public CloudQueue getQueueReference(String queueAddress) throws URISyntaxException {
		return new CloudQueue(queueAddress, this);
	}

	public Iterable<CloudQueue> listQueues() throws UnsupportedEncodingException, StorageException, IOException {
		return this.listQueues(null);
	}

	public Iterable<CloudQueue> listQueues(String prefix) throws UnsupportedEncodingException, StorageException, IOException {
		return this.listQueues(prefix, QueueListingDetails.All);
	}

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
