package com.alipay.android.ui.fragment;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.FrameLayout.LayoutParams;
import android.widget.TextView;

import com.alipay.android.client.RootActivity;
import com.alipay.android.log.AlipayLogAgent;
import com.alipay.android.log.Constants;
import com.alipay.android.log.StorageStateInfo;
import com.eg.android.AlipayGphone.R;

public class ContactOtherPop implements OnClickListener{
	private AlertDialog contactOtherDialog; 
	private PopCancelCallback popCallback;
	private RootActivity mContext;
	View contentView;
	private String phoneNum;
	
	public ContactOtherPop(RootActivity context,PopCancelCallback callback,String phoneNum) {
		this.popCallback = callback;
		this.mContext = context;
		this.phoneNum = phoneNum;
	}
	
	public void showSuccessPop() {
		AlertDialog.Builder builder = new AlertDialog.Builder(mContext);//.setView(contentView);
		contactOtherDialog = builder.create();
		contactOtherDialog.setCanceledOnTouchOutside(false);
		contactOtherDialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
			@Override
			public void onCancel(DialogInterface dialog) {
				popCallback.onCancel();//backToHome();
			}
		});
		contentView = contactOtherDialog.getLayoutInflater().inflate(R.layout.contact_other_dialog,null); //LayoutInflater.from(mContext).inflate(R.layout.contact_other_dialog,null);
		TextView callButton = (TextView) contentView.findViewById(R.id.call_other);
		String phoneNumText = mContext.getText(R.string.call_other) +"";
		callButton.setText(phoneNumText.replace("$s", phoneNum));
		
		Button closeButton = (Button) contentView.findViewById(R.id.btn_close);
		callButton.setOnClickListener(this);
		closeButton.setOnClickListener(this);
		showDialog();
	}

	private void showDialog() {
		if (!contactOtherDialog.isShowing()) {
			contactOtherDialog.show();
			FrameLayout.LayoutParams layoutParams = new FrameLayout.LayoutParams(Integer.parseInt(mContext.getConfigData().getScreenWidth()) - 10,LayoutParams.WRAP_CONTENT);
			contactOtherDialog.setContentView(contentView, layoutParams);
		}
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.btn_close:
			closeCallPop();
			popCallback.onCancel();//backToHome();
			break;
		case R.id.call_other:
			if(phoneNum != null && !"".equals(phoneNum)){
		        Intent intent = new Intent(Intent.ACTION_DIAL,Uri.parse("tel:" + phoneNum));  
		        mContext.startActivity(intent);  
			}
			
			/*StorageStateInfo storageStateInfo = StorageStateInfo.getInstance();
			AlipayLogAgent.onEvent(mContext, 
					Constants.MONITORPOINT_EVENT, 
					"Y", 
					"", 
					storageStateInfo.getValue(Constants.STORAGE_APPID),
					storageStateInfo.getValue(Constants.STORAGE_APPVERSION),
					storageStateInfo.getValue(Constants.STORAGE_PRODUCTID), 
					mContext.getUserId(), 
					storageStateInfo.getValue(Constants.STORAGE_PRODUCTVERSION), 
					storageStateInfo.getValue(Constants.STORAGE_CLIENTID),
					Constants.EVENTTYPE_TRANSFERCALL);*/
			break;	
		default:
			break;
		}
	}
	
	private void closeCallPop() {
		if(contactOtherDialog != null && contactOtherDialog.isShowing()){
			contactOtherDialog.dismiss();
			contactOtherDialog = null;
		}
	}
}
