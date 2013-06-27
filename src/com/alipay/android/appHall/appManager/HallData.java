/**
 * 
 */
package com.alipay.android.appHall.appManager;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Observable;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.os.Build;
import android.os.HandlerThread;

import com.alipay.android.appHall.CacheManager;
import com.alipay.android.appHall.DeviceHelper;
import com.alipay.android.appHall.Helper;
import com.alipay.android.appHall.common.Defines;
import com.alipay.android.client.AlipayApplication;
import com.alipay.android.client.constant.Constant;
import com.alipay.android.client.constant.PhoneConstant;
import com.alipay.android.core.Style;
import com.alipay.android.net.RequestMaker;
import com.alipay.android.net.http.HttpRequestMaker;
import com.alipay.android.util.LogUtil;
import com.alipay.platform.core.Command;
import com.alipay.platform.view.ActivityMediator;
import com.alipay.platform.view.OnDataChangeListener;
import com.eg.android.AlipayGphone.R;

/**
 * @author sanping.li
 * 
 */
public class HallData extends Observable implements OnDataChangeListener {
    public static final String syncFuncStatus = "13";
    
    /**
     * 初始化
     */
    public static final int STATE_INIT = 1;
    /**
     * 同步
     */
    public static final int STATE_SYNC=2;
    /**
     * 同步成功
     */
    public static final int STATE_SYNC_OK = 3;
    /**
     * 同步失败
     */
    public static final int STATE_SYNC_FAIL = 4;
    
    /**
     * 状态
     */
    private int mState;

    private Context mContext;
    // LegacyApps.
    public static final int[] mLagacyAppsName = { R.string.app_lottery, R.string.BarcodePay,
                                                 R.string.WaterRate, R.string.PowerRate,
                                                 R.string.GasRate, R.string.PhoneAndWideBand,
                                                 R.string.CCRApp, R.string.super_transfer,
                                                 R.string.voucherDetail, R.string.voucherIndex,
                                                 R.string.recordListView, R.string.SystemMsg,
                                                 R.string.ConsumptionRecordDetailTitle,
                                                 R.string.quickpay, R.string.BarcodeGather,
                                                 R.string.userFeedback, /*R.string.nfcEntrance*/ };

    public static final int[] mLagacyAppsIcon = { -1, R.drawable.app_barcode_pay,
                                                 R.drawable.app_water, R.drawable.app_power,
                                                 R.drawable.app_gas, R.drawable.app_broadband,
                                                 R.drawable.app_credit_card,
                                                 R.drawable.homepage_apps0, -1, -1, -1, -1, -1,
                                                 R.drawable.homepage_apps6, -1, -1, /*-1*/ };

    public static final int[] mLagacyAppsPosition = { 3, 8, 10, 11, 12, 13, 14, 15,
                                                     Integer.MAX_VALUE, Integer.MAX_VALUE,
                                                     Integer.MAX_VALUE, Integer.MAX_VALUE,
                                                     Integer.MAX_VALUE, Integer.MAX_VALUE,
                                                     Integer.MAX_VALUE, Integer.MAX_VALUE, /*Integer.MAX_VALUE*/ };
    
    // 09999980 id 是nfc入口的id
    public static final String[] mLagacyAppsId = { "09999991", "09999993", "09999995", "09999996",
                                                  "09999997", "09999998", "09999999", "09999988",
                                                  "09999987", "09999986", "09999985", "09999984",
                                                  "09999985", "09999989", "09999992", "09999000"/*, "09999980"*/ };//09999000id为公测反馈id

    public static final String[] mNativeAppsId = { "29999991" };
    public static final String[] mNativeAppsName = { "com.alipay.android.client.baseFunction.MessageList" };

 // 首页应用ID
    public static final String[] sordedIdArray = new String[] {"09999988", "09999989",
                                                               "10000007","10000003" };
    
    // 券市场
    public static final String COUPON_MARKET = "80000001";

