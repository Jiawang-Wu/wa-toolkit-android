package com.windowsazure.samples.android.storageclient;

import java.io.IOException;
import java.io.StringWriter;
import java.net.*;
import java.security.InvalidKeyException;
import java.util.*;

import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.client.methods.HttpRequestBase;

import com.windowsazure.samples.android.storageclient.*;

final class ContainerWASServiceRequest implements AbstractContainerRequest
{
    public void addMetadata(HttpRequestBase request, HashMap hashmap)
    {
        BaseRequest.addMetadata(request, hashmap);
    }

    public HttpPut create(URI uri, boolean createIfNotExists, boolean isPublic)
            throws IOException, URISyntaxException, IllegalArgumentException, StorageException
        {
        	UriQueryBuilder uriquerybuilder = new UriQueryBuilder();
            uriquerybuilder.add("createIfNotExists", "" + createIfNotExists);
            uriquerybuilder.add("isPublic", "" + isPublic);
            return BaseRequest.create(uri, 0, uriquerybuilder);
        }

    public HttpPut create(URI uri, int timeout)
            throws IOException, URISyntaxException, IllegalArgumentException, StorageException
        {
            return create(uri, false, false);
        }

	@Override
	public HttpGet getUri(URI containerOperationsUri, int timeoutInMs) throws IOException, URISyntaxException, StorageException {
		HttpGet request = new HttpGet();
        BaseRequest.setURIAndHeaders(request, containerOperationsUri, new UriQueryBuilder());
        return request;
	}

	@Override
	public boolean isUsingWasServiceDirectly() {
		return false;
	}

	@Override
	public HttpDelete delete(URI containerOperationsUri,
			int timeoutInMs) throws IOException, URISyntaxException,
			IllegalArgumentException, StorageException
	{
		UriQueryBuilder uriquerybuilder = new UriQueryBuilder();
	    return BaseRequest.delete(containerOperationsUri, 0, uriquerybuilder);
	}

    public HttpGet list(URI uri, String prefix, ContainerListingDetails containerlistingdetails) throws IOException, URISyntaxException, StorageException
        {
    		UriQueryBuilder uriquerybuilder = new UriQueryBuilder();
    		URI listContainersUri = PathUtility.appendPathToUri(uri, "containers");
            if(!Utility.isNullOrEmpty(prefix))
            {
            	uriquerybuilder.add("containerPrefix", prefix);
            }
            return BaseRequest.setURIAndHeaders(new HttpGet(), listContainersUri, uriquerybuilder);
        }

	@Override
	public HttpPut setAcl(URI uri,
			BlobContainerPublicAccessType publicAccess)
			throws NotImplementedException, IllegalArgumentException, IOException, URISyntaxException, StorageException {
		return this.create(uri, true, publicAccess != BlobContainerPublicAccessType.OFF);
	}

