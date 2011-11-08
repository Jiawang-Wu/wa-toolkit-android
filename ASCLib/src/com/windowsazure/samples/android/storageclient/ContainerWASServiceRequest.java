package com.windowsazure.samples.android.storageclient;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.HashMap;

import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.client.methods.HttpRequestBase;

final class ContainerWASServiceRequest implements AbstractContainerRequest {
	public void addMetadata(HttpRequestBase request, HashMap metadata) {
		BaseRequest.addMetadata(request, metadata);
	}

	@Override
	public HttpPut create(URI endpoint, boolean createIfNotExist)
			throws IOException, URISyntaxException, IllegalArgumentException,
			StorageException {
		return create(endpoint, false, false);
	}

	public HttpPut create(URI endpoint, boolean createIfNotExists, boolean isPublic)
			throws IOException, URISyntaxException, IllegalArgumentException,
			StorageException {
		UriQueryBuilder uriquerybuilder = new UriQueryBuilder();
		uriquerybuilder.add("createIfNotExists", "" + createIfNotExists);
		uriquerybuilder.add("isPublic", "" + isPublic);
		return BaseRequest.create(endpoint, uriquerybuilder);
	}

	@Override
	public HttpDelete delete(URI endpoint)
			throws IOException, URISyntaxException, IllegalArgumentException,
			StorageException {
		UriQueryBuilder uriquerybuilder = new UriQueryBuilder();
		return BaseRequest.delete(endpoint, uriquerybuilder);
	}

	@Override
	public HttpGet getUri(URI endpoint)
			throws IOException, URISyntaxException, StorageException {
		HttpGet request = new HttpGet();
		BaseRequest.setURIAndHeaders(request, endpoint,
				new UriQueryBuilder());
		return request;
	}

	@Override
	public boolean isUsingWasServiceDirectly() {
		return false;
	}

	@Override
	public HttpGet list(URI endpoint, String prefix,
			ContainerListingDetails containerlistingdetails)
			throws IOException, URISyntaxException, StorageException {
		UriQueryBuilder uriquerybuilder = new UriQueryBuilder();
		URI listContainersUri = PathUtility.appendPathToUri(endpoint, "containers");
		if (!Utility.isNullOrEmpty(prefix)) {
			uriquerybuilder.add("containerPrefix", prefix);
		}
		return BaseRequest.setURIAndHeaders(new HttpGet(), listContainersUri,
				uriquerybuilder);
	}

	@Override
	public HttpPut setAcl(URI uri, BlobContainerPublicAccessType publicAccess)
			throws NotImplementedException, IllegalArgumentException,
			IOException, URISyntaxException, StorageException {
		return this.create(uri, true,
				publicAccess != BlobContainerPublicAccessType.OFF);
	}
}
