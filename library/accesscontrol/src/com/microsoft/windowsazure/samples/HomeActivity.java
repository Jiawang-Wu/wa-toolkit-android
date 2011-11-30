package com.microsoft.windowsazure.samples;

import android.app.Activity;
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
    
    private void doLogin(){  
		Intent intent = new Intent(this, AccessControlLoginActivity.class);

		AccessControlLoginContext loginContext = new AccessControlLoginContext();
    	loginContext.IdentityProviderRepository = new IdentityProvidersRepository("https://wazmobiletoolkitdev.accesscontrol.windows.net/v2/metadata/IdentityProviders.js?protocol=javascriptnotify&realm=uri:windowsazure.storage.phone&version=1.0");
    	loginContext.AccessTokenHandler = new SimpleWebTokenHandler("uri:windowsazure.storage.phone", "WeB19TTI3qalIhMfDuAwjowtTvsnmNFQvQqQzc6YoVI=");
    	loginContext.SuccessLoginActivity = SuccessfulLoginActivity.class;
    	loginContext.ErrorLoginActivity = UnsuccessfulLoginActivity.class;
    	intent.putExtra(AccessControlLoginActivity.AccessControlLoginContextKey, loginContext);	
	
    	startActivity(intent);    	
    }
    
}