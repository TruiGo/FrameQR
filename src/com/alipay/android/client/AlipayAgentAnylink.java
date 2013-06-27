package com.alipay.android.client;

import org.json.JSONObject;

import android.app.Activity;
import android.app.Application;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;

import com.alipay.android.client.constant.Constant;
import com.alipay.android.client.util.BaseHelper;
import com.alipay.android.client.util.Responsor;
import com.alipay.android.common.data.UserData;
import com.alipay.android.comon.component.ProgressDiv;
import com.alipay.android.log.AlipayLogAgent;
import com.alipay.android.log.Constants;
import com.alipay.android.log.StorageStateInfo;
import com.alipay.android.util.LogUtil;
import com.eg.android.AlipayGphone.R;

/**
 * 共享链接方式代付
 * @author Zhanzhi
 *
 */
 
public class AlipayAgentAnylink extends RootActivity implements OnClickListener{
	/**
	 * Tag for logcat
	 */
	private static final String LOG_TAG = "AlipayAgentAnylink";
	
	/**
	 * Variables for About View
	 */
	private TextView mTitleName=null;
	private TextView mTxAgentLinkTitle=null;
	private TextView mTxAgentLink=null;
	private TextView mTxAgentLinkMemo=null;
	
	private Button mAgentNextButton = null;
	private Button mAgentCancelButton = null;
	
	private static boolean isCreated = false;
	private static String mTradeNo = null;
	private static String mBizType = null;
	
	private static final int MSG_GET_AGENT_ANY_LINK = 1110;
	private static final int MSG_CANCEL_AGENT_ANY_LINK = 1111;
	
	private ProgressDiv mProgress=null;

    
    DialogInterface.OnClickListener btnErrorOk = new DialogInterface.OnClickListener() {
        @Override
        public void onClick(DialogInterface dialog, int which) {
            // TODO Auto-generated method stub
            setResult(Activity.RESULT_CANCELED);
            finish();
        }
    };
	
	private Handler mHandler = new Handler()
	{
		public void handleMessage(Message msg)
		{
			LogUtil.logOnlyDebuggable(LOG_TAG, "handleMessage msg:"+ msg.what);
			
			Responsor responsor;
			JSONObject jsonResponse;
			
			showResult(msg);
			
			switch (msg.what) 
			{
				case MSG_GET_AGENT_ANY_LINK:
					String agentLink = null;
		            String txtMemo = null;
					responsor = (Responsor) msg.obj;
					if(responsor.memo != null) {
			            //获取memo
			            txtMemo = responsor.memo;
			        }
					jsonResponse = responsor.obj;
					
					if(responsor.status == 100) {
						((AlipayApplication)getApplicationContext()).setRecordsRefresh(true);
						//获取到链接url
						agentLink = jsonResponse.optString(Constant.RPF_AGENT_URL);
						isCreated = true;
						updateAgentLink(agentLink);
					} else {
						//出错，需要返回上一个界面
						isCreated = false;

	                    if (responsor.status == 0) {
	                        txtMemo = getResources().getString(R.string.CheckNetwork);
	                    }else if (responsor.status == 1) {
	                        txtMemo = getResources().getString(R.string.WarningDataCheck);
	                    }
						getDataHelper().showDialog(AlipayAgentAnylink.this, R.drawable.erroricon, 
			                    getResources().getString(R.string.AgentPayErrorPrompt), 
			                    txtMemo,
			                    getResources().getString(R.string.Ensure), btnErrorOk,
			                    null, null, null, null);
					}
				break;
				
				case MSG_CANCEL_AGENT_ANY_LINK:
					responsor = (Responsor) msg.obj;
					jsonResponse = responsor.obj;
					if(responsor.status == 100) {
						//提示用户取消成功，之后返回到上一个界面
						setResult(Activity.RESULT_CANCELED);
						finish();
					} else {
						//出错，需要返回上一个界面；之后跳转到消费记录页面
//							mActivity.setResult(Activity.RESULT_FIRST_USER);
//							mActivity.finish();
						//此处有MessageFilter来处理异常
					}
				break;
			}
		}
	};
	
