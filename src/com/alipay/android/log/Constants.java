package com.alipay.android.log;

public class Constants {
	public final static int STORAGE_TYPE_APPLANCHER = 1;
	public final static int STORAGE_TYPE_PAGEJUMP = 2;
	public final static int STORAGE_TYPE_EVENT = 3;
	public final static int STORAGE_TYPE_ERROR = 4;
	public final static int STORAGE_TYPE_VIEWMODEL = 5;
	
	public static boolean LOG_SWITCH = true;
	public static int LOG_ACCOUNT = 0;
	public static Object lock = new Object();
	public final static String LOGFILE_PATH = "/logs";
	public final static String LOGFILE_NAME = "/userlog.log";
	
	public final static int LOG_MAX_ACCOUNT = 20;
	public static long LAST_SEND_TIME = 0;
	
	public final static String BARCODEVIEW = "barcodeView";
	
	public enum BehaviourID{
		NONE("none"),
		CLICKED("clicked"),
		SUBMITED("submited"),
		BIZLAUNCHED("bizLaunched"),
		ERROR("error"),
		EXCEPTION("exception"),
		SETGESTURE("setGesture"),
		CHECKGESTURE("checkGesture"),
		MONITOR("monitor");
		
		private String desc;
		private BehaviourID(String desc) {
			this.desc = desc;
		}
		
		public String getDes(){
			return desc;
		}
	}

	//memo
	public static final String STATE_LOGIN = "Y";
	public static final String STATE_UNLOGIN = "N";
	public static final String ONEPERSONSHARE = "onePersonShare";
	public static final String ANYPERSONSHARE = "anyPersonShare";
	
	public static final String MONITORPOINT_CLIENTSERR = "MonitorPoint_ClientsErr";
	public static final String MONITORPOINT_CONNECTERR = "MonitorPoint_ConnectErr";
	public static final String MONITORPOINT_EXCEPTION = "MonitorPoint_Exception";
	public static final String MONITORPOINT_EVENT_VIEWRETURN = "MonitorPoint_viewReturn";
	public static final String MONITORPOINT_EVENT_BIZRESULT = "MonitorPoint_BizResult";
	public static final String MONITORPOINT_EVENT_CHECKUPDATE = "MonitorPoint_CheckUpdate";
	public static final String MONITORPOINT_EVENT_BUTTONCLICKED = "MonitorPoint_ButtonClicked";
	public static final String MONITORPOINT_EVENT_SHAREINFO = "MonitorPoint_ShareInfo";
	public static final String MONITORPOINT_EVENT_CONTACT = "MonitorPoint_ContactFrom";
	public static final String MONITORPOINT_EVENT = "MonitorPoint_Event";
	
	//sub event id
	public static final String EVENTTYPE_PAIPAISCANRESOULT = "eventType_PaipaiScanResoult";
	public static final String EVENTTYPE_GOTONEWTRANSFERPAGE = "eventType_gotoNewTransferPage";
	public static final String EVENTTYPE_SUPERTRANSFERREADYSHAKE = "eventType_superTransferReadyShake";
	public static final String EVENTTYPE_SHAKESUCCESSUSEDTIME = "eventType_ShakeSuccessUsedTime";
	public static final String EVENTTYPE_CONFIRMTRANSFERINFO_ALIPAYACCOUNT = "eventType_confirmTransferInfo_alipayAccount";
	public static final String EVENTTYPE_CONFIRMTRANSFERINFO_MOBILENO = "eventType_confirmTransferInfo_mobileNo";
	public static final String EVENTTYPE_CONFIRMTRANSFERACCONTBUTTONCLICK = "eventType_confirmTransferAccontButtonClick";
	public static final String EVENTTYPE_HISTORYCONTACTBUTTONCLICK = "eventType_historyContactButtonClick";
	public static final String EVENTTYPE_LOCALCONTACTBUTTONCLICK = "eventType_localContactButtonClick";
	public static final String EVENTTYPE_TRANSFERCALL = "eventType_transferCall";
	public static final String EVENTTYPE_GESTURESETTINGSUCCESS = "eventType_KB_GestureSetting_success";
	public static final String EVENTTYPE_TICKETZONECLICKED = "eventType_KB_TicketZoneClicked";
	public static final String EVENTTYPE_BILLZONECLICKED = "eventType_KB_BillZoneClicked";
	public static final String EVENTTYPE_MYTICKETCLICKED = "eventType_KB_MyTicketClicked";
	public static final String EVENTTYPE_UPDATECLICKED = "eventType_Update";
	public static final String EVENTTYPE_KB_TICKETDETAILSOUNDBUTTONCLICKED = "eventType_KB_TicketDetailSoundButtonClicked";
	public static final String EVENTTYPE_KB_TICKETDETAILREADDETAILBUTTONCLICKED = "eventType_KB_TicketDetailReadDetailButtonClicked";
	public static final String EVENTTYPE_SENDSMSBUTTONCLICKINCREDITCARDVIEW ="eventType_SendSmsButtonClickInCreditCardView";
	public static final String EVENTTYPE_CALLBUTTONCLICKINCREDITCARDVIEW = "eventType_CallButtonClickInCreditCardView";	
	
