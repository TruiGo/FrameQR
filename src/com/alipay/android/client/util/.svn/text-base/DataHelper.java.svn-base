package com.alipay.android.client.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.StringTokenizer;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnKeyListener;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.os.Build;
import android.os.Handler;
import android.os.Message;
import android.os.SystemClock;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.alipay.android.appHall.DeviceHelper;
import com.alipay.android.appHall.common.CacheSet;
import com.alipay.android.client.AlipayApplication;
import com.alipay.android.client.AlipayDataCacheHelper;
import com.alipay.android.client.RootActivity;
import com.alipay.android.client.constant.Constant;
import com.alipay.android.common.data.ConfigData;
import com.alipay.android.common.data.UserInfo;
import com.alipay.android.comon.component.ProgressDiv;
import com.alipay.android.comon.component.StyleAlertDialog;
import com.alipay.android.log.Constants;
import com.alipay.android.log.StorageStateInfo;
import com.alipay.android.net.alipayclient2.APHttpClient;
import com.alipay.android.push.ServiceManager;
import com.alipay.android.security.RSA;
import com.alipay.android.util.LogUtil;
import com.eg.android.AlipayGphone.R;

public class DataHelper {
	private static final String TAG = "DataHelper";

	private final String FILE_PATH = "/data/data/";

	private final String FILE_NAME = "contact";

	private final String FILE_NAME_HISTORY = "history";

	private ConfigData mConfigData;
	private AlipayApplication mApplication;

	public String mLoginAccountPre = "";

	public APHttpClient nm = null;

	public DialogInterface.OnCancelListener cancelListener = new DialogInterface.OnCancelListener() {
		@Override
		public void onCancel(DialogInterface arg0) {
			// TODO Auto-generated method stub
			// need send cancel command to server.
			cancel();
		}
	};

	public DialogInterface.OnClickListener cancelBtnListener = new DialogInterface.OnClickListener() {
		@Override
		public void onClick(DialogInterface arg0, int arg1) {
			// TODO Auto-generated method stub
			cancel();
		}
	};

	public DataHelper(AlipayApplication application) {
		mApplication = application;
		mConfigData = mApplication.getConfigData();
		nm = new APHttpClient(mApplication);
	}

	public void cancel() {
		nm.abort();
	}

	public boolean isCanceled() {
		return nm.isAborted();
	}

	public ProgressDiv showProgressDialogWithCancelButton(Activity context,
			CharSequence title, CharSequence message, boolean indeterminate,
			boolean cancelable,
			DialogInterface.OnCancelListener cancelListener,
			DialogInterface.OnClickListener cancelBtnListener) {
		// Log.i(TAG, "Show Progress Dialog!");
		try {
			if (context != null && !context.isFinishing()) {
			    ProgressDiv dialog = new ProgressDiv(context);
				dialog.setTitle(title);
				dialog.setMessage(message);
				dialog.setIndeterminate(indeterminate);
				dialog.setCancelable(false);
				dialog.setOnKeyListener(new DialogInterface.OnKeyListener() {
					@Override
					public boolean onKey(DialogInterface dialog, int keyCode,
							KeyEvent event) {
						// TODO Auto-generated method stub
						if (keyCode == KeyEvent.KEYCODE_SEARCH) {
							return true;
						}
						return false;
					}

				});
				// dialog.setOnCancelListener(cancelListener);
				// dialog.setButton(Dialog.BUTTON_POSITIVE,
				// context.getResources()
				// .getString(R.string.Cancel), cancelBtnListener);
				dialog.show();
				return dialog;
			}
		} catch (Exception e) {

		}
		return null;
	}

	public ProgressDiv showProgressDialogWithoutCancelButton(
			Activity context, CharSequence title, CharSequence message,
			boolean indeterminate, boolean cancelable,
			DialogInterface.OnCancelListener cancelListener,
			DialogInterface.OnClickListener cancelBtnListener) {
		// Log.i(TAG, "Show Progress Dialog!");
		try {
			if (context != null && !context.isFinishing()) {
			    ProgressDiv dialog = new ProgressDiv(context);
				dialog.setTitle(title);
				dialog.setMessage(message);
				dialog.setIndeterminate(indeterminate);
				dialog.setCancelable(false);
				// dialog.setOnCancelListener(cancelListener);
				// dialog.setButton(Dialog.BUTTON_POSITIVE,
				// context.getResources()
				// .getString(R.string.Cancel), cancelBtnListener);
				dialog.show();
				return dialog;

			}
		} catch (Exception e) {

		}
		return null;
	}

	StyleAlertDialog dialog;
	public void showDialog(Activity tContext, int iconRes, String titleText,
			String MessageText, String btn1Msg,
			DialogInterface.OnClickListener btn1, String btn2Msg,
			DialogInterface.OnClickListener btn2, String btn3Msg,
			DialogInterface.OnClickListener btn3) {
		// Log.i(TAG, "show error message.");
		try {
			if (tContext != null && !tContext.isFinishing()) {
				if(dialog != null && dialog.getDialog() != null && dialog.getDialog().isShowing())
					dialog.dismiss();
				
				dialog = new StyleAlertDialog(tContext, iconRes == -1 ? 0
		                    : iconRes, titleText, MessageText, btn1Msg, btn1,
		                    btn2Msg, btn2, null);
                /*if (btn1Msg != null) {
                    dialog.getDialog().setCancelable(false);
                }*/
				
				dialog.getDialog().setOnKeyListener(new OnKeyListener() {

					@Override
					public boolean onKey(DialogInterface dialog, int keyCode,
							KeyEvent event) {
						if (keyCode == KeyEvent.KEYCODE_SEARCH) {
							return true;
						}
						return false;
					}

				});
				dialog.show();
			}
		} catch (Exception e) {
		    e.printStackTrace();
		}
	}

	public void showToast(Activity context,String memo){
    	View toastView = LayoutInflater.from(context.getApplicationContext()).inflate(
                R.layout.alipay_toast_view, null);
    	TextView toastTextView = (TextView)toastView.findViewById(R.id.toastText);
    	toastTextView.setText(memo);
        Toast toast = new Toast(context.getApplicationContext());
        toast.setView(toastView);
        toast.setGravity(Gravity.CENTER, 0, 0);
        toast.setDuration(Toast.LENGTH_SHORT);
        toast.show();
	}
	
	
	static public boolean IsSystemError(int status) {
		if ((status >= 100 && status < 200) || status >= 600)
			return false;
		else
			return true;
	}

	private String encrpt(String pwd) {
		String rsaPK = mConfigData.getPublicKey();
		String rsaTS = mConfigData.getTimeStamp();
		String EncrptPassWd = RSA.encrypt(pwd + rsaTS, rsaPK);
		log("rsaPK " + rsaPK);
		log("rsaTS " + rsaTS);
		log("EncrptPassWd " + EncrptPassWd);
		LogUtil.logOnlyDebuggable("xxxx", rsaPK +" ; "+ rsaTS +" ; "+EncrptPassWd);

		return EncrptPassWd;
	}

	public void SimpleRequest(String requestType, final Handler callback,
			final int myWhat) {
		HashMap<String, String> mDataMap = new HashMap<String, String>();
		SendRequest(requestType, mDataMap, callback, myWhat);
	}

