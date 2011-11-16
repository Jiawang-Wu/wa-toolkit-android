package com.windowsazure.samples.android.storageclient.tests;

import junit.framework.Test;
import junit.framework.TestSuite;

public class AllTests {

	public static Test suite() {
		TestSuite suite = new TestSuite(AllTests.class.getName());
		// $JUnit-BEGIN$
		suite.addTestSuite(CloudBlobContainerUsingAccountAndKeyTests.class);
		suite.addTestSuite(CloudBlockBlobUsingAccountAndKeyTests.class);
		suite.addTestSuite(CloudBlobContainerUsingSASServiceByHttpTests.class);
		suite.addTestSuite(CloudBlockBlobUsingSASServiceByHttpTests.class);
		suite.addTestSuite(WAZServiceAccountCredentialsUsingHttpTests.class);
		suite.addTestSuite(CloudBlobContainerUsingSASServiceByHttpsTests.class);
		suite.addTestSuite(CloudBlockBlobUsingSASServiceByHttpsTests.class);
		suite.addTestSuite(WAZServiceAccountCredentialsUsingHttpsTests.class);
		// $JUnit-END$
		return suite;
	}
}
