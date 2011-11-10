package com.windowsazure.samples.android.storageclient;

import java.util.HashMap;

public class BlobContainerPermissions {

	public BlobContainerPublicAccessType publicAccess;

	public BlobContainerPermissions() {
		publicAccess = BlobContainerPublicAccessType.OFF;
	}
}
