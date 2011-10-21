package com.windowsazure.samples.android.storageclient.tests;

import java.net.URI;
import java.net.URISyntaxException;

import com.windowsazure.samples.android.storageclient.CloudBlobClient;
import com.windowsazure.samples.android.storageclient.wazservice.WAZServiceAccount;
import com.windowsazure.samples.android.storageclient.wazservice.WAZServiceUsernameAndPassword;

public class WAZServiceTestingAccount {
	public static final String PROXY_HOST_STRING = "https://wazmobiletoolkit.cloudapp.net:443";
	//public static final String PROXY_HOST_STRING = "http://wazmobiletoolkit.cloudapp.net:10080";
	//public static final String PROXY_HOST_STRING = "http://192.168.129.28:10080";
	//public static final String PROXY_HOST_STRING = "https://192.168.129.28";
	public static final String PROXY_PASSWORD = "s1m0n1";
	public static final String PROXY_USERNAME = "sguest";
	public static final String PROXY_OTHER_PASSWORD = "s1m0n2";
	public static final String PROXY_OTHER_USERNAME = "sguest2";

	public static WAZServiceUsernameAndPassword getUsernameAndPassword()
	{
		return new WAZServiceUsernameAndPassword(PROXY_USERNAME, PROXY_PASSWORD);
	}
	
	public static WAZServiceUsernameAndPassword getDifferentUsernameAndPassword()
	{
		return new WAZServiceUsernameAndPassword(PROXY_OTHER_USERNAME, PROXY_OTHER_PASSWORD);
	}

	public static WAZServiceAccount getAccount() throws URISyntaxException
	{
		return new WAZServiceAccount(getUsernameAndPassword(), getServiceHost());
	}

	public static WAZServiceAccount getDifferentAccount() throws URISyntaxException
	{
		return new WAZServiceAccount(getDifferentUsernameAndPassword(), getServiceHost());
	}

	public static URI getServiceHost() throws URISyntaxException {
		return new URI(PROXY_HOST_STRING);
	}

	public static CloudBlobClient getCloudBlobClient() throws URISyntaxException, Exception {
		return getAccount().createCloudBlobClient();
	}

	public static CloudBlobClient getCloudBlobClientWithDifferentAccount() throws URISyntaxException, Exception {
		return getDifferentAccount().createCloudBlobClient();
	}
}
