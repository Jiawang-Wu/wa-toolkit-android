package com.microsoft.samples.windowsazure.android.accesscontrol.core;

import java.io.Serializable;

public interface IAccessToken extends Serializable {
	
	String getClaimValue(String claimName);
	String getRawToken();
	String getAudience();
	String getIssuer();
	boolean isExpired();
	
}
