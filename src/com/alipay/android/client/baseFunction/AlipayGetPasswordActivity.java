
package com.alipay.android.client.baseFunction;

import java.util.Timer;
import java.util.TimerTask;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Editable;
import android.text.TextWatcher;
import android.text.method.PasswordTransformationMethod;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.EditText;
import android.widget.ScrollView;

import com.alipay.android.client.MessageFilter;
import com.alipay.android.client.RootActivity;
import com.alipay.android.client.constant.AlipayHandlerMessageIDs;
import com.alipay.android.client.constant.Constant;
import com.alipay.android.client.util.AlipayInputErrorCheck;
import com.alipay.android.client.util.BaseHelper;
import com.alipay.android.client.util.LephoneConstant;
import com.alipay.android.client.util.Utilz;
import com.alipay.android.comon.component.EditTextWithButton;
import com.alipay.android.comon.component.ProgressDiv;
import com.alipay.android.log.Constants;
import com.alipay.android.security.RSA;
import com.eg.android.AlipayGphone.R;

/**
 * 找回支付密码或者找回登录密码的抽象父类。
 * 
 * @author cheng.zhou
 * @version $Id: AlipayGetPasswordActivity.java, v 0.1 2010-10-12 下午06:25:01
 *          cheng.zhou Exp $
 */
public abstract class AlipayGetPasswordActivity extends RootActivity {
    protected int number = 60;// 重新获取短信校验码的倒计时数字
    
    public final int VALIDATESMS = 1688;

    protected static final int TIMER_MESSAGE = 1; // 重新获取短信校验码的时钟消息

    private Timer timer; // 重新获取短信校验码的倒计时timer

    private Activity mActivity = null;

    protected ScrollView mResetPasswordView;// 输入新密码的view

    protected ScrollView mRetrieveAndGetSmsCheckCodeView;// 获取和输入短信校验码的view

    protected ScrollView mIdCardView;// 身份验证view

    // 获取和输入短信校验码view里的"重新获取校验码"按钮，点击该按钮将向服务器请求校验码，服务器会将校验码发送到用户手机
    protected Button mGetSmsCheckCodeButton;

    // 获取和输入短信校验码view里的”下一步“按钮，点击该按钮将向服务器验证短信校验码
    protected Button mValidateSmsCheckCodeButton;

    // 身份认证界面里面下一步的按钮
    protected Button idCardViewButton;

    protected EditText mSmsCheckCodeEditText;// 短信验证码输入框

    protected EditText mPassword;// 密码输入框
    // protected EditText mPasswordAgain ;//确认密码输入框

    protected ProgressDiv mProgress; // 进度条

    protected CheckBox mShowPasswordCheckBox; // 是否显示密码的选择框

    protected EditText mIdCardEditText;// 身份证输入框

    protected int mOperationType = GET_SMS_CHECK_CODE; // 操作类型

    protected static final int GET_SMS_CHECK_CODE = 1; // 获取短信校验码

    protected static final int VALIDATE_SMS_CHECK_CODE = 2; // 验证短信校验码

    protected static final int SET_PASSWORD = 3; // 设置密码

    protected static final int ID_CARD_CONFIRM = 4; // 身份验证

    protected MessageFilter mMessageFilter = new MessageFilter(this);

    protected String mResetKey;

    // 加载view里的控件
    public abstract void loadAllVariables();

    // 处理服务器返回并显示结果
    public abstract void showResult(Message msg);

    // 得到重置密码操作的类型，返回登录密码或支付密码
    abstract protected String getResetPasswordType();

    // 得到验证短信校验码操作的类型，可为验证登录密码或支付密码
    abstract protected String getPreResetPasswordType();

    abstract protected void doGetSmsCheckCode();

