/*
 * Copyright 2009, Embedded General Inc.  All right reserved
 * 
 * draft:Roger
 * 
 */
package com.alipay.android.client;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Html;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.AdapterView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import com.alipay.android.client.constant.AlipayHandlerMessageIDs;
import com.alipay.android.client.constant.Constant;
import com.alipay.android.client.util.BaseHelper;
import com.alipay.android.client.util.LephoneConstant;
import com.alipay.android.client.util.Responsor;
import com.alipay.android.comon.component.PullRefreshView;
import com.alipay.android.comon.component.ScrollMoreListAdapter.MoreListener;
import com.eg.android.AlipayGphone.R;

/**
 *  收支明细
 */
public class SubItemAccountQueryActivity extends RootActivity implements OnClickListener {
    private static final int TODAY = 0;
    private static final int THISWEEK = TODAY + 1;
    private static final int THISMONTH = THISWEEK + 1;

    private int type;
    private ArrayList<InfoItem> mDatas;
    private DealListAdapter mListAdapter;
    private String[] mTradeTypes;

    private int mCurrentPage;//当前页数
    private int mCount;//获取到的条数
    private int mRequestPage;//请求页数
    private int mTotalPage;//总页数
    private int mTotalCount;//总条数

    private boolean mRefresh;
    private boolean mPullRefresh;

    private TextView mTitleView;
    private ListView mListView;
    private PullRefreshView mRefreshView;
    private TextView mEmptyView;
    private PopupWindow mPopupWindow;

    JSONObject myJsonObject = null;
    private MessageFilter mMessageFilter;
    int totalCount = 0;
    int currentPage = 0;
    int pageCount = 10;
    /**
     * 是否加载状态
     */
    private boolean mIsLoading;

    private Handler mHandler = new Handler() {
        public void handleMessage(Message msg) {
            mIsLoading = false;
            Responsor responsor = (Responsor) msg.obj;
            JSONObject obj = responsor.obj;
            boolean tResultOK = mMessageFilter.process(msg);
            switch (msg.what) {
                case AlipayHandlerMessageIDs.ACCOUNT_QUERY_TRADLIST:
                    if (tResultOK) {
                        ParseTransList(obj);
                        if (mRequestPage == 1) {
                        	mListView.setSelection(0);
        				}
                    } else {// 获取信息失败
                        if (mRequestPage == 1) {
                            if (mPullRefresh) {
                                mRefreshView.refreshFinished();
                                mPullRefresh = false;
                            }
                        }
                        mListAdapter.getMorefailed();
                        mRequestPage = mCurrentPage;
                    }
                    break;
                default:
                    break;
            }
            super.handleMessage(msg);
        }
    };

    private class InfoItem {
        String tFlowNumber = "";
        String tType = "";
        String tMoney = "";
        String tSite = "";
        String tTime = "";
        String tSimpleTime = "";
        String tNote = "";
        String tBalance = "";
    }

    //设置tab的背景
    Field mBottomLeftStrip;
    Field mBottomRightStrip;

    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (LephoneConstant.isLephone()) {
            getWindow().addFlags(LephoneConstant.FLAG_ROCKET_MENU_NOTIFY);
        }

        type = THISMONTH;
        mDatas = new ArrayList<InfoItem>();
        mTradeTypes = getResources().getStringArray(R.array.accountDetail);
        mMessageFilter = new MessageFilter(this);
        mCurrentPage = -1;
        mCount = -1;
        mRefresh = true;

