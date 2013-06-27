package com.alipay.android.client.baseFunction;

import java.util.Timer;
import java.util.TimerTask;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ScrollView;
import android.widget.TextView;

import com.alipay.android.client.AlipayAccountManager;
import com.alipay.android.client.Login;
import com.alipay.android.client.MessageFilter;
import com.alipay.android.client.RootActivity;
import com.alipay.android.client.constant.AlipayHandlerMessageIDs;
import com.alipay.android.client.constant.Constant;
import com.alipay.android.client.util.AlipayInputErrorCheck;
import com.alipay.android.client.util.BaseHelper;
import com.alipay.android.client.util.DBHelper;
import com.alipay.android.common.data.UserInfo;
import com.alipay.android.comon.component.EditTextHasNullChecker;
import com.alipay.android.comon.component.EditTextWithButton;
import com.alipay.android.comon.component.ProgressDiv;
import com.alipay.android.log.Constants;
import com.eg.android.AlipayGphone.R;

public class SendSMSCodeActivity extends RootActivity{
	private Timer timer; // 重新获取短信校验码的倒计时timer
	private ProgressDiv mProgress; // 进度条
	private ScrollView mRetrieveAndGetSmsCheckCodeView;// 获取和输入短信校验码的view
	// 获取和输入短信校验码view里的"重新获取校验码"按钮，点击该按钮将向服务器请求校验码，服务器会将校验码发送到用户手机
	private Button mGetSmsCheckCodeButton;
	private int number = 60;// 重新获取短信校验码的倒计时数字
	private static final int TIMER_MESSAGE = 1; // 重新获取短信校验码的时钟消息
	private EditText mSmsCheckCodeEditText;// 短信验证码输入框
	private EditTextWithButton mSmsCheckCodeEditTextWithButton;
	private Button mValidateSmsCheckCodeButton;// 获取和输入短信校验码view里的”下一步“按钮，点击该按钮将向服务器验证短信校验码
	private MessageFilter mMessageFilter = new MessageFilter(this);
    private String mPasswordType;
    private String mNewPW;
    private String mCurrentPW;
    private TextView mHintForInputSmsCheckCode;
    private TextView mTitletext;
    private EditTextHasNullChecker hasNullChecker = new EditTextHasNullChecker();
	protected Handler mHandler = new Handler() {
		public void handleMessage(Message msg) {
			  int what = msg.what;
			if(what == TIMER_MESSAGE){
				if (mGetSmsCheckCodeButton != null) {
					mGetSmsCheckCodeButton.setEnabled(false);
					mGetSmsCheckCodeButton.setText("(" + number-- + ")秒后"
							+ getString(R.string.GetPassTipViewTwoInfo2));
					mGetSmsCheckCodeButton.setTextColor(getResources().getColor(R.color.TextColorGray));
					if (number == 0) {
						mGetSmsCheckCodeButton.setEnabled(true);
						mGetSmsCheckCodeButton
						.setText(getString(R.string.GetPassTipViewTwoInfo2));
						mGetSmsCheckCodeButton.setTextColor(getResources().getColor(R.color.ButtonColorYellow));
						stopTimer();
					}
				}
				return;
			}
		      boolean tResultOK = false;
		      tResultOK = mMessageFilter.process(msg);
			  if(mProgress != null){
				  mProgress.dismiss();
			  }
            switch (what) {
                case AlipayHandlerMessageIDs.RQF_CMSCODE://请求短信
					if(tResultOK){
						startTimer();
					}
					break;
                case AlipayHandlerMessageIDs.RQF_VERIFYMPASSWDCHECKCODE://上传短信验证结果
                	if(tResultOK){
                		modifyFinish();
                	}
		              break;
                default:
                	break;
            }
            super.handleMessage(msg);
        }
    };
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.sms_response);
		Intent intent = getIntent();
		if(intent != null){
			mPasswordType = intent.getStringExtra(Constant.RQF_PASSWORDTYPE);
			mNewPW  = intent.getStringExtra(Constant.RQF_NEWPASSWORD);
			mCurrentPW = intent.getStringExtra(Constant.RQF_OLDPASSWORD);
		}
			startTimer();
		loadCommonVarialbles();
	}
	
	/**
     * 加载找回登录密码和找回支付密码共同的变量。
     */
    protected void loadCommonVarialbles() {
    	mTitletext = (TextView) findViewById(R.id.title_text);
    	initTitle();
        mRetrieveAndGetSmsCheckCodeView = (ScrollView) findViewById(R.id.RetrieveAndInputSmsCheckCodeView);
        mSmsCheckCodeEditTextWithButton = (EditTextWithButton) findViewById(R.id.SmsCheckCodeEditText);
        mHintForInputSmsCheckCode = (TextView) findViewById(R.id.HintForInputSmsCheckCode);
        mHintForInputSmsCheckCode.setHint(getHintContent(getUserData().getMobileNo()));
        mSmsCheckCodeEditText = mSmsCheckCodeEditTextWithButton.getEtContent();
        mSmsCheckCodeEditText.addTextChangedListener(hasNullChecker);
        mGetSmsCheckCodeButton = (Button) findViewById(R.id.GetSmsCheckCodeButton);
        mGetSmsCheckCodeButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View arg0) {
                doGetSmsCheckCode();
            }
        });
        mValidateSmsCheckCodeButton = (Button) findViewById(R.id.ValidateSmsCheckCodeButton);
        hasNullChecker.addNeedEnabledButton(mValidateSmsCheckCodeButton);
        hasNullChecker.addNeedCheckView(mSmsCheckCodeEditText);
        mValidateSmsCheckCodeButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View arg0) {
            	doValidateSmsCheckCode(mSmsCheckCodeEditText.getText().toString());
            }
        });
    }
    
    private void initTitle() {
		if(Constant.MODIFY_STYLE_LOGIN.equalsIgnoreCase(mPasswordType)){
			mTitletext.setText(R.string.ModifyLoginPassword);
		}else if(Constant.MODIFY_STYLE_TRADE.equalsIgnoreCase(mPasswordType)){
			mTitletext.setText(R.string.ModifyPayPassword);
		}
	}

	protected void doGetSmsCheckCode() {
		getDataHelper().sendModifyPassword(mHandler,
				AlipayHandlerMessageIDs.RQF_CMSCODE, mNewPW,
				mCurrentPW, mPasswordType);
        if (mProgress == null) {
            mProgress = getDataHelper().showProgressDialogWithCancelButton(this, null, getResources()
                    .getString(R.string.RegisterGetImage), false, true, getDataHelper().cancelListener,
                    getDataHelper().cancelBtnListener);
        }
    }
	
    /*
     * 验证短信校验码，验证成功后服务器会返回时间戳,客户端将转到修改密码界面
     */
    protected void doValidateSmsCheckCode(String sms) {
        if (sms.length() == 0) {
        	BaseHelper.recordWarningMsg(this,getResources().getString(R.string.CheckcodeNotNull),Constants.FINDLOGINPASSWORDVIEW);
        	
        	getDataHelper().showDialog(this, R.drawable.infoicon,
                    getResources().getString(R.string.WarngingString),
                    getResources().getString(R.string.CheckcodeNotNull),
                    getResources().getString(R.string.Ensure), null, null, null, null, null);
            return;
        }
        getDataHelper().sendVerifyMPasswdCheckCode(mHandler, AlipayHandlerMessageIDs.RQF_VERIFYMPASSWDCHECKCODE, mPasswordType, sms, Constant.OP_VERIFYMPASSWDCHECKCODE);

        if (mProgress == null) {
            mProgress = getDataHelper().showProgressDialogWithCancelButton(this, null, getResources()
                    .getString(R.string.RegisterGetImage), false, true, getDataHelper().cancelListener,
                    getDataHelper().cancelBtnListener);
        }
    }
    
    /*
     * 设置短信校验码输入框的hint内容
     */
    protected String getHintContent(String phone) {
        String tNumber = phone;
        int tResult = AlipayInputErrorCheck.CheckUserID(phone);
        if (tResult != AlipayInputErrorCheck.NO_ERROR) {
            // check error.
            String warningMsg;
            if (tResult == AlipayInputErrorCheck.ERROR_INVALID_FORMAT) {
                warningMsg = getResources().getString(R.string.WarningInvalidAccountName);
            } else if (tResult == AlipayInputErrorCheck.ERROR_NULL_INPUT) {
                warningMsg = getResources().getString(R.string.NotBindPhoneNo);
            } else {
                warningMsg = "UNKNOWN_ERROR TYPE = " + tResult;
            }
            
            BaseHelper.recordWarningMsg(this,warningMsg,Constants.FINDLOGINPASSWORDVIEW);
            
            getDataHelper().showDialog(this, R.drawable.infoicon,
                    getResources().getString(R.string.WarngingString), warningMsg, getResources()
                            .getString(R.string.Ensure), new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            finish();
                            // AlipayPhoneBinding.startPhoneBinding(mActivity);
                        }

                    }, null, null, null, null);
            return getString(R.string.NotBindPhoneNo);
        }
        return getString(R.string.InputPhone) + tNumber.substring(0, 3) + "****"
                + tNumber.substring(7) + getString(R.string.ReceiveSMScode);
    }
    
    
    
	/**
     * 启动重新获取密码的倒计时timer, timer时间会在mHandler中处理
     */
    protected void startTimer() {
        number = 60;
        timer = new Timer();
        timer.schedule(new TimerTask() {
            public void run() {
                Message message = new Message();
                message.what = TIMER_MESSAGE;
                mHandler.sendMessage(message);
            }
        }, 0, 1000);
    }
    
    protected void stopTimer() {
        if (timer != null) {
            timer.cancel(); // 停止timer
            timer = null;
        }
    }
    
    private void modifyFinish() {
		String warningMsg = "";
		if (Constant.MODIFY_STYLE_LOGIN.equals(mPasswordType)) {
			warningMsg = getResources().getString(R.string.UseNewPasswordLogin);
		} else {
			warningMsg = getResources().getString(R.string.UseNewPasswordPay);
		}
		getDataHelper().showDialog(SendSMSCodeActivity.this,
				R.drawable.infoicon,
				getResources().getString(R.string.ModifySuccess), warningMsg,
				getResources().getString(R.string.Ensure), btn, null, null,
				null, null);
	}

	DialogInterface.OnClickListener btn = new DialogInterface.OnClickListener() {
		@Override
		public void onClick(DialogInterface dialog, int which) {
			if (Constant.MODIFY_STYLE_LOGIN.equals(mPasswordType)) {
				SendSMSCodeActivity.this.getConfigData().setSessionId(null);
				// APHttpClient.clearSessionCookie();
				Constant.GAS_JSON = null;
				Constant.WATER_JSON = null;
				Constant.ELECTRIC_JSON = null;
				Constant.COMMUN_JSON = null;

				if (SendSMSCodeActivity.this.getUserData() != null) {
					BaseHelper.exitProcessOnClickListener
							.clearUserCache(SendSMSCodeActivity.this);
					SendSMSCodeActivity.this.logoutUser();
				}
				
				//执行和退出一样的流程
		        DBHelper db = new DBHelper(SendSMSCodeActivity.this);
				UserInfo userInfo = db.getLastLoginUser(null);
				if (null != userInfo) {
					//重置登陆密码和自动登陆
					db.resetRsaPassword(userInfo.userAccount, userInfo.type);
					
					if (Constant.AUTOLOGIN_YES == db.getAutoLogin(userInfo.userAccount, userInfo.type)) {
						//这里不删除自动登陆记录，只是将自动登陆状态改为unknow状态
						db.saveAutoLogin(userInfo.userAccount, userInfo.type, Constant.AUTOLOGIN_UNKNOW);
					}
				}
				db.close();

				Intent tIntent = new Intent(SendSMSCodeActivity.this,
						Login.class);
				SendSMSCodeActivity.this.startActivityForResult(tIntent, Constant.REQUEST_LOGIN_BACK);
				SendSMSCodeActivity.this.setResult(AlipayAccountManager.MODIFY_PASSWORD_SUCCESS);
			}
			SendSMSCodeActivity.this.finish();
			}
	};  
    
    
    
}
