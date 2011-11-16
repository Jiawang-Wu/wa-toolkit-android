package com.microsoft.samples.windowsazure.android.accesscontrol.core;

public interface IAccessTokenReceiver {
	
	void onAccessTokenReceived(IAccessToken token, int tokenStatus);
	
}
