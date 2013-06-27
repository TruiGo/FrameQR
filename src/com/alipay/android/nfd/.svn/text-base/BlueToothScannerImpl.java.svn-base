package com.alipay.android.nfd;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;

import com.alipay.android.security.Des;
import com.alipay.android.util.LogUtil;

/**
 * 蓝牙发现类
 * @author daping.gp
 * @version $Id: BlueToothScannerPeerImpl.java, v 0.1 2012-6-20 上午10:27:54 daping.gp Exp $
 */
public class BlueToothScannerImpl implements ClientScanner {
    //蓝牙设备
    BluetoothAdapter               bluetoothAdapter;
    SearchDeviceReceiver           searchDeviceReceiver;
    private Context                mContext;
    private boolean                disCoverFinished = true;
    private DiscoveredResultSet discoveredResultSet;
    private String mBizType;
    public BlueToothScannerImpl(Context context) {
        this.mContext = context;
        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        disCoverFinished = true;
    }

    @Override 
    public boolean discoverClient(DiscoveredResultSet discoveredResultSet,String bizType) {
        this.discoveredResultSet = discoveredResultSet;
        mBizType = bizType;
        try {
        	startBlueToothDiscovery();
        } catch (Exception e) {
        	e.printStackTrace();
        	disCoverFinished = true;
        	return false;
        }
        
        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if(bluetoothAdapter == null){
            disCoverFinished = true;
            return false;
        }
        if(bluetoothAdapter.isEnabled()){
            bluetoothAdapter.startDiscovery();
        }else{
            if(!bluetoothAdapter.enable()){
            	 disCoverFinished = true;
            	 if (searchDeviceReceiver != null) {
                     mContext.unregisterReceiver(searchDeviceReceiver);
                     searchDeviceReceiver = null;
                 }
            	 return false;
            }
            NFDUtils.getInstance(mContext).putBoolean(NFDUtils.BlueToothOpreator, true);
        }        
        return true;
    }

    /**
     * 开启蓝牙进行扫描
     */
    private void startBlueToothDiscovery() throws Exception {
        IntentFilter intentFilter = new IntentFilter(BluetoothAdapter.ACTION_DISCOVERY_FINISHED);
        intentFilter.addAction(BluetoothDevice.ACTION_FOUND);
        intentFilter.addAction(BluetoothAdapter.ACTION_STATE_CHANGED);
        searchDeviceReceiver = new SearchDeviceReceiver();
        mContext.registerReceiver(searchDeviceReceiver, intentFilter);
    }

    class SearchDeviceReceiver extends BroadcastReceiver {
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (BluetoothDevice.ACTION_FOUND.equals(action)) {
                // 代表远程蓝牙适配器的对象取出
                BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                if(device==null || device.getName() == null){
                    return;
                }
                String deviceName = device.getName();
                LogUtil.logOnlyDebuggable("xxxx", "deviceName===> " + deviceName+"");
                // 添加蓝牙发现者信息
                if (deviceName.startsWith("$")&& deviceName.endsWith("^")) {
                    deviceName =  deviceName.substring(1, deviceName.length() - 1);
                    if(deviceName.endsWith("#")){
                        deviceName = deviceName.substring(0, deviceName.length() - 1);
                        deviceName = Des.decrypt(deviceName, NFDUtils.ALIPAY_INFO);
                    }
                    if(deviceName==null){
                    	return;
                    }
                    String nameArray[] = deviceName.split("#");
                    if(mBizType.equals("*")||(nameArray!=null&&nameArray.length>=1&&nameArray[nameArray.length-1].equals(mBizType)))
                    discoveredResultSet.refreshClient(deviceName.substring(0,(deviceName.length()-mBizType.length()-1)));
                }
            } else if (BluetoothAdapter.ACTION_DISCOVERY_FINISHED.equals(action)) {
                setIsDiscoverFinished(true);
                unregistBluetoothReciever();
                //通知并刷新界面
            	discoveredResultSet.refreshClient("");
            	LogUtil.logOnlyDebuggable("xxxx", "====> bluetooth discover finish -->");
            } else if(BluetoothAdapter.ACTION_STATE_CHANGED.equals(action)){
            	if(bluetoothAdapter.getState() == BluetoothAdapter.STATE_ON){
            		LogUtil.logOnlyDebuggable("xxxx", "state changed STATE_ON");
            		bluetoothAdapter.startDiscovery();
            	}
            }
        }
    }
    
    public synchronized void unregistBluetoothReciever(){
        if(bluetoothAdapter != null)
            bluetoothAdapter.cancelDiscovery();
        try{
        if (searchDeviceReceiver != null) {
            mContext.unregisterReceiver(searchDeviceReceiver);
            searchDeviceReceiver = null;
        }}catch(Exception e){
        	e.printStackTrace();
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
        unregistBluetoothReciever();
        setIsDiscoverFinished(true);
    }
}
