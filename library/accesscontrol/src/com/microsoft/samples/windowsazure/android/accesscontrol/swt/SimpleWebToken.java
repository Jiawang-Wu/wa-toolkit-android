package com.microsoft.samples.windowsazure.android.accesscontrol.swt;

import java.net.URLDecoder;
import java.util.Date;
import java.util.Hashtable;
import java.util.Map;

import com.microsoft.samples.windowsazure.android.accesscontrol.core.AccessTokenConstants;
import com.microsoft.samples.windowsazure.android.accesscontrol.core.IAccessToken;

public class SimpleWebToken implements IAccessToken {
	
	private static final long serialVersionUID = 1L;

	private String mRawToken;
	private String mAudience;
	private String mIssuer;
	private String mExpiresOn;
	private Hashtable<String, String> mClaims;
	
	public SimpleWebToken(String rawToken) {
		this.mRawToken = rawToken;
		this.parse();
	}
	
	public Map<String, String> getClaims() {
		return mClaims;
	}
	
	public String getClaimValue(String claimName) {
		return this.mClaims.get(claimName);
	}
	
	public String getRawToken() {
		return this.mRawToken;
	}
	
	public String getAudience() {
		return this.mAudience;
	}
	
	public String getIssuer() {
		return this.mIssuer;
	}
	
	public boolean isExpired() {
		long epoch = Long.parseLong(this.mExpiresOn);		
		return new Date().after(new Date(epoch * 1000l));
	}
	
	private void parse() {
		if (mRawToken.length() == 0) return; 
		
		this.mClaims = new Hashtable<String, String>();
		String[] tuples = this.mRawToken.split("&");
		
		for(Integer i = 0; i < tuples.length; i++ ){
			String[] keyValuePair = tuples[i].split("=");
			
			String key = URLDecoder.decode(keyValuePair[0]); 
			String value = URLDecoder.decode(keyValuePair[1]);
		
			if(key.equals(AccessTokenConstants.AUDIENCE)) {
				this.mAudience = value;
				continue;
			}
			
			if(key.equals(AccessTokenConstants.EXPIRES_ON)) {
				this.mExpiresOn = value;
				continue;
			}
			
			if(key.equals(AccessTokenConstants.ISSUER)) {
				this.mIssuer = value;
				continue;
			}
                 
			this.mClaims.put(key, value);
		}	
	}
	
}
