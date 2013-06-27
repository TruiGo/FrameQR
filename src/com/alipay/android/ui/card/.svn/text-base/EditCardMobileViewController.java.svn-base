package com.alipay.android.ui.card;

import org.json.JSONObject;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewStub;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.alipay.android.appHall.common.Defines;
import com.alipay.android.biz.CardManagerBiz;
import com.alipay.android.biz.CheckPasswordBiz;
import com.alipay.android.biz.CommonRespHandler;
import com.alipay.android.client.baseFunction.AlipayAccountBindingChoice;
import com.alipay.android.client.util.AlipayInputErrorCheck;
import com.alipay.android.client.util.DBHelper;
import com.alipay.android.client.util.Utilz;
import com.alipay.android.common.data.UserInfo;
import com.alipay.android.comon.component.EditTextWithButton;
import com.alipay.android.comon.component.TimerButton;
import com.alipay.android.ui.bean.BankCardInfo;
import com.alipay.android.ui.framework.BaseViewController;
import com.alipay.android.ui.framework.BaseViewController.BizAsyncTask;
import com.alipay.android.ui.transfer.NewReceiverViewController;
import com.alipay.android.util.JsonConvert;
import com.eg.android.AlipayGphone.R;

public class EditCardMobileViewController extends BaseViewController {
	private String mSignId;
	private String mUserPhone;
	private String mCardType;
	private String mUpdateType;
	
	private Button mConfirmButton;
	
