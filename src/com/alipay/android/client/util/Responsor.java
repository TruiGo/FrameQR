package com.alipay.android.client.util;

import org.json.JSONObject;

public class Responsor {
    /**
	 * 
	 */

	final public String type;

    final public int status;

    final public JSONObject obj;

    public String memo; // need modify to meet spec.

    public Responsor(String requestType, int resultStatus, JSONObject jsonObject, String jsonMemo) {
       
		type = requestType;
        status = resultStatus;
        obj = jsonObject;
        memo = jsonMemo;
    }

}