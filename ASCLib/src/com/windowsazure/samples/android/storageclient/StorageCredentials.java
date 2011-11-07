package com.windowsazure.samples.android.storageclient;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.security.InvalidKeyException;
import java.util.HashMap;

import org.apache.http.client.methods.HttpRequestBase;

public abstract class StorageCredentials {

	protected static StorageCredentials tryParseCredentials(HashMap hashmap)
			throws InvalidKeyException, IllegalArgumentException,
			NotImplementedException {
		String s = hashmap.get("AccountName") == null ? null : (String) hashmap
				.get("AccountName");
		String s1 = hashmap.get("AccountKey") == null ? null : (String) hashmap
				.get("AccountKey");
		String s2 = hashmap.get("SharedAccessSignature") == null ? null
				: (String) hashmap.get("SharedAccessSignature");
		if (s != null && s1 != null && s2 == null)
			if (Base64.validateIsBase64String(s1))
				return new StorageCredentialsAccountAndKey(s, s1);
			else
				throw new InvalidKeyException(
						"Storage Key is not a valid base64 encoded string.");
		if (s == null && s1 == null && s2 != null)
			return new StorageCredentialsSharedAccessSignature(s2);
		else
			return null;
	}

	public static StorageCredentials tryParseCredentials(String s)
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

	public abstract String computeHmac256(String s) throws InvalidKeyException,
			NotImplementedException;

	public abstract String computeHmac512(String s) throws InvalidKeyException,
			NotImplementedException;

	public abstract String containerEndpointPostfix();

	abstract StorageCredentials credentialsForBlobOf(
			CloudBlobContainer cloudBlobContainer)
			throws IllegalArgumentException, UnsupportedEncodingException,
			NotImplementedException, URISyntaxException, StorageException,
			IOException;

	protected abstract boolean doCredentialsNeedTransformUri()
			throws NotImplementedException;

	public abstract String getAccountName();

	abstract AbstractBlobRequest getBlobRequest();

	abstract AbstractContainerRequest getContainerRequest();

	public abstract void signRequest(HttpRequestBase request, long l)
			throws NotImplementedException, InvalidKeyException,
			StorageException, MalformedURLException;

	public abstract void signRequestLite(HttpRequestBase request, long l)
			throws NotImplementedException, StorageException,
			InvalidKeyException;

	public abstract String toString(boolean boolean1);

	public abstract URI transformUri(URI uri) throws NotImplementedException,
			URISyntaxException, StorageException;
}
