package com.windowsazure.samples.android.storageclient;

import java.io.IOException;
import java.io.StringWriter;
import java.net.*;
import java.security.InvalidKeyException;
import java.util.*;

import com.windowsazure.samples.android.storageclient.*;

final class ContainerWASServiceRequest implements AbstractContainerRequest
{
    public void addMetadata(HttpURLConnection httpurlconnection, HashMap hashmap)
    {
        BaseRequest.addMetadata(httpurlconnection, hashmap);
    }

    public HttpURLConnection create(URI uri, boolean createIfNotExists, boolean isPublic)
            throws IOException, URISyntaxException, IllegalArgumentException, StorageException
        {
        	UriQueryBuilder uriquerybuilder = new UriQueryBuilder();
            uriquerybuilder.add("createIfNotExists", "" + createIfNotExists);
            uriquerybuilder.add("isPublic", "" + isPublic);
            return BaseRequest.create(uri, 0, uriquerybuilder);
        }
    
    public HttpURLConnection create(URI uri, int timeout)
            throws IOException, URISyntaxException, IllegalArgumentException, StorageException
        {
            return create(uri, false, false);
        }

	@Override
	public HttpURLConnection getUri(URI containerOperationsUri, int timeoutInMs) throws IOException, URISyntaxException, StorageException {
        HttpURLConnection httpurlconnection = BaseRequest.createURLConnection(containerOperationsUri, 0, new UriQueryBuilder());
        httpurlconnection.setRequestMethod("GET");
        httpurlconnection.setDoOutput(false);
        return httpurlconnection;
	}

	@Override
	public boolean isUsingWasServiceDirectly() {
		return false;
	}

	@Override
	public HttpURLConnection delete(URI containerOperationsUri,
			int timeoutInMs) throws IOException, URISyntaxException,
			IllegalArgumentException, StorageException
	{
		UriQueryBuilder uriquerybuilder = new UriQueryBuilder();
	    return BaseRequest.delete(containerOperationsUri, 0, uriquerybuilder);
	}

	/*
    ContainerRequest()
    {
    }

    public static void addMetadata(HttpURLConnection httpurlconnection, String s, String s1)
    {
        BaseRequest.addMetadata(httpurlconnection, s, s1);
    }

    public static HttpURLConnection delete(URI uri, int i)
        throws IOException, URISyntaxException, IllegalArgumentException, StorageException
    {
        UriQueryBuilder uriquerybuilder = getContainerUriQueryBuilder();
        return BaseRequest.delete(uri, i, uriquerybuilder);
    }

    public static HttpURLConnection getAcl(URI uri, int i)
        throws IOException, URISyntaxException, StorageException
    {
        UriQueryBuilder uriquerybuilder = getContainerUriQueryBuilder();
        uriquerybuilder.add("comp", "acl");
        HttpURLConnection httpurlconnection = createURLConnection(uri, i, uriquerybuilder);
        httpurlconnection.setRequestMethod("GET");
        return httpurlconnection;
    }

    public static HttpURLConnection getMetadata(URI uri, int i)
        throws IllegalArgumentException, IOException, URISyntaxException, StorageException
    {
        UriQueryBuilder uriquerybuilder = getContainerUriQueryBuilder();
        return BaseRequest.getMetadata(uri, i, uriquerybuilder);
    }

    public static HttpURLConnection getProperties(URI uri, int i)
        throws IllegalArgumentException, IOException, URISyntaxException, StorageException
    {
        UriQueryBuilder uriquerybuilder = getContainerUriQueryBuilder();
        return BaseRequest.getProperties(uri, i, uriquerybuilder);
    }

    public static HttpURLConnection list(URI uri, int i, ListingContext listingcontext, ContainerListingDetails containerlistingdetails)
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
        HttpURLConnection httpurlconnection = createURLConnection(uri, i, uriquerybuilder);
        httpurlconnection.setRequestMethod("GET");
        return httpurlconnection;
    }

    public static HttpURLConnection setAcl(URI uri, int i, BlobContainerPublicAccessType blobcontainerpublicaccesstype)
        throws IOException, URISyntaxException, StorageException
    {
        UriQueryBuilder uriquerybuilder = getContainerUriQueryBuilder();
        uriquerybuilder.add("comp", "acl");
        HttpURLConnection httpurlconnection = createURLConnection(uri, i, uriquerybuilder);
        httpurlconnection.setRequestMethod("PUT");
        httpurlconnection.setDoOutput(true);
        if(blobcontainerpublicaccesstype != BlobContainerPublicAccessType.OFF)
            httpurlconnection.setRequestProperty("x-ms-blob-public-access", blobcontainerpublicaccesstype.toString().toLowerCase());
        return httpurlconnection;
    }

    public static HttpURLConnection setMetadata(URI uri, int i)
        throws IllegalArgumentException, IOException, URISyntaxException, StorageException
    {
        UriQueryBuilder uriquerybuilder = getContainerUriQueryBuilder();
        return BaseRequest.setMetadata(uri, i, uriquerybuilder);
    }

    public static void signRequest(HttpURLConnection httpurlconnection, Credentials credentials, Long long1)
        throws InvalidKeyException, StorageException
    {
        BaseRequest.signRequestForBlobAndQueue(httpurlconnection, credentials, long1);
    }

    public static void signRequestForSharedKeyLite(HttpURLConnection httpurlconnection, Credentials credentials, Long long1)
        throws InvalidKeyException, StorageException
    {
        BaseRequest.signRequestForBlobAndQueueSharedKeyLite(httpurlconnection, credentials, long1);
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

    private static HttpURLConnection createURLConnection(URI uri, int i, UriQueryBuilder uriquerybuilder)
        throws IOException, URISyntaxException, IllegalArgumentException, StorageException
    {
        return BaseRequest.createURLConnection(uri, i, uriquerybuilder);
    }

*/}
