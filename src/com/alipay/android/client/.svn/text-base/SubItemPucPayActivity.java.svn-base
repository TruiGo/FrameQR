/*
 * Copyright 2009, Embedded General Inc.  All right reserved
 * 
 * draft:Roger
 * 
 */
package com.alipay.android.client;

import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.InputType;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.alipay.android.client.baseFunction.AlipayPhoneBinding;
import com.alipay.android.client.constant.AlipayHandlerMessageIDs;
import com.alipay.android.client.constant.Constant;
import com.alipay.android.client.lifePayment.HistoryBillInfo;
import com.alipay.android.client.lifePayment.PaymentCity;
import com.alipay.android.client.util.BaseHelper;
import com.alipay.android.client.util.LephoneConstant;
import com.alipay.android.client.util.Responsor;
import com.alipay.android.comon.component.EditTextHasNullChecker;
import com.alipay.android.comon.component.EditTextWithButton;
import com.alipay.android.comon.component.ProgressDiv;
import com.alipay.android.core.MsgAction;
import com.alipay.android.core.ParamString;
import com.alipay.android.log.AlipayLogAgent;
import com.alipay.android.log.Constants;
import com.alipay.android.log.StorageStateInfo;
import com.alipay.android.safepay.MobileSecurePayer;
import com.alipay.android.ui.bill.BillManagerRootControllerActivity;
import com.eg.android.AlipayGphone.R;

/**
 * The activity about DeviceInput job. </p> 生活缴费
 * 
 * @author Roger.Huang
 */
