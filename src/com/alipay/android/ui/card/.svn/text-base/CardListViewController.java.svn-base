package com.alipay.android.ui.card;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import com.alipay.android.appHall.CacheManager;
import com.alipay.android.biz.CardManagerBiz;
import com.alipay.android.biz.CommonRespHandler;
import com.alipay.android.client.AlipayApplication;
import com.alipay.android.client.constant.Constant;
import com.alipay.android.log.AlipayLogAgent;
import com.alipay.android.log.Constants;
import com.alipay.android.log.Constants.BehaviourID;
import com.alipay.android.log.StorageStateInfo;
import com.alipay.android.servicebeans.GetCardList;
import com.alipay.android.ui.adapter.CardListAdapter;
import com.alipay.android.ui.bean.BankCardInfo;
import com.alipay.android.ui.framework.BaseViewController;
import com.alipay.android.util.JsonConvert;
import com.eg.android.AlipayGphone.R;
/**
 * 银行卡列表界面
 * @author caidie.wang
 *
 */
public class CardListViewController extends BaseViewController{
	
	private ListView bankList = null;//银行卡列表
	private ArrayList<BankCardInfo> bankCardInfos = null;
	private TextView emptyView = null;//列表为空
	protected StorageStateInfo storageStateInfo;
	private CacheManager cacheManager;
	private AlipayApplication application;

	@Override
	protected void onCreate() {
		super.onCreate();
		mView = LayoutInflater.from(getRootController()).inflate(R.layout.card_list, null, false);
		addView(mView, null);
		bankCardInfos = new ArrayList<BankCardInfo>();
		storageStateInfo = StorageStateInfo.getInstance();
		cacheManager = CacheManager.getInstance(getRootController());
		application = (AlipayApplication) getRootController().getApplicationContext();
		
		loadAllVariables();
		loadCacheCardList();
	}
	private void loadAllVariables() {
		TextView title = (TextView)findViewById(R.id.title_text);
		title.setText(R.string.bank_card);
		
		bankList = (ListView)findViewById(R.id.CardList);
		bankList.setOnItemClickListener(new AdapterView.OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				BankCardInfo itemBankCardInfo = (BankCardInfo)bankCardInfos.get(position);
				if (true/*null != itemBankCardInfo && 
						BankCardInfo.BIZ_EXPRESS_DETAILS_ACCESSABLE == itemBankCardInfo.getBizType()*/)
				CardListViewController.this.getRootController().navigateTo("CardDetailView", itemBankCardInfo);
			}
		});
		emptyView  = (TextView)findViewById(R.id.CardListEmpty);
		
		TextView addCardText = (TextView)findViewById(R.id.AddCard);
		addCardText.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				getRootController().navigateTo("AddCardView");
				
				//添加快捷银行卡埋点
				doSimpleLog(Constants.APPID_walletBankCard,
						Constants.BehaviourID.CLICKED, 
						Constants.VIEWID_InputCardView, 
						Constants.BANKCARDLIST, 
						Constants.Seed_AddBankCardIcon);
			}
		});
	}
	@Override
	protected void onResume() {
		super.onResume();
		JSONArray cardList = getSavedCardList();
		if(cardList == null || application.isBankListRefresh()){
			getCardList();
		}
	}
	/**
	 * 加载缓存的卡列表数据
	 */
	private void loadCacheCardList(){
		JSONArray cardList = getSavedCardList();
		if(cardList != null && !application.isBankListRefresh()){
			loadCardList(cardList);
		}
	}
	/**
	 * 银行卡列表
	 */
	private void getCardList() {
		new BizAsyncTask(GetCardList.BIZ_TAG,true).execute();
	}
	
	@Override
	protected Object doInBackground(String bizType, String... params) {
		if(bizType.equalsIgnoreCase(GetCardList.BIZ_TAG)){
			return new CardManagerBiz().getCardList();
		}
		return super.doInBackground(bizType, params);
	}
	
	@Override
	protected void onPostExecute(String bizType, Object result) {
		if(bizType.equalsIgnoreCase(GetCardList.BIZ_TAG)){
			JSONObject response = JsonConvert.convertString2Json((String)result);
			if(!CommonRespHandler.processSpecStatu(getRootController(), response)){
				return;
			}
			if(CommonRespHandler.filterBizResponse(getRootController(), response)){
				application.setBankListRefresh(false);
				processGetListCardResponse(response);
			}
		}
		super.onPostExecute(bizType, result);
	}
	/**
	 * 处理返回的银行列表数据
	 * @param response
	 */
	private void processGetListCardResponse(JSONObject response){
		try {
			JSONArray cardList =  response.getJSONArray(Constant.CARD_LIST);
			savedCardList(cardList);
			loadCardList(cardList);
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}
	/**
	 * 加载银行卡列表
	 * @param cardList
	 * @throws JSONException
	 */
	private void loadCardList(JSONArray cardList){
		bankCardInfos.clear();
		try {
			if(cardList != null && cardList.length()>0){
				JSONObject currentBankListItem = null;
				BankCardInfo currentBankListItemInfo = null;
				for(int i=0;i<cardList.length();i++){
					currentBankListItem = cardList.getJSONObject(i);
					currentBankListItemInfo = new BankCardInfo().processBankCardInfoJson(currentBankListItem);
					bankCardInfos.add(currentBankListItemInfo);
				}
			}
			setCardListAdapter(bankCardInfos);
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}
	/**
	 * 缓存银行卡列表
	 * @param cardList
	 */
	private void savedCardList(JSONArray cardList){
		cacheManager.putObject(Constant.CARD_LIST, cardList);
	}
	/**
	 * 获取缓存的银行卡列表
	 * @return
	 */
	private JSONArray getSavedCardList(){
		return (JSONArray)cacheManager.getObject(Constant.CARD_LIST);
	}
	/**
	 * 给List设置适配器
	 * @param cardList
	 */
	private void setCardListAdapter(ArrayList<BankCardInfo> cardList){
		if(cardList.size() == 0){
			emptyView.setVisibility(View.VISIBLE);
			bankList.setEmptyView(emptyView);
			return;
		}
		CardListAdapter cardListAdapter = new CardListAdapter(getRootController(), cardList);
		bankList.setAdapter(cardListAdapter);
	}
	
	public boolean onKeyDown(int keyCode, android.view.KeyEvent event) {
		if(keyCode == KeyEvent.KEYCODE_BACK){
			AlipayLogAgent.writeLog(getRootController(), 
					BehaviourID.CLICKED,
					Constants.WALLE_BANK_CARD, 
					"-", 
					Constants.BANK_CARD_LIST,Constants.BACK_ICON);
		}
		
		return super.onKeyDown(keyCode, event);
	}

}