	//current view info
	public final static String STORAGE_RECORDER = "recorder";
	public final static String STORAGE_TCID = "TCID";
	public final static String STORAGE_CURRENTVIEWID = "currentViewID";
	public final static String STORAGE_APPID = "appID";
	public final static String STORAGE_APPVERSION = "appVersion";
	public final static String STORAGE_PRODUCTID = "productID";
	public final static String STORAGE_PRODUCTVERSION = "productVersion";
	public final static String STORAGE_CLIENTID = "clientID";
	public final static String STORAGE_ALIPAYID = "alipayID";
	public final static String STORAGE_UUID = "uuID";
	public final static String STORAGE_USERID = "userID";
	public final static String STORAGE_MODELVERSION = "modelVersion";
	public final static String STORAGE_REQUESTTYPE = "requestType";
	
	
	//button id
	public final static String LOGINBUTTON = "LoginButton";
	public final static String CONTACT_ALIPAY = "contact_alipay";
	public final static String CONTACT_PHONE = "contact_phone";
	public final static String CONTACT_BARCODE = "contact_barcode";
	
	
	//viewId need record
	public final static String TRADERECORDSVIEW = "tradeRecordsView";
	public final static String TRADERECORDSWAITVIEW = "tradeRecordsWaitView";
	public final static String TRADERECORDSFORALLVIEW = "tradeRecordsAllView";
	public final static String TRADEDETAILSVIEW = "tradeDetailsView";
	public final static String TRADEMSGVIEW = "tradeMsgView";
	public final static String WITHDRAWVIEW = "withdrawView";
	public final static String FEEDBACKVIEW = "feedbackView";
	public final static String NOTIFYSETTINGVIEW = "notifySettingView";
	public final static String SAFEPAYSETTINGVIEW = "safePaySettingView";
	public final static String LOGINVIEW = "loginView";
	public final static String HELPVIEW = "helpView";
	public final static String CARDMANAGEVIEW = "cardManageView";
	public final static String PROTOCOLVIEW = "protocolView";
	public final static String SUBSTITUTEPAYHOMEVIEW = "substitutePayHomeView";
	public final static String ONEPERSONSUBSTITUTEPAYVIEW = "onePersonsubstitutePayView";
	public final static String ANYBODYSUBSTITUTEPAYVIEW = "anyBodysubstitutePayView";
	public final static String ANYBODYSUBSTITUTEPAYSELECTTYPEVIEW = "AnyBodysubstitutePaySelectTypeView";
	public final static String HOMEVIEW = "homeView";
	public final static String FINDLOGINPASSWORDVIEW = "findLoginPasswordView";
	public final static String ACCOUNTMANAGEVIEW = "accountManageView";
	public final static String REGISTERVIEW = "registerView";
	public final static String GETREGISTERSMSVIEW = "getRegisterSmsView";
	public final static String SELECTACCOUNTVIEW = "selectAccountView";
	public final static String SCANBARCODEVIEW = "scanBarCodeView";
	public final static String CASHREGISTERVIEW = "cashRegisterView";
	public final static String PHONEBINDINGVIEW = "phoneBindingView";
	public final static String BINDINGCHECKCODEVIEW = "bindingCheckCodeView";
    public final static String MOREVIEW = "moreView";
    public final static String RECOMMANDVIEW = "recommandView";
    public final static String WALLETACCOUNT = "walletAccount";
    public final static String WALLETBILL = "walletBill";
    public final static String WALLETTICKET = "walletTicket";
    
