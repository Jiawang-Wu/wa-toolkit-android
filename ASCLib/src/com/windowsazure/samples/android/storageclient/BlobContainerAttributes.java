package com.windowsazure.samples.android.storageclient;

import java.net.URI;
import java.util.HashMap;

final class BlobContainerAttributes
{

    protected BlobContainerAttributes()
    {
    }

    protected HashMap metadata;
    protected BlobContainerProperties properties;
    protected String name;
    protected URI uri;
}
