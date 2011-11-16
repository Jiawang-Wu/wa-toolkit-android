package com.windowsazure.samples.android.storageclient.tests;

import java.util.ArrayList;
import java.util.HashMap;

import junit.framework.Assert;

public abstract class TestCaseWithManagedResources extends TestCase {
	HashMap<Object, ResourceCleaner> resourceToCleanerMapping = new HashMap<Object, ResourceCleaner>();

	protected void addResourceCleaner(Object resource, ResourceCleaner cleaner) {
		resourceToCleanerMapping.put(resource, cleaner);
	}

	protected void removeResourceCleaner(Object resource) {
		resourceToCleanerMapping.remove(resource);
	}

	@Override
	protected void tearDown() {
		ArrayList<Exception> exceptionsWhileCleanningUp = new ArrayList<Exception>();
		for (ResourceCleaner cleanUp : resourceToCleanerMapping.values()) {
			try {
				cleanUp.clean();
			} catch (Exception exception) {
				exceptionsWhileCleanningUp.add(exception);
			}
		}
		if (!exceptionsWhileCleanningUp.isEmpty()) {
			Assert.fail("Exceptions while cleanning up the tests");
		}
	}
}
