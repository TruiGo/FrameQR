package com.alipay.android.ui.personlcard;

import java.io.File;
import java.io.FileOutputStream;
import java.text.SimpleDateFormat;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.Executor;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicLong;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.json.JSONObject;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.BitmapFactory.Options;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Handler;
import android.os.Message;
import android.os.RemoteException;
import android.os.Vibrator;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ImageSpan;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;
import android.view.animation.ScaleAnimation;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.alipay.android.appHall.CacheManager;
import com.alipay.android.appHall.Helper;
import com.alipay.android.appHall.common.BitmapDownloadListener;
import com.alipay.android.biz.CommonRespHandler;
import com.alipay.android.biz.PersonlCardBiz;
import com.alipay.android.biz.QuickPayBiz;
import com.alipay.android.client.constant.Constant;
import com.alipay.android.client.util.AlipayDataStore;
import com.alipay.android.client.util.BaseHelper;
import com.alipay.android.common.data.ConfigData;
import com.alipay.android.common.data.UserData;
import com.alipay.android.comon.component.ProgressDiv;
import com.alipay.android.log.AlipayLogAgent;
import com.alipay.android.log.Constants;
import com.alipay.android.longlink.ISocketResultNotifer;
import com.alipay.android.longlink.LongLinkServiceManager;
import com.alipay.android.nfd.NFDUtils;
import com.alipay.android.servicebeans.CheckPhoneBlack;
import com.alipay.android.servicebeans.SendPayeeInfo;
import com.alipay.android.ui.framework.BaseViewController;
import com.alipay.android.ui.framework.RootController;
import com.alipay.android.ui.framework.BaseViewController.BizAsyncTask;
import com.alipay.android.ui.quickpay.C2BConfirmViewController;
import com.alipay.android.ui.voucher.VerifyResultNotifier;
import com.alipay.android.util.JsonConvert;
import com.alipay.sonicwavenfc.SonicWaveNFC;
import com.alipay.sonicwavenfc.SonicWaveNFCHandler;
import com.eg.android.AlipayGphone.R;

