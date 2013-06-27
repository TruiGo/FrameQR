package com.alipay.android.client;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;

import com.alipay.android.appHall.common.Defines;
import com.alipay.android.client.util.BaseHelper;
import com.alipay.android.client.util.DBHelper;
import com.alipay.android.client.util.Utilz;
import com.alipay.android.common.data.UserData;
import com.alipay.android.common.data.UserInfo;
import com.alipay.android.comon.component.AlipayPattern;
import com.alipay.android.comon.component.PatterAlertDialog;
import com.alipay.android.log.AlipayLogAgent;
import com.alipay.android.log.Constants;
import com.alipay.android.log.StorageStateInfo;
import com.eg.android.AlipayGphone.R;

public class PatternLockActivity extends RootActivity {
	public static final String ACTION_PATTERNLOCK = "com.alipay.android.action.PATTERNLOCK";
	public static final String PATTERNLOCKTYPE = "PatternLockType";
	public static final String PATTERNLOCK_CHECK = "PatternLockCheck";
	public static final String PATTERNLOCK_SET = "PatternLockSet";
	
	public static boolean PATTERNLOCKED = false;
//	public static final String SKIPME = "skip";
//	public static final String NEXTACTIVITY = "NextActivity";
//	private boolean mSkipable = false;
//	private String mNextActivity = null;
	
	private AlipayPattern mPatternView = null;
	private boolean mCheckPattern = false;
	private boolean mStartedByAction = false;

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			if (mCheckPattern) {
				if (BaseHelper.tabOnKeyDown(this, keyCode, event))
					return true;
			}
		}
		
		return super.onKeyDown(keyCode, event);
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setShowMenu(false);
		
		if (ACTION_PATTERNLOCK.equals(getIntent().getAction())) {
			mStartedByAction = true;
			PATTERNLOCKED = true;
			countMeIn(false);
		}
		
		this.setContentView(R.layout.alipay_patterncomponent);
		
		Bundle extras = this.getIntent().getExtras();
		boolean checkOrSet = true;
		if (null != extras) {
			String patternLockType = extras.getString(PATTERNLOCKTYPE);
			if (null != patternLockType) {
				if (patternLockType.equals(PATTERNLOCK_CHECK)) {
					checkOrSet = true;
				}
				else {// if (patternLockType.equals(PATTERNLOCK_SET)) {
					checkOrSet = false;
				}
			}
		}
		initPattern(checkOrSet);
		
		
