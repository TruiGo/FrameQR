package com.alipay.android.common.data;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.os.Handler;
import android.os.Message;

import com.alipay.android.client.RootActivity;
import com.alipay.android.client.constant.AlipayHandlerMessageIDs;
import com.alipay.android.client.constant.Constant;
import com.alipay.android.client.util.BaseHelper;
import com.alipay.android.util.LogUtil;

public class AlipayUserState {
	static final String TAG = "AlipayUserState";

	//所使用账户的来源
	public static final int ACCOUNT_TYPE_CLIENT = 0;
	public static final int ACCOUNT_TYPE_SAFEPAY = 1;
	
	//用户状态模式
	public static final int ACCOUNT_STATE_NO_LOGIN = 0;
	public static final int ACCOUNT_STATE_AUTH_PREPARE = 1;
	public static final int ACCOUNT_STATE_AUTH_SAFEPAY = 2;
	public static final int ACCOUNT_STATE_LOGIN = 3;
	public static final int ACCOUNT_STATE_LOGIN_SAFEPAY = 4;
	
	private final int UPDATE_CURRENT_USER_STATE = 1;

	private int mUserState = ACCOUNT_STATE_NO_LOGIN;
	
	private UserInfo mCurUser = null;
	private JSONArray mContactsArray = null;
	
	private String mUserAcount = "";
	private String mUserId = "";
	private String mUserType = "";
	private int mUserAcountFrom = 0;
	
	//单例模式
	private static AlipayUserState myself = null;
	
	AlipayUserState() {
		//
	}
	
	public static AlipayUserState getInstance() {
		if (myself == null) {
			myself = new AlipayUserState();
		}
		return myself;
	}
	
	public void reset() {
		mCurUser = null;
		mContactsArray = null;
		
		mUserAcount = "";
		mUserId = "";
		mUserType = "";
		mUserAcountFrom = 0;
		mUserState = ACCOUNT_STATE_NO_LOGIN;
		
		mCurUser = null;
	}
	
	public boolean isActiveAccount(String curUserId) {
		boolean ret = false;
		
		if (myself.mUserAcountFrom == ACCOUNT_TYPE_SAFEPAY) {
			ret = true;
		} else {
			//判断userid是否在安全认证list中
			if (mUserId == null || mUserId.equals("")) {
				ret = false;
			} else {
				if(mContactsArray != null && mContactsArray.length()>0) {
					for(int j = 0; j<mContactsArray.length();j++) {
						JSONObject mContacts = mContactsArray.optJSONObject(j);
						String userId = mContacts.optString(Constant.SAFE_PAY_RESULT_USERID);
						if (userId != null && userId.equals(myself.mUserId)) {
							ret = true;
							break;
						}
					}
				}
			}
		}
		LogUtil.logMsg(Constant.LOG_LEVEL_DEBUG, TAG, 
				"isActiveAccount mUserId="+mUserId);
		return ret;
	}
	
	public String getValidAcount() {
		if (myself.mUserAcountFrom == ACCOUNT_TYPE_SAFEPAY) {
			return myself.mUserId;
		} else {
			return myself.mUserAcount;
		}
	}
	
	//
	public void setUserInfo(UserInfo curUser) {
		myself.mCurUser = curUser;
		
		myself.mUserAcount = mCurUser.userAccount;
		myself.mUserId = mCurUser.userId;
		myself.mUserType = mCurUser.type;
		myself.mUserAcountFrom = ACCOUNT_TYPE_CLIENT;
		
		if (myself.mUserType.equals("alipay")) {
			//当前仅有支付宝类型的login账户可以被进行认证
			myself.mUserState = ACCOUNT_STATE_AUTH_PREPARE;
		} else if (myself.mUserType.equals("taobao")) {
			myself.mUserState = ACCOUNT_STATE_NO_LOGIN;
		}
	}
	
	//
	public void setSafepayAuthList(JSONArray accountData) {
		myself.mContactsArray = accountData;
		
		if (myself.mUserAcount.equals("")) {
			JSONObject mContacts = accountData.optJSONObject(0);
			String tUserAcount = mContacts.optString(Constant.SAFE_PAY_RESULT_USERLOGINID);
			String tUserId = mContacts.optString(Constant.SAFE_PAY_RESULT_USERID);
			
			if(tUserAcount != null && !tUserAcount.equals("")) {
				myself.mUserAcount = tUserAcount;
				myself.mUserId = tUserId;
				myself.mUserAcountFrom = ACCOUNT_TYPE_SAFEPAY;
				myself.mUserState = ACCOUNT_STATE_AUTH_SAFEPAY;
			}
		} else {	//已经从本地登录列表中获得了账号
			//没法再进行状态转换，因为不知道当前账号是否已经登录
		}
	}
	
