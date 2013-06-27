package com.alipay.android.ui.voucher;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Observable;
import java.util.Observer;
import java.util.Set;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.text.TextUtils.TruncateAt;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.LayoutAnimationController;
import android.view.animation.TranslateAnimation;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.FrameLayout.LayoutParams;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import com.alipay.android.appHall.Helper;
import com.alipay.android.appHall.appManager.AppItemInfo;
import com.alipay.android.appHall.appManager.HallData;
import com.alipay.android.appHall.uiengine.AnimRelativeLayout;
import com.alipay.android.biz.CommonRespHandler;
import com.alipay.android.biz.VoucherBiz;
import com.alipay.android.client.AlipayApplication;
import com.alipay.android.client.AnimationData;
import com.alipay.android.client.constant.Constant;
import com.alipay.android.client.util.AlipayDataStore;
import com.alipay.android.common.data.UserData;
import com.alipay.android.common.data.UserData.UDVoucherInfo;
import com.alipay.android.comon.component.ProgressDiv;
import com.alipay.android.comon.component.PullRefreshView;
import com.alipay.android.comon.component.ScrollMoreListAdapter;
import com.alipay.android.log.AlipayLogAgent;
import com.alipay.android.log.Constants.BehaviourID;
import com.alipay.android.longlink.LongLinkServiceManager;
import com.alipay.android.longlink.PayResultHanlder;
import com.alipay.android.serviceImpl.AliPassServiceImpl;
import com.alipay.android.servicebeans.CancelTrade;
import com.alipay.android.ui.bean.Voucher;
import com.alipay.android.ui.framework.BaseViewController;
import com.alipay.android.ui.widget.PagedHorizontalLayout;
import com.alipay.android.util.JsonConvert;
import com.eg.android.AlipayGphone.R;

public class VoucherListViewController extends BaseViewController implements OnClickListener,PayResultHanlder,VerifyResultNotifier,Observer{
	private final static String BIZTYPE_QUERYVOUCHERLIST = "biztype_queryVoucherlist";
	private final String AVAILABLE = "available";
	private final String UNAVAILABLE = "unAvailable";
	
	private BizAsyncTask loadTask;
	private VoucherListAdapter mVoucherListAdapter;
	private LongLinkServiceManager longLinkServiceManager;
	private PullRefreshView mRefreshView;
	private LayoutInflater mLayoutInflater;
	private ImageButton mVoucherMaket;
	private TextView titleTextView;
	private int mPageCount = 20;
	private int mTotalPage = 1;
	private int mCurrentPage = 1;
	private String beginDate;
	private String endDate;
	private UserData userData;
	private AlipayApplication application;
	private HallData hallData;
	private ProgressDiv progressDiv;
	
	private List<Voucher> mVoucherDatas = new ArrayList<Voucher>();
	private final int TODETAILRESULT = 111; 
	private ViewGroup mListContainer;
	private String from;
	private Animation voucherLayoutAnimation;
	private PagedHorizontalLayout tabViewLayout;
	private Button mSwitchUnAvailable;
	private Button mSwitchAvailable;
	
	private PopupWindow guidePopupWindow;
    AlipayDataStore mAlipayDataStore;
	private ListView mCurrentListView;
	private ListView mAvaiVoucherListView;
	private ListView mUnAvaiVoucherListView;
	private PullRefreshView mUnRefreshView;
    AliPassServiceImpl mAliPassServiceImpl;
    List<Voucher> mMixedVoucherList = new ArrayList<Voucher>();
    List<Voucher> voucherList = new ArrayList<Voucher>();
    String currentStauts;
	private View mNoUnAvailableView;
	private View mNoAvailableView;
    
	@Override
	protected void onCreate() {
		super.onCreate();
        mView = LayoutInflater.from(this.getRootController()).inflate(R.layout.voucher_list, null, false);
        
        getRootController().setContentView(mView);
		loadAllVariables();
		initListener();
		
		from = getRootController().getIntent().getStringExtra("pageFrom");
		mMixedVoucherList.addAll(mAliPassServiceImpl.mixLocalAndOnlineVoucher(userData.getAccountName(),getCachedVoucher()));
		if(animationData != null&& "MainPage".equals(from)){
			int currentStatus = userData.getCurrentRequestStatus();
			
			if(currentStatus == UserData.REQUEST_OK){
				populateUI(mMixedVoucherList);
				if(mMixedVoucherList.size() > 0)
					showAnimation();
				else{
					populateUI(mMixedVoucherList);
					mCurrentListView.setVisibility(View.VISIBLE);
				}
			}else{
				dispalyVouchers();
			}
		}else {
			dispalyVouchers();
		}
	}
	
