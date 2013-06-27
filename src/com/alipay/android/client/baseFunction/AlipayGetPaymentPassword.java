/**
 * 
 */
package com.alipay.android.client.baseFunction;

import org.json.JSONException;
import org.json.JSONObject;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Message;
import android.view.KeyEvent;
import android.view.View;
import android.widget.TextView;

import com.alipay.android.client.constant.Constant;
import com.alipay.android.client.util.Responsor;
import com.eg.android.AlipayGphone.R;

/**
 * @author chengkuang
 * 找回支付密码，包括获取和输入短信校验码界面，重置支付密码界面
 */
public class AlipayGetPaymentPassword extends AlipayGetPasswordActivity
{
    private String mBindMobileNo = "" ;//绑定的手机号码
    private boolean isFromSMSTransfer = false;//是否来自短信上行流程到密码设置
	public void loadAllVariables()
	{
		TextView title = (TextView)findViewById(R.id.title_text);	
		title.setText(getResources().getString(R.string.GetPayPassword));
		loadCommonVarialbles(Constant.MODIFY_STYLE_TRADE);
		//Constant.STR_MOBILENO is set when user login.
//		TextView hintForInputSmsCheckCode = (TextView)findViewById(R.id.HintForInputSmsCheckCode);
//		hintForInputSmsCheckCode.setHint(getHintContent(Constant.STR_MOBILENO));
		
		Intent passIntent = getIntent();
		if(passIntent != null){
			if(passIntent.getStringExtra("rsaPK") != null && passIntent.getStringExtra("rsaTS") != null){
				rsaPK = passIntent.getStringExtra("rsaPK");
				rsaTS = passIntent.getStringExtra("rsaTS");
			}
		}
		
		if (isFromSMSTransfer = getIntent().getBooleanExtra("isFromSMSTransfer", false))
        {
            mResetPasswordView.setVisibility(View.VISIBLE);
        }else {
            if (getUserData().isCertificate()) {
                mIdCardView.setVisibility(View.VISIBLE);
            }else{
                mRetrieveAndGetSmsCheckCodeView.setVisibility(View.VISIBLE);
            }
            
        } 
	}
	
	public void showResult(Message msg)
	{
		Responsor responsor = (Responsor) msg.obj;
		JSONObject obj = responsor.obj;
		String memo = responsor.memo;
		boolean tResultOK = false;
		boolean needDismissProcessDialog = true;
        
        tResultOK = mMessageFilter.process(msg);
        if(obj!=null){
            rsaPK = obj.optString("rsaPK","");
            rsaTS = obj.optString("rsaTS","");
        }
        
		if ((tResultOK) && (!getDataHelper().isCanceled()))
		{
		    if(mOperationType == ID_CARD_CONFIRM&&obj!=null)//服务器返回获取短信校验码结果
            {
		        final String isSmsSwitch = obj.optString(Constant.RPF_SMSTRANS_SENDSMS_SMSSWITCH,"false");
		        final String restTime = obj.optString(Constant.RPF_SMSTRANS_SENDSMS_RESTTIME,"0");
		        
		        mResetKey = obj.optString(Constant.RPF_RESETKEY,"");
		        
		        getDataHelper().showDialog(this, R.drawable.infoicon, getResources()
	                    .getString(R.string.StrConfirmPayTitle),
		                memo, this.getResources().getString(
                                R.string.Ensure),
                        new DialogInterface.OnClickListener() {
                            @Override public void onClick(
                                    DialogInterface dialog, int which)
                            {
                                    if ("true".equals(isSmsSwitch)) {
                                        Intent tIntent = null;
                                        tIntent = new Intent(AlipayGetPaymentPassword.this,
                                                AlipayGetPaymentPasswordSMSTransfer.class);
                                        tIntent.putExtra("rsaPK", rsaPK);
                                        tIntent.putExtra("rsaTS", rsaTS);
                                        
                                        
                                        // tIntent.putExtra("checkCode",
                                        // myHelper.mDefaultValueMap.get(Constant.RPF_CHECK_CODE));
                                        tIntent.putExtra(Constant.RPF_SMSTRANS_SENDSMS_RESTTIME, restTime);
                                        startActivity(tIntent);
                                        AlipayGetPaymentPassword.this.finish();
                                    }else if ("false".equals(isSmsSwitch)) {
                                        mGetSmsCheckCodeButton.setEnabled(false);
                                        startTimer();
                                        mIdCardView.setVisibility(View.GONE);
                                        mRetrieveAndGetSmsCheckCodeView.setVisibility(View.VISIBLE);
                                        
                                    }
                                // TODO Auto-generated method stub
                                
                            }
                        }, null, null, null, null);
            }   
		    
		    else if(mOperationType == GET_SMS_CHECK_CODE)//服务器返回获取短信校验码结果
			{
				try
				{
					mResetKey = obj.optString(Constant.RPF_RESETKEY,"");
					
					mBindMobileNo = obj.getString(Constant.RPF_MOBILE_NO);
                    if (mBindMobileNo != null && !mBindMobileNo.equals("")) {
                        TextView hintForInputSmsCheckCode = (TextView) findViewById(R.id.HintForInputSmsCheckCode);
                        hintForInputSmsCheckCode.setHint(getHintContent(mBindMobileNo));
                    }
					
                    getDataHelper().showDialog(AlipayGetPaymentPassword.this, 
							R.drawable.infoicon, 
							getResources().getString(R.string.WarngingString),memo,
							getResources().getString(R.string.Ensure),btnForOk,null, null,null, null);
				}
				catch (JSONException e)
				{
				}
			}				
			else if(mOperationType == VALIDATE_SMS_CHECK_CODE)//服务器返回验证短信校验码成功结果，客户端进入重置密码界面。
			{
			    stopTimer();
				mGetSmsCheckCodeButton.setEnabled(true);
				mGetSmsCheckCodeButton.setText(getString(R.string.GetPassTipViewTwoInfo2));
				mSmsCheckCodeEditText.setText("");
				mPassword.setText("");
				mPassword.requestFocus();
//				mPasswordAgain.setText("");
				mRetrieveAndGetSmsCheckCodeView.setVisibility(View.GONE);
				mResetPasswordView.setVisibility(View.VISIBLE);
			}
			else if(mOperationType == SET_PASSWORD)//服务器返回设置密码结果
			{
			    getDataHelper().showDialog(AlipayGetPaymentPassword.this, 
						R.drawable.infoicon, 
						getString(R.string.GetPaySuccessTitle),getString(R.string.GetPaySuccessInfo),
						getString(R.string.Ensure), new DialogInterface.OnClickListener(){
					@Override
					public void onClick(DialogInterface dialog, int which) {
						finish();
					}
				},
				null, null,
				null, null);
			}
		}

		if (needDismissProcessDialog)
		{
			if (mProgress != null)
			{
				mProgress.dismiss();
				mProgress = null;
			}
		}
	}
	
