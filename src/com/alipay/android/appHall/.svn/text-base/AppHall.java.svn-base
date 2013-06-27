package com.alipay.android.appHall;

import java.util.ArrayList;
import java.util.Observable;
import java.util.Observer;

import com.alipay.android.appHall.appManager.AppItemInfo;
import com.alipay.android.appHall.appManager.HallData;
import com.alipay.android.client.RootActivity;

public abstract class AppHall implements Observer {
    protected RootActivity mContext;
    /**
     * 业务列表
     */
    protected ArrayList<AppItemInfo> mAppItemInfos;
    protected HallData mHallData;

    public AppHall(RootActivity context) {
        mContext = context;

        mHallData = mContext.getHallData();
        if (mHallData.getState()<=0) {
            mHallData.initializeAppList();
            mHallData.syncAppStatus();
        }
        mAppItemInfos = new ArrayList<AppItemInfo>();
        
        ArrayList<AppItemInfo> hallDatas = mHallData.getData();
		if (!mHallData.getFuncId().equalsIgnoreCase("")) {
			String[] funcids = mHallData.getFuncId().split("\\|");
			for (int j = 0; j < funcids.length; j++) {
				for (int i = 0; i < hallDatas.size(); i++) {
					if (hallDatas.get(i).getId()
							.equals(funcids[j])) {
						hallDatas.remove(i);
					}
				}
			}
		}
        
        
        init();
        mHallData.addObserver(this);
    }

    public abstract void init();

    public abstract void update(Observable observable, Object data);
}
