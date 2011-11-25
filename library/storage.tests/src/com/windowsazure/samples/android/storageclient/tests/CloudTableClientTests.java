package com.windowsazure.samples.android.storageclient.tests;

import java.net.URISyntaxException;
import java.util.Iterator;

import org.apache.http.HttpStatus;

import junit.framework.Assert;

import com.windowsazure.samples.android.storageclient.CloudTableClient;
import com.windowsazure.samples.android.storageclient.StorageException;

public abstract class CloudTableClientTests<T extends CloudClientAccountProvider> extends CloudTableClientBasedTest<T> {

	public void testWhenQueryTablesShouldReturnTableList() throws Exception {
		try {
			client.createTableIfNotExist("TableClientTestsList1");
			client.createTableIfNotExist("TableClientTestsList2");
			client.createTableIfNotExist("TableClientTestsList3");
	
			boolean table1 = false, table2 = false, table3 = false;
			Iterator<String> tables = client.listTables().iterator();
			while (tables.hasNext()) {
				String table = tables.next();
				table1 = table1 || table.equalsIgnoreCase("TableClientTestsList1");
				table2 = table2 || table.equalsIgnoreCase("TableClientTestsList2");
				table3 = table3 || table.equalsIgnoreCase("TableClientTestsList3");
			}
	
			Assert.assertTrue(table1 && table2 && table3);
		} catch (Exception e) {
			throw e;
		} finally {
			client.deleteTableIfExist("TableClientTestsList1");
			client.deleteTableIfExist("TableClientTestsList2");
			client.deleteTableIfExist("TableClientTestsList3");
		}
	}

	public void testWhenQueryPrefixTablesShouldReturnTableList() throws Exception {
		try {
			client.createTableIfNotExist("PreTableClientTestsList1");
			client.createTableIfNotExist("PreTableClientTestsList2");
			client.createTableIfNotExist("NPreTableClientTestsList3");
	
			boolean returnedListOk = true; int count = 0;
			Iterator<String> tables = client.listTables("PreTableClientTestsList").iterator();
			while (tables.hasNext()) {
				String table = tables.next();
				returnedListOk = returnedListOk && table.startsWith("PreTableClientTestsList");
				count++;
			}
	
			Assert.assertTrue(returnedListOk);
			Assert.assertTrue(count >= 2);
		} catch (Exception e) {
			throw e;
		} finally {
			client.deleteTableIfExist("PreTableClientTestsList1");
			client.deleteTableIfExist("PreTableClientTestsList2");
			client.deleteTableIfExist("NPreTableClientTestsList3");
		}
	}

	public void testWhenCreateTableShouldBeAdded() throws Exception {
		String testTableName = "TableClientTestsAdd";
		
		try {
			client.deleteTableIfExist(testTableName);		
			Assert.assertFalse(client.doesTableExist(testTableName));
			
			client.createTable(testTableName);
			Assert.assertTrue(client.doesTableExist(testTableName));
		} catch (Exception e) {
			throw e;
		} finally {
			client.deleteTableIfExist(testTableName);
		}
	}

	public void testWhenCreateDuplicatedTableShouldThrow() throws Exception {
		String testTableName = "TableClientTestsDupAdd";

		try {
			client.deleteTableIfExist(testTableName);
			Assert.assertFalse(client.doesTableExist(testTableName));
	
			boolean errorThrown = false;
			client.createTable(testTableName);
			Assert.assertTrue(client.doesTableExist(testTableName));
			try {
				client.createTable(testTableName);
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

	public void testWhenDeleteTableShouldBeRemoved() throws Exception {
		String testTableName = "TableClientTestsDelete";

		try {
			client.createTable(testTableName);
	
			Assert.assertTrue(client.doesTableExist(testTableName));
	
			client.deleteTable(testTableName);
	
			Assert.assertFalse(client.doesTableExist(testTableName));
		} catch (Exception e) {
			throw e;
		} finally {
			client.deleteTableIfExist(testTableName);
		}
	}

	public void testWhenDeleteNonExistentTableShouldThrow() throws Exception {
		String testTableName = "TableClientTestsDeleteNotExist";

		Assert.assertFalse(client.doesTableExist(testTableName));

		boolean errorThrown = false;
		try {
			client.deleteTable(testTableName);
		} catch (StorageException e) {
			errorThrown = e.m_HttpStatusCode == HttpStatus.SC_NOT_FOUND || e.m_HttpStatusCode == HttpStatus.SC_UNAUTHORIZED;
		}

		Assert.assertTrue(errorThrown);
	}
	
	public void testWhenFindExistentShouldReturnTrue() throws URISyntaxException, Exception {
		String testTableName = "TableClientTestsFindExist";

		try {
		client.createTableIfNotExist(testTableName);		
		Assert.assertTrue(client.doesTableExist(testTableName));
		} catch (Exception e) {
			throw e;
		} finally {
			client.deleteTableIfExist(testTableName);
		}
	}

	public void testWhenFindNotExistentShouldReturnFalse() throws URISyntaxException, Exception {
		String testTableName = "TableClientTestsFindNotExist";
			
		Assert.assertFalse(client.doesTableExist(testTableName));
	}

	public void testWhenCreateFromModelShouldCreateTableWithTypeName() throws Exception {
		String testTableName = "TestTableEntity";

		try {
			client.deleteTableIfExist(testTableName);
			Assert.assertFalse(client.doesTableExist(testTableName));
	
			CloudTableClient.CreateTableFromModel(TestTableEntity.class, client.getEndpoint().toASCIIString(), client.getCredentials());
			Assert.assertTrue(client.doesTableExist(testTableName));				
		} catch (Exception e) {
			throw e;
		} finally {
			client.deleteTableIfExist(testTableName);
		}
	}
	
}
