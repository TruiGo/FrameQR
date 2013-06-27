package com.alipay.android.client;

import org.json.JSONObject;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.webkit.WebSettings;
import android.webkit.WebSettings.LayoutAlgorithm;
import android.webkit.WebView;
import android.widget.TextView;

import com.alipay.android.client.advert.WebClient;
import com.alipay.android.client.constant.Constant;
import com.alipay.android.client.util.Responsor;
import com.alipay.android.common.data.MessageData;
import com.alipay.android.common.data.MessageRecord;
import com.alipay.android.core.ParamString;
import com.eg.android.AlipayGphone.R;

public class AlipaySystemMsgDetail extends RootActivity {
    
	public static final String WEBVIEWBASEDATA = "<HTML><body><h3 align=\"center\" style=\"margin-bottom: 0px\"><font color=\"#666666\">$title$</font></h3><div style=\"margin-top: 0px\"><center><font color=\"#cccccc\">$date$</font></center></div><hr noshade size=\"1px\" color=\"#666666\"/><p><font color=\"#666666\">$content$</font></p></body></HTML>";

	private TextView mTitleView ;
	private WebView mSystemMsgDetailWebView;
	private String MSG_TITLE = "$title$";
	private String MSG_DATE = "$date$";
	private String MSG_CONTENT = "$content$";
	
	public MessageRecord messageRecord;

    private MessageData mMessageData;

	private String mUserId;

	private String mMsgId;
	
	private final  int GETMSGDETAIL = 0x111;
	
	private MessageFilter mMessageFilter;
	
	private Handler mHandler = new Handler(){
		@Override
		public void handleMessage(Message msg) {
			Responsor responsor = (Responsor) msg.obj;
            JSONObject jsonResponse = responsor.obj;
            boolean tResultOK = mMessageFilter.process(msg);
            switch (msg.what) {
                case GETMSGDETAIL:
                    if (tResultOK) {
                    	if(jsonResponse != null){
                    		JSONObject msgObject = jsonResponse.optJSONObject("msg");
                    		if(msgObject != null){
                    			messageRecord = new MessageRecord().fillData(msgObject);
                    			if(messageRecord != null && mMessageData != null){
                    				mMessageData.readedMsg(messageRecord);//change status of the message
                    			}
                    			setData();
                    		}
                    	}
                    }
                    break;
                default:
                    break;
            }
			super.handleMessage(msg);
		}
	};
    
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.alipay_system_msg_detail);
        AlipayApplication application = (AlipayApplication) getApplicationContext();
        mMessageData = application.getMessageData();
		initActivity();
		setData();
	}
	
	private void initActivity(){
		mTitleView = (TextView)findViewById(R.id.title_text);
		mSystemMsgDetailWebView = (WebView) findViewById(R.id.systemMsgDetailWebView);
        AlipayApplication alipayApplication = (AlipayApplication) getApplicationContext();
		mSystemMsgDetailWebView.setWebViewClient(new WebClient(alipayApplication.getHallData(), null));
		
		WebSettings setting = mSystemMsgDetailWebView.getSettings();
		setting.setLayoutAlgorithm(LayoutAlgorithm.SINGLE_COLUMN);
		setting.setDefaultTextEncodingName("UTF-8") ;
		mTitleView.setText(R.string.SystemMsg);
		mMessageFilter = new MessageFilter(this);
		
		if (getUserData() != null) {//登录后才可以查看系统消息
			showMessage(); 
		} else {
			Intent loginIntent = new Intent(this, Login.class);
			startActivityForResult(loginIntent, Constant.REQUEST_LOGIN_BACK);
		}
	}
	
	private void showMessage() {
		Intent intent = getIntent();
		String params = intent.getStringExtra("param");
		
		getMessageDetail(params);
	}

	private void getMessageDetail(String params) {
		if (params != null && !"".equals(params)) {
			ParamString paramStr = new ParamString(params);
			
			mUserId = paramStr.getValue("uId");
			mMsgId = paramStr.getValue(Constant.RPF_MSGID);
			if(mMsgId != null){
				messageRecord = mMessageData.getMessageById(mMsgId);
				if(messageRecord != null){
					setData();
				}else
					getDataHelper().sendGetMsgDetail(mHandler,GETMSGDETAIL,mMsgId);
			}
		}else{
			messageRecord = (MessageRecord) getIntent().getSerializableExtra("msg");
			setData();
		}
	}
	
	private void setData(){
		if(messageRecord == null)
			return;
		if(messageRecord != null && messageRecord.isNew()){
			messageRecord.setNew(false);
			mMessageData.readedMsg(messageRecord);
            setResult(RESULT_OK);
		}
		switch (messageRecord.getType()) {
		case MessageRecord.TYPE_TEXT:
			String data = WEBVIEWBASEDATA.replace(MSG_TITLE, messageRecord.getTopic()).replace(MSG_DATE, messageRecord.getDate()).replace(MSG_CONTENT, messageRecord.getContent());
			mSystemMsgDetailWebView.loadDataWithBaseURL(null,data, "text/html", "utf-8",null);                               
			break;
		case MessageRecord.TYPE_URL:
            mSystemMsgDetailWebView.loadUrl(messageRecord.getContent());
			break;
		case MessageRecord.TYPE_HTML:
            mSystemMsgDetailWebView.loadDataWithBaseURL(null,messageRecord.getContent(), "text/html", "utf-8",null);
			break;
		default:
			break;
		}
		
	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (requestCode == Constant.REQUEST_LOGIN_BACK) {
			if (resultCode == Activity.RESULT_OK) {
				showMessage();
			}
		}
	}
}
