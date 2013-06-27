/**
 * 
 */
package com.alipay.android.net.http;

import java.util.HashMap;

/**
 * @author sanping.li@alipay.com
 *
 */
public class HttpUtils {
	public static HashMap<String, String> getContentType(String string){
		HashMap<String, String> result = new HashMap<String, String>();
		String[] params = string.split(";");
		String[] pairs = null;
		for(String param:params){
			if(param.indexOf('=')==-1){
				pairs = new String[2];
				pairs[0] = "Content-Type";
				pairs[1] = param;
			}else
				pairs = param.split("=");
			result.put(pairs[0], pairs[1]);
		}
		return result;
	}

	//
    // 查找某一字符串string中，特定子串subString的出现次数 
    public static int occurrenceOf(String string, String subString) {
        if (string == null || subString == null)
            return 0;

        int rawLength = string.length();
        string = string.replaceAll(subString, "");
        return (rawLength - string.length()) / subString.length();
    }
}
