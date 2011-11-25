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
import org.apache.http.client.methods.HttpEntityEnclosingRequestBase;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.entity.StringEntity;

final class TableRequest {

	public static final String ATOM_NS = "http://www.w3.org/2005/Atom";
	public static final String DATA_SERVICES_NS = "http://schemas.microsoft.com/ado/2007/08/dataservices";
	public static final String METADATA_NS = "http://schemas.microsoft.com/ado/2007/08/dataservices/metadata";

	// table stuff
	
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
		result.setEntity(new StringEntity(buildTableBody(tableName)));
		addRequiredHeaders(result);
		return result;
	}
	
	public static HttpDelete delete(URI endpoint, String tableName) throws IOException, URISyntaxException, StorageException {
		String requestUri = endpoint.toASCIIString() + String.format("/Tables('%s')", tableName);
		HttpDelete result = BaseRequest.setURIAndHeaders(new HttpDelete(), new URI(requestUri), new UriQueryBuilder());
		addRequiredHeaders(result);
		return result;		
	}
	
	// entity stuff
	
	public static HttpGet queryEntity(URI endpoint, String tableName, String filter, int top) throws IOException, URISyntaxException, StorageException {
		String requestUri = endpoint.toASCIIString() + ((filter != null) ? String.format("/%s()?$filter=%s", tableName, Utility.safeEncode(filter)) : String.format("/%s()", tableName));
		requestUri += (top > 0 ? ((filter != null) ? '&' : '?') + String.format("$top=%d", top) : "");  
		return BaseRequest.setURIAndHeaders(new HttpGet(), new URI(requestUri), new UriQueryBuilder());		
	}

	public static HttpPost insertEntity(URI endpoint, String tableName, TableProperty<?>[] properties) throws IOException, URISyntaxException, StorageException {
		String requestUri = endpoint.toASCIIString() + "/" + tableName;
		HttpPost result = BaseRequest.setURIAndHeaders(new HttpPost(), new URI(requestUri), new UriQueryBuilder());
		result.setEntity(new StringEntity(buildEntityBody(properties)));
		addRequiredHeaders(result);
		return result;
	}
	
	public static HttpPut insertOrReplaceEntity(URI endpoint, String tableName, TableProperty<?>[] properties) throws IOException, URISyntaxException, StorageException {
		CloudTableEntity keys = getEntitiyKeys(properties);						
		String requestUri = endpoint.toASCIIString() + String.format("/%s(PartitionKey='%s',RowKey='%s')", tableName, keys.PartitionKey, keys.RowKey);
		HttpPut result = BaseRequest.setURIAndHeaders(new HttpPut(), new URI(requestUri), new UriQueryBuilder());
		result.setEntity(new StringEntity(buildEntityBody(properties, result.getURI().toASCIIString())));
		addRequiredHeaders(result, null, null, true);
		return result;
	}
	
	public static HttpRequestBase insertOrMergeEntity(URI endpoint, String tableName, TableProperty<?>[] properties) throws IOException, URISyntaxException, StorageException {
		CloudTableEntity keys = getEntitiyKeys(properties);						
		String requestUri = endpoint.toASCIIString() + String.format("/%s(PartitionKey='%s',RowKey='%s')", tableName, keys.PartitionKey, keys.RowKey);
		HttpMerge result = BaseRequest.setURIAndHeaders(new HttpMerge(), new URI(requestUri), new UriQueryBuilder());
		result.setEntity(new StringEntity(buildEntityBody(properties, result.getURI().toASCIIString())));
		addRequiredHeaders(result, null, null, true);
		return result;		
	}
	
	public static HttpPut updateEntity(URI endpoint, String tableName, TableProperty<?>[] properties) throws IOException, URISyntaxException, StorageException {
		CloudTableEntity keys = getEntitiyKeys(properties);						
		String requestUri = endpoint.toASCIIString() + String.format("/%s(PartitionKey='%s',RowKey='%s')", tableName, keys.PartitionKey, keys.RowKey);
		HttpPut result = BaseRequest.setURIAndHeaders(new HttpPut(), new URI(requestUri), new UriQueryBuilder());
		result.setEntity(new StringEntity(buildEntityBody(properties)));
		addRequiredHeaders(result, null, "*", false);
		return result;
	}	

	public static HttpRequestBase mergeEntity(URI endpoint, String tableName, TableProperty<?>[] properties) throws IOException, URISyntaxException, StorageException {
		CloudTableEntity keys = getEntitiyKeys(properties);					
		String requestUri = endpoint.toASCIIString() + String.format("/%s(PartitionKey='%s',RowKey='%s')", tableName, keys.PartitionKey, keys.RowKey);
		HttpMerge result = BaseRequest.setURIAndHeaders(new HttpMerge(), new URI(requestUri), new UriQueryBuilder());
		result.setEntity(new StringEntity(buildEntityBody(properties)));
		addRequiredHeaders(result, null, "*", false);
		return result;		
	}

	public static HttpDelete deleteEntity(URI endpoint, String tableName, TableProperty<?>[] properties) throws IOException, URISyntaxException, StorageException {
		CloudTableEntity keys = getEntitiyKeys(properties);				
		return deleteEntity(endpoint, tableName, keys.PartitionKey, keys.RowKey);
	}
	
	public static HttpDelete deleteEntity(URI endpoint, String tableName, String partitionKey, String rowKey) throws IOException, URISyntaxException, StorageException {
		String requestUri = endpoint.toASCIIString() + String.format("/%s(PartitionKey='%s',RowKey='%s')", tableName, partitionKey, rowKey);
		HttpDelete result = BaseRequest.setURIAndHeaders(new HttpDelete(), new URI(requestUri), new UriQueryBuilder());
		addRequiredHeaders(result, "0", "*", false);
		return result;				
	}

	// help stuff
	
	private static void addRequiredHeaders(HttpRequestBase request) {
		addRequiredHeaders(request, null, null, false);
	}
	
	private static void addRequiredHeaders(HttpRequestBase request, String contentLength, String ifMatch, boolean replaceVersion) {		
		request.addHeader("Content-Type", "application/atom+xml");
		if (contentLength != null) request.addHeader("Content-Length", contentLength);
		if (ifMatch != null) request.addHeader("If-Match", ifMatch);
		if (replaceVersion) {
			request.removeHeaders("x-ms-version");
			request.addHeader("x-ms-version", "2011-08-18");
		}
	}
	
	private static String buildTableBody(String tableName) {
		StringWriter result = new StringWriter();
		result.append("<?xml version=\"1.0\" encoding=\"utf-8\"?>\n");
		result.append(String.format("<entry xmlns:d=\"%s\" xmlns:m=\"%s\" xmlns=\"%s\">\n", DATA_SERVICES_NS, METADATA_NS, ATOM_NS));
	    result.append(String.format("<title /> <updated>%s</updated> <author> <name /> </author> <id />\n", getISO8601Time()));
	    result.append(String.format("<content type=\"application/xml\"> <m:properties> <d:TableName>%s</d:TableName> </m:properties> </content>\n", tableName)); 
	    result.append("</entry>");
		return result.toString();
	}
	
	private static String buildEntityBody(TableProperty<?>[] properties) {
		return buildEntityBody(properties, null);
	}
	
	private static String buildEntityBody(TableProperty<?>[] properties, String id) {
		StringWriter result = new StringWriter();
		result.append("<?xml version=\"1.0\" encoding=\"utf-8\" standalone=\"yes\"?>\n");
		result.append(String.format("<entry xmlns:d=\"%s\" xmlns:m=\"%s\" xmlns=\"%s\">\n", DATA_SERVICES_NS, METADATA_NS, ATOM_NS));
		if (id != null)
			result.append(String.format("<title /> <updated>%s</updated> <author> <name /> </author> <id>%s</id>\n", getXmlTimeWithTZ(), id));
		else
			result.append(String.format("<title /> <updated>%s</updated> <author> <name /> </author> <id />\n", getXmlTimeWithTZ()));
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
	
	private static CloudTableEntity getEntitiyKeys(TableProperty<?>[] properties) {
		CloudTableEntity result = new CloudTableEntityKey();
		for (int i = 0; i < properties.length; i++) {
			if (properties[i].getName().equals("PartitionKey")) {
				result.PartitionKey = properties[i].getRepresentation();
				continue;
			} else if (properties[i].getName().equals("RowKey")) {
				result.RowKey = properties[i].getRepresentation();
			}
		}
		Utility.assertNotNull("PartitionKey property not found", result.PartitionKey);
		Utility.assertNotNull("RowKey property not found", result.RowKey);
		return result;
	}

	private static final String ISO8601 = "yyyy-MM-dd'T'HH:mm:ss.SSSSSSSZ";
	private static final TimeZone GMT_ZONE = TimeZone.getTimeZone("GMT");
	private static String getISO8601Time() {
	    SimpleDateFormat sdf = new SimpleDateFormat(ISO8601, Locale.US);
	    sdf.setTimeZone(GMT_ZONE);
	    return sdf.format (new Date());
	}
	
	private static final String XML_FORMAT_TZ = "yyyy-MM-dd'T'HH:mm:ssZ";
	private static String getXmlTimeWithTZ() {
		SimpleDateFormat sdf = new SimpleDateFormat(XML_FORMAT_TZ); 
		sdf.setTimeZone(GMT_ZONE);
        return sdf.format(new Date());
	}
	
	private static class CloudTableEntityKey extends CloudTableEntity { }

	private static class HttpMerge extends HttpEntityEnclosingRequestBase {

		public HttpMerge() { }

		@Override
		public String getMethod() {
			return "MERGE";
		}
		
	}	
	
}



