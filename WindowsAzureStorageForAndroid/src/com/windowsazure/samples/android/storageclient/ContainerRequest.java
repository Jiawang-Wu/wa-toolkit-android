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
	public static void addMetadata(HttpRequestBase request, HashMap<String, String> metadata) {
		BaseRequest.addMetadata(request, metadata);
	}

	protected static UriQueryBuilder getContainerUriQueryBuilder()
			throws StorageException {
		UriQueryBuilder uriQueryBuilder = new UriQueryBuilder();
		try {
			uriQueryBuilder.add("restype", "container");
		} catch (IllegalArgumentException illegalargumentexception) {
			throw Utility
					.generateNewUnexpectedStorageException(illegalargumentexception);
		}
		return uriQueryBuilder;
	}

	public HttpHead getProperties(URI endpoint)
			throws IllegalArgumentException, IOException, URISyntaxException,
			StorageException {
		UriQueryBuilder uriQueryBuilder = getContainerUriQueryBuilder();
		return BaseRequest.getProperties(endpoint, uriQueryBuilder);
	}

	public static HttpPut setMetadata(URI endpoint) throws IllegalArgumentException,
			IOException, URISyntaxException, StorageException {
		UriQueryBuilder uriQueryBuilder = getContainerUriQueryBuilder();
		return BaseRequest.setMetadata(endpoint, uriQueryBuilder);
	}

	@Override
	public HttpPut create(URI endpoint, boolean createIfNotExist)
			throws IOException, URISyntaxException, IllegalArgumentException,
			StorageException {
		UriQueryBuilder uriQueryBuilder = getContainerUriQueryBuilder();
		return BaseRequest.create(endpoint, uriQueryBuilder);
	}

	@Override
	public HttpDelete delete(URI endpoint) throws IOException,
			URISyntaxException, IllegalArgumentException, StorageException {
		UriQueryBuilder uriQueryBuilder = getContainerUriQueryBuilder();
		return BaseRequest.delete(endpoint, uriQueryBuilder);
	}

	@Override
	public HttpGet getUri(URI endpoint)
			throws IOException, URISyntaxException, StorageException {
		return null;
	}

	@Override
	public boolean isUsingWasServiceDirectly() {
		return true;
	}

	@Override
	public HttpGet list(URI endpoint, String prefix,
			ContainerListingDetails listingDetails)
			throws NotImplementedException, IOException, URISyntaxException,
			StorageException {
		UriQueryBuilder uriQueryBuilder = getContainerUriQueryBuilder();
		uriQueryBuilder.add("comp", "list");
		if (!Utility.isNullOrEmpty(prefix)) {
			uriQueryBuilder.add("prefix", prefix);
		}
		if (listingDetails == ContainerListingDetails.ALL
				|| listingDetails == ContainerListingDetails.METADATA) {
			uriQueryBuilder.add("include", "metadata");
		}
		return BaseRequest
				.setURIAndHeaders(new HttpGet(), endpoint, uriQueryBuilder);
	}

    public static HttpGet getAcl(URI endpoint)
            throws IOException, URISyntaxException, StorageException
        {
            UriQueryBuilder uriQueryBuilder = getContainerUriQueryBuilder();
            uriQueryBuilder.add("comp", "acl");
            return BaseRequest.setURIAndHeaders(new HttpGet(), endpoint, uriQueryBuilder);
        }
    
	@Override
	public HttpPut setAcl(URI endpoint,
			BlobContainerPublicAccessType publicAccess)
			throws NotImplementedException, IOException, URISyntaxException,
			StorageException {
		UriQueryBuilder uriQueryBuilder = getContainerUriQueryBuilder();
		uriQueryBuilder.add("comp", "acl");
		HttpPut request = BaseRequest.setURIAndHeaders(new HttpPut(),
				endpoint, uriQueryBuilder);
		if (publicAccess != BlobContainerPublicAccessType.OFF) {
			request.setHeader("x-ms-blob-public-access", publicAccess
					.toString().toLowerCase());
		}
		return request;
	}
}
