package com.windowsazure.samples.android.storageclient.tests;

import java.net.URISyntaxException;

import com.windowsazure.samples.android.storageclient.CloudClientAccount;
import com.windowsazure.samples.android.storageclient.CloudStorageAccount;
import com.windowsazure.samples.android.storageclient.NotImplementedException;
import com.windowsazure.samples.android.storageclient.StorageCredentialsAccountAndKey;

public class CloudStorageAccountProvider extends CloudClientAccountProvider {

	private static final String Account = "wazmobilesdk";
	private static final String Key = "0X2emKMok0j/OS+EpkEUdXTMzaB3rTyK2xrjhIn7za8l8o6BGo8YAiUHFnCdiQNgszBOZAOUVXVyT06Fuwt88g==";

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
