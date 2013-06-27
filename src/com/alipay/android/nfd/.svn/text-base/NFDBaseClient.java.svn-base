package com.alipay.android.nfd;

import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.os.RemoteException;

public class NFDBaseClient {
	private NFDBaseCallback nfdBaseCallback;
	protected IClientObserverPeer clientObserverPeer; 
	private Object mLock = new Object();
	
	public void bindService(Context context){
		if (clientObserverPeer == null) {
			context.bindService(new Intent("com.alipay.android.nfd.NFDSERVICE"),connection, Context.BIND_AUTO_CREATE);
        }
	}
	
	public void unbindService(Context context){
		if(clientObserverPeer != null){
			context.unbindService(connection);
			try {
				unRegistSerivce();
			} catch (RemoteException e) {
				e.printStackTrace();
			}
		}
	}
	
	private void unRegistSerivce() throws RemoteException {
		if (clientObserverPeer != null) {
			clientObserverPeer.unRegist();
			clientObserverPeer.businessExit();
			clientObserverPeer = null;
		}
	}
	
	protected ServiceConnection connection = new ServiceConnection() {
		@Override
		public void onServiceDisconnected(ComponentName name) {
			try {
				unRegistSerivce();
			} catch (RemoteException e) {
				e.printStackTrace();
			}
		}

		
		@Override
		public void onServiceConnected(ComponentName name, IBinder service) {
			synchronized(mLock){
				clientObserverPeer = IClientObserverPeer.Stub.asInterface(service) ;
    			mLock.notifyAll();
    		}
		}
	};
	
	private IClientCallback clientCallback =  new IClientCallback.Stub() {
		@Override
		public void refresh(final List<String> clientInfos) throws RemoteException {
			nfdBaseCallback.binderRefresh(clientInfos, clientObserverPeer);
		}
		
        @Override
        public void startActivity(String packageName, String className, int iCallingPid,Bundle bundle) throws RemoteException {
        	nfdBaseCallback.binderStartActivity(packageName, className, iCallingPid, bundle,clientObserverPeer);
        }

		@Override
		public void registReslut(final int resultCode) throws RemoteException {
			nfdBaseCallback.binderRegistReslut(resultCode,clientObserverPeer);
		}
	};
	
	public IClientCallback getClientCallback(){
		return clientCallback;
	}
	
	public void registCallback(NFDBaseCallback nfdBaseCallback){
		this.nfdBaseCallback = nfdBaseCallback;
	}
	
	private Timer preTimer;
	public void registTimeOutTimer(long time) {
        Timer timer = new Timer();
        TimerTask task = new TimerTask() {
            @Override
            public void run() {
            	if (clientObserverPeer != null)
					try {
						clientObserverPeer.unRegist();
					} catch (RemoteException e) {
						e.printStackTrace();
					}
            }
        };

        if (preTimer != null)
            preTimer.cancel();
        timer.schedule(task, time);
        preTimer = timer;
    }
	
	public IClientObserverPeer getClientObserverPeer(){
		return clientObserverPeer;
	}
	
	public void nfdRegist(String userInfo,String userId,String bizType){
		if (clientObserverPeer == null) {
            try {
                synchronized (mLock) {
                    mLock.wait();
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        
        try {
            if (clientObserverPeer != null){
            	clientObserverPeer.registCallback(clientCallback);
                clientObserverPeer.encryptRegist(userInfo, userId, bizType);
            }
        } catch (RemoteException e) {
            e.printStackTrace();
        }
	}
	
	public void nfdDiscover(String bizType){
		try {
			if (clientObserverPeer == null)
				synchronized (mLock) {
					mLock.wait();
				}
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		
		try {
			if (clientObserverPeer != null && !clientObserverPeer.isDiscovering()) {
				clientObserverPeer.registCallback(clientCallback);
				clientObserverPeer.discoverClientInfo(bizType);
			}
		} catch (RemoteException e) {
			e.printStackTrace();
		}
	}
}