package com.windowsazure.samples.android.storageclient;

import java.net.MalformedURLException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;
import java.util.Map.Entry;

import org.apache.http.Header;
import org.apache.http.client.methods.HttpRequestBase;

abstract class Canonicalizer {
	private static void addCanonicalizedHeaders(HttpRequestBase request,
			StringBuilder stringBuilder) {
		Header[] headers = request.getAllHeaders();
		ArrayList<String> xMsHeaderNames = new ArrayList<String>();
		for (Header header : headers) {
			if (header.getName().toLowerCase(Locale.US).startsWith("x-ms-"))
				xMsHeaderNames.add(header.getName().toLowerCase(Locale.US));
		}
		Collections.sort(xMsHeaderNames);
		StringBuilder singleElementStringBuilder;
		for (Iterator<String> headerNameIterator = xMsHeaderNames.iterator(); headerNameIterator.hasNext(); appendCanonicalizedElement(
				stringBuilder, singleElementStringBuilder.toString())) {
			String headerName = (String) headerNameIterator.next();
			singleElementStringBuilder = new StringBuilder(headerName);
			String separator = ":";
			ArrayList<String> headerValues = getHeaderValues(headers, headerName);
			for (Iterator<String> headerValueIterator = headerValues.iterator(); headerValueIterator
					.hasNext();) {
				String headerValue = (String) headerValueIterator.next();
				String headerValueWithoutNewLines = headerValue.replace("\r\n", "");
				singleElementStringBuilder.append(separator);
				singleElementStringBuilder.append(headerValueWithoutNewLines);
				separator = ",";
			}

		}

	}

	private static void appendCanonicalizedElement(StringBuilder stringBuilder,
			String canonicalizedElementString) {
		stringBuilder.append("\n");
		stringBuilder.append(canonicalizedElementString);
	}

	protected static final TimeZone GMT_ZONE = TimeZone.getTimeZone("GMT");

	protected static String getGMTTime(Date date) {
		SimpleDateFormat simpledateformat = new SimpleDateFormat(
				"EEE, dd MMM yyyy HH:mm:ss z", Locale.US);
		simpledateformat.setTimeZone(GMT_ZONE);
		return simpledateformat.format(date);
	}

	public static String getIfModifiedSince(HttpRequestBase request) {
		return Utility.getFirstHeaderValueOrEmpty(request, "If-Modified-Since");
	}

	//TODO: refactoring (this should be abstract an implemented on the subclasses) 
	protected static String canonicalizeHttpRequest(URL url, String accountName,
			String httpMethodName, String contentTypeString, long contentLength, String defaultDate, HttpRequestBase request)
			throws StorageException {
		StringBuilder stringBuilder = new StringBuilder(request.getMethod());
		appendCanonicalizedElement(stringBuilder,
				Utility.getFirstHeaderValueOrEmpty(request, "Content-Encoding"));
		appendCanonicalizedElement(stringBuilder,
				Utility.getFirstHeaderValueOrEmpty(request, "Content-Language"));
		appendCanonicalizedElement(stringBuilder, contentLength != -1L ? String.valueOf(contentLength)
				: "");
		appendCanonicalizedElement(stringBuilder,
				Utility.getFirstHeaderValueOrEmpty(request, "Content-MD5"));
		appendCanonicalizedElement(stringBuilder, contentTypeString == null ? "" : contentTypeString);
		String xMsDateString = Utility.getFirstHeaderValueOrEmpty(request, "x-ms-date");
		appendCanonicalizedElement(stringBuilder, xMsDateString.equals("") ? defaultDate : "");
		String ifModifiedSinceString = "";
		String ifModifiedSince = getIfModifiedSince(request);
		if (ifModifiedSince != null && ifModifiedSince.length() != 0) {
			ifModifiedSinceString = getGMTTime(new Date(ifModifiedSince));
		}
		appendCanonicalizedElement(stringBuilder, ifModifiedSinceString);
		appendCanonicalizedElement(stringBuilder,
				Utility.getFirstHeaderValueOrEmpty(request, "If-Match"));
		appendCanonicalizedElement(stringBuilder,
				Utility.getFirstHeaderValueOrEmpty(request, "If-None-Match"));
		appendCanonicalizedElement(stringBuilder,
				Utility.getFirstHeaderValueOrEmpty(request,
						"If-Unmodified-Since"));
		appendCanonicalizedElement(stringBuilder,
				Utility.getFirstHeaderValueOrEmpty(request, "Range"));
		addCanonicalizedHeaders(request, stringBuilder);
		appendCanonicalizedElement(stringBuilder,
				getCanonicalizedResource(url, accountName));
		String result = stringBuilder.toString();
		return result;
	}

