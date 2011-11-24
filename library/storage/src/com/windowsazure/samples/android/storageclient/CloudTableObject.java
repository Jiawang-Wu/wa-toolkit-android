package com.windowsazure.samples.android.storageclient;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.Field;
import java.net.URI;
import java.util.Map;
import java.util.Map.Entry;

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
	
	public static Iterable<Map<String, Object>> query(URI baseUri, StorageCredentials credentials, String tableName) 
			throws UnsupportedEncodingException, StorageException, IOException {
		return query(baseUri, credentials, tableName, null);
	}
	
	public static Iterable<Map<String, Object>> query(URI baseUri, StorageCredentials credentials, String tableName, String filter) 
			throws UnsupportedEncodingException, StorageException, IOException {
		final URI thatUri = baseUri;
		final StorageCredentials thatCredentials = credentials;
		final String thatTableName = tableName;
		final String thatFilter = filter;
		StorageOperation<Iterable<Map<String, Object>>> storageOperation = new StorageOperation<Iterable<Map<String, Object>>>() {
			public Iterable<Map<String, Object>> execute() throws Exception {
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

	public Iterable<E> query(Class<E> clazz) throws Exception {
		return query(clazz, null);
	}
	
	public Iterable<E> query(Class<E> clazz, String filter) throws Exception {
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
	
	public void insert(E entity) throws UnsupportedEncodingException, StorageException, IOException {		
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
	
	public void insertOrReplace(E entity) throws UnsupportedEncodingException, StorageException, IOException {		
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

	public void insertOrMerge(E entity) throws UnsupportedEncodingException, StorageException, IOException {		
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

	public void update(E entity) throws UnsupportedEncodingException, StorageException, IOException {
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

	public void merge(E entity) throws UnsupportedEncodingException, StorageException, IOException {
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

	public void delete(E entity) throws UnsupportedEncodingException, StorageException, IOException {
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
	
	public void delete(String partitionKey, String rowKey) throws UnsupportedEncodingException, StorageException, IOException {
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

	private static TableProperty<?>[] getUnknownEntityProperties(Map<String, Object> entity) throws IllegalArgumentException, IllegalAccessException {
		TableProperty<?>[] properties = new TableProperty<?>[entity.size()];
		int i = 0;
		for (Entry<String, Object> property : entity.entrySet()) {
			properties[i++] = TableProperty.newProperty(property.getKey(), property.getValue().getClass(), property.getValue());
		}
		return properties;
	}

	public static void insert(final URI endpoint,
			final StorageCredentials tableCredentials, final String tableName,
			final Map<String, Object> entity) throws UnsupportedEncodingException, StorageException, IOException {
		StorageOperation<Void> storageOperation = new StorageOperation<Void>() {
			public Void execute() throws Exception {
				HttpPost request = TableRequest.insertEntity(endpoint, tableName, getUnknownEntityProperties(entity));
				tableCredentials.signTableRequest(request);
				this.processRequest(request);
				if (result.statusCode != HttpStatus.SC_CREATED) {
					throw new StorageInnerException(String.format("Couldn't insert entity on table '%s'", tableName));
				} 
				return null;
			}
		};
		storageOperation.executeTranslatingExceptions();
	}
	
	public static void update(final URI endpoint,
			final StorageCredentials tableCredentials, final String tableName,
			final Map<String, Object> entity) throws UnsupportedEncodingException, StorageException, IOException {
		StorageOperation<Void> storageOperation = new StorageOperation<Void>() {
			public Void execute() throws Exception {
				HttpPut request = TableRequest.updateEntity(endpoint, tableName, getUnknownEntityProperties(entity));
				tableCredentials.signTableRequest(request);
				this.processRequest(request);
				if (result.statusCode != HttpStatus.SC_NO_CONTENT) {
					throw new StorageInnerException(String.format("Couldn't update entity on table '%s'", tableName));
				} 
				return null;
			}
		};
		storageOperation.executeTranslatingExceptions();
	}
}
