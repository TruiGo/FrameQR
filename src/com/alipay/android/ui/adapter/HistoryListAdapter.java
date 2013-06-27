package com.alipay.android.ui.adapter;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.alipay.android.client.constant.Constant;
import com.alipay.android.ui.bean.TransferReceiver;
import com.alipay.android.util.HideUtils;
import com.eg.android.AlipayGphone.R;

public class HistoryListAdapter extends BaseAdapter{
	private LayoutInflater mLayoutInflater;
	private List<TransferReceiver> receiverList = new ArrayList<TransferReceiver>(); 
	private TransferReceiver receiver;
	
	public HistoryListAdapter(Context context) {
		mLayoutInflater = LayoutInflater.from(context);
	}

	@Override
	public int getCount() {
		return receiverList.size();
	}

	@Override
	public TransferReceiver getItem(int position) {
		return receiverList.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder viewHolder;
		if(convertView == null){
			convertView = mLayoutInflater.inflate(R.layout.phone_contact, null);
			viewHolder = new ViewHolder();
			viewHolder.dispayNameText = (TextView) convertView.findViewById(R.id.displayNameText);
			viewHolder.recvAccountText = (TextView) convertView.findViewById(R.id.phoneNumberText);
			convertView.setTag(viewHolder);
		}else{
			viewHolder = (ViewHolder) convertView.getTag();
		}
		
		receiver = receiverList.get(position);
//		viewHolder.dispayNameText.setText(receiver.recvRealName);
		viewHolder.dispayNameText.setText(HideUtils.hide(receiver.recvRealName));
		if(Constant.TRADE.equals(receiver.bizType)){
//			viewHolder.recvAccountText.setText(receiver.recvAccount);
			viewHolder.recvAccountText.setText(HideUtils.hide(receiver.recvAccount));
		}else if(Constant.TRANSFER.equals(receiver.bizType)){
//			viewHolder.recvAccountText.setText(receiver.recvMobile);
			viewHolder.recvAccountText.setText(HideUtils.hide(receiver.recvMobile));
		}
		return convertView;
	}

	public void setDataForRefresh(List<TransferReceiver> responseRecevers) {
		receiverList.clear();
		if(responseRecevers != null)
			receiverList.addAll(responseRecevers);
		notifyDataSetChanged();
	}
}

class ViewHolder{
	TextView dispayNameText;
	TextView recvAccountText;
}