    public final static String VIEWID_GestureView = "gestureView";
    public final static String VIEWID_NoneView = "-";
    public final static String VIEWID_PasswordView = "passwordView";
    public final static String VIEWID_SetGestureView = "setGestureView";
    public final static String VIEWID_AlipayLoginView = "alipayLoginView";
    public final static String VIEWID_TaobaoLoginView = "taobaoLoginView";
    public final static String VIEWID_BankCardDetails = "bankCardDetails";
    public final static String VIEWID_WithdrawHome = "withdrawHome";
    public final static String VIEWID_ModifyBankPhoneHome = "modifyBankPhoneHome";
    public final static String VIEWID_SeeLimitHome = "seeLimitHome";
    public final static String VIEWID_RepaymentHome = "repaymentHome";
    public final static String VIEWID_SignBankCardHome = "signBankCardHome";
    public final static String VIEWID_InputCardView = "inputCardView";
    public final static String VIEWID_BankCardList = "bankCardList";
    public final static String VIEWID_SmsConfirmView = "smsConfirmView";
    public final static String VIEWID_SignResultView = "signResultView";
    public final static String VIEWID_ManagePasswordView = "managePasswordView";
    public final static String VIEWID_PwdMngHome = "pwdMngHome";
    public final static String VIEWID_InputLoginPwdView = "inputLoginPwdView";
    
    public final static String APPID_signBankCard = "signBankCard";
    public final static String APPID_walletBankCard = "walletBankCard";
    //应用id 首页
    public final static String WALLETAPPSHOW = "walletAppShow";
    public final static String APPID_CCR = "09999999";//信用卡还款
    
    //view id
    public final static String VIEWID_CCR_NEW = "newCardFromView";//新卡还款界面
    public final static String VIEWID_CCR_OLD = "oldCardFromView";//老卡还款界面
    public final static String VIEWID_CCR_SUCCESS = "repaymentSuccessView";//还款成功界面
    public final static String WALLETHOME = "walletHome";
    public final static String ACCOUNTHOME = "accountHome";
    public final static String MSGLIST = "msgList";
    public final static String BANKCARDLIST = "bankCardList";
    public final static String FACEHOME = "faceHome";
    public final static String BILLLIST = "billList";
    public final static String MYTICKETLIST = "myTicketList";
    public final static String MYNAMECARD = "myNameCard";
    public final static String SAVEFACEVIEW = "saveFaceView";
    public final static String SEEFACEVIEW = "seeFaceView";
    
    public final static String APP1IDHOME = "app1IdHome";
    public final static String APP2IDHOME = "app2IdHome";
    public final static String APP3IDHOME = "app3IdHome";
    public final static String APP4IDHOME = "app4IdHome";
    
    public final static String APPCENTER = "appCenter";
    
