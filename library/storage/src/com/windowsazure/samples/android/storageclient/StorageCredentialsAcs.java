package com.windowsazure.samples.android.storageclient;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.security.InvalidKeyException;
import java.util.HashMap;

import org.apache.http.client.methods.HttpRequestBase;

public final class StorageCredentialsAcs extends StorageCredentials {

	private String m_Token;
	
	public StorageCredentialsAcs(String token) {
		this.m_Token = token;
	}
	
	@Override
	protected boolean canCredentialsComputeHmac() throws NotImplementedException {
		return false;
	}

	@Override
	protected boolean canCredentialsSignRequest() throws NotImplementedException { 
		return true;
	}

	@Override
	protected boolean canCredentialsSignRequestLite() throws NotImplementedException {
		return true;
	}

	@Override
	public String computeHmac256(String string) throws InvalidKeyException, NotImplementedException { 
		throw new NotImplementedException();
	}

	@Override
	public String computeHmac512(String string) throws InvalidKeyException, NotImplementedException {
		throw new NotImplementedException();
	}

	@Override
	public String containerEndpointPostfix() {
		return "container/";
	}

	@Override
	StorageCredentials credentialsForBlobOf(CloudBlobContainer container)
			throws IllegalArgumentException, UnsupportedEncodingException, URISyntaxException, StorageException, IOException {
		URI uri = container.getTransformedAddress();
		String decoded = uri.toString().replace("&amp;", "&");
		HashMap<String, String[]> arguments = PathUtility.parseQueryString(decoded);
		
		return SharedAccessSignatureHelper.parseQuery(arguments);
	}

	@Override
	protected boolean doCredentialsNeedTransformUri() {
		return false;
	}

	@Override
	public String getAccountName() {
		return null;
	}

	@Override
	AbstractBlobRequest getBlobRequest() {
		return new BlobWASServiceRequest();
	}

	@Override
	AbstractContainerRequest getContainerRequest() {
		return new ContainerWASServiceRequest();
	}

	@Override
	public void signRequest(HttpRequestBase request, long length)
			throws NotImplementedException, InvalidKeyException, StorageException, MalformedURLException {
		this.signAllRequests(request);
	}

	@Override
	public void signRequestLite(HttpRequestBase request, long length)
			throws NotImplementedException, StorageException, InvalidKeyException {
		this.signAllRequests(request);		
	}

	@Override
	public void signTableRequest(HttpRequestBase request)
			throws InvalidKeyException, MalformedURLException, IllegalArgumentException, StorageException {
		this.signAllRequests(request);
	}

	@Override
	public void signTableRequestLite(HttpRequestBase request) {
		this.signAllRequests(request);
	}
	
	public void signAllRequests(HttpRequestBase request) {
		request.addHeader("Authorization", "OAuth " + this.m_Token);
	}

	@Override
	public String toString(boolean showSignature) {
		return m_Token;
	}

	@Override
	public URI transformUri(URI uri) throws URISyntaxException, StorageException {
		return uri;
	}

	@Override
	boolean equals(StorageCredentials rightCredentials) { 
		return false;
	}
}