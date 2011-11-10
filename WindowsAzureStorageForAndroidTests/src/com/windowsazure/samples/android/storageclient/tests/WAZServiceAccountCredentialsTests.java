package com.windowsazure.samples.android.storageclient.tests;

import java.net.URI;
import java.net.UnknownHostException;

import javax.security.auth.login.LoginException;

import com.windowsazure.samples.android.storageclient.CloudBlobClient;
import com.windowsazure.samples.android.storageclient.wazservice.WAZServiceAccount;
import com.windowsazure.samples.android.storageclient.wazservice.WAZServiceUsernameAndPassword;

public abstract class WAZServiceAccountCredentialsTests<T extends WAZServiceAccountProvider>
		extends TestCase {

	private T accountProvider = SuperClassTypeParameterCreator.create(this, 0);

	public void testLoginWithNonExistantUsernameThrowsException()
			throws Exception {
		final WAZServiceAccount account = new WAZServiceAccount(
				new WAZServiceUsernameAndPassword("NonExistentUsername",
						WAZServiceUsernameAndPasswordProvider.PROXY_PASSWORD),
				accountProvider.getServiceHost());

		this.assertThrows(new RunnableWithExpectedException() {
			@Override
			public void run() throws Exception {
				account.createCloudBlobClient();
			}
		}, LoginException.class);
	}

	public void testLoginWithInvalidPasswordThrowsException() throws Exception {
		final WAZServiceAccount account = new WAZServiceAccount(
				new WAZServiceUsernameAndPassword(
						WAZServiceUsernameAndPasswordProvider.PROXY_USERNAME,
						"InvalidPassword"), accountProvider.getServiceHost());

		this.assertThrows(new RunnableWithExpectedException() {
			@Override
			public void run() throws Exception {
				account.createCloudBlobClient();
			}
		}, LoginException.class);
	}

	public void testLoginToInvalidSiteThrowsException() throws Exception {
		final WAZServiceAccount account = new WAZServiceAccount(
				WAZServiceUsernameAndPasswordProvider.getUsernameAndPassword(),
				new URI("https://www.microsoft.com"));

		this.assertThrows(new RunnableWithExpectedException() {
			@Override
			public void run() throws Exception {
				account.createCloudBlobClient();
			}
		}, LoginException.class);
	}

	public void testLoginToInexistentSiteThrowsException() throws Exception {
		final WAZServiceAccount account = new WAZServiceAccount(
				WAZServiceUsernameAndPasswordProvider.getUsernameAndPassword(),
				new URI("https://www.site.that.doesnt.exist.com.iDontExist"));

		this.assertThrows(new RunnableWithExpectedException() {
			@Override
			public void run() throws Exception {
				account.createCloudBlobClient();
			}
		}, UnknownHostException.class);
	}

	public void testCloudBlobClientCreationUsingWAZServiceIsSuccessful()
			throws Exception {
		CloudBlobClient cloudBlobClient = accountProvider.getAccount()
				.createCloudBlobClient();
	}
}
