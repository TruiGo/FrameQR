package com.alipay.android.client.baseFunction;

import java.util.HashMap;

import org.json.JSONObject;

import android.app.Activity;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.alipay.android.client.MessageFilter;
import com.alipay.android.client.RootActivity;
import com.alipay.android.client.constant.AlipayHandlerMessageIDs;
import com.alipay.android.client.constant.Constant;
import com.alipay.android.client.util.AlipayInputErrorCheck;
import com.alipay.android.client.util.Responsor;
import com.alipay.android.common.data.AlipayUserState;
import com.alipay.android.comon.component.ProgressDiv;
import com.alipay.android.log.AlipayLogAgent;
import com.alipay.android.log.Constants;
import com.alipay.android.log.StorageStateInfo;
import com.eg.android.AlipayGphone.R;

public class AlipayFeedBack extends RootActivity implements OnClickListener {

	private TextView mAlipayTitleItemName;
	private Button mSubmitFeedbackContentButton;
	private EditText mEditeInputTelNum;
	private EditText mUserFeedbackContent;
	private TextView hasnumTV;
	// 所需参数
	private String feedbackContent;
	private String mobileNo = "";
	private String userName = "";
	public static final String proposalType = "proposal";
	private String productID = "";
	private String productVersion = "";
	HashMap<String, String> mDataMap = new HashMap<String, String>();
	// 服务器响应的结果
	private int RESPONSE_RESULT_SUCCEED = 100;

