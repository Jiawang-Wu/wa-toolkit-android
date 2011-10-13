package com.windowsazure.storageclient;

import java.net.URI;
import java.net.URISyntaxException;

public interface IListBlobItem
{

    public abstract CloudBlobContainer getContainer()
        throws NotImplementedException, URISyntaxException, StorageException;

    public abstract CloudBlobDirectory getParent()
        throws NotImplementedException, URISyntaxException, StorageException;

    public abstract URI getUri() throws NotImplementedException;
}
