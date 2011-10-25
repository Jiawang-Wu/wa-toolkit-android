//REVIEW

package com.windowsazure.samples.android.storageclient;

import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.util.*;

abstract class Canonicalizer
{
/*
    Canonicalizer()
    {
    }

    private static void addCanonicalizedHeaders(HttpBaseRequest request, StringBuilder stringbuilder)
    {
        Map map = request.getRequestProperties();
        ArrayList arraylist = new ArrayList();
        Iterator iterator = map.keySet().iterator();
        do
        {
            if(!iterator.hasNext())
                break;
            String s = (String)iterator.next();
            if(s.toLowerCase(Locale.US).startsWith("x-ms-"))
                arraylist.add(s.toLowerCase(Locale.US));
        } while(true);
        Collections.sort(arraylist);
        StringBuilder stringbuilder1;
        for(Iterator iterator1 = arraylist.iterator(); iterator1.hasNext(); appendCanonicalizedElement(stringbuilder, stringbuilder1.toString()))
        {
            String s1 = (String)iterator1.next();
            stringbuilder1 = new StringBuilder(s1);
            String s2 = ":";
            ArrayList arraylist1 = getHeaderValues(map, s1);
            for(Iterator iterator2 = arraylist1.iterator(); iterator2.hasNext();)
            {
                String s3 = (String)iterator2.next();
                String s4 = s3.replace("\r\n", "");
                stringbuilder1.append(s2);
                stringbuilder1.append(s4);
                s2 = ",";
            }

        }

    }

    private static void appendCanonicalizedElement(StringBuilder stringbuilder, String s)
    {
        stringbuilder.append("\n");
        stringbuilder.append(s);
    }

    private static String getCanonicalizedResource(URL url, String s)
        throws StorageException
    {
        StringBuilder stringbuilder = new StringBuilder("/");
        stringbuilder.append(s);
        stringbuilder.append(url.getPath());
        StringBuilder stringbuilder1 = new StringBuilder(stringbuilder.toString());
        HashMap hashmap = PathUtility.parseQueryString(url.getQuery());
        HashMap hashmap1 = new HashMap();
        java.util.Map.Entry entry;
        StringBuilder stringbuilder2;
        for(Iterator iterator = hashmap.entrySet().iterator(); iterator.hasNext(); hashmap1.put(entry.getKey() != null ? ((Object) (((String)entry.getKey()).toLowerCase(Locale.US))) : null, stringbuilder2.toString()))
        {
            entry = (java.util.Map.Entry)iterator.next();
            List list = Arrays.asList((Object[])entry.getValue());
            Collections.sort(list);
            stringbuilder2 = new StringBuilder();
            String s2;
            for(Iterator iterator2 = list.iterator(); iterator2.hasNext(); stringbuilder2.append(s2))
            {
                s2 = (String)iterator2.next();
                if(stringbuilder2.length() > 0)
                    stringbuilder2.append(",");
            }

        }

        ArrayList arraylist = new ArrayList(hashmap1.keySet());
        Collections.sort(arraylist);
        StringBuilder stringbuilder3;
        for(Iterator iterator1 = arraylist.iterator(); iterator1.hasNext(); appendCanonicalizedElement(stringbuilder1, stringbuilder3.toString()))
        {
            String s1 = (String)iterator1.next();
            stringbuilder3 = new StringBuilder();
            stringbuilder3.append(s1);
            stringbuilder3.append(":");
            stringbuilder3.append((String)hashmap1.get(s1));
        }

        return stringbuilder1.toString();
    }

    private static ArrayList getHeaderValues(Map map, String s)
    {
        ArrayList arraylist = new ArrayList();
        List list = null;
        Iterator iterator = map.entrySet().iterator();
        do
        {
            if(!iterator.hasNext())
                break;
            java.util.Map.Entry entry = (java.util.Map.Entry)iterator.next();
            if(!((String)entry.getKey()).toLowerCase(Locale.US).equals(s))
                continue;
            list = (List)entry.getValue();
            break;
        } while(true);
        if(list != null)
        {
            String s1;
            for(Iterator iterator1 = list.iterator(); iterator1.hasNext(); arraylist.add(Utility.trimStart(s1)))
                s1 = (String)iterator1.next();

        }
        return arraylist;
    }

    protected static String canonicalizeHttpRequest(URL url, String s, String s1, String s2, long l, String s3, HttpBaseRequest request)
        throws StorageException
    {
        StringBuilder stringbuilder = new StringBuilder(request.getRequestMethod());
        appendCanonicalizedElement(stringbuilder, Utility.getStandardHeaderValue(request, "Content-Encoding"));
        appendCanonicalizedElement(stringbuilder, Utility.getStandardHeaderValue(request, "Content-Language"));
        appendCanonicalizedElement(stringbuilder, l != -1L ? String.valueOf(l) : "");
        appendCanonicalizedElement(stringbuilder, Utility.getStandardHeaderValue(request, "Content-MD5"));
        appendCanonicalizedElement(stringbuilder, s2 == null ? "" : s2);
        String s4 = Utility.getStandardHeaderValue(request, "x-ms-date");
        appendCanonicalizedElement(stringbuilder, s4.equals("") ? s3 : "");
        String s5 = "";
        if(request.getIfModifiedSince() > 0L)
            s5 = Utility.getGMTTime(new Date(request.getIfModifiedSince()));
        appendCanonicalizedElement(stringbuilder, s5);
        appendCanonicalizedElement(stringbuilder, Utility.getStandardHeaderValue(request, "If-Match"));
        appendCanonicalizedElement(stringbuilder, Utility.getStandardHeaderValue(request, "If-None-Match"));
        appendCanonicalizedElement(stringbuilder, Utility.getStandardHeaderValue(request, "If-Unmodified-Since"));
        appendCanonicalizedElement(stringbuilder, Utility.getStandardHeaderValue(request, "Range"));
        addCanonicalizedHeaders(request, stringbuilder);
        appendCanonicalizedElement(stringbuilder, getCanonicalizedResource(url, s));
        return stringbuilder.toString();
    }

    protected static String canonicalizeHttpRequestLite(URL url, String s, String s1, String s2, long l, String s3, HttpBaseRequest request)
        throws StorageException
    {
        StringBuilder stringbuilder = new StringBuilder(request.getRequestMethod());
        String s4 = Utility.getStandardHeaderValue(request, "Content-MD5");
        appendCanonicalizedElement(stringbuilder, s4);
        appendCanonicalizedElement(stringbuilder, s2);
        String s5 = Utility.getStandardHeaderValue(request, "x-ms-date");
        appendCanonicalizedElement(stringbuilder, s5.equals("") ? s3 : "");
        addCanonicalizedHeaders(request, stringbuilder);
        appendCanonicalizedElement(stringbuilder, getCanonicalizedResource(url, s));
        return stringbuilder.toString();
    }

    protected abstract String canonicalize(HttpBaseRequest request, String s, Long long1)
        throws StorageException;
*/}
