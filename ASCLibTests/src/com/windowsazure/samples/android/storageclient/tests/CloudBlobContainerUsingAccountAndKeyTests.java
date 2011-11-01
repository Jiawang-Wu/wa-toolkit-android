package com.windowsazure.samples.android.storageclient.tests;

import com.windowsazure.samples.android.storageclient.CloudBlobContainer;

public class CloudBlobContainerUsingAccountAndKeyTests
	extends CloudBlobContainerTests<CloudStorageAccountProvider>
{
	public void testDeleteContainerTwiceShouldNotThrowException() throws Exception {
		final CloudBlobContainer container = this.createContainer("testdeletecontainertwicethrowsexception");
		container.delete();
		
		// Windows Azure works this way. Two deletes inside the 30 seconds margin don't throw an exception.
		container.delete();
	}
}
