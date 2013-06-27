package com.alipay.android.core;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;

import com.alipay.android.appHall.security.CertHandler;

/**
 * @author sanping.li
 *
 */
/**
 * @author sanping.li
 *
 */
public interface Engine {
    public static final int RESULT_CANCEL = 0;
    public static final int RESULT_OK = -1;
    public static final int RESULT_FAIL = 1;
    
    /**
     * 初始化，初始化消息处理
     */
    public void init(Context context);
    /**
     * @return 运行的包Id
     */
    public String getPkgId();
    public void setPkgId(String pkgId);
    
    public String getPkgVersion();
    public void setPkgVersion(String pkgId);
    
    /**
     * @return 包的路径
     */
    public String getPath();
    public void setPath(String path);
    
    /**
     * @param sourceId 源包Id
     * @param action 操作类型
     * @param params 参数
     */
    public void execute(String sourceId,int action,String params);
    public void handleMsg(String sourceId,int action,String params);
    
    /**
     * @param targetId 目标包Id
     * @param result 结果
     * @param params 参数
     */
    public void callback(String targetId,int result,Object params);
    
    /**
     * @return 所依附的上下文，如果App则Activity
     */
    public Context getContext();
    public void attachContext(Context context);

    /**
     * @return 父Id
     */
    public String getParentId();
    public void setParentId(String pkgId);
    
    /**
     * Manifest
     */
    public void setManifest(ManifestDoc doc);
    public Object getManifest(String key);
    
    /**
     * 安全验证
     */
    public CertHandler getCertHandler();
    /**
     * @return 是否应用（有人机交互界面）
     */
    public boolean isApp();
    public void setApp(boolean isApp);
    
    /**
     * 退出
     */
    public void exit();
    
    /**
     * Android生命周期的回调，依附于上下文
     */
    public void create(String params,Bundle bundle);
    public void start(Context context);
    public void reStart(Context context);
    public void resume(Context context);
    public void pause(Context context);
    public void stop(Context context);
    public void destroy(Context context);
    public void newIntent(Context context,Intent intent);
    
    /**
     * Android状态存取回调
     */
    public void saveState(Context context,Bundle bundle);
    
    /**
     * Android按键回调
     */
    public boolean keyDown(Context context,int keyCode, KeyEvent event);
    
    /**
     * 调回前台
     */
    public void bringToFront();
}
