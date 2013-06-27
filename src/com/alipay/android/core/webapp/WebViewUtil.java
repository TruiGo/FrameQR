package com.alipay.android.core.webapp;

import java.lang.reflect.Field;

import android.graphics.Color;
import android.webkit.WebView;
import android.widget.EditText;

public class WebViewUtil {
    public static void processEdit(WebView view){
        EditText editText = null;
        try {
            Class<?> webViewDebugClass = Class.forName("android.webkit.DebugFlags");
            Field f = webViewDebugClass.getDeclaredField("WEB_VIEW");
            f.setAccessible(true);
            f.setBoolean(null, true);
            f = webViewDebugClass.getDeclaredField("WEB_TEXT_VIEW");
            f.setAccessible(true);
            f.setBoolean(null, true);
            
            editText = (EditText) ProxyUtil.getDeclaredField(view, "mWebTextView");
        } catch (SecurityException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IllegalArgumentException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (NoSuchFieldException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
//        editText.setBackgroundColor(Color.TRANSPARENT);
 catch (ClassNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }
}
