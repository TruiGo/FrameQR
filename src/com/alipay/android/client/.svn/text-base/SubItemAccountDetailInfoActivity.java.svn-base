package com.alipay.android.client;

import com.alipay.android.client.RootActivity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.alipay.android.client.constant.Constant;
import com.alipay.android.client.util.BaseHelper;
import com.eg.android.AlipayGphone.R;

public class SubItemAccountDetailInfoActivity extends RootActivity{
	private TextView tText;
	
	private LinearLayout noteLayout;

	private Intent accountInfoIntent;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.alipay_operationinfo_320_480);
		
		loadVairables();
	}

	private void loadVairables() {
	    TextView title = (TextView)findViewById(R.id.title_text);
	    title.setText(R.string.AccountQueryAccountDetail);
	    
	    LinearLayout listDivide = (LinearLayout)findViewById(R.id.ListDivide);
		BaseHelper.fixBackgroundRepeat(listDivide);
	    
		String tString;
		accountInfoIntent = getIntent();
		tText = (TextView)findViewById(R.id.AQOFlow);
		tString = accountInfoIntent.getStringExtra(Constant.RPF_TRANSLOGID);
		tText.setText(tString);

		tText = (TextView) findViewById(R.id.AQOType);
		tString = accountInfoIntent.getStringExtra(Constant.RPF_TRANSTYPE);
		tText.setText(tString);

		tText = (TextView) findViewById(R.id.AQOMoney);
		tString = accountInfoIntent.getStringExtra(Constant.RPF_MONEY);
		
		TextView dealCategory = (TextView)findViewById(R.id.AQOMoneyTitle);
		if(tString.startsWith("-")){
			dealCategory.setText(getResources().getText(R.string.AccountQueryOut));
			tString = tString.substring(1);
			tText.setTextColor(getResources().getColor(R.color.TextColorYellow));
		}else{
			dealCategory.setText(getResources().getText(R.string.AccountQueryMoney));
			tText.setTextColor(getResources().getColor(R.color.TextColorGreen));
		}
		tText.setText(tString);

		tText = (TextView) findViewById(R.id.AQOSite);
		tString = accountInfoIntent.getStringExtra(Constant.RPF_TRANSINSTITUTION);
		tText.setText(tString);
		
		//余额
		tText = (TextView) findViewById(R.id.AQOBalance);
		tString = accountInfoIntent.getStringExtra(Constant.RPF_BALANCE);
		tText.setText(tString);

		tText = (TextView) findViewById(R.id.AQOTime);
		tString = accountInfoIntent.getStringExtra(Constant.RPF_DATE);
		tText.setText(tString);

		noteLayout = (LinearLayout)findViewById(R.id.AQOInfo6);
		tText = (TextView) findViewById(R.id.AQONote);
		tString = accountInfoIntent.getStringExtra(Constant.RPF_TRANSMEMO);
		tText.setText(tString);
	}
	
	@Override
	protected void onResume() {
		super.onResume();
		if(accountInfoIntent != null){
			String note = accountInfoIntent.getStringExtra(Constant.RPF_TRANSMEMO);
			if(note == null || note.equals("")){
				noteLayout.setVisibility(View.GONE);
			}else{
				noteLayout.setVisibility(View.VISIBLE);
			}
		}
	}
}
