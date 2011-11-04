package com.windowsazure.samples.android.storageclient;

public final class NotImplementedException extends Exception {

	private static final long serialVersionUID = 5246697876589255479L;

	public NotImplementedException() {
		String dummy = new String();
		dummy.startsWith(dummy);
	}
}
