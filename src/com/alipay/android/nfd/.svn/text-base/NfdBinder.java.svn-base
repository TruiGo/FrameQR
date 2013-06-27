package com.alipay.android.nfd;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import org.json.JSONException;
import org.json.JSONObject;

import android.bluetooth.BluetoothAdapter;
import android.content.Context;
import android.os.RemoteException;

import com.alipay.android.nfd.IClientObserverPeer.Stub;
import com.alipay.android.security.Des;
import com.alipay.android.util.LogUtil;

public class NfdBinder extends Stub {
    private final static String TAG = "NfdBinder";
    /** 参与者对象 */
    private ClientObserverPeer mLBSClientClientObserver;
    private ClientObserverPeer mBluetoothClientObserver;
    /** 客户端回调函数 */
    private IClientCallback mClientCallback;
    /** 去重数据公共类 */
    private DiscoveredResultSet discoveredResultSet;
    /** 注册类型 */
    RegistType mRegistType;
    private List<ClientScanner> scanners;
    private BlueToothScannerImpl blueToothScannerPeerImpl;
    private LBSScannerImpl lbsScannerPeerImpl;
    private String mRegistMsg = new String();
    private Context mContext;
    private boolean isDiscovering = false;
    private int mRegistStyle = 15;
    private boolean mIsRegistSuccess = false;
    private Timer preTimer;
    private DispatchDiscoveryTaskThread mLBSScannerThread;
    
    public NfdBinder(Context context) {
        super();
        mContext = context;
        prepareScanners();
        prepareObservers();
        mIsRegistSuccess = false;
        isDiscovering = false;
        mRegistStyle = Integer.parseInt(NFDUtils.getNfdType(mContext));
    }

    private void prepareObservers() {
        mLBSClientClientObserver = new LBSObserverPeerImpl(mContext);
        mBluetoothClientObserver = new BlueToothObserverPeerImpl(mContext,
            BluetoothAdapter.getDefaultAdapter());
    }

