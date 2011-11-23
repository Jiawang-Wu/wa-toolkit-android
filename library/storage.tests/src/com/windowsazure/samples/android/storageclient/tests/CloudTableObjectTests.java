package com.windowsazure.samples.android.storageclient.tests;

import java.net.URISyntaxException;
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

			boolean found1 = false, found2 = false;
			Iterator<TestTableEntity> records = tableObject.queryEntities(TestTableEntity.class).iterator();
			while (records.hasNext()) {
				TestTableEntity record = records.next();
				found1 = found1 || record.PartitionKey.equals("test_partition_q1");
				found2 = found2 || record.PartitionKey.equals("test_partition_q2");
			}
			
			Assert.assertTrue(found1 && found2);						
		} catch (Exception e) {
			throw e;
		} finally {
			client.deleteTableIfExist(testTableName);			
		}		
	}
	
	public void testWhenInsertEntityShouldBeAdded() throws Exception {
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
			obj.PartitionKey = "test_partition_a";
			obj.RowKey = "test_rowkey_a";
			obj.Description = "test_description";
			tableObject.insertEntity(obj);
			
			Iterator<TestTableEntity> records = tableObject.queryEntities(TestTableEntity.class, "PartitionKey eq 'test_partition_a'").iterator();
			boolean found = false;
			while (records.hasNext()) {
				TestTableEntity record = records.next();
				found = found || record.PartitionKey.equals("test_partition_a");
			}
			
			Assert.assertTrue(found);
		} catch (Exception e) {
			throw e;
		} finally {		
			client.deleteTableIfExist(testTableName);
		}
	}
	
	public void testWhenInsertDuplicatedEntityShouldThrow() {
		
	}
	
	public void testWhenUpdateEntityShouldBeChanged() throws Exception {
		String testTableName = "TableObjectTestsUpdateEntity";
		CloudStorageAccountProvider accountProvider = new CloudStorageAccountProvider();	
		CloudTableClient client = accountProvider.getAccount().createCloudTableClient();
		
		try {
			client.createTableIfNotExist(testTableName);
			
			CloudStorageAccount account = (CloudStorageAccount)accountProvider.getAccount();
			CloudTableObject<TestTableEntity> tableObject = 
					new CloudTableObject<TestTableEntity>(testTableName, 
							account.getTableEndpoint(), account.getCredentials());
			
			TestTableEntity obj = new TestTableEntity();
			obj.PartitionKey = "test_partition_u";
			obj.RowKey = "test_rowkey_u";
			obj.Description = "test_description";
			tableObject.insertEntity(obj);

			Iterator<TestTableEntity> records = tableObject.queryEntities(TestTableEntity.class, "PartitionKey eq 'test_partition_u'").iterator();
			boolean found = false;
			while (records.hasNext()) {
				TestTableEntity record = records.next();
				found = found || record.PartitionKey.equals("test_partition_u");
			}
			Assert.assertTrue(found);
			
			obj.Description = "entity updated";
			tableObject.updateEntity(obj);
			
			records = tableObject.queryEntities(TestTableEntity.class, "PartitionKey eq 'test_partition_u'").iterator();
			boolean updated = false;
			while (records.hasNext()) {
				TestTableEntity record = records.next();
				updated = updated || (record.PartitionKey.equals("test_partition_u") && record.Description.equals("entity updated"));
			}
			Assert.assertTrue(updated);			
		} catch (Exception e) {
			throw e;
		} finally {		
			client.deleteTableIfExist(testTableName);
		}
	}
	
	public void testWhenUpdateNonExistentEntityShouldThrow() {
		
	}
	
	public void testWhenDeleteEntityShouldBeRemoved() throws URISyntaxException, Exception {
		String testTableName = "TableObjectTestsDeleteEntity";
		CloudStorageAccountProvider accountProvider = new CloudStorageAccountProvider();	
		CloudTableClient client = accountProvider.getAccount().createCloudTableClient();
		
		try {
			client.createTableIfNotExist(testTableName);
			
			CloudStorageAccount account = (CloudStorageAccount)accountProvider.getAccount();
			CloudTableObject<TestTableEntity> tableObject = 
					new CloudTableObject<TestTableEntity>(testTableName, 
							account.getTableEndpoint(), account.getCredentials());
			
			TestTableEntity obj = new TestTableEntity();
			obj.PartitionKey = "test_partition_d";
			obj.RowKey = "test_rowkey_d";
			obj.Description = "test_description";
			tableObject.insertEntity(obj);

			Iterator<TestTableEntity> records = tableObject.queryEntities(TestTableEntity.class, "PartitionKey eq 'test_partition_d'").iterator();
			boolean found = false;
			while (records.hasNext()) {
				TestTableEntity record = records.next();
				found = found || record.PartitionKey.equals("test_partition_d");
			}
			Assert.assertTrue(found);
			
			tableObject.deleteEntity(obj);
			
			records = tableObject.queryEntities(TestTableEntity.class, "PartitionKey eq 'test_partition_d'").iterator();
			found = false;
			while (records.hasNext()) {
				TestTableEntity record = records.next();
				found = found || (record.PartitionKey.equals("test_partition_d"));
			}
			Assert.assertFalse(found);			
		} catch (Exception e) {
			throw e;
		} finally {		
			client.deleteTableIfExist(testTableName);
		}
	}
	
	public void testWhenDeleteNonExistentEntityShouldThrow() {
		
	}
	
	public void testWhenInsertOrReplaceNewEntityShouldBeAdded() throws Exception {
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
			obj.PartitionKey = "test_partition_ir";
			obj.RowKey = "test_rowkey_ir";
			obj.Description = "test_description";
			tableObject.insertOrReplaceEntity(obj);		
			
			Iterator<TestTableEntity> records = tableObject.queryEntities(TestTableEntity.class, "PartitionKey eq 'test_partition_ir'").iterator();
			boolean found = false;
			while (records.hasNext()) {
				TestTableEntity record = records.next();
				found = found || record.PartitionKey.equals("test_partition_ir");
			}
			
			Assert.assertTrue(found);
		} catch (Exception e) {
			throw e;
		} finally {		
			client.deleteTableIfExist(testTableName);
		}
	}

	public void testWhenInsertOrReplaceExistentEntityShouldBeReplaced() throws URISyntaxException, Exception {
		String testTableName = "TableObjectTestsAddReplaceEntity2";
		CloudStorageAccountProvider accountProvider = new CloudStorageAccountProvider();	
		CloudTableClient client = accountProvider.getAccount().createCloudTableClient();
		
		try {
			client.createTableIfNotExist(testTableName);
			
			CloudStorageAccount account = (CloudStorageAccount)accountProvider.getAccount();
			CloudTableObject<TestTableEntity> tableObject = 
					new CloudTableObject<TestTableEntity>(testTableName, 
							account.getTableEndpoint(), account.getCredentials());
			
			TestTableEntity obj = new TestTableEntity();
			obj.PartitionKey = "test_partition_ir";
			obj.RowKey = "test_rowkey_ir";
			obj.Description = "test_description";
			tableObject.insertEntity(obj);
			
			Iterator<TestTableEntity> records = tableObject.queryEntities(TestTableEntity.class, "PartitionKey eq 'test_partition_ir'").iterator();
			boolean found = false;
			while (records.hasNext()) {
				TestTableEntity record = records.next();
				found = found || record.PartitionKey.equals("test_partition_ir");
			}
			
			Assert.assertTrue(found);
			
			CloudTableObject<TestTableEntityEx> tableObjectEx = 
					new CloudTableObject<TestTableEntityEx>(testTableName, 
							account.getTableEndpoint(), account.getCredentials());
			
			TestTableEntityEx entityEx = new TestTableEntityEx();
			entityEx.copy(obj);
			entityEx.Value1 = 1;
			entityEx.Value1 = 2;
			tableObjectEx.insertOrReplaceEntity(entityEx);
			
			Iterator<TestTableEntityEx> recordsEx = tableObjectEx.queryEntities(TestTableEntityEx.class, "PartitionKey eq 'test_partition_ir'").iterator();
			found = false;
			while (records.hasNext()) {
				TestTableEntityEx record = recordsEx.next();
				found = found || record.PartitionKey.equals("test_partition_ir");
			}
			
			Assert.assertTrue(found);	
		} catch (Exception e) {
			throw e;
		} finally {		
			client.deleteTableIfExist(testTableName);
		}		
	}
	
	public void testWhenInsertOrMergeNewEntityShouldBeAdded() throws Exception {
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
			obj.PartitionKey = "test_partition_im";
			obj.RowKey = "test_rowkey_im";
			obj.Description = "test_description";
			tableObject.insertOrMergeEntity(obj);	
			
			Iterator<TestTableEntity> records = tableObject.queryEntities(TestTableEntity.class, "PartitionKey eq 'test_partition_im'").iterator();
			boolean found = false;
			while (records.hasNext()) {
				TestTableEntity record = records.next();
				found = found || record.PartitionKey.equals("test_partition_im");
			}
			
			Assert.assertTrue(found);
		} catch (Exception e) {
			throw e;
		} finally {		
			client.deleteTableIfExist(testTableName);
		}
	}
	
	public void testWhenInsertOrMergeEXistentEntityShouldBeMerged() {
		
	}
	
}
