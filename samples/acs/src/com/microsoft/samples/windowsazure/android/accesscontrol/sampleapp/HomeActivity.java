package com.microsoft.samples.windowsazure.android.accesscontrol.sampleapp;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

import com.microsoft.samples.windowsazure.android.accesscontrol.core.IdentityProvidersRepository;
import com.microsoft.samples.windowsazure.android.accesscontrol.login.AccessControlLoginActivity;
import com.microsoft.samples.windowsazure.android.accesscontrol.login.AccessControlLoginContext;
import com.microsoft.samples.windowsazure.android.accesscontrol.swt.SimpleWebTokenHandler;

public class HomeActivity extends Activity {
	
	/** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.main);
	
		Button homeButton = (Button) findViewById(R.id.homeButton);
		homeButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {	
				doLogin();
			}
		});
    }
    
    private void doLogin() {
    	String acsNamespace = getString(R.string.cloud_ready_acs_namespace);
    	String acsRealm = getString(R.string.cloud_ready_acs_realm);
    	String acsSymmKey = getString(R.string.cloud_ready_acs_symmetric_key);
    	
    	if (isValidConfigurationValue(acsNamespace) && isValidConfigurationValue(acsRealm) && isValidConfigurationValue(acsSymmKey)) {    	
			Intent intent = new Intent(this, AccessControlLoginActivity.class);
	
			AccessControlLoginContext loginContext = new AccessControlLoginContext();
	    	loginContext.IdentityProviderRepository = new IdentityProvidersRepository(
	    			String.format("https://%s.accesscontrol.windows.net/v2/metadata/IdentityProviders.js?protocol=javascriptnotify&realm=%s&version=1.0", acsNamespace, acsRealm));
	    	loginContext.AccessTokenHandler = new SimpleWebTokenHandler(acsRealm, acsSymmKey);
	    	loginContext.SuccessLoginActivity = SuccessfulLoginActivity.class;
	    	loginContext.ErrorLoginActivity = UnsuccessfulLoginActivity.class;
	    	intent.putExtra(AccessControlLoginActivity.AccessControlLoginContextKey, loginContext);	
		
	    	startActivity(intent);    
    	} else {
    		AlertDialog dialog = new AlertDialog.Builder(this).create();
    		dialog.setTitle("Configuration Error");
    		dialog.setCanceledOnTouchOutside(true);
    		dialog.setCancelable(true);
    		dialog.setMessage("Please review your ACS configuration and try again");
    		dialog.show();
    	}
    }
    
	private boolean isValidConfigurationValue(String value) {
		return !(value.startsWith("{") || value.trim() == "");
	}

}