/*
 * Copyright 2009, Embedded General Inc.  All right reserved
 * 
 * draft:Roger
 * 
 */
package com.alipay.android.client;

import java.util.ArrayList;
import java.util.HashMap;

import org.json.JSONArray;
import org.json.JSONObject;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.alipay.android.appHall.bussiness.CallBackFilter;
import com.alipay.android.client.constant.AlipayHandlerMessageIDs;
import com.alipay.android.client.constant.Constant;
import com.alipay.android.client.util.AlipayInputErrorCheck;
import com.alipay.android.client.util.BaseHelper;
import com.alipay.android.client.util.LephoneConstant;
import com.alipay.android.client.util.Responsor;
import com.alipay.android.comon.component.ProgressDiv;
import com.alipay.android.log.Constants;
import com.alipay.android.net.RequestMaker;
import com.alipay.android.net.http.HttpRequestMaker;
import com.alipay.platform.core.Command;
import com.alipay.platform.view.ActivityMediator;
import com.alipay.platform.view.OnDataChangeListener;
import com.eg.android.AlipayGphone.R;

/**
 * The activity about Card job.
 * 卡通管理
 * </p>
 * @author Roger.Huang
 */
public class SubItemCardActivity extends RootActivity implements OnClickListener, OnItemClickListener, OnItemSelectedListener, OnDataChangeListener {
	@SuppressWarnings("unused")
    private static final String TAG = "SubItemCardActivity";

	JSONObject myJsonObject = null;
	private MessageFilter mMessageFilter = new MessageFilter(this);
	Handler mHandler = new Handler(){
		public void handleMessage(Message msg){
			showResult(msg);
			switch(msg.what){
				case AlipayHandlerMessageIDs.PREQUESTKT_FINISH:
					preQuestKTFinish();
					break;
				case AlipayHandlerMessageIDs.QUESTKT_FINISH:
					requestKTFinish();
					break;
				case AlipayHandlerMessageIDs.BINDINGKT_FINISH:
					bindingKTFinish(msg);
					break;
			}
			super.handleMessage(msg);
		}
	};

	/**
	 * Title Name
	 */
	 private TextView mTitleName=null;

	 /**
	  * Variable for ProgressDialog when paying
	  */
	 private ProgressDiv mProgress=null;

	 private RelativeLayout mInfoCanvas=null;
	 private RelativeLayout mCardInfoCanvas=null;
	 private TextView mMainCardCue=null;
	 private TextView mActiveCardCue = null;
	 private TextView mApplyCardCue=null;
	 private TextView mCardCue1=null;
	 private TextView mCardCue2=null;
	 private TextView mCardCue3=null;
//	 private LinearLayout mCardCanvas = null;
//	 private LinearLayout mCardSummaryArea = null;
//	 private TextView mSummary=null;
	 private Button mCardActiveButton=null;//激活界面button
	 private Button mCardApplyButton = null;//申请界面button
	 private RelativeLayout mKTProtocolContent = null;
	 private TextView mKTProtocolText=null;
	 private TextView mKTProtocolText1=null;
	 private TextView mKTProtocolText2=null;
	 private RelativeLayout mCardApplyStartContent = null;
	 private EditText mNameInput = null;
	 private EditText mCardNoInput = null;
	 private View mCardApplyContent = null;//申请界面
	 private View mKTActivationContent = null;//激活界面
	 private View mWaitSignup = null;
	 private EditText mBankIdInput = null;
	 private EditText mPasswordInput = null;
	 private ListView mBindListView = null;
	 private RelativeLayout mCardMainCanvas = null;
	 private Button mApplyCardButton = null;//申请卡通
	 private RelativeLayout mCardApplyMainCanvas=null;
	 private String  CardNoIdentify= null;

	 private class bankTableList{
		 String bankId = null;
		 String bankName = null;
		 String activeMsg = null;
	 }
	 private static final int KT_ERROR_VIEW = 0;
	 private static final int KT_NEWONE_FIRST = 1;
	 private static final int KT_NEWONE_APPLY = 2;
	 private static final int KT_NEWONE_REFER = 3;
	 private static final int KT_HASBANKLIST = 4;
	 private static final int KT_ACTIVATION = 5;
	 private static final int KT_WAITSIGNUP = 6;
	 private class cardInfo{

		 int cardContentState = KT_ERROR_VIEW;
		 int hasAuthed = -1;			// 0 Ϊ��֤�û��� 1Ϊδ��֤�û�
		 String realName = "";
		 String idCardNo = "";
		 int selectedValidBankId = 0;
		 ArrayList<bankTableList> vaildBankList = new ArrayList<bankTableList>();
		 View currentDisplayContent = null;
		 int selectedBankId = 0;
		 ArrayList<bankTableList> bankList = new ArrayList<bankTableList>();
	 }
	 private cardInfo mCardInfo = new cardInfo();

	 /**
	  * Handler for process Paying successful
	  */
	 private int mErrorType = NO_ERROR;
    private static final int NO_ERROR = -7;
    private static final int BINDING_KT_FINISH = -6;
    private static final int BINDING_KT_START = -5;
    private static final int REQUEST_KT_FINISH = -4;
    private static final int REQUEST_KT_START = -3;
    private static final int PRE_REQUEST_KT_FINISH = -2;
    private static final int PRE_REQUEST_KT_START = -1;
    private static final int GET_RSA_KEY_FINISH = -9;
    private static final int GET_RSA_KEY_START = -8;
    private TextView mUser = null;
    private TextView mAccount = null;

    public static final String GET_RSAKEY_ID = "10";
    private RequestMaker mRequestMaker;
    private CallBackFilter mBackFilter;
	    
