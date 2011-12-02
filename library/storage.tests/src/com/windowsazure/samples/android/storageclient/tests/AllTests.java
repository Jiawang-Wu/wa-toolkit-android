package com.windowsazure.samples.android.storageclient.tests;

import junit.framework.Test;
import junit.framework.TestSuite;

public class AllTests {

	public static Test suite() {
		TestSuite suite = new TestSuite(AllTests.class.getName());
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

		// WAZ Service HTTPS
		suite.addTestSuite(WAZServiceAccountCredentialsUsingHttpsTests.class);

		// Tables - WAZ Service HTTPS
		suite.addTestSuite(CloudTableObjectUsingWAZServiceByHttpsTests.class);
		suite.addTestSuite(CloudTableClientUsingWAZServiceByHttpsTests.class);
		
		// Queues - WAZ Service HTTPS
		suite.addTestSuite(CloudQueueUsingWAZServiceByHttpsTests.class);
		suite.addTestSuite(CloudQueueMessagesUsingWAZServiceByHttpsTests.class);
		
		// Blobs - WAZ Service HTTPS
		suite.addTestSuite(CloudBlobContainerUsingSASServiceByHttpsTests.class);
		suite.addTestSuite(CloudBlockBlobUsingSASServiceByHttpsTests.class);

		// Tables - ACS
		suite.addTestSuite(CloudTableClientUsingWAZServiceWithACSTests.class);
		suite.addTestSuite(CloudTableObjectUsingWAZServiceWithACSTests.class);
		
		// Queues - ACS
		suite.addTestSuite(CloudQueueUsingWAZServiceWithACSTests.class);
		suite.addTestSuite(CloudQueueMessagesUsingWAZServiceWithACSTests.class);
		
		// Blobs - ACS
		suite.addTestSuite(CloudBlobContainerUsingSASServiceWithACSTests.class);
		suite.addTestSuite(CloudBlockBlobUsingSASServiceWithACSTests.class);
		
		// $JUnit-END$
		return suite;
	}
}
