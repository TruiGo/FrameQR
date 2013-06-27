/**
 * 
 */
package com.alipay.android.core;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;

import com.alipay.android.client.RootActivity;
import com.alipay.platform.core.AlipayApp;

/**
 * @author sanping.li
 *
 */
public class ActivityShell extends RootActivity {
    private String mPkgId;
    private Engine mEngine;
    private Object mObject;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mPkgId = getIntent().getStringExtra("id");
        String params = getIntent().getStringExtra("data");
        mEngine = getMBus().getEngine(mPkgId);
        if (mEngine == null) {
            popError();
        } else {
            mEngine.attachContext(this);
            mEngine.create(params, savedInstanceState);
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (mEngine == null) {
            popError();
        } else
            mEngine.start(this);
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        if (mEngine == null) {
            popError();
        } else
            mEngine.reStart(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (mEngine == null) {
            popError();
        } else
            mEngine.resume(this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (mEngine == null) {
            popError();
        } else
            mEngine.pause(this);
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (mEngine == null) {
            popError();
        } else
            mEngine.stop(this);
    }

    @Override
    protected void onDestroy() {
        if (mEngine == null) {
            popError();
        } else {
            mEngine.destroy(this);
        }
        super.onDestroy();
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        if (mEngine == null) {
            popError();
        } else {
            mEngine.newIntent(this,intent);
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (mEngine == null) {
            popError();
        } else if (mEngine.keyDown(this,keyCode, event))
            return true;
        return super.onKeyDown(keyCode, event);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        if (mEngine == null) {
            popError();
        } else
            mEngine.saveState(this,outState);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (mEngine != null){
        	
        	AlipayApp alipayApp = (AlipayApp) this.getApplicationContext();
        	Object paipaiObject = alipayApp.getGlobalData("bootFromPaipai");
            if(paipaiObject != null && (Boolean)paipaiObject)
            {
            	alipayApp.putGlobalData("bootFromPaipai", false);
            	setUserData(null);
            	mEngine.exit();
                return;
            }
        }

        mEngine.callback(requestCode + "", resultCode, data);
    }

    private void popError() {
//        Toast.makeText(this, "Can't find engine for pkg:" + mPkgId, 2000).show();
        Log.e("ActivityShell", "Can't find engine for pkg:" + mPkgId);
        finish();
    }

    public String getPkgId() {
        return mPkgId;
    }

    public Engine getEngine() {
        return mEngine;
    }

    public Object getObject() {
        return mObject;
    }

    public void setObject(Object object) {
        mObject = object;
    }

    
}
