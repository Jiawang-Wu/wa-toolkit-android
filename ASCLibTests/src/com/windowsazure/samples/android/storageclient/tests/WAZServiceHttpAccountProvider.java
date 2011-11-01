package com.windowsazure.samples.android.storageclient.tests;

import java.net.URI;
import java.net.URISyntaxException;

import com.windowsazure.samples.android.storageclient.wazservice.WAZServiceAccount;

public class WAZServiceHttpAccountProvider extends WAZServiceAccountProvider {

	public static final String PROXY_HOST_STRING = "http://wazmobiletoolkit.cloudapp.net:10080";
	//public static final String PROXY_HOST_STRING = "http://192.168.129.44:10080";

	@Override
	public URI getServiceHost() throws URISyntaxException {
		return new URI(PROXY_HOST_STRING);
	}
}
