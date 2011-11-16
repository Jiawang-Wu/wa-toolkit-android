package com.windowsazure.samples.android.storageclient;

import java.net.URI;
import java.net.URISyntaxException;
import java.security.InvalidKeyException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map.Entry;

public final class CloudStorageAccount implements CloudClientAccount {

	protected static final String ACCOUNT_KEY_NAME = "AccountKey";

	protected static final String ACCOUNT_NAME_NAME = "AccountName";

	//private static final String BLOB_BASE_DNS_NAME = "blob.core.windows.net";

	protected static final String BLOB_ENDPOINT_NAME = "BlobEndpoint";

	//private static final String DEFAULT_ENDPOINTS_PROTOCOL_NAME = "DefaultEndpointsProtocol";

	//private static final String DEVELOPMENT_STORAGE_PROXY_URI_NAME = "DevelopmentStorageProxyUri";

	//private static final String DEVSTORE_ACCOUNT_KEY = "Eby8vdM02xNOcqFlqUwJPLlmEtlCDXJ1OUzFT50uSRZ6IFsuFq2UVErCz4I6tq/K1SZFPTOtr/KBHBeksoGMGw==";

	//private static final String DEVSTORE_ACCOUNT_NAME = "devstoreaccount1";

	//private static final String DEVSTORE_CREDENTIALS_IN_STRING = "AccountName=devstoreaccount1;AccountKey=Eby8vdM02xNOcqFlqUwJPLlmEtlCDXJ1OUzFT50uSRZ6IFsuFq2UVErCz4I6tq/K1SZFPTOtr/KBHBeksoGMGw==";

	private static CloudStorageAccount m_devStoreAccount;

	protected static final String QUEUE_BASE_DNS_NAME = "queue.core.windows.net";

	protected static final String QUEUE_ENDPOINT_NAME = "QueueEndpoint";

	protected static final String SHARED_ACCESS_SIGNATURE_NAME = "SharedAccessSignature";

	protected static final String TABLE_BASE_DNS_NAME = "table.core.windows.net";

	protected static final String TABLE_ENDPOINT_NAME = "TableEndpoint";

	//private static final String USE_DEVELOPMENT_STORAGE_NAME = "UseDevelopmentStorage";

	private static String getDefaultBlobEndpoint(HashMap<String, String> configuration) {
		String defaultEndpointsProtocol = configuration.get("DefaultEndpointsProtocol") == null ? "http"
				: (String) configuration.get("DefaultEndpointsProtocol");
		String accountName = configuration.get("AccountName") == null ? null
				: (String) configuration.get("AccountName");
		return getDefaultBlobEndpoint(defaultEndpointsProtocol, accountName);
	}

	private static String getDefaultBlobEndpoint(String scheme, String accountName) {
		return String.format("%s://%s.%s", new Object[] { scheme, accountName,
		"blob.core.windows.net" });
	}

	public static String getDefaultQueueEndpoint(HashMap<String, String> configuration) {
		String defaultEndpointsProtocol = configuration.get("DefaultEndpointsProtocol") == null ? "http"
				: (String) configuration.get("DefaultEndpointsProtocol");
		String accountName = configuration.get("AccountName") == null ? null
				: (String) configuration.get("AccountName");
		return getDefaultQueueEndpoint(defaultEndpointsProtocol, accountName);
	}

	public static String getDefaultQueueEndpoint(String scheme, String accountName) {
		return String.format("%s://%s.%s", new Object[] { scheme, accountName,
				"queue.core.windows.net" });
	}

	private static String getDefaultTableEndpoint(HashMap<String, String> configuration) {
		String defaultEndpointsProtocol = configuration.get("DefaultEndpointsProtocol") == null ? "http"
				: (String) configuration.get("DefaultEndpointsProtocol");
		String accountName = configuration.get("AccountName") == null ? null
				: (String) configuration.get("AccountName");
		return getDefaultTableEndpoint(defaultEndpointsProtocol, accountName);
	}

	private static String getDefaultTableEndpoint(String scheme, String accountName) {
		return String.format("%s://%s.%s", new Object[] { scheme, accountName,
				"table.core.windows.net" });
	}

