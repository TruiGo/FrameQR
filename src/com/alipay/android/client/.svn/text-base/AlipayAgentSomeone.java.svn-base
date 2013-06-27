package com.alipay.android.client;

import java.util.ArrayList;
import java.util.Formatter;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.alipay.android.client.constant.Constant;
import com.alipay.android.client.util.AlipayInputErrorCheck;
import com.alipay.android.client.util.BaseHelper;
import com.alipay.android.client.util.Responsor;
import com.alipay.android.common.data.UserData;
import com.alipay.android.comon.component.EditTextHasNullChecker;
import com.alipay.android.comon.component.ProgressDiv;
import com.alipay.android.log.Constants;
import com.alipay.android.util.LogUtil;
import com.eg.android.AlipayGphone.R;

/**
 * 指定人代付页面
 * @author Zhanzhi
 *
 */
 
public class AlipayAgentSomeone extends RootActivity {
	/**
	 * Tag for logcat
	 */
	private static final String LOG_TAG = "AlipayAgentSomeone";
	
	/**
	 * Variables for About View
	 */
	private TextView mTitleName=null;
	private ImageView mImgContact=null;
	private AutoCompleteTextView mTxContact=null;
	private EditText mTxContactMemo=null;
	private RelativeLayout mbtnAddContact=null;
	private CheckBox mAddtoContacts = null;
	private Button mBtnAgentSubmit = null;
	
	private String mAgentName = null;
	
//	private String mOldAgentName = null;
	private String mOldAgentAccount = null;
	private String mOldAgentName = null;
	
//	private static boolean isCreated = false;
	private static String mTradeNo = null;
	private static String mBizType = null;
	
	private static final int MSG_SUBMIT_AGENT_PAY = 1120;
	
	public static Activity mActivity = null;
	private ProgressDiv mProgress=null;
	EditTextHasNullChecker editTextHasNullChecker = new EditTextHasNullChecker();
	
//	private JSONObject myJsonObject = null;
	
	private Handler mHandler = new Handler()
	{
		public void handleMessage(Message msg)
		{
			LogUtil.logOnlyDebuggable(LOG_TAG, "handleMessage msg:"+ msg.what);
			
			showResult(msg);
			
			switch (msg.what) 
			{
				case MSG_SUBMIT_AGENT_PAY:
					//处理服务端返回的结果
					handleResultforSubmit(msg);
				break;
			}
		}
	};
	
	private void showResult(Message msg){
		
//		Responsor responsor = (Responsor)msg.obj;
//		JSONObject obj = responsor.obj;
//		boolean tResultOK = false;
		boolean needDismissProcessDialog = true;
		
//		tResultOK = mMessageFilter.process(msg);
		
//		if((tResultOK) && (!dataHelper.isCanceled())){
//			myJsonObject = obj;
//		}
		
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
        mActivity = this;
        
        setContentView(R.layout.alipay_agent_someone_320_480);
        
//        isCreated = false;

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
        
        getLocalCntactList();
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
//        isCreated = false;
        LogUtil.logAnyTime(LOG_TAG,"onDestroy() called");
    }
    
    private void loadAllVariables(){
    	// Display Item Name.
    	mTitleName = (TextView) findViewById(R.id.title_text);
    	mTitleName.setText(R.string.AgentPayTitle);
    	
    	mImgContact = (ImageView) findViewById(R.id.AgentOneContactImg);
    	mImgContact.setOnClickListener(new OnClickListener()
		{
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				Intent intent = new Intent(AlipayAgentSomeone.this, AlipayContact.class);
				intent.setData(Uri.parse(mTitleName.getText().toString()));//向下一个Activity传递了string类型参数
				startActivityForResult(intent, Constant.REQUEST_CODE);//以传递参数的方式跳转到下一个Activity
			}
		}
		);
    	
    	mbtnAddContact = (RelativeLayout) findViewById(R.id.AgentOneAddContactLayout);
    	mAddtoContacts = (CheckBox)findViewById(R.id.AgentOneAddContactCheckBox);
    	
