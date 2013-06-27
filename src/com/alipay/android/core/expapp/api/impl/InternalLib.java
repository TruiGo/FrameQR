/**
 * 
 */
package com.alipay.android.core.expapp.api.impl;

import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Vector;

import org.json.JSONArray;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.text.ClipboardManager;
import android.widget.Toast;

import com.alipay.android.appHall.appManager.HallData;
import com.alipay.android.appHall.common.Defines;
import com.alipay.android.appHall.component.UIButton;
import com.alipay.android.appHall.component.UIWebBox;
import com.alipay.android.appHall.component.share.ShareEntry;
import com.alipay.android.appHall.uiengine.Computable;
import com.alipay.android.appHall.uiengine.UIInterface;
import com.alipay.android.appHall.uiengine.UIList;
import com.alipay.android.client.RootActivity;
import com.alipay.android.client.paipaihelper.PaipaiRouteParser;
import com.alipay.android.core.expapp.AppUiElementType;
import com.alipay.android.core.expapp.Page;
import com.alipay.android.core.expapp.api.Interpreter;
import com.alipay.android.core.expapp.api.Lib;
import com.alipay.android.util.JsonConvert;
import com.alipay.android.util.LogUtil;
import com.alipay.platform.core.AlipayApp;
import com.eg.android.AlipayGphone.R;

/**
 * @author sanping.li
 * 
 */
public class InternalLib implements Lib {
    /**
     * 函数名
     */

    public static final String[] NAMES = { "ctrl_value_set", "ctrl_value_get", "ctrl_visible_set",
                                          "ctrl_visible_get", "ctrl_enable_set", "ctrl_enable_get",
                                          "ctrl_index_get", "ctrl_compute", "ctrl_reset",
                                          "ctrl_data_set", "ctrl_data_add", "ctrl_data_get",
                                          "ctrl_data_clear", "ctrl_nextpage_get",
                                          "ctrl_pagesize_get", "ctrl_exps_set", "ctrl_delexp_set",
                                          "ctrl_total_set", "loadUrl", "loadData",

                                          "exec", "res_edit", "compute", "ternary",
                                          "pattern_matches", "value_format", "openurl", "stok",

                                          "compare", "compare_string",

                                          "value_get",

                                          "request_data", "response_get",

                                          "cache_set", "cache_get", "cache_add", "cache_clear",
                                          "cache_add_all", "cache_remove", "global_set",
                                          "global_get", "global_remove", "global_clear",
                                          "disk_set", "disk_get", "disk_add", "disk_remove",
                                          "disk_removeall",

                                          "push", "pop", "up", "down", "show_msg", "share_info",
                                          "exit", "refresh", "goto", "pasteboard_set",
                                          "pasteboard_get",

                                          "do_pay", "do_login", "do_agent_pay", "safe_token_init",
                                          "client_info",
                                          
                                          "user_info_dirty",

                                          "device_capture_barcode", "device_uuid", "device_time",
                                          "do_bindmobile","paipai_routeparser"};

    public static final int CTRL_VALUE_SET = 0;
    public static final int CTRL_VALUE_GET = CTRL_VALUE_SET + 1;
    public static final int CTRL_VISIBLE_SET = CTRL_VALUE_GET + 1;
    public static final int CTRL_VISIBLE_GET = CTRL_VISIBLE_SET + 1;
    public static final int CTRL_ENABLE_SET = CTRL_VISIBLE_GET + 1;
    public static final int CTRL_ENABLE_GET = CTRL_ENABLE_SET + 1;
    public static final int CTRL_INDEX_GET = CTRL_ENABLE_GET + 1;
    public static final int CTRL_COMPUTE = CTRL_INDEX_GET + 1;
    public static final int CTRL_RESET = CTRL_COMPUTE + 1;
    public static final int CTRL_DATA_SET = CTRL_RESET + 1;
    public static final int CTRL_DATA_ADD = CTRL_DATA_SET + 1;
    public static final int CTRL_DATA_GET = CTRL_DATA_ADD + 1;
    public static final int CTRL_DATA_CLEAR = CTRL_DATA_GET + 1;
    public static final int CTRL_NEXTPAGE_GET = CTRL_DATA_CLEAR + 1;
    public static final int CTRL_PAGESIZE_GET = CTRL_NEXTPAGE_GET + 1;
    public static final int CTRL_EXPS_SET = CTRL_PAGESIZE_GET + 1;
    public static final int CTRL_DELEXP_SET = CTRL_EXPS_SET + 1;
    public static final int CTRL_TOTAL_SET = CTRL_DELEXP_SET + 1;
    public static final int CTRL_LOAD_URL = CTRL_TOTAL_SET + 1;
    public static final int CTRL_LOAD_DATA = CTRL_LOAD_URL + 1;

