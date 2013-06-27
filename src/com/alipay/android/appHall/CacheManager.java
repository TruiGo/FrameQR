package com.alipay.android.appHall;

import java.util.HashMap;

import android.content.Context;

import com.alipay.android.appHall.common.CacheSet;

public class CacheManager {
	private static CacheManager sRef;
	// cache.
	final HashMap<String, Object> mhmCache = new HashMap<String, Object>();

	Context context;

	private CacheManager(Context context) {
		this.context = context;
	}

	public static synchronized CacheManager getInstance(Context context) {
		if (sRef == null) {
			sRef = new CacheManager(context);
		}
		return sRef;
	}

	public void put(String key, String value) {
		this.mhmCache.put(key, value);
	}
	
	public String get(String key) {
		String value = (String)this.mhmCache.get(key);
		if (value == null)
			value = this.getFromLocal(key);
		return value;
	}
	

	public void put2Local(String key) {
		String value = (String)this.mhmCache.get(key);
		put2Local(key, value);
	}

	public void put2Local(String key, String value) {
		CacheSet cacheSet = CacheSet.getInstance(this.context);
		cacheSet.putString(key, value);
	}

	private String getFromLocal(String key) {
		CacheSet cacheSet = CacheSet.getInstance(this.context);
		return cacheSet.getString(key);
	}

	public String remove(String key) {
		String temp = (String)this.mhmCache.remove(key);

		CacheSet cacheSet = CacheSet.getInstance(this.context);
		cacheSet.remove(key);
		return temp;
	}

	public void putObject(String key, Object value) {
		this.mhmCache.put(key, value);
	}
	
	public Object getObject(String key) {
		return this.mhmCache.get(key);
	}

	public void destroy() {
		mhmCache.clear();
		sRef = null; 
	}
}
