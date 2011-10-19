package com.windowsazure.samples.android.storageclient;

import java.net.HttpURLConnection;

abstract class StorageOperation
{
    public abstract Object execute(Object firstArgument, Object secondArgument) throws Exception;

    protected void initialize()
    {
        result = new RequestResult();
    }

    protected StorageException materializeException(HttpURLConnection httpurlconnection) throws NotImplementedException
    {
        if(exceptionReference != null)
            return exceptionReference;
        else
            return StorageException.translateException(httpurlconnection, null);
    }

    protected StorageException exceptionReference;
    protected RequestResult result;
    boolean nonExceptionedRetryableFailure;
    HttpURLConnection httpurlconnection;
}
