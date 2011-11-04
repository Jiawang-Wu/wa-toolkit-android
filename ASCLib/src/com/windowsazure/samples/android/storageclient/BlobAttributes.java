package com.windowsazure.samples.android.storageclient;

import java.net.URI;
import java.util.HashMap;

final class BlobAttributes {

	protected HashMap metadata;

	protected BlobProperties properties;
	protected URI uri;
	public String snapshotID;
	public BlobAttributes() {
		metadata = new HashMap();
		properties = new BlobProperties();
	}
}
