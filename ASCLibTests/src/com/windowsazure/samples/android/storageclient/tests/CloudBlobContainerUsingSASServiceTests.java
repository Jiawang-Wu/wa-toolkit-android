package com.windowsazure.samples.android.storageclient.tests;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.ParameterizedType;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;

import org.apache.http.HttpRequest;
import org.apache.http.HttpResponse;
import org.apache.http.StatusLine;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.impl.client.DefaultHttpClient;

import junit.framework.Assert;

import com.windowsazure.samples.android.storageclient.CloudBlobClient;
import com.windowsazure.samples.android.storageclient.CloudBlobContainer;
import com.windowsazure.samples.android.storageclient.NotImplementedException;
import com.windowsazure.samples.android.storageclient.StorageException;
import com.windowsazure.samples.android.storageclient.StorageInnerException;

public abstract class CloudBlobContainerUsingSASServiceTests<T extends WAZServiceAccountProvider> extends TestCaseWithManagedResources {
	
	public void testCreateContainerWithInvalidNameThrowsException()
			throws Exception {
		this.assertThrows(new RunnableWithExpectedException() {
			@Override
			public void run() throws Exception {
				thisTest.createContainer("my_test_container");
			}
		}, StorageException.class);
	}

	public void testCreateContainerWithEmptydNameThrowsException()
			throws Exception {
		this.assertThrows(new RunnableWithExpectedException() {
			@Override
			public void run() throws Exception {
				new CloudBlobContainer("", cloudBlobClient);
			}
		}, IllegalArgumentException.class);
	}

	public void testCreateContainerWithNullNameThrowsException()
			throws Exception {
		this.assertThrows(new RunnableWithExpectedException() {
			@Override
			public void run() throws Exception {
				new CloudBlobContainer((String) null, cloudBlobClient);
			}
		}, IllegalArgumentException.class);
	}

	public void testCreateContainerTwiceThrowsException()
			throws Exception {
		final CloudBlobContainer container = this.createContainer("testcreatecontainertwicethrowsexception");
		this.assertThrows(new RunnableWithExpectedException() {
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
		this.assertThrows(new RunnableWithExpectedException() {
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
		this.assertThrows(new RunnableWithExpectedException() {
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
		this.assertThrows(new RunnableWithExpectedException() {
			@Override
			public void run() throws Exception {
				thisTest.createContainer("testcreateexistentcontainerthrowsexception");
			}
		}, StorageException.class);
	}

	public void testDeleteNonexistantContainerThrowsException() throws Exception {
		final CloudBlobContainer container = new CloudBlobContainer("non-existant-container", cloudBlobClient);
		this.assertThrows(new RunnableWithExpectedException() {
			@Override
			public void run() throws Exception {
				container.delete();
			}
		}, StorageException.class);
	}

	public void testDeleteContainerWithInvalidNameThrowsException() throws Exception {
		final CloudBlobContainer container = new CloudBlobContainer("invalid_container", cloudBlobClient);
		this.assertThrows(new RunnableWithExpectedException() {
			@Override
			public void run() throws Exception {
				container.delete();
			}
		}, StorageException.class);
	}

	public void testDeleteContainerTwiceThrowsException() throws Exception {
		final CloudBlobContainer container = this.createContainer("testdeletecontainertwicethrowsexception");
		container.delete();
		this.assertThrows(new RunnableWithExpectedException() {
			@Override
			public void run() throws Exception {
				container.delete();
			}
		}, StorageException.class);
	}

	public void testContainersDoesntShareUris() throws Exception {
		CloudBlobContainer container = this.createContainer("testcontainersdoesntshareuris-1");
		CloudBlobContainer otherContainer = this.createContainer("testcontainersdoesntshareuris-2");
		URI firstUri = container.getUri();
		URI secondUri = container.getUri();
		Assert.assertTrue(firstUri != secondUri);
	}

	public void testContainerUriHasProperPattern() throws Exception {
		CloudBlobContainer container = this.createContainer("testcontainerurihasproperpattern");
		URI uri = container.getUri();
		Assert.assertTrue(uri.getAuthority().endsWith(".blob.core.windows.net"));
		Assert.assertEquals(uri.getPath(), "/testcontainerurihasproperpattern");
		String query = uri.getQuery();
		String[] arguments = query.split("&");
		ArrayList<String> argumentNames = new ArrayList<String>();
		for (String argument : arguments)
		{
			argumentNames.add(argument.split("=")[0]);
		}
		Assert.assertTrue(argumentNames.contains("se"));
		Assert.assertTrue(argumentNames.contains("amp;sr"));
		Assert.assertTrue(argumentNames.contains("amp;sp"));
		Assert.assertTrue(argumentNames.contains("amp;sig"));
	}

	private CloudBlobClient cloudBlobClient;

	private CloudBlobClient otherCloudBlobClient;
	
	private CloudBlobContainerUsingSASServiceTests thisTest;

	public void setUp()
	{
		thisTest = this;
		try {
			T accountProvider = SuperClassTypeParameterCreator.create(this, 0);
			cloudBlobClient = accountProvider.getCloudBlobClient();
			otherCloudBlobClient = accountProvider.getCloudBlobClientWithDifferentAccount();
		} catch (Exception e) {
			e.printStackTrace();
			Assert.fail();
		}
	}

	private CloudBlobContainer createContainer(String containerName) throws StorageException, NotImplementedException, URISyntaxException, UnsupportedEncodingException, IOException {
		final CloudBlobContainer container = new CloudBlobContainer(containerName, cloudBlobClient);
		container.create();
		this.addCleanUp(new ResourceCleaner()
		{
			public void clean() throws NotImplementedException, StorageException, UnsupportedEncodingException, IOException
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
