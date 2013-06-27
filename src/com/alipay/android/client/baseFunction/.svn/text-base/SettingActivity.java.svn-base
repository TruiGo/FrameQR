package com.alipay.android.client.baseFunction;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.alipay.android.appHall.common.CacheSet;
import com.alipay.android.client.RootActivity;
import com.alipay.android.client.constant.Constant;
import com.alipay.android.client.util.AlipayDataStore;
import com.eg.android.AlipayGphone.R;
/**
 * 更多——设置
 * @author caidie.wang
 *
 */
public class SettingActivity extends RootActivity implements OnClickListener{
	
	private AlipayDataStore mAlipayDataStore;
	
	private TextView mTitleView ;
	
	private RelativeLayout mNotificationSetting;
	private TextView mNotificationSettingStatus;
	
	private RelativeLayout querySmsSetting;
	private TextView smsSettingStatus;
	private CheckBox shakeSettingStatus;
	private RelativeLayout shakeSetting;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.more_setting);
		mAlipayDataStore = new AlipayDataStore(this);
		loadAllVariables();
		
	}
	
	@Override
	protected void onResume() {
		super.onResume();
		initData();
	}

	private void loadAllVariables(){
		mTitleView = (TextView)findViewById(R.id.title_text);
		mNotificationSetting = (RelativeLayout)findViewById(R.id.notification_setting);
		mNotificationSetting.setOnClickListener(this);
		mNotificationSettingStatus = (TextView)findViewById(R.id.notification_setting_status);
		querySmsSetting = (RelativeLayout)findViewById(R.id.query_sms_setting);
		querySmsSetting.setOnClickListener(this);
		smsSettingStatus = (TextView)findViewById(R.id.sms_setting_status);
		
		shakeSetting = (RelativeLayout) findViewById(R.id.shake_setting);
		shakeSetting.setVisibility(View.GONE);
		shakeSettingStatus = (CheckBox) findViewById(R.id.shake_setting_status);
		
		CacheSet cacheSet = CacheSet.getInstance(this);
    	String shakeOpen = cacheSet.getString("shakeOpen");
        if("false".equals(shakeOpen)){
        	shakeSettingStatus.setChecked(false);
        }else if("true".equals(shakeOpen) || "".equals(shakeOpen)){
        	shakeSettingStatus.setChecked(true);
        }
		
		shakeSettingStatus.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
			CacheSet cacheSet = CacheSet.getInstance(SettingActivity.this);
			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				cacheSet.putString("shakeOpen", isChecked +"");
			}
		});
	}
	
	private void initData() {
		mTitleView.setText(R.string.MenuTxt_Setting);
		//读取本地保存的数据，收通知服务的开关状态
    	String receiveStatus = mAlipayDataStore.getString(AlipayDataStore.NOTIFICATION_SETTING_RECEIVECHECKEDSTATUS);
    	if(receiveStatus.equals("") || receiveStatus.equals("true")){
    		mNotificationSettingStatus.setText(getResources().getString(R.string.ON));
    	}else{
    		mNotificationSettingStatus.setText(getResources().getString(R.string.OFF));
    	}
    	//读取本地保存数据，信用卡获取账单短信开关状态
    	boolean isAuthorize = mAlipayDataStore.getBoolean(Constant.IS_AUTHORIZE_SMS_QUERY, false);
    	if(isAuthorize){
    		smsSettingStatus.setText(getResources().getString(R.string.ON));
    	}else{
    		smsSettingStatus.setText(getResources().getString(R.string.OFF));
    	}
	}
	
	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.notification_setting:
			jumpToNotifiSetting(this);
			break;
		case R.id.query_sms_setting:
			jumpToSmsSetting(this);
			break;
		default:
			break;
		}
	}
	
	private static void jumpToNotifiSetting(final Activity context) {
        Intent tIntent = new Intent(context, AlipayNotifiSetting.class);
        context.startActivity(tIntent);
    }
	
	private void jumpToSmsSetting(final Activity context) {
		Intent intent = new Intent(context,AlipayCCRSmsSetting.class);
		context.startActivity(intent);
	}

}
