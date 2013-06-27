package com.alipay.android.appHall.component;

import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import android.app.Activity;
import android.text.Editable;
import android.text.InputFilter;
import android.text.TextWatcher;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.alipay.android.appHall.CacheManager;
import com.alipay.android.appHall.uiengine.InputCheckListener;
import com.alipay.android.appHall.uiengine.NeedSaveListener;
import com.alipay.android.appHall.uiengine.UIInterface;
import com.alipay.android.client.RootActivity;
import com.alipay.android.client.util.BaseHelper;
import com.alipay.android.core.expapp.Page;
import com.eg.android.AlipayGphone.R;

public class UIInputBox extends LinearLayout implements UIInterface, InputCheckListener,
                                        NeedSaveListener {

    public static final int UIINPUTBOX_TYPE_DIGIT = 0, UIINPUTBOX_TYPE_CHAR = 1,
            UIINPUTBOX_TYPE_PASSWORD = 2;
    private boolean isSave;
    private int type;
    private String expression;
    private String desc;
    private String emptyDesc;
    private String regex;
    private Page mPage;

    private String componentId;

    private RelativeLayout inputBox = null;
   
    //内容头部信息
  	private TextView mTvContentName;
  	private  EditText mEtContent;
  	//清除按钮图片
  	private ImageButton mClearButton;
  	//输入框单位
  	private TextView mTvUnit;

    // onInputFinish,用来做输入检验,这个如何体现

    public UIInputBox(Page page) {
        super(page.getRawContext());
        mPage = page;
    }
    
    public EditText getEditText(){
    	return mEtContent;
    }
    
    public ImageButton getClearButton(){
    	return mClearButton;
    }

    // layout_height和singleLine这两个属性无法从xml文件中获取
    public void init(ViewGroup parent) {
        componentId = mPage.getEngine().getPkgId() + "." + mPage.getPageName() + "." + get_Id();

        inputBox = (RelativeLayout) LayoutInflater.from(mPage.getRawContext()).inflate(
            R.layout.edittext_with_clearbutton, parent, false);
        
        mTvContentName = (TextView) inputBox.findViewById(R.id.contentName);
        mEtContent = (EditText) inputBox.findViewById(R.id.content);
        mClearButton = (ImageButton) inputBox.findViewById(R.id.clearButton);
        mTvUnit = (TextView) inputBox.findViewById(R.id.unit);
        
        addListener();
       
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(LayoutParams.FILL_PARENT,LayoutParams.WRAP_CONTENT);
        layoutParams.leftMargin = 0;
        this.setLayoutParams(layoutParams);
        
        this.addView(inputBox);
        
        this.setPadding(0, inputBox.getPaddingTop(),
                0, inputBox.getPaddingBottom());
        
//        this.setSingleLine(true);
//        this.setTextColor(inputBox.getTextColors());

        this.mEtContent.setTextSize(TypedValue.COMPLEX_UNIT_PX, mEtContent.getTextSize());
//        this.setBackgroundDrawable(inputBox.getBackground());
    }
    
     /**
     * add listener for commponent
     */
	private void addListener() {
		mEtContent.addTextChangedListener(new TextWatcher() {
			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {
			}
			
			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {
			}
			
			@Override
			public void afterTextChanged(Editable s) {
				 if (s.length() == 0) {  
					 setButtonVisiable(false);// 隐藏按钮   
	             } else if(getEnable()){  
	            	 setButtonVisiable(true);// 显示按钮   
	             }  
			}
		});
        
        mClearButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				mEtContent.setText("");
				setButtonVisiable(false);
			}
		});
	}
    
    public void setInputName(String contentNameString){
    	if(contentNameString != null && !"".equals(contentNameString)){
    		mTvContentName.setVisibility(View.VISIBLE);
    		mTvContentName.setText(contentNameString);
    	}else
    		mTvContentName.setVisibility(View.GONE);
    }
    
    public void setButtonVisiable(boolean visiable){
    	mClearButton.setVisibility(visiable ? View.VISIBLE : View.GONE);
    }
    
    public void setUnit(String unitString){
    	if(unitString != null && !"".equals(unitString)){
    		mTvUnit.setVisibility(View.VISIBLE);
    		mTvUnit.setText(unitString);
    	}else
    		mTvUnit.setVisibility(View.GONE);
    }

    public void setIsSave(boolean isSave) {
        this.isSave = isSave;
    }

    public boolean getIsSave() {
        return isSave;
    }

    public void setUIInputBoxType(int type) {
        this.type = type;
    }

    public int getUIInputBoxType() {
        return type;
    }

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
        if (value == null) {
            return;
        }
        mEtContent.setText(value.toString().trim());
    }

    @Override
    public Object getValue() {
        // TODO Auto-generated method stub
        if (mEtContent.getText()==null) {
            return null;
        }
        return mEtContent.getText().toString().trim();
    }

    @Override
    public void setEnable(boolean enabled) {
        mEtContent.setEnabled(enabled);
        mEtContent.setFocusable(enabled);
        if(!enabled)
            setButtonVisiable(false);
    }

    @Override
    public boolean getEnable() {
        return mEtContent.isEnabled();
    }

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
    public String getSelectedIndex() {
        return null;
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
        setLayoutParams(params);
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
        mEtContent.setText(text.trim());
    }

    @Override
    public String get_Text() {
        return mEtContent.getText().toString().trim();
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
    public boolean inputCheck() {
        if (getVisibility()==ViewGroup.GONE || !getEnable()) {
            return true;
        }
        
        if (regex == null || regex.length() <= 0)
            return true;

        String value = (String) getValue();

        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(value);

        RootActivity activity = (RootActivity) mPage.getEngine().getContext();
        if (minCharNum >= 1 && value.length() <= 0 && emptyDesc!=null && emptyDesc != "") {
        	BaseHelper.recordWarningMsg(activity,emptyDesc);
        	
        	activity.getDataHelper().showDialog((Activity)getContext(), R.drawable.infoicon,
                getResources().getString(R.string.WarngingString), emptyDesc,
                getResources().getString(R.string.Ensure), null, null, null, null, null);
            return false;
        }

        if ((value != null && minCharNum != -1 && value.length() < minCharNum)
            || (!matcher.matches() && desc != null && desc != "")) {
        	BaseHelper.recordWarningMsg(activity,desc);
        	
        	activity.getDataHelper().showDialog((Activity)getContext(), R.drawable.infoicon,
                getResources().getString(R.string.WarngingString), desc,
                getResources().getString(R.string.Ensure), null, null, null, null, null);
            return false;
        }
        return true;
    }

    @Override
    public void doSave(CacheManager cm) {
        cm.put(componentId, mEtContent.getText().toString());
    }

    @Override
    public void setDefault(CacheManager cm) {
        String value = cm.get(componentId);
        if (value != null && value != "") {
        	mEtContent.setText(value);
        }
    }

    private int maxCharNum = -1;
    private int minCharNum = -1;

    @Override
    public void setMaxCharNum(String maxCharNum) {
        this.maxCharNum = Integer.valueOf(maxCharNum);
        if (this.maxCharNum != -1) {
        	mEtContent.setFilters(new InputFilter[] { new InputFilter.LengthFilter(this.maxCharNum) });
        }
    }

    @Override
    public void setMinCharNum(String minCharNum) {
        this.minCharNum = Integer.valueOf(minCharNum);
    }
    
    public int getMinCharNum(){
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
        Editable editable = mEtContent.getText();
        if(editable!=null&&editable.toString().trim().length()>0)
            return true;
        return false;
    }
}