public class SubItemPucPayActivity extends RootActivity implements
		OnItemClickListener {
	public static Activity mContext = null;
	HashMap<String, String> myResponse = new HashMap<String, String>();
	JSONObject myJsonObject = null;
	private MessageFilter mMessageFilter = new MessageFilter(this);
	private Handler mHandler = new Handler() {
		public void handleMessage(Message msg) {
			if (msg.obj != null) {
				if (msg.what != AlipayHandlerMessageIDs.RQF_PAY
						&& msg.what != AlipayHandlerMessageIDs.RQF_AGENT_PAY) {
					showResult(msg);
				}
			} else {
				if (mProgress != null) {
					mProgress.dismiss();
					mProgress = null;
				}
			}

			switch (msg.what) {
			case AlipayHandlerMessageIDs.RQF_PAY:
			case AlipayHandlerMessageIDs.RQF_AGENT_PAY:
				payingFinish2(msg);
				break;
			}
			super.handleMessage(msg);

		}
	};

	private void payingFinish2(Message msg) {
		closeProgress();

		try {
			if (msg.arg1 == MobileSecurePayer.PAY_OK) {// 快捷支付成功
				if (Constant.OPERATION_UID != null) {
                    getMBus().sendMsg("", Constant.OPERATION_UID, MsgAction.ACT_LAUNCH, null);
				} else{
                    if(mCityInfo.mCompanies[mCurrentCompany].chargeQueryMode.equalsIgnoreCase("7")){
                    	//代缴模式的公司缴费成功提示
                    	getDataHelper().showDialog(this, -1, 
                    			getResources().getString(R.string.LifeAgentPaySuccedTip),
                    			getResources().getString(R.string.LifeAgentPaySuccedText),
                    			getResources().getString(R.string.Ensure), 
                    			new DialogInterface.OnClickListener() {
									@Override
									public void onClick(DialogInterface dialog, int which) {
										Intent intent = new Intent(SubItemPucPayActivity.this, Main.class);
										intent.setAction(Intent.ACTION_MAIN);
										intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
										startActivity(intent);
										
									}
									}, null, null, null, null);
                    }else{
	                    Intent intent = new Intent(SubItemPucPayActivity.this, Main.class);
						intent.setAction(Intent.ACTION_MAIN);
						intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
						startActivity(intent);
                    }
                    
/*//                    setTabIndex(Main.TAB_INDEX_DEFAULT);
                    Intent intent = new Intent();
                    intent.setClass(this, MainActivity.class);
                    startActivity(intent);
//					finish();
*/				}
			} else if (msg.arg1 == 10 || msg.arg1 == MobileSecurePayer.PAY_OLD) {//代付取消或快捷支付失败
//                setTabIndex(Main.TAB_INDEX_RECORDS_CONSUMPTION);
                Intent intent = new Intent();
				intent.setClass(mContext, BillManagerRootControllerActivity.class);
				mContext.startActivity(intent);
				this.finish();
			}

//			this.finish();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * 缴费类型 water,electricity,gas，broadband ,phone
	 */
	private String mChargeType = "";
	private String viewType = "";
    private String appID = "";
	private int mCurPage = NO_ERROR;
	private static final int NO_ERROR = -5;
	private static final int CREATE_BILL_NO = -4;// 创建缴费流水号
	private static final int BILL_CONFIRM = -3;// 账单确认
	private static final int BILL_OPTION = -2;// 账单选择
	private static final int INPUT_INFO = -1;// 信息录入
	/**
	 * 界面组件初始化
	 */
	private TextView mTitleName = null;
	private ImageView historyIcon = null; 
	/**
	 * 缴费信息录入界面组件初始化
	 */
	private ScrollView mPucChargeRegionLayout = null;// 信息录入界面
	private TextView selectCity = null;// 显示默认缴费城市，可跳转至缴费城市滑动列表
	private Spinner mSelectPaymentCompany = null;// 缴费公司下拉列表
	private ArrayAdapter<String> mSelectPaymentCompanyAdapter = null;// 缴费公司下拉列表数据适配器
	private TextView mSelectPaymentCompanyTip = null;// 缴费公司提示
	private TextView mAgentPayModeCompanyTip = null; //缴费的代付模式下的提示
	private LinearLayout mSelectChargeWayLayout = null;// 缴费方式layout
	private Spinner mSelectPaymentWay = null;// 选择缴费方式下拉列表
	private ArrayAdapter<String> mSelectPaymentWayAdapter = null;// 选择缴费方式下拉列表数据适配器
	private TextView mPucNameId = null;// 缴费的方式，条形码/户号/户名
	private EditTextWithButton mPucNameEditText = null;// 条形码/户号/户名输入框
	private ImageButton mScanButton = null;// 条形码扫描
	private EditTextWithButton mPucNumEditText = null;// 缴费金额输入框
	private EditTextWithButton mPucNumEditText2 = null;// 缴费金额输入框
	private WebView mView = null;// wap
	private TextView mPucRead = null;// 阅读协议
	private CheckBox mPucCheckBox = null;// 协议服务单选框
	private Button mNext = null;// 下一步的button
	private boolean isDisplaySelected = false;// 是否显示账单页面
	private RelativeLayout mPucChargeSelectedLayout = null;// 账单选择界面
	private ListView mPucChargeSelectedListViews = null; // 账单选择列表
	private Button mSubmit = null;// 提交button
	private CheckBox mSettingTimeCheckBox = null;
	private Spinner mSelectTime = null;
	private TextView mPucNumTip = null;
	private TextView mPucNumTip2 = null;
	
	/**
	 * 缴费确认界面组件初始化
	 */
	private ScrollView mPucChargeConfirmLayout = null;
	private TextView mRegionId = null;// 缴费城市
	private TextView mChargeCompanyId = null;// 缴费公司
	private LinearLayout mChargeNameLayout = null;
	private TextView mChargeName = null;
	private TextView mChargeNameContent = null;
	private LinearLayout mChargeNumLayout = null;
	private TextView mChargeNum = null;
	private TextView mChargeNumContent = null;
	private LinearLayout mArrearsBillLayout = null;
	private TextView mArrearsBillId = null;
	private TextView mPaymentId = null;
	//代付模式下，确认界面显示服务费和总金额。
	private LinearLayout mServiceChargeLayout = null;
	private LinearLayout mTotalPaymentLayout = null;
	private TextView mTotalPaymentId = null;
	//充值模式、欠费自由缴模式下显示的输入框
	private EditTextWithButton mConfirmLayoutPaymentInput = null;
	/**
	 * 联网进度条
	 */
	private ProgressDiv mProgress = null;
    private ProgressBar mProgressBar;
	/**
	 * View Switcher Variable
	 */
	private static boolean AGENT_REQUEST = false;
	/**
	 * 判断是否第一次访问服务器
	 */
	private boolean isFirst = true;
	/**
	 * 缴费录入信息解析
	 */
	private String[] mCityList = null;// 缴费城市列表信息
	private String mCompany = null;// 默认显示的公司,如果该字段为空，请默认使用第一个公司
	private String mCompanyShort = "";//默认显示公司的缩写。和  mCompany 是对相应的。
	private String mChargeInputType = null;// 录入方式,如果没有该字段，请默认选择Defaultselected的值
	private int mCurrentInput = 0;// 选择录入方式的index
	private String mAgreementUrl = null;// 阅读协议url
	private JSONObject mHistoryValueJSON = null;// 缴费历史内容
	private HashMap<String, String> mHistoryValue = null;
	private CityInfo mCityInfo = null;// 默认缴费信息
	private int mCurrentCompany = -1;// 当前公司的index
	
	private String  mHistorySelectCompany = ""; //历史记录中返回的公司
	private String  mHistorySelectCompanyShort = ""; //历史记录中返回的公司的缩写
	
	private int mDate = 0;//用户设置的提醒日期
	private boolean isHistorySelected = false ;//是否是从历史中选择了一条记录返回
	private String mSystemDate = "25";
	
	
	/**
	 * 
	 */
	private ChargeBillLists[] mCBLists = null;
	private String mCurrentCityStr = "";// 当前城市的名字
	private String mBillNo = null;// 缴费流水号
	private String mCanSelect = null;
	private String mBillId = null;
	private String mChargeRegion = null;
	private String mChargeUnit = null;
	private String mBillInfo = null;
	private String mChargeAmount = null;
	private String mAgentPayChargeAmount = "0";

	private ConfirmBillLists[] mConfirmBLists = null;

	private StorageStateInfo storageStateInfo = StorageStateInfo.getInstance();
	EditTextHasNullChecker editTextHasNullChecker = new EditTextHasNullChecker();
	EditTextHasNullChecker confirmeditTextHasNullChecker = new EditTextHasNullChecker(){
		public void afterTextChanged(android.text.Editable s) {
			super.afterTextChanged(s);
			String payAmount = "".equalsIgnoreCase(s.toString().trim())?"0":s.toString().trim();
			mPaymentId.setText(payAmount + getResources().getString(R.string.Yuan));
			mTotalPaymentId.setText(String.valueOf(Float.parseFloat(payAmount) + 1) + getResources().getString(R.string.Yuan));
		};
	};

	private String params;//push跳转时带参数
	private String amount;//push参数中的金额
	/**
	 * 缴费城市的所有信息
	 * 
	 * @author chengkuang
	 * 
	 */
	private class ChargeBillLists {
		private String mBillId = ""; // 账单id
		private String mInfo = "";// 信息
	}

	/**
	 * View Switcher Variable
	 */

	/**
	 * 缴费城市的所有信息
	 * 
	 * @author chengkuang
	 * 
	 */
	private class ConfirmBillLists {
		private String mLabel = ""; // 户名,户 号
		private String mContent = "";// 内容
	}

	/**
	 * 缴费城市的所有信息
	 * 
	 * @author chengkuang
	 * 
	 */
	private class CityInfo {
		private String mCity = ""; // 城市
		// private String mCityTip = "";//城市的提示
		private CompanyInfo[] mCompanies = null;// 公司的名字
	}

	/**
	 * 缴费公司信息
	 * 
	 * @author chengkuang
	 * 
	 */
	private class CompanyInfo {
		private String mCompanyName = "";// 默认选中的公司
		private InputInfos[] mInputInfos = null;
		private InputInfos[] mInputInfos1 = null;
		private InputInfos[] inputInfosConfirm = null;
		
		private ChargeInputType mChargeType = null;// chargeType 中的value=0使用
													// inputInfos，value=1使用inputInfos1
		private String attention = "";// 缴费公司支持的时间段
		private String status = "";// 缴费状态标志 0正常/1暂停 缴费
		
		private String chargeQueryMode =  "";//该公司缴费模式
		private String shortName = "";

	}

	/**
	 * 输入的信息
	 * 
	 * @author chengkuang
	 * 
	 */
	private class InputInfos {
		private String mInputInfoKeyLabel = "";// 户号或户号等
		private String mField = "";// 字段名称
		private String mfieldType = "";// 字段类型0、 英文文本; 1、数字 3、金额、4、中文文本。
		private Rule[] mRules = null;// 判断规则
		private String mEnable = "";
	}

	/**
	 * 户名户号的判断规则信息
	 * 
	 * @author chengkuang
	 * 
	 */
	private class Rule {
		private String mRegex = "";// 户名正则表达式
		private String mError = "";// 错误信息
		// private String mCheck="";//户号规则
	}

	private class ChargeInputType {
		// private String mLabel="";//缴费方式
		private Options[] mOptions = null;
		private String mDefaultselected = "";
	}

	private class Options {
		private String mName = "";// 条形码|销根号
		private String mValue = "";// 0|1
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		mContext = this;

		if (LephoneConstant.isLephone()) {
			getWindow().addFlags(LephoneConstant.FLAG_ROCKET_MENU_NOTIFY);
		}

		setContentView(R.layout.alipay_pucinputinfo);
		loadAllVariables();

		AGENT_REQUEST = false;

		setTitle();
		
	}


	@Override
	public void onDestroy() {
		super.onDestroy();
		if (mHandler != null) {
			mHandler = null;
		}
		AGENT_REQUEST = false;
	}

	private void openProcessDialog(String msg) {
		if (mProgress == null) {
			mProgress = getDataHelper().showProgressDialogWithCancelButton(this, null,
					msg, false, true, getDataHelper().cancelListener,
					getDataHelper().cancelBtnListener);
		}
	}

	private void loadAllVariables() {
		Intent intent = getIntent();
		mChargeType = intent.getStringExtra(Constant.PUBLIC_PAY_URL);
		if (intent != null) {
			params = intent.getStringExtra("param");
			if(params == null || "".equalsIgnoreCase(params)){
				Bundle fromHistoryBundle = intent.getExtras();
				if(fromHistoryBundle != null){
					HistoryBillInfo historyBillInfo = (HistoryBillInfo) fromHistoryBundle.getSerializable(Constant.LIFEPAY_HISTORYINFO_KEY);
					if(historyBillInfo != null){
						isHistorySelected = true;
						mCompany = historyBillInfo.chargeCompanyName;
						mCompanyShort = historyBillInfo.chargeCompanySname;
						mHistorySelectCompany = historyBillInfo.chargeCompanyName;
						mHistorySelectCompanyShort = historyBillInfo.chargeCompanySname;
						mCurrentCityStr = historyBillInfo.city;
						mHistoryValue  = new HashMap<String, String>();
						
						try {
							mHistoryValueJSON = new JSONObject(historyBillInfo.originalData);
						} catch (JSONException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					}
				}
			}
		}

		mCurPage = INPUT_INFO;

		mTitleName = (TextView) findViewById(R.id.title_text);
		historyIcon = (ImageView) findViewById(R.id.title_right);
		historyIcon.setVisibility(View.VISIBLE);
		historyIcon.setImageResource(R.drawable.history_status);
		historyIcon.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {	
				AlipayLogAgent.writeLog(SubItemPucPayActivity.this, Constants.BehaviourID.CLICKED, null, null, appID, null, viewType + "InputView", viewType + "HistoryView", "lifePayInputnextStepButton", null);
					Intent intent = new Intent (SubItemPucPayActivity.this,LifePayHistoryActivity.class);
					intent.putExtra(Constant.PUBLIC_PAY_URL, mChargeType);
					intent.putExtra(Constant.JUMP_HISTORY_FROM, Constant.JUMP_HISTORY_FROM_INPUT);
					startActivityForResult(intent, Constant.START_LIFEPAY_HISTORY);
			}
		});
        mProgressBar = (ProgressBar) findViewById(R.id.ProgressBar);
		mPucChargeRegionLayout = (ScrollView) findViewById(R.id.PucChargeRegionLayout);
		mPucChargeConfirmLayout = (ScrollView) findViewById(R.id.PucChargeConfirmLayout);
		selectCity = (TextView) findViewById(R.id.SelectCityTextView);
		selectCity.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if (mCityList != null) {
					Intent intent = new Intent(SubItemPucPayActivity.this,
							PaymentCity.class);
					intent.putExtra(Constant.PAYMENT_CITY_LIST, mCityList);
					intent.putExtra(Constant.DEFAULT, selectCity.getText());
					SubItemPucPayActivity.this.startActivityForResult(intent,
							Constant.START_PAYMENT_CITY);
				}
			}
		});

		// 缴费公司
		mSelectPaymentCompany = (Spinner) findViewById(R.id.SelectPaymentCompany);
		mSelectPaymentCompanyAdapter = new ArrayAdapter<String>(this,
				R.layout.spinner_item);
		mSelectPaymentCompanyAdapter
				.setDropDownViewResource(R.layout.simple_spinner_dropdown_item);
		mSelectPaymentCompany.setAdapter(mSelectPaymentCompanyAdapter);
		mSelectPaymentCompany
				.setOnItemSelectedListener(new OnItemSelectedListener() {
					@Override
					public void onItemSelected(AdapterView<?> parent,
							View view, int position, long id) {
						mCurrentCompany = position;
						mCompany = mCityInfo.mCompanies[mCurrentCompany].mCompanyName;
						mCompanyShort = mCityInfo.mCompanies[mCurrentCompany].shortName;
						mCurrentInput = 0;
						updateInputInfo();
					}

					@Override
					public void onNothingSelected(AdapterView<?> parent) {

					}
				});
		// 缴费公司提示消息
		mSelectPaymentCompanyTip = (TextView) findViewById(R.id.SelectPaymentCompanyTip);
		//代付缴费的公司的提示消息
		mAgentPayModeCompanyTip = (TextView) findViewById(R.id.AgentPayModeCompanyTip);
		// 缴费方式
		mSelectChargeWayLayout = (LinearLayout) findViewById(R.id.SelectChargeWayLayout);
		// 选择缴费方式Spinner
		mSelectPaymentWay = (Spinner) findViewById(R.id.SelectPaymentWay);
		// 缴费方式Adapter
		mSelectPaymentWayAdapter = new ArrayAdapter<String>(this,
				R.layout.spinner_item);
		mSelectPaymentWayAdapter
				.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		mSelectPaymentWay.setAdapter(mSelectPaymentWayAdapter);
		mSelectPaymentWay
				.setOnItemSelectedListener(new OnItemSelectedListener() {
					@Override
					public void onItemSelected(AdapterView<?> parent,
							View view, int position, long id) {
						mCurrentInput = position;
						updatePaymentInfo(position);
					}

					@Override
					public void onNothingSelected(AdapterView<?> parent) {
					}
				});

		mPucNameEditText = (EditTextWithButton) findViewById(R.id.PucNameEditText);
		// 条形码或户名、户号
		mPucNameId = mPucNameEditText.getInputName();
		editTextHasNullChecker.addNeedCheckView(mPucNameEditText.getEtContent());
		mPucNameEditText.getEtContent().addTextChangedListener(editTextHasNullChecker);
		// 调起条形码扫描
		mScanButton = (ImageButton) findViewById(R.id.scanButton);
		mScanButton.setOnClickListener(new ScanButtonListener());
		// 缴费金额
		mPucNumEditText = (EditTextWithButton) findViewById(R.id.PucNumEditText);
		editTextHasNullChecker.addNeedCheckView(mPucNumEditText.getEtContent());
		mPucNumEditText.getEtContent().addTextChangedListener(editTextHasNullChecker);
		
		mPucNumEditText2 = (EditTextWithButton) findViewById(R.id.PucNumEditText2);
		editTextHasNullChecker.addNeedCheckView(mPucNumEditText2.getEtContent());
		mPucNumEditText2.getEtContent().addTextChangedListener(editTextHasNullChecker);
		
		mPucNumTip = (TextView) findViewById(R.id.PucNumTip);
		mPucNumTip2 = (TextView) findViewById(R.id.PucNumTip2);
		
		mConfirmLayoutPaymentInput = (EditTextWithButton) findViewById(R.id.ConfirmLayoutPaymentInput);
		// 协议wap
		mView = (WebView) findViewById(R.id.registerWebView);
		WebSettings settings = mView.getSettings();
		settings.setSupportZoom(false);
		
		mView.setVisibility(View.GONE);
		mView.setWebViewClient(new WebViewClient() {
			@Override
			public void onPageFinished(WebView view, String url) {
				if (url.indexOf("webMC.htm") != -1) {
					mView.loadData("", "text/html", "utf-8");
					mView.setVisibility(View.GONE);
					setTitle();
					mPucChargeRegionLayout.setVisibility(View.VISIBLE);
					historyIcon.setVisibility(View.VISIBLE);
				}
			}

			@Override
			public void onReceivedError(WebView view, int errorCode,
					String description, String failingUrl) {
                if (failingUrl.indexOf("webMC.htm") == -1) {
			    Toast.makeText(SubItemPucPayActivity.this, "协议加载失败!", 2000).show();
			}
			}
		});
		mView.setWebChromeClient(new WebChromeClient(){
	        @Override
	        public void onProgressChanged(WebView view, int newProgress) {
	            if (newProgress >= 0&&newProgress<100){
	                mProgressBar.setVisibility(View.VISIBLE);
	                mProgressBar.setProgress(newProgress);
	            }else
	                mProgressBar.setVisibility(View.GONE);
	        }
		});
		// 阅读协议
		mPucRead = (TextView) findViewById(R.id.PucRead);
		mPucRead.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if (mAgreementUrl != null && !mAgreementUrl.equals("")) {
					mTitleName.setText(R.string.PayServiceInfo);
					mView.setVisibility(View.VISIBLE);
					mPucChargeRegionLayout.setVisibility(View.GONE);
					historyIcon.setVisibility(View.GONE);

					/*// 缴费协议
					AlipayLogAgent.onPageJump(
							SubItemPucPayActivity.this,
							storageStateInfo
									.getValue(Constants.STORAGE_CURRENTVIEWID),
							Constants.PROTOCOLVIEW, // jumpViewId
							storageStateInfo.getValue(Constants.STORAGE_APPID), // appID
							storageStateInfo
									.getValue(Constants.STORAGE_APPVERSION), // AppVersionID
							storageStateInfo
									.getValue(Constants.STORAGE_PRODUCTID),
							getUserId(),
							storageStateInfo
									.getValue(Constants.STORAGE_PRODUCTVERSION),
							storageStateInfo
									.getValue(Constants.STORAGE_CLIENTID));*/


					mView.loadUrl(mAgreementUrl);
				}
			}
		});
		// 同步缴费信息数据
		doConnect();
		
		// 同意协议单选框
		mPucCheckBox = (CheckBox) findViewById(R.id.PucCheckBox);
		
		mSelectTime = (Spinner) findViewById(R.id.SelectTime);
		mSelectTime.setVisibility(View.GONE);
		mSelectTime.setOnItemSelectedListener(new OnItemSelectedListener() {
			@Override
			public void onItemSelected(AdapterView<?> parent, View view,
					int position, long id) {
				mDate = position + 1;//日期从2开始，选中的日期即为position + 1 
			}
			@Override
			public void onNothingSelected(AdapterView<?> parent) {
				
			}
		});
		
		mSettingTimeCheckBox = (CheckBox) findViewById(R.id.SettingTimeCheckBox);
		mSettingTimeCheckBox.setChecked(false);
