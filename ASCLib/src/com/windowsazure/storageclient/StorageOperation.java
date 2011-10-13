package com.windowsazure.storageclient;

import java.net.HttpURLConnection;

abstract class StorageOperation
{

    public abstract Object execute(Object obj, Object obj1)
        throws NotImplementedException, Exception;

    protected void initialize() throws NotImplementedException
    {
    	throw new NotImplementedException();
    }

    protected StorageException materializeException(HttpURLConnection httpurlconnection) throws NotImplementedException
    {
    	throw new NotImplementedException();
    }

    protected StorageException exceptionReference;
    protected boolean nonExceptionedRetryableFailure;
    protected RequestResult result;
}
