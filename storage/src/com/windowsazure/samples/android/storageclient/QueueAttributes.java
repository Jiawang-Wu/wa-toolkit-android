package com.windowsazure.samples.android.storageclient;

import java.net.URI;
import java.util.HashMap;
import java.util.Map;

public class QueueAttributes {
	private Map<String, String> m_Metadata;
	private URI m_Uri;

	public QueueAttributes() {
		m_Metadata = new HashMap<String, String>();
	}

	public QueueAttributes(QueueAttributes other) {
		this(other.getUri(), other.getMetadata());
	}

	public QueueAttributes(URI uri, Map<String, String> metadata) {
		m_Uri = uri;
		m_Metadata = new HashMap<String, String>();
		m_Metadata.putAll(metadata);
	}

	public Map<String, String> getMetadata() {
		return m_Metadata;
	}

	public URI getUri() {
		return m_Uri;
	}
}
