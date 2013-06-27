package com.alipay.android.ui.adapter;

import java.util.ArrayList;

import com.alipay.android.ui.quickpay.C2BConfirmViewController.Billinfo;
import com.eg.android.AlipayGphone.R;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

public class QuickPayBillListAdapter extends BaseAdapter {
	private ArrayList<Billinfo> mBillInfos = null;
	private Context mContext = null;
	
	public QuickPayBillListAdapter(Context context,ArrayList<Billinfo> billInfosDatas){
		this.mContext = context;
		this.mBillInfos  = billInfosDatas;
	}
	@Override
	public int getCount() {
		if(mBillInfos == null)
			return 0;
		return mBillInfos.size();
	}

	@Override
	public Object getItem(int position) {
		return mBillInfos == null ? null : mBillInfos.get(position);
	}

	@Override
	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder holder;
		Billinfo billInfo = mBillInfos.get(position);
		 if (convertView == null) {
	            convertView = LayoutInflater.from(mContext).inflate(
	                R.layout.quickpay_billlist_item, null);
		 holder  = new ViewHolder();
		 holder.count =  (TextView) convertView.findViewById(R.id.quickpayGoodscount);
		 holder.goodsName = (TextView) convertView.findViewById(R.id.quickpayGoodsName);
		 holder.price = (TextView) convertView.findViewById(R.id.quickpayBiiprice);
		 convertView.setTag(holder);
		 }else{
			 holder = (ViewHolder) convertView.getTag();
		 }
		 
		 holder.count.setText(billInfo.count);
		 holder.goodsName.setText(billInfo.goodsName);
		 holder.price.setText(billInfo.price);

		 
		return convertView;
	}
	
	private class ViewHolder{
		TextView count;
		TextView goodsName;
		TextView  price ;
	}
}
