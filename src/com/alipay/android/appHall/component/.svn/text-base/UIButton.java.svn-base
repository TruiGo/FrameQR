package com.alipay.android.appHall.component;

import java.util.HashMap;

import android.content.Context;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;

import com.alipay.android.appHall.Helper;
import com.alipay.android.appHall.uiengine.UIInterface;
import com.alipay.android.appHall.uiengine.UIMonitorListener;
import com.eg.android.AlipayGphone.R;

public class UIButton extends Button implements UIInterface,UIMonitorListener {

	public static final String BUTTON_TYPE_MAIN = "main";
	public static final String BUTTON_TYPE_SUB = "sub";
	private Context context;
	
	public UIButton(Context context) {
		super(context);
		this.context = context;
	}

	public void init(ViewGroup parent, String buttonClass) {
		Button button;
		if (buttonClass.equals(BUTTON_TYPE_MAIN)) {
			button = (Button) LayoutInflater.from(this.context).inflate(
					R.layout.button_main, parent, false);
		} else if(buttonClass.equals(BUTTON_TYPE_SUB)){
			button = (Button) LayoutInflater.from(this.context).inflate(
					R.layout.button_sub, parent, false);
		}else{
            button = (Button) LayoutInflater.from(this.context).inflate(
                R.layout.button_small, parent, false);
		}

		LinearLayout.LayoutParams layoutParams = (LinearLayout.LayoutParams) button
				.getLayoutParams();
		this.setLayoutParams(Helper.copyLinearLayoutParams(layoutParams));
		this.setBackgroundDrawable(button.getBackground());
		this.setPadding(button.getPaddingLeft(), button.getPaddingTop(),
				button.getPaddingRight(), button.getPaddingBottom());
        this.setTextSize(TypedValue.COMPLEX_UNIT_PX, button.getTextSize());
        this.setTextColor(button.getTextColors());
	}
	
	public void init(ViewGroup parent) {

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

	// true为visible，false为gone
	public boolean getVisible() {
		return getVisibility() == View.VISIBLE;
	}

	@Override
	public void setValue(Object value) {
	    set_Text(value.toString());
	}

	@Override
	public Object getValue() {

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
        LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) getLayoutParams();
        return params.width;
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
        return (String)getText();
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
    
    private String buttonType;

    public String getButtonType() {
        return buttonType;
    }

    public void setButtonType(String buttonType) {
        if("submit".equals(buttonType)){
        	setEnable(false);
        }
        this.buttonType = buttonType;
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
    
    private String monitor = "";

	@Override
	public String getMonitor() {
		// TODO Auto-generated method stub
		return monitor;
	}

	@Override
	public void setMonitor(String monitor) {
		// TODO Auto-generated method stub
		this.monitor = monitor;
	}

	@Override
	public void doStorage() {
		// TODO Auto-generated method stub
		
	}
}
