package com.windowsazure.samples.android.storageclient;

import java.net.URI;
import java.net.URISyntaxException;
import java.security.InvalidKeyException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

public final class CloudStorageAccount implements CloudClientAccount {

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

	private static String getDefaultBlobEndpoint(HashMap hashmap) {
		String s = hashmap.get("DefaultEndpointsProtocol") == null ? "http"
				: (String) hashmap.get("DefaultEndpointsProtocol");
		String s1 = hashmap.get("AccountName") == null ? null
				: (String) hashmap.get("AccountName");
		return getDefaultBlobEndpoint(s, s1);
	}

	private static String getDefaultBlobEndpoint(String s, String s1) {
		return String.format("%s://%s.%s", new Object[] { s, s1,
				"blob.core.windows.net" });
	}

	private static String getDefaultQueueEndpoint(HashMap hashmap) {
		String s = hashmap.get("DefaultEndpointsProtocol") == null ? "http"
				: (String) hashmap.get("DefaultEndpointsProtocol");
		String s1 = hashmap.get("AccountName") == null ? null
				: (String) hashmap.get("AccountName");
		return getDefaultQueueEndpoint(s, s1);
	}

	private static String getDefaultQueueEndpoint(String s, String s1) {
		return String.format("%s://%s.%s", new Object[] { s, s1,
				"queue.core.windows.net" });
	}

	private static String getDefaultTableEndpoint(HashMap hashmap) {
		String s = hashmap.get("DefaultEndpointsProtocol") == null ? "http"
				: (String) hashmap.get("DefaultEndpointsProtocol");
		String s1 = hashmap.get("AccountName") == null ? null
				: (String) hashmap.get("AccountName");
		return getDefaultTableEndpoint(s, s1);
	}

	private static String getDefaultTableEndpoint(String s, String s1) {
		return String.format("%s://%s.%s", new Object[] { s, s1,
				"table.core.windows.net" });
	}

