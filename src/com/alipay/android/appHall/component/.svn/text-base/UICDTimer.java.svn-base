/**
 * 
 */
package com.alipay.android.appHall.component;

import java.util.HashMap;
import java.util.Timer;
import java.util.TimerTask;

import android.content.Context;
import android.graphics.Color;
import android.os.Handler;
import android.os.Message;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.alipay.android.appHall.uiengine.UIInterface;
import com.alipay.android.core.expapp.ExpAppRuntime;
import com.alipay.android.core.expapp.Page;
import com.eg.android.AlipayGphone.R;

/**
 * @author sanping.li
 * 倒计时器
 *
 */
public class UICDTimer extends TextView implements UIInterface {
    /**
     * 前面的提示文字
     */
    private String mHint;
    /**
     * 剩余时间,秒
     */
    private long mTime;

    /**
     * 定时器
     */
    private Timer timer;

    /**
     * 结束表达式
     */
    private String mExpression;
    private Page mPage;

    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            update();
        }
    };

    public UICDTimer(Context context, Page page) {
        super(context);
        mPage = page;
    }

    public void init(ViewGroup parent, String hint) {
        mHint = hint;

        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
            LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
        layoutParams.leftMargin = 15;
        layoutParams.rightMargin = 15;
        //        layoutParams.gravity=Gravity.LEFT|Gravity.CENTER_VERTICAL;
        this.setGravity(Gravity.LEFT);
        this.setLayoutParams(layoutParams);
        this.setTextColor(Color.BLACK);
        this.setTextSize(16);
    }

    /**
     * 启动重新倒计时timer
     */
    protected void startTimer() {
        timer = new Timer();
        mPage.getEngine().registerRuntimeObject(ExpAppRuntime.RUNTIME_TIMER, null, timer);
        timer.schedule(new TimerTask() {
            public void run() {
                mTime -= 10;
                handler.sendEmptyMessage(0);
            }
        }, 0, 10000);
    }

    private void cancelTimer() {
        mTime = 0;
        if (timer != null) {
            timer.cancel(); //停止timer
            timer = null;
        }
    }

    private void update() {
        if (mTime <= 0) {
            if (mExpression != null && timer != null) {
                Object id = getTag() == null ? "" : getTag();
                mPage.interpreter("cdTimer id:" + id.toString(), mExpression);
            }
            cancelTimer();
            return;
        }
        StringBuffer buffer = new StringBuffer(mHint);
        int day = (int) (mTime / (60 * 60 * 24));
        if (day >= 1)
            buffer.append(day + getContext().getString(R.string.day));
        int hour = (int) ((mTime - day * 60 * 60 * 24) / (60 * 60));
        //        if(day>=1||hour>=1)
        buffer.append(hour + getContext().getString(R.string.hour));
        int minute = (int) (mTime - day * 60 * 60 * 24 - hour * 60 * 60) / 60;
        buffer.append(minute + getContext().getString(R.string.minute));

        this.setText(buffer.toString());
    }

    public void setExpression(String expression) {
        mExpression = expression;
    }

    public String getExpression() {
        return mExpression;
    }

    //-----------------------UI的接口-------------------------------
    @Override
    public void setVisible(boolean isVisible) {
        setVisibility(isVisible ? View.VISIBLE : View.GONE);
    }

    @Override
    public boolean getVisible() {
        return getVisibility() == View.VISIBLE;
    }

    @Override
    public void setValue(Object value) {
        mTime = Long.parseLong(value.toString());

        update();
        startTimer();
    }

    @Override
    public Object getValue() {
        return mTime;
    }

    @Override
    public void setEnable(boolean isEnable) {
        setEnabled(isEnable);
    }

    @Override
    public boolean getEnable() {
        return isEnabled();
    }

    @Override
    public String getSelectedIndex() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public void setIsSave(boolean isSave) {

    }

    @Override
    public boolean getIsSave() {
        return false;
    }

    @Override
    public void set_MarginLeft(int marginLeft) {
        LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) getLayoutParams();
        params.leftMargin = marginLeft;
    }

    @Override
    public int get_MarginLeft() {
        LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) getLayoutParams();
        return params.leftMargin;
    }

    @Override
    public void set_MarginRight(int marginRight) {
        LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) getLayoutParams();
        params.rightMargin = marginRight;
    }

    @Override
    public int get_MarginRight() {
        LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) getLayoutParams();
        return params.rightMargin;
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
    public void set_Width(int width) {
        LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) getLayoutParams();
        params.width = width;
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
        mHint = text;
    }

    @Override
    public String get_Text() {
        return getText().toString();
    }

    @Override
    public String get_Tag() {
        return (String) getTag();
    }

    @Override
    public void set_Tag(String tag) {
        setTag(tag);
    }

    @Override
    public LinearLayout.LayoutParams getAlipayLayoutParams() {
        return (android.widget.LinearLayout.LayoutParams) getLayoutParams();
    }

    //tag for android 1.5
    HashMap<Integer, Object> mTags = new HashMap<Integer, Object>();

    @Override
    public Object get_tag(int type) {
        return mTags.get(type);
    }

    @Override
    public void set_Tag(int type, Object tag) {
        mTags.put(type, tag);
    }
}
