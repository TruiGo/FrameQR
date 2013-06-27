package com.alipay.android.ui.quickpay;


import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import org.json.JSONObject;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.media.AudioManager;
import android.os.Build;
import android.os.RemoteException;
import android.os.Vibrator;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;
import android.view.animation.RotateAnimation;
import android.view.animation.ScaleAnimation;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.ImageView.ScaleType;

import com.alipay.android.appHall.CacheManager;
import com.alipay.android.appHall.appManager.HallData;
import com.alipay.android.biz.QuickPayBiz;
import com.alipay.android.client.Login;
import com.alipay.android.client.constant.Constant;
import com.alipay.android.client.util.AlipayDataStore;
import com.alipay.android.client.util.BaseHelper;
import com.alipay.android.log.AlipayLogAgent;
import com.alipay.android.log.Constants;
import com.alipay.android.longlink.ISocketResultNotifer;
import com.alipay.android.longlink.LongLinkServiceManager;
import com.alipay.android.servicebeans.CheckPhoneBlack;
import com.alipay.android.servicebeans.GetDynamicId;
import com.alipay.android.ui.fragment.FragmentHelper;
import com.alipay.android.ui.framework.BaseViewController;
import com.alipay.android.ui.voucher.VerifyResultNotifier;
import com.alipay.android.util.JsonConvert;
import com.alipay.sonicwavenfc.SonicWaveNFC;
import com.alipay.sonicwavenfc.SonicWaveNFCHandler;
import com.eg.android.AlipayGphone.R;

public class SendSoundViewController extends BaseViewController implements OnClickListener,VerifyResultNotifier{
	private TextView mUserPrompt = null;
	private TextView mConnecttingText = null;
	private ImageView mSendSound = null;
	private TextView mTitleView = null;
	private ImageView mTitleShadow;
	private ImageView mHelp = null;
	private ImageView mGuideView;
	private ImageView mLoadingCircle;
	private ImageView mQuickpayHelpText;
	private ImageView mRippleImage1;
	private ImageView mRippleImage2;
	private ImageView mRippleImage3;
	private SonicWaveNFC mSonicWaveNFC;
	private LongLinkServiceManager longLinkServiceManager;
	private HeadSetReceiver headSetReceiver;
	private FragmentHelper fragmentHelper;
	private String mDynamicId = "";
	private BizAsyncTask mGetDynamicIdTask;
	private BizAsyncTask mCheckPhoneBlackTask;
	private RotateAnimation mRotateAnimation;
	private ScaleAnimation mScaleAnimationInner;
	private ScaleAnimation mScaleAnimationoutter;
	private boolean isNeedRegetID = false;
	private boolean sendButtonIsUnable = false;
	private boolean continueDealLongLink = true;;
	private boolean misHeadSetOn = false;
	private boolean firstTime = false;
	private Timer timer = new Timer(); 
	private MyTimerTask mMyTimerTask = null;
	private  CacheManager mCacheManager = CacheManager.getInstance(getRootController());
	SimpleDateFormat   sDateFormat   =   new   SimpleDateFormat("ssSSS"); 
	private long createTime;
	private long sendTime;
	private String mBillNumber = "";//对于c2b如果长连接返回的数据不带商品明细，就直接拿订单号唤起安全支付。
	
	
	class MyTimerTask extends TimerTask {
		@Override
		public void run() {
			getRootController().runOnUiThread(new Runnable() {
				@Override
				public void run() {
					mSendSound.setClickable(true);
					mSendSound
							.setBackgroundResource(R.drawable.sendsound_button_bg);
					mLoadingCircle.clearAnimation();
					mLoadingCircle.setVisibility(View.GONE);
					mUserPrompt.setText(getRootController().getResources()
							.getString(R.string.searchfailed_notice));
				}
			});
		}
	}
	
	@Override
	protected void onCreate() {
		super.onCreate();
		mView = LayoutInflater.from(this.getRootController()).inflate(R.layout.quickpay_sendsound, null, false);  
        addView(mView,null);
        loadAllVariables();
        init();
	}
	
