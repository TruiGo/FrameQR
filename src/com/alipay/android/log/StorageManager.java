package com.alipay.android.log;

import android.content.Context;

import com.alipay.android.log.Constants.BehaviourID;
import com.alipay.android.util.LogUtil;

public class StorageManager extends Thread{
	private Context mContext;
	private String mViewID;
	private String mAppID;
	private String mAppVersionID;
	private String mExceptionType;
	
	/**
	 * 记录时间，支付宝ID，支付宝版本，日志模型版本，TCID，会话ID，用户ID，行为ID，行为状态，
	 * 状态消息，子应用ID，子应用版本，视图ID，ref视图ID , 种子, URL, 行为发起方类型, 日志生产者类型
	 */
	private final String LOG_FORMAT_STRING = ",%s,%s,%s,%s,%s,%s,%s,%s,%s,%s,%s,%s,%s,%s,%s,%s,%s,%s";
	
//	private final String LOG_FORMAT_STRING = 	   "D-VM,%s,%s,%s,1,%s,,%s,%s,%s,%s,%s,%s,%s,%s$$";
	private final String LOG_ERROR_FORMAT_STRING =    "e,%s,%s,%s,1,%s,,%s,%s,%s,%s,%s,%s,%s,%s$$";
	//存储状态信息
	private StorageStateInfo mStorageStateInfo;
	
	
	/**
	 * 记录时间，支付宝ID，支付宝版本，日志模型版本，TCID，会话ID，用户ID，
	 * 行为ID，行为状态，状态消息，子应用ID，子应用版本，视图ID，ref视图ID , 种子, URL, 行为发起方类型,  日志生产者类型
	 */
	private Constants.BehaviourID mBehaviourID;
	private String mBehaviourStatus;
	private String mStatusMessage;
	private String mAppVersion;
	private String mRefViewID;
	private String mSeed;
	private String mUrl;
	private String mBehaviourPro;
	private String mLogPro;
	private String[] mExtendParam;
	
	StorageManager(Context context, Constants.BehaviourID behaviourID, String behaviourStatus, String statusMessage, 
			String appID,String appVersion, String viewID, String refViewID, 
			String seed, String url, String behaviourPro, String logPro,String[] extendParams){
		mStorageStateInfo = StorageStateInfo.getInstance();
		this.mContext = context;
		this.mBehaviourID = behaviourID;
		this.mBehaviourStatus = behaviourStatus;
		this.mStatusMessage = statusMessage;
		this.mAppID = appID;
		this.mAppVersion = appVersion;
		this.mViewID = viewID;
		this.mRefViewID = refViewID;
		this.mSeed = seed;
		this.mUrl = url;
		this.mBehaviourPro = behaviourPro;
		this.mLogPro = logPro;
		this.mExtendParam = extendParams;
	}
	
	/**
	 * WebApp使用
	 */
	public StorageManager(Context context,Constants.BehaviourID behaviourID,String behaviourStatus,String statusMessage,
			String appID,String appVersion,String[] extendParams) {
		this(context, behaviourID, behaviourStatus, statusMessage, appID, appVersion, null, null, null, null, null, null, null);
		if(extendParams != null &&extendParams.length >= 6){
			mViewID = extendParams[0];
			mRefViewID = extendParams[1];
			mSeed = extendParams[2];
			mUrl = extendParams[3];
			mBehaviourPro = extendParams[4];
			mLogPro = extendParams[5];
			
			if(extendParams.length > 6){
				mExtendParam = new String[extendParams.length - 6];
				for(int i = 0;i < mExtendParam.length;i++){
					mExtendParam[i] = extendParams[i + 6];
				}
			}
		}
	}
	
	@Override
	public void run() {
		if(Constants.BehaviourID.MONITOR.equals(mBehaviourID)){
			writeModel(mBehaviourStatus+LOG_FORMAT_STRING);
		}else if (Constants.BehaviourID.ERROR == mBehaviourID 
			|| Constants.BehaviourID.EXCEPTION == mBehaviourID) {
			writeErrorModel();
		} else {
			writeModel("D-VM"+LOG_FORMAT_STRING);
		}
	}

	private void writeErrorModel() {
		String userID = mStorageStateInfo.getValue(Constants.STORAGE_USERID);
		String logStr = String.format(LOG_ERROR_FORMAT_STRING,
				LogBaseHelper.getNowTime(),//时间
				mStorageStateInfo.getValue(Constants.STORAGE_PRODUCTID),//支付宝ID
				mStorageStateInfo.getValue(Constants.STORAGE_PRODUCTVERSION),//支付宝版本
				mStorageStateInfo.getValue(Constants.STORAGE_CLIENTID),//ClientID
				mStorageStateInfo.getValue(Constants.STORAGE_USERID),//UserID
				mBehaviourID == null ? "" : mBehaviourID.getDes(),//行为ID
				(userID ==null || "".equals(userID)) ? Constants.STATE_UNLOGIN : Constants.STATE_LOGIN,//行为状态
				"", //状态消息
				mAppID== null ? "" : mAppID, //子应用ID
				mAppVersionID == null ? "" : mAppVersionID, //子应用版本
				mExceptionType, //异常类型
				mStatusMessage == null ? "" : mStatusMessage);//堆栈信息

		LogUtil.logOnlyDebuggable("StorageManager", logStr);
		LogBaseHelper.writeToFile(mContext, Constants.LOGFILE_PATH, Constants.LOGFILE_NAME, logStr);
		CheckAndSend();
	}
	
