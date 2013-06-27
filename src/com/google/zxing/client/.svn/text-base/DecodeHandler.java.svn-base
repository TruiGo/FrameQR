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

package com.google.zxing.client;

import java.io.ByteArrayOutputStream;
import java.util.Hashtable;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.ImageFormat;
import android.graphics.Rect;
import android.graphics.YuvImage;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;

import com.eg.android.AlipayGphone.R;
import com.etao.kaka.decode.DecodeResult;
import com.etao.kaka.decode.KakaDecode;
import com.google.zxing.BinaryBitmap;
import com.google.zxing.DecodeHintType;
import com.google.zxing.MultiFormatReader;
import com.google.zxing.ReaderException;
import com.google.zxing.Result;
import com.google.zxing.client.camera.CameraManager;
import com.google.zxing.common.HybridBinarizer;

final class DecodeHandler extends Handler {

  private static final String TAG = DecodeHandler.class.getSimpleName();

  private final BarcodeCaptureActivity activity;

  DecodeHandler(BarcodeCaptureActivity activity) {
    this.activity = activity;
  }

  @Override
  public void handleMessage(Message message) {
    switch (message.what) {
      case R.id.decode:
        decode((byte[]) message.obj, message.arg1, message.arg2);
        break;
      case R.id.quit:
        Looper.myLooper().quit();
        break;
    }
  }

  /**
   * Decode the data within the viewfinder rectangle, and time how long it took. For efficiency,
   * reuse the same reader objects from one decode to the next.
   *
   * @param data   The YUV preview frame.
   * @param width  The width of the preview frame.
   * @param height The height of the preview frame.
   */
  private void decode(byte[] data, int width, int height) {
    long start = System.currentTimeMillis();
//    Result rawResult = null;
//    PlanarYUVLuminanceSource source = CameraManager.get().buildLuminanceSource(data, width, height);
//    BinaryBitmap bitmap = new BinaryBitmap(new HybridBinarizer(source));
//    try {
//      rawResult = multiFormatReader.decodeWithState(bitmap);
//    } catch (ReaderException re) {
//      // continue
//    } finally {
//      multiFormatReader.reset();
//    }
//
//    if (rawResult != null) {
//      long end = System.currentTimeMillis();
//      Message message = Message.obtain(activity.getHandler(), R.id.decode_succeeded, rawResult);
//      Bundle bundle = new Bundle();
//      bundle.putParcelable(DecodeThread.BARCODE_BITMAP, source.renderCroppedGreyscaleBitmap());
//      message.setData(bundle);
//      message.sendToTarget();
//    } else {
//      Message message = Message.obtain(activity.getHandler(), R.id.decode_failed);
//      message.sendToTarget();
//    }
    
//    Bitmap b = null;
	DecodeResult result = null;
//	b = getFramingRGBBitmap(data, width, height, null);
	YuvImage yuv_image = new YuvImage(data, ImageFormat.NV21, width,
			height, null);

	result = KakaDecode.yuvcodeDecode(yuv_image);
//	b.recycle();

	if (result != null) {
		Message message = Message.obtain(activity.getHandler(),
				R.id.decode_succeeded);
		message.arg1 = result.type;
		message.arg2 = result.subType;
		message.obj = result;
		message.sendToTarget();
	} else {
		Message message = Message.obtain(activity.getHandler(),
				R.id.decode_failed);
		message.sendToTarget();
	}
  }
  
  private Bitmap getFramingRGBBitmap(byte[] data, int width,
			int height, Rect framingRect) {
		// Get the YuV image
		YuvImage yuv_image = new YuvImage(data, ImageFormat.NV21, width,
				height, null);
		// Convert YuV to Jpeg
		ByteArrayOutputStream output_stream = new ByteArrayOutputStream();
		// height 540 width 960
		int left = 0;
		int top = 0;
		int right = 0;
		int bottom = 0;
		if (height > width) {
			// just for yunos
			width = width >> 4 << 4;
			right = width;
			top = (height - width) / 2;
			bottom = top + width;
		} else {
			height = height >> 4 << 4;
			left = (width - height) / 2;
			top = 0;
			right = left + height;
			bottom = height;
		}
		yuv_image.compressToJpeg(new Rect(left, top, right, bottom), 50,
				output_stream);
		// yuv_image.compressToJpeg(framingRect, 50, output_stream);
		// Convert from Jpeg to Bitmap
		Bitmap b = BitmapFactory.decodeByteArray(output_stream.toByteArray(),
				0, output_stream.size());
		return b;
	}

}
