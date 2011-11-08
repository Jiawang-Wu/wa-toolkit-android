package com.windowsazure.samples.android.storageclient.tests;

import junit.framework.Test;
import junit.framework.TestSuite;

public class MiddleRunTests {

	public static Test suite() {
		TestSuite suite = new TestSuite(MiddleRunTests.class.getName());
		// $JUnit-BEGIN$
		suite.addTestSuite(CloudBlobContainerUsingAccountAndKeyTests.class);
		suite.addTestSuite(CloudBlobContainerUsingSASServiceByHttpsTests.class);
		suite.addTestSuite(CloudBlockBlobUsingAccountAndKeyTests.class);
		suite.addTestSuite(CloudBlockBlobUsingSASServiceByHttpsTests.class);
		suite.addTestSuite(WAZServiceAccountCredentialsUsingHttpsTests.class);
		// $JUnit-END$
		return suite;
	}

}
