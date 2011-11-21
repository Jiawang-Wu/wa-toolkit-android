package com.windowsazure.samples.android.storageclient.tests;

import java.net.URI;
import java.net.URISyntaxException;

public class WAZServiceHttpsAccountProvider extends WAZServiceAccountProvider {

	public static final String PROXY_HOST_STRING = "https://panthro.cloudapp.net";

	// public static final String PROXY_HOST_STRING = "https://192.168.129.44";

	@Override
	public URI getServiceHost() throws URISyntaxException {
		return new URI(PROXY_HOST_STRING);
	}
}
