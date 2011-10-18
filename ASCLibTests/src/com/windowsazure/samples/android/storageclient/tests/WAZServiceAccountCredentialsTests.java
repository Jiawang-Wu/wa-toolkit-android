package com.windowsazure.samples.android.storageclient.tests;

import java.net.URI;
import java.net.UnknownHostException;

import javax.security.auth.login.LoginException;

import junit.framework.Assert;

import com.windowsazure.samples.android.storageclient.CloudBlobClient;
import com.windowsazure.samples.android.storageclient.wazservice.WAZServiceAccount;
import com.windowsazure.samples.android.storageclient.wazservice.WAZServiceUsernameAndPassword;

public class WAZServiceAccountCredentialsTests extends TestCase {
	public void testAuthentificationTokenIsValidAfterLogin() throws Exception {
		String token = WAZServiceTestingAccount.getAccount()
				.loginToWAZService();
		Assert.assertTrue(token != "");
	}

	public void testLoginWithNonExistantUsernameThrowsException()
			throws Exception {
		final WAZServiceAccount account = new WAZServiceAccount(
				new WAZServiceUsernameAndPassword("NonExistentUsername",
						WAZServiceTestingAccount.PROXY_PASSWORD),
				WAZServiceTestingAccount.getServiceHost());

		this.assertThrows(new ExpectedExceptionRunnable() {
			@Override
			public void run() throws Exception {
				CloudBlobClient token = account.createCloudBlobClient();
			}
		}, LoginException.class);
	}

	public void testLoginWithInvalidPasswordThrowsException() throws Exception {
		final WAZServiceAccount account = new WAZServiceAccount(
				new WAZServiceUsernameAndPassword(
						WAZServiceTestingAccount.PROXY_USERNAME,
						"InvalidPassword"),
				WAZServiceTestingAccount.getServiceHost());

		this.assertThrows(new ExpectedExceptionRunnable() {
			@Override
			public void run() throws Exception {
				CloudBlobClient token = account.createCloudBlobClient();
			}
		}, LoginException.class);
	}

	public void testLoginToInvalidSiteThrowsException() throws Exception {
		final WAZServiceAccount account = new WAZServiceAccount(
				WAZServiceTestingAccount.getUsernameAndPassword(), new URI(
						"https://www.microsoft.com"));

		this.assertThrows(new ExpectedExceptionRunnable() {
			@Override
			public void run() throws Exception {
				CloudBlobClient token = account.createCloudBlobClient();
			}
		}, LoginException.class);
	}

	public void testLoginToInexistentSiteThrowsException() throws Exception {
		final WAZServiceAccount account = new WAZServiceAccount(
				WAZServiceTestingAccount.getUsernameAndPassword(), new URI(
						"https://www.site.that.doesnt.exist.com.iDontExist"));

		this.assertThrows(new ExpectedExceptionRunnable() {
			@Override
			public void run() throws Exception {
				CloudBlobClient token = account.createCloudBlobClient();
			}
		}, UnknownHostException.class);
	}

	public void testCloudBlobClientCreationUsingWAZServiceIsSuccessful()
			throws Exception {
		CloudBlobClient cloudBlobClient = WAZServiceTestingAccount.getAccount()
				.createCloudBlobClient();
	}
}
