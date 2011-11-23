package com.windowsazure.samples.android.sampleapp;

import com.windowsazure.samples.android.storageclient.CloudBlobClient;
import com.windowsazure.samples.android.storageclient.CloudClientAccount;
import com.windowsazure.samples.android.storageclient.CloudQueueClient;
import com.windowsazure.samples.android.storageclient.CloudTableClient;

import android.app.Application;

public class SampleApplication extends Application {

	private CloudBlobClient m_CloudBlobClient;
	private CloudClientAccount m_CloudClientAccount;
	private CloudQueueClient m_CloudQueueClient;
	private CloudTableClient m_CloudTableClient;
	private ConnectionType m_ConnectionType;
	
	public enum ConnectionType {
		DIRECT, CLOUDREADYACS, CLOUDREADYSIMPLE, NOVALUE;

		public static ConnectionType toConnectionType(String str) {
			try {
				return valueOf(str.toUpperCase());
			} catch (Exception ex) {
				return NOVALUE;
			}
		}
	}
	
    public CloudBlobClient getCloudBlobClient() throws Exception {
        if (m_CloudBlobClient == null) {
        	m_CloudBlobClient = this.getCloudClientAccount().createCloudBlobClient();
        }
        
        return m_CloudBlobClient;
    }
    
    public CloudQueueClient getCloudQueueClient() throws Exception {
        if (m_CloudQueueClient == null) {
        	m_CloudQueueClient = this.getCloudClientAccount().createCloudQueueClient();
        }
        
        return m_CloudQueueClient;
    }
    
    public CloudTableClient getCloudTableClient() throws Exception {
        if (m_CloudTableClient == null) {
        	m_CloudTableClient = this.getCloudClientAccount().createCloudTableClient();
        }
        
        return m_CloudTableClient;
    }
	
	public CloudClientAccount getCloudClientAccount() throws Exception {
		if (m_CloudClientAccount == null) {
			throw new Exception("You need to set a Cloud Client Account before being able to access it.");
		}
		
		return m_CloudClientAccount;
	}
	
	public ConnectionType getConnectionType() {
		if (m_ConnectionType == null) {
			m_ConnectionType = ConnectionType.toConnectionType(getString(R.string.toolkit_connection_type));
		}
		
		return m_ConnectionType;
	}
	
    public void setCloudClientAccount(CloudClientAccount cloudClientAccount) {
		m_CloudClientAccount = cloudClientAccount;
	}	
	
}
