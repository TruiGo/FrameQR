package com.alipay.android.nfd;

import java.io.IOException;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.concurrent.LinkedBlockingQueue;

import android.content.Context;
import android.net.DhcpInfo;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.text.format.Formatter;

import com.alipay.android.util.LogUtil;

/**
 * 同一局域网扫描发现类
 * 
 * @author daping.gp
 * @version $Id: LANWifiScannerPeerImpl.java, v 0.1 2012-6-20 上午11:53:02
 *          daping.gp Exp $
 */
public class WlanScannerImpl implements ClientScanner {
	private final static String TAG = "LANWifiScannerPeerImpl";
	LanScanner mlanScanner;
	private boolean disCoverFinished = true;
	DhcpInfo dhcp;
	Context mContext;
	UDPTransMsg mUdpData;
	String mBizType;

	public WlanScannerImpl(Context context) {
		mContext = context;
	}

	@Override
	public boolean discoverClient(DiscoveredResultSet discoveredResultSet,
			String bizType) {
		setIsDiscoverFinished(false);
		mBizType = bizType;
		boolean isRet = true;
		try {
			Set<String> lanScannerInfo = new LinkedHashSet<String>(getRemotePeerList());
			if (lanScannerInfo != null) {
				discoveredResultSet.refreshClient(lanScannerInfo);
			}
			setIsDiscoverFinished(true);
			LogUtil.logOnlyDebuggable("xxxx",
					"====> lanwifi discover finish ---> result : "
							+ lanScannerInfo);
		} catch (Exception e) {
			e.printStackTrace();
			setIsDiscoverFinished(true);
			isRet = false;
		}
		return isRet;
	}

	/**
	 * 获取远程参与者list
	 * 
	 * @return
	 */
	public LinkedBlockingQueue<String> getRemotePeerList() {
	    LinkedBlockingQueue<String> mLanInfo = new LinkedBlockingQueue<String>();
	    try {
		    relaseResources();
			mUdpData = new UDPTransMsg(mBizType);
			mUdpData.start();
			WifiManager wifiManager = (WifiManager) mContext
					.getSystemService(Context.WIFI_SERVICE);
			if (wifiManager==null||!NFDUtils.getInstance(mContext).isUseWifi()) {
			    setIsDiscoverFinished(true);
			    return mLanInfo;
			}
			WifiInfo wifiInfo = wifiManager.getConnectionInfo();
			DhcpInfo dhcp =wifiManager.getDhcpInfo();
			mlanScanner = new LanScanner(Formatter.formatIpAddress((wifiInfo
					.getIpAddress() & dhcp.netmask)),
					Formatter.formatIpAddress(dhcp.netmask),
					wifiInfo.getIpAddress());
			mlanScanner.doScanTask();
			Thread.sleep(2000);
			mLanInfo = mUdpData.getResult();
			setIsDiscoverFinished(true);
			return mLanInfo;
		} catch (Exception e) {
		    setIsDiscoverFinished(true);
			return mLanInfo;
		} finally {
		    relaseResources();
		}
	}

	@Override
	public boolean isDiscoverFinished() {
		return disCoverFinished;
	}

	@Override
	public synchronized void setIsDiscoverFinished(boolean finished) {
		disCoverFinished = finished;
	}

	@Override
	public void relaseResources() {
		if (mUdpData != null) {
			try {
				mUdpData.setThreadExit();
				mUdpData = null;
			} catch (IOException e) {
			    e.printStackTrace();
			}
		}
		if (mlanScanner != null) {
			try {
				mlanScanner.setThreadExit();
				mlanScanner = null;
			} catch (IOException e) {
			    e.printStackTrace();
			}
		}
	}
}
