package com.windowsazure.samples.android.storageclient;

import java.util.HashMap;

import org.apache.http.Header;
import org.apache.http.HttpResponse;
import org.apache.http.message.AbstractHttpMessage;

class BaseResponse {
	public static String getContentMD5(HttpResponse response) {
		return getHeaderValueOrNullIfNonExistent(response, "Content-MD5");
	}

	public static String getDate(HttpResponse response) {
		String headerOrNull = response.getFirstHeader("Date").getValue();
		return headerOrNull != null ? headerOrNull : response.getFirstHeader("x-ms-date").getValue();
	}

	public static String getEtag(HttpResponse response) {
		return getHeaderValueOrNullIfNonExistent(response, "Etag");
	}

	private static String getHeaderValueOrNullIfNonExistent(
			HttpResponse response, String headerName) {
		Header headerOrNull = response.getFirstHeader(headerName);
		if (headerOrNull != null) {
			return headerOrNull.getValue();
		} else {
			return null;
		}
	}

	public static HashMap<String, String> getMetadata(AbstractHttpMessage request) {
		return getValuesByHeaderPrefix(request, "x-ms-meta-");
	}

	public static String getRequestId(HttpResponse response) {
		return getHeaderValueOrNullIfNonExistent(response, "x-ms-request-id");
	}

	private static HashMap<String, String> getValuesByHeaderPrefix(
			AbstractHttpMessage response, String prefix) {
		HashMap<String, String> headersByPrefix = new HashMap<String, String>();
		int prefixLength = prefix.length();
		for (Header header : response.getAllHeaders()) {
			if (header.getName() != null && header.getName().startsWith(prefix)) {
				headersByPrefix.put(header.getName().substring(prefixLength), header.getValue());
			}
		}
		return headersByPrefix;
	}
}
