package com.microsoft.samples.windowsazure.android.accesscontrol.login;

import java.io.Serializable;

import com.microsoft.samples.windowsazure.android.accesscontrol.core.IAccessTokenHandler;
import com.microsoft.samples.windowsazure.android.accesscontrol.core.IIdentityProviderRepository;

public class AccessControlLoginContext implements Serializable {
	
	private static final long serialVersionUID = 1L;
		
	public IAccessTokenHandler AccessTokenHandler;
	public IIdentityProviderRepository IdentityProviderRepository;
	public Class<?> SuccessLoginActivity;
	public Class<?> ErrorLoginActivity;
	
}
