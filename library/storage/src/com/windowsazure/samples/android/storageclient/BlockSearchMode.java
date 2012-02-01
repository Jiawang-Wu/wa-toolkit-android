package com.windowsazure.samples.android.storageclient;

public enum BlockSearchMode {
	COMMITTED("Committed"), UNCOMMITTED("Uncommitted"), LATEST("Latest");

	public static BlockSearchMode fromValue(String value) {
		if (value != null) {
			for (BlockSearchMode blockSearchMode : values()) {
				if (blockSearchMode.value.equals(value)) {
					return blockSearchMode;
				}
			}
		}

		throw new IllegalArgumentException("Invalid BlockSearchMode: " + value);
	}

	private final String value;

	BlockSearchMode(String value) {
		this.value = value;
	}

	public String toValue() {
		return value;
	}
	
}
