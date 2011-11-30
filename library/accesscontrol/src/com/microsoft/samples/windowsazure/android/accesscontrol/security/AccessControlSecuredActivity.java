package com.microsoft.samples.windowsazure.android.accesscontrol.security;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;

import com.microsoft.samples.windowsazure.android.accesscontrol.core.IAccessToken;
import com.microsoft.samples.windowsazure.android.accesscontrol.login.AccessControlLoginActivity;
import com.microsoft.samples.windowsazure.android.accesscontrol.login.AccessControlLoginContext;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Base64;

public abstract class AccessControlSecuredActivity extends Activity {

	public final void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		if (this.shouldAuthenticate()) {
			IAccessToken token = getAccessControlToken();

			if (token == null || token.isExpired()) {				
				if (token != null) onTokenExpiredWarning();
				doAcsLogin();
				finish();
				return;
			}
		}
		
		onCreateCompleted(savedInstanceState);
	}

	protected IAccessToken getAccessControlToken() {
		SharedPreferences settings = getSharedPreferences(getPreferencesFileName(), 0);
		IAccessToken accessToken = null;

		Bundle optionSet = getIntent().getExtras();
		if (optionSet != null) {
			accessToken = (IAccessToken) optionSet.getSerializable(AccessControlLoginActivity.AuthenticationTokenKey);

			if (accessToken != null) {
				SharedPreferences.Editor editor = settings.edit();
				try {
					editor.putString(getPreferencesTokenKey(), toString(accessToken));
				} catch (IOException e) {
					e.printStackTrace();
				}

				editor.commit();
				return accessToken;
			}
		}

		String serializedToken = settings.getString(getPreferencesTokenKey(), "");

		if (serializedToken == "") return null;

		try {
			accessToken = (IAccessToken) fromString(serializedToken);
		} catch (Exception e) {
			e.printStackTrace();
		}

		return accessToken;
	}

	protected void doAcsLogin() {
		Intent intent = new Intent(this, AccessControlLoginActivity.class);
		intent.putExtra(AccessControlLoginActivity.AccessControlLoginContextKey, getLoginContext());
		startActivity(intent);
	}

	protected abstract boolean shouldAuthenticate();

	protected abstract void onCreateCompleted(Bundle savedInstanceState);
	
	protected abstract void onTokenExpiredWarning();

	protected abstract String getPreferencesFileName();
	
	protected abstract String getPreferencesTokenKey();

	protected abstract AccessControlLoginContext getLoginContext();

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
