package com.alipay.android.nfd;

import android.os.RemoteException;


public class LbsThread extends Thread {
    private ClientObserverPeer mLBSClientClientObserver;
    private String mRegMsg;
    private int mType;
    private boolean mRegFinished;
    private boolean mLbsRegistResult;
    private String mBizType;
    IClientCallback mCallback;
    
    public LbsThread(ClientObserverPeer lBSClientClientObserver, String registMsg, int type,
                     String bizType,IClientCallback callback) {
        mLBSClientClientObserver = lBSClientClientObserver;
        mRegMsg = registMsg;
        mType = type;
        mBizType = bizType;
        mCallback = callback;
        mLbsRegistResult = false;
        mRegFinished  = false;
    }

    public void run() {
        mLbsRegistResult = mLBSClientClientObserver.regist(mRegMsg, mType, mBizType);
        
//        mRegFinished  = true;
        synchronized (mRegMsg) {
        	setRegistFinished(true);
            mRegMsg.notify();
        }
        try {
        	if(mLbsRegistResult)//成功立即通知应用，失败时需要与本地结果
        		mCallback.registReslut(3);
//            mCallback.registReslut((mLbsRegistResult? 3:0));
        } catch (RemoteException e) {
           e.printStackTrace();
        }
    }
    
    public boolean getRegistFinished() {
		return mRegFinished;
    }
    
    public void setRegistFinished(boolean finished){
		mRegFinished = finished;
    }

    public boolean getRegistResult() {
        return mLbsRegistResult;
    }
}
