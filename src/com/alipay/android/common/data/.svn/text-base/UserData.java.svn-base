package com.alipay.android.common.data;

import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Observable;
import java.util.Observer;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.BitmapFactory.Options;
import android.os.Handler;
import android.os.Message;
import android.view.View;

import com.alipay.android.appHall.Helper;
import com.alipay.android.appHall.common.BitmapDownloadListener;
import com.alipay.android.client.AlipayAccountManager;
import com.alipay.android.client.AlipayApplication;
import com.alipay.android.client.Main;
import com.alipay.android.client.MessageFilter;
import com.alipay.android.client.constant.Constant;
import com.alipay.android.client.util.AlipayDataStore;
import com.alipay.android.client.util.DataHelper;
import com.alipay.android.client.util.Responsor;
import com.alipay.android.client.util.Utilz;
import com.alipay.android.ui.bean.Voucher;

public class UserData extends Observable implements Saveable {
    private Context mContext;
    /**
     * 真实名字
     */
    private String mRealName;
    
    /**
     * 淘宝账户名
     */
    private String mTaobaoAccountName;
    /**
     * 账户名
     */
    private String mAccountName;
    /**
     * UserID
     */
    private String mUserId;
    /**
     * Token
     */
    private String mToken;
    /**
     * external Token
     */
    private String mExtToken;
    /**
     * 是否认证用户 "Y"|"N"
     */
    private boolean mCertificate;
    /**
     * 账号类型
     */
    private String mUserGrade;
    /**
     * 手机号
     */
    private String mMobileNo;
    /**
     * 可用余额
     */
    private String mAvailableBalance;
    /**
     * 免费流量文案
     */
    private String mAvailablePayment;
    /**
     * 已用免费流量
     */
    private String mYetFlow;
    /**
     * 可用免费流量
     */
    private String balanceFlow;
    private String mFreeFlow;
    /**
     * 未读消息数量
     */
    private int mMsgCount;
    /**
     * 待处理交易数
     */
    private int mTradeCount;
    /**
     * 第一条消息
     */
    private MessageRecord mFirstMsg;

    /**
     * 最后金额变动时间
     */
    private String lastChangeTime;

    /**
     * 集分宝
     */
    private String pointBalance;

    /**
     * 红包
     */
    private String redBag;

    /**
     * 是否可以绑定手机
     */
    private String canBind;

    /**
     * 冻结金额
     */
    private String freezeBalance;
    
    
    /**
     * 用户头像存储路径
     */
    private String mUserAvtarPath; 

    private Handler mHandler;
    
    private String mCardCount;
    
    private String mTaobaoSid;
    
    /**
     * 是否显示浮出提醒
     */
    private boolean mViewInfo;

    /**
     * 用户手势
     */
    private String mUserPattern;
    
    /**
     * 用户手势
     */
    private String mLastLoginAccount;

    private static final int MSG_CODE_DATA = 0;
    private static final int MSG_CODE_AVATAR = MSG_CODE_DATA + 1;
    
    /**
     * 账单数据脏标志
     */
    private boolean mBillDirty;
    /**
     * 券数据脏标志
     */
    private boolean mVoucherDirty;

    
    public UserData(Context context) {
        mContext = context;
        init();
        mViewInfo = true;
        mBillDirty = true;
        mVoucherDirty = true;
        dataStore = new AlipayDataStore(mContext);
    }

