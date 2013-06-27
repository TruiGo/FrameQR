package com.alipay.android.comon.component;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;

import com.eg.android.AlipayGphone.R;

public class ContactPickerView extends FrameLayout{
	private ImageView pickButton;
	private EditTextWithButton phoneNumber;
	
	public ContactPickerView(Context context) {
		super(context);
	}
	
	public ContactPickerView(Context context, AttributeSet set) {
		super(context, set);
		LayoutInflater.from(context).inflate(R.layout.contact_picker, this, true);
	}
	
	@Override
	protected void onFinishInflate() {
		super.onFinishInflate();
		loadAllVirables();
	}

	private void loadAllVirables() {
		phoneNumber = (EditTextWithButton) findViewById(R.id.phoneNumber);
		pickButton = (ImageView) findViewById(R.id.pickButton);
	}
	
	public ImageView getPickButton(){
		return pickButton;
	}
	
	public EditText getInputText(){
		return phoneNumber.getEtContent();
	}
}