	private JSONArray getCachedVoucher() {
		JSONArray voucherArray = null;
		if(hasCachedVoucherData()){
			UDVoucherInfo voucherInfo = userData.getUDVoucherInfo();
			String voucherList = voucherInfo.mVoucherList;
			try {
				voucherArray = new JSONArray(voucherList);
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}
		return voucherArray;
	}
	
	private boolean loadFinish(){
		return userData.getCurrentRequestStatus() == UserData.REQUEST_OK;
	}
	
	private boolean isLoading(){
		return userData.getCurrentRequestStatus() == UserData.REQUEST_PROCESSING;
	}
	
	/**
     * 显示收支明细加强引导
     */
    private void showGuidePopupWindow() {
    	mAvaiVoucherListView.postDelayed(new Runnable() {
			@Override
			public void run() {
				if(needShowGuide()){
					Rect frame = new Rect();
					getRootController().getWindow().getDecorView().getWindowVisibleDisplayFrame(frame);
					final ImageView showGuide = new ImageView(getRootController());
					showGuide.setBackgroundResource(R.drawable.voucher_market_guide);
					guidePopupWindow = new PopupWindow(showGuide, LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT,false);
					guidePopupWindow.setBackgroundDrawable(getRootController().getResources().getDrawable(R.drawable.voucher_market_guide));
					guidePopupWindow.setOutsideTouchable(true);
					guidePopupWindow.setFocusable(true);
					
					guidePopupWindow.showAtLocation(getRootController().getWindow().getDecorView(), Gravity.TOP|Gravity.RIGHT, 0, frame.top);
					
					showGuide.setOnClickListener(new OnClickListener() {
						@Override
						public void onClick(View v) {
							hideGuide();
						}
					});
					guidePopupWindow.setOnDismissListener(new PopupWindow.OnDismissListener() {
						@Override
						public void onDismiss() {
							AlipayApplication application = (AlipayApplication) getRootController().getApplicationContext();
					    	mAlipayDataStore.putString(AlipayDataStore.VOUCHERLIST_VERSION, application.getConfigData().getProductVersion());
						}
					});
				}
			}
		}, 200);
	}
    
	/**
     * 是否显示收支明细引导
     * @param application
     * @return
     */
    private boolean needShowGuide(){
    	AlipayApplication application = (AlipayApplication) getRootController().getApplicationContext();
		return mAlipayDataStore.getString(AlipayDataStore.VOUCHERLIST_VERSION, "0.0.0").compareTo(application.getConfigData().getProductVersion()) < 0;
	}
    /**
     * 设置Version 不再显示引导
     */
    private void hideGuide(){
    	if(guidePopupWindow != null && guidePopupWindow.isShowing()){
			guidePopupWindow.dismiss();
			AlipayApplication application = (AlipayApplication) getRootController().getApplicationContext();
	    	mAlipayDataStore.putString(AlipayDataStore.VOUCHERLIST_VERSION, application.getConfigData().getProductVersion());
		}
    }

	private void dispalyVouchers() {
		if(isLoading()){
			userData.addObserver(this);
			showProgress();
			mVoucherListAdapter.removeFooterView();
		} else if(loadFinish()){
//			refreshPage();
			populateUI(mMixedVoucherList);
			mCurrentListView.setVisibility(View.VISIBLE);
		} else {
			queryVoucherList(mCurrentPage,mPageCount,beginDate,endDate);
		}
		
		showGuidePopupWindow();
	}
	
	/**
	 * 缓存中是否有券数据
	 * @return
	 */
	private boolean hasCachedVoucherData() {
		UDVoucherInfo voucherInfo = userData.getUDVoucherInfo();
		return voucherInfo != null && voucherInfo.mVoucherList != null && !"".equals(voucherInfo.mVoucherList)
				&& !"[]".equals(voucherInfo.mVoucherList);
	}
	
	@Override  
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if(keyCode == KeyEvent.KEYCODE_BACK && animationData !=  null
				&& "MainPage".equals(from) && hasVoucher()){
			if(voucherLayoutAnimation != null && !voucherLayoutAnimation.hasEnded())
				return true;
			hideAnimation();
			viewReturn();
			return true;
		}else if(keyCode == KeyEvent.KEYCODE_BACK){//不播放动画返回埋点
			viewReturn();
		}
		
		return super.onKeyDown(keyCode, event);
	}
	
