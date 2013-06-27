package com.alipay.android.servicebeans;

import org.json.JSONException;
import org.json.JSONObject;

import com.alipay.android.client.constant.Constant;

public class VerifyExpressCard extends BaseServiceBean {

	String mInstId; //银行短名称,如ICBC
	String mCardType; //DC—借记卡，CC—贷记卡
	String mCardNo;  //如果是信用卡则为索引卡号
	String mCardNoType;
	String mExpireDate;
	String mCvv2;
	String mHolderName; //如为实名认证用户，由支付宝账号带入
	String mMobile;
	String mCertNo;
	String mCertType;
	
	public VerifyExpressCard() {
		operationType = ServiceBeanConstants.OPERATION_TYPE_VERIFYEXPRESSCARD;
	}
	
	@Override
	protected String buildRequestAsString() {
		try {
			JSONObject requestJson = prepareRequest();
			requestJson.put(Constant.INSTID, mInstId);
			requestJson.put(Constant.CARDTYPE, mCardType);
			requestJson.put(Constant.CARDNO, mCardNo);
			requestJson.put(Constant.CARDNOTYPE, mCardNoType);

			requestJson.put(Constant.EXPIREDATE, mExpireDate);
			requestJson.put(Constant.CVV2, mCvv2);
			requestJson.put(Constant.HOLDERNAME, mHolderName);
			requestJson.put(Constant.MOBILE, mMobile);
			requestJson.put(Constant.CERTNO, mCertNo);
			requestJson.put(Constant.CERTTYPE, mCertType);
			
			return requestJson.toString();
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return null;
	}

	@Override
	public void initParams(String... taskParams) {
		super.initParams(taskParams);
		
		if (10 == taskParams.length) {
			mInstId = taskParams[0];
			mCardType = taskParams[1];
			mCardNo = taskParams[2];
			mCardNoType = taskParams[3];
			mExpireDate = taskParams[4];
			mCvv2 = taskParams[5];
			mHolderName = taskParams[6];
			mMobile = taskParams[7];
			mCertNo = taskParams[8];
			mCertType = taskParams[9];
		}
	}
}
