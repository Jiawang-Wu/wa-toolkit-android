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
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;

public class WAZServiceRegisterActivity extends Activity
{
    private Button registerButton;
	private EditText wasServicesBaseUrlText;
	private EditText usernameText;
	private EditText passwordText;
	private ProgressBar progressBar;
	private TextView usernameLabel;
	private TextView passwordLabel;
	//private TextView wasServicesBaseUrlLabel;
	private Button backButton;
	private TextView emailLabel;
	private EditText emailText;
	private TextView confirmPasswordLabel;
	private EditText confirmPasswordText;
	private AsyncTask<Void, Void, AlertDialog.Builder> currentTask;

	public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
    	setContentView(R.layout.waz_service_registration);
    	
        TextView title = (TextView)findViewById(R.id.header_title);
        title.setText(getString(R.string.was_service_registration_title));

        registerButton = (Button)findViewById(R.id.register_button);
        backButton = (Button)findViewById(R.id.header_back_button);
    	//wasServicesBaseUrlLabel = (TextView)findViewById(R.id.waz_services_base_url_label);
    	wasServicesBaseUrlText = (EditText)findViewById(R.id.waz_services_base_url_value);
    	usernameLabel = (TextView)findViewById(R.id.username_label);
    	usernameText = (EditText)findViewById(R.id.username_value);
    	emailLabel = (TextView)findViewById(R.id.email_label);
    	emailText = (EditText)findViewById(R.id.email_value);
    	passwordLabel = (TextView)findViewById(R.id.password_label);
    	passwordText = (EditText)findViewById(R.id.password_value);
    	confirmPasswordLabel = (TextView)findViewById(R.id.confirm_password_label);
    	confirmPasswordText = (EditText)findViewById(R.id.confirm_password_value);
    	progressBar = (ProgressBar)findViewById(R.id.waz_service_login_progress);
        
    	TextWatcher passwordTextWatcher = new TextWatcher() {
			public void afterTextChanged(Editable s) {
				onPasswordChange();
			}
			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {
			}
			public void onTextChanged(CharSequence s, int start, int before,
					int count) {
			}
			};
		passwordText.addTextChangedListener(passwordTextWatcher);
		confirmPasswordText.addTextChangedListener(passwordTextWatcher);
        registerButton.setOnClickListener(new View.OnClickListener() {
        	public void onClick(View view) { onRegisterButton(view); }
        	});
        backButton.setOnClickListener(new View.OnClickListener() {
        	public void onClick(View view) { onBackButton(view); }
        	});
        backButton.setVisibility(View.VISIBLE);
    }

	protected void onPasswordChange() {
		registerButton.setEnabled(this.arePasswordsEqual());
	}

	protected void onBackButton(View view) {
    	Intent storageTypeSelector = new Intent(this, WAZServiceLoginActivity.class);
    	startActivity (storageTypeSelector);
		finish();
	}

	protected void onPause()
	{
		super.onPause();
		AsyncTask<Void, Void, Builder> task = currentTask; 
		if (task != null)
		{
			task.cancel(true);
			currentTask = null;
		}
	}

	private void onRegisterButton(View view) {
		if (this.arePasswordsEqual())
		{
			final WAZServiceRegisterActivity thisActivity = this;
			class RegisterTask extends AsyncTask<Void, Void, AlertDialog.Builder> {
				protected AlertDialog.Builder doInBackground(Void... params) {
					try {
						WAZServiceAccount account = new WAZServiceAccount(
								new WAZServiceUsernameAndPassword(usernameText.getText().toString(), passwordText.getText().toString()), 
								new URI(wasServicesBaseUrlText.getText().toString()));
						
						account.register(emailText.getText().toString());
						
						// Just in case, we force a log-in to check our new account
						account.getCredentials();
						
						((SampleApplication) getApplication()).setCloudClientAccount(account);
					}
			    	catch (Exception exception) {
			    		AlertDialog.Builder builder = new AlertDialog.Builder(thisActivity);
			    		builder.setTitle("Couldn't register to the WAZ Service");
			    		builder.setMessage(exception.getLocalizedMessage());
			    		builder.setCancelable(true);
			    		return builder;
					}
					return null;
			    }
				
				protected void onPreExecute() {
				    registerButton.setVisibility(View.GONE);
					wasServicesBaseUrlText.setVisibility(View.GONE);
				    //wasServicesBaseUrlLabel.setVisibility(View.GONE);
					//wasServicesBaseUrlText.setVisibility(View.GONE);
					usernameLabel.setVisibility(View.GONE);
					usernameText.setVisibility(View.GONE);
					emailLabel.setVisibility(View.GONE);
					emailText.setVisibility(View.GONE);
					passwordLabel.setVisibility(View.GONE);
					passwordText.setVisibility(View.GONE);
					confirmPasswordLabel.setVisibility(View.GONE);
					confirmPasswordText.setVisibility(View.GONE);
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
						progressBar.setVisibility(View.GONE);
					    registerButton.setVisibility(View.VISIBLE);
					    //wasServicesBaseUrlLabel.setVisibility(View.VISIBLE);
						//wasServicesBaseUrlText.setVisibility(View.VISIBLE);
					    usernameLabel.setVisibility(View.VISIBLE);
						usernameText.setVisibility(View.VISIBLE);
						emailLabel.setVisibility(View.VISIBLE);
						emailText.setVisibility(View.VISIBLE);
						passwordLabel.setVisibility(View.VISIBLE);
						passwordText.setVisibility(View.VISIBLE);
						confirmPasswordLabel.setVisibility(View.VISIBLE);
						confirmPasswordText.setVisibility(View.VISIBLE);
					}
					currentTask = null;
				}
			}
			currentTask = new RegisterTask();
			currentTask.execute();
		}
		else
		{
			AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this);
			dialogBuilder.setTitle("Can't complete registration");
			dialogBuilder.setMessage("The password and the confirmation password don't match");
			Dialog dialog = dialogBuilder.create();
			dialog.setCanceledOnTouchOutside(true);
			dialog.show();
		}
	}

	private boolean arePasswordsEqual() {
		return passwordText.getText().toString().equals(confirmPasswordText.getText().toString());
	}
}
