package com.windowsazure.samples.android.storageclient;

import java.net.URI;
import java.util.Calendar;
import java.util.Date;

import org.apache.http.message.AbstractHttpMessage;

final class BlobResponse extends BaseResponse {

	public static BlobAttributes getAttributes(AbstractHttpMessage response,
			URI blobUri, String snapshotID) {
		BlobAttributes blobAttributes = new BlobAttributes();
		BlobProperties blobProperties = blobAttributes.properties;
		blobProperties.cacheControl = Utility.getFirstHeaderValueOrEmpty(
				response, "Cache-Control");
		blobProperties.contentEncoding = Utility.getFirstHeaderValueOrEmpty(
				response, "Content-Encoding");
		blobProperties.contentLanguage = Utility.getFirstHeaderValueOrEmpty(
				response, "Content-Language");
		blobProperties.contentMD5 = Utility.getFirstHeaderValueOrEmpty(
				response, "Content-MD5");
		blobProperties.contentType = Utility.getFirstHeaderValueOrEmpty(
				response, "Content-Type");
		blobProperties.eTag = Utility.getFirstHeaderValueOrEmpty(response,
				"ETag");

		String lastModifiedString = Utility.getFirstHeaderValueOrEmpty(
				response, "Last-Modified");
		if (lastModifiedString.length() != 0) {
			Calendar calendar = Calendar.getInstance(Utility.LOCALE_US);
			calendar.setTimeZone(Utility.UTC_ZONE);
			calendar.setTime(new Date());
			blobProperties.lastModified = calendar.getTime();
		}

		String blobTypeString = Utility.getFirstHeaderValueOrEmpty(response,
				"x-ms-blob-type");
		if (blobTypeString.length() != 0) {
			blobProperties.blobType = BlobType.fromValue(blobTypeString);
		}
		String leaseStatusString = Utility.getFirstHeaderValueOrEmpty(response,
				"x-ms-lease-status");
		if (!Utility.isNullOrEmpty(leaseStatusString))
			blobProperties.leaseStatus = LeaseStatus.fromValue(leaseStatusString);
		String cacheRangeString = Utility.getFirstHeaderValueOrEmpty(response, "Cache-Range");
		String blobContentLengthString = Utility.getFirstHeaderValueOrEmpty(response,
				"x-ms-blob-content-length");
		if (!Utility.isNullOrEmpty(cacheRangeString))
			blobProperties.length = Long.parseLong(cacheRangeString);
		else if (!Utility.isNullOrEmpty(blobContentLengthString)) {
			blobProperties.length = Long.parseLong(blobContentLengthString);
		} else {
			String contentLengthString = Utility.getFirstHeaderValueOrEmpty(response,
					"Content-Length");
			if (!Utility.isNullOrEmpty(contentLengthString))
				blobProperties.length = Long.parseLong(contentLengthString);
		}
		blobAttributes.uri = blobUri;
		blobAttributes.snapshotID = snapshotID;
		blobAttributes.metadata = getMetadata(response);
		return blobAttributes;
	}

	public static String getLeaseID(AbstractHttpMessage response) {
		return Utility.getFirstHeaderValueOrEmpty(response, "x-ms-lease-id");
	}

	public static String getLeaseTime(AbstractHttpMessage response) {
		return Utility.getFirstHeaderValueOrEmpty(response, "x-ms-lease-time");
	}

	public static String getSnapshotTime(AbstractHttpMessage response) {
		return Utility.getFirstHeaderValueOrEmpty(response, "x-ms-snapshot");
	}

	BlobResponse() { }
	
}
