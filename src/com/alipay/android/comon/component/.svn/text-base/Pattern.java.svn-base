package com.alipay.android.comon.component;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewStub;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.alipay.android.client.constant.Constant;
import com.alipay.android.security.Des;
import com.eg.android.AlipayGphone.R;

public class Pattern extends RelativeLayout {

    private View mPatternCheckView = null;
    private View mPatternSettingView = null;
    private LockView mLockView = null;
    private String mUserPattern = null;
    private TextView mTopTextView = null;
    private TextView mBottomTextView = null;
    
	protected OnPatternCheckedListener mPatternCheckedListener;
	protected OnPatternChangeListener mPatternChangeListener;
	public Pattern(Context context) {
		super(context);
		// TODO Auto-generated constructor stub
	}
    public Pattern(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public Pattern(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);

        init();
    }

    private void init() {
		LayoutInflater.from(this.getContext()).inflate(R.layout.alipay_pattern, this, true);
		mLockView = (LockView) this.findViewById(R.id.lockView);
		mTopTextView = (TextView) this
				.findViewById(R.id.patternTopDescription);
		mBottomTextView = (TextView) this
				.findViewById(R.id.patternBottomDescription);
    }
    
    public void checkPattern(final String patternPassword) {
    	if (null == mPatternCheckView) {
    		mPatternCheckView = ((ViewStub)this.findViewById(R.id.patternCheck)).inflate();
    	}
    	else if (View.GONE == mPatternCheckView.getVisibility()) {
    		mPatternCheckView.setVisibility(View.VISIBLE);
    	}
    	
    	if (null != mPatternSettingView && View.VISIBLE == mPatternSettingView.getVisibility()) {
    		mPatternSettingView.setVisibility(View.GONE);
    	}
    	
    	if (null != mLockView) {
    		mLockView.setOnLockInputListener(new LockView.OnLockInputListener() {
				
				@Override
				public void onLockInput(int indexUnlocked) {
					// TODO Auto-generated method stub
					
				}
				
				@Override
				public void onLockDone(String path) {
					//验证手势密码
					if (path.length() >= LockView.MINSELECTED) {
						String pathDeString = Des.decrypt(path, Constant.ALIPAY_INFO);
						if (pathDeString.equals(patternPassword)) {
							if (null != mPatternCheckedListener) {
								mPatternCheckedListener.onPatternChecked(false, true);
							}
						}
					}
					else {
						//TODO:手势密码不足4位
						if (null != mTopTextView) {
							mTopTextView.setText(R.string.pattern_passwordTooShort);
						}
						
						mLockView.clear();
					}
				}
			});
    	}
    	
    	if (null != mBottomTextView) {
    		mBottomTextView.setOnClickListener(new View.OnClickListener() {
				
				@Override
				public void onClick(View v) {
					if (null != mPatternCheckedListener) {
						mPatternCheckedListener.onPatternChecked(true, false);
					}
				}
			});
    	}
    }
    
    public void TryToSetPattern(boolean skipable) {
    	if (null == mPatternSettingView) {
    		mPatternSettingView = ((ViewStub)this.findViewById(R.id.patternSetting)).inflate();
    	}
    	else if (View.GONE == mPatternSettingView.getVisibility()) {
    		mPatternSettingView.setVisibility(View.VISIBLE);
    	}
    	
    	if (null != mPatternCheckView && View.VISIBLE == mPatternCheckView.getVisibility()) {
    		mPatternCheckView.setVisibility(View.GONE);
    	}
    	
    	ImageButton skipButton = (ImageButton) this
				.findViewById(R.id.title_right);
		if (null != skipButton && null != mPatternChangeListener) {
			skipButton.setOnClickListener(new View.OnClickListener() {

				@Override
				public void onClick(View v) {
					if (null != mPatternChangeListener) {
						mPatternChangeListener.onPatternChange(true, null);
					}
				}
			});
		}
		
    	if (null != mLockView) {
    		final LockIndicator lockIndicator = ((LockIndicator)this.findViewById(R.id.lockIndicator));
    		
    		mLockView.setOnLockInputListener(new LockView.OnLockInputListener() {
				
				@Override
				public void onLockInput(int indexUnlocked) {
					// TODO Auto-generated method stub
					
				}
				
				@Override
				public void onLockDone(String path) {
					if (path.length() >= LockView.MINSELECTED) {
						if (null == mUserPattern) {
							mUserPattern = path;
							if (null != lockIndicator) {
								lockIndicator.setPath(path);
							}
							mLockView.clear();
						}
						else {
							if (mUserPattern.equals(path)) {
							}
							else {
								//TODO:前后两次不一致
								if (null != mTopTextView) {
									mTopTextView.setText(R.string.pattern_passwordDifferent);
								}
							}
						}
						
					}
					else {
						//TODO:手势密码不足4位
						if (null != mTopTextView) {
							mTopTextView.setText(R.string.pattern_passwordTooShort);
						}
						mLockView.clear();
					}
				}
			});
    	}
		
    	if (null != mBottomTextView) {
    		mBottomTextView.setVisibility(View.GONE);
    	}
    }
    
    public void setOnPatternCheckedListener(OnPatternCheckedListener l) {
    	mPatternCheckedListener = l;
    }
    
    public void setOnPatternChangeListener(OnPatternChangeListener l) {
    	mPatternChangeListener = l;
    }
    
    public interface OnPatternCheckedListener {
    	void onPatternChecked(boolean forgetPassword, boolean patternIsRight);
    }
    
    public interface OnPatternChangeListener {
        void onPatternChange(boolean skip, String newPattern);
    }
}