	/**
	 * @param userID 支付宝账户ID
	 * @param behaviourID 用户做了一个什么行为,可扩展,需要与BI同步
	 * @param behaviourStatus 用户做了一个行为的结果是什么
	 * @param statusMessage 用户做了一个行为的结果附加信息是什么
	 * @param appID 应用模块
	 * @param appVerision 应用模块的版本
	 * @param currentViewID 用户当前看到的是什么视图界面
	 * @param refViewID 用户上一个看到的是什么视图界面
	 * @param seed 用户在上一个视图界面上点了什么埋点
	 * @param url 用户当前所在视图的URL是什么，客户端没有URL，该字段为具有url的页面准备的
	 * @param behaviourPro 行为发起方类型：该行为是用户主动发起还是系统主动发起，u代表用户，s代表系统
	 * @param logPro 日志生产者类型：该日志是client日志、wap日志还是server日志，c代表客户端，w代表wap页面，s代表服务端
	 */
	private void writeModel(String modelPattern){
		String logStr = String.format(modelPattern,
				LogBaseHelper.getNowTime(),//时间
				mStorageStateInfo.getValue(Constants.STORAGE_PRODUCTID),//支付宝ID
				mStorageStateInfo.getValue(Constants.STORAGE_PRODUCTVERSION),//支付宝版本
				mStorageStateInfo.getValue(Constants.STORAGE_MODELVERSION),//modelVersion
				mStorageStateInfo.getValue(Constants.STORAGE_CLIENTID),//ClientID
				mStorageStateInfo.getValue(Constants.STORAGE_UUID),//UUID
				mStorageStateInfo.getValue(Constants.STORAGE_USERID),//userID
				mBehaviourID == null ? "" : mBehaviourID.getDes(),//行为ID
				mBehaviourStatus == null ? "" : mBehaviourStatus,//行为ID
				mStatusMessage == null ? "" : mStatusMessage,
				mAppID == null ? "" : mAppID,
				mAppVersion == null ? "" : mAppVersion,
 				mViewID == null ? "" : mViewID,
				mRefViewID == null ? "" : mRefViewID,
				mSeed == null ? "" : mSeed,
				mUrl == null ? "" : mUrl,
				(mBehaviourPro == null || "".equals(mBehaviourPro)) ? "u" : mBehaviourPro,
				(mLogPro == null || "".equals(mLogPro)) ? "c" : mLogPro);
		
		//添加扩展字段
		if(mExtendParam != null && mExtendParam.length > 0){
			StringBuilder logBuilder = new StringBuilder(logStr);
			for(String curParam : mExtendParam){
				logBuilder.append("," + curParam);
			}
			
			logStr = logBuilder.toString();
		}
		
		String log = logStr + "$$";
		LogUtil.logOnlyDebuggable("StorageManager", log);
		LogBaseHelper.writeToFile(mContext, Constants.LOGFILE_PATH, Constants.LOGFILE_NAME, log);
		
		CheckAndSend();
	}

	StorageManager(Context context, String parament1, String parament2, String ViewID, int LogType,String... extendParams){
		this(context, null, null, null, null, null, null, null, null, null, null, null, extendParams);
		logTypeParams(parament1, parament2, LogType, extendParams);
	}
	
	private void logTypeParams(String parament1, String parament2, int LogType, String... extendParams) {
		switch (LogType) {
		case Constants.STORAGE_TYPE_ERROR:
			mExceptionType = parament2;
			mStatusMessage = parament1;
//			mBehaviourID = (mExceptionType != null && mExceptionType.equals()) ? BehaviourID.ERROR;
			mBehaviourID = BehaviourID.EXCEPTION;
			break;
		case Constants.STORAGE_TYPE_EVENT:
			mBehaviourID = BehaviourID.CLICKED;
			if(extendParams != null && extendParams.length > 0)
				mSeed = extendParams[0];
			mBehaviourStatus = parament2;
			break;
		case Constants.STORAGE_TYPE_PAGEJUMP:
			mBehaviourID = BehaviourID.NONE;
			mViewID = parament1;
			mRefViewID = parament2;
			break;
		default:
			mBehaviourID = BehaviourID.NONE;
			break;
		}
	}
	
	
	/*
	 * 检查是否需要上传
	 */
	private void CheckAndSend(){
		Constants.LOG_ACCOUNT++;
		long nowtime = System.currentTimeMillis();
		if(Constants.LOG_ACCOUNT>= Constants.LOG_MAX_ACCOUNT&&nowtime - Constants.LAST_SEND_TIME>60000){
			if(LogSendManager.SendLog(mContext)){
				Constants.LOG_ACCOUNT=0;
			}else{
				Constants.LOG_ACCOUNT = Constants.LOG_MAX_ACCOUNT;
			}
			Constants.LAST_SEND_TIME = nowtime;
		}
	}
}
