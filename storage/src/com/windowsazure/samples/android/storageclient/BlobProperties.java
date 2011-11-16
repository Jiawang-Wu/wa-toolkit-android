package com.windowsazure.samples.android.storageclient;

import java.util.Date;

public final class BlobProperties {

	protected BlobType blobType;

	public String cacheControl;

	public String contentEncoding;

	public String contentLanguage;
	public String contentMD5;
	public String contentType;
	public String eTag;
	public Date lastModified;
	public LeaseStatus leaseStatus;
	public long length;
	public BlobProperties() {
		blobType = BlobType.UNSPECIFIED;
		leaseStatus = LeaseStatus.UNLOCKED;
	}
	
	public BlobProperties(BlobProperties properties) {
		this.copyFrom(properties);
	}
	public BlobType getBlobType() {
		return blobType;
	}

	void copyFrom(BlobProperties properties) {
		blobType = BlobType.UNSPECIFIED;
		leaseStatus = LeaseStatus.UNLOCKED;
		blobType = properties.blobType;
		contentEncoding = properties.contentEncoding;
		contentLanguage = properties.contentLanguage;
		contentType = properties.contentType;
		eTag = properties.eTag;
		leaseStatus = properties.leaseStatus;
		length = properties.length;
		lastModified = properties.lastModified;
	}
}
