/*
 * Copyright 2009, Embedded General Inc.  All right reserved
 * 
 * draft:Roger
 * 
 */
package com.alipay.android.client.baseFunction;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;

import com.alipay.android.client.RootActivity;
import com.eg.android.AlipayGphone.R;

/**
 * The activity to show About View.
 * </p>
 * @author Roger.Huang
 */
public class AlipayAbout extends RootActivity{
	@SuppressWarnings("unused")
    private static final String TAG = "AlipyAbout";
	private TextView mTitleName=null;
	private TextView mVersion=null;
	
	private Button mPhoneNumber = null;
	
	/** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //get current device width and height.
        DisplayMetrics tDisplayMetrics = new DisplayMetrics();
        Display tDisplay = ((WindowManager)getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay();
        tDisplay.getMetrics(tDisplayMetrics);
  
        setContentView(R.layout.alipay_about_320_480);
        
        loadAllVariables();
    }
    
    private void loadAllVariables(){
    	// Display Item Name.
    	mTitleName = (TextView) findViewById(R.id.title_text);
    	mTitleName.setText(R.string.MenuAbout);
    	mVersion = (TextView)findViewById(R.id.AboutVersion);
    	String pv = getString(R.string.AboutVersionName)+getConfigData().getProductVersion();
    	mVersion.setText(pv);
		
        mPhoneNumber = (Button)findViewById(R.id.AlipayServicePhoneNumberButton);
        mPhoneNumber.setOnClickListener(new OnClickListener()
        {

			@Override
			public void onClick(View v)
			{
//				Intent i = new Intent(Intent.ACTION_SEND);    
//				i.setType("message/rfc822") ; // 真机上使用这行  
//				i.putExtra(Intent.EXTRA_EMAIL, new String[]{"service-mobile@alipay.com"});    
//				i.putExtra(Intent.EXTRA_SUBJECT,getResources().getString(R.string.MailSubject)+getResources().getString(R.string.android_versionName)+")");    
//				i.putExtra(Intent.EXTRA_TEXT,getResources().getString(R.string.MailContent));    
//				startActivity(Intent.createChooser(i, getResources().getString(R.string.ChoiceMail))); 
			    String tel =  mPhoneNumber.getText().toString();
				Intent intent=new Intent(Intent.ACTION_DIAL, Uri.parse("tel:"+tel));
				startActivity(intent);
			}
        });
    }
}
