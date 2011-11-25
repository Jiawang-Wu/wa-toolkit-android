package com.windowsazure.samples.android.storageclient.tests;

import junit.framework.Assert;

import android.test.AndroidTestCase;

import com.windowsazure.samples.android.storageclient.CloudTableClient;

public class CloudTableClientBasedTest<T extends CloudClientAccountProvider> extends AndroidTestCase {
	
	protected void setUp() {
		try {
			super.setUp();
			T accountProvider = SuperClassTypeParameterCreator.create(this, 0);
			client = accountProvider.getCloudTableClient();
		} catch (Exception e) {
			e.printStackTrace();
			Assert.fail();
		}
	}

	protected CloudTableClient client;
}
