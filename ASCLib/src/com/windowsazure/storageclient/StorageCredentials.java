package com.windowsazure.storageclient;

import java.net.*;
import java.security.InvalidKeyException;
import java.util.HashMap;

public abstract class StorageCredentials
{

    public StorageCredentials()
    {
    }

    public static StorageCredentials tryParseCredentials(String s)
        throws NotImplementedException, InvalidKeyException, StorageException
    {
    	throw new NotImplementedException();
    }

    protected static StorageCredentials tryParseCredentials(HashMap hashmap)
        throws NotImplementedException, InvalidKeyException
    {
    	throw new NotImplementedException();
    }

    public abstract String computeHmac256(String s)
        throws NotImplementedException, InvalidKeyException;

    public abstract String computeHmac512(String s)
        throws NotImplementedException, InvalidKeyException;

    public abstract String getAccountName() throws NotImplementedException;

    public abstract void signRequest(HttpURLConnection httpurlconnection, long l)
        throws NotImplementedException, InvalidKeyException, StorageException;

    public abstract void signRequestLite(HttpURLConnection httpurlconnection, long l)
        throws NotImplementedException, StorageException, InvalidKeyException;

    public abstract String toString(Boolean boolean1);

    public abstract URI transformUri(URI uri)
        throws NotImplementedException, URISyntaxException, StorageException;

    protected abstract Boolean canCredentialsComputeHmac() throws NotImplementedException;

    protected abstract Boolean canCredentialsSignRequest() throws NotImplementedException;

    protected abstract Boolean canCredentialsSignRequestLite() throws NotImplementedException;

    protected abstract Boolean doCredentialsNeedTransformUri() throws NotImplementedException;
}
