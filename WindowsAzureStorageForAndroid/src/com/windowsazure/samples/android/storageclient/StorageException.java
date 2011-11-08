package com.windowsazure.samples.android.storageclient;

import java.io.IOException;
import java.io.UnsupportedEncodingException;

import org.apache.http.HttpResponse;

public final class StorageException extends Exception {

	public static final long serialVersionUID = 0x6ea4e362e7d2d5f0L;

	protected static StorageExtendedErrorInformation getErrorDetailsFromRequest(
			HttpResponse response) throws NotImplementedException,
			UnsupportedEncodingException, IOException {
		if (response == null)
			return null;
		StorageErrorResponse storageerrorresponse = new StorageErrorResponse(
				response.getEntity().getContent());
		return storageerrorresponse.getExtendedErrorInformation();
	}

	public static StorageException translateException(HttpResponse response,
			Exception exception) throws NotImplementedException,
			UnsupportedEncodingException, IOException {
		if (response == null)
			return new StorageException(
					"Client error",
					"A Client side exception occurred, please check the inner exception for more details: "
							+ exception.getMessage(), 306, null, exception);
		StorageExtendedErrorInformation storageextendederrorinformation = getErrorDetailsFromRequest(response);
		StorageException storageexception = null;
		String s = "";
		int i = 0;
		i = response.getStatusLine().getStatusCode();
		s = response.getStatusLine().getReasonPhrase();
		if (s == null)
			s = "";
		if (storageextendederrorinformation != null) {
			storageexception = new StorageException(
					storageextendederrorinformation.errorCode, s, i,
					storageextendederrorinformation, exception);
			if (storageexception != null)
				return storageexception;
		}
		storageexception = translateFromHttpStatus(i, s, null, exception);
		if (storageexception != null)
			return storageexception;
		else
			return new StorageException(
					StorageErrorCode.SERVICE_INTERNAL_ERROR.toString(),
					"The server encountered an unknown failure: ".concat(s),
					500, null, exception);
	}

	protected static StorageException translateFromHttpStatus(int i, String s,
			StorageExtendedErrorInformation storageextendederrorinformation,
			Exception exception) {
		switch (i) {
		case 403:
			return new StorageException(
					StorageErrorCode.ACCESS_DENIED.toString(), s, i,
					storageextendederrorinformation, exception);

		case 404:
		case 410:
			return new StorageException(
					StorageErrorCode.RESOURCE_NOT_FOUND.toString(), s, i,
					storageextendederrorinformation, exception);

		case 400:
			return new StorageException(
					StorageErrorCode.BAD_REQUEST.toString(), s, i,
					storageextendederrorinformation, exception);

		case 304:
		case 412:
			return new StorageException(
					StorageErrorCode.CONDITION_FAILED.toString(), s, i,
					storageextendederrorinformation, exception);

		case 409:
			return new StorageException(
					StorageErrorCode.RESOURCE_ALREADY_EXISTS.toString(), s, i,
					storageextendederrorinformation, exception);

		case 504:
			return new StorageException(
					StorageErrorCode.SERVICE_TIMEOUT.toString(), s, i,
					storageextendederrorinformation, exception);

		case 416:
			return new StorageException(
					StorageErrorCode.BAD_REQUEST.toString(), s, i,
					storageextendederrorinformation, exception);

		case 500:
			return new StorageException(
					StorageErrorCode.SERVICE_INTERNAL_ERROR.toString(), s, i,
					storageextendederrorinformation, exception);

		case 501:
			return new StorageException(
					StorageErrorCode.NOT_IMPLEMENTED.toString(), s, i,
					storageextendederrorinformation, exception);

		case 502:
			return new StorageException(
					StorageErrorCode.BAD_GATEWAY.toString(), s, i,
					storageextendederrorinformation, exception);

		case 505:
			return new StorageException(
					StorageErrorCode.HTTP_VERSION_NOT_SUPPORTED.toString(), s,
					i, storageextendederrorinformation, exception);
		}
		return null;
	}

	public final String errorCode;
	public final StorageExtendedErrorInformation extendedErrorInformation;
	public final int httpStatusCode;
	public StorageException(String s, String detailMessage, int i,
			StorageExtendedErrorInformation storageextendederrorinformation,
			Exception exception) {
		super(detailMessage, exception);
		errorCode = s;
		httpStatusCode = i;
		extendedErrorInformation = storageextendederrorinformation;
	}
}