	public static String getDefaultScheme() {
		return "http";
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
			String schemeWithSeparator = uri.getScheme().concat("://");
			schemeWithSeparator = schemeWithSeparator.concat(uri.getHost());
			return new CloudStorageAccount(
					new StorageCredentialsAccountAndKey(
							"devstoreaccount1",
							"Eby8vdM02xNOcqFlqUwJPLlmEtlCDXJ1OUzFT50uSRZ6IFsuFq2UVErCz4I6tq/K1SZFPTOtr/KBHBeksoGMGw=="),
					new URI(schemeWithSeparator.concat(":10000/devstoreaccount1")), new URI(schemeWithSeparator
							.concat(":10001/devstoreaccount1")), new URI(schemeWithSeparator
							.concat(":10002/devstoreaccount1")));
		}
	}

	protected static HashMap<String, String> parseAccountString(String accountString)
			throws IllegalArgumentException {
		String arguments[] = accountString.split(";");
		HashMap<String, String> argumentsMap = new HashMap<String, String>();
		for (String argument : arguments) {
			int argumentSeparatorIndex = argument.indexOf("=");
			if (argumentSeparatorIndex < 1)
				throw new IllegalArgumentException("Invalid Connection String");
			String name = argument.substring(0, argumentSeparatorIndex);
			String value = argument.substring(argumentSeparatorIndex + 1);
			argumentsMap.put(name, value);
		}

		return argumentsMap;
	}
	
	public static CloudStorageAccount parse(String configurationString)
			throws URISyntaxException, InvalidKeyException,
			IllegalArgumentException, NotImplementedException {
		if (configurationString == null || configurationString.length() == 0)
			throw new IllegalArgumentException("Invalid Connection String");
		HashMap<String, String> accountString = parseAccountString(configurationString);
		for (Iterator<Entry<String, String>> iterator = accountString.entrySet().iterator(); iterator
				.hasNext();) {
			Entry<String, String> entry = iterator.next();
			if (entry.getValue() == null
					|| ((String) entry.getValue()).equals(""))
				throw new IllegalArgumentException("Invalid Connection String");
		}

		CloudStorageAccount account = tryConfigureDevStore(accountString);
		if (account != null)
		{
			return account;
		}
		account = tryConfigureServiceAccount(accountString);
		if (account != null)
		{
			return account;
		}
		else
		{
			throw new IllegalArgumentException("Invalid Connection String");
		}
	}
	
	private static CloudStorageAccount tryConfigureDevStore(HashMap<String, String> configuration)
			throws URISyntaxException, IllegalArgumentException,
			NotImplementedException {
		if (configuration.containsKey("UseDevelopmentStorage")) {
			String s = (String) configuration.get("UseDevelopmentStorage");
			URI uri = null;
			if (!Boolean.parseBoolean(s))
				return null;
			if (configuration.containsKey("DevelopmentStorageProxyUri"))
				uri = new URI(
						(String) configuration.get("DevelopmentStorageProxyUri"));
			return getDevelopmentStorageAccount(uri);
		} else {
			return null;
		}
	}
	private static CloudStorageAccount tryConfigureServiceAccount(
			HashMap<String, String> configuration) throws URISyntaxException, InvalidKeyException,
			IllegalArgumentException, NotImplementedException {
		String defaultEndpointsProtocol = configuration.get("DefaultEndpointsProtocol") == null ? null
				: ((String) configuration.get("DefaultEndpointsProtocol"))
						.toLowerCase();
		if (defaultEndpointsProtocol != null && !defaultEndpointsProtocol.equals("http") && !defaultEndpointsProtocol.equals("https"))
			return null;
		StorageCredentials credentials = StorageCredentials
				.tryParseCredentials(configuration);
		URI blobEndpoint = configuration.containsKey("BlobEndpoint") ? new URI(
				(String) configuration.get("BlobEndpoint")) : null;
		URI queueEndpoint = configuration.containsKey("QueueEndpoint") ? new URI(
				(String) configuration.get("QueueEndpoint")) : null;
		URI tableEndpoint = configuration.containsKey("TableEndpoint") ? new URI(
				(String) configuration.get("TableEndpoint")) : null;
		if (credentials != null) {
			if (defaultEndpointsProtocol != null && configuration.containsKey("AccountName")
					&& configuration.containsKey("AccountKey"))
				return new CloudStorageAccount(credentials,
						blobEndpoint != null ? blobEndpoint : new URI(
								getDefaultBlobEndpoint(configuration)),
						queueEndpoint != null ? queueEndpoint : new URI(
								getDefaultQueueEndpoint(configuration)),
						tableEndpoint != null ? tableEndpoint : new URI(
								getDefaultTableEndpoint(configuration)));
			if (configuration.containsKey("BlobEndpoint")
					|| configuration.containsKey("QueueEndpoint")
					|| configuration.containsKey("TableEndpoint"))
				return new CloudStorageAccount(credentials, blobEndpoint, queueEndpoint,
						tableEndpoint);
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
	public CloudStorageAccount(StorageCredentials credentials, URI blobEndpoint,
			URI queueEndpoint, URI tableEndpoint) {
		m_Credentials = credentials;
		m_BlobEndpoint = blobEndpoint;
		m_QueueEndpoint = queueEndpoint;
		m_TableEndpoint = tableEndpoint;
	}
	public CloudStorageAccount(
			StorageCredentialsAccountAndKey accountAndKey,
			boolean useHttps) throws URISyntaxException {
		this((accountAndKey), new URI(
				getDefaultBlobEndpoint(useHttps ? "https"
						: "http",
						accountAndKey.getAccountName())),
				new URI(getDefaultQueueEndpoint(
						useHttps ? "https" : "http",
						accountAndKey.getAccountName())),
				new URI(getDefaultTableEndpoint(
						useHttps ? "https" : "http",
						accountAndKey.getAccountName())));
	}

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
	
	public CloudQueueClient createCloudQueueClient()
			throws NotImplementedException, URISyntaxException {
		if (getQueueEndpoint() == null)
			throw new IllegalArgumentException("No queue endpoint configured.");
		if (m_Credentials == null)
			throw new IllegalArgumentException("No credentials provided.");
		if (!m_Credentials.canCredentialsSignRequest())
			throw new IllegalArgumentException(
					"CloudQueueClient requires a credential that can sign request");
		else
			return new CloudQueueClient(getQueueEndpoint(), getCredentials());
	}

	public URI getBlobEndpoint() {
		if (getCredentials() instanceof StorageCredentialsSharedAccessSignature)
		{
			throw new IllegalArgumentException(
					"Endpoint information not available for Account using Shared Access Credentials.");
		}
		else
		{
			return m_BlobEndpoint;
		}
	}
	public StorageCredentials getCredentials() {
		return m_Credentials;
	}
	public URI getQueueEndpoint() {
		if (getCredentials() instanceof StorageCredentialsSharedAccessSignature)
		{
			throw new IllegalArgumentException(
					"Endpoint information not available for Account using Shared Access Credentials.");
		}
		else
		{
			return m_QueueEndpoint;
		}
	}
	public URI getTableEndpoint() {
		if (getCredentials() instanceof StorageCredentialsSharedAccessSignature)
		{
			throw new IllegalArgumentException(
					"Endpoint information not available for Account using Shared Access Credentials.");
		}
		else
		{
			return m_TableEndpoint;
		}
	}
	protected void setCredentials(StorageCredentials storagecredentials) {
		m_Credentials = storagecredentials;
	}
	@Override
	public String toString() {
		return toString(false);
	}
	public String toString(boolean showSignature) {
		if (m_Credentials != null
				&& Utility.isNullOrEmpty(m_Credentials.getAccountName()))
			return m_Credentials.toString(showSignature);
		ArrayList<String> configurationList = new ArrayList<String>();
		if (this == m_devStoreAccount)
			configurationList.add(String.format("%s=true",
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
			configurationList.add(String.format("%s=true",
					new Object[] { "UseDevelopmentStorage" }));
			configurationList.add(String.format("%s=%s://%s",
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
			configurationList.add(String
					.format("%s=%s", new Object[] { "DefaultEndpointsProtocol",
							getBlobEndpoint().getScheme() }));
			if (getCredentials() != null)
				configurationList.add(getCredentials().toString(showSignature));
		} else {
			if (getBlobEndpoint() != null)
				configurationList.add(String.format("%s=%s", new Object[] {
						"BlobEndpoint", getBlobEndpoint() }));
			if (getQueueEndpoint() != null)
				configurationList.add(String.format("%s=%s", new Object[] {
						"QueueEndpoint", getQueueEndpoint() }));
			if (getTableEndpoint() != null)
				configurationList.add(String.format("%s=%s", new Object[] {
						"TableEndpoint", getTableEndpoint() }));
			if (getCredentials() != null)
				configurationList.add(getCredentials().toString(showSignature));
		}
		StringBuilder stringBuilder = new StringBuilder();
		for (Iterator<String> iterator = configurationList.iterator(); iterator.hasNext(); stringBuilder
				.append(';')) {
			String configurationLine = (String) iterator.next();
			stringBuilder.append(configurationLine);
		}

		if (configurationList.size() > 0)
			stringBuilder.deleteCharAt(stringBuilder.length() - 1);
		return stringBuilder.toString();
	}
}
