/*
 * Copyright (C) 2010 ZXing authors
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

package com.google.zxing.client.camera;

import java.util.Collection;
import java.util.List;
import java.util.regex.Pattern;

import com.eg.android.AlipayGphone.R;
import com.google.zxing.client.BarcodeCaptureActivity;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.ImageFormat;
import android.graphics.PixelFormat;
import android.graphics.Point;
import android.hardware.Camera;
import android.hardware.Camera.Parameters;
import android.os.Build;
import android.util.Log;
import android.view.Display;
import android.view.WindowManager;

//final class CameraConfigurationManager {
//
//  private static final String TAG = CameraConfigurationManager.class.getSimpleName();
//
//  private static final int TEN_DESIRED_ZOOM = 27;
//  private static final int DESIRED_SHARPNESS = 30;
//
//  private static final Pattern COMMA_PATTERN = Pattern.compile(",");
//
//  private final Context context;
//  private Point screenResolution;
//  private Point cameraResolution;
//  private int previewFormat;
//  private String previewFormatString;
//
//  CameraConfigurationManager(Context context) {
//    this.context = context;
//  }
//
//  /**
//   * Reads, one time, values from the camera that are needed by the app.
//   */
//  void initFromCameraParameters(Camera camera) {
//    Camera.Parameters parameters = camera.getParameters();
//    previewFormat = parameters.getPreviewFormat();
//    if (previewFormat != PixelFormat.YCbCr_420_SP && previewFormat != PixelFormat.YCbCr_422_SP ) {
//    	parameters.setPreviewFormat(PixelFormat.YCbCr_420_SP);
//    	previewFormat = PixelFormat.YCbCr_420_SP;
//    } else {
//    	//
//    }
//    previewFormatString = parameters.get("preview-format");
//    Log.d(TAG, "Default preview format: " + previewFormat + '/' + previewFormatString);
//    WindowManager manager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
//    Display display = manager.getDefaultDisplay();
//    screenResolution = new Point(display.getWidth(), display.getHeight());
//    Log.d(TAG, "Screen resolution: " + screenResolution);
//    cameraResolution = getCameraResolution(parameters, screenResolution);
//    Log.d(TAG, "Camera resolution: " + screenResolution);
//  }
//
//  /**
//   * Sets the camera up to take preview images which are used for both preview and decoding.
//   * We detect the preview format here so that buildLuminanceSource() can build an appropriate
//   * LuminanceSource subclass. In the future we may want to force YUV420SP as it's the smallest,
//   * and the planar Y can be used for barcode scanning without a copy in some cases.
//   */
//  void setDesiredCameraParameters(Camera camera) {
//    Camera.Parameters parameters = camera.getParameters();
//    Log.d(TAG, "Setting preview size: " + cameraResolution);
//    parameters.setPreviewSize(cameraResolution.x, cameraResolution.y);
//    setFlash(parameters);
//    setZoom(parameters);
//    //setSharpness(parameters);
//    camera.setParameters(parameters);
//  }
//
//  Point getCameraResolution() {
//    return cameraResolution;
//  }
//
//  Point getScreenResolution() {
//    return screenResolution;
//  }
//
//  int getPreviewFormat() {
//    return previewFormat;
//  }
//
//  String getPreviewFormatString() {
//    return previewFormatString;
//  }
//
//  private static Point getCameraResolution(Camera.Parameters parameters, Point screenResolution) {
//
//    String previewSizeValueString = parameters.get("preview-size-values");
//    // saw this on Xperia
//    if (previewSizeValueString == null) {
//      previewSizeValueString = parameters.get("preview-size-value");
//    }
//
//    Point cameraResolution = null;
//
//    if (previewSizeValueString != null) {
//      Log.d(TAG, "preview-size-values parameter: " + previewSizeValueString);
//      cameraResolution = findBestPreviewSizeValue(previewSizeValueString, screenResolution);
//    }
//
//    if (cameraResolution == null) {
//      // Ensure that the camera resolution is a multiple of 8, as the screen may not be.
//      cameraResolution = new Point(
//          (screenResolution.x >> 3) << 3,
//          (screenResolution.y >> 3) << 3);
//    }
//
//    return cameraResolution;
//  }
//
//  private static Point findBestPreviewSizeValue(CharSequence previewSizeValueString, Point screenResolution) {
//    int bestX = 0;
//    int bestY = 0;
//    int diff = Integer.MAX_VALUE;
//    for (String previewSize : COMMA_PATTERN.split(previewSizeValueString)) {
//
//      previewSize = previewSize.trim();
//      int dimPosition = previewSize.indexOf('x');
//      if (dimPosition < 0) {
//        Log.w(TAG, "Bad preview-size: " + previewSize);
//        continue;
//      }
//
//      int newX;
//      int newY;
//      try {
//        newX = Integer.parseInt(previewSize.substring(0, dimPosition));
//        newY = Integer.parseInt(previewSize.substring(dimPosition + 1));
//      } catch (NumberFormatException nfe) {
//        Log.w(TAG, "Bad preview-size: " + previewSize);
//        continue;
//      }
//
//      int newDiff = Math.abs(newX - screenResolution.x) + Math.abs(newY - screenResolution.y);
//      if (newDiff == 0) {
//        bestX = newX;
//        bestY = newY;
//        break;
//      } else if (newDiff < diff) {
//        bestX = newX;
//        bestY = newY;
//        diff = newDiff;
//      }
//
//    }
//
//    if (bestX > 0 && bestY > 0) {
//      return new Point(bestX, bestY);
//    }
//    return null;
//  }
//
//  private static int findBestMotZoomValue(CharSequence stringValues, int tenDesiredZoom) {
//    int tenBestValue = 0;
//    for (String stringValue : COMMA_PATTERN.split(stringValues)) {
//      stringValue = stringValue.trim();
//      double value;
//      try {
//        value = Double.parseDouble(stringValue);
//      } catch (NumberFormatException nfe) {
//        return tenDesiredZoom;
//      }
//      int tenValue = (int) (10.0 * value);
//      if (Math.abs(tenDesiredZoom - value) < Math.abs(tenDesiredZoom - tenBestValue)) {
//        tenBestValue = tenValue;
//      }
//    }
//    return tenBestValue;
//  }
//  
//  public void setLight(Camera camera, boolean bOn) {
//		if (camera != null) {
//			Camera.Parameters parameters = camera.getParameters();
//			if (bOn) {
//				parameters.setFlashMode(Parameters.FLASH_MODE_TORCH);
//			} else {
//				parameters.setFlashMode(Parameters.FLASH_MODE_OFF);
//			}
//			camera.setParameters(parameters);
//
//		}
//	}
//
//  private void setFlash(Camera.Parameters parameters) {
//    // FIXME: This is a hack to turn the flash off on the Samsung Galaxy.
//    // And this is a hack-hack to work around a different value on the Behold II
//    // Restrict Behold II check to Cupcake, per Samsung's advice
//    //if (Build.MODEL.contains("Behold II") &&
//    //    CameraManager.SDK_INT == Build.VERSION_CODES.CUPCAKE) {
//    if (Build.MODEL.contains("Behold II") && CameraManager.SDK_INT == 3) { // 3 = Cupcake
//      parameters.set("flash-value", 1);
//    } else {
//      parameters.set("flash-value", 2);
//    }
//    // This is the standard setting to turn the flash off that all devices should honor.
//    parameters.set("flash-mode", "off");
//  }
//
//  private void setZoom(Camera.Parameters parameters) {
//
//    String zoomSupportedString = parameters.get("zoom-supported");
//    if (zoomSupportedString != null && !Boolean.parseBoolean(zoomSupportedString)) {
//      return;
//    }
//
//    int tenDesiredZoom = TEN_DESIRED_ZOOM;
//
//    String maxZoomString = parameters.get("max-zoom");
//    if (maxZoomString != null) {
//      try {
//        int tenMaxZoom = (int) (10.0 * Double.parseDouble(maxZoomString));
//        if (tenDesiredZoom > tenMaxZoom) {
//          tenDesiredZoom = tenMaxZoom;
//        }
//      } catch (NumberFormatException nfe) {
//        Log.w(TAG, "Bad max-zoom: " + maxZoomString);
//      }
//    }
//
//    String takingPictureZoomMaxString = parameters.get("taking-picture-zoom-max");
//    if (takingPictureZoomMaxString != null) {
//      try {
//        int tenMaxZoom = Integer.parseInt(takingPictureZoomMaxString);
//        if (tenDesiredZoom > tenMaxZoom) {
//          tenDesiredZoom = tenMaxZoom;
//        }
//      } catch (NumberFormatException nfe) {
//        Log.w(TAG, "Bad taking-picture-zoom-max: " + takingPictureZoomMaxString);
//      }
//    }
//
//    String motZoomValuesString = parameters.get("mot-zoom-values");
//    if (motZoomValuesString != null) {
//      tenDesiredZoom = findBestMotZoomValue(motZoomValuesString, tenDesiredZoom);
//    }
//
//    String motZoomStepString = parameters.get("mot-zoom-step");
//    if (motZoomStepString != null) {
//      try {
//        double motZoomStep = Double.parseDouble(motZoomStepString.trim());
//        int tenZoomStep = (int) (10.0 * motZoomStep);
//        if (tenZoomStep > 1) {
//          tenDesiredZoom -= tenDesiredZoom % tenZoomStep;
//        }
//      } catch (NumberFormatException nfe) {
//        // continue
//      }
//    }
//
//    // Set zoom. This helps encourage the user to pull back.
//    // Some devices like the Behold have a zoom parameter
//    if (maxZoomString != null || motZoomValuesString != null) {
//      parameters.set("zoom", String.valueOf(tenDesiredZoom / 10.0));
//    }
//
//    // Most devices, like the Hero, appear to expose this zoom parameter.
//    // It takes on values like "27" which appears to mean 2.7x zoom
//    if (takingPictureZoomMaxString != null) {
//      parameters.set("taking-picture-zoom", tenDesiredZoom);
//    }
//  }
//
//  /*
//  private void setSharpness(Camera.Parameters parameters) {
//
//    int desiredSharpness = DESIRED_SHARPNESS;
//
//    String maxSharpnessString = parameters.get("sharpness-max");
//    if (maxSharpnessString != null) {
//      try {
//        int maxSharpness = Integer.parseInt(maxSharpnessString);
//        if (desiredSharpness > maxSharpness) {
//          desiredSharpness = maxSharpness;
//        }
//      } catch (NumberFormatException nfe) {
//        Log.w(TAG, "Bad sharpness-max: " + maxSharpnessString);
//      }
//    }
//
//    parameters.set("sharpness", desiredSharpness);
//  }
//   */
//}

