/*
 * Copyright (C) 2008 ZXing authors
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.google.zxing.client;

import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.FeatureInfo;
import android.content.pm.PackageManager;
import android.content.res.AssetFileDescriptor;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.Point;
import android.graphics.Rect;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Vibrator;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.Display;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.WindowManager;
import android.widget.FrameLayout.LayoutParams;

import com.alipay.android.appHall.common.CacheSet;
import com.alipay.android.client.RootActivity;
import com.alipay.android.client.constant.Constant;
import com.alipay.android.comon.component.StyleAlertDialog;
import com.alipay.android.log.AlipayLogAgent;
import com.alipay.android.log.Constants;
import com.eg.android.AlipayGphone.R;
import com.etao.kaka.decode.DecodeResult;
import com.google.zxing.ResultMetadataType;
import com.google.zxing.client.camera.CameraManager;

/**
 * The barcode reader activity itself. This is loosely based on the
 * CameraPreview example included in the Android SDK.
 * 
 * @author dswitkin@google.com (Daniel Switkin)
 * @author Sean Owen
 */
public final class BarcodeCaptureActivity extends RootActivity implements
		SurfaceHolder.Callback {
	private static final String TAG = BarcodeCaptureActivity.class
			.getSimpleName();

	private static final float BEEP_VOLUME = 0.10f;
	private static final long VIBRATE_DURATION = 200L;

	private static final Set<ResultMetadataType> DISPLAYABLE_METADATA_TYPES;
	static {
		DISPLAYABLE_METADATA_TYPES = new HashSet<ResultMetadataType>(5);
		DISPLAYABLE_METADATA_TYPES.add(ResultMetadataType.ISSUE_NUMBER);
		DISPLAYABLE_METADATA_TYPES.add(ResultMetadataType.SUGGESTED_PRICE);
		DISPLAYABLE_METADATA_TYPES
				.add(ResultMetadataType.ERROR_CORRECTION_LEVEL);
		DISPLAYABLE_METADATA_TYPES.add(ResultMetadataType.POSSIBLE_COUNTRY);
	}

	private enum Source {
		NATIVE_APP_INTENT, PRODUCT_SEARCH_LINK, ZXING_LINK, NONE
	}

	private CaptureActivityHandler mCaptureActivityHandler;

	private MediaPlayer mediaPlayer;
	private DecodeResult lastResult;
	private boolean hasSurface;
	private boolean playBeep;
	private boolean vibrate;
	private Source source;
	private String returnUrlTemplate;

	private Intent accountIntent = null;
	private int mGetCodeType = 0;
	
	private String mHintText = "";

	Rect frame;

	ControllerView mController;

	/**
	 * When the beep has finished playing, rewind to queue up another one.
	 */
	private final OnCompletionListener beepListener = new OnCompletionListener() {
		public void onCompletion(MediaPlayer mediaPlayer) {
			mediaPlayer.seekTo(0);
		}
	};

	public Handler getHandler() {
		return mCaptureActivityHandler;
	}

	private boolean isNexusOne() {
		return Build.MODEL.contains("Nexus One");
	}

	public void setFlashlightMode(View view) {
		mController.setFlashlightMode();
	}

	@Override
	public void onCreate(Bundle icicle) {
		super.onCreate(icicle);

		WindowManager manager = (WindowManager) this
				.getSystemService(Context.WINDOW_SERVICE);
		Display display = manager.getDefaultDisplay();
		Point screenResolution = new Point(display.getWidth(),
				display.getHeight());

		getWindow().setFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON,
				WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

		setContentView(R.layout.barcode_capture);

		LayoutInflater mInflater = LayoutInflater.from(this);
		mController = (ControllerView) mInflater.inflate(
				R.layout.activity_capture_content, null);
		this.addContentView(mController, new LayoutParams(
				LayoutParams.FILL_PARENT, LayoutParams.FILL_PARENT));

		CameraManager.init(getApplication());

		mController.setCaptureActivityHandler(mCaptureActivityHandler);
		mController.setCameraManager(CameraManager.get());

		lastResult = null;
		hasSurface = false;

		// inactivityTimer = new InactivityTimer(this);

		// 此处保留，用来辨别扫一维码还是二维码
		accountIntent = getIntent();
		String getCodeType = accountIntent
				.getStringExtra(Constant.BARCODE_CAPTURE_TYPE);
		if (getCodeType != null && !getCodeType.equals("")) {
			mGetCodeType = Integer.parseInt(getCodeType);
		}
		
		String getHint = accountIntent.getStringExtra(Constant.BARCODE_CAPTURE_HINT);
	    if (getHint != null && !getHint.equals("")) {
	    	mHintText = getHint;
	    } else {
	    	mHintText = getString(R.string.scanQRTip);
	    }
	    
	    mController.setTextHint(mHintText);

		boolean isLightAvailable = false;
		FeatureInfo[] fs = (this).getPackageManager()
				.getSystemAvailableFeatures();
		for (FeatureInfo f : fs) {
			if (PackageManager.FEATURE_CAMERA_FLASH.equals(f.name)) {
				isLightAvailable = true;
			}
		}
		
		// http://code.google.com/p/android/issues/detail?id=15112 nexus one
				// system bug
		if (isLightAvailable&&!isNexusOne()) {
			mController.setIsSurportLight(true);
		}
	}


	@Override
	protected void onUserLeaveHint() {
		// TODO Auto-generated method stub
		super.onUserLeaveHint();
		if (mController!=null) {
			mController.setIsFlashOn(false);
		}
	}

	@Override
	protected void onResume() {
		super.onResume();
		resetStatusView();
		mController.onResume();
		SurfaceView surfaceView = (SurfaceView) findViewById(R.id.preview_view);
		SurfaceHolder surfaceHolder = surfaceView.getHolder();
		if (hasSurface) {
			// The activity was paused but not stopped, so the surface still
			// exists. Therefore
			// surfaceCreated() won't be called, so init the camera here.
			initCamera(surfaceHolder);
		} else {
			// Install the callback and wait for surfaceCreated() to init the
			// camera.
			surfaceHolder.addCallback(this);
			surfaceHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
		}

		source = Source.NATIVE_APP_INTENT;

		resetStatusView();

		SharedPreferences prefs = PreferenceManager
				.getDefaultSharedPreferences(this);
		playBeep = prefs.getBoolean(PreferencesActivity.KEY_PLAY_BEEP, true);
		if (playBeep) {
			// See if sound settings overrides this
			AudioManager audioService = (AudioManager) getSystemService(AUDIO_SERVICE);
			if (audioService.getRingerMode() != AudioManager.RINGER_MODE_NORMAL) {
				playBeep = false;
			}
		}
		vibrate = prefs.getBoolean(PreferencesActivity.KEY_VIBRATE, false);
		initBeepSound();

		if (Constants.firstOpenCM) {
			Constants.firstOpenCM = false;
			Constants.paipaiStep1End = System.currentTimeMillis();
			long paipaiTimeBlock1 = Constants.paipaiStep1End
					- Constants.paipaiStep1Start;
			AlipayLogAgent.writeLog(BarcodeCaptureActivity.this,
					Constants.BehaviourID.MONITOR, "paipai", null, null, null,
					null, null, null, "android", CacheSet.getInstance(this)
							.getString(Constant.CHANNELS), "callCamera",
					paipaiTimeBlock1 + "");
		}
	}

	@Override
	protected void onPause() {
		super.onPause();
		mController.onPause();
		if (mCaptureActivityHandler != null) {
			mCaptureActivityHandler.quitSynchronously();
			mCaptureActivityHandler = null;
			mController.setCaptureActivityHandler(null);
		}
		CameraManager.get().closeDriver();
	}

	@Override
	protected void onDestroy() {
		// inactivityTimer.shutdown();
		super.onDestroy();
		stopScan();
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			if (source == Source.NATIVE_APP_INTENT) {
				setResult(RESULT_CANCELED);
				finish();
				return true;
			} else if ((source == Source.NONE || source == Source.ZXING_LINK)
					&& lastResult != null) {
				resetStatusView();
				if (mCaptureActivityHandler != null) {
					mCaptureActivityHandler
							.sendEmptyMessage(R.id.restart_preview);
				}
				return true;
			}
		} else if (keyCode == KeyEvent.KEYCODE_FOCUS
				|| keyCode == KeyEvent.KEYCODE_CAMERA) {
			// Handle these events so they don't launch the Camera app
			return true;
		}
		return super.onKeyDown(keyCode, event);
	}

	@Override
	public void onConfigurationChanged(Configuration config) {
		// Do nothing, this is to prevent the activity from being restarted when
		// the keyboard opens.
		super.onConfigurationChanged(config);
	}

	public void surfaceCreated(SurfaceHolder holder) {
		if (!hasSurface) {
			hasSurface = true;
			initCamera(holder);
		}
	}

	public void surfaceDestroyed(SurfaceHolder holder) {
		hasSurface = false;
	}

	public void surfaceChanged(SurfaceHolder holder, int format, int width,
			int height) {

	}

	/**
	 * A valid barcode has been found, so give an indication of success and show
	 * the results.
	 * 
	 * @param rawResult
	 *            The contents of the barcode.
	 * @param barcode
	 *            A greyscale bitmap of the camera data which was decoded.
	 */
	public void handleDecode(DecodeResult rawResult, Bitmap barcode) {
		// inactivityTimer.onActivity();
		lastResult = rawResult;
		// historyManager.addHistoryItem(rawResult);
		// if (barcode == null) {
		// // This is from history -- no saved barcode
		// handleDecodeInternally(rawResult, null);
		// } else {
		playBeepSoundAndVibrate();
		// drawResultPoints(barcode, rawResult);
		switch (source) {
		case NATIVE_APP_INTENT:
		case PRODUCT_SEARCH_LINK:
			handleDecodeExternally(rawResult, barcode);
			break;
		case ZXING_LINK:
			// if (returnUrlTemplate == null){
			// handleDecodeInternally(rawResult, barcode);
			// } else {
			// handleDecodeExternally(rawResult, barcode);
			// }
			break;
		case NONE:
			// SharedPreferences prefs =
			// PreferenceManager.getDefaultSharedPreferences(this);
			// if (prefs.getBoolean(PreferencesActivity.KEY_BULK_MODE, false)) {
			// Toast.makeText(this, R.string.msg_bulk_mode_scanned,
			// Toast.LENGTH_SHORT).show();
			// // Wait a moment or else it will scan the same barcode
			// continuously about 3 times
			// if (mCaptureActivityHandler != null) {
			// mCaptureActivityHandler.sendEmptyMessageDelayed(R.id.restart_preview,
			// BULK_MODE_SCAN_DELAY_MS);
			// }
			// resetStatusView();
			// } else {
			// handleDecodeInternally(rawResult, barcode);
			// }
			break;
		}
		// }
	}

	// Briefly show the contents of the barcode, then handle the result outside
	// Barcode Scanner.
	private void handleDecodeExternally(DecodeResult rawResult, Bitmap barcode) {

		String resultstr = rawResult.strCode;
		accountIntent.setData(Uri.parse(resultstr));
		this.setResult(Activity.RESULT_OK, accountIntent);
		finish();
	}

	/**
	 * Creates the beep MediaPlayer in advance so that the sound can be
	 * triggered with the least latency possible.
	 */
	private void initBeepSound() {
		if (playBeep && mediaPlayer == null) {
			// The volume on STREAM_SYSTEM is not adjustable, and users found it
			// too loud,
			// so we now play on the music stream.
			setVolumeControlStream(AudioManager.STREAM_MUSIC);
			mediaPlayer = new MediaPlayer();
			mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
			mediaPlayer.setOnCompletionListener(beepListener);

			AssetFileDescriptor file = getResources().openRawResourceFd(
					R.raw.beep);
			try {
				mediaPlayer.setDataSource(file.getFileDescriptor(),
						file.getStartOffset(), file.getLength());
				file.close();
				mediaPlayer.setVolume(BEEP_VOLUME, BEEP_VOLUME);
				mediaPlayer.prepare();
			} catch (IOException e) {
				mediaPlayer = null;
			}
		}
	}

	private void playBeepSoundAndVibrate() {
		if (playBeep && mediaPlayer != null) {
			mediaPlayer.start();
		}
		if (vibrate) {
			Vibrator vibrator = (Vibrator) getSystemService(VIBRATOR_SERVICE);
			vibrator.vibrate(VIBRATE_DURATION);
		}
	}

	private void initCamera(SurfaceHolder surfaceHolder) {
		try {
			CameraManager.get().openDriver(surfaceHolder);
			CameraManager.get().setDisplayOrientation(90);
		} catch (IOException ioe) {
			Log.w(TAG, ioe);
			displayFrameworkBugMessageAndExit();
			return;
		} catch (RuntimeException e) {
			// Barcode Scanner has seen crashes in the wild of this variety:
			// java.?lang.?RuntimeException: Fail to connect to camera service
			Log.w(TAG, "Unexpected error initializating camera", e);
			displayFrameworkBugMessageAndExit();
			return;
		}
		if (mCaptureActivityHandler == null) {
			mCaptureActivityHandler = new CaptureActivityHandler(this);
			mController.setCaptureActivityHandler(mCaptureActivityHandler);
		}
		restartScan();
	}

	private void displayFrameworkBugMessageAndExit() {
		StyleAlertDialog dialog = new StyleAlertDialog(
				BarcodeCaptureActivity.this, 0, BarcodeCaptureActivity.this
						.getResources().getString(R.string.app_name), "",
				BarcodeCaptureActivity.this.getResources().getString(
						R.string.button_ok), new FinishListener(this), null,
				null, new FinishListener(this));
		dialog.show();
	}

	private void resetStatusView() {
		lastResult = null;
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		return true;
	}

	public void stopScan() {
		if (mController != null) {
			mController.stopScan();
		}

	}

	public void restartScan() {
		if (mController != null) {
			mController.restartScan();
		}
	}

}
