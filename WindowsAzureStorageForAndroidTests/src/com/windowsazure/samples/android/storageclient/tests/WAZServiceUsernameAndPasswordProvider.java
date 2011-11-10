package com.windowsazure.samples.android.storageclient.tests;

import com.windowsazure.samples.android.storageclient.wazservice.WAZServiceUsernameAndPassword;

public class WAZServiceUsernameAndPasswordProvider {
	public static final String PROXY_USERNAME = "sguest";
	public static final String PROXY_PASSWORD = "s1m0n1";
	public static final String PROXY_OTHER_USERNAME = "sguest2";
	public static final String PROXY_OTHER_PASSWORD = "s1m0n2";

	public static WAZServiceUsernameAndPassword getUsernameAndPassword() {
		return new WAZServiceUsernameAndPassword(PROXY_USERNAME, PROXY_PASSWORD);
	}

	public static WAZServiceUsernameAndPassword getDifferentUsernameAndPassword() {
		return new WAZServiceUsernameAndPassword(PROXY_OTHER_USERNAME,
				PROXY_OTHER_PASSWORD);
	}
}
