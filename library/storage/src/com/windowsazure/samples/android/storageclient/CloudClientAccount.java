package com.windowsazure.samples.android.storageclient;

public interface CloudClientAccount {
	CloudBlobClient createCloudBlobClient() throws Exception;
	CloudQueueClient createCloudQueueClient() throws Exception;
	CloudTableClient createCloudTableClient() throws Exception;
}
