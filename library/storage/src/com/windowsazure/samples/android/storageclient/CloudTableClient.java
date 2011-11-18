package com.windowsazure.samples.android.storageclient;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.Field;
import java.net.URI;
import java.net.URISyntaxException;
import java.sql.Timestamp;
import java.util.ArrayList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathFactory;

import org.apache.http.HttpStatus;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;

import com.windowsazure.samples.android.storageclient.StorageCredentials;
import com.windowsazure.samples.android.storageclient.Utility;

public class CloudTableClient {

	private URI m_Endpoint;
	private StorageCredentials m_Credentials;

	public CloudTableClient(URI baseUri, StorageCredentials credentials) {
		Utility.assertNotNull("baseUri", baseUri);
		Utility.assertNotNull("credentials", credentials);

		if (!baseUri.isAbsolute()) {
			throw new IllegalArgumentException(String.format("Address '%s' is not an absolute address. Relative addresses are not permitted in here.", baseUri));
		}

		m_Endpoint = baseUri;
		m_Credentials = credentials;
	}

	public Timestamp getMinSupportedDateTime() {
		return null;
	}

	public URI getEndpoint() {
		return m_Endpoint;
	}

	public StorageCredentials getCredentials() {
		return m_Credentials;
	}

	public boolean doesTableExist(String tableName) throws UnsupportedEncodingException, StorageException, IOException {
		final String thatTableName = tableName;
		StorageOperation<Boolean>  storageOperation = new StorageOperation<Boolean>() {
			public Boolean execute() throws Exception {				
				HttpGet request = TableRequest.exist(m_Endpoint, thatTableName);						
				m_Credentials.signTableRequest(request);
				this.processRequest(request);
				return result.statusCode == HttpStatus.SC_OK;
			}
		};
        return storageOperation.executeTranslatingExceptions();
	}

	public Iterable<String> listTables() 
			throws UnsupportedEncodingException, StorageException, IOException {
		StorageOperation<Iterable<String>>  storageOperation = new StorageOperation<Iterable<String>>() {
			public Iterable<String> execute() throws Exception {
				ArrayList<String> tables = new ArrayList<String>();
				HttpGet request = TableRequest.list(m_Endpoint);						
				m_Credentials.signTableRequest(request);
				this.processRequest(request);
				if (result.statusCode != HttpStatus.SC_OK) {
					throw new StorageInnerException("Couldn't get table's list");
				} else {
					DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
					DocumentBuilder builder = factory.newDocumentBuilder();
					Document document = builder.parse(result.httpResponse.getEntity().getContent());
					
					XPath xpath = XPathFactory.newInstance().newXPath();
					String expression = "/feed/entry/content/properties/TableName";
					NodeList tableNames = (NodeList) xpath.evaluate(expression, document, XPathConstants.NODESET);
					for (int i = 0; i < tableNames.getLength(); i++) {						
						tables.add(tableNames.item(i).getTextContent());
					}
				}
				return tables;
			}
		};
        return storageOperation.executeTranslatingExceptions();
	}

	public Iterable<String> listTables(String prefix)
			throws UnsupportedEncodingException, StorageException, IOException {
		final String thatPrefix = prefix;
		StorageOperation<Iterable<String>>  storageOperation = new StorageOperation<Iterable<String>>() {
			public Iterable<String> execute() throws Exception {
				ArrayList<String> tables = new ArrayList<String>();
				HttpGet request = TableRequest.list(m_Endpoint, thatPrefix);						
				m_Credentials.signTableRequest(request);
				this.processRequest(request);
				if (result.statusCode != HttpStatus.SC_OK) {
					throw new StorageInnerException("Couldn't get table's list");
				} else {
					DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
					DocumentBuilder builder = factory.newDocumentBuilder();
					Document document = builder.parse(result.httpResponse.getEntity().getContent());
					
					XPath xpath = XPathFactory.newInstance().newXPath();
					String expression = "/feed/entry/content/properties/TableName";
					NodeList tableNames = (NodeList) xpath.evaluate(expression, document, XPathConstants.NODESET);
					for (int i = 0; i < tableNames.getLength(); i++) {						
						tables.add(tableNames.item(i).getTextContent());
					}
				}
				return tables;
			}
		};
        return storageOperation.executeTranslatingExceptions();
	}
	
