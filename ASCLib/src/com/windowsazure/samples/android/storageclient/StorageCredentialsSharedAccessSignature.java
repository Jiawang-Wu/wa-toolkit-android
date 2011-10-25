package com.windowsazure.samples.android.storageclient;

import java.net.*;

import org.apache.http.client.methods.HttpRequestBase;

public final class StorageCredentialsSharedAccessSignature extends StorageCredentials
{

 public StorageCredentialsSharedAccessSignature(String s)
 {
     m_Token = s;
 }

 protected Boolean canCredentialsComputeHmac()
 {
     return Boolean.valueOf(false);
 }

 protected Boolean canCredentialsSignRequest()
 {
     return Boolean.valueOf(false);
 }

 protected Boolean canCredentialsSignRequestLite()
 {
     return Boolean.valueOf(false);
 }

 public String computeHmac256(String s)
 {
     return null;
 }

 public String computeHmac512(String s)
 {
     return null;
 }

  protected Boolean doCredentialsNeedTransformUri()
 {
     return Boolean.valueOf(true);
 }

 public String getAccountName()
 {
     return null;
 }

 public String getToken()
 {
     return m_Token;
 }

 public void signRequest(HttpRequestBase request, long l)
 {
 }

 public void signRequestLite(HttpRequestBase request, long l)
 {
 }

 public String toString(Boolean boolean1)
 {
     return String.format("%s=%s", new Object[] {
         "SharedAccessSignature", boolean1.booleanValue() ? m_Token : "[signature hidden]"
     });
 }

public URI transformUri(URI uri)
     throws URISyntaxException, StorageException
 {
     return PathUtility.addToQuery(uri, m_Token);
 }

@Override
public String containerEndpointPostfix() {
	return "";
}
 private String m_Token;
}
