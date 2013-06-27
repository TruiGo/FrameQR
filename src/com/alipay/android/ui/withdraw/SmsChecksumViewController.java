package com.alipay.android.ui.withdraw;

import org.json.JSONObject;

import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.alipay.android.biz.CommonRespHandler;
import com.alipay.android.biz.WithdrawBiz;
import com.alipay.android.client.constant.Constant;
import com.alipay.android.client.util.AlipayInputErrorCheck;
import com.alipay.android.client.util.BaseHelper;
import com.alipay.android.client.util.CheckState;
import com.alipay.android.comon.component.EditTextHasNullChecker;
import com.alipay.android.servicebeans.DrawMoney;
import com.alipay.android.ui.framework.BaseViewController;
import com.alipay.android.util.JsonConvert;
import com.eg.android.AlipayGphone.R;
/**
 * 提现风险短信校验
 * @author caidie.wang
 * CTU认证未风险账户需要进行短信校验，校验后账户可提现限额统一调整为49999.00元
 */
public class SmsChecksumViewController extends BaseViewController{
	
	
	private TextView title;
	private Button checkButton;
	private EditText smsCheckCode;
	private TextView smsCheckCodeTip;
	private Button nextButton;
	
	private EditTextHasNullChecker editTextHasNullChecker;
	private int times = 59;//60秒后可重新获取
	private Runnable countdownRunnable;

	@Override
	protected void onCreate() {
		super.onCreate();
		mView = (LinearLayout)LayoutInflater.from(getRootController()).inflate(R.layout.withdrawals_check, null,false);
		addView(mView, null);
		editTextHasNullChecker = new EditTextHasNullChecker();
		loadAllVariables();		
	}
	
	private void loadAllVariables() {
		title = (TextView)findViewById(R.id.title_text);
		title.setText(R.string.WithDrawal);
		//发送校验码
		checkButton = (Button)findViewById(R.id.GetSmsCheckCodeButton);
		countdownRunnable = new Runnable() {
			@Override
			public void run() {
				if(times >0){
					times--;
					updataView();
					checkButton.postDelayed(this, 1000);
				}
			}
			private void updataView() {
				checkButton.setText("(" +times +")秒后" +getRootController().getString(R.string.GetPassTipViewTwoInfo2));
				checkButton.setTextColor(getRootController().getResources().getColor(R.color.TextColorGray));
	            if(times == 0)
	            {
	            	checkButton.setEnabled(true);
	            	checkButton.setText(getRootController().getString(R.string.GetPassTipViewTwoInfo2));
	            	checkButton.setTextColor(getRootController().getResources().getColor(R.color.ButtonColorYellow));
	            } 
			}
		};
		formatCheckButton();
		checkButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				resendRandomCode();
			}
		});
		//短信校验码输入
		smsCheckCode = (EditText)findViewById(R.id.SmsCheckCode);
		editTextHasNullChecker.addNeedCheckView(smsCheckCode);
		smsCheckCode.addTextChangedListener(editTextHasNullChecker);
		//短信校验码说明
		smsCheckCodeTip = (TextView)findViewById(R.id.CheckCodeTip);
		String tip = (String)params;
		smsCheckCodeTip.setText(tip);
		//确认提交校验
		nextButton = (Button)findViewById(R.id.NextButton);
		editTextHasNullChecker.addNeedEnabledButton(nextButton);
		nextButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				submitRandomCode();
			}
		});
	}
	/**
	 * 初始化倒计时button
	 */
	protected void formatCheckButton() {
		times = 59;
		checkButton.setEnabled(false);
		checkButton.setText("(" + times +")秒后" +getRootController().getString(R.string.GetPassTipViewTwoInfo2));
		checkButton.setTextColor(getRootController().getResources().getColor(R.color.TextColorGray));
		checkButton.postDelayed(countdownRunnable, 1000);
	}
	/**
	 * 校验短信校验码
	 * @param smsCode
	 * @return
	 */
	private boolean checkSmsCode(String smsCode) {
    	CheckState checkState = AlipayInputErrorCheck.checkDigital(getRootController(), smsCode, 4);
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
	 * 发送重新获取验证码请求
	 */
	protected void resendRandomCode() {
		new BizAsyncTask(DrawMoney.BIZ_NEED_RESEND_RANDOM_CODE,true).execute("true");
	}
	/**
	 * 提交短信验证码
	 */
	private void submitRandomCode(){
		String smsCode = smsCheckCode.getText().toString();
		if(!checkSmsCode(smsCode)){
			return;
		}
		new BizAsyncTask(DrawMoney.BIZ_RANDOM_CODE,true).execute(smsCode);
	}
	@Override
	protected Object doInBackground(String bizType, String... params) {
		if(bizType.equalsIgnoreCase(DrawMoney.BIZ_NEED_RESEND_RANDOM_CODE)){
			return new WithdrawBiz().resendRandomCode(params[0]);
		}else if(bizType.equalsIgnoreCase(DrawMoney.BIZ_RANDOM_CODE)){
			return new WithdrawBiz().submitRandomCode(params[0]);
		}
		return super.doInBackground(bizType, params);
	}
	@Override
	protected void onPostExecute(String bizType, Object result) {
		if(bizType.equalsIgnoreCase(DrawMoney.BIZ_NEED_RESEND_RANDOM_CODE)){
			JSONObject response = JsonConvert.convertString2Json((String)result);
			if(!CommonRespHandler.processSpecStatu(this.getRootController(), response)){
				if(CommonRespHandler.getStatus(response) == 2050){//2050
					formatCheckButton();
				}
				return;
			}
		}else if(bizType.equalsIgnoreCase(DrawMoney.BIZ_RANDOM_CODE)){
			JSONObject response = JsonConvert.convertString2Json((String)result);
			if(!CommonRespHandler.processSpecStatu(this.getRootController(), response)){
				return;
			}
			if(CommonRespHandler.filterBizResponse(this.getRootController(), response)){
				processDrawMoneySuccess(response);
			}
		}
		super.onPostExecute(bizType, result);
	}
	
	/**
	 * 提现成功处理
	 * @param response
	 */
	private void processDrawMoneySuccess(JSONObject response) {
		String title = this.getRootController().getString(R.string.CheckCashApplySuccess);
		this.getRootController().getDataHelper().showDialog(this.getRootController(), R.drawable.ok, 
				title, response.optString(Constant.RPF_MEMO), this.getRootController().getString(R.string.Ensure),
				new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog,
							int which) {
						getRootController().finish();
					}
				}, null, null, null, null);
	}
}
