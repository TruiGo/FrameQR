package com.alipay.android.nfd;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.alipay.android.ui.bean.TransferReceiver;
import com.eg.android.AlipayGphone.R;

public class NFDReceiverAdapter extends BaseAdapter{
	private List<TransferReceiver> nfdReceivers = new ArrayList<TransferReceiver>();
	private Context mContext;
	
	public NFDReceiverAdapter(Context context) {
		this.mContext = context;
	}
	
	@Override
	public int getCount() {
		return nfdReceivers.size();
	}

	@Override
	public Object getItem(int position) {
		return nfdReceivers.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ReceiverHodler hodler;
		if (convertView == null){
			hodler = new ReceiverHodler();
			convertView = LayoutInflater.from(mContext).inflate(R.layout.nfd_receiver_item, null);
			
			hodler.nameView = (TextView) convertView.findViewById(R.id.receiverUserName);
			hodler.accountView = (TextView) convertView.findViewById(R.id.receiverUserAccount);
			convertView.setTag(hodler);
		}else{
			hodler = (ReceiverHodler) convertView.getTag();
		}
		
		TransferReceiver nfdReceiver = (TransferReceiver) getItem(position);
		
		/**
		 * 名字的第一个汉子隐藏如果账号是邮箱：前三个字母显示，中间用三个星代替后面跟上@xxx如果账号是手机，
		 * 前三个字母显示，中间用四个星代替后面跟上手机后四位
		 */
		String nameText = "";
		if(nfdReceiver.recvRealName.length() >= 1)
			nameText = nfdReceiver.recvRealName.substring(1);
		hodler.nameView.setText("*" + nameText);
		
		String userAccount = nfdReceiver.recvAccount;
		
		if(userAccount.matches(".*@.*")){
			userAccount = userAccount.substring(0,3) + "***"+ userAccount.substring(userAccount.indexOf("@"));
		}else if(userAccount.matches("1\\d{10}")){
			userAccount = userAccount.substring(0,3) + "****"+ userAccount.substring(7);
		}
		hodler.accountView.setText(userAccount);
		return convertView;
	}
	
	public void resetData(){
		nfdReceivers.clear();
		notifyDataSetChanged();
	}
	
	public synchronized void setDataAndRefresh(List<TransferReceiver> receivers){
		nfdReceivers.addAll(receivers);
		notifyDataSetChanged();
	}
	
	public synchronized void setDataAndRefresh(TransferReceiver receiver){
		nfdReceivers.add(receiver);
		notifyDataSetChanged();
	}
	
	class ReceiverHodler{
		TextView nameView;
		TextView accountView;
	}
}
