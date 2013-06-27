package com.alipay.android.ui.voucher;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONObject;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.AbsoluteSizeSpan;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.alipay.android.appHall.Helper;
import com.alipay.android.appHall.appManager.AppItemInfo;
import com.alipay.android.appHall.appManager.HallData;
import com.alipay.android.appHall.common.BitmapDownloadListener;
import com.alipay.android.biz.AlipayClientPubBiz;
import com.alipay.android.biz.CommonRespHandler;
import com.alipay.android.biz.VoucherBiz;
import com.alipay.android.client.constant.Constant;
import com.alipay.android.common.data.UserData;
import com.alipay.android.longlink.LongLinkServiceManager;
import com.alipay.android.longlink.PayResultHanlder;
import com.alipay.android.nfd.LbsLocation;
import com.alipay.android.nfd.LbsRequestParams;
import com.alipay.android.servicebeans.CancelTrade;
import com.alipay.android.servicebeans.GetStoreList;
import com.alipay.android.servicebeans.GetTaobaoSid;
import com.alipay.android.ui.adapter.AvailableVoucherAdapter;
import com.alipay.android.ui.bean.Store;
import com.alipay.android.ui.bean.VerifyCode;
import com.alipay.android.ui.bean.VerifyRecord;
import com.alipay.android.ui.bean.Voucher;
import com.alipay.android.ui.bean.VoucherDetail;
import com.alipay.android.ui.widget.NumberFlowIndicator;
import com.alipay.android.util.Base64;
import com.alipay.android.util.JsonConvert;
import com.eg.android.AlipayGphone.R;
import com.google.zxing.client.FinishListener;