    public void encryptRegist(String reginfo, String param1, String bizType) throws RemoteException {
        if (mIsRegistSuccess) {
            try {
                mClientCallback.registReslut(2);
            } catch (RemoteException e) {
                e.printStackTrace();
            }
            return;
        }
        JSONObject requestJson = new JSONObject();
        try {
            requestJson.put("userId", getMD5Str(param1));
            requestJson.put("userInfo", Des.encrypt(reginfo, NFDUtils.ALIPAY_INFO));
            LogUtil.logOnlyDebuggable("xxxx", "LBS regist ==>" + param1 + " : " + reginfo);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        String requestStr = requestJson.toString();
        LbsThread lbsRegist = new LbsThread(mLBSClientClientObserver, requestStr, mRegistStyle,bizType, mClientCallback);
        lbsRegist.start();
        
		try {
			synchronized (requestStr) {
				if (!lbsRegist.getRegistFinished()) {
					LogUtil.logOnlyDebuggable("xxxx", "mIsRegistSuccess ===> requestStr.wait()");
					requestStr.wait();
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		mRegistType = null;
		// 支付宝账号标志
		mRegistMsg = "$" + Des.encrypt(reginfo + "#" + bizType, NFDUtils.ALIPAY_INFO) + "#" + "^";
		// 没有使用wifi
		if (!NFDUtils.getInstance(mContext).hasNetWork() || (lbsRegist.getRegistFinished() && !lbsRegist.getRegistResult())) {
			((BlueToothObserverPeerImpl) mBluetoothClientObserver).registCallback(mClientCallback);
			if (mBluetoothClientObserver.regist(mRegistMsg, mRegistStyle, bizType)) {
				mRegistType = RegistType.BlueTooth;
				try {
					mClientCallback.registReslut(1);
				} catch (RemoteException e) {
					e.printStackTrace();
				}
				mIsRegistSuccess = true;
				return;
			}
		}
		
		mIsRegistSuccess = mIsRegistSuccess || lbsRegist.getRegistResult();
		if(!mIsRegistSuccess)
			mClientCallback.registReslut(0);
    }

    private boolean isDiscoverTimeout;
    /** 
     * 遍历本地发现和lbs发现 获取结果list
     * @see com.alipay.nfd.IClientObserver#retrieveClientInfo()
     */
    public void discoverClientInfo(String bizType) throws RemoteException {
        if (isDiscovering) {
            return;
        }

        if (preTimer != null) {
            preTimer.cancel();
            preTimer = null;
        }
        Timer timer = new Timer();
        TimerTask task = new TimerTask() {
            @Override
            public void run() {
                isDiscovering = false;
                for (ClientScanner cs : scanners) {
                    cs.setIsDiscoverFinished(true);
                }
                //通知并刷新界面
                discoveredResultSet.refreshClient("");
                isDiscoverTimeout = true;
                mLBSScannerThread.timeout = true;
                LogUtil.logOnlyDebuggable("xxxx", "==>15s timeOut");
                releaseRource();
            }
        };
        timer.schedule(task, 15000);
        preTimer = timer;
        discoveredResultSet.resetClientInfo();
        isDiscovering = true;
        isDiscoverTimeout = false;
        for (ClientScanner cs : scanners) {
            long delay = 0;
            if (cs instanceof BlueToothScannerImpl){
            	NFDUtils.getInstance(mContext).openBlueTooth();
            	delay = 7000;
            	if(hasRegisted()){
            		 delay = 4500;
            	}
            }
            
            LogUtil.logOnlyDebuggable("xxxx", delay +"");
            DispatchDiscoveryTaskThread temp = new DispatchDiscoveryTaskThread(cs, discoveredResultSet, bizType, delay);
            temp.start();
            if (cs instanceof LBSScannerImpl){
            	mLBSScannerThread = temp;
            }
        }
    }

    @Override
    public boolean hasRegisted() throws RemoteException {
        return mIsRegistSuccess;
    }

    @Override
    public boolean unRegist() throws RemoteException {
        mIsRegistSuccess = false;
        if (mRegistType == null)
            return true;
        switch (mRegistType) {
            case BlueTooth:
                mBluetoothClientObserver.unRegist();
                break;
        }
        return true;
    }

    @Override
    public void registCallback(com.alipay.android.nfd.IClientCallback callback)
                                                                               throws RemoteException {
        mClientCallback = (IClientCallback) callback;
        discoveredResultSet = new DiscoveredResultSet(mClientCallback);
    }

    public boolean isDiscoverFinished() {
        for (ClientScanner scanner : scanners) {
            if (!scanner.isDiscoverFinished()) {
                return false;
            }
        }
        if (preTimer != null) {
            preTimer.cancel();
            preTimer = null;
        }
        return true;
    }

    @Override
    public boolean isDiscovering() throws RemoteException {
        isDiscovering = !isDiscoverFinished();
        return isDiscovering;
    }

    @Override
    public boolean modifyBluetoothName() throws RemoteException {
        return ((BlueToothObserverPeerImpl) mBluetoothClientObserver).modifyBluetoothName();
    }

    public void prepareScanners() {
        scanners = new ArrayList<ClientScanner>();
        blueToothScannerPeerImpl = new BlueToothScannerImpl(mContext);
        scanners.add(blueToothScannerPeerImpl);
        lbsScannerPeerImpl = new LBSScannerImpl(mContext);
        scanners.add(lbsScannerPeerImpl);
    }

    @Override
    public void businessExit() throws RemoteException {
        isDiscovering = false;
        for (ClientScanner scanner : scanners) {
            scanner.setIsDiscoverFinished(true);
            scanner.relaseResources();
        }
        NFDUtils.getInstance(mContext).relaseInstance();
    }

    public void releaseRource() {
        isDiscovering = false;
        for (ClientScanner scanner : scanners) {
            scanner.setIsDiscoverFinished(true);
            scanner.relaseResources();
        }
    }

    private String getMD5Str(String str) {
        MessageDigest messageDigest = null;
        try {
            messageDigest = MessageDigest.getInstance("MD5");
            messageDigest.reset();
            messageDigest.update(str.getBytes("UTF-8"));
        } catch (NoSuchAlgorithmException e) {
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        byte[] byteArray = messageDigest.digest();

        StringBuffer md5StrBuff = new StringBuffer();

        for (int i = 0; i < byteArray.length; i++) {
            if (Integer.toHexString(0xFF & byteArray[i]).length() == 1)
                md5StrBuff.append("0").append(Integer.toHexString(0xFF & byteArray[i]));
            else
                md5StrBuff.append(Integer.toHexString(0xFF & byteArray[i]));
        }
        return md5StrBuff.toString();
    }

	@Override
	public boolean isDiscoverTimeout() throws RemoteException {
		return isDiscoverTimeout;
	}
}
