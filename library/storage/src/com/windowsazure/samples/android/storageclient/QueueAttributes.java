package com.windowsazure.samples.android.storageclient;

import java.net.URI;
import java.util.HashMap;
import java.util.Map;

public class QueueAttributes {
	private Map<String, String> m_Metadata;
	private URI m_Uri;

	/**
	Initializes a new instance of the QueueAttributes class.
	@see CloudQueue 
	*/
	public QueueAttributes() {
		m_Metadata = new HashMap<String, String>();
	}

	/**
	Initializes a new instance of the QueueAttributes class.
	@see CloudQueue 
	*/
	public QueueAttributes(QueueAttributes other) {
		this(other.getUri(), other.getMetadata());
	}

	/**
	Initializes a new instance of the QueueAttributes class.
	@param uri the URI for the queue.
	@param metadata the queue's user-defined metadata.
	@see CloudQueue 
	*/
	public QueueAttributes(URI uri, Map<String, String> metadata) {
		m_Uri = uri;
		m_Metadata = new HashMap<String, String>();
		m_Metadata.putAll(metadata);
	}

	/**
	Gets the queue's user-defined metadata.
	@return Map<String, String>
	*/
	public Map<String, String> getMetadata() {
		return m_Metadata;
	}

	/**
	Gets the URI for the queue.
	@return URI
	*/
	public URI getUri() {
		return m_Uri;
	}
}
