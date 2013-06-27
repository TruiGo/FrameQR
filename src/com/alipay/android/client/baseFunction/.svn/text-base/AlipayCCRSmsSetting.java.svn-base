package com.alipay.android.client.baseFunction;

import android.os.Bundle;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;
import android.widget.CompoundButton.OnCheckedChangeListener;

import com.alipay.android.client.RootActivity;
import com.alipay.android.client.constant.Constant;
import com.alipay.android.client.util.AlipayDataStore;
import com.eg.android.AlipayGphone.R;

/**
 * 信用卡账单短信开发设置界面
 * @author caidie.wang
 *
 */
public class AlipayCCRSmsSetting extends RootActivity {
	
	private AlipayDataStore mAlipayDataStore;
	
	private TextView mTitle;
	private CheckBox mSmsQueryAutchorize;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.ccr_sms_setting);
		mAlipayDataStore = new AlipayDataStore(AlipayCCRSmsSetting.this);
		loadAllVariables();
		
	}
	
	@Override
	protected void onResume() {
		super.onResume();
		initData();
	}

	private void loadAllVariables() {
		mTitle = (TextView)findViewById(R.id.title_text);
		mSmsQueryAutchorize = (CheckBox)findViewById(R.id.SmsQueryAutchorizeCheckBox);
		mSmsQueryAutchorize = (CheckBox)findViewById(R.id.SmsQueryAutchorizeCheckBox);
    	mSmsQueryAutchorize.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				mAlipayDataStore.putBoolean(Constant.IS_AUTHORIZE_SMS_QUERY, isChecked);
			}
		});
	}
	
	private void initData() {
		mTitle.setText(R.string.CCR_SMS_QUERY_AUTCHORIZE);
		//读取本地保存的数据，获取查看短信状态
    	boolean isAuthorize = mAlipayDataStore.getBoolean(Constant.IS_AUTHORIZE_SMS_QUERY, false);
    	mSmsQueryAutchorize.setChecked(isAuthorize);
	}
}
