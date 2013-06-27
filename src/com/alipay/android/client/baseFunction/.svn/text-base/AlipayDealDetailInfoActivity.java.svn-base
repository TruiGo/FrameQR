/**
 * 
 */
package com.alipay.android.client.baseFunction;

import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.ClipboardManager;
import android.text.Html;
import android.text.SpannableStringBuilder;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.alipay.android.client.AlipayAgentPay;
import com.alipay.android.client.AlipayAgentPayDetail;
import com.alipay.android.client.AlipayApplication;
import com.alipay.android.client.AlipayTradeStatus;
import com.alipay.android.client.Login;
import com.alipay.android.client.MessageFilter;
import com.alipay.android.client.RootActivity;
import com.alipay.android.client.constant.AlipayHandlerMessageIDs;
import com.alipay.android.client.constant.Constant;
import com.alipay.android.client.util.AlipayInputErrorCheck;
import com.alipay.android.client.util.BaseHelper;
import com.alipay.android.client.util.Responsor;
import com.alipay.android.common.data.UserData;
import com.alipay.android.comon.component.ProgressDiv;
import com.alipay.android.core.MsgAction;
import com.alipay.android.core.ParamString;
import com.alipay.android.log.Constants;
import com.alipay.android.log.StorageStateInfo;
import com.alipay.android.ui.fragment.ContactOtherPop;
import com.alipay.android.ui.fragment.PopCancelCallback;
import com.eg.android.AlipayGphone.R;

/**
 * @author wb_xianglin.ren
 * 交易详情
 *
 */
public class AlipayDealDetailInfoActivity extends RootActivity implements OnClickListener,PopCancelCallback{
	private TextView mTitleTextView = null;
	private AlipayDetailInfo mDetailInfo = null;
	private AlipayAgentPayDetail mPeerpayInfo = null;
	
	private ProgressDiv mProgress = null;
	private JSONObject myJsonObject = null;
	
	private LinearLayout arryButtonLayout = null;//tab
	private ScrollView mDealInfoContent = null;
	private ScrollView mCarryInfoContent = null;
	private ScrollView mWaitCOnfirmContent = null;
	private ScrollView mPeerPayInfoContent = null;
	private LinearLayout dynamicCarryInfoCanvas = null;
	
	private LinearLayout mHaveCarryInfo = null;
	private LinearLayout mNoCarryInfo = null;
	
	private LinearLayout mDICanvas = null;
	private LinearLayout mTransferFlow = null;
	private LinearLayout mFilterCanvas = null;
	private Button mRemindReceiver = null;
	
	private ViewGroup mViewCanvas = null;
	private Button mDetailButton = null;
	private Button mDistributionButton = null;
	private Button mConfirmGoods = null;
	private Button mRemindSeller = null;
	private Button mDealDetailTab = null;
	private Button mDealProcessTab = null;

	private String mTradeNo = null;
	private String mBizType = null;
	
	private int mResultType = -1;
	private int buyer;
	private String smsSended;
	
	private RelativeLayout originalDetailInfoLyt = null;
	
	private RelativeLayout lifePayDetailInfoLyt = null;
	private LinearLayout lifePayTitleLyt = null;
	private ImageView lifePayTitleImage = null;
	private TextView lifePayTitleKind = null;
	private TextView lifePayTitleDate = null;
	private LinearLayout lifePayCityLyt = null;
	private TextView lifePayCityContent = null;
	private LinearLayout lifePayCompanyLyt = null;
	private TextView lifePayCompanyContent = null;
	private LinearLayout lifePayUserNameLyt = null;
	private TextView lifePayUserName = null;
	private TextView lifePayUserNameContent = null;
	private LinearLayout lifePayTradeNoLyt = null;
	private TextView lifePayTradeNo = null;
	private TextView lifePayTradeNoContent = null;
	private LinearLayout lifePayAmountLyt = null;
	private TextView lifePayAmountContent = null;
	private LinearLayout lifePayFeeLyt = null;
	private TextView lifePayFeeContent = null;
	private LinearLayout lifePayPenaltyLyt = null;
	private TextView lifePayPenaltyContent = null;
	private LinearLayout lifePayTotalAmountLyt = null;
	private TextView lifePayTotalAmountContent = null;
	private LinearLayout lifePayPenaltyHintLyt = null;
	
	private LifePayDetailInfo lifePayDetailsInfo = null;
	
	private int billType = 0;
	private String city;
	private String chargeType;
	private String billKey;
	private String company;
	private String payAmount;
	private String billUserName;
	private String pucDate;
	private String billDate;
	
	private String billIdForLifePay = "";
	private String billNoForLifePay = "";
	
	private static final int DEAL_INFO = 0;
	private static final int CARRY_INFO = 1;
	
	private AlipayTradeStatus mTradeStatus = null;
	
	private static final int MSG_CANCEL_AGENT_ANY_LINK = 1121;
	
	public static final String LOG_TAG = "AlipayDealDetailInfoActivity";
	private MessageFilter mMessageFilter = new MessageFilter(this);
	private boolean mIsPay;
	
