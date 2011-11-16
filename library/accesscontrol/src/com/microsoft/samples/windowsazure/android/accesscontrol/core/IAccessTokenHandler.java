package com.microsoft.samples.windowsazure.android.accesscontrol.core;

import java.io.Serializable;

public interface IAccessTokenHandler extends Serializable {
	
	IAccessToken parseToken(String xml);
	
	IAccessTokenReceiver getTokenReceiver();
	void setTokenReceiver(IAccessTokenReceiver tokenReceiver);
	
}