	 private void showResult(Message msg) {
		 Responsor responsor = (Responsor)msg.obj;
		 JSONObject obj = responsor.obj;
		 boolean tResultOK = false;
		 boolean needDismissProcessDialog=true;

		 tResultOK = mMessageFilter.process(msg);

		 if ((tResultOK) && (!getDataHelper().isCanceled())){
			 myJsonObject = obj;
			 if (mErrorType == GET_RSA_KEY_START)
			 {
				 mErrorType = GET_RSA_KEY_FINISH;
				 needDismissProcessDialog = false;
			 }
			 else if (mErrorType == PRE_REQUEST_KT_START){
				 mErrorType = PRE_REQUEST_KT_FINISH;
			 }else if (mErrorType == REQUEST_KT_START){
				 mErrorType = REQUEST_KT_FINISH;
			 }else if (mErrorType == BINDING_KT_START){
				 mErrorType = BINDING_KT_FINISH;
			 }
		 }

		 if (needDismissProcessDialog){
		     closeProgress();
		 }
	 }

	 /** Called when the activity is first created. */
	 @Override
    public void onCreate(Bundle savedInstanceState) {
        mRequestMaker = new HttpRequestMaker(getApplicationContext(), R.raw.interfaces);
        mBackFilter = new CallBackFilter(this);

        super.onCreate(savedInstanceState);
        if (LephoneConstant.isLephone()) {
            getWindow().addFlags(LephoneConstant.FLAG_ROCKET_MENU_NOTIFY);
        }
        setContentView(R.layout.alipay_card_320_480);

        loadAllVariables();

        mErrorType = PRE_REQUEST_KT_START;
        getDataHelper().sendPreRequestKT(mHandler, AlipayHandlerMessageIDs.PREQUESTKT_FINISH);
        openProcessDialog(getResources().getString(R.string.PleaseWait));
    }

