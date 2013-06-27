package com.alipay.android.ui.transfer;

import java.util.ArrayList;
import java.util.List;

import android.app.KeyguardManager;
import android.bluetooth.BluetoothAdapter;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.AnimationDrawable;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.PowerManager;
import android.os.RemoteException;
import android.os.Vibrator;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.alipay.android.appHall.Helper;
import com.alipay.android.client.constant.Constant;
import com.alipay.android.common.data.UserData;
import com.alipay.android.comon.component.EditTextHasNullChecker;
import com.alipay.android.comon.component.EditTextWithButton;
import com.alipay.android.nfd.IClientObserverPeer;
import com.alipay.android.nfd.NFDBaseCallback;
import com.alipay.android.nfd.NFDBaseClient;
import com.alipay.android.nfd.NFDReceiverAdapter;
import com.alipay.android.ui.bean.TransferReceiver;
import com.alipay.android.ui.framework.BaseViewController;
import com.alipay.android.ui.framework.RootController;
import com.alipay.android.util.LogUtil;
import com.eg.android.AlipayGphone.R;

public class NewReceiverViewController extends BaseViewController implements View.OnClickListener, SensorEventListener,NFDBaseCallback{
	private static final String DISCOVERREQ = "localDiscover";
	private static final String DISCOVERREGIST = "discoverRegisgt";
	private EditTextWithButton mReceiverAccountInput;
	private Button mNextButton;
	private Button mInputReceiverButton;
	private Button mShakeReceiverButton;
	private TransferReceiver transferReceiver;
	private EditTextHasNullChecker checker;
	private LinearLayout mInputReceiverCanvas;
	private LinearLayout mDiscoverReceiverCanvas;
	private LinearLayout mNoResultView;
	private LinearLayout mFirstOpenView;
	private LinearLayout mDiscoverLoading;
	private TextView mDiscoverAgainText;
	private ImageView mShakeImage;
	private ImageView mDiscoveringImage;
	private AnimationDrawable discoverAnimation;
//	private TextView mNfcSupportText;
//	private TextView mNfcUsageInfo;
	
