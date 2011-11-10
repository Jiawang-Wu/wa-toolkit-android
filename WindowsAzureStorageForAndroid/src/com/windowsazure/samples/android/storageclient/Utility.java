package com.windowsazure.samples.android.storageclient;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.StringWriter;
import java.io.UnsupportedEncodingException;
import java.io.Writer;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.TimeZone;

import org.apache.http.Header;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.message.AbstractHttpMessage;

public class Utility {
	protected static final TimeZone GMT_ZONE = TimeZone.getTimeZone("GMT");

	protected static final TimeZone UTC_ZONE = TimeZone.getTimeZone("UTC");

	protected static final Locale LOCALE_US = Locale.US;

	protected static boolean areCredentialsEqual(
			StorageCredentials storagecredentials,
			StorageCredentials storagecredentials1) {
		if (storagecredentials == storagecredentials1)
			return true;
		if (storagecredentials1 == null
				|| storagecredentials.getClass() != storagecredentials1
						.getClass())
			return false;
		if (storagecredentials instanceof StorageCredentialsAccountAndKey)
			return ((StorageCredentialsAccountAndKey) storagecredentials)
					.toString(true)
					.equals(((StorageCredentialsAccountAndKey) storagecredentials1)
							.toString(true));
		if (storagecredentials instanceof StorageCredentialsSharedAccessSignature)
			return ((StorageCredentialsSharedAccessSignature) storagecredentials)
					.getToken()
					.equals(((StorageCredentialsSharedAccessSignature) storagecredentials1)
							.getToken());
		return storagecredentials.equals(storagecredentials1);
	}

	protected static void assertNotNull(String description, Object object) {
		if (object == null) {
			throw new IllegalArgumentException(description);
		}
	}

	protected static void assertNotNullOrEmpty(String description, String string) {
		assertNotNull(description, string);
		if (string.length() == 0) {
			throw new IllegalArgumentException(
					"The argument must not be an empty string or null:"
							.concat(description));
		}
	}

	public static StorageException generateNewUnexpectedStorageException(
			Exception exception) {
		StorageException storageexception = new StorageException(
				StorageErrorCode.NONE.toString(),
				"Unexpected internal storage client error.", 306, null, null);
		storageexception.initCause(exception);
		return storageexception;
	}

	protected static byte[] getBytesFromLong(long l) {
		byte abyte0[] = new byte[8];
		for (int i = 0; i < 8; i++)
			abyte0[7 - i] = (byte) (int) (l >> 8 * i & 255L);

		return abyte0;
	}

	protected static String getFirstHeaderValueOrEmpty(
			AbstractHttpMessage request, String propertyName) {
		Header header = request.getFirstHeader(propertyName);
		return header != null ? header.getValue() : "";
	}

	protected static String getGMTTime() {
		SimpleDateFormat simpledateformat = new SimpleDateFormat(
				"EEE, dd MMM yyyy HH:mm:ss z", LOCALE_US);
		simpledateformat.setTimeZone(GMT_ZONE);
		String result = simpledateformat.format(new Date());
		if (result.endsWith("GMT+00:00")) {
			result = result.replace("GMT+00:00", "GMT");
		}
		return result;
	}

	protected static String getGMTTime(Date date) {
		SimpleDateFormat simpledateformat = new SimpleDateFormat(
				"EEE, dd MMM yyyy HH:mm:ss z", LOCALE_US);
		simpledateformat.setTimeZone(GMT_ZONE);
		return simpledateformat.format(date);
	}

	public static String getHttpResponseBody(HttpResponse response)
			throws UnsupportedEncodingException, IOException {
		Reader reader = new BufferedReader(new InputStreamReader(response
				.getEntity().getContent(), "UTF-8"));
		char[] buffer = new char[(int) response.getEntity().getContentLength()];
		reader.read(buffer);
		return new String(buffer);
	}

	public static String getIfModifiedSince(HttpRequestBase request) {
		return getFirstHeaderValueOrEmpty(request, "If-Modified-Since");
	}

	protected static String getUTCTimeOrEmpty(Date date) {
		if (date == null) {
			return "";
		} else {
			SimpleDateFormat simpledateformat = new SimpleDateFormat(
					"yyyy-MM-dd'T'HH:mm:ss'Z'", Locale.US);
			simpledateformat.setTimeZone(TimeZone.getTimeZone("UTC"));
			return simpledateformat.format(date);
		}
	}

	protected static IOException initIOException(Exception exception) {
		IOException ioexception = new IOException();
		ioexception.initCause(exception);
		return ioexception;
	}

	protected static boolean isNullOrEmpty(String s) {
		return s == null || s.length() == 0;
	}

