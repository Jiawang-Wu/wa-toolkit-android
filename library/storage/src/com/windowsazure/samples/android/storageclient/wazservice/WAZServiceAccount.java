package com.windowsazure.samples.android.storageclient.wazservice;

import java.io.IOException;
import java.io.StringWriter;
import java.net.URI;
import java.net.URISyntaxException;

import javax.security.auth.login.LoginException;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ByteArrayEntity;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.xml.sax.SAXException;

import com.windowsazure.samples.android.storageclient.CloudClientAccount;
import com.windowsazure.samples.android.storageclient.CloudTableClient;
import com.windowsazure.samples.android.storageclient.CloudQueueClient;
import com.windowsazure.samples.android.storageclient.PathUtility;
import com.windowsazure.samples.android.storageclient.StorageCredentials;
import com.windowsazure.samples.android.storageclient.CloudBlobClient;
import com.windowsazure.samples.android.storageclient.Utility;
import com.windowsazure.samples.android.storageclient.WAZServiceAccountCredentials;

public class WAZServiceAccount implements CloudClientAccount {

	private WAZServiceUsernameAndPassword m_WazServiceData;
	private URI m_WazServiceBaseUri;

	public WAZServiceAccount(
			WAZServiceUsernameAndPassword wazServiceData, URI wazServiceBaseUri) {
		this.m_WazServiceData = wazServiceData;
		this.m_WazServiceBaseUri = wazServiceBaseUri;
	}

	public CloudBlobClient createCloudBlobClient() throws Exception {
		return new CloudBlobClient(getBlobEndpoint(), getCredentials());
	}
	
	public CloudTableClient createCloudTableClient() throws Exception {
		return new CloudTableClient(getTableEndpoint(), getCredentials());
	}
	
	public CloudQueueClient createCloudQueueClient() throws Exception {
		return new CloudQueueClient(getQueueEndpoint(), getCredentials());
	}
	
	public String loginToWAZService() throws Exception {
		final String CREDENTIAL_NS = "http://schemas.datacontract.org/2004/07/Microsoft.Samples.WindowsPhoneCloud.StorageClient.Credentials";
		final String INSTANCE_NS = "http://www.w3.org/2001/XMLSchema-instance";
		final String LOGIN_NODE_NAME = "Login";
		final String PASSWORD_NODE_NAME = "Password";
		final String USERNAME_NODE_NAME = "UserName";

		StringWriter stringwriter = new StringWriter();
		stringwriter.append("<?xml version=\"1.0\" encoding=\"utf-8\"?>\n");
		stringwriter.append(String.format("<%s xmlns=\"%s\" xmlns:i=\"%s\">\n", LOGIN_NODE_NAME, CREDENTIAL_NS, INSTANCE_NS));
		stringwriter.append(String.format("<%s>%s</%s>\n", PASSWORD_NODE_NAME, m_WazServiceData.getPassword(), PASSWORD_NODE_NAME));
		stringwriter.append(String.format("<%s>%s</%s>\n", USERNAME_NODE_NAME, m_WazServiceData.getUsername(), USERNAME_NODE_NAME));
		stringwriter.append(String.format("</%s>\n", LOGIN_NODE_NAME));

		String loginXmlString = stringwriter.toString();

		HttpPost request = new HttpPost(PathUtility.appendPathToUri(this.m_WazServiceBaseUri, LOGIN_PATH));
		request.setEntity(new ByteArrayEntity(loginXmlString.getBytes()));
		request.setHeader("Content-Type", "text/xml");
		HttpClient client = Utility.getDefaultHttpClient();
		HttpResponse httpResponse = client.execute(request);
		
		if (httpResponse.getStatusLine().getStatusCode() == 200) {
			DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
			DocumentBuilder builder = factory.newDocumentBuilder();
			Document dom = builder.parse(httpResponse.getEntity().getContent());
			Element root = dom.getDocumentElement();
			String token = root.getTextContent();
			if (token != null) {
				return token;
			}
			else {
				throw new LoginException("Couldn't log-in to the WAZ Service: Invalid username or password");
			}
		}
		
		throw new LoginException("Couldn't log-in to the WAZ Service: The login request returned " + httpResponse.getStatusLine().getReasonPhrase());
	}

