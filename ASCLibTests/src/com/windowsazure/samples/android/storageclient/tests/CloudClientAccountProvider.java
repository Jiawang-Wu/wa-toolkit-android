package com.windowsazure.samples.android.storageclient.tests;

import java.net.URI;
import java.net.URISyntaxException;

import com.windowsazure.samples.android.storageclient.CloudBlobClient;
import com.windowsazure.samples.android.storageclient.CloudClientAccount;
import com.windowsazure.samples.android.storageclient.NotImplementedException;
import com.windowsazure.samples.android.storageclient.wazservice.WAZServiceAccount;

public abstract class CloudClientAccountProvider {

	public abstract CloudClientAccount getAccount() throws URISyntaxException, NotImplementedException;
	
	public abstract CloudClientAccount getDifferentAccount() throws URISyntaxException, NotImplementedException;

	public CloudBlobClient getCloudBlobClient() throws URISyntaxException, Exception {
		return getAccount().createCloudBlobClient();
	}
}
