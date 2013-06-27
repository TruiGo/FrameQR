package com.alipay.android.core.webapp;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.BitmapFactory.Options;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.text.Html;
import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.alipay.android.appHall.Helper;
import com.alipay.android.client.RootActivity;
import com.eg.android.AlipayGphone.R;

/**
 * @author sanping.li
 *
 */
public class TitleBar {
    private Page mPage;
    private RelativeLayout mTitleBar;
    private TextView mTitle;
    private Button mTitleLeft;
    private Button mTitleRight;
    private Button mTitleSlaveRight;

    public TitleBar(Page page) {
        mPage = page;
        RootActivity activity = (RootActivity) mPage.getContext();

        mTitleBar = (RelativeLayout) activity.findViewById(R.id.TitleLayout);
        mTitleLeft = (Button) activity.findViewById(R.id.title_left);
        mTitleRight = (Button) activity.findViewById(R.id.title_right);
        mTitleRight.setBackgroundResource(R.drawable.title_right_btn_bg);
        mTitleSlaveRight = (Button) activity.findViewById(R.id.title_slaveright);
        mTitleSlaveRight.setBackgroundResource(R.drawable.title_right_btn_bg);
        mTitle = (TextView) activity.findViewById(R.id.title_text);
    }

    public void hide(){
        mTitleBar.setVisibility(View.GONE);
    }

