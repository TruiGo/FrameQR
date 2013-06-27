/**
 * Copyright (c) 2011 by Aliyun. 
 */
package com.alipay.android.nfd;

import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Enumeration;
import java.util.List;

import android.annotation.TargetApi;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.os.Environment;
import android.telephony.CellLocation;
import android.telephony.NeighboringCellInfo;
import android.telephony.PhoneStateListener;
import android.telephony.SignalStrength;
import android.telephony.TelephonyManager;
import android.telephony.cdma.CdmaCellLocation;
import android.telephony.gsm.GsmCellLocation;


class PhoneInfoManager 
{
	final static int InvalidRSSI = -1111; 
	public  int testNeighType = 0;
	static PhoneInfoManager mInstance ;
	static int servRSSI = InvalidRSSI;
	private TelephonyManager phoneService;
	private WifiManager wifiService;
	private CellLocation preMainCell = null;
	private String preMainWifi = null;
	List<NeighboringCellInfo> mPreListNeighCell = null; 
	List<ScanResult> mPreListNeighWifi = null;
	private Boolean mHasInited = false;
	private ConnectivityManager connectionMgr = null;
	private Context mContext=null;
	private int mCDMARssi = InvalidRSSI;
	private int testNum = 0;
	private List<NeighboringCellInfo> testListNeigh = null;
	private Boolean mPermitedCheckEnv = true;
	private int batteryLevel = 100;
	private Boolean mHasGpsStatusChanged = false;
	private MyPhoneStateListener  myPhoneStateListener = null;
	
	private PhoneInfoManager()
	{
		
	}
	
	/**
	 * @brief 获得PhoneInfoManager实例
	 * @return 获得实例成功，返回PhoneInfoManager对象，否则为null
	 */			
	static PhoneInfoManager getInstance()
	{
		if( mInstance != null )
			return mInstance;
		mInstance = new PhoneInfoManager();
		return mInstance;
	}
	
	/**
	 * @brief 初始化
	 * @return true初始化成功，否则false
	 */	
	Boolean init(Context context)
	{
		mContext = context;
		phoneService = (TelephonyManager)mContext.getSystemService(Service.TELEPHONY_SERVICE);
		wifiService = (WifiManager)mContext.getSystemService(Service.WIFI_SERVICE);
		connectionMgr = (ConnectivityManager) mContext.getSystemService(Context.CONNECTIVITY_SERVICE);

		myPhoneStateListener= new MyPhoneStateListener();
		//mContext.registerReceiver(mBatteryInfoReceiver, new IntentFilter(Intent.ACTION_BATTERY_CHANGED));
		phoneService.listen(myPhoneStateListener,PhoneStateListener.LISTEN_SIGNAL_STRENGTHS );
		mHasInited = true;
		return true;
	}
	
	/**
	 * @brief 释放占用资源
	 */	
	void kill()
	{
		try
		{
			if( phoneService != null && myPhoneStateListener != null )
			{
				phoneService.listen(myPhoneStateListener,PhoneStateListener.LISTEN_NONE);
				phoneService = null;
			}
	
//			if( mContext != null )
//				mContext.unregisterReceiver(mBatteryInfoReceiver);
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		
		mInstance = null;
		mContext = null;
		myPhoneStateListener = null;
		mHasInited = false;
	}
	
	/**
	 * @brief 检查是否有SDCARD
	 * @return true如果有SDCARD
	 */
	boolean hasSDcard()
	{
		return Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED);
	};
	
