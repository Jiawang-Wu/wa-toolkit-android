package com.windowsazure.samples.android.storageclient;

import java.io.UnsupportedEncodingException;
import java.security.InvalidKeyException;
import java.util.*;

final class SharedAccessSignatureHelper
{

    SharedAccessSignatureHelper()
    {
    }

    protected static UriQueryBuilder generateSharedAccessSignature(SharedAccessPolicy sharedaccesspolicy, String s, String s1, String s2)
        throws IllegalArgumentException, StorageException
    {
        Utility.assertNotNullOrEmpty("resourceType", s1);
        Utility.assertNotNull("signature", s2);
        UriQueryBuilder uriquerybuilder = new UriQueryBuilder();
        if(sharedaccesspolicy != null)
        {
            String s3 = SharedAccessPolicy.permissionsToString(sharedaccesspolicy.permissions);
            if(Utility.isNullOrEmpty(s3))
                s3 = null;
            String s4 = Utility.getUTCTimeOrEmpty(sharedaccesspolicy.sharedAccessStartTime);
            if(!Utility.isNullOrEmpty(s4))
                uriquerybuilder.add("st", s4);
            String s5 = Utility.getUTCTimeOrEmpty(sharedaccesspolicy.sharedAccessExpiryTime);
            if(!Utility.isNullOrEmpty(s5))
                uriquerybuilder.add("se", s5);
            if(!Utility.isNullOrEmpty(s3))
                uriquerybuilder.add("sp", s3);
        }
        uriquerybuilder.add("sr", s1);
        if(!Utility.isNullOrEmpty(s))
            uriquerybuilder.add("si", s);
        if(!Utility.isNullOrEmpty(s2))
            uriquerybuilder.add("sig", s2);
        return uriquerybuilder;
    }

    protected static String generateSharedAccessSignatureHash(SharedAccessPolicy sharedaccesspolicy, String s, String s1, CloudBlobClient cloudblobclient)
        throws InvalidKeyException, StorageException, NotImplementedException
    {
        Utility.assertNotNullOrEmpty("resourceName", s1);
        Utility.assertNotNull("client", cloudblobclient);
        String s2 = null;
        if(sharedaccesspolicy == null)
        {
            Utility.assertNotNullOrEmpty("groupPolicyIdentifier", s);
            s2 = String.format("%s\n%s\n%s\n%s\n%s", new Object[] {
                "", "", "", s1, s
            });
        } else
        {
            if(sharedaccesspolicy.sharedAccessExpiryTime == null)
                throw new IllegalArgumentException("Policy Expiry time is mandatory and cannot be null");
            if(sharedaccesspolicy.permissions == null)
                throw new IllegalArgumentException("Policy permissions are mandatory and cannot be null");
            s2 = String.format("%s\n%s\n%s\n%s\n%s", new Object[] {
                SharedAccessPolicy.permissionsToString(sharedaccesspolicy.permissions), Utility.getUTCTimeOrEmpty(sharedaccesspolicy.sharedAccessStartTime), Utility.getUTCTimeOrEmpty(sharedaccesspolicy.sharedAccessExpiryTime), s1, s != null ? s : ""
            });
        }
        s2 = Utility.safeDecode(s2);
        String s3 = cloudblobclient.getCredentials().computeHmac256(s2);
        return s3;
    }

    protected static StorageCredentialsSharedAccessSignature parseQuery(HashMap hashmap)
        throws IllegalArgumentException, StorageException
    {
        String s = null;
        String s1 = null;
        String s2 = null;
        String s3 = null;
        String s4 = null;
        String s5 = null;
        String s6 = null;
        boolean flag = false;
        StorageCredentialsSharedAccessSignature storagecredentialssharedaccesssignature = null;
        Iterator iterator = hashmap.entrySet().iterator();
        do
        {
            if(!iterator.hasNext())
                break;
            java.util.Map.Entry entry = (java.util.Map.Entry)iterator.next();
            String s9 = ((String)entry.getKey()).toLowerCase(Locale.US);
            if(s9.equals("st"))
            {
                s1 = ((String[])entry.getValue())[0];
                flag = true;
            } else
            if(s9.equals("se"))
            {
                s2 = ((String[])entry.getValue())[0];
                flag = true;
            } else
            if(s9.equals("sp"))
            {
                s4 = ((String[])entry.getValue())[0];
                flag = true;
            } else
            if(s9.equals("sr"))
            {
                s3 = ((String[])entry.getValue())[0];
                flag = true;
            } else
            if(s9.equals("si"))
            {
                s5 = ((String[])entry.getValue())[0];
                flag = true;
            } else
            if(s9.equals("sig"))
            {
                s = ((String[])entry.getValue())[0];
                flag = true;
            } else
            if(s9.equals("sv"))
            {
                s6 = ((String[])entry.getValue())[0];
                flag = true;
            }
        } while(true);
        if(flag)
        {
            if(s == null || s3 == null)
            {
                String s7 = "Missing mandatory parameters for valid Shared Access Signature";
                throw new IllegalArgumentException(s7);
            }
            UriQueryBuilder uriquerybuilder = new UriQueryBuilder();
            if(!Utility.isNullOrEmpty(s1))
                uriquerybuilder.add("st", s1);
            if(!Utility.isNullOrEmpty(s2))
                uriquerybuilder.add("se", s2);
            if(!Utility.isNullOrEmpty(s4))
                uriquerybuilder.add("sp", s4);
            uriquerybuilder.add("sr", s3);
            if(!Utility.isNullOrEmpty(s5))
                uriquerybuilder.add("si", s5);
            if(!Utility.isNullOrEmpty(s6))
                uriquerybuilder.add("sv", s6);
            if(!Utility.isNullOrEmpty(s))
                uriquerybuilder.add("sig", s);
            String s8 = uriquerybuilder.toString();
            storagecredentialssharedaccesssignature = new StorageCredentialsSharedAccessSignature(s8);
        }
        return storagecredentialssharedaccesssignature;
    }
}
