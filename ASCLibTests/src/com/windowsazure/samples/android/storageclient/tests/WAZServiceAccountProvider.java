package com.windowsazure.samples.android.storageclient.tests;

import java.net.URI;
import java.net.URISyntaxException;

import com.windowsazure.samples.android.storageclient.CloudBlobClient;
import com.windowsazure.samples.android.storageclient.CloudClientAccount;
import com.windowsazure.samples.android.storageclient.wazservice.WAZServiceAccount;

public abstract class WAZServiceAccountProvider extends CloudClientAccountProvider {

	public abstract URI getServiceHost() throws URISyntaxException;

	public CloudClientAccount getAccount() throws URISyntaxException
	{
		return new WAZServiceAccount(WAZServiceUsernameAndPasswordProvider.getUsernameAndPassword(), getServiceHost());
	}

	public CloudClientAccount getDifferentAccount() throws URISyntaxException
	{
		return new WAZServiceAccount(WAZServiceUsernameAndPasswordProvider.getDifferentUsernameAndPassword(), getServiceHost());
	}
}
