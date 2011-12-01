package com.windowsazure.samples.android.storageclient;

import java.io.IOException;
import java.io.UnsupportedEncodingException;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;

public final class StorageException extends Exception {

	public static final long serialVersionUID = 0x6ea4e362e7d2d5f0L;

	public static StorageException generateNewUnexpectedStorageException(Exception exception) {
		StorageException storageexception = new StorageException(
				StorageErrorCode.NONE.toString(),
				"Unexpected internal storage client error.", 306, null, null);
		storageexception.initCause(exception);
		return storageexception;
	}

	protected static StorageExtendedErrorInformation getErrorDetailsFromRequest(
			HttpResponse response) throws UnsupportedEncodingException, IOException {
		if (response == null)
			return null;
		HttpEntity responseEntity = response.getEntity();
		if (responseEntity != null && responseEntity.getContentLength() > 0) {
			StorageErrorResponse storageerrorresponse = new StorageErrorResponse(
					responseEntity.getContent());
			return storageerrorresponse.getExtendedErrorInformation();
		}
		else {
			StorageExtendedErrorInformation errorInformation = new StorageExtendedErrorInformation();
			errorInformation.errorCode = "No further error information";
			errorInformation.errorMessage = "The server response was " + response.getStatusLine().getReasonPhrase();
			return errorInformation;
		}
	}

	public static StorageException translateException(HttpResponse response,
			Exception exception) throws UnsupportedEncodingException, IOException {
		if (response == null)
			return new StorageException(
					"Client error",
					"A Client side exception occurred, please check the inner exception for more details: "
							+ exception.getMessage(), 306, null, exception);
		StorageExtendedErrorInformation extendedErrorInformation = getErrorDetailsFromRequest(response);
		StorageException translatedException = null;
		String reasonPhrase = "";
		int statusCode = 0;
		statusCode = response.getStatusLine().getStatusCode();
		reasonPhrase = response.getStatusLine().getReasonPhrase();
		if (reasonPhrase == null)
			reasonPhrase = "";
		if (extendedErrorInformation != null) {
			translatedException = new StorageException(
					extendedErrorInformation.errorCode, reasonPhrase, statusCode,
					extendedErrorInformation, exception);
			if (translatedException != null)
				return translatedException;
		}
		translatedException = translateFromHttpStatus(statusCode, reasonPhrase, null, exception);
		if (translatedException != null)
			return translatedException;
		else
			return new StorageException(
					StorageErrorCode.SERVICE_INTERNAL_ERROR.toString(),
					"The server encountered an unknown failure: ".concat(reasonPhrase),
					500, null, exception);
	}

	protected static StorageException translateFromHttpStatus(int statusCode, String reasonPhrase,
			StorageExtendedErrorInformation extenderErrorInformation,
			Exception exception) {
		switch (statusCode) {
		case 403:
			return new StorageException(
					StorageErrorCode.ACCESS_DENIED.toString(), reasonPhrase, statusCode,
					extenderErrorInformation, exception);

		case 404:
		case 410:
			return new StorageException(
					StorageErrorCode.RESOURCE_NOT_FOUND.toString(), reasonPhrase, statusCode,
					extenderErrorInformation, exception);

		case 400:
			return new StorageException(
					StorageErrorCode.BAD_REQUEST.toString(), reasonPhrase, statusCode,
					extenderErrorInformation, exception);

		case 304:
		case 412:
			return new StorageException(
					StorageErrorCode.CONDITION_FAILED.toString(), reasonPhrase, statusCode,
					extenderErrorInformation, exception);

		case 409:
			return new StorageException(
					StorageErrorCode.RESOURCE_ALREADY_EXISTS.toString(), reasonPhrase, statusCode,
					extenderErrorInformation, exception);

		case 504:
			return new StorageException(
					StorageErrorCode.SERVICE_TIMEOUT.toString(), reasonPhrase, statusCode,
					extenderErrorInformation, exception);

		case 416:
			return new StorageException(
					StorageErrorCode.BAD_REQUEST.toString(), reasonPhrase, statusCode,
					extenderErrorInformation, exception);

		case 500:
			return new StorageException(
					StorageErrorCode.SERVICE_INTERNAL_ERROR.toString(), reasonPhrase, statusCode,
					extenderErrorInformation, exception);

		case 501:
			return new StorageException(
					StorageErrorCode.NOT_IMPLEMENTED.toString(), reasonPhrase, statusCode,
					extenderErrorInformation, exception);

		case 502:
			return new StorageException(
					StorageErrorCode.BAD_GATEWAY.toString(), reasonPhrase, statusCode,
					extenderErrorInformation, exception);

		case 505:
			return new StorageException(
					StorageErrorCode.HTTP_VERSION_NOT_SUPPORTED.toString(), reasonPhrase,
					statusCode, extenderErrorInformation, exception);
		}
		return null;
	}

	public final String m_ErrorCode;
	public final StorageExtendedErrorInformation m_ExtendedErrorInformation;
	public final int m_HttpStatusCode;
	public StorageException(String errrorCode, String detailMessage, int statusCode,
			StorageExtendedErrorInformation extendedErrorInformation,
			Exception exception) {
		super(detailMessage, exception);
		m_ErrorCode = errrorCode;
		m_HttpStatusCode = statusCode;
		m_ExtendedErrorInformation = extendedErrorInformation;
	}
}
