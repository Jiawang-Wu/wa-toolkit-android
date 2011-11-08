package com.windowsazure.samples.android.storageclient;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

import org.apache.http.client.methods.HttpGet;

public class BlobWASServiceRequest implements AbstractBlobRequest {

	@Override
	public HttpGet list(URI endpoint, CloudBlobContainer container,
			String prefix, boolean useFlatBlobListing)
			throws URISyntaxException, IllegalArgumentException,
			StorageException, NotImplementedException, IOException {
		UriQueryBuilder uriQueryBuilder = new UriQueryBuilder();
		URI listBlobsUri = PathUtility.appendPathToUri(endpoint, "blob");
		uriQueryBuilder.add("containerName", container.getName());
		uriQueryBuilder.add("useFlatBlobListing", "" + useFlatBlobListing);
		if (!Utility.isNullOrEmpty(prefix)) {
			uriQueryBuilder.add("blobPrefix", prefix);
		}
		return BaseRequest.setURIAndHeaders(new HttpGet(), listBlobsUri,
				uriQueryBuilder);
	}
}
