package com.alipay.android.dao;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONObject;

import com.alipay.android.client.constant.Constant;
import com.alipay.android.ui.bean.TransferReceiver;

public class TransferReceiverDAO {
	public List<TransferReceiver> convertJson2List(JSONArray historyRecordList) {
		List<TransferReceiver> receiverList = new ArrayList<TransferReceiver>();
		for (int i = 0; i < historyRecordList.length(); i++) {
			JSONObject curTradeJson = historyRecordList.optJSONObject(i);
			TransferReceiver transferReceiver = new TransferReceiver();
			String bizType = curTradeJson.optString(Constant.RPF_BIZTYPE);

			if (Constant.TRADE.equals(bizType)) {
				transferReceiver.bizType = Constant.TRADE;
				transferReceiver.recvAccount = curTradeJson.optString(Constant.RECVACCOUNT);
				if(transferReceiver.recvAccount.matches("1\\d{10}")){
					transferReceiver.recvMobile = transferReceiver.recvAccount;
				}
			} else if (Constant.TRANSFER.equals(bizType)) {
				transferReceiver.bizType = Constant.TRANSFER;
				transferReceiver.recvMobile = curTradeJson.optString(Constant.RECVMOBILE);
			} else 
				continue;
			
			transferReceiver.userId = curTradeJson.optString(Constant.USERID);
			transferReceiver.recvName = curTradeJson.optString(Constant.RECVNAME);
			transferReceiver.recvRealName = curTradeJson.optString(Constant.RECVNAME);
			receiverList.add(transferReceiver);
		}
		return receiverList;
	}
}
