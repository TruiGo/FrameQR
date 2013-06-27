/**
 * 
 */
package com.alipay.android.appHall.bussiness;

import org.json.JSONObject;

import android.graphics.BitmapFactory;

import com.alipay.android.net.http.DataParser;
import com.alipay.android.util.JsonConvert;

/**
 * @author sanping.li
 * 数据解析
 *
 */
public class JsonParser implements DataParser {
	public static final int TYPE_IMAGE = 1;
	

	@Override
	public Object parseStream(Object object, int mimeType) throws Exception {
		Object ret = null;
		switch(mimeType){
		case TYPE_NORMAL:
			JSONObject jsonResponse = new JSONObject((String)object);
			ret = JsonConvert.Json2Map(jsonResponse);
			break;
		case TYPE_IMAGE:
		    byte[] datas = (byte[]) object;
		    ret = BitmapFactory.decodeByteArray(datas, 0, datas.length);
			break;
		default:
		    ret = object;
		    break;
		}
		return ret;
	}


}
