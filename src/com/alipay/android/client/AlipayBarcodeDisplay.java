package com.alipay.android.client;

import java.util.HashMap;
import java.util.Timer;
import java.util.TimerTask;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.RemoteException;
import android.os.Vibrator;
import android.util.DisplayMetrics;
import android.view.GestureDetector;
import android.view.GestureDetector.OnGestureListener;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup.LayoutParams;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.alipay.android.client.constant.AlipayHandlerMessageIDs;
import com.alipay.android.client.constant.Constant;
import com.alipay.android.client.util.AlipayDataStore;
import com.alipay.android.client.util.AlipayLocateInfo;
import com.alipay.android.client.util.BaseHelper;
import com.alipay.android.client.util.DBHelper;
import com.alipay.android.client.util.Responsor;
import com.alipay.android.common.data.AlipayUserState;
import com.alipay.android.common.data.UserInfo;
import com.alipay.android.comon.component.ProgressDiv;
import com.alipay.android.comon.component.StyleAlertDialog;
import com.alipay.android.core.MsgAction;
import com.alipay.android.safepay.MobileSecurePayHelper;
import com.alipay.android.safepay.SafePayResultChecker;
import com.alipay.android.util.LogUtil;
import com.eg.android.AlipayGphone.R;
import com.google.zxing.client.Contents;
import com.google.zxing.client.FinishListener;
import com.google.zxing.client.Intents;
import com.google.zxing.client.encode.QRCodeEncoder;

/**
 * 
 * @author zhan zhi
 *
 */
public class AlipayBarcodeDisplay extends RootActivity implements OnTouchListener, OnGestureListener{
	private static final String LOG_TAG = "AlipayBarCodeDisplay";
	
	private static boolean mTest = false;
	private static boolean mQRCodeTest = false;
	
	private String mUserName = "";
//	private String mReturnedUserId = "";
	
//	private String mCurAccount = null;
	private String mCurTradeNo = null;
//	private String mCurBizType = null;
	private String mCurTradeAct = null;
//	private String mExternToken = null;
	private String mCurBarcodeTradeNo = null;
	
	private static boolean mAlarmTimer = true;
	
//	private boolean mReInitPushService = false;
	private boolean mPushLinkLostAlertShow = false;
	private boolean mPushLinkLostShow = false;

	private static final int MSG_DISPLAY_BLANK_HANDLE = 1019;
	private static final int MSG_VALIDATE_TIMER_CMD = 1020;
	private static final int MSG_DISPLAY_ALARM_TIMER = 1021;
	private static final int MSG_CHECK_PUSHSERVICE_STATUS = 1022;
	private static final int MSG_BARCODE_GET_BARCODE = 1023;
	private static final int MSG_BARCODE_GET_BARCODE_LICENSE = 1024;
	
	private static final int MSG_BARCODE_WAP_UNSUPPORT = 1030;
	private static final int MSG_BARCODE_PUSHLINK_INIT_FAILED = 1031;
	private static final int MSG_BARCODE_PUSHLINK_INITED = 1032;
	private static final int MSG_BARCODE_PUSHLINK_DATA = 1033;
	
	protected final static int REQUEST_CAPTURE_ACTION = 0;
    protected final static int REQUEST_CALCULATE_ACTION = 1;
    protected final static int REQUEST_OPENBARCODE_ACTION = 2;
    protected final static int REQUEST_PAYCONFIRM_ACTION = 3;
	
    public DBHelper userDb = null;
	private AlipayDataStore mAlipayDataStore = null;
	private BroadcastReceiver mIntentReceiver = null;
//	private AlipayTradeStatus mTradeStatus = null;
	MobileSecurePayHelper mspHelper = new MobileSecurePayHelper();
	private AlipayUserState curUserState = AlipayUserState.getInstance();
	
	private int mCurPushStatus = -1;
	private boolean mLaunchClosePushlink = false;
	
	private boolean mBarcodePayRet = false;
	
	private GestureDetector mGestureDetector; 
	
	private int mBackIndex = -1;
	private String mBarcodeKey = null;
	private Bitmap Barcode1DImg = null;
	private Bitmap Barcode2DImg = null;
//	private Drawable mBarcodeDrawable = null;
//	private Drawable mBackDrawable = null;
	private Bitmap mRotate2DImg = null;
	
	private View mbackLayout;
	private TextView mTitleName=null;
	private RelativeLayout mAllLayout = null;
	private TextView mBarCodePromptView;
	private ProgressBar mProgressBarTimer = null;
	private ImageView mBarcodeImgView = null;  
	private ImageView mQrcodeImgView = null; 
	private ImageView mBarcodeImgLeft = null;
	private ImageView mBarcodeImgRight = null;
	private TextView mTxtBarCodeNumber;
	private TextView mTxtCurAccount = null;
	private Button mBtnUserGuide = null;
	
	private ImageView mBackGroundImg = null;
	
	/**
	 * Variables for error type
	 */
	private int mErrorType = NO_ERROR;
	private static final int NO_ERROR = 0;
	//快捷支付更新
	private static final int BARCODE_SAFEPAY_UPDATE_START = -1;
	private static final int BARCODE_SAFEPAY_UPDATE_FINISH = -2;
	//检查是否已经激活
	private static final int BARCODE_SAFEPAY_CHECK_START = -3;
	private static final int BARCODE_SAFEPAY_CHECK_FINISH = -4;
	//激活流程
	private static final int BARCODE_SAFEPREPAY_START = -5;
	private static final int BARCODE_SAFEPREPAY_FINISH = -6;
	//建立pushlink
	private static final int BARCODE_PUSHLINK_START = -7;
	private static final int BARCODE_PUSHLINK_FINISH = -8;
	//获取条码
	private static final int BARCODE_GET_START = -9;
	private static final int BARCODE_GET_DOING = -10;
	private static final int BARCODE_GET_FINISH = -11;
	//开始支付
	private static final int BARCODE_SAFEPAY_START = -12;
	private static final int BARCODE_SAFEPAY_FINISH = -13;
	
	private MessageFilter mMessageFilter = new MessageFilter(this);
	private ProgressDiv mProgress=null;
	
	private ProgressDialog mTempProgress=null;
	private boolean mShowProgressbarforSafePay = false;
	private boolean mWorkAround = false;
	
	private boolean mIfDisp1DBarcode = true; //true为显示一维码, false显示二维码
	private boolean IsBarcodeDisplayed = false; //条形码是否成功显示
	
	private static final int mPollingInterval = 60*1000;
	private int mPollingCount = 0; //轮询计数器，根据这个计数可以得出共扫描了多少时间了，以便决定是否要退出。
	private int mTotalDispTime = 1000*60*5;  //总共允许显示条码的时间（毫秒）
	
	private AlarmManager am = null;
	private PendingIntent sender = null;
	
	private int mScaleHeight = 320;
	private int mScaleWeight = 240;
	private float mCurScale = 1.5f;
	private static float mbarcodeSizeScale = 0.65f;
	private static float mqrodeSizeScale = 0.9f;
    private QRCodeEncoder qrCodeEncoder;
    private String mbackOfBarcode = "0";
    private final int mAllBackImg = 1;
    
    private static final HashMap<String, Integer> mBackImgMap = new HashMap<String, Integer>();
    static
	{
    	mBackImgMap.clear();
    	
    	mBackImgMap.put("0", new Integer(R.drawable.basic));
    	mBackImgMap.put("1", new Integer(R.drawable.chopsticks));
    	mBackImgMap.put("2", new Integer(R.drawable.lantern));
    	mBackImgMap.put("3", new Integer(R.drawable.comb));
//    	mBackImgMap.put("4", R.drawable.fan);
    	
//    	mBackImgMap.put("3", new Integer(R.drawable.rain));
	}
    
//    private static float[] mScaleMap = {0.6f, 0.8f, 0.9f};
	
	private Timer mTimer = null;// = new Timer(true);
	BarcodeTradeInfo mBarcodeTradeInfo = null;
	
	protected void stopAlarmTimer() {
        if(am != null) {
        	am.cancel(sender);
        }
    }
	
    protected void startAlarmTimer() {
    	if(null != am) {
    		am.set(AlarmManager.RTC_WAKEUP, System.currentTimeMillis()+mTotalDispTime, sender);
    		LogUtil.logMsg(Constant.LOG_LEVEL_DEBUG, LOG_TAG, "startAlarmTimer startAlarmTimer ELAPSED_REALTIME_WAKEUP!");
		}
    }
	
	protected void stopTimer() {
        if(mTimer != null) {
        	mTimer.cancel(); //停止timer
        	mTimer = null;
        }
    }
	
    protected void startTimer() {
    	if(null!=mTimer) {
    		stopTimer();
		}
    	mTimer = new Timer(true);
    	mTimer.schedule(new timerTask(), 1000, mPollingInterval);
    }
    
	class timerTask extends TimerTask {
		public void run() {
			Message message = new Message(); 
			message.what = MSG_VALIDATE_TIMER_CMD;
			mHandler.sendMessage(message);
		}	
	}

