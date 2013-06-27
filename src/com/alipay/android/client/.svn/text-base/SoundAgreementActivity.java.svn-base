package com.alipay.android.client;

import com.eg.android.AlipayGphone.R;

import android.os.Bundle;
import android.webkit.WebSettings;
import android.webkit.WebView;

public class SoundAgreementActivity extends RootActivity {

	private WebView mSoundAgreement;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.sound_agreement);
		mSoundAgreement = (WebView) findViewById(R.id.soundAgreementWebView);
		WebSettings settings =mSoundAgreement.getSettings();
		settings.setSupportZoom(false);
		mSoundAgreement.loadUrl("file:///android_asset/sound_accredit_agreement.html");
		
	}

}
