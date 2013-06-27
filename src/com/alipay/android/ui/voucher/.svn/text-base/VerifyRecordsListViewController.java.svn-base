package com.alipay.android.ui.voucher;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONObject;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import com.alipay.android.biz.CommonRespHandler;
import com.alipay.android.biz.VoucherBiz;
import com.alipay.android.comon.component.PullRefreshView;
import com.alipay.android.comon.component.ScrollMoreListAdapter;
import com.alipay.android.servicebeans.GetVerifyRecordList;
import com.alipay.android.ui.bean.VerifyRecord;
import com.alipay.android.ui.framework.BaseViewController;
import com.alipay.android.util.JsonConvert;
import com.eg.android.AlipayGphone.R;

public class VerifyRecordsListViewController extends BaseViewController{
	private ListView mVerifyListView;
	private VerifyListAdapter mVerifyListAdapter;
	private VerifyRecordData verifyRecordData;
//	private TextView mCanUsedTimes;
//	private TextView mUsedTimes;
//	private TextView mTotalTimes;
	private PullRefreshView mRefreshView;
	private List<VerifyRecord> mVerifyList = new ArrayList<VerifyRecord>();
	private LayoutInflater mLayoutInflater;
	private int mTotalPage;
	private int mCurrentPage = 1;
	private int mPageCount = 20;
	private String voucherFrom;
	private String voucherId;
	private String outBizNo;
	private BizAsyncTask queryVerifyTask;
	
	
	@Override
	protected void onCreate() {
		mView = LayoutInflater.from(getRootController()).inflate(R.layout.verifyrecord_list, null,false);
		addView(mView, null);
		
		if(params != null){
			verifyRecordData = (VerifyRecordData) params;
			voucherId = verifyRecordData.getVoucherId();
			voucherFrom = verifyRecordData.getVoucherFrom();
			outBizNo = verifyRecordData.getOutBizNo();
		}
		loadAllVariables();
		
		queryVerifyList(mCurrentPage, mPageCount);
	}
	
	private boolean mPullRefresh;
	private PullRefreshView.RefreshListener mRefreshListener = new PullRefreshView.RefreshListener() {
        @Override
        public void onRefresh() {
            mPullRefresh = true;
            refresh();
        }
    };
	
    private void refresh() {
    	mCurrentPage = 1;
    	queryVerifyList(mCurrentPage, mPageCount);
    }
    
	private void loadAllVariables() {
		mLayoutInflater = LayoutInflater.from(getRootController());
		
		mVerifyListView = (ListView) mView.findViewById(R.id.verifyListView);
		mVerifyListAdapter = new VerifyListAdapter(mVerifyListView, getRootController(), mVerifyList);
		mVerifyListView.setAdapter(mVerifyListAdapter);
		
		mRefreshView = (PullRefreshView) findViewById(R.id.pull);
		mRefreshView.setRefreshListener(mRefreshListener);
		mRefreshView.setEnablePull(false);
		mVerifyListView.setOnScrollListener(listScrollListener);
		
		mVerifyListAdapter.setMoreListener(new ScrollMoreListAdapter.MoreListener() {
			@Override
			public void onMore() {
				if(mVerifyList.size() > 0)
					nextPage();
				else
					refresh();
			}
		});
	}
	
	@Override
	protected Object doInBackground(String bizType, String... params) {
		if(GetVerifyRecordList.BIZ_TAG.equals(bizType)){
			return new VoucherBiz().getVerifyRecordList(params[0], params[1], params[2], params[3], params[4]);
		}
		return super.doInBackground(bizType, params);
	}
	
