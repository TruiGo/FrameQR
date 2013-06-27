package com.alipay.android.servicebeans;

import org.json.JSONException;
import org.json.JSONObject;

import com.alipay.android.client.constant.Constant;

public class DeleteCardWithoutPWD extends BaseServiceBean {

	String mInstId; //银行短名称,如ICBC
	String mCardType; //DC—借记卡，CC—贷记卡
	String mCardNo;  //如果是信用卡则为索引卡号
	String mActionType; //306 ：从信用卡还款已存列表删除 307 ：从CIF提现设置卡列表删除

	public DeleteCardWithoutPWD() {
		operationType = ServiceBeanConstants.OPERATION_TYPE_DELCARDWITHOUTPWD;
	}
	
	@Override
	protected String buildRequestAsString() {
		try {
			JSONObject requestJson = prepareRequest();
			requestJson.put(Constant.CARDNO, mCardNo);
			requestJson.put(Constant.CARDTYPE, mCardType);
			requestJson.put(Constant.INSTID, mInstId);
			requestJson.put(Constant.ACTIONTYPE, mActionType);
			
			return requestJson.toString();
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return null;
	}

	@Override
	public void initParams(String... taskParams) {
		super.initParams(taskParams);
		
		if (4 == taskParams.length) {
			mCardNo = taskParams[0];
			mCardType = taskParams[1];
			mInstId = taskParams[2];
			mActionType = taskParams[3];
		}
	}

}
