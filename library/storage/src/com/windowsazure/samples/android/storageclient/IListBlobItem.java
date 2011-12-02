package com.windowsazure.samples.android.storageclient;

import java.net.URI;
import java.net.URISyntaxException;

interface IListBlobItem {

	public abstract CloudBlobContainer getContainer()
			throws NotImplementedException, URISyntaxException,
			StorageException;

	public abstract URI getUri() throws NotImplementedException;
	
}
