package com.alipay.android.ui.voucher;

import android.app.Activity;
import android.os.AsyncTask;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateInterpolator;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.alipay.android.appHall.Helper;
import com.alipay.android.common.data.ConfigData;
import com.alipay.android.log.AlipayLogAgent;
import com.alipay.android.log.Constants.BehaviourID;
import com.alipay.android.longlink.LongLinkServiceManager;
import com.alipay.android.servicebeans.GetStoreList;
import com.alipay.android.ui.adapter.AvailableVoucherAdapter;
import com.alipay.android.ui.aniutil.Rotate3dAnimation;
import com.alipay.android.ui.aniutil.Rotate3dAnimationListener;
import com.alipay.android.ui.aniutil.RotateAnimationListener;
import com.alipay.android.ui.bean.Voucher;
import com.alipay.android.ui.bean.VoucherDetail;
import com.alipay.android.ui.framework.BaseViewController;
import com.eg.android.AlipayGphone.R;

public abstract class DetailViewController extends BaseViewController{
	protected BizAsyncTask queryTask;
	protected final String BIZTYPE_GETVOUCHERDETAIL = "biztype_getVoucherDetail";
	protected boolean verifyResult;
	protected ViewGroup mContainer;
	protected ViewGroup mFrontPage;
	protected ViewGroup mBackPage;
	protected ImageView logoImage;
	protected TextView statusText;
	protected TextView storeNameText;
	protected TextView voucherNameText;
	protected TextView expireTip;
	protected ImageView voucherFront;
	protected ImageView voucherBack;
	protected TextView mExpireTimeText;//"有效期:" TextView
	protected TextView mVoucherFromText;
	protected LongLinkServiceManager longLinkServiceManager;
	protected AvailableVoucherAdapter mVoucherDetailAdapter;
	protected VoucherDetail mVoucherDetail;
	protected LinearLayout mQueryFailView;
	protected ImageView mRefreshView;
	
	protected VoucherDetailRotateListener mVoucherDetailRotateListener;
	
