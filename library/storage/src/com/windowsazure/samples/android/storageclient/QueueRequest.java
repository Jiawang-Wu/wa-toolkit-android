package com.windowsazure.samples.android.storageclient;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Map;

import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpHead;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.entity.ByteArrayEntity;

public class QueueRequest {

	public static HttpPut create(URI uri) throws IllegalArgumentException, IOException, URISyntaxException, StorageException {
		return BaseRequest.create(uri, null);
	}

	public static void addMetadata(HttpPut request,
			Map<String, String> metadata) {
		BaseRequest.addMetadata(request, metadata);
	}

	public static HttpGet list(URI baseUri, String prefix, QueueListingDetails detailsIncluded) throws IOException, URISyntaxException, StorageException {
		UriQueryBuilder uriQueryBuilder = new UriQueryBuilder();
		uriQueryBuilder.add("comp", "list");
		if (prefix != null)
		{
			uriQueryBuilder.add("prefix", prefix);
		}
		if (detailsIncluded.equals(QueueListingDetails.Metadata))
		{
			uriQueryBuilder.add("include", "metadata");
		}
		return BaseRequest.setURIAndHeaders(new HttpGet(), baseUri, uriQueryBuilder);
	}

	public static HttpDelete delete(URI uri) throws IOException, URISyntaxException, StorageException {
		return BaseRequest.delete(uri, null);
	}

	public static HttpHead getProperties(URI uri) throws IllegalArgumentException, StorageException, IOException, URISyntaxException {
		UriQueryBuilder uriQueryBuilder = new UriQueryBuilder();
		uriQueryBuilder.add("comp", "metadata");
		return BaseRequest.getProperties(uri, uriQueryBuilder);
	}

	public static HttpPut setMetadata(URI uri) throws IOException, URISyntaxException, StorageException {
		return BaseRequest.setMetadata(uri, null);
	}

	public static HttpPost addMessage(URI uri, int timeToLiveInMilliseconds, CloudQueueMessage message) throws IOException, URISyntaxException, StorageException {
		UriQueryBuilder uriQueryBuilder = new UriQueryBuilder();
		uriQueryBuilder.add("messagettl", "" + timeToLiveInMilliseconds);
		
		HttpPost request = BaseRequest.setURIAndHeaders(new HttpPost(), queueMessagesUri(uri), uriQueryBuilder);
		
		String requestBody = String.format("<QueueMessage>\n<MessageText>%s</MessageText>\n</QueueMessage>", message.getRawContent());
		request.setEntity(new ByteArrayEntity(requestBody.getBytes()));
		
		return request;
	}

	private static URI queueMessagesUri(URI uri) throws URISyntaxException {
		return PathUtility.appendPathToUri(uri, "messages");
	}

	public static HttpGet getMessages(URI uri, int messageCount, boolean peekMessages, int visibilityTimeoutInSeconds) throws IOException, URISyntaxException, StorageException {
		UriQueryBuilder uriQueryBuilder = new UriQueryBuilder();
		
		if (peekMessages)
		{
			uriQueryBuilder.add("peekonly", "true");
		}
		else
		{
			uriQueryBuilder.add("visibilitytimeout", "" + visibilityTimeoutInSeconds);
		}

		if (messageCount != 1)
		{
			uriQueryBuilder.add("numofmessages", "" + messageCount);
		}
		
		return BaseRequest.setURIAndHeaders(new HttpGet(), queueMessagesUri(uri), uriQueryBuilder);
	}

	public static HttpDelete deleteMessage(URI uri, String popReceipt) throws IOException, URISyntaxException, StorageException {
		UriQueryBuilder uriQueryBuilder = new UriQueryBuilder();
		uriQueryBuilder.add("popreceipt", popReceipt);
		return BaseRequest.delete(queueMessagesUri(uri), uriQueryBuilder);
	}
}
