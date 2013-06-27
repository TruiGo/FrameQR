package com.alipay.android.ui.voucher;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.RelativeLayout.LayoutParams;
import android.widget.TextView;
import android.widget.Toast;

import com.alipay.android.appHall.Helper;
import com.alipay.android.appHall.common.BitmapDownloadListener;
import com.alipay.android.biz.CommonRespHandler;
import com.alipay.android.biz.VoucherBiz;
import com.alipay.android.client.Login;
import com.alipay.android.client.SchemeHandler;
import com.alipay.android.client.constant.Constant;
import com.alipay.android.common.data.UserData;
import com.alipay.android.core.ParamString;
import com.alipay.android.log.Constants.BehaviourID;
import com.alipay.android.nfd.LbsLocation;
import com.alipay.android.nfd.LbsRequestParams;
import com.alipay.android.serviceImpl.AliPassServiceImpl;
import com.alipay.android.servicebeans.GetStoreList;
import com.alipay.android.ui.adapter.AvailableVoucherAdapter;
import com.alipay.android.ui.bean.AlipassInfo;
import com.alipay.android.ui.bean.AlipassInfo.Einfo;
import com.alipay.android.ui.bean.AlipassInfo.FileInfo;
import com.alipay.android.ui.bean.AlipassInfo.Locations;
import com.alipay.android.ui.bean.AlipassInfo.Style;
import com.alipay.android.ui.bean.Store;
import com.alipay.android.ui.bean.VerifyCode;
import com.alipay.android.ui.bean.Voucher;
import com.alipay.android.ui.bean.Voucher.Mode;
import com.alipay.android.ui.bean.VoucherDetail;
import com.alipay.android.ui.beanutil.AlipassConverter;
import com.alipay.android.ui.beanutil.VoucherSyncHelper;
import com.alipay.android.ui.widget.NumberFlowIndicator;
import com.alipay.android.util.FileUtils;
import com.alipay.android.util.JsonConvert;
import com.eg.android.AlipayGphone.R;

public class CouponDetailViewController extends DetailViewController implements OnClickListener,OnPageChangeListener{
	private AliPassServiceImpl mAliPassServiceImpl;
	private static final String BIZTYPE_GETLOCAOALIPASS = "getLocalAlipass";
	public static final int SUCCESS = 100;
	public static final int CANCLE = 101;
	public static final int FAIL = 102;
	
	private Button mAddButton;
	private Button mCancelButton;
	private ImageView mDivider1;
	private ImageView mDivider2;
	
	private ViewGroup mFreeDetailContainer;
	private ViewGroup mStoreListContainer;
	private ViewGroup mDisclaimer;
	private View mDetailView;
	private TextView mDisclaimerText;
	private View mDisclaimerDivider;
	
	private TextView mVoucherTitle;
	//xxxx.alipass
	private String mPassName;
	//pass文件前缀
	private String mPrefixPassName;
	private String mSchema;
	private Uri mPathUri;
	private AlipassInfo mAlipassInfo;//alipass文件信息
	private ImageView mCouponStamp;
	private ViewGroup mCodeSoundContainer;//二维码声波展示区
	private ImageView mBodyContainer;//
	private NumberFlowIndicator indic;//多码时显示
	private ViewPager mViewPager;
	private List<VerifyCode> verifyCodeList = new ArrayList<VerifyCode>();
	private UserData mUserData;
	private Voucher selectedVoucher;
	private String voucherId;
	private String voucherFrom;
	private String outBizNo;
	private TextView mBackTilte;//反面标题
//	private View mFrontBg;
//	private ImageView mRightBottomCorner;
	private ViewGroup mFrontScrollView;
	
	//反面每个item上的箭头
	private ImageView mDisclaimerArrow;
	private ImageView mDetailArrow;
	private String stripUrl;
	
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if(keyCode == KeyEvent.KEYCODE_BACK && mSchema != null && 
				mFrontPage.getVisibility() == View.VISIBLE && mBackPage.getVisibility() == View.GONE){
			animateOut(mSchema,CANCLE,"未添加成功");
			return true;
		}
		
