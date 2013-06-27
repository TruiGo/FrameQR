package com.alipay.android.client.baseFunction;

import java.util.Timer;
import java.util.TimerTask;

import android.app.Activity;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.InputType;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.alipay.android.client.RootActivity;
import com.alipay.android.client.constant.AlipayHandlerMessageIDs;
import com.alipay.android.client.constant.Constant;
import com.alipay.android.client.util.AlipayInputErrorCheck;
import com.alipay.android.client.util.BaseHelper;
import com.alipay.android.client.util.Responsor;
import com.alipay.android.comon.component.EditTextWithButton;
import com.alipay.android.comon.component.ProgressDiv;
import com.alipay.android.log.Constants;
import com.alipay.android.util.LogUtil;
import com.eg.android.AlipayGphone.R;

public class AlipayPhoneBindingCheckcode extends RootActivity implements OnClickListener{
	
	private static final String LOG_TAG = "AlipayPhoneBindingCheckcode";
	
	private TextView mTitleTextView = null;
	private EditText mTxtCheckCode = null;
	private EditText mTxtPaypwd = null;
	protected Button mGetSmsCheckCodeButton ; 
	protected Button mBindingSubmitButton ;
	
	private String mBindMobileNo = null;
	
	private Activity mActivity = null;
	
	private int mTimerCount = 60;
	private static final int TIMER_MESSAGE = 1000;
    private Timer mTimer = null;
	
	private ProgressDiv mProgress = null;
	
	private Handler mHandler = new Handler(){
		@Override
		public void handleMessage(Message msg) {
			// TODO Auto-generated method stub
			super.handleMessage(msg);
			
			Responsor responsor = (Responsor)msg.obj;
			String errorText = "";
			if(msg.what != TIMER_MESSAGE) {
				errorText = responsor.memo;
			}
			
			switch(msg.what){
				case TIMER_MESSAGE:     
	                if(mGetSmsCheckCodeButton != null)
	                {
	                    mGetSmsCheckCodeButton.setText("(" +mTimerCount-- +")秒后" +getString(R.string.GetPassTipViewTwoInfo2));
	                    mGetSmsCheckCodeButton.setTextColor(getResources().getColor(R.color.TextColorGray));
	                    if(mTimerCount < 0)
	                    {
	                        throw new IllegalStateException("number should not less than 0");
	                    }
	                    if(mTimerCount == 0)
	                    {
	                        mGetSmsCheckCodeButton.setEnabled(true);
	                        mGetSmsCheckCodeButton.setText(getString(R.string.GetPassTipViewTwoInfo2));
	                        mGetSmsCheckCodeButton.setTextColor(getResources().getColor(R.color.ButtonColorYellow));
	                        stopTimer();
	                    }   
	                }
                break; 
                
				case AlipayHandlerMessageIDs.PHONE_BINDING_CONFIRM:
					closeProgress();
					
					if(errorText == null || errorText.equals("")){
						errorText = getResources().getString(R.string.PhoneBindingSuccessful);
					}
					
					DialogInterface.OnClickListener btnForResult = null;
					if(responsor.status == 100) {
						btnForResult = btnForBindingOk;
						//更新UserData中绑定号码的字段
						getUserData().setMobileNo(mBindMobileNo);
					}
					getDataHelper().showDialog(mActivity, R.drawable.ok, 
			    			getResources().getString(R.string.infotitle), errorText, 
							getResources().getString(R.string.Ensure), btnForResult, 
							null, null, null, null);
                    break;
                
				case AlipayHandlerMessageIDs.PHONE_BINDING_APPLY:
					closeProgress();
					
					if(errorText == null || errorText.equals("")){
						errorText = getResources().getString(R.string.PhoneBindingSuccessful);
					}
					if(responsor.status == 100) {
						//不做处理，当前也显示
						
						//重新开始计时
						startTimer();
						mGetSmsCheckCodeButton.setEnabled(false);
					} else {
	                    if (responsor.status == 0) {
	                        errorText = getResources().getString(R.string.CheckNetwork);
	                    }else if (responsor.status == 1) {
	                        errorText = getResources().getString(R.string.WarningDataCheck);
	                    }
						//出错，返回上一页
					    getDataHelper().showDialog(mActivity, R.drawable.infoicon, 
				    			getResources().getString(R.string.WarngingString), errorText, 
								getResources().getString(R.string.Ensure), btnForBindingError, 
								null, null, null, null);
					}
					
                    break;
			}
		}
		
	};
	
