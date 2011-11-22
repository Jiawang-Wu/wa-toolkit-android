package com.windowsazure.samples.android.storageclient;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.util.ArrayList;

import org.apache.http.Header;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.entity.ByteArrayEntity;
import org.apache.http.entity.InputStreamEntity;
import org.apache.http.message.AbstractHttpMessage;

import android.util.Base64;
import android.util.Log;

public final class CloudBlockBlob extends CloudBlob {

	public CloudBlockBlob(CloudBlockBlob blob)
			throws NotImplementedException, StorageException {
		throw new NotImplementedException();
	}

	public CloudBlockBlob(URI blobUri, CloudBlobClient serviceClient)
			throws NotImplementedException, StorageException {
		throw new NotImplementedException();
	}

	public CloudBlockBlob(URI blobUri, CloudBlobClient serviceClient,
			CloudBlobContainer cloudblobcontainer)
			throws StorageException {
		super(blobUri, serviceClient, cloudblobcontainer);
		m_Properties.blobType = BlobType.BLOCK_BLOB;
	}

	public CloudBlockBlob(URI blobUri, String snapshotId, CloudBlobClient serviceClient)
			throws NotImplementedException, StorageException {
		throw new NotImplementedException();
	}

	private InputStream adaptedToSupportMarking(InputStream inputStream) {
		if (!inputStream.markSupported()) {
			return new BufferedInputStream(inputStream);
		} else {
			return inputStream;
		}
	}

	public void commitBlockList(Iterable<BlockEntry> blockEntriesList)
			throws StorageException,
			UnsupportedEncodingException, IOException {
		commitBlockList(blockEntriesList, null);
	}

	public void commitBlockList(final Iterable<BlockEntry> blockEntriesList,
			final String leaseID) throws StorageException, UnsupportedEncodingException, IOException {
		final CloudBlockBlob blob = this;
		StorageOperation<Void> storageOperation = new StorageOperation<Void>() {
			public Void execute() throws Exception {
				HttpPut request = BlobRequest.putBlockList(
						blob.getTransformedAddress(),
						blob.m_Properties, leaseID);
				BlobRequest.addMetadata(request, blob.m_Metadata);

				String formattedBlocksList = BlobRequest
						.formatBlockListAsXML(blockEntriesList);
				request.setEntity(new ByteArrayEntity(formattedBlocksList
						.getBytes()));

				m_ServiceClient.getCredentials().signRequest(request,
						formattedBlocksList.length());
				this.processRequest(request);
				if (result.statusCode != 201) {
					throw new StorageInnerException(
							"Couldn't commit a blocks list");
				}

				BlobAttributes attributes = BlobResponse.getAttributes(
						(AbstractHttpMessage) result.httpResponse,
						blob.getUri(), null);
				blob.m_Properties.eTag = attributes.properties.eTag == null ? blob.m_Properties.eTag
						: attributes.properties.eTag;
				blob.m_Properties.lastModified = attributes.properties.lastModified;
				if (attributes.properties.length != 0L)
					blob.m_Properties.length = attributes.properties.length;
				return null;
			}
		};
        storageOperation.executeTranslatingExceptions();
	}

	public ArrayList<String> downloadBlockList(BlockListingFilter listingFilter)
			throws NotImplementedException, StorageException {
		throw new NotImplementedException();
	}

	private boolean isBase64URLSafeString(String base64UrlString) {
		try 
		{
			Base64.decode(base64UrlString, Base64.URL_SAFE | Base64.NO_WRAP
					| Base64.NO_PADDING);
			return true;
		} catch (IllegalArgumentException exception) {
			return false;
		}
	}

	public BlobOutputStream openOutputStream() throws StorageException, NotImplementedException {
		return new BlobOutputStream(this);
	}

	public BlobOutputStream openOutputStream(String snapshotId)
			throws NotImplementedException, StorageException {
		throw new NotImplementedException();
	}

	private long totalLengthOf(InputStream inputStream) throws IOException {
		inputStream.mark(Integer.MAX_VALUE);
		long skippedLength = 0;
		long length = 0;
		do 
		{
			skippedLength = inputStream.skip(Integer.MAX_VALUE);
			length += skippedLength; 
		} while (skippedLength > 0);
		inputStream.reset();
		return length;
	}

	@Override
	public void upload(InputStream inputstream, long length)
			throws NotImplementedException, StorageException, IOException {
		upload(inputstream, length, null);
	}

	@Override
	public void upload(InputStream inputStream, long length, String leaseID)
			throws NotImplementedException, StorageException, IOException {
		if (length < -1L) {
			throw new IllegalArgumentException(
					"Invalid stream length, specify -1 for unkown length stream, or a positive number of bytes");
		} else if (length < 0L) {
			inputStream = this.adaptedToSupportMarking(inputStream);
			length = this.totalLengthOf(inputStream);
		}

		uploadFullBlob(inputStream, length, leaseID);
	}

	public void uploadBlock(String blockId, InputStream inputStream, long length)
			throws StorageException, IOException {
		if (Utility.isNullOrEmpty(blockId)
				|| !this.isBase64URLSafeString(blockId)) {
			throw new IllegalArgumentException(
					"Invalid blockID, BlockID must be a valid Base64 String.");
		}
		if (length == -1L) {
			inputStream = this.adaptedToSupportMarking(inputStream);
			length = this.totalLengthOf(inputStream);
		}

		if (length < -1L) {
			throw new IllegalArgumentException(
					"Invalid stream length, specify -1 for unkown length stream, or a positive number of bytes");
		} else if (length > 0x400000L) {
			throw new IllegalArgumentException(
					"Invalid stream length, length must be less than or equal to 4 MB in size.");
		}

		uploadBlockInternal(blockId, inputStream, length);
	}

	private void uploadBlockInternal(final String blockId,
			final InputStream inputStream,
			final long length) throws StorageException, IOException {
		final CloudBlockBlob blob = this;
		StorageOperation<Void> storageOperation = new StorageOperation<Void>() {
			public Void execute() throws Exception {
				HttpPut request = BlobRequest.putBlock(
						blob.getTransformedAddress(), blockId);
				m_ServiceClient.getCredentials().signRequest(request, length);
				InputStreamEntity entity = new InputStreamEntity(inputStream,
						length);
				request.setEntity(entity);
				this.processRequest(request);
				if (result.statusCode != 201) {
					Log.d("Upload blob uri", request.getURI().toString());
					Log.d("encoded block id", blockId);
					for (Header header : request.getAllHeaders())
					{
						Log.d("Upload blob header",
								String.format("%s=%s", header.getName(), header.getValue()));
					}
					throw new StorageInnerException(
							"Couldn't upload a blob block");
				}
				return null;
			}
		};
        storageOperation.executeTranslatingExceptions();
	}

	public static String encodedBlockId(String blockId)
	{
		return encodedBlockId(blockId.getBytes());
	}
	public static String encodedBlockId(byte[] blockIdBytes)
	{
		return Base64.encodeToString(blockIdBytes, Base64.URL_SAFE
				| Base64.NO_WRAP);
	}
}