    public static final int EXEC = CTRL_LOAD_DATA + 1;
    public static final int RES_EDIT = EXEC + 1;
    public static final int COMPUTE = RES_EDIT + 1;
    public static final int TERNARY = COMPUTE + 1;
    public static final int PATTERN_MATCHES = TERNARY + 1;
    public static final int VALUE_FORMAT = PATTERN_MATCHES + 1;
    public static final int OPENURL = VALUE_FORMAT + 1;
    public static final int STOK = OPENURL + 1;

    public static final int COMPARE = STOK + 1;
    public static final int COMPARE_STRING = COMPARE + 1;

    public static final int VALUE_GET = COMPARE_STRING + 1;

    public static final int REQUEST_DATA = VALUE_GET + 1;
    public static final int RESPONSE_GET = REQUEST_DATA + 1;

    public static final int CACHE_SET = RESPONSE_GET + 1;
    public static final int CACHE_GET = CACHE_SET + 1;
    public static final int CACHE_ADD = CACHE_GET + 1;
    public static final int CACHE_CLEAR = CACHE_ADD + 1;
    public static final int CACHE_ADD_ALL = CACHE_CLEAR + 1;
    public static final int CACHE_REMOVE = CACHE_ADD_ALL + 1;
    public static final int GLOBAL_SET = CACHE_REMOVE + 1;
    public static final int GLOBAL_GET = GLOBAL_SET + 1;
    public static final int GLOBAL_REMOVE = GLOBAL_GET + 1;
    public static final int GLOBAL_CLEAR = GLOBAL_REMOVE + 1;
    public static final int DISK_SET = GLOBAL_CLEAR + 1;
    public static final int DISK_GET = DISK_SET + 1;
    public static final int DISK_ADD = DISK_GET + 1;
    public static final int DISK_REMOVE = DISK_ADD + 1;
    public static final int DISK_REMOVEALL = DISK_REMOVE + 1;

    public static final int PUSH = DISK_REMOVEALL + 1;
    public static final int POP = PUSH + 1;
    public static final int UP = POP + 1;
    public static final int DOWN = UP + 1;
    public static final int SHOW_MSG = DOWN + 1;
    public static final int SHARE_INFO = SHOW_MSG + 1;
    public static final int EXIT = SHARE_INFO + 1;
    public static final int REFRESH = EXIT + 1;
    public static final int GOTO = REFRESH + 1;

    public static final int PASTEBOARD_SET = GOTO + 1;
    public static final int PASTEBOARD_GET = PASTEBOARD_SET + 1;

    public static final int DO_PAY = PASTEBOARD_GET + 1;
    public static final int DO_LOGIN = DO_PAY + 1;
    public static final int DO_AGENT_PAY = DO_LOGIN + 1;
    public static final int SAFE_TOKEN_INIT = DO_AGENT_PAY + 1;
    public static final int CLIENT_INFO = SAFE_TOKEN_INIT + 1;
    
    public static final int USER_INFO_DIRTY=CLIENT_INFO+1;

    public static final int DEVICE_CAPTURE_BARCODE = USER_INFO_DIRTY + 1;
    public static final int DEVICE_UUID = DEVICE_CAPTURE_BARCODE + 1;
    public static final int DEVICE_TIME = DEVICE_UUID + 1;

    public static final int DO_BIND_MOBILE = DEVICE_TIME + 1;
    public static final int PAIPAI_ROUTEPARSER = DO_BIND_MOBILE+1;
    
    /**
     * 外部函数，用来扩展
     */
    @SuppressWarnings({ "rawtypes" })
    private Hashtable mExternalLibs;

    private Operate mOperate;

    public InternalLib() {
        mOperate = new Operate();
    }