    // 找回登陆密码身份验证按钮触发事件
    protected void doIdCardConfirm(String type) {
        mOperationType = ID_CARD_CONFIRM;
        Editable idCardTemp = mIdCardEditText.getText();
        String idCard = "";
        if (idCardTemp != null) {
            idCard = idCardTemp.toString();
        }
        //
        getDataHelper().sendIdCard(mHandler, AlipayHandlerMessageIDs.CHECK_IDCARD, idCard, type);
        if (mProgress == null) {
            mProgress = getDataHelper().showProgressDialogWithCancelButton(this, null, getResources()
                    .getString(R.string.RegisterGetImage), false, true, getDataHelper().cancelListener,
                    getDataHelper().cancelBtnListener);
        }
    };

    protected Handler mHandler = new Handler() {
		public void handleMessage(Message msg) {
            if (msg.what != TIMER_MESSAGE) {
                showResult(msg);
            }
            switch (msg.what) {
                case TIMER_MESSAGE:
                    if (mGetSmsCheckCodeButton != null) {
                        mGetSmsCheckCodeButton.setText("(" + number-- + ")秒后"
                                + getString(R.string.GetPassTipViewTwoInfo2));
                        mGetSmsCheckCodeButton.setTextColor(getResources().getColor(R.color.TextColorGray));
                        // if(number < 0)
                        // {
                        // throw new
                        // IllegalStateException("number should not less than 0");
                        // }
                        if (number == 0) {
                            mGetSmsCheckCodeButton.setEnabled(true);
                            mGetSmsCheckCodeButton
                                    .setText(getString(R.string.GetPassTipViewTwoInfo2));
                            mGetSmsCheckCodeButton.setTextColor(getResources().getColor(R.color.ButtonColorYellow));
                            stopTimer();
                        }
                    }
                    break;
            }

            super.handleMessage(msg);
        }
    };
    
    protected String rsaPK;
    protected String rsaTS;
	