public final class CameraConfigurationManager {
	private static final String TAG = "CameraConfiguration";
	private static final int MIN_PREVIEW_PIXELS = 320 * 240; // small screen
	private static final int MAX_PIC_PIXELS = 800 * 480; // large/HD screen
	private static final int MAX_PREVIEW_PIXELS =  960*720; // large/HD screen
	private final Context context;
	private Point screenResolution;
	private Point cameraResolution;
	private Point picResolution;

	CameraConfigurationManager(Context context) {
		this.context = context;
	}

	/**
	 * Reads, one time, values from the camera that are needed by the app.
	 */
	void initFromCameraParameters(Camera camera) {
		SharedPreferences spCamera = context.getSharedPreferences("camera", Context.MODE_PRIVATE);
		int screenX = spCamera.getInt("screenX", 0);
		int screenY = spCamera.getInt("screenY", 0);
		int cameraX = spCamera.getInt("cameraX", 0);
		int cameraY = spCamera.getInt("cameraY", 0);
		if (screenX == 0 || screenY == 0 || cameraX == 0 || cameraY == 0) {
			Camera.Parameters parameters = camera.getParameters();
			WindowManager manager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
			Display display = manager.getDefaultDisplay();
			int width = display.getWidth();
			int height = display.getHeight();
			if (width < height) {
				int temp = width;
				width = height;
				height = temp;
			}
			screenResolution = new Point(width, height);
			cameraResolution = findBestPreviewSizeValue(parameters, screenResolution, false);

			picResolution = findBestTakePicSizeValue(parameters, screenResolution, false);
			SharedPreferences.Editor editor = context.getSharedPreferences("camera", Context.MODE_PRIVATE).edit();
			editor.putInt("screenX", screenResolution.x);
			editor.putInt("screenY", screenResolution.y);
			editor.putInt("cameraX", cameraResolution.x);
			editor.putInt("cameraY", cameraResolution.y);
			editor.putInt("picX", picResolution.x);
			editor.putInt("picY", picResolution.y);
		//	 KakaLog.logDebug("the resolutin x is:\t"+cameraResolution.x+"the y is :\t"+cameraResolution.y);
		//	 KakaLog.logDebug("the pic resolutin x is:\t"+picResolution.x+"the y is :\t"+picResolution.y);

			editor.putInt("format", getBestSupportImageFormat(parameters));
			editor.commit();
		} else {
			screenResolution = new Point(screenX, screenY);
			cameraResolution = new Point(cameraX, cameraY);
		}
	}