	/**
	 * @brief 检查GPS是否已开启
	 */
	boolean hasGpsOpened()
	{
		LocationManager locationManager = (LocationManager)mContext.getSystemService(Context.LOCATION_SERVICE );
		return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER );
	};
	
	/**
	 * @brief 检查GPRS是否已开启
	 */
	boolean hasGprsOpened()
	{
		//if( !isNetPermitted() )
		//	return false;
		return connectionMgr.getNetworkInfo(ConnectivityManager.TYPE_MOBILE).getState() == NetworkInfo.State.CONNECTED; 
	}
	
	/**
	 * @brief 检查wifi是否已开启
	 */
	boolean hasWifiOpened()
	{
		//if( !isNetPermitted() )
		//	return false;
		return connectionMgr.getNetworkInfo(ConnectivityManager.TYPE_WIFI).getState() == NetworkInfo.State.CONNECTED ; 
	}
	
	/////////////TODO:test
	void changeNet(List<NeighboringCellInfo> listNeighCell)
	{
		//((GsmCellLocation) preMainCell).setLacAndCid(testNum, testNum+1);
		
		if( listNeighCell != null )
		{
			if( listNeighCell.size() == 0 )
			{
				NeighboringCellInfo neigh = new NeighboringCellInfo();
				neigh.setCid(testNum);
				listNeighCell.add(neigh);
				NeighboringCellInfo neigh1 = new NeighboringCellInfo();
				neigh1.setCid(testNum+1);
				listNeighCell.add(neigh1);
				testListNeigh = listNeighCell;
			}
			else
			{
				for(int i=0; i<listNeighCell.size(); ++i)
				{
					NeighboringCellInfo neigh = listNeighCell.get(i);
					neigh.setCid(testNum);
				}
			}
		}
		++testNum;
	}
	////////////////test
	/**
	 *@brief 设置是否可以扫描手机定位环境
	 *@param flag
	 */
	void setCheckEnvAble(Boolean flag)
	{
		mPermitedCheckEnv = flag;
	}
	
	/**
	 * @brief 检查是否GPS状态发生了变化
	 * @return true发生了变化，false没有变化
	 */
	Boolean hasGPSStateChanged()
	{
		return mHasGpsStatusChanged;
	}
	
	/**
	 * @brief 设置GPS状态是否改变
	 */
	void setGPSChangedState(Boolean state)
	{
		mHasGpsStatusChanged = state;
	}
	
	/**
	 * @brief 检查是否主网络状态发生了变化
	 * @return true发生了变化，false没有变化
	 */
	Boolean hasNetStateChanged()
	{
		Boolean ret = false;
		if( !mPermitedCheckEnv  || !isCanUseSim())
			return ret;
		StringBuffer reason = new StringBuffer();
		if( hasGprsOpened() )
		{
			if( hasMainCellChanged(reason))
				ret = true;
			else if( testNeighType != 0 )
			{
				if( testNeighType == 1 && hasNeighCellChanged(reason) )
					ret = true;
				else if( testNeighType == 2 && hasNeighCellChangedR(reason)  )
					ret = true;
			}
		}
		/*
		if( hasWifiOpened() )
		{
			if( hasMainWifiChanged(reason) )
				ret = true;
			else if( testNeighType != 0 )
			{
				if( testNeighType == 1 && hasNeighWifiChanged(reason) )
					ret = true;
				else if( testNeighType == 2 && hasNeighWifiChangedR(reason) )
					ret = true;
			}
		}*/
		String content = "";
		content += "PreCell=" + getCellInfo(preMainCell,mPreListNeighCell)+ "\n" +
				   "CurCell=" + getCellInfo() + "\n";
		content += "PreWifi=" + getWifiInfo(mPreListNeighWifi) + "\n" +
		 		   "CurWifi=" + getWifiInfo()+"\n";
		if( ret )
			content += "Net State Changed!!!reason:"+reason+"\n------------------------------\n";
		//Logger.getInstance().TIME(content);
		return ret;
	}
	
	
	/**
	 * @brief 更新上一次网络信息
	 */
	
	void updatePreNetInfo()
	{
		if( hasGprsOpened() )
		{
			preMainCell = getMainCell();
			mPreListNeighCell = phoneService.getNeighboringCellInfo();
			//TODO:test
			//mPreListNeighCell = testListNeigh;
			//test
		}
		else
		{
			preMainCell = null;
			mPreListNeighCell = null;
		}
		
		if( hasWifiOpened() )
		{
			preMainWifi = getMainWifi();
			mPreListNeighWifi =wifiService.getScanResults();
		}
		else
		{
			preMainWifi = null;
			mPreListNeighWifi = null;
		}
	}
	
	/**
	 * @brief 检查是否主基站发生了变化
	 * @return true发生了变化，false没有变化
	 */
	Boolean hasMainCellChanged(StringBuffer reason)
	{
		reason.append("MainCell:");
		Boolean ret = false;
		CellLocation currCellInfo = phoneService.getCellLocation();
		if( null == currCellInfo )
		{
			//GPRS is not available
			reason.append("null == currCellInfo;");
			return false;
		}
		else if( null == preMainCell )
		{
			//first detection
			reason.append("null == preMainCell;");
			return true;
		}
		else if( currCellInfo instanceof GsmCellLocation) 
		{
			if( preMainCell instanceof GsmCellLocation )
			{
				GsmCellLocation preMainGsmCell = (GsmCellLocation) preMainCell;
				GsmCellLocation gsmLocation = (GsmCellLocation) currCellInfo;
				if( gsmLocation.getLac() == preMainGsmCell.getLac() &&
					gsmLocation.getCid() == preMainGsmCell.getCid()	)
				{
					reason.append("Same Cell Id;");
					return false;
				}
				else
				{
					reason.append("Different Cell Id;");
					return true;
				}
			}
			
		}  
		else if ( currCellInfo instanceof CdmaCellLocation) 
		{
			if( preMainCell instanceof CdmaCellLocation )
			{
				CdmaCellLocation preMainCdmaCell = (CdmaCellLocation) preMainCell;
				CdmaCellLocation cdmaLocation = (CdmaCellLocation) currCellInfo;
				if( cdmaLocation.getSystemId() == preMainCdmaCell.getSystemId() &&
					cdmaLocation.getNetworkId() == preMainCdmaCell.getNetworkId() &&
					cdmaLocation.getBaseStationId() == preMainCdmaCell.getBaseStationId())
					return false;
				else
				{
					return true;
				}
			}
		}
		return ret;
		
	}
	
	/**
	 * @brief 检查用户输入cell信息是否和当前主cell信息一致
	 * @param usrCellInfo 类型CellLocation，用户传入的cell信息
	 * @return true相同，false不同
	 */
	Boolean isEqualCurMainCell(CellLocation usrCellInfo)
	{
		Boolean ret = false;
		CellLocation currCellInfo = phoneService.getCellLocation();
		if( null == currCellInfo && null == usrCellInfo )
		{
			ret = true;
		}
		if( null != currCellInfo && null != usrCellInfo )
		{
			if( currCellInfo instanceof GsmCellLocation && usrCellInfo instanceof GsmCellLocation) 
			{
				GsmCellLocation curCell = (GsmCellLocation) currCellInfo;
				GsmCellLocation usrCell = (GsmCellLocation) usrCellInfo;
				if( usrCell.getLac() == curCell.getLac() &&
					usrCell.getCid() == curCell.getCid()	)
					ret = true;
			}  
			else if ( currCellInfo instanceof CdmaCellLocation && usrCellInfo instanceof CdmaCellLocation) 
			{
				CdmaCellLocation usrCdma = (CdmaCellLocation) usrCellInfo;
				CdmaCellLocation curCdma = (CdmaCellLocation) currCellInfo;
				if( usrCdma.getSystemId() == curCdma.getSystemId() &&
					usrCdma.getNetworkId() == curCdma.getNetworkId() &&
					usrCdma.getBaseStationId() == curCdma.getBaseStationId())
					ret = true;
			}
		}
		return ret;
	}
	
	/**
	 * @brief 检查用户输入两个cell信息是否相同
	 * @param cellA,cellB 用户传入的cell信息
	 * @return true相同，false不同
	 */
	Boolean isSameCell(CellLocation cellA,CellLocation cellB)
	{
		Boolean ret = false;
		if( null == cellB && null == cellA )
		{
			ret = true;
		}
		if( null != cellB && null != cellA )
		{
			if( cellB instanceof GsmCellLocation && cellA instanceof GsmCellLocation) 
			{
				GsmCellLocation curCell = (GsmCellLocation) cellB;
				GsmCellLocation usrCell = (GsmCellLocation) cellA;
				if( usrCell.getLac() == curCell.getLac() &&
					usrCell.getCid() == curCell.getCid()	)
					ret = true;
			}  
			else if ( cellB instanceof CdmaCellLocation && cellA instanceof CdmaCellLocation) 
			{
				CdmaCellLocation usrCdma = (CdmaCellLocation) cellA;
				CdmaCellLocation curCdma = (CdmaCellLocation) cellB;
				if( usrCdma.getSystemId() == curCdma.getSystemId() &&
					usrCdma.getNetworkId() == curCdma.getNetworkId() &&
					usrCdma.getBaseStationId() == curCdma.getBaseStationId())
					ret = true;
			}
		}
		return ret;
	}
	
	/**
	 * @brief 检查临近基站是否发生变化
	 * @return true发生了变化，false没有变化
	 */
	Boolean hasNeighCellChanged(StringBuffer reason)
	{
		reason.append("NeighCell:");
		List<NeighboringCellInfo> curListNeighCell = phoneService.getNeighboringCellInfo();
		//TODO:test
		//changeNet(curListNeighCell);
		//test
		if( null == curListNeighCell && null == mPreListNeighCell )
		{
			reason.append("null == curListNeighCell && null == mPreListNeighCell;");
			return false;
		}
		int curSize = (null == curListNeighCell) ? 0 : curListNeighCell.size();
		int preSize = (null == mPreListNeighCell) ? 0 : mPreListNeighCell.size();
		if( Math.abs( curSize - preSize ) >= 2 )
		{
			reason.append("Math.abs( curSize - preSize ) >= 2;");
			return true;
		}
		if( null == curListNeighCell || null == mPreListNeighCell )
		{
			reason.append("one neighCell is null,other does not;");
			return true;
		}
		int countSame = 0;
		for (int i = 0; i < curListNeighCell.size(); i++) 
		{
			NeighboringCellInfo curNeighCell = curListNeighCell.get(i);
			//curNeighCell.
			for( int j=0; j < mPreListNeighCell.size(); ++j)
			{
				NeighboringCellInfo preNeighCell = mPreListNeighCell.get(j);
				if( curNeighCell.getLac() == preNeighCell.getLac() &&
					curNeighCell.getCid() == preNeighCell.getCid())
				{
					++countSame;
					break;
				}
			}
		}
		double sameRate =  (double)countSame/(double)preSize;
		if( sameRate <= 0.5 )
		{
			reason.append("sameRate <= 0.5;");
			return true;
		}
		reason.append("diffRate < 0.5;");
		return false;
	}
	
	/**
	 * @brief 检查临近基站是否发生变化
	 * @return true发生了变化，false没有变化
	 */
	Boolean hasNeighCellChangedR(StringBuffer reason)
	{
		reason.append("NeighCell:");
		List<NeighboringCellInfo> curListNeighCell = phoneService.getNeighboringCellInfo();
		if( null == curListNeighCell && null == mPreListNeighCell )
		{
			reason.append("null == curListNeighCell && null == mPreListNeighCell;");
			return false;
		}
		int curSize = (null == curListNeighCell) ? 0 : curListNeighCell.size();
		int preSize = (null == mPreListNeighCell) ? 0 : mPreListNeighCell.size();
		if( Math.abs( curSize - preSize ) >= 1 )
		{
			reason.append("Math.abs( curSize - preSize ) >= 1;");
			return true;
		}
		if( null == curListNeighCell || null == mPreListNeighCell )
		{
			reason.append("one neighCell is null,other does not;");
			return true;
		}
		int countSame = 0;
		for (int i = 0; i < curListNeighCell.size(); i++) 
		{
			NeighboringCellInfo curNeighCell = curListNeighCell.get(i);
			for( int j=0; j < mPreListNeighCell.size(); ++j)
			{
				NeighboringCellInfo preNeighCell = mPreListNeighCell.get(j);
				if( curNeighCell.getLac() == preNeighCell.getLac() &&
					curNeighCell.getCid() == preNeighCell.getCid())
				{
					++countSame;
					break;
				}
			}
		}
		double sameRate =  (double)countSame/(double)preSize;
		if( sameRate <= 0.9 )
		{
			reason.append("sameRate <= 0.9;");
			return true;
		}
		reason.append("diffRate > 0.1;");
		return false;
	}
	
	/**
	 * @brief 检查是否主Wifi发生了变化
	 * @return true发生了变化，false没有变化
	 */
	Boolean hasMainWifiChanged(StringBuffer reason)
	{
		reason.append("MainWifi");
		Boolean ret = false;
		String curWifiAddr = getMainWifi();
		if( null == curWifiAddr )
		{
			//current wifi is unAvailable
			reason.append("null == curWifiAddr;");
			ret = false;
		}
		else if( null == preMainWifi )
		{
			//this is the first detection
			reason.append("null == preMainWifi;");
			ret = true;
		}
		else if( preMainWifi.equalsIgnoreCase(curWifiAddr) )
		{
			//same wifi
			reason.append("preMainWifi.equalsIgnoreCase(curWifiAddr)== true;");
			ret = false;
		}
		else 
		{
			//wifi has changed
			reason.append("preMainWifi.equalsIgnoreCase(curWifiAddr)!= true;");
			ret = true;
		}
		return ret;
	}
	
	/**
	 * @brief 检查用户输入wifi信息是否和当前主wifi信息一致
	 * @param usrWifiAddr 类型String，用户传入的wifi mac add信息
	 * @return true相同，false不同
	 */
	Boolean isEqualCurMainWifi(String usrWifiAddr)
	{
		Boolean ret = false;
		String curWifiAddr = getMainWifi();
		if( null == curWifiAddr &&  null == usrWifiAddr)
			ret = true;
		else if( null != curWifiAddr &&  null != usrWifiAddr && preMainWifi.equalsIgnoreCase(curWifiAddr) )
			ret = true;
		return ret;
	}
	
	/**
	 * @brief 检查用户输入的两个wifi信息相同
	 * @param wifiA,wifiB 用户传入的wifi信息
	 * @return true相同，false不同
	 */
	Boolean isSameWifi(String wifiA,String wifiB)
	{
		Boolean ret = false;
		if( null == wifiB &&  null == wifiA)
			ret = true;
		else if( null != wifiB &&  null != wifiA && preMainWifi.equalsIgnoreCase(wifiA) )
			ret = true;
		return ret;
	}
	
	/**
	 * @brief 检查临近Wifi是否发生变化
	 * @return true发生了变化，false没有变化
	 */
	Boolean hasNeighWifiChanged(StringBuffer reason)
	{
		reason.append("NeighWifi");
		List<ScanResult> curListNeighWifi = wifiService.getScanResults();
		if( null == curListNeighWifi && null == mPreListNeighWifi )
		{
			reason.append("null == curListNeighWifi && null == mPreListNeighWifi;");
			return false;
		}
		int curSize = (null == curListNeighWifi) ? 0 : curListNeighWifi.size();
		int preSize = (null == mPreListNeighWifi) ? 0 : mPreListNeighWifi.size();
		if( Math.abs( curSize - preSize ) >= 2 )
		{
			reason.append("Math.abs( curSize - preSize ) >= 2");
			return true;
		}
		if( null == curListNeighWifi || null == mPreListNeighWifi )
		{
			reason.append("null == curListNeighWifi || null == mPreListNeighWifi;");
			return true;
		}
		int countSame = 0;
		for (int i = 0; i < curListNeighWifi.size(); i++) 
		{
			String curWifiAddr = curListNeighWifi.get(i).BSSID;
			for( int j=0; j < mPreListNeighWifi.size(); ++j)
			{
				String preWifiAddr = mPreListNeighWifi.get(j).BSSID;
				if( curWifiAddr != null && preWifiAddr != null && preWifiAddr.equalsIgnoreCase(curWifiAddr))
					++countSame;
			}
		}
		double sameRate =  (double)countSame/(double)preSize;
		if( sameRate <= 0.5 )
		{
			reason.append("sameRate <= 0.5;");
			return true;
		}
		reason.append("diffRate < 0.5;");
		return false;
	}
	
	/**
	 * @brief 检查临近Wifi是否发生变化
	 * @return true发生了变化，false没有变化
	 */
	Boolean hasNeighWifiChangedR(StringBuffer reason)
	{
		reason.append("NeighWifi");
		List<ScanResult> curListNeighWifi = wifiService.getScanResults();

		if( null == curListNeighWifi && null == mPreListNeighWifi )
		{
			reason.append("null == curListNeighWifi && null == mPreListNeighWifi;");
			return false;
		}
		int curSize = (null == curListNeighWifi) ? 0 : curListNeighWifi.size();
		int preSize = (null == mPreListNeighWifi) ? 0 : mPreListNeighWifi.size();
		if( Math.abs( curSize - preSize ) >= 1 )
		{
			reason.append("Math.abs( curSize - preSize ) >= 1;");
			return true;
		}
		if( null == curListNeighWifi || null == mPreListNeighWifi )
		{
			reason.append("null == curListNeighWifi || null == mPreListNeighWifi ;");
			return true;
		}
		int countSame = 0;
		for (int i = 0; i < curListNeighWifi.size(); i++) 
		{
			String curWifiAddr = curListNeighWifi.get(i).BSSID;
			for( int j=0; j < mPreListNeighWifi.size(); ++j)
			{
				String preWifiAddr = mPreListNeighWifi.get(i).BSSID;
				if( curWifiAddr != null && preWifiAddr != null && preWifiAddr.equalsIgnoreCase(curWifiAddr))
					++countSame;
			}
		}
		double sameRate =  (double)countSame/(double)preSize;
		if( sameRate <= 0.9 )
		{
			reason.append("sameRate <= 0.9;");
			return true;
		}
		reason.append("diffRate < 0.1;");
		return false;
	}
	/**
	 * @brief 
	 * @return 
	 */
	TelephonyManager getTelephonyManager()
	{
		return phoneService;
	}
	
	/**
	 * @brief 
	 * @return 
	 */
	WifiManager getWifiManager()
	{
		return wifiService;
	}
	
	
	/**
	 * @brief 获得临近wifi个数
	 * @return 
	 */
	int getNeighWifiNum()
	{
		int ret = 0;
		List<ScanResult> curListNeighWifi = wifiService.getScanResults();
		if( curListNeighWifi != null )
			ret = curListNeighWifi.size() - 1;
		return ret;
	}

	/**
	 * @brief 获得临近基站个数
	 * @return 
	 */
	int getNeighCellNum()
	{
		int ret = 0;
		List<NeighboringCellInfo> curListNeighCell = phoneService.getNeighboringCellInfo();
		if( curListNeighCell != null )
			ret = curListNeighCell.size();
		return ret;
	}
	
	/**
	 * @brief 获得Main Cell
	 * @return 如果成功返回CellLocation对象,否则返回null;
	 */	
	CellLocation getMainCell()
	{
		return phoneService.getCellLocation();
	}
	
	/**
	 * @brief 获取临近基站信息
	 * @return
	 */
	List<NeighboringCellInfo> getNeighCell()
	{
		return phoneService.getNeighboringCellInfo();
	}
	
	/**
	 * @brief 获取wifi item信息
	 * @return
	 */
	List<ScanResult> getWifiItems()
	{
		return wifiService.getScanResults();
	}
	
	/**
	 * @brief 获得Main Wifi Address
	 * @return String 如果成功返回wifi address对象,否则返回null;
	 */	
	String getMainWifi()
	{
		String ret = null;
		List<ScanResult> srs = wifiService.getScanResults();
		if( srs != null && srs.size() > 0 )
			ret = srs.get(0).BSSID;
		return ret;
	}
		

	/**
	 * @brief 获得Cell信号强度
	 */	
	
	int getCellStrength()
	{
		return servRSSI; 
	}
	
	/**
	 * @brief 获得Cell信号强度
	 */	
	
	int getCDMAStrength()
	{
		return mCDMARssi; 
	}
	/**
	 * @brief 获得IMEI信息
	 * @return String IMEI信息
	 */	
	String getIMEI()
	{
		return phoneService.getDeviceId();
	}
	
	/**
	 * @brief 获得cell信息
	 * @return String 格式化的cell信息
	 */	
	String getCellInfo()
	{
		String cellInfo = "";
		CellLocation location = phoneService.getCellLocation();
		if( location != null && location instanceof GsmCellLocation) 
		{
			GsmCellLocation gsmLocation = (GsmCellLocation) location;
			cellInfo += String.format("ServCell=[%d*%d*%d],", 
									  gsmLocation.getLac(),gsmLocation.getCid(),servRSSI);
			List<NeighboringCellInfo> ncis = phoneService.getNeighboringCellInfo();
			for (int i = 0; ncis != null && i < ncis.size(); i++) 
			{
				NeighboringCellInfo nci = ncis.get(i);
				String format="";
				if( i == ncis.size() - 1 )
					format = "Neighbour%d=[%d*%d*%d]";
				else
					format = "Neighbour%d=[%d*%d*%d],";
				cellInfo += String.format(format ,i+1,nci.getLac(),nci.getCid(),nci.getRssi());
			}
		} 
		else if (location == null)
		{
			cellInfo="Get CellLocation Failed";
		}
		else if ( !(location instanceof GsmCellLocation)) 
		{
			cellInfo="CellInfo is not belonged to GSM";
		}
		return cellInfo;
	}
	
	/**
	 * @brief 获得cell信息
	 * @return String 格式化的cell信息
	 */	
	String getCellInfo( CellLocation location ,List<NeighboringCellInfo> ncis)
	{
		String cellInfo = "";
		if( location != null && location instanceof GsmCellLocation) 
		{
			GsmCellLocation gsmLocation = (GsmCellLocation) location;
			cellInfo += String.format("ServCell=[%d*%d*%d],", 
									  gsmLocation.getLac(),gsmLocation.getCid(),servRSSI);
			for (int i = 0; ncis != null && i < ncis.size(); i++) 
			{
				NeighboringCellInfo nci = ncis.get(i);
				String format="";
				if( i == ncis.size() - 1 )
					format = "Neighbour%d=[%d*%d*%d]";
				else
					format = "Neighbour%d=[%d*%d*%d],";
				cellInfo += String.format(format ,i+1,nci.getLac(),nci.getCid(),nci.getRssi());
			}
		} 
		else if (location == null)
		{
			cellInfo="Get CellLocation Failed";
		}
		else if ( !(location instanceof GsmCellLocation)) 
		{
			cellInfo="CellInfo is not belonged to GSM";
		}
		return cellInfo;
	}
	
	/**
	 * @brief 获得wifi信息
	 * @return String 格式化的wifi信息
	 */	
	String getWifiInfo()
	{
		String wifiInfo = "";
		List<ScanResult> srs = wifiService.getScanResults();
		for (int i = 0; srs != null && i < srs.size(); i++) 
		{
			ScanResult sr = srs.get(i);
			String format="";
			if( i == srs.size() - 1 )
				format = "Wifi%d=[%s*%d]";
			else
				format = "Wifi%d=[%s*%d],";
			wifiInfo += String.format(format, i+1,sr.BSSID,sr.level);
		}
		return wifiInfo;
	}

	/**
	 * @brief 获得wifi信息
	 * @return String 格式化的wifi信息
	 */	
	String getWifiInfo( List<ScanResult> srs )
	{
		String wifiInfo = "";
		for (int i = 0; srs != null && i < srs.size(); i++) 
		{
			ScanResult sr = srs.get(i);
			String format="";
			if( i == srs.size() - 1 )
				format = "Wifi%d=[%s*%d]";
			else
				format = "Wifi%d=[%s*%d],";
			wifiInfo += String.format(format, i+1,sr.BSSID,sr.level);
		}
		return wifiInfo;
	}
	
	/**
	 * @brief 用户是否允许网络辅助定位
	 */
	boolean isNetPermitted()
	{
		return 	((LocationManager) mContext.getSystemService(Context.LOCATION_SERVICE)).isProviderEnabled(LocationManager.NETWORK_PROVIDER);
	}
	
	
	/**
	 * @brief 检查是否有可用的网路连接
	 * @param mActivity  Activity
	 * @return true如果有可用的网路连接
	 */
	boolean isNetworkAvailable() 
	{
		if( !mHasInited )
			return false;
		ConnectivityManager connectivity = (ConnectivityManager) mContext.getSystemService(Context.CONNECTIVITY_SERVICE);
		if (connectivity == null)
		{
			return false;
		} 
		else 
		{
			NetworkInfo[] info = connectivity.getAllNetworkInfo();
			if (info != null) 
			{
				for (int i = 0; i < info.length; i++) 
				{
					if (info[i].getState() == NetworkInfo.State.CONNECTED) 
					{
						return true;
					}
				}
			}
		}
		return false;
	}
	/**
	 * @brief 检查wifi是否已经连上
	 * @return true已连接
	 */
	boolean isWifiConnectted() 
	{
		//if( !isNetPermitted() )
		//	return false;
		ConnectivityManager connectivity = (ConnectivityManager) mContext.getSystemService(Context.CONNECTIVITY_SERVICE);
		if (connectivity == null)
		{
			return false;
		} 
		else 
		{
			NetworkInfo[] info = connectivity.getAllNetworkInfo();
			if (info != null) 
			{
				for (int i = 0; i < info.length; i++) 
				{
					if (info[i].getType() == ConnectivityManager.TYPE_WIFI &&  info[i].getState() == NetworkInfo.State.CONNECTED) 
					{
						return true;
					}
				}
			}
		}
		return false;
	}
	/**
	 * @brief 获得IMEI号，放入固定16字节长度的字节数组里
	 * @return 长度为16的字节数组，尾部为\0
	 */
	byte[] getByte16IMEI()
	{
		byte[] ret = new byte[16];
		String  imeiStr= phoneService.getDeviceId();
		int len = imeiStr.length() >= 15 ? 15 : imeiStr.length();
		imeiStr.getBytes(0, len, ret, 0);
		ret[len]='\0';
		return ret;
	}
	
	/**
	 * @brief 获得手机DeviceType，放入最大长度为16的字节数组里
	 * @return 最大长度为16的字节数组，末尾一位为\0
	 */
	byte[] getDeviceTypeMax16B()
	{
		String typeStr = Build.MODEL;
		if( null == typeStr )
			typeStr = "unkown";
		int len = typeStr.length() >= 15 ? 15 : typeStr.length();
		byte[] ret = new byte[len+1];
		typeStr.getBytes(0, len, ret, 0);
		ret[len]='\0';
		return ret;
	}
	
	/**
	 * @brief sim卡是否可读
	 * @return true-可用，false不可用
	 */ 
	public boolean isCanUseSim() 
	{ 
	    try 
	    { 
	        return TelephonyManager.SIM_STATE_READY == phoneService.getSimState(); 
	    } 
	    catch (Exception e) 
	    { 
	        e.printStackTrace(); 
	    } 
	    return false; 
	} 

	/**
	 * @brief 判断当前sdcard是否可用
	 * @return true-可用，false不可用
	 */
	Boolean isSdcardAvailable()
	{
		Boolean ret = false;
		try{
			ret = Environment.getExternalStorageState().equals(android.os.Environment.MEDIA_MOUNTED);
		}
		catch(Exception e)
		{
		}
		return ret;
	}
	/**
	 * @brief 获得sdcard路径+"/"
	 * @return
	 */
	String getSdcardPath()
	{
		String ret="null";
		try{
			if (isSdcardAvailable()) 
			{ 
				String sdDir = Environment.getExternalStorageDirectory().toString();//获取跟目录
				if( sdDir != null )
					ret = sdDir + "/";
			} 
		}
		catch(Exception e)
		{}
		return ret;
	}
	
	/**
	 * @brief 获得本机IP地址+"/"
	 * @return
	 */
	public String getLocalIpAddress() 
	{
		try 
		{
			for (Enumeration<NetworkInterface> en = NetworkInterface.getNetworkInterfaces(); en.hasMoreElements();) 
			{
				NetworkInterface intf = en.nextElement();
				for (Enumeration<InetAddress> enumIpAddr = intf.getInetAddresses(); enumIpAddr.hasMoreElements();) 
				{
					InetAddress inetAddress = enumIpAddr.nextElement();
					if (!inetAddress.isLoopbackAddress()) 
					{
						return inetAddress.getHostAddress().toString();
					}
				}
			}
		} 
		catch (SocketException ex) 
		{
			System.err.println(ex.toString());
		}
		return "unknown";
	}
	
	/**
	 * @brief:获得手机电量
	 * @return 电量整数值，100为满，0为无电量 
	 */
	int getBatteryLevel()
	{
		return batteryLevel;
	}
	
	/**
	 * 释放手机状态侦听器 
	 */
	void releaseListeners()
	{
		mContext.unregisterReceiver(mBatteryInfoReceiver);
	}
	
	/**
	 * @brief 手机状态侦听器
	 */	
	private class MyPhoneStateListener extends PhoneStateListener
    {
      /* Get the Signal strength from the provider, each time there is an update */
	@TargetApi(7)
	@Override
      public void onSignalStrengthsChanged(SignalStrength signalStrength)
      {
         super.onSignalStrengthsChanged(signalStrength);
         int asm = signalStrength.getGsmSignalStrength();
         servRSSI = asm;
         mCDMARssi = signalStrength.getCdmaDbm();
      }
    }
	
	/**
	 * @brief 手机电量侦听器
	 */	
    private BroadcastReceiver mBatteryInfoReceiver = new BroadcastReceiver() 
    {
        @Override
        public void onReceive(Context context, Intent intent) 
        {
            String action = intent.getAction();
            if (Intent.ACTION_BATTERY_CHANGED.equals(action)) 
            {
            	batteryLevel = intent.getIntExtra("level", 0);
            }

        }

    };

	
}