	@Override
	protected void onPostExecute(String bizType, Object result) {
		if(GetVerifyRecordList.BIZ_TAG.equals(bizType)){
			JSONObject responseJson = JsonConvert.convertString2Json((String)result);
			if(CommonRespHandler.filterBizResponse(getRootController(), responseJson)){
				if(mCurrentPage == 1){//缓存第一页第一张券信息
					mVerifyList.clear();
				}
				JSONArray verifyRecordArray = responseJson.optJSONArray("verifyRecordList");
				List<VerifyRecord> verifyRecordList = JsonConvert.jArry2BeanList(verifyRecordArray, new VerifyRecord());
				if(verifyRecordList != null)
					mVerifyList.addAll(verifyRecordList);
				
				mTotalPage = responseJson.optInt("totalPage");
				mVerifyListAdapter.notifyDataSetChanged();
				mRefreshView.refreshFinished();
			}else{
				 if (mCurrentPage == 1) {
                     if (mPullRefresh) {
                         mRefreshView.refreshFinished();
                         mPullRefresh = false;
                     }
                 }
				 mVerifyListAdapter.getMorefailed();
			}
		}
		super.onPostExecute(bizType, result);
	}
	
	private class VerifyListAdapter extends com.alipay.android.comon.component.ScrollMoreListAdapter{
        public VerifyListAdapter(ListView listView, Context context, List<VerifyRecord> data) {
            super(mVerifyListView, context, data);
        }

        @Override
        public View getItemView(int position, View convertView, ViewGroup arg2) {
        	ViewHolder viewHolder;
    		if(convertView == null){
    			convertView = mLayoutInflater.inflate(R.layout.verifyrecord_item, null);
    			viewHolder = new ViewHolder();
    			viewHolder.verifyAddressText = (TextView) convertView.findViewById(R.id.verifyAddress);
    			viewHolder.verifyCountText = (TextView) convertView.findViewById(R.id.verifyCount);
    			viewHolder.verifyTimeText = (TextView) convertView.findViewById(R.id.verifyTime);
    			
    			convertView.setTag(viewHolder);
    		}else{
    			viewHolder =   (ViewHolder) convertView.getTag();
    		}
    		VerifyRecord verifyRecord = (VerifyRecord) mListDatas.get(position);
    		
    		String verifyAddress = verifyRecord.getAddress();
    		if(verifyAddress == null || "".equals(verifyAddress)){
    			viewHolder.verifyAddressText.setVisibility(View.GONE);
    		}else{
    			viewHolder.verifyAddressText.setText(verifyAddress);
    		}
    		viewHolder.verifyCountText.setText(verifyRecord.getVerifyCount()+"份");
    		viewHolder.verifyTimeText.setText(verifyRecord.getVerifyTime());
    		
    		if(getCount() > 1){
    			if(position == 0){ //动态设置背景图片
    				convertView.setBackgroundResource(R.drawable.list_btn_top_normal);
    			}else if(position == mListDatas.size() -1){
    				convertView.setBackgroundResource(R.drawable.list_btn_f_normal);
    			}else{
    				convertView.setBackgroundResource(R.drawable.list_btn_m_normal);
    			}
    		}else{
    			convertView.setBackgroundResource(R.drawable.list_btn_single_normal);
    		}
    		
    		return convertView;
        }

        @Override
        protected boolean hasMore() {
        	return mCurrentPage < mTotalPage;
        }

		@Override
		protected void itemClick(AdapterView<?> arg0, View arg1, int position, long arg3) {
		}
    }
	
	class ViewHolder{
		TextView verifyAddressText;
		TextView verifyCountText;
		TextView verifyTimeText;
	}
	
	private OnScrollListener listScrollListener = new OnScrollListener() {
        private boolean willLoad;

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
            if (mVerifyListAdapter.hasMore() && firstVisibleItem + visibleItemCount >= totalItemCount) {
                willLoad = true;
            }
        }
    };
    
    private void nextPage() {
    	mCurrentPage++;
    	queryVerifyList(mCurrentPage,mPageCount);
	}
    
    private void queryVerifyList(int currentPage,int pageCount){
    	if(queryVerifyTask != null && queryVerifyTask.getStatus() != BizAsyncTask.Status.FINISHED)
    		return;
    	queryVerifyTask = new BizAsyncTask(GetVerifyRecordList.BIZ_TAG,false);
    	queryVerifyTask.execute(voucherId,voucherFrom,outBizNo,currentPage +"",pageCount+"");
    }
}
