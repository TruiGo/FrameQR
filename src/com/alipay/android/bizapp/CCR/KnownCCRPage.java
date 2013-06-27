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
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.ToggleButton;

import com.alipay.android.appHall.CacheManager;
import com.alipay.android.bizapp.BaseBiz;
import com.alipay.android.bizapp.BasePage;
import com.alipay.android.client.constant.AlipayHandlerMessageIDs;
import com.alipay.android.client.constant.Constant;
import com.alipay.android.client.util.AlipayDataStore;
import com.alipay.android.comon.component.EditTextWithButton;
import com.alipay.android.comon.component.ThreeSMSListView;
import com.eg.android.AlipayGphone.R;

public class KnownCCRPage extends BasePage{
	
	private CCRActivity mContext;
	
	private Spinner knownBank;
	private TextView ccrTimeTip = null;
	private EditTextWithButton mCountEdit = null;
	private TextView mBankSearchTelephone = null;
	private TextView mBankPhone = null;
	private Button mNextBtn = null;

	private ThreeSMSListView smsList = null;
	private ToggleButton mSMSSelect = null;

	private AlipayDataStore mAlipayDataStore;
	private CCRBankCardInfo currentUserInfo;//当前银行用戶信息
	private CCRBankCardInfo currentBankInfo;//當前銀行信息
	private ArrayList<CCRBankCardInfo> mSupportBank;

	public KnownCCRPage(Activity context) {
		mContext = (CCRActivity) context;
	}
	
	@Override
	public void onCreate(BaseBiz bizObj) {
		super.onCreate(bizObj);
		mPageView = (FrameLayout) mContext.getLayoutInflater().inflate(
				R.layout.baseapp_layout, null);
		View view = View.inflate(mContext, R.layout.page_ccr_known, null);
		mPageView.addView(view);
		mAlipayDataStore = new AlipayDataStore(mContext);
		loadAllVariables();
	}
	
	@Override
	public void loadAllVariables() {
		super.loadAllVariables();
		// 设置page title
				TextView titleName = (TextView) mPageView.findViewById(R.id.title_text);
				titleName.setText(R.string.CCRApp);
				// 动态获取存在本地的还款信息
				knownBank = (Spinner) mPageView.findViewById(R.id.SelectBank);
				
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
					}
				});
				
				mBankSearchTelephone = (TextView) mPageView
						.findViewById(R.id.BankSearchTelephone);
				mBankPhone = (TextView) mPageView.findViewById(R.id.BankPhone);
				mBankPhone.setOnClickListener(new View.OnClickListener() {
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
					}
				});

				mNextBtn = (Button) mPageView.findViewById(R.id.next);
				mNextBtn.setEnabled(false);
				mNextBtn.setOnClickListener(new View.OnClickListener() {
					@Override
					public void onClick(View v) {
						sendCCRRequest();
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
	}
	@Override
	public void onResume() {
		super.onResume();
		currentUserInfo  = (CCRBankCardInfo)params;
		CacheManager cm = CacheManager.getInstance(mContext);
		mSupportBank = (ArrayList<CCRBankCardInfo>) (cm
				.getObject(Constant.CCR_SUPPORT_ONE_BANK_INFO));
		if(mSupportBank==null||mSupportBank.size()<=0){
			sendGetBankInfo();
		}else{
			currentBankInfo = mSupportBank.get(0);
			setBankUserInfo(currentUserInfo);
			setOtherInfo(currentBankInfo);
			mSupportBank.clear();
		}
		
	}

	private void setBankUserInfo(CCRBankCardInfo currentBankInfo) {
		ArrayList<CCRBankCardInfo> arrayList = new ArrayList<CCRBankCardInfo>();
		arrayList.add(currentBankInfo);
		BankNameAdapter bankAdapter = new BankNameAdapter(mContext, arrayList);
		knownBank.setAdapter(bankAdapter);
		knownBank.setEnabled(false);
		setSMSButtonIsShow(currentBankInfo);
	}
	
	private void setOtherInfo(CCRBankCardInfo bankItemInfo) {
		ccrTimeTip.setText(bankItemInfo.getArriveDatetime());
				String bankName = bankItemInfo.getBankCardInfo().getBankName();
				String phone = bankItemInfo.getPhoneNumber();
				if (!bankName.equals("") && !phone.equals("")) {
					String bankTel = String.format(
							mContext.getResources().getString(R.string.BankTelDes),
							bankName);
					mBankSearchTelephone.setText(bankTel);
					mBankPhone.setText(Html.fromHtml("<u>" + phone + "</u>"));
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
			 smsList.getThreeBankMessages(currentBankItemInfo
					.getBankCardInfo().getBankMark());
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

	private void sendGetBankInfo() {
		String msg = mContext.getResources().getString(
				R.string.QrcodeTradeCreate);
		mContext.showProgressDialog(mContext, null, msg);
		HashMap<String, String> mapdata = BizCCRUtil
				.prepareGetBankInfo(Constant.RQF_CCR_QUERYBANK,currentUserInfo.getBankCardInfo().getBankMark());
		mBizObj.sendHttpRequest(mContext, Constant.RQF_CCR_GETBANKS_TYPE,
				mapdata, AlipayHandlerMessageIDs.CCR_GET_BANK_INFO);
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
			smsList.show(currentUserInfo.getBankCardInfo().getBankMark());
		}
		mCountEdit.setFocusable(true);
		((InputMethodManager) mContext
				.getSystemService(Context.INPUT_METHOD_SERVICE))
				.toggleSoftInput(0, InputMethodManager.HIDE_NOT_ALWAYS);
	}
	
	
	/**
	 * 创建新信用卡还款订单
	 * 
	 * @param cacheKey
	 *            CCDC返回的信用卡key
	 * @param cardNumberType
	 *            卡号类型�?明文;1cacheKey;
	 */
	public void sendCCRRequest(){
		
		String payMoney = mCountEdit.getText();
		if (!BizCCRUtil.checkMoney(mContext, payMoney)) {
			return;
		}
		String msg = mContext.getResources().getString(
				R.string.QrcodeTradeCreate);
		mContext.showProgressDialog(mContext, null, msg);
		mContext.setNowCardInfo(
				currentBankInfo.getBankCardInfo().getBankName(),
				currentBankInfo.getBankCardInfo().getBankMark(),
				currentUserInfo.getBankCardInfo().getUserName(),
				currentBankInfo.getRemindDate());
		//接口请求数据组装
		HashMap<String, String> mapdata = BizCCRUtil.prepareNewUData(
				currentUserInfo.getCardIndexNumber(), currentUserInfo.getBankCardInfo().getUserName(),
				currentBankInfo.getBankCardInfo().getBankMark(),
				mCountEdit.getText(), "2",null,null);
		
		mBizObj.sendHttpRequest(mContext,
				Constant.RQF_CCR_CREATE_CREDIT_CARD_TRADE_FOR_NEW, mapdata,
				AlipayHandlerMessageIDs.CCR_CREATE_CREDIT_CARD_TRADE_FOR_NEW);
		}
	
}