	private Handler mHandler = new Handler()
	{
		public void handleMessage(Message msg)
		{
			LogUtil.logMsg(Constant.LOG_LEVEL_WARNING, LOG_TAG, "handleMessage msg:"+ msg.what);
			LogUtil.logMsg(Constant.LOG_LEVEL_DEBUG, LOG_TAG, "2 mErrorType="+mErrorType);
			
			Responsor responsor;
			JSONObject jsonResponse;
//			boolean resultOK;
			
			switch (msg.what) 
			{
			case MSG_DISPLAY_BLANK_HANDLE:
				if (mTempProgress != null){	
					mTempProgress.dismiss();
					mTempProgress=null;
				}
				break;
				
			case MSG_DISPLAY_ALARM_TIMER:
				stopAlarmTimer();
				
				mErrorType = NO_ERROR;
				closePushLink();
				
				getDataHelper().showDialog(AlipayBarcodeDisplay.this, R.drawable.infoicon, 
						getResources().getString(R.string.WarngingString),
						getResources().getString(R.string.barcode_wait_timeout), 
						getResources().getString(R.string.Ensure),
						new DialogInterface.OnClickListener() {
							@Override public void onClick(DialogInterface dialog, int which)
							{
								//退出条码支付应用
								showDesktop();
							}
						}, null, null, null, null);		
				break;
				
			case MSG_VALIDATE_TIMER_CMD:
				if(mPollingInterval*(mPollingCount+1) >= mTotalDispTime) {//显示的时间已到
					mProgressBarTimer.incrementProgressBy(1);
					stopTimer();
					getDataHelper().showDialog(AlipayBarcodeDisplay.this, R.drawable.infoicon, 
							getResources().getString(R.string.WarngingString),
							getResources().getString(R.string.barcode_wait_timeout), 
							getResources().getString(R.string.Ensure),
							new DialogInterface.OnClickListener() {
								@Override public void onClick(DialogInterface dialog, int which)
								{
									//退出条码支付应用
									showDesktop();
								}
							}, null, null, null, null);		
				} else {
					mProgressBarTimer.incrementProgressBy(1);
					mPollingCount++; // 扫描计数器
				}
				break;
				
			case MSG_BARCODE_WAP_UNSUPPORT:
				stopAlarmTimer();
				mErrorType = NO_ERROR;
				getDataHelper().showDialog(AlipayBarcodeDisplay.this, R.drawable.infoicon, 
						getResources().getString(R.string.WarngingString),
						getResources().getString(R.string.barcode_wap_unsupport), 
						getResources().getString(R.string.Ensure),
						new DialogInterface.OnClickListener() {
							@Override public void onClick(DialogInterface dialog, int which)
							{
								//退出条码支付应用
								showDesktop();
							}
						}, null, null, null, null);	
            	break;

            //快捷支付相关的消息处理
			case AlipayHandlerMessageIDs.RQF_PAY:
				LogUtil.logMsg(Constant.LOG_LEVEL_DEBUG, LOG_TAG, 
						"handleMessage RQF_PAY barcodeToken:"+Constant.STR_BARCODETOKEN);
				closeProgress();
            	safepayActionHandle(msg);
            	break;
            
			case AlipayHandlerMessageIDs.SAFEPAY_CHECK_GET_AUTHLIST:
				safepayListHandle(msg);
			break; 
			
			case AlipayHandlerMessageIDs.ALIPAY_UPLOAD_LOCATE_INFO:
				break;
            	
			case MSG_BARCODE_PUSHLINK_INIT_FAILED:
				closeProgress();
				LogUtil.logMsg(Constant.LOG_LEVEL_DEBUG, LOG_TAG, 
						"handleMessage MSG_BARCODE_PUSHLINK_INIT_FAILED mErrorType="+mErrorType 
						+", mCurPushStatus="+mCurPushStatus);
				pushlinkLostHandle();
				break;
			
			case AlipayHandlerMessageIDs.BARCODE_TRADE_CANCEL:
			case AlipayHandlerMessageIDs.PAYING_RESULT_NOTICE:
				responsor = (Responsor) msg.obj;
				jsonResponse = responsor.obj;
				
				if( responsor != null) {
					if (responsor.status==100 ) {
						LogUtil.logMsg(Constant.LOG_LEVEL_DEBUG, LOG_TAG, 
								"handleMessage BARCODE_TRADE_CANCEL or PAYING_RESULT_NOTICE success! mCurTradeNo="+mCurBarcodeTradeNo 
								+", mCurTradeAct="+mCurTradeAct);
					}
				}
				mCurBarcodeTradeNo = null;
//				mCurTradeNo = null;
				mCurTradeAct = null;
				break;
            	
			case MSG_BARCODE_PUSHLINK_INITED:
				closeProgress();
				mCurPushStatus = getPushServiceStatus();
				
				if(mErrorType == BARCODE_PUSHLINK_START) {	//之前的检查已经通过
					//继续获取条码
					mHandler.sendEmptyMessage(MSG_CHECK_PUSHSERVICE_STATUS);
				} else if(mErrorType == BARCODE_GET_FINISH) {	//条码已经获取成功了,长连接中断又恢复
					//继续等待服务端push交易数据
				} else {
					//还没到可以获取条码的状态
				}
				break;
				
			case MSG_BARCODE_PUSHLINK_DATA:
				handelDealData(msg);
				break;
				
			case MSG_CHECK_PUSHSERVICE_STATUS:
				checkPushService();			
            	break;
            	
			case MSG_BARCODE_GET_BARCODE_LICENSE:
				handelLicenseData(msg);
				break;

			case MSG_BARCODE_GET_BARCODE:
				// 每次获取动态条码串时更新背景图片
				changeBackImgforBarcode();

				if (mTest) {
					mBarcodeKey = "12345678";
					mErrorType = BARCODE_GET_FINISH;
					getBarcodeImg(mBarcodeKey);
				} else {
					responsor = (Responsor) msg.obj;
					jsonResponse = responsor.obj;
					
					if( responsor.status==100 ) {
						mErrorType = BARCODE_GET_FINISH;
						mBarcodeKey = jsonResponse.optString(Constant.RPF_BARCODE_KEY);
//						mBarcodeKey = "1234567890";
						getBarcodeImg(mBarcodeKey);
						
						//此时需要发送地理位置信息
						AlipayLocateInfo locateInfo = BaseHelper.getLocation(AlipayBarcodeDisplay.this, null);
						if (locateInfo != null) {
						    getDataHelper().sendActionLocateInfo(mHandler, AlipayHandlerMessageIDs.ALIPAY_UPLOAD_LOCATE_INFO,
									mUserName, locateInfo.curCellId, locateInfo.curCellIdNet, locateInfo.curCellIdLac,
									locateInfo.latitude, locateInfo.longititude, locateInfo.accuracyRange, 
									getConfigData().getProductId(), Constant.LOCATE_BUSINESS_BARCODEPAY);
						}
						
						//定时检查
						if (mAlarmTimer) {
							startAlarmTimer();
						} else {
							startTimer();
						}
					} else {
						closeProgress();
						mErrorType = NO_ERROR;
						LogUtil.logMsg(Constant.LOG_LEVEL_WARNING, LOG_TAG, 
								"handleMessage MSG_GET_BARCODE_RESP tStatus:"+ responsor.status);
						mMessageFilter.process(msg);
					}
				}
			break;

			case R.id.encode_succeeded:
				closeProgress();
	        	
	            Bitmap tImg = (Bitmap) msg.obj;
	            int width = tImg.getWidth();  
	            int height = tImg.getHeight(); 
	            LogUtil.logMsg(Constant.LOG_LEVEL_DEBUG, LOG_TAG, "encode_succeeded image width:"+width +", height:"+height);
	            
	            mBackIndex = mBackImgMap.get(mbackOfBarcode).intValue();

	            mAlipayDataStore.putString(AlipayDataStore.BARCODE_BACK_IMG, mbackOfBarcode);
	            
	            if (mIfDisp1DBarcode) {
	            	Barcode1DImg = (Bitmap) msg.obj;
	            	switchtoBarcode();
	            	mIfDisp1DBarcode = false;
	        		
	        		//获取二维码
	        		getBarcodeImg(mBarcodeKey);
	            	
	            } else {
	            	//二维码
	            	Barcode2DImg = (Bitmap) msg.obj;
	            }
	            
	            break;
	            
	        case R.id.encode_failed:
	        	closeProgress();
	            showErrorMessage(R.string.msg_encode_barcode_failed);
	            qrCodeEncoder = null;
	            mErrorType = NO_ERROR;
	            break;
				
			default:
				break;
			}
			
			super.handleMessage(msg);
		}
	};
	
	private void pushlinkLostHandle() {
//		if ((mErrorType == BARCODE_GET_START && mReInitPushService == true)) {
		if(mErrorType <= BARCODE_PUSHLINK_START) {
			stopAlarmTimer();
			this.mErrorType = NO_ERROR;
			this.mCurPushStatus = -1;
			this.mPushLinkLostShow = true;
			
//			if (mActivity != null && mActivity.hasWindowFocus()) 
			if (!this.mLaunchClosePushlink)
			{
				this.mPushLinkLostAlertShow = true;
				getDataHelper().showDialog(this, R.drawable.infoicon, 
						getResources().getString(R.string.WarngingString),
						getResources().getString(R.string.BarcodePushStatusError), 
						getResources().getString(R.string.Ensure),
						new DialogInterface.OnClickListener() {
							@Override public void onClick(DialogInterface dialog, int which)
							{
								//退出条码支付应用
								showDesktop();
							}
						}, null, null, null, null);	
			} else {
				clearPushLink();
			}
		}

	}
	
