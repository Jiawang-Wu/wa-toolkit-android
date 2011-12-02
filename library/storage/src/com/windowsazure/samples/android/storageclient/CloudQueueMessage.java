package com.windowsazure.samples.android.storageclient;

import java.sql.Timestamp;

public class CloudQueueMessage {
	public static final long MaxMessageSize = 8 * 1024; // 8 kb
	public static final int MaxTimeToLiveInSeconds = 7 * 24 * 60 * 60; //7 Days;
	public static final int MaxNumberOfMessagesToPeek = 32;

	private String m_MessageId;
	private byte[] m_RawContent;
	private Timestamp m_InsertionTime;
	private Timestamp m_ExpirationTime;
	private int m_DequeueCount;
	private String m_PopReceipt;
	private Timestamp m_NextVisibleTime;

	/**
	Initializes a new instance of the CloudQueueMessage class with the given byte array.
	@param content the content of the message
	*/
	public CloudQueueMessage(byte[] content) {
		this(null, content, null, null, 0);
	}

	/**
	Initializes a new instance of the CloudQueueMessage class with the given string.
	@param content the content of the message
	*/
	public CloudQueueMessage(String content) {
		this(content.getBytes());
	}

	CloudQueueMessage(String messageId, byte[] rawContent,
			Timestamp insertionTime, Timestamp expirationTime, int dequeueCount) {
		this(messageId, rawContent, insertionTime, expirationTime, dequeueCount, null, null);
	}

	CloudQueueMessage(String messageId, byte[] rawContent,
			Timestamp insertionTime, Timestamp expirationTime, int dequeueCount, String popReceipt, Timestamp nextVisibleTime) {
		m_MessageId = messageId;
		m_RawContent = rawContent;
		m_InsertionTime = insertionTime;
		m_ExpirationTime = expirationTime;
		m_DequeueCount = dequeueCount;
		m_PopReceipt = popReceipt;
		m_NextVisibleTime = nextVisibleTime;
	}

	/**
	Gets the content of the message as a byte array.
	@return byte[]
	*/
	public byte[] getAsBytes() {
		return m_RawContent;
	}

	/**
	Gets the message ID.
	@return String
	*/
	public String getId() {
		return m_MessageId;
	}

	/**
	Gets the message's pop receipt.
	@return String
	*/
	public String getPopReceipt() {
		return m_PopReceipt;
	}

	/**
	Gets the time that the message was added to the queue.
	@return Timestamp
	*/
	public Timestamp getInsertionTime() {
		return m_InsertionTime;
	}

	/**
	Gets the time that the message expires.
	@return Timestamp
	*/
	public Timestamp getExpirationTime() {
		return m_ExpirationTime;
	}

	/**
	Gets the time that the message will next be visible.
	@return Timestamp
	*/
	public Timestamp getNextVisibleTime() {
		return m_NextVisibleTime;
	}

	/**
	Gets the content of the message, as a string.
	@return String
	*/
	public String getAsString() {
		return new String(this.getAsBytes());
	}

	/**
	Gets the number of times this message has been dequeued.
	@return int
	*/
	public int getDequeueCount() {
		return m_DequeueCount;
	}

	/**
	Gets a string representation of all the information contained in this object
	@return String
	*/
	public String toString() {
		return String.format("Id: %s\nContent: %s\nInsertion Time: %s\nExpiration Time: %s\nDequeue Count: %s\nPop Receipt: %s\nNext Visible Time: %s\n",
				m_MessageId,
				this.getAsString(),
				m_InsertionTime,
				m_ExpirationTime,
				m_DequeueCount,
				m_PopReceipt,
				m_NextVisibleTime);
	}
}
