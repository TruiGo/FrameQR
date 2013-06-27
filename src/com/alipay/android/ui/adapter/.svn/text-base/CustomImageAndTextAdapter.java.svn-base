package com.alipay.android.ui.adapter;

import java.util.ArrayList;

import android.app.Activity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

public abstract class CustomImageAndTextAdapter extends BaseAdapter{
	
	public Activity mContext = null;
	public ArrayList<Object> mArrayList = null;
	
	public CustomImageAndTextAdapter(Activity context,Object arrayList){
		mContext = context;
		this.mArrayList = (ArrayList)arrayList;
	}

	@Override
	public int getCount() {
		return mArrayList != null?mArrayList.size():0;
	}

	@Override
	public Object getItem(int position) {
		return position;
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public abstract View getView(int position, View convertView, ViewGroup parent);

}
