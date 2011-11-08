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
	public BlobProperties(BlobProperties blobProperties) {
		blobType = BlobType.UNSPECIFIED;
		leaseStatus = LeaseStatus.UNLOCKED;
		blobType = blobProperties.blobType;
		contentEncoding = blobProperties.contentEncoding;
		contentLanguage = blobProperties.contentLanguage;
		contentType = blobProperties.contentType;
		eTag = blobProperties.eTag;
		leaseStatus = blobProperties.leaseStatus;
		length = blobProperties.length;
		lastModified = blobProperties.lastModified;
	}
	public BlobType getBlobType() {
		return blobType;
	}
}
