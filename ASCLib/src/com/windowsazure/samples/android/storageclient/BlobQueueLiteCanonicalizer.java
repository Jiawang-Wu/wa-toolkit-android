package com.windowsazure.samples.android.storageclient;

import java.net.MalformedURLException;
import java.security.InvalidParameterException;

import org.apache.http.client.methods.HttpRequestBase;

final class BlobQueueLiteCanonicalizer extends Canonicalizer {

	BlobQueueLiteCanonicalizer() {
	}

	@Override
	protected String canonicalize(HttpRequestBase httpurlconnection, String s,
			Long long1) throws StorageException, MalformedURLException {
		if (long1.longValue() < -1L)
			throw new InvalidParameterException(
					"ContentLength must be set to -1 or positive Long value");
		else
			return canonicalizeHttpRequestLite(httpurlconnection.getURI()
					.toURL(), s, httpurlconnection.getMethod(),
					Utility.getFirstHeaderValueOrEmpty(httpurlconnection,
							"Content-Type"), long1.longValue(), null,
					httpurlconnection);
	}
}
