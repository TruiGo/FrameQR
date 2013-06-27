package com.alipay.android.client.constant;

import java.net.MalformedURLException;
import java.net.URL;

import org.json.JSONObject;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;

import com.alipay.android.common.data.UserInfo;

/*
 * 	This class define all the constant value needed by Alipay
 * 
 *  draft: dingding.bx.jiang@embeddedgeneral.com
 * */

public class Constant {
    /**
     * 从支付宝设置程序获取值
     */
    public static String getValue(Context context, String uri, String defaultVal) {
    	
//    	return "http://sx1.aaa.alipay.net/mobilecs.htm";
    	
        Cursor cursor = context.getContentResolver().query(Uri.parse(uri), null, null, null, null);
        if (cursor != null && cursor.getCount() > 0) {
            cursor.moveToFirst();
            String ret = cursor.getString(0);
            cursor.close();
            return ret;
        }
        return defaultVal;
    }

    /**
     * 是否是开发状态
     */
    public static boolean isDebug(Context context) {
        try {
            ApplicationInfo applicationInfo = context.getPackageManager().getApplicationInfo(
                context.getPackageName(), PackageManager.GET_CONFIGURATIONS);
            if ((applicationInfo.flags & ApplicationInfo.FLAG_DEBUGGABLE) != 0) {
                // development mode
                return true;
            } else {
                //release mode
                return false;
            }
        } catch (Exception e) {
        }
        return false;
    }
    /**
     * 获取信用卡CCDC地址
     * @param context
     * @return
     */
//    public final static String getCcrCcdcURL(Context context){
//    	if (isDebug(context)) {
//            return getValue(context, "content://com.alipay.setting/CcrCcdcUrl",
//            		"https://ccdcapi.alipay.com/cacheWapCardInfo.json");
//        } else {
//            return "https://ccdcapi.alipay.com/cacheWapCardInfo.json";
//        }
//    }
    
    /**
     * 获取CCDC地址
     * @param context
     * @return
     */
    public final static String getCcdcURL(Context context){

    	if (isDebug(context)) {
            String valueString = getValue(context, "content://com.alipay.setting/CcrCcdcUrl",
            		"https://ccdcapi.alipay.com/");
      
        	try {
    			URL aUrl = new URL(valueString);
    			valueString = aUrl.getProtocol() + "://" + aUrl.getHost() + "/";
    		} catch (MalformedURLException e) {
    			// TODO Auto-generated catch block
    			e.printStackTrace();
    		}
        	
        	return valueString;
        } else {
            return "https://ccdcapi.alipay.com/";
        }

    }

    /**
     * 获取业务地址
     */
    public final static String getAlipayURL(Context context) {
        if (isDebug(context)) {
            return getValue(context, "content://com.alipay.setting/BizServerUrl",
                "https://mgw.alipay.com/mobilecs.htm");
        } else {
            return "https://mgw.alipay.com/mobilecs.htm";
        }
    }

    /**
     * 获取快捷支付地址
     */
    public final static String getSafepayURL(Context context) {
        if (isDebug(context)) {
            return getValue(context, "content://com.alipay.setting/MspServerUrl",
                "https://msp.alipay.com/x.htm");
        } else {
            return "https://msp.alipay.com/x.htm";
        }
    }

    /**
     * 获取容器地址
     */
    public final static String getContainerURL(Context context) {
        if (isDebug(context)) {
            return getValue(context, "content://com.alipay.setting/ContainerServerUrl",
                "https://mobilecsprod.alipay.com/mobilecs.htm");
        } else {
            return "https://mobilecsprod.alipay.com/mobilecs.htm";
        }
    }

    public final static String getAlipayServer(Context context) {
        String strUrl = getAlipayURL(context);

        URL url = null;
        try {
            url = new URL(strUrl);
            return url.getProtocol() + "://" + url.getHost();
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }

        return null;
    }

    /**
     * 获取PUSH端口
     */
    public final static int getPushPort(Context context) {
        if (isDebug(context)) {
            return Integer.parseInt(getValue(context, "content://com.alipay.setting/PushPort",
                "443"));
        } else {
            return 443;
        }
    }

    /**
     * 获取PUSH地址
     */
    public final static String getPushURL(Context context) {
        if (isDebug(context)) {
            return getValue(context, "content://com.alipay.setting/PushServerUrl",
                "mobilepmgw.alipay.com");
        } else {
            return "mobilepmgw.alipay.com";
        }
    }

    /**
     * 获取PUSH是否使用SSL
     */
    public final static boolean getPushSsl(Context context) {
        if (isDebug(context)) {
            return getValue(context, "content://com.alipay.setting/PushUseSsl", "1").equals("1");
        } else {
            return true;
        }
    }

    /**
     * 获取是否输出日志
     */
    public final static boolean getLog(Context context) {
        if (isDebug(context)) {
            return getValue(context, "content://com.alipay.setting/Log", "1").equals("1");
        } else {
            return false;
        }
    }

    /**
     * 获取统计埋点地址
     */
    public final static String getStatisticsUrl(Context context) {
        if (isDebug(context)) {
            return getValue(context, "content://com.alipay.setting/StatisticsServerUrl",
                "http://mdap.alipay.com/loggw/log.do");
        } else {
            return "http://mdap.alipay.com/loggw/log.do";
        }
    }
    
    /**
     * 获取cns系统的地址
     */
    public final static String getCnsServer(Context context) {
    	if (isDebug(context)) {
            return "http://reserv3.d133.alipay.net";
        } else {
            return "http://mobilecns.alipay.com";
        }
    }
    
    /**
     * 获取配置轮询的地址
     */
    public final static String getConfigrURL(Context context){
    	return getCnsServer(context) + "/getMsgAndCfg.htm";
    }
    
    /**
     * 获取点击上报的地址
     */
    public final static String getReportURL(Context context){
    	return getCnsServer(context) + "/msgRead.htm";
    }

    public final static String getPucPayURL(Context context) {
        return getAlipayServer(context) + "/puc/webMC.htm";
    }

    public final static String getAmusementURL(Context context) {
        return getAlipayServer(context) + "/amusement.htm";
    }

    public final static String getAgreementURL(Context context) {
        return getAlipayServer(context) + "/agreement.htm";
    }

    public final static String getLotteryUrl(Context context) {
        return getAlipayServer(context) + "/aoke.htm";
    }

    public final static String getAlipayDomain(Context context) {
        String strUrl = getAlipayURL(context);

        URL url = null;
        try {
            url = new URL(strUrl);
            return url.getHost();
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }

        return null;
    }

    public static String getURL(Context context, String url) {
        if (isDebug(context)) {
            if (url.indexOf("205") != -1 || url.indexOf("mobilecsprod") != -1) {
                return getContainerURL(context);
            }
            return getAlipayURL(context);
        } else {
            return url;
        }
    }
    
    public final static String BANK_BHB = "BHB";
    public final static String BANK_CEB = "CEB";
    public final static String BANK_BGB = "BGB";
    public final static String BANK_BOC = "BOC";
    public final static String BANK_CCB = "CCB";
    public final static String BANK_CMB = "CMB";
    public final static String BANK_ZGCCB = "ZGCCB";
    public final static String BANK_CITIC = "CITIC";
    public final static String BANK_SDB = "SDB";
    public final static String BANK_SPABANK = "SPABANK";
    public final static String BANK_CMBC = "CMBC";
    public final static String BANK_CIB = "CIB";
    public final static String BANK_GDB = "GDB";
    public final static String BANK_SHRCB = "SHRCB";
    public final static String BANK_HXBANK = "HXBANK";
    public final static String BANK_COMM = "COMM";
    public final static String BANK_TCCB = "TCCB";
    public final static String BANK_JSBANK = "JSBANK";
    public final static String BANK_ALIPAY = "ALIPAY";
    public final static String BANKTERMURL = "http://fun.alipay.com/bank/index.htm?page=";
    public static String getTermUrl(String bankName, boolean creditCard) {
    	String bankTermUrl = null;
    	
    	if (creditCard) {
    		if (BANK_BHB.equals(bankName) ||
    			BANK_CEB.equals(bankName) ||
    			BANK_CMB.equals(bankName) ||
    			BANK_CITIC.equals(bankName) ||
    			BANK_SDB.equals(bankName) ||
    			BANK_SPABANK.equals(bankName) ||
    			BANK_CMBC.equals(bankName) ||
    			BANK_CIB.equals(bankName) ||
    			BANK_GDB.equals(bankName) ||
    			BANK_SHRCB.equals(bankName) ||
    			BANK_HXBANK.equals(bankName) ||
    			BANK_TCCB.equals(bankName) ||
    			BANK_JSBANK.equals(bankName) ||
    			BANK_ALIPAY.equals(bankName)) {
    			
    			bankTermUrl = BANKTERMURL + bankName;
    			} 
    	}else {
    		if (BANK_BHB.equals(bankName) ||
        			BANK_CEB.equals(bankName) ||
        			BANK_BGB.equals(bankName) ||
        			BANK_BOC.equals(bankName) ||
        			BANK_CCB.equals(bankName) ||
        			BANK_CMB.equals(bankName) ||
        			BANK_ZGCCB.equals(bankName) ||
        			BANK_CITIC.equals(bankName) ||
        			BANK_SDB.equals(bankName) ||
        			BANK_SPABANK.equals(bankName) ||
        			BANK_CMBC.equals(bankName) ||
        			BANK_CIB.equals(bankName) ||
        			BANK_GDB.equals(bankName) ||
        			BANK_SHRCB.equals(bankName) ||
        			BANK_HXBANK.equals(bankName) ||
        			BANK_COMM.equals(bankName) ||
        			BANK_TCCB.equals(bankName) ||
        			BANK_JSBANK.equals(bankName) ||
        			BANK_ALIPAY.equals(bankName)) {
    			
    			bankTermUrl = BANKTERMURL + bankName + "J";
    			} 
    	}
    	
    	return bankTermUrl;
    }

