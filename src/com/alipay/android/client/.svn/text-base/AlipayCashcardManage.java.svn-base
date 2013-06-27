package com.alipay.android.client;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.alipay.android.client.constant.AlipayHandlerMessageIDs;
import com.alipay.android.client.constant.Constant;
import com.alipay.android.client.util.Responsor;
import com.alipay.android.comon.component.ProgressDiv;
import com.alipay.android.util.LogUtil;
import com.eg.android.AlipayGphone.R;

public class AlipayCashcardManage extends RootActivity {
	/**
	 * Tag for logcat
	 */
	private static final String LOG_TAG = "AlipayCashcardManage";
	
	public static Activity mActivity = null;
	
//	private Intent mIntent=null;
	private TextView mTitleName=null;
	 
	private ArrayList<bankInfoItem> mInfo = new ArrayList<bankInfoItem>();
//	private ArrayList<String> mBankId = new ArrayList<String>();
//	private ArrayList<String> mBankName = new ArrayList<String>();
//	private ArrayList<String> mType = new ArrayList<String>();
//	private ArrayList<String> mDesc = new ArrayList<String>();
//	private ArrayList<String> mCardWithdrawAmount = new ArrayList<String>();
	
	//score message parameter
//	private static final int ONEPAGECOUNT = 5;
	private int mCashcardListTotalCount = 0; // trading message total count
//	private int mCashcardListGetPage = -1;  
	private int mCashcardListGetCount = 0; 
	
//	private RelativeLayout mContentCanvas=null;
	private ListView mListView=null;
	private TextView mHaveNOMsg=null;
	private Button mBtnAddnew=null;
	
	final CharSequence[] actionItems = {"编辑", "删除", "设置为优先"};
	
	private int mErrorType = NO_ERROR;
	private static final int NO_ERROR = -5;
	private static final int ACCOUNT_OUT_FINISH = -4;
	private static final int ACCOUNT_OUT_START = -3;
	private static final int GET_CARDLIST_FINISH = -2;
	private static final int GET_CARDLIST_START = -1;
		
