/**
 * 
 */
package com.alipay.android.core;

import java.util.HashMap;
import java.util.Map;

/**
 * @author sanping.li
 *
 */
public class ParamString {
    private Map<String, String> params;

    public ParamString(String ps) {
        params = new HashMap<String, String>();
        if (ps != null && ps.length() > 0) {
            String[] strs = ps.split("&");
            String[] pairs = null;
            for (String str : strs) {
                pairs = str.split("=");
                if (pairs.length >= 2) {
                    params.put(pairs[0], pairs[1]);
                }
            }
        }
    }

    public String getValue(String key) {
        return params.get(key);
    }

    public void deleteValue(String key) {
        params.remove(key);
    }

    public void addPair(String key, Object value) {
        if (value == null)
            return;
        params.put(key, value.toString());
    }

    public String toString() {
        if (params.isEmpty())
            return null;

        StringBuffer buffer = new StringBuffer();
        for (String key : params.keySet()) {
            if (buffer.length() > 0)
                buffer.append('&');
            buffer.append(key + "=" + params.get(key));
        }
        return buffer.toString();
    }
}
