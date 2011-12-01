package com.windowsazure.samples.android.storageclient;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.Map;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import org.w3c.dom.DOMException;
import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

final class TableResponse {

	// table stuff

	public static Iterable<String> getTableList(InputStream input)
			throws SAXException, IOException, ParserConfigurationException, XPathExpressionException {
		ArrayList<String> tables = new ArrayList<String>();
		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		DocumentBuilder builder = factory.newDocumentBuilder();
		Document document = builder.parse(input);

		XPath xpath = XPathFactory.newInstance().newXPath();
		String expression = "/feed/entry/content/properties/TableName";
		NodeList tableNames = (NodeList) xpath.evaluate(expression, document, XPathConstants.NODESET);
		for (int i = 0; i < tableNames.getLength(); i++) {
			tables.add(tableNames.item(i).getTextContent());
		}
		return tables;
	}

	// entity stuff

	public static Iterable<Map<String, Object>> getUnknownEntities(InputStream input)
			throws DOMException, Exception {
		ArrayList<Map<String, Object>> entities = new ArrayList<Map<String, Object>>();
		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		DocumentBuilder builder = factory.newDocumentBuilder();
		Document document = builder.parse(input);

		XPath xpath = XPathFactory.newInstance().newXPath();
		String expression = "/feed/entry";
		NodeList entitiesData = (NodeList) xpath.evaluate(expression, document, XPathConstants.NODESET);

		expression = "content/properties";
		for (int i = 0; i < entitiesData.getLength(); i++) {
			Hashtable<String, Object> entity = new Hashtable<String, Object>();
			Node properties = (Node) xpath.evaluate(expression, entitiesData.item(i), XPathConstants.NODE);
			addProperties(entity, properties.getChildNodes());
			entities.add(entity);
			}
		return entities;
	}

	public static <E> Iterable<E> getEntities(Class<E> clazz, InputStream input)
			throws DOMException, Exception {
		ArrayList<E> entities = new ArrayList<E>();
		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		DocumentBuilder builder = factory.newDocumentBuilder();
		Document document = builder.parse(input);

		XPath xpath = XPathFactory.newInstance().newXPath();
		String expression = "/feed/entry";
		NodeList entitiesData = (NodeList) xpath.evaluate(expression, document, XPathConstants.NODESET);

		expression = "content/properties";
		for (int i = 0; i < entitiesData.getLength(); i++) {
			E entity = clazz.newInstance();
			Node properties = (Node) xpath.evaluate(expression, entitiesData.item(i), XPathConstants.NODE);
			applyProperties(entity, properties.getChildNodes());
			entities.add(entity);
			}
		return entities;
	}

	// help stuff

	private static Iterable<TableProperty<?>> getNormalizedProperties(NodeList properties) throws DOMException, Exception {
		ArrayList<TableProperty<?>> result = new ArrayList<TableProperty<?>>();

    	for (int i = 0; i < properties.getLength(); i++) {
    		Node property = properties.item(i);
    		if (property.getNodeName().startsWith("d:")) {
        		NamedNodeMap attribs = property.getAttributes();
        		String edmType = EdmType.EdmString.toString();
        		if ((attribs != null) && (attribs.getLength() > 0)) {
	    			Node attribValue = attribs.getNamedItem("m:type");
	    			if (attribValue != null) edmType = attribValue.getTextContent();
        		}
        		String propertyName = property.getNodeName().substring(2);
        		TableProperty<?> normalizedProperty = TableProperty.fromRepresentation(
        				propertyName,
        				EdmType.fromRepresentation(edmType),
        				property.getTextContent());
        		result.add(normalizedProperty);
    		}
    	}

		return result;
	}

    private static void addProperties(Hashtable<String,Object> entity, NodeList properties) throws DOMException, Exception {
    	for (TableProperty<?> property : getNormalizedProperties(properties)) {
    		entity.put(property.getName(), property.getValue());
    	}
    }

    private static <E> void applyProperties(E entity, NodeList properties) throws DOMException, Exception {
    	for (TableProperty<?> property : getNormalizedProperties(properties)) {
    		Field field = null;
    		try {
    			field = entity.getClass().getField(property.getName());
        		field.set(entity, property.getValue());
    		} catch (Exception e) { }
    	}
    }

}
