package com.alipay.android.client;

import org.json.JSONException;
import org.json.JSONObject;

import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;

import com.alipay.android.client.baseFunction.UpdateHelper;
import com.eg.android.AlipayGphone.R;

public class UpdateActivity extends RootActivity implements OnClickListener{
	private Button ensureButton;
	private Button cancelButton;
	private TextView messageText;
	private String updateParam;
	private String updateUrl;
	private String updateTip;
	private JSONObject updateObject;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.alert_dialog);
		ensureButton = (Button) findViewById(R.id.button1);
		cancelButton = (Button) findViewById(R.id.button2);
		messageText = (TextView) findViewById(R.id.message);
		
		updateParam = getIntent().getStringExtra("updateParam");
		try {
			updateObject = new JSONObject(updateParam);
			updateUrl = updateObject.optString("url");
			updateTip = updateObject.optString("tip");
			messageText.setText(updateTip);
		} catch (JSONException e) {
			e.printStackTrace();
		}
		
		ensureButton.setText(R.string.update_now);
		cancelButton.setText(R.string.update_later);
		
		ensureButton.setBackgroundResource(R.drawable.popwindow_btn_left);
		ensureButton.setTextColor(this.getResources()
				.getColorStateList(R.drawable.main_btn_color));
		cancelButton.setBackgroundResource(R.drawable.popwindow_btn_right);
		cancelButton
            .setTextColor(this.getResources().getColorStateList(R.drawable.sub_btn_color));
		
		ensureButton.setOnClickListener(this);
		cancelButton.setOnClickListener(this);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.button1:
			if(updateUrl != null && !"".equals(updateUrl)){
				UpdateHelper myUpdator = new UpdateHelper(this);
				myUpdator.update(updateUrl);
			}else{
				finish();
			}	
			break;
		case R.id.button2:
			finish();
			break;
		default:
			break;
		}
	}
}