/**
 * 
 */
package com.alipay.android.core;

import android.os.Handler;
import android.os.Looper;
import android.os.Message;

/**
 * @author sanping.li
 *
 */
public class MsgHandler extends Handler {
    private MBus mBus;

    public MsgHandler(Looper looper, MBus bus) {
        super(looper);
        mBus = bus;
    }

    @Override
    public void handleMessage(Message msg) {
        int action = msg.what;
        String params = (String) msg.obj;
        ParamString paramString = new ParamString(params);
        String source = paramString.getValue("_source_");
        String target = paramString.getValue("_target_");
        Engine sourceE = mBus.getEngine(source);
        switch (action) {
            case MsgAction.ACT_BOOT:

                break;
            case MsgAction.ACT_FINISH:

                break;
            case MsgAction.ACT_LAUNCH:
                Engine engine = mBus.getEngine(target);
                if (engine != null) {
                    if (engine.isApp()) {
                        mBus.popApp(target);
                        if (sourceE != null)
                            sourceE.callback(target, Engine.RESULT_OK, params);
                    }
                } else {
                    mBus.incubate(sourceE, target, params);
                }
                break;
            case MsgAction.ACT_EXIT:
                if (sourceE != null && sourceE.isApp()) {
                    mBus.removeApp(source);
                } else {
                    mBus.unRegisterService(source);
                }
                break;
            default:
                break;
        }
        super.handleMessage(msg);
    }

}