	private boolean hasRefresh;
	private Handler mHandler = new Handler(){
		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			Responsor responsor;
			if(msg.what != AlipayHandlerMessageIDs.RQF_PAY 
					&& msg.what != AlipayHandlerMessageIDs.DEAL_QUERY_LIFEPAY_BILL)
			{
        		showResult(msg);
			}
			switch(msg.what){
			case AlipayHandlerMessageIDs.DEAL_QUERY_DETAILINFO:
				getDealDetailInfo();
				break;
			case AlipayHandlerMessageIDs.DEAL_QUERY_LIFEPAY_BILL:
				queryLifePayDetails(msg);
				break;
			case AlipayHandlerMessageIDs.DEAL_QUERY_LIFEPAY_DETAIL:
				getLifePayDetailInfo(msg);
				break;
			case AlipayHandlerMessageIDs.DEAL_QUERY_LIFEPAY_BILLNO:
				queryLifePayBillNo(msg);
				break;
			case AlipayHandlerMessageIDs.DEAL_QUERY_AGENT_INFO:
            	getPeerPayDetail();
            	break;
			case AlipayHandlerMessageIDs.RQF_PAY:
            	payingFinish(msg);
            	break;
			case MSG_CANCEL_AGENT_ANY_LINK:
				responsor = (Responsor) msg.obj;
				if(responsor.status == 100) {
					//提示用户取消成功，之后返回到上一个界面，并且刷新消费记录
					((AlipayApplication) getApplicationContext()).setRecordsRefresh(true);
				    getDataHelper().showDialog(AlipayDealDetailInfoActivity.this,
							R.drawable.infoicon,
							getResources().getString(R.string.WarngingString), responsor.memo,
							getResources().getString(R.string.Ensure), btnForPayingOk,
							null, null, null, null);
				} else {
					//出错，留在当前页面
				}
				break;
				//向卖家发送提示信息
			case AlipayHandlerMessageIDs.SEND_REMIND_MSG:
				responsor = (Responsor) msg.obj;
				if(responsor.status == 100) {
					showToast(getText(R.string.remindSuccess)+"");
				}
				break;
			case AlipayHandlerMessageIDs.SEND_REMIND_RECEIVER_MSG:
				responsor = (Responsor) msg.obj;
				if(responsor.status == 100) {
					showToast(getText(R.string.remindReceiverSuccess)+"");
					mRemindReceiver.setEnabled(false);
				}
				break;
			}
		}
		
	};
	
	DialogInterface.OnClickListener btnForPayingOk = new DialogInterface.OnClickListener(){
		@Override
		public void onClick(DialogInterface dialog, int which) {
			setResult(Activity.RESULT_OK);
			finish();
		}
	};
	
	
	private void showToast(String msg){
		Toast successToast = Toast.makeText(this,msg, 0);
		successToast.setGravity(Gravity.CENTER, 0, 0);
		successToast.show();
	}
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.alipay_deal_detail_info);
		mTradeStatus = new AlipayTradeStatus(this);
		mDetailInfo = new AlipayDetailInfo();
		
		initLayoutElement();
		
		if(getUserData() != null)
			getDataFromIntent();
		else{
			Intent loginIntent = new Intent(this,Login.class);
            startActivityForResult(loginIntent, Constant.REQUEST_LOGIN_BACK);
		}
	}
	
	@Override
	protected void onResume() {
		super.onResume();
		StorageStateInfo.getInstance().putValue(Constants.STORAGE_CURRENTVIEWID, Constants.TRADEDETAILSVIEW);
	}
	
	private void initLayoutElement(){
		Button tButton=null;
		
		mTitleTextView = (TextView)findViewById(R.id.title_text);
		
		arryButtonLayout = (LinearLayout)findViewById(R.id.CarryButtonLayout);
		mDetailButton = (Button)this.findViewById(R.id.DetailButton);
		mDetailButton.setOnClickListener(this);
		mDetailButton.setSelected(true);
		mDistributionButton = (Button)this.findViewById(R.id.DistributionButton);
		mDistributionButton.setOnClickListener(this);
		
		LinearLayout listDivide = (LinearLayout)findViewById(R.id.ListDivide);
		BaseHelper.fixBackgroundRepeat(listDivide);
		
		mPeerPayInfoContent = (ScrollView) LayoutInflater.from(this).inflate(R.layout.alipay_agent_detail_320_480, null);
		mDealInfoContent = (ScrollView) LayoutInflater.from(this).inflate(R.layout.alipay_dealinfo_320_480, null);
        mConfirmGoods = (Button)mDealInfoContent.findViewById(R.id.ConfirmGoods);
        mConfirmGoods.setOnClickListener(new OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                mIsPay = false;
                BaseHelper.payDeal(AlipayDealDetailInfoActivity.this,mHandler,mProgress,mDetailInfo.resultTradeNo, getExtToken(),"","trade", mDetailInfo.tBizType);
            }
        
        });
        
        //提醒卖家发货
        mRemindSeller = (Button) mDealInfoContent.findViewById(R.id.RemindSeller);
        mRemindSeller.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
			    getDataHelper().sendRemindMessage(AlipayDealDetailInfoActivity.this,mHandler,AlipayHandlerMessageIDs.SEND_REMIND_MSG,mDetailInfo.tOutTradeNO,mDetailInfo.resultTradeNo);
				openProcessDialog(getString(R.string.sendingRemindMessage));
			}
		});
        
		mCarryInfoContent = (ScrollView) LayoutInflater.from(this).inflate(R.layout.alipay_carryinfo_320_480, null);
		mHaveCarryInfo = (LinearLayout)mCarryInfoContent.findViewById(R.id.HaveCarryInfoLayout);
		mNoCarryInfo = (LinearLayout)mCarryInfoContent.findViewById(R.id.NoCarryInfoLayout);
    	mWaitCOnfirmContent = (ScrollView) LayoutInflater.from(this).inflate(R.layout.alipay_waitconfirm_320_480, null);
    	tButton = (Button) mWaitCOnfirmContent.findViewById(R.id.WREnsureButton);
    	tButton.setOnClickListener(this);
    	
    	mViewCanvas = (ViewGroup) findViewById(R.id.DealQueryCanvas);
    	
    	originalDetailInfoLyt = (RelativeLayout) mDealInfoContent.findViewById(R.id.DealInfoBlock);
    	mDealDetailTab = (Button) mDealInfoContent.findViewById(R.id.dealDetail_tab);
		mDealDetailTab.setSelected(true);
		mDealDetailTab.setOnClickListener(this);
		mDealProcessTab = (Button) mDealInfoContent.findViewById(R.id.dealProcess_tab);
		mDealProcessTab.setOnClickListener(this);
		mDICanvas = (LinearLayout) mDealInfoContent.findViewById(R.id.DICanvas);
		mTransferFlow = (LinearLayout) mDealInfoContent.findViewById(R.id.transferFlow);
		mFilterCanvas = (LinearLayout) mDealInfoContent.findViewById(R.id.filter);
		mRemindReceiver = (Button) mDealInfoContent.findViewById(R.id.remindReceiver);
		
		lifePayDetailInfoLyt = (RelativeLayout) mDealInfoContent.findViewById(R.id.life_pay_details);
		lifePayDetailInfoLyt.setVisibility(View.GONE);
		lifePayTitleLyt = (LinearLayout) mDealInfoContent.findViewById(R.id.lifepay_title_layout);
		lifePayTitleImage = (ImageView) mDealInfoContent.findViewById(R.id.lifepay_icon);
		lifePayTitleKind = (TextView) mDealInfoContent.findViewById(R.id.lifepay_type);
		lifePayTitleDate = (TextView) mDealInfoContent.findViewById(R.id.lifepay_date);
		lifePayCityLyt = (LinearLayout) mDealInfoContent.findViewById(R.id.lifepay_city_layout);
		lifePayCityContent = (TextView) mDealInfoContent.findViewById(R.id.lifepay_city_content);
		lifePayCompanyLyt = (LinearLayout) mDealInfoContent.findViewById(R.id.lifepay_company_layout);
		lifePayCompanyContent = (TextView) mDealInfoContent.findViewById(R.id.lifepay_company_content);
		lifePayUserNameLyt = (LinearLayout) mDealInfoContent.findViewById(R.id.lifepay_username_layout);
		lifePayUserName = (TextView) mDealInfoContent.findViewById(R.id.lifepay_username);
		lifePayUserNameContent = (TextView) mDealInfoContent.findViewById(R.id.lifepay_username_content);
		lifePayTradeNoLyt = (LinearLayout) mDealInfoContent.findViewById(R.id.lifepay_tradeno_layout);
		lifePayTradeNo = (TextView) mDealInfoContent.findViewById(R.id.lifepay_tradeno);
		lifePayTradeNoContent = (TextView) mDealInfoContent.findViewById(R.id.lifepay_tradeno_content);
		lifePayAmountLyt = (LinearLayout) mDealInfoContent.findViewById(R.id.lifepay_amount_layout);
		lifePayAmountContent = (TextView) mDealInfoContent.findViewById(R.id.lifepay_amount_content);
		lifePayFeeLyt = (LinearLayout) mDealInfoContent.findViewById(R.id.lifepay_fee_layout);
		lifePayFeeContent = (TextView) mDealInfoContent.findViewById(R.id.lifepay_fee_content);
		lifePayPenaltyLyt = (LinearLayout) mDealInfoContent.findViewById(R.id.lifepay_penalty_layout);
		lifePayPenaltyContent = (TextView) mDealInfoContent.findViewById(R.id.lifepay_penalty_content);
		lifePayTotalAmountLyt = (LinearLayout) mDealInfoContent.findViewById(R.id.lifepay_totalamount_layout);
		lifePayTotalAmountContent = (TextView) mDealInfoContent.findViewById(R.id.lifepay_totalamount_content);
		lifePayPenaltyHintLyt = (LinearLayout) mDealInfoContent.findViewById(R.id.lifepay_penaltyhint_layout);
	}
	
	private void getDataFromIntent(){
		Intent intent = getIntent();
		String params = intent.getStringExtra("param");
		if(params !=null && !"".equals(params)){
			ParamString paramString = new ParamString(params);
			mTradeNo = paramString.getValue("no");
			mBizType = paramString.getValue("biz");
//			mResultType = uri.getQueryParameter(Constant.RPF_RESULT_TYPE);
			buyer = intent.getIntExtra(Constant.RPF_BUYER, 100);
		}else{
			billType = intent.getIntExtra(Constant.BILL_TYPE, 0);
			
			mTradeNo = intent.getStringExtra(Constant.RQF_TRADE_NO);
			mBizType = intent.getStringExtra(Constant.RPF_BIZTYPE);
			
			mResultType = intent.getIntExtra(Constant.RPF_RESULT_TYPE, 0);
			buyer = intent.getIntExtra(Constant.RPF_BUYER, 100);
			
			city = intent.getStringExtra(Constant.CITY);
			chargeType = intent.getStringExtra(Constant.SUBBIZTYPE);
			billKey = intent.getStringExtra(Constant.BILLKEY);
			company = intent.getStringExtra(Constant.RPF_CHARGEINSTNAME);
			payAmount = intent.getStringExtra(Constant.RPF_BILLAMOUNT);
			billUserName = intent.getStringExtra(Constant.RPF_BILLUSERNAME);
			pucDate = intent.getStringExtra(Constant.ORIGINALBILLDATE);
			billDate = intent.getStringExtra(Constant.RPF_BILLDATE);
		}
		
//		mTitleTextView.setText(statusMemo);
		boolean isPeerpay = false;
		if (mBizType != null && mBizType.equals("PEERPAY"))
		{
			mTitleTextView.setText(R.string.AgentPayDetailTitle);
			isPeerpay = true;
		} 
		else if(mBizType != null && mBizType.equals("JF"))
		{
			mTitleTextView.setText(R.string.LifePayDetailTitle);
			isPeerpay = false;
		}
		else
		{
			mTitleTextView.setText(R.string.ConsumptionRecordDetailTitle);
			isPeerpay = false;
		}
		
		sendGetDealDetailCommand(mTradeNo, mBizType, isPeerpay);
	}
	
	private void sendGetDealDetailCommand(String tradeNo, String bizType, boolean isPeerpay){
		if(null == tradeNo){
			return;
		}
		
		int cmd = -1;
		if (isPeerpay) {
			cmd = AlipayHandlerMessageIDs.DEAL_QUERY_AGENT_INFO;
			getDataHelper().sendQueryAgentDetail(mHandler, cmd, tradeNo, bizType);
		} 
		else if(billType == 1)
		{
			cmd = AlipayHandlerMessageIDs.DEAL_QUERY_LIFEPAY_BILL;
			
			JSONObject node = new JSONObject();
			try {
				node.put("billKey", billKey == null ? "" : billKey);
				node.put("city", city == null ? "" : city);
				node.put("company", company == null ? "" : company);
				node.put("chargeInputType", 0);
				node.put("payAmount", payAmount == null ? "" : payAmount);
				node.put("billUserName", billUserName == null ? "" : billUserName);
			} 
			catch (JSONException e) 
			{
				e.printStackTrace();
			}
			String inputInfo = node.toString();
			
			if(chargeType != null)
			{
				chargeType = chargeType.toLowerCase();
			}
			getDataHelper().sendQueryLifePayBill(mHandler, cmd, city, chargeType, inputInfo, pucDate, payAmount, billUserName);
		}
		else {
			cmd = AlipayHandlerMessageIDs.DEAL_QUERY_DETAILINFO;
			getDataHelper().sendQueryTradeDetail(mHandler, cmd, tradeNo, bizType);
		}	

		String msg = getResources().getString(R.string.DealQueryGetingDetailInfo);
		openProcessDialog(msg);
	}

	private void queryLifePayBillNo(Message msg)
	{
		//parse json.
		int status = -1;
		if(msg != null && msg.obj != null)
		{
			Responsor responsor = (Responsor)msg.obj;
			JSONObject obj = responsor.obj;
			
			status = responsor.status;
			if(obj != null)
			{
				billNoForLifePay = obj.optString("billNo", "");
			}
		}
		
		//调用快捷支付接口
		if(status == 100)
		{
			mIsPay = true;
			mProgress = BaseHelper.payDeal(AlipayDealDetailInfoActivity.this, mHandler, mProgress, 
					billNoForLifePay, getExtToken(), "", "trade", "puc_charge");
		}
	}
	
	private void queryLifePayDetails(Message msg)
	{
		Responsor responsor = (Responsor)msg.obj;
		JSONObject obj = responsor.obj;
		
		boolean tResultOK = false;
		tResultOK = mMessageFilter.process(msg);
		
		if((tResultOK) && (!getDataHelper().isCanceled()))
		{
			if(obj != null)
			{
				billIdForLifePay = obj.optString("billId", "");
			}
			
			//Support safe pay by default.
			String isSupportSecurityPay = "True";//isSupportSecurePay() ? "True" : "False";
			
			int cmd = AlipayHandlerMessageIDs.DEAL_QUERY_LIFEPAY_DETAIL;
			
			getDataHelper().sendQueryLifePayDetail(mHandler, cmd, billIdForLifePay, isSupportSecurityPay);
		}
		else
		{
			if (mProgress != null)
			{
				mProgress.dismiss();
				mProgress = null;
			}
		}
	}
	
	private boolean isSupportSecurePay()
	{
		PackageManager manager = getApplication().getPackageManager();
		List<PackageInfo> pkgList = manager.getInstalledPackages(0);
		
		for (int i = 0; i < pkgList.size(); i++)
		{
			PackageInfo pI = pkgList.get(i);
			if (pI.packageName.equalsIgnoreCase("com.alipay.android.app"))
			{
				return true;
			}
		}
		
		return false;
	}
	
	private void openProcessDialog(String msg){
		if (mProgress == null){
			mProgress = getDataHelper().showProgressDialogWithCancelButton(this, null,
					msg, false, true, getDataHelper().cancelListener,
					getDataHelper().cancelBtnListener);
		}
	}
	
	private void showResult(Message msg){
		
		Responsor responsor = (Responsor)msg.obj;
		JSONObject obj = responsor.obj;
		boolean tResultOK = false;
		boolean needDismissProcessDialog = true;
		
		tResultOK = mMessageFilter.process(msg);
		
		if((tResultOK) && (!getDataHelper().isCanceled()))
		{
			myJsonObject = obj;
		}
		
		if(needDismissProcessDialog){
			if (mProgress != null){
				mProgress.dismiss();
				mProgress = null;
			}
		}
	}
	
	private void getDealDetailInfo(){
		if(myJsonObject != null && myJsonObject.length()>0)
		{
			if(null == mDetailInfo){
				mDetailInfo = new AlipayDetailInfo();
			}
			mDetailInfo.setDetailInfo(this,myJsonObject);

			mResultType = mTradeStatus.getMapStatus(mDetailInfo.resultStatus);
			mDetailInfo.setResultType(mResultType);

			showDetailInfo(DEAL_INFO);
		}
	}
	
	private void getLifePayDetailInfo(Message msg)
	{
		boolean tResultOK = false;
		tResultOK = mMessageFilter.process(msg);
		
		if((tResultOK) && (!getDataHelper().isCanceled()))
		{
			if(myJsonObject != null && myJsonObject.length()>0)
			{
				if (null == lifePayDetailsInfo)
				{
					lifePayDetailsInfo = new LifePayDetailInfo();
				}

				lifePayDetailsInfo.setDetailInfo(myJsonObject);

				// mResultType =
				// mTradeStatus.getMapStatus(mDetailInfo.resultStatus);
				// mDetailInfo.setResultType(mResultType);
				showLifePayDetailInfo();
			}
		}
		else
		{
			if (mProgress != null)
			{
				mProgress.dismiss();
				mProgress = null;
			}
		}
	}
	
	private void getPeerPayDetail(){
		if(myJsonObject != null && myJsonObject.length()>0)
		{
			if(null == mPeerpayInfo){
				mPeerpayInfo = new AlipayAgentPayDetail();
			}
			mPeerpayInfo.setDetailInfo(this,myJsonObject);
			
			int resultType = mTradeStatus.getMapStatus(mPeerpayInfo.tPeerPayStatus);
			mPeerpayInfo.setResultType(resultType);
			
			displayPeerpayDetailInfo(mPeerpayInfo);
		}
	}
	
	private void displayPeerpayDetailInfo(AlipayAgentPayDetail info){
		ScrollView mInfo = null;
		
		// 代付详情信息
		mDetailButton.setVisibility(View.GONE);
		mDistributionButton.setVisibility(View.GONE);
//		LinearLayout tButton = (LinearLayout) this.findViewById(R.id.CarryButtonLayout);
//		if (tButton != null) {
//		    tButton.setVisibility(View.GONE);
//		}
		
		//填写代付详情页的大致信息
		TextView tText = null;
    	String tString = null;
    	LinearLayout tInfoCanva = null;
    	
    	Button btnPeerpay = (Button) mPeerPayInfoContent.findViewById(R.id.agentPayButton);
    	btnPeerpay.setOnClickListener(this);
    	Button btnCancelpay = (Button) mPeerPayInfoContent.findViewById(R.id.cancelbutton);
    	btnCancelpay.setOnClickListener(this);
    	
    	//代付提示信息
    	tText = (TextView) mPeerPayInfoContent.findViewById(R.id.agent_detail_prompt);
    	tString = getResources().getString(R.string.AgentPayDetailPrompt);
    	if (info.resultType != AlipayTradeStatus.PEERPAY_INIT){
    		tInfoCanva = (LinearLayout) mPeerPayInfoContent.findViewById(R.id.agent_detail_prompt_layout);
    		tInfoCanva.setVisibility(View.GONE);
    		
    		btnPeerpay.setVisibility(View.GONE);
    		btnCancelpay.setVisibility(View.GONE);
    	}else{
    		tText.setText(tString);
    		btnPeerpay.setText(R.string.AgentPayActionPaying);
    		btnCancelpay.setText(R.string.AgentPayActionRefuse);
    	}
    	
    	//代付申请人名称
    	tText = (TextView) mPeerPayInfoContent.findViewById(R.id.agentApplicantName);
    	tString = info.tBuyerName;
    	if (tString.equals("")){
    		tInfoCanva = (LinearLayout) mPeerPayInfoContent.findViewById(R.id.agent_detail_applicant_name);
    		tInfoCanva.setVisibility(View.GONE);
    	}else{
    		tText.setText(tString);
//    		tText.setTextColor(Color.BLUE);
    		tText.setTextColor(this.getResources().getColor(R.color.text_orange));
    		
    		tInfoCanva = (LinearLayout) mPeerPayInfoContent.findViewById(R.id.agent_detail_applicant_name);
    	}
    	
    	//代付申请人账号
    	tText = (TextView) mPeerPayInfoContent.findViewById(R.id.agentApplicantAccount);
    	tString = info.tBuyerAccount;
    	if (tString.equals("")){
    		;
    	}else{
    		tString = "(" + tString + ")";
    		tText.setText(tString);
    	}
    	
    	//代付金额 
    	tText = (TextView) mPeerPayInfoContent.findViewById(R.id.agentPayMoney);
    	tString = info.tPayMoney;
    	if (tString.equals("")){
    		tInfoCanva = (LinearLayout) mPeerPayInfoContent.findViewById(R.id.agent_detail_money);
    		tInfoCanva.setVisibility(View.GONE);
    	}else{
    		tText.setText(tString);
//    		tText.setTextColor(Color.BLUE);
    		tText.setTextColor(this.getResources().getColor(R.color.text_orange));
    	}
    	
    	//代付留言
    	tText = (TextView) mPeerPayInfoContent.findViewById(R.id.agentPayMemo);
    	if (info.resultType == AlipayTradeStatus.PEERPAY_INIT){
    		tString = info.tBuyerApplyMemo;
    	} else {
    		TextView txtTitle = (TextView) mPeerPayInfoContent.findViewById(R.id.agentPayMemoTitle);
    		txtTitle.setText(R.string.AgentPayStatus);
    		
    		tString = info.teerPayStatusMemo;
//    		tText.setTextColor(Color.GREEN);
    		tText.setTextColor(this.getResources().getColor(R.color.text_light_gray));
    	}
    	if (tString.equals("")){
    		tInfoCanva = (LinearLayout) mPeerPayInfoContent.findViewById(R.id.agent_detail_memo);
    		tInfoCanva.setVisibility(View.GONE);
    	}else{
    		tText.setText(tString);
    	}
    	
    	//商品名称
    	tText = (TextView) mPeerPayInfoContent.findViewById(R.id.agentGoodsName);
    	tString = info.tGoodsName;
    	if (tString.equals("")){
    		tInfoCanva = (LinearLayout) mPeerPayInfoContent.findViewById(R.id.agent_detail_goods_name);
    		tInfoCanva.setVisibility(View.GONE);
    	}else{
    		tText.setText(tString);
    	}
    	
    	//收款方 
    	tText = (TextView) mPeerPayInfoContent.findViewById(R.id.agentSellerName);
    	tString = info.tSellerName;
    	if (tString.equals("")){
    		tInfoCanva = (LinearLayout) mPeerPayInfoContent.findViewById(R.id.agent_detail_seller_name);
    		tInfoCanva.setVisibility(View.GONE);
    	}else{
    		tString = info.tSellerName + "(" + info.tSellerAccount + ")";
    		tText.setText(tString);
    	}
    	
    	//交易号 
    	tText = (TextView) mPeerPayInfoContent.findViewById(R.id.agentTradeNo);
    	tString = info.tTradeNo;
    	if (tString.equals("")){
    		tInfoCanva = (LinearLayout) mPeerPayInfoContent.findViewById(R.id.agent_detail_trade_number);
    		tInfoCanva.setVisibility(View.GONE);
    	}else{
    		tText.setText(tString);
    	}
    	
    	//创建时间 
    	tText = (TextView) mPeerPayInfoContent.findViewById(R.id.agentPayCreatedTime);
    	tString = info.tTradeTime;
    	if (tString.equals("")){
    		tInfoCanva = (LinearLayout) mPeerPayInfoContent.findViewById(R.id.agent_detail_created_time);
    		tInfoCanva.setVisibility(View.GONE);
    	}else{
    		tText.setText(tString);
    	}
		
		mInfo = mPeerPayInfoContent;

		mViewCanvas.setVisibility(View.VISIBLE);
		mViewCanvas.removeAllViews();
		if(mInfo!=null)
		{
			mViewCanvas.addView(mInfo);
		}
	}
	
	 private void displayDealInfoContent(AlipayDetailInfo info){
	    	TextView tText=null;
	    	String tString;
	    	RelativeLayout tInfoCanva=null;
	    	LinearLayout transferStatusCanvas ;
	    	tString = info.tTranferStatus;
	    	
	    	transferStatusCanvas = (LinearLayout) mDealInfoContent.findViewById(R.id.transferFlow);
	    	if("".equals(tString) || "[]".equals(tString)){
	    		transferStatusCanvas.setVisibility(View.GONE);
	    		mFilterCanvas.setVisibility(View.GONE);
	    	}else{
	    		mFilterCanvas.setVisibility(View.VISIBLE);
	    		displayTransferFlow(transferStatusCanvas,tString);
	    	}
	    	
	    	// 
	    	tText = (TextView) mDealInfoContent.findViewById(R.id.WareName);
	    	tString = info.tGoodsName;
	    	if (tString.equals("")){
	    		tInfoCanva = (RelativeLayout) mDealInfoContent.findViewById(R.id.DealInfo1);
	    		tInfoCanva.setVisibility(View.GONE);
	    	}else{
	    		tText.setText(Html.fromHtml(tString).toString());
	    	}
	    	
	    	// 
	    	tText = (TextView) mDealInfoContent.findViewById(R.id.DealMoney);
	    	int tradeStatus = mTradeStatus.getMapStatus(info.resultStatus);
	    	if (tradeStatus == AlipayTradeStatus.WAIT_BUYER_PAY) {
	    		tString = info.tPayMoney;			//等待付款时返回
	    	} else {
	    		tString = info.resultTradeMoney;	//非等待付款时返回 
	    	}
	    	if (tString.equals("")){
	    		tInfoCanva = (RelativeLayout) mDealInfoContent.findViewById(R.id.DealInfo2);
	    		tInfoCanva.setVisibility(View.GONE);
	    	}else{
	    		
//	    	    if (info.resultAct!=null&&info.resultAct.indexOf("买")!=-1) {
//	    	        tString = "-"+tString;
//	                tText.setTextColor(getResources().getColor(R.color.text_orange));
//	            }
//	            if (info.resultAct!=null&&info.resultAct.indexOf("卖")!=-1) {
//	                tString = "+"+tString;
//	                tText.setTextColor(getResources().getColor(R.color.TextColorGreen));
//	            }
	    		//金额统一为橘色，去掉前面+ - 号
	            tText.setTextColor(getResources().getColor(R.color.text_orange));
	    		if(tString.indexOf(getString(R.string.Yuan))!= -1)
	    		{
	    			tText.setText(tString);
	    		}
	    		else
	    		{
	    			tText.setText(tString+getString(R.string.UseCoponYuan));
	    		}
	    	}
	    	
	    	//��ֱ� 	
	    	tText = (TextView) mDealInfoContent.findViewById(R.id.DealPoint);
	    	tString = info.tPoint;
	    	if(tString.length()>0){
	    		tText.setVisibility(View.VISIBLE);	
	    		String usePoint = String.format(getString(R.string.usePoint), tString);
		    	tText.setText(usePoint);
		    	
	    	}else{
	    		tText.setVisibility(View.GONE);
	    	}
	    	
	    	// 
	    	tText = (TextView) mDealInfoContent.findViewById(R.id.DealAction);
	    	tString = info.tAct;
	    	if (tString.equals("")){
	    		tInfoCanva = (RelativeLayout) mDealInfoContent.findViewById(R.id.DealInfo3);
	    		tInfoCanva.setVisibility(View.GONE);
	    	}else{
	    		tText.setText(tString);
	    	}
	    	
	    	//交易状态 
	    	tText = (TextView) mDealInfoContent.findViewById(R.id.DealStatus);
	    	info.setStatusColor(tText);

	    	//代付人 
	    	tInfoCanva = (RelativeLayout) mDealInfoContent.findViewById(R.id.DealAgentInfoLayout);
	    	tText = (TextView) mDealInfoContent.findViewById(R.id.DealAgentInfo);
	    	tString = info.tPeerPayStatus;
	    	System.out.println("tPeerPayStatus="+tString);
	    	int peerpayType = -1;
	    	if(tString != null && !tString.equals("")) {
	    		peerpayType = this.mTradeStatus.getMapStatus(info.tPeerPayStatus);
	    	}
	    	System.out.println("peerpayType="+peerpayType);
	    	if ((peerpayType == AlipayTradeStatus.PEERPAY_INIT || peerpayType == AlipayTradeStatus.PEERPAY_SUCCESS)
	    			&& mDetailInfo.resultIsBuyer != 1){
	    		tInfoCanva.setVisibility(View.VISIBLE);
	    		System.out.println("tPeerPayName="+info.tPeerPayName);
    			tString = info.tPeerPayName;
    			if (tString != null && !tString.equals("")) {
    				//指定人代付方式
	    			tString = tString + "(" + info.tPeerPayAccount +")";
	    			if (tString != null) {
		    			SpannableStringBuilder strStyle=BaseHelper.changeStringStyle(tString, "(", ")", Color.GRAY, 22);
		    			if (strStyle != null) {
		    				tText.setText(strStyle);
		    			} else {
		    				tText.setText(tString);
		    			}
	    			}
    			} else {
	    			//共享代付链接方式
	    			tText.setText(Html.fromHtml("<u>"
	    					+ getResources().getString(R.string.AgentPayActionCopyLink) + "</u>"));
	    			tText.setTextColor(getResources().getColorStateList(R.drawable.alipay_link_text_colors));
	    			tText.setOnClickListener(new OnClickListener() {
	    				@Override public void onClick(View arg0)
	    				{
	    					//拷贝动作：copy数据到内存
	    					ClipboardManager clip = (ClipboardManager)getSystemService(Context.CLIPBOARD_SERVICE);		
	    					clip.setText(mDetailInfo.tPeerPayAccount);
	    					Toast.makeText(AlipayDealDetailInfoActivity.this, getResources().getText(R.string.AgentPayCopyLinkHint).toString(), 
	    							Toast.LENGTH_SHORT).show();
	    				}
	    			});
	    			
	    			TextView txtTitle = (TextView) mDealInfoContent.findViewById(R.id.DealAgentTitle);
	    			txtTitle.setVisibility(View.INVISIBLE);
	    		}
    		} else {
	    		tInfoCanva.setVisibility(View.GONE);
    		}
	    	
	    	// 
	    	tText = (TextView) mDealInfoContent.findViewById(R.id.DealFrom);
	    	tInfoCanva = (RelativeLayout) mDealInfoContent.findViewById(R.id.DealInfo4);
	    	tString = info.tPartnerName;
	    	if (tString.equals("")){
	    		tInfoCanva.setVisibility(View.GONE);
	    	}else{
	    		if (tString != null) {
	    			tText.setText(tString);
    			}
	    	}
	    	tInfoCanva.setVisibility(View.GONE);
	    	
	    	// 
	    	tText = (TextView) mDealInfoContent.findViewById(R.id.DealObject);
	    	tString = info.tSellerName;
	    	if (tString.equals("")){
	    		tInfoCanva = (RelativeLayout) mDealInfoContent.findViewById(R.id.DealInfo5);
	    		tInfoCanva.setVisibility(View.GONE);
	    	}else{
	    		if (tString != null) {
	    			SpannableStringBuilder strStyle=BaseHelper.changeStringStyle(tString, "(", ")", Color.GRAY, 22);
	    			if (strStyle != null) {
	    				tText.setText(strStyle);
	    			} else {
	    				tText.setText(tString);
	    			}
    			}
	    	}
	    	
	    	// 交易类型
	    	tText = (TextView) mDealInfoContent.findViewById(R.id.DealType);
	    	tString = info.tTradeType;
	    	if (tString.equals("")){
	    		tInfoCanva = (RelativeLayout) mDealInfoContent.findViewById(R.id.DealInfo6);
	    		tInfoCanva.setVisibility(View.GONE);
	    	}else{
	    		tText.setText(tString);
	    	}
	    	
	    	
	    	// 订单号
	    	tText = (TextView) mDealInfoContent.findViewById(R.id.orderNumber);
	    	if("TO_CARD_MOBILE".equals(info.tBizType)){
	    		tString = info.tTradeNumber;
	    	}else{
	    		tString = "";
	    	}
	    	if (tString.equals("")){
	    		tInfoCanva = (RelativeLayout) mDealInfoContent.findViewById(R.id.DealInfo9);
	    		tInfoCanva.setVisibility(View.GONE);
	    	}else{
	    		tText.setText(tString);
	    	}
	    	
	    	// 交易号
	    	tText = (TextView) mDealInfoContent.findViewById(R.id.DealNumber);
	    	if("TO_CARD_MOBILE".equals(info.tBizType)){//转账类型
	    		tString = info.tOutTradeNO;
	    	}else{
	    		tString = info.tTradeNumber;
	    	}
	    	if (tString.equals("")){
	    		tInfoCanva = (RelativeLayout) mDealInfoContent.findViewById(R.id.DealInfo7);
	    		tInfoCanva.setVisibility(View.GONE);
	    	}else{
	    		tText.setText(tString);
	    	}
	    	
	    	// 交易时间
	    	tText = (TextView) mDealInfoContent.findViewById(R.id.DealTime);
	    	tString = info.tTradeTime;
	    	if (tString.equals("")){
	    		tInfoCanva = (RelativeLayout) mDealInfoContent.findViewById(R.id.DealInfo8);
	    		tInfoCanva.setVisibility(View.GONE);
	    	}else{
	    		tText.setText(tString);
	    	}
	    	
	    	String tBtnStr1 = "";
	    	String tBtnStr2 = "";
	    	if(info.resultIsBuyer == 0 &&
	    			info.resultType == AlipayTradeStatus.WAIT_BUYER_PAY) {
	    		if (peerpayType == AlipayTradeStatus.PEERPAY_INIT) {
	    			//已申请代付，但是尚未付款
	    			tBtnStr1 = getResources().getString(R.string.DealQueryEnsureSelfPaying);
					tBtnStr2 = getResources().getString(R.string.AgentPayActionCancel);
	    		} else {
	    			//等待付款，且当前没有申请代付
	    			tBtnStr1 = getResources().getString(R.string.DealQueryEnsurePaying);
					tBtnStr2 = getResources().getString(R.string.AgentPayButtonPrompt);
	    		}
			}
	    	
	    	Button btnPay = null;
	    	btnPay = (Button) mDealInfoContent.findViewById(R.id.ensurebutton);
	    	if (btnPay != null) {
		    	if (tBtnStr1.equals("")){
		    		btnPay.setVisibility(View.GONE);
		    	}else{
		    		btnPay.setText(tBtnStr1);
		    	}
		    	btnPay.setOnClickListener(this);
	    	}
	    	
	    	Button btnAgent = null;
	    	btnAgent = (Button) mDealInfoContent.findViewById(R.id.makeAgentButton);
	    	if (btnAgent != null) {
		    	if (tBtnStr2.equals("") || (info.tBizType != null && (info.tBizType.equals("PUC_CHARGE")
		    					 ||info.tBizType.equals("CCR")||info.tBizType.equals("EVOUCHER")))
		    					 ||"TO_CARD_MOBILE".equals(info.tBizType)){
		    		btnAgent.setVisibility(View.GONE);
		    	}else{
		    		btnAgent.setText(tBtnStr2);
		    	}
		    	btnAgent.setOnClickListener(this);
	    	}
	    	
	    	if(info.tBizType != null && "TO_CARD_MOBILE".equals(info.tBizType) && info.resultStatus != null && "PAID".equals(info.resultStatus)){
				if("1".equals(info.tSmsPush)){
					mRemindReceiver.setVisibility(View.VISIBLE);
					mRemindReceiver.setEnabled(true);
				}else{
					mRemindReceiver.setVisibility(View.GONE);
				}
			}else{
				mRemindReceiver.setVisibility(View.GONE);
			}
	    	
	    	mRemindReceiver.setOnClickListener(this);
	    }
	 
	 private void displayCarryInfoContent(AlipayDetailInfo info){
		 TextView tText=null;
		 String tString;
		 RelativeLayout tInfoCanva=null;
		 tText = (TextView) mCarryInfoContent.findViewById(R.id.ReceiveAddress);
		 tString = info.tBuyerAddress;
		 if (tString.equals("")){
			 tInfoCanva = (RelativeLayout) mCarryInfoContent.findViewById(R.id.CarryInfo1);
			 tInfoCanva.setVisibility(View.GONE);
		 }else{
			 tText.setText(tString);
		 }

		 tText = (TextView) mCarryInfoContent.findViewById(R.id.CarryCo);
		 tString = info.tLogisticsName;
		 if (tString.equals("")){
			 tInfoCanva = (RelativeLayout) mCarryInfoContent.findViewById(R.id.CarryInfo2);
			 tInfoCanva.setVisibility(View.GONE);
		 }else{
			 tText.setText(tString);
		 }
		 
		 tText = (TextView) mCarryInfoContent.findViewById(R.id.CarryNO);
		 tString = info.tLogisticsNo;
		 if (tString.equals("")){
			 tInfoCanva = (RelativeLayout) mCarryInfoContent.findViewById(R.id.CarryInfo3);
			 tInfoCanva.setVisibility(View.GONE);
		 }else{
			 tText.setText(tString);
		 }
		 
		 dynamicCarryInfoCanvas = (LinearLayout) mCarryInfoContent.findViewById(R.id.dynamicCarryInfoCanvas);
		 ImageView carryInfoDevider = (ImageView)mCarryInfoContent.findViewById(R.id.dynamicCarryInfoDevider);
		 //无物流信息
		 if(info.tLogisticsStatus == null || (info.tLogisticsStatus.length() == 1 && hasNoCarryInfo(info.tLogisticsStatus.optJSONObject(0)))){
			 dynamicCarryInfoCanvas.setVisibility(View.GONE);
			 carryInfoDevider.setVisibility(View.GONE);
			 return;
		 }
		 
		 try {
			 JSONArray tLogisticsStatus = info.tLogisticsStatus;
			 dynamicCarryInfoCanvas.setVisibility(View.VISIBLE);
			 carryInfoDevider.setVisibility(View.VISIBLE);
			 dynamicCarryInfoCanvas.removeAllViews();
			 
			 JSONObject curJson;
			 for(int index = 0; tLogisticsStatus != null && index < tLogisticsStatus.length(); index ++){
				 curJson = tLogisticsStatus.getJSONObject(tLogisticsStatus.length() - 1 - index);
				 if(!"".equals(curJson.optString(Constant.RPF_LOGISTICSDATETIME)) && !"".equals(curJson.optString(Constant.RPF_LOGISTICSDESC))){
					 View curView = LayoutInflater.from(this).inflate(R.layout.dynamic_carryinfo_item, null);
					 setDataForView(curView, curJson , index , tLogisticsStatus.length());
					 dynamicCarryInfoCanvas.addView(curView);
				 }
			 }
		 } catch (JSONException e) {
			 e.printStackTrace();
		 }
	 }
	 
	private boolean hasNoCarryInfo(JSONObject carryInfo){
		//过滤返回无运单信息的数据
		if(carryInfo.optString(Constant.RPF_LOGISTICSDESC).contains(getText(R.string.hasNoMatchOrder).toString())){
			return true;
		}
		return false;
	}
	
	private void displayTransferFlow(LinearLayout tInfoCanva, String transferStr){
		try{
			View transferItemView;
			JSONArray transferStatusJson = new JSONArray(transferStr);
			//转账状态经过的节点数
			int tradeStatusLength = transferStatusJson.length();
			for (int i = 0; tradeStatusLength > 0 && i < 4; i++) {
				transferItemView = LayoutInflater.from(this).inflate(R.layout.transfer_state_item, null);
				TransferItem transferItem = new TransferItem();
				transferItem.dateNode = (TextView) transferItemView.findViewById(R.id.status_date);
				transferItem.timeNode = (TextView) transferItemView.findViewById(R.id.status_time);
				transferItem.imageNode =  (ImageView) transferItemView.findViewById(R.id.nodeImage);
				transferItem.descNode = (TextView) transferItemView.findViewById(R.id.status_desc);
				
				if(i < tradeStatusLength){
					setNode(transferItem,transferStatusJson.optJSONObject(i),i);
				}else{
					initNormalNode(transferItem,i,tradeStatusLength,isCurrentNodeSuc(transferStatusJson.optJSONObject(tradeStatusLength -1).optString("statusCode")));
				}
				
				tInfoCanva.addView(transferItemView);
			}
		}catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	private boolean isCurrentNodeSuc(String statusCode){
		return Constant.STATUS_INIT.equals(statusCode) || Constant.STATUS_CONFIRM.equals(statusCode) || Constant.STATUS_PAID.equals(statusCode) 
				|| Constant.STATUS_SUCCESS.equals(statusCode) || Constant.STATUS_WS.equals(statusCode);
	}
	
	
	/**
	 * 设置节点值
	 */
	private void setNode(TransferItem transferItem, JSONObject curNodeJson, int curPosition) {
		String dateTime = curNodeJson.optString("time");
		if(dateTime.length() >= 11){
			transferItem.dateNode.setText(dateTime.substring(0,10).replace("/", "-"));
			transferItem.timeNode.setText(dateTime.substring(11).replace("/", "-"));
		}
		
		String statusCode = curNodeJson.optString("statusCode");
		String mapedMemo = TradeStatuMemoMaping.getMapedTip(this,statusCode);
		if("".equals(mapedMemo)){
			mapedMemo = curNodeJson.optString("statusDesc");
		}
		transferItem.descNode.setText(mapedMemo);
		int drawableId = R.drawable.current_point_success;
		
		if(isCurrentNodeSuc(statusCode)){
			switch (curPosition) {
			case 0:
				drawableId = R.drawable.first_point_success;
				break;
			case 3:
				drawableId = R.drawable.last_point_success;
				break;
			default:
				drawableId = R.drawable.current_point_success;
				break;
			}
		}else{
			switch (curPosition) {
			case 0:
				drawableId = R.drawable.first_point_fail;
				break;
			case 3:
				drawableId = R.drawable.last_point_fail;
				break;
			default:
				drawableId = R.drawable.current_point_fail;
				break;
			}
		} 
		
		Drawable drawable = getResources().getDrawable(drawableId);
		transferItem.imageNode.setBackgroundDrawable(drawable);
	}

	/**
	 * 初始化普通的结点
	 */
	private void initNormalNode(TransferItem transferItem, int curPosition, int tradeStatusLength, boolean previousSuc) {
		transferItem.dateNode.setText("0000-00-00");
		transferItem.dateNode.setVisibility(View.INVISIBLE);
		transferItem.timeNode.setText("00:00:00");
		transferItem.timeNode.setVisibility(View.INVISIBLE);
		String curNodeDesc = "";
		int drawableId = R.drawable.point_normal;
		
		if(curPosition == tradeStatusLength){
			if(previousSuc)
				drawableId = R.drawable.first_point_normal;
			else
				drawableId = R.drawable.point_normal;
			
			switch (curPosition) {
			case 1:
				curNodeDesc = getText(R.string.wait_to_receive) +"";
				break;
			case 2:
				curNodeDesc = getText(R.string.commit_bank_process) +"";
				break;
			case 3:
				if(previousSuc)
					drawableId = R.drawable.last_point_normal_suc;
				else
					drawableId = R.drawable.last_point_normal;
				
				curNodeDesc = getText(R.string.transfer_success) +"";
				break;
			}
		}else{
			switch (curPosition) {
			case 1:
				drawableId = R.drawable.point_normal;
				curNodeDesc = getText(R.string.wait_to_receive) +"";
				break;
			case 2:
				drawableId = R.drawable.point_normal;
				curNodeDesc = getText(R.string.commit_bank_process) +"";
				break;
			case 3:
				drawableId = R.drawable.last_point_normal;
				curNodeDesc = getText(R.string.transfer_success) +"";
				break;
			}
		}
		
		Drawable drawable = getResources().getDrawable(drawableId);
		transferItem.imageNode.setBackgroundDrawable(drawable);
		
		transferItem.descNode.setText(curNodeDesc);
	}
	
	private void setDataForView(View curView, JSONObject curData,int curIndex,int tLogisticsStatusLength) {
		TextView status_date = (TextView)curView.findViewById(R.id.status_date);
		TextView status_time = (TextView)curView.findViewById(R.id.status_time);
		TextView status_desc = (TextView)curView.findViewById(R.id.status_desc);
		ImageView status_node = (ImageView) curView.findViewById(R.id.nodeImage);
		
		if(tLogisticsStatusLength == 1){
			setLastItemColor(status_date, status_time, status_desc);
			status_node.setBackgroundDrawable(getResources().getDrawable(R.drawable.node_one));
		}else if(curIndex == 0){
			setLastItemColor(status_date, status_time, status_desc);
			status_node.setBackgroundDrawable(getResources().getDrawable(R.drawable.node_top));
		}else if(curIndex == tLogisticsStatusLength -1){
			status_node.setBackgroundDrawable(getResources().getDrawable(R.drawable.node_bottom));
		}else{
			status_node.setBackgroundDrawable(getResources().getDrawable(R.drawable.node_middle));
		}
		
		String dateTime = curData.optString(Constant.RPF_LOGISTICSDATETIME);
		if(dateTime.length() >= 11){
			status_date.setText(dateTime.substring(0,10).replace("-", "/"));
			status_time.setText(dateTime.substring(11).replace("-", "/"));
		}
		status_desc.setText(curData.optString(Constant.RPF_LOGISTICSDESC));
	}

	private void setLastItemColor(TextView status_date, TextView status_time, TextView status_desc) {
		//orange color for the last item
		status_date.setTextColor(getResources().getColor(R.color.TextColorYellow));
		status_time.setTextColor(getResources().getColor(R.color.TextColorYellow));
		status_desc.setTextColor(getResources().getColor(R.color.TextColorYellow));
	}
	
	private static final int WAIT_BUYER_CONFIRM_GOODS=4; 
	private static final int REMIND_SELLER_DISPATCH_GOOD=3; 
	
	private void showDetailInfo(int showType){
		ScrollView mInfo = null;
		
		if(mDetailInfo == null)
		{
			return;
		}
		{
			if(DEAL_INFO == showType){
				displayDealInfoContent(mDetailInfo);
				mInfo = mDealInfoContent;

			}else if(CARRY_INFO == showType){
				if(mDetailInfo.tBuyerAddress!= null && mDetailInfo.tBuyerAddress.equals("")
				        &&mDetailInfo.tLogisticsType !=null && mDetailInfo.tLogisticsType.equals("")
						&&mDetailInfo.tLogisticsName.equals("") && mDetailInfo.tLogisticsFee.equals("")
						&&mDetailInfo.tLogisticsPhone.equals("") && mDetailInfo.tLogisticsMemo.equals(""))
				{
					mHaveCarryInfo.setVisibility(View.GONE);
					mNoCarryInfo.setVisibility(View.VISIBLE);
				}
				else
				{
					displayCarryInfoContent(mDetailInfo);
					mHaveCarryInfo.setVisibility(View.VISIBLE);
					mNoCarryInfo.setVisibility(View.GONE);
				}

				mInfo = mCarryInfoContent;
			}
		}

//        LinearLayout tButton = (LinearLayout) this.findViewById(R.id.CarryButtonLayout);
		if(mDetailInfo.tBuyerAddress!= null && mDetailInfo.tBuyerAddress.equals("")
                &&mDetailInfo.tLogisticsType !=null && mDetailInfo.tLogisticsType.equals("")
                &&mDetailInfo.tLogisticsName.equals("") && mDetailInfo.tLogisticsFee.equals("")
                &&mDetailInfo.tLogisticsPhone.equals("") && mDetailInfo.tLogisticsMemo.equals("")){
//	        if (tButton != null) {
//	            tButton.setVisibility(View.GONE);
//	        }
		}else{
			ImageView titleShadow = (ImageView)findViewById(R.id.TitleShadow);
	        if (arryButtonLayout != null) {
	        	titleShadow.setVisibility(View.GONE);
	        	arryButtonLayout.setVisibility(View.VISIBLE);
	        }
		}
		
		mViewCanvas.setVisibility(View.VISIBLE);
		mViewCanvas.removeAllViews();
		if(mInfo!=null)
		{
			mViewCanvas.addView(mInfo);
		}
		if ((buyer == 0)
				&& (mResultType == WAIT_BUYER_CONFIRM_GOODS)&&DEAL_INFO == showType)
		{
			mConfirmGoods.setVisibility(View.VISIBLE);
		}
		else
		{
			mConfirmGoods.setVisibility(View.GONE);
		}
		
		if ((buyer == 0)
				&& (mResultType == REMIND_SELLER_DISPATCH_GOOD)&&DEAL_INFO == showType)
		{
			mRemindSeller.setVisibility(View.VISIBLE);
		}
		else
		{
			mRemindSeller.setVisibility(View.GONE);
		}
	}
	
	private void showLifePayDetailInfo()
	{
		ScrollView mInfo = null;
		
		if(lifePayDetailsInfo == null)
		{
			return;
		}
		
		displayLifePayDealInfoContent();
		mInfo = mDealInfoContent;
		
		mViewCanvas.setVisibility(View.VISIBLE);
		mViewCanvas.removeAllViews();
		if(mInfo!=null)
		{
			mViewCanvas.addView(mInfo);
		}
	}
	
	private void displayLifePayDealInfoContent()
	{
		if(originalDetailInfoLyt != null && lifePayDetailInfoLyt != null)
		{
			originalDetailInfoLyt.setVisibility(View.GONE);
			lifePayDetailInfoLyt.setVisibility(View.VISIBLE);
		}
		
		//top
		if(chargeType != null)
		{
			if(chargeType.equalsIgnoreCase("electric"))
			{
				lifePayTitleImage.setImageResource(R.drawable.life_pay_top_electric);
				lifePayTitleKind.setText(R.string.PowerRate);
			}
			else if(chargeType.equalsIgnoreCase("water"))
			{
				lifePayTitleImage.setImageResource(R.drawable.life_pay_top_water);
				lifePayTitleKind.setText(R.string.WaterRate);
			}
			else if(chargeType.equalsIgnoreCase("gas"))
			{
				lifePayTitleImage.setImageResource(R.drawable.life_pay_top_gas);
				lifePayTitleKind.setText(R.string.GasRate);
			}
			else if(chargeType.equalsIgnoreCase("commun"))
			{
				lifePayTitleImage.setImageResource(R.drawable.life_pay_top_commun);
				lifePayTitleKind.setText(R.string.PhoneAndWideBand);
			}
			
			lifePayTitleKind.setTextColor(getResources().getColor(R.color.text_lifepay_title));
		}

		if(billDate != null && billDate.length() > 0)
		{
			lifePayTitleDate.setText(" - " + billDate);
			lifePayTitleDate.setTextColor(getResources().getColor(R.color.text_lifepay_title));
		}
		
		//region
		if(lifePayDetailsInfo.getChargeRegion().length() > 0)
		{
			lifePayCityContent.setText(lifePayDetailsInfo.getChargeRegion());
		}
		else
		{
			lifePayCityLyt.setVisibility(View.GONE);
		}
		
		//company
		if(lifePayDetailsInfo.getChargeCompany().length() > 0)
		{
			lifePayCompanyContent.setText(lifePayDetailsInfo.getChargeCompany());
		}
		else
		{
			lifePayCompanyLyt.setVisibility(View.GONE);
		}
		
		//username
		if(lifePayDetailsInfo.getChargeUserName().length() > 0)
		{
			String content[] = lifePayDetailsInfo.getChargeUserName().split(",");
			if(content != null && content.length >= 2)
			{
				lifePayUserName.setText(content[0]+"：");
				lifePayUserNameContent.setText(content[1]);
			}
			else
			{
				lifePayUserNameLyt.setVisibility(View.GONE);
			}
		}
		else
		{
			lifePayUserNameLyt.setVisibility(View.GONE);
		}
		
		//userno
		if(lifePayDetailsInfo.getChargeTradeNo().length() > 0)
		{
			String content[] = lifePayDetailsInfo.getChargeTradeNo().split(",");
			if(content != null && content.length >= 2)
			{
				lifePayTradeNo.setText(content[0]+"：");
				lifePayTradeNoContent.setText(content[1]);
			}
			else
			{
				lifePayTradeNoLyt.setVisibility(View.GONE);
			}
		}
		else
		{
			lifePayTradeNoLyt.setVisibility(View.GONE);
		}
		
		//amount
		if(lifePayDetailsInfo.getChargeAmount().length() > 0)
		{
			lifePayAmountContent.setText(lifePayDetailsInfo.getChargeAmount());

	    	if(lifePayDetailsInfo.getChargeAmount().indexOf(getString(R.string.Yuan))!= -1)
	    	{
	    		lifePayAmountContent.setText(lifePayDetailsInfo.getChargeAmount());
	    	}
	    	else
	    	{
	    		lifePayAmountContent.setText(lifePayDetailsInfo.getChargeAmount()+getString(R.string.UseCoponYuan));
	    	}
	    	
	    	if(!(lifePayDetailsInfo.getChargePenalty().length() > 0
						|| lifePayDetailsInfo.getHasBillFines().equalsIgnoreCase("true")))
	    	{
	    		lifePayAmountContent.setTextColor(getResources().getColor(R.color.text_orange));
	    	}
		}
		else
		{
			lifePayAmountLyt.setVisibility(View.GONE);
		}
		
		//fee
		if(lifePayDetailsInfo.getChargeFee().length() > 0)
		{
			lifePayFeeContent.setText(lifePayDetailsInfo.getChargeFee());

		   	if(lifePayDetailsInfo.getChargeFee().indexOf(getString(R.string.Yuan))!= -1)
		   	{
		   		lifePayFeeContent.setText(lifePayDetailsInfo.getChargeFee());
		   	}
		   	else
		   	{
		   		lifePayFeeContent.setText(lifePayDetailsInfo.getChargeFee()+getString(R.string.UseCoponYuan));
		   	}
		}
		else
		{
			lifePayFeeLyt.setVisibility(View.GONE);
		}
		
		//penalty
		if(lifePayDetailsInfo.getChargePenalty().length() > 0)
		{
			lifePayPenaltyContent.setText(lifePayDetailsInfo.getChargePenalty());

		   	if(lifePayDetailsInfo.getChargePenalty().indexOf(getString(R.string.Yuan))!= -1)
		   	{
		   		lifePayPenaltyContent.setText(lifePayDetailsInfo.getChargePenalty());
		   	}
		   	else
		   	{
		   		lifePayPenaltyContent.setText(lifePayDetailsInfo.getChargePenalty()+getString(R.string.UseCoponYuan));
		   	}
		}
		else
		{
			lifePayPenaltyLyt.setVisibility(View.GONE);
		}
		
		//total amount
		if(lifePayDetailsInfo.getChargeTotalAmount().length() > 0 
				&& (lifePayDetailsInfo.getChargePenalty().length() > 0
						|| lifePayDetailsInfo.getHasBillFines().equalsIgnoreCase("true")))
		{
			lifePayTotalAmountContent.setText(lifePayDetailsInfo.getChargeTotalAmount());
			
			lifePayTotalAmountContent.setTextColor(getResources().getColor(R.color.text_orange));
		   	if(lifePayDetailsInfo.getChargeTotalAmount().indexOf(getString(R.string.Yuan))!= -1)
		   	{
		   		lifePayTotalAmountContent.setText(lifePayDetailsInfo.getChargeTotalAmount());
		   	}
		   	else
		   	{
		   		lifePayTotalAmountContent.setText(lifePayDetailsInfo.getChargeTotalAmount()+getString(R.string.UseCoponYuan));
		   	}
		}
		else
		{
			lifePayTotalAmountLyt.setVisibility(View.GONE);
		}
		
		//penalty hint
		if(lifePayDetailsInfo.getHasBillFines().equalsIgnoreCase("true")
				&& lifePayDetailsInfo.getChargePenalty().length() == 0)
		{
			lifePayPenaltyHintLyt.setVisibility(View.VISIBLE);
		}
		else
		{
			lifePayPenaltyHintLyt.setVisibility(View.GONE);
		}
		
		//ensure
		Button btnPay = null;
    	btnPay = (Button) mDealInfoContent.findViewById(R.id.ensurebutton);
    	if (btnPay != null)
    	{
	    	btnPay.setText(R.string.LifepayQueryEnsurePaying);
	    	btnPay.setOnClickListener(this);
    	}
    	
    	Button btnAgent = null;
    	btnAgent = (Button) mDealInfoContent.findViewById(R.id.makeAgentButton);
    	if (btnAgent != null)
    	{
    		btnAgent.setVisibility(View.GONE);
    	}
    	
    	Button btnConfirmGoods = null;
    	btnConfirmGoods = (Button) mDealInfoContent.findViewById(R.id.ConfirmGoods);
    	if (btnConfirmGoods != null)
    	{
    		btnConfirmGoods.setVisibility(View.GONE);
    	}
    	
    	Button btnRemindSeller = null;
    	btnRemindSeller = (Button) mDealInfoContent.findViewById(R.id.RemindSeller);
    	if (btnRemindSeller != null)
    	{
    		btnRemindSeller.setVisibility(View.GONE);
    	}
	}
	
	@Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) 
    {
    	super.onActivityResult(requestCode, resultCode, data);

    	if(requestCode == Constant.REQUEST_CODE)
    	{
    		if(resultCode != Activity.RESULT_FIRST_USER){ 
    			//刷新当前页面状态
//                hasRefresh = true;
//    			sendGetDealDetailCommand(mDetailInfo.resultTradeNo, mDetailInfo.tBizType, false);
    			finish();
    		}
    	}else if (requestCode == Constant.REQUEST_LOGIN_BACK) {
			if (resultCode == Activity.RESULT_OK) {
				getDataFromIntent();
			}
		}
    }

	@Override
	public void onClick(View v) {
		switch(v.getId()){
		case R.id.DetailButton:
			showDetailInfo(DEAL_INFO);
			this.mDetailButton.setSelected(true);
			this.mDistributionButton.setSelected(false);
			break;
			
		case R.id.DistributionButton:
			showDetailInfo(CARRY_INFO);
			this.mDetailButton.setSelected(false);
			this.mDistributionButton.setSelected(true);
			break;
			
			//交易详情页的button
		case R.id.ensurebutton:
			if(billType == 1)
			{
				//创建账单
			    int cmd = AlipayHandlerMessageIDs.DEAL_QUERY_LIFEPAY_BILLNO;
				getDataHelper().sendQueryLifePayBillNo(mHandler, cmd, billIdForLifePay);
				String msg = getResources().getString(R.string.DealQueryGetingDetailInfo);
				openProcessDialog(msg);
			}
			else if (mDetailInfo.resultIsBuyer == 0
					&& mDetailInfo.resultType == AlipayTradeStatus.WAIT_BUYER_PAY) {
				//调用快捷支付接口
			    mIsPay = true;
			    
				mProgress = BaseHelper.payDeal(AlipayDealDetailInfoActivity.this, mHandler, mProgress, 
						mDetailInfo.resultTradeNo, getExtToken(),"",
						"TO_CARD_MOBILE".equals(mDetailInfo.tBizType) ? "cell_num_transfer":"trade",//类型 
						mDetailInfo.tBizType);
			}
			break;
			
		case R.id.makeAgentButton:
			if (mDetailInfo.resultIsBuyer == 0 && mDetailInfo.resultType == AlipayTradeStatus.WAIT_BUYER_PAY) {
				int peerpayType = this.mTradeStatus.getMapStatus(mDetailInfo.tPeerPayStatus);
				switch(peerpayType) {
					case AlipayTradeStatus.PEERPAY_INIT:
						//调用取消代付接口
						cancelAgentAnyLink(false);
						UserData mUserData = this.getUserData();
			    		if (mUserData!=null) {
			    			mUserData.resetStatus();
						}
						break;
					default:
						//先检查交易金额
						int start = mDetailInfo.tPayMoney.indexOf(".");
						String intmoney;
						if(start>0)
							intmoney = mDetailInfo.tPayMoney.substring(0, start);
						else {
							if(mDetailInfo.tPayMoney.length() > 1 && mDetailInfo.tPayMoney.endsWith("元"))
								intmoney = mDetailInfo.tPayMoney.substring(0, mDetailInfo.tPayMoney.length() -1);
							else{
								intmoney = mDetailInfo.tPayMoney;
							}
						}
						int money = new Integer(intmoney).intValue();

						int tResult = AlipayInputErrorCheck.NO_ERROR;
						if(money < 1) {
							tResult = AlipayInputErrorCheck.ERROR_TOO_LITTLE;
						}
						
						if (tResult == AlipayInputErrorCheck.NO_ERROR) {
							//跳转到代付页面
							Intent tIntent = new Intent();
							tIntent.setClass(AlipayDealDetailInfoActivity.this, AlipayAgentPay.class);
							tIntent.putExtra(Constant.RQF_TRADE_NO, mDetailInfo.tTradeNumber);
							tIntent.putExtra(Constant.RPF_BIZTYPE, mBizType);
							tIntent.putExtra("isDetailActivity", true);
							
							startActivityForResult(tIntent, Constant.REQUEST_CODE);
						} else {
							//金额小于1元不支持代付申请
							String warningMsg = getResources().getString(R.string.AgentPayLittleMoney);
							
							BaseHelper.recordWarningMsg(this,warningMsg);
							
							getDataHelper().showDialog(this,
									R.drawable.infoicon,
									getResources().getString(R.string.WarngingString), warningMsg,
									getResources().getString(R.string.Ensure), null,
									null, null,
									null, null);
						}
						break;
				}
			}
			break;
		
		//代付详情页的button
		case R.id.agentPayButton:
			if (mPeerpayInfo.resultType == AlipayTradeStatus.PEERPAY_INIT) {
				//调用快捷支付接口
				mProgress = BaseHelper.payDeal(AlipayDealDetailInfoActivity.this, mHandler, mProgress, 
						mPeerpayInfo.tTradeNo, getExtToken(),"","peer_pay", "peerpay_trade");
			}
			break;
		
		case R.id.cancelbutton:
			//调用取消代付接口
			String promptMsg = "您确定拒绝为" + mPeerpayInfo.tBuyerName + "付款吗？";
			getDataHelper().showDialog(this,
    				R.drawable.infoicon,
    				getResources().getString(R.string.StrConfirmPayTitle), promptMsg,
    				getResources().getString(R.string.Ensure), btnRefusePay,
    				getResources().getString(R.string.Cancel), null,
    				null, null);
			
			break;
		case R.id.dealDetail_tab:
			mDICanvas.setVisibility(View.VISIBLE);
			mTransferFlow.setVisibility(View.GONE);
			mDealDetailTab.setSelected(true);
			mDealProcessTab.setSelected(false);
			break;
		case R.id.dealProcess_tab:
			mDICanvas.setVisibility(View.GONE);
			mTransferFlow.setVisibility(View.VISIBLE);
			mDealDetailTab.setSelected(false);
			mDealProcessTab.setSelected(true);
			break;
		case R.id.remindReceiver:
			getDataHelper().sendRemindReceiver(AlipayDealDetailInfoActivity.this,mHandler,AlipayHandlerMessageIDs.SEND_REMIND_RECEIVER_MSG,mDetailInfo.resultTradeNo);
			openProcessDialog(getString(R.string.sendingRemindMessage));
			break;
		}
	}
	
	DialogInterface.OnClickListener btnRefusePay = new DialogInterface.OnClickListener(){
		@Override
		public void onClick(DialogInterface dialog,
				int which) {
			// TODO Auto-generated method stub
			
			cancelAgentAnyLink(true);
		}
	};
	
	private void cancelAgentAnyLink(boolean isPeerpay){
		String bizNo = null;
		
		if (isPeerpay) {
//			bizNo = mPeerpayInfo.tPeerPayId;
			bizNo = mTradeNo;
		} else {
			bizNo = mDetailInfo.tTradeNumber;
		}
		
		getDataHelper().cancelAgentPay(mHandler, MSG_CANCEL_AGENT_ANY_LINK, 
				bizNo, this.mBizType, null);
    	
    	String msg = getResources().getString(R.string.AgentPayCancel);
		openProcessDialog(msg);
    }
	
	private void payingFinish(Message msg){
		int ret = msg.arg1;
		
		if(ret == 1)
		{
			//成功
            if(mIsPay&&Constant.OPERATION_UID!=null){
                getMBus().sendMsg("", Constant.OPERATION_UID, MsgAction.ACT_LAUNCH, null);
                setResult(RESULT_OK);
                finish();
                return;
            }
			if (mPeerpayInfo != null && mPeerpayInfo.resultType == AlipayTradeStatus.PEERPAY_INIT) {
				//代付成功，不直接退出，刷新此页
			    hasRefresh = true;
				sendGetDealDetailCommand(mTradeNo, mBizType, true);
			} else {
				setResult(RESULT_OK);
				if("TO_CARD_MOBILE".equals(mDetailInfo.tBizType)){
					new ContactOtherPop(this, this, mDetailInfo.tSellerAccount).showSuccessPop();
				}else{
					finish();
				}
			}
		}
//        AlipayMsgActivity.mNotRefresh = true;
		//代付调用快捷支付后，需留在当前
		//但是显示的布局应该会有不同
    }
	

    public boolean onKeyDown(int keyCode, KeyEvent event){
        if (keyCode == KeyEvent.KEYCODE_BACK){
            if(hasRefresh){
                setResult(RESULT_OK);
            }
        }
        return super.onKeyDown(keyCode, event);
    }

	@Override
	public void onCancel() {
		finish();
	}
}

