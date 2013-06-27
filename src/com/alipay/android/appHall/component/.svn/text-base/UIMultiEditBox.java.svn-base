package com.alipay.android.appHall.component;

import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import android.app.Activity;
import android.text.Editable;
import android.text.InputFilter;
import android.text.InputType;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.LinearLayout;

import com.alipay.android.appHall.Helper;
import com.alipay.android.appHall.uiengine.InputCheckListener;
import com.alipay.android.appHall.uiengine.UIInterface;
import com.alipay.android.client.RootActivity;
import com.alipay.android.client.util.BaseHelper;
import com.alipay.android.core.expapp.Page;
import com.eg.android.AlipayGphone.R;

public class UIMultiEditBox extends EditText implements UIInterface ,InputCheckListener{

    private int     charNum;
    
    private Page mPage;

    public UIMultiEditBox(Page page) {
        super(page.getRawContext());
        mPage = page;
    }

    public void init(ViewGroup parent) {
        this.setInputType(InputType.TYPE_TEXT_FLAG_MULTI_LINE | InputType.TYPE_CLASS_TEXT);
        EditText inputBox = (EditText) LayoutInflater.from(mPage.getRawContext()).inflate(
            R.layout.alipay_multiedittext, parent, false);
        LinearLayout.LayoutParams layoutParams = (LinearLayout.LayoutParams) inputBox
            .getLayoutParams();
        this.setLayoutParams(Helper.copyLinearLayoutParams(layoutParams));
        this.setPadding(inputBox.getPaddingLeft(), inputBox.getPaddingTop(),
            inputBox.getPaddingRight(), inputBox.getPaddingBottom());
        this.setSingleLine(false);
        this.setTextColor(inputBox.getTextColors());
        this.setTextSize(TypedValue.COMPLEX_UNIT_PX, inputBox.getTextSize());
        this.setLineSpacing(7, 1);
        this.setGravity(inputBox.getGravity());
        this.setBackgroundDrawable(inputBox.getBackground());
    }

    public int getCharNum() {
        return charNum;
    }

    public void setCharNum(int charNum) {
        this.charNum = charNum;
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
        }
        setText(value.toString().trim());
    }

    @Override
    public Object getValue() {
        // TODO Auto-generated method stub
        return getText().toString().trim();
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
        if (text == null) {
            return;
        }
        setText(text.trim());
    }

    @Override
    public String get_Text() {
        if (getText()==null) {
            return null;
        }
        return getText().toString().trim();
    }
    
    @Override
    public String get_Tag() {
        return (String) this.getTag();
    }

    @Override
    public void set_Tag(String tag) {
        this.setTag(tag);
    }
    
    private String emptyDesc;
    private String desc;
    private String regex;
    
    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }
    
    public String getEmptyDesc() {
        return emptyDesc;
    }

    public void setEmptyDesc(String emptyDesc) {
        this.emptyDesc = emptyDesc;
    }

    public String getRegex() {
        return regex;
    }

    public void setRegex(String regex) {
        this.regex = regex;
    }

    @Override
    public boolean inputCheck() {
        if (getVisibility()==ViewGroup.GONE || !getEnable()) {
            return true;
        }
        
        boolean bSatisfy = true;
        String value = getValue().toString();

        RootActivity activity = (RootActivity) mPage.getEngine().getContext();
        if (minCharNum >= 1 && value == null && emptyDesc!=null && emptyDesc != "") {
        	BaseHelper.recordWarningMsg(activity,emptyDesc);
        	
        	activity.getDataHelper().showDialog((Activity) mPage.getRawContext(), R.drawable.infoicon,
                getResources().getString(R.string.WarngingString), emptyDesc,
                getResources().getString(R.string.Ensure), null, null, null, null, null);
            return false;
        }

        // Verify the minCharNum first.
        if((value == null && minCharNum >0)||(value != null&& value.length() < minCharNum)){
            bSatisfy = false;
        }

        // Verify the regex.
        if( bSatisfy &&regex!=null&&regex.length()>0){
            try {
                Pattern pattern = Pattern.compile(regex);
                Matcher matcher = pattern.matcher(value);
                bSatisfy = matcher.matches();
            } catch (Exception e) {
                //e.printStackTrace();
            }
        }
            
        if( !bSatisfy ){
        	BaseHelper.recordWarningMsg(activity,desc);
        	
        	activity.getDataHelper().showDialog((Activity) mPage.getRawContext(), R.drawable.infoicon, getResources()
                .getString(R.string.WarngingString), desc, getResources()
                .getString(R.string.Ensure), null, null, null, null, null);           
        }

        return bSatisfy;
    }

    private int maxCharNum = -1;
    private int minCharNum = -1;

    @Override
    public void setMaxCharNum(String maxCharNum ) {
        this.maxCharNum = Integer.valueOf(maxCharNum);
        if (this.maxCharNum!=-1) {
            setFilters(new InputFilter[]{new InputFilter.LengthFilter(this.maxCharNum)});
        }
    }

    @Override
    public void setMinCharNum(String minCharNum) {
        this.minCharNum = Integer.valueOf(minCharNum);
    }

	public int getMinCharNum() {
		return minCharNum;
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
    

    @Override
    public boolean Inputed() {
        Editable editable = getText();
        if(editable!=null&&editable.toString().trim().length()>0)
            return true;
        return false;
    }
}
