package com.windowsazure.samples.android.storageclient;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import javax.crypto.Mac;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

public final class StorageKey
{

    public static synchronized String computeMacSha256(StorageKey storagekey, String s)
        throws InvalidKeyException, IllegalArgumentException
    {
        if(storagekey.m_Hmacsha256 == null)
            storagekey.initHmacSha256();
        byte abyte0[] = null;
        try
        {
            abyte0 = s.getBytes("UTF8");
        }
        catch(UnsupportedEncodingException unsupportedencodingexception)
        {
            throw new IllegalArgumentException(unsupportedencodingexception);
        }
        return Base64.encode(storagekey.m_Hmacsha256.doFinal(abyte0));
    }

    public static synchronized String computeMacSha512(StorageKey storagekey, String s)
        throws IllegalArgumentException, InvalidKeyException
    {
        if(storagekey.m_Hmacsha512 == null)
            storagekey.initHmacSha512();
        byte abyte0[] = null;
        try
        {
            abyte0 = s.getBytes("UTF8");
        }
        catch(UnsupportedEncodingException unsupportedencodingexception)
        {
            throw new IllegalArgumentException(unsupportedencodingexception);
        }
        return Base64.encode(storagekey.m_Hmacsha512.doFinal(abyte0));
    }

    public StorageKey(byte abyte0[])
    {
        setKey(abyte0);
    }

    public String getBase64EncodedKey()
    {
        return Base64.encode(m_Key);
    }

    public byte[] getKey()
    {
        byte abyte0[] = (byte[])m_Key.clone();
        return abyte0;
    }

    public void setKey(byte abyte0[])
    {
        m_Key = abyte0;
        m_Hmacsha256 = null;
        m_Hmacsha512 = null;
        m_Key256 = null;
        m_Key512 = null;
    }

    public void setKey(String s)
        throws IOException
    {
        m_Key = Base64.decode(s);
    }

    private void initHmacSha256()
        throws InvalidKeyException
    {
        m_Key256 = new SecretKeySpec(m_Key, "HmacSHA256");
        try
        {
            m_Hmacsha256 = Mac.getInstance("HmacSHA256");
        }
        catch(NoSuchAlgorithmException nosuchalgorithmexception)
        {
            throw new IllegalArgumentException();
        }
        m_Hmacsha256.init(m_Key256);
    }

    private void initHmacSha512()
        throws InvalidKeyException
    {
        m_Key512 = new SecretKeySpec(m_Key, "HmacSHA512");
        try
        {
            m_Hmacsha512 = Mac.getInstance("HmacSHA512");
        }
        catch(NoSuchAlgorithmException nosuchalgorithmexception)
        {
            throw new IllegalArgumentException();
        }
        m_Hmacsha512.init(m_Key512);
    }

    private Mac m_Hmacsha256;
    private Mac m_Hmacsha512;
    private SecretKey m_Key256;
    private SecretKey m_Key512;
    private byte m_Key[];
}