    @SuppressWarnings({ "unchecked", "rawtypes" })
    @Override
    public Object invoke(AstFunction function, Page page) {
        int index = -1;
        for (int i = 0; i < NAMES.length; ++i) {
            if (NAMES[i].equals(function.getName())) {
                index = i;
                break;
            }
        }
        Vector args = function.getArgs();// 函数参数
        try {
            AlipayApp app = (AlipayApp) page.getEngine().getContext().getApplicationContext();// Application
            RootActivity activity = (RootActivity) page.getEngine().getContext();
            switch (index) {
                case CTRL_VALUE_GET:// 获取控件值
                    if (args.size() < 1)
                        break;
                    String idg = (String) args.elementAt(0);
                    UIInterface widg = (UIInterface) page.findWidget(idg);
                    return widg.getValue();
                case CTRL_VALUE_SET:// 设置控件值
                    if (args.size() < 2)
                        break;
                    String ids = (String) args.elementAt(0);
                    Object value = args.elementAt(1);
                    if (value instanceof String
                        && ((String) value).startsWith(AppUiElementType.Reference)) {
                        value = page.getEngine().getValue(
                            ((String) value).replace(AppUiElementType.Reference, ""));
                    }
                    UIInterface wids = (UIInterface) page.findWidget(ids);
                    wids.setValue(value);
                    break;
                case CTRL_VISIBLE_GET:// 获取控件是否可见
                    if (args.size() < 1)
                        break;
                    String idvg = (String) args.elementAt(0);
                    UIInterface widvg = (UIInterface) page.findWidget(idvg);
                    return (widvg.getVisible() ? "true" : "false");
                case CTRL_VISIBLE_SET:// 设置控件是否可见
                    if (args.size() < 2)
                        break;
                    String idv = (String) args.elementAt(0);
                    Object val = args.elementAt(1);
                    UIInterface widv = (UIInterface) page.findWidget(idv);
                    widv.setVisible(val.equals("true"));
                    break;
                case CTRL_ENABLE_GET:// 获取控件是否可用
                    if (args.size() < 1)
                        break;
                    String ideg = (String) args.elementAt(0);
                    UIInterface wideg = (UIInterface) page.findWidget(ideg);
                    return (wideg.getEnable() ? "true" : "false");
                case CTRL_ENABLE_SET:// 设置控件是否可用
                    if (args.size() < 2)
                        break;
                    String ides = (String) args.elementAt(0);
                    Object valuees = args.elementAt(1);
                    UIInterface wides = (UIInterface) page.findWidget(ides);
                    if (wides instanceof UIButton && valuees.equals("false")) {
                        page.getInputBoxIsNullListener().clearNeedEnableButton();
                    }
                    wides.setEnable(valuees.equals("true"));
                    break;
                case CTRL_INDEX_GET:// 获取控件选中项的索引
                    if (args.size() < 1)
                        break;
                    String idis = (String) args.elementAt(0);
                    UIInterface widis = (UIInterface) page.findWidget(idis);
                    return widis.getSelectedIndex();
                case CTRL_COMPUTE:// 控件计算
                    if (args.size() < 1)
                        break;
                    String idic = (String) args.elementAt(0);
                    Computable widic = (Computable) page.findWidget(idic);
                    ArrayList<Object> ctrl_params = new ArrayList<Object>();
                    for (int i = 1; i < args.size(); ++i) {
                        ctrl_params.add(args.elementAt(i));
                    }
                    return widic.compute(ctrl_params);
                case CTRL_RESET:// 重置组件
                    if (args.size() < 1)
                        break;
                    String idir = (String) args.elementAt(0);
                    Computable widir = (Computable) page.findWidget(idir);
                    widir.reset();
                    break;
                case CTRL_DATA_SET:
                    if (args.size() < 2)
                        break;
                    String idss = (String) args.elementAt(0);
                    Object os = args.elementAt(1);
                    if (os instanceof String) {
                        String str = (String) os;
                        if (str.startsWith(AppUiElementType.Reference))
                            str = page.getEngine().getValue(
                                str.replace(AppUiElementType.Reference, ""));
                        os = JsonConvert.Json2Array(new JSONArray(str));
                    }
                    ArrayList<Object> valuess = (ArrayList<Object>) os;
                    UIList listss = (UIList) page.findWidget(idss);
                    listss.clearData();
                    listss.setData(valuess);
                    break;
                case CTRL_DATA_ADD:
                    if (args.size() < 2)
                        break;
                    String idds = (String) args.elementAt(0);
                    Object odd = args.elementAt(1);
                    if (odd instanceof String) {
                        String str = (String) odd;
                        if (str.startsWith(AppUiElementType.Reference))
                            str = page.getEngine().getValue(
                                str.replace(AppUiElementType.Reference, ""));
                        odd = JsonConvert.Json2Array(new JSONArray(str));
                    }
                    ArrayList<Object> valueds = (ArrayList<Object>) odd;
                    UIList listds = (UIList) page.findWidget(idds);
                    listds.addData(valueds);
                case CTRL_DATA_GET:
                    if (args.size() < 1)
                        break;
                    String iddg = (String) args.elementAt(0);
                    UIList listdg = (UIList) page.findWidget(iddg);
                    return listdg.getData();
                case CTRL_DATA_CLEAR:
                    if (args.size() < 1)
                        break;
                    String idcg = (String) args.elementAt(0);
                    UIList listcg = (UIList) page.findWidget(idcg);
                    listcg.clearData();
                    break;
                case CTRL_NEXTPAGE_GET:
                    if (args.size() < 1)
                        break;
                    String idng = (String) args.elementAt(0);
                    UIList listng = (UIList) page.findWidget(idng);
                    return listng.getCurrentPage();
                case CTRL_PAGESIZE_GET:
                    if (args.size() < 1)
                        break;
                    String idpg = (String) args.elementAt(0);
                    UIList listpg = (UIList) page.findWidget(idpg);
                    return listpg.getPageSize();
                case CTRL_EXPS_SET:
                    if (args.size() < 2)
                        break;
                    String ideps = (String) args.elementAt(0);
                    Object oes = args.elementAt(1);
                    if (oes instanceof String) {
                        String str = (String) oes;
                        if (str.startsWith(AppUiElementType.Reference))
                            str = page.getEngine().getValue(
                                str.replace(AppUiElementType.Reference, ""));
                        oes = JsonConvert.Json2Array(new JSONArray(str));
                    }
                    ArrayList<Object> valueeps = (ArrayList<Object>) oes;
                    UIList listeps = (UIList) page.findWidget(ideps);
                    listeps.setItemExps(valueeps);
                    break;
                case CTRL_DELEXP_SET:
                    if (args.size() < 2)
                        break;
                    String iddes = (String) args.elementAt(0);
                    String ods = (String) args.elementAt(1);
                    if (ods.startsWith(AppUiElementType.Reference))
                        ods = page.getEngine()
                            .getValue(ods.replace(AppUiElementType.Reference, ""));
                    UIList listdes = (UIList) page.findWidget(iddes);
                    listdes.setDelExp(ods);
                    break;
                case CTRL_TOTAL_SET:
                    if (args.size() < 2)
                        break;
                    String idtes = (String) args.elementAt(0);
                    Object oto = args.elementAt(1);
                    int total = 0;
                    if (oto != null && oto instanceof String) {
                        String str = (String) oto;
                        if (str.matches("^\\d*$"))
                            total = Integer.parseInt(str);
                    }
                    UIList listtes = (UIList) page.findWidget(idtes);
                    listtes.setTotal(total);
                    break;
                case CTRL_LOAD_URL:// 浏览器组件加载地址
                    if (args.size() < 2)
                        break;
                    String idu = (String) args.elementAt(0);
                    Object webUrl = args.elementAt(1);
                    if (webUrl instanceof String
                        && ((String) webUrl).startsWith(AppUiElementType.Reference)) {
                        webUrl = page.getEngine().getValue(
                            ((String) webUrl).replace(AppUiElementType.Reference, ""));
                    }
                    Object widu = page.findWidget(idu);
                    if (widu instanceof UIWebBox) {
                        UIWebBox webu = (UIWebBox) widu;
                        webu.loadWebUrl(webUrl.toString());
                    } else {
                        throw new Exception("can't loadUrl" + webUrl);
                    }
                    break;
                case CTRL_LOAD_DATA:// 浏览器组件加载数据
                    if (args.size() < 2)
                        break;
                    String idd = (String) args.elementAt(0);
                    Object webData = args.elementAt(1);
                    if (webData instanceof String
                        && ((String) webData).startsWith(AppUiElementType.Reference)) {
                        webData = page.getEngine().getValue(
                            ((String) webData).replace(AppUiElementType.Reference, ""));
                    }
                    Object widd = page.findWidget(idd);
                    if (widd instanceof UIWebBox) {
                        UIWebBox webu = (UIWebBox) widd;
                        webu.loadWebData(webData.toString());
                    } else {
                        throw new Exception("can't loadData" + webData);
                    }
                    break;

                case EXEC:
                    if (args.size() < 1)
                        break;
                    String exp = (String) args.elementAt(0);
                    String exId = exp;
                    if (exp.startsWith(AppUiElementType.Reference))
                        exp = page.getEngine().getExp(exp.replace(AppUiElementType.Reference, ""));
                    return page.interpreter(exId, exp);

                case RES_EDIT:
                    if (args.size() < 3)
                        break;
                    String mType = (String) args.elementAt(0);
                    String mId = (String) args.elementAt(1);
                    String mValue = (String) args.elementAt(2);
                    if (mType.equalsIgnoreCase("rule")) {
                        page.getEngine().addRule(mId, mValue);
                    } else if (mType.equalsIgnoreCase("string")) {
                        if (mValue.startsWith(AppUiElementType.Reference))
                            mValue = page.getEngine().getValue(
                                mValue.replace(AppUiElementType.Reference, ""));
                        page.getEngine().addValue(mId, mValue);
                    } else if (mType.equalsIgnoreCase("exp")) {
                        page.getEngine().addValue(mId, mValue);
                    }
                    break;
                case COMPUTE:
                    if (args.size() < 2)
                        break;
                    String operator = (String) args.elementAt(0);
                    ArrayList<Object> cparams = new ArrayList<Object>();
                    for (int i = 1; i < args.size(); ++i) {
                        cparams.add(args.elementAt(i));
                    }
                    return mOperate.compute(page.getEngine(), operator, cparams);

                case TERNARY:// 三元表达式
                    if (args.size() < 2)
                        break;
                    String rules = (String) args.elementAt(0);
                    if (rules.startsWith(AppUiElementType.Reference))
                        rules = page.getEngine().getRule(
                            rules.replace(AppUiElementType.Reference, ""));
                    Object select = args.elementAt(1);
                    return page.getEngine().getRule(rules, select);

                case PATTERN_MATCHES:// 正则表达式匹配
                    if (args.size() < 2)
                        break;
                    String regex = (String) args.elementAt(0);
                    if (regex.startsWith(AppUiElementType.Reference))
                        regex = page.getEngine().getValue(
                            regex.replace(AppUiElementType.Reference, ""));
                    String arg = (String) args.elementAt(1);
                    if (arg.startsWith(AppUiElementType.Reference))
                        arg = page.getEngine()
                            .getValue(arg.replace(AppUiElementType.Reference, ""));
                    return PatternHelper.patternMatches(regex, arg);
                case VALUE_FORMAT:
                    if (args.size() < 2)
                        break;
                    String format = (String) args.elementAt(0);
                    if (format.startsWith(AppUiElementType.Reference))
                        regex = page.getEngine().getValue(
                            format.replace(AppUiElementType.Reference, ""));
                    Object ov = args.elementAt(1);
                    if (ov instanceof String) {
                        String str = ov.toString();
                        if (str.startsWith(AppUiElementType.Reference))
                            str = page.getEngine().getValue(
                                str.replace(AppUiElementType.Reference, ""));
                        if (str.matches("^[\\d,.]*$"))
                            ov = Float.parseFloat(str);
                        else
                            ov = str;
                    }
                    return String.format(format, ov);
                case STOK:
                    if (args.size() < 1)
                        break;
                    Object v = args.elementAt(0);
                    if (v == null)
                        return "";
                    return v;
                case COMPARE:// 比较大小
                    if (args.size() < 2)
                        break;
                    String com1 = args.elementAt(0).toString();
                    if (com1.startsWith(AppUiElementType.Reference))
                        com1 = page.getEngine().getValue(
                            com1.replace(AppUiElementType.Reference, ""));
                    String com2 = args.elementAt(1).toString();
                    if (com2.startsWith(AppUiElementType.Reference))
                        com2 = page.getEngine().getValue(
                            com2.replace(AppUiElementType.Reference, ""));
                    int ret = 0;
                    if (com1.matches("^[\\d,.]*$") && com2.matches("^[\\d,.]*$"))
                        ret = Float.compare(Float.parseFloat(com1), Float.parseFloat(com2));
                    else if (com1.length() == 1 && com2.length() == 1)
                        ret = com1.charAt(0) - com2.charAt(0);
                    if (ret < 0)
                        return "-1";
                    else if (ret > 0)
                        return "1";
                    else
                        return "0";
                case VALUE_GET:
                    if (args.size() < 2)
                        break;
                    Object od = args.elementAt(0);
                    if (od instanceof ArrayList<?>) {
                        int ii = Integer.parseInt((String) args.elementAt(1));
                        return ((ArrayList<?>) od).get(ii);
                    } else {
                        String kk = (String) args.elementAt(1);
                        return ((HashMap<?, ?>) od).get(kk);
                    }
                case CACHE_GET:// 获取请求缓存
                    if (args.size() < 1)
                        break;
                    String keycg = (String) args.elementAt(0);
                    Object retO = page.getEngine().getRequestCache(keycg);
                    if (retO == null)
                        return "";
                    else
                        return retO;
                case CACHE_SET:// 保存请求缓存
                    if (args.size() < 2)
                        break;
                    String keycs = (String) args.elementAt(0);
                    Object values = args.elementAt(1);
                    page.getEngine().setRequestCache(keycs, values);
                    break;
                case CACHE_ADD:
                    if (args.size() < 2)
                        break;
                    String keyca = (String) args.elementAt(0);
                    Object valuea = args.elementAt(1);
                    if (valuea instanceof String) {
                        String str = valuea.toString();
                        if (str.startsWith(AppUiElementType.Reference))
                            str = page.getEngine().getValue(
                                str.replace(AppUiElementType.Reference, ""));
                        valuea = str;
                    }
                    Object object = page.getEngine().getRequestCache(keyca);

                    ArrayList<Object> dataa = null;
                    if (object == null) {
                        dataa = new ArrayList<Object>();
                    } else
                        dataa = (ArrayList<Object>) object;
                    dataa.add(valuea);

                    page.getEngine().setRequestCache(keyca, dataa);
                    break;
                case CACHE_ADD_ALL:
                    if (args.size() < 2)
                        break;
                    String keycal = (String) args.elementAt(0);
                    Object valueal = args.elementAt(1);
                    Object objectl = page.getEngine().getRequestCache(keycal);

                    ArrayList<Object> dataal = null;
                    if (objectl == null) {
                        dataal = new ArrayList<Object>();
                        page.getEngine().setRequestCache(keycal, dataal);
                    } else
                        dataal = (ArrayList<Object>) objectl;
                    dataal.addAll((ArrayList<?>) valueal);
                    break;
                case CACHE_CLEAR:
                    if (args.size() < 1)
                        break;
                    String keycr = (String) args.elementAt(0);
                    page.getEngine().removeRequestCache(keycr);
                    break;
                case CACHE_REMOVE:
                    if (args.size() < 2)
                        break;
                    String keyr = (String) args.elementAt(0);
                    int valuer = Integer.parseInt((String) args.elementAt(1));
                    ArrayList<Object> datar = (ArrayList<Object>) page.getEngine().getRequestCache(
                        keyr);
                    datar.remove(valuer);
                    break;
                case GLOBAL_SET:// 保存全局数据
                    if (args.size() < 2)
                        break;
                    String keygs = (String) args.elementAt(0);
                    Object valuegs = args.elementAt(1);
                    app.putGlobalData(keygs, valuegs);
                    break;
                case GLOBAL_GET:// 获取全局数据
                    if (args.size() < 1)
                        break;
                    String keygg = (String) args.elementAt(0);
                    return app.getGlobalData(keygg);
                case GLOBAL_REMOVE:// 删除全局数据
                    if (args.size() < 1)
                        break;
                    String keygr = (String) args.elementAt(0);
                    app.removeGlobalData(keygr);
                    break;
                case GLOBAL_CLEAR:
                    app.clearGloalData();
                    break;
                case CLIENT_INFO:
                    if (args.size() < 1)
                        break;
                    String keyci = (String) args.elementAt(0);
                    return app.getInfoData(keyci);
                case DISK_SET:// 持久化数据，单值对的
                    if (args.size() < 2)
                        break;
                    String keyds = (String) args.elementAt(0);
                    Object valds = args.elementAt(1);
                    if (valds instanceof String) {
                        String str = valds.toString();
                        if (str.startsWith(AppUiElementType.Reference))
                            str = page.getEngine().getValue(
                                str.replace(AppUiElementType.Reference, ""));
                        valds = str;
                    }
                    page.getEngine().saveToDisk(keyds, valds);
                    break;
                case DISK_GET:
                    if (args.size() < 1)
                        break;
                    String keydg = (String) args.elementAt(0);
                    return page.getEngine().getFromDisk(keydg);
                case DISK_ADD:
                    if (args.size() < 2)
                        break;
                    String keyda = (String) args.elementAt(0);
                    Object valuda = args.elementAt(1);
                    if (valuda instanceof String) {
                        String str = valuda.toString();
                        if (str.startsWith(AppUiElementType.Reference))
                            str = page.getEngine().getValue(
                                str.replace(AppUiElementType.Reference, ""));
                        valuda = str;
                    }
                    page.getEngine().addToDisk(keyda, valuda);
                    break;
                case DISK_REMOVE:
                    if (args.size() < 1)
                        break;
                    String keydr = (String) args.elementAt(0);
                    page.getEngine().removeFromDisk(keydr);
                    break;
                case DISK_REMOVEALL:
                    page.getEngine().removeAllFromDisk();
                    break;

                case UP:
                case PUSH:// 创建新页面
                    if (args.size() < 1)
                        break;
                    String keyps = (String) args.elementAt(0);
                    page.getEngine().push(keyps);
                    break;
                case DOWN:
                case POP:// 返回页面
                    if (args.size() < 1)
                        break;
                    String keypp = (String) args.elementAt(0);
                    page.getEngine().pop(Integer.parseInt(keypp));
                    break;
                case SHOW_MSG:// 弹出对话框
                    if (args.size() < 4)
                        break;
                    String type = (String) args.elementAt(0);
                    String content = (String) args.elementAt(1);
                    String ok = (String) args.elementAt(2);
                    String cancel = (String) args.elementAt(3);
                    ArrayList<String> pms = new ArrayList<String>();
                    Object ob = null;
                    for (int i = 4; i < args.size(); ++i) {
                        ob = args.elementAt(i);
                        pms.add(ob.toString());
                    }
                    page.getEngine().showAlert(Integer.parseInt(type), content, ok, cancel, pms);
                    break;
                case SHARE_INFO:
                    if (args.size() < 2)
                        break;
                    String title = (String) args.elementAt(0);
                    if (title.startsWith(AppUiElementType.Reference))
                        title = page.getEngine().getValue(
                            title.replace(AppUiElementType.Reference, ""));
                    String info = (String) args.elementAt(1);
                    if (info.startsWith(AppUiElementType.Reference))
                        info = page.getEngine().getValue(
                            info.replace(AppUiElementType.Reference, ""));
                    ShareEntry.Share((Activity) page.getRawContext(), title, info);
                    break;
                case EXIT:
                    if (args != null && args.size() > 0) {
                        String param = (String) args.elementAt(0);
                        page.getEngine().setFrom(param);
                    }
                    page.getEngine().exit();
                    break;
                case REFRESH:
                    page.onRefresh();
                    break;
                case GOTO:
                    if (args.size() < 2)
                        break;
                    String view = (String) args.elementAt(0);
                    String funcId = (String) args.elementAt(1);
                    String goParams = null;
                    if (args.size() > 2) {
                        String str = (String) args.elementAt(2);
                        goParams = URLDecoder.decode(str);
                    }
                    if (funcId != null && funcId.length() > 0) {
                        if (funcId.trim().endsWith(".amr"))
                            funcId = funcId.trim().substring(0, funcId.length() - 4);
                        if (funcId.startsWith("2")) {
                            String mNativeAppName = "";
                            for (int i = 0; i < HallData.mNativeAppsId.length; i++) {
                                if (HallData.mNativeAppsId[i].equals(funcId)) {
                                    mNativeAppName = HallData.mNativeAppsName[i];
                                    break;
                                }
                            }
                            Activity currentActivity = (Activity) page.getEngine().getContext();
                            Intent intent = new Intent();
                            intent.setClassName(currentActivity.getApplicationContext(),
                                mNativeAppName);
                            page.getEngine().getContext().startActivity(intent);
                        } else {
                            if (view != null && view.length() > 0)
                                page.getEngine().execApp(funcId, view, goParams);
                            else
                                page.getEngine().execApp(funcId, null, goParams);
                        }

                    } else if (view != null && view.length() > 0) {
                        int loc = page.getEngine().getLocation(view);
                        if (loc != -1) {
                            page.getEngine().pop(loc);
                        } else {
                            page.getEngine().push(view);
                        }
                    }
                    break;

                case PASTEBOARD_SET:
                    if (args.size() < 1)
                        break;
                    ClipboardManager clip1 = (ClipboardManager) page.getEngine().getContext()
                        .getSystemService(Context.CLIPBOARD_SERVICE);
                    String qrcode_content = (String) args.elementAt(0);
                    clip1.setText(qrcode_content); // 复制
                    Toast toast1 = Toast.makeText(page.getEngine().getContext(), page.getEngine()
                        .getContext().getString(R.string.PaipaiCopyofQrcodeCopy), 5000);
                    toast1.show();
                    // page.getEngine().destroyEngine();
                    break;

                case PASTEBOARD_GET:
                    if (args.size() < 0)
                        break;
                    ClipboardManager clip2 = (ClipboardManager) page.getEngine().getContext()
                        .getSystemService(Context.CLIPBOARD_SERVICE);
                    if (clip2.getText() == null) {
                        return "";
                    }
                    return clip2.getText();
                case REQUEST_DATA:
                    if (args.size() < 2)
                        break;
                    String dataId = (String) args.elementAt(0);
                    String rule = (String) args.elementAt(1);
                    String action = (String) args.elementAt(2);
                    ArrayList<String> params = new ArrayList<String>();
                    Object o = null;
                    for (int i = 3; i < args.size(); ++i) {
                        o = args.elementAt(i);
                        if (o == null)
                            params.add("");
                        else if (o instanceof ArrayList<?>)
                            params.add(JsonConvert.Array2Json((ArrayList<Object>) o));
                        else
                            params.add(o.toString());
                    }
                    page.requestData(dataId, rule, action, params);
                    break;
                case RESPONSE_GET:
                    if (args.size() < 2)
                        break;
                    String key = (String) args.elementAt(0);
                    String data = (String) args.elementAt(1);
                    return page.response_get(key, data);
                case DO_AGENT_PAY: {
                    if (args.size() < 4)
                        break;
                    String tradeNO = (String) args.elementAt(0);
                    String partnerID = (String) args.elementAt(1);
                    String bizType = (String) args.elementAt(2);
                    String bizSubType = (String) args.elementAt(3);
                    page.getEngine().agent_pay(tradeNO, partnerID, bizType, bizSubType);
                }
                    break;
                case DO_PAY:
                    if (args.size() < 4)
                        break;
                    String tradeNO = (String) args.elementAt(0);
                    String partnerID = (String) args.elementAt(1);
                    String bizType = (String) args.elementAt(2);
                    String bizSubType = (String) args.elementAt(3);
                    Object ro = null;
                    String ruleId = null;
                    for (int i = args.size() - 1; i >= 4; --i) {
                        ro = args.elementAt(i);
                        if (ro != null && !ro.toString().equalsIgnoreCase("browser")) {
                            ruleId = ro.toString();
                            break;
                        }
                    }
                    page.getEngine().pay(tradeNO, partnerID, bizType, bizSubType, ruleId);
                    break;
                case SAFE_TOKEN_INIT:
                    String rul = null;
                    if (args != null && args.size() > 0) {
                        rul = (String) args.elementAt(0);
                    }
                    page.getEngine().safeTokenInit(rul);
                    break;
                case DO_LOGIN:
                    String rId = null;
                    if (args != null && args.size() > 0)
                        rId = (String) args.elementAt(0);
                    activity.logoutUser();
                    page.getEngine().login(rId);
                    break;
                case DEVICE_CAPTURE_BARCODE:
                    if (args.size() < 2)
                        break;
                    String previewHint = (String) args.elementAt(0);
                    String nextExp = (String) args.elementAt(1);
                    String curCodeType = (String) args.elementAt(2);
                    page.getEngine().setRequestCache(Defines.scanRule, nextExp);
                    page.getEngine().doCaptureBarcode(previewHint, nextExp, curCodeType);
                    break;

                case OPENURL:
                    if (args.size() < 1)
                        break;
                    String url = (String) args.elementAt(0);
                    if (url.startsWith(AppUiElementType.Reference))
                        url = page.getEngine()
                            .getValue(url.replace(AppUiElementType.Reference, ""));
                    System.out.println("OPEN_URL_BROWSER qrcodeContent=" + url);
                    page.getEngine().openUrlBrowser(url);
                    break;
                case DEVICE_UUID:// 获取设备唯一号
                    return activity.getConfigData().getClientId();
                case COMPARE_STRING:
                    if (args.size() < 2)
                        break;
                    Object var1 = args.elementAt(0);
                    if (var1 instanceof String
                        && var1.toString().startsWith(AppUiElementType.Reference))
                        var1 = page.getEngine().getValue(
                            var1.toString().replace(AppUiElementType.Reference, ""));
                    Object var2 = args.elementAt(1);
                    if (var2 instanceof String
                        && var2.toString().startsWith(AppUiElementType.Reference))
                        var2 = page.getEngine().getValue(
                            var2.toString().replace(AppUiElementType.Reference, ""));
                    return var1.equals(var2);
                case DEVICE_TIME:
                    if (args.size() < 1)
                        break;
                    String fm = (String) args.elementAt(0);
                    long cur = System.currentTimeMillis();
                    if (fm.equalsIgnoreCase("format_millisecond"))
                        return String.valueOf(cur);
                    else if (fm.equalsIgnoreCase("format_second"))
                        return String.valueOf(cur / 1000);
                case DO_BIND_MOBILE:
                    String r = null;
                    if (args != null && args.size() > 0)
                        r = (String) args.elementAt(0);
                    page.getEngine().bindMobile(r);
                    break;
                case USER_INFO_DIRTY:
                    if (args.size() < 1)
                        break;
                    String param = null;
                    boolean bill = false;
                    boolean coupon = false;
                    for(int i=0;i<args.size();i++){
                        param = (String) args.elementAt(i);
                        if(param.equalsIgnoreCase("bill"))
                            bill = true;
                        else if(param.equalsIgnoreCase("coupon"))
                            coupon = true;
                    }
                    page.getEngine().setUserDataDirty(false,bill,coupon);
                    break;
                case PAIPAI_ROUTEPARSER:
                	if (args.size() < 3)
                		break;
                	String forwardOtherOperation = args.elementAt(0).toString();
                	String memo = (String) args.elementAt(1);
                	String routeInfoList = (String) args.elementAt(2);
                	
                	PaipaiRouteParser prp = new PaipaiRouteParser(activity,page,forwardOtherOperation,memo,Uri.decode(routeInfoList));
                	prp.routing();
                	break;
                default:
                    if (mExternalLibs == null) {
                        throw new Exception("invalid method");
                    }

                    Object lib = mExternalLibs.get(function.getName());
                    if (lib != null)
                        return ((Lib) lib).invoke(function, page);
                    else
                        throw new Exception("invalid method");
            }
        } catch (Exception e) {
            StringBuffer buffer = new StringBuffer();
            buffer.append("Expression id=" + function.getExpressionId() + ":");
            buffer.append("$" + function.getName() + "(");
            if (args != null) {
                for (Object o : args) {
                    if (o == null)
                        o = "null-pointer";
                    buffer.append(o.toString() + ",");
                }
                buffer.deleteCharAt(buffer.length() - 1);
            }
            buffer.append(")");
            LogUtil.logContainerDebuggable(Interpreter.TAG,
                buffer.toString() + " Encountered Exception:" + e.getMessage());
        }
        return null;
    }

    /**
     * 注册外部函数
     */
    @SuppressWarnings({ "rawtypes", "unchecked" })
    public void registerLib(String name, Lib lib) {
        if (mExternalLibs == null)
            mExternalLibs = new Hashtable();
        mExternalLibs.put(name, lib);
    }

}
