package com.alipay.android.bizapp.CCR;

import java.util.Calendar;
import java.util.HashMap;

import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;

import com.alipay.android.bizapp.BaseBiz;
import com.alipay.android.bizapp.BasePage;
import com.alipay.android.client.constant.AlipayHandlerMessageIDs;
import com.alipay.android.client.constant.Constant;
import com.alipay.android.log.AlipayLogAgent;
import com.alipay.android.log.Constants;
import com.alipay.android.log.Constants.BehaviourID;
import com.eg.android.AlipayGphone.R;

public class CCRSettingPage extends BasePage{
	
	CCRActivity mContext=null;
	private TextView userName;
	private TextView bankNumber;
	private CheckBox pushSettingCB;
	private Spinner pushSetTimeSpinner;
	private Button finishButton;
	
	private RelativeLayout pushSetTimeLayout;
	private ArrayAdapter<String> pushSetTimeSpinnerAdapter;
	private RelativeLayout setPush;
	
	public CCRSettingPage(CCRActivity context){
		mContext = (CCRActivity)context;
	}
	
	@Override
	public void onCreate(BaseBiz bizObj) {
		super.onCreate(bizObj);
		// TODO Auto-generated method stub
		mPageView = (FrameLayout) mContext.getLayoutInflater().inflate(
				R.layout.baseapp_layout, null);
		View view =  View.inflate(mContext, R.layout.page_ccr_setting, null); 
		mPageView.addView(view);
		loadAllVariables();
	}
	@Override
	public void onResume() {
		super.onResume();
		CCRBankCardInfo currentBankItemInfo = mContext.getNowCardInfo();
		userName.setText(currentBankItemInfo.getBankCardInfo().getUserName());
		bankNumber.setText(currentBankItemInfo.getBankCardInfo().getBankName()+"(尾号"+mContext.mNowCard.getCreditCardTailNumber()+")");
		setPushTip();
	}
	

	/**
	 * 设置提醒时间
	 */
	private void setPushTip() {
		CCRBankCardInfo currentBankItemInfo = mContext.getNowCardInfo();
		String isNeedAlert = currentBankItemInfo.getIsNeedAlert();
		String remindDate = currentBankItemInfo.getRemindDate();
		if(isNeedAlert.compareTo("false")==0){
			setPush.setVisibility(View.GONE);
		}else{
			setPushTipAdapter();
			if(!remindDate.equals("")){
				if(Integer.parseInt(remindDate)>0){
					pushSetTimeSpinner.setSelection(Integer.parseInt(remindDate)-1);
				}else{
					pushSetTimeSpinner.setSelection(0);
				}
				pushSettingCB.setChecked(true);
			}else{
				Calendar calendar = Calendar.getInstance();
				int day = calendar.get(Calendar.DAY_OF_MONTH);
				if(day > 0){
					pushSetTimeSpinner.setSelection(day-1);
				}else{
					pushSetTimeSpinner.setSelection(0);
				}
			}
		}
	}
	/**
	 * 为提醒设置适配�?
	 */
	private void setPushTipAdapter(){
		pushSetTimeSpinnerAdapter = new ArrayAdapter<String>(mContext,R.layout.spinner_item);
		for(int i=0;i<31;i++){
    		pushSetTimeSpinnerAdapter.add((i+1)+"日");
    	}
    	
		pushSetTimeSpinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
    	pushSetTimeSpinner.setAdapter(pushSetTimeSpinnerAdapter);
	}
	@Override
	public void loadAllVariables() {
		TextView mTitleName = (TextView) mPageView.findViewById(R.id.title_text);
    	mTitleName.setText(R.string.CCRRemindSet);
    	
    	userName = (TextView)mPageView.findViewById(R.id.UserName);
    	
    	bankNumber = (TextView)mPageView.findViewById(R.id.BankNumber);
    	
    	setPush = (RelativeLayout)mPageView.findViewById(R.id.SetPush);
    	pushSettingCB = (CheckBox)mPageView.findViewById(R.id.PushSettingCB);
    	pushSettingCB.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				if(isChecked){
					pushSetTimeLayout.setVisibility(View.VISIBLE);
				}else{
					pushSetTimeLayout.setVisibility(View.GONE);
				}
			}
		});
    	pushSetTimeLayout = (RelativeLayout)mPageView.findViewById(R.id.PushSetTimeRelativeLayout);
    	//TODO:根据服务器的新接口来设置还款日期
    	pushSetTimeSpinner = (Spinner)mPageView.findViewById(R.id.PushSetTimeSpinner);
    	
    	finishButton = (Button)mPageView.findViewById(R.id.Finish);
    	
    	finishButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if(pushSettingCB.isChecked() && setPush.getVisibility()==View.VISIBLE){
					sendSetCreditCardDepositAlert();
				}
				mContext.backPartner();
//				mContext.finish();
//				mContext.gotoAppHallActivity();
			}
		});
    	
	}
	
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		return true;
	}

	/**
	 * 发送设置还款提�?
	 */
	protected void sendSetCreditCardDepositAlert() {
		TextView textView = (TextView)pushSetTimeSpinner.getSelectedView();
		String textStr = (String)textView.getText();
		String alertDate = textStr.substring(0,textStr.length()-1);
		HashMap<String, String> requestData = BizCCRUtil.prepareSetCreditCardDepositAlert(mContext.mNowCard.getCardIndexNumber(), alertDate);
		mBizObj.sendHttpRequest(mContext, Constant.RQF_CCR_SET_CREDIT_CARD_DEPOSIT_ALERT, requestData,AlipayHandlerMessageIDs.CCR_SET_CREDIT_CARD_DEPOSIT_ALET);
		AlipayLogAgent.writeLog(mContext,BehaviourID.CLICKED, Constants.APPID_CCR, Constants.VIEWID_CCR_SUCCESS, Constants.VIEWID_CCR_SUCCESS, Constants.SEEDID_REMINDBUTTON);
	}
	
	 

	@Override
	public void onDestory() {
		// TODO Auto-generated method stub
		
	}

}
