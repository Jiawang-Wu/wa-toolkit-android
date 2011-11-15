package com.windowsazure.samples.android.storageclient;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.Date;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.impl.client.DefaultHttpClient;

final class ExecutionEngine {
	protected static Object execute(Object firstArgument, Object secondArgument,
			StorageOperation storageOperation) throws StorageException,
			UnsupportedEncodingException, IOException {
		storageOperation.initialize();
		try {
			return storageOperation.execute(firstArgument, secondArgument);
		} catch (Exception exception) {
			exception.printStackTrace();
			storageOperation.exceptionReference = StorageException
					.translateException(storageOperation.result.httpResponse, exception);
			throw storageOperation.exceptionReference;
		}
	}

	protected static RequestResult processRequest(HttpRequestBase request)
			throws IOException {
		HttpClient httpClient = new DefaultHttpClient();
		HttpResponse httpResponse = httpClient.execute(request);
		RequestResult requestResult = new RequestResult();
		requestResult.startDate = new Date();
		requestResult.statusCode = httpResponse.getStatusLine().getStatusCode();
		requestResult.statusMessage = httpResponse.getStatusLine()
				.getReasonPhrase();
		requestResult.stopDate = new Date();
		requestResult.serviceRequestID = BaseResponse.getRequestId(httpResponse);
		requestResult.eTag = BaseResponse.getEtag(httpResponse);
		requestResult.date = BaseResponse.getDate(httpResponse);
		requestResult.contentMD5 = BaseResponse.getContentMD5(httpResponse);
		requestResult.httpResponse = httpResponse;
		return requestResult;
	}
}
