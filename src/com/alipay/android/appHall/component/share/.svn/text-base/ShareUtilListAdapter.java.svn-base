/**
 * 
 */
package com.alipay.android.appHall.component.share;

import java.util.ArrayList;
import java.util.HashMap;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.text.ClipboardManager;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ImageView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.eg.android.AlipayGphone.R;

public class ShareUtilListAdapter extends SimpleAdapter implements OnItemClickListener{
	
	public static final String APP_PACKAGENAME_BLUETOOTH="com.android.bluetooth";
	public static final String APP_PACKAGENAME_SMS="com.android.mms";
	public static final String APP_ICONS="icons";
	public static final String APP_LABLE="label";
	public static final String APP_PACKAGENAME="app";
	public static final String APP_NAME="name";
	public static final String SHARE_COPY="copytoqq";
	public static final String SEND_CONTENT="sendcontent";

	private Activity mActivity;
	private ArrayList<HashMap<String, Object>> mArrayList;
	private Intent mIntent;
	private AlertDialog mDialog;
	private int mListSize=0;
	public ShareUtilListAdapter(Activity activity,
			ArrayList<HashMap<String, Object>> shareappdata, Intent shareintent,AlertDialog dialog) {
		super(activity, shareappdata, -1, 
	             null, 
	             null);
		mActivity = activity;
		mArrayList = shareappdata;
		mListSize = mArrayList.size();
		mIntent = shareintent;
		mDialog = dialog;
	}


	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		// TODO Auto-generated method stub
		View view = null;
		if(convertView!=null)
			view = convertView;
		else{
			view = mActivity.getLayoutInflater().inflate(R.layout.share_item, null);
		}
		((ImageView)view.findViewById(R.id.appicon)).setImageDrawable((Drawable) mArrayList.get(position).get(APP_ICONS));
		((TextView)view.findViewById(R.id.appname)).setText( (CharSequence) mArrayList.get(position).get(APP_LABLE));
		TextView copyIntro = (TextView)view.findViewById(R.id.copyinfo);
		if(position==mListSize-1){			
			copyIntro.setVisibility(View.VISIBLE);
			copyIntro.setText((CharSequence) mArrayList.get(position).get(SHARE_COPY));
		}else{
			copyIntro.setVisibility(View.GONE);
		}
		return view;
	}


	@Override
	public void onItemClick(AdapterView<?> arg0, View arg1, int position, long arg3) {
		if(position==mListSize-1){
			ClipboardManager clip = (ClipboardManager)mActivity.getSystemService(Context.CLIPBOARD_SERVICE);		
			String temp = (String) (mArrayList.get(position).get(SEND_CONTENT));
			clip.setText(temp); // 复制
			Toast toast = Toast.makeText(mActivity,mActivity.getString(R.string.copytoclip),5000);
            toast.show();
		}else{
			mIntent.setClassName((String) mArrayList.get(position).get(APP_PACKAGENAME),(String) mArrayList.get(position).get(APP_NAME));
			mIntent.putExtra(Intent.EXTRA_TEXT, (String) mArrayList.get(position).get(SEND_CONTENT));
			mActivity.startActivity(mIntent);
		}
		mDialog.dismiss();
	}
	
}