	public boolean onKeyDown(int keyCode, android.view.KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			if(mFrontPage.getVisibility() == View.GONE && mBackPage.getVisibility() == View.VISIBLE){
				applyRotation(false, 360, 270);
				return true;
			}
		} else {
			if (verifyResult)
				getRootController().setResult(Activity.RESULT_OK);

			AlipayLogAgent.writeLog(getRootController(), BehaviourID.CLICKED, "walletTicket", "myTicketList", "myTicketDetails", "backIcon");
		}
		return super.onKeyDown(keyCode, event);
	};
	
	protected void applyRotation(boolean front,float start, float end) {
        // Find the center of the container
        final float centerX = mContainer.getWidth() / 2.0f;
        final float centerY = mContainer.getHeight() / 2.0f;

        // Create a new 3D rotation with the supplied parameter
        // The animation listener is used to trigger the next animation
        final Rotate3dAnimation rotation =
                new Rotate3dAnimation(start, end, centerX, centerY, 310.0f, true);
        rotation.setDuration(500);
        rotation.setFillAfter(true);
        rotation.setInterpolator(new AccelerateInterpolator());
        Rotate3dAnimationListener animationListener = new Rotate3dAnimationListener(front,mContainer);
        animationListener.setmRotateAnimationListener(mVoucherDetailRotateListener);
        rotation.setAnimationListener(animationListener);
        mContainer.startAnimation(rotation);
    }
	
	protected abstract void loadAllVariables();
	
	protected void loadSameViews(){
		mFrontPage =  (ViewGroup) mView.findViewById(R.id.frontPage);
		mBackPage = (ViewGroup) mView.findViewById(R.id.backPage);
		logoImage = (ImageView) mView.findViewById(R.id.voucherLogo);
		statusText = (TextView) mView.findViewById(R.id.voucherStatu);
		storeNameText = (TextView) mView.findViewById(R.id.storeName);
		voucherNameText = (TextView) mView.findViewById(R.id.voucherName);
		expireTip = (TextView) mView.findViewById(R.id.expireTip);
		mContainer = (ViewGroup) mView.findViewById(R.id.container);
//		mContainer.setPersistentDrawingCache(ViewGroup.PERSISTENT_ANIMATION_CACHE);
		mVoucherDetailRotateListener = new VoucherDetailRotateListener();
		voucherFront = (ImageView) mView.findViewById(R.id.voucher_front);
		voucherBack = (ImageView) mView.findViewById(R.id.voucher_back);
		mExpireTimeText = (TextView) mView.findViewById(R.id.expireTimeText);
		mVoucherFromText = (TextView) mView.findViewById(R.id.voucherFromText);
		
		
		//设置界面标题信息
		TextView titleTextView = (TextView) mView.findViewById(R.id.title_text);
		titleTextView.setText(R.string.voucherDetail);
		
		mQueryFailView = (LinearLayout) mView.findViewById(R.id.voucherQueryFail);
		mRefreshView = (ImageView) mQueryFailView.findViewById(R.id.refreshView);
	}
	
	protected void setVoucherSource(String voucherFromDes,String voucherSource) {
		mVoucherFromText.setVisibility(View.VISIBLE);
		String sourceStr = "";
		if(voucherFromDes != null && !"".equals(voucherFromDes)){
			sourceStr = "来自: "+voucherFromDes;
			mVoucherFromText.setText(sourceStr);
		}else if(voucherSource != null && !"".equals(voucherSource)){
			if("1".equals(voucherSource)){
				sourceStr = "聚划算";
			}else if("2".equals(voucherSource)){
				sourceStr = "天猫";
			}else if("3".equals(voucherSource)){
				sourceStr = "淘宝";
			}else if("4".equals(voucherSource)){
				sourceStr = "支付宝";
			}
			sourceStr = "券来自" + sourceStr;
			mVoucherFromText.setText(sourceStr);
		}else{
			mVoucherFromText.setVisibility(View.INVISIBLE);
		}
	}

	/**
	 * 设置标题信息
	 * @param status
	 * @param expiredEndTime
	 * @param refundApplyTime
	 * @param refundFinishTime
	 * @param beginDate
	 * @param endDate
	 */
	protected void setTimeZone(String status,String expiredEndTime,String refundApplyTime,String refundFinishTime,
			String beginDate,String endDate) {
		if(Voucher.Status.USED.equals(status)){//已使用
			setUsedTime();
		} else if(Voucher.Status.EXPIRED.equals(status)){//已过期
			setTimeTipText("过期时间:",expiredEndTime != null ? expiredEndTime : "null");
		} else if(Voucher.Status.REFUNDED.equals(status)){//已退款
			setTimeTipText("退款时间:",refundFinishTime != null ? refundFinishTime : "null");
		} else if(Voucher.Status.REFUNDING.equals(status)){//退款中
			setTimeTipText("申请时间:",refundApplyTime != null ? refundApplyTime : "null");
		} else if(Voucher.Status.INVALID.equals(status)){//无效
			setTimeTipText("失效时间:",expiredEndTime != null ? expiredEndTime : "null");
		} else if(isAvailableVoucher(status)){//可用
			if(beginDate != null && endDate != null)
				mExpireTimeText.setText(Helper.toDBC("有效期:"+beginDate.replace("-", ".") + "~"+ endDate.replace("-", ".")));
		}
	}
	
	//已使用券需要根据核销历史确认时间，延迟实现
	protected abstract void setUsedTime();
	
	protected void setTimeTipText(String prefix,String time) {
		if("null".equals(time) || time == null){
			return;
		}
		
		if(time != null && time.length() > 10){
			time = (String) time.subSequence(0, 10);
		}
		mExpireTimeText.setText(prefix + time);
	}

	public final class VoucherDetailRotateListener implements RotateAnimationListener{
		@Override
		public void onFrontToBack() {
			mFrontPage.setVisibility(View.GONE);
			mBackPage.setVisibility(View.VISIBLE);
		}

		@Override
		public void onBackToFront() {
			mFrontPage.setVisibility(View.VISIBLE);
			mBackPage.setVisibility(View.GONE);
		}

		@Override
		public void onRotateFinished() {
			rotateFinished();
		}
	}
	
	protected boolean isAvailableVoucher(String status){
		return Voucher.Status.SOON_EXPIRED.equals(status)
				|| Voucher.Status.CAN_USE.equals(status);
	}
	
	protected boolean isRefoundVoucher(String status){
		return Voucher.Status.REFUNDED.equals(status)
				|| Voucher.Status.REFUNDING.equals(status);
	}
	
	/**
	 * 设置标题
	 */
	protected void setTitleContent(String status,Integer leftDays,String voucherName) {
		setTitleLogoAndBg(status);
		if(leftDays == -1){
			expireTip.setVisibility(View.INVISIBLE);
		}else {
			expireTip.setVisibility(View.VISIBLE);
			if(leftDays == 0){
				expireTip.setText("今天到期");
			}else if(leftDays > 0){
				expireTip.setText("剩"+ leftDays +"天");
			}
		}
		
		statusText.setText(Voucher.Status.STATUSMAP.get(status));
		voucherNameText.setText(voucherName);
		voucherNameText.setTextSize(18);
		storeNameText.setVisibility(View.GONE);
	}
	
	//根据状态设置背景和文字
	protected void setTitleLogoAndBg(String stauts){}
	
	protected void queryVoucherInfo(String voucherId,String voucherFrom,String outBizNo){
		if(queryTask != null && queryTask.getStatus() != AsyncTask.Status.FINISHED)
			queryTask.cancel(true);
		queryTask = new BizAsyncTask(BIZTYPE_GETVOUCHERDETAIL);
		ConfigData configData = getRootController().getConfigData();
		queryTask.execute(voucherId,voucherFrom,outBizNo,configData.getScreenWidth()+"*"+ configData.getScreenHeight(),configData.getUserAgent());
	}
	
	private BizAsyncTask loadTask;
	protected void queryStoreList(String cityId,String voucherFrom,String currentPage,String pageCount) {
		if(mVoucherDetail != null){
			if(loadTask != null && loadTask.getStatus() != AsyncTask.Status.FINISHED)
				return;
			loadTask = new BizAsyncTask(GetStoreList.BIZ_TAG);
			loadTask.execute(mVoucherDetail.getGoodsId(),cityId,voucherFrom,"1","2");
		}
	}
	
	/**
	 * 正面跳转到反面成功后的回调
	 */
	protected void rotateFinished(){
		AlipayLogAgent.writeLog(getRootController(), BehaviourID.CLICKED, "walletTicket", "myTicketDetails", "myTicketDetails",
				"seeMyTicketBackSide");
	}
	
	@Override
	public void onDestroy() {
		if(longLinkServiceManager != null){
			longLinkServiceManager.unRegisterUserCallback();
		}
		
		if(mVoucherDetailAdapter != null)
			mVoucherDetailAdapter.stopsenddata(); 
	};
}
