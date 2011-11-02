package com.windowsazure.samples.android.storageclient;

import java.net.URI;
import java.util.HashMap;

final class BlobAttributes
{

    public BlobAttributes()
    {
        metadata = new HashMap();
        properties = new BlobProperties();
    }

    protected HashMap metadata;
    protected BlobProperties properties;
    protected URI uri;
    public String snapshotID;
}
