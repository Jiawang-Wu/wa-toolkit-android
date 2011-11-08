package com.windowsazure.samples.android.storageclient.tests;

import java.net.URISyntaxException;

import com.windowsazure.samples.android.storageclient.CloudClientAccount;
import com.windowsazure.samples.android.storageclient.CloudStorageAccount;
import com.windowsazure.samples.android.storageclient.NotImplementedException;
import com.windowsazure.samples.android.storageclient.StorageCredentialsAccountAndKey;

public class CloudStorageAccountProvider extends CloudClientAccountProvider {

	private static final String ACCOUNT = "panthro";
	private static final String ACCESS_KEY = "Z1YR54ga4/Ne8VERxx9b2C3ICnZbci+bZR+48wdBlppqQg70snYeVq3vIARhlNULUzdiT88XsggioR/aDaAjNQ==";

	@Override
	public CloudClientAccount getAccount() throws URISyntaxException,
			NotImplementedException {
		return new CloudStorageAccount(new StorageCredentialsAccountAndKey(
				ACCOUNT, ACCESS_KEY));
	}

	@Override
	public CloudClientAccount getDifferentAccount() throws URISyntaxException,
			NotImplementedException {
		return this.getAccount();
	}

}
