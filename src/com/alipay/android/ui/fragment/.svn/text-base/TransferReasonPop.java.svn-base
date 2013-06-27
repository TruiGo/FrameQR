package com.alipay.android.ui.fragment;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;

import com.alipay.android.appHall.Helper;
import com.eg.android.AlipayGphone.R;

public class TransferReasonPop {
	private Context mContext;
	private DialogWrapper customPop;
	private String[] reasonArray;
	private SharedPreferences preferences;
	
	public TransferReasonPop(Context context){
		this.mContext = context;
		reasonArray = new String[]{
				mContext.getText(R.string.transfer) + "",
				mContext.getText(R.string.goodsPay) + "",
				mContext.getText(R.string.luanch) + "",
				mContext.getText(R.string.songForWeekDay) + ""};
		
		preferences =  mContext.getSharedPreferences("transferReason", Context.MODE_PRIVATE);
		customPop = new DialogWrapper(mContext);
	}
	
	public void showPop(final EditText transformResonInput){
		String reasonStr = preferences.getString("reason", "");
		if("".equals(reasonStr)){
			Editor editor = preferences.edit();
			editor.putString("reason", reasonArray[0] + "$#$" + reasonArray[1] + "$#$" + reasonArray[2] + "$#$" + reasonArray[3]);
			editor.commit();
		}else{
			reasonArray = reasonStr.split("\\$#\\$");
		}
		
		customPop.showPop(R.layout.custom_listview_dialog,//对话框布局
						mContext.getText(R.string.transferReason) + "",//对话框标题
						new ArrayAdapter<String>(mContext,R.layout.transfer_simple_list_item,reasonArray),//对话框数据
						new AdapterView.OnItemClickListener() {//对话框监听器
							@Override
							public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
								transformResonInput.setText(reasonArray[position]);
								customPop.dismissPop();
								transformResonInput.requestFocus();
								Helper.moveCursorToLast(transformResonInput.getText());
							}
						});
	}
}
