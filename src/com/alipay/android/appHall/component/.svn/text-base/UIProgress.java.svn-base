package com.alipay.android.appHall.component;

import java.util.HashMap;
import java.util.Timer;
import java.util.TimerTask;

import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ProgressBar;

import com.alipay.android.appHall.uiengine.UIInterface;
import com.alipay.android.core.expapp.ExpAppRuntime;
import com.alipay.android.core.expapp.Page;
import com.eg.android.AlipayGphone.R;

public class UIProgress extends LinearLayout implements UIInterface {

    private Page mPage;

    private ProgressBar pb;

    public UIProgress(Page page) {
        super(page.getRawContext());
        mPage = page;
    }

    public void init(ViewGroup parent, int style) {
        // pb = (ProgressBar)
        // LayoutInflater.from(mPage.getRawContext()).inflate(
        // R.layout.uiprogressbar, parent, false);

        if (style != 2) {
            pb = (ProgressBar) LayoutInflater.from(mPage.getRawContext()).inflate(
                R.layout.uiprogressbarstylehorizontal, parent, false);
        } else {
            pb = (ProgressBar) LayoutInflater.from(mPage.getRawContext()).inflate(
                R.layout.uiprogressbarstyleinverse, parent, false);
        }

        this.addView(pb);
        // LinearLayout.LayoutParams params = (LinearLayout.LayoutParams)
        // pb.getLayoutParams();
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
            LayoutParams.FILL_PARENT, LayoutParams.WRAP_CONTENT);
        this.setLayoutParams(layoutParams);
    }

    private boolean isDoReset;

    public void setIsDoReset(boolean isDoReset) {
        this.isDoReset = isDoReset;
    }

    public boolean getIsDoReset() {
        return isDoReset;
    }

    private String expression;

    public void setExpression(String expression) {
        this.expression = expression;
    }

    public String getExpression() {
        return expression;
    }

    // 以秒为单位
    private long updateTimeBlock = 1;

    public void setUpdateTimeBlock(long updateTimeBlock) {
        this.updateTimeBlock = updateTimeBlock;
    }

    public long getUpdateTimeBlock() {
        return updateTimeBlock;
    }

    // 最大时间，以秒为单位
    private int maxBlock;

    public synchronized int get_Max() {
        return pb.getMax();
    }

    public synchronized void set_Max(int maxBlock) {
        this.maxBlock = maxBlock;
        pb.setMax(maxBlock);
    }

    private int currentProgress;

    public synchronized int get_Progress() {
        return pb.getProgress();
    }

	public synchronized void set_Progress(int currentProgress) {
		this.currentProgress = currentProgress;
		pb.setProgress(currentProgress);
		if (timer != null) {
			timer.cancel();
		}
		mPage.getEngine().acquireWakeLock();
		startTimer();
	}

    private boolean isBegin = true;

    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            Object id = getTag() == null ? "" : getTag();
            if (maxBlock == currentProgress && isDoReset == true) {
                isBegin = true;
                currentProgress = 1;
                pb.setProgress(currentProgress);
                mPage.interpreter("progressbar::" + id.toString(), expression);
            } else if (maxBlock == currentProgress && isDoReset == false) {
                mPage.interpreter("progressbar::" + id.toString(), expression);
                timer.cancel();
            }
            if (!isBegin) {
                incrementProgressBy((int) (updateTimeBlock));
                return;
            }
            isBegin = false;
        }
    };

    private Timer timer;

    protected void startTimer() {
        timer = new Timer();
        mPage.getEngine().registerRuntimeObject(ExpAppRuntime.RUNTIME_TIMER, null, timer);
        timer.schedule(new TimerTask() {
            public void run() {
                handler.sendEmptyMessage(0);
            }
        }, 0, updateTimeBlock * 1000);
    }

    private synchronized void incrementProgressBy(int diff) {
        if (currentProgress + diff > maxBlock) {
            pb.setProgress(maxBlock);
            // mPage.interpreter(expression);
            return;
        }
        if (currentProgress + diff < 0) {
            pb.setProgress(0);
            return;
        }

        currentProgress = currentProgress + diff;
        pb.incrementProgressBy(diff);
    }

    public void setVisible(boolean isVisible) {
        setVisibility(isVisible ? View.VISIBLE : View.GONE);
    }

    public boolean getVisible() {
        return getVisibility() == View.VISIBLE;
    }

    @Override
    public void setValue(Object value) {
        // TODO Auto-generated method stub
        if (value == null) {
            return;
        } else if (Integer.parseInt(value.toString()) == 0) {
            this.set_Progress(0);
        }
    }

    @Override
    public Object getValue() {
        // TODO Auto-generated method stub
        return null;
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
    private int width = LayoutParams.WRAP_CONTENT;

    public void set_Width(int width) {
        isChange = true;
        this.width = width;
        changedWidth();
    }

    private void changedWidth() {
        ViewGroup.LayoutParams params = (ViewGroup.LayoutParams) pb.getLayoutParams();
        params.width = this.width;
        pb.setLayoutParams(params);
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

    @Override
    protected void finalize() throws Throwable {
        // TODO Auto-generated method stub
        super.finalize();
        if (timer != null) {
            timer.cancel();
            timer = null;
        }
    }

}