	private void setBarcodeText(String barcodeStr)
	{
		if (barcodeStr != null && !barcodeStr.equals("")) {
			int index = barcodeStr.length()/2;;
			
			if (barcodeStr.length() > 8) {
				index = barcodeStr.length()/3;
			}
			
			StringBuffer str = new StringBuffer(barcodeStr); 
			String strInsert = " "; 
			str.insert(index, strInsert);
			
			if (index*2+1 < barcodeStr.length()) {
				str.insert(index*2+1, strInsert);
			}
			mTxtBarCodeNumber.setText(str.toString());
		} else {
			mTxtBarCodeNumber.setText(barcodeStr);
		}
		mTxtBarCodeNumber.setTextSize(24f);
	}

	private void getBarcodeImg(String barcodeStr)
	{
		String realString = barcodeStr;
		setBarcodeText(barcodeStr);
		
		int desiredWidth = -1;
		int desiredHeight = -1;
		int smallerDimension = -1;

		Intent intent = new Intent(Intents.Encode.ACTION);
		if (mIfDisp1DBarcode) {
			intent.putExtra(Intents.Encode.FORMAT, "CODE_128");

			desiredHeight = (int) (mScaleHeight * mbarcodeSizeScale);
			desiredWidth = (int) (mScaleWeight * mbarcodeSizeScale);
			LogUtil.logMsg(Constant.LOG_LEVEL_DEBUG, LOG_TAG, "111 desiredHeight:"+ desiredHeight +", desiredWidth:"+desiredWidth);
		} else {
			intent.putExtra(Intents.Encode.FORMAT, "QR_CODE");
			
			//等比按尺寸放大条码显示 
			desiredWidth = (int) (mScaleHeight * mqrodeSizeScale);
			desiredHeight = (int) (mScaleWeight * mqrodeSizeScale);
			smallerDimension = desiredWidth < desiredHeight ? desiredWidth : desiredHeight;
			desiredWidth = smallerDimension;
			desiredHeight = smallerDimension;
			LogUtil.logMsg(Constant.LOG_LEVEL_DEBUG, LOG_TAG, "desiredWidth:"+ desiredWidth +", desiredHeight:"+desiredHeight);
			
			if (mQRCodeTest) {
				realString = "Alipay:"+realString;
			} else {
				String prefix = mAlipayDataStore.getString(AlipayDataStore.QRCODE_PREFIX);
				if (prefix != null && !prefix.equals("")) {
					Constant.STR_QRCODE_PREFIX = prefix;
				}
				//登录时从服务端获取到的前缀
				realString = Constant.STR_QRCODE_PREFIX + barcodeStr;;
			}
		}
		
		intent.putExtra(Intents.Encode.TYPE, Contents.Type.TEXT);
		intent.putExtra(Intents.Encode.DATA, realString);
		LogUtil.logMsg(Constant.LOG_LEVEL_INFO, LOG_TAG, "contents:"+ realString);

		try {
			qrCodeEncoder = new QRCodeEncoder(AlipayBarcodeDisplay.this, intent);

//			qrCodeEncoder.requestBarcode(mHandler, desiredHeight, desiredWidth);
			qrCodeEncoder.requestBarcode(mHandler, desiredWidth, desiredWidth);
        } catch (IllegalArgumentException e) {
        	showErrorMessage(R.string.msg_encode_contents_failed);
        }
	}
	
//	private void getScaleSize() {
//		//目前仅处理了当前市面上较多的三种比例屏幕：4/3, 3/2, 5/3
//		if (mCurScale < 1.4f) {
//			mbarcodeSizeScale = 0.56f;
//		} else if (mCurScale > 1.6f) {
//			mbarcodeSizeScale = 0.64f;
//		} else {
//			mbarcodeSizeScale = 0.72f;
//		} 
//	}
	
	private void showErrorMessage(int message) {
//	    AlertDialog.Builder builder = new AlertDialog.Builder(this);
//	    builder.setMessage(message);
//	    builder.setPositiveButton(R.string.button_ok, new FinishListener(this));
//	    builder.setOnCancelListener(new FinishListener(this));
//	    builder.show();
	    
        StyleAlertDialog dialog = new StyleAlertDialog(this, 0, "",getResources().getString(R.string.message),
        		getResources().getString(R.string.button_ok),new FinishListener(this),
        		null,null, new FinishListener(this));
            dialog.show();
	}

	private void safepayActionHandle(Message msg){
//		String memo = null;
		int ret = msg.arg1;
		String strRet = (String)msg.obj;
		LogUtil.logMsg(Constant.LOG_LEVEL_WARNING, LOG_TAG, strRet);

		switch (mErrorType) {
			case BARCODE_SAFEPREPAY_START:
				mErrorType = BARCODE_SAFEPREPAY_FINISH;
				break;
			case BARCODE_SAFEPAY_START:
				mErrorType = BARCODE_SAFEPAY_FINISH;
				break;
		}
		LogUtil.logMsg(Constant.LOG_LEVEL_DEBUG, LOG_TAG, 
				"safepayActionHandle 3 mErrorType="+mErrorType + ", ret="+ret);
		
		if(ret == 1) {
			switch (mErrorType) {
				case BARCODE_SAFEPREPAY_FINISH:
					//激活成功
					if (getUserData()!=null) {
						curUserState.setUserState(AlipayUserState.ACCOUNT_STATE_LOGIN_SAFEPAY);
						//启动pushLinkService,开始试图去获取动态条码串
						mErrorType = BARCODE_PUSHLINK_START;
						bindToPushService(true);
					} else {
						curUserState.setUserState(AlipayUserState.ACCOUNT_STATE_AUTH_SAFEPAY);
						//快捷支付已经激活，但是还未获取barcodeKey
						String validAcount = curUserState.getValidAcount();
						getBarcodeByLicense("true", validAcount);
					}
					//更新列表和状态
					curUserState.updateSafepayAuthList(this, mHandler);
					break;
					
				case BARCODE_SAFEPAY_FINISH:
//					if (strRet != null && strRet.equals("9000")) {
					if (strRet != null && strRet.contains("resultStatus")) {
						SafePayResultChecker resultChecker = new SafePayResultChecker(strRet);
						String status = resultChecker.getReturnStr(Constant.SAFE_PAY_RESULT_STATUS);
						if (status != null && status.equals("9000")) { //用户中途取消了此次支付
							//需要处理MobileSecurePayer中异步线程返回的结果
							//客户端需要通过服务端通知收银台
							LogUtil.logMsg(Constant.LOG_LEVEL_DEBUG, LOG_TAG, 
									"safepayActionHandle 4 mErrorType="+mErrorType +", mCurBarcodeTradeNo="+mCurBarcodeTradeNo);
							//向服务端请求取消当前条码支付交易
							getDataHelper().sendPayResultNotice(mHandler, AlipayHandlerMessageIDs.PAYING_RESULT_NOTICE, 
				    				mCurBarcodeTradeNo, "finish", "");
				    		
				    		mBarcodePayRet = true;
				    		
				    		// 6. 付款成功——退出还是刷新当前页面
							showDesktop();
						}
					}
					break;
			}
		} else {
			//失败，分情况处理
			switch (mErrorType) {
				case BARCODE_SAFEPAY_FINISH:
					if (strRet != null && strRet.contains("resultStatus")) {
						SafePayResultChecker resultChecker = new SafePayResultChecker(strRet);
						String status = resultChecker.getReturnStr(Constant.SAFE_PAY_RESULT_STATUS);
						if (status != null && status.equals("6001")) { //用户中途取消了此次支付
							//客户端需要通过服务端通知收银台
							LogUtil.logMsg(Constant.LOG_LEVEL_WARNING, LOG_TAG, "safepayActionHandle 4 status="+status + ", ret="+ret);
							//向服务端请求取消当前条码支付交易
							getDataHelper().sendCancelBarcodePay(mHandler, AlipayHandlerMessageIDs.BARCODE_TRADE_CANCEL, 
				    				mUserName, mCurBarcodeTradeNo);
							showDesktop();
						} else {	//其他快捷支付错误
							//提示对话框，用户点击退出
//							memo = resultChecker.getReturnStr(Constant.SAFE_PAY_RESULT_MEMO);
							//退出条码支付应用
							showDesktop();

						}
					}
					break;
				case BARCODE_SAFEPREPAY_FINISH:
				default:
					// 没法继续，则finish
					showDesktop();
					break;
			}
//			mErrorType = NO_ERROR;
		}
    }
	