public class VoucherDetailViewController extends DetailViewController implements OnClickListener,PayResultHanlder,VerifyResultNotifier
	,OnPageChangeListener{
	private Voucher selectedVoucher;
	
	private List<VerifyRecord> verifyRecordList;
	private List<VerifyCode> verifyCodeList = new ArrayList<VerifyCode>();
	private LinearLayout mUnAvailableContainer;
	private ViewGroup mAvailableContainer;
	private TextView mRefoundAmount;
	private TextView mRefoundTimes;
	private TextView mPriceText;
	private TextView mVoucherPrice;
	private TextView mTipView;
	private ImageView mPriceCodeDivider;
//	private PagedHorizontalLayout mHorizontalLayout;
	private RelativeLayout mGoodsDetailContainer;
	private RelativeLayout mUsedRecordContainer;
	private RelativeLayout mStoreListContainer;
	private String goodsUrl;
	private TextView mBuyMoneyText;
	private TextView mBuyAmounText;
	private TextView mUseAddress;
	private String voucherId;
	private String voucherFrom;
	private String cityId; //release为空,测试接口需要
	private String outBizNo;
	
	
	private LinearLayout mStoreQueryFailView;
	private LinearLayout mVoucherDetailContainer;
	private boolean mStoreQueryed;
	
	private NumberFlowIndicator indic;
	private ViewPager mViewPager;
	private ViewGroup mAmountView;
	private ViewGroup mUseAddressView;
	private ViewGroup mVoucherDetailView;
	
	
	private ImageView mDivider1;
	public static final String VIRTUALID = "virtualVoucherId";
	
	@Override
	protected void onCreate() {
		super.onCreate();
		mView  = LayoutInflater.from(getRootController()).inflate(R.layout.layout_voucher_detail, null,false);
		addView(mView, null);
		
		selectedVoucher = (Voucher) params;
		voucherId = selectedVoucher.getVoucherId(); 
		voucherFrom = selectedVoucher.getVoucherFrom();
		outBizNo = selectedVoucher.getOutBizNo();
		
		loadAllVariables();
		initListener();
		if(selectedVoucher != null){
			setTitleContent(selectedVoucher.getStatus(),selectedVoucher.getLeftDays(),
					selectedVoucher.getVoucherName());
		}
		queryVoucherInfo(voucherId,voucherFrom,outBizNo);
	}
	
	private BitmapDownloadListener mBitmapDownloadListener = new BitmapDownloadListener() {
		@Override
		public void onComplete(final Bitmap bm) {
			getRootController().runOnUiThread(new Runnable() {
				@Override
				public void run() {
					logoImage.setImageBitmap(bm);
				}
			});
		}
	};
	
	protected void setTitleLogoAndBg(String status){
		if(Voucher.Status.CAN_USE.equals(status)){
			statusText.setTextColor(getRootController().getResources().getColor(R.color.text_green));
			mFrontPage.setBackgroundResource(R.drawable.available_voucher_bg);
			mDivider1.setBackgroundResource(R.drawable.avai_voucher_divider);
		}else{
			mFrontPage.setBackgroundResource(R.drawable.unavailable_voucher_bg);
			statusText.setTextColor(getRootController().getResources().getColor(R.color.text_gray));
			mDivider1.setBackgroundResource(R.drawable.unavai_voucher_divider);
		}
		
		String logoUrl = selectedVoucher.getLogoUrl();
		String appsPath = Helper.getCachePath(getRootController(),Voucher.VOUCHERICONDIR);
		Object bitmapUri = null;
		File iconFile = Voucher.getIconFile(appsPath,logoUrl);
		
		if(iconFile != null && iconFile.exists())
			bitmapUri = iconFile;
		else 
			bitmapUri = logoUrl;
		
		Bitmap voucherBitmap = Helper.bitmapFromUriString(getRootController(), bitmapUri, mBitmapDownloadListener, R.drawable.default_brand_logo);
		logoImage.setImageBitmap(voucherBitmap);
	}
	
	protected void initListener() {
		mGoodsDetailContainer.setOnClickListener(this);
		mUsedRecordContainer.setOnClickListener(this);
		mStoreListContainer.setOnClickListener(this);
		mRefreshView.setOnClickListener(this);
		mStoreQueryFailView.setOnClickListener(this);
		voucherFront.setOnClickListener(this);
		voucherBack.setOnClickListener(this);
		mVoucherDetailView.setOnClickListener(this);
		
		mViewPager.setAdapter(mVoucherDetailAdapter);  
		indic.setViewPager(mViewPager);
		mViewPager.setOnPageChangeListener(this);
	}
	
	@Override
	protected void onResume() {
		super.onResume();
		if(longLinkServiceManager != null)
			longLinkServiceManager.registUserCallback(this,this);
	}
	
	protected void loadAllVariables() {
		loadSameViews();
		indic = (NumberFlowIndicator) mView.findViewById(R.id.viewIndicator);
		mViewPager = (ViewPager) mView.findViewById(R.id.viewflow);
		//设置ViewPager高度,xml无法设置高度
		View view = LayoutInflater.from(getRootController()).inflate(R.layout.voucher_detail_item, new LinearLayout(getRootController()));
		view.measure(0, 0);
		mViewPager.getLayoutParams().height = view.getMeasuredHeight() + 8;
		
		longLinkServiceManager = LongLinkServiceManager.getInstance(getRootController(),this);
		longLinkServiceManager.setContext(getRootController());
		mUnAvailableContainer = (LinearLayout) mView.findViewById(R.id.unAvailableContainer);
		mAvailableContainer = (ViewGroup) mView.findViewById(R.id.availableContainer);
		mRefoundAmount = (TextView) mView.findViewById(R.id.refoundAmount);
		mRefoundTimes = (TextView) mView.findViewById(R.id.refoundTimes);
		
		mPriceText = (TextView) mView.findViewById(R.id.priceText);
		mTipView = (TextView) mView.findViewById(R.id.mTip);
		mVoucherPrice = (TextView) mView.findViewById(R.id.voucherPrice);
		mPriceCodeDivider = (ImageView) mView.findViewById(R.id.priceCodeDivider);
		
		mAmountView = (ViewGroup) mBackPage.findViewById(R.id.amountView);
		mUseAddressView = (ViewGroup) mBackPage.findViewById(R.id.useAddressView);
		mVoucherDetailView = (ViewGroup) mFrontPage.findViewById(R.id.voucher_detail_view);
		
		mGoodsDetailContainer = (RelativeLayout) mView.findViewById(R.id.goodsDetailContainer);
		mUsedRecordContainer = (RelativeLayout) mView.findViewById(R.id.usedRecordContainer);
		mStoreListContainer = (RelativeLayout) mView.findViewById(R.id.storeListContainer);
		mBuyMoneyText = (TextView) mView.findViewById(R.id.buyMoney);
		mBuyAmounText = (TextView) mView.findViewById(R.id.buyAmount);
		mUseAddress = (TextView) mView.findViewById(R.id.useAddress);
		
		mVoucherDetailAdapter = new AvailableVoucherAdapter(this, verifyCodeList,longLinkServiceManager,indic,null);  
		
		mStoreQueryFailView = (LinearLayout) mView.findViewById(R.id.storeQueryFail);
		mVoucherDetailContainer = (LinearLayout) mView.findViewById(R.id.voucherDetailInfo);
		mDivider1 = (ImageView) mFrontPage.findViewById(R.id.divider1);
	}
	
	@Override
	protected Object doInBackground(String bizType, String... params) {
		if(BIZTYPE_GETVOUCHERDETAIL.equals(bizType)){
			return new VoucherBiz().queryVoucherDetail(params[0],params[1],params[2],params[3],params[4]);
		}else if(GetStoreList.BIZ_TAG.equals(bizType)){
			LbsLocation lbsLocation = new LbsLocation(getRootController());
			LbsRequestParams lsbParam = lbsLocation.getCurrentPositionInfo();
			JSONObject storeListParam = lsbParam.getRequestJsonParams();
			return new VoucherBiz().getStoreList(params[0], params[1],params[2],params[3],params[4],"allstore",storeListParam.toString());
		}else if(CancelTrade.BIZTYPE.equals(bizType)){
			return new VoucherBiz().cancelTrade(params[0]);
		}else if(GetTaobaoSid.BIZ_GETTAOBAOSID.equals(bizType)){
			return new AlipayClientPubBiz().getTaobaoSid();
		}
		return super.doInBackground(bizType, params);
	}

	@Override
	protected void onPostExecute(String bizType, Object result) {
		if(BIZTYPE_GETVOUCHERDETAIL.equals(bizType)){
			mQueryFailView.setVisibility(View.GONE);
			mVoucherDetailContainer.setVisibility(View.VISIBLE);
			voucherFront.setVisibility(View.VISIBLE);
			
			JSONObject responseJson = JsonConvert.convertString2Json((String)result);
			
			if(CommonRespHandler.filterBizResponse(getRootController(), responseJson)){
				mVoucherDetail = JsonConvert.json2Bean(responseJson, new VoucherDetail());
				verifyRecordList = JsonConvert.jArry2BeanList(responseJson.optJSONArray("verifyRecordList"), new VerifyRecord());//核销历史
				mVoucherDetailAdapter.setSupportBarcode("2".equals(mVoucherDetail.getVoucherCodeShowType()));//设置是否支持二维码
				mVoucherDetailAdapter.isYimaCode(isYimaCode());//设置是否支持二维码
				
				if(isAvailableVoucher(mVoucherDetail.getStatus())){//可用券
					longLinkServiceManager.bindService(this,this);//绑定核销服务
					
					List<VerifyCode> verifyCodes = JsonConvert.jArry2BeanList(responseJson.optJSONArray("verifyCodeList"), new VerifyCode());
					verifyCodeList.clear();
					for(VerifyCode verifyCode : verifyCodes){
						if(isAvailableVoucher(verifyCode.getVerifyStatus())){
							verifyCodeList.add(verifyCode);
						}
					}
					
					mVoucherDetailAdapter.notifyDataSetChanged();
					mViewPager.setCurrentItem(0);
				}
				
				refreshUI(mVoucherDetail,responseJson);
				//设置时间文字显示
				setTimeZone(mVoucherDetail.getStatus(),mVoucherDetail.getExpiredEndDate(),mVoucherDetail.getGmtRefundApply(),
						mVoucherDetail.getGmtRefundFinish(),mVoucherDetail.getExpiredBeginDate(),mVoucherDetail.getExpiredEndDate());
				
				setTitleContent(mVoucherDetail.getStatus(),mVoucherDetail.getLeftDays(),
						mVoucherDetail.getVoucherName());
				
				if(isYimaCode() && "o2o".equals(mVoucherDetail.getVoucherFrom())){
					logoImage.setBackgroundDrawable(null);
					logoImage.setImageResource(R.drawable.m_logo);
				}
					
				setTotalAmount(mVoucherDetail);
			}else{
				mVoucherDetailContainer.setVisibility(View.GONE);
				mQueryFailView.setVisibility(View.VISIBLE);
				voucherFront.setVisibility(View.INVISIBLE);
			}
		}else if(GetStoreList.BIZ_TAG.equals(bizType)){
			JSONObject responseJson = JsonConvert.convertString2Json((String)result);
			
			if(CommonRespHandler.getStatus(responseJson) == 100){
				mStoreQueryFailView.setVisibility(View.GONE);
				mStoreQueryed = true;
				JSONArray storeListArray = responseJson.optJSONArray("storeList");
				List<Store> storeList = JsonConvert.jArry2BeanList(storeListArray, new Store()) ;
				
				JSONArray nearbyArray = responseJson.optJSONArray("nearbyList");
				List<Store> nearByStoreList = JsonConvert.jArry2BeanList(nearbyArray, new Store()) ;
				
				if(storeList.size() == 0 && nearByStoreList.size() == 0){
					mStoreListContainer.setVisibility(View.GONE);
					return ;
				}else
					mStoreListContainer.setVisibility(View.VISIBLE);
			}else{
				mStoreQueryFailView.setVisibility(View.VISIBLE);
				mStoreListContainer.setVisibility(View.GONE);
			}
		}else if(GetTaobaoSid.BIZ_GETTAOBAOSID.equals(bizType)){
			JSONObject responseJson = JsonConvert.convertString2Json((String)result);
			
			if(CommonRespHandler.filterBizResponse(getRootController(), responseJson)){
				String taobaoSid = responseJson.optString(Constant.RPF_TAOBAOSID);
				if(taobaoSid != null)
					getRootController().getUserData().setTaobaoSid(taobaoSid);
				
				jump2GoodWap(taobaoSid);
			}
		}
		super.onPostExecute(bizType, result);
	}
	
	private boolean isYimaCode() {
		return mVoucherDetail.getIsYimaCode() != null && 1 == mVoucherDetail.getIsYimaCode();
	}

	private void setTotalAmount(VoucherDetail voucherDetail) {
		if(Voucher.VOUCHERTYPE.ONCETIME.equals(voucherDetail.getVoucherType())){
			mPriceText.setText(R.string.voucherAmount);
			String voucherPrice = voucherDetail.getActualAmount();
			
			if(isAvailableVoucher(voucherDetail.getStatus())){
				mVoucherPrice.setTextColor(getRootController().getResources().getColor(R.color.text_green));
			}else{
				mVoucherPrice.setTextColor(getRootController().getResources().getColor(R.color.text_gray));
			}
			
			if(voucherId != null && VIRTUALID.equals(voucherId) || (voucherPrice != null && voucherPrice.length() > 3 && !"0.00".equalsIgnoreCase(voucherPrice))){
				voucherPrice = "￥" + voucherPrice;
				SpannableString priceSpaString = new SpannableString(voucherPrice);
				priceSpaString.setSpan(new AbsoluteSizeSpan(25,true) , 0, 1, Spannable.SPAN_INCLUSIVE_EXCLUSIVE);
				priceSpaString.setSpan(new AbsoluteSizeSpan(40,true) , 1, voucherPrice.length() - 3, Spannable.SPAN_INCLUSIVE_EXCLUSIVE);
				priceSpaString.setSpan(new AbsoluteSizeSpan(25,true) , voucherPrice.length() - 3, voucherPrice.length(), Spannable.SPAN_INCLUSIVE_EXCLUSIVE);
				mVoucherPrice.setText(priceSpaString);
				
				priceContentVisible();
			}else{
				priceContentInVisible();
			}
		}else{
			String canUseTimes;
			if(isAvailableVoucher(voucherDetail.getStatus())){
				mVoucherPrice.setTextColor(getRootController().getResources().getColor(R.color.text_green));
				canUseTimes = voucherDetail.getCanUseTimes() + "";
				mPriceText.setText(R.string.availableAmout);
			}else{
				mVoucherPrice.setTextColor(getRootController().getResources().getColor(R.color.text_gray));
				canUseTimes = voucherDetail.getTotalTimes()  + "";
				mPriceText.setText(R.string.amountUnit);
			}
			canUseTimes = canUseTimes + "份";
			
			if (canUseTimes.length() >= 2) {
				SpannableString amountSpaString = new SpannableString(canUseTimes);
				amountSpaString.setSpan(new AbsoluteSizeSpan(40, true), 0, canUseTimes.length() - 1, Spannable.SPAN_INCLUSIVE_EXCLUSIVE);
				amountSpaString.setSpan(new AbsoluteSizeSpan(25, true), canUseTimes.length() - 1, canUseTimes.length(),
						Spannable.SPAN_INCLUSIVE_EXCLUSIVE);
				mVoucherPrice.setText(amountSpaString);
				priceContentVisible();
			} else {
				priceContentInVisible();
			}
		}
		
		if(isYimaCode()){
			priceContentGone();
			mTipView.setText(Helper.toDBC(getRootController().getText(R.string.mTip) + ""));
		}else{
			mTipView.setText("");
		}
		
		if(voucherId != null && VIRTUALID.equals(voucherId)){
			priceContentVisible();
			mTipView.setText(getRootController().getText(R.string.sampleTip));
		}
	}

	private void priceContentInVisible() {
		mPriceText.setVisibility(View.INVISIBLE);
		mVoucherPrice.setVisibility(View.INVISIBLE);
		mPriceCodeDivider.setVisibility(View.INVISIBLE);
	}
	
	private void priceContentGone(){
		mPriceText.setVisibility(View.GONE);
		mVoucherPrice.setVisibility(View.GONE);
		mPriceCodeDivider.setVisibility(View.GONE);
	}
	
	private void priceContentVisible() {
		mPriceText.setVisibility(View.VISIBLE);
		mVoucherPrice.setVisibility(View.VISIBLE);
		mPriceCodeDivider.setVisibility(View.VISIBLE);
	}
	
	@Override
	protected void rotateFinished() {
		super.rotateFinished();
		if (!mStoreQueryed)
			queryStoreList(cityId,voucherFrom,"1","2");
	}
	
	/**
	 * 获取券详情后刷新界面
	 */
	private void refreshUI(VoucherDetail voucherDetail,JSONObject respJson){
		if(isAvailableVoucher(voucherDetail.getStatus())){
			mUnAvailableContainer.setVisibility(View.GONE);
			mAvailableContainer.setVisibility(View.VISIBLE);
		}else if(isRefoundVoucher(voucherDetail.getStatus())){
			mUnAvailableContainer.setVisibility(View.VISIBLE);
			mAvailableContainer.setVisibility(View.GONE);
			
			String refundAmount = voucherDetail.getRefundAmount();
			Integer refundTimes = voucherDetail.getRefundTimes();//Utilz.parseInt(, 0);
			mRefoundAmount.setText("退款金额："+refundAmount);
			mRefoundTimes.setText("退款份数："+refundTimes);
			if(refundAmount != null ){
				mRefoundAmount.setVisibility(View.VISIBLE);
			}else{
				mRefoundAmount.setVisibility(View.GONE);
			}
		}else{
			mUnAvailableContainer.setVisibility(View.GONE);
			mAvailableContainer.setVisibility(View.GONE);
		}
		
		if(verifyRecordList == null || verifyRecordList.size() == 0){
			mUsedRecordContainer.setVisibility(View.GONE);
		}else {
			mUsedRecordContainer.setVisibility(View.VISIBLE);
		}
		
		goodsUrl = mVoucherDetail.getGoodsDetailUrl();
		if(goodsUrl == null || "".equals(goodsUrl)){
			mGoodsDetailContainer.setVisibility(View.GONE);
		}else{
			mGoodsDetailContainer.setVisibility(View.VISIBLE);
		}
		
		mBuyMoneyText.setText("￥"+mVoucherDetail.getAmount());
		mBuyAmounText.setText(mVoucherDetail.getTotalTimes() +"份");
		
		if(verifyRecordList.size() == 0){
			mUseAddressView.setVisibility(View.GONE);
		}else {
			String address = verifyRecordList.get(0).getAddress();
			
			if(address != null && !"".equals(address)){
				mUseAddress.setText(address);
				mUseAddressView.setVisibility(View.VISIBLE);
			}else
				mUseAddressView.setVisibility(View.GONE);
		}
		
		if("0".equals(mVoucherDetail.getVoucherType())){
			mBuyAmounText.setVisibility(View.GONE);
			mAmountView.setVisibility(View.GONE);
		}else{
			mBuyAmounText.setVisibility(View.VISIBLE);
			mAmountView.setVisibility(View.VISIBLE);
		}
		
		String voucherFromDesc = voucherDetail.getVoucherFromDesc();
		setVoucherSource(voucherFromDesc,mVoucherDetail.getVoucherSource());
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.voucher_front:
			applyRotation(true,0, 90);
			break;
		case R.id.voucher_detail_view:
			getRootController().finish();
			break;
		case R.id.voucher_back:
			applyRotation(false, 360, 270);
			break;
		case R.id.storeQueryFail:
			queryStoreList(cityId,voucherFrom,"1","2");
			break;
		case R.id.refreshView:
			queryVoucherInfo(voucherId, voucherFrom, outBizNo);
			break;
		case R.id.goodsDetailContainer:
			UserData userData = getRootController().getUserData();
			String taobaoSid = userData.getTaobaoSid();
			if (!"".equals(taobaoSid)) {
				jump2GoodWap(taobaoSid);
			} else {
				getTaobaoSid();
			}
			break;
		case R.id.usedRecordContainer:
			if(verifyRecordList.size() > 0){
				VerifyRecordData verifyRecordData = new VerifyRecordData(verifyRecordList,mVoucherDetail.getTotalTimes(),mVoucherDetail.getCanUseTimes());
				verifyRecordData.setVoucherId(voucherId);
				verifyRecordData.setVoucherFrom(voucherFrom);
				verifyRecordData.setOutBizNo(outBizNo);
				getRootController().navigateTo("VerifyRecordsListView",verifyRecordData);
			}
			break;
		case R.id.storeListContainer:
			mVoucherDetail.setStoreListTitle(getRootController().getResources().getText(R.string.availableStore) + "");
			getRootController().navigateTo("StoreListView",mVoucherDetail);
			break;
		default:
			break;
		}
	}

	private void jump2GoodWap(String taobaoSid) {
		String detailGoodsUrl = goodsUrl ;
		if (detailGoodsUrl != null && detailGoodsUrl.indexOf("http") != -1) {
			Uri uri = Uri.parse(goodsUrl);
			String query = uri.getQuery();
			if (query == null) {
				detailGoodsUrl += "?ttid=600351@zhifubao_android_4.0.0";
			} else {
				detailGoodsUrl += "&ttid=600351@zhifubao_android_4.0.0";
			}

			if (taobaoSid != null)
				detailGoodsUrl = detailGoodsUrl + "&sid=" + taobaoSid;

			AppItemInfo info = getRootController().getHallData().getAppConfig(HallData.COUPON_MARKET);
			String params = "entry=" + Base64.encode(detailGoodsUrl.getBytes());
			if (info != null) {
				info.onClick(getRootController(), params, AppItemInfo.FROM_APPCTETER);
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

	//"memo":"success/fail","action":" verify","tradeNO":""
	@Override
	public void onSuccess(JSONObject response) {
		showVerifyResult(R.string.verifySuccess,R.drawable.verify_success_notify,true,response);
		verifyResult = true;
	}

	@Override
	public void onFail(JSONObject response) {
//		Toast.makeText(getRootController(), "核销失败.............", Toast.LENGTH_LONG).show();
		showVerifyResult(R.string.verifyFail,R.drawable.verify_fail_notify,false,response);
	}
	
	private void showVerifyResult(int resultTextId,int resultImageId,final boolean success,JSONObject response){
		View toastView = LayoutInflater.from(getRootController()).inflate(
                R.layout.verify_result_toast, null);
    	TextView toastTextView = (TextView)toastView.findViewById(R.id.resultText);
    	ImageView resultImage = (ImageView) toastView.findViewById(R.id.resultImage);
    	
    	String verifyStr = "";
    	int verifyTimes = response.optInt("verifyTimes");
    	if(verifyTimes == 0 || verifyTimes == 1){
    		verifyStr = getRootController().getText(resultTextId) + "";
    	}else{
    		verifyStr = "成功消费"+ verifyTimes + "份";
    	}
    	
    	toastTextView.setEllipsize(null);
    	toastTextView.setText(Helper.toDBC(verifyStr));
    	resultImage.setBackgroundResource(resultImageId);
    	
        final Toast toast = new Toast(getRootController());
        toast.setView(toastView);
        toast.setGravity(Gravity.CENTER, 0, 0);
        toast.setDuration(Toast.LENGTH_SHORT);
        toast.show();
        
        mViewPager.postDelayed(new Runnable() {
			@Override
			public void run() {
				toast.cancel();
				if (success) {
					queryVoucherInfo(voucherId,voucherFrom,outBizNo);
				}
			}
		}, 2000);
	}
	
	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		if(requestCode == Constant.REQUEST_LOGIN_BACK && resultCode == Activity.RESULT_OK){
			queryVoucherInfo(voucherId,voucherFrom,outBizNo);
		}
		super.onActivityResult(requestCode, resultCode, data);
	}

	private boolean scrollFinish;
	@Override
	public void onPageScrollStateChanged(int arg0) {
		if(arg0 == ViewPager.SCROLL_STATE_IDLE || arg0 == ViewPager.SCROLL_STATE_SETTLING){
			scrollFinish = true;
		}
	}

	@Override
	public void onPageScrolled(int arg0, float arg1, int arg2) {
	}

	@Override
	public void onPageSelected(int arg0) {
		if(scrollFinish){
			indic.onSwitched(null,arg0);
			scrollFinish = false;
		}
	}

	@Override
	protected void setUsedTime() {
		setTimeTipText("使用时间:" ,(verifyRecordList.size() > 0 ? verifyRecordList.get(0).getVerifyTime() : "null"));
	}
}