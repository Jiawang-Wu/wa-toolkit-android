package com.windowsazure.samples.android.sampleapp;

import java.net.URI;

import com.microsoft.samples.windowsazure.android.accesscontrol.core.IAccessToken;
import com.microsoft.samples.windowsazure.android.accesscontrol.core.IdentityProvidersRepository;
import com.microsoft.samples.windowsazure.android.accesscontrol.login.AccessControlLoginContext;
import com.microsoft.samples.windowsazure.android.accesscontrol.security.AccessControlSecuredActivity;
import com.microsoft.samples.windowsazure.android.accesscontrol.swt.SimpleWebTokenHandler;
import com.windowsazure.samples.android.sampleapp.SampleApplication.ConnectionType;
import com.windowsazure.samples.android.storageclient.CloudClientAccount;
import com.windowsazure.samples.android.storageclient.CloudStorageAccount;
import com.windowsazure.samples.android.storageclient.StorageCredentialsAccountAndKey;
import com.windowsazure.samples.android.storageclient.wazservice.WAZServiceAccountAcs;

import android.app.AlertDialog;
import android.os.Bundle;
import android.view.Window;

public abstract class SecuritableActivity extends AccessControlSecuredActivity {

	static final String CLAIM_TYPE_NAME_IDENTIFIER = "http://schemas.xmlsoap.org/ws/2005/05/identity/claims/nameidentifier";
	static final String CLAIM_TYPE_IDENTITY_PROVIDER = "http://schemas.microsoft.com/accesscontrolservice/2010/07/claims/identityprovider";

	@Override
	protected void onCreateCompleted(Bundle savedInstanceState) {
		this.requestWindowFeature(Window.FEATURE_NO_TITLE);

		CloudClientAccount cloudClientAccount = null;
		try {
			cloudClientAccount = this.getSampleApplication().getCloudClientAccount();
		} catch (Exception e) {
			e.printStackTrace();
		}

		if (cloudClientAccount == null) {
			try {
				switch (this.getSampleApplication().getConnectionType()) {
				case DIRECT:
					String accountName = getString(R.string.direct_account_name);
					String accessKey = getString(R.string.direct_access_key);
					cloudClientAccount = new CloudStorageAccount(new StorageCredentialsAccountAndKey(accountName, accessKey));
					break;
				case CLOUDREADYACS:
					IAccessToken token = getAccessControlToken();

					// HACK: Right now the ACS proxy service requires the user to be registered before.
					// This will be removed in vNext, so right now we're generating an email based on the
					// nameidentifier and idp claims
					String userName = token.getClaimValue(CLAIM_TYPE_NAME_IDENTIFIER);
					String identityProvider = token.getClaimValue(CLAIM_TYPE_IDENTITY_PROVIDER);
					String email = String.format("%s@%s.com", userName,	identityProvider);
					String proxyService = getString(R.string.cloud_ready_acs_proxy_service);
					cloudClientAccount = new WAZServiceAccountAcs(userName, email, token.getRawToken(), new URI(proxyService));
					break;
				case CLOUDREADYSIMPLE:
					// We don't do anything. The account should've been
					// configured when the application started
					cloudClientAccount = this.getSampleApplication().getCloudClientAccount();
					break;
				}

				// Configure the account we'll use to access the storage
				this.getSampleApplication().setCloudClientAccount(cloudClientAccount);
			} catch (Exception exception) {
				this.showErrorMessage(
						"Couldn't configure a cloud client account", exception);
			}
		}
	}

	@Override
	protected void onTokenExpiredWarning() {
		this.getSampleApplication().expireAccessToken();
		this.getSampleApplication().setCloudClientAccount(null);
	}

	@Override
	protected boolean shouldAuthenticate() {
		return this.getSampleApplication().getConnectionType()
				.equals(ConnectionType.CLOUDREADYACS);
	}

	@Override
	protected String getPreferencesFileName() {
		return SampleApplication.PREFERENCE_FILENAME;
	}

	@Override
	protected String getPreferencesTokenKey() {
		return SampleApplication.PREFERENCE_FILENAME;
	}

	@Override
	protected AccessControlLoginContext getLoginContext() {
		String namespace = getString(R.string.cloud_ready_acs_namespace);
		String realm = getString(R.string.cloud_ready_acs_realm);
		String symmetricKey = getString(R.string.cloud_ready_acs_symmetric_key);
		String repository = String
				.format("https://%s.accesscontrol.windows.net/v2/metadata/IdentityProviders.js?protocol=javascriptnotify&realm=%s&version=1.0",
						namespace, realm);

		AccessControlLoginContext loginContext = new AccessControlLoginContext();
		loginContext.IdentityProviderRepository = new IdentityProvidersRepository(repository);
		loginContext.AccessTokenHandler = new SimpleWebTokenHandler(realm, symmetricKey);
		loginContext.SuccessLoginActivity = this.getClass();

		return loginContext;
	}

	protected SampleApplication getSampleApplication() {
		return (SampleApplication) this.getApplication();
	}

	protected Bundle optionSet() {
		return getIntent().getExtras();
	}

	void showErrorMessageIfAny(String title, Throwable exception) {
		if (exception != null) {
			this.showErrorMessage(title, exception);
		}
	}

	void showErrorMessage(String title, Throwable exception) {
		exception.printStackTrace();
		System.out.println(exception.toString());
		AlertDialog dialog = dialogToShow(title, exception).create();
		dialog.setCanceledOnTouchOutside(true);
		dialog.show();
	}

	AlertDialog.Builder dialogToShow(String title, Throwable exception) {
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setTitle(title);
		builder.setMessage(exception.getLocalizedMessage());
		builder.setCancelable(true);
		return builder;
	}

}
