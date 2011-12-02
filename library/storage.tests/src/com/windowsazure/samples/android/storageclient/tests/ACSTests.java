package com.windowsazure.samples.android.storageclient.tests;

import junit.framework.Test;
import junit.framework.TestSuite;

public class ACSTests {

	public static Test suite() {
		TestSuite suite = new TestSuite(AllTests.class.getName());
		// $JUnit-BEGIN$

		// Blobs - ACS
		suite.addTestSuite(CloudBlobContainerUsingSASServiceWithACSTests.class);
		suite.addTestSuite(CloudBlockBlobUsingSASServiceWithACSTests.class);

		// Queues - ACS
		suite.addTestSuite(CloudQueueUsingWAZServiceWithACSTests.class);
		suite.addTestSuite(CloudQueueMessagesUsingWAZServiceWithACSTests.class);

		// Tables - ACS
		suite.addTestSuite(CloudTableClientUsingWAZServiceWithACSTests.class);
		suite.addTestSuite(CloudTableObjectUsingWAZServiceWithACSTests.class);
		
		// $JUnit-END$
		return suite;
	}
}
