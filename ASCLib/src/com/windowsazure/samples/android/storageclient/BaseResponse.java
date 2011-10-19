//REVIEW
package com.windowsazure.samples.android.storageclient;

import java.net.HttpURLConnection;
import java.util.*;

class BaseResponse
{
    public static String getEtag(HttpURLConnection httpurlconnection)
    {
        return httpurlconnection.getHeaderField("Etag");
    }

    public static HashMap getMetadata(HttpURLConnection httpurlconnection)
    {
        return getValuesByHeaderPrefix(httpurlconnection, "x-ms-meta-");
    }

    private static HashMap getValuesByHeaderPrefix(HttpURLConnection httpurlconnection, String s)
    {
        HashMap hashmap = new HashMap();
        Map map = httpurlconnection.getHeaderFields();
        int i = s.length();
        Iterator iterator = map.entrySet().iterator();
        do
        {
            if(!iterator.hasNext())
                break;
            java.util.Map.Entry entry = (java.util.Map.Entry)iterator.next();
            if(entry.getKey() != null && ((String)entry.getKey()).startsWith(s))
            {
                List list = (List)entry.getValue();
                hashmap.put(((String)entry.getKey()).substring(i), list.get(0));
            }
        } while(true);
        return hashmap;
    }

    public static String getContentMD5(HttpURLConnection httpurlconnection)
    {
        return httpurlconnection.getHeaderField("Content-MD5");
    }

    public static String getDate(HttpURLConnection httpurlconnection)
    {
        String s = httpurlconnection.getHeaderField("Date");
        return s != null ? s : httpurlconnection.getHeaderField("x-ms-date");
    }

    public static String getRequestId(HttpURLConnection httpurlconnection)
    {
        return httpurlconnection.getHeaderField("x-ms-request-id");
    }

}
