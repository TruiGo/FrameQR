/**
 * 
 */
package com.alipay.android.client.baseFunction;

import java.util.ArrayList;
import java.util.HashMap;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.InputType;
import android.text.method.PasswordTransformationMethod;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.EditText;
import android.widget.TextView;

import com.alipay.android.appHall.bussiness.CallBackFilter;
import com.alipay.android.client.AlipayAccountManager;
import com.alipay.android.client.Login;
import com.alipay.android.client.MessageFilter;
import com.alipay.android.client.RootActivity;
import com.alipay.android.client.constant.AlipayHandlerMessageIDs;
import com.alipay.android.client.constant.Constant;
import com.alipay.android.client.util.AlipayInputErrorCheck;
import com.alipay.android.client.util.BaseHelper;
import com.alipay.android.client.util.DBHelper;
import com.alipay.android.client.util.LephoneConstant;
import com.alipay.android.common.data.UserInfo;
import com.alipay.android.comon.component.EditTextHasNullChecker;
import com.alipay.android.comon.component.EditTextWithButton;
import com.alipay.android.comon.component.ProgressDiv;
import com.alipay.android.net.RequestMaker;
import com.alipay.android.net.http.HttpRequestMaker;
import com.alipay.platform.core.Command;
import com.alipay.platform.view.ActivityMediator;
import com.alipay.platform.view.OnDataChangeListener;
import com.eg.android.AlipayGphone.R;

/**
 * @author chengkuang 重新设置密码
 */
