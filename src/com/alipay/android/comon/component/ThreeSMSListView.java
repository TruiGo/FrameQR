package com.alipay.android.comon.component;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.graphics.Color;
import android.os.Handler;
import android.os.Message;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.ForegroundColorSpan;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.alipay.android.bizapp.CCR.SmsInfo;
import com.alipay.android.bizapp.CCR.SmsQuery;
import com.alipay.android.client.constant.Constant;
import com.alipay.android.client.util.AlipayDataStore;
import com.eg.android.AlipayGphone.R;

public class ThreeSMSListView extends LinearLayout{
	
	private Context context;
	
	private LinearLayout content1Layout;
	private LinearLayout content2Layout;
	private TextView content1;
	private TextView content2;
	private TextView content3;

	private AlipayDataStore mAlipayDataStore;

	private LinearLayout mSmsProgressLayout = null;
	private ProgressBar mProgressBar = null;
	private ArrayList<String> smsFormatArray;//当前银行短信账单
	protected static final int STOP = 0;
	
	private String currentBankMark = null;

	public ThreeSMSListView(Context context) {
		super(context);
	}
	
	public ThreeSMSListView(Context context,AttributeSet attrs){
		super(context, attrs);
		this.context = context;
		LayoutInflater inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		addView(inflater.inflate(R.layout.three_sms_list_layout, null));
		loadAllVariables();
		mAlipayDataStore = new AlipayDataStore(context); 
	}
	private void loadAllVariables() {
		content1Layout = (LinearLayout)findViewById(R.id.Content1Layout);
		content1 = (TextView)findViewById(R.id.Content1);
		content2Layout = (LinearLayout)findViewById(R.id.Content2Layout);
		content2 = (TextView)findViewById(R.id.Content2);
		content3 = (TextView)findViewById(R.id.Content3);
		mSmsProgressLayout = (LinearLayout)findViewById(R.id.SmsProgressLayout);
		mProgressBar = (ProgressBar)findViewById(R.id.QueryBarProgress);
	}
	/**
	 * 显示短信列表
	 */
	public void show(String bankMark){
		currentBankMark = bankMark;
		this.setVisibility(View.VISIBLE);
		queryCurrentBankBill();
	}
	/**
	 * 查询当前银行账单
	 */
	public void queryCurrentBankBill() {
		mSmsProgressLayout.setVisibility(View.VISIBLE);
		mProgressBar.setVisibility(View.VISIBLE);
		Thread mThread = new Thread(new Runnable() {
			@Override
			public void run() {
				smsFormatArray = new ArrayList<String>();
				JSONObject currentBankSmsFormat = getCurrentBankSmsFormat(currentBankMark);
				if(currentBankSmsFormat != null){
					String address = currentBankSmsFormat.optString(Constant.RPF_CCR_SMSPHONENUMBER);
//					String address = "1252015910903584";
					String body = currentBankSmsFormat.optString(Constant.RPF_CCR_SMSKEYWORD);
					SmsQuery smsQuery = new SmsQuery(context);
					List<SmsInfo> smsList = smsQuery.getSmsInfo(address, body);
					for(int i=0;i<smsList.size();i++){
						smsFormatArray.add((smsList.get(i)).getSmsbody());
					}
				}
				
				Message msg = new Message();  
                msg.what = STOP;  
                mHandler.sendMessage(msg); 
				
			}
		});
		mThread.start();
	}
	
