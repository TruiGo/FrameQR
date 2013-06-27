package com.alipay.android.appHall.component;

import java.util.HashMap;

import android.content.Context;
import android.text.TextUtils;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.TextView;

import com.alipay.android.appHall.Helper;
import com.alipay.android.appHall.uiengine.UIInterface;
import com.eg.android.AlipayGphone.R;

public class UILabel extends TextView implements UIInterface {
    private Context         context;

    public UILabel(Context context) {
        super(context);
        this.context = context;
    }

    public void init(ViewGroup parent) {
        TextView textView = (TextView) LayoutInflater.from(this.context).inflate(R.layout.label,
            parent, false);

        LinearLayout.LayoutParams layoutParams = (LayoutParams) textView.getLayoutParams();
        this.setLayoutParams(Helper.copyLinearLayoutParams(layoutParams));
        this.setGravity(Gravity.LEFT);
        this.setBackgroundDrawable(textView.getBackground());
        this.setTextSize(TypedValue.COMPLEX_UNIT_DIP, textView.getTextSize());
        this.setTextColor(textView.getTextColors());
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
        setText(value.toString());
    }

    @Override
    public Object getValue() {
        // TODO Auto-generated method stub
        return getText();
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
        setText(text);
    }

    @Override
    public String get_Text() {
        return (String) getText();
    }

    @Override
    public String get_Tag() {
        return (String) this.getTag();
    }

    @Override
    public void set_Tag(String tag) {
        this.setTag(tag);
    }
    
    public void set_Lines(int lines){
        if(lines>0){
            setLines(lines);
            this.setEllipsize(TextUtils.TruncateAt.valueOf("END"));
        }
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
