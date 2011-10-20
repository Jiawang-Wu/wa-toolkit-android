package com.windowsazure.samples.android.storageclient;

import java.io.Serializable;
import java.util.HashMap;

public final class StorageExtendedErrorInformation
    implements Serializable
{

    public StorageExtendedErrorInformation()
    {
    	additionalDetails = new HashMap();
    }

    private static final long serialVersionUID = 0x15310acda3ffd515L;
    public HashMap additionalDetails;
    public String errorCode;
    public String errorMessage;
}
