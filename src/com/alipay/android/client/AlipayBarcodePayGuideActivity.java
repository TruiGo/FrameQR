package com.alipay.android.client;

import android.app.Activity;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

import com.alipay.android.client.util.AlipayDataStore;
import com.eg.android.AlipayGphone.R;

public class AlipayBarcodePayGuideActivity extends Activity implements OnClickListener {
    ImageView iv = null;
    Button mbtnStart = null;
    TextView mtxtTitle = null;
    private CheckBox mShowGuide = null;
    
//    private GifView gifPay;
//    private GifView gifWhere;
    
    private Activity mActivity = null;
    private AlipayDataStore mAlipayDataStore = null;

    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        setContentView(R.layout.alipay_barcodepay_guide);
        mActivity = this;
        mAlipayDataStore = new AlipayDataStore(this);
        
//        gifPay = (GifView)findViewById(R.id.gifPay);
//        gifPay.setGifImage(R.drawable.barcode_guide_pay);
//        gifPay.setOnClickListener(this);
//        gifPay.setGifImageType(GifImageType.COVER);
//        
//        gifWhere = (GifView)findViewById(R.id.gifWhere);
//        //gifWhere.setGifImage(R.drawable.barcode_guide_where);
//        gifWhere.setOnClickListener(this);
//        gifWhere.setVisibility(View.GONE);
//        
//        DisplayMetrics dmScreen = BaseHelper.getDisplayMetrics(mActivity);
//        //等比按尺寸放大条码显示 
//		int desiredWidth = (int) ((dmScreen.widthPixels) * 0.95);
//		int desiredHeight = (int) ((dmScreen.heightPixels) * 0.95);
//		
//		int smallerDimension = desiredWidth < desiredHeight ? desiredWidth : desiredHeight;
//		desiredWidth = smallerDimension;
//		desiredHeight = (int)(smallerDimension*0.75f);
//		
//		gifPay.setShowDimension(desiredWidth, desiredHeight);
		
		
        
        WebView mWebView = (WebView)findViewById(R.id.guide_view);
        mWebView.getSettings().setJavaScriptEnabled(true);  
        mWebView.getSettings().setPluginsEnabled(true);  
        
//        String html =
//        	"<object width=\"240\" height=\"240\"> <param name=\"movie\" value=\"file:///android_asset/barcode_guide.swf\"> <embed src=\"file:///android_asset/barcode_guide.swf\" width=\"240\" height=\"240\"> </embed> </object>";
//    	String mimeType = "text/html";
//    	String encoding = "utf-8";
//    	mWebView.loadDataWithBaseURL("null", html, mimeType, encoding, "");

//        int desiredWidth = 240;
//		int desiredHeight = 240;
//		int smallerDimension = -1;
//		
//	    WindowManager manage=getWindowManager();
//	    Display display=manage.getDefaultDisplay();
//	    desiredHeight=display.getHeight();
//	    desiredWidth=display.getWidth();
//
//        smallerDimension = desiredWidth < desiredHeight ? desiredWidth : desiredHeight;
//        LayoutParams params = new LayoutParams(smallerDimension, smallerDimension);
//        mWebView.setLayoutParams(params);
        
//        mWebView.loadUrl("http://d1.sina.com.cn/201103/29/293011_2.gif");  
        
    	mtxtTitle = (TextView) findViewById(R.id.title_text);
        mtxtTitle.setText("条码支付使用向导");
        
        mShowGuide = (CheckBox)findViewById(R.id.GuideShowCheckBox);
        String firstGuide = mAlipayDataStore.getString(AlipayDataStore.BARCODE_SHOW_GUIDE);
        if (firstGuide != null && firstGuide.equals("false")) {	//不需要显示向导
        	mShowGuide.setChecked(true);
        }
        
        //只要进入使用向导，则下次就不是第一次
        mAlipayDataStore.putString(AlipayDataStore.BARCODE_SHOW_GUIDE, "false");

        mbtnStart = (Button) findViewById(R.id.ButtonUserGuide);
        mbtnStart.setOnClickListener(this);
        mbtnStart.setText("开始体验");
    }
    
    @Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		
//		gifPay.showAnimation();
	}
	
	@Override
    protected void onPause() {
        // TODO Auto-generated method stub
        super.onPause();
        
//      gifPay.showCover(); 
    }
	
	@Override
    protected void onDestroy() {
        // TODO Auto-generated method stub
        super.onDestroy();
        
        if (mShowGuide.isChecked()) {
        	mAlipayDataStore.putString(AlipayDataStore.BARCODE_SHOW_GUIDE, "false");
        } else {
        	mAlipayDataStore.putString(AlipayDataStore.BARCODE_SHOW_GUIDE, "true");
        }
    }

    @Override
    public void onClick(View v) {
    	switch (v.getId()) {
	    	case R.id.ButtonUserGuide:
	    		AlipayBarcodeDisplay.startBarcodeDisplayActivity(mActivity);
	            this.finish();
	    		break;
	    		
	    	}
    }
    
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
        	this.finish();
			return true;
        } else if (keyCode == KeyEvent.KEYCODE_SEARCH){
        	//捕获此事件，但不做处理
        	return true;
        }
        return false;
    }
}