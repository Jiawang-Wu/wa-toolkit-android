package com.windowsazure.samples.android.storageclient;

import java.net.URI;
import java.net.URISyntaxException;

import org.apache.http.client.methods.HttpGet;

final class BlobRequest implements AbstractBlobRequest
{

    BlobRequest()
    {
    }

	@Override
	public HttpGet list(URI endpoint, CloudBlobContainer container, String s,
			boolean useFlatBlobListing) throws URISyntaxException,
			IllegalArgumentException, StorageException, NotImplementedException {
		// TODO Auto-generated method stub
		return null;
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

    public static HttpURLConnection delete(URI uri, int i, String s, DeleteSnapshotsOption deletesnapshotsoption, String s1, BlobRequestOptions blobrequestoptions, OperationContext operationcontext)
        throws IOException, URISyntaxException, IllegalArgumentException, StorageException
    {
        if(s != null && deletesnapshotsoption != DeleteSnapshotsOption.NONE)
            throw new IllegalArgumentException(String.format("The option '%s' must be 'None' to delete a specific snapshot specified by '%s'", new Object[] {
                "deleteSnapshotsOption", "snapshot"
            }));
        UriQueryBuilder uriquerybuilder = new UriQueryBuilder();
        BaseRequest.addSnapshot(uriquerybuilder, s);
        HttpURLConnection httpurlconnection = BaseRequest.delete(uri, i, uriquerybuilder, operationcontext);
        BaseRequest.addLeaseId(httpurlconnection, s1);
        if(blobrequestoptions != null)
            blobrequestoptions.requestAccessCondition.applyConditionToRequest(httpurlconnection);
        switch(deletesnapshotsoption)
        {
        // TO DO TODO
        case INCLUDE_SNAPSHOTS: // '\002'
            httpurlconnection.setRequestProperty("x-ms-delete-snapshots", "include");
            break;

        case DELETE_SNAPSHOTS_ONLY: // '\003'
            httpurlconnection.setRequestProperty("x-ms-delete-snapshots", "only");
            break;
        }
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

    public static HttpURLConnection get(URI uri, int i, String s, String s1, BlobRequestOptions blobrequestoptions, OperationContext operationcontext)
        throws IOException, URISyntaxException, IllegalArgumentException, StorageException
    {
        UriQueryBuilder uriquerybuilder = new UriQueryBuilder();
        BaseRequest.addSnapshot(uriquerybuilder, s);
        HttpURLConnection httpurlconnection = BaseRequest.createURLConnection(uri, i, uriquerybuilder, operationcontext);
        httpurlconnection.setRequestMethod("GET");
        if(blobrequestoptions != null)
            blobrequestoptions.requestAccessCondition.applyConditionToRequest(httpurlconnection);
        if(s1 != null)
            BaseRequest.addLeaseId(httpurlconnection, s1);
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

    public static HttpURLConnection list(URI uri, int i, BlobListingContext bloblistingcontext, BlobRequestOptions blobrequestoptions, OperationContext operationcontext)
        throws URISyntaxException, IOException, IllegalArgumentException, StorageException
    {
        UriQueryBuilder uriquerybuilder = ContainerRequest.getContainerUriQueryBuilder();
        uriquerybuilder.add("comp", "list");
        if(bloblistingcontext != null)
        {
            if(!Utility.isNullOrEmpty(bloblistingcontext.prefix))
                uriquerybuilder.add("prefix", bloblistingcontext.prefix);
            if(!Utility.isNullOrEmpty(bloblistingcontext.delimiter))
                uriquerybuilder.add("delimiter", bloblistingcontext.delimiter);
            if(!Utility.isNullOrEmpty(bloblistingcontext.marker))
                uriquerybuilder.add("marker", bloblistingcontext.marker);
            if(bloblistingcontext.maxResults != null && bloblistingcontext.maxResults.intValue() > 0)
                uriquerybuilder.add("maxresults", bloblistingcontext.maxResults.toString());
            if(bloblistingcontext.listingDetails.size() > 0)
            {
                StringBuilder stringbuilder = new StringBuilder();
                boolean flag = false;
                if(bloblistingcontext.listingDetails.contains(BlobListingDetails.SNAPSHOTS))
                {
                    if(!flag)
                        flag = true;
                    else
                        stringbuilder.append(",");
                    stringbuilder.append("snapshots");
                }
                if(bloblistingcontext.listingDetails.contains(BlobListingDetails.UNCOMMITTED_BLOBS))
                {
                    if(!flag)
                        flag = true;
                    else
                        stringbuilder.append(",");
                    stringbuilder.append("uncommittedblobs");
                }
                if(bloblistingcontext.listingDetails.contains(BlobListingDetails.METADATA))
                {
                    if(!flag)
                        flag = true;
                    else
                        stringbuilder.append(",");
                    stringbuilder.append("metadata");
                }
                uriquerybuilder.add("include", stringbuilder.toString());
            }
        }
        HttpURLConnection httpurlconnection = createURLConnection(uri, i, uriquerybuilder, blobrequestoptions, operationcontext);
        httpurlconnection.setRequestMethod("GET");
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
*/}