	public StorageCredentials getCredentials() throws Exception {
		return new WAZServiceAccountCredentials(this.loginToWAZService());
	}

	private URI getBlobEndpoint() throws URISyntaxException {
    	return PathUtility.appendPathToUri(this.m_WazServiceBaseUri, SHARED_ACCESS_SIGNATURE_SERVICE_PATH);
	}

	private URI getTableEndpoint() throws URISyntaxException {
	 	return PathUtility.appendPathToUri(this.m_WazServiceBaseUri, TABLES_PROXY_SERVICE_PATH);
	}
	
	private URI getQueueEndpoint() throws URISyntaxException {
		return PathUtility.appendPathToUri(this.m_WazServiceBaseUri, QUEUES_PROXY_SERVICE_PATH);
	}	
	
	private static final String LOGIN_PATH = "/AuthenticationService/login";
	private static final String REGISTER_PATH = "/AuthenticationService/register";
	private static final String SHARED_ACCESS_SIGNATURE_SERVICE_PATH = "/SharedAccessSignatureService";
	private static final String QUEUES_PROXY_SERVICE_PATH = "/AzureQueuesProxy.axd";
	private static final String TABLES_PROXY_SERVICE_PATH = "/AzureTablesProxy.axd";

	public void register(String email) throws ClientProtocolException, IOException, LoginException, ParserConfigurationException, IllegalStateException, SAXException, URISyntaxException {
		final String CREDENTIAL_NS = "http://schemas.datacontract.org/2004/07/Microsoft.Samples.WindowsPhoneCloud.StorageClient.Credentials";
		final String INSTANCE_NS = "http://www.w3.org/2001/XMLSchema-instance";
		final String REGISTER_NODE_NAME = "RegistrationUser";
		final String USERNAME_NODE_NAME = "Name";
		final String EMAIL_NODE_NAME = "EMail";
		final String PASSWORD_NODE_NAME = "Password";

		StringWriter stringwriter = new StringWriter();
		stringwriter.append(String.format("<%s xmlns:i=\"%s\" xmlns=\"%s\">\n", REGISTER_NODE_NAME, INSTANCE_NS, CREDENTIAL_NS));
		stringwriter.append(String.format("<%s>%s</%s>\n", EMAIL_NODE_NAME, email, EMAIL_NODE_NAME));
		stringwriter.append(String.format("<%s>%s</%s>\n", USERNAME_NODE_NAME, m_WazServiceData.getUsername(), USERNAME_NODE_NAME));
		stringwriter.append(String.format("<%s>%s</%s>\n", PASSWORD_NODE_NAME, m_WazServiceData.getPassword(), PASSWORD_NODE_NAME));
		stringwriter.append(String.format("</%s>\n", REGISTER_NODE_NAME));

		String registerXmlString = stringwriter.toString();

		HttpPost request = new HttpPost(PathUtility.appendPathToUri(this.m_WazServiceBaseUri, REGISTER_PATH));
		request.setEntity(new ByteArrayEntity(registerXmlString.getBytes()));
		request.setHeader("Content-Type", "text/xml");
		HttpClient client = Utility.getDefaultHttpClient();
		HttpResponse httpResponse = client.execute(request);
		
		if (httpResponse.getStatusLine().getStatusCode() == 200) {
			DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
			DocumentBuilder builder = factory.newDocumentBuilder();
			Document dom = builder.parse(httpResponse.getEntity().getContent());
			Element root = dom.getDocumentElement();
			String response = root.getTextContent();
			if (!response.equals("Success")) {
				throw new LoginException(String.format("Couldn't register in the WAZ Service: %s", response));
			}
		}
		else {
			throw 	new LoginException("Couldn't register in the WAZ Service: The register request returned " + httpResponse.getStatusLine().getReasonPhrase());
		}
	}
}
