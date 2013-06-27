/**
 * 
 */
package com.alipay.android.comon.component;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.TabHost;

/**
 * @author sanping.li
 *
 */
public class MidTabHost extends TabHost {
    private boolean mInit;

    public MidTabHost(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public MidTabHost(Context context) {
        super(context);
    }

    @Override
    public void setCurrentTab(int index) {
        if (mInit){
            mInit = false;
            return;
        }
        super.setCurrentTab(index);
    }

    @Override
    public void addTab(TabSpec tabSpec) {
        mInit = true;
        super.addTab(tabSpec);
    }

}
