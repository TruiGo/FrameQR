package com.alipay.android.ui.adapter;

import java.util.ArrayList;
import java.util.List;

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

import com.alipay.android.client.lifePayment.ChineseToPy;
import com.alipay.android.ui.bean.ContactPerson;
import com.alipay.android.util.PinYinAndHanziUtils;
import com.eg.android.AlipayGphone.R;

public class ContactListAdapter extends BaseAdapter{
	private LayoutInflater mLayoutInflater;
	private List<ContactPerson> mLocalContacts = new ArrayList<ContactPerson>();
	private ContactPerson curContacter;
	private Context mContext;
	
	public ContactListAdapter(Context context){
		mLayoutInflater = LayoutInflater.from(context);
		mContext = context;
	}

	public int getCount() {
		return mLocalContacts.size();
	}

	public ContactPerson getItem(int position) {
		return mLocalContacts.get(position);
	}

	public long getItemId(int position) {
		return position;
	}

	public View getView(int position, View convertView, ViewGroup parent) {
		final ViewHolder viewHolder;
		if(convertView == null){
			convertView = mLayoutInflater.inflate(R.layout.phone_contact, null);
			viewHolder = new ViewHolder();
			viewHolder.tagForNameText = (TextView) convertView.findViewById(R.id.tagForName);
//			viewHolder.tagDivider = (ImageView) convertView.findViewById(R.id.tagDiveder);
			
			viewHolder.dispayNameText = (TextView) convertView.findViewById(R.id.displayNameText);
			viewHolder.phoneNumberText = (TextView) convertView.findViewById(R.id.phoneNumberText);
			viewHolder.phoneNumAmountText = (TextView) convertView.findViewById(R.id.phoneNumAmount);
			convertView.setTag(viewHolder);
		}else{
			viewHolder =   (ViewHolder) convertView.getTag();
		}
		
		curContacter = mLocalContacts.get(position);
		
		if(!curContacter.isNumberMatch){
			List<String[]> matchedResult = curContacter.matchedPinYin;
			
			if(matchedResult != null && matchedResult.size() > 0){
				String displayName = curContacter.displayName;
				int matchedIndex = 0 , matchedLength = 0,firstMatchedIndex = 0;
				
				String[] displayNameArray = PinYinAndHanziUtils.splitDisplayNameWithHanzi(displayName);
				for (int i = 0; i < matchedResult.size(); i++) {
					matchedIndex = Integer.parseInt(matchedResult.get(i)[0]);
					if(i == 0){
						firstMatchedIndex = displayName.indexOf(displayNameArray[matchedIndex]);
					}
					
					if(PinYinAndHanziUtils.isHanzi(displayNameArray[matchedIndex])){
						if(matchedIndex > 0 && !PinYinAndHanziUtils.isHanzi(displayNameArray[matchedIndex -1]))
							matchedLength = displayName.indexOf(displayNameArray[matchedIndex]) - firstMatchedIndex + 1;
						else
							matchedLength ++;
					}else
						matchedLength += matchedResult.get(i)[1].length();
				}
				
				SpannableString displayWithColor = new SpannableString(displayName);
				displayWithColor.setSpan(new ForegroundColorSpan(Color.parseColor("#4C91C7")), 
						firstMatchedIndex, firstMatchedIndex + matchedLength,Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
				
				viewHolder.dispayNameText.setText(displayWithColor);
				viewHolder.tagForNameText.setVisibility(View.GONE);
//				viewHolder.tagDivider.setVisibility(View.GONE);
			}else{
				String curPinyin = ChineseToPy.getSinglePy(curContacter.displayName.substring(0,1));
				if(position == 0){
					viewHolder.tagForNameText.setText(curPinyin);
					viewHolder.tagForNameText.setVisibility(View.VISIBLE);
//					viewHolder.tagDivider.setVisibility(View.VISIBLE);
				}else{
					String prePinyin = ChineseToPy.getSinglePy(mLocalContacts.get(position -1).displayName.substring(0,1));
					
					if(!prePinyin.equalsIgnoreCase(curPinyin)){
						viewHolder.tagForNameText.setText(curPinyin);
						viewHolder.tagForNameText.setVisibility(View.VISIBLE);
//						viewHolder.tagDivider.setVisibility(View.VISIBLE);
					}else{
						viewHolder.tagForNameText.setVisibility(View.GONE);
//						viewHolder.tagDivider.setVisibility(View.GONE);
					}
				}
				viewHolder.dispayNameText.setText(curContacter.displayName);
			}
			
			viewHolder.phoneNumberText.setText(curContacter.phoneNumber.get(0));
			if(curContacter.phoneNumber.size() > 1){
				viewHolder.phoneNumAmountText.setVisibility(View.VISIBLE);
				String phoneAmountText = mContext.getText(R.string.phoneNumAmount) + "";
				viewHolder.phoneNumAmountText.setText(phoneAmountText.replace("$s", curContacter.phoneNumber.size() + ""));
			}else{
				viewHolder.phoneNumAmountText.setVisibility(View.GONE);
			}
		}else{
			if(curContacter.matchedNum != null){
				SpannableString phoneNumberWithColor = new SpannableString(curContacter.phoneNumber.get(0));
				int matchedIndex = curContacter.phoneNumber.get(0).indexOf(curContacter.matchedNum); 
				phoneNumberWithColor.setSpan(new ForegroundColorSpan(Color.parseColor("#4C91C7")), 
						matchedIndex ,matchedIndex + curContacter.matchedNum.length(),Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
				
				viewHolder.phoneNumberText.setText(phoneNumberWithColor);
				viewHolder.tagForNameText.setVisibility(View.GONE);
			}else{
				viewHolder.phoneNumberText.setText(curContacter.phoneNumber.get(0));
			}
			viewHolder.dispayNameText.setText(curContacter.displayName);
		}
		
		return convertView;
	}
	
	
	class ViewHolder{
		TextView tagForNameText;
//		ImageView tagDivider;
		TextView dispayNameText;
		TextView phoneNumberText;
		TextView phoneNumAmountText;
	}

	public void refreshUI(List<ContactPerson> contactPersons) {
		mLocalContacts.clear();
		mLocalContacts.addAll(contactPersons);
		notifyDataSetChanged();
	}
}
