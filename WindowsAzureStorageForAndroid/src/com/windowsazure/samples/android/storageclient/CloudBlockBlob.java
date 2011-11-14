package com.windowsazure.samples.android.storageclient;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.util.ArrayList;

import org.apache.http.client.methods.HttpPut;
import org.apache.http.entity.ByteArrayEntity;
import org.apache.http.entity.InputStreamEntity;
import org.apache.http.message.AbstractHttpMessage;

import android.util.Base64;

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
			throws NotImplementedException, StorageException,
			UnsupportedEncodingException, IOException {
		commitBlockList(blockEntriesList, null);
	}

	public void commitBlockList(final Iterable<BlockEntry> blockEntriesList,
			final String leaseID) throws NotImplementedException,
			StorageException, UnsupportedEncodingException, IOException {
		StorageOperation storageoperation = new StorageOperation() {
			public Void execute(CloudBlobClient cloudblobclient,
					CloudBlob cloudblob) throws Exception {
				HttpPut request = BlobRequest.putBlockList(
						cloudblob.getTransformedAddress(),
						cloudblob.m_Properties, leaseID);
				BlobRequest.addMetadata(request, cloudblob.m_Metadata);

				String formattedBlocksList = BlobRequest
						.formatBlockListAsXML(blockEntriesList);
				request.setEntity(new ByteArrayEntity(formattedBlocksList
						.getBytes()));

				cloudblobclient.getCredentials().signRequest(request,
						formattedBlocksList.length());
				result = ExecutionEngine.processRequest(request);
				if (result.statusCode != 201) {
					throw new StorageInnerException(
							"Couldn't commit a blocks list");
				}

				BlobAttributes attributes = BlobResponse.getAttributes(
						(AbstractHttpMessage) result.httpResponse,
						cloudblob.getUri(), null);
				cloudblob.m_Properties.eTag = attributes.properties.eTag == null ? cloudblob.m_Properties.eTag
						: attributes.properties.eTag;
				cloudblob.m_Properties.lastModified = attributes.properties.lastModified;
				if (attributes.properties.length != 0L)
					cloudblob.m_Properties.length = attributes.properties.length;
				return null;
			}

			@Override
			public Object execute(Object obj, Object obj1) throws Exception {
				return execute((CloudBlobClient) obj, (CloudBlob) obj1);
			}

		};
		ExecutionEngine.execute(m_ServiceClient, this, storageoperation);
	}

	public ArrayList<String> downloadBlockList(BlockListingFilter listingFilter)
			throws NotImplementedException, StorageException {
		throw new NotImplementedException();
	}

	private boolean isBase64URLSafeString(String base64UrlString) {
		try {
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
		long length = inputStream.skip(Integer.MAX_VALUE);
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
		StorageOperation storageOperation = new StorageOperation() {
			public Void execute(CloudBlobClient serviceClient,
					CloudBlob blob) throws Exception {
				HttpPut request = BlobRequest.putBlock(
						blob.getTransformedAddress(), blockId);
				serviceClient.getCredentials().signRequest(request, length);
				InputStreamEntity entity = new InputStreamEntity(inputStream,
						length);
				request.setEntity(entity);
				result = ExecutionEngine.processRequest(request);
				if (result.statusCode != 201) {
					throw new StorageInnerException(
							"Couldn't upload a blob block");
				}
				return null;
			}

			@Override
			public Object execute(Object obj, Object obj1) throws Exception {
				return execute((CloudBlobClient) obj, (CloudBlob) obj1);
			}

		};
		ExecutionEngine.execute(m_ServiceClient, this, storageOperation);
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
