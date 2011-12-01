package com.windowsazure.samples.sample;

import java.net.URI;

import com.windowsazure.samples.android.storageclient.wazservice.WAZServiceAccount;
import com.windowsazure.samples.android.storageclient.wazservice.WAZServiceUsernameAndPassword;
import com.windowsazure.samples.authentication.AuthenticationTokenFactory;
import com.windowsazure.samples.sample.R;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

public class ProxyLogin extends Activity {

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.proxylogin);
        Button loginButton = (Button)findViewById(R.id.LoginButton);
        loginButton.setOnClickListener(new View.OnClickListener( ) {
        	public void onClick(View view) {login();}
        	});
    }

    private void login() {
    	EditText proxyField = (EditText)findViewById(R.id.proxyURLField);
    	EditText usernameField = (EditText)findViewById(R.id.usernameField);
    	EditText passwordField = (EditText)findViewById(R.id.passwordField);
    	try {
			String host = proxyField.getText().toString();
			String username = usernameField.getText().toString();
			String password = passwordField.getText().toString();
			ProxySelector.credential = AuthenticationTokenFactory.buildProxyToken(host, username, password);
			ProxySelector.blobClient = new WAZServiceAccount(
					new WAZServiceUsernameAndPassword(username, password),
					new URI("https://" + host)).createCloudBlobClient();
	    	Intent launchStorageTypeSelector = new Intent(this, StorageTypeSelector.class);
	    	startActivity (launchStorageTypeSelector);
		}
    	catch (Exception e) {
    		AlertDialog ad = new AlertDialog.Builder(this).create();
    		ad.setCancelable(false); // This blocks the 'BACK' button
    		ad.setMessage(e.toString());
    		ad.setButton("OK", new DialogInterface.OnClickListener() {
    		    @Override
    		    public void onClick(DialogInterface dialog, int which) {
    		        dialog.dismiss();
    		    }
    		});
    		ad.show();
    		System.out.println(e.toString());
		}
	}
}
