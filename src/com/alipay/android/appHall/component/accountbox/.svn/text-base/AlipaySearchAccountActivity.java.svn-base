package com.alipay.android.appHall.component.accountbox;

import java.util.ArrayList;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnFocusChangeListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.alipay.android.appHall.Helper;
import com.alipay.android.appHall.component.UIAccountBox;
import com.alipay.android.client.RootActivity;
import com.alipay.android.client.constant.Constant;
import com.alipay.android.client.util.AlipayDataStore;
import com.alipay.android.client.util.AlipayInputErrorCheck;
import com.alipay.android.client.util.BaseHelper;
import com.alipay.android.client.util.LephoneConstant;
import com.alipay.android.log.Constants;
import com.alipay.android.util.LogUtil;
import com.eg.android.AlipayGphone.R;

public class AlipaySearchAccountActivity extends RootActivity implements
		OnItemClickListener, OnClickListener {
	private static final String LOG_TAG = "AlipaySearchAccountActivity";

	private Intent mIntent = null;
	private String mGotAccount = "";

	private TextView mtxtTitle = null;
	private TextView mContact = null;
	private EditText mPayAccount = null;
	private Button mEnsureContact = null;
	private LinearLayout mBtnAccountSearch = null;
	private LinearLayout mBtnLocalGet = null;
	private LinearLayout mBtnBarcodeGet = null;
	private ListView mPayAccountList = null;;
	private ImageButton mButton;

	@SuppressWarnings("rawtypes")
	private ArrayAdapter mAccountAdapter = null;
	ArrayList<String> mAccountList = null;

	private boolean mAccountFromQR = false;

	// private int mFlagFrom = -1;
	private String mCurContact = "";

	private String mCurCodeType = Constant.BARCODE_CAPTURE_TYPE_QRCODE;

	private boolean useLN;

	private boolean useQR;
	private boolean alipay_account;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.alipay_searchlist);

		mGotAccount = null;

		mIntent = getIntent();

		alipay_account = mIntent.getBooleanExtra(UIAccountBox.ACCOUNT_TYPES[0],
				true);
		useQR = mIntent.getBooleanExtra(UIAccountBox.ACCOUNT_TYPES[1], true);
		useLN = mIntent.getBooleanExtra(UIAccountBox.ACCOUNT_TYPES[2], true);
		// mFlagFrom = mIntent.getIntExtra(Constant.ACCOUNT_SEARCH_FROM_TYPE,
		// 0);
		mCurContact = mIntent.getStringExtra(Constant.PAY_TRADE_RECORD_CONTACT);

		if (mCurContact.indexOf("(") != -1) {
			mCurContact = mCurContact.trim();
			int startIndex = mCurContact.indexOf("(");
			mCurContact = mCurContact.substring(startIndex + 1,
					mCurContact.length() - 1);
		}

		loadAllVariables();
	}

	private void loadAllVariables() {
		mtxtTitle = (TextView) findViewById(R.id.title_text);
		mtxtTitle.setText(R.string.selectPeerAccount);

		mPayAccount = (EditText) findViewById(R.id.search_box);
		if (mCurContact != null
				&& mCurContact.equals(getString(R.string.getAccountSuccess))) {
			mCurContact = "";
		}
		LogUtil.logMsg(Constant.LOG_LEVEL_INFO, LOG_TAG,
				"loadAllVariables mCurContact:" + mCurContact);

		mButton = (ImageButton) findViewById(R.id.clear_box);
		mButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				mPayAccount.setText("");
			}
		});

		if (mCurContact != null && mCurContact.length() > 0)
			mButton.setVisibility(View.VISIBLE);
		mPayAccount.setText(mCurContact);
		mPayAccount.addTextChangedListener(filterTextWatcher);

		mEnsureContact = (Button) findViewById(R.id.AccountEnsure);
		mEnsureContact.setOnClickListener(this);

		mContact = (TextView) findViewById(R.id.PayAccountSearchText);
		mContact.setOnFocusChangeListener(new OnFocusChangeListener() {
			@Override
			public void onFocusChange(View v, boolean hasFocus) {
				// TODO Auto-generated method stub
				if (mBtnAccountSearch.isFocusable()) {
					int newColor = 0xFF9d6504;
					mContact.setTextColor(newColor);
				}
			}
		});

		mBtnAccountSearch = (LinearLayout) findViewById(R.id.PayAccountSearchButton);
		if (mBtnAccountSearch != null) {
			mBtnAccountSearch.setOnClickListener(this);
		}
		if (!alipay_account) {
			mBtnAccountSearch.setVisibility(View.GONE);
		}

		mBtnLocalGet = (LinearLayout) findViewById(R.id.PayLocalGetButton);
		if (mBtnLocalGet != null) {
			mBtnLocalGet.setOnClickListener(this);
		}
		if (!useLN) {
			mBtnLocalGet.setVisibility(View.GONE);
		}

		mBtnBarcodeGet = (LinearLayout) findViewById(R.id.PayQrcodeGetButton);
		if (mBtnBarcodeGet != null) {
			mBtnBarcodeGet.setOnClickListener(this);
		}
		if (!useQR) {
			mBtnBarcodeGet.setVisibility(View.GONE);
		}

		mPayAccountList = (ListView) findViewById(android.R.id.list);
		mPayAccountList.setOnItemClickListener(this);

		// mAccountList = mDataHelper.getContactData(this);
		// if(mAccountList!=null && mAccountList.size()>0){
		// mAccountAdapter = new ArrayAdapter(this,
		// R.layout.contact_fliter_item, R.id.account, mAccountList);
		// mPayAccountList.setAdapter(mAccountAdapter);
		// }
		String[] userGroup = getDataHelper().getHistoryContactGroup(this);
		if (userGroup != null) {
			mAccountAdapter = new ArrayAdapter<String>(this,
					R.layout.contact_fliter_item, R.id.account, userGroup);
			mPayAccountList.setAdapter(mAccountAdapter);
		} else {
			// mPayAccountList.setVisibility(View.GONE);
			// mHaveNoRecord.setVisibility(View.VISIBLE);
		}

	}

	private TextWatcher filterTextWatcher = new TextWatcher() {

		public void afterTextChanged(Editable s) {
		}

		public void beforeTextChanged(CharSequence s, int start, int count,
				int after) {
		}

		public void onTextChanged(CharSequence s, int start, int before,
				int count) {
			if (mAccountAdapter != null) {
				mAccountAdapter.getFilter().filter(s);
			}

			if (s.length() > 0) {
				mButton.setVisibility(View.VISIBLE);
			} else {
				mButton.setVisibility(View.GONE);
			}
		}

	};

	@Override
	protected void onStart() {
		// TODO Auto-generated method stub
		super.onStart();
		// Log.d(LOG_TAG, "onStart is called.");
	}

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		// Log.d(LOG_TAG,"onResume() called");
		super.onResume();
	}

	@Override
	protected void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
		// Log.d(LOG_TAG,"onPause() called");
	}

	@Override
	protected void onStop() {
		// TODO Auto-generated method stub
		super.onStop();
		// Log.d(LOG_TAG,"onStop() called");
	}

	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		LogUtil.logMsg(Constant.LOG_LEVEL_INFO, LOG_TAG, "onDestroy() called.");
		super.onDestroy();
		LogUtil.logMsg(Constant.LOG_LEVEL_INFO, LOG_TAG, "onDestroy() done.");
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		case R.id.AccountEnsure:
			String curAccount = mPayAccount.getText().toString();

			// check account input
			int tResult = AlipayInputErrorCheck.CheckUserID(curAccount);
			if (tResult != AlipayInputErrorCheck.NO_ERROR) {
				// Log.i(LOG_TAG, "error account is: "+tPayAccount);
				// check error.
				String warningMsg = getResources().getString(
						R.string.WarningInvalidAccountName);
				if (tResult == AlipayInputErrorCheck.ERROR_NULL_INPUT) {
					warningMsg = getResources().getString(
							R.string.NoEmptyAccountName);
				}
				getDataHelper().showDialog(AlipaySearchAccountActivity.this,
						R.drawable.infoicon,
						getResources().getString(R.string.WarngingString),
						warningMsg, getResources().getString(R.string.Ensure),
						null, null, null, null, null);
				BaseHelper.recordWarningMsg(this, warningMsg,Constants.SELECTACCOUNTVIEW);
				
			} else {
				backWithAccount(mPayAccount.getText().toString(), false,
						UIAccountBox.ISSURE);
				// mPayAccount.setImeOptions(EditorInfo.IME_ACTION_DONE);
			}
			break;

		// case R.id.PayAccountSearchText:
		case R.id.PayAccountSearchButton:
			// case R.id.PayAccountSearchLayout:
			Intent intentcontact = new Intent(AlipaySearchAccountActivity.this,
					AlipayContact.class);
			intentcontact.setData(Uri.parse(mtxtTitle.getText().toString()));// 向下一个Activity传递了string类型参数
			startActivityForResult(intentcontact, Constant.REQUEST_CODE);// 以传递参数的方式跳转到下一个Activity
			break;

		// case R.id.PayQrcodeGetText:
		case R.id.PayQrcodeGetButton:
			// case R.id.PayQrcodeGetLayout:
			// ImageView tQrcode =
			// (ImageView)findViewById(R.id.PayQrcodeGetIcon);
			// tQrcode.setBackgroundResource(R.drawable.search_account_qrcode_pressed);
			// TODO Auto-generated method stub
			Intent intentQRcode = new Intent(AlipaySearchAccountActivity.this,
					com.google.zxing.client.BarcodeCaptureActivity.class);
			intentQRcode.putExtra(Constant.BARCODE_CAPTURE_TYPE, mCurCodeType);
			startActivityForResult(intentQRcode, Constant.REQUEST_CODE_QRCODE);// 以传递参数的方式跳转到下一个Activity
			Helper.hideInputPanel(this, mPayAccount);
			break;
		case R.id.PayLocalGetButton:
			// case R.id.PayLocalGetLayout:
			Helper.hideInputPanel(this, mBtnLocalGet);
			BaseHelper.doPhoneContact(this);
		}
	}

	@Override
	public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
		// TODO Auto-generated method stub
		mGotAccount = mPayAccountList.getItemAtPosition(arg2).toString();
		LogUtil.logMsg(Constant.LOG_LEVEL_DEBUG, LOG_TAG,
				"mPayAccountList mGotAccount=" + mGotAccount);
		backWithAccount(mGotAccount, false, UIAccountBox.ISHIS);
	}

	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			LogUtil.logMsg(Constant.LOG_LEVEL_INFO, LOG_TAG,
					"onKeyDown KEYCODE_BACK!");
			backWithAccount("", false, "");
			return true;
		} else if (keyCode == KeyEvent.KEYCODE_SEARCH) {
			return true;
		}
		return false;
	}

	private String[] mPhoneString = null;
	private String phoneNumber = "";

	private void setAccount(Intent intent) {

		if (intent == null) {
			return;
		}
		if (!LephoneConstant.isLephone()) {
			ArrayList<String> tPhone = BaseHelper.getContactPhoneNo(
					intent.getData(), this);

			if (tPhone.size() > 1) {

				mPhoneString = new String[tPhone.size()];
				for (int i = 0; i < tPhone.size(); i++) {
					mPhoneString[i] = tPhone.get(i);
				}

				new AlertDialog.Builder(this)
						.setTitle(R.string.SelectContact)
						.setSingleChoiceItems(mPhoneString, 0,
								new DialogInterface.OnClickListener() {
									public void onClick(DialogInterface dialog,
											int which) {
										phoneNumber = mPhoneString[which];
										dialog.dismiss();
										backWithAccount(phoneNumber, false,
												UIAccountBox.ISLN);
									}
								}).show();
				return;
			} else if (tPhone.size() == 1) {
				phoneNumber = tPhone.get(0);
			} else {
				phoneNumber = "";
			}
		} else if (LephoneConstant.isLephone()) {
			try {
				Uri contactData = intent.getData();
				Cursor c = this.managedQuery(contactData, null, null, null,
						null);
				if (c.moveToFirst()) {
					phoneNumber = c.getString(c.getColumnIndexOrThrow("data1"));
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		if (phoneNumber == null) {
			phoneNumber = "";
		}

		backWithAccount(phoneNumber, false, UIAccountBox.ISLN);
	}

	public void onActivityResult(int requestCode, int resultCode, Intent intent) {
		super.onActivityResult(requestCode, resultCode, intent);

		LogUtil.logMsg(Constant.LOG_LEVEL_DEBUG, LOG_TAG,
				"onActivityResult() called. requestCode:" + requestCode
						+ ", resultCode:" + resultCode);

		if (requestCode == Constant.REQUEST_CODE) {
			if (resultCode == RESULT_OK) {
				// mPayAccount.setText(intent.getData().toString());
				Uri intentUri = intent.getData();
				if(intentUri == null){
					mGotAccount = "";
				}else{
					mGotAccount = intentUri.toString();
				}
				
				String name = intent.getStringExtra(Constant.PRF_CONTACTNAME);

				if(name != null && !"".equals(name)){
					name = "*" + name.substring(1);
					mGotAccount = name + "(" + mGotAccount + ")";
				}
				
				backWithAccount(mGotAccount, true, UIAccountBox.ISAA);
			} else {
				mGotAccount = "";
			}
		} else if (requestCode == Constant.REQUEST_CODE_QRCODE) {
			if (resultCode == Activity.RESULT_OK) {
				mAccountFromQR = true;

				// mPayAccount.setText(getString(R.string.getAccountSuccess));
				mGotAccount = intent.getData().toString();
				LogUtil.logMsg(Constant.LOG_LEVEL_DEBUG, LOG_TAG,
						"onActivityResult mSnapshotQrCode=" + mGotAccount);

				AlipayDataStore settings = new AlipayDataStore(this);
				Constant.STR_QRCODE_PREFIX = settings
						.getString(AlipayDataStore.QRCODE_PREFIX);

				if (mAccountFromQR && mGotAccount != null
						&& !mGotAccount.contains(Constant.STR_QRCODE_PREFIX)) {
					// 不是支付宝二维码
				    getDataHelper().showDialog(
							this,
							R.drawable.infoicon,
							getResources().getString(R.string.WarngingString),
							getResources().getString(
									R.string.QrcodeCreateTradeWarning),
							getResources().getString(R.string.Ensure),
							new DialogInterface.OnClickListener() {
								@Override
								public void onClick(DialogInterface dialog,
										int which) {
									// 记录参数复位
									mPayAccount.setText("");
									mAccountFromQR = false;
									mGotAccount = "";
								}
							}, null, null, null, null);
				} else {
					backWithAccount(mGotAccount, false, UIAccountBox.ISQR);
				}

			}
		} else if (requestCode == Constant.REQUEST_LEPHONE_CONTACT
				|| requestCode == Constant.REQUEST_CONTACT) {
			// 直接back键的情况
			if (intent == null) {
				return;
			}
			setAccount(intent);

		} else {
			mGotAccount = "";
		}
	}

	private void backWithAccount(String account, boolean isContact,
			String account_type) {
		try {
			// 返回时，不带有效参数
			if (mAccountFromQR) {
				mIntent.putExtra(Constant.BARCODE_CAPTURE_TYPE, mAccountFromQR);
			}
			mIntent.putExtra(Constant.ACCOUNT_SEARCH_IS_CONTACT, isContact);
			mIntent.putExtra(UIAccountBox.ACCOUNT_TYPE, account_type);
			mIntent.setData(Uri.parse(account));
			this.setResult(Activity.RESULT_OK, mIntent);
			
			this.finish();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