	public void SendRequest(String requestType,
			final HashMap<String, String> RequestData, final Handler callback,
			final int myWhat) {
		// post-process request . client
		RequestData.put(Constant.RQF_CLIENT_ID, mConfigData.getClientId());

		// encrpyt every password.
		String pwd = RequestData.get(Constant.RQF_LOGIN_PASSWORD);
		if (pwd != null) {
			RequestData.put(Constant.RQF_LOGIN_PASSWORD, encrpt(pwd));
		}

		pwd = RequestData.get(Constant.RQF_PAY_PASSWORD);
		if (pwd != null) {
			RequestData.put(Constant.RQF_PAY_PASSWORD, encrpt(pwd));
		}

		pwd = RequestData.get(Constant.RQF_DRAFTPASSWORD);
		if (pwd != null) {
			RequestData.put(Constant.RQF_DRAFTPASSWORD, encrpt(pwd));
		}

		JSONObject jsonRequest = new JSONObject(RequestData);
		SendJSONRequest(requestType, jsonRequest, callback, myWhat);
	}

	public void SendJSONRequest(final String requestType,
			final JSONObject jsonRequest, final Handler callback,
			final int myWhat) {
		// 只要访问网络，都置为需要刷新状态
		AlipayDataCacheHelper.setRefreshFlag(true);

		// Asynchronous
		new Thread(new Runnable() {
			public void run() {
				JSONObject jsonResponse = null;
				int resultStatus = 0;
				String memo = null;
				try {
					synchronized (nm) {
						String sessionId = mApplication.getConfigData()
								.getSessionId();

						// process JSON object before send.
						jsonRequest.put(Constant.RQF_OPERATION_TYPE,
								requestType);
						StorageStateInfo.getInstance().putValue(
								Constants.STORAGE_REQUESTTYPE, requestType);
						jsonRequest.put(Constant.RPF_SESSION_ID, sessionId);
						
						String bizUrl = Constant.getAlipayURL(mApplication);
						if(Constant.OP_QUERY_ADVERT.equals(requestType)){
							bizUrl = bizUrl.replace("https", "http").replace("mobilecs.htm", "mtk.htm");
						}
						nm.setUrl(bizUrl);
						
						String resp = nm.sendSynchronousRequest(jsonRequest
								.toString());
						if (resp != null && resp != "") {
							jsonResponse = new JSONObject(resp);
							resultStatus = jsonResponse
									.getInt(Constant.RPF_RESULT_STATUS);
							memo = jsonResponse.optString(Constant.RPF_MEMO);
						} else {
							if (nm.errorType == APHttpClient.SSL_ERROR) {
								resultStatus = 1; // tbd
							} else if (nm.errorType == APHttpClient.IO_ERROR) {
								resultStatus = 0; // tbd
							} else {
								resultStatus = 0; // tbd
							}
						}
					}

				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (Exception e) {
					e.printStackTrace();
				}

				//
				//
				Responsor responsor = new Responsor(requestType, resultStatus,
						jsonResponse, memo);
				Message msg = new Message();
				msg.what = myWhat;
				msg.arg1 = resultStatus;
				msg.obj = responsor;

				if (callback != null) {
					callback.sendMessage(msg);
				}

				if (jsonResponse != null)
					log(jsonResponse.toString());

				if (IsSystemError(resultStatus))
					log("system error " + resultStatus);
			}
		}).start();

		// log(jsonRequest.toString());
	}

	public void sendMobilePay(Handler handler, int cmd) {
		SimpleRequest(Constant.OP_CONFIRM_MOBILE_PAY, handler, cmd);
	}

	public void sendLogin(Handler handler, int cmd, String account, String pwd,
			String checkcodeId, String checkcode, String alipayCheckcode,
			JSONObject extraParam, boolean bTaobao, Context context) {
		JSONObject tObject = new JSONObject();

		try {
			tObject.put(Constant.RQF_CLIENT_ID, mConfigData.getClientId());
			tObject.put(Constant.RQF_EXTRAPARAM, extraParam.toString());

			tObject.put(Constant.RQF_LOGIN_ACCOUNT, account);
			tObject.put(Constant.RQF_LOGIN_PASSWORD, encrpt(pwd));

			tObject.put(Constant.RQF_PRODUCT_VERSION,
					mConfigData.getProductVersion());
			tObject.put(Constant.RQF_PRODUCT_ID, mConfigData.getProductId());
			// tObject.put(Constant.RQF_ACCESS_POINT,
			// mTelephoneHelper.getAPN());
			tObject.put(Constant.RQF_ACCESS_POINT,
					DeviceHelper.getApnInUse(context));
			tObject.put(Constant.RQF_USER_AGENT, mConfigData.getUserAgent());
			tObject.put(Constant.RQF_SCREEN_WIDTH, mConfigData.getScreenWidth());
			tObject.put(Constant.RQF_SCREEN_HIGH, mConfigData.getScreenHeight());

			tObject.put(Constant.RPF_TAOBAO_CHECKCODEID, checkcodeId);
			tObject.put(Constant.RQF_TAOBAO_CHECKCODE, checkcode);
			tObject.put(Constant.RQF_ALIPAY_CHECKCODE, alipayCheckcode);
			if (bTaobao) {
				tObject.put(Constant.RQF_LOGINTYPE, "taobao");
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}

		SendJSONRequest(Constant.OP_LOGIN, tObject, handler, cmd);
	}

	public void sendUpdate(Handler handler, int cmd) {
		HashMap<String, String> mDataMap = new HashMap<String, String>();

		mDataMap.put(Constant.RQF_PRODUCT_VERSION,
				mConfigData.getProductVersion());
		mDataMap.put(Constant.RQF_PRODUCT_ID, mConfigData.getProductId());

		SendRequest(Constant.OP_UPGRADE, mDataMap, handler, cmd);
	}

	public void sendPayResultNotice(Handler handler, int cmd, String tradeNo,
			String action, String qrcode) {
		HashMap<String, String> mDataMap = new HashMap<String, String>();

		mDataMap.put(Constant.RQF_TRADE_NO, tradeNo);
		mDataMap.put(Constant.RQF_P2P_PAY_ACTION, action);
		mDataMap.put(Constant.RQF_P2P_PAY_QRCODE, qrcode);
		SendRequest(Constant.OP_P2P_PAY_RESULT, mDataMap, handler, cmd);
	}

	public void sendUserInfoSupplement(Handler handler, int cmd,
			String loginId, String userName, String pwd) {
		HashMap<String, String> mDataMap = new HashMap<String, String>();

		mDataMap.put(Constant.RQF_LOGON_ID, loginId);
		mDataMap.put(Constant.RQF_USER_NAME, userName);
		mDataMap.put(Constant.RQF_PAY_PASSWORD, pwd);

		SendRequest(Constant.OP_QUSER_INFO_SUPPLEMENT, mDataMap, handler, cmd);
	}

	public void sendApplyBinding(Handler handler, int cmd, String phoneNumber,String loginAccount) {
		HashMap<String, String> mDataMap = new HashMap<String, String>();
		mDataMap.put(Constant.RQF_MOBILE_NO, phoneNumber);
		if(loginAccount != null)
			mDataMap.put(Constant.RQF_LOGIN_ACCOUNT, loginAccount);

		SendRequest(Constant.OP_APPLY_BINDING, mDataMap, handler, cmd);
	}

	public void sendConfirmBinding(Handler handler, int cmd,
			String smsCheckCode, String payPassword,String phone) {
		HashMap<String, String> mDataMap = new HashMap<String, String>();
		mDataMap.put(Constant.RQF_BINDING_CHECKCODE, smsCheckCode);
		mDataMap.put(Constant.RQF_BINDING_PAYPWD, payPassword);
		// 此处需要增加以下俩个参数
		 mDataMap.put("mobileNo", phone);
		// mDataMap.put("isEncrypted ", "1");

		SendRequest(Constant.OP_CONFIRM_BINDING, mDataMap, handler, cmd);
	}

	public void sendConfirmAccountBinding(Handler handler, int cmd,
			String alipayId, String password, String loginType, Context context) {
		HashMap<String, String> mDataMap = new HashMap<String, String>();
		mDataMap.put(Constant.RQF_LOGIN_ACCOUNT, alipayId);
		mDataMap.put(Constant.RQF_LOGIN_PASSWORD, password);
		mDataMap.put(Constant.RQF_PRODUCT_VERSION,
				mConfigData.getProductVersion());

		mDataMap.put(Constant.RQF_PRODUCT_ID, mConfigData.getProductId());
		// mDataMap.put(Constant.RQF_ACCESS_POINT, mTelephoneHelper.getAPN());
		mDataMap.put(Constant.RQF_ACCESS_POINT, mConfigData.getAccessPoint());
		mDataMap.put(Constant.RQF_USER_AGENT, mConfigData.getUserAgent());
		mDataMap.put(Constant.RQF_SCREEN_WIDTH, mConfigData.getScreenWidth());
		mDataMap.put(Constant.RQF_SCREEN_HIGH, mConfigData.getScreenHeight());
		mDataMap.put(Constant.RQF_LOGINTYPE, loginType); // accountType:
															// taobao|alipay

		SendRequest(Constant.OP_BINDINGLOGINID, mDataMap, handler, cmd);
	}
	
	public void sendQueryBalance(Handler handler, int cmd,boolean mBillDirty,boolean mVoucherDirty) {
		HashMap<String, String> mDataMap = new HashMap<String, String>();
		mDataMap.put("refreshCardList", "true");
		mDataMap.put("refreshBillList", mBillDirty?"true":"false");
		mDataMap.put("refreshVoucherList", mVoucherDirty?"true":"false");
		mDataMap.put("apiVersion", "2");
		SendRequest(Constant.OP_QUERYBALANCE,mDataMap, handler, cmd);
	}
	
	public void sendQueryAdvert(Handler handler, int cmd) {
		HashMap<String, String> mDataMap = new HashMap<String, String>();
		mDataMap.put(Constant.RQF_CLIENT_ID, mConfigData.getClientId());

		SendRequest(Constant.OP_QUERY_ADVERT, mDataMap, handler, cmd);
	}

	public void sendQueryTransList(Handler handler, int cmd, String timeRange,
			String nextPage, String pageCount) {
		HashMap<String, String> mDataMap = new HashMap<String, String>();
		mDataMap.put(Constant.RQF_TIMERANGE, timeRange);
		mDataMap.put(Constant.RQF_NEXTPAGE, nextPage);
		mDataMap.put(Constant.RPF_PAGECOUNT, pageCount);

		SendRequest(Constant.OP_QUERYTRANSLIST, mDataMap, handler, cmd);
	}

	public void sendQueryTradeList(Handler handler, int cmd, String timeRange,
			String tradeType, String nextPage, String pageCount,
			String userRole, String startRowNum) {
		HashMap<String, String> mDataMap = new HashMap<String, String>();
		mDataMap.put(Constant.RQF_TIMERANGE, timeRange);
		mDataMap.put(Constant.RQF_QUERYTRADETYPE, tradeType);
		mDataMap.put(Constant.RQF_NEXTPAGE, nextPage);
		mDataMap.put(Constant.RPF_PAGECOUNT, pageCount);
		mDataMap.put(Constant.RPF_USERROLE, userRole);
		mDataMap.put(Constant.RPF_STARTROWNUM, startRowNum);

		SendRequest(Constant.OP_QUERYTRADELISTS, mDataMap, handler, cmd);

	}

	public void sendQueryScoreList(Handler handler, int cmd, String nextPage,
			String pageCount) {
		HashMap<String, String> mDataMap = new HashMap<String, String>();
		mDataMap.put(Constant.RQF_NEXTPAGE, nextPage);
		mDataMap.put(Constant.RPF_PAGECOUNT, pageCount);

		SendRequest(Constant.OP_QUERYSCORELISTS, mDataMap, handler, cmd);

	}

	public void sendQueryTradeDetail(Handler handler, int cmd, String tradeNo,
			String bizType) {
		HashMap<String, String> mDataMap = new HashMap<String, String>();
		mDataMap.put(Constant.RPF_TRADENO, tradeNo);
		mDataMap.put(Constant.RPF_BIZTYPE, bizType);

		SendRequest(Constant.OP_QUERYTRADEDETAIL, mDataMap, handler, cmd);
	}
	
	public void sendQueryLifePayBill(Handler handler, int cmd, String city,
			String chargeType, String inputInfo, String pucDate, String payAmount, String billUserName)
	{
		HashMap<String, String> mDataMap = new HashMap<String, String>();
		mDataMap.put("city", city);
		mDataMap.put("chargeType", chargeType);
		mDataMap.put("inputInfo", inputInfo);
		mDataMap.put("pucDate", pucDate);
		mDataMap.put("payAmount", payAmount);
		mDataMap.put("billUserName", billUserName);

		SendRequest(Constant.OP_QUERYLIFEPAYBILL, mDataMap, handler, cmd);
	}
	
	public void sendQueryLifePayDetail(Handler handler, int cmd, String billId,
			String isSupportSecurityPay)
	{
		HashMap<String, String> mDataMap = new HashMap<String, String>();
		mDataMap.put("billId", billId);
		mDataMap.put("isSupportSecurityPay", isSupportSecurityPay);

		SendRequest(Constant.OP_QUERYLIFEPAYDETAIL, mDataMap, handler, cmd);
	}
	
	public void sendQueryLifePayBillNo(Handler handler, int cmd, String billId)
	{
		HashMap<String, String> mDataMap = new HashMap<String, String>();
		mDataMap.put("billId", billId);
		SendRequest(Constant.OP_QUERYLIFEPAYBILLNO, mDataMap, handler, cmd);
	}

	public void sendPreDrawMoney(Handler handler, int cmd) {
		SimpleRequest(Constant.OP_PREDRAWMONEYTWO, handler, cmd);
	}

	public void sendDrawMoney(Handler handler, int cmd, String amount,
			String pwd, String bankId) {
		HashMap<String, String> mDataMap = new HashMap<String, String>();
		mDataMap.put(Constant.RQF_AMOUNT, amount);
		mDataMap.put(Constant.RQF_PAY_PASSWORD, pwd);
		mDataMap.put(Constant.RQF_BANKID, bankId);

		SendRequest(Constant.OP_DRAWMONEY, mDataMap, handler, cmd);
	}

	public void sendPreRequestKT(Handler handler, int cmd) {
		SimpleRequest(Constant.OP_PREREQUESTKT2, handler, cmd);
	}

	public void sendRequestKT(Handler handler, int cmd, String bankId,
			String realName, String idCardNO) {
		HashMap<String, String> mDataMap = new HashMap<String, String>();

		mDataMap.put(Constant.RQF_BANKID, bankId);
		mDataMap.put(Constant.RQF_REALNAME, realName);
		mDataMap.put(Constant.RQF_IDCARDNO, idCardNO);

		SendRequest(Constant.OP_REQUESTTK, mDataMap, handler, cmd);
	}

	public void sendBindingKT(Handler handler, int cmd, String bankCardNO,
			String pwd) {
		HashMap<String, String> mDataMap = new HashMap<String, String>();

		mDataMap.put(Constant.RQF_BANKCARDNO, bankCardNO);
		mDataMap.put(Constant.RQF_PAY_PASSWORD, pwd);

		SendRequest(Constant.OP_BINDINGKT, mDataMap, handler, cmd);
	}

	public void sendGetMsgList(Handler handler, int cmd, String num) {
		HashMap<String, String> mDataMap = new HashMap<String, String>();

		mDataMap.put(Constant.OP_GET_MSGNUM, num);
		SendRequest(Constant.OP_GET_MSGLIST, mDataMap, handler, cmd);
	}
	
	public void sendGetMsgDetail(Handler handler, int cmd,String msgId){
		HashMap<String, String> mDataMap = new HashMap<String, String>();
		
		mDataMap.put(Constant.RPF_MSGID, msgId);
		SendRequest(Constant.OP_GETMSGDETAIL, mDataMap, handler, cmd);
	}

	public void sendGetNextMsg(Handler handler, int cmd) {
		SimpleRequest(Constant.OP_GET_NEXTMSG, handler, cmd);
	}

	public void sendGetMsgCount(Handler handler, int cmd) {
		SimpleRequest(Constant.OP_GET_MSGCOUNT, handler, cmd);
	}

	public void sendQueryContactLists(Handler handler, int cmd) {
		SimpleRequest(Constant.OP_QUERYCONTACT, handler, cmd);
	}

	public void sendQueryTradeNum(Handler handler, int cmd, String time,
			String type, String userRole) {
		HashMap<String, String> mDataMap = new HashMap<String, String>();

		mDataMap.put(Constant.RQF_QUERYTRADETYPE, type);
		mDataMap.put(Constant.RQF_TIMERANGE, time);
		mDataMap.put(Constant.RQF_USERROLE, userRole);

		SendRequest(Constant.OP_QUERYTRADENUM, mDataMap, handler, cmd);
	}

	public void sendModifyPassword(Handler handler, int cmd,
			String newPassword, String oldPassword, String passwordType) {
		HashMap<String, String> mDataMap = new HashMap<String, String>();

		mDataMap.put(Constant.RQF_NEWPASSWORD, encrpt(newPassword));
		mDataMap.put(Constant.RQF_OLDPASSWORD, encrpt(oldPassword));
		mDataMap.put(Constant.RQF_PASSWORDTYPE, passwordType);

		SendRequest(Constant.OP_MODIFYPASSWORD, mDataMap, handler, cmd);
	}

	/*
	 * 用户提交反馈内容
	 */
	public void sendSubmitFeedbackContent(String requestType,
			String proposalType, String userName, String mobileNo,
			String feedbackContent, String productID, String productVersion,
			Handler handler, int cmd) {
		HashMap<String, String> mDataMap = new HashMap<String, String>();
		mDataMap.put(Constant.RQF_FEEDBACK_PROPOSALTYPE, proposalType);
		mDataMap.put(Constant.RQF_FEEDBACK_USERNAME, userName);
		mDataMap.put(Constant.RQF_FEEDBACK_MOBILENO, mobileNo);
		mDataMap.put(Constant.RQF_FEEDBACK_SUGGESTION, feedbackContent);
		mDataMap.put(Constant.RQF_FEEDBACK_PRODUCTID, productID);
		mDataMap.put(Constant.RQF_FEEDBACK_PRODUCTVERSION, productVersion);

		SendRequest(requestType, mDataMap, handler, cmd);
	}

	/*
	 * 获取用户短信的上行开关和验证码
	 */
	public void sendReqGetSwitchAndCheckCode(Handler handler, int cmd) {
		SimpleRequest(Constant.OP_SMSTRANS_GETSMSSWITCH, handler, cmd);
	}

	/*
	 * 用户进行身份证验证，发送身份证
	 */
	public void sendIdCard(Handler handler, int cmd, String idcard, String type) {
		HashMap<String, String> mDataMap = new HashMap<String, String>();
		mDataMap.put(Constant.RPF_IDCARDNO, idcard);
		mDataMap.put(Constant.RQF_PASSWORDTYPE, type);
		SendRequest(Constant.RQF_IDCARDCONFIRM, mDataMap, handler, cmd);
	}

	/*
	 * 用户请求服务端校验码
	 */
	public void sendPreSendSMSCheckCode(Handler handler, int cmd) {
		SimpleRequest(Constant.OP_SMSCHECKCODE, handler, cmd);
	}

	/*
	 * 用户输入并提交和服务端的交互，服务暂存用户数据，短信通知用户6位校验码
	 */
	public void sendSMSCheckCode(Handler handler, int cmd, String phoneNo,
			String checkCode, String type) {
		HashMap<String, String> mDataMap = new HashMap<String, String>();

		mDataMap.put(Constant.RQF_LOGIN_ACCOUNT, phoneNo);
		mDataMap.put(Constant.RQF_CHECK_CODE, checkCode);
		mDataMap.put(Constant.RQF_PASSWORDTYPE, type);
		SendRequest(Constant.OP_SENDSMSCHECKCODE, mDataMap, handler, cmd);
	}

	/*
	 * 用户提交输入的验证码来来获取“找回支付”的上行短信
	 */
	public void sendCheckCodeGetSMS(Handler handler, int cmd, String checkCode,
			String requestType) {
		HashMap<String, String> mDataMap = new HashMap<String, String>();

		mDataMap.put(Constant.RQF_SMSTRANS_CHECKCODE, checkCode);
		SendRequest(requestType, mDataMap, handler, cmd);
	}

	/*
	 * 通过短信上行找回支付密码：点击“设置密码”，请求网络，判断用户是否回复短信
	 */
	public void sendPreSettingPayPSW(Handler handler, int cmd,
			String requestType) {
		HashMap<String, String> mDataMap = new HashMap<String, String>();

		SendRequest(requestType, mDataMap, handler, cmd);
	}

	/*
	 * 用户输入短信校验码，服务端验证后返回时间戳和公钥�
	 */
	public void sendPreResetPassword(Handler handler, int cmd, String type,
			String resetKey, String checkCode) {
		HashMap<String, String> mDataMap = new HashMap<String, String>();
		mDataMap.put(Constant.RPF_RESETKEY, resetKey);
		mDataMap.put(Constant.RQF_CHECK_CODE, checkCode);
		SendRequest(type, mDataMap, handler, cmd);
	}

	/*
	 * 修改支付密码、修改登录密码的短信校验请求
	 */
	public void sendVerifyMPasswdCheckCode(Handler handler, int cmd, String passwordType,
		  String checkCode, String optType) {
		HashMap<String, String> mDataMap = new HashMap<String, String>();
		mDataMap.put(Constant.RQF_PASSWORDTYPE, passwordType);
		mDataMap.put(Constant.RQF_CHECK_CODE, checkCode);
		SendRequest(optType, mDataMap, handler, cmd);
	}
	
	/*
	 * 提交新的密码
	 */
	public void sendResetPassword(Handler handler, int cmd, String type,
			String password) {
		HashMap<String, String> mDataMap = new HashMap<String, String>();
		mDataMap.put(Constant.RQF_PASSWORD, /*encrpt(password)*/password);
		SendRequest(type, mDataMap, handler, cmd);
	}

	/*
	 * 获取快捷支付设置信息接口
	 */
	public void sendQueryPaySettings(Handler handler, int cmd) {
		SimpleRequest(Constant.OP_QUERYPAYSETTINGS, handler, cmd);
	}

	/*
	 * 保存快捷支付设置信息接口
	 */
	public void sendModifyPaySettings(Handler handler, int cmd,
			String password, String payType) {
		HashMap<String, String> mDataMap = new HashMap<String, String>();
		mDataMap.put(Constant.RQF_PAY_PASSWORD, password);
		mDataMap.put(Constant.RQF_PAYTYPE, payType);
		SendRequest(Constant.OP_MODIFYPAYSETTINGS, mDataMap, handler, cmd);
	}

	// 信息录入
	public void sendGetPucInputInfo(Handler handler, int cmd,
			String chargeType, String city, String key) {
		HashMap<String, String> mDataMap = new HashMap<String, String>();
		mDataMap.put(Constant.RQF_CHARGETYPE, chargeType);
		mDataMap.put(Constant.RQF_CITY, city);
		if(key != null && !"".equals(key))
			mDataMap.put(Constant.KEY, key);
		
		SendRequest(Constant.OP_GETPUCINPUTINFO, mDataMap, handler, cmd);
	}

	 //账单选择
	public void sendGetChargeBills(Handler handler, int cmd, String chargeType,
			String inputInfo) {
		HashMap<String, String> mDataMap = new HashMap<String, String>();
		mDataMap.put(Constant.RQF_CHARGETYPE, chargeType);
		mDataMap.put(Constant.RQF_INPUTINFO, inputInfo);
		SendRequest(Constant.OP_GETCHARGEBILLS, mDataMap, handler, cmd);
	}
	
//	public void sendGetChargeBills(Handler handler, int cmd, String chargeType,
//			String inputInfo) {
//		HashMap<String, String> mDataMap = new HashMap<String, String>();
//		mDataMap.put(Constant.RQF_CHARGETYPE, chargeType);
//		mDataMap.put(Constant.RQF_INPUTINFO, inputInfo);
//		SendRequest(Constant.OP_GETCHARGEBILLS, mDataMap, handler, cmd);
//	}

	// 账单确认
	public void sendConfirmBillInfo(Handler handler, int cmd, String billId) {
		HashMap<String, String> mDataMap = new HashMap<String, String>();
		mDataMap.put(Constant.RPF_BILLID, billId);
		SendRequest(Constant.OP_CONFIRMBILLINFO, mDataMap, handler, cmd);
	}

	// 创建缴费流水�
	public void sendCreateBillNo(Handler handler, int cmd, String billId,String payment) {
		HashMap<String, String> mDataMap = new HashMap<String, String>();
		mDataMap.put(Constant.RPF_BILLID, billId);
		mDataMap.put(Constant.RPF_AMOUNT, payment);
		SendRequest(Constant.OP_CREATEBILLNO, mDataMap, handler, cmd);
	}

	// 获取生活缴费的历史
	public void sendGetLifePayHistory(Handler handler, int cmd, String mHistoryType) {
		HashMap<String, String> mDataMap = new HashMap<String, String>();
		mDataMap.put(Constant.RQF_CHARGETYPE, mHistoryType);
		SendRequest(Constant.OP_GETHISTORYBILLS, mDataMap, handler, cmd);
	}
	
	
	// ---------- 代付相关 ----------
	// 1. 申请代付设置
	public void sendAgentPay(Handler handler, int cmd, String bizNo,
			String bizType, String isAddContact, String agentAccount,
			String agentMemo) {
		HashMap<String, String> mDataMap = new HashMap<String, String>();
		mDataMap.put(Constant.RQF_BIZNO, bizNo);
		mDataMap.put(Constant.RQF_BIZTYPE, bizType);
		mDataMap.put(Constant.RQF_IS_ADDCONTACT, isAddContact);

		// 下面区别处理链接代付和指定人代付
		if (agentAccount != null)
			mDataMap.put(Constant.RQF_AGENT_ACCOUNT, agentAccount);
		if (agentMemo != null)
			mDataMap.put(Constant.RQF_AGENT_MEMO, agentMemo);
		SendRequest(Constant.OP_AGENTPAY_APPLY, mDataMap, handler, cmd);
	}

	// 2. 取消代付设置
	public void cancelAgentPay(Handler handler, int cmd, String bizNo,
			String bizType, String agentMemo) {
		HashMap<String, String> mDataMap = new HashMap<String, String>();
		mDataMap.put(Constant.RQF_BIZNO, bizNo);
		mDataMap.put(Constant.RQF_BIZTYPE, bizType);

		if (agentMemo != null)
			mDataMap.put(Constant.RQF_AGENT_MEMO, agentMemo);
		SendRequest(Constant.OP_AGENTPAY_CANCEL, mDataMap, handler, cmd);
	}

	// 3. 代付详情查询
	public void sendQueryAgentDetail(Handler handler, int cmd, String tradeNo,
			String bizType) {
		HashMap<String, String> mDataMap = new HashMap<String, String>();
		mDataMap.put(Constant.RQF_BIZNO, tradeNo);
		mDataMap.put(Constant.RQF_BIZTYPE, bizType);

		SendRequest(Constant.OP_QUERYAGENTDETAIL, mDataMap, handler, cmd);
	}

	// ---------- 条码支付 ----------
	// 1. 获取条码字符
	public void sendGetBarCodeReq(Handler handler, int cmd, String loginAccount) {
		HashMap<String, String> mDataMap = new HashMap<String, String>();
		mDataMap.put(Constant.RQF_LOGIN_ACCOUNT, loginAccount);
		SendRequest(Constant.OP_GET_BARCODE, mDataMap, handler, cmd);
	}

	// 3. 用户取消当前的条码支付交易
	public void sendCancelBarcodePay(Handler handler, int cmd,
			String loginAccount, String tradeNo) {
		HashMap<String, String> mDataMap = new HashMap<String, String>();
		mDataMap.put(Constant.RQF_LOGIN_ACCOUNT, loginAccount);
		mDataMap.put(Constant.RQF_TRADE_NO, tradeNo);
		SendRequest(Constant.OP_CANCEL_BARCODE_TRADE, mDataMap, handler, cmd);
	}

	// 1. 获取条码相关参数——认证模式
	public void sendGetBarCodebyLicense(Handler handler, int cmd,
			String curAccount, String isActive) {
		HashMap<String, String> mDataMap = new HashMap<String, String>();
		mDataMap.put(Constant.RQF_QRRCODEPAY_ACCOUNT, curAccount);
		mDataMap.put(Constant.RQF_SAFEPAY_IS_ACTIVE, isActive);
		SendRequest(Constant.OP_GET_BARCODE_LICENSE, mDataMap, handler, cmd);
	}

	// ---------- 悦享拍 ----------

	// 2. 悦享拍-获得信息
	public void sendGetPaipaiQrcodeInfo(Handler handler, int cmd,
			String account, String qrcodeKey) {
		HashMap<String, String> mDataMap = new HashMap<String, String>();
		mDataMap.put(Constant.RQF_QRRCODEPAY_ACCOUNT, account);
		mDataMap.put(Constant.RQF_QRRCODEPAY_KEY, qrcodeKey);
		SendRequest(Constant.OP_GET_QRRCODEPAY_QRCODEINFO, mDataMap, handler,
				cmd);
	}

	// 3. 悦享拍-创建捐赠交易
	public void sendCreatePaipaiDonatePay(Handler handler, int cmd,
			String loginAccount, String qrcodeKey, String payAmount,
			String payMemo) {
		HashMap<String, String> mDataMap = new HashMap<String, String>();
		mDataMap.put(Constant.RQF_LOGIN_ACCOUNT, loginAccount);
		mDataMap.put(Constant.RQF_QRRCODEPAY_DONATE_KEY, qrcodeKey);
		mDataMap.put(Constant.RQF_QRRCODEPAY_DONATE_AMOUNT, payAmount);
		mDataMap.put(Constant.RQF_QRRCODEPAY_DONATE_SIGN, payMemo);
		SendRequest(Constant.OP_CREATE_QRRCODEPAY_DONATE, mDataMap, handler,
				cmd);
	}

	// ---------- 条码收银 ----------
	// 1. 创建针对当前二维码的条码交易
	public void sendBarcodeGathering(Handler handler, int cmd,
			String curAccount, String barcodeKey, String amount, String memo,
			String loginFlag, String isActivation) {
		HashMap<String, String> mDataMap = new HashMap<String, String>();
		mDataMap.put(Constant.RQF_BARCODEGATHER_ACCOUNT, curAccount);
		mDataMap.put(Constant.RQF_BARCODEGATHER_KEY, barcodeKey);
		mDataMap.put(Constant.RQF_BARCODEGATHER_AMOUNT, amount);
		mDataMap.put(Constant.RQF_BARCODEGATHER_MEMO, memo);
		mDataMap.put(Constant.RQF_BARCODEGATHER_LOGINFLAG, loginFlag);
		mDataMap.put(Constant.RQF_BARCODEGATHER_ACTIVEFLAG, isActivation);

		SendRequest(Constant.OP_CREATE_BARCODEGATHER_TRADE, mDataMap, handler,
				cmd);
	}

	// ---------- 保存地理位置信息 ----------
	public void sendActionLocateInfo(Handler handler, int cmd,
			String curAccount, String curCellId, String curCellIdNet,
			String curCellIdLac, String latitude, String longititude,
			String accuracyRange, String productId, int businessId) {
		HashMap<String, String> mDataMap = new HashMap<String, String>();
		mDataMap.put(Constant.RQF_QRRCODEPAY_ACCOUNT, curAccount);
		mDataMap.put(Constant.RQF_LOCATE_CELLID_CURENT, curCellId);
		mDataMap.put(Constant.RQF_LOCATE_CELLID_NEIGHBOR1, curCellIdNet);
		mDataMap.put(Constant.RQF_LOCATE_CELLID_NEIGHBOR2, curCellIdLac);
		mDataMap.put(Constant.RQF_LOCATE_GPS_LATITUDE, latitude);
		mDataMap.put(Constant.RQF_LOCATE_GPS_LONGITUDE, longititude);
		mDataMap.put(Constant.RQF_LOCATE_DEVIATION_RANGE, accuracyRange);
		mDataMap.put(Constant.RQF_PRODUCT_ID, productId);
		mDataMap.put(Constant.RQF_LOCATE_ACTION_BUSINESSID,
				String.valueOf(businessId));

		SendRequest(Constant.OP_LOCATE_UPLOAD_INFO, mDataMap, handler, cmd);
	}

	// -------------webApp---------------------
	// 商户授权
	public void sendAuthReq(Handler handler, int cmd, String partnerId,
			boolean authResult) {
		HashMap<String, String> mDataMap = new HashMap<String, String>();
		mDataMap.put(Constant.RQF_PARTNER_ID, partnerId);
		if (authResult)
			mDataMap.put(Constant.RQF_AUTH_RESULT, Constant.AUTHO_YES);
		else
			mDataMap.put(Constant.RQF_AUTH_RESULT, Constant.AUTHO_NO);
		SendRequest(Constant.OP_LOGIN_AUTH, mDataMap, handler, cmd);
	}

	// 创建订单
	public void sendCreateOrder(Handler handler, int cmd, String orderToken) {
		HashMap<String, String> mDataMap = new HashMap<String, String>();
		mDataMap.put(Constant.RQF_ORDER_TOKEN, orderToken);

		SendRequest(Constant.OP_CREATE_EXORDER, mDataMap, handler, cmd);
	}

	private static void log(String s) {
		// Log.d(TAG, "[BookImporter] " + s);
	}

	public static void chmod(String permission, String path) {
		try {
			String command = "chmod " + permission + " " + path;
			Runtime runtime = Runtime.getRuntime();
			runtime.exec(command);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	// 在账户信息中添加自动完成联系�
	public ArrayList<String> getContactData(RootActivity context) {
		ArrayList<String> data = null;
		JSONObject myJsonObject = null;
		if (isFile(context).exists()) {
			myJsonObject = readContactToFile(context);
			if (myJsonObject != null) {
				data = getContactInfoSuccess(myJsonObject);
			}
		}
		return data;
	}

	private ArrayList<String> getContactInfoSuccess(JSONObject myJsonObject) {
		ArrayList<String> data = new ArrayList<String>();
		JSONArray array = myJsonObject.optJSONArray(Constant.RPF_CONTACTLISTS);
		if (array == null) {
			return null;
		}
		for (int i = 0; i < array.length(); i++) {
			JSONObject itemObj = array.optJSONObject(i);
			if (itemObj != null) {
				JSONArray arrayContact = itemObj
						.optJSONArray(Constant.PRF_CONTACTS);
				if (arrayContact != null) {
					for (int j = 0; j < arrayContact.length() + 1; j++) {

						JSONObject contactObj = arrayContact.optJSONObject(j);
						if (contactObj != null) {
							String account = contactObj
									.optString(Constant.PRF_CONTACTACCOUNT);

							data.add(account);
						}

					}
					return data;
				}
			}
		}
		return null;
	}

	/*
	 * read file
	 */
	private JSONObject readContactToFile(RootActivity context) {
		JSONObject jObject = null;

		try {
			File file = isFile(context);
			if (file.exists()) {
				FileInputStream in = new FileInputStream(file);
				if (in.available() > 0) {
					jObject = new JSONObject(
							BaseHelper.convertStreamToString(in));
				}
				in.close();
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return jObject;
	}

	/*
	 * Get this file
	 */
	private File isFile(RootActivity context) {
		String strUserId = context.getUserId();
		File file = new File(FILE_PATH + context.getPackageName() + "/files/"
				+ strUserId + "/contact/", FILE_NAME);
		return file;
	}

	private File isFile(RootActivity context, String filaName) {
		String strUserId = context.getUserId();
		File file = new File(FILE_PATH + context.getPackageName() + "/files/"
				+ strUserId + "/contact/", filaName);
		return file;
	}

	//

	/**
	 * 判断是否是android4.0 以及根据后台进程限制 来进行相应的push消息处理
	 * sys.settings_secure_version
	 * @return
	 */
	public boolean isStartPushService() {
		boolean ret = true;
		if (Integer.parseInt(Build.VERSION.SDK) < 14) {
			return ret;
		}
		// 标准 59;不允许 55/69;一个 61;两个 63;三个 65;四个 67
		@SuppressWarnings("rawtypes")
		Class cla = null;
		Object value = null;
		try {
			cla = Class.forName("android.os.SystemProperties");
			@SuppressWarnings("rawtypes")
			Class[] claArrayTypes = { String.class };
			Method meth = cla.getMethod("get", claArrayTypes);
			// Method meth = cla.getMethod("secure", claArrayTypes);
			Object[] arglist = { "sys.settings_secure_version" };
			value = meth.invoke(null, arglist);
		} catch (Exception e) {
			e.printStackTrace();
		}
		System.out.println("value====="+value);
		if (value != null && ("55".equals(value)||"69".equals(value)||"65".equals(value)||"67".equals(value))) {
			ret = false;
			System.out.println("1ret====="+ret);
		} 
		System.out.println("2ret====="+ret);
		return ret;
	}

	// 判断手机是否有root权限
	private boolean rooted() {
		boolean ret = false;
		@SuppressWarnings("rawtypes")
		Class cla = null;
		Object value = null;
		try {
			cla = Class.forName("android.os.SystemProperties");
			@SuppressWarnings("rawtypes")
			Class[] claArrayTypes = { String.class };
			Method meth = cla.getMethod("get", claArrayTypes);
			// Method meth = cla.getMethod("secure", claArrayTypes);
			Object[] arglist = { "ro.secure" };
			value = meth.invoke(null, arglist);
		} catch (Exception e) {
			e.printStackTrace();
		}
		if (value != null && "1".equals(value)) {
			ret = false;
		} else if (value != null && "0".equals(value)) {
			ret = true;
		}
		return ret;
	}
	
	// 判断手机的数据网络开关的状态
	public static boolean getMobileDataStatus(Context context) {
		ConnectivityManager conMgr = 
 				(ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
 		
 		Class<?> conMgrClass = null; 				// ConnectivityManager类
        Field iConMgrField = null; 					// ConnectivityManager类中的字段
        Object iConMgr = null; 						// IConnectivityManager类的引用
        Class<?> iConMgrClass = null; 				// IConnectivityManager类
        Method getMobileDataEnabledMethod = null; 	// getMobileDataEnabled方法

	    try {
	    	// 取得ConnectivityManager类
	        conMgrClass = Class.forName(conMgr.getClass().getName());
	         
	        // 取得ConnectivityManager类中的对象mService
	        iConMgrField = conMgrClass.getDeclaredField("mService");
	         
	        // 设置mService可访问
	        iConMgrField.setAccessible(true);
	         
	        // 取得mService的实例化类IConnectivityManager
	        iConMgr = iConMgrField.get(conMgr);
	         
	        // 取得IConnectivityManager类
	        iConMgrClass = Class.forName(iConMgr.getClass().getName());
	         
	        // 取得IConnectivityManager类中的getMobileDataEnabled(boolean)方法
	        getMobileDataEnabledMethod = iConMgrClass.getDeclaredMethod("getMobileDataEnabled");
	         
	        // 设置getMobileDataEnabled方法可访问
	        getMobileDataEnabledMethod.setAccessible(true);
	         
	        // 调用getMobileDataEnabled方法
	        return (Boolean) getMobileDataEnabledMethod.invoke(iConMgr);
        } catch (ClassNotFoundException e) {
         	e.printStackTrace();
        } catch (NoSuchFieldException e) {
         	e.printStackTrace();
        } catch (SecurityException e) {
         	e.printStackTrace();
        } catch (NoSuchMethodException e) {
         	e.printStackTrace();
        } catch (IllegalArgumentException e) {
         	e.printStackTrace();
        } catch (IllegalAccessException e) {
         	e.printStackTrace();
        } catch (InvocationTargetException e) {
     	   // TODO Auto-generated catch block
     	   e.printStackTrace();
        }
         
        return false;
   }
	
	private String getTimes() {  
        long ut = SystemClock.elapsedRealtime() / 1000;  
        if (ut == 0) {  
            ut = 1;  
        }  
//        int m = (int) ((ut / 60) % 60);  
//        int h = (int) ((ut / 3600));  
        long minutes = ut/60; 
        
        return String.valueOf(minutes); 
    } 
	
	// 判断手机是否有360
	private boolean haveAppforKey(Context context, String appKey) {
		boolean ret = false;
		
		String PACKAGE_OPDA = "cn.opda.a.phonoalbumshoushou";		//安卓优化大师
	    String PACKAGE_360 = "com.qihoo360.mobilesafe";		//360手机卫士
		String PACKAGE_QQ = "com.tencent.qqpimsecure";			//qq手机管家
		String wantedPkg = "";
		
		if (appKey.equals(Constant.PKG_QIHOO)) {
			wantedPkg = PACKAGE_360;
		} else if (appKey.equals(Constant.PKG_TENCENT)) {
			wantedPkg = PACKAGE_QQ;
		} else if (appKey.equals(Constant.PKG_OPDA)) {
			wantedPkg = PACKAGE_OPDA;
		}
		
		if (wantedPkg.length() > 0) {
			List<PackageInfo> apps = new ArrayList<PackageInfo>();
			PackageManager pManager = context.getPackageManager();
			
			//获取手机内所有应用
			List<PackageInfo> pkgList = pManager.getInstalledPackages(0);
			System.out.println("getPackageInfo size="+pkgList.size());
			
			for (int i = 0; i < pkgList.size(); i++) {
				PackageInfo pkg = (PackageInfo) pkgList.get(i);
				if (pkg.packageName != null && pkg.packageName.contains(wantedPkg)) {
					ret = true;
					System.out.println("packageName:"+pkg.packageName +" haveAppforKey="+ret +" for app:"+appKey);
					break;
				}
			}
		}
		
		return ret;
	}

	/*
	 * 获取本地成功登录的账号列表
	 */
	public UserInfo getLatestLoginedUser(Context context) {
		Constant.ALIPAY_INFO = BaseHelper.getDesKey(context);
		DBHelper db = new DBHelper(context);

		UserInfo userInfo = db.getLastLoginUser(null);
		db.close();

		if (userInfo != null) {
			// if(userInfo.userName.length()>0 &&
			// userInfo.userPassword.length()>0) {
			if (userInfo.userAccount != null && userInfo.userAccount.length() > 0) {
				LogUtil.logMsg(Constant.LOG_LEVEL_DEBUG, TAG,
						"getLatestLoginedUser userName=" + userInfo.userAccount
								+ ", userId=" + userInfo.userId + ", type="
								+ userInfo.type);
				return userInfo;
			}
		}
		return null;
	}

	public void sendActive(Handler handler, int cmd, Context context) {
		try {
			JSONObject requestData = new JSONObject();
			requestData.put(Constant.CHANNELS, CacheSet.getInstance(context)
					.getString(Constant.CHANNELS));
			requestData.put(Constant.RQF_CLIENT_ID, mConfigData.getClientId());
			requestData
					.put(Constant.RQF_PRODUCT_ID, mConfigData.getProductId());
			requestData.put(Constant.RQF_PRODUCT_VERSION,
					mConfigData.getProductVersion());
			requestData.put(Constant.RQF_SCREEN_WIDTH,
					mConfigData.getScreenWidth());
			requestData.put(Constant.RQF_SCREEN_HIGH,
					mConfigData.getScreenHeight());
			requestData
					.put(Constant.RQF_USER_AGENT, mConfigData.getUserAgent());
			requestData.put(Constant.RQF_MOBILEBRAND, Build.BRAND);
			requestData.put(Constant.RQF_MOBILEMODEL, Build.MODEL);
			requestData.put(Constant.RQF_ACCESS_POINT,
					mConfigData.getAccessPoint());
			requestData.put(Constant.RQF_CLIENTTYPE, "phone");
			requestData.put(Constant.RQF_SYSTEMTYPE, "android");
			requestData.put(Constant.RQF_SYSTEMVERSION, Build.VERSION.RELEASE);
			requestData.put(Constant.RQF_APPTYPE, "client");
			requestData.put(Constant.ROOTED, rooted());
			requestData.put(Constant.PUSH_SWITCH, 
					CacheSet.getInstance(context).getString(Constant.PUSH_SWITCH));
			ServiceManager serviceManager = new ServiceManager(context);
			requestData.put(Constant.PUSH_RECORD, serviceManager.getServiceRecord());
			//上报当前用户手机的数据网络开关状态
			requestData.put(Constant.DATA_CONNECTIVITY_FLAG, getMobileDataStatus(context));
			requestData.put(Constant.LAUNCH_TIME, getTimes());
			requestData.put(Constant.PKG_QIHOO, haveAppforKey(context, Constant.PKG_QIHOO));
			requestData.put(Constant.PKG_TENCENT, haveAppforKey(context, Constant.PKG_TENCENT));
			requestData.put(Constant.PKG_OPDA, haveAppforKey(context, Constant.PKG_OPDA));
			requestData.put(Constant.AWID, StorageStateInfo.getInstance().getValue(Constants.STORAGE_UUID));
			
			requestData.put(Constant.RQF_CLIENTPOSITION,
					DeviceHelper.getCellInfo(context));
			sendAsynchronousRequest(Constant.OP_REPORT_ACTIVE, requestData,
					handler, cmd);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void sendAsynchronousRequest(final String requestType,
			final JSONObject jsonRequest, final Handler callback,
			final int myWhat) {
		// Asynchronous
		new Thread(new Runnable() {
			public void run() {
				JSONObject jsonResponse = null;
				int resultStatus = 0;
				String memo = null;
				try {
					jsonRequest.put(Constant.RQF_OPERATION_TYPE, requestType);
					StorageStateInfo.getInstance().putValue(
							Constants.STORAGE_REQUESTTYPE, requestType);

					String sessionId = mApplication.getConfigData()
							.getSessionId();
					jsonRequest.put(Constant.RPF_SESSION_ID, sessionId);

					String alipayUrl = Constant.getAlipayURL(mApplication)
							.replace("https", "http")
							.replace("mobilecs.htm", "mtk.htm");
					// String alipayUrl =
					// "http://mobile.n41.alipay.net/home/mtk.htm";
					APHttpClient httpClient = new APHttpClient(alipayUrl,
							mApplication);
					String resp = httpClient.sendSynchronousRequest(
							jsonRequest.toString(), false);

					if (resp != null) {
						jsonResponse = new JSONObject(resp);
						resultStatus = jsonResponse
								.getInt(Constant.RPF_RESULT_STATUS);
						memo = jsonResponse.optString(Constant.RPF_MEMO);
					}else {
                        if (httpClient.errorType == APHttpClient.SSL_ERROR) {
                            resultStatus = 1; // tbd
                        } else if (httpClient.errorType == APHttpClient.IO_ERROR) {
                            resultStatus = 0; // tbd
                        } else {
                            resultStatus = 0; // tbd
                        }
                    }
				} catch (Exception e) {
					e.printStackTrace();
				}

				//
				try {
					Responsor responsor = new Responsor(requestType,
							resultStatus, jsonResponse, memo);
					Message msg = new Message();
					msg.what = myWhat;
					msg.arg1 = resultStatus;
					msg.obj = responsor;
					callback.sendMessage(msg);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}).start();
	}

	// 在当前账户信息中获取最近联系人记录
	public String[] getHistoryContactGroup(RootActivity context) {
		File file = isFile(context, FILE_NAME_HISTORY);
		String[] tUser = null;

		if (file.exists()) {
			FileInputStream in;
			try {
				in = new FileInputStream(file);
				StringTokenizer tToken = new StringTokenizer(
						BaseHelper.convertStreamToString(in), ";");
				if (tToken.countTokens() > 0)
					tUser = new String[tToken.countTokens()];

				int count = 0;
				while (tToken.hasMoreTokens()) {
					tUser[count++] = tToken.nextToken();
				}
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return tUser;
	}

	/*
	 * add userId to file for contact history
	 */
	public void addUserIdToFile(RootActivity context, String userId) {
		File file = isFile(context, FILE_NAME_HISTORY);

		try {
			if (file.exists()) {
				FileInputStream in = new FileInputStream(file);
				String tWantDelete = null;
				if (in.available() > 0) {
					String tString = BaseHelper.convertStreamToString(in);
					System.out.println("original tString=" + tString);
					if (tString.indexOf(userId) < 0) {
						// 没有重复的，则写入
						// tString += ";" + userId;
						tString = tString + userId + ";";

						// writeUserIdToFile(file, tString);
					}

					// 写入后，需要判断是否超出需要记录的个数；超过则做删除处理——删除第一个
					StringTokenizer tToken = new StringTokenizer(tString, ";");
					System.out.println("tToken count=" + tToken.countTokens());

					if (tToken.countTokens() > Constant.mHistoryContactCount) {
						// 超出期望的记录个数
						tWantDelete = tToken.nextToken() + ";";
						System.out.println("tWantDelete=" + tWantDelete);
						// deleteUserIdToFile(file, tWantDelete);
						tString = tString.replace(tWantDelete, "");
					}

					writeUserIdToFile(file, tString);
				}
				in.close();
			} else {
				String strUserId = context.getUserId();

				File dire = new File(FILE_PATH + context.getPackageName()
						+ "/files/" + strUserId + "/contact/");
				if (!dire.exists()) {
					dire.mkdirs();
				}
				File fileName = new File(FILE_PATH + context.getPackageName()
						+ "/files/" + strUserId + "/contact/",
						FILE_NAME_HISTORY);
				fileName.createNewFile();
				writeUserIdToFile(fileName, userId + ";");
			}

		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/*
	 * write userId to file
	 */
	private void writeUserIdToFile(File fileName, String userId) {
		try {
			FileOutputStream out = new FileOutputStream(fileName);
			out.write(userId.getBytes());
			out.flush();
			out.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public void sendRemindMessage(Context context, final Handler mHandler,
			final int myWhat, String outTradeNO, String tradeNO) {
		HashMap<String, String> notifyData = new HashMap<String, String>();
		notifyData.put(Constant.RPF_OUTTRADENO, outTradeNO);
		notifyData.put(Constant.RPF_TRADENO, tradeNO);
		notifyData.put(Constant.RQF_CLIENT_ID, mConfigData.getClientId());
		JSONObject requestData = new JSONObject(notifyData);
		SendJSONRequest(Constant.OP_NOTIFYDELIVERY, requestData, mHandler,
				myWhat);
	}

	public void sendRemindReceiver(Context context, Handler mHandler, int myWhat, String preBillNO) {
		HashMap<String, String> notifyData = new HashMap<String, String>();
		notifyData.put(Constant.RPF_PREBILLNO, preBillNO);
		notifyData.put(Constant.RQF_CLIENT_ID, mConfigData.getClientId());
		JSONObject requestData = new JSONObject(notifyData);
		SendJSONRequest(Constant.OP_SENDCOLLECTIONMSG, requestData, mHandler,myWhat);
	}

	public void dismissDialog() {
		if(dialog != null){
			dialog.getDialog().setCancelable(true);
			dialog.dismiss();
		}
	}

	public void setDialogCancelable(boolean b) {
		if(dialog != null && dialog.getDialog() != null){
			dialog.getDialog().setCancelable(false);
		}
	}
}
