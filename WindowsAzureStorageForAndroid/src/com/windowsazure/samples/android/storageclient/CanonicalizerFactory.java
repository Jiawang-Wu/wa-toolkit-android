package com.windowsazure.samples.android.storageclient;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;

import org.apache.http.client.methods.HttpRequestBase;

final class CanonicalizerFactory {

	private static final BlobQueueFullCanonicalizer BLOB_QUEUE_FULL_V2_INSTANCE = new BlobQueueFullCanonicalizer();

	private static final BlobQueueLiteCanonicalizer BLOB_QUEUE_LITE_INSTANCE = new BlobQueueLiteCanonicalizer();

	protected static Canonicalizer getBlobQueueFullCanonicalizer(
			HttpRequestBase request) {
		if (validateVersionIsSupported(request))
			return BLOB_QUEUE_FULL_V2_INSTANCE;
		else
			throw new UnsupportedOperationException(
					"Storage protocol version prior to 2009-09-19 are not supported.");
	}

	protected static Canonicalizer getBlobQueueLiteCanonicalizer(
			HttpRequestBase request) {
		if (validateVersionIsSupported(request))
			return BLOB_QUEUE_LITE_INSTANCE;
		else
			throw new UnsupportedOperationException(
					"Versions before 2009-09-19 do not support Shared Key Lite for Blob And Queue.");
	}

	private static boolean validateVersionIsSupported(HttpRequestBase request) {
		String xMsVersionString = Utility.getFirstHeaderValueOrEmpty(request, "x-ms-version");
		if (xMsVersionString.length() == 0 || xMsVersionString.length() == 0)
			return true;
		try {
			Calendar date2009819US = Calendar.getInstance(Utility.LOCALE_US);
			date2009819US.set(2009, 8, 19, 0, 0, 0);
			date2009819US.set(14, 0);
			SimpleDateFormat yyyyMmDdFormat = new SimpleDateFormat(
					"yyyy-MM-dd");
			java.util.Date xMsVersionDate = yyyyMmDdFormat.parse(xMsVersionString);
			Calendar xmsVersionDateCalendar = Calendar.getInstance(Utility.LOCALE_US);
			xmsVersionDateCalendar.setTime(xMsVersionDate);
			xmsVersionDateCalendar.set(11, 0);
			xmsVersionDateCalendar.set(12, 0);
			xmsVersionDateCalendar.set(13, 0);
			xmsVersionDateCalendar.set(14, 1);
			if (xmsVersionDateCalendar.compareTo(date2009819US) >= 0)
				return true;
		} catch (ParseException parseexception) {
			return false;
		}
		return false;
	}
	CanonicalizerFactory() {
	}

}
