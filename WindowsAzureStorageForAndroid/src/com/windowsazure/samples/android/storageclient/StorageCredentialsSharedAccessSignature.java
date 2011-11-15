package com.windowsazure.samples.android.storageclient;

import java.net.URI;
import java.net.URISyntaxException;

import org.apache.http.client.methods.HttpRequestBase;

public final class StorageCredentialsSharedAccessSignature extends
		StorageCredentials {

	private String m_Token;

	public StorageCredentialsSharedAccessSignature(String token) {
		m_Token = token;
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
	public String computeHmac256(String string) {
		return null;
	}

	@Override
	public String computeHmac512(String length) {
		return null;
	}

	@Override
	public String containerEndpointPostfix() {
		return "";
	}

	@Override
	StorageCredentials credentialsForBlobOf(
			CloudBlobContainer container) {
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
	public void signRequest(HttpRequestBase request, long length) {
	}

	@Override
	public void signRequestLite(HttpRequestBase request, long length) {
	}

	@Override
	public String toString(boolean showSignature) {
		return String.format("%s=%s", new Object[] { "SharedAccessSignature",
				showSignature ? m_Token : "[signature hidden]" });
	}

	@Override
	public URI transformUri(URI uri) throws URISyntaxException,
			StorageException {
		return PathUtility.addToQuery(uri, m_Token);
	}
}
