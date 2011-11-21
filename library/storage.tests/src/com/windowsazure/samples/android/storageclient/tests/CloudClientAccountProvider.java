package com.windowsazure.samples.android.storageclient.tests;

import java.net.URISyntaxException;

import com.windowsazure.samples.android.storageclient.CloudBlobClient;
import com.windowsazure.samples.android.storageclient.CloudClientAccount;
import com.windowsazure.samples.android.storageclient.CloudQueueClient;
import com.windowsazure.samples.android.storageclient.CloudTableClient;
import com.windowsazure.samples.android.storageclient.NotImplementedException;

public abstract class CloudClientAccountProvider {

	public abstract CloudClientAccount getAccount() throws URISyntaxException,
			NotImplementedException;

	public abstract CloudClientAccount getDifferentAccount()
			throws URISyntaxException;

	public CloudBlobClient getCloudBlobClient() throws URISyntaxException,
			Exception {
		return getAccount().createCloudBlobClient();
	}

	public CloudQueueClient getCloudQueueClient() throws URISyntaxException,
	Exception {
		return getAccount().createCloudQueueClient();
	}

	public CloudTableClient getCloudTableClient() throws Exception {
		return getAccount().createCloudTableClient();
	}
	
}
