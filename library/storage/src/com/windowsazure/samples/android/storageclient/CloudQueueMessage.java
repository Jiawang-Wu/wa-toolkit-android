package com.windowsazure.samples.android.storageclient;

import java.sql.Timestamp;

import android.util.Base64;

public class CloudQueueMessage {
	public static final long MaxMessageSize = 8 * 1024; // 8 kb
	public static final int MaxTimeToLiveInSeconds = 7 * 24 * 60 * 60; //7 Days;
	public static final int MaxNumberOfMessagesToPeek = 32;

	private String m_MessageId;
	private String m_RawContent;
	private Timestamp m_InsertionTime;
	private Timestamp m_ExpirationTime;
	private int m_DequeueCount;
	private String m_PopReceipt;
	private Timestamp m_NextVisibleTime;
	private boolean m_isBinaryMessage; 

	public CloudQueueMessage(byte[] content) {
		this(null, Base64.encodeToString(content, Base64.DEFAULT), null, null, 0);
	}

	public CloudQueueMessage(String content) {
		this(content.getBytes());
	}

	public CloudQueueMessage(String messageId, String rawContent,
			Timestamp insertionTime, Timestamp expirationTime, int dequeueCount) {
		this(messageId, rawContent, insertionTime, expirationTime, dequeueCount, null, null);
	}

	public CloudQueueMessage(String messageId, String rawContent,
			Timestamp insertionTime, Timestamp expirationTime, int dequeueCount, String popReceipt, Timestamp nextVisibleTime) {
		m_MessageId = messageId;
		m_RawContent = rawContent;
		m_InsertionTime = insertionTime;
		m_ExpirationTime = expirationTime;
		m_DequeueCount = dequeueCount;
		m_PopReceipt = popReceipt;
		m_NextVisibleTime = nextVisibleTime;
	}

	public byte[] getAsBytes() {
		return Base64.decode(m_RawContent, Base64.DEFAULT);
	}

	public String getId() {
		return m_MessageId;
	}

	public String getPopReceipt() {
		return m_PopReceipt;
	}

	public Timestamp getInsertionTime() {
		return m_InsertionTime;
	}

	public Timestamp getExpirationTime() {
		return m_ExpirationTime;
	}

	public Timestamp getNextVisibleTime() {
		return m_NextVisibleTime;
	}

	public String getAsString() {
		return new String(this.getAsBytes());
	}

	public int getDequeueCount() {
		return m_DequeueCount;
	}
	
	public String toString()
	{
		return String.format("Id: %s\nContent: %s\nInsertion Time: %s\nExpiration Time: %s\nDequeue Count: %s\nPop Receipt: %s\nNext Visible Time: %s\n",
				m_MessageId,
				m_RawContent,
				m_InsertionTime,
				m_ExpirationTime,
				m_DequeueCount,
				m_PopReceipt,
				m_NextVisibleTime);
	}

	public Object getRawContent() {
		return m_RawContent;
	}
}
