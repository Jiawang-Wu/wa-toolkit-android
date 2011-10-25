package com.windowsazure.samples.android.storageclient;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.HashMap;

import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.client.methods.HttpRequestBase;

public interface AbstractContainerRequest {

	void addMetadata(HttpRequestBase request, HashMap m_Metadata);

	HttpPut create(URI containerOperationsUri, int timeoutInMs)
			throws IOException, URISyntaxException,
			IllegalArgumentException, StorageException;

	HttpGet getUri(URI containerOperationsUri, int timeoutInMs) throws IOException, URISyntaxException, StorageException;

	boolean isUsingWasServiceDirectly();

	HttpDelete delete(URI m_ContainerOperationsUri, int timeoutInMs) throws IOException, URISyntaxException, IllegalArgumentException, StorageException;

}

