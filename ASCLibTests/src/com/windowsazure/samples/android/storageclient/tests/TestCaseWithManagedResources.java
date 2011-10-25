package com.windowsazure.samples.android.storageclient.tests;

import java.util.ArrayList;

import junit.framework.Assert;

public abstract class TestCaseWithManagedResources extends TestCase
{
	ArrayList<ResourceCleaner> cleaners = new ArrayList<ResourceCleaner>();
	
	protected void addCleanUp(ResourceCleaner cleaner)
	{
		cleaners.add(cleaner);
	}
	
	protected void tearDown()
	{
		ArrayList<Exception> exceptionsWhileCleanningUp = new ArrayList<Exception>(); 
		for (ResourceCleaner cleanUp : cleaners)
		{
			try
			{
				cleanUp.clean();
			}
			catch (Exception exception)
			{
				exceptionsWhileCleanningUp.add(exception);
			}
		}
		if (!exceptionsWhileCleanningUp.isEmpty())
		{
			Assert.fail("Exceptions while cleanning up the tests");
		}
	}
}
