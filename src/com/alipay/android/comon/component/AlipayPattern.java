package com.alipay.android.comon.component;

import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.text.TextPaint;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewStub;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.alipay.android.client.RootActivity;
import com.eg.android.AlipayGphone.R;

public class AlipayPattern extends RelativeLayout {

    private View mPatternCheckView = null;
    private View mPatternSettingView = null;
    private LockView mLockView = null;
    private String mUserPattern = null;
    private TextView mTopTextView = null;
    private TextView mBottomTextView = null;
    private int mMaxPatternCheckTime = 5;
    private int mPatternWrongCount = 0;
    
	protected OnPatternCheckedListener mPatternCheckedListener;
	protected OnPatternChangeListener mPatternChangeListener;
	public AlipayPattern(Context context) {
		super(context);
		// TODO Auto-generated constructor stub
	}
    public AlipayPattern(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public AlipayPattern(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);

        init();
    }

    @Override
	public boolean onTouchEvent(MotionEvent event) {
		// TODO Auto-generated method stub
		super.onTouchEvent(event);
		//防止手势界面以下的其他输入框响应
		return true;
	}
    
	private void init() {
		LayoutInflater.from(this.getContext()).inflate(R.layout.alipay_pattern, this, true);
		mLockView = (LockView) this.findViewById(R.id.lockView);
		
		mTopTextView = (TextView) this
				.findViewById(R.id.patternTopDescription);
//		if (null != mTopTextView) {
//			TextPaint textPaint = mTopTextView.getPaint();
//			textPaint.setFakeBoldText(true);
//		}
		
		mBottomTextView = (TextView) this
				.findViewById(R.id.patternBottomDescription);
//		if (null != mTopTextView) {
//			TextPaint textPaint = mTopTextView.getPaint();
//			textPaint.setFakeBoldText(true);
//		}
    }
    
