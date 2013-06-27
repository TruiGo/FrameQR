package com.alipay.android.ui.transfer;

import java.util.ArrayList;

import org.json.JSONObject;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.BitmapFactory.Options;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.Looper;
import android.text.Editable;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextWatcher;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.text.style.ForegroundColorSpan;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.alipay.android.appHall.Helper;
import com.alipay.android.appHall.common.Defines;
import com.alipay.android.biz.CommonRespHandler;
import com.alipay.android.biz.TransferBiz;
import com.alipay.android.client.AlipayApplication;
import com.alipay.android.client.FreeFlowActivity;
import com.alipay.android.client.constant.Constant;
import com.alipay.android.client.util.AlipayDataStore;
import com.alipay.android.client.util.BaseHelper;
import com.alipay.android.common.data.UserData;
import com.alipay.android.comon.component.ContactPickerView;
import com.alipay.android.comon.component.EditTextHasNullChecker;
import com.alipay.android.comon.component.EditTextWithButton;
import com.alipay.android.core.MsgAction;
import com.alipay.android.dao.TransferReasonDAO;
import com.alipay.android.log.Constants;
import com.alipay.android.servicebeans.ConsultChargeFee;
import com.alipay.android.servicebeans.GetAccountInfo;
import com.alipay.android.servicebeans.PhoneBindingQuery;
import com.alipay.android.servicebeans.ServiceBeanConstants;
import com.alipay.android.ui.bean.AccountInfo;
import com.alipay.android.ui.bean.TransferReceiver;
import com.alipay.android.ui.fragment.ContactOtherPop;
import com.alipay.android.ui.fragment.FragmentHelper;
import com.alipay.android.ui.fragment.PopCancelCallback;
import com.alipay.android.ui.fragment.TransferReasonPop;
import com.alipay.android.ui.framework.BaseViewController;
import com.alipay.android.util.HideUtils;
import com.alipay.android.util.JsonConvert;
import com.alipay.android.util.PinYinAndHanziUtils;
import com.eg.android.AlipayGphone.R;

