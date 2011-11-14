package com.windowsazure.samples.android.storageclient.tests;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import junit.framework.Assert;

import com.windowsazure.samples.android.storageclient.BlobContainerPermissions;
import com.windowsazure.samples.android.storageclient.BlobContainerPublicAccessType;
import com.windowsazure.samples.android.storageclient.CloudBlob;
import com.windowsazure.samples.android.storageclient.CloudBlobContainer;
import com.windowsazure.samples.android.storageclient.NotImplementedException;
import com.windowsazure.samples.android.storageclient.StorageException;

public abstract class CloudBlobContainerTests<T extends CloudClientAccountProvider>
		extends CloudBlobClientBasedTest<T> {

	/** Invalid attempts to create containers - START **/
	public void testCreateContainerWithInvalidNameThrowsException()
			throws Exception {
		this.assertThrows(new RunnableWithExpectedException() {
			public void run() throws Exception {
				thisTest.createContainer("my_test_container");
			}
		}, StorageException.class);
		Assert.assertFalse(this.cloudBlobClient.listContainers().iterator()
				.hasNext());
	}

	public void testCreateContainerWithEmptydNameThrowsException()
			throws Exception {
		this.assertThrows(new RunnableWithExpectedException() {
			public void run() throws Exception {
				new CloudBlobContainer("", cloudBlobClient);
			}
		}, IllegalArgumentException.class);
		Assert.assertFalse(this.cloudBlobClient.listContainers().iterator()
				.hasNext());
	}

	public void testCreateContainerWithNullNameThrowsException()
			throws Exception {
		this.assertThrows(new RunnableWithExpectedException() {
			public void run() throws Exception {
				new CloudBlobContainer((String) null, cloudBlobClient);
			}
		}, IllegalArgumentException.class);
		Assert.assertFalse(this.cloudBlobClient.listContainers().iterator()
				.hasNext());
	}

	public void testCreateContainerTwiceThrowsException() throws Exception {
		final CloudBlobContainer container = this
				.createContainer("testcreatecontainertwicethrowsexception");
		List<CloudBlobContainer> containers = this.toList(this.cloudBlobClient
				.listContainers());
		Assert.assertEquals(containers.size(), 1);
		Assert.assertEquals(containers.get(0).getName(), container.getName());
		this.assertThrows(new RunnableWithExpectedException() {
			public void run() throws Exception {
				container.create();
			}
		}, StorageException.class);
		containers = this.toList(this.cloudBlobClient.listContainers());
		Assert.assertEquals(containers.size(), 1);
	}

	public void testCreateExistentContainerThrowsException() throws Exception {
		this.createContainer("testcreateexistentcontainerthrowsexception");
		this.assertThrows(new RunnableWithExpectedException() {
			public void run() throws Exception {
				thisTest.createContainer("testcreateexistentcontainerthrowsexception");
			}
		}, StorageException.class);
		List<CloudBlobContainer> containers = this.toList(this.cloudBlobClient
				.listContainers());
		Assert.assertEquals(containers.size(), 1);
	}

	/** Invalid attempts to create containers - END **/

	/** Invalid attempts to delete containers - START **/
	public void testDeleteNonexistantContainerThrowsException()
			throws Exception {
		final CloudBlobContainer container = new CloudBlobContainer(
				"non-existant-container", cloudBlobClient);
		this.assertThrows(new RunnableWithExpectedException() {
			public void run() throws Exception {
				container.delete();
			}
		}, StorageException.class);
	}

	public void testDeleteContainerWithInvalidNameThrowsException()
			throws Exception {
		final CloudBlobContainer container = new CloudBlobContainer(
				"invalid_container", cloudBlobClient);
		this.assertThrows(new RunnableWithExpectedException() {
			public void run() throws Exception {
				container.delete();
			}
		}, StorageException.class);
	}

	/** Invalid attempts to delete containers - END **/

	/** Listing containers - START **/
	public void testCreatedContainerIsListed() throws Exception {
		final CloudBlobContainer container = this
				.createContainer("testcreatedcontainerislisted");
		List<CloudBlobContainer> containers = this.toList(this.cloudBlobClient
				.listContainers());
		Assert.assertEquals(containers.size(), 1);
		Assert.assertEquals(containers.get(0).getName(), container.getName());
	}

	public void testPublicContainerIsListed() throws Exception {
		final CloudBlobContainer container = this
				.createContainer("testcreatedcontainerislisted");
		BlobContainerPermissions permissions = new BlobContainerPermissions();
		permissions.publicAccess = BlobContainerPublicAccessType.CONTAINER;
		container.uploadPermissions(permissions);
		List<CloudBlobContainer> containers = this.toList(this.cloudBlobClient
				.listContainers());
		Assert.assertEquals(containers.size(), 1);
		Assert.assertEquals(containers.get(0).getName(), container.getName());
	}

	public void testDeletedContainerIsNotListed() throws Exception {
		final CloudBlobContainer container = this
				.createContainer("testdeletedcontainerisnotlisted");
		List<CloudBlobContainer> containers = this.toList(this.cloudBlobClient
				.listContainers());
		Assert.assertEquals(containers.size(), 1);
		Assert.assertEquals(containers.get(0).getName(), container.getName());
		container.delete();
		Assert.assertFalse(this.cloudBlobClient.listContainers().iterator()
				.hasNext());
	}

	public void testListingIsAccurateWhileCreatingAndDeletingSeveralContainers()
			throws Exception {
		final String containerBaseName = "listingisaccuratewhilecreatinganddeletingseveralcontainers-";
		final ArrayList<String> expectedContainerNames = new ArrayList<String>();
		class CreateAndDeleteContainersHelper {
			void create(String suffix) throws Exception {
				String containerName = containerBaseName + suffix;
				thisTest.createContainer(containerName);
				expectedContainerNames.add(containerName);
				this.assertListingGivesExpectedContainers();
			}

			void delete(String suffix) throws Exception {
				String containerName = containerBaseName + suffix;
				CloudBlobContainer container = new CloudBlobContainer(
						containerName, cloudBlobClient);
				thisTest.deleteContainer(container);
				expectedContainerNames.remove(containerName);
				this.assertListingGivesExpectedContainers();
			}

			void assertListingGivesExpectedContainers()
					throws NotImplementedException, Exception {
				ArrayList<String> containerNames = thisTest
						.getContainerNames(cloudBlobClient.listContainers());
				thisTest.AssertHaveSameElements(expectedContainerNames,
						containerNames);
			}
		}
		;

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

	public void testListingContainersByPrefixes() throws Exception {
		this.createContainer("abc-0");
		this.createContainer("abc-1");
		this.createContainer("abc-2");
		this.createContainer("ab-3");
		this.createContainer("a-4");
		this.createContainer("def-5");
		this.createContainer("def-6");
		Assert.assertFalse(cloudBlobClient.listContainers("nothing").iterator()
				.hasNext());
		this.AssertHaveSameElements(
				this.getContainerNames(cloudBlobClient.listContainers("abc")),
				Arrays.asList(new String[] { "abc-0", "abc-1", "abc-2" }));
		this.AssertHaveSameElements(
				this.getContainerNames(cloudBlobClient.listContainers("abc-")),
				Arrays.asList(new String[] { "abc-0", "abc-1", "abc-2" }));
		this.AssertHaveSameElements(
				this.getContainerNames(cloudBlobClient.listContainers("abc-0")),
				Arrays.asList(new String[] { "abc-0" }));
		this.AssertHaveSameElements(this.getContainerNames(cloudBlobClient
				.listContainers("ab")), Arrays.asList(new String[] { "ab-3",
				"abc-0", "abc-1", "abc-2" }));
		this.AssertHaveSameElements(
				this.getContainerNames(cloudBlobClient.listContainers("ab-")),
				Arrays.asList(new String[] { "ab-3" }));
		this.AssertHaveSameElements(
				this.getContainerNames(cloudBlobClient.listContainers("a")),
				Arrays.asList(new String[] { "ab-3", "abc-0", "abc-1", "abc-2",
						"a-4" }));
		this.AssertHaveSameElements(
				this.getContainerNames(cloudBlobClient.listContainers("d")),
				Arrays.asList(new String[] { "def-5", "def-6" }));
		this.AssertHaveSameElements(this.getContainerNames(cloudBlobClient
				.listContainers("def-55")), Arrays.asList(new String[] {}));
		this.AssertHaveSameElements(
				this.getContainerNames(cloudBlobClient.listContainers("blah")),
				Arrays.asList(new String[] {}));
		this.AssertHaveSameElements(
				this.getContainerNames(cloudBlobClient.listContainers("")),
				Arrays.asList(new String[] { "ab-3", "abc-0", "abc-1", "abc-2",
						"a-4", "def-5", "def-6" }));
	}

	/** Listing containers - END **/

	/** Getting container SAS - START **/
	public void testContainersDoesntShareUris() throws Exception {
		CloudBlobContainer container = this
				.createContainer("testcontainersdoesntshareuris-1");
		CloudBlobContainer otherContainer = this
				.createContainer("testcontainersdoesntshareuris-2");
		URI firstUri = container.getUri();
		URI secondUri = otherContainer.getUri();
		Assert.assertTrue(!firstUri.equals(secondUri));
	}

	public void testContainerUriHasProperPattern() throws Exception {
		CloudBlobContainer container = this
				.createContainer("testcontainerurihasproperpattern");
		URI uri = container.getUri();
		Assert.assertTrue(uri.getAuthority().endsWith(".blob.core.windows.net"));
		Assert.assertEquals(uri.getPath(), "/testcontainerurihasproperpattern");
	}

	/** Getting container SAS - END **/

	/** Listing blob's SASs - START **/
	public void testCreatedBlobsAreListed() throws Exception {
		String blob1Name = "blob1";
		String blob2Name = "blob2";
		CloudBlobContainer container = this
				.createContainer("testcreatedblobsarelisted");

		this.createEmptyBlob(container, blob1Name);
		this.AssertHaveSameElements(this.getBlobNames(container.listBlobs()),
				Arrays.asList(new String[] { blob1Name }));

		this.createEmptyBlob(container, blob2Name);
		this.AssertHaveSameElements(this.getBlobNames(container.listBlobs()),
				Arrays.asList(new String[] { blob1Name, blob2Name }));
	}

	public void testCreatedBlobsAreListedProperlyByPrefixesUsingFlatListing()
			throws Exception {
		CloudBlobContainer container = this
				.createContainer("testcreatedblobsarelistedproperlybyprefixes");
		this.createEmptyBlob(container, "abc/def/jkl");
		this.createEmptyBlob(container, "abc/0");
		this.createEmptyBlob(container, "abc/1");
		this.createEmptyBlob(container, "abc/2");
		this.createEmptyBlob(container, "ab/3");
		this.createEmptyBlob(container, "a/4");
		this.createEmptyBlob(container, "def/5");
		this.createEmptyBlob(container, "def/6");

		Assert.assertFalse(container.listBlobs("nothing").iterator().hasNext());
		ArrayList<CloudBlob> blobs = this.toList(container.listBlobs("abc",
				true));
		this.AssertHaveSameElements(
				this.getBlobNames(blobs),
				Arrays.asList(new String[] { "abc/0", "abc/1", "abc/2",
						"abc/def/jkl" }));
		this.AssertHaveSameElements(
				this.getBlobNames(container.listBlobs("abc/", true)),
				Arrays.asList(new String[] { "abc/0", "abc/1", "abc/2",
						"abc/def/jkl" }));
		this.AssertHaveSameElements(
				this.getBlobNames(container.listBlobs("abc/0", true)),
				Arrays.asList(new String[] { "abc/0" }));
		this.AssertHaveSameElements(
				this.getBlobNames(container.listBlobs("ab", true)),
				Arrays.asList(new String[] { "ab/3", "abc/0", "abc/1", "abc/2",
						"abc/def/jkl" }));
		this.AssertHaveSameElements(
				this.getBlobNames(container.listBlobs("ab/", true)),
				Arrays.asList(new String[] { "ab/3" }));
		this.AssertHaveSameElements(
				this.getBlobNames(container.listBlobs("a", true)),
				Arrays.asList(new String[] { "ab/3", "abc/0", "abc/1", "abc/2",
						"a/4", "abc/def/jkl" }));
		this.AssertHaveSameElements(
				this.getBlobNames(container.listBlobs("d", true)),
				Arrays.asList(new String[] { "def/5", "def/6" }));
		this.AssertHaveSameElements(
				this.getBlobNames(container.listBlobs("def/55", true)),
				Arrays.asList(new String[] {}));
		this.AssertHaveSameElements(
				this.getBlobNames(container.listBlobs("blah", true)),
				Arrays.asList(new String[] {}));
		this.AssertHaveSameElements(
				this.getBlobNames(container.listBlobs("", true)),
				Arrays.asList(new String[] { "ab/3", "abc/0", "abc/1", "abc/2",
						"a/4", "def/5", "def/6", "abc/def/jkl" }));
	}

	public void testListWithEmptyPrefixAuthorizesCorrectly() throws Exception {
		CloudBlobContainer container = this
				.createContainer("testlistwithemptyprefixauthorizescorrectly");
		this.AssertHaveSameElements(
				this.getBlobNames(container.listBlobs("", true)),
				Arrays.asList(new String[] {}));
		this.AssertHaveSameElements(
				this.getBlobNames(container.listBlobs("", false)),
				Arrays.asList(new String[] {}));
	}

	public void testUploadingEmptyOrNullValueForMetadataThrowsException()
			throws Exception {
		final CloudBlobContainer container = this
				.createContainer("testuploadingemptyornullvaluethrowsexception");

		container.getMetadata().put("key2", "");
		this.assertThrows(new RunnableWithExpectedException() {
			public void run() throws Exception {
				container.uploadMetadata();
			}
		}, StorageException.class);

		container.getMetadata().clear();
		container.getMetadata().put("key2", null);
		this.assertThrows(new RunnableWithExpectedException() {
			public void run() throws Exception {
				container.uploadMetadata();
			}
		}, StorageException.class);
	}

	public void testCreatedBlobsAreListedProperlyByPrefixesNotUsingFlatListing()
			throws Exception {
		CloudBlobContainer container = this
				.createContainer("testcreatedblobsarelistedproperlybyprefixesusingflatlisting");
		this.createEmptyBlob(container, "abc/def/jkl");
		this.createEmptyBlob(container, "abc/0");
		this.createEmptyBlob(container, "abc/1");
		this.createEmptyBlob(container, "abc/2");
		this.createEmptyBlob(container, "ab/3");
		this.createEmptyBlob(container, "a/4");
		this.createEmptyBlob(container, "def/5");
		this.createEmptyBlob(container, "def/6");
		this.createEmptyBlob(container, "ghi");

		Assert.assertFalse(container.listBlobs("nothing", false).iterator()
				.hasNext());
		this.AssertHaveSameElements(
				this.getBlobNames(container.listBlobs("abc", false)),
				Arrays.asList(new String[] {}));
		this.AssertHaveSameElements(
				this.getBlobNames(container.listBlobs("abc/", false)),
				Arrays.asList(new String[] { "abc/0", "abc/1", "abc/2" }));
		this.AssertHaveSameElements(
				this.getBlobNames(container.listBlobs("abc/0", false)),
				Arrays.asList(new String[] { "abc/0" }));
		this.AssertHaveSameElements(
				this.getBlobNames(container.listBlobs("ab", false)),
				Arrays.asList(new String[] {}));
		this.AssertHaveSameElements(
				this.getBlobNames(container.listBlobs("ab/", false)),
				Arrays.asList(new String[] { "ab/3" }));
		this.AssertHaveSameElements(
				this.getBlobNames(container.listBlobs("a", false)),
				Arrays.asList(new String[] {}));
		this.AssertHaveSameElements(
				this.getBlobNames(container.listBlobs("d", false)),
				Arrays.asList(new String[] {}));
		this.AssertHaveSameElements(
				this.getBlobNames(container.listBlobs("def/", false)),
				Arrays.asList(new String[] { "def/5", "def/6" }));
		this.AssertHaveSameElements(
				this.getBlobNames(container.listBlobs("def/55", false)),
				Arrays.asList(new String[] {}));
		this.AssertHaveSameElements(
				this.getBlobNames(container.listBlobs("blah", false)),
				Arrays.asList(new String[] {}));

		this.AssertHaveSameElements(
				this.getBlobNames(container.listBlobs("", false)),
				Arrays.asList(new String[] { "ghi" }));
	}

	/** Listing blob's SASs - END 
	 * @throws IOException 
	 * @throws StorageException 
	 * @throws NotImplementedException 
	 * @throws UnsupportedEncodingException 
	 * @throws URISyntaxException **/

	@Override
	protected void setUp() {
		super.setUp();
		thisTest = this;
	}

	private CloudBlobContainerTests<T> thisTest;
}