    public void checkPattern(final String userAccount, final String realName,
    		final String patternPassword, final Bitmap userAvatarBitmap) {
    	if (null == patternPassword || "".equals(patternPassword)) {
    		return ;
    	}
    	
    	View patternLayout = this.findViewById(R.id.patternLayout);
    	patternLayout.setBackgroundResource(R.drawable.application_bg_patterncheck);
    	
    	mPatternWrongCount = 0;
    	
    	if (null == mPatternCheckView) {
    		mPatternCheckView = ((ViewStub)this.findViewById(R.id.patternCheck)).inflate();
    	}
    	else if (View.GONE == mPatternCheckView.getVisibility()) {
    		mPatternCheckView.setVisibility(View.VISIBLE);
    	}
    	
    	TextView userNameView = (TextView)mPatternCheckView.findViewById(R.id.userName);
    	if (null != userNameView) {
    		userNameView.setText(realName);
    		TextPaint paint = userNameView.getPaint();  
    		paint.setFakeBoldText(true);  
    	}
    	
    	if (null != userAvatarBitmap) {
    		ImageView userAvatarView = (ImageView)mPatternCheckView.findViewById(R.id.userAvatar);
    		userAvatarView.setBackgroundResource(R.drawable.default_avatar_ouline);
    		userAvatarView.setImageBitmap(userAvatarBitmap);
    	}
    	
    	TextView userAccountView = (TextView)mPatternCheckView.findViewById(R.id.userLoginAccount);
    	if (null != userAccountView) {
    		userAccountView.setText(userAccount);
    	}
    	
    	if (null != mPatternSettingView && View.VISIBLE == mPatternSettingView.getVisibility()) {
    		mPatternSettingView.setVisibility(View.GONE);
    	}
    	
    	if (null != mLockView) {
    		
			if (null != mTopTextView) {
				mTopTextView.setTextColor(this.getResources().getColor(R.color.patterntop_color));
				mTopTextView.setText(R.string.pattern_pleaseIntputPattern);
			}
			
			if (null != mBottomTextView) {
				mBottomTextView.setText(R.string.pattern_forgetPattern);
	    		mBottomTextView.setOnClickListener(new View.OnClickListener() {
					
					@Override
					public void onClick(View v) {
						if (null != mPatternCheckedListener) {
							mPatternCheckedListener.onPatternChecked(true, false);
						}
					}
				});
			}
			
			mLockView.clear();
    		mLockView.setOnLockInputListener(new LockView.OnLockInputListener() {
				
				@Override
				public void onLockInput(int indexUnlocked) {
					// TODO Auto-generated method stub
					
				}
				
				@Override
				public void onLockDone(String path) {
					//验证手势密码
					if (path.length() >= LockView.MINSELECTED) {
						String pathDeString = path;//Des.decrypt(path, Constant.ALIPAY_INFO);
						if (pathDeString.equals(patternPassword)) {
							if (null != mPatternCheckedListener) {
								mPatternWrongCount = 0;
								mPatternCheckedListener.onPatternChecked(false, true);
								return ;
							}
						}
					}
					
					//TODO:手势密码错误
					mPatternWrongCount++;
					if (mPatternWrongCount == mMaxPatternCheckTime) {
						mPatternWrongCount = 0;
						Context context = AlipayPattern.this.getContext();
						if (context instanceof RootActivity) {
							RootActivity activity = (RootActivity)context;
							activity.getDataHelper().showDialog(activity, R.drawable.erroricon, getResources().getString(R.string.WarngingString), 
									getResources().getString(R.string.patterResetMessage),
									getResources().getString(R.string.Ensure), new DialogInterface.OnClickListener() {
										@Override
										public void onClick(DialogInterface dialog, int which) {
											mPatternCheckedListener.onPatternChecked(false, false);
											}
										},
										null, null, null, null);
						}
						else {
							mPatternCheckedListener.onPatternChecked(false, false);
						}
						
						return ;
					}
					
					if (null != mTopTextView) {
						String patternWrongAlert = null;
						if (-1 == mMaxPatternCheckTime) {
							patternWrongAlert = AlipayPattern.this.getResources().getString(R.string.pattern_passwordIsWrong);
						}
						else {
							patternWrongAlert = AlipayPattern.this.getResources().getString(R.string.pattern_passwordIsWrong_limit);
							patternWrongAlert = String.format(patternWrongAlert, mMaxPatternCheckTime - mPatternWrongCount);
						}
						mTopTextView.setText(patternWrongAlert);
						mTopTextView.setTextColor(Color.RED);
					}

					mLockView.clear();
					
//					//验证手势密码
//					if (path.length() >= LockView.MINSELECTED) {
//						String pathDeString = path;//Des.decrypt(path, Constant.ALIPAY_INFO);
//						if (pathDeString.equals(patternPassword)) {
//							if (null != mPatternCheckedListener) {
//								mPatternCheckedListener.onPatternChecked(false, true);
//								return ;
//							}
//						}
//						else {
//							//TODO:手势密码错误
//							mPatternWrongCount++;
//							if (mPatternWrongCount == mMaxPatternCheckTime) {
//								mPatternWrongCount = 0;
//								mPatternCheckedListener.onPatternChecked(false, false);
//								return ;
//							}
//							
//							if (null != mTopTextView) {
//								String patternWrongAlert = null;
//								if (mMaxPatternCheckTime > 0) {
//									patternWrongAlert = AlipayPattern.this.getResources().getString(R.string.pattern_passwordIsWrong);
//								}
//								else {
//									patternWrongAlert = AlipayPattern.this.getResources().getString(R.string.pattern_passwordIsWrong_limit);
//									String.format(patternWrongAlert, mMaxPatternCheckTime - mPatternWrongCount);
//								}
//								mTopTextView.setText(patternWrongAlert);
//							}
//						}
//					}
//					else {
//						//TODO:手势密码不足4位
//						if (null != mTopTextView) {
//							mTopTextView.setText(R.string.pattern_passwordTooShort);
//						}
//					}
//
//					if (null != mTopTextView) {
//						mTopTextView.setTextColor(Color.RED);
//					}
//					mLockView.clear();
				}
			});
    	}
    }
    
