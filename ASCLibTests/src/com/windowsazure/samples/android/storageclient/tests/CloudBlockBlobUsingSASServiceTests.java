package com.windowsazure.samples.android.storageclient.tests;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Arrays;

import com.windowsazure.samples.android.storageclient.CloudBlobContainer;
import com.windowsazure.samples.android.storageclient.CloudBlockBlob;
import com.windowsazure.samples.android.storageclient.NotImplementedException;
import com.windowsazure.samples.android.storageclient.StorageException;
import com.windowsazure.samples.android.storageclient.StorageInnerException;

public abstract class CloudBlockBlobUsingSASServiceTests
	<T extends CloudClientAccountProvider> extends CloudBlobClientBasedTest<T> 
{
	public void testCreateBlobInPrivateContainer() throws StorageInnerException, Exception
	{
		CloudBlobContainer container = this.createContainer("testcreateblobinprivatecontainer");
		String blobName = "someblob";
		CloudBlockBlob blob = container.getBlockBlobReference(blobName);
		ByteArrayInputStream contentsStream = new ByteArrayInputStream("".getBytes()); 
		blob.upload(contentsStream, 0);
		ArrayList<String> blobNames = this.getBlobNames(container.listBlobs());
		this.AssertHaveSameElements(blobNames, Arrays.asList(new String[]{blobName}));
	}
}
