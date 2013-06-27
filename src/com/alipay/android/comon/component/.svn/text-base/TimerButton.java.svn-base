package com.alipay.android.comon.component;

import com.alipay.android.ui.transfer.NewReceiverViewController;
import com.eg.android.AlipayGphone.R;

import android.R.integer;
import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.widget.Button;

public class TimerButton extends Button {

	public TimerButton(Context context) {
		super(context);
		// TODO Auto-generated constructor stub
	}
    public TimerButton(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public TimerButton(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }
    
    private String mFormatText;
    public TimerButton setFormatText(String formatText) {
    	mFormatText = formatText;
    	return this;
    }
    
    private String mTextEnabled;
    public TimerButton setTextEnabled(String TextEnabled) {
    	mTextEnabled = TextEnabled;
    	return this;
    }
    
    private int mTickCount;
    public TimerButton setTickCount(int tickCount) {
    	stopTicking();
    	mTickCount = tickCount;
    	return this;
    }
    
    private Handler handler = new Handler() {

		@Override
		public void handleMessage(Message msg) {
			
			switch (msg.what) {
			case TIMERBUTTON_TICK:
				if (0 == --mTickCount) {
					enableMe();
				} else {
					if (null != mFormatText) {
						setText(String.format(mFormatText, mTickCount));
						tick();
					}				
				}
				default:
					break;
			}

			
			super.handleMessage(msg);
		}
    	
    };
    
    private static final int TIMERBUTTON_TICK = 1;
    public TimerButton startTicking() {
    	if (mTickCount > 0) {
    		disableMe();
    		
			if (null != mFormatText) {
				setText(String.format(mFormatText, mTickCount));
			}
			
    		tick();
    	} else {
    		setTextColor(getResources().getColor(R.color.ButtonColorYellow));
    	}
    	
    	return this;
    }
    
    public void stopTicking() {
    	if (mTickCount > 0) {
    		handler.removeMessages(TIMERBUTTON_TICK);
    	}
    }
    
    public void disableMe() {
    	setEnabled(false);
		setTextColor(getResources().getColor(R.color.TextColorGray));
    }
    
    public void enableMe() {
    	setEnabled(true);
		setText(mTextEnabled);
		setTextColor(getResources().getColor(R.color.ButtonColorYellow));
    }
    
    private void tick() {
    	handler.sendMessageDelayed(Message.obtain(handler, TIMERBUTTON_TICK), 1000);
    }
}
