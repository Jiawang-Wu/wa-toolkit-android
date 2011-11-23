package com.windowsazure.samples.android.storageclient;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.Field;
import java.net.URI;
import java.util.Hashtable;

import org.apache.http.HttpStatus;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.client.methods.HttpRequestBase;

public class CloudTableObject<E extends CloudTableEntity> {
	
	private String m_TableName;
	private URI m_Endpoint;
	private StorageCredentials m_Credentials;
	
	public CloudTableObject(String tableName, URI baseUri, StorageCredentials credentials) {
		Utility.assertNotNull("baseUri", baseUri);
		Utility.assertNotNull("credentials", credentials);

		if (!baseUri.isAbsolute()) {
			throw new IllegalArgumentException(String.format("Address '%s' is not an absolute address. Relative addresses are not permitted in here.", baseUri));
		}

		m_Endpoint = baseUri;
		m_Credentials = credentials;
		m_TableName = tableName;
	}
	
	public String getTableName() {
		return m_TableName;
	}
	
	public URI getBaseUri() {
		return m_Endpoint;
	}
	
	public StorageCredentials getCredentials() {
		return m_Credentials;
	}
	
	public static Iterable<Hashtable<String, Object>> queryEntities(URI baseUri, StorageCredentials credentials, String tableName) 
			throws UnsupportedEncodingException, StorageException, IOException {
		return queryEntities(baseUri, credentials, tableName, null);
	}
	
	public static Iterable<Hashtable<String, Object>> queryEntities(URI baseUri, StorageCredentials credentials, String tableName, String filter) 
			throws UnsupportedEncodingException, StorageException, IOException {
		final URI thatUri = baseUri;
		final StorageCredentials thatCredentials = credentials;
		final String thatTableName = tableName;
		final String thatFilter = filter;
		StorageOperation<Iterable<Hashtable<String, Object>>> storageOperation = new StorageOperation<Iterable<Hashtable<String, Object>>>() {
			public Iterable<Hashtable<String, Object>> execute() throws Exception {
				HttpGet request = TableRequest.queryEntity(thatUri, thatTableName, thatFilter);
				thatCredentials.signTableRequest(request);
				this.processRequest(request);
				if (result.statusCode != HttpStatus.SC_OK) {
					throw new StorageInnerException(String.format("Couldn't query entities on table '%s'", thatTableName));
				} 
				return TableResponse.getUnknownEntities(result.httpResponse.getEntity().getContent());
			}
		};
		return storageOperation.executeTranslatingExceptions();		
	}

	public Iterable<E> queryEntities(Class<E> clazz) throws Exception {
		return queryEntities(clazz, null);
	}
	
	
	public Iterable<E> queryEntities(Class<E> clazz, String filter) throws Exception {
		final String thatFilter = filter;
		final Class<E> thatClazz = clazz; 
		StorageOperation<Iterable<E>> storageOperation = new StorageOperation<Iterable<E>>() {
			public Iterable<E> execute() throws Exception {
				HttpGet request = TableRequest.queryEntity(m_Endpoint, m_TableName, thatFilter);
				m_Credentials.signTableRequest(request);
				this.processRequest(request);
				if (result.statusCode != HttpStatus.SC_OK) {
					throw new StorageInnerException(String.format("Couldn't query entities on table '%s'", m_TableName));
				} 
				return TableResponse.getEntities(thatClazz, result.httpResponse.getEntity().getContent());
			}
		};
		return storageOperation.executeTranslatingExceptions();
	}
	
	public void insertEntity(E entity) throws UnsupportedEncodingException, StorageException, IOException {		
		final E thatEntity = entity;
		StorageOperation<Void> storageOperation = new StorageOperation<Void>() {
			public Void execute() throws Exception {
				HttpPost request = TableRequest.insertEntity(m_Endpoint, m_TableName, getEntityProperties(thatEntity));
				m_Credentials.signTableRequest(request);
				this.processRequest(request);
				if (result.statusCode != HttpStatus.SC_CREATED) {
					throw new StorageInnerException(String.format("Couldn't insert entity on table '%s'", m_TableName));
				} 
				return null;
			}
		};
		storageOperation.executeTranslatingExceptions();
	}
	