    private ArrayList<AppItemInfo> mLegacyApps;
    private ArrayList<AppItemInfo> mActiveApps;
    private HashMap<String, AppItemInfo> mApps;

    /**
     * AppItem排序
     */
    private AppItemInfoComparator mAppItemInfoComparator;

    /**
     * 业务管理�?
     */
    private AppManager mAppManager;

    /**
     * 业务包处理器
     */
    private AppHandler mAppHandler;
    private ActivityMediator mActivityMediator;
    
    public HallData(Context context) {
        mContext = context;
        mAppManager = new XmlAppManager(this);
        mActivityMediator = new ActivityMediator(this);
        mState = 0;
        init();
    }

    private void init() {
        mLegacyApps = new ArrayList<AppItemInfo>();
        mActiveApps = new ArrayList<AppItemInfo>();
        mAppItemInfoComparator = new AppItemInfoComparator();
        // 创建业务处理�?
        HandlerThread thread = new HandlerThread("operationApp");
        thread.start();
        mAppHandler = new AppHandler(this, thread.getLooper());
    }

    /**
     * 请求同步
     */
    public void syncAppStatus() {
        if (mApps == null || mApps.size() > 0) {
            mState = STATE_SYNC;
            AlipayApplication application = (AlipayApplication) mContext.getApplicationContext();
            ArrayList<String> params = new ArrayList<String>();
            params.add(application.getConfigData().getProductId());// productID
            params.add(application.getConfigData().getProductVersion());// productVersion
            params.add(DeviceHelper.getUserAgent(mContext));// user agent

            params.add(mAppManager.getAppList(mApps));// funcList
            params.add(DeviceHelper.getScreenResolution(mContext));// resolution
            params.add("");// sessionId
            params.add(application.getConfigData().getClientId());// clientID

            RequestMaker requestMaker = new HttpRequestMaker(mContext, R.raw.interfaces);
            mActivityMediator.sendCommand(syncFuncStatus, "clean", requestMaker, params);
        }
    }

    
//    // htc nfc入口
// 	// iconIndex 是图标在数组中的位置
// 	private int iconIndex = -1;
// 	private int iconId = -1;
//
// 	public void setPropertsData(int icon_index, int icon_id) {
// 		iconIndex = icon_index;
// 		iconId = icon_id;
// 	}

 	// 淘宝彩票
 	// nativeIndex 是图标在数组中的位置
 	int nativeIndex = -1;

 	public void setNativeIndex(int index) {
 		nativeIndex = index;
 	}

 	// 业务id
 	String funcid = "";

 	public void setFuncId(String funcid) {
 		this.funcid = funcid;
 	}

 	public String getFuncId() {
 		return funcid;
 	}

 	private void filterIcon() {
 		// caipiao
 		if (nativeIndex != -1 && nativeIndex < mLagacyAppsIcon.length) {
 			mLagacyAppsIcon[nativeIndex] = -1;
 		}

// 		// htc nfc
// 		if (iconIndex != -1 && iconIndex < mLagacyAppsIcon.length) {
// 			mLagacyAppsIcon[iconIndex] = iconId;
// 		}
 	}
    
    
    /**
     * 初始�?
     */
    public void initializeAppList() {
        mState = STATE_INIT;
        LogUtil.logAnyTime("step", "initializeAppList:" + this.getClass().getName());    
    	filterIcon();
        if (mApps == null || mApps.size() <= 0) {
            // get all apps's info from config file
            String mAppsPath = (mContext.getFilesDir().getAbsolutePath() + Defines.Apps);
            mApps = Helper.getConfigInfo(this, mAppsPath + Defines.CONFIG_NAME);

            //
            // Prepare lagacy apps.
            prepareLagacyApps();

            // Legacy apps(in other words, old apps).
            filterLegacyApps();

            //
            // do PreInstall.whether preInstall apps need to install or update
            // (syn to local file after install)
            doPreInstall();

            // Prepare already-installed apps.
            prepareAlreadyInstalledApps();
        }
    }