	private Handler mHandler = new Handler(){  
        public void handleMessage(Message msg){  
            switch (msg.what) {  
            case STOP:  
            	mProgressBar.setVisibility(View.GONE);
            	mSmsProgressLayout.setVisibility(View.GONE);
            	smsListSetContent(smsFormatArray);
                break;  
            }  
        }  
    };
	/**
	 * 获取当前银行所对应的短信模板
	 * @param bankMark
	 * @return
	 */
	private JSONObject getCurrentBankSmsFormat(String bankMark){
		String smsFormat = mAlipayDataStore.getString(AlipayDataStore.BANK_CCR_SMS_FORMAT);
		JSONObject currentBankSmsFormat = null;
		try {
			JSONArray smsFormatArray = new JSONArray(smsFormat);
			for(int i=0;i<smsFormatArray.length();i++){
				JSONObject currentSmsFormatInfo = smsFormatArray.getJSONObject(i);
				if(currentSmsFormatInfo.optString(Constant.RPF_CCR_BANKMAKE).equals(bankMark)){
					currentBankSmsFormat = currentSmsFormatInfo;
					return currentBankSmsFormat;
				}
			}
		} catch (JSONException e) {
			e.printStackTrace();
		} 
		return currentBankSmsFormat;
	}
	/**
	 * 布局
	 * 
	 * @param listContent2
	 */
	private void smsListSetContent(ArrayList<String> listContent) {
		int size = listContent.size();
		if(size>3){
			size = 3;
		}
		switch (size) {
		case 0:
			content3.setVisibility(View.VISIBLE);
			String text = context.getResources().getString(R.string.SmsNullTip);
			content3.setText(context.getResources().getString(R.string.SmsNullTip));
			content3.setGravity(Gravity.CENTER);
			content2Layout.setVisibility(View.GONE);
			content1Layout.setVisibility(View.GONE);
			break;
		case 1:
			content3.setVisibility(View.VISIBLE);
			content3.setGravity(Gravity.LEFT);
			content3.setText(listContent.get(0));
			content2Layout.setVisibility(View.GONE);
			content1Layout.setVisibility(View.GONE);
			checkedDigital(content3, Color.RED);
			break;
		case 2:
			content3.setVisibility(View.VISIBLE);
			content3.setGravity(Gravity.LEFT);
			content2Layout.setVisibility(View.VISIBLE);
			content3.setText(listContent.get(0));
			content2.setText(listContent.get(1));
			content1Layout.setVisibility(View.GONE);
			checkedDigital(content3, Color.RED);
			checkedDigital(content2, Color.RED);
			break;
		case 3:
			content3.setVisibility(View.VISIBLE);
			content3.setGravity(Gravity.LEFT);
			content2Layout.setVisibility(View.VISIBLE);
			content1Layout.setVisibility(View.VISIBLE);
			content3.setText(listContent.get(0));
			content2.setText(listContent.get(1));
			content1.setText(listContent.get(2));
			checkedDigital(content3, Color.RED);
			checkedDigital(content2, Color.RED);
			checkedDigital(content1, Color.RED);
			break;
		}

	}
	/**
	 * ���ö������ݸ�ʽ
	 * 
	 * @param textView
	 * @param fontColor
	 */
	private void checkedDigital(TextView textView, int fontColor) {
		String text = (textView.getText()).toString();
		SpannableString msp = new SpannableString(text);
		char currentChar;
		for (int i = 0; i < text.length(); i++) {
			currentChar = text.charAt(i);
			if (Character.isDigit(currentChar) == true || currentChar == '.') {
				msp.setSpan(new ForegroundColorSpan(fontColor), i, i + 1,
						Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
			}
		}
		textView.setText(msp);
	}

	/**
	 * ��ȡ���ж�Ӧ�Ķ�������3��
	 * 
	 * @param bankMark
	 * @return
	 */
	public ArrayList<String> getThreeBankMessages(String bankMark) {
		ArrayList<String> threeBankMessages = new ArrayList<String>();
		String content3 = mAlipayDataStore.getString(bankMark
				+ AlipayDataStore.BANK_CCR_SMS_CONTENT3);
		if (!content3.equals("")) {
			threeBankMessages.add(content3);
		}
		String content2 = mAlipayDataStore.getString(bankMark
				+ AlipayDataStore.BANK_CCR_SMS_CONTENT2);
		if (!content2.equals("")) {
			threeBankMessages.add(content2);
		}
		String content1 = mAlipayDataStore.getString(bankMark
				+ AlipayDataStore.BANK_CCR_SMS_CONTENT1);
		if (!content1.equals("")) {
			threeBankMessages.add(content1);
		}
		return threeBankMessages;
	}
}
