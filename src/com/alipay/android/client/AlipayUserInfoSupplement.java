/**
 * 
 */
package com.alipay.android.client;

import java.util.ArrayList;
import java.util.HashMap;

import org.json.JSONObject;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnFocusChangeListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.alipay.android.appHall.bussiness.CallBackFilter;
import com.alipay.android.client.constant.AlipayHandlerMessageIDs;
import com.alipay.android.client.constant.Constant;
import com.alipay.android.client.util.AlipayDataStore;
import com.alipay.android.client.util.AlipayInputErrorCheck;
import com.alipay.android.client.util.BaseHelper;
import com.alipay.android.comon.component.ProgressDiv;
import com.alipay.android.net.RequestMaker;
import com.alipay.android.net.http.HttpRequestMaker;
import com.alipay.platform.core.Command;
import com.alipay.platform.view.ActivityMediator;
import com.alipay.platform.view.OnDataChangeListener;
import com.eg.android.AlipayGphone.R;

/**
 * @author chengkuang
 *
 */
public class AlipayUserInfoSupplement extends RootActivity implements OnFocusChangeListener, OnDataChangeListener{
	@SuppressWarnings("unused")
    private static final String TAG = "AlipyUserInfoSupplement";
	
	HashMap<String, String> myResponse = new HashMap<String, String>();
	JSONObject myJsonObject = null;
	private MessageFilter mMessageFilter = new MessageFilter(this);
	Activity mActivity = null;
	
	private int mErrorType = UPDATE_OK;
	private static final int UPDATE_OK = -3;
	private static final int GET_RSA_KEY_START = -1;
    public static final String GET_RSAKEY_ID = "10";
    private RequestMaker mRequestMaker;
    private CallBackFilter mBackFilter;
    
    Handler mHandler = new Handler() {
        public void handleMessage(Message msg) {
            showResult(msg);
            super.handleMessage(msg);
        }
    };
	
	private TextView mTitleName = null;
	private TextView mUserNameInfo = null;
	private EditText mRealNameInfo = null;
	private String mRealNameText = null;
	private EditText mPayPassWInfo = null;
	private String mPayPassWText = null;
	private Button mSupplyButton = null;
	
	/**
	 * Variable for ProgressDialog when paying
	 */
	private ProgressDiv mProgress = null;
	/** Called when the activity is first created. */
	@Override public void onCreate(Bundle savedInstanceState){
        mRequestMaker = new HttpRequestMaker(getApplicationContext(), R.raw.interfaces);
        mBackFilter = new CallBackFilter(this);
        
		super.onCreate(savedInstanceState);
		mActivity = this;
		setContentView(R.layout.alipay_userinfosupplement_320_480);
		loadAllVariables();
	}
	
	private void loadAllVariables()
	{
		mTitleName = (TextView) findViewById(R.id.title_text);
		mTitleName.setText(R.string.UserInfoSupplementInfo);
		
		mUserNameInfo = (TextView)findViewById(R.id.UserNameInfo);
//		String s = getUserName();
		mUserNameInfo.setText(getUserName());
		mRealNameInfo = (EditText)findViewById(R.id.RealNameInfo);
		mPayPassWInfo = (EditText)findViewById(R.id.PayPassWInfo);
		mSupplyButton = (Button)findViewById(R.id.SupplyButton);
		mSupplyButton.setOnClickListener(new OnClickListener() {
			@Override public void onClick(View arg0)
			{
				getRSAKey();
			}
		});
		mSupplyButton.setOnFocusChangeListener(this);
		BaseHelper.showDialog(this,getResources().getString(R.string.WarngingString), getResources().getString(R.string.MEMO_411),R.drawable.infoicon);
	}
	
	
	private void getRSAKey()
	{
		if (mErrorType == GET_RSA_KEY_START)
			return;
		mRealNameText = mRealNameInfo.getText().toString();
		mPayPassWText = mPayPassWInfo.getText().toString();
		int tResult = AlipayInputErrorCheck.CheckRealName(mRealNameText);
		if (tResult != AlipayInputErrorCheck.NO_ERROR)
		{
			// check error.
			String warningMsg;
			if (tResult == AlipayInputErrorCheck.ERROR_INVALID_FORMAT)
			{
				warningMsg = getResources().getString(R.string.WarningInvalidRealName);
			}
			else if (tResult == AlipayInputErrorCheck.ERROR_NULL_INPUT)
			{
				warningMsg = getResources().getString(R.string.WarningRealNameEmpty);
			}
			else if (tResult == AlipayInputErrorCheck.ERROR_OUTOF_RANGE)
			{
				warningMsg = getResources().getString(R.string.WarningRealNameOutRange);
			}
			else
			{
				warningMsg = "UNKNOWN_ERROR TYPE = "+ tResult;
				//    			Log.i(LOG_TAG, warningMsg);
			}
			
			BaseHelper.recordWarningMsg(this,warningMsg);
			
			getDataHelper().showDialog(this, R.drawable.infoicon, getResources()
					.getString(R.string.WarngingString), warningMsg,
					getResources().getString(R.string.Ensure), null, null,
					null, null, null);
			return;
		}

		// check password input
		tResult = AlipayInputErrorCheck.checkPayingPasswordSet(mPayPassWText);
		if (tResult != AlipayInputErrorCheck.NO_ERROR)
		{
			// check error.
			String warningMsg;
			if (tResult == AlipayInputErrorCheck.ERROR_INVALID_FORMAT)
			{
				warningMsg = getResources().getString(
						R.string.WarningInvalidLoginPassword);
			}
			else if (tResult == AlipayInputErrorCheck.ERROR_NULL_INPUT)
			{
				warningMsg = getResources().getString(R.string.NoEmptyPassword);
			}
			else
			{
				warningMsg = "UNKNOWN_ERROR TYPE = " + tResult;
				// Log.i(LOG_TAG, warningMsg);
			}
			
			BaseHelper.recordWarningMsg(this,warningMsg);
			
			getDataHelper().showDialog(this, R.drawable.infoicon, getResources()
					.getString(R.string.WarngingString), warningMsg,
					getResources().getString(R.string.Ensure), null, null,
					null, null, null);
			return;
		}
		
		mErrorType = GET_RSA_KEY_START;
        ActivityMediator activityMediator = new ActivityMediator(this);
        ArrayList<String> params = new ArrayList<String>();
        String sessionId = getSessionId();
        params.add(sessionId);
        params.add(getConfigData().getClientId());
        activityMediator.sendCommand(GET_RSAKEY_ID,  "clean", mRequestMaker, params);
//		myHelper.sendGetRSAKey(mHandler,
//				AlipayHandlerMessageIDs.START_PROCESS_GOT_DATA);
		if(mProgress == null)
		{
			mProgress = getDataHelper().showProgressDialogWithCancelButton(this, null,
					getResources().getString(R.string.StrDealing),
					false, true, getDataHelper().cancelListener,
					getDataHelper().cancelBtnListener);
		}
	}
	
