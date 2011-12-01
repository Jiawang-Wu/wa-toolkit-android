package com.windowsazure.samples.android.storageclient;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.security.InvalidKeyException;
import java.util.HashMap;

import org.apache.http.client.methods.HttpRequestBase;

import android.util.Base64;

public abstract class StorageCredentials {

	protected static StorageCredentials tryParseCredentials(HashMap<String, String> configuration)
			throws InvalidKeyException, IllegalArgumentException,
			NotImplementedException {
		String accountName = configuration.get("AccountName") == null ? null : (String) configuration
				.get("AccountName");
		String accountKey = configuration.get("AccountKey") == null ? null : (String) configuration
				.get("AccountKey");
		String sas = configuration.get("SharedAccessSignature") == null ? null
				: (String) configuration.get("SharedAccessSignature");
		if (accountName != null && accountKey != null && sas == null)
			if (validateIsBase64String(accountKey))
				return new StorageCredentialsAccountAndKey(accountName, accountKey);
			else
				throw new InvalidKeyException(
						"Storage Key is not a valid base64 encoded string.");
		if (accountName == null && accountKey == null && sas != null)
			return new StorageCredentialsSharedAccessSignature(sas);
		else
			return null;
	}

	abstract boolean equals(StorageCredentials rightCredentials);

	private static boolean validateIsBase64String(String base64String) {
		try {
			return Base64.decode(base64String, Base64.NO_WRAP) != null;
		}
		catch (Exception exception) {
			return false;
		}
	}

	public static StorageCredentials tryParseCredentials(String configurationString)
			throws NotImplementedException, InvalidKeyException,
			StorageException {
		throw new NotImplementedException();
	}

	public StorageCredentials() {
	}

	protected abstract boolean canCredentialsComputeHmac()
			throws NotImplementedException;

	protected abstract boolean canCredentialsSignRequest()
			throws NotImplementedException;

	protected abstract boolean canCredentialsSignRequestLite()
			throws NotImplementedException;

	public abstract String computeHmac256(String string) throws InvalidKeyException,
			NotImplementedException;

	public abstract String computeHmac512(String string) throws InvalidKeyException,
			NotImplementedException;

	public abstract String containerEndpointPostfix();

	abstract StorageCredentials credentialsForBlobOf(
			CloudBlobContainer container)
			throws IllegalArgumentException, UnsupportedEncodingException,
			URISyntaxException, StorageException,
			IOException;

	protected abstract boolean doCredentialsNeedTransformUri();

	public abstract String getAccountName();

	abstract AbstractBlobRequest getBlobRequest();

	abstract AbstractContainerRequest getContainerRequest();

	public abstract void signRequest(HttpRequestBase request, long length)
			throws NotImplementedException, InvalidKeyException,
			StorageException, MalformedURLException;

	public abstract void signRequestLite(HttpRequestBase request, long length)
			throws NotImplementedException, StorageException,
			InvalidKeyException;

	public abstract void signTableRequest(HttpRequestBase request)
			throws InvalidKeyException, MalformedURLException, IllegalArgumentException, StorageException;

	public abstract void signTableRequestLite(HttpRequestBase request);

	public abstract String toString(boolean showSignature);

	public abstract URI transformUri(URI uri) throws URISyntaxException, StorageException;
}
