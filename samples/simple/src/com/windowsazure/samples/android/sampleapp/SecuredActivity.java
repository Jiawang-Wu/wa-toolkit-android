package com.windowsazure.samples.android.sampleapp;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.net.URI;

import com.microsoft.samples.windowsazure.android.accesscontrol.core.IAccessToken;
import com.microsoft.samples.windowsazure.android.accesscontrol.core.IdentityProvidersRepository;
import com.microsoft.samples.windowsazure.android.accesscontrol.login.AccessControlLoginActivity;
import com.microsoft.samples.windowsazure.android.accesscontrol.login.AccessControlLoginContext;
import com.microsoft.samples.windowsazure.android.accesscontrol.swt.SimpleWebTokenHandler;
import com.windowsazure.samples.android.storageclient.CloudClientAccount;
import com.windowsazure.samples.android.storageclient.CloudStorageAccount;
import com.windowsazure.samples.android.storageclient.StorageCredentialsAccountAndKey;
import com.windowsazure.samples.android.storageclient.wazservice.WAZServiceAccountAcs;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Base64;
import android.view.Window;

public abstract class SecuredActivity extends Activity {

	static final String PREFERENCE_FILENAME = "simple.preferences";
	static final String PREFERENCE_ACCESS_TOKEN_KEY = "access_token";
	static final String CLAIM_TYPE_NAME_IDENTIFIER = "http://schemas.xmlsoap.org/ws/2005/05/identity/claims/nameidentifier";
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		this.requestWindowFeature(Window.FEATURE_NO_TITLE);

		CloudClientAccount cloudClientAccount = null;
       
		try {
			switch (this.getSampleApplication().getConnectionType()) {
				case DIRECT:
					String accountName = getString(R.string.direct_account_name);
					String accessKey = getString(R.string.direct_access_key);

					cloudClientAccount = new CloudStorageAccount(new StorageCredentialsAccountAndKey(accountName, accessKey)); 
					break;
				case CLOUDREADYACS:
					String namespace = getString(R.string.cloud_ready_acs_namespace);
					String realm = getString(R.string.cloud_ready_acs_realm);
					String proxyService = getString(R.string.cloud_ready_acs_proxy_service);
					String symmetricKey = getString(R.string.cloud_ready_acs_symmetric_key);

					IAccessToken token = getAccessControlToken();					
					
					if (token == null || token.isExpired()) {
						String repository = String.format("https://%s.accesscontrol.windows.net/v2/metadata/IdentityProviders.js?protocol=javascriptnotify&realm=%s&version=1.0", namespace, realm);
						doAcsLogin(repository, realm, symmetricKey);
						return;
					}
					
					String userName = token.getClaimValue(CLAIM_TYPE_NAME_IDENTIFIER);
					cloudClientAccount = new WAZServiceAccountAcs(userName, "foo@bar.com", token.getRawToken(), new URI(proxyService)); 	
					
					break;
				case CLOUDREADYSIMPLE:
					// We don't do anything. The account should've been configured when the application started
					cloudClientAccount = this.getSampleApplication().getCloudClientAccount();
					break;
			}
			
			// Configure the account we'll use to access the storage
			this.getSampleApplication().setCloudClientAccount(cloudClientAccount);
        }
        catch (Exception exception)
        {
        	this.showErrorMessage("Couldn't configure a cloud client account", exception);
        }
    }

	private void doAcsLogin(String identityProvidersRepository, String realm, String symmetricKey) {
		Intent intent = new Intent(this, AccessControlLoginActivity.class);

		AccessControlLoginContext loginContext = new AccessControlLoginContext();
		loginContext.IdentityProviderRepository = new IdentityProvidersRepository(identityProvidersRepository);
		loginContext.AccessTokenHandler = new SimpleWebTokenHandler(realm, symmetricKey);
		loginContext.SuccessLoginActivity = this.getClass();
		//loginContext.ErrorLoginActivity = SecuredActivity.class; //TODO: add error activity
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
				
				// TODO: Move the registration to a better place
				
				
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
	
	protected SampleApplication getSampleApplication() {
		return (SampleApplication) this.getApplication(); 
	}
	
	protected Bundle optionSet() {
		return getIntent().getExtras();
	}
	
	void showErrorMessageIfAny(String title, Exception exception) {
		if (exception != null) {
			this.showErrorMessage(title, exception);
		}
	}
	
	void showErrorMessage(String title, Exception exception) {
		exception.printStackTrace();
    	System.out.println(exception.toString());
		AlertDialog dialog = dialogToShow(title, exception).create();
		dialog.setCanceledOnTouchOutside(true);
		dialog.show();
	}

	AlertDialog.Builder dialogToShow(String title, Exception exception) {
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setTitle(title);
		builder.setMessage(exception.getLocalizedMessage());
		builder.setCancelable(true);
		return builder;
	}
	
}
