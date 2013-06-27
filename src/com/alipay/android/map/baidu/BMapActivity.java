/**
 * 
 */
package com.alipay.android.map.baidu;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

import com.alipay.android.map.GeoPointUtil;
import com.baidu.mapapi.BMapManager;
import com.baidu.mapapi.GeoPoint;
import com.baidu.mapapi.MKAddrInfo;
import com.baidu.mapapi.MKBusLineResult;
import com.baidu.mapapi.MKDrivingRouteResult;
import com.baidu.mapapi.MKGeneralListener;
import com.baidu.mapapi.MKPlanNode;
import com.baidu.mapapi.MKPoiResult;
import com.baidu.mapapi.MKSearch;
import com.baidu.mapapi.MKSearchListener;
import com.baidu.mapapi.MKSuggestionResult;
import com.baidu.mapapi.MKTransitRoutePlan;
import com.baidu.mapapi.MKTransitRouteResult;
import com.baidu.mapapi.MKWalkingRouteResult;
import com.baidu.mapapi.MapActivity;
import com.baidu.mapapi.MapController;
import com.baidu.mapapi.MapView;
import com.baidu.mapapi.RouteOverlay;
import com.baidu.mapapi.TransitOverlay;
import com.eg.android.AlipayGphone.R;

/**
 * @author sanping.li
 *
 */
public class BMapActivity extends MapActivity implements MKSearchListener {
    public static final String KEY_BAIDU_MAP = "AAC5C793C7CDB8A5535BB52034FD84C59415C1CD";

    private BMapManager mBMapMan;
    private MapView mMapView;
    private TextView mTitleName;

    private MKSearch mMKSearch;

    private String mGeoPoint;
    private String mLabelText;

    private String mSrcGeo;
    private String mDestGeo;
    private String mMode;

    @Override
    protected void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        setContentView(R.layout.bd_map);

        Intent intent = getIntent();
        mGeoPoint = intent.getStringExtra("geo");
        mLabelText = intent.getStringExtra("label");
        mSrcGeo = intent.getStringExtra("srcGeo");
        mDestGeo = intent.getStringExtra("destGeo");
        mMode = intent.getStringExtra("mode");

        loadAllVariables();

