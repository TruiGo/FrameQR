/**
 * 
 */
package com.alipay.android.appHall.component;

import java.util.HashMap;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ScrollView;

import com.alipay.android.appHall.uiengine.UIInterface;
import com.alipay.android.core.expapp.Page;
import com.eg.android.AlipayGphone.R;

/**
 * @author sanping.li
 *
 */
public class UIGroup extends LinearLayout implements UIInterface {
    private Page mPage;
    private boolean mScroll;
    private ViewGroup group;

    public UIGroup(Page Page) {
        super(Page.getRawContext());
        mPage = Page;
        this.setOrientation(VERTICAL);
    }
    
    public void init(ViewGroup parent,int style,boolean scroll) {
        mScroll = scroll;
        
        if(scroll){
            ScrollView scrollView = (ScrollView) LayoutInflater.from(mPage.getRawContext()).inflate(R.layout.uiscrollgroup, parent, false);
            mPage.setScrollBarEnabled(false);
            group = scrollView;
        }else{
            LinearLayout layout = new LinearLayout(getContext());
            layout.setOrientation(LinearLayout.VERTICAL);
            group = layout;
        }
        
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LayoutParams.FILL_PARENT,LayoutParams.WRAP_CONTENT);
        super.addView(group,params);
        
        switch(style){
            case 1:
                group.setBackgroundResource(R.drawable.group_bg1);
                break;
			case 2:
                group.setBackgroundResource(R.drawable.paipai_donate_love_background);
                break;
			case 3:
                group.setBackgroundResource(R.drawable.agent_pay_pop_top);
                break;
        }
    }
    
    @Override
    public void addView(View child, android.view.ViewGroup.LayoutParams params) {
        if(mScroll){
            LinearLayout linearLayout = (LinearLayout) group.findViewById(R.id.content);
            linearLayout.addView(child, params);
        }else
            group.addView(child, params);
    }

    @Override
    public void addView(View child) {
        if(mScroll){
            LinearLayout linearLayout = (LinearLayout) group.findViewById(R.id.content);
            linearLayout.addView(child);
        }else
            group.addView(child);
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
    private int     width    = ViewGroup.LayoutParams.FILL_PARENT;

    public void set_Width(int width) {
        isChange = true;
        this.width = ViewGroup.LayoutParams.FILL_PARENT;
        changedWidth();
    }

    private void changedWidth() {
        ViewGroup.LayoutParams params = (ViewGroup.LayoutParams) group.getLayoutParams();
        params.width = this.width;
        group.setLayoutParams(params);
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
        LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) group.getLayoutParams();
        params.leftMargin = marginLeft;
    }

    @Override
    public int get_MarginLeft() {
        LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) group.getLayoutParams();
        return params.leftMargin;
    }

    @Override
    public void set_MarginRight(int marginRight) {
        LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) group.getLayoutParams();
        params.rightMargin = marginRight;
    }

    @Override
    public int get_MarginRight() {
        LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) group.getLayoutParams();
        return params.rightMargin;
    }
    
    @Override
    public void set_MarginTop(int marginTop) {
        LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) group.getLayoutParams();
        params.topMargin = marginTop;
    }

    @Override
    public int get_MarginTop() {
        LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) group.getLayoutParams();
        return params.topMargin;
    }
    
    @Override
    public void set_MarginBottom(int marginBottom) {
        LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) group.getLayoutParams();
        params.bottomMargin = marginBottom;
    }

    @Override
    public int get_MarginBottom() {
        LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) group.getLayoutParams();
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
        return (android.widget.LinearLayout.LayoutParams) group.getLayoutParams();
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
