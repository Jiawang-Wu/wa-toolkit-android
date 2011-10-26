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
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;

import org.apache.http.HttpRequest;
import org.apache.http.HttpResponse;
import org.apache.http.StatusLine;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.impl.client.DefaultHttpClient;

import junit.framework.Assert;

import com.windowsazure.samples.android.storageclient.BlobContainerPermissions;
import com.windowsazure.samples.android.storageclient.BlobContainerPublicAccessType;
import com.windowsazure.samples.android.storageclient.CloudBlob;
import com.windowsazure.samples.android.storageclient.CloudBlobClient;
import com.windowsazure.samples.android.storageclient.CloudBlobContainer;
import com.windowsazure.samples.android.storageclient.NotImplementedException;
import com.windowsazure.samples.android.storageclient.StorageException;
import com.windowsazure.samples.android.storageclient.StorageInnerException;

public abstract class CloudBlobContainerUsingSASServiceTests<T extends WAZServiceAccountProvider> extends TestCaseWithManagedResources {
	
	/** Invalid attempts to create containers - START **/
	public void testCreateContainerWithInvalidNameThrowsException()
			throws Exception {
		this.assertThrows(new RunnableWithExpectedException() {
			@Override
			public void run() throws Exception {
				thisTest.createContainer("my_test_container");
			}
		}, StorageException.class);
		Assert.assertFalse(this.cloudBlobClient.listContainers().iterator().hasNext());
	}

	public void testCreateContainerWithEmptydNameThrowsException()
			throws Exception {
		this.assertThrows(new RunnableWithExpectedException() {
			@Override
			public void run() throws Exception {
				new CloudBlobContainer("", cloudBlobClient);
			}
		}, IllegalArgumentException.class);
		Assert.assertFalse(this.cloudBlobClient.listContainers().iterator().hasNext());
	}

	public void testCreateContainerWithNullNameThrowsException()
			throws Exception {
		this.assertThrows(new RunnableWithExpectedException() {
			@Override
			public void run() throws Exception {
				new CloudBlobContainer((String) null, cloudBlobClient);
			}
		}, IllegalArgumentException.class);
		Assert.assertFalse(this.cloudBlobClient.listContainers().iterator().hasNext());
	}