public class PersonlCardViewController extends BaseViewController implements
		OnClickListener, VerifyResultNotifier {

	private RootController mRootController = null;

	private TextView topTitle = null;

	private ImageView avatar;// user_avatar
	private ImageView avatarLayer;// user_avatar_layer
	private ImageView plugImg;

	private LinearLayout bg_layout;// bg_layout
	private ImageView IV_line;
	private ImageView IV_accountLevel;// accountLevel_image
	private TextView TV_realName;// realname
	private TextView TV_userAccount;// userAccount
	private LongLinkServiceManager longLinkServiceManager;

	private ImageView IV_qrCodeImage;// qrCode_image

	private TextView mBottom_tip = null;
	private TextView mBottom_tip2 = null;
	private AlertDialog avatarDialog = null;
	private ListView personlCardEditListView = null;

	private AlipayDataStore dataStore;

	String savedQrcodePath = "";

	private SonicWaveNFC mSonicWaveNFC;
	private MySonicWaveNFCHandler mMySonicWaveNFCHandler;
	private BizAsyncTask mSendPayeeInfoask;
	private BizAsyncTask mCheckPhoneBlackTask;
	private AlipayDataStore mAlipayDataStore;
	private CacheManager mCacheManager = CacheManager
			.getInstance(getRootController());
	private Timer mReceiveSoundTimer;
	private ProgressDiv progressDiv;
	private boolean needCheckLongLink = false;
	private long lastTime;

	@Override
	protected void onCreate() {
		// TODO Auto-generated method stub
		super.onCreate();
		mAlipayDataStore = new AlipayDataStore(getRootController());
		mRootController = getRootController();
		mView = LayoutInflater.from(mRootController).inflate(
				R.layout.personlcard, null, false);
		addView(mView, null);
		dataStore = new AlipayDataStore(mRootController);
		savedQrcodePath = dataStore.getString(mRootController.getUserId() + ":"
				+ AlipayDataStore.CURUSER_QRCODE_PATH, "");

		// curAvatarPath = dataStore.getString(mRootController.getUserId() + ":"
		// + AlipayDataStore.LASTLOGINUSERAVTARPATH, "");
		// if (curAvatarPath == null || "".equals(curAvatarPath)) {
		// hasAvatar = false;
		// } else {
		// hasAvatar = true;
		// }
		UserData userData = mRootController.getUserData();
		if (userData != null) {
			userData.resetStatus();
			hasAvatar = userData.hasUserAvatar();
		}

		loadAllVariables();
		initHandler();
		queryNameCard();

		if (mCacheManager.get("isSetSurpport").equalsIgnoreCase("true")) {
			if (mCacheManager.get("isSurpportSound").equalsIgnoreCase("true")) {
				mSonicWaveNFC.startReceiveData(10000, 20, getRootController(),
						mMySonicWaveNFCHandler);
				initBottomTip();
				mBottom_tip.setVisibility(View.VISIBLE);
				mBottom_tip2.setVisibility(View.VISIBLE);
				// 如果支持声波就建立长连接
				prepareLongLink();
			} else {
				mBottom_tip.setText(getRootController().getResources()
						.getString(R.string.unsupportReceivepayeeBottomtipTex));
				mBottom_tip2.setVisibility(View.INVISIBLE);
			}
		} else {
			checkPhoneBlack();
		}
	}

	private void prepareLongLink() {
		longLinkServiceManager.bindService(this, null);
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
		} else {}
	}

	private void initBottomTip() {
		Drawable d = getRootController().getResources().getDrawable(
				R.drawable.surpport_sound);
		d.setBounds(0, 0, d.getIntrinsicWidth(), d.getIntrinsicHeight());
		ImageSpan span = new ImageSpan(d, ImageSpan.ALIGN_BASELINE);
		SpannableString spanString = new SpannableString(getRootController()
				.getResources().getString(
						R.string.supportReceivepayeeBottomtipTex));
		spanString.setSpan(span, spanString.length() - 2, spanString.length(),
				Spannable.SPAN_INCLUSIVE_EXCLUSIVE);
		mBottom_tip.setText(spanString);
	}

	private class MySonicWaveNFCHandler implements SonicWaveNFCHandler {

		/*
		 * (non-Javadoc)
		 * 
		 * @see
		 * com.alipay.sonicwavenfc.SonicWaveNFCHandler#onDataReceived(java.lang
		 * .String)
		 */
		@Override
		public void onDataReceived(String arg0) {
//			String dynamicId = arg0;
			try{
				String reg = "^kf[(a-z)\\d]{16}$";
				Pattern pattern = Pattern.compile(reg, Pattern.CASE_INSENSITIVE);
				Matcher matcher = pattern.matcher(arg0);
				long currentTimeMillis = System.currentTimeMillis();
				if (matcher.matches() && (currentTimeMillis-lastTime)>5*1000){// 校验快付声波的数据
					lastTime = currentTimeMillis;
					mSonicWaveNFC.stopReceiveData();
					checkLongLink();
					((Vibrator) getRootController().getSystemService(
							Context.VIBRATOR_SERVICE)).vibrate(
							Constant.DEFAULT_VIBRATE_PATTERN, -1);
					queryMessageInfo(getRootController().getResources().getString(R.string.receivePay));
					mSendPayeeInfoask = new BizAsyncTask(SendPayeeInfo.BIZ_TAG,
							false);
					mSendPayeeInfoask.execute(arg0);
				} else {
					mSonicWaveNFC.startReceiveData(10000, 20, getRootController(),
							mMySonicWaveNFCHandler);
				}
			}catch (Exception e){
				mSonicWaveNFC.startReceiveData(10000, 20, getRootController(),
						mMySonicWaveNFCHandler);
			}
		}

		@Override
		public void onReceiveDataFailed(int arg0) {
		}

		@Override
		public void onReceiveDataInfo(String arg0) {
		}

		@Override
		public void onReceiveDataStarted() {
		}

		@Override
		public void onReceiveDataTimeout() {
		}

		@Override
		public void onSendDataFailed(int arg0) {
		}

		@Override
		public void onSendDataInfo(String arg0) {
		}

		@Override
		public void onSendDataStarted() {
		}

		@Override
		public void onSendDataTimeout() {
		}
	}

	private void loadAllVariables() {
		longLinkServiceManager = LongLinkServiceManager.getInstance(
				getRootController(), null);
		topTitle = (TextView) findViewById(R.id.title_text);
		topTitle.setText("我的名片");

		IV_line = (ImageView) findViewById(R.id.line);

		avatar = (ImageView) findViewById(R.id.user_avatar);

		avatarLayer = (ImageView) findViewById(R.id.user_avatar_layer);//

		plugImg = (ImageView) findViewById(R.id.plugImg);

		if (hasAvatar) {
			Bitmap mAvtar = mRootController.getUserData().getUserAvtar();
			avatar.setImageBitmap(mAvtar);
			avatarLayer.setImageResource(R.drawable.avatar_layer_hasavatar);
			plugImg.setVisibility(View.INVISIBLE);
		}
		avatar.setOnClickListener(this);

		IV_accountLevel = (ImageView) findViewById(R.id.accountLevel_image);// accountLevel_image
		TV_realName = (TextView) findViewById(R.id.realname);// realname
		TV_userAccount = (TextView) findViewById(R.id.userAccount);// userAccount

		TV_realName.setText(mRootController.getRealName());
		TV_userAccount.setText(mRootController.getAccountName(false));
		bg_layout = (LinearLayout) findViewById(R.id.bg_layout);
		IV_qrCodeImage = (ImageView) findViewById(R.id.qrCode_image);// qrCode_image
		mBottom_tip = (TextView) findViewById(R.id.bottom_tip);
		mBottom_tip2 = (TextView) findViewById(R.id.bottom_tip2);

		Bitmap curUserCodeBitmap = null;
		if (savedQrcodePath != null && !"".equals(savedQrcodePath)) {
			curUserCodeBitmap = getCurUserCode(savedQrcodePath);
			if (curUserCodeBitmap != null) {
				IV_qrCodeImage.setImageBitmap(curUserCodeBitmap);
				IV_qrCodeImage.invalidate();
			}
		}

		mMySonicWaveNFCHandler = new MySonicWaveNFCHandler();
		mSonicWaveNFC = null;
		mSonicWaveNFC = SonicWaveNFC.getInstance();
		mSonicWaveNFC.initSonicWaveNFC(getRootController());// 实例构造后，需要检测耳机是否 插入

		imageRepeat();
		preparePersonlCardEdit();
	}

	private void imageRepeat() {
		BaseHelper.fixBackgroundRepeat(bg_layout);
		BaseHelper.fixBackgroundRepeat(IV_line);
	}

	private void preparePersonlCardEdit() {
		personlCardEditListView = new ListView(mRootController);
		personlCardEditListView.setBackgroundColor(Color.WHITE);
		personlCardEditListView.setDividerHeight(1);
		personlCardEditListView.setCacheColorHint(Color.WHITE);
		BaseAdapter personlCardEditAdapter = new PersonlCardEditAdapter(
				mRootController);
		personlCardEditListView.setAdapter(personlCardEditAdapter);
		personlCardEditListView
				.setOnItemClickListener((OnItemClickListener) personlCardEditAdapter);
		if (avatarDialog == null) {
			avatarDialog = new AlertDialog.Builder(mRootController)
					.setTitle(
							mRootController.getResources().getString(
									R.string.saveAvatar))
					.setView(personlCardEditListView).create();
			((PersonlCardEditAdapter) personlCardEditAdapter)
					.setDialog(avatarDialog);
		}
	}

	static final int PHOTOCROP = 0;

	@Override
	public void onStart() {
		// TODO Auto-generated method stub
		super.onStart();
	}

	@Override
	protected void onResume() {
		if (mReceiveSoundTimer != null) {
			mReceiveSoundTimer.cancel();
			mReceiveSoundTimer = null;
			mSonicWaveNFC.startReceiveData(10000, 20, getRootController(),
					mMySonicWaveNFCHandler);// 部分手机锁屏后
		}
		if(needCheckLongLink){
		    checkLongLink();
		}else{
			needCheckLongLink = true;
		}
		super.onResume();
	}

	@Override
	public void onPause() {
		mReceiveSoundTimer = new Timer();
		mReceiveSoundTimer.schedule(new TimerTask() {
			@Override
			public void run() {
				mSonicWaveNFC.stopReceiveData();
				mReceiveSoundTimer.cancel();
			}
		}, 1000 * 30);
		super.onPause();
	}

	@Override
	public void onStop() {
		// TODO Auto-generated method stub
		super.onStop();
	}

	@Override
	public void onDestroy() {
		if (mSonicWaveNFC != null) {
			mSonicWaveNFC.stopReceiveData();
		}
		if (longLinkServiceManager != null) {
			longLinkServiceManager.unBindService();
		}
		super.onDestroy();
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		// TODO Auto-generated method stub
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			AlipayLogAgent.writeLog(mRootController, Constants.BehaviourID.CLICKED, null, null,
                    Constants.WALLETACCOUNT, null, Constants.WALLETHOME, Constants.MYNAMECARD,
                    Constants.BACKICON, "");
		}
		return super.onKeyDown(keyCode, event);
	}

	private boolean hasAvatar;

	@Override
	public void onClick(View v) {
		UserData userData = mRootController.getUserData();

		switch (v.getId()) {
		case R.id.user_avatar:
			if (userData != null && userData.hasUserAvatar()) {
				AlipayLogAgent.writeLog(mRootController, Constants.BehaviourID.CLICKED, null, null,
	                    Constants.WALLETACCOUNT, null, Constants.SEEFACEVIEW, Constants.MYNAMECARD,
	                    Constants.SETFACEICON, "");
				gotoLargeAvatar();
			} else {
				AlipayLogAgent.writeLog(mRootController, Constants.BehaviourID.CLICKED, null, null,
	                    Constants.WALLETACCOUNT, null, Constants.SAVEFACEVIEW, Constants.MYNAMECARD,
	                    Constants.SETFACEICON, "");
				showAvatarMenu();
			}
		default:
			break;
		}
	}

	// 有头像时，去新activity
	private void gotoLargeAvatar() {
		Intent mIntent = new Intent();
		mIntent.setClass(mRootController, LargeAvatarActivity.class);
		mRootController.startActivityForResult(mIntent, PHOTOCROP);
	}

	// 没有头像时弹出菜单
	private void showAvatarMenu() {
		avatarDialog.show();
	}

	// ================================================================联网逻辑

	private final static String BIZTYPE_QUERYNAMECARD = "biztype_queryNameCard";

	private final static String BIZTYPE_UPLOADHEADIMG = "biztype_uploadHeadImg";

	private BizAsyncTask loadTask;

	private void checkPhoneBlack() {
		mCheckPhoneBlackTask = new BizAsyncTask(CheckPhoneBlack.BIZ_TAG, false);
		mCheckPhoneBlackTask.execute(Build.MANUFACTURER, Build.MODEL,
				Build.VERSION.RELEASE);
	}

	private void queryNameCard() {
		if (loadTask != null
				&& loadTask.getStatus() != AsyncTask.Status.FINISHED)
			loadTask.cancel(true);
		loadTask = new BizAsyncTask(BIZTYPE_QUERYNAMECARD, false);
		loadTask.execute();
	}

	// private void uploadHeadImg(String uploadHeadImg_base64, String fileName)
	// {
	// if (loadTask != null
	// && loadTask.getStatus() != AsyncTask.Status.FINISHED)
	// loadTask.cancel(true);
	// loadTask = new BizAsyncTask(BIZTYPE_UPLOADHEADIMG, false);
	// loadTask.execute(uploadHeadImg_base64, fileName);
	// }

	@Override
	protected void onPreDoInbackgroud(String bizType) {
		// TODO Auto-generated method stub
		super.onPreDoInbackgroud(bizType);
		if (BIZTYPE_QUERYNAMECARD.equals(bizType)) {
			if (progressDiv == null) {
				progressDiv = getRootController().getDataHelper()
						.showProgressDialogWithoutCancelButton(mRootController,
								null, "正在获取用户信息...", false, false, null, null);
			}
		} else if (BIZTYPE_UPLOADHEADIMG.equals(bizType)) {

		}

	}

	@Override
	protected Object doInBackground(String bizType, String... params) {
		if (BIZTYPE_QUERYNAMECARD.equals(bizType)) {
			return new PersonlCardBiz().queryNameCard();
		} else if (BIZTYPE_UPLOADHEADIMG.equals(bizType)) {
			return new PersonlCardBiz().uploadHeadImg(params[0], params[1]);
		} else if (SendPayeeInfo.BIZ_TAG.equalsIgnoreCase(bizType)) {
			return new QuickPayBiz().sendPayeeInfo(params[0]);
		} else if (CheckPhoneBlack.BIZ_TAG.equalsIgnoreCase(bizType)) {
			return new QuickPayBiz().checkPhoneBlack(params[0], params[1],
					params[2]);
		}
		return super.doInBackground(bizType, params);
	}

	@Override
	protected void onPostExecute(String bizType, Object result) {
		// TODO Auto-generated method stub
		if (BIZTYPE_QUERYNAMECARD.equals(bizType)) {

			if (progressDiv != null && progressDiv.isShowing()) {
				progressDiv.dismiss();
				progressDiv = null;
			}

			JSONObject responseJson = JsonConvert
					.convertString2Json((String) result);

			if (CommonRespHandler.filterBizResponse(getRootController(),
					responseJson)) {

				String userGrade = responseJson.optString("userGrade");
				String userQrCode = responseJson.optString("qrCode");
				String userAcctBarcodePath = responseJson
						.optString("userAcctBarcodePath");

				// requestBarcode(IV_qrCodeImage, userQrCode);

				if ("P".equals(userGrade)) {
					userGradeValue = USERGRADE_NEEDLIGHTON;
				} else if ("N".equals(userGrade)) {
					userGradeValue = USERGRADE_NORMAL;
				} else if ("V".equals(userGrade)) {
					userGradeValue = USERGRADE_VIP;
				} else if ("I".equals(userGrade)) {
					userGradeValue = USERGRADE_VVIP;
				}

				if (savedQrcodePath == null || "".equals(savedQrcodePath)) {
					Helper.bitmapFromUriString(mRootController,
							userAcctBarcodePath, mBitmapDownloadListener, -1);
				}

				populateUI();
			}
		} else if (SendPayeeInfo.BIZ_TAG.equalsIgnoreCase(bizType)) {
			try{
				mSonicWaveNFC.startReceiveData(10000, 20, getRootController(),
						mMySonicWaveNFCHandler);
				JSONObject responseJson = JsonConvert
						.convertString2Json((String) result);
				if (responseJson != null
						&& responseJson.optString("resultStatus").equalsIgnoreCase(
								"100")) {
					initBottomTip();
					// 重新监听声波。
					// mSonicWaveNFC.startReceiveData(10000, 20,
					// getRootController(), mMySonicWaveNFCHandler);
				} else {
					String memo1 = responseJson != null ? responseJson
							.optString("memo") : getRootController().getResources()
							.getString(R.string.checkConnect);
					String memo = "".equalsIgnoreCase(memo1) ? getRootController()
							.getResources().getString(R.string.checkConnect)
							: memo1;
	//				messageQue.offer(memo);
					queryMessageInfo(memo);
					// getRootController().getDataHelper().showToast(getRootController(),
					// memo.equalsIgnoreCase("")?getRootController().getResources().getString(R.string.checkConnect):memo);
				}
			}catch (Exception e){
				mSonicWaveNFC.startReceiveData(10000, 20, getRootController(),
						mMySonicWaveNFCHandler);
			}

		} else if (CheckPhoneBlack.BIZ_TAG.equalsIgnoreCase(bizType)) {
			mAlipayDataStore.putString(AlipayDataStore.PERSONLCARDFIST,
					getRootController().getConfigData().getProductId());
			JSONObject responseJson = JsonConvert
					.convertString2Json((String) result);
			if (responseJson != null
					&& responseJson.optString("resultStatus").equalsIgnoreCase(
							"100")) {
				String blacks = responseJson.optString("blacks");
				mSonicWaveNFC.setPhoneConfig(blacks);
				if (mSonicWaveNFC.isReceiverSoincWave()) {
					mSonicWaveNFC.startReceiveData(10000, 20,
							getRootController(), mMySonicWaveNFCHandler);
					prepareLongLink();
					initBottomTip();
					mBottom_tip.setVisibility(View.VISIBLE);
					mBottom_tip2.setVisibility(View.VISIBLE);
					mCacheManager.put("isSurpportSound", "true");
					mCacheManager.put("isSetSurpport", "true");
				} else {
					mBottom_tip
							.setText(getRootController()
									.getResources()
									.getString(
											R.string.unsupportReceivepayeeBottomtipTex));
					mCacheManager.put("isSurpportSound", "false");
					mCacheManager.put("isSetSurpport", "true");
				}
			} else {
				mCacheManager.put("isSetSurpport", "false");
			}
		}
		super.onPostExecute(bizType, result);
	}

	private Executor toastExecutor = new Executor() {
		private ConcurrentLinkedQueue<Runnable> messageQue1 = new ConcurrentLinkedQueue<Runnable>();
		Runnable runnable = null;
		Timer timer = new Timer();
		AtomicBoolean isActive = new AtomicBoolean(false);

		@Override
		public void execute(final Runnable command) {
			messageQue1.offer(new Runnable() {

				@Override
				public void run() {
					isActive.set(true);
					command.run();
					timer.schedule(new TimerTask() {
						
						@Override
						public void run() {
							exec();
							isActive.set(false);
						}
					},3000);
				}
			});
			if (!isActive.get()) {
				exec();
			}
		}

		void exec() {
			if ((runnable = messageQue1.poll()) != null) {
				runnable.run();
			}
		}
	};

	// private void requestBarcode(final ImageView barcodeImage, String
	// verifyCode) {
	// BarcodeHelper.getBarcodeImg(false,verifyCode,new Handler(){
	// public void handleMessage(Message msg) {
	// switch (msg.what) {
	// case R.id.encode_succeeded:
	// Bitmap bm = (Bitmap) msg.obj;
	//
	// if(bm !=null){
	// Bitmap barcodeBitmap = getBarcode(bm);
	// saveQrImage(barcodeBitmap);
	// LogUtil.logOnlyDebuggable("Barcode", "encode result==> width="+
	// bm.getWidth()+";" + "height="+ bm.getHeight());
	//
	// if (null != barcodeBitmap) {
	// barcodeImage.setImageBitmap(barcodeBitmap);
	// // barcodeImage.setBackground(new BitmapDrawable(null,barcodeBitmap));
	// }
	// }
	// break;
	// }
	// super.handleMessage(msg);
	// }},mRootController);
	// }

	private void saveQrImage(Bitmap bm) {
		try {
			if (null != bm) {
				avatarBitmap = bm;
				if (null != bm) {
					File file = new File(getUserQrcodeDirStr(mRootController,
							mRootController.getUserId()));
					file.mkdirs();

					file = new File(getUserQrcodeFileStr(mRootController,
							mRootController.getUserId()));
					file.createNewFile();

					if (bm.compress(Bitmap.CompressFormat.PNG, 100,
							new FileOutputStream(file))) {
						dataStore.putString(mRootController.getUserId() + ":"
								+ AlipayDataStore.CURUSER_QRCODE_PATH,
								file.getAbsolutePath());
					}
				}

				mHandler.sendEmptyMessage(GET_USER_QECODE);
			} else {
				// TODO 设置二维码默认图片
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	// private Bitmap getBarcode(Bitmap bitmap) {
	// if (null == bitmap) {
	// return null;
	// }
	//
	// ScreenScaleParam param =
	// BarcodeHelper.detectScreenLevel(mRootController);
	//
	// Bitmap barcodeBitmap = Bitmap.createBitmap(bitmap, (int)(20 *
	// param.getCurScale()), (int)(20 * param.getCurScale()),
	// bitmap.getWidth() - (int)(40 * param.getCurScale()), bitmap.getHeight() -
	// (int)(40 * param.getCurScale()));
	// // bitmap.recycle();
	//
	// return barcodeBitmap;
	// }

	public static final int USERGRADE_NEEDLIGHTON = 0;
	public static final int USERGRADE_NORMAL = 1;
	public static final int USERGRADE_VIP = 2;
	public static final int USERGRADE_VVIP = 3;

	private int userGradeValue = 0;

	private void populateUI() {
		if (USERGRADE_VIP == userGradeValue || USERGRADE_VVIP == userGradeValue) {
			IV_accountLevel.setVisibility(View.VISIBLE);

		} else {
			IV_accountLevel.setVisibility(View.GONE);
		}
	}

	/**
	 * 下载二维码
	 */
	private BitmapDownloadListener mBitmapDownloadListener = new BitmapDownloadListener() {
		@Override
		public void onComplete(Bitmap bm) {
			try {
				if (null != bm) {
					avatarBitmap = bm;
					saveQrImage(avatarBitmap);
					if (null != bm) {
						File file = new File(getUserQrcodeDirStr(
								mRootController, mRootController.getUserId()));
						file.mkdirs();

						file = new File(getUserQrcodeFileStr(mRootController,
								mRootController.getUserId()));
						file.createNewFile();

						if (bm.compress(Bitmap.CompressFormat.PNG, 100,
								new FileOutputStream(file))) {
							dataStore
									.putString(
											mRootController.getUserId()
													+ ":"
													+ AlipayDataStore.CURUSER_QRCODE_PATH,
											file.getAbsolutePath());
						}
					}

					mHandler.sendEmptyMessage(GET_USER_QECODE);
				} else {
					// TODO 设置二维码默认图片
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	};

	private Handler mHandler;

	private static final int GET_USER_QECODE = 0;

	private Bitmap avatarBitmap;

	private void initHandler() {
		mHandler = new Handler() {
			@Override
			public void handleMessage(Message msg) {
				switch (msg.what) {
				case GET_USER_QECODE:
					if (avatarBitmap != null) {
						IV_qrCodeImage.setImageBitmap(avatarBitmap);

						IV_qrCodeImage.invalidate();
					}
					break;

				default:
					break;
				}
			}
		};
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (resultCode == Activity.RESULT_OK) {
			switch (requestCode) {
			case PHOTOCROP:
				UserData userData = mRootController.getUserData();
				if (userData != null && userData.hasUserAvatar()) {
					avatar.setImageBitmap(userData.getUserAvtar());
					avatar.invalidate();
					avatarLayer
							.setImageResource(R.drawable.avatar_layer_hasavatar);
					plugImg.setVisibility(View.INVISIBLE);
				}
			default:
				break;
			}
		}
	}

	// private Bitmap getAvatarFromIntent(Intent data) {
	// String url = data.getStringExtra(GetAvatarViewController.EXTRA_DATA);
	// Options options = new Options();
	//
	// DisplayMetrics displayMetrics = new DisplayMetrics();
	// Display display = ((WindowManager) mRootController
	// .getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay();
	// display.getMetrics(displayMetrics);
	// options.inDensity = displayMetrics.densityDpi;
	// options.inScaled = true;
	// return BitmapFactory.decodeFile(url, options);
	//
	// }

	// ==============================================================================服务端返回图片名并保存
	private static String getUserQrcodeFileStr(Context context, String userId) {
		return getUserQrcodeDirStr(context, userId) + "userQrCode.png";
	}

	private static String getUserQrcodeDirStr(Context context, String userId) {
		return context.getFilesDir() + File.separator + "userdata"
				+ File.separator + "userQrcode" + File.separator + userId
				+ File.separator;
	}

	private Bitmap getCurUserCode(String mUserQrcodePath) {
		Options options = new Options();
		options.inDensity = (int) Helper.getDensityDpi(mRootController);
		options.inScaled = true;
		try {
			return BitmapFactory.decodeFile(mUserQrcodePath, options);
		} catch (Error e) {
			// Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}

	// 长连接
	@Override
	public void onSuccess(JSONObject response) {
		// TODO Auto-generated method stub
		((Vibrator) getRootController().getSystemService(
				Context.VIBRATOR_SERVICE)).vibrate(
				Constant.DEFAULT_VIBRATE_PATTERN, -1);
		if (response != null) {
			String action = response.optString(Constant.RPF_PUSH_TRADE_ACTION);
			if (action
					.equalsIgnoreCase(Constant.RPF_PUSH_TRADE_ACT_SOUNDWAVEPAYPUSH)) {
				String memo = response.optString(Constant.MEMO);
//				messageQue.offer(memo);
				queryMessageInfo(memo);
			}
		}
	}

	@Override
	public void onFail(JSONObject response) {
		// TODO Auto-generated method stub

	}

	private void showVerifyResult(final String resultText) {

		topTitle.post(new Runnable() {
			@Override
			public void run() {
				View toastView = LayoutInflater.from(getRootController())
						.inflate(R.layout.verify_result_toast, null);
				TextView toastTextView = (TextView) toastView
						.findViewById(R.id.resultText);
				ImageView resultImage = (ImageView) toastView
						.findViewById(R.id.resultImage);

				toastTextView.setEllipsize(null);
				toastTextView.setText(Helper.toDBC(resultText));
				// resultImage.setBackgroundResource(R.drawable.tb_discover_fail);

				final Toast toast = new Toast(getRootController());
				toast.setView(toastView);
				toast.setGravity(Gravity.CENTER, 0, 0);
				toast.setDuration(Toast.LENGTH_SHORT);
				toast.show();
			}
		});
	}

	private void queryMessageInfo(final String toastMessage) {
		toastExecutor.execute(new Runnable() {

			@Override
			public void run() {
//				String str;
//				if ((str = messageQue.poll()) != null) {
					showVerifyResult(toastMessage);
//				}
			}
		});
	}

	// @SuppressWarnings("unchecked")
	// private void prepareShareDialog() {
	// shareListView = new ListView(mRootController);
	// shareListView.setBackgroundColor(Color.WHITE);
	// shareListView.setDividerHeight(1);
	// shareListView.setCacheColorHint(Color.WHITE);
	//
	// Object[] data= prepareShareWeiboData();
	// if (data!=null&&data[0]!=null&&data[1]!=null) {
	// SimpleAdapter shareAdapter = new ShareAdapter(mRootController,
	// (ArrayList<HashMap<String,Object>>)data[0], (Intent)data[1]);
	// shareListView.setAdapter(shareAdapter);
	// shareListView
	// .setOnItemClickListener((OnItemClickListener) shareAdapter);
	// if (shareDialog == null) {
	// shareDialog = new AlertDialog.Builder(mRootController)
	// .setTitle("分享到微博").setView(shareListView).create();
	// }
	// }
	// }

	// private Object[] prepareShareWeiboData() {
	//
	// ArrayList<HashMap<String, Object>> mMapData = null;
	// Intent mIntent = null;
	//
	// mIntent = new Intent(Intent.ACTION_SEND);
	// mIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
	// mIntent.putExtra(Intent.EXTRA_SUBJECT,
	// mRootController.getString(R.string.shareClient));
	// mIntent.setType("text/plain");
	// try {
	// // 获取程序列表
	// PackageManager pm = mRootController.getPackageManager();
	//
	// List<ResolveInfo> listResolveInfo = pm.queryIntentActivities(
	// mIntent, PackageManager.MATCH_DEFAULT_ONLY);
	// mMapData = new ArrayList<HashMap<String, Object>>();
	// // List<ResolveInfo> --> ArrayList<HashMap<String, Object>>
	// if (listResolveInfo != null) {
	// int size = listResolveInfo.size();
	//
	// for (int i = 0; i < size; i++) {
	// HashMap<String, Object> shareqrcodedatatemp = new HashMap<String,
	// Object>();
	//
	// ResolveInfo temp = (ResolveInfo) listResolveInfo.get(i);
	// if (!temp.activityInfo.packageName.toLowerCase().equals(
	// APP_PACKAGENAME_TENCENT_WEIBO)&&!temp.activityInfo.packageName.toLowerCase().equals(
	// APP_PACKAGENAME_SINA_WEIBO))
	// continue;
	//
	// shareqrcodedatatemp.put(APP_ICONS,
	// temp.loadIcon(pm));
	// shareqrcodedatatemp.put(APP_LABLE,
	// temp.loadLabel(pm));
	// shareqrcodedatatemp.put(
	// APP_PACKAGENAME,
	// temp.activityInfo.applicationInfo.packageName);
	// shareqrcodedatatemp.put(APP_NAME,
	// temp.activityInfo.name);
	//
	// shareqrcodedatatemp.put(SEND_CONTENT,
	// "分享二维码");
	// mMapData.add(shareqrcodedatatemp);
	// }
	// }
	// if (mMapData.size() <= 0) {
	// Toast toast = Toast.makeText(mRootController, "无分享软件", 5000);
	// toast.setGravity(Gravity.CENTER, 0, 0);
	// toast.show();
	// return null;
	// }
	// return new Object[]{mMapData,mIntent};
	// } catch (Exception e) {
	// e.printStackTrace();
	// return null;
	// }
	// }

	// private static final int EDITMENU_MAKEPHOTO = 0;
	// private static final int EDITMENU_NATIVIIMAGE = 1;
	//
	// static final int[] itemIds = new int[] { EDITMENU_MAKEPHOTO,
	// EDITMENU_NATIVIIMAGE};
	// static final String[] itemNames = new String[] { "拍头像", "从相册选择"};
	// static final int[] itemIcons = new int[] { R.drawable.makephono,
	// R.drawable.localimage};

	// public static final String APP_PACKAGENAME_BLUETOOTH =
	// "com.android.bluetooth";
	// public static final String APP_PACKAGENAME_SMS = "com.android.mms";
	// public static final String APP_ICONS = "icons";
	// public static final String APP_LABLE = "label";
	// public static final String APP_PACKAGENAME = "app";
	// public static final String APP_NAME = "name";
	// public static final String SEND_CONTENT = "sendcontent";
	// public static final String APP_PACKAGENAME_TENCENT_WEIBO =
	// "com.tencent.wblog";
	// public static final String APP_PACKAGENAME_SINA_WEIBO= "com.sina.weibo";
	//
	// private class ShareAdapter extends SimpleAdapter implements
	// OnItemClickListener {
	//
	// private ArrayList<HashMap<String, Object>> mMapData = null;
	// private Context mContext = null;
	// private Intent mIntent = null;
	//
	// public ShareAdapter(Context context,
	// ArrayList<HashMap<String, Object>> mapData, Intent intent) {
	// super(context, mapData, -1, null, null);
	// // TODO Auto-generated constructor stub
	// mMapData = mapData;
	// mContext = context;
	// mIntent = intent;
	// }
	//
	// @Override
	// public View getView(int position, View convertView, ViewGroup parent) {
	// View view = null;
	// if (convertView != null)
	// view = convertView;
	// else {
	// view = ((Activity) mContext).getLayoutInflater().inflate(
	// R.layout.share_item, null);
	// }
	// ((ImageView) view.findViewById(R.id.appicon))
	// .setImageDrawable((Drawable) mMapData.get(position).get(
	// APP_ICONS));
	// ((TextView) view.findViewById(R.id.appname))
	// .setText((CharSequence) mMapData.get(position).get(
	// APP_LABLE));
	// TextView copyIntro = (TextView) view.findViewById(R.id.copyinfo);
	// copyIntro.setVisibility(View.GONE);
	// return view;
	// }
	//
	// @Override
	// public void onItemClick(AdapterView<?> arg0, View arg1, int position,
	// long arg3) {
	// mIntent.setClassName(
	// (String) mMapData.get(position).get(APP_PACKAGENAME),
	// (String) mMapData.get(position).get(APP_NAME));
	// mIntent.putExtra(Intent.EXTRA_TEXT, (String) mMapData.get(position)
	// .get(SEND_CONTENT));
	// mIntent.setType("image/*");
	// // TODO 先写好了一个本地图片
	// mIntent.putExtra(
	// Intent.EXTRA_STREAM,
	// Uri.parse("file:///storage/sdcard0/DCIM/Camera/1352972607721.jpg"));
	// mContext.startActivity(mIntent);
	// if (shareDialog != null) {
	// shareDialog.dismiss();
	// }
	// }
	// }
	//
	// public class MenuItem {
	// ImageView icon;
	// TextView name;
	// }

}
