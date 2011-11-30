package com.microsoft.samples.windowsazure.android.accesscontrol.sampleapp;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.content.SharedPreferences;
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

		setContentView(R.layout.home);
	
		Button manualLoginButton = (Button) findViewById(R.id.manualLoginButton);
		manualLoginButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {	
				doManualLogin();
			}
		});
		
		Button autoLoginButton = (Button) findViewById(R.id.autoLoginButton);
		autoLoginButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {	
				navigateToSecuredView();
			}
		});

		Button resetButton = (Button) findViewById(R.id.resetButton);
		resetButton.setVisibility(hasTokenStored() ? View.VISIBLE : View.INVISIBLE);
		resetButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {	
				resetToken();
			}
		});

    }
    
    @Override
    public void onResume() {
    	super.onResume();
		Button resetButton = (Button) findViewById(R.id.resetButton);
		resetButton.setVisibility(hasTokenStored() ? View.VISIBLE : View.INVISIBLE);
    }
    
    private void doManualLogin() {
    	if (!validateACSConfiguration()) return;
    	
    	String acsNamespace = getString(R.string.cloud_ready_acs_namespace);
    	String acsRealm = getString(R.string.cloud_ready_acs_realm);
    	String acsSymmKey = getString(R.string.cloud_ready_acs_symmetric_key);
    	
		Intent intent = new Intent(this, AccessControlLoginActivity.class);

		AccessControlLoginContext loginContext = new AccessControlLoginContext();
    	loginContext.IdentityProviderRepository = new IdentityProvidersRepository(
    			String.format("https://%s.accesscontrol.windows.net/v2/metadata/IdentityProviders.js?protocol=javascriptnotify&realm=%s&version=1.0", acsNamespace, acsRealm));
    	loginContext.AccessTokenHandler = new SimpleWebTokenHandler(acsRealm, acsSymmKey);
    	loginContext.SuccessLoginActivity = SuccessfulLoginActivity.class;
    	loginContext.ErrorLoginActivity = UnsuccessfulLoginActivity.class;
    	intent.putExtra(AccessControlLoginActivity.AccessControlLoginContextKey, loginContext);	
	
    	startActivity(intent);    
    }
    
    private void navigateToSecuredView() {
    	if (!validateACSConfiguration()) return;

    	Intent intent = new Intent(this, SecuredActivity.class);
    	startActivity(intent);
    }
    
    private boolean hasTokenStored() {
		SharedPreferences settings = getSharedPreferences(SecuredActivity.PREFERENCE_FILENAME, 0);
		String token = settings.getString(SecuredActivity.PREFERENCE_ACCESS_TOKEN_KEY, "");
    	return token != "";
    }
    
    private void resetToken() {
		SharedPreferences settings = getSharedPreferences(SecuredActivity.PREFERENCE_FILENAME, 0);
		SharedPreferences.Editor editor = settings.edit();
		editor.putString(SecuredActivity.PREFERENCE_ACCESS_TOKEN_KEY, "");
		editor.commit();
		Button resetButton = (Button) findViewById(R.id.resetButton);
		resetButton.setVisibility(View.INVISIBLE);
    }
    
    private boolean validateACSConfiguration() {
    	String acsNamespace = getString(R.string.cloud_ready_acs_namespace);
    	String acsRealm = getString(R.string.cloud_ready_acs_realm);
    	String acsSymmKey = getString(R.string.cloud_ready_acs_symmetric_key);

    	if (isValidConfigurationValue(acsNamespace) && isValidConfigurationValue(acsRealm) && isValidConfigurationValue(acsSymmKey))
    		return true;
    	
		AlertDialog dialog = new AlertDialog.Builder(this).create();
		dialog.setTitle("Configuration Error");
		dialog.setCanceledOnTouchOutside(true);
		dialog.setCancelable(true);
		dialog.setMessage("Please review your ACS configuration and try again");
		dialog.show();

		return false;
    }
    
	private boolean isValidConfigurationValue(String value) {
		return !(value.startsWith("{") || value.trim() == "");
	}

}