    public final static String OP_GET_RSA_KEY = "getRSAPKey";

    public final static String OP_LOGIN = "login";

    public final static String OP_CHECKPASSWORD = "checkPassword";

    public final static String OP_PERSONAL_SETUP = "personalSetup";

    public final static String OP_PRE_P2P_PAY = "p2pPrePay";

    public final static String OP_P2P_PAY = "p2pPay";

    public final static String OP_TRADE_PAY = "tradePay";

    public final static String OP_PRE_REGISTER = "preRegister";

    public final static String OP_REGISTER = "register";

    public final static String OP_CONFIRM_GOODS = "confirmGoods";

    public static final String USER_ROLE_BUYER = "buyer";

    public final static String OP_QUSER_INFO_SUPPLEMENT = "qUserInfoSupplement";

    public final static String OP_DEPOSIT_AMOUNT_LIST = "depositAmountList";

    public final static String OP_PRE_DEPOSIT_4_MOBILE = "preDeposit4Mobile";

    public final static String OP_DEPOSIT_4_MOBILE = "deposit4Mobile";

    public final static String OP_CONFIRM_MOBILE_PAY = "confirmMobilePay";

    public final static String OP_UPGRADE = "update";

    public final static String OP_PUC_PAY = "pucPay";

    public final static String OP_BILLINFO = "billinfo"; // TBD

    public final static String OP_APPLY_BINDING = "applyMobileBinding";

    public final static String RQF_MOBILE_NO = "mobileNo";

    public final static String OP_CONFIRM_BINDING = "bindMobileBinding";

    public final static String RQF_BINDING_CHECKCODE = "smsCheckCode";

    public final static String RQF_BINDING_PAYPWD = "paymentPassword";

    public final static String OP_GET_PUCBILLPAY_URL = "getPucBillPayUrl";

    public final static String OP_GET_NEXTMSG = "viewMsgDetail";

    public static final String OP_GET_MSGLIST = "viewMsgList";
    
    public static final String OP_GETMSGDETAIL = "getMsgDetail";

    public static final String OP_GET_MSGNUM = "count";

    public final static String OP_GET_MSGCOUNT = "getMsgCount";

    public final static String OP_BINDINGLOGINID = "bandingLoginId";

    public final static String OP_CREATE_P2P = "createP2P";

    public final static String OP_SUPPORTTRANSFERBANKLIST = "supportTransferBankList";

    public final static String OP_QUERY_ACCOUNTS = "queryUserAccounts";

    public final static String OP_LOGOUT = "logout";

    public final static String OP_GATHERING = "gathering";

    public final static String OP_AA_GATHERING = "preAABatchGathring";

    public final static String OP_NOTIFYDELIVERY = "notifyDelivery";
    
    public final static String OP_SENDCOLLECTIONMSG = "sendCollectionMsg";

    /* ����ͷ����?����ֶζ���?�? */
    public final static String FIELD_TO_MOBILE_NO = "toMobileNO";

    public final static String FIELD_TO_MOBILE_AMOUNT = "toMobileAmount";

    public final static String FIELD_TO_MOBILE_LOCATE = "locale";

    public final static String FIELD_TO_MOBILE_CORP = "corporation";

    public final static String FIELD_TO_PAY_AMOUNT = "toPayAmount";

    public final static String FIELD_BILL_NO = "billNO";

    public final static String FIELD_SMS_CODE = "smsCode";

    public final static String FIELD_SMS_CONTENT = "smsContent";

    /* ���������ֶζ��� */
    public final static String RQF_OPERATION_TYPE = "operationType"; // ������
    
    public final static String RQF_ACTION_TYPE = "actionType"; //business type.
    public final static String CARDBIZTYPE = "cardBizType";
    public final static int CREDITCARD = 1;
    public final static int DRAW = 2;

    public final static String RQF_CLIENT_ID = "clientID"; // �ͻ���ID

    public final static String RQF_LOGIN_ACCOUNT = "loginAccount"; // ��¼�˻���

    public final static String RQF_LOGIN_PASSWORD = "loginPassword"; // ����
    public final static String RQF_RSALOGIN_PASSWORD = "rsaLoginPassword";
    
    public final static String RQF_ACCESS_POINT = "accessPoint";

    public final static String RQF_PRODUCT_ID = "productID"; // �ͻ��˲�ƷID

    public final static String RQF_PRODUCT_VERSION = "productVersion"; // �ͻ��˲�ڰ�?�?

    public final static String RQF_LOGINTYPE = "loginType"; // �ͻ��˲�ڰ�?�?

    public final static String RQF_GREETING = "greeting";

    public final static String RQF_USER_AGENT = "userAgent";

    public final static String RQF_APPTYPE = "appType";

    public final static String RQF_CLIENTPOSITION = "clientPosition";

    public final static String RQF_SYSTEMVERSION = "systemVersion";

    public final static String RQF_SYSTEMTYPE = "systemType";

    public final static String RQF_CLIENTTYPE = "clientType";

    public final static String RQF_MOBILEBRAND = "mobileBrand";

    public final static String RQF_MOBILEMODEL = "mobileModel";

    public final static String RQF_SCREEN_WIDTH = "screenWidth";

    public final static String RQF_SCREEN_HIGH = "screenHigh";

    public final static String RQF_EXTRAPARAM = "extraParam";

    public final static String RPF_EXTRADATA = "extraData";

    public final static String RQF_TO_PAY_ACCOUNT = "toPayAccount";

    public final static String RQF_TO_PAY_REASON = "toPayReason";

    public final static String RQF_TO_PAY_AMOUNT = "toPayAmount";

    public final static String RQF_TO_PAY_MEMO = "toPayMemo";

    public final static String RQF_ACCOUNTCERTIFIED = "accountCertified";

    public final static String RQF_LOGINACCOUNT = "loginAccount";

    public final static String RQF_PAY_PASSWORD = "payPassword"; // ֧������

    public final static String RQF_SMS_PHONENO = "smsMobileNo";

    public final static String RQF_TRADE_NO = "tradeNo";

    public final static String RQF_USE_COUPON = "useCoupon";

    public final static String RQF_COUPON_AMOUNT = "couponAmount";

    public final static String RQF_CHECK_CODE = "checkCode";

    public final static String RQF_PHONE_NO = "phoneNO";

    public final static String RQF_REAL_NAME = "realName";

    public final static String RQF_USER_NAME = "userName";

    public final static String RQF_LOGON_ID = "logonId";

    public final static String RQF_TID = "tid";

    public final static String RQF_ACCOUNTLIST = "accountList";

    public final static String RQF_LOGINID = "loginId";

    public final static String RQF_ISADDTOCONTACTS = "isAddContact";

    public final static String RQF_SMSNUMBER = "smsMobileNo";

    public final static String RQF_TO_GET_ACCOUNT = "gatheringAccount"; // ????��?请�??��?

    public final static String RQF_TO_GET_REASON = "gatheringMemo";

    public final static String RQF_TO_GET_AMOUNT = "gatheringAmount";

    public final static String RQF_TO_GETALL_CONTENT = "detailContent"; // �?��?��?请�??��?

    public final static String RQF_TO_GETALL_REASON = "reason";

    public final static String RQF_TO_GETALL_ACCOUNT = "account";

    public final static String RQF_TO_GETALL_AMOUNT = "amount";

    public final static String RQF_TO_GETALL_TAG = "tag";

    /* ����˷��ؽӿ��ֶ�����?�? */
    public final static String RPF_RSA_PK = "rsaPK"; // ��Կ

    public final static String RPF_RSA_TS = "rsaTS"; // ʱ���?�?

    public final static String RPF_SESSION_ID = "sessionId"; // ʱ���?�?

    public final static String RPF_RESULT_STATUS = "resultStatus"; // ������

    public final static String RPF_ORDER_VALIDATE = "orderValidate"; // ������֤���?�?

    public final static String RPF_OUT_TRADE_NO = "outTradeNo"; // �ⲿ���׺�

    public final static String RPF_USER_NAME = "userName"; // �û����?�?

    public final static String RPF_TOTAL_FEE = "totalFee"; // Ӧ���ܼ�

    public final static String RPF_AVAILABLE_BALANCE = "availableBalance"; // �û��������?�?

    public final static String RPF_GREETING = "greeting"; // ��Ұ��?���?�?

    public final static String RPF_MEMO = "memo"; // ��ע

    public final static String RPF_REDBAG = "couponBalance"; // add by renxl on
                                                             // 2010-08-18

    public final static String RPF_FREEZE = "freezeBalance"; // add by renxl on
                                                             // 2010-08-18

    public final static String RPF_RESULT_TYPE = "resultType"; // add by renxl
                                                               // on 2010-08-25
    
    public final static String RPF_SMSPUSH = "smsPush"; // add by renxl

    public final static String RPF_POINTBALANCE = "pointBalance"; // �??�?

    public final static String RPF_LASTCHANGETIME = "lastChangeTime";

    public final static String RPF_BINDING_MOBILE = "bindingMobileNo";

    public final static String RPF_FLOW_TEXT = "flowText";

    public final static String RPF_YET_FLOW = "yetFlow";

    public final static String RPF_FREE_FLOW = "freeFlow";

    public final static String RPF_USERGRADE = "userGrade";

    public final static String RPF_BALANCEFLOW = "balanceFlow";

    public final static String RPF_HEADIMGPATH = "userHeadImgPath";
    
    public final static String RPF_COMMEND = "commend";
    
    public final static String RPF_CAN_BINDING = "canBindingMobile";

    public final static String RPF_CURENT_VERSION = "currentProductVersion";

    public final static String RPF_ISCERTIFICATING = "isCertificating";

