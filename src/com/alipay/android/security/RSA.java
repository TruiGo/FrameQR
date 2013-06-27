/**
 * created since 2009-7-20
 */
package com.alipay.android.security;

import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.PublicKey;
import java.security.spec.X509EncodedKeySpec;

import javax.crypto.Cipher;

import com.alipay.android.util.Base64;

/**
 */
public class RSA
{

	private static final String ALGORITHM = "RSA";

	/**
	 * @param algorithm
	 * @param ins
	 * @return
	 * @throws NoSuchAlgorithmException
	 * @throws AlipayException
	 */
	private static PublicKey getPublicKeyFromX509(String algorithm,
			String bysKey) 
	{
		try
		{
			byte[] decodedKey = Base64.decode( bysKey );
			X509EncodedKeySpec x509 = new X509EncodedKeySpec(decodedKey);

			KeyFactory keyFactory = KeyFactory.getInstance(algorithm);		
			return keyFactory.generatePublic(x509);
		}
		catch(Exception e)
		{
			return null;
		}
	}

	public static String encrypt(String content, String key)
	{
		if("".equals(content)){
			return "";
		}
		
		try
		{
			PublicKey pubkey = getPublicKeyFromX509(ALGORITHM, key);

			if(pubkey == null)
			{
				return null;
			}
			Cipher cipher = Cipher.getInstance("RSA/ECB/PKCS1Padding");
			cipher.init(Cipher.ENCRYPT_MODE, pubkey);

			byte plaintext[] = content.getBytes("UTF-8");
			byte[] output = cipher.doFinal( plaintext );

			String s = new String(Base64.encode(output));
			
			return s;

		} 
		catch (Exception e)
		{
			e.printStackTrace();
			return null;
		}
	}
}