	private String getUserName()
	{
		String tLoginId = getIntent().getData().toString();
		if(tLoginId!=null && !tLoginId.equals(""))
		{
			return tLoginId;
		}
		else
		{
			AlipayDataStore settings = new AlipayDataStore(this);
			return settings.getString(AlipayDataStore.NAME);
		}
	}

	
	private void showResult(Message msg){
		boolean tResultOK 	= false;
		boolean needDismissProcessDialog = true;

        tResultOK = mMessageFilter.process(msg);
        if ((tResultOK) && (!getDataHelper().isCanceled())) {
            if (mErrorType == UPDATE_OK) {
                supplySuccess();
            }
        }

        if (needDismissProcessDialog) {
            closeProgress();
        }
	}

	private void supplySuccess()
	{
		
		getDataHelper().showDialog(mActivity, R.drawable.infoicon,getResources().getString(R.string.Save_Success_Title),
				                     getResources().getString(R.string.Save_Success), 
				                     getResources().getString(R.string.Ensure), new DialogInterface.OnClickListener() {
				     					@Override public void onClick(DialogInterface dialog,
				    							int which)
				    					{
//				    						mActivity.setResult(Activity.RESULT_OK);
//				    						AlipayMain.startMain(mActivity);
				    						mActivity.finish();
				    					}
				    				}, null, null, null, null);
//		AlertDialog.Builder tDialog = new AlertDialog.Builder(mActivity);
//		tDialog.setIcon(R.drawable.infoicon);
//		tDialog.setTitle(getResources().getString(R.string.Save_Success_Title));
//		tDialog.setMessage(getResources().getString(R.string.Save_Success));
//		tDialog.setPositiveButton(R.string.Ensure,
//				new DialogInterface.OnClickListener() {
//					@Override public void onClick(DialogInterface dialog,
//							int which)
//					{
////						mActivity.setResult(Activity.RESULT_OK);
////						AlipayMain.startMain(mActivity);
//						mActivity.finish();
//					}
//				});
//		tDialog.show();	
	}
	
	@Override
	public void onFocusChange(View v, boolean hasFocus)
	{
		// TODO Auto-generated method stub
		
	}

    @Override
    public boolean preCancel(Command command) {
        return false;
    }

    @Override
    public void onCancel(Command command) {
    }

    @Override
    public boolean preFail(Command command) {
        return false;
    }

    @Override
    public void onFail(Command command) {
        closeProgress();

        String content = command.getResponseMessage();
        BaseHelper.showDialog(AlipayUserInfoSupplement.this, getString(R.string.Error), content, R.drawable.erroricon);
    }

    @SuppressWarnings("unchecked")
    @Override
    public void onComplete(Command command) {
        //
        HashMap<String, Object> hmResponse = (HashMap<String, Object>) command.getResponseData();

        if(!mBackFilter.processCommand(hmResponse)){
            if(command.getmId().equals(GET_RSAKEY_ID)){
                mErrorType = UPDATE_OK;
                
                String string = (String) hmResponse.get(Constant.RPF_RSA_PK);
                if(string != null){
                    setPublicKey(string);
//                    alipayApp.putInfoData(GlobalDataDefine.RSA_PK, string);
//                    DataHelper.getInstance().mDefaultValueMap.put(Constant.RPF_RSA_PK, (String) string);
                }
                
                string = (String) hmResponse.get(Constant.RPF_RSA_TS);
                if(string != null){
                    setTimeStamp(string);
//                    alipayApp.putInfoData(GlobalDataDefine.RSA_TS, string);
//                    DataHelper.getInstance().mDefaultValueMap.put(Constant.RPF_RSA_TS, (String) string);
                }
                
                // Send command to do UserInfoSupplement.
                getDataHelper().sendUserInfoSupplement(mHandler, AlipayHandlerMessageIDs.USER_INFO_SUPPLEMENT, 
                                                mUserNameInfo.getText().toString(), mRealNameText, mPayPassWText);
            }
        }else{
            closeProgress();
        }        
    }

    @Override
    public void setRuleId(String ruleId) {
    }

    @Override
    public String getRuleId() {
        return null;
    }

    @Override
    public Context getContext() {
        return AlipayUserInfoSupplement.this;
    }
    
    void closeProgress(){
        try {
            if(mProgress!=null){
                mProgress.dismiss();
                mProgress = null;
            }
        } catch (Exception e) {
            //e.printStackTrace();
        }
    }    

}
