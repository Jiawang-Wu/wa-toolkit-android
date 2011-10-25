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

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.impl.client.DefaultHttpClient;

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
				storageoperation.exceptionReference = StorageException.translateException(storageoperation.result.httpResponse, e);
		        throw storageoperation.exceptionReference;
			}
    }

    protected static void getResponseCode(RequestResult requestresult, HttpResponse response)
        throws IOException
    {
        requestresult.statusCode = response.getStatusLine().getStatusCode();
        requestresult.statusMessage = response.getStatusLine().getReasonPhrase();
        requestresult.stopDate = new Date();
        requestresult.serviceRequestID = BaseResponse.getRequestId(response);
        requestresult.eTag = BaseResponse.getEtag(response);
        requestresult.date = BaseResponse.getDate(response);
        requestresult.contentMD5 = BaseResponse.getContentMD5(response);
    }

    protected static RequestResult processRequest(HttpRequestBase request)
        throws IOException
    {
    	HttpClient client = new DefaultHttpClient();
    	HttpResponse response = client.execute(request);
        RequestResult requestresult = new RequestResult();
        requestresult.startDate = new Date();
        requestresult.statusCode = response.getStatusLine().getStatusCode();
        requestresult.statusMessage = response.getStatusLine().getReasonPhrase();
        requestresult.stopDate = new Date();
        requestresult.serviceRequestID = BaseResponse.getRequestId(response);
        requestresult.eTag = BaseResponse.getEtag(response);
        requestresult.date = BaseResponse.getDate(response);
        requestresult.contentMD5 = BaseResponse.getContentMD5(response);
    	requestresult.httpResponse = response;
        return requestresult;
    }

    /*
    protected static InputStream getInputStream(HttpBaseRequest request)
        throws IOException
    {
        RequestResult requestresult = new RequestResult();
        operationcontext.m_CurrentRequestObject = request;
        requestresult.startDate = new Date();
        operationcontext.m_RequestResults.add(requestresult);
        try
        {
            return request.getInputStream();
        }
        catch(IOException ioexception)
        {
            getResponseCode(requestresult, request, operationcontext);
            throw ioexception;
        }
    }
*/
}
