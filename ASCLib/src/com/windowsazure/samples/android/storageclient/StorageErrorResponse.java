package com.windowsazure.samples.android.storageclient;

import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.HashMap;
import javax.xml.namespace.QName;

import com.windowsazure.samples.android.storageclient.internal.xml.DOMAdapter;

final class StorageErrorResponse
{

    public StorageErrorResponse()
    {
    	m_IsParsed = false;
    	m_ErrorInfo = new StorageExtendedErrorInformation();
    	m_ErrorInfo.errorMessage = "The server response couldn't be parsed. Further details aren't available.";
    }
    public StorageErrorResponse(InputStream errorStream) throws NotImplementedException, UnsupportedEncodingException, IOException
    {
    	Utility.assertNotNull("errorStream", errorStream);
    	m_StreamRef = errorStream;
    	m_IsParsed = false;
    	m_ErrorInfo = new StorageExtendedErrorInformation();
    	this.parseResponse();
    }

    public StorageExtendedErrorInformation getExtendedErrorInformation()
        throws NotImplementedException
    {
    	return m_ErrorInfo;
    }

    private void parseResponse()
        throws NotImplementedException, UnsupportedEncodingException, IOException
    {
    	class StorageErrorResponseDOMAdapter extends DOMAdapter<String> {
    		
    		public StorageErrorResponseDOMAdapter(String xmlString) {
    			super(xmlString);
    		}

    		@Override
    		public String build()
    		{
    			return getRootNode().getInnerText();
    		}
    	}

    	String xmlString = Utility.readStringFromStream(m_StreamRef);
    	try
    	{
    		m_ErrorInfo.errorMessage = new StorageErrorResponseDOMAdapter(xmlString).build();
    	}
    	catch (Exception exception)
    	{
    		m_ErrorInfo.errorMessage = xmlString;
    	}
    	m_IsParsed = true;
    }

    private StorageExtendedErrorInformation m_ErrorInfo;
    private boolean m_IsParsed;
    private InputStream m_StreamRef;
}
