package com.windowsazure.samples.android.storageclient;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.security.InvalidKeyException;
import java.util.HashMap;
import java.util.Iterator;

import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpHead;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.client.methods.HttpRequestBase;

final class BaseRequest {
	private static String m_UserAgent;

	public static void addLeaseId(HttpRequestBase request, String s) {
		if (s != null)
			addOptionalHeader(request, "x-ms-lease-id", s);
	}

	public static void addMetadata(HttpRequestBase request, HashMap hashmap) {
		if (hashmap != null) {
			java.util.Map.Entry entry;
			for (Iterator iterator = hashmap.entrySet().iterator(); iterator
					.hasNext(); addMetadata(request, (String) entry.getKey(),
					(String) entry.getValue()))
				entry = (java.util.Map.Entry) iterator.next();

		}
	}

	public static void addMetadata(HttpRequestBase request, String s, String s1) {
		Utility.assertNotNullOrEmpty("value", s1);
		request.setHeader((new StringBuilder()).append("x-ms-meta-").append(s)
				.toString(), s1);
	}

	public static void addOptionalHeader(HttpRequestBase request, String s,
			String s1) {
		if (s1 != null && !s1.equals(""))
			request.addHeader(s, s1);
	}

	public static void addSnapshot(UriQueryBuilder uriquerybuilder,
			String snapshotId) throws StorageException {
		if (snapshotId != null) {
			uriquerybuilder.add("snapshot", snapshotId);
		}
	}

	public static HttpPut create(URI uri, UriQueryBuilder uriquerybuilder)
			throws IOException, URISyntaxException, IllegalArgumentException,
			StorageException {
		if (uriquerybuilder == null)
			uriquerybuilder = new UriQueryBuilder();
		HttpPut put = new HttpPut();
		setURIAndHeaders(put, uri, uriquerybuilder);
		return put;
	}

	public static HttpDelete delete(URI uri, UriQueryBuilder uriquerybuilder)
			throws IOException, URISyntaxException, StorageException {
		if (uriquerybuilder == null)
			uriquerybuilder = new UriQueryBuilder();
		return setURIAndHeaders(new HttpDelete(), uri, uriquerybuilder);
	}

	public static HttpHead getProperties(URI uri,
			UriQueryBuilder uriquerybuilder) throws IOException,
			URISyntaxException, StorageException {
		if (uriquerybuilder == null)
			uriquerybuilder = new UriQueryBuilder();
		return setURIAndHeaders(new HttpHead(), uri, uriquerybuilder);
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

	public static HttpPut setMetadata(URI uri, UriQueryBuilder uriquerybuilder)
			throws IOException, URISyntaxException, StorageException {
		if (uriquerybuilder == null)
			uriquerybuilder = new UriQueryBuilder();
		uriquerybuilder.add("comp", "metadata");
		return setURIAndHeaders(new HttpPut(), uri, uriquerybuilder);
	}

	public static <T extends HttpRequestBase> T setURIAndHeaders(T request,
			URI uri, UriQueryBuilder uriquerybuilder) throws IOException,
			URISyntaxException, StorageException {
		if (uriquerybuilder == null) {
			uriquerybuilder = new UriQueryBuilder();
		}
		request.setURI(uriquerybuilder.addToURI(uri));
		request.addHeader("x-ms-version", "2009-09-19");
		request.addHeader("User-Agent", getUserAgent());
		// request.addHeader("Content-Type", "");
		return request;
	}

	public static void signRequestForBlobAndQueue(HttpRequestBase request,
			Credentials credentials, Long contentLength)
			throws InvalidKeyException, StorageException, MalformedURLException {
		request.setHeader("x-ms-date", Utility.getGMTTime());
		Canonicalizer canonicalizer = CanonicalizerFactory
				.getBlobQueueFullCanonicalizer(request);
		String s = canonicalizer.canonicalize(request,
				credentials.getAccountName(), contentLength);
		String s1 = StorageKey.computeMacSha256(credentials.getKey(), s);
		request.setHeader(
				"Authorization",
				String.format("%s %s:%s", "SharedKey",
						credentials.getAccountName(), s1));
	}
}
