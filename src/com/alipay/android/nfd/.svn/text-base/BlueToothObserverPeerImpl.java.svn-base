package com.alipay.android.nfd;

import android.bluetooth.BluetoothAdapter;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;

import com.alipay.android.util.LogUtil;

/**
 * 蓝牙注册信息类
 * 
 * @author daping.gp
 * @version $Id: BlueToothObserverImpl.java, v 0.1 2012-6-21 上午9:53:18 daping.gp
 *          Exp $
 */
public class BlueToothObserverPeerImpl implements ClientObserverPeer {
	private static final String TAG = "BlueToothObserverPeerImpl";
	// 蓝牙设备
	BluetoothAdapter bluetoothAdapter;
	private Context mContext;
	private String originalDeviceName;
	private boolean rigistResult;
	private BluetoothReceiver bluetoothReceiver;
	private String newDeviceName;
	IClientCallback mCallback;
	private Bundle m_lock = new Bundle();
	private Object m_second = new Object();
	private boolean mBluetoothOpenStatus;

	public BlueToothObserverPeerImpl(Context context, BluetoothAdapter bta) {
		this.mContext = context;
		bluetoothAdapter = bta;

	}

	@Override
	public boolean regist(String clientInfo, int type, String bizType) {
		if ((type & RegistType.BlueTooth.getCode()) == 0) {
			return false;
		}

		rigistResult = false;
		if (bluetoothAdapter == null) {
			LogUtil.logOnlyDebuggable(TAG, "此手机不支持蓝牙 ");
			return false;
		}
		newDeviceName = clientInfo;

		/*
		 * if (!bluetoothAdapter.isEnabled()) { if (!bluetoothAdapter.enable())
		 * { return false; } NFDUtils.getInstance(mContext).putBoolean(
		 * NFDUtils.BlueToothOpreator, true); }
		 */
		mBluetoothOpenStatus = true;
		if (!bluetoothAdapter.isEnabled()) {
			mBluetoothOpenStatus = false;
		}

		synchronized (m_lock) {
			try {
				mCallback.startActivity("", "", 0, m_lock);
				LogUtil.logOnlyDebuggable("xxxx", "mCallback.startActivity");
				m_lock.wait();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

		return rigistResult;
	}

	public boolean modifyBluetoothName() {
		if (!mBluetoothOpenStatus) {
			NFDUtils.getInstance(mContext).putBoolean(NFDUtils.BlueToothOpreator, true);
		}
		if (bluetoothAdapter != null && bluetoothAdapter.isEnabled()) {
			originalDeviceName = bluetoothAdapter.getName();
			if (originalDeviceName != null && (!originalDeviceName.startsWith("$")) && !(originalDeviceName.endsWith("^"))) {
				NFDUtils.getInstance(mContext).putString(NFDUtils.BlueToothKey, originalDeviceName);
			}
			rigistResult = bluetoothAdapter.setName(newDeviceName);
		}
		synchronized (m_lock) {
			m_lock.notify();
		}
		return rigistResult;
	}

	public void registCallback(IClientCallback mClientCallback) {
		mCallback = mClientCallback;
	}

	private class BluetoothReceiver extends BroadcastReceiver {
		@Override
		public void onReceive(Context context, Intent intent) {
			String action = intent.getAction();
			if (BluetoothAdapter.ACTION_STATE_CHANGED.equals(action)) {
				if (!bluetoothAdapter.isEnabled()) {
					return;
				}
				bluetoothAdapter.setName(NFDUtils.getInstance(mContext).getString(NFDUtils.BlueToothKey, originalDeviceName));
				bluetoothAdapter.disable();
				synchronized (m_second) {
					NFDUtils.getInstance(mContext).putBoolean(NFDUtils.BlueToothOpreator, false);
				}
				unregisterBlueToothReceiver();
			}
			LogUtil.logOnlyDebuggable("xxxx", "renameDevice reuslt = " + rigistResult);
		}
	}

	@Override
	public boolean unRegist() {
		return closeBlueToothDevices();
	}

	/**
	 * 关闭蓝牙注册信息
	 * 
	 * @return
	 */
	private boolean closeBlueToothDevices() {
		try {
			synchronized (m_lock) {
				m_lock.notify();
			}
			if (bluetoothAdapter == null) {
				return false;
			}
			if (bluetoothAdapter.isEnabled()) {
				bluetoothAdapter.setName(NFDUtils.getInstance(mContext).getString(NFDUtils.BlueToothKey, originalDeviceName));

				if ((NFDUtils.getInstance(mContext).getBoolean(NFDUtils.BlueToothOpreator))) {
					bluetoothAdapter.disable();
					NFDUtils.getInstance(mContext).putBoolean(NFDUtils.BlueToothOpreator, false);
				}
			} else {
				synchronized (m_second) {
					bluetoothReceiver = new BluetoothReceiver();
					mContext.registerReceiver(bluetoothReceiver, new IntentFilter(BluetoothAdapter.ACTION_STATE_CHANGED));
					if (!bluetoothAdapter.enable()) {
						unregisterBlueToothReceiver();
					}
					NFDUtils.getInstance(mContext).putBoolean(NFDUtils.BlueToothOpreator, true);
				}
			}
		} catch (Exception e) {
			return false;
		}
		return true;
	}

	private void unregisterBlueToothReceiver() {
		try {
			if (bluetoothReceiver != null) {
				mContext.unregisterReceiver(bluetoothReceiver);
				bluetoothReceiver = null;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
