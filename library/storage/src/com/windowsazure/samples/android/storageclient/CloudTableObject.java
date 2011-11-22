package com.windowsazure.samples.android.storageclient;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.Field;
import java.net.URI;
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
import org.apache.http.client.methods.HttpPut;
import org.apache.http.client.methods.HttpRequestBase;
import org.w3c.dom.DOMException;
import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

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
	
	public Iterable<E> queryEntities(Class<E> clazz) throws Exception {
		return queryEntities(clazz, null);
	}
	
	public Iterable<E> queryEntities(Class<E> clazz, String filter) throws Exception {
		final String thatFilter = filter;
		final Class<E> thatClazz = clazz; 
		StorageOperation<Iterable<E>> storageOperation = new StorageOperation<Iterable<E>>() {
			public Iterable<E> execute() throws Exception {
				ArrayList<E> entities = new ArrayList<E>();
				HttpGet request = TableRequest.queryEntity(m_Endpoint, m_TableName, thatFilter);
				m_Credentials.signTableRequest(request);
				this.processRequest(request);
				if (result.statusCode != HttpStatus.SC_OK) {
					throw new StorageInnerException(String.format("Couldn't query entities on table '%s'", m_TableName));
				} else { 
					DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
					DocumentBuilder builder = factory.newDocumentBuilder();
					Document document = builder.parse(result.httpResponse.getEntity().getContent());
					
					XPath xpath = XPathFactory.newInstance().newXPath();
					String expression = "/feed/entry";
					NodeList entitiesData = (NodeList) xpath.evaluate(expression, document, XPathConstants.NODESET);
					
					expression = "content/properties";
					for (int i = 0; i < entitiesData.getLength(); i++) {
						E entity = thatClazz.newInstance();
						Node properties = (Node) xpath.evaluate(expression, entitiesData.item(i), XPathConstants.NODE);
						applyProperties(entity, properties.getChildNodes());
						entities.add(entity);
						}
				}
				return entities;
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

	private TableProperty<?>[] getEntityProperties(E entity) throws IllegalArgumentException, IllegalAccessException {
		Field[] fields = entity.getClass().getFields();
		TableProperty<?>[] properties = new TableProperty<?>[fields.length];
		for (int i = 0; i < fields.length; i++) {
			properties[i] = TableProperty.newProperty(fields[i].getName(), fields[i].getType(), fields[i].get(entity));
		}
		return properties;
	}
	
    /*@SuppressWarnings ("unchecked")
    private Class<E> getTypeParameterClass()
    {
        Type type = getClass().getGenericSuperclass();
        ParameterizedType paramType = (ParameterizedType) type;
        return (Class<E>) paramType.getActualTypeArguments()[0];
    }*/

    private void applyProperties(E entity, NodeList properties) throws DOMException, Exception {
    	for (int i = 0; i < properties.getLength(); i++) {
    		Node property = properties.item(i);
    		if (property.getNodeName().startsWith("d:")) {
        		NamedNodeMap attribs = property.getAttributes();
        		String edmType = "Edm.String";
        		if ((attribs != null) && (attribs.getLength() > 0))
        		{
	    			Node attribValue = attribs.getNamedItem("m:type");
	    			if (attribValue != null) edmType = attribValue.getTextContent();
        		}
        		String propertyName = property.getNodeName().substring(2);
        		Field field = null;
        		try {
        			field = entity.getClass().getField(propertyName);
	        		TableProperty<?> convertedProperty = TableProperty.fromRepresentation(
	        				property.getNodeName().substring(2), 
	        				EdmType.fromRepresentation(edmType), 
	        				property.getTextContent());
	        		field.set(entity, convertedProperty.getValue());
        		} catch (Exception e) { }
    		}
    	}
    }
	
}
