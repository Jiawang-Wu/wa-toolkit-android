package com.windowsazure.samples.android.storageclient;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.Date;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.impl.client.DefaultHttpClient;
import java.security.KeyStore;

import org.apache.http.HttpVersion;
import org.apache.http.conn.ClientConnectionManager;
import org.apache.http.conn.scheme.PlainSocketFactory;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.scheme.SchemeRegistry;
import org.apache.http.conn.ssl.SSLSocketFactory;
import org.apache.http.impl.conn.tsccm.ThreadSafeClientConnManager;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpParams;
import org.apache.http.params.HttpProtocolParams;
import org.apache.http.protocol.HTTP;

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
		} catch (Exception exception) {
			exception.printStackTrace();
			this.exceptionReference = StorageException.translateException(this.result.httpResponse, exception);
			throw this.exceptionReference;
		}
	}

	public RequestResult processRequest(HttpRequestBase request)
			throws IOException {
		HttpClient httpClient = getFullTrustedHttpClient();
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
	
    public static HttpClient getFullTrustedHttpClient() {
        try {
            KeyStore trustStore = KeyStore.getInstance(KeyStore.getDefaultType());
            trustStore.load(null, null);

            SSLSocketFactory sf = new EasySSLSocketFactory(trustStore);
            sf.setHostnameVerifier(SSLSocketFactory.ALLOW_ALL_HOSTNAME_VERIFIER);

            HttpParams params = new BasicHttpParams();
            HttpProtocolParams.setVersion(params, HttpVersion.HTTP_1_1);
            HttpProtocolParams.setContentCharset(params, HTTP.UTF_8);

            SchemeRegistry registry = new SchemeRegistry();
            registry.register(new Scheme("http", PlainSocketFactory.getSocketFactory(), 80));
            registry.register(new Scheme("https", sf, 443));

            ClientConnectionManager ccm = new ThreadSafeClientConnManager(params, registry);

            return new DefaultHttpClient(ccm, params);
        } catch (Exception e) {
            return new DefaultHttpClient();
        }
    }
}
