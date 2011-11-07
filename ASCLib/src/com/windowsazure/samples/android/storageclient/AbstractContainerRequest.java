package com.windowsazure.samples.android.storageclient;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPut;

public interface AbstractContainerRequest {

	HttpPut create(URI endpoint, boolean createIfNotExist)
			throws IOException, URISyntaxException, IllegalArgumentException,
			StorageException;

	HttpDelete delete(URI endpoint, int timeoutInMs)
			throws IOException, URISyntaxException, IllegalArgumentException,
			StorageException;

	HttpGet getUri(URI endpoint, int timeoutInMs)
			throws IOException, URISyntaxException, StorageException;

	boolean isUsingWasServiceDirectly();

	HttpGet list(URI endpoint, String prefix,
			ContainerListingDetails listingDetails)
			throws NotImplementedException, IOException, URISyntaxException,
			StorageException;

	HttpPut setAcl(URI endpoint,
			BlobContainerPublicAccessType publicAccess)
			throws NotImplementedException, IllegalArgumentException,
			IOException, URISyntaxException, StorageException;

}
