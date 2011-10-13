package com.windowsazure.storageclient;

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
        throws NotImplementedException, InvalidKeyException, IllegalArgumentException
    {
    	throw new NotImplementedException();
    }

    public static synchronized String computeMacSha512(StorageKey storagekey, String s)
        throws NotImplementedException, IllegalArgumentException, InvalidKeyException
    {
    	throw new NotImplementedException();
    }

    public StorageKey(byte abyte0[]) throws NotImplementedException
    {
    	throw new NotImplementedException();
    }

    public String getBase64EncodedKey() throws NotImplementedException
    {
    	throw new NotImplementedException();
    }

    public byte[] getKey() throws NotImplementedException
    {
    	throw new NotImplementedException();
    }

    public void setKey(byte abyte0[]) throws NotImplementedException
    {
    	throw new NotImplementedException();
    }

    public void setKey(String s)
        throws NotImplementedException, IOException
    {
    	throw new NotImplementedException();
    }

    private void initHmacSha256()
        throws NotImplementedException, InvalidKeyException
    {
    	throw new NotImplementedException();
    }

    private void initHmacSha512()
        throws NotImplementedException, InvalidKeyException
    {
    	throw new NotImplementedException();
    }

    private Mac m_Hmacsha256;
    private Mac m_Hmacsha512;
    private SecretKey m_Key256;
    private SecretKey m_Key512;
    private byte m_Key[];
}
