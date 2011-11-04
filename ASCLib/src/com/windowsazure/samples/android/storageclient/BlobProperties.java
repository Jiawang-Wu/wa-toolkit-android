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
	public BlobProperties(BlobProperties blobproperties) {
		blobType = BlobType.UNSPECIFIED;
		leaseStatus = LeaseStatus.UNLOCKED;
		blobType = blobproperties.blobType;
		contentEncoding = blobproperties.contentEncoding;
		contentLanguage = blobproperties.contentLanguage;
		contentType = blobproperties.contentType;
		eTag = blobproperties.eTag;
		leaseStatus = blobproperties.leaseStatus;
		length = blobproperties.length;
		lastModified = blobproperties.lastModified;
	}
	public BlobType getBlobType() {
		return blobType;
	}
}
