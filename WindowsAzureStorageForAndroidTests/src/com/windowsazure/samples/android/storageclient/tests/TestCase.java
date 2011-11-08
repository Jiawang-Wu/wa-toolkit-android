package com.windowsazure.samples.android.storageclient.tests;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;

import junit.framework.Assert;

//public abstract class TestCase extends android.test.AndroidTestCase {
public abstract class TestCase extends junit.framework.TestCase {
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

	protected <T> ArrayList<T> toList(Iterable<T> iterable) {
		ArrayList<T> list = new ArrayList<T>();
		for (T element : iterable) {
			list.add(element);
		}
		return list;
	}

	protected void AssertHashMapsAreEquivalent(HashMap leftHashMap,
			HashMap<String, String> rightHashMap) {
		Assert.assertEquals(leftHashMap.size(), rightHashMap.size());
		Assert.assertTrue(leftHashMap.entrySet().containsAll(
				rightHashMap.entrySet()));
		Assert.assertTrue(rightHashMap.entrySet().containsAll(
				leftHashMap.entrySet()));
	}
}
