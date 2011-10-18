package com.windowsazure.samples.android.storageclient.tests;

import junit.framework.Assert;
import android.test.AndroidTestCase;

public class TestCase extends AndroidTestCase {
	 public <T extends Exception> void assertThrows(ExpectedExceptionRunnable runnable, Class<T> exceptionClass) throws Exception
	 {
		 try
		 {
			 runnable.run();
			 Assert.fail();
		 }
		 catch (Exception exception)
		 {
			   if (exceptionClass.isInstance(exception))
			   {
		           return;
		       }
			   else 
			   {
		          throw exception;
		       }
		 }
	 }
}
