package com.alipay.android.ui.card;

import org.json.JSONObject;

import android.content.DialogInterface;
import android.os.AsyncTask;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewStub;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;

import com.alipay.android.appHall.Helper;
import com.alipay.android.appHall.common.Defines;
import com.alipay.android.biz.CardManagerBiz;
import com.alipay.android.biz.CheckPasswordBiz;
import com.alipay.android.biz.CommonRespHandler;
import com.alipay.android.biz.LoginBiz;
import com.alipay.android.client.AlipayAccountManager;
import com.alipay.android.client.AlipayApplication;
import com.alipay.android.client.Login;
import com.alipay.android.client.constant.Constant;
import com.alipay.android.client.util.DBHelper;
import com.alipay.android.client.util.Utilz;
import com.alipay.android.common.data.UserInfo;
import com.alipay.android.comon.component.EditTextWithButton;
import com.alipay.android.comon.component.PatterAlertDialog;
import com.alipay.android.security.RSA;
import com.alipay.android.ui.bean.BankCardInfo;
import com.alipay.android.ui.framework.BaseViewController;
import com.alipay.android.ui.framework.BaseViewController.BizAsyncTask;
import com.alipay.android.util.JsonConvert;
import com.alipay.android.util.SecureUtil;
import com.eg.android.AlipayGphone.R;

public class DeleteCardViewController extends BaseViewController {
	private EditText mPayPassEditText;
	private String mSignId;
	private String mCardType;
	private String mKeyString;
	private String mTimStampString;
	private String mCardNo;
	
	private Button mConfirmButton;
	private int mAction;
	private static final int ACTION_NONE = 0;
	private static final int ACTION_DELCIFWITHDRAW = 1;
	private static final int ACTION_DELCCR = 2;
	
	CheckBox mCheckBox;
	
	@Override
	protected void onCreate() {
		super.onCreate();
		mView = LayoutInflater.from(getRootController()).inflate(R.layout.alipay_express_delete, null, false);
		addView(mView, null);

		final BankCardInfo cardInfo = (BankCardInfo)params;
		mSignId = cardInfo.getCardId();
		mCardType = cardInfo.getCardType();
		mCardNo = cardInfo.getCardNo();
		
		TextView title = (TextView)findViewById(R.id.title_text);
		title.setText(R.string.express_delete_title);
		
		TextView topDescription = (TextView)findViewById(R.id.topDescription);
		topDescription.setText(R.string.express_delete_description);
		
		mPayPassEditText = (EditText)findViewById(R.id.editText);
		mPayPassEditText.setHint(getString(R.string.PaymentPassword));
		mPayPassEditText.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
		mPayPassEditText.addTextChangedListener(new TextWatcher() {
			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {
			}
			
			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {
			}
			
			@Override
			public void afterTextChanged(Editable inputedStr) {
				if (inputedStr.length() == 0) {
					mConfirmButton.setEnabled(false);
				} else {
					mConfirmButton.setEnabled(true);
				}
			}
		});
		
		mConfirmButton = (Button)findViewById(R.id.confirmButton);
		mConfirmButton.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO check password
				Helper.hideInput(mConfirmButton.getContext(), mConfirmButton);
				getRSAKey();
			}
		});
		mConfirmButton.setEnabled(false);

		mAction = ACTION_NONE;
		TextView checkBoxTextView = (TextView)findViewById(R.id.checkBoxTextview);
		//本期暂时不做信用卡删除
		/*if (Utilz.equalsAtBit(cardInfo.getBizType(), BankCardInfo.BIZ_EXPRESS_DELETEFROMCCFUNDLIST)) {
			checkBoxTextView.setText(R.string.express_agree_noccfund);
			mAction = ACTION_DELCCR;
		} else */if (Utilz.equalsAtBit(cardInfo.getBizType(), BankCardInfo.BIZ_EXPRESS_DELETETIXIAN)) {
			checkBoxTextView.setText(R.string.express_agree_nowidthdrawal);
			mAction = ACTION_DELCIFWITHDRAW;
		} else {
			View delPlusLayoutView = findViewById(R.id.delPlusLayout);
			delPlusLayoutView.setVisibility(View.GONE);
			
			//just in case
			//当delFromCCR或delFromCifWithdraw有一个为true，即需要在撤销签约后从其他列表附带进行删除时，此参数不能为空
			mCardNo = null;
		}
		
		if (ACTION_NONE != mAction) {
			mCheckBox = (CheckBox)findViewById(R.id.checkBox);
		}
	}

	private void getRSAKey() {
		new BizAsyncTask(LoginBiz.BIZTAG_GETRSAKEY, true).execute();
	}
	
	/**
	 * 异步任务
	 */
	@Override
	protected Object doInBackground(String bizType, String... paramsList) {	
		if(bizType.equalsIgnoreCase(LoginBiz.BIZTAG_GETRSAKEY)){
			return new LoginBiz().getRSAKey();
       	} else if (bizType.equalsIgnoreCase(CardManagerBiz.BIZTAG_CANCELSAVEDEXPRESSCARD)) {
       		return new CardManagerBiz().CancelSavedExpressCard(mSignId, mCardType, 
       				//RSA.encrypt(mPayPassEditText.getText().toString(), mKeyString),
       				SecureUtil.encrpt(mPayPassEditText.getText().toString(), mKeyString, mTimStampString),
       				mCardNo, 
       				ACTION_DELCCR == mAction && mCheckBox.isChecked() ? "true" : "false", 
       				ACTION_DELCIFWITHDRAW == mAction && mCheckBox.isChecked() ? "true" : "false");
       	}
		
		return super.doInBackground(bizType, paramsList);
	}
	
	
	@Override
	protected void onPostExecute(String bizType, Object result) {
		JSONObject responseJson = JsonConvert.convertString2Json((String)result);

    	if(!CommonRespHandler.processSpecStatu(getRootController(), responseJson)){
			return;
		}
		if(CommonRespHandler.filterBizResponse(getRootController(), responseJson)){
			if (bizType.equalsIgnoreCase(LoginBiz.BIZTAG_GETRSAKEY)) {
				mKeyString = responseJson.optString(Constant.RPF_RSA_PK);
				mTimStampString = responseJson.optString(Constant.RPF_RSA_TS);
				new BizAsyncTask(CardManagerBiz.BIZTAG_CANCELSAVEDEXPRESSCARD, true).execute();
			} else if (bizType.equalsIgnoreCase(CardManagerBiz.BIZTAG_CANCELSAVEDEXPRESSCARD)) {
				new PatterAlertDialog(getRootController()).show(
						responseJson.optString(
						Constant.RPF_MEMO), R.drawable.pattern_success, 
						new PatterAlertDialog.OnDismissListener() {

					@Override
					public void onDismiss() {
                		AlipayApplication application = (AlipayApplication) getRootController()
                				.getApplicationContext();
                		application.setBankListRefresh(true);
                		application.setSavedCCRBankRefresh(true);
                		//刷新卡数量
                		getRootController().getUserData().resetStatus();
                		
						pop(2);
					}
					
				});
			}
		}
	}
}
