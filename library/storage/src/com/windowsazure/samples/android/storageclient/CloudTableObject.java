package com.windowsazure.samples.android.storageclient;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.Field;
import java.net.URI;

import org.apache.http.HttpStatus;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;

public class CloudTableObject<E> {
	
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
	
	public Iterable<E> query(String filter) {
		return null;
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
	
	private TableProperty<?>[] getEntityProperties(E entity) throws IllegalArgumentException, IllegalAccessException {
		Field[] fields = entity.getClass().getFields();
		TableProperty<?>[] properties = new TableProperty<?>[fields.length];
		for (int i = 0; i < fields.length; i++) {
			properties[i] = TableProperty.newProperty(fields[i].getName(), fields[i].getType(), fields[i].get(entity));
		}
		return properties;
	}
	
}
