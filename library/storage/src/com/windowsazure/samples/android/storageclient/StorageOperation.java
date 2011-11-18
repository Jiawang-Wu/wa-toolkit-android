package com.windowsazure.samples.android.storageclient;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.Date;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.impl.client.DefaultHttpClient;

abstract class StorageOperation<T> {
	protected StorageException exceptionReference;

	protected RequestResult result;

	public abstract T execute() throws Exception;
	
	protected T executeTranslatingExceptions() throws StorageException,
			UnsupportedEncodingException, IOException {
		try {
			result = new RequestResult();
			return this.execute();
		} catch (Exception exception) {
			exception.printStackTrace();
			this.exceptionReference = StorageException.translateException(this.result.httpResponse, exception);
			throw this.exceptionReference;
		}
	}

	protected RequestResult processRequest(HttpRequestBase request)
			throws IOException {
		HttpClient httpClient = new DefaultHttpClient();
		HttpResponse httpResponse = httpClient.execute(request);
		result.startDate = new Date();
		result.statusCode = httpResponse.getStatusLine().getStatusCode();
		result.statusMessage = httpResponse.getStatusLine().getReasonPhrase();
		result.stopDate = new Date();
		result.serviceRequestID = BaseResponse.getRequestId(httpResponse);
		result.eTag = BaseResponse.getEtag(httpResponse);
		result.date = BaseResponse.getDate(httpResponse);
		result.contentMD5 = BaseResponse.getContentMD5(httpResponse);
		result.httpResponse = httpResponse;
		return result;
	}
	
	protected StorageException materializeException(HttpResponse response)
			throws NotImplementedException, UnsupportedEncodingException,
			IOException {
		if (exceptionReference != null)
		{
			return exceptionReference;
		}
		else
		{
			return StorageException.translateException(response, null);
		}
	}
}
