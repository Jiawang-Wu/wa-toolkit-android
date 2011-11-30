package com.microsoft.samples.windowsazure.android.accesscontrol.sampleapp;

import java.util.ArrayList;
import java.util.List;
import java.util.Map.Entry;

import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.microsoft.samples.windowsazure.android.accesscontrol.core.IAccessToken;
import com.microsoft.samples.windowsazure.android.accesscontrol.core.IdentityProvidersRepository;
import com.microsoft.samples.windowsazure.android.accesscontrol.login.AccessControlLoginContext;
import com.microsoft.samples.windowsazure.android.accesscontrol.security.AccessControlSecuredActivity;
import com.microsoft.samples.windowsazure.android.accesscontrol.swt.SimpleWebTokenHandler;

public class SecuredActivity extends AccessControlSecuredActivity {

	public static final String PREFERENCE_FILENAME = "acs_sample.preferences";
	public static final String PREFERENCE_ACCESS_TOKEN_KEY = "access_token";

	@Override
	protected boolean shouldAuthenticate() {
		return true;
	}

	@Override
	protected void onCreateCompleted(Bundle savedInstanceState) {
		setContentView(R.layout.secured);	
		
		IAccessToken token = this.getAccessControlToken();
		List<String> claims = new ArrayList<String>();
		for(Entry<String, String> entry : token.getClaims().entrySet())
			claims.add(String.format("%s: %s", entry.getKey(), entry.getValue()));
		
		ListView claimList = (ListView) findViewById(R.id.claimList);
		claimList.setAdapter(new ArrayAdapter<String>(this, R.layout.claim_item, claims)); 
	}

	@Override
	protected void onTokenExpiredWarning() {
		// TODO Auto-generated method stub		
	}

	@Override
	protected String getPreferencesFileName() {
		return PREFERENCE_FILENAME;
	}

	@Override
	protected String getPreferencesTokenKey() {
		return PREFERENCE_ACCESS_TOKEN_KEY;
	}

	@Override
	protected AccessControlLoginContext getLoginContext() {		
    	String acsNamespace = getString(R.string.cloud_ready_acs_namespace);
    	String acsRealm = getString(R.string.cloud_ready_acs_realm);
    	String acsSymmKey = getString(R.string.cloud_ready_acs_symmetric_key);

		AccessControlLoginContext loginContext = new AccessControlLoginContext();
    	loginContext.IdentityProviderRepository = new IdentityProvidersRepository(
    			String.format("https://%s.accesscontrol.windows.net/v2/metadata/IdentityProviders.js?protocol=javascriptnotify&realm=%s&version=1.0", acsNamespace, acsRealm));
    	loginContext.AccessTokenHandler = new SimpleWebTokenHandler(acsRealm, acsSymmKey);
    	loginContext.SuccessLoginActivity = this.getClass();
    	loginContext.ErrorLoginActivity = this.getClass();
    	
    	return loginContext;
	}
	
}
