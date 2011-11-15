package com.windowsazure.samples.android.storageclient;

import java.io.IOException;
import java.io.UnsupportedEncodingException;

import org.apache.http.HttpResponse;

abstract class StorageOperation {
	protected StorageException exceptionReference;

	protected RequestResult result;

	public abstract Object execute(Object firstArgument, Object secondArgument)
			throws Exception;
	protected void initialize() {
		result = new RequestResult();
	}
	
	protected StorageException materializeException(HttpResponse response)
			throws NotImplementedException, UnsupportedEncodingException,
			IOException {
		if (exceptionReference != null)
			return exceptionReference;
		else
			return StorageException.translateException(response, null);
	}
}
