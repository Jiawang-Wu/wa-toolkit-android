package com.windowsazure.storageclient;

import java.net.*;

public final class StorageCredentialsSharedAccessSignature extends StorageCredentials
{

    public StorageCredentialsSharedAccessSignature(String s) throws NotImplementedException
    {
    	throw new NotImplementedException();
    }

    protected Boolean canCredentialsComputeHmac() throws NotImplementedException
    {
    	throw new NotImplementedException();
    }

    protected Boolean canCredentialsSignRequest() throws NotImplementedException
    {
    	throw new NotImplementedException();
    }

    protected Boolean canCredentialsSignRequestLite() throws NotImplementedException
    {
    	throw new NotImplementedException();
    }

    public String computeHmac256(String s) throws NotImplementedException
    {
    	throw new NotImplementedException();
    }

    public String computeHmac512(String s) throws NotImplementedException
    {
    	throw new NotImplementedException();
    }

    protected Boolean doCredentialsNeedTransformUri() throws NotImplementedException
    {
    	throw new NotImplementedException();
    }

    public String getAccountName() throws NotImplementedException
    {
    	throw new NotImplementedException();
    }

    public String getToken() throws NotImplementedException
    {
    	throw new NotImplementedException();
    }

    public void signRequest(HttpURLConnection httpurlconnection, long l) throws NotImplementedException
    {
    	throw new NotImplementedException();
    }

    public void signRequestLite(HttpURLConnection httpurlconnection, long l) throws NotImplementedException
    {
    	throw new NotImplementedException();
    }

    public String toString(Boolean boolean1)
    {
    	return null;
    }

    public URI transformUri(URI uri)
        throws NotImplementedException, URISyntaxException, StorageException
    {
    	throw new NotImplementedException();
    }

    private String m_Token;
}