	@Override
	protected void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		if(rsaPK != null && rsaTS != null){
			outState.putString("rsaPK", rsaPK);
			outState.putString("rsaTS", rsaTS);
		}
	}

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (LephoneConstant.isLephone()) {
            getWindow().addFlags(LephoneConstant.FLAG_ROCKET_MENU_NOTIFY);
        }
        this.setContentView(R.layout.alipay_getpassword_320_480);

        mActivity = this;
        
        loadAllVariables();
        
        if(savedInstanceState != null){
        	String savedRsaPK = savedInstanceState.getString("rsaPK");
        	String savedRsaTS = savedInstanceState.getString("rsaTS");
        	
        	if(savedRsaPK != null){
        		rsaPK = savedRsaPK;
        	}
        	if(savedRsaTS != null){
        		rsaTS = savedRsaTS;
        	}
        }
    }

    /**
     * 加载找回登录密码和找回支付密码共同的变量。
     */
    protected void loadCommonVarialbles(final String type) {
        mIdCardEditText = ((EditTextWithButton) findViewById(R.id.IdCardEditText)).getEtContent();
        mIdCardEditText.addTextChangedListener(mWatcherForIdCard);
        mResetPasswordView = (ScrollView) findViewById(R.id.ResetPasswordView);
        mRetrieveAndGetSmsCheckCodeView = (ScrollView) findViewById(R.id.RetrieveAndInputSmsCheckCodeView);
        mIdCardView = (ScrollView) findViewById(R.id.IdCardView);

        idCardViewButton = (Button) findViewById(R.id.IdCardNext);
        idCardViewButton.setEnabled(false);
        // 进行身份验证
        idCardViewButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View arg0) {
                doIdCardConfirm(type);
            }
        });

        mSmsCheckCodeEditText = ((EditTextWithButton) findViewById(R.id.SmsCheckCodeEditText))
                .getEtContent();
        mSmsCheckCodeEditText.addTextChangedListener(mWatcher);
        mPassword = (EditText) findViewById(R.id.SetPWEditText1);
        // mPasswordAgain = (EditText)findViewById(R.id.SetPWEditText2);
        mGetSmsCheckCodeButton = (Button) findViewById(R.id.GetSmsCheckCodeButton);
        mGetSmsCheckCodeButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View arg0) {
                doGetSmsCheckCode();
            }
        });

        mValidateSmsCheckCodeButton = (Button) findViewById(R.id.ValidateSmsCheckCodeButton);
        mValidateSmsCheckCodeButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View arg0) {
                doValidateSmsCheckCode(getPreResetPasswordType(), mSmsCheckCodeEditText.getText()
                        .toString());
            }
        });
        Button resetPasswordButton = (Button) findViewById(R.id.ResetPasswordButton);
        resetPasswordButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View arg0) {
                doResetPassword(getResetPasswordType());
            }
        });
        CheckBox showPasswordCheckBox = (CheckBox) findViewById(R.id.ShowPasswordCheckBox);
        showPasswordCheckBox.setOnCheckedChangeListener(new OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    // mPassword.setTransformationMethod(null);
                    // mPasswordAgain.setTransformationMethod(null);
                    BaseHelper.setDispayMode(mPassword, null);
                    // BaseHelper.setDispayMode(mPasswordAgain,null);
                } else {
                    // mPassword.setTransformationMethod(new
                    // PasswordTransformationMethod());
                    // mPasswordAgain.setTransformationMethod(new
                    // PasswordTransformationMethod());
                    BaseHelper.setDispayMode(mPassword, new PasswordTransformationMethod());
                    // BaseHelper.setDispayMode(mPasswordAgain,new
                    // PasswordTransformationMethod());
                }
            }

        });
    }

    /*
     * 验证短信校验码，验证成功后服务器会返回时间戳,客户端将转到修改密码界面
     */
    protected void doValidateSmsCheckCode(String type, String sms) {
        mOperationType = VALIDATE_SMS_CHECK_CODE;
        if (sms.length() == 0) {
        	BaseHelper.recordWarningMsg(this,getResources().getString(R.string.CheckcodeNotNull),Constants.FINDLOGINPASSWORDVIEW);
        	
        	getDataHelper().showDialog(this, R.drawable.infoicon,
                    getResources().getString(R.string.WarngingString),
                    getResources().getString(R.string.CheckcodeNotNull),
                    getResources().getString(R.string.Ensure), null, null, null, null, null);
            return;
        }
        getDataHelper().sendPreResetPassword(mHandler, AlipayHandlerMessageIDs.PRERESET, type, mResetKey,
                sms);
        if (mProgress == null) {
            mProgress = getDataHelper().showProgressDialogWithCancelButton(this, null, getResources()
                    .getString(R.string.RegisterGetImage), false, true, getDataHelper().cancelListener,
                    getDataHelper().cancelBtnListener);
        }
    }

    /*
     * 重置新的密码
     */
    protected void doResetPassword(String type) {
        mOperationType = SET_PASSWORD;
        String tPassword = mPassword.getText().toString();
        // String tPasswordAgain = mPasswordAgain.getText().toString();

        // if(!tPassword.equals(tPasswordAgain))
        // {
        // myHelper.showDialog(this, R.drawable.infoicon, getResources()
        // .getString(R.string.WarngingString), getResources().getString(
        // R.string.PasswordWrongInfo),
        // getResources().getString(R.string.Ensure), null, null,
        // null, null, null);
        // return;
        // }
        int tResult = AlipayInputErrorCheck.checkPayingPasswordSet(tPassword);
        if (tResult != AlipayInputErrorCheck.NO_ERROR) {
            String warningMsg;
            if (tResult == AlipayInputErrorCheck.ERROR_INVALID_FORMAT) {
                warningMsg = getResources().getString(R.string.WarningInvalidLoginPassword);
            } else if (tResult == AlipayInputErrorCheck.ERROR_NULL_INPUT) {
                warningMsg = getResources().getString(R.string.NoEmptyPassword);
            } else {
                warningMsg = "UNKNOWN_ERROR TYPE = " + tResult;
            }
            
            BaseHelper.recordWarningMsg(this,warningMsg,Constants.FINDLOGINPASSWORDVIEW);
            
            getDataHelper().showDialog(this, R.drawable.infoicon,
                    getResources().getString(R.string.WarngingString), warningMsg, getResources()
                            .getString(R.string.Ensure), null, null, null, null, null);
            return;
        }
        
        if(Utilz.isEmpty(rsaTS) || Utilz.isEmpty(rsaPK)){
        	rsaTS = getConfigData().getTimeStamp();
        	rsaPK = getConfigData().getPublicKey();
        }
        
        getDataHelper().sendResetPassword(mHandler, AlipayHandlerMessageIDs.RESET, type, RSA.encrypt(tPassword + rsaTS, rsaPK)
                /*.getText().toString()*/);
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

    private TextWatcher mWatcher = new TextWatcher() {
        @Override
        public void afterTextChanged(Editable s) {

        }

        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            if (s.length() > 0) {
                // 已经xml中设置了该button的背景。不需要在这设置了
                // mValidateSmsCheckCodeButton.setBackgroundResource(R.drawable.alipay_button_main);
                mValidateSmsCheckCodeButton.setEnabled(true);
            } else {
                // 已经xml中设置了该button的背景。不需要在这设置了
                // mValidateSmsCheckCodeButton.setBackgroundResource(android.R.drawable.btn_default);
                mValidateSmsCheckCodeButton.setEnabled(false);
            }
        }

    };

    /**
     * 获取短信校验码，服务器会将短信校验码发送到用户手机。
     * 
     * @param isResetSend
     * @param phone 用户账号
     */
    protected void doGetSmsCheckCode(String phone, String code, String style) {
        int tResult = AlipayInputErrorCheck.CheckUserID(phone);
        if (tResult != AlipayInputErrorCheck.NO_ERROR) {
            // check error.
            String warningMsg;
            if (tResult == AlipayInputErrorCheck.ERROR_INVALID_FORMAT) {
                warningMsg = getResources().getString(R.string.WarningInvalidAccountName);
            } else if (tResult == AlipayInputErrorCheck.ERROR_NULL_INPUT) {
                warningMsg = getResources().getString(R.string.NoEmptyAccountName);
            } else {
                warningMsg = "UNKNOWN_ERROR TYPE = " + tResult;
            }
            
            BaseHelper.recordWarningMsg(this,warningMsg,Constants.FINDLOGINPASSWORDVIEW);
            
            getDataHelper().showDialog(this, R.drawable.infoicon,
                    getResources().getString(R.string.WarngingString), warningMsg, getResources()
                            .getString(R.string.Ensure), null, null, null, null, null);
            return;
        }

        getDataHelper().sendSMSCheckCode(mHandler, AlipayHandlerMessageIDs.SMSCHECKCODE, phone, code,
                style);

        if (mProgress == null) {
            mProgress = getDataHelper().showProgressDialogWithCancelButton(this, null, getResources()
                    .getString(R.string.RegisterGetImage), false, true, getDataHelper().cancelListener,
                    getDataHelper().cancelBtnListener);
        }
    }

    protected DialogInterface.OnClickListener btnForOk = new DialogInterface.OnClickListener() {
        @Override
        public void onClick(DialogInterface dialog, int which) {
            mGetSmsCheckCodeButton.setEnabled(false);
            startTimer();
        }
    };

    protected void stopTimer() {
        if (timer != null) {
            timer.cancel(); // 停止timer
            timer = null;
        }
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

    public void onActivityResult(int requestCode, int resultCode, Intent intent) {
        switch (requestCode) {
            case Constant.REQUEST_PHONE_BINDDING:
                if (resultCode == RESULT_OK) {
                } else if (resultCode == RESULT_CANCELED) {
                    mActivity.finish();
                }
                break;
        }
    }

    private TextWatcher mWatcherForIdCard = new TextWatcher() {
        @Override
        public void afterTextChanged(Editable s) {

        }

        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {

            // 电话和验证码有一个为空，则下一步为disable.
            if (mIdCardEditText.getText().toString().equals("")) {
                idCardViewButton.setEnabled(false);
            } else {
                idCardViewButton.setEnabled(true);
            }
        }

    };
}
