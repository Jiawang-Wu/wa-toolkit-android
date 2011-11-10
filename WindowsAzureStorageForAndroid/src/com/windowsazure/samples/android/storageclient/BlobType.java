package com.windowsazure.samples.android.storageclient;

public enum BlobType {
	UNSPECIFIED("Unspecified"), BLOCK_BLOB("BlockBlob"), PAGE_BLOB("PageBlob");

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

	public static BlobType getDefault() {
		return UNSPECIFIED;
	}

	private final String value;

	BlobType(String value) {
		this.value = value;
	}

	public String toValue() {
		return value;
	}

}
