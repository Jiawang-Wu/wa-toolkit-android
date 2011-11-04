package com.windowsazure.samples.android.storageclient;

import java.io.IOException;
import java.io.StringWriter;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.HashMap;

import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpHead;
import org.apache.http.client.methods.HttpPut;

final class BlobRequest implements AbstractBlobRequest {

	public static void addMetadata(HttpPut request, HashMap metadata) {
		BaseRequest.addMetadata(request, metadata);
	}

	public static HttpDelete delete(URI uri) throws IOException,
			URISyntaxException, IllegalArgumentException, StorageException {
		UriQueryBuilder uriquerybuilder = new UriQueryBuilder();
		return BaseRequest.delete(uri, uriquerybuilder);
	}

	public static String formatBlockListAsXML(Iterable<BlockEntry> blockList)
			throws StorageException {
		StringWriter stringwriter = new StringWriter();
		stringwriter.append("<?xml version=\"1.0\" encoding=\"utf-8\"?>\n");
		stringwriter.append("<BlockList>\n");
		for (BlockEntry blockEntry : blockList) {
			String elementName = blockEntry.searchMode.toValue();
			stringwriter.append(String.format("  <%s>", elementName));
			stringwriter.append(blockEntry.id);
			stringwriter.append(String.format("</%s>\n", elementName));
		}
		stringwriter.append("</BlockList>\n");
		return stringwriter.toString();
	}

	public static HttpGet get(URI uri) throws IOException, URISyntaxException,
			IllegalArgumentException, StorageException {
		UriQueryBuilder uriquerybuilder = new UriQueryBuilder();
		return BaseRequest
				.setURIAndHeaders(new HttpGet(), uri, uriquerybuilder);
	}

	public static HttpHead getProperties(URI uri, String snapshotId,
			String leaseId) throws IllegalArgumentException, StorageException,
			IOException, URISyntaxException {
		UriQueryBuilder uriquerybuilder = new UriQueryBuilder();
		BaseRequest.addSnapshot(uriquerybuilder, snapshotId);
		HttpHead request = BaseRequest.getProperties(uri, uriquerybuilder);
		BaseRequest.addLeaseId(request, leaseId);
		return request;
	}

	public static HttpPut put(URI uri, BlobProperties blobproperties,
			BlobType blobType, String leaseID, long length) throws IOException,
			URISyntaxException, StorageException {

		if (blobType == BlobType.UNSPECIFIED) {
			throw new IllegalArgumentException(
					"The blob type cannot be undefined.");
		}
		HttpPut request = BaseRequest
				.setURIAndHeaders(new HttpPut(), uri, null);
		BaseRequest.addOptionalHeader(request, "Cache-Control",
				blobproperties.cacheControl);
		BaseRequest.addOptionalHeader(request, "Content-Type",
				blobproperties.contentType);
		BaseRequest.addOptionalHeader(request, "Content-MD5",
				blobproperties.contentMD5);
		BaseRequest.addOptionalHeader(request, "Content-Language",
				blobproperties.contentLanguage);
		BaseRequest.addOptionalHeader(request, "Content-Encoding",
				blobproperties.contentEncoding);
		if (blobType == BlobType.PAGE_BLOB) {
			request.addHeader("Content-Length", "0");
			request.addHeader("x-ms-blob-type", "PageBlob");
			request.addHeader("x-ms-blob-content-length",
					String.valueOf(length));
			blobproperties.length = length;
		} else {
			request.addHeader("x-ms-blob-type", "BlockBlob");
		}
		BaseRequest.addLeaseId(request, leaseID);
		return request;
	}

	public static HttpPut putBlock(URI uri, String blockId, String leaseId)
			throws IOException, URISyntaxException, IllegalArgumentException,
			StorageException {
		UriQueryBuilder uriquerybuilder = new UriQueryBuilder();
		uriquerybuilder.add("comp", "block");
		uriquerybuilder.add("blockid", blockId);
		HttpPut request = BaseRequest.setURIAndHeaders(new HttpPut(), uri,
				uriquerybuilder);
		BaseRequest.addLeaseId(request, leaseId);
		return request;
	}

	public static HttpPut putBlockList(URI uri, BlobProperties blobproperties,
			String s) throws IllegalArgumentException, IOException,
			URISyntaxException, StorageException {
		UriQueryBuilder uriquerybuilder = new UriQueryBuilder();
		uriquerybuilder.add("comp", "blocklist");
		HttpPut request = BaseRequest.setURIAndHeaders(new HttpPut(), uri,
				uriquerybuilder);
		BaseRequest.addLeaseId(request, s);
		BaseRequest.addOptionalHeader(request, "x-ms-blob-cache-control",
				blobproperties.cacheControl);
		BaseRequest.addOptionalHeader(request, "x-ms-blob-content-encoding",
				blobproperties.contentEncoding);
		BaseRequest.addOptionalHeader(request, "x-ms-blob-content-language",
				blobproperties.contentLanguage);
		BaseRequest.addOptionalHeader(request, "x-ms-blob-content-md5",
				blobproperties.contentMD5);
		BaseRequest.addOptionalHeader(request, "x-ms-blob-content-type",
				blobproperties.contentType);
		return request;
	}

	public static HttpPut setMetadata(URI uri, String s)
			throws IllegalArgumentException, IOException, URISyntaxException,
			StorageException {
		HttpPut request = BaseRequest.setMetadata(uri, null);
		BaseRequest.addLeaseId(request, s);
		return request;
	}

	BlobRequest() {
	}

	@Override
	public HttpGet list(URI endpoint, CloudBlobContainer container, String s,
			boolean useFlatBlobListing) throws URISyntaxException,
			IllegalArgumentException, StorageException,
			NotImplementedException, IOException {

		URI listBlobsUri = PathUtility.appendPathToUri(endpoint,
				container.getName());
		new ContainerRequest();
		UriQueryBuilder uriquerybuilder = ContainerRequest
				.getContainerUriQueryBuilder();
		uriquerybuilder.add("comp", "list");

		if (s != null) {
			uriquerybuilder.add("prefix", s);
		}
		if (!useFlatBlobListing) {
			uriquerybuilder.add("delimiter", "/");
		}
		uriquerybuilder.add("include", "metadata");
		return BaseRequest.setURIAndHeaders(new HttpGet(), listBlobsUri,
				uriquerybuilder);
	}

}