    public final static String RPF_MSG_COUNT = "msgCount";

    public final static String RPF_TRADE_COUNT = "tradeCount";

    public final static String RPF_FIRST_MSG = "firstNewMsg";

    public final static String RPF_MOBILE_NO = "mobileNo";

    public final static String RPF_LOGIN_TOKEN = "loginToken";
    
    public final static String RPF_TAOBAOSID = "taobaoSid";

    public final static String RPF_LOGIN_USERID = "userID";

    public final static String RPF_GOODS_NAME = "goodsName";

    public final static String RPF_TRADE_FROM = "tradeFrom";

    public final static String RPF_IS_QUSER = "isQuser";

    public final static String RPF_TO_PAY_USER_NAME = "toPayUserName";

    public final static String RPF_TO_PAY_ACCOUNT = "toPayAccount";

    public final static String RPF_TO_IS_Contact = "isContact";

    public final static String RPF_ORDER_DATA = "orderData"; // �������?�?

    public final static String RPF_TRADE_DATA = "tradeData"; // ���صĶ������?�?

    public final static String RPF_PERSONAL_SETUP_RESULT = "personalSetupResult"; // �û������ڷ���˵��ʺ���?�?

    public final static String RPF_LOGON_ID = "logonId"; // �û�ע��ʱ��ĵ�¼�ʺ�?�?

    public final static String RPF_DOWNLOAD_URL = "downloadURL";

    public final static String RPF_URL = "url";

    public final static String RPF_CHECK_CODE = "checkCode";

    public final static String RQF_MOBILE_NUMBER = "mobileNumber";

    public final static String RPF_AMOUNT_LIST = "amountList";

    public final static String RPF_DISCOUNT = "discount";

    public final static String RPF_ABSENCE = "absence";

    // �ʻ���ѯ
    public final static String OP_QUERYBALANCE = "queryBalance"; // �˻�����ѯ

    public final static String RPF_CURRENTBALANCE = "currentBalance"; // ��ǰ���?�?

    // �ʻ���ϸ��ѯ
    public final static String OP_QUERYTRANSLIST = "queryTransList"; // �˻���ϸ��ѯ

    public final static String RQF_TIMERANGE = "timeRange"; // ��ѯʱ���?�?

    public final static String RQF_NEXTPAGE = "nextPage"; // ��һҳ

    public final static String RQF_PAGECOUNT = "pageCount"; // ÿҳ��ʾ�������?�?

    public final static String RQF_STARTDATETIME = "startDateTime";

    public final static int PAGECOUNT = 5; // ÿҳ��ʾ�������?�?

    public final static String RQF_USERROLE = "userRole";

    // query Trade
    public final static String OP_QUERYTRADENUM = "queryTradeNum";

    public final static String RQF_QUERYTRADETYPE = "queryTradeType";

    public final static String RPF_DATE = "date"; // ʱ��

    public final static String RPF_SIMPLEDATE = "simpleDate"; // ʱ��

    public final static String RPF_TRANSTYPE = "transType"; // ��������

    public final static String RPF_MONEY = "money"; // ���?�?

    public final static String RPF_TRANSLOGID = "transLogId"; // ������ˮ��

    public final static String RPF_TRANSINSTITUTION = "transInstitution"; // ����

    public final static String RPF_TRANSMEMO = "transMemo"; // ��ע

    public final static String RPF_PAGECOUNT = "pageCount"; // ÿҳ��ʾ�������?�?

    public final static String RPF_PAGE = "page"; // Ŀǰ�ڼ�ҳ

    public final static String RPF_TOTALCOUNT = "totalCount"; // ���������?�?

    public final static String RPF_TABLELIST = "transList"; //

    public final static String RPF_USERROLE = "userRole";

    public final static String RPF_STARTROWNUM = "startRowNum";

    // ������ϸ��ѯ
    public final static String OP_QUERYTRADELISTS = "queryTradeLists"; // ������ϸ��ѯ

    public final static String RPF_TRADETIME = "tradeTime"; // ����ʱ��
    
    public final static String RPF_BILLDATE = "billDate";
    
    public final static String RPF_BILLUSERNAME = "billUserName";

    public final static String RPF_GOODSNAME = "goodsName"; // ��Ʒ���?�?
    
    public final static String RPF_SHOWTITLE = "showTitle";

    public final static String RPF_TRADEMONEY = "tradeMoney"; // ���׽��?�?
    
    public final static String RPF_BILLAMOUNT = "billAmount";

    public final static String RPF_TRADESTATUS = "tradeStatus"; // ����״̬

    public final static String RPF_STATUS = "status";
    
    public final static String RPF_BUYER = "buyer"; // ���Ƿ������?�?

    public final static String RPF_TRADESTATUSMEMO = "tradeStatusMemo"; // ����״̬Memo
    
    public final static String RPF_STATUSMEMO = "statusMemo";

    public final static String RPF_TRANFERSTATUS = "tranferStatus"; // 

    public final static String RPF_TRADENO = "tradeNO"; // ������ˮ��
    
    public final static String RPF_ID = "id";

    public final static String RPF_ACT = "act"; // ������Ϊ

    public final static String RPF_TRADETABLELIST = "tradeTableList";

    public final static String RPF_BIZTYPE = "bizType";

    // ?��?�??�??�?
    public final static String OP_QUERYSCORELISTS = "queryPointLists"; // 请�?类�?

    public final static String RPF_TRANSID = "transId"; // �?��??

    public final static String RPF_TRANSMONEY = "transMoney"; // �??�????

    public final static String RPF_TRANSDATE = "transDate"; // ????��?

    public final static String RPF_POINTLIST = "pointList"; // �????��

    // ���ʽ��ײ�ѯ
    public final static String OP_QUERYTRADEDETAIL = "queryTradeDetail"; // ���ʽ���������?
    
    public final static String OP_QUERYLIFEPAYBILL = "getChargeBills";
    
    public final static String OP_QUERYLIFEPAYDETAIL = "confirmBillInfo";
    
    public final static String OP_QUERYLIFEPAYBILLNO = "createBillNo";

    public final static String RPF_PAID = "paid"; // �Ƿ��ǡ��ȴ���Ҹ��״�?�ж�������ѡ���ĸ�

    public final static String RPF_PAYMONEY = "payMoney"; // Ӧ���ܼ�

    public final static String RPF_BALANCE = "balance"; // �˻����?�?

    public final static String RPF_KATONGLIST = "katongList";

    public final static String RPF_OUTTRADENO = "outTradeNO";
    
    public final static String RPF_PREBILLNO = "preBillNO";

    public final static String RPF_LOGISTICSSTATUS = "logisticsStatus";

    public final static String RQF_KATONGID = "katongId";

    public final static String RPF_CHANNELSTATE = "channelState";

    public final static String RPF_PARTNERNAME = "partnerName"; // 4��

    public final static String RPF_SELLERNAME = "sellerName"; // ���׶Է�
    
    public final static String RPF_CHARGEINSTNAME = "chargeInstName";

    public final static String RPF_SELLER_NAME = "sellerName"; // �̼����?�?

    public final static String RPF_GOOD_TITLE = "goodTitle"; // ��Ʒ���?�?

    public final static String RPF_SELLER_ACCOUNT = "sellerAccount"; // �̼��ʺ�

    public final static String RPF_PARTNER_NAME = "partnerName"; // ��������

    public final static String RPF_TRADETYPE = "tradeType"; // ��������

    public final static String RPF_SUPPORTED_COUPON = "supportedByCoupon";

    public final static String RPF_COUPON_AMOUNT = "couponAmount";

    public final static String RPF_POINT = "pointInfo"; // �˻����?�?

    public final static String RPF_LOGISTICSID = "logisticsid"; // ����(�жϣ�

    public final static String RPF_BUYERADDRESS = "buyerAddress"; // �ջ���?

    public final static String RPF_LOGISTICSTYPE = "logisticsType"; // ���ͷ�ʽ

    public final static String RPF_LOGISTICSNAME = "logisticsName"; // ����˾

    public final static String RPF_LOGISTICSFEE = "logisticsFee"; // �˷�

    public final static String RPF_LOGISTICSPHONE = "logisticsPhone"; // jϵ�绰

    public final static String RPF_LOGISTICSMEMO = "logisticsMemo"; // ������Ϣ

    public final static String RPF_LOGISTICSNO = "logisticsNo"; // ������Ϣ

    public final static String RPF_LOGISTICSDATETIME = "status_time"; // ������Ϣ

    public final static String RPF_LOGISTICSDESC = "status_desc"; // ������Ϣ

    public final static String RPF_PEERPAY_ID = "peerPayId";

    public final static String RPF_PEERPAY_STATUS = "peerPayStatus";

    public final static String RPF_PEERPAY_STATUSMEMO = "peerPayStatusMemo";

    public final static String RPF_PEERPAY_ACCOUNT = "peerPayAccount";

    public final static String RPF_PEERPAY_NAME = "peerPayName";

    // ��ͨ��ֵ�ӿ�
    // �û�����ͨ��ֵ
    public final static String OP_PREKTDESPOSIT = "prektDeposit"; // �û�����ͨ��ֵ

    public final static String RPF_KTDEPOSITLIST = "ktDepositList"; // ����б�?rray

    public final static String RPF_KTBANKID = "ktBankId"; // ��ͨ����ID

    public final static String RPF_KTBANKNAME = "ktBankName"; // ��ͨ�������?�?

    public final static String RPF_KTSHORTCODE = "ktShortCode"; // ��ͨ��λβ��

    public final static String RPF_RSAPK = "rsaPK"; // RSA��Կ

    public final static String RPF_RSATS = "rsaTS"; // ʱ���?�?

    // �û�ѡ��һ��ͨ�������ֵ��֧�����룬�û�ȷ��
    public final static String OP_KTDEPOSIT = "ktDeposit"; // �û�ѡ��һ��ͨ

