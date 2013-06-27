/**
 * 
 */
package com.alipay.android.util;

import com.alipay.android.security.RSA;

/**
 * @author sanping.li
 *
 */
public class SecureUtil {

    public static String encrpt(String content,String rsaPK,String rsaTS)
    {
        return RSA.encrypt(content + rsaTS, rsaPK);
        
    }
}
