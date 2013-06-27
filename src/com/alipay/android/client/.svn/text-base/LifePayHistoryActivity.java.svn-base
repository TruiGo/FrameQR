package com.alipay.android.client;

import java.text.SimpleDateFormat;
import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Shader;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.LayerDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.alipay.android.client.constant.AlipayHandlerMessageIDs;
import com.alipay.android.client.constant.Constant;
import com.alipay.android.client.lifePayment.HistoryBillInfo;
import com.alipay.android.client.util.BaseHelper;
import com.alipay.android.client.util.Responsor;
import com.alipay.android.comon.component.ProgressDiv;
import com.alipay.android.log.AlipayLogAgent;
import com.alipay.android.log.Constants;
import com.alipay.android.log.StorageStateInfo;
import com.alipay.android.ui.adapter.LifePayHistoryAdapter;
import com.eg.android.AlipayGphone.R;

public class LifePayHistoryActivity extends RootActivity implements OnClickListener,OnItemClickListener{

	private ListView mLifepayHistoryListView;
	private LinearLayout footerView;
	private LinearLayout mCreatNewBill;
	private TextView titleView;
	private String mHistoryType = ""; //历史记录类型：水费、电费、燃气、固话宽带
	private String jumpHistoryFrom = "";
	private ProgressDiv mProgress = null;
//	private ImageView mBillListDivide1 = null;
//	private ImageView mBillListDivide2 = null;
	private ImageView mBillListDivide3 = null;
	private ImageView mListEnd = null;
	private final int NO_HISTORY = 501;//没有历史记录
	private final int RESULT_OK = 100;
	private String mStartTime;
	private String mEndTime;
	private String appID = "";
	
	private ArrayList<HistoryBillInfo> mHistoryBillInfos = new ArrayList<HistoryBillInfo>();
	private LifePayHistoryAdapter  historyAdapter;
	private MessageFilter mMessageFilter = new MessageFilter(this);
	private SimpleDateFormat sDateFormat = new SimpleDateFormat("hhmmssSSS");
	private StorageStateInfo mStorageStateInfo = StorageStateInfo.getInstance();
	private final String viewID = "HistoryListView";
	private String viewType = "";
	private Handler mHandler = new Handler(){
		@Override
		public void handleMessage(Message msg) {
			if(msg.obj != null && msg.what == AlipayHandlerMessageIDs.GET_LIFEPAY_HISTORY_BILLS){
				showResult(msg);
			}
			super.handleMessage(msg);
		}	
	};
	
	private void showResult(Message msg){
		Responsor responsor = (Responsor) msg.obj;
		JSONObject obj = responsor.obj;
		int responsorstatus = responsor.status;
		
		if(responsorstatus == NO_HISTORY){
			mEndTime = sDateFormat.format(new java.util.Date());
			footerView.setVisibility(View.GONE);
			if(jumpHistoryFrom.equalsIgnoreCase(Constant.JUMP_HISTORY_FROM_HALL)){//从九宫格进的历史，如果没有历史，直接跳表单页面
				if((Integer.parseInt(mEndTime) - Integer.parseInt(mStartTime))<1000){//防止请求和响应之间的时间太短而出现闪屏。
					try {
						Thread.sleep(1000-(Integer.parseInt(mEndTime) - Integer.parseInt(mStartTime)));
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
				closeProgress();
				Intent intent = new Intent(this,SubItemPucPayActivity.class);
				intent.putExtra(Constant.PUBLIC_PAY_URL, mHistoryType);
				this.startActivity(intent);
				this.finish();
			}else if(jumpHistoryFrom.equalsIgnoreCase(Constant.JUMP_HISTORY_FROM_INPUT)){//从表单页面进的历史，如果没有历史，则给出弹出提示
				closeProgress();
				getDataHelper().showDialog(this, -1, "", responsor.memo, 
						                  getResources().getString(R.string.Ensure),
						                  new DialogInterface.OnClickListener() {
												@Override
												public void onClick(DialogInterface dialog, int which) {
													LifePayHistoryActivity.this.finish();
												}
												}, null, null, null, null);
			}
		}
		
		else if(responsorstatus ==  RESULT_OK && (!getDataHelper().isCanceled())){
			closeProgress();
			String historyRecordsStr = obj.optString("historyRecords");
			try {
				JSONArray historyRecordsStrJSONArray = new JSONArray(historyRecordsStr);
				if(historyRecordsStrJSONArray != null){
					for(int i=0;i<historyRecordsStrJSONArray.length();i++){
						JSONObject history = historyRecordsStrJSONArray.optJSONObject(i);
						if(history != null){
							HistoryBillInfo historyBillInfo = new HistoryBillInfo();
							historyBillInfo.billKey = history.optString(Constant.BILL_KEY);
							historyBillInfo.ownerName = history.optString(Constant.BILL_OWNERNAME);
							historyBillInfo.chargeCompanyName = history.optString(Constant.BILL_CHARGEINSTNAME);
							historyBillInfo.chargeCompanySname = history.optString(Constant.BILL_CHARGECOMPANYSNAME);
							historyBillInfo.city = history.optString(Constant.BILL_CITY);
							historyBillInfo.isReminded = history.optString(Constant.BILL_ISREMINDED);
							historyBillInfo.remindDate = history.optString(Constant.BILL_REMINDDATE);							
							historyBillInfo.originalData = history.toString();
							mHistoryBillInfos.add(historyBillInfo);
						}
						//如果用是从表单页面跳转到历史记录，就不显示button，从九宫格页面跳转到的就显示。
						if(jumpHistoryFrom.equalsIgnoreCase(Constant.JUMP_HISTORY_FROM_INPUT)){
							footerView.setVisibility(View.GONE);
						}else{
							footerView.setVisibility(View.VISIBLE);
						}
						historyAdapter.notifyDataSetChanged();    
					}
				}
			} catch (JSONException e) {
				e.printStackTrace();
			}
			
		}else {
			closeProgress();
			mMessageFilter.process(msg);
		}
		
	}
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.lifepay_history_list);
		loadAllVariables();
		
		if(jumpHistoryFrom.equalsIgnoreCase(Constant.JUMP_HISTORY_FROM_HALL)){
			jumpHistoryFrom = Constant.JUMP_HISTORY_FROM_HALL;
			if(getUserData() != null){//从九宫格进历史，如果登录则取历史
				getLifeypayHistory();
				mStartTime = sDateFormat.format(new java.util.Date());
			}else {//如果没登录，直接到表单填写页面。
				Intent intent = new Intent(this,SubItemPucPayActivity.class);
				intent.putExtra(Constant.PUBLIC_PAY_URL, mHistoryType);
				this.startActivity(intent);
				this.finish();
			}
		}
		if(jumpHistoryFrom.equalsIgnoreCase(Constant.JUMP_HISTORY_FROM_INPUT)){
			jumpHistoryFrom = Constant.JUMP_HISTORY_FROM_INPUT;
			if(getUserData() == null){//从表单页面进历史，如果没有登录，则要先登录
			    Intent intent = new Intent(this,Login.class);
			    startActivityForResult(intent,Constant.LIFE_PAY_HISTORY_REQUEST_LOGIN);
			}else{
				getLifeypayHistory();
			}
		}
	}
	