	@Override
	protected void onCreate() {
		super.onCreate();
		mView = LayoutInflater.from(getRootController()).inflate(R.layout.alipay_express_edit, null, false);
		addView(mView, null);
		
		final BankCardInfo cardInfo = (BankCardInfo)params;
		mSignId = cardInfo.getCardId();
		mCardType = cardInfo.getCardType();
		
		TextView title = (TextView)findViewById(R.id.title_text);
		
		int bizAvailable = cardInfo.getBizType();
		if (Utilz.equalsAtBit(bizAvailable, BankCardInfo.BIZ_EXPRESS_MOBILE_EDITABLE)) {
			mUpdateType = "modify";
			title.setText(R.string.express_mobile_edit);
		} else if (Utilz.equalsAtBit(bizAvailable, BankCardInfo.BIZ_EXPRESS_MOBILE_EMPTY)) {
			mUpdateType = "add";
			title.setText(R.string.express_mobile_add);
		}
		
		mSmsResendButton = (TimerButton)findViewById(R.id.resendButton);
		mSmsResendButton.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				mSmsResend = true;
				mSmsCode = null;
				modifyExpressUserPhone();
			}
		});
		
		mConfirmButton = (Button)findViewById(R.id.confirmButton);
		mConfirmButton.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				if (0 == mCurStep) {
					mUserPhone = mEditText.getText().toString();
					int tResult = AlipayInputErrorCheck.checkPhoneNumber(mUserPhone);
					if (AlipayInputErrorCheck.NO_ERROR == tResult) {
						verifyModifyExpressUserPhone();
					} else {
						String warningMsg;
			            if (tResult == AlipayInputErrorCheck.ERROR_INVALID_FORMAT) {
			                warningMsg = getString(R.string.WarningPhoneNumberFormatError);
			            } else if (tResult == AlipayInputErrorCheck.ERROR_NULL_INPUT) {
			                warningMsg = getString(R.string.PhoneNOEmpty);
			            } else {
			                warningMsg = "UNKNOWN_ERROR TYPE = " + tResult;
			            }
			            if (!warningMsg.equals("")) {
			                showDialog(R.drawable.infoicon, getString(R.string.WarngingString), warningMsg,
			                    getString(R.string.Ensure), null, null, null, null, null);
			            }
					}

					
				} else if (1 == mCurStep) {
					mSmsCode = mEditText.getText().toString();
					modifyExpressUserPhone();
				}

			}
		});
		
		loadStep(STEP_ONE);
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
        	return back();
        }
        
		return super.onKeyDown(keyCode, event);
	}
	
	private boolean back() {
		if (mCurStep > 0) {
			loadStep(--mCurStep);
			return true;
		}
		
		return false;
	}
	
	private static final int STEP_ONE = 0;
	private static final int STEP_TWO = 1;
	private int mCurStep = STEP_ONE;
	private TimerButton mSmsResendButton;
	private boolean mSmsResend = false;
	private String mSmsCode;
	private void loadStep(int stepIndex) {
		mCurStep = stepIndex;
		mConfirmButton.setEnabled(false);
		
		switch (stepIndex) {
		case STEP_ONE:
			int descriptionResId = 0;
			int editHintResId = 0;
			if ("modify".equals(mUpdateType)) {
				descriptionResId = R.string.express_mobile_edit_description;
				editHintResId = R.string.express_mobile_edit_hint;
			} else {
				descriptionResId = R.string.express_mobile_add_description;
				editHintResId = R.string.express_mobile_add_hint;
			}
			setTopDescription(descriptionResId);
			setEditHint(editHintResId);
			mEditText.setInputType(InputType.TYPE_CLASS_PHONE);
			Utilz.setTextLimit(mEditText, 11);
			mEditText.addTextChangedListener(new TextWatcher() {
				@Override
				public void onTextChanged(CharSequence s, int start, int before, int count) {
				}
				
				@Override
				public void beforeTextChanged(CharSequence s, int start, int count,
						int after) {
				}
				
				@Override
				public void afterTextChanged(Editable inputedStr) {
					mConfirmButton.setEnabled(inputedStr.length() > 0);
				}
			});
			
			if (null != mSmsResendButton && View.VISIBLE == mSmsResendButton.getVisibility()) {
				mSmsResendButton.setVisibility(View.GONE);
			}
			break;
		case STEP_TWO:
			String userPhoneTemp = Utilz.getMobileNum4Show(mUserPhone);
			setTopDescription(String.format(getString(R.string.express_afterSmsSent), userPhoneTemp));
			setEditHint(R.string.InputCheckCodeHint);
			mEditText.setInputType(InputType.TYPE_CLASS_TEXT);
			Utilz.setTextLimit(mEditText, -1);
			
			if (null == mSmsResendButton) {
				mSmsResendButton = (TimerButton)findViewById(R.id.resendButton);
				mSmsResendButton.setOnClickListener(new View.OnClickListener() {
					
					@Override
					public void onClick(View v) {
						mSmsResend = true;
						mSmsCode = null;
						mSmsResendButton.setTickCount(60).startTicking();
						modifyExpressUserPhone();
					}
				});
			}
			mSmsResendButton.setFormatText(getString(R.string.express_resend_afterSmsSent))
			.setTextEnabled(getString(R.string.express_resend_enabled))
			.setTickCount(60).startTicking();
			
			if (View.GONE == mSmsResendButton.getVisibility()) {
				mSmsResendButton.setVisibility(View.VISIBLE);
			}

			break;
		default:
			break;
		}
	}
	
	private TextView mTopDescription;
	private void setTopDescription(int resId) {
		if (null == mTopDescription) {
			mTopDescription = (TextView)findViewById(R.id.topDescription);
		}
		
		mTopDescription.setText(resId);
	}
	private void setTopDescription(String text) {
		if (null == mTopDescription) {
			mTopDescription = (TextView)findViewById(R.id.topDescription);
		}
		
		mTopDescription.setText(text);
	}
	
	private EditText mEditText;
	private void setEditHint(int resId) {
		if (null == mEditText) {
			mEditText = (EditText)findViewById(R.id.editText);
		}
		
		mEditText.setText(null);
		mEditText.setHint(getString(resId));
	}
	
	private void verifyModifyExpressUserPhone() {
		new BizAsyncTask(CardManagerBiz.BIZTAG_VERIFYMODIFYEXPRESSUSERPHONE, true).execute();
	}
	
	private void modifyExpressUserPhone() {
		new BizAsyncTask(CardManagerBiz.BIZTAG_MODIFYEXPRESSUSERPHONE, true).execute();
	}
	
	/**
	 * 异步任务
	 */
	@Override
	protected Object doInBackground(String bizType, String... params) {
		if (bizType.equalsIgnoreCase(CardManagerBiz.BIZTAG_VERIFYMODIFYEXPRESSUSERPHONE)) {
			return new CardManagerBiz().verifyModifyExpressUserPhone(mSignId, mUserPhone, mUpdateType);
		}
		else if (bizType.equalsIgnoreCase(CardManagerBiz.BIZTAG_MODIFYEXPRESSUSERPHONE)) {
       		return new CardManagerBiz().modifyExpressUserPhone(mSmsCode, mSmsResend ? "true" : "false");
       	}
		
		return super.doInBackground(bizType, params);
	}
	
	
	@Override
	protected void onPostExecute(String bizType, Object result) {
		JSONObject responseJson = JsonConvert.convertString2Json((String)result);

    	if(!CommonRespHandler.processSpecStatu(getRootController(), responseJson)){
			return;
		}
		if(CommonRespHandler.filterBizResponse(getRootController(), responseJson)){
			if (bizType.equalsIgnoreCase(CardManagerBiz.BIZTAG_VERIFYMODIFYEXPRESSUSERPHONE)) {
				//modifyExpressUserPhone();
				loadStep(STEP_TWO);
			} else if (bizType.equalsIgnoreCase(CardManagerBiz.BIZTAG_MODIFYEXPRESSUSERPHONE)) {
				showDialog(R.drawable.infoicon, null, 
            			responseJson.optString("memo"), getString(R.string.Ensure), 
            			null, null, null, null, null);
			}
		}
	}
}
