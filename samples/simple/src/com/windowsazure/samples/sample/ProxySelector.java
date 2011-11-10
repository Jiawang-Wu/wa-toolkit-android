package com.windowsazure.samples.sample;

import java.net.URISyntaxException;

import com.windowsazure.samples.android.storageclient.CloudBlobClient;
import com.windowsazure.samples.android.storageclient.CloudStorageAccount;
import com.windowsazure.samples.android.storageclient.NotImplementedException;
import com.windowsazure.samples.android.storageclient.StorageCredentialsAccountAndKey;
import com.windowsazure.samples.authentication.AuthenticationToken;
import com.windowsazure.samples.authentication.AuthenticationTokenFactory;
import com.windowsazure.samples.sample.R;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ToggleButton;

public class ProxySelector extends Activity
{

	public static String ACCOUNT = "account";
	public static String ACCESS_KEY = "accesskey";

	public static AuthenticationToken credential;
	public static CloudBlobClient blobClient;

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        Button startButton = (Button)findViewById(R.id.StartButton);
        final Activity activity = this;
        startButton.setOnClickListener(new View.OnClickListener( ) {
        	public void onClick(View view) {try {
				start();
			} catch (Exception e) {
				e.printStackTrace();
				Builder builder = new AlertDialog.Builder(activity);
				builder.setTitle("Credentials Error");
				builder.setMessage("Default values have not been changed. Please set your account and accessKey");
				builder.create().show();
			}}
        	});
    }

    private void start() throws IllegalArgumentException, NotImplementedException, URISyntaxException
	{
        ToggleButton proxySwitch = (ToggleButton)findViewById(R.id.useProxySwitch);
        if (!proxySwitch.isChecked() && (ACCOUNT == "account" || ACCESS_KEY == "accesskey"))
        {
        	this.showDialog(1);
        }
        else
        {
	        if (proxySwitch.isChecked())
	        {
	        	Intent launchProxyLogin = new Intent(this, ProxyLogin.class);
	        	startActivity (launchProxyLogin);
	        }
	        else
	        {
	        	credential = AuthenticationTokenFactory.buildDirectConnectToken(ACCOUNT, ACCESS_KEY);
	        	blobClient = new CloudStorageAccount(new StorageCredentialsAccountAndKey(ACCOUNT, ACCESS_KEY)).createCloudBlobClient(); 
	        	Intent launchStorageTypeSelector = new Intent(this, StorageTypeSelector.class);
	        	startActivity (launchStorageTypeSelector);
	        }
        }
	}

    protected Dialog onCreateDialog(int id)
    {
    	AlertDialog errorOverlay = new AlertDialog.Builder(this).create();
    	errorOverlay.setTitle("Credentials Error");
    	errorOverlay.setMessage("Default values have not been changed. Please set your account and accessKey");
    	return errorOverlay;
    }

}