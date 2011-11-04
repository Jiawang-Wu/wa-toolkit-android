package com.windowsazure.samples.android.storageclient;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

import org.apache.http.client.methods.HttpGet;

public interface AbstractBlobRequest {

	HttpGet list(URI endpoint, CloudBlobContainer container, String s,
			boolean useFlatBlobListing) throws URISyntaxException,
			IllegalArgumentException, StorageException,
			NotImplementedException, IOException;
}
