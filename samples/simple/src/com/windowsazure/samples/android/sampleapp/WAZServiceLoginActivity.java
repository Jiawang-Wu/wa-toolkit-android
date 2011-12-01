package com.windowsazure.samples.android.sampleapp;

import java.net.URI;

import com.windowsazure.samples.android.storageclient.wazservice.WAZServiceAccount;
import com.windowsazure.samples.android.storageclient.wazservice.WAZServiceUsernameAndPassword;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.app.Dialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;

public class WAZServiceLoginActivity extends Activity {
    private Button loginButton;
	private EditText wasServicesBaseUrlText;
	private EditText usernameText;
	private EditText passwordText;
	private ProgressBar progressBar;
	private TextView usernameLabel;
	private TextView passwordLabel;
	//private TextView wasServicesBaseUrlLabel;
	private Button registerButton;
	private AsyncTask<Void, Void, AlertDialog.Builder> currentTask;

	public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
    	setContentView(R.layout.waz_service_login);

        TextView title = (TextView)findViewById(R.id.header_title);
        title.setText(getString(R.string.was_service_login_title));

        loginButton = (Button)findViewById(R.id.login_button);
        registerButton = (Button)findViewById(R.id.header_register_button);
    	//wasServicesBaseUrlLabel = (TextView)findViewById(R.id.waz_services_base_url_label);
    	wasServicesBaseUrlText = (EditText)findViewById(R.id.waz_services_base_url_value);
    	usernameLabel = (TextView)findViewById(R.id.username_label);
    	usernameText = (EditText)findViewById(R.id.username_value);
    	passwordLabel = (TextView)findViewById(R.id.password_label);
    	passwordText = (EditText)findViewById(R.id.password_value);
    	progressBar = (ProgressBar)findViewById(R.id.waz_service_login_progress);

        loginButton.setOnClickListener(new View.OnClickListener() {
        	public void onClick(View view) { onLoginButton(view); }
        	});
        registerButton.setOnClickListener(new View.OnClickListener() {
        	public void onClick(View view) { onRegisterButton(view); }
        	});
        registerButton.setVisibility(View.VISIBLE);
    }

	protected void onPause() {
		super.onPause();
		AsyncTask<Void, Void, Builder> task = currentTask;
		if (task != null) {
			task.cancel(true);
			currentTask = null;
		}
	}

	private void onLoginButton(View view) {
		loginButton.setEnabled(false);
		registerButton.setEnabled(false);

		final WAZServiceLoginActivity thisActivity = this;
		class LoginTask extends AsyncTask<Void, Void, AlertDialog.Builder> {
			protected AlertDialog.Builder doInBackground(Void... params) {
				try {
					WAZServiceAccount account = new WAZServiceAccount(
							new WAZServiceUsernameAndPassword(usernameText.getText().toString(), passwordText.getText().toString()),
							new URI(wasServicesBaseUrlText.getText().toString()));

					// We call getCredentials to force the account to perform a login
					account.getCredentials();

					((SampleApplication) getApplication()).setCloudClientAccount(account);
				}
		    	catch (Exception exception) {
		    		AlertDialog.Builder builder = new AlertDialog.Builder(thisActivity);
		    		builder.setTitle("Couldn't log-in to the WAZ Service");
		    		builder.setMessage(exception.getLocalizedMessage());
		    		builder.setCancelable(true);
		    		return builder;
				}
				return null;
		    }

			protected void onPreExecute() {
			    loginButton.setVisibility(View.GONE);
				wasServicesBaseUrlText.setVisibility(View.GONE);
			    //wasServicesBaseUrlLabel.setVisibility(View.GONE);
				//wasServicesBaseUrlText.setVisibility(View.GONE);
				usernameLabel.setVisibility(View.GONE);
				usernameText.setVisibility(View.GONE);
				passwordLabel.setVisibility(View.GONE);
				passwordText.setVisibility(View.GONE);
				progressBar.setVisibility(View.VISIBLE);
			}

			protected void onPostExecute(AlertDialog.Builder dialogBuilder) {
				if (dialogBuilder == null) {
			    	Intent storageTypeSelector = new Intent(thisActivity, StorageTypeSelectorActivity.class);
			    	startActivity (storageTypeSelector);
			    	finish();
				}
				else {
					Dialog dialog = dialogBuilder.create();
					dialog.setCanceledOnTouchOutside(true);
					dialog.show();
					loginButton.setEnabled(true);
					registerButton.setEnabled(true);
					progressBar.setVisibility(View.GONE);
				    loginButton.setVisibility(View.VISIBLE);
				    //wasServicesBaseUrlLabel.setVisibility(View.VISIBLE);
					//wasServicesBaseUrlText.setVisibility(View.VISIBLE);
				    usernameLabel.setVisibility(View.VISIBLE);
					usernameText.setVisibility(View.VISIBLE);
					passwordLabel.setVisibility(View.VISIBLE);
					passwordText.setVisibility(View.VISIBLE);
				}
				currentTask = null;
			}
		}
		currentTask = new LoginTask();
		currentTask.execute();
	}

	protected void onRegisterButton(View view) {
		loginButton.setEnabled(false);
		registerButton.setEnabled(false);
    	Intent storageTypeSelector = new Intent(this, WAZServiceRegisterActivity.class);
    	startActivity (storageTypeSelector);
    	finish();
	}
}
