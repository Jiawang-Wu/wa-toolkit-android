package com.windowsazure.samples.android.storageclient;

public class BlobContainerPermissions {

	public BlobContainerPublicAccessType publicAccess;

	public BlobContainerPermissions() {
		publicAccess = BlobContainerPublicAccessType.OFF;
	}
}
