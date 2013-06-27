/*
 * Copyright (C) 2011 The AlipayClient Project
 * All right reserved.
 * author: chengkuang@alipay.com
 * refactoring by shiqun.shi@alipay.com
 */

package com.alipay.android.client;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.StringTokenizer;
import java.util.Timer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Html;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.MeasureSpec;
import android.view.View.OnClickListener;
import android.view.View.OnFocusChangeListener;
import android.view.View.OnKeyListener;
import android.view.ViewStub;
import android.view.inputmethod.EditorInfo;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TabHost.TabContentFactory;
import android.widget.TextView;
import android.widget.Toast;

import com.alipay.android.appHall.CacheManager;
import com.alipay.android.appHall.Helper;
import com.alipay.android.appHall.bussiness.CallBackFilter;
import com.alipay.android.appHall.common.BitmapDownloadListener;
import com.alipay.android.appHall.common.CacheSet;
import com.alipay.android.appHall.common.Defines;
import com.alipay.android.client.baseFunction.AlipayGetLoginPassword;
import com.alipay.android.client.baseFunction.AlipayRegister;
import com.alipay.android.client.baseFunction.UpdateHelper;
import com.alipay.android.client.constant.AlipayHandlerMessageIDs;
import com.alipay.android.client.constant.Constant;
import com.alipay.android.client.manufacture.AppUpgradeInterceptor;
import com.alipay.android.client.manufacture.InterceptorFactory;
import com.alipay.android.client.util.AlipayDataStore;
import com.alipay.android.client.util.AlipayInputErrorCheck;
import com.alipay.android.client.util.BaseHelper;
import com.alipay.android.client.util.DBHelper;
import com.alipay.android.client.util.LephoneConstant;
import com.alipay.android.client.util.Utilz;
import com.alipay.android.client.webapp.webActivity;
import com.alipay.android.common.data.AlipayUserState;
import com.alipay.android.common.data.UserData;
import com.alipay.android.common.data.UserInfo;
import com.alipay.android.comon.component.AlipayPattern;
import com.alipay.android.comon.component.EditTextWithButton;
import com.alipay.android.comon.component.PatterAlertDialog;
import com.alipay.android.comon.component.ProgressDiv;
import com.alipay.android.comon.component.SmartInputTextView;
import com.alipay.android.log.Constants;
import com.alipay.android.log.StorageStateInfo;
import com.alipay.android.net.RequestMaker;
import com.alipay.android.net.http.HttpRequestMaker;
import com.alipay.android.push.ServiceManager;
import com.alipay.android.safepay.MobileSecurePayHelper;
import com.alipay.android.security.Des;
import com.alipay.android.security.RSA;
import com.alipay.android.ui.beanutil.VoucherSyncHelper;
import com.alipay.android.util.LogUtil;
import com.alipay.platform.core.AlipayApp;
import com.alipay.platform.core.Command;
import com.alipay.platform.view.ActivityMediator;
import com.alipay.platform.view.OnDataChangeListener;
import com.eg.android.AlipayGphone.R;
import com.taobao.statistic.TBS;

public class Login extends RootActivity implements TabContentFactory, OnDataChangeListener {
	public static final String FORCEAUTOLOGIN = "forceautologin";
	public static final String FORCEPATTERNSETTING = "forcepatternsetting";
	public static final String FORCENOPATTERNCHECK = "forcenopatterncheck";
    public static final String GET_RSAKEY_ID = "10";

    public static final String LOGIN_ID = "11";

    public static final String GET_CHECK_CODE_ID = "12";

    private static final String TAG = "Login";

    private final String FILE_PATH = Constant.FILE_PATH;

    private final String FILE_NAME = "autocompleteforuser";

    private AlipayUserState curUserState = AlipayUserState.getInstance();

    // private MessageFilter mMessageFilter = new MessageFilter(this);
    private RequestMaker mRequestMaker;

    private CallBackFilter mBackFilter;

    String mExtraData = null;

    String mLoginServerTime = null;
    
    private String mUserPattern = null;
    private View mWelcomeView = null;
    private boolean mAutoLoginBefore = false; //not this time
    private boolean mAutoLogin = false;
    private boolean mCheckPatternAfterLogin = false;
    private boolean mForgetPatternPassword = false;
    private boolean mForceAutoLogin = false;    //强制自动登录
    private boolean mForcePatternSetting = false; //强制设置手势，主要是忘记密码的情况
    private boolean mForceNoPatternCheck = false; //强制不检测手势，主要为第三方应用调用考虑
    
    class CheckCodeBitmapDownloadListener implements BitmapDownloadListener {
        public CheckCodeBitmapDownloadListener() {
            super();
        }

        @Override
        public void onComplete(final Bitmap checkCodeBitmap) {
            Login.this.runOnUiThread(new Runnable() {
                public void run() {
                    if (checkCodeBitmap != null) {
                        if (mTaobao) {
                            mIsTaobaoShowCheckCode = true;
                            mTaobaoCheckCodeImg = checkCodeBitmap;
                        } else if (mAliPay) {
                            mIsAlipayShowCheckCode = true;
                            mAlipayCheckCodeImg = checkCodeBitmap;
                        }

                        mCheckCodeLayout.setVisibility(View.VISIBLE);
                        setCheckCodeImg(checkCodeBitmap);
                        mPasswordEdit.setImeOptions(EditorInfo.IME_ACTION_NEXT);
                        mCheckCodeInput.setText("");
                        mCheckCodeInput.requestFocus();
                    }

                    closeProgress();
                }
            });
        }
    }

    private Handler mHandlerforRefreshUi = new Handler() {
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case REFRESH_UI: {
                    if (Login.this.mLinearLayoutforDropDown != null) {
                        try {
                            ListView parent = (ListView) Login.this.mLinearLayoutforDropDown
                                .getParent();
                            parent.setCacheColorHint(Color.parseColor("#00000000"));
                            parent.setBackgroundColor(Color.parseColor("#ffffffff"));

                            View parent2 = (View) parent.getParent();
                            parent2.setBackgroundColor(Color.parseColor("#ffffffff"));
                        } catch (Exception e) {
                            // e.printStackTrace();
                        }
                    }
                }
                    break;
                case SHOW_CHECKCODE_UI:

                    break;
            }

            super.handleMessage(msg);
        }
    };

    @Override
    protected void onStart() {
        super.onStart();

        // 终止之前运行的Push Service
        // 此服务在九宫格或者某一应用中start，因为初始化需要的参数与登录相关，故在此处终止
        // Log.d("Login", "Stopping PushLink service");
        // stopService(new Intent(this, AlipayPushLinkService.class));

        // 启动push服务——低于2.0的不需要启动
        int expectedVersion = 4;
        if (Integer.valueOf(Build.VERSION.SDK).intValue() > expectedVersion) {
            // 告知serviceManager停止push服务——因为用户可能会改变有效用户信息（登录或者删除）
            ServiceManager serviceManager = new ServiceManager(this);
            serviceManager.stopService();
        }

    }

    @Override
    protected void onStop() {
        super.onStop();

        // 启动push服务——低于2.0的不需要启动
        int expectedVersion = 4;
        if (Integer.valueOf(Build.VERSION.SDK).intValue() > expectedVersion) {
            ServiceManager serviceManager = new ServiceManager(this);
            AlipayDataStore OtherDataSettings = new AlipayDataStore(Login.this);
            String pushStatus = OtherDataSettings
                .getString(AlipayDataStore.NOTIFICATION_SETTING_RECEIVECHECKEDSTATUS);
            if (pushStatus != null && pushStatus.equals("false")) {
                serviceManager.stopService();
            } else {
                serviceManager.startService(1);
            }
        }
    }

    private SmartInputTextView mUserNameEdit = null;

    public View mLinearLayoutforDropDown = null;

    private String mUserName = "";

    private EditText mPasswordEdit = null;

    private String mPassword = "";

    static String preUserName = "";

    private String mCheckCode = "";
    private Button loginTypeButton = null;

    private Button mLoginButton = null;

    private TextView mTitleName = null;

    private CheckBox mPasswordCheckBox = null;

    private ProgressDiv mProgress = null;

    /**
     * Error Messages Array
     */
    private int mErrorType = LOGIN_OK;

    private static final int LOGIN_OK = -5;

    private static final int LOGIN_FINISH = -4;

    private static final int LOGIN_START = -3;

    private static final int GET_RSA_KEY_FINISH = -2;

    private static final int GET_RSA_KEY_START = -1;

    ArrayAdapter<String> monthArray = null;

    private ImageView mEditImage = null;

    // private TextView mPasswordTip = null;
    private TextView mRetrieve = null;

    private RecentAdapter adapter;

    RelativeLayout mCheckCodeLayout = null;

    private boolean mIsTaobaoShowCheckCode = false;

    private boolean mIsNeedShowCheckCode = false;

    private boolean mIsAlipayNeedShowCheckCode = false;

    private boolean mIsAlipayShowCheckCode = false;

    ImageView mCheckCodeImg = null;

    EditText mCheckCodeInput = null;

    String mCheckCodeId = "";

    String mCheckCodeUrl = "";

    Bitmap mTaobaoCheckCodeImg = null;

    Bitmap mAlipayCheckCodeImg = null;

    String mAlipayCheckCodeUrl = "";

    public DBHelper db = null;

    public boolean mTaobao = false;

    public boolean mAliPay = false;

    public static final String LOGINTYPE_TAOBAO = "taobao";

    public static final String LOGINTYPE_ALIPAY = "alipay";

    public static final String LOGINTYPE_SAFEPAY = "safepay";
    
    public static final String LOGINTYPE_AUTOLOGIN = "autologin";
    public static final String LOGINTYPE_NOAUTOLOGIN = "noautologin";
    
    public static final String LOGINTYPE = "logintype";
    
    private Intent mIntent = null;

    public static Activity mActivity = null;
    
    private AlipayPattern mPatternView = null;

    private Button mSignupButton = null;
    
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mRequestMaker = new HttpRequestMaker(getApplicationContext(), R.raw.interfaces);
        mBackFilter = new CallBackFilter(this);

        if (LephoneConstant.isLephone()) {
            getWindow().addFlags(LephoneConstant.FLAG_ROCKET_MENU_NOTIFY);
        }
        mActivity = this;
        Constant.ALIPAY_INFO = BaseHelper.getDesKey(this);
        db = new DBHelper(this);
        mIntent = getIntent();

        //
        AlipayDataStore settings = new AlipayDataStore(this);
        settings.TryRemoveDownloadFile();
        Constant.MobileCompany = getResources().getString(R.string.useragent);

        setContentView(R.layout.alipay_login_320_480);
        loadAllVariables();

        //
        InitSetting();

        //卡宝：取消提示
