package com.microsoft.samples.windowsazure.android.accesscontrol.core;

public class IdentityProviderMetadata {

		private String mName;
		private String mLoginUrl;
		private String mLogoutUrl;
		private String mImageUrl;
	
		public IdentityProviderMetadata(String name, String loginUrl, String logoutUrl, String imageUrl) {
			mName = name;
			mLoginUrl = loginUrl;
			mLogoutUrl = logoutUrl;
			mImageUrl = imageUrl;
		}
		
		public String getName() {
			return mName;
		}
		
		public String getLoginUrl() {
			return mLoginUrl;
		}
		
		public String getLogoutUrl() {
			return mLogoutUrl;
		}
		
		public String getImageUrl() {
			return mImageUrl;
		}
		
}
