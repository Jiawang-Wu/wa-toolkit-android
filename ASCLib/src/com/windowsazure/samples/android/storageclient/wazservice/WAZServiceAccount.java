package com.windowsazure.samples.android.storageclient.wazservice;

import java.net.URI;

import javax.security.auth.login.LoginException;

import com.windowsazure.samples.android.storageclient.NotImplementedException;
import com.windowsazure.samples.android.storageclient.StorageCredentials;
import com.windowsazure.samples.android.storageclient.CloudBlobClient;
import com.windowsazure.samples.android.storageclient.internal.web.XmlHttp;
import com.windowsazure.samples.android.storageclient.internal.web.XmlHttpResult;
import com.windowsazure.samples.android.storageclient.internal.xml.DOMAdapter;
import com.windowsazure.samples.android.storageclient.internal.xml.DOMBuilder;
import com.windowsazure.samples.android.storageclient.internal.xml.XmlNode;

public class WAZServiceAccount {

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

	private class LoginRequestDOMBuilder extends DOMBuilder {
		
		public LoginRequestDOMBuilder(String username, String password) {
			this.username = username;
			this.password = password;
		}
		
		@Override
		protected void buildDOM() {
			XmlNode loginNode = addRootNode(LOGIN_NODE_NAME);
			addDefaultNamespace(loginNode, CREDENTIAL_NS);
			addNamespace(loginNode, "i", INSTANCE_NS);
			
			addTextNode(loginNode, PASSWORD_NODE_NAME , password);
			addTextNode(loginNode, USERNAME_NODE_NAME , username);
		}
		
		private static final String CREDENTIAL_NS = "http://schemas.datacontract.org/2004/07/Microsoft.Samples.WindowsPhoneCloud.StorageClient.Credentials";
		private static final String INSTANCE_NS = "http://www.w3.org/2001/XMLSchema-instance";
		private static final String LOGIN_NODE_NAME = "Login";
		private static final String PASSWORD_NODE_NAME = "Password";
		private static final String USERNAME_NODE_NAME = "UserName";
		
		private String password;
		private String username;
	}
	
	private class LoginResponse {
		public boolean isAuthenticated;
		public String token;
	}
	
	private class LoginResponseDOMAdapter extends DOMAdapter<LoginResponse> {
		
		public LoginResponseDOMAdapter(String xmlString) {
			super(xmlString);
		}

		@Override
		public LoginResponse build() {
			LoginResponse result = new LoginResponse();
			result.isAuthenticated = false;
			
			try
			{
				result.token = getRootNode().getInnerText();
				result.isAuthenticated = result.token != null;
			}
			catch (Exception e)
			{
			} 
			
			return result;
		}
	}
	
	public String loginToWAZService() throws Exception {
		String loginXmlString = new LoginRequestDOMBuilder(this.m_WazServiceData.getUsername(),
				this.m_WazServiceData.getPassword()).getXmlString(true);
		String path = this.m_WazServiceBaseUri.getPath() + LOGIN_PATH;
		XmlHttpResult result = XmlHttp.PostSSL(this.m_WazServiceBaseUri.getHost(), path, null, loginXmlString);
		if (result.getStatusCode() == com.windowsazure.samples.android.storageclient.internal.web.HttpStatusCode.OK)
		{
			LoginResponse response = new LoginResponseDOMAdapter(result.getXmlString()).build();
			if (response.isAuthenticated)
			{
				return response.token;
			}
			else
			{
				throw new LoginException("Couldn't log-in to the WAZ Service: Invalid username or password");
			}
		}
		throw new LoginException("Couldn't log-in to the WAZ Service: The login request returned ".concat(result.getStatusCode().toString()));
	}

	public StorageCredentials getCredentials() throws Exception {
		return new WAZServiceAccountCredentials(this.loginToWAZService());
	}

	private URI getBlobEndpoint() {
		return this.m_WazServiceBaseUri;
	}

	private static final String LOGIN_PATH = "/AuthenticationService/login";
	private static final String SHARED_ACCESS_SIGNATURE_SERVICE_PATH = "/SharedAccessSignatureService";
}
