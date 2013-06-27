package com.alipay.android.biz;

import org.json.JSONObject;

import android.os.Message;

import com.alipay.android.appHall.bussiness.CallBackFilter;
import com.alipay.android.appHall.common.Defines;
import com.alipay.android.client.MessageFilter;
import com.alipay.android.client.RootActivity;
import com.alipay.android.client.constant.AlipayHandlerMessageIDs;
import com.alipay.android.client.constant.Constant;
import com.alipay.android.client.util.Responsor;
import com.alipay.android.servicebeans.BaseServiceBean;
import com.alipay.android.util.JsonConvert;

public class CommonRespHandler{
	public static boolean filter(BaseServiceBean baseServiceBean) {
		return "100".equals(baseServiceBean.resultStatus);
    }
	
	public static int getStatus(JSONObject responseJson){
		return responseJson.optInt(Defines.resultStatus);
	}
	
	public static boolean filterBizResponse(RootActivity activity,JSONObject responseJson){
		if(responseJson == null)
			return false;
		
		CallBackFilter callBackFilter = new CallBackFilter(activity);
		return !callBackFilter.processCommand(JsonConvert.Json2Map(responseJson));
	}

	public static boolean processBizSpecStatu(RootActivity activity,JSONObject responseJson){
		if(responseJson != null && !isPayResult(responseJson)){//对非快捷支付请求响应做特殊处理
			MessageFilter messageFilter = new MessageFilter(activity);
			Message message = new Message();
			Responsor responser = new Responsor(null, getStatus(responseJson), responseJson, responseJson.optString("memo"));
			message.obj = responser;
			return messageFilter.process(message);
		}
		
		return true;
	}
	
	public static boolean processSpecStatu(RootActivity activity,JSONObject responseJson){
		if(responseJson != null && isCancelInstallSafePay(responseJson) ){//对取消安装移动快捷支付响应做特殊处理
			return false;
		}else if(responseJson != null && !isPayResult(responseJson)){//对非快捷支付请求响应做特殊处理
			MessageFilter messageFilter = new MessageFilter(activity);
			Message message = new Message();
			Responsor responser = new Responsor(null, getStatus(responseJson), responseJson, responseJson.optString("memo"));
			message.obj = responser;
			return messageFilter.process(message);
		}
		
		return true;
	}
	
	public static boolean isCancelInstallSafePay(JSONObject responseJson) {
		return responseJson.optBoolean("timeOut");
	}

	private static boolean isPayResult(JSONObject responseJson){
		return responseJson.optInt(Constant.RQ_PAY) == AlipayHandlerMessageIDs.RQF_PAY;
	}
}
