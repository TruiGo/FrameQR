//package com.alipay.android.appHall.uiengine;
//
//import java.util.Observable;
//import java.util.Observer;
//
//import android.view.View;
//import android.view.View.OnClickListener;
//import android.widget.Button;
//import android.widget.FrameLayout;
//import android.widget.ProgressBar;
//
//import com.alipay.android.appHall.appManager.AppItemInfo;
//import com.alipay.android.appHall.appManager.HallData;
//import com.alipay.android.client.RootActivity;
//import com.eg.android.AlipayGphone.R;
//
//public class AppHallForCouponMarket implements Observer {
//    private FrameLayout marketShortcut;
//    private RootActivity mContext;
//    private HallData mHallData;
//
//    public AppHallForCouponMarket(RootActivity context) {
//        mContext = context;
//        mHallData = mContext.getHallData();
//        mHallData.addObserver(this);
//    }
//
//    public void setContainerViewForCouponMarket(FrameLayout view) {
//        marketShortcut = view;
//        refresh();
//    }
//
//    private void refresh() {
//        Button marketButton = (Button) marketShortcut.findViewById(R.id.couponMarket);
//        ProgressBar marketProgress = (ProgressBar) marketShortcut
//            .findViewById(R.id.loadingMarketProgress);
//        final AppItemInfo info = mHallData.getAppConfig(HallData.COUPON_MARKET);
//        if (info == null)
//            return;
//        marketButton.setOnClickListener(new OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                if (info.getState() != AppItemInfo.STATE_UPGRADEING
//                    && info.getState() != AppItemInfo.STATE_INSTALLING)
//                    info.onClick(mContext, null, AppItemInfo.FROM_APPCTETER);
//            }
//        });
//        marketButton.setBackgroundResource(R.drawable.couponmarket_normal);
//        marketProgress.setVisibility(View.GONE);
//
//        switch (info.getState()) {
//            case AppItemInfo.STATE_PRE_INSTALL:
//            	marketButton.setBackgroundResource(R.drawable.couponmarket_update);
//                break;
//            case AppItemInfo.STATE_INSTALLING:
//            case AppItemInfo.STATE_UPGRADEING:
//                marketButton.setBackgroundResource(R.drawable.couponmarket_updating);
//                marketProgress.setVisibility(View.VISIBLE);
//                break;
//            case AppItemInfo.STATE_PRE_UPGRADE:
//                marketButton.setBackgroundResource(R.drawable.couponmarket_update);
//                break;
//            default:
//                break;
//        }
//    }
//
//    @Override
//    public void update(Observable observable, Object data) {
//        mContext.runOnUiThread(new Runnable() {
//            @Override
//            public void run() {
//                refresh();
//            }
//        });
//    }
//
//}
