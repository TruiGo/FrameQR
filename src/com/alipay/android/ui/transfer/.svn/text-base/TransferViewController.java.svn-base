package com.alipay.android.ui.transfer;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Observable;
import java.util.Observer;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.content.Intent;
import android.graphics.BitmapFactory.Options;
import android.os.AsyncTask;
import android.os.Build;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.HeaderViewListAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.alipay.android.appHall.Helper;
import com.alipay.android.appHall.appManager.HallData;
import com.alipay.android.appHall.common.CacheSet;
import com.alipay.android.biz.CommonRespHandler;
import com.alipay.android.biz.TransferBiz;
import com.alipay.android.client.AlipayApplication;
import com.alipay.android.client.constant.Constant;
import com.alipay.android.client.util.AlipayDataStore;
import com.alipay.android.common.data.UserData;
import com.alipay.android.comon.component.CommonGallery;
import com.alipay.android.comon.component.PullRefreshView;
import com.alipay.android.comon.component.SelectionBar;
import com.alipay.android.comon.component.SelectionBar.LetterSelectedListener;
import com.alipay.android.dao.ContactPersonDAO;
import com.alipay.android.dao.TransferReceiverDAO;
import com.alipay.android.dataprovider.ContactProvider;
import com.alipay.android.log.AlipayLogAgent;
import com.alipay.android.log.Constants;
import com.alipay.android.servicebeans.GetTransferList;
import com.alipay.android.ui.adapter.ContactListAdapter;
import com.alipay.android.ui.adapter.HistoryListAdapter;
import com.alipay.android.ui.bean.ContactPerson;
import com.alipay.android.ui.bean.TransferReceiver;
import com.alipay.android.ui.fragment.FragmentHelper;
import com.alipay.android.ui.fragment.MultiPhoneNumPop;
import com.alipay.android.ui.framework.BaseViewController;
import com.alipay.android.ui.guide.AppGuide;
import com.alipay.android.ui.guide.GuideListener;
import com.alipay.android.util.JsonConvert;
import com.eg.android.AlipayGphone.R;