        setContentView(R.layout.alipay_accountquery_320_480_new);
        loadAllVariables();
    }

    @Override
    protected void onResume() {
        if (mRefresh) {
            mDatas.clear();
            mListAdapter.notifyDataSetChanged();
            mCurrentPage = -1;
            mCount = -1;
            refresh();
            mRefresh = false;
        }
        super.onResume();
    }

    private void loadAllVariables() {
        mTitleView = (TextView) findViewById(R.id.title_text);
        mTitleView.setText(mTradeTypes[type]);
        Drawable drawable = getResources().getDrawable(R.drawable.title_dropdown_icon);
        drawable.setBounds(0, 0, drawable.getMinimumWidth(), drawable.getMinimumHeight());
        mTitleView.setCompoundDrawables(null, null, drawable, null);
        mTitleView.setCompoundDrawablePadding(5);
        mTitleView.setClickable(true);
        mTitleView.setOnClickListener(this);
        ImageButton titleBtn = (ImageButton) findViewById(R.id.title_right);
        titleBtn.setVisibility(View.VISIBLE);
        titleBtn.setBackgroundResource(R.drawable.tradeicon_status);
        titleBtn.setOnClickListener(this);

        mRefreshView = (PullRefreshView) findViewById(R.id.pull);
        mRefreshView.setRefreshListener(mRefreshListener);
        mRefreshView.setEnablePull(true);
        mEmptyView = (TextView) findViewById(R.id.EmptyCanvas);
        mListView = (ListView) findViewById(R.id.ListViewCanvas);
        mListAdapter = new DealListAdapter(mListView,this, mDatas);
        mListAdapter.setMoreListener(mMoreListener);
        mListView.setAdapter(mListAdapter);
        mListView.setOnItemClickListener(mListAdapter);
        mListView.setOnScrollListener(listScrollListener);
    }

    private InfoItem makeItem(JSONObject obj) {
        InfoItem tItem = new InfoItem();

        tItem.tFlowNumber = obj.optString(Constant.RPF_TRANSLOGID);
        tItem.tMoney = obj.optString(Constant.RPF_MONEY);
        tItem.tNote = obj.optString(Constant.RPF_TRANSMEMO);
        tItem.tBalance = obj.optString(Constant.RPF_BALANCE);
        tItem.tSite = obj.optString(Constant.RPF_TRANSINSTITUTION);
        tItem.tTime = obj.optString(Constant.RPF_DATE);
        tItem.tSimpleTime = obj.optString(Constant.RPF_SIMPLEDATE);
        tItem.tType = obj.optString(Constant.RPF_TRANSTYPE);
        return tItem;
    }

    private void ParseTransList(JSONObject obj) {
        JSONArray arrayObj;
        JSONObject itemObj;

        mTotalPage = Integer.valueOf(obj.optString(Constant.RPF_PAGECOUNT));
        mCurrentPage = Integer.valueOf(obj.optString(Constant.RPF_PAGE));
        mTotalCount = Integer.valueOf(obj.optString(Constant.RPF_TOTALCOUNT));

        if (mRequestPage == 1) {//第一页
            mCount = 0;
            mDatas.clear();
        }

        if (mTotalCount == 0) {
            mEmptyView.setText(getResources().getText(R.string.HaveNoBalanceRecords));
            mListView.setEmptyView(mEmptyView);
            return;
        }

        try {
            arrayObj = obj.getJSONArray(Constant.RPF_TABLELIST);
            for (int i = 0; i < arrayObj.length(); i++) {
                itemObj = arrayObj.getJSONObject(i);
                mDatas.add(makeItem(itemObj));
                mCount++;
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        if (mCount > 0) {
            mListAdapter.notifyDataSetChanged();
        }

        if (mRequestPage == 1) {
            if (mPullRefresh) {
                mRefreshView.refreshFinished();
                mPullRefresh = false;
            }
            mListAdapter.reset();
        }
    }

    private void showHistoryInfoDetail(InfoItem tItem) {
        Intent intent = new Intent(this, SubItemAccountDetailInfoActivity.class);
        intent.putExtra(Constant.RPF_TRANSLOGID, tItem.tFlowNumber);
        intent.putExtra(Constant.RPF_TRANSTYPE, tItem.tType);
        intent.putExtra(Constant.RPF_MONEY, tItem.tMoney);
        intent.putExtra(Constant.RPF_TRANSINSTITUTION, tItem.tSite);
        intent.putExtra(Constant.RPF_BALANCE, tItem.tBalance);
        intent.putExtra(Constant.RPF_DATE, tItem.tTime);
        intent.putExtra(Constant.RPF_TRANSMEMO, tItem.tNote);

        //startActivity(intent);
        this.startActivity(intent);
    }

    private OnScrollListener listScrollListener = new OnScrollListener() {
        private boolean willLoad;;

        @Override
        public void onScrollStateChanged(AbsListView view, int scrollState) {
            if (willLoad && scrollState == SCROLL_STATE_IDLE) {
                willLoad = false;
                nextPage();
            }
        }

        @Override
        public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount,
                             int totalItemCount) {
            if (mListAdapter.hasMore() && firstVisibleItem + visibleItemCount >= totalItemCount) {
                willLoad = true;
            }
        }
    };

    private class DealListAdapter extends com.alipay.android.comon.component.ScrollMoreListAdapter {
        private class InfoItemView {
            ImageView icon;
            TextView data;
            TextView money;
            TextView note;
            TextView more;
            TextView balance;
            ImageView listDivide;
			View listEnd;
        }

        public DealListAdapter(ListView listView, Context context, ArrayList<?> data) {
            super(listView,context, data);
        }

        @Override
        public View getItemView(int position, View convertView, ViewGroup arg2) {
            final InfoItemView tInfo;
            if (convertView == null) {
                tInfo = new InfoItemView();
                convertView = LayoutInflater.from(mContext).inflate(
                    R.layout.alipay_detailinfoitem_320_480, null);
                tInfo.icon = (ImageView) convertView.findViewById(R.id.DetailInfoItemIcon);
                tInfo.data = (TextView) convertView.findViewById(R.id.DetailInfoItemLowerLine1);
                tInfo.money = (TextView) convertView.findViewById(R.id.DetailInfoItemRightCenter);
                tInfo.note = (TextView) convertView.findViewById(R.id.DetailInfoItemUpperLine1);
                tInfo.balance = (TextView) convertView.findViewById(R.id.accountBalance);
                tInfo.more = (TextView) convertView.findViewById(R.id.DealMore);
                tInfo.listDivide = (ImageView)convertView.findViewById(R.id.ShouzhiListDivide);
                tInfo.listEnd = convertView.findViewById(R.id.ListEnd);
                convertView.setTag(tInfo);
            } else {
                tInfo = (InfoItemView) convertView.getTag();
            }
            tInfo.icon.setVisibility(View.VISIBLE);
            tInfo.data.setText(mDatas.get(position).tSimpleTime);
            String tStr = mDatas.get(position).tMoney;
            if(position == 0){
            	//addDivideAtStart(convertView);
            }else{
            	removeDivide(convertView);
            }
            
            if(tInfo.listDivide != null){
            	BaseHelper.fixBackgroundRepeat(tInfo.listDivide);
            }
            
            tInfo.listEnd.setVisibility(View.GONE);
			// if(position == getCount()-1){
			// tInfo.listEnd.setVisibility(View.VISIBLE);
			// BaseHelper.fixBackgroundRepeat(tInfo.listEnd);
			// }else{
			// tInfo.listEnd.setVisibility(View.GONE);
			// }
            if (tStr.indexOf("-") != -1) {
                tInfo.money.setText(Html.fromHtml("<font color='#ff7700'>- " + tStr.substring(1)
                                                  + "</font>"));
            } else {
                tInfo.money.setText(Html.fromHtml("<font color='#449900'>+ " + tStr + "</font>"));
            }
            tInfo.balance.setText(getText(R.string.balance) + mDatas.get(position).tBalance);
            tInfo.note.setText(mDatas.get(position).tType);
            tInfo.more.setVisibility(View.GONE);

            return convertView;
        }

        @Override
        protected boolean hasMore() {
            return mCurrentPage < mTotalPage && mCount < mTotalCount;
        }

        @Override
        protected void itemClick(AdapterView<?> arg0, View arg1, int position, long arg3) {
            diaplayItemInfo((InfoItem) mListDatas.get(position));
        }
        
    }

    /**
     * 打开详情
     */
    private void diaplayItemInfo(InfoItem info) {
        showHistoryInfoDetail(info);
    }

    private MoreListener mMoreListener = new MoreListener() {
        @Override
        public void onMore() {
            nextPage();
        }
    };

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.title_text:
                showPop();
                break;
            case R.id.title_right:
            	finish();
//            	overridePendingTransition(android.R.anim.slide_in_left, android.R.anim.slide_out_right);
                break;

            default:
                break;
        }
    }
    
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
    	if(keyCode == KeyEvent.KEYCODE_BACK){
    		finish();
        	overridePendingTransition(android.R.anim.slide_in_left, android.R.anim.slide_out_right);
    	}
    	return super.onKeyDown(keyCode, event);
    }
    

    private void showPop() {
        if (mPopupWindow != null && mPopupWindow.isShowing()) {
            mPopupWindow.dismiss();
            return;
        }
        ListView types = new ListView(this);
        ArrayList<HashMap<String, String>> menus = new ArrayList<HashMap<String, String>>();
        for (String str : mTradeTypes) {
            HashMap<String, String> map = new HashMap<String, String>();
            map.put("name", str);
            menus.add(map);
        }

        types.setAdapter(new SimpleAdapter(this, menus, R.layout.shouzhi_pop,
            new String[] { "name" }, new int[] { android.R.id.text1 }));
        types.setCacheColorHint(Color.TRANSPARENT);
        types.setDividerHeight(0);
        types.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1, int position, long arg3) {
                type = position;
                mTitleView.setText(mTradeTypes[type]);
                mDatas.clear();
                mListAdapter.notifyDataSetChanged();
                mCurrentPage = -1;
                mCount = -1;
                refresh();
                mPopupWindow.dismiss();
            }
        });
        float scale = getResources().getDisplayMetrics().density;
        int w = (int) (175*scale);
        mPopupWindow = new PopupWindow(types, w, LayoutParams.WRAP_CONTENT);
        mPopupWindow.setBackgroundDrawable(getResources().getDrawable(R.drawable.records_pop_bg));
        mPopupWindow.setOutsideTouchable(true);
        mPopupWindow.setFocusable(true);
        mPopupWindow.showAsDropDown(mTitleView, (-mPopupWindow.getWidth()+ mTitleView.getMeasuredWidth()) / 2, 0);
    }

    /**
     * 下一页
     */
    private void nextPage() {
        if (mIsLoading)
            return;
        if (mCurrentPage < mTotalPage && mCount < mTotalCount) {
            if (mCurrentPage == -1)
                mRequestPage = 1;
            else
                mRequestPage = mCurrentPage + 1;
        } else {
            return;
        }
        getData();
    }

    private void refresh() {//请求第一页
        mRequestPage = 1;
        getData();
    }

    private PullRefreshView.RefreshListener mRefreshListener = new PullRefreshView.RefreshListener() {
        @Override
        public void onRefresh() {
            mPullRefresh = true;
            refresh();
        }
    };

    private void getData() {
        String tTimeRange = "1m";
        switch (type) {
            case THISWEEK:
                tTimeRange = "1w";
                break;
            case THISMONTH:
                tTimeRange = "1m";
                break;
            default:
                tTimeRange = "1d";
                break;
        }
        mIsLoading = true;
        getDataHelper().sendQueryTransList(mHandler,
            AlipayHandlerMessageIDs.ACCOUNT_QUERY_TRADLIST, tTimeRange, mRequestPage + "",
            pageCount + "");
    }
}
