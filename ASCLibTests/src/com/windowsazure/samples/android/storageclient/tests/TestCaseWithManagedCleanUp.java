package com.windowsazure.samples.android.storageclient.tests;

import java.util.ArrayList;

import junit.framework.Assert;

public abstract class TestCaseWithManagedCleanUp extends TestCase
{
	ArrayList<Cleaner> cleaners = new ArrayList<Cleaner>();
	
	protected void addCleanUp(Cleaner cleaner)
	{
		cleaners.add(cleaner);
	}
	
	protected void tearDown()
	{
		ArrayList<Exception> exceptionsWhileCleanningUp = new ArrayList<Exception>(); 
		for (Cleaner cleanUp : cleaners)
		{
			try
			{
				cleanUp.run();
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
