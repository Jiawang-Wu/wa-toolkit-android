package com.windowsazure.samples.android.storageclient.tests;

public class TestTableEntityEx extends TestTableEntity {
	public int Value1;
	public int Value2;
	public String ExtraInfo;
	
	public void copy(TestTableEntity e) {
		PartitionKey = e.PartitionKey;
		RowKey = e.RowKey;
		Description = e.Description;
		Flag = e.Flag;
		Count = e.Count;
	}
}
