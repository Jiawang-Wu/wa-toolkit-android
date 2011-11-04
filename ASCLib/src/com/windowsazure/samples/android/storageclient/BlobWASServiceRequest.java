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
		UriQueryBuilder uriquerybuilder = new UriQueryBuilder();
		URI listBlobsUri = PathUtility.appendPathToUri(endpoint, "blob");
		uriquerybuilder.add("containerName", container.getName());
		uriquerybuilder.add("useFlatBlobListing", "" + useFlatBlobListing);
		if (!Utility.isNullOrEmpty(prefix)) {
			uriquerybuilder.add("blobPrefix", prefix);
		}
		return BaseRequest.setURIAndHeaders(new HttpGet(), listBlobsUri,
				uriquerybuilder);
	}
}
