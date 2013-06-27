package com.alipay.android.bizapp.CCR;

import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Message;
import android.telephony.TelephonyManager;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Toast;

import com.alipay.android.appHall.CacheManager;
import com.alipay.android.bizapp.BaseAppActivity;
import com.alipay.android.bizapp.BaseBiz;
import com.alipay.android.bizapp.BasePage;
import com.alipay.android.bizapp.BizFactory;
import com.alipay.android.bizapp.BizType;
import com.alipay.android.client.Login;
import com.alipay.android.client.constant.AlipayHandlerMessageIDs;
import com.alipay.android.client.constant.Constant;
import com.alipay.android.client.partner.PartnerInfo;
import com.alipay.android.client.partner.PartnerSourceId;
import com.alipay.android.client.util.AlipayDataStore;
import com.alipay.android.client.util.Utilz;
import com.alipay.android.core.ParamString;
import com.alipay.android.log.AlipayLogAgent;
import com.alipay.android.log.Constants;
import com.alipay.android.log.StorageStateInfo;
import com.alipay.android.ui.adapter.AlertDialogAdapter;
import com.alipay.android.ui.bean.BankCardInfo;
import com.alipay.android.ui.fragment.PopTeleponeOperators;
import com.eg.android.AlipayGphone.R;

public class CCRActivity extends BaseAppActivity {

    final int PAGE_ID_NEWUSER = 1;
    final int PAGE_ID_OLDUSER = 2;
    final int PAGE_ID_SETTING = 3;
    final int PAGE_ID_KNOWN = 4;

    BasePage mActivePage = null;
    int mActivePageId = -1;
    BaseBiz mBizObj = null;

    CCRSettingPage mSettingPage = null;
    NewUserCCRPage mNewUserPage = null;
    OldUserCCRPage mOldUserPage = null;
    private KnownCCRPage knownCCRPage = null;
    
    CCRBankCardInfo mNowCard = new CCRBankCardInfo();// 本次成功还款信息

    private TelephonyManager tm;
    public List<String> mTelOperatorsName = new ArrayList<String>();
    
    private AlipayDataStore mAlipayDataStore = null;
	private boolean isNew;//当前进行的还款类型
	public PartnerInfo currentPartnerInfo;//当前合作方信息
	

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mAlipayDataStore = new AlipayDataStore(this);
        setContentView(R.layout.page_ccr_newuser);
        if (getUserData() != null) {
            loadPages();
        } else {
            if (savedInstanceState == null) {
                Intent tIntent = new Intent(this, Login.class);
                startActivityForResult(tIntent, Constant.REQUEST_LOGIN_BACK);
            }
        }
        