	protected static HashMap<String, String> parseAccountString(String s)
			throws IllegalArgumentException {
		String as[] = s.split(";");
		HashMap<String, String> hashmap = new HashMap<String, String>();
		for (int i = 0; i < as.length; i++) {
			int j = as[i].indexOf("=");
			if (j < 1)
				throw new IllegalArgumentException("Invalid Connection String");
			String s1 = as[i].substring(0, j);
			String s2 = as[i].substring(j + 1);
			hashmap.put(s1, s2);
		}

		return hashmap;
	}

	public static String readStringFromStream(InputStream inputStream)
			throws UnsupportedEncodingException, IOException {
		Utility.assertNotNull("inputStream", inputStream);
		Writer writer = new StringWriter();
		Reader reader = new BufferedReader(new InputStreamReader(inputStream,
				"UTF-8"));
		char[] buffer = new char[1024];
		int bytesRead;
		while ((bytesRead = reader.read(buffer)) != -1) {
			writer.write(buffer, 0, bytesRead);
		}
		return writer.toString();
	}

	protected static String safeDecode(String s) throws StorageException {
		if (s == null)
			return null;
		if (s.length() == 0)
			return "";
		try {
			if (s.contains("+")) {
				StringBuilder stringbuilder = new StringBuilder();
				int i = 0;
				for (int j = 0; j < s.length(); j++) {
					if (s.charAt(j) != '+')
						continue;
					if (j > i)
						stringbuilder.append(URLDecoder.decode(
								s.substring(i, j), "UTF-8"));
					stringbuilder.append("+");
					i = j + 1;
				}

				if (i != s.length())
					stringbuilder.append(URLDecoder.decode(
							s.substring(i, s.length()), "UTF-8"));
				return stringbuilder.toString();
			}
		} catch (UnsupportedEncodingException unsupportedencodingexception) {
			throw generateNewUnexpectedStorageException(unsupportedencodingexception);
		}
		try {
			return URLDecoder.decode(s, "UTF-8");
		} catch (UnsupportedEncodingException e) {
			// TODO TO DO I added the try and catch because the exception wasn't
			// being handled
			e.printStackTrace();
			throw new StorageException("UnsupportedEncodingException",
					"UnsupportedEncodingException", 0, null, e);
		}
	}

	protected static String safeEncode(String s) throws StorageException {
		if (s == null)
			return null;
		if (s.length() == 0)
			return "";
		String s1;
		try {
			s1 = URLEncoder.encode(s, "UTF-8");
			if (s.contains(" ")) {
				StringBuilder stringbuilder = new StringBuilder();
				int i = 0;
				for (int j = 0; j < s.length(); j++) {
					if (s.charAt(j) != ' ')
						continue;
					if (j > i)
						stringbuilder.append(URLEncoder.encode(
								s.substring(i, j), "UTF-8"));
					stringbuilder.append("%20");
					i = j + 1;
				}

				if (i != s.length())
					stringbuilder.append(URLEncoder.encode(
							s.substring(i, s.length()), "UTF-8"));
				return stringbuilder.toString();
			}
		} catch (UnsupportedEncodingException unsupportedencodingexception) {
			throw generateNewUnexpectedStorageException(unsupportedencodingexception);
		}
		return s1;
	}
	protected static String safeRelativize(URI uri, URI uri1)
			throws URISyntaxException {
		if (!uri.getHost().equals(uri1.getHost())
				|| !uri.getScheme().equals(uri1.getScheme()))
			return uri1.toString();
		String s = uri.getPath();
		String s1 = uri1.getPath();
		int i = 1;
		int j = 0;
		int k = 0;
		for (; j < s.length(); j++) {
			if (j >= s1.length()) {
				if (s.charAt(j) == '/')
					k++;
				continue;
			}
			if (s.charAt(j) != s1.charAt(j))
				break;
			if (s.charAt(j) == '/')
				i = j + 1;
		}

		if (j == s1.length())
			return (new URI(null, null, null, uri1.getQuery(),
					uri1.getFragment())).toString();
		s1 = s1.substring(i);
		StringBuilder stringbuilder = new StringBuilder();
		for (; k > 0; k--)
			stringbuilder.append("../");

		if (!isNullOrEmpty(s1))
			stringbuilder.append(s1);
		if (!isNullOrEmpty(uri1.getQuery())) {
			stringbuilder.append("?");
			stringbuilder.append(uri1.getQuery());
		}
		if (!isNullOrEmpty(uri1.getFragment())) {
			stringbuilder.append("#");
			stringbuilder.append(uri1.getRawFragment());
		}
		return stringbuilder.toString();
	}

	protected static String trimEnd(String s, char c) {
		int i;
		for (i = s.length() - 1; i > 0 && s.charAt(i) == c; i--)
			;
		return i != s.length() - 1 ? s.substring(i) : s;
	}

	protected static String trimStart(String s) {
		int i;
		for (i = 0; i < s.length() && s.charAt(i) == ' '; i++)
			;
		return s.substring(i);
	}
}
