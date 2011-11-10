package com.windowsazure.samples.android.storageclient;

import java.net.URI;
import java.util.Date;

import org.apache.http.message.AbstractHttpMessage;

final class ContainerResponse extends BaseResponse {
	public static BlobContainerAttributes getAttributes(URI transformedContainerUri,
			RequestResult result) throws StorageException {
		BlobContainerAttributes attributes = new BlobContainerAttributes();
		java.net.URI containerUri = PathUtility.stripURIQueryAndFragment(transformedContainerUri);
		attributes.uri = containerUri;
		attributes.name = PathUtility.getContainerNameFromUri(containerUri);
		BlobContainerProperties properties = attributes.properties;
		properties.eTag = BaseResponse
				.getEtag(result.httpResponse);
		properties.lastModified = new Date(result.httpResponse
				.getFirstHeader("last-modified").getValue());
		attributes.metadata = getMetadata((AbstractHttpMessage) result.httpResponse);
		return attributes;
	}
	
    public static String getAcl(AbstractHttpMessage response)
    {
    	return Utility.getFirstHeaderValueOrEmpty(response, "x-ms-blob-public-access");
    }
}
