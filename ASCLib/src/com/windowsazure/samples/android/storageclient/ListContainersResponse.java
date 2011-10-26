package com.windowsazure.samples.android.storageclient;

import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.URISyntaxException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

final class ListContainersResponse
{

    public ListContainersResponse(InputStream inputstream)
    {
        m_Containers = new ArrayList<CloudBlobContainer>();
        m_StreamRef = inputstream;
    }

    public ArrayList getContainers(CloudBlobClient cloudblobclient)
        throws NotImplementedException, StorageException, UnsupportedEncodingException, IOException, ParserConfigurationException, SAXException, URISyntaxException
    {
        if(!m_IsParsed)
            parseResponse(cloudblobclient);
        return m_Containers;
    }

    public String getMarker()
    {
        return m_Marker;
    }

    public int getMaxResults()
    {
        return m_MaxResults;
    }

    public String getNextMarker()
    {
        return m_NextMarker;
    }

    public String getPrefix()
    {
        return m_Prefix;
    }

    public void parseResponse(CloudBlobClient cloudblobclient)
        throws NotImplementedException, StorageException, UnsupportedEncodingException, IOException, ParserConfigurationException, SAXException, URISyntaxException
    {
    	DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
    	DocumentBuilder builder = factory.newDocumentBuilder();
    	Document dom = builder.parse(m_StreamRef);
    	Element root = dom.getDocumentElement();
        NodeList items = root.getElementsByTagName("Container");
        for (int index = 0; index < items.getLength(); ++index)
        {
        	String name = items.item(index).getFirstChild().getFirstChild().getNodeValue();
        	String url = items.item(index).getLastChild().getFirstChild().getNodeValue();
        	m_Containers.add(new CloudBlobContainer(name, cloudblobclient));
        }
    }

    private ArrayList<CloudBlobContainer> m_Containers;
    private boolean m_IsParsed;
    private String m_Marker;
    private int m_MaxResults;
    private String m_NextMarker;
    private String m_Prefix;
    private InputStream m_StreamRef;
}
