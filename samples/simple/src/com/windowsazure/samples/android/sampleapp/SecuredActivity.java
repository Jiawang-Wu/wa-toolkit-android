package com.windowsazure.samples.android.sampleapp;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;

import com.microsoft.samples.windowsazure.android.accesscontrol.core.IAccessToken;
import com.microsoft.samples.windowsazure.android.accesscontrol.core.IdentityProvidersRepository;
import com.microsoft.samples.windowsazure.android.accesscontrol.login.AccessControlLoginActivity;
import com.microsoft.samples.windowsazure.android.accesscontrol.login.AccessControlLoginContext;
import com.microsoft.samples.windowsazure.android.accesscontrol.swt.SimpleWebTokenHandler;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Base64;
import android.view.Window;

public class SecuredActivity extends Activity {

	static final int MISSING_CONNECTION_TYPE = 0;
	static final int MISSING_DIRECT_PARAMETERS = 1;
	static final int MISSING_CLOUDREADY_ACS_PARAMETERS = 2;
	static final int MISSING_CLOUDREADY_SIMPLE_PARAMETERS = 3;

	static final String PREFERENCE_FILENAME = "simple.preferences";
	static final String PREFERENCE_ACCESS_TOKEN_KEY = "access_token";
	
	protected Boolean hasValidCredentials;

	public enum ConnectionType {
		DIRECT, CLOUDREADYACS, CLOUDREADYSIMPLE, NOVALUE;

		public static ConnectionType toConnectionType(String str) {
			try {
				return valueOf(str.toUpperCase());
			} catch (Exception ex) {
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

		switch (ConnectionType.toConnectionType(connectionType)) {
			case DIRECT:
				String accountName = getString(R.string.direct_account_name);
				String accessKey = getString(R.string.direct_access_key);
	
				if (!isValidConfigurationValue(accountName) || !isValidConfigurationValue(accessKey)) {
					showDialog(MISSING_DIRECT_PARAMETERS);
				}
				break;
			case CLOUDREADYACS:
				String namespace = getString(R.string.cloud_ready_acs_namespace);
				String realm = getString(R.string.cloud_ready_acs_realm);
				String proxyService = getString(R.string.cloud_ready_acs_proxy_service);
				String symmetricKey = getString(R.string.cloud_ready_acs_symmetric_key);
	
				if (!isValidConfigurationValue(namespace)
						|| !isValidConfigurationValue(realm)
						|| !isValidConfigurationValue(proxyService)
						|| !isValidConfigurationValue(symmetricKey)) {
					showDialog(MISSING_CLOUDREADY_ACS_PARAMETERS);
				}
								
				
				IAccessToken token = getAccessControlToken();
				
				if (token == null || token.isExpired()) {
					String repository = String.format("https://%s.accesscontrol.windows.net/v2/metadata/IdentityProviders.js?protocol=javascriptnotify&realm=%s&version=1.0", namespace, realm);
					doAcsLogin(repository, realm, symmetricKey);
				} else {
					launchBootStrapActivity();
				}
				
				return;
			case CLOUDREADYSIMPLE:
				String service = getString(R.string.cloud_ready_simple_proxy_service);
	
				if (!isValidConfigurationValue(service)) {
					showDialog(MISSING_CLOUDREADY_SIMPLE_PARAMETERS);
				}
				break;
		}
	}
	
	private void launchBootStrapActivity(){
		// If current activity is this class it launches the MainWindow bootstrap activity		
		if (this.getComponentName().getClassName().equals(getCurrentClassName())){
	    	Intent mainActivity = new Intent(this, MainWindowActivity.class);
	    	startActivity (mainActivity);			
		}
	}
	
	public static String getCurrentClassName(){
		 return (new CurClassNameGetter()).getClassName();
	}

	//Static Nested Class doing the trick
	public static class CurClassNameGetter extends SecurityManager{
		public String getClassName(){
		return getClassContext()[1].getName();
		}
	}
	
	protected Dialog onCreateDialog(int id) {
		AlertDialog dialog = new AlertDialog.Builder(this).create();
		dialog.setTitle(getString(R.string.configuration_error_title));
		dialog.setCanceledOnTouchOutside(true);
		dialog.setCancelable(true);
		
		switch (id) {
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
		
		dialog.setOnCancelListener(new OnCancelListener()
		{
		    public void onCancel(DialogInterface dialog)
		    {
		    	activity.finish();
		    }
		});
		
		return dialog;
	}

	private boolean isValidConfigurationValue(String value) {
		return !(value.startsWith("{") || value.trim() == "");
	}

	private void doAcsLogin(String identityProvidersRepository, String realm, String symmetricKey) {
		Intent intent = new Intent(this, AccessControlLoginActivity.class);

		AccessControlLoginContext loginContext = new AccessControlLoginContext();
		loginContext.IdentityProviderRepository = new IdentityProvidersRepository(identityProvidersRepository);
		loginContext.AccessTokenHandler = new SimpleWebTokenHandler(realm, symmetricKey);
		loginContext.SuccessLoginActivity = SecuredActivity.class;
		loginContext.ErrorLoginActivity = SecuredActivity.class;
		intent.putExtra(AccessControlLoginActivity.AccessControlLoginContextKey, loginContext);

		startActivity(intent);
	}

	private IAccessToken getAccessControlToken() {
		SharedPreferences settings = getSharedPreferences(PREFERENCE_FILENAME, 0);
		IAccessToken accessToken = null;
		
		Bundle optionSet = getIntent().getExtras();
		if (optionSet != null) {
			accessToken = (IAccessToken) optionSet.getSerializable(AccessControlLoginActivity.AuthenticationTokenKey);

			if (accessToken != null) {
				SharedPreferences.Editor editor = settings.edit();
				try {
					editor.putString(PREFERENCE_ACCESS_TOKEN_KEY, toString(accessToken));
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				editor.commit();
				
				return accessToken;
			}
		}

		String serializedToken = settings.getString(PREFERENCE_ACCESS_TOKEN_KEY, "");

		if (serializedToken == "")
			return null;

		try {
			accessToken = (IAccessToken) fromString(serializedToken);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return accessToken;
	}

	private static Object fromString(String object) 
			throws IOException, ClassNotFoundException {
		byte[] data = Base64.decode(object, Base64.DEFAULT);
		ObjectInputStream stream = new ObjectInputStream(new ByteArrayInputStream(data));
		Object objectInstance = stream.readObject();
		stream.close();
		return objectInstance;
	}

	private static String toString( Serializable object ) throws IOException {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        ObjectOutputStream outputStream = new ObjectOutputStream( byteArrayOutputStream );
        outputStream.writeObject( object );
        outputStream.close();
        return new String(Base64.encode(byteArrayOutputStream.toByteArray(), Base64.DEFAULT));
    }
}
