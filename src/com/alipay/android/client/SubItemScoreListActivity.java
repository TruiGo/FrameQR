package com.alipay.android.client;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.alipay.android.appHall.Helper;
import com.alipay.android.client.constant.AlipayHandlerMessageIDs;
import com.alipay.android.client.constant.Constant;
import com.alipay.android.client.util.AlipayDataStore;
import com.alipay.android.client.util.BaseHelper;
import com.alipay.android.client.util.LephoneConstant;
import com.alipay.android.client.util.Responsor;
import com.alipay.android.comon.component.ProgressDiv;
import com.eg.android.AlipayGphone.R;



public class SubItemScoreListActivity extends RootActivity {
	
	
	/**
	 * Tag for logcat
	 */
	public static final String LOG_TAG = "AlipyDealQuery";
	
	public static Activity mActivity = null;
	/**
	 * Variable for ProgressDialog when paying
	 */
	private ProgressDiv mProgress=null;
	private MessageFilter mMessageFilter 	= new MessageFilter(this);
	
	//score message parameter
	private static final int ONEPAGECOUNT = 5;
	private int mScoreListTotalCount = 0; // trading message total count
	private int mScoreListGetPage = -1;  
	private int mScoreListGetCount = 0; 
	
	/**
	 * Deal Info Class.
	 * @author alex_hxq
	 *
	 */
	private class infoItem{
		// 
		
		String resultPointId="";
		String resultPointMemo="";
		String resultPointDate="";
		String resultPointValue="";
		 
	}
	
	private ArrayList<infoItem> mInfo = new ArrayList<infoItem>();
//	private RelativeLayout mContentCanvas=null;
	private ListView mListView=null;
	private TextView mHaveNOMsg=null;
	private TextView title = null;
	
	private Handler mHandler 				= new Handler(){

		@Override
		public void handleMessage(Message msg) 
		{
			
			Responsor responsor 	= (Responsor)msg.obj;
			JSONObject jsonResponse = null;
			boolean tResultOK = false;
			
			closeProgress();

			tResultOK = mMessageFilter.process(msg);
			if ((tResultOK) && (!getDataHelper().isCanceled())){
				jsonResponse = responsor.obj;
			}

			switch(msg.what)
			{
			case AlipayHandlerMessageIDs.SCORE_QUERY_LIST: //click remind list item
				if(jsonResponse != null){
					getScoreListForRefresh(jsonResponse);
				}
				break;			
			
			default:
				break;				
			}
			
			super.handleMessage(msg);
		}
		
	};

	@Override
	public void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
        if(LephoneConstant.isLephone())
        {
        	getWindow().addFlags(LephoneConstant.FLAG_ROCKET_MENU_NOTIFY);
        }
        mActivity = this;

