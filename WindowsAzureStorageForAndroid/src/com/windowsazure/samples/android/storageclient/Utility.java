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
			StorageCredentials leftCredentials,
			StorageCredentials rightCredentials) {
		if (leftCredentials == rightCredentials)
			return true;
		if (rightCredentials == null
				|| leftCredentials.getClass() != rightCredentials
						.getClass())
			return false;
		if (leftCredentials instanceof StorageCredentialsAccountAndKey)
			return ((StorageCredentialsAccountAndKey) leftCredentials)
					.toString(true)
					.equals(((StorageCredentialsAccountAndKey) rightCredentials)
							.toString(true));
		if (leftCredentials instanceof StorageCredentialsSharedAccessSignature)
			return ((StorageCredentialsSharedAccessSignature) leftCredentials)
					.getToken()
					.equals(((StorageCredentialsSharedAccessSignature) rightCredentials)
							.getToken());
		return leftCredentials.equals(rightCredentials);
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

	protected static byte[] getBytesFromLong(long number) {
		byte abyte0[] = new byte[8];
		for (int i = 0; i < 8; i++)
			abyte0[7 - i] = (byte) (int) (number >> 8 * i & 255L);

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

	protected static boolean isNullOrEmpty(String string) {
		return string == null || string.length() == 0;
	}

	protected static HashMap<String, String> parseAccountString(String accountString)
			throws IllegalArgumentException {
		String arguments[] = accountString.split(";");
		HashMap<String, String> argumentsMap = new HashMap<String, String>();
		for (String argument : arguments) {
			int argumentSeparatorIndex = argument.indexOf("=");
			if (argumentSeparatorIndex < 1)
				throw new IllegalArgumentException("Invalid Connection String");
			String name = argument.substring(0, argumentSeparatorIndex);
			String value = argument.substring(argumentSeparatorIndex + 1);
			argumentsMap.put(name, value);
		}

		return argumentsMap;
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

	protected static String safeDecode(String encodedString) throws StorageException {
		if (encodedString == null)
			return null;
		if (encodedString.length() == 0)
			return "";
		try {
			if (encodedString.contains("+")) {
				StringBuilder stringBuilder = new StringBuilder();
				int index = 0;
				for (int j = 0; j < encodedString.length(); j++) {
					if (encodedString.charAt(j) != '+')
						continue;
					if (j > index)
						stringBuilder.append(URLDecoder.decode(
								encodedString.substring(index, j), "UTF-8"));
					stringBuilder.append("+");
					index = j + 1;
				}

				if (index != encodedString.length())
					stringBuilder.append(URLDecoder.decode(
							encodedString.substring(index, encodedString.length()), "UTF-8"));
				return stringBuilder.toString();
			}
		} catch (UnsupportedEncodingException unsupportedencodingexception) {
			throw generateNewUnexpectedStorageException(unsupportedencodingexception);
		}
		try {
			return URLDecoder.decode(encodedString, "UTF-8");
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
			throw new StorageException("UnsupportedEncodingException",
					"UnsupportedEncodingException", 0, null, e);
		}
	}

	protected static String safeEncode(String string) throws StorageException {
		if (string == null)
			return null;
		if (string.length() == 0)
			return "";
		String stringAsUtf8;
		try {
			stringAsUtf8 = URLEncoder.encode(string, "UTF-8");
			if (string.contains(" ")) {
				StringBuilder stringbuilder = new StringBuilder();
				int i = 0;
				for (int j = 0; j < string.length(); j++) {
					if (string.charAt(j) != ' ')
						continue;
					if (j > i)
						stringbuilder.append(URLEncoder.encode(
								string.substring(i, j), "UTF-8"));
					stringbuilder.append("%20");
					i = j + 1;
				}

				if (i != string.length())
					stringbuilder.append(URLEncoder.encode(
							string.substring(i, string.length()), "UTF-8"));
				return stringbuilder.toString();
			}
		} catch (UnsupportedEncodingException unsupportedencodingexception) {
			throw generateNewUnexpectedStorageException(unsupportedencodingexception);
		}
		return stringAsUtf8;
	}
	protected static String safeRelativize(URI baseUri, URI uriToRelativize)
			throws URISyntaxException {
		if (!baseUri.getHost().equals(uriToRelativize.getHost())
				|| !baseUri.getScheme().equals(uriToRelativize.getScheme()))
			return uriToRelativize.toString();
		String baseUriPath = baseUri.getPath();
		String uriToRelativizePath = uriToRelativize.getPath();
		int i = 1;
		int baseUriPathIndex = 0;
		int directoriesCount = 0;
		for (; baseUriPathIndex < baseUriPath.length(); baseUriPathIndex++) {
			if (baseUriPathIndex >= uriToRelativizePath.length()) {
				if (baseUriPath.charAt(baseUriPathIndex) == '/')
					directoriesCount++;
				continue;
			}
			if (baseUriPath.charAt(baseUriPathIndex) != uriToRelativizePath.charAt(baseUriPathIndex))
				break;
			if (baseUriPath.charAt(baseUriPathIndex) == '/')
				i = baseUriPathIndex + 1;
		}

		if (baseUriPathIndex == uriToRelativizePath.length())
			return (new URI(null, null, null, uriToRelativize.getQuery(),
					uriToRelativize.getFragment())).toString();
		uriToRelativizePath = uriToRelativizePath.substring(i);
		StringBuilder stringbuilder = new StringBuilder();
		for (; directoriesCount > 0; directoriesCount--)
			stringbuilder.append("../");

		if (!isNullOrEmpty(uriToRelativizePath))
			stringbuilder.append(uriToRelativizePath);
		if (!isNullOrEmpty(uriToRelativize.getQuery())) {
			stringbuilder.append("?");
			stringbuilder.append(uriToRelativize.getQuery());
		}
		if (!isNullOrEmpty(uriToRelativize.getFragment())) {
			stringbuilder.append("#");
			stringbuilder.append(uriToRelativize.getRawFragment());
		}
		return stringbuilder.toString();
	}

	protected static String trimEnd(String string, char characterToTrim) {
		int i;
		for (i = string.length() - 1; i > 0 && string.charAt(i) == characterToTrim; i--)
			;
		return i != string.length() - 1 ? string.substring(i) : string;
	}

	protected static String trimStart(String string) {
		int i;
		for (i = 0; i < string.length() && string.charAt(i) == ' '; i++)
			;
		return string.substring(i);
	}
}
