package com.alipay.android.push.util;

/**
 * Static constants for this package.
 */
public class Constants {
	
	public static final boolean PUSH_TEST = false;

    public static final String PUSH_PREFERENCE_NAME = "push_preferences";
    
    public static final String PUSH_SERVICE_RECORD = "service_record";
    public static final String SERVICE_RECORD_LINKED = "#";
    public static final String SERVICE_RECORD_SEPERATOR = "|";
    public static final String SERVICE_INIT_TIME = "1970-01-01 00:00:00";
    public static final String SERVICE_OLD_TRIGER = "old_triger";
    public static final String SERVICE_OLD_TIME = "old_time";
    public static final String SERVICE_PRE_TRIGER = "pre_triger";
    public static final String SERVICE_PRE_TIME = "pre_time";
    public static final String SERVICE_CUR_TRIGER = "cur_triger";
    public static final String SERVICE_CUR_TIME = "cur_time";
    
    public static final String SERVICE_INIT_TRIGER = "0";
    public static final String SERVICE_BOOT_TRIGER = "11";
    public static final String SERVICE_POWER_TRIGER = "12";
    public static final String SERVICE_USER_TRIGER = "13";
    public static final String SERVICE_CONN_TRIGER = "14";

    // PREFERENCE KEYS
    public static final String CALLBACK_ACTIVITY_PACKAGE_NAME = "CALLBACK_ACTIVITY_PACKAGE_NAME";
    public static final String CALLBACK_ACTIVITY_CLASS_NAME = "CALLBACK_ACTIVITY_CLASS_NAME";

    public static final String API_KEY = "API_KEY";
    public static final String VERSION = "VERSION";
    public static final String SSL_USED = "SSL_USED";
    public static final String ZIP_FLAG = "ZIP_FLAG";
    public static final String RETRY_TIMES = "RETRY_TIMES";
    public static final String PROTOCOL_VERSION = "PROTOCOL_VERSION";
    public static final String XMPP_HOST = "XMPP_HOST";
    public static final String XMPP_PORT = "XMPP_PORT";
    public static final String XMPP_USERNAME = "XMPP_USERNAME";
    public static final String XMPP_TOKEN = "XMPP_TOKEN";
    public static final String DEVICE_ID = "DEVICE_ID";
    
    public static final String CONTENT_PUB_MSG_ID = "CONTENT_PUB_MSG_ID";
    public static final String CONTENT_PER_MSG_ID = "CONTENT_PER_MSG_ID";
    public static final String CONTENT_MISSION_ID = "CONTENT_MISSION_ID";
    
    public static final String CLIENT_IDENTIFIER = "CLIENT_IDENTIFIER";
    public static final String LAST_CONNECTED_TIME = "LAST_CONNECTED_TIME";
    public static final String LAST_CONFIG_TIME = "LAST_CONFIG_TIME";
    public static final String DELAY_CONFIG_TIME = "DELAY_CONFIG_TIME";
    public static final String CREATE_CONNECT_TIME = "CREATE_CONNECT_TIME";

    public static final String EMULATOR_DEVICE_ID = "EMULATOR_DEVICE_ID";
    public static final String NOTIFICATION_ICON = "NOTIFICATION_ICON";

    public static final String NOTIFICATION_SETTING_NAME = "";
    
    // NOTIFICATION SETTINGS    
    public static final String SETTINGS_NOTIFICATION_ENABLED = "SETTINGS_NOTIFICATION_ENABLED";
    public static final String SETTINGS_SOUND_ENABLED = "SETTINGS_SOUND_ENABLED";
    public static final String SETTINGS_VIBRATE_ENABLED = "SETTINGS_VIBRATE_ENABLED";
    public static final String SETTINGS_TOAST_ENABLED = "SETTINGS_TOAST_ENABLED";
    public static final String SETTINGS_TIME_STARTTIME = "SETTINGS_TIME_START";
    public static final String SETTINGS_TIME_ENDTIME = "SETTINGS_TIME_END";

