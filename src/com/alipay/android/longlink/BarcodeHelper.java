package com.alipay.android.longlink;

import android.content.Context;
import android.content.Intent;
import android.graphics.Rect;
import android.os.Handler;
import android.util.DisplayMetrics;

import com.alipay.android.client.constant.Constant;
import com.alipay.android.client.util.AlipayDataStore;
import com.alipay.android.client.util.BaseHelper;
import com.alipay.android.util.LogUtil;
import com.google.zxing.client.Contents;
import com.google.zxing.client.Intents;
import com.google.zxing.client.encode.QRCodeEncoder;

public class BarcodeHelper {
	
	public static ScreenScaleParam detectScreenLevel(Context activity){
		ScreenScaleParam scaleParam = new ScreenScaleParam();
		DisplayMetrics dmScreen = BaseHelper.getDisplayMetrics(activity);
		scaleParam.setScaleHeight(dmScreen.heightPixels);
		scaleParam.setScaleWeight(dmScreen.widthPixels);
		
		switch (dmScreen.widthPixels) {
			case 240:
				scaleParam.setBarcodeSizeScale(0.8f);
				scaleParam.setScaleHeight(320);
				break;
			case 320:
				scaleParam.setBarcodeSizeScale(0.65f);
				scaleParam.setScaleHeight(480);
				break;
			case 480:
				scaleParam.setBarcodeSizeScale(0.65f);
				scaleParam.setScaleHeight(800);
				break;
			case 540:
				scaleParam.setBarcodeSizeScale(0.64f);
				scaleParam.setScaleHeight(960);
				break;
			default:
				scaleParam.setBarcodeSizeScale(0.65f);
				break;
		}
		
		LogUtil.logOnlyDebuggable("BarcodeHelper", "detectScreenLevel widthPixels:"+ scaleParam.getScaleWeight() +", heightPixels:"+scaleParam.getScaleHeight()
				+ ", scale="+scaleParam.getCurScale());
		return scaleParam;
	}
	
	public static Rect getBarcodeSize(Context context) {
		int desiredWidth = -1;
		int desiredHeight = -1;
		int smallerDimension = -1;
		ScreenScaleParam scaleParam = detectScreenLevel(context);
		//等比按尺寸放大条码显示 
		desiredHeight = (int) (scaleParam.getScaleHeight() * scaleParam.getBarcodeSizeScale());
		desiredWidth = (int) (scaleParam.getScaleWeight() * scaleParam.getBarcodeSizeScale());
		smallerDimension = desiredWidth < desiredHeight ? desiredWidth : desiredHeight;
		
		return new Rect(0, 0, smallerDimension, smallerDimension);
	}
	
	public static void getBarcodeImg(boolean oneDBarcode,String barcodeStr,Handler handler,Context context){
		int desiredWidth = -1;
		int desiredHeight = -1;
		int smallerDimension = -1;
		ScreenScaleParam scaleParam = detectScreenLevel(context);

		Intent intent = new Intent(Intents.Encode.ACTION);
		if (oneDBarcode) {
			intent.putExtra(Intents.Encode.FORMAT, "CODE_128");
		} else {
			intent.putExtra(Intents.Encode.FORMAT, "QR_CODE");
			
//			AlipayDataStore mAlipayDataStore = new AlipayDataStore(context);
//			String prefix = mAlipayDataStore.getString(AlipayDataStore.QRCODE_PREFIX);
//			if (prefix != null && !prefix.equals("")) {
//				Constant.STR_QRCODE_PREFIX = prefix;
//			}
//			//登录时从服务端获取到的前缀
//			barcodeStr = Constant.STR_QRCODE_PREFIX + barcodeStr;
		}
		
		//等比按尺寸放大条码显示 
		desiredHeight = (int) (scaleParam.getScaleHeight() * scaleParam.getBarcodeSizeScale());
		desiredWidth = (int) (scaleParam.getScaleWeight() * scaleParam.getBarcodeSizeScale());
		smallerDimension = desiredWidth < desiredHeight ? desiredWidth : desiredHeight;
		intent.putExtra(Intents.Encode.TYPE, Contents.Type.TEXT);
		intent.putExtra(Intents.Encode.DATA, barcodeStr);
		
		LogUtil.logOnlyDebuggable("BarcodeHelper","contents:" + barcodeStr);
		try {
			QRCodeEncoder qrCodeEncoder = new QRCodeEncoder(context, intent);
			qrCodeEncoder.requestBarcode(handler, smallerDimension, smallerDimension);
        } catch (IllegalArgumentException e) {
        	e.printStackTrace();
        	LogUtil.logOnlyDebuggable("BarcodeHelper","---------------decode fail");
        }
	}
}
