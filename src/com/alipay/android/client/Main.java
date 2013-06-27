package com.alipay.android.client;

import java.io.File;
import java.io.FileNotFoundException;
import java.lang.reflect.Field;
import java.nio.charset.Charset;
import java.util.List;
import java.util.Observable;
import java.util.Observer;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.BitmapFactory.Options;
import android.graphics.Shader;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.LayerDrawable;
import android.net.Uri;
import android.nfc.NdefMessage;
import android.nfc.NdefRecord;
import android.nfc.NfcEvent;
import android.os.Build;
import android.os.Build.VERSION;
import android.os.Bundle;
import android.os.SystemClock;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.alipay.android.appHall.Helper;
import com.alipay.android.appHall.appManager.AppItemInfo;
import com.alipay.android.appHall.appManager.HallData;
import com.alipay.android.appHall.common.CacheSet;
import com.alipay.android.appHall.uiengine.AppHallForAppCenter;
import com.alipay.android.appHall.uiengine.AppHallForHomePage;
import com.alipay.android.appHall.uiengine.CacheFrameLayout;
import com.alipay.android.client.baseFunction.AlipayDetailInfo;
import com.alipay.android.client.baseFunction.MessageList;
import com.alipay.android.client.constant.Constant;
import com.alipay.android.client.constant.PhoneConstant;
import com.alipay.android.client.manufacture.ManuConstants;
import com.alipay.android.client.util.AlipayDataStore;
import com.alipay.android.client.util.BaseHelper;
import com.alipay.android.client.util.DBHelper;
import com.alipay.android.client.util.LephoneConstant;
import com.alipay.android.client.util.MenuHelper;
import com.alipay.android.client.util.SmartBarUtils;
import com.alipay.android.common.data.AlipayUserState;
import com.alipay.android.common.data.MessageData;
import com.alipay.android.common.data.UserData;
import com.alipay.android.common.data.UserData.UDBillInfo;
import com.alipay.android.common.data.UserData.UDLifePayInfo;
import com.alipay.android.common.data.UserData.UDVoucherInfo;
import com.alipay.android.common.data.UserInfo;
import com.alipay.android.comon.component.CommonGallery;
import com.alipay.android.comon.component.StyleAlertDialog;
import com.alipay.android.log.AlipayLogAgent;
import com.alipay.android.log.Constants;
import com.alipay.android.serviceImpl.AliPassServiceImpl;
import com.alipay.android.ui.bean.Voucher;
import com.alipay.android.ui.bill.BillManagerRootControllerActivity;
import com.alipay.android.ui.card.CardManagerRootControllerActivity;
import com.alipay.android.ui.guide.AppGuide;
import com.alipay.android.ui.guide.GuideListener;
import com.alipay.android.ui.personlcard.PersonlCardRootControllerActivity;
import com.alipay.android.ui.voucher.VoucherIndexActivity;
import com.alipay.android.ui.widget.SlidingDrawer;
import com.alipay.android.ui.widget.SlidingDrawer.OnDrawerCloseListener;
import com.alipay.android.ui.widget.SlidingDrawer.OnDrawerOpenListener;
import com.alipay.android.util.LogUtil;
import com.eg.android.AlipayGphone.R;
import com.taobao.statistic.TBS;

public class Main extends RootActivity implements Observer, GuideListener {

    public static final String TAG = "Main";

    private SchemeHandler mSchemeHandler;

    protected static final int BLUETOOTH_REQUEST_DISCOVERABLE = 168;

    private AlipayDataStore dataStore;
    private CacheFrameLayout mCacheFrameLayout;
    /**
     * 账户区块：分为登录和未登录
     */
    private ImageView user_avatar;// 头像
    private RelativeLayout mUserInfoLayout;// 用来设置整体点击事件

    private ImageView plugImg;
    private ImageView rotateImg;

    private LinearLayout mLoginedArea;// 初始化之后显示
    private TextView mRealName;
    private TextView mAccountName;
    private TextView mBalanceHead;
    private TextView mBalanceEnd;

    private TextView mBankCardNumber;
    private LinearLayout mSystemMessageLayout;// 设置点击事件
    private ImageView mSystemMessageImage; // systemMessageImage
    private TextView mSystemMessageNumber;

    private LinearLayout moneyLayout;
    private LinearLayout bankCardLayout;

    private LinearLayout unLoginedArea;// 初始化之前显示

    private boolean intentSended = false;
    private boolean processScheme = false;

    // 账单区
    private RelativeLayout mBillLayout;
    private LinearLayout mUninitedBillLayout; // bill_uninited_layout
    private RelativeLayout mBillTopLayout;// bill_inited_top
    private RelativeLayout mBillBottomLayout;// bill_inited_bottom

    private TextView mUninitedBillText; // bill_uninited_content

    private TextView mBillType;// billtype
    private TextView mBillTime;// billtime
    private TextView mBillBalance;// billbalance
    private TextView mBillState;// billstate
    private TextView mBillCount;// billCount

    // 优惠券区
    private LinearLayout mUninitedCouponLayout;// coupon_uninited

    private TextView unInitedDefaultContent;// coupon_uninited_defaultContent

    private RelativeLayout mInitedCouponLayout;// coupon_inited
    private ImageView mCouponInitedImage;// coupon_inited_Image
    private TextView mCouponName;// coupon_name
    private TextView mCouponStatus;// voucher_status
    private TextView mCouponTimetip;// voucher_timetip

    private UserData userData;

    // 优惠券市场区处理
    // private AppHallForCouponMarket appHallForCouponMarket;

    // 4个app
    private AppHallForHomePage appHallForHomePage;
    private LinearLayout appsShortcut;

    private AlipayUserState curUserState = AlipayUserState.getInstance();
    private DBHelper db;
    NfcCallBack nfcCallback;

    String FILE_NAME = "alipay_client";

    private SlidingDrawer mAppSlidingDrawer;
    private GridView mAppsGridView;
    private View mHandle;
	
	private int lifePayCount = 0;

    private ImageView mOverlay1;
    private ImageView mOverlay2;
    private ImageView mOverlay3;
    private ImageView mOverlay4;

    private AlipayApplication application;
    private AliPassServiceImpl mAliPassServiceImpl; 

    @Override
    public void onGuideComplete() {
        mGuideGallery.setVisibility(View.GONE);
    }

    private AppGuide appGuide;
    private String GUIDE_PATH = "homepage_guide";
    private CommonGallery mGuideGallery;// 向导Guide页面
    private String mExtraData;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setDispathced(false);
    	Uri uri = getIntent().getData();
		if(uri != null)
			mExtraData = uri.toString(); 
        if ("mx2".equals(android.os.Build.DEVICE)) {
            setTheme(R.style.AlipayMainTheme);
        }

        super.onCreate(savedInstanceState);

        Helper.getDisplayMetrics(this);

        dataStore = new AlipayDataStore(this);

        mSchemeHandler = new SchemeHandler(this);

        if (LephoneConstant.isLephone()) {
            getWindow().addFlags(LephoneConstant.FLAG_ROCKET_MENU_NOTIFY);
        }

