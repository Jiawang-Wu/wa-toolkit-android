package com.windowsazure.samples.android.storageclient.tests;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;
import java.util.concurrent.Callable;

import junit.framework.Assert;

public abstract class TestCase extends android.test.AndroidTestCase {
	protected <T extends Exception> void assertThrows(
			RunnableWithExpectedException runnable, Class<T> exceptionClass)
			throws Exception {
		try {
			runnable.run();
			Assert.fail();
		} catch (Exception exception) {
			if (exceptionClass.isInstance(exception)) {
				return;
			} else {
				throw exception;
			}
		}
	}

	protected <T> void AssertHaveSameElements(Collection<T> firstCollection,
			Collection<T> secondCollection) {
		Assert.assertEquals(firstCollection.size(), secondCollection.size());
		Assert.assertFalse(firstCollection.retainAll(secondCollection));
		Assert.assertFalse(secondCollection.retainAll(firstCollection));
	}

	protected void assertEventuallyTrue(Callable<Boolean> callable, int timeout) throws Exception {
		long start = System.currentTimeMillis();
		long current;
		do
		{
			if (callable.call())
			{
				return;
			}
			current = System.currentTimeMillis();
		} while (start + timeout < current);
		Assert.assertTrue(callable.call());
	}
	
	protected <T> ArrayList<T> toList(Iterable<T> iterable) {
		ArrayList<T> list = new ArrayList<T>();
		for (T element : iterable) {
			list.add(element);
		}
		return list;
	}

	protected void AssertHashMapsAreEquivalent(Map<String, String> leftHashMap,
			Map<String, String> rightHashMap) {
		Assert.assertEquals(leftHashMap.size(), rightHashMap.size());
		Assert.assertTrue(leftHashMap.entrySet().containsAll(
				rightHashMap.entrySet()));
		Assert.assertTrue(rightHashMap.entrySet().containsAll(
				leftHashMap.entrySet()));
	}
}
