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

	public CloudBlockBlob(CloudBlockBlob cloudblockblob)
			throws NotImplementedException, StorageException {
		throw new NotImplementedException();
	}

	public CloudBlockBlob(URI uri, CloudBlobClient cloudblobclient)
			throws NotImplementedException, StorageException {
		throw new NotImplementedException();
	}

	public CloudBlockBlob(URI uri, CloudBlobClient cloudblobclient,
			CloudBlobContainer cloudblobcontainer)
			throws NotImplementedException, StorageException {
		super(uri, cloudblobclient, cloudblobcontainer);
		m_Properties.blobType = BlobType.BLOCK_BLOB;
	}

	public CloudBlockBlob(URI uri, String s, CloudBlobClient cloudblobclient)
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

	public void commitBlockList(Iterable<BlockEntry> iterable)
			throws NotImplementedException, StorageException,
			UnsupportedEncodingException, IOException {
		commitBlockList(iterable, null);
	}

	public void commitBlockList(final Iterable<BlockEntry> blockEntries,
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
						.formatBlockListAsXML(blockEntries);
				request.setEntity(new ByteArrayEntity(formattedBlocksList
						.getBytes()));

				cloudblobclient.getCredentials().signRequest(request,
						formattedBlocksList.length());
				result = ExecutionEngine.processRequest(request);
				if (result.statusCode != 201) {
					throw new StorageInnerException(
							"Couldn't commit a blocks list");
				}

				BlobAttributes blobattributes = BlobResponse.getAttributes(
						(AbstractHttpMessage) result.httpResponse,
						cloudblob.getUri(), null);
				cloudblob.m_Properties.eTag = blobattributes.properties.eTag == null ? cloudblob.m_Properties.eTag
						: blobattributes.properties.eTag;
				cloudblob.m_Properties.lastModified = blobattributes.properties.lastModified;
				if (blobattributes.properties.length != 0L)
					cloudblob.m_Properties.length = blobattributes.properties.length;
				return null;
			}

			@Override
			public Object execute(Object obj, Object obj1) throws Exception {
				return execute((CloudBlobClient) obj, (CloudBlob) obj1);
			}

		};
		ExecutionEngine.execute(m_ServiceClient, this, storageoperation);
	}

	public ArrayList downloadBlockList() throws NotImplementedException,
			StorageException {
		throw new NotImplementedException();
	}

	public ArrayList downloadBlockList(BlockListingFilter blocklistingfilter)
			throws NotImplementedException, StorageException {
		throw new NotImplementedException();
	}

	private boolean isBase64URLSafeString(String string) {
		try {
			Base64.decode(string, Base64.URL_SAFE | Base64.NO_WRAP
					| Base64.NO_PADDING);
			return true;
		} catch (IllegalArgumentException exception) {
			return false;
		}
	}

	public BlobOutputStream openOutputStream() throws NotImplementedException,
			StorageException {
		throw new NotImplementedException();
	}

	public BlobOutputStream openOutputStream(String s)
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
	public void upload(InputStream inputstream, long l)
			throws NotImplementedException, StorageException, IOException {
		upload(inputstream, l, null);
	}

	@Override
	public void upload(InputStream inputstream, long length, String leaseID)
			throws NotImplementedException, StorageException, IOException {
		if (length < -1L) {
			throw new IllegalArgumentException(
					"Invalid stream length, specify -1 for unkown length stream, or a positive number of bytes");
		} else if (length < 0L) {
			inputstream = this.adaptedToSupportMarking(inputstream);
			length = this.totalLengthOf(inputstream);
		}

		uploadFullBlob(inputstream, length, leaseID);
	}

	public void uploadBlock(String blockId, String leaseID,
			InputStream inputstream, long length)
			throws NotImplementedException, StorageException, IOException {
		if (Utility.isNullOrEmpty(blockId)
				|| !this.isBase64URLSafeString(blockId)) {
			throw new IllegalArgumentException(
					"Invalid blockID, BlockID must be a valid Base64 String.");
		}
		if (length == -1L) {
			inputstream = this.adaptedToSupportMarking(inputstream);
			length = this.totalLengthOf(inputstream);
		}

		if (length < -1L) {
			throw new IllegalArgumentException(
					"Invalid stream length, specify -1 for unkown length stream, or a positive number of bytes");
		} else if (length > 0x400000L) {
			throw new IllegalArgumentException(
					"Invalid stream length, length must be less than or equal to 4 MB in size.");
		}

		uploadBlockInternal(blockId, leaseID, inputstream, length);
	}

	private void uploadBlockInternal(final String blockId,
			final String leaseID, final InputStream inputstream,
			final long length) throws NotImplementedException,
			StorageException, IOException {
		StorageOperation storageoperation = new StorageOperation() {
			public Void execute(CloudBlobClient cloudblobclient,
					CloudBlob cloudblob) throws Exception {
				HttpPut request = BlobRequest.putBlock(
						cloudblob.getTransformedAddress(), blockId, leaseID);
				cloudblobclient.getCredentials().signRequest(request, length);
				InputStreamEntity entity = new InputStreamEntity(inputstream,
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
		ExecutionEngine.execute(m_ServiceClient, this, storageoperation);
	}
}
