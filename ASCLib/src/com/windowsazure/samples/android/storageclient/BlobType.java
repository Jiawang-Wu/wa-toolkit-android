package com.windowsazure.samples.android.storageclient;

enum BlobType {
	UNSPECIFIED("Unspecified"), BLOCK_BLOB("BlockBlob"), PAGE_BLOB("PageBlob");

	private final String value;

	BlobType(String value) {
		this.value = value;
	}

	public static BlobType fromValue(String value) {
		if (value != null) {
			for (BlobType blobType : values()) {
				if (blobType.value.equals(value)) {
					return blobType;
				}
			}
		}

		throw new IllegalArgumentException("Invalid BlobType: " + value);
	}

	public String toValue() {
		return value;
	}

	public static BlobType getDefault() {
		return UNSPECIFIED;
	}

}