class TransferItem{
	TextView dateNode;
	TextView timeNode;
	ImageView imageNode;
	TextView descNode;
}

class LifePayDetailInfo
{
	private String chargeRegion = "";
	private String chargeCompany = "";
	private String chargeUserName = "";
	private String chargeTradeNo = "";
	private String chargeAmount = "";
	private String chargeFee = "";
	private String chargePenalty = "";
	private String chargeTotalAmount = "";
	private String hasBillFines = "";
	
	public String getChargeRegion()
	{
		return chargeRegion;
	}
	public void setChargeRegion(String chargeRegion)
	{
		this.chargeRegion = chargeRegion;
	}
	public String getChargeCompany() 
	{
		return chargeCompany;
	}
	public void setChargeCompany(String chargeCompany) 
	{
		this.chargeCompany = chargeCompany;
	}
	public String getChargeUserName()
	{
		return chargeUserName;
	}
	public void setChargeUserName(String chargeUserName) 
	{
		this.chargeUserName = chargeUserName;
	}
	public String getChargeTradeNo()
	{
		return chargeTradeNo;
	}
	public void setChargeTradeNo(String chargeTradeNo)
	{
		this.chargeTradeNo = chargeTradeNo;
	}
	public String getChargeAmount() 
	{
		return chargeAmount;
	}
	public void setChargeAmount(String chargeAmount)
	{
		this.chargeAmount = chargeAmount;
	}
	public String getChargeFee() 
	{
		return chargeFee;
	}
	public void setChargeFee(String chargeFee)
	{
		this.chargeFee = chargeFee;
	}
	public String getChargePenalty()
	{
		return chargePenalty;
	}
	public void setChargePenalty(String chargePenalty) 
	{
		this.chargePenalty = chargePenalty;
	}
	public String getChargeTotalAmount()
	{
		return chargeTotalAmount;
	}
	public void setChargeTotalAmount(String chargeTotalAmount)
	{
		this.chargeTotalAmount = chargeTotalAmount;
	}
	public String getHasBillFines()
	{
		return hasBillFines;
	}
	public void setHasBillFines(String hasBillFines)
	{
		this.hasBillFines = hasBillFines;
	}
	
