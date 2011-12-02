package com.windowsazure.samples.android.storageclient;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.Field;
import java.net.URI;
import java.security.InvalidKeyException;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.http.HttpStatus;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.client.methods.HttpRequestBase;

/**
Provides a client for accessing a Windows Azure Table entities. 
@see CloudTableEntity
*/	
public class CloudTableObject<E extends CloudTableEntity> {
	
	private String m_TableName;
	private URI m_Endpoint;
	private StorageCredentials m_Credentials;

	/**
	Initializes a new instance of the CloudTableObject class using the specified Table service endpoint and account credentials for a specific Table.
	@param tableName the table name to handle
	@param baseUri an absolute {@link URI} giving the base location of the table storage
	@param credentials the {@link StorageCredentials} used to access the storage repository
	@see StorageCredentials
	*/ 
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

	/**
	Gets the table name of the handled Table.
	@return	table name for handled Table
	*/ 
	public String getTableName() {
		return m_TableName;
	}
	
	/**
	Gets the base location of the table storage.
	@return	the URI value of the table storage
	*/	                          
	public URI getBaseUri() {
		return m_Endpoint;
	}
	
	/**
	Gets the credentials used to access the table storage.
	@return	the credentials for accesing the table storage
	*/	                          
	public StorageCredentials getCredentials() {
		return m_Credentials;
	}
	
	/**
	Returns an iterable collection of table entities for the storage account.
	@param baseUri an absolute {@link URI} giving the base location of the table storage
	@param credentials the {@link StorageCredentials} used to access the storage repository
	@param tableName the table name to query entities from
	@return	an iterable collection of table entities 
	*/	                          
	public static Iterable<Map<String, Object>> query(URI baseUri, StorageCredentials credentials, String tableName) 
			throws UnsupportedEncodingException, StorageException, IOException {
		return query(baseUri, credentials, tableName, null);
	}
	
	/**
	Returns an iterable collection of table entities for the storage account that satisfy a the specified filter.
	@param baseUri an absolute {@link URI} giving the base location of the table storage
	@param credentials the {@link StorageCredentials} used to access the storage repository
	@param tableName the table name to query entities from
	@param filter the filter that entities must satisfy
	@return	an iterable collection of table entities 
	*/	                          
	public static Iterable<Map<String, Object>> query(URI baseUri, StorageCredentials credentials, String tableName, String filter) 
			throws UnsupportedEncodingException, StorageException, IOException {
		return query(baseUri, credentials, tableName, filter, 0);
	}

	/**
	Returns an iterable collection of the first N (specified by parameter top) table entities for the storage account that satisfy a the specified filter.
	@param baseUri an absolute {@link URI} giving the base location of the table storage
	@param credentials the {@link StorageCredentials} used to access the storage repository
	@param tableName the table name to query entities from
	@param filter the filter that entities must satisfy
	@param top the max number of entities to retrieve
	@return	an iterable collection of table entities 
	*/	                          
	public static Iterable<Map<String, Object>> query(URI baseUri, StorageCredentials credentials, String tableName, String filter, int top) 
			throws UnsupportedEncodingException, StorageException, IOException {
		final URI thatUri = baseUri;
		final StorageCredentials thatCredentials = credentials;
		final String thatTableName = tableName;
		final String thatFilter = filter;
		final int thatTop = top;
		StorageOperation<Iterable<Map<String, Object>>> storageOperation = new StorageOperation<Iterable<Map<String, Object>>>() {
			public Iterable<Map<String, Object>> execute() throws Exception {
				HttpGet request = TableRequest.queryEntity(thatUri, thatTableName, thatFilter, thatTop);
				signCallNValidate(request, this, thatCredentials, HttpStatus.SC_OK, String.format("Couldn't query entities on table '%s'", thatTableName));												
				return TableResponse.getUnknownEntities(result.httpResponse.getEntity().getContent());
			}
		};
		return storageOperation.executeTranslatingExceptions();		
	}

	/**
	Inserts a new table entity for the storage account.
	@param baseUri an absolute {@link URI} giving the base location of the table storage
	@param credentials the {@link StorageCredentials} used to access the storage repository
	@param tableName the table name to query entities from
	@param properties a map that represents the properties (key/value) of an entity
	*/	                          
	public static void insert(URI baseUri, StorageCredentials credentials, String tableName, Map<String, Object> properties) 
			throws UnsupportedEncodingException, StorageException, IOException {
		final URI thatUri = baseUri;
		final StorageCredentials thatCredentials = credentials;
		final String thatTableName = tableName;
		final Map<String, Object> thatProperties = properties;
		StorageOperation<Void> storageOperation = new StorageOperation<Void>() {
			public Void execute() throws Exception {
				HttpPost request = TableRequest.insertEntity(thatUri, thatTableName, getUnknownEntityProperties(thatProperties));
				signCallNValidate(request, this, thatCredentials, HttpStatus.SC_CREATED, String.format("Couldn't insert entity on table '%s'", thatTableName));								
				return null;
			}
		};
		storageOperation.executeTranslatingExceptions();
	}
	
