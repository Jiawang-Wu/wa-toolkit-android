package com.windowsazure.samples.android.storageclient;

import java.net.URI;
import java.util.HashMap;

final class BlobContainerAttributes {

	protected HashMap<String, String> metadata;

	protected BlobContainerProperties properties;
	protected String name;
	protected URI uri;
	protected BlobContainerAttributes() {
		metadata = new HashMap<String, String>();
		properties = new BlobContainerProperties();
	}
}
