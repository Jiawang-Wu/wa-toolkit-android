package com.windowsazure.samples.android.storageclient;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.security.InvalidKeyException;
import java.util.HashMap;

import org.apache.http.client.methods.HttpRequestBase;

import android.util.Base64;

public abstract class StorageCredentials {

	protected static StorageCredentials tryParseCredentials(HashMap<String, String> configuration)
			throws InvalidKeyException, IllegalArgumentException,
			NotImplementedException {
		String accountName = configuration.get("AccountName") == null ? null : (String) configuration
				.get("AccountName");
		String accountKey = configuration.get("AccountKey") == null ? null : (String) configuration
				.get("AccountKey");
		String sas = configuration.get("SharedAccessSignature") == null ? null
				: (String) configuration.get("SharedAccessSignature");
		if (accountName != null && accountKey != null && sas == null)
			if (validateIsBase64String(accountKey))
				return new StorageCredentialsAccountAndKey(accountName, accountKey);
			else
				throw new InvalidKeyException(
						"Storage Key is not a valid base64 encoded string.");
		if (accountName == null && accountKey == null && sas != null)
			return new StorageCredentialsSharedAccessSignature(sas);
		else
			return null;
	}

	abstract boolean equals(StorageCredentials rightCredentials);

	private static boolean validateIsBase64String(String base64String) {
		try {
			return Base64.decode(base64String, Base64.NO_WRAP) != null;
		}
		catch (Exception exception) {
			return false;
		}
	}

	/**
	* Tries to parse a connectionString to create the appropriate StorageCredentials object
	* 
	* @param connectionString
	*            a string of key value pairs representing the StorageCredentials
	* @return a StorageCredentials object represented by the connectionString
	* @throws InvalidKeyException
	*             if the account key specified in the connectionString is not valid.
	*/
	public static StorageCredentials tryParseCredentials(String configurationString)
			throws NotImplementedException, InvalidKeyException,
			StorageException {
		throw new NotImplementedException();
	}

	public StorageCredentials() {
	}

	/**
	* RESERVED, for internal use only.  Gets a value indicating whether the <Code>ComputeHmac</Code> method will return a valid HMAC-encoded
	* signature string when called using the specified credentials.
	* 
	* @return <Code>True</Code> if these credentials will yield a valid signature string; otherwise,
	*         <Code>false</Code>
	*/
	protected abstract boolean canCredentialsComputeHmac()
			throws NotImplementedException;

	/**
	* RESERVED, for internal use only.  Gets a value indicating whether a request can be signed under the Shared Key authentication scheme
	* using the specified credentials.
	* 
	* @return <Code>True</Code> if a request can be signed with these credentials; otherwise,
	*         <Code>false</Code>
	*/
	protected abstract boolean canCredentialsSignRequest()
			throws NotImplementedException;

	/**
	* RESERVED, for internal use only.  Gets a value indicating whether a request can be signed under the Shared Key Lite authentication scheme
	* using the specified credentials.
	* 
	* @return <Code>True</Code> if a request can be signed with these credentials; otherwise,
	*         <Code>false</Code>
	*/
	protected abstract boolean canCredentialsSignRequestLite()
			throws NotImplementedException;

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
	public abstract String computeHmac256(String string) throws InvalidKeyException,
			NotImplementedException;

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
	public abstract String computeHmac512(String string) throws InvalidKeyException,
			NotImplementedException;

	public abstract String containerEndpointPostfix();

	abstract StorageCredentials credentialsForBlobOf(
			CloudBlobContainer container)
			throws IllegalArgumentException, UnsupportedEncodingException,
			URISyntaxException, StorageException,
			IOException;

	/**
	* RESERVED, for internal use only.  Gets a value indicating whether the <Code>TransformUri</Code> method should be called to transform a
	* resource URI to a URI that includes a token for a shared access signature.
	* 
	* @return <Code>True</Code> if the URI must be transformed; otherwise, <Code>false</Code>
	*/
	protected abstract boolean doCredentialsNeedTransformUri();

	/**
	* Gets the associated account name for the credentials.
	* 
	* @return the associated account name for the credentials.
	*/
	public abstract String getAccountName();

	abstract AbstractBlobRequest getBlobRequest();

	abstract AbstractContainerRequest getContainerRequest();

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
	public abstract void signRequest(HttpRequestBase request, long length)
			throws NotImplementedException, InvalidKeyException,
			StorageException, MalformedURLException;

	/**
	* Signs a request using the specified credentials under the Shared Key Lite authentication scheme.
	* 
	* @param request
	*            the HttpRequestBase object to sign
	*/
	public abstract void signRequestLite(HttpRequestBase request, long length)
			throws NotImplementedException, StorageException,
			InvalidKeyException;

	public abstract void signTableRequest(HttpRequestBase request)
			throws InvalidKeyException, MalformedURLException, IllegalArgumentException, StorageException;

	public abstract void signTableRequestLite(HttpRequestBase request);

	/**
	* Returns a String that represents this instance.
	* 
	* @param exportSecrets
	*            <Code>True</Code> to include sensitive data in the string; otherwise, <Code>false</Code>
	* @return a string for the credentials, optionally with sensitive data.
	*/
	public abstract String toString(boolean exportSecrets);

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
	public abstract URI transformUri(URI resourceUri) throws URISyntaxException, StorageException;
}
