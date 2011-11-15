package com.windowsazure.samples.android.storageclient;

import java.util.HashMap;

import android.net.Uri;

public class CloudQueue {
	public CloudQueue(String address, StorageCredentials credentials) {
	}

	public CloudQueueClient getServiceClient() {
		return null;
	}

	public String getName() {
		return null;
	}

	public Uri getUri() {
		return null;
	}

	public QueueAttributes getAttributes() {
		return null;
	}

	public HashMap<String, String> getMetadata() {
		return null;
	}

	public int getApproximateMessageCount() {
		return 0;
	}

	public boolean getEncodeMessage() {
		return false;
	}

	public boolean setEncodeMessage(boolean encodeMessage) {
		return false;
	}

	public void Create() {
	}

	public boolean CreateIfNotExist() {
		return false;
	}

	public void Delete() {
	}

	public boolean Exists() {
		return false;
	}

	public void FetchAttributes() {
	}

	public void SetMetadata() {
	}

	public int RetrieveApproximateMessageCount() {
		return 0;
	}

	public void AddMessage(CloudQueueMessage message) {
	}

	public void AddMessage(CloudQueueMessage message,
			int timeToLiveInMilliseconds) {
	}

	public Iterable<CloudQueueMessage> GetMessages(int messageCount) {
		return null;
	}

	public Iterable<CloudQueueMessage> GetMessages(int messageCount,
			int visibilityTimeoutInMilliseconds) {
		return null;
	}

	public CloudQueueMessage GetMessage() {
		return null;
	}

	public CloudQueueMessage GetMessage(int visibilityTimeoutInMilliseconds) {
		return null;
	}

	public CloudQueueMessage PeekMessage() {
		return null;
	}

	public Iterable<CloudQueueMessage> PeekMessages(int messageCount) {
		return null;
	}

	public void DeleteMessage(CloudQueueMessage message) {
	}

	public void DeleteMessage(String messageId, String popReceipt) {
	}

	public void Clear() {
	}
}