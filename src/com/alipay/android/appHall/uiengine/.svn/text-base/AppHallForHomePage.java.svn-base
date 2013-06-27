package com.alipay.android.appHall.uiengine;

import java.util.Observable;

import android.os.Build;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;

import com.alipay.android.appHall.AppHall;
import com.alipay.android.appHall.appManager.AppItemInfo;
import com.alipay.android.client.RootActivity;
import com.alipay.android.client.util.Utilz;
import com.eg.android.AlipayGphone.R;

public class AppHallForHomePage extends AppHall {
    private LinearLayout mLinearLayout;

    public AppHallForHomePage(RootActivity context) {
        super(context);
    }

    public void setContainerViewForHomePage(LinearLayout view) {
        mLinearLayout = view;
        refresh();
    }

    private void refresh() {
        View[] views = new View[4];
        views[0] = (RelativeLayout) mLinearLayout.findViewById(R.id.appFirst);
        views[1] = (RelativeLayout) mLinearLayout.findViewById(R.id.appSecond);
        views[2] = (RelativeLayout) mLinearLayout.findViewById(R.id.appThird);
        views[3] = (RelativeLayout) mLinearLayout.findViewById(R.id.appFourth);

        for (int i = 0; i < views.length; ++i) {
            final AppItemInfo info = mAppItemInfos.get(i);
            if (info == null){
            	setDefaultAppStyle(views[i]);
            	continue;
            }
            views[i] = info.getViewForShortcut(mContext, views[i], i);
            if (info.getState() == AppItemInfo.STATE_DELETEED) {
                ImageView imageView = (ImageView) views[i].findViewById(R.id.SubItemIcon);
                imageView.getDrawable().setAlpha(127);
                imageView.invalidate();
            } else {
                ImageView imageView = (ImageView) views[i].findViewById(R.id.SubItemIcon);
                imageView.getDrawable().setAlpha(255);
                imageView.invalidate();
            }
            
            views[i].setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {

                    if (info.getState() == AppItemInfo.STATE_DELETEED) {
                        return;
                    }

                    info.onClick(mContext, null, AppItemInfo.FROM_HOMEPAGE);
                }
            });
        }
    }
    
    private void setDefaultAppStyle(View view){
    	ImageView mImage = (ImageView) view.findViewById(R.id.SubItemIcon);
    	mImage.setImageResource(R.drawable.homepage_appsdefault);
    	ProgressBar mProgressBar = (ProgressBar) view.findViewById(R.id.progressbar);
    	mProgressBar.setVisibility(View.INVISIBLE);
    	ImageView overlay = (ImageView) view.findViewById(R.id.overlay);
        overlay.setVisibility(View.INVISIBLE);
    }

    @Override
    public void update(Observable observable, Object data) {
        mContext.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                mAppItemInfos.clear();
                mAppItemInfos.addAll(mHallData.getHomeData());
                // 在这里如何刷新?
                refresh();
            }
        });
    }

    @Override
    public void init() {
        mAppItemInfos = mHallData.getHomeData();
    }
}
