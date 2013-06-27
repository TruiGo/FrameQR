package com.alipay.android.appHall;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Calendar;
import java.util.HashMap;

import org.json.JSONArray;
import org.json.JSONObject;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.BitmapFactory.Options;
import android.net.Uri;
import android.text.Editable;
import android.text.Selection;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;

import com.alipay.android.appHall.appManager.AppItemInfo;
import com.alipay.android.appHall.appManager.AppManager;
import com.alipay.android.appHall.appManager.HallData;
import com.alipay.android.appHall.common.BitmapDownloadListener;
import com.alipay.android.appHall.common.BitmapDownloader;
import com.alipay.android.appHall.common.Defines;
import com.alipay.android.client.util.BaseHelper;
import com.alipay.android.comon.component.ProgressDiv;
import com.alipay.android.net.http.HttpClient;
import com.alipay.android.util.FileUtils;

public class Helper {
    //    public static Bitmap bitmapFromStatus(Context context, Bitmap bm, String status) {
    //        int resId = 0x00;
    //        if (status.equalsIgnoreCase(Defines.in)) {
    //            resId = R.drawable.setup_icon;
    //        } else if (status.equalsIgnoreCase(Defines.up)) {
    //            resId = R.drawable.update_icon;
    //        } else if (status.equalsIgnoreCase(Defines.un)) {
    //            resId = R.drawable.offline_icon;
    //        }else
    //            return bm;
    //
    //        Bitmap mask = BitmapFactory.decodeResource(context.getResources(), resId);
    //        Bitmap blend = blendBitmap(context, bm, mask, 0, 0.5f * bm.getHeight(), bm.getWidth(), bm.getHeight()*5/6);
    //        return blend;
    //    }

    /**
     * 根据传入的条件删除JsonArray中的对象,不需要进行的操作传入null
     * @param appArray 传入的Json对象
     * @param funcId 传入的功能参数
     * @param index 删除该索引表示的对象
     * @return 进行操作后返回的Json对象
     */
    //	public static JSONArray removeItemFromArrayByParam(JSONArray preArray,String funcId, Integer index) {
    //		JSONArray newArray = new JSONArray();
    //		try {
    //			for (int i = 0; i < preArray.length(); i++) {
    //				JSONObject preItem = preArray.getJSONObject(i);
    //				if (index != null) {
    //					if (i != index) 
    //						newArray.put(preItem);
    //				} else if (funcId != null) {
    //					String tempfuncId = preItem.optString(Defines.funcId);
    //					if (!tempfuncId.equalsIgnoreCase(funcId))
    //						newArray.put(preItem);
    //				} else {
    //					// 待添加操作
    //				}
    //			}
    //		} catch (Exception e) {
    //			e.printStackTrace();
    //		}
    //		return newArray;
    //	}
    
    public static LinearLayout.LayoutParams copyLinearLayoutParams(LinearLayout.LayoutParams layoutParams){
        LinearLayout.LayoutParams params = new LayoutParams(layoutParams.width, layoutParams.height);
        params.gravity = layoutParams.gravity;
        params.bottomMargin=layoutParams.bottomMargin;
        params.topMargin = layoutParams.topMargin;
        params.leftMargin = layoutParams.leftMargin;
        params.rightMargin = layoutParams.rightMargin;
        params.weight=layoutParams.weight;
        params.layoutAnimationParameters = layoutParams.layoutAnimationParameters;
        return params;
    }
    
