package com.windowsazure.samples.android.storageclient.tests;

import junit.framework.Test;
import junit.framework.TestSuite;

public class WAZServiceByHttpTests {

	public static Test suite() {
		TestSuite suite = new TestSuite(WAZServiceByHttpTests.class.getName());
		//$JUnit-BEGIN$
		suite.addTestSuite(CloudBlobContainerUsingSASServiceByHttpTests.class);
		suite.addTestSuite(CloudBlockBlobUsingSASServiceByHttpTests.class);
		suite.addTestSuite(CloudQueueMessagesUsingWAZServiceByHttpTests.class);
		suite.addTestSuite(CloudQueueUsingWAZServiceByHttpTests.class);
		suite.addTestSuite(WAZServiceAccountCredentialsUsingHttpTests.class);
		//$JUnit-END$
		return suite;
	}

}
