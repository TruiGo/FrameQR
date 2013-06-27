package com.alipay.android.ui.voucher;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import org.json.JSONArray;
import org.json.JSONObject;

import android.content.Context;
import android.net.Uri;
import android.os.AsyncTask;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.alipay.android.appHall.Helper;
import com.alipay.android.appHall.appManager.AppItemInfo;
import com.alipay.android.appHall.appManager.HallData;
import com.alipay.android.biz.AlipayClientPubBiz;
import com.alipay.android.biz.CommonRespHandler;
import com.alipay.android.biz.VoucherBiz;
import com.alipay.android.client.constant.Constant;
import com.alipay.android.common.data.UserData;
import com.alipay.android.comon.component.PullRefreshView;
import com.alipay.android.comon.component.ScrollMoreListAdapter;
import com.alipay.android.nfd.LbsLocation;
import com.alipay.android.nfd.LbsRequestParams;
import com.alipay.android.servicebeans.GetStoreList;
import com.alipay.android.servicebeans.GetTaobaoSid;
import com.alipay.android.ui.bean.Store;
import com.alipay.android.ui.bean.VoucherDetail;
import com.alipay.android.ui.framework.BaseViewController;
import com.alipay.android.util.Base64;
import com.alipay.android.util.JsonConvert;
import com.eg.android.AlipayGphone.R;

public class StoreListViewController extends BaseViewController{
	private PullRefreshView mRefreshView;
	private List<Store> mStoreList;
	private int mPageCount = 20;
	private int mTotalPage = 0;
	private int mCurrentPage = 1;
	private BizAsyncTask loadTask;
	private String goodsId;
	private String voucherFrom;
	private String cityId;
	private ListView mStoreListView;
	private StoreListAdapter mStoreListAdapter;
	private VoucherDetail mVoucherDetail;
	private List<Store> mNearByStores = new ArrayList<Store>();
	private String detailUrl;
	private TextView mStoreTitle;
	
	@Override
	protected void onCreate() {
		mView  = LayoutInflater.from(getRootController()).inflate(R.layout.store_list, null,false); 
		
		if(params != null){
			mVoucherDetail = (VoucherDetail) params;
			goodsId = mVoucherDetail.getGoodsId();
			voucherFrom = mVoucherDetail.getVoucherFrom();
		}
		
		addView(mView, null);
		
		loadAllVariables();
		
		if(mVoucherDetail != null)
			queryStoreList(mCurrentPage, mPageCount);
	}

	private void loadAllVariables() {
		mStoreTitle = (TextView) mView.findViewById(R.id.title_text);
		mStoreTitle.setText(mVoucherDetail.getStoreListTitle());
		mStoreListView = (ListView) mView.findViewById(R.id.storeListView);
		mStoreList = new ArrayList<Store>();
		mStoreListAdapter = new StoreListAdapter(mStoreListView, getRootController(), mStoreList);
		mStoreListView.setAdapter(mStoreListAdapter);
		mStoreListView.setOnItemClickListener(mStoreListAdapter);
		
		mRefreshView = (PullRefreshView) findViewById(R.id.pull);
        mRefreshView.setRefreshListener(mRefreshListener);
        mRefreshView.setEnablePull(false);
        mStoreListView.setOnScrollListener(listScrollListener);
        
        mStoreListAdapter.setMoreListener(new ScrollMoreListAdapter.MoreListener() {
			@Override
			public void onMore() {
				if(mStoreList.size() > 0)
					nextPage();
				else
					refresh();
			}
		});
	}
	
