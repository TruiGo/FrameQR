/**
 * 
 */
package com.alipay.android.client.webapp;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

import com.alipay.android.client.MessageFilter;
import com.alipay.android.client.RootActivity;
import com.alipay.android.client.constant.Constant;
import com.alipay.android.comon.component.ProgressDiv;
import com.eg.android.AlipayGphone.R;

/**
 * @author sanping.li@alipay.com
 *
 */
public class AuthoActivity extends RootActivity implements OnClickListener {

	private String mPartnerId;
//    private String mAuthLevel;
	private boolean mAuthResult;

	private ProgressDiv mProgress;
	private MessageFilter mMessageFilter = new MessageFilter(this);
	private Handler mHandler = new Handler(){
		public void handleMessage(Message msg){
			showResult(msg);
			super.handleMessage(msg);
		}
	};
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
        setContentView(R.layout.app_autho);
        mPartnerId = getIntent().getStringExtra(Constant.RQF_PARTNER_ID);
//        mAuthLevel = getIntent().getStringExtra(Constant.RQF_AUTH_LEVEL);
        
        loadVariables();
	}

	/**
	 * 
	 */
	private void loadVariables() {
		Button button = (Button) findViewById(R.id.ok_btn);
		button.setOnClickListener(this);
		button = (Button) findViewById(R.id.cancel_btn);
		button.setOnClickListener(this);
	}

	/* (non-Javadoc)
	 * @see android.view.View.OnClickListener#onClick(android.view.View)
	 */
	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.ok_btn:
			mAuthResult = true;
			openProcessDialog(getString(R.string.app_autho_loading));
			doAuth();
			break;
			
		case R.id.cancel_btn:
			mAuthResult = false;
			openProcessDialog(getString(R.string.app_autho_loading));
			doAuth();
			break;

		default:
			break;
		}
	}
	
	private void doAuth(){
	    getDataHelper().sendAuthReq(mHandler, 0, mPartnerId, mAuthResult);
	}

	private void showResult(Message msg) {
    	boolean tResultOK = mMessageFilter.process(msg);

		if ((tResultOK) && (!getDataHelper().isCanceled())){
			if(mAuthResult)
				setResult(RESULT_OK,getIntent());
			else
				setResult(RESULT_CANCELED,getIntent());
			finish();
		}
		
		if (mProgress != null){
			mProgress.dismiss();
			mProgress=null;
		}
	}
	
	private void openProcessDialog(String msg) {
		if (mProgress == null) {
			mProgress = getDataHelper().showProgressDialogWithoutCancelButton(this, null, 
					msg, 
					false, true, 
					getDataHelper().cancelListener,
					getDataHelper().cancelBtnListener);
		}
	}
}