	private ViewGroup mIndexView;
	private LinearLayout mLoadingView;
	private LinearLayout mResultView;
	private TextView mDiscoverResult;
	private LinearLayout mLoadingCanvas;
	private ListView mDiscoveredResultList;
    private SensorManager mSensorManager;
    private NFDReceiverAdapter mReceiverAdapter;
    private MediaPlayer mediaPlayer;
    private int backTimes = 0;
    private TransferReceiver mySelf = new TransferReceiver();
    private KeyguardManager mKeyguardManager;
//    private StorageStateInfo storageStateInfo;
    private long shakeStartTime;
    private Bundle bluetoothLock;
	protected static final int BLUETOOTH_REQUEST_DISCOVERABLE = 168;
	private NFDBaseClient nfdBaseClient;
	private Vibrator vibrator;
	private PowerManager.WakeLock mWakeLock;
//	private LinearLayout mFilterLayout;
    
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
    	if(keyCode == KeyEvent.KEYCODE_BACK){
    		mSensorManager.unregisterListener(this);
    	}
    	return super.onKeyDown(keyCode, event);
    }
	
	private synchronized List<TransferReceiver> processRetReceiver(List<String> clientInfos) {
		List<TransferReceiver> nfdReceivers = new ArrayList<TransferReceiver>();
		for(String clientInfo : clientInfos){
			if(clientInfo != null &&clientInfo.length() > 0){
				TransferReceiver receiver = produceNewReceiver(clientInfo);
				if(receiver != null && !receiver.equals(mySelf))
					nfdReceivers.add(receiver);
			}
		}
		return nfdReceivers;
	}
	
	private TransferReceiver produceNewReceiver(String clientInfo) {
		String[] receiverStr = clientInfo.split("#");
		if(receiverStr.length >= 2){
			TransferReceiver receiver = new TransferReceiver();
			receiver.recvRealName = receiverStr[1];
			receiver.recvAccount = receiverStr[0];
			if(receiverStr.length >= 3)
				receiver.recvMobile = receiverStr[2];
			return receiver;
		}
		return null;
	}

	@Override
	protected void onCreate() {
		mView = LayoutInflater.from(this.getRootController()).inflate(R.layout.transfer_input_new, null, false);
		addView(mView, null);
		
		loadAllVariables();
		initListener();
		checker.addNeedCheckView(mReceiverAccountInput.getEtContent());
		checker.addNeedEnabledButton(mNextButton);
		mReceiverAccountInput.getEtContent().addTextChangedListener(checker);
		
		registSensor();
		getRootController().setVolumeControlStream(AudioManager.STREAM_MUSIC);
	}
	
	@Override
	protected void onResume() {
		super.onResume();
		if(getRootController().getWindow() != null && getRootController().getWindow().getCurrentFocus() !=null){
			getRootController().getWindow().getCurrentFocus().clearFocus();
		}
		
		if(mInputReceiverButton.isSelected())
			showInputPanel();
		
		registSensor();
		mView.requestFocus();
	}
	
	private void loadAllVariables() {
		PowerManager pm = (PowerManager) getRootController().getSystemService(Context.POWER_SERVICE);
		mWakeLock = pm.newWakeLock(PowerManager.SCREEN_BRIGHT_WAKE_LOCK| PowerManager.ON_AFTER_RELEASE, "");
		
//		storageStateInfo = StorageStateInfo.getInstance();
		mKeyguardManager = (KeyguardManager) getRootController().getSystemService(Context.KEYGUARD_SERVICE);
		checker = new EditTextHasNullChecker();
		mReceiverAccountInput = (EditTextWithButton) findViewById(R.id.receiverAccountInput);
		mNextButton = (Button) findViewById(R.id.confirmTransformInfoButton);
		mInputReceiverButton = (Button) findViewById(R.id.inputReceiver);
		mInputReceiverButton.setSelected(true);
		mShakeReceiverButton = (Button) findViewById(R.id.shakeReceiver);
//		mNfcSupportText = (TextView) findViewById(R.id.nfcSupport);
//		mNfcUsageInfo = (TextView) findViewById(R.id.nfcUsageInfo);
		
		mInputReceiverCanvas = (LinearLayout) findViewById(R.id.inputReceiverCanvas);
		mDiscoverReceiverCanvas = (LinearLayout) findViewById(R.id.discoverReceiverCanvas);
		mSensorManager = (SensorManager) getRootController().getSystemService(Context.SENSOR_SERVICE);
        mDiscoveredResultList = (ListView) mView.findViewById(R.id.discoveredResultList);
        mIndexView = (ViewGroup) mView.findViewById(R.id.indexView);
        mLoadingView = (LinearLayout) mView.findViewById(R.id.loadingView);
        mResultView = (LinearLayout) mView.findViewById(R.id.resultView);
        mDiscoverResult = (TextView) mView.findViewById(R.id.discoverResult);
        mNoResultView = (LinearLayout) mView.findViewById(R.id.noResultView);
        mFirstOpenView = (LinearLayout) mView.findViewById(R.id.firstOpenView);
        mDiscoverLoading = (LinearLayout) mView.findViewById(R.id.discoverLoading);
        mDiscoverAgainText = (TextView) mView.findViewById(R.id.discoverAgainText);
        mLoadingCanvas = (LinearLayout) mView.findViewById(R.id.loadingCanvas);
//        mFilterLayout = (LinearLayout) mView.findViewById(R.id.filter);
        
        mShakeImage = (ImageView) mView.findViewById(R.id.shakeImage);
        mDiscoveringImage = (ImageView) mView.findViewById(R.id.discovering_loading);
        mDiscoveringImage.setBackgroundResource(R.drawable.discover_loading); 
        
        mReceiverAdapter = new NFDReceiverAdapter(getRootController());
        mDiscoveredResultList.setAdapter(mReceiverAdapter);
        
        mediaPlayer = MediaPlayer.create(getRootController(), R.raw.shake_success);
        
        mySelf.recvRealName = getRootController().getUserData().getRealName();
        mySelf.recvAccount = getRootController().getUserData().getAccountName();
        mySelf.recvMobile = getRootController().getUserData().getMobileNo();
		showInputPanel();
		
		nfdBaseClient = new NFDBaseClient();
		vibrator = (Vibrator) getRootController().getSystemService(Context.VIBRATOR_SERVICE);
	}
	
	private void initListener() {
		mInputReceiverButton.setOnClickListener(this);
		mShakeReceiverButton.setOnClickListener(this);
		mNextButton.setOnClickListener(this);
		
		mDiscoveredResultList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				TransferReceiver receiver = (TransferReceiver) mReceiverAdapter.getItem(position);
				receiver.transferType = TransferType.FROMLBS;//
				receiver.bizType = Constant.TRADE;
				getRootController().navigateTo("ConfirmView", receiver);
				mSensorManager.unregisterListener(NewReceiverViewController.this);
			}
		});
	}
	
	private void registSensor(){
		if(mShakeReceiverButton.isSelected()){
        	mSensorManager.registerListener(this,mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER),SensorManager.SENSOR_DELAY_NORMAL);
		}
	}
	
	@Override
	protected Object doInBackground(String bizType, String... params) {
		if(DISCOVERREQ.equals(bizType)){
			backTimes = 0;
			nfdBaseClient.nfdDiscover("payment");
			mReceiverAdapter.resetData();
			shakeStartTime = System.currentTimeMillis();
			
			vibrate();
		}else if(DISCOVERREGIST.equals(bizType)){
			UserData userData = getRootController().getUserData();
			if(userData != null)
				nfdBaseClient.nfdRegist(userData.getAccountName() + "#" + userData.getRealName() + "#"+ userData.getMobileNo(),
						userData.getUserId(),"payment");
			
			vibrate();
		}
		return super.doInBackground(bizType, params);
	}
	
	private void showInputPanel() {
        if(getRootController().getWindow() != null && getRootController().getWindow().getCurrentFocus() !=null){
			getRootController().getWindow().getCurrentFocus().clearFocus();
		}
		
		Helper.showInputPanel(getRootController(), mReceiverAccountInput.getEtContent());
		Helper.moveCursorToLast(mReceiverAccountInput.getEtContent().getText());
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.confirmTransformInfoButton:
			confirmInfo();
			break;
		case R.id.inputReceiver:
			if(mInputReceiverButton.isSelected())
				return;
			mInputReceiverCanvas.setVisibility(View.VISIBLE);
			mDiscoverReceiverCanvas.setVisibility(View.GONE);
			
			mSensorManager.unregisterListener(this);
			mInputReceiverButton.setSelected(true);
			mShakeReceiverButton.setSelected(false);
			showInputPanel();
			break;
		case R.id.shakeReceiver:
			if(mShakeReceiverButton.isSelected())
				return;
			
			luanchShakeApp();
			mInputReceiverButton.setSelected(false);
			mShakeReceiverButton.setSelected(true);
			registSensor();
			
			Helper.hideInputPanel(getRootController(), getRootController().getCurrentFocus());
			try {
				IClientObserverPeer observerPeer = nfdBaseClient.getClientObserverPeer();
				if(observerPeer != null && observerPeer.isDiscovering()){
					showLoadingCanvas();
				}else{
					showIndexView();
				}
			} catch (RemoteException e) {
				e.printStackTrace();
			}
			break;
		default:
			break;
		}
	}

	private void luanchShakeApp() {
		/*AlipayLogAgent.onEvent(getRootController(),
				Constants.MONITORPOINT_EVENT, 
				"Y", 
				"", 
				"09999988",
				"1.0",
				storageStateInfo.getValue(Constants.STORAGE_PRODUCTID), 
				getRootController().getUserId(), 
				storageStateInfo.getValue(Constants.STORAGE_PRODUCTVERSION), 
				storageStateInfo.getValue(Constants.STORAGE_CLIENTID),
				Constants.EVENTTYPE_SUPERTRANSFERREADYSHAKE);*/
	}
	
	private void discoveredTime(long startTime,long endTime) {
		/*AlipayLogAgent.onEvent(getRootController(),
				Constants.MONITORPOINT_EVENT, 
				(endTime - startTime) + "", 
				"", 
				"09999988",
				"1.0",
				storageStateInfo.getValue(Constants.STORAGE_PRODUCTID), 
				getRootController().getUserId(), 
				storageStateInfo.getValue(Constants.STORAGE_PRODUCTVERSION), 
				storageStateInfo.getValue(Constants.STORAGE_CLIENTID),
				Constants.EVENTTYPE_SHAKESUCCESSUSEDTIME);*/
	}

	private void confirmInfo() {
		String inputAccount = mReceiverAccountInput.getText().trim();
		String errorText = "";
		transferReceiver = new TransferReceiver();
		if(inputAccount.matches("1\\d{10}")){//手机号
			transferReceiver.recvMobile = inputAccount;
			transferReceiver.recvAccount = inputAccount;
//			new BizAsyncTask(PhoneBindingQuery.BIZ_TAG,true,getRootController().getText(R.string.verifingAccount) + "").execute(inputAccount);
			getRootController().navigateTo("ConfirmView", transferReceiver);
		}else if(inputAccount.matches(".+@.+")){//邮箱
			transferReceiver.bizType = Constant.TRADE;
			transferReceiver.recvAccount = inputAccount;
			Helper.hideInput(getRootController(), mReceiverAccountInput);
			getRootController().navigateTo("ConfirmView", transferReceiver);
		}else if(inputAccount.matches("\\d+") && inputAccount.length() != 11){
			errorText = getRootController().getResources().getString(R.string.phone_format_tip) + "";
		}else if(inputAccount.matches("[0-9&&[^1]]\\d{10}")){
			errorText = getRootController().getResources().getString(R.string.phone_format_error) + "";
		}else {
			errorText = getRootController().getResources().getString(R.string.account_format_tip) + "";
		}
		
		String opResult = "";//埋点操作成功还是失败
		if(!"".equals(errorText)){
			opResult = "N";
			RootController rootController = getRootController();
			rootController.getDataHelper().showDialog(rootController, R.drawable.infoicon, 
					rootController.getResources().getString(R.string.StrConfirmPayTitle), errorText, 
					rootController.getResources().getString(R.string.Ensure), null, null, null, null, null);
		}else{
			opResult = "Y";
		}
		
		/*StorageStateInfo storageStateInfo = StorageStateInfo.getInstance();
		AlipayLogAgent.onEvent(getRootController(), 
				Constants.MONITORPOINT_EVENT, 
				opResult, 
				"", 
				"09999988",
				"1.0",
				storageStateInfo.getValue(Constants.STORAGE_PRODUCTID), 
				getRootController().getUserId(), 
				storageStateInfo.getValue(Constants.STORAGE_PRODUCTVERSION), 
				storageStateInfo.getValue(Constants.STORAGE_CLIENTID),
				Constants.EVENTTYPE_CONFIRMTRANSFERACCONTBUTTONCLICK);*/
	}
	   
	@Override
	public void onSensorChanged(SensorEvent event) {
		int sensorType = event.sensor.getType();
        float[] values = event.values;
        
        if (mKeyguardManager.inKeyguardRestrictedInputMode())
           return;
        
        if (sensorType == Sensor.TYPE_ACCELEROMETER) {
        	 if (Math.abs(values[0]) > 18 || Math.abs(values[1]) > 18 || Math.abs(values[2]) > 18) {
                nfdBaseClient.bindService(getRootController().getApplicationContext());
                
                nfdBaseClient.registCallback(this);
                hasVibratored = false;
                registClientInfo();
                discoverClientInfo();
	        }
        }
	}
	
	private BizAsyncTask registClientTask;
	private boolean hasVibratored;
	private void registClientInfo() {
        if (registClientTask != null && registClientTask.getStatus() == AsyncTask.Status.RUNNING)
            return;
        
        IClientObserverPeer observerPeer = nfdBaseClient.getClientObserverPeer();
        try {
			if(observerPeer == null || !observerPeer.hasRegisted()){
				registClientTask = new BizAsyncTask(DISCOVERREGIST,false);
				registClientTask.execute();
			}
		} catch (RemoteException e) {
			e.printStackTrace();
		}
    }
	
	private synchronized void vibrate() {
		if(!hasVibratored){
			vibrator.vibrate(180);
			hasVibratored = true;
		}
	}

	private BizAsyncTask discoverTask;
	private void discoverClientInfo() {
		try {
			IClientObserverPeer observerPeer = nfdBaseClient.getClientObserverPeer();
			if((discoverTask != null && discoverTask.getStatus() == AsyncTask.Status.RUNNING) || (observerPeer != null && observerPeer.isDiscovering()))
				return;
			
			if(mediaPlayer != null && mediaPlayer.isPlaying())
				return;
			
			if(mediaPlayer != null )
				mediaPlayer.release();
			mediaPlayer = MediaPlayer.create(getRootController(), R.raw.shake);
			if(mediaPlayer != null){
        		mediaPlayer.start();
				mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
					@Override
					public void onCompletion(MediaPlayer mp) {
						startDiscoverTask();
					}
				});
			}else{
				startDiscoverTask();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	private void startDiscoverTask() {
		discoverTask = new BizAsyncTask(DISCOVERREQ, false);
		discoverTask.execute();
	}

	@Override
	public void onAccuracyChanged(Sensor sensor, int accuracy) {
	}
	
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == BLUETOOTH_REQUEST_DISCOVERABLE && resultCode == 0) {
    		notifyBluetooth();
        } else {
            try {
                if (nfdBaseClient.getClientObserverPeer() != null)
                	nfdBaseClient.getClientObserverPeer().modifyBluetoothName();
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }
    }
	
	private void notifyBluetooth() {
		if (bluetoothLock != null)
			synchronized (bluetoothLock) {
				bluetoothLock.notify();
			}
	}
	
	@Override
	protected void onPreDoInbackgroud(String bizType) {
		if(DISCOVERREQ.equals(bizType)/* || LBSDISCOVER.equals(bizType)*/){
			showLoadingView();
			mDiscoverAgainText.setVisibility(View.GONE);
			mDiscoverLoading.setVisibility(View.VISIBLE);
		}
	}
	
	@Override
	public void onPause() {
		super.onPause();
		if(mSensorManager != null)
			mSensorManager.unregisterListener(this);
	}
	
	@Override
	public void onStop() {
		super.onStop();
		if(mSensorManager != null)
			mSensorManager.unregisterListener(this);
	}
	
	private void showIndexView(){
		mInputReceiverCanvas.setVisibility(View.GONE);
		mDiscoverReceiverCanvas.setVisibility(View.VISIBLE);
		mIndexView.setVisibility(View.VISIBLE);
		mLoadingView.setVisibility(View.GONE);
		mResultView.setVisibility(View.GONE);
		mNoResultView.setVisibility(View.GONE);
		mFirstOpenView.setVisibility(View.VISIBLE);
		mShakeImage.setBackgroundResource(R.drawable.discovering_loading_1);
		
		/*if(((Integer.parseInt(VERSION.SDK)>13) && getRootController().getPackageManager().hasSystemFeature(PackageManager.FEATURE_NFC))){
			mNfcSupportText.setVisibility(View.VISIBLE);
			mNfcUsageInfo.setVisibility(View.VISIBLE);
		}else{
			mNfcSupportText.setVisibility(View.GONE);
			mNfcUsageInfo.setVisibility(View.GONE);
		}*/
	}
	
	private void showLoadingView(){
		mInputReceiverCanvas.setVisibility(View.GONE);
		mDiscoverReceiverCanvas.setVisibility(View.VISIBLE);
		mIndexView.setVisibility(View.GONE);
		mLoadingView.setVisibility(View.VISIBLE);
		mResultView.setVisibility(View.GONE);
		
		showDiscoveringAni();
	}
	
	private void showLoadingCanvas(){
		mInputReceiverCanvas.setVisibility(View.GONE);
		mDiscoverReceiverCanvas.setVisibility(View.VISIBLE);
		mLoadingCanvas.setVisibility(View.VISIBLE);
	}
	
	private void showDiscoveringAni(){
		discoverAnimation = (AnimationDrawable)mDiscoveringImage.getBackground();
		discoverAnimation.start();
		mWakeLock.acquire(30000);
	}
	
	private void showResultView(){
		mIndexView.setVisibility(View.GONE);
		mLoadingView.setVisibility(View.GONE);
		mResultView.setVisibility(View.VISIBLE);
		mDiscoverResult.setVisibility(View.VISIBLE);
	}
	
	private void showNoResultView(){
		showIndexView();
		mFirstOpenView.setVisibility(View.GONE);
		mNoResultView.setVisibility(View.VISIBLE);
		mShakeImage.setBackgroundResource(R.drawable.discover_nobody);
		mDiscoverResult.setVisibility(View.GONE);
	}
	
	private boolean fromNFC;
	@Override
	public void intentCallBack(Object params) {
		super.intentCallBack(params);
		shakeStartTime = System.currentTimeMillis();
		String nfcStr = (String) params;
		if(nfcStr != null && mShakeReceiverButton.isSelected()){
			List<String> receiverStr = new ArrayList<String>();
			receiverStr.add(nfcStr);
			fromNFC = true;
			try {
				nfdBaseClient.registCallback(this);
				nfdBaseClient.getClientCallback().refresh(receiverStr);
			} catch (RemoteException e) {
				e.printStackTrace();
			}
		}
	}
	
	@Override
	public void onDestroy() {
		super.onDestroy();
		if(mediaPlayer != null){
			mediaPlayer.stop();
			mediaPlayer.release();
		}
//		unBindService();
	}

	@Override
	public void binderStartActivity(String packageName, String className, int iCallingPid,Bundle bundle,IClientObserverPeer nfdService) {
		getRootController().countMeNotTemporary(true);
		Intent discoverableIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_DISCOVERABLE);
        discoverableIntent.putExtra(BluetoothAdapter.EXTRA_DISCOVERABLE_DURATION, 300);
        getRootController().startActivityForResult(discoverableIntent, BLUETOOTH_REQUEST_DISCOVERABLE);
        bluetoothLock = bundle;
	}

	@Override
	public void binderRefresh(final List<String> clientInfos,final IClientObserverPeer nfdService) {
		getRootController().runOnUiThread(new Runnable() {
			@Override
			public void run() {
				LogUtil.logOnlyDebuggable("xxxx", "return from discover" + clientInfos);
				try {
					if (clientInfos != null && clientInfos.size() > 0 && (nfdService != null && !nfdService.isDiscoverTimeout()) || fromNFC) {
						backTimes ++;
						List<TransferReceiver> nfdRecievers = processRetReceiver(clientInfos);
						if (nfdRecievers.size() > 0) {
							if (backTimes == 1) {
								if(mediaPlayer != null )
									mediaPlayer.release();
								mediaPlayer = MediaPlayer.create(getRootController(),R.raw.shake_success);
								if(mediaPlayer != null)
					        		mediaPlayer.start();
								
								discoveredTime(shakeStartTime,System.currentTimeMillis());
							}

							mReceiverAdapter.setDataAndRefresh(nfdRecievers);
							String resultText = getRootController().getText(R.string.found_total_receiver) +"";
							mDiscoverResult.setText(resultText.replace("$s$", mReceiverAdapter.getCount()+""));
							
							if (discoverAnimation!= null && discoverAnimation.isRunning())
								discoverAnimation.stop();
							showResultView();
						}
					} else {
						mDiscoverAgainText.setVisibility(View.GONE);
						mDiscoverLoading.setVisibility(View.VISIBLE);
					}

					if (nfdService != null && !nfdService.isDiscovering() || fromNFC) {
						if (discoverAnimation!= null && discoverAnimation.isRunning())
							discoverAnimation.stop();
						
						if (mReceiverAdapter.getCount() == 0 && mShakeReceiverButton.isSelected()){
							showNoResultView();
						}else {
							mDiscoverAgainText.setVisibility(View.VISIBLE);
							mDiscoverLoading.setVisibility(View.GONE);
						}
					}
					fromNFC = false;
				} catch (RemoteException e) {
					e.printStackTrace();
				}
			}
		});
	}

	@Override
	public void binderRegistReslut(final int resultCode,IClientObserverPeer nfdService) {
		getRootController().runOnUiThread(new Runnable() {
			@Override
			public void run() {
				if(resultCode == 1|| resultCode == 3){
					nfdBaseClient.registTimeOutTimer(5 * 60 * 1000);
				}
			}
		});
	}
}
