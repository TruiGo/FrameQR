package com.alipay.android.ui.card;

import java.net.URLEncoder;
import java.util.ArrayList;

import org.json.JSONObject;

import android.R.integer;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.StyleSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewStub;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.alipay.android.appHall.CacheManager;
import com.alipay.android.biz.CardManagerBiz;
import com.alipay.android.biz.CommonRespHandler;
import com.alipay.android.bizapp.CCR.CCRActivity;
import com.alipay.android.client.AlipayAccountManager;
import com.alipay.android.client.AlipayApplication;
import com.alipay.android.client.baseFunction.AlipayHelp;
import com.alipay.android.client.baseFunction.More;
import com.alipay.android.client.constant.Constant;
import com.alipay.android.client.util.Utilz;
import com.alipay.android.comon.component.ButtonList;
import com.alipay.android.comon.component.NumberView;
import com.alipay.android.comon.component.PatterAlertDialog;
import com.alipay.android.log.Constants;
import com.alipay.android.servicebeans.GetCardList;
import com.alipay.android.ui.bean.BankCardInfo;
import com.alipay.android.ui.bean.ButtonItem;
import com.alipay.android.ui.framework.BaseViewController;
import com.alipay.android.ui.framework.BaseViewController.BizAsyncTask;
import com.alipay.android.ui.withdraw.WithdrawRootControllerActivity;
import com.alipay.android.util.JsonConvert;
import com.alipay.b.a.b;
import com.eg.android.AlipayGphone.R;

public class CardDetailViewController extends BaseViewController {
	private int mDeleteType;
	private static final int DELETETYPE_UNKNOW = -1;
	private static final int DELETETYPE_KATONG = 0;
	private static final int DELETETYPE_KUAIJIE = 1;
	private static final int DELETETYPE_TIXIAN = 2;
	private static final int DELETETYPE_CCFUND = 3;
	
	@Override
	protected void onCreate() {
		super.onCreate();
		mView = LayoutInflater.from(getRootController()).inflate(R.layout.card_details, null, false);
		addView(mView, null);

		loadAllVariables();
		
		//查看银行卡详情埋点
		doSimpleLog(Constants.APPID_walletBankCard,
				Constants.BehaviourID.CLICKED, 
				Constants.VIEWID_BankCardDetails, 
				Constants.BANKCARDLIST, 
				Constants.Seed_SeeBankCard);
	}

