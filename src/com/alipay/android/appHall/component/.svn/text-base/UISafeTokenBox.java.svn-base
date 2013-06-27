package com.alipay.android.appHall.component;

import java.security.InvalidKeyException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.HashMap;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.drawable.AnimationDrawable;
import android.graphics.drawable.Drawable;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.alipay.android.appHall.uiengine.Computable;
import com.alipay.android.appHall.uiengine.UIInterface;
import com.alipay.android.core.expapp.Page;
import com.eg.android.AlipayGphone.R;

public class UISafeTokenBox extends LinearLayout implements UIInterface,
		Computable {

	private int[] numArray = new int[] { R.drawable.zero, R.drawable.one,
			R.drawable.two, R.drawable.three, R.drawable.four, R.drawable.five,
			R.drawable.six, R.drawable.seven, R.drawable.eight, R.drawable.nine };
	private int[] numFuzzyArray = new int[] { R.drawable.zero_fuzzy,
			R.drawable.one_fuzzy, R.drawable.two_fuzzy, R.drawable.three_fuzzy,
			R.drawable.four_fuzzy, R.drawable.five_fuzzy, R.drawable.six_fuzzy,
			R.drawable.seven_fuzzy, R.drawable.eight_fuzzy,
			R.drawable.nine_fuzzy };

	private ImageView[] images;

	private Page mPage;

	private static final int duration = 100;

	private Resources res;

	public UISafeTokenBox(Page page) {
		super(page.getRawContext());
		mPage = page;
	}

	// private Context context;

	public UISafeTokenBox(Context context) {
		super(context);
		// this.context = context;
	}

	// private static int a = 234567;

	public void init(ViewGroup parent) {

		res = mPage.getRawContext().getResources();
		
		
		LinearLayout imageLayoutParent = (LinearLayout) LayoutInflater.from(
				mPage.getRawContext()).inflate(R.layout.uibaoling, parent,
				false);
		images = new ImageView[6];
		
		LayoutParams params = new LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.WRAP_CONTENT);
			params.gravity = Gravity.CENTER_HORIZONTAL;
			addView(imageLayoutParent,params);
		parent.addView(this);
		

//		LinearLayout layoutTemp = (LinearLayout) LayoutInflater.from(
//				mPage.getRawContext()).inflate(R.layout.uibaoling, parent,
//				false);
//		images = new ImageView[6];
//
//		LinearLayout layout = (LinearLayout) layoutTemp
//				.findViewById(R.id.imageLayout);
//		layoutTemp.removeAllViews();
//		addView(layout);
//		parent.addView(this);
		images[0] = (ImageView) imageLayoutParent.findViewById(R.id.image0);
		images[1] = (ImageView) imageLayoutParent.findViewById(R.id.image1);
		images[2] = (ImageView) imageLayoutParent.findViewById(R.id.image2);
		images[3] = (ImageView) imageLayoutParent.findViewById(R.id.image3);
		images[4] = (ImageView) imageLayoutParent.findViewById(R.id.image4);
		images[5] = (ImageView) imageLayoutParent.findViewById(R.id.image5);
	}

	public void setVisible(boolean isVisible) {
		setVisibility(isVisible ? View.VISIBLE : View.GONE);
	}

	public boolean getVisible() {
		return getVisibility() == View.VISIBLE;
	}

	private String currentValueNum;

	@Override
	public void setValue(Object value) {
		String newValueNum = value.toString();

		String num = value.toString();

		if (currentValueNum == null) {
			for (int i = 0; i < 6; i++) {
				char[] chars = num.toCharArray();
				images[i].setBackgroundDrawable(res
						.getDrawable(numArray[chars[i] - 48]));
			}
			currentValueNum = newValueNum;
			return;
		}

		// 动画效果
		char[] oldChars = currentValueNum.toCharArray();
		char[] newChars = ((String) value).toCharArray();
		for (int j = 0; j < 6; j++) {
			// =============

			int oldNum = oldChars[j] - 48;
			int newNum = newChars[j] - 48;
			changeNum(oldNum, newNum, images[j]);
		}
		//
		currentValueNum = newValueNum;
	}

	private void changeNum(int oldNum, int newNum, ImageView image) {
		AnimationDrawable anim = new AnimationDrawable();

		Drawable d1 = res.getDrawable(numArray[oldNum]);
		Drawable d2 = res.getDrawable(numFuzzyArray[oldNum]);
		Drawable d3 = res.getDrawable(numFuzzyArray[newNum]);
		Drawable d4 = res.getDrawable(numArray[newNum]);
		anim.addFrame(d1, duration);
		anim.addFrame(d2, duration);
		anim.addFrame(d3, duration);
		anim.addFrame(d4, duration);
		anim.setOneShot(true);
		image.setBackgroundDrawable(anim);
		anim.start();

	}

	@Override
	public Object getValue() {
		// TODO Auto-generated method stub
		return currentValueNum;
	}

	@Override
	public void setEnable(boolean enabled) {
		this.setEnabled(enabled);
	}

	@Override
	public boolean getEnable() {
		// TODO Auto-generated method stub
		return isEnabled();
	}

	// ----------------------------设置宽度
	private boolean isChange = true;
	private int width = LayoutParams.FILL_PARENT;

	public void set_Width(int width) {
		isChange = true;
		this.width = width;
		changedWidth();
	}

	private void changedWidth() {
		ViewGroup.LayoutParams params = (ViewGroup.LayoutParams) getLayoutParams();
		params.width = this.width;
		setLayoutParams(params);
	}

	@Override
	protected void onWindowVisibilityChanged(int visibility) {
		super.onWindowVisibilityChanged(visibility);
		if (isChange) {
			changedWidth();
		}
		isChange = false;
		// 最后测试动态更改宽度
		requestLayout();
	}

	@Override
	public String getSelectedIndex() {
		return null;
	}

	private boolean isSave;

	@Override
	public void setIsSave(boolean isSave) {
		this.isSave = isSave;
	}

	@Override
	public boolean getIsSave() {
		return isSave;
	}

	@Override
	public void set_MarginLeft(int marginLeft) {
		// LinearLayout.LayoutParams params = (LinearLayout.LayoutParams)
		// getLayoutParams();
		// params.leftMargin = marginLeft;
	}

	@Override
	public int get_MarginLeft() {
		// LinearLayout.LayoutParams params = (LinearLayout.LayoutParams)
		// getLayoutParams();
		return 0;
	}

	@Override
	public void set_MarginRight(int marginRight) {
		// LinearLayout.LayoutParams params = (LinearLayout.LayoutParams)
		// getLayoutParams();
		// params.rightMargin = marginRight;
	}

	@Override
	public int get_MarginRight() {
		// LinearLayout.LayoutParams params = (LinearLayout.LayoutParams)
		// getLayoutParams();
		return 0;
	}

	@Override
	public void set_MarginTop(int marginTop) {
		LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) getLayoutParams();
		params.topMargin = marginTop;
	}

	@Override
	public int get_MarginTop() {
		LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) getLayoutParams();
		return params.topMargin;
	}

	@Override
	public void set_MarginBottom(int marginBottom) {
		LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) getLayoutParams();
		params.bottomMargin = marginBottom;
	}

	@Override
	public int get_MarginBottom() {
		LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) getLayoutParams();
		return params.bottomMargin;
	}

	@Override
	public int get_Width() {
		return getWidth();
	}

	@Override
	public void set_Id(int id) {
		setId(id);
	}

	@Override
	public int get_Id() {
		return getId();
	}

	@Override
	public void set_Text(String text) {
	}

	@Override
	public String get_Text() {
		return null;
	}

	@Override
	public String get_Tag() {
		return (String) this.getTag();
	}

	@Override
	public void set_Tag(String tag) {
		this.setTag(tag);
	}

	@Override
	public LinearLayout.LayoutParams getAlipayLayoutParams() {
		return (android.widget.LinearLayout.LayoutParams) getLayoutParams();
	}

	// tag for android 1.5
	HashMap<Integer, Object> mTags = new HashMap<Integer, Object>();

	@Override
	public Object get_tag(int type) {
		return mTags.get(type);
	}

	@Override
	public void set_Tag(int type, Object tag) {
		mTags.put(type, tag);
	}

	// HOTP(SHA1(手机特征信息+BID+sessionKey+时间)
	@Override
	public Object compute(ArrayList<Object> params) {
		String ret = null;
		MessageDigest sha1 = null;

		Object param1 = params.get(0);
		if (param1 == null) {
			param1 = "";
		}

		Object param2 = params.get(1);
		if (param2 == null) {
			param2 = "";
		}

		Object param3 = params.get(2);
		long time = 0;
		byte[] tempRet;
		if (param3 == null) {
			return null;
		} else {
			if (param3.toString().indexOf(".") != -1) {
				int index = param3.toString().indexOf(".");
				param3 = param3.toString().substring(0, index);
			}
			time = Long.parseLong(param3.toString());
		}
		if (param2.toString().length() > 0) {
			String tempstr = param1.toString() + param2.toString();
			// byte[] tempRet = hexStringToByte(param1.toString() +
			// param2.toString());
			try {
				sha1 = MessageDigest.getInstance("SHA1");
				sha1.reset();
				sha1.update(tempstr.getBytes("UTF-8"));
			} catch (Exception e) {
				e.printStackTrace();
			}
			tempRet = sha1.digest();
		} else {
			tempRet = decodeHex(param1.toString().toCharArray());// hexStringToByte(param1.toString());
		}
		try {
			ret = generateOTP(tempRet, time, 6);
		} catch (InvalidKeyException e) {
			e.printStackTrace();
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		}

		setValue(ret);
		return ret;
	}

	@Override
	public void reset() {
	}

	public static byte[] hmac_sha1(byte[] keyBytes, byte[] text)
			throws NoSuchAlgorithmException, InvalidKeyException {
		Mac hmacSha1;
		try {
			hmacSha1 = Mac.getInstance("HmacSHA1");
		} catch (NoSuchAlgorithmException nsae) {
			hmacSha1 = Mac.getInstance("HMAC-SHA-1");
		}
		SecretKeySpec macKey = new SecretKeySpec(keyBytes, "RAW");
		hmacSha1.init(macKey);
		return hmacSha1.doFinal(text);
	}

	private static final int[] DIGITS_POWER
	// 0 1 2 3 4 5 6 7 8
	= { 1, 10, 100, 1000, 10000, 100000, 1000000, 10000000, 100000000 };

	static public String generateOTP(byte[] secret, long movingFactor,
			int codeDigits) throws NoSuchAlgorithmException,
			InvalidKeyException {
		// put movingFactor value into text byte array
		String result = null;
		int digits = codeDigits;
		byte[] text = new byte[8];
		for (int i = text.length - 1; i >= 0; i--) {
			text[i] = (byte) (movingFactor & 0xff);
			movingFactor >>= 8;
		}
		byte[] hash = hmac_sha1(secret, text);
		int offset = hash[hash.length - 1] & 0xf;
		int binary = ((hash[offset] & 0x7f) << 24)
				| ((hash[offset + 1] & 0xff) << 16)
				| ((hash[offset + 2] & 0xff) << 8) | (hash[offset + 3] & 0xff);

		int otp = binary % DIGITS_POWER[codeDigits];
		result = Integer.toString(otp);
		while (result.length() < digits) {
			result = "0" + result;
		}
		return result;
	}

	/**
	 * Converts an array of characters representing hexidecimal values into an
	 * array of bytes of those same values. The returned array will be half the
	 * length of the passed array, as it takes two characters to represent any
	 * given byte. An exception is thrown if the passed char array has an odd
	 * number of elements.
	 * 
	 * @param data
	 *            An array of characters containing hexidecimal digits
	 * @return A byte array containing binary data decoded from the supplied
	 *         char array.
	 */
	public static byte[] decodeHex(char[] data) {

		int len = data.length;

		if ((len & 0x01) != 0) {
			throw new IllegalArgumentException("Odd number of characters.");
		}

		byte[] out = new byte[len >> 1];

		// two characters form the hex value.
		for (int i = 0, j = 0; j < len; i++) {
			int f = toDigit(data[j], j) << 4;
			j++;
			f = f | toDigit(data[j], j);
			j++;
			out[i] = (byte) (f & 0xFF);
		}

		return out;
	}

	/**
	 * Converts a hexadecimal character to an integer.
	 * 
	 * @param ch
	 *            A character to convert to an integer digit
	 * @param index
	 *            The index of the character in the source
	 * @return An integer
	 */
	protected static int toDigit(char ch, int index) {
		int digit = Character.digit(ch, 16);
		if (digit == -1) {
			throw new IllegalArgumentException("Illegal hexadecimal charcter "
					+ ch + " at index " + index);
		}
		return digit;
	}

	public static byte[] hexStringToByte(String hex) {
		int len = (hex.length() / 2);
		byte[] result = new byte[len];
		char[] achar = hex.toCharArray();
		for (int i = 0; i < len; i++) {
			int pos = i * 2;
			result[i] = (byte) (toByte(achar[pos]) << 4 | toByte(achar[pos + 1]));
		}
		return result;
	}

	private static byte toByte(char c) {
		byte b = (byte) "0123456789ABCDEF".indexOf(c);
		return b;
	}

}
