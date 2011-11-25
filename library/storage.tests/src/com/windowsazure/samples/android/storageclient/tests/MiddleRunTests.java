package com.windowsazure.samples.android.storageclient.tests;

import junit.framework.Test;
import junit.framework.TestSuite;

public class MiddleRunTests {

	public static Test suite() {
		TestSuite suite = new TestSuite(MiddleRunTests.class.getName());
		// $JUnit-BEGIN$
		
		// Tables - Direct
		suite.addTestSuite(CloudTableClientUsingAccountAndKeyTests.class);
		suite.addTestSuite(CloudTableObjectUsingAccountAndKeyTests.class);
		
		// Queues - Direct
		suite.addTestSuite(CloudQueueUsingAccountAndKeyTests.class);
		suite.addTestSuite(CloudQueueMessagesUsingAccountAndKeyTests.class);
		
		// Blobs - Direct
		suite.addTestSuite(CloudBlobContainerUsingAccountAndKeyTests.class);
		suite.addTestSuite(CloudBlockBlobUsingAccountAndKeyTests.class);

		// WAZ Service HTTP
		suite.addTestSuite(WAZServiceAccountCredentialsUsingHttpTests.class);
		
		// Tables - WAZ Service HTTP
		suite.addTestSuite(CloudTableObjectUsingWAZServiceByHttpTests.class);
		suite.addTestSuite(CloudTableClientUsingWAZServiceByHttpTests.class);
		
		// Queues - WAZ Service HTTP
		suite.addTestSuite(CloudQueueUsingWAZServiceByHttpTests.class);
		suite.addTestSuite(CloudQueueMessagesUsingWAZServiceByHttpTests.class);
		
		// Blobs - WAZ Service HTTP
		suite.addTestSuite(CloudBlobContainerUsingSASServiceByHttpTests.class);
		suite.addTestSuite(CloudBlockBlobUsingSASServiceByHttpTests.class);

		// $JUnit-END$
		return suite;
	}

}
