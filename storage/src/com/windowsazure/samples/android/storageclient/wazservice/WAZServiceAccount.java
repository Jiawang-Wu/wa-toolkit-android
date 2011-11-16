package com.windowsazure.samples.android.storageclient.wazservice;

import java.io.StringWriter;
import java.net.URI;
import java.net.URISyntaxException;

import javax.security.auth.login.LoginException;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ByteArrayEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import com.windowsazure.samples.android.storageclient.CloudClientAccount;
import com.windowsazure.samples.android.storageclient.CloudTableClient;
import com.windowsazure.samples.android.storageclient.CloudQueueClient;
import com.windowsazure.samples.android.storageclient.PathUtility;
import com.windowsazure.samples.android.storageclient.StorageCredentials;
import com.windowsazure.samples.android.storageclient.CloudBlobClient;
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

		String path = this.m_WazServiceBaseUri.getPath() + LOGIN_PATH;
		
		HttpPost request = new HttpPost("https://" + this.m_WazServiceBaseUri.getHost() + path);
		request.setEntity(new ByteArrayEntity(loginXmlString.getBytes()));
		request.setHeader("Content-Type", "text/xml");
		HttpClient client = new DefaultHttpClient();
		HttpResponse httpResponse = client.execute(request);
		
		if (httpResponse.getStatusLine().getStatusCode() == 200)
		{
			DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
			DocumentBuilder builder = factory.newDocumentBuilder();
			Document dom = builder.parse(httpResponse.getEntity().getContent());
			Element root = dom.getDocumentElement();
			String token = root.getTextContent();
			if (token != null)
			{
				return token;
			}
			else
			{
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
	 	return PathUtility.appendPathToUri(this.m_WazServiceBaseUri, SHARED_ACCESS_SIGNATURE_SERVICE_PATH);
	}
	
	private URI getQueueEndpoint() throws URISyntaxException {
	    	return PathUtility.appendPathToUri(this.m_WazServiceBaseUri, SHARED_ACCESS_SIGNATURE_SERVICE_PATH);
	}	
	
	private static final String LOGIN_PATH = "/AuthenticationService/login";
	private static final String SHARED_ACCESS_SIGNATURE_SERVICE_PATH = "/SharedAccessSignatureService";
}