	 private void loadAllVariables(){
		 // Display Item Name.
		 mTitleName = (TextView) findViewById(R.id.title_text);
		 mTitleName.setText(R.string.CardManager);

//		 mItemIcon = (ImageView) findViewById(R.id.AlipaySubTitleNameIcon);
//		 mItemIcon.setImageResource(R.drawable.cartoon);
		 mCardMainCanvas = (RelativeLayout)LayoutInflater.from(this).inflate(R.layout.alipay_card_main_320_480, null);

		 mInfoCanvas = (RelativeLayout) findViewById(R.id.MainInfoDisplayArea);
		 mCardInfoCanvas = (RelativeLayout) mCardMainCanvas.findViewById(R.id.CardInfoDisplayArea);
//		 mCardCanvas = (LinearLayout) mCardMainCanvas.findViewById(R.id.CardCanvas);
//		 mCardCanvas.setVisibility(View.INVISIBLE);
//		 mCardSummaryArea = (LinearLayout) mCardMainCanvas.findViewById(R.id.CardSummaryArea);
//		 mSummary = (TextView) mCardMainCanvas.findViewById(R.id.CardMainSummaryText);
		 mApplyCardButton = (Button)mCardMainCanvas.findViewById(R.id.ApplyCardButton);
		 mApplyCardButton.setOnClickListener(new OnClickListener()
		 {
			 @Override
			 public void onClick(View v)
			 {
				 mCardInfo.cardContentState = KT_NEWONE_FIRST;
				 updatePreRequestView();
			 }
		 });
		 // sub-view

		 mCardApplyContent = LayoutInflater.from(this).inflate(R.layout.alipay_card_apply_320_480, null);
		 mCardApplyStartContent = (RelativeLayout)mCardApplyContent.findViewById(R.id.CardStatView);
		 mCardApplyMainCanvas = (RelativeLayout)mCardApplyContent.findViewById(R.id.CardApplyMainView);
		 mCardCue1 = (TextView)mCardApplyContent.findViewById(R.id.CardApplyCueText1);
		 mCardCue2 = (TextView)mCardApplyContent.findViewById(R.id.CardApplyCueText2);
		 mCardCue3 = (TextView)mCardApplyContent.findViewById(R.id.CardApplyCueText3);
		 mNameInput = (EditText) mCardApplyContent.findViewById(R.id.CardApplyRealName);
		 mCardNoInput = (EditText) mCardApplyContent.findViewById(R.id.CardApplyIDNO);
		 String tAccount = getAccountName(false);
		 ((TextView)mCardApplyContent.findViewById(R.id.CardApplyAccount)).setText(tAccount);
		 mKTProtocolContent = (RelativeLayout)mCardApplyContent.findViewById(R.id.cardProtocol);
		 mKTProtocolText = (TextView) mKTProtocolContent.findViewById(R.id.cardProtocolText);
		 mKTProtocolText1 = (TextView) mKTProtocolContent.findViewById(R.id.cardProtocolText1);
		 mKTProtocolText2 = (TextView) mKTProtocolContent.findViewById(R.id.cardProtocolText2);
		 mKTActivationContent = LayoutInflater.from(this).inflate(R.layout.alipay_card_active_320_480, null);
		 mCardActiveButton = (Button) mKTActivationContent.findViewById(R.id.CardActiveDetailButton);
		 mCardActiveButton.setVisibility(View.INVISIBLE);
		 mCardActiveButton.setOnClickListener(this);
		 mCardApplyButton = (Button)mCardApplyContent.findViewById(R.id.CardApplyDetailButton);
		 mCardApplyButton.setVisibility(View.INVISIBLE);
		 mCardApplyButton.setOnClickListener(this);
		 mBankIdInput = (EditText) mKTActivationContent.findViewById(R.id.KTBankId);
		 mPasswordInput = (EditText) mKTActivationContent.findViewById(R.id.KTBankPassWord);
		 mActiveCardCue = (TextView) mKTActivationContent.findViewById(R.id.CardActiveCueText);
		 mApplyCardCue = (TextView) mCardApplyContent.findViewById(R.id.CardApplyCueText);
		 mMainCardCue = (TextView) mCardMainCanvas.findViewById(R.id.CardMainCueText);
		 mBindListView = (ListView) LayoutInflater.from(this).inflate(R.layout.alipay_list, null);
		 //mBindListView.setDivider(getResources().getDrawable(R.drawable.line3));
		 
		 mBindListView.setOnItemClickListener(this);
		 mBindListView.setOnItemSelectedListener(this);
		 
		 mWaitSignup = LayoutInflater.from(this).inflate(R.layout.alipay_card_waitsignup_320_480, null);
		 mUser = (TextView)mWaitSignup.findViewById(R.id.CardWaitCueText3);
		 mAccount = (TextView)mWaitSignup.findViewById(R.id.CardWaitCueText4);
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
	 private void addBanklist(int m,bankTableList oneBank){
		 int i = m;
		 if(i != 0 && oneBank.activeMsg.equals(getResources().getString(R.string.CardActiveMsg1))){
			 /*for(int j=0; j<i; j++){
				mCardInfo.bankList.set(j+1, mCardInfo.bankList.get(j));
			}*/
			 mCardInfo.bankList.add(0, oneBank);
		 }else if(i != 0 && oneBank.activeMsg.equals(getResources().getString(R.string.CardActiveMsg2))){
			 int k = 0;
			 for(int j = 0; j < i; j++){
				 if(mCardInfo.bankList.get(j).activeMsg.equals(getResources().getString(R.string.CardActiveMsg1))){
					 k = j;
				 }
			 }
			 /*for(int j=k; j<i; j++){
				mCardInfo.bankList.set(j+1, mCardInfo.bankList.get(j));
			}*/
			 mCardInfo.bankList.add(k, oneBank);
		 }else{
			 mCardInfo.bankList.add(oneBank);
		 }
	 }

	 private void updateCardInfo(){
		 // initialize card info class instance.
		 mCardInfo.selectedBankId = 0;
		 mCardInfo.bankList.clear();
		 int m = 0;

		 mCardInfo.hasAuthed = Integer.valueOf(myJsonObject.optString(Constant.RPF_HASAUTHED));
		 mCardInfo.realName = myJsonObject.optString(Constant.RQF_REALNAME);
		 mCardInfo.idCardNo = myJsonObject.optString(Constant.RQF_IDCARDNO);
//		 Log.i(LOG_TAG, "hasAuthed="+mCardInfo.hasAuthed+" ; "+"realName="+mCardInfo.realName+" ; "+"idCardNo="+mCardInfo.idCardNo);
		 JSONArray array = myJsonObject.optJSONArray(Constant.RPF_BANKLIST);
		 if (array == null){
			 return;
		 }
		 int length = array.length();
		 for(int i=0; i<length; i++){
			 JSONObject itemObj = array.optJSONObject(i);
			 if (itemObj != null){
				 bankTableList oneBank = new bankTableList();
				 oneBank.bankId = itemObj.optString(Constant.RPF_BANKID);
				 oneBank.bankName = itemObj.optString(Constant.RPF_BANKNAME);
				 oneBank.activeMsg = itemObj.optString(Constant.RPF_ACTIVEMSG);
				 if (oneBank.activeMsg.equals("") || (oneBank.activeMsg == null)){
					 mCardInfo.vaildBankList.add(oneBank);
				 }else {
					 addBanklist(m,oneBank);
					 m++;
				 }
				 //    			Log.i(LOG_TAG, "bankId="+oneBank.bankId+" ; "+"bankName="+oneBank.bankName+" ; "+"activeMsg="+oneBank.activeMsg);
			 }
		 }

		 //if (mCardInfo.hasAuthed == 0){
		 mNameInput.setText(mCardInfo.realName);
		 String EndidCardNo = mCardInfo.idCardNo;
		 if(mCardInfo.hasAuthed == 0){ 	
			 if(EndidCardNo != null && !EndidCardNo.equals("")){
				 int idCardNoLength = 0;
				 idCardNoLength = EndidCardNo.length();
				 if(idCardNoLength > 4){
					 String t = "";
					 if(idCardNoLength == 18){
						 t = getResources().getString(R.string.CardInfoPreStr);
					 }else {
						 t = getResources().getString(R.string.CardInfoPreStr1);
					 }
					 EndidCardNo = EndidCardNo.substring(idCardNoLength-4, idCardNoLength);
					 EndidCardNo = t+EndidCardNo;
				 }
			 }
			 mCardNoInput.setText(EndidCardNo);
		 }
		 else {
			 mCardNoInput.setText(EndidCardNo);
		 }
		 CardNoIdentify = EndidCardNo;
		 //		mCardNoInput.setText(mCardInfo.idCardNo);
		 mCardInfo.cardContentState = KT_HASBANKLIST;
		 //if (mCardInfo.bankList.size() != 0){
		 // add new KT item
//		 bankTableList oneBank = new bankTableList();
//		 oneBank.bankId = "";
//		 oneBank.bankName = getResources().getString(R.string.CardBankActMsg_NEWItemString);
//		 oneBank.activeMsg = getResources().getString(R.string.CardBankActMsg_NEW);
//		 mCardInfo.bankList.add(oneBank);
		 //}
	 //}else if (mCardInfo.hasAuthed == 1){
		 //	mCardInfo.cardContentState = KT_HASBANKLIST;//KT_NEWONE_FIRST;
		 //}else{
		 //	mCardInfo.cardContentState = KT_ERROR_VIEW;
		 //}

		 bankListAdapter adapter = (bankListAdapter) mBindListView.getAdapter();
		 if (adapter == null){
			 mBindListView.setAdapter(new bankListAdapter());
		 }else{
			 adapter.notifyDataSetChanged();
		 }
	 }

	 private void updatePreRequestView(){



		 if (mCardInfo.cardContentState == KT_ERROR_VIEW)
		 {
			 return;
		 }
		 /*
    	if (mCardInfo.hasAuthed == 0){
    		// ��֤�û�
    		if (mCardInfo.bankList.size() == 0){
    			// δ�õ�������Ϣ, ���¸�����ͼ
    			mCardInfo.cardContentState = KT_NEWONE_FIRST;
    			updatePreRequestView();
    			return;
    		}else{
		  */
		 if (
				 (mCardInfo.cardContentState == KT_NEWONE_FIRST)
				 || (mCardInfo.cardContentState == KT_NEWONE_APPLY)
//				 || (mCardInfo.cardContentState == KT_NEWONE_REFER)
		 ){
			 newOneKTViewSwitcher();
			 mInfoCanvas.removeAllViews();
			 mInfoCanvas.addView(mCardApplyContent);
			 return;
		 }
		 else if(mCardInfo.cardContentState == KT_HASBANKLIST)
		 {
			 displayMainCanvas();
		 }
		 else{
			 existKTViewSwithcer();
		 }

	 }
	 /*
	  * 银行列表界面
	  */
	 private void displayMainCanvas()
	 {

		 View tContentView = mCardMainCanvas;
		 if (tContentView == null)
		 {
			 mInfoCanvas.removeAllViews();
		 }
		 else
		 {
			 if (!tContentView.equals(mCardInfo.currentDisplayContent)){
				 mInfoCanvas.removeAllViews();
				 mInfoCanvas.addView(tContentView);
			 }
		 }
		 

         if(mCardInfo.bankList.size()!=0){
             mMainCardCue.setText(R.string.HaveCardsText);
         }else{
             mMainCardCue.setText(R.string.NoCardsText);
         }
		 mBindListView.setSelection(mCardInfo.selectedBankId);

	 }
	 /*
	  * 激活界面
	  */
	 private void existKTViewSwithcer(){

		 int tButtonState = View.GONE;
		 String tButtonString = "";
		 View tContentView = mCardMainCanvas;
		 String tCueText = getResources().getString(R.string.HaveCardsText);

		 bankTableList bank = mCardInfo.bankList.get(mCardInfo.selectedBankId);
		 if (bank.activeMsg.equals(getResources().getString(R.string.CardBankActMsg_UNACTIVATION)))
		 {
			 if (mCardInfo.cardContentState == KT_ACTIVATION){
				 tCueText = getResources().getString(R.string.ACTIVATION_Cue);
				 tButtonState = View.VISIBLE;
				 tButtonString = getResources().getString(R.string.CardActive);
				 tContentView = mKTActivationContent;
			 }else{
				 tButtonState = View.VISIBLE;
				 tButtonString = getResources().getString(R.string.CardActive);
			 }
		 }

		 if (tContentView == null)
		 {
			 mInfoCanvas.removeAllViews();
		 }else
		 {
			 if (!tContentView.equals(mCardInfo.currentDisplayContent)){
				 mInfoCanvas.removeAllViews();
				 mInfoCanvas.addView(tContentView);
			 }
		 }
		 mActiveCardCue.setText(tCueText);
		 mCardActiveButton.setText(tButtonString);
		 mCardActiveButton.setVisibility(tButtonState);
		 mBindListView.setSelection(mCardInfo.selectedBankId);
	 }
	 /*
	  * 开通新卡界面
	  */
	 private void newOneKTViewSwitcher(){
		 int tButtonState = View.GONE;
		 String tButtonString = "";
		 String tCueText = "";
		 mCardCue1.setVisibility(View.GONE);
		 mCardCue2.setVisibility(View.GONE);
		 mCardCue3.setVisibility(View.GONE);

		 if (mCardInfo.hasAuthed == 1)
		 {
			 mNameInput.setEnabled(true);
			 mNameInput.setFocusable(true);
			 mCardNoInput.setEnabled(true);
			 mCardNoInput.setFocusable(true);
		 }
		 else
		 {
			 mNameInput.setEnabled(false);
			 mNameInput.setFocusable(false);
			 mCardNoInput.setEnabled(false);
			 mCardNoInput.setFocusable(false);
		 }

		 if (mCardInfo.cardContentState == KT_NEWONE_FIRST){
			 tCueText = getResources().getString(R.string.CardApplyStartAccount);
			 tButtonString = getResources().getString(R.string.CardApplyNext);
			 tButtonState = View.VISIBLE;
			 mCardApplyStartContent.setVisibility(View.VISIBLE);
			 mKTProtocolContent.setVisibility(View.GONE);
			 mCardApplyMainCanvas.setVisibility(View.GONE);
			 RadioButton tRB = (RadioButton) mCardApplyStartContent.findViewById(R.id.CardApllyCSBank);
			 tRB.setText(mCardInfo.vaildBankList.get(mCardInfo.selectedValidBankId).bankName);
		 }else if (mCardInfo.cardContentState == KT_NEWONE_APPLY){
			 tCueText = getResources().getString(R.string.CardApplyAccount)+mCardInfo.vaildBankList.get(mCardInfo.selectedValidBankId).bankName+getResources().getString(R.string.CardApplyAccount2);
			 tButtonString = getResources().getString(R.string.ApplyCardApply);
			 tButtonState = View.VISIBLE;
			 mCardApplyMainCanvas.setVisibility(View.VISIBLE);
			 mCardApplyStartContent.setVisibility(View.GONE);
			 mKTProtocolContent.setVisibility(View.GONE);
		 }else{
			 tCueText = getResources().getString(R.string.CardApplyEffer);
			 tButtonString = getResources().getString(R.string.Ensure);
			 tButtonState = View.VISIBLE;
			 mKTProtocolContent.setVisibility(View.VISIBLE);
			 mCardApplyStartContent.setVisibility(View.GONE);
			 mCardApplyMainCanvas.setVisibility(View.GONE);
			 mCardCue1.setVisibility(View.VISIBLE);
			 mCardCue2.setVisibility(View.VISIBLE);
			 mCardCue3.setVisibility(View.VISIBLE);
			 mCardCue1.setText(mCardInfo.vaildBankList.get(mCardInfo.selectedValidBankId).bankName);
			 mCardCue3.setText(getResources().getString(R.string.CardApplyEffer1));
			 mCardCue2.setVisibility(View.GONE);
			 
		 }

		 mApplyCardCue.setText(tCueText);
		 mCardApplyButton.setText(tButtonString);
		 mCardApplyButton.setVisibility(tButtonState);
	 }

	 private void cardButtonFunction(){
		 if (mCardInfo.cardContentState == KT_NEWONE_REFER){
			 // finish it? TBD
			 jumpToHome();
			 return;
		 }
		 else if (mCardInfo.cardContentState == KT_NEWONE_APPLY){
			 // send cmd to apply KT
			 applyKT();
			 return;
		 }
		 else if (mCardInfo.cardContentState == KT_NEWONE_FIRST){
			 mCardInfo.cardContentState ++;
			 updatePreRequestView();
			 return;
		 }

		 //IndexOutOfBoundsException: edited by liudong
		 if (mCardInfo.bankList.size() > mCardInfo.selectedBankId) {
			 bankTableList bank = mCardInfo.bankList.get(mCardInfo.selectedBankId);
			 if (bank.activeMsg.equals(getResources().getString(R.string.CardBankActMsg_UNACTIVATION))){
				 if (mCardInfo.cardContentState == KT_ACTIVATION){
					 // send activation command.
					 bindingKT();
				 }
			 }
		 }

	 }

	 private void bindingKT(){
		 String tBandId = mBankIdInput.getText().toString();
		 int tResult = AlipayInputErrorCheck.CheckBankNO(tBandId);
		 if (tResult != AlipayInputErrorCheck.NO_ERROR){
			 // check error.
			 String warningMsg;
			 if (tResult == AlipayInputErrorCheck.ERROR_INVALID_FORMAT){
				 warningMsg = getResources().getString(R.string.WarningInvalidBankId);
			 }else if (tResult == AlipayInputErrorCheck.ERROR_NULL_INPUT){
				 warningMsg = getResources().getString(R.string.WarningBankIdEmpty);
			 }else{
				 warningMsg = "UNKNOWN_ERROR TYPE = "+ tResult;
				 //    			Log.i(LOG_TAG, warningMsg);
			 }
			 
			 BaseHelper.recordWarningMsg(this,warningMsg,Constants.CARDMANAGEVIEW);
			 
			 getDataHelper().showDialog(this,
					 R.drawable.infoicon,
					 getResources().getString(R.string.WarngingString), warningMsg,
					 getResources().getString(R.string.Ensure), null,
					 null, null,
					 null, null);
			 mErrorType = NO_ERROR;
			 return;
		 }
		 // ֧������.
		 String tPassword = mPasswordInput.getText().toString();
		 tResult = AlipayInputErrorCheck.checkPayingPassword(tPassword);//.CheckIdentityCard(tPassword);
		 if (tResult != AlipayInputErrorCheck.NO_ERROR){
			 // check error.
			 String warningMsg;
			 if (tResult == AlipayInputErrorCheck.ERROR_INVALID_FORMAT){
				 warningMsg = getResources().getString(R.string.WarningInvalidLoginPassword);
			 }else if (tResult == AlipayInputErrorCheck.ERROR_NULL_INPUT){
				 warningMsg = getResources().getString(R.string.PayPasswordEmpty);
			 }else{
				 warningMsg = "UNKNOWN_ERROR TYPE = "+ tResult;
				 //    			Log.i(LOG_TAG, warningMsg);
			 }
			 
			 BaseHelper.recordWarningMsg(this,warningMsg,Constants.CARDMANAGEVIEW);
			 
			 getDataHelper().showDialog(this,
					 R.drawable.infoicon,
					 getResources().getString(R.string.WarngingString), warningMsg,
					 getResources().getString(R.string.Ensure), null,
					 null, null,
					 null, null);
			 mErrorType = NO_ERROR;
			 return;
		 }


		//
		mErrorType = GET_RSA_KEY_START;
        ActivityMediator activityMediator = new ActivityMediator(this);
        ArrayList<String> params = new ArrayList<String>();
        String sessionId = getSessionId();
        params.add(sessionId);
        params.add(getConfigData().getClientId());
        activityMediator.sendCommand(GET_RSAKEY_ID,  "clean", mRequestMaker, params);
//		 myHelper.sendGetRSAKey(mHandler,
//					AlipayHandlerMessageIDs.START_PROCESS_GOT_DATA);
		
		 openProcessDialog(getResources().getString(R.string.PleaseWait));
	 }
	 
	 private void applyKT(){
		 // ��Ҫ��ʵ������.
		 hideInput(mNameInput);
		 hideInput(mCardNoInput);
		 BaseHelper.setDispayMode(mNameInput, null);
		 BaseHelper.setDispayMode(mCardNoInput, null);
		 String tName = mNameInput.getText().toString();
		 int tResult = AlipayInputErrorCheck.CheckRealName(tName);
		 if (tResult != AlipayInputErrorCheck.NO_ERROR){
			 // check error.
			 String warningMsg;
			 if (tResult == AlipayInputErrorCheck.ERROR_INVALID_FORMAT){
				 warningMsg = getResources().getString(R.string.WarningInvalidRealName);
			 }else if (tResult == AlipayInputErrorCheck.ERROR_NULL_INPUT){
				 warningMsg = getResources().getString(R.string.WarningRealNameEmpty);
			 }else if (tResult == AlipayInputErrorCheck.ERROR_OUTOF_RANGE){
				 warningMsg = getResources().getString(R.string.WarningRealNameOutRange);
			 }else{
				 warningMsg = "UNKNOWN_ERROR TYPE = "+ tResult;
				 //    			Log.i(LOG_TAG, warningMsg);
			 }
			 
			 BaseHelper.recordWarningMsg(this,warningMsg,Constants.CARDMANAGEVIEW);
			 
			 getDataHelper().showDialog(this,
					 R.drawable.infoicon,
					 getResources().getString(R.string.WarngingString), warningMsg,
					 getResources().getString(R.string.Ensure), null,
					 null, null,
					 null, null);
			 mErrorType = NO_ERROR;
			 return;
		 }

		 // ��Ҫ���֤�ż��.
		 String tCardNo = myJsonObject.optString(Constant.RQF_IDCARDNO);
		 //    	Log.i(LOG_TAG, "tCardNo:"+tCardNo);
		 if(mCardInfo.hasAuthed == 1){
			 if (tCardNo == null || tCardNo.equals("")){
				 tCardNo = mCardNoInput.getText().toString();
				 tResult = AlipayInputErrorCheck.CheckIdentityCard(tCardNo);
				 //            	Log.i(LOG_TAG, "tResult:"+tResult);
			 }else{
				 String tCardinputStr = mCardNoInput.getText().toString();
				 if(!tCardNo.equals(tCardinputStr) && !CardNoIdentify.equals(tCardinputStr)){
					 tCardNo = mCardNoInput.getText().toString();
					 tResult = AlipayInputErrorCheck.CheckIdentityCard(tCardNo);
				 }
			 }
			 if (tResult != AlipayInputErrorCheck.NO_ERROR){
				 // check error.
				 String warningMsg;
				 if (tResult == AlipayInputErrorCheck.ERROR_INVALID_FORMAT){
					 warningMsg = getResources().getString(R.string.WarningInvalidIdCard);
					 //            			Log.i(LOG_TAG, warningMsg);
				 }else if (tResult == AlipayInputErrorCheck.ERROR_NULL_INPUT){
					 warningMsg = getResources().getString(R.string.WarningIdCardEmpty);
				 }else{
					 warningMsg = "UNKNOWN_ERROR TYPE = "+ tResult;
					 //            			Log.i(LOG_TAG, warningMsg);
				 }
				 
				 BaseHelper.recordWarningMsg(this,warningMsg,Constants.CARDMANAGEVIEW);
				 
				 getDataHelper().showDialog(this,
						 R.drawable.infoicon,
						 getResources().getString(R.string.WarngingString), warningMsg,
						 getResources().getString(R.string.Ensure), null,
						 null, null,
						 null, null);
				 mErrorType = NO_ERROR;
				 return;

			 }   	
		 }    	

		 // ��������ִ�м��ͨ��
		 mErrorType = REQUEST_KT_START;
		 getDataHelper().sendRequestKT(mHandler, AlipayHandlerMessageIDs.QUESTKT_FINISH,
				 mCardInfo.vaildBankList.get(mCardInfo.selectedValidBankId).bankId,
				 tName,
				 tCardNo);
		 openProcessDialog(getResources().getString(R.string.PleaseWait));  	
	 }

	 private void preQuestKTFinish(){
		 if (mErrorType == PRE_REQUEST_KT_FINISH){
			 //    		Log.i(LOG_TAG, "PreQuest KT OK!");
			 mErrorType = NO_ERROR;

			 updateCardInfo();
			 mCardInfoCanvas.addView(this.mBindListView);
			 displayMainCanvas();
		 }else{
			 //    		Log.i(LOG_TAG, "PreQuest KT Failed!");
			 mErrorType = NO_ERROR;
			 if (getDataHelper().isCanceled()){
//				 jumpToHome();
			 }
		 }
	 }

	 private void requestKTFinish(){
		 if (mErrorType == REQUEST_KT_FINISH){
			 //    		Log.i(LOG_TAG, "Quest KT OK!");
			 mErrorType = NO_ERROR;
			 //String tProtocol = myJsonObject.optString(Constant.RPF_MEMO);
			 //show protocol message here
			 String tProtocol = getResources().getString(R.string.ProtocolMessage);
			 mKTProtocolText.setText(tProtocol);
			 String tProtocol1 = getResources().getString(R.string.ProtocolMessage1);
			 mKTProtocolText1.setText(tProtocol1);
			 String tProtocol2 = getResources().getString(R.string.ProtocolMessage2);
			 mKTProtocolText2.setText(tProtocol2);
//			 mCardInfo.cardContentState = KT_NEWONE_REFER;
			 mCardInfo.cardContentState = KT_HASBANKLIST;
			 getDataHelper().showDialog(this,
					 R.drawable.ok, 
					 getString(R.string.ApplyCardApplySuccess),getString(R.string.WaitSignupSummary)+"\n"+getString(R.string.WaitSignupSummaryTwo)+"\n"+getString(R.string.ApplyRealNameTitleStr).substring(2)+mNameInput.getText().toString()+"\n"+getString(R.string.CardApplyUserName)+getAccountName(false),
					 getString(R.string.Ensure), bindingKTbtnForOk,
					 null, null,
					 null, null);
//			 updatePreRequestView();
		 }else{
			 //    		Log.i(LOG_TAG, "Quest KT Failed!");
			 mErrorType = NO_ERROR;
			 if (getDataHelper().isCanceled()){
				 jumpToHome();
			 }
		 }
	 }

	 DialogInterface.OnClickListener bindingKTbtnForOk = new DialogInterface.OnClickListener(){
		 @Override
		 public void onClick(DialogInterface dialog, int which) {
			 // TODO Auto-generated method stub
			 mCardInfoCanvas.removeAllViews();
			 mErrorType = PRE_REQUEST_KT_START;
			 getDataHelper().sendPreRequestKT(mHandler, AlipayHandlerMessageIDs.PREQUESTKT_FINISH);
			 openProcessDialog(getResources().getString(R.string.PleaseWait));
		 }
	 };
	 private void bindingKTFinish(Message msg){
		 if (mErrorType == BINDING_KT_FINISH){
			 //    		Log.i(LOG_TAG, "Binding KT OK!");
			 Responsor responsor = (Responsor)msg.obj;
			 String errorText = responsor.memo;
			 if(errorText == null || errorText.equals("")){
				 errorText = getResources().getString(R.string.CardActiveSucess);
			 }
			 getDataHelper().showDialog(this,
					 R.drawable.ok, 
					 errorText,getString(R.string.CardActiveSucessInfo),
					 getResources().getString(R.string.Ensure), bindingKTbtnForOk,
					 null, null,
					 null, null);		
			 mErrorType = NO_ERROR;
		 }else{
			 //    		Log.i(LOG_TAG, "Binding KT Failed!");
			 mErrorType = NO_ERROR;
			 if (getDataHelper().isCanceled()){
				 jumpToHome();
			 }
		 }
	 }

	 @Override
	 public void onClick(View v) {
		 // TODO Auto-generated method stub
		 switch(v.getId()){
		 case R.id.CardActiveDetailButton:
			 cardButtonFunction();
			 break;
		 case R.id.CardApplyDetailButton:
			 cardButtonFunction();
			 break;
		 }
	 }

	 private class bankListAdapter extends BaseAdapter{
		 @Override
		 public int getCount() {
			 // TODO Auto-generated method stub
			 return mCardInfo.bankList.size();
		 }

		 @Override
		 public Object getItem(int position) {
			 // TODO Auto-generated method stub
			 return mCardInfo.bankList.get(position);
		 }
		 @Override
		 public boolean isEnabled(int position)
		 {
			 if(mCardInfo.bankList.get(position).activeMsg.equals(getResources().getString(R.string.CardBankActMsg_ACTIVATION)))
			 {
				 return false;
			 }
			 return true;
		 }
		 
		 @Override
		 public long getItemId(int position) {
			 // TODO Auto-generated method stub
			 return position;
		 }

		 private class Item{
			 TextView uiBankName;
			 TextView uiActMsg;
		 }

		 @Override
		 public View getView(int position, View convertView, ViewGroup parent) {
			 // TODO Auto-generated method stub
			 final Item item;
			 if (convertView == null){
				 item = new Item();
				 convertView = LayoutInflater.from(SubItemCardActivity.this).inflate(R.layout.alipay_card_infoitem_320_480, null);
				 item.uiBankName = (TextView) convertView.findViewById(R.id.CardInfoItemBankName);
				 item.uiActMsg = (TextView) convertView.findViewById(R.id.CardInfoItemState);
				 convertView.setTag(item);
			 }else{
				 item = (Item) convertView.getTag();
			 }
			 ImageView im = (ImageView)convertView.findViewById(R.id.InfoItemIcon);
			 bankTableList oneBank = mCardInfo.bankList.get(position);
			 if(!oneBank.activeMsg.equals(SubItemCardActivity.this.getResources().getString(R.string.CardBankActMsg_NEW))){
				 item.uiActMsg.setText(oneBank.activeMsg);				
				 im.setVisibility(View.GONE);
			 }else{
				 item.uiActMsg.setText("");
				 im.setVisibility(View.VISIBLE);
			 }
			 if(oneBank.activeMsg.equals(getResources().getString(R.string.CardBankActMsg_UNACTIVATION)))
			 {
			     item.uiBankName.setTextColor(getResources().getColor(R.color.text_gray));
			     item.uiActMsg.setTextColor(getResources().getColor(R.color.text_gray));
//				 convertView.setBackgroundResource(R.drawable.alipay_list_iteminfo_bg);
			 }
			 else if(oneBank.activeMsg.equals(getResources().getString(R.string.CardBankActMsg_WAITSIGNUP)))
			 {
                 item.uiBankName.setTextColor(getResources().getColor(R.color.text_gray));
                 item.uiActMsg.setTextColor(getResources().getColor(R.color.text_gray));
//				 convertView.setBackgroundResource(R.drawable.alipay_list_iteminfo_bg);
			 }
			 else
			 {
                 item.uiBankName.setTextColor(getResources().getColor(R.color.text_light_gray));
                 item.uiActMsg.setTextColor(getResources().getColor(R.color.text_light_gray));
//				 convertView.setBackgroundResource(R.drawable.list_enable);
			 }
			 item.uiBankName.setText(oneBank.bankName);

			 return convertView;
		 }
	 }

	 @Override
	 public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
			 if(mCardInfo.bankList.get(arg2).activeMsg.equals(getResources().getString(R.string.CardBankActMsg_UNACTIVATION)))
			 {
				 mCardInfo.cardContentState = KT_ACTIVATION;
				 mCardInfo.selectedBankId=arg2;
				 updatePreRequestView();
			 }
			 else if(mCardInfo.bankList.get(arg2).activeMsg.equals(getResources().getString(R.string.CardBankActMsg_WAITSIGNUP)))
			 {
				 mCardInfo.cardContentState=KT_WAITSIGNUP;
				 mInfoCanvas.removeAllViews();
				 mUser.setText(getString(R.string.ApplyRealNameTitleStr).substring(2)+getRealName());
				 mAccount.setText(getString(R.string.CardApplyUserName)+getAccountName(false));
				 mInfoCanvas.addView(mWaitSignup);
			 }
			 else if (mCardInfo.selectedBankId != arg2)				
			 {
				 mCardInfo.selectedBankId = arg2;
				 displayMainCanvas();
			 }
	 }

