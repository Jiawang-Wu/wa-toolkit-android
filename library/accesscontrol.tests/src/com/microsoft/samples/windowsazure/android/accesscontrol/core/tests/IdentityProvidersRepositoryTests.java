package com.microsoft.samples.windowsazure.android.accesscontrol.core.tests;

import com.microsoft.samples.windowsazure.android.accesscontrol.core.IIdentityProviderRepository;
import com.microsoft.samples.windowsazure.android.accesscontrol.core.IdentityProviderMetadata;
import com.microsoft.samples.windowsazure.android.accesscontrol.core.IdentityProvidersRepository;

import android.test.AndroidTestCase;
import junit.framework.Assert;

public class IdentityProvidersRepositoryTests extends AndroidTestCase {

	public void testWhenCreatedShouldHaveAnInstancedObject() {
		IIdentityProviderRepository provider = new IdentityProvidersRepository("http://foo");
		
		Assert.assertNotNull(provider);
	}
	
	public void testWhenCreatedWithInvalidUrlShouldReturnNull() {
		IIdentityProviderRepository provider = new IdentityProvidersRepository("http://foo");
		IdentityProviderMetadata[] metadata = provider.getIndentityProvidersMetadata();
		
		Assert.assertNull(metadata);
	}
}
