package com.windowsazure.samples.android.storageclient.tests;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;

import junit.framework.Assert;

import com.windowsazure.samples.android.storageclient.BlobProperties;
import com.windowsazure.samples.android.storageclient.CloudBlobContainer;
import com.windowsazure.samples.android.storageclient.CloudBlockBlob;
import com.windowsazure.samples.android.storageclient.StorageInnerException;

public class CloudBlockBlobUsingAccountAndKeyTests extends
		CloudBlockBlobTests<CloudStorageAccountProvider> {
	public void testCopiedBlobIsIdenticalToSourceBlob() throws StorageInnerException, Exception {
		CloudBlobContainer container = this.createQueue("testcopiedblobisidenticaltosourceblob1");
		CloudBlockBlob sourceBlob = container.getBlockBlobReference("sourceBlob");
		String sampleContent = "SampleContent";

		ByteArrayInputStream contentsStream = new ByteArrayInputStream(sampleContent.getBytes());
		sourceBlob.upload(contentsStream, sampleContent.length());

		String key = "sampleKey";
		String value = "sampleValue";
		sourceBlob.getMetadata().put(key, value);
		sourceBlob.uploadMetadata();

		String contentEncoding = "someEncoding";
		String contentLanguage = "english";
		String contentType = "customType";

		BlobProperties blobProperties = sourceBlob.getProperties();
		
		blobProperties.contentType = contentType;
		blobProperties.contentEncoding = contentEncoding;
		blobProperties.contentLanguage = contentLanguage;
		
		sourceBlob.uploadProperties();

		CloudBlockBlob copiedBlob = container.getBlockBlobReference("copiedBlob");
		copiedBlob.copyFromBlob(sourceBlob);

		// Test that has same content
		ByteArrayOutputStream downloadedContentsStream = new ByteArrayOutputStream();
		copiedBlob.download(downloadedContentsStream);
		Assert.assertEquals(sampleContent, downloadedContentsStream.toString());
		
		// Test that has same metadata
		Assert.assertTrue(copiedBlob.getMetadata().containsKey(key));
		Assert.assertEquals(value, copiedBlob.getMetadata().get(key));

		// Test that has same properties
		Assert.assertEquals(contentEncoding, copiedBlob.getProperties().contentEncoding);
		Assert.assertEquals(contentLanguage, copiedBlob.getProperties().contentLanguage);
		Assert.assertEquals(contentType, copiedBlob.getProperties().contentType);
	}
}
