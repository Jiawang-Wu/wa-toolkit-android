package com.windowsazure.samples.android.storageclient.tests;

import java.net.URISyntaxException;
import java.util.Hashtable;
import java.util.Iterator;

import org.apache.http.HttpStatus;

import junit.framework.Assert;

import android.test.AndroidTestCase;
import com.windowsazure.samples.android.storageclient.CloudStorageAccount;
import com.windowsazure.samples.android.storageclient.CloudTableClient;
import com.windowsazure.samples.android.storageclient.CloudTableObject;
import com.windowsazure.samples.android.storageclient.StorageException;

public class CloudTableObjectTests extends AndroidTestCase {

	public void testWhenQueryUnknownEntitiesShouldRetrieveRecords() throws Exception {
		String testTableName = "TableObjectTestsUnknownEntity";
		CloudStorageAccountProvider accountProvider = new CloudStorageAccountProvider();
		CloudTableClient client = accountProvider.getAccount().createCloudTableClient();
		CloudTableObject<TestTableEntity> tableObject = client.getCloudTableObject(testTableName); 
		
		try{
			client.createTableIfNotExist(testTableName);

			TestTableEntity obj = new TestTableEntity();
			obj.PartitionKey = "test_partition_q1";
			obj.RowKey = "test_rowkey_q1";
			obj.Description = "test_description";
			tableObject.insert(obj);	
			
			obj.PartitionKey = "test_partition_q2";
			obj.RowKey = "test_rowkey_q2";
			tableObject.insert(obj);

			boolean found1 = false, found2 = false;
			Iterable<Hashtable<String, Object>> records = CloudTableObject.query(
					tableObject.getBaseUri(), 
					tableObject.getCredentials(), 
					tableObject.getTableName());
			for (Hashtable<String,Object> record : records) {
				found1 = found1 || record.get("PartitionKey").equals("test_partition_q1");
				found2 = found2 || record.get("PartitionKey").equals("test_partition_q2");
			}
			
			Assert.assertTrue(found1 && found2);						
		} catch (Exception e) {
			throw e;
		} finally {
			client.deleteTableIfExist(testTableName);			
		}		
	}
	
