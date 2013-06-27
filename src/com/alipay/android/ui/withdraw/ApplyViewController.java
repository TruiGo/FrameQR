package com.alipay.android.ui.withdraw;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;

import com.alipay.android.biz.CommonRespHandler;
import com.alipay.android.biz.WithdrawBiz;
import com.alipay.android.client.constant.Constant;
import com.alipay.android.client.util.AlipayInputErrorCheck;
import com.alipay.android.client.util.BaseHelper;
import com.alipay.android.client.util.CheckState;
import com.alipay.android.common.data.UserData;
import com.alipay.android.comon.component.EditTextHasNullChecker;
import com.alipay.android.comon.component.EditTextWithButton;
import com.alipay.android.servicebeans.DrawMoney;
import com.alipay.android.servicebeans.PreDrawMoney;
import com.alipay.android.ui.adapter.WithdrawBankListAdapter;
import com.alipay.android.ui.bean.WithdrawBankInfo;
import com.alipay.android.ui.framework.BaseViewController;
import com.alipay.android.util.JsonConvert;
import com.alipay.android.util.SecureUtil;
import com.eg.android.AlipayGphone.R;
/**
 * 提现主界面
 * @author caidie.wang
 *
 */
public class ApplyViewController extends BaseViewController{
	
	private Spinner bankListSpinner;
	private EditTextWithButton withdrawalsMoney;
	private TextView allowWithdrawalsCash;
	private EditText paymentPassword;
	private Button submit;
	
	private EditTextHasNullChecker editTextHasNullChecker = null;
	private ArrayList<WithdrawBankInfo> bankArrray = null;
	private WithdrawBankInfo currentBankInfo = null;
	
	private String katong = "katong";
	
	private String bankId;//从列表过来的提现
	
	@Override
	protected void onCreate() {
		super.onCreate();
		bankId = (String)params;
		mView = (LinearLayout)LayoutInflater.from(this.getRootController()).inflate(R.layout.withdrawals_case, null, false);
		addView(mView, null);
		editTextHasNullChecker = new EditTextHasNullChecker();
		bankArrray = new ArrayList<WithdrawBankInfo>();
		loadAllVariables();
		doPreDrawMoney();
	}

