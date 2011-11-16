package com.windowsazure.samples.android.storageclient.tests;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URISyntaxException;
import java.util.ArrayList;

import junit.framework.Assert;

import com.windowsazure.samples.android.storageclient.CloudQueue;
import com.windowsazure.samples.android.storageclient.CloudQueueClient;
import com.windowsazure.samples.android.storageclient.NotImplementedException;
import com.windowsazure.samples.android.storageclient.StorageException;
import com.windowsazure.samples.android.storageclient.StorageInnerException;

public abstract class CloudQueueClientBasedTest<T extends CloudClientAccountProvider>
	extends TestCaseWithManagedResources {
@Override
protected void setUp() {
	try {
		super.setUp();
		T accountProvider = SuperClassTypeParameterCreator.create(this, 0);
		cloudQueueClient = accountProvider.getCloudQueueClient();
	} catch (Exception e) {
		e.printStackTrace();
		Assert.fail();
	}
}

protected CloudQueue createQueue(String queueName)
		throws StorageException, NotImplementedException,
		URISyntaxException, UnsupportedEncodingException, IOException {
	final CloudQueue queue = new CloudQueue(queueName, cloudQueueClient);
	queue.create();
	this.addResourceCleaner(queue, cleanerFor(queue));
	return queue;
}

protected void deleteQueue(CloudQueue queue)
		throws StorageException, NotImplementedException,
		URISyntaxException, UnsupportedEncodingException, IOException,
		StorageInnerException {
	queue.delete();
	this.removeResourceCleaner(queue);
}

protected ResourceCleaner cleanerFor(final CloudQueue queue) {
	return new ResourceCleaner() {
		public void clean() throws NotImplementedException,
				StorageException, UnsupportedEncodingException, IOException {
			try {
				queue.delete();
			} catch (Exception e) {
			}
		}
	};
}

protected ArrayList<String> getQueuesNames(
		Iterable<CloudQueue> queues)
		throws NotImplementedException {
	ArrayList<String> names = new ArrayList<String>();
	for (CloudQueue queue : queues) {
		names.add(queue.getName());
	}
	return names;
}

protected CloudQueueClient cloudQueueClient;
}
