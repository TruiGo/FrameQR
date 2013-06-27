package com.alipay.android.log;

import android.content.Context;

import com.alipay.android.log.Constants.BehaviourID;

public class AlipayLogAgent {
	
	/*
	 * 异常记录
	 */
	public static void onError(Context context, String errorStr, String ViewID, String exceptionType){
		try{
			   if(context==null||!Constants.LOG_SWITCH){
				   return;
			   }
			   new StorageManager(context, errorStr, exceptionType ,ViewID, Constants.STORAGE_TYPE_ERROR).start();
	   }catch(Exception e){
			   e.printStackTrace();
	   }
	}
	
	/*
	 * 上传日志至服务器
	 */
	public static void uploadLog(Context context/*, String errorStr*/){
		try{
			   if(context==null||!Constants.LOG_SWITCH){
				   return;
			   }
			   new LogSendManager(context/*, errorStr*/).start();
	   }catch(Exception e){
			   e.printStackTrace();
	   }
	}
	
	/**
	 * @param behaviourID 行为ID 为monitor时，需要在行为状态指定日志类型如：D-VM,e,used...
	 * @param behaviourStatus 行为状态
	 * @param statusMessage 状态消息
	 * @param appID 应用ID
	 * @param appVersion 应用版本
	 * @param viewID 当前视图ID
	 * @param refViewID 跳转之前的视图ID
	 * @param seed 种子
	 * @param extendParams 扩展ID
	 * 
	 * @see #writeLog(Context, String, String, String, String, String[])
	 * @see #writeLog(Context, String, com.alipay.android.log.Constants.BehaviourID, String, String, String, String, String, String, String, String, String, String, String[])
	 */
	public static void writeLog(Context context, Constants.BehaviourID behaviourID, String behaviourStatus, String statusMessage, 
			String appID,String appVersion, String viewID, String refViewID, String seed,String... extendParams){
		try {
			if (context == null || !Constants.LOG_SWITCH) {
				return;
			}
			new StorageManager(context, behaviourID, behaviourStatus, statusMessage, appID, appVersion, viewID, refViewID, seed, null,null,null,extendParams)
					.start();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * @param context
	 * @param behaviourID 为monitor时，需要在行为状态指定日志类型如：D-VM,e,used...
	 * @param behaviourStatus
	 * @param statusMessage
	 * @param appID
	 * @param appVersion
	 * @param viewID
	 * @param refViewID
	 * @param seed
	 */
	public static void writeLog(Context context, Constants.BehaviourID behaviourID, String behaviourStatus, String statusMessage, 
			String appID,String appVersion, String viewID, String refViewID, String seed){
		try {
			if (context == null || !Constants.LOG_SWITCH) {
				return;
			}
			new StorageManager(context, behaviourID, behaviourStatus, statusMessage, appID, appVersion, viewID, refViewID, seed,null,null,null,null)
					.start();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * @param behaviourID 行为ID 为monitor时，需要在行为状态指定日志类型如：D-VM,e,used...
	 * @param behaviourStatus 行为状态
	 * @param statusMessage 状态消息
	 * @param appID 应用ID
	 * @param appVersion 应用版本
	 * @param viewID 当前视图ID
	 * @param refViewID 跳转之前的视图ID
	 * @param seed 种子ID
	 * @param url 当前所在界面url
	 * @param behaviourPro 行为产生者
	 * @param logPro 日志产生者
	 * @param extendParams 扩展字段
	 */
	public static void writeLog(Context context, Constants.BehaviourID behaviourID,String appID,String viewID, String refViewID, String seed){
		try {
			if (context == null || !Constants.LOG_SWITCH) {
				return;
			}
			new StorageManager(context, behaviourID,null,null, appID,null, viewID, refViewID, seed,null,null,null,null)
			.start();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	/*public static void writeLog(Context context, Constants.BehaviourID behaviourID,String appID,String appVersion,String viewID, String refViewID, String seed){
		try {
			if (context == null || !Constants.LOG_SWITCH) {
				return;
			}
			new StorageManager(context, behaviourID,null,null, appID,appVersion, viewID, refViewID, seed,null,null,null,null)
			.start();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public static void writeLog(Context context, Constants.BehaviourID behaviourID, String behaviourStatus, String statusMessage, 
			String appID,String appVersion, String viewID, String refViewID, 
			String seed, String url, String behaviourPro, String logPro,String... extendParams){
		try {
			if (context == null || !Constants.LOG_SWITCH) {
				return;
			}
			new StorageManager(context,behaviourID, behaviourStatus, statusMessage, appID, appVersion, viewID, refViewID, seed, url,
					behaviourPro, logPro, extendParams).start();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}*/
	
	/**
	 * 行为ID，行为状态，状态消息，子应用ID，子应用版本，视图ID，ref视图ID , 种子, URL, 行为发起方类型,  日志生产者类型
	 */
	public static void writeLog(Context context, String behaviourID,String behaviourStatus,String statusMessage,String appID,String appVersion,String[] extendParams){
		try {
			if (context == null || !Constants.LOG_SWITCH) {
				return;
			}
			
			BehaviourID behavID = null;
			if(behaviourID.equals(BehaviourID.CLICKED.getDes())){
				behavID = BehaviourID.CLICKED;
			}else if(behaviourID.equals(BehaviourID.SUBMITED.getDes())){
				behavID = BehaviourID.SUBMITED;
			}else if(behaviourID.equals(BehaviourID.BIZLAUNCHED.getDes())){
				behavID = BehaviourID.BIZLAUNCHED;
			}else if(behaviourID.equals(BehaviourID.ERROR.getDes())){
				behavID = BehaviourID.ERROR;
			}else if(behaviourID.equals(BehaviourID.EXCEPTION.getDes())){
				behavID = BehaviourID.EXCEPTION;
			}else if(behaviourID.equals(BehaviourID.MONITOR.getDes())){
				behavID = BehaviourID.MONITOR;
			}else {
				behavID = BehaviourID.NONE;
			}
			
			new StorageManager(context, behavID,behaviourStatus,statusMessage,appID,appVersion, extendParams).start();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * 注册客户端常用信息
	 * @param alipayId productID
	 * @param productVersion 
	 * @param clientID
	 * @param uuId
	 * @param modelVersion 1
	 */
	public static void initClient(String alipayId,String productVersion,String clientID,String uuId,String modelVersion){
		StorageStateInfo stateInfos = StorageStateInfo.getInstance();
		stateInfos.registClient(alipayId, productVersion, clientID, uuId, modelVersion);
	}
	
	/**
	 * 清除注册信息
	 */
	public static void unInitClient(){
		StorageStateInfo stateInfos = StorageStateInfo.getInstance();
		stateInfos.unRegistClient();
	}

}
