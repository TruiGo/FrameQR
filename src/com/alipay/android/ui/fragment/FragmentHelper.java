package com.alipay.android.ui.fragment;

import android.content.Context;
import android.graphics.Rect;
import android.view.Gravity;
import android.view.View;
import android.widget.PopupWindow;

import com.alipay.android.client.AlipayApplication;
import com.alipay.android.client.util.AlipayDataStore;
import com.alipay.android.ui.framework.RootController;

public class FragmentHelper {
	private AlipayDataStore mAlipayDataStore;
	private AlipayApplication application;

	public FragmentHelper(Context context) {
		mAlipayDataStore = new AlipayDataStore(context);
		application = (AlipayApplication) context.getApplicationContext();
	}

	public boolean needShow(String key) {
		return mAlipayDataStore.getString(key, "0.0.0").compareTo(application.getConfigData().getProductVersion()) < 0;
	}
	
	public void hideGuide(String key) {
		mAlipayDataStore.putString(key, application.getConfigData().getProductVersion());
	}
	
	public void showGuidPop(final RootController activity,final PopupWindow guidePopWindow,
			final View innerView,final String key,boolean fullScreen,boolean autoDismiss){
		Rect frame = new Rect();
		activity.getWindow().getDecorView().getWindowVisibleDisplayFrame(frame);
		guidePopWindow.setOutsideTouchable(true);
		guidePopWindow.setFocusable(true);
		if(fullScreen)
			guidePopWindow.setHeight(frame.height());
		
		guidePopWindow.showAtLocation(activity.getWindow().getDecorView(), Gravity.RIGHT|Gravity.TOP, 0, frame.top);
		
		if(autoDismiss){
			Runnable r = new Runnable() {
				@Override
				public void run() {
					if (guidePopWindow != null && guidePopWindow.isShowing()) {
						guidePopWindow.dismiss();
					}
				}
			};
			innerView.postDelayed(r, 1000 * 8);
			
			guidePopWindow.setOnDismissListener(new PopupWindow.OnDismissListener() {
				@Override
				public void onDismiss() {
					hideGuide(key);
				}
			});
		}
	}
}
