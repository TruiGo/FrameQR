package com.alipay.android.ui.fragment;

import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.ImageView;

import com.alipay.android.client.RootActivity;
import com.alipay.android.ui.adapter.AlertDialogAdapter;
import com.eg.android.AlipayGphone.R;

public class PopTeleponeOperators {
	private RootActivity mContext;
	private CustomAlertDialog contactDialog;
	AlertDialogAdapter mAlertDialogAdapter;
	int mDefaultChecked =0;
	private int currentItemPostion =mDefaultChecked;
	
	public PopTeleponeOperators(RootActivity context,AlertDialogAdapter alertDialogAdapter){
		this.mContext = context;
		contactDialog = new CustomAlertDialog(mContext);
		mAlertDialogAdapter = alertDialogAdapter;
	}
	
	public void showPop(String title,OnClickListener buttonListener){
		contactDialog.showPop(R.layout.custom_alertdialog,
				title, 
				mAlertDialogAdapter, 
				buttonListener,new AdapterView.OnItemClickListener(){
					@Override
					public void onItemClick(AdapterView<?> parent, View view,
							int position, long id) {
						if(currentItemPostion != position){
							ImageView indicator = (ImageView) contactDialog.getCustomDialog().findViewById(currentItemPostion);
							if(indicator!=null){
								indicator.setImageState(new int[]{}, true);
							}
							ImageView indicator1 = (ImageView) contactDialog.getCustomDialog().findViewById(position);
							if(indicator1 != null){
								indicator1.setImageState(new int[]{android.R.attr.state_checked}, true);
							}
							//currentItemPostion = position;
							setDefaultCheckedPosition(position);
						}
						}});
	}
	
	public void dismissPop(){
		contactDialog.dismissPop();
	}
	
	public int getCheckedItemPosition() {
		return currentItemPostion;
	}
	
	public void setDefaultCheckedPosition(int defalutCheckPostion){
		currentItemPostion = mAlertDialogAdapter.setDefaultCheckedPosition(defalutCheckPostion);
	}
}
