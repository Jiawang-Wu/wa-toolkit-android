package com.windowsazure.samples.android.storageclient.tests;

import java.net.URI;
import java.net.URISyntaxException;

import com.windowsazure.samples.android.storageclient.wazservice.WAZServiceAccount;
import com.windowsazure.samples.android.storageclient.wazservice.WAZServiceUsernameAndPassword;

public class WAZServiceTestingAccount {
	//public static final String PROXY_HOST_STRING = "https://wazmobiletoolkit.cloudapp.net";
	public static final String PROXY_HOST_STRING = "http://192.168.129.28:10080";
	public static final String PROXY_PASSWORD = "s1m0n1";
	public static final String PROXY_USERNAME = "sguest";

	public static WAZServiceUsernameAndPassword getUsernameAndPassword()
	{
		return new WAZServiceUsernameAndPassword(PROXY_USERNAME, PROXY_PASSWORD);
	}
	
	public static WAZServiceAccount getAccount() throws URISyntaxException
	{
		return new WAZServiceAccount(getUsernameAndPassword(), getServiceHost());
	}

	public static URI getServiceHost() throws URISyntaxException {
		return new URI(PROXY_HOST_STRING);
	}
}
