package com.windowsazure.samples.android.storageclient;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.security.InvalidKeyException;

import org.apache.http.client.methods.HttpRequestBase;

import android.util.Base64;

public final class StorageCredentialsAccountAndKey extends StorageCredentials {

	private Credentials m_Credentials;

	boolean equals(StorageCredentials rightCredentials)
	{
		return rightCredentials instanceof StorageCredentialsAccountAndKey
				&& this.equals((StorageCredentialsAccountAndKey) rightCredentials);
	}

	boolean equals(StorageCredentialsAccountAndKey rightCredentials)
	{
		return this.toString(true).equals(rightCredentials.toString(true));
	}

	public StorageCredentialsAccountAndKey(String accountName, byte key[])
	{
		m_Credentials = new Credentials(accountName, key);
	}

	public StorageCredentialsAccountAndKey(String accountName, String key)
			throws IllegalArgumentException {
		this(accountName, Base64.decode(key, Base64.NO_WRAP));
	}

	@Override
	protected boolean canCredentialsComputeHmac() {
		return true;
	}

	@Override
	protected boolean canCredentialsSignRequest() {
		return true;
	}

	@Override
	protected boolean canCredentialsSignRequestLite() {
		return true;
	}

	@Override
	public String computeHmac256(String string) throws InvalidKeyException,
			IllegalArgumentException {
		return StorageKey.computeMacSha256(m_Credentials.getKey(), string);
	}

	@Override
	public String computeHmac512(String string) throws InvalidKeyException,
			IllegalArgumentException {
		return StorageKey.computeMacSha512(m_Credentials.getKey(), string);
	}

	@Override
	public String containerEndpointPostfix() {
		return "";
	}

	@Override
	StorageCredentials credentialsForBlobOf(
			CloudBlobContainer container)
			throws IllegalArgumentException, UnsupportedEncodingException,
			URISyntaxException, StorageException,
			IOException {
		return this;
	}

	@Override
	protected boolean doCredentialsNeedTransformUri() {
		return false;
	}

	@Override
	public String getAccountName() {
		return m_Credentials.getAccountName();
	}

	protected String getBase64EncodedKey() throws NotImplementedException {
		return m_Credentials.getKey().getBase64EncodedKey();
	}

	@Override
	AbstractBlobRequest getBlobRequest() {
		return new BlobRequest();
	}

	@Override
	AbstractContainerRequest getContainerRequest() {
		return new ContainerRequest();
	}

	public Credentials getCredentials() {
		return m_Credentials;
	}

	public void setCredentials(Credentials credentials) {
		m_Credentials = credentials;
	}

	@Override
	public void signRequest(HttpRequestBase request, long length)
			throws InvalidKeyException, StorageException, MalformedURLException {
		BaseRequest.signRequestForBlobAndQueue(request, m_Credentials,
				length);
	}

	@Override
	public void signRequestLite(HttpRequestBase request, long length)
			throws StorageException, InvalidKeyException,
			NotImplementedException {
		// BaseRequest.signRequestForBlobAndQueueSharedKeyLite(httpurlconnection,
		// m_Credentials, Long.valueOf(l));
		throw new NotImplementedException();
	}

	@Override
	public String toString(boolean showKey) {
		return String.format("%s=%s;%s=%s", new Object[] {
				"AccountName",
				getAccountName(),
				"AccountKey",
				showKey ? m_Credentials.getKey()
						.getBase64EncodedKey() : "[key hidden]" });
	}

	@Override
	public URI transformUri(URI uri) {
		return uri;
	}
}