	public void updateSafepayAuthList(RootActivity context, Handler handler) {
		if (handler == null) {
			handler = mHandler;
		}
		//向快捷支付服务请求本地认证账号列表
		BaseHelper.checkStatus(context, handler, 
				AlipayHandlerMessageIDs.SAFEPAY_CHECK_GET_AUTHLIST, 
				null, "", Constant.SAFE_GET_AUTHLIST, 0, null);
	}
	
	// the handler use to receive the install check result.
	private Handler mHandler = new Handler() 
	{
		public void handleMessage(Message msg) {	
			try {
				switch (msg.what)
				{
					case AlipayHandlerMessageIDs.SAFEPAY_CHECK_GET_AUTHLIST:
						safepayActionHandle(msg);
					break; 
					
					case UPDATE_CURRENT_USER_STATE:
						//获取到新的快捷支付list，更新当前用户状态——主要对登录列表账户
						updateCurAccount();
					break; 
				}
				super.handleMessage(msg);
			}
			catch(Exception e)
			{
				e.printStackTrace();
			}
		}
	};
	
	public void updateCurAccount() {
		if (myself.mUserAcountFrom == ACCOUNT_TYPE_SAFEPAY) {
			return;
		} else if (myself.mUserId != null){
			//判断当前用户是否在新的快捷支付列表中
			if (myself.mUserId.equals("")) {
				//
			} else {
				if (isActiveAccount(myself.mUserId)) {
					switch (myself.mUserState)
					{
						case ACCOUNT_STATE_AUTH_PREPARE:
							myself.mUserState = ACCOUNT_STATE_AUTH_SAFEPAY;
						break; 
						
						case ACCOUNT_STATE_LOGIN:
							myself.mUserState = ACCOUNT_STATE_LOGIN_SAFEPAY;
						break;
					}
				}
			}
			LogUtil.logMsg(Constant.LOG_LEVEL_DEBUG, TAG, "updateCurAccount mUserState="+myself.mUserState);
			return ;
		}
		
		return ;
	}
	
	public void updateUserState(boolean isLogin) {
		if (isLogin) {
			switch (myself.mUserState)
			{
				case ACCOUNT_STATE_NO_LOGIN:
				case ACCOUNT_STATE_AUTH_PREPARE:
					myself.mUserState = ACCOUNT_STATE_LOGIN;
				break; 
				
				case ACCOUNT_STATE_AUTH_SAFEPAY:
					myself.mUserState = ACCOUNT_STATE_LOGIN_SAFEPAY;
				break;
			}
		} else {
			switch (myself.mUserState)
			{
				case ACCOUNT_STATE_LOGIN:
					myself.mUserState = ACCOUNT_STATE_AUTH_PREPARE;
				break; 
				
				case ACCOUNT_STATE_LOGIN_SAFEPAY:
					myself.mUserState = ACCOUNT_STATE_AUTH_SAFEPAY;
				break;
			}
		}
	}
	
	public void setUserState(int curState) {
		myself.mUserState = curState;
	}
	
	public int getUserState() {
		return myself.mUserState;
	}
	
	//设置当前用户名，仅一次
	public void setUserAccount(String userAcount) {
		myself.mUserAcount = userAcount;
	}
	
	public String getUserAccount() {
		return myself.mUserAcount;
	}
	
	//设置当前用户的支付宝内部id，仅一次
	public void setUserId(String userId) {
		myself.mUserId = userId;
	}
	
	public String getUserId() {
		return myself.mUserId;
	}

	public String getUserType() {
		return myself.mUserType;
	}
	
	public void setAccountFrom(int type) {
		myself.mUserAcountFrom = type;
	}
	
	
	private void safepayActionHandle(Message msg){
		int ret = msg.arg1;
		String strRet = (String)msg.obj;
		LogUtil.logMsg(Constant.LOG_LEVEL_WARNING, TAG, strRet);
		
		if(ret == 1) {	//成功
			if (strRet != null) {
				try {
					JSONObject objContent = BaseHelper.string2JSON(strRet, ";");
					String result = objContent.getString(Constant.SAFE_PAY_RESULT_ARRAY);

					if (result !=null && !result.equals("")) {
						JSONObject tObject = new JSONObject(result);
						JSONArray tContactsArray = tObject.optJSONArray(Constant.SAFE_PAY_RESULT_ACCOUNTDATA);
						if(tContactsArray != null && tContactsArray.length()>0) {
							myself.setSafepayAuthList(tContactsArray);
							//更新当前用户状态
							Message message = new Message(); 
							message.what = UPDATE_CURRENT_USER_STATE;
							mHandler.sendMessage(message);
						} else {
							//没有获取到有效的认证账号
						}
					} else {
						//没有获取到有效的认证账号
					}
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		} else { //失败
			
		}
	}
}  