/**
 * 
 */
package com.alipay.android.appHall.security;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.security.KeyFactory;
import java.security.MessageDigest;
import java.security.PublicKey;
import java.security.spec.X509EncodedKeySpec;

import com.alipay.android.util.Base64;


/**
 * @author sanping.li
 *
 */
public class VerifyHelper {
    private static final String SIGN_ALGORITHM = "MD5withRSA";
    private static final String HASH_ALGORITHM = "MD5";
    private static final String KEY_ALGORITHM = "RSA";
    
    private static final String PUBLIC_KEY="MIGfMA0GCSqGSIb3DQEBAQUAA4GNADCBiQKBgQCiTyQpH9s5luQw15KpRM20UeNaSl5u4YYkcqoCljz4lb5Z8qw6gdU8WG7AENn4kRF3X2hvr42i6IU+j/vGO2DQDuPVaihysDEm2e7rgT1AVTaMHNYdEvbLFPrYqDntSAmH07nknOYEDbobGaQInfJsUf3TUh4rZiAMxC2DnlPftwIDAQAB";
    
    
    public static boolean verify(File file,String digest) throws Exception{
        MessageDigest digester = MessageDigest.getInstance(HASH_ALGORITHM);
        InputStream inputStream = new FileInputStream(file);
        byte[] bytes = new byte[8192];
        int byteCount;
        while ((byteCount = inputStream.read(bytes)) > 0) {
          digester.update(bytes, 0, byteCount);
        }
        byte[] content = digester.digest();
        
        inputStream.close();
        
        return verify(content, digest, PUBLIC_KEY);
    }

    private static boolean verify(byte[] content, String digest, String key) throws Exception{
        PublicKey pubkey = getPublicKeyFromX509(KEY_ALGORITHM, key);

        java.security.Signature signature = java.security.Signature.getInstance(SIGN_ALGORITHM);
        signature.initVerify(pubkey);

        signature.update(content);
        byte[] output = Base64.decode(digest);
       
        return  signature.verify(output);
    }


    private static PublicKey getPublicKeyFromX509(String algorithm,String bysKey) throws Exception{
        byte[] decodedKey = Base64.decode(bysKey);
        X509EncodedKeySpec x509 = new X509EncodedKeySpec(decodedKey);

        KeyFactory keyFactory = KeyFactory.getInstance(algorithm);    
        return keyFactory.generatePublic(x509);
    }
    
    
}