    public static JSONArray removeItemFromArrayById(JSONArray appArray, String funcId) {
        JSONArray appNewArray = new JSONArray();
        try {
            for (int i = 0; i < appArray.length(); i++) {
                try {
                    JSONObject appItem = appArray.getJSONObject(i);
                    String tempfuncId = appItem.optString(Defines.funcId);
                    if (!tempfuncId.equalsIgnoreCase(funcId))
                        appNewArray.put(appItem);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return appNewArray;
    }

//    public static int findItemFromArrayById(JSONArray appArray, String funcId) {
//        int index = -1;
//        try {
//            for (int i = 0; i < appArray.length(); i++) {
//                try {
//                    JSONObject appItem = appArray.getJSONObject(i);
//                    String tempfuncId = appItem.optString(Defines.funcId);
//                    if (tempfuncId.equalsIgnoreCase(funcId)) {
//                        index = i;
//                        break;
//                    }
//                } catch (Exception e) {
//                    e.printStackTrace();
//                }
//            }
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//        return index;
//    }
    
    public synchronized static Bitmap bitmapFromUriString(final Context context, final Object object,
                                             final BitmapDownloadListener bitmapDownloadListener,int defVal) {
    	// Prepare appropriate options for target bitmap.
    	final Options options = new Options();
    	options.inDensity =(int) Helper.getDensityDpi(context);
    	options.inScaled = true;
    	
    	// Use a local image As temp.
    	final Bitmap bmTemp = (-1 == defVal) ? null : BitmapFactory.decodeResource(context.getResources(),
    			defVal,options);
    	
    	if(object == null || "".equals(object))
    		return bmTemp;
    	
        String scheme = null;
        if(object instanceof String)
            scheme = Uri.parse((String)object).getScheme();
        
        if (scheme != null && scheme.indexOf("http") != -1) {
            new Thread(new Runnable() {
                public void run() {
                    try {
                        HttpClient httpClient = new HttpClient((String) object, context);
                        InputStream inputStream = httpClient.inputStreamFromUrl();
                        Bitmap bm = BitmapFactory.decodeStream(inputStream, null, options);
                        if(inputStream!=null)
                            inputStream.close();
                        
						if (bm == null){
							if(bitmapDownloadListener instanceof BitmapDownloader)
	                        	return;
							bm = bmTemp;
						}
                        
                        bitmapDownloadListener.onComplete(bm);
                    } catch (Exception e) {
                        e.printStackTrace();
                        //通知下载失败
                        notifyResult(bitmapDownloadListener,false);
                    }
                }
            }).start();
            
            //通知正在下载
            notifyResult(bitmapDownloadListener,true);
            return bmTemp;
        } else {
            Bitmap bm = null;
            if(object instanceof String)
                bm = BitmapFactory.decodeFile((String)object, options);
            else if(object instanceof File){
                try {
                    FileInputStream inputStream = new FileInputStream((File)object);
                    bm = BitmapFactory.decodeStream(inputStream,null, options);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
			if (bm == null)
				bm = bmTemp;
			
//			if (bitmapDownloadListener != null)
//                bitmapDownloadListener.onComplete(bm);
			
			return bm;
        }
    } 
    
    private static void notifyResult(BitmapDownloadListener bitmapDownloadListener,boolean result){
    	if(bitmapDownloadListener instanceof BitmapDownloader){
			BitmapDownloader bitmapDownloader = (BitmapDownloader)bitmapDownloadListener;
			bitmapDownloader.onDownLoading(result);
		}
    }
    
    public static int getDensityDpi(Context context) {
        DisplayMetrics displayMetrics = getDisplayMetrics(context);
        return displayMetrics.densityDpi;
    }
    
    public static float getDensity(Context context) {
        DisplayMetrics displayMetrics = getDisplayMetrics(context);
        return displayMetrics.density;
    }

    public static DisplayMetrics getDisplayMetrics(Context context) {
        DisplayMetrics tDisplayMetrics = new DisplayMetrics();
        Display tDisplay = ((WindowManager) context.getSystemService(Context.WINDOW_SERVICE))
            .getDefaultDisplay();
        tDisplay.getMetrics(tDisplayMetrics);
        return tDisplayMetrics;
    }

    //    public static Bitmap bitmapFromRatio(Context context, float value, Bitmap bm) {
    //        Bitmap barBitmap = BitmapFactory.decodeResource(context.getResources(), R.drawable.loading_bar);
    //        Bitmap mask = BitmapFactory.decodeResource(context.getResources(), R.drawable.loading_bg);
    //        barBitmap = blendBitmap(context, barBitmap, mask, value * mask.getWidth(), 0, barBitmap.getWidth(), barBitmap.getHeight());
    //        Bitmap result = blendBitmap(context, bm, barBitmap, bm.getWidth()*0.1f, 0.78f*bm.getHeight(), bm.getWidth()*0.9f, bm.getHeight()*0.9f);
    //        return result;
    //    }
    //
    //    private static Bitmap blendBitmap(Context context, Bitmap bitmap, Bitmap mask, float left, float top, float right, float bottom) {
    //        Bitmap blend = bitmap;
    //        try {
    //            blend = bitmap.copy(Bitmap.Config.ARGB_8888, true);
    //            Canvas canvas = new Canvas(blend);
    //            Rect dst = new Rect((int)left, (int)top, (int)right, (int)bottom);
    //            canvas.drawBitmap(mask, null, dst, null);
    //            canvas.save(Canvas.ALL_SAVE_FLAG);
    //            canvas.restore();
    //        } catch (Exception e) {
    //            e.printStackTrace();
    //        }
    //        return blend;
    //    }
    //
    //    public static Bitmap bitmapFromPaused(Context context, Bitmap original, float ratio) {
    //        Bitmap mask = BitmapFactory.decodeResource(context.getResources(), R.drawable.resume_icon);
    //        Bitmap paused = blendBitmap(context, original, mask, original.getWidth()*0.25f, original.getHeight()*0.25f, original.getWidth()*0.75f, original.getHeight()*0.75f);
    //        paused = bitmapFromRatio(context, ratio, paused);
    //
    //        return paused;
    //    }    
    
    public static String dayFromInt(int day) {
        day++;
        
        String strDay = null;
        switch(day){
            case Calendar.SUNDAY:
                strDay = "Sun";
                break;
            case Calendar.MONDAY:
                strDay = "Mon";
                break;
            case Calendar.TUESDAY:
                strDay = "Tue";
                break;
            case Calendar.WEDNESDAY:
                strDay = "Wed";
                break;
            case Calendar.THURSDAY:
                strDay = "Thu";
                break;
            case Calendar.FRIDAY:
                strDay = "Fri";
                break; 
            case Calendar.SATURDAY:
                strDay = "Sat";
                break;                
        }
        return strDay;
    }    
    
    public static ProgressDiv showProgressDialog(Context context, CharSequence title,
                                                    CharSequence message) {
        try {
            ProgressDiv dialog = new ProgressDiv(context);
            dialog.setMessage(message);
            dialog.setCancelable(false);
            dialog.show();
            return dialog;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public static void hideInputPanel(Context context, View view) {
        // Hide the input method panel.
        try {
            hideInput(context, view);
            View focus = view.findFocus();
            if (focus != null)
                focus.clearFocus();
        } catch (Exception e) {
            e.printStackTrace();
        }        
    }
    
    /**
     * 隐藏输入法，不移除焦点
     * @param context
     * @param view
     */
    public static void hideInput(Context context, View view) {
    	// Hide the input method panel.
    	try {
    		InputMethodManager imm = (InputMethodManager) context
    		.getSystemService(Context.INPUT_METHOD_SERVICE);
    		imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
    	} catch (Exception e) {
    		e.printStackTrace();
    	}        
    }
    
    public static void showInputPanel(Context contex,View view){
    	view.requestFocus();
    	InputMethodManager im = ((InputMethodManager)contex.getSystemService(Context.INPUT_METHOD_SERVICE));  
    	im.showSoftInput(view, 0);
    }
    
    public static void moveCursorToLast(Editable etable) {
		Selection.setSelection(etable,etable.length());       
    }

    public static JSONObject getConfig(InputStream isConfig) {
        JSONObject configObj = null;
        try {
            configObj = new JSONObject(BaseHelper.convertStreamToString(isConfig));
        } catch (Exception e) {
        }
        
        return configObj;
    }
    
    public static HashMap<String, AppItemInfo> getConfigInfo(HallData appHall, String fileName) {
        HashMap<String,AppItemInfo> rets = new HashMap<String,AppItemInfo>();
        FileInputStream fis = null;
        try {
            File file = new File(fileName);
            fis = new FileInputStream(file);
            rets = getConfigInfo(appHall,fis);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }finally{
            try {
              if(fis!=null)
                  fis.close();
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }  
        }
        return rets;
    }
    
    public static HashMap<String, AppItemInfo> getConfigInfo(HallData hallData, InputStream isConfig) {
        HashMap<String,AppItemInfo> rets = new HashMap<String,AppItemInfo>();
        
        JSONObject configObj = getConfig(isConfig);
        if (configObj == null)
            return rets;

        AppItemInfo appConfigInfo = null;
        try {
            AppManager appManager = hallData.getAppManager();
            JSONArray apps = configObj.getJSONArray("apps");
            for (int i = 0; i < apps.length(); i++) {
                JSONObject app = apps.getJSONObject(i);
                
                String funcId = app.optString(Defines.funcId,null);
                Object o = app.opt(Defines.type);
                int type = AppItemInfo.TYPE_XML;
                if (o != null) {//兼容以前格式
                    if (o instanceof String) {
                        String string = o.toString();
                        type = string.equalsIgnoreCase(Defines.x) ? AppItemInfo.TYPE_XML
                            : AppItemInfo.TYPE_NATIVE;
                    } else {
                        type = (Integer) o;
                    }
                }
                appConfigInfo = new AppItemInfo(hallData, funcId, type);
                
                String name = app.optString(Defines.name,"");
                if(name.length()<=0&&funcId!=null&&appManager.isAppExisted(funcId)){
                    name = appManager.getAppName(funcId);
                }
                appConfigInfo.setName(name);

                String icon = app.optString(Defines.icon, "");
                if(icon.length()<=0&&funcId!=null&&appManager.isAppExisted(funcId)){
                    icon = appManager.getAppIcon(funcId);
                }else if(!icon.startsWith("http://")&&icon.indexOf('/')!=-1){
                    int index = icon.lastIndexOf('/');
                    icon = icon.substring(index);
                }
                appConfigInfo.setIconUrl(icon);

                int showOrder = app.optInt(Defines.showOrder, Integer.MAX_VALUE);
                appConfigInfo.setOrder(showOrder);

                String version = app.optString(Defines.version, "1.0.0.0");
                appConfigInfo.setCurVersion(version);
                
                int isShow = app.optInt(Defines.isShow,1);
                appConfigInfo.setShow(isShow);
                
                int state = app.optInt(Defines.status, AppItemInfo.STATE_NOMARL);
                appConfigInfo.setState(state);
                
                appConfigInfo.setMinSdkVersion(app.optInt(Defines.minSdkVersion, 0x00));
                
                appConfigInfo.setUrl(app.optString(Defines.uri, null));
                
                appConfigInfo.setPercent(app.optDouble(Defines.percent, 0));

                appConfigInfo.setMinCpu(app.optInt(Defines.minCpu, 0));

                rets.put(funcId,appConfigInfo);
            }
        } catch (Exception e) {
            //e.printStackTrace();
        }

        return rets;
    }    
    
    /**
     * 全角转半角
     * @param input String.
     * @return 半角字符串
     */
	public static String toDBC(String input) {
		char c[] = input.toCharArray();
		for (int i = 0; i < c.length; i++) {
			if (c[i] == '\u3000') {
				c[i] = ' ';
			} else if (c[i] > '\uFF00' && c[i] < '\uFF5F') {
				c[i] = (char) (c[i] - 65248);
			}
		}
		String returnString = new String(c);
		return returnString;
	}

	public static void clearFocus(View view) {
		 View focus = view.findFocus();
         if (focus != null)
             focus.clearFocus();
	}
	
	public static String getCachePath(Context context,String cacheDir){
    	File logoDir = getDiskCacheDir(context,cacheDir);
    	if(logoDir == null){
    		logoDir = new File(context.getCacheDir().getPath() + File.separator + cacheDir);
    	}
		return logoDir.getAbsolutePath() + File.separator;
    }
	
	public static File getDiskCacheDir(Context context,String fileDir) {
        // Check if media is mounted or storage is built-in, if so, try and use external cache dir
        // otherwise use internal cache dir
        try {
			File cacheFile = context.getCacheDir();
			File cacheFileDir = new File(cacheFile + File.separator + fileDir);
			if(!cacheFileDir.exists()){
				cacheFileDir.mkdirs();
			}
			return cacheFileDir;
		} catch (Exception e) {
			e.printStackTrace();
		}
        return null;
    }
	
	//clean voucher logo cache files 
	public static void cleanFiles(Context context,File filePath){
		if(filePath != null){
			if(filePath != null /*&& filePath.getTotalSpace() > 1024 * 1024 * 1*/){
				File[] logoFiles = filePath.listFiles(/*new FilenameFilter() {
					@Override
					public boolean accept(File dir, String filename) {
						return filename.endsWith(".png");
					}
				}*/);
				
				for(File file :logoFiles){
					if(file.isFile()){
						file.delete();
					}
				}
			}
		}
	}
	
	public static String urlToKey(String logoUrl){
		if(logoUrl != null)
			return urlMd5(logoUrl);
		return null;
	}
	
	private static String urlMd5(String url){
		try {
			MessageDigest md5 = MessageDigest.getInstance("MD5");
			char[] charArray = url.toCharArray();
			byte[] byteArray = new byte[charArray.length];
			
			for (int i = 0; i < charArray.length; i++)
			    byteArray[i] = (byte) charArray[i];
			
			byte[] md5Bytes = md5.digest(byteArray);
			
			StringBuffer hexValue = new StringBuffer();
			
			for (int i = 0; i < md5Bytes.length; i++) {
			    int val = ((int) md5Bytes[i]) & 0xff;
			    if (val < 16)
			        hexValue.append("0");
			    hexValue.append(Integer.toHexString(val));
			}
			return hexValue.toString();
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		}
		return null;
	}
	
}
