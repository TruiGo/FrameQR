package com.alipay.android.client.webapp;



import java.net.URLDecoder;
import java.util.ArrayList;

import org.apache.http.NameValuePair;
import org.apache.http.client.utils.URLEncodedUtils;
import org.apache.http.message.BasicNameValuePair;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.KeyEvent;
import android.view.View;
import android.view.WindowManager;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.alipay.android.client.Main;
import com.alipay.android.client.RootActivity;
import com.alipay.android.client.constant.AlipayHandlerMessageIDs;
import com.alipay.android.client.constant.Constant;
import com.alipay.android.client.util.BaseHelper;
import com.alipay.android.client.util.LephoneConstant;
import com.alipay.android.comon.component.ProgressDiv;
import com.alipay.android.core.MsgAction;
import com.alipay.android.log.AlipayLogAgent;
import com.alipay.android.log.Constants;
import com.alipay.android.log.StorageStateInfo;
import com.alipay.android.safepay.MobileSecurePayer;
import com.alipay.android.safepay.SafePayResultChecker;
import com.eg.android.AlipayGphone.R;

/**
 * @author sanping.li@alipay.com
 *
 */
public class webActivity extends RootActivity {
	public static final String TAG = "webapp";
	
	public static final int RES_CODE_AUTHO = 10;
	
	public static final String WEBAPP_URL = "url";
	public static final String WEBAPP_NAME = "name";

	public static final String WEBAPP = "webActivity";

	public static final String WEBAPP_CHANNEL_ID = "10001";
	
	private WebView webView;
	private TextView mTitleName;
	private Client client;
	private String url;
	private String title;
	private String callBackUrl;
	private String failUrl;
	private String succesUrl;//交易成功
	private ProgressDiv mProgress;
    private ProgressBar mProgressBar;
	private StorageStateInfo storageStateInfo;
	