    //头像埋点id
    public final static String SETFACEICON = "setFaceIcon";
    public final static String BACKICON = "backIcon";
    public final static String SAVEICON = "saveIcon";
    public final static String EDITICON = "editIcon";
    //埋点id 
    public final static String SEEDID_NEXTBUTTON = "nextButton";
    public final static String SEEDID_PHONEQUERY = "phoneQuery";
    public final static String SEEDID_SMSQUERY = "smsQuery";
    public final static String SEEDID_EXPERIENCEICON = "experienceIcon";
    public final static String SEEDID_BILLQUERY = "billQuery";
    public final static String SEEDID_REMINDBUTTON = "remindButton";
    public final static String SEEDID_CLICKOLDCARD = "clickOldCard";
    public final static String SEEDID_CREATICON = "creatIcon";
    public final static String ACCOUNTHOMEZONE = "accountHomeZone";
    public final static String MSGICON = "msgIcon";
    public final static String BANKCARDICON = "bankCardIcon";
    public final static String FACEBOOKICON = "faceBookIcon";
    public final static String BILLZONE = "billZone";
    public final static String TICKETZONE = "ticketZone";
    
    public final static String HOMEAPP1ICON = "homeApp1Icon";
    public final static String HOMEAPP2ICON = "homeApp2Icon";
    public final static String HOMEAPP3ICON = "homeApp3Icon";
    public final static String HOMEAPP4ICON = "homeApp4Icon";
    
    public final static String HOMEAPPSHOWICON = "homeAppShowIcon";
    
    public final static String Seed_LaunchAlipayWallet = "launchAlipayWallet";
    public final static String Seed_CheckGesture = "checkGesture";
    public final static String Seed_SetGestureButton = "setGestureButton";
    public final static String Seed_SkipGestureButton = "skipGestureButton";
    public final static String Seed_SeeBankCard = "seeBankCard";
    public final static String Seed_AddBankCardIcon = "addBankCardIcon";
    public final static String Seed_WithdrawIcon = "withdrawIcon";
    public final static String Seed_ModifyBankPhoneIcon = "modifyBankPhoneIcon";
    public final static String Seed_SeeLimitIcon = "seeLimitIcon";
    public final static String Seed_RepaymentIcon = "repaymentIcon";
    public final static String Seed_SignBankCardIcon = "signBankCardIcon";
    public final static String Seed_DelBankCardIcon = "delBankCardIcon";
    public final static String SEEDID_BACKICON = "backIcon";
    public final static String Seed_ConfirmButton = "confirmButton";
    public final static String Seed_NextButton = "nextButton";
    public final static String Seed_PwdMng = "pwdMng";
    public final static String Seed_SetGesture = "setGesture";

	//公共缴费单viewId
	public final static String WATERINPUTVIEW = "waterInputView";
	public final static String ELECTRICITYINPUTVIEW = "electricityInputView";
	public final static String GASINPUTVIEW = "gasInputView";
	public final static String WIDELINEINPUTVIEW = "widelineInputView";
	public final static String LOTTERYVIEW = "lotteryView";
	
	//信用卡还款ViewId
	public final static String CCRNEWUSERVIEW = "CCRNewUserView";
	public final static String CCROLDUSERVIEW = "CCROldUserView";
	public final static String CCRPAYOKVIEW = "CCRPayOKView";
	public final static String CCRKNOWNVIEW = "CCRKnownView";
	
	//卡宝我的券埋点
	public final static String KABAOMYTICKETLISTVIEW = "kaBaoMyTicketListView";//列表
	public final static String KKABAOTICKETSTOREVIEW = "kaBaoTicketStoreView";//市场
	public final static String KABAOTICKETDETAILVIEW = "kaBaoTicketDetailView";//详情
	
	//卡包银行卡列表埋点
	public static final String WALLE_BANK_CARD = "walletBankCard";
	public static final String ADD_BANK_CARD = "addBankCard";
	public static final String BANK_CARD_LIST = "bankCardList";
	public static final String ADD_CARD_BANK_ICON = "addCardBankIcon";
	public static final String BANK_CARD_DETAILS = "bankCardDetails";
	public static final String SEE_BANK_CARD = "seeBankCard";
	public static final String BACK_ICON = "backIcon";
	
	//悦享拍埋点
	public static boolean firstOpenCM;
	public static long paipaiStep1Start;
	public static long paipaiStep1End;

	public static boolean parserQR;
	public static long paipaiStep3Start;
	public static long paipaiStep3End;
}