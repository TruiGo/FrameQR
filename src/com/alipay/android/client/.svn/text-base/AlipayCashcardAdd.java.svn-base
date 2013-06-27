
package com.alipay.android.client;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.DialogInterface;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.style.ForegroundColorSpan;
import android.text.style.StyleSpan;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnFocusChangeListener;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ScrollView;
import android.widget.Spinner;
import android.widget.TextView;

import com.alipay.android.client.constant.AlipayHandlerMessageIDs;
import com.alipay.android.client.constant.Constant;
import com.alipay.android.client.util.BaseHelper;
import com.alipay.android.client.util.LephoneConstant;
import com.alipay.android.client.util.Responsor;
import com.alipay.android.comon.component.ProgressDiv;
import com.alipay.android.util.LogUtil;
import com.eg.android.AlipayGphone.R;


public class AlipayCashcardAdd extends RootActivity {
	/**
	 * Tag for logcat
	 */
	private static final String LOG_TAG = "AlipayCashcardAdd";
	
	private MessageFilter mMessageFilter = new MessageFilter(this);
    Handler mHandler = new Handler(){
        public void handleMessage(Message msg){
        	Responsor responsor 	= (Responsor)msg.obj;
			JSONObject jsonResponse = responsor.obj;
			
			showResult(msg);
			
            switch(msg.what){
	            case AlipayHandlerMessageIDs.ACCOUNTOUT_GETINFO:
	            	getAccountInfoFinish(jsonResponse);
	            	break;
	            case AlipayHandlerMessageIDs.ACCOUNTOUT_COLLECTFINISH:
	            	accountCollectFinish(jsonResponse);
	            	break;
            }
            super.handleMessage(msg);
        }
    };
	
	
	/**
	 * Title Name
	 */
	private TextView mTitleName=null;
	
	private EditText mBankNumber=null;
	
	/**
	 * Variable for ProgressDialog when paying
	 */
	private ProgressDiv mProgress=null;
	
	
	
	private ScrollView mAccountOutputCanvas = null;
	private TextView mInfo=null;//������Ϣ
	private TextView mCanWithdraw = null;
	private Spinner mSelectBankSpinner = null;
	private ArrayAdapter<String> mBankNameadapter = null;
	private ArrayList<String> mBankId = new ArrayList<String>();
	private ArrayList<String> mType = new ArrayList<String>();
	private ArrayList<String> mDesc = new ArrayList<String>();
	private ArrayList<String> mCardWithdrawAmount = new ArrayList<String>();
	private String mCurrentType = null;//�������е�����
	private String mKatong = "katong";
	/**
	 * Handler for process Paying successful
	 */
	private int mErrorType = NO_ERROR;
	private static final int NO_ERROR = -5;
	private static final int ACCOUNT_OUT_FINISH = -4;
	private static final int ACCOUNT_OUT_START = -3;
	private static final int GET_ACCOUNT_FINISH = -2;
	private static final int GET_ACCOUNT_START = -1;
	
    private void showResult(Message msg) {
    	boolean tResultOK = false;
    	boolean needDismissProcessDialog=true;
    	
    	tResultOK = mMessageFilter.process(msg);
    	
    	if ((tResultOK) && (!getDataHelper().isCanceled())){
            if (mErrorType == GET_ACCOUNT_START){
            	mErrorType = GET_ACCOUNT_FINISH;
            }else if (mErrorType == ACCOUNT_OUT_START){
    			mErrorType = ACCOUNT_OUT_FINISH;
    		}
        }
    	
    	if (needDismissProcessDialog){
    		if (mProgress != null){
        		mProgress.dismiss();
        		mProgress=null;
        	}
    	}
    }
	
