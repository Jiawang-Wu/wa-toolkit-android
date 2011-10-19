//REVIEW
package com.windowsazure.samples.android.storageclient;

import java.io.IOException;
import java.net.*;
import java.security.InvalidKeyException;
import java.util.*;

final class BaseRequest
{
    public static void addMetadata(HttpURLConnection httpurlconnection, HashMap hashmap)
    {
        if(hashmap != null)
        {
            java.util.Map.Entry entry;
            for(Iterator iterator = hashmap.entrySet().iterator(); iterator.hasNext(); addMetadata(httpurlconnection, (String)entry.getKey(), (String)entry.getValue()))
                entry = (java.util.Map.Entry)iterator.next();

        }
    }

    public static void addMetadata(HttpURLConnection httpurlconnection, String s, String s1)
    {
        Utility.assertNotNullOrEmpty("value", s1);
        httpurlconnection.setRequestProperty((new StringBuilder()).append("x-ms-meta-").append(s).toString(), s1);
    }

    public static HttpURLConnection create(URI uri, int i, UriQueryBuilder uriquerybuilder)
            throws IOException, URISyntaxException, IllegalArgumentException, StorageException
        {
            if(uriquerybuilder == null)
                uriquerybuilder = new UriQueryBuilder();
            HttpURLConnection httpurlconnection = createURLConnection(uri, i, uriquerybuilder);
            httpurlconnection.setFixedLengthStreamingMode(0);
            httpurlconnection.setDoOutput(true);
            httpurlconnection.setRequestMethod("PUT");
            return httpurlconnection;
        }

    public static HttpURLConnection createURLConnection(URI uri, int i, UriQueryBuilder uriquerybuilder)
            throws IOException, URISyntaxException, StorageException
        {
            if(uriquerybuilder == null)
                uriquerybuilder = new UriQueryBuilder();
            if(i != 0)
                uriquerybuilder.add("timeout", String.valueOf(i / 1000));
            URL url = uriquerybuilder.addToURI(uri).toURL();
            HttpURLConnection httpurlconnection = (HttpURLConnection)url.openConnection();
            httpurlconnection.setReadTimeout(i);
            httpurlconnection.setRequestProperty("x-ms-version", "2009-09-19");
            httpurlconnection.setRequestProperty("User-Agent", getUserAgent());
            httpurlconnection.setRequestProperty("Content-Type", "");
            return httpurlconnection;
        }

    private static String getUserAgent()
    {
        if(m_UserAgent == null)
            m_UserAgent = String.format("%s/%s", new Object[] {
                "WA-Storage", Package.getPackage("com.windowsazure.storageclient").getImplementationVersion()
            });
        return m_UserAgent;
    }

    private static String m_UserAgent;

    /*
    BaseRequest()
    {
    }

    public static void addLeaseId(HttpURLConnection httpurlconnection, String s)
    {
        if(s != null)
            addOptionalHeader(httpurlconnection, "x-ms-lease-id", s);
    }

    public static void addOptionalHeader(HttpURLConnection httpurlconnection, String s, String s1)
    {
        if(s1 != null && !s1.equals(""))
            httpurlconnection.setRequestProperty(s, s1);
    }

    public static void addSnapshot(UriQueryBuilder uriquerybuilder, String s)
        throws StorageException
    {
        if(s != null)
            uriquerybuilder.add("snapshot", s);
    }

    public static HttpURLConnection delete(URI uri, int i, UriQueryBuilder uriquerybuilder)
        throws IOException, URISyntaxException, StorageException
    {
        if(uriquerybuilder == null)
            uriquerybuilder = new UriQueryBuilder();
        HttpURLConnection httpurlconnection = createURLConnection(uri, i, uriquerybuilder);
        httpurlconnection.setDoOutput(true);
        httpurlconnection.setRequestMethod("DELETE");
        return httpurlconnection;
    }

    public static HttpURLConnection getMetadata(URI uri, int i, UriQueryBuilder uriquerybuilder)
        throws StorageException, IOException, URISyntaxException
    {
        if(uriquerybuilder == null)
            uriquerybuilder = new UriQueryBuilder();
        uriquerybuilder.add("comp", "metadata");
        HttpURLConnection httpurlconnection = createURLConnection(uri, i, uriquerybuilder);
        httpurlconnection.setDoOutput(true);
        httpurlconnection.setRequestMethod("HEAD");
        return httpurlconnection;
    }

    public static HttpURLConnection getProperties(URI uri, int i, UriQueryBuilder uriquerybuilder)
        throws IOException, URISyntaxException, StorageException
    {
        if(uriquerybuilder == null)
            uriquerybuilder = new UriQueryBuilder();
        HttpURLConnection httpurlconnection = createURLConnection(uri, i, uriquerybuilder);
        httpurlconnection.setDoOutput(true);
        httpurlconnection.setRequestMethod("HEAD");
        return httpurlconnection;
    }

    public static HttpURLConnection setMetadata(URI uri, int i, UriQueryBuilder uriquerybuilder)
        throws IOException, URISyntaxException, StorageException
    {
        if(uriquerybuilder == null)
            uriquerybuilder = new UriQueryBuilder();
        uriquerybuilder.add("comp", "metadata");
        HttpURLConnection httpurlconnection = createURLConnection(uri, i, uriquerybuilder);
        httpurlconnection.setFixedLengthStreamingMode(0);
        httpurlconnection.setDoOutput(true);
        httpurlconnection.setRequestMethod("PUT");
        return httpurlconnection;
    }

    public static void signRequestForBlobAndQueue(HttpURLConnection httpurlconnection, Credentials credentials, Long long1)
        throws InvalidKeyException, StorageException
    {
        httpurlconnection.setRequestProperty("x-ms-date", Utility.getGMTTime());
        Canonicalizer canonicalizer = CanonicalizerFactory.getBlobQueueFullCanonicalizer(httpurlconnection);
        String s = canonicalizer.canonicalize(httpurlconnection, credentials.getAccountName(), long1);
        String s1 = StorageKey.computeMacSha256(credentials.getKey(), s);
        httpurlconnection.setRequestProperty("Authorization", String.format("%s %s:%s", new Object[] {
            "SharedKey", credentials.getAccountName(), s1
        }));
    }

    public static void signRequestForBlobAndQueueSharedKeyLite(HttpURLConnection httpurlconnection, Credentials credentials, Long long1)
        throws InvalidKeyException, StorageException
    {
        httpurlconnection.setRequestProperty("x-ms-date", Utility.getGMTTime());
        Canonicalizer canonicalizer = CanonicalizerFactory.getBlobQueueLiteCanonicalizer(httpurlconnection);
        String s = canonicalizer.canonicalize(httpurlconnection, credentials.getAccountName(), long1);
        String s1 = StorageKey.computeMacSha256(credentials.getKey(), s);
        httpurlconnection.setRequestProperty("Authorization", String.format("%s %s:%s", new Object[] {
            "SharedKeyLite", credentials.getAccountName(), s1
        }));
    }
*/}