public class ConfirmViewController extends BaseViewController implements OnClickListener,PopCancelCallback{
	private TransferReceiver transferReceiver;
	private ImageView reasonButton;
	private boolean toAccount;
	private EditTextWithButton recvAccount;
	private AutoCompleteTextView receiverUserNameInput;
	private EditTextWithButton transformMoneyInput;
	private RelativeLayout receiverPhoneLayout;
	private Button confirmTransferInfoButton;
	private RelativeLayout userNameLayout;
	private ContactPickerView notifyPhoneNum;
	private CheckBox inputPhoneNumberCheckBox;
	private EditTextWithButton transformResonInput;
	private ImageButton cleanButton;
	private TextView rateTip;
	private ProgressBar chargeFeeProgessBar;
	private TransferBiz transformBiz;
	private boolean hasSmsMobile;
	private BizAsyncTask  bizAsyncTask;
	private LinearLayout  chargeFeeLayout;
	private EditTextHasNullChecker hasNullChecker = new EditTextHasNullChecker();
	private ImageView transferBg;
	private TextView freeFlowView;
	private TextView receiveTimeView;
	private int isChargeFree;
	private RelativeLayout mAgreeServiceLayout;
	private CheckBox mTransferServiceCheck;
	private TextView mPucRead;
	private ImageView mGuideView;
	private FragmentHelper fragmentHelper;
	private AlipayApplication mApplication;
	
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if(receiverUserNameInput.isPopupShowing() && keyCode == KeyEvent.KEYCODE_BACK){
			receiverUserNameInput.dismissDropDown();
			return true;
		}
		return super.onKeyDown(keyCode, event);
	}
	
	@Override
	public void onControllerInit() {
		if(getRootController().getUserData() != null){
			try{
				getRootController().getDataHelper().dismissDialog();
			}catch (Exception e) {
				e.printStackTrace();
			}
			if(transferReceiver.recvMobile != null && transferReceiver.recvMobile.matches("1\\d{10}")){
				new BizAsyncTask(PhoneBindingQuery.BIZ_TAG,true,getRootController().getText(R.string.verifingAccount) + "").execute(transferReceiver.recvMobile);
			}else if(transferReceiver.recvAccount != null && transferReceiver.recvAccount.matches(".+@.+")){
				new BizAsyncTask(GetAccountInfo.BIZ_TAG,true,getRootController().getText(R.string.verifingAccount) + "").execute(transferReceiver.recvAccount);
			}else{
				populateUI();
			}
		}
	}
	
	@Override
	protected void onCreate() {
		transferReceiver = (TransferReceiver) params;
		mView  = LayoutInflater.from(getRootController()).inflate(R.layout.transfer_confirm_userinfo, null, false);
		addView(mView, null);
		
        transformBiz = new TransferBiz();
        
        loadAllVariables();
        initListener();
        hasNullChecker.addNeedEnabledButton(confirmTransferInfoButton);
	}

	@Override
	protected void onResume() {
		super.onResume();
		if(mProgress != null && mProgress.isShowing() && autoDismiss){
			closeProgress();
		}
	}
	
	private void loadAllVariables() {
		mApplication = (AlipayApplication) getRootController().getApplication();
		fragmentHelper = new FragmentHelper(getRootController());
		transferBg = (ImageView) findViewById(R.id.transfer_bg);
		reasonButton = (ImageView) findViewById(R.id.reasonButton);
		userNameLayout = (RelativeLayout) findViewById(R.id.userNameLayout);
		recvAccount = (EditTextWithButton) findViewById(R.id.recvAccount);
		recvAccount.getEtContent().setFocusable(false);
		receiverUserNameInput = (AutoCompleteTextView) findViewById(R.id.receiverUserNameInput);
		transformMoneyInput = (EditTextWithButton) findViewById(R.id.transformMoneyInput);
		
		hasNullChecker.addNeedCheckView(transformMoneyInput.getEtContent());
		transformMoneyInput.getEtContent().addTextChangedListener(hasNullChecker);
		mAgreeServiceLayout = (RelativeLayout) findViewById(R.id.agreeServiceLayout);
		mTransferServiceCheck = (CheckBox)findViewById(R.id.transferServiceCheck);
		mPucRead = (TextView) findViewById(R.id.PucRead);
		
		chargeFeeLayout = (LinearLayout) findViewById(R.id.chargeFeeLayout);
		transformResonInput = (EditTextWithButton) findViewById(R.id.transformResonInput);
		inputPhoneNumberCheckBox = (CheckBox) findViewById(R.id.inputPhoneNumberCheckBox);
		notifyPhoneNum = (ContactPickerView)findViewById(R.id.notifyPhoneNum);
		receiverPhoneLayout = (RelativeLayout)findViewById(R.id.receiverPhoneLayout);
		confirmTransferInfoButton = (Button) findViewById(R.id.confirmTransformInfoButton);
		rateTip = (TextView) findViewById(R.id.rateTip);
		chargeFeeProgessBar = (ProgressBar) findViewById(R.id.chargeFeeProgessBar);
		freeFlowView = (TextView) findViewById(R.id.free_flow_tip);
		cleanButton = (ImageButton) findViewById(R.id.cleanButton);
		receiveTimeView = (TextView) findViewById(R.id.receiveTime);
		mGuideView = (ImageView) mView.findViewById(R.id.guideView);
	}

	private void populateUI() {
		//转账到手机还是到支付宝账号
        toAccount = Constant.TRADE.equals(transferReceiver.bizType);
		//动态设置界面
		if(toAccount){
			chargeFeeLayout.setVisibility(View.GONE);
			freeFlowView.setVisibility(View.VISIBLE);
			String userRealName = transferReceiver.recvRealName;
			mAgreeServiceLayout.setVisibility(View.GONE);
			
			if(transferReceiver.recvAccount.matches(".+@.+"))
				transferBg.setBackgroundDrawable(getRootController().getResources().getDrawable(R.drawable.transfer_to_account));
			else
				transferBg.setBackgroundDrawable(getRootController().getResources().getDrawable(R.drawable.transfer_to_account_net));
			
			if(userRealName != null && !"".equals(userRealName.trim()) && userRealName.trim().length() > 0){
//				recvAccount.setText( "*" + userRealName.substring(1,userRealName.length()) + " (" + transferReceiver.recvAccount +")");
				if(transferReceiver.transferType==null){//如果用户自己输入账号，不隐藏该账号
					recvAccount.setText( HideUtils.hide(userRealName) + " ("+transferReceiver.recvAccount+")");
				}else{
					recvAccount.setText( HideUtils.hide(userRealName) + " (" + HideUtils.hide(transferReceiver.recvAccount) +")");
				}
			}else{
//				recvAccount.setText(transferReceiver.recvAccount);
				recvAccount.setText(HideUtils.hide(transferReceiver.recvAccount));
			}
			
			UserData userData = getRootController().getUserData();
			String availableStr = "";
			if(userData != null)
				availableStr = userData.getAvailablePayment();
				
			String availablePayment = "";
			if(userData == null || availableStr == null){
			       availablePayment = "" + getRootController().getText(R.string.freeFlowText);
			}else{
				availablePayment = availableStr + getRootController().getText(R.string.freeFlowText);
			}
			availablePayment = Helper.toDBC(availablePayment);
			SpannableString availableSpanStr = new SpannableString(availablePayment);
			availableSpanStr.setSpan(new ClickableSpan() {
				@Override
				public void onClick(View widget) {
					Intent intent = new Intent(getRootController(), FreeFlowActivity.class);
					getRootController().startActivity(intent);
				}
			}, availableSpanStr.length() - 5, availableSpanStr.length() -1, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
			availableSpanStr.setSpan(new ForegroundColorSpan(Color.parseColor("#ABABAB")), 0, availableSpanStr.length() - 5, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
			availableSpanStr.setSpan(new ForegroundColorSpan(Color.parseColor("#ABABAB")), availableSpanStr.length() - 1, availableSpanStr.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
			
			freeFlowView.setText(availableSpanStr);
			freeFlowView.setMovementMethod(LinkMovementMethod.getInstance());
			
			notifyPhoneNum.getInputText().setText(transferReceiver.recvMobile);
			receiverPhoneLayout.setVisibility(View.VISIBLE);
			userNameLayout.setVisibility(View.GONE);
		}else{
			transferBg.setBackgroundDrawable(getRootController().getResources().getDrawable(R.drawable.transfer_to_card));
			mAgreeServiceLayout.setVisibility(View.VISIBLE);
			String userName = transferReceiver.recvName;
			if(userName != null && !"".equals(userName.trim()) && userName.trim().length() > 0){
				if(userName.length() > 6){
					recvAccount.setText(userName.subSequence(0, 6) + "... " + transferReceiver.recvMobile);
//					recvAccount.setText(HideUtils.hide(userName)  + HideUtils.hide(transferReceiver.recvMobile));
				}else{
					recvAccount.setText(userName  + " " + transferReceiver.recvMobile);
//					recvAccount.setText(HideUtils.hide(userName)  + " " + HideUtils.hide(transferReceiver.recvMobile));
				}
			}else{
				recvAccount.setText(transferReceiver.recvMobile);
//				recvAccount.setText(HideUtils.hide(transferReceiver.recvMobile));
			}
			
			if(!"".equals(transferReceiver.recvRealName)){
				receiverUserNameInput.setText(transferReceiver.recvRealName);
			}else{
				receiverUserNameInput.setText(transferReceiver.recvName);
			}
			rateTip.setVisibility(View.VISIBLE);
			if(isChargeFree == 0){
				rateTip.setText(getRootController().getText(R.string.freeNow));
			}else{
				rateTip.setText(getRootController().getText(R.string.zeroRMB));
			}
			receiverPhoneLayout.setVisibility(View.GONE);
			userNameLayout.setVisibility(View.VISIBLE);
			hasNullChecker.addNeedCheckView(receiverUserNameInput);
			
			receiverUserNameInput.addTextChangedListener(hasNullChecker);
		}
		
		recvAccount.getEtContent().setEnabled(false);
		recvAccount.getClearButton().setVisibility(View.GONE);
		
		if(!toAccount && fragmentHelper.needShow(AlipayDataStore.TRANSFERCONFIRM)){//显示引导
			Options options = new Options();
	        options.inDensity =(int) Helper.getDensityDpi(getRootController());
	        options.inScaled = true;
	        mGuideView.setVisibility(View.VISIBLE);
			mGuideView.setBackgroundResource(R.drawable.transfer_flow_03);
			mGuideView.setScaleType(ScaleType.FIT_XY);
			
			mGuideView.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					mGuideView.setVisibility(View.GONE);
					fragmentHelper.hideGuide(AlipayDataStore.TRANSFERCONFIRM);
					if(!toAccount && (transferReceiver.recvRealName == null || "".equals(transferReceiver.recvRealName)))
						showUserDropDown();
				}
			});
		}
	}

	private void initListener() {
		mPucRead.setOnClickListener(this);
		reasonButton.setOnClickListener(this);
		inputPhoneNumberCheckBox.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				notifyPhoneNum.setVisibility(isChecked ? View.VISIBLE : View.GONE);
				hasSmsMobile = isChecked;
				if(isChecked){
					hasNullChecker.addNeedCheckView(notifyPhoneNum.getInputText());
					notifyPhoneNum.getInputText().addTextChangedListener(hasNullChecker);
				}else{
					hasNullChecker.removeNeedCheckView(notifyPhoneNum.getInputText());
				}
			}
		});
		
		if(toAccount && (transferReceiver.recvMobile != null && transferReceiver.recvMobile.matches("1\\d{10}")))
			inputPhoneNumberCheckBox.setChecked(true);
		
		notifyPhoneNum.getPickButton().setOnClickListener(this);
		confirmTransferInfoButton.setOnClickListener(this);
		receiverUserNameInput.setOnClickListener(this);
		receiverUserNameInput.setOnFocusChangeListener(new View.OnFocusChangeListener() {
			@Override
			public void onFocusChange(View v, boolean hasFocus) {
				if(hasFocus && receiverUserNameInput.getText().length() == 0){
					showUserDropDown();
				}
			}
		});
		cleanButton.setOnClickListener(this);
		
		//删除所有输入框字符后需要显示用户名
		receiverUserNameInput.addTextChangedListener(new TextWatcher() {
			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {}
			@Override
			public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
			@Override
			public void afterTextChanged(Editable s) {
				if(s.length() == 0){
					Handler mHander = new Handler(Looper.getMainLooper());
		               mHander.postDelayed(new Runnable() {
		                  @Override
		                  public void run() {
		                     showUserDropDown();
		                  }
		               }, 3);
					cleanButton.setVisibility(View.GONE);
				}else{
					cleanButton.setVisibility(View.VISIBLE);
				}
			}
		});
		
		//转账到手机时才计算费用
		if(!toAccount){
			transformMoneyInput.getEtContent().setOnFocusChangeListener(new View.OnFocusChangeListener() {
				@Override
				public void onFocusChange(View v, boolean hasFocus) {
					if(!hasFocus && isChargeFree != 0){
						if(validateMoneyInput())
							new BizAsyncTask(ConsultChargeFee.BIZ_TAG,false).execute();
						else
							rateTip.setText(getRootController().getText(R.string.zeroRMB));
					}
				}
			});
		}
		
		receiveTimeView.setOnClickListener(this);
	}

	private boolean validateMoneyInput() {
		String moneyInput = transformMoneyInput.getEtContent().getText()+"";
		return moneyInput.trim().length() != 0 && !(moneyInput.matches("0*\\.\\d{0,2}"));
	}

	@Override
	protected Object doInBackground(String bizType, String... params) {
		if(TransferBiz.BIZ_TAG_P2P.equals(bizType)){
			return transformBiz.p2pPay(transformMoneyInput.getText(), //金额
								transformResonInput.getText() + "",//理由 
								transferReceiver.recvAccount, //账号
								hasSmsMobile ? notifyPhoneNum.getInputText().getText() +"" : "",//接收短信号码 
								hasSmsMobile + "", 
								transferReceiver.transferType == null ? "super_transfer" : transferReceiver.transferType,//添加联系人,memo
								getRootController());
		}else if(TransferBiz.BIZ_TAG_P2PHONE.equals(bizType)){
			return transformBiz.p2phonePay(transferReceiver.recvMobile,//收款手机号
					receiverUserNameInput.getText()+"",//收款人姓名
					transformMoneyInput.getText(), //金额
					"",//目标银行的简称 
					"",//到账时间 
					transformResonInput.getText() + "",//理由 
					transferReceiver.transferType == null ? "super_transfer" : transferReceiver.transferType,
					getRootController());
		}else if(ConsultChargeFee.BIZ_TAG.equals(bizType)){
			return transformBiz.getTransformFee("", "", transformMoneyInput.getText());
		}else if(bizType.equalsIgnoreCase(PhoneBindingQuery.BIZ_TAG)){
        	return new TransferBiz().queryPhoneBinding(params[0]);
        }else if(bizType.equalsIgnoreCase(GetAccountInfo.BIZ_TAG)){
        	return new TransferBiz().getTransferAccountInfo(params[0]);
        }
		return super.doInBackground(bizType, params);
	}
	
	@Override
	protected void onPostExecute(String bizType, Object result) {
		if(TransferBiz.BIZ_TAG_P2P.equals(bizType) || TransferBiz.BIZ_TAG_P2PHONE.equals(bizType)){
			JSONObject responseJson = JsonConvert.convertString2Json((String)result);
			if(isOverflowPayLimit(responseJson)){
				return;
			}
			
			if(!CommonRespHandler.processSpecStatu(getRootController(), responseJson)){//是否继续
				return;
			}
			
			if(!CommonRespHandler.filterBizResponse(getRootController(), responseJson)){//失败时跳转到消费记录
				mApplication.getMBus().sendMsg("", "09999985", MsgAction.ACT_LAUNCH, null);
				getRootController().finish();
			}else{
				if(TransferBiz.BIZ_TAG_P2PHONE.equals(bizType)){
					new ContactOtherPop(getRootController(), this, transferReceiver.recvMobile).showSuccessPop();
				}else{
					getRootController().finish();
				}
			}
		}else if(ConsultChargeFee.BIZ_TAG.equals(bizType)){
			JSONObject responseJson = JsonConvert.convertString2Json((String)result);
			if(!CommonRespHandler.processSpecStatu(getRootController(), responseJson)){//是否继续
				return;
			}
			
			rateTip.setVisibility(View.VISIBLE);
			chargeFeeProgessBar.setVisibility(View.GONE);
			//网络错误
			if(Integer.parseInt(responseJson.optString(Defines.resultStatus)) == 0){
				return;
			}else if(CommonRespHandler.filterBizResponse(getRootController(), responseJson)){
				rateTip.setText(responseJson.optString(ServiceBeanConstants.CHARGEFEE));
			}
		}else if(bizType.equalsIgnoreCase(PhoneBindingQuery.BIZ_TAG)){//查询账户类型
        	JSONObject responseJson = JsonConvert.convertString2Json((String)result);
        	if(!CommonRespHandler.processBizSpecStatu(getRootController(), responseJson)){
        		if(responseJson != null && !CommonRespHandler.isCancelInstallSafePay(responseJson)){
        			getRootController().pop();//不是取消快捷支付安装才退出当前界面
        		}
				return;
        	}
        	
			if(CommonRespHandler.filterBizResponse(getRootController(), responseJson)){
				String bindingType = responseJson.optString(Constant.BINDINGTYPE);
				String enableStatus = responseJson.optString(Constant.ENABLESTATUS);
				String userName = responseJson.optString(Constant.RQF_USER_NAME);
				String logonId = responseJson.optString(Constant.RQF_LOGON_ID);
				
				String userId = responseJson.optString(Constant.USERID);
				isChargeFree = responseJson.optInt(Constant.ISCHARGEFREE);
				
				if(AccountInfo.MOBILE_LOGON_ENABLE.equals(bindingType) || 
						(AccountInfo.T.equals(enableStatus) && AccountInfo.SINGLE_BINDING.equals(bindingType))){
					transferReceiver.bizType = Constant.TRADE;
					transferReceiver.recvAccount = logonId;
					transferReceiver.userId = userId;
					transferReceiver.recvRealName = userName;
				}else{
					transferReceiver.bizType = Constant.TRANSFER;
					if(!"".equals(userName)){
						transferReceiver.recvRealName = userName;
					}
				}
				populateUI();
			}else{
				getRootController().pop();
			}
        }else if(bizType.equalsIgnoreCase(GetAccountInfo.BIZ_TAG)){//查询Email账户类型
        	JSONObject responseJson = JsonConvert.convertString2Json((String)result);
        	if(!CommonRespHandler.processBizSpecStatu(getRootController(), responseJson)){
        		if(responseJson != null && !CommonRespHandler.isCancelInstallSafePay(responseJson)){
        			getRootController().pop();//不是取消快捷支付安装才退出当前界面
        		}
				return;
        	}
			if(CommonRespHandler.filterBizResponse(getRootController(), responseJson)){
				transferReceiver.recvRealName = responseJson.optString(Constant.QUICKPAY_C2C_PAYEENAME);
				populateUI();
			}else{
				getRootController().pop();
			}
        }
		super.onPostExecute(bizType, result);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.receiverUserNameInput:
			if(receiverUserNameInput.getText().length() == 0)
				showUserDropDown();
			break;
		case R.id.receiveTime:
			getRootController().navigateTo("SupportBankListView");
			break;
		case R.id.PucRead:
			getRootController().navigateTo("TransferServiceView");
			break;
		case R.id.cleanButton:
			receiverUserNameInput.setText("");
			Helper.clearFocus(receiverUserNameInput.getRootView());
			receiverUserNameInput.requestFocus();
			cleanButton.setVisibility(View.GONE);
			break;
		case R.id.reasonButton:
			new TransferReasonPop(getRootController()).showPop(transformResonInput.getEtContent());
			break;
		case R.id.pickButton:
			BaseHelper.doPhoneContact(getRootController());
			break;
		case R.id.confirmTransformInfoButton:
			if(validateInput()){
				//重复点击取消前一个请求
				if(bizAsyncTask != null && bizAsyncTask.getStatus() != AsyncTask.Status.FINISHED){
					bizAsyncTask.cancel(true);
				}
				String subEventType = "";
				if(toAccount){
					bizAsyncTask =  new BizAsyncTask(TransferBiz.BIZ_TAG_P2P,false);
					subEventType = Constants.EVENTTYPE_CONFIRMTRANSFERINFO_ALIPAYACCOUNT;
				}else{
					bizAsyncTask = new BizAsyncTask(TransferBiz.BIZ_TAG_P2PHONE,false);
					subEventType = Constants.EVENTTYPE_CONFIRMTRANSFERINFO_MOBILENO;
				}
				bizAsyncTask.execute();
				
				/*StorageStateInfo storageStateInfo = StorageStateInfo.getInstance();
				AlipayLogAgent.onEvent(getRootController(), 
						Constants.MONITORPOINT_EVENT, 
						"Y", 
						"", 
						"09999988",
						"1.0",
						storageStateInfo.getValue(Constants.STORAGE_PRODUCTID), 
						getRootController().getUserId(), 
						storageStateInfo.getValue(Constants.STORAGE_PRODUCTVERSION), 
						storageStateInfo.getValue(Constants.STORAGE_CLIENTID),
						subEventType);*/
				
				//保存转账理由
				String transferReason = transformResonInput.getText() + "";
				if(transferReason != null && !"".equals(transferReason))
					new TransferReasonDAO().saveTransferReason(getRootController(), transferReason);
			}
			break;
		default:
			break;
		}
	}
	
	/**
	 * 验证界面上的输入
	 */
	private boolean validateInput() {
		String moneyInput = transformMoneyInput.getEtContent().getText()+"";
		String errorTip = "";
		if(toAccount){//到账户0-2000
			if(moneyInput.matches("0*\\.?0{0,2}")){//金额不能小于0元
				errorTip = getRootController().getResources().getString(R.string.to_account_minlimit);
			}/*else if(!moneyInput.matches("0*[1]?\\d{0,3}(\\.\\d{0,2})?|0*2000(\\.00)?")){
				errorTip = getRootController().getResources().getString(R.string.transfer_ammount_max_tip);
			}*/
		}else{//到手机1-2000
			if(moneyInput.matches("0*\\.\\d{0,2}")){//金额不能小于1元
				errorTip = getRootController().getResources().getString(R.string.transfer_ammount_min_tip);
			}/*else if(!moneyInput.matches("0*[1]?\\d{0,3}(\\.\\d{0,2})?|0*2000(\\.00)?")){
				errorTip = getRootController().getResources().getString(R.string.transfer_ammount_max_tip);
			}*/
			
			String userName = receiverUserNameInput.getText() + "";
			if(!PinYinAndHanziUtils.isHanziAndAlphabet(userName)){
				errorTip = getRootController().getResources().getString(R.string.realName_format_error);
			}
			
			if(userName.trim().length() < 2){
				errorTip = getRootController().getResources().getString(R.string.userName_format_error);
			}
			
			if(!mTransferServiceCheck.isChecked()){
				errorTip = getRootController().getResources().getString(R.string.AgreeServiceOk);
			}
		}
		
		if(hasSmsMobile){
			//输入是否为手机号
			String notifyNum = notifyPhoneNum.getInputText().getText()+"";
			if (toAccount && !notifyNum.matches("(\\+86)?1\\d{10}")) {
				errorTip = getRootController().getResources().getString(R.string.phone_format_tip);
			}
		}
		
		if(!"".equals(errorTip)){
			getRootController().getDataHelper().showDialog(getRootController(), R.drawable.infoicon, 
			getRootController().getResources().getString(R.string.StrConfirmPayTitle), errorTip, 
			getRootController().getResources().getString(R.string.Ensure), null, null, null, null, null);
			return false;
		}
		
		return true;
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (requestCode == Constant.REQUEST_LOGIN_BACK) {
            if (resultCode == Activity.RESULT_OK) {
            	new BizAsyncTask(PhoneBindingQuery.BIZ_TAG,true,getRootController().getText(R.string.verifingAccount) + "").execute(transferReceiver.recvMobile);
            }
        }else{
        	if(data == null || data.getData() == null)
    			return;
    		
    		ArrayList<String> phoneList = BaseHelper.getContactPhoneNo(data.getData(), getRootController());
    		if(phoneList.size() > 0){
    			notifyPhoneNum.getInputText().setText(phoneList.get(0));
    		}
        }
		
		super.onActivityResult(requestCode, resultCode, data);
	}
	
	@Override
	protected void onPreDoInbackgroud(String bizType) {
		if(ConsultChargeFee.BIZ_TAG.equals(bizType)){
			rateTip.setVisibility(View.GONE);
			chargeFeeProgessBar.setVisibility(View.VISIBLE);
		}
		
		if(TransferBiz.BIZ_TAG_P2P.equals(bizType) || TransferBiz.BIZ_TAG_P2PHONE.equals(bizType)){
			showProgress(true);
		}
	}
	
	private void showUserDropDown() {
		if(transferReceiver.recvName != null && !"".equals(transferReceiver.recvName) && !fragmentHelper.needShow(AlipayDataStore.TRANSFERCONFIRM)){
			receiverUserNameInput.setAdapter(new ArrayAdapter<String>(getRootController(), R.layout.drop_down_item,new String[]{transferReceiver.recvName.replaceAll(" ", "")}));
			receiverUserNameInput.showDropDown();
		}
	}

	@Override
	public void onCancel() {
		if(receiverUserNameInput.isPopupShowing())
			receiverUserNameInput.dismissDropDown();
		getRootController().finish();
	}
	
	private boolean isOverflowPayLimit(JSONObject responseJson){
		if(responseJson==null){
			return false;
		}
		switch(CommonRespHandler.getStatus(responseJson)){
		case 3014:
		case 3015:
		case 3016:
		case 3017:
			getRootController().getDataHelper().showDialog(getRootController(),R.drawable.infoicon,getRootController().getResources().getString(R.string.WarngingString), 
					responseJson.optString(Defines.memo), getRootController().getResources().getString(R.string.Ensure),new DialogInterface.OnClickListener() {
						@Override
						public void onClick(DialogInterface dialog,
								int which) {
							showInputPanel();
						}
					}, null, null, null, null);
			return true;
			default:
				return false;
		}
	}
	
	private void showInputPanel() {
        if(getRootController().getWindow() != null && getRootController().getWindow().getCurrentFocus() !=null){
			getRootController().getWindow().getCurrentFocus().clearFocus();
		}
		
		Helper.showInputPanel(getRootController(), transformMoneyInput.getEtContent());
		Helper.moveCursorToLast(transformMoneyInput.getEtContent().getText());
	}
}