	/**
	Updates a table entity for the storage account.
	@param baseUri an absolute {@link URI} giving the base location of the table storage
	@param credentials the {@link StorageCredentials} used to access the storage repository
	@param tableName the table name to query entities from
	@param properties a map that represents the properties (key/value) of an entity
	*/	                          
	public static void update(URI baseUri, StorageCredentials credentials, String tableName, Map<String, Object> properties) 
			throws UnsupportedEncodingException, StorageException, IOException {
		final URI thatUri = baseUri;
		final StorageCredentials thatCredentials = credentials;
		final String thatTableName = tableName;
		final Map<String, Object> thatProperties = properties;
		StorageOperation<Void> storageOperation = new StorageOperation<Void>() {
			public Void execute() throws Exception {
				HttpPut request = TableRequest.updateEntity(thatUri, thatTableName, getUnknownEntityProperties(thatProperties));
				signCallNValidate(request, this, thatCredentials, HttpStatus.SC_NO_CONTENT, String.format("Couldn't update entity on table '%s'", thatTableName));								
				return null;
			}
		};
		storageOperation.executeTranslatingExceptions();
	}
	
	/**
	Returns an iterable collection of table entities for the storage account.
	@param clazz the type of the entity object to retrieve
	@return	an iterable collection of table entities 
	*/	                          
	public Iterable<E> query(Class<E> clazz) throws Exception {
		return query(clazz, null);
	}
	
	/**
	Returns an iterable collection of table entities for the storage account that satisfy a the specified filter.
	@param clazz the type of the entity object to retrieve
	@param filter the filter that entities must satisfy
	@return	an iterable collection of table entities 
	*/	                          
	public Iterable<E> query(Class<E> clazz, String filter) throws Exception {
		return query(clazz, filter, 0);
	}
	
	/**
	Returns an iterable collection of the first N (specified by parameter top) table entities for the storage account that satisfy a the specified filter.
	@param clazz the type of the entity object to retrieve
	@param filter the filter that entities must satisfy
	@param top the max number of entities to retrieve
	@return	an iterable collection of table entities 
	*/	                          
	public Iterable<E> query(Class<E> clazz, String filter, int top) throws Exception {
		final String thatFilter = filter;
		final Class<E> thatClazz = clazz; 
		final int thatTop = top;
		StorageOperation<Iterable<E>> storageOperation = new StorageOperation<Iterable<E>>() {
			public Iterable<E> execute() throws Exception {
				HttpGet request = TableRequest.queryEntity(m_Endpoint, m_TableName, thatFilter, thatTop);
				signCallNValidate(request, this, m_Credentials, HttpStatus.SC_OK, String.format("Couldn't query entities on table '%s'", m_TableName));								
				return TableResponse.getEntities(thatClazz, result.httpResponse.getEntity().getContent());
			}
		};
		return storageOperation.executeTranslatingExceptions();
	}
	
	/**
	Inserts a new table entity for the storage account.
	@param entity the entity to insert into the table
	*/	                          
	public void insert(E entity) throws UnsupportedEncodingException, StorageException, IOException {		
		final E thatEntity = entity;
		StorageOperation<Void> storageOperation = new StorageOperation<Void>() {
			public Void execute() throws Exception {
				HttpPost request = TableRequest.insertEntity(m_Endpoint, m_TableName, getEntityProperties(thatEntity));
				signCallNValidate(request, this, m_Credentials, HttpStatus.SC_CREATED, String.format("Couldn't insert entity on table '%s'", m_TableName));								
				return null;
			}
		};
		storageOperation.executeTranslatingExceptions();
	}
	
	/**
	Inserts or replaces a table entity for the storage account.
	@param entity the entity to insert or replace into the table
	*/	                          
	public void insertOrReplace(E entity) throws UnsupportedEncodingException, StorageException, IOException {		
		final E thatEntity = entity;
		StorageOperation<Void> storageOperation = new StorageOperation<Void>() {
			public Void execute() throws Exception {
				HttpPut request = TableRequest.insertOrReplaceEntity(m_Endpoint, m_TableName, getEntityProperties(thatEntity));
				signCallNValidate(request, this, m_Credentials, HttpStatus.SC_NO_CONTENT, String.format("Couldn't insert/replace entity on table '%s'", m_TableName));								
				return null;
			}
		};
		storageOperation.executeTranslatingExceptions();
	}

