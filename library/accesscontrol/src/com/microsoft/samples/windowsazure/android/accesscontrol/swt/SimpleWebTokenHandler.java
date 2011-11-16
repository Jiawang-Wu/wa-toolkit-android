package com.microsoft.samples.windowsazure.android.accesscontrol.swt;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Document;
import org.xml.sax.SAXException;

import android.text.Html;
import android.text.Spanned;
import android.util.Base64;

import com.microsoft.samples.windowsazure.android.accesscontrol.core.AccessTokenConstants;
import com.microsoft.samples.windowsazure.android.accesscontrol.core.IAccessToken;
import com.microsoft.samples.windowsazure.android.accesscontrol.core.IAccessTokenHandler;
import com.microsoft.samples.windowsazure.android.accesscontrol.core.IAccessTokenReceiver;

public class SimpleWebTokenHandler implements IAccessTokenHandler, Serializable {

	private static final long serialVersionUID = 1L;
	
	private IAccessTokenReceiver mTokenReceiver;
	private String mAudienceUri;
	private String mSWTSigningKey;
	
	public SimpleWebTokenHandler(String audienceUri, String swtSigningKey) {
		mAudienceUri = audienceUri;
		mSWTSigningKey = swtSigningKey;		
	}
	
	public SimpleWebTokenHandler(IAccessTokenReceiver tokenReceiver, String audienceUri, String swtSigningKey) {
		mTokenReceiver = tokenReceiver;
		mAudienceUri = audienceUri;
		mSWTSigningKey = swtSigningKey;
	}
	
	public static boolean isValid(SimpleWebToken token, String audienceUri, String swtSigningKey) throws NoSuchAlgorithmException, InvalidKeyException, IllegalStateException, UnsupportedEncodingException {
		String[] chunks = token.getRawToken().split("&HMACSHA256=");
    	
    	if(chunks.length < 2)
    		return false;
    
    	if(token.isExpired())
    		return false;
    	
    	if(!token.getAudience().equals(audienceUri))
    		return false;
    	
    	SecretKeySpec signingKey = new SecretKeySpec(Base64.decode(swtSigningKey, Base64.DEFAULT), AccessTokenConstants.HMAC_SHA256);
        
    	Mac mac = Mac.getInstance(AccessTokenConstants.HMAC_SHA256);
    	mac.init(signingKey);
    	byte[] rawHmac = mac.doFinal(chunks[0].getBytes("ASCII"));
    	
    	return new String(Base64.encode(rawHmac, Base64.DEFAULT)).trim().equals(URLDecoder.decode(chunks[1]));
	}
	
	@Override
	public IAccessToken parseToken(String xml) {
    	SimpleWebToken parsedToken = null;
    	Integer parsedTokenStatus = AccessTokenConstants.UNKNOWN_TOKEN;
    				        	
		try {
			JSONObject response = new JSONObject(xml);
			String token = response.getString("securityToken");
        	Spanned decodedToken = Html.fromHtml(token);
        	
        	InputStream inputStream = new ByteArrayInputStream(decodedToken.toString().getBytes("UTF-16"));
        	DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();
            Document dom = builder.parse(inputStream);
            
            String simpleWebTokenBase64 = dom.getChildNodes().item(0).getTextContent();
            
            byte[] rawToken = Base64.decode(simpleWebTokenBase64, Base64.DEFAULT);
            
            String simpleWebToken = new String(rawToken);
            parsedToken = new SimpleWebToken(simpleWebToken);
            
            parsedTokenStatus = SimpleWebTokenHandler.isValid(parsedToken, mAudienceUri, mSWTSigningKey) ? AccessTokenConstants.VALID_TOKEN : AccessTokenConstants.INVALID_TOKEN;
            
		} catch (JSONException e) {
		} catch (InvalidKeyException e) {
		} catch (NoSuchAlgorithmException e) {
		} catch (IllegalStateException e) {
		} catch (UnsupportedEncodingException e) {
		} catch (ParserConfigurationException e) {			
		} catch (SAXException e) {
		} catch (IOException e) {
		}
		
		// listener callback
		if (mTokenReceiver != null) mTokenReceiver.onAccessTokenReceived(parsedToken, parsedTokenStatus);
		
		return parsedToken;
	}

	public IAccessTokenReceiver getTokenReceiver() {
		return mTokenReceiver;
	}
	
	public void setTokenReceiver(IAccessTokenReceiver tokenReceiver) {
		mTokenReceiver = tokenReceiver;
	}
	
}
