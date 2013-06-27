package com.alipay.android.bizapp.CCR;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;

import org.json.JSONObject;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.widget.Toast;

import com.alipay.android.bizapp.BaseBiz;
import com.alipay.android.bizapp.BizCallBack;
import com.alipay.android.client.AlipayApplication;
import com.alipay.android.client.AnimationData;
import com.alipay.android.client.constant.AlipayHandlerMessageIDs;
import com.alipay.android.client.constant.Constant;
import com.alipay.android.client.util.AlipayDataStore;
import com.alipay.android.client.util.Responsor;
import com.alipay.android.common.data.UserData;
import com.alipay.android.safepay.MobileSecurePayer;
import com.alipay.android.ui.bill.BillManagerRootControllerActivity;
import com.eg.android.AlipayGphone.R;

public class BizCCR extends BaseBiz {

	protected CCRActivity mContext = null;
	private AlipayDataStore mAlipayDataStore = null;
	private AlipayApplication application = null;

	public BizCCR(Activity context) {
		super(context);
		mContext = (CCRActivity) context;
		mAlipayDataStore = new AlipayDataStore(mContext);
		application  = (AlipayApplication) mContext.getApplicationContext();
	}

	Handler mHandler = new Handler() {
		public void handleMessage(Message msg) {
			Responsor responsor = (Responsor) msg.obj;
			JSONObject jsonResponse = null;
			boolean tResultOK = false;
			tResultOK = processMessage(msg);
			if ((tResultOK)) {
				jsonResponse = responsor.obj;
				if(msg.what != AlipayHandlerMessageIDs.CCR_LINK_CCDC_GET_CACHEKEY){
					mContext.dismissProgressDialog();
				}
				switch (msg.what) {
				case AlipayHandlerMessageIDs.CCR_CREATE_CREDIT_CARD_TRADE_FOR_NEW:
					mAlipayDataStore.putBoolean(mContext.getUserId()
							+ AlipayDataStore.BANK_CCR_IS_NEW, true);
					application.setBankListRefresh(true);
					application.setSavedCCRBankRefresh(true);
					payCCR(jsonResponse);
					break;
				case AlipayHandlerMessageIDs.CCR_CREATE_CREDIT_CARD_TRADE_FOR_OLD:
					payCCR(jsonResponse);
					break;
				case AlipayHandlerMessageIDs.CCR_GET_SUPPORT_BANKLIST:
					ProcessBankList(jsonResponse);
					break;
				case AlipayHandlerMessageIDs.CCR_GET_SAVED_BANKLIST:
					String isSaved = jsonResponse.optString(Constant.RPF_CCR_HASSAVED);
					if (isSaved != null && isSaved.compareTo("false") == 0){
						ArrayList<CCRBankCardInfo> savedBankList = BizCCRUtil.getSavedBank(mContext);
						if(savedBankList != null ){
							BizCCRUtil.deleteSavedBank(mContext);
						}
					}
					application.setSavedCCRBankRefresh(false);
					ProcessBankList(jsonResponse);
					break;
				case AlipayHandlerMessageIDs.CCR_GET_BANK_INFO:
					processBankInfo(jsonResponse);
					break;
				case AlipayHandlerMessageIDs.CCR_SET_CREDIT_CARD_DEPOSIT_ALET:
					finishSet(jsonResponse);
					break;
				case AlipayHandlerMessageIDs.CCR_LINK_CCDC_GET_CACHEKEY:
					createTradeOrder(jsonResponse);
					break;
				}
			} else {
				jsonResponse = responsor.obj;
				switch (msg.what) {
				case AlipayHandlerMessageIDs.CCR_LINK_CCDC_GET_CACHEKEY:
					createTradeOrder(jsonResponse);
					break;

				default:
					break;
				}
			}
			if(msg.what != AlipayHandlerMessageIDs.CCR_LINK_CCDC_GET_CACHEKEY){
				mContext.dismissProgressDialog();
			}
		}

	};

	private void finishSet(JSONObject jsonResponse) {
		Toast.makeText(mContext, "提醒设置成功", Toast.LENGTH_SHORT).show();
	}

