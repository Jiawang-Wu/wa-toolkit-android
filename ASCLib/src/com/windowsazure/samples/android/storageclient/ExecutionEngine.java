//REVIEW

package com.windowsazure.samples.android.storageclient;

import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.*;
import java.security.InvalidKeyException;
import java.util.ArrayList;
import java.util.Date;
import java.util.concurrent.TimeoutException;

final class ExecutionEngine
{
    protected static Object execute(Object obj, Object obj1, StorageOperation storageoperation) throws StorageException, NotImplementedException, UnsupportedEncodingException, IOException
    {
        	storageoperation.initialize();
			try 
			{
				return storageoperation.execute(obj, obj1);
			} 
			catch (Exception e) 
			{
				e.printStackTrace();
				storageoperation.exceptionReference = StorageException.translateException(storageoperation.httpurlconnection, e);
		        throw storageoperation.exceptionReference;
			}
    }

    protected static void getResponseCode(RequestResult requestresult, HttpURLConnection httpurlconnection)
        throws IOException
    {
        requestresult.statusCode = httpurlconnection.getResponseCode();
        requestresult.statusMessage = httpurlconnection.getResponseMessage();
        requestresult.stopDate = new Date();
        requestresult.serviceRequestID = BaseResponse.getRequestId(httpurlconnection);
        requestresult.eTag = BaseResponse.getEtag(httpurlconnection);
        requestresult.date = BaseResponse.getDate(httpurlconnection);
        requestresult.contentMD5 = BaseResponse.getContentMD5(httpurlconnection);
    }

    protected static RequestResult processRequest(HttpURLConnection httpurlconnection)
        throws IOException
    {
        RequestResult requestresult = new RequestResult();
        requestresult.startDate = new Date();
        requestresult.statusCode = httpurlconnection.getResponseCode();
        requestresult.statusMessage = httpurlconnection.getResponseMessage();
        requestresult.stopDate = new Date();
        requestresult.serviceRequestID = BaseResponse.getRequestId(httpurlconnection);
        requestresult.eTag = BaseResponse.getEtag(httpurlconnection);
        requestresult.date = BaseResponse.getDate(httpurlconnection);
        requestresult.contentMD5 = BaseResponse.getContentMD5(httpurlconnection);
        return requestresult;
    }
    
    /*
    protected static InputStream getInputStream(HttpURLConnection httpurlconnection)
        throws IOException
    {
        RequestResult requestresult = new RequestResult();
        operationcontext.m_CurrentRequestObject = httpurlconnection;
        requestresult.startDate = new Date();
        operationcontext.m_RequestResults.add(requestresult);
        try
        {
            return httpurlconnection.getInputStream();
        }
        catch(IOException ioexception)
        {
            getResponseCode(requestresult, httpurlconnection, operationcontext);
            throw ioexception;
        }
    }
*/
}
