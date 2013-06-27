/*
 * Copyright 2009, Embedded General Inc.  All right reserved
 * 
 * draft:Roger
 * 
 */
package com.alipay.android.client.baseFunction;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.InputType;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.alipay.android.client.MessageFilter;
import com.alipay.android.client.RootActivity;
import com.alipay.android.client.constant.AlipayHandlerMessageIDs;
import com.alipay.android.client.constant.Constant;
import com.alipay.android.client.util.AlipayInputErrorCheck;
import com.alipay.android.client.util.BaseHelper;
import com.alipay.android.client.util.LephoneConstant;
import com.alipay.android.client.util.Responsor;
import com.alipay.android.comon.component.ProgressDiv;
import com.alipay.android.log.Constants;
import com.alipay.android.util.LogUtil;
import com.eg.android.AlipayGphone.R;

public class AlipayPhoneBinding extends RootActivity implements OnClickListener{
	/**
	 * Tag for logcat
	 */
	private static final String LOG_TAG = "AlipyPhoneBinding";
	
	private MessageFilter mMessageFilter = new MessageFilter(this);
	private Handler mHandler = new Handler(){
        public void handleMessage(Message msg){
        	showResult(msg);
            switch(msg.what){
                case AlipayHandlerMessageIDs.PHONE_BINDING_APPLY:
                    phoneBindingFinish(msg);
                    break;
            }
            super.handleMessage(msg);
        }
    };
    
    /**
	 * Variable for ProgressDialog when paying
	 */
	private ProgressDiv mProgress=null;
	
	private int mErrorType = NO_ERROR;
	private static final int NO_ERROR = -3;
	private static final int PHONE_BINDING_FINISH = -2;
	private static final int PHONE_BINDING_START = -1;
	
	/**
	 * Title Name
	 */
	private TextView mTitleName=null;
	private EditText mTxtPhoneNumber=null;
	
	private Activity mActivity = null;
	private Activity mSelfActivity = null;
	
	/**
	 * Ensure Button
	 */
	private Button mEnsure=null;
	private String mLoginAccount;
	
//	public static void startPhoneBinding(final Activity t){
//		mActivity = t;
//		Intent tIntent = new Intent(t, AlipayPhoneBinding.class);
//    	t.startActivityForResult(tIntent, Constant.REQUEST_PHONE_BINDDING);
//	}
	