	private void init() {
		registerHeadsetPlugReceiver();
		upDateNotice();
		// 判断是否是首次使用
		if (fragmentHelper.needShow(AlipayDataStore.QUICKPAY)) {
			showGuide();
		}
		mSonicWaveNFC = SonicWaveNFC.getInstance();
		mSonicWaveNFC.initSonicWaveNFC(getRootController());// 实例构造后，需要检测耳机是否 插入
		if (getRootController().getUserData() != null) {
			logLaunch();
			prepareLongLink();
			if ("".equalsIgnoreCase(mDynamicId)) {
				getDynamicId();
				mConnecttingText.setVisibility(View.VISIBLE);
			}
			if (!mCacheManager.get("isSetSurpport").equalsIgnoreCase("true")) {
				checkPhoneBlack();
			}
		}
	}
	
	private void prepareLongLink(){
		longLinkServiceManager.bindService(this,null);
	}
	
	private void checkLongLink() {
		if (longLinkServiceManager != null
				&& !longLinkServiceManager.isSocketConnected()) {
			if (!longLinkServiceManager.isBindService()) {
				longLinkServiceManager.bindService(this, null);
			} else {
				// 设置Socket连接监听
				longLinkServiceManager
						.setSocketListener(new ISocketResultNotifer.Stub() {
							// socket建立成功
							@Override
							public void onSocketCreateSuccess()
									throws RemoteException {
								closeProgressAndSendWave();
							}

							// socket建立失败
							@Override
							public void onSocketCreateFail()
									throws RemoteException {
								closeProgressAndSendWave();
							}

							private void closeProgressAndSendWave() {
								closeProgress();
							}
						});
				longLinkServiceManager.reConnect();
			}
		} else {
		}
	}
	
	private void checkPhoneBlack(){
		mCheckPhoneBlackTask = new BizAsyncTask(CheckPhoneBlack.BIZ_TAG,false);
		mCheckPhoneBlackTask.execute(Build.MANUFACTURER,Build.MODEL,Build.VERSION.RELEASE);
	} 
	
