package com.microsoft.samples.windowsazure.android.accesscontrol.login;

import com.microsoft.samples.windowsazure.android.accesscontrol.core.AccessTokenConstants;
import com.microsoft.samples.windowsazure.android.accesscontrol.core.IAccessToken;
import com.microsoft.samples.windowsazure.android.accesscontrol.core.IAccessTokenReceiver;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.webkit.WebView;
import android.webkit.WebViewClient;

public class AccessControlWebAuthActivity extends Activity implements IAccessTokenReceiver {
	
	public static final String LoginUrlKey = "LoginUrl";
	private AccessControlLoginContext mAccessControlLoginContext;
	
    @Override
    public void onCreate(Bundle savedInstanceState) {    	
    	super.onCreate(savedInstanceState);
    	
    	String loginUrl = null;
		Bundle extras = getIntent().getExtras(); 
		if(extras != null) {
			loginUrl = extras.getString(LoginUrlKey);
			mAccessControlLoginContext = (AccessControlLoginContext)extras.getSerializable(AccessControlLoginActivity.AccessControlLoginContextKey);
		}
		
		mAccessControlLoginContext.AccessTokenHandler.setTokenReceiver(this);
    	
    	WebView webview = new WebView(this);
    	setContentView(webview);
		webview.getSettings().setJavaScriptEnabled(true);
		webview.addJavascriptInterface(new AccessControlJavascriptNotify(mAccessControlLoginContext.AccessTokenHandler), "external");
		webview.setWebViewClient(new AuthWebViewClient());
		webview.loadUrl(loginUrl);
    }

	@Override
	public void onAccessTokenReceived(IAccessToken token, int tokenStatus) {
		Intent intent = (tokenStatus == AccessTokenConstants.VALID_TOKEN) ? 
				new Intent(this, mAccessControlLoginContext.SuccessLoginActivity) :
				new Intent(this, mAccessControlLoginContext.ErrorLoginActivity);
		intent.putExtra(AccessControlLoginActivity.AuthenticationTokenKey, token);
		intent.putExtra(AccessControlLoginActivity.AuthenticationTokenStatusKey, tokenStatus);
		startActivity(intent);
		finish();
	}
	
    private class AuthWebViewClient extends WebViewClient { }

}