	public void createTable(String tableName) 
			throws UnsupportedEncodingException, StorageException, IOException {
		final String thatTableName = tableName;
		StorageOperation<Void> storageOperation = new StorageOperation<Void>() {
			public Void execute() throws Exception {
				HttpPost request = TableRequest.create(m_Endpoint, thatTableName);
				m_Credentials.signTableRequest(request);
				this.processRequest(request);
				if (result.statusCode != HttpStatus.SC_CREATED) {
					throw new StorageInnerException(String.format("Couldn't create table '%s'", thatTableName));
				}
				return null;
			}
		};
		storageOperation.executeTranslatingExceptions();
	}

	public boolean createTableIfNotExist(String tableName) throws UnsupportedEncodingException, StorageException, IOException {
		if (!doesTableExist(tableName)) { 
			try {
				createTable(tableName);
				return true;
			} catch (Exception e) {
				return false;
			}
		} else {
			return false;
		}
	}

	public void deleteTable(String tableName) throws UnsupportedEncodingException, StorageException, IOException {
		final String thatTableName = tableName;
		StorageOperation<Void> storageOperation = new StorageOperation<Void>() {
			public Void execute() throws Exception {
				HttpDelete request = TableRequest.delete(m_Endpoint, thatTableName);
				m_Credentials.signTableRequest(request);
				this.processRequest(request);
				if (result.statusCode != HttpStatus.SC_NO_CONTENT) {
					throw new StorageInnerException(String.format("Couldn't delete table '%s'", thatTableName));
				}
				return null;
			}
		};
		storageOperation.executeTranslatingExceptions();
	}

	public boolean deleteTableIfExist(String tableName) throws UnsupportedEncodingException, StorageException, IOException {
		if (doesTableExist(tableName)) {
			try {
				deleteTable(tableName);
				return true;
			} catch (Exception e) {
				return false;
			}
		} else {
			return false;
		}
	}

	public static void CreateTableFromModel(Class<?> type, final String baseAddress, final StorageCredentials credentials) 
			throws UnsupportedEncodingException, StorageException, IOException, URISyntaxException {

		Field[] fields = type.getFields();
		final TableProperty<?>[] properties = new TableProperty<?>[fields.length];
		for (int i = 0; i < fields.length; i++) {
			properties[i] = TableProperty.newProperty(fields[i].getName(), fields[i].getType());
		}
		
		final String tableName = type.getSimpleName();		
		CloudTableClient client = new CloudTableClient(new URI(baseAddress), credentials);
		client.createTable(tableName);		

		StorageOperation<Void> storageOperation = new StorageOperation<Void>() {
			public Void execute() throws Exception {
				HttpPost request = TableRequest.insertEntity(new URI(baseAddress), tableName, properties);
				credentials.signTableRequest(request);
				this.processRequest(request);
				if (result.statusCode != HttpStatus.SC_CREATED) {
					throw new StorageInnerException(String.format("Couldn't create table '%s'", tableName));
				} else {
					HttpDelete deleteRequest = TableRequest.deleteEntity(new URI(baseAddress), tableName, properties);
					credentials.signTableRequest(deleteRequest);
					this.processRequest(deleteRequest);
					if (result.statusCode != HttpStatus.SC_NO_CONTENT)
						throw new StorageInnerException(String.format("Couldn't create table '%s'", tableName));
				}
				return null;
			}
		};
		storageOperation.executeTranslatingExceptions();
	}
	
	// public TableServiceContext GetDataServiceContext();
	// public void Attach(DataServiceContext serviceContext);
	
}
