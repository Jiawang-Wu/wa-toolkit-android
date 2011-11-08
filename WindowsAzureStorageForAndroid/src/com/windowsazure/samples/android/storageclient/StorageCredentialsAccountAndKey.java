package com.windowsazure.samples.android.storageclient;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.security.InvalidKeyException;

import org.apache.http.client.methods.HttpRequestBase;

public final class StorageCredentialsAccountAndKey extends StorageCredentials {

	private Credentials m_Credentials;

	public StorageCredentialsAccountAndKey(String s, byte abyte0[])
			throws NotImplementedException {
		m_Credentials = new Credentials(s, abyte0);
	}

	public StorageCredentialsAccountAndKey(String s, String s1)
			throws IllegalArgumentException, NotImplementedException {
		this(s, Base64.decode(s1));
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
	public String computeHmac256(String text) throws InvalidKeyException,
			IllegalArgumentException {
		return StorageKey.computeMacSha256(m_Credentials.getKey(), text);
	}

	@Override
	public String computeHmac512(String text) throws InvalidKeyException,
			IllegalArgumentException {
		return StorageKey.computeMacSha512(m_Credentials.getKey(), text);
	}

	@Override
	public String containerEndpointPostfix() {
		return "";
	}

	@Override
	StorageCredentials credentialsForBlobOf(
			CloudBlobContainer cloudBlobContainer)
			throws IllegalArgumentException, UnsupportedEncodingException,
			NotImplementedException, URISyntaxException, StorageException,
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
	public void signRequest(HttpRequestBase request, long contentLength)
			throws InvalidKeyException, StorageException, MalformedURLException {
		BaseRequest.signRequestForBlobAndQueue(request, m_Credentials,
				contentLength);
	}

	@Override
	public void signRequestLite(HttpRequestBase request, long l)
			throws StorageException, InvalidKeyException,
			NotImplementedException {
		// BaseRequest.signRequestForBlobAndQueueSharedKeyLite(httpurlconnection,
		// m_Credentials, Long.valueOf(l));
		throw new NotImplementedException();
	}

	@Override
	public String toString(boolean boolean1) {
		return String.format("%s=%s;%s=%s", new Object[] {
				"AccountName",
				getAccountName(),
				"AccountKey",
				boolean1 ? m_Credentials.getKey()
						.getBase64EncodedKey() : "[key hidden]" });
	}

	@Override
	public URI transformUri(URI uri) {
		return uri;
	}
}
