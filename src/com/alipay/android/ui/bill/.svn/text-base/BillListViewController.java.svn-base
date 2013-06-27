package com.alipay.android.ui.bill;

import java.util.ArrayList;
import java.util.Observable;
import java.util.Observer;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Rect;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.animation.Animation;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.FrameLayout.LayoutParams;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import com.alipay.android.appHall.Helper;
import com.alipay.android.appHall.uiengine.AnimRelativeLayout;
import com.alipay.android.biz.BillManagerBiz;
import com.alipay.android.biz.CommonRespHandler;
import com.alipay.android.client.AlipayApplication;
import com.alipay.android.client.AlipayTradeStatus;
import com.alipay.android.client.AnimationData;
import com.alipay.android.client.Main;
import com.alipay.android.client.SubItemAccountQueryActivity;
import com.alipay.android.client.baseFunction.AlipayDealDetailInfoActivity;
import com.alipay.android.client.baseFunction.AlipayDetailInfo;
import com.alipay.android.client.constant.Constant;
import com.alipay.android.client.util.AlipayDataStore;
import com.alipay.android.client.util.Utilz;
import com.alipay.android.common.data.UserData;
import com.alipay.android.common.data.UserData.UDBillInfo;
import com.alipay.android.common.data.UserData.UDLifePayInfo;
import com.alipay.android.comon.component.PullRefreshView;
import com.alipay.android.comon.component.ScrollMoreListAdapter.MoreListener;
import com.alipay.android.servicebeans.GetBillList;
import com.alipay.android.ui.adapter.BillListAdapter;
import com.alipay.android.ui.bean.BillListInfo;
import com.alipay.android.ui.framework.BaseViewController;
import com.alipay.android.util.JsonConvert;
import com.eg.android.AlipayGphone.R;

/**
 * 账单列表 第一部分：账单（公共事业缴费账单、信用卡账单）按时间正序排列显示 第二部分：未付款交易，按时间倒序排列显示
 * 第三部分：除未付款后的全部交易，按时间倒序排列显示
 * 
 * @author caidie.wang
 * 
 */
