package com.alipay.android.client.baseFunction;

import java.util.ArrayList;
import java.util.HashMap;

import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Build;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.alipay.android.appHall.bussiness.CallBackFilter;
import com.alipay.android.client.RootActivity;
import com.alipay.android.client.util.AlipayDataStore;
import com.alipay.android.client.util.BaseHelper;
import com.alipay.android.client.util.TelephoneInfoHelper;
import com.alipay.android.comon.component.ProgressDiv;
import com.alipay.android.log.Constants;
import com.alipay.android.net.RequestMaker;
import com.alipay.android.net.http.HttpRequestMaker;
import com.alipay.android.push.ServiceManager;
import com.alipay.android.util.LogUtil;
import com.alipay.platform.core.Command;
import com.alipay.platform.view.ActivityMediator;
import com.alipay.platform.view.OnDataChangeListener;
import com.eg.android.AlipayGphone.R;

public class AlipayNotifiSetting extends RootActivity implements OnClickListener, OnDataChangeListener{

	public static final String UPDATE_PUSHSETTING_ID = "17";
    private RequestMaker mRequestMaker;
    private CallBackFilter mBackFilter;
	
	 private TextView mAlipayTitleItemName;
	 private CheckBox mReceiveCheckBox,mVoiceRemindCheckBox,mVibratorRemindCheckBox;
	 private TextView mTimeSettingResult,mVoiceRemindText,mVibratorRemindText,mTimePeriodSettingViewText;
	 private RelativeLayout mTimePeriodSettingView;
	 private AlertDialog mAlertDialog;
	 private View settingView;
	 private EditText startTimeView,endTimeView;
	 private int startTime = 8;
	 private int endTime = 23;
	 private String mInitStartTime = "";
	 private String mInitEndTime = "";
	 private String receiveCheckedStatus, voiceCheckedStatus,vibratorCheckedStatus;
	 
	private ProgressDiv mProgress = null;
	private int mErrorType = UPDATE_START;
//	private static final int UPDATE_FINISH = -2;
	private static final int UPDATE_START = -1;

	
	 
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		
		mRequestMaker = new HttpRequestMaker(getApplicationContext(),R.raw.interfaces);
		mBackFilter = new CallBackFilter(this);
		
		setContentView(R.layout.alipay_notification_setting);
		loadAllVariables();
		
		//告知push服务设置文件名
		ServiceManager serviceManager = new ServiceManager(this);
        serviceManager.setSettingPreferences(AlipayDataStore.SETTING_INFOS);
		
		initSettingData();
	}
	
	private void loadAllVariables()
	{
		settingView =LayoutInflater.from(getApplicationContext()).inflate(R.layout.alipay_notification_timeperiod_setting, null);
		startTimeView = (EditText) settingView.findViewById(R.id.startTime);
		endTimeView = (EditText)settingView.findViewById(R.id.endtime);
		
		mAlipayTitleItemName = (TextView) findViewById(R.id.title_text);
		mAlipayTitleItemName.setText(R.string.MenuNotifiSetting);
		
		mReceiveCheckBox = (CheckBox) findViewById(R.id.ReceiveCheckBox);
		mReceiveCheckBox.setOnCheckedChangeListener(mOnCheckedChangeListener);
		mVoiceRemindCheckBox = (CheckBox) findViewById(R.id.VoiceRemindCheckBox);
		mVibratorRemindCheckBox = (CheckBox) findViewById(R.id.VibratorRemindCheckBox);
		mTimePeriodSettingView = (RelativeLayout) findViewById(R.id.TimePeriodSettingView);
		mTimePeriodSettingView.setOnClickListener(this);
		mTimeSettingResult = (TextView) findViewById(R.id.TimeSettingResult);
		
		mVoiceRemindText = (TextView) findViewById(R.id.VoiceRemindText);
		mVibratorRemindText = (TextView) findViewById(R.id.VibratorRemindText);
		mTimePeriodSettingViewText = (TextView) findViewById(R.id.TimePeriodSettingViewText);
		
		mAlertDialog = createTimePeriodSettingDialog();	
	}
	
