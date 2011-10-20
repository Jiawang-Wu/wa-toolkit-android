package com.windowsazure.samples.android.storageclient.tests;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URISyntaxException;

import junit.framework.Assert;

import com.windowsazure.samples.android.storageclient.CloudBlobClient;
import com.windowsazure.samples.android.storageclient.CloudBlobContainer;
import com.windowsazure.samples.android.storageclient.NotImplementedException;
import com.windowsazure.samples.android.storageclient.StorageException;
import com.windowsazure.samples.android.storageclient.StorageInnerException;

public class CloudBlobContainerUsingSASServiceTests extends TestCaseWithManagedCleanUp {
	
	public void testCreateContainerWithInvalidNameThrowsException()
			throws Exception {
		this.assertThrows(new ExpectedExceptionRunnable() {
			@Override
			public void run() throws Exception {
				thisTest.createContainer("my_test_container");
			}
		}, StorageException.class);
	}

	public void testCreateContainerWithEmptydNameThrowsException()
			throws Exception {
		this.assertThrows(new ExpectedExceptionRunnable() {
			@Override
			public void run() throws Exception {
				new CloudBlobContainer("", cloudBlobClient);
			}
		}, IllegalArgumentException.class);
	}

	public void testCreateContainerWithNullNameThrowsException()
			throws Exception {
		this.assertThrows(new ExpectedExceptionRunnable() {
			@Override
			public void run() throws Exception {
				new CloudBlobContainer((String) null, cloudBlobClient);
			}
		}, IllegalArgumentException.class);
	}

	public void testCreateContainerTwiceThrowsException()
			throws Exception {
		final CloudBlobContainer container = this.createContainer("testcreatecontainertwicethrowsexception");
		this.assertThrows(new ExpectedExceptionRunnable() {
			@Override
			public void run() throws Exception {
				container.create();
			}
		}, StorageException.class);
	}

	public void testCreateContainerCreatedByOtherAccountThrowsException()
			throws Exception {
		String containerName = "testcreatecontainercreatedbyotheraccountthrowsexception";
		final CloudBlobContainer container = this.createContainer(containerName);
		final CloudBlobContainer sameContainer = new CloudBlobContainer(containerName, otherCloudBlobClient);
		this.assertThrows(new ExpectedExceptionRunnable() {
			@Override
			public void run() throws Exception {
				sameContainer.create();
			}
		}, StorageException.class);
	}

	public void testDeleteContainerWithoutPermissionsThrowsException()
			throws Exception {
		String containerName = "testdeletecontainerwithoutpermissionsthrowsexception";
		final CloudBlobContainer container = this.createContainer(containerName);
		final CloudBlobContainer sameContainer = new CloudBlobContainer(containerName, otherCloudBlobClient);
		this.assertThrows(new ExpectedExceptionRunnable() {
			@Override
			public void run() throws Exception {
				sameContainer.delete();
			}
		}, StorageException.class);
		container.delete();
	}

	public void testCreateExistentContainerThrowsException()
			throws Exception {
		this.createContainer("testcreateexistentcontainerthrowsexception");
		this.assertThrows(new ExpectedExceptionRunnable() {
			@Override
			public void run() throws Exception {
				thisTest.createContainer("testcreateexistentcontainerthrowsexception");
			}
		}, StorageException.class);
	}

	public void testContainerUriIsWABlobUri() throws Exception {
		CloudBlobContainer container = this.createContainer("testcontaineruriiswabloburi");
		URI uri = container.getUri();
		Assert.assertTrue(uri.getAuthority().endsWith(".blob.core.windows.net"));
	}

	public void testDeleteNonexistantContainerThrowsException() throws Exception {
		final CloudBlobContainer container = new CloudBlobContainer("non-existant-container", cloudBlobClient);
		this.assertThrows(new ExpectedExceptionRunnable() {
			@Override
			public void run() throws Exception {
				container.delete();
			}
		}, StorageException.class);
	}

	public void testDeleteContainerWithInvalidNameThrowsException() throws Exception {
		final CloudBlobContainer container = new CloudBlobContainer("invalid_container", cloudBlobClient);
		this.assertThrows(new ExpectedExceptionRunnable() {
			@Override
			public void run() throws Exception {
				container.delete();
			}
		}, StorageException.class);
	}

	public void testDeleteContainerTwiceThrowsException() throws Exception {
		final CloudBlobContainer container = this.createContainer("testdeletecontainertwicethrowsexception");
		container.delete();
		this.assertThrows(new ExpectedExceptionRunnable() {
			@Override
			public void run() throws Exception {
				container.delete();
			}
		}, StorageException.class);
	}

	private CloudBlobClient cloudBlobClient;

	private CloudBlobClient otherCloudBlobClient;
	
	private CloudBlobContainerUsingSASServiceTests thisTest;
	
	public void setUp()
	{
		thisTest = this;
		try {
			cloudBlobClient = WAZServiceTestingAccount.getCloudBlobClient();
			otherCloudBlobClient = WAZServiceTestingAccount.getCloudBlobClientWithDifferentAccount();
		} catch (Exception e) {
			e.printStackTrace();
			Assert.fail();
		}
	}

	private CloudBlobContainer createContainer(String containerName) throws StorageException, NotImplementedException, URISyntaxException, UnsupportedEncodingException, IOException {
		final CloudBlobContainer container = new CloudBlobContainer(containerName, cloudBlobClient);
		container.create();
		this.addCleanUp(new Cleaner()
		{
			public void run() throws NotImplementedException, StorageException, UnsupportedEncodingException, IOException
			{
				try
				{
					container.delete();
				} catch (Exception e) {
				}
			}
		});
		return container; 
	}
}
