package com.alipay.android.ui.adapter;

import java.util.ArrayList;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.alipay.android.ui.bean.WithdrawBankInfo;
import com.eg.android.AlipayGphone.R;
/**
 * 提现银行列表Adapter
 * @author caidie.wang
 *
 */
public class WithdrawBankListAdapter extends BankListAdapter{

	public WithdrawBankListAdapter(Activity context,
			ArrayList<WithdrawBankInfo> arrayList) {
		super(context, arrayList);
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		if (convertView == null) {
            convertView = LayoutInflater.from(mContext)
                    .inflate(R.layout.image_and_text_listitem, null);
        }
		ImageView icon = null;
		TextView tip = null;
		icon = (ImageView)convertView.findViewById(R.id.Icon);
		tip = (TextView)convertView.findViewById(R.id.Tip);
		icon.setImageBitmap(getImage(BANK_ICON_PARTH,((WithdrawBankInfo)mArrayList.get(position)).getBankMark()));
		tip.setText(((WithdrawBankInfo)mArrayList.get(position)).getBankName());
		return convertView;
	}

}
