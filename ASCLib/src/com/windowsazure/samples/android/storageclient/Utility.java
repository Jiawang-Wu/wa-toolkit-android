package com.windowsazure.samples.android.storageclient;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.StringWriter;
import java.io.UnsupportedEncodingException;
import java.io.Writer;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

public class Utility {
	protected static boolean determinePathStyleFromUri(URI endpointUri,
			String accountName) {
		if (accountName == null) {
			return !isNullOrEmpty(endpointUri.getPath());
		}

		String s1 = endpointUri.getPath();
		if (!isNullOrEmpty(s1) && s1.startsWith("/")) {
			s1 = s1.substring(1);
		}

		if (isNullOrEmpty(s1) || endpointUri.getHost().startsWith(accountName)) {
			return false;
		}

		return !isNullOrEmpty(s1) && s1.startsWith(accountName);
	}

    protected static String getUTCTimeOrEmpty(Date date)
    {
        if(date == null)
        {
            return "";
        } else
        {
            SimpleDateFormat simpledateformat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'", Locale.US);
            simpledateformat.setTimeZone(TimeZone.getTimeZone("UTC"));
            return simpledateformat.format(date);
        }
    }

    protected static String safeEncode(String s)
            throws StorageException
        {
            if(s == null)
                return null;
            if(s.length() == 0)
                return "";
            String s1;
            try
            {
                s1 = URLEncoder.encode(s, "UTF-8");
                if(s.contains(" "))
                {
                    StringBuilder stringbuilder = new StringBuilder();
                    int i = 0;
                    for(int j = 0; j < s.length(); j++)
                    {
                        if(s.charAt(j) != ' ')
                            continue;
                        if(j > i)
                            stringbuilder.append(URLEncoder.encode(s.substring(i, j), "UTF-8"));
                        stringbuilder.append("%20");
                        i = j + 1;
                    }

                    if(i != s.length())
                        stringbuilder.append(URLEncoder.encode(s.substring(i, s.length()), "UTF-8"));
                    return stringbuilder.toString();
                }
            }
            catch(UnsupportedEncodingException unsupportedencodingexception)
            {
                throw generateNewUnexpectedStorageException(unsupportedencodingexception);
            }
            return s1;
        }

    protected static String safeDecode(String s)
            throws StorageException
        {
            if(s == null)
                return null;
            if(s.length() == 0)
                return "";
            try
            {
                if(s.contains("+"))
                {
                    StringBuilder stringbuilder = new StringBuilder();
                    int i = 0;
                    for(int j = 0; j < s.length(); j++)
                    {
                        if(s.charAt(j) != '+')
                            continue;
                        if(j > i)
                            stringbuilder.append(URLDecoder.decode(s.substring(i, j), "UTF-8"));
                        stringbuilder.append("+");
                        i = j + 1;
                    }

                    if(i != s.length())
                        stringbuilder.append(URLDecoder.decode(s.substring(i, s.length()), "UTF-8"));
                    return stringbuilder.toString();
                }
            }
            catch(UnsupportedEncodingException unsupportedencodingexception)
            {
                throw generateNewUnexpectedStorageException(unsupportedencodingexception);
            }
            try {
    			return URLDecoder.decode(s, "UTF-8");
    		} catch (UnsupportedEncodingException e) {
    			// TODO TO DO I added the try and catch because the exception wasn't being handled
    			e.printStackTrace();
    			throw new StorageException("UnsupportedEncodingException", "UnsupportedEncodingException", 0, null,
    					e);
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
					.toString(Boolean.valueOf(true))
					.equals(((StorageCredentialsAccountAndKey) storagecredentials1)
							.toString(Boolean.valueOf(true)));
		if (storagecredentials instanceof StorageCredentialsSharedAccessSignature)
			return ((StorageCredentialsSharedAccessSignature) storagecredentials)
					.getToken()
					.equals(((StorageCredentialsSharedAccessSignature) storagecredentials1)
							.getToken());
		return storagecredentials.equals(storagecredentials1);
	}

	protected static boolean isNullOrEmpty(String s) {
		return s == null || s.length() == 0;
	}

	protected static String trimEnd(String s, char c) {
		int i;
		for (i = s.length() - 1; i > 0 && s.charAt(i) == c; i--)
			;
		return i != s.length() - 1 ? s.substring(i) : s;
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
	/*
	 * protected static String getStandardHeaderValue(HttpURLConnection
	 * httpurlconnection, String s) { String s1 =
	 * httpurlconnection.getRequestProperty(s); return s1 != null ? s1 : ""; }
	 * 
	 * protected static String getGMTTime(Date date) { SimpleDateFormat
	 * simpledateformat = new SimpleDateFormat("EEE, dd MMM yyyy HH:mm:ss z",
	 * LOCALE_US); simpledateformat.setTimeZone(GMT_ZONE); return
	 * simpledateformat.format(date); }
	 * 
	 * protected static String getGMTTime() { SimpleDateFormat simpledateformat
	 * = new SimpleDateFormat("EEE, dd MMM yyyy HH:mm:ss z", LOCALE_US);
	 * simpledateformat.setTimeZone(GMT_ZONE); return
	 * simpledateformat.format(new Date()); }
	 */

	public static String getHttpResponseBody(HttpURLConnection httpurlconnection) throws UnsupportedEncodingException, IOException {
		 Reader reader = new BufferedReader(new InputStreamReader(httpurlconnection.getInputStream(), "UTF-8"));
		 char[] buffer = new char[httpurlconnection.getContentLength()];
		 reader.read(buffer);
		 return new String(buffer);
	}
	public static String readStringFromStream(InputStream inputStream) throws UnsupportedEncodingException, IOException {
		Writer writer = new StringWriter();
		 Reader reader = new BufferedReader(new InputStreamReader(inputStream, "UTF-8"));
		 char[] buffer = new char[1024];
		 int bytesRead;
		 while ((bytesRead = reader.read(buffer)) != -1)
		 {
			 writer.write(buffer, 0, bytesRead);
		 }
		 return writer.toString();
	}
}
