package com.windowsazure.samples.android.storageclient;

import java.net.URI;
import java.net.URISyntaxException;

import org.apache.http.client.methods.HttpRequestBase;

public final class StorageCredentialsSharedAccessSignature extends
		StorageCredentials {

	private String m_Token;

	boolean equals(StorageCredentials rightCredentials) {
		return rightCredentials instanceof StorageCredentialsSharedAccessSignature
				&& this.equals((StorageCredentialsSharedAccessSignature) rightCredentials);
	}

	boolean equals(StorageCredentialsSharedAccessSignature rightCredentials) {
		return this.m_Token.equals(rightCredentials.m_Token);
	}

	/**
	* Initializes a new instance of the StorageCredentialsSharedAccessSignature class with the specified
	* shared access token.
	* 
	* @param token
	*            the shared access signature token.
	*/
	public StorageCredentialsSharedAccessSignature(String token) {
		m_Token = token;
	}

	/**
	* RESERVED, for internal use only.  Gets a value indicating whether the <Code>ComputeHmac</Code> method will return a valid HMAC-encoded
	* signature string when called using the specified credentials.
	* 
	* @return <Code>True</Code> if these credentials will yield a valid signature string; otherwise,
	*         <Code>false</Code>
	*/
	@Override
	protected boolean canCredentialsComputeHmac() {
		return false;
	}

	/**
	* RESERVED, for internal use only.  Gets a value indicating whether a request * can be signed under the Shared Key authentication scheme
	* using the 
	* specified credentials.
	* 
	* @return <Code>True</Code> if a request can be signed with these credentials; otherwise,
	*         <Code>false</Code>
	*/
	@Override
	protected boolean canCredentialsSignRequest() {
		return false;
	}

	/**
	* RESERVED, for internal use only.  Gets a value indicating whether a request
	*  can be signed under the Shared Key Lite authentication scheme
	* using the 
	* specified credentials.
	* 
	* @return <Code>True</Code> if a request can be signed with these credentials; otherwise,
	*         <Code>false</Code>
	*/
	@Override
	protected boolean canCredentialsSignRequestLite() {
		return false;
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
	public String computeHmac256(String value) {
		return null;
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
	public String computeHmac512(String value) {
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

	/**
	* RESERVED, for internal use only. Gets a value indicating whether the <Code>TransformUri</Code> method should be called to transform a
	* resource URI to a URI that includes a token for a shared access signature.
	* 
	* @return <Code>True</Code> if the URI must be transformed; otherwise, <Code>false</Code>
	*/
	@Override
	protected boolean doCredentialsNeedTransformUri() {
		return true;
	}

	/**
	* Gets the associated account name for the credentials.
	* 
	* @return the associated account name for the credentials.
	*/
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

	/**
	* Signs a request using the specified credentials under the Shared Key authentication scheme.
	* 
	* @param connection
	*            the HttpURLConnection object to sign
	* @param contentLength
	*            the length of the content written to the output stream. If unknown specify -1;
	* @throws InvalidKeyException
	*             if the given key is invalid.
	* @throws StorageException
	*/
	@Override
	public void signRequest(HttpRequestBase request, long length) {
	}

	@Override
	public void signRequestLite(HttpRequestBase request, long length) {
	}

	@Override
	public void signTableRequest(HttpRequestBase request) {
		// TODO Auto-generated method stub

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
		return String.format("%s=%s", new Object[] { "SharedAccessSignature",
				exportSecrets ? m_Token : "[signature hidden]" });
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
	public URI transformUri(URI resourceUri) throws URISyntaxException,
			StorageException {
		return PathUtility.addToQuery(resourceUri, m_Token);
	}
}
