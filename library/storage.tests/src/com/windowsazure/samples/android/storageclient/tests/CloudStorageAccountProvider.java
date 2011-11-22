package com.windowsazure.samples.android.storageclient.tests;

import java.net.URISyntaxException;

import com.windowsazure.samples.android.storageclient.CloudClientAccount;
import com.windowsazure.samples.android.storageclient.CloudStorageAccount;
import com.windowsazure.samples.android.storageclient.StorageCredentialsAccountAndKey;

public class CloudStorageAccountProvider extends CloudClientAccountProvider {

	private static final String ACCOUNT = "panthro";
	private static final String ACCESS_KEY = "9r3qbPdSzEzp/CQsVm64eY3ntmgJoeVkdeGuI1qJH4xv+JcJiFDFo5aKcinaA9oPdoUzwsHEO17IA8lwZZHGMA==";

	@Override
	public CloudClientAccount getAccount() throws URISyntaxException {
		return new CloudStorageAccount(new StorageCredentialsAccountAndKey(
				ACCOUNT, ACCESS_KEY));
	}

	@Override
	public CloudClientAccount getDifferentAccount() throws URISyntaxException {
		return this.getAccount();
	}

}