    private void doPreInstall() {
        AppPreInstaller appPreInstaller = new AppPreInstaller(mContext);
        appPreInstaller.doPreInstall(this);
    }

    /**
     * 准备本地业务
     */
    private void prepareLagacyApps() {
        for (int index = 0; index < mLagacyAppsName.length; index++) {
            String funcId = mLagacyAppsId[index];

            AppItemInfo appItemInfo = getAppConfig(funcId);// new AppItemInfo();
            if (appItemInfo == null) {
                if (mLagacyAppsIcon[index] == -1)
                    appItemInfo = new AppItemInfo(this, funcId, AppItemInfo.TYPE_NATIVE, 0);
                else
                    appItemInfo = new AppItemInfo(this, funcId, AppItemInfo.TYPE_NATIVE);
                appItemInfo.setOrder(mLagacyAppsPosition[index]);
            } else if (appItemInfo != null && mLagacyAppsIcon[index] == -1) {
                appItemInfo.setShow(0);
            }
            appItemInfo.setName(String.valueOf(mLagacyAppsName[index]));
            appItemInfo.setIconUrl(String.valueOf(mLagacyAppsIcon[index]));

            mLegacyApps.add(appItemInfo);
        }
    }

    /**
     * 准备已经安装的业�?
     */
    private void prepareAlreadyInstalledApps() {
        ArrayList<AppItemInfo> appArray = mAppManager.getAppListInfo(mApps);
        mActiveApps.addAll(appArray);
    }

    /**
     * 过滤本地的业�?
     */
    private void filterLegacyApps() {
        int expectedVersion = 4;
        if (Integer.valueOf(Build.VERSION.SDK).intValue() < expectedVersion) {
            Constant.isShowQRCodeBar = false;
        }

        for (int i = 0; i < this.mLegacyApps.size(); i++) {
            if (PhoneConstant.isMoto) {
                if (mLegacyApps.get(i).getName().toString()
                    .equalsIgnoreCase(R.string.app_lottery + "")) {
                    continue;
                }
            }

            if (!((!Constant.isShowCreditcard && mLegacyApps.get(i).getName().toString()
                .equalsIgnoreCase(R.string.CreditCardRepay + "")) || (!Constant.isShowQRCodeBar && mLegacyApps
                .get(i).getName().toString().equalsIgnoreCase(R.string.PaipaiPay + "")))) {
                mApps.put(mLegacyApps.get(i).getId(), mLegacyApps.get(i));
            }
        }
    }

    public AppItemInfo getAppConfig(String funcId) {
        return mApps.get(funcId);
    }

    public void setAppConfig(String funcId, AppItemInfo appConfigInfo) {
        mApps.put(funcId, appConfigInfo);
    }

    /**
     * 存储所有业务列�?
     */
    public void storeApps() throws Exception {
        // Sort the mActiveApps.
        ArrayList<AppItemInfo> apps = new ArrayList<AppItemInfo>(mApps.values());
        Collections.sort(apps, mAppItemInfoComparator);

        JSONObject data = new JSONObject();
        JSONArray jsonArray = new JSONArray();
        JSONObject item = null;
        int position = 0;
        for (AppItemInfo configInfo : apps) {
            if (0 == configInfo.isShow()) {
                continue;
            }

            item = new JSONObject();
            item.put(Defines.funcId, configInfo.getId());
            item.put(Defines.name, configInfo.getName());
            item.put(Defines.icon, configInfo.getIconUrl());
            item.put(Defines.type, configInfo.getType());
            item.put(Defines.isShow, configInfo.isShow());
            if (configInfo.getState() == AppItemInfo.STATE_DELETEED || configInfo.isShow() == 0)
                configInfo.setOrder(Integer.MAX_VALUE);
            else {
                configInfo.setOrder(position);
                position++;
            }
            item.put(Defines.showOrder, configInfo.getOrder());
            item.put(Defines.version, configInfo.getCurVersion());
            item.put(Defines.status, configInfo.getState());
            item.put(Defines.minSdkVersion, configInfo.getMinSdkVersion());
            item.put(Defines.uri, configInfo.getUrl());
            item.put(Defines.percent, configInfo.getPercent());
            jsonArray.put(item);
        }

        data.put("apps", jsonArray);

        String mAppsPath = (mContext.getFilesDir().getAbsolutePath() + Defines.Apps);
        object2JsonFile(data, mAppsPath + Defines.CONFIG_NAME);
    }