	/**
	Inserts or merges a table entity for the storage account.
	@param entity the entity to insert or merge into the table
	*/	                          
	public void insertOrMerge(E entity) throws UnsupportedEncodingException, StorageException, IOException {		
		final E thatEntity = entity;
		StorageOperation<Void> storageOperation = new StorageOperation<Void>() {
			public Void execute() throws Exception {
				HttpRequestBase request = TableRequest.insertOrMergeEntity(m_Endpoint, m_TableName, getEntityProperties(thatEntity));
				signCallNValidate(request, this, m_Credentials, HttpStatus.SC_NO_CONTENT, String.format("Couldn't insert/merge entity on table '%s'", m_TableName));				
				return null;
			}
		};
		storageOperation.executeTranslatingExceptions();
	}

	/**
	Updates a table entity for the storage account.
	@param entity the entity to update in the table
	*/	                          
	public void update(E entity) throws UnsupportedEncodingException, StorageException, IOException {
		final E thatEntity = entity;
		StorageOperation<Void> storageOperation = new StorageOperation<Void>() {
			public Void execute() throws Exception {
				HttpPut request = TableRequest.updateEntity(m_Endpoint, m_TableName, getEntityProperties(thatEntity));
				signCallNValidate(request, this, m_Credentials, HttpStatus.SC_NO_CONTENT, String.format("Couldn't update entity on table '%s'", m_TableName));				
				return null;
			}
		};
		storageOperation.executeTranslatingExceptions();
	}

	/**
	Merges a table entity for the storage account.
	@param entity the entity to merge in the table
	*/	                          
	public void merge(E entity) throws UnsupportedEncodingException, StorageException, IOException {
		final E thatEntity = entity;
		StorageOperation<Void> storageOperation = new StorageOperation<Void>() {
			public Void execute() throws Exception {
				HttpRequestBase request = TableRequest.mergeEntity(m_Endpoint, m_TableName, getEntityProperties(thatEntity));
				signCallNValidate(request, this, m_Credentials, HttpStatus.SC_NO_CONTENT, String.format("Couldn't merge entity on table '%s'", m_TableName));
				return null;
			}
		};
		storageOperation.executeTranslatingExceptions();		
	}

	/**
	Deletes a table entity for the storage account.
	@param entity the entity to delete from the table
	*/	                          
	public void delete(E entity) throws UnsupportedEncodingException, StorageException, IOException {
		final E thatEntity = entity;
		StorageOperation<Void> storageOperation = new StorageOperation<Void>() {
			public Void execute() throws Exception {
				HttpDelete request = TableRequest.deleteEntity(m_Endpoint, m_TableName, getEntityProperties(thatEntity));
				signCallNValidate(request, this, m_Credentials, HttpStatus.SC_NO_CONTENT, String.format("Couldn't delete entity on table '%s'", m_TableName));
				return null;
			}
		};
		storageOperation.executeTranslatingExceptions();
	}
	
	/**
	Deletes a table entity for the storage account.
	@param partitionKey the partition key of the entity to delete
	@param rowKey the row key of the entity to delete
	*/	                          
	public void delete(String partitionKey, String rowKey) throws UnsupportedEncodingException, StorageException, IOException {
		final String thatPartitionKey =  partitionKey;
		final String thatRowKey = rowKey;
		StorageOperation<Void> storageOperation = new StorageOperation<Void>() {
			public Void execute() throws Exception {
				HttpDelete request = TableRequest.deleteEntity(m_Endpoint, m_TableName, thatPartitionKey, thatRowKey);
				signCallNValidate(request, this, m_Credentials, HttpStatus.SC_NO_CONTENT, String.format("Couldn't delete entity on table '%s'", m_TableName));
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
	
	private static void signCallNValidate(HttpRequestBase request, StorageOperation<?> operation, StorageCredentials credentials, int expectedStatus, String errorMessage) 
			throws InvalidKeyException, IllegalArgumentException, StorageException, IOException, StorageInnerException {
		credentials.signTableRequest(request);
		operation.processRequest(request);
		if (operation.getResult().statusCode != expectedStatus) {
			throw new StorageInnerException(String.format(errorMessage));
		} 
		
	}

}
