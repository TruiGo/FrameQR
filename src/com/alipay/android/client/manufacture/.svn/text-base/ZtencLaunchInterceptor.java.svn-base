package com.alipay.android.client.manufacture;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;

import com.alipay.android.appHall.common.CacheSet;
import com.eg.android.AlipayGphone.R;

/**
 * 中兴登录时拦截，提示流量消耗信息
 * @author wb-wuxiaofan
 *
 */
public class ZtencLaunchInterceptor implements AppLaunchInterceptor {
	private LayoutInflater inflater;
	private View dialogView;
	private CheckBox remindAgainCheckBox;
	
	private String remindAgain;
	
	public void initInterceptor(Context context){
		remindAgain = CacheSet.getInstance(context).getString(ManuConstants.NOTREMINDAGAIN);
		inflater = LayoutInflater.from(context);
		dialogView = inflater.inflate(R.layout.charge_remind, null);
		remindAgainCheckBox = (CheckBox) dialogView.findViewById(R.id.remindAgain);
	}
	
	@Override
	public void execLaunch(final Context context,final LaunchCallback launchCallback) {
		initInterceptor(context);
		
		if(!"".equals(remindAgain) && Boolean.valueOf(remindAgain)){
			launchCallback.onConfirm();
			return;
		}
		
		//process checkBox
		remindAgain(context);
		
		new AlertDialog.Builder(context).setTitle(context.getText(R.string.systemInfo))
				.setIcon(R.drawable.abouticon)
				.setView(dialogView)
				.setPositiveButton(context.getResources().getString(R.string.ZTEEnsureButton),
						new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						launchCallback.onConfirm();
					}
				})
				.setNegativeButton(context.getResources().getString(R.string.ZTEExitButton), new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						if(remindAgainCheckBox.isChecked()){
							CacheSet.getInstance(context).putString(ManuConstants.NOTREMINDAGAIN, "");
						}
						
						launchCallback.onCancel();
					}
				}).show();
	}
	
	private void remindAgain(final Context context) {
		remindAgainCheckBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(CompoundButton buttonView,boolean isChecked) {
				if (isChecked) {
					CacheSet.getInstance(context).putString(ManuConstants.NOTREMINDAGAIN, "true");
				}else{
					CacheSet.getInstance(context).putString(ManuConstants.NOTREMINDAGAIN, "");
				}
			}
		});
	}
}
