package com.windowsazure.samples.android.storageclient.tests;

import java.net.URI;
import java.net.URISyntaxException;

import com.windowsazure.samples.android.storageclient.CloudBlobClient;
import com.windowsazure.samples.android.storageclient.CloudClientAccount;
import com.windowsazure.samples.android.storageclient.wazservice.WAZServiceAccountAcs;

public class WAZACSServiceAccountProvider extends CloudClientAccountProvider {

	String username = "kRmUFsBuyh4mM0I9TDjQdvFdqb6itmuoJABB5sBA3Ng=";
	String email = "kRmUFsBuyh4mM0I9TDjQdvFdqb6itmuoJABB5sBA3Ng=@uri:WindowsLiveID.com";
	String rawToken = "http%3a%2f%2fschemas.xmlsoap.org%2fws%2f2005%2f05%2fidentity%2fclaims%2fnameidentifier=kRmUFsBuyh4mM0I9TDjQdvFdqb6itmuoJABB5sBA3Ng%3d&http%3a%2f%2fschemas.microsoft.com%2faccesscontrolservice%2f2010%2f07%2fclaims%2fidentityprovider=uri%3aWindowsLiveID&Audience=uri%3awazmobiletoolkit&ExpiresOn=1322846250&Issuer=https%3a%2f%2fwazmobiletoolkitdev.accesscontrol.windows.net%2f&HMACSHA256=UHnYLq0h8LfDDqUZ4JGee3a2xfshd%2b89M9UyfWqsnqc%3d";
	String serviceHostString = "https://panthroacs.cloudapp.net";
	
	URI serviceHost() throws URISyntaxException
	{
		return new URI(serviceHostString);
	}
	

	@Override
	public CloudClientAccount getAccount() throws URISyntaxException {
		return new WAZServiceAccountAcs(username, email, rawToken, serviceHost());
	}

	@Override
	public CloudClientAccount getDifferentAccount() throws URISyntaxException {
		return new WAZServiceAccountAcs(username, email, rawToken, serviceHost());
	}

	public CloudBlobClient getCloudBlobClientWithDifferentAccount()
			throws URISyntaxException, Exception {
		return getDifferentAccount().createCloudBlobClient();
	}
}
