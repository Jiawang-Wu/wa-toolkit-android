package com.windowsazure.samples.android.storageclient;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URISyntaxException;
import java.security.InvalidKeyException;
import java.util.HashMap;

import org.apache.http.client.methods.HttpRequestBase;

public class WAZServiceAccountCredentials extends StorageCredentials {

	private String m_AuthorizationToken;

	boolean equals(StorageCredentials rightCredentials)	{
		return rightCredentials instanceof WAZServiceAccountCredentials
				&& this.equals((WAZServiceAccountCredentials) rightCredentials);
	}

	boolean equals(WAZServiceAccountCredentials rightCredentials) {
		return this.m_AuthorizationToken.equals(rightCredentials.m_AuthorizationToken);
	}

	public WAZServiceAccountCredentials(String authorizationToken) {
		this.m_AuthorizationToken = authorizationToken;
	}

	@Override
	protected boolean canCredentialsComputeHmac() throws NotImplementedException {
		return false;
	}

	@Override
	protected boolean canCredentialsSignRequest() throws NotImplementedException {
		return false;
	}

	@Override
	protected boolean canCredentialsSignRequestLite() throws NotImplementedException {
		return false;
	}

	@Override
	public String computeHmac256(String s) throws NotImplementedException, InvalidKeyException {
		return null;
	}

	@Override
	public String computeHmac512(String s) throws NotImplementedException, InvalidKeyException {
		return null;
	}

	@Override
	public String containerEndpointPostfix() {
		return "container/";
	}

	@Override
	StorageCredentials credentialsForBlobOf(CloudBlobContainer cloudBlobContainer)
			throws	IllegalArgumentException, 
					UnsupportedEncodingException,
					URISyntaxException, 
					StorageException,
					IOException {		
		URI uri = cloudBlobContainer.getTransformedAddress();
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
	public void signRequest(HttpRequestBase request, long l)
			throws NotImplementedException, InvalidKeyException, StorageException {
		request.addHeader("AuthToken", m_AuthorizationToken);
	}

	@Override
	public void signRequestLite(HttpRequestBase request, long l)
			throws NotImplementedException, StorageException,
			InvalidKeyException {
	}

	@Override
	public String toString(boolean showSignature) {
		return null;
	}

	@Override
	public URI transformUri(URI uri) throws URISyntaxException, StorageException {
		return null;
	}

	@Override
	public void signTableRequest(HttpRequestBase request) {
		request.addHeader("AuthToken", m_AuthorizationToken);		
	}

	@Override
	public void signTableRequestLite(HttpRequestBase request) {
		// TODO Auto-generated method stub		
	}
}
