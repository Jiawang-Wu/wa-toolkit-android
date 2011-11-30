package com.microsoft.samples.windowsazure.android.accesscontrol.sampleapp;

import android.app.Activity;
import android.os.Bundle;
import android.widget.TextView;

import com.microsoft.samples.windowsazure.android.accesscontrol.login.AccessControlLoginActivity;

public class UnsuccessfulLoginActivity extends Activity {

    @Override
    public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.errormain);
		
		Integer tokenStatus = -1;
    	Bundle extras = getIntent().getExtras(); 
		if (extras != null) {
			tokenStatus = extras.getInt(AccessControlLoginActivity.AuthenticationTokenStatusKey);
		}  	

		TextView text = (TextView) findViewById(R.id.textViewError);
		text.setText("token error status: " + tokenStatus.toString());
    }
    
}
