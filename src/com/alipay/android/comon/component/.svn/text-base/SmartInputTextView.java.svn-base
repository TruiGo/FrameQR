package com.alipay.android.comon.component;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.AutoCompleteTextView;

public class SmartInputTextView extends AutoCompleteTextView {
    private int mSmartThreshold = 0;
    
	public SmartInputTextView(Context context) {
		super(context);
	}
	
    public SmartInputTextView(Context context, AttributeSet attrs) {
    	super(context, attrs);
    }

    public SmartInputTextView(Context context, AttributeSet attrs, int defStyle) {
    	super(context, attrs, defStyle);
    }
    
    private boolean mAutoCompleteEnable = true;
    public void setAutoComplete(boolean enable) {
    	mAutoCompleteEnable = enable;
    }
    
	@Override
	public void setThreshold(int threshold) {
        if (threshold < 0) {
            threshold = 0;
        }

        mSmartThreshold = threshold;
	}

	@Override
	public int getThreshold() {
		return mSmartThreshold;
	}

	@Override
	public boolean enoughToFilter() {
		return mAutoCompleteEnable ? getText().length() >= mSmartThreshold : false;
	}
	
}