    public final static String RQF_KTBANKID = "ktBankId"; // ��ͨ����ID

    public final static String RQF_KTSHORTCODE = "ktShortCode"; // ��ͨ��λβ��

    public final static String RQF_DEPOSITAMOUNT = "depositAmount"; // ��ֵ���?�?

    public final static String RQF_PAYPASSWORD = "payPassword"; // ֧������

    // ���?��ֵ
    public final static String OP_DRAFTDEPOSIT = "draftDeposit"; // ��������

    public final static String RQF_DRAFTNO = "draftNO"; // ��Ʊ����

    public final static String RQF_DRAFTPASSWORD = "draftPassword"; // ��Ʊ����

    // �û�ѡ�����ֻ�ͻ����������?����?
    public final static String OP_PREREQUESTKT2 = "preRequestKT2"; // ��������

    public final static String RPF_BANKLIST = "list"; // �����б�

    public final static String RPF_BANKID = "bankId"; // ����ID

    public final static String RPF_BANKNAME = "bankName"; // �������?�?

    public final static String RPF_ACTIVEMSG = "activeMSG"; // ������Ϣ

    public final static String RPF_HASAUTHED = "hasAuthed"; // �Ƿ�����֤�û�

    public final static String RPF_REALNAME = "realName"; // �û���ʵ����

    public final static String RPF_IDCARDNO = "idCardNO"; // �û����֤����?�?

    public final static String RPF_CARDWITHDRAWAMOUNT = "cardWithdrawAmount";

    // �û�ѡ�����У���������ʵ��������֤���룬ȷ���ύ����
    public final static String OP_REQUESTTK = "requestKT"; // ��������

    public final static String RQF_BANKID = "bankId"; // ���д���

    public final static String RQF_REALNAME = "realName"; // ��ʵ����

    public final static String RQF_IDCARDNO = "idCardNO"; // ���֤����?�?

    public final static String RQF_CLIENTID = "clientID"; // �ͻ���ID

    // �û������ͨ
    public final static String OP_PREBINDINGKT = "preBindingKT"; // ��������

    public final static String OP_BINDINGKT = "bindingKT"; // ��������

    public final static String RQF_BANKCARDNO = "bankCardNO"; // ���п���

    public final static String OP_VIEWMSGDETAIL = "viewMsgDetail"; // ��������

    public final static String RPF_MSGID = "msgId"; // ��ϢID(INT

    public final static String RPF_MSGLEVEL = "msgLevel"; // ��Ϣ����

    public final static String RPF_MSGTOPIC = "msgTopic"; // ��Ϣ����

    public final static String RPF_MSGCONTENT = "msgContent"; // ��Ϣ����

    public final static String RPF_MSGTYPE = "msgType"; // ��Ϣ����

    // ����ȡֵ��plaiTtext��html
    public final static String RPF_MSGDATE = "msgDate"; // ��Ϣ����ʱ��

    public final static String RPF_MSGCOUNT = "msgCount"; // ����Ϣ����(INT

    public final static String OP_PREDRAWMONEY = "preDrawMoney"; // ��������

    public final static String OP_PREDRAWMONEYTWO = "preDrawMoney2"; // ��������

    public final static String RPF_ONCEAVAILABLEAMOUNT = "onceAvailableAmount"; // ���տ��������?�?

    public final static String RPF_AVAILABLEAMOUNT = "availableAmount"; // ���ο��������?�?

    public final static String RPF_BANKTABLELIST = "bankTableList";

    public final static String RPF_TYPE = "type";

    public final static String RPF_WITHDRAWCOUNT = "withdrawCount";

    public final static String OP_DRAWMONEY = "drawMoney"; // ��������

    // public final static String RQF_CLIENTID = "clientID"; //�ͻ���ID
    public final static String RQF_AMOUNT = "amount"; // ���ֽ��?�?

    public final static String RQF_SUPPORTEDCARD = "supportedCard"; // ���ֽ��?�?

    public final static String RQF_DESC = "desc";

    public final static String RQF_ID = "Id";

    // query contact
    public final static String OP_QUERYCONTACT = "queryContactLists";

    public final static String RPF_CONTACTLISTS = "contactLists";

    public final static String PRF_GROUPCOUNT = "groupCount";

    public final static String PRF_CONTACTACCOUNT = "account";

    public final static String PRF_CONTACTNAME = "name";

    public final static String PRF_CONTACTGROUPNAME = "groupName";

    public final static String PRF_CONTACTS = "contacts";

    public final static String PQF_CONTACTINFO = "contactInfo";

    public final static String OP_ADDCONTACT = "addContact";

    // EasyOwn Charge.
    public final static String OP_GETEASYOWNRATE = "getEasyownRate";

    public final static String RPF_RATE = "rate";

    public final static String OP_GETSUPPORTEDCHARGECARD = "getSupportedChargeCard";

    public final static String RPF_SUPPORTEDAMOUNT = "supportedAmount";

    public final static String OP_EASYOWNDEPOSIT = "easyownDeposit";

    public final static String RQF_EASYOWN_NO = "cardNo";

    public final static String RQF_EASYOWN_PASSWORD = "cardPass";

    public final static String RQF_EASYOWN_AMOUNT = "cardAmount";

    public final static String RPF_EASYOWN_DEPOSITID = "depositeId";

    public final static String OP_GETEASYOWNRESULT = "getEasyownResult";

    public final static String RQF_EASYOWN_DEPOSITID = "depositeId";

    // Netbank Charge.
    public final static String OP_GETSUPPORTEDBANK = "getSupportedBank";

    public final static String RPF_SUPPORTEDBANKLIST = "supportedBankList";

    public final static String OP_EBANKDEPOSIT = "ebankDeposit";

    public final static String RQF_NETBANK_BANKID = "bankId";

    public final static String RQF_NETBANK_DEPOSITAMOUNT = "depositAmount";

    public final static String RQF_NETBANK_OPERATORTYPE = "operatorType";

    public final static String RPF_NETBANK_BANKURL = "bankUrl";

    // modifyPassword
    public final static String OP_MODIFYPASSWORD = "modifyPassword";

    public final static String RQF_NEWPASSWORD = "newPassword";

    public final static String RQF_OLDPASSWORD = "oldPassword";

    public final static String RQF_PASSWORDTYPE = "passwordType";

    // �?���??�?
    public final static String RQF_IDCARDCONFIRM = "idCardConfirm";

    // ?????���???��????�??
    public final static String OP_SMSTRANS_GETSMSSWITCH = "getSMSSwitch";

    public final static String OP_SMSTRANS_SEDSMS = "sendSMSCode";

    public final static String RQF_SMSTRANS_CLIENTID = "clientID";

    public final static String RQF_SMSTRANS_LOGINID = "loginId";

    public final static String RQF_SMSTRANS_CHECKCODE = "checkCode";

    public final static String RQF_SMSTRANS_SESSIONID = "sessionId";

    public final static String RPF_SMSTRANS_SENDSMS_MEMO = "memo";

    public final static String RPF_SMSTRANS_SENDSMS_RESTTIME = "restTime";

    public final static String RPF_SMSTRANS_SENDSMS_SMSSWITCH = "smsSwitch";

    public final static String OP_SMSTRANS_PRESETPSW = "verifySMSCode";

    // submitFeedbackContent
    public final static String OP_FEEDBACK_OPERATIONTYPE = "addProposal";

    public final static String RQF_FEEDBACK_PROPOSALTYPE = "proposalType";

    public final static String RQF_FEEDBACK_USERNAME = "userName";

    public final static String RQF_FEEDBACK_MOBILENO = "mobileNo";

    public final static String RQF_FEEDBACK_SUGGESTION = "suggestion";

    public final static String RQF_FEEDBACK_PRODUCTID = "productID";

    public final static String RQF_FEEDBACK_PRODUCTVERSION = "productVersion";

    // get password
    public final static String OP_SMSCHECKCODE = "preSendSMSCheckCode";

    public final static String OP_SENDSMSCHECKCODE = "sendSMSCheckCode";

    public final static String OP_PRERESET_LOGIN_PASSWORD = "preResetLoginPassword";

    public final static String OP_PRERESET_TRADE_PASSWORD = "preResetTradePassword";

    public final static String OP_RESETLOGINPASSWORD = "resetLoginPassword";

    public final static String OP_RESETTRADEPASSWORD = "resetTradePassword";

    public final static String RQF_PASSWORD = "password";

    public final static String RPF_RESETKEY = "resetKey";

    public final static String RPF_AABATCHKEY = "aaBatchKey";

    public final static String RPF_AAACCONT = "account";

    public final static String RPF_AANAME = "name";

    public final static String RPF_AATAG = "tag";

    public final static String RPF_AAFLAG = "flag";

    public final static String RPF_AAAMOUNT = "amount";

    public final static String RPF_DETAILCONTENT = "detailContent";

    public final static String RPF_TOTALAMOUNT = "totalAmount";

    public final static String OP_AABATCHGATHRING = "aaBatchGathring";

    public final static String RQF_ACCOUNTTYPE = "accountType";

    public final static String OP_QUERYPAYSETTINGS = "queryPaySettings";

    public final static String OP_MODIFYPAYSETTINGS = "modifyPaySettings";

    public static final String ENCODING = "UTF-8";

    public static final String HTTP_METHOD = "POST";
    
    public static final String OP_VERIFYMPASSWDCHECKCODE = "verifyMPasswdCheckCode";

    // ?��????????��?
    public final static String RPF_BARCODE_PAY_TOKEN = "barcodePayToken";

    public final static String RPF_BARCODE_QRCODE_PREFIX = "qrcodePrefix";

    public final static String OP_GET_BARCODE = "getBarcode";

