package com.windowsazure.samples.android.storageclient;

import java.net.URI;
import java.util.HashMap;

final class BlobAttributes {

	protected HashMap<String, String> metadata;

	protected BlobProperties properties;
	protected URI uri;
	public String snapshotID;

	/**
	* Initializes a new instance of the BlobAttributes class
	*/
	public BlobAttributes() {
		metadata = new HashMap<String, String>();
		properties = new BlobProperties();
	}
}
