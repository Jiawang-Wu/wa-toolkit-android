package com.windowsazure.samples.android.storageclient.wazservice;

public class WAZServiceUsernameAndPassword {

	private String m_Username;
	private String m_Password;

	public WAZServiceUsernameAndPassword(String username, String password) {
		this.m_Username = username; 
		this.m_Password = password; 
	}

	public String getPassword() {
		return this.m_Password;
	}
	public String getUsername() {
		return this.m_Username;
	}

}
