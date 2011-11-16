package com.microsoft.samples.windowsazure.android.accesscontrol.swt.tests;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.Date;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

import junit.framework.Assert;

import com.microsoft.samples.windowsazure.android.accesscontrol.core.AccessTokenConstants;
import com.microsoft.samples.windowsazure.android.accesscontrol.core.IAccessToken;
import com.microsoft.samples.windowsazure.android.accesscontrol.core.IAccessTokenReceiver;
import com.microsoft.samples.windowsazure.android.accesscontrol.swt.SimpleWebToken;
import com.microsoft.samples.windowsazure.android.accesscontrol.swt.SimpleWebTokenHandler;

import android.test.AndroidTestCase;
import android.util.Base64;

public class SimpleWebTokenHandlerTests extends AndroidTestCase {
	
	private final String mTokenObject = "{\"appliesTo\":\"urn:Audience.Tests\",\"context\":null,\"created\":1321034495,\"expires\":1321038095,\"securityToken\":\"&lt;?xml version=&quot;1.0&quot; encoding=&quot;utf-16&quot;?>&lt;wsse:BinarySecurityToken wsu:Id=&quot;uuid:4db367c2-786d-4d3a-8b19-142480f88136&quot; ValueType=&quot;http://schemas.xmlsoap.org/ws/2009/11/swt-token-profile-1.0&quot; EncodingType=&quot;http://docs.oasis-open.org/wss/2004/01/oasis-200401-wss-soap-message-security-1.0#Base64Binary&quot; xmlns:wsu=&quot;http://docs.oasis-open.org/wss/2004/01/oasis-200401-wss-wssecurity-utility-1.0.xsd&quot; xmlns:wsse=&quot;http://docs.oasis-open.org/wss/2004/01/oasis-200401-wss-wssecurity-secext-1.0.xsd&quot;>#ENCODEDTOKEN#&lt;/wsse:BinarySecurityToken>\",\"tokenType\":\"http://schemas.xmlsoap.org/ws/2009/11/swt-token-profile-1.0\"}";
	private final String mRawToken = "http%3a%2f%2fschemas.xmlsoap.org%2fws%2f2005%2f05%2fidentity%2fclaims%2femailaddress=john.doe%40mail.com&http%3a%2f%2fschemas.xmlsoap.org%2fws%2f2005%2f05%2fidentity%2fclaims%2fname=John+Doe&http%3a%2f%2fschemas.xmlsoap.org%2fws%2f2005%2f05%2fidentity%2fclaims%2fnameidentifier=John+Doe&http%3a%2f%2fschemas.microsoft.com%2faccesscontrolservice%2f2010%2f07%2fclaims%2fidentityprovider=IP.Tests&Audience=urn%3aAudience.Tests&ExpiresOn=#EXPIRATION#&Issuer=Issuer.Tests&HMACSHA256=#SIGNATURE#"; 
	
	private final String mAudienceUri = "urn:Audience.Tests";
	private final String mSignkingKey = "uPWNd1dF5c2vdtFEC7NIPhk6WgZglSHyqRRoI1+dc5I=";
	
	public void testWhenCreatedShouldHaveAnInstancedObject() {
		SimpleWebTokenHandler handler = new SimpleWebTokenHandler("audience", "signKey");
		
		Assert.assertNotNull(handler);
	}
	
	public void testWhenParseValidTokenShouldCallReceiverValidStatus() throws InvalidKeyException, NoSuchAlgorithmException, IllegalStateException, UnsupportedEncodingException {
		TesteableReceiver receiver = new TesteableReceiver();		
		SimpleWebTokenHandler handler = new SimpleWebTokenHandler(receiver, mAudienceUri, mSignkingKey);
		
		String encodedToken = new String(Base64.encode(buildValidRawToken().getBytes(), Base64.DEFAULT));
		String tokenObject = mTokenObject.replace("ENCODEDTOKEN", encodedToken);
		handler.parseToken(tokenObject);
		
		Assert.assertNotNull(receiver.AccessToken);
		Assert.assertEquals(receiver.TokenStatus, AccessTokenConstants.VALID_TOKEN);		
	}
	
	public void testWhenParseExpiredTokenShouldCallReceiverWithInvalidStatus() throws InvalidKeyException, NoSuchAlgorithmException, IllegalStateException, UnsupportedEncodingException {
		TesteableReceiver receiver = new TesteableReceiver();		
		SimpleWebTokenHandler handler = new SimpleWebTokenHandler(receiver, mAudienceUri, mSignkingKey);
		
		String encodedToken = new String(Base64.encode(buildExpiredRawToken().getBytes(), Base64.DEFAULT));
		String tokenObject = mTokenObject.replace("ENCODEDTOKEN", encodedToken);
		handler.parseToken(tokenObject);

		Assert.assertNotNull(receiver.AccessToken);		
		Assert.assertEquals(receiver.TokenStatus, AccessTokenConstants.INVALID_TOKEN);
		Assert.assertTrue(receiver.AccessToken.isExpired());
	}
	
