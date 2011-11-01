package com.windowsazure.samples.android.storageclient;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.security.InvalidKeyException;

import org.apache.http.client.methods.HttpRequestBase;

public final class StorageCredentialsAccountAndKey extends StorageCredentials
{

    public StorageCredentialsAccountAndKey(String s, byte abyte0[]) throws NotImplementedException
    {
        m_Credentials = new Credentials(s, abyte0);
    }

    public StorageCredentialsAccountAndKey(String s, String s1) throws IllegalArgumentException, NotImplementedException
    {
        this(s, Base64.decode(s1));
    }

    protected Boolean canCredentialsComputeHmac()
    {
        return true;
    }

    protected Boolean canCredentialsSignRequest()
    {
        return true;
    }

    protected Boolean canCredentialsSignRequestLite()
    {
        return true;
    }

    public String computeHmac256(String text)
        throws InvalidKeyException, IllegalArgumentException
    {
        return StorageKey.computeMacSha256(m_Credentials.getKey(), text);
    }

    public String computeHmac512(String text)
        throws InvalidKeyException, IllegalArgumentException
    {
        return StorageKey.computeMacSha512(m_Credentials.getKey(), text);
    }

    public void signRequest(HttpRequestBase request, long contentLength)
    		throws InvalidKeyException, StorageException, MalformedURLException
    {
    	BaseRequest.signRequestForBlobAndQueue(request, m_Credentials, contentLength);
    }
    
    protected Boolean doCredentialsNeedTransformUri()
    {
        return false;
    }

    public String getAccountName()
    {
        return m_Credentials.getAccountName();
    }

    protected String getBase64EncodedKey() throws NotImplementedException
    {
        return m_Credentials.getKey().getBase64EncodedKey();
    }

    public Credentials getCredentials()
    {
        return m_Credentials;
    }

    public void setCredentials(Credentials credentials)
    {
        m_Credentials = credentials;
    }

    public void signRequestLite(HttpRequestBase request, long l)
        throws StorageException, InvalidKeyException, NotImplementedException
    {
        //BaseRequest.signRequestForBlobAndQueueSharedKeyLite(httpurlconnection, m_Credentials, Long.valueOf(l));
        	throw new NotImplementedException();
    }

    public String toString(Boolean boolean1)
    {
        return String.format("%s=%s;%s=%s", new Object[] {
            "AccountName", getAccountName(), "AccountKey", boolean1.booleanValue() ? m_Credentials.getKey().getBase64EncodedKey() : "[key hidden]"
        });
    }

    public URI transformUri(URI uri)
    {
        return uri;
    }

    private Credentials m_Credentials;

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
	AbstractContainerRequest getContainerRequest() {
		return new ContainerRequest();
	}

	@Override
	AbstractBlobRequest getBlobRequest() {
		return new BlobRequest();
	}
}
