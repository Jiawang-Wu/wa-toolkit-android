package com.windowsazure.samples.android.storageclient;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

import javax.crypto.Mac;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

import android.util.Base64;

final class StorageKey {

	public static synchronized String computeMacSha256(StorageKey key,
			String canonicalRequestString) throws InvalidKeyException, IllegalArgumentException {
		if (key.m_Hmacsha256 == null)
			key.initHmacSha256();
		byte canonicalRequestBytes[] = null;
		try {
			canonicalRequestBytes = canonicalRequestString.getBytes("UTF8");
		} catch (UnsupportedEncodingException unsupportedencodingexception) {
			throw new IllegalArgumentException(unsupportedencodingexception);
		}
		return Base64.encodeToString(key.m_Hmacsha256.doFinal(canonicalRequestBytes), Base64.NO_WRAP);
	}

	public static synchronized String computeMacSha512(StorageKey storagekey,
			String canonicalRequestString) throws IllegalArgumentException, InvalidKeyException {
		if (storagekey.m_Hmacsha512 == null)
			storagekey.initHmacSha512();
		byte canonicalRequestBytes[] = null;
		try {
			canonicalRequestBytes = canonicalRequestString.getBytes("UTF8");
		} catch (UnsupportedEncodingException unsupportedencodingexception) {
			throw new IllegalArgumentException(unsupportedencodingexception);
		}
		return Base64.encodeToString(storagekey.m_Hmacsha512.doFinal(canonicalRequestBytes), Base64.NO_WRAP);
	}

	private Mac m_Hmacsha256;

	private Mac m_Hmacsha512;

	private SecretKey m_Key256;

	private SecretKey m_Key512;

	private byte m_Key[];

	public StorageKey(byte key[]) {
		setKey(key);
	}

	public String getBase64EncodedKey() {
		return Base64.encodeToString(m_Key, Base64.NO_WRAP);
	}

	public byte[] getKey() {
		byte key[] = m_Key.clone();
		return key;
	}
	private void initHmacSha256() throws InvalidKeyException {
		m_Key256 = new SecretKeySpec(m_Key, "HmacSHA256");
		try {
			m_Hmacsha256 = Mac.getInstance("HmacSHA256");
		} catch (NoSuchAlgorithmException nosuchalgorithmexception) {
			throw new IllegalArgumentException();
		}
		m_Hmacsha256.init(m_Key256);
	}
	private void initHmacSha512() throws InvalidKeyException {
		m_Key512 = new SecretKeySpec(m_Key, "HmacSHA512");
		try {
			m_Hmacsha512 = Mac.getInstance("HmacSHA512");
		} catch (NoSuchAlgorithmException nosuchalgorithmexception) {
			throw new IllegalArgumentException();
		}
		m_Hmacsha512.init(m_Key512);
	}
	public void setKey(byte key[]) {
		m_Key = key;
		m_Hmacsha256 = null;
		m_Hmacsha512 = null;
		m_Key256 = null;
		m_Key512 = null;
	}
	public void setKey(String key) throws IOException {
		m_Key = Base64.decode(key, Base64.NO_WRAP);
	}
}
