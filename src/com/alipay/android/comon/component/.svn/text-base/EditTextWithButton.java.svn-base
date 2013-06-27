package com.alipay.android.comon.component;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.text.Editable;
import android.text.InputFilter;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;

import com.eg.android.AlipayGphone.R;

public class EditTextWithButton extends RelativeLayout{

	private EditText mInputContent;
	private ImageButton mClearButton;
	private ImageButton mFuncButton;
	private TextView mInputName;
	
	private String inputName;
	private float inputNameTextSize;
	private float inputTextSize;
	private int inputTextColor;
	private int inputType;
	private int id;
	private Drawable buttonBackground;
	private int maxlength;
	private String hintString;
	private int hintTextColor;
	private boolean buttonVisiable;
	private boolean isAlipayMoney;
	
	public EditTextWithButton(Context context){
		super(context);
	}
	
	public EditTextWithButton(Context context, AttributeSet set) {
		super(context, set);
		LayoutInflater.from(context).inflate(R.layout.common_edittext_with_button, this, true);
		
		TypedArray a = context.obtainStyledAttributes(set, R.styleable.EditTextWithButton);
		inputName = a.getString(R.styleable.EditTextWithButton_inputName);
		inputNameTextSize = a.getFloat(R.styleable.EditTextWithButton_inputNameTextSize, 16);
		inputTextSize = a.getFloat(R.styleable.EditTextWithButton_inputNameTextSize, 16);
		inputTextColor = a.getColor(R.styleable.EditTextWithButton_inputTextColor, Color.BLACK);
		inputType = a.getInt(R.styleable.EditTextWithButton_inputType, 0x1);
		maxlength = a.getInt(R.styleable.EditTextWithButton_maxLength, -1);
		id = a.getInt(R.styleable.EditTextWithButton_inputId,0);
		hintString = a.getString(R.styleable.EditTextWithButton_inputHint);
		hintTextColor = a.getColor(R.styleable.EditTextWithButton_inputHintTextColor, 
				getResources().getColor(R.color.TextColorBlack));
		
		buttonBackground = a.getDrawable(R.styleable.EditTextWithButton_buttonBackgound);
		buttonVisiable = a.getBoolean(R.styleable.EditTextWithButton_buttonVisiable, false);
		isAlipayMoney = a.getBoolean(R.styleable.EditTextWithButton_isAlipayMoney, false);
		
		a.recycle();
	}
	
	@Override
	protected void onFinishInflate() {
		super.onFinishInflate();
		mInputContent  = (EditText) findViewById(R.id.content);
		mInputName = (TextView) findViewById(R.id.contentName);
		mClearButton= (ImageButton) findViewById(R.id.clearButton);
		mFuncButton = (ImageButton) findViewById(R.id.funcButton);
		setInputName(inputName);
		setInputNameTextSize(inputNameTextSize);
		setInputTextSize(inputTextSize);
		setInputTextColor(inputTextColor);
		setInputId(id);
		setInputType(inputType);
		setHint(hintString);
		setHintTextColor(hintTextColor);
		setLength(maxlength);
		addListener();
		setButtonVisiable(buttonVisiable);
		setFuncButtonBg(buttonBackground);
	}
	
    public void setOnEditorActionListener(OnEditorActionListener l) {
    	if (null != mInputContent) {
    		mInputContent.setOnEditorActionListener(l);
    	}
    }
    
    public void addTextChangedListener(TextWatcher watcher) {
    	if (null != mInputContent) {
    		mInputContent.addTextChangedListener(watcher);
    	}
    }
    
    public void setOnFocusChangeListener(OnFocusChangeListener l) {
    	if (null != mInputContent) {
    		mInputContent.setOnFocusChangeListener(l);
    	}
    }
    
	/**
	 * add listener for component
	 */
	private void addListener() {
		mInputContent.addTextChangedListener(new TextWatcher() {
			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {
			}
			
			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {
			}
			
			@Override
			public void afterTextChanged(Editable inputedStr) {
				if (inputedStr.length() == 0) {
					setButtonVisiable(false);// 隐藏按钮
				} else {
					setButtonVisiable(true);// 显示按钮
				}

				if(isAlipayMoney){
					String temp = inputedStr.toString();
					int posDot = temp.indexOf(".");
					if (posDot == -1)
						return;
	
					if (temp.length() - posDot - 1 > 2) {
						inputedStr.delete(posDot + 3, posDot + 4);
					}
				}
			}
		});

		mClearButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				mInputContent.setText("");
				mClearButton.setVisibility(View.GONE);
			}
		});
	}
	
	public void setButtonVisiable(boolean visiable){
		mClearButton.setVisibility(visiable ? View.VISIBLE : View.GONE);
		if(buttonBackground != null){
			mFuncButton.setVisibility(visiable ? View.GONE : View.VISIBLE);
		}
	}
	
	public void setText(CharSequence inputContent){
		mInputContent.setText(inputContent);
	}
	
	public String getText(){
		return mInputContent.getText().toString();
	}
	
	public EditText getEtContent(){
		return mInputContent;
	}
	
	public void setInputName(String inputName){
		if(inputName != null && !"".equals(inputName)){
			mInputName.setText(inputName);
			mInputName.setVisibility(View.VISIBLE);
		}else{
			mInputName.setText("");
			mInputName.setVisibility(View.GONE);
		}
	}
	
	public TextView getInputName(){
		return mInputName;
	}
	
	public void setInputNameTextSize(float textSize){
		if(textSize > 0){
			mInputName.setTextSize(textSize);
		}
	}
	
	public void setInputTextSize(float textSize){
		if(textSize > 0){
			mInputContent.setTextSize(textSize);
		}
	}
	
	public void setInputTextColor(int textColor){
		mInputContent.setTextColor(textColor);
	}
	
	public void setInputType(int inputType){
		mInputContent.setInputType(inputType);
	}
	
	public void setInputId(int id){
		if(id != 0)
			mInputContent.setId(id);
	}
	
	public void setHint(String hintString){
		if(hintString !=null && !"".equals(hintString))
			mInputContent.setHint(hintString);
	}
	
	public void setHintTextColor(int textColor){
		mInputContent.setHintTextColor(textColor);
	}
	
	public void setLength(int maxlength){
		if (maxlength >= 0) {
			mInputContent.setFilters(new InputFilter[] { new InputFilter.LengthFilter(maxlength) });
        } else {
        	mInputContent.setFilters(new InputFilter[0]);
        }
	}
	
	public void setFuncButtonBg(Drawable buttonBackground) {
		if(buttonBackground != null){
			this.buttonBackground = buttonBackground;
			setFuncButtonVisiable(true);
			mFuncButton.setBackgroundDrawable(buttonBackground);
		}
	}
	
	public ImageButton getFuncButton(){
		return mFuncButton;
	}
	
	public ImageButton getClearButton(){
		return mClearButton;
	}
	
	public void setFuncButtonVisiable(boolean visiable){
		if(buttonBackground != null){
			mFuncButton.setVisibility(visiable ? View.VISIBLE : View.GONE);
		}
		mClearButton.setVisibility(visiable ? View.GONE : View.VISIBLE);
	}
}