	private void createTradeOrder(JSONObject jsonResponse) {
		String cacheKey = "";
		if (jsonResponse != null) {// 联网正常返回响应数据
			if (jsonResponse.has(Constant.KEY)) {
				cacheKey = jsonResponse.optString(Constant.KEY);
				if (!cacheKey.equals("")) {// 响应cacheKey不为空，发cacheKey
					mContext.mNewUserPage.sendCCRRequest(cacheKey,
							NewUserCCRPage.CACHE_KEY);
				} else {// cacheKey为空，发信用卡明文
					mContext.mNewUserPage.sendCCRRequest(cacheKey,
							NewUserCCRPage.PLAIN_TEXT);
				}
			} else {// 联网正常返回响应，但无cacheKey值
				mContext.mNewUserPage.sendCCRRequest(cacheKey,
						NewUserCCRPage.PLAIN_TEXT);
			}
		} else {// 联网异常无正常响应数据
			mContext.mNewUserPage.sendCCRRequest(cacheKey,
					NewUserCCRPage.PLAIN_TEXT);
		}

	}

	Handler mSafeHandler = new Handler() {
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case AlipayHandlerMessageIDs.RQF_PAY:
			case AlipayHandlerMessageIDs.RQF_AGENT_PAY:
				payingFinish(msg);
				break;
			}
			mContext.dismissProgressDialog();
		}
	};

	@Override
	public void sendHttpRequest(BizCallBack bizCallback, String requestType,
			final HashMap<String, String> RequestData, int messageId) {
		mBizCallback = bizCallback;
		SendRequest(mContext, requestType, RequestData, mHandler, messageId);
	}

	public void sendHttpRequestToGet(BizCallBack bizCallback, int messageId) {
		mBizCallback = bizCallback;
		SendRequestToGet(mContext, mHandler, messageId);
	}

	private void ProcessBankList(JSONObject responsor) {
		String isSaved = responsor.optString(Constant.RPF_CCR_HASSAVED);
		saveSMSTemplete(responsor);
		if (isSaved != null && isSaved.compareTo("true") == 0) {
			String banklist = responsor.optString(Constant.RPF_CCR_SAVEDBANK);
			if (banklist != null) {
				BizCCRUtil.setSavedBank(mContext, banklist);
				mAlipayDataStore.putBoolean(mContext.getUserId()
						+ AlipayDataStore.BANK_CCR_IS_NEW, false);
				Calendar cal = Calendar.getInstance();
				long currentTime = cal.getTimeInMillis();
				mAlipayDataStore.putString(AlipayDataStore.BANK_CCR_SAVED_TIME,
						"" + currentTime);
				mContext.JumptoPage(mContext.PAGE_ID_OLDUSER, false);
			}
		} else if (isSaved != null && isSaved.compareTo("false") == 0) {
			String banklist = responsor.optString(Constant.RPF_CCR_SUPPORTBANK);
			if (banklist != null) {
				BizCCRUtil.getSupportBank(mContext, banklist ,Constant.CCR_SUPPORT_BANK_LIST);
				mContext.JumptoPage(mContext.PAGE_ID_NEWUSER, true);
			}
		}
	}
	
	private void processBankInfo(JSONObject responsor){
		saveSMSTemplete(responsor);
		String banklist = responsor.optString(Constant.RPF_CCR_SUPPORTBANK);
		if (banklist != null) {
			BizCCRUtil.getSupportBank(mContext, banklist,Constant.CCR_SUPPORT_ONE_BANK_INFO);
			mContext.JumptoPage(mContext.PAGE_ID_KNOWN, true);
		}
	}
	
	private void saveSMSTemplete(JSONObject responsor){
		String SmsTemplete = responsor.optString(Constant.RPF_CCR_SMSTEMPLATE);
		if (SmsTemplete != null && SmsTemplete.length() > 0) {
			AlipayDataStore alipayDataStore = new AlipayDataStore(mContext);
			alipayDataStore.putString(AlipayDataStore.BANK_CCR_SMS_FORMAT,
					SmsTemplete);
		}
	}

	private void payCCR(JSONObject responsor) {
		String theLastTimeSuccessCardIndexNumber = responsor
				.optString(Constant.RPF_CCR_CARD_INDEX_NUMBER);
		mAlipayDataStore.putString(
				AlipayDataStore.THE_LAST_TIME_SUCCESS_CARD_INDEX_NUMBER,
				theLastTimeSuccessCardIndexNumber);
		mContext.mNowCard.setCardIndexNumber(responsor
				.optString(Constant.RPF_CCR_CARD_INDEX_NUMBER));
		mContext.mNowCard.setIsNewCard(responsor
				.optString(Constant.RPF_CCR_IS_NEW_CARD));
		mContext.mNowCard.setIsNeedAlert(responsor
				.optString(Constant.RPF_CCR_IS_NEED_ALERT));
		String tradeNo = responsor.optString(Constant.RPF_CCR_TRADENUMBER);
		payDeal(mContext, mSafeHandler, tradeNo, mContext.getExtToken(), "",
				"trade", "ccr");
	}

	private void payingFinish(Message msg) {
		mContext.dismissProgressDialog();
		AlipayApplication application = (AlipayApplication) mContext
				.getApplicationContext();
		application.setRecordsRefresh(true);
		UserData mUserdata = mContext.getUserData();
		if (mUserdata!=null) {
			mUserdata.resetStatus();
		}
		try {
			if (msg.arg1 == MobileSecurePayer.PAY_OK) {// 快捷支付成功
				
				if (Boolean.parseBoolean(mContext.mNowCard.getIsNeedAlert())){
					mContext.JumptoPage(mContext.PAGE_ID_SETTING, true);
				} else {
					mContext.backPartner();
				}
			} else {// 失败到交易记录
				
				mContext.finish();
				application.setRecordsRefresh(true);
				Intent intent = new Intent(mContext,BillManagerRootControllerActivity.class);
				mContext.startActivity(intent);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	private boolean processMessage(Message msg) {
		Responsor responsor = (Responsor) msg.obj;
		int status = responsor.status;
		String memo = responsor.memo;
		String tTitle = "";
		int iconID = R.drawable.erroricon;
		if (status == 0) {
			memo = mContext.getResources().getString(R.string.CheckNetwork);
			tTitle = mContext.getResources().getString(R.string.ErrorString);
		} else if (status == 1) {
		    memo = mContext.getResources().getString(R.string.WarningDataCheck);
            tTitle = mContext.getResources().getString(R.string.ErrorString);
        }  else 
        	if (status == 100) {
			// has nothing to process here.
			return true;
		} else if (status == 200) {
			mContext.backToLogin(R.drawable.infoicon, mContext.getResources()
					.getString(R.string.WarngingString), memo);
			return false;
		} else if (status == Constant.CCDC_FAILURE) {// 连接CCDC获取cacheKey失败
			return false;
		} else if(status == 2039){//卡片用户信息被删除，需要重新获取已存信用卡列表
			
			mContext.getDataHelper().showDialog(mContext, iconID, mContext.getResources().getString(R.string.WarngingString), memo,
					mContext.getResources().getString(R.string.Ensure),
					new DialogInterface.OnClickListener() {
						@Override
						public void onClick(DialogInterface dialog, int which) {
							mContext.sendGetBankListRequest();
							AlipayApplication application = (AlipayApplication) mContext
									.getApplicationContext();
							application.setBankListRefresh(true);
							application.setSavedCCRBankRefresh(true);
						}
					}, null, null, null, null);
			return false;
		}
		else {
			tTitle = mContext.getResources().getString(R.string.ErrorString);
		}
		if (msg.what == AlipayHandlerMessageIDs.CCR_GET_SUPPORT_BANKLIST
				|| msg.what == AlipayHandlerMessageIDs.CCR_GET_SAVED_BANKLIST) {

			mContext.getDataHelper().showDialog(mContext, iconID, tTitle, memo,
					mContext.getResources().getString(R.string.Ensure),
					new DialogInterface.OnClickListener() {
						@Override
						public void onClick(DialogInterface dialog, int which) {
							mContext.gotoAppHallActivity();
						}
					}, null, null, null, null);

			return false;
		}else if(msg.what == AlipayHandlerMessageIDs.CCR_LINK_CCDC_GET_CACHEKEY){
			return false;
		} else {
			mContext.showDialog(iconID, tTitle, memo, mContext.getResources()
					.getString(R.string.Ensure), null, null, null, null, null);
			return false;
		}
	}
}
