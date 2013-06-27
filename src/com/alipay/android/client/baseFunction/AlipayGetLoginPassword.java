/**
 * 
 */

package com.alipay.android.client.baseFunction;

import org.json.JSONException;
import org.json.JSONObject;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Message;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.alipay.android.appHall.common.Defines;
import com.alipay.android.client.Login;
import com.alipay.android.client.constant.AlipayHandlerMessageIDs;
import com.alipay.android.client.constant.Constant;
import com.alipay.android.client.util.BaseHelper;
import com.alipay.android.client.util.Responsor;
import com.alipay.android.comon.component.EditTextWithButton;
import com.alipay.android.util.Base64;
import com.eg.android.AlipayGphone.R;

/**
 * @author chengkuang 找回登录密码，包括输入电话号码和图片校验码界面，输入短信校验码界面以及重置登录密码的界面。
 */
public class AlipayGetLoginPassword extends AlipayGetPasswordActivity {
    public static final int VALIDATE_SMS_CHECK_CODE_FAILED = 115; // 输入校验码连续超过3次错误。

    public static final int VALIDATE_SMS_CHECK_CODE_ERROR = 118; // 输入校验码错误小于等于3次

    // 以下变量为输入电话号码和图片校验码View 所对应的变量和控件
    private ScrollView mInputPhoneNumberAndImageCheckCodeView;// 输入电话号码和图片校验码的view，只有找回登录密码时需要

    private EditText mPhoneNumberEditText; // 手机号码输入框

    private EditTextWithButton mImageCheckCodeEditText;// 图片校验码输入框

    private ImageView mCheckCodeImage;// 图片校验码的图片

    private String mBindMobileNo;// 绑定的手机号码

    private boolean mIsInputSmsCheckCodeError = false; // 是否输入了错误的短信校验码

    private Button mValidateImageCheckCodeButton; // 用于验证图片校验码的“下一步”按钮

    private static final int GET_IMAGE_CHECK_CODE = 0; // 操作类型：获取图片校验码

    private Display tDisplay = null;

    private DisplayMetrics tDisplayMetrics = null;
    
    public void loadAllVariables() {
        tDisplay = this.getWindowManager().getDefaultDisplay();
        tDisplayMetrics = new DisplayMetrics();
        tDisplay.getMetrics(tDisplayMetrics);
        TextView title = (TextView) findViewById(R.id.title_text);
        title.setText(getResources().getString(R.string.GetLoginPassword));
        mOperationType = GET_IMAGE_CHECK_CODE;
        doGetImageCheckCode();
        mCheckCodeImage = (ImageView) findViewById(R.id.CheckCodeImage);
        mPhoneNumberEditText = ((EditTextWithButton) findViewById(R.id.GetPhoneNumberEditText))
                .getEtContent();
        mPhoneNumberEditText.setText(getIntent().getStringExtra(Constant.LOGINACCOUNT_CACHE));
        BaseHelper.setDispayMode(mPhoneNumberEditText, null);
        
        // mPhoneNumberEditText.setInputType(InputType.TYPE_NULL);
        mPhoneNumberEditText.addTextChangedListener(mWatcher);// 响应用户输入，判断控件是否为空
        mValidateImageCheckCodeButton = (Button) findViewById(R.id.ValidateImageCheckCode);
        mValidateImageCheckCodeButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View arg0) {
            	getSMSCode();
            }
        });
        mImageCheckCodeEditText = (EditTextWithButton) findViewById(R.id.ImageCheckCodeEditText);
        mImageCheckCodeEditText.getEtContent().addTextChangedListener(mWatcher);// 响应用户输入，判断控件是否为空
        loadCommonVarialbles(Constant.MODIFY_STYLE_LOGIN);
        mInputPhoneNumberAndImageCheckCodeView = (ScrollView) findViewById(R.id.InputPhoneNumberAndImageCheckCodeView);
        mInputPhoneNumberAndImageCheckCodeView.setVisibility(View.VISIBLE);
    }
    
    // 用图片校验码获取登录短信校验码
    private void getSMSCode() {
        mOperationType = GET_SMS_CHECK_CODE;
        hideInput(mPhoneNumberEditText);
        hideInput(mImageCheckCodeEditText.getEtContent());
        doGetSmsCheckCode(mPhoneNumberEditText.getText().toString(),
                mImageCheckCodeEditText.getText().toString(), Constant.MODIFY_STYLE_LOGIN);
	}

    /**
     * 当服务器返回“重置密码失败”时，显示对话框，用户点确定后，返回输入电话号码和图片校验码界面。
     * 
     * @param memo
     */
    private void displayErrorMessage(String memo) {
        getDataHelper().showDialog(AlipayGetLoginPassword.this, R.drawable.infoicon, getResources()
                .getString(R.string.ErrorString), memo, getString(R.string.Ensure),
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        goBackToInputPhoneNumberAndImageCheckCodeView();
                    }
                }, null, null, null, null);
    }

