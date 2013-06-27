package com.alipay.android.client;

import java.lang.reflect.Field;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.util.Log;
import android.view.InflateException;
import android.view.LayoutInflater;
import android.view.LayoutInflater.Factory;
import android.view.LayoutInflater.Factory2;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.alipay.android.appHall.appManager.HallData;
import com.alipay.android.client.baseFunction.More;
import com.alipay.android.client.baseFunction.MoreOnClickListener;
import com.alipay.android.client.constant.Constant;
import com.alipay.android.client.util.DataHelper;
import com.alipay.android.client.util.MenuHelper;
import com.alipay.android.common.data.ConfigData;
import com.alipay.android.common.data.UserData;
import com.alipay.android.comon.component.ProgressDiv;
import com.alipay.android.core.MBus;
import com.alipay.android.log.AlipayLogAgent;
import com.alipay.android.log.Constants;
import com.alipay.android.util.LogUtil;
import com.alipay.platform.core.AlipayApp;
import com.eg.android.AlipayGphone.R;

public class RootActivity extends Activity {
    private AlipayApplication mApplication;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        LogUtil.logAnyTime("step", "onCreate:" + this.getClass().getName());    
        super.onCreate(savedInstanceState);
        mApplication = (AlipayApplication) getApplicationContext();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        mApplication.onSaveSate();
    }

    private boolean mCountMe = true;
    public void countMeIn(boolean countMe) {
    	mCountMe = countMe;
    }
    
    private boolean mCountMeNotTemporary = false;
    public void countMeNotTemporary(boolean countMeNot) {
    	mCountMeNotTemporary = countMeNot;
    }
    @Override
    protected void onResume() {
        super.onResume();
        countMeForResume();
    }
    
    //主要针对像悦享拍加载时间较长情况
    public void countMeForResume() {

        mCountMeNotTemporary = false;
        
        if (mCountMe) {
            mApplication.setActivity(this);
            mApplication.stepForward();
            LogUtil.logAnyTime("step", "onResume:" + this.getClass().getName());	
        }
    }

    @Override
	protected void onUserLeaveHint() {
		super.onUserLeaveHint();
        if (mCountMe && !mCountMeNotTemporary) {
    		mApplication.stepBackward();
    		LogUtil.logAnyTime("step", "onUserLeaveHint:" + this.getClass().getName());
        }
	}

    public void doSimpleLog(Constants.BehaviourID behaviourID, String curViewID, String refViewID, String seed) {
    	AlipayLogAgent.writeLog(
    			this, 
    			behaviourID, 
    			null, null, 
    			null, null,
    			curViewID, 
    			refViewID, 
    			seed);
    }
    
    public void doSimpleLog(String appID, Constants.BehaviourID behaviourID, String curViewID, String refViewID, String seed) {
    	AlipayLogAgent.writeLog(
    			this, 
    			behaviourID, 
    			null, null, 
    			appID, null,
    			curViewID, 
    			refViewID, 
    			seed);
    }
    
    public void doSimpleLog(Constants.BehaviourID behaviourID, String behaviourStatus, String curViewID, String refViewID, String seed) {
    	AlipayLogAgent.writeLog(
    			this, 
    			behaviourID, 
    			behaviourStatus, null, 
    			null, null,
    			curViewID, 
    			refViewID, 
    			seed);
    }
    
    public void doSimpleLog(String appID, Constants.BehaviourID behaviourID, String behaviourStatus, String curViewID, String refViewID, String seed) {
    	AlipayLogAgent.writeLog(
    			this, 
    			behaviourID, 
    			behaviourStatus, null, 
    			appID, null,
    			curViewID, 
    			refViewID, 
    			seed);
    }

	/**
     * 登录用户数据
     */
    public UserData getUserData() {
        return mApplication.getUserData();
    }

    public void setUserData(UserData userData) {
        mApplication.setUserData(userData);
    }
    
    /**
     * 动画数据
     */
    public AnimationData getAnimationData() {
        return mApplication.getAnimationData();
    }

    public void setAnimationData(AnimationData animationData) {
        mApplication.setAnimationData(animationData);
    }

    public void logoutUser() {
        mApplication.logoutUser();
    }

    public String getUserId() {
        UserData userData = getUserData();
        return userData == null ? "" : userData.getUserId();
    }

    public String getExtToken() {
        UserData userData = getUserData();
        return userData == null ? getCertUserData().getExtToken() : userData.getExtToken();
    }

    public String getRealName() {
        UserData userData = getUserData();
        return userData == null ? "" : userData.getRealName();
    }

    public String getMobileNo() {
        UserData userData = getUserData();
        return userData == null ? "" : userData.getMobileNo();
    }

    public String getToken() {
        UserData userData = getUserData();
        return userData == null ? "" : userData.getToken();
    }

    public boolean isCertificate() {
        UserData userData = getUserData();
        return userData == null ? false : userData.isCertificate();
    }

	public boolean isDispathced() {
		return mApplication.isDispathced();
	}

	public void setDispathced(boolean dispathced) {
		mApplication.setDispathced(dispathced);
	}
    
    /**
     * @param containCert 是否包含认证用户
     * @return
     */
    public String getAccountName(boolean containCert) {
        UserData userData = getUserData();
        if (userData != null) {
            return userData.getAccountName();
        } else if (containCert) {
            return getCertUserData().getAccountName();
        } else {
            return "";
        }
    }

    /**
     * 配置数据
     */
    public ConfigData getConfigData() {
        mApplication = (AlipayApplication) getApplicationContext();
        return mApplication.getConfigData();
    }

    public String getPublicKey() {
        ConfigData configData = getConfigData();
        return configData.getPublicKey();
    }

    public void setPublicKey(String publicKey) {
        ConfigData configData = getConfigData();
        configData.setPublicKey(publicKey);
    }

    public String getTimeStamp() {
        ConfigData configData = getConfigData();
        return configData.getTimeStamp();
    }

    public void setTimeStamp(String timeStamp) {
        ConfigData configData = getConfigData();
        configData.setTimeStamp(timeStamp);
    }

    public String getSessionId() {
        ConfigData configData = getConfigData();
        return configData.getSessionId();
    }

    public void setSessionId(String sessionId) {
        ConfigData configData = getConfigData();
        configData.setSessionId(sessionId);
    }

    /**
     * 大厅数据
     */
    public HallData getHallData() {
        return mApplication.getHallData();
    }

    /**
     * 认证用户
     */
    public UserData getCertUserData() {
        return mApplication.getCertUserData();
    }

    public void setCertUserData(UserData certUserData) {
        mApplication.setCertUserData(certUserData);
    }

    public DataHelper getDataHelper() {
        return mApplication.getDataHelper();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == Constant.REQUEST_LOGIN_BACK) {
            if (resultCode == RESULT_CANCELED) {
                AlipayApp alipayApp = (AlipayApp) this.getApplicationContext();
                Object paipaiObject = alipayApp.getGlobalData("bootFromPaipai");
                if (paipaiObject != null && (Boolean) paipaiObject) {
                    return;
                }
                Intent intent = new Intent(this, Main.class);
                intent.setAction(Intent.ACTION_MAIN);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
                return;
            }
        }
    }

    public MBus getMBus() {
        return mApplication.getMBus();
    }

    private boolean mShowMenu = true;
    public void setShowMenu(boolean showMenu) {
    	mShowMenu = showMenu;
    }
    
    public boolean getShowMenu(){
    	return mShowMenu;
    }
    
	@Override
	public boolean onPrepareOptionsMenu(Menu menu) {
		int menuSize = menu.size();
		if (menuSize > 0) {
			MenuItem item = menu.getItem(menuSize - 1);
			if (null != item) {
				item.setTitle(null == this.getUserData() ? R.string.MenuNologin : R.string.MenuLogOut);
			}
		}

	    return menu.hasVisibleItems();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {

		if (!mShowMenu) {
			return super.onCreateOptionsMenu(menu);
		}
		
		boolean supportMenu = MenuHelper.setMemu(getLayoutInflater());
		if (!supportMenu) {
			return false;
		}
		
	    MenuInflater inflater = getMenuInflater();

	    try {
			inflater.inflate(null == this.getUserData() ? R.menu.menu_nologin : R.menu.menu_loggedin, menu);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	    return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		int itemId = item.getItemId();
		switch (itemId) {
			case R.id.update:
                final ProgressDiv progress = getDataHelper().showProgressDialogWithoutCancelButton(this, null,
                        this.getString(R.string.GettingInfo), false, true, null, null);
                
			    final Handler handler = new Handler() {
			        public void handleMessage(Message msg) {
			            boolean tResultOK = false;

			            tResultOK = new MessageFilter(RootActivity.this).process(msg);
			            if ((tResultOK) && (!getDataHelper().isCanceled())) {
			                ;
			            }

		                if (progress != null) {
		                	progress.dismiss();
		                }
		                
			            super.handleMessage(msg);
			        }
			    };
			    
				MoreOnClickListener.onClick(itemId, this, getDataHelper(), handler);
				break;
			case R.id.menuLogout:
				if (null != this.getUserData()) {
					MoreOnClickListener.onClick(itemId, this, null, null);
				} else if (!(this instanceof Login)){
                    Intent tIntent = new Intent(this, Login.class);
                    tIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(tIntent);
                    
                    if(!(this instanceof Main)) {
                    	finish();
                    }
				}
				break;
			case R.id.more:
				if (!(this instanceof More)) {
					Intent intent = new Intent();
			        intent.setClass(this, More.class);
			    	startActivity(intent);
				}

				break;
			default:
				MoreOnClickListener.onClick(itemId, this, null, null);
				
				break;
		}
		
		return super.onOptionsItemSelected(item);
	}
	protected void setMenuBackground(){                     
	    // Log.d(TAG, "Enterting setMenuBackGround");  
	    getLayoutInflater().setFactory( new LayoutInflater.Factory() {  
	        public View onCreateView(String name, Context context, AttributeSet attrs) {
	            if ( name.equalsIgnoreCase( "com.android.internal.view.menu.IconMenuItemView" ) ) {
	                try { // Ask our inflater to create the view  
	                    LayoutInflater f = getLayoutInflater();  
	                    Class layoutClass = TextView.class;
	                    Field[] fields = LayoutInflater.class.getDeclaredFields();
	                    for (Field eField : fields) {
	                    	Log.v("field", eField.getName());
	                    }
	                    
	                    View viewTemp = null;
						try {
							Field privateFactoryField = LayoutInflater.class.getDeclaredField("mPrivateFactory");
							privateFactoryField.setAccessible(true);
							Factory2 privateFactory = (Factory2)privateFactoryField.get(f);
							viewTemp = privateFactory.onCreateView(null, name, context, attrs);
						} catch (Exception e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
							Field factoryField = LayoutInflater.class.getDeclaredField("mFactory");
							factoryField.setAccessible(true);
							Factory factory = (Factory)factoryField.get(f);
							viewTemp = factory.onCreateView(name, context, attrs);
						}

						if (null != viewTemp) {
		                    final View menuitemView = viewTemp;
		                    menuitemView.post(new Runnable() {
								
								@Override
								public void run() {
									menuitemView.setBackgroundResource(R.drawable.btn_blue_b_bg);
								}
							});	
		                    
		                    return menuitemView;
						}
						
						return null;
	            }
	        catch ( InflateException e ) {}
	        catch (NoSuchFieldException e) {
				e.printStackTrace();
			}
	        catch (IllegalArgumentException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IllegalAccessException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
	   } 
	    return null;
	}});
	}
    
}