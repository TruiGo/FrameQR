package com.alipay.android.client;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Build;
import android.os.Handler;
import android.webkit.CookieManager;
import android.webkit.CookieSyncManager;
import android.widget.Toast;

import com.alipay.android.appHall.CacheManager;
import com.alipay.android.appHall.Helper;
import com.alipay.android.appHall.appManager.HallData;
import com.alipay.android.appHall.bussiness.JsonParser;
import com.alipay.android.appHall.common.CacheSet;
import com.alipay.android.client.constant.Constant;
import com.alipay.android.client.constant.PhoneConstant;
import com.alipay.android.client.exception.SystemExceptionHandler;
import com.alipay.android.client.util.AlipayDataStore;
import com.alipay.android.client.util.BaseHelper;
import com.alipay.android.client.util.DataHelper;
import com.alipay.android.common.data.AdvertData;
import com.alipay.android.common.data.ConfigData;
import com.alipay.android.common.data.MessageData;
import com.alipay.android.common.data.UserData;
import com.alipay.android.comon.component.ProgressDiv;
import com.alipay.android.comon.component.StyleAlertDialog;
import com.alipay.android.core.ActivityShell;
import com.alipay.android.core.MBus;
import com.alipay.android.core.Style;
import com.alipay.android.core.expapp.ExpAppRuntime;
import com.alipay.android.core.webapp.WebAppRunTime;
import com.alipay.android.net.http.HttpClient;
import com.alipay.android.servicebeans.ServiceBeanFactory;
import com.alipay.android.util.LogUtil;
import com.alipay.platform.core.AlipayApp;
import com.eg.android.AlipayGphone.R;

/**
 * @author sanping.li
 *
 */
public class AlipayApplication extends AlipayApp {
    private Map<String, Class<?>> mEngines;
    private MBus mBus;

    public static final int POOL_SIZE = 4;
    public static final int KEEP_ALIVE_TIME = 5;
    public static final int QUEUE_SIZE = 128;
    private ThreadPoolExecutor mParallelExectuor;
    private Activity mActivity;
    //
    private ProgressDiv mProgress = null;
    private StyleAlertDialog mAlertDialog = null;
    /**
     * 消息是否需要刷新,这几个数据可以考虑门面下
     */
    private boolean mMsgsRefresh;
    /**
     * 记录是否需要刷新
     */
    private boolean mRecordsRefresh;
    /**
     * 是否刷新银行卡列表
     */
    private boolean bankListRefresh = true;
    /**
     * 是否刷新已存信用卡列表
     */
    private boolean savedCCRBankRefresh = true;
    /**
     * 记录是否第一次进入转账应用
     */
    private boolean isFirstInTransfer = true;
    /**
     * 业务列表数据
     */
    private HallData mHallData;
    /**
     * 配置数据
     */
    private ConfigData mConfigData;
    /**
     * 当前登录用户
     */
    private UserData mUserData;
    
    /**
     * 动画所需数据
     */
    private AnimationData mAnimationData;
    /**
     * 条码使用的认证用户
     */
    private UserData mCertUserData;
    /**
     * 系统消息（会员服务）数据
     */
    private MessageData mMessageData;
    /**
     * 广告数据
     */
    private AdvertData mAdvertData;

    private boolean isSafePayCalled = false;
    private boolean verifySuccess = false;

    /**
     * 样式
     */
    private Style mStyle;

    private DataHelper mDataHelper;

    public static BroadcastReceiver mSafepayInstallreceiver;
    public static PayData mPayData;

    /*快捷支付取消安装关闭当前应用*/
    public static boolean mFinishPage = false;
    public static boolean mFinishPageFirst = false;
    
    private int mIsFirstTimeRun = -1;
    
    private Context context;
    