    public final static String RPF_BARCODE_KEY = "barcodeKey";

    public final static String OP_GET_BARCODE_LICENSE = "getBarcodeByLicense";

    public final static String RQF_SAFEPAY_IS_ACTIVE = "isActivation";

    public final static String OP_CHECK_SAFEPAY_INIT = "spAccountState";

    public final static String RQF_SAFEPAY_TOKEN = "spToken";

    public final static String RPF_SAFEPAY_INITIALIZED = "spActive";

    public final static String OP_CANCEL_BARCODE_TRADE = "closeBarcodeTrade";

    public final static String RPF_BARCODE_PARTER_ACCOUNT = "otherAccount";

    public final static String RPF_BARCODE_PARTER_NAME = "otherName";

    public final static String RPF_BARCODE_PARTER_ADDRESS = "otherAddress";

    public final static String RPF_BARCODE_PARTER_PHONE = "otherPhone";

    public final static String RPF_BARCODE_NEED_PAY_PASSWORD = "needPaymentPassword";

    public final static String RPF_BARCODE_CUR_ACCOUNT = "returnAccount";

    // ??��????��???
    public final static String OP_CREATE_QRRCODEPAY_TRADE = "paiPaiPayCreate";

    public final static String RQF_QRRCODEPAY_KEY = "qrCode";

    public final static String RPF_QRRCODEPAY_FLAG = "paiPaiFlag";

    public final static String RPF_QRRCODEPAY_NEXT_ACTION = "forwardOtherOperation";

    public final static String OP_GET_QRRCODEPAY_QRCODEINFO = "paiPaiGetInfo";

    public final static String RQF_QRRCODEPAY_ACCOUNT = "account";

    public final static String RPF_QRRCODEPAY_DONATE_ACCOUNT = "accountName";

    public final static String RPF_QRRCODEPAY_DONATE_PROJECT = "qrCodeName";

    public final static String RPF_QRRCODEPAY_DONATE_MONEY = "qrCodeMoney";

    public final static String OP_CREATE_QRRCODEPAY_DONATE = "paiPaiPayByLicense";

    // RQF_LOGIN_ACCOUNT
    public final static String RQF_QRRCODEPAY_EXTERN_TOKEN = "externToken";

    public final static String RQF_QRRCODEPAY_DONATE_KEY = "sellerQrcode";

    public final static String RQF_QRRCODEPAY_DONATE_AMOUNT = "payAmount";

    public final static String RQF_QRRCODEPAY_DONATE_SIGN = "signature";

    // ?��??��?????��?
    public final static String OP_CREATE_BARCODEGATHER_TRADE = "barcodeGathering";

    public final static String RQF_BARCODEGATHER_KEY = "buyerBarcode";

    public final static String RQF_BARCODEGATHER_LOGINFLAG = "loginflag";

    public final static String RQF_BARCODEGATHER_ACTIVEFLAG = "isActivation";

    public final static String RQF_BARCODEGATHER_ACCOUNT = "gatheringAccount";

    public final static String RQF_BARCODEGATHER_AMOUNT = "gatheringAmount";

    public final static String RQF_BARCODEGATHER_MEMO = "gatheringMemo";

    public final static String RPF_BARCODEGATHER_USERID = "returnAccount";

    // �???��?�?��信�?
    public final static String OP_LOCATE_UPLOAD_INFO = "uploadLocation";

    public final static String RQF_LOCATE_CELLID_CURENT = "cellid";

    public final static String RQF_LOCATE_CELLID_NEIGHBOR1 = "cellid1";

    public final static String RQF_LOCATE_CELLID_NEIGHBOR2 = "cellid2";

    public final static String RQF_LOCATE_GPS_LONGITUDE = "longitude";

    public final static String RQF_LOCATE_GPS_LATITUDE = "latitude";

    public final static String RQF_LOCATE_DEVIATION_RANGE = "errorRange";

    public final static String RQF_LOCATE_ACTION_BUSINESSID = "businessID";

    public static final int LOCATE_BUSINESS_LOGIN = 100;

    public static final int LOCATE_BUSINESS_BARCODEPAY = 201;

    public static final int LOCATE_BUSINESS_BARCODEGET = 202;

    public static final int LOCATE_BUSINESS_PAIPAI = 203;

    // taobao check code
    public final static String RPF_TAOBAO_CHECKCODEID = "checkCodeId";

    public final static String RQF_TAOBAO_CHECKCODE = "checkCode";

    public final static String RPF_TAOBAO_CHECKCODEURL = "checkCodeUrl";

    public final static String RPF_ALIPAY_CHECKCODEURL = "loginCheckCodeUrl";

    public final static String RPF_ALIPAY_CHECKCODEIMG = "loginCheckCodeImg";

    public final static String RQF_ALIPAY_CHECKCODE = "loginCheckCode";

    // ?��?�????
    public final static String RPF_SHOWMODULE = "showModule";

    public final static String RPF_SHOWMODULE_KEY_XSHSH = "xshsh";

    public final static String RPF_SHOWMODULE_KEY_AKCP = "cp";

    public final static String RPF_SHOWMODULE_VALUE_Y = "Y";

    public final static String RPF_SHOWMODULE_VALUE_N = "N";

    public static String MobileCompany = "";// ??��??????�?�?

    // query status
    public static final String STATUS_ALL = "all";

    public static final String STATUS_PROCESSING = "processing";

    public static final String STATUS_TRADESUC = "tradeSuc";

    public static final String STATUS_TRADEFAIL = "tradeFail";

    public static final String STATUS_WAITPAY = "waitPay";

    public static final String STATUS_CONFIRMGOODS = "confirmGoods";

    public static final int REQUEST_CODE = 1;

    public static final int REQUEST_CODE_TWO = 2;// �???��?�?��activity�??�?��请�?

    public static final int REQUEST_CODE_THREE = 3;

    public static final int REQUEST_CODE_QRCODE = 4;

    public static final int LEPHONE_PICK_CONTACT = 4;// �?hone?��???���?

    public static final int REQUEST_LOGIN_BACK = 5;

    public static final int REQUEST_PHONE_BINDDING = 6;

    public static final int REQUEST_GET_ACCOUNT = 7;

    public static final int REQUEST_LEPHONE_CONTACT = 8;

    public static final int REQUEST_CONTACT = 9;

    public static final int START_PAYMENT_CITY = 10;

    public static int PAYMENT_CITY_BACK = 11;
    
    public static final int REQUEST_WEBAPP_AUTH = 12;
    
    public static final int REQUEST_WAPLOGIN = 16;
    
    public static final int REQUEST_PATTERNSETTING_BACK = 15;
    
    public static final int START_LIFEPAY_HISTORY = 16;
    
    public static final int SETTING_TIME_CHECKBOX_REQUEST_LOGIN = 17;  
    
    public static final int SETTING_TIME_CHECKBOX_REQUEST_BINDING_PHONE = 18; 
    
    public static final int LIFE_PAY_HISTORY_REQUEST_LOGIN = 19; 
    
    public static final int REQUEST_GETSMS = 20;

    public static final String CASH_REGISTER_STATUS = "cashRegister";

    public static final int DEAL_STATUS = 0; // 交�??��?

    public static final int WEBVIEW_STATUS = 1; // 娱�?�?��

    public static final int PUC_STATUS = 2; // ???�??缴费

    public static final int TAOBAO_ORDER = 3;

    public static final int CONFIRM_GOODS = 4;// �???�货

    public static final String MODIFY_STYLE_LOGIN = "login";

    public static final String MODIFY_STYLE_TRADE = "trade";

    public static final String FILE_PATH = "/data/data/";

    public static final String BindingTaobao = "taobao";

    public static final String BindingAlipay = "alipay";

    /* add by renxl on 2010-07-14 */
    // URL
    public static final String PUBLIC_PAY_URL = "public_pay_url";
    
    //从哪个页面到历史记录页面的标记。
    public static final String JUMP_HISTORY_FROM = "jump_history_from";
    public static final String JUMP_HISTORY_FROM_HALL = "jump_history_from_hall";//从九宫格进入历史账号页面
    public static final String JUMP_HISTORY_FROM_INPUT = "jump_history_from_input";//从账单填写页面格进入历史
    
    public static final String LIFEPAY_HISTORY_TYPE = "lifepay_history_type";
    public static final String LIFEPAY_HISTORYINFO_KEY = "lifepay_historyinfo_key";
    

    // public static final String URL_WATER_RATE =
    // "puc/webMC.htm?charge_type=water"; //水费
    // public static final String URL_POWER_RATE =
    // "puc/webMC.htm?charge_type=electric"; //?�费
    // public static final String URL_PHONE_RATE =
    // "puc/webMC.htm?charge_type=commun"; //?��?
    // public static final String URL_GAS_RATE =
    // "puc/webMC.htm?charge_type=gas"; //?��?
    // public static final String URL_MOBILE_RATE =
    // "puc/webMC.htm?charge_type=mobile"; //???

    public static final String URL_WATER_RATE = "water"; // 水费

    public static final String URL_POWER_RATE = "electric"; // ?�费

    public static final String URL_GAS_RATE = "gas"; // ?��?

    public static final String URL_COMMUN_RATE = "commun";

    public static JSONObject WATER_JSON = null;

    public static JSONObject ELECTRIC_JSON = null;

    public static JSONObject GAS_JSON = null;

    public static JSONObject COMMUN_JSON = null;
    
    public static boolean isCommendCouponShowed = false;

    // trade records cache flag
    public static boolean isTradeListRefresh = true;

    public static String tradeListDir = "tradeRecord";

    public static String recordFile = "traderecord";

    // trade remind cache flag
    public static boolean isMsgRemindRefresh = true;

    public static String tradeRemindDir = "tradeRemind";

    public static String remindFile = "remindmessage";

    // account info cache flag
    public static boolean isAccountInfoRefresh = true;