	private int getBestSupportImageFormat(Camera.Parameters parameters) {
		List<Integer> list = parameters.getSupportedPictureFormats();
		if (list.contains(ImageFormat.JPEG)) {
			return ImageFormat.JPEG;
		} else if (list.contains(ImageFormat.RGB_565)) {
			return ImageFormat.RGB_565;
		} else if (list.contains(ImageFormat.NV21)) {
			return ImageFormat.NV21;
		}
		return 0;
	}

	void setDesiredCameraParameters(Camera camera) {
		Camera.Parameters parameters = camera.getParameters();
		if (parameters == null) {
			Log.w(TAG, "Device error: no camera parameters are available. Proceeding without configuration.");
			return;
		}
		String focusMode = findSettableValue(parameters.getSupportedFocusModes(), Camera.Parameters.FOCUS_MODE_AUTO);
		if (focusMode != null) {
			parameters.setFocusMode(focusMode);
		}
		parameters.setPreviewSize(cameraResolution.x, cameraResolution.y);
		// parameters.setRotation(90);

		camera.setParameters(parameters);
	}

	Point getCameraResolution() {
		return cameraResolution;
	}

	Point getScreenResolution() {
		return screenResolution;
	}

	public void setLight(Camera mCamera, boolean isTurnOn) {
//		if (camera != null) {
//			Camera.Parameters parameters = camera.getParameters();
//			if (bOn) {
//				parameters.setFlashMode(Parameters.FLASH_MODE_TORCH);
//			} else {
//				parameters.setFlashMode(Parameters.FLASH_MODE_OFF);
//			}
//			camera.setParameters(parameters);
//
//		}
		if (mCamera!=null) {
			Camera.Parameters params = mCamera.getParameters();
			if (params!=null && !isTurnOn) {
				params.setFlashMode(Camera.Parameters.FLASH_MODE_OFF);    
				mCamera.setParameters( params );
			}else if (params!=null && isTurnOn) {//打开闪光灯
				params.setFlashMode(Camera.Parameters.FLASH_MODE_TORCH);    
				mCamera.setParameters( params );
			}
		}
	}

