package com.alipay.android.ui.fragment;

import android.app.Dialog;
import android.content.Context;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.eg.android.AlipayGphone.R;

/**
 * 由标题和ListView构成的通用对话框
 */
public class DialogWrapper {
	private Dialog customDialog;
	private Context mContext;
	
	public DialogWrapper(Context context){
		this.mContext = context;
	}
	
	/**
	 * 
	 * @param layout 布局
	 * @param title 标题
	 * @param adapter 数据源
	 * @param listener 监听器
	 */
	public void showPop(int layout,String title,BaseAdapter adapter,OnItemClickListener listener) {
		if(customDialog == null || !customDialog.isShowing()){
			customDialog = new Dialog(mContext,R.style.dialog_with_no_title_style);
			customDialog.setContentView(layout);
		}
		TextView titleView = (TextView) customDialog.findViewById(R.id.title);
		titleView.setText(title);
		
		ListView contentListView = (ListView) customDialog.findViewById(R.id.content);
		contentListView.setAdapter(adapter);
		contentListView.setOnItemClickListener(listener);
		customDialog.show();
	}
	
	public void dismissPop(){
		if(customDialog != null && customDialog.isShowing()){
			customDialog.dismiss();
			customDialog = null;
		}
	}
}
