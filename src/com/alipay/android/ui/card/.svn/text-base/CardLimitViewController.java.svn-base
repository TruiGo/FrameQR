package com.alipay.android.ui.card;

import org.json.JSONArray;
import org.json.JSONObject;

import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.alipay.android.biz.CardManagerBiz;
import com.alipay.android.biz.CommonRespHandler;
import com.alipay.android.client.constant.Constant;
import com.alipay.android.ui.bean.BankCardInfo;
import com.alipay.android.ui.framework.BaseViewController;
import com.alipay.android.ui.framework.BaseViewController.BizAsyncTask;
import com.alipay.android.util.JsonConvert;
import com.eg.android.AlipayGphone.R;

public class CardLimitViewController extends BaseViewController {
	
	public static final String BANKSHORTNAME = "bankShortName";
	public static final String CARDTYPE = "cardType";
	private String mBankShortName;
	private String mCardType;
	
	@Override
	protected void onCreate() {
		super.onCreate();
		mView = LayoutInflater.from(getRootController()).inflate(R.layout.alipay_express_cardlimit, null, false);
		addView(mView, null);
		
		TextView title = (TextView)findViewById(R.id.title_text);
		title.setText(R.string.express_limit_title);

		BankCardInfo cardInfo = (BankCardInfo)params;
		if (null != cardInfo) {
			mBankShortName = cardInfo.getBankMark();
			mCardType = cardInfo.getCardType();
			
			loadVariables();
			requestCardLimit();
		} else {
			//提示错误，然后返回
		}
	}
	
	private TextView mLimit_SmallSingle;
	private TextView mLimit_SmallDaily;
	private TextView mLimit_SmallMonthly;
	private TextView mLimit_BigSingle;
	private TextView mLimit_BigDaily;
	private TextView mLimit_BigMonthly;
	private void loadVariables() {
		mLimit_SmallSingle = (TextView)findViewById(R.id.smallSingleLimitValue);
		mLimit_SmallDaily = (TextView)findViewById(R.id.smallDailyLimitValue);
		mLimit_SmallMonthly = (TextView)findViewById(R.id.smallMonthlyLimitValue);
		mLimit_BigSingle = (TextView)findViewById(R.id.bigSingleLimitValue);
		mLimit_BigDaily = (TextView)findViewById(R.id.bigDailyLimitValue);
		mLimit_BigMonthly = (TextView)findViewById(R.id.bigMonthlyLimitValue);
		
		if (Constant.CARDTYPE_CC.equals(mCardType)) {
			View smallTitleCC = findViewById(R.id.smallLimitTitle_cc);
			smallTitleCC.setVisibility(View.VISIBLE);
			View bigTitleCC = findViewById(R.id.bigLimitTitle_cc);
			bigTitleCC.setVisibility(View.VISIBLE);
		} else {
			View smallTitleDC = findViewById(R.id.smallLimitTitle_dc);
			smallTitleDC.setVisibility(View.VISIBLE);
			View bigTitleDC = findViewById(R.id.bigLimitTitle_dc);
			bigTitleDC.setVisibility(View.VISIBLE);
		}
		
	}
	
	private BizAsyncTask mGetCardLimitTask = null;

	private void requestCardLimit() {
		if (null != mGetCardLimitTask && AsyncTask.Status.FINISHED != mGetCardLimitTask.getStatus()) {
			mGetCardLimitTask.cancel(true);
		}
		
		mGetCardLimitTask = new BizAsyncTask(CardManagerBiz.BIZTAG_GETEXPRESSCARDLIMIT, true);
		mGetCardLimitTask.execute();
	}
	
	/**
	 * 异步任务
	 */
	@Override
	protected Object doInBackground(String bizType, String... params) {
		if(bizType.equalsIgnoreCase(CardManagerBiz.BIZTAG_GETEXPRESSCARDLIMIT)){
			
			return new CardManagerBiz().getExpressCardLimit(mBankShortName, mCardType);
       	}
		
		return super.doInBackground(bizType, params);
	}
	
	
	@Override
	protected void onPostExecute(String bizType, Object result) {
		JSONObject responseJson = JsonConvert.convertString2Json((String)result);

    	if(!CommonRespHandler.processSpecStatu(getRootController(), responseJson)){
			return;
		}
		if(CommonRespHandler.filterBizResponse(getRootController(), responseJson)){

			if (bizType.equalsIgnoreCase(CardManagerBiz.BIZTAG_GETEXPRESSCARDLIMIT)) {
				try {
				JSONObject cardLimitObject = responseJson.optJSONArray("bankLimitList")
						.optJSONObject(0)
						.optJSONArray("cardLimitList")
				        .optJSONObject(0);
				
				//这里位置颠倒了，请勿修改
				mLimit_SmallSingle.setText(cardLimitObject.optString("limitForBigSingle"));
				mLimit_SmallDaily.setText(cardLimitObject.optString("limitForBigDaily"));
				mLimit_SmallMonthly.setText(cardLimitObject.optString("limitForBigMonthly"));
				mLimit_BigSingle.setText(cardLimitObject.optString("limitForSmallSingle"));
				mLimit_BigDaily.setText(cardLimitObject.optString("limitForSmallDaily"));
				mLimit_BigMonthly.setText(cardLimitObject.optString("limitForSmallMonthly"));

			} catch (NullPointerException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			}
		}
	
		super.onPostExecute(bizType, result);
	}

}