	/** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if(LephoneConstant.isLephone())
        {
        	getWindow().addFlags(LephoneConstant.FLAG_ROCKET_MENU_NOTIFY);
        }
        setContentView(R.layout.alipay_cashcard_add_320_480);

        loadAllVariables();
/*        
        mErrorType = GET_ACCOUNT_START;
        myHelper.sendPreDrawMoney(mHandler, AlipayHandlerMessageIDs.ACCOUNTOUT_GETINFO);
        openProcessDialog(getResources().getString(R.string.PleaseWait));
        
        Button tSure = (Button) findViewById(R.id.AccountOutputEnsureButton);
        tSure.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				accountOutEnsure();
			}
        });
*/        
    }
    
    private void loadAllVariables(){
    	
    	// Display Item Name.
    	mTitleName = (TextView) findViewById(R.id.title_text);
    	mTitleName.setText(R.string.CashcardAdding);
    	
    	mBankNumber = (EditText) findViewById(R.id.CashcardNumberInput);
    	mBankNumber.setOnFocusChangeListener(new OnFocusChangeListener() {
			@Override
			public void onFocusChange(View v, boolean hasFocus) {
				// TODO Auto-generated method stub
				if (mBankNumber.isFocusable()) {
					LogUtil.logAnyTime(LOG_TAG, "onFocusChange isFocusable mBankNumber");
//					int inputMode = WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN;
//					getWindow().setSoftInputMode(inputMode);
				}
			}
    	});
    	
    	String methodName = "李贺";
    	String noteStr = String.format(getString(R.string.CashcardAddNewPrompt), methodName);
    	mInfo = (TextView) findViewById(R.id.CashcardAddNewPrompt);
//    	mInfo.setText(noteStr);
    	int indexS = noteStr.indexOf("“") + 1;
    	int indexE = noteStr.indexOf("”");
    	SpannableStringBuilder strStyle=new SpannableStringBuilder(noteStr);  
    	strStyle.setSpan(new ForegroundColorSpan(Color.BLACK), indexS, indexE, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
    	strStyle.setSpan(new StyleSpan(android.graphics.Typeface.BOLD),indexS, indexE, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        mInfo.setText(strStyle);
//    	mInfo.setText(Html.fromHtml(noteStr.substring(0, indexS)
//    		    + "<b> <font color='black'>" + noteStr.substring(indexS, indexE)
//    		    + "</font> </b>" + noteStr.substring(indexE, noteStr.length())));

    	/*
    	mAccountOutputCanvas = (ScrollView) findViewById(R.id.AccountOutputCanvas);
    	mAccountOutputCanvas.setVisibility(View.INVISIBLE);
    	mInfo = (TextView) findViewById(R.id.AccountouputInfo);
    	mCheckMoneyInfo = (TextView) findViewById(R.id.CheckMoneyInfo);
    	mCanWithdraw = (TextView) findViewById(R.id.CanWithdraw);
    	mUpdateYourCredit = (TextView) findViewById(R.id.UpdateYourCredit);
    	mUpdateYourCredit.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View v)
			{
				jumpToHelp();
			}
    		
    	});
    	mSelectBankSpinner = (Spinner)findViewById(R.id.SelectBankSpinner);
    	mBankNameadapter = new ArrayAdapter<String>(this, R.layout.spinner_item);    	
    	mBankNameadapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
    	mSelectBankSpinner.setOnItemSelectedListener(new OnItemSelectedListener() 
		{
			@Override
			public void onItemSelected(AdapterView<?> parent, View view, int position, long id) 
			{
				try
				{
					mCurrentId = mBankId.get(position);
					mCurrentType = mType.get(position);
//					if(mCurrentType.equals(mKatong))
//					{
						mInfo.setText(mDesc.get(position));
						mCheckMoneyInfo.setText(mCardWithdrawAmount.get(position));
//					}
//					else
//					{
//						mInfo.setText(getString(R.string.BankCardInfo));
//					}
				}
				catch(Exception e)
				{
										
				}
			}
			@Override
			public void onNothingSelected(AdapterView<?> parent) 
			{
				
			}
		});
    	
    	*/
    }
    
    
    private void getAccountInfoFinish(JSONObject object){
    	if (mErrorType == GET_ACCOUNT_FINISH){
//    		Log.i(LOG_TAG, "get Account info OK");
    		mErrorType = NO_ERROR;
    		mAccountOutputCanvas.setVisibility(View.VISIBLE);
    		updateInfoView(object);
    	}else{
//    		Log.i(LOG_TAG, "get Account info Failed");
    		mErrorType = NO_ERROR;
    		if (getDataHelper().isCanceled()){
    			jumpToHome();
    		}
    	}
    }
    
    private void updateInfoView(JSONObject jsonResponse){
    	
    	
//		String tRealName = myHelper.mDefaultValueMap.get(Constant.RPF_USER_NAME);
		String tBalance = jsonResponse.optString(Constant.RPF_AVAILABLEAMOUNT); 
		String tCount = jsonResponse.optString(Constant.RPF_WITHDRAWCOUNT);
		String tCanWithdraw = mCanWithdraw.getText().toString();
		mCanWithdraw.setText(tCanWithdraw.replace("@", tCount));
//		TextView uiRealName = (TextView) findViewById(R.id.AORealName);
//		uiRealName.setText(tRealName);
//		TextView uiBalance = (TextView) findViewById(R.id.AMMaxNumberNote);
//		uiBalance.setText(tBalance);
		try
		{

			JSONArray arrayObj = jsonResponse.getJSONArray(Constant.RPF_BANKTABLELIST);

			mBankId.clear();
			mType.clear();
			mDesc.clear();
			mCardWithdrawAmount.clear();
			if(mBankNameadapter != null)
			{
				mBankNameadapter.clear();
			}
			for (int i = 0; i < arrayObj.length(); i++)
			{
				JSONObject objItem = arrayObj.getJSONObject(i);
				String tBandId = objItem.optString(Constant.RQF_NETBANK_BANKID);
				String type = objItem.optString(Constant.RPF_TYPE);
				String typeText = "";
				if(type.equals("katong"))
				{
					typeText = "��ͨ";
				}
				else
				{
					typeText = "";
				}
				mType.add(type);
				mBankNameadapter.add(objItem.optString(Constant.RPF_BANKNAME)+" ("+getString(R.string.LastNumber)+tBandId.substring(tBandId.length()-4)+")"+typeText);
				mBankId.add(tBandId);
				
				mDesc.add(objItem.optString(Constant.RQF_DESC));
				mCardWithdrawAmount.add(objItem.optString(Constant.RPF_CARDWITHDRAWAMOUNT));
			}
			
			mBankNameadapter.notifyDataSetChanged();
			
			if( mBankNameadapter.getCount() > 0 )
				mSelectBankSpinner.setSelection(0);
		}
		catch (JSONException e)
		{
			e.printStackTrace();
		}
		this.mSelectBankSpinner.setAdapter(mBankNameadapter);
		
    }
    
    
    
    DialogInterface.OnClickListener btnForOk = new DialogInterface.OnClickListener(){
		@Override
		public void onClick(DialogInterface dialog, int which) {
			// TODO Auto-generated method stub
			//AlipayMain.startMain(SubItemAccountOutputActivity.this);
//			BaseHelper.showDesktop(SubItemAccountOutputActivity.this);
		}
	};
    private void accountCollectFinish(JSONObject jsonResponse){
    	if (mErrorType == ACCOUNT_OUT_FINISH){
//    		Log.i(LOG_TAG, "Account Collect Ok!");
    		mErrorType = NO_ERROR;
    		String tTitle = "";
    		// paying successful. show info.
    		if(mCurrentType.equals(mKatong))
    		{
    			tTitle = getResources().getString(R.string.CheckCashSuccess);
    		}
    		else
    		{
    			tTitle = getResources().getString(R.string.CheckCashApplySuccess);
    		}
    		getDataHelper().showDialog(this, R.drawable.ok, 
    				tTitle,jsonResponse.optString(Constant.RPF_MEMO),  
    				getResources().getString(R.string.Ensure), btnForOk, 
    				null, null, null, null);
    	}else{
//    		Log.i(LOG_TAG, "Account Collect Failed!");
    		mErrorType = NO_ERROR;
    	}
    }
    
    
    
    
    
    private void jumpToHome(){
    	BaseHelper.showDesktop(this);
    	//finish();
    }
    
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
        	jumpToHome();
            return true;
        }

        return false;
    }
}
