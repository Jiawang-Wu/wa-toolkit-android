package com.windowsazure.samples.android.storageclient;

import java.util.HashMap;

public class BlobContainerPermissions {

	public BlobContainerPublicAccessType publicAccess;

	private HashMap m_SharedAccessPolicies;

	public BlobContainerPermissions() {
		publicAccess = BlobContainerPublicAccessType.OFF;
		m_SharedAccessPolicies = new HashMap();
	}
	public HashMap getSharedAccessPolicies() {
		return m_SharedAccessPolicies;
	}
}
