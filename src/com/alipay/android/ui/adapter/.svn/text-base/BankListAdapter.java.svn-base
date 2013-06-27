package com.alipay.android.ui.adapter;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.BitmapFactory.Options;
import android.view.View;
import android.view.ViewGroup;

import com.alipay.android.appHall.Helper;
/**
 * 银行列表Adapter
 * @author caidie.wang
 *
 */
public abstract class BankListAdapter extends CustomImageAndTextAdapter{
	
	public static final String BANK_ICON_PARTH = "bank_mark/icon";
	public static final String BANK_NAME_PARTH = "bank_mark/name";
	
	public HashMap <String, Integer>mBankMap = new HashMap <String, Integer>();
	
	public BankListAdapter(Activity context, Object arrayList) {
		this(context, arrayList,null);
	}
	
	public BankListAdapter(Activity context, Object arrayList,String iconParth) {
		super(context, arrayList);
	}
	
	public Bitmap getImage(String imagePath,String imageName){
		Bitmap image = null;
		final Options options = new Options();
        options.inDensity =(int) Helper.getDensityDpi(mContext);
        options.inScaled = true;
        try {
			image = BitmapFactory.decodeStream(mContext.getAssets().open(
					imagePath + File.separator + imageName+".png"), null, options);
        } catch (IOException e) {
			e.printStackTrace();
		}
        try {
        	if(image == null){
     			image = BitmapFactory.decodeStream(mContext.getAssets().open(imagePath + File.separator + "default.png"), null, options);
     		}
        } catch (IOException e) {
			e.printStackTrace();
		}
		return image;
	}
	
	@Override
	public abstract View getView(int position, View convertView, ViewGroup parent);

}
