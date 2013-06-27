package com.alipay.android.bizapp.CCR;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;

/**
 * class description：获取手机中的各种短信信息<BR>
 * PS： 需要权限 <uses-permission android:name="android.permission.READ_SMS" /><BR>
 * @author caidie.wang
 */

public class SmsQuery {
	
	//全部短信   
    private static final String SMS_ALL   = "content://sms/";
	private Uri uri;
	private ContentResolver resolver;
	List<SmsInfo> infos;

	public SmsQuery(Context context) {
		infos = new ArrayList<SmsInfo>();
		resolver = context.getContentResolver();
	}

	/**
	 * Role:获取短信的各种信息 
	 */
	public List<SmsInfo> getSmsInfo() {
		uri = Uri.parse(SMS_ALL);
		String[] projection = new String[] { "_id", "address", "person",
				"body", "date", "type" };
		Cursor cusor = resolver.query(uri, projection, null, null,
				"date desc");
		int nameColumn = cusor.getColumnIndex("person");
		int phoneNumberColumn = cusor.getColumnIndex("address");
		int smsbodyColumn = cusor.getColumnIndex("body");
		int dateColumn = cusor.getColumnIndex("date");
		int typeColumn = cusor.getColumnIndex("type");
		if (cusor != null) {
			while (cusor.moveToNext()) {
				SmsInfo smsinfo = new SmsInfo();
				smsinfo.setName(cusor.getString(nameColumn));
				smsinfo.setDate(cusor.getString(dateColumn));
				smsinfo.setPhoneNumber(cusor.getString(phoneNumberColumn));
				smsinfo.setSmsbody(cusor.getString(smsbodyColumn));
				smsinfo.setType(cusor.getString(typeColumn));
				infos.add(smsinfo);
			}
			cusor.close();
		}
		return infos;
	}
	
	/**
	 * Role:获取短信的各种信息 
	 */
	public List<SmsInfo> getSmsInfo(String address,String containBody) {
		uri = Uri.parse(SMS_ALL);
		String[] projection = new String[] { "_id", "address", "person",
				"body", "date", "type" };
		Cursor cusor = resolver.query(uri, projection, "address like ? and body like ?", new String[]{address,"%"+containBody+"%"},
				"date desc");
		int nameColumn = cusor.getColumnIndex("person");
		int phoneNumberColumn = cusor.getColumnIndex("address");
		int smsbodyColumn = cusor.getColumnIndex("body");
		int dateColumn = cusor.getColumnIndex("date");
		int typeColumn = cusor.getColumnIndex("type");
		if (cusor != null) {
			while (cusor.moveToNext()) {
				SmsInfo smsinfo = new SmsInfo();
				smsinfo.setName(cusor.getString(nameColumn));
				smsinfo.setDate(cusor.getString(dateColumn));
				smsinfo.setPhoneNumber(cusor.getString(phoneNumberColumn));
				smsinfo.setSmsbody(cusor.getString(smsbodyColumn));
				smsinfo.setType(cusor.getString(typeColumn));
				infos.add(smsinfo);
			}
			cusor.close();
		}
		return infos;
	}


}
