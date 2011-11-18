package com.windowsazure.samples.android.storageclient.tests;

import junit.framework.Test;
import junit.framework.TestSuite;

public class MiddleRunTests {

	public static Test suite() {
		TestSuite suite = new TestSuite(MiddleRunTests.class.getName());
		// $JUnit-BEGIN$
		suite.addTestSuite(CloudQueueTests.class);
		suite.addTestSuite(CloudQueueMessagesTests.class);
		suite.addTestSuite(CloudBlobContainerUsingAccountAndKeyTests.class);
		suite.addTestSuite(CloudBlockBlobUsingAccountAndKeyTests.class);
		suite.addTestSuite(CloudBlobContainerUsingSASServiceByHttpTests.class);
		suite.addTestSuite(CloudBlockBlobUsingSASServiceByHttpTests.class);
		suite.addTestSuite(WAZServiceAccountCredentialsUsingHttpTests.class);
		// $JUnit-END$
		return suite;
	}

}
