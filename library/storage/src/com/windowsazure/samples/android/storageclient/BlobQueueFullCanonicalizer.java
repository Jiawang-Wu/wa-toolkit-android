package com.windowsazure.samples.android.storageclient;

import java.net.MalformedURLException;
import java.security.InvalidParameterException;

import org.apache.http.client.methods.HttpRequestBase;

final class BlobQueueFullCanonicalizer extends Canonicalizer {

	BlobQueueFullCanonicalizer() {
	}

	@Override
	protected String canonicalize(HttpRequestBase request, String accountName,
			Long contentLength) throws StorageException, MalformedURLException {
		if (contentLength.longValue() < -1L)
			throw new InvalidParameterException(
					"ContentLength must be set to -1 or positive Long value");
		else
			return canonicalizeHttpRequest(request.getURI().toURL(),
					accountName, request.getMethod(),
					Utility.getFirstHeaderValueOrEmpty(request,
							"Content-Type"), contentLength.longValue(), null,
					request);
	}
	
}