    @Override
    public void onCreate() {
        super.onCreate();
        
        LogUtil.init(this);
        //init ExceptionHandler
        SystemExceptionHandler.getInstance().init(this);
        HttpClient.sAndroidHttpClient = null;

        //init ServiceBeanFactory
        ServiceBeanFactory.getInstance().initContext(this);
        mEngines = new HashMap<String, Class<?>>();
        initEngines();
        mBus = new MBus(this);
        mParallelExectuor = new ThreadPoolExecutor(1, POOL_SIZE, KEEP_ALIVE_TIME, TimeUnit.SECONDS, new ArrayBlockingQueue<Runnable>(QUEUE_SIZE));

        LogUtil.logAnyTime("AlipayApplication", "onCreate");
        initGlobalData();
        
        context = this;
        updateChannelInfo();
        
        factorySet();
        //只有当前版本已经运行过才有可能执行恢复
        if (!isFirstTimeRun()) {
        	onRestoreState();
        }

        initConfigData();
        
        /*new Timer().schedule(new TimerTask() {
			@Override
			public void run() {
				getConfigData().setSessionId("7a30e543fb565625bda4211b6ebb561d");
				LogUtil.logOnlyDebuggable("AlipayApplication", "超时===============");
			}
		}, 1 * 1000 * 30,1 * 1000 * 30);*/
    }
    
//    private Properties nfcProperties;
    private Properties lotteryProperties;
    /**
     * 
     * @param propertiesName
     * @return
     */
    private Properties loadProperties(String propertiesName){
    	Properties properties = new Properties();
    	try {
			InputStream is = context.getAssets().open(propertiesName);
			properties.load(is);
		} catch (IOException e) {
			e.printStackTrace();
		}
    	return properties;
    }
    
    /**
     * 
     * @param properties
     * @param curChannelInfo
     * @return
     */
    private String getPropertyValue(Properties properties, String curChannelInfo){
    	if(properties == null) return "";
        String propertyValue = "";
        // 是否在配置文件中
        for(int i = 0; i < properties.size(); i++){
        	if(properties.containsKey(curChannelInfo)) {
        		// 获取value
        		propertyValue = properties.getProperty(curChannelInfo, "");
        		// 保存当前渠道号
        		CacheSet.getInstance(this).putString(Constant.CHANNELS, curChannelInfo);
        	}
        }
        return propertyValue;
    }
    
//    /**
//     * 读取nfc配置文件
//     * @param curChannelInfo
//     */
//    public void loadNFCProperties() {
//    	nfcProperties = loadProperties("nfcEnterance.properties");
//    	String propertyValue = getPropertyValue(nfcProperties, curChannelInfo);
//    	if(propertyValue.equalsIgnoreCase("")) return;
//       	String[] propertyValues = propertyValue.split(",");
//       	// 图标在halldata数组中的位置
//       	int iconIndex = Integer.valueOf(propertyValues[0]);
//       	// 图标的资源id
//       	int iconId = getResources().getIdentifier(propertyValues[1], "drawable", this.getPackageName());
//       	mHallData.setPropertsData(iconIndex, iconId);
//    }
    
    /**
     * 
     * @param curChannelInfo
     * @param oldChannelInfo
     */
    public void loadLotteryEnterance() {
    	lotteryProperties = loadProperties("lotteryEnterance.properties");
    	String propertyValue = getPropertyValue(lotteryProperties, curChannelInfo);
    	if(propertyValue.equalsIgnoreCase("")) return;
    	if(curChannelInfo.equalsIgnoreCase("alipay")) {
    		CacheSet.getInstance(this).putString(Constant.CHANNELS, curChannelInfo);
    		oldChannelInfo = curChannelInfo;
    	}
    	String[] propertyValues = propertyValue.split(",");
    	int nativeIndex = Integer.valueOf(propertyValues[0]);
    	String funcid = propertyValues[1];
    	mHallData.setFuncId(funcid);
    	mHallData.setNativeIndex(nativeIndex);
    }
    private String oldChannelInfo;
    private String curChannelInfo;
    private void updateChannelInfo() {
        oldChannelInfo = CacheSet.getInstance(this).getString(Constant.CHANNELS);
        curChannelInfo = this.getString(R.string.channels);
        
//        loadNFCProperties();
        loadLotteryEnterance();
        
//        if ("".equals(oldChannelInfo)) {
//            oldChannelInfo = "alipay";
//        }
    }

