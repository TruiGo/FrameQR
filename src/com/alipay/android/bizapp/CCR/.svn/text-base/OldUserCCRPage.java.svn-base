package com.alipay.android.bizapp.CCR;

import java.util.ArrayList;
import java.util.HashMap;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.text.Editable;
import android.text.Html;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.ToggleButton;

import com.alipay.android.bizapp.BaseBiz;
import com.alipay.android.bizapp.BasePage;
import com.alipay.android.client.AlipayApplication;
import com.alipay.android.client.SchemeHandler;
import com.alipay.android.client.constant.AlipayHandlerMessageIDs;
import com.alipay.android.client.constant.Constant;
import com.alipay.android.client.partner.PartnerInfo;
import com.alipay.android.client.util.AlipayDataStore;
import com.alipay.android.comon.component.EditTextWithButton;
import com.alipay.android.comon.component.ThreeSMSListView;
import com.alipay.android.log.AlipayLogAgent;
import com.alipay.android.log.Constants;
import com.alipay.android.log.Constants.BehaviourID;
import com.alipay.android.ui.bean.BankCardInfo;
import com.alipay.android.ui.pushwebView.PushWebViewRootControllerActivity;
import com.eg.android.AlipayGphone.R;

public class OldUserCCRPage extends BasePage {

	private CCRActivity mContext = null;
	
	private LinearLayout tabLayout = null;//tab
	private Button mTabButton = null;// 老用户还款Tab
	private Spinner selectedBank = null;// 已存信用卡列表
	private TextView ccrTimeTip = null;
	private EditTextWithButton mCountEdit = null;
	private TextView mBankSearchTelephone = null;
	private TextView mBankPhone = null;
	private Button mNextBtn = null;

	private ArrayList<CCRBankCardInfo> mSavedBank = null;// 已存一行列表
	private int currentSelectedBankItem = 0;// 当前选中银行列表id

	private Button mTabNewButton;

	private ThreeSMSListView smsList = null;
	private ToggleButton mSMSSelect = null;

	private AlipayDataStore mAlipayDataStore;

	private LinearLayout ccrPhoneTip = null;// 银行客服电话layout

	private CCRBankCardInfo currentBankItemInfo;//当前选中银行信息
	
	private String cardIndexNumber = null;//从卡列表过来的还款
	private boolean isRefresh = true;//从卡列表过来最多刷一次
	/**
	 * 从卡列表过来的卡存在的位置
	 * 本地已存卡列表3个月后或有新卡还款后更新，如果同主站同步的信用卡已存卡不在本地已存卡列表，去同步一次。
	 */
	private int existLocation = -1;

	private AlipayApplication application;

	private CCRBankCardInfo currentCCRBankCardInfo ;
	
	// 无线体验金
	private ImageView mTiyanjin;
//	private static final String sUrl = "http://wap.alipay.com/clientTowapLogin.htm?goto=http%3A%2F%2Ffun.alipay.com%2Fccrmobile%2Findex.htm%3Fsource%3Dapp%26cardNo=%26bank=%26holderName=&ct=1";
	private static final String sUrl = "http://fun.alipay.com/qianbaotyj/index.htm?source=app&cardNo=&bank=&holderName=&ct=1";

	ImageView mSendBankMsgIcon;
	ImageView mBankPhoneIcon;
	
	public OldUserCCRPage(Activity context) {
		mContext = (CCRActivity) context;
	}

	@Override
	public void onCreate(BaseBiz bizObj) {
		super.onCreate(bizObj);
		mPageView = (FrameLayout) mContext.getLayoutInflater().inflate(
				R.layout.baseapp_layout, null);
		View view = View.inflate(mContext, R.layout.page_ccr_olduser, null);
		mPageView.addView(view);
		mAlipayDataStore = new AlipayDataStore(mContext);
		application  = (AlipayApplication) mContext.getApplicationContext();
		loadAllVariables();
	}