public class AlipayModifyPassword extends RootActivity implements
		OnDataChangeListener {
	private ProgressDiv mProgress = null;

	public static final String GET_RSAKEY_ID = "10";
	private RequestMaker mRequestMaker;
	private CallBackFilter mBackFilter;

	private int mErrorType = MODIFY_START;
	private static final int MODIFY_FINISH = -3;
	private static final int MODIFY_START = -2;
	private static final int GET_RSA_KEY = -1;
	private MessageFilter mMessageFilter = new MessageFilter(this);
	EditTextHasNullChecker editTextHasNullChecker = new EditTextHasNullChecker();
	public static final int REQUEST_SMSCODE_OPERATE = 10;
	private Handler mHandler = new Handler() {
		public void handleMessage(Message msg) {
			// Responsor responsor = (Responsor)msg.obj;
			// JSONObject jsonResponse = responsor.obj;

			showResult(msg);
			switch (msg.what) {
			case AlipayHandlerMessageIDs.MODIFY_PASSWORD:
				if (mErrorType == MODIFY_FINISH) {
//					modifyFinish();
				}
				break;
			}
			super.handleMessage(msg);
		}
	};

	private TextView mTitle = null;

	private EditText mCurrentPasswordEditText = null;
	private EditText mNewPasswordEditText = null;
	// private EditText mConfirmNewPasswordEditText = null;

	private Button mAlipayModifyButton = null;

	private String mTitleInfo = null;
	private String mCurrentPW = null;
	private String mNewPW = null;
	// private String mConfirmPW = null;
	private CheckBox mCheck = null;
	private String passwordType = "";

	@Override
	public void onCreate(Bundle savedInstanceState) {
		mRequestMaker = new HttpRequestMaker(getApplicationContext(),
				R.raw.interfaces);
		mBackFilter = new CallBackFilter(this);

		super.onCreate(savedInstanceState);
		if (LephoneConstant.isLephone()) {
			getWindow().addFlags(LephoneConstant.FLAG_ROCKET_MENU_NOTIFY);
		}
		this.setContentView(R.layout.alipay_modifypassword);
		loadAllVariables();
	}

	private void loadAllVariables() {
		mTitleInfo = this.getIntent().getDataString();
		mTitle = (TextView) findViewById(R.id.title_text);
		mTitle.setText(mTitleInfo);

		passwordType = this.getIntent().getStringExtra(Constant.RQF_PASSWORDTYPE);
		mCurrentPasswordEditText = ((EditTextWithButton) findViewById(R.id.CurrentPasswordEditText))
				.getEtContent();
		mCurrentPasswordEditText.addTextChangedListener(editTextHasNullChecker);
		editTextHasNullChecker.addNeedCheckView(mCurrentPasswordEditText);

		mNewPasswordEditText = ((EditTextWithButton) findViewById(R.id.NewPasswordEditText))
				.getEtContent();
		mNewPasswordEditText.addTextChangedListener(editTextHasNullChecker);
		editTextHasNullChecker.addNeedCheckView(mNewPasswordEditText);

		mCurrentPasswordEditText.setInputType(InputType.TYPE_CLASS_TEXT
				| InputType.TYPE_TEXT_VARIATION_PASSWORD);
		mNewPasswordEditText.setInputType(InputType.TYPE_CLASS_TEXT
				| InputType.TYPE_TEXT_VARIATION_PASSWORD);

		// mConfirmNewPasswordEditText =
		// (EditText)findViewById(R.id.ConfirmNewPasswordEditText);
		mAlipayModifyButton = (Button) findViewById(R.id.AlipayModifyButton);
		editTextHasNullChecker.addNeedEnabledButton(mAlipayModifyButton);

		mAlipayModifyButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				mCurrentPW = mCurrentPasswordEditText.getText().toString().trim();
				mNewPW = mNewPasswordEditText.getText().toString().trim();
				// mConfirmPW=mConfirmNewPasswordEditText.getText().toString();
				if (!matchPassword(mCurrentPW)) {
					return;
				}
				if (!matchPassword(mNewPW)) {
					return;
				}
				/*
				 * if(!matchPassword(mConfirmPW)) { return; }
				 * if(!mNewPW.equals(mConfirmPW)) {
				 * myHelper.showDialog(AlipayModifyPassword.this,
				 * R.drawable.infoicon, getResources()
				 * .getString(R.string.WarngingString), getResources()
				 * .getString(R.string.PasswordWrongInfo),
				 * getResources().getString(R.string.Ensure), null, null, null,
				 * null, null); return; }
				 */
				doGetRSAPKey();

			}

		});

		mCheck = (CheckBox) findViewById(R.id.ModifyPasswordCheck);
		mCheck.setOnCheckedChangeListener(new OnCheckedChangeListener() {

			@Override
			public void onCheckedChanged(CompoundButton buttonView,
					boolean isChecked) {
				if (isChecked) {
					// mCurrentPasswordEditText.setTransformationMethod(null);
					// mNewPasswordEditText.setTransformationMethod(null);
					// mConfirmNewPasswordEditText.setTransformationMethod(null);
					BaseHelper.setDispayMode(mCurrentPasswordEditText, null);
					BaseHelper.setDispayMode(mNewPasswordEditText, null);
					// BaseHelper.setDispayMode(mConfirmNewPasswordEditText,null);
				} else {
					// mCurrentPasswordEditText.setTransformationMethod(new
					// PasswordTransformationMethod());
					// mNewPasswordEditText.setTransformationMethod(new
					// PasswordTransformationMethod());
					// mConfirmNewPasswordEditText.setTransformationMethod(new
					// PasswordTransformationMethod());
					BaseHelper.setDispayMode(mCurrentPasswordEditText,
							new PasswordTransformationMethod());
					BaseHelper.setDispayMode(mNewPasswordEditText,
							new PasswordTransformationMethod());
					// BaseHelper.setDispayMode(mConfirmNewPasswordEditText,new
					// PasswordTransformationMethod());
				}
			}

		});
	}

	private void doGetRSAPKey() {
		mErrorType = GET_RSA_KEY;
		ActivityMediator activityMediator = new ActivityMediator(this);
		ArrayList<String> params = new ArrayList<String>();
		String sessionId = getSessionId();
		params.add(sessionId);
		params.add(getConfigData().getClientId());
		activityMediator.sendCommand(GET_RSAKEY_ID, "clean", mRequestMaker,
				params);

		// myHelper.sendGetRSAKey(mHandler,
		// AlipayHandlerMessageIDs.START_PROCESS_GOT_DATA);
		if (mProgress == null) {
			mProgress = getDataHelper().showProgressDialogWithCancelButton(
					this, null, getResources().getString(R.string.PleaseWait),
					false, true, getDataHelper().cancelListener,
					getDataHelper().cancelBtnListener);
		}
	}

	private void doLoginOrPayPassword() {
		if (mTitleInfo.equals(getResources().getString(
				R.string.ModifyLoginPassword))) {
			getDataHelper().sendModifyPassword(mHandler,
					AlipayHandlerMessageIDs.MODIFY_PASSWORD, mNewPW,
					mCurrentPW, Constant.MODIFY_STYLE_LOGIN);
		} else {
			getDataHelper().sendModifyPassword(mHandler,
					AlipayHandlerMessageIDs.MODIFY_PASSWORD, mNewPW,
					mCurrentPW, Constant.MODIFY_STYLE_TRADE);
		}

	}

	/*
	 * check password
	 */
	private boolean matchPassword(String password) {
		int tResult = AlipayInputErrorCheck.checkLoginPassword(password);
		if (tResult != AlipayInputErrorCheck.NO_ERROR) {
			// check error.
			String warningMsg;
			if (tResult == AlipayInputErrorCheck.ERROR_INVALID_FORMAT) {
				warningMsg = getResources().getString(
						R.string.WarningInvalidLoginPassword);
			} else if (tResult == AlipayInputErrorCheck.ERROR_NULL_INPUT) {
				warningMsg = getResources().getString(R.string.NoEmptyPassword);
			} else {
				warningMsg = "UNKNOWN_ERROR TYPE = " + tResult;
			}
			BaseHelper.recordWarningMsg(this, warningMsg);

			getDataHelper().showDialog(this, R.drawable.infoicon,
					getResources().getString(R.string.WarngingString),
					warningMsg, getResources().getString(R.string.Ensure),
					null, null, null, null, null);
			return false;
		}
		return true;
	}

	private void showResult(Message msg) {
		boolean tResultOK = false;
		boolean needDismissProcessDialog = true;

		tResultOK = mMessageFilter.process(msg);

		if ((tResultOK) && (!getDataHelper().isCanceled())) {
			if ((tResultOK) && (!getDataHelper().isCanceled())) {
				if (mErrorType == GET_RSA_KEY) {
					// mErrorType = MODIFY_START;
					// needDismissProcessDialog = false;
					// doLoginOrPayPassword();
				} else if (mErrorType == MODIFY_START) {
					//跳转到短信校验码界面
					Intent smsCodeIntent = new Intent(AlipayModifyPassword.this, SendSMSCodeActivity.class);
					smsCodeIntent.putExtra(Constant.RQF_PASSWORDTYPE, passwordType);
					//需要传递新旧密码，以便重新获取短信时使用。
					smsCodeIntent.putExtra(Constant.RQF_NEWPASSWORD, mNewPW);
					smsCodeIntent.putExtra(Constant.RQF_OLDPASSWORD, mCurrentPW);
					
					startActivityForResult(smsCodeIntent, REQUEST_SMSCODE_OPERATE);
					mErrorType = MODIFY_FINISH;//这里需要修改。
				}
			}
		}

		if (needDismissProcessDialog) {
			closeProgress();
		}
	}

	private void modifyFinish() {
		String warningMsg = "";
		if (mTitleInfo.equals(this.getResources().getString(
				R.string.ModifyLoginPassword))) {
			warningMsg = getResources().getString(R.string.UseNewPasswordLogin);
		} else {
			warningMsg = getResources().getString(R.string.UseNewPasswordPay);
		}
		getDataHelper().showDialog(AlipayModifyPassword.this,
				R.drawable.infoicon,
				getResources().getString(R.string.ModifySuccess), warningMsg,
				getResources().getString(R.string.Ensure), btn, null, null,
				null, null);
	}

	DialogInterface.OnClickListener btn = new DialogInterface.OnClickListener() {
		@Override
		public void onClick(DialogInterface dialog, int which) {
			if (mTitleInfo.equals(getResources().getString(
					R.string.ModifyLoginPassword))) {
				AlipayModifyPassword.this.getConfigData().setSessionId(null);
				// APHttpClient.clearSessionCookie();
				Constant.GAS_JSON = null;
				Constant.WATER_JSON = null;
				Constant.ELECTRIC_JSON = null;
				Constant.COMMUN_JSON = null;

				if (AlipayModifyPassword.this.getUserData() != null) {
					BaseHelper.exitProcessOnClickListener
							.clearUserCache(AlipayModifyPassword.this);
					AlipayModifyPassword.this.logoutUser();
				}
				
				//执行和退出一样的流程
		        DBHelper db = new DBHelper(AlipayModifyPassword.this);
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

				Intent tIntent = new Intent(AlipayModifyPassword.this,
						Login.class);
				AlipayModifyPassword.this.startActivityForResult(tIntent, Constant.REQUEST_LOGIN_BACK);
				AlipayModifyPassword.this.setResult(AlipayAccountManager.MODIFY_PASSWORD_SUCCESS);
			}
				AlipayModifyPassword.this.finish();
			}
	};
	
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		switch (requestCode) {
		case Constant.REQUEST_PHONE_BINDDING:
			if (resultCode == RESULT_OK) {
//				getDataHelper().sendSMSCheckCode(mHandler, AlipayHandlerMessageIDs.SMSCHECKCODE, phone, code,
//		                style);
			}
			break;
		case REQUEST_SMSCODE_OPERATE:
			this.finish();
			break;
		}
	}

	@Override
	public boolean preCancel(Command command) {
		return false;
	}

	@Override
	public void onCancel(Command command) {

	}

	@Override
	public boolean preFail(Command command) {
		return false;
	}

	@Override
	public void onFail(Command command) {
		closeProgress();

		String content = command.getResponseMessage();
		BaseHelper.showDialog(AlipayModifyPassword.this,
				getString(R.string.Error), content, R.drawable.erroricon);
	}

	@SuppressWarnings("unchecked")
	@Override
	public void onComplete(Command command) {
		//
		HashMap<String, Object> hmResponse = (HashMap<String, Object>) command
				.getResponseData();

		if (!mBackFilter.processCommand(hmResponse)) {
			if (command.getmId().equals(GET_RSAKEY_ID)) {
				mErrorType = MODIFY_START;

				String string = (String) hmResponse.get(Constant.RPF_RSA_PK);
				if (string != null) {
					setPublicKey(string);
					
					// alipayApp.putInfoData(GlobalDataDefine.RSA_PK, string);
					// DataHelper.getInstance().mDefaultValueMap.put(Constant.RPF_RSA_PK,
					// (String) string);
				}

				string = (String) hmResponse.get(Constant.RPF_RSA_TS);
				if (string != null) {
					setTimeStamp(string);
					// alipayApp.putInfoData(GlobalDataDefine.RSA_TS, string);
					// DataHelper.getInstance().mDefaultValueMap.put(Constant.RPF_RSA_TS,
					// (String) string);
				}

				// Send command to modify password.
				doLoginOrPayPassword();
			}
		} else {
			closeProgress();
		}
	}

	@Override
	public void setRuleId(String ruleId) {
	}

	@Override
	public String getRuleId() {
		return null;
	}

	@Override
	public Context getContext() {
		return AlipayModifyPassword.this;
	}

	void closeProgress() {
		try {
			if (mProgress != null) {
				mProgress.dismiss();
				mProgress = null;
			}
		} catch (Exception e) {
			// e.printStackTrace();
		}
	}
}
