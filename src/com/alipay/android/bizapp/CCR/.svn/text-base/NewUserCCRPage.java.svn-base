package com.alipay.android.bizapp.CCR;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.text.Editable;
import android.text.Html;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;

import com.alipay.android.appHall.CacheManager;
import com.alipay.android.bizapp.BaseBiz;
import com.alipay.android.bizapp.BasePage;
import com.alipay.android.client.SchemeHandler;
import com.alipay.android.client.constant.AlipayHandlerMessageIDs;
import com.alipay.android.client.constant.Constant;
import com.alipay.android.client.util.AlipayDataStore;
import com.alipay.android.comon.component.EditTextWithButton;
import com.alipay.android.log.AlipayLogAgent;
import com.alipay.android.log.Constants;
import com.alipay.android.log.Constants.BehaviourID;
import com.alipay.android.ui.pushwebView.PushWebViewRootControllerActivity;
import com.eg.android.AlipayGphone.R;

public class NewUserCCRPage extends BasePage {
	CCRActivity mContext=null;
	
	EditTextWithButton mBankAccount = null;
	EditTextWithButton mUserName = null;
	EditTextWithButton mPayMoney = null;
	Spinner mBankName = null;
	private TextView mSelectBankHint = null;
	Button mNextBtn = null;
	TextView mBankPhonenum = null;
	ImageView mSendBankMsgIcon;
	ImageView mBankPhoneIcon;
	TextView mBankDes = null;
	TextView mBankLimit = null;
	private LinearLayout tabLayout = null;//tab
	Button mNewCardBtn = null;
	Button mOldCardBtn = null;
	
	ArrayList<CCRBankCardInfo> mSupportBank = null;
	String[] mCardBin = null;
	boolean mTextChanged = false;
	int mOldTextLen=0;
	int mOldSelection=0;
	String mOldString="";
	boolean mRemoveBlank = false;

	// 无线体验金
	private ImageView mTiyanjin;
//	private static final String sUrl = "http://wap.alipay.com/clientTowapLogin.htm?goto=http%3A%2F%2Ffun.alipay.com%2Fccrmobile%2Findex.htm%3Fsource%3Dapp";
	private static final String sUrl = "http://fun.alipay.com/qianbaotyj/index.htm?source=app";
	private String mUrl = "";
	private AlipayDataStore mAlipayDataStore;
	private ImageView titleShadow;
	private CCRBankCardInfo currentCCRBankCardInfo;

	
	public static final String PLAIN_TEXT = "0";
	public static final String CACHE_KEY = "1";

//	private AlipayDataStore mAlipayDataStore;
	
	public NewUserCCRPage(Activity context){
		mContext = (CCRActivity)context;
		mAlipayDataStore = new AlipayDataStore(mContext);

	}
	