	private void loadAllVariables() {
		Intent intent = getIntent();
		if(intent != null){
			mHistoryType = intent.getStringExtra(Constant.PUBLIC_PAY_URL);
			jumpHistoryFrom  = intent.getStringExtra(Constant.JUMP_HISTORY_FROM);
		}
		titleView = (TextView) findViewById(R.id.title_text);
		initTitle();
		mLifepayHistoryListView = (ListView) findViewById(R.id.lifepayHistoryList);
		
		historyAdapter = new LifePayHistoryAdapter(mHistoryBillInfos,mHistoryType,this);
		footerView = (LinearLayout) LayoutInflater.from(this).inflate(R.layout.llistview_bottombutton, null);
//		mBillListDivide1 = (ImageView) findViewById(R.id.billListDivide1);
//		mBillListDivide2 = (ImageView) findViewById(R.id.billListDivide2);
		mBillListDivide3 = (ImageView) footerView.findViewById(R.id.billListDivide3);
		mListEnd = (ImageView) footerView.findViewById(R.id.ListEnd);
		mLifepayHistoryListView.addFooterView(footerView);
		mLifepayHistoryListView.setAdapter(historyAdapter);
		mLifepayHistoryListView.setOnItemClickListener(this);
		footerView.setOnClickListener(this);
		footerView.setVisibility(View.GONE);
		imageRepeat();
	}
	
	private void imageRepeat() {
		LinearLayout historyLayout = (LinearLayout) findViewById(R.id.historyLayout);
		LayerDrawable ld1 = (LayerDrawable) historyLayout.getBackground();
		((BitmapDrawable) ld1.getDrawable(0)).setTileModeXY(
				Shader.TileMode.REPEAT, Shader.TileMode.REPEAT);

//		BaseHelper.fixBackgroundRepeat(mBillListDivide1);
//		BaseHelper.fixBackgroundRepeat(mBillListDivide2);
		BaseHelper.fixBackgroundRepeat(mBillListDivide3);
		BaseHelper.fixBackgroundRepeat(mListEnd);
	}
	
	private  void initTitle(){
		   if(mHistoryType.equalsIgnoreCase(Constant.URL_WATER_RATE)){//水费
			    viewType = "water";
			    appID = "09999995";
		    	titleView.setText(getResources().getString(R.string.WaterRate));
		    }else if(mHistoryType.equalsIgnoreCase(Constant.URL_POWER_RATE)){//电费
		    	viewType = "electricity";
		    	appID = "09999996";
		    	titleView.setText(getResources().getString(R.string.PowerRate));
		    }else if(mHistoryType.equalsIgnoreCase(Constant.URL_GAS_RATE)){//燃气
		    	viewType = "gas";
		    	appID = "09999997";
		    	titleView.setText(getResources().getString(R.string.GasRate));
		    }else if(mHistoryType.equalsIgnoreCase(Constant.URL_COMMUN_RATE)){//固话宽带
		    	viewType = "wideline";
		    	appID = "09999998";
		    	titleView.setText(getResources().getString(R.string.PhoneAndWideBand));
		    }
	}
	/**
	 *获取生活缴费的历史记录
	 ****/
	private void getLifeypayHistory(){
		getDataHelper().sendGetLifePayHistory(mHandler
				     , AlipayHandlerMessageIDs.GET_LIFEPAY_HISTORY_BILLS, mHistoryType);
		openProcessDialog(getResources().getString(R.string.GettingHistory));
	}
	
