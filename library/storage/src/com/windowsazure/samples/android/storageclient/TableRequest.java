package com.windowsazure.samples.android.storageclient;

import java.io.IOException;
import java.io.StringWriter;
import java.net.URI;
import java.net.URISyntaxException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

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

	public static HttpGet list(URI endpoint, String prefix) throws IOException, URISyntaxException, StorageException {
		Utility.assertNotNullOrEmpty("prefix", prefix);
		char lastChar = prefix.charAt(prefix.length() - 1);				
		String endPrefix = String.format("%s%s", prefix.substring(0, prefix.length() - 1), (char)(lastChar + 1)); 
		String requestUri = endpoint.toASCIIString() + "/Tables?$filter=TableName%20ge%20'" + prefix + "'%20and%20TableName%20le%20'" + endPrefix + "'";
		return BaseRequest.setURIAndHeaders(new HttpGet(), new URI(requestUri), new UriQueryBuilder());
	}

	public static HttpGet exist(URI endpoint, String tableName) throws IOException, URISyntaxException, StorageException {
		String requestUri = endpoint.toASCIIString() + String.format("/Tables('%s')", tableName);
		return BaseRequest.setURIAndHeaders(new HttpGet(), new URI(requestUri), new UriQueryBuilder());		
	}
	
	public static HttpPost create(URI endpoint, String tableName) throws IOException, URISyntaxException, StorageException {
		String requestUri = endpoint.toASCIIString() + "/Tables";
		HttpPost result = BaseRequest.setURIAndHeaders(new HttpPost(), new URI(requestUri), new UriQueryBuilder());
		String body = buildCreateBody(tableName);
		result.addHeader("Content-Type", "application/atom+xml");
		result.setEntity(new StringEntity(body));
		return result;
	}
	
	public static HttpPost createFromModel(URI endpoint, String tableName, TableProperty<?>[] properties) throws IOException, URISyntaxException, StorageException {
		String requestUri = endpoint.toASCIIString() + "/Tables/" + tableName;
		HttpPost result = BaseRequest.setURIAndHeaders(new HttpPost(), new URI(requestUri), new UriQueryBuilder());
		String body = buildEntity(properties);
		result.addHeader("Content-Type", "application/atom+xml");
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
	
	private static String buildEntity(TableProperty<?>[] properties) {
		StringWriter result = new StringWriter();
		result.append("<?xml version=\"1.0\" encoding=\"utf-8\" standalone=\"yes\"?>\n");
		result.append(String.format("<entry xmlns:d=\"%s\" xmlns:m=\"%s\" xmlns=\"%s\">\n", DATA_SERVICES_NS, METADATA_NS, ATOM_NS));
	    result.append(String.format("<title /> <updated>%s</updated> <author> <name /> </author> <id />\n", getISO8601Time()));
	    result.append("<content type=\"application/xml\"> <m:properties>\n");
	    for (int i = 0; i < properties.length; i++) {
	    	TableProperty<?> property = properties[i];
	    	if (property.getName().equals("PartitionKey") || property.getName().equals("RowKey"))
	    		result.append(String.format("<d:%s>%s</d:%s>\n", property.getName(), property.getRepresentation(), property.getName()));
	    	else
	    		result.append(String.format("<d:%s m:type=\"%s\">%s</d:%s>\n", property.getName(), property.getEdmType().toString(), property.getRepresentation(), property.getName())); 
	    }
	    result.append("</m:properties> </content>\n"); 
	    result.append("</entry>");
		return result.toString();
	}

	private static final String ISO8601 = "yyyy-MM-dd'T'HH:mm:ss.SSSSSSSZ";
	private static final TimeZone GMT_ZONE = TimeZone.getTimeZone("GMT");
	private static String getISO8601Time() {
	    SimpleDateFormat sdf = new SimpleDateFormat(ISO8601, Locale.US);
	    sdf.setTimeZone(GMT_ZONE);
	    return sdf.format (new Date());
	}
	
}
