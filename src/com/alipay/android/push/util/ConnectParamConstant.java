package com.alipay.android.push.util;

public class ConnectParamConstant {
	// used to initial message (client to server)
	// client need to tell server these properties
	public static final String USERID = "userId";
	public static final String IMEI = "IMEI";
	public static final String PRODUCTID  = "productId";
	public static final String PRODUCTVERSION = "productVersion";
	public static final String OSTYPE = "osType";
	public static final String OSVERSION = "osVersion";
	public static final String INSTALLCHANNEL = "installChannel";
	public static final String CONNECTTYPE = "connectType";
	public static final String MANUFACTURER = "manufacturer";
	public static final String MODEL = "model";
	public static final String PUSHVERSION = "pushVersion";
	public static final String MOBILE_STATE = "mobileStatus";
	public static final String MOBILE_IP = "regionIP";
	
	// about user location information
	//CID标识基站，若手机处在工作状态，则必须要和一个通讯基站进行通讯，通过CID就可以确定手机所在的地理范围
	public static final String CELL_ID = "cellId";
	//标识国家:一个国家的MCC不唯一，例如中国有460和461，
	public static final String CELL_MCC = "cellIdMCC";
	//一家运营商也不只一个MNC，例如中国移动有00、02、07
	public static final String CELL_MNC = "cellIdMNC";
	//LAC标识区域，运营商将大区域划分成若干小区域，每个区域分配一个LAC
	public static final String CELL_LAC = "cellIdLAC";
	
	public static final String LATITUDE = "latitude";
	public static final String LONGITITUDE = "longititude";
	public static final String ACCURACY_RANGE = "accuracyRange";
	
	//初始化请求的响应
	public static final String KEEPLIVE_TIME = "keepLiveTime";
	public static final String RECONNECT_TIME = "reconnectTime";
	public static final String UPDATE_LBSINFO = "updateLBSInfo";
	
	// used to push content message (server to client)
	// client need to these properties to show or next action
	public static final String NOTIFICATION_ID = "notificationId";
	public static final String NOTIFICATION_MISSIONID = "notificationMissionId";
	public static final String NOTIFICATION_TITLE = "title";
	public static final String NOTIFICATION_MESSAGE = "content";
	public static final String NOTIFICATION_URI = "uri";
	public static final String NOTIFICATION_STYLE = "style";
}
