package com.alipay.android.ui.fragment;

import android.app.Dialog;
import android.content.Context;
import android.view.View.OnClickListener;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import com.alipay.android.ui.adapter.AlertDialogAdapter;
import com.eg.android.AlipayGphone.R;

public class CustomAlertDialog {
	private Dialog customDialog;
	
	private Context mContext;
	
	public CustomAlertDialog(Context context){
		this.mContext = context;
	}
	
	/**
	 * 
	 * @param layout 布局
	 * @param title 标题
	 * @param adapter 数据源
	 * @param listener 监听器
	 */
	public void showPop(int layout,String title,AlertDialogAdapter adapter,OnClickListener buttonListener,OnItemClickListener listener) {
		if(customDialog == null || !customDialog.isShowing()){
			customDialog = new Dialog(mContext,R.style.dialog_with_no_title_style);
			customDialog.setContentView(layout);
		}
		TextView titleView = (TextView) customDialog.findViewById(R.id.title);
		titleView.setText(title);
		
		ListView contentListView = (ListView) customDialog.findViewById(R.id.content);
		contentListView.setAdapter(adapter);
		contentListView.setOnItemClickListener(listener);
		
		Button button = (Button)customDialog.findViewById(R.id.next);
		button.setOnClickListener(buttonListener);
		customDialog.show();
	}
	
	public void dismissPop(){
		if(customDialog != null && customDialog.isShowing()){
			customDialog.dismiss();
			customDialog = null;
		}
	}
	
	public Dialog getCustomDialog() {
		return customDialog;
	} 
}