    public void initBarItem(final String name, final String title, final String action,final String icon) {
        mPage.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                View.OnClickListener clickListener = new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (action != null && action.length() > 0) {
                            mPage.sendJavascript(action);
                        }

                        if (v == mTitleLeft) {
                            mPage.sendJavascript("javascript:cordova.fireDocumentEvent('backbutton');");
                        } else if (v == mTitleRight) {
                            mPage.sendJavascript("cordova.require('cordova/channel').rightItem.fire();");
                        } else if (v == mTitleSlaveRight) {
                            mPage.sendJavascript("cordova.require('cordova/channel').slaveRightItem.fire();");
                        } else if (v == mTitle) {
                            mPage.sendJavascript("cordova.require('cordova/channel').centerItem.fire();");
                        }
                    }
                };
                if (name.equalsIgnoreCase("right")) {
                    initItem(mTitleRight,title,icon);
                    mTitleRight.setOnClickListener(clickListener);
                } else if (name.equalsIgnoreCase("slaveright")) {
                    initItem(mTitleSlaveRight,title,icon);
                    mTitleSlaveRight.setOnClickListener(clickListener);
                } else if (name.equalsIgnoreCase("left")) {
                    mPage.setBackAction(action);
                    initItem(mTitleLeft,title,icon);
                    mTitleLeft.setOnClickListener(clickListener);
                } else if (name.equalsIgnoreCase("center")) {
                    if (action != null && action.length() > 0) {
                        setCenterIndicator(true);
                    } else {
                        setCenterIndicator(false);
                    }
                    mTitle.setOnClickListener(clickListener);
                }
            }
        });

    }
    
    private void initItem(Button button,String title,String icon){
        if(icon!=null&&icon.length()>0){
            setButtonIcon(button,icon);
        }else if (title != null && title.length() > 0) {
            button.setVisibility(View.VISIBLE);
            button.setBackgroundResource(R.drawable.title_right_btn_bg);
            button.setText(title);
        } else {
            button.setVisibility(View.INVISIBLE);
        }
    }
    

    private Bitmap getBitmap(String path) {
        Options options = new Options();
        options.inDensity = (int) Helper.getDensityDpi(mPage.getContext());
        options.inScaled = true;

        try {
            return BitmapFactory.decodeFile(path, options);
        } catch (Error e) {
            e.printStackTrace();
        }

        return null;
    }

    public void clearBarItem() {
        mPage.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                mTitleLeft.setVisibility(View.INVISIBLE);
                mTitleLeft.setText("");
                mTitleLeft.setBackgroundResource(R.drawable.title_right_btn_bg);
                mTitleRight.setVisibility(View.INVISIBLE);
                mTitleRight.setText("");
                mTitleRight.setBackgroundResource(R.drawable.title_right_btn_bg);
                mTitleSlaveRight.setVisibility(View.INVISIBLE);
                mTitleSlaveRight.setText("");
                mTitleSlaveRight.setBackgroundResource(R.drawable.title_right_btn_bg);
                mTitle.setCompoundDrawables(null, null, null, null);
                setTitles(mPage.getAppView().getTitle());
                mPage.setBackAction(null);
            }
        });
    }

    public void setTitles(String title) {
        if (mPage.getAppView().getVisibility() == View.VISIBLE)
            mTitle.setText(Html.fromHtml(title).toString());
    }

    public void setTitle(final String name, final String title) {
        mPage.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (name.equalsIgnoreCase("left")) {
                    if (title != null && title.length() > 0) {
                        mTitleLeft.setVisibility(View.VISIBLE);
                        mTitleLeft.setText(title);
                        mTitleLeft.setBackgroundResource(R.drawable.title_right_btn_bg);
                    } else {
                        mTitleLeft.setVisibility(View.INVISIBLE);
                    }
                } else if (name.equalsIgnoreCase("right")) {
                    if (title != null && title.length() > 0) {
                        mTitleRight.setVisibility(View.VISIBLE);
                        mTitleRight.setText(title);
                        mTitleRight.setBackgroundResource(R.drawable.title_right_btn_bg);
                    } else {
                        mTitleRight.setVisibility(View.INVISIBLE);
                    }
                } else if (name.equalsIgnoreCase("slaveright")) {
                    if (title != null && title.length() > 0) {
                        mTitleSlaveRight.setVisibility(View.VISIBLE);
                        mTitleSlaveRight.setText(title);
                        mTitleSlaveRight.setBackgroundResource(R.drawable.title_right_btn_bg);
                    } else {
                        mTitleSlaveRight.setVisibility(View.INVISIBLE);
                    }
                } else if (name.equalsIgnoreCase("center")) {
                    setTitles(title);
                }
            }
        });
    }

    public void setIcon(final String name, final String icon) {
        mPage.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (name.equalsIgnoreCase("left")) {
                    if (icon != null && icon.length() > 0) {
                        setButtonIcon(mTitleLeft,icon);
                    } else {
                        mTitleLeft.setVisibility(View.INVISIBLE);
                    }
                } else if (name.equalsIgnoreCase("right")) {
                    if (icon != null && icon.length() > 0) {
                        setButtonIcon(mTitleRight,icon);
                    } else {
                        mTitleRight.setVisibility(View.INVISIBLE);
                    }
                } else if (name.equalsIgnoreCase("slaveright")) {
                    if (icon != null && icon.length() > 0) {
                        setButtonIcon(mTitleSlaveRight,icon);
                    } else {
                        mTitleSlaveRight.setVisibility(View.INVISIBLE);
                    }
                }
            }
        });
    }

    public void setCenterIndicator(final boolean visiable) {
        mPage.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Drawable drawable = null;
                if (visiable) {
                    drawable = mPage.getContext().getResources().getDrawable(R.drawable.title_dropdown_icon);
                    drawable.setBounds(0, 0, drawable.getMinimumWidth(),
                        drawable.getMinimumHeight());
                    mTitle.setCompoundDrawablePadding(5);
                }
                mTitle.setCompoundDrawables(null, null, drawable, null);
            }
        });
    }

    private void setButtonIcon(Button button, String icon) {
    	Bitmap bitmap = getBitmap(mPage.getRunTime().getPath()+ "www/" +icon);
    	
    	if (bitmap != null){
	        float scale = Helper.getDisplayMetrics(mPage.getContext()).density;
	        button.setVisibility(View.VISIBLE);
	        button.setText("");
	        int h = button.getHeight();
	        int bh = (int) (bitmap.getHeight()*scale);
	        if(button.getHeight()>bh)
	            h = bh;
	        int w =(int) ((h*bitmap.getWidth())/bitmap.getHeight());
	        BitmapDrawable drawable = new BitmapDrawable(bitmap);
	        button.setBackgroundDrawable(drawable);
	        button.getLayoutParams().width=w;
	        button.getLayoutParams().height=h;
    	}
    }

}