	@Override
	protected void onNewIntent(Intent intent) {
		if(intent != null && intent.getStringExtra(Constant.JUMP_HISTORY_FROM)
				                   .equalsIgnoreCase(Constant.JUMP_HISTORY_FROM_INPUT)){
		     mCreatNewBill.setVisibility(View.GONE);
		     jumpHistoryFrom = Constant.JUMP_HISTORY_FROM_INPUT;
		}
		super.onNewIntent(intent);
	}
	
	
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if(requestCode == Constant.LIFE_PAY_HISTORY_REQUEST_LOGIN 
				&& resultCode == Activity.RESULT_OK){//登录后，请求历史账号记录。
			getLifeypayHistory();	
		}else if(resultCode == Activity.RESULT_CANCELED){//处理Login界面按返回键，按返回键需要返回到表单界面
			this.finish();
		}
		
	}
	@Override
	public void onClick(View v) {
		 AlipayLogAgent.writeLog(this, Constants.BehaviourID.CLICKED, null, null, appID, null, viewType + viewID, viewType + "InputView", "createNewLifePayBillButton", null);
		
		Intent intent = new Intent(this,SubItemPucPayActivity.class);
		intent.putExtra(Constant.PUBLIC_PAY_URL, mHistoryType);
		intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
		this.startActivity(intent);
//		finish();
		
	}
	
	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position,
			long id) {
		HistoryBillInfo selectHistoryBillInfo = (HistoryBillInfo) mLifepayHistoryListView.getItemAtPosition(position);
		Intent resultIntent = new Intent(LifePayHistoryActivity.this,SubItemPucPayActivity.class);
		Bundle bundle = new Bundle();
		bundle.putSerializable(Constant.LIFEPAY_HISTORYINFO_KEY, selectHistoryBillInfo);
		resultIntent.putExtras(bundle);
		//数据埋点。
//		AlipayLogAgent.onPageJump(this, 
//				                 viewType + viewID, viewType + "InputView", 
//				                 mStorageStateInfo.getValue(Constants.STORAGE_APPID),
//				                 mStorageStateInfo.getValue(Constants.STORAGE_APPVERSION),
//				                 mStorageStateInfo.getValue(Constants.STORAGE_PRODUCTID), 
//				                 getUserId(),
//				                 mStorageStateInfo.getValue(Constants.STORAGE_PRODUCTVERSION),
//				                 mStorageStateInfo.getValue(Constants.STORAGE_CLIENTID));
		AlipayLogAgent.writeLog(this, Constants.BehaviourID.CLICKED, null, null, appID, null, viewType + viewID, viewType + "InputView" , "clickBillAccountButton", null);
		if(jumpHistoryFrom.equalsIgnoreCase(Constant.JUMP_HISTORY_FROM_HALL)){
			resultIntent.putExtra(Constant.PUBLIC_PAY_URL, mHistoryType);
			startActivity(resultIntent);
		}else{
			this.setResult(Activity.RESULT_OK, resultIntent);
			this.finish();
		}
	}
	
	private void openProcessDialog(String msg) {
		if (mProgress == null) {
			mProgress = getDataHelper().showProgressDialogWithCancelButton(this, null,
					msg, false, true, getDataHelper().cancelListener,
					getDataHelper().cancelBtnListener);
		}
	}
	
	private void closeProgress() {
		if (mProgress != null) {
			mProgress.dismiss();
			mProgress = null;
		}
	}
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
//		if (keyCode == KeyEvent.KEYCODE_BACK) {
//		AlipayLogAgent.onEvent(this,
//	              Constants.MONITORPOINT_EVENT_VIEWRETURN,
//	              "", 
//	              viewType + viewID, 
//	              mStorageStateInfo.getValue(Constants.STORAGE_APPID), 
//	              mStorageStateInfo.getValue(Constants.STORAGE_APPVERSION),
//	              mStorageStateInfo.getValue(Constants.STORAGE_PRODUCTID), 
//	              getUserId(), 
//	              mStorageStateInfo.getValue(Constants.STORAGE_PRODUCTVERSION),
//	              mStorageStateInfo.getValue(Constants.STORAGE_CLIENTID));
//		}
		 AlipayLogAgent.writeLog(this, Constants.BehaviourID.CLICKED, null, null, appID, null, viewType + viewID, Constants.APPCENTER, "backIcon", null);
		return super.onKeyDown(keyCode, event);
	}
	
}