	private ProgressDiv mProgress=null;
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
//	            	accountCollectFinish();
	            	break;
            }
            super.handleMessage(msg);
        }
    };
    
    private void showResult(Message msg) {
    	boolean tResultOK = false;
    	boolean needDismissProcessDialog=true;
    	
    	tResultOK = mMessageFilter.process(msg);
    	
    	if ((tResultOK) && (!getDataHelper().isCanceled())){
            if (mErrorType == GET_CARDLIST_START){
            	mErrorType = GET_CARDLIST_FINISH;
            }else if (mErrorType == ACCOUNT_OUT_START){
    			mErrorType = ACCOUNT_OUT_FINISH;
    		}
        }
    	
    	if (needDismissProcessDialog){
    		closeProgress();
    	}
    }
    
    private void getAccountInfoFinish(JSONObject object){
    	if (mErrorType == GET_CARDLIST_FINISH){
    		LogUtil.logAnyTime(LOG_TAG, "get Account info OK");
    		mErrorType = NO_ERROR;

    		getCashcardListForRefresh(object);
    	}else{
//    		LogUtil.logAnyTime(LOG_TAG, "get Account info Failed");
    		mErrorType = NO_ERROR;
    		if (getDataHelper().isCanceled()){
//    			jumpToHome();
    		}
    	}
    }
    
    
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        setContentView(R.layout.alipay_cashcard_manage_320_480);
        mActivity = this;

        loadAllVariables();
    }
    
    private void loadAllVariables(){
    	// Display Item Name.
    	mTitleName = (TextView) findViewById(R.id.title_text);
    	mTitleName.setText(R.string.CashcardManaging);
    	
    	mListView = (ListView)findViewById(R.id.CashcardListViewCanvas);
    	mListView.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                	mHandler.postAtFrontOfQueue(new Runnable() {
                        public void run() {
//                        	mListView.setSelection(0);
                        }
                    });
                }
            }
        });
    	mHaveNOMsg = (TextView)findViewById(R.id.EmptyCanvas);
    	
    	mBtnAddnew = (Button)findViewById(R.id.CashcardAddButton);
    	mBtnAddnew.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View v)
			{
				//尚未设置提现银行帐号，直接跳转到添加银行卡界面
				Intent tIntent = new Intent(AlipayCashcardManage.this, AlipayCashcardAdd.class);
				startActivity(tIntent);
			}
    		
    	});
    }
    
    @Override
	protected void onStart() {
		// TODO Auto-generated method stub
		super.onStart();
		
		mInfo.clear();
		
		mErrorType = GET_CARDLIST_START;
		getDataHelper().sendPreDrawMoney(mHandler, AlipayHandlerMessageIDs.ACCOUNTOUT_GETINFO);
        openProcessDialog(getResources().getString(R.string.PleaseWait));
	}
    
    private int getCashcardListForRefresh(JSONObject jsonResponse){
		int iRet = 0;
		JSONArray arrayObj;
    	JSONObject itemObj;
    	
    	mListView.setVisibility(View.VISIBLE);
    	
//    	if (mCashcardListGetPage != -1)
//    	{
//    		if(mCashcardListGetCount < mCashcardListTotalCount)
//    		{
//    			if(mInfo.size()>0)
//    			{
//					int location = mInfo.size() - 1;
//					mInfo.remove(location);
//    			}
//    		}
//    	}

    	try{
    		if(jsonResponse != null)
    		{
    			arrayObj = jsonResponse.getJSONArray(Constant.RPF_BANKTABLELIST);
    			if(arrayObj != null)
    			{
//	    			mCashcardListTotalCount = Integer.valueOf(jsonResponse.optString(Constant.RPF_TOTALCOUNT));
//	    			mCashcardListGetPage = Integer.valueOf(jsonResponse.optString(Constant.RPF_PAGE));
	
	    			for(int i=0; i<arrayObj.length(); i++) {
	    				itemObj = arrayObj.getJSONObject(i);
	    				bankInfoItem info = makeItem(itemObj);
	    				mInfo.add(info); 
	
	    				mCashcardListGetCount++;
	    			}
    			}
    		}
    	}catch(JSONException e){
    		e.printStackTrace();
    	}
    	
    	if(mInfo.size() == 0)
    	{
    		mCashcardListTotalCount = 0; // trading message total count
//    		mCashcardListGetPage = -1;  
    		mCashcardListGetCount = 0; 
    		
    	}
    	
    	if(mCashcardListGetCount < mCashcardListTotalCount)
    	{
    		bankInfoItem tItem = new bankInfoItem();
			mInfo.add(tItem);
    	}
    	
    	if(mInfo.size() != 0)
    	{
    		mHaveNOMsg.setVisibility(View.GONE);
    		refreshTradingRemindList();
    	} else {
    		mListView.setVisibility(View.GONE);
    		mHaveNOMsg.setVisibility(View.VISIBLE);
    	}
    	
    	return iRet;
	}
    
    private bankInfoItem makeItem(JSONObject itemObj){
    	final bankInfoItem tItem = new bankInfoItem();

    	tItem.mBankId = itemObj.optString(Constant.RPF_BANKID);
    	tItem.mBankName = itemObj.optString(Constant.RPF_BANKNAME);
    	String type = itemObj.optString(Constant.RPF_TYPE);
    	if(type != null && type.equals("katong"))
		{
    		tItem.mBankType = "卡通";
		}
		else
		{
			tItem.mBankType = "";
		}
    	
    	tItem.mBankDesc = itemObj.optString(Constant.RQF_DESC);
    	tItem.mBankDrawAmount = itemObj.optString(Constant.RPF_CARDWITHDRAWAMOUNT);
		
    	return tItem;
 	}
    
    private void refreshTradingRemindList()
	{
    	CashBankListAdapter listAdapter = new CashBankListAdapter(this, mInfo);
		mListView.setAdapter(listAdapter);
		mListView.setSelection(mInfo.size());
		mListView.setOnItemClickListener(mCashBankListClick);
		
	}
    
	private OnItemClickListener mCashBankListClick = new OnItemClickListener() {
		@Override
		public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
//			if ((mCashcardListGetCount < mCashcardListTotalCount)
//					&& (arg2 == mCashcardListGetCount)){
//				getMoreInfoFromNet();	
//			} else 
			{
				//给出动作选择对话框
				AlertDialog.Builder builder = new AlertDialog.Builder(mActivity);
				builder.setTitle("选择操作");
				builder.setItems(actionItems, new DialogInterface.OnClickListener() {
				    public void onClick(DialogInterface dialog, int item) {
				    	//
				    	Toast.makeText(getApplicationContext(), actionItems[item], Toast.LENGTH_SHORT).show();
				    }
				});
				AlertDialog alert = builder.create();
				alert.show();
				
				LogUtil.logAnyTime(LOG_TAG, "mCashBankListClick create AlertDialog!");

			}
		}
	};
	
