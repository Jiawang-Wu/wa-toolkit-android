package com.windowsazure.samples.android.storageclient;

public final class Credentials {

	private String m_AccountName;

	private StorageKey m_Key;

	public Credentials(String s, byte abyte0[]) {
		if (s == null || s.length() == 0)
			throw new IllegalArgumentException("Invalid accountName");
		if (abyte0 == null) {
			throw new IllegalArgumentException("Invalid key");
		} else {
			m_AccountName = s;
			m_Key = new StorageKey(abyte0);
			return;
		}
	}

	public Credentials(String s, String s1) {
		this(s, Base64.decode(s1));
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
	protected void setAccountName(String s) {
		m_AccountName = s;
	}
}
