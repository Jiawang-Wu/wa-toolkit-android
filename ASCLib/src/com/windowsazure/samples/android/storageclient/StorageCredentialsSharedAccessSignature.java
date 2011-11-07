package com.windowsazure.samples.android.storageclient;

import java.net.URI;
import java.net.URISyntaxException;

import org.apache.http.client.methods.HttpRequestBase;

public final class StorageCredentialsSharedAccessSignature extends
		StorageCredentials {

	private String m_Token;

	public StorageCredentialsSharedAccessSignature(String s) {
		m_Token = s;
	}

	@Override
	protected boolean canCredentialsComputeHmac() {
		return false;
	}

	@Override
	protected boolean canCredentialsSignRequest() {
		return false;
	}

	@Override
	protected boolean canCredentialsSignRequestLite() {
		return false;
	}

	@Override
	public String computeHmac256(String s) {
		return null;
	}

	@Override
	public String computeHmac512(String s) {
		return null;
	}

	@Override
	public String containerEndpointPostfix() {
		return "";
	}

	@Override
	StorageCredentials credentialsForBlobOf(
			CloudBlobContainer cloudBlobContainer) {
		return this;
	}

	@Override
	protected boolean doCredentialsNeedTransformUri() {
		return true;
	}

	@Override
	public String getAccountName() {
		return null;
	}

	@Override
	AbstractBlobRequest getBlobRequest() {
		return new BlobRequest();
	}

	@Override
	AbstractContainerRequest getContainerRequest() {
		return new ContainerRequest();
	}

	public String getToken() {
		return m_Token;
	}

	@Override
	public void signRequest(HttpRequestBase request, long l) {
	}

	@Override
	public void signRequestLite(HttpRequestBase request, long l) {
	}

	@Override
	public String toString(boolean boolean1) {
		return String.format("%s=%s", new Object[] { "SharedAccessSignature",
				boolean1 ? m_Token : "[signature hidden]" });
	}

	@Override
	public URI transformUri(URI uri) throws URISyntaxException,
			StorageException {
		return PathUtility.addToQuery(uri, m_Token);
	}
}
