package com.windowsazure.samples.android.storageclient.tests;

import junit.framework.Test;
import junit.framework.TestSuite;

public class FastRunTests {

	public static Test suite() {
		TestSuite suite = new TestSuite(FastRunTests.class.getName());
		// $JUnit-BEGIN$
		suite.addTestSuite(CloudBlobContainerUsingAccountAndKeyTests.class);
		suite.addTestSuite(CloudBlockBlobUsingAccountAndKeyTests.class);
		// $JUnit-END$
		return suite;
	}

}