//		Bundle extras = this.getIntent().getExtras();
//		// set title and description
//		String patternLockType = extras.getString(PATTERNLOCKTYPE);
//		TextView titleTextView = (TextView) this.findViewById(R.id.title_text);
//		mDescriptionText = (TextView) this
//				.findViewById(R.id.patternLockDescription);
//		
//		if (null != patternLockType && patternLockType.equals(PATTERNLOCK_VERIFY)) {
//			mPatternVerifyNeeded = true;
//			
//			this.findViewById(R.id.patternTitle).setVisibility(View.GONE);
//			mDescriptionText.setText(R.string.pattern_pleaseIntputPattern);
//			mUserPattern = getUserData().getUserPattern();
//		} 
//		else {
//			mPatternVerifyNeeded = false;
//
//			if (null != extras) {
//				mSkipable = extras.getBoolean(SKIPME);
//				mNextActivity = extras.getString(NEXTACTIVITY);
//			}
//			
//			titleTextView.setText(R.string.pattern_setPattern);
//			mDescriptionText.setText(R.string.pattern_drawPattern);
//			
//			if (mSkipable) {
//				Button skipButton = (Button) this
//						.findViewById(R.id.title_right);
//				if (null != skipButton && null != mNextActivity) {
//					skipButton.setOnClickListener(new View.OnClickListener() {
//
//						@Override
//						public void onClick(View v) {
//							try {
//								Class<?> clazz = Class.forName(mNextActivity);
//								PatternLockActivity.this.startActivity(
//										new Intent(PatternLockActivity.this, clazz));
//							} catch (ClassNotFoundException e) {
//								// TODO Auto-generated catch block
//								e.printStackTrace();
//							}
//
//						}
//					});
//				}
//			}
//		}
//		
//		mLockIndicator = (LockIndicator)this.findViewById(R.id.lockIndicator);
//
//		// set lockview
//		final LockView lockView = (LockView) this.findViewById(R.id.lockView);
//		lockView.setOnLockInputListener(new LockView.OnLockInputListener() {
//
//			@Override
//			public void onLockInput(int indexUnlocked) {
//
//			}
//
//			@Override
//			public void onLockDone(String path) {
//				if (null == path || path.length() < 1) {
//					return ;
//				}
//				//TODO:手势密码不足4位的处理是否可以集中放到前面, depends on 文案
//				
//				if (mPatternVerifyNeeded) {
//					//验证手势密码
//					if (path.length() >= LockView.MINSELECTED) {
//						String pathDeString = Des.decrypt(path, Constant.ALIPAY_INFO);
//						if (pathDeString.equals(mUserPattern)) {
//							PatternLockActivity.this.finish();
//						}
//					}
//					else {
//						//TODO:手势密码不足4位
//						mDescriptionText.setText(R.string.pattern_passwordTooShort);
//						lockView.clear();
//					}
//				}
//				else {
//					//验证手势密码
//					if (path.length() >= LockView.MINSELECTED) {
//						if (null == mUserPattern) {
//							mUserPattern = path;
//							if (null != mLockIndicator) {
//								mLockIndicator.setPath(path);
//							}
//							lockView.clear();
//						}
//						else {
//							if (mUserPattern.equals(path)) {
//								String pathDeString = path;//Des.decrypt(path, Constant.ALIPAY_INFO);
//								
//								getUserData().setUserPattern(pathDeString);
//								
//								//保存到数据库
//								DBHelper db = new DBHelper(PatternLockActivity.this);
//								UserInfo userInfo = db.getLastLoginUser(null);
//								db.updatePattern(userInfo.userAccount, userInfo.type, pathDeString);
//								db.close();
//							}
//							else {
//								//TODO:前后两次不一致
//								mDescriptionText.setText(R.string.pattern_passwordDifferent);
//							}
//						}
//						
//					}
//					else {
//						//TODO:手势密码不足4位
//						mDescriptionText.setText(R.string.pattern_passwordTooShort);
//						lockView.clear();
//					}
//				}
//			} //end of onLockDone
//			
//		});
		
	} //end of onCreate
	
    /*
     * checkOrSetPattern: true为验证手势，否则为设置手势
     */
    private void initPattern(boolean checkOrSetPattern) {
    	mCheckPattern = checkOrSetPattern;
    	
    	mPatternView = (AlipayPattern)this.findViewById(R.id.AlipayPattern);

		if (null != mPatternView) {
			
			if (checkOrSetPattern) {
	    		
				mPatternView.setOnPatternCheckedListener(new AlipayPattern.OnPatternCheckedListener() {
					
					@Override
					public void onPatternChecked(boolean forgetPassword, boolean patternIsRight) {
						if (patternIsRight) {
							PatternLockActivity.this.finish();
						}
						else if (forgetPassword) {
							PatternLockActivity.this.getDataHelper().showDialog(PatternLockActivity.this, R.drawable.infoicon, getResources().getString(R.string.WarngingString), 
									getResources().getString(R.string.alertAfterForgetPassword),
									getResources().getString(R.string.pattern_reset), new DialogInterface.OnClickListener() {
										@Override
										public void onClick(DialogInterface dialog, int which) {
											    goLogin(true);
											}
										},
									getResources().getString(R.string.DialogCancel), null, null, null);
						}
						else {
							goLogin(false);
						}
					}
				});
				
				UserData userData = this.getUserData();
				if (null != userData) {
					String userPatternString = userData.getUserPattern();
					if (null != userPatternString && !"".equals(userPatternString)) {
						mPatternView.checkPattern(userData.getLastLoginAccount(), 
								Utilz.getSecuredRealName(userData.getRealName()), 
								userPatternString, userData.getUserAvtar());
					}
					else {
						//TODO 异常情况
						finish();
					}
				}
				else {
					//TODO:正常应该不会走到这里，发现userData为空跳转到登陆
					goLogin(false);
				}
			}
			else {
				mPatternView.setOnPatternChangeListener(new AlipayPattern.OnPatternChangeListener() {
					
					@Override
					public void onPatternChange(boolean skip, String newPattern) {
						if (!skip && null != newPattern) {
							String pathDeString = newPattern;//Des.decrypt(path, Constant.ALIPAY_INFO);
							
							getUserData().setUserPattern(pathDeString);

							//保存到数据库
							DBHelper db = new DBHelper(PatternLockActivity.this);
							UserInfo userInfo = db.getLastLoginUser(null);
							db.updatePattern(userInfo.userAccount, userInfo.type, pathDeString);
							db.close();
							
							//设置手势成功埋点
							doSimpleLog(Constants.BehaviourID.SETGESTURE, "Y",
									Constants.VIEWID_SetGestureView, 
									Constants.VIEWID_ManagePasswordView, 
									Constants.Seed_SetGestureButton);
							
							//手势埋点
							/*StorageStateInfo storageStateInfo = StorageStateInfo.getInstance();
							AlipayLogAgent.onEvent(PatternLockActivity.this, 
									Constants.MONITORPOINT_EVENT, 
									"Y", 
									"", 
									"",
									"",
									storageStateInfo.getValue(Constants.STORAGE_PRODUCTID), 
									PatternLockActivity.this.getUserId(), 
									storageStateInfo.getValue(Constants.STORAGE_PRODUCTVERSION), 
									storageStateInfo.getValue(Constants.STORAGE_CLIENTID),
									Constants.EVENTTYPE_GESTURESETTINGSUCCESS);*/
							
							new PatterAlertDialog(PatternLockActivity.this).show(R.string.patterSetSuccess, R.drawable.pattern_success,
									new PatterAlertDialog.OnDismissListener() {
								public void onDismiss() {
									PatternLockActivity.this.finish();
								}
							});
							//Toast.makeText(PatternLockActivity.this, R.string.patterSetSuccess, 3000).show();
						}
						
						//PatternLockActivity.this.finish();
					}
				});
				
				mPatternView.TryToSetPattern(false);
			}
		}
    }
    
    private void goLogin(boolean forecePatternSetting) {
    	DBHelper db = new DBHelper(this);
		UserInfo userInfo = db.getLastLoginUser(null);
		if (null != userInfo) {
			//重置登陆密码和手势
			db.resetRsaPassword(userInfo.userAccount, userInfo.type);
			db.deletePattern(userInfo.userAccount, userInfo.type);

			if (1 == db.getAutoLogin(userInfo.userAccount, userInfo.type)) {
				db.deleteAutoLoginRecord(userInfo.userAccount, userInfo.type);
			}
		}
		db.close();

    	logoutUser();

		Intent intent = new Intent(this, Login.class);
    	
    	AlipayApplication appContext = (AlipayApplication)getApplicationContext();
    	Activity currentActivity = appContext.getActivity();
    	if (null != currentActivity && !(currentActivity instanceof Main)) {
    		appContext.finishCurrentActivity();
    		intent.putExtra(Defines.NextActivity, Main.class.getName());
    	}
		
		intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
		intent.putExtra(Login.FORCEPATTERNSETTING, forecePatternSetting);
		intent.putExtra(Login.FORCEAUTOLOGIN, true);
		startActivity(intent);

		finish();
    }

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
	}

	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		if (mStartedByAction) {
			PATTERNLOCKED = false;
		}
	}
}