package com.windowsazure.samples.android.storageclient;

import java.io.IOException;
import java.io.InputStream;
import java.net.URISyntaxException;
import java.text.ParseException;
import java.util.ArrayList;
import javax.xml.namespace.QName;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

class ListBlobsResponse
{

    public ListBlobsResponse(InputStream inputstream)
    {
        m_Blobs = new ArrayList<CloudBlob>();
        m_StreamRef = inputstream;
    }

    public ArrayList<CloudBlob> getBlobs(CloudBlobClient cloudblobclient, CloudBlobContainer cloudblobcontainer)
        throws StorageException, NotImplementedException, SAXException, IOException, ParserConfigurationException
    {
        if(!m_IsParsed)
            parseResponse(cloudblobclient, cloudblobcontainer);
        return m_Blobs;
    }

    public String getDelimiter()
    {
        return m_Delimiter;
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

    public void parseResponse(CloudBlobClient cloudblobclient, CloudBlobContainer cloudblobcontainer)
        throws StorageException, NotImplementedException, SAXException, IOException, ParserConfigurationException
    {
    	DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
    	DocumentBuilder builder = factory.newDocumentBuilder();
    	Document dom = builder.parse(m_StreamRef);
    	Element root = dom.getDocumentElement();
        NodeList items = root.getElementsByTagName("Blob");
        for (int index = 0; index < items.getLength(); ++index)
        {
        	String name = items.item(index).getFirstChild().getFirstChild().getNodeValue();
        	String url = items.item(index).getLastChild().getFirstChild().getNodeValue();
        	//m_Blobs.add(new CloudBlob(name, cloudblobclient));
        	throw new NotImplementedException();
        }
    }

    private ArrayList<CloudBlob> m_Blobs;
    private boolean m_IsParsed;
    private String m_Marker;
    private int m_MaxResults;
    private String m_NextMarker;
    private String m_Prefix;
    private InputStream m_StreamRef;
    private String m_Delimiter;
}
