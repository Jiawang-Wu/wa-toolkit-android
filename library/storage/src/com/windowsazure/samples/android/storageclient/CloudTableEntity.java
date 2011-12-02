package com.windowsazure.samples.android.storageclient;

/**
Base class for entity classes to be used with CloudTableObject instances.
@see CloudTableObject 
*/	

public abstract class CloudTableEntity {
	public String PartitionKey;
	public String RowKey;
	
	public void copyKeys(CloudTableEntity e) {
		PartitionKey = e.PartitionKey;
		RowKey = e.RowKey;
	}
}
