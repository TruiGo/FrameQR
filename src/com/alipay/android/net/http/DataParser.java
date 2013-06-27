/**
 * 
 */
package com.alipay.android.net.http;

/**
 * @author sanping.li@alipay.com
 *
 */
public interface DataParser {
    public static final int TYPE_NORMAL = 0;
	public Object parseStream(Object object,int mimeType) throws Exception;
}