	private boolean mPullRefresh;
	private PullRefreshView.RefreshListener mRefreshListener = new PullRefreshView.RefreshListener() {
        @Override
        public void onRefresh() {
            mPullRefresh = true;
            refresh();
        }
    };
    
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
            if (mStoreListAdapter.hasMore() && firstVisibleItem + visibleItemCount >= totalItemCount) {
                willLoad = true;
            }
        }
    };
    
    private void nextPage() {
    	mCurrentPage++;
        queryStoreList(mCurrentPage, mPageCount);
	}
    
    private void refresh() {//请求第一页
        mCurrentPage = 1;
        if(mNearByStores != null)
        	mNearByStores.clear();
        queryStoreList(mCurrentPage,mPageCount);
    }

	@Override
	protected Object doInBackground(String bizType, String... params) {
		if(GetStoreList.BIZ_TAG.equals(bizType)){
			LbsLocation lbsLocation = new LbsLocation(getRootController());
			LbsRequestParams lsbParam = lbsLocation.getCurrentPositionInfo();
			JSONObject storeListParam = lsbParam.getRequestJsonParams();
			return new VoucherBiz().getStoreList(params[0],params[1],params[2],params[3],params[4],params[5],storeListParam.toString());
		} else if(GetTaobaoSid.BIZ_GETTAOBAOSID.equals(bizType)){
			return new AlipayClientPubBiz().getTaobaoSid();
		}
		return super.doInBackground(bizType, params);
	}
	
	@Override
	protected void onPostExecute(String bizType, Object result) {
		if(GetStoreList.BIZ_TAG.equals(bizType)){
			JSONObject responseJson = JsonConvert.convertString2Json((String)result);
			
			if(CommonRespHandler.filterBizResponse(getRootController(), responseJson)){
				if (mCurrentPage == 1) {
					mStoreList.clear();
				}
				List<Store> allStoreList = new ArrayList<Store>();
				List<Store> nearByStoreList = new ArrayList<Store>();
				
				JSONArray nearbyArray = responseJson.optJSONArray("nearbyList");
				if(nearbyArray != null && nearbyArray.length() > 0){
					nearByStoreList.addAll(JsonConvert.jArry2BeanList(nearbyArray, new Store()));
					mNearByStores.addAll(JsonConvert.jArry2BeanList(nearbyArray, new Store()));
				}else
					allStoreList.addAll(JsonConvert.jArry2BeanList(responseJson.optJSONArray("storeList"), new Store()));
				mTotalPage = responseJson.optInt("totalPage");
				
				if(nearByStoreList.size() > 0){
					mStoreList.addAll(nearByStoreList);
					mStoreListAdapter.notifyDataSetChanged();
					mRefreshView.refreshFinished();
					if(nearByStoreList.size() < mPageCount && mCurrentPage < mTotalPage){
						nextPage();
					}
				}else if(nearByStoreList.size() == 0 && allStoreList.size() > 0){
					mStoreList.addAll(removeSameStore(allStoreList));
					mStoreListAdapter.notifyDataSetChanged();
					mRefreshView.refreshFinished();
				}
			}else{
				 if (mCurrentPage == 1) {
                     if (mPullRefresh) {
                         mRefreshView.refreshFinished();
                         mPullRefresh = false;
                     }
                 }
                 mStoreListAdapter.getMorefailed();
			}
		}else if(GetTaobaoSid.BIZ_GETTAOBAOSID.equals(bizType)){
			JSONObject responseJson = JsonConvert.convertString2Json((String)result);
			
			if(CommonRespHandler.getStatus(responseJson) == 100){
				String taobaoSid = responseJson.optString(Constant.RPF_TAOBAOSID);
				if(taobaoSid != null)
					getRootController().getUserData().setTaobaoSid(taobaoSid);
				
				jump2StoreWap(taobaoSid);
			}
		}
		super.onPostExecute(bizType, result);
	}
	
	/**
	 * 移除相同的Store对象
	 */
	private List<Store> removeSameStore(List<Store> storeList) {
		CopyOnWriteArrayList<Store> stores = new CopyOnWriteArrayList<Store>(storeList);
		for(Store store : mNearByStores){
			for(Store curStore : stores){
				if(store.equals(curStore)){
					stores.remove(curStore);
					break;
				}
			}
		}
		return stores;
	}

	/**
	 * 查询商户列表
	 * @param currentPage
	 * @param pageCount
	 */
	private void queryStoreList(int currentPage,int pageCount){
		if(loadTask != null && loadTask.getStatus() != AsyncTask.Status.FINISHED)
			return;
		loadTask = new BizAsyncTask(GetStoreList.BIZ_TAG,false);
		
		String queryType = "allstore";
		if(currentPage == 1){
			queryType = "nearby";
		}
		
		loadTask.execute(goodsId,cityId,voucherFrom,currentPage + "",pageCount + "",queryType);
	}
	
	private class StoreListAdapter extends com.alipay.android.comon.component.ScrollMoreListAdapter{
		private LayoutInflater mLayoutInflater;
    	
        public StoreListAdapter(ListView listView, Context context, List<Store> data) {
            super(listView, context, data);
            mLayoutInflater = LayoutInflater.from(context);
        }

        @Override
        public View getItemView(int position, View convertView, ViewGroup arg2) {
        	ViewHolder viewHolder;
    		if(convertView == null){
    			convertView = mLayoutInflater.inflate(R.layout.store_item, null);
    			viewHolder = new ViewHolder();
    			viewHolder.nameText = (TextView) convertView.findViewById(R.id.storeName);
    			viewHolder.addressText = (TextView) convertView.findViewById(R.id.storeAddress);
    			viewHolder.phoneText = (TextView) convertView.findViewById(R.id.storePhone);
    			viewHolder.indicatorImage = (ImageView) convertView.findViewById(R.id.indicator);
    			convertView.setTag(viewHolder);
			} else {
    			viewHolder =   (ViewHolder) convertView.getTag();
    		}
    		
    		int size = mListDatas.size();
    		if(position == 0 && size != 1){ //动态设置背景图片
    			convertView.setBackgroundResource(R.drawable.list_btn_top);
    		}else if(size != 1 && position == size -1){
    			convertView.setBackgroundResource(R.drawable.list_btn_f);
    		}else{
    			convertView.setBackgroundResource(R.drawable.list_btn_m);
    		}
    		
    		Store store = (Store) mListDatas.get(position);
    		viewHolder.nameText.setText(Helper.toDBC(store.getStoreName()));
    		viewHolder.addressText.setText(Helper.toDBC(store.getStoreAddress()));
    		String mobilePhone;
    		String phone = store.getStorePhone();//电话
    		String mobile = store.getStoreMobile();//手机
    		if(mobile != null && !"".equals(mobile)){
    			mobilePhone = mobile;
    		}else{
    			mobilePhone = phone;
    		}
    		viewHolder.phoneText.setText(Helper.toDBC("电话:"+mobilePhone));
    		if(mobilePhone == null || "".equals(mobilePhone) || "null".equals(mobilePhone))
    			viewHolder.phoneText.setVisibility(View.INVISIBLE);
    		else 
    			viewHolder.phoneText.setVisibility(View.VISIBLE);
    		
    		viewHolder.indicatorImage.setVisibility(View.VISIBLE);
    		String detailUrl = store.getStoreDetailUrl();
			if(detailUrl == null || "".equals(detailUrl)){
				viewHolder.indicatorImage.setVisibility(View.GONE);
				convertView.setEnabled(false);
			} else {
				convertView.setEnabled(true);
				viewHolder.indicatorImage.setVisibility(View.VISIBLE);
			}
    		
    		return convertView;
        }

        @Override
        protected boolean hasMore() {
        	return mCurrentPage < mTotalPage;
        }

        @Override
        protected void itemClick(AdapterView<?> arg0, View arg1, int position, long arg3) {
        	Store store = (Store) mStoreListAdapter.getItem(position);
        	detailUrl = store.getStoreDetailUrl();
			if(detailUrl == null || "".equals(detailUrl)){
				return;
			}
        	
        	UserData userData = getRootController().getUserData();
			String taobaoSid = userData.getTaobaoSid();
			if (!"".equals(taobaoSid)) {
				jump2StoreWap(taobaoSid);
			} else {
				getTaobaoSid();
			}
        }
    }
	
	private BizAsyncTask bizTaoSidTask;
	private void getTaobaoSid(){
		if(bizTaoSidTask != null && bizTaoSidTask.getStatus() != AsyncTask.Status.FINISHED)
			return;
		bizTaoSidTask = new BizAsyncTask(GetTaobaoSid.BIZ_GETTAOBAOSID);
		bizTaoSidTask.execute();
	}

	private void jump2StoreWap(String taobaoSid) {
		if(detailUrl != null && !"".equals(detailUrl)){
    		Uri uri = Uri.parse(detailUrl);
			String query = uri.getQuery();
			if(query == null){
				detailUrl += "?ttid=600351@zhifubao_android_4.0.0";
			}else{
				detailUrl += "&ttid=600351@zhifubao_android_4.0.0";
			}
			
			UserData userData = getRootController().getUserData();
			if(userData != null)
				detailUrl = detailUrl + "&sid="+userData.getTaobaoSid();
			
			AppItemInfo info = getRootController().getHallData().getAppConfig(HallData.COUPON_MARKET);
			String params = "entry="+Base64.encode(detailUrl.getBytes());
			if(info != null){
				info.onClick(getRootController(), params, AppItemInfo.FROM_APPCTETER);
			}
    	}
	}
	
	class ViewHolder{
		TextView nameText;
		TextView addressText;
		TextView phoneText;
		ImageView indicatorImage;
	}
}