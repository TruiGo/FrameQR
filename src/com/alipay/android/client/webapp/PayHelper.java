/**
 * 
 */
package com.alipay.android.client.webapp;

import org.json.JSONObject;

import android.os.Handler;
import android.os.Message;

import com.alipay.android.client.MessageFilter;
import com.alipay.android.client.constant.AlipayHandlerMessageIDs;
import com.alipay.android.client.constant.Constant;
import com.alipay.android.client.util.BaseHelper;
import com.alipay.android.client.util.Responsor;
import com.eg.android.AlipayGphone.R;

/**
 * @author sanping.li@alipay.com
 *
 */
public class PayHelper {
	private webActivity activity;
	private MessageFilter mMessageFilter;
	
	private String tradeNo;
	private String partnerID = "";
	private String bizType = "trade"; 
	private String bizSubType = "";

	private Handler mHandler = new Handler(){

		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case AlipayHandlerMessageIDs.CREATE_BILL_NO:
				Responsor responsor = (Responsor) msg.obj;
				JSONObject jsonResponse = responsor.obj;

				boolean tResultOK =  mMessageFilter.process(msg);
				if ((tResultOK) && (!activity.getDataHelper().isCanceled())){
					tradeNo = jsonResponse.optString(Constant.RQF_TRADE_NO);
					
					safePay();
				}
				activity.closeProgress();
				break;

			default:
				break;
			}
			super.handleMessage(msg);
		}
		
	};
	
	public PayHelper(webActivity activity) {
		this.activity = activity;
		mMessageFilter= new MessageFilter(activity);
	}

	/**
	 * 创建订单
	 */
	public void dopay(String orderToken){
		activity.openProcessDialog(activity.getResources().getString(R.string.PleaseWait));
		
		activity.getDataHelper().sendCreateOrder(mHandler,AlipayHandlerMessageIDs.CREATE_BILL_NO,orderToken);
	}
	
	/**
	 * 调快捷支付
	 */
	private void safePay(){
		BaseHelper.payDeal(activity, activity.getmHandler(), activity.getmProgress(), tradeNo, activity.getToken(), partnerID, bizType, bizSubType);
	}
}
