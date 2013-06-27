/**
 * 
 */
package com.alipay.android.core.expapp;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.Timer;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.os.PowerManager;
import android.view.KeyEvent;

import com.alipay.android.appHall.Helper;
import com.alipay.android.appHall.appManager.HallData;
import com.alipay.android.appHall.baseService.BaseServiceProvider;
import com.alipay.android.appHall.baseService.XmlAppService;
import com.alipay.android.appHall.common.CacheSet;
import com.alipay.android.appHall.common.Defines;
import com.alipay.android.appHall.component.accountbox.AlipaySetContentListener;
import com.alipay.android.appHall.component.accountbox.AlipayTypeListener;
import com.alipay.android.appHall.component.accountbox.SaveAccountListener;
import com.alipay.android.client.AlipayApplication;
import com.alipay.android.client.RootActivity;
import com.alipay.android.client.constant.AlipayHandlerMessageIDs;
import com.alipay.android.client.constant.Constant;
import com.alipay.android.client.util.BaseHelper;
import com.alipay.android.core.ActivityShell;
import com.alipay.android.core.MBus;
import com.alipay.android.core.MsgAction;
import com.alipay.android.core.ParamString;
import com.alipay.android.core.Runtime;
import com.alipay.android.core.expapp.NormalPage.RequestData;
import com.alipay.android.core.expapp.api.Interpreter;
import com.alipay.android.log.AlipayLogAgent;
import com.alipay.android.log.Constants;
import com.alipay.android.log.StorageStateInfo;
import com.alipay.android.net.RequestMaker;
import com.alipay.android.net.http.HttpRequestMaker;
import com.alipay.android.safepay.MobileSecurePayer;
import com.alipay.android.safepay.SafePayResultChecker;
import com.alipay.android.ui.bill.BillManagerRootControllerActivity;
import com.alipay.android.util.JsonConvert;
import com.alipay.android.util.LogUtil;
import com.alipay.platform.core.AlipayApp;
import com.alipay.platform.view.ActivityMediator;

/**
 * @author sanping.li
 * 
 */
public class ExpAppRuntime extends Runtime {
    /**
     * 缓存
     */
    private Map<String, Object> mhmCache;
    //
    // response data.
    private Map<String, HashMap<String, Object>> mhmResponseData;
    /**
     * 字符串常量
     */
    private Map<String, String> values;
    /**
     * rules常量
     */
    private Map<String, String> rules;
    /**
     * 表达式常量
     */
    private Map<String, String> exps;

    private StorageStateInfo storageStateInfo;

    private String enterId;

    private String mFrom;

    //
    private ExpAppHandler appPkgHandler;

    private ExpAppNavigation mAppNavigation;

    //
    private RequestMaker mRequestMaker;

    //
    private BaseServiceProvider baseServiceProvider;
    private boolean mAutoDismiss;

    @Override
    public void handleMsg(String sourceId, int action, String params) {
        // TODO Auto-generated method stub

    }

