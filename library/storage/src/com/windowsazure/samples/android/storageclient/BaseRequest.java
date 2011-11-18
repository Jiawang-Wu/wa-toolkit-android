package com.windowsazure.samples.android.storageclient;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.security.InvalidKeyException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Iterator;
import java.util.Locale;
import java.util.Map;
import java.util.TimeZone;
import java.util.Map.Entry;

import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpHead;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.client.methods.HttpRequestBase;

final class BaseRequest {
	private static String m_UserAgent;

	public static void addLeaseId(HttpRequestBase request, String leaseId) {
		if (leaseId != null)
			addOptionalHeader(request, "x-ms-lease-id", leaseId);
	}

	public static void addMetadata(HttpRequestBase request, Map<String, String> metadata) {
		if (metadata != null) {
			Map.Entry<String, String> metadataEntry;
			for (Iterator<Entry<String, String>> iterator = metadata.entrySet().iterator(); iterator
					.hasNext(); addMetadata(request, (String) metadataEntry.getKey(),
					metadataEntry.getValue()))
				metadataEntry = iterator.next();

		}
	}

	public static void addMetadata(HttpRequestBase request, String name, String value) {
		Utility.assertNotNullOrEmpty("value", value);
		request.setHeader((new StringBuilder()).append("x-ms-meta-").append(name)
				.toString(), value);
	}

	public static void addOptionalHeader(HttpRequestBase request, String name,
			String value) {
		if (value != null && !value.equals(""))
			request.addHeader(name, value);
	}

	public static void addSnapshot(UriQueryBuilder uriQueryBuilder,
			String snapshotId) throws StorageException {
		if (snapshotId != null) {
			uriQueryBuilder.add("snapshot", snapshotId);
		}
	}

	public static HttpPut create(URI endpoint, UriQueryBuilder uriQueryBuilder)
			throws IOException, URISyntaxException, IllegalArgumentException,
			StorageException {
		if (uriQueryBuilder == null)
			uriQueryBuilder = new UriQueryBuilder();
		HttpPut request = new HttpPut();
		setURIAndHeaders(request, endpoint, uriQueryBuilder);
		return request;
	}

	public static HttpDelete delete(URI endpoint, UriQueryBuilder uriQueryBuilder)
			throws IOException, URISyntaxException, StorageException {
		if (uriQueryBuilder == null)
			uriQueryBuilder = new UriQueryBuilder();
		return setURIAndHeaders(new HttpDelete(), endpoint, uriQueryBuilder);
	}

	public static HttpHead getProperties(URI endpoint,
			UriQueryBuilder uriQueryBuilder) throws IOException,
			URISyntaxException, StorageException {
		if (uriQueryBuilder == null)
			uriQueryBuilder = new UriQueryBuilder();
		return setURIAndHeaders(new HttpHead(), endpoint, uriQueryBuilder);
	}

	private static String getUserAgent() {
		if (m_UserAgent == null)
			m_UserAgent = String
					.format("%s/%s",
							new Object[] {
									"WA-Storage",
									Package.getPackage(
											"com.windowsazure.samples.android.storageclient")
											.getImplementationVersion() });
		return m_UserAgent;
	}

	public static HttpPut setMetadata(URI endpoint, UriQueryBuilder uriQueryBuilder)
			throws IOException, URISyntaxException, StorageException {
		if (uriQueryBuilder == null)
			uriQueryBuilder = new UriQueryBuilder();
		uriQueryBuilder.add("comp", "metadata");
		return setURIAndHeaders(new HttpPut(), endpoint, uriQueryBuilder);
	}

	public static <T extends HttpRequestBase> T setURIAndHeaders(T request,
			URI endpoint, UriQueryBuilder uriQueryBuilder) throws IOException,
			URISyntaxException, StorageException {
		if (uriQueryBuilder == null) {
			uriQueryBuilder = new UriQueryBuilder();
		}
		request.setURI(uriQueryBuilder.addToURI(endpoint));
		request.addHeader("x-ms-version", "2009-09-19");
		request.addHeader("User-Agent", getUserAgent());
		// request.addHeader("Content-Type", "");
		return request;
	}

	protected static final TimeZone GMT_ZONE = TimeZone.getTimeZone("GMT");

	protected static String getGMTTime() {
		SimpleDateFormat simpledateformat = new SimpleDateFormat(
				"EEE, dd MMM yyyy HH:mm:ss z", Locale.US);
		simpledateformat.setTimeZone(GMT_ZONE);
		String result = simpledateformat.format(new Date());
		if (result.endsWith("GMT+00:00")) {
			result = result.replace("GMT+00:00", "GMT");
		}
		return result;
	}
	
	public static void signRequestForBlobAndQueue(HttpRequestBase request,
			Credentials credentials, Long contentLength)
			throws InvalidKeyException, StorageException, MalformedURLException {
		request.setHeader("x-ms-date", getGMTTime());
		Canonicalizer canonicalizer = CanonicalizerFactory
				.getBlobQueueFullCanonicalizer(request);
		String canonicalizedRequest = canonicalizer.canonicalize(request,
				credentials.getAccountName(), contentLength);
		String signature = StorageKey.computeMacSha256(credentials.getKey(), canonicalizedRequest);
		request.setHeader(
				"Authorization",
				String.format("%s %s:%s", "SharedKey",
						credentials.getAccountName(), signature));
	}
	
	public static void signRequestForTable(HttpRequestBase request, Credentials credentials) 
			throws MalformedURLException, StorageException, InvalidKeyException, IllegalArgumentException {
		request.setHeader("x-ms-date", getGMTTime());
		//Beginning with version 2009-09-19 
		request.setHeader("DataServiceVersion", "1.0;NetFx");
		request.setHeader("MaxDataServiceVersion", "2.0;NetFx");
		Canonicalizer canonicalizer = CanonicalizerFactory.getTableFullCanonicalizer(request);
		String canonicalizedRequest = canonicalizer.canonicalize(request, credentials.getAccountName(), -1l);
		String signature = StorageKey.computeMacSha256(credentials.getKey(), canonicalizedRequest);
		request.setHeader("Authorization", String.format("%s %s:%s", "SharedKey", credentials.getAccountName(), signature));		
	}
	
}