    	mTxContact = (AutoCompleteTextView) findViewById(R.id.AgentOneContactText);
    	mTxContact.addTextChangedListener(editTextHasNullChecker);
    	editTextHasNullChecker.addNeedCheckView(mTxContact);
    	
    	
    	mTxContact.requestFocus();
    	mTxContact.setOnItemClickListener(new OnItemClickListener(){
    		@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				// TODO Auto-generated method stub
				if(mbtnAddContact.getVisibility()==View.VISIBLE){
					mbtnAddContact.setVisibility(View.GONE);
				}
			}
		});
    	
    	mTxContact.addTextChangedListener(new TextWatcher(){
			@Override
			public void onTextChanged(CharSequence s, int start, int before,
					int count) {
				// TODO Auto-generated method stub
				mTxContact.setThreshold(1);
				
//				mAddtoContacts.setChecked(false);
				if(mbtnAddContact.getVisibility()==View.GONE){
					mbtnAddContact.setVisibility(View.VISIBLE);
				}
				//代付人改变了，更新代付人姓名
				mAgentName = "";
			}

			@Override
			public void afterTextChanged(Editable arg0) {
				// TODO Auto-generated method stub
				String tContact = arg0.toString();  
				if (mOldAgentAccount != null && !mOldAgentAccount.equals("")
						&& mOldAgentAccount.trim().equalsIgnoreCase(tContact.trim().toLowerCase())) {
					//和之前获取的联系人一样
					mbtnAddContact.setVisibility(View.GONE);
				}
			}

			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {
				// TODO Auto-generated method stub
			}
			
		});
    	ArrayList<String> acdata= getDataHelper().getContactData(this);
		if(acdata!=null&&acdata.size()>0){
			ArrayAdapter<String> sa = new ArrayAdapter<String>(this, R.layout.contact_fliter_item, R.id.account, acdata);
			mTxContact.setAdapter(sa);
		}
		
		
    	mTxContactMemo = (EditText) findViewById(R.id.AgentOneMemoText);

    	mBtnAgentSubmit = (Button) findViewById(R.id.AgentOneSubmitButton);
    	editTextHasNullChecker.addNeedEnabledButton(mBtnAgentSubmit);
    	
    	mBtnAgentSubmit.setOnClickListener(new OnClickListener() {
			@Override 
			public void onClick(View arg0) {
				submitAgentPayReq();
			}
		});
    }
    
    private void getLocalCntactList(){
    	//
    }
    
    private void submitAgentPayReq(){
    	//必须首先检查相关域是否填写正确
    	//1. 代付指定账户不能为空
    	String tTxtContact = mTxContact.getText().toString();
    	if(tTxtContact.contains("(") && tTxtContact.contains(")")){
    		tTxtContact = tTxtContact.substring(tTxtContact.indexOf("(")+1, tTxtContact.indexOf(")"));
    	}
    	int result = AlipayInputErrorCheck.NO_ERROR;
    	if (tTxtContact != null && !tTxtContact.equals("")) {
    		result = AlipayInputErrorCheck.CheckUserID(tTxtContact);
    		
    		//2. 指定的代付人不能是买家自己
    		if (result == AlipayInputErrorCheck.NO_ERROR) {
        		if (tTxtContact.equals(getAccountName(false))) {
        			result = -100;
        		}
        	}
        	
    	} else {
    		result = AlipayInputErrorCheck.ERROR_NULL_INPUT;
    	}
    	
    	if (result == AlipayInputErrorCheck.NO_ERROR) {
    		String isAddContact = "true";
    		if (!mAddtoContacts.isChecked()) {
    			//需要添加到联系人列表
    			isAddContact = "false";
    		}
    		
    		//向服务端请求代付设置
    		getDataHelper().sendAgentPay(mHandler, MSG_SUBMIT_AGENT_PAY, 
    				mTradeNo, mBizType, isAddContact, 
    				tTxtContact, mTxContactMemo.getText().toString());
    		
    		UserData mUserData = this.getUserData();
    		if (mUserData!=null) {
    			mUserData.resetStatus();
			}
    	
        	openProcessDialog(getString(R.string.AgentPayCreateSomeone));
    	} else {
    		//输入有错误，提示用户对话框
    		String warningMsg = getString(R.string.AgentAccountError);
    		switch (result) {
    			case AlipayInputErrorCheck.ERROR_NULL_INPUT:
    				warningMsg = getString(R.string.AgentAccountErrorEmpty);
    			break;
    			
    			case AlipayInputErrorCheck.ERROR_INVALID_FORMAT:
    				warningMsg = getString(R.string.AgentAccountErrorFormat);
        		break;
        		
    			case -100:
    				warningMsg = getString(R.string.AgentAccountErrorSelf);
            	break;
    		}
    		
    		getDataHelper().showDialog(mActivity, R.drawable.infoicon,
    				getResources().getString(R.string.WarngingString), warningMsg,
    				getResources().getString(R.string.Ensure), null, null,
					null, null, null);
    		BaseHelper.recordWarningMsg(this, warningMsg,Constants.ONEPERSONSUBSTITUTEPAYVIEW);
    	}

    }

    private void handleResultforSubmit(Message msg){	
    	String txtMemo = null;
    	
		Responsor responsor = (Responsor)msg.obj;
		if(responsor.memo != null) {
			//获取memo
			txtMemo = responsor.memo;
		}
		
		if(responsor.status == 100) {
//			isCreated = true;
			
			if (mAgentName == null || mAgentName.equals("")) {
				mAgentName = mTxContact.getText().toString();
			}
			
			//添加到本地联系人历史记录
			getDataHelper().addUserIdToFile(this, mTxContact.getText().toString());
			
			getDataHelper().showDialog(this, R.drawable.ok, 
					getResources().getString(R.string.AgentPayConfirmPrompt), 
					txtMemo, getResources().getString(R.string.Ensure), btnForOk,
					null, null, null, null);
		} else {
			//出错，需要返回上一个界面——代付发起页
//			isCreated = false;

            if (responsor.status == 0) {
                txtMemo = getResources().getString(R.string.CheckNetwork);
            }else if (responsor.status == 1) {
                txtMemo = getResources().getString(R.string.WarningDataCheck);
            }
		    getDataHelper().showDialog(this, R.drawable.erroricon, 
					getResources().getString(R.string.AgentPayErrorPrompt), 
					txtMemo,
					getResources().getString(R.string.Ensure), btnErrorOk,
					null, null, null, null);
		}
    }
    
    DialogInterface.OnClickListener btnForOk = new DialogInterface.OnClickListener() {
		@Override
		public void onClick(DialogInterface dialog, int which) {
			mActivity.setResult(Activity.RESULT_OK);
			mActivity.finish();
		}
	};
	
	DialogInterface.OnClickListener btnErrorOk = new DialogInterface.OnClickListener() {
		@Override
		public void onClick(DialogInterface dialog, int which) {
			// TODO Auto-generated method stub
			mActivity.setResult(Activity.RESULT_CANCELED);
			mActivity.finish();
		}
	};

	
    
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
        	
			mActivity.setResult(Activity.RESULT_CANCELED);
			mActivity.finish();
        } else if (keyCode == KeyEvent.KEYCODE_SEARCH){
        	//捕获此事件，但不做处理
        	return true;
        }
        
        return false;
    }
	
	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) 
	{
		super.onActivityResult(requestCode, resultCode, data);  
		if(requestCode == Constant.REQUEST_CODE)
		{
			if(resultCode == Activity.RESULT_OK)
			{
				mOldAgentAccount = data.getData().toString();
				
				if (mOldAgentAccount != null || !mOldAgentAccount.equals("")) {
					mTxContact.setText(mOldAgentAccount);
				}
				mAgentName = data.getStringExtra(Constant.PRF_CONTACTNAME);
				if (mAgentName == null || mAgentName.equals("")) {
					mAgentName = mTxContact.getText().toString();
				}
				
				if((mAgentName != null && !mAgentName.equals("")) && (mOldAgentAccount != null && !mOldAgentAccount.equals(""))){
					mTxContact.setText(handleEditTextAccountInfo(mOldAgentAccount,mAgentName));
				}
//				mOldAgentName = mAgentName;
				
				mTxContact.setThreshold(1000);
				
				mbtnAddContact.setVisibility(View.GONE);
			}
		}
	}


	private String handleEditTextAccountInfo(String account,String name ) {
		String accountAndName = "";
		if(name.length()>=1){
			Formatter textFormatter = new Formatter();
			textFormatter.format("*%s(%s)",name.substring(1),account);
			accountAndName = textFormatter.toString();
		}else{
			accountAndName = account;
		}
		return accountAndName;
	}
	
	

}