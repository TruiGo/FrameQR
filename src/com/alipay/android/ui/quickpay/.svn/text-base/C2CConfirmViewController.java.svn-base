package com.alipay.android.ui.quickpay;


import org.json.JSONObject;

import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.alipay.android.appHall.Helper;
import com.alipay.android.biz.QuickPayBiz;
import com.alipay.android.client.constant.Constant;
import com.alipay.android.client.util.BaseHelper;
import com.alipay.android.comon.component.EditTextHasNullChecker;
import com.alipay.android.comon.component.EditTextWithButton;
import com.alipay.android.servicebeans.CreateFastPay;
import com.alipay.android.ui.framework.BaseViewController;
import com.alipay.android.util.HideUtils;
import com.alipay.android.util.JsonConvert;
import com.eg.android.AlipayGphone.R;

public class C2CConfirmViewController extends BaseViewController implements OnClickListener {
	private TextView mTitleView = null;
	private ImageView mTitleShadow = null;
	private Button mcreatQuickPay = null;
	private TextView mPersonNameTextView = null;
	private TextView mAccountTextView = null;
	private EditTextWithButton mPaymentEdit = null;
	private BizAsyncTask mCreateFastPayTask;
	private String  dynamicId = "";
	private String payeeName = "";
	private String payeeAccount = "";
	private String tradeNum ="";
	
	private EditTextHasNullChecker hasNullChecker = new EditTextHasNullChecker();
	
	@Override
	protected void onCreate() {
		super.onCreate();
		JSONObject obj = (JSONObject)params;
		payeeName = obj.optString(Constant.QUICKPAY_C2C_PAYEENAME);
		payeeAccount = obj.optString(Constant.QUICKPAY_C2C_PAYEEACCOUNT);
		dynamicId = obj.optString(Constant.QUICKPAY_C2C_SESSIONCODE);
		mView = LayoutInflater.from(this.getRootController()).inflate(R.layout.quickpay_c2c_confirminfor, null, false);
		addView(mView, null);
		loadAllVariables();
	}

	private void loadAllVariables() {
		mTitleView = (TextView) findViewById(R.id.title_text);
		mTitleView.setText(getRootController().getResources().getString(R.string.quickpay));
		mTitleShadow = (ImageView) findViewById(R.id.TitleShadow);
		mTitleShadow.setVisibility(View.GONE);
		mcreatQuickPay = (Button) findViewById(R.id.creatQuickPay);
		mcreatQuickPay.setClickable(true);
		mcreatQuickPay.setOnClickListener(this);
		hasNullChecker.addNeedEnabledButton(mcreatQuickPay);
		mPersonNameTextView = (TextView) findViewById(R.id.personNameTextView);
		mPersonNameTextView.setText(HideUtils.hide(payeeName));
		mAccountTextView = (TextView) findViewById(R.id.accountTextView);
		mAccountTextView.setText(HideUtils.hide(payeeAccount));
		mPaymentEdit = (EditTextWithButton) findViewById(R.id.paymentEdit);
		mPaymentEdit.getEtContent().addTextChangedListener(hasNullChecker);
		hasNullChecker.addNeedCheckView(mPaymentEdit.getEtContent());
		mPaymentEdit.getEtContent().setTypeface(Typeface.DEFAULT_BOLD,Typeface.BOLD);
		Helper.clearFocus(mPaymentEdit.getRootView());
		mPersonNameTextView.requestFocus();
	}
	
	@Override
	protected Object doInBackground(String bizType, String... params) {
		if (CreateFastPay.BIZ_TAG.equalsIgnoreCase(bizType)) {
			return new QuickPayBiz().createFastPay(params[0], params[1],
					getRootController());
		} else if ("C2CPAY".equalsIgnoreCase(bizType)) {
			closeProgress();
			BaseHelper.payBizDeal(getRootController(), null, params[0],
					getRootController().getExtToken(), null, "trade", "");
		}
		return super.doInBackground(bizType, params);
	}

	@Override
	protected void onPostExecute(String bizType, Object result) {
		if (CreateFastPay.BIZ_TAG.equalsIgnoreCase(bizType)) {
			// closeProgress();
			JSONObject responseJson = JsonConvert
					.convertString2Json((String) result);
			String memo = responseJson != null ? responseJson.optString("memo")
					: getRootController().getResources().getString(
							R.string.createPayFail);
			String status = responseJson != null ? responseJson
					.optString("resultStatus") : "";
			final String isStayCurrentPage = status;
			if ("".equalsIgnoreCase(memo)) {
				memo = getRootController().getResources().getString(
						R.string.createPayFail);// "创建交易失败，请重试";
			}
			if ("100".equalsIgnoreCase(status)) {
				tradeNum = responseJson.optString("tradeNo");
				new BizAsyncTask("C2CPAY", true).execute(tradeNum);
			} else {
				getRootController().getDataHelper().showDialog(
						getRootController(),
						R.drawable.infoicon,
						getRootController().getResources().getString(
								R.string.WarngingString),
						memo,
						getRootController().getResources().getString(
								R.string.Ensure),
						new DialogInterface.OnClickListener() {
							@Override
							public void onClick(DialogInterface dialog,
									int which) {
								mcreatQuickPay.setClickable(true);
								// 做判断，如过金额不对，则停留当前页面，如果创建交易失败的返回上个页面。
								if ("false".equalsIgnoreCase(isStayCurrentPage)) {
									getRootController().pop();
								}

							}
						}, null, null, null, null);

			}
		} else if ("C2CPAY".equalsIgnoreCase(bizType)) {
			getRootController().pop();
		}
		super.onPostExecute(bizType, result);
	}
	
	private void createFastPay() {
		String input = mPaymentEdit.getText().toString();
		if (!input.matches("0*\\.?0{0,2}")) {
			mCreateFastPayTask = new BizAsyncTask(CreateFastPay.BIZ_TAG, true,
					getRootController().getResources().getString(
							R.string.CreatePayTradeInfo));
			mCreateFastPayTask.execute(dynamicId, mPaymentEdit.getText()
					.toString());
		} else {
			mcreatQuickPay.setClickable(true);
			getRootController().getDataHelper().showDialog(
					getRootController(),
					R.drawable.infoicon,
					getRootController().getResources().getString(
							R.string.WarngingString),
					getRootController().getResources().getString(
							R.string.inputPaymentErr),
					getRootController().getResources().getString(
							R.string.Ensure),
					new DialogInterface.OnClickListener() {
						@Override
						public void onClick(DialogInterface dialog, int which) {
						}
					}, null, null, null, null);

		}
	}

	@Override
	public void onClick(View v) {
		int id = v.getId();
		switch (id) {
		case R.id.creatQuickPay:
			 mcreatQuickPay.setClickable(false);
			 createFastPay();
			 //隐藏键盘
			 hideInput(mPaymentEdit.getEtContent());
			break;
		default:
			break;
		}
	}
	
	/**
	 * 隐藏输入法
	 */
	private void hideInput(EditText editText) {
		editText.clearFocus();
		InputMethodManager imm = (InputMethodManager) getRootController()
				.getSystemService(Context.INPUT_METHOD_SERVICE);
		imm.hideSoftInputFromWindow(editText.getWindowToken(), 0);
	}
	
}