	/**
	 * 存在券数据时才播放动画
	 */
	private boolean hasVoucher(){
		return (AVAILABLE.equals(currentStauts) && mMixedVoucherList.size() > 0) ||
				(UNAVAILABLE.equals(currentStauts) && voucherList.size() > 0);
	}
	
	private void initListener() {
		mVoucherMaket.setOnClickListener(this);
		longLinkServiceManager.registUserCallback(this,this);
		mSwitchUnAvailable.setOnClickListener(this);
    	mSwitchAvailable.setOnClickListener(this);
	}

	private void loadAllVariables() {
		mAliPassServiceImpl = new AliPassServiceImpl(getRootController());
		application = (AlipayApplication) getRootController().getApplication();
		mAlipayDataStore = new AlipayDataStore(getRootController());
		animationData = application.getAnimationData();
		currentStauts = AVAILABLE;
		
		mListContainer = (ViewGroup) findViewById(R.id.listContainer);
		
		userData = getRootController().getUserData();
		longLinkServiceManager = LongLinkServiceManager.getInstance(getRootController(),this);
		mAvaiVoucherListView = (ListView) mView.findViewById(R.id.avaiVoucherListView);
		mUnAvaiVoucherListView = (ListView) mView.findViewById(R.id.unAvaiVoucherListView);
		mCurrentListView = mAvaiVoucherListView;
		
		mLayoutInflater = LayoutInflater.from(getRootController());
		mVoucherMaket = (ImageButton) findViewById(R.id.title_right);
		mVoucherMaket.setImageResource(R.drawable.voucher_market);
		mVoucherMaket.setVisibility(View.VISIBLE);
		
		titleTextView = (TextView) findViewById(R.id.title_text);
		titleTextView.setText(R.string.myVoucher);
		mRefreshView = (PullRefreshView) findViewById(R.id.availablePull);
		mUnRefreshView = (PullRefreshView) findViewById(R.id.unAvailablePull);

		setListViewAdapter(mCurrentListView);
		hallData = application.getHallData();
		tabViewLayout = (PagedHorizontalLayout) mView.findViewById(R.id.tabViewLayout);
		
		mSwitchUnAvailable = (Button) mView
				.findViewById(R.id.switchUnAvailable);
		mSwitchAvailable = (Button) mView.findViewById(R.id.switchAvailable);
		mNoUnAvailableView = mView.findViewById(R.id.noUnAvailableView);
		mNoAvailableView = mView.findViewById(R.id.noAvailableView);
	}
	
	private void setListViewAdapter(ListView currentListview) {
		removeFooterView();
		mVoucherListAdapter = new VoucherListAdapter(currentListview, getRootController());
		mVoucherListAdapter.setMoreListener(new ScrollMoreListAdapter.MoreListener() {
			@Override
			public void onMore() {
				 queryVoucherList(mCurrentPage,mPageCount,beginDate,endDate);
			}
		});
		currentListview.setAdapter(mVoucherListAdapter);
		currentListview.setOnItemClickListener(mVoucherListAdapter);
		mVoucherListAdapter.reset();
		
		//可用
		mRefreshView.setRefreshListener(mRefreshListener);
        mRefreshView.setEnablePull(true);
        
        //不可用
        mUnRefreshView.setRefreshListener(mRefreshListener);
        mUnRefreshView.setEnablePull(true);
        currentListview.setOnScrollListener(listScrollListener);
	}
	
	private void removeFooterView() {
		if(mVoucherListAdapter != null)//初始化为新的Adapter之前先删除FooterView
			mVoucherListAdapter.removeFooterView();
	}

