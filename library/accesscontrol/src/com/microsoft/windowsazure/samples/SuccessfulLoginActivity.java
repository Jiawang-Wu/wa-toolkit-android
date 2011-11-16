package com.microsoft.windowsazure.samples;

import com.microsoft.samples.windowsazure.android.accesscontrol.core.IAccessToken;
import com.microsoft.samples.windowsazure.android.accesscontrol.login.AccessControlLoginActivity;

import android.app.Activity;
import android.os.Bundle;
import android.widget.TextView;

public class SuccessfulLoginActivity extends Activity {
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.endmain);
		
    	IAccessToken accessToken = null;
		Bundle extras = getIntent().getExtras(); 
		if(extras != null) {
			accessToken = (IAccessToken)extras.getSerializable(AccessControlLoginActivity.AuthenticationTokenKey);
		}  	

		TextView text = (TextView) findViewById(R.id.textViewToken);
		text.setText(accessToken.getRawToken());
    }

}