	 @Override
	 public void onItemSelected(AdapterView<?> arg0, View arg1, int arg2,
			 long arg3) {
		 if (mCardInfo.selectedBankId != arg2){
			 mCardInfo.selectedBankId = arg2;
			 displayMainCanvas();
		 }
	 }

	 @Override
	 public void onNothingSelected(AdapterView<?> arg0) {
	 }






	 private void jumpToHome(){
		 BaseHelper.showDesktop(this);
		 //finish();
	 }

	 private void backToTrueView(){
	
		 if (mCardInfo.cardContentState == KT_NEWONE_APPLY){
			 mCardInfo.cardContentState = KT_NEWONE_FIRST;
			 updatePreRequestView();
		 }else{
			 mCardInfo.cardContentState = KT_HASBANKLIST;
			 updatePreRequestView();
		 }
	 }
	 private void backFunction(){
		 if (mCardInfo.cardContentState == KT_HASBANKLIST || mCardInfo.cardContentState == KT_ERROR_VIEW){
			 //back to first view
			 jumpToHome();
		 }else{
			 backToTrueView();
		 }
	 }
	 public boolean onKeyDown(int keyCode, KeyEvent event) {
		 if (keyCode == KeyEvent.KEYCODE_BACK) {
			 backFunction();
			 return true;
		 }

		 return false;
	 }
	 /**
	  * 隐藏输入法
	  */
	 private void hideInput(EditText text)
	 {
		 text.clearFocus();
		 InputMethodManager imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);   
		 imm.hideSoftInputFromWindow(text.getWindowToken(), 0);
	 }
	 
	 public void onActivityResult(int requestCode, int resultCode, Intent intent) {
		switch(requestCode) {
			case Constant.REQUEST_PHONE_BINDDING:
				if (resultCode == RESULT_OK) {
					 mErrorType = PRE_REQUEST_KT_START;
					 getDataHelper().sendPreRequestKT(mHandler, AlipayHandlerMessageIDs.PREQUESTKT_FINISH);
					 openProcessDialog(getResources().getString(R.string.PleaseWait));
				} else if (resultCode == RESULT_CANCELED) {
					jumpToHome();
				}
				break;
		}
	}

    @Override
    public boolean preCancel(Command command) {
        return false;
    }

    @Override
    public void onCancel(Command command) {
    }

    @Override
    public boolean preFail(Command command) {
        return false;
    }

    @Override
    public void onFail(Command command) {
        closeProgress();

        String content = command.getResponseMessage();
        BaseHelper.showDialog(SubItemCardActivity.this, getString(R.string.Error), content,
            R.drawable.erroricon);
    }

    @SuppressWarnings("unchecked")
    @Override
    public void onComplete(Command command) {
        //
        HashMap<String, Object> hmResponse = (HashMap<String, Object>) command.getResponseData();

        if(!mBackFilter.processCommand(hmResponse)){
            if(command.getmId().equals(GET_RSAKEY_ID)){
                mErrorType = BINDING_KT_START;
                
                String string = (String) hmResponse.get(Constant.RPF_RSA_PK);
                if(string != null){
                    setPublicKey(string);
//                    alipayApp.putInfoData(GlobalDataDefine.RSA_PK, string);
//                    DataHelper.getInstance().mDefaultValueMap.put(Constant.RPF_RSA_PK, (String) string);
                }
                
                string = (String) hmResponse.get(Constant.RPF_RSA_TS);
                if(string != null){
                    setTimeStamp(string);
//                    alipayApp.putInfoData(GlobalDataDefine.RSA_TS, string);
//                    DataHelper.getInstance().mDefaultValueMap.put(Constant.RPF_RSA_TS, (String) string);
                }
                
                // Send command to do BindingKT.
                getDataHelper().sendBindingKT(mHandler, AlipayHandlerMessageIDs.BINDINGKT_FINISH,
                                        mBankIdInput.getText().toString(), mPasswordInput.getText().toString());
            }
        }else{
            closeProgress();
        }        
    }

    @Override
    public void setRuleId(String ruleId) {
    }

    @Override
    public String getRuleId() {
        return null;
    }

    @Override
    public Context getContext() {
        return SubItemCardActivity.this;
    }
    
    void closeProgress(){
        try {
            if(mProgress!=null){
                mProgress.dismiss();
                mProgress = null;
            }
        } catch (Exception e) {
            //e.printStackTrace();
        }
    }    
}
