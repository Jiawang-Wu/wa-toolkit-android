//REVIEW
package com.windowsazure.samples.android.storageclient;

import java.io.IOException;
import java.net.*;
import java.security.InvalidKeyException;
import java.util.*;

import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.client.methods.HttpRequestBase;

final class BaseRequest
{
    public static void addMetadata(HttpRequestBase request, HashMap hashmap)
    {
        if(hashmap != null)
        {
            java.util.Map.Entry entry;
            for(Iterator iterator = hashmap.entrySet().iterator(); iterator.hasNext();
            		addMetadata(request, (String)entry.getKey(), (String)entry.getValue()))
                entry = (java.util.Map.Entry)iterator.next();

        }
    }

    public static void addMetadata(HttpRequestBase request, String s, String s1)
    {
        Utility.assertNotNullOrEmpty("value", s1);
        request.setHeader((new StringBuilder()).append("x-ms-meta-").append(s).toString(), s1);
    }

    public static HttpPut create(URI uri, int i, UriQueryBuilder uriquerybuilder)
            throws IOException, URISyntaxException, IllegalArgumentException, StorageException
        {
            if(uriquerybuilder == null)
                uriquerybuilder = new UriQueryBuilder();
            HttpPut put = new HttpPut();
            setURIAndHeaders(put, uri, uriquerybuilder);
            return put;
        }

    public static <T extends HttpRequestBase> T setURIAndHeaders(T request, URI uri, UriQueryBuilder uriquerybuilder)
            throws IOException, URISyntaxException, StorageException
        {
            if(uriquerybuilder == null)
            {
                uriquerybuilder = new UriQueryBuilder();
            }
            request.setURI(uriquerybuilder.addToURI(uri));
            request.addHeader("x-ms-version", "2009-09-19");
            request.addHeader("User-Agent", getUserAgent());
            request.addHeader("Content-Type", "");
            return request;
        }

    private static String getUserAgent()
    {
        if(m_UserAgent == null)
            m_UserAgent = String.format("%s/%s", new Object[] {
                "WA-Storage", Package.getPackage("com.windowsazure.samples.android.storageclient").getImplementationVersion()
            });
        return m_UserAgent;
    }

    private static String m_UserAgent;

    public static HttpDelete delete(URI uri, int i, UriQueryBuilder uriquerybuilder)
            throws IOException, URISyntaxException, StorageException
        {
            if(uriquerybuilder == null)
                uriquerybuilder = new UriQueryBuilder();
    		HttpDelete request = new HttpDelete();
            setURIAndHeaders(request, uri, uriquerybuilder);
            return request;
        }

    public static void addOptionalHeader(HttpRequestBase request, String s, String s1)
    {
        if(s1 != null && !s1.equals(""))
            request.addHeader(s, s1);
    }

    public static void addLeaseId(HttpRequestBase request, String s)
    {
        if(s != null)
            addOptionalHeader(request, "x-ms-lease-id", s);
    }

    public static void signRequestForBlobAndQueue(HttpRequestBase request, Credentials credentials, Long contentLength)
            throws InvalidKeyException, StorageException, MalformedURLException
        {
            request.setHeader("x-ms-date", Utility.getGMTTime());
            Canonicalizer canonicalizer = CanonicalizerFactory.getBlobQueueFullCanonicalizer(request);
            String s = canonicalizer.canonicalize(request, credentials.getAccountName(), contentLength);
            String s1 = StorageKey.computeMacSha256(credentials.getKey(), s);
            request.setHeader("Authorization", String.format("%s %s:%s", "SharedKey", credentials.getAccountName(), s1));
        }

    /*
    BaseRequest()
    {
    }

    public static void addSnapshot(UriQueryBuilder uriquerybuilder, String s)
        throws StorageException
    {
        if(s != null)
            uriquerybuilder.add("snapshot", s);
    }

    public static HttpRequestBase getMetadata(URI uri, int i, UriQueryBuilder uriquerybuilder)
        throws StorageException, IOException, URISyntaxException
    {
        if(uriquerybuilder == null)
            uriquerybuilder = new UriQueryBuilder();
        uriquerybuilder.add("comp", "metadata");
        HttpRequestBase request = createURLConnection(uri, i, uriquerybuilder);
        request.setDoOutput(true);
        request.setRequestMethod("HEAD");
        return request;
    }

    public static HttpRequestBase getProperties(URI uri, int i, UriQueryBuilder uriquerybuilder)
        throws IOException, URISyntaxException, StorageException
    {
        if(uriquerybuilder == null)
            uriquerybuilder = new UriQueryBuilder();
        HttpRequestBase request = createURLConnection(uri, i, uriquerybuilder);
        request.setDoOutput(true);
        request.setRequestMethod("HEAD");
        return request;
    }

    public static HttpRequestBase setMetadata(URI uri, int i, UriQueryBuilder uriquerybuilder)
        throws IOException, URISyntaxException, StorageException
    {
        if(uriquerybuilder == null)
            uriquerybuilder = new UriQueryBuilder();
        uriquerybuilder.add("comp", "metadata");
        HttpRequestBase request = createURLConnection(uri, i, uriquerybuilder);
        request.setFixedLengthStreamingMode(0);
        request.setDoOutput(true);
        request.setRequestMethod("PUT");
        return request;
    }

    public static void signRequestForBlobAndQueueSharedKeyLite(HttpRequestBase request, Credentials credentials, Long long1)
        throws InvalidKeyException, StorageException
    {
        request.setRequestProperty("x-ms-date", Utility.getGMTTime());
        Canonicalizer canonicalizer = CanonicalizerFactory.getBlobQueueLiteCanonicalizer(request);
        String s = canonicalizer.canonicalize(request, credentials.getAccountName(), long1);
        String s1 = StorageKey.computeMacSha256(credentials.getKey(), s);
        request.setRequestProperty("Authorization", String.format("%s %s:%s", new Object[] {
            "SharedKeyLite", credentials.getAccountName(), s1
        }));
    }
*/}
