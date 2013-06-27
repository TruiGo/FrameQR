package com.alipay.android.servicebeans;

import org.json.JSONException;
import org.json.JSONObject;

import com.alipay.android.client.constant.Constant;
import com.alipay.android.net.alipayclient2.APHttpClient;

public class ReportLBSLocationInfo extends BaseServiceBean {

    /** 经度 */
    double longitude;
    /** 纬度 */
    double latitude;
    /** 业务参数 */
    String params;
    
    String  cellId;
    
    String bizType;
    public ReportLBSLocationInfo() {
        operationType = ServiceBeanConstants.I_AM_HERE;
    }

    @Override
    protected String buildRequestAsString() {
        try {
            JSONObject requestJson = prepareRequest();
            requestJson.put(Constant.LBS_BUSINESSTYPE, bizType);
            requestJson.put(Constant.LBS_LONGITUDE, longitude);
            requestJson.put(Constant.LBS_LATITUDE, latitude);
            requestJson.put(Constant.LBS_PARAMS,new JSONObject(params));
            requestJson.put(Constant.LBS_CELLID,new JSONObject(cellId));
            requestJson.put(Constant.RQF_SMSTRANS_SESSIONID, "");
            return requestJson.toString();
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public void initParams(String... taskParams) {
        super.initParams(taskParams);
        longitude = Double.parseDouble(taskParams[0]);
        latitude = Double.parseDouble(taskParams[1]);
        params = taskParams[2];
        cellId = taskParams[3];
        bizType = taskParams[4];
    }
    
	@Override
	public String doX() {
		String alipayUrl = Constant.getAlipayURL(super.mContext)
				.replace("https", "http").replace("mobilecs.htm", "mtk.htm");
		APHttpClient aPHttpClient = new APHttpClient(alipayUrl, mContext);
		String response = aPHttpClient
				.sendSynchronousBizRequest(buildRequestAsString());
		extractBasicResponse(response);
		return response;
	}

	@Override
	protected void extractBasicResponse(String response) {
		try {
			JSONObject responseJson = new JSONObject(response);
			resultStatus = responseJson.optString("resultStatus");
			memo = responseJson.optString("memo");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
