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

	public static HttpDelete delete(URI endpoint) throws IOException,
			URISyntaxException, IllegalArgumentException, StorageException {
		UriQueryBuilder uriquerybuilder = new UriQueryBuilder();
		return BaseRequest.delete(endpoint, uriquerybuilder);
	}

	public static String formatBlockListAsXML(Iterable<BlockEntry> blockEntriesList)
			throws StorageException {
		StringWriter stringwriter = new StringWriter();
		stringwriter.append("<?xml version=\"1.0\" encoding=\"utf-8\"?>\n");
		stringwriter.append("<BlockList>\n");
		for (BlockEntry blockEntry : blockEntriesList) {
			String elementName = blockEntry.searchMode.toValue();
			stringwriter.append(String.format("  <%s>", elementName));
			stringwriter.append(blockEntry.id);
			stringwriter.append(String.format("</%s>\n", elementName));
		}
		stringwriter.append("</BlockList>\n");
		return stringwriter.toString();
	}

	public static HttpGet get(URI endpoint) throws IOException, URISyntaxException,
			IllegalArgumentException, StorageException {
		UriQueryBuilder uriquerybuilder = new UriQueryBuilder();
		return BaseRequest
				.setURIAndHeaders(new HttpGet(), endpoint, uriquerybuilder);
	}

	public static HttpHead getProperties(URI endpoint, String snapshotId,
			String leaseId) throws IllegalArgumentException, StorageException,
			IOException, URISyntaxException {
		UriQueryBuilder uriquerybuilder = new UriQueryBuilder();
		BaseRequest.addSnapshot(uriquerybuilder, snapshotId);
		HttpHead request = BaseRequest.getProperties(endpoint, uriquerybuilder);
		BaseRequest.addLeaseId(request, leaseId);
		return request;
	}

	public static HttpPut put(URI endpoint, BlobProperties blobProperties,
			BlobType blobType, String leaseID, long contentLength) throws IOException,
			URISyntaxException, StorageException {

		if (blobType == BlobType.UNSPECIFIED) {
			throw new IllegalArgumentException(
					"The blob type cannot be undefined.");
		}
		HttpPut request = BaseRequest
				.setURIAndHeaders(new HttpPut(), endpoint, null);
		BaseRequest.addOptionalHeader(request, "Cache-Control",
				blobProperties.cacheControl);
		BaseRequest.addOptionalHeader(request, "Content-Type",
				blobProperties.contentType);
		BaseRequest.addOptionalHeader(request, "Content-MD5",
				blobProperties.contentMD5);
		BaseRequest.addOptionalHeader(request, "Content-Language",
				blobProperties.contentLanguage);
		BaseRequest.addOptionalHeader(request, "Content-Encoding",
				blobProperties.contentEncoding);
		if (blobType == BlobType.PAGE_BLOB) {
			request.addHeader("Content-Length", "0");
			request.addHeader("x-ms-blob-type", "PageBlob");
			request.addHeader("x-ms-blob-content-length",
					String.valueOf(contentLength));
			blobProperties.length = contentLength;
		} else {
			request.addHeader("x-ms-blob-type", "BlockBlob");
		}
		BaseRequest.addLeaseId(request, leaseID);
		return request;
	}

	public static HttpPut putBlock(URI endpoint, String encodedBlockId, String leaseId)
			throws IOException, URISyntaxException, IllegalArgumentException,
			StorageException {
		UriQueryBuilder uriQueryBuilder = new UriQueryBuilder();
		uriQueryBuilder.add("comp", "block");
		uriQueryBuilder.add("blockid", encodedBlockId);
		HttpPut request = BaseRequest.setURIAndHeaders(new HttpPut(), endpoint,
				uriQueryBuilder);
		BaseRequest.addLeaseId(request, leaseId);
		return request;
	}

	public static HttpPut putBlockList(URI endpoint, BlobProperties blobProperties,
			String leaseId) throws IllegalArgumentException, IOException,
			URISyntaxException, StorageException {
		UriQueryBuilder uriQueryBuilder = new UriQueryBuilder();
		uriQueryBuilder.add("comp", "blocklist");
		HttpPut request = BaseRequest.setURIAndHeaders(new HttpPut(), endpoint,
				uriQueryBuilder);
		BaseRequest.addLeaseId(request, leaseId);
		BaseRequest.addOptionalHeader(request, "x-ms-blob-cache-control",
				blobProperties.cacheControl);
		BaseRequest.addOptionalHeader(request, "x-ms-blob-content-encoding",
				blobProperties.contentEncoding);
		BaseRequest.addOptionalHeader(request, "x-ms-blob-content-language",
				blobProperties.contentLanguage);
		BaseRequest.addOptionalHeader(request, "x-ms-blob-content-md5",
				blobProperties.contentMD5);
		BaseRequest.addOptionalHeader(request, "x-ms-blob-content-type",
				blobProperties.contentType);
		return request;
	}

	public static HttpPut setMetadata(URI endpoint, String leaseId)
			throws IllegalArgumentException, IOException, URISyntaxException,
			StorageException {
		HttpPut request = BaseRequest.setMetadata(endpoint, null);
		BaseRequest.addLeaseId(request, leaseId);
		return request;
	}

	BlobRequest() {
	}

	@Override
	public HttpGet list(URI endpoint, CloudBlobContainer container, String prefix,
			boolean useFlatBlobListing) throws URISyntaxException,
			IllegalArgumentException, StorageException,
			NotImplementedException, IOException {

		URI listBlobsUri = PathUtility.appendPathToUri(endpoint,
				container.getName());
		new ContainerRequest();
		UriQueryBuilder uriQueryBuilder = ContainerRequest
				.getContainerUriQueryBuilder();
		uriQueryBuilder.add("comp", "list");

		if (prefix != null) {
			uriQueryBuilder.add("prefix", prefix);
		}
		if (!useFlatBlobListing) {
			uriQueryBuilder.add("delimiter", "/");
		}
		uriQueryBuilder.add("include", "metadata");
		return BaseRequest.setURIAndHeaders(new HttpGet(), listBlobsUri,
				uriQueryBuilder);
	}

}
