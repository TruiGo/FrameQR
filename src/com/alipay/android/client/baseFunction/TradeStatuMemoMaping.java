package com.alipay.android.client.baseFunction;

import android.content.Context;

import com.alipay.android.client.constant.Constant;
import com.eg.android.AlipayGphone.R;

public class TradeStatuMemoMaping {
	
	public static String getMappedStatus(Context context,String resultStatus, String resultStatusMemo){
		if(Constant.STATUS_INIT.equals(resultStatus)){//等待付款
			return context.getText(R.string.wait_to_pay) + "";
		}else if(Constant.STATUS_PAID.equals(resultStatus)){//已付款，等待对方收款
			return context.getText(R.string.wait_to_receive) + "";
		}else if(Constant.STATUS_CONFIRM.equals(resultStatus)){//已确认，等待银行处理
			return context.getText(R.string.wait_bank_process) + "";
		}else if(Constant.STATUS_SUCCESS.equals(resultStatus)){//成功,等待银行处理
			return context.getText(R.string.wait_bank_process) + "";
		}else if(Constant.STATUS_TIME_OUT.equals(resultStatus) || Constant.STATUS_PAY_FAILURE.equals(resultStatus)//交易失败
				|| Constant.STATUS_UN_TRADED.equals(resultStatus) || Constant.STATUS_TRADE_FAIL.equals(resultStatus)
				|| Constant.STATUS_WF.equals(resultStatus)){
			return context.getText(R.string.transfer_fail) + "";
		}else if(Constant.STATUS_WS.equals(resultStatus)){//提现成功
			return context.getText(R.string.DealQueryDealOK) + "";
		}
		return resultStatusMemo;
	}
	/**
	 * 获得每种状态对应的映射文案
	 */
	public static String getMapedTip(Context context,String statusCode) {
		if(Constant.STATUS_INIT.equals(statusCode)){//等待付款
			return context.getText(R.string.wait_to_pay) + "";
		}else if(Constant.STATUS_PAID.equals(statusCode)){//已付款，等待对方收款
			return context.getText(R.string.pay_success) + "";
		}else if(Constant.STATUS_CONFIRM.equals(statusCode)){//已确认，对方确认收款
			return context.getText(R.string.confirm_receive) + "";
		}else if(Constant.STATUS_SUCCESS.equals(statusCode)){//成功,等待银行处理
			return context.getText(R.string.commit_bank_process) + "";
		}else if(Constant.STATUS_UN_TRADED.equals(statusCode)){//未确认
			return context.getText(R.string.not_confirm_transfer) +"";
		}else if(Constant.STATUS_PAY_FAILURE.equals(statusCode)|| Constant.STATUS_TIME_OUT.equals(statusCode)//交易失败 
				|| Constant.STATUS_TRADE_FAIL.equals(statusCode) || Constant.STATUS_WF.equals(statusCode)){
			return context.getText(R.string.transfer_state_fail) + "";
		}else if(Constant.STATUS_WS.equals(statusCode)){//提现成功
			return context.getText(R.string.transfer_success) + "";
		}
		return "";
	}
}
