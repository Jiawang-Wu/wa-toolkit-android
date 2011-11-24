package com.windowsazure.samples.android.storageclient;

public abstract class CloudTableEntity {
	public String PartitionKey;
	public String RowKey;
	
	public void copyKeys(CloudTableEntity e) {
		PartitionKey = e.PartitionKey;
		RowKey = e.RowKey;
	}
}
