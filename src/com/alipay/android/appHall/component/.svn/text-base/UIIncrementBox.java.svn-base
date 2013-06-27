package com.alipay.android.appHall.component;

import java.util.HashMap;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.alipay.android.appHall.Helper;
import com.alipay.android.appHall.component.incrementitem.GetMoneyDataStructure;
import com.alipay.android.appHall.component.incrementitem.UtilGetMoneyIncreamItem;
import com.alipay.android.appHall.uiengine.InputCheckListener;
import com.alipay.android.appHall.uiengine.UIInterface;
import com.alipay.android.core.expapp.Page;
import com.eg.android.AlipayGphone.R;

public class UIIncrementBox extends LinearLayout implements UIInterface , InputCheckListener{

    public static final int ITEMTYPE_GETMONEY    = 1;
    public static final int ITEMTYPE_ANOTHER     = 2;

    private Page mPage;

    private LinearLayout    fakeRootLinearLayout = null;

    public UIIncrementBox(Page page) {
        super(page.getRawContext());
        mPage = page;
    }

    public void init(ViewGroup parent) {
    }

    //传进去item类型和需要传入的数据的封装对象
    public void init(ViewGroup parent, int itemType, Object dataStructure) {
        LinearLayout layout = (LinearLayout) LayoutInflater.from(mPage.getRawContext()).inflate(
        R.layout.uiincream, parent, false);

        this.addView(layout,Helper.copyLinearLayoutParams((LayoutParams) layout.getLayoutParams()));
        fakeRootLinearLayout = (LinearLayout) layout.findViewById(R.id.fakeRootLinearLayout);
        loadIncreamItemsLayout(itemType, dataStructure);
        loadIncreamControlLayout(itemType);
    }

    private UtilGetMoneyIncreamItem utilGetMoneyItem;

    private void loadIncreamItemsLayout(int itemType, Object dataStructure) {
        switch (itemType) {
            case UIIncrementBox.ITEMTYPE_GETMONEY:
                utilGetMoneyItem = new UtilGetMoneyIncreamItem(mPage, this, fakeRootLinearLayout,
                    (GetMoneyDataStructure) dataStructure);
                utilGetMoneyItem.loadIncreamItemsData();
                break;
            case UIIncrementBox.ITEMTYPE_ANOTHER:

                break;
            default:
                break;
        }
    }

    private void loadIncreamControlLayout(int itemType) {
        switch (itemType) {
            case UIIncrementBox.ITEMTYPE_GETMONEY:
                utilGetMoneyItem.loadIncreamControlData();
                break;
            case UIIncrementBox.ITEMTYPE_ANOTHER:

                break;
            default:
                break;
        }
    }

    // ----------------------------设置宽度
    private boolean isChange = true;
    private int     width    = ViewGroup.LayoutParams.FILL_PARENT;

    public void set_Width(int width) {
        isChange = true;
        this.width = width;
        changedWidth();
    }

    private void changedWidth() {
        ViewGroup.LayoutParams params = (ViewGroup.LayoutParams) getLayoutParams();
        params.width = this.width;
        setLayoutParams(params);
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
        return utilGetMoneyItem.getValue();
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
        LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) findViewById(R.id.uiincream)
            .getLayoutParams();
        params.leftMargin = marginLeft;
        findViewById(R.id.uiincream).setLayoutParams(params);
    }

    @Override
    public int get_MarginLeft() {
        LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) findViewById(R.id.uiincream)
            .getLayoutParams();
        return params.leftMargin;
    }

    @Override
    public void set_MarginRight(int marginRight) {
        LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) findViewById(R.id.uiincream)
            .getLayoutParams();
        params.rightMargin = marginRight;
        findViewById(R.id.uiincream).setLayoutParams(params);
    }

    @Override
    public int get_MarginRight() {
        LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) findViewById(R.id.uiincream)
            .getLayoutParams();
        return params.rightMargin;
    }
    
    @Override
    public void set_MarginTop(int marginTop) {
        LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) findViewById(
            R.id.uiincream).getLayoutParams();
        params.topMargin = marginTop;
        findViewById(R.id.uiincream).setLayoutParams(params);
    }

    @Override
    public int get_MarginTop() {
        LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) findViewById(
            R.id.uiincream).getLayoutParams();
        return params.topMargin;
    }
    
    @Override
    public void set_MarginBottom(int marginBottom) {
        LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) findViewById(
            R.id.uiincream).getLayoutParams();
        params.bottomMargin = marginBottom;
        findViewById(R.id.uiincream).setLayoutParams(params);
    }

    @Override
    public int get_MarginBottom() {
        LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) findViewById(
            R.id.uiincream).getLayoutParams();
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
        return (LinearLayout.LayoutParams) findViewById(R.id.uiincream)
                .getLayoutParams();
    }

    @Override
    public boolean inputCheck() {
        return utilGetMoneyItem.validateCheck();
    }

    @Override
    public void setMaxCharNum(String maxCharNum) {
        // TODO Auto-generated method stub
        
    }

    @Override
    public void setMinCharNum(String minCharNum) {
        // TODO Auto-generated method stub
        
    }

    @Override
    public String getDesc() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public void setDesc(String desc) {
        // TODO Auto-generated method stub
        
    }

    @Override
    public String getRegex() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public void setRegex(String regex) {
        // TODO Auto-generated method stub
        
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
    

    @Override
    public boolean Inputed() {
        return utilGetMoneyItem.inputed();
    }
}
