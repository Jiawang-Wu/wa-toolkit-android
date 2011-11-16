package com.microsoft.samples.windowsazure.android.accesscontrol.swt.tests;

import junit.framework.Assert;
import android.test.AndroidTestCase;

import com.microsoft.samples.windowsazure.android.accesscontrol.swt.SimpleWebToken;

public class SimpleWebTokenTests extends AndroidTestCase {

	public void testWhenCreatedWithNullShouldThrowException() {
		boolean exceptionRaised = false;
		
		try {
			new SimpleWebToken(null);
		} catch (NullPointerException e) {
			exceptionRaised = true;
		}
		
		Assert.assertTrue(exceptionRaised);
	}
	
	public void testWhenCreatedWithEmptyShouldBeEmpty() {
		SimpleWebToken token = new SimpleWebToken("");
		
		Assert.assertEquals(token.getRawToken(), "");
	}
	
	public void testWhenCreatedWithValidRawShouldParseValues() {
		String rawToken = "http%3a%2f%2fschemas.xmlsoap.org%2fws%2f2005%2f05%2fidentity%2fclaims%2femailaddress=john.doe%40mail.com&http%3a%2f%2fschemas.xmlsoap.org%2fws%2f2005%2f05%2fidentity%2fclaims%2fname=John+Doe&http%3a%2f%2fschemas.xmlsoap.org%2fws%2f2005%2f05%2fidentity%2fclaims%2fnameidentifier=John+Doe&http%3a%2f%2fschemas.microsoft.com%2faccesscontrolservice%2f2010%2f07%2fclaims%2fidentityprovider=IP.Tests&Audience=urn%3aAudience.Tests&ExpiresOn=1321030774&Issuer=Issuer.Tests&HMACSHA256=scDKPjfCMLNelZHAqUaaFq0uHA3Kp3UQru707VLGaVY%3d";
		SimpleWebToken token = new SimpleWebToken(rawToken);
		
		Assert.assertEquals(token.getRawToken(), rawToken);
		Assert.assertEquals(token.getAudience(), "urn:Audience.Tests");
		Assert.assertEquals(token.getIssuer(), "Issuer.Tests");
		Assert.assertEquals(token.getClaimValue("http://schemas.xmlsoap.org/ws/2005/05/identity/claims/emailaddress"), "john.doe@mail.com");
		Assert.assertEquals(token.getClaimValue("http://schemas.xmlsoap.org/ws/2005/05/identity/claims/name"), "John Doe");
		Assert.assertEquals(token.getClaimValue("http://schemas.xmlsoap.org/ws/2005/05/identity/claims/nameidentifier"), "John Doe");
		Assert.assertEquals(token.getClaimValue("http://schemas.microsoft.com/accesscontrolservice/2010/07/claims/identityprovider"), "IP.Tests");
	}
	
}