public class TransferViewController extends BaseViewController implements OnClickListener,LetterSelectedListener,GuideListener,
				OnTouchListener,Observer{
	private ListView mContactListView;
	private ListView mHistoryListView;
	private EditText mSearchInput;
//	private Button mAddContactButton;
	private ContactListAdapter contactListAdapter;
	private HistoryListAdapter historyListAdapter;
	private SelectionBar letterSelectionBar;
	private Button recentContactButton;
	private Button receiverButton;
	private RelativeLayout mLocalContactCanvas;
	private RelativeLayout mHistoryCountView;
	private View headerView;
	private PullRefreshView mHistoryContactCanvas;
	private LinearLayout emptyLoacalView;
	
	private TextView localLoadingText;
	private TextView mHistoryCount;
	private RelativeLayout historyLoadingView;
	
	private Map<Character, Integer> alphaIndex = new HashMap<Character, Integer>();
	private ContactPerson contactClicked;
	private TransferReceiver transferReceiver;
	private ContactProvider contactProvider = ContactProvider.getInstance();
	private final int SEARCHCONTACT_REQUEST = 0;
	private BizAsyncTask historyLoadTask;
	String GUIDE_PATH = "transfer_guide";
    private CommonGallery mGuideGallery;// 向导Guide页面
    private AppGuide appGuide;
    private AlipayApplication application ;
    private boolean mPullRefresh;
//    private String userId;
    private UserData userData;
	
	private ContactPersonDAO contactPersonDAO;
	private static final String LOADCONTACTTASK = "";
	private FragmentHelper fragmentHelper;
	private Options options;
	private ImageView mGuideView;
//	private LinearLayout mFilterLayout;
	private ImageView titleRight;
	
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if(keyCode == KeyEvent.KEYCODE_BACK){
			getRootController().finish();
			return true;
		}
		
		return super.onKeyDown(keyCode, event);
	}
	
	@Override
	protected void onCreate() {
		mView = LayoutInflater.from(this.getRootController()).inflate(R.layout.transfer_index, null, false);  
        addView(mView,null);
        
        application = (AlipayApplication) getRootController().getApplicationContext();
        contactProvider.addObserver(this);
        
        loadAllVariables();
        initListener();
        
        if(appGuide.needShowGuide()){
        	appGuide.initGuide(mGuideGallery);
        	appGuide.setOnCompleteListener(this);
        }else if(userData != null && recentContactButton.isSelected()){
			loadHistory();
			logLaunch();
        }
        
        if(!contactProvider.isLoading() && contactProvider.getContactData().size() == 0){
        	new BizAsyncTask(LOADCONTACTTASK,false).execute();
        }
        loadLocalContact(contactProvider.getContactData());
	}
	
	private void logLaunch(){
		String mPkgId = "09999988";
		List<String> sortedIds = Arrays.asList(HallData.sordedIdArray);
		int i = sortedIds.indexOf(mPkgId);
		if (sortedIds.contains(mPkgId)) {
			AlipayLogAgent.writeLog(getRootController(), Constants.BehaviourID.BIZLAUNCHED, null, null, mPkgId, null, mPkgId+"Home", Constants.WALLETHOME,
					"homeApp" + (i + 1) + "Icon", null);
		} else {
			AlipayLogAgent.writeLog(getRootController(), Constants.BehaviourID.BIZLAUNCHED, null, null, mPkgId, null, mPkgId+"Home", Constants.APPCENTER,
					mPkgId + "Icon");
		}
	}
	
	@Override
	protected void onResume() {
		super.onResume();
		userData = getRootController().getUserData();
		
		//调用安全支付后需要刷新界面
		if(userData != null && application.isSafePayCalled() && recentContactButton.isSelected()){
			sendLoadHistReq();
		}
	}

	/**
	 * 加载历史联系人
	 * 1. 优先从本地加载历史联系人
	 * 2. 调用过安全支付或本地无历史联系人，从服务器获取
	 */
	private void loadHistory() {
		if(!application.isSafePayCalled()){
			List<TransferReceiver> transferReceivers = null;
			try {
				transferReceivers = loadFromLocal();
			} catch (JSONException e) {
				e.printStackTrace();
			}
			if((transferReceivers != null && transferReceivers.size() > 0))
				return;
		}
		sendLoadHistReq();
	}

	/**
	 * 读取本地历史联系人
	 * @return
	 * @throws JSONException
	 */
	private List<TransferReceiver> loadFromLocal() throws JSONException {
		String userId = (userData == null) ? "" : userData.getUserId();
		String historyListStr = CacheSet.getInstance(getRootController()).getString(userId);
		List<TransferReceiver> transferReceivers = null;
		if(!"".equals(historyListStr)){
			transferReceivers = new TransferReceiverDAO().convertJson2List(new JSONArray(historyListStr));
			historyListAdapter.setDataForRefresh(transferReceivers);
			setTotalCount(transferReceivers.size());
			application.setFirstInTransfer(false);
		}
		return transferReceivers;
	}

	/**
	 * 从服务器获取历史联系人
	 */
	private void sendLoadHistReq() {
		if(historyLoadTask != null && historyLoadTask.getStatus() != AsyncTask.Status.FINISHED)
			historyLoadTask.cancel(true);
		
		historyLoadTask = new BizAsyncTask(GetTransferList.BIZ_TAG,false);
		historyLoadTask.execute();
	}
	
	/**
	 * load界面上的View
	 */
	private void loadAllVariables() {
		options = new Options();
        options.inDensity =(int) Helper.getDensityDpi(getRootController());
        options.inScaled = true;
        
        TextView title = (TextView) findViewById(R.id.title_text);
		title.setText(R.string.select_receiver);

		titleRight = (ImageButton) findViewById(R.id.title_right);
		titleRight.setVisibility(View.VISIBLE);
		titleRight.setImageResource(R.drawable.add_contact);
		titleRight.setOnClickListener(this);
		
		ImageView shadow = (ImageView)findViewById(R.id.TitleShadow);
		shadow.setVisibility(View.GONE);
        
		fragmentHelper = new FragmentHelper(getRootController());
		appGuide = new AppGuide(getRootController(), GUIDE_PATH,false,AlipayDataStore.TRANSFERVERSION);
//		if(getRootController().getUserData() != null)
//			userId = getRootController().getUserData();
		userData = getRootController().getUserData();
        mGuideGallery = (CommonGallery) findViewById(R.id.guideGallery);
		recentContactButton = (Button) findViewById(R.id.recent_contact_tab);
		recentContactButton.setSelected(true);
		receiverButton = (Button) findViewById(R.id.receiver_tab);
		contactPersonDAO = new ContactPersonDAO();
		
//		mAddContactButton = (Button) findViewById(R.id.add_contact_button);
		letterSelectionBar = (SelectionBar) findViewById(R.id.letters_selection_bar);
		
		historyLoadingView = (RelativeLayout) findViewById(R.id.historyLoadingView);
		localLoadingText = (TextView) findViewById(R.id.loading_local_text);
		mHistoryCount = (TextView) findViewById(R.id.historyCount);
		mLocalContactCanvas = (RelativeLayout) findViewById(R.id.localContactCanvas);
		mHistoryCountView = (RelativeLayout) findViewById(R.id.historyCountView);
		emptyLoacalView = (LinearLayout) findViewById(R.id.emptyLocalCanvas);
		
		mHistoryContactCanvas = (PullRefreshView) findViewById(R.id.historyContactCanvas);
        mHistoryContactCanvas.setRefreshListener(mRefreshListener);
        mHistoryContactCanvas.setEnablePull(true);
//        mFilterLayout = (LinearLayout) mView.findViewById(R.id.filter);
        
        
        mGuideView = (ImageView) findViewById(R.id.guideView);
        if(fragmentHelper.needShow(AlipayDataStore.TRANSFERINDEX)){
			mGuideView.setBackgroundResource(R.drawable.transfer_flow_01);
			mGuideView.setScaleType(ScaleType.FIT_XY);
			
			mGuideView.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					if(contactListAdapter.getCount() > 0 || historyListAdapter.getCount() > 0){
						if(receiverButton.isSelected())
							mContactListView.setSelection(1);
						
						mGuideView.setBackgroundResource(R.drawable.transfer_flow_02);
						mGuideView.setOnClickListener(new View.OnClickListener() {
							@Override
							public void onClick(View v) {
								dismissGuide();
							}
						});
					}else{
						dismissGuide();
					}
				}
			});
			mGuideView.setVisibility(View.VISIBLE);
        }
        
        initListView();
	}
	
	private void dismissGuide() {
		mGuideView.setVisibility(View.GONE);
		fragmentHelper.hideGuide(AlipayDataStore.TRANSFERINDEX);
		if (Integer.valueOf(Build.VERSION.SDK).intValue() >= 8){ 
			mContactListView.smoothScrollToPosition(0);
		}
	}
	
	private PullRefreshView.RefreshListener mRefreshListener = new PullRefreshView.RefreshListener() {
        @Override
        public void onRefresh() {
            mPullRefresh = true;
            sendLoadHistReq();
        }
    };
	
	/**
	 * 初始化监听器
	 */
	private void initListener() {
		recentContactButton.setOnClickListener(this);
		receiverButton.setOnClickListener(this);
//		mAddContactButton.setOnClickListener(this);
		letterSelectionBar.setOnLetterSelectedListener(this);
		
		mContactListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view,int position, long id) {
				HeaderViewListAdapter headerListAdapter = (HeaderViewListAdapter) parent.getAdapter();
				if(position == 0){
					getRootController().startActivityForResult(new Intent(getRootController(), SearchContactActivity.class), SEARCHCONTACT_REQUEST);
					return;
				}
				
				contactClicked = ((ContactListAdapter)headerListAdapter.getWrappedAdapter()).getItem(position -1);

				if(contactClicked.phoneNumber.size() > 1){
					showContactPop(contactClicked);
				}else{
					transferReceiver = new TransferReceiver();
					transferReceiver.recvName = contactClicked.displayName;
					transferReceiver.recvMobile = contactClicked.phoneNumber.get(0);
					transferReceiver.transferType = TransferType.FROMCONTACT;
					getRootController().navigateTo("ConfirmView", transferReceiver);
				}
			}
		});
		
		mSearchInput.setOnClickListener(this);
		mContactListView.setOnTouchListener(this);
		mHistoryListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				TransferReceiver receiver = historyListAdapter.getItem(position);
				receiver.transferType = TransferType.FROMHISTORY;
				if(Constant.TRADE.equals(receiver.bizType)){
					if(receiver.recvAccount != null && receiver.recvAccount.matches("1\\d{10}")){
						transfer2Phone(receiver);
					}else
						getRootController().navigateTo("ConfirmView", receiver);
				}else
					transfer2Phone(receiver);
			}

			private void transfer2Phone(TransferReceiver receiver) {
				transferReceiver = new TransferReceiver();
				transferReceiver.recvName = receiver.recvName;
				transferReceiver.recvMobile = receiver.recvMobile;
				transferReceiver.recvRealName = receiver.recvRealName;
				transferReceiver.recvAccount = receiver.recvAccount;
				transferReceiver.transferType = receiver.transferType;
				getRootController().navigateTo("ConfirmView", transferReceiver);
			}
		});
	}
     
	/**
	 * 异步任务
	 */
	@Override
	protected Object doInBackground(String bizType, String... params) {
		if(bizType.equalsIgnoreCase(GetTransferList.BIZ_TAG)){
        	return new TransferBiz().loadTransferList();
        }else if(bizType.equalsIgnoreCase(LOADCONTACTTASK)){
        	contactPersonDAO.loadAllContacts(getRootController());
        }
		return super.doInBackground(bizType, params);
	}
	
	/**
	 * 异步任务返回的结果处理
	 */
	protected void onPostExecute(String bizType, Object result) {
		if(bizType.equalsIgnoreCase(GetTransferList.BIZ_TAG)){//获得转账历史
			historyLoadingView.setVisibility(View.GONE);
			mHistoryListView.setVisibility(View.VISIBLE);
			
			application.setSafePayCalled(false);
        	if (mPullRefresh) {
                mHistoryContactCanvas.refreshFinished();
                mPullRefresh = false;
            }
        	
        	JSONObject responseJson = JsonConvert.convertString2Json((String)result);
        	if(!CommonRespHandler.processSpecStatu(getRootController(), responseJson)){
				return;
			}
			if(CommonRespHandler.filterBizResponse(getRootController(), responseJson)){
				JSONArray transferRecordList = responseJson.optJSONArray(Constant.TRANSFERRECORDLIST);
    			populateHistoryListView(transferRecordList);
			}
        }
		super.onPostExecute(bizType, result);
	}

	/**
	 * 填充主界面联系人ListView
	 * @param result 从数据库中load出的联系人
	 */
	private void initListView() {
		mContactListView = (ListView) findViewById(R.id.contacterList);
		mHistoryListView = (ListView) findViewById(R.id.historyListView);
		headerView = LayoutInflater.from(getRootController()).inflate(R.layout.contact_search_box, null);
		mContactListView.addHeaderView(headerView);
		
		historyListAdapter = new HistoryListAdapter(getRootController());
		mHistoryListView.setAdapter(historyListAdapter);
		
		mSearchInput = (EditText) findViewById(R.id.searchEditText);
		contactListAdapter = new ContactListAdapter(getRootController());
		mContactListView.setAdapter(contactListAdapter);
	}
	
	/**
	 * 历史记录填充ListView
	 * @param historyRecordList
	 */
	private void populateHistoryListView(JSONArray historyRecordList){
		if(recentContactButton.isSelected())
			showHistoryList();
		if(recentContactButton.isSelected())
			mHistoryCountView.setVisibility(View.VISIBLE);
		
		if(historyRecordList!= null && historyRecordList.length() > 0){
			setTotalCount(historyRecordList.length());
			historyListAdapter.setDataForRefresh(new TransferReceiverDAO().convertJson2List(historyRecordList));
			//缓存数据
			if(userData != null)
				CacheSet.getInstance(getRootController()).putString(userData.getUserId(), historyRecordList.toString());
		}else{
			setTotalCount(0);
			historyListAdapter.setDataForRefresh(null);
			if(application.isFirstTransfer()){
//				mFilterLayout.setBackgroundResource(R.drawable.tab_right);
				application.setFirstInTransfer(false);
				showLocalContact(contactProvider.getContactData());
			}
		}
	}
	
	private void setTotalCount(int totalCount){
		String totalCountStr = getRootController().getText(R.string.total_recent_receiver) + "";
		mHistoryCount.setText(totalCountStr.replace("$s$", totalCount+""));
	}

	@Override
	public void onClick(View v) {
//		StorageStateInfo storageStateInfo = StorageStateInfo.getInstance();
		switch (v.getId()) {
		case R.id.title_right:
			getRootController().navigateTo("NewReceiverView");
			/*AlipayLogAgent.onEvent(getRootController(),
					Constants.MONITORPOINT_EVENT, 
					"Y", 
					"", 
					"09999988",
					"1.0",
					storageStateInfo.getValue(Constants.STORAGE_PRODUCTID), 
					getRootController().getUserId(), 
					storageStateInfo.getValue(Constants.STORAGE_PRODUCTVERSION), 
					storageStateInfo.getValue(Constants.STORAGE_CLIENTID),
					Constants.EVENTTYPE_GOTONEWTRANSFERPAGE);*/
			break;
		case R.id.recent_contact_tab:
//			mFilterLayout.setBackgroundResource(R.drawable.tab_left);
			recentContactButton.setSelected(true);
			receiverButton.setSelected(false);
			application.setFirstInTransfer(false);
			showHistoryList();
			
			try {
				if(!application.isSafePayCalled())//读取本地最近联系人
					loadFromLocal();
				else//刷新最近联系人
					sendLoadHistReq();
			} catch (JSONException e) {
				e.printStackTrace();
			}
			mHistoryCountView.setVisibility(View.VISIBLE);
			
			/*AlipayLogAgent.onEvent(getRootController(), 
					Constants.MONITORPOINT_EVENT, 
					"Y", 
					"", 
					"09999988",
					"1.0",
					storageStateInfo.getValue(Constants.STORAGE_PRODUCTID), 
					getRootController().getUserId(), 
					storageStateInfo.getValue(Constants.STORAGE_PRODUCTVERSION), 
					storageStateInfo.getValue(Constants.STORAGE_CLIENTID),
					Constants.EVENTTYPE_HISTORYCONTACTBUTTONCLICK);*/
			break;
		case R.id.receiver_tab:
//			mFilterLayout.setBackgroundResource(R.drawable.tab_right);
			recentContactButton.setSelected(false);
			receiverButton.setSelected(true);
			application.setFirstInTransfer(false);
			showLocalContact(contactProvider.getContactData());
			
			/*AlipayLogAgent.onEvent(getRootController(), 
					Constants.MONITORPOINT_EVENT, 
					"Y",
					"",
					"09999988",
					"1.0",
					storageStateInfo.getValue(Constants.STORAGE_PRODUCTID), 
					getRootController().getUserId(), 
					storageStateInfo.getValue(Constants.STORAGE_PRODUCTVERSION), 
					storageStateInfo.getValue(Constants.STORAGE_CLIENTID),
					Constants.EVENTTYPE_LOCALCONTACTBUTTONCLICK);*/
			break;
		case R.id.searchEditText:
			getRootController().startActivityForResult(new Intent(getRootController(), SearchContactActivity.class), SEARCHCONTACT_REQUEST);
			break;
		default:
			break;
		}
	}

	private void showLocalContact(List<ContactPerson> contactPersons) {
		receiverButton.setSelected(true);
		recentContactButton.setSelected(false);
		showContactList();
	}

	private synchronized void loadLocalContact(List<ContactPerson> contactPersons) {
		if(!contactProvider.loadingFinish()){//正在加载时显示loading
			showLoadingView();
			return;
		}
		
		localLoadingText.setVisibility(View.GONE);
		mContactListView.setVisibility(View.VISIBLE);
		if(contactProvider.loadingFinish() && contactPersons.size() == 0){//加载完成显示无联系人
			mContactListView.setEmptyView(emptyLoacalView);
			letterSelectionBar.setVisibility(View.GONE);
		}else{//正常显示
			letterSelectionBar.initAlphaIndex(contactPersons,alphaIndex);
			letterSelectionBar.setVisibility(View.VISIBLE);
			contactListAdapter.refreshUI(contactPersons);
		}
	}

	private void showLoadingView() {
		localLoadingText.setVisibility(View.VISIBLE);
		mContactListView.setVisibility(View.GONE);
		letterSelectionBar.setVisibility(View.GONE);
	}

	/**
	 * 滑动条监听事件
	 */
	@Override
	public void onLetterSelected(char selectedChar) {
		if(alphaIndex.get(selectedChar) != null){
			mContactListView.setSelection(alphaIndex.get(selectedChar));
		}
	}
	
	/**
	 * 联系人存在多个号码时的弹出框
	 * @param contactPerson 多号码联系人
	 */
	private void showContactPop(final ContactPerson contactPerson){
		final MultiPhoneNumPop multiPhoneNumPop = new MultiPhoneNumPop(getRootController());
		multiPhoneNumPop.showPop(contactPerson, new AdapterView.OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				transferReceiver = new TransferReceiver();
				transferReceiver.recvName = contactPerson.displayName;
				transferReceiver.recvMobile = contactPerson.phoneNumber.get(position);
				transferReceiver.transferType = TransferType.FROMCONTACT;
				getRootController().navigateTo("ConfirmView", transferReceiver);
				multiPhoneNumPop.dismissPop();
			}
		});
	}
	
	@Override
	protected void onPreDoInbackgroud(String bizType) {
		if(GetTransferList.BIZ_TAG.equals(bizType)){
			mHistoryContactCanvas.setLoadingViewVisiable(false);
			historyLoadingView.setVisibility(View.VISIBLE);
			mHistoryListView.setVisibility(View.INVISIBLE);
			
			if (mHistoryListView.getEmptyView() != null) {
				mHistoryListView.getEmptyView().setVisibility(View.GONE);
				mHistoryListView.setEmptyView(null);
	        }
		}
	}
	
	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		if(requestCode == SEARCHCONTACT_REQUEST){
			if(resultCode == Activity.RESULT_OK){
				transferReceiver = data.getParcelableExtra("transformReceiver");
				transferReceiver.transferType = TransferType.FROMCONTACT;
				getRootController().navigateTo("ConfirmView", transferReceiver);
			}
		}else if(requestCode != SEARCHCONTACT_REQUEST && resultCode == Activity.RESULT_CANCELED){
			getRootController().finish();
		}else if(requestCode == Constant.REQUEST_LOGIN_BACK && resultCode == Activity.RESULT_OK){
			loadHistory();
			logLaunch();
		}
		
		super.onActivityResult(requestCode, resultCode, data);
	}
	
	private void showContactList(){
		mHistoryCountView.setVisibility(View.GONE);
		mHistoryContactCanvas.setVisibility(View.GONE);
		mLocalContactCanvas.setVisibility(View.VISIBLE);
	}
	
	private void showHistoryList(){
		receiverButton.setSelected(false);
		recentContactButton.setSelected(true);
		mHistoryContactCanvas.setVisibility(View.VISIBLE);
		mLocalContactCanvas.setVisibility(View.GONE);
	}

	@Override
	public void onGuideComplete() {
		mGuideGallery.setVisibility(View.GONE);
		sendLoadHistReq();
		logLaunch();
	}

	public void resetSubTab() {
		showHistoryList();
	}
	
	@Override
	public boolean onTouch(View v, MotionEvent event) {
		switch (v.getId()) {
		case R.id.searchEditText:
			if(event.getAction() == MotionEvent.ACTION_DOWN){
				getRootController().startActivityForResult(new Intent(getRootController(), SearchContactActivity.class), SEARCHCONTACT_REQUEST);
			}
			return true;
		case R.id.contacterList:
			Helper.hideInputPanel(getRootController(), mSearchInput);
			return false;
		default:
			break;
		}
		return false;
	}

	@SuppressWarnings("unchecked")
	@Override
	public void update(Observable observable, final Object data) {
		getRootController().runOnUiThread(new Runnable() {
			@Override
			public void run() {
				loadLocalContact((List<ContactPerson>) data);
			}
		});
	}
	
	@Override
	public void onDestroy() {
		contactProvider.deleteObserver(this);
	}
}
