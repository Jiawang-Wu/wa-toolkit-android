package com.microsoft.samples.windowsazure.android.accesscontrol.login;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;

public class AccessControlLoginActivity extends Activity {
		
	public static final String AccessControlLoginContextKey = "AccessControlLoginContext";
	public static final String AuthenticationTokenKey = "AuthenticationToken";
	public static final String AuthenticationTokenStatusKey = "AuthenticationTokenStatus";
	
	public static final int CANNOTLOADIPLIST = 1;
	public static final int IPLISTEMPTY = 2;
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
    	
    	super.onCreate(savedInstanceState);
    	
    	AccessControlLoginContext loginContext = null;
		Bundle extras = getIntent().getExtras(); 
		if(extras != null) {
			loginContext = (AccessControlLoginContext)extras.getSerializable(AccessControlLoginContextKey);
		}  	
    	
    	setContentView(new AccessControlLoginView(this, loginContext));
    }
    
    protected Dialog onCreateDialog(int id)
    {
	    AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this);
    	dialogBuilder.setTitle("Login error");
	    dialogBuilder.setNeutralButton("Ok", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface arg0, int arg1) {
            	finish();
            }
        });
	
	    switch(id) {
		    case CANNOTLOADIPLIST:
		    	dialogBuilder.setMessage("Cannot load identity provider list correctly, please try again later...");
		    	break;
		    case IPLISTEMPTY:
		    	dialogBuilder.setMessage("Cannot load identity provider list or no identity providers configured to log in, please verify it and try again later...");
		    	break;		    	
	    }
	
	    return dialogBuilder.create();
    }
    
}
