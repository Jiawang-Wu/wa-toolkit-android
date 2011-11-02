package com.windowsazure.samples.android.storageclient;

import java.net.HttpURLConnection;
import java.net.URI;
import java.util.Calendar;
import java.util.Date;

import org.apache.http.HttpResponse;
import org.apache.http.message.AbstractHttpMessage;

final class BlobResponse extends BaseResponse
{

    BlobResponse()
    {
    }

    public static BlobAttributes getAttributes(AbstractHttpMessage response, URI blobUri, String snapshotID)
    {
        BlobAttributes blobattributes = new BlobAttributes();
        BlobProperties blobproperties = blobattributes.properties;
        blobproperties.cacheControl = Utility.getFirstHeaderValueOrEmpty(response, "Cache-Control");
        blobproperties.contentEncoding = Utility.getFirstHeaderValueOrEmpty(response, "Content-Encoding");
        blobproperties.contentLanguage = Utility.getFirstHeaderValueOrEmpty(response, "Content-Language");
        blobproperties.contentMD5 = Utility.getFirstHeaderValueOrEmpty(response, "Content-MD5");
        blobproperties.contentType = Utility.getFirstHeaderValueOrEmpty(response, "Content-Type");
        blobproperties.eTag = Utility.getFirstHeaderValueOrEmpty(response, "ETag");
        Calendar calendar = Calendar.getInstance(Utility.LOCALE_US);
        calendar.setTimeZone(Utility.UTC_ZONE);
        calendar.setTime(new Date(Utility.getFirstHeaderValueOrEmpty(response, "Last-Modified")));
        blobproperties.lastModified = calendar.getTime();
        String s1 = Utility.getFirstHeaderValueOrEmpty(response, "x-ms-blob-type");
        blobproperties.blobType = BlobType.fromValue(s1);
        String s2 = Utility.getFirstHeaderValueOrEmpty(response, "x-ms-lease-status");
        if(!Utility.isNullOrEmpty(s2))
            blobproperties.leaseStatus = LeaseStatus.fromValue(s2);
        String s3 = Utility.getFirstHeaderValueOrEmpty(response, "Cache-Range");
        String s4 = Utility.getFirstHeaderValueOrEmpty(response, "x-ms-blob-content-length");
        if(!Utility.isNullOrEmpty(s3))
            blobproperties.length = Long.parseLong(s3);
        else
        if(!Utility.isNullOrEmpty(s4))
        {
            blobproperties.length = Long.parseLong(s4);
        } else
        {
            String s5 = Utility.getFirstHeaderValueOrEmpty(response, "Content-Length");
            if(!Utility.isNullOrEmpty(s5))
                blobproperties.length = Long.parseLong(s5);
        }
        blobattributes.uri = blobUri;
        blobattributes.snapshotID = snapshotID;
        blobattributes.metadata = getMetadata(response);
        return blobattributes;
    }

    public static String getLeaseID(AbstractHttpMessage response)
    {
        return Utility.getFirstHeaderValueOrEmpty(response, "x-ms-lease-id");
    }

    public static String getLeaseTime(AbstractHttpMessage response)
    {
        return Utility.getFirstHeaderValueOrEmpty(response, "x-ms-lease-time");
    }

    public static String getSnapshotTime(AbstractHttpMessage response)
    {
        return Utility.getFirstHeaderValueOrEmpty(response, "x-ms-snapshot");
    }
}
