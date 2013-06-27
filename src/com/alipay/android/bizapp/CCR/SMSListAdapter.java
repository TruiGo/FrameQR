package com.alipay.android.bizapp.CCR;

import java.util.ArrayList;

import com.eg.android.AlipayGphone.R;

import android.content.Context;
import android.graphics.Color;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.ForegroundColorSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

public class SMSListAdapter extends BaseAdapter{
	
	private ArrayList<String> mArrayList;
	private Context mContext;

	public SMSListAdapter(Context mContext,ArrayList<String> mArrayList){
		this.mContext = mContext;
		this.mArrayList = mArrayList;
	}

	@Override
	public int getCount() {
		return mArrayList.size();
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
	public View getView(int position, View convertView, ViewGroup parent) {
		if(convertView ==null){
			convertView = LayoutInflater.from(mContext).inflate(R.layout.sms_list_item, null);
		}
		TextView listItem = (TextView)convertView.findViewById(R.id.SMSListItemText);
		listItem.setText(mArrayList.get(position));
		checkedDigital(listItem, Color.RED);
		return convertView;
	}
	
	private void checkedDigital(TextView textView,int fontColor){
		String text = (String)textView.getText();
		SpannableString msp = new SpannableString(text);
		char currentChar ;
		for(int i=0;i<text.length();i++){
			currentChar = text.charAt(i);
			if(Character.isDigit(currentChar) == true || currentChar == '.'){
				msp.setSpan(new ForegroundColorSpan(fontColor), i, i+1, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
			}
		}
		textView.setText(msp);
	}

}
