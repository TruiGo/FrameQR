package com.alipay.android.appHall.component;

import java.util.HashMap;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.LinearLayout;

import com.alipay.android.appHall.CacheManager;
import com.alipay.android.appHall.Helper;
import com.alipay.android.appHall.uiengine.NeedSaveListener;
import com.alipay.android.appHall.uiengine.UIInterface;
import com.alipay.android.core.expapp.Page;
import com.eg.android.AlipayGphone.R;

public class UICheckBox extends CheckBox implements UIInterface,NeedSaveListener {

    private Page mPage;
    
    private String componentId;
    
    public UICheckBox(Page page) {
        super(page.getRawContext());
        mPage = page;
    }

    public void init(ViewGroup parent) {
        componentId = mPage.getEngine().getPkgId() + "."
                + mPage.getPageName() + "." + get_Id();
        
        CheckBox checkBox = (CheckBox) LayoutInflater.from(mPage.getRawContext()).inflate(
            R.layout.check_box, parent, false);

        LinearLayout.LayoutParams layoutParams = (LinearLayout.LayoutParams) checkBox.getLayoutParams();
        setLayoutParams(Helper.copyLinearLayoutParams(layoutParams));
        setChecked(false);
        setBackgroundDrawable(checkBox.getBackground());
        setTextSize(checkBox.getTextSize());
        setTextColor(getContext().getResources().getColor(R.color.TextColorBlack));
        setButtonDrawable(R.drawable.btn_check);
    }
    


    private String expression;

    public void setExpression(String expression) {
        this.expression = expression;
    }

    public String getExpression() {
        return expression;
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
        if (value.toString().equalsIgnoreCase("false")) {
            setChecked(false);
        } else if (value.toString().equalsIgnoreCase("true")) {
            setChecked(true);
        }
        Object id = getTag()==null?"":getTag();
        mPage.interpreter("checkBox::"+id.toString(),expression);
    }

    @Override
    public Object getValue() {
        // TODO Auto-generated method stub
        return isChecked() ? "true" : "false";
    }

    @Override
    public void setEnable(boolean enabled) {
        super.setEnabled(enabled);
    }

    @Override
    public boolean getEnable() {
        // TODO Auto-generated method stub
        return super.isEnabled();
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

    private boolean isSave;

    public void setIsSave(boolean isSave) {
        this.isSave = isSave;
    }

    public boolean getIsSave() {
        return isSave;
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
        setText(text);
    }

    @Override
    public String get_Text() {
        return (String) getText();
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
    public String getSelectedIndex() {
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
    public void doSave(CacheManager cm) {
        String value = this.isChecked()?"true":"false";
        cm.put(componentId, value);
    }

    @Override
    public void setDefault(CacheManager cm) {
        String value = CacheManager.getInstance(mPage.getRawContext()).get(componentId);
        if (value != null && value != "") {
           return;
        }
        setChecked(value.equals("true")?true:false);
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
