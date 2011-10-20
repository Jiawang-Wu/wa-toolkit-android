package com.windowsazure.samples.android.storageclient;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.HashMap;

public interface AbstractContainerRequest {

	void addMetadata(HttpURLConnection httpurlconnection, HashMap m_Metadata);

	HttpURLConnection create(URI containerOperationsUri, int timeoutInMs) 
			throws IOException, URISyntaxException, 
			IllegalArgumentException, StorageException;

	HttpURLConnection getUri(URI containerOperationsUri, int timeoutInMs) throws IOException, URISyntaxException, StorageException;

	boolean isUsingWasServiceDirectly();

	HttpURLConnection delete(URI m_ContainerOperationsUri, int timeoutInMs) throws IOException, URISyntaxException, IllegalArgumentException, StorageException;
	
}

