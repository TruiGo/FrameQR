package com.alipay.android.comon.component;

import java.io.File;
import java.io.IOException;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.BitmapFactory.Options;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.widget.TextView;

import com.alipay.android.appHall.Helper;
import com.eg.android.AlipayGphone.R;

public class NumberView extends TextView{
	
	String text = null;
	int type = 0;
	public static final int TYPE_GOLD = 0;//金色
	public static final int TYPE_SILVER = 1;//银色
	
	public static final String GOLD_NUMBER_PARTH = "number/gold";
	public static final String SILVER_NUMBER_PARTH = "number/silver";
	
	int imageHeight;
	int imageWidth;
	private float widthScale = 1;
	private Paint p;
	private Bitmap currentBitmap;
	private Bitmap currentStartBitmap;
	BitmapFactory.Options options;
	
	public NumberView(Context context) {
		super(context);
		init();
	}
	
	public NumberView(Context context,AttributeSet attrs) {
		super(context, attrs);
		TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.NumberView);
		text = a.getString(R.styleable.NumberView_text);
		type = a.getInt(R.styleable.NumberView_type, TYPE_GOLD);
		//获取设置属性值
		a.recycle();
		p = new Paint();
		init();
	}
	


	private void init() {
		options = new BitmapFactory.Options();
	}

	public void setText(String text) {
		this.text = text;
	}
	public void setType(int type) {
		this.type = type;
	}
	@Override
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);
		switch (type) {
		case TYPE_GOLD:
			currentStartBitmap = getImage(GOLD_NUMBER_PARTH, "start");
			drawBitmap(GOLD_NUMBER_PARTH,canvas);
			break;
		case TYPE_SILVER:
			currentStartBitmap = getImage(SILVER_NUMBER_PARTH, "start");
			drawBitmap(SILVER_NUMBER_PARTH,canvas);
			break;
		default:
			break;
		}
	}
	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		super.onMeasure(widthMeasureSpec, heightMeasureSpec);
		options.inJustDecodeBounds = true;
		try {
			BitmapFactory.decodeStream(getContext().getAssets().open(
					GOLD_NUMBER_PARTH + File.separator + "start"+".png"), null, options);
		} catch (IOException e) {
			e.printStackTrace();
		}
		getDisplayScale(getContext());
		
		imageHeight = (int)(options.outHeight * widthScale);
		imageWidth = (int)(options.outWidth * widthScale + 2);
		int textLength = text.length();
		int width = imageWidth*textLength;
		int height = imageHeight;
		setMeasuredDimension(width, height);
	}
	private void getDisplayScale(Context context) {
		DisplayMetrics displayMetrics = Helper.getDisplayMetrics(context);
		int windowWidth = displayMetrics.widthPixels;
		if(windowWidth < 480){
			widthScale = windowWidth/480.0f;
		}
	}

	private void drawBitmap(String imagePath, Canvas canvas) {
		for (int i = 0; i < text.length(); i++) {
			char currentChar = text.charAt(i);
			if (currentChar == '*') {
				canvas.drawBitmap(currentStartBitmap,
						new Rect(0, 0, currentStartBitmap.getWidth(),
								currentStartBitmap.getHeight()), new Rect(0, 0,
								imageWidth, imageHeight), p);
			} else if (currentChar == ' ') {
			} else {
				currentBitmap = getImage(imagePath, String.valueOf(currentChar));
				if (currentBitmap != null) {
					canvas.drawBitmap(currentBitmap,
							new Rect(0, 0, currentBitmap.getWidth(),
									currentBitmap.getHeight()), new Rect(0, 0,
									imageWidth, imageHeight), p);
				}
			}
			moveCanvas(canvas);
		}
	}
	
	private void moveCanvas(Canvas canvas) {
		canvas.save();
		canvas.translate(imageWidth, 0);
	}

	public Bitmap getImage(String imagePath,String imageName){
		Bitmap image = null;
		final Options options = new Options();
        options.inDensity =(int) Helper.getDensityDpi(getContext());
        options.inScaled = true;
        try {
			image = BitmapFactory.decodeStream(getContext().getAssets().open(
					imagePath + File.separator + imageName+".png"), null, options);
        } catch (IOException e) {
			e.printStackTrace();
		}
		return image;
	}
	
}
