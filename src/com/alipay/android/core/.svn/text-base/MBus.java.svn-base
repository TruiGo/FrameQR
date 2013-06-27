/**
 * 
 */
package com.alipay.android.core;

import java.io.File;
import java.util.HashMap;
import java.util.Map;
import java.util.Stack;

import android.content.Context;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Message;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.alipay.android.appHall.appManager.AppItemInfo;
import com.alipay.android.appHall.appManager.HallData;
import com.alipay.android.appHall.common.Defines;
import com.alipay.android.client.AlipayApplication;
import com.alipay.android.client.constant.Constant;
import com.alipay.android.log.AlipayLogAgent;
import com.alipay.android.log.Constants;
import com.eg.android.AlipayGphone.R;

/**
 * @author sanping.li
 *
 */
public class MBus {
    public static final String TAG = "MBus>>";

    private Context mContext;
    private String mPath;
    private Map<String, Engine> mPkgs;
    private Stack<Engine> mAppStack;
    private Handler mHandler;

    public MBus(Context context) {
        mContext = context;
        mPkgs = new HashMap<String, Engine>();
        mAppStack = new Stack<Engine>();
        mPath = mContext.getFilesDir().getAbsolutePath() + Defines.Apps;

        HandlerThread thread = new HandlerThread("Mbus");
        thread.start();
        mHandler = new MsgHandler(thread.getLooper(), this);
    }

    /**
     * 注册运行时
     * @param engine
     */
    public void registerService(Engine engine) {
        mPkgs.put(engine.getPkgId(), engine);
    }

    public void pushApp(Engine engine) {
        registerService(engine);
        if (engine.isApp()) {
            mAppStack.push(engine);
        }
    }

    public void popApp(String PkgId) {
        if (mAppStack.isEmpty())
            return;
        Engine engine = mAppStack.peek();
        if (engine.getPkgId().equalsIgnoreCase(PkgId)) {//在最顶层
            engine.bringToFront();
            return;
        }
        while (!engine.getPkgId().equalsIgnoreCase(PkgId)) {
            mAppStack.pop();
            unRegisterService(engine.getPkgId());
            engine.destroy(mContext);
            engine = mAppStack.peek();
        }
    }

    public void removeApp(String PkgId) {
        if (mAppStack.isEmpty())
            return;
        for (Engine engine : mAppStack) {
            if (engine.getPkgId().equalsIgnoreCase(PkgId))
                mAppStack.removeElement(engine);
        }
        unRegisterService(PkgId);
    }

    public void unRegisterService(String PkgId) {
        mPkgs.remove(PkgId);
    }

    /**
     * 孵化应用
     * @param AppId
     */
    public void incubate(Engine source, String targetId, String params) {
        NativeLoader nativeLoader = new NativeLoader(mContext, source, targetId);
        if (nativeLoader.load(params)) {
            return;
        }

        AppItemInfo info = ((AlipayApplication) mContext).getHallData().getAppConfig(targetId);
        if (info == null) {
            return;
        }

        File file = new File(mPath + targetId + "/");
        if (info.getState() == AppItemInfo.STATE_PRE_INSTALL
            || info.getState() == AppItemInfo.STATE_INSTALL_PAUSE
            || info.getState() == AppItemInfo.STATE_PRE_UPGRADE
            || info.getState() == AppItemInfo.STATE_UPGRADE_PAUSE
            || info.getState() == AppItemInfo.STATE_DELETEED
            || info.getState() == AppItemInfo.STATE_PRE_DELETE || !file.exists()) {
            String msg = mContext.getString(R.string.pls_install_bundle).replace("%s",
                info.getNameText());
            if (info.getState() == AppItemInfo.STATE_DELETEED
                || info.getState() == AppItemInfo.STATE_PRE_DELETE) {
                msg = mContext.getString(R.string.deleted_bundle).replace("%s", info.getNameText());
            }
            View toastView = LayoutInflater.from(mContext)
                .inflate(R.layout.alipay_toast_view, null);
            TextView toastTextView = (TextView) toastView.findViewById(R.id.toastText);
            toastTextView.setText(msg);
            Toast toast = new Toast(mContext);
            toast.setView(toastView);
            toast.setGravity(Gravity.CENTER, 0, 0);
            toast.setDuration(Toast.LENGTH_SHORT);
            toast.show();
            return;
        }

        Loader loader = new Loader(mContext, source, targetId, mPath + targetId + "/");
        Engine engine = loader.load(params);

        if (engine != null) {
            if (engine.isApp()) {
                pushApp(engine);
            } else {
                registerService(engine);
            }
            if (source != null)
                source.callback(targetId, Engine.RESULT_OK, params);
        } else {
            if (source != null)
                source.callback(targetId, Engine.RESULT_FAIL, params);
        }

        //埋点
        if (engine != null) {
            boolean buried = false;
            if (Constant.FROMAPPCENTER) {
                for (int i = 0; i < HallData.sordedIdArray.length; i++) {
                    if (HallData.sordedIdArray[i].equals(targetId)) {
                        AlipayLogAgent.writeLog(mContext, Constants.BehaviourID.BIZLAUNCHED, null,
                            null, targetId, engine.getPkgVersion(), targetId+"Home", Constants.WALLETHOME,
                            "homeApp" + (i + 1) + "Icon");
                        buried = true;
                        break;
                    }
                }
                if (!buried) {
                    AlipayLogAgent.writeLog(mContext, Constants.BehaviourID.BIZLAUNCHED, null,
                        null, targetId, engine.getPkgVersion(), targetId+"Home", Constants.APPCENTER,
                        targetId + "Icon");
                }
            }
            Constant.FROMAPPCENTER = true;
        }
    }

    public Engine getEngine(String PkgId) {
        return mPkgs.get(PkgId);
    }

    /**
     * 发送消息
     */
    public void sendMsg(String sourceId, String targetId, int action, String params) {
        Message message = mHandler.obtainMessage();
        message.what = action;
        StringBuffer buffer = new StringBuffer();
        if (sourceId != null)
            buffer.append("_source_=" + sourceId + "&");
        if (targetId != null)
            buffer.append("_target_=" + targetId + "&");
        if (params != null) {
            buffer.append(params);
        } else {
            buffer.deleteCharAt(buffer.length() - 1);
        }
        message.obj = buffer.toString();
        mHandler.sendMessage(message);
    }

    public Context getContext() {
        return mContext;
    }

    /**
     * 销毁
     */
    public void destroy() {
        mPkgs.clear();
        mAppStack.clear();
    }
}
