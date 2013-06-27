/**
 * 
 */
package com.alipay.android.util;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * @author sanping.li
 *
 */
public class JsonConvert {
    public static HashMap<String, Object> Json2Map(JSONObject object){
        Iterator<?> iterator = object.keys();
        HashMap<String, Object> ret = new HashMap<String, Object>();
        while(iterator.hasNext()){
            String name = iterator.next().toString();
            Object val = object.opt(name);
            if(val instanceof JSONObject){
                ret.put(name,Json2Map((JSONObject) val));
            }else if(val instanceof JSONArray){
                ret.put(name,Json2Array((JSONArray) val));
            }else{
                ret.put(name,val);
            }
        }
        return ret;
    }
    
    /**
     * 将Json对象转换为bean对象，json中数据类型只能是基本类型及其包装类型
     * @param object
     * @param bean
     * @return
     */
    public static <T> T json2Bean(JSONObject object,T bean){
    	Iterator<?> iterator = object.keys();
        while(iterator.hasNext()){
            String name = iterator.next().toString();
            Object val = object.opt(name);
            
            Class<? extends Object> clazz = bean.getClass();
            String firstLetter = name.substring(0,1);
            String beanMethodName = firstLetter.toUpperCase() + name.substring(1);
            
			try {
				Method method = clazz.getMethod("set" + beanMethodName, val.getClass());
            	method.invoke(bean, val);
			} catch (NoSuchMethodException e) {
			} catch (Exception e) {
				e.printStackTrace();
			}
        }
    	return bean;
    }
    
    /**
     * 将JsonArray对象转换为一组对象，json中数据类型只能是基本类型及其包装类型
     * @param object
     * @param bean
     * @return
     */
	@SuppressWarnings("unchecked")
	public static <T> List<T> jArry2BeanList(JSONArray object,T bean){
		List<T> beanList = new ArrayList<T>();
		if(object == null)
			return beanList;
		
    	for(int i = 0 ;i< object.length();i++){
    		try {
				JSONObject jsonObj = object.optJSONObject(i);
				beanList.add(json2Bean(jsonObj,(T)bean.getClass().newInstance()));
			} catch (Exception e) {
				e.printStackTrace();
			}
    	}
    	return beanList;
    }
    
    public static ArrayList<Object> Json2Array(JSONArray array){
        int length = array.length();
        ArrayList<Object> ret = new ArrayList<Object>();
        for(int i =0;i<length;++i){
            Object val = array.opt(i);
            if(val instanceof JSONObject){
                ret.add(Json2Map((JSONObject) val));
            }else if(val instanceof JSONArray){
                ret.add(Json2Array((JSONArray) val));
            }else{
                ret.add(val);
            }
        }
        return ret;
    }
    
    public static String Map2Json(HashMap<String, Object> hashMap){
        if(hashMap==null) return "";
        Set<String> set = hashMap.keySet();
        JSONObject jsonObject = new JSONObject();
        try{
            Object o = null;
            for(String str:set){
                o = hashMap.get(str);
                if(o instanceof HashMap<?, ?>)
                    jsonObject.put(str, new JSONObject((HashMap<?, ?>) o));
                else if(o instanceof ArrayList<?>)
                    jsonObject.put(str, new JSONArray((ArrayList<?>) o));
                else
                    jsonObject.put(str,o);
            }
        }catch(Exception e){}
        return jsonObject.toString();
    }
    
    public static String Array2Json(ArrayList<Object> arrayList){
        if(arrayList==null) return null;
        JSONArray jsonArray = new JSONArray();
        for(Object o:arrayList){
            if(o instanceof HashMap<?, ?>)
                jsonArray.put(new JSONObject((HashMap<?, ?>)o));
            else if(o instanceof ArrayList<?>)
                jsonArray.put(new JSONArray((ArrayList<?>) o));
            else
                jsonArray.put(o);
        }
        return jsonArray.toString();
    }
    
    public static String ArrayString2Json(ArrayList<String> arrayList){
        JSONArray jsonArray = new JSONArray(arrayList);
        return jsonArray.toString();
    }
    
    /**
	 * 将字符串结果转换为Json对象
	 * @param result String result
	 * @return null/JsonObject
	 */
	public static JSONObject convertString2Json(String result) {
		if(result == null)
			return null;
		
		JSONObject responseJson = null;
		try {
		    responseJson = new JSONObject(result);
		} catch (JSONException e) {
		    e.printStackTrace();
		}
		return responseJson;
	}
}
