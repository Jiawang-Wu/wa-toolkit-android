//REVIEW

package com.windowsazure.samples.android.storageclient;

import java.net.*;
import java.util.Date;

final class ContainerResponse extends BaseResponse
{
    public static BlobContainerAttributes getAttributes(HttpURLConnection httpurlconnection, boolean flag)
            throws StorageException
        {
            BlobContainerAttributes blobcontainerattributes = new BlobContainerAttributes();
            java.net.URI uri;
            try
            {
                uri = PathUtility.stripURIQueryAndFragment(httpurlconnection.getURL().toURI());
            }
            catch(URISyntaxException urisyntaxexception)
            {
                StorageException storageexception = Utility.generateNewUnexpectedStorageException(urisyntaxexception);
                throw storageexception;
            }
            blobcontainerattributes.uri = uri;
            blobcontainerattributes.name = PathUtility.getContainerNameFromUri(uri, flag);
            BlobContainerProperties blobcontainerproperties = blobcontainerattributes.properties;
            blobcontainerproperties.eTag = BaseResponse.getEtag(httpurlconnection);
            blobcontainerproperties.lastModified = new Date(httpurlconnection.getLastModified());
            blobcontainerattributes.metadata = getMetadata(httpurlconnection);
            return blobcontainerattributes;
        }

    /*
    ContainerResponse()
    {
    }

    public static String getAcl(HttpURLConnection httpurlconnection)
    {
        return httpurlconnection.getHeaderField("x-ms-blob-public-access");
    }

*/
	}
