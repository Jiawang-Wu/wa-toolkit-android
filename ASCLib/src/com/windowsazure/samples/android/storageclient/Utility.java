package com.windowsazure.samples.android.storageclient;

import java.net.URI;

public class Utility 
{
    protected static boolean determinePathStyleFromUri(URI endpointUri, String accountName)
    {
        if(accountName == null)
        {
            return !isNullOrEmpty(endpointUri.getPath());
        }

        String s1 = endpointUri.getPath();
        if(!isNullOrEmpty(s1) && s1.startsWith("/"))
        {
            s1 = s1.substring(1);
        }
        
        if(isNullOrEmpty(s1) || endpointUri.getHost().startsWith(accountName))
        {
            return false;
        }
        
        return !isNullOrEmpty(s1) && s1.startsWith(accountName);
    }

    protected static boolean isNullOrEmpty(String s)
    {
        return s == null || s.length() == 0;
    }

    protected static void assertNotNull(String description, Object object)
    {
        if(object == null)
        {
        	throw new IllegalArgumentException(description);
        }
    }

    protected static void assertNotNullOrEmpty(String description, String string)
    {
        assertNotNull(description, string);
        if(string.length() == 0)
        {
            throw new IllegalArgumentException("The argument must not be an empty string or null:".concat(description));
        }
    }
}