	DialogInterface.OnClickListener btnForBindingOk = new DialogInterface.OnClickListener(){
		@Override
		public void onClick(DialogInterface dialog, int which) {
			// TODO Auto-generated method stub
			mActivity.setResult(RESULT_OK);
			mActivity.finish();
		}
	};
	
	DialogInterface.OnClickListener btnForBindingError = new DialogInterface.OnClickListener(){
		@Override
		public void onClick(DialogInterface dialog, int which) {
			// TODO Auto-generated method stub
			mActivity.setResult(RESULT_FIRST_USER);
			mActivity.finish();
		}
	};

	private String mLoginAccount;
	
	protected void stopTimer()
    {
        if(mTimer != null)
        {
        	mTimer.cancel();
        	mTimer = null;
        }
    }

    protected void startTimer()
    {
    	mTimerCount = 60;
    	if(null != mTimer) {
	  		stopTimer();
		}
        mTimer = new Timer();
        mTimer.schedule(
            new TimerTask()
            {     
                public void run() 
                {   
                    Message message = new Message();       
                    message.what = TIMER_MESSAGE;       
                    mHandler.sendMessage(message);     
                }  
            }, 0,1000);
    }
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.alipay_phonebinding_checkcode);
		
		mActivity = this;
		mBindMobileNo = getIntent().getStringExtra(Constant.RPF_MOBILE_NO);
		LogUtil.logOnlyDebuggable(LOG_TAG, "onCreate mBindMobileNo="+mBindMobileNo);
		
		initLayoutElement();
		mLoginAccount = getIntent().getStringExtra(Constant.RQF_LOGIN_ACCOUNT);
		
		mGetSmsCheckCodeButton.setEnabled(false);
        startTimer();
	}
	
	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		
	}
	
	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		stopTimer();
		
		super.onDestroy();
	}
	
	private void initLayoutElement(){
		mTitleTextView = (TextView)findViewById(R.id.title_text);
		mTitleTextView.setText(R.string.BindingPhoneCheckCode);
		
		mTxtCheckCode = ((EditTextWithButton)findViewById(R.id.InputCheckCodeEditText)).getEtContent();
		mTxtPaypwd = ((EditTextWithButton)findViewById(R.id.PayPasswordEdit)).getEtContent();
//		mTxtPaypwd.setTransformationMethod(PasswordTransformationMethod.getInstance());
		mTxtPaypwd.setInputType(InputType.TYPE_CLASS_TEXT
                | InputType.TYPE_TEXT_VARIATION_PASSWORD);
        if (mBindMobileNo != null && !mBindMobileNo.equals("")) {
            TextView hintForInputSmsCheckCode = (TextView) findViewById(R.id.HintForInputSmsCheckCode);
            hintForInputSmsCheckCode.setHint(getHintContent(mBindMobileNo));
        }
		
		mGetSmsCheckCodeButton = (Button)findViewById(R.id.GetSmsCheckCodeButton); 
        mGetSmsCheckCodeButton.setOnClickListener(new OnClickListener() 
        {
            @Override public void onClick(View arg0)
            {
                doGetSmsCheckCode();
            }
        });

        mBindingSubmitButton = (Button)findViewById(R.id.BindingSubmitButton);
        mBindingSubmitButton.setOnClickListener(new OnClickListener() 
        {
            @Override 
            public void onClick(View arg0)
            {
                doSubmitBinding();
            }
        });
	}
	
	protected String getHintContent(String phone)
    {
        String tNumber = phone;
        int tResult = AlipayInputErrorCheck.CheckUserID(phone);
        if (tResult != AlipayInputErrorCheck.NO_ERROR)
        {
            // check error.
            String warningMsg;
            if (tResult == AlipayInputErrorCheck.ERROR_INVALID_FORMAT)
            {
                warningMsg = getResources().getString(R.string.WarningInvalidAccountName);
            }
            else if (tResult == AlipayInputErrorCheck.ERROR_NULL_INPUT)
            {
                warningMsg = getResources().getString(R.string.NotBindPhoneNo);
            }
            else
            {
                warningMsg = "UNKNOWN_ERROR TYPE = " + tResult;
            }
            
            BaseHelper.recordWarningMsg(this,warningMsg,Constants.BINDINGCHECKCODEVIEW);
            
            getDataHelper().showDialog(this, R.drawable.infoicon, getResources()
                    .getString(R.string.WarngingString), warningMsg,
                    getResources().getString(R.string.Ensure), new DialogInterface.OnClickListener()
                    {
                        @Override
                        public void onClick(DialogInterface dialog, int which)
                        {
                            finish();
                        }
                        
                    }, null, null,
                    null, null);
            return getString(R.string.NotBindPhoneNo);
        }
        return getString(R.string.InputPhone)+tNumber.substring(0,3)+"****"+tNumber.substring(7)+getString(R.string.ReceiveSMScode);
    }
	
	protected void doGetSmsCheckCode()
    {
		//停止之前还在继续的timer
		stopTimer();
		
		//向服务端请求欲绑定手机的验证码
		getDataHelper().sendApplyBinding(mHandler, AlipayHandlerMessageIDs.PHONE_BINDING_APPLY, mBindMobileNo,mLoginAccount);
	
		openProcessDialog(getString(R.string.BindingPhoneGetCheckCode));
    }
	
	protected void doSubmitBinding()
    {
		String strCheckCode = mTxtCheckCode.getText().toString();
    	int tResult = AlipayInputErrorCheck.CheckBindingcode(strCheckCode);
    	if (tResult != AlipayInputErrorCheck.NO_ERROR){
    		// check error.
    		String warningMsg;
    		
    		if (tResult == AlipayInputErrorCheck.ERROR_OUTOF_RANGE){
    			warningMsg = getResources().getString(R.string.WarningCheckCodeOutRange);
    		}else if (tResult == AlipayInputErrorCheck.ERROR_NULL_INPUT){
    			warningMsg = getResources().getString(R.string.WarningCheckCodeEmpty);
    		}else{
    			warningMsg = getResources().getString(R.string.WarningCheckCodeFormatError);
    		}
    		
    		BaseHelper.recordWarningMsg(this,warningMsg,Constants.BINDINGCHECKCODEVIEW);
    		
    		getDataHelper().showDialog(this, 
    				R.drawable.infoicon, 
    				getResources().getString(R.string.WarngingString), warningMsg,
    				getResources().getString(R.string.Ensure), null,
    				null, null,
    				null, null);
    		return;
    	}
    	
    	String strPaypwd = mTxtPaypwd.getText().toString();
    	tResult = AlipayInputErrorCheck.checkPayingPassword(strPaypwd);
    	if (tResult != AlipayInputErrorCheck.NO_ERROR){
    		// check error.
    		String warningMsg;
    		
    		if (tResult == AlipayInputErrorCheck.ERROR_NULL_INPUT){
    			warningMsg = getResources().getString(R.string.WarningPayPwdEmpty);
    		}else{
    			warningMsg = getResources().getString(R.string.WarningPayPwdFormatError);
    		}
    		
    		BaseHelper.recordWarningMsg(this,warningMsg,Constants.BINDINGCHECKCODEVIEW);
    		
    		getDataHelper().showDialog(this, 
    				R.drawable.infoicon, 
    				getResources().getString(R.string.WarngingString), warningMsg,
    				getResources().getString(R.string.Ensure), null,
    				null, null,
    				null, null);
    		return;
    	}
    	
		//向服务端提交请求：绑定手机
    	getDataHelper().sendConfirmBinding(mHandler, AlipayHandlerMessageIDs.PHONE_BINDING_CONFIRM, 
    			mTxtCheckCode.getText().toString(), mTxtPaypwd.getText().toString(),mBindMobileNo);
	
    	openProcessDialog(getString(R.string.PhoneBindingWaitingMessage));
    }
	
	private void openProcessDialog(String msg){
		if (mProgress == null){
			mProgress = getDataHelper().showProgressDialogWithCancelButton(this, null,
					msg, false, true, getDataHelper().cancelListener,
					getDataHelper().cancelBtnListener);
		}
	}
	
	private void closeProgress(){
		if (mProgress != null){	
		mProgress.dismiss();
		mProgress=null;
		}
	}
	
	
	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		
		switch(v.getId()){
		//
		}
	}
	
	public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
        	mActivity.setResult(RESULT_CANCELED);
        	mActivity.finish();
            return true;
        } else if (keyCode == KeyEvent.KEYCODE_SEARCH){
        	//捕获此事件，但不做处理
        	return true;
        }
        return false;
    }
	
}