		return super.onKeyDown(keyCode, event);
	}

	private void animateOut(final String scheme,final int status,final String statusMemo) {
		if(status == 100){//成功动画
			final Animation moveDownAnimation = new TranslateAnimation(0f,0f,mFrontPage.getTop(),mFrontPage.getBottom() + 
					getRootController().getResources().getDimension(R.dimen.coupon_bootom_margin));
			moveDownAnimation.setDuration(1000);
			moveDownAnimation.setFillAfter(true);
			moveDownAnimation.setAnimationListener(new Animation.AnimationListener() {
				@Override
				public void onAnimationStart(Animation animation) {}
				@Override
				public void onAnimationRepeat(Animation animation) {}
				
				@Override
				public void onAnimationEnd(Animation animation) {
					backToThirdParty(scheme,status,statusMemo);
					doSimpleLog("alipass", BehaviourID.CLICKED, "ticketDetails", "dingding", "addButton");
				}
			});
			mContainer.startAnimation(moveDownAnimation);
		}else{//失败,取消退出
			backToThirdParty(scheme,status,statusMemo);
			doSimpleLog("alipass", BehaviourID.CLICKED, "ticketDetails", "dingding", "cancelButton");
		}
	}
	
	private void backToThirdParty(String scheme,int status,String statusMemo){
		Integer serailNumber = getSerialNum();
		Intent dingdingIntent = new Intent();
		String schemaStr = scheme + "://platformapi/addalipass?status="+status+"&statusMemo="+statusMemo + (serailNumber != null ? "&serialNumber=" + serailNumber :"");
		dingdingIntent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
		dingdingIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
		dingdingIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		dingdingIntent.setData(Uri.parse(schemaStr));
		startActivity(dingdingIntent);
		getRootController().finish();
		getRootController().overridePendingTransition(R.anim.grow_from_middle, R.anim.shrink_to_middle);
		getRootController().moveTaskToBack(true);
		
		//标记已经分发过
		getRootController().setDispathced(true);
	}

	private Integer getSerialNum() {
		if(mAlipassInfo != null){
			FileInfo fileInfo = mAlipassInfo.getFileInfo();
			if(fileInfo != null)  
				return fileInfo.getSerialNumber();
		}
		return null;
	}

	@Override
	protected void onCreate() {
		super.onCreate();
		getRootController().setDispathced(false);
		mView = LayoutInflater.from(getRootController()).inflate(R.layout.layout_coupon_detail, null, false);
		addView(mView, null);
		loadAllVariables();
		initListeners();
		selectedVoucher = (Voucher) params;
		if(selectedVoucher != null ){
			if (Voucher.Mode.OFFLINE.equals(selectedVoucher.getMode())) { // 本地离线券
				mPassName = selectedVoucher.getPassFileName();
				mAlipassInfo = mAliPassServiceImpl.getAlipassFileByName(mUserData.getAccountName(),mPassName);
				mView.postDelayed(new Runnable() {//在界面渲染完成后在显示图片
					@Override
					public void run() {
						refreshUI();
					}
				}, 200);
			} else if (Voucher.Mode.PREVIEW.equals(selectedVoucher.getMode())) {
				getRootController().setShowMenu(false);
				String params = getRootController().getIntent().getStringExtra(SchemeHandler.PARAM);
				if(params != null && !"null".equals(params.trim())){//预览界面
					ParamString paramString = new ParamString(params);
					mSchema = paramString.getValue("sourceId");
					String path = paramString.getValue("path");
					mPassName = path.substring(path.lastIndexOf("/") + 1);
					if(mPassName != null)
						mPrefixPassName = mPassName.substring(0,mPassName.lastIndexOf("."));
					mPathUri = Uri.parse(path);
					boolean unZipResult = mAliPassServiceImpl.readAndUnZipPassFile(mPathUri, mPassName);//从第三方读取pass文件并解压
					if (unZipResult) {
						setButtonVisiable();
						new BizAsyncTask(BIZTYPE_GETLOCAOALIPASS).execute(mPassName);//从本地图区数据展示
					} else {
						Toast.makeText(getRootController(), "解压读取文件失败", Toast.LENGTH_SHORT).show();
						voucherFront.setVisibility(View.INVISIBLE);
						mAddButton.setEnabled(false);
					}
					
					addButtonUseable();
				}
			} else {// 从网络上获取，联网取详情
				selectedVoucher = (Voucher) params;
				voucherId = selectedVoucher.getVoucherId();
				voucherFrom = selectedVoucher.getVoucherFrom();
				outBizNo = selectedVoucher.getOutBizNo();
				
				setTitleContent(selectedVoucher.getStatus(),selectedVoucher.getLeftDays() , "");
				setTitleText(selectedVoucher.getVoucherName());
				queryVoucherInfo(voucherId, voucherFrom, outBizNo);
			}
		}
	}
	
	@Override
	protected void onResume() {
		super.onResume();
		if(Voucher.Mode.PREVIEW.equals(selectedVoucher.getMode()))
			getRootController().countMeNotTemporary(true);
	}
	
	private void addButtonUseable() {
		if(mUserData != null && mAliPassServiceImpl.passFileExist(mUserData.getAccountName(),mPassName)){
			enableAddButton();
		}
	}
	
	private void setButtonVisiable() {
		if(mPathUri != null && mPathUri.getScheme() != null && mPathUri.getScheme().startsWith("content")){
			mCancelButton.setText(R.string.Cancel);
			mCancelButton.setVisibility(View.VISIBLE);
			mCancelButton.setBackgroundResource(R.drawable.title_btn_xml);
			
			mAddButton.setText(R.string.add);
			mAddButton.setVisibility(View.VISIBLE);
			mAddButton.setBackgroundResource(R.drawable.title_btn_xml);
		}
	}
	
	protected void loadAllVariables() {
		loadSameViews();
		mUserData = getRootController().getUserData();
		mAliPassServiceImpl = new AliPassServiceImpl(getRootController());
		mAddButton = (Button) mView.findViewById(R.id.title_slaveright);
		RelativeLayout.LayoutParams param = (LayoutParams) mAddButton.getLayoutParams();
		param.addRule(RelativeLayout.ALIGN_PARENT_RIGHT, RelativeLayout.TRUE);
		
		mCancelButton = (Button) mView.findViewById(R.id.title_left);
		mFreeDetailContainer = (ViewGroup) mBackPage.findViewById(R.id.freeDetailContainer);
		mVoucherTitle = (TextView) mView.findViewById(R.id.voucher_title);
		mDivider1 = (ImageView) mView.findViewById(R.id.divider1);
		mDivider2 = (ImageView) mView.findViewById(R.id.divider2);
		mStoreListContainer = (ViewGroup) mBackPage.findViewById(R.id.storeListContainer);
		mDisclaimer = (ViewGroup) mBackPage.findViewById(R.id.disclaimer);
		mDetailView = mBackPage.findViewById(R.id.detailView);
		mDisclaimerText = (TextView) mBackPage.findViewById(R.id.disclaimerText);
		mCouponStamp = (ImageView) mFrontPage.findViewById(R.id.coupon_stamp);
		mCouponStamp.bringToFront();
		
		mCodeSoundContainer = (ViewGroup) mFrontPage.findViewById(R.id.codeSoundContainer);
		mBodyContainer = (ImageView) mFrontPage.findViewById(R.id.bodyContainer);
//		mFrontBg =  mFrontPage.findViewById(R.id.frontBg);
//		mRightBottomCorner =  (ImageView) mFrontPage.findViewById(R.id.right_bottom_corner);
		mFrontScrollView =  (ViewGroup) mFrontPage.findViewById(R.id.frontScrollView);
		
		mBackTilte = (TextView) mBackPage.findViewById(R.id.back_title);
		mDisclaimerArrow = (ImageView) mBackPage.findViewById(R.id.disclaimer_arrow);
		mDetailArrow = (ImageView) mBackPage.findViewById(R.id.detail_arrow);
		setArrowBg(mDetailArrow,true);//详情箭头默认向下
		indic = (NumberFlowIndicator) mView.findViewById(R.id.viewIndicator);
		mViewPager = (ViewPager) mView.findViewById(R.id.viewflow);
		//设置ViewPager高度,xml无法设置高度
		View view = LayoutInflater.from(getRootController()).inflate(R.layout.voucher_detail_item, new LinearLayout(getRootController()));
		view.measure(0, 0);
		mViewPager.getLayoutParams().height = view.getMeasuredHeight() + 8;
		mVoucherDetailAdapter = new AvailableVoucherAdapter(this, verifyCodeList,null,indic,Color.WHITE);
		mDisclaimerDivider = mBackPage.findViewById(R.id.disclaimer_divider);
	}
	
	private void initListeners() {
		mAddButton.setOnClickListener(this);
		mCancelButton.setOnClickListener(this);
		voucherFront.setOnClickListener(this);
		voucherBack.setOnClickListener(this);
		mFreeDetailContainer.setOnClickListener(this);
		mStoreListContainer.setOnClickListener(this);
		mDisclaimer.setOnClickListener(this);
		
		mViewPager.setAdapter(mVoucherDetailAdapter);  
		indic.setViewPager(mViewPager);
		mViewPager.setOnPageChangeListener(this);
		mRefreshView.setOnClickListener(this);
//		mFrontBg.setOnClickListener(this);
		mFrontScrollView.setOnClickListener(this);
	}
	
	@Override
	protected Object doInBackground(String bizType, String... params) {
		if(BIZTYPE_GETVOUCHERDETAIL.equals(bizType)){
			return new VoucherBiz().queryVoucherDetail(params[0],params[1],params[2],params[3],params[4]);
		}else if(BIZTYPE_GETLOCAOALIPASS.equals(bizType)){
			return mAliPassServiceImpl.resolvePassFromTempDir(params[0]);
		}else if(GetStoreList.BIZ_TAG.equals(bizType)){
			LbsLocation lbsLocation = new LbsLocation(getRootController());
			LbsRequestParams lsbParam = lbsLocation.getCurrentPositionInfo();
			JSONObject storeListParam = lsbParam.getRequestJsonParams();
			return new VoucherBiz().getStoreList(params[0], params[1],params[2],params[3],params[4],"allstore",storeListParam.toString());
		}
		return super.doInBackground(bizType, params);
	}
	
	@Override
	protected void onPostExecute(String bizType, Object result) {
		if(BIZTYPE_GETVOUCHERDETAIL.equals(bizType)){//查询券详情
			JSONObject responseJson = JsonConvert.convertString2Json((String)result);
			if(CommonRespHandler.filterBizResponse(getRootController(), responseJson)){
				mVoucherDetail = JsonConvert.json2Bean(responseJson, new VoucherDetail());
				mAlipassInfo = mAliPassServiceImpl.convertVoucherDetail2Alipass(mVoucherDetail,responseJson);
				
				refreshUI();
				mQueryFailView.setVisibility(View.GONE);
			}else{
				mQueryFailView.setVisibility(View.VISIBLE);
				mQueryFailView.bringToFront();
				voucherFront.setVisibility(View.INVISIBLE);
			}
		}else if(BIZTYPE_GETLOCAOALIPASS.equals(bizType)){
			if(result != null)
				mAlipassInfo = (AlipassInfo) result;
			
			selectedVoucher.setPassFileName(mPassName.substring(0, mPassName.lastIndexOf(".")));
			refreshUI();
		}else if(GetStoreList.BIZ_TAG.equals(bizType)){
			JSONObject responseJson = JsonConvert.convertString2Json((String)result);
			if(CommonRespHandler.filterBizResponse(getRootController(), responseJson)){
				mStoreQueryed = true;
				JSONArray storeListArray = responseJson.optJSONArray("storeList");
				List<Store> storeList = JsonConvert.jArry2BeanList(storeListArray, new Store()) ;
				if(storeList.size() > 0){
					mStoreListContainer.setVisibility(View.VISIBLE);
				}else
					mStoreListContainer.setVisibility(View.GONE);
			}
		}
		super.onPostExecute(bizType, result);
	}
	
	//设置券详情界面文字颜色
	private void setTextColor(String stauts){
		Style alipassStyle = mAlipassInfo.getStyle();
		if(alipassStyle != null){
			int passColor = alipassStyle.getBackgroundColor();
			if(Voucher.Status.CAN_USE.equals(stauts)){
				if(passColor != 0){
					statusText.setTextColor(Color.WHITE);
					voucherNameText.setTextColor(Color.WHITE);
					mExpireTimeText.setTextColor(Color.WHITE);
					mVoucherFromText.setTextColor(Color.WHITE);
					expireTip.setTextColor(Color.WHITE);
					mVoucherTitle.setTextColor(Color.WHITE);
					mBackTilte.setTextColor(Color.WHITE);
				}else{
					statusText.setTextColor(getRootController().getResources().getColor(R.color.text_green));
				}
			}
		}else{
			mVoucherTitle.setTextColor(getRootController().getResources().getColor(R.color.TextColorBlack));
		}
	}
		
	private void refreshUI() {
		if(mAlipassInfo != null){
			setTextColor(mAlipassInfo.getStatus());//设置文字颜色
			setCouponStamp(mAlipassInfo.getStatus());
			voucherFront.setVisibility(View.VISIBLE);
			
			Style alipassStyle = mAlipassInfo.getStyle();
			if(alipassStyle != null){
				int passColor = alipassStyle.getBackgroundColor();
				setBgColor(passColor);
				setTimeClockColor(passColor);
			}
			
			selectedVoucher.setExpiredEndDate(mAlipassInfo.getEvoucherInfo().getEndDate());
			Integer leftDays = selectedVoucher.getLeftDays();
			setTitleContent(mAlipassInfo.getStatus(),leftDays , mAlipassInfo.getMerchant().getMname());
			mDisclaimerText.setText(mAlipassInfo.getEvoucherInfo().getDisclaimer());
			
			String title = mAlipassInfo.getEvoucherInfo().getTitle();
			setTitleText(title);
			TextView detailText = (TextView) mDetailView.findViewById(R.id.detailDescription);
			detailText.setText(mAlipassInfo.getEvoucherInfo().getDescription());
			
			setTimeZone(mAlipassInfo.getStatus(),null,null,null,mAlipassInfo.getEvoucherInfo().getStartDate(),mAlipassInfo.getEvoucherInfo().getEndDate());
			if(selectedVoucher != null && Mode.ONLINE.equals(selectedVoucher.getMode())){
				String voucherFromDesc = mVoucherDetail.getVoucherFromDesc();
				setVoucherSource(voucherFromDesc,mVoucherDetail.getVoucherSource());//设置券来源
				setTitleFromSource(voucherFromDesc);
			}
			
			Einfo einfo = mAlipassInfo.getEvoucherInfo().geteInfo();
			List<VerifyCode> verifycodeList = null;
			if(einfo != null ){
				verifyCodeList.clear();
				verifycodeList = AlipassConverter.barcodes2VerifyCode(einfo.getBarcodeList());
			}
			//存在券码显示券码样式
			if(verifycodeList != null && verifycodeList.size() > 0){
				mCouponStamp.setVisibility(View.GONE);
				mCodeSoundContainer.setVisibility(View.VISIBLE);
				mVoucherDetailAdapter.setSupportBarcode(true);
				verifyCodeList.addAll(verifycodeList);
				mVoucherDetailAdapter.notifyDataSetChanged();
				mViewPager.setCurrentItem(0);
			} else {//不存在券码显示样式
				boolean isOnLine = !(Mode.OFFLINE.equals(selectedVoucher.getMode()) || Mode.PREVIEW.equals(selectedVoucher.getMode()));
				mCodeSoundContainer.setVisibility(View.GONE);
				
				String displayInfo = isOnLine ? mVoucherDetail.getDisplayInfo(): null;
				if(displayInfo != null){
					try {
						JSONObject displayJson = new JSONObject(displayInfo);
						stripUrl = displayJson.optString("strip");
					} catch (JSONException e) {
						e.printStackTrace();
					}
				}else
					stripUrl = null;
				
				File stripFile = mAliPassServiceImpl.getPassBgImage(selectedVoucher.getMode(), stripUrl, getRootController(),
						mUserData != null ? mUserData.getAccountName() : null, mPrefixPassName, mPassName, listener);
				
				if(stripFile != null && stripFile.exists()){
					setPassBg(stripFile);
				}
			}
		}else
			voucherFront.setVisibility(View.INVISIBLE);
	}
	
	private BitmapDownloadListener listener = new BitmapDownloadListener() {
		@Override
		public void onComplete(final Bitmap bm) {
			getRootController().runOnUiThread(new Runnable() {
				@Override
				public void run() {
					if (stripUrl != null) {
						File stripFile = new File(Helper.getCachePath(getRootController(), AliPassServiceImpl.ALIPASSDIR)
								+ Helper.urlToKey(stripUrl));
						FileUtils.saveBitmap2File(bm, stripFile);

						// 读取文件
						if (stripFile != null && stripFile.exists())
							setPassBg(stripFile);
					}
				}
			});
		}
	};
	
	private void setPassBg(File stripFile) {
		int reqWidth = mFrontPage.getWidth();
		Bitmap stripBitmap = FileUtils.getImageBitmap(reqWidth, stripFile);
		if (stripBitmap != null) {
			float ratio = FileUtils.widthRetio(reqWidth, stripFile);
			Bitmap mBitmap = Bitmap.createScaledBitmap(stripBitmap, stripBitmap.getWidth(), (int) (stripBitmap.getHeight() * ratio), true);
//			if (!stripBitmap.isRecycled())
//				stripBitmap.recycle();
			mBodyContainer.setVisibility(View.VISIBLE);
			mBodyContainer.setImageBitmap(mBitmap);
		}
	}

	private void setTitleText(String title) {
		mVoucherTitle.setTextSize(getTextSize(title));
		mVoucherTitle.setText(Helper.toDBC(title));
	}

	private void setTitleFromSource(String source) {
		if(source != null && source.length() > 0)
			mBackTilte.setText(source);
	}

	private void setBgColor(int passColor) {
		if (!isAvailableVoucher(mAlipassInfo.getStatus())) {
			mContainer.setBackgroundColor(getRootController().getResources().getColor(R.color.voucher_unAvailable));
			mDivider1.setBackgroundResource(R.drawable.divider1_unavailable);
			mDivider2.setBackgroundResource(R.drawable.divider2_unavailable);
		} else {
			if(passColor != 0){
				mContainer.setBackgroundColor(passColor);
				mDivider1.setBackgroundResource(R.drawable.divider1_user_define);
			} else{
				mContainer.setBackgroundColor(getRootController().getResources().getColor(R.color.voucher_available));
				mDivider1.setBackgroundResource(R.drawable.divider1_available);
				mDivider2.setBackgroundResource(R.drawable.divider2_available);
			}
		}
	}
	
	@Override
	protected void setTitleLogoAndBg(String stauts) {
		logoImage.setImageBitmap(selectedVoucher.getCouponLogoBitmap(getRootController(), false,mUserData == null ? "" : mUserData.getAccountName()));
		logoImage.setScaleType(ScaleType.FIT_CENTER);
	}
	
	private void setTimeClockColor(int passColor) {
		Resources resource = getRootController().getResources();
		if(passColor != 0 ){
			Drawable whiteDrawable = resource.getDrawable(R.drawable.white_expire_icon);
			whiteDrawable.setBounds(0, 0, whiteDrawable.getMinimumWidth(), whiteDrawable.getMinimumHeight());
			expireTip.setCompoundDrawables(whiteDrawable, null, null, null);
		}else{
			Drawable orangeDrawable = resource.getDrawable(R.drawable.orange_expire_icon);
			orangeDrawable.setBounds(0, 0, orangeDrawable.getMinimumWidth(), orangeDrawable.getMinimumHeight());
			expireTip.setCompoundDrawables(orangeDrawable, null, null, null);
		}
	}
	
	/**
	 * 设置状态图片
	 * @param status
	 */
	private void setCouponStamp(String status) {
		if(Voucher.Status.USED.equals(status)){//已使用
			mCouponStamp.setBackgroundResource(R.drawable.coupon_stamp_used);
		} else if(Voucher.Status.EXPIRED.equals(status)){//已过期
			mCouponStamp.setBackgroundResource(R.drawable.coupon_stamp_expired);
		} else if(Voucher.Status.REFUNDED.equals(status)){//已退款
			mCouponStamp.setBackgroundResource(R.drawable.coupon_stamp_refunded);
		} else if(Voucher.Status.REFUNDING.equals(status)){//退款中
			mCouponStamp.setBackgroundResource(R.drawable.coupon_stamp_refunding);
		} else if(Voucher.Status.INVALID.equals(status)){//无效
			mCouponStamp.setBackgroundResource(R.drawable.coupon_stamp_invalidate);
		} else if(isAvailableVoucher(status)){//可用
			mCouponStamp.setBackgroundResource(R.drawable.coupon_stamp_canuse);
		}
	}
	
	private boolean mStoreQueryed;
	@Override
	protected void rotateFinished() {
		super.rotateFinished();
		if(selectedVoucher!= null){
			if(Voucher.Mode.ONLINE.equals(selectedVoucher.getMode())){
				if(!mStoreQueryed )
					queryStoreList("",voucherFrom,"1","2");
			}else{
				List<Locations> locations = mAlipassInfo.getEvoucherInfo().getLocations();
				if(locations != null && locations.size() > 0){
					mStoreListContainer.setVisibility(View.VISIBLE);
				}else
					mStoreListContainer.setVisibility(View.GONE);
			}
		}
	}

	private int getTextSize(String title) {
		int titleLenght = title.length();
		if(titleLenght <= 10){
			return 20;
		}else if(titleLenght <= 14){
			return 18;
		}else if(titleLenght <= 19){
			return 16;
		}else {
			return 14;
		}
		
	}
	
	private void setArrowBg(ImageView imageView,boolean selected){
		imageView.setImageResource(selected ?R.drawable.list_down_arrow :R.drawable.list_right_arrow);
	}
	
	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.title_slaveright:
			if(mUserData != null){
				bindPassFile();
			} else {// 跳转到登录
				Intent tIntent = new Intent(getRootController(), Login.class);
				tIntent.putExtra(Constant.PATTERNNEED, "false");
				getRootController().startActivityForResult(tIntent, Constant.REQUEST_LOGIN_BACK);
			}
			break;
		case R.id.title_left:
			if(mSchema != null)
				animateOut(mSchema,CANCLE,"未添加成功");
			break;
		case R.id.voucher_front:
			applyRotation(true,0, 90);
			doSimpleLog("alipass", BehaviourID.CLICKED, "ticketBackSideDetails", "ticketDetails", "seeTicketBackSide");
			break;
		case R.id.voucher_back:
			applyRotation(false, 360, 270);
			doSimpleLog("alipass", BehaviourID.CLICKED, "ticketDetails", "ticketBackSideDetails", "seeTicketFrontSide");
			break;
		case R.id.storeListContainer:
			if (mVoucherDetail != null) {
				mVoucherDetail.setStoreListTitle(getRootController().getResources().getText(R.string.storeList) + "");
				getRootController().navigateTo("StoreListView",mVoucherDetail);
			} else {
				List<Locations> locationList = mAlipassInfo.getEvoucherInfo().getLocations();
				if(locationList != null && locationList.size() > 0){
					navigateTo("AlipassStoreListView",locationList);
				}
			}
			doSimpleLog("alipass", BehaviourID.CLICKED, "usableShopList", "ticketBackSideDetails", "seeUsableShop");
			break;
		case R.id.disclaimer:
			if(mDisclaimerText.getVisibility() == View.GONE){
				mDisclaimerText.setVisibility(View.VISIBLE);
				mDisclaimerDivider.setVisibility(View.VISIBLE);
				mDetailView.setVisibility(View.GONE);
				setArrowBg(mDisclaimerArrow,true);
				setArrowBg(mDetailArrow,false);
				doSimpleLog("alipass", BehaviourID.CLICKED, "ticketBackSideDetails", "ticketBackSideDetails", "seeDisclaimer");
			} else {
				mDisclaimerText.setVisibility(View.GONE);
				mDisclaimerDivider.setVisibility(View.GONE);
				setArrowBg(mDisclaimerArrow,false);
			}
			break;
		case R.id.freeDetailContainer:
			if(mDetailView.getVisibility() == View.GONE){
				mDetailView.setVisibility(View.VISIBLE);
				mDisclaimerText.setVisibility(View.GONE);
				mDisclaimerDivider.setVisibility(View.GONE);
				setArrowBg(mDetailArrow,true);
				setArrowBg(mDisclaimerArrow,false);
				
				doSimpleLog("alipass", BehaviourID.CLICKED, "ticketBackSideDetails", "ticketBackSideDetails", "seePromotionalInfo");
			}else{
				setArrowBg(mDetailArrow,false);
				mDetailView.setVisibility(View.GONE);
			}
			break;
		case R.id.frontScrollView:
			if(selectedVoucher != null && !Voucher.Mode.PREVIEW.equals(selectedVoucher.getMode()))
				getRootController().finish();
			break;
		case R.id.refreshView:
			queryVoucherInfo(voucherId, voucherFrom, outBizNo);
			break;
		default:
			break;
		}
	}

	private void bindPassFile() {
		boolean bindResult = mAliPassServiceImpl.bindPassFile(mUserData.getAccountName(),mPassName);
		if(bindResult){
			animateOut(mSchema,SUCCESS,"已添加至支付宝钱包");
			enableAddButton();
			VoucherSyncHelper.getInstance(getRootController()).syncFiles(mUserData.getAccountName());
		}else{
			animateOut(mSchema,FAIL,"未添加成功");
		}
	}
	
	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if(requestCode == Constant.REQUEST_LOGIN_BACK && resultCode == Activity.RESULT_OK && mPassName != null){
			mUserData = getRootController().getUserData();
			if(mUserData != null && mAliPassServiceImpl.passFileExist(mUserData.getAccountName(),mPassName)){
				enableAddButton();
			}else
				mAddButton.postDelayed(new Runnable() {
					@Override
					public void run() {
						bindPassFile();
					}
				}, 300);
		}
	}

	private void enableAddButton() {
		mAddButton.setEnabled(false);
		mAddButton.setTextColor(getRootController().getResources().getColor(R.color.text_light_gray));
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
	}
}
