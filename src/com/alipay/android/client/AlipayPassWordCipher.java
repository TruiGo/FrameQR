package com.alipay.android.client;

import java.security.SecureRandom;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.DESedeKeySpec;

public class AlipayPassWordCipher{
	
	private static final String PASSWORD_CRYPT_KEY = "_alipay__android__alipay";
	private final static String DES = "DESede";
	
	public static byte[] encrypt(byte[] src, byte[] key)throws Exception { 
         SecureRandom sr = new SecureRandom(); 
         DESedeKeySpec dks = new DESedeKeySpec(key); 

         SecretKeyFactory keyFactory = SecretKeyFactory.getInstance(DES); 
         SecretKey securekey = keyFactory.generateSecret(dks); 

         Cipher cipher = Cipher.getInstance(DES); 
         cipher.init(Cipher.ENCRYPT_MODE, securekey, sr); 

         return cipher.doFinal(src); 
    }
	
      public static byte[] decrypt(byte[] src, byte[] key)throws Exception { 
         SecureRandom sr = new SecureRandom(); 
         DESedeKeySpec dks = new DESedeKeySpec(key); 

         SecretKeyFactory keyFactory = SecretKeyFactory.getInstance(DES); 
         SecretKey securekey = keyFactory.generateSecret(dks); 

         Cipher cipher = Cipher.getInstance(DES); 
         cipher.init(Cipher.DECRYPT_MODE, securekey, sr); 
         return cipher.doFinal(src); 
      }
      
 
       public static byte[] hex2byte(byte[] b) { 
          if((b.length%2)!=0) 
              throw new IllegalArgumentException("length is not even number"); 
          byte[] b2 = new byte[b.length/2]; 
          for (int n = 0; n < b.length; n+=2) { 
              String item = new String(b,n,2); 
              b2[n/2] = (byte)Integer.parseInt(item,16); 
             } 
         return b2; 
      }
       
       public static String byte2hex(byte[] b) { 
           String hs = ""; 
           String stmp = ""; 
           for (int n = 0; n < b.length; n++) { 
                   stmp = (java.lang.Integer.toHexString(b[n] & 0XFF)); 
                   if (stmp.length() == 1) 
                           hs = hs + "0" + stmp; 
                   else 
                           hs = hs + stmp; 
           } 
           return hs.toUpperCase(); 
       }
       
       public final static String encrypt(String password){ 
    	      try { 
    	          return byte2hex(encrypt(password.getBytes(),PASSWORD_CRYPT_KEY.getBytes())); 
    	      }catch(Exception e) { 
    	      } 
    	      return null; 
       }
       
       public final static String decrypt(String data){ 
           try { 
              return new String(decrypt(hex2byte(data.getBytes()),PASSWORD_CRYPT_KEY.getBytes())); 
          }catch(Exception e) { 
          } 
          return null; 
      }
       
//       public static void testCipher()
//       {
//     	  String pwd = "qdafgafd889123"; 
//     	  
//     	  String encryptStr = AlipayPassWordCipher.encrypt(pwd);
//     	  
//     	  System.out.println("111++: " + encryptStr);
//     	  
//     	  String decryptStr = AlipayPassWordCipher.decrypt(encryptStr);
//     	  
//     	  System.out.println("222++: " + decryptStr);
//     	  
//     	  
//       }

}