	int num = 240;// 限制的最大字数
	protected ProgressDiv mProgress;// 进度条
	private MessageFilter mMessageFilter = null;
	private AlipayUserState curUserState = AlipayUserState.getInstance();
	private Handler mHandler = new Handler() {

		@Override
		public void handleMessage(Message msg) {
			boolean tResultOK = false;
	        Responsor responsor = (Responsor) msg.obj;
	        String memo = responsor.memo;
			tResultOK = mMessageFilter.process(msg);
			if (mProgress != null) {
				mProgress.dismiss();
			}
			switch (msg.what) {
			case AlipayHandlerMessageIDs.MENU_FEEDBACK_MSG:
				if (tResultOK) {
			        if("".equalsIgnoreCase(memo)){
			        	memo = getResources().getString(R.string.SubmitFeedbackContentSucceed);
			        }
				    getDataHelper().showDialog(AlipayFeedBack.this, R.drawable.infoicon, getResources().getString(R.string.WarngingString),
				    		memo, getResources().getString(R.string.Ensure),
							new DialogInterface.OnClickListener() {
								@Override
								public void onClick(DialogInterface dialog, int which) {
									AlipayFeedBack.this.finish();

								}
							}, null, null, null, null);
				} /*else {
					showDialog(AlipayFeedBack.this, getResources().getString(R.string.SubmitFeedbackContentFail));
				}
*/
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
		setContentView(R.layout.alipay_userfeedback);
		loadAllVariables();
	}

	private void loadAllVariables() {
		mEditeInputTelNum = (EditText) findViewById(R.id.InputTelNumber);
		mAlipayTitleItemName = (TextView) findViewById(R.id.title_text);
		mAlipayTitleItemName.setText(R.string.MenuUserFeedback);
		mUserFeedbackContent = (EditText) findViewById(R.id.UserFeedbackContent);
		mUserFeedbackContent.addTextChangedListener(mTextWatcher);
		mSubmitFeedbackContentButton = (Button) findViewById(R.id.SubmitFeedbackContentButton);
		mSubmitFeedbackContentButton.setOnClickListener(this);
		hasnumTV = (TextView) findViewById(R.id.ContentNumWarningText);
		hasnumTV.setText(0 + this.getResources().getString(R.string.FeedbackContentWarning));
		if (getUserData()!=null) {
			mEditeInputTelNum.setVisibility(View.GONE);
		}
		mMessageFilter = new MessageFilter(this);
	}

	@Override
	public void onClick(View v) {
		feedbackContent = mUserFeedbackContent.getText().toString().trim();
		productID = getConfigData().getProductId();
		productVersion = getConfigData().getProductVersion();
		
		String errorStr = "";
		if (getUserData()!=null) {
			mobileNo = getMobileNo();
			userName = curUserState.getUserAccount();
			if (feedbackContent == null || feedbackContent.equals("")) {
				showDialog(AlipayFeedBack.this, getResources().getString(R.string.NoEmptyFeedbackContent));
				errorStr = getResources().getString(R.string.NoEmptyFeedbackContent);
			}else {
				doSubmitFeedbackContent();
			}
		} else { // 未登陆的时候只有输入的手机号，没有用户名
			mobileNo = mEditeInputTelNum.getText().toString().trim();
			userName = "";
			if (feedbackContent == null || feedbackContent.equals("")) {
				showDialog(AlipayFeedBack.this, getResources().getString(R.string.NoEmptyFeedbackContent));
				errorStr = getResources().getString(R.string.NoEmptyFeedbackContent);
			}else if (mobileNo == null || mobileNo.equals("")) {
				showDialog(AlipayFeedBack.this, getResources().getString(R.string.NoEmptyFeedbackTelNum));
				errorStr = getResources().getString(R.string.NoEmptyFeedbackTelNum);
			}else{
				int matchesValue = AlipayInputErrorCheck.checkPhoneNumber(mobileNo);
				if (matchesValue == AlipayInputErrorCheck.NO_ERROR) {
					doSubmitFeedbackContent();
				} else {
					showDialog(AlipayFeedBack.this, getResources().getString(R.string.ErrorInputTelNum));
					errorStr = getResources().getString(R.string.ErrorInputTelNum);
				}
			}
		}
		
		/*if(!"".equals(errorStr)){
			StorageStateInfo storageStateInfo = StorageStateInfo.getInstance();
			AlipayLogAgent.onEvent(this,
		 			Constants.MONITORPOINT_CLIENTSERR,
		 			errorStr, 
		 			Constants.FEEDBACKVIEW,//viewID
		 			"",//appID
		 			"",//appVersion
		 			storageStateInfo.getValue(Constants.STORAGE_PRODUCTID),
		 			getUserId(),
		 			storageStateInfo.getValue(Constants.STORAGE_PRODUCTVERSION),
		 			storageStateInfo.getValue(Constants.STORAGE_CLIENTID));
		}*/
	}

	private void doSubmitFeedbackContent() {
		mProgress = getDataHelper().showProgressDialogWithCancelButton(this, "", getResources().getString(R.string.SubmitingFeedbackContent), false, true,
		    getDataHelper().cancelListener, getDataHelper().cancelBtnListener);
		getDataHelper().sendSubmitFeedbackContent(Constant.OP_FEEDBACK_OPERATIONTYPE, proposalType, userName, mobileNo, feedbackContent, productID,
				productVersion, mHandler, AlipayHandlerMessageIDs.MENU_FEEDBACK_MSG);
	}

	private void showDialog(Activity activity, String message) {
	    getDataHelper().showDialog(activity, R.drawable.infoicon, getResources().getString(R.string.WarngingString), message,
				getResources().getString(R.string.Ensure), null, null, null, null, null);
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			feedbackContent = mUserFeedbackContent.getText().toString().trim();
			if (!(feedbackContent == null || feedbackContent.equals(""))) {
			    getDataHelper().showDialog(AlipayFeedBack.this, R.drawable.infoicon, getResources().getString(R.string.WarngingString), getResources()
						.getString(R.string.WarningGiveUpFeedbackContent), getResources().getString(R.string.Ensure), giveUpFeedbackContentEnsure,
						getResources().getString(R.string.Cancel), null, null, null);
			} else {
				this.finish();
			}

		}
		return false;
	}

	protected DialogInterface.OnClickListener giveUpFeedbackContentEnsure = new DialogInterface.OnClickListener() {
		@Override
		public void onClick(DialogInterface dialog, int which) {
			AlipayFeedBack.this.finish();
		}
	};

	private TextWatcher mTextWatcher = new TextWatcher() {
		private CharSequence temp;
		private int selectionStart;
		private int selectionEnd;

		@Override
		public void beforeTextChanged(CharSequence s, int start, int count, int after) {
			// TODO Auto-generated method stub

		}

		@Override
		public void onTextChanged(CharSequence s, int start, int before, int count) {
			// TODO Auto-generated method stub
			temp = s;
		}

		@Override
		public void afterTextChanged(Editable s) {
			// TODO Auto-generated method stub
			// int number = num - s.length();
			int number = s.length();
			hasnumTV.setText(number + AlipayFeedBack.this.getResources().getString(R.string.FeedbackContentWarning));
			selectionStart = mUserFeedbackContent.getSelectionStart();
			selectionEnd = mUserFeedbackContent.getSelectionEnd();
			if (temp.length() > num) {
				s.delete(selectionStart - 1, selectionEnd);
				int tempSelection = selectionEnd;
				mUserFeedbackContent.setText(s);
				mUserFeedbackContent.setSelection(tempSelection);// 设置光标在最后
			}

		}

	};

}