    public void TryToSetPattern(boolean skipable) {
    	
    	View patternLayout = this.findViewById(R.id.patternLayout);
    	patternLayout.setBackgroundResource(R.drawable.application_bg);
    	
    	if (null == mPatternSettingView) {
    		mPatternSettingView = ((ViewStub)this.findViewById(R.id.patternSetting)).inflate();
    	}
    	else if (View.GONE == mPatternSettingView.getVisibility()) {
    		mPatternSettingView.setVisibility(View.VISIBLE);
    	}
    	
    	if (null != mPatternCheckView && View.VISIBLE == mPatternCheckView.getVisibility()) {
    		mPatternCheckView.setVisibility(View.GONE);
    	}
    	
       	TextView titleTextView = (TextView)this.findViewById(R.id.title_text);
    	if (null != titleTextView) {
    		titleTextView.setText(R.string.pattern_setPattern);
    	}
    	
		if (null != mTopTextView) {
			mTopTextView.setTextColor(this.getResources().getColor(R.color.patterntop_color));
			mTopTextView.setText(R.string.pattern_drawPattern);
		}
    	
		if (skipable) {
			Button skipButton = (Button) this.findViewById(R.id.title_right);
			if (null != skipButton) {
				if (null != mPatternChangeListener) {
					skipButton.setOnClickListener(new View.OnClickListener() {

						@Override
						public void onClick(View v) {
							if (null != mPatternChangeListener) {
								mPatternChangeListener.onPatternChange(true,
										null);
							}
						}
					});
				}
				
				if (View.GONE == skipButton.getVisibility()) {
					skipButton.setVisibility(View.VISIBLE);
					skipButton.setBackgroundResource(R.drawable.title_btn_xml);
				}
				skipButton.setText(R.string.skip);
			}
		}
    	
    	if (null != mLockView) {
    		final LockIndicator lockIndicator = ((LockIndicator)this.findViewById(R.id.lockIndicator));
    		
    		if (null != mBottomTextView) {
    			mBottomTextView.setText(R.string.pattern_resetPattern);
        		mBottomTextView.setOnClickListener(new View.OnClickListener() {
    				
    				@Override
    				public void onClick(View v) {
    					//恢复初始设置手势状态
    					mUserPattern = null;
    					mBottomTextView.setVisibility(View.INVISIBLE);
    					if (null != mTopTextView) {
    						mTopTextView.setTextColor(AlipayPattern.this.getResources().getColor(R.color.patterntop_color));
    						mTopTextView.setText(R.string.pattern_drawPattern);
    						lockIndicator.clear();
    					}
    				}
    			});
        		
        		mBottomTextView.setVisibility(View.INVISIBLE);
    		}
    		
    		mLockView.clear();
    		mLockView.setOnLockInputListener(new LockView.OnLockInputListener() {
				
				@Override
				public void onLockInput(int indexUnlocked) {
					// TODO Auto-generated method stub
					
				}
				
				@Override
				public void onLockDone(String path) {
					if (null == mUserPattern) {
						if (null == path || path.length() < LockView.MINSELECTED) {
							//TODO:手势密码不足4位
							if (null != mTopTextView) {
								mTopTextView.setTextColor(Color.RED);
								mTopTextView.setText(R.string.pattern_passwordTooShort);
							}
						}
						else {
							mUserPattern = path;
							if (null != lockIndicator) {
								lockIndicator.setPath(path);
						    	
								if (null != mTopTextView) {
									mTopTextView.setTextColor(AlipayPattern.this.getResources().getColor(R.color.patterntop_color));
									mTopTextView.setText(R.string.pattern_drawPatternAgain);
								}
							}
						}
					}
					else if (mUserPattern.equals(path)) {
						if (null != mPatternChangeListener) {
							mPatternChangeListener.onPatternChange(false, path);
							return ;
						}
					}
					else {
						//TODO:前后两次不一致
						if (null != mTopTextView) {
							mTopTextView.setTextColor(Color.RED);
							mTopTextView.setText(R.string.pattern_passwordDifferentAgain);
							
				        	if (null != mBottomTextView) {
				        		mBottomTextView.setVisibility(View.VISIBLE);
				        	}
						}
					}
					
					mLockView.clear();
				}
			});
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