    public static String accountDir = "accountInfo";

    public static String accountFile = "accountinfo";

    public static UserInfo mCurUserInfo = null;

    public static String STR_BARCODESWITCH = "true"; // ?��????�??

    public static String STR_BARCODETOKEN = ""; // ?��????�??token

    public static String STR_QRCODE_PREFIX = "[Alipay]:"; // ?��????�?��???�?

    public static final String STR_EXTERNTOKEN = "extern_token";// sessionID

    public static final String STR_NEWVERSION = "existNewVersion";// ??��???�?

    public static String OP_RETURNURL = "return_url";

    public static final int REQUEST_CODE_LOGIN = 10;

    public static final boolean isUseSafePay = true;

    public static final String RQF_USERSTATUS = "userStatus";

    public static final String RQF_PAYTYPE = "payType";

    public static final String RQF_PAYQUOTA = "payQuota";

    public static final String RQF_PAYTYPEMEMO = "payTypeMemo";

    // public static final String RPF_OPENEDNO = "openedNo";

    // 代�??��?�??
    // 代�??��?
    public static final String OP_AGENTPAY_APPLY = "applyPeerPay";

    public static final String RQF_BIZNO = "bizNo";

    public static final String RQF_BIZTYPE = "bizType";

    public static final String RQF_AGENT_ACCOUNT = "peerPayAccount";

    public static final String RQF_AGENT_MEMO = "message";

    public static final String RQF_IS_ADDCONTACT = "isAddContact";

    public static final String RPF_AGENT_URL = "peerPayUrl";

    public static final String RPF_AGENT_PAYID = "peerPayApplyId";

    // 代�????
    public static final String OP_AGENTPAY_CANCEL = "cancelPeerPay";

    public static final String RQF_PEER_PAYID = "peerPayId";

    // 代�?�??
    public static final String OP_QUERYAGENTDETAIL = "peerPayDetail";

    public static final String RPF_BUYER_NAME = "buyerName";

    public static final String RPF_BUYER_ACCOUNT = "buyerLoginId";

    public static final String RPF_APPLY_MESSAGE = "applyMessage";

    // ???�??缴费�??
    // 信�?�??
    public static final String OP_GETPUCINPUTINFO = "getPucInputInfo";

    public static final String RQF_CHARGETYPE = "chargeType";

    public static final String RQF_CHARGEINPUTTYPE = "chargeInputType";

    public static final String RQF_CITY = "city";

    public static final String RPF_HISTORYVALUE = "historyValue";

    public static final String RPF_CITIES = "cities";

    public static final String RPF_CONTENT = "content";

    public static final String TYPE_WATER = "water";

    public static final String TYPE_ELEC = "electricity";

    public static final String TYPE_GAS = "gas";

    public static final String TYPE_BROADBAND = "broadband";

    public static final String TYPE_PHONE = "phone";

    public static final String AGREEMENT_URL = "agreementUrl";
    
    //获取历史缴费账号
    public static final String OP_GETHISTORYBILLS = "getHistoryBills";
    

    // �?????
    public static final String OP_GETCHARGEBILLS = "getChargeBills";

    public static final String RQF_INPUTINFO = "inputInfo";
    
    public static final String RQF_REMINDSTATUS = "remindStatus";
    
    public static final String RQF_REMINDDATE = "remindDate";    

    // �??�??
    public static final String OP_CONFIRMBILLINFO = "confirmBillInfo";

    public static final String RPF_BILLID = "billId";
    
    public static final String RPF_AMOUNT = "amount";

    public static final String RPF_INFO = "info";

    // ??��缴费�?��??
    public static final String OP_CREATEBILLNO = "createBillNo";

    public static final String RPF_BILLNO = "billNo";

    // ??��缴费??????�???��?�?
    public static final String TIP = "tip";

    public static final String COMPANIES = "companies";
    
    public static final String SYSTEMDATE = "systemDate";

    public static final String CHARGE_MODEL = "chargeModel";

    public static final String DEFAULT = "default";

    public static final String DEFAULTSELECTED = "defaultselected";

    public static final String INPUTINFOS = "inputInfos";

    public static final String INPUTINFOS1 = "inputInfos1";
    
    public static final String INPUTINFOSCONFIRM = "inputInfosConfirm";

    public static final String LABEL = "label";

    public static final String FIELD = "field";

    public static final String FIELD_TYPE = "fieldType";

    public static final String BILL_KEY = "billKey";

    public static final String RULES = "rules";

    public static final String REGEX = "regex";

    public static final String ERROR = "error";

    public static final String CHECK = "check";

    public static final String ENABLE = "enable";

    public static final String NAME = "name";
    
    public static final String SHORTNAME = "shortName";

    public static final String VALUE = "value";

    public static final String OPTIONS = "options";

    public static final String COMPANY = "company";
    
    public static final String CHARGECOMPANYSNAME = "chargeCompanySname";

    public static final String FIELD_NAME = "fieldname";

    public static final String SELECTED = "selected";

    public static final String CANSELECT = "canSelected";

    public static final String LISTS = "lists";

    public static final String CHARGEUSERINFO = "chargeUserInfo";

    public static final String CHARGE_REGION = "chargeRegion";

    public static final String CHARGE_UNIT = "chargeUnit";

    public static final String CHARGE_AMOUNT = "chargeAmount";

    public static final String BILL_INFO = "billInfo";
    
    public static final String CHARGE_MODE = "chargeQueryMode";
    
    
    
    
    
    //缴费记录信息    
    public static final String BILL_HISTORYRECORDS = "historyRecords";
    
    public static final String BILL_CHARGEINSTNAME = "chargeInstName";
    
    public static final String BILL_CITY = "city";
    
    public static final String BILL_OWNERNAME = "ownerName";
    
    public static final String BILL_CHARGECOMPANYSNAME = "chargeCompanySname";
    
    
    //缴费提醒设置
    public static final String BILL_ISREMINDED = "isReminded";
    
    public static final String BILL_REMINDDATE = "remindDate";
    
    public static final String BILL_ISBOUND = "isBound";
    
    
    

    // web app
    public static final String RES_PARAM_CALLBACKURL = "call_back_url";

    public static final String RES_PARAM_FAILURL = "fail_url";

    public static final String RES_PARAM_PARTNER = "partner_id";

    public static final String RES_PARAM_TOKEN = "ordertoken";

    public static final String RES_PARAM_TIMESTAMP = "timestamp";

    public static final String RES_PARAM_CHANNEL = "pay_channel_id";

    public static final String RPF_CACHEPATH = "cachepath";

    public static final String RPF_UPDATESTATUS = "needupdate";

    public static final String SERVER_VERSION = "serverversion";

    public static final String NOW_VERSION = "nowversion";

    public static final String INSTALLSAFEPAY = "installSafePay";

    public static String ALIPAY_INFO = "alipaymobile0225";

    public static final int SHARE_APP = 1;// �???�享

    public static final int SHARE_PAY = 2;// ?�享代�?

    public static final String OP_QUERY_ADVERT = "queryCmsAD";

    public static final String RPF_CMS_ADS = "cmsAds";
    public static final String RPF_CMS_TYPE = "adType";
    public static final String RPF_CMS_URL = "adUrl";
    public static final String RPF_CMS_VALID = "isValid";
    // ??????
    public static final String OP_LOGIN_AUTH = "loginAuth";

    public static final String RQF_PARTNER_ID = "partnerId";

    public static final String RQF_AUTH_RESULT = "authResult";
    
    public static final String RQF_AUTH_LEVEL = "level";

    public static final String RQF_LOGIN_TOKEN = "login_token";

    public static final String AUTHO_YES = "Y";

    public static final String AUTHO_NO = "N";

    // ?????��订�?
    public static final String OP_CREATE_EXORDER = "createExtTrade";

    public static final String RQF_ORDER_TOKEN = "orderToken";

    // public static final String RQF_TRADE_NO = "tradeNo";
    public static final String RQF_EXTERN_TOKEN = "externToken";

    // ?��?�????
    public static boolean isShowCreditcard = true;

    public static boolean isShowQRCodeBar = true;

    public static boolean isShowOKLotto = true;

    // ?��??��????
    public static String BARCODE_CAPTURE_HINT = "captureHint";

    public static String BARCODE_CAPTURE_TYPE = "captureCodeType";

    public static String BARCODE_CAPTURE_TYPE_PUBCHARGE = "1"; // "barcode";

    public static String BARCODE_CAPTURE_TYPE_QRCODE = "2"; // "qrcode";

    public static final String RPF_PUSH_TRADE_ACTION = "action";

    public final static String RPF_PUSH_TRADE_ACT_PAY = "pay";

    public final static String RPF_PUSH_TRADE_ACT_FINISH = "finish";

    public final static String RPF_PUSH_TRADE_ACT_VERIFY = "verify";
    
    public final static String RPF_PUSH_TRADE_ACT_CANCEL = "cancel";
	
	
	public final static String RPF_PUSH_TRADE_ACT_FASTPAY = "fastpay";
    public final static String RPF_PUSH_TRADE_ACT_GETGOODSLIST = "getGoodsList";
    public final static String RPF_PUSH_TRADE_ACT_SOUNDWAVEPAYPUSH = "soundWavePayPush";
    

    // p2p交�?�?????
    public static final String OP_P2P_PAY_RESULT = "p2pPaySuccessPush";

    public static final String RQF_P2P_PAY_ACTION = "action";

    public static final String RQF_P2P_PAY_QRCODE = "qrCode";

    public static final String RPF_P2P_PAY_FORWARD = "forwardView";

    public static final String GETMONEY_TRADE_TYPE_P2P = "createp2p";

    public static final String GETMONEY_TRADE_TYPE_BARCODE = "barcodeP2P";

