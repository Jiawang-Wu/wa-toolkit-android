package com.microsoft.samples.windowsazure.android.accesscontrol.core;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Serializable;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class IdentityProvidersRepository implements IIdentityProviderRepository, Serializable {

	private static final long serialVersionUID = 1L;

	private String mIdentityProvidersUri;
	
	public IdentityProvidersRepository(String identityProvidersUri) {
		mIdentityProvidersUri = identityProvidersUri;
	}
	
	@Override
	public IdentityProviderMetadata[] getIndentityProvidersMetadata() {		
		IdentityProviderMetadata[] result = null;
		
		try {
	    	HttpGet request = new HttpGet(mIdentityProvidersUri);
	    	HttpClient client = new DefaultHttpClient();
	    	HttpResponse response;
				response = client.execute(request);
	    	
	    	BufferedReader r = new BufferedReader(new InputStreamReader(response.getEntity().getContent()));
	    	StringBuilder total = new StringBuilder();
	    	String line;
	    	
	    	while ((line = r.readLine()) != null) {
	    	    total.append(line);
	    	}
	    	
	    	String json = total.toString();
	    	JSONArray providersArray = new JSONArray(json);
	    	result = new IdentityProviderMetadata[providersArray.length()];
	    	for (int i = 0; i < providersArray.length(); i++) {
	    		JSONObject providerObject = providersArray.getJSONObject(i);
	    		result[i] = new IdentityProviderMetadata(
	    				providerObject.getString("Name"), 
	    				providerObject.getString("LoginUrl"), 
	    				providerObject.getString("LogoutUrl"), 
	    				providerObject.getString("ImageUrl")
	    				);
	    	}	    	
		} catch (IllegalStateException e) { 
		} catch (ClientProtocolException e) {
		} catch (IOException e) {
		} catch (JSONException e) {
		}
		
		return result;
	}
	
}