	public LifePayDetailInfo()
	{
		
	}
	
	public void setDetailInfo(JSONObject itemObj)
	{
		if(itemObj != null)
		{
			hasBillFines = itemObj.optString("hasBillFines", "");
			chargePenalty = itemObj.optString("billFines", "");
			chargeAmount = itemObj.optString("billPucAmount", "");
			
			String content = itemObj.optString("content");
			JSONObject jsonObj = null;
			try
			{
				jsonObj = new JSONObject(content);
			} 
			catch (JSONException e1)
			{
				e1.printStackTrace();
			}
			
			if(jsonObj != null)
			{
				chargeTotalAmount = jsonObj.optString("chargeAmount", "");
				chargeRegion = jsonObj.optString("chargeRegion", "");
				chargeCompany = jsonObj.optString("chargeUnit", "");
				
				JSONArray userDetails = null;
				try 
				{
					userDetails = jsonObj.getJSONArray("chargeUserInfo");
				} 
				catch (JSONException e1)
				{
					e1.printStackTrace();
				}
				
				if(userDetails != null)
				{
					JSONObject info = null;
					for(int i=0; i<userDetails.length(); i++)
					{
						try
						{
							info = userDetails.getJSONObject(i);
						} 
						catch (JSONException e)
						{
							e.printStackTrace();
						}
						
						if(info != null)
						{
							String label = info.optString("label", "");
							if(label.length() > 0)
							{
								if(chargeUserName.length() == 0)
								{
									chargeUserName = label + "," + info.optString("content", "");
								}
								else
								{
									chargeTradeNo = label + "," + info.optString("content", "");
								}
							}
						}
					}
				}
			}
		}
	}
}
