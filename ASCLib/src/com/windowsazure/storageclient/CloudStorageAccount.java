package com.windowsazure.storageclient;

import java.net.URI;
import java.net.URISyntaxException;
import java.security.InvalidKeyException;
import java.util.*;

public final class CloudStorageAccount
{

    public static CloudStorageAccount getDevelopmentStorageAccount() throws NotImplementedException, NotImplementedException
    {
    	throw new NotImplementedException();
    }

    public static CloudStorageAccount getDevelopmentStorageAccount(URI uri)
        throws NotImplementedException, URISyntaxException
    {
    	throw new NotImplementedException();
    }

    public static CloudStorageAccount parse(String s)
        throws NotImplementedException, URISyntaxException, InvalidKeyException
    {
    	throw new NotImplementedException();
    }

    private static String getDefaultBlobEndpoint(HashMap hashmap) throws NotImplementedException, NotImplementedException
    {
    	throw new NotImplementedException();
    }

    private static String getDefaultBlobEndpoint(String s, String s1) throws NotImplementedException, NotImplementedException
    {
    	throw new NotImplementedException();
    }

    private static String getDefaultQueueEndpoint(HashMap hashmap) throws NotImplementedException, NotImplementedException
    {
    	throw new NotImplementedException();
    }

    private static String getDefaultQueueEndpoint(String s, String s1) throws NotImplementedException, NotImplementedException
    {
    	throw new NotImplementedException();
    }

    private static String getDefaultTableEndpoint(HashMap hashmap) throws NotImplementedException, NotImplementedException
    {
    	throw new NotImplementedException();
    }

    private static String getDefaultTableEndpoint(String s, String s1) throws NotImplementedException, NotImplementedException
    {
    	throw new NotImplementedException();
    }

    private static CloudStorageAccount tryConfigureDevStore(HashMap hashmap)
        throws NotImplementedException, URISyntaxException
    {
    	throw new NotImplementedException();
    }

    private static CloudStorageAccount tryConfigureServiceAccount(HashMap hashmap)
        throws NotImplementedException, URISyntaxException, InvalidKeyException
    {
    	throw new NotImplementedException();
    }

    public CloudStorageAccount(StorageCredentials storagecredentials)
        throws NotImplementedException, URISyntaxException
    {
    	throw new NotImplementedException();
    }

    public CloudStorageAccount(StorageCredentials storagecredentials, URI uri, URI uri1, URI uri2) throws NotImplementedException, NotImplementedException
    {
    	throw new NotImplementedException();
    }

    public CloudStorageAccount(StorageCredentialsAccountAndKey storagecredentialsaccountandkey, Boolean boolean1)
        throws NotImplementedException, URISyntaxException
    {
    	throw new NotImplementedException();
    }

    public CloudBlobClient createCloudBlobClient() throws NotImplementedException, NotImplementedException
    {
    	throw new NotImplementedException();
    }

    public URI getBlobEndpoint() throws NotImplementedException, NotImplementedException
    {
    	throw new NotImplementedException();
    }

    public StorageCredentials getCredentials() throws NotImplementedException, NotImplementedException
    {
    	throw new NotImplementedException();
    }

    public URI getQueueEndpoint() throws NotImplementedException, NotImplementedException
    {
    	throw new NotImplementedException();
    }

    public URI getTableEndpoint() throws NotImplementedException, NotImplementedException
    {
    	throw new NotImplementedException();
    }

    public String toString()
    {
    	return null;
    }

    public String toString(Boolean boolean1) throws NotImplementedException, NotImplementedException
    {
    	throw new NotImplementedException();
    }

    protected void setCredentials(StorageCredentials storagecredentials) throws NotImplementedException, NotImplementedException
    {
    	throw new NotImplementedException();
    }

    protected static final String ACCOUNT_KEY_NAME = "AccountKey";
    protected static final String ACCOUNT_NAME_NAME = "AccountName";
    private static final String BLOB_BASE_DNS_NAME = "blob.core.windows.net";
    protected static final String BLOB_ENDPOINT_NAME = "BlobEndpoint";
    private static final String DEFAULT_ENDPOINTS_PROTOCOL_NAME = "DefaultEndpointsProtocol";
    private static final String DEVELOPMENT_STORAGE_PROXY_URI_NAME = "DevelopmentStorageProxyUri";
    private static final String DEVSTORE_ACCOUNT_KEY = "Eby8vdM02xNOcqFlqUwJPLlmEtlCDXJ1OUzFT50uSRZ6IFsuFq2UVErCz4I6tq/K1SZFPTOtr/KBHBeksoGMGw==";
    private static final String DEVSTORE_ACCOUNT_NAME = "devstoreaccount1";
    private static final String DEVSTORE_CREDENTIALS_IN_STRING = "AccountName=devstoreaccount1;AccountKey=Eby8vdM02xNOcqFlqUwJPLlmEtlCDXJ1OUzFT50uSRZ6IFsuFq2UVErCz4I6tq/K1SZFPTOtr/KBHBeksoGMGw==";
    private static CloudStorageAccount m_devStoreAccount;
    protected static final String QUEUE_BASE_DNS_NAME = "queue.core.windows.net";
    protected static final String QUEUE_ENDPOINT_NAME = "QueueEndpoint";
    protected static final String SHARED_ACCESS_SIGNATURE_NAME = "SharedAccessSignature";
    protected static final String TABLE_BASE_DNS_NAME = "table.core.windows.net";
    protected static final String TABLE_ENDPOINT_NAME = "TableEndpoint";
    private static final String USE_DEVELOPMENT_STORAGE_NAME = "UseDevelopmentStorage";
    private URI m_BlobEndpoint;
    private StorageCredentials m_Credentials;
    private URI m_QueueEndpoint;
    private URI m_TableEndpoint;
}
