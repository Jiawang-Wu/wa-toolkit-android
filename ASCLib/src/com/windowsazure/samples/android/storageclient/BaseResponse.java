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
		String s = response.getFirstHeader("Date").getValue();
		return s != null ? s : response.getFirstHeader("x-ms-date").getValue();
	}

	public static String getEtag(HttpResponse response) {
		return getHeaderValueOrNullIfNonExistent(response, "Etag");
	}

	private static String getHeaderValueOrNullIfNonExistent(
			HttpResponse response, String headerName) {
		Header header = response.getFirstHeader(headerName);
		if (header != null) {
			return header.getValue();
		} else {
			return null;
		}
	}

	public static HashMap getMetadata(AbstractHttpMessage request) {
		return getValuesByHeaderPrefix(request, "x-ms-meta-");
	}

	public static String getRequestId(HttpResponse response) {
		return getHeaderValueOrNullIfNonExistent(response, "x-ms-request-id");
	}

	private static HashMap getValuesByHeaderPrefix(
			AbstractHttpMessage response, String s) {
		HashMap hashmap = new HashMap();
		int i = s.length();
		for (Header header : response.getAllHeaders()) {
			if (header.getName() != null && header.getName().startsWith(s)) {
				hashmap.put(header.getName().substring(i), header.getValue());
			}
		}
		return hashmap;
	}
}
