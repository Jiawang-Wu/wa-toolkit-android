package com.windowsazure.samples.android.storageclient.wazservice;

import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URISyntaxException;
import java.security.InvalidKeyException;

import com.windowsazure.samples.android.storageclient.NotImplementedException;
import com.windowsazure.samples.android.storageclient.StorageCredentials;
import com.windowsazure.samples.android.storageclient.StorageException;

public class WAZServiceAccountCredentials extends StorageCredentials {

	private String m_AuthorizationToken;

	public WAZServiceAccountCredentials(String authorizationToken) {
		this.m_AuthorizationToken = authorizationToken;
	}

	@Override
	public String computeHmac256(String s) throws NotImplementedException,
			InvalidKeyException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String computeHmac512(String s) throws NotImplementedException,
			InvalidKeyException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getAccountName() {
		return null;
	}

	@Override
	public void signRequest(HttpURLConnection httpurlconnection, long l)
			throws NotImplementedException, InvalidKeyException,
			StorageException {
        httpurlconnection.setRequestProperty("AuthToken", m_AuthorizationToken);
	}

	@Override
	public void signRequestLite(HttpURLConnection httpurlconnection, long l)
			throws NotImplementedException, StorageException,
			InvalidKeyException {
		// TODO Auto-generated method stub

	}

	@Override
	public String toString(Boolean boolean1) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public URI transformUri(URI uri) throws NotImplementedException,
			URISyntaxException, StorageException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected Boolean canCredentialsComputeHmac()
			throws NotImplementedException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected Boolean canCredentialsSignRequest()
			throws NotImplementedException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected Boolean canCredentialsSignRequestLite()
			throws NotImplementedException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected Boolean doCredentialsNeedTransformUri()
			throws NotImplementedException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String containerEndpointPostfix() {
		return "/SharedAccessSignatureService/container/";
	}
}
