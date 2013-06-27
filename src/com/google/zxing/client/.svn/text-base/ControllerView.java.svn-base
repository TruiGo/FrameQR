package com.google.zxing.client;

import android.content.Context;
import android.content.pm.PackageManager;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.eg.android.AlipayGphone.R;
import com.google.zxing.client.camera.CameraManager;

public class ControllerView extends RelativeLayout implements OnTouchListener{
//	private SearchBar mSearchBar;
	private BarCodeScanView mBarScanView;
	private ImageButton mScanTorch;
	private View mContainer;
	Context ctx;
	private boolean bFlashOn;
	CaptureActivityHandler mCaptureActivityHandler;
	private CameraManager mCm;
	private FrameLayout mask;
	
	
	public void setTextHint(String textHint){
		TextView typeTipText = (TextView)this.findViewById(R.id.scan_type_tip);
		typeTipText.setText(textHint);
	}
	
	public ControllerView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		// TODO Auto-generated constructor stub
		this.ctx = context;
	}

	public ControllerView(Context context, AttributeSet attrs) {
		super(context, attrs);
		// TODO Auto-generated constructor stub
		this.ctx = context;
	}

	public ControllerView(Context context) {
		super(context);
		// TODO Auto-generated constructor stub
		this.ctx = context;
	}
	
	@Override
	protected void onFinishInflate() {
		// TODO Auto-generated method stub
		super.onFinishInflate();
		
		init();
	}
	
	private void init(){
		bFlashOn = false;
		mBarScanView = (BarCodeScanView) findViewById(R.id.barCodeScan);

		if (!ctx.getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA_FLASH)) {
			findViewById(R.id.scan_torch).setVisibility(View.GONE);
		}
		mContainer = findViewById(R.id.container);
		mScanTorch = (ImageButton)findViewById(R.id.scan_torch);
		mask = (FrameLayout)findViewById(R.id.mask);
		mask.setOnTouchListener(this);
	}
	
	private boolean isSurportLight;
	
	public void setIsSurportLight(boolean isSurportLight){
		this.isSurportLight = isSurportLight;
	}
	
	
	
	public void setIsFlashOn(boolean bFlashOn){
		this.bFlashOn = bFlashOn;
	}
	
	public void setFlashlightMode() {
		if (!isSurportLight) {
			mScanTorch.setVisibility(View.GONE);
			return;
		}
		bFlashOn = !bFlashOn;
		if (bFlashOn) {
			mScanTorch.setImageResource(R.drawable.scan_flashlight_effect);
		} else {
			mScanTorch.setImageResource(R.drawable.scan_flashlight);
		}
		mCm.setLight(bFlashOn);
	}

	
	
	public void setCaptureActivityHandler(CaptureActivityHandler handler){
		mCaptureActivityHandler = handler;
	}
	
	
	public void setCameraManager(CameraManager mCm){
		this.mCm = mCm;
	}
	
	
	public void stopScan() {
		if (mCaptureActivityHandler != null) {
			mCaptureActivityHandler.stopAutoFocus();
			mCaptureActivityHandler.stopDecode();
		}

	}

	public void restartScan() {
		if (mCaptureActivityHandler != null) {
			mBarScanView.setVisibility(View.VISIBLE);
			mCaptureActivityHandler.requestAutoFocus();
			mCaptureActivityHandler.restartPreviewAndDecode();
		}
	}
	
	public void onPause(){

	}
	
	public void onResume(){
		
	}

	@Override
	public boolean onTouch(View v, MotionEvent event) {
		// TODO Auto-generated method stub
		if(v.getId()==R.id.mask){
//			hideTextSearchBar();
			return true;
		}
		return false;
	}
}