    public static final String ACCOUNT_SEARCH_FROM_TYPE = "accountfrom";

    public static final String ACCOUNT_SEARCH_IS_CONTACT = "iscontact";

    public static final String PAY_TRADE_FROM_TYPE = "payfrom";

    public static final String PAY_TRADE_FROM_ACCOUNT = "accountSearch";

    public static final String PAY_TRADE_FROM_PAIPAI = "paipai";

    public static final String PAY_TRADE_RECORD_CONTACT = "recordContact";

    public static final long[] DEFAULT_VIBRATE_PATTERN = { 0, 50, 50, 50 };

    public static final int mHistoryContactCount = 5;

    public static boolean mWapSupport = true;

    public static final String mSafepayVersion = "2.3.1";

    public static int LOG_LEVEL = 4;

    public static int LOG_LEVEL_ERROR = 1;

    public static int LOG_LEVEL_WARNING = 2;

    public static int LOG_LEVEL_DEBUG = 3;

    public static int LOG_LEVEL_INFO = 4;

    public static int LOG_LEVEL_V = 5;

    // �??????��?
    // bizType
    public static final String SAFE_PAY_PAY_TYPE = "trade"; // �?????

    public static final String SAFE_PAY_CONFIRM_GOODS_TYPE = "confirm_goods"; // �???�货

    public static final String SAFE_PAY_ACTIVE_BIZ_TYPE = "prepay_initial"; // �?????�?��

    // bizSubType
    // �??????��?mAlixPay.ppay(strOrderInfo)__MobileSecurePayer
    public static final String SAFE_PAY_ACTIVE = "barcode_prepay"; // �?????�?��

    public static final String SAFE_PAY_PAY = "barcode_Pay"; // ?��????

    // �??????��?mAlixPay.prePay(strOrderInfo)__MobileSecureCheck
    public static final String SAFE_PAY_CHECK = "barcode_paycheck"; // ?��?????��?tid

    public static final String SAFE_GET_AUTHLIST = "get_sp_authlist"; // ?��?�?????认�?�??list

    // action-orderInfo
    public static final String SAFE_ORDER_GET_AUTHLIST = "getAccountList";

    // result
    public static final String SAFE_PAY_RESULT_TOKEN = "token";

    public static final String SAFE_PAY_RESULT_STATUS = "resultStatus";

    public static final String SAFE_PAY_RESULT_MEMO = "memo";

    public static final String SAFE_PAY_RESULT_ARRAY = "result";

    // �????????认�?�????��
    public static final String SAFE_PAY_RESULT_ACCOUNTDATA = "accountData";

    public static final String SAFE_PAY_RESULT_USERLOGINID = "userloginid";

    public static final String SAFE_PAY_RESULT_USERID = "userid";

    // reportActive
    public final static String OP_REPORT_ACTIVE = "reportActive";

    public final static String CHANNELS = "channels";
    
    public final static String PUSH_SWITCH = "pushSwitch";
    public final static String PUSH_RECORD = "pushRecord";
    public final static String LAUNCH_TIME = "launchtime";
    public final static String DATA_CONNECTIVITY_FLAG = "dataConnectivity";

    public static final String OP_SEND_CRASHINFO = "addProposal";

    public final static String ROOTED = "rooted";
    
    public final static String PKG_OPDA = "pkgOpda";
    public final static String PKG_QIHOO = "pkgQihoo";
    public final static String PKG_TENCENT = "pkgQQ";
    public final static String AWID = "awid";

    public static final String PAYMENT_CITY_LIST = "paymentCityList";

    public static final String CLICK_CITY = "clickCity";

    public static final String ATTENTION = "attention";//缴费公司支持时间段提示

    public static final String STATUS = "status";
    
    public static final String BILLKEY = "billKey";
    public static final String CHARGEINST = "chargeInst";
    public static final String CITY = "city";
    public static final String FINEAMOUNT = "fineAmount";
    public static final String GMTCREATE = "gmtCreate";
    public static final String GMTMODIFIED = "gmtModified";
    public static final String ORIGINALBILLDATE = "originalBillDate";
    public static final String OWNERNAME = "ownerName";
    public static final String PROVINCE = "province";
    public static final String SUBBIZTYPE = "subBizType";

    // �??�??�????D
    public static String OPERATION_UID;

    // ?��?�?????系�?�??页�?
    public static String BARCODE_JUMP_TAB = "";

    public static String BARCODE_JUMP_SYSTEMMSG = "msg";

    // �??认�?
    public static String ISCERTIFIED = "isCertified";

    public static String LOGINACCOUNT_CACHE = "loginAccountCache";

    public static String IP_CITY = "ipCity";

    //信用卡还款请求字段
    public static final String RQF_CCR_CREATE_CREDIT_CARD_TRADE_FOR_NEW = "createCreditCardTradeForNew";
    public static final String RQF_CCR_CREATE_CREDIT_CARD_TRADE_FOR_OLD = "createCreditCardTradeForOld";
    public static final String RQF_CCR_GETBANKS_TYPE = "getSavedCreditCardsAndBanks";

    public static final String RQF_CCR_CCNOMBER = "creditCardNumber";
    public static final String RQF_CCR_DEPOSITAMOUNT = "depositAmount";
    public static final String RQF_CCR_HOLDERNAME = "holderName";
    public static final String RQF_CCR_BANKMARK = "bankMark";
    public static final String RQF_CCR_GET_SAVED_CREDIT_CARDS = "getSavedCreditCards";

    public static final String RQF_CCR_QUERYTYPE = "queryType";
    public static final String RQF_CCR_QUERYSAVED = "savedCreditCardList";
    public static final String RQF_CCR_QUERYBANK = "bankList";

    public static final String RPF_CCR_HASSAVED = "hasSavedCreditCards";
    public static final String RPF_CCR_SAVEDBANK = "savedCreditCards";
    public static final String RPF_CCR_SUPPORTBANK = "supportBanks";
    public static final String RPF_CCR_CARD_INDEX_NUMBER = "cardIndexNumber";
    public static final String PARAM_KEY = "PARAM_KEY";
    public static final String RPF_CCR_HOLDERNAME = "holderName";
    public static final String RPF_CCR_BANKMAKE = "bankMark";
    public static final String RPF_CCR_BANKNAME = "bankName";
    public static final String RPF_CCR_ARRIVEDATETIME = "arriveDatetime";
    public static final String RPF_CCR_PHONENUMBER = "phoneNumber";
    public static final String RPF_CCR_AMOUNTLIMIT = "amountLimit";
    public static final String RPF_CCR_REMINDDATE = "remindDate";
    public static final String RPF_CCR_CARDBINLIST = "cardBinList";
    public static final String RPF_CCR_SMSTEMPLATE = "smsTemplates";
    public static final String RPF_CCR_SMSPHONENUMBER = "smsPhoneNumber";
    public static final String RPF_CCR_SMSKEYWORD = "smsKeyword";
    public static final String RPF_CCR_TRADENUMBER = "tradeNumber";
    public static final String RQF_CCR_CARDINDEXTNUMBER = "cardIndexNumber";
    public static final String RPF_CCR_IS_NEW_CARD = "isNewCard";
    public static final String RQF_CCR_ALERT_DATE = "alertDate";
    public static final String RQF_CCR_SET_CREDIT_CARD_DEPOSIT_ALERT = "setCreditCardDepositAlert";

    //浏览器启动打通字段
    //saId=10000007&clientVersion=1.0.0.0&qrcode=10000000
    public static final String EXPLEROR = "explorer";
    public static final String SAID = "saId";
    public static final String CLIENTVERSION = "clientVersion";
    public static final String QRCODE = "qrcode";
    
	//push消息点击上报
    public static final String PUSH_MISSION_ID = "pushMissionId";
    public static final String PUSH_MSG_ID = "perMsgId";
    public static final String PUSH_PUB_MSG_ID  = "pubMsgId";
    
    public final static String PUSH_REPORT_USERID = "userId";
    public final static String PUSH_REPORT_CLIENTID = "clientId";
    public final static String PUSH_REPORT_MISSIONID = "missionId";
    public final static String PUSH_REPORT_MSGID = "msgId";
    public final static String PUSH_REPORT_PUBMSGID = "pubMsgId";
    
    //push打通客户端字段
    public static final String PUSH_DATA = "pushData";
    public static final String APP_DATA = "appData";
    public static final String PUSHMSGTYPE = "type";
    public static final String PUSHAPPS = "pushapps";
    public static final String APPID = "saId";
    public static final String ACTIVITYNAME = "activityName";
    public static final String ISREBOOT = "isReboot";
    public static final String PARAMS = "params";
    public static final String PACKAGENAME = "packageName";
    public static final String CARDNO = "cardNo";
    public static final String CARDNOTYPE = "cardNoType";
    public static final String PUSH_CCR_USERID = "uId";

    public static final String RPF_CCR_IS_NEED_ALERT = "isNeedAlert";

    public static boolean mClientIsRunning = false;
    public static boolean mSafePayIsRunning = false;

    //信用卡
    public static final String RPF_STAT = "stat";
    public static final String OK = "ok";
    public static final String FAILURE = "failure";
    public static final String KEY = "key";
    public static final String RQF_CCR_CARDNUMBERTYPE = "cardNumberType";
    //信用卡连接CCDC失败
    public static final int CCDC_FAILURE = -100;
	
	public static final String PackageInfo = "com.eg.android.AlipayGphone";

    public static final String TRANSFERRECORDLIST = "transferRecordList";

    //手机号绑定关系
    public static String BINDINGTYPE = "bindingType";
    public static String NOT_BINDING = "NOT_BINDING";
    public static String MULTI_BINDING = "MULTI_BINDING";
    public static String SINGLE_BINDING = "SINGLE_BINDING";