//	private void getMoreInfoFromNet()
//	{
//		//
//	}
	
    private class CashBankListAdapter extends BaseAdapter{

    	private ArrayList<bankInfoItem> mList=null;
    	private Context mContext;

    	public CashBankListAdapter(Context c, ArrayList<bankInfoItem> list){
    		mList = list;
    		mContext = c;
    	}
    	
		@Override
		public int getCount() {
			// TODO Auto-generated method stub
			return mList.size();
		}

		@Override
		public Object getItem(int arg0) {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public long getItemId(int arg0) {
			// TODO Auto-generated method stub
			return arg0;
		}
		
		private class SubItem{
			TextView mBankInfo;
//			TextView mBankType;
//			TextView mBankPriority;
			TextView mBankDesc;
			TextView mBankDrawAmount;
			
			TextView mMore;
		}
		
		@Override
		public View getView(int position, View convertView, ViewGroup arg2) 
		{
			// TODO Auto-generated method stub
			final SubItem tSubItem;
			
			if(convertView == null){
				tSubItem = new SubItem();

				convertView = LayoutInflater.from(mContext).inflate(R.layout.alipay_cashcard_list_item, null);
				tSubItem.mBankInfo = (TextView)convertView.findViewById(R.id.CashcardBankInfo);
				//CashcardPriority
				tSubItem.mBankDesc = (TextView)convertView.findViewById(R.id.CashcardTimeInfo);
				tSubItem.mBankDrawAmount = (TextView)convertView.findViewById(R.id.CashcardLimitInfo);
				
				tSubItem.mMore = (TextView)convertView.findViewById(R.id.MoreItem);
				
				convertView.setTag(tSubItem);
				
			}else{
				tSubItem = (SubItem)convertView.getTag();
			}
			
			String tBankId = mList.get(position).mBankId;
			String tBankInfo = mList.get(position).mBankName
				+ " (" + getString(R.string.LastNumber) + tBankId.substring(tBankId.length()-4)+") "
				+mList.get(position).mBankType;
			int indexS = tBankInfo.indexOf("(");
	    	int indexE = tBankInfo.indexOf(")")+1;
			tSubItem.mBankInfo.setText(Html.fromHtml(tBankInfo.substring(0, indexS)
					+ "<b> <font color='red'>" + tBankInfo.substring(indexS, indexE)
					+ "</font> </b>" + tBankInfo.substring(indexE, tBankInfo.length())));
//			tSubItem.mBankInfo.setText(tBankInfo);
//			tSubItem.mBankPriority.setText(mList.get(position).mBankType);
			tSubItem.mBankDesc.setText(mList.get(position).mBankDesc);
			tSubItem.mBankDrawAmount.setText(mList.get(position).mBankDrawAmount);
			
//			if ((mCashcardListGetCount < mCashcardListTotalCount)
//					&& (position == mCashcardListGetCount)){
//				tSubItem.mMore.setVisibility(View.VISIBLE);
//			}else{
				tSubItem.mMore.setVisibility(View.GONE);
//			}
			
			return convertView;

		}
    }

	private void openProcessDialog(String msg){
		if (mProgress == null){
			mProgress = getDataHelper().showProgressDialogWithCancelButton(this, null, 
					msg, 
					false, true, 
					getDataHelper().cancelListener,
					getDataHelper().cancelBtnListener);
		}
	}
	
	private void closeProgress(){
		if (mProgress != null){	
		mProgress.dismiss();
		mProgress=null;
		}
	}
	
	private class bankInfoItem{
		String mBankId="";
		String mBankName="";
		String mBankDesc="";
		String mBankType="";
		String mBankDrawAmount=""; 
	}
    
}    