	private void setLayoutAni() {
		AnimationSet set = new AnimationSet(true);
        Animation animation = new AlphaAnimation(0.0f, 1.0f);
        animation.setDuration(60);
        set.addAnimation(animation);

        animation = new TranslateAnimation(
            Animation.RELATIVE_TO_SELF, 0.0f,Animation.RELATIVE_TO_SELF, 0.0f,
            Animation.RELATIVE_TO_SELF, -1.0f,Animation.RELATIVE_TO_SELF, 0.0f
        );
        animation.setDuration(100);
        set.addAnimation(animation);
        LayoutAnimationController controller = new LayoutAnimationController(set, 0.5f);
        mCurrentListView.setLayoutAnimation(controller);
	}
	
	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		if(requestCode == TODETAILRESULT && resultCode == Activity.RESULT_OK){
			refresh();
		}
	}
	
	@Override
	protected void onResume() {
        if(getRootController().topController()!=this)
            getRootController().setContentView(R.layout.voucher_list);
		if(longLinkServiceManager != null)
			longLinkServiceManager.registUserCallback(this,this);
		
		if(application.isVerifySuccess() || application.isSafePayCalled()){
			refresh();
			application.setVerifySuccess(false);
			application.setSafePayCalled(false);
		}
	}
	
	private void viewReturn(){
		AlipayLogAgent.writeLog(getRootController(), BehaviourID.CLICKED, null, null, "walletTicket", null, "walletHome", "myTicketList", "backIcon");
	}
	 
	@Override
	protected Object doInBackground(String bizType, String... params) {
		if(BIZTYPE_QUERYVOUCHERLIST.equals(bizType)){
			return new VoucherBiz().queryVoucherList(params[0], params[1], params[2],params[3],params[4]);
		}else if(CancelTrade.BIZTYPE.equals(bizType)){
			return new VoucherBiz().cancelTrade(params[0]);
		}
		return super.doInBackground(bizType, params);
	}
	
	@Override
	protected void onPostExecute(String bizType, Object result) {
		if(BIZTYPE_QUERYVOUCHERLIST.equals(bizType)){
			JSONObject responseJson = JsonConvert.convertString2Json((String)result);
			if(!CommonRespHandler.processSpecStatu(getRootController(), responseJson)){//是否继续
				if (mCurrentPage == 1) {//下拉刷新失败
                    if (mPullRefresh) {
                        mRefreshView.refreshFinished();
                        mUnRefreshView.refreshFinished();
                        mPullRefresh = false;
                    }
                }
				mVoucherListAdapter.getMorefailed();
				return;
			}
			
			if(CommonRespHandler.filterBizResponse(getRootController(), responseJson)){
				mTotalPage = responseJson.optInt("totalPage");
				JSONArray voucherArray = responseJson.optJSONArray("voucherList");
				voucherList.clear();
				if (mCurrentPage == 1 && AVAILABLE.equals(currentStauts)) {
					voucherList.addAll(mAliPassServiceImpl.mixLocalAndOnlineVoucher(userData.getAccountName(), voucherArray));
				} else {
					voucherList.addAll(JsonConvert.jArry2BeanList(voucherArray, new Voucher()));
				}
				
				populateUI(voucherList);
				mRefreshView.refreshFinished();
				mUnRefreshView.refreshFinished();
				
				if(voucherArray != null && voucherArray.length() > 0){//刷新后更新缓存中数据
					if(userData != null){
						UDVoucherInfo voucherInfo = userData.getUDVoucherInfo();
						if(voucherInfo != null && mCurrentPage == 1 && AVAILABLE.equals(currentStauts)){
							voucherInfo.mVoucherList = responseJson.optString("voucherList");
							try {
								if (voucherArray == null||voucherArray.getJSONObject(0)==null) {
									voucherInfo.has_no_voucher = true;
								}else{
									String status = voucherArray.getJSONObject(0).optString("status", "");
									if (!status.equals(Voucher.Status.CAN_USE)) {
										voucherInfo.only_cantUse_voucher = true;
									}
								}
							} catch (JSONException e) {
								e.printStackTrace();
							}
						}
					}
				}
	        	
				mVoucherListAdapter.removeFooterView();
			}
		}
		
		super.onPostExecute(bizType, result);
	}
	
	@Override
	public void onNewIntent() {
		super.onNewIntent();
		refresh();
	}

	private void populateUI(List<Voucher> voucherList) {
		if(voucherList.size() == 0){
			if (UNAVAILABLE.equals(currentStauts)) {
				mNoUnAvailableView.setVisibility(View.VISIBLE);
				mUnRefreshView.setEnablePull(false);
			} else {
				mNoAvailableView.setVisibility(View.VISIBLE);
				mRefreshView.setEnablePull(false);
			}
		}else{
			mNoUnAvailableView.setVisibility(View.GONE);
			mNoAvailableView.setVisibility(View.GONE);
		}
		
		if (mCurrentPage == 1){
			mVoucherDatas.clear();
			mCurrentListView.postDelayed(new Runnable() {
				@Override
				public void run() {
					mCurrentListView.setSelection(0);
					mCurrentListView.scrollTo(0, 0);
				}
			}, 100);
		}
		
		mVoucherDatas.addAll(voucherList);
		mVoucherListAdapter.notifyDataSetChanged();
	}

	/**
	 * 加载优惠券信息
	 * @param status 优惠券类型
	 */
	private void queryVoucherList(int currentPage,int pageCount,String beginDate,String endDate){
		if(loadTask != null && loadTask.getStatus() != AsyncTask.Status.FINISHED){
			if(mCurrentPage == 1)
				loadTask.cancel(true);
			else
				return;
		}
		
		loadTask = new BizAsyncTask(BIZTYPE_QUERYVOUCHERLIST,false);
		loadTask.execute(currentStauts,currentPage + "",pageCount +"",beginDate,endDate);
	}
	
	private boolean mPullRefresh;
	private PullRefreshView.RefreshListener mRefreshListener = new PullRefreshView.RefreshListener() {
        @Override
        public void onRefresh() {
            mPullRefresh = true;
            removeFooterView();
            refresh();
        }
    };
    
    private void refresh() {//请求第一页
        mCurrentPage = 1;
        mCurrentListView.setVisibility(View.VISIBLE);
        mPullRefresh = true;
        queryVoucherList(mCurrentPage, mPageCount,beginDate,endDate);
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
            if (firstVisibleItem + visibleItemCount >= totalItemCount && mVoucherListAdapter.hasMore()) {
                willLoad = true;
            }
        }
    };
    
    private void nextPage() {
    	mCurrentPage++;
    	queryVoucherList(mCurrentPage, mPageCount,beginDate,endDate);
	}
    
    private void setItembackgroud(View convertView, Voucher curVoucher) {
		if(!curVoucher.isAvailabelVoucher()){
			convertView.setBackgroundColor(getRootController().getResources().getColor(R.color.voucher_unAvailable));
		}else{
			int backgroudColor = curVoucher.getBackGroundColor();
			if(backgroudColor != 0){
				convertView.setBackgroundColor(backgroudColor);
			}else{
				convertView.setBackgroundColor(getRootController().getResources().getColor(R.color.voucher_available));
			}
		}
	}
	
	//自定义背景颜色显示白色
	private void setNameTextColor(TextView nameText,TextView statusText, Voucher curVoucher) {
		if(curVoucher.getBackGroundColor() != 0 && Voucher.Status.CAN_USE.equals(curVoucher.getStatus())){
			nameText.setTextColor(Color.WHITE);
			statusText.setTextColor(Color.WHITE);
		}else{
			nameText.setTextColor(Color.parseColor("#333333"));
			if(curVoucher.isAvailabelVoucher()){
				statusText.setTextColor(Color.parseColor("#388243"));
			}else{
				statusText.setTextColor(Color.parseColor("#999999"));
				
			}
		}
	}
    
    private class VoucherListAdapter extends com.alipay.android.comon.component.ScrollMoreListAdapter implements Observer{
    	private Set<String> logoKeyList = new HashSet<String>();
    	
    	public VoucherListAdapter(ListView listView, Context context) {
        	super(mCurrentListView, context, mVoucherDatas);
        	initLogoKey();
        }
        
        @Override
        public int getCount() {//判断是否增加一个Item
        	return mVoucherDatas.size();
        }
        
        @Override
        public long getItemId(int position) {
        	return super.getItemId(position);
        }
        
        private void initLogoKey(){
    		for(Voucher voucher : mVoucherDatas){
    			addLogoKey(voucher);
    		}
    	}

		private void addLogoKey(Voucher voucher) {
			voucher.addObserver(this);
			String logoKey = Helper.urlToKey(voucher.getLogoUrl());
			if(logoKey != null && !"".equals(logoKey) && !logoKeyList.contains(logoKey))
				logoKeyList.add(logoKey);
		}
        
        @Override
        public void notifyDataSetChanged() {
        	initLogoKey();
        	super.notifyDataSetChanged();
        }

        @Override
        public View getItemView(int position, View convertView, ViewGroup arg2) {
        	ViewHolder viewHolder;
    		if(convertView == null){
    			convertView = mLayoutInflater.inflate(R.layout.voucher_item, null);
    			viewHolder = new ViewHolder(); 
    			
    			viewHolder.logoUrlImage = (ImageView) convertView.findViewById(R.id.voucherLogo);
    			viewHolder.statusText = (TextView) convertView.findViewById(R.id.voucherStatu);
    			viewHolder.storeNameText = (TextView) convertView.findViewById(R.id.storeName);
    			viewHolder.voucherNameText = (TextView) convertView.findViewById(R.id.voucherName);
    			viewHolder.voucherNameText.setEllipsize(TruncateAt.END);
    			viewHolder.voucherNameText.setMaxLines(2);
    			
    			viewHolder.expireText = (TextView) convertView.findViewById(R.id.expireTip);
    			
    			convertView.setTag(viewHolder);
    		}else{
    			viewHolder =   (ViewHolder) convertView.getTag();
    		}
    		
    		Voucher curVoucher = (Voucher) mListDatas.get(position);
    		setItembackgroud(convertView, curVoucher);
    		
    		setCurView(viewHolder, curVoucher,logoKeyList);
    		return convertView;
        }

        @Override
        protected boolean hasMore() {
        	if(loadTask != null && loadTask.getStatus() != AsyncTask.Status.FINISHED){
        		return false;
        	}
        	
        	return mCurrentPage < mTotalPage;
        }

        @Override
        protected void itemClick(AdapterView<?> arg0, View arg1, int position, long arg3) {
Voucher selectedVoucer = (Voucher) mListDatas.get(position);
    		
			Intent intent = new Intent(getRootController(),VoucherDetailActivity.class);
			intent.putExtra("voucher", selectedVoucer);
			getRootController().startActivityForResult(intent, TODETAILRESULT);
			
			AlipayLogAgent.writeLog(getRootController(), BehaviourID.SUBMITED, null, null, "walletTicket", null, "myTicketDetails", "myTicketList", "seeMyTicket");
        }

		@Override
		public void update(final Observable observable, final Object data) {
			if (observable instanceof Voucher) {
				Voucher curVoucher = (Voucher) observable;
				if (data != null) {
					boolean result = Boolean.parseBoolean(data.toString());
					if (result)
						logoKeyList.remove(Helper.urlToKey(curVoucher.getLogoUrl()));
					else
						logoKeyList.add(Helper.urlToKey(curVoucher.getLogoUrl()));
				} else {
					getRootController().runOnUiThread(new Runnable() {
						@Override
						public void run() {
							notifyDataSetChanged();
						}
					});
				}
			}
		}
    }
	
	@Override
	public void onDestroy() {
		cancleLongLink();
		hallData.deleteObserver(this);
	}

	private void cancleLongLink() {
		if(longLinkServiceManager != null){
			longLinkServiceManager.unRegisterUserCallback();
			longLinkServiceManager.unBindService();
		}
	}
	
	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.title_right:
			AppItemInfo info = getRootController().getHallData().getAppConfig(HallData.COUPON_MARKET);
            // 去券市场
            if (info != null && info.getState() == AppItemInfo.STATE_PRE_UPGRADE){
            	info.onClick(getRootController(), null, AppItemInfo.FROM_APPCTETER);
            	hallData.addObserver(this);
            	if (progressDiv == null) {            		
            		String tipsStr = getRootController().getResources().getString(R.string.marketUpgrading).replace( "%s", info.getNameText() );
	        		
            		progressDiv = getRootController().getDataHelper().showProgressDialogWithCancelButton(getRootController(), null,
            				tipsStr, false, true, getRootController().getDataHelper().cancelListener, getRootController().getDataHelper().cancelBtnListener);
                }
            }else if(info != null && info.getState() == AppItemInfo.STATE_NOMARL){
            	Constant.FROMAPPCENTER = false;
            	info.onClick(getRootController(), null, AppItemInfo.FROM_APPCTETER);
            	AlipayLogAgent.writeLog(getRootController(), BehaviourID.CLICKED, null, null, "walletTicket", null, "80000001Home", "myTicketList", "marketIcon");
            }
			break;
		case R.id.switchUnAvailable:
			tabViewLayout.snapToNextPage(400,new PagedHorizontalLayout.PageScrollListener() {
				@Override
				public void onScrollFinish() {
					mCurrentListView = mUnAvaiVoucherListView;
					currentStauts = UNAVAILABLE;
					
					mVoucherDatas.clear();
					mVoucherListAdapter.notifyDataSetChanged();
					mVoucherListAdapter.addFooterView();
					mAvaiVoucherListView.setVisibility(View.GONE);
					
					setListViewAdapter(mUnAvaiVoucherListView);
					refresh();
				}
			});
			break;
		case R.id.switchAvailable:
			tabViewLayout.snapToPreviousPage(400,new PagedHorizontalLayout.PageScrollListener() {
				@Override
				public void onScrollFinish() {
					mCurrentListView = mAvaiVoucherListView;
					currentStauts = AVAILABLE;
					
					mVoucherDatas.clear();
					mVoucherListAdapter.notifyDataSetChanged();
					mVoucherListAdapter.addFooterView();
					mUnAvaiVoucherListView.setVisibility(View.GONE);
					
					setListViewAdapter(mAvaiVoucherListView);
					refresh();
				}
			});
			break;
		default:
			break;
		}
	}

	@Override
	public void paySuccess(String tradeNo) {
	}

	@Override
	public void payFail(String tradeNo) {
		notifyPayFail(tradeNo);
	}

	private void notifyPayFail(String tradeNo) {
		new BizAsyncTask(CancelTrade.BIZTYPE,false).execute(tradeNo);
	}

	@Override
	public void onCancel(String tradeNo) {
		notifyPayFail(tradeNo);
	}

	// "memo":"success/fail","action":" verify","tradeNO":""
	@Override
	public void onSuccess(JSONObject response) {
		// Toast.makeText(getRootController(), "核销成功.............",Toast.LENGTH_LONG).show();
		showVerifyResult(R.string.verifySuccess, R.drawable.verify_success_notify, true, response);
	}

	@Override
	public void onFail(JSONObject response) {
		// Toast.makeText(getRootController(), "核销失败.............",Toast.LENGTH_LONG).show();
		showVerifyResult(R.string.verifyFail, R.drawable.verify_fail_notify, false, response);
	}

	private void showVerifyResult(int resultTextId, int resultImageId, final boolean success, JSONObject response) {
		View toastView = LayoutInflater.from(getRootController()).inflate(R.layout.verify_result_toast, null);
		TextView toastTextView = (TextView) toastView.findViewById(R.id.resultText);
		ImageView resultImage = (ImageView) toastView.findViewById(R.id.resultImage);

		String verifyStr = "";
		int verifyTimes = response.optInt("verifyTimes");
		if (verifyTimes == 0 || verifyTimes == 1) {
			verifyStr = getRootController().getText(resultTextId) + "";
		} else {
			verifyStr = "成功消费" + verifyTimes + "份";
		}

		toastTextView.setText(Helper.toDBC(verifyStr));
		toastTextView.setEllipsize(null);
		resultImage.setBackgroundResource(resultImageId);

		final Toast toast = new Toast(getRootController());
		toast.setView(toastView);
		toast.setGravity(Gravity.CENTER, 0, 0);
		toast.setDuration(Toast.LENGTH_SHORT);
		toast.show();

		mListContainer.postDelayed(new Runnable() {
			@Override
			public void run() {
				toast.cancel();
				if (success) {
					refresh();
				}
			}
		}, 2000);
	}

	@Override
	public void update(Observable observable, Object data) {
		if(observable instanceof HallData){
			AppItemInfo info = getRootController().getHallData().getAppConfig(HallData.COUPON_MARKET);
	        // 去券市场
	        if (info != null && info.getState() == AppItemInfo.STATE_NOMARL){
	        	if(progressDiv != null && progressDiv.isShowing()){
	        		progressDiv.dismiss();
	        	}
	        	
	        	info.onClick(getRootController(), null, AppItemInfo.FROM_APPCTETER);
	        	AlipayLogAgent.writeLog(getRootController(), BehaviourID.CLICKED, null, null, "walletTicket", null, "80000001Home", "myTicketList", "marketIcon");
	        	if(hallData != null)
	        		hallData.deleteObserver(this);
	        }
		}else if(observable instanceof UserData){
			closeProgress();
			if(userData != null && userData.getIsQueryBalanceSuccess()){
				mMixedVoucherList.clear();
				mMixedVoucherList.addAll(mAliPassServiceImpl.mixLocalAndOnlineVoucher(userData.getAccountName(),getCachedVoucher()));
//				refreshPage();
				populateUI(mMixedVoucherList);
				mRefreshView.setEnablePull(true);
			}else{
				View toastView = LayoutInflater.from(getRootController()).inflate(R.layout.alipay_toast_view, null);
				TextView toastTextView = (TextView) toastView.findViewById(R.id.toastText);
				toastTextView.setText(R.string.CheckNetwork);
				Toast toast = new Toast(getRootController());
				toast.setView(toastView);
				toast.setDuration(Toast.LENGTH_SHORT);
				toast.setGravity(Gravity.BOTTOM, 0, 50);
				toast.show();
				
				mCurrentListView.setVisibility(View.INVISIBLE);
			}
			if(userData != null)
				userData.deleteObserver(this);
		}
	}
	
	private AnimationData animationData;
	
	private void setCurView(ViewHolder viewHolder, Voucher curVoucher,Set<String> logoKeys) {
		if(curVoucher.isAvailabelVoucher()){
			viewHolder.statusText.setTextColor(Color.parseColor("#388243"));
		}else{
			viewHolder.statusText.setTextColor(Color.parseColor("#999999"));
		}
			
		Integer leftDays = curVoucher.getLeftDays();
		if(leftDays == -1){
			viewHolder.expireText.setVisibility(View.INVISIBLE);
		}else {
			viewHolder.expireText.setVisibility(View.VISIBLE);
			if(leftDays == 0){
				viewHolder.expireText.setText("今天到期");
			}else if(leftDays > 0){
				viewHolder.expireText.setText("剩"+ leftDays +"天");
			}
		}
		
		viewHolder.statusText.setText(Voucher.Status.STATUSMAP.get(curVoucher.getStatus()));
		viewHolder.voucherNameText.setText(curVoucher.getVoucherName());
		
		String merchantName = curVoucher.getMerchantName();
		if(merchantName != null && !"".equals(merchantName)){
			viewHolder.storeNameText.setText(merchantName);
			viewHolder.storeNameText.setVisibility(View.VISIBLE);
			viewHolder.voucherNameText.setLines(2);
			
			if(Voucher.Status.CAN_USE.equals(curVoucher.getStatus()) && curVoucher.getBackGroundColor() != 0){
				viewHolder.storeNameText.setTextColor(Color.WHITE);
			}else{
				viewHolder.storeNameText.setTextColor(Color.parseColor("#333333"));
			}
		}else{
			viewHolder.storeNameText.setVisibility(View.GONE);
			viewHolder.voucherNameText.setLines(3);
		}
		
		Bitmap logoBitmap = null;
		if(curVoucher.getMode() == null){
			logoBitmap = curVoucher.getLogoBitmap(getRootController(),logoKeys.contains(Helper.urlToKey(curVoucher.getLogoUrl())));
		} else {
			String userAccount = userData.getAccountName();
			logoBitmap = curVoucher.getCouponLogoBitmap(getRootController(), false, userAccount);
		}
		viewHolder.logoUrlImage.setImageBitmap(logoBitmap);
		viewHolder.logoUrlImage.setScaleType(ScaleType.FIT_CENTER);
		
		setNameTextColor(viewHolder.voucherNameText,viewHolder.statusText, curVoucher);
		setTimeClockColor(viewHolder.expireText,curVoucher);
	}
	
	private void setTimeClockColor(TextView expireText, Voucher curVoucher) {
		Resources resource = getRootController().getResources();
		if(curVoucher.getBackGroundColor() != 0 ){
			expireText.setTextColor(Color.WHITE);
			Drawable whiteDrawable = resource.getDrawable(R.drawable.white_expire_icon);
			whiteDrawable.setBounds(0, 0, whiteDrawable.getMinimumWidth(), whiteDrawable.getMinimumHeight());
			expireText.setCompoundDrawables(whiteDrawable, null, null, null);
		}else{
			expireText.setTextColor(R.color.text_orange);
			Drawable orangeDrawable = resource.getDrawable(R.drawable.orange_expire_icon);
			orangeDrawable.setBounds(0, 0, orangeDrawable.getMinimumWidth(), orangeDrawable.getMinimumHeight());
			expireText.setCompoundDrawables(orangeDrawable, null, null, null);
		}
	}

	/**
	 * 界面退出时动画
	 */
	private void hideAnimation() {
        AnimRelativeLayout animFrameLayout = (AnimRelativeLayout) findViewById(R.id.listContainer);
        voucherLayoutAnimation = animFrameLayout.playOutAnim(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
            }
            
            @Override
            public void onAnimationRepeat(Animation animation) {
            }
            
            @Override
            public void onAnimationEnd(Animation animation) {
                voucherLayoutAnimation=null;
                getRootController().finish();
            }
        });
	}
	

	/**
	 * 界面出现时动画
	 */
	private void showAnimation() {
        AnimRelativeLayout animFrameLayout = (AnimRelativeLayout) findViewById(R.id.listContainer);
        animFrameLayout.setAnimData(animationData);
        
        animFrameLayout.setTargetWidget(R.id.tabViewLayout);
        animFrameLayout.setMovedWidget(R.id.tabViewLayout);
        voucherLayoutAnimation=animFrameLayout.playInAnim(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
            }
            
            @Override
            public void onAnimationRepeat(Animation animation) {
            }
            
            @Override
            public void onAnimationEnd(Animation animation) {
                voucherLayoutAnimation=null;
                dispalyVouchers();
            }
        });
	}
}

class ViewHolder{
	ImageView logoUrlImage;
	TextView voucherNameText;
	TextView statusText;
	TextView expireText;
	TextView storeNameText;
}
