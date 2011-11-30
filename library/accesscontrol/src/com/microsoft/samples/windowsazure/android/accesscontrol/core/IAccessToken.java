package com.microsoft.samples.windowsazure.android.accesscontrol.core;

import java.io.Serializable;
import java.util.Map;

public interface IAccessToken extends Serializable {
	
	Map<String, String> getClaims();
	String getClaimValue(String claimName);
	String getRawToken();
	String getAudience();
	String getIssuer();
	boolean isExpired();
	
}