	private void safepayListHandle(Message msg) {
		int ret = msg.arg1;
		String strRet = (String)msg.obj;
		LogUtil.logMsg(Constant.LOG_LEVEL_DEBUG, LOG_TAG, strRet);
		
		if(ret == 1) {	//成功
			if (strRet != null) {
				try {
					JSONObject objContent = BaseHelper.string2JSON(strRet, ";");
					String result = objContent.getString(Constant.SAFE_PAY_RESULT_ARRAY);

					if (result !=null && !result.equals("")) {
						JSONObject tObject = new JSONObject(result);
						JSONArray tContactsArray = tObject.optJSONArray(Constant.SAFE_PAY_RESULT_ACCOUNTDATA);
						
						if(tContactsArray != null && tContactsArray.length()>0) {
							curUserState.setSafepayAuthList(tContactsArray);
							//更新当前用户状态
							curUserState.updateCurAccount();
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
		//更新列表和状态
		curUserState.updateUserState(getUserData()!=null);

		if (mErrorType == BARCODE_SAFEPAY_UPDATE_FINISH) {
			mErrorType = BARCODE_SAFEPAY_CHECK_START;
			//根据状态选择下步动作
			selectNextAction();
		}
		
	}
	
	private void selectNextAction() {
		if (mErrorType <= BARCODE_SAFEPAY_UPDATE_FINISH) {
//			if (!Constant.mWapSupport && BaseHelper.isWapApn(mActivity)) {
//				mHandler.sendEmptyMessage(MSG_BARCODE_WAP_UNSUPPORT);
//			} else 
			{
				//获取当前有效账户
				String validAcount = curUserState.getValidAcount();
				int mState = curUserState.getUserState();
				if (getUserData()!=null) {
					switch (mState) {
						case AlipayUserState.ACCOUNT_STATE_LOGIN:
							//此处检查登录用户是否已经通过快捷支付认证
							mErrorType = BARCODE_SAFEPAY_CHECK_START;
							if(curUserState.isActiveAccount(curUserState.getUserId())) {
								//登录账户，已经处于快捷支付本地认证list
								curUserState.setUserState(AlipayUserState.ACCOUNT_STATE_LOGIN_SAFEPAY);
								
								mErrorType = BARCODE_PUSHLINK_START;
								//激活成功，开始试图去获取动态条码串
								//mHandler.sendEmptyMessage(MSG_CHECK_PUSHSERVICE_STATUS);
								//启动pushLinkService
								bindToPushService(true);
							} else {
								//该用户需要通过快捷支付认证
								mErrorType = BARCODE_SAFEPREPAY_START;
								//通过获取登录时得到的extern_token，调用快捷支付激活当前用户
								BaseHelper.payDeal((RootActivity)AlipayBarcodeDisplay.this, mHandler, 
										mProgress, "", getExtToken(), "", 
										Constant.SAFE_PAY_ACTIVE_BIZ_TYPE, Constant.SAFE_PAY_ACTIVE);
							}
						break;
						
						case AlipayUserState.ACCOUNT_STATE_LOGIN_SAFEPAY:
							//登录账户，已经处于快捷支付本地认证list
							mErrorType = BARCODE_PUSHLINK_START;
							//激活成功，开始试图去获取动态条码串
							//mHandler.sendEmptyMessage(MSG_CHECK_PUSHSERVICE_STATUS);
							//启动pushLinkService
							bindToPushService(true);
						break;	
					}
				} else {	//未登录模式下
					switch (mState) {
						//AlipayUserState.ACCOUNT_STATE_NO_LOGIN:
						//此情况在onStart中已经处理

						case AlipayUserState.ACCOUNT_STATE_AUTH_PREPARE:
							//曾经登录过的账号，需要检查是否通过快捷支付认证
							mErrorType = BARCODE_SAFEPAY_CHECK_START;

							if(curUserState.isActiveAccount(curUserState.getUserId())) {
								//登录账户，已经处于快捷支付本地认证list
								curUserState.setUserState(AlipayUserState.ACCOUNT_STATE_AUTH_SAFEPAY);
								mErrorType = BARCODE_SAFEPAY_CHECK_FINISH;
								// 激活成功，开始试图去获取动态条码串
								getBarcodeByLicense("true", validAcount);
							} else {
								//该用户需要通过快捷支付认证
								mErrorType = BARCODE_SAFEPREPAY_START;
								//通过获取登录时得到的extern_token，调用快捷支付激活当前用户
								getBarcodeByLicense("false", validAcount);
							}
						break;
							
						case AlipayUserState.ACCOUNT_STATE_AUTH_SAFEPAY:
							//该登录过的账户，已经处于快捷支付本地认证list
							if (mErrorType == BARCODE_SAFEPAY_CHECK_START) {
								mErrorType = BARCODE_SAFEPAY_CHECK_FINISH;
								//获取条码、barcode_token和extern_token
								getBarcodeByLicense("true", validAcount);
							}
						break;	
					}
				}
			}
		}
	}

	private int getPushServiceStatus() {
		int pushStatus = -1;
		
		if (this.mIsBound == true && mService != null) {
			//获取push service的状态
			try {
				pushStatus = mService.getPushLinkStatus();
			} catch (RemoteException e) {
				LogUtil.logMsg(Constant.LOG_LEVEL_ERROR, LOG_TAG,"get status of push Service failed");
            }
		} 
		return pushStatus;
	}
	
	private void getBarcodeKey() {
		int mState = curUserState.getUserState();
		LogUtil.logMsg(Constant.LOG_LEVEL_DEBUG, LOG_TAG, 
				"checkBarcodePayEnv msg:"+MSG_BARCODE_GET_BARCODE
                +", userName:"+mUserName +", mState="+mState);

    	mErrorType = BARCODE_GET_DOING;
    	
    	openProcessDialog(null, getString(R.string.BarcodeGetRequest));
    	
		//向服务端请求条码字符串
    	getDataHelper().sendGetBarCodeReq(mHandler, MSG_BARCODE_GET_BARCODE, mUserName);
    }
	
	private void getBarcodeByLicense(String isActive, String curAccount) {
		String promptMsg = "";
		int mState = curUserState.getUserState();
		switch (mState) {
			case AlipayUserState.ACCOUNT_STATE_AUTH_PREPARE:
				promptMsg = getString(R.string.BarcodeSafepayInit);
				mErrorType = BARCODE_SAFEPREPAY_START;
				break;
			case AlipayUserState.ACCOUNT_STATE_AUTH_SAFEPAY:
				promptMsg = getString(R.string.BarcodeGetRequest);
				mErrorType = BARCODE_SAFEPREPAY_FINISH;
				break;
		}
		LogUtil.logMsg(Constant.LOG_LEVEL_DEBUG, LOG_TAG, 
				"getBarcodeByLicense msg:"+MSG_BARCODE_GET_BARCODE_LICENSE
                +", curAccount:"+curAccount +", mState="+mState +", mErrorType="+mErrorType);
		
		closeProgress();
    	openProcessDialog(null, promptMsg);
    	
		//向服务端请求条码字符串
    	getDataHelper().sendGetBarCodebyLicense(mHandler, MSG_BARCODE_GET_BARCODE_LICENSE, curAccount, isActive);
    } 
	
	private void handelLicenseData(Message msg) {
		String memo = null;
		Responsor responsor;
		JSONObject jsonResponse;
		
		responsor = (Responsor) msg.obj;
		jsonResponse = responsor.obj;
		
		DialogInterface.OnClickListener btnForError = null;
		
		int mState = curUserState.getUserState();
		
		LogUtil.logMsg(Constant.LOG_LEVEL_INFO, LOG_TAG, 
				"handleMessage MSG_GET_BARCODE_LICENSE_RESP mErrorType:"+mErrorType + ", mState:"+mState);
		
		if( responsor != null) {
			if (responsor.status==100 ) {
				String curAccount = jsonResponse.optString(Constant.RPF_BARCODE_CUR_ACCOUNT);
//				String validAcount = curUserState.getValidAcount();
				switch(mState) {
					case AlipayUserState.ACCOUNT_STATE_AUTH_PREPARE:
						//必然是未登录且未知是否认证的情况
						//此返回的curAccount就是对应的userId
						curUserState.setUserId(curAccount);
						if(curUserState.isActiveAccount(curAccount)) {
							//激活的
							curUserState.setUserState(AlipayUserState.ACCOUNT_STATE_AUTH_SAFEPAY);
							mErrorType = BARCODE_SAFEPREPAY_FINISH;
							getBarcodeByLicense("true", curAccount);
						} else {
							//没有激活，调用快捷支付激活
	                        getCertUserData().setExtToken(jsonResponse.optString(Constant.RQF_QRRCODEPAY_EXTERN_TOKEN));
							if (mErrorType == BARCODE_SAFEPREPAY_START) {
								//调用快捷支付激活
								BaseHelper.payDeal(AlipayBarcodeDisplay.this, mHandler, 
										mProgress, "", getExtToken(), "", 
										Constant.SAFE_PAY_ACTIVE_BIZ_TYPE, Constant.SAFE_PAY_ACTIVE);
							}
						}
						break;
					default:
					    getCertUserData().setExtToken(jsonResponse.optString(Constant.RQF_QRRCODEPAY_EXTERN_TOKEN));
						Constant.STR_BARCODETOKEN  = jsonResponse.optString(Constant.RPF_BARCODE_PAY_TOKEN);
						mBarcodeKey = jsonResponse.optString(Constant.RPF_BARCODE_KEY);
						
						LogUtil.logMsg(Constant.LOG_LEVEL_INFO, LOG_TAG, 
								"handleMessage MSG_GET_BARCODE_LICENSE_RESP mErrorType:"+mErrorType + ", mBarcodeKey:"+mBarcodeKey);
						
						if (mErrorType == BARCODE_SAFEPREPAY_FINISH) {
							mErrorType = BARCODE_PUSHLINK_START;
							closeProgress();
							//需要先建立长连接
							bindToPushService(true);
						}
						break;	
				}
			} else {
				memo = responsor.memo;
				if (responsor.status == 0) {
					memo = getResources().getString(R.string.CheckNetwork);
				}else if (responsor.status == 1) {
                    memo = getResources().getString(R.string.WarningDataCheck);
                }
				//其他任何错误，目前的处理时提示用户，确认后推出条码支付
				btnForError = new DialogInterface.OnClickListener() {
					@Override public void onClick(DialogInterface dialog, int which)
					{
						//退出条码支付应用
						showDesktop();
					}
				};
				
				LogUtil.logMsg(Constant.LOG_LEVEL_INFO, LOG_TAG, 
						"handleMessage MSG_GET_BARCODE_LICENSE_RESP tStatus:"+ responsor.status + ", memo:"+memo);
				//和服务端交互出错
				mErrorType = NO_ERROR;

				getDataHelper().showDialog(this, R.drawable.infoicon, 
						getResources().getString(R.string.WarngingString), memo, 
						getResources().getString(R.string.Ensure), btnForError, 
						null, null, null, null);	
			} 
		}
	}
	
	private void handelDealData(Message msg){
		JSONObject jsonResponse = null;
		
		String dealString = (String) msg.obj;
		try {
			jsonResponse = new JSONObject(dealString);
			if (jsonResponse != null) {
				mCurTradeNo = jsonResponse.optString(Constant.RPF_TRADENO);
				mCurTradeAct = jsonResponse.optString(Constant.RPF_PUSH_TRADE_ACTION);
				LogUtil.logMsg(Constant.LOG_LEVEL_DEBUG, LOG_TAG, "dealString="+jsonResponse.toString());
				
				if (mErrorType != BARCODE_SAFEPAY_START) {
					((Vibrator) getSystemService(VIBRATOR_SERVICE)).vibrate(Constant.DEFAULT_VIBRATE_PATTERN, -1);
				}
				
				if (mCurTradeAct != null && mCurTradeAct.equals(Constant.RPF_PUSH_TRADE_ACT_PAY)) {
					if (mCurTradeNo != null && !mCurTradeNo.equals("")) {
//						closePushLink();
						if (mErrorType == BARCODE_GET_FINISH) {
							mErrorType = BARCODE_SAFEPAY_START;
							// 5. 得到交易数据，调用快捷支付接口付款
							BaseHelper.payDeal(AlipayBarcodeDisplay.this, mHandler, mProgress, 
									mCurTradeNo, getExtToken(),"","trade", Constant.SAFE_PAY_PAY);
							
							mCurBarcodeTradeNo = mCurTradeNo;
							
							//已经调用了快捷支付，此时终止5分钟条码有效计时器；
							if (mAlarmTimer) {
								stopAlarmTimer();
							} else {
								stopTimer();
							}
							// 时间进度条复位
							mPollingCount = 0;
							mProgressBarTimer.setProgress(0);
							
							//并且中断长连接，以防再次收到交易，因为同一时间只处理一个交易
//							stopPushService();
						} else {
							//等待付款，但是交易号有误——无效的交易号
						}
					}
				} else if (mCurTradeAct != null 
						&& (mCurTradeAct.equals(Constant.RPF_PUSH_TRADE_ACT_FINISH)
								|| mCurTradeAct.equals(Constant.RPF_PUSH_TRADE_ACT_CANCEL))) {
					String memo = jsonResponse.optString(Constant.RPF_MEMO);
					String title = getString(R.string.barcode_deal_notice);
					int icon = -1;
					if (mCurTradeAct.equals(Constant.RPF_PUSH_TRADE_ACT_FINISH)) {
						icon = R.drawable.ok;
						title = getString(R.string.GetMoneyResultSuccess);
					} else if (mCurTradeAct.equals(Constant.RPF_PUSH_TRADE_ACT_CANCEL)) {
						icon = R.drawable.erroricon;
					} else {
						icon = R.drawable.infoicon;
					}
//					closePushLink();
					if (mErrorType != BARCODE_SAFEPAY_START) { //用户正在处于付款过程，不弹出对话框	
						//已有结果的交易数据，直接显示给用户
					    getDataHelper().showDialog(this, icon, title, memo, 
								getResources().getString(R.string.Ensure),
								new DialogInterface.OnClickListener() {
									@Override public void onClick(DialogInterface dialog, int which)
									{
										//退出条码支付应用
										showDesktop();
									}
								}, null, null, null, null);	
					}
					
					//交易已经完成，停止条码计时器
					if (mAlarmTimer) {
						stopAlarmTimer();
					} else {
						stopTimer();
					}
			    } else {
//					mErrorType = NO_ERROR;	
				}
				
			}
		}
		catch (JSONException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
	}
	
	private void checkPushService() {
		mCurPushStatus = getPushServiceStatus();
		LogUtil.logMsg(Constant.LOG_LEVEL_DEBUG, LOG_TAG, 
				"handleMessage MSG_CHECK_PUSHSERVICE_STATUS curPushStatus:"+mCurPushStatus);
		
		switch (mCurPushStatus) {
			case AlipayPushLinkService.SERVICE_NEW:
			case AlipayPushLinkService.SERVICE_CREATED:
				//调用push service的接口，调整服务状态
				if (this.mIsBound == true && mService != null) {
					openProcessDialog(null, getString(R.string.BarcodePushCheck));
//					mReInitPushService = true;
//					
//					try {
//						mService.initializePushLink(0, Constant.STR_BARCODETOKEN);
//					} catch (RemoteException e) {
//		                Log.e(LOG_TAG,"try to initialize push Service failed");
//		            }
				} 
				break;
			case AlipayPushLinkService.SERVICE_INITED:
				mErrorType = BARCODE_PUSHLINK_FINISH;
				int mState = curUserState.getUserState();
				LogUtil.logMsg(Constant.LOG_LEVEL_INFO, LOG_TAG, 
						"handleMessage MSG_GET_BARCODE_LICENSE_RESP mErrorType:"+mErrorType 
						+ ", mState:"+mState +", mBarcodeKey="+mBarcodeKey);
				
				if (AlipayUserState.ACCOUNT_STATE_AUTH_SAFEPAY == mState) {	//认证未登录
					if (mBarcodeKey != null && !mBarcodeKey.equals("")) {
						mErrorType = BARCODE_GET_FINISH;
						//已经获取到二维码
						getBarcodeImg(mBarcodeKey);
						
						if (mAlarmTimer) {
							startAlarmTimer();
						} else {
							startTimer();
						}
					} else {
						//快捷支付已经激活，但是还未获取barcodeKey
						String validAcount = curUserState.getValidAcount();
						getBarcodeByLicense("true", validAcount);
					}
				} else {	//登录且认证——现在长连接仅在认证后建立
					if (mErrorType == BARCODE_PUSHLINK_FINISH) {
						mErrorType = BARCODE_GET_START;
						getBarcodeKey();
					}
				}
				break;
		}
	}
	
//	private void stopPushService() {
//		int curPushStatus = getPushServiceStatus();
//		BaseHelper.LogMsg(Constant.LOG_LEVEL_DEBUG, LOG_TAG, "stopPushService curPushStatus:"+curPushStatus);
//		
//		switch (curPushStatus) {
//			case AlipayPushLinkService.SERVICE_INITED:
//				//调用push service的接口，调整服务状态
//				if (mIsBound == true && mService != null) {
//					try {
//						mService.closePushLink();
//					} catch (RemoteException e) {
//						BaseHelper.LogMsg(Constant.LOG_LEVEL_ERROR, LOG_TAG,"try to close push Service failed");
//		            }
//				} 
//				break;
//		}
//	}
	
	private void changeBackImgforBarcode() {
		String tNewIndex = "0";
		
		// 获取上次使用的图片序号
		String tIndex = mAlipayDataStore.getString(AlipayDataStore.BARCODE_BACK_IMG);
		if (tIndex != null && !tIndex.equals("")) {
			int tIndexValue = Integer.valueOf(tIndex).intValue();
			// 背景图片序号更新
			tIndexValue = tIndexValue + 1;
			
			if (tIndexValue >= mAllBackImg) {
				tIndexValue = 0;
			}
			
			tNewIndex = String.valueOf(tIndexValue);
			LogUtil.logMsg(Constant.LOG_LEVEL_INFO, LOG_TAG, "handleMessage tNewIndex="+ tNewIndex);
		}
		//转换String到int，此处需要和服务端匹配
		mbackOfBarcode = tNewIndex;
	}

	private void openProcessDialog(String title, String msg) {
		if (mProgress == null) {
			mProgress = new ProgressDiv(this);
			
			mProgress.setTitle(title);
			mProgress.setMessage(msg);
			mProgress.setIndeterminate(false);
			mProgress.setCancelable(false);
			mProgress.show();
		}
	}
	
	private void closeProgress(){
		if (mProgress != null){	
			mProgress.dismiss();
			mProgress=null;
		}
	}

	@Override
	public void onCreate(Bundle savedInstanceState) 
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.alipay_barcode_display_320_480);
		
		
		this.mAlipayDataStore = new AlipayDataStore(this);
		
		this.am = (AlarmManager)getSystemService(ALARM_SERVICE);
		Intent intent = new Intent();
		intent.setAction(BARCODE_DISPLAY_ALARM_TIMER);
		this.sender = PendingIntent.getBroadcast(this, 0, intent, 0);
		
//		mBarcodeDrawable = null;
//		mBackDrawable = null;
		this.mRotate2DImg = null;
		this.mBackIndex = -1;
		this.mBarcodeKey = null;
		this.Barcode1DImg = null;
		this.Barcode2DImg = null;
		
		this.mGestureDetector = new GestureDetector(this, this); 
		
		loadAllVariables();
		
		unbindToPushService();
		
		//register a Broadcast Intent Receiver
        registerBroadcastReceiver();

        this.mCurPushStatus = -1;
		
		//调整亮度，方便条码的扫描
		brightnessMax();
		detectScreenLevel();
		
		this.mErrorType = NO_ERROR;

		curUserState.reset();
		//先获取本地成功登录账号
		UserInfo tCurUserInfo = getDataHelper().getLatestLoginedUser(this);
		if (tCurUserInfo != null) {	//本地成功登录账号存在
			curUserState.setUserInfo(tCurUserInfo);
			curUserState.setAccountFrom(AlipayUserState.ACCOUNT_TYPE_CLIENT);
			
			if (tCurUserInfo.type != null && tCurUserInfo.type.equals("alipay")) {
				curUserState.setUserState(AlipayUserState.ACCOUNT_STATE_AUTH_PREPARE);
			}
		}
		curUserState.updateUserState(getUserData()!=null);

		//先检查快捷支付版本
//		if (mspHelper.detectSafepayUpdate(mActivity, 1)) {
//			//需要安装快捷支付
//			mErrorType = BARCODE_SAFEPAY_UPDATE_START;
//		} else {
//			mErrorType = BARCODE_SAFEPAY_UPDATE_FINISH;
//		}

		mUserName = curUserState.getUserAccount();
		int mState = curUserState.getUserState();
		switch (mState) {
			case AlipayUserState.ACCOUNT_STATE_AUTH_PREPARE:
			case AlipayUserState.ACCOUNT_STATE_AUTH_SAFEPAY:
				if(mUserName != null && !mUserName.equals("")) {
					mTxtCurAccount.setText(getString(R.string.alipay_current_account)+"："+mUserName);
					mTxtCurAccount.setVisibility(View.VISIBLE);
				}
			break;
			
			default:
				//
			break;
		}
		
		LogUtil.logMsg(Constant.LOG_LEVEL_DEBUG, LOG_TAG,
				"onCreate() isLogin="+(getUserData()!=null) +", mUserName="+mUserName);
	}
	
	private void detectScreenLevel()
	{
		DisplayMetrics dmScreen = BaseHelper.getDisplayMetrics(this);
		mScaleHeight = dmScreen.heightPixels;
		mScaleWeight = dmScreen.widthPixels;
		
		switch (mScaleWeight) {
			case 240:
				mbarcodeSizeScale = 0.8f;
				mScaleHeight = 320;
				break;
			case 320:
				mbarcodeSizeScale = 0.65f;
				mScaleHeight = 480;
				break;
			case 480:
				mbarcodeSizeScale = 0.65f;
				mScaleHeight = 800;
				break;
			case 540:
				mbarcodeSizeScale = 0.64f;
				mScaleHeight = 960;
				break;
			default:
				mbarcodeSizeScale = 0.65f;
				break;
		}
		
		mCurScale = (float)mScaleHeight/(float)mScaleWeight;
		LogUtil.logMsg(Constant.LOG_LEVEL_DEBUG, LOG_TAG, 
				"detectScreenLevel widthPixels:"+ mScaleWeight +", heightPixels:"+mScaleHeight
				+ ", scale="+mCurScale);
	}
	
	private void loadAllVariables()
	{
		mTitleName = (TextView) findViewById(R.id.title_text);
		mTitleName.setText(R.string.barcode_pay_title);
		
		mAllLayout = (RelativeLayout) findViewById(R.id.AlipayBarCodeDisplayLayout);
		mAllLayout.setOnTouchListener(this);
		mAllLayout.setLongClickable(true);
		
		mbackLayout = (View) findViewById(R.id.BarCodeDisplayBG);
		mbackLayout.setOnTouchListener(this);
		mbackLayout.setLongClickable(true);
		
		mProgressBarTimer = (ProgressBar)findViewById(R.id.TimerProgressBar);
		mProgressBarTimer.setVisibility(View.VISIBLE);
        mProgressBarTimer.setMax(mTotalDispTime/mPollingInterval);
        mProgressBarTimer.setVisibility(View.INVISIBLE);

		mBarcodeImgView = (ImageView)findViewById(R.id.barcode_img);
		mBarcodeImgView.setOnTouchListener(this);
		mBarcodeImgView.setLongClickable(true);
		
		mBackGroundImg = (ImageView)findViewById(R.id.barcode_background);
		
		mQrcodeImgView = (ImageView)findViewById(R.id.qrcode_img);
		mQrcodeImgView.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if (!IsBarcodeDisplayed) {
					switchtoBarcode();
					mIfDisp1DBarcode = false;
				}
			}
		});
		
		mBarcodeImgLeft = (ImageView)findViewById(R.id.left_point_img);
		mBarcodeImgRight = (ImageView)findViewById(R.id.right_point_img);
		if (mIfDisp1DBarcode) {
			mBarcodeImgLeft.setImageResource(R.drawable.barcode_switch_solid);
			mBarcodeImgRight.setImageResource(R.drawable.barcode_switch_hollow);
		}
		
		mBarCodePromptView = (TextView)findViewById(R.id.bar_code_prompt);
		mBarCodePromptView.setTextColor(getResources().getColor(R.color.TextColorBlue));
		mBarCodePromptView.setTextSize(16.0f);
		mBarCodePromptView.bringToFront();
		LayoutParams params = mBarCodePromptView.getLayoutParams();
		mBarCodePromptView.setLayoutParams(params);
		
		mTxtBarCodeNumber = (TextView)findViewById(R.id.barcode_text);
		mTxtBarCodeNumber.bringToFront();
		
		mTxtCurAccount = (TextView)findViewById(R.id.barcode_curaccount);
		mTxtCurAccount.bringToFront();
		mTxtCurAccount.setVisibility(View.GONE);
		 
		mBtnUserGuide = (Button)findViewById(R.id.ButtonUserGuide);
		mBtnUserGuide.setVisibility(View.INVISIBLE);
		mBtnUserGuide.setTextScaleX(1.2f);
		mBtnUserGuide.setTextColor(getResources().getColor(R.color.TextColorYellow));
		mBtnUserGuide.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				//回到使用向导
				Intent intent = new Intent(AlipayBarcodeDisplay.this, AlipayBarcodePayGuideActivity.class);
				startActivity(intent);
				
				if (mAlarmTimer) {
					stopAlarmTimer();
					am = null;
				}
				finish();
			}
		});
	}
	
	private static final String BARCODE_DISPLAY_ALARM_TIMER = "com.alipay.barcode.display";
	
	private void registerBroadcastReceiver(){
        if (this.mIntentReceiver == null){
        	this.mIntentReceiver = new BroadcastReceiver(){             
                @Override
                public void onReceive(Context context, Intent intent){
                	LogUtil.logMsg(Constant.LOG_LEVEL_WARNING, LOG_TAG, "BarcodePay broadcast Received " + intent.getAction());
                 
                    String action = intent.getAction();
                 
                    if(AlipayPushLinkService.PUSH_LINK_FAILED.equals(action)){                     
                        mHandler.sendEmptyMessage(MSG_BARCODE_PUSHLINK_INIT_FAILED);                     
                    }
                    else if(AlipayPushLinkService.PUSH_LINK_INITIALIZED.equals(action)){
                        mHandler.sendEmptyMessage(MSG_BARCODE_PUSHLINK_INITED);                     
                    } 
                    else if(AlipayPushLinkService.PUSH_LINK_RECONNECT.equals(action)){ 
//                        mHandler.sendEmptyMessage(FM_TUNE_SUCCEED);                 
                    }
                    else if(AlipayPushLinkService.PUSH_LINK_CONTENT.equals(action)){
                    	Bundle appData = intent.getExtras();
                    	String dealJSON = appData.getString("appdata");
                    	LogUtil.logMsg(Constant.LOG_LEVEL_DEBUG, LOG_TAG, "BarcodePay Received dealJSON:"+dealJSON);
                    	
                    	Message dataMsg = mHandler.obtainMessage();
                    	dataMsg.what = MSG_BARCODE_PUSHLINK_DATA;
                    	//JSON: 数据格式
                    	dataMsg.obj = dealJSON;
                        mHandler.sendMessage(dataMsg);                 
                    }
                    else if(BARCODE_DISPLAY_ALARM_TIMER.equals(action)){                     
                        mHandler.sendEmptyMessage(MSG_DISPLAY_ALARM_TIMER);                     
                    }
                }
            };
            
            IntentFilter i = new IntentFilter();
            i.addAction(AlipayPushLinkService.PUSH_LINK_FAILED);
            i.addAction(AlipayPushLinkService.PUSH_LINK_INITIALIZED);
            i.addAction(AlipayPushLinkService.PUSH_LINK_RECONNECT);
            i.addAction(AlipayPushLinkService.PUSH_LINK_CONTENT);
            i.addAction(BARCODE_DISPLAY_ALARM_TIMER);

            registerReceiver(mIntentReceiver, i);
        }
	}
	
	private void brightnessMax() {   
	    WindowManager.LayoutParams lp = getWindow().getAttributes();   
	    LogUtil.logMsg(Constant.LOG_LEVEL_INFO, LOG_TAG,"brightnessMax() get curscreenBrightness:"+lp.screenBrightness);
	    lp.screenBrightness = 1.0f;   
	    getWindow().setAttributes(lp);   
	}
	
	@Override
	protected void onStart() {
		// TODO Auto-generated method stub
		super.onStart();

		if ((mErrorType == BARCODE_GET_FINISH && mCurPushStatus == 1)
				|| mPushLinkLostShow == true || mLaunchClosePushlink == true) {
			//已经获取到条码了，或者已经告知用户长连接建立失败，则不再更新
		} else {
			int mState = curUserState.getUserState();
			if (mState == AlipayUserState.ACCOUNT_STATE_NO_LOGIN) {
				gotoLogin(true);
			}
			else{
				//先检查快捷支付版本
				if (mspHelper.detectSafepayUpdate(this, 1, mBtnForCancel)) {
					//需要安装快捷支付
					mErrorType = BARCODE_SAFEPAY_UPDATE_START;
				} else if (!Constant.mWapSupport && BaseHelper.isWapApn(this)) {
					mHandler.sendEmptyMessage(MSG_BARCODE_WAP_UNSUPPORT);
				} else if (mErrorType >= BARCODE_SAFEPAY_UPDATE_START){
					curUserState.updateSafepayAuthList(this, mHandler);
//					curUserState.updateUserState(Constant.isLogin);
					mErrorType = BARCODE_SAFEPAY_UPDATE_FINISH;
				}
			}
		}
		LogUtil.logMsg(Constant.LOG_LEVEL_DEBUG, LOG_TAG, "onStart is called."); 
	}

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		LogUtil.logMsg(Constant.LOG_LEVEL_DEBUG, LOG_TAG,"onResume() called");
		super.onResume();

		if (mErrorType == BARCODE_SAFEPREPAY_START && mWorkAround) {
			//已经调用快捷支付，但是快捷支付没有返回结果却回到了条码页面
			//解决：弹出一个短时间的progressbar即可
			if (mTempProgress == null) {
				mTempProgress = new ProgressDialogNew(this);
				
				mTempProgress.setTitle(null);
				mTempProgress.setMessage(getString(R.string.BarcodeSafepayResult));
				mTempProgress.setIndeterminate(false);
				mTempProgress.setCancelable(true);
				mTempProgress.show();
			}

			LogUtil.logMsg(Constant.LOG_LEVEL_DEBUG, LOG_TAG,"onResume() show BarcodeSafepayResult! " +
					", mShowProgressbarforSafePay="+mShowProgressbarforSafePay);
			
			mShowProgressbarforSafePay = true;
			
			mHandler.sendEmptyMessageDelayed(MSG_DISPLAY_BLANK_HANDLE, 1000);
		}
		
		if (mPushLinkLostAlertShow == false && mPushLinkLostShow == true) {
			mPushLinkLostAlertShow = true;
			getDataHelper().showDialog(this, R.drawable.infoicon, 
					getResources().getString(R.string.WarngingString),
					getResources().getString(R.string.BarcodePushStatusError), 
					getResources().getString(R.string.Ensure),
					new DialogInterface.OnClickListener() {
						@Override public void onClick(DialogInterface dialog, int which)
						{
							//退出条码支付应用
							showDesktop();
						}
					}, null, null, null, null);	
		}
	}
	
	@Override
    protected void onPause() {
        // TODO Auto-generated method stub
        super.onPause();
        LogUtil.logMsg(Constant.LOG_LEVEL_DEBUG, LOG_TAG,"onPause() called");
    }

	@Override
	protected void onStop() {
		// TODO Auto-generated method stub
		super.onStop();
		LogUtil.logMsg(Constant.LOG_LEVEL_DEBUG, LOG_TAG,"onStop() called");
//		stopTimer();
	}
	
	@Override
    protected void onDestroy() {
        // TODO Auto-generated method stub
		LogUtil.logMsg(Constant.LOG_LEVEL_DEBUG, LOG_TAG,"onDestroy() called.");
		if (mAlarmTimer) {
			stopAlarmTimer();
		} else {
			stopTimer();
		}
		// 时间进度条复位
		mPollingCount = 0;
		mProgressBarTimer.setProgress(0);
		
		mErrorType = NO_ERROR;
		mShowProgressbarforSafePay = false;
		mTempProgress = null;
		
//		mBarcodeDrawable = null;
//		mBackDrawable = null;
		if (mRotate2DImg != null) {
			mRotate2DImg.recycle();
			mRotate2DImg = null;
		}

		if (Barcode1DImg != null) {
			Barcode1DImg.recycle();
			Barcode1DImg = null;
		}
		
		if (Barcode2DImg != null) {
			Barcode2DImg.recycle();
			Barcode2DImg = null;
		}
		
		mBackIndex = -1;
		mBarcodeKey = null;
		
		mCurBarcodeTradeNo = null;
		mCurTradeNo = null;
		mCurTradeAct = null;
		
		closeProgress();
		
        super.onDestroy();
        
        /*
        unbindToPushService();
//        mReInitPushService = false;
        mPushLinkLostShow = false;
        mPushLinkLostAlertShow = false;
        */
        
        //Unregister the Broadcast Intent Receiver
        unregisterReceiver(mIntentReceiver);
        
        LogUtil.logMsg(Constant.LOG_LEVEL_DEBUG, LOG_TAG,"onDestroy() done.");
    }
	
	/**
	 * 登录
	 * @param back 登录后是否返回条码
	 */
	private void gotoLogin(boolean back) {
		Intent tIntent = null;
		
		tIntent = new Intent(this, Login.class);
		tIntent.setData(Uri.parse(getComponentName().getClassName()));
		startActivityForResult(tIntent, Constant.REQUEST_LOGIN_BACK);
		
		if (mAlarmTimer) {
			stopAlarmTimer();
			am = null;
		}
		finish();
	}

	public void ShowProgressBar(boolean bShow)
	{
//		ProgressBar pb = (ProgressBar)findViewById(R.id.BalanceProgressBar);
//		pb.setVisibility(bShow ? View.VISIBLE : View.GONE);
	}

	DialogInterface.OnClickListener btnForOk = new DialogInterface.OnClickListener(){
		@Override
		public void onClick(DialogInterface dialog, int which) {
			if (mAlarmTimer) {
				stopAlarmTimer();
				am = null;
			}
			finish();
		}
	};
	
	private DialogInterface.OnClickListener mBtnForCancel = new DialogInterface.OnClickListener() {
		@Override public void onClick(DialogInterface dialog, int which)
		{
			showDesktop();
		}
	};

	public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
        	LogUtil.logMsg(Constant.LOG_LEVEL_INFO, LOG_TAG, "onKeyDown KEYCODE_BACK!");
        	
        	if (mErrorType > BARCODE_SAFEPREPAY_START 
        			|| (mErrorType <= BARCODE_SAFEPREPAY_START && mErrorType > BARCODE_PUSHLINK_START)
        			|| mErrorType <= BARCODE_GET_FINISH) {
//        	if (mErrorType <= BARCODE_GET_FINISH) {	//非等待扫描状态，不允许back退出
        		showDesktop();
        	}
			return true;
        } else if (keyCode == KeyEvent.KEYCODE_SEARCH){
        	//捕获此事件，但不做处理
        	return true;
        }
        return false;
    }
	
	private void closePushLink() {
		if (mCurPushStatus == AlipayPushLinkService.SERVICE_INITED) {
			//发送关闭连接请求
			if (this.mIsBound == true && mService != null) {
				//获取push service的状态
				try {
					mService.closePushLink();
					mLaunchClosePushlink = true;
				} catch (RemoteException e) {
					LogUtil.logMsg(Constant.LOG_LEVEL_WARNING, LOG_TAG,"closePushLink of push Service failed");
	            }
			} 
		}
	}
	
	private void clearPushLink() {
		unbindToPushService();
//      mReInitPushService = false;
		this.mPushLinkLostShow = false;
		this.mPushLinkLostAlertShow = false;
	}

	public void onActivityResult(int requestCode, int resultCode, Intent intent) {
	    super.onActivityResult(requestCode, resultCode, intent);
		LogUtil.logMsg(Constant.LOG_LEVEL_DEBUG, LOG_TAG, 
				"onActivityResult() called. requestCode" + requestCode
				+ ", resultCode" + resultCode);
//		String validStr = null;
		//复位时间进度条
		mPollingCount = 0;
		mProgressBarTimer.setProgress(0);
		
		if (requestCode == Constant.REQUEST_LOGIN_BACK) {
			if (resultCode == RESULT_OK) {
				//登录顺利返回，仍在当前页面
				//重新申请条码动态串
				getBarcodeKey();
			} else {
				//没有成功登录，则退出应用
				if (mAlarmTimer) {
					stopAlarmTimer();
					am = null;
				}
				finish();
			}
		}
	}

	public static void startBarcodeDisplayActivity(Context packageContext) {
	    Intent intent = new Intent(packageContext, AlipayBarcodeDisplay.class);
	    packageContext.startActivity(intent);
	}
	