private  void initSettingData()
{
	AlipayDataStore OtherDataSettings = new AlipayDataStore(AlipayNotifiSetting.this);
	
	receiveCheckedStatus =OtherDataSettings.getString(AlipayDataStore.NOTIFICATION_SETTING_RECEIVECHECKEDSTATUS);
	if(receiveCheckedStatus.equals("")) {
		initCheckBoxStatus("true", mReceiveCheckBox);
	} else
		initCheckBoxStatus(receiveCheckedStatus,mReceiveCheckBox);
//	if(!receiveCheckedStatus.equals("") && receiveCheckedStatus.equals("ON"))
//	{
//		mReceiveCheckBox.setChecked(true);
//	}else
//	{
//		mReceiveCheckBox.setChecked(false);
//	}
	
	voiceCheckedStatus = OtherDataSettings.getString(AlipayDataStore.NOTIFICATION_SETTING_VOICECHECKEDSTATUS);
	initCheckBoxStatus(voiceCheckedStatus,mVoiceRemindCheckBox);
//	if(!voiceCheckedStatus.equals("") && voiceCheckedStatus.equals("ON"))
//	{
//		mVoiceRemindCheckBox.setChecked(true);
//	}else
//	{
//		mVoiceRemindCheckBox.setChecked(false);
//	}
	
	vibratorCheckedStatus = OtherDataSettings.getString(AlipayDataStore.NOTIFICATION_SETTING_VIBRATORCHECKEDSTATUS);
	initCheckBoxStatus(vibratorCheckedStatus,mVibratorRemindCheckBox);
//	if(!vibratorCheckedStatus.equals("") && vibratorCheckedStatus.equals("ON"))
//	{
//		mVibratorRemindCheckBox.setChecked(true);
//	}else
//	{
//		mVibratorRemindCheckBox.setChecked(false);
//	}
	
	mInitStartTime = OtherDataSettings.getString(AlipayDataStore.NOTIFICATION_SETTING_STARTTIME);
	mInitEndTime  = OtherDataSettings.getString(AlipayDataStore.NOTIFICATION_SETTING_ENDTIME);
	startTimeView.setText(mInitStartTime);
	endTimeView.setText(mInitEndTime);
	
	if((!mInitStartTime.equals("")) && (!mInitEndTime.equals("")))
	{
		startTime = Integer.valueOf(mInitStartTime).intValue();
		endTime = Integer.valueOf(mInitEndTime).intValue();
	} else {
		//默认值
		startTimeView.setText(String.valueOf(startTime));
		endTimeView.setText(String.valueOf(endTime));
	}
	
	mTimeSettingResult.setText("  " + startTime + ":00--" + endTime + ":00");
}	
	
private void initCheckBoxStatus(String checkBoxStatus,CheckBox checkBox)
{
	if(checkBoxStatus.equals(""))
	{
		checkBox.setChecked(false);
	}else
	{
		if(checkBoxStatus.equals("true"))
		{
			checkBox.setChecked(true);
		}else
		{
			checkBox.setChecked(false);
		}
	}
}
	
 private CheckBox.OnCheckedChangeListener mOnCheckedChangeListener = new CheckBox.OnCheckedChangeListener()
 {

	@Override
	public void onCheckedChanged(CompoundButton buttonView, boolean isChecked)
	{
		
		if(!isChecked)
		{   
			receiveCheckedStatus = "false";
			mVoiceRemindCheckBox.setClickable(false);
			mVoiceRemindCheckBox.setEnabled(false);
			mVibratorRemindCheckBox.setClickable(false);
			mVibratorRemindCheckBox.setEnabled(false);
			mTimePeriodSettingView.setClickable(false);
			
			changeTextColor(AlipayNotifiSetting.this.getResources().getColor(R.color.TextColorTip));
		}else
		{
			receiveCheckedStatus = "true";
			mVoiceRemindCheckBox.setClickable(true);
			mVoiceRemindCheckBox.setEnabled(true);
			mVibratorRemindCheckBox.setClickable(true);
			mVibratorRemindCheckBox.setEnabled(true);
			mTimePeriodSettingView.setClickable(true);
			
			changeTextColor(AlipayNotifiSetting.this.getResources().getColor(R.color.TextColorBlack));
		}
	}
	 
 };
 private void changeTextColor(int color)
 {
	 mVoiceRemindText.setTextColor(color);
	 mVibratorRemindText.setTextColor(color);
	 mTimePeriodSettingViewText.setTextColor(color);
	 mTimeSettingResult.setTextColor(color);
	 
 }