	private Handler mHandler = new Handler(){

		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case AlipayHandlerMessageIDs.RQF_PAY:
				String strRet = (String)msg.obj;
				SafePayResultChecker resultChecker = new SafePayResultChecker(strRet);
				String status = resultChecker.getReturnStr("resultStatus");
				if(status != null &&status.equals(MobileSecurePayer.SAFEPAY_STATE_OK)&&resultChecker.isPayOk()){

	                if(Constant.OPERATION_UID!=null){
	                    getMBus().sendMsg("", Constant.OPERATION_UID, MsgAction.ACT_LAUNCH, null);
	                    finish();
	                    return;
	                }
	                
					if(callBackUrl!=null&&callBackUrl.trim().length()>0){
						openProcessDialog(getString(R.string.PleaseWait));
						succesUrl = callBackUrl;
						webView.loadUrl(callBackUrl);
					}
				}else{
					if(failUrl!=null&&failUrl.trim().length()>0){
						webView.loadUrl(failUrl);
					}else if(isTaobao){
						isTaobao = false;
						webView.goBack();
					}
				}
				break;

			default:
				break;
			}
			super.handleMessage(msg);
		}
		
	};


	
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        storageStateInfo = StorageStateInfo.getInstance();
        
        AlipayLogAgent.writeLog(this, Constants.BehaviourID.BIZLAUNCHED, null, null, "09999991", null, "09999991Home", Constants.APPCENTER, "09999991Icon", null);
        
		/*AlipayLogAgent.onBizAppLaunch(this, 
    			"09999991",//appId
    			Constants.LOTTERYVIEW,//viewId
        		"",//appVersion
        		storageStateInfo.getValue(Constants.STORAGE_PRODUCTID),//productID
        		getUserId(),
        		storageStateInfo.getValue(Constants.STORAGE_PRODUCTVERSION),
        		storageStateInfo.getValue(Constants.STORAGE_CLIENTID));//userID
		storageStateInfo.putValue(Constants.STORAGE_APPID, "09999991");
		storageStateInfo.putValue(Constants.STORAGE_APPVERSION, "");*/
        
		if(LephoneConstant.isLephone())
        {
        	getWindow().addFlags(LephoneConstant.FLAG_ROCKET_MENU_NOTIFY);
        }
		// get current device width and height.
		DisplayMetrics tDisplayMetrics = new DisplayMetrics();
		Display tDisplay = ((WindowManager) getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay();
		tDisplay.getMetrics(tDisplayMetrics);
		
        setContentView(R.layout.webapp_view);
        
        client  = new Client(this);
        
        url = getIntent().getStringExtra(WEBAPP_URL);
        title = getIntent().getStringExtra(WEBAPP_NAME);
        
        loadVariables();
        
        loadData();
    }
	/**
	 * 加载数据
	 */
	private void loadData() {
		loadUrl(url);
	}
	/**
	 * 
	 */
	private void loadVariables() {
		webView = (WebView) findViewById(R.id.content);
		mTitleName = (TextView) findViewById(R.id.title_text);
        mProgressBar = (ProgressBar) findViewById(R.id.ProgressBar);
		WebSettings webSettings = webView.getSettings();
		webSettings.setJavaScriptEnabled(true);
		
		webView.setWebViewClient(client);
		webView.setWebChromeClient(new MyWebChromeClient());
		webView.setScrollBarStyle(WebView.SCROLLBARS_OUTSIDE_OVERLAY);
		//清空记录和历史
		webView.clearCache(true);
		webView.clearHistory();
		
		setTitles(title);
	}
	
	/**
	 * @param string
	 */
	private void setTitles(String string) {
		mTitleName.setText(string);
	}
	/**
	 * 按键处理
	 */
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if ((keyCode == KeyEvent.KEYCODE_BACK) && webView.canGoBack()&&!webView.getUrl().equals(succesUrl)) {
			webView.goBack();
			return true;
		}else if((keyCode == KeyEvent.KEYCODE_BACK) && webView.canGoBack()&&webView.getUrl().equals(succesUrl)){
			succesUrl = null;
			goCatHome();
			return true;
		}else if(keyCode == KeyEvent.KEYCODE_BACK){
			goHome();
			/*AlipayLogAgent.onEvent(this,
					Constants.MONITORPOINT_EVENT_VIEWRETURN, 
					"",
					Constants.LOTTERYVIEW,
					storageStateInfo.getValue(Constants.STORAGE_APPID),
					storageStateInfo.getValue(Constants.STORAGE_APPVERSION),
					storageStateInfo.getValue(Constants.STORAGE_PRODUCTID),
					getUserId(),
					storageStateInfo.getValue(Constants.STORAGE_PRODUCTVERSION),
					storageStateInfo.getValue(Constants.STORAGE_CLIENTID));*/
			return true;
		}
		return super.onKeyDown(keyCode, event);
	}
	
	/**
	 * 根据登陆的状态，判断用户去哪个界面
	 */
	private void goHome()
	{
        jumpToHome();
	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
		switch (requestCode) {
		case RES_CODE_AUTHO:
			if(resultCode==RESULT_OK&&data!=null){//授权
				callBackUrl = data.getStringExtra(Constant.RES_PARAM_CALLBACKURL);
				if(callBackUrl!=null&&callBackUrl.trim().length()>0)
					webView.loadUrl(callBackUrl);
			}else if(resultCode==RESULT_CANCELED&&data!=null){//未授权
				failUrl = data.getStringExtra(Constant.RES_PARAM_FAILURL);
				if(failUrl!=null&&failUrl.trim().length()>0)
					webView.loadUrl(failUrl);
			}
			break;
		case Constant.REQUEST_LOGIN_BACK:
			if(resultCode==RESULT_OK&&getToken()!=null&&data!=null){//登录成功
				callBackUrl = data.getStringExtra(Constant.RES_PARAM_CALLBACKURL);
				loadUrl(callBackUrl);
			}
			break;
		default:
			break;
		}
	}
	
	public Handler getmHandler() {
		return mHandler;
	}
	
	public ProgressDiv getmProgress() {
		return mProgress;
	}

	public WebView getWebView() {
		return webView;
	}
	
	public void setCallBackUrl(String callBackUrl) {
		this.callBackUrl = callBackUrl;
	}
	
	public void setFailUrl(String failUrl) {
		this.failUrl = failUrl;
	}
	
	public String getSuccesUrl() {
		return succesUrl;
	}
	public void setSuccesUrl(String succesUrl) {
		this.succesUrl = succesUrl;
	}
	
	public void openProcessDialog(String msg)
	{
		if (mProgress == null)
		{
			mProgress = getDataHelper().showProgressDialogWithCancelButton(this, null, 
					msg, 
					false, true, 
					getDataHelper().cancelListener,
					getDataHelper().cancelBtnListener);
		}
	}

	public void closeProgress()
	{
		if (mProgress != null)
		{
			mProgress.dismiss();
			mProgress=null;
		}		
	}
	
	private void loadUrl(String url){
	    try{
    		String str = addToken(url);
    		ShowProgressBar(true);
    		webView.loadUrl(str);
	    }catch (Exception e) {
        }
	}
	

	/**
	 * @param url
	 * @param u
	 * @return
	 */
	private String addToken(String url) {
		if(getToken()!=null&&url!=null&&url.length()>0){
			Uri u = Uri.parse(url);
			BasicNameValuePair pair = new BasicNameValuePair(Constant.RQF_LOGIN_TOKEN,getToken());
			ArrayList<NameValuePair> arrayList = new ArrayList<NameValuePair>();
			arrayList.add(pair);
			if(u.getQuery() != null)
				url += ("&"+URLEncodedUtils.format(arrayList, "utf-8"));
			else
				url += ("?"+URLEncodedUtils.format(arrayList, "utf-8"));
		}
		return url;
	}
	
	public void ShowProgressBar(boolean bShow)
	{
//		ProgressBar pb = (ProgressBar)findViewById(R.id.ProgressBar);
//		pb.setVisibility(bShow ? View.VISIBLE : View.GONE);
	}
	
	 /**
     * Provides a hook for calling "alert" from javascript. Useful for
     * debugging your javascript.
     */
    final class MyWebChromeClient extends WebChromeClient 
    {
		@Override
        public void onReceivedTitle(WebView view, String title){
        	setTitles(title);
        }
		
        @Override
        public void onProgressChanged(WebView view, int newProgress) {
            if (newProgress >= 0&&newProgress<100){
                mProgressBar.setVisibility(View.VISIBLE);
                mProgressBar.setProgress(newProgress);
            }else
                mProgressBar.setVisibility(View.GONE);
        }
    }

	
	//淘宝
    private boolean isTaobao;
	public void HandleTradePay(String url)
	{
		String strTradeNoFlag 	= "alipay_trade_no";
		String strReturnUrlFlag = "return_url=";
		int iStart = url.indexOf("trade_pay.do?");
		int iEnd = url.indexOf(strTradeNoFlag);
		if( iStart != -1 && iEnd!=-1)
		{
			//
			webView.stopLoading();
			ShowProgressBar(false);
			
			//
			Uri u = Uri.parse(URLDecoder.decode(url));
			String m_strTradeNO = u.getQueryParameter(strTradeNoFlag);
			
			int iStartReturnUrl	= u.toString().indexOf(strReturnUrlFlag);
			if(iStartReturnUrl != -1)
			{
				iStartReturnUrl		+= strReturnUrlFlag.length();

				callBackUrl = u.toString().substring(iStartReturnUrl);
			}
			
//			callBackUrl = u.getQueryParameter(strReturnUrlFlag);
			if(callBackUrl==null||callBackUrl.trim().length()==0)
				callBackUrl = this.url;

			isTaobao = true;
			BaseHelper.payDeal(this,mHandler,mProgress,m_strTradeNO, getExtToken(),"","trade","");
		}		
	}
	

	public void HandleSessionInvalid(String strUrl)
	{
		int iStart = strUrl.indexOf("alipay_session_invalid.htm");
		if( iStart != -1 )
		{
			BaseHelper.ReLogin(R.drawable.infoicon, 
								getString(R.string.WarngingString), 
								getString(R.string.PucPayReLogin),
								this);
		}
		// else ok.
	}
	
	/**
	 * 
	 */
	private void goCatHome() {
		int step = webView.copyBackForwardList().getSize();
		webView.goBackOrForward(1-step);
	}
	

	private void jumpToHome()
	{
		BaseHelper.showDesktop(this);
		// finish();
	}
	
}