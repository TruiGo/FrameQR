package com.alipay.android.net.alipayclient2;

import java.util.ArrayList;
import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.message.BasicHeader;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;
import android.content.Context;
import android.webkit.CookieManager;
import com.alipay.android.client.constant.Constant;
import com.alipay.android.net.http.HttpClient;
import com.alipay.android.util.LogUtil;

public class APHttpClient extends HttpClient
{
	static final String TAG = "APHttpClient";
	
	String mSessionId;
	Context mContext;
    public APHttpClient(Context context) {
        super(context);
        this.mContext = context;
    }

    public APHttpClient(String url, Context context) {
        super(url, context);
        this.mContext = context;
    }

	public boolean isAborted()
	{
		return false;
	}

	public void abort()
	{
	}	
	
	public String sendSynchronousRequestAsHttpResponse(
			ArrayList<BasicNameValuePair> pairs) {
		String strResponse = null;
		HttpResponse httpResponse = sendSynchronousRequestAsHttpResponse(pairs,prepareHeaders());
		if (httpResponse == null)
            return strResponse;
        
        HttpEntity entity = httpResponse.getEntity();
        strResponse = getStrFromResponse(entity);
//        strResponse = EntityUtils.toString(entity);
        LogUtil.logOnlyDebuggable(TAG, "Response " + strResponse);

        updateCookie2(httpResponse);
        
        return strResponse;
	}
	
	private ArrayList<BasicHeader> prepareHeaders()
	{
		ArrayList<BasicHeader> headers = new ArrayList<BasicHeader>();
		try
		{

			String cookie = CookieManager.getInstance().getCookie(Constant.getPucPayURL(mContext));
			if (cookie != null)
			{
				headers.add(new BasicHeader("cookie", cookie));
				if (mSessionId == null)
				{
					// initial sessionId.
					mSessionId = extractSessionID(cookie);
				}
				
				LogUtil.logOnlyDebuggable(TAG, "request cookie " + cookie);
			}
			String tCompayName = Constant.MobileCompany;
			if(tCompayName != null && !tCompayName.equals(""))
			{
				headers.add(new BasicHeader("User-Agent", tCompayName));
			}
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		
		return headers;
	}
	
    public String sendSynchronousRequest(String strReqData, boolean updateCookie) {
        String strResponse = null;
        try {
            ArrayList<BasicHeader> headers = prepareHeaders();
            HttpResponse httpResponse = super.sendSynchronousRequestAsHttpResponse(strReqData, headers);
            if (httpResponse == null)
                return strResponse;
            
            HttpEntity entity = httpResponse.getEntity();
            strResponse = getStrFromResponse(entity);
//            strResponse = EntityUtils.toString(entity);
            LogUtil.logOnlyDebuggable(TAG, "Response " + strResponse);

            if (updateCookie){
                updateCookie2(httpResponse);
            }
        } catch (Exception e) {
        	e.printStackTrace();
        }

        return strResponse;
    }
	
	public String sendSynchronousRequest(String strReqData)
	{
		return sendSynchronousRequest(strReqData, true);
	}
	
	public String sendSynchronousBizRequest(String strReqData){
		String result = sendSynchronousRequest(strReqData);
		return setNullResult(result);
	}

	private String setNullResult(String result) {
		if(result == null){
			try {
				JSONObject responseJson = new JSONObject();
				responseJson.put(Constant.RPF_RESULT_STATUS, errorType+"");
				responseJson.put(Constant.NETWORK_MEMO, "networkError");
				result = responseJson.toString();
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}
		return result;
	}
	
	private void updateCookie2(HttpResponse response)
	{
		try
		{
		Header[] cookies = response.getHeaders("set-cookie");
		//List<String> cookies = headers["set-cookie"];

		if (cookies == null)
			return;
		
		String session;
		CookieManager.getInstance().setAcceptCookie(true);

		for (int i=0; i<cookies.length; i++)
		{
			@SuppressWarnings("unused")
			String name = cookies[i].getName();
			String cookie = cookies[i].getValue();
			
			LogUtil.logOnlyDebuggable(TAG, "cookie add " + cookie);
			if (cookie.indexOf("domain=") < 0)
			{
				cookie = cookie + "; domain=" + Constant.getAlipayDomain(mContext);
			}
			
			//
			CookieManager.getInstance().setCookie(Constant.getPucPayURL(mContext), cookie);
			CookieManager.getInstance().setCookie(Constant.getAmusementURL(mContext), cookie);
			
			session = extractSessionID(cookie);
			if (session != null)
			{
				mSessionId = session;
			}
		}

		LogUtil.logOnlyDebuggable(TAG, "cookies "	+ CookieManager.getInstance().getCookie(Constant.getPucPayURL(mContext)));
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
	}

	public void updateCookie(Context context)
	{
		// CookieManager.getInstance().removeAllCookie();
		// CookieManager.getInstance().setCookie(Constant.getPucPayURL(),
		// "JSESSIONID=" + mSessionId);
		return;
	}
	
	public String getSessionId()
	{
		return mSessionId;
	}
	
	static public String extractSessionID(String cookie)
	{
		String sessionId = null;
		int pos = cookie.indexOf("JSESSIONID");
		int pos2 = -1;
		if (pos >= 0)
		{
			pos = pos + "JSESSIONID=".length();
			pos2 = cookie.indexOf(";", pos);
			if (pos2 == -1)
			{
				sessionId = cookie.substring(pos);
			}
			else
			{
				sessionId = cookie.substring(pos, pos2);
			}
		}
		return sessionId;
	}	
	public static void clearSessionCookie(){
		CookieManager.getInstance().removeSessionCookie();
	}
}
