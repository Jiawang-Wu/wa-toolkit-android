package com.windowsazure.samples.android.storageclient;

import java.io.IOException;
import java.io.InputStream;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.apache.http.Header;
import org.apache.http.HttpResponse;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

public class QueueResponse {

	public static Iterable<CloudQueue> getList(InputStream responseStream, CloudQueueClient serviceClient) throws SAXException, IOException, ParserConfigurationException, URISyntaxException {

		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		DocumentBuilder builder = factory.newDocumentBuilder();
		Document dom = builder.parse(responseStream);
		Element root = dom.getDocumentElement();
		NodeList items = root.getElementsByTagName("Queue");

		ArrayList<CloudQueue> queues = new ArrayList<CloudQueue>(items.getLength());
		for (int index = 0; index < items.getLength(); ++index) {
			Element queueElement = (Element) items.item(index);

			String name = queueElement.getElementsByTagName("Name").item(0).getTextContent();
			//String url = queueElement.getElementsByTagName("Url").item(0).getTextContent();
			CloudQueue queue = new CloudQueue(name, serviceClient);

			NodeList metadataElements = queueElement.getElementsByTagName("Metadata");
			if (metadataElements.getLength() != 0)
			{
				Element metadataElement = (Element) metadataElements.item(0);
				queue.getMetadata().putAll(QueueResponse.getMetadata(metadataElement));
			}
			queues.add(queue);
		}
		return queues;
	}

	private static Map<String, String> getMetadata(Element metadataElement) {
		NodeList items = metadataElement.getChildNodes();
		Map<String, String> metadata = new HashMap<String, String>();

		for (int index = 0; index < items.getLength(); ++index) {
			Element metadataEntryElement = (Element) items.item(index);
			
			metadata.put(metadataEntryElement.getNodeName(), metadataEntryElement.getTextContent());
		}
		
		return metadata;
	}

	public static Map<String, String> getMetadata(HttpResponse response) {
		Map<String, String> metadata = new HashMap<String, String>();
		for (Header header : response.getAllHeaders())
		{
			String metadataHeaderPrefix = "x-ms-meta-"; 
			if (header.getName().startsWith(metadataHeaderPrefix))
			{
				metadata.put(header.getName().substring(metadataHeaderPrefix.length()), header.getValue());
			}
		}
		return metadata;
	}

}