    // NOTIFICATION FIELDS
    public static final String NOTIFICATION_ID = "NOTIFICATION_ID";
    public static final String NOTIFICATION_TITLE = "NOTIFICATION_TITLE";
    public static final String NOTIFICATION_MESSAGE = "NOTIFICATION_MESSAGE";
    public static final String NOTIFICATION_URI = "NOTIFICATION_URI";
    public static final String NOTIFICATION_STYLE = "NOTIFICATION_STYLE";
    public static final String NOTIFICATION_BIZTYPE = "NOTIFICATION_BIZTYPE";
    public static final String NOTIFICATION_PUSH_DATA = "pushData";
    public static final String NOTIFICATION_MISSION_ID = "pushMissionId";
    public static final String NOTIFICATION_MSG_ID = "perMsgId";
    public static final String NOTIFICATION_PUB_MSG_ID = "pubMsgId";

    // INTENT ACTIONS
    public static final String ACTION_SHOW_PUBMESSAGE = "com.alipay.android.push.SHOW_PUBMESSAGE";
    public static final String ACTION_SHOW_NOTIFICATION = "com.alipay.android.push.SHOW_NOTIFICATION";
    public static final String ACTION_NOTIFICATION_CLICKED = "com.alipay.android.push.NOTIFICATION_CLICKED";
    public static final String ACTION_NOTIFICATION_CLEARED = "com.alipay.android.push.NOTIFICATION_CLEARED";
    
    public static final String ACTION_PUSH_CONNECT = "com.alipay.android.push.CONNECT";
    public static final String ACTION_PUSH_DISCONNECT = "com.alipay.android.push.DISCONNECT";
    
    public static final String ACTION_KEEPLIVE_TIMER = "com.alipay.android.push.KEEPLIVE";
    
    public static final int MSG_KEEPLIVE_TIMER = 1016;

    // NOTIFICATION DEFAULT TIME
    public static final String NOTIFICATION_DEAFULT_STARTTIME = "8";
    public static final String NOTIFICATION_DEAFULT_ENDTIME = "23";
    
    // ------------------------------------------------------------------------
    public final static String CHANNELS = "channels";
    
    // config request fields
    public final static String REQ_USER_ID = "userId";
    public final static String REQ_CLIENT_ID = "clientId";
    public final static String REQ_PUB_MSG_ID = "lastPubMsgId";
    public final static String REQ_PUSH_CFG_ID = "pushCfgId";
    public final static String REQ_CHANNEL_ID = "channelId";
    public final static String REQ_PRODUCT_ID = "productId";
    public final static String REQ_VERSION_ID = "versionId";
    public final static String REQ_OS_TYPE = "osType";
    public final static String REQ_OS_VERSION = "osVersion";
    
    // config response fields
    public final static String RPF_RESULT_STATUS = "resultStatus";
    public final static String RPF_MEMO = "memo";
    
    public final static String RPF_DATE_RECORD = "dataRecord";
    public final static String RPF_CONSUME_COUNT = "consumeCount";
    public final static String RPF_SUCCESS_COUNT = "successCount";
    public final static String RPF_INTERVAL_TIME = "intervalTime";
    public final static String RPF_PUSH_CFG = "pushCfg";
    public final static String RPF_PUSH_PUB_MSG = "publicMsg";
    public final static String RPF_PUSH_PERSONAL_MSG = "personalMsg";
    
    // about message
    public final static String PUSH_NOTIFIRE = "notifier_parcelable";
    public final static String RPF_MISSION_ID = "notificationMissionId";
    public final static String RPF_MSG_ID = "notificationId";
    public final static String RPF_MSG_TITLE = "title";
    public final static String RPF_MSG_CONTENT = "content";
    public final static String RPF_MSG_URI = "uri";
    public final static String RPF_MSG_STYLE = "style";
    public final static String RPF_MSG_BIZTYPE = "bizType";
    
    // about server config
    public final static String RPF_CFG_DOMAIN = "domain";
    public final static String RPF_CFG_PORT = "port";
    public final static String RPF_CFG_SSL = "ssl";
    public final static String RPF_CFG_ZIP = "compress";
    public final static String RPF_CFG_VERSION = "version";
    
}
