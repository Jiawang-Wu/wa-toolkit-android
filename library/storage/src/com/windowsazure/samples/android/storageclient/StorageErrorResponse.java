package com.windowsazure.samples.android.storageclient;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

final class StorageErrorResponse {

	private StorageExtendedErrorInformation m_ErrorInfo;

	private InputStream m_StreamRef;

	public StorageErrorResponse() {
		m_ErrorInfo = new StorageExtendedErrorInformation();
		m_ErrorInfo.errorMessage = "The server response couldn't be parsed. Further details aren't available.";
	}

	public StorageErrorResponse(InputStream errorStream)
			throws UnsupportedEncodingException,
			IOException {
		Utility.assertNotNull("errorStream", errorStream);
		m_StreamRef = errorStream;
		m_ErrorInfo = new StorageExtendedErrorInformation();
		this.parseResponse();
	}
	public StorageExtendedErrorInformation getExtendedErrorInformation() {
		return m_ErrorInfo;
	}
	private void parseResponse() throws UnsupportedEncodingException, IOException {
		try {
			DocumentBuilderFactory factory = DocumentBuilderFactory
					.newInstance();
			DocumentBuilder builder = factory.newDocumentBuilder();
			Document dom = builder.parse(m_StreamRef);
			
			Element root = dom.getDocumentElement();

			// SAS Service error <string></string>
			if (root.getNodeName().equals("string")) {
				m_ErrorInfo.errorCode = m_ErrorInfo.errorMessage = ((Element) root).getTextContent();
			} else { // WA error <Code></Code><Message></Message>
				Element codeElement = (Element) root.getElementsByTagName(
						"Code").item(0);
				Element messageElement = (Element) root.getElementsByTagName(
						"Message").item(0);
				if (codeElement == null) {
					codeElement = (Element) root.getElementsByTagName(
							"code").item(0);
					messageElement = (Element) root.getElementsByTagName(
						"message").item(0);
			    }
				m_ErrorInfo.errorCode = codeElement.getTextContent();
				m_ErrorInfo.errorMessage = messageElement.getTextContent();
			}
		} catch (Exception exception) {
			String xmlString = Utility.readStringFromStream(m_StreamRef);
			m_ErrorInfo.errorMessage = xmlString;
		}
	}
}
