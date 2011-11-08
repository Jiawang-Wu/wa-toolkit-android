package com.windowsazure.samples.android.storageclient;

enum LeaseStatus {
	LOCKED("locked"), UNLOCKED("unlocked"), UNSPECIFIED("unspecified");

	public static LeaseStatus fromValue(String value) {
		if (value != null) {
			for (LeaseStatus blobType : values()) {
				if (blobType.value.equals(value)) {
					return blobType;
				}
			}
		}

		throw new IllegalArgumentException("Invalid LeaseStatus: " + value);
	}

	public static LeaseStatus getDefault() {
		return UNSPECIFIED;
	}

	private final String value;

	LeaseStatus(String value) {
		this.value = value;
	}

	public String toValue() {
		return value;
	}

}