	@Override
	public void onResume() {
		processFocus();
		
		if(application.isSavedCCRBankRefresh()){
			mContext.sendGetBankListRequest();
		}else{
			mSavedBank = BizCCRUtil.getSavedBank(mContext);
			if(mSavedBank != null && mSavedBank.size() > 0){
//				setSelectBankList(mSavedBank, 0);
				setSelectBankAdapter(mSavedBank);
				loadPaymentInfo();
				
			}
		}
			
	}
	/**
	 * 加载还款信息
	 */
	private void loadPaymentInfo() {
		currentCCRBankCardInfo = (CCRBankCardInfo)params;
		if(currentCCRBankCardInfo != null){//内部业务  push  第三方调用 带入信息
			BankCardInfo bankCardInfo = currentCCRBankCardInfo.getBankCardInfo();
			
			cardIndexNumber  = currentCCRBankCardInfo.getCardIndexNumber();
			if(cardIndexNumber != null && cardIndexNumber.length() > 0 ){
				setBankSelected(cardIndexNumber);
			}else if(bankCardInfo != null && bankCardInfo.getCardTailNumber() != null && bankCardInfo.getBankMark() != null && bankCardInfo.getUserName() != null){
				outsideEvoke(currentCCRBankCardInfo);
			}else{
				insideEvoke();
			}
		}else{
			insideEvoke();
		}
	}

	/**
	 * 特殊焦点处理
	 */
	private void processFocus(){
		View focus = mContext.getCurrentFocus();
		 if (focus != null && focus instanceof EditText) {
	           focus.clearFocus();// 要先清除之前的焦点，当再有焦点时，才会弹出键盘，
	           ((EditText)focus).setSelection(((EditText)focus).length());
	        }
		mPageView.invalidate();
		mPageView.requestFocus();
		if(mCountEdit != null){
			mCountEdit.requestFocus();
		}
	}
	
	private void insideEvoke(){
		if(mSavedBank != null && mSavedBank.size() > 0){
			String theLastTimeSuccessCardIndexNumber = mAlipayDataStore
					.getString(
							AlipayDataStore.THE_LAST_TIME_SUCCESS_CARD_INDEX_NUMBER,
							"0");
			setBankSelected(theLastTimeSuccessCardIndexNumber);
		}
	}
	
	private void outsideEvoke(CCRBankCardInfo ccrBankCardInfo) {
		PartnerInfo partnerInfo = ccrBankCardInfo.getPartnerInfo();
		int selectedId = -1;
		selectedId = matchBankCard(ccrBankCardInfo);
		if(selectedId == -1){
			mContext.mNewUserPage.init(ccrBankCardInfo);
			mContext.JumptoPage(mContext.PAGE_ID_NEWUSER, true);
			params = null;
		}
	}

	private int matchBankCard(CCRBankCardInfo ccrBankCardInfo) {
		int selectedIndex = -1;
		if(mSavedBank!=null){
			CCRBankCardInfo bank = null;
			BankCardInfo bankCardInfo = ccrBankCardInfo.getBankCardInfo();
			if(bankCardInfo.getCardTailNumber() != null && bankCardInfo.getBankMark() != null && bankCardInfo.getUserName() != null){
				for(int i=0;i<mSavedBank.size();i++){
					bank = mSavedBank.get(i);
					if (bankCardInfo.getCardTailNumber().equalsIgnoreCase(bank.getCreditCardTailNumber()) 
							&& bankCardInfo.getBankMark().equalsIgnoreCase(bank.getBankCardInfo().getBankMark())
							&& bankCardInfo.getUserName().equalsIgnoreCase(bank.getBankCardInfo().getUserName())) {
						selectedIndex = i;
						selectedBank.setSelection(i);
						mCountEdit.setText(ccrBankCardInfo.getAmount());
						break;
					}
				}
			}
		}
		return selectedIndex;
	}

