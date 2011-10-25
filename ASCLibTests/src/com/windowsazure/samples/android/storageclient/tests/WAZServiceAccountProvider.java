package com.windowsazure.samples.android.storageclient.tests;

import java.net.URI;
import java.net.URISyntaxException;

import com.windowsazure.samples.android.storageclient.CloudBlobClient;
import com.windowsazure.samples.android.storageclient.wazservice.WAZServiceAccount;

public abstract class WAZServiceAccountProvider {

	public abstract URI getServiceHost() throws URISyntaxException;

	public WAZServiceAccount getAccount() throws URISyntaxException
	{
		return new WAZServiceAccount(WAZServiceUsernameAndPasswordProvider.getUsernameAndPassword(), getServiceHost());
	}

	public WAZServiceAccount getDifferentAccount() throws URISyntaxException
	{
		return new WAZServiceAccount(WAZServiceUsernameAndPasswordProvider.getDifferentUsernameAndPassword(), getServiceHost());
	}

	public CloudBlobClient getCloudBlobClient() throws URISyntaxException, Exception {
		return getAccount().createCloudBlobClient();
	}

	public CloudBlobClient getCloudBlobClientWithDifferentAccount() throws URISyntaxException, Exception {
		return getDifferentAccount().createCloudBlobClient();
	}
}