    private void object2JsonFile(JSONObject obj, String filename) throws IOException {
        File config = new File(filename);
        FileOutputStream fos = new FileOutputStream(config);
        fos.write(obj.toString().getBytes());
        fos.close();
    }

    /**
     * 暂停所有安装中/升级中的业务
     */
    private void pausedAllAppOperation() {
        if (mActiveApps == null)
            return;
        for (int index = 0; index < mActiveApps.size(); index++) {
            AppItemInfo appItemInfo = mActiveApps.get(index);
            int type = appItemInfo.getType();
            if (type == AppItemInfo.TYPE_XML
                && (appItemInfo.getState() == AppItemInfo.STATE_INSTALLING || appItemInfo
                    .getState() == AppItemInfo.STATE_UPGRADEING)) {
                doPause(appItemInfo.getId());
            }
        }
    }

    /**
     * 退出的时候销�?
     */
    public void destroy() {
        deleteObservers();
        pausedAllAppOperation();
        try {
            storeApps();
        } catch (Exception e) {
            e.printStackTrace();
        }

        if (mActiveApps != null)
            mActiveApps.clear();
        if (mLegacyApps != null)
            mLegacyApps.clear();
        if (mApps != null)
            mApps.clear();
        mState = 0;
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
        syncApps();
        mState = STATE_SYNC_FAIL;
    }

    // String extendValue = appItem.optString(Defines.extend);
    // if (AppItemInfo.INDEXOPERATION.equals(extendValue)) {
    // setHomePageIconArray(funcId);
    // }

    @Override
    public void onComplete(Command command) {
        try {
            //
            String response = (String) command.getResponseRawData();
            JSONObject objResponse = new JSONObject(response);
            CacheManager cacheManager = CacheManager.getInstance(mContext);
            if (objResponse.has(Defines.backFuncId)) {
                Object o = objResponse.get(Defines.backFuncId);
                if (o != null)
                    Constant.OPERATION_UID = o.toString();
            }

            String hasChange = objResponse.get(Defines.hasChange).toString();
            if (hasChange != null && hasChange.equalsIgnoreCase(Defines.valueHasChange)) {
                String funcList = objResponse.get(Defines.funcList).toString();
                cacheManager.put(Defines.funcList, funcList);
            } else {
                cacheManager.remove(Defines.funcList);
            }

            if (objResponse.has(Defines.specialFuncList)) {
                String specialFuncList = objResponse.get(Defines.specialFuncList).toString();
                cacheManager.put(Defines.specialFuncList, specialFuncList);
            } else {
                cacheManager.remove(Defines.specialFuncList);
            }
        } catch (Exception e) {
            e.printStackTrace();

        } finally {
            syncApps();
            mState = STATE_SYNC_OK;
        }
    }