	public void insertOrReplaceEntity(E entity) throws UnsupportedEncodingException, StorageException, IOException {		
		final E thatEntity = entity;
		StorageOperation<Void> storageOperation = new StorageOperation<Void>() {
			public Void execute() throws Exception {
				HttpPut request = TableRequest.insertOrReplaceEntity(m_Endpoint, m_TableName, getEntityProperties(thatEntity));
				m_Credentials.signTableRequest(request);
				this.processRequest(request);
				if (result.statusCode != HttpStatus.SC_NO_CONTENT) {
					throw new StorageInnerException(String.format("Couldn't insert/replace entity on table '%s'", m_TableName));
				} 
				return null;
			}
		};
		storageOperation.executeTranslatingExceptions();
	}

	public void insertOrMergeEntity(E entity) throws UnsupportedEncodingException, StorageException, IOException {		
		final E thatEntity = entity;
		StorageOperation<Void> storageOperation = new StorageOperation<Void>() {
			public Void execute() throws Exception {
				HttpRequestBase request = TableRequest.insertOrMergeEntity(m_Endpoint, m_TableName, getEntityProperties(thatEntity));
				m_Credentials.signTableRequest(request);
				this.processRequest(request);
				if (result.statusCode != HttpStatus.SC_NO_CONTENT) {
					throw new StorageInnerException(String.format("Couldn't insert/merge entity on table '%s'", m_TableName));
				} 
				return null;
			}
		};
		storageOperation.executeTranslatingExceptions();
	}

	public void updateEntity(E entity) throws UnsupportedEncodingException, StorageException, IOException {
		final E thatEntity = entity;
		StorageOperation<Void> storageOperation = new StorageOperation<Void>() {
			public Void execute() throws Exception {
				HttpPut request = TableRequest.updateEntity(m_Endpoint, m_TableName, getEntityProperties(thatEntity));
				m_Credentials.signTableRequest(request);
				this.processRequest(request);
				if (result.statusCode != HttpStatus.SC_NO_CONTENT) {
					throw new StorageInnerException(String.format("Couldn't update entity on table '%s'", m_TableName));
				} 
				return null;
			}
		};
		storageOperation.executeTranslatingExceptions();
	}

	public void mergeEntity(E entity) throws UnsupportedEncodingException, StorageException, IOException {
		final E thatEntity = entity;
		StorageOperation<Void> storageOperation = new StorageOperation<Void>() {
			public Void execute() throws Exception {
				HttpRequestBase request = TableRequest.mergeEntity(m_Endpoint, m_TableName, getEntityProperties(thatEntity));
				m_Credentials.signTableRequest(request);
				this.processRequest(request);
				if (result.statusCode != HttpStatus.SC_NO_CONTENT) {
					throw new StorageInnerException(String.format("Couldn't merge entity on table '%s'", m_TableName));
				} 
				return null;
			}
		};
		storageOperation.executeTranslatingExceptions();		
	}

	public void deleteEntity(E entity) throws UnsupportedEncodingException, StorageException, IOException {
		final E thatEntity = entity;
		StorageOperation<Void> storageOperation = new StorageOperation<Void>() {
			public Void execute() throws Exception {
				HttpDelete request = TableRequest.deleteEntity(m_Endpoint, m_TableName, getEntityProperties(thatEntity));
				m_Credentials.signTableRequest(request);
				this.processRequest(request);
				if (result.statusCode != HttpStatus.SC_NO_CONTENT) {
					throw new StorageInnerException(String.format("Couldn't delete entity on table '%s'", m_TableName));
				} 
				return null;
			}
		};
		storageOperation.executeTranslatingExceptions();
	}
	
	public void deleteEntity(String partitionKey, String rowKey) throws UnsupportedEncodingException, StorageException, IOException {
		final String thatPartitionKey =  partitionKey;
		final String thatRowKey = rowKey;
		StorageOperation<Void> storageOperation = new StorageOperation<Void>() {
			public Void execute() throws Exception {
				HttpDelete request = TableRequest.deleteEntity(m_Endpoint, m_TableName, thatPartitionKey, thatRowKey);
				m_Credentials.signTableRequest(request);
				this.processRequest(request);
				if (result.statusCode != HttpStatus.SC_NO_CONTENT) {
					throw new StorageInnerException(String.format("Couldn't delete entity on table '%s'", m_TableName));
				} 
				return null;
			}
		};
		storageOperation.executeTranslatingExceptions();		
	}
	

	private TableProperty<?>[] getEntityProperties(E entity) throws IllegalArgumentException, IllegalAccessException {
		Field[] fields = entity.getClass().getFields();
		TableProperty<?>[] properties = new TableProperty<?>[fields.length];
		for (int i = 0; i < fields.length; i++) {
			properties[i] = TableProperty.newProperty(fields[i].getName(), fields[i].getType(), fields[i].get(entity));
		}
		return properties;
	}

}
