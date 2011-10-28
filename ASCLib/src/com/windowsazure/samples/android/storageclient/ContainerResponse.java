//REVIEW

package com.windowsazure.samples.android.storageclient;

import java.net.*;
import java.util.Date;

import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpRequestBase;

final class ContainerResponse extends BaseResponse
{
    public static BlobContainerAttributes getAttributes(URI originalUri, RequestResult result)
            throws StorageException
        {
            BlobContainerAttributes blobcontainerattributes = new BlobContainerAttributes();
            java.net.URI uri = PathUtility.stripURIQueryAndFragment(originalUri);
            blobcontainerattributes.uri = uri;
            blobcontainerattributes.name = PathUtility.getContainerNameFromUri(uri);
            BlobContainerProperties blobcontainerproperties = blobcontainerattributes.properties;
            blobcontainerproperties.eTag = BaseResponse.getEtag(result.httpResponse);
            blobcontainerproperties.lastModified = new Date(result.httpResponse.getFirstHeader("last-modified").getValue());
            blobcontainerattributes.metadata = getMetadata(result.httpResponse);
            return blobcontainerattributes;
        }

    /*
    ContainerResponse()
    {
    }

    public static String getAcl(HttpBaseRequest request)
    {
        return request.getHeaderField("x-ms-blob-public-access");
    }

*/
	}
