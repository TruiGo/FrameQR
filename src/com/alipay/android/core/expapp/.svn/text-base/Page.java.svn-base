package com.alipay.android.core.expapp;

import java.util.ArrayList;

import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.CompoundButton.OnCheckedChangeListener;

import com.alipay.android.appHall.uiengine.InputCheckListener;
import com.alipay.android.appHall.uiengine.NeedSaveListener;
import com.alipay.android.appHall.uiengine.PageHasNullInputChecker;
import com.alipay.android.appHall.uiengine.SendLogListener;
import com.alipay.android.appHall.uiengine.UIInterface;
import com.alipay.platform.view.OnDataChangeListener;

public interface Page {

    void addElement(View view);
    void addConfirmBarElement(View view,LayoutParams params);
    ViewGroup getFormPanel();
    ViewGroup getConfirmBar();
    ViewGroup getTitle();
    View getView();
    void setView(View view);
    ExpAppRuntime getEngine(); 
    void setPageTitle(String pageTitle);
    OnClickListener getOnClickListener();
    OnCheckedChangeListener getOnCheckedChangeListener();
    void setPageName(String pageName);
    void add2delayGetValueGroup(UIInterface view);
    void addInputCheckListener(InputCheckListener listener);
    void addNeedSaveListener(NeedSaveListener listener);
    void addSendLogListener(SendLogListener listener);
    PageHasNullInputChecker getInputBoxIsNullListener();
    android.content.Context getRawContext();
    public void setOnAppearString(String value);
    public void onAppear();
    public void setOnRefresh(String value);
    public void onRefresh();
    public void setOnCancel(String value);
    public void onCancel();
    public OnDataChangeListener getOnDataChangeListener();
    

    /**
     * 请求数据。
     * @param action 
     * @param params 
     */
    public void requestData(String interfaceId, String ruleId, String action, ArrayList<String> params);
    public Object response_get(String dataId, String key);

    /**
     * 执行表达式
     */
    public Object interpreter(String id,String expression);

    /**
     * 处理rule
     */
    public void processRule(String rId,String rule, String operand);
    
    /**
     * 查找组件
     */
    public Object findWidget(String viewId);
    public abstract String getPageName();
    public abstract String getRealPageName();
    void setScrollBarEnabled(boolean enable);
    public ViewGroup getContent();
    public boolean isCancelCheckInput();
    public void setCancelCheckInput(boolean cancelCheckInput);
    public void preAppear();
    public void setPreAppear(String value);
}
