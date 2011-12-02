package com.windowsazure.samples.android.storageclient;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.Field;
import java.net.URI;
import java.net.URISyntaxException;
import java.sql.Timestamp;

import org.apache.http.HttpStatus;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;

import com.windowsazure.samples.android.storageclient.StorageCredentials;
import com.windowsazure.samples.android.storageclient.Utility;

/**
Provides a client for accessing the Windows Azure Table service. 
*/	
public class CloudTableClient {

	private URI m_Endpoint;
	private StorageCredentials m_Credentials;

	/**
	Initializes a new instance of the CloudTableClient class using the specified Table service endpoint and account credentials.
	@param baseUri an absolute {@link URI} giving the base location of the table storage
	@param credentials the {@link StorageCredentials} used to access the storage repository
	@see StorageCredentials
	*/ 
	public CloudTableClient(URI baseUri, StorageCredentials credentials) {
		Utility.assertNotNull("baseUri", baseUri);
		Utility.assertNotNull("credentials", credentials);

		if (!baseUri.isAbsolute()) {
			throw new IllegalArgumentException(String.format("Address '%s' is not an absolute address. Relative addresses are not permitted in here.", baseUri));
		}

		m_Endpoint = baseUri;
		m_Credentials = credentials;
	}

	/**
	Gets the lower bound for date time.
	@return	lower supported date time value
	*/ 
	public Timestamp getMinSupportedDateTime() {
		return null;
	}

	/**
	Gets the base location of the table storage.
	@return	the URI value of the table storage
	*/	                          
	public URI getEndpoint() {
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
	Checks whether the table exists.
   	@param tableName the table name to check for
	@return	true if table exists; otherwise, false 
	*/	                          
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
	 
	/**
	Returns an iterable collection of table names for the storage account.
	@return	an iterable collection of table names 
	*/	                          
	public Iterable<String> listTables() 
			throws UnsupportedEncodingException, StorageException, IOException {
		StorageOperation<Iterable<String>>  storageOperation = new StorageOperation<Iterable<String>>() {
			public Iterable<String> execute() throws Exception {
				HttpGet request = TableRequest.list(m_Endpoint);						
				m_Credentials.signTableRequest(request);
				this.processRequest(request);
				if (result.statusCode != HttpStatus.SC_OK) {
					throw new StorageInnerException("Couldn't get table's list");
				}
				return TableResponse.getTableList(result.httpResponse.getEntity().getContent());
			}
		};
        return storageOperation.executeTranslatingExceptions();
	}

	/**
	Returns an enumerable collection of table names that begin with the specified prefix. 
	@return	an iterable collection of table names 
	*/	                          
	public Iterable<String> listTables(String prefix)
			throws UnsupportedEncodingException, StorageException, IOException {
		final String thatPrefix = prefix;
		StorageOperation<Iterable<String>>  storageOperation = new StorageOperation<Iterable<String>>() {
			public Iterable<String> execute() throws Exception {
				HttpGet request = TableRequest.list(m_Endpoint, thatPrefix);						
				m_Credentials.signTableRequest(request);
				this.processRequest(request);
				if (result.statusCode != HttpStatus.SC_OK) {
					throw new StorageInnerException("Couldn't get table's list");
				}
				return TableResponse.getTableList(result.httpResponse.getEntity().getContent());
			}
		};
        return storageOperation.executeTranslatingExceptions();
	}

	/**
	Creates a table with specified name.
   	@param tableName the table name to create
	*/	                          
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

	/**
	Creates the table if it does not already exist.
   	@param tableName the table name to create
	@return	true if table was created; otherwise, false
	*/	                          
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

	/**
	Deletes the table.
   	@param tableName the table name to delete
	*/	
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

	/**
	Deletes the table if it exists.
   	@param tableName the table name to delete
	@return	true if table was deleted; otherwise, false
	*/	                          
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
	 
	/**
	Creates tables from a class model defined in code. The table created will have the same name as the type used as parameter to create it.
   	@param type	the type of the class that defines the table
   	@param baseAddress an absolute {@link URI} giving the base location of the table storage
   	@param credentials the {@link StorageCredentials} used to access the storage repository
	*/	                          
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

	/**
	Creates a new instance of a CloudTableObject to handle table entities.
   	@param tableName the table name to handle
	@return a new instance of {@link CloudTableObject} to handle table's items
	@see CloudTableObject
	*/
	public <E extends CloudTableEntity> CloudTableObject<E> getCloudTableObject(String tableName) {
		return new CloudTableObject<E>(tableName, m_Endpoint, m_Credentials);
	}
		
}