	public static CloudStorageAccount getDevelopmentStorageAccount()
			throws IllegalArgumentException, NotImplementedException {
		if (m_devStoreAccount == null)
			try {
				m_devStoreAccount = getDevelopmentStorageAccount(new URI(
						"http://127.0.0.1"));
			} catch (URISyntaxException urisyntaxexception) {
			}
		return m_devStoreAccount;
	}
	public static CloudStorageAccount getDevelopmentStorageAccount(URI uri)
			throws URISyntaxException, IllegalArgumentException,
			NotImplementedException {
		if (uri == null) {
			return getDevelopmentStorageAccount();
		} else {
			String s = uri.getScheme().concat("://");
			s = s.concat(uri.getHost());
			return new CloudStorageAccount(
					new StorageCredentialsAccountAndKey(
							"devstoreaccount1",
							"Eby8vdM02xNOcqFlqUwJPLlmEtlCDXJ1OUzFT50uSRZ6IFsuFq2UVErCz4I6tq/K1SZFPTOtr/KBHBeksoGMGw=="),
					new URI(s.concat(":10000/devstoreaccount1")), new URI(s
							.concat(":10001/devstoreaccount1")), new URI(s
							.concat(":10002/devstoreaccount1")));
		}
	}
	public static CloudStorageAccount parse(String s)
			throws URISyntaxException, InvalidKeyException,
			IllegalArgumentException, NotImplementedException {
		if (s == null || s.length() == 0)
			throw new IllegalArgumentException("Invalid Connection String");
		HashMap hashmap = Utility.parseAccountString(s);
		for (Iterator iterator = hashmap.entrySet().iterator(); iterator
				.hasNext();) {
			java.util.Map.Entry entry = (java.util.Map.Entry) iterator.next();
			if (entry.getValue() == null
					|| ((String) entry.getValue()).equals(""))
				throw new IllegalArgumentException("Invalid Connection String");
		}

		CloudStorageAccount cloudstorageaccount = tryConfigureDevStore(hashmap);
		if (cloudstorageaccount != null)
			return cloudstorageaccount;
		cloudstorageaccount = tryConfigureServiceAccount(hashmap);
		if (cloudstorageaccount != null)
			return cloudstorageaccount;
		else
			throw new IllegalArgumentException("Invalid Connection String");
	}
	private static CloudStorageAccount tryConfigureDevStore(HashMap hashmap)
			throws URISyntaxException, IllegalArgumentException,
			NotImplementedException {
		if (hashmap.containsKey("UseDevelopmentStorage")) {
			String s = (String) hashmap.get("UseDevelopmentStorage");
			URI uri = null;
			if (!Boolean.parseBoolean(s))
				return null;
			if (hashmap.containsKey("DevelopmentStorageProxyUri"))
				uri = new URI(
						(String) hashmap.get("DevelopmentStorageProxyUri"));
			return getDevelopmentStorageAccount(uri);
		} else {
			return null;
		}
	}
	private static CloudStorageAccount tryConfigureServiceAccount(
			HashMap hashmap) throws URISyntaxException, InvalidKeyException,
			IllegalArgumentException, NotImplementedException {
		String s = hashmap.get("DefaultEndpointsProtocol") == null ? null
				: ((String) hashmap.get("DefaultEndpointsProtocol"))
						.toLowerCase();
		if (s != null && !s.equals("http") && !s.equals("https"))
			return null;
		StorageCredentials storagecredentials = StorageCredentials
				.tryParseCredentials(hashmap);
		URI uri = hashmap.containsKey("BlobEndpoint") ? new URI(
				(String) hashmap.get("BlobEndpoint")) : null;
		URI uri1 = hashmap.containsKey("QueueEndpoint") ? new URI(
				(String) hashmap.get("QueueEndpoint")) : null;
		URI uri2 = hashmap.containsKey("TableEndpoint") ? new URI(
				(String) hashmap.get("TableEndpoint")) : null;
		if (storagecredentials != null) {
			if (s != null && hashmap.containsKey("AccountName")
					&& hashmap.containsKey("AccountKey"))
				return new CloudStorageAccount(storagecredentials,
						uri != null ? uri : new URI(
								getDefaultBlobEndpoint(hashmap)),
						uri1 != null ? uri1 : new URI(
								getDefaultQueueEndpoint(hashmap)),
						uri2 != null ? uri2 : new URI(
								getDefaultTableEndpoint(hashmap)));
			if (hashmap.containsKey("BlobEndpoint")
					|| hashmap.containsKey("QueueEndpoint")
					|| hashmap.containsKey("TableEndpoint"))
				return new CloudStorageAccount(storagecredentials, uri, uri1,
						uri2);
		}
		return null;
	}
	private URI m_BlobEndpoint;
	private StorageCredentials m_Credentials;
	private URI m_QueueEndpoint;
	private URI m_TableEndpoint;
	public CloudStorageAccount(StorageCredentials storagecredentials)
			throws URISyntaxException {
		m_Credentials = storagecredentials;
		m_BlobEndpoint = new URI(getDefaultBlobEndpoint("http",
				m_Credentials.getAccountName()));
		m_QueueEndpoint = new URI(getDefaultQueueEndpoint("http",
				m_Credentials.getAccountName()));
		m_TableEndpoint = new URI(getDefaultTableEndpoint("http",
				m_Credentials.getAccountName()));
	}
	public CloudStorageAccount(StorageCredentials storagecredentials, URI uri,
			URI uri1, URI uri2) {
		m_Credentials = storagecredentials;
		m_BlobEndpoint = uri;
		m_QueueEndpoint = uri1;
		m_TableEndpoint = uri2;
	}
	public CloudStorageAccount(
			StorageCredentialsAccountAndKey storagecredentialsaccountandkey,
			boolean boolean1) throws URISyntaxException {
		this((storagecredentialsaccountandkey), new URI(
				getDefaultBlobEndpoint(boolean1 ? "https"
						: "http",
						storagecredentialsaccountandkey.getAccountName())),
				new URI(getDefaultQueueEndpoint(
						boolean1 ? "https" : "http",
						storagecredentialsaccountandkey.getAccountName())),
				new URI(getDefaultTableEndpoint(
						boolean1 ? "https" : "http",
						storagecredentialsaccountandkey.getAccountName())));
	}
	@Override
	public CloudBlobClient createCloudBlobClient()
			throws NotImplementedException {
		if (getBlobEndpoint() == null)
			throw new IllegalArgumentException("No blob endpoint configured.");
		if (m_Credentials == null)
			throw new IllegalArgumentException("No credentials provided.");
		if (!m_Credentials.canCredentialsSignRequest())
			throw new IllegalArgumentException(
					"CloudBlobClient requires a credential that can sign request");
		else
			return new CloudBlobClient(getBlobEndpoint(), getCredentials());
	}
	public URI getBlobEndpoint() {
		if (getCredentials() instanceof StorageCredentialsSharedAccessSignature)
			throw new IllegalArgumentException(
					"Endpoint information not available for Account using Shared Access Credentials.");
		else
			return m_BlobEndpoint;
	}
	public StorageCredentials getCredentials() {
		return m_Credentials;
	}
	public URI getQueueEndpoint() {
		if (getCredentials() instanceof StorageCredentialsSharedAccessSignature)
			throw new IllegalArgumentException(
					"Endpoint information not available for Account using Shared Access Credentials.");
		else
			return m_QueueEndpoint;
	}
	public URI getTableEndpoint() {
		if (getCredentials() instanceof StorageCredentialsSharedAccessSignature)
			throw new IllegalArgumentException(
					"Endpoint information not available for Account using Shared Access Credentials.");
		else
			return m_TableEndpoint;
	}
	protected void setCredentials(StorageCredentials storagecredentials) {
		m_Credentials = storagecredentials;
	}
	@Override
	public String toString() {
		return toString(false);
	}
	public String toString(boolean boolean1) {
		if (m_Credentials != null
				&& Utility.isNullOrEmpty(m_Credentials.getAccountName()))
			return m_Credentials.toString(boolean1);
		ArrayList arraylist = new ArrayList();
		if (this == m_devStoreAccount)
			arraylist.add(String.format("%s=true",
					new Object[] { "UseDevelopmentStorage" }));
		else if (m_Credentials != null
				&& "devstoreaccount1".equals(m_Credentials.getAccountName())
				&& m_Credentials
						.toString(true)
						.equals("AccountName=devstoreaccount1;AccountKey=Eby8vdM02xNOcqFlqUwJPLlmEtlCDXJ1OUzFT50uSRZ6IFsuFq2UVErCz4I6tq/K1SZFPTOtr/KBHBeksoGMGw==")
				&& m_BlobEndpoint != null
				&& getBlobEndpoint().getHost().equals(
						getQueueEndpoint().getHost())
				&& getQueueEndpoint().getHost().equals(
						getTableEndpoint().getHost())
				&& getBlobEndpoint().getScheme().equals(
						getQueueEndpoint().getScheme())
				&& getQueueEndpoint().getScheme().equals(
						getTableEndpoint().getScheme())) {
			arraylist.add(String.format("%s=true",
					new Object[] { "UseDevelopmentStorage" }));
			arraylist.add(String.format("%s=%s://%s",
					new Object[] { "DevelopmentStorageProxyUri",
							getBlobEndpoint().getScheme(),
							getBlobEndpoint().getHost() }));
		} else if (getBlobEndpoint().getHost()
				.endsWith("blob.core.windows.net")
				&& getQueueEndpoint().getHost().endsWith(
						"queue.core.windows.net")
				&& getTableEndpoint().getHost().endsWith(
						"table.core.windows.net")
				&& getBlobEndpoint().getScheme().equals(
						getQueueEndpoint().getScheme())
				&& getQueueEndpoint().getScheme().equals(
						getTableEndpoint().getScheme())) {
			arraylist.add(String
					.format("%s=%s", new Object[] { "DefaultEndpointsProtocol",
							getBlobEndpoint().getScheme() }));
			if (getCredentials() != null)
				arraylist.add(getCredentials().toString(boolean1));
		} else {
			if (getBlobEndpoint() != null)
				arraylist.add(String.format("%s=%s", new Object[] {
						"BlobEndpoint", getBlobEndpoint() }));
			if (getQueueEndpoint() != null)
				arraylist.add(String.format("%s=%s", new Object[] {
						"QueueEndpoint", getQueueEndpoint() }));
			if (getTableEndpoint() != null)
				arraylist.add(String.format("%s=%s", new Object[] {
						"TableEndpoint", getTableEndpoint() }));
			if (getCredentials() != null)
				arraylist.add(getCredentials().toString(boolean1));
		}
		StringBuilder stringbuilder = new StringBuilder();
		for (Iterator iterator = arraylist.iterator(); iterator.hasNext(); stringbuilder
				.append(';')) {
			String s = (String) iterator.next();
			stringbuilder.append(s);
		}

		if (arraylist.size() > 0)
			stringbuilder.deleteCharAt(stringbuilder.length() - 1);
		return stringbuilder.toString();
	}
}
