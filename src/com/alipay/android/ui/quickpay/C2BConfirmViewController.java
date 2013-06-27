package com.alipay.android.ui.quickpay;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.alipay.android.client.constant.Constant;
import com.alipay.android.client.util.BaseHelper;
import com.alipay.android.ui.adapter.QuickPayBillListAdapter;
import com.alipay.android.ui.framework.BaseViewController;
import com.alipay.android.util.HideUtils;
import com.eg.android.AlipayGphone.R;

public class C2BConfirmViewController extends BaseViewController implements OnClickListener{
	private TextView mTitleView = null;
	private TextView mCompanyNameTextView = null;
	private TextView mBillNumberTextView = null;
	private TextView mTotalMonyTextView = null;
	private ListView mBillDetailInforList = null;
	private ImageView mOverLayListButton = null;
	private Button mButtonNext = null;
	private RelativeLayout headView;
	private LinearLayout mAlipaySubTitle = null;
	private RelativeLayout mC2bLayout = null;
	private LinearLayout mConmpayNameLayout = null;
	private ImageView mDevide4 = null;
//	private ImageView mDevide5 = null;
	private ImageView mDevide6 = null;
	private RelativeLayout mBillNumberLayout = null;
	private RelativeLayout  mTotalMonyLayout = null;
	private Button mReSend = null;
	private RelativeLayout mBillDetailInforLayout = null;
	
	
	private String mCompanyName ="";
	private String mBillNumber ="";
	private String mTotalMoney = "";
	private JSONObject pushData = null;
	private JSONArray pushBillInfos = null;
	private QuickPayBillListAdapter mQuickPayBillListAdapter = null;
	private boolean isOpen = false;
	private ArrayList<Billinfo> billInfosDatas = new ArrayList<Billinfo>();
	
	public static final String C2CPAY = "c2cpay"; 
	
	@Override
	protected void onCreate() {
		super.onCreate();
		pushData = (JSONObject)params;
		mView = LayoutInflater.from(this.getRootController()).inflate(R.layout.quickpay_c2b_confirminfor, null, false);
		addView(mView, null);
		loadAllVariables();
		
		
	}
	
	private void loadAllVariables() {
		mTitleView = (TextView) findViewById(R.id.title_text);
		mTitleView.setText(getRootController().getResources().getString(R.string.quickpay));
		
		
		mAlipaySubTitle = (LinearLayout) findViewById(R.id.AlipaySubTitle);
		mButtonNext = (Button) findViewById(R.id.buttonNext);
		mButtonNext.setOnClickListener(this);
		mConmpayNameLayout = (LinearLayout) findViewById(R.id.conmpayNameLayout);
		mDevide4 = (ImageView) findViewById(R.id.devide4);
//		mDevide5 = (ImageView) findViewById(R.id.devide5);
		mDevide6 =(ImageView) findViewById(R.id.devide6);
		mBillNumberLayout = (RelativeLayout) findViewById(R.id.billNumberLayout);
		mTotalMonyLayout = (RelativeLayout) findViewById(R.id.TotalMonyLayout);
		mReSend = (Button) findViewById(R.id.reSend);
		mReSend.setOnClickListener(this);
		mBillDetailInforLayout = (RelativeLayout) findViewById(R.id.billDetailInforLayout);
		mCompanyNameTextView = (TextView) findViewById(R.id.companyNameTextView);
		mBillNumberTextView = (TextView) findViewById(R.id.billNumberTextView);
		mTotalMonyTextView = (TextView) findViewById(R.id.totalMonyTextView);
		mBillDetailInforList = (ListView) findViewById(R.id.billDetailInforList);
		mOverLayListButton = (ImageView) findViewById(R.id.overLayListButton);
		mOverLayListButton.setOnClickListener(this);
		init();
		mQuickPayBillListAdapter = new QuickPayBillListAdapter(getRootController(), billInfosDatas);
		headView =(RelativeLayout) LayoutInflater.from(getRootController()).inflate(R.layout.quickpay_billlist_headview, null);
		mBillDetailInforList.addHeaderView(headView);
		mBillDetailInforList.setAdapter(mQuickPayBillListAdapter);
		isOpen = false;
		
	}
	