	private static void initializeTorch(Camera.Parameters parameters) {
		doSetTorch(parameters, false);
	}

	private static void doSetTorch(Camera.Parameters parameters, boolean newSetting) {
		String flashMode;
		if (newSetting) {
			flashMode = findSettableValue(parameters.getSupportedFlashModes(), Camera.Parameters.FLASH_MODE_TORCH, Camera.Parameters.FLASH_MODE_ON);
		} else {
			flashMode = findSettableValue(parameters.getSupportedFlashModes(), Camera.Parameters.FLASH_MODE_OFF);
		}
		if (flashMode != null) {
			parameters.setFlashMode(flashMode);
		}
	}

	private static Point findBestPreviewSizeValue(Camera.Parameters parameters, Point screenResolution, boolean portrait) {
		Camera.Size result = null;
		float dr = Float.MAX_VALUE;
		float ratio = (float) screenResolution.x / (float) screenResolution.y;

		for (Camera.Size supportedPreviewSize : parameters.getSupportedPreviewSizes()) {
			int pixels = supportedPreviewSize.height * supportedPreviewSize.width;
			if (pixels < MIN_PREVIEW_PIXELS || pixels > MAX_PREVIEW_PIXELS) {
				continue;
			}
			int supportedWidth = portrait ? supportedPreviewSize.height : supportedPreviewSize.width;
			int supportedHeight = portrait ? supportedPreviewSize.width : supportedPreviewSize.height;

			float r = (float) supportedWidth / (float) supportedHeight;
			if (Math.abs(r - ratio) < dr ) {
				dr = Math.abs(r - ratio);
				result = supportedPreviewSize;
			}
		}
		Point bestSize = new Point(result.width,result.height);
		return bestSize;

	}

	private static Point findBestTakePicSizeValue(Camera.Parameters parameters, Point screenResolution, boolean portrait) {
		Camera.Size result = null;
		float dr = Float.MAX_VALUE;
		float ratio = (float) screenResolution.x / (float) screenResolution.y;

		for (Camera.Size supportedPreviewSize : parameters.getSupportedPictureSizes()) {
			int pixels = supportedPreviewSize.height * supportedPreviewSize.width;
			if (pixels < MIN_PREVIEW_PIXELS || pixels > MAX_PIC_PIXELS) {
				continue;
			}
			int supportedWidth = portrait ? supportedPreviewSize.height : supportedPreviewSize.width;
			int supportedHeight = portrait ? supportedPreviewSize.width : supportedPreviewSize.height;

			float r = (float) supportedWidth / (float) supportedHeight;
			if (Math.abs(r - ratio) < dr ) {
				dr = Math.abs(r - ratio);
				result = supportedPreviewSize;
			}
		}
		Point bestSize = new Point(result.width,result.height);
		return bestSize;
	}

	private static String findSettableValue(Collection<String> supportedValues, String... desiredValues) {
		String result = null;
		if (supportedValues != null) {
			for (String desiredValue : desiredValues) {
				if (supportedValues.contains(desiredValue)) {
					result = desiredValue;
					break;
				}
			}
		}
		Log.i(TAG, "Settable value: " + result);
		return result;
	}

}