        Constant.MobileCompany = getResources().getString(R.string.useragent);

        if ("mx2".equals(android.os.Build.DEVICE)) {
            Constant.isMZ = true;
            getActionBar().setDisplayOptions(0);
			SmartBarUtils.setActionBarViewCollapsable(getActionBar(), true);
        } else {
            requestWindowFeature(Window.FEATURE_NO_TITLE);
        }

        setContentView(R.layout.mainactivity_layout);
        // getWindow().setBackgroundDrawableResource(android.R.color.white);

        if ((Integer.parseInt(VERSION.SDK) > 13)
            && this.getPackageManager().hasSystemFeature(PackageManager.FEATURE_NFC)) {
            nfcCallback = new NfcCallBack(this, this);
        }
        // mPSKSoundControler = new PSKMain(this);

        if (!Constant.isDebug(this)) {
            try {
                TBS.Page.create(Main.class.getName(), "Main");
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        loadAllVariables(savedInstanceState);

        appGuide = new AppGuide(this, GUIDE_PATH, false, AlipayDataStore.TRANSFERVERSION);

		if(appGuide.needShowGuide() && mExtraData == null){
            appGuide.initGuide(mGuideGallery);
            appGuide.setOnCompleteListener(this);
        }

        Helper.getDisplayMetrics(this);
    }

    private void loadAllVariables(Bundle savedInstanceState) {
        db = new DBHelper(this);
        mAliPassServiceImpl = new AliPassServiceImpl(this);
        
        mGuideGallery = (CommonGallery) findViewById(R.id.guideGallery);
        mOverlay1 = (ImageView) findViewById(R.id.overlay1);
        mOverlay2 = (ImageView) findViewById(R.id.overlay2);
        mOverlay3 = (ImageView) findViewById(R.id.overlay3);
        mOverlay4 = (ImageView) findViewById(R.id.overlay4);

        plugImg = (ImageView) findViewById(R.id.plugImg);
        plugImg.setVisibility(View.INVISIBLE);

        rotateImg = (ImageView) findViewById(R.id.rotateImg);
        rotateImg.setVisibility(View.INVISIBLE);

        application = (AlipayApplication) getApplication();

        appHallForHomePage = new AppHallForHomePage(this);
        appsShortcut = (LinearLayout) findViewById(R.id.appsShortcut);
        appHallForHomePage.setContainerViewForHomePage(appsShortcut);

        mUserInfoLayout = (RelativeLayout) findViewById(R.id.item_userinfo);
        mLoginedArea = (LinearLayout) findViewById(R.id.loginedArea);
        unLoginedArea = (LinearLayout) findViewById(R.id.unloginedArea);
        mBankCardNumber = (TextView) findViewById(R.id.bankCardNumber);
        mSystemMessageNumber = (TextView) findViewById(R.id.systemMessageNumber);

        moneyLayout = (LinearLayout) findViewById(R.id.money);
        bankCardLayout = (LinearLayout) findViewById(R.id.bankCard);
        bankCardLayout.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent tIntent = new Intent(Main.this, CardManagerRootControllerActivity.class);
                mResetUserStatus = false;
                startActivity(tIntent);
                AlipayLogAgent.writeLog(Main.this, Constants.BehaviourID.CLICKED, null, null,
                    Constants.WALLETACCOUNT, null, Constants.BANKCARDLIST, Constants.WALLETHOME,
                    Constants.BANKCARDICON, "");
            }
        });

        mAccountName = (TextView) findViewById(R.id.user_account);
        mCacheFrameLayout = (CacheFrameLayout) findViewById(R.id.cachecontent);
        mRealName = (TextView) findViewById(R.id.realName);
        mBalanceHead = (TextView) mUserInfoLayout.findViewById(R.id.user_balance_head);
        mBalanceEnd = (TextView) mUserInfoLayout.findViewById(R.id.user_balance_end);

        user_avatar = (ImageView) findViewById(R.id.user_avatar);
        user_avatar.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                if (userData != null) {

                    AlipayLogAgent.writeLog(Main.this, Constants.BehaviourID.CLICKED, null, null,
                        Constants.WALLETACCOUNT, null, Constants.MYNAMECARD, Constants.WALLETHOME,
                        Constants.FACEBOOKICON, "");

                    Intent intent = new Intent(Main.this, PersonlCardRootControllerActivity.class);
                    mResetUserStatus = false;
                    intent.putExtra("pageFrom", "MainPage");
                    startActivity(intent);
                } else {
                    doLoginClickListener.onClick(null);
                }
            }
        });

        // 账单区
        mBillLayout = (RelativeLayout) findViewById(R.id.item_Bill);
        mUninitedBillLayout = (LinearLayout) findViewById(R.id.bill_uninited_layout);
        mUninitedBillText = (TextView) findViewById(R.id.bill_uninited_content);
        mBillTopLayout = (RelativeLayout) findViewById(R.id.bill_inited_top);
        mBillBottomLayout = (RelativeLayout) findViewById(R.id.bill_inited_bottom);
        mBillType = (TextView) findViewById(R.id.billtype);
        mBillTime = (TextView) findViewById(R.id.billtime);
        mBillBalance = (TextView) findViewById(R.id.billbalance);
        mBillState = (TextView) findViewById(R.id.billstate);
        mBillCount = (TextView) findViewById(R.id.billCount);
        // 优惠券区
        mUninitedCouponLayout = (LinearLayout) findViewById(R.id.coupon_uninited);
        unInitedDefaultContent = (TextView) findViewById(R.id.coupon_uninited_defaultContent);
        mInitedCouponLayout = (RelativeLayout) findViewById(R.id.coupon_inited);
        mCouponInitedImage = (ImageView) findViewById(R.id.coupon_inited_Image);
        mCouponName = (TextView) findViewById(R.id.coupon_name);
        mCouponName.setMaxLines(2);
        mCouponStatus = (TextView) findViewById(R.id.voucher_status);
        mCouponTimetip = (TextView) findViewById(R.id.voucher_timetip);

        mAppSlidingDrawer = (SlidingDrawer) findViewById(R.id.appslidingDrawer);

        mAppsGridView = (GridView) findViewById(R.id.apps_grid);
        new AppHallForAppCenter(this).setContainerViewForAppCenter(mAppsGridView);
        mHandle = mAppSlidingDrawer.getHandle();

        mAppSlidingDrawer.setOnDrawerCloseListener(new OnDrawerCloseListener() {// 收回做一些处理

                @Override
                public void onDrawerClosed() {
                    isAppCenterOpen = false;
                    mAppSlidingDrawer.setBackgroundColor(0x00000000);
                }
            });

        mAppSlidingDrawer.setOnDrawerOpenListener(new OnDrawerOpenListener() {// 展开时做一些处理

                @Override
                public void onDrawerOpened() {
                    isAppCenterOpen = true;
                    mAppSlidingDrawer.setBackgroundColor(0x995d4f49);
                    AlipayLogAgent.writeLog(Main.this, Constants.BehaviourID.CLICKED, null, null,
                        Constants.WALLETAPPSHOW, null, Constants.APPCENTER, Constants.WALLETHOME,
                        Constants.HOMEAPPSHOWICON, "");
                    
                    final HallData hallData = getHallData();
                    if(hallData.getState()==HallData.STATE_SYNC_FAIL){//失败需要继续请求
                        getDataHelper().showToast(Main.this, getString(R.string.sync_apps_ing));
                        application.executeTask(new Runnable() {
                            public void run() {
                                hallData.syncAppStatus();
                            }
                        });
                    }
                }
            });
        mSystemMessageImage = (ImageView) findViewById(R.id.systemMessageImage);
        mSystemMessageLayout = (LinearLayout) findViewById(R.id.systemMessage);
        mSystemMessageLayout.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (getUserData() == null) {
                    return;
                }
                jumpToMessageList(Main.this);
                AlipayLogAgent.writeLog(Main.this, Constants.BehaviourID.CLICKED, null, null,
                    Constants.WALLETACCOUNT, null, Constants.MSGLIST, Constants.WALLETHOME,
                    Constants.MSGICON, "");
            }
        });

        // 获取传感器管理服务

        if (getUserData() == null) {
            AlipayDataStore settings = new AlipayDataStore(this);

            String sam_login = settings.getString(AlipayDataStore.SAM_FIRST);
            if (PhoneConstant.isMoto) {
                String license = settings.getString(AlipayDataStore.DISPLAY_LICENSE);
                if (!license.equals("1")) {
                    displayLicense();
                }
            }

            if (!sam_login.equals("1")) {
                if (PhoneConstant.isSam) {
                    getDataHelper().showDialog(this, 0, "",
                        getResources().getString(R.string.AboutCopyrightDialog),
                        getResources().getString(R.string.Ensure), null, null, null, null, null);
                }
            }

            // 根据channel信息确定是否需要创建快捷方�?
            String curChannel = CacheSet.getInstance(this).getString(Constant.CHANNELS);

            String man = "";
            try {
                Field mField = Build.class.getDeclaredField("MANUFACTURER");
                mField.setAccessible(true);
                man = mField.get(Build.class).toString();
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            } catch (SecurityException e) {
                e.printStackTrace();
            } catch (NoSuchFieldException e) {
                e.printStackTrace();
            }

			if (!ManuConstants.ZTENC.equals(curChannel)
					&& !man.toLowerCase().contains("meizu")
					&& mExtraData == null) 
                initShortcut();
            }

        if (savedInstanceState == null) {
            processScheme = true;
        }

        imageRepeat();
        initLayoutAndListener(getUserData());
    }

    private void imageRepeat() {
        LayerDrawable drawable = (LayerDrawable) mAppsGridView.getBackground();
        if (drawable!=null) {//大黄蜂手机获取到的drawable为null
        	LayerDrawable layerDrawable = (LayerDrawable) drawable.getDrawable(0);
            ((BitmapDrawable) layerDrawable.getDrawable(0)).setTileModeXY(Shader.TileMode.REPEAT,
                Shader.TileMode.REPEAT);	
            LayerDrawable bgLayerDrawable = (LayerDrawable) drawable.getDrawable(1);
            ((BitmapDrawable) bgLayerDrawable.getDrawable(0)).setTileModeXY(Shader.TileMode.REPEAT,
            		Shader.TileMode.REPEAT);
		}


        BaseHelper.fixBackgroundRepeat(mOverlay1);
        BaseHelper.fixBackgroundRepeat(mOverlay2);
        BaseHelper.fixBackgroundRepeat(mOverlay3);
        BaseHelper.fixBackgroundRepeat(mOverlay4);

    }

    private boolean isAppCenterOpen;

    public Bitmap getLastUserAvtar(String mUserAvtar) {
        Options options = new Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(mUserAvtar, options);
        int dw = options.outWidth / 120;
        int dh = options.outHeight / 120;
        int scale = Math.max(dw, dh);

        options = new Options();
        options.inDensity = (int) Helper.getDensityDpi(this);
        options.inScaled = true;
        options.inSampleSize = scale;

        try {
            return BitmapFactory.decodeFile(mUserAvtar, options);
        } catch (Error e) {
            // Auto-generated catch block
            e.printStackTrace();
        }

        return null;
    }

    private final static String TAG_ACCOUNTINFO = "tag_accountinfo";
    private final static String TAG_BILLINFO = "tag_billinfo";
    private final static String TAG_COUPONINFO = "tag_couponinfo";

    private void initLayoutAndListener(UserData mUserData) {
        UserInfo lastLoginUser = null;

        db.open(this);
        lastLoginUser = db.getLastLoginUser(null);
        moneyLayout.setVisibility(View.INVISIBLE);
        bankCardLayout.setVisibility(View.INVISIBLE);

        mUserInfoLayout.setTag(TAG_ACCOUNTINFO);
        mBillLayout.setTag(TAG_BILLINFO);
        mUninitedCouponLayout.setTag(TAG_COUPONINFO);
        mInitedCouponLayout.setTag(TAG_COUPONINFO);

        if (mUserData == null) {
            if (lastLoginUser == null) {
                // 优惠券区使用默认图片，点击进入券市场
                mLoginedArea.setVisibility(View.GONE);
                unLoginedArea.setVisibility(View.VISIBLE);
                mUninitedBillLayout.setVisibility(View.VISIBLE);
                mBillTopLayout.setVisibility(View.GONE);
                mBillBottomLayout.setVisibility(View.GONE);
                mUninitedCouponLayout.setVisibility(View.VISIBLE);
                mInitedCouponLayout.setVisibility(View.GONE);
                mBillLayout.setOnClickListener(doLoginClickListener);
                mUninitedCouponLayout.setOnClickListener(doLoginClickListener);
                user_avatar.setImageResource(R.drawable.default_avatar);
            } else {
                // 读取对应账户上次缓存的券列表的第一个项，点击进行登录；没有显示默认图片，点击进入券市场列表
                String lastLoginUserAccount = "";
                String lastLoginUserRealName = "";
                String lastLoginUserAvtarPath = "";
                String lastLoginUserId = "";
                if (lastLoginUser != null) {
                    lastLoginUserAccount = lastLoginUser.userAccount;
                    lastLoginUserId = lastLoginUser.userId;
                    lastLoginUserRealName = lastLoginUser.userName;
                    lastLoginUserAvtarPath = dataStore.getString(
                        lastLoginUserId + ":" + AlipayDataStore.LASTLOGINUSERAVTARPATH, "");
                }

                user_avatar.setImageResource(R.drawable.default_avatar);
                mLoginedArea.setVisibility(View.VISIBLE);
                unLoginedArea.setVisibility(View.GONE);
                mUninitedBillLayout.setVisibility(View.VISIBLE);
                mBillTopLayout.setVisibility(View.GONE);
                mBillBottomLayout.setVisibility(View.GONE);

                mUninitedCouponLayout.setVisibility(View.VISIBLE);
                mInitedCouponLayout.setVisibility(View.INVISIBLE);
                mUninitedCouponLayout.setOnClickListener(doLoginClickListener);
                unInitedDefaultContent = (TextView) findViewById(R.id.coupon_uninited_defaultContent);
                lastLoginUserAccount = BaseHelper.setUnloginAccount(lastLoginUserAccount);
                mAccountName.setText(lastLoginUserAccount);
                if (lastLoginUserRealName == null || lastLoginUserRealName.equals("")) {
                    mRealName.setText("你好，欢迎回来");
                } else {
                    mRealName.setText(lastLoginUserRealName);
                }

                Bitmap mAvtar = null;
                if (lastLoginUserAvtarPath != null && !"".equals(lastLoginUserAvtarPath)) {
                    mAvtar = getLastUserAvtar(lastLoginUserAvtarPath);
                }
                if (mAvtar != null && lastLoginUserAvtarPath != null
                    && !"".equals(lastLoginUserAvtarPath)) {
                    user_avatar.setImageBitmap(mAvtar);
                }

                mBillLayout.setOnClickListener(doLoginClickListener);
            }

            plugImg.setVisibility(View.INVISIBLE);
            rotateImg.setVisibility(View.INVISIBLE);

            mUninitedBillText.setText("点击这里显示你的交易／账单");
            unInitedDefaultContent.setText("点击这里查看你的卡券");

            mSystemMessageImage.setVisibility(View.INVISIBLE);
            mBillCount.setVisibility(View.GONE);
            // mSystemMessageLayout.setVisibility(View.INVISIBLE);
            mSystemMessageNumber.setVisibility(View.INVISIBLE);
            mUserInfoLayout.setOnClickListener(doLoginClickListener);
        } else {// userData不为null
            rotateImg.setVisibility(View.VISIBLE);
            mUserInfoLayout.setOnClickListener(gotoAccountManagerListener);

            // TODO 这里需要更改 搜索 mVoucherInfo
            UDVoucherInfo mVoucherInfo = mUserData.getUDVoucherInfo();

            if (null == mVoucherInfo || mVoucherInfo.has_no_voucher) {// 没有卡券的情况                  	
            	
            	AppItemInfo couponMarketInfo = getHallData().getAppConfig(HallData.COUPON_MARKET);       
            	
            	if (couponMarketInfo != null)
            	{
	                unInitedDefaultContent.setText( getResources().getString(R.string.tipsForUnInitedVoucher)
	                		.replace( "%s", couponMarketInfo.getNameText()));
	                mUninitedCouponLayout.setVisibility(View.VISIBLE);
	                mInitedCouponLayout.setVisibility(View.GONE);
	                mUninitedCouponLayout.setOnClickListener(gotoMyCouponClickListener);
            	}
            } else if (mVoucherInfo.only_cantUse_voucher) {// 只有已使用券
                unInitedDefaultContent.setText("你已没有可用卡券");
                mUninitedCouponLayout.setVisibility(View.VISIBLE);
                mInitedCouponLayout.setVisibility(View.GONE);
                mUninitedCouponLayout.setOnClickListener(gotoMyCouponClickListener);
            } else {// 有我的券
                List<Voucher> mVoucherList = mixVoucherList(mVoucherInfo);

                if (mVoucherList != null && mVoucherList.size() > 0) {
                	Voucher mVoucher = mVoucherList.get(0);
                    mVoucher.addObserver(this);
                    Bitmap mVoucherBitmap = getVoucherLogo(mVoucher,true);
                    
                    if (mVoucher != null) {// 有我的券
                        mCouponName.setText(mVoucher.getVoucherName());
                        if (mVoucherBitmap != null) {
                            mCouponInitedImage.setImageBitmap(mVoucherBitmap);
                        }
                        mUninitedCouponLayout.setVisibility(View.GONE);
                        mInitedCouponLayout.setVisibility(View.VISIBLE);
                        mInitedCouponLayout.setOnClickListener(gotoMyCouponClickListener);
                    }
                }
            }
            mSystemMessageNumber.setVisibility(View.VISIBLE);
            mSystemMessageImage.setVisibility(View.VISIBLE);
            mUserInfoLayout.setOnClickListener(gotoAccountManagerListener);
        }
    }
    
	private List<Voucher> mixVoucherList(UDVoucherInfo mVoucherInfo) {
		JSONArray voucherArray = null;
		if(hasCacheVoucher(mVoucherInfo)){
			try {
				voucherArray = new JSONArray(mVoucherInfo.mVoucherList);
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}
		
		return mAliPassServiceImpl.mixLocalAndOnlineVoucher(getUserData().getAccountName(), voucherArray);
	}

	private boolean hasCacheVoucher(UDVoucherInfo mVoucherInfo) {
		return mVoucherInfo != null && mVoucherInfo.mVoucherList != null
			 && mVoucherInfo.mVoucherList.length() > 0 && !"[]".equals(mVoucherInfo.mVoucherList);
	}
    
    private void setLayoutAndListener(UserData mUserData) {

        if (userData != null && !userData.getIsQueryBalanceSuccess()) {
            // 去账户管理
            mUserInfoLayout.setOnClickListener(gotoAccountManagerListener);
            // TODO 去账单列表
            mBillLayout.setOnClickListener(gotoBillClickListener);
            if (mUninitedCouponLayout.getVisibility() == View.VISIBLE) {
                mUninitedCouponLayout.setOnClickListener(gotoMyCouponClickListener);
            }
            if (mInitedCouponLayout.getVisibility() == View.VISIBLE) {
                mInitedCouponLayout.setOnClickListener(gotoMyCouponClickListener);
            }
            return;
        }

        if (mUserData != null) {
            mLoginedArea.setVisibility(View.VISIBLE);
            unLoginedArea.setVisibility(View.GONE);
            mUninitedBillLayout.setVisibility(View.GONE);
            mBillTopLayout.setVisibility(View.VISIBLE);
            mBillBottomLayout.setVisibility(View.VISIBLE);
            mUninitedCouponLayout.setVisibility(View.GONE);
            mInitedCouponLayout.setVisibility(View.VISIBLE);

            // 如果没有账单信息，展示默认图片；如果有账单信息展示第一条
            // 处理账单信息
            UDBillInfo mBillInfo = mUserData.getUDBillInfo();
			UDLifePayInfo mLifePayInfo = mUserData.getUDLifePayInfo();
			int intTotalCount = 0;
			
			if (mBillInfo != null) {
				String strBillCount = mBillInfo.mWaitPayBillCount;
				try {
					if(strBillCount != null && !"".equals(strBillCount))
						intTotalCount = Integer.parseInt(strBillCount);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
			if(mLifePayInfo != null)
			{
				String strLifePayCount = mLifePayInfo.mTotalLifePayCount;
				try {
					lifePayCount = Integer.parseInt(strLifePayCount);
					intTotalCount += lifePayCount;
				} catch (Exception e) {
					e.printStackTrace();
				}
			}

			if (intTotalCount > 0) {
				mBillCount.setText("" + intTotalCount);
				mBillCount.setVisibility(View.VISIBLE);
			} else {
				mBillCount.setVisibility(View.GONE);
			}			
			
            if (mBillInfo != null
                && (mBillInfo.mTotalBillCount == null || "0".equals(mBillInfo.mTotalBillCount))
                && intTotalCount <= 0) {
                // 账单为0的显示情况
                mUninitedBillLayout.setVisibility(View.VISIBLE);
                mBillTopLayout.setVisibility(View.GONE);
                mBillBottomLayout.setVisibility(View.GONE);
                mUninitedBillText = (TextView) findViewById(R.id.bill_uninited_content);
                mUninitedBillText.setText("你还没消费过,记录空空的...");

            } else if (mBillInfo != null && mBillInfo.mBillList != null
    				&& mBillInfo.mBillList != "") {
    			JSONArray billArray = null;
    			JSONObject billInfo = null;
    			String billType = null;
    			String selected = mLifePayInfo.mFirstShow;
    			try {
    				billArray = new JSONArray(mBillInfo.mBillList);
    			} catch (JSONException e) {
    				e.printStackTrace();
    			}
    			
    			if(selected.length() > 0 && billArray != null)
    			{
    				for(int i=0; i<billArray.length(); i++)
    				{
    					try
    					{
    						billInfo = billArray.getJSONObject(i);
    					}
    					catch (JSONException e)
    					{
    						e.printStackTrace();
    					}
    					
    					if(billInfo != null 
    							&& (billInfo.optString("tradeNO").equals(selected) || billInfo.optString("id").equals(selected)))
    					{
    						billType = billInfo.optString("billType");
    						break;
    					}
    					
    					billInfo = null;
    				}
    			
    			
    				if (billInfo != null)
    				{
    					if(Integer.parseInt(billType) == 2)
    					{
    						String act = billInfo.optString("act");
    						if (act.indexOf("买") > -1) {
    							act = "-";
    						} else if (act.indexOf("卖") > -1) {
    							act = "+";
    						}

    						mBillType.setText(billInfo.optString("goodsName"));
    						String billTime = billInfo.optString("tradeTime");
    						mBillTime.setText(billTime);
    						mBillBalance.setText(act + billInfo.optString("tradeMoney"));
    						AlipayDetailInfo mDetailInfo = makeItem(billInfo, billType);
    						if (mDetailInfo != null) {
    							mDetailInfo.setStatusColor(mBillState);
    						}
    					}
    					else if(Integer.parseInt(billType) == 1)
    					{
    						String act = "-";
    						mBillType.setText(billInfo.optString("showTitle", ""));
    						mBillTime.setText(billInfo.optString("billDate", ""));
    						mBillBalance.setText(act + billInfo.optString("billAmount", "") 
    								+ getResources().getString(R.string.Yuan));
    						AlipayDetailInfo mDetailInfo = makeItem(billInfo, billType);
    						if (mDetailInfo != null) {
    							mDetailInfo.setStatusColor(mBillState);
    						}
    					}
    				}
    				else
    				{
    					mUninitedBillLayout.setVisibility(View.VISIBLE);
    					mBillTopLayout.setVisibility(View.GONE);
    					mBillBottomLayout.setVisibility(View.GONE);
    					mUninitedBillText = (TextView) findViewById(R.id.bill_uninited_content);
    					mUninitedBillText.setText("交易/账单");
    				}
    			}
    			else
    			{
    				mUninitedBillLayout.setVisibility(View.VISIBLE);
    				mBillTopLayout.setVisibility(View.GONE);
    				mBillBottomLayout.setVisibility(View.GONE);
    				mUninitedBillText = (TextView) findViewById(R.id.bill_uninited_content);
    				mUninitedBillText.setText("交易/账单");
    			}
    		}

            // 有你的卡券，点击进入你的卡券；如果没有我的券，余额接口有推荐券，显示券，点击进入你的卡券市场；再没有推荐券，显示默认图，点击进券市场
            // 处理优惠券信息
            UDVoucherInfo mVoucherInfo = mUserData.getUDVoucherInfo();
            List<Voucher> mixedVoucherList = mixVoucherList(mVoucherInfo);
			if(mixedVoucherList.size() > 0){
                Bitmap mVoucherBitmap = null;
                Voucher firstVoucher = mixedVoucherList.get(0);
                firstVoucher.addObserver(this);
                mVoucherBitmap = getVoucherLogo(firstVoucher,true);
				
				if(firstVoucher.isAvailabelVoucher()){
					String merchantName = firstVoucher.getMerchantName();
					if(merchantName != null && !"".equals(merchantName)){
						mCouponName.setText(merchantName + "\r\n"+ firstVoucher.getVoucherName());
					}else
						mCouponName.setText(firstVoucher.getVoucherName());
					if (mVoucherBitmap != null) {
						mCouponInitedImage.setImageBitmap(mVoucherBitmap);
					}

					String couponStatus = firstVoucher.getStatus();
					if (couponStatus == null || "".equals(couponStatus)) {
						couponStatus = "";
					} else {
						couponStatus = Voucher.Status.STATUSMAP
								.get(couponStatus);
					}
					mCouponStatus.setText(couponStatus);

					Integer leftDays = firstVoucher.getLeftDays();
					if(leftDays == -1){
						mCouponTimetip.setVisibility(View.INVISIBLE);
					}else {
						mCouponTimetip.setVisibility(View.VISIBLE);
						if(leftDays == 0){
							mCouponTimetip.setText("今天到期");
						}else if(leftDays > 0){
							mCouponTimetip.setText("剩"+ leftDays +"天");
						}
					}
					mUninitedCouponLayout.setVisibility(View.GONE);
					mInitedCouponLayout.setVisibility(View.VISIBLE);
					mInitedCouponLayout
							.setOnClickListener(gotoMyCouponClickListener);
				}else{
					unInitedDefaultContent.setText("你已没有可用卡券");
                    mUninitedCouponLayout.setVisibility(View.VISIBLE);
                    mInitedCouponLayout.setVisibility(View.GONE);
                    mUninitedCouponLayout.setOnClickListener(gotoMyCouponClickListener);
                }
            } else {
            	
            	AppItemInfo couponMarketInfo = getHallData().getAppConfig(HallData.COUPON_MARKET);
            	
            	if (couponMarketInfo != null)
            	{
	                unInitedDefaultContent.setText( getResources().getString(R.string.tipsForUnInitedVoucher)
	                		.replace( "%s", couponMarketInfo.getNameText()) );            
	                mUninitedCouponLayout.setVisibility(View.VISIBLE);
	                mInitedCouponLayout.setVisibility(View.GONE);
	                mUninitedCouponLayout.setOnClickListener(gotoMyCouponClickListener);
            	}
            }

            // 去账户管理
            mUserInfoLayout.setOnClickListener(gotoAccountManagerListener);
            // TODO 去账单列表
            mBillLayout.setOnClickListener(gotoBillClickListener);
        }
        mSystemMessageImage.setVisibility(View.VISIBLE);
        // TODO 去券市场
    }

    private AlipayDetailInfo makeItem(JSONObject itemObj, String type) {
    	
    	AlipayTradeStatus mTradeStatus = null;
        mTradeStatus = new AlipayTradeStatus(this);

        final AlipayDetailInfo tItem = new AlipayDetailInfo();

        tItem.setDetailInfo(this, itemObj);
        
    	String tTradStatus = "";
		if(type != null && Integer.parseInt(type) == 2)
		{
			tTradStatus = itemObj.optString(Constant.RPF_TRADESTATUS);
		}
		else if(type != null && Integer.parseInt(type) == 1)
		{
			tTradStatus = itemObj.optString(Constant.STATUS);
		}
		
		tItem.resultType = mTradeStatus.getMapStatus(tTradStatus);
		
		return tItem;
    }
		
    private OnClickListener doLoginClickListener = new OnClickListener() {

        @Override
        public void onClick(View v) {
            if (isAppCenterOpen) {
                return;
            }
            doLogin();
        }
    };

    private OnClickListener gotoAccountManagerListener = new OnClickListener() {

        @Override
        public void onClick(View v) {
            if (isAppCenterOpen) {
                return;
            }
            if (!intentSended) {
                prepareAnim(mUserInfoLayout,mOverlay1);

                Intent intent = new Intent();
                intent.setClass(Main.this, AlipayAccountManager.class);
                // intent.addFlags(Intent.FLAG_ACTIVITY_NO_USER_ACTION);
                intent.putExtra("pageFrom", "MainPage");
                mResetUserStatus = false;
                startActivity(intent);
                intentSended = true;
                AlipayLogAgent.writeLog(Main.this, Constants.BehaviourID.CLICKED, null, null,
                    Constants.WALLETACCOUNT, null, Constants.ACCOUNTHOME, Constants.WALLETHOME,
                    Constants.ACCOUNTHOMEZONE, "");
			}
		}
	};

    private void prepareAnim(View view,View belowView) {
        Bitmap screenBitmap = mCacheFrameLayout.getCacheBitmap();//ScreenHelper.getViewBitmap(mAccountName.getRootView());
        if (screenBitmap != null) {
            int[] loca = ScreenHelper.getViewLocation(view);
            int[] belowLoca = ScreenHelper.getViewLocation(belowView);
            int height = belowLoca[1]-loca[1];
            AnimationData animationData = new AnimationData(screenBitmap, loca[1], height);
            application.setAnimationData(animationData);
        }
    }

    private OnClickListener gotoBillClickListener = new OnClickListener() {
        @Override
        public void onClick(View v) {

            if (!intentSended) {
                prepareAnim(mBillLayout,mOverlay2);

                Intent intent = new Intent(Main.this,
						BillManagerRootControllerActivity.class);
				// intent.addFlags(Intent.FLAG_ACTIVITY_NO_USER_ACTION);
				mResetUserStatus = false;
				intent.putExtra("pageFrom", "MainPage");
				startActivity(intent);
				intentSended = true;
				AlipayLogAgent.writeLog(Main.this,
						Constants.BehaviourID.CLICKED, null, null,
						Constants.WALLETBILL, null, Constants.BILLLIST,
						Constants.WALLETHOME, Constants.BILLZONE, "");
            }
        }
    };

    private OnClickListener gotoMyCouponClickListener = new OnClickListener() {
        @Override
        public void onClick(View v) {

            if (!intentSended) {
                prepareAnim(mUninitedCouponLayout,mOverlay3);

                // 如果无券信息，显示初始化引导，去券市场；如果有券信息，显示券信息，去我的券列表；去过获取余额失败，从新获取余额，直接进入券列表
                Intent intent = new Intent(Main.this, VoucherIndexActivity.class);
                // intent.addFlags(Intent.FLAG_ACTIVITY_NO_USER_ACTION);
                mResetUserStatus = false;
                intent.putExtra("pageFrom", "MainPage");
                startActivity(intent);
                intentSended = true;
                AlipayLogAgent.writeLog(Main.this, Constants.BehaviourID.CLICKED, null, null,
                    Constants.WALLETTICKET, null, Constants.MYTICKETLIST, Constants.WALLETHOME,
                    Constants.TICKETZONE, "");
            }
        }
    };

    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (mAppSlidingDrawer.isOpened()) {
            closeDrawer();
            return true;
        } else {
            if (BaseHelper.tabOnKeyDown(this, keyCode, event))
                return true;
            return super.onKeyDown(keyCode, event);
        }
    }

    private void closeDrawer() {
        mAppSlidingDrawer.onInterceptTouchEvent(MotionEvent.obtain(SystemClock.uptimeMillis(),
            SystemClock.uptimeMillis(), MotionEvent.ACTION_DOWN, mHandle.getLeft() + 15,
            mHandle.getTop() + 15, 0));
        mAppSlidingDrawer.onTouchEvent(MotionEvent.obtain(SystemClock.uptimeMillis(),
            SystemClock.uptimeMillis(), MotionEvent.ACTION_UP, mHandle.getLeft() + 15,
            mHandle.getTop() + 15, 0));
    }

    protected void jumpToMessageList(Context context) {
        Intent intent = new Intent(Main.this, MessageList.class);
        // intent.addFlags(Intent.FLAG_ACTIVITY_NO_USER_ACTION);
        mResetUserStatus = false;
        context.startActivity(intent);
    }

    private void initShortcut() {
        File f = this.getFileStreamPath(FILE_NAME);
        if (!f.exists()) {
            getDataHelper().showDialog(this, 0, getResources().getString(R.string.WarngingString),
                getResources().getString(R.string.install_shortcut_tip),
                getResources().getString(R.string.Ensure), new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        installShortcut();
                    }
                }, getResources().getString(R.string.Cancel),
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        CacheSet.getInstance(Main.this).putString("confirmNotCreate", "true");
                    }
                }, null, null);
            try {
                this.openFileOutput(FILE_NAME, Context.MODE_PRIVATE);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
        }
        // 如果存在快捷方式(对过渡版本做的处�?
        else {
            String isNewShortCut = CacheSet.getInstance(this).getString("isNewShorCut");
            String confirmNotCreate = CacheSet.getInstance(this).getString("confirmNotCreate");

            // 不是新的快捷方式确定没有点击不创�?
            if (!"true".equalsIgnoreCase(isNewShortCut)
                && !"true".equalsIgnoreCase(confirmNotCreate)) {
                // 删除原先的快捷方�?
                unInstallShortcut();
                // 重新安装快捷方式
                installShortcut();
            }
        }
    }

    private void unInstallShortcut() {
        String DELETE_ACTION = "com.android.launcher.action.UNINSTALL_SHORTCUT";

        ComponentName comp = new ComponentName(this.getPackageName(), getPackageName()
                                                                      + ".AlipayLogin");
        Intent actionMainIntent = new Intent(Intent.ACTION_MAIN).addCategory(
            Intent.CATEGORY_LAUNCHER).setComponent(comp);

        Intent intent = new Intent(DELETE_ACTION);
        intent.putExtra(Intent.EXTRA_SHORTCUT_NAME, getString(R.string.app_name));
        intent.putExtra(Intent.EXTRA_SHORTCUT_INTENT, actionMainIntent);
        sendBroadcast(intent);
    }

    private void installShortcut() {
        String ACTION_INSTALL_SHORTCUT = "com.android.launcher.action.INSTALL_SHORTCUT";
        String EXTRA_SHORTCUT_DUPLICATE = "duplicate";

        Intent actionMainIntent = new Intent(Intent.ACTION_MAIN);
        actionMainIntent.addCategory(Intent.CATEGORY_LAUNCHER);
        String pkgName = this.getPackageName();
        actionMainIntent.setComponent(new ComponentName(pkgName, pkgName + ".AlipayLogin"));
        actionMainIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK
                                  | Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED);

        Intent installShortcutIntent = new Intent(ACTION_INSTALL_SHORTCUT);
        installShortcutIntent.putExtra(Intent.EXTRA_SHORTCUT_NAME, getString(R.string.app_name));
        installShortcutIntent.putExtra(EXTRA_SHORTCUT_DUPLICATE, false);
        installShortcutIntent.putExtra(Intent.EXTRA_SHORTCUT_INTENT, actionMainIntent);
        installShortcutIntent.putExtra(Intent.EXTRA_SHORTCUT_ICON_RESOURCE,
            Intent.ShortcutIconResource.fromContext(this, R.drawable.appicon));
        sendBroadcast(installShortcutIntent);

        // 标记已经是最新的快捷方式
        CacheSet.getInstance(this).putString("isNewShorCut", "true");
    }

    private void displayLicense() {
        StyleAlertDialog dialog = new StyleAlertDialog(this, 0, getResources().getString(
            R.string.Disclaimer), getResources().getString(R.string.DisclaimerContent),
            getResources().getString(R.string.Accept), new DialogInterface.OnClickListener() {

                @Override
                public void onClick(DialogInterface dialog, int which) {
                    AlipayDataStore settings = new AlipayDataStore(Main.this);
                    settings.putString(AlipayDataStore.DISPLAY_LICENSE, "1");
                }

            }, getResources().getString(R.string.Refuse), new DialogInterface.OnClickListener() {

                @Override
                public void onClick(DialogInterface dialog, int which) {
                    BaseHelper.exitProcessSilently(Main.this);
                }
            }, new DialogInterface.OnCancelListener() {
                @Override
                public void onCancel(DialogInterface dialog) {
                    Main.this.finish();

                }
            });
        dialog.getDialog().setOnKeyListener(new DialogInterface.OnKeyListener() {
            @Override
            public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event) {
                if (keyCode == KeyEvent.KEYCODE_SEARCH) {
                    return true;
                }
                return false;
            }

        });
        dialog.show();
    }

    @Override
    public void update(final Observable observable,final Object data) {
    	runOnUiThread(new Runnable() {
			@Override
			public void run() {
		        if (observable instanceof Voucher) {
		        	if(data == null){
		        		Voucher voucher = (Voucher) observable;
		        		// 去获取图片
		        		Bitmap mVoucherLogo = getVoucherLogo(voucher,false);
		        		if(mVoucherLogo != null){
		        			mCouponInitedImage.setImageBitmap(mVoucherLogo);
		        			voucher.deleteObserver(Main.this);
		        		}
		        	}
		        } else {
		            // processQueryBalanceFail();
		            refresh(getUserData());
		        }
			}
		});
    }
    
	private Bitmap getVoucherLogo(Voucher voucher,boolean loading){
		Bitmap logoBitmap;
		if(voucher.getMode() == Voucher.Mode.ONLINE || voucher.getMode() == null){
			logoBitmap = voucher.getLogoBitmap(this,loading);
		} else {
			String userAccount = userData.getAccountName();
			logoBitmap = voucher.getCouponLogoBitmap(this, false, userAccount);
		}
		return logoBitmap;
	}

    @Override
    protected void onPause() {
        super.onPause();
        if (!Constant.isDebug(this)) {
            try {
                TBS.Page.leave(Main.class.getName());
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public NdefMessage createNdefMessage(NfcEvent event) {
        String type = "alipay.text/plain";
        String text = userInfo;
        if (text == null)
            return null;
        NdefMessage msg = new NdefMessage(
            new NdefRecord[] { createMimeRecord(type, text.getBytes()) });
        return msg;
    }

    public NdefRecord createMimeRecord(String mimeType, byte[] payload) {
        byte[] mimeBytes = mimeType.getBytes(Charset.forName("US-ASCII"));
        NdefRecord mimeRecord = new NdefRecord(NdefRecord.TNF_MIME_MEDIA, mimeBytes, new byte[0],
            payload);
        return mimeRecord;
    }

    private String userInfo;

    private void initRegistInfo() {
        if (userData == null) {
            db.open(this);
            UserInfo lastUserInfo = db.getLastLoginUser(Constant.BindingAlipay);
            if (lastUserInfo != null) {
                userInfo = lastUserInfo.userAccount + "#" + lastUserInfo.userName + "#"
                           + lastUserInfo.phoneNo;
            }
        } else {
            userInfo = userData.getAccountName() + "#" + userData.getRealName() + "#"
                       + userData.getMobileNo();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();

        Intent intent = getIntent();
        if (processIntent(intent)) {
            return;
        }

        intentSended = false;

        saveUserInfo();

        if (intent != null && intent.getData() != null && processScheme) {
            processScheme = false;
            Uri uri = getIntent().getData();
            if (uri != null) {
//                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//                intent.setFlags(Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS);
//                intent.setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
//                intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
//                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                mSchemeHandler.execute(uri);
            }
        }

        userData = getUserData();
        if (userData != null) {
            if (userData.requestData(this, getDataHelper()) == UserData.REQUEST_PROCESSING) {
                loginingInit();

            }
        }

        refresh(userData);
        initRegistInfo();

        LogUtil.logMsg(Constant.LOG_LEVEL_DEBUG, "Main", "--------onResume()--------isLogin:"
                                                         + (getUserData() != null) + ", curAcount="
                                                         + curUserState.getUserAccount());

        if (!Constant.isDebug(this)) {
            try {
                TBS.Page.enter(Main.class.getName());
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private void loginingInit() {
        mSystemMessageImage.setVisibility(View.VISIBLE);

        moneyLayout.setVisibility(View.GONE);

        mUserInfoLayout.setOnClickListener(gotoAccountManagerListener);

        mUninitedBillLayout.setVisibility(View.VISIBLE);
        mBillTopLayout.setVisibility(View.GONE);
        mBillBottomLayout.setVisibility(View.GONE);
        mUninitedBillText.setText("交易/账单");

        mUninitedCouponLayout.setVisibility(View.VISIBLE);
        mInitedCouponLayout.setVisibility(View.GONE);
        unInitedDefaultContent.setText("我的卡券");

    }

    private boolean mResetUserStatus = true;

    public void SetRefreshUserStatus(boolean refreshUserStatus) {
        mResetUserStatus = refreshUserStatus;
    }

    @Override
    protected void onUserLeaveHint() {
        // TODO Auto-generated method stub
        super.onUserLeaveHint();

        if (mResetUserStatus) {
            resetUserStatus();
        }
        mResetUserStatus = true;
    }

    private void resetUserStatus() {
        if (getUserData() != null) {
            getUserData().resetStatus();
        }
    }

    private void refresh(UserData userData) {

        setLayoutAndListener(userData);

        if (userData != null) {

            rotateImg.setVisibility(View.VISIBLE);

            mLoginedArea.setVisibility(View.VISIBLE);
            unLoginedArea.setVisibility(View.GONE);
            if (userData.isCertificate()) {
                mRealName.setText(getRealName());
                mRealName.setBackgroundResource(0);
            } else {
                if (null != getRealName() && "" != getRealName()) {
                    mRealName.setText(getRealName());
                } else {
                    mRealName.setText("你好,欢迎回来");
                }
            }

            mAccountName.setText(userData.getAccountName());

            String balance = userData.getAvailableBalance();
            if (balance != null && balance != "") {

                if (balance.length() > 11) {
                    balance = balance.substring(0, 8) + "...";
                    mBalanceHead.setText(balance);
                } else {
                    int pointIndex = balance.indexOf(".");
                    if (pointIndex == -1) {
                        mBalanceHead.setText(balance);
                    } else {
                        String balanceHead = balance.substring(0, pointIndex + 1);
                        String balanceEnd = balance.substring(pointIndex + 1);
                        mBalanceHead.setText(balanceHead);
                        mBalanceEnd.setText(balanceEnd);
                    }
                }
                moneyLayout.setVisibility(View.VISIBLE);
            }

            AlipayApplication application = (AlipayApplication) getApplicationContext();
            MessageData mMessageData = application.getMessageData();
            // 系统消息总数量
            mSystemMessageImage.setVisibility(View.VISIBLE);
            int remindNum = userData.getMsgCount() + mMessageData.getUnreadMsgCount();
            if (remindNum > 99) {
                mSystemMessageNumber.setText("N");
            } else {
                mSystemMessageNumber.setText(remindNum + "");
            }
            if (remindNum > 0) {
                // mSystemMessageLayout.setVisibility(View.VISIBLE);
                mSystemMessageNumber.setVisibility(View.VISIBLE);
            } else {
                // mSystemMessageLayout.setVisibility(View.INVISIBLE);
                mSystemMessageNumber.setVisibility(View.INVISIBLE);
            }

            String cardCount = userData.getCardCount();
            if (cardCount == null) {
                mBankCardNumber.setText("");
                bankCardLayout.setVisibility(View.INVISIBLE);
            } else {
                mBankCardNumber.setText(cardCount + "");
                bankCardLayout.setVisibility(View.VISIBLE);
            }

            Bitmap avatarImg = userData.getUserAvtar();
            if (avatarImg != null) {
                user_avatar.setImageBitmap(avatarImg);
                plugImg.setVisibility(View.INVISIBLE);
            } else {
                user_avatar.setImageResource(R.drawable.default_avatar);
                plugImg.setVisibility(View.VISIBLE);
            }

        } else {
            initLayoutAndListener(userData);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        db.close();

        UserData userData = getUserData();
        if (userData != null) {
            userData.deleteObserver(this);
        }

        if (db != null)
            db.close();
        if (!Constant.isDebug(this)) {
            try {
                TBS.Page.destroy(Main.class.getName());
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private void saveUserInfo() {
        UserInfo tCurUserInfo = getDataHelper().getLatestLoginedUser(this);
        if (tCurUserInfo != null) { // 本地成功登录账号存在
            curUserState.setUserInfo(tCurUserInfo);
            curUserState.setAccountFrom(AlipayUserState.ACCOUNT_TYPE_CLIENT);

            if (tCurUserInfo.type != null && tCurUserInfo.type.equals("alipay")) {
                curUserState.setUserState(AlipayUserState.ACCOUNT_STATE_AUTH_PREPARE);
            } else if (tCurUserInfo.type != null && tCurUserInfo.type.equals("taobao")
                       && userData != null && userData.getAccountName() != null) {
                curUserState.setUserAccount(userData.getAccountName());
                curUserState.setUserId(userData.getUserId());
            }

            if (userData != null) {
                curUserState.setUserState(AlipayUserState.ACCOUNT_STATE_LOGIN);
            }

        } else {
            curUserState.reset();
        }
        curUserState.updateUserState(userData != null);

        // Constant.STR_ACCOUNT = curUserState.getUserAccount();
        getCertUserData().setAccountName(curUserState.getUserAccount());
        getCertUserData().setUserId(curUserState.getUserId());
    }

    private void doLogin() {
        Intent intent = new Intent(this, Login.class);
        startActivityForResult(intent, Constant.REQUEST_LOGIN_BACK);
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
		if((intent.getFlags() & Intent.FLAG_ACTIVITY_LAUNCHED_FROM_HISTORY) == Intent.FLAG_ACTIVITY_LAUNCHED_FROM_HISTORY){
			processScheme = !isDispathced();
		}else
			processScheme = true ;

        getIntent().setData(intent.getData());
        processIntent(intent);
    }

    private boolean processIntent(Intent intent) {
        if (!Intent.ACTION_MAIN.equalsIgnoreCase(intent.getAction())
            && (intent.getFlags() & Intent.FLAG_ACTIVITY_CLEAR_TOP) == Intent.FLAG_ACTIVITY_CLEAR_TOP
            && intent.getBooleanExtra(Constant.EXIT, false)) {
            finish();
            return true;
        } /*
          * else { Uri uri = intent.getData(); mSchemeHandler.execute(uri); }
          */

        return false;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        if (!Main.this.getShowMenu()) {
            return super.onCreateOptionsMenu(menu);
        }

        boolean supportMenu = MenuHelper.setMemu(getLayoutInflater());
        if (!supportMenu) {
            return false;
        }

        MenuInflater inflater = getMenuInflater();

        if (Constant.isMZ) {
            try {
                inflater.inflate(null == this.getUserData() ? R.menu.menu_nologin_mz
                    : R.menu.menu_loggedin_mz, menu);
            } catch (Exception e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        } else {
            try {
                inflater.inflate(null == this.getUserData() ? R.menu.menu_nologin
                    : R.menu.menu_loggedin, menu);
            } catch (Exception e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        mResetUserStatus = false;
        int itemId = item.getItemId();
        switch (itemId) {
            case R.id.accountManagerInSmartBar:

                if (isAppCenterOpen) {
                    mAppSlidingDrawer.close();
                    return true;
                } else {
                    if (getUserData() == null) {
                        doLoginClickListener.onClick(null);
                    } else {
                        gotoAccountManagerListener.onClick(null);
                    }
                }
                break;
            case R.id.bankCardInSmartBar:
                if (isAppCenterOpen) {
                    mAppSlidingDrawer.close();
                    return true;
                } else {
                    if (getUserData() == null) {
                        doLoginClickListener.onClick(null);
                    } else {
                        Intent tIntent = new Intent(Main.this,
                            CardManagerRootControllerActivity.class);
                        mResetUserStatus = false;
                        startActivity(tIntent);
                        AlipayLogAgent.writeLog(Main.this, Constants.BehaviourID.CLICKED, null,
                            null, Constants.WALLETACCOUNT, null, Constants.BANKCARDLIST,
                            Constants.WALLETHOME, Constants.BANKCARDICON, "");
                    }
                }
                break;
            default:
                break;
        }
        return super.onOptionsItemSelected(item);
    }
}