        doAction();
    }

    private void loadAllVariables() {
        mTitleName = (TextView) findViewById(R.id.title_text);
        mTitleName.setText(R.string.map);
        
        mBMapMan = new BMapManager(getApplication());
        mBMapMan.init(KEY_BAIDU_MAP, new MKGeneralListener() {
            @Override
            public void onGetPermissionState(int iError) {
                Toast.makeText(BMapActivity.this, getString(R.string.authorization_error)+iError, Toast.LENGTH_SHORT).show();//返回授权验证错误，通过错误代码判断原因，MKEvent中常量值。
            }

            @Override
            public void onGetNetworkState(int iError) {
                Toast.makeText(BMapActivity.this, getString(R.string.net_error)+iError, Toast.LENGTH_SHORT).show();// 返回网络错误，通过错误代码判断原因，MKEvent中常量值。
            }
        });
        super.initMapActivity(mBMapMan);

        mMapView = (MapView) findViewById(R.id.bmapsView);
        mMapView.setBuiltInZoomControls(true); //设置启用内置的缩放控件
    }

    private void doAction() {
        if (mGeoPoint != null) {//标注
            MapController mMapController = mMapView.getController(); // 得到mMapView的控制权,可以用它控制和驱动平移和缩放
            GeoPoint point = GeoPointUtil.transToBGeoPoint(mGeoPoint);
            mMapController.setCenter(point); //设置地图中心点
            mMapController.setZoom(15); //设置地图zoom级别

            Drawable marker = getResources().getDrawable(R.drawable.icon_map_marka);
            mMapView.getOverlays().add(new LocationOverlay(point, marker, mLabelText));
        } else {//路线
            MapController mMapController = mMapView.getController(); // 得到mMapView的控制权,可以用它控制和驱动平移和缩放
            GeoPoint point = GeoPointUtil.transToBGeoPoint(mSrcGeo);
            mMapController.setCenter(point); //设置地图中心点

            mMKSearch = new MKSearch();
            mMKSearch.init(mBMapMan, this);
            GeoPoint geoPoint = GeoPointUtil.transToBGeoPoint(mSrcGeo);
            if (mMode.equalsIgnoreCase("d") || mMode.equalsIgnoreCase("w")) {
                MKPlanNode start = new MKPlanNode();
                start.pt = geoPoint;
                MKPlanNode end = new MKPlanNode();
                end.pt = GeoPointUtil.transToBGeoPoint(mDestGeo);
                if (mMode.equalsIgnoreCase("d")) {
                    // 设置驾车路线搜索策略，时间优先、费用最少或距离最短
                    mMKSearch.setDrivingPolicy(MKSearch.ECAR_TIME_FIRST);
                    mMKSearch.drivingSearch(null, start, null, end);
                } else if (mMode.equalsIgnoreCase("w")) {
                    mMKSearch.walkingSearch(null, start, null, end);
                }
            } else if (mMode.equalsIgnoreCase("r")) {//需要先查询城市
                mMKSearch.reverseGeocode(geoPoint);
            }
        }
    }

    @Override
    protected void onDestroy() {
        if (mBMapMan != null) {
            mBMapMan.destroy();
            mBMapMan = null;
        }
        super.onDestroy();
    }

    @Override
    protected void onPause() {
        if (mBMapMan != null) {
            mBMapMan.stop();
        }
        super.onPause();
    }

    @Override
    protected void onResume() {
        if (mBMapMan != null) {
            mBMapMan.start();
        }
        super.onResume();
    }

    @Override
    protected boolean isRouteDisplayed() {
        return false;
    }

    @Override
    public void onGetAddrResult(MKAddrInfo result, int iError) {
        if (result == null) {
            Toast.makeText(this, getString(R.string.get_city_error)+iError, Toast.LENGTH_SHORT).show();//获取城市错误，通过错误代码判断原因，MKEvent中常量值。
            return;
        }
        String city = result.addressComponents.city;
        MKPlanNode start = new MKPlanNode();
        start.pt = GeoPointUtil.transToBGeoPoint(mSrcGeo);
        MKPlanNode end = new MKPlanNode();
        end.pt = GeoPointUtil.transToBGeoPoint(mDestGeo);
        mMKSearch.setTransitPolicy(MKSearch.EBUS_TIME_FIRST);
        mMKSearch.transitSearch(city, start, end);
    }

    @Override
    public void onGetBusDetailResult(MKBusLineResult arg0, int arg1) {

    }

    @Override
    public void onGetDrivingRouteResult(MKDrivingRouteResult result, int iError) {
        if (result == null) {
            Toast.makeText(this, getString(R.string.get_driving_route_error)+iError, Toast.LENGTH_SHORT).show();//获取驾车路线错误，通过错误代码判断原因，MKEvent中常量值。
            return;
        }
        RouteOverlay routeOverlay = new RouteOverlay(BMapActivity.this, mMapView);
        routeOverlay.setData(result.getPlan(0).getRoute(0));
        mMapView.getOverlays().add(routeOverlay);
        mMapView.invalidate(); //刷新地图
    }

    @Override
    public void onGetPoiResult(MKPoiResult arg0, int arg1, int arg2) {

    }

    @Override
    public void onGetSuggestionResult(MKSuggestionResult arg0, int arg1) {

    }

    @Override
    public void onGetTransitRouteResult(MKTransitRouteResult result, int iError) {
        if (result == null) {
            Toast.makeText(this, getString(R.string.get_transit_route_error)+iError, Toast.LENGTH_SHORT).show();//获取公交路线错误，通过错误代码判断原因，MKEvent中常量值。
            return;
        }
        popItems(result);
    }

    @Override
    public void onGetWalkingRouteResult(MKWalkingRouteResult result, int iError) {
        if (result == null) {
            Toast.makeText(this, getString(R.string.get_walk_route_erro)+iError, Toast.LENGTH_SHORT).show();//获取步行路线错误，通过错误代码判断原因，MKEvent中常量值。
            return;
        }
        RouteOverlay routeOverlay = new RouteOverlay(BMapActivity.this, mMapView);
        // 此处仅展示一个方案作为示例
        routeOverlay.setData(result.getPlan(0).getRoute(0));
        mMapView.getOverlays().add(routeOverlay);
        mMapView.invalidate(); //刷新地图
    }
    
    private void popItems(final MKTransitRouteResult result){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(R.string.select_lines);
        MKTransitRoutePlan planNode = null;
        int num = result.getNumPlan();
        String[] contents = new String[num];
        String lines = null;
        for(int i =0;i<num;++i){
            planNode = result.getPlan(i);
            lines = "";
            for(int j = 0;j<planNode.getNumLines();++j){
                if(lines.length()>0)
                    lines+="->";
                lines+=planNode.getLine(j).getTitle();
            }
            contents[i]=lines;
        }
        builder.setCancelable(false);
        builder.setSingleChoiceItems(contents, 0, new DialogInterface.OnClickListener() {
            
            @Override
            public void onClick(DialogInterface dialog, int which) {
                TransitOverlay transitOverlay = new TransitOverlay(BMapActivity.this, mMapView);
                transitOverlay.setData(result.getPlan(which));
                mMapView.getOverlays().add(transitOverlay);
                mMapView.invalidate(); //刷新地图
                dialog.dismiss();
            }
        });
        builder.show();
    }

}
