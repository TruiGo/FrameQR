package com.alipay.android.bizapp.CCR;

import java.util.ArrayList;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.alipay.android.ui.adapter.BankListAdapter;
import com.eg.android.AlipayGphone.R;

public class BankNameAdapter extends BankListAdapter {
	
	public BankNameAdapter(Activity context, ArrayList<CCRBankCardInfo> bankList){
		super(context, bankList);
	}
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ImageView bankIcon;
		TextView bankName;
		TextView userName;
		if (convertView == null) {
            convertView = LayoutInflater.from(mContext)
                    .inflate(R.layout.ccr_bankinfo_item_adapter, null);
        } 
		bankIcon = (ImageView)convertView.findViewById(R.id.BankIcon);
		bankName = (TextView)convertView.findViewById(R.id.BankNameItem);
		userName = (TextView)convertView.findViewById(R.id.UserNameItem);
		bankIcon.setImageBitmap(getImage(BankListAdapter.BANK_ICON_PARTH,((CCRBankCardInfo)mArrayList.get(position)).getBankCardInfo().getBankMark()));
		bankName.setText(((CCRBankCardInfo)mArrayList.get(position)).getBankCardInfo().getBankName());
		
		String bankCardTailNumber = ((CCRBankCardInfo)mArrayList.get(position)).getCreditCardTailNumber();
		String handlerName = ((CCRBankCardInfo)mArrayList.get(position)).getBankCardInfo().getUserName();
		if(handlerName != null && !handlerName.equals("")){
			handlerName = bankCardTailNumber+String.format(mContext.getResources().getString(R.string.Bracket),handlerName);
		}
		userName.setText(handlerName);
		
		return convertView;
	}

}