	private void init(){
		if(pushData != null){
			mCompanyName = pushData.optString(Constant.QUICKPAY_PUSH_STORENAME);
			mCompanyNameTextView.setText(mCompanyName);
			mBillNumber = pushData.optString(Constant.QUICKPAY_PUSH_TRADENO);
			mBillNumberTextView.setText(mBillNumber);
			mTotalMoney  = pushData.optString(Constant.QUICKPAY_PUSH_REALAMOUNT);
			mTotalMonyTextView.setText(mTotalMoney + "元");
			pushBillInfos = pushData.optJSONArray(Constant.QUICKPAY_PUSH_GOODSLIST);
			if(pushBillInfos != null && pushBillInfos.length()>0){
				for(int i=0;i<pushBillInfos.length();i++){
					try {
						JSONObject billInfoObj = pushBillInfos.getJSONObject(i);
						Billinfo billInfo = new Billinfo();
						billInfo.count = billInfoObj.optString(Constant.QUICKPAY_PUSH_GOODSCOUNT);
						billInfo.goodsName = billInfoObj.optString(Constant.QUICKPAY_PUSH_GOODSNAME);
						billInfo.price = billInfoObj.optString(Constant.QUICKPAY_PUSH_GOODSPRICE);
						billInfosDatas.add(billInfo);
					} catch (JSONException e) {
						e.printStackTrace();
					}

				}
			}
		}
	}
	@Override
	protected void onPostExecute(String bizType, Object result) {
		if(C2BConfirmViewController.C2CPAY.equals(bizType)){
			getRootController().pop();
		}
		super.onPostExecute(bizType, result);
	}
	@Override
	protected Object doInBackground(String bizType, String... params) {
		if(C2BConfirmViewController.C2CPAY.equals(bizType)){
			closeProgress();
			BaseHelper.payBizDeal(getRootController(), null, mBillNumber, getRootController().getExtToken(), null,
							"trade", "");
		}
		
		return super.doInBackground(bizType, params);
	}
	
	@Override
	public void onClick(View v) {
		int id = v.getId();
		
		switch (id) {
		case R.id.overLayListButton:
			//判断按钮的状态，决定是收起listView还是展开listView
			isOpen = isOpen?false:true;
			openOrCloseList();
			break;
		case R.id.buttonNext:
			new BizAsyncTask(C2BConfirmViewController.C2CPAY).execute();
			

			break;
		case R.id.reSend:
			 getRootController().pop();
		break;
		default:
			break;
		}
	}
	//展开或者折叠list
	private void openOrCloseList(){
		int vivible = isOpen?View.GONE:View.VISIBLE;
		if(isOpen){
		mBillDetailInforLayout.setBackgroundColor(getRootController().getResources().getColor(R.color.TextColorWhite));
		}else{
			mBillDetailInforLayout.setBackgroundColor(getRootController().getResources().getColor(R.color.quickpay_bg_color));	
		}
		mOverLayListButton.setBackgroundResource(isOpen?R.drawable.bill_close:R.drawable.bill_open);
		mAlipaySubTitle.setVisibility(vivible);
		mButtonNext.setVisibility(vivible);
		mConmpayNameLayout.setVisibility(vivible);
		mDevide4.setVisibility(vivible);
//		mDevide5.setVisibility(vivible);
		mDevide6.setVisibility(vivible);
		mBillNumberLayout.setVisibility(vivible);
		mTotalMonyLayout.setVisibility(vivible);
		mReSend.setVisibility(vivible);
	}
	
	public class Billinfo{
		public String count = "";
		public String goodsName = "";
		public String price = "";
	}
	
}