	public void testWhenQueryEntitiesShouldRetrieveRecords() throws Exception {
		String testTableName = "TableObjectTestsQueryEntity";
		CloudStorageAccountProvider accountProvider = new CloudStorageAccountProvider();
		CloudTableClient client = accountProvider.getAccount().createCloudTableClient();
		CloudTableObject<TestTableEntity> tableObject = client.getCloudTableObject(testTableName); 

		try{
			client.createTableIfNotExist(testTableName);

			TestTableEntity obj = new TestTableEntity();
			obj.PartitionKey = "test_partition_q1";
			obj.RowKey = "test_rowkey_q1";
			obj.Description = "test_description";
			tableObject.insert(obj);	
			
			obj.PartitionKey = "test_partition_q2";
			obj.RowKey = "test_rowkey_q2";
			tableObject.insert(obj);

			boolean found1 = false, found2 = false;
			Iterator<TestTableEntity> records = tableObject.query(TestTableEntity.class).iterator();
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
	
	public void testWhenInsertNewEntityShouldBeAdded() throws Exception {
		String testTableName = "TableObjectTestsAddEntity";
		CloudStorageAccountProvider accountProvider = new CloudStorageAccountProvider();	
		CloudTableClient client = accountProvider.getAccount().createCloudTableClient();
		CloudTableObject<TestTableEntity> tableObject = client.getCloudTableObject(testTableName); 
		
		try {
			client.createTableIfNotExist(testTableName);
				
			TestTableEntity obj = new TestTableEntity();
			obj.PartitionKey = "test_partition_a";
			obj.RowKey = "test_rowkey_a";
			obj.Description = "test_description";
			tableObject.insert(obj);
			
			Iterator<TestTableEntity> records = tableObject.query(TestTableEntity.class, "PartitionKey eq 'test_partition_a'").iterator();
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
	
	public void testWhenInsertDuplicatedEntityShouldThrow() throws Exception {
		String testTableName = "TableObjectTestsAddDupEntity";
		CloudStorageAccountProvider accountProvider = new CloudStorageAccountProvider();	
		CloudTableClient client = accountProvider.getAccount().createCloudTableClient();
		CloudTableObject<TestTableEntity> tableObject = client.getCloudTableObject(testTableName); 
		
		try {
			client.createTableIfNotExist(testTableName);
				
			TestTableEntity obj = new TestTableEntity();
			obj.PartitionKey = "test_partition_a";
			obj.RowKey = "test_rowkey_a";
			obj.Description = "test_description";
			tableObject.insert(obj);
			
			boolean errorThrown = false;
			try {
				tableObject.insert(obj);
			} catch (StorageException e) {
				errorThrown = e.m_HttpStatusCode == HttpStatus.SC_CONFLICT;
			}

			Assert.assertTrue(errorThrown);
		} catch (Exception e) {
			throw e;
		} finally {		
			client.deleteTableIfExist(testTableName);
		}		
	}
	
	public void testWhenUpdateEntityShouldBeChanged() throws Exception {
		String testTableName = "TableObjectTestsUpdateEntity";
		CloudStorageAccountProvider accountProvider = new CloudStorageAccountProvider();	
		CloudTableClient client = accountProvider.getAccount().createCloudTableClient();
		CloudTableObject<TestTableEntity> tableObject = client.getCloudTableObject(testTableName); 
		
		try {
			client.createTableIfNotExist(testTableName);
			
			TestTableEntity obj = new TestTableEntity();
			obj.PartitionKey = "test_partition_u";
			obj.RowKey = "test_rowkey_u";
			obj.Description = "test_description";
			tableObject.insert(obj);

			Iterator<TestTableEntity> records = tableObject.query(TestTableEntity.class, "PartitionKey eq 'test_partition_u'").iterator();
			boolean found = false;
			while (records.hasNext()) {
				TestTableEntity record = records.next();
				found = found || record.PartitionKey.equals("test_partition_u");
			}
			Assert.assertTrue(found);
			
			obj.Description = "entity updated";
			tableObject.update(obj);
			
			records = tableObject.query(TestTableEntity.class, "PartitionKey eq 'test_partition_u'").iterator();
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
	
	public void testWhenUpdateNonExistentEntityShouldThrow() throws Exception {
		String testTableName = "TableObjectTestsUpdateNotExEntity";
		CloudStorageAccountProvider accountProvider = new CloudStorageAccountProvider();	
		CloudTableClient client = accountProvider.getAccount().createCloudTableClient();
		CloudTableObject<TestTableEntity> tableObject = client.getCloudTableObject(testTableName); 
		
		try {
			client.createTableIfNotExist(testTableName);
			
			TestTableEntity obj = new TestTableEntity();
			obj.PartitionKey = "test_partition_u";
			obj.RowKey = "test_rowkey_u";
			obj.Description = "test_description";

			boolean errorThrown = false;
			try {
				tableObject.update(obj);
			} catch (StorageException e) {
				errorThrown = e.m_HttpStatusCode == HttpStatus.SC_NOT_FOUND;
			}

			Assert.assertTrue(errorThrown);
		} catch (Exception e) {
			throw e;
		} finally {		
			client.deleteTableIfExist(testTableName);
		}
	}
	
	public void testWhenMergeEntityShouldBeChanged() throws URISyntaxException, Exception {
		String testTableName = "TableObjectTestsMergeEntity";
		CloudStorageAccountProvider accountProvider = new CloudStorageAccountProvider();	
		CloudTableClient client = accountProvider.getAccount().createCloudTableClient();
		CloudTableObject<TestTableEntity> tableObject = client.getCloudTableObject(testTableName); 
		
		try {
			client.createTableIfNotExist(testTableName);
			
			TestTableEntity obj = new TestTableEntity();
			obj.PartitionKey = "test_partition_m";
			obj.RowKey = "test_rowkey_m";
			obj.Description = "test_description";
			tableObject.insert(obj);

			Iterator<TestTableEntity> records = tableObject.query(TestTableEntity.class, "PartitionKey eq 'test_partition_m'").iterator();
			boolean found = false;
			while (records.hasNext()) {
				TestTableEntity record = records.next();
				found = found || record.PartitionKey.equals("test_partition_m");
			}
			Assert.assertTrue(found);
			
			CloudTableObject<TestTableOtherEntity> tableObjectEx = client.getCloudTableObject(testTableName);
			
			TestTableOtherEntity entityEx = new TestTableOtherEntity();
			entityEx.copyKeys(obj);
			entityEx.Value1 = 1;
			entityEx.Value2 = 2;
			entityEx.ExtraInfo = "merged";
			tableObjectEx.merge(entityEx);
			
			Iterable<Hashtable<String, Object>> mergedRecords = CloudTableObject.query(
					tableObject.getBaseUri(), 
					tableObject.getCredentials(), 
					tableObject.getTableName(),
					"PartitionKey eq 'test_partition_m'");
			for(Hashtable<String, Object> record : mergedRecords) {
				Assert.assertTrue(record.get("PartitionKey").equals("test_partition_m"));
				Assert.assertTrue(record.get("Description").equals("test_description"));
				Assert.assertTrue(record.get("ExtraInfo").equals("merged"));
			}
		} catch (Exception e) {
			throw e;
		} finally {		
			client.deleteTableIfExist(testTableName);
		}
	}
	
	public void testWhenMergeNonExistentEntityShouldThrow() throws Exception {
		String testTableName = "TableObjectTestsMergeNotExEntity";
		CloudStorageAccountProvider accountProvider = new CloudStorageAccountProvider();	
		CloudTableClient client = accountProvider.getAccount().createCloudTableClient();
		CloudTableObject<TestTableEntity> tableObject = client.getCloudTableObject(testTableName); 
		
		try {
			client.createTableIfNotExist(testTableName);
			
			TestTableEntity obj = new TestTableEntity();
			obj.PartitionKey = "test_partition_m";
			obj.RowKey = "test_rowkey_m";
			obj.Description = "test_description";

			boolean errorThrown = false;
			try {
				tableObject.merge(obj);
			} catch (StorageException e) {
				errorThrown = e.m_HttpStatusCode == HttpStatus.SC_NOT_FOUND;
			}

			Assert.assertTrue(errorThrown);
		} catch (Exception e) {
			throw e;
		} finally {
			client.deleteTableIfExist(testTableName);
		}
	}	
	
	public void testWhenDeleteEntityShouldBeRemoved() throws URISyntaxException, Exception {
		String testTableName = "TableObjectTestsDeleteEntity";
		CloudStorageAccountProvider accountProvider = new CloudStorageAccountProvider();	
		CloudTableClient client = accountProvider.getAccount().createCloudTableClient();
		CloudTableObject<TestTableEntity> tableObject = client.getCloudTableObject(testTableName); 
		
		try {
			client.createTableIfNotExist(testTableName);
				
			TestTableEntity obj = new TestTableEntity();
			obj.PartitionKey = "test_partition_d";
			obj.RowKey = "test_rowkey_d";
			obj.Description = "test_description";
			tableObject.insert(obj);

			Iterator<TestTableEntity> records = tableObject.query(TestTableEntity.class, "PartitionKey eq 'test_partition_d'").iterator();
			boolean found = false;
			while (records.hasNext()) {
				TestTableEntity record = records.next();
				found = found || record.PartitionKey.equals("test_partition_d");
			}
			Assert.assertTrue(found);
			
			tableObject.delete(obj);
			
			records = tableObject.query(TestTableEntity.class, "PartitionKey eq 'test_partition_d'").iterator();
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
	
	public void testWhenDeleteNonExistentEntityShouldThrow() throws Exception {
		String testTableName = "TableObjectTestsDeleteNotExEntity";
		CloudStorageAccountProvider accountProvider = new CloudStorageAccountProvider();	
		CloudTableClient client = accountProvider.getAccount().createCloudTableClient();
		CloudTableObject<TestTableEntity> tableObject = client.getCloudTableObject(testTableName); 
		
		try {
			client.createTableIfNotExist(testTableName);
				
			TestTableEntity obj = new TestTableEntity();
			obj.PartitionKey = "test_partition_d";
			obj.RowKey = "test_rowkey_d";
			obj.Description = "test_description";

			boolean errorThrown = false;
			try {
				tableObject.delete(obj);
			} catch (StorageException e) {
				errorThrown = e.m_HttpStatusCode == HttpStatus.SC_NOT_FOUND;
			}

			Assert.assertTrue(errorThrown);		
		} catch (Exception e) {
			throw e;
		} finally {		
			client.deleteTableIfExist(testTableName);
		}	
	}
	
	public void testWhenInsertOrReplaceNewEntityShouldBeAdded() throws Exception {
		String testTableName = "TableObjectTestsAddReplaceEntity";
		CloudStorageAccountProvider accountProvider = new CloudStorageAccountProvider();	
		CloudTableClient client = accountProvider.getAccount().createCloudTableClient();
		CloudTableObject<TestTableEntity> tableObject = client.getCloudTableObject(testTableName); 
		
		try {
			client.createTableIfNotExist(testTableName);
			
			TestTableEntity obj = new TestTableEntity();
			obj.PartitionKey = "test_partition_ir";
			obj.RowKey = "test_rowkey_ir";
			obj.Description = "test_description";
			tableObject.insertOrReplace(obj);		
			
			Iterator<TestTableEntity> records = tableObject.query(TestTableEntity.class, "PartitionKey eq 'test_partition_ir'").iterator();
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
		String testTableName = "TableObjectTestsAddReplaceExEntity";
		CloudStorageAccountProvider accountProvider = new CloudStorageAccountProvider();	
		CloudTableClient client = accountProvider.getAccount().createCloudTableClient();
		CloudTableObject<TestTableEntity> tableObject = client.getCloudTableObject(testTableName); 
		
		try {
			client.createTableIfNotExist(testTableName);
			
			TestTableEntity obj = new TestTableEntity();
			obj.PartitionKey = "test_partition_ir";
			obj.RowKey = "test_rowkey_ir";
			obj.Description = "test_description";
			tableObject.insert(obj);
			
			Iterator<TestTableEntity> records = tableObject.query(TestTableEntity.class, "PartitionKey eq 'test_partition_ir'").iterator();
			boolean found = false;
			while (records.hasNext()) {
				TestTableEntity record = records.next();
				found = found || record.PartitionKey.equals("test_partition_ir");
			}
			
			Assert.assertTrue(found);
			
			CloudTableObject<TestTableOtherEntity> tableObjectEx = client.getCloudTableObject(testTableName); 
			
			TestTableOtherEntity entityEx = new TestTableOtherEntity();
			entityEx.copyKeys(obj);
			entityEx.Value1 = 1;
			entityEx.Value2 = 2;
			tableObjectEx.insertOrReplace(entityEx);
			
			Iterator<TestTableOtherEntity> recordsEx = tableObjectEx.query(TestTableOtherEntity.class, "PartitionKey eq 'test_partition_ir'").iterator();
			found = false;
			while (recordsEx.hasNext()) {
				TestTableOtherEntity record = recordsEx.next();
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
			tableObject.insertOrMerge(obj);	
			
			Iterator<TestTableEntity> records = tableObject.query(TestTableEntity.class, "PartitionKey eq 'test_partition_im'").iterator();
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
	
	public void testWhenInsertOrMergeExistentEntityShouldBeMerged() throws Exception {
		String testTableName = "TableObjectTestsAddMergeExEntity";
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
			tableObject.insert(obj);
			
			Iterator<TestTableEntity> records = tableObject.query(TestTableEntity.class, "PartitionKey eq 'test_partition_im'").iterator();
			boolean found = false;
			while (records.hasNext()) {
				TestTableEntity record = records.next();
				found = found || record.PartitionKey.equals("test_partition_im");
			}
			
			Assert.assertTrue(found);
			
			CloudTableObject<TestTableOtherEntity> tableObjectEx = 
					new CloudTableObject<TestTableOtherEntity>(testTableName, 
							account.getTableEndpoint(), account.getCredentials());
			
			TestTableOtherEntity entityEx = new TestTableOtherEntity();
			entityEx.copyKeys(obj);
			entityEx.Value1 = 1;
			entityEx.Value2 = 2;
			tableObjectEx.insertOrMerge(entityEx);
			
			Iterator<TestTableOtherEntity> recordsEx = tableObjectEx.query(TestTableOtherEntity.class, "PartitionKey eq 'test_partition_im'").iterator();
			found = false;
			while (recordsEx.hasNext()) {
				TestTableOtherEntity record = recordsEx.next();
				found = found || record.PartitionKey.equals("test_partition_im");
			}
			
			Assert.assertTrue(found);	
		} catch (Exception e) {
			throw e;
		} finally {		
			client.deleteTableIfExist(testTableName);
		}				
	}
	
}
