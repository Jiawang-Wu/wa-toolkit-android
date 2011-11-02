package com.windowsazure.samples.android.storageclient;

import java.net.HttpURLConnection;
import java.text.*;
import java.util.Calendar;
import java.util.Locale;

import org.apache.http.client.methods.HttpRequestBase;

final class CanonicalizerFactory
{

    CanonicalizerFactory()
    {
    }

    private static Boolean validateVersionIsSupported(HttpRequestBase request)
    {
        String s = Utility.getFirstHeaderValueOrEmpty(request, "x-ms-version");
        if(s.length() == 0 || s.length() == 0)
            return Boolean.valueOf(true);
        try
        {
            Calendar calendar = Calendar.getInstance(Utility.LOCALE_US);
            calendar.set(2009, 8, 19, 0, 0, 0);
            calendar.set(14, 0);
            SimpleDateFormat simpledateformat = new SimpleDateFormat("yyyy-MM-dd");
            java.util.Date date = simpledateformat.parse(s);
            Calendar calendar1 = Calendar.getInstance(Utility.LOCALE_US);
            calendar1.setTime(date);
            calendar1.set(11, 0);
            calendar1.set(12, 0);
            calendar1.set(13, 0);
            calendar1.set(14, 1);
            if(calendar1.compareTo(calendar) >= 0)
                return Boolean.valueOf(true);
        }
        catch(ParseException parseexception)
        {
            return Boolean.valueOf(false);
        }
        return Boolean.valueOf(false);
    }

    protected static Canonicalizer getBlobQueueFullCanonicalizer(HttpRequestBase httpurlconnection)
    {
        if(validateVersionIsSupported(httpurlconnection).booleanValue())
            return BLOB_QUEUE_FULL_V2_INSTANCE;
        else
            throw new UnsupportedOperationException("Storage protocol version prior to 2009-09-19 are not supported.");
    }

    protected static Canonicalizer getBlobQueueLiteCanonicalizer(HttpRequestBase httpurlconnection)
    {
        if(validateVersionIsSupported(httpurlconnection).booleanValue())
            return BLOB_QUEUE_LITE_INSTANCE;
        else
            throw new UnsupportedOperationException("Versions before 2009-09-19 do not support Shared Key Lite for Blob And Queue.");
    }

    private static final BlobQueueFullCanonicalizer BLOB_QUEUE_FULL_V2_INSTANCE = new BlobQueueFullCanonicalizer();
    private static final BlobQueueLiteCanonicalizer BLOB_QUEUE_LITE_INSTANCE = new BlobQueueLiteCanonicalizer();

}