//    /**
//     * 验证是否实名认证。
//     */
//    private void doAccountCertified() {
//        mOperationType = ACCOUNT_CERTIFIED;
//
//        myHelper.sendAccountCertified(mHandler, AlipayHandlerMessageIDs.RQF_ACCOUNTCERTIFIED,
//                mPhoneNumberEditText.getText().toString());
//        if (mProgress == null) {
//            mProgress = myHelper.showProgressDialogWithCancelButton(this, null, getResources()
//                    .getString(R.string.RegisterGetImage), false, true, myHelper.cancelListener,
//                    myHelper.cancelBtnListener);
//        }
//    }

    /**
     * 返回输入电话号码和图片校验码的界面。
     */
    private void goBackToInputPhoneNumberAndImageCheckCodeView() {
        stopTimer();
        //mPhoneNumberEditText.setText(mBindMobileNo); // 返回后显示之前的电话号码
        mPhoneNumberEditText.setSelection(mPhoneNumberEditText.getText().length());
        mImageCheckCodeEditText.setText("");
        mRetrieveAndGetSmsCheckCodeView.setVisibility(View.GONE);
        mInputPhoneNumberAndImageCheckCodeView.setVisibility(View.VISIBLE);
        refreshCheckImage(); // 刷新图片
    }
    
    public void onActivityResult(int requestCode, int resultCode, Intent intent) {
        switch (requestCode) {
            case Constant.REQUEST_PHONE_BINDDING:
                if (resultCode == RESULT_OK) {
                	getSMSCode();
                }
            break;
        }
    }

    public void showResult(Message msg) {
        Responsor responsor = (Responsor) msg.obj;
        JSONObject obj = responsor.obj;
        String memo = responsor.memo;
        boolean tResultOK = false;
        boolean needDismissProcessDialog = true;

        try {
            // 短信校验码试错三次以后，服务器返回重置密码失败，此时需要弹出定制对话框，用户点确定后后退到获取短信校验码的view
            if (obj != null && mOperationType == VALIDATE_SMS_CHECK_CODE
                    && obj.getInt(Constant.RPF_RESULT_STATUS) == VALIDATE_SMS_CHECK_CODE_FAILED) {
                mMessageFilter.setShowAlert(false);
            } else if (obj != null && mOperationType == VALIDATE_SMS_CHECK_CODE
                    && obj.getInt(Constant.RPF_RESULT_STATUS) == VALIDATE_SMS_CHECK_CODE_ERROR)// 如果输入校验码错误，则再次点击“获取校验码”应该返回前一界面
            {
                mIsInputSmsCheckCodeError = true;
            } else {
                mMessageFilter.setShowAlert(true);
            }
            tResultOK = mMessageFilter.process(msg);
            if (obj != null && mOperationType == VALIDATE_SMS_CHECK_CODE
                    && obj.getInt(Constant.RPF_RESULT_STATUS) == VALIDATE_SMS_CHECK_CODE_FAILED) {
                displayErrorMessage(memo);
            }
        } catch (JSONException e1) {
        	
        }

        if ((tResultOK) && (!getDataHelper().isCanceled())) {
            if (mOperationType == GET_IMAGE_CHECK_CODE)// 服务器返回获取图片校验码结果
            {
                if (obj != null) {
                    String mSession = "";
                    try {
                        mSession = (String) obj.get(Constant.RPF_SESSION_ID);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    if (mSession != null) {
                        setSessionId(mSession);
                    }  
                }
                processImageCheckCode(obj);
                
                
            } else if (mOperationType == GET_SMS_CHECK_CODE)// 服务器返回获取短信校验码结果
            {
                try {
                    mResetKey = obj.getString(Constant.RPF_RESETKEY);
                    mBindMobileNo = obj.getString(Constant.RPF_MOBILE_NO);
                    if (mBindMobileNo != null && !mBindMobileNo.equals("")) {
                        TextView hintForInputSmsCheckCode = (TextView) findViewById(R.id.HintForInputSmsCheckCode);
                        hintForInputSmsCheckCode.setHint(getHintContent(mBindMobileNo));
                    }
                    mSmsCheckCodeEditText.setText("");
                    mSmsCheckCodeEditText.requestFocus();
                    getDataHelper().showDialog(AlipayGetLoginPassword.this, R.drawable.infoicon,
                            getResources().getString(R.string.WarngingString), memo, getResources()
                                    .getString(R.string.Ensure), btnForOk, null, null, null, null);
                    mInputPhoneNumberAndImageCheckCodeView.setVisibility(View.GONE);
                    mRetrieveAndGetSmsCheckCodeView.setVisibility(View.VISIBLE);
                } catch (Exception e) {
                	e.printStackTrace();
                }
            } else if (mOperationType == VALIDATE_SMS_CHECK_CODE&&obj!=null)// 服务器返回验证校验码结果
            {
                boolean isCertificate = obj.optString(Constant.ISCERTIFIED).equals("Y");
                if (isCertificate) {
                    // =====================添加身份校验
                    goToIdCardView();
                } else {
                	String string = obj.optString(Constant.RPF_RSA_PK);
                    if(string != null){
                        setPublicKey(string);
                    }
                    
                    string = obj.optString(Constant.RPF_RSA_TS);
                    if(string != null){
                        setTimeStamp(string);
                    }
                	
                    goToResetPasswordView();
                }
            }

            else if (mOperationType == ID_CARD_CONFIRM&&obj!=null)// 服务器返回身份校验结果
            {
                //保存公钥和时间戳
                String string = obj.optString(Constant.RPF_RSA_PK);
                if(string != null){
                    setPublicKey(string);
                }
                
                string = obj.optString(Constant.RPF_RSA_TS);
                if(string != null){
                    setTimeStamp(string);
                }
                
                
                // 如果身份验证正确，进入重置密码界面.
                goToResetPasswordView();
            } else if (mOperationType == SET_PASSWORD)// 服务器返回设置密码结果
            {
                // 显示密码设置成功对话框，用户点确定后退出Activity.
                getDataHelper().showDialog(AlipayGetLoginPassword.this, R.drawable.infoicon,
                        getString(R.string.GetLoginSuccessTitle),
                        getString(R.string.GetLoginSuccessInfo), getString(R.string.Ensure),
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                doFinishMyself();
                            }
                        }, null, null, null, null);
            }
        }

        if (needDismissProcessDialog) {
            if (mProgress != null) {
                mProgress.dismiss();
                mProgress = null;
            }
        }
    }

    /**
     * 将密码清空，显示身份验证界面
     */
    private void goToIdCardView() {
        stopTimer();
        mRetrieveAndGetSmsCheckCodeView.setVisibility(View.GONE);
        mIdCardView.setVisibility(View.VISIBLE);
        
        if (null != mIdCardEditText) {
        	mIdCardEditText.requestFocus();
        }
    }

    /**
     * 将密码清空，显示重置密码界面
     */
    private void goToResetPasswordView() {
        stopTimer();
        
        if (mIdCardView!=null && mIdCardView.getVisibility() == View.VISIBLE) {
            mIdCardView.setVisibility(View.GONE);
        }
        mPassword.setText("");
        mPassword.requestFocus();
        // mPasswordAgain.setText("");
        mRetrieveAndGetSmsCheckCodeView.setVisibility(View.GONE);
        mResetPasswordView.setVisibility(View.VISIBLE);
    }

    /**
     * 得到图片校验码
     */
    private void doGetImageCheckCode() {
        getDataHelper().sendPreSendSMSCheckCode(mHandler, AlipayHandlerMessageIDs.CHECKCODEIMAGE);
        if (mProgress == null) {
            mProgress = getDataHelper().showProgressDialogWithCancelButton(this, null, getResources()
                    .getString(R.string.RegisterGetImage), false, true, getDataHelper().cancelListener,
                    getDataHelper().cancelBtnListener);
        }
    }

    /**
     * 处理服务器返回的图片校验码,如果失败，则提示重新刷新图片校验码。
     */
    private void processImageCheckCode(JSONObject jsonObject) {
        byte[] b = Base64.decode(jsonObject.optString(Constant.RPF_CHECK_CODE));
        if (b != null && b.length > 0) {
            mCheckCodeImage.setImageBitmap(BitmapFactory.decodeByteArray(b, 0, b.length));
            if (tDisplayMetrics.widthPixels > 320 && tDisplayMetrics.heightPixels > 480) {
                RelativeLayout.LayoutParams m = new RelativeLayout.LayoutParams(mCheckCodeImage
                        .getDrawable().getMinimumWidth() * 3 / 2, mCheckCodeImage.getDrawable()
                        .getMinimumHeight() * 3 / 2);
                m.topMargin = 20;
                m.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
                mCheckCodeImage.setLayoutParams(m);
            } else {
                RelativeLayout.LayoutParams m = new RelativeLayout.LayoutParams(mCheckCodeImage
                        .getDrawable().getMinimumWidth(), mCheckCodeImage.getDrawable()
                        .getMinimumHeight());
                m.topMargin = 13;
                m.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
                mCheckCodeImage.setLayoutParams(m);
            }
        } else {
            getDataHelper().showDialog(AlipayGetLoginPassword.this, R.drawable.infoicon, getResources()
                    .getString(R.string.WarngingString),
                    getResources().getString(R.string.RegisterCheckAgain), getResources()
                            .getString(R.string.Ensure), new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            refreshCheckImage();
                        }
                    }, null, null, null, null);
        }
    }

    /**
     * 刷新图片校验码
     */
    private void refreshCheckImage() {
        mOperationType = GET_IMAGE_CHECK_CODE;
        doGetImageCheckCode();
    }

    /**
     * 每次返回到前一界面。
     * 
     * @param keyCode
     * @param event
     * @return
     * @see android.app.Activity#onKeyDown(int, android.view.KeyEvent)
     */
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            if (mResetPasswordView != null && mResetPasswordView.getVisibility() == View.VISIBLE) {
                mResetPasswordView.setVisibility(View.GONE);
                mInputPhoneNumberAndImageCheckCodeView.setVisibility(View.VISIBLE);
                mPhoneNumberEditText.setText("");
                mImageCheckCodeEditText.setText("");
                refreshCheckImage();
            } else if (mRetrieveAndGetSmsCheckCodeView != null
                    && mRetrieveAndGetSmsCheckCodeView.getVisibility() == View.VISIBLE) {
                goBackToInputPhoneNumberAndImageCheckCodeView();
            } else if (mIdCardView != null && mIdCardView.getVisibility() == View.VISIBLE) {
                mIdCardView.setVisibility(View.GONE);
                mInputPhoneNumberAndImageCheckCodeView.setVisibility(View.VISIBLE);
                mPhoneNumberEditText.setText("");
                mImageCheckCodeEditText.setText("");
                refreshCheckImage();
            } else if (mInputPhoneNumberAndImageCheckCodeView != null
                    && mInputPhoneNumberAndImageCheckCodeView.getVisibility() == View.VISIBLE) {
            	doFinishMyself();
            }
            return true;
        }
        return false;
    }

    /**
     * zhixin:卡宝中找回登录密码的入口多了，在登陆后用户仍然有入口，而且交互要求要返回登陆页面，故增加此方法
     */
    private void doFinishMyself() {
    	
    	//如果已经注销则跳转到登陆
        Bundle extra = getIntent().getExtras();
    	if (null == getUserData() && extra.containsKey(Defines.NextActivity)) {
	        Intent tIntent = new Intent(AlipayGetLoginPassword.this, Login.class);

	        tIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
	        startActivity(tIntent);
    	}
    	
        this.finish();
    }

    @Override
    protected String getPreResetPasswordType() {
        return Constant.OP_PRERESET_LOGIN_PASSWORD;
    }

    @Override
    public String getResetPasswordType() {
        return Constant.OP_RESETLOGINPASSWORD;
    }

    @Override
    protected void doGetSmsCheckCode() {
        if (mIsInputSmsCheckCodeError)// 如果输入短信校验码错误，此时需要返回到前一界面重新输入图片校验码
        {
            mIsInputSmsCheckCodeError = false; // 需要将该状态重置为false
            goBackToInputPhoneNumberAndImageCheckCodeView();
            return;
        }
        mOperationType = GET_SMS_CHECK_CODE;
        doGetSmsCheckCode(mPhoneNumberEditText.getText().toString(), mImageCheckCodeEditText
                .getText().toString(), Constant.MODIFY_STYLE_LOGIN);
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
            // 电话和验证码有一个为空，则下一步为disable.
            if (mPhoneNumberEditText.getText().toString().equals("")
                    || mImageCheckCodeEditText.getText().toString().equals("")) {
                mValidateImageCheckCodeButton.setEnabled(false);
            } else {
                mValidateImageCheckCodeButton.setEnabled(true);
            }
        }

    };

    /**
     * 隐藏输入法
     */
    private void hideInput(EditText editText) {
        editText.clearFocus();
        InputMethodManager imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(editText.getWindowToken(), 0);
    }

}
