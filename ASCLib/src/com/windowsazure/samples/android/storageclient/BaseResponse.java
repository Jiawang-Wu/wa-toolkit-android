//REVIEW
package com.windowsazure.samples.android.storageclient;

import java.util.*;

import org.apache.http.Header;
import org.apache.http.HttpResponse;

class BaseResponse
{
    public static String getEtag(HttpResponse response)
    {
    	return getHeaderValueOrNullIfNonExistent(response, "Etag");
    }

    public static HashMap getMetadata(HttpResponse request)
    {
        return getValuesByHeaderPrefix(request, "x-ms-meta-");
    }

    private static HashMap getValuesByHeaderPrefix(HttpResponse response, String s)
    {
        HashMap hashmap = new HashMap();
        int i = s.length();
        for (Header header : response.getAllHeaders())
        {
            if(header.getName() != null
            		&& header.getName().startsWith(s))
            {
                hashmap.put(header.getName().substring(i), header.getValue());
            }
        }
        return hashmap;
    }

    public static String getContentMD5(HttpResponse response)
    {
    	return getHeaderValueOrNullIfNonExistent(response, "Content-MD5");
    }

    public static String getDate(HttpResponse response)
    {
        String s = response.getFirstHeader("Date").getValue();
        return s != null ? s : response.getFirstHeader("x-ms-date").getValue();
    }

    public static String getRequestId(HttpResponse response)
    {
    	return getHeaderValueOrNullIfNonExistent(response, "x-ms-request-id");
    }

    private static String getHeaderValueOrNullIfNonExistent(HttpResponse response, String headerName)
    {
    	Header header = response.getFirstHeader(headerName);
    	if (header != null)
    	{
            return header.getValue();
    	}
    	else
    	{
    		return null;
    	}
    }
}
