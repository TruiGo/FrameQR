package com.alipay.android.ui.card;

import org.json.JSONArray;
import org.json.JSONObject;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.DisplayMetrics;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewStub;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.alipay.android.appHall.Helper;
import com.alipay.android.appHall.common.Defines;
import com.alipay.android.biz.CardManagerBiz;
import com.alipay.android.biz.CommonRespHandler;
import com.alipay.android.biz.TransferBiz;
import com.alipay.android.bizapp.CCR.CCRBankCardInfo;
import com.alipay.android.client.AlipayApplication;
import com.alipay.android.client.constant.Constant;
import com.alipay.android.client.util.AlipayInputErrorCheck;
import com.alipay.android.client.util.Utilz;
import com.alipay.android.comon.component.EditTextWithButton;
import com.alipay.android.comon.component.HtmlActivity;
import com.alipay.android.comon.component.PatterAlertDialog;
import com.alipay.android.comon.component.TimerButton;
import com.alipay.android.log.Constants;
import com.alipay.android.ui.bean.BankCardInfo;
import com.alipay.android.ui.framework.BaseViewController;
import com.alipay.android.util.JsonConvert;
import com.eg.android.AlipayGphone.R;

public class AddCardViewController extends BaseViewController {
	private String mCardNoType;
	@Override
	protected void onCreate() {
		super.onCreate();
		mView = LayoutInflater.from(getRootController()).inflate(R.layout.alipay_newexpresscard, null, false);
		addView(mView, null);

		TextView title = (TextView)findViewById(R.id.title_text);
		title.setText(R.string.express_open);

		if (null == params) {
			loadStep(STEP_ONE, true);
		} else {
   			BankCardInfo cardInfo = (BankCardInfo)params;
   			mBankShortName = cardInfo.getBankMark();
   			mCardType = cardInfo.getCardType();
   			if (Constant.CARDTYPE_CC.equals(mCardType)) {
   				mCardNoType = Constant.CARDNOTYPE_CREDITCARDINDEXKEY;
   				mIsCreditCard = true;
   			} else {
   				mCardNoType = Constant.CARDNOTYPE_DEBITCARDNO;
   			}
   			
   			mCardNo = cardInfo.getCardId();
   			mCardNoSubmit = cardInfo.getCardTailNumber();;
   			
			loadStep(STEP_TWO, true);
			
			requestExpressSignForm();
		}
		

	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
        	return back(true);
        }
        
