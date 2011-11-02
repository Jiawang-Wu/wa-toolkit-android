package com.windowsazure.samples.android.storageclient;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.HashMap;

import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPut;

final class BlobRequest implements AbstractBlobRequest
{

    BlobRequest()
    {
    }

	@Override
	public HttpGet list(URI endpoint, CloudBlobContainer container, String s,
			boolean useFlatBlobListing) throws URISyntaxException,
			IllegalArgumentException, StorageException, NotImplementedException, IOException {

		URI listBlobsUri = PathUtility.appendPathToUri(endpoint, container.getName());
		UriQueryBuilder uriquerybuilder = new ContainerRequest().getContainerUriQueryBuilder();
        uriquerybuilder.add("comp", "list");

        if(s != null)
        {
        	uriquerybuilder.add("prefix", s);
        }
        if (!useFlatBlobListing)
        {
        	uriquerybuilder.add("delimiter", "/");
        }
        uriquerybuilder.add("include", "metadata");
        return BaseRequest.setURIAndHeaders(new HttpGet(), listBlobsUri, uriquerybuilder);
	}
	
	public static void addMetadata(HttpPut request, HashMap metadata) {
		BaseRequest.addMetadata(request, metadata);
	}

	public static HttpPut put(URI uri, int timeoutInMs,
			BlobProperties blobproperties, BlobType blobType, String leaseID,
			long l) throws IOException, URISyntaxException, StorageException {
		
        if(blobType == BlobType.UNSPECIFIED)
        {
            throw new IllegalArgumentException("The blob type cannot be undefined.");
        }
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
    public static HttpDelete delete(URI uri)
            throws IOException, URISyntaxException, IllegalArgumentException, StorageException
        {
            UriQueryBuilder uriquerybuilder = new UriQueryBuilder();
            return BaseRequest.delete(uri, uriquerybuilder);
        }
    public static HttpGet get(URI uri)
            throws IOException, URISyntaxException, IllegalArgumentException, StorageException
        {
            UriQueryBuilder uriquerybuilder = new UriQueryBuilder();
            return BaseRequest.setURIAndHeaders(new HttpGet(), uri, uriquerybuilder);
        }
}
    
/*    public static void addMetadata(HttpURLConnection httpurlconnection, HashMap hashmap, OperationContext operationcontext)
    {
        BaseRequest.addMetadata(httpurlconnection, hashmap, operationcontext);
    }

    public static void addMetadata(HttpURLConnection httpurlconnection, String s, String s1, OperationContext operationcontext)
    {
        BaseRequest.addMetadata(httpurlconnection, s, s1, operationcontext);
    }

    public static HttpURLConnection copyFrom(URI uri, int i, String s, String s1, AccessConditionHeaderType accessconditionheadertype, String s2, String s3, BlobRequestOptions blobrequestoptions, 
            OperationContext operationcontext)
        throws StorageException, IllegalArgumentException, IOException, URISyntaxException
    {
        if(s1 != null)
        {
            s = s.concat("?snapshot=");
            s = s.concat(s1);
        }
        HttpURLConnection httpurlconnection = BaseRequest.createURLConnection(uri, i, null, operationcontext);
        if(blobrequestoptions != null)
            blobrequestoptions.requestAccessCondition.applyConditionToRequest(httpurlconnection);
        httpurlconnection.setFixedLengthStreamingMode(0);
        httpurlconnection.setDoOutput(true);
        httpurlconnection.setRequestMethod("PUT");
        httpurlconnection.setRequestProperty("x-ms-copy-source", s);

        switch(accessconditionheadertype)
        {
        case IF_MATCH: // '\001'
            httpurlconnection.setRequestProperty("x-ms-source-if-match", s2);
            break;

        case IF_MODIFIED_SINCE: // '\002'
            httpurlconnection.setRequestProperty("x-ms-source-if-modified-since", s2);
            break;

        case IF_NONE_MATCH: // '\003'
            httpurlconnection.setRequestProperty("x-ms-source-if-none-match", s2);
            break;

        case IF_UNMODIFIED_SINCE: // '\004'
            httpurlconnection.setRequestProperty("x-ms-source-if-unmodified-since", s2);
            break;
        }
        BaseRequest.addLeaseId(httpurlconnection, s3);
        return httpurlconnection;
    }

    public static HttpURLConnection get(URI uri, int i, String s, long l, long l1, String s1, 
            BlobRequestOptions blobrequestoptions, OperationContext operationcontext)
        throws IOException, URISyntaxException, IllegalArgumentException, StorageException
    {
        HttpURLConnection httpurlconnection = get(uri, i, s, s1, blobrequestoptions, operationcontext);
        String s2 = String.format(Utility.LOCALE_US, "bytes=%d-%d", new Object[] {
            Long.valueOf(l), Long.valueOf((l + l1) - 1L)
        });
        httpurlconnection.setRequestProperty("x-ms-range", s2);
        return httpurlconnection;
    }

    public static HttpURLConnection getBlockList(URI uri, int i, String s, BlockListingFilter blocklistingfilter, String s1, BlobRequestOptions blobrequestoptions, OperationContext operationcontext)
        throws IllegalArgumentException, StorageException, IOException, URISyntaxException
    {
        UriQueryBuilder uriquerybuilder = new UriQueryBuilder();
        uriquerybuilder.add("comp", "blocklist");
        uriquerybuilder.add("blocklisttype", blocklistingfilter.toString());
        BaseRequest.addSnapshot(uriquerybuilder, s);
        HttpURLConnection httpurlconnection = BaseRequest.createURLConnection(uri, i, uriquerybuilder, operationcontext);
        httpurlconnection.setRequestMethod("GET");
        if(blobrequestoptions != null)
            blobrequestoptions.requestAccessCondition.applyConditionToRequest(httpurlconnection);
        BaseRequest.addLeaseId(httpurlconnection, s1);
        return httpurlconnection;
    }

    public static HttpURLConnection getPageRanges(URI uri, int i, String s, String s1, BlobRequestOptions blobrequestoptions, OperationContext operationcontext)
        throws IllegalArgumentException, StorageException, IOException, URISyntaxException
    {
        UriQueryBuilder uriquerybuilder = new UriQueryBuilder();
        uriquerybuilder.add("comp", "pagelist");
        BaseRequest.addSnapshot(uriquerybuilder, s);
        HttpURLConnection httpurlconnection = createURLConnection(uri, i, uriquerybuilder, blobrequestoptions, operationcontext);
        httpurlconnection.setRequestMethod("GET");
        BaseRequest.addOptionalHeader(httpurlconnection, "snapshot", s);
        BaseRequest.addLeaseId(httpurlconnection, s1);
        return httpurlconnection;
    }

    public static HttpURLConnection getProperties(URI uri, int i, String s, String s1, BlobRequestOptions blobrequestoptions, OperationContext operationcontext)
        throws IllegalArgumentException, StorageException, IOException, URISyntaxException
    {
        UriQueryBuilder uriquerybuilder = new UriQueryBuilder();
        BaseRequest.addSnapshot(uriquerybuilder, s);
        HttpURLConnection httpurlconnection = BaseRequest.getProperties(uri, i, uriquerybuilder, operationcontext);
        BaseRequest.addLeaseId(httpurlconnection, s1);
        if(blobrequestoptions != null)
            blobrequestoptions.requestAccessCondition.applyConditionToRequest(httpurlconnection);
        return httpurlconnection;
    }

    public static HttpURLConnection lease(URI uri, int i, LeaseAction leaseaction, String s, BlobRequestOptions blobrequestoptions, OperationContext operationcontext)
        throws IOException, URISyntaxException, IllegalArgumentException, StorageException
    {
        UriQueryBuilder uriquerybuilder = new UriQueryBuilder();
        uriquerybuilder.add("comp", "lease");
        HttpURLConnection httpurlconnection = createURLConnection(uri, i, uriquerybuilder, blobrequestoptions, operationcontext);
        httpurlconnection.setDoOutput(true);
        httpurlconnection.setRequestMethod("PUT");
        httpurlconnection.setFixedLengthStreamingMode(0);
        BaseRequest.addLeaseId(httpurlconnection, s);
        httpurlconnection.setRequestProperty("x-ms-lease-action", leaseaction.toString());
        return httpurlconnection;
    }

    public static HttpURLConnection put(URI uri, int i, BlobProperties blobproperties, BlobType blobtype, String s, long l, BlobRequestOptions blobrequestoptions, 
            OperationContext operationcontext)
        throws IOException, URISyntaxException, IllegalArgumentException, StorageException
    {
        if(blobtype == BlobType.UNSPECIFIED)
            throw new IllegalArgumentException("The blob type cannot be undefined.");
        HttpURLConnection httpurlconnection = createURLConnection(uri, i, null, blobrequestoptions, operationcontext);
        httpurlconnection.setDoOutput(true);
        httpurlconnection.setRequestMethod("PUT");
        BaseRequest.addOptionalHeader(httpurlconnection, "Cache-Control", blobproperties.cacheControl);
        BaseRequest.addOptionalHeader(httpurlconnection, "Content-Type", blobproperties.contentType);
        BaseRequest.addOptionalHeader(httpurlconnection, "Content-MD5", blobproperties.contentMD5);
        BaseRequest.addOptionalHeader(httpurlconnection, "Content-Language", blobproperties.contentLanguage);
        BaseRequest.addOptionalHeader(httpurlconnection, "Content-Encoding", blobproperties.contentEncoding);
        if(blobtype == BlobType.PAGE_BLOB)
        {
            httpurlconnection.setFixedLengthStreamingMode(0);
            httpurlconnection.setRequestProperty("Content-Length", "0");
            httpurlconnection.setRequestProperty("x-ms-blob-type", "PageBlob");
            httpurlconnection.setRequestProperty("x-ms-blob-content-length", String.valueOf(l));
            blobproperties.length = l;
        } else
        {
            httpurlconnection.setRequestProperty("x-ms-blob-type", "BlockBlob");
        }
        BaseRequest.addLeaseId(httpurlconnection, s);
        return httpurlconnection;
    }

    public static HttpURLConnection putBlock(URI uri, int i, String s, String s1, BlobRequestOptions blobrequestoptions, OperationContext operationcontext)
        throws IOException, URISyntaxException, IllegalArgumentException, StorageException
    {
        UriQueryBuilder uriquerybuilder = new UriQueryBuilder();
        uriquerybuilder.add("comp", "block");
        uriquerybuilder.add("blockid", s);
        HttpURLConnection httpurlconnection = createURLConnection(uri, i, uriquerybuilder, blobrequestoptions, operationcontext);
        httpurlconnection.setDoOutput(true);
        httpurlconnection.setRequestMethod("PUT");
        BaseRequest.addLeaseId(httpurlconnection, s1);
        return httpurlconnection;
    }

    public static HttpURLConnection putBlockList(URI uri, int i, BlobProperties blobproperties, String s, BlobRequestOptions blobrequestoptions, OperationContext operationcontext)
        throws IllegalArgumentException, IOException, URISyntaxException, StorageException
    {
        UriQueryBuilder uriquerybuilder = new UriQueryBuilder();
        uriquerybuilder.add("comp", "blocklist");
        HttpURLConnection httpurlconnection = createURLConnection(uri, i, uriquerybuilder, blobrequestoptions, operationcontext);
        httpurlconnection.setDoOutput(true);
        httpurlconnection.setRequestMethod("PUT");
        BaseRequest.addLeaseId(httpurlconnection, s);
        BaseRequest.addOptionalHeader(httpurlconnection, "x-ms-blob-cache-control", blobproperties.cacheControl);
        BaseRequest.addOptionalHeader(httpurlconnection, "x-ms-blob-content-encoding", blobproperties.contentEncoding);
        BaseRequest.addOptionalHeader(httpurlconnection, "x-ms-blob-content-language", blobproperties.contentLanguage);
        BaseRequest.addOptionalHeader(httpurlconnection, "x-ms-blob-content-md5", blobproperties.contentMD5);
        BaseRequest.addOptionalHeader(httpurlconnection, "x-ms-blob-content-type", blobproperties.contentType);
        return httpurlconnection;
    }

    public static HttpURLConnection putPage(URI uri, int i, PageProperties pageproperties, String s, BlobRequestOptions blobrequestoptions, OperationContext operationcontext)
        throws IOException, URISyntaxException, IllegalArgumentException, StorageException
    {
        UriQueryBuilder uriquerybuilder = new UriQueryBuilder();
        uriquerybuilder.add("comp", "page");
        HttpURLConnection httpurlconnection = createURLConnection(uri, i, uriquerybuilder, blobrequestoptions, operationcontext);
        httpurlconnection.setDoOutput(true);
        httpurlconnection.setRequestMethod("PUT");
        if(pageproperties.pageOperation == PageOperationType.CLEAR)
            httpurlconnection.setFixedLengthStreamingMode(0);
        httpurlconnection.setRequestProperty("x-ms-page-write", pageproperties.pageOperation.toString());
        httpurlconnection.setRequestProperty("x-ms-range", pageproperties.range.toString());
        BaseRequest.addLeaseId(httpurlconnection, s);
        return httpurlconnection;
    }

    public static HttpURLConnection setMetadata(URI uri, int i, String s, BlobRequestOptions blobrequestoptions, OperationContext operationcontext)
        throws IllegalArgumentException, IOException, URISyntaxException, StorageException
    {
        HttpURLConnection httpurlconnection = BaseRequest.setMetadata(uri, i, null, operationcontext);
        BaseRequest.addLeaseId(httpurlconnection, s);
        if(blobrequestoptions != null)
            blobrequestoptions.requestAccessCondition.applyConditionToRequest(httpurlconnection);
        return httpurlconnection;
    }

    public static HttpURLConnection setProperties(URI uri, int i, BlobProperties blobproperties, String s, Long long1, BlobRequestOptions blobrequestoptions, OperationContext operationcontext)
        throws IllegalArgumentException, IOException, URISyntaxException, StorageException
    {
        UriQueryBuilder uriquerybuilder = new UriQueryBuilder();
        uriquerybuilder.add("comp", "properties");
        HttpURLConnection httpurlconnection = createURLConnection(uri, i, uriquerybuilder, blobrequestoptions, operationcontext);
        httpurlconnection.setFixedLengthStreamingMode(0);
        httpurlconnection.setDoOutput(true);
        httpurlconnection.setRequestMethod("PUT");
        BaseRequest.addLeaseId(httpurlconnection, s);
        if(long1 != null)
        {
            httpurlconnection.setRequestProperty("x-ms-blob-content-length", long1.toString());
            blobproperties.length = long1.longValue();
        }
        BaseRequest.addOptionalHeader(httpurlconnection, "x-ms-blob-cache-control", blobproperties.cacheControl);
        BaseRequest.addOptionalHeader(httpurlconnection, "x-ms-blob-content-encoding", blobproperties.contentEncoding);
        BaseRequest.addOptionalHeader(httpurlconnection, "x-ms-blob-content-language", blobproperties.contentLanguage);
        BaseRequest.addOptionalHeader(httpurlconnection, "x-ms-blob-content-md5", blobproperties.contentMD5);
        BaseRequest.addOptionalHeader(httpurlconnection, "x-ms-blob-content-type", blobproperties.contentType);
        return httpurlconnection;
    }

    public static HttpURLConnection snapshot(URI uri, int i, String s, BlobRequestOptions blobrequestoptions, OperationContext operationcontext)
        throws IOException, URISyntaxException, IllegalArgumentException, StorageException
    {
        UriQueryBuilder uriquerybuilder = new UriQueryBuilder();
        uriquerybuilder.add("comp", "snapshot");
        HttpURLConnection httpurlconnection = createURLConnection(uri, i, uriquerybuilder, blobrequestoptions, operationcontext);
        httpurlconnection.setFixedLengthStreamingMode(0);
        httpurlconnection.setDoOutput(true);
        httpurlconnection.setRequestMethod("PUT");
        BaseRequest.addLeaseId(httpurlconnection, s);
        return httpurlconnection;
    }

    public static byte[] writeBlockListToStream(Iterable iterable, OperationContext operationcontext)
        throws XMLStreamException, StorageException
    {
        StringWriter stringwriter = new StringWriter();
        XMLOutputFactory xmloutputfactory = XMLOutputFactory.newInstance();
        XMLStreamWriter xmlstreamwriter = xmloutputfactory.createXMLStreamWriter(stringwriter);
        xmlstreamwriter.writeStartDocument();
        xmlstreamwriter.writeStartElement("BlockList");
        for(Iterator iterator = iterable.iterator(); iterator.hasNext(); xmlstreamwriter.writeEndElement())
        {
            BlockEntry blockentry = (BlockEntry)iterator.next();
            if(blockentry.searchMode == BlockSearchMode.COMMITTED)
                xmlstreamwriter.writeStartElement("Committed");
            else
            if(blockentry.searchMode == BlockSearchMode.UNCOMMITTED)
                xmlstreamwriter.writeStartElement("Uncommitted");
            else
            if(blockentry.searchMode == BlockSearchMode.LATEST)
                xmlstreamwriter.writeStartElement("Latest");
            xmlstreamwriter.writeCharacters(blockentry.id);
        }

        xmlstreamwriter.writeEndElement();
        xmlstreamwriter.writeEndDocument();
        try
        {
            return stringwriter.toString().getBytes("UTF8");
        }
        catch(UnsupportedEncodingException unsupportedencodingexception)
        {
            throw Utility.generateNewUnexpectedStorageException(unsupportedencodingexception);
        }
    }

    private static HttpURLConnection createURLConnection(URI uri, int i, UriQueryBuilder uriquerybuilder, BlobRequestOptions blobrequestoptions, OperationContext operationcontext)
        throws IOException, URISyntaxException, IllegalArgumentException, StorageException
    {
        HttpURLConnection httpurlconnection = BaseRequest.createURLConnection(uri, i, uriquerybuilder, operationcontext);
        if(blobrequestoptions != null)
            blobrequestoptions.requestAccessCondition.applyConditionToRequest(httpurlconnection);
        return httpurlconnection;
    }
*/    
