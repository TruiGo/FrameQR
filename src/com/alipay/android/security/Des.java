package com.alipay.android.security;
import java.security.Key;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;

import com.alipay.android.util.Base64;

public class Des
{
	public static String encrypt(String content, String strkey)
	{
		if(content.equals(""))
		{
			return "";
		}
		else
		return doFinal(Cipher.ENCRYPT_MODE, content, strkey);
	}
	
	public static String decrypt(String content, String strkey)
	{
		if(content.equals(""))
		{
			return "";
		}
		else
		return doFinal(Cipher.DECRYPT_MODE, content, strkey);
	}
	
	public static String doFinal(int opmode, String content, String strkey)
	{
		try
		{
			Key key = new SecretKeySpec(strkey.getBytes(), "DES");
			Cipher cipher = Cipher.getInstance( "DES" );
			cipher.init( opmode, key );

			//
			// make input
			byte plaintext[] = null;
			if( opmode  == Cipher.DECRYPT_MODE )
				plaintext = Base64.decode( content );
			else
				plaintext = content.getBytes( "UTF-8" );
			

			byte[] output = cipher.doFinal( plaintext );
			
			
			//
			// make output
			String Ciphertext = null;
			if( opmode  == Cipher.DECRYPT_MODE )
				Ciphertext = new String( output );
			else
				Ciphertext = Base64.encode( output);
			
			return Ciphertext;

		} 
		catch (Exception e)
		{
			e.printStackTrace();
			
			return null;
		}
	}
}