	/**
	 * 获取�?��缴费成功的银行信息ID
	 * 
	 * @return
	 */
	public int getRecentlySuccessBankItemInfoId(String cardIndexNumber) {
		int currentBankItemInfoId = 0;
		if (mSavedBank != null) {
			for (int i = 0; i < mSavedBank.size(); i++) {
				if (cardIndexNumber.equals(mSavedBank.get(i)
						.getCardIndexNumber())) {
					currentBankItemInfoId = i;
				}
			}
		}
		return currentBankItemInfoId;
	}

	@Override
	public void loadAllVariables() {
		mSendBankMsgIcon = (ImageView)mPageView.findViewById(R.id.SendBankMsgIcon);
		mSendBankMsgIcon.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View v) {
				if (currentBankItemInfo == null) {
					return;
				}
				// 用户输入为空，或者用户输入卡号不合法 现在确定是
				CCRBankTelMsg bankTelMsg = null;
				bankTelMsg = CCRBankTelMsg.getEnumByCode(currentBankItemInfo.getBankCardInfo()
						.getBankMark());
				try {
					if (bankTelMsg != null) {
						mContext.sendCCRSMSMsg(bankTelMsg,bankTelMsg.getMsg().replace("$",currentBankItemInfo.getBankCardInfo().getCardTailNumber()).replace("@","卡号"));
					}
					AlipayLogAgent.writeLog(mContext,BehaviourID.CLICKED, Constants.APPID_CCR,"-", Constants.VIEWID_CCR_OLD,  Constants.SEEDID_SMSQUERY);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
    	
		// 设置page title
		TextView titleName = (TextView) mPageView.findViewById(R.id.title_text);
		titleName.setText(R.string.CCRApp);
		ImageView titleShadow = (ImageView)mPageView.findViewById(R.id.TitleShadow);
		titleShadow.setVisibility(View.GONE);
		// 将“我的信用卡”设置为选中状态
		tabLayout = (LinearLayout)mPageView.findViewById(R.id.TabLayout);
		mTabButton = (Button) mPageView.findViewById(R.id.OldUserButton);
		mTabButton.setSelected(true);
		mTabNewButton = (Button) mPageView.findViewById(R.id.NewUserButton);
		mTabNewButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				mContext.JumptoPage(mContext.PAGE_ID_NEWUSER, true);
				AlipayLogAgent.writeLog(mContext,BehaviourID.CLICKED, Constants.APPID_CCR,  Constants.VIEWID_CCR_NEW,Constants.VIEWID_CCR_OLD, Constants.SEEDID_CREATICON);
			}
		});
		// 动态获取存在本地的还款信息
		selectedBank = (Spinner) mPageView.findViewById(R.id.SelectBank);
		selectedBank.setOnItemSelectedListener(new OnItemSelectedListener() {

			@Override
			public void onItemSelected(AdapterView<?> arg0, View arg1,
					int arg2, long arg3) {
				currentSelectedBankItem = arg2;
				currentBankItemInfo = mSavedBank.get(arg2);
				setOtherInfo(currentBankItemInfo);
				AlipayLogAgent.writeLog(mContext,BehaviourID.CLICKED, Constants.APPID_CCR, Constants.VIEWID_CCR_OLD, Constants.VIEWID_CCR_OLD, Constants.SEEDID_CLICKOLDCARD);
			}

			@Override
			public void onNothingSelected(AdapterView<?> arg0) {
				// TODO Auto-generated method stub

			}
		});
		ccrTimeTip = (TextView) mPageView.findViewById(R.id.CCRTimeTip);
		smsList = (ThreeSMSListView) mPageView.findViewById(R.id.SMSListView);
		// 还款金额输入�?
		mCountEdit = (EditTextWithButton) mPageView.findViewById(R.id.CCRCount);
		// 自动获取短信账单 TODO
		// 呼叫银行电话
		mSMSSelect = (ToggleButton) mPageView.findViewById(R.id.SMSBtn);
		mSMSSelect.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				boolean isAuthorize = mAlipayDataStore.getBoolean(Constant.IS_AUTHORIZE_SMS_QUERY, false);
				if(isAuthorize){
					showSmsList();
				}else{
					mSMSSelect.setChecked(false);
					mContext.getDataHelper().showDialog(mContext, R.drawable.infoicon, mContext.getResources().getString(R.string.StrConfirmPayTitle), mContext.getResources().getString(R.string.AuthorizeSmsQuery),
							mContext.getResources().getString(R.string.Allow),
							new DialogInterface.OnClickListener() {
								@Override
								public void onClick(DialogInterface dialog, int which) {
									mAlipayDataStore.putBoolean(Constant.IS_AUTHORIZE_SMS_QUERY, true);
									showSmsList();
								}
							},
							mContext.getResources().getString(R.string.Cancel), 
							new DialogInterface.OnClickListener() {
								@Override
								public void onClick(DialogInterface dialog, int which) {
									mAlipayDataStore.putBoolean(Constant.IS_AUTHORIZE_SMS_QUERY, false);
								}
							},null, null);
				}
				AlipayLogAgent.writeLog(mContext,BehaviourID.CLICKED, Constants.APPID_CCR, "-",Constants.VIEWID_CCR_OLD,  Constants.SEEDID_BILLQUERY);
			}
		});
		// �������е绰
		ccrPhoneTip = (LinearLayout) mPageView.findViewById(R.id.CCRPhoneTip);
		mBankSearchTelephone = (TextView) mPageView
				.findViewById(R.id.BankSearchTelephone);
		mBankPhoneIcon =(ImageView)mPageView.findViewById(R.id.BankPhoneImage);
		mBankPhone = (TextView) mPageView.findViewById(R.id.BankPhone);
		mBankPhoneIcon.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
			    String mobile = mBankPhone.getText().toString();
	            if(mobile.matches("^[0-9]*$")){
	               // 生成呼叫意图
				Intent intent = new Intent(Intent.ACTION_DIAL, Uri.parse("tel:"
	                     + mobile));
	               // 开始呼叫
	               mContext.startActivity(intent);
	            }
	            //拨打电话埋点数据
	            AlipayLogAgent.writeLog(mContext,BehaviourID.CLICKED, Constants.APPID_CCR,"-",  Constants.VIEWID_CCR_OLD, Constants.SEEDID_PHONEQUERY);
			}
		});

		mNextBtn = (Button) mPageView.findViewById(R.id.next);
		mNextBtn.setEnabled(false);
		mNextBtn.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				if(mSavedBank != null && mSavedBank.size()!=0){
					sendCreateCreditCardTrade();
					AlipayLogAgent.writeLog(mContext,BehaviourID.SUBMITED, Constants.APPID_CCR,  "-",Constants.VIEWID_CCR_OLD, Constants.SEEDID_NEXTBUTTON);
				}
			}
		});
		// 检测焦点机制
		mCountEdit.getEtContent().addTextChangedListener(new TextWatcher() {

			@Override
			public void afterTextChanged(Editable s) {
				// TODO Auto-generated method stub
				if (s.length() == 0) {
					mCountEdit.setButtonVisiable(false);// 隐藏按钮
				} else {
					mCountEdit.setButtonVisiable(true);// 显示按钮
				}
				String money = mCountEdit.getText();
				if (money.length() > 0) {
					mNextBtn.setEnabled(true);
				} else {
					mNextBtn.setEnabled(false);
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
				// TODO Auto-generated method stub
				try {
					if (s.length() > 0) {
						String tString = s.toString().trim();
						if ((tString.length() > 0)) {
							int dotPos = tString.indexOf('.');
							if (dotPos != -1) {
								if (tString.length() > dotPos + 3) {
									tString = tString.substring(0, dotPos + 3);
									mCountEdit.setText(tString);
									mCountEdit.getEtContent().setSelection(
											tString.length());
								}
								if (dotPos == 0) {
									tString = "0" + tString;
									mCountEdit.setText(tString);
									mCountEdit.getEtContent().setSelection(
											tString.length());
								}

							}
						}
					}

				} catch (Exception e) {
					e.printStackTrace();
				}
			}

		});
		
		mTiyanjin = (ImageView) mPageView.findViewById(R.id.CCRtiyanjin);
		if (!mAlipayDataStore.getBoolean(mContext.getUserId(), false)) {
			mTiyanjin.setVisibility(View.VISIBLE);
			mTiyanjin.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					Intent intent = new Intent();
		            intent.putExtra(SchemeHandler.PARAM_TITLE,mContext.getResources().getString(R.string.CCRtiyanjin));
		            intent.putExtra(SchemeHandler.PARAM_URL, sUrl+"&extern_token="+mContext.getExtToken());
		            intent.setClass(mContext, PushWebViewRootControllerActivity.class);
		            mContext.startActivity(intent);
				}
			});
		}
	}
	/**
	 * 短信列表的显示
	 */
	protected void showSmsList() {
		if (smsList.isShown()) {
			smsList.setVisibility(View.GONE);
			mSMSSelect.setChecked(false);
		} else {
			mSMSSelect.setChecked(true);
			smsList.show(currentBankItemInfo.getBankCardInfo().getBankMark());
		}
		mCountEdit.setFocusable(true);
		((InputMethodManager) mContext
				.getSystemService(Context.INPUT_METHOD_SERVICE))
				.toggleSoftInput(0, InputMethodManager.HIDE_NOT_ALWAYS);
	}
	
	
	private void setSelectBankAdapter(ArrayList<CCRBankCardInfo> savedBank){
		BankNameAdapter oldUserInfoAdapter = new BankNameAdapter(mContext,
				savedBank);
		selectedBank.setAdapter(oldUserInfoAdapter);
	}

	/**
	 * 设置界面其他组件显示内容
	 * 
	 * @param currentBankItemInfo
	 */
	private void setOtherInfo(CCRBankCardInfo currentBankItemInfo) {
		// 到账时间显示
		String timeTip = currentBankItemInfo.getArriveDatetime();
		if (!timeTip.equals("")) {
			ccrTimeTip.setVisibility(View.VISIBLE);
			ccrTimeTip.setText(timeTip);
		}
		// 银行客服电话
		String bankName = currentBankItemInfo.getBankCardInfo().getBankName();
		String phone = currentBankItemInfo.getPhoneNumber();
		if (!bankName.equals("") && !phone.equals("")) {
			ccrPhoneTip.setVisibility(View.VISIBLE);
//			String bankTel = String.format(
//					mContext.getResources().getString(R.string.BankTelDes),
//					bankName);
			mBankSearchTelephone.setText(mContext.getResources().getString(R.string.BankTelDes));
			mBankPhone.setText(Html.fromHtml("<u>" + phone + "</u>"));
		}
		if(phone!=null&&(phone.trim().length()>0)&&(!phone.equalsIgnoreCase("暂无电话"))){
			mBankPhoneIcon.setVisibility(View.VISIBLE);
		}else{
			mBankPhoneIcon.setVisibility(View.GONE);
		}
		setSMSButtonIsShow(currentBankItemInfo);
		
		if (CCRBankTelMsg.getEnumByCode(currentBankItemInfo.getBankCardInfo()
				.getBankMark()) != null) {
			mSendBankMsgIcon.setVisibility(View.VISIBLE);
		}else{
			mSendBankMsgIcon.setVisibility(View.GONE);
		}
	}

	/**
	 * 7大银行显示短信账单查询按钮
	 * 
	 * @param currentBankItemInfo
	 */
	private void setSMSButtonIsShow(CCRBankCardInfo currentBankItemInfo) {
		String currentBankMark = currentBankItemInfo.getBankCardInfo().getBankMark();
		boolean isShowSms = false;
		if (BankSMSInfo.currentBankIsNeedSmsServer(currentBankMark)) {
			 smsList.getThreeBankMessages(currentBankItemInfo.getBankCardInfo()
					.getBankMark());
			isShowSms = true;
		}
		if (isShowSms) {
			mSMSSelect.setVisibility(View.VISIBLE);
		} else {
			mSMSSelect.setVisibility(View.GONE);
		}
		smsList.setVisibility(View.GONE);
		mSMSSelect.setChecked(false);
	}

	/**
	 * �����ÿ�������������
	 */
	protected void sendCreateCreditCardTrade() {
		CCRBankCardInfo currentBankItemInfo = mSavedBank
				.get(currentSelectedBankItem);
		String cardIndexNumber = currentBankItemInfo.getCardIndexNumber();
		String holderName = currentBankItemInfo.getBankCardInfo().getUserName();
		String bankMark = currentBankItemInfo.getBankCardInfo().getBankMark();
		String depositAmount = mCountEdit.getText();
		if (!BizCCRUtil.checkMoney(mContext, depositAmount)) {
			return;
		}
		String msg = mContext.getResources().getString(
				R.string.QrcodeTradeCreate);
		PartnerInfo partnerInfo = null;
//		if(currentCCRBankCardInfo != null ){
//			partnerInfo = currentCCRBankCardInfo.getPartnerInfo();
//		}
		mContext.showProgressDialog(mContext, null, msg);
		mContext.setNowCardInfo(currentBankItemInfo.getBankCardInfo().getBankName(),
				currentBankItemInfo.getBankCardInfo().getBankMark(), holderName,
				currentBankItemInfo.getRemindDate());
		HashMap<String, String> requestData = BizCCRUtil
				.prepareCreateCreditCardTrade(cardIndexNumber, holderName,
						bankMark, depositAmount,currentCCRBankCardInfo);
		mBizObj.sendHttpRequest(mContext,
				Constant.RQF_CCR_CREATE_CREDIT_CARD_TRADE_FOR_OLD, requestData,
				AlipayHandlerMessageIDs.CCR_CREATE_CREDIT_CARD_TRADE_FOR_OLD);
	}

	@Override
	public void onDestory() {
		// TODO Auto-generated method stub

	}

	// 记录返回键日志
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			AlipayLogAgent.writeLog(mContext,BehaviourID.CLICKED, Constants.APPID_CCR,  "-",Constants.VIEWID_CCR_OLD, Constants.SEEDID_BACKICON);
		}
		return false;
	}

	public void setBankSelected(String no) {
//		int i = 0;
//		if(mSavedBank!=null){
//			for (CCRBankCardInfo bank : mSavedBank) {
//				if (bank.getCardIndexNumber().compareTo(no) == 0) {
//					selectedBank.setSelection(i);
//					break;
//				}
//				i++;
//			}
//		}
		if(cardIndexNumber != null && !cardIndexNumber.equals("") && mSavedBank != null){
			for(int i=0;i<mSavedBank.size();i++){
				CCRBankCardInfo currentBankItem = mSavedBank.get(i);
				if(currentBankItem.getCardIndexNumber().equals(cardIndexNumber)){
					existLocation = i;
					selectedBank.setSelection(i);
				}
			}
			if(existLocation == -1 && isRefresh){
				mContext.sendGetBankListRequest();
				existLocation = 0;
				isRefresh = false;
			}
		}
	}
	
	
		public void setRefurbishTYJImage(){
		if (mAlipayDataStore.getBoolean(mContext.getUserId(), false)
				&& mTiyanjin != null) {
			mTiyanjin.setVisibility(View.GONE);
		}
	}
	
}