        setContentView(R.layout.alipay_scorelist_320_480);
        loadAllVariables();
	}
	private void loadAllVariables(){
		
		//message list view
		mListView = (ListView)findViewById(R.id.ListViewCanvas);
		mHaveNOMsg = (TextView)findViewById(R.id.EmptyCanvas);
		title = (TextView)findViewById(R.id.title_text);
		title.setText(getResources().getString(R.string.score_bao));
		if(isFirstRun()){
			showInfoDialog();				
		}else{
			updateContentView();
		}
	}
	
	private void showInfoDialog(){
		View dialogview = LayoutInflater.from(this).inflate(R.layout.alipay_scoreintro_dialog, null);
		TextView jiFenTip = (TextView)dialogview.findViewById(R.id.JiFenTip);
		jiFenTip.setText(Helper.toDBC(getResources().getString(R.string.score_content)));
		AlertDialog.Builder dialog = new AlertDialog.Builder(SubItemScoreListActivity.this);
		dialog.setTitle(getResources().getString(R.string.score_info));
		dialog.setView(dialogview);
		//dialog.setMessage(getResources().getString(R.string.score_content));
		//dialog.setIcon(getResources().getDrawable(R.drawable.infoicon));
		dialog.setPositiveButton(getResources().getString(R.string.PhoneBindingEnsureButtonName), new OnClickListener() {

			   @Override
			   public void onClick(DialogInterface dialog, int which) {
				   updateContentView();
				   dialog.dismiss();
			    
			   }
			  });
		dialog.setCancelable(false);
		dialog.show();		
	}
	private boolean isFirstRun(){		
//		String firstrun = "FIRSTSCORE";		
		String firstrun = "FIRSTSCORE"+getAccountName(false);
		SharedPreferences   user = getSharedPreferences(AlipayDataStore.SETTING_INFOS, Context.MODE_PRIVATE); 
		boolean isFirst = user.getBoolean(firstrun, true);
		if(isFirst){
			Editor prefsPrivateEditor = user.edit();   
            prefsPrivateEditor.putBoolean(firstrun, false);   
            prefsPrivateEditor.commit();  
            }
//		return true;
		return isFirst;

	}
	
	 private void updateContentView(){
		 int cmd = AlipayHandlerMessageIDs.SCORE_QUERY_LIST;
//			String tTimeRange="3m";
//			String tTradeType=Constant.STATUS_WAITPAY + "|" + Constant.STATUS_CONFIRMGOODS;
			String tPageCount = Integer.toString(ONEPAGECOUNT);
			String tNextPage="1";
//			String tUserRole="buyer"; 
			
			mListView.setVisibility(View.GONE);
			mScoreListGetCount = 0;
			mInfo.clear();
			
			//myHelper.sendQueryTradeList(mHandler, cmd, tTimeRange, tTradeType, tNextPage, tPageCount, tUserRole);
			getDataHelper().sendQueryScoreList(mHandler, cmd, tNextPage, tPageCount);
			openProcessDialog(getResources().getString(R.string.getting_scorelist));
	    }
	 private void getMoreInfoFromNet()
		{
			int cmd = AlipayHandlerMessageIDs.SCORE_QUERY_LIST;
//			String tTimeRange="3m";
//			String tTradeType=Constant.STATUS_WAITPAY + "|" + Constant.STATUS_CONFIRMGOODS;
			String tPageCount = Integer.toString(ONEPAGECOUNT);
			String tNextPage="1";
//			String tUserRole="buyer";
			
//			mListView.setVisibility(View.GONE);
			
			if (mScoreListGetPage == -1){
				tNextPage = "1";
			}else if (mScoreListGetCount < mScoreListTotalCount){
	    		int nextPage = mScoreListGetPage + 1;
	    		tNextPage = Integer.toString(nextPage);
			}else{
				return;
			}
			
			//myHelper.sendQueryTradeList(mHandler, cmd, tTimeRange, tTradeType, tNextPage, tPageCount, tUserRole);
			getDataHelper().sendQueryScoreList(mHandler, cmd, tNextPage, tPageCount);
			openProcessDialog(getResources().getString(R.string.getting_scorelist));	
		} 
	 	private infoItem makeItem(JSONObject itemObj){
	    	final infoItem tItem = new infoItem();

	    	tItem.resultPointId=itemObj.optString(Constant.RPF_TRANSID);
	    	tItem.resultPointMemo=itemObj.optString(Constant.RPF_TRANSMEMO);
	    	tItem.resultPointDate=itemObj.optString(Constant.RPF_TRANSDATE);
	    	tItem.resultPointValue=itemObj.optString(Constant.RPF_TRANSMONEY);
			
	    	return tItem;
	 	}
		private int getScoreListForRefresh(JSONObject jsonResponse){
			int iRet = 0;
			JSONArray arrayObj;
	    	JSONObject itemObj;
	    	
	    	mListView.setVisibility(View.VISIBLE);
	    	if (mScoreListGetPage != -1)
	    	{
	    		if(mScoreListGetCount<mScoreListTotalCount)
	    		{
	    			if(mInfo.size()>0)
	    			{
						int location = mInfo.size() - 1;
						mInfo.remove(location);
	    			}
	    		}
	    	}

	    	try{
	    		if(jsonResponse != null)
	    		{
	    			arrayObj = jsonResponse.getJSONArray(Constant.RPF_POINTLIST);
	    			if(arrayObj != null)
	    			{
	    			mScoreListTotalCount = Integer.valueOf(jsonResponse.optString(Constant.RPF_TOTALCOUNT));
	    			mScoreListGetPage = Integer.valueOf(jsonResponse.optString(Constant.RPF_PAGE));

	    			for(int i=0; i<arrayObj.length(); i++) {
	    				itemObj = arrayObj.getJSONObject(i);
	    				infoItem info = makeItem(itemObj);
	    				mInfo.add(info); 
	    				

	    				mScoreListGetCount++;
	    			}
	    			}
	    		}
	    	}catch(JSONException e){
	    		e.printStackTrace();
	    	}
	    	if(mInfo.size() == 0)
	    	{
	    		mScoreListTotalCount = 0; // trading message total count
	    		mScoreListGetPage = -1;  
	    		mScoreListGetCount = 0; 
	    		
	    	}
	    	if(mScoreListGetCount < mScoreListTotalCount)
	    	{
	    		infoItem tItem = new infoItem();
				mInfo.add(tItem);
	    	}
	    	
	    	if(mInfo.size() != 0)
	    	{
	    		mHaveNOMsg.setVisibility(View.GONE);
	    		refreshTradingRemindList();
	    	}
	    	else
	    	{
	    		mListView.setVisibility(View.GONE);
	    		mHaveNOMsg.setVisibility(View.VISIBLE);
	    	}
	    	
	    	return iRet;
		}
		private void refreshTradingRemindList()
		{
			ScoreListAdapter listAdapter = new ScoreListAdapter(this, mInfo);
			mListView.setAdapter(listAdapter);
			mListView.setSelection(mInfo.size());
			mListView.setOnItemClickListener(mScoreListClick);
			
		}
		private OnItemClickListener mScoreListClick = new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				if ((mScoreListGetCount < mScoreListTotalCount)
						&& (arg2 == mScoreListGetCount)){
					getMoreInfoFromNet();	
				}
				
				
			}
		};
	    private class ScoreListAdapter extends BaseAdapter{

	    	private ArrayList<infoItem> mList=null;
	    	private Context mContext;

	    	public ScoreListAdapter(Context c, ArrayList<infoItem> list){
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
			
			@Override
			public boolean isEnabled(int position) {
				if ((mScoreListGetCount < mScoreListTotalCount)
						&& (position == mScoreListGetCount)){
					return true;
				}else{
				return false;	}
			}
			
			private class SubItem{
				TextView mTransId;
				TextView mMoney;
				TextView mDate;
				TextView mMemo;
				TextView mMore;
				ImageView listDivide;
			}
			@Override
			public View getView(int position, View convertView, ViewGroup arg2) 
			{
				// TODO Auto-generated method stub
				final SubItem tSubItem;
				if(convertView == null){
					tSubItem = new SubItem();
					

					convertView = LayoutInflater.from(mContext).inflate(R.layout.alipay_scorelist_item, null);
					tSubItem.mTransId = (TextView)convertView.findViewById(R.id.PointId);
					tSubItem.mMoney = (TextView)convertView.findViewById(R.id.PointValue);
					tSubItem.mDate = (TextView)convertView.findViewById(R.id.PointDate);
					tSubItem.mMemo = (TextView)convertView.findViewById(R.id.PointMemo);
					
					tSubItem.mMore = (TextView)convertView.findViewById(R.id.MoreItem);
					tSubItem.listDivide = (ImageView)convertView.findViewById(R.id.list_divide);
					BaseHelper.fixBackgroundRepeat(tSubItem.listDivide);
					convertView.setTag(tSubItem);
					
				}else{
					tSubItem = (SubItem)convertView.getTag();
				}
				
				tSubItem.mTransId.setText(mList.get(position).resultPointId);
				tSubItem.mMoney.setText(mList.get(position).resultPointValue);
				tSubItem.mDate.setText(mList.get(position).resultPointDate);
				tSubItem.mMemo.setText(mList.get(position).resultPointMemo);
				tSubItem.listDivide.setVisibility(View.VISIBLE);
				
				if ((mScoreListGetCount < mScoreListTotalCount)
						&& (position == mScoreListGetCount)){
					tSubItem.mMore.setVisibility(View.VISIBLE);
					tSubItem.listDivide.setVisibility(View.GONE);
					convertView.setBackgroundResource(R.drawable.alipay_list_iteminfo_bg);
				}else{
					tSubItem.mMore.setVisibility(View.GONE);
				}
				
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
	
}