@Override
public void onClick(View v) 
 {
	// TODO Auto-generated method stub

	mAlertDialog.show();
 }
 
 private AlertDialog createTimePeriodSettingDialog()
 {
	 AlertDialog.Builder builder = new Builder(AlipayNotifiSetting.this);
	 builder.setTitle(R.string.NotificationDialogTitle);
	 builder.setView(settingView);
	 builder.setPositiveButton(R.string.DialogSetting,new DialogInterface.OnClickListener()
	 {
		@Override
		public void onClick(DialogInterface dialog, int which) {
//			checkInputData();
//			saveSettingData();
			checkAndSaveSettingTimeData();
		}
	});
	 
	 builder.setNegativeButton(R.string.DialogCancel,new DialogInterface.OnClickListener()
	 {

		@Override
		public void onClick(DialogInterface dialog, int which) {
		}
		 
	 });
	 
	return builder.create();
	 
 }

@Override
public boolean onKeyDown(int keyCode, KeyEvent event) {
	// TODO Auto-generated method stub
	if (keyCode == KeyEvent.KEYCODE_BACK) 
	{   
		if(mAlertDialog != null && !mAlertDialog.isShowing())
		{
			if (isChanged()) {
				//发送数据更新请求
				requestUpdateSettings();
			} else {
				this.finish();
			}
		}else {
			 mAlertDialog.dismiss();
		}
		return true;
	}   
	return false;
}

	private void requestUpdateSettings(){
		mErrorType = UPDATE_START;
		
	    ActivityMediator activityMediator = new ActivityMediator(this);
	    ArrayList<String> params = new ArrayList<String>();

	    TelephoneInfoHelper mTelephoneInfoHelper = TelephoneInfoHelper.getTelephoneHelper(this);
	    params.add(mTelephoneInfoHelper.getProductId());//ProductId
	    params.add(mTelephoneInfoHelper.getProductVersion());//ProductVersion
	    
	    params.add(mReceiveCheckBox.isChecked()?"ON":"OFF");//开关设置
	    
	    String setting_start;
	    String setting_end;
	    if (startTime > 9 ) {
	    	setting_start = String.format("%s:00:00", startTime);
	    } else {
	    	setting_start = String.format("0%s:00:00", startTime);
	    }
	    if (endTime > 9 ) {
	    	setting_end = String.format("%s:00:00", endTime);
	    } else {
	    	setting_end = String.format("0%s:00:00", endTime);
	    }

	    LogUtil.logOnlyDebuggable("push_setting", "requestUpdateSettings setting_start:" 
	    		+ setting_start +", setting_end:"+setting_end);
	    
	    params.add(setting_start);	//开始时间
	    params.add(setting_end);	//结束时间
	    
	    String sessionId = getSessionId();
	    params.add(sessionId);// sessionId
        params.add(mTelephoneInfoHelper.getClientID());// clientID
	    
	    activityMediator.sendCommand(UPDATE_PUSHSETTING_ID,  "clean", mRequestMaker, params);

		if(mProgress == null)
		{
			mProgress = getDataHelper().showProgressDialogWithCancelButton(this, null,
					getResources().getString(R.string.PleaseWait),
					false, true, getDataHelper().cancelListener,
					getDataHelper().cancelBtnListener);
		}
	}
 
 /**
  * 保存偏好设置除时间段以外的数据
  * */
 private void saveSettingOtherData()
 {
	 AlipayDataStore OtherDataSettings = new AlipayDataStore(AlipayNotifiSetting.this);
	 
	 receiveCheckedStatus = mReceiveCheckBox.isChecked()?"true":"false";
	 voiceCheckedStatus = mVoiceRemindCheckBox.isChecked()?"true":"false";
	 vibratorCheckedStatus = mVibratorRemindCheckBox.isChecked()?"true":"false";
	 OtherDataSettings.putString(AlipayDataStore.NOTIFICATION_SETTING_RECEIVECHECKEDSTATUS, receiveCheckedStatus);
	 OtherDataSettings.putString(AlipayDataStore.NOTIFICATION_SETTING_VOICECHECKEDSTATUS, voiceCheckedStatus);
	 OtherDataSettings.putString(AlipayDataStore.NOTIFICATION_SETTING_VIBRATORCHECKEDSTATUS, vibratorCheckedStatus);
	 OtherDataSettings.putString(AlipayDataStore.NOTIFICATION_SETTING_STARTTIME, startTime + "");
	 OtherDataSettings.putString(AlipayDataStore.NOTIFICATION_SETTING_ENDTIME, endTime + "");
	 
	 //启动push服务——低于2.0的不需要启动
     int expectedVersion = 4;
     if(Integer.valueOf(Build.VERSION.SDK).intValue() > expectedVersion){
		 ServiceManager serviceManager = new ServiceManager(this);	
		 if (receiveCheckedStatus!=null && receiveCheckedStatus.equals("false")) {
//			 Intent intent = new Intent();
//			 intent.setAction("com.alipay.android.push.DISCONNECT");
//			 intent.setClass(AlipayNotifiSetting.this, NotificationService.class);
			 //告知serviceManager停止push服务
			 serviceManager.stopService();
		 } else {
//			 Intent intent = new Intent();
//			 intent.setAction("com.alipay.android.push.CONNECT");
//			 intent.setClass(AlipayNotifiSetting.this, NotificationService.class);
			 //告知serviceManager启动push服务
			 serviceManager.startService(2);
		 }
     }
     
     this.finish();
 }
 
 /**
  * 保存偏好设置时间段的数据
  * */
 private void checkAndSaveSettingTimeData()
 {
	 String	startTimeValue = startTimeView.getText().toString().trim();
	 String endTimeValue = endTimeView.getText().toString().trim();
	 if((startTimeValue == null) || (startTimeValue != null && startTimeValue.equals("") )
	     || endTimeValue == null || (endTimeValue != null && endTimeValue.equals("") ))
	 {
		 mAlertDialog.dismiss();
		 
		 BaseHelper.recordWarningMsg(this,getResources().getString(R.string.TimeInputError1),Constants.NOTIFYSETTINGVIEW);
		 
		 getDataHelper().showDialog(AlipayNotifiSetting.this, R.drawable.infoicon, 
				 getResources().getString(R.string.WarngingString),
				  getResources().getString(R.string.TimeInputError1), 
				  getResources().getString(R.string.Ensure),
				  null, null, null, null, null);
		 return;
	 } else
	 {
		 int tempStartTime = Integer.valueOf(startTimeValue).intValue();
		 int tempEndTime = Integer.valueOf(endTimeValue).intValue();
		 if(tempStartTime > 24 || tempEndTime > 24)
		 {
			 mAlertDialog.dismiss();
			 
			 BaseHelper.recordWarningMsg(this,getResources().getString(R.string.TimeInputError1),Constants.NOTIFYSETTINGVIEW);
			 
			 getDataHelper().showDialog(AlipayNotifiSetting.this, R.drawable.infoicon, 
					 getResources().getString(R.string.WarngingString),
					  getResources().getString(R.string.TimeInputError1), 
					  getResources().getString(R.string.Ensure),
					  null, null, null, null, null);
			 return;
		 } else if(tempStartTime >= tempEndTime)
		 {
			 mAlertDialog.dismiss();
			 
			 BaseHelper.recordWarningMsg(this,getResources().getString(R.string.TimeInputError2),Constants.NOTIFYSETTINGVIEW);
			 
			 getDataHelper().showDialog(AlipayNotifiSetting.this, R.drawable.infoicon, 
					 getResources().getString(R.string.WarngingString),
					  getResources().getString(R.string.TimeInputError2), 
					  getResources().getString(R.string.Ensure),
					  null, null, null, null, null);
			 return;
		 } else {
			 startTime = tempStartTime;
			 endTime = tempEndTime;
		 }
	 }
	 
	 mTimeSettingResult.setText("  " + startTime + ":00--" + endTime + ":00");

	 // 保存时间段设置数据
	 AlipayDataStore settings = new AlipayDataStore(AlipayNotifiSetting.this);
	 settings.putString(AlipayDataStore.NOTIFICATION_SETTING_STARTTIME, startTime + "");
	 settings.putString(AlipayDataStore.NOTIFICATION_SETTING_ENDTIME, endTime + "");
 }

	private  boolean isChanged() {
		boolean ret = false;
		
		//获取之前保存的数据
		AlipayDataStore settings = new AlipayDataStore(AlipayNotifiSetting.this);
		String pushStatus = settings.getString(AlipayDataStore.NOTIFICATION_SETTING_RECEIVECHECKEDSTATUS);
		String voiceStatus = settings.getString(AlipayDataStore.NOTIFICATION_SETTING_VOICECHECKEDSTATUS);
		String vibrateStatus = settings.getString(AlipayDataStore.NOTIFICATION_SETTING_VIBRATORCHECKEDSTATUS);
//		String startTime = settings.getString(AlipayDataStore.NOTIFICATION_SETTING_STARTTIME);
//		String endTime = settings.getString(AlipayDataStore.NOTIFICATION_SETTING_ENDTIME);
		
		//获取当前的数据
		String curpushStatus = mReceiveCheckBox.isChecked()?"true":"false";
		String curvoiceStatus = mVoiceRemindCheckBox.isChecked()?"true":"false";
		String curvibrateStatus = mVibratorRemindCheckBox.isChecked()?"true":"false";
		String curstartTime = startTimeView.getText().toString().trim();
		String curendTime = endTimeView.getText().toString().trim();
		
		//比较判断
		if (!curpushStatus.equals(pushStatus) 
				|| !curvoiceStatus.equals(voiceStatus) || !curvibrateStatus.equals(vibrateStatus)
				|| !curstartTime.equals(mInitStartTime) || !curendTime.equals(mInitEndTime)) {
			ret = true;
		}
		
		return ret;
	}
	 
 	void closeProgress(){
	     try {
	         if(mProgress!=null){
	             mProgress.dismiss();
	             mProgress = null;
	         }
	     } catch (Exception e) {
	         //e.printStackTrace();
	     }
	 }
 
	@Override
	public Context getContext() {
		// TODO Auto-generated method stub
		return AlipayNotifiSetting.this;
	}
	
	@Override
	public void onCancel(Command command) {
		// TODO Auto-generated method stub
		
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public void onComplete(Command command) {
	     //
	     HashMap<String, Object> hmResponse = (HashMap<String, Object>) command.getResponseData();

	     if(!mBackFilter.processCommand(hmResponse)){
	         if(command.getmId().equals(UPDATE_PUSHSETTING_ID)){
	        	 if(mErrorType == UPDATE_START) {
					mErrorType = UPDATE_START;
					saveSettingOtherData();
				}  
	         }
	     }

	     closeProgress();
	}
	
	@Override
	public void onFail(Command command) {
		// TODO Auto-generated method stub
		closeProgress();
		
		//通过toast提示用户
		Toast.makeText(getApplicationContext(), getResources().getString(R.string.PushSettingError),
			     Toast.LENGTH_SHORT).show();
		
		//失败，数据不做保存，直接退出
		this.finish();
		
//	    String content = command.getResponseMessage();
//	    BaseHelper.showDialog(AlipayNotifiSetting.this, getString(R.string.Error), content,
//	         R.drawable.erroricon);
	}
	
	@Override
	public boolean preCancel(Command command) {
		// TODO Auto-generated method stub
		return false;
	}
	
	@Override
	public boolean preFail(Command command) {
		// TODO Auto-generated method stub
		return false;
	}
	
	@Override
	public void setRuleId(String ruleId) {
		// TODO Auto-generated method stub
		
	}
	@Override
	public String getRuleId() {
		// TODO Auto-generated method stub
		return null;
	}
}
