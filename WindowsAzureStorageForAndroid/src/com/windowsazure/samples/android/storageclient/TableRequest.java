package com.windowsazure.samples.android.storageclient;

import java.io.IOException;
import java.io.StringWriter;
import java.net.URI;
import java.net.URISyntaxException;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;

final class TableRequest {

	public static final String ATOM_NS = "http://www.w3.org/2005/Atom";
	public static final String DATA_SERVICES_NS = "http://schemas.microsoft.com/ado/2007/08/dataservices";
	public static final String METADATA_NS = "http://schemas.microsoft.com/ado/2007/08/dataservices/metadata";

	public static HttpGet list(URI endpoint) throws IOException, URISyntaxException, StorageException {
		String requestUri = endpoint.toASCIIString() + "/Tables";
		return BaseRequest.setURIAndHeaders(new HttpGet(), new URI(requestUri), new UriQueryBuilder());
	}
	
	public static HttpGet find(URI endpoint, String tableName) throws IOException, URISyntaxException, StorageException {
		String requestUri = endpoint.toASCIIString() + String.format("/Tables('%s')", tableName);
		return BaseRequest.setURIAndHeaders(new HttpGet(), new URI(requestUri), new UriQueryBuilder());		
	}
	
	public static HttpPost create(URI endpoint, String tableName) throws IOException, URISyntaxException, StorageException {
		String requestUri = endpoint.toASCIIString() + "/Tables";
		HttpPost result = BaseRequest.setURIAndHeaders(new HttpPost(), new URI(requestUri), new UriQueryBuilder());
		String body = buildCreateBody(tableName);
		result.addHeader("Content-Type", "application/atom+xml");
		//result.addHeader("Content-Length", new Integer(body.length()).toString());
		result.setEntity(new StringEntity(body));
		return result;
	}
	
	public static HttpDelete delete(URI endpoint, String tableName) throws IOException, URISyntaxException, StorageException {
		String requestUri = endpoint.toASCIIString() + String.format("/Tables('%s')", tableName);
		HttpDelete result = BaseRequest.setURIAndHeaders(new HttpDelete(), new URI(requestUri), new UriQueryBuilder());
		result.addHeader("Content-Type", "application/atom+xml");
		return result;		
	}

	private static String buildCreateBody(String tableName) {
		StringWriter result = new StringWriter();
		result.append("<?xml version=\"1.0\" encoding=\"utf-8\"?>\n");
		result.append(String.format("<entry xmlns:d=\"%s\" xmlns:m=\"%s\" xmlns=\"%s\">\n", DATA_SERVICES_NS, METADATA_NS, ATOM_NS));
	    result.append(String.format("<title /> <updated>%s</updated> <author> <name /> </author> <id />\n", getISO8601Time()));
	    result.append(String.format("<content type=\"application/xml\"> <m:properties> <d:TableName>%s</d:TableName> </m:properties> </content>\n", tableName)); 
	    result.append("</entry>");
		return result.toString();
	}

	private final static String ISO8601 = "yyyy-MM-dd'T'HH:mm:ss.SSSSSSSZ";
	private static String getISO8601Time() {
	    SimpleDateFormat sdf = new SimpleDateFormat(ISO8601);
	    return sdf.format (new Date());
	}
	
}