//        mPasswordCheckBox.setOnClickListener(new OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                // TODO Auto-generated method stub
//                if (mPasswordCheckBox.isChecked()) {
//                    // add dialog
//                    if (mTaobao) {
//                        getDataHelper()
//                            .showDialog(Login.this, R.drawable.infoicon,
//                                getResources().getString(R.string.PWD_HINT),
//                                getResources().getString(R.string.PWD_HINT_INFO_TAOBAO),
//                                getResources().getString(R.string.Ensure), null, null, null, null,
//                                null);
//                    } else {
//                        getDataHelper()
//                            .showDialog(Login.this, R.drawable.infoicon,
//                                getResources().getString(R.string.PWD_HINT),
//                                getResources().getString(R.string.PWD_HINT_INFO),
//                                getResources().getString(R.string.Ensure), null, null, null, null,
//                                null);
//                    }
//                }
//            }
//
//        });
    }
    
    public void showLogin() {
    	if (null != mWelcomeView && View.VISIBLE == mWelcomeView.getVisibility()) {
    		mWelcomeView.setVisibility(View.GONE);
    	}
    	
    	if (null != mPatternView && View.VISIBLE == mPatternView.getVisibility()) {
    		mPatternView.setVisibility(View.GONE);
    		setShowMenu(true);
    	}
    }
    
    void showWelcome() {
    	if (null == mWelcomeView) {
        	ViewStub welcomeUseStub = (ViewStub)this.findViewById(R.id.welcomeuse);
        	if (null != welcomeUseStub) {
        		mWelcomeView = welcomeUseStub.inflate();
        	}
    	}
    	else {
    		mWelcomeView.setVisibility(View.VISIBLE);
    	}
    }

    /*
     * checkOrSetPattern: true为验证手势，否则为设置手势
     */
    private void initPattern(boolean checkOrSetPattern) {
		if (null == mPatternView) {
			ViewStub view = (ViewStub)this.findViewById(R.id.pattern);
			View view2 = view.inflate();
			AlipayPattern view3 = (AlipayPattern)view2;
			mPatternView = view3;//(AlipayPattern)(((ViewStub) this.findViewById(R.id.pattern)).inflate());
		}
		
		//hide menu
		setShowMenu(false);
		
		if (null != mPatternView) {
			
			if (View.GONE == mPatternView.getVisibility()) {
				mPatternView.setVisibility(View.VISIBLE);
			}
			
			if (checkOrSetPattern) {
				mPatternView.setOnPatternCheckedListener(new AlipayPattern.OnPatternCheckedListener() {
					
					@Override
					public void onPatternChecked(boolean forgetPassword, boolean patternIsRight) {
						mForgetPatternPassword = forgetPassword;
						if (patternIsRight) {
							//验证手势成功埋点
							doSimpleLog(Constants.BehaviourID.CHECKGESTURE, "Y",
									Constants.VIEWID_NoneView, 
									Constants.VIEWID_GestureView, 
									Constants.Seed_CheckGesture);
							
							if (mCheckPatternAfterLogin) {
								login();
							}
							else {
								showWelcome();
								mLoginButton.performClick();
							}
						}
						else if (forgetPassword) {
							Login.this.getDataHelper().showDialog(Login.this, R.drawable.infoicon, getResources().getString(R.string.WarngingString), 
									getResources().getString(R.string.alertAfterForgetPassword),
									getResources().getString(R.string.pattern_reset), new DialogInterface.OnClickListener() {
										@Override
										public void onClick(DialogInterface dialog, int which) {
											    doAfterCheckPatternFailed();
											    mForcePatternSetting = true;
											}
										},
									getResources().getString(R.string.DialogCancel), new DialogInterface.OnClickListener() {
										@Override
										public void onClick(DialogInterface dialog, int which) {
											    mForgetPatternPassword = false;
											}
										}, null, null);
						}
						else {
							doAfterCheckPatternFailed();
						}
					}
				});
				
				//获取头像
				UserData userData = getUserData();
				Bitmap avatarUser = null;
				if (null != userData) {
					avatarUser = userData.getUserAvtar();
				}

				if (mAutoLogin) {
					if (null != db) {
						UserInfo userInfo = db.getLastLoginUser(null);
						if (null == avatarUser) {
							avatarUser = UserData.getUserAvtar(this, userInfo.userId);
						}
						if (null != userInfo) {
							mPatternView.checkPattern(getUserNameShow(userInfo.userAccount), 
									Utilz.getSecuredRealName(userInfo.userName), 
									mUserPattern, avatarUser);
						}
					}
				}
				else {
					
					if (null != userData) {
						mPatternView.checkPattern(getUserNameShow(mUserName), 
								Utilz.getSecuredRealName(userData.getRealName()), 
								mUserPattern, avatarUser);
					}
				}

			} else {
				mPatternView.setOnPatternChangeListener(new AlipayPattern.OnPatternChangeListener() {
					
					@Override
					public void onPatternChange(boolean skip, String newPattern) {
						if (!skip) {
							String pathDeString = newPattern;//Des.decrypt(path, Constant.ALIPAY_INFO);
							
							getUserData().setUserPattern(pathDeString);
							
							//保存到数据库
							if (null != db) {
								db.updatePattern(mUserName, mTaobao ? LOGINTYPE_TAOBAO : LOGINTYPE_ALIPAY, pathDeString);
							}
							
							//设置手势成功埋点
							doSimpleLog(Constants.BehaviourID.SETGESTURE, "Y",
									Constants.VIEWID_SetGestureView, 
									Constants.VIEWID_PasswordView, 
									Constants.Seed_SetGestureButton);
							new PatterAlertDialog(Login.this).show(R.string.patterSetSuccess, R.drawable.pattern_success,
									new PatterAlertDialog.OnDismissListener() {
								public void onDismiss() {
									login();
								}
							});

						}
						else {
							Login.this.getDataHelper().showDialog(Login.this, R.drawable.infoicon, getResources().getString(R.string.WarngingString), 
									getResources().getString(R.string.alertAfterSkipPattern),
									getResources().getString(R.string.Ensure), new DialogInterface.OnClickListener() {
										@Override
										public void onClick(DialogInterface dialog, int which) {
											login();
											}
										},
										getResources().getString(R.string.MenuBack), null, null, null);
						}
					}
				});
				
				mPatternView.TryToSetPattern(true);

				if (!mForcePatternSetting) {
					new PatterAlertDialog(Login.this).show(R.string.patternTip, null);
				}
			}
		}
    }
    
    private void doAfterCheckPatternFailed() {
    	if (null != mPatternView) {
    		mPatternView.setVisibility(View.GONE);
    		setShowMenu(true);
    	}
    	
    	mAutoLogin = false;
    	mAutoLoginBefore =false;
    	
		if (null != db) {
			UserInfo userInfo = db.getLastLoginUser(null);
			if (null != userInfo) {
				//重置登陆密码和手势
				mPasswordEdit.setText("");
				db.resetRsaPassword(userInfo.userAccount, userInfo.type);
				db.deletePattern(userInfo.userAccount, userInfo.type);
				
				if (Constant.AUTOLOGIN_YES == db.getAutoLogin(userInfo.userAccount, userInfo.type)) {
					db.deleteAutoLoginRecord(userInfo.userAccount, userInfo.type);
				}
				
				mUserPattern = null;
				
				UserData userData = getUserData();
				if (null != userData) {
					userData.setUserPattern(null);
				}
			}
		}
    }

    private void InitSetting() {
        AlipayDataStore settings = new AlipayDataStore(this);
        String logintype = settings.getString(AlipayDataStore.LOGINTYPE);

        if (mIntent.getExtras() != null) {
            Bundle mBundle = mIntent.getExtras();
            String type = mBundle.getString(AlipayDataStore.LOGINTYPE);
            if (type != null
                && (type.equals(AlipayDataStore.LOGINTYPE_ALIPAY) || (type
                    .equals(AlipayDataStore.LOGINTYPE_TAOBAO)))) {
                logintype = type;
            }
            
            mForceAutoLogin = mBundle.getBoolean(FORCEAUTOLOGIN);
            mForcePatternSetting = mBundle.getBoolean(FORCEPATTERNSETTING);
            mForceNoPatternCheck = mBundle.getBoolean(FORCENOPATTERNCHECK);
        }

        if (logintype.equals(LOGINTYPE_TAOBAO)) {
            mTaobao = true;
            mAliPay = false;
            mUserNameEdit.setHint(R.string.LoginTaobaoHint);
            loginTypeButton.setText(getResources().getString(R.string.LoginTypeAlipay));
            mTitleName.setBackgroundResource(R.drawable.title_taobao_logo);

            //taobao login隐藏注册
            if (null != mSignupButton) {
            	mSignupButton.setVisibility(View.GONE);
            }
            // mPasswordTip.setText(R.string.LoginPassword);
            // mLoginImage.setBackgroundResource(R.drawable.login_taobao);
            // mLoginButton.setBackgroundResource(R.drawable.alipay_button_taobao);
        } else {
            mTaobao = false;
            mAliPay = true;
            mUserNameEdit.setHint(R.string.LoginAlipayHint);
            loginTypeButton.setText(getResources().getString(R.string.LoginTypeTaobao));
            mTitleName.setBackgroundResource(R.drawable.title_alipay_logo);
            // mPasswordTip.setText(R.string.LoginPassword);
            // mLoginImage.setBackgroundResource(R.drawable.login_alipay);
            // mLoginButton.setBackgroundResource(R.drawable.alipay_button_cash);
        }

        readSetting(true);
        
        if (!mAutoLogin) {
        	Utilz.warmupUrl(Constant.getContainerURL(this), this, false);
        	Utilz.warmupUrl(Constant.getAlipayURL(this), this, false);
        } 
    }

    private OnClickListener loginTypeListener = new OnClickListener() {

        @Override
        public void onClick(View v) {
            // TODO Auto-generated method stub
            if (!mAliPay) {
                mTaobao = false;
                mAliPay = true;
                updateSetting(false);
                // mLoginButton.setBackgroundResource(R.drawable.alipay_button_cash);
                // mLoginImage.setBackgroundResource(R.drawable.login_alipay);
                if (mIsAlipayShowCheckCode) {
                    mPasswordEdit.setImeOptions(EditorInfo.IME_ACTION_NEXT);
                } else
                    mPasswordEdit.setImeOptions(EditorInfo.IME_ACTION_DONE);
                readSetting(false);
                mTitleName.setBackgroundResource(R.drawable.title_alipay_logo);
                mUserNameEdit.setHint(R.string.LoginAlipayHint);
                mPasswordEdit.setHint(R.string.LoginPwdHint);
                //mPasswordCheckBox.setText(R.string.StoredPassword);
                // mPasswordTip.setText(R.string.LoginPassword);
                setEditContent();
                loginTypeButton.setText(getResources().getString(R.string.LoginTypeTaobao));

                //taobao login隐藏注册
                if (null != mSignupButton) {
                	mSignupButton.setVisibility(View.VISIBLE);
                }
            } else {
                mTaobao = true;
                mAliPay = false;
                updateSetting(false);
                // mLoginImage.setBackgroundResource(R.drawable.login_taobao);
                // mLoginButton.setBackgroundResource(R.drawable.alipay_button_taobao);
                if (mIsTaobaoShowCheckCode)
                    mPasswordEdit.setImeOptions(EditorInfo.IME_ACTION_NEXT);
                else
                    mPasswordEdit.setImeOptions(EditorInfo.IME_ACTION_DONE);
                readSetting(false);
                mTitleName.setBackgroundResource(R.drawable.title_taobao_logo);
                mUserNameEdit.setHint(R.string.LoginTaobaoHint);
                mPasswordEdit.setHint(R.string.LoginPwdHint);
                //mPasswordCheckBox.setText(R.string.StoredTaobaoPassword);
                // mPasswordTip.setText(R.string.LoginPassword);
                setEditContent();
                loginTypeButton.setText(getResources().getString(R.string.LoginTypeAlipay));
                
                //taobao login隐藏注册
                if (null != mSignupButton) {
                	mSignupButton.setVisibility(View.GONE);
                }
            }
        }
    };

    private void readSetting(boolean checkAutoLogin) {
        String type;
        if (!mTaobao) {
            type = LOGINTYPE_ALIPAY;

            mRetrieve.setVisibility(View.VISIBLE);
            if (!mIsAlipayShowCheckCode) {
                mCheckCodeLayout.setVisibility(View.GONE);
            } else {
                mCheckCodeLayout.setVisibility(View.VISIBLE);
                if (mAlipayCheckCodeImg != null)
                    // mCheckCodeImg.setImageBitmap(mAlipayCheckCodeImg);
                    setCheckCodeImg(mAlipayCheckCodeImg);
                mCheckCodeInput.setText("");
            }
        } else {
            type = LOGINTYPE_TAOBAO;

            mRetrieve.setVisibility(View.GONE);
            if (!mIsTaobaoShowCheckCode) {
                mCheckCodeLayout.setVisibility(View.GONE);
            } else {
                mCheckCodeLayout.setVisibility(View.VISIBLE);
                if (mTaobaoCheckCodeImg != null)
                    // mCheckCodeImg.setImageBitmap(mTaobaoCheckCodeImg);
                    setCheckCodeImg(mTaobaoCheckCodeImg);
                mCheckCodeInput.setText("");
            }
        }

        UserInfo userInfo = db.getLastLoginUser(type);
        if (userInfo == null) {
            userInfo = new UserInfo();
        }
        // else ok.
        mUserNameEdit.setText("");
        mPasswordEdit.setText("");
        mPasswordCheckBox.setChecked(true);
        mUserNameEdit.setThreshold(100);
        mUserNameEdit.setText(userInfo.userAccount);
        BaseHelper.setDispayMode(mUserNameEdit, null);
        String pw = "";
        try {
            if (!userInfo.userPassword.equals("")) {
                pw = Des.decrypt(userInfo.userPassword, Constant.ALIPAY_INFO);
                if (pw != null && !"".equals(pw)) {
                    mPasswordEdit.setText(userInfo.randomNum);
                }
            } else if (!"".equals(userInfo.rsaPassword)) {
                mPasswordEdit.setText(userInfo.randomNum);
            } else {
                mPasswordEdit.setText("");
            }

            if (mForceAutoLogin) {
            	mPasswordCheckBox.setChecked(true);
            }
            //只有之前已经手动不勾选自动登录才会不勾选，否则一律勾选
            else if (Constant.AUTOLOGIN_NO == db.getAutoLogin(userInfo.userAccount, type)) {
            	mPasswordCheckBox.setChecked(false);
            }

//            else if (!Utilz.isEmpty(pw) || !"".equals(userInfo.rsaPassword)) {
//                mPasswordCheckBox
//                    .setChecked((pw.length() > 0 || userInfo.rsaPassword.length() > 0) ? true
//                        : false);
//            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        
        //不做自动登陆处理
        if (!checkAutoLogin || getUserData() != null) {
        	return ;
        }
        
        mUserPattern = db.getPattern(userInfo.userAccount, type);
        mAutoLoginBefore = (Constant.AUTOLOGIN_YES == db.getAutoLogin(userInfo.userAccount, type));
        Bundle extraBundle = mIntent.getExtras();
        if (null != extraBundle && LOGINTYPE_AUTOLOGIN.equals(extraBundle.getString(LOGINTYPE))) {
            //检测自动登录
            if (mAutoLoginBefore && !"".equals(userInfo.rsaPassword)) {
            	mAutoLogin = true;
            	if (null != mUserPattern && !mForceNoPatternCheck) {
            		initPattern(true);
            	}
            	else {
            		if (null != mLoginButton) {
    					showWelcome();
    					mLoginButton.performClick();
    				}
            	}
            }
        }

    }

    /**
     * 如果用户没有登录成功就不保留密码
     * 
     * @param isSave
     */
    private void updateSetting(boolean isSave) {
        AlipayDataStore settings = new AlipayDataStore(this);
        SharedPreferences preference = settings.settings;

        if (settings.getBoolean(AlipayDataStore.ISFIRSTLOGIN, true)) {
            settings.putBoolean(AlipayDataStore.ISFIRSTLOGIN, false);
        }

        if (mTaobao) {
            if (isSave) {
                preference.edit().putString(AlipayDataStore.LOGINTYPE, LOGINTYPE_TAOBAO).commit();
            }

            mRetrieve.setVisibility(View.GONE);
            if (!mIsTaobaoShowCheckCode) {
                mCheckCodeLayout.setVisibility(View.GONE);
            } else {
                mCheckCodeLayout.setVisibility(View.VISIBLE);
            }
        } else {
            if (isSave) {
                preference.edit().putString(AlipayDataStore.LOGINTYPE, LOGINTYPE_ALIPAY).commit();
            }

            mRetrieve.setVisibility(View.VISIBLE);
            if (!mIsAlipayShowCheckCode) {
                mCheckCodeLayout.setVisibility(View.GONE);
            } else {
                mCheckCodeLayout.setVisibility(View.VISIBLE);
            }
        }
    }

    private void loadAllVariables() {
        mEditImage = (ImageView) findViewById(R.id.LoginAccountButton);
        mEditImage.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                mUserNameEdit.setThreshold(0);
                adapter.getFilter().filter(null);

                mUserNameEdit.showDropDown();

                if (LephoneConstant.isLephone()) {
                    // refresh the elements.
                    Timer m_timerRefreshUi = new Timer();
                    m_timerRefreshUi.schedule(new RefreshUiTask(Login.this), 1 * 200);
                }
            }
        });
        // mLoginImage = (ImageView) findViewById(R.id.LoginImage);
        // Display Item Name.
        mTitleName = (TextView) findViewById(R.id.title_text);
        mTitleName.setBackgroundResource(R.drawable.title_alipay_logo);

        String[] userGroup = getUserGroup();
        if (userGroup != null) {
            monthArray = new ArrayAdapter<String>(this,
                android.R.layout.simple_dropdown_item_1line, userGroup);
        }

        // load user input variables
        mUserNameEdit = (SmartInputTextView) findViewById(R.id.LoginAccountEditText);
        
        //240X320上显示AutoComplete会有问题，所以关闭此功能
        if (320 == getResources().getDisplayMetrics().heightPixels) {
        	mUserNameEdit.setAutoComplete(false);
        }
        
        adapter = new RecentAdapter(this);
        if (monthArray != null) {
            // mUserNameEdit.setAdapter(monthArray);
            // mUserNameEdit.setAdapter(adapter);
        }
        mUserNameEdit.setAdapter(adapter);

        mUserNameEdit.setOnItemClickListener(new userlistItemClickListener());
        mUserNameEdit.setOnKeyListener(new UserEditOnKeyListener());
        setEditContent();
     
        mUserNameEdit.setTextColor(Color.BLACK);
        
        final ScrollView loginScrollView = (ScrollView)findViewById(R.id.LoginScrollView);
        final EditTextWithButton etWithButton = (EditTextWithButton) findViewById(R.id.LoginPasswordEditText);
        etWithButton.setInputName("");
        
        EditText passwordEdit = etWithButton.getEtContent();
        passwordEdit.setOnFocusChangeListener(new OnFocusChangeListener() {
			@Override
			public void onFocusChange(View v, boolean hasFocus) {
				if(hasFocus){
					int divide = etWithButton.getTop()-mUserNameEdit.getBottom();
					loginScrollView.scrollBy(0, mUserNameEdit.getHeight() + divide);
				}
				
			}
		});

        mPasswordEdit = etWithButton.getEtContent();
        mPasswordEdit.setHint(R.string.LoginPwdHint);

        // load login button
        mLoginButton = (Button) findViewById(R.id.AlipayLoginButton);
        mLoginButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View arg0) {
             // get user name and password. to login.
                mUserName = mUserNameEdit.getText().toString();
                mPassword = mPasswordEdit.getText().toString();
                mCheckCode = mCheckCodeInput.getText().toString();

                // 将全角字符串转换为半角字符串
                mUserName = Helper.toDBC(mUserName);
                mPassword = Helper.toDBC(mPassword);
                mCheckCode = Helper.toDBC(mCheckCode);

                getDataHelper().mLoginAccountPre = mUserName;
                // check user name
                int tResult = AlipayInputErrorCheck.CheckUserID(mUserName);
                if (mTaobao == true && tResult != AlipayInputErrorCheck.ERROR_NULL_INPUT)
                    tResult = AlipayInputErrorCheck.NO_ERROR;
                if (tResult != AlipayInputErrorCheck.NO_ERROR) {
                    // check error.
                    String warningMsg;
                    if (tResult == AlipayInputErrorCheck.ERROR_INVALID_FORMAT) {
                        warningMsg = getResources().getString(R.string.WarningInvalidAccountName);
                    } else if (tResult == AlipayInputErrorCheck.ERROR_NULL_INPUT) {
                        warningMsg = getResources().getString(R.string.NoEmptyAccountName);
                    } else {
                        warningMsg = "UNKNOWN_ERROR TYPE = " + tResult;
                        // Log.i(LOG_TAG, warningMsg);
                    }

                    BaseHelper.recordWarningMsg(Login.this, warningMsg, Constants.LOGINVIEW);

                    getDataHelper().showDialog(Login.this, R.drawable.infoicon,
                        getResources().getString(R.string.WarngingString), warningMsg,
                        getResources().getString(R.string.Ensure), null, null, null, null, null);
                    mErrorType = LOGIN_OK;
                    return;
                }

                // check password input
                tResult = AlipayInputErrorCheck.checkLoginPassword(mPassword);
                if (tResult != AlipayInputErrorCheck.NO_ERROR) {
                    // check error.
                    String warningMsg;
                    if (tResult == AlipayInputErrorCheck.ERROR_INVALID_FORMAT) {
                        warningMsg = getResources().getString(R.string.WarningInvalidLoginPassword);
                    } else if (tResult == AlipayInputErrorCheck.ERROR_NULL_INPUT) {
                        warningMsg = getResources().getString(R.string.NoEmptyPassword);
                    } else {
                        warningMsg = "UNKNOWN_ERROR TYPE = " + tResult;
                        // Log.i(LOG_TAG, warningMsg);
                    }

                    BaseHelper.recordWarningMsg(Login.this, warningMsg, Constants.LOGINVIEW);

                    getDataHelper().showDialog(Login.this, R.drawable.infoicon,
                        getResources().getString(R.string.WarngingString), warningMsg,
                        getResources().getString(R.string.Ensure), null, null, null, null, null);
                    mErrorType = LOGIN_OK;
                    return;
                }
                if ((mTaobao && mIsTaobaoShowCheckCode) || (mAliPay && mIsAlipayShowCheckCode)) {
                    if (mCheckCode.length() == 0) {
                        BaseHelper.recordWarningMsg(Login.this, getString(R.string.checkcodeerr),
                            Constants.LOGINVIEW);

                        getDataHelper().showDialog(Login.this, R.drawable.infoicon,
                            getResources().getString(R.string.WarngingString),
                            getString(R.string.checkcodeerr), getResources().getString(R.string.Ensure),
                            null, null, null, null, null);
                        mErrorType = LOGIN_OK;
                        return;
                    }
                }
                
            	UserInfo userInfo = db.getUser(mUserName, mTaobao ? LOGINTYPE_TAOBAO : LOGINTYPE_ALIPAY);
                if (userInfo == null) {
                    userInfo = new UserInfo();
                }

                Helper.hideInput(Login.this, mPasswordEdit);
                
                if (mPassword.equals(userInfo.randomNum)) {
                    loginMethod();
                } else {
                	getRSAKey();
                }
            }
        });
        
        
        // get check box
        mPasswordCheckBox = (CheckBox) findViewById(R.id.LoginStoredPasswordCheckBox);

        mRetrieve = (TextView) findViewById(R.id.AlipayRetrieveTextview);
        mRetrieve.setText(Html.fromHtml("<u>" + getResources().getString(R.string.Retrieve)
                                        + "</u>"));
