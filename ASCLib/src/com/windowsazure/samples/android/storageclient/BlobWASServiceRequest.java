package com.windowsazure.samples.android.storageclient;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.HashMap;

import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPut;

public class BlobWASServiceRequest implements AbstractBlobRequest {

	@Override
	public HttpGet list(URI endpoint, CloudBlobContainer container, String prefix, boolean useFlatBlobListing) throws URISyntaxException, IllegalArgumentException, StorageException, NotImplementedException, IOException {
		UriQueryBuilder uriquerybuilder = new UriQueryBuilder();
		URI listBlobsUri = PathUtility.appendPathToUri(endpoint, "blob");
    	uriquerybuilder.add("containerName", container.getName());
    	uriquerybuilder.add("useFlatBlobListing", "" + useFlatBlobListing);
        if(!Utility.isNullOrEmpty(prefix))
        {
        	uriquerybuilder.add("blobPrefix", prefix);
        }
        return BaseRequest.setURIAndHeaders(new HttpGet(), listBlobsUri, uriquerybuilder);
	}

	@Override
	public void addMetadata(HttpPut request, HashMap metadata) {
		BaseRequest.addMetadata(request, metadata);
	}

	@Override
	public HttpPut put(URI uri, int timeoutInMs,
			BlobProperties blobproperties, BlobType blobType, String leaseID,
			long l) throws IOException, URISyntaxException, StorageException {
		
        if(blobType == BlobType.UNSPECIFIED)
            throw new IllegalArgumentException("The blob type cannot be undefined.");
        HttpPut request = BaseRequest.setURIAndHeaders(new HttpPut(), uri, null);
        BaseRequest.addOptionalHeader(request, "Cache-Control", blobproperties.cacheControl);
        BaseRequest.addOptionalHeader(request, "Content-Type", blobproperties.contentType);
        BaseRequest.addOptionalHeader(request, "Content-MD5", blobproperties.contentMD5);
        BaseRequest.addOptionalHeader(request, "Content-Language", blobproperties.contentLanguage);
        BaseRequest.addOptionalHeader(request, "Content-Encoding", blobproperties.contentEncoding);
        if(blobType == BlobType.PAGE_BLOB)
        {
            request.addHeader("Content-Length", "0");
            request.addHeader("x-ms-blob-type", "PageBlob");
            request.addHeader("x-ms-blob-content-length", String.valueOf(l));
            blobproperties.length = l;
        } else
        {
            request.addHeader("x-ms-blob-type", "BlockBlob");
        }
        BaseRequest.addLeaseId(request, leaseID);
        return request;
	}

}
