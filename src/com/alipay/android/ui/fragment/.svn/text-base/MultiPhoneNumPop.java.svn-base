package com.alipay.android.ui.fragment;

import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;

import com.alipay.android.client.RootActivity;
import com.alipay.android.ui.bean.ContactPerson;
import com.eg.android.AlipayGphone.R;

public class MultiPhoneNumPop {
	private RootActivity mContext;
	private DialogWrapper contactDialog;
	
	public MultiPhoneNumPop(RootActivity context){
		this.mContext = context;
		contactDialog = new DialogWrapper(mContext);
	}
	
	public void showPop(ContactPerson contactPerson,OnItemClickListener listener){
		contactDialog.showPop(R.layout.custom_listview_dialog,
				contactPerson.displayName, 
				new ArrayAdapter<String>(mContext, R.layout.transfer_simple_list_item,contactPerson.phoneNumber), 
				listener);
	}
	
	public void dismissPop(){
		contactDialog.dismissPop();
	}
}