	public void testWhenParseTamperedTokenShouldCallReceiverWithInvalidStatus() throws InvalidKeyException, NoSuchAlgorithmException, IllegalStateException, UnsupportedEncodingException {
		TesteableReceiver receiver = new TesteableReceiver();		
		SimpleWebTokenHandler handler = new SimpleWebTokenHandler(receiver, mAudienceUri, mSignkingKey);
		
		String encodedToken = new String(Base64.encode(buildTamperedRawToken().getBytes(), Base64.DEFAULT));
		String tokenObject = mTokenObject.replace("ENCODEDTOKEN", encodedToken);
		handler.parseToken(tokenObject);

		Assert.assertNotNull(receiver.AccessToken);		
		Assert.assertEquals(receiver.TokenStatus, AccessTokenConstants.INVALID_TOKEN);
		Assert.assertFalse(receiver.AccessToken.isExpired());
	}
	
	public void testWhenValidTokenShouldReturnTrue() throws InvalidKeyException, NoSuchAlgorithmException, IllegalStateException, UnsupportedEncodingException {
		SimpleWebToken token = new SimpleWebToken(buildValidRawToken());
		
		Assert.assertTrue(SimpleWebTokenHandler.isValid(token, mAudienceUri, mSignkingKey));
	}
	
	public void testWhenExpiredTokenShouldReturnFalse() throws InvalidKeyException, NoSuchAlgorithmException, IllegalStateException, UnsupportedEncodingException {
		SimpleWebToken token = new SimpleWebToken(buildExpiredRawToken());
		
		Assert.assertFalse(SimpleWebTokenHandler.isValid(token, mAudienceUri, mSignkingKey));	
	}
	
	public void testWhenTamperedTokenShouldReturnFalse() throws InvalidKeyException, NoSuchAlgorithmException, IllegalStateException, UnsupportedEncodingException {
		SimpleWebToken token = new SimpleWebToken(buildTamperedRawToken());
		
		Assert.assertFalse(SimpleWebTokenHandler.isValid(token, mAudienceUri, mSignkingKey));		
	}
	
	private String buildSignatureFor(String token) throws NoSuchAlgorithmException, IllegalStateException, UnsupportedEncodingException, InvalidKeyException {
    	SecretKeySpec signingKey = new SecretKeySpec(Base64.decode(mSignkingKey, Base64.DEFAULT), AccessTokenConstants.HMAC_SHA256);
        
    	Mac mac = Mac.getInstance(AccessTokenConstants.HMAC_SHA256);
    	mac.init(signingKey);
    	byte[] rawHmac = mac.doFinal(token.getBytes("ASCII"));
    	
    	return URLEncoder.encode(new String(Base64.encode(rawHmac, Base64.DEFAULT)).trim());
	}
	
	private String buildValidRawToken() throws InvalidKeyException, NoSuchAlgorithmException, IllegalStateException, UnsupportedEncodingException {
		Date d = new Date();
		d.setYear(d.getYear() + 1);
		String token = mRawToken.replace("#EXPIRATION#", new Long(d.getTime() / 1000).toString());
		Integer end = token.indexOf("&HMACSHA256=");
		token = token.replace("#SIGNATURE#", buildSignatureFor(token.substring(0, end)));
		return token;
	}

	private String buildExpiredRawToken() throws InvalidKeyException, NoSuchAlgorithmException, IllegalStateException, UnsupportedEncodingException {
		Date d = new Date();
		d.setYear(d.getYear() - 1);
		String token = mRawToken.replace("#EXPIRATION#", new Long(d.getTime() / 1000).toString());
		Integer end = token.indexOf("&HMACSHA256=");
		token = token.replace("#SIGNATURE#", buildSignatureFor(token.substring(0, end)));
		return token;
	}

	private String buildTamperedRawToken() throws InvalidKeyException, NoSuchAlgorithmException, IllegalStateException, UnsupportedEncodingException {
		Date d = new Date();
		d.setYear(d.getYear() + 1);
		String token = mRawToken.replace("#EXPIRATION#", new Long(d.getTime() / 1000).toString());
		Integer end = token.indexOf("&HMACSHA256=");
		token = token.replace("#SIGNATURE#", buildSignatureFor(token.substring(0, end))).replace("urn%3aAudience.Tests", "urn%3atamperedUri");
		return token;		
	}
	
	private final class TesteableReceiver implements IAccessTokenReceiver {

		IAccessToken AccessToken;
		int TokenStatus;	
	
		public void onAccessTokenReceived(IAccessToken token, int tokenStatus) {
			AccessToken = token;
			TokenStatus = tokenStatus;
		}
		
	}

}
