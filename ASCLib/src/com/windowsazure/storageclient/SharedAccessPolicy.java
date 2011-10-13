package com.windowsazure.storageclient;

import java.text.ParseException;
import java.util.Date;
import java.util.EnumSet;
import javax.xml.namespace.QName;

public class SharedAccessPolicy
{

    public static EnumSet permissionsFromString(String s) throws NotImplementedException
    {
    	throw new NotImplementedException();
    }

    public static String permissionsToString(EnumSet enumset) throws NotImplementedException
    {
    	throw new NotImplementedException();
    }

    public SharedAccessPolicy() throws NotImplementedException
    {
    }

    public EnumSet permissions;
    public Date sharedAccessExpiryTime;
    public Date sharedAccessStartTime;
}