    public void executeTask(Runnable task){
        mParallelExectuor.execute(task);
    }
    /**
     * 检测当前版本是否是第一次运行
     * 1表示第一次运行，0表示之前已经运行过,-1表示未初始化，尚未检测过是否是第一次运行
     */
    public boolean isFirstTimeRun() {
    	if (-1 == mIsFirstTimeRun) {
    		AlipayDataStore dataStore = new AlipayDataStore(this);
    		if (dataStore.containString(AlipayDataStore.CURENTVERSION)) {
    			if (dataStore.getString(AlipayDataStore.CURENTVERSION).equals(
    					mConfigData.getProductVersion())) {
    				mIsFirstTimeRun = 0;
    			}
    		} 

    		//第一次运行
    		if (-1 == mIsFirstTimeRun){
    			mIsFirstTimeRun = 1;
    			dataStore.putString(AlipayDataStore.CURENTVERSION, mConfigData.getProductVersion());
    		}
    	}
    	
    	return 1 == mIsFirstTimeRun;
    }

    private void initConfigData() {
        CookieSyncManager.createInstance(getApplicationContext());
        CookieManager.getInstance().removeAllCookie();
        CookieManager.getInstance().removeSessionCookie();
        mPayData = new PayData();

        mDataHelper = new DataHelper(this);
    }

    private void initEngines() {
        mEngines.put("", ExpAppRuntime.class);
        mEngines.put("web", WebAppRunTime.class);
    }

    public Map<String, Class<?>> getEngines() {
        return mEngines;
    }

    public MBus getMBus() {
        return mBus;
    }

    public void showToast(String info) {
        Toast.makeText(mActivity, info, Toast.LENGTH_LONG).show();
    }

    public void setActivity(Activity activity) {
        mActivity = activity;
    }

    public Activity getActivity() {
        return mActivity;
    }

    public void startActivity(String pkgId, String params) {
        Intent intent = new Intent(mActivity, ActivityShell.class);
        intent.putExtra("id", pkgId);
        intent.putExtra("data", params);
        if (mActivity instanceof Main) {
        	Main mainActivity = (Main)mActivity;
        	mainActivity.SetRefreshUserStatus(false);
        }
        mActivity.startActivity(intent);
    }

    /**
     * 显示进度条
     */
    public void showProgress() {
        mProgress = Helper.showProgressDialog(mActivity, null, getString(R.string.processing));
    }

