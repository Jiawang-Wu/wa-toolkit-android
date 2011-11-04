package com.windowsazure.samples.android.storageclient;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPut;

public interface AbstractContainerRequest {

	HttpPut create(URI containerOperationsUri, boolean createIfNotExist)
			throws IOException, URISyntaxException, IllegalArgumentException,
			StorageException;

	HttpDelete delete(URI m_ContainerOperationsUri, int timeoutInMs)
			throws IOException, URISyntaxException, IllegalArgumentException,
			StorageException;

	HttpGet getUri(URI containerOperationsUri, int timeoutInMs)
			throws IOException, URISyntaxException, StorageException;

	boolean isUsingWasServiceDirectly();

	HttpGet list(URI uri, String prefix,
			ContainerListingDetails containerlistingdetails)
			throws NotImplementedException, IOException, URISyntaxException,
			StorageException;

	HttpPut setAcl(URI m_ContainerOperationsUri,
			BlobContainerPublicAccessType publicAccess)
			throws NotImplementedException, IllegalArgumentException,
			IOException, URISyntaxException, StorageException;

}
