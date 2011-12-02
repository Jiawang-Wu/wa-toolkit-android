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

	boolean equals(StorageCredentials rightCredentials) {
		return rightCredentials instanceof StorageCredentialsAccountAndKey
				&& this.equals((StorageCredentialsAccountAndKey) rightCredentials);
	}

	boolean equals(StorageCredentialsAccountAndKey rightCredentials) {
		return this.toString(true).equals(rightCredentials.toString(true));
	}

	/**
	* Initializes a new instance of the StorageCredentialsAccountAndKey class, using the storage account name
	* and access key.
	* 
	* @param accountName
	*            The name of the storage account.
	* @param key
	*            The account access key, as an array of bytes.
	*/
	public StorageCredentialsAccountAndKey(String accountName, byte key[]) {
		m_Credentials = new Credentials(accountName, key);
	}

	/**
	* Initializes a new instance of the StorageCredentialsAccountAndKeyclass, using the storage account name
	* and access key.
	* 
	* @param accountName
	*            The name of the storage account.
	* @param key
	*            The account access key, as a Base64-encoded string.
	* @throws IOException
	*             if the key is not correclty encoded.
	*/
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

	/**
	* Encodes a Shared Key or Shared Key Lite signature string by using the HMAC-SHA256 algorithm over a
	* UTF-8-encoded string-to-sign.
	* 
	* @param value
	*            UTF-8-encoded string-to-sign.
	* @return An HMAC-encoded signature string
	* @throws InvalidKeyException
	*             if the key is not a valid base64 encoded string.
	*/
	@Override
	public String computeHmac256(String value) throws InvalidKeyException,
			IllegalArgumentException {
		return StorageKey.computeMacSha256(m_Credentials.getKey(), value);
	}

	/**
	* Encodes a Shared Key signature string by using the HMAC-SHA256 algorithm over a UTF-8-encoded
	* string-to-sign.
	* 
	* @param value
	*            UTF-8-encoded string-to-sign.
	* @return An HMAC-encoded signature string
	* @throws InvalidKeyException
	*             if the key is not a valid base64 encoded string.
	*/
	@Override
	public String computeHmac512(String value) throws InvalidKeyException,
			IllegalArgumentException {
		return StorageKey.computeMacSha512(m_Credentials.getKey(), value);
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

	/**
	* Gets the associated account name for the credentials.
	* 
	* @return the associated account name for the credentials.
	*/
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

	/**
	* Returns the internal Credentials associated with the StorageCredentials.
	* 
	* @return the internal Credentials associated with the StorageCredentials.
	*/
	public Credentials getCredentials() {
		return m_Credentials;
	}

	/**
	* @param credentials
	*            the m_Credentials to set
	*/
	public void setCredentials(Credentials credentials) {
		m_Credentials = credentials;
	}

	/**
	* Signs a request using the specified credentials under the Shared Key authentication scheme.
	* 
	* @param request
	*            the HttpRequestBase object to sign
	* @param contentLength
	*            the length of the content written to the output stream. If unknown specify -1;
	* @throws InvalidKeyException
	*             if the given key is invalid.
	* @throws StorageException
	*/
	@Override
	public void signRequest(HttpRequestBase request, long contentLength)
			throws InvalidKeyException, StorageException, MalformedURLException {
		BaseRequest.signRequestForBlobAndQueue(request, m_Credentials,
				contentLength);
	}

	/**
	* Signs a request using the specified credentials under the Shared Key Lite authentication scheme.
	* 
	* @param request
	*            the HttpRequestBase object to sign
	*/
	@Override
	public void signRequestLite(HttpRequestBase request, long length)
			throws StorageException, InvalidKeyException,
			NotImplementedException {
		// BaseRequest.signRequestForBlobAndQueueSharedKeyLite(httpurlconnection,
		// m_Credentials, Long.valueOf(l));
		throw new NotImplementedException();
	}

	@Override
	public void signTableRequest(HttpRequestBase request)
			throws InvalidKeyException, MalformedURLException, IllegalArgumentException, StorageException {
		BaseRequest.signRequestForTable(request, m_Credentials);
	}

	@Override
	public void signTableRequestLite(HttpRequestBase request) {
	// TODO Auto-generated method stub

	}

	/**
	* Returns a String that represents this instance.
	* 
	* @param exportSecrets
	*            <Code>True</Code> to include sensitive data in the string; otherwise, <Code>false</Code>
	* @return a string for the credentials, optionally with sensitive data.
	*/
	@Override
	public String toString(boolean exportSecrets) {
		return String.format("%s=%s;%s=%s", new Object[] {
				"AccountName",
				getAccountName(),
				"AccountKey",
				exportSecrets ? m_Credentials.getKey()
						.getBase64EncodedKey() : "[key hidden]" });
	}

	/**
	* Transforms a resource URI into a shared access signature URI, by appending a shared access token.
	* 
	* @param resourceUri
	*            The resource URI to be transformed.
	* @return The URI for a shared access signature, including the resource URI and the shared access token.
	* @throws URISyntaxException
	*             if the resourceURI is not properly formatted.
	* @throws StorageException
	* @throws IllegalArgumentException
	*/
	@Override
	public URI transformUri(URI resourceUri) {
		return resourceUri;
	}

}
