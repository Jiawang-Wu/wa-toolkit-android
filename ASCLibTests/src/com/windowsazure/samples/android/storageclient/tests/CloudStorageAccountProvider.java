package com.windowsazure.samples.android.storageclient.tests;

import java.net.URISyntaxException;

import com.windowsazure.samples.android.storageclient.CloudClientAccount;
import com.windowsazure.samples.android.storageclient.CloudStorageAccount;
import com.windowsazure.samples.android.storageclient.NotImplementedException;
import com.windowsazure.samples.android.storageclient.StorageCredentialsAccountAndKey;

public class CloudStorageAccountProvider extends CloudClientAccountProvider {

	private static final String Account = "Account";
	private static final String Key = "Key";

	@Override
	public CloudClientAccount getAccount() throws URISyntaxException, NotImplementedException {
		return new CloudStorageAccount(new StorageCredentialsAccountAndKey(Account, Key));
	}

	@Override
	public CloudClientAccount getDifferentAccount() throws URISyntaxException, NotImplementedException 
	{
		return this.getAccount();
	}

}
