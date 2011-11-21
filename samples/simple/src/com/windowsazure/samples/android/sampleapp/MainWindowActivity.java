package com.windowsazure.samples.android.sampleapp;

import java.net.URI;

import com.windowsazure.samples.android.storageclient.CloudClientAccount;
import com.windowsazure.samples.android.storageclient.CloudStorageAccount;
import com.windowsazure.samples.android.storageclient.StorageCredentialsAccountAndKey;
import com.windowsazure.samples.android.storageclient.wazservice.WAZServiceAccount;
import com.windowsazure.samples.android.storageclient.wazservice.WAZServiceUsernameAndPassword;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.Window;

public class MainWindowActivity extends Activity {
	
	static final int MISSING_CONNECTION_TYPE = 0;
	static final int MISSING_DIRECT_PARAMETERS = 1;
	static final int MISSING_CLOUDREADY_ACS_PARAMETERS = 2;
	static final int MISSING_CLOUDREADY_SIMPLE_PARAMETERS = 3;
	
	public enum ConnectionType { DIRECT, CLOUDREADYACS, CLOUDREADYSIMPLE, NOVALUE;		
		public static ConnectionType toConnectionType(String str)
	    {
	        try {
	            return valueOf(str.toUpperCase());
	        } 
	        catch (Exception ex) {
	            return NOVALUE;
	        }
	    }  
	}
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    	this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        
        String connectionType = getString(R.string.toolkit_connection_type);

        if (!isValidConfigurationValue(connectionType)) {
        	showDialog(MISSING_CONNECTION_TYPE);
        	return;
        }
        
        CloudClientAccount cloudClientAccount = null;
        
        try
        {
	    	switch(ConnectionType.toConnectionType(connectionType)) {
	    		case DIRECT:
	    			String accountName = getString(R.string.direct_account_name);
	    			String accessKey = getString(R.string.direct_access_key);
	    			
	    			if (!isValidConfigurationValue(accountName) || !isValidConfigurationValue(accessKey)) {
	    				showDialog(MISSING_DIRECT_PARAMETERS);
	    			}
	    			
	    			cloudClientAccount = new CloudStorageAccount(new StorageCredentialsAccountAndKey(accountName, accessKey)); 
	    			break;
	    			
	    		case CLOUDREADYACS:
	    			String namespace = getString(R.string.cloud_ready_acs_namespace);
	    			String realm = getString(R.string.cloud_ready_acs_realm);
	    			String proxyService = getString(R.string.cloud_ready_acs_proxy_service);
	    			
	    			if (!isValidConfigurationValue(namespace) || !isValidConfigurationValue(realm) || !isValidConfigurationValue(proxyService)) {
	    				showDialog(MISSING_CLOUDREADY_ACS_PARAMETERS);
	    			}
	    			break;
	    			
	    		case CLOUDREADYSIMPLE:
	    			String service = getString(R.string.cloud_ready_simple_proxy_service);
	            	// TODO: Ask username and password? Add option to register?
	    			
	    			if (!isValidConfigurationValue(service)) {
	    				showDialog(MISSING_CLOUDREADY_SIMPLE_PARAMETERS);
	    			}
	    			
	    			cloudClientAccount = new WAZServiceAccount(new WAZServiceUsernameAndPassword("admin", "Passw0rd!"),
	    					new URI(service)); 
	    			break;
	    	}
	    	
	    	// Configure the account we'll use to access the storage
	        SampleApplication application = (SampleApplication) this.getApplication();
			application.setCloudClientAccount(cloudClientAccount);

			showStorageSelector();    	
        }
        catch (Exception exception)
        {
        	// TODO: Show an error message
        	exception.printStackTrace();
        }
    }
        
    protected Dialog onCreateDialog(int id) {	
    	AlertDialog dialog = new AlertDialog.Builder(this).create();    	
		dialog.setTitle(getString(R.string.configuration_error_title));
		
    	switch(id) {
    		case MISSING_CONNECTION_TYPE:
    			dialog.setMessage(getString(R.string.error_missing_connection_type));
		    	break;
    		case MISSING_DIRECT_PARAMETERS:
		    	dialog.setMessage(getString(R.string.error_missing_direct_parameters));
		    	break;
    		case MISSING_CLOUDREADY_ACS_PARAMETERS:
		    	dialog.setMessage(getString(R.string.error_missing_cloud_ready_acs_parameters));
		    	break;	
    		case MISSING_CLOUDREADY_SIMPLE_PARAMETERS:
		    	dialog.setMessage(getString(R.string.error_missing_cloud_ready_simple_parameters));
		    	break;
    	}

    	final Activity activity = this;
    	
    	dialog.setButton(DialogInterface.BUTTON_NEGATIVE, "OK", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
            	activity.finish();
            }
        });
    	
		return dialog;
    }
    
    private boolean isValidConfigurationValue(String value) {
    	return !(value.startsWith("{") || value.trim() == "");
    }
    
    private void showStorageSelector(){
    	Intent storageTypeSelector = new Intent(this, StorageTypeSelectorActivity.class);
    	startActivity (storageTypeSelector);
    }

}