	//TODO: refactoring (this should be abstract an implemented on the subclasses) 
	protected static String canonicalizeHttpRequestLite(URL url, String accountName,
			String httpMethodName, String contentTypeString, long contentLength, String defaultDate, HttpRequestBase request)
			throws StorageException {
		StringBuilder stringbuilder = new StringBuilder(request.getMethod());
		String s4 = Utility.getFirstHeaderValueOrEmpty(request, "Content-MD5");
		appendCanonicalizedElement(stringbuilder, s4);
		appendCanonicalizedElement(stringbuilder, contentTypeString);
		String s5 = Utility.getFirstHeaderValueOrEmpty(request, "x-ms-date");
		appendCanonicalizedElement(stringbuilder, s5.equals("") ? defaultDate : "");
		addCanonicalizedHeaders(request, stringbuilder);
		appendCanonicalizedElement(stringbuilder, getCanonicalizedResource(url, accountName));
		return stringbuilder.toString();
	}
	
	//TODO: refactoring (this should be named abstract canonicalizeHttpRequest an implemented on the subclasses) 
	protected static String canonicalizeHttpTableRequest(URL url, String accountName, String httpMethodName, String contentTypeString, 
			long contentLength, String defaultDate, HttpRequestBase request) throws StorageException {
		StringBuilder stringBuilder = new StringBuilder(request.getMethod());
		appendCanonicalizedElement(stringBuilder, Utility.getFirstHeaderValueOrEmpty(request, "Content-MD5"));
		appendCanonicalizedElement(stringBuilder, contentTypeString == null ? "" : contentTypeString);
		String xMsDateString = Utility.getFirstHeaderValueOrEmpty(request, "x-ms-date");
		appendCanonicalizedElement(stringBuilder, xMsDateString.equals("") ? defaultDate : xMsDateString);
		appendCanonicalizedElement(stringBuilder, getCanonicalizedResource(url, accountName));
		String result = stringBuilder.toString();
		return result;		
	}

	private static String getCanonicalizedResource(URL url, String accountName)
			throws StorageException {
		StringBuilder accountNameStringBuilder = new StringBuilder("/");
		accountNameStringBuilder.append(accountName);
		accountNameStringBuilder.append(url.getPath());
		StringBuilder stringBuilder = new StringBuilder(
				accountNameStringBuilder.toString());
		HashMap<String, String[]> queryArguments = PathUtility.parseQueryString(url.getQuery());
		HashMap<String, String> queryArgumentsToCanonicalize = new HashMap<String, String>();
		java.util.Map.Entry<String, String[]> queryArgumentEntry;
		StringBuilder queryArgumentStringBuilder;
		for (Iterator<Entry<String, String[]>> queryArgumentIterator = queryArguments.entrySet().iterator(); queryArgumentIterator
				.hasNext(); queryArgumentsToCanonicalize.put(
				queryArgumentEntry.getKey() != null ? queryArgumentEntry.getKey()
						.toLowerCase(Locale.US) : null, queryArgumentStringBuilder
						.toString())) {
			queryArgumentEntry = (Entry<String, String[]>) queryArgumentIterator.next();
			List<String> list = Arrays.asList(queryArgumentEntry.getValue());
			Collections.sort(list);
			queryArgumentStringBuilder = new StringBuilder();
			String s2;
			for (Iterator<String> iterator2 = list.iterator(); iterator2.hasNext(); queryArgumentStringBuilder
					.append(s2)) {
				s2 = (String) iterator2.next();
				if (queryArgumentStringBuilder.length() > 0)
					queryArgumentStringBuilder.append(",");
			}

		}

		ArrayList<String> canonicalizedQueryArguments = new ArrayList<String>(queryArgumentsToCanonicalize.keySet());
		Collections.sort(canonicalizedQueryArguments);
		StringBuilder stringbuilder3;
		for (Iterator<String> iterator1 = canonicalizedQueryArguments.iterator(); iterator1.hasNext(); appendCanonicalizedElement(
				stringBuilder, stringbuilder3.toString())) {
			String s1 = (String) iterator1.next();
			stringbuilder3 = new StringBuilder();
			stringbuilder3.append(s1);
			stringbuilder3.append(":");
			stringbuilder3.append((String) queryArgumentsToCanonicalize.get(s1));
		}

		return stringBuilder.toString();
	}

	private static ArrayList<String> getHeaderValues(Header[] headers, String name) {
		ArrayList<String> trimmedValues = new ArrayList<String>();
		List<String> values = new ArrayList<String>();
		for (Header entry : headers) {
			if (entry.getName().toLowerCase(Locale.US).equals(name)) {
				values.add(entry.getValue());
			}
		}
		if (values.size() != 0) {
			String s1;
			for (Iterator<String> iterator1 = values.iterator(); iterator1.hasNext(); trimmedValues
					.add(Utility.trimStart(s1)))
				s1 = (String) iterator1.next();

		}
		return trimmedValues;
	}

	Canonicalizer() {
	}

	protected abstract String canonicalize(HttpRequestBase request, String accountName,
			Long contentLength) throws StorageException, MalformedURLException;
}