    //账户状态
    public static String ENABLESTATUS = "enableStatus";
    //用户id
    public static String USERID = "userId";
    public static String ISCHARGEFREE = "isChargeFree";
    public static String RECEIVERMOBILE = "receiverMobile";
    public static String RECEIVERNAME = "receiverName";
    public static String TRANSFERAMOUNT = "transferAmount";
    public static String BANKSHORTNAME = "bankShortName";
    public static String TRANSFERSPEEDNAME = "transferSpeedName";
    public static String NETWORK_MEMO = "network_memo";

    public static String RECVMOBILE = "recvMobile";
    public static String RECVNAME = "recvName";
    public static String RECVACCOUNT = "recvAccount";

    public static String TRADE = "TRADE";
    public static String TRANSFER = "TRANSFER";
    public static String RQ_PAY = "pay_request";
    public static String STATUS_INIT = "INIT";
    public static String STATUS_PAID = "PAID";
    public static String STATUS_CONFIRM = "CONFIRM";
    public static String STATUS_SUCCESS = "SUCCESS";
    public static String STATUS_TIME_OUT = "TIME_OUT";
    public static String STATUS_PAY_FAILURE = "PAY_FAILURE";
    public static String STATUS_UN_TRADED = "UN_TRADED";
    public static String STATUS_TRADE_FAIL = "TRADE_FAIL";
    public static String STATUS_WS = "WS";
    public static String STATUS_WF = "WF";

	public static String IS_OWNER = "isOwner";

    public static final String CCR_SUPPORT_BANK_LIST = "CCR_SUPPORT_BANK_LIST";
    public static final String CCR_SUPPORT_ONE_BANK_INFO = "CCR_SUPPORT_ONE_BANK_INFO";
    //近场发现LBS
    //public static final String  LBS_OPERATIONTYPE                        = "operationType";
    public static final String LBS_CLIENTID = "clientID";
    public static final String LBS_BUSINESSTYPE = "businessType";
    public static final String LBS_LONGITUDE = "longitude";
    public static final String LBS_LATITUDE = "latitude";
    public static final String LBS_CELLID = "cellId";
    public static final String LBS_PARAMS = "params";
    public static final String LBS_RADIUS = "radius";
	public static final String IS_AUTHORIZE_SMS_QUERY = "IS_AUTHORIZE_SMS_QUERY";

	public static final String TRADE_TYPE = "TRADE_TYPE";//交易记录类型
	//提现
	public static final String BANK_TABLE_LIST = "bankTableList";
	public static final String BANK_ID = "bankId";
	public static final String BANK_ICON = "bankIcon";
	public static final String BANK_NAME = "bankName";
	public static final String USER_NAME = "userName";
	public static final String CARD_WITHDRAW_AMOUNT = "cardWithdrawAmount";
	public static final String DESC = "desc";
	public static final String TYPE = "type";
	public static final String AVAILABLE_AMOUNT = "availableAmount";
	public static final String RSA_PK = "rsaPK";
	public static final String RSA_TS = "rsaTS";
	public static final String ARRIVE_DATE = "arriveDate";
	public static final String NEW_DESC = "newDesc";
	public static final String MEMO = "memo";
	public static final String BANK_MARK = "bankMark";
	//卡管理
	public static final String CARD_LIST = "cardList";
	public static final String CARD_INDEX_NUMBER = "cardIndexNumber";
	public static final String HOLDER_NAME = "holderName";
	public static final String CREDIT_CARD_TYPE = "creditCardType";
	public static final String CARD_NUMBER = "cardNumber";
	public static final String DEBIT_CARD_TYPE = "debitCardType";
	public static final String BIZ_TYPE = "bizType";
	public static final String APPLY_TIME = "applyTime";
	public static final String BILL_TYPE = "billType";
	public static final String WAIT_PAY_BILL_COUNT = "waitPayBillCount";
	public static final String LIFE_PAY_BILL_COUNT = "pucBillCount";
	public static final String PASSWRODTYPE = "passwrodType";
	
	public static final int AUTOLOGIN_UNKNOW = 2;
	public static final int AUTOLOGIN_YES = 1;
	public static final int AUTOLOGIN_NO = 0;

	public static final String EXIT = "EXIT";

	//快捷签约
	public static final String CARDTYPE = "cardType";
	public static final String CARDTYPE_CC = "CC";
	public static final String CARDTYPE_SCC = "SCC";
	public static final String CARDTYPE_DC = "DC";
	public static final String EXPIREDATE = "expiredDate";
	public static final String CVV2 = "cvv2";
	public static final String HOLDERNAME = "holderName"; //如为实名认证用户，由支付宝账号带入
	public static final String MOBILE = "mobile";
	public static final String CERTNO = "certNo";
	public static final String CERTTYPE = "certType";
	public static final String SIGNID = "signId";
	public static final String VERIFYCODE = "verifyCode";
	public static final String INSTID = "instId";
	public static final String RESENDVERIFYCODE = "resendVerifyCode";
	public static final String PAYPASSWORD = "payPassword";
	public static final String USERPHONE = "userPhone";
	public static final String UPDATETYPE = "updateType";
	public static final String ACTIONTYPES = "actionTypes";
	public static final String BANKPHONE = "bankPhone";
	public static final String ACTIONTYPE = "actionType";
	public static final String SOURCECHANNELS = "sourceChannels";
	public static final String BINDINGMOBILE = "bindingMobile";
	public static final String CARDID = "cardId";
	public static final String CARDNOTYPE_DEBITCARDNO = "0";
	public static final String CARDNOTYPE_CREDITCARDCACHEKEY = "1";
	public static final String CARDNOTYPE_CREDITCARDINDEXKEY = "2";
	
	public static final int ACTION_NONE = -1;
	public static final int ACTION_WITHDRAW = 0;
	public static final int ACTION_CHECKCARDLIMIT = 1;
	public static final int ACTION_ADDMOBILE = 2;
	public static final int ACTION_EDITMOBILE = 3;
	public static final int ACTION_CCFUND = 4;
	public static final int ACTION_SIGN = 5;
	public static final int ACTION_CCPHONE = 6;
	public static final int ACTION_CCSMS = 7;
	
	public static final int LENGTH_ONE = 1;
	public static final int LENGTH_TWO = 2;
	public static final int LENGTH_THREE = 3;
	public static final int SOURCE_NULL = 0;
	public static final int SOURCE_CORRECT = 1;
	public static final int SOURCE_FORMATERR = 2;
	
	public static final String CARDDATA_KEY = "CARDDATA_KEY";
	public static final String HELP_INDEX_DELETEKATONG = "40";
	public static final String HELP_URL = "file:///android_asset/help.html";
	public static final String ACTIONTYPE_DELETECIF = "307";
	public static final String ACTIONTYPE_DELETECCFUND = "306";
	
	public static final String ACTION_DELCIFWITHDRAW = "delFromCifWithdraw";
	public static final String ACTION_DELCCR = "delFromCCR";
	
	public static final int RESULT_OK = 100;
	public static final int RESULT_AccountHighRisk = 199;
	
		//快付
	public static final String QUICKPAY_DYNAMICID = "dynamicId";
	public static final String QUICKPAY_PAYMONEY = "payMoney";
	public static final String QUICKPAY_PUSH_TRADENO = "tradeNo";
	public static final String QUICKPAY_PUSH_REALAMOUNT = "realAmount";
	public static final String QUICKPAY_PUSH_GOODSLIST = "goodsList";
	public static final String QUICKPAY_PUSH_GOODSNAME = "goodsName";
	public static final String QUICKPAY_PUSH_GOODSCOUNT = "count";
	public static final String QUICKPAY_PUSH_GOODSPRICE = "price";
	public static final String QUICKPAY_PUSH_STORENAME = "storeName";
	
	public static final String QUICKPAY_C2C_PAYEENAME = "payeeName";
	public static final String QUICKPAY_C2C_PAYEEACCOUNT = "payeeAccount";
	public static final String QUICKPAY_C2C_SESSIONCODE = "sessionCode";
	
	public static final String QUICKPAY_MANUFACTURER = "manufacturer";
	public static final String QUICKPAY_PHONEMODEL = "phoneModel";
	public static final String QUICKPAY_OSVERSION = "osVersion";
	public static final String QUICKPAY_ISSURPPORTSOUND = "isSurpportSound";
	public static final String QUICKPAY_ISSETSURPPORT = "isSetSurpport";
	public static final String QUICKPAY_BLACKS = "blacks";
	public static boolean isMZ = false;
	
	public static final String SOURCE_ID = "sourceId";
	public static final String OUT_TRADE_NO = "outTradeNo";
	public static final String RETURN_URL = "returnUrl";
//	public static final String USER_NAME = "userName";
	public static final String AMOUNT = "amount";
	public static final String CARD_TAIL_NUMBER = "cardTailNumber";
	public static final String PARAM = "param";
	public static final String MODIFY = "modify";
	public static final String OUT_ORDER_CHANGE = "outOrderChange";
	public static final String ORDER_SOURCE = "orderSource";
	public static final String OUT_ORDER_NO = "outOrderNo";

	public static final String SENDSMSCODE_RP_JSONCONTENT = "jsonContent";
	public static final String IMAGECODE = "imagecode";
	public static final String ISNEEDREQUESTCMS = "isNeedRequestCMS";
	public static final String INPUT_LOGINACCOUNT = "inputLoginAccount";
	
	public static final int MODIFYLOGINPASSWORD = 1;
	public static final int MODIFYPAYPASSWORD = 2;
	public static final int GETPAYMENTPASSWWORD = 3;	
	public static final int GETLOGINPASSWWORD = 4;
	
	//临时存放数据
	public static  String dynamicId = "";
	public static  String tradeNum = "";
	
	public static boolean FROMAPPCENTER = true;
	//是否跳过手势
	public static String PATTERNNEED = "patternNeed";
	
}