//	private void switchCodeViewType() {
//		if (mIfDisp1DBarcode) {
//	    	switchtoBarcode();
//			mIfDisp1DBarcode = false;
//		} else {
//			switchtoQrcode();
//            mIfDisp1DBarcode = true;
//		}
//	}
	
	private void switchtoBarcode() {
		mBarcodeImgLeft.setImageResource(R.drawable.barcode_switch_solid);
		mBarcodeImgRight.setImageResource(R.drawable.barcode_switch_hollow);
		
		mBarcodeImgView.setVisibility(View.VISIBLE);
		mBarcodeImgView.setImageBitmap(Barcode1DImg);
		
		mBackGroundImg.setVisibility(View.VISIBLE);
		mBackGroundImg.setImageResource(mBackIndex);
//		mbackLayout.setBackgroundResource(mBackIndex);
		
		/*
		if (mLowLevelScreen) {
			mBarcodeImgView.setImageBitmap(Barcode1DImg);
		} else {
			if (mBackDrawable == null) {
				mBackDrawable = new BitmapDrawable(getResources(), Barcode1DImg); 
			} else {
				mBackDrawable.setAlpha(255);
			}
	
			mbackLayout.setBackgroundDrawable(mBackDrawable);
			
			if (mBarcodeDrawable == null) {
				Bitmap backBmp = BitmapFactory.decodeResource(getResources(), mBackIndex);
				mBarcodeDrawable = new BitmapDrawable(getResources(), backBmp);  
			}
			mBarcodeImgView.setBackgroundDrawable(mBarcodeDrawable);
		}
		*/
		mQrcodeImgView.setVisibility(View.INVISIBLE);

		IsBarcodeDisplayed = true;
	}
	
	private void switchtoQrcode() {
		if (Barcode2DImg == null) {	//二维码还未生成
			return;
		}
		
		mBarcodeImgLeft.setImageResource(R.drawable.barcode_switch_hollow);
		mBarcodeImgRight.setImageResource(R.drawable.barcode_switch_solid);
		
		mQrcodeImgView.setVisibility(View.VISIBLE);
		
		/*
		mRotate2DImg  = Barcode2DImg;
		
		if (mRotate2DImg == null && Barcode2DImg != null) {
			int width = Barcode2DImg.getWidth();  
	        int height = Barcode2DImg.getHeight(); 

			Bitmap newImg = Bitmap.createBitmap(Barcode2DImg, 0, 0, width, height);
	
			// Bitmap 旋转
	        Matrix vMatrix = new Matrix();
	        vMatrix.setRotate( 45 );
	
	        mRotate2DImg = Bitmap.createBitmap(newImg, 0, 0, 
	        	newImg.getWidth(), newImg.getHeight(), vMatrix, true);
			mRotate2DImg = newImg;
		}
		
        mQrcodeImgView.setImageBitmap(mRotate2DImg);
        */
        mQrcodeImgView.setImageBitmap(Barcode2DImg);

        mBarcodeImgView.setVisibility(View.INVISIBLE);
        mBackGroundImg.setVisibility(View.INVISIBLE);
		
		IsBarcodeDisplayed = false;
	}
	
	private void showDesktop() {
		closePushLink();
		
		if(Constant.OPERATION_UID != null && this.mBarcodePayRet == true){
            getMBus().sendMsg("", Constant.OPERATION_UID, MsgAction.ACT_LAUNCH, "");
		} else {
		}
		
		if (mAlarmTimer) {
			stopAlarmTimer();
			am = null;
		}
		
		if (this.mCurPushStatus == -1) {
			clearPushLink();
		}
      
		this.mBarcodePayRet = false;
		finish();
	}

	@Override
	  public boolean onTouch(View v, MotionEvent event) {
	      // OnGestureListener will analyzes the given motion event
		  LogUtil.logMsg(Constant.LOG_LEVEL_DEBUG, LOG_TAG, "onTouch event!");
		  if (mGestureDetector.onTouchEvent(event))
			  return true;
		  else
			  return false;  
	  }
	
	// 用户按下触摸屏、快速移动后松开，由1个MotionEvent ACTION_DOWN, 多个ACTION_MOVE, 1个ACTION_UP触发
	@Override
	public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
        // TODO Auto-generated method stub
		final int FLING_MIN_DISTANCE = 100, FLING_MIN_VELOCITY = 100;
		
		LogUtil.logMsg(Constant.LOG_LEVEL_DEBUG, LOG_TAG, "onFling Enter!");
		
		boolean isSwitch = false;
		  
	    // 触发条件 ：  
	    // X轴的坐标位移大于FLING_MIN_DISTANCE，且移动速度大于FLING_MIN_VELOCITY个像素/秒  
	    if (e1.getX() - e2.getX() > FLING_MIN_DISTANCE
	            && Math.abs(velocityX) > FLING_MIN_VELOCITY) {
	        // Fling left
	    	LogUtil.logMsg(Constant.LOG_LEVEL_DEBUG, LOG_TAG, "Fling Left");
		    isSwitch = true;
	    } else if (e2.getX() - e1.getX() > FLING_MIN_DISTANCE
	            && Math.abs(velocityX) > FLING_MIN_VELOCITY) {
	        // Fling right
	    	LogUtil.logMsg(Constant.LOG_LEVEL_DEBUG, LOG_TAG, "Fling Right");
	    	isSwitch = true;
	    }
	    
	    if (isSwitch) {
	    	//切换条码显示
//	    	switchCodeViewType();
	    }
	    
		return false;
    }
	
	// 用户长按触摸屏，由多个MotionEvent ACTION_DOWN触发
	@Override
	public void onLongPress(MotionEvent e) {
	    // TODO Auto-generated method stub
	}

	// 用户按下触摸屏，并拖动，由1个MotionEvent ACTION_DOWN, 多个ACTION_MOVE触发
	@Override
	public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
	    // TODO Auto-generated method stub
	    return false;
	}
	  
	@Override
	public boolean onDown(MotionEvent e) {
	    // TODO Auto-generated method stub
		LogUtil.logMsg(Constant.LOG_LEVEL_DEBUG, LOG_TAG, "onDown");
		
		if (IsBarcodeDisplayed && mErrorType == BARCODE_GET_FINISH
				&& Barcode2DImg != null) {
			switchtoQrcode();
			mIfDisp1DBarcode = true;
		}
//		else if (!IsBarcodeDisplayed && mErrorType == BARCODE_GET_FINISH
//				&& Barcode1DImg != null) {
//			switchtoBarcode();
//			mIfDisp1DBarcode = false;
//		}
		
	    return false;
	}

	// 用户轻触触摸屏，尚未松开或拖动，由一个1个MotionEvent ACTION_DOWN触发
	// 注意和onDown()的区别，强调的是没有松开或者拖动的状态
	@Override
	public void onShowPress(MotionEvent e) {
	    // TODO Auto-generated method stub
	}

	// 用户（轻触触摸屏后）松开，由一个1个MotionEvent ACTION_UP触发
	@Override
	public boolean onSingleTapUp(MotionEvent e) {
	    // TODO Auto-generated method stub
	    return false;
	}
	  

	/********************************************************************
     * The following codes are for connecting to PushLink service
     */        
        
    private boolean mIsBound = false; //used for identify the service is binded    

    private IAlipayPushLinkService mService = null;
    
    private ServiceConnection mServConnection = new ServiceConnection() {
         public void onServiceConnected(ComponentName className, android.os.IBinder service){
        	 LogUtil.logMsg(Constant.LOG_LEVEL_DEBUG, LOG_TAG, 
        			 "onServiceConnected: PushLink service Connected! ");
              
             mService = IAlipayPushLinkService.Stub.asInterface(service);
             mIsBound = true;
         }
        
         public void onServiceDisconnected(ComponentName className){
        	 LogUtil.logMsg(Constant.LOG_LEVEL_DEBUG, LOG_TAG, 
        			 "onServiceDisconnected: PushLink service disconnected!");
             mService = null;
             finish();
         }
    };

    private boolean bindToPushService(boolean show) {
    	LogUtil.logMsg(Constant.LOG_LEVEL_DEBUG, LOG_TAG, "Start to bind to PushLink service");
//        startService(new Intent(this, AlipayPushLinkService.class));
        if (show) {
        	openProcessDialog(null, getString(R.string.BarcodePushCheck));
        }
        
        return getApplicationContext().bindService((new Intent(this,AlipayPushLinkService.class)), mServConnection, Context.BIND_AUTO_CREATE);
    }
    
    private void unbindToPushService(){
    	LogUtil.logMsg(Constant.LOG_LEVEL_DEBUG, LOG_TAG, "Start to unbind to PushLink service");
        if (mService != null && this.mIsBound){
        	getApplicationContext().unbindService(mServConnection);
            this.mIsBound = false;
        }
        LogUtil.logMsg(Constant.LOG_LEVEL_DEBUG, LOG_TAG, "done-unbind to PushLink service");
        
        getApplicationContext().stopService(new Intent(this, AlipayPushLinkService.class));
        LogUtil.logMsg(Constant.LOG_LEVEL_DEBUG, LOG_TAG, 
        		"unbindToPushService to stopService PushLink service");
    }

	
}
