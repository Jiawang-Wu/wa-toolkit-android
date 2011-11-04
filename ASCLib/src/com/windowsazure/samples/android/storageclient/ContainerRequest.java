package com.windowsazure.samples.android.storageclient;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.HashMap;

import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpHead;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.client.methods.HttpRequestBase;

final class ContainerRequest implements AbstractContainerRequest {
	public static void addMetadata(HttpRequestBase request, HashMap hashmap) {
		BaseRequest.addMetadata(request, hashmap);
	}

	protected static UriQueryBuilder getContainerUriQueryBuilder()
			throws StorageException {
		UriQueryBuilder uriquerybuilder = new UriQueryBuilder();
		try {
			uriquerybuilder.add("restype", "container");
		} catch (IllegalArgumentException illegalargumentexception) {
			throw Utility
					.generateNewUnexpectedStorageException(illegalargumentexception);
		}
		return uriquerybuilder;
	}

	public static HttpHead getProperties(URI uri)
			throws IllegalArgumentException, IOException, URISyntaxException,
			StorageException {
		UriQueryBuilder uriquerybuilder = getContainerUriQueryBuilder();
		return BaseRequest.getProperties(uri, uriquerybuilder);
	}

	public static HttpPut setMetadata(URI uri) throws IllegalArgumentException,
			IOException, URISyntaxException, StorageException {
		UriQueryBuilder uriquerybuilder = getContainerUriQueryBuilder();
		return BaseRequest.setMetadata(uri, uriquerybuilder);
	}

	@Override
	public HttpPut create(URI uri, boolean createIfNotExist)
			throws IOException, URISyntaxException, IllegalArgumentException,
			StorageException {
		UriQueryBuilder uriquerybuilder = getContainerUriQueryBuilder();
		return BaseRequest.create(uri, uriquerybuilder);
	}

	@Override
	public HttpDelete delete(URI uri, int i) throws IOException,
			URISyntaxException, IllegalArgumentException, StorageException {
		UriQueryBuilder uriquerybuilder = getContainerUriQueryBuilder();
		return BaseRequest.delete(uri, uriquerybuilder);
	}

	@Override
	public HttpGet getUri(URI containerOperationsUri, int timeoutInMs)
			throws IOException, URISyntaxException, StorageException {
		return null;
	}

	@Override
	public boolean isUsingWasServiceDirectly() {
		return true;
	}

	@Override
	public HttpGet list(URI uri, String prefix,
			ContainerListingDetails containerlistingdetails)
			throws NotImplementedException, IOException, URISyntaxException,
			StorageException {
		UriQueryBuilder uriquerybuilder = getContainerUriQueryBuilder();
		uriquerybuilder.add("comp", "list");
		if (!Utility.isNullOrEmpty(prefix)) {
			uriquerybuilder.add("prefix", prefix);
		}
		if (containerlistingdetails == ContainerListingDetails.ALL
				|| containerlistingdetails == ContainerListingDetails.METADATA) {
			uriquerybuilder.add("include", "metadata");
		}
		return BaseRequest
				.setURIAndHeaders(new HttpGet(), uri, uriquerybuilder);
	}

	@Override
	public HttpPut setAcl(URI m_ContainerOperationsUri,
			BlobContainerPublicAccessType publicAccess)
			throws NotImplementedException, IOException, URISyntaxException,
			StorageException {
		UriQueryBuilder uriquerybuilder = getContainerUriQueryBuilder();
		uriquerybuilder.add("comp", "acl");
		HttpPut request = BaseRequest.setURIAndHeaders(new HttpPut(),
				m_ContainerOperationsUri, uriquerybuilder);
		if (publicAccess != BlobContainerPublicAccessType.OFF) {
			request.setHeader("x-ms-blob-public-access", publicAccess
					.toString().toLowerCase());
		}
		return request;
	}
}
