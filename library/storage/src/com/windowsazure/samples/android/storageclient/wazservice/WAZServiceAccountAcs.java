package com.windowsazure.samples.android.storageclient.wazservice;

import java.net.URI;
import java.net.URISyntaxException;

import com.windowsazure.samples.android.storageclient.CloudBlobClient;
import com.windowsazure.samples.android.storageclient.CloudClientAccount;
import com.windowsazure.samples.android.storageclient.CloudQueueClient;
import com.windowsazure.samples.android.storageclient.CloudTableClient;
import com.windowsazure.samples.android.storageclient.PathUtility;
import com.windowsazure.samples.android.storageclient.StorageCredentials;

public class WAZServiceAccountAcs implements CloudClientAccount {

	private StorageCredentials m_Credentials; 
	private URI m_WazServiceBaseUri;

	public WAZServiceAccountAcs(StorageCredentials credentials, URI wazServiceBaseUri) {
		this.m_Credentials = credentials;
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
	
	public StorageCredentials getCredentials() throws Exception {
		return this.m_Credentials;
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
	
	private static final String SHARED_ACCESS_SIGNATURE_SERVICE_PATH = "/SharedAccessSignatureService";
	private static final String QUEUES_PROXY_SERVICE_PATH = "/AzureQueuesProxy.axd";
	private static final String TABLES_PROXY_SERVICE_PATH = "/AzureTablesProxy.axd";
}