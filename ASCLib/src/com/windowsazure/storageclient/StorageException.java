package com.windowsazure.storageclient;

import java.io.IOException;
import java.net.HttpURLConnection;

public final class StorageException extends Exception
{

    public StorageException(String s, String s1, int i, StorageExtendedErrorInformation storageextendederrorinformation, Exception exception) throws NotImplementedException
    {
    	throw new NotImplementedException();
    }

    protected static StorageExtendedErrorInformation getErrorDetailsFromRequest(HttpURLConnection httpurlconnection) throws NotImplementedException
    {
    	throw new NotImplementedException();
    }

    public static StorageException translateException(HttpURLConnection httpurlconnection, Exception exception) throws NotImplementedException
    {
    	throw new NotImplementedException();
    }

    protected static StorageException translateFromHttpStatus(int i, String s, StorageExtendedErrorInformation storageextendederrorinformation, Exception exception) throws NotImplementedException
    {
    	throw new NotImplementedException();
    }

    public static final long serialVersionUID = 0x6ea4e362e7d2d5f0L;
    public final String errorCode;
    public final StorageExtendedErrorInformation extendedErrorInformation;
    public final int httpStatusCode;
}
