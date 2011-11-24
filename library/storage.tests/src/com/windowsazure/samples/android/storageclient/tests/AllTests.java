package com.windowsazure.samples.android.storageclient.tests;

import junit.framework.Test;
import junit.framework.TestSuite;

public class AllTests {

	public static Test suite() {
		TestSuite suite = new TestSuite(AllTests.class.getName());
		// $JUnit-BEGIN$
		suite.addTestSuite(CloudTableClientTests.class);
		suite.addTestSuite(CloudTableObjectTests.class);
		suite.addTestSuite(CloudBlobContainerUsingAccountAndKeyTests.class);
		suite.addTestSuite(CloudBlockBlobUsingAccountAndKeyTests.class);
		suite.addTestSuite(CloudQueueUsingAccountAndKeyTests.class);
		suite.addTestSuite(CloudQueueMessagesUsingAccountAndKeyTests.class);
		suite.addTestSuite(WAZServiceAccountCredentialsUsingHttpTests.class);
		suite.addTestSuite(CloudBlobContainerUsingSASServiceByHttpTests.class);
		suite.addTestSuite(CloudBlockBlobUsingSASServiceByHttpTests.class);
		suite.addTestSuite(CloudQueueUsingWAZServiceByHttpTests.class);
		suite.addTestSuite(CloudQueueMessagesUsingWAZServiceByHttpTests.class);
		suite.addTestSuite(WAZServiceAccountCredentialsUsingHttpsTests.class);
		suite.addTestSuite(CloudBlobContainerUsingSASServiceByHttpsTests.class);
		suite.addTestSuite(CloudBlockBlobUsingSASServiceByHttpsTests.class);
		suite.addTestSuite(CloudQueueUsingWAZServiceByHttpsTests.class);
		suite.addTestSuite(CloudQueueMessagesUsingWAZServiceByHttpsTests.class);
		// $JUnit-END$
		return suite;
	}
}