	@Override
	public void loadAllVariables() {
		TextView titleName = (TextView) mPageView.findViewById(R.id.title_text);
    	titleName.setText(R.string.CCRApp);
		titleShadow = (ImageView)mPageView.findViewById(R.id.TitleShadow);
		titleShadow.setVisibility(View.GONE);
		
    	mTiyanjin = (ImageView) mPageView.findViewById(R.id.CCRtiyanjin);
		if (!mAlipayDataStore.getBoolean(mContext.getUserId(), false)) {
			mTiyanjin.setVisibility(View.VISIBLE);
			mTiyanjin.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					if (mBankName != null && mSupportBank != null) {
						int bankId = (int) mBankName.getSelectedItemId();
						mSupportBank.get(bankId).getBankCardInfo().getBankName();
						 mSupportBank.get(bankId).getBankCardInfo().getBankMark();
						try {
							mUrl = "&cardNo="
									+ mBankAccount.getText()
									+ "&bank="
									+mSupportBank.get(bankId).getBankCardInfo().getBankMark()
									+ "&holderName="
									+ URLEncoder.encode(mUserName.getText(),"gb2312");
						} catch (UnsupportedEncodingException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					} else {
						mUrl = "&cardNo=&bank=&holderName=";
					}
					mUrl = sUrl + mUrl+"&ct=1&extern_token="+mContext.getExtToken();
					Intent intent = new Intent();
		            intent.putExtra(SchemeHandler.PARAM_TITLE,mContext.getResources().getString(R.string.CCRtiyanjin));
		            intent.putExtra(SchemeHandler.PARAM_URL, mUrl);
		            intent.setClass(mContext, PushWebViewRootControllerActivity.class);
		            mContext.startActivity(intent);
				}
			});
		}
		
		mBankPhoneIcon = (ImageView)mPageView.findViewById(R.id.BankPhoneIcon);
    	mBankPhonenum = (TextView)mPageView.findViewById(R.id.BankPhone);
    	mBankPhoneIcon.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View v) {
			    String mobile = mBankPhonenum.getText().toString();
	            if(mobile.matches("^[0-9]*$")){
				Intent intent = new Intent(Intent.ACTION_DIAL, Uri.parse("tel:"
	                     + mobile));
	               mContext.startActivity(intent);
	            }
	            //拨打电话埋点数据
	            AlipayLogAgent.writeLog(mContext,BehaviourID.CLICKED, Constants.APPID_CCR,"-",Constants.VIEWID_CCR_NEW, Constants.SEEDID_PHONEQUERY);
			}
    		
    	});
    	mSendBankMsgIcon = (ImageView)mPageView.findViewById(R.id.SendBankMsgIcon);
    	mSendBankMsgIcon.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View v) {
				//用户输入为空，或者用户输入卡号不合法 现在确定是
				String banaccount = mBankAccount.getText();
				banaccount = banaccount.replaceAll(" ", "");
				if(!BizCCRUtil.checkAccount(mContext, banaccount)){
					return;
				}
				
				String bankAccount = mBankAccount.getText().trim();
				CCRBankTelMsg bankTelMsg = null;
				if (mBankName != null && mSupportBank != null) {
					int bankId = (int) mBankName.getSelectedItemId();
					bankTelMsg = CCRBankTelMsg.getEnumByCode(mSupportBank.get(bankId).getBankCardInfo().getBankMark());
				}
				try{
					if(bankTelMsg!=null){
						mContext.sendCCRSMSMsg(bankTelMsg,bankTelMsg.getMsg().replace("$",bankAccount.substring(bankAccount.length()-4)).replace("@", banaccount));
					}
					AlipayLogAgent.writeLog(mContext,BehaviourID.CLICKED, Constants.APPID_CCR, "-",Constants.VIEWID_CCR_NEW,Constants.SEEDID_SMSQUERY);
				}catch(Exception e){
					e.printStackTrace();
				}
			}
    	});
    	mBankDes = (TextView)mPageView.findViewById(R.id.BankSearchTelephone);
    	mBankLimit = (TextView)mPageView.findViewById(R.id.LimitHint);
    	
		mBankAccount = (EditTextWithButton) mPageView
				.findViewById(R.id.BankAccountEditText);
    	mBankAccount.getEtContent().addTextChangedListener(new TextWatcher(){

			@Override
			public void afterTextChanged(Editable s) {
				if (s.length() == 0) {  
					mBankAccount.setButtonVisiable(false);// ���ذ�ť   
	            } else {  
	            	mBankAccount.setButtonVisiable(true);// ��ʾ��ť   
	            }
				
			}

			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {
				// TODO Auto-generated method stub
				
			}

			@Override
			public void onTextChanged(CharSequence s, int start, int before,
					int count) {
				// TODO Auto-generated method stub
				String account = s.toString();

				//if(account.length()>15)
					//return;
				if(mTextChanged){
					String tempacc = account.replaceAll(" ", "");
					if(tempacc.length()>4&&mSupportBank!=null){
						int bankCount = mSupportBank.size();
				        for(int i=0; i<bankCount; i++){
							ArrayList<String> bin = ((CCRBankCardInfo) mSupportBank
									.get(i)).getCardBinList();
				        	for(String binstr:bin){
				        		if(binstr.compareTo(tempacc)==0){
				        			mBankName.setSelection(i);
				        		}
				        	}
				        }
					}
					mTextChanged = false;
					if(mOldSelection<16)
						mBankAccount.getEtContent().setSelection(
								mOldSelection + mOldSelection / 4);
					else
						mBankAccount.getEtContent().setSelection(19);
						
					return;
				}
				int len = account.length();
				mOldSelection = account
						.substring(0,
								mBankAccount.getEtContent().getSelectionEnd())
						.replaceAll(" ", "").length();
				int selection = mBankAccount.getEtContent().getSelectionEnd();
				if (mOldTextLen > len
						&& mOldString.substring(selection, selection + 1)
								.compareTo(" ") == 0) {
					mOldString = account;
					mOldTextLen = len;
					return;
				}
					
				mTextChanged = true;	
				
				// TODO Auto-generated method stub

				account = account.replaceAll(" ", "");
				int accountlength = account.length();
									
				for(int i=0; i<accountlength/4; i++){	
						//int len2 = account.length();
					account = account.replaceFirst(
							account.substring(0, 4 * (i + 1) + i),
							account.substring(0, 4 * (i + 1) + i) + " ");
					
				}
				len = account.length();
				if(len>19){
					len = 19;
					account.substring(0, 18);
				}
				mOldTextLen = len;
				mOldString = account;
				mBankAccount.setText(account);
				checkNextBtnEnable();
			}
    		
    	});
    	
		mUserName = (EditTextWithButton) mPageView
				.findViewById(R.id.UserNameEditText);
    	if(mContext.isCertificate()){
    		mUserName.setText(mContext.getRealName());
    	}
    	mUserName.getEtContent().addTextChangedListener(new TextWatcher(){

			@Override
			public void afterTextChanged(Editable s) {
				// TODO Auto-generated method stub
				if (s.length() == 0) {  
					mUserName.setButtonVisiable(false);// 隐藏按钮   
	            } else {  
	            	mUserName.setButtonVisiable(true);// 显示按钮   
	            }
				checkNextBtnEnable();
			}

			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {
				// TODO Auto-generated method stub
				
			}

			@Override
			public void onTextChanged(CharSequence s, int start, int before,
					int count) {
				// TODO Auto-generated method stub
				
			}
    	});
    	mPayMoney = (EditTextWithButton) mPageView.findViewById(R.id.CCRCount);
    	mPayMoney.getEtContent().addTextChangedListener(new TextWatcher(){

			@Override
			public void afterTextChanged(Editable s) {
				// TODO Auto-generated method stub
				if (s.length() == 0) {  
					mPayMoney.setButtonVisiable(false);// 隐藏按钮   
	            } else {  
	            	mPayMoney.setButtonVisiable(true);// 显示按钮   
	            }
				checkNextBtnEnable();
			}

			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {
				// TODO Auto-generated method stub
				
			}

			@Override
			public void onTextChanged(CharSequence s, int start, int before,
					int count) {
				// TODO Auto-generated method stub
				try {
                    if (s.length() > 0) {
                        String tString = s.toString().trim();
                        if ((tString.length() > 0)) {
                            int dotPos = tString.indexOf('.');
                            if (dotPos != -1) {
                                if (tString.length() > dotPos + 3) {
                                    tString = tString.substring(0, dotPos + 3);
                                    mPayMoney.setText(tString);
									mPayMoney.getEtContent().setSelection(
											tString.length());
                                }
                                if (dotPos == 0) {
                                    tString = "0" + tString;
                                    mPayMoney.setText(tString);
									mPayMoney.getEtContent().setSelection(
											tString.length());
                                }

                            }
                        }
                    }                   

                } catch (Exception e) {
                    e.printStackTrace();
                }
			}
    		
    	});
    	mNextBtn = (Button) mPageView.findViewById(R.id.next);
    	mNextBtn.setOnClickListener(new View.OnClickListener(){

			@Override
			public void onClick(View v) {
				//get方式连接外部CCDC服务器获取cacheKey
				sendCacheKeyRequest();
				AlipayLogAgent.writeLog(mContext,BehaviourID.SUBMITED, Constants.APPID_CCR, "-",Constants.VIEWID_CCR_NEW, Constants.SEEDID_NEXTBUTTON);
			}
    		
    	});
    	mNextBtn.setEnabled(false);
    	mBankName = (Spinner) mPageView.findViewById(R.id.SelectBank);
    	mBankName.setOnItemSelectedListener(new OnItemSelectedListener(){

			@Override
			public void onItemSelected(AdapterView<?> arg0, View arg1,
					int arg2, long arg3) {
				// TODO Auto-generated method stub

				/*String bankTel = String.format(mContext.getResources()
						.getString(R.string.BankTelDes), mSupportBank.get(arg2)
						.getmBankName());*/
				// String banklimit =
				// String.format(mContext.getResources().getString(R.string.BankLimitStr),
				// mSupportBank.get(arg2).getmAmountLimit());
//				mBankLimit.setText(mSupportBank.get(arg2).getmAmountLimit());
				mBankPhonenum.setText(Html
						.fromHtml("<u>"
								+ mSupportBank.get(arg2).getPhoneNumber()
								+ "</u>"));
				mBankDes.setText(mContext.getResources().getString(R.string.BankTelDes));
				mSelectBankHint.setText(mSupportBank.get(arg2)
						.getArriveDatetime());
				if(mSupportBank.get(arg2).getPhoneNumber()!=null&&(mSupportBank.get(arg2).getPhoneNumber().length()>0)&&(!mSupportBank.get(arg2).getPhoneNumber().equalsIgnoreCase("暂无电话"))){
					mBankPhoneIcon.setVisibility(View.VISIBLE);
				}else{
					mBankPhoneIcon.setVisibility(View.GONE);
				}
				if (CCRBankTelMsg.getEnumByCode(mSupportBank.get(arg2).getBankCardInfo()
						.getBankMark()) != null) {
					mSendBankMsgIcon.setVisibility(View.VISIBLE);
				}else{
					mSendBankMsgIcon.setVisibility(View.GONE);
				}
				if(mSupportBank.get(arg2).getPhoneNumber()!=null&&(mSupportBank.get(arg2).getPhoneNumber().length()>0)&&(!mSupportBank.get(arg2).getPhoneNumber().equalsIgnoreCase("暂无电话"))){
					mBankPhoneIcon.setVisibility(View.VISIBLE);
				}else{
					mBankPhoneIcon.setVisibility(View.GONE);
				}
				if (CCRBankTelMsg.getEnumByCode(mSupportBank.get(arg2).getBankCardInfo()
						.getBankMark()) != null) {
					mSendBankMsgIcon.setVisibility(View.VISIBLE);
				}else{
					mSendBankMsgIcon.setVisibility(View.GONE);
				}
			}
			@Override
			public void onNothingSelected(AdapterView<?> arg0) {
				// TODO Auto-generated method stub
			}
    		
    	});
    	
		mSelectBankHint = (TextView) mPageView
				.findViewById(R.id.SelectBankHint);
    	tabLayout = (LinearLayout)mPageView.findViewById(R.id.TabLayout);
    	mNewCardBtn = (Button)mPageView.findViewById(R.id.NewUserButton);
    	mNewCardBtn.setSelected(true);
    	mOldCardBtn = (Button)mPageView.findViewById(R.id.OldUserButton);
    	mOldCardBtn.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				mContext.JumptoPage(mContext.PAGE_ID_OLDUSER, true);
			}
    		
    	});
	}

	/**
	 * get方式连接外部CCDC服务器获取cacheKey
	 */
	protected void sendCacheKeyRequest() {
		String banaccount = mBankAccount.getText();
		banaccount = banaccount.replaceAll(" ", "");
		if(!BizCCRUtil.checkAccount(mContext, banaccount)){
			return;
		}

		String userName = mUserName.getText();
		if (!BizCCRUtil.checkName(mContext, userName)) {
			return;
		}

		String payMoney = mPayMoney.getText();
		if (!BizCCRUtil.checkMoney(mContext, payMoney)) {
			return;
		}

//		String url = "http://ccdc.shd21.alipay.net/cacheWapCardInfo.json";
//		String url = "https://ccdcapi.alipay.com/cacheWapCardInfo.json";//线上
//		 String url ="http://ccdc.stable.alipay.net/cacheWapCardInfo.json";//开发环�?
//		String url = "http://ccdcapi.sit.alipay.net/cacheWapCardInfo.json";// sit环境
		String url = Constant.getCcdcURL(mContext) + "cacheWapCardInfo.json";
		String requestData = "cardNo="+banaccount;
		String currentUrl = BizCCRUtil.prepareCCDCUrl(url,requestData);
		mBizObj.mHttpClient.setUrl(currentUrl);
		String msg = mContext.getResources().getString(
				R.string.QrcodeTradeCreate);
		mContext.showProgressDialog(mContext, null, msg);
		mBizObj.sendHttpRequestToGet(mContext,
				AlipayHandlerMessageIDs.CCR_LINK_CCDC_GET_CACHEKEY);
	}

	@Override
	public void onCreate(BaseBiz bizObj) {
		super.onCreate(bizObj);
		mPageView = (FrameLayout) mContext.getLayoutInflater().inflate(
				R.layout.baseapp_layout, null);
		View view =  View.inflate(mContext, R.layout.page_ccr_newuser, null); 
		mPageView.addView(view);
		loadAllVariables();
	}

	@Override
	public void onDestory() {
		
	}
	
	@Override
	public void onResume() {
		
		processFocus();
		showTabBar();
		
		CacheManager cm = CacheManager.getInstance(mContext);
		mSupportBank = (ArrayList<CCRBankCardInfo>) (cm
				.getObject(Constant.CCR_SUPPORT_BANK_LIST));
		if(mSupportBank==null||mSupportBank.size()<=0){
			sendSupportBankRequest();
		}else{
        //取得银行信息
			setSupportBankList(mSupportBank);
			loadPaymentInfo();
		}
    	
	}

	private void processFocus() {
		View focus = mContext.getCurrentFocus();
        if (focus != null && focus instanceof EditText) {
           focus.clearFocus();// 要先清除之前的焦点，当再有焦点时，才会弹出键盘，
           ((EditText)focus).setSelection(((EditText)focus).length());
        }
		mPageView.invalidate();
		mPageView.requestFocus();
		if(mBankAccount != null){
			mBankAccount.requestFocus();
		}
	}
	
	private void showTabBar() {
		ArrayList<CCRBankCardInfo>  savedBank = BizCCRUtil.getSavedBank(mContext);
		if(savedBank==null||savedBank.size()<=0){
			titleShadow.setVisibility(View.VISIBLE);
			tabLayout.setVisibility(View.GONE);
		}
	}
	
	private void loadPaymentInfo() {
		currentCCRBankCardInfo = (CCRBankCardInfo)params;
		if(currentCCRBankCardInfo != null){
			startEvoke(currentCCRBankCardInfo);
		}
	}

	private void startEvoke(CCRBankCardInfo ccrBankCardInfo) {
		matchBankCard(ccrBankCardInfo);
		params = null;
	}

	private void matchBankCard(CCRBankCardInfo ccrBankCardInfo) {
		int i = 0;
		if(mSupportBank!=null && ccrBankCardInfo.getBankCardInfo() != null){
			for (CCRBankCardInfo bank : mSupportBank) {
				if (ccrBankCardInfo.getBankCardInfo().getBankMark() != null && ccrBankCardInfo.getBankCardInfo().getBankMark().equalsIgnoreCase(bank.getBankCardInfo().getBankMark())) {
					mBankName.setSelection(i);
					if(ccrBankCardInfo.getAmount() != null){
						mPayMoney.setText(ccrBankCardInfo.getAmount());
					}
					if(ccrBankCardInfo.getBankCardInfo().getUserName() != null){
						mUserName.setText(ccrBankCardInfo.getBankCardInfo().getUserName());
					}
					break;
				}
				i++;
			}
		}
	}

	/**
	 * 创建新信用卡还款订单
	 * 
	 * @param cacheKey
	 *            CCDC返回的信用卡key
	 * @param cardNumberType
	 *            卡号类型�?明文;1cacheKey;
	 */
	public void sendCCRRequest(String cacheKey,String cardNumberType){
		
		String banaccount = mBankAccount.getText();
		if (cardNumberType.equals(PLAIN_TEXT)) {
			banaccount = banaccount.replaceAll(" ", "");
		} else if (cardNumberType.equals(CACHE_KEY)) {
				banaccount = cacheKey;
		}
		
		int bankId = (int) mBankName.getSelectedItemId();
		mContext.setNowCardInfo(
				((CCRBankCardInfo) mSupportBank.get(bankId)).getBankCardInfo().getBankName(),
				((CCRBankCardInfo) mSupportBank.get(bankId)).getBankCardInfo().getBankMark(),
				mUserName.getText(),
				((CCRBankCardInfo) mSupportBank.get(bankId)).getRemindDate());
		HashMap<String, String> mapdata = BizCCRUtil.prepareNewUData(
				banaccount, mUserName.getText(),
				((CCRBankCardInfo) mSupportBank.get(bankId)).getBankCardInfo().getBankMark(),
				mPayMoney.getText(), cardNumberType,currentCCRBankCardInfo,mBankAccount.getText());
		
		mBizObj.sendHttpRequest(mContext,
				Constant.RQF_CCR_CREATE_CREDIT_CARD_TRADE_FOR_NEW, mapdata,
				AlipayHandlerMessageIDs.CCR_CREATE_CREDIT_CARD_TRADE_FOR_NEW);
		}
		 
	/**
	 * 发送获取支持信用卡银行请求
	 */
	private void sendSupportBankRequest(){
		String msg = mContext.getResources().getString(
				R.string.QrcodeTradeCreate);
		mContext.showProgressDialog(mContext, null, msg);
		HashMap<String, String> mapdata = BizCCRUtil
				.prepareGetBankData(Constant.RQF_CCR_QUERYBANK);
		mBizObj.sendHttpRequest(mContext, Constant.RQF_CCR_GETBANKS_TYPE,
				mapdata, AlipayHandlerMessageIDs.CCR_GET_SUPPORT_BANKLIST);
	}
	
	//设置支持银行列表
	public void setSupportBankList(ArrayList<CCRBankCardInfo> supportBank){
		BankNameAdapter bankAdapter = new BankNameAdapter(mContext, supportBank);
        mBankName.setAdapter(bankAdapter);
	}

	private void checkNextBtnEnable(){
		String bankAccount =  mBankAccount.getText();
		String username =  mUserName.getText();
		String money =  mPayMoney.getText();
		if (bankAccount.length() > 0 && username.length() > 0
				&& money.length() > 0) {
			mNextBtn.setEnabled(true);
		}else{
			mNextBtn.setEnabled(false);			
		}
		
	}

	//记录返回键日�?
	public boolean onKeyDown(int keyCode, KeyEvent event){
		if (keyCode == KeyEvent.KEYCODE_BACK){
			AlipayLogAgent.writeLog(mContext,BehaviourID.CLICKED, Constants.APPID_CCR, "-",Constants.VIEWID_CCR_NEW, Constants.SEEDID_BACKICON);
		}
		return false;
	}
	
	
	public void setRefurbishTYJImage(){
		if (mAlipayDataStore.getBoolean(mContext.getUserId(), false)
				&& mTiyanjin != null) {
			mTiyanjin.setVisibility(View.GONE);
		}
	}
}
