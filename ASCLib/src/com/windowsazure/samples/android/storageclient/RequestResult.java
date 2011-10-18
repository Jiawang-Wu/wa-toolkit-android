package com.windowsazure.samples.android.storageclient;

import java.util.Date;

public class RequestResult
{

    public RequestResult() throws NotImplementedException
    {
    	throw new NotImplementedException();
    }

    public Exception exception;
    public String serviceRequestID;
    public String contentMD5;
    public String date;
    public String eTag;
    public Date startDate;
    public int statusCode;
    public String statusMessage;
    public Date stopDate;
    protected volatile int m_CurrentOperationByteCount;
}
