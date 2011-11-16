package com.windowsazure.samples.android.storageclient.tests;

import java.net.URISyntaxException;

import com.windowsazure.samples.android.storageclient.CloudClientAccount;
import com.windowsazure.samples.android.storageclient.CloudStorageAccount;
import com.windowsazure.samples.android.storageclient.StorageCredentialsAccountAndKey;

public class CloudStorageAccountProvider extends CloudClientAccountProvider {

	private static final String ACCOUNT = "account";
	private static final String ACCESS_KEY = "key";

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
