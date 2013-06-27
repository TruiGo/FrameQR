package com.alipay.android.client.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Scanner;

import org.apache.http.message.BasicNameValuePair;

import android.content.ContentResolver;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.BitmapFactory.Options;
import android.text.InputFilter;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.alipay.android.appHall.Helper;
import com.alipay.android.client.RootActivity;
import com.alipay.android.client.constant.Constant;
import com.alipay.android.net.alipayclient2.APHttpClient;
import com.alipay.android.net.http.RespData;
import com.alipay.android.util.LogUtil;
import com.eg.android.AlipayGphone.R;

public class Utilz {
	
	public static int parseInt(String string) {
		int ret = -1;
		try {
			ret = Integer.parseInt(string);
		} catch (NumberFormatException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return ret;
	}
	
	public static int parseInt(String string, int defaultRet) {
		try {
			return Integer.parseInt(string);
		} catch (NumberFormatException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return  defaultRet;
	}
	
	public static Integer valueOf(String string) {
		int ret = -1;
		try {
			ret = Integer.valueOf(string);
		} catch (NumberFormatException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return ret;
	}
	
	public static boolean showInputMethod(View v, boolean checkCurrentViewFocus) {
		if (null == v || (checkCurrentViewFocus ? !v.isFocused() : false)) {
			return false;
		}
		
		InputMethodManager imm = (InputMethodManager)v.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);

		//imm.toggleSoftInput(0, 0);
		return imm.showSoftInput(v, InputMethodManager.SHOW_IMPLICIT); //or 0
	}
	
	public static boolean isSystemPatternEnable(ContentResolver resolver) {
		String enableString = android.provider.Settings.Secure.getString(resolver, "lock_pattern_autolock");//android.provider.Settings.Secure.LOCK_PATTERN_ENABLED
		return "1".equals(enableString);
	}
	
	public static boolean isEmpty(String string) {
		return (null== string || "".equals(string)) ? true : false;
	}
	
	public static String getSecuredRealName(String realName) {
		String realNameShow = realName;
		int length = realNameShow.length();
		if (length > 1) {
			realNameShow = "*" + realNameShow.substring(1);
		}
		
		return realNameShow;
	}
	
	public static String getSecuredNikName(String nikName) {
		String realNikShow = nikName;
		int length = realNikShow.length();
		if (length > 2) {
			realNikShow = realNikShow.substring(0, 1) + "**" + realNikShow.substring(length - 1);
		} else if (length > 0){
			realNikShow = "**" + realNikShow.substring(length - 1);
		}
		
		return realNikShow;
	}
		public static Bitmap getImage(Context context, String imagePath,String imageName){
		Bitmap image = null;
		final Options options = new Options();
        options.inDensity =(int) Helper.getDensityDpi(context);
        options.inScaled = true;
        try {
			image = BitmapFactory.decodeStream(context.getAssets().open(
					imagePath + File.separator + imageName+".png"), null, options);
        } catch (IOException e) {
			e.printStackTrace();
		}
        try {
        	if(image == null){
     			image = BitmapFactory.decodeStream(context.getAssets().open(imagePath + File.separator + "default.png"), null, options);
     		}
        } catch (IOException e) {
			e.printStackTrace();
		}
		return image;
	}
	
	public static void setAlpha(float alphaFloat, View view)
	{
		int alpha = (int)(alphaFloat * 255);
		
	    if (view instanceof ViewGroup)
	    {
	        for (int i = 0; i < ((ViewGroup) view).getChildCount(); i++)
	        {
	        	setAlpha(alpha, ((ViewGroup) view).getChildAt(i));
	            if (((ViewGroup) view).getBackground() != null) ((ViewGroup) view).getBackground().setAlpha(alpha);
	        }
	    }
	    else if (view instanceof ImageView)
	    {
	        if (((ImageView) view).getDrawable() != null) ((ImageView) view).getDrawable().setAlpha(alpha);
	        if (((ImageView) view).getBackground() != null) ((ImageView) view).getBackground().setAlpha(alpha);
	    }
	    else if (view instanceof TextView)
	    {
	        ((TextView) view).setTextColor(((TextView) view).getTextColors().withAlpha(alpha));
	        if (((TextView) view).getBackground() != null) ((TextView) view).getBackground().setAlpha(alpha);
	    }
	    else if (view instanceof EditText)
	    {
	        ((EditText) view).setTextColor(((EditText) view).getTextColors().withAlpha(alpha));
	        if (((EditText) view).getBackground() != null) ((EditText) view).getBackground().setAlpha(alpha);
	    }

	}
	
	public static void setTextLimit(EditText editText, int maxlength){
		if (maxlength >= 0) {
			editText.setFilters(new InputFilter[] { new InputFilter.LengthFilter(maxlength) });
        } else {
        	editText.setFilters(new InputFilter[0]);
        }
	}
	
	public static String getMobileNum4Show(String mobileNum) {
		if (null != mobileNum && mobileNum.length() > 7) {
			return mobileNum.substring(0, 3) + "****" + mobileNum.substring(7);
		}
		
		return mobileNum;	
	}
	public static boolean equalsAtBit(int num, int bit) {
		return 0 != (num & bit);
	}
	
	public static int checkNumber(String number, int lengthFixed) {
		int tResult = AlipayInputErrorCheck.CheckNumber(number);
		if (AlipayInputErrorCheck.NO_ERROR == tResult) {
			if (number.length() != lengthFixed) {
				tResult = AlipayInputErrorCheck.ERROR_INVALID_FORMAT;
			}
		}
		
		return translateErrCode(tResult);
	}
	
	public static int checkMobileNo(String mobileNo, RootActivity activity, boolean alertError) {
		String warningMsg = null;
		int tResult = AlipayInputErrorCheck.checkPhoneNumber(mobileNo);
		if (alertError && AlipayInputErrorCheck.NO_ERROR != tResult) {
			warningMsg = activity.getString(R.string.ErrorInputTelNum);
		}

		if (!Utilz.isEmpty(warningMsg)) {
			alert(warningMsg, activity);
		}
		
		return translateErrCode(tResult);
	}
	
	public static int translateErrCode(int errCodeSource) {
		switch (errCodeSource) {
		case AlipayInputErrorCheck.ERROR_NULL_INPUT:
			return Constant.SOURCE_NULL;
		case AlipayInputErrorCheck.NO_ERROR:
			return Constant.SOURCE_CORRECT;
		default:
			return Constant.SOURCE_FORMATERR;
		}
	}
	
	/*
	 * 0表示空
	 * 1表示正确
	 * 2表示错误
	 */
	public static int checkId(String id, RootActivity activity, boolean alertError) {
		String warningMsg = null;
		int tResult = AlipayInputErrorCheck.CheckIdentityCard(id);
		if (tResult != AlipayInputErrorCheck.NO_ERROR && alertError) {
			if (tResult == AlipayInputErrorCheck.ERROR_INVALID_FORMAT) {
				warningMsg = activity.getResources().getString(R.string.WarningInvalidIdCard);
				// Log.i(LOG_TAG, warningMsg);
			} else if (tResult == AlipayInputErrorCheck.ERROR_NULL_INPUT) {
				warningMsg = activity.getResources().getString(
						R.string.WarningIdCardEmpty);
			} else {
				warningMsg = "UNKNOWN_ERROR TYPE = " + tResult;
				// Log.i(LOG_TAG, warningMsg);
			}
		}

		if (!Utilz.isEmpty(warningMsg)) {
			alert(warningMsg, activity);
		}
		
		return translateErrCode(tResult);
	}
	
	public static void alert(String message, RootActivity activity) {
		activity.getDataHelper().showDialog(
				activity, R.drawable.infoicon, activity.getResources().getString(R.string.WarngingString),
				message, activity.getResources().getString(R.string.Ensure), null, null, null,
				null, null);
	}
	
	public static boolean isPhoneNumber(String string) {
		if (null == string || 11 != string.length()) {
			return false;
		}
		
		int offset = 0;
		int length = string.length();
		while (offset < length && -1 != Character.digit(string.charAt(offset), 10)) {
			offset++;
		}

		return offset == length;
	}
	
	public static void warmupUrl(final String url, final Context mContext, boolean syncRequest) {
		if (syncRequest) {
			LogUtil.logOnlyDebuggable("timestamp", "warmupUrl+sync:" + url);
			APHttpClient aPHttpClient = new APHttpClient(url, mContext);
	    	RespData resp = aPHttpClient.sendSynchronousGetRequest(null);
		} else {
			new Thread(new Runnable() {
				
				@Override
				public void run() {
					LogUtil.logOnlyDebuggable("timestamp", "warmupUrl+async:" + url);
					APHttpClient aPHttpClient = new APHttpClient(url, mContext);
			    	RespData resp = aPHttpClient.sendSynchronousGetRequest(null);
				}
			}).start();
		}

	}
	
    static int sFrequency = -1;
    public synchronized static int getCPUFrequencyMax() {
        if (sFrequency == -1) {
            sFrequency = readSystemFileAsInt("/sys/devices/system/cpu/cpu0/cpufreq/cpuinfo_max_freq");
            if (-1 != sFrequency) {
            	sFrequency /= 1000;
            }
        }

        return sFrequency;
    }

	private static int readSystemFileAsInt(final String pSystemFile) {
		FileInputStream fileInputStream = null;
		try {
			fileInputStream = new FileInputStream(pSystemFile);

			StringBuilder sb = new StringBuilder();
			final Scanner sc = new Scanner(fileInputStream);
			while (sc.hasNextLine()) {
				sb.append(sc.nextLine());
			}
			
			return Integer.parseInt(sb.toString());
		} catch (Exception e) {
			e.printStackTrace();
			
			return -1;
		} finally {
			try {
				fileInputStream.close();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
}