	private void logLaunch(){
		String mPkgId = "09999989";
		List<String> sortedIds = Arrays.asList(HallData.sordedIdArray);
		int i = sortedIds.indexOf(mPkgId);
		if (sortedIds.contains(mPkgId)) {
			AlipayLogAgent.writeLog(getRootController(), Constants.BehaviourID.BIZLAUNCHED, null, null, mPkgId, null, mPkgId+"Home", Constants.WALLETHOME,
					"homeApp" + (i + 1) + "Icon", null);
		} else {
			AlipayLogAgent.writeLog(getRootController(), Constants.BehaviourID.BIZLAUNCHED, null, null, mPkgId, null, mPkgId+"Home", Constants.APPCENTER,
					mPkgId + "Icon", null);
		}
	}
	
	
	private void loadAllVariables() {
		fragmentHelper = new FragmentHelper(getRootController());
		longLinkServiceManager = LongLinkServiceManager.getInstance(getRootController(),null);
		mConnecttingText = (TextView)findViewById(R.id.connecttingText);
		mTitleView = (TextView) findViewById(R.id.title_text);
		mTitleView.setText(getRootController().getResources().getString(R.string.quickpay));
		mTitleShadow = (ImageView) findViewById(R.id.TitleShadow);
		mTitleShadow.setVisibility(View.GONE);
		mUserPrompt = (TextView) findViewById(R.id.userPrompt);
		mSendSound = (ImageView) findViewById(R.id.sendSound);
		mSendSound.setOnClickListener(this);
		mSendSound.setClickable(false);
		
		mRippleImage1 = (ImageView) findViewById(R.id.rippleImage1);
		mRippleImage2 = (ImageView) findViewById(R.id.rippleImage2);
		mRippleImage3 = (ImageView) findViewById(R.id.rippleImage3);
		
		mLoadingCircle = (ImageView) findViewById(R.id.imageLoading);
		mHelp = (ImageView)findViewById(R.id.quickpayHelp);
		mHelp.setOnClickListener(this);
		mGuideView = (ImageView) findViewById(R.id.guideView);
		mGuideView.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
					dismissGuide();
				}
		});
		mQuickpayHelpText = (ImageView) findViewById(R.id.quickpayHelpText);
		initAnimation();
	}
	
	private void initAnimation(){
		
		mRotateAnimation = new RotateAnimation(0,360,Animation.RELATIVE_TO_SELF,0.5f,Animation.RELATIVE_TO_SELF,0.5f);
		mRotateAnimation.setDuration(1400);
		mRotateAnimation.setFillAfter(true); 
		mRotateAnimation.setRepeatMode(Animation.RESTART);//重复
		mRotateAnimation.setRepeatCount(Animation.INFINITE);//无限次
        LinearInterpolator lir = new LinearInterpolator();  
        mRotateAnimation.setInterpolator(lir); 
		
        mScaleAnimationoutter =new ScaleAnimation(1.2f, 1.8f, 1.2f, 1.8f,   
                Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);  
        mScaleAnimationoutter.setFillAfter(true); 
        mScaleAnimationoutter.setRepeatMode(Animation.RESTART);//重复
        mScaleAnimationoutter.setRepeatCount(Animation.INFINITE);//无限次
        LinearInterpolator lir1 = new LinearInterpolator();  
        mScaleAnimationoutter.setInterpolator(lir1); 
        mScaleAnimationoutter.setDuration(1500);//设置动画持续时间   
        
        mScaleAnimationoutter =new ScaleAnimation(1.0f, 1.2f, 1.0f, 1.2f,Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);  
        mScaleAnimationoutter.setFillAfter(true); 
        mScaleAnimationoutter.setRepeatMode(Animation.RESTART);//重复
        mScaleAnimationoutter.setRepeatCount(Animation.INFINITE);//无限次
        LinearInterpolator lir2 = new LinearInterpolator();  
        mScaleAnimationoutter.setInterpolator(lir2); 
        mScaleAnimationoutter.setDuration(800);//设置动画持续时间 
	}
	
	
	private void operateRipple(boolean cancle){
		if(cancle){
			mRippleImage1.setVisibility(View.GONE);
			mRippleImage2.setVisibility(View.GONE);
			mRippleImage3.setVisibility(View.GONE);
			mRippleImage3.clearAnimation();
			mRippleImage2.clearAnimation();
			mRippleImage1.clearAnimation();
		}else{
			mRippleImage1.setVisibility(View.VISIBLE);
			mRippleImage2.setVisibility(View.VISIBLE);
			mRippleImage3.setVisibility(View.VISIBLE);
			mRippleImage1.setAnimation(mScaleAnimationInner);
			mRippleImage2.setAnimation(mScaleAnimationoutter);
			mRippleImage3.setAnimation(mScaleAnimationoutter);
		}
	}

	
	private void getDynamicId(){
		sendButtonIsUnable = true;
		mConnecttingText.setVisibility(View.VISIBLE);
		mUserPrompt.setVisibility(View.INVISIBLE);
		mSendSound.setClickable(false);
		mSendSound.setBackgroundResource(R.drawable.quickpay_sendsound_unable);
		mLoadingCircle.setVisibility(View.VISIBLE);
		mLoadingCircle.startAnimation(mRotateAnimation);
		mGetDynamicIdTask = new BizAsyncTask(GetDynamicId.BIZ_TAG, false);
		mGetDynamicIdTask.execute();
	}
	
	@Override
	public void onClick(View v) {
		int viewId = v.getId();
		switch (viewId) {
		case R.id.sendSound:
			if (isHeadsetOn(getRootController())) {
				upDateNotice();
			} else {
				sendTime = System.currentTimeMillis();
				if (isNeedRegetID) {
					mUserPrompt.setVisibility(View.INVISIBLE);
					mConnecttingText.setVisibility(View.VISIBLE);
					checkLongLink();
					getDynamicId();
				} else {
					if ((sendTime - createTime) >(1000 * 60 * 4)) {// 超过了四分钟
						// mUserPrompt 提示用户 网络异常，点击重试
						mUserPrompt.setVisibility(View.VISIBLE);
						mQuickpayHelpText.setVisibility(View.VISIBLE);
						mUserPrompt
								.setText(getRootController()
										.getResources()
										.getString(
												R.string.quickpay_loadfailed_notice));
						isNeedRegetID = true;
						mSendSound
								.setBackgroundResource(R.drawable.quickpay_reget_dynamicid);
					} else {
						checkLongLink();
						sendSoundWave(mDynamicId);
					}
				}
			}
			break;
		case R.id.quickpayHelp:
			showGuide();
			break;
		default:
			break;
		}
	}
	
	private void dismissGuide() {
		mGuideView.setVisibility(View.GONE);
		fragmentHelper.hideGuide(AlipayDataStore.QUICKPAY);
	}
	
	private void showGuide(){
		mGuideView.setImageResource(R.drawable.quickpay_guide);
		mGuideView.setScaleType(ScaleType.FIT_XY);
    	mGuideView.setVisibility(View.VISIBLE);
	}
	
	private void sendSoundWave(String dynamicId) {
		if (dynamicId != null && !dynamicId.equalsIgnoreCase("")) {
			// 设置混音模式时的声音文件
			mSonicWaveNFC.setBkSoundWave4MixFromAsset(getRootController(),
					"SendingData.wav");
			// 设置混音模式（0，超声；1，增强有声；2，混音）
			int iSoundType = SonicWaveNFC.NFC_SENDDATA_SOUNDTYPE_MIX;
			int iTimeoutSeconds = 5;
			int iVolume = 50; // 40%音量
			// 发送数据
			try {
				mQuickpayHelpText.setVisibility(View.GONE);
				mUserPrompt.setVisibility(View.VISIBLE);
				mUserPrompt.setText(getRootController().getResources()
						.getString(R.string.searching));
				mLoadingCircle.setAnimation(mRotateAnimation);
				operateRipple(false);
				mSonicWaveNFC.startSendData(dynamicId, iTimeoutSeconds,
						iSoundType, iVolume, getRootController(),
						mSonicWaveNFCHandler);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	@Override
	protected void onResume() {
		super.onResume();
		continueDealLongLink = true;
		mQuickpayHelpText.setVisibility(View.VISIBLE);;
	}
	
	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (requestCode == Constant.REQUEST_LOGIN_BACK
				&& resultCode == Activity.RESULT_OK) {
			logLaunch();
			longLinkServiceManager.unBindService();
			prepareLongLink();
			if (!mCacheManager.get(Constant.QUICKPAY_ISSETSURPPORT)
					.equalsIgnoreCase("true")) {
				checkPhoneBlack();
			}
			if ("".equalsIgnoreCase(mDynamicId)) {
				getDynamicId();
				mConnecttingText.setVisibility(View.VISIBLE);
			}
		}
		super.onActivityResult(requestCode, resultCode, data);
	}

	@Override
	protected Object doInBackground(String bizType, String... params) {
		if (GetDynamicId.BIZ_TAG.equalsIgnoreCase(bizType)
				|| "backGroundGetDynamicId".equalsIgnoreCase(bizType)) {
			return new QuickPayBiz().getDynamicId();
		} else if (C2BConfirmViewController.C2CPAY.equals(bizType)) {
			// closeProgress();
			BaseHelper.payBizDeal(getRootController(), null, mBillNumber,
					getRootController().getExtToken(), null, "trade", "");
		} else if (CheckPhoneBlack.BIZ_TAG.equalsIgnoreCase(bizType)) {
			return new QuickPayBiz().checkPhoneBlack(params[0], params[1],
					params[2]);
		}
		return super.doInBackground(bizType, params);
	}

	@Override
	protected void onPostExecute(String bizType, Object result) {
		if(GetDynamicId.BIZ_TAG.equalsIgnoreCase(bizType)){
			mConnecttingText.setVisibility(View.GONE);
			mSendSound.setClickable(true);
			sendButtonIsUnable = false;
			mLoadingCircle.setVisibility(View.GONE);
			mLoadingCircle.clearAnimation();
			JSONObject responseJson = JsonConvert.convertString2Json((String)result);
			if(responseJson != null && responseJson.optString(Constant.RPF_RESULT_STATUS).equalsIgnoreCase("100")){
				isNeedRegetID = false;
				mDynamicId = responseJson.optString(Constant.QUICKPAY_DYNAMICID, "");
				createTime = System.currentTimeMillis();//记下获取ID的时间，以便客户端判断超时。
				upDateNotice();
			}else if(responseJson != null && responseJson.optString(Constant.RPF_RESULT_STATUS).equalsIgnoreCase("200")){
				getRootController().getDataHelper().showDialog(getRootController(),R.drawable.infoicon,getRootController().getResources().getString(R.string.WarngingString), 
						getRootController().getResources().getString(R.string.PucPayReLogin), getRootController().getResources().getString(R.string.Ensure),new DialogInterface.OnClickListener() {
							@Override
							public void onClick(DialogInterface dialog,
									int which) {
								mDynamicId = "";
								mSendSound.setClickable(true);
								backToLogin();
							}
						}, null, null, null, null);
				 
			}else{
				mSendSound.setBackgroundResource(R.drawable.quickpay_reget_dynamicid);
				mUserPrompt.setVisibility(View.VISIBLE);
				 mUserPrompt.setText(getRootController().getResources().getString(R.string.quickpay_loadfailed_notice));
				isNeedRegetID = true;
			}
		}else if("backGroundGetDynamicId".equalsIgnoreCase(bizType)){
			JSONObject responseJson = JsonConvert.convertString2Json((String)result);
			if(responseJson != null && responseJson.optString(Constant.RPF_RESULT_STATUS).equalsIgnoreCase("100")){
				mDynamicId = responseJson.optString(Constant.QUICKPAY_DYNAMICID, "");
			}else{
			}
		}else if(C2BConfirmViewController.C2CPAY.equals(bizType)){
//			getRootController().finish();
		}else if(CheckPhoneBlack.BIZ_TAG.equalsIgnoreCase(bizType)){
			JSONObject responseJson = JsonConvert.convertString2Json((String) result);
			if(responseJson != null && responseJson.optString(Constant.RPF_RESULT_STATUS).equalsIgnoreCase("100")){
				String blacks = responseJson.optString(Constant.QUICKPAY_BLACKS);
				if(blacks != null && !blacks.equalsIgnoreCase("")){
					mSonicWaveNFC.setPhoneConfig(blacks);
				}
				if(mSonicWaveNFC.isReceiverSoincWave()){
					 mCacheManager.put(Constant.QUICKPAY_ISSURPPORTSOUND, "true");
					 mCacheManager.put(Constant.QUICKPAY_ISSETSURPPORT, "true");
				}else{
					 mCacheManager.put(Constant.QUICKPAY_ISSURPPORTSOUND, "false");
					 mCacheManager.put(Constant.QUICKPAY_ISSETSURPPORT, "true");
				}
			}else{
				mCacheManager.put(Constant.QUICKPAY_ISSETSURPPORT, "false");
			}
		}
		super.onPostExecute(bizType, result);
	}

	private void upDateNotice() {
		if (isHeadsetOn(getRootController())) {
			mUserPrompt.setVisibility(View.VISIBLE);
			mUserPrompt.setText(getRootController().getResources().getString(
					R.string.quickpay_headseton_notice));
			// 声波按钮置为不可用
			mSendSound.setClickable(false);
			mSendSound
					.setBackgroundResource(R.drawable.quickpay_sendsound_unable);
		} else if (sendButtonIsUnable) {
			mUserPrompt.setVisibility(View.INVISIBLE);
			mSendSound.setClickable(false);
			mSendSound
					.setBackgroundResource(R.drawable.quickpay_sendsound_unable);
		} else {
			mUserPrompt.setVisibility(View.INVISIBLE);
			mSendSound.setClickable(true);
			mSendSound.setBackgroundResource(R.drawable.sendsound_button_bg);
		}
	}


	@Override
	public void onPause() {
		super.onPause();
	}
	
	@Override
	public void onDestroy() {
		if(mSonicWaveNFC != null){
			mSonicWaveNFC.stopSendData();
		}
		unRegisterHeadsetPlugReceiver();
		longLinkServiceManager.unBindService();
		super.onDestroy();
		
	}


	@Override
	public void onSuccess(JSONObject response) {
		// 判断是从c2c还是c2b
		mSendSound.setClickable(true);
		if (mMyTimerTask != null) {
			mMyTimerTask.cancel();
		}
		mSonicWaveNFC.stopSendData();
		mLoadingCircle.clearAnimation();
		mLoadingCircle.setVisibility(View.GONE);
		mConnecttingText.setVisibility(View.GONE);
		// mNeedShakeView.clearAnimation();
		if (continueDealLongLink) {
			((Vibrator) getRootController().getSystemService(
					Context.VIBRATOR_SERVICE)).vibrate(
					Constant.DEFAULT_VIBRATE_PATTERN, -1);
			if (response != null) {
				mDynamicId = "";// 收到长连接后清除之前的动态ID，并重新获取动态ID。
				operateRipple(true);
				getDynamicId();
				String action = response
						.optString(Constant.RPF_PUSH_TRADE_ACTION);
				if (action
						.equalsIgnoreCase(Constant.RPF_PUSH_TRADE_ACT_GETGOODSLIST)) {// push的c2b数据
					// c2b数据停止波纹动画。。
					// c2b由于长连接接收的数据有限，目前暂时不推送商品明细，直接推送订单号，然后调起安全支付。
					mBillNumber = response
							.optString(Constant.QUICKPAY_PUSH_TRADENO);
					continueDealLongLink = false;
					new BizAsyncTask(C2BConfirmViewController.C2CPAY, false)
							.execute();
				} else if (action
						.equalsIgnoreCase(Constant.RPF_PUSH_TRADE_ACT_FASTPAY)) {// push的c2c数据
					continueDealLongLink = false;
					getRootController().navigateTo("C2CConfirmView", response);
				}
			}
		}
	}

	@Override
	public void onFail(JSONObject response) {
	}
	
	
	private SonicWaveNFCHandler mSonicWaveNFCHandler = new SonicWaveNFCHandler() {
		
		@Override
		public void onSendDataTimeout() {
			operateRipple(true);
			mMyTimerTask = new MyTimerTask();
			timer.schedule(mMyTimerTask, 5*1000);
		}
		
		@Override
		public void onSendDataStarted() {
			mSendSound.setClickable(false);
			mSendSound.setBackgroundResource(R.drawable.quickpay_sendsound_unable);
			checkLongLink();
		}
		
		@Override
		public void onSendDataInfo(String arg0) {}
		
		@Override
		public void onSendDataFailed(int arg0) {
		}
		
		@Override
		public void onReceiveDataTimeout() {}
		
		@Override
		public void onReceiveDataStarted() {}
		
		@Override
		public void onReceiveDataInfo(String arg0) {}
		
		@Override
		public void onReceiveDataFailed(int arg0) {}
		
		@Override
		public void onDataReceived(String arg0) {}
	};

    /**
     * 直接判断是否插入了外放耳麦。该函数的返回值可能不准确，建议结合  registerHeadsetPlugReceiver 方式
     * @param context
     * @return 是否插入了外放耳麦
     */
    public boolean isHeadsetOn(Context context) {

        try {
            if (((AudioManager) context.getSystemService(Context.AUDIO_SERVICE)).isBluetoothA2dpOn()){
                System.out.println("检测蓝牙耳机被插入");
                return misHeadSetOn = true;
            }
        } catch (Exception e) {
        }

        if (!firstTime) {
            boolean isOn = false;
            try {

                isOn = ((AudioManager) context.getSystemService(Context.AUDIO_SERVICE)).isWiredHeadsetOn();
                misHeadSetOn = isOn;
                firstTime = true;
            } catch (Exception e) {
            }
        }
        return misHeadSetOn;
    }
	
	 //接收耳机拨插时的广播。
	  public class  HeadSetReceiver extends BroadcastReceiver {
	        @Override
	         public void onReceive(Context context, Intent intent) {
	               String action = intent.getAction();
	               if (action.equals(Intent.ACTION_HEADSET_PLUG)) {
	                   if(intent.getIntExtra("state", 0) == 0){
	                	   misHeadSetOn = false;
	                    upDateNotice();
	                   }else{
	                    misHeadSetOn = true;
	                    upDateNotice();
	                   }
	               }
	         }
	 };
	
	/**
	 * 注册耳机拔出、插入的Receiver
	 * 
	 * @param context
	 */
	public void registerHeadsetPlugReceiver() {
		// 先反注册之前的那个
		unRegisterHeadsetPlugReceiver();

		try {
			// 注册外放耳机是否插入的监听者
			IntentFilter intentFilter = new IntentFilter();
			intentFilter.addAction("android.intent.action.HEADSET_PLUG");
			if (headSetReceiver == null) {
				headSetReceiver = new HeadSetReceiver();
			}
			getRootController().getApplication().registerReceiver(
					headSetReceiver, intentFilter);
		} catch (Exception e) {
		}
	}
	    
	/**
	 * 反注册耳机拔出、插入的Receiver
	 */
	public void unRegisterHeadsetPlugReceiver() {
		if (headSetReceiver != null) {
			try {
				getRootController().getApplication().unregisterReceiver(headSetReceiver);
				headSetReceiver = null;
			} catch (Exception e) {
			}
		}
	}

	private void backToLogin() {
		Intent tIntent = new Intent(getRootController(), Login.class);
		tIntent.putExtra("", getRootController().getClass());
		getRootController().startActivityForResult(tIntent,Constant.REQUEST_LOGIN_BACK);
	}
	
}
