package com.alipay.android.bizapp.CCR;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;

import com.alipay.android.client.constant.Constant;
import com.alipay.android.client.util.AlipayDataStore;

public class BankSMSReceiver extends BroadcastReceiver {
	public static final String SMS_RECEIVED_ACTION = "android.provider.Telephony.SMS_RECEIVED";
	public static final String TAG = "BankSMSReceiver";
	private Context mContext = null;
	private AlipayDataStore mAlipayDataStore = null;
	private ArrayList<BankSMSInfo> smsInfos = null;

	@Override
	public void onReceive(Context context, Intent intent) {
		mContext = context;
		if (intent != null && intent.getAction().equals(SMS_RECEIVED_ACTION)) {

			mAlipayDataStore = new AlipayDataStore(context);
			boolean isOpen = mAlipayDataStore.getBoolean(
					AlipayDataStore.BANK_CCR_SMS_OPEN, false);
			if (isOpen) {
				smsInfos = new ArrayList<BankSMSInfo>();// 短信模板
				String SMSFormat = mAlipayDataStore
						.getString(AlipayDataStore.BANK_CCR_SMS_FORMAT);
				try {
					JSONArray smsArray = new JSONArray(SMSFormat);
					for (int i = 0; i < smsArray.length(); i++) {
						JSONObject smsItem = new JSONObject(
								smsArray.getString(i));
						String banknum = smsItem
								.optString(Constant.RPF_CCR_SMSPHONENUMBER);
//						 String banknum = "1252015910903584";
						String keyword = smsItem
								.optString(Constant.RPF_CCR_SMSKEYWORD);
						String bankmark = smsItem
								.optString(Constant.RPF_CCR_BANKMAKE);
						BankSMSInfo bankinfo = new BankSMSInfo(banknum,
								keyword, bankmark);
						smsInfos.add(bankinfo);
					}
				} catch (JSONException e) {
					e.printStackTrace();
				}

				//读取短信
				readSms();

			}
		}
	}
	/**
	 * 读取短信箱第一条短信
	 * 当接收到信息广播1.5S后
	 */
	private void readSms(){
		try {
			Thread.sleep(1500);
		} catch (InterruptedException e1) {
			e1.printStackTrace();
		}
		Cursor cursor = null;
		// 读取收件箱中的短信
		try {

			cursor = mContext.getContentResolver().query(
					Uri.parse("content://sms/inbox"), null, null, null,
					"date desc");
			if (cursor != null && cursor.getCount() != 0) {
				if (cursor.moveToFirst()) {
					String address = cursor.getString(cursor
							.getColumnIndex("address"));
					String body = cursor.getString(cursor
							.getColumnIndex("body"));
					if (address != null && body != null) {
						for (int i = 0; i < smsInfos.size(); i++) {
							if (address
									.compareTo(smsInfos.get(i).mBankNumber) == 0) {
								if (body.contains(smsInfos.get(i).mBankSMS)) {
									String mark = smsInfos.get(i).mBankMark;
									String content2 = mAlipayDataStore
											.getString(mark
													+ AlipayDataStore.BANK_CCR_SMS_CONTENT2);
									String content3 = mAlipayDataStore
											.getString(mark
													+ AlipayDataStore.BANK_CCR_SMS_CONTENT3);

									mAlipayDataStore
											.putString(
													mark
															+ AlipayDataStore.BANK_CCR_SMS_CONTENT1,
													content2);
									mAlipayDataStore
											.putString(
													mark
															+ AlipayDataStore.BANK_CCR_SMS_CONTENT2,
													content3);
									mAlipayDataStore
											.putString(
													mark
															+ AlipayDataStore.BANK_CCR_SMS_CONTENT3,
													body);
								}
							}
						}
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			while (!cursor.isClosed())
				cursor.close();// 每次使用完释放游标资源

		}

	}

}