	public static final String BANK_ICON_PARTH = "bank_mark/icon";
	public static final String BANK_NAME_PARTH = "bank_mark/name";
	private void loadAllVariables() {
		final BankCardInfo cardInfo = (BankCardInfo)params;
		
		TextView title = (TextView)findViewById(R.id.title_text);
		title.setText(R.string.express_card_detail);
		
		ViewStub cardDetailsStub = (ViewStub)findViewById(R.id.cardDetailsLayout);
		cardDetailsStub.setLayoutResource(cardInfo.isOwner() ? R.layout.express_card_gold : R.layout.express_card_silver);
		cardDetailsStub.inflate();
		
		ImageView bankLogoView = (ImageView)findViewById(R.id.BankCardIcon);
		bankLogoView.setImageBitmap(Utilz.getImage(getRootController(), BANK_ICON_PARTH, 
				cardInfo.getBankMark()));
		ImageView bankNameView = (ImageView)findViewById(R.id.BankCardName);
		bankNameView.setImageBitmap(Utilz.getImage(getRootController(), BANK_NAME_PARTH, 
				cardInfo.getBankMark()));
	
		NumberView cardNoTextView = (NumberView)findViewById(R.id.BankCardNumber);
		cardNoTextView.setText("**** **** **** " + cardInfo.getCardTailNumber());
		
		TextView cardTypeTextView = (TextView)findViewById(R.id.cardType);
		cardTypeTextView.setTypeface(Typeface.MONOSPACE,Typeface.ITALIC);
		cardTypeTextView.setText(BankCardInfo.CREDIT.equals(cardInfo.getCardType()) ? R.string.credit : R.string.debit);
		
		TextView holderNameTextView = (TextView)findViewById(R.id.holderNameText);
		holderNameTextView.setText(cardInfo.getUserName());

		if (Utilz.equalsAtBit(cardInfo.getSourceChannel(), BankCardInfo.CARDSOURCE_EXPRESS) ||
				Utilz.equalsAtBit(cardInfo.getSourceChannel(), BankCardInfo.CARDSOURCE_KATONG)) {
			View expressLogoView = findViewById(R.id.expressLogo);
			expressLogoView.setVisibility(View.VISIBLE);
//			TextView cardStatusTextView = (TextView)findViewById(R.id.cardExpressStatus);
//			cardStatusTextView.setText(String.format(getString(R.string.expressPay), getString(R.string.CardActiveMsg3)));
		}
		
		//TextView cardSignTimeTextView = (TextView)findViewById(R.id.cardSignedTime);
		//cardSignTimeTextView.setText(String.format(getString(R.string.apply_time), cardInfo.getApplyTime()));
		
		//TextView cardMobileTextView = (TextView)findViewById(R.id.cardMobileLinked);
		//cardMobileTextView.setText(String.format(getString(R.string.bankCarMobile), cardInfo.getBindMobile()));
	
		
		ButtonList actionListView = (ButtonList)findViewById(R.id.cardActionList);
		actionListView.setOnItemClickListener(new ButtonList.OnItemClickListener() {

			@Override
			public void onItemClick(View view, int position, int id) {
				switch((int)id) {
				case Constant.ACTION_WITHDRAW:
					Intent tIntent = new Intent(getRootController(),WithdrawRootControllerActivity.class);
		        	tIntent.putExtra(Constant.BANK_ID, cardInfo.getBankId());
		        	getRootController().startActivity(tIntent);
		        	
					//提现埋点
					doSimpleLog(Constants.APPID_walletBankCard,
							Constants.BehaviourID.SUBMITED, 
							Constants.VIEWID_WithdrawHome, 
							Constants.VIEWID_BankCardDetails, 
							Constants.Seed_WithdrawIcon);
					break;
				case Constant.ACTION_ADDMOBILE:
				case Constant.ACTION_EDITMOBILE:
					CardDetailViewController.this.getRootController().navigateTo("EditCardMobileView", params);
					
					//修改银行卡手机埋点
					doSimpleLog(Constants.APPID_walletBankCard,
							Constants.BehaviourID.SUBMITED, 
							Constants.VIEWID_ModifyBankPhoneHome, 
							Constants.VIEWID_BankCardDetails, 
							Constants.Seed_ModifyBankPhoneIcon);
					break;
				case Constant.ACTION_CHECKCARDLIMIT:
					CardDetailViewController.this.getRootController().navigateTo("CardLimitView", params);
					
					//查看限额埋点
					doSimpleLog(Constants.APPID_walletBankCard,
							Constants.BehaviourID.SUBMITED, 
							Constants.VIEWID_SeeLimitHome, 
							Constants.VIEWID_BankCardDetails, 
							Constants.Seed_SeeLimitIcon);

					break;
				case Constant.ACTION_CCFUND:
					Intent intent = new Intent(CardDetailViewController.this.getRootController(), CCRActivity.class);
					intent.putExtra(Constant.PARAM, cardInfo.serialization());
//					intent.putExtra(Constant.PARAM_KEY, Constant.CARDDATA_KEY);
//					
//					CacheManager.getInstance(
//							CardDetailViewController.this.getRootController()).putObject(Constant.CARDDATA_KEY, params);
					startActivity(intent);
					
					//还款埋点
					doSimpleLog(Constants.APPID_walletBankCard,
							Constants.BehaviourID.SUBMITED, 
							Constants.VIEWID_RepaymentHome, 
							Constants.VIEWID_BankCardDetails, 
							Constants.Seed_RepaymentIcon);
					break;
				case Constant.ACTION_SIGN:
					navigateTo("AddCardView", params);
					
					//已存卡签约为快捷埋点
					doSimpleLog(Constants.APPID_walletBankCard,
							Constants.BehaviourID.SUBMITED, 
							Constants.VIEWID_SignBankCardHome, 
							Constants.VIEWID_BankCardDetails, 
							Constants.Seed_SignBankCardIcon);
					break;
				default:
					break;
				}
			}

			
		});
		
		ArrayList<ButtonItem> buttonItemList = new ArrayList<ButtonItem>();
	
		int bizAvailable = cardInfo.getBizType();
		
		//信用卡还款
		if (Utilz.equalsAtBit(bizAvailable, BankCardInfo.BIZ_CREDITCARDFUND_NEW) || 
				Utilz.equalsAtBit(bizAvailable, BankCardInfo.BIZ_CREDITCARDFUND_OLD)) {
			buttonItemList.add(new ButtonItem().setAction(Constant.ACTION_CCFUND)
					.setText(getString(R.string.CreditCardRepay)));
		}
		
		//提现
		if (Utilz.equalsAtBit(bizAvailable, BankCardInfo.BIZ_WIDTHDRAW_INSTANT) ||
				Utilz.equalsAtBit(bizAvailable, BankCardInfo.BIZ_WIDTHDRAW_NORMAL)) {
				
			buttonItemList.add(new ButtonItem().setAction(Constant.ACTION_WITHDRAW)
					.setText(getString(R.string.WithDrawal)));
		}
		
		//开通快捷支付
		if (Utilz.equalsAtBit(bizAvailable, BankCardInfo.BIZ_EXPRESS_SIGNABLE)) {
			buttonItemList.add(new ButtonItem().setAction(Constant.ACTION_SIGN)
					.setText(getString(R.string.express_open)));
		}
		
		//手机付款限额
		if (Utilz.equalsAtBit(bizAvailable, BankCardInfo.BIZ_EXPRESS_CARDLIMIT_ACCESSABLE)) {
			buttonItemList.add(new ButtonItem().setAction(Constant.ACTION_CHECKCARDLIMIT)
					.setText(getString(R.string.express_limit_mobile)));
		}
		
		//修改银行卡手机／绑定银行卡手机（本期暂不实现）
//		if (Utilz.equalsAtBit(bizAvailable, BankCardInfo.BIZ_EXPRESS_MOBILE_EMPTY)) {
//			buttonItemList.add(new ButtonItem().setAction(Constant.ACTION_ADDMOBILE)
//					.setText(getString(R.string.addMobileNum)));
//		} else if (Utilz.equalsAtBit(bizAvailable, BankCardInfo.BIZ_EXPRESS_MOBILE_EDITABLE)) {
//			buttonItemList.add(new ButtonItem().setAction(Constant.ACTION_EDITMOBILE)
//					.setText(getString(R.string.modifyMobileNum))
//					.setTextExtra(cardInfo.getBindMobile()));
//		}

		mDeleteType = DELETETYPE_UNKNOW;
		if (Utilz.equalsAtBit(bizAvailable, BankCardInfo.BIZ_EXPRESS_DELETEKATONG)) {
			mDeleteType = DELETETYPE_KATONG;
		} else if (Utilz.equalsAtBit(bizAvailable, BankCardInfo.BIZ_EXPRESS_DELETEKUAIJIE)) {
			mDeleteType = DELETETYPE_KUAIJIE;
		} else if (Utilz.equalsAtBit(bizAvailable, BankCardInfo.BIZ_EXPRESS_DELETETIXIAN)) {
			mDeleteType = DELETETYPE_TIXIAN;
		}
		//本期暂时不做信用卡删除
		/* else if (Utilz.equalsAtBit(bizAvailable, BankCardInfo.BIZ_EXPRESS_DELETEFROMCCFUNDLIST)) {
			mDeleteType = DELETETYPE_CCFUND;
		}*/
		
		if (DELETETYPE_UNKNOW != mDeleteType) {	
			ImageButton deleteButton = (ImageButton)findViewById(R.id.title_right);
			deleteButton.setVisibility(View.VISIBLE);
			deleteButton.setBackgroundResource(R.drawable.title_delete_icon);
			deleteButton.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					//删除埋点
					doSimpleLog(Constants.APPID_walletBankCard,
							Constants.BehaviourID.CLICKED, 
							Constants.VIEWID_NoneView, 
							Constants.VIEWID_BankCardDetails, 
							Constants.Seed_DelBankCardIcon);
					
					switch (mDeleteType) {
					case DELETETYPE_KUAIJIE:
						CardDetailViewController.this.getRootController().navigateTo("DeleteCardView", params);
						break;
					case DELETETYPE_KATONG:
//				        Intent tIntent = new Intent(getRootController(), AlipayHelp.class);
//				        tIntent.putExtra(AlipayHelp.INDEX, Constant.HELP_INDEX_DELETEKATONG);
//				        startActivity(tIntent);
						alert(getString(R.string.express_delete_katong));
						break;
					case DELETETYPE_TIXIAN:
					case DELETETYPE_CCFUND:
		            	showDialog(R.drawable.infoicon, null, 
		            	getString(R.string.express_deletethiscard), getString(R.string.Ensure), new DialogInterface.OnClickListener() {
							@Override
							public void onClick(DialogInterface dialog,
									int which) {
								deleteCardWithoutPWD(DELETETYPE_TIXIAN == mDeleteType ?
										Constant.ACTIONTYPE_DELETECIF : Constant.ACTIONTYPE_DELETECCFUND);
							}
						}, getString(R.string.Cancel), null, null, null);
						break;
					}				
				}
			});
		}
		
		actionListView.setData(buttonItemList);

	}
	
	private void deleteCardWithoutPWD(String actionType){
		new BizAsyncTask(CardManagerBiz.BIZTAG_DELETECARDWITHOUTPWD, true).execute(actionType);
	}
	
	/**
	 * 异步任务
	 */
	@Override
	protected Object doInBackground(String bizType, String... paramsList) {
		if(bizType.equalsIgnoreCase(CardManagerBiz.BIZTAG_DELETECARDWITHOUTPWD)){
			if (0 < paramsList.length) {
				final BankCardInfo cardInfo = (BankCardInfo)params;
				return new CardManagerBiz().deleteCardWithoutPWD(cardInfo.getBankId(), 
						cardInfo.getCardType(), cardInfo.getBankName(), paramsList[0]);
			}
       	}
		
		return super.doInBackground(bizType, paramsList);
	}
	
	@Override
	protected void onPostExecute(String bizType, Object result) {
		JSONObject responseJson = JsonConvert.convertString2Json((String)result);

    	if(!CommonRespHandler.processSpecStatu(getRootController(), responseJson)){
			return;
		}
		if(CommonRespHandler.filterBizResponse(getRootController(), responseJson)){
			if (bizType.equalsIgnoreCase(CardManagerBiz.BIZTAG_DELETECARDWITHOUTPWD)) {
				
				new PatterAlertDialog(getRootController()).show(
						getString(R.string.express_delete_success)
						/*responseJson.optString(
						Constant.RPF_MEMO)*/, R.drawable.pattern_success, 
						new PatterAlertDialog.OnDismissListener() {

					@Override
					public void onDismiss() {
                		AlipayApplication application = (AlipayApplication) getRootController()
                				.getApplicationContext();
                		application.setBankListRefresh(true);
                		
                		//刷新卡数量
                		getRootController().getUserData().resetStatus();
                		
                    	pop();
					}
					
				});
			}
		}
	}
}
