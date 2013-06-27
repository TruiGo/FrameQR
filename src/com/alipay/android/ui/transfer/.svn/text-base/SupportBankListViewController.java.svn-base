package com.alipay.android.ui.transfer;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONObject;

import android.graphics.Color;
import android.os.AsyncTask;
import android.view.LayoutInflater;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.alipay.android.biz.CommonRespHandler;
import com.alipay.android.biz.TransferBiz;
import com.alipay.android.servicebeans.SupportTransferBankList;
import com.alipay.android.ui.framework.BaseViewController;
import com.alipay.android.util.JsonConvert;
import com.eg.android.AlipayGphone.R;

public class SupportBankListViewController extends BaseViewController{
	private LinearLayout twoHourContainer;
	private LinearLayout nextDayContainer;
	private BizAsyncTask receiveTimeTask; 
	private List<String> inTwoHours = new ArrayList<String>();
	private List<String> nextDay = new ArrayList<String>();
	
	@Override
	protected void onCreate() {
		mView  = LayoutInflater.from(getRootController()).inflate(R.layout.transfer_receive_time, null, false);
		
		addView(mView, null);
		loadAllVariables();
		sendReceiveReq();
	}

	private void loadAllVariables() {
		twoHourContainer =  (LinearLayout) findViewById(R.id.twoHourContainer);
		nextDayContainer = (LinearLayout) findViewById(R.id.nextDayContainer);
	}
	
	private void sendReceiveReq(){
		if(receiveTimeTask != null && receiveTimeTask.getStatus() != AsyncTask.Status.FINISHED)
			receiveTimeTask.cancel(true);
		
		receiveTimeTask =  new BizAsyncTask(SupportTransferBankList.BIZ_TAG);
		receiveTimeTask.execute();
	}
	
	@Override
	protected Object doInBackground(String bizType, String... params) {
		if(SupportTransferBankList.BIZ_TAG.equals(bizType)){
			return new TransferBiz().getReceiveTime("");
		}
		return super.doInBackground(bizType, params);
	}
	
	@Override
	protected void onPostExecute(String bizType, Object result) {
		if(SupportTransferBankList.BIZ_TAG.equals(bizType)){
			JSONObject responseJson = JsonConvert.convertString2Json((String)result);
        	if(!CommonRespHandler.processSpecStatu(getRootController(), responseJson)){
        		if(CommonRespHandler.getStatus(responseJson) == 0){
        			getRootController().pop();
        		}
				return;
			}
        	
			if(CommonRespHandler.filterBizResponse(getRootController(), responseJson)){
				JSONArray respArray = responseJson.optJSONArray("supportBankList");
				if(respArray != null){
					for (int i = 0; i < respArray.length(); i++) {
						JSONObject curObject = respArray.optJSONObject(i);
						String speedList = curObject.optString("speedList");
						String bankName = curObject.optString("bankName");
						List<String> speeds = Arrays.asList(speedList.split(":"));
						if (speeds.contains("IN_TWO_HOURS") && speeds.contains("NEXT_DAY")) {
							inTwoHours.add(bankName);
						} else if (speeds.contains("NEXT_DAY") && !speeds.contains("IN_TWO_HOURS")) {
							nextDay.add(bankName);
						}
					}
				}
				
				twoHourContainer.removeAllViews();
				for(String twoHourStr : inTwoHours){
					twoHourContainer.addView(initNewTextView(twoHourStr));
				}
				
				nextDayContainer.removeAllViews();
				for(String nextDayStr : nextDay){
					nextDayContainer.addView(initNewTextView(nextDayStr));
				}
			}
		}
		super.onPostExecute(bizType, result);
	}

	private TextView initNewTextView(String twoHourStr) {
		TextView tvTemp = new TextView(getRootController());
		tvTemp.setText(twoHourStr);
		tvTemp.setTextSize(16);
		tvTemp.setTextColor(Color.parseColor("#333333"));
		tvTemp.setPadding(0, 10, 0, 0);
		return tvTemp;
	}
	
	
}