	/** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if(LephoneConstant.isLephone())
        {
        	getWindow().addFlags(LephoneConstant.FLAG_ROCKET_MENU_NOTIFY);
        }
        //get current device width and height.
        DisplayMetrics tDisplayMetrics = new DisplayMetrics();
        Display tDisplay = ((WindowManager)getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay();
        tDisplay.getMetrics(tDisplayMetrics);
        //Log.i(LOG_TAG, "Device Width="+tDisplayMetrics.widthPixels+"; Device Height="+tDisplayMetrics.heightPixels);

        setContentView(R.layout.alipay_phone_binding_320_480);
        mSelfActivity = this;
        mLoginAccount = getIntent().getStringExtra(Constant.RQF_LOGIN_ACCOUNT);
        
        loadAllVariables();
//        mActivity.finish();
    }
    
    private void loadAllVariables(){

        // Display Item Name.
    	mTitleName = (TextView) findViewById(R.id.title_text);
    	mTitleName.setText(R.string.AccountBindingPhone);
    	
    	mTxtPhoneNumber = (EditText) findViewById(R.id.PhoneNumberEdit);
//    	mTxtPhoneNumber.setInputType(InputType.TYPE_CLASS_NUMBER); 
    	mTxtPhoneNumber.setInputType(InputType.TYPE_CLASS_PHONE);
//    	TelephonyManager tm = (TelephonyManager) this.getSystemService(Context.TELEPHONY_SERVICE);
//        String tel = tm.getLine1Number();
//        if (tel != null && !tel.equals("")) {
//        	mTxtPhoneNumber.setText(tel);
//        }
    	
//    	mTxtPayPassword = (EditText) findViewById(R.id.PayPasswordEdit);
//    	mTxtPayPassword.setInputType(InputType.TYPE_CLASS_NUMBER);
    	
    	mEnsure = (Button) findViewById(R.id.PhoneBindingEnsureButton);
    	mEnsure.setOnClickListener(this);
    }
    
    private void showResult(Message msg) {
//    	Responsor responsor = (Responsor)msg.obj;
//    	JSONObject obj = responsor.obj;
    	boolean tResultOK = false;
    	boolean needDismissProcessDialog=true;
    	
    	tResultOK = mMessageFilter.process(msg);
    	
    	if ((tResultOK) && (!getDataHelper().isCanceled())){
    		
    		if(mErrorType == PHONE_BINDING_START){
    			mErrorType = PHONE_BINDING_FINISH;
    		}
    	}
    	
    	if (needDismissProcessDialog){
    		if (mProgress != null){
        		mProgress.dismiss();
        		mProgress=null;
        	}
    	}
    }
    
    private void phoneBindingFinish(Message msg){
//    	if(true) {
    	
    	Responsor responsor = (Responsor)msg.obj;
		String errorText = responsor.memo;
		if(errorText == null || errorText.equals("")){
			errorText = getResources().getString(R.string.PhoneBindingSuccessful);
		}

		if (mErrorType == PHONE_BINDING_FINISH){
    		LogUtil.logAnyTime(LOG_TAG, "Phone Binding Check OK");
    		
    		Intent tIntent = new Intent(AlipayPhoneBinding.this, AlipayPhoneBindingCheckcode.class);
			tIntent.putExtra(Constant.RPF_MOBILE_NO, mTxtPhoneNumber.getText().toString());
			tIntent.putExtra(Constant.RQF_LOGIN_ACCOUNT, mLoginAccount);
	    	startActivityForResult(tIntent, Constant.REQUEST_CODE);

//	    	myHelper.showErrorDialog(this, R.drawable.ok, 
//	    			getResources().getString(R.string.PhoneBindingSuccessful), errorText, 
//					getResources().getString(R.string.Ensure), btnForBindingOk, 
//					null, null, null, null);
		} else {
			LogUtil.logAnyTime(LOG_TAG, "Phone Binding Check Failed!");
			getDataHelper().showDialog(mActivity, R.drawable.infoicon, 
					getResources().getString(R.string.WarngingString), errorText, 
					getResources().getString(R.string.Ensure), null, 
					null, null, null, null);	
		}
		
		mErrorType = NO_ERROR;
    	
    }
    
    
    DialogInterface.OnClickListener btnForBindingOk = new DialogInterface.OnClickListener(){
		@Override
		public void onClick(DialogInterface dialog, int which) {
			// TODO Auto-generated method stub
//			mSelfActivity.setResult(RESULT_OK);
//			mSelfActivity.finish();
			Intent tIntent = new Intent(AlipayPhoneBinding.this, AlipayPhoneBindingCheckcode.class);
			tIntent.putExtra(Constant.RPF_MOBILE_NO, mTxtPhoneNumber.getText().toString());
			tIntent.putExtra(Constant.RQF_LOGIN_ACCOUNT, mLoginAccount);
	    	startActivityForResult(tIntent, Constant.REQUEST_CODE);
		}
	};

    
	@Override
	public void onClick(View v) {
		if (mErrorType == PHONE_BINDING_START){
			return;
		}
		
    	String tString = mTxtPhoneNumber.getText().toString();
    	// check PhoneNumber
    	int tResult = AlipayInputErrorCheck.checkPhoneNumber(tString);
    	if (tResult != AlipayInputErrorCheck.NO_ERROR){
    		// check error.
    		String warningMsg;
    		
    		if (tResult == AlipayInputErrorCheck.ERROR_INVALID_FORMAT){
    			warningMsg = getResources().getString(R.string.WarningPhoneNumberFormatError);
    		}else if (tResult == AlipayInputErrorCheck.ERROR_NULL_INPUT){
    			warningMsg = getResources().getString(R.string.PhoneNOEmpty);
    		}else{
    			warningMsg = "UNKNOWN_ERROR TYPE = "+ tResult;
    		}
    		BaseHelper.recordWarningMsg(this,warningMsg,Constants.PHONEBINDINGVIEW);
    		
    		getDataHelper().showDialog(this, 
    				R.drawable.infoicon, 
    				getResources().getString(R.string.WarngingString), warningMsg,
    				getResources().getString(R.string.Ensure), null,
    				null, null,
    				null, null);
    		mErrorType = NO_ERROR;
    		return;
    	}
    	
		mErrorType = PHONE_BINDING_START;
		getDataHelper().sendApplyBinding(mHandler, AlipayHandlerMessageIDs.PHONE_BINDING_APPLY, tString,mLoginAccount);
		
		if(mProgress == null){
		    mProgress = getDataHelper().showProgressDialogWithCancelButton(this, null, 
					getResources().getString(R.string.PhoneBindingWaitingMessage), 
					false, true, 
					getDataHelper().cancelListener,
					getDataHelper().cancelBtnListener);
		}
	}
	 
	public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
        	mSelfActivity.setResult(RESULT_CANCELED);
        	mSelfActivity.finish();
            return true;
        } else if (keyCode == KeyEvent.KEYCODE_SEARCH){
        	//捕获此事件，但不做处理
        	return true;
        }
        return false;
    }
	
	public void onActivityResult(int requestCode, int resultCode, Intent intent) {
		switch(requestCode) {
			case Constant.REQUEST_CODE:
				if (resultCode == RESULT_OK) {
					//正常返回，绑定完成
//					AlipayApplication alipayApplication = (AlipayApplication) getApplicationContext();
//					alipayApplication.putGlobalData("refresh", "1");
					if(getUserData() != null && !getUserData().equals("")){
						getUserData().setMobileNo(mTxtPhoneNumber.getText().toString());	
					}
					//绑定手机成功完成
					setResult(Activity.RESULT_OK);
					this.finish();
				} else {
					//出错或者返回，仍留在当前界面
				}
			break;
		}
	}

}