		return super.onKeyDown(keyCode, event);
	}

	private boolean back(boolean keyBack) {
		if (STEP_THREE == mCurStep) {
			//埋点
			doSimpleLog(Constants.APPID_signBankCard,
					Constants.BehaviourID.CLICKED,
					Constants.VIEWID_SignBankCardHome, 
					Constants.VIEWID_SmsConfirmView, 
					Constants.SEEDID_BACKICON);
			
			if (null != mSmsResendButton) {
				mSmsResendButton.stopTicking();
			}
			
			loadStep(STEP_TWO, false);
			return true;
		} else if (STEP_TWO == mCurStep) {
			//埋点
			doSimpleLog(Constants.APPID_signBankCard,
					Constants.BehaviourID.CLICKED,
					Constants.VIEWID_NoneView, 
					Constants.VIEWID_SignBankCardHome, 
					Constants.SEEDID_BACKICON);
			if (null == params) {
				loadStep(STEP_ONE, false);
			} else {
				pop();
			}
			
			return true;
		}else if (STEP_ONE == mCurStep && null == params) {
			if (keyBack) {
				//埋点
				doSimpleLog(Constants.APPID_signBankCard,
						Constants.BehaviourID.CLICKED,
						Constants.VIEWID_BankCardList, 
						Constants.VIEWID_InputCardView, 
						Constants.SEEDID_BACKICON);
			}

			pop();
			return true;
		}
		
		return false;
	}
	
	private int mCurStep = 0;
	private static final int STEP_ONE = 0;
	private static final int STEP_TWO = 1;
	private static final int STEP_THREE = 2;
	View mStepOneLayout;
	View mStepTwoLayout;
	View mStepThreeLayout;
	private void loadStep(int stepIndex, boolean forwardIntent) {
		mCurStep = stepIndex;
		
		switch (stepIndex) {
		case STEP_ONE:
			if (null == mStepOneLayout) {
				mStepOneLayout = ((ViewStub)findViewById(R.id.stepOne)).inflate();
			}
			
			break;
		case STEP_TWO:
			if (null == mStepTwoLayout) {
				mStepTwoLayout = ((ViewStub)findViewById(R.id.stepTwo)).inflate();
			}

			break;
		case STEP_THREE:
			if (null == mStepThreeLayout) {
				mStepThreeLayout = ((ViewStub)findViewById(R.id.stepThree)).inflate();
			}
			break;
		default:
			break;
		}
		
		loadVariables(stepIndex, forwardIntent);
		
		if (null != mStepOneLayout) {
			if (0 != stepIndex) {
				mStepOneLayout.setVisibility(View.GONE);
			} else {
				mStepOneLayout.setVisibility(View.VISIBLE);
			}
		}
		
		if (null != mStepTwoLayout) {
			if (1 != stepIndex) {
				mStepTwoLayout.setVisibility(View.GONE);
			} else {
				mStepTwoLayout.setVisibility(View.VISIBLE);
			}
		}

		if (null != mStepThreeLayout) {
			if (2 != stepIndex) {
				mStepThreeLayout.setVisibility(View.GONE);
			} else {
				mStepThreeLayout.setVisibility(View.VISIBLE);
			}
		}

	}
	
	private void loadVariables(int stepIndex, boolean forwardIntent) {
		switch (stepIndex) {
		case STEP_ONE:
			loadStepOneVariables(forwardIntent);
			break;
		case STEP_TWO:
			loadStepTwoVariables(forwardIntent);
			break;
		case STEP_THREE:
			loadStepThreeVariables(forwardIntent);
			break;
		default:
			break;
		}
	}

	private EditTextWithButton mCardNoEdit;
	private String mCardNo; //请求cachekey, 如果是借记卡则为卡号
	private Button mNextBtn;
	private boolean mTextFormatAlready = false;

	int mOldTextLen=0;
	int mOldSelection=0;
	String mOldString="";

	private void loadStepOneVariables(boolean forwardIntent) {

		if (null == mCardNoEdit) {
			mCardNoEdit = (EditTextWithButton) findViewById(R.id.cardNoEditText);
			mCardNoEdit.setOnFocusChangeListener(new View.OnFocusChangeListener() {
				
				@Override
				public void onFocusChange(View v, boolean hasFocus) {
					if (hasFocus) {
						Utilz.showInputMethod(v, false);
					}
				}
			});
			mCardNoEdit.requestFocus();
			mCardNoEdit.setOnEditorActionListener(new TextView.OnEditorActionListener() {
				
				@Override
				public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
					if (mCardNoEdit.getText().length() > 15) {
						Helper.hideInput(getRootController(), mCardNoEdit);
						mNextBtn.performClick();
						return true;
					}
					return false;
				}
			});
			
			mCardNoEdit.getEtContent().addTextChangedListener(new TextWatcher(){

				@Override
				public void afterTextChanged(Editable s) {
					if (s.length() == 0) {  
						mCardNoEdit.setButtonVisiable(false);
		            } else {  
		            	mCardNoEdit.setButtonVisiable(true);  
		            }
					
				}

				@Override
				public void beforeTextChanged(CharSequence s, int start, int count,
						int after) {
					// TODO Auto-generated method stub
					
				}

				@Override
				public void onTextChanged(CharSequence s, int start, int before,
						int count) {
					int length = s.length();

					if (0 == length % 5 && length > 0) {
						if (0 == count) {
							mCardNoEdit.setText(s.toString().subSequence(0, length - 1));
							mCardNoEdit.getEtContent().setSelection(length - 1);
						} else {
							mCardNoEdit.setText(new StringBuilder(s).insert(length - 1, ' '));
							mCardNoEdit.getEtContent().setSelection(length + 1);
						}

					}

					enableNextBtn(length > 0);
				}
	    	});
		}

		if (null == mNextBtn) {
	    	mNextBtn = (Button) findViewById(R.id.next);
	    	mNextBtn.setOnClickListener(new View.OnClickListener(){

				@Override
				public void onClick(View v) {
					//埋点
					doSimpleLog(Constants.APPID_signBankCard,
							Constants.BehaviourID.SUBMITED,
							Constants.VIEWID_SignBankCardHome, 
							Constants.VIEWID_InputCardView, 
							Constants.Seed_NextButton);
					
					Helper.hideInput(mNextBtn.getContext(), mNextBtn);
					mCardNoSubmit = mCardNoEdit.getText().replaceAll(" ", "");
					if (checkCarNo(mCardNoSubmit)) {
						CheckCardBin();
					}
				}
	    		
	    	});
		}

	}
	
	private String mCardNoSubmit;
	private boolean checkCarNo(String cardNo) {
    	int tResult = 0;
    	tResult = AlipayInputErrorCheck.CheckCardNO(cardNo);
		if (tResult != AlipayInputErrorCheck.NO_ERROR) {
            // check error.
            String warningMsg;

            if (tResult == AlipayInputErrorCheck.ERROR_INVALID_FORMAT){
				 warningMsg = getString(R.string.WarningInvalidBankId);
			 }else if (tResult == AlipayInputErrorCheck.ERROR_NULL_INPUT){
				 warningMsg = getString(R.string.WarningBankIdEmpty);
			 }else{
				 warningMsg = "UNKNOWN_ERROR TYPE = "+ tResult;
			 }

            if (!warningMsg.equals("")) {
            	
   			 showDialog(R.drawable.infoicon, 
				        getString(R.string.WarngingString),warningMsg, 
				        getString(R.string.Ensure), null, null, null, null, null);
            	
//            	context.showDialog(R.drawable.infoicon, context.getResources()
//                    .getString(R.string.WarngingString), warningMsg,
//                    context.getResources().getString(R.string.Ensure), null, null, null, null, null);
            }	 
            return false;
        }	
		
		return true;
	}
	
	private void enableNextBtn(boolean enable){
		mNextBtn.setEnabled(enable);
	}
	
	private boolean mIsCertified = false;
	private boolean mShowCVV2 = false;
	private boolean mCheckCertNo = false;
	private boolean mExpiredDate = false;
	private String mCertNo;
	private String mApplicantName;
	private String mApplicantNameInput;
	private String mMobileNo;
	private String mCVV2;
	private String mValiYear;
	private String mValiMonth;
	private Button mSignButton;
	private View mCertifiedInfoLayout;
	private View mNoCertifiedInfoLayout;
	private View mCCInfoLayout;
	private View mCCDateInfoLayout;
	
	public static final String BANK_ICON_PARTH = "bank_mark/icon";
	public static final String BANK_NAME_PARTH = "bank_mark/name";
	private TextView mCardNoText;
	private void loadStepTwoVariables(boolean forwardIntent) {
		ImageView bankLogoView = (ImageView)findViewById(R.id.bankIcon);
		bankLogoView.setImageBitmap(Utilz.getImage(getRootController(), BANK_ICON_PARTH, 
				mBankShortName));
		ImageView bankNameView = (ImageView)findViewById(R.id.bankName);
		bankNameView.setImageBitmap(Utilz.getImage(getRootController(), BANK_NAME_PARTH, 
				mBankShortName));
		
		if (null == mCardNoText) {
			mCardNoText = (TextView)findViewById(R.id.cardNoText);
			DisplayMetrics dmDisplayMetrics = getRootController().getResources().getDisplayMetrics();
	        if (320 > dmDisplayMetrics.densityDpi || 728 == dmDisplayMetrics.heightPixels) {
	        	mCardNoText.setTextSize(mCardNoText.getTextSize() / 2);
	        } 
	        //meizu
	        else if (960 == dmDisplayMetrics.heightPixels && 640 == dmDisplayMetrics.widthPixels) {
	        	mCardNoText.setTextSize(mCardNoText.getTextSize() / 3);
	        }
		}
		mCardNoText.setText("****   ****   ****   " + 
				mCardNoSubmit.substring(mCardNoSubmit.length() - 4));
		
		TextView cardType = (TextView)findViewById(R.id.cardTypeText);
		cardType.setText(String.format(getString(R.string.cardType_dot), 
				getString(mIsCreditCard ? R.string.credit : R.string.debit)));
		
		if (null == mCCInfoLayout) {
			mCCInfoLayout = findViewById(R.id.ccInfoLayout);
		}
		mCCInfoLayout.setVisibility(mIsCreditCard ? View.VISIBLE: View.GONE);
		
		if (null == mCCDateInfoLayout) {
			mCCDateInfoLayout = findViewById(R.id.ccDateInfoLayout);
		}
		
		if (null == mSignButton) {
			mSignButton = (Button)findViewById(R.id.signButton);
			mSignButton.setOnClickListener(new View.OnClickListener() {
				
				@Override
				public void onClick(View v) {
					//埋点
					doSimpleLog(Constants.APPID_signBankCard,
							Constants.BehaviourID.SUBMITED,
							Constants.VIEWID_SmsConfirmView, 
							Constants.VIEWID_SignBankCardHome, 
							Constants.Seed_ConfirmButton);
					
					if (checkSignForm()) {
						verifyExpressCard();
					}
				}
			});
		}
		
		if (null == mMonthEditText) {
			mMonthEditText = (EditText)findViewById(R.id.monthEditText);
		}

		if (null == mYearEditText) {
			mYearEditText = (EditText)findViewById(R.id.yearEditText);
		}
		
		if (null == mCvv2EditText) {
			mCvv2EditText = (EditText)findViewById(R.id.cvv2EditText);
		}

		if (null == mApplicantIdEditText) {
			mApplicantIdEditText = (EditText)findViewById(R.id.applicantIdEditText);
		}

		if (null == mApplicantNameEditText) {
			mApplicantNameEditText = (EditText)findViewById(R.id.applicantNameEditText);
		}

		if (null == mMobileEditText) {
			mMobileEditText = (EditText)findViewById(R.id.mobileEditText);
		}

		if (forwardIntent) {
			mApplicantName = null;
			mMonthEditText.setText(null);
			mYearEditText.setText(null);
			mCvv2EditText.setText(null);
			mApplicantIdEditText.setText(null);
			mApplicantNameEditText.setText(null);
			mMobileEditText.setText(null);
		}
		
		mMonthEditText.addTextChangedListener(mTextWatcherShared);
		mYearEditText.addTextChangedListener(mTextWatcherShared);
		mCvv2EditText.addTextChangedListener(mTextWatcherShared);
		mApplicantIdEditText.addTextChangedListener(mTextWatcherShared);
		mApplicantNameEditText.addTextChangedListener(mTextWatcherShared);
		mMobileEditText.addTextChangedListener(mTextWatcherShared);
		
		if (null == mCheckBox) {
			mCheckBox = (CheckBox)findViewById(R.id.checkBox);
			mCheckBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {

				@Override
				public void onCheckedChanged(CompoundButton buttonView,
						boolean isChecked) {
					checkSignFormRawly();
				}
				
			});
		}
		mCheckBox.setChecked(true);
		
		if (null == mCheckBoxTextview) {
			mCheckBoxTextview = findViewById(R.id.checkBoxTextview);
			mCheckBoxTextview.setOnClickListener(new View.OnClickListener() {

				@Override
				public void onClick(View v) {

					final String bankTermUrl = Constant.getTermUrl(mBankShortName, mIsCreditCard);
					final String alipayTermUrl = Constant.getTermUrl(Constant.BANK_ALIPAY, mIsCreditCard);
					if (null != bankTermUrl) {
						final CharSequence[] items = {
								getString(R.string.express_sign_alipay_term), 
								getString(R.string.express_sign_bank_term)};

						AlertDialog.Builder builder = new AlertDialog.Builder(getRootController());
						builder.setTitle(R.string.express_sign_choose_term);
						builder.setItems(items, new DialogInterface.OnClickListener() {
						    public void onClick(DialogInterface dialog, int item) {
						    	goToUrl(0 == item ? alipayTermUrl : bankTermUrl);
						    }
						});
						
						AlertDialog alert = builder.create();
						alert.show();
						
					} else if (null != alipayTermUrl){
						goToUrl(alipayTermUrl);
					}

				}
				
			});
		}
		
	}
	
	private void goToUrl(String url) {
        Intent tIntent = new Intent(getRootController(), HtmlActivity.class);
        tIntent.putExtra(HtmlActivity.URL, url);
        tIntent.putExtra(HtmlActivity.TITLE, getString(R.string.ServiceInfo));
        startActivity(tIntent);
	}
	
	private void checkSignFormRawly() {
		int ret = Constant.SOURCE_CORRECT;
		if (mIsCreditCard) {
			ret = checkMonth(false) * checkYear(false) * checkCVV2(false);
		}

		ret *= (checkRealName(false) * checkID(false) * checkMobileNum(false) * checkAgreeCheckBox());
		
		mSignButton.setEnabled(Constant.SOURCE_NULL != ret);
	}
	
	private boolean checkSignForm() {
		boolean ret = true;
		if (mIsCreditCard) {
			ret = Constant.SOURCE_CORRECT == checkMonth(true) &&
					Constant.SOURCE_CORRECT == checkYear(true) &&
					Constant.SOURCE_CORRECT == checkCVV2(true);
		}

		if (ret) {
			ret = Constant.SOURCE_CORRECT == checkRealName(true) &&
					Constant.SOURCE_CORRECT == checkID(true) &&
					Constant.SOURCE_CORRECT == checkMobileNum(true) &&
					Constant.SOURCE_CORRECT == checkAgreeCheckBox();
		}
		
		return ret;
	}

	private void checkCertifiedInfo() {
//		if (null == mCertifiedInfoLayout) {
//			mCertifiedInfoLayout = findViewById(R.id.certifiedInfoLayout);
//		}
//		mCertifiedInfoLayout.setVisibility(mIsCertified ? View.VISIBLE : View.GONE);
//		
		if (null == mNoCertifiedInfoLayout) {
			mNoCertifiedInfoLayout = findViewById(R.id.noCertifiedInfoLayout);
		}
		mNoCertifiedInfoLayout.setVisibility(Utilz.isEmpty(mCertNo) && mCheckCertNo ? View.VISIBLE : View.GONE);
	}
	
	private String mSignId;
	private TextView mTopDescription;
	private EditText mSmsCodeEditText;
	private boolean mSmsResend = false;
	private TimerButton mSmsResendButton;
	private Button mSmsConfirmButton;
	private String mSmsCode;
	private void loadStepThreeVariables(boolean forwardIntent) {
		if (null == mTopDescription) {
			mTopDescription = (TextView)findViewById(R.id.topDescription);
		}
		String userPhoneTemp = mMobileNo.substring(0, 3) + "****" + mMobileNo.substring(7);
		mTopDescription.setText(String.format(getString(R.string.express_afterSmsSent), userPhoneTemp));
		
		if (null == mSmsCodeEditText) {
			mSmsCodeEditText = (EditText)findViewById(R.id.editText);
			mSmsCodeEditText.addTextChangedListener(new TextWatcher() {
				
				@Override
				public void onTextChanged(CharSequence s, int start, int before, int count) {
				}
				
				@Override
				public void beforeTextChanged(CharSequence s, int start, int count,
						int after) {
					// TODO Auto-generated method stub
					
				}
				
				@Override
				public void afterTextChanged(Editable s) {
					mSmsConfirmButton.setEnabled(s.length() > 0);
				}
			});
		} else {
			mSmsCodeEditText.setText(null);
		}

		if (null == mSmsResendButton) {
			mSmsResendButton = (TimerButton)findViewById(R.id.resendButton);
			mSmsResendButton.setOnClickListener(new View.OnClickListener() {
				
				@Override
				public void onClick(View v) {
					mSmsResend = true;
					mSmsCode = null;
					mSmsResendButton.setTickCount(60).startTicking();

					signExpressCard();
				}
			});
		}
		mSmsResendButton.setFormatText(getString(R.string.express_resend_afterSmsSent))
		.setTextEnabled(getString(R.string.express_resend_enabled))
		.setTickCount(60).startTicking();
		
		if (View.GONE == mSmsResendButton.getVisibility()) {
			mSmsResendButton.setVisibility(View.VISIBLE);
		}
		
		if (null == mSmsConfirmButton) {
			mSmsConfirmButton = (Button)findViewById(R.id.confirmButton);
			mSmsConfirmButton.setOnClickListener(new View.OnClickListener() {
				
				@Override
				public void onClick(View v) {
					//埋点
					doSimpleLog(Constants.APPID_signBankCard,
							Constants.BehaviourID.SUBMITED,
							Constants.VIEWID_SignResultView, 
							Constants.VIEWID_SmsConfirmView, 
							Constants.Seed_ConfirmButton);
					
					mSmsCode = mSmsCodeEditText.getText().toString();
					
					Helper.hideInput(getRootController(), mSmsCodeEditText);
					signExpressCard();
				}
			});
		}
	}

	
	/**
	 * 异步任务
	 */
	@Override
	protected Object doInBackground(String bizType, String... paramsList) {
		if(bizType.equalsIgnoreCase(CardManagerBiz.BIZTAG_CHECKCARDBIN)){
			
			return new CardManagerBiz().checkCardBin(mCardNoSubmit);
       	} else if (bizType.equalsIgnoreCase(CardManagerBiz.BIZTAG_GETSUPPORTEXPRESSBANKLIST)) {
       		
       		return new CardManagerBiz().GetExpressBankParamsForSign(mBankShortName, mCardType);
       		
       	} else if (bizType.equalsIgnoreCase(CardManagerBiz.BIZTAG_VERIFYEXPRESSCARD)) {
    		return new CardManagerBiz().verifyExpressCard(mBankShortName, mCardType, mCardNo, mCardNoType,
    				mIsCreditCard && mExpiredDate ? mValiYear + "-" + mValiMonth: null, mCVV2, Utilz.isEmpty(mApplicantName) ? 
    						mApplicantNameInput : mApplicantName, 
    						mMobileNo, mCertNo, "身份证");
       	} else if (bizType.equalsIgnoreCase(CardManagerBiz.BIZTAG_SIGNEXPRESSCARD)) {
       		return new CardManagerBiz().signExpressCard(mSignId, mSmsCode, mSmsResend ? "true" : "false");
       	}
		
		return super.doInBackground(bizType, paramsList);
	}
	
	private boolean mRequestExpressSignForm = true;
	@Override
	protected void onPostExecute(String bizType, Object result) {
		if (null == result) {
			//检查卡bin联网失败
			if(bizType.equalsIgnoreCase(CardManagerBiz.BIZTAG_CHECKCARDBIN)){
				alert(getString(R.string.CheckNetwork));
			}
			return ;
		}
		
		JSONObject responseJson = JsonConvert.convertString2Json((String)result);
		String memoString = responseJson.optString(Constant.RPF_MEMO);
		if (0 == Utilz.parseInt(responseJson.optString(Constant.RPF_RESULT_STATUS), -1)) {
			memoString = getString(R.string.CheckNetwork);
		}
		
		if(bizType.equalsIgnoreCase(CardManagerBiz.BIZTAG_CHECKCARDBIN)){
			String errCodeString = responseJson.optString("ERRCODE");
			if ("".equals(errCodeString)) {
				mCardType = responseJson.optString("CARDTYPE");
				if (Constant.CARDTYPE_CC.equals(mCardType) ||
						Constant.CARDTYPE_SCC.equals(mCardType)) {
					mCardNo = responseJson.optString("UUID");
					mIsCreditCard = true;
					mCardType = Constant.CARDTYPE_CC;
					mCardNoType = Constant.CARDNOTYPE_CREDITCARDCACHEKEY;
				}
				else {
					mCardNo = mCardNoEdit.getText().replaceAll(" ", "");
					mIsCreditCard = false;
					mCardType = Constant.CARDTYPE_DC;
					mCardNoType = Constant.CARDNOTYPE_DEBITCARDNO;
				}

				mBankShortName = responseJson.optString("INSTID");
				
				if (mRequestExpressSignForm) {
					requestExpressSignForm();
				}
				mRequestExpressSignForm = true;
			}
			else {
				
				String error = getString(R.string.ccdc_err_CARDCHECKFAILED);
				//处理异常情况
				if ("CARD_NO_BLANK".equals(errCodeString)) {
					//卡号为空
					error = getString(R.string.ccdc_err_CARD_NO_BLANK);
				}
				else if ("SYS_ERROR".equals(errCodeString)) {
					//系统异常
					error = getString(R.string.ccdc_err_SYS_ERROR);
				}
				else if ("CARD_BIN_NOT_MATCH".equals(errCodeString)) {
					//卡bin不匹配
					error = getString(R.string.ccdc_err_CARD_NOT_SUPPORT);
				}
				else if ("SERVICE_ACCESS_FAILURE".equals(errCodeString)) {
					//服务端访问失败
					error = getString(R.string.ccdc_err_SERVICE_ACCESS_FAILURE);
				}
				else if ("AREA_GROUP_NOT_EXIST".equals(errCodeString)) {
					//地区匹配失败，不存在该卡BIN的地区分组信息
					error = getString(R.string.ccdc_err_AREA_GROUP_NOT_EXIST);
				}
				else if ("AREA_EXT_NOT_EXIST".equals(errCodeString)) {
					//地区匹配失败，不存在该卡BIN的地区扩展信息
					error = getString(R.string.ccdc_err_AREA_EXT_NOT_EXIST);
				}
				else if ("AREA_MAPPING_NOT_EXIST".equals(errCodeString)) {
					//地区匹配失败，该卡BIN分组信息存在，但未找到该号段的映射关系
					error = getString(R.string.ccdc_err_AREA_MAPPING_NOT_EXIST);
				}

            	alert(error);
			}

       	}else if (bizType.equalsIgnoreCase(CardManagerBiz.BIZTAG_GETSUPPORTEXPRESSBANKLIST)) {
       		if (Constant.RESULT_OK != responseJson.optInt(Defines.resultStatus)) {
       			
       			showDialog(R.drawable.infoicon, getString(R.string.WarngingString),
       					memoString, getString(R.string.Ensure), new DialogInterface.OnClickListener() {
							
							@Override
							public void onClick(DialogInterface dialog, int which) {
								if (null != params) {
									back(false);
								}
							}
						}, null, null,
       					null, null);
       		} else {
				//用户手动填写卡号才会执行此步骤
				if (null == params) {
					loadStep(STEP_TWO, true);
				}
				
				mShowCVV2 = false;
				mCheckCertNo = false;
				mExpiredDate = false;
				try {
					JSONArray checkItemList = responseJson.optJSONArray("supportBankList")
							.optJSONObject(0)
							.optJSONArray("cardList")
					        .optJSONObject(0)
					        .optJSONArray("checkItemList");
					
					String valueTemp = null;
					for (int i = 0; i < checkItemList.length(); i++) {
						valueTemp = checkItemList.optString(i);
						if ("mobile".equals(valueTemp)) {
							
						} else if ("certType".equals(valueTemp)) {
							
						} else if ("certNo".equals(valueTemp)) {
							mCheckCertNo = true;
						} else if ("cvv2".equals(valueTemp)) {
							mShowCVV2 = true;
						} else if ("expiredDate".equals(valueTemp)) {
							mExpiredDate = true;
						} 
						
					}

				} catch (NullPointerException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				String isCertified = responseJson.optString("isCertified");
				mIsCertified = false;
				mCertNo = null;
				if ("true".equalsIgnoreCase(isCertified)) {
					mIsCertified = true;
					mCertNo = responseJson.optString("certNo");

					TextView idText = (TextView)findViewById(R.id.idText);
					idText.setText(String.format(getString(R.string.IDNO_dot), responseJson.optString("showCertNo")));
					idText.setVisibility(View.VISIBLE);
				}
				
				checkCertifiedInfo();
				
				if (mIsCreditCard) {
					mCvv2EditText.setVisibility(mShowCVV2 ? View.VISIBLE : View.GONE);
					
					if (null != mCCDateInfoLayout) {
						mCCDateInfoLayout.setVisibility(mExpiredDate ? View.VISIBLE : View.GONE);
					}
				}
				
				mApplicantName = responseJson.optString("userName");
				if (!Utilz.isEmpty(mApplicantName)) {
					TextView realName = (TextView)findViewById(R.id.realNameText);
					realName.setVisibility(View.VISIBLE);
					realName.setText(String.format(getString(R.string.realname_dot), 
							Utilz.getSecuredRealName(mApplicantName)));
				} else {
					TextView realNameText = (TextView)findViewById(R.id.realNameText);
					realNameText.setVisibility(View.GONE);

					mApplicantNameEditText.setVisibility(View.VISIBLE);
				}
				
				//这里已经确认不自动填写绑定手机号码
//				if (responseJson.has("bindMobile")) {
//					if (null != mMobileEditText) {
//						mMobileEditText.setText(responseJson.optString("bindMobile"));
//					}
//				}
       		}
			
		} else if (bizType.equalsIgnoreCase(CardManagerBiz.BIZTAG_VERIFYEXPRESSCARD)) {
   			if (Constant.CARDNOTYPE_CREDITCARDCACHEKEY.equals(mCardNoType)) {
   				//防止信用卡cache失效的情况
   				String cardNo = responseJson.optString(Constant.CARDNO);
   				if ("".equals(mCardNo)) {
   					mRequestExpressSignForm = false;
   					CheckCardBin();
   				} else {
   					mCardNo = cardNo;
   					mCardNoType = Constant.CARDNOTYPE_CREDITCARDINDEXKEY;
   				}
   			}
   			
       		if (Constant.RESULT_OK != responseJson.optInt(Defines.resultStatus)) {
       			
       			alert(memoString);

       		} else {
    			mSignId = responseJson.optString("signId");
    			loadStep(STEP_THREE, true);
       		}

		}
       	else {
       		boolean resend = mSmsResend;
       		mSmsResend = false;

       		//处理高风险签约，这里暂时只能通过是否含有“风险”文字来做判断
       		if (!resend && memoString.contains("风险")) {
       			showDialog(R.drawable.infoicon, getString(R.string.WarngingString),
       					memoString, getString(R.string.Ensure), new DialogInterface.OnClickListener() {
    						
    						@Override
    						public void onClick(DialogInterface dialog, int which) {
    							pop();
    						}
    					}, null, null,
       					null, null);
       			
       			return ;
       		}

	    	if(!CommonRespHandler.processSpecStatu(getRootController(), responseJson)){
				return;
			}
	    	
			if(CommonRespHandler.filterBizResponse(getRootController(), responseJson)){
				if (bizType.equalsIgnoreCase(CardManagerBiz.BIZTAG_SIGNEXPRESSCARD)) {
					//include resend verifycode
					if (!resend) {
						//提示签约成功						
						new PatterAlertDialog(getRootController()).show(
								memoString, R.drawable.pattern_success, 
								new PatterAlertDialog.OnDismissListener() {

							@Override
							public void onDismiss() {
                        		AlipayApplication application = (AlipayApplication) getRootController()
                        				.getApplicationContext();
                        		application.setBankListRefresh(true);
                        		
                        		//刷新卡数量
                        		getRootController().getUserData().resetStatus();
                        		
                            	pop(null == params ? 1 : 2);
							}
							
						});
					}
				}
			}
		}
       	
		super.onPostExecute(bizType, result);
	}

	BizAsyncTask mCheckCardBinTask = null;
	private void CheckCardBin() {
		if (null != mCheckCardBinTask && AsyncTask.Status.FINISHED != mCheckCardBinTask.getStatus()) {
			mCheckCardBinTask.cancel(true);
		}
		
		mCheckCardBinTask = new BizAsyncTask(CardManagerBiz.BIZTAG_CHECKCARDBIN, true);
		mCheckCardBinTask.execute();
	}
	
	BizAsyncTask mRequestSignFormTask = null;
	String mBankShortName;
	String mCardType;
	boolean mIsCreditCard = false;
	private void requestExpressSignForm() {
		if (null != mRequestSignFormTask && AsyncTask.Status.FINISHED != mRequestSignFormTask.getStatus()) {
			mRequestSignFormTask.cancel(true);
		}
		
		mRequestSignFormTask = new BizAsyncTask(CardManagerBiz.BIZTAG_GETSUPPORTEXPRESSBANKLIST, true);
		mRequestSignFormTask.execute();
	}
	
	BizAsyncTask mVerifyExpressCardTask = null;
	private void verifyExpressCard() {
		if (null != mVerifyExpressCardTask && AsyncTask.Status.FINISHED != mVerifyExpressCardTask.getStatus()) {
			mVerifyExpressCardTask.cancel(true);
		}
		
		mVerifyExpressCardTask = new BizAsyncTask(CardManagerBiz.BIZTAG_VERIFYEXPRESSCARD, true);
		mVerifyExpressCardTask.execute();
	}
	
	BizAsyncTask mSignExpressCardTask = null;
	private void signExpressCard() {
		if (null != mSignExpressCardTask && AsyncTask.Status.FINISHED != mSignExpressCardTask.getStatus()) {
			mSignExpressCardTask.cancel(true);
		}
		
		mSignExpressCardTask = new BizAsyncTask(CardManagerBiz.BIZTAG_SIGNEXPRESSCARD, true);
		mSignExpressCardTask.execute();
	}

	private EditText mApplicantNameEditText;
	private int checkRealName(boolean alertError) {
		if (!Utilz.isEmpty(mApplicantName)) {
			return Constant.SOURCE_CORRECT;
		}

		mApplicantNameInput = mApplicantNameEditText.getText().toString();

		String warningMsg = null;
		if (Utilz.isEmpty(mApplicantNameInput)) {
			warningMsg = getString(R.string.express_sign_err_nameblank);
		}

		int tResult = AlipayInputErrorCheck.CheckRealName(mApplicantNameInput);
		if (alertError && tResult != AlipayInputErrorCheck.NO_ERROR) {
			// check error.
			if (tResult == AlipayInputErrorCheck.ERROR_INVALID_FORMAT) {
				warningMsg = getString(R.string.WarningInvalidRealName);
			} else if (tResult == AlipayInputErrorCheck.ERROR_NULL_INPUT) {
				warningMsg = getString(R.string.WarningRealNameEmpty);
			} else if (tResult == AlipayInputErrorCheck.ERROR_OUTOF_RANGE) {
				warningMsg = getString(R.string.WarningRealNameOutRange);
			} else {
				warningMsg = "UNKNOWN_ERROR TYPE = " + tResult;
			}

			showDialog(R.drawable.infoicon, getString(R.string.WarngingString),
					warningMsg, getString(R.string.Ensure), null, null, null,
					null, null);
		}

		return Utilz.translateErrCode(tResult);
	}
	
	private EditText mMonthEditText;
	private int checkMonth(boolean alertError) {
		if (!mExpiredDate) {
			return Constant.SOURCE_CORRECT;
		}
		
		mValiMonth = mMonthEditText.getText().toString();
		if (!alertError) {
			return mValiMonth.length() > 0 ? Constant.SOURCE_CORRECT : Constant.SOURCE_NULL;
		}
		
		int ret = Utilz.checkNumber(mValiMonth, Constant.LENGTH_TWO);
		if (Constant.SOURCE_CORRECT != ret) {
			alert(getString(R.string.cc_err_invaliddate));
		}
		
		return ret;
	}
	
	private EditText mYearEditText;
	private int checkYear(boolean alertError) {
		if (!mExpiredDate) {
			return Constant.SOURCE_CORRECT;
		}
		
		mValiYear = mYearEditText.getText().toString();
		if (!alertError) {
			return mValiYear.length() > 0 ? Constant.SOURCE_CORRECT : Constant.SOURCE_NULL;
		}
		
		int ret = Utilz.checkNumber(mValiYear, Constant.LENGTH_TWO);
		if (Constant.SOURCE_CORRECT != ret) {
			alert(getString(R.string.cc_err_invaliddate));
		}
		
		return ret;
	}
	
	EditText mCvv2EditText;
	private int checkCVV2(boolean alertError) {
		if (View.GONE == mCvv2EditText.getVisibility()) {
			return Constant.SOURCE_CORRECT;
		}
		
		mCVV2 = mCvv2EditText.getText().toString();
		if (!alertError) {
			return mCVV2.length() > 0 ? Constant.SOURCE_CORRECT : Constant.SOURCE_NULL;
		}
		
		int ret = Utilz.checkNumber(mCVV2, Constant.LENGTH_THREE);
		if (Constant.SOURCE_CORRECT != ret) {
			alert(getString(R.string.cc_err_cvv2));
		}
		
		return ret;
	}
	
	EditText mApplicantIdEditText;
	private int checkID(boolean alertError) {
		//不需要输入身份证号
		if (null != mNoCertifiedInfoLayout && View.GONE == mNoCertifiedInfoLayout.getVisibility()) {
			return Constant.SOURCE_CORRECT;
		}
		
		if (!mIsCertified) {
			mCertNo = mApplicantIdEditText.getText().toString();
		}
		
		return Utilz.checkId(mCertNo, this.getRootController(), alertError);
	}
	
	private EditText mMobileEditText;
	private int checkMobileNum(boolean alertError) {
		if (null == mMobileEditText) {
			mMobileEditText = (EditText)findViewById(R.id.mobileEditText);
		}
		mMobileNo = mMobileEditText.getText().toString();
		
		return Utilz.checkMobileNo(mMobileNo, this.getRootController(), alertError);
	}
	
	CheckBox mCheckBox;
	View mCheckBoxTextview;
	private int checkAgreeCheckBox() {

		int ret = Constant.SOURCE_CORRECT;
		if (!mCheckBox.isChecked()) {
			//alert
			ret = Constant.SOURCE_NULL;
		}

		return ret;
	}
	
	private TextWatcher mTextWatcherShared = new TextWatcher(){

		@Override
		public void afterTextChanged(Editable s) {
			checkSignFormRawly();
		}

		@Override
		public void beforeTextChanged(CharSequence s, int start, int count,
				int after) {
			
		}

		@Override
		public void onTextChanged(CharSequence s, int start, int before,
				int count) {
			
		}
	};
}