    /**
     * 隐藏进度条
     */
    public void closeProgress() {
        try {
            if (mProgress != null && mProgress.isShowing())
                mProgress.dismiss();
            mProgress = null;
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void alert(ExpAppRuntime engine, int type, String content, String ok, String cancel,
                      ArrayList<String> params) {
        if (mAlertDialog != null)
            mAlertDialog.dismiss();
        mAlertDialog = BaseHelper.showDialog(mActivity, type, content, ok, cancel, params, engine);
    }

    public StyleAlertDialog getAlertDialog() {
        return mAlertDialog;
    }

    private void initGlobalData() {
        mHallData = new HallData(this);

        mConfigData = new ConfigData(this);
        mConfigData.init();
        logoutUser();
        mCertUserData = new UserData(this);

        mMessageData = new MessageData(this);

        mAdvertData = new AdvertData(this);
        mStyle = new Style(this, Style.TYPE_METRO);
        
        Constant.GAS_JSON = null;
        Constant.WATER_JSON = null;
        Constant.ELECTRIC_JSON = null;
        Constant.COMMUN_JSON = null;

    }

    private void factorySet() {
        //如果为厂商内置的版本，在表中加入内置标识，通过标识来判断
        AlipayDataStore tData = new AlipayDataStore(this);
        if (tData.getString(AlipayDataStore.MOTO_BRAND).equals("moto")) {
            PhoneConstant.isMoto = true;
        }
        if (tData.getString(AlipayDataStore.SAMSUNG_BRAND).equals("samsung")) {
            PhoneConstant.isSam = true;
        }

        //此变量仅在厂商预制包中被设置
        if (PhoneConstant.isMoto) {
            tData.putString(AlipayDataStore.MOTO_BRAND, "moto");
        }
        if (PhoneConstant.isSam) {
            tData.putString(AlipayDataStore.SAMSUNG_BRAND, "samsung");
        }

        String userAgent = Build.MANUFACTURER + Build.MODEL + "," + getString(R.string.useragent);
        mConfigData.setUserAgent(userAgent);
    }
    
	// 记录是否已经分发过
	private boolean mDispathced;
	public boolean isDispathced() {
		return mDispathced;
	}

	public void setDispathced(boolean dispathced) {
		this.mDispathced = dispathced;
	}

	public boolean isMsgsRefresh() {
        return mMsgsRefresh;
    }

    public void setMsgsRefresh(boolean msgsRefresh) {
        mMsgsRefresh = msgsRefresh;
    }

    public boolean isRecordsRefresh() {
        return mRecordsRefresh;
    }

    public void setRecordsRefresh(boolean recordsRefresh) {
        mRecordsRefresh = recordsRefresh;
    }
    
    public boolean isBankListRefresh() {
		return bankListRefresh;
	}

	public void setBankListRefresh(boolean bankListRefresh) {
		this.bankListRefresh = bankListRefresh;
	}
	
	public boolean isSavedCCRBankRefresh() {
		return savedCCRBankRefresh;
	}

	public void setSavedCCRBankRefresh(boolean savedCCRBankRefresh) {
		this.savedCCRBankRefresh = savedCCRBankRefresh;
	}

	public boolean isFirstTransfer() {
        return isFirstInTransfer;
    }

    public void setFirstInTransfer(boolean isFirstInTransfer) {
        this.isFirstInTransfer = isFirstInTransfer;
    }
    
    public boolean isVerifySuccess() {
		return verifySuccess;
	}

	public void setVerifySuccess(boolean verifySuccess) {
		this.verifySuccess = verifySuccess;
	}

	@Override
    public int getOrmMapping() {
        return 0;
    }

    @Override
    protected void onInital() {
        mController.setDataPaser(new JsonParser());
    }

    //global data key
    public static final String USER_MOBILE = "user.mobile";
    public static final String USER_ACCOUNT = "user.account";
    public static final String USER_SESSIONID = "user.sessionId";
    public static final String USER_USERID = "user.userid";
    public static final String STATUS_IS_LOGIN = "status.isLogin";
    public static final String PAY_ACCOUNT = "pay.account";
    public static final String OS_NAME = "os.name";
    public static final String OS_VERSION = "os.version";
    public static final String OS_CLIENT_ID = "os.clientid";
    public static final String PRODUCTVERSION = "alipayclient.productversion";
    public static final String PRODUCTID = "alipayclient.productid";

    @Override
    public Object getInfoData(String key) {
        if (key.equalsIgnoreCase(USER_MOBILE)) {
            if (mUserData != null) {
                return mUserData.getMobileNo();
            } else {
                return "";
            }
        } else if (key.equalsIgnoreCase(USER_ACCOUNT)) {
            if (mUserData != null) {
                return mUserData.getAccountName();
            } else {
                return mCertUserData.getAccountName();
            }
        } else if (key.equalsIgnoreCase(USER_SESSIONID)) {
            return mConfigData.getSessionId();
        } else if (key.equalsIgnoreCase(STATUS_IS_LOGIN)) {
            return mUserData != null;
        } else if (key.equalsIgnoreCase(OS_NAME)) {
            return "Android";
        } else if (key.equalsIgnoreCase(OS_VERSION)) {
            return Build.VERSION.RELEASE;
        } else if (key.equalsIgnoreCase(OS_CLIENT_ID)) {
            return mConfigData.getClientId();
        } else if (key.equalsIgnoreCase(USER_USERID)) {
            if (mUserData != null) {
                return mUserData.getUserId();
            } else {
                return mCertUserData.getUserId();
            }
        }else if (key.equalsIgnoreCase(PRODUCTVERSION)) {
            return getConfigData().getProductVersion();
        }else if (key.equalsIgnoreCase(PRODUCTID)) {
            return getConfigData().getProductId();
        } else
            return super.getInfoData(key);
    }

    @Override
    public Object getGlobalData(String key) {
        if (key.equalsIgnoreCase(USER_MOBILE))
            return getInfoData(key);
        return mDatas.get(key);
    }



    public HallData getHallData() {
        return mHallData;
    }

    public UserData getUserData() {
        return mUserData;
    }

    public void setUserData(UserData userData) {
        mUserData = userData;
        
        if (null == mUserData && null != mPatternMonitor) {
        	mPatternMonitor.stop();
        }
    }

    public AnimationData getAnimationData() {
		return mAnimationData;
	}

	public void setAnimationData(AnimationData animationData) {
		this.mAnimationData = animationData;
	}

	

    public UserData getCertUserData() {
        return mCertUserData;
    }

    public void setCertUserData(UserData certUserData) {
        mCertUserData = certUserData;
    }

    public ConfigData getConfigData() {
        return mConfigData;
    }

    public MessageData getMessageData() {
        return mMessageData;
    }

    public DataHelper getDataHelper() {
        return mDataHelper;
    }

    public AdvertData getAdvertData() {
        return mAdvertData;
    }

    /**
     * 新的结构中标志是否调用安全支付
     * @param safePayCalled
     */
    public void setSafePayCalled(boolean safePayCalled) {
        this.isSafePayCalled = safePayCalled;
    }

    public boolean isSafePayCalled() {
        return isSafePayCalled;
    }

    public Style getStyle() {
        return mStyle;
    }
    
    /**
    * 用户登出
    */
   public void logoutUser() {
       mUserData = null;
       mConfigData.setSessionId(null);
       stopPatternMonitor();
   }
    
    public void destroy() {
        mBus.destroy();
        mHallData.destroy();
        mConfigData.destroy();
        mAdvertData.destroy();
        mAnimationData = null;
        CacheManager.getInstance(this).destroy();

        Constant.GAS_JSON = null;
        Constant.WATER_JSON = null;
        Constant.ELECTRIC_JSON = null;
        Constant.COMMUN_JSON = null;

        logoutUser();
        CookieManager.getInstance().removeAllCookie();
        CookieManager.getInstance().removeSessionCookie();
        
        SharedPreferences preferences = getSharedPreferences("__global_cache__.tmp", MODE_PRIVATE);
        preferences.edit().clear().commit();
        
        mIsFirstTimeRun = -1;
    }

    public void onSaveSate() {
        SharedPreferences preferences = getSharedPreferences("__global_cache__.tmp", MODE_PRIVATE);
        Editor editor = preferences.edit();
        editor.putBoolean("msg", mMsgsRefresh);
        editor.putBoolean("record", mRecordsRefresh);
        editor.putBoolean("isRunning", Constant.mClientIsRunning);
        editor.putInt("halls", mHallData.getState());
        if(Constant.GAS_JSON !=null)
            editor.putString("gas", Constant.GAS_JSON.toString());
        if(Constant.WATER_JSON !=null)
            editor.putString("water", Constant.WATER_JSON.toString());
        if(Constant.ELECTRIC_JSON !=null)
            editor.putString("electric", Constant.ELECTRIC_JSON.toString());
        if(Constant.COMMUN_JSON !=null)
            editor.putString("commun", Constant.COMMUN_JSON.toString());
        editor.commit();

        mConfigData.save(preferences, "config");
        if (mUserData != null) {
            editor.putBoolean("user", true).commit();
            mUserData.save(preferences, "user");
        }
        mCertUserData.save(preferences, "cert");
        mAdvertData.save(preferences, "advert");
    }

    public void onRestoreState() {
        SharedPreferences preferences = getSharedPreferences("__global_cache__.tmp", MODE_PRIVATE);
        mMsgsRefresh = preferences.getBoolean("msg", false);
        mRecordsRefresh = preferences.getBoolean("record", false);
        Constant.mClientIsRunning = preferences.getBoolean("isRunning", false);
        try {
            String str = preferences.getString("gas", null);
            if(str!=null)
                Constant.GAS_JSON = new JSONObject(str);
            str = preferences.getString("water", null);
            if(str!=null)
                Constant.WATER_JSON = new JSONObject(str);
            str = preferences.getString("electric", null);
            if(str!=null)
                Constant.ELECTRIC_JSON = new JSONObject(str);
            str = preferences.getString("commun", null);
            if(str!=null)
                Constant.COMMUN_JSON = new JSONObject(str);
        } catch (JSONException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        mConfigData.restore(preferences, "config");
        if (preferences.contains("hall")) {
            //刷新halldata数据
            if (preferences.getInt("halls", 0)<=0) {
                mHallData.initializeAppList();
                mHallData.syncAppStatus();
            }
        }
        if (preferences.contains("user") && preferences.getBoolean("user", false)) {
            mUserData = new UserData(this);
            mUserData.restore(preferences, "user");
        }
        mCertUserData.restore(preferences, "cert");
        mAdvertData.restore(preferences, "advert");
    }
    
    PatternMonitor mPatternMonitor = null;
    public void startPatternMonitor() {
    	if (null == mPatternMonitor) {
    		mPatternMonitor = new PatternMonitor(this);
    	}
    	
    	mPatternMonitor.start();
    }
    public void stopPatternMonitor() {
    	if (null != mPatternMonitor) {
    		mPatternMonitor.stop();
    	}
    }
    
    private Handler mHandler = new Handler();
    private Runnable mRunnable = new Runnable() {
		public void run() {
			LogUtil.logOnlyDebuggable("step", "alert");
	    	if (null != mPatternMonitor) {
	    		mPatternMonitor.startLockActivity();
	    	}
		}
	};
	
	private int mStepCount = -1;
	private int mDuration = 15000;
	private boolean mResumeLastTime = false;
	public void stepForward() {
		LogUtil.logOnlyDebuggable("step", "onResume");
		removeStepMessage();
		
		if (mResumeLastTime) {
			LogUtil.logOnlyDebuggable("step", "bring to foreground");
		}
		mResumeLastTime = true;

		LogUtil.logOnlyDebuggable("step", "step=" + mStepCount);
	}
	
	public void stepBackward() {
		LogUtil.logOnlyDebuggable("step", "onUserLeaveHint");
		postStepMessage();
		mResumeLastTime = false;
	
		LogUtil.logOnlyDebuggable("step", "step=" + mStepCount);
	}
	
	private boolean mAvailable = true;
	private void postStepMessage() {
		if (mAvailable) {
			mAvailable = false;
			mHandler.postDelayed(mRunnable, mDuration);
		}
	}
	
	private void removeStepMessage() {
		if (!mAvailable) {
			mHandler.removeCallbacks(mRunnable);
			mAvailable = true;
		}
	}
	
	public void finishCurrentActivity() {
		if (null != mActivity) {
			mActivity.finish();
		}
	}
}
