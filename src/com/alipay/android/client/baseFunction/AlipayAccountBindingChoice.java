/*
 * Copyright 2009, Embedded General Inc.  All right reserved
 * 
 * draft:Roger
 * 
 */
package com.alipay.android.client.baseFunction;

import java.util.ArrayList;
import java.util.HashMap;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.alipay.android.appHall.bussiness.CallBackFilter;
import com.alipay.android.client.Login;
import com.alipay.android.client.MessageFilter;
import com.alipay.android.client.RootActivity;
import com.alipay.android.client.constant.AlipayHandlerMessageIDs;
import com.alipay.android.client.constant.Constant;
import com.alipay.android.client.util.AlipayInputErrorCheck;
import com.alipay.android.client.util.BaseHelper;
import com.alipay.android.client.util.Responsor;
import com.alipay.android.comon.component.ProgressDiv;
import com.alipay.android.net.RequestMaker;
import com.alipay.android.net.http.HttpRequestMaker;
import com.alipay.platform.core.Command;
import com.alipay.platform.view.ActivityMediator;
import com.alipay.platform.view.OnDataChangeListener;
import com.eg.android.AlipayGphone.R;

public class AlipayAccountBindingChoice extends RootActivity implements OnDataChangeListener{
	/**
	 * Tag for logcat
	 */
	public static final String LOG_TAG = "AlipayAccountBindingChoice";
	public static final String GET_RSAKEY_ID = "10";
	private RequestMaker mRequestMaker;
	private CallBackFilter mBackFilter;
	
	private MessageFilter mMessageFilter = new MessageFilter(this);
	private Handler mHandler = new Handler(){
        public void handleMessage(Message msg){
        	showResult(msg);
            switch(msg.what){
                case AlipayHandlerMessageIDs.ACCOUNT_BINDING_FINISH:
                	AccountBindingFinish(msg);
                    break;
            }
            super.handleMessage(msg);
        }
    };
	
	
	/**
     * Intent Variable
     */
	private static Activity mActivity = null;
	
	/**
	 * Ensure Button
	 */
	private Button mLogin=null;
	private Button mRegister=null;
	private TextView mAccount=null; 
	/**
	 * Title Name
	 */
	private TextView mTitleName=null;	
	
    /**
	 * Variable for ProgressDialog when paying
	 */
	private ProgressDiv mProgress=null;
	
	private int mErrorType = NO_ERROR;
	private static final int NO_ERROR = -3;
	private static final int ACCOUNT_BINDING_FINISH = -2;
	private static final int ACCOUNT_BINDING_START = -1;
	
	private static final int GET_RSA_KEY_START = -5;
	
	public static void startPhoneBinding(final Activity t){
		mActivity = t;
		Intent tIntent = new Intent(t, AlipayAccountBindingChoice.class);
    	t.startActivity(tIntent);
	}
	
