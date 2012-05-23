package com.windowsazure.samples.android.storageclient;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.Date;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpRequestBase;

abstract class StorageOperation<T> {
	protected StorageException exceptionReference;

	protected RequestResult result;

	public RequestResult getResult() {
		return result;
	}

	public abstract T execute() throws Exception;

	protected T executeTranslatingExceptions() throws StorageException,
			UnsupportedEncodingException, IOException {
		try {
			result = new RequestResult();
			return this.execute();
		} catch (StorageException exception) {
			throw exception;
		} catch (Exception exception) {
			exception.printStackTrace();
			this.exceptionReference = StorageException.translateException(this.result.httpResponse, exception);
			throw this.exceptionReference;
		}
	}

	public RequestResult processRequest(HttpRequestBase request)
			throws IOException {
		HttpClient httpClient = Utility.getDefaultHttpClient();
		HttpResponse httpResponse = httpClient.execute(request);
		//If you're not sure why you're getting a failed response, try uncomenting this to see the body
		//ByteArrayOutputStream outStream = new ByteArrayOutputStream();
		//httpResponse.getEntity().writeTo(outStream);
		//byte [] responseBodyBytes = outstream.toByteArray();
		//String responseBodyString = new String(responseBodyBytes);
		//Log.i("StorageOperation-processRequest", responseBodyString);
		
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
		if (exceptionReference != null) {
			return exceptionReference;
		}
		else {
			return StorageException.translateException(response, null);
		}
	}
}
