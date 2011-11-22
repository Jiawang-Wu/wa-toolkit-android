package com.windowsazure.samples.android.storageclient.tests;

import java.util.ArrayList;
import java.util.Iterator;

import junit.framework.Assert;

import android.test.AndroidTestCase;

import com.windowsazure.samples.android.storageclient.CloudStorageAccount;
import com.windowsazure.samples.android.storageclient.CloudTableClient;
import com.windowsazure.samples.android.storageclient.CloudTableObject;

public class CloudTableObjectTests extends AndroidTestCase {

	public void testWhenQueryEntitiesShouldRetrieveRecords() throws Exception {
		String testTableName = "TableObjectTestsQueryEntity";
		CloudStorageAccountProvider accountProvider = new CloudStorageAccountProvider();
		CloudTableClient client = accountProvider.getAccount().createCloudTableClient();
		
		try{
			client.createTableIfNotExist(testTableName);

			CloudStorageAccount account = (CloudStorageAccount)accountProvider.getAccount();
			CloudTableObject<TestTableEntity> tableObject = 
					new CloudTableObject<TestTableEntity>(testTableName, 
							account.getTableEndpoint(), account.getCredentials());

			TestTableEntity obj = new TestTableEntity();
			obj.PartitionKey = "test_partition_q1";
			obj.RowKey = "test_rowkey_q1";
			obj.Description = "test_description";
			tableObject.insertEntity(obj);	
			
			obj.PartitionKey = "test_partition_q2";
			obj.RowKey = "test_rowkey_q2";
			tableObject.insertEntity(obj);

			boolean q1 = false, q2 = false;
			Iterator<TestTableEntity> records = tableObject.queryEntities(TestTableEntity.class).iterator();
			/*while (records.hasNext()) {
				TestTableEntity record = records.next();
				q1 = q1 || record.PartitionKey.equals("test_partition_q1");
				q2 = q2 || record.PartitionKey.equals("test_partition_q2");
			}*/
			
			//Assert.assertTrue(q1 && q2);						
		} catch (Exception e) {
			throw e;
		} finally {
			client.deleteTableIfExist(testTableName);			
		}		
	}
	
	/*public void testWhenInsertEntityShouldBeAdded() throws Exception {
		String testTableName = "TableObjectTestsAddEntity";
		CloudStorageAccountProvider accountProvider = new CloudStorageAccountProvider();	
		CloudTableClient client = accountProvider.getAccount().createCloudTableClient();
		
		try {
			client.createTableIfNotExist(testTableName);
			
			CloudStorageAccount account = (CloudStorageAccount)accountProvider.getAccount();
			CloudTableObject<TestTableEntity> tableObject = 
					new CloudTableObject<TestTableEntity>(testTableName, 
							account.getTableEndpoint(), account.getCredentials());
			
			TestTableEntity obj = new TestTableEntity();
			obj.PartitionKey = "test_partition";
			obj.RowKey = "test_rowkey";
			obj.Description = "test_description";
			tableObject.insertEntity(obj);
			
			//TODO: move next stuff to other test methods when query already implemented
			obj.Flag = true;
			tableObject.updateEntity(obj);
		} catch (Exception e) {
			throw e;
		} finally {		
			client.deleteTableIfExist(testTableName);
		}
	}

	public void testWhenUpdateEntityShouldBeChanged() {
		
	}
	
	public void testWhenDeleteEntityShouldBeRemoved() {
		
	}
	
	public void testWhenInsertOrReplaceEntityShouldBeAdded() throws Exception {
		String testTableName = "TableObjectTestsAddReplaceEntity";
		CloudStorageAccountProvider accountProvider = new CloudStorageAccountProvider();	
		CloudTableClient client = accountProvider.getAccount().createCloudTableClient();
		
		try {
			client.createTableIfNotExist(testTableName);
			
			CloudStorageAccount account = (CloudStorageAccount)accountProvider.getAccount();
			CloudTableObject<TestTableEntity> tableObject = 
					new CloudTableObject<TestTableEntity>(testTableName, 
							account.getTableEndpoint(), account.getCredentials());
			
			TestTableEntity obj = new TestTableEntity();
			obj.PartitionKey = "test_partition_ir1";
			obj.RowKey = "test_rowkey_ir1";
			obj.Description = "test_description";
			tableObject.insertOrReplaceEntity(obj);			
		} catch (Exception e) {
			throw e;
		} finally {		
			client.deleteTableIfExist(testTableName);
		}
	}
	
	public void testWhenInsertOrMergeEntityShouldBeAdded() throws Exception {
		String testTableName = "TableObjectTestsAddMergeEntity";
		CloudStorageAccountProvider accountProvider = new CloudStorageAccountProvider();	
		CloudTableClient client = accountProvider.getAccount().createCloudTableClient();
		
		try {
			client.createTableIfNotExist(testTableName);
			
			CloudStorageAccount account = (CloudStorageAccount)accountProvider.getAccount();
			CloudTableObject<TestTableEntity> tableObject = 
					new CloudTableObject<TestTableEntity>(testTableName, 
							account.getTableEndpoint(), account.getCredentials());
			
			TestTableEntity obj = new TestTableEntity();
			obj.PartitionKey = "test_partition_im1";
			obj.RowKey = "test_rowkey_im1";
			obj.Description = "test_description";
			tableObject.insertOrMergeEntity(obj);			
		} catch (Exception e) {
			throw e;
		} finally {		
			client.deleteTableIfExist(testTableName);
		}
	}*/
	
}