	private void loadAllVariables() {
		TextView title = (TextView)findViewById(R.id.title_text);
		title.setText(R.string.WithDrawal);
		//提现银行到账时间说明
		final TextView bankTip = (TextView)findViewById(R.id.BankTip);
		//提现金额
		withdrawalsMoney = (EditTextWithButton)findViewById(R.id.WithdrawalsMoney);
		editTextHasNullChecker.addNeedCheckView(withdrawalsMoney.getEtContent());
		withdrawalsMoney.addTextChangedListener(editTextHasNullChecker);
		//可提现金额
		allowWithdrawalsCash = (TextView)findViewById(R.id.AllowWithdrawalsCash);
		//提现账户说明
		final TextView withdrawalsCashTip = (TextView)findViewById(R.id.WithdrawalsCashTip);
		//支付密码
		paymentPassword = (EditText)findViewById(R.id.PaymentPassword);
		editTextHasNullChecker.addNeedCheckView(paymentPassword);
		paymentPassword.addTextChangedListener(editTextHasNullChecker);
		//提现银行列表
		bankListSpinner = (Spinner)findViewById(R.id.WithdrawalsCaseBankList);
		bankListSpinner.setOnItemSelectedListener(new OnItemSelectedListener() {
			@Override
			public void onItemSelected(AdapterView<?> arg0, View arg1,
					int arg2, long arg3) {
				currentBankInfo = bankArrray.get(arg2);
				bankTip.setText(currentBankInfo.getArriveDate());
				withdrawalsCashTip.setText(currentBankInfo.getNewDesc());
			}
			@Override
			public void onNothingSelected(AdapterView<?> arg0) {
				
			}
		});
		//提现确认按钮
		submit = (Button)findViewById(R.id.WithdrawalsEnsure);
		editTextHasNullChecker.addNeedEnabledButton(submit);
		submit.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				doDrawMoney();
			}
		});
	}
	/**
	 * 执行获取提现信息
	 */
	private void doPreDrawMoney() {
		new BizAsyncTask(PreDrawMoney.BIZ_TAG, true).execute();
	}
	/**
	 * 执行提现确认请求
	 */
	private void doDrawMoney() {
		String amount = withdrawalsMoney.getText().toString();
		if(!chenkAmountPass(amount)){
			return;
		}
		String password = paymentPassword.getText().toString();
		if(!checkPasswordPass(password)){
			return;
		}
		password = SecureUtil.encrpt(password, this.getRootController().getPublicKey(), this.getRootController().getTimeStamp());
		UserData mUserData = getRootController().getUserData();
		if (mUserData!=null) {
			mUserData.resetStatus();
		}
		new BizAsyncTask(DrawMoney.BIZ_TAG,true).execute(currentBankInfo.getBankId(), amount, password);
	}
	/**
	 * 设置银行列表Adapter
	 * @param bankListArray
	 */
	private void setBankListAdapter(ArrayList<WithdrawBankInfo> bankListArray){
		WithdrawBankListAdapter bankListAdapter = new WithdrawBankListAdapter(this.getRootController(), bankListArray);
		bankListSpinner.setAdapter(bankListAdapter);
		if(bankId != null && !bankId.equals("")){
			for(int i=0;i<bankListArray.size();i++){
				WithdrawBankInfo currBankInfo = bankListArray.get(i);
				if(currBankInfo.getBankId().equals(bankId)){
					bankListSpinner.setSelection(i);
				}
				
			}
		}
		
	}
	/**
	 * 金额输入框检测
	 * @param amount
	 */
	private boolean chenkAmountPass(String amount){
		CheckState checkState = AlipayInputErrorCheck.checkAmount(getRootController(), amount, getRootController().getString(R.string.WithDrawal));
		if(checkState.bRet){
			return true;
		}else{
			BaseHelper.recordWarningMsg(getRootController(),checkState.strErr);
			this.getRootController().getDataHelper().showDialog(this.getRootController(),
					R.drawable.infoicon, this.getRootController().getString(R.string.WarngingString),
					checkState.strErr, this.getRootController().getString(R.string.Ensure), null, 
					null, null, null, null);
			return false;
		}
	}
	/**
	 * 密码输入框检测
	 * @param password
	 */
	private boolean checkPasswordPass(String password){
		CheckState checkState = AlipayInputErrorCheck.checkPaymentPassword(getRootController(), password, null);
		if(checkState.bRet){
			return true;
		}else{
			BaseHelper.recordWarningMsg(getRootController(),checkState.strErr);
			this.getRootController().getDataHelper().showDialog(this.getRootController(),
					R.drawable.infoicon, this.getRootController().getString(R.string.WarngingString),
					checkState.strErr, this.getRootController().getString(R.string.Ensure), null, 
					null, null, null, null);
			return false;
		}
	}
	
	@Override
	protected Object doInBackground(String bizType, String... params) {
		if(bizType.equalsIgnoreCase(PreDrawMoney.BIZ_TAG)){
			return new WithdrawBiz().sendPreDrawMoney();
		}else if(bizType.equalsIgnoreCase(DrawMoney.BIZ_TAG)){
			return new WithdrawBiz().sendDrawMoney(params[0], params[1], params[2]);
		}
		return super.doInBackground(bizType, params);
	}
	
	@Override
	protected void onPostExecute(String bizType, Object result) {
		if(bizType.equalsIgnoreCase(PreDrawMoney.BIZ_TAG)){
			JSONObject response = JsonConvert.convertString2Json((String)result);
			if(!CommonRespHandler.processSpecStatu(getRootController(), response)){
				return;
			}
			if(CommonRespHandler.filterBizResponse(getRootController(), response)){
				processPreDrawMoneyResponse(response);
			}
		}else if(bizType.equalsIgnoreCase(DrawMoney.BIZ_TAG)){
			JSONObject response = JsonConvert.convertString2Json((String)result);
			if(!CommonRespHandler.processSpecStatu(getRootController(), response)){
				if(CommonRespHandler.getStatus(response) == 2050){//2050
					toSmsChecksumView(response.optString(Constant.MEMO));
				}
				return;
			}
			if(CommonRespHandler.filterBizResponse(getRootController(), response)){
				processDrawMoneyResponse(response);
				UserData userData = getRootController().getUserData();
				if (userData!=null) {
					userData.resetStatus();
				}
			}
		}
		super.onPostExecute(bizType, result);
	}
	/**
	 * CTU风险账户需要跳转到短信验证码校验界面校验
	 */
	private void toSmsChecksumView(String memo) {
		getRootController().navigateTo("SmsChecksumView",memo);
	}

	/**
	 * 提现成功处理
	 * @param response
	 */
	private void processDrawMoneyResponse(JSONObject response) {
		String title = "";
		if(currentBankInfo.getType().equalsIgnoreCase(katong)){
			title = this.getRootController().getString(R.string.CheckCashSuccess);
		}else{
			title = this.getRootController().getString(R.string.CheckCashApplySuccess);
		}
		this.getRootController().getDataHelper().showDialog(this.getRootController(), R.drawable.ok, 
				title, response.optString(Constant.RPF_MEMO), this.getRootController().getString(R.string.Ensure),
				new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog,
							int which) {
						getRootController().pop();
					}
				}, null, null, null, null);
	}

	/**
	 * 获取提现信息解析
	 * @param response
	 */
	private void processPreDrawMoneyResponse(JSONObject response){
		try {
			JSONArray bankList = response.getJSONArray(Constant.BANK_TABLE_LIST);
			if(bankList != null && bankList.length()>0){
				bankArrray.clear();
				WithdrawBankInfo currBankInfo = null;
				JSONObject currentBankItem = null;
				for(int i=0;i<bankList.length();i++){
					currentBankItem = bankList.getJSONObject(i);
					currBankInfo = new WithdrawBankInfo().parserJsonBankInfo(this.getRootController(), currentBankItem);
					bankArrray.add(currBankInfo);
				}
				setBankListAdapter(bankArrray);
			}
			String availableAmount = response.getString(Constant.AVAILABLE_AMOUNT);
			allowWithdrawalsCash.setText(availableAmount+"元");
			String rsaPK = response.getString(Constant.RSA_PK);
			getRootController().setPublicKey(rsaPK);
			String rsaTS = response.getString(Constant.RSA_TS);
			getRootController().setTimeStamp(rsaTS);
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}

}