	/** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.alipay_account_binding_choice_320_480);
        
        mRequestMaker = new HttpRequestMaker(getApplicationContext(),
				R.raw.interfaces);
		mBackFilter = new CallBackFilter(this);
		
        loadAllVariables();
        if(mActivity != null)
        	mActivity.finish();
    }
    
    private void loadAllVariables(){
 //   	mIntent = getIntent();
    	
    	mRegister = (Button) findViewById(R.id.AlipayAccountBindingRegisterButton);
    	mRegister.setOnClickListener(new OnClickListener() {
			@Override public void onClick(View arg0)
			{
				Intent tIntent = new Intent(AlipayAccountBindingChoice.this,AlipayRegister.class);
				startActivity(tIntent);
			}
		});
    	mLogin = (Button) findViewById(R.id.AlipayAccountBindingLoginButton);
    	mLogin.setOnClickListener(new OnClickListener() {
			@Override public void onClick(View arg0)
			{
//				AlipayAccountBinding.startAccountBinding(AlipayAccountBindingChoice.this);
//				ConfirmAccountBinding();
				GetRSAKey();
			}
		});		    	
		mTitleName = (TextView) findViewById(R.id.title_text);
		mTitleName.setText(R.string.TITLE_ACCOUNT_BINDING);

		mAccount = (TextView) findViewById(R.id.AlipayAccountBindingName);
		String tStr = getString(R.string.AccountBindingName);
		if(getDataHelper().mLoginAccountPre!=null)
		{
			tStr = tStr.replace("$", getDataHelper().mLoginAccountPre);
		}
		mAccount.setText(tStr);
		
    }
    private void showResult(Message msg) {
//    	Responsor responsor = (Responsor)msg.obj;
//    	JSONObject obj = responsor.obj;
    	boolean tResultOK = false;
    	boolean needDismissProcessDialog=true;
    	
    	tResultOK = mMessageFilter.process(msg);
    	
    	if ((tResultOK) && (!getDataHelper().isCanceled())){
    		
    		if(mErrorType == ACCOUNT_BINDING_START){
    			mErrorType = ACCOUNT_BINDING_FINISH;
    		}
    		
    	}
    	
    	if (needDismissProcessDialog){
    		if (mProgress != null){
        		mProgress.dismiss();
        		mProgress=null;
        	}
    	}
    }
    /*
     * 绑定账户
     */
    private void ConfirmAccountBinding()
    {
    	if (mErrorType == ACCOUNT_BINDING_START){
			return;
		}
		
		EditText tAccountEdit = (EditText) findViewById(R.id.BindingAccount);
    	String tAccount = tAccountEdit.getText().toString();
		EditText tPasswordEdit = (EditText) findViewById(R.id.BindingPassword);
    	String tPassword = tPasswordEdit.getText().toString();
    	
    	// check Account
    	int tResult = AlipayInputErrorCheck.CheckUserID(tAccount);
    	if (tResult != AlipayInputErrorCheck.NO_ERROR){
    		// check error.
    		String warningMsg;
    		if (tResult == AlipayInputErrorCheck.ERROR_INVALID_FORMAT){
    			warningMsg = getResources().getString(R.string.WarningInvalidAccountName);
    		}else if (tResult == AlipayInputErrorCheck.ERROR_NULL_INPUT){
				warningMsg = getResources().getString(R.string.NoEmptyAccountName);
    		}else{
    			warningMsg = "UNKNOWN_ERROR TYPE = "+ tResult;
    		}
    		BaseHelper.recordWarningMsg(this,warningMsg);
    		
    		getDataHelper().showDialog(this, 
    				R.drawable.infoicon, 
    				getResources().getString(R.string.WarngingString), warningMsg,
    				getResources().getString(R.string.Ensure), null,
    				null, null,
    				null, null);
    		mErrorType = NO_ERROR;
    		return;
    	}
    	
    	// check password input
		tResult = AlipayInputErrorCheck.checkLoginPassword(tPassword);
		if (tResult != AlipayInputErrorCheck.NO_ERROR)
		{
			// check error.
			String warningMsg;
			if (tResult == AlipayInputErrorCheck.ERROR_INVALID_FORMAT)
			{
				warningMsg = getResources().getString(
						R.string.WarningInvalidLoginPassword);
			}
			else if (tResult == AlipayInputErrorCheck.ERROR_NULL_INPUT)
			{
				warningMsg = getResources().getString(R.string.NoEmptyPassword);
			}
			else
			{
				warningMsg = "UNKNOWN_ERROR TYPE = " + tResult;
				// Log.i(LOG_TAG, warningMsg);
			}
			
			BaseHelper.recordWarningMsg(this,warningMsg);
			
			getDataHelper().showDialog(this, R.drawable.infoicon, getResources()
					.getString(R.string.WarngingString), warningMsg,
					getResources().getString(R.string.Ensure), null, null,
					null, null, null);
			mErrorType = NO_ERROR;
			return;
		}
    	
		mErrorType = ACCOUNT_BINDING_START;
		getDataHelper().sendConfirmAccountBinding(mHandler, AlipayHandlerMessageIDs.ACCOUNT_BINDING_FINISH, tAccount, tPassword,Constant.BindingAlipay,AlipayAccountBindingChoice.this);
		
		if(mProgress == null){
		    mProgress = getDataHelper().showProgressDialogWithCancelButton(this, null, 
					getResources().getString(R.string.PhoneBindingWaitingMessage), 
					false, true, 
					getDataHelper().cancelListener,
					getDataHelper().cancelBtnListener);
		}
	}
	 
    public boolean onKeyDown(int keyCode, KeyEvent event)
    {
    	if (keyCode == KeyEvent.KEYCODE_BACK)
    	{
    		finish();
    		//AlipayMain.startMain(AlipayAccountBinding.this);
    		//	        	BaseHelper.showDesktop(AlipayAccountBinding.this);
//    		goLogin();
    		return true;
    	}
    	return false;
    }
    
