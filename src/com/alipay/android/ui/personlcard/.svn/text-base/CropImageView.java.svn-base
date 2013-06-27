/**
 * 
 */
package com.alipay.android.ui.personlcard;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.reflect.Field;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.BitmapFactory.Options;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.graphics.PointF;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Region;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.FloatMath;
import android.view.Display;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.ImageButton;

import com.eg.android.AlipayGphone.R;

public class CropImageView extends FrameLayout implements View.OnTouchListener,
		View.OnClickListener {
	private Bitmap mBitmap;
	private int mWidth;
	private int mHeight;

	private int mClipWidth;
	private int mClipHeight;
	private RectF mMask;

	private DisplayMetrics mDisplayMetrics;

	private GetAvatarActivity mAvatarActivity;

	private String tmp_avatarFileStr = "";
	public String tmp_avatarDirStr = "";

	private void setAvatarTmpPath() {
		tmp_avatarDirStr = mAvatarActivity.getFilesDir() + File.separator
				+ "userdata" + File.separator + "useravatar" + File.separator
				+ mAvatarActivity.getUserId() + File.separator;
		tmp_avatarFileStr = mAvatarActivity.getFilesDir() + File.separator
				+ "userdata" + File.separator + "useravatar" + File.separator
				+ mAvatarActivity.getUserId() + File.separator
				+ "__tmp_avatar.jpg";
	}

	public CropImageView(Context context) {
		super(context);
		getDisplay();

		init();
	}

	public CropImageView(Context context, AttributeSet attrs) {
		super(context, attrs);
		getDisplay();

		mClipWidth = mDisplayMetrics.widthPixels * 2 / 3;
		mClipHeight = mDisplayMetrics.widthPixels * 2 / 3;

		setMinimumWidth(mClipWidth);
		setMinimumHeight(mClipHeight);

		init();
	}

	void setActivity(GetAvatarActivity mAvatarActivity) {
		this.mAvatarActivity = mAvatarActivity;
		setAvatarTmpPath();
	}

	private void getDisplay() {
		mDisplayMetrics = new DisplayMetrics();
		Display display = ((WindowManager) getContext().getSystemService(
				Context.WINDOW_SERVICE)).getDefaultDisplay();
		display.getMetrics(mDisplayMetrics);
	}

	@SuppressWarnings("deprecation")
	private void init() {
		setOnTouchListener(this);
		setLongClickable(true);

		setFocusable(true);
		setFocusableInTouchMode(true);

		getStatusBarHeight();

		View cropAction = LayoutInflater.from(getContext()).inflate(
				R.layout.crop_image_action, null);
		FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(
				LayoutParams.FILL_PARENT, LayoutParams.WRAP_CONTENT);
		params.gravity = Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL;
		params.bottomMargin = 5;
		addView(cropAction, params);

		ImageButton button = (ImageButton) cropAction
				.findViewById(R.id.crop_rotate);
		button.setOnClickListener(this);
		button = (ImageButton) cropAction.findViewById(R.id.crop_zoomin);
		button.setOnClickListener(this);
		button.setHapticFeedbackEnabled(false);
		button = (ImageButton) cropAction.findViewById(R.id.crop_zoomout);
		button.setOnClickListener(this);
		button.setHapticFeedbackEnabled(false);
	}

	private boolean mChanged;

	public void setBitmap(Bitmap bitmap) {
		if (bitmap == null)
			return;
		mChanged = true;
		mBitmap = bitmap;
		mWidth = bitmap.getWidth();
		mHeight = bitmap.getHeight();
		requestLayout();
	}

	public void setBitmap(String path) {

		Options options = new Options();
		options.inJustDecodeBounds = true;
		BitmapFactory.decodeFile(path, options);
		int dw = options.outWidth / mDisplayMetrics.widthPixels;
		int dh = options.outHeight / mDisplayMetrics.heightPixels;
		int scale = Math.max(dw, dh);

		options = new Options();
		options.inDensity = mDisplayMetrics.densityDpi;
		options.inScaled = true;
		options.inPurgeable = true;
		options.inSampleSize = scale;
		Bitmap mBitmap = null;
		try {
			mBitmap = BitmapFactory.decodeFile(path, options);
		}catch(Exception e) {
			e.printStackTrace();
		}
		setBitmap(mBitmap);
	}

	public void copyFile(File sourceFile, File targetFile) throws IOException {
		BufferedInputStream inBuff = null;
		BufferedOutputStream outBuff = null;
		try {
			inBuff = new BufferedInputStream(new FileInputStream(sourceFile));

			outBuff = new BufferedOutputStream(new FileOutputStream(targetFile));

			byte[] b = new byte[1024 * 5];
			int len;
			while ((len = inBuff.read(b)) != -1) {
				outBuff.write(b, 0, len);
			}
			outBuff.flush();
		} finally {
			if (inBuff != null)
				inBuff.close();
			if (outBuff != null)
				outBuff.close();
		}
	}

	@Override
	protected void dispatchDraw(Canvas canvas) {
		if (mBitmap != null) {
			canvas.drawBitmap(mBitmap, mMatrix, null);
		}

		Paint paint = new Paint();
		paint.setColor(Color.YELLOW);
		paint.setStyle(Style.STROKE);
		paint.setStrokeWidth(4);

		canvas.clipRect(mMask, Region.Op.XOR);
		canvas.drawColor(0x66000000);
		canvas.drawRect(mMask, paint);
		super.dispatchDraw(canvas);

	}

	@Override
	protected void onLayout(boolean changed, int left, int top, int right,
			int bottom) {
		super.onLayout(changed, left, top, right, bottom);
		if (mChanged || changed) {
			Rect rect = new Rect(0, 0, right - left, bottom - top);
			mMask = new RectF(rect.centerX() - mClipWidth * 0.5f,
					rect.centerY() - mClipHeight * 0.5f, rect.centerX()
							+ mClipWidth * 0.5f, rect.centerY() + mClipHeight
							* 0.5f);
			if (isFrist) {
				initMatrix();
				isFrist = false;
			}
			
			if (mChanged) {
				mChanged = false;
				mMatrix = new Matrix();
				initMatrix();
			}
		}
	}

	private boolean isFrist = true;

	private void initMatrix() {
		float scale = 1.0f;
		if (mWidth > mHeight) {
			scale = mDisplayMetrics.widthPixels * 1.0f / mWidth;
		} else {
			scale = mDisplayMetrics.heightPixels * 1.0f / mHeight;
		}
		mMatrix.postScale(scale, scale);

		float dx = mMask.centerX() - mWidth * scale * 0.5f;
		float dy = mMask.centerY() - mHeight * scale * 0.5f;
		mMatrix.postTranslate(dx, dy);
	}

	public void resizeBitmap(float scale) {
		RectF rect = getMapedRect();

		mMatrix.postScale(scale, scale, rect.centerX(), rect.centerY());
		postInvalidate();
	}

	public void rotateBitmap(float degree) {
		RectF rect = getMapedRect();
		n = --angleInt % 4;
		n = Math.abs(n);
		mMatrix.postRotate(90, rect.centerX(), rect.centerY());
		savedMatrix.postRotate(90);
		postInvalidate();

	}

	private RectF getMapedRect() {
		RectF rect = new RectF(0, 0, mWidth, mHeight);
		mMatrix.mapRect(rect);
		return rect;
	}

	public Bitmap getCropBitmap() {
		if (mBitmap == null)
			return null;
		Bitmap bitmap = Bitmap.createBitmap(mClipWidth, mClipHeight,
				Bitmap.Config.RGB_565);
		Canvas canvas = new Canvas(bitmap);
		canvas.translate(-mMask.left, -mMask.top);
		canvas.drawBitmap(mBitmap, mMatrix, null);
		return bitmap;
	}

	public void recycle() {
		if (mBitmap != null)
			mBitmap.recycle();
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.crop_rotate:
			rotateBitmap(90);
			break;
		case R.id.crop_zoomin:
			resizeBitmap(0.9f);
			break;
		case R.id.crop_zoomout:
			resizeBitmap(1.1f);
			break;
		default:
			break;
		}
	}

	public String writeBitmap(Bitmap bitmap) throws Exception {
		File file = new File(tmp_avatarDirStr);
		if (!file.exists()) {
			file.mkdirs();
		}
		file = new File(tmp_avatarFileStr);

		BufferedOutputStream bos = new BufferedOutputStream(
				new FileOutputStream(file));
		bitmap.compress(Bitmap.CompressFormat.JPEG, 50, bos);
		bos.flush();
		bos.close();
		return file.getPath();
	}

	Matrix mMatrix = new Matrix();
	Matrix savedMatrix = new Matrix();

	float minScaleR;// 最小缩放比例
	static final float MAX_SCALE = 10f;// 最大缩放比例

	static final int NONE = 0;// 初始状态
	static final int DRAG = 1;// 拖动
	static final int ZOOM = 2;// 缩放
	int mode = NONE;

	PointF prev = new PointF();
	PointF mid = new PointF();
	float oldDist = 1f;

	private int statusBarHeight = 0;
	private int angleInt = 4;
	private int n = 0;

	@Override
	public boolean onTouch(View v, MotionEvent event) {
		switch (event.getAction() & MotionEvent.ACTION_MASK) {
		// 主点按下
		case MotionEvent.ACTION_DOWN:
			savedMatrix.set(mMatrix);
			prev.set(event.getX(), event.getY());
			if (isOnCP(event.getX(), event.getY())) {
				mode = DRAG;
			} else {
				mode = NONE;
			}
			break;
		case MotionEvent.ACTION_POINTER_DOWN:
			oldDist = spacing(event);
			boolean isonpic = isOnCP(event.getX(), event.getY());
			if (oldDist > 10f && isonpic) {
				savedMatrix.set(mMatrix);
				midPoint(mid, event);
				mode = ZOOM;
			}
			break;
		case MotionEvent.ACTION_UP:
		case MotionEvent.ACTION_POINTER_UP:
			mode = NONE;
			break;
		case MotionEvent.ACTION_MOVE:
			if (mode == DRAG) {
				// ...
				mMatrix.set(savedMatrix);
				mMatrix.postTranslate(event.getX() - prev.x, event.getY()
						- prev.y);
			} else if (mode == ZOOM) {
				float newDist = spacing(event);
				if (newDist > 10f) {
					mMatrix.set(savedMatrix);
					float scale = newDist / oldDist;
					mMatrix.postScale(scale, scale, mid.x, mid.y);
				}
			}
			break;
		}
		CheckView();
		invalidate();
		return true;
	}

	private float spacing(MotionEvent event) {
		float x = event.getX(0) - event.getX(1);
		float y = event.getY(0) - event.getY(1);
		return FloatMath.sqrt(x * x + y * y);
	}

	private void midPoint(PointF point, MotionEvent event) {
		float x = event.getX(0) + event.getX(1);
		float y = event.getY(0) + event.getY(1);
		point.set(x / 2, y / 2);
	}

	private boolean isOnCP(float evx, float evy) {
		float p[] = new float[9];
		mMatrix.getValues(p);
		float scale = Math.max(Math.abs(p[0]), Math.abs(p[1]));
		RectF rectf = null;
		switch (n) {
		case 0:
			rectf = new RectF(p[2], p[5], (p[2] + mWidth * scale),
					(p[5] + mHeight * scale));
			break;
		case 1:
			rectf = new RectF(p[2], p[5] - mWidth * scale, p[2] + mHeight
					* scale, p[5]);
			break;
		case 2:
			rectf = new RectF(p[2] - mWidth * scale, p[5] - mHeight * scale,
					p[2], p[5]);
			break;
		case 3:
			rectf = new RectF(p[2] - mHeight * scale, p[5], p[2], p[5] + mWidth
					* scale);
			break;
		}
		if (rectf != null && rectf.contains(evx, evy)) {
			return true;
		}
		return false;
	}

	private void CheckView() {
		float p[] = new float[9];
		mMatrix.getValues(p);
		float scale = Math.max(Math.abs(p[0]), Math.abs(p[1]));
		if (mode == ZOOM) {
			if (scale < minScaleR) {
				mMatrix.setScale(minScaleR, minScaleR);
				center();
			}
			if (scale > MAX_SCALE) {
				mMatrix.set(savedMatrix);
			}
		}
	}

	private void center() {
		center(true, true);
	}

	protected void center(boolean horizontal, boolean vertical) {

		Matrix m = new Matrix();
		m.set(mMatrix);
		RectF rect = new RectF(0, 0, mBitmap.getWidth(), mBitmap.getHeight());
		m.mapRect(rect);

		float height = rect.height();
		float width = rect.width();

		float deltaX = 0, deltaY = 0;

		if (vertical) {
			// 图片小于屏幕大小，则居中显示。大于屏幕，上方留空则往上移，下方留空则往下移
			int screenHeight = mDisplayMetrics.heightPixels;
			if (height < screenHeight) {
				deltaY = (screenHeight - height - statusBarHeight) / 2
						- rect.top;
			} else if (rect.top > 0) {
				deltaY = -rect.top;
			} else if (rect.bottom < screenHeight) {
				deltaY = mHeight - rect.bottom;
			}
		}

		if (horizontal) {
			int screenWidth = mDisplayMetrics.widthPixels;
			if (width < screenWidth) {
				deltaX = (screenWidth - width) / 2 - rect.left;
			} else if (rect.left > 0) {
				deltaX = -rect.left;
			} else if (rect.right > screenWidth) {
				deltaX = (screenWidth - width) / 2 - rect.left;
			}
		}
		mMatrix.postTranslate(deltaX, deltaY);
		if (n % 4 != 0) {
			mMatrix.postRotate(-90 * (n % 4), mWidth / 2, mHeight / 2);
		}
	}

	private void getStatusBarHeight() {
		try {
			Class<?> c = Class.forName("com.android.internal.R$dimen");
			Object obj = c.newInstance();
			Field field = c.getField("status_bar_height");
			int x = Integer.parseInt(field.get(obj).toString());
			statusBarHeight = getResources().getDimensionPixelSize(x);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