    /*
    ContainerRequest()
    {
    }

    public static void addMetadata(HttpBaseRequest request, String s, String s1)
    {
        BaseRequest.addMetadata(request, s, s1);
    }

    public static HttpBaseRequest delete(URI uri, int i)
        throws IOException, URISyntaxException, IllegalArgumentException, StorageException
    {
        UriQueryBuilder uriquerybuilder = getContainerUriQueryBuilder();
        return BaseRequest.delete(uri, i, uriquerybuilder);
    }

    public static HttpBaseRequest getAcl(URI uri, int i)
        throws IOException, URISyntaxException, StorageException
    {
        UriQueryBuilder uriquerybuilder = getContainerUriQueryBuilder();
        uriquerybuilder.add("comp", "acl");
        HttpBaseRequest request = setURIAndHeaders(uri, i, uriquerybuilder);
        request.setRequestMethod("GET");
        return request;
    }

    public static HttpBaseRequest getMetadata(URI uri, int i)
        throws IllegalArgumentException, IOException, URISyntaxException, StorageException
    {
        UriQueryBuilder uriquerybuilder = getContainerUriQueryBuilder();
        return BaseRequest.getMetadata(uri, i, uriquerybuilder);
    }

    public static HttpBaseRequest getProperties(URI uri, int i)
        throws IllegalArgumentException, IOException, URISyntaxException, StorageException
    {
        UriQueryBuilder uriquerybuilder = getContainerUriQueryBuilder();
        return BaseRequest.getProperties(uri, i, uriquerybuilder);
    }

    public static HttpBaseRequest list(URI uri, int i, ListingContext listingcontext, ContainerListingDetails containerlistingdetails)
        throws URISyntaxException, IOException, IllegalArgumentException, StorageException
    {
        UriQueryBuilder uriquerybuilder = getContainerUriQueryBuilder();
        uriquerybuilder.add("comp", "list");
        if(listingcontext != null)
        {
            if(!Utility.isNullOrEmpty(listingcontext.prefix))
                uriquerybuilder.add("prefix", listingcontext.prefix);
            if(!Utility.isNullOrEmpty(listingcontext.marker))
                uriquerybuilder.add("marker", listingcontext.marker);
            if(listingcontext.maxResults != null && listingcontext.maxResults.intValue() > 0)
                uriquerybuilder.add("maxresults", listingcontext.maxResults.toString());
        }
        if(containerlistingdetails == ContainerListingDetails.ALL || containerlistingdetails == ContainerListingDetails.METADATA)
            uriquerybuilder.add("include", "metadata");
        HttpBaseRequest request = setURIAndHeaders(uri, i, uriquerybuilder);
        request.setRequestMethod("GET");
        return request;
    }

    public static HttpBaseRequest setAcl(URI uri, int i, BlobContainerPublicAccessType blobcontainerpublicaccesstype)
        throws IOException, URISyntaxException, StorageException
    {
        UriQueryBuilder uriquerybuilder = getContainerUriQueryBuilder();
        uriquerybuilder.add("comp", "acl");
        HttpBaseRequest request = setURIAndHeaders(uri, i, uriquerybuilder);
        request.setRequestMethod("PUT");
        request.setDoOutput(true);
        if(blobcontainerpublicaccesstype != BlobContainerPublicAccessType.OFF)
            request.setRequestProperty("x-ms-blob-public-access", blobcontainerpublicaccesstype.toString().toLowerCase());
        return request;
    }

    public static HttpBaseRequest setMetadata(URI uri, int i)
        throws IllegalArgumentException, IOException, URISyntaxException, StorageException
    {
        UriQueryBuilder uriquerybuilder = getContainerUriQueryBuilder();
        return BaseRequest.setMetadata(uri, i, uriquerybuilder);
    }

    public static void signRequest(HttpBaseRequest request, Credentials credentials, Long long1)
        throws InvalidKeyException, StorageException
    {
        BaseRequest.signRequestForBlobAndQueue(request, credentials, long1);
    }

    public static void signRequestForSharedKeyLite(HttpBaseRequest request, Credentials credentials, Long long1)
        throws InvalidKeyException, StorageException
    {
        BaseRequest.signRequestForBlobAndQueueSharedKeyLite(request, credentials, long1);
    }

    public static void writeSharedAccessIdentifiersToStream(HashMap hashmap, StringWriter stringwriter)
        throws XMLStreamException
    {
        Utility.assertNotNull("sharedAccessPolicies", hashmap);
        Utility.assertNotNull("outWriter", stringwriter);
        XMLOutputFactory xmloutputfactory = XMLOutputFactory.newInstance();
        XMLStreamWriter xmlstreamwriter = xmloutputfactory.createXMLStreamWriter(stringwriter);
        if(hashmap.keySet().size() > 5)
        {
            String s = String.format("Too many %d shared access policy identifiers provided. Server does not support setting more than %d on a single container.", new Object[] {
                Integer.valueOf(hashmap.keySet().size()), Integer.valueOf(5)
            });
            throw new IllegalArgumentException(s);
        }
        xmlstreamwriter.writeStartDocument();
        xmlstreamwriter.writeStartElement("SignedIdentifiers");
        for(Iterator iterator = hashmap.entrySet().iterator(); iterator.hasNext(); xmlstreamwriter.writeEndElement())
        {
            java.util.Map.Entry entry = (java.util.Map.Entry)iterator.next();
            SharedAccessPolicy sharedaccesspolicy = (SharedAccessPolicy)entry.getValue();
            xmlstreamwriter.writeStartElement("SignedIdentifier");
            xmlstreamwriter.writeStartElement("Id");
            xmlstreamwriter.writeCharacters((String)entry.getKey());
            xmlstreamwriter.writeEndElement();
            xmlstreamwriter.writeStartElement("AccessPolicy");
            xmlstreamwriter.writeStartElement("Start");
            xmlstreamwriter.writeCharacters(Utility.getUTCTimeOrEmpty(sharedaccesspolicy.sharedAccessStartTime));
            xmlstreamwriter.writeEndElement();
            xmlstreamwriter.writeStartElement("Expiry");
            xmlstreamwriter.writeCharacters(Utility.getUTCTimeOrEmpty(sharedaccesspolicy.sharedAccessExpiryTime));
            xmlstreamwriter.writeEndElement();
            xmlstreamwriter.writeStartElement("Permission");
            xmlstreamwriter.writeCharacters(SharedAccessPolicy.permissionsToString(sharedaccesspolicy.permissions));
            xmlstreamwriter.writeEndElement();
            xmlstreamwriter.writeEndElement();
        }

        xmlstreamwriter.writeEndElement();
        xmlstreamwriter.writeEndDocument();
    }

    private static HttpBaseRequest setURIAndHeaders(URI uri, int i, UriQueryBuilder uriquerybuilder)
        throws IOException, URISyntaxException, IllegalArgumentException, StorageException
    {
        return BaseRequest.setURIAndHeaders(uri, i, uriquerybuilder);
    }

*/}
