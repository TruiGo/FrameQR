package com.alipay.android.ui.adapter;

import java.util.ArrayList;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.alipay.android.comon.component.NumberView;
import com.alipay.android.ui.bean.BankCardInfo;
import com.alipay.android.ui.bean.BankCardListItemInfo;
import com.eg.android.AlipayGphone.R;
/**
 * 卡列表适配器
 * @author caidie.wang
 *
 */
public class CardListAdapter extends BankListAdapter{
	
	

	public CardListAdapter(Activity context, ArrayList<BankCardInfo> arrayList) {
		super(context, arrayList,BankListAdapter.BANK_ICON_PARTH);
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder viewHolder = null;
		if(convertView == null){
			convertView = LayoutInflater.from(mContext).inflate(R.layout.bank_card_list_item,null);
			viewHolder = new ViewHolder();
			viewHolder.relativeLayout = (RelativeLayout)convertView.findViewById(R.id.BankCardListItemLayout);
			viewHolder.bankCardIcon = (ImageView)convertView.findViewById(R.id.BankCardIcon);
			viewHolder.bankCardName = (ImageView)convertView.findViewById(R.id.BankCardName);
			viewHolder.bankCardNumber = (NumberView)convertView.findViewById(R.id.BankCardNumberStar);
			viewHolder.bankTypeIcon1 = (ImageView)convertView.findViewById(R.id.BankTypeIcon1);
			viewHolder.bankTypeIcon2 = (TextView)convertView.findViewById(R.id.BankTypeIcon2);
			convertView.setTag(viewHolder);
		}else{
			viewHolder =   (ViewHolder) convertView.getTag();
		}
		
		//当前一条银行卡数据
		final BankCardInfo currentBankCardInfo = (BankCardInfo)mArrayList.get(position);
		//设置银行ICON
		viewHolder.bankCardIcon.setImageBitmap(getImage(BANK_ICON_PARTH,currentBankCardInfo.getBankMark()));
		//设置银行名字Image
		viewHolder.bankCardName.setImageBitmap(getImage(BANK_NAME_PARTH,currentBankCardInfo.getBankMark()));
		//其他卡数据单独特殊处理，返回处理后适合显示数据
		BankCardListItemInfo bankCardListItemInfo = new BankCardListItemInfo().getBankCardListItemInfo(mContext, currentBankCardInfo);
		//设置卡号
		viewHolder.bankCardNumber.setText("**** **** **** "+currentBankCardInfo.getCardTailNumber());
		if(currentBankCardInfo.isOwner()){
			viewHolder.bankCardNumber.setType(NumberView.TYPE_GOLD);
			viewHolder.relativeLayout.setBackgroundResource(R.drawable.bank_card_background);
		}else{
			viewHolder.bankCardNumber.setType(NumberView.TYPE_SILVER);
			viewHolder.relativeLayout.setBackgroundResource(R.drawable.bank_card_background_silver);
		}
		
		//设置快捷显示
		if(bankCardListItemInfo.getIsShowIcon1Res()){
			viewHolder.bankTypeIcon1.setVisibility(View.VISIBLE);
			viewHolder.bankTypeIcon1.setImageResource(R.drawable.shortcut_icon);
		}else{
			viewHolder.bankTypeIcon1.setVisibility(View.GONE);
		}
		//设置卡类型显示
		if(bankCardListItemInfo.getCardTppeStr() != null){
			viewHolder.bankTypeIcon2.setText(bankCardListItemInfo.getCardTppeStr());
		}else{
			viewHolder.bankTypeIcon2.setText("   ");
		}
		return convertView;
	}
	
	public class ViewHolder{
		RelativeLayout relativeLayout = null;
		ImageView bankCardIcon = null;
		ImageView bankCardName = null;
		NumberView bankCardNumber = null;
		ImageView bankTypeIcon1 = null;
		TextView bankTypeIcon2 = null;
	}
	
}
