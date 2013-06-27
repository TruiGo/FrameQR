package com.alipay.android.ui.adapter;

import java.util.ArrayList;

import com.alipay.android.client.LifePayHistoryActivity;
import com.alipay.android.client.constant.Constant;
import com.alipay.android.client.lifePayment.HistoryBillInfo;
import com.eg.android.AlipayGphone.R;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class LifePayHistoryAdapter extends BaseAdapter {

	private ArrayList<HistoryBillInfo> mHistoryBillInfos = new ArrayList<HistoryBillInfo>();
	private Context mContext;
	private String mLifePaymHistoryType;
	private ImageView mBillListDivide1 = null;
	private ImageView mBillListDivide2 = null;
	
	
	
	public LifePayHistoryAdapter(ArrayList<HistoryBillInfo> historyBillInfos,String lifePaymHistoryType ,Context context){
		this.mHistoryBillInfos = historyBillInfos;
		this.mLifePaymHistoryType = lifePaymHistoryType;
		this.mContext = context;
	}
	
	
	@Override
	public int getCount() {
		   if (mHistoryBillInfos == null)
	            return 0;
	        return mHistoryBillInfos.size();
	}

	@Override
	public Object getItem(int position) {
		return mHistoryBillInfos.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder holder;
		HistoryBillInfo historyBillInfo = mHistoryBillInfos.get(position);
		 if (convertView == null) {
	            convertView = LayoutInflater.from(mContext).inflate(
	                R.layout.lifepay_history_item, null);
		 holder  = new ViewHolder();
//		 holder.lifePayIcon =  (ImageView) convertView.findViewById(R.id.lifePayIcon);
		 holder.companyName = (TextView) convertView.findViewById(R.id.companyName);
		 holder.billKey = (TextView) convertView.findViewById(R.id.billID);
		 convertView.setTag(holder);
		 }else{
			 holder = (ViewHolder) convertView.getTag();
		 }
		 
		 holder.companyName.setText(historyBillInfo.chargeCompanyName);
		 if(historyBillInfo.ownerName != null && !historyBillInfo.ownerName.equalsIgnoreCase("")){
		     holder.billKey.setText(historyBillInfo.billKey + " | " + historyBillInfo.ownerName);
		 }else{
			 holder.billKey.setText(historyBillInfo.billKey);
		 }
		 
			mBillListDivide1 = (ImageView)convertView.findViewById(R.id.billListDivide1);
			mBillListDivide2 = (ImageView)convertView.findViewById(R.id.billListDivide2);
		 
		 if(mLifePaymHistoryType.equalsIgnoreCase(Constant.URL_WATER_RATE)){
			 //设置水费的图标
		 }else if(mLifePaymHistoryType.equalsIgnoreCase(Constant.URL_POWER_RATE)){
			 //设置电费的图标
		 }else if(mLifePaymHistoryType.equalsIgnoreCase(Constant.URL_GAS_RATE)){
			 //设置燃气费的图标
		 }else if(mLifePaymHistoryType.equalsIgnoreCase(Constant.URL_COMMUN_RATE)){
			 //设置固话宽带的图标
		 }
		return convertView;
	}
	 
	private class ViewHolder{
//		ImageView lifePayIcon;
		TextView companyName;
		TextView  billKey ;
	}

}
