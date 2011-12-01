package com.microsoft.samples.windowsazure.android.accesscontrol.login;

import com.microsoft.samples.windowsazure.android.accesscontrol.core.AccessTokenConstants;
import com.microsoft.samples.windowsazure.android.accesscontrol.core.IAccessToken;
import com.microsoft.samples.windowsazure.android.accesscontrol.core.IAccessTokenReceiver;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Window;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;

public class AccessControlWebAuthActivity extends Activity implements
		IAccessTokenReceiver {

	public static final String LoginUrlKey = "LoginUrl";
	private AccessControlLoginContext mAccessControlLoginContext;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		String loginUrl = null;
		Bundle extras = getIntent().getExtras();
		if (extras != null) {
			loginUrl = extras.getString(LoginUrlKey);
			mAccessControlLoginContext = (AccessControlLoginContext) extras
					.getSerializable(AccessControlLoginActivity.AccessControlLoginContextKey);
		}

		mAccessControlLoginContext.AccessTokenHandler.setTokenReceiver(this);
		this.getWindow().requestFeature(Window.FEATURE_PROGRESS);

		WebView webview = new WebView(this);
		setContentView(webview);        
		webview.getSettings().setJavaScriptEnabled(true);
		webview.addJavascriptInterface(new AccessControlJavascriptNotify(mAccessControlLoginContext.AccessTokenHandler), "external");
		webview.setWebViewClient(new AuthWebViewClient());
		webview.setWebChromeClient(new AuthWebChromeClient(this));
		webview.loadUrl(loginUrl);
	}

	@Override
	public void onAccessTokenReceived(IAccessToken token, int tokenStatus) {
		Intent intent = (tokenStatus == AccessTokenConstants.VALID_TOKEN) ? new Intent(
				this, mAccessControlLoginContext.SuccessLoginActivity)
				: new Intent(this,
						mAccessControlLoginContext.ErrorLoginActivity);
		intent.putExtra(AccessControlLoginActivity.AuthenticationTokenKey,
				token);
		intent.putExtra(
				AccessControlLoginActivity.AuthenticationTokenStatusKey,
				tokenStatus);
		startActivity(intent);
		finish();
	}

	private class AuthWebViewClient extends WebViewClient { }
	
	private class AuthWebChromeClient extends WebChromeClient {
		
		private Activity mParentActivity;
		private CharSequence mTitle;
		
		public AuthWebChromeClient(Activity parentActivity) {
			mParentActivity = parentActivity;
			mTitle = parentActivity.getTitle();
		}
		
		@Override
        public void onProgressChanged(WebView view, int progress)
        {        	
			mParentActivity.setTitle(String.format("Loading (%d%%)...", progress));
			mParentActivity.setProgress(progress * 100);

            if(progress == 100)	mParentActivity.setTitle(mTitle);
        }
		
	}
	
}