	/*@Override
	public void onActivityResult(int requestCode, int resultCode, Intent intent) {
		super.onActivityResult(requestCode, resultCode, intent);
		
		if(requestCode == VALIDATESMS && resultCode == Activity.RESULT_OK){
			if (isFromSMSTransfer = intent.getBooleanExtra("isFromSMSTransfer", true))
	        {
				if (getUserData().isCertificate()) {
	                mIdCardView.setVisibility(View.VISIBLE);
	            }else{
	                mRetrieveAndGetSmsCheckCodeView.setVisibility(View.VISIBLE);
	            }
	        }
		}
    }*/
	
	/**
	 * 每次返回到前一View.到最前view时退出Activity.
	 * @param keyCode
	 * @param event
	 * @return
	 * @see android.app.Activity#onKeyDown(int, android.view.KeyEvent)
	 */
	public boolean onKeyDown(int keyCode, KeyEvent event)
	{		
		if (keyCode == KeyEvent.KEYCODE_BACK)
		{
			if(mResetPasswordView!=null && mResetPasswordView.getVisibility() == View.VISIBLE)
			{
				if(isFromSMSTransfer){
				    getDataHelper().showDialog(AlipayGetPaymentPassword.this, 
							R.drawable.infoicon, 
							getResources().getString(R.string.WarngingString),getResources().getString(R.string.WarningGiveUpGetPayPSW),
							getResources().getString(R.string.Ensure),giveUpGetPayPSWEnsure,getResources().getString(R.string.Cancel), null,null, null);
				}else
				{
				mResetPasswordView.setVisibility(View.GONE);
				mRetrieveAndGetSmsCheckCodeView.setVisibility(View.VISIBLE);
				}
			}
			else if(mRetrieveAndGetSmsCheckCodeView!=null && mRetrieveAndGetSmsCheckCodeView.getVisibility() == View.VISIBLE)
			{
				this.finish();
			}
			else if(mIdCardView!=null && mIdCardView.getVisibility() == View.VISIBLE)
            {
                this.finish();
            }
			return true;
		}
		return false;
	}
	
	
	 protected DialogInterface.OnClickListener giveUpGetPayPSWEnsure= new DialogInterface.OnClickListener()
	    {
	        @Override
	        public void onClick(DialogInterface dialog, int which) {
	        	AlipayGetPaymentPassword.this.finish();
	        }
	    };
    @Override
    protected String getResetPasswordType() {
        return Constant.OP_RESETTRADEPASSWORD;
    }

    @Override
    protected String getPreResetPasswordType() {
        return Constant.OP_PRERESET_TRADE_PASSWORD;
    }
    
    public void onActivityResult(int requestCode, int resultCode, Intent intent) {
        switch (requestCode) {
            case Constant.REQUEST_PHONE_BINDDING:
                if (resultCode == RESULT_OK) {
                	doGetSmsCheckCode();
                }
                break;
        }
    }

    @Override
    protected void doGetSmsCheckCode() {
        mOperationType = GET_SMS_CHECK_CODE;
        doGetSmsCheckCode(getMobileNo(), "",Constant.MODIFY_STYLE_TRADE);
    }
}
