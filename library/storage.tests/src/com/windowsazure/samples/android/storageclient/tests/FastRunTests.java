package com.windowsazure.samples.android.storageclient.tests;

import junit.framework.Test;
import junit.framework.TestSuite;

public class FastRunTests {

	public static Test suite() {
		TestSuite suite = new TestSuite(FastRunTests.class.getName());
		// $JUnit-BEGIN$

		// Tables - Direct
		suite.addTestSuite(CloudTableObjectUsingAccountAndKeyTests.class);
		
		// Queues - Direct
		suite.addTestSuite(CloudQueueMessagesUsingAccountAndKeyTests.class);
		
		// Blobs - Direct
		suite.addTestSuite(CloudBlockBlobUsingAccountAndKeyTests.class);

		// WAZ Service HTTP
		suite.addTestSuite(WAZServiceAccountCredentialsUsingHttpTests.class);
		
		// Tables - WAZ Service HTTP
		suite.addTestSuite(CloudTableObjectUsingWAZServiceByHttpTests.class);
		
		// Queues - WAZ Service HTTP
		suite.addTestSuite(CloudQueueMessagesUsingWAZServiceByHttpTests.class);
		
		// Blobs - WAZ Service HTTP
		suite.addTestSuite(CloudBlockBlobUsingSASServiceByHttpTests.class);

		// $JUnit-END$
		return suite;
	}

}
