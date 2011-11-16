package com.windowsazure.samples.android.storageclient.tests;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URISyntaxException;
import java.util.Iterator;

import org.apache.http.HttpStatus;

import junit.framework.Assert;

import com.windowsazure.samples.android.storageclient.CloudStorageAccount;
import com.windowsazure.samples.android.storageclient.CloudTableClient;
import com.windowsazure.samples.android.storageclient.StorageCredentialsAccountAndKey;
import com.windowsazure.samples.android.storageclient.StorageException;

import android.test.AndroidTestCase;

public class CloudTableClientTests extends AndroidTestCase {

	private final String ACCOUNT_NAME = "panthro";
	private final String ACCOUNT_KEY = "9r3qbPdSzEzp/CQsVm64eY3ntmgJoeVkdeGuI1qJH4xv+JcJiFDFo5aKcinaA9oPdoUzwsHEO17IA8lwZZHGMA==";
	
	public void testWhenQueryTablesShouldReturnTableList() 
			throws IllegalArgumentException, URISyntaxException, UnsupportedEncodingException, StorageException, IOException {
		CloudStorageAccount account = new CloudStorageAccount(new StorageCredentialsAccountAndKey(ACCOUNT_NAME, ACCOUNT_KEY));
		CloudTableClient client = new CloudTableClient(account.getTableEndpoint(), account.getCredentials());
				
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
	}
	
	public void testWhenCreateTableShouldBeAdded() 
			throws IllegalArgumentException, URISyntaxException, UnsupportedEncodingException, StorageException, IOException {
		CloudStorageAccount account = new CloudStorageAccount(new StorageCredentialsAccountAndKey(ACCOUNT_NAME, ACCOUNT_KEY));
		CloudTableClient client = new CloudTableClient(account.getTableEndpoint(), account.getCredentials());
		
		String testTableName = "TableClientTestsAdd";
		client.deleteTableIfExist(testTableName);
		
		Assert.assertFalse(client.doesTableExist(testTableName));
		
		client.createTable(testTableName);
		
		Assert.assertTrue(client.doesTableExist(testTableName));		
	}

	public void testWhenCreateDuplicatedTableShouldThrow() 
			throws IllegalArgumentException, URISyntaxException, UnsupportedEncodingException, StorageException, IOException {
		CloudStorageAccount account = new CloudStorageAccount(new StorageCredentialsAccountAndKey(ACCOUNT_NAME, ACCOUNT_KEY));
		CloudTableClient client = new CloudTableClient(account.getTableEndpoint(), account.getCredentials());
		
		String testTableName = "TableClientTestsDupAdd";
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
	}

	public void testWhenDeleteTableShouldBeRemoved() 
			throws IllegalArgumentException, URISyntaxException, UnsupportedEncodingException, StorageException, IOException {
		CloudStorageAccount account = new CloudStorageAccount(new StorageCredentialsAccountAndKey(ACCOUNT_NAME, ACCOUNT_KEY));
		CloudTableClient client = new CloudTableClient(account.getTableEndpoint(), account.getCredentials());
		
		String testTableName = "TableClientTestsDelete";
		client.createTable(testTableName);
		
		Assert.assertTrue(client.doesTableExist(testTableName));
		
		client.deleteTable(testTableName);
		
		Assert.assertFalse(client.doesTableExist(testTableName));		
	}

	public void testWhenDeleteNonExistentTableShouldThrow() 
			throws IllegalArgumentException, URISyntaxException, UnsupportedEncodingException, StorageException, IOException {
		CloudStorageAccount account = new CloudStorageAccount(new StorageCredentialsAccountAndKey(ACCOUNT_NAME, ACCOUNT_KEY));
		CloudTableClient client = new CloudTableClient(account.getTableEndpoint(), account.getCredentials());

		String testTableName = "TableClientTestsDeleteNotExist";
		Assert.assertFalse(client.doesTableExist(testTableName));

		boolean errorThrown = false;		
		try {
			client.deleteTable(testTableName);		
		} catch (StorageException e) {
			errorThrown = e.m_HttpStatusCode == HttpStatus.SC_NOT_FOUND;
		}
		
		Assert.assertTrue(errorThrown);
	}

}