    @Override
    public void callback(String targetId, int result, final Object params) {
        RootActivity activity = (RootActivity) mContext;
        AlipayApplication application = (AlipayApplication) mContext.getApplicationContext();

        int requestCode = Integer.parseInt(targetId);

        if (alipayTypeListener != null && result == Activity.RESULT_OK) {
            final int res = result;
            activity.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    alipayTypeListener.setAlipayType(res, params);
                }
            });
        }

        if ((requestCode == Constant.REQUEST_LEPHONE_CONTACT || requestCode == Constant.REQUEST_CONTACT)) {
            Intent localContacts = null;
            if (params instanceof Intent) {
                localContacts = (Intent) params;
            }

            if (localContacts != null && setContentListener != null) {
                if (result == Activity.RESULT_OK)
                    setContentListener.accountBoxSetContent(result, params);
            }
            return;
        }

        if (requestCode == Defines.REQUEST_CODE_AGENT_PAY) {
            if (result == Activity.RESULT_OK) {
                exit();
                // 注销当前页面
                // 成功，返回到应用中心——九宫格
            } else if (result == Activity.RESULT_CANCELED) {
                // 保留当前页面
                // 失败，但是因为订单已经创建，所以需要跳转到消费记录页面
            }
        } else if (requestCode == Constant.REQUEST_LOGIN_BACK) {
            if (result == Activity.RESULT_OK) {
                if (doServiceCallback(null))
                    return;
                // Handle pending request data.
                RequestData requestData = mAppNavigation.isEmpty() ? null : mAppNavigation
                    .peek4Statck().mPendingRequestData;
                if (requestData != null) {
                    mAppNavigation.peek4Statck().requestData(requestData.interfaceId,
                        requestData.ruleId, ActivityMediator.ACT_CLEAN, requestData.params);
                    mAppNavigation.peek4Statck().mPendingRequestData = null;
                } else
                    exit();
            } else {
                //mFrom = Main.TAB_INDEX_DEFAULT;
                exit();
            }
        } else if (requestCode == Defines.REQUEST_CODE_BAIN_MOBILE) {
            if (result == Activity.RESULT_OK) {
                if (doServiceCallback(null))
                    return;
            }
        } else if (requestCode == AlipayHandlerMessageIDs.RQF_PAY
                   || requestCode == AlipayHandlerMessageIDs.RQF_SAFE_TOKEN_INIT) {
            application.closeProgress();
            if (AlipayApplication.mSafepayInstallreceiver != null) {
                application.unregisterReceiver(AlipayApplication.mSafepayInstallreceiver);
                AlipayApplication.mSafepayInstallreceiver = null;
            }
            if (accountListener != null && accountListener.isNeedSave()) {
                accountListener.saveAccount();
            }

            if (params != null && requestCode == AlipayHandlerMessageIDs.RQF_SAFE_TOKEN_INIT) {
                AlipayApp app = (AlipayApp) mContext.getApplicationContext();// Application
                try {
                    String content = params.toString();
                    SafePayResultChecker resultChecker = new SafePayResultChecker(content);
                    result = Integer.valueOf(resultChecker.getReturnStr("resultStatus")) == 9000 ? MobileSecurePayer.PAY_OK
                        : MobileSecurePayer.PAY_FAIL;
                    app.putInfoData(AlipayApplication.PAY_ACCOUNT,
                        resultChecker.getResultField("userId"));
                } catch (Exception e) {
                }
            }

            if (doServiceCallback(result == MobileSecurePayer.PAY_OK ? "9000" : "*"))
                return;
            if (result == MobileSecurePayer.PAY_OK) {
                exit();
            } else {
                //				mFrom = Main.TAB_INDEX_RECORDS_CONSUMPTION;
                //                Intent intent = new Intent();
                //                intent.setClass(mContext, BillManagerRootControllerActivity.class);
                //                mContext.startActivity(intent);

                application.getMBus().sendMsg("", "09999985", MsgAction.ACT_LAUNCH, null);

                exit();
            }
        } else if (requestCode == Constant.REQUEST_GET_ACCOUNT) {
            if (setContentListener != null)
                setContentListener.accountBoxSetContent(result, params);
        } else if (requestCode == AlipayHandlerMessageIDs.RQF_SAFE_TOKEN_INIT) {
            if (setContentListener != null)
                setContentListener.accountBoxSetContent(result, params);
        } else if (requestCode == AlipayHandlerMessageIDs.SAFEPAY_CHECK_GET_AUTHLIST) {
            safepayListHandle(result, params.toString());
        } else if (requestCode == Defines.REQUEST_CODE_QRCODE) {
            if (result == Activity.RESULT_OK) {
                String snapshotQrCode = "";
                Intent data = (Intent) params;
                if (data != null && data.getData() != null) {
                    snapshotQrCode = data.getData().toString();

                    String curPrefix = "";
                    // 分离原有二维码前缀[Alipay]:
                    if (snapshotQrCode.indexOf("http") < 0 && snapshotQrCode.indexOf("Alipay") >= 0) {
                        int index = snapshotQrCode.indexOf(":");
                        if (index >= 0) {
                            curPrefix = snapshotQrCode.substring(0, index + 1);
                        }
                    }
                    // 分离升级后二维码前缀
                    // http://qr.alipay.com/12312342435
                    else if (snapshotQrCode.indexOf("https://qr.alipay.com/") >= 0
                             || snapshotQrCode.indexOf("http://qr.alipay.com/") >= 0
                            		/* || snapshotQrCode.indexOf("http://barcodeprod.test.alipay.net/") >= 0
                            				 || snapshotQrCode.indexOf("https://barcodeprod.test.alipay.net/") >= 0
                            				 || snapshotQrCode.indexOf("http://barcodeprod.n65.alipay.net/") >= 0
                            				 || snapshotQrCode.indexOf("https://barcodeprod.n65.alipay.net/") >= 0
                            				 || snapshotQrCode.indexOf("https://barcodeprod2.p38.alipay.net/") >= 0
                            				 || snapshotQrCode.indexOf("http://barcodeprod2.p38.alipay.net/") >= 0
                            				 || snapshotQrCode.indexOf("https://barcodeprod.stable.alipay.net/") >= 0
                            				 || snapshotQrCode.indexOf("http://barcodeprod.stable.alipay.net/") >= 0
                            		 
                    		 */) {
                        int index = snapshotQrCode.lastIndexOf("/");
                        if (index >= 0) {
                            curPrefix = snapshotQrCode.substring(0, index + 1);
                            snapshotQrCode = snapshotQrCode.substring(index + 1);
                        }
                    }

                   LogUtil.logOnlyDebuggable("qq","REQUEST_CODE_QRCODE account="
                                       + activity.getAccountName(true) + ", qrcodePrefix="
                                       + Constant.STR_QRCODE_PREFIX + ", curPrefix=" + curPrefix);

                    getCurrentPage().getEngine().setRequestCache(Defines.qrcode, snapshotQrCode);
                    getCurrentPage().getEngine().setRequestCache(Defines.account,
                        activity.getAccountName(true));
                    getCurrentPage().getEngine().setRequestCache(Defines.qrcodePrefix,
                        Constant.STR_QRCODE_PREFIX);
                    getCurrentPage().getEngine()
                        .setRequestCache(Defines.qrcodeCurPrefix, curPrefix);

                    // Execute the corresponding rules or expression.
                    application.setActivity(activity);//设置执行表达式的上下文
                    String exp = null;
                    String expId = (String) getCurrentPage().getEngine().getRequestCache(
                        Defines.scanRule);
                    if (expId != null && expId.length() > 0) {
                        exp = (String) getExp(expId);
                    }
                    if (exp != null && exp.length() > 0) {
                        // appRunTime.getCurrentPage().processRule(rule,
                        // resultStatus);
                        getCurrentPage().interpreter(expId, exp);
                        
                        Constants.parserQR = true;
                        Constants.paipaiStep3Start = System.currentTimeMillis();
                        //悦享拍记录第二阶段埋点
                        
                        long paipaiTimeBlock2 = Constants.paipaiStep3Start - Constants.paipaiStep1End;
                        AlipayLogAgent.writeLog(activity,
                        		Constants.BehaviourID.MONITOR, "paipai", null,
                				null, null, null,
                				null, null, "android",CacheSet.getInstance(activity).getString(Constant.CHANNELS),"parseCode",paipaiTimeBlock2+"");
                        
                        
                        
                    } else {
                        exit();
                    }
                }

                else {
                    exit();
                }
            } else {
                exit();
            }
        }
    }

    private void safepayListHandle(int ret, String strRet) {
        RootActivity activity = (RootActivity) mContext;
        AlipayApplication application = (AlipayApplication) mContext.getApplicationContext();
        LogUtil.logMsg(Constant.LOG_LEVEL_DEBUG, "checkSafePayList", strRet);
        application.closeProgress();
        if (ret == 1) { // 成功
            if (strRet != null) {
                try {
                    JSONObject objContent = BaseHelper.string2JSON(strRet, ";");
                    String result = objContent.getString(Constant.SAFE_PAY_RESULT_ARRAY);

                    if (result != null && !result.equals("")) {
                        JSONObject tObject = new JSONObject(result);
                        JSONArray tContactsArray = tObject
                            .optJSONArray(Constant.SAFE_PAY_RESULT_ACCOUNTDATA);
                        if (tContactsArray != null && tContactsArray.length() > 0) {
                            for (int i = 0; i < tContactsArray.length(); ++i) {
                                JSONObject object = tContactsArray.optJSONObject(i);
                                if (object != null
                                    && activity.getUserId().equals(object.opt("userid"))) {
                                    application.putInfoData(AlipayApplication.PAY_ACCOUNT,
                                        activity.getUserId());
                                    if (doServiceCallback("9000"))
                                        return;
                                    exit();
                                    return;
                                }
                            }
                        } else {
                            // 没有获取到有效的认证账号
                        }
                    } else {
                        // 没有获取到有效的认证账号
                    }
                } catch (JSONException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }
            String extern_token = activity.getExtToken();
            if (extern_token != null && extern_token.length() > 0)// 有账号
                BaseHelper.safeTokenInit(activity,
                    ((XmlAppService) baseServiceProvider).getHandler(), null, extern_token,
                    "otp_account");
            else
                BaseHelper
                    .safeTokenInit(activity, ((XmlAppService) baseServiceProvider).getHandler(),
                        null, null, "otp_noaccount");
        } else { // 失败
            if (doServiceCallback("*"))
                return;
            //			mFrom = Main.TAB_INDEX_RECORDS_CONSUMPTION;
            Intent intent = new Intent();
            intent.setClass(mContext, BillManagerRootControllerActivity.class);
            mContext.startActivity(intent);
            exit();
        }
    }

    @Override
    public void create(String params, Bundle bundle) {
        mhmCache = new HashMap<String, Object>();
        mhmResponseData = new HashMap<String, HashMap<String, Object>>();
        values = new HashMap<String, String>();
        rules = new HashMap<String, String>();
        exps = new HashMap<String, String>();

        storageStateInfo = StorageStateInfo.getInstance();
        // record appId when start app
        storageStateInfo.putValue(Constants.STORAGE_APPID, mPkgId);

        ParamString paramString = new ParamString(params);
        enterId = paramString.getValue("viewId");
        if (enterId == null) {
            enterId = (String) mManifestDoc.getManifest("entry");
        }
        String str = paramString.getValue("_source_");
        if (str != null && str.length() > 0)
            mFrom = str;

        initParams(paramString);

        baseServiceProvider = new XmlAppService((ActivityShell) mContext);
        mAppNavigation = new ExpAppNavigation(this);

        mRequestMaker = new HttpRequestMaker(mContext, mPath + ExpAppHandler.interfaceName);
        appPkgHandler = new ExpAppHandler(this);
        appPkgHandler.start(enterId);
    }

    private void initParams(ParamString paramString) {//目前只处理悦享拍的
        if (getPkgId().equalsIgnoreCase("10000007")) {
            String qrcode = paramString.getValue(Constant.QRCODE);
            AlipayApplication application = (AlipayApplication) mContext.getApplicationContext();
            if (qrcode != null && qrcode.length() > 0) {
                application.putGlobalData("10000007-qrcode", qrcode);
                application.putGlobalData("bootFromPaipai", true);
            }else{
                application.removeGlobalData("10000007-qrcode");
                application.removeGlobalData("bootFromPaipai");
            }
        }
    }

    @Override
    public void start(Context context) {
        // TODO Auto-generated method stub

    }

    @Override
    public void reStart(Context context) {
        // TODO Auto-generated method stub

    }

    @Override
    public void resume(Context context) {
        AlipayApplication application = (AlipayApplication) mContext.getApplicationContext();
        if (mAutoDismiss) {
            application.closeProgress();
            mAutoDismiss = false;
        }
        if (AlipayApplication.mFinishPage) {
            AlipayApplication.mFinishPage = false;
            AlipayApplication.mFinishPageFirst = false;
            exit();
        }
    }

    @Override
    public void pause(Context context) {
        // TODO Auto-generated method stub

    }

    @Override
    public void stop(Context context) {
        // TODO Auto-generated method stub

    }

    @Override
    public void destroy(Context context) {
        synchronized (this) {
            unRegisterRuntime();
            releaseWakeLock();
        }
    }

    @Override
    public void saveState(Context context, Bundle bundle) {
        // TODO Auto-generated method stub

    }

    @Override
    public void newIntent(Context context, Intent intent) {
        // TODO Auto-generated method stub

    }

    @Override
    public boolean keyDown(Context context, int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            if (getCurrentPage() != null)
                getCurrentPage().onCancel();
            return true;
        }
        return false;
    }

    public RequestMaker getRequestMaker() {
        return mRequestMaker;
    }

    public Page push(String pageId) {
        Helper.hideInputPanel(mContext, getCurrentPage().getFormPanel());
        appPkgHandler.loadPage(pageId);
        return null;
    }

    public ExpAppHandler getAppHandler() {
        return appPkgHandler;
    }

    public void pop(int levels) {
        Helper.hideInputPanel(this.getContext(), this.getCurrentPage().getFormPanel());
        mAppNavigation.pop4Stack(levels);
    }

    public Page getCurrentPage() {
        return mAppNavigation.getCurrentPage();
    }

    public int getLocation(String viewId) {
        return mAppNavigation.getLocation(viewId);
    }

    public Object getRequestCache(String key) {
        return mhmCache.get(key);
    }

    public void setRequestCache(String key, Object value) {
        mhmCache.put(key, value);
    }

    public void removeRequestCache(String keyca) {
        mhmCache.remove(keyca);
    }

    public void showAlert(int type, String content, String ok, String cancel,
                          ArrayList<String> params) {
        String msg = content;
        String ok_exp = ok;
        String cancel_exp = cancel;
        if (content != null && content.startsWith(AppUiElementType.Reference))
            msg = getValue(content.replace(AppUiElementType.Reference, ""));
        if (ok != null && ok.startsWith(AppUiElementType.Reference))
            ok_exp = getValue(ok.replace(AppUiElementType.Reference, ""));
        if (cancel != null && cancel.startsWith(AppUiElementType.Reference))
            cancel_exp = getValue(cancel.replace(AppUiElementType.Reference, ""));

        ArrayList<String> args = null;
        if (params != null) {
            args = new ArrayList<String>();
            for (String str : params) {
                if (str != null && str.startsWith(AppUiElementType.Reference))
                    str = getValue(str.replace(AppUiElementType.Reference, ""));
                args.add(str);
            }
        }

        AlipayApplication application = (AlipayApplication) mContext.getApplicationContext();
        application.alert(this, type, msg, ok_exp, cancel_exp, args);
    }

    public Page createPage(String pageName) {
        return mAppNavigation.createPage(pageName);
    }

    public void push2Stack(Page page) {
        // 判断是否为入口界面
//        if (page != null && page.getPageName().equals(mManifestDoc.getManifest("entry"))) {
//        	
//        	for (int i = 0; i < HallData.sordedIdArray.length; i++) {
//				if (HallData.sordedIdArray[i].equals(mPkgId)) {
//					AlipayLogAgent.writeLog(mContext, Constants.BehaviourID.BIZLAUNCHED, null, null, mPkgId, getPkgVersion(), enterId, Constants.WALLETHOME, "homeApp"+(i+1)+"Icon", null);
//					break;
//				}else{
//					AlipayLogAgent.writeLog(mContext, Constants.BehaviourID.BIZLAUNCHED, null, null, mPkgId, getPkgVersion(), enterId, Constants.HOMEAPPSHOW, mPkgId+"Icon", null);
//				}
//			}
//        	
//        } else {
//			//跳转
////			AlipayLogAgent.writeLog(mContext, Constants.BehaviourID.CLICKED, mPkgId, getPkgVersion(), page.getRealPageName(), page.getPageName(), null);
//        }

        mAppNavigation.push2Stack(page);
    }

    public void pay(String tradeNO, String partnerID, String bizType, String bizSubType, String rule) {
        AlipayApplication application = (AlipayApplication) mContext.getApplicationContext();
        mAutoDismiss = true;
        application.showProgress();
        baseServiceProvider.pay(tradeNO, partnerID, bizType, bizSubType, rule);
    }

    public void bindMobile(String ruleID) {
        baseServiceProvider.bindMobile(ruleID);
    }

    public void safeTokenInit(String rule) {
        AlipayApplication application = (AlipayApplication) mContext.getApplicationContext();
        mAutoDismiss = true;
        application.showProgress();
        AlipayApplication.mFinishPageFirst = true;
        baseServiceProvider.safeTokenInit(rule);
    }

    public boolean login(String ruleId) {
        RootActivity activity = (RootActivity) mContext;
        if (activity.getUserData() != null)
            return true;
        else {
            baseServiceProvider.login(ruleId);
            return false;
        }
    }

    public void doCaptureBarcode(String previewHint, String rule, String curCodeType) {
        // TODO Auto-generated method stub
        String hint = previewHint;
        if (previewHint != null && previewHint.startsWith(AppUiElementType.Reference))
            hint = getValue(previewHint.replace(AppUiElementType.Reference, ""));

        baseServiceProvider.captureBarcode(hint, rule, curCodeType);
    }

    private boolean doServiceCallback(String op) {
        String id = null;
        if (op == null)
            id = baseServiceProvider.getExpId();
        else
            id = baseServiceProvider.getRuleId();
        if (id != null && id.length() > 0) {
            if (id.startsWith(AppUiElementType.Reference))
                id = id.replace(AppUiElementType.Reference, "");
            String exp = null;
            if (op == null)
                exp = getValue(id);
            else
                exp = getRule(getRule(id), op);
            if (exp != null) {
                String exId = id;
                if (op != null)
                    exId = "ruleId::" + id + "|select::" + op;
                getCurrentPage().interpreter(exId, exp);
                return true;
            }
        }
        return false;
    }

    public void openUrlBrowser(String url) {
        // TODO Auto-generated method stub
        baseServiceProvider.openUrlBrowser(url);
    }

    public Map<String, HashMap<String, Object>> getResponsDataHM() {
        return mhmResponseData;
    }

    public void agent_pay(String tradeNO, String partnerID, String bizType, String bizSubType) {
        baseServiceProvider.agentPay(tradeNO, partnerID, bizType, bizSubType);
    }

    public File getFile(String path) {
        return mCertHandler.verify(path);
    }

    public void execApp(String funcId, String view, String params) {
        RootActivity activity = (RootActivity) mContext;
        String str = "";
        if (view != null) {
            str = "viewId=" + view;
        }
        if (params != null) {
            if (str.length() > 0) {
                str += ("&" + params);
            } else {
                str = params;
            }
        }
        activity.getMBus().sendMsg(mPkgId, funcId, MsgAction.ACT_LAUNCH, str);

        exit();// 释放自身
    }

    public void setFrom(String from) {
        mFrom = from;
    }

    private SaveAccountListener accountListener;

    public void setSaveAccountListener(SaveAccountListener accountListener) {
        this.accountListener = accountListener;
    }

    private AlipaySetContentListener setContentListener;

    public void setAlipaySetContentListener(AlipaySetContentListener listener) {
        this.setContentListener = listener;
    }

    private AlipayTypeListener alipayTypeListener;

    public void setAlipayTypeListener(AlipayTypeListener listener) {
        this.alipayTypeListener = listener;
    }

    /**
     * 处理rule
     */
    public String getRule(String rule, Object operand) {
        try {
            HashMap<String, Object> map = JsonConvert.Json2Map(new JSONObject(rule));
            if (map.containsKey(operand.toString())) {
                return (String) map.get(operand.toString());
            } else if (map.containsKey("*")) {
                return (String) map.get("*");
            }
        } catch (JSONException e) {
            LogUtil.logContainerDebuggable(Interpreter.TAG,
                "Rule :" + rule + " Encountered Exception:" + e.getMessage());
            e.printStackTrace();
        }
        return null;
    }

    /**
     * EXP的配置数据
     */
    public void addValue(String id, String value) {
        values.put(id.toLowerCase(), value);
    }

    public void addRule(String id, String value) {
        rules.put(id.toLowerCase(), value);
    }

    public void addExp(String id, String value) {
        exps.put(id.toLowerCase(), value);
    }

    public String getValue(String id) {
        String str = id.toLowerCase();
        if (!values.containsKey(str)) {
            LogUtil.logContainerDebuggable("RES", "Can't find String/Expression : id = " + id);
            return id;
        }
        return values.get(str);
    }

    public String getRule(String id) {
        String str = id.toLowerCase();
        if (!rules.containsKey(str)) {
            LogUtil.logContainerDebuggable("RES", "Can't find Rule : id = " + id);
            return null;
        }
        return rules.get(str);
    }

    public String getExp(String id) {
        return getValue(id.toLowerCase());
    }

    /**
     * 存储JSONARRAY会有问题？
     */
    public void saveToDisk(String key, Object value) {
        SharedPreferences preferences = mContext.getSharedPreferences("db" + mPkgId,
            Context.MODE_PRIVATE);
        Editor editor = preferences.edit();
        editor.putString(key, value.toString());
        editor.commit();
    }

    public Object getFromDisk(String key) {
        SharedPreferences preferences = mContext.getSharedPreferences("db" + mPkgId,
            Context.MODE_PRIVATE);
        String ret = preferences.getString(key, null);

        try {
            if (ret != null) {
                return JsonConvert.Json2Array(new JSONArray(ret));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return ret;
    }

    public void addToDisk(String key, Object value) {
        SharedPreferences preferences = mContext.getSharedPreferences("db" + mPkgId,
            Context.MODE_PRIVATE);
        String val = preferences.getString(key, "[]");

        try {
            JSONArray array = new JSONArray(val);
            array.put(value);

            Editor editor = preferences.edit();
            editor.putString(key, array.toString());
            editor.commit();
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public void removeFromDisk(String key) {
        SharedPreferences preferences = mContext.getSharedPreferences("db" + mPkgId,
            Context.MODE_PRIVATE);
        Editor editor = preferences.edit();
        editor.remove(key);
        editor.commit();
    }

    public void removeAllFromDisk() {
        SharedPreferences preferences = mContext.getSharedPreferences("db" + mPkgId,
            Context.MODE_PRIVATE);
        Editor editor = preferences.edit();
        editor.clear();
        editor.commit();
    }

    @Override
    public void exit() {
        AlipayApplication application = (AlipayApplication) mContext.getApplicationContext();
        MBus mBus = application.getMBus();
        mBus.sendMsg(mPkgId, null, MsgAction.ACT_EXIT, null);
        if (isApp()) {

            RootActivity activity = (RootActivity) mContext;
            if (mFrom != null && mFrom.equals("1")) {
                Intent intent = new Intent();
                intent.setClass(mContext, BillManagerRootControllerActivity.class);
                mContext.startActivity(intent);
            }
            activity.finish();
        }
    }

    // ==================================Timer=========================================================

    private PowerManager.WakeLock wakeLock;
    public final static String TIMER_TAG = "timer";
    public final static int RUNTIME_TIMER = 1;

    public void acquireWakeLock() {
        if (wakeLock == null) {
            PowerManager pm = (PowerManager) mContext.getSystemService(Context.POWER_SERVICE);
            wakeLock = pm.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, TIMER_TAG);
            wakeLock.acquire();
        }
    }

    private void releaseWakeLock() {
        if (wakeLock != null && wakeLock.isHeld()) {
            wakeLock.release();
            wakeLock = null;
        }

    }

    private HashMap<String, Timer> runTimeTimerMap;

    private void registerTimer(String tag, Timer timer) {
        if (runTimeTimerMap == null) {
            runTimeTimerMap = new HashMap<String, Timer>();
        }

        if (tag == null || tag.length() == 0) {
            String tempTag = String.valueOf(runTimeTimerMap.size());
            runTimeTimerMap.put(tempTag, timer);
        } else {
            runTimeTimerMap.put(tag, timer);
        }
    }

    /**
     * 释放单个timer
     * 
     * @param tag
     * @param timer
     */
    private void unRegisterTimer(String tag) {
        if (runTimeTimerMap == null || runTimeTimerMap.size() == 0) {
            return;
        }

        Timer tempTimer = runTimeTimerMap.get(tag);
        if (tempTimer != null) {
            tempTimer.cancel();
        }

        runTimeTimerMap.remove(tag);
    }

    /**
     * 注册运行时对象接口
     */
    public void registerRuntimeObject(int runTimeType, String tag, Object runTimeObject) {
        switch (runTimeType) {
            case RUNTIME_TIMER:
                registerTimer(tag, (Timer) runTimeObject);
                break;

            default:
                break;
        }
    }

    /**
     * 释放运行时对象接口
     */
    public void unRegisterRuntimeObject(int runTimeType, String tag) {
        switch (runTimeType) {
            case RUNTIME_TIMER:
                unRegisterTimer(tag);
                break;

            default:
                break;
        }
    }

    /**
     * 释放所有timer
     */
    public void unRegisterTimers() {
        if (runTimeTimerMap == null || runTimeTimerMap.size() == 0) {
            return;
        }
        Set<String> keySet = runTimeTimerMap.keySet();
        for (String tag : keySet) {
            Timer tempTimer = runTimeTimerMap.get(tag);
            tempTimer.cancel();
        }
        runTimeTimerMap.clear();
    }

    /**
     * 释放所有运行时对象接口
     */
    public void unRegisterRuntime() {
        unRegisterTimers();
    }

    public void setUserDataDirty(boolean card, boolean bill, boolean coupon) {
        RootActivity activity = (RootActivity) mContext;
        activity.getUserData().setBillDirty(bill);
        activity.getUserData().setVoucherDirty(coupon);
        activity.getUserData().resetStatus();
    }
}
