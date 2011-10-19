package com.windowsazure.samples.android.storageclient.tests;

import com.windowsazure.samples.android.storageclient.CloudBlobClient;
import com.windowsazure.samples.android.storageclient.CloudBlobContainer;

public class CloudBlobDirectoryUsingWAZServiceTests extends TestCase {
	public void testCreateContainer()
			throws Exception {
		CloudBlobClient cloudBlobClient = WAZServiceTestingAccount.getAccount()
				.createCloudBlobClient();
		String containerName = "myTestContainer";
		CloudBlobContainer container = new CloudBlobContainer(containerName, cloudBlobClient);
		container.create();
	}
}
