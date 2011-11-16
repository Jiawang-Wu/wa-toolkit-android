package com.microsoft.samples.windowsazure.android.accesscontrol.login;

import com.microsoft.samples.windowsazure.android.accesscontrol.core.IAccessTokenHandler;

public final class AccessControlJavascriptNotify {
	
		private IAccessTokenHandler mAccessTokenHandler;
	
    	public AccessControlJavascriptNotify(IAccessTokenHandler tokenHandler){
    		mAccessTokenHandler = tokenHandler;
    	}
		
		public void notify(String securityTokenResponse) {
			mAccessTokenHandler.parseToken(securityTokenResponse);
		}

} 
