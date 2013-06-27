package com.alipay.android.appHall.HighFrequencyApps;

import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;

import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;

import com.alipay.android.client.util.AlipayDataStore;
import com.alipay.android.util.JsonConvert;

public final class PrepareHighFrequencyApps {
	
	//格式为账户id：json（string） 
	private HashMap<String,Object> savedData;
	
	private AlipayDataStore alipayDataStore;
	
	public PrepareHighFrequencyApps(Context context){
		alipayDataStore = new AlipayDataStore(context);
		String countData = alipayDataStore.getString(AlipayDataStore.APP_FREQUENCY_COUNT);
		if (countData==null || countData == "") {
			//todo：第一次无旧数据的时候，在这里设置默认的三个应用id，计数为000
			savedData = new HashMap<String, Object>();
			savedData.put("10000003","0");
			savedData.put("10000008","0");
			savedData.put("10000006","0");
		}else {
			try {
				savedData = JsonConvert.Json2Map(new JSONObject(countData));
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
	
	//增加应用计数
	public void increaseSavedData(String id){
		Iterator iter = savedData.keySet().iterator(); 
		while (iter.hasNext()) {
		    Object key = iter.next();
		    Object value = savedData.get(key);
		    if (key.toString().equals(id)) {
				int intvalue = Integer.parseInt((String)value)+1;
				value = String.valueOf(intvalue);
				savedData.put((String)key, value);
		    	break;
			}
		} 
	}
	
	//退出客户端时候调用
	public void saveData(){
		alipayDataStore.putString(AlipayDataStore.APP_FREQUENCY_COUNT, JsonConvert.Map2Json(savedData));
	}
	
	//得到所有的app使用频率(参数：账户、数据项size、运营id,如果没有则为-1)
	//String数组，按顺序返回n个业务应用的id
	public String[] getFinalResult(int itemsize,int yunyingid){
		String[] finalResult = new String[itemsize];
		String[] tempResult = getAllAppsFrequency(itemsize);
		if (yunyingid==-1) {
			finalResult = tempResult;
			return finalResult;
		}
		finalResult[0] = String.valueOf(yunyingid);
		System.arraycopy(tempResult, 0, finalResult, 1, 2);
		return finalResult;
	}
	
	//得到所有的app使用频率(数据项size)
	private String[] getAllAppsFrequency(int itemsize){
		HashMap<String, Object> subResult = new HashMap<String, Object>();
		subResult.put("10000003","3");
		subResult.put("10000004","4");
		subResult.put("10000006","6");
		subResult.put("10000007","7");
		
		Object[] values=subResult.values().toArray();
		
		Comparator<Object> c = new Comparator<Object>(){
			   @Override
			   public int compare(Object o1, Object o2) {
			    //TODO: add argument check yourself
			    int a1 = Integer.parseInt((String)o1);
			    int a2 = Integer.parseInt((String)o2);
			    if(a1>a2)return -1;
			    if(a1==a2)return 0;
			    else return 1;
			   }
			  };
		
		
		//将本地文件当中的数据解析出来并返回(返回3个最高的)
		if (subResult == null || subResult.size()< 3) {
			return null;
		}
		Arrays.sort(values,  c);
		
		String[] finalResult = new String[itemsize];
		
		Iterator iterator = null;
		int n = 0;
		for (int i = 0; i < values.length; i++) {
			iterator = subResult.keySet().iterator();
			while(iterator.hasNext()) {
				String key = (String) iterator.next();
				String value = (String) subResult.get(key);
				if (value.equals(values[i])) {
					finalResult[n] = key;
					if (n==itemsize-1) {
						return finalResult;
					}
					n++;
				}
			}
		}
		return null;
	}
}