        tm = (TelephonyManager) this.getSystemService(Context.TELEPHONY_SERVICE);
        mTelOperatorsName.clear();
        mTelOperatorsName.addAll(Arrays.asList(getResources().getStringArray(R.array.teleponeOperators)));
    }

    @Override
    public void onResume() {
        super.onResume();
        if(mNewUserPage!=null){
			mNewUserPage.setRefurbishTYJImage();
		}
		if(mOldUserPage!=null){
			mOldUserPage.setRefurbishTYJImage();
		}
    }

    /*
     * 初始化并加载页面
     */
    public void loadPages() {
        mBizObj = BizFactory.creatBizObject(BizType.BizCCR, this);
        //初始化page
        initPages();
        //处理外部启动        
        processEvoke();
        // 跳转页面
        displayPage();
        // 日志埋点
        storageLog();
    }
    /**
     * 初始化page
     */
    private void initPages(){
    	knownCCRPage = new KnownCCRPage(this);
        knownCCRPage.onCreate(mBizObj);
        mSettingPage = new CCRSettingPage(this);
        mSettingPage.onCreate(mBizObj);
        mNewUserPage = new NewUserCCRPage(this);
        mNewUserPage.onCreate(mBizObj);
        mOldUserPage = new OldUserCCRPage(this);
        mOldUserPage.onCreate(mBizObj);
    }
    /**
     * 显示界面
     */
    private void displayPage(){
    	 int pageid = 0;
         ArrayList<CCRBankCardInfo> savedBankList = BizCCRUtil.getSavedBank(this);
         if(isNew){
         	pageid = PAGE_ID_KNOWN;
         }else if ((savedBankList != null && savedBankList.size() > 0)
             || mAlipayDataStore.getBoolean(getUserId() + AlipayDataStore.BANK_CCR_IS_NEW, false)) {
             pageid = PAGE_ID_OLDUSER;
         } else {
             sendGetBankListRequest();
         }
         if (pageid > 0)
             JumptoPage(pageid, true);
    }
    /**
     * 埋点
     */
    private void storageLog(){
        StorageStateInfo storageStateInfo = StorageStateInfo.getInstance();
        AlipayLogAgent.writeLog(this, Constants.BehaviourID.BIZLAUNCHED, null, null, "09999999", "1.0", "09999999Home", Constants.APPCENTER, "09999999Icon", null);
    }
    /**
     * 处理外部唤起信用卡业务
     */
    private void processEvoke(){
    	if(getIntent() != null){
    		startEvoke();
    	}
    }
    private void startEvoke() {
    	String params = getIntent().getStringExtra(Constant.PARAM);
    	if(params != null){
    		
    		ParamString paramString = new ParamString(params);
    		
    		CCRBankCardInfo ccrBankCardInfo = new CCRBankCardInfo();
        	BankCardInfo bankCardInfo = new BankCardInfo().deserialization(params);
    		currentPartnerInfo = new PartnerInfo().deserialization(params);
    		currentPartnerInfo.setParam(params);
    		
    		String currentParam = paramString.getValue(Constant.CARDNO);
    		
    		if(currentParam != null){//信用卡索引号  客户端内部应用与push调用
    			
    			ccrBankCardInfo.setCardIndexNumber(URLDecoder.decode(currentParam));
    			
    			if(paramString.getValue(Constant.PUSH_CCR_USERID) != null){
    				
    				String userid = URLDecoder.decode(paramString.getValue(Constant.PUSH_CCR_USERID));
    				if (userid.length()>0 && userid.compareTo(getUserId()) != 0) {
    		            this.getDataHelper().showDialog(this, R.drawable.infoicon,
    		                this.getResources().getString(R.string.WarngingString),
    		                this.getResources().getString(R.string.ccr_nopush_promotion),
    		                this.getResources().getString(R.string.Ensure),
    		                new DialogInterface.OnClickListener() {
    		                    @Override
    		                    public void onClick(DialogInterface dialog, int which) {
    		                        CCRActivity.this.gotoAppHallActivity();
    		                    }
    		                }, null, null, null, null);
    		            return;
    		        }
    			}
    		}
    		currentParam = paramString.getValue(Constant.AMOUNT);
    		if(currentParam != null){
    			ccrBankCardInfo.setAmount(URLDecoder.decode(currentParam));
    		}
    		ccrBankCardInfo.setBankCardInfo(bankCardInfo);
    		ccrBankCardInfo.setPartnerInfo(currentPartnerInfo);
        	
        	mOldUserPage.init(ccrBankCardInfo);
        	mNewUserPage.init(ccrBankCardInfo);
        	knownCCRPage.init(ccrBankCardInfo);
        	
			if (Utilz.equalsAtBit(bankCardInfo.getBizType(),
					BankCardInfo.BIZ_CREDITCARDFUND_NEW)) {
				isNew = true;
			} else if (Utilz.equalsAtBit(bankCardInfo.getBizType(),
					BankCardInfo.BIZ_CREDITCARDFUND_OLD)) {
				isNew = false;
			}
    		
    	}
    }

	/**
     * 获取已存信用卡列表请求
     */
    public void sendGetBankListRequest() {
        String msg = getResources().getString(R.string.QrcodeTradeCreate);
        showProgressDialog(this, null, msg);
        HashMap<String, String> mapdata = BizCCRUtil
            .prepareGetBankData(Constant.RQF_CCR_QUERYSAVED);
        mBizObj.sendHttpRequest(this, Constant.RQF_CCR_GETBANKS_TYPE, mapdata,
            AlipayHandlerMessageIDs.CCR_GET_SAVED_BANKLIST);
    }

    /*
     * 页面切换
     * 
     * @see com.alipay.android.bizapp.BaseAppActivity#JumptoPage(int)
     */
    public void JumptoPage(int id, boolean toTop) {

        View toView = null;
        BasePage basePage = null;
        String viewId = "";
        switch (id) {
            case PAGE_ID_SETTING:
                toView = mSettingPage.getPageView();
                basePage = mSettingPage;
                viewId = Constants.CCRPAYOKVIEW;
                break;
            case PAGE_ID_NEWUSER:
                toView = mNewUserPage.getPageView();
                basePage = mNewUserPage;
                viewId = Constants.CCRNEWUSERVIEW;
                break;
            case PAGE_ID_OLDUSER:
                toView = mOldUserPage.getPageView();
                basePage = mOldUserPage;
                viewId = Constants.CCROLDUSERVIEW;
                break;
            case PAGE_ID_KNOWN:
                toView = knownCCRPage.getPageView();
                basePage = knownCCRPage;
                viewId = Constants.CCRKNOWNVIEW;
                break;
            default:
                break;
        }
        if (this.mActivePageId != id) {
            if (mPageViewList.size() > 0) {
                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(mPageViewList.peek().getWindowToken(),
                    InputMethodManager.HIDE_NOT_ALWAYS);
            }
            mActivePageId = id;
            setContentView(toView);
            pushViewToStack(toView, basePage, toTop);
            // 日志埋点
            jumpPageLog(viewId);
        } else {
            basePage.onResume();
        }
    }

    @Override
    public void bizResult(Message msg) {
        // TODO Auto-generated method stub
        switch (msg.what) {
            case AlipayHandlerMessageIDs.CCR_CREATE_CREDIT_CARD_TRADE_FOR_OLD:
                break;
            case AlipayHandlerMessageIDs.CCR_GET_SAVED_BANKLIST:
                break;
        }
    }

    /**
     * 当前还卡成功信用卡信息
     * 
     * @param bankName
     * @param bankMark
     * @param userName
     */
    public void setNowCardInfo(String bankName, String bankMark, String userName, String remindDate) {
        mNowCard.setExistBankInfo(bankName, bankMark, userName, "", "", "", "", remindDate);
    }

    /**
     * 获取当前还款成功信用卡信息
     * 
     * @return
     */
    public CCRBankCardInfo getNowCardInfo() {
        return mNowCard;
    }
    
    public void backPartner(){
    	 if(currentPartnerInfo != null){
    		 String showText = "是否返回？";
    		 if(currentPartnerInfo.getSourceId() != null && currentPartnerInfo.getSourceId().equalsIgnoreCase(PartnerSourceId.ZHANGDAN_51)){
    			 showText = "是否要返回51信用卡客户端？";
    		 }
    		 if(currentPartnerInfo.getReturnUrl() != null && currentPartnerInfo.getParam() != null){
    			 getDataHelper().showDialog(this, R.drawable.infoicon,getResources().getString(R.string.WarngingString),showText,
        				 getResources().getString(R.string.Ensure), new DialogInterface.OnClickListener() {
    						@Override
    						public void onClick(DialogInterface dialog, int which) {
    							Uri uri = Uri.parse(currentPartnerInfo.getReturnUrl());
    							Intent intent = new Intent(Intent.ACTION_VIEW,uri);
    			    			try {
    			    				startActivity(intent);
								} catch (Exception e) {
								}
    			    			
    			    			countMeNotTemporary(true);
    			    			finish();
    						}
    					}, getResources().getString(R.string.Cancel), new DialogInterface.OnClickListener() {
    						@Override
    						public void onClick(DialogInterface dialog, int which) {
    							finish();
    						}
    					}, null, null);
    		 }else{
    			 finish();
    		 }
    	 }else{
    		 finish();
    	 }
    }
    
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == Constant.REQUEST_LOGIN_BACK) {
            if (getUserData() != null) {
                loadPages();
            } else {
                gotoAppHallActivity();
            }
        }
    }
    //复用转账中多个手机号码的popwindow模型
    //private void showMobilePop(final String title,List<String> data,final CCRBankTelMsg bankTelMsg,final String msg){}
    public void sendCCRSMSMsg(CCRBankTelMsg bankTelMsg,String msg){
    	//showTelPop(bankTelMsg,msg);
		String netOperator = tm.getNetworkOperator();
		if (netOperator != null && netOperator.length() >= 5&&bankTelMsg != null) {
			String netType = netOperator.substring(3, 5);
			sendSMSMsg(netType,msg,bankTelMsg);
		}else{
			//弹出选择界面，供用户选择运营商
			showTelPop(bankTelMsg,msg);
		}
    }
    private void sendSMSMsg(String netType ,String text,CCRBankTelMsg bankTelMsg){
		
		String tel = null;
		if (netType.equalsIgnoreCase("00")||netType.equalsIgnoreCase("02")) {
			tel = "smsto:"+bankTelMsg.getYdTel();
		}else if (netType.equalsIgnoreCase("01")) {
			tel = "smsto:"+bankTelMsg.getLtTel();
		}else if (netType.equalsIgnoreCase("03")) {
		    if(bankTelMsg.getDxTel().trim().length()>0){
		    	tel = "smsto:"+bankTelMsg.getDxTel();
			}else{
				//提示用户该手机号码不支持
				Toast.makeText(CCRActivity.this,getText(R.string.NoSupportSendMsg) + "", Toast.LENGTH_SHORT).show();
				return;
			}
		}
		//有匹配的运营商
		if(tel != null){
			Uri smsToUri = Uri.parse(tel);
			Intent mIntent = new Intent( android.content.Intent.ACTION_SENDTO, smsToUri);  
			mIntent.putExtra("sms_body",text);  
			startActivity( mIntent );
		}else {
			showTelPop(bankTelMsg,text);
		}
	}
    private void showTelPop(final CCRBankTelMsg bankTelMsg,final String text){
    	final AlertDialogAdapter alertDialog = new AlertDialogAdapter(this,mTelOperatorsName);
		final PopTeleponeOperators popTeleponeOperators = new PopTeleponeOperators(this,alertDialog);
		
		popTeleponeOperators.showPop(getResources().getString(R.string.TelType),new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				sendSMSMsg(bankTelMsg.getTelTypeId()[popTeleponeOperators.getCheckedItemPosition()],text,bankTelMsg);
				popTeleponeOperators.dismissPop();
			}
		});
    }
}
