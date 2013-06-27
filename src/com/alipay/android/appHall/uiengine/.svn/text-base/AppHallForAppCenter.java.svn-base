package com.alipay.android.appHall.uiengine;

import java.util.ArrayList;
import java.util.Observable;

import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.AdapterView.OnItemClickListener;

import com.alipay.android.appHall.AppHall;
import com.alipay.android.appHall.appManager.AppItemInfo;
import com.alipay.android.client.AlipayApplication;
import com.alipay.android.client.RootActivity;

public class AppHallForAppCenter extends AppHall {
    private GridView mGridView;
    private ItemAdapterForAppCenter itemAdapter;

    public AppHallForAppCenter(RootActivity context) {
        super(context);
        // TODO Auto-generated constructor stub
    }

    public void setContainerViewForAppCenter(GridView view) {
        mGridView = view;
        mGridView.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View arg1, int index, long arg3) {
                if (index < mAppItemInfos.size()) {
                    AppItemInfo itemInfo = mAppItemInfos.get(index);
                    itemInfo.onClick(mContext, null, AppItemInfo.FROM_APPCTETER);
                }
            }
        });

        itemAdapter = new ItemAdapterForAppCenter();
        mGridView.setAdapter(itemAdapter);
    }

    // AppItemAdater for mAppsGrid.
    public class ItemAdapterForAppCenter extends BaseAdapter {
        AppItemInfo info = new AppItemInfo(null, null, 0);

        @Override
        public int getCount() {
            int num = mAppItemInfos.size();
            int cols = 4;//mGridView.getNumColumns();
            int n = num % cols;
            if (n == 0)
                return num;
            return num + cols - n;
        }

        @Override
        public Object getItem(int position) {
            return null;
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int index, View convertView, ViewGroup parent) {
            View view = null;
            if (index < mAppItemInfos.size()) {
                AppItemInfo itemInfo = mAppItemInfos.get(index);
                view = itemInfo.getViewForAppCenter(mContext, convertView);
            } else {
                view = info.getViewForAppCenter(mContext, convertView);
            }
            return view;
        }
    }

    @Override
    public void update(Observable observable, Object data) {
        if (data != null) {
            AlipayApplication alipayApplication = (AlipayApplication) mContext
                .getApplicationContext();
            mHallData = alipayApplication.getHallData();
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
            
            mAppItemInfos.addAll(mHallData.getData());
            mHallData.addObserver(this);
        }

        mContext.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                mAppItemInfos.clear();
                mAppItemInfos.addAll(mHallData.getAppCenterData());
                itemAdapter.notifyDataSetChanged();
            }
        });
    }

    @Override
    public void init() {
        mAppItemInfos.addAll(mHallData.getAppCenterData());
    }

}
