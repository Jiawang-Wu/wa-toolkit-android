package com.windowsazure.samples.test;
import junit.framework.Assert;
import android.test.AndroidTestCase;

import com.windowsazure.samples.authentication.AuthenticationToken;
import com.windowsazure.samples.authentication.AuthenticationTokenFactory;
import com.windowsazure.samples.authentication.NotAuthenticatedException;
import com.windowsazure.samples.internal.authentication.ProxyToken;
import com.windowsazure.samples.table.AzureTableCollection;
import com.windowsazure.samples.table.AzureTableManager;
import com.windowsazure.samples.table.TableReader;


public class AuthenticationTests extends AndroidTestCase {

	public static AuthenticationToken buildDirectConnectToken() {
		return AuthenticationTokenFactory.buildDirectConnectToken(ACCOUNT, KEY);
	}

	public static AuthenticationToken buildProxyToken()
		throws NotAuthenticatedException {

		return AuthenticationTokenFactory.buildProxyToken(PROXY_HOST, PROXY_USERNAME, PROXY_PASSWORD);
	}

	public static AuthenticationToken buildMockToken() {
		return AuthenticationTokenFactory.buildMockToken();
	}

	public void testDirectConnect() {
		try {
			AuthenticationToken token = buildDirectConnectToken();

			TableReader reader = new AzureTableManager(token);
			AzureTableCollection tables = reader.queryTables();
			tables.getTables().size();
			// Assert: this doesn't throw any exception
		}
		catch(Exception e) {
			Assert.fail(e.getMessage());
		}
	}

	public void testProxy() {
		try {
			ProxyToken proxy = (ProxyToken) buildProxyToken();
			String token = proxy.getToken();
			Assert.assertTrue(token.length() > 0);
		}
		catch(Exception e) {
			Assert.fail(e.getMessage());
		}
	}

	private static final String ACCOUNT = "account";
    private static final String KEY = "accesskey";
	private static final String PROXY_HOST = "yourWazServicesBaseURL.cloudapp.net";
	private static final String PROXY_PASSWORD = "s1m0n1";
	private static final String PROXY_USERNAME = "sguest";
}