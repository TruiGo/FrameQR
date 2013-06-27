package com.alipay.android.comon.component;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.eg.android.AlipayGphone.R;

public class PatterAlertDialog {
	private AlertDialog mAlertDialog = null;
	private int mDuration = 3000;
	private Context mContext;

	public PatterAlertDialog(Context context) {
		mContext = context;
	}
	
	private Handler mHandler = new Handler();
	private Runnable mRunnable = new Runnable() {
		
		@Override
		public void run() {
			if (null != mAlertDialog)
			mAlertDialog.dismiss();
		}
	};
	
	public AlertDialog show(int textId, OnDismissListener l) {
		return show(mContext.getString(textId), -1, l);
	}
	
	public AlertDialog show(int textId, int iconId, OnDismissListener l) {
		return show(mContext.getString(textId), iconId, l);
	}
	
	public AlertDialog show(String text, OnDismissListener l) {
		return show(text, -1, l);
	}
	
	public AlertDialog show(String textAlert, int iconId, final OnDismissListener l) {

		AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
		//builder.setView(layout);
		mAlertDialog = builder.create();
		mAlertDialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
			
			@Override
			public void onDismiss(DialogInterface dialog) {
				mHandler.removeCallbacks(mRunnable);
				if (null != l) {
					l.onDismiss();
				}
			}
		});
		mAlertDialog.setCancelable(false);
		mAlertDialog.setCanceledOnTouchOutside(false);
		
		LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		View layout = mAlertDialog.getLayoutInflater().inflate(R.layout.patternalert, null);
		layout.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				//mAlertDialog.dismiss();
			}
		});

		TextView text = (TextView) layout.findViewById(R.id.alertText);
		text.setText(textAlert);
		
		if (-1 != iconId) {
			ImageView image = (ImageView) layout.findViewById(R.id.icon);
			image.setImageResource(iconId);
			image.setVisibility(View.VISIBLE);
		}
		mAlertDialog.show();
		mAlertDialog.setContentView(layout);

		mHandler.postDelayed(mRunnable, mDuration);
		
		return mAlertDialog;
	}
	
    public interface OnDismissListener {
        /**
         * This method will be invoked when the dialog is dismissed.
         * 
         * @param dialog The dialog that was dismissed will be passed into the
         *            method.
         */
        public void onDismiss();
    }

}
