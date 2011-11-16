package com.windowsazure.samples.android.storageclient;

import android.util.Base64;

public final class Credentials {

	private String m_AccountName;

	private StorageKey m_Key;

	public Credentials(String accountName, byte key[]) {
		if (accountName == null || accountName.length() == 0)
			throw new IllegalArgumentException("Invalid accountName");
		if (key == null) {
			throw new IllegalArgumentException("Invalid key");
		} else {
			m_AccountName = accountName;
			m_Key = new StorageKey(key);
			return;
		}
	}

	public Credentials(String accountName, String key) {
		this(accountName, Base64.decode(key, Base64.NO_WRAP));
	}

	public String exportBase64EncodedKey() {
		return getKey().getBase64EncodedKey();
	}

	public byte[] exportKey() {
		return getKey().getKey();
	}

	public String getAccountName() {
		return m_AccountName;
	}

	public StorageKey getKey() {
		return m_Key;
	}
	protected void setAccountName(String accountName) {
		m_AccountName = accountName;
	}
}