    private void init() {
        mHandler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                switch (msg.what) {
                    case MSG_CODE_DATA:
                        AlipayApplication application = (AlipayApplication) mContext
                            .getApplicationContext();
                        Responsor responsor = (Responsor) msg.obj;
                        
                        MessageFilter mf = null;
                        boolean tResultOK = false;
                        if (curActivity!=null && !curActivity.isFinishing()) {
                        	mf = new MessageFilter(curActivity);
                        	tResultOK = mf.process(msg);
						}

                        JSONObject obj = responsor.obj;
                        
                        int status = responsor.status;
                        //                tResultOK = mMessageFilter.process(msg);
                        mBillDirty = true;
                        mVoucherDirty = true;
                        if (tResultOK&&(status == 100) && (!application.getDataHelper().isCanceled())) {
                        	mRequestStatus = REQUEST_OK;
                            fillData(obj);
                            setChanged();
                            isQueryBalanceSuccess = true;
                        } 
                        else if (!tResultOK && (!application.getDataHelper().isCanceled())) {
                        	isQueryBalanceSuccess = false;
                            mRequestStatus = REQUEST_NO;
                            setChanged();
						}
                        break;

                    default:
                        break;
                }
                notifyObservers();
            }

        };
    }
    
    private boolean isQueryBalanceSuccess;
    
    public boolean getIsQueryBalanceSuccess(){
    	return isQueryBalanceSuccess;
    }

    public String getFreezeBalance() {
        return freezeBalance;
    }

    public void setFreezeBalance(String freezeBalance) {
        this.freezeBalance = freezeBalance;
    }

    public String getCanBind() {
        return canBind;
    }

    public void setCanBind(String canBind) {
        this.canBind = canBind;
    }

    public String getRealName() {
        if (mRealName != null)
            return mRealName;
        else
            return "";
    }

    public void setRealName(String realName) {
        mRealName = realName;
    }
    
    private boolean currentAccountIsTaobao;
    
    public boolean currentAccountIsTaobao(){
    	return currentAccountIsTaobao;
    }
    
    public void setCurrentAccountType(boolean currentAccountIsTaobao){
    	this.currentAccountIsTaobao = currentAccountIsTaobao;
    }
    
    
    
    public String getTaobaoAccountName() {
        if (mTaobaoAccountName != null)
            return mTaobaoAccountName;
        else
            return "";
    }

    public void setTaobaoAccountName(String taobaoAccountName) {
    	mTaobaoAccountName = taobaoAccountName;
    }

    public String getAccountName() {
        if (mAccountName != null)
            return mAccountName;
        else
            return "";
    }

    public void setAccountName(String accountName) {
        mAccountName = accountName;
    }

    public String getMobileNo() {
        if (mMobileNo != null)
            return mMobileNo;
        else
            return "";
    }

    public void setBalanceFlow(String balanceFlow) {
        this.balanceFlow = balanceFlow;
    }

    public String getBalanceFlow() {
        if (balanceFlow != null)
            return balanceFlow;
        else
            return "";
    }

    public String getTaobaoSid() {
    	if(mTaobaoSid != null)
    		return mTaobaoSid;
    	else 
    		return "";
	}

	public void setTaobaoSid(String taobaoSid) {
		this.mTaobaoSid = taobaoSid;
	}

	public void setMobileNo(String mobileNo) {
        mMobileNo = mobileNo;
    }

    public String getAvailableBalance() {
        if (mAvailableBalance != null && mAvailableBalance.endsWith("元"))
            return mAvailableBalance.substring(0, mAvailableBalance.length() - 1);
        return mAvailableBalance;
    }

    public void setAvailableBalance(String availableBalance) {
        mAvailableBalance = availableBalance;
    }

    public String getAvailablePayment() {
        return mAvailablePayment;
    }

    public void setAvailablePayment(String availablePayment) {
        mAvailablePayment = availablePayment;
    }

    public String getYetFlow() {
        return mYetFlow;
    }

    public String getFreeFlow() {
        return mFreeFlow;
    }

    public void setYetFlow(String yetFlow) {
        mYetFlow = yetFlow;
    }

    public void setFreeFlow(String freeFlow) {
        mFreeFlow = freeFlow;
    }

    public String getUserId() {
        if (mUserId != null)
            return mUserId;
        else
            return "";
    }

    public void setUserId(String userId) {
        mUserId = userId;
    }

    public String getExtToken() {
        if (mExtToken != null)
            return mExtToken;
        else
            return "";
    }

    public void setExtToken(String extToken) {
        mExtToken = extToken;
    }

    public boolean isCertificate() {
        return mCertificate;
    }

    public void setCertificate(boolean certificate) {
        mCertificate = certificate;
    }

    public String getUserGrade() {
        return mUserGrade;
    }

    public void setUserGrade(String userGrade) {
        mUserGrade = userGrade;
    }

    public String getToken() {
        if (mToken != null)
            return mToken;
        else
            return "";
    }

    public void setToken(String token) {
        mToken = token;
    }

    public int getMsgCount() {
        return mMsgCount;
    }

    public void setMsgCount(int msgCount) {
        mMsgCount = msgCount;
    }

    public int getTradeCount() {
        return mTradeCount;
    }

    public void setTradeCount(int tradeCount) {
        mTradeCount = tradeCount;
    }

    public MessageRecord getFirstMsg() {
        return mFirstMsg;
    }

    public void setFirstMsg(MessageRecord firstMsg) {
        mFirstMsg = firstMsg;
    }
    
    public boolean hasUserAvatar(){
		if (mUserAvtarPath == null || "".equals(mUserAvtarPath)) {
		return false;
	} else {
		return true;
	}
    }
    
    public String getUserAvtarPath(){
    	return mUserAvtarPath;
    }
    
    public static String getUserAvtarFileStr(Context context, String userId) {
    	return getUserAvtarDirStr(context,userId) +curAvatarName +".jpg";
    }
    
    public static String getUserAvtarDirStr(Context context, String userId) {
    	return context.getFilesDir() + File.separator + "userdata"
                + File.separator + "useravatar" + File.separator+ userId + File.separator;
    }
    
    public void setUserAvtarPath(String userAvtarPath) {
    	mUserAvtarPath = userAvtarPath;
    }
    
    public static Bitmap getUserAvtar(Context context, String userId) {
    	String avatarPath = getUserAvtarFileStr(context, userId);
    	if (!Utilz.isEmpty(avatarPath)) {
    		try {
                Options options = new Options();
                options.inJustDecodeBounds = true;
                BitmapFactory.decodeFile(avatarPath, options);
                int dw = options.outWidth / 120;
                int dh = options.outHeight /120;
                int scale = Math.max(dw, dh);

                options = new Options();
                options.inDensity = (int) Helper.getDensityDpi(context);
                options.inScaled = true;
                options.inSampleSize = scale;
                return BitmapFactory.decodeFile(avatarPath, options);
            } catch (Error e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
    	}
    	return null;
    }
    
    public Bitmap getUserAvtar() {
        String curUserAvatarStoredPath = dataStore.getString(getUserId()+":"+AlipayDataStore.LASTLOGINUSERAVTARPATH, "");
    	if (curUserAvatarStoredPath!=null&&!"".equals(curUserAvatarStoredPath)) {
    		try {
    	        Options options = new Options();
    	        options.inJustDecodeBounds = true;
    	        BitmapFactory.decodeFile(curUserAvatarStoredPath, options);
    	        int dw = options.outWidth / 120;
    	        int dh = options.outHeight /120;
    	        int scale = Math.max(dw, dh);

    	        options = new Options();
    	        options.inDensity = (int) Helper.getDensityDpi(mContext);
    	        options.inScaled = true;
    	        options.inSampleSize = scale;
    	        
                return BitmapFactory.decodeFile(curUserAvatarStoredPath, options);
            } catch (Error e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
    	}
    	return null;
    }
    
//    String curUserAvatarStoredPath = mAlipayDataStore.getString(getUserId()+":"+AlipayDataStore.LASTLOGINUSERAVTARPATH, "");
//	Bitmap avatarImg = null;
//	if (curUserAvatarStoredPath!=null&&!"".equals(curUserAvatarStoredPath)) {
//		avatarImg = getLastUserAvtar(curUserAvatarStoredPath);
//		if (avatarImg!=null) {
//			user_avatar.setImageBitmap(avatarImg);
//		}
//	}else{
//		user_avatar.setImageResource(R.drawable.default_avatar);
//	}
    

    public void setLastChangeTime(String lastChangeTime) {
        this.lastChangeTime = lastChangeTime;
    }

    public String getLastChangeTime() {
        return lastChangeTime;
    }

    public String getPointBalance() {
        if (pointBalance != null && pointBalance.endsWith("个"))
            return pointBalance.substring(0, pointBalance.length() - 1);
        return pointBalance;
    }

    public void setPointBalance(String pointBalance) {
        this.pointBalance = pointBalance;
    }

    public String getRedBag() {
        if (redBag != null && redBag.endsWith("元"))
            return redBag.substring(0, redBag.length() - 1);
        return redBag;
    }

    public void setRedBag(String redBag) {
        this.redBag = redBag;
    }
    
    public void setCardCount(String cardCount){
    	this.mCardCount = cardCount;
    }
    
    public String getCardCount(){
    	return mCardCount;
    }
    

    public boolean isViewInfo() {
        return mViewInfo;
    }

    public void setViewInfo(boolean viewInfo) {
        mViewInfo = viewInfo;
    }
    
    public String getUserPattern() {
        return mUserPattern;
    }

    public void setUserPattern(String userPattern) {
        mUserPattern = userPattern;
        if (null != mUserPattern) {
        	AlipayApplication application = (AlipayApplication) mContext;
        	if (null != application) {
        		application.startPatternMonitor();
        	}
        }
        else {
        	AlipayApplication application = (AlipayApplication) mContext;
        	if (null != application) {
        		application.stopPatternMonitor();
        	}
        }
    }
    
    public String getLastLoginAccount() {
        return mLastLoginAccount;
    }

    public void setLastLoginAccount(String lastLoginAccount) {
    	mLastLoginAccount = lastLoginAccount;
    }
    
    public void setContext(Context context) {
        mContext = context;
    }

    public static final int REQUEST_INVALID = -1;
    public static final int REQUEST_OK = 0;
    public static final int REQUEST_NO = REQUEST_OK + 1;
    public static final int REQUEST_PROCESSING = REQUEST_NO + 1;
    private int mRequestStatus = REQUEST_NO;

    private AlipayDataStore dataStore;
    /**
     * 下载头像
     */
    private BitmapDownloadListener mBitmapDownloadListener = new BitmapDownloadListener() {
        @Override
        public void onComplete(Bitmap bm) {
            try {
            	if (null != bm) {
                    File file = new File(getUserAvtarDirStr(mContext, mUserId));
                    file.mkdirs();
                    
                    file = new File(getUserAvtarFileStr(mContext, mUserId));
                    file.createNewFile();
                    
                    if (bm.compress(Bitmap.CompressFormat.JPEG, 100, new FileOutputStream(file))) {
                        setUserAvtarPath(file.getAbsolutePath());
                        
                        dataStore.putString(getUserId()+":"+AlipayDataStore.LASTLOGINUSERAVTARPATH,file.getAbsolutePath());
                        
                    } else {
                        setUserAvtarPath(null);
                    }
                    setChanged();
                    mHandler.sendEmptyMessage(MSG_CODE_AVATAR);
            	}
            	else {
            		setUserAvtarPath(null);
            	}
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    };

    private void fillData(JSONObject jsonResp) {
        setAvailableBalance(jsonResp.optString(Constant.RPF_AVAILABLE_BALANCE));
        setFreezeBalance(jsonResp.optString(Constant.RPF_FREEZE));
        setLastChangeTime(jsonResp.optString(Constant.RPF_LASTCHANGETIME));

        setPointBalance(jsonResp.optString(Constant.RPF_POINTBALANCE));
        setRedBag(jsonResp.optString(Constant.RPF_REDBAG));

        setCanBind(jsonResp.optString(Constant.RPF_CAN_BINDING));
        setMobileNo(jsonResp.optString(Constant.RPF_BINDING_MOBILE));

        setAvailablePayment(jsonResp.optString(Constant.RPF_FLOW_TEXT));
        setYetFlow(jsonResp.optString(Constant.RPF_YET_FLOW));
        setFreeFlow(jsonResp.optString(Constant.RPF_FREE_FLOW));
        setUserGrade(jsonResp.optString(Constant.RPF_USERGRADE));

        setBalanceFlow(jsonResp.optString(Constant.RPF_BALANCEFLOW));

        String avatar = jsonResp.optString(Constant.RPF_HEADIMGPATH);

        setTradeCount(jsonResp.optInt(Constant.RPF_TRADE_COUNT));
        
        //银行卡数量
        String cardCount = jsonResp.optString("cardCount");
        if (cardCount!=null&&cardCount!="") {
			mCardCount = cardCount;
		}
        
        //TODO 在这里进行账单、优惠券列表的存储
        //账单
        JSONObject billData =jsonResp.optJSONObject("billData") ;
        mBillInfo = new UDBillInfo();
        if (billData!=null) {
        	mBillInfo.mBillData = billData.toString();
        	mBillInfo.mStartRowNum = (billData.optString("startRowNum"));
        	mBillInfo.mWaitPayBillCount = (billData.optString("waitPayBillCount"));
        	mBillInfo.mTotalBillCount=(billData.optString("totalCount"));;
        	mBillInfo.mBillList=(billData.optString("billList"));
		}
        //Life pay
        mLifePayInfo = new UDLifePayInfo();
        mLifePayInfo.mTotalLifePayCount = jsonResp.optString("pucBillCount");
        mLifePayInfo.mFirstShow = jsonResp.optString("showFirstBillId");
        mLifePayInfo.mLifePayList =jsonResp.optString("pucBills");
        //Merge UDBillInfo and UDLifePayInfo
        if(mBillInfo.mBillList != null 
        		&& mLifePayInfo.mLifePayList != null 
        		&& !mLifePayInfo.mLifePayList.equals("") 
        		&& !mLifePayInfo.mLifePayList.endsWith("[]"))
        {
        	String removeFirst = (mLifePayInfo.mLifePayList.trim()).replace("[", "");
        	String removeLast = removeFirst.replace("]", "");
        	
        	if(mBillInfo.mBillList.equals("[]"))
        	{
        		String result = mBillInfo.mBillData.replace(":[", ":["+removeLast);
        		mBillInfo.mBillData = result;
        	}
        	else if(mBillInfo.mBillList.equals(""))
        	{
        		String result = mBillInfo.mBillData.replace("\"billList\":", "\"billList\":"+mLifePayInfo.mLifePayList.trim());
        		mBillInfo.mBillData = result;
        	}
        	else
        	{
        		String result = mBillInfo.mBillData.replace(":[", ":["+removeLast+",");
        		mBillInfo.mBillData = result;
        	}
        	
	        mBillInfo.mBillList = mLifePayInfo.mLifePayList.trim() + mBillInfo.mBillList.trim();
	        String result = mBillInfo.mBillList.replace("][", ",");
	        mBillInfo.mBillList = result;
        }
        //优惠券
        JSONObject voucherData =jsonResp.optJSONObject("voucherData") ;
        mVoucherInfo = new UDVoucherInfo();
        if (voucherData!=null) {
        	mVoucherInfo.mTotalCount = (voucherData.optInt("totalCount"));
        	mVoucherInfo.mVoucherList = (voucherData.optString("voucherList"));
        	
        	mVoucherInfo.mCurrentPage =(voucherData.optInt("currentPage"));
        	mVoucherInfo.mTotlaPage =(voucherData.optInt("totalPage"));
        	
        	JSONArray voucherArray = voucherData.optJSONArray("voucherList");
        	
        	try {
				if (voucherArray == null||voucherArray.length() == 0) {
					mVoucherInfo.has_no_voucher = true;
				}else{
					String status = voucherArray.getJSONObject(0).optString("status", "");
					if (!status.equals(Voucher.Status.CAN_USE)) {
						mVoucherInfo.only_cantUse_voucher = true;
					}
				}
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}

        if (avatar.length() > 0) {//获取头像
        	setAvatarName(avatar);
        	
        	String lastAvatarPath = dataStore.getString(getUserId()+":"+AlipayDataStore.LASTLOGINUSERAVTARPATH, "");
        	if (lastAvatarPath.indexOf(curAvatarName)!=-1) {
        		mUserAvtarPath = lastAvatarPath;
				return;
			}
        	
            Helper.bitmapFromUriString(mContext, avatar, mBitmapDownloadListener,-1);//R.drawable.default_avatar);
        }
    }
    
    private static String curAvatarName;
    
    private void setAvatarName(String avatarUrl){
    	int index = avatarUrl.lastIndexOf("/");
    	if (index!=-1) {
			curAvatarName = avatarUrl.substring(index+1);
		}
    }
    
    private UDBillInfo mBillInfo;
    
    public UDBillInfo getUDBillInfo(){
    	return mBillInfo;
    }
    

    public class UDBillInfo{
    	
    	public String mBillData;
    	
    	public String mWaitPayBillCount;
    	
    	public String mStartRowNum;
    	
    	public String mTotalBillCount;
        
    	public String mBillList;
    }
    
    private UDLifePayInfo mLifePayInfo;
    
    public UDLifePayInfo getUDLifePayInfo()
    {
    	return mLifePayInfo;
    }
    
    public class UDLifePayInfo
    {
    	public String mTotalLifePayCount;
    	public String mFirstShow;
    	public String mLifePayList;
    }
    
    private UDVoucherInfo mVoucherInfo;
    
    public UDVoucherInfo getUDVoucherInfo(){
    	return mVoucherInfo;
    }
    
    public class UDVoucherInfo{
    	
    	public int mTotalCount;
    	
    	public int mTotlaPage;
        
    	public int mCurrentPage;
         
    	public String mVoucherList;
         
    	public boolean only_cantUse_voucher;
    	
    	public boolean has_no_voucher;
    }
    
    private Activity curActivity = null;
    
    
    public int requestData(Observer observer, final DataHelper dataHelper) {
        if (observer != null)
        	addObserver(observer);
        
        if (observer!=null && observer instanceof Activity &&(observer instanceof Main||observer instanceof AlipayAccountManager)) {
        	curActivity = (Activity)observer;
		}
        
        switch (mRequestStatus) {
            case REQUEST_OK:
                return mRequestStatus;
            case REQUEST_NO:
                dataHelper.sendQueryBalance(mHandler, MSG_CODE_DATA,mBillDirty,mVoucherDirty);
                mRequestStatus = REQUEST_PROCESSING;
                return mRequestStatus;
            case REQUEST_PROCESSING:
                return mRequestStatus;
            default:
                return REQUEST_INVALID;
        }
    }

    public void resetStatus() {
        mRequestStatus = REQUEST_NO;
    }
    
    public int getCurrentRequestStatus(){
    	return mRequestStatus;
    }

    public void setBillDirty(boolean billDirty) {
        mBillDirty = billDirty;
    }

    public void setVoucherDirty(boolean voucherDirty) {
        mVoucherDirty = voucherDirty;
    }

    @Override
    public void save(SharedPreferences preferences, String key) {
        Editor editor = preferences.edit();
        editor.putString(key + ".balanceFlow", balanceFlow);
        editor.putString(key + ".mRealName", mRealName);
        editor.putString(key + ".mTaobaoAccountName", mTaobaoAccountName);
        editor.putString(key + ".mAccountName", mAccountName);
        editor.putBoolean(key + ".currentAccountIsTaobao",currentAccountIsTaobao);
        editor.putString(key + ".mUserId", mUserId);
        editor.putString(key + ".mToken", mToken);
        editor.putString(key + ".mExtToken", mExtToken);
        editor.putString(key + ".mUserGrade", mUserGrade);
        editor.putBoolean(key + ".mCertificate", mCertificate);
        editor.putString(key + ".mMobileNo", mMobileNo);
        editor.putString(key + ".mAvailableBalance", mAvailableBalance);
        editor.putString(key + ".mAvailablePayment", mAvailablePayment);
        editor.putString(key + ".mYetFlow", mYetFlow);
        editor.putString(key + ".mFreeFlow", mFreeFlow);
        editor.putInt(key + ".mMsgCount", mMsgCount);
        editor.putInt(key + ".mTradeCount", mTradeCount);
        try {
            if (mFirstMsg != null)
                editor.putString(key + ".mFirstMsg", mFirstMsg.toJson().toString());
        } catch (JSONException e) {
            e.printStackTrace();
        }
        editor.putString(key + ".mUserAvtarPath", mUserAvtarPath);
        editor.putString(key + ".lastChangeTime", lastChangeTime);
        editor.putString(key + ".pointBalance", pointBalance);
        editor.putString(key + ".redBag", redBag);
        editor.putString(key + ".canBind", canBind);
        editor.putString(key + ".freezeBalance", freezeBalance);
        editor.putBoolean(key + ".mViewInfo", mViewInfo);
        editor.commit();
    }

    @Override
    public void restore(SharedPreferences preferences, String key) {
        balanceFlow = preferences.getString(key + ".balanceFlow", null);
        mRealName = preferences.getString(key + ".mRealName", null);
        mTaobaoAccountName = preferences.getString(key + ".mTaobaoAccountName", null);
        mAccountName = preferences.getString(key + ".mAccountName", null);
        currentAccountIsTaobao = preferences.getBoolean(key + ".currentAccountIsTaobao", false);
        mUserId = preferences.getString(key + ".mUserId", null);
        mToken = preferences.getString(key + ".mToken", null);
        mExtToken = preferences.getString(key + ".mExtToken", null);
        mUserGrade = preferences.getString(key + ".mUserGrade", null);
        mCertificate = preferences.getBoolean(key + ".mCertificate", false);
        mMobileNo = preferences.getString(key + ".mMobileNo", null);
        mAvailableBalance = preferences.getString(key + ".mAvailableBalance", null);
        mAvailablePayment = preferences.getString(key + ".mAvailablePayment", null);
        mYetFlow = preferences.getString(key + ".mYetFlow", null);
        mFreeFlow = preferences.getString(key + ".mFreeFlow", null);
        mMsgCount = preferences.getInt(key + ".mMsgCount", 0);
        mTradeCount = preferences.getInt(key + ".mTradeCount", 0);
        String str = preferences.getString(key + ".mFirstMsg", null);
        if (str != null) {
            try {
                MessageRecord record = new MessageRecord();
                record.fillData(new JSONObject(str));
                mFirstMsg = record;
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        mUserAvtarPath = preferences.getString(key + ".mUserAvtarPath", null);
        lastChangeTime = preferences.getString(key + ".lastChangeTime", null);
        pointBalance = preferences.getString(key + ".pointBalance", null);
        redBag = preferences.getString(key + ".redBag", null);
        canBind = preferences.getString(key + ".canBind", null);
        freezeBalance = preferences.getString(key + ".freezeBalance", null);
        mViewInfo = preferences.getBoolean(key + ".mViewInfo", false);
    }
}
