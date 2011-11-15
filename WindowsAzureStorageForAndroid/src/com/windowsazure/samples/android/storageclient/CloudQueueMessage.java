package com.windowsazure.samples.android.storageclient;

import java.sql.Timestamp;

public class CloudQueueMessage {
	public static final long MaxMessageSize = 0;
	public static final int MaxTimeToLiveInMilliseconds = 0;
	public static final int MaxNumberOfMessagesToPeek = 0;

	public CloudQueueMessage(byte[] content) {

	}

	public CloudQueueMessage(String content) {

	}

	public byte[] getAsBytes() {
		return null;
	}

	public String getId() {
		return null;
	}

	public String getPopReceipt() {
		return null;
	}

	public Timestamp getInsertionTime() {
		return null;
	}

	public Timestamp getExpirationTime() {
		return null;
	}

	public Timestamp getNextVisibleTime() {
		return null;
	}

	public String getAsString() {
		return null;
	}

	public int getDequeueCount() {
		return 0;
	}
}