public class BillListViewController extends BaseViewController implements
		OnClickListener, OnItemClickListener ,Observer{

	private boolean mPullRefresh;
	private PullRefreshView.RefreshListener mRefreshListener = null;
	private MoreListener mMoreListener = null;
	private PullRefreshView pullRefreshView = null;

	private ListView billListView = null;
	private TextView billListEmptyView = null;
	private BillListAdapter billListAdapter;
	private OnScrollListener listScrollListener = null;
	// 列表数据（待重新解析）
	private ArrayList<AlipayDetailInfo> mDatas = null;
	// 埋点使用
	private AlipayTradeStatus mTradeStatus;

	// 请求数据
	private int mRequestPage;// 请求页数

	// TODO:换对象
	private BillListInfo billListInfo = null;

	private int maxLoadingCount;

	private AnimationData animationData;

	private Animation billBelowAnimation;
	private String from;
	private UserData mUserData;
	/**
     * 收支明细引导加强
     */
    private PopupWindow guidePopupWindow;
    AlipayDataStore mAlipayDataStore;

	/**
	 * 是否加载状态
	 */
	private boolean mIsLoading;

	private AlipayApplication application = null;
	private ImageButton titleRight;

	@Override
	protected void onCreate() {
		super.onCreate();
//		mView = LayoutInflater.from(getRootController()).inflate(
//				R.layout.record_list, null, false);
//		addView(mView, null);
		getRootController().setContentView(R.layout.record_list);
		application = (AlipayApplication) getRootController()
				.getApplicationContext();
		mDatas = new ArrayList<AlipayDetailInfo>();
		mTradeStatus = new AlipayTradeStatus(getRootController());
		billListInfo = new BillListInfo();
		
		initListener();
		loadAllVariables();
		
		from = getRootController().getIntent().getStringExtra("pageFrom");
		int currentStatus = mUserData.getCurrentRequestStatus();
		if(currentStatus == UserData.REQUEST_OK){
			loadList();
			//有截图并且从首页跳转到此界面才播放动画
			if (animationData != null && "MainPage".equals(from))
				showAnimation();
		}else{
			dispalyBills();
			showGuidePopupWindow();
		}
	}
	
	/**
     * 显示收支明细加强引导
     */
    private void showGuidePopupWindow() {
    	billListView.postDelayed(new Runnable() {
			@Override
			public void run() {
				if(needShowGuide()){
					Rect frame = new Rect();
					getRootController().getWindow().getDecorView().getWindowVisibleDisplayFrame(frame);
					final ImageView showGuide = new ImageView(getRootController());
					showGuide.setBackgroundResource(R.drawable.trade_in_out_guide);
					guidePopupWindow = new PopupWindow(showGuide, LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT,false);
					guidePopupWindow.setBackgroundDrawable(getRootController().getResources().getDrawable(R.drawable.trade_in_out_guide));
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
					    	mAlipayDataStore.putString(AlipayDataStore.RECORDLIST_VERSION, application.getConfigData().getProductVersion());
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
		return mAlipayDataStore.getString(AlipayDataStore.RECORDLIST_VERSION, "0.0.0").compareTo(application.getConfigData().getProductVersion()) < 0;
	}
    
    
    /**
     * 设置Version 不再显示引导
     */
    private void hideGuide(){
    	if(guidePopupWindow != null && guidePopupWindow.isShowing()){
			guidePopupWindow.dismiss();
			AlipayApplication application = (AlipayApplication) getRootController().getApplicationContext();
	    	mAlipayDataStore.putString(AlipayDataStore.RECORDLIST_VERSION, application.getConfigData().getProductVersion());
		}
    }
	
	private void dispalyBills() {
		int currentStatus = mUserData.getCurrentRequestStatus();
		boolean querySuccess = mUserData.getIsQueryBalanceSuccess();
		if(currentStatus == UserData.REQUEST_PROCESSING){//首页加载中
			mUserData.addObserver(this);
			showProgress();
			billListAdapter.removeFooterView();
		} else if(currentStatus == UserData.REQUEST_NO && !querySuccess){ //首页加载失败
			application.setRecordsRefresh(true);
		}
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK && animationData != null
				&& "MainPage".equals(from)) {
			if (billBelowAnimation != null && !billBelowAnimation.hasEnded() )
				return true;
			hideAnimation();
			return true;
		}else if(keyCode == KeyEvent.KEYCODE_BACK){
			gotoMainActivity();
			return true;
		}
		return super.onKeyDown(keyCode, event);
	}
	
	/**
	 * 跳转到AppHallActivity
	 */
	public void gotoMainActivity() {
		Intent intent = new Intent(getRootController(), Main.class);
		intent.setAction(Intent.ACTION_MAIN);
		intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
		startActivity(intent);
		getRootController().finish();
	}

	/**
	 * 界面退出时动画
	 */
	private void hideAnimation() {
        AnimRelativeLayout animFrameLayout = (AnimRelativeLayout) findViewById(R.id.record_list);
        billBelowAnimation = animFrameLayout.playOutAnim(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
            }
            
            @Override
            public void onAnimationRepeat(Animation animation) {
            }
            
            @Override
            public void onAnimationEnd(Animation animation) {
                billBelowAnimation=null;
                getRootController().finish();
            }
        });
	}
	
	private void showAnimation() {
        AnimRelativeLayout animFrameLayout = (AnimRelativeLayout) findViewById(R.id.record_list);
        animFrameLayout.setAnimData(animationData);
        animFrameLayout.setTargetWidget(R.id.ListViewCanvas);
        animFrameLayout.setMovedWidget(R.id.bill_canvas);
        billBelowAnimation=animFrameLayout.playInAnim(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
            }

            @Override
            public void onAnimationRepeat(Animation animation) {
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                billBelowAnimation=null;
                showGuidePopupWindow();
            }
        });
	}

	/**
	 * 加载首页保存列表数据
	 */
	private void loadList() {
		UDBillInfo udBillInfo = getRootController().getUserData().getUDBillInfo();
		UDLifePayInfo udLifePayInfo = getRootController().getUserData().getUDLifePayInfo();
		if(udLifePayInfo != null && udLifePayInfo.mTotalLifePayCount.length() > 0)
		{
			billListInfo.setLifePayCount(Integer.parseInt(udLifePayInfo.mTotalLifePayCount));
		}
		
		if (udBillInfo != null && udBillInfo.mBillList != null&& !"".equals(udBillInfo.mBillList)  && !"[]".equals(udBillInfo.mBillList)) {
			JSONObject response = JsonConvert
					.convertString2Json((String) udBillInfo.mBillData);
			ParseTransList(response);
		} else{
			setEmptyView();
		}
	}

	@Override
	protected void onResume() {
	    if(getRootController().topController()!=this)
	        getRootController().setContentView(R.layout.record_list);
		boolean refresh = application.isRecordsRefresh();
		if (refresh) {
			refreshAllData();
			UserData mUserData = getRootController().getUserData();
			if (mUserData != null) {
				mUserData.resetStatus();
			}
			application.setRecordsRefresh(false);
		}

		Helper.hideInputPanel(getRootController(), getRootController()
				.getWindow().getDecorView());
	}

	private void loadAllVariables() {
		mUserData = getRootController().getUserData();
		application = (AlipayApplication) getRootController().getApplication();
		animationData = application.getAnimationData();
		mAlipayDataStore = new AlipayDataStore(getRootController());
		TextView title = (TextView) findViewById(R.id.title_text);
		title.setText(R.string.bill_title);

		titleRight = (ImageButton) findViewById(R.id.title_right);
		titleRight.setVisibility(View.VISIBLE);
		titleRight.setBackgroundResource(R.drawable.shouzhi_status);
		titleRight.setOnClickListener(this);

		pullRefreshView = (PullRefreshView) findViewById(R.id.pull);
		pullRefreshView.setRefreshListener(mRefreshListener);
		pullRefreshView.setEnablePull(true);

		billListView = (ListView) findViewById(R.id.ListViewCanvas);
		billListAdapter = new BillListAdapter(billListView,
				getRootController(), mDatas, billListInfo);
		billListAdapter.setMoreListener(mMoreListener);
		billListView.setAdapter(billListAdapter);
		billListView.setOnItemClickListener(this);
		billListView.setOnScrollListener(listScrollListener);

		billListEmptyView = (TextView) findViewById(R.id.EmptyCanvas);
	}

	@Override
	public void onItemClick(AdapterView<?> arg0, View arg1, int position,
			long arg3) {
		diaplayItemInfo((AlipayDetailInfo) mDatas.get(position));
	}

	/**
	 * 打开详情
	 */
	private void diaplayItemInfo(AlipayDetailInfo info) {
		Intent intent = new Intent(getRootController(),
				AlipayDealDetailInfoActivity.class);

		int type = info.getBillType();
		
		if(type == 1)
		{
			intent.putExtra(Constant.CITY, info.lifePayCity);
			intent.putExtra(Constant.SUBBIZTYPE, info.lifePaySubBizType);
			intent.putExtra(Constant.BILLKEY, info.lifePayBillKey);
			intent.putExtra(Constant.RPF_CHARGEINSTNAME, info.tSellerName);
			intent.putExtra(Constant.RPF_BILLAMOUNT, info.resultTradeMoney);
			intent.putExtra(Constant.ORIGINALBILLDATE, info.lifePayOriginalBillDate);
			intent.putExtra(Constant.RPF_BILLDATE, info.resultTradeTime);
			intent.putExtra(Constant.RPF_BILLUSERNAME, info.lifePayOwnerName);
		}
		else
		{
			intent.putExtra(Constant.RPF_RESULT_TYPE, info.resultType);
			intent.putExtra(Constant.RPF_BUYER, info.resultIsBuyer);
		}
		intent.putExtra(Constant.BILL_TYPE, info.getBillType());
		intent.putExtra(Constant.RQF_TRADE_NO, info.resultTradeNo);
		intent.putExtra(Constant.RPF_TRADESTATUSMEMO, info.resultStatusMemo);
		intent.putExtra(Constant.RPF_BIZTYPE, info.tBizType);

		getRootController().startActivity(intent);
		// 如果代付需要刷新列表需要这个状态值，并新增处理方法
		// getRootController().startActivityForResult(intent,
		// Constant.REQUEST_CODE);

		/*AlipayLogAgent.onPageJump(
				getRootController(),
				Constants.TRADERECORDSFORALLVIEW,
				Constants.TRADEDETAILSVIEW,
				"",// appId
				"", // appVersion
				storageStateInfo.getValue(Constants.STORAGE_PRODUCTID),
				getRootController().getUserId(),
				storageStateInfo.getValue(Constants.STORAGE_PRODUCTVERSION),
				storageStateInfo.getValue(Constants.STORAGE_CLIENTID));*/

	}

	/**
	 * 刷新所有数据
	 */
	private void refreshAllData() {
		if (billListView.getEmptyView() != null) {
			billListView.getEmptyView().setVisibility(View.GONE);
			billListView.setEmptyView(null);
		}
		mDatas.clear();
		billListAdapter.notifyDataSetChanged();
		billListInfo.setPage(-1);
		billListInfo.setmCount(-1);
		// mCount = -1;
		refresh();
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.title_right:
			jumpToPaymentDetail();
			break;
		default:
			break;
		}
	}

	/**
	 * 跳转到收支明细
	 */
	private void jumpToPaymentDetail() {
		Intent intent = new Intent(getRootController(),
				SubItemAccountQueryActivity.class);
		getRootController().startActivity(intent);
//		getRootController().overridePendingTransition(R.anim.activity_turn_out,
//				R.anim.activity_turn_in);
	}

	/**
	 * 初始化 Listenter
	 */
	private void initListener() {
		// 刷新
		mRefreshListener = new PullRefreshView.RefreshListener() {
			@Override
			public void onRefresh() {
				mPullRefresh = true;
				refresh();
				UserData mUserData = getRootController().getUserData();
				if (mUserData != null) {
					mUserData.resetStatus();
				}
			}
		};
		// 获取更多
		mMoreListener = new MoreListener() {
			@Override
			public void onMore() {
				nextPage();
			}
		};
		//
		listScrollListener = new OnScrollListener() {
			private boolean willLoad;;

			@Override
			public void onScrollStateChanged(AbsListView view, int scrollState) {
				if (willLoad && scrollState == SCROLL_STATE_IDLE) {
					willLoad = false;
					nextPage();
				}
			}

			@Override
			public void onScroll(AbsListView view, int firstVisibleItem,
					int visibleItemCount, int totalItemCount) {
				if (billListAdapter.hasMore()
						&& firstVisibleItem + visibleItemCount >= totalItemCount) {
					willLoad = true;
				}
			}
		};

	}

	/**
	 * 列表顶部拖拽，刷新列表，请求加载第一页数据
	 */
	private void refresh() {// 请求第一页
		mRequestPage = 1;
		billListInfo.setStartRowNum("1,1");
		// mStartRowNum = "1,1";
		maxLoadingCount = 0;
		pullRefreshView.setRefreshListener(null);
		doGetBillList(String.valueOf(mRequestPage),
				billListInfo.getStartRowNum());
	}

	/**
	 * 拖拽列表底部，请求加载下一页数据
	 */
	private void nextPage() {
		if (mIsLoading) {
			return;
		}

		if (billListInfo.hasMore()) {
			if (billListInfo.getPage() == -1)
				mRequestPage = 1;
			else
				mRequestPage = billListInfo.getPage() + 1;
		} else {
			return;
		}
		doGetBillList(String.valueOf(mRequestPage),
				billListInfo.getStartRowNum());
	}

	/**
	 * 获取账单列表数据
	 * 
	 * @param nextPage
	 * @param pageCount
	 * @param startRowNum
	 */
	private void doGetBillList(String nextPage, String startRowNum) {
		mIsLoading = true;
		new BizAsyncTask(GetBillList.BIZ_TAG, false).execute(nextPage,
				startRowNum);
	}

	@Override
	protected Object doInBackground(String bizType, String... params) {
		if (bizType.equalsIgnoreCase(GetBillList.BIZ_TAG)) {
			return new BillManagerBiz().getBillList(params[0], params[1]);
		}
		return super.doInBackground(bizType, params);
	}
	
	@Override
	protected void onPreDoInbackgroud(String bizType) {
		super.onPreDoInbackgroud(bizType);
		if(GetBillList.BIZ_TAG.equals(bizType)){
			if(mPullRefresh)
				billListAdapter.removeFooterView();
			else
				billListAdapter.addFooterView();
		}
	}

	@Override
	protected void onPostExecute(String bizType, Object result) {
		if (bizType.equalsIgnoreCase(GetBillList.BIZ_TAG)) {
			pullRefreshView.setRefreshListener(mRefreshListener);
			mIsLoading = false;
			JSONObject response = JsonConvert
					.convertString2Json((String) result);
			if (!CommonRespHandler.processSpecStatu(getRootController(),
					response)) {
				if (mRequestPage == 1) {
					if (mPullRefresh) {
						pullRefreshView.refreshFinished();
						mPullRefresh = false;
					}
				}
				billListAdapter.getMorefailed();
				mRequestPage = billListInfo.getPage();
				return;
			}
			if (CommonRespHandler.filterBizResponse(getRootController(),
					response)) {
				ParseTransList(response);
				if (mRequestPage == 1) {
					billListView.setSelection(0);
				}
			}
		}
		super.onPostExecute(bizType, result);
	}

	/**
	 * 解析数据
	 */
	private void ParseTransList(JSONObject obj) {

		int pucBillCount = Integer.valueOf(obj.optString(Constant.LIFE_PAY_BILL_COUNT, "-1"));
		if(pucBillCount > 0)
		{			
			//Merge
			String pucBills = obj.optString("pucBills");
			if(pucBills != null)
	        {
	        	String removeFirst = (pucBills.trim()).replace("[", "");
	        	String removeLast = removeFirst.replace("]", "");

	        	String source = obj.toString();
	        	String result = null;
	        	if(source != null)
	        	{
	        		result = source.replace(":[", ":["+removeLast+",");
	        	}
	        	
	        	if(result != null)
	        	{
	        		try 
	        		{
						obj = new JSONObject(result);
						billListInfo.setLifePayCount(pucBillCount);
					} 
	        		catch (JSONException e) 
	        		{
						e.printStackTrace();
					}
	        		
	        		
	        	}
	        }
		}

		if (obj.has(Constant.RPF_PAGECOUNT)) {
			billListInfo.setPageCount(Utilz.parseInt(
					obj.optString(Constant.RPF_PAGECOUNT), 1));
		}
		billListInfo.setPage(Integer.valueOf(obj.optString(Constant.RPF_PAGE)));
		billListInfo.setTotalCount(Integer.valueOf(obj
				.optString(Constant.RPF_TOTALCOUNT)));
		// billListInfo.setTotalCount(500);
		billListInfo.setStartRowNum(obj.optString(Constant.RPF_STARTROWNUM));
		if (billListInfo.getPage() == 1
				&& obj.has(Constant.WAIT_PAY_BILL_COUNT)) {
			billListInfo.setWaitPayBillCount(Utilz.parseInt(
					obj.optString(Constant.WAIT_PAY_BILL_COUNT), 0));
		}
		if (mRequestPage == 1) {// 第一页
			billListInfo.setmCount(0);
			// mCount = 0;
			mDatas.clear();
		}

		if (billListInfo.getTotalCount() == 0 
				&& billListInfo.getLifePayCount() <= 0) {
			setEmptyView();
			return;
		}

		JSONArray arrayObj;
		JSONObject itemObj;
		try {
			arrayObj = obj.getJSONArray("billList");
			for (int i = 0; i < arrayObj.length(); i++) {
				itemObj = arrayObj.getJSONObject(i);
				mDatas.add(makeItem(itemObj));
				billListInfo.mCountAdd1();
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}

		if (billListInfo.getmCount() > 0) {
			billListAdapter.notifyDataSetChanged();
		}

		// if (mRequestPage == 1) {
		// if (mPullRefresh) {
		// pullRefreshView.refreshFinishd();
		// mPullRefresh = false;
		// }
		// billListAdapter.reset();
		// }
		doPullRefresh();

		if (billListAdapter.getCount() < 10 && maxLoadingCount < 3) {
			nextPage();
			maxLoadingCount++;
		}
	}

	private void setEmptyView() {
		doPullRefresh();
		CharSequence emptyStr = null;
		emptyStr = getRootController().getResources().getText(
				R.string.HaveNoConsumptionRecords);
		billListEmptyView.setText(emptyStr);
		billListView.setEmptyView(billListEmptyView);
		billListAdapter.notifyDataSetChanged();
	}

	/**
	 * 请求结束对是否进行了刷新操作做处理
	 */
	private void doPullRefresh() {
		if (mRequestPage == 1) {
			if (mPullRefresh) {
				pullRefreshView.refreshFinished();
				mPullRefresh = false;
			}
			billListAdapter.reset();
		}
	}

	private AlipayDetailInfo makeItem(JSONObject itemObj) {
		final AlipayDetailInfo tItem = new AlipayDetailInfo();

		tItem.setDetailInfo(getRootController(), itemObj);

		String tTradStatus = itemObj.optString(Constant.RPF_TRADESTATUS);
		tItem.resultType = mTradeStatus.getMapStatus(tTradStatus);

		return tItem;
	}


	@Override
	public void update(Observable observable, Object arg1) {
		if(observable instanceof UserData){
			closeProgress();
			if(mUserData != null && mUserData.getIsQueryBalanceSuccess())
				loadList();
			else{
				View toastView = LayoutInflater.from(getRootController()).inflate(R.layout.alipay_toast_view, null);
				TextView toastTextView = (TextView) toastView.findViewById(R.id.toastText);
				toastTextView.setText(R.string.CheckNetwork);
				Toast toast = new Toast(getRootController());
				toast.setView(toastView);
				toast.setDuration(Toast.LENGTH_SHORT);
				toast.setGravity(Gravity.BOTTOM, 0, 50);
				toast.show();
				
				if (mRequestPage == 1) {
					if (mPullRefresh) {
						pullRefreshView.refreshFinished();
						mPullRefresh = false;
					}
				}
				billListAdapter.getMorefailed();
			}
			if(mUserData != null)
				mUserData.deleteObserver(this);
		}
	}

}