//        mRetrieve
//            .setTextColor(getResources().getColorStateList(R.drawable.alipay_link_text_colors));
        mRetrieve.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View arg0) {
                jumpToRetrieve();
            }
        });

        loginTypeButton = (Button) findViewById(R.id.logintypebutton);

        mCheckCodeLayout = (RelativeLayout) findViewById(R.id.InputCheckCodeLayout);
        mCheckCodeImg = (ImageView) findViewById(R.id.CheckCodeImage);
        mCheckCodeImg.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mProgress == null) {
                    mProgress = getDataHelper().showProgressDialogWithCancelButton(Login.this,
                        null, getResources().getString(R.string.PleaseWait), false, true,
                        getDataHelper().cancelListener, getDataHelper().cancelBtnListener);
                    if (mTaobao) {
                        getCheckCodeBitmap(mCheckCodeUrl);
                    } else if (mAliPay) {
                        getCheckCodeBitmap(mAlipayCheckCodeUrl);
                    }
                }
            }
        });

        mCheckCodeInput = (EditText) findViewById(R.id.ImageCheckCodeEditText);
        loginTypeButton.setOnClickListener(loginTypeListener);
        

        // load signup button
        mSignupButton = (Button) findViewById(R.id.signupbutton);
        if (null != mSignupButton) {
        	mSignupButton.setOnClickListener(new View.OnClickListener() {
				
				@Override
				public void onClick(View v) {
					Intent tIntent = new Intent(Login.this, AlipayRegister.class);
					startActivity(tIntent);
//					MapManager manager = new MapManager(mActivity);
//					manager.getMap().location("39.920000,116.460000", "北京");
					
				}
			});
        }
    }

    private void setEditContent() {
        mEditImage.measure(MeasureSpec.UNSPECIFIED, MeasureSpec.UNSPECIFIED);
        mUserNameEdit.measure(MeasureSpec.UNSPECIFIED, MeasureSpec.UNSPECIFIED);
        // mEditImage.getMeasuredWidth() + 5, mUserNameEdit.getPaddingBottom());
    }

    public String[] spitString(String version) {
        String checkVersion = "^\\d{1,2}[.]\\d{1,2}[.]\\d{1,2}[.]\\d{1,4}$";
        Pattern p = null; // 正则表达式对象
        Matcher m = null; // 操作的字符串的对像
        p = Pattern.compile(checkVersion);// 载入验证规则
        m = p.matcher(version);// 载入需要验证的字符串
        if (m.matches()) {// 验证是否符合表达式
            String[] splitResult = version.split(".");
            return splitResult;
        } else {
            return null;
        }

    }

    private void sendLoginRequest(String account, String pwd, String checkcode,
                                  String alipayCheckCode) {
        String sessionId = getSessionId();

        ActivityMediator activityMediator = new ActivityMediator(this);
        ArrayList<String> params = new ArrayList<String>();
        params.add(account);// 账户
        params.add(pwd);// 密码

        params.add(mTaobao ? "taobao" : "alipay");// 登陆类型
        params.add(getConfigData().getProductId());// ProductId
        params.add(getConfigData().getProductVersion());// ProductVersion
        params.add(getConfigData().getUserAgent());// user agent
        params.add("false");// loginTaobao
        params.add(checkcode);// checkCode
        params.add(mCheckCodeId);// checkCodeId
        params.add(alipayCheckCode);// loginCheckCode
        params.add(CacheSet.getInstance(this).getString(Constant.CHANNELS));
        params.add(android.os.Build.VERSION.RELEASE);// osVersion
        params.add(getConfigData().getScreenWidth() + "");// ScreenWidth
        params.add(getConfigData().getScreenHeight() + "");// ScreenHeight
        params.add(StorageStateInfo.getInstance().getValue(Constants.STORAGE_UUID) + "");// awid
        
        params.add(sessionId);// sessionId
        params.add(getConfigData().getClientId());// clientID
        activityMediator.sendCommand(LOGIN_ID, "clean", mRequestMaker, params);

        mCheckCodeId = "";
    }

    /**
     * The method when user press Login menu.
     */
    private void loginMethod() {
        // send account check command here
        mErrorType = LOGIN_START;

        UserInfo userInfo = db.getUser(mUserName, mTaobao ? LOGINTYPE_TAOBAO : LOGINTYPE_ALIPAY);
        if (userInfo == null) {
            userInfo = new UserInfo();
        }

        // use rsaPassword login
        String rsaPassword = "";
        if (mPassword.equals(userInfo.randomNum)) {
            rsaPassword = userInfo.rsaPassword;
        } else {
            rsaPassword = RSA.encrypt(mPassword, getPublicKey());
        }

        if (mTaobao)
            sendLoginRequest(mUserName, rsaPassword, mCheckCode, "");
        else if (mAliPay)
            sendLoginRequest(mUserName, rsaPassword, "", mCheckCode);

        if (mProgress != null) {
        	mProgress.dismiss();
        	mProgress = null;
        }
        mProgress = getDataHelper().showProgressDialogWithCancelButton(this, null,
        		getResources().getString(R.string.UserLoginProcessing), false, false,
        		getDataHelper().cancelListener, getDataHelper().cancelBtnListener);
    }

    private void getRSAKey() {
        if (mErrorType == GET_RSA_KEY_START)
            return;

        mErrorType = GET_RSA_KEY_START;
        ActivityMediator activityMediator = new ActivityMediator(this);
        ArrayList<String> params = new ArrayList<String>();
        setSessionId(null);
        params.add("");
        params.add(getConfigData().getClientId());
        activityMediator.sendCommand(GET_RSAKEY_ID, "clean", mRequestMaker, params);

        // myHelper.sendGetRSAKey(mHandler,
        // AlipayHandlerMessageIDs.START_PROCESS_GOT_DATA);
        if (mProgress == null) {
            mProgress = getDataHelper().showProgressDialogWithCancelButton(this, null,
                getResources().getString(R.string.ConfirmAccountPassword), false, true,
                getDataHelper().cancelListener, getDataHelper().cancelBtnListener);
        }
    }

    // go register view
    public void jumpToRetrieve() {
        String account = mUserNameEdit.getText().toString();

        Intent tIntent = new Intent(Login.this, AlipayGetLoginPassword.class);
        tIntent.putExtra(Constant.LOGINACCOUNT_CACHE, account);
        startActivity(tIntent);
    }

    @Override
    public View createTabContent(String arg0) {
        // TODO Auto-generated method stub
        final View tabContent = LayoutInflater.from(this).inflate(R.layout.alipay_login_320_480,
            null);
        return tabContent;
    }

    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
        	//在设置手势时禁止back
    		if (null != mPatternView && View.VISIBLE == mPatternView.getVisibility()) {
    			BaseHelper.tabOnKeyDown(this, keyCode, event);
    			return true;
    		}

            // referer from xml app.

            if (referer().equalsIgnoreCase(Defines.XML_APP)) {
                notifyReferer(false);
            }

    		if (goNextActivity()) {
    			return true;
    		}
    		
            setResult(RESULT_CANCELED);
            finish();
        }

        return false;
    }

    private void notifyReferer(boolean loginState) {
        // referer from xml app
        String referer = this.getIntent().getStringExtra(Defines.REFERER);
        if (referer != null && referer.equals(Defines.XML_APP)) {
            if (loginState)
                setResult(RESULT_OK);
            else {
                setResult(RESULT_CANCELED);
            }
            // synchronized (AppService.locksforAppService) {
            // AppService.retforAppServiceLogin = loginState;
            // AppService.locksforAppService.notify();
            // }
        }
    }

    /*
     * Get this file
     */
    private File isFile() {
        File file = new File(FILE_PATH + this.getPackageName() + "/files/userid/", FILE_NAME);
        return file;
    }

    private void addUserIdToRecent(String account, String password, String userId, String username,
                                   String phoneNo, String userAvatarPath) {
        
        //缓存此次登陆成功的用户pattern
    	mUserPattern = db.getPattern(account, mTaobao ? LOGINTYPE_TAOBAO : LOGINTYPE_ALIPAY);
    	if (null != mUserPattern) {
        	getUserData().setUserPattern(mUserPattern);
            mUserPattern = null;
    	}
    	
        try {
            if (mTaobao) {
                db.save(account, password, LOGINTYPE_TAOBAO, userId, username, phoneNo, userAvatarPath);
            } else {
                db.save(account, password, LOGINTYPE_ALIPAY, userId, username, phoneNo, userAvatarPath);
            }
            // db.close();
        } catch (Exception e) {
        	e.printStackTrace();
        }
    }

    /*
     * get all of user
     */
    private String[] getUserGroup() {
        File file = isFile();
        String[] tUser = null;
        if (file.exists()) {
            FileInputStream in;
            try {
                in = new FileInputStream(file);
                StringTokenizer tToken = new StringTokenizer(BaseHelper.convertStreamToString(in),
                    ",");
                if (tToken.countTokens() > 0)
                    tUser = new String[tToken.countTokens()];
                int count = 0;
                while (tToken.hasMoreTokens()) {
                    tUser[count++] = tToken.nextToken();
                }
            } catch (FileNotFoundException e) {

            }
        }
        return tUser;
    }

    @Override
    protected void onDestroy() {
        if (adapter.getCursor() != null)
            adapter.getCursor().close();
        db.close();
        super.onDestroy();
    }

    // add by renxl on 2010-08-19
    private boolean fromPubPay() {
        Intent intent = getIntent();
        String returnUrl = intent.getStringExtra(Constant.OP_RETURNURL);

        if (returnUrl != null) {
            Intent tIntent = new Intent();

            tIntent.putExtra(Constant.OP_RETURNURL, returnUrl);
            this.setResult(Constant.REQUEST_CODE_LOGIN, tIntent);
            this.finish();
            return true;
        }

        return false;
    }

    //
    // a callback for userlist delete operation.
    protected void onUserDelete() {
        // this.mUserNameEdit.setText("");
        // this.mPasswordEdit.setText("");
        // this.mPasswordCheckBox.setChecked(false);
        readSetting(true);
        Toast.makeText(this, getString(R.string.delete_success), Toast.LENGTH_SHORT).show();
    }

    class userlistItemClickListener implements OnItemClickListener {

        @Override
        public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
            String type;
            if (!mTaobao) {
                type = LOGINTYPE_ALIPAY;
            } else {
                type = LOGINTYPE_TAOBAO;
            }

            String userName = Login.this.mUserNameEdit.getText().toString();
            UserInfo userInfo = Login.this.db.getUser(userName, type);

            if (userInfo == null) {
                userInfo = new UserInfo();
                mUserPattern = null;
                mAutoLoginBefore = false;
            }
            else {
                mUserPattern = db.getPattern(userInfo.userAccount, type);
                mAutoLoginBefore = (Constant.AUTOLOGIN_YES == db.getAutoLogin(userInfo.userAccount, type));
            }
            // else ok.

            mUserNameEdit.setThreshold(100);
            mUserNameEdit.setText(userInfo.userAccount);
            BaseHelper.setDispayMode(mUserNameEdit, null);
            try {
                mPasswordEdit.setText(userInfo.randomNum);
            } catch (Exception e) {
                e.printStackTrace();
            }
            
            //mPasswordCheckBox.setChecked(userInfo.randomNum.length() > 0 ? true : false);
            //只有之前已经手动不勾选自动登录才会不勾选，否则一律勾选
            mPasswordCheckBox.setChecked((0 == db.getAutoLogin(userInfo.userAccount, type)) ? false : true);
        }
    }

    class UserEditOnKeyListener implements OnKeyListener {
        @Override
        public boolean onKey(View v, int keyCode, KeyEvent event) {
            if (event.getAction() == KeyEvent.ACTION_UP) {
                switch (keyCode) {
                    case KeyEvent.KEYCODE_ENTER:
                        //
                        // 跳到密码框
                        //
                        mPasswordEdit.requestFocusFromTouch();
                        return true;
                }
            }
            String userName = Login.this.mUserNameEdit.getText().toString();
            if (preUserName.equalsIgnoreCase(userName))
                return false;
            else {
                String type;
                if (!mTaobao) {
                    type = LOGINTYPE_ALIPAY;
                } else {
                    type = LOGINTYPE_TAOBAO;
                }
                try {
                    UserInfo preUserInfo = Login.this.db.getUser(preUserName, type);
                    preUserName = userName;

                    UserInfo userInfo = Login.this.db.getUser(userName, type);

                    if (userInfo == null && preUserInfo != null) {
                        userInfo = new UserInfo();
                        try {
                            mPasswordEdit.setText(Des.decrypt(userInfo.userPassword,
                                Constant.ALIPAY_INFO));
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        
                        //卡宝不再对此做处理，所以先注释掉
//                        mPasswordCheckBox
//                            .setChecked((userInfo.userPassword.length() > 0 || userInfo.rsaPassword
//                                .length() > 0) ? true : false);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                // else ok.

                Login.this.mUserNameEdit.setThreshold(1);
                return false;
            }
        }
    }

    public static final int REFRESH_UI = 0xff00ff00;

    public static final int SHOW_CHECKCODE_UI = 0xff00ff01;

    static class RefreshUiTask extends java.util.TimerTask {
        Login context;

        public RefreshUiTask(Login context) {
            this.context = context;
        }

        @Override
        public void run() {
            Message msg = new Message();
            msg.what = REFRESH_UI;
            context.mHandlerforRefreshUi.sendMessage(msg);
        }
    }

    private void checkSafepayUpdate() {
        // check to see if the MobileSecurePay is already installed.
        MobileSecurePayHelper mspHelper = new MobileSecurePayHelper();
        mspHelper.detectMobile_spUpdate(this,/* mHandler,*/
            AlipayHandlerMessageIDs.CHECK_UPDATE_FINISH,
            getResources().getString(R.string.UpdateLoginInfo));
    }

    private boolean gotoReqActivity() {
    	if (LOGIN_OK == mErrorType) {
    		UserData userData = getUserData();
    		if (null != userData) {
    			saveUser2DB(userData);
    		}	
    	}
    	
		if (!Constant.isDebug(this)) {
			try {
				TBS.updateUserAccount(mUserName);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
        Uri classNameUri = mIntent.getData();
        if (classNameUri != null && !classNameUri.equals("")) {
            String className = classNameUri.toString();
            if (SubItemPucPayActivity.class.getCanonicalName().equalsIgnoreCase(className)) {
                this.setResult(Activity.RESULT_OK, getIntent());
                finish();
                return true;
            }/*else if(TransferViewController.class.getCanonicalName().equalsIgnoreCase(className)){
            	this.setResult(, getIntent());
                finish();
                return true;
            }*/
        }

        // from web app
        Intent intent = getIntent();
        String from = intent.getStringExtra(Constant.OP_RETURNURL);
        if (from != null && from.equals(webActivity.WEBAPP)) {
            if (intent.getStringExtra(Constant.RES_PARAM_CALLBACKURL) == null) {
                Intent tIntent = new Intent(Login.this, webActivity.class);
                String url = intent.getStringExtra(webActivity.WEBAPP_URL);
                if (url == null) {
                    tIntent.putExtra(webActivity.WEBAPP_URL, Constant.getLotteryUrl(this)
                                                             + "?sessionId=" + getSessionId());
                } else
                    tIntent.putExtra(webActivity.WEBAPP_URL, url);
                tIntent.putExtra(webActivity.WEBAPP_NAME,
                    intent.getStringExtra(webActivity.WEBAPP_NAME));
                startActivity(tIntent);
            } else {
                intent.putExtra(webActivity.WEBAPP_URL, Constant.getLotteryUrl(this)
                                                        + "?sessionId=" + getSessionId());
                setResult(Activity.RESULT_OK, intent);
            }
            finish();
            return true;
        }

        // referer from xml app.
        if (referer().equalsIgnoreCase(Defines.XML_APP)) {
            notifyReferer(true);
            this.finish();
            return true;
        }

        if (fromPubPay()) {
            return true;
        }
        
        
        String appIdFrom = intent.getStringExtra("appId");//从未登录点击应用进入登录
		if (appIdFrom != null && !"".equals(appIdFrom)) {
			getIntent().putExtra("appId", appIdFrom);

			Intent tIntent = new Intent();
			tIntent.putExtra("appId", appIdFrom);
			this.setResult(RESULT_OK, tIntent);
			finish();
			return true;
		}

        return false;
    }
    
    private boolean goNextActivity() {
        String nextActivity = this.getIntent().getStringExtra(Defines.NextActivity);
        if (null != nextActivity) {
        	try {
				Class<?> clazz = Class.forName(nextActivity);
				Intent intent = new Intent(this, clazz);
				Uri uri = getIntent().getData();
				intent.setData(uri);
				intent.setAction(Intent.ACTION_MAIN);
				intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
				intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
				
				this.startActivity(intent);
				this.finish();
				return true;
			} catch (ClassNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
        }

        return false;
    }

    private String referer() {
        String referer = this.getIntent().getStringExtra(Defines.REFERER);
        if (referer == null)
            referer = "";

        return referer;
    }

    public void getCheckCodeBitmap(String url) {
        BitmapDownloadListener bitmapDownloadListener = new CheckCodeBitmapDownloadListener();
        Helper.bitmapFromUriString(Login.this, url, bitmapDownloadListener, R.drawable.app_default);
    }

	private void updateMethod(final HashMap<String, Object> map, String MessageText, final String extraData, final String logintime,
			final boolean isForce) {
		try {
			// 升级提示语更改
			String channel = CacheSet.getInstance(mActivity).getString(Constant.CHANNELS);
			AppUpgradeInterceptor upgradeInterceptor = InterceptorFactory.getInstance().getUpgradeInterceptor(channel);
			if (upgradeInterceptor != null) {
				String upgradeRemind = upgradeInterceptor.execUpgrade(mActivity);
				MessageText += "\n" + upgradeRemind;
			}

			getDataHelper().showDialog(this, R.drawable.infoicon, getResources().getString(R.string.WarngingString), MessageText,
					getResources().getString(R.string.Update), new DialogInterface.OnClickListener() {
						@Override
						public void onClick(DialogInterface dialog, int which) {
							
							//升级之前先保存用户信息
				    		UserData userData = getUserData();
				    		if (null != userData) {
				    			saveUser2DB(userData);
				    		}
				    		
							Object object = map.get(Constant.RPF_DOWNLOAD_URL);
							if (object != null) {
								String url = (String) object;
								UpdateHelper myUpdator = new UpdateHelper(Login.this);
								myUpdator.update(url);
								
								/*StorageStateInfo storageStateInfo = StorageStateInfo.getInstance();
								AlipayLogAgent.onEvent(Login.this, 
										Constants.MONITORPOINT_EVENT, 
										"", 
										"", 
										"",
										"",
										storageStateInfo.getValue(Constants.STORAGE_PRODUCTID), 
										Login.this.getUserId(), 
										storageStateInfo.getValue(Constants.STORAGE_PRODUCTVERSION), 
										storageStateInfo.getValue(Constants.STORAGE_CLIENTID),
										Constants.EVENTTYPE_UPDATECLICKED);*/
								
								
							}
						}
					}, getResources().getString(isForce ? R.string.MenuExit : R.string.next_time), new DialogInterface.OnClickListener() {
						@Override
						public void onClick(DialogInterface dialog, int which) {
							if (!isForce) {
								saveUpdateState();
								
								login2Main(extraData, logintime);

//								if (gotoReqActivity())
//									return;
//
//								setResult(RESULT_OK);
//								finish();
							}
							else {
								//强制升级退出客户端
								BaseHelper.exitProcessSilently(Login.this);
							}
						}
					}, null, null);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
    
    /**
     * 记录当前当前时间和网络状态
     */
    private void saveUpdateState() {
    	AlipayDataStore settings = new AlipayDataStore(this);
    	settings.putString(AlipayDataStore.LASTREJECTTIME, System.currentTimeMillis() +"");
    	settings.putString(AlipayDataStore.LASTNETSTATE, BaseHelper.is3GOrWifi(this) ? "wifi/3g" : "2g");
    	String updateTimes = settings.getString(AlipayDataStore.REJECTTIMES,"0");
    	settings.putString(AlipayDataStore.REJECTTIMES, Integer.parseInt(updateTimes) + 1 + "");
	}
    
    private void setCheckCodeImg(Bitmap bm) {
        mCheckCodeImg.setImageBitmap(bm);

        // 某些手机上会有crash问题，暂时关闭以便发布
        // if(tDisplayMetrics.widthPixels>320 &&
        // tDisplayMetrics.heightPixels>480)
        // {
        // // LinearLayout.LayoutParams m = new
        // LinearLayout.LayoutParams(mCheckCodeImg.getDrawable().getMinimumWidth()*3/2,mCheckCodeImg.getDrawable().getMinimumHeight()*3/2);
        // // m.addRule(RelativeLayout.RIGHT_OF, R.id.RegisterCheckPanel);
        // // m.addRule(RelativeLayout.);
        // // m.gravity=Gravity.CENTER_VERTICAL;
        // // mCheckCodeImg.setLayoutParams(m);
        //
        // RelativeLayout.LayoutParams layoutParams = new
        // RelativeLayout.LayoutParams(mCheckCodeImg.getDrawable().getMinimumWidth()*3/2,
        // mCheckCodeImg.getDrawable().getMinimumHeight()*3/2);
        // layoutParams.addRule(RelativeLayout.CENTER_VERTICAL);
        // mCheckCodeImg.setLayoutParams(layoutParams);
        // }
    }

    @Override
    public boolean preCancel(Command command) {
        return false;
    }

    @Override
    public void onCancel(Command command) {

    }

    @Override
    public boolean preFail(Command command) {
        return false;
    }

    @Override
    public void onFail(Command command) {
        closeProgress();
        if (command.getmId().equals(GET_RSAKEY_ID)) {
            setPublicKey(null);
            setTimeStamp(null);
        }
        resetStates();

        String content = command.getResponseMessage();
        //BaseHelper.showDialog(Login.this, getString(R.string.Error), content, R.drawable.erroricon);
        
        if (mAutoLogin) {
            login();
//    		Toast.makeText(this.getApplicationContext(), content,
//    				Toast.LENGTH_SHORT).show();
        }
        else {
//            Login.this.getDataHelper().showDialog(Login.this, R.drawable.erroricon, getString(R.string.Error), 
//            		content,
//    				getResources().getString(R.string.Ensure), new DialogInterface.OnClickListener() {
//    					@Override
//    					public void onClick(DialogInterface dialog, int which) {
//    						showLogin();
//    						}
//    					},
//    					null, null, null, null);
        }
        
//    	View toastView = LayoutInflater.from(this.getApplicationContext()).inflate(
//                R.layout.alipay_toast_view, null);
//    	TextView toastTextView = (TextView)toastView.findViewById(R.id.toastText);
//    	toastTextView.setText(R.string.networkfailed);
//        Toast toast = new Toast(this.getApplicationContext());
//        toast.setView(toastView);
//        toast.setGravity(Gravity.CENTER, 0, 0);
//        toast.setDuration(Toast.LENGTH_SHORT);
//        toast.show();
        getDataHelper().showToast(this, content);
    }

    @SuppressWarnings("unchecked")
    @Override
    public void onComplete(Command command) {
        //
        HashMap<String, Object> hmResponse = (HashMap<String, Object>) command.getResponseData();

        if (!mBackFilter.processCommand(hmResponse)) {
            if (command.getmId().equals(GET_RSAKEY_ID)) {
                mErrorType = LOGIN_OK;
                String string = (String) hmResponse.get(Constant.RPF_RSA_PK);
                if (string != null) {
                    setPublicKey(string);

                    processOriginalPassword(string.toString());
                }

                string = (String) hmResponse.get(Constant.RPF_RSA_TS);
                setTimeStamp(string);
                // send command to LogIn.
                loginMethod();
            } else if (command.getmId().equals(LOGIN_ID)) {
                closeProgress();

                mErrorType = LOGIN_OK;
                loginFinish(hmResponse);
            }
        } else {
            mErrorType = LOGIN_OK;
            if (command.getmId().equals(LOGIN_ID)) {
                getCheckCode(hmResponse);
            } else {
                setPublicKey(null);
                setTimeStamp(null);
            }
            closeProgress();
        }
    }

    // process original password in db
    private void processOriginalPassword(String rsaPucKey) {
        db.processPassword(rsaPucKey);
    }

    void closeProgress() {
        try {
            if (mProgress != null && mProgress.isShowing()) {
                mProgress.dismiss();
                mProgress = null;
            }
        } catch (Exception e) {
            // e.printStackTrace();
        }
    }

    void resetStates() {
        if (mErrorType == GET_RSA_KEY_START) {
            mErrorType = GET_RSA_KEY_FINISH;
        } else if (mErrorType == LOGIN_START) {
            mErrorType = LOGIN_FINISH;
        }
    }

    private void getCheckCode(HashMap<String, Object> hmResponse) {
        Object object = hmResponse.get(Constant.RPF_TAOBAO_CHECKCODEID);
        String checkCodeId = "";
        if (object != null)
            checkCodeId = (String) object;
        object = hmResponse.get(Constant.RPF_TAOBAO_CHECKCODEURL);
        String checkCodeUrl = "";
        if (object != null)
            checkCodeUrl = (String) object;
        if (checkCodeId.length() > 0 && checkCodeUrl.length() > 0) {
            mCheckCodeId = checkCodeId;
            mCheckCodeUrl = checkCodeUrl;
            mIsNeedShowCheckCode = true;
            getCheckCodeBitmap(mCheckCodeUrl);
        } else if (mIsNeedShowCheckCode) {
            getCheckCodeBitmap(mCheckCodeUrl);
        }

        // 校验码获取方式变更
        object = hmResponse.get(Constant.RPF_ALIPAY_CHECKCODEURL);
        String alipayCheckCodeUrl = "";
        if (object != null)
            alipayCheckCodeUrl = (String) object;
        if (alipayCheckCodeUrl.length() > 0) {
            mAlipayCheckCodeUrl = alipayCheckCodeUrl;
            mIsAlipayNeedShowCheckCode = true;
            getCheckCodeBitmap(mAlipayCheckCodeUrl);
        } else if (mIsAlipayNeedShowCheckCode) {
            getCheckCodeBitmap(mAlipayCheckCodeUrl);
        }
    }

    @SuppressWarnings("unchecked")
    private void loginFinish(HashMap<String, Object> hmResponse) {
        Helper.hideInputPanel(this.getContext(), this.getCurrentFocus());

        AlipayApplication application = (AlipayApplication) getApplicationContext();

        ((AlipayApp) this.getApplicationContext()).putGlobalData("bootFromPaipai", false);

        UserData userData = new UserData(application);
        //
        String errorText = hmResponse.get(Defines.memo).toString();
        Object obj = hmResponse.get(Constant.RQF_USER_NAME);
        if (obj != null) {
            userData.setRealName((String) obj);
        }
        
        userData.setCurrentAccountType(mTaobao);

		// 支付宝账户登录情况
		obj = hmResponse.get(Constant.RQF_LOGON_ID);
		if (obj != null) {
			userData.setAccountName((String) obj);
		}

		if (mTaobao) {
			mUserName = mUserNameEdit.getText().toString();
			if (mUserName != null && !mUserName.equals("")) {
				userData.setTaobaoAccountName(mUserName);
			}
		}
        
        obj = hmResponse.get(Constant.RPF_MOBILE_NO);
        if (obj != null) {
            userData.setMobileNo((String) obj);
        }
        obj = hmResponse.get(Constant.ISCERTIFIED);
        if (obj != null) {
            userData.setCertificate(obj.equals("Y"));
        }
        obj = hmResponse.get(Constant.RPF_LOGIN_TOKEN);
        if (obj != null) {
            userData.setToken((String) obj);
        }
        
        obj = hmResponse.get(Constant.RPF_TAOBAOSID);
        if(obj != null){
        	userData.setTaobaoSid((String) obj);
        }
        
        setUserData(userData);
        application.setFirstInTransfer(true);
        application.setMsgsRefresh(true);
        application.setBankListRefresh(true);
        application.setSavedCCRBankRefresh(true);

        // 条码支付使用的token，作为用户识别用
        Object barcodeToken = hmResponse.get(Constant.RPF_BARCODE_PAY_TOKEN);
        LogUtil.logOnlyDebuggable(TAG, "loginFinish barcodeToken:" + barcodeToken);
        if (barcodeToken != null && !barcodeToken.equals("")) {
            Constant.STR_BARCODETOKEN = (String) barcodeToken;
            Constant.STR_BARCODESWITCH = "true";
        }
        LogUtil.logOnlyDebuggable(TAG, "loginFinish STR_BARCODETOKEN:" + Constant.STR_BARCODETOKEN);

        // 条码支付二维码前缀，用于我要收付款的标识
        Object qrcodePrefix = hmResponse.get(Constant.RPF_BARCODE_QRCODE_PREFIX);
        LogUtil.logOnlyDebuggable(TAG, "loginFinish qrcodePrefix:" + qrcodePrefix);
        if (qrcodePrefix != null && !qrcodePrefix.equals("")) {
            Constant.STR_QRCODE_PREFIX = (String) qrcodePrefix;
        }

        Object userId = hmResponse.get(Constant.RPF_LOGIN_USERID);
        if (userId != null) {
            userData.setUserId((String) userId);
            StorageStateInfo.getInstance().putValue(Constants.STORAGE_USERID, (String)userId);
        }

        updateSetting(true);

        obj = hmResponse.get(Constant.STR_EXTERNTOKEN);
        if (obj != null) {
            userData.setExtToken((String) obj);
        }

        obj = hmResponse.get(Constant.RPF_MSG_COUNT);
        if (obj != null) {
            userData.setMsgCount(Utilz.parseInt(obj.toString(), 0));
        }

        /*
         * obj = hmResponse.get(Constant.RPF_TRADE_COUNT); if (obj != null) {
         * userData.setTradeCount(Integer.parseInt(obj.toString())); }
         */

        obj = hmResponse.get(Constant.RPF_EXTRADATA);
        String extraData = null;
        if (obj != null)
            extraData = (String) obj;
        obj = hmResponse.get(AlipayDataStore.LOGIN_SERVER_TIME);
        String loginServerTime = null;
        if (obj != null)
            loginServerTime = (String) obj;

        obj = hmResponse.get(Constant.RPF_SHOWMODULE);
        if (obj != null) {
            String xshsh_value = (String) ((HashMap<String, String>) obj)
                .get(Constant.RPF_SHOWMODULE_KEY_XSHSH);
            if (xshsh_value != null && xshsh_value.equals(Constant.RPF_SHOWMODULE_VALUE_Y)) {
                Constant.isShowQRCodeBar = true;
            } else {
                Constant.isShowQRCodeBar = false;
            }
        }

        // 更新全局用户信息
        UserInfo tCurUserInfo = getDataHelper().getLatestLoginedUser(this);
        if (tCurUserInfo != null) { // 本地成功登录账号存在
            curUserState.setUserInfo(tCurUserInfo);
            curUserState.setAccountFrom(AlipayUserState.ACCOUNT_TYPE_CLIENT);

            if (tCurUserInfo.type != null && tCurUserInfo.type.equals("alipay")) {
                curUserState.setUserState(AlipayUserState.ACCOUNT_STATE_AUTH_PREPARE);
            } else if (tCurUserInfo.type != null && tCurUserInfo.type.equals("taobao")
                       && userData.getAccountName() != null) {
                curUserState.setUserAccount(userData.getAccountName());
            }

            curUserState.setUserState(AlipayUserState.ACCOUNT_STATE_LOGIN);
        }

        // 获取本地保存的当前版本
        AlipayDataStore settings = new AlipayDataStore(this);
        String lastversion = settings.getString(AlipayDataStore.LASTUPDATEVERSION);
//        String lastUpdateTime = settings.getString(AlipayDataStore.LASTUPDATEVERSION);
        settings.putString(AlipayDataStore.QRCODE_PREFIX, Constant.STR_QRCODE_PREFIX);

        // 获取服务端提供的升级版本信息
        obj = hmResponse.get(Constant.STR_NEWVERSION);
        String isUpdate = null;
        if (obj != null)
            isUpdate = (String) obj;
        obj = hmResponse.get(Constant.RPF_CURENT_VERSION);
        String updateversion = null;
        if (obj != null)
            updateversion = (String) obj;
        
        if(!lastversion.equals(updateversion)){
        	resetUpdateVariables(settings, updateversion);
    	}
        
        // 提示升级
        if (isUpdate.compareTo("1") == 0) {
        	String rejectTimes = settings.getString(AlipayDataStore.REJECTTIMES,"0");
        	if("0".equals(rejectTimes)){//第一次
        		updateMethod(hmResponse, errorText, extraData, loginServerTime, false);
        	}else if("1".equals(rejectTimes)){//第二次
        		String lastRejectTime = settings.getString(AlipayDataStore.LASTREJECTTIME, "0");
            	String lastNetState = settings.getString(AlipayDataStore.LASTNETSTATE, "2g");
            	if("2g".equals(lastNetState) && BaseHelper.is3GOrWifi(this)){
            		updateMethod(hmResponse, errorText, extraData, loginServerTime, false);
            	}else if("wifi/3g".equals(lastNetState) && BaseHelper.is3GOrWifi(this)){
            		long timeIntervel = System.currentTimeMillis() - Long.parseLong(lastRejectTime);
            		if(timeIntervel >= 7 * 24 * 60 * 60 * 1000L){
            			updateMethod(hmResponse, errorText, extraData, loginServerTime, false);
            		}else
            			login2Main(extraData, loginServerTime);
            	}else 
            		login2Main(extraData, loginServerTime);
        	}else//提示两次之后不再提示
        		login2Main(extraData, loginServerTime);
        } else if (isUpdate.compareTo("2") == 0) {//强制升级
            updateMethod(hmResponse, errorText, extraData, loginServerTime, true);
        } else {//不需要升级
            login2Main(extraData, loginServerTime);
        }
        
        checkSafepayUpdate();
        
        VoucherSyncHelper voucherSyncHelper = VoucherSyncHelper.getInstance(this);
        if(voucherSyncHelper.isSyncing()){
        	voucherSyncHelper.syncFiles(getUserData().getAccountName());
        }
    }

	private void saveUser2DB(UserData userData) {
		UserInfo userInfo = db.getUser(mUserName, mTaobao ? LOGINTYPE_TAOBAO : LOGINTYPE_ALIPAY);
        if (userInfo == null) {
            userInfo = new UserInfo();
        }

        String rsaPucKey = getPublicKey();
        if (this.mPasswordCheckBox.isChecked()){
            if (userInfo.randomNum.equals(mPassword) /*|| !Utilz.isEmpty(userInfo.rsaPassword)*/) {
                addUserIdToRecent(mUserName, userInfo.rsaPassword, userData.getUserId(),
                    userData.getRealName(), userData.getMobileNo(),userData.getUserAvtarPath());
            } else {
                addUserIdToRecent(mUserName, RSA.encrypt(mPassword, rsaPucKey),
                    userData.getUserId(), userData.getRealName(), userData.getMobileNo(),userData.getUserAvtarPath());
            }
        }else
            addUserIdToRecent(mUserName, "", userData.getUserId(), userData.getRealName(),
                userData.getMobileNo(),userData.getUserAvtarPath());
        
        //缓存当前登录账户，淘宝账户登录时和userData的mAccountName中的值不一样
        userData.setLastLoginAccount(getUserNameShow(mUserName));
	}
	
	private String getUserNameShow(String userName) {
		if (mTaobao) {
			return Utilz.getSecuredNikName(userName);
		}
		
		String userNameShow = userName;
		if (Utilz.isPhoneNumber(userNameShow)) {
			userNameShow = userNameShow.substring(0, 3) + "****" + userNameShow.substring(7);
		} else {
	    	int atPos = userName.lastIndexOf("@");
	    	if (atPos > -1) {
	    		userNameShow = userNameShow.substring(0, atPos > 3 ? 3 : 1) + "***" + userNameShow.substring(atPos);
	    	}
		}
	
    	return userNameShow;
	}

	private void resetUpdateVariables(AlipayDataStore settings, String updateversion) {
		settings.putString(AlipayDataStore.LASTUPDATEVERSION, updateversion);
		settings.putString(AlipayDataStore.REJECTTIMES, "0");
		settings.putString(AlipayDataStore.LASTREJECTTIME, "0");
	}

	private boolean mSetPatternNeeded = false;
	private void login2Main(String extraData, String loginServerTime) {
		CacheManager cacheManager = CacheManager.getInstance(this);
		cacheManager.put(Constant.RPF_EXTRADATA, extraData);
		cacheManager.put(AlipayDataStore.LOGIN_SERVER_TIME, loginServerTime);
		
		//特殊情况下需要屏蔽手势设置(eg. 丁丁优惠)
        String patternNeeded = getIntent().getStringExtra(Constant.PATTERNNEED);
		if (patternNeeded != null) {
			login();
			cacheManager.put2Local("patternSkip", "true");
			return;
		}
		
		mSetPatternNeeded = false;
        mUserPattern = db.getPattern(mUserName, mTaobao ? LOGINTYPE_TAOBAO : LOGINTYPE_ALIPAY);
        
        String patternSkip = cacheManager.get("patternSkip");
        int autoLoginStatus = -1;
        if(patternSkip == null || "".equals(patternSkip)){
        	autoLoginStatus = db.getAutoLogin(mUserName, mTaobao ? LOGINTYPE_TAOBAO : LOGINTYPE_ALIPAY);
        }
       
        mAutoLoginBefore = (Constant.AUTOLOGIN_YES == autoLoginStatus);
		
		// 检测系统手势
		if (Utilz.isSystemPatternEnable(getContentResolver())) {
			mSetPatternNeeded = false;
		}
		else if (mPasswordCheckBox.isChecked() &&
				(0 == autoLoginStatus ||
						(-1 == autoLoginStatus && null == mUserPattern))) {
			mSetPatternNeeded = true;
		}
		// 之前不是自动登录，本次是自动登录，或者之前没有登录过，本次是自动登录;同时也要没有设置过手势
//    		else if (!mAutoLoginBefore && null == mUserPattern
//    				&& (null != mPasswordCheckBox && mPasswordCheckBox.isChecked())) {
//    			mSetPatternNeeded = true;
//    		}
		else if (mAutoLoginBefore && mForgetPatternPassword) {
			mSetPatternNeeded = true;
		}
		
		if (mForcePatternSetting && mPasswordCheckBox.isChecked()) {
			mSetPatternNeeded = true;
		}
		
		if (mSetPatternNeeded) {
			//设置手势
			initPattern(false);
			
			//防止在设置手势页面退出没有保存账户的情况
	    	if (LOGIN_OK == mErrorType) {
	    		UserData userData = getUserData();
	    		if (null != userData) {
	    			saveUser2DB(userData);
	    		}	
	    	}
	    	cacheManager.put2Local("patternSkip", "");
		}else if (null != mUserPattern && !mAutoLogin) {
			//验证手势
			mCheckPatternAfterLogin = true;
			initPattern(true);
		} else {
			login();
		}

	}

	public void doAfterFailedtoLogin() {
		if (mAutoLogin) {
			login();
		}
		else {
			showLogin();
		}
	}
	private void login() {
		if (gotoReqActivity()) {
		    return;
		}
		
		if (goNextActivity()) {
			return ;
		}
		
		setResult(RESULT_OK);
		finish();
	}

    @Override
    public void setRuleId(String ruleId) {
        // TODO Auto-generated method stub

    }

    @Override
    public String getRuleId() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public Context getContext() {
        return Login.this;
        
    }

    public void clearPassword() {
        mPasswordEdit.setText("");
    }
}
