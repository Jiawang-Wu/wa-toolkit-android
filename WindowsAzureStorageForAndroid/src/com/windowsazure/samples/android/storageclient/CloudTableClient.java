package com.windowsazure.samples.android.storageclient;

import java.sql.Timestamp;

import android.net.Uri;

public class CloudTableClient
{
    public CloudTableClient(String baseAddress, StorageCredentials credentials)
    {
    	
    }
    public Timestamp getMinSupportedDateTime()
    {
    	return null;
    }
    public Uri getBaseUri()
    {
    	return null;
    }
    public StorageCredentials getCredentials()
    {
    	return null;
    }
    //public static void CreateTablesFromModel(Type serviceContextType, String baseAddress, StorageCredentials credentials);
    //public TableServiceContext GetDataServiceContext();
    //public void Attach(DataServiceContext serviceContext);
    public void createTable(String tableName)
    {
    }
    public boolean createTableIfNotExist(String tableName)
    {
    	return false;
    }
    public boolean doesTableExist(String tableName)
    {
    	return false;
    }
    public Iterable<String> listTables()
    {
    	return null;
    }
    public Iterable<String> listTables(String prefix)
    {
    	return null;
    }
    public void deleteTable(String tableName)
    {
    }
    public boolean deleteTableIfExist(String tableName)
    {
    	return false;
    }
}