	public void testCreateContainerTwiceThrowsException()
			throws Exception {
		final CloudBlobContainer container = this.createContainer("testcreatecontainertwicethrowsexception");
		List<CloudBlobContainer> containers = this.toList(this.cloudBlobClient.listContainers());
		Assert.assertEquals(containers.size(), 1);
		Assert.assertEquals(containers.get(0).getName(), container.getName());
		this.assertThrows(new RunnableWithExpectedException() {
			@Override
			public void run() throws Exception {
				container.create();
			}
		}, StorageException.class);
		containers = this.toList(this.cloudBlobClient.listContainers());
		Assert.assertEquals(containers.size(), 1);
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

	public void testCreateExistentContainerThrowsException()
			throws Exception {
		this.createContainer("testcreateexistentcontainerthrowsexception");
		this.assertThrows(new RunnableWithExpectedException() {
			@Override
			public void run() throws Exception {
				thisTest.createContainer("testcreateexistentcontainerthrowsexception");
			}
		}, StorageException.class);
		List<CloudBlobContainer> containers = this.toList(this.cloudBlobClient.listContainers());
		Assert.assertEquals(containers.size(), 1);
	}
	/** Invalid attempts to create containers - END **/

	/** Invalid attempts to delete containers - START **/
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
		List<CloudBlobContainer> containers = this.toList(this.cloudBlobClient.listContainers());
		Assert.assertEquals(containers.size(), 1);
		container.delete();
		Assert.assertFalse(this.cloudBlobClient.listContainers().iterator().hasNext());
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
	/** Invalid attempts to delete containers - END **/

	/** Listing containers - START **/
	public void testCreatedContainerIsListed()
			throws Exception {
		final CloudBlobContainer container = this.createContainer("testcreatedcontainerislisted");
		List<CloudBlobContainer> containers = this.toList(this.cloudBlobClient.listContainers());
		Assert.assertEquals(containers.size(), 1);
		Assert.assertEquals(containers.get(0).getName(), container.getName());
	}

	public void testPublicContainerIsListed()
			throws Exception {
		final CloudBlobContainer container = this.createContainer("testcreatedcontainerislisted");
		BlobContainerPermissions permissions = new BlobContainerPermissions();
		permissions.publicAccess = BlobContainerPublicAccessType.CONTAINER;
		container.uploadPermissions(permissions);
		List<CloudBlobContainer> containers = this.toList(this.cloudBlobClient.listContainers());
		Assert.assertEquals(containers.size(), 1);
		Assert.assertEquals(containers.get(0).getName(), container.getName());
	}

	public void testDeletedContainerIsNotListed()
			throws Exception {
		final CloudBlobContainer container = this.createContainer("testdeletedcontainerisnotlisted");
		List<CloudBlobContainer> containers = this.toList(this.cloudBlobClient.listContainers());
		Assert.assertEquals(containers.size(), 1);
		Assert.assertEquals(containers.get(0).getName(), container.getName());
		container.delete();
		Assert.assertFalse(this.cloudBlobClient.listContainers().iterator().hasNext());
	}
	public void testListingIsAccurateWhileCreatingAndDeletingSeveralContainers()
			throws Exception {
		final String containerBaseName = "listingisaccuratewhilecreatinganddeletingseveralcontainers-";
		final ArrayList<String> expectedContainerNames = new ArrayList<String>(); 
		class CreateAndDeleteContainersHelper
		{
			void create(String suffix) throws Exception
			{
				String containerName = containerBaseName + suffix;
				thisTest.createContainer(containerName);
				expectedContainerNames.add(containerName);
				this.assertListingGivesExpectedContainers();
			}
			void delete(String suffix) throws Exception
			{
				String containerName = containerBaseName + suffix;
				CloudBlobContainer container = new CloudBlobContainer(containerName, cloudBlobClient);
				container.delete();
				expectedContainerNames.remove(containerName);
				this.assertListingGivesExpectedContainers();
			}
			void assertListingGivesExpectedContainers() throws NotImplementedException, Exception
			{
				ArrayList<String> containerNames = thisTest.getContainerNames(cloudBlobClient.listContainers());
				thisTest.AssertHaveSameElements(expectedContainerNames, containerNames);
			}
		};
		
		CreateAndDeleteContainersHelper helper = new CreateAndDeleteContainersHelper();
		helper.create("0");
		helper.delete("0");
		helper.create("1");
		helper.create("2");
		helper.delete("1");
		helper.delete("2");
		helper.create("3");
		helper.create("4");
		helper.delete("4");
		helper.delete("3");
		helper.create("5");
		helper.create("6");
		helper.create("7");
		helper.create("8");
		helper.delete("5");
		helper.delete("7");
		helper.create("9");
		helper.create("10");
		helper.delete("6");
		helper.create("11");
		helper.delete("11");
		helper.delete("9");
		helper.delete("10");
		helper.delete("8");
		helper.create("12");
		helper.delete("12");
		helper.create("13");
		helper.create("14");
		helper.delete("13");
		helper.delete("14");
	}

	public void testListingByPrefixes() throws Exception
	{
		this.createContainer("abc-0");
		this.createContainer("abc-1");
		this.createContainer("abc-2");
		this.createContainer("ab-3");
		this.createContainer("a-4");
		this.createContainer("def-5");
		this.createContainer("def-6");
		Assert.assertFalse(cloudBlobClient.listContainers("nothing").iterator().hasNext());
		this.AssertHaveSameElements(this.getContainerNames(cloudBlobClient.listContainers("abc")),
				Arrays.asList(new String[]{"abc-0", "abc-1", "abc-2"}));
		this.AssertHaveSameElements(this.getContainerNames(cloudBlobClient.listContainers("abc-")),
				Arrays.asList(new String[]{"abc-0", "abc-1", "abc-2"}));
		this.AssertHaveSameElements(this.getContainerNames(cloudBlobClient.listContainers("abc-0")),
				Arrays.asList(new String[]{"abc-0"}));
		this.AssertHaveSameElements(this.getContainerNames(cloudBlobClient.listContainers("ab")),
				Arrays.asList(new String[]{"ab-3", "abc-0", "abc-1", "abc-2"}));
		this.AssertHaveSameElements(this.getContainerNames(cloudBlobClient.listContainers("ab-")),
				Arrays.asList(new String[]{"ab-3"}));
		this.AssertHaveSameElements(this.getContainerNames(cloudBlobClient.listContainers("a")),
				Arrays.asList(new String[]{"ab-3", "abc-0", "abc-1", "abc-2", "a-4"}));
		this.AssertHaveSameElements(this.getContainerNames(cloudBlobClient.listContainers("d")),
				Arrays.asList(new String[]{"def-5", "def-6"}));
		this.AssertHaveSameElements(this.getContainerNames(cloudBlobClient.listContainers("def-55")),
				Arrays.asList(new String[]{}));
		this.AssertHaveSameElements(this.getContainerNames(cloudBlobClient.listContainers("blah")),
				Arrays.asList(new String[]{}));
		this.AssertHaveSameElements(this.getContainerNames(cloudBlobClient.listContainers("")),
				Arrays.asList(new String[]{"ab-3", "abc-0", "abc-1", "abc-2", "a-4", "def-5", "def-6"}));
	}
	/** Listing containers - END **/

	/** Getting container SAS - START **/
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
	/** Getting container SAS - END **/

	public void testUsersCanSeeOtherUsersContainers()
			throws Exception {
		String privateContainerName = "testuserscanseeotheruserscontainers-private";
		String publicContainerName = "testuserscanseeotheruserscontainers-public";
		final CloudBlobContainer privatecontainer = this.createContainer(privateContainerName);
		final CloudBlobContainer publicContainer = this.createContainer(publicContainerName);
		
		BlobContainerPermissions permissions = new BlobContainerPermissions();
		permissions.publicAccess = BlobContainerPublicAccessType.CONTAINER;
		publicContainer.uploadPermissions(permissions);

		this.AssertHaveSameElements(this.getContainerNames(otherCloudBlobClient.listContainers()),
				Arrays.asList(new String[]{privateContainerName, publicContainerName}));
	}

	/** Listing blob's SASs - START **/
	/*
	public void testListBlobs()
			throws Exception {
		String containerName = "testgetblobsas";
		String blobName = "testgetblobsas";
		CloudBlobContainer container = new CloudBlobContainer(containerName, cloudBlobClient);
		this.AssertHaveSameElements(this.getBlobNames(container.listBlobs()),
				Arrays.asList(new String[]{blobName}));
	}*/
	/** Listing blob's SASs - END **/

	private <T> void AssertHaveSameElements(Collection<T> firstCollection, Collection<T> secondCollection) {
		Assert.assertEquals(firstCollection.size(), secondCollection.size());
		Assert.assertFalse(firstCollection.retainAll(secondCollection));
		Assert.assertFalse(secondCollection.retainAll(firstCollection));
	}

		private ArrayList<String> getContainerNames(Iterable<CloudBlobContainer> containers) throws NotImplementedException 
		{
			ArrayList<String> names = new ArrayList<String>();
			for(CloudBlobContainer container : containers)
			{
				names.add(container.getName());
			}
			return names;
		}

		private ArrayList<String> getBlobNames(Iterable<CloudBlob> blobs) throws NotImplementedException, URISyntaxException 
		{
			ArrayList<String> names = new ArrayList<String>();
			for(CloudBlob blob : blobs)
			{
				names.add(blob.getName());
			}
			return names;
		}

		private <T> ArrayList<T> toList(Iterable<T> iterable) {
		ArrayList<T> list = new ArrayList<T>();
		for (T element : iterable)
		{
			list.add(element);
		}
		return list;
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

	private CloudBlobClient cloudBlobClient;

	private CloudBlobClient otherCloudBlobClient;
	
	private CloudBlobContainerUsingSASServiceTests<T> thisTest;
}