//		mSelectTime.setSelection(Integer.parseInt(mSystemDate)-1);
		if(getUserData() != null 
				&& getUserData().getMobileNo() != null 
				&& !getUserData().getMobileNo().equalsIgnoreCase("")){
			mSettingTimeCheckBox.setChecked(true);
			mSelectTime.setVisibility(View.VISIBLE);
		}
		mSettingTimeCheckBox.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				if(isChecked && getUserData() == null){
					//未登陆选中跳转登录登录
					Intent intent = new Intent(SubItemPucPayActivity.this,Login.class);
					mSettingTimeCheckBox.setChecked(false);
					startActivityForResult(intent, Constant.SETTING_TIME_CHECKBOX_REQUEST_LOGIN);
				}
				else if(isChecked && getUserData() != null &&  getUserData().getMobileNo().equalsIgnoreCase("")){
					//跳转绑定手机界面
					mSettingTimeCheckBox.setChecked(false);
					getDataHelper().showDialog(SubItemPucPayActivity.this,
							                    R.drawable.infoicon, 
							                    getResources().getString(R.string.Hint), 
							                    getResources().getString(R.string.PhoneBindingNotice), 
							                    getResources().getString(R.string.Ensure), 
							                    new DialogInterface.OnClickListener() {
													@Override
													public void onClick(DialogInterface dialog, int which) {
														Intent intent = new Intent(SubItemPucPayActivity.this,AlipayPhoneBinding.class);
														startActivityForResult(intent, Constant.SETTING_TIME_CHECKBOX_REQUEST_BINDING_PHONE);
														}
													}, 
							                    getResources().getString(R.string.Cancel), 
							                    new DialogInterface.OnClickListener() {
													@Override
													public void onClick(DialogInterface dialog, int which) {
														mSettingTimeCheckBox.setChecked(false);
														}
													},
													null, null);
					

				}else if(isChecked && getUserData() != null && !getUserData().getMobileNo().equalsIgnoreCase("")){
					mSelectTime.setVisibility(View.VISIBLE);
				}
				else {
					mSelectTime.setVisibility(View.GONE);
				}
				AlipayLogAgent.writeLog(SubItemPucPayActivity.this, Constants.BehaviourID.CLICKED, null, null, appID, null, viewType + "InputView", null, "clickAlertButton", null);
			}
		});
		// 下一步
		mNext = (Button) findViewById(R.id.next);
		editTextHasNullChecker.addNeedEnabledButton(mNext);
		mNext.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {

				AlipayLogAgent.writeLog(SubItemPucPayActivity.this, Constants.BehaviourID.CLICKED, null, null, appID, null, viewType + "InputView", viewType + "SubmitView", "nextButton", null);
				hideInput(mPucNameEditText.getEtContent());
				hideInput(mPucNumEditText.getEtContent());
				if (mPucCheckBox.isChecked()) {
					if (mCityInfo != null && mCityInfo.mCompanies != null) {
						if (mCurrentInput == 1) {
							checkInputInfo(mCityInfo.mCompanies[mCurrentCompany].mInputInfos1);
						} else {
							checkInputInfo(mCityInfo.mCompanies[mCurrentCompany].mInputInfos);
						}
					}
				} else {
				    getDataHelper().showDialog(SubItemPucPayActivity.this,
							R.drawable.infoicon,
							getResources().getString(R.string.WarngingString),
							getResources().getString(R.string.AgreeServiceOk),
							getResources().getString(R.string.Ensure), null,
							null, null, null, null);
					BaseHelper.recordWarningMsg(SubItemPucPayActivity.this,
							getResources().getString(R.string.AgreeServiceOk),
							viewType + "InputView");
				}
			}

		});
		// 账单选择界面
		mPucChargeSelectedLayout = (RelativeLayout) findViewById(R.id.PucChargeSelectedLayout);
		// 账单选择列表
		mPucChargeSelectedListViews = (ListView) findViewById(R.id.PucChargeSelectedListView);
		mPucChargeSelectedListViews.setOnItemClickListener(this);
		// 提交
		mSubmit = (Button) findViewById(R.id.Submit);
		mSubmit.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				AlipayLogAgent.writeLog(SubItemPucPayActivity.this, Constants.BehaviourID.CLICKED, null, null, appID, null, viewType + "SubmitView", null, "submited", null);
				mCurPage = CREATE_BILL_NO;//这个页面发送网络请求失败后，mCurPage是否有问题。
				cmdToServer();
			}
		});
		// 缴费确认界面
		mChargeNameLayout = (LinearLayout) findViewById(R.id.ChargeNameLayout);
		mChargeNumLayout = (LinearLayout) findViewById(R.id.ChargeNumLayout);
		mArrearsBillLayout = (LinearLayout) findViewById(R.id.ArrearsBillLayout);
		mRegionId = (TextView) findViewById(R.id.RegionId);
		mChargeCompanyId = (TextView) findViewById(R.id.ChargeCompanyId);
		mChargeName = (TextView) findViewById(R.id.ChargeName);
		mChargeNameContent = (TextView) findViewById(R.id.ChargeNameContent);
		mChargeNum = (TextView) findViewById(R.id.ChargeNum);
		mChargeNumContent = (TextView) findViewById(R.id.ChargeNumContent);
		mArrearsBillId = (TextView) findViewById(R.id.ArrearsBillId);
		mPaymentId = (TextView) findViewById(R.id.PaymentId);
		mServiceChargeLayout = (LinearLayout) findViewById(R.id.ServiceChargeLayout);
		mTotalPaymentLayout = (LinearLayout) findViewById(R.id.TotalPaymentLayout);
		mTotalPaymentId = (TextView) findViewById(R.id.TotalPaymentId);
		
		mConfirmLayoutPaymentInput = (EditTextWithButton) findViewById(R.id.ConfirmLayoutPaymentInput);
		confirmeditTextHasNullChecker.addNeedCheckView(mConfirmLayoutPaymentInput.getEtContent());
		mConfirmLayoutPaymentInput.getEtContent().addTextChangedListener(confirmeditTextHasNullChecker);
		confirmeditTextHasNullChecker.addNeedEnabledButton(mSubmit);
	}

	/**
	 * 为了减少联网的次数，如果是用户第一次登陆或者更换城市就进行联网，否则就使用第一联网后的城市数据
	 */
	private void doConnect() {
//		JSONObject json = null;
		if (mChargeType.equals(Constant.URL_GAS_RATE)) {
			viewType = "gas";
			appID = "09999997";
//			json = Constant.GAS_JSON;
		} else if (mChargeType.equals(Constant.URL_WATER_RATE)) {
			viewType = "water";
			appID = "09999995";
//			json = Constant.WATER_JSON;
		} else if (mChargeType.equals(Constant.URL_POWER_RATE)) {
			viewType = "electricity";
			appID = "09999996";
//			json = Constant.ELECTRIC_JSON;
		} else if (mChargeType.equals(Constant.URL_COMMUN_RATE)) {
			viewType = "wideline";
			appID = "09999998";
//			json = Constant.COMMUN_JSON;
		}
//		if (json != null) {
//			if (mCurPage == INPUT_INFO) {
//				parseGetPucInputInfo(json);
//				updateInputInfo();
//				selectCity.setText(mCityInfo.mCity);
//			}
//		} else {
			cmdToServer();
			setAmountText();
	}


	private void setAmountText() {
		if(amount != null && !"".equals(amount)){//push金额填充
			mPucNumEditText.setText(amount);
			mPucNumEditText.getEtContent().setSelection(amount.length());
		}
	}


	/**
	 * 检测每个输入是否合法
	 */
	private String checkItem(String regex, String str, String error) {
		try {
			if (regex.equals("isNotEmpty")) {
				if (str != null && !str.equals("")) {
					return "";
				} else {
					return error;
				}
			}
			Pattern pattern = Pattern.compile(regex);
			Matcher matcher = pattern.matcher(str);
			if (matcher.matches()) {
				return "";
			}
		} catch (Exception e) {

		}
		return error;
	}

	/**
	 * 根据用户选择缴费方式选择检测
	 */
	private void checkInputInfo(InputInfos[] inputInfos) {
		if (inputInfos != null && inputInfos.length > 0) {
			for (int i = 0; i < inputInfos.length; i++) {
				String str = mPucNameEditText.getText().toString().trim();
				if (i == 1) {
					str = mPucNumEditText.getText().toString().trim();
				}
				if(i == 2){
					str = mPucNumEditText2.getText().toString().trim();
				}
				if (inputInfos[i].mRules != null) {
					for (int j = 0; j < inputInfos[i].mRules.length; j++) {
						String error = checkItem(
								inputInfos[i].mRules[j].mRegex, str,
								inputInfos[i].mRules[j].mError);
						if (!error.equals("")) {
							BaseHelper.recordWarningMsg(
									SubItemPucPayActivity.this, error);

							getDataHelper().showDialog(SubItemPucPayActivity.this,
									R.drawable.erroricon, getResources()
											.getString(R.string.ErrorString),
									error,
									getResources().getString(R.string.Ensure),
									null, null, null, null, null);
							return;
						}
					}
				}
			}
			mCurPage = BILL_OPTION;
			cmdToServer();
		}
	}

	/**
	 * 校验确认页面的输入框
	 */
	private void checkConfirmInputInfos(InputInfos[] inputInfos) {
		String inputMoney = mConfirmLayoutPaymentInput.getEtContent().getText().toString().trim();
		if (inputInfos != null && inputInfos.length > 0) {
			for (int i = 0; i < inputInfos.length; i++) {
//				String str = mConfirmLayoutPaymentInput.getEtContent().getText().toString().trim();
//				if (i == 1) {
//					str = mPucNumEditText.getText().toString().trim();
//				}
//				if(i == 2){
//					str = mPucNumEditText2.getText().toString().trim();
//				}
				if (inputInfos[i].mRules != null) {
					for (int j = 0; j < inputInfos[i].mRules.length; j++) {
						String error = checkItem(
								inputInfos[i].mRules[j].mRegex, inputMoney,
								inputInfos[i].mRules[j].mError);
						if (!error.equals("")) {
							BaseHelper.recordWarningMsg(
									SubItemPucPayActivity.this, error);

							getDataHelper().showDialog(SubItemPucPayActivity.this,
									R.drawable.erroricon, getResources()
											.getString(R.string.ErrorString),
									error,
									getResources().getString(R.string.Ensure),
									null, null, null, null, null);
							return;
						}
					}
				}
			}
		    getDataHelper().sendCreateBillNo(mHandler,
					AlipayHandlerMessageIDs.CREATE_BILL_NO, mBillId,inputMoney);
		    openProcessDialog(getResources().getString(R.string.NetBankGetInfo));
		}
	}
	
	
	private void showResult(Message msg) {
		try {
			Responsor responsor = (Responsor) msg.obj;
			// int resultData = responsor.status;
			JSONObject obj = responsor.obj;
//			JSONObject obj = new JSONObject("{\"agreementUrl\":\"https://mgw.alipay.com/puc/agreement.htm\",\"cities\":[\"北京\",\"成都\",\"哈尔滨\",\"海口\",\"杭州\",\"合肥\",\"呼和浩特\",\"济南\",\"连云港\",\"马鞍山\",\"南昌\",\"南京\",\"南通\",\"宁波\",\"青岛\",\"上海\",\"沈阳\",\"石家庄\",\"苏州\",\"泰州\",\"温州\",\"新余\",\"徐州\",\"扬州\",\"镇江\",\"郑州\",\"重庆\"],\"company\":\"杭州市燃气（集团）有限公司\",\"content\":\"{\\\"city\\\":\\\"杭州\\\",\\\"companies\\\":[{\\\"attention\\\":\\\"\\\",\\\"bigChargeBillUrl\\\":[\\\"https://img.alipay.com/sys/pucprod/style/sample/zjhzghrqgas.jpg\\\",\\\"https://img.alipay.com/sys/pucprod/style/sample/zjhzghrqgas.jpg\\\"],\\\"chargeBillUrl\\\":[\\\"https://img.alipay.com/sys/pucprod/style/sample/lf_zjhzghrqgas_thumb.jpg\\\"],\\\"inputInfos\\\":[{\\\"enable\\\":1,\\\"field\\\":\\\"billKey\\\",\\\"fieldType\\\":0,\\\"label\\\":\\\"用户号\\\",\\\"rules\\\":[{\\\"error\\\":\\\"用户号长度不能小于10。\\\",\\\"regex\\\":\\\".{10,100}\\\"},{\\\"error\\\":\\\"用户号长度不能超过10。\\\",\\\"regex\\\":\\\".{1,10}\\\"},{\\\"error\\\":\\\"用户号不能为空。\\\",\\\"regex\\\":\\\"isNotEmpty\\\"},{\\\"error\\\":\\\"用户号必须为10位数字。\\\",\\\"regex\\\":\\\"\\\\d{10}\\\"}],\\\"tip\\\":\\\"请输入用户号\\\"}],\\\"name\\\":\\\"杭州港华燃气有限公司(余杭区)\\\",\\\"status\\\":\\\"0\\\"},{\\\"attention\\\":\\\"\\\",\\\"bigChargeBillUrl\\\":[\\\"https://img.alipay.com/sys/pucprod/style/sample/hzqjgas.jpg\\\",\\\"https://img.alipay.com/sys/pucprod/style/sample/hzqjgas.jpg\\\"],\\\"chargeBillUrl\\\":[\\\"https://img.alipay.com/pa/img/thumb_null.jpg\\\"],\\\"inputInfos\\\":[{\\\"enable\\\":1,\\\"field\\\":\\\"billKey\\\",\\\"fieldType\\\":0,\\\"label\\\":\\\"用户号\\\",\\\"rules\\\":[{\\\"error\\\":\\\"用户号长度不能小于5。\\\",\\\"regex\\\":\\\".{5,100}\\\"},{\\\"error\\\":\\\"用户号长度不?艹?0。\\\",\\\"regex\\\":\\\".{1,10}\\\"},{\\\"error\\\":\\\"用户号不能为空。\\\",\\\"regex\\\":\\\"isNotEmpty\\\"},{\\\"error\\\":\\\"号码应为Q+4~9位数字\\\",\\\"regex\\\":\\\"Q\\\\d{4,9}\\\"}],\\\"tip\\\":\\\"请填写用户号\\\"}],\\\"name\\\":\\\"杭州钱江燃气\\\",\\\"status\\\":\\\"0\\\"},{\\\"attention\\\":\\\"\\\",\\\"bigChargeBillUrl\\\":[\\\"https://img.alipay.com/sys/pucprod/style/sample/hzgas.jpg\\\",\\\"https://img.alipay.com/sys/pucprod/style/sample/hzgas.jpg\\\"],\\\"chargeBillUrl\\\":[\\\"https://img.alipay.com/sys/pucprod/style/sample/lf_hzgas_thumb.jpg\\\"],\\\"inputInfos\\\":[{\\\"enable\\\":1,\\\"field\\\":\\\"billUserName\\\",\\\"fieldType\\\":0,\\\"label\\\":\\\"户名\\\",\\\"rules\\\":[{\\\"error\\\":\\\"户名不能为空。\\\",\\\"regex\\\":\\\"isNotEmpty\\\"}],\\\"tip\\\":\\\"请填写户名\\\"},{\\\"enable\\\":1,\\\"field\\\":\\\"billKey\\\",\\\"fieldType\\\":0,\\\"label\\\":\\\"编号\\\",\\\"rules\\\":[{\\\"error\\\":\\\"编号长度不能小于6。\\\",\\\"regex\\\":\\\".{6,100}\\\"},{\\\"error\\\":\\\"编号长度不能超过6。\\\",\\\"regex\\\":\\\".{1,6}\\\"},{\\\"error\\\":\\\"编号不能为空。\\\",\\\"regex\\\":\\\"isNotEmpty\\\"},{\\\"error\\\":\\\"编号必须为6位数字。\\\",\\\"regex\\\":\\\"\\\\d{6}\\\"}],\\\"tip\\\":\\\"请填写编号\\\"}],\\\"name\\\":\\\"杭州市燃气（集团）有限公司\\\",\\\"status\\\":\\\"0\\\"}]}\",\"historyValue\":\"{\\\"billKey\\\":\\\"072736\\\",\\\"billUserName\\\":\\\"张美娟\\\"}\",\"ipCity\":\"杭州\",\"memo\":\"操作成功。\",\"resultStatus\":100,\"serialVersionUID\":-8216049486162061508,\"sessionId\":\"43f79285374dc8d343e7a3b03f8061e1\"}");
//			boolean tResultOK = false;
			boolean needDismissProcessDialog = true;
			int status = responsor.status;
            String memo = responsor.memo;
//			tResultOK = mMessageFilter.process(msg);// 本次请求是否成功
			if ((status == 100) && (!getDataHelper().isCanceled())) {
				myJsonObject = obj;
				if (mCurPage == INPUT_INFO) {
					historyIcon.setVisibility(View.VISIBLE);
					parseGetPucInputInfo(myJsonObject);
					updateInputInfo();
				} else if (mCurPage == BILL_OPTION) {
					parseGetChargeBillsInfo(myJsonObject);

				} else if (mCurPage == BILL_CONFIRM) {
					parseConfirmBillInfo(myJsonObject);
					updateConfirmInfo();
					// mErrorType = CREATE_BILL_NO;
					// this.cmdToServer();
				} else if (mCurPage == CREATE_BILL_NO) {
					mBillNo = myJsonObject.optString(Constant.RPF_BILLNO);
					if (AGENT_REQUEST == true) {
						needDismissProcessDialog = true;

						// 代付需求，进入到代付页面
						Intent tIntent = new Intent();
						tIntent.setClass(SubItemPucPayActivity.this,
								AlipayAgentPay.class);
						tIntent.putExtra(Constant.FIELD_BILL_NO, mBillNo);
						tIntent.putExtra(Constant.RPF_BIZTYPE, "puc_charge");

						startActivityForResult(tIntent,
								Constant.REQUEST_CODE_THREE);
					} else {
						needDismissProcessDialog = false;
						BaseHelper.payDeal(SubItemPucPayActivity.this,
								mHandler, mProgress, mBillNo,
								getExtToken(), "", "trade",
								"puc_charge");
						mCurPage = NO_ERROR;
						// TODO 调用快捷支付
					}
				}
				// mErrorType = NO_ERROR;
			}else if(isHistorySelected){
//				historyIcon.setVisibility(View.GONE);
				getDataHelper().showDialog(this,
		                   0, getResources().getString(R.string.WarngingString), 
		                   (memo == null?"网络异常":memo), 
		                   getResources().getString(R.string.Ensure),
		                   new DialogInterface.OnClickListener() {
								@Override
								public void onClick(DialogInterface dialog, int which) {
									SubItemPucPayActivity.this.finish();
								}
								}, null, null, null, null);
				
				
				
			}else{
				 mMessageFilter.process(msg);
			}
			if (needDismissProcessDialog) {
				if (mProgress != null) {
					mProgress.dismiss();
					mProgress = null;
				}
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (requestCode == Constant.REQUEST_CODE_THREE) {
			// 代付页面返回
			AGENT_REQUEST = false;

			Message backMsg = mHandler
					.obtainMessage(AlipayHandlerMessageIDs.RQF_AGENT_PAY);
			if (resultCode == Activity.RESULT_OK) {
				backMsg.arg1 = 0;
			} else if (resultCode == Activity.RESULT_CANCELED) {
				// 保留当前页面
				// 失败，但是因为订单已经创建，所以需要跳转到消费记录页面
				backMsg.arg1 = 10;
			}
			mHandler.sendMessage(backMsg);
		}
		if (requestCode == Constant.START_PAYMENT_CITY
				&& resultCode == Activity.RESULT_OK) {
			if(getUserData() != null && getUserData().getMobileNo() != null && !getUserData().getMobileNo().equalsIgnoreCase("")){
				mSettingTimeCheckBox.setChecked(true);
				mSelectTime.setVisibility(View.VISIBLE);
			}else{
				mSettingTimeCheckBox.setChecked(false);
			}
			
			mSelectTime.setSelection(Integer.parseInt(mSystemDate)-1);
			isHistorySelected = false;
			mCurrentCityStr = data.getStringExtra(Constant.CLICK_CITY);
			mPucNameEditText.getEtContent().setHint("");
			mPucNumTip.setVisibility(View.GONE);
			mPucNumTip2.setVisibility(View.GONE);
			mCurrentInput = 0;
			mCurrentCompany = -1;
			mCurPage = INPUT_INFO;
			mCompany = null;
			mCompanyShort = null;
			mHistoryValue = null;
			mChargeInputType = null;
			cmdToServer();
			isFirst = false;
		}
		if (requestCode == Constant.REQUEST_CODE_QRCODE
				&& resultCode == Activity.RESULT_OK) {
			if (data.getData() != null) {
				String mGotPeerAccount = data.getData().toString();
				mPucNameEditText.setText(mGotPeerAccount);
				try {
					// 根据条码长度大于解析选择解析方式
					switch (mGotPeerAccount.length()) {
					case 24:
						mPucNumEditText.setText(Integer
								.parseInt(mGotPeerAccount.substring(16, 21))
								+ "." + mGotPeerAccount.substring(21, 23));
						break;
					case 25:
						mPucNumEditText.setText(Integer
								.parseInt(mGotPeerAccount.substring(16, 22))
								+ "." + mGotPeerAccount.substring(22, 24));
						break;
					case 32:
						mPucNumEditText.setText(Integer
								.parseInt(mGotPeerAccount.substring(20, 28))
								+ "." + mGotPeerAccount.substring(28, 30));
						break;
					case 34:
						mPucNumEditText.setText(Integer
								.parseInt(mGotPeerAccount.substring(22, 30))
								+ "." + mGotPeerAccount.substring(30, 32));
						break;
					default:
						break;
					}
				} catch (NumberFormatException e) {
					e.printStackTrace();
				}
			}
		}
		if(requestCode == Constant.START_LIFEPAY_HISTORY
		      && resultCode == Activity.RESULT_OK){
			//处理历史记录返回的结果
			Bundle historyBundle = data.getExtras();
			HistoryBillInfo historyBillInfo = (HistoryBillInfo) historyBundle.getSerializable(Constant.LIFEPAY_HISTORYINFO_KEY);
			//解析带回的缴费历史记录账号，主要是拿到城市，和之前的默认城市做对比
			if(historyBillInfo != null){
				if(historyBillInfo.isReminded.equalsIgnoreCase("0")){//设置过提醒，修改时间选择的值。
					mSelectTime.setSelection(Integer.parseInt(historyBillInfo.remindDate) - 1);					
				}else{
					mSelectTime.setSelection(Integer.parseInt(mSystemDate)-1);
				}
				isHistorySelected = true; 
				mCurPage = INPUT_INFO;
				mCompany = historyBillInfo.chargeCompanyName;
				mCompanyShort = historyBillInfo.chargeCompanySname;
				
				try {
					mHistoryValueJSON =  new JSONObject(historyBillInfo.originalData);
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				
				isFirst = false;
				mCurrentInput = 0;
				
    			if(!mCurrentCityStr.equalsIgnoreCase(historyBillInfo.city)){
    				mCurrentCityStr = historyBillInfo.city;
    				mCurrentCompany = -1;
    				mHistorySelectCompany = historyBillInfo.chargeCompanyName;
    				mHistorySelectCompanyShort = historyBillInfo.chargeCompanySname;
    				mHistoryValue  = new HashMap<String, String>();
    				mChargeInputType = null;
    				cmdToServer();
    			}else{
    				for(int i=0;i<mCityInfo.mCompanies.length;i++){
    					if(historyBillInfo.chargeCompanyName.equalsIgnoreCase(mCityInfo.mCompanies[i].mCompanyName)){
    						mCurrentCompany = i;
    					}
    				}
    				String historyvalue = historyBillInfo.originalData.toString();
					if (historyvalue != null
							&& !historyvalue.equals("")) {
						if (mCompanyShort
								.equals(mCityInfo.mCompanies[mCurrentCompany].shortName)) {
							boolean type = true;
							if (mChargeInputType != null
									&& !mChargeInputType.equals("")) {
								if (mChargeInputType.equals("1")) {
									type = false;
								} 
							} else if (mCityInfo.mCompanies[mCurrentCompany].mChargeType != null &&
									mCityInfo.mCompanies[mCurrentCompany].mChargeType.mDefaultselected != null
									&& mCityInfo.mCompanies[mCurrentCompany].mChargeType.mDefaultselected
											.length()>0) {
								if (mCityInfo.mCompanies[mCurrentCompany].mChargeType.mDefaultselected
										.equals("1")) {
									type = false;
								}
							} 
							if(type)
								setHistoryValue(mCityInfo.mCompanies[mCurrentCompany].mInputInfos);
							else
								setHistoryValue(mCityInfo.mCompanies[mCurrentCompany].mInputInfos1);
						}
					}
					updateInputInfo();
    			}
            }

		}
		if(requestCode == Constant.REQUEST_LOGIN_BACK
			  && resultCode == Activity.RESULT_OK){
			//处理从登陆返回。
		}
		if(requestCode == Constant.SETTING_TIME_CHECKBOX_REQUEST_LOGIN
				&& resultCode == Activity.RESULT_OK){
			//用户点击设置提醒时间的checkbox，未登陆跳登陆后返回后的结果。登陆后检查用户是否绑定了手机号码。
			if(getUserData() != null && getUserData().getMobileNo().equalsIgnoreCase("")){//登陆了但是用户没有绑定手机号
				getDataHelper().showDialog(SubItemPucPayActivity.this,
	                    R.drawable.infoicon, 
	                    getResources().getString(R.string.Hint), 
	                    getResources().getString(R.string.PhoneBindingNotice), 
	                    getResources().getString(R.string.Ensure), 
	                    new DialogInterface.OnClickListener() {
							@Override
							public void onClick(DialogInterface dialog, int which) {
								Intent intent = new Intent(SubItemPucPayActivity.this,AlipayPhoneBinding.class);
								startActivityForResult(intent, Constant.SETTING_TIME_CHECKBOX_REQUEST_BINDING_PHONE);
								}
							}, 
	                    getResources().getString(R.string.Cancel), 
	                    new DialogInterface.OnClickListener() {
							@Override
							public void onClick(DialogInterface dialog, int which) {
								mSettingTimeCheckBox.setChecked(false);
								}
							}, null, null);
				
			}else{
				mSettingTimeCheckBox.setChecked(true);
				mSelectTime.setVisibility(View.VISIBLE);
			}
		}
		if(requestCode == Constant.SETTING_TIME_CHECKBOX_REQUEST_BINDING_PHONE
				&& resultCode == Activity.RESULT_OK){//点击设置提醒时间的checkbox，绑定手机成功后，处理结果
			           mSettingTimeCheckBox.setChecked(true);
			           mSelectTime.setVisibility(View.VISIBLE);
				}
	}

	/**
	 * 解析信息录入的json信息 只有上海有缴费类型有缴费模型
	 * 
	 * @param myJsonObject
	 */
	private void parseGetPucInputInfo(JSONObject myJsonObject) {
		try {
			// 第一次进入产生的数据保存
			if (isFirst) {
				if (mChargeType.equals(Constant.URL_GAS_RATE)) {
					Constant.GAS_JSON = myJsonObject;
				} else if (mChargeType.equals(Constant.URL_WATER_RATE)) {
					Constant.WATER_JSON = myJsonObject;
				} else if (mChargeType.equals(Constant.URL_POWER_RATE)) {
					Constant.ELECTRIC_JSON = myJsonObject;
				} else if (mChargeType.equals(Constant.URL_COMMUN_RATE)) {
					Constant.COMMUN_JSON = myJsonObject;
				}
				// 默认显示的城市
				JSONArray jsonCity = myJsonObject
						.optJSONArray(Constant.RPF_CITIES);
				String lbsCity = myJsonObject.optString(Constant.IP_CITY);
				mCityList = new String[jsonCity.length() + 1];
				mCityList[0] = lbsCity;
				if (jsonCity != null) {
					for (int i = 0; i < jsonCity.length(); i++) {
						String cityStr = jsonCity.optString(i);
						this.mCityList[i + 1] = cityStr;
					}
				}
			}
			//获取服务器返回的当前日期，为默认缴费日期做准备。
			mSystemDate = myJsonObject.optString(Constant.SYSTEMDATE);
			if(mSystemDate == null || mSystemDate.equalsIgnoreCase("")){
				mSystemDate = "25";
			}
			
			// 默认显示的公司,如果是从历史选择了一条记录mCompany的值为选择的值,mHistoryValueJSON为选择的记录
			String historyvalue = "";
			if(!isHistorySelected){
				mCompanyShort = myJsonObject.optString(Constant.SHORTNAME,"");
				mCompany = myJsonObject.optString(Constant.COMPANY,"");
				  // 缴费历史内容 在content中的inputInfo中循环取得
				 historyvalue = myJsonObject
						.optString(Constant.RPF_HISTORYVALUE);
				if ( historyvalue != null && !historyvalue.equals("")) {
					mHistoryValue = new HashMap<String, String>();
					mHistoryValueJSON = new JSONObject(historyvalue);
				}
				isHistorySelected = false;
			}else {
				historyvalue = mHistoryValueJSON.toString();
//				mCurrentCityStr = mHistorySelectCompany;
			}
			// 默认的录入方式
			mChargeInputType = myJsonObject
					.optString(Constant.RQF_CHARGEINPUTTYPE);
			// 默认的缴费协议地址
			mAgreementUrl = myJsonObject.optString(Constant.AGREEMENT_URL);
			// 默认缴费输入信息
			mCityInfo = new CityInfo();
			{
				String tContent = myJsonObject.optString(Constant.RPF_CONTENT);
				if (tContent != null && !tContent.equals("")) {
					JSONObject itemObj = new JSONObject(tContent);
					if (itemObj != null) {
						// 默认缴费城市
						mCityInfo.mCity = itemObj.optString(Constant.RQF_CITY);
						if (mCityList != null) {
							mSelectPaymentCompanyAdapter.clear();
						}
						// 对应的缴费公司列表
						JSONArray jsonCompany = itemObj
								.getJSONArray(Constant.COMPANIES);
						if (jsonCompany != null) {
							mCityInfo.mCompanies = new CompanyInfo[jsonCompany
									.length()];
							for (int j = 0; j < jsonCompany.length(); j++) {
								mCityInfo.mCompanies[j] = new CompanyInfo();
								JSONObject companyObj = jsonCompany
										.optJSONObject(j);
								mCityInfo.mCompanies[j].mCompanyName = companyObj
										.optString(Constant.NAME);
								mCityInfo.mCompanies[j].shortName = companyObj
										.optString(Constant.SHORTNAME);
								/*if (mCompany
										.equals(mCityInfo.mCompanies[j].mCompanyName)) {
									mCurrentCompany = j;// 当前默认的缴费公司id
								}*///使用公司的缩写来做判断
								if (mCompanyShort
										.equals(mCityInfo.mCompanies[j].shortName)) {
									mCurrentCompany = j;// 当前默认的缴费公司id
								}
								//缴费公司缴费模式查询。
								String chargeQueryMode = companyObj.optString(Constant.CHARGE_MODE);
								if(chargeQueryMode != null && !chargeQueryMode.equalsIgnoreCase("")){
									mCityInfo.mCompanies[j].chargeQueryMode =  chargeQueryMode;
								}
								
								//这里拿历史中的公司和城市列表中的数据做对比。
								/*if (mHistorySelectCompany
										.equals(mCityInfo.mCompanies[j].mCompanyName)) {
									mCurrentCompany = j;// 当前默认的缴费公司id
								}*///由于公司名称会变化，但是公司的缩写是不会变化的，所以要是用公司缩写来判断
								if (mHistorySelectCompanyShort
										.equals(mCityInfo.mCompanies[j].shortName)) {
									mCurrentCompany = j;// 当前默认的缴费公司id
								}
								// 逐个将缴费公司给缴费spinner数据适配器
								mSelectPaymentCompanyAdapter
										.add(mCityInfo.mCompanies[j].mCompanyName);
								// 每个缴费公司的inputInfos
								JSONArray inputInfosArray = companyObj
										.optJSONArray(Constant.INPUTINFOS);
								if (inputInfosArray != null) {
									mCityInfo.mCompanies[j].mInputInfos = new InputInfos[inputInfosArray
											.length()];
									// 设置缴费公司相关信息
									setInputInfo(inputInfosArray,mCityInfo.mCompanies[j].mInputInfos);

								}

								//缴费确认界面的inputInfos
								JSONArray inputInfosConfirmArray = companyObj.optJSONArray(Constant.INPUTINFOSCONFIRM);
								if(inputInfosConfirmArray != null){
									mCityInfo.mCompanies[j].inputInfosConfirm =  new InputInfos[inputInfosConfirmArray.length()];
									setInputInfo(inputInfosConfirmArray,mCityInfo.mCompanies[j].inputInfosConfirm);
								}
								
								
								// 每个缴费公司的inputInfos1
								JSONArray inputInfosArray1 = companyObj
										.optJSONArray(Constant.INPUTINFOS1);
								if (inputInfosArray1 != null) {
									mCityInfo.mCompanies[j].mInputInfos1 = new InputInfos[inputInfosArray1
											.length()];
									// TODO:inputInfos1是什么信息
									setInputInfo(
											inputInfosArray1,
											mCityInfo.mCompanies[j].mInputInfos1);
								}

								// 缴费公司支持时间段提示
								String attention = companyObj
										.optString(Constant.ATTENTION);
								if (attention != null && !attention.equals("")) {
									mCityInfo.mCompanies[j].attention = attention;
								}
								
								
								String status = companyObj
										.optString(Constant.STATUS);
								if (status != null && !status.equals("")) {
									mCityInfo.mCompanies[j].status = status;
								}
								
								// 缴费公司缴费方式
								JSONObject chargeTypeObj = companyObj
										.optJSONObject(Constant.RQF_CHARGEINPUTTYPE);
								if (chargeTypeObj != null && !chargeTypeObj.equals("")) {
									mCityInfo.mCompanies[j].mChargeType = new ChargeInputType();
									// 默认选择的缴费方式
									mCityInfo.mCompanies[j].mChargeType.mDefaultselected = chargeTypeObj
											.optString(Constant.DEFAULTSELECTED);
									// 缴费方式
									JSONArray jsonOptions = chargeTypeObj
											.getJSONArray(Constant.OPTIONS);
									mCityInfo.mCompanies[j].mChargeType.mOptions = new Options[jsonOptions
											.length()];
									for (int n = 0; n < jsonOptions.length(); n++) {
										JSONObject optionsObj = jsonOptions
												.optJSONObject(n);
										if (optionsObj != null) {
											mCityInfo.mCompanies[j].mChargeType.mOptions[n] = new Options();
											mCityInfo.mCompanies[j].mChargeType.mOptions[n].mName = optionsObj
													.optString(Constant.NAME);
											mCityInfo.mCompanies[j].mChargeType.mOptions[n].mValue = optionsObj
													.optString(Constant.VALUE);
										}
									}
								}
								/**
								 * 存放HistoryValue值
								 */
								if (historyvalue != null
										&& !historyvalue.equals("")) {
									if (mCompanyShort
											.equals(mCityInfo.mCompanies[j].shortName)) {
										boolean type = true;
										if (mChargeInputType != null
												&& !mChargeInputType.equals("")) {
											if (mChargeInputType.equals("1")) {
												type = false;
											} 
										} else if (mCityInfo.mCompanies[j].mChargeType != null &&
												mCityInfo.mCompanies[j].mChargeType.mDefaultselected != null
												&& mCityInfo.mCompanies[j].mChargeType.mDefaultselected
														.length()>0) {
											if (mCityInfo.mCompanies[j].mChargeType.mDefaultselected
													.equals("1")) {
												type = false;
											}
										} 
										if(type)
											setHistoryValue(mCityInfo.mCompanies[j].mInputInfos);
										else
											setHistoryValue(mCityInfo.mCompanies[j].mInputInfos1);
									}
								}
							}
						}
					}
				}
			}
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	/**
	 * 设置缴费历史记录内容
	 * 
	 * @param mInputInfos
	 */
	private void setHistoryValue(InputInfos[] mInputInfos) {
		InputInfos currentInputInfo = null;
		String historyValue = "";
		if (mInputInfos != null && mInputInfos.length > 0) {
			for (int i = 0; i < mInputInfos.length; i++) {
				currentInputInfo = mInputInfos[i];
				if (currentInputInfo.mField != null
						&& currentInputInfo.mField.length()>0
						&& mHistoryValueJSON != null) {
					historyValue = mHistoryValueJSON
							.optString(currentInputInfo.mField);
					if (!historyValue.equals("")) {
						mHistoryValue
								.put(currentInputInfo.mField, historyValue);
					}
				}
			}
		}
	}

	/**
	 * 设置缴费公司相关信息
	 * 
	 * @param inputInfosArray
	 * @param mInputInfos
	 */
	private void setInputInfo(JSONArray inputInfosArray,
			InputInfos[] mInputInfos) {
		for (int i = 0; i < inputInfosArray.length(); i++) {
			JSONObject currentInputInfo = inputInfosArray.optJSONObject(i);
			mInputInfos[i] = new InputInfos();
			mInputInfos[i].mInputInfoKeyLabel = currentInputInfo
					.optString(Constant.LABEL);
			mInputInfos[i].mField = currentInputInfo.optString(Constant.FIELD);
			mInputInfos[i].mfieldType = currentInputInfo
					.optString(Constant.FIELD_TYPE);
			mInputInfos[i].mEnable = currentInputInfo
					.optString(Constant.ENABLE);
			JSONArray jsonRules = currentInputInfo.optJSONArray(Constant.RULES);
			if (jsonRules != null) {
				mInputInfos[i].mRules = new Rule[jsonRules.length()];
				for (int m = 0; m < jsonRules.length(); m++) {
					mInputInfos[i].mRules[m] = new Rule();
					JSONObject rulesObj = jsonRules.optJSONObject(m);
					mInputInfos[i].mRules[m].mRegex = rulesObj
							.optString(Constant.REGEX);
					mInputInfos[i].mRules[m].mError = rulesObj
							.optString(Constant.ERROR);
				}
			}
		}
	}

	/**
	 * 账单选择 缴费输入信息描述 账单选择规则按照服务端的指定的行为决定 selected 表示默认选中哪一个账单 canSelect
	 * 表示是否允许用户选择。0是不允许用户选择。 {“selected”:0,"lists",[{“billId”:1, “info”:
	 * “2010年11月 25.00元（其中包含滞纳金1.12元）”}，…], “canSelect”:0}
	 * 
	 * @param myJsonObject
	 */
	private void parseGetChargeBillsInfo(JSONObject myJsonObject) {
		try {
			mBillId = myJsonObject.optString(Constant.RPF_BILLID);
			// JSONObject billJson =
			// myJsonObject.optJSONObject(Constant.RPF_CONTENT);
			String content = myJsonObject.optString(Constant.RPF_CONTENT);
			if (content != null && !content.equals("") /*
														 * &&mCityInfo.mCompanies
														 * [mCurrentCompany].
														 * mChargeModel
														 * .equals("1")
														 */) {
				JSONObject billJson = new JSONObject(content);
				if (billJson != null) {
					// mSelected = billJson.optString(Constant.SELECTED);
					mCanSelect = billJson.optString(Constant.CANSELECT);
					JSONArray jsonContent = billJson
							.optJSONArray(Constant.LISTS);
					if (jsonContent != null) {
						mCBLists = new ChargeBillLists[jsonContent.length()];
						for (int i = 0; i < jsonContent.length(); i++) {
							JSONObject billObj = jsonContent.getJSONObject(i);
							mCBLists[i] = new ChargeBillLists();
							mCBLists[i].mBillId = billObj
									.optString(Constant.RPF_BILLID);
							mCBLists[i].mInfo = billObj
									.optString(Constant.RPF_INFO);
						}
					}
				}
				updateChargeBill();
				isDisplaySelected = true;
			} else {
				mCurPage = BILL_CONFIRM;
				isDisplaySelected = false;
				this.cmdToServer();
			}

		} catch (JSONException e) {
			e.printStackTrace();
		}
	}

	/**
	 * 账单确认 缴费输入信息描述
	 * {“chargeRegion”:”江苏-南京”,”chargeUnit”:”南京自来水公司”,[{“label”：”户名
	 * ”，”content”：”SHX”}, {“label”：”户号”，”content”：”123456789”}]
	 * “billInfo”:[”账单信息”，…],“chargeAmount”：”25元”}
	 * 
	 * @param myJsonObject
	 */
	private void parseConfirmBillInfo(JSONObject myJsonObject) {
		try {
			String content = myJsonObject.optString(Constant.RPF_CONTENT);
			if (content != null) {
				JSONObject billJson = new JSONObject(content);
				if (billJson != null && !content.equals("")) {
					mChargeRegion = billJson.optString(Constant.CHARGE_REGION);
					mChargeUnit = billJson.optString(Constant.CHARGE_UNIT);
					mChargeAmount = billJson.optString(Constant.CHARGE_AMOUNT);
					JSONArray billArray = billJson
							.optJSONArray(Constant.BILL_INFO);
					if (billArray != null) {
						StringBuffer sb = new StringBuffer();
						for (int i = 0; i < billArray.length(); i++) {
							sb.append(billArray.optString(i) + "\n");
						}
						if (sb.length() > 0) {
							mBillInfo = sb.toString();
						}
					}

					JSONArray jsonContent = billJson
							.optJSONArray(Constant.CHARGEUSERINFO);
					if (jsonContent != null) {
						mConfirmBLists = new ConfirmBillLists[jsonContent
								.length()];
						for (int i = 0; i < jsonContent.length(); i++) {
							mConfirmBLists[i] = new ConfirmBillLists();
							JSONObject billObj = jsonContent.getJSONObject(i);
							mConfirmBLists[i].mLabel = billObj
									.optString(Constant.LABEL);
							mConfirmBLists[i].mContent = billObj
									.optString(Constant.RPF_CONTENT);
						}
					}
				}
			}

		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private IntentFilter mNetworkStateChangedFilter;
	// private BroadcastReceiver mNetworkStateIntentReceiver;
	// PubPayViewer mPubViewer = null;
	boolean firstRunFlag = true;

	public void registerNetworkStateListener() {
		mNetworkStateChangedFilter = new IntentFilter();
		mNetworkStateChangedFilter
				.addAction(ConnectivityManager.CONNECTIVITY_ACTION);
	}



	private void cmdToServer() {
		String msg = getResources().getString(R.string.NetBankGetInfo);

		if (mCurPage == INPUT_INFO) {
			String key = null;
			if(params != null && !"".equals(params)){
				ParamString param = new ParamString(params);
				key = param.getValue("key");
				amount = param.getValue("amount");
			}
		    getDataHelper().sendGetPucInputInfo(mHandler,
					AlipayHandlerMessageIDs.INPUT_INFO, mChargeType,
					mCurrentCityStr,key);
		    openProcessDialog(msg);
		}
		if (mCurPage == BILL_OPTION) {
			JSONObject json = new JSONObject();
			
			try {
				// TODO 此处有可能需要修改，根据瑞华的新文档来做
				if (mCityInfo.mCompanies[mCurrentCompany].mChargeType != null) {
					json.put(
							Constant.RQF_CHARGEINPUTTYPE,
							mCityInfo.mCompanies[mCurrentCompany].mChargeType.mOptions[mCurrentInput].mValue);
				} else {
					json.put(Constant.RQF_CHARGEINPUTTYPE, "0");
				}
				json.put(Constant.COMPANY,
						mCityInfo.mCompanies[mCurrentCompany].mCompanyName);
				json.put(Constant.RQF_CITY, selectCity.getText());

				InputInfos[] tInput = null;
				if (mCurrentInput == 1) {
					tInput = mCityInfo.mCompanies[mCurrentCompany].mInputInfos1;
				} else {
					tInput = mCityInfo.mCompanies[mCurrentCompany].mInputInfos;
				}
				// for(int i = 0;i<tInput.length;i++)
				{
					json.put(tInput[0].mField, mPucNameEditText.getText()
							.toString().trim());// "271234567110210000001249");
					if (tInput.length == 2) {
						json.put(tInput[1].mField, mPucNumEditText.getText()
								.toString().trim());// "1.24");//
					}
					//新版代办模式不会显示三个输入款。
//					if(tInput.length ==3){
//						json.put(tInput[1].mField, mPucNumEditText.getText()
//								.toString().trim());// "1.24");//
//						json.put(tInput[2].mField, mPucNumEditText2.getText()
//								.toString().trim());// "1.24");//
//						//判断该公司是否是代办模式，如果是金额输入框的值，给确认页面使用。
//						if(mCityInfo.mCompanies[mCurrentCompany].chargeQueryMode.equalsIgnoreCase("7")){
//							mAgentPayChargeAmount = mPucNumEditText2.getEtContent().getText().toString().trim();
//						}
//						
//					}
					String remindStatus = mSettingTimeCheckBox.isChecked()?"0":"1";
					String date = remindStatus.equalsIgnoreCase("0")?(mDate + ""):"";
					json.put(Constant.RQF_REMINDSTATUS, remindStatus);
					json.put(Constant.RQF_REMINDDATE, date);
				}
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			getDataHelper().sendGetChargeBills(mHandler,
					AlipayHandlerMessageIDs.BILL_OPTION, mChargeType,
					json.toString());
			openProcessDialog(msg);
			
		}
		if (mCurPage == BILL_CONFIRM) {
		    getDataHelper().sendConfirmBillInfo(mHandler,
					AlipayHandlerMessageIDs.BILL_CONFIRM, mBillId);
		    openProcessDialog(msg);
		}
		if (mCurPage == CREATE_BILL_NO) {
			if(mConfirmLayoutPaymentInput.getVisibility() == View.VISIBLE ){
				checkConfirmInputInfos(mCityInfo.mCompanies[mCurrentCompany].inputInfosConfirm);
//				if(!inputMoney.matches(regularExpression)){
//                	getDataHelper().showDialog(this, -1, 
//                			getResources().getString(R.string.Error),
//                			getResources().getString(R.string.PaymentError),
//                			getResources().getString(R.string.Ensure), 
//                			new DialogInterface.OnClickListener() {
//								@Override
//								public void onClick(DialogInterface dialog, int which) {
//								}
//							}, null, null, null, null);
//				}else{
//				    getDataHelper().sendCreateBillNo(mHandler,
//							AlipayHandlerMessageIDs.CREATE_BILL_NO, mBillId,inputMoney);
//				    openProcessDialog(msg);
//				}
			}else{
			    getDataHelper().sendCreateBillNo(mHandler,
						AlipayHandlerMessageIDs.CREATE_BILL_NO, mBillId,"");
			    openProcessDialog(msg);
			}
		}
//		openProcessDialog(msg);

	}

	/**
	 * 更新生活缴费第一个界面
	 */
	private void updateInputInfo() {
		// 设置缴费城市列表
		if (mCityInfo.mCity != null) {
			selectCity.setText(mCityInfo.mCity);
		}
		// 设置默认缴费公司
		if (mCurrentCompany != -1) {
			mSelectPaymentCompany.setSelection(mCurrentCompany);
		} else {
			mCurrentCompany = 0;
			mSelectPaymentCompany.setSelection(0);
		}
		//设置公司的attention
		CompanyInfo currentCompanyInfo = mCityInfo.mCompanies[mCurrentCompany];
		mNext = (Button) findViewById(R.id.next);
		if("1".equals(currentCompanyInfo.status) && currentCompanyInfo.attention != null){
			mSelectPaymentCompanyTip.setText(currentCompanyInfo.attention);
			mSelectPaymentCompanyTip.setVisibility(View.VISIBLE);
			editTextHasNullChecker.removeButton(mNext);
			mNext.setEnabled(false);
		}else if("0".equals(currentCompanyInfo.status)){
			mSelectPaymentCompanyTip.setVisibility(View.GONE);
			editTextHasNullChecker.addNeedEnabledButton(mNext);
		}
		//设置缴费公司的代付模式下的提示。7为代办模式
		if("7".equalsIgnoreCase(currentCompanyInfo.chargeQueryMode)){//支持代办模式
			mAgentPayModeCompanyTip.setVisibility(View.VISIBLE);
		}else{                                                       
			mAgentPayModeCompanyTip.setVisibility(View.GONE);
		}
		
		// 此城市缴费方式有多种
		if (mCityInfo.mCompanies[mCurrentCompany].mChargeType != null
				) {
			int optionLength = mCityInfo.mCompanies[mCurrentCompany].mChargeType.mOptions.length;
			if (optionLength > 1)// 用户所需输入的信息有多种的话
			{
				mSelectPaymentWayAdapter.clear();
				for (int i = 0; i < optionLength; i++) {
					mSelectPaymentWayAdapter
							.add(mCityInfo.mCompanies[mCurrentCompany].mChargeType.mOptions[i].mName);
				}
				mSelectChargeWayLayout.setVisibility(View.VISIBLE);
				this.mSelectPaymentWay.setVisibility(View.VISIBLE);
				if (mChargeInputType != null && !mChargeInputType.equals("")) {
					mCurrentInput = Integer.parseInt(mChargeInputType);
					updatePaymentInfo(mCurrentInput);
				} else if (mCityInfo.mCompanies[mCurrentCompany].mChargeType.mDefaultselected != null
						&& !mCityInfo.mCompanies[mCurrentCompany].mChargeType.mDefaultselected
								.equals("")) {
					mCurrentInput = Integer
							.parseInt(mCityInfo.mCompanies[mCurrentCompany].mChargeType.mDefaultselected);
					updatePaymentInfo(mCurrentInput);
				}
				mSelectPaymentWay.setSelection(mCurrentInput);
			} else {
				mSelectChargeWayLayout.setVisibility(View.GONE);
				this.mSelectPaymentWay.setVisibility(View.GONE);
				mCurrentInput = 0;
				setPaymentInfo(mCityInfo.mCompanies[mCurrentCompany].mInputInfos);
				mSelectPaymentWay.setSelection(mCurrentInput);
			}
		} else {
			this.mSelectPaymentWay.setVisibility(View.GONE);
			mSelectChargeWayLayout.setVisibility(View.GONE);
			mCurrentInput = 0;
			setPaymentInfo(mCityInfo.mCompanies[mCurrentCompany].mInputInfos);
		}
		//缴费提醒设置的控制。
		if(mHistoryValueJSON != null){//最近的一次缴费值可能不包含Constant.BILL_ISREMINDED这个key（最近一次的记录json就不包含这个key）,所以可能取到空
			if(mHistoryValueJSON.optString(Constant.BILL_ISREMINDED) != null 
					&& mHistoryValueJSON.optString(Constant.BILL_ISREMINDED).equals("0")){
				mSettingTimeCheckBox.setChecked(false);
				mSelectTime.setSelection(Integer.parseInt(mHistoryValueJSON.optString(Constant.BILL_REMINDDATE))-1);
			}else{
				mSelectTime.setSelection(Integer.parseInt(mSystemDate)-1);
			}
		}else{
			mSelectTime.setSelection(Integer.parseInt(mSystemDate)-1);
		}
		
	}
	
	public void setChargeWayContent(){

		
	}

	/**
	 * 缴费方式信息
	 * 
	 * @param currentInput
	 */
	private void updatePaymentInfo(int currentInput) {
		if (currentInput == 0) {
			setPaymentInfo(mCityInfo.mCompanies[mCurrentCompany].mInputInfos);
		} else {
			setPaymentInfo(mCityInfo.mCompanies[mCurrentCompany].mInputInfos1);
		}
	}

	private boolean hasBarcodeStr(String str){
		if(str == null)
			return false;
		
		return str.contains(getText(R.string.hasBarcode1)) || str.contains(getText(R.string.hasBarcode2));
	}
	
	/**
	 * 设置缴费输入界面的信息
	 */
	private void setPaymentInfo(InputInfos[] inputInfos) {
		if (inputInfos != null && inputInfos.length > 0) {
			setEnable(inputInfos[0].mEnable, mPucNameId);
			mPucNameEditText.setInputName(inputInfos[0].mInputInfoKeyLabel + "：");
			
			//"浙江全省"固话宽带的特殊处理
			if(mCurrentCityStr.equalsIgnoreCase(getResources().getString(R.string.ZhejiangAll)) 
					                     && mChargeType.equalsIgnoreCase(Constant.URL_COMMUN_RATE)){
				mPucNumTip.setVisibility(View.VISIBLE);
			    if(mCompanyShort.equalsIgnoreCase("ZHEJIANGHANGZHOUUNICOM")){//所选公司为浙江联通
//			    	mPucNameEditText.getEtContent().setHint(R.string.PhoneNumWithoutAreaCode);
			    	mPucNumTip2.setVisibility(View.VISIBLE);
			    	mPucNumTip.setText(getResources().getString(R.string.ZhejiangUniTip1));
			    	mPucNumTip2.setText(getResources().getString(R.string.ZhejiangUniTip2));
			    }else if(mCompanyShort.equalsIgnoreCase("ZHEJIANGHANGZHOUTELECOMKD")){//所选为浙江电信公司（宽带）
			    	mPucNameEditText.getEtContent().setHint("");
			    	mPucNumTip.setText(getResources().getString(R.string.ZhejiangCtBroadbandTip));
			    	mPucNumTip2.setVisibility(View.GONE);
			    }else if(mCompanyShort.equalsIgnoreCase("ZHEJIANGHANGZHOUTELECOM")){//所选为浙江电信公司（固话）
			    	mPucNameEditText.getEtContent().setHint("");
			    	mPucNumTip.setVisibility(View.VISIBLE);
			    	mPucNumTip.setText(getResources().getString(R.string.ZhejiangCtPhoneTip));
			    	mPucNumTip2.setVisibility(View.GONE);
			    }
			}else if(mChargeType.equalsIgnoreCase(Constant.URL_COMMUN_RATE) 
					     && !mCurrentCityStr.equalsIgnoreCase(getResources().getString(R.string.ZhejiangAll))){
//				if(inputInfos[0].mInputInfoKeyLabel.contains(getResources().getString(R.string.PhoneNumber))){
//					mPucNameEditText.getEtContent().setHint(getResources().getString(R.string.PhoneNumWithoutAreaCode));
//				}else{ 需求更改，取消固话宽带非浙江全省的提示：（(请不要输入区号)）
					mPucNameEditText.getEtContent().setHint("");
//				}
				
			}
			
			if (hasBarcodeStr(inputInfos[0].mInputInfoKeyLabel)) {
				mScanButton.setVisibility(View.VISIBLE);
			} else {
				mScanButton.setVisibility(View.GONE);
			}
			setInputType(inputInfos, mPucNameEditText.getEtContent());
			BaseHelper.setDispayMode(mPucNameEditText.getEtContent(), null);
			// setTip(mPucNameTip,inputInfos[0].mLabelTip);
			setHistoryToTextview(inputInfos[0], mPucNameEditText.getEtContent());
			if (inputInfos.length == 2) {
				mPucNumEditText.setVisibility(View.VISIBLE);
				// mPucNumTip.setVisibility(View.VISIBLE);
				setEnable(inputInfos[1].mEnable, mPucNumEditText.getInputName());
				mPucNumEditText.setInputName(inputInfos[1].mInputInfoKeyLabel + "：");
				setInputType(inputInfos, mPucNumEditText.getEtContent());
				if(amount != null && !"".equals(amount)){//push金额填充
					mPucNumEditText.setText(amount);
					mPucNumEditText.getEtContent().setSelection(amount.length());
				}else
					setHistoryToTextview(inputInfos[1], mPucNumEditText.getEtContent());
				mPucNumEditText.requestFocus();
				mPucNumEditText2.setVisibility(View.GONE);
				mPucNumEditText2.setText("");
			} else if(inputInfos.length == 3){
				mPucNumEditText.setVisibility(View.VISIBLE);
				// mPucNumTip.setVisibility(View.VISIBLE);
				setEnable(inputInfos[1].mEnable, mPucNumEditText.getInputName());
				mPucNumEditText.setInputName(inputInfos[1].mInputInfoKeyLabel + "：");
				setInputType(inputInfos, mPucNumEditText.getEtContent());
				setHistoryToTextview(inputInfos[1], mPucNumEditText.getEtContent());
				mPucNumEditText.requestFocus();
				
				mPucNumEditText2.setVisibility(View.VISIBLE);
				// mPucNumTip.setVisibility(View.VISIBLE);
				setEnable(inputInfos[2].mEnable, mPucNumEditText2.getInputName());
				mPucNumEditText2.setInputName(inputInfos[2].mInputInfoKeyLabel + "：");
				setInputType(inputInfos, mPucNumEditText2.getEtContent());
				setHistoryToTextview(inputInfos[2], mPucNumEditText2.getEtContent());
				mPucNumEditText2.requestFocus();		
			}else {
				mPucNumEditText.setVisibility(View.GONE);
				mPucNumEditText.setText("");
				mPucNumEditText2.setVisibility(View.GONE);
				mPucNumEditText2.setText("");
				// mPucNumTip.setVisibility(View.GONE);
			}
		}
	}

	/**
	 * @param inputInfos
	 */
	private void setInputType(InputInfos[] inputInfos, EditText editText) {
		int inputType = InputType.TYPE_CLASS_TEXT;
		switch (Integer.parseInt(inputInfos[0].mfieldType)) {
		case 0:
			inputType = InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS;
			break;
		case 1:
		case 3:
			inputType = InputType.TYPE_CLASS_NUMBER
					| InputType.TYPE_NUMBER_FLAG_DECIMAL;
			break;

		}
		editText.setInputType(inputType);
	}

	/**
	 * 如果有历史记录设置响应的记录到输入框
	 * 
	 * @param inputInfos
	 * @param textView
	 */
	private void setHistoryToTextview(InputInfos inputInfos, EditText textView) {
		if (mCompanyShort != null
				&& !mCompanyShort.equals("")
				&& mCompanyShort
						.equals(mCityInfo.mCompanies[mCurrentCompany].shortName)
				&& mHistoryValue != null && !mHistoryValue.equals("")) {
			if (mChargeInputType != null && !mChargeInputType.equals("")) {
				int chargeInputType = Integer.parseInt(mChargeInputType);
				if (chargeInputType == mCurrentInput) {
					String value = mHistoryValue.get(inputInfos.mField);
					if (value != null && !value.equals("")) {
						textView.setText(value);
					} else {
						textView.setText("");
					}
				} else {
					textView.setText("");
				}
			} else if (mCityInfo.mCompanies[mCurrentCompany].mChargeType != null
					&& !mCityInfo.mCompanies[mCurrentCompany].mChargeType.equals("") &&
					!mCityInfo.mCompanies[mCurrentCompany].mChargeType.mDefaultselected
							.equals("")
					&& mCityInfo.mCompanies[mCurrentCompany].mChargeType.mDefaultselected != null) {
				int chargeInputType = Integer
						.parseInt(mCityInfo.mCompanies[mCurrentCompany].mChargeType.mDefaultselected);
				if (chargeInputType == mCurrentInput) {
					String value = mHistoryValue.get(inputInfos.mField);
					if (value != null && !value.equals("")) {
						textView.setText(value);
					} else {
						textView.setText("");
					}
				} else {
					textView.setText("");
				}
			} else {
				String value = mHistoryValue.get(inputInfos.mField);
				if (value != null && !value.equals("")) {
					textView.setText(value);
				} else {
					textView.setText("");
				}
			}
		} else {
			textView.setText("");
		}
		BaseHelper.setDispayMode(textView, null);
	}

	private void setEnable(String enable, TextView textView) {
		if (enable.equals("0")) {
			textView.setEnabled(false);
		} else {
			textView.setEnabled(true);
		}
	}

	/**
	 * 跟新账单界面
	 */
	private void updateChargeBill() {
		/*// 跳转到账单选择界面
		AlipayLogAgent.onPageJump(this,
				storageStateInfo.getValue(Constants.STORAGE_CURRENTVIEWID),
				viewType + "SelectListView",
				storageStateInfo.getValue(Constants.STORAGE_APPID),
				storageStateInfo.getValue(Constants.STORAGE_APPVERSION),
				storageStateInfo.getValue(Constants.STORAGE_PRODUCTID),
				getUserId(),
				storageStateInfo.getValue(Constants.STORAGE_PRODUCTVERSION),			
				storageStateInfo.getValue(Constants.STORAGE_CLIENTID));*/


		mTitleName.setText(R.string.SelectedBill);
		historyIcon.setVisibility(View.GONE);
		mPucChargeSelectedLayout.setVisibility(View.VISIBLE);
		mPucChargeRegionLayout.setVisibility(View.GONE);

		if (mCBLists != null && mCBLists.length > 0) {
			mPucChargeSelectedListViews.setAdapter(new BillAdapter());
		}
	}

	/**
	 * 确认信息界面
	 */
	private void updateConfirmInfo() {
		/*// 跳转到信息确认界面
		AlipayLogAgent.onPageJump(this,
				storageStateInfo.getValue(Constants.STORAGE_CURRENTVIEWID),
				viewType + "SubmitView",
				storageStateInfo.getValue(Constants.STORAGE_APPID),
				storageStateInfo.getValue(Constants.STORAGE_APPVERSION),
				storageStateInfo.getValue(Constants.STORAGE_PRODUCTID),
				getUserId(),
				storageStateInfo.getValue(Constants.STORAGE_PRODUCTVERSION),
				storageStateInfo.getValue(Constants.STORAGE_CLIENTID));*/


		mTitleName.setText(R.string.ConfirmBill);
		historyIcon.setVisibility(View.GONE);
		mPucChargeConfirmLayout.setVisibility(View.VISIBLE);
		mPucChargeRegionLayout.setVisibility(View.GONE);
		mPucChargeSelectedLayout.setVisibility(View.GONE);

		mRegionId.setText(mChargeRegion);
		mChargeCompanyId.setText(mChargeUnit);
		int length = mConfirmBLists.length;
		if (length > 0 && length < 3) {
			mChargeNameLayout.setVisibility(View.VISIBLE);
			mChargeName.setText(mConfirmBLists[0].mLabel + "：");
			mChargeNameContent.setText(mConfirmBLists[0].mContent);
			if (length > 1) {
				mChargeNumLayout.setVisibility(View.VISIBLE);
				mChargeNum.setText(mConfirmBLists[1].mLabel + "：");
				mChargeNumContent.setText(mConfirmBLists[1].mContent);
			}
		} else {
			mChargeNameLayout.setVisibility(View.GONE);
			mChargeNumLayout.setVisibility(View.GONE);
		}
		
		if (mBillInfo != null && mBillInfo.length() > 0) {
			mArrearsBillLayout.setVisibility(View.VISIBLE);
			mArrearsBillId.setText(mBillInfo);
		} else {
			mArrearsBillLayout.setVisibility(View.GONE);
		}
		
		if(mCityInfo.mCompanies[mCurrentCompany].chargeQueryMode.equalsIgnoreCase("7")){
			mPaymentId.setText(mAgentPayChargeAmount + getResources().getString(R.string.Yuan));
		}else{
			mPaymentId.setText(mChargeAmount + getResources().getString(R.string.Yuan));
		}
		
		if(mCityInfo.mCompanies[mCurrentCompany].inputInfosConfirm == null 
				|| mCityInfo.mCompanies[mCurrentCompany].inputInfosConfirm.length<1){
			editTextHasNullChecker.removeButton(mSubmit);
			mSubmit.setEnabled(true);
		}else{
			mConfirmLayoutPaymentInput.setVisibility(View.VISIBLE);
			mConfirmLayoutPaymentInput.getEtContent().setText("");
			mConfirmLayoutPaymentInput.clearFocus();
			mPaymentId.setText(mChargeAmount + getResources().getString(R.string.Yuan));
			mPaymentId.requestFocus();
			confirmeditTextHasNullChecker.addNeedCheckView(mConfirmLayoutPaymentInput.getEtContent());
			mConfirmLayoutPaymentInput.getEtContent().addTextChangedListener(confirmeditTextHasNullChecker);
			confirmeditTextHasNullChecker.addNeedEnabledButton(mSubmit);
			mSubmit.setEnabled(false);
		}
		
		if(mCityInfo.mCompanies[mCurrentCompany].chargeQueryMode.equalsIgnoreCase("7")){//支持代付模式
			mServiceChargeLayout.setVisibility(View.VISIBLE);
			mTotalPaymentLayout.setVisibility(View.VISIBLE);
			String.valueOf(Integer.parseInt(mAgentPayChargeAmount) + 1);
			mTotalPaymentId.setText(String.valueOf(Integer.parseInt(mAgentPayChargeAmount) + 1) + getResources().getString(R.string.Yuan));
		}else{
			mServiceChargeLayout.setVisibility(View.GONE);
			mTotalPaymentLayout.setVisibility(View.GONE);
		}
		
	}

	DialogInterface.OnClickListener PhoneInputOk = new DialogInterface.OnClickListener() {
		@Override
		public void onClick(DialogInterface dialog, int which) {
			// TODO Auto-generated method stub
			// AlipayMain.startMain(SubItemPucPayActivity.this);
			BaseHelper.showDesktop(SubItemPucPayActivity.this);
		}
	};

	private final class ScanButtonListener implements OnClickListener {
		@Override
		public void onClick(View v) {
			Intent intentQRcode = new Intent(SubItemPucPayActivity.this,
					com.google.zxing.client.BarcodeCaptureActivity.class);
			intentQRcode.putExtra(Constant.BARCODE_CAPTURE_TYPE,
					Constant.BARCODE_CAPTURE_TYPE_PUBCHARGE);
			intentQRcode.putExtra(Constant.BARCODE_CAPTURE_HINT, getResources().getString(R.string.scanAccountCode));
			startActivityForResult(intentQRcode, Constant.REQUEST_CODE_QRCODE);// 以传递参数的方式跳转到下一个Activity
		}
	}

	private class BillAdapter extends BaseAdapter {

		@Override
		public int getCount() {
			// TODO Auto-generated method stub
			return mCBLists.length;
		}

		@Override
		public Object getItem(int position) {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public boolean isEnabled(int position) {
			if (mCanSelect.equals("0")) {
				if (position != 0) {
					return false;
				}
			}
			return true;
		}

		@Override
		public long getItemId(int position) {
			// TODO Auto-generated method stub
			return position;
		}

		TextView info;
		LinearLayout tSettingItemLayout;

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			if (convertView == null) {
				convertView = LayoutInflater.from(SubItemPucPayActivity.this)
						.inflate(R.layout.alipay_setting_iteminfo, null);
				tSettingItemLayout = (LinearLayout) convertView
						.findViewById(R.id.SettingItemLayout);
				info = (TextView) convertView.findViewById(R.id.ItemTitleInfo);
				// item.uiActMsg = (TextView)
				// convertView.findViewById(R.id.CardInfoItemState);
				convertView.setTag(tSettingItemLayout);
			} else {
				tSettingItemLayout = (LinearLayout) convertView.getTag();
				info = (TextView) tSettingItemLayout
						.findViewById(R.id.ItemTitleInfo);
			}
			if (mCBLists != null) {

				info.setText(mCBLists[position].mInfo);
			}

			if (position != 0) {
				if (mCanSelect.equals("0")) {
					tSettingItemLayout
							.setBackgroundResource(R.drawable.list_enable);
				} else {
					tSettingItemLayout
							.setBackgroundResource(R.drawable.alipay_list_iteminfo_bg);
				}
			} else {
				tSettingItemLayout
						.setBackgroundResource(R.drawable.alipay_list_iteminfo_bg);
			}

			return convertView;
		}
	}

	@Override
	public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
		mBillId = mCBLists[arg2].mBillId;
		mCurPage = BILL_CONFIRM;
		this.cmdToServer();
	}


	/*private void jumpToHome() {
        if (getUserData()!=null) {
//            setTabIndex(Main.TAB_INDEX_DEFAULT);
            Intent intent = new Intent();
            intent.setClass(mContext, MainActivity.class);
            startActivity(intent);
        }
		this.finish();
	}*/

	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			if (mPucChargeConfirmLayout.getVisibility() == View.VISIBLE) {
				// 水电煤确认页面返回
				logReturn(viewType + "SubmitView");

				mPucChargeConfirmLayout.setVisibility(View.GONE);
				if (isDisplaySelected) {
					mPucChargeSelectedLayout.setVisibility(View.VISIBLE);
					mTitleName.setText(R.string.SelectedBill);
					historyIcon.setVisibility(View.GONE);
					// 更新当前界面
					storageStateInfo.putValue(Constants.STORAGE_CURRENTVIEWID,
							viewType + "SelectListView");
				} else {
					setTitle();
					mPucChargeRegionLayout.setVisibility(View.VISIBLE);

					storageStateInfo.putValue(Constants.STORAGE_CURRENTVIEWID,
							viewType + "InputView");
				}
			} else if (mPucChargeSelectedLayout.getVisibility() == View.VISIBLE) {
				logReturn(viewType + "SelectListView");

				setTitle();
				mPucChargeSelectedLayout.setVisibility(View.GONE);
				mPucChargeRegionLayout.setVisibility(View.VISIBLE);

				storageStateInfo.putValue(Constants.STORAGE_CURRENTVIEWID,
						viewType + "InputView");
			} else if (mView.getVisibility() == View.VISIBLE) {
				mView.loadData("", "text/html", "utf-8");
				mView.setVisibility(View.GONE);
				setTitle();
				mPucChargeRegionLayout.setVisibility(View.VISIBLE);
				historyIcon.setVisibility(View.VISIBLE);

				storageStateInfo.putValue(Constants.STORAGE_CURRENTVIEWID,
						viewType + "InputView");
				return true;
			} else {
				logReturn(viewType + "InputView");
//				jumpToHome();
				this.finish();
			}
			return true;
		}
		return false;
	}

	private void logReturn(String viewId) {
		// 记录界面的返回事件
		/*// 记录界面的返回事件
		AlipayLogAgent.onEvent(this, Constants.MONITORPOINT_EVENT_VIEWRETURN,
				"", viewId, storageStateInfo.getValue(Constants.STORAGE_APPID),
				storageStateInfo.getValue(Constants.STORAGE_APPVERSION),
				storageStateInfo.getValue(Constants.STORAGE_PRODUCTID),
				getUserId(),
				storageStateInfo.getValue(Constants.STORAGE_PRODUCTVERSION),
				storageStateInfo.getValue(Constants.STORAGE_CLIENTID));*/
		if(mCurPage == INPUT_INFO){
			AlipayLogAgent.writeLog(this, Constants.BehaviourID.CLICKED, null, null, appID, null, viewType + "InputView", null, "backIcon", null);
		}else{
			AlipayLogAgent.writeLog(this, Constants.BehaviourID.CLICKED, null, null, appID, null, viewType + "SubmitView", viewType + "InputView", "backIcon", null);
		}
	}

	// add by renxl on 2010-08-26
	private void setTitle() {
		if (mChargeType == null) {
			mTitleName.setText(R.string.CashRegisterTitle);
			return;
		}

		if (mChargeType.equals(Constant.URL_GAS_RATE)) {
			mTitleName.setText(R.string.GasRate);
		} else if (mChargeType.equals(Constant.URL_WATER_RATE)) {
			mTitleName.setText(R.string.WaterRate);
		} else if (mChargeType.equals(Constant.URL_POWER_RATE)) {
			mTitleName.setText(R.string.PowerRate);
		} else if (mChargeType.equals(Constant.URL_COMMUN_RATE)) {
			mTitleName.setText(R.string.PhoneAndWideBand);
		} else {
			mTitleName.setText(R.string.CashRegisterTitle);
		}
		historyIcon.setVisibility(View.VISIBLE);
	}

	/**
	 * 隐藏输入法
	 */
	private void hideInput(EditText editText) {
		editText.clearFocus();
		InputMethodManager imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
		imm.hideSoftInputFromWindow(editText.getWindowToken(), 0);
	}

	private void closeProgress() {
		if (mProgress != null) {
			mProgress.dismiss();
			mProgress = null;
		}
	}
}
