package com.alipay.android.client.util;

import android.content.Context;
import android.graphics.Color;
import android.os.Handler;
import android.util.AttributeSet;
import android.view.InflateException;
import android.view.LayoutInflater;
import android.view.LayoutInflater.Factory;
import android.view.View;
import android.widget.TextView;

public class MenuHelper {

	public static boolean setMemu(final LayoutInflater inflater) {
		boolean ret = true;
		try {
			inflater.setFactory(new Factory() {
			    @Override
			    public View onCreateView(String name, Context arg1, AttributeSet attrs) {
			        if (name.indexOf(".IconMenuItemView") >= 0) {
			            // Ask our inflater to create the view.
			            LayoutInflater f = inflater;
			            final View[] view = new View[1];
			            try {
			                view[0] = f.createView(name, null, attrs);
			            } catch (InflateException e) {
			                hackAndroid23(name, attrs, f, view);
			            } catch (Exception e) {
			                e.printStackTrace();
			            }
			            
			            // Kind of apply our own background.
			            new Handler().post(new Runnable() {
			                public void run() {
			                	View viewTemp = view[0];
			                	if (null != viewTemp) {
			                		viewTemp.setBackgroundColor(Color.rgb(227,224,219)); //R.color.menuitem_color);
			                		
			                		if (viewTemp instanceof TextView) {
			                            // set the text color
			                            ((TextView)view[0]).setTextColor(Color.BLACK);
			                		}
			                	}

			                }
			            });
			            
			            return view[0];
			        }                
			        return null;
			    }
			});
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			ret = true;
		}
		
		return ret;
	}

	public static void hackAndroid23(final String name,
			final android.util.AttributeSet attrs, final LayoutInflater f,
			final View[] view) {
		try {
			f.inflate(new MenuXmlPullParser(name, attrs, f, view), null, false);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