	private void showResult(Message msg){
		
		boolean needDismissProcessDialog = true;
		
		
		if(needDismissProcessDialog){
			if (mProgress != null){
				mProgress.dismiss();
				mProgress = null;
			}
		}
	}

	
	/** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.alipay_agent_anylink_320_480);
        
        isCreated = false;

        loadAllVariables();
        
        Intent intent = getIntent();
        mTradeNo = intent.getStringExtra(Constant.RQF_BIZNO);
        mBizType = intent.getStringExtra(Constant.RQF_BIZTYPE);
    }
    
    @Override
    protected void onStart() {
        // TODO Auto-generated method stub
        super.onStart();
        LogUtil.logAnyTime(LOG_TAG,"onStart() called");
        
        if (!isCreated) {
        	getAgentAnyLink();
        }
    }
    
    @Override
	protected void onStop() {
		// TODO Auto-generated method stub
		super.onStop();
	}
	
	@Override
    protected void onDestroy() {
        // TODO Auto-generated method stub
        super.onDestroy();
        isCreated = false;
        LogUtil.logAnyTime(LOG_TAG,"onDestroy() called");
    }
    
    private void loadAllVariables(){
    	// Display Item Name.
    	mTitleName = (TextView) findViewById(R.id.title_text);
    	mTitleName.setText(R.string.AgentPayTitle);
    	
    	mTxAgentLinkTitle = (TextView) findViewById(R.id.AgentLinkTitle);
    	mTxAgentLink = (TextView) findViewById(R.id.AgentLinkInfo);
    	mTxAgentLinkMemo = (TextView) findViewById(R.id.AgentLinkMemoText);
    	
		mAgentNextButton = (Button) findViewById(R.id.AgentLinkNextButton);
		mAgentNextButton.setOnClickListener(new OnClickListener() {
			@Override 
			public void onClick(View arg0) {
//				shareAgentLink();
				
				String txtShareLink = mTxAgentLinkMemo.getText().toString().trim();
				LogUtil.logOnlyDebuggable(LOG_TAG, "txtShareLink:"+txtShareLink);
		        if (txtShareLink != null && !txtShareLink.equals("")) {
		        	txtShareLink = txtShareLink + "\t" + mTxAgentLink.getText().toString();
		        } else {
		        	txtShareLink = mTxAgentLinkMemo.getHint().toString().trim() + "\t" + mTxAgentLink.getText().toString();
		        }

				if (txtShareLink != null && txtShareLink.contains("http")) {
					BaseHelper.Share(AlipayAgentAnylink.this, Constant.SHARE_PAY, txtShareLink);
					UserData mUserData = AlipayAgentAnylink.this.getUserData();
		    		if (mUserData!=null) {
		    			mUserData.resetStatus();
					}
					/*StorageStateInfo storageStateInfo = StorageStateInfo.getInstance();
					AlipayLogAgent.onPageJump(AlipayAgentAnylink.this, 
			    			 Constants.ANYBODYSUBSTITUTEPAYVIEW,
			         		 Constants.ANYBODYSUBSTITUTEPAYSELECTTYPEVIEW, 
			         		 storageStateInfo.getValue(Constants.STORAGE_APPID), //appID
			         		 storageStateInfo.getValue(Constants.STORAGE_APPVERSION), //appVersion
			         		 storageStateInfo.getValue(Constants.STORAGE_PRODUCTID),
			       		 	 getUserId(),
			 	 			 storageStateInfo.getValue(Constants.STORAGE_PRODUCTVERSION),
			 	 			 storageStateInfo.getValue(Constants.STORAGE_CLIENTID));*/
				}
			}
		});
    	
    	
    	mAgentCancelButton = (Button) findViewById(R.id.AgentLinkCancelButton);
    	mAgentCancelButton.setOnClickListener(new OnClickListener() {
			@Override 
			public void onClick(View arg0) {
				cancelAgentAnyLink();
			}
		});
    }
    
    private void getAgentAnyLink(){
    	//向服务端请求代付的共享链接
        getDataHelper().sendAgentPay(mHandler, MSG_GET_AGENT_ANY_LINK, 
				mTradeNo, mBizType, "false", 
				null, null);
	
    	openProcessDialog(getString(R.string.AgentPayGetAnyLink));
    }
    
    private void updateAgentLink(String agentLink){
    	//更新共享链接url
    	mTxAgentLink.setText(agentLink);
    	mTxAgentLinkTitle.setText(R.string.AgentAnylinkCreated);
    }
    
    private void cancelAgentAnyLink(){
    	//向服务端请求取消代付的共享链接
        getDataHelper().cancelAgentPay(mHandler, MSG_CANCEL_AGENT_ANY_LINK, 
				mTradeNo, mBizType, null);
	
    	openProcessDialog(getString(R.string.AgentPayCancel));
    }
    
//    private void shareAgentLink(){
//    	//共享链接url给本机相应应用
//    	Intent it = new Intent(Intent.ACTION_SEND);
//        it.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//        it.putExtra(Intent.EXTRA_SUBJECT, mActivity.getString(R.string.AgentPayLinkShareTitle));
//        String memo = mTxAgentLinkMemo.getText().toString().trim();
//        Log.w(LOG_TAG, "memo:"+memo);
//        if (memo != null && !memo.equals("")) {
//        	memo = memo + "\t" + mTxAgentLink.getText().toString();
//        } else {
//        	memo = mTxAgentLinkMemo.getHint().toString().trim() + "\t" + mTxAgentLink.getText().toString();
//        }
//        it.putExtra(Intent.EXTRA_TEXT,memo);
//        it.setType("text/plain");
//
//    	Intent newIntent = Intent.createChooser(it, mActivity.getString(R.string.AgentPayLinkShareTitle));
//    	newIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//    	mActivity.startActivity(newIntent);
//    }
    
    private void openProcessDialog(String msg) {
		if (mProgress == null) {
			mProgress = getDataHelper().showProgressDialogWithCancelButton(this, null, 
					msg, 
					false, false, null, null);
		}
	}
    
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
        	LogUtil.logAnyTime(LOG_TAG, "onKeyDown KEYCODE_BACK!");
        	setResult(Activity.RESULT_FIRST_USER);
        	finish();
        } else if (keyCode == KeyEvent.KEYCODE_SEARCH){
        	//捕获此事件，但不做处理
        	return true;
        }
        
        return false;
    }

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub

	}

}