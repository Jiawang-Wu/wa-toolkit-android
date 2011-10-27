package com.windowsazure.samples.android.storageclient;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.HashMap;

import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPut;

public interface AbstractBlobRequest {

	HttpGet list(URI endpoint, CloudBlobContainer container, String s, boolean useFlatBlobListing) throws URISyntaxException, IllegalArgumentException, StorageException, NotImplementedException, IOException;

	void addMetadata(HttpPut request, HashMap m_Metadata);

	HttpPut put(URI transformedAddress, int timeoutInMs,
			BlobProperties m_Properties, BlobType blobType, String leaseID,
			long l) throws IOException, URISyntaxException, StorageException;

}