    /**
     * 处理同步的数�?
     */
    private void syncApps() {
        if (mApps.size() <= 0)
            return;
        boolean changed = processList();

        // store apps info
        if (changed) {
            try {
                storeApps();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        processAd();

        refresh();
    }

    /**
     * 处理业务列表
     */
    private boolean processList() {
        boolean update = false;
        CacheManager cacheManager = CacheManager.getInstance(mContext);
        String strAppList = cacheManager.get(Defines.funcList);
        JSONArray appArray = new JSONArray();
        if (strAppList != null && strAppList.length() > 0) {
            try {
                appArray = new JSONArray(strAppList);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        AppItemInfo appItemInfo = null;
        JSONObject appItem = new JSONObject();
        String icon = null;
        for (int i = 0; i < appArray.length(); i++) {
            try {
                appItem = appArray.getJSONObject(i);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            String funcId = appItem.optString(Defines.funcId);
            if (funcId == null || funcId.length() <= 0)
                continue;

            // 临时�?.5上过�?0000002 和条码业�?
            if (Integer.valueOf(Build.VERSION.SDK) < 4
                && (funcId.equalsIgnoreCase("10000002") || funcId.equalsIgnoreCase("10000007") || funcId
                    .equalsIgnoreCase("09999993")))
                continue;

            appItemInfo = getAppConfig(funcId);// 本地已经准备好的应用对应的itemInfo
            if (appItemInfo == null) {
                String rawType = appItem.optString(Defines.type, Defines.x);
                int type = AppItemInfo.TYPE_XML;
                if (!rawType.equalsIgnoreCase(Defines.x))
                    type = AppItemInfo.TYPE_NATIVE;
                appItemInfo = new AppItemInfo(this, funcId, type);
                setAppConfig(funcId, appItemInfo);
            }

            appItemInfo.setName(appItem.optString(Defines.name, appItemInfo.getName().toString()));
            icon = appItem.optString(Defines.iconAddr, null);
            if (!appItemInfo.getIconUrl().toString().equalsIgnoreCase(icon)) {
                appItemInfo.setIconUrl(appItem.optString(Defines.iconAddr, appItemInfo.getIconUrl()
                    .toString()));
                appItemInfo.resetIcon();
            }
            appItemInfo.setLastVersion(appItem.optString(Defines.version,
                appItemInfo.getCurVersion()));
            appItemInfo.setUrl(appItem.optString(Defines.uri, appItemInfo.getUrl()));//可以处理缓存状态,以前是暂停现在是否该暂停
            appItemInfo.setOrder(appItem.optInt(Defines.position, appItemInfo.getOrder()));
            appItemInfo.setShow(appItem.optInt(Defines.isShow, appItemInfo.isShow()));

            update = processState(appItemInfo, appItem);
        }
        return update;
    }

    /**
     * 处理业务的状态
     * 如果是暂停状态，也以服务器返回为准
     */
    private boolean processState(AppItemInfo appItemInfo, JSONObject appItem) {
        boolean update = false;
        String status = appItem.optString(Defines.status, Defines.normal);

        if (status.equalsIgnoreCase(Defines.in)) {
            appItemInfo.setState(AppItemInfo.STATE_PRE_INSTALL);
            update = true;
            updateApps(appItemInfo);
        } else if (status.equalsIgnoreCase(Defines.up)) {
            appItemInfo.setState(AppItemInfo.STATE_PRE_UPGRADE);
            update = true;
            updateApps(appItemInfo);
        } else if (status.equalsIgnoreCase(Defines.un)) {
            appItemInfo.setState(AppItemInfo.STATE_PRE_DELETE);
            update = true;
            updateApps(appItemInfo);
        } else if (status.equalsIgnoreCase(Defines.autoIn)) {
            appItemInfo.setState(AppItemInfo.STATE_PRE_INSTALL);
            doInstall(appItemInfo.getId());
        } else if (status.equalsIgnoreCase(Defines.autoUp)) {
            appItemInfo.setState(AppItemInfo.STATE_PRE_UPGRADE);
            doUpgrade(appItemInfo.getId());
        } else if (status.equalsIgnoreCase(Defines.autoUn)) {
            appItemInfo.setState(AppItemInfo.STATE_PRE_DELETE);
            doDelete(appItemInfo.getId());
        } else if (status.equalsIgnoreCase(Defines.available)) {
            // 以前广告是否显示
            update = true;
        } else {
            appItemInfo.setState(AppItemInfo.STATE_NOMARL);
            update = true;
            updateApps(appItemInfo);
        }
        return update;
    }

    /**
     * 处理冒泡运营
     */
    private boolean processAd() {
        boolean update = false;
        CacheManager cacheManager = CacheManager.getInstance(mContext);
        String operationList = cacheManager.get(Defines.specialFuncList);
        JSONArray operationArray = new JSONArray();
        if (operationList != null && operationList.length() > 0) {
            try {
                operationArray = new JSONArray(operationList);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        JSONObject appItem = new JSONObject();
        for (int i = 0; i < operationArray.length(); i++) {
            try {
                appItem = operationArray.getJSONObject(i);
            } catch (Exception e) {
                e.printStackTrace();
            }
            String funcId = appItem.optString(Defines.funcId);
            if (funcId == null || funcId.length() <= 0)
                continue;
            String adType = appItem.optString(Defines.type);
            String adString = null;
            if (adType != null && (adType.equalsIgnoreCase(Defines.label)))
                adString = appItem.optString(Defines.content);
            update = true;
            updateAds(funcId, adType, adString);
        }
        return update;
    }

    /**
     * 更新激活状态业务的冒泡运营
     */
    private void updateAds(String funcId, String adType, String adString) {
        for (AppItemInfo item : mActiveApps) {
            if (item.getId().equalsIgnoreCase(funcId)) {
                item.setAdType(adType);
                item.setAdString(adString);
                break;
            }
        }
    }

    /**
     * 更新、增加业务到激活的业务列表
     */
    public void updateApps(AppItemInfo itemInfo) {
        boolean contain = false;
        int size = mActiveApps.size();
        for (int index = 0; index < size; ++index) {
            AppItemInfo item = mActiveApps.get(index);
            if (item.getId().equalsIgnoreCase(itemInfo.getId())) {
                mActiveApps.set(index, itemInfo);
                contain = true;
                break;
            }
        }
        if (!contain && itemInfo.isShow() == 1) {
            mActiveApps.add(itemInfo);
        }
    }

    /**
     * 删除激活的业务列表中的业务
     */
    public void removeApp(AppItemInfo itemInfo) {
        // Remove the current app from mAppsGrid.
        mActiveApps.remove(itemInfo);
    }

    /**
     * 获取激活的业务列表
     */
    public ArrayList<AppItemInfo> getData() {
        // Sort the mActiveApps.
        Collections.sort(mActiveApps, mAppItemInfoComparator);
        
        if (!funcid.equalsIgnoreCase("")) {
			String[] funcids = funcid.split("\\|");
			for (int j = 0; j < funcids.length; j++) {
				for (int i = 0; i < mActiveApps.size(); i++) {
					if (mActiveApps.get(i).getId().equals(funcids[j])) {
						mActiveApps.remove(i);
					}
				}
			}
		}
        
        return mActiveApps;
    }

    /**
     * 获取应用中心的业务列表
     */
    public ArrayList<AppItemInfo> getAppCenterData() {
        // Sort the mActiveApps.

        ArrayList<AppItemInfo> mAppCenterData = new ArrayList<AppItemInfo>();
        mAppCenterData.addAll(mActiveApps);
//        过滤掉首页的找优惠
//        for (int i = 0; i < mAppCenterData.size(); i++) {
//            if (mAppCenterData.get(i).getId().equals(COUPON_MARKET)) {
//                mAppCenterData.remove(i);
//                break;
//            }
//        }
        for (int j = 0; j < sordedIdArray.length; j++) {
            for (int i = 0; i < mAppCenterData.size(); i++) {
                if (mAppCenterData.get(i).getId().equals(sordedIdArray[j])) {
                    mAppCenterData.remove(i);
                    break;
                }
            }
        }
        Collections.sort(mAppCenterData, mAppItemInfoComparator);
        return mAppCenterData;
    }

    /**
     * 删除缓存的数�?
     */
    public void removeAppFromCache(String appId) {
        try {
            // 存储状�?
            storeApps();
        } catch (Exception e) {
            e.printStackTrace();
        }
        // Remove the current item from Cache Manager.
        CacheManager cacheManager = CacheManager.getInstance(mContext);
        String strAppList = cacheManager.get(Defines.funcList);
        if (strAppList == null || strAppList.length() <= 0)
            return;
        JSONArray appArray = null;
        try {
            appArray = new JSONArray(strAppList);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        if (appArray == null)
            return;
        appArray = Helper.removeItemFromArrayById(appArray, appId);

        cacheManager.put(Defines.funcList, appArray.toString());

        // Sync the status info to local.
        cacheManager.put2Local(Defines.funcList);
    }

    /**
     * 刷新界面
     */
    public void refresh() {
        setChanged();
        notifyObservers();
    }

    @Override
    public void setRuleId(String ruleId) {

    }

    @Override
    public String getRuleId() {
        return null;
    }

    @Override
    public Context getContext() {
        return mContext;
    }

    public void reAttachContext(Context context) {
        mContext = context;
        mAppManager = new XmlAppManager(this);
    }

    public AppManager getAppManager() {
        return mAppManager;
    }

    /**
     * 升级
     */
    public void doUpgrade(String funcId) {
        mAppHandler.doAction(funcId, AppHandler.ACTION_UPGRADE);
    }

    /**
     * 安装
     */
    public void doInstall(String funcId) {
        mAppHandler.doAction(funcId, AppHandler.ACTION_INSTALL);
    }

    /**
     * 卸载
     */
    public void doDelete(String funcId) {
        mAppHandler.doAction(funcId, AppHandler.ACTION_DELETE);
    }

    /**
     * 暂停
     */
    public void doPause(String funcId) {
        AppItemInfo itemInfo = getAppConfig(funcId);
        if (itemInfo.getState() == AppItemInfo.STATE_INSTALLING) {
            itemInfo.setState(AppItemInfo.STATE_INSTALL_PAUSE);
        } else if (itemInfo.getState() == AppItemInfo.STATE_UPGRADEING) {
            itemInfo.setState(AppItemInfo.STATE_UPGRADE_PAUSE);
        }
        refresh();
    }

    /**
     * 恢复
     */
    public void doResume(String funcId) {
        mAppHandler.doAction(funcId, AppHandler.ACTION_RESUME);
    }

    private class AppItemInfoComparator implements Comparator<AppItemInfo> {
        public final int compare(AppItemInfo pFirst, AppItemInfo pSecond) {
            return Integer.valueOf(pFirst.getOrder()).compareTo(pSecond.getOrder());
        }
    }

    public Style getStyle() {
        AlipayApplication application = (AlipayApplication) mContext.getApplicationContext();
        return application.getStyle();
    }

    /**
     * 获取首页四格应用的数据
     */
    public ArrayList<AppItemInfo> getHomeData() {
        ArrayList<AppItemInfo> ret = new ArrayList<AppItemInfo>();
        //		ArrayList<AppItemInfo> centers = getData();
        AppItemInfo info = null;
        for (String str : sordedIdArray) {
            info = getAppConfig(str);
            //			if (info == null || info.getState() == AppItemInfo.STATE_DELETEED
            //|| ret.contains(info)) {// 如果该Id不存在或者该Id对应的项在首页数据已经存在

            //				for (AppItemInfo item : centers) {
            //					if (!ret.contains(item)
            //							&& !item.getId().equalsIgnoreCase(
            //									sordedIdArray[sordedIdArray.length - 1])) {// 替换的不能是运营
            //						info = item;
            //						break;
            //					}
            //				}
            //			}
            ret.add(info);
        }
        return ret;
    }

    public int getState() {
        return mState;
    }

}