    private void AccountBindingFinish(Message msg)
    {
    	if (mErrorType == ACCOUNT_BINDING_FINISH)
    	{
    		mErrorType = NO_ERROR;
    		AccountBindingOK(msg);
    	}
    	else
    	{
    		mErrorType = NO_ERROR;
    	}
    }
    
    private void AccountBindingOK(Message msg){
    	Responsor responsor = (Responsor)msg.obj;
		String errorText = responsor.memo;
		if(errorText == null || errorText.equals("")){
			errorText = getResources().getString(R.string.AccountBindingOK);
		}
		
		getDataHelper().showDialog(this, R.drawable.ok, 
//    			getResources().getString(R.string.PhoneBindingSuccessful), getResources().getString(R.string.PhoneInputOKMsg), 
    			getResources().getString(R.string.AccountBindingOK), errorText,    			
				getResources().getString(R.string.Ensure), btnForBindingOk, 
				null, null, null, null);
    }
    
    DialogInterface.OnClickListener btnForBindingOk = new DialogInterface.OnClickListener()
    {
		@Override
		public void onClick(DialogInterface dialog, int which)
		{
			// TODO Auto-generated method stub
			try
			{
				if(mActivity != null)
					mActivity.finish();
			}
			catch(Exception e)
			{
				e.printStackTrace();
			}
//			goLogin();
			BaseHelper.showDesktop(AlipayAccountBindingChoice.this);
		}
	};

	private void GetRSAKey() {
		if (mErrorType == GET_RSA_KEY_START)
			return;

		mErrorType = GET_RSA_KEY_START;
		ActivityMediator activityMediator = new ActivityMediator(this);
		ArrayList<String> params = new ArrayList<String>();
		params.add(getSessionId());
		params.add(getConfigData().getClientId());
		activityMediator.sendCommand(GET_RSAKEY_ID, "clean", mRequestMaker,
				params);

		// myHelper.sendGetRSAKey(mHandler,
		// AlipayHandlerMessageIDs.START_PROCESS_GOT_DATA);
		if (mProgress == null) {
			mProgress = getDataHelper().showProgressDialogWithCancelButton(
					this, null,
					getResources().getString(R.string.ConfirmAccountPassword),
					false, true, getDataHelper().cancelListener,
					getDataHelper().cancelBtnListener);
		}
	}

	@Override
	public boolean preCancel(Command command) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void onCancel(Command command) {
		// TODO Auto-generated method stub

	}

	@Override
	public boolean preFail(Command command) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void onFail(Command command) {
		// TODO Auto-generated method stub
		 closeProgress();
	        if (command.getmId().equals(GET_RSAKEY_ID)) {
	            setPublicKey(null);
	            setTimeStamp(null);
	        }
	}

	@Override
	public void onComplete(Command command) {
		//
		HashMap<String, Object> hmResponse = (HashMap<String, Object>) command
				.getResponseData();

		if (!mBackFilter.processCommand(hmResponse)) {
			if (command.getmId().equals(GET_RSAKEY_ID)) {
				mErrorType = NO_ERROR;
				String string = (String) hmResponse.get(Constant.RPF_RSA_PK);
				if (string != null) {
					setPublicKey(string);

				}

				string = (String) hmResponse.get(Constant.RPF_RSA_TS);
				setTimeStamp(string);
				// send command to LogIn.
				ConfirmAccountBinding();
			}
		} else {
			mErrorType = NO_ERROR;

			setPublicKey(null);
			setTimeStamp(null);

			closeProgress();
		}
	}

	@Override
	public void setRuleId(String ruleId) {
		// TODO Auto-generated method stub

	}

	@Override
	public String getRuleId() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Context getContext() {
		// TODO Auto-generated method stub
		return AlipayAccountBindingChoice.this;
	}

	void closeProgress() {
		try {
			if (mProgress != null && mProgress.isShowing()) {
				mProgress.dismiss();
				mProgress = null;
			}
		} catch (Exception e) {
			// e.printStackTrace();
		}
	}
	
//	public void goLogin(){
//		Intent intent = new Intent(this, Login.class);
//		startActivity(intent);
//	}
}
