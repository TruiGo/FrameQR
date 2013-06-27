package com.alipay.android.biz;

import org.json.JSONException;
import org.json.JSONObject;

import com.alipay.android.client.RootActivity;
import com.alipay.android.client.constant.Constant;
import com.alipay.android.client.util.BaseHelper;
import com.alipay.android.servicebeans.BaseServiceBean;
import com.alipay.android.servicebeans.ServiceBeanConfig;
import com.alipay.android.servicebeans.ServiceBeanFactory;

public class TransferBiz {
    public static String       BIZ_TAG_P2P     = "p2pPay";
    public static String       BIZ_TAG_P2PHONE = "p2phone";
    private ServiceBeanFactory serviceFactory  = ServiceBeanFactory.getInstance();

    /**
     * 获取转账历史
     * @param clientID
     * @param sessionId
     * @param queryTradeType
     * @return
     */
    public String loadTransferList() {
        BaseServiceBean getTransferList = serviceFactory
            .getBean(ServiceBeanConfig.BEAN_NAME_GETTRANSFERLIST);
        getTransferList.initParams();
        return getTransferList.doX();
    }

    /**
     * 验证手机号绑定关系
     * @param clientID
     * @param sessionId
     * @param phoneNumber
     * @return
     */
    public String queryPhoneBinding(String phoneNumber) {
        BaseServiceBean queryMobileBinding = serviceFactory
            .getBean(ServiceBeanConfig.BEAN_NAME_PHONEBINDINGQUERY);

        // 业务联网参数设置
        queryMobileBinding.initParams(phoneNumber);
        return queryMobileBinding.doX();
    }

    /**
     * p2p付款
     */
    public String p2pPay(String toPayAmount, String toPayReason, String toPayAccount,
                         String smsMobileNo, String isAddContact, String toPayMemo,
                         RootActivity activity) {
        BaseServiceBean createP2pOrder = serviceFactory
            .getBean(ServiceBeanConfig.BEAN_NAME_CREATEP2PORDER);

        // 业务联网参数设置
        createP2pOrder.initParams(toPayAmount, toPayReason, toPayAccount, smsMobileNo,
            isAddContact, toPayMemo);
        String responseStr = createP2pOrder.doX();

        if (CommonRespHandler.filter(createP2pOrder)) {
            JSONObject p2pResponse;
            try {
                p2pResponse = new JSONObject(responseStr);
                String tradeNo = p2pResponse.optString(Constant.RQF_TRADE_NO);

                return BaseHelper.payBizDeal(activity, null, tradeNo, activity.getExtToken(), null,
                    "trade", "");
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return null;
        } else {
            return responseStr;
        }
    }

    public String p2phonePay(String receiverPhone, String receiverName, String transferAmount,
                             String bankShortName, String transferSpeedName, String reason,String toPayMemo,
                             RootActivity activity) {
        BaseServiceBean createP2PhoneOrder = serviceFactory
            .getBean(ServiceBeanConfig.BEAN_NAME_CREATEP2PHONEORDER);

        // 业务联网参数设置
        createP2PhoneOrder.initParams(receiverPhone, receiverName, transferAmount, bankShortName,
            transferSpeedName, reason,toPayMemo);
        String responseStr = createP2PhoneOrder.doX();

        if (CommonRespHandler.filter(createP2PhoneOrder)) {
            JSONObject p2pResponse;
            try {
                p2pResponse = new JSONObject(responseStr);
                String billNo = p2pResponse.optString(Constant.RPF_BILLNO);

                return BaseHelper.payBizDeal(activity, null, billNo, activity.getExtToken(), null,
                    "cell_num_transfer", "");
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return null;
        } else {
            return responseStr;
        }
    }

    public String getTransformFee(String transferSpeedName, String bankShortName,
                                  String transferAmount) {
        BaseServiceBean consultChargeFee = serviceFactory
            .getBean(ServiceBeanConfig.BEAN_NAME_CONSULTCHARGEFEE);
        consultChargeFee.initParams(transferSpeedName, bankShortName, transferAmount);

        return consultChargeFee.doX();
    }

	public String getReceiveTime(String bankShortName) {
		BaseServiceBean getReceiveTime = serviceFactory.getBean(ServiceBeanConfig.BEAN_NAME_SUPPORTTRANSFERBANKLIST);
		getReceiveTime.initParams(bankShortName);

		return getReceiveTime.doX();
	}
	
	public String getTransferAccountInfo(String toPayAccount) {
		BaseServiceBean getAccountInfo = serviceFactory
				.getBean(ServiceBeanConfig.BEAN_NAME_GETACCOUNTINFO);
		getAccountInfo.initParams(toPayAccount);

		return getAccountInfo.doX();
	}
}
