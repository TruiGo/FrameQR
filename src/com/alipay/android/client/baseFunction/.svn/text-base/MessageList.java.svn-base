package com.alipay.android.client.baseFunction;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Observable;
import java.util.Observer;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Html;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.alipay.android.client.AlipayApplication;
import com.alipay.android.client.AlipaySystemMsgDetail;
import com.alipay.android.client.Login;
import com.alipay.android.client.MessageFilter;
import com.alipay.android.client.RootActivity;
import com.alipay.android.client.advert.WebClient;
import com.alipay.android.client.constant.AlipayHandlerMessageIDs;
import com.alipay.android.client.util.Responsor;
import com.alipay.android.common.data.AdvertData;
import com.alipay.android.common.data.MessageData;
import com.alipay.android.common.data.MessageRecord;
import com.alipay.android.common.data.SortableItem;
import com.alipay.android.common.data.UserData;
import com.eg.android.AlipayGphone.R;

public class MessageList extends RootActivity implements OnClickListener, Observer {
	
    public static final int RESPONSE_CODE_EDITOR = 0;
    public static final int RESPONSE_CODE_MSG_DETAIL = RESPONSE_CODE_EDITOR + 1;

    private ImageButton mTitleRight;
    private ListView mListView;
    private TextView mEmptyView;
    /**
     * 广告位
     */
    private WebView mWebView;
    private FrameLayout mOperation;
    private boolean mShowAd;

    private MessageFilter mMessageFilter;
    private DealListAdapter mListAdapter;

    private boolean loadMsg;
    
    /**
     * 消息数据
     */
    private ArrayList<SortableItem> mMsgDatas;

    private MessageData mMessageData;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.message_list);

        if (savedInstanceState != null && savedInstanceState.containsKey("show")) {
            mShowAd = savedInstanceState.getBoolean("show");
        } else {
            mShowAd = true;
        }
        AlipayApplication application = (AlipayApplication) getApplicationContext();
        mMessageData = application.getMessageData();
        application.setMsgsRefresh(true);
        mMessageFilter = new MessageFilter(this);
        mMsgDatas = new ArrayList<SortableItem>();

        loadAllVariables();
        
        if (getUserData() == null) {
        	isPaipai = true;
        	Intent tIntent = new Intent(this, Login.class);
        	startActivity(tIntent);
		}
        
    }
    
    private boolean isPaipai;

    private void loadAllVariables() {
    	
    	TextView title = (TextView)findViewById(R.id.title_text);
    	title.setText(R.string.message_title);
    	
        mTitleRight = (ImageButton) findViewById(R.id.title_right);
        mTitleRight.setBackgroundResource(R.drawable.title_delete_icon);
        mTitleRight.setVisibility(View.VISIBLE);
        mTitleRight.setOnClickListener(this);

        mListView = (ListView) findViewById(R.id.content);
        mListAdapter = new DealListAdapter(mListView, this, mMsgDatas);
        mListView.setAdapter(mListAdapter);
        mListView.setOnItemClickListener(mListAdapter);
        mEmptyView = (TextView) findViewById(R.id.empty_info);

        mOperation = (FrameLayout) findViewById(R.id.operation);
        mWebView = (WebView) findViewById(R.id.banner);
        Button button = (Button) findViewById(R.id.close_banner);
        button.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                mShowAd = false;
                mOperation.setVisibility(View.GONE);
            }
        });
        AlipayApplication application = (AlipayApplication) getApplicationContext();
        AdvertData advertData = application.getAdvertData();
        advertData.addObserver(this);
        WebClient webClient = new WebClient(application.getHallData(), this);
        mWebView.setWebViewClient(webClient);
        mWebView.getSettings().setJavaScriptEnabled(true);
        mWebView.setWebChromeClient(new WebChromeClient() {
            @Override
            public void onProgressChanged(WebView view, int newProgress) {
                super.onProgressChanged(view, newProgress);
                if (newProgress >= 100) {
                    mOperation.setVisibility(View.VISIBLE);
                }
            }
        });
        refreshAd(advertData);
    }

    private void refreshAd(AdvertData advertData) {
        if (mShowAd && advertData.getInfoAd() != null) {
            mWebView.loadUrl(advertData.getInfoAd());
        } else {
            mOperation.setVisibility(View.GONE);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (isPaipai) {
        	isPaipai = false;
			return;
		}
        AlipayApplication application = (AlipayApplication) getApplicationContext();
        boolean refresh = application.isMsgsRefresh();
        if (refresh) {
            refreshAllData();
            application.setMsgsRefresh(false);
        }
        initRemind();
    }

    private void refreshAllData() {
        if (mListView.getEmptyView() != null) {
            mListView.getEmptyView().setVisibility(View.GONE);
            mListView.setEmptyView(null);
        }
        loadMsg = false;
        mMsgDatas.clear();
        getSavedMsg();
        getData();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.title_right:
                editMode();
                break;
            default:
                break;
        }
    }
    
    private void initRemind(){
        mTitleRight.setVisibility(View.VISIBLE);
        refresh();
        mListView.setSelection(0);
    }

    private void editMode() {
        if (mMsgDatas == null || mMsgDatas.size() <= 0) {
            Toast toast = Toast.makeText(this, R.string.no_msg_to_delete, 3000);
            toast.setGravity(Gravity.CENTER_VERTICAL, 0, 0);
            toast.show();
            return;
        }
        Intent intent = new Intent(this, MessageEditor.class);
        startActivityForResult(intent, RESPONSE_CODE_EDITOR);
    }

    private void getData() {
        getSystemMsg();
    }

    private void getSavedMsg() {
        ArrayList<MessageRecord> items = mMessageData.getAllMsg();
        if (items != null) {
            mMsgDatas.addAll(items);
        }
        refresh();
    }
    
    private void refresh() {
        Collections.sort(mMsgDatas);
        if (loadMsg && mMsgDatas.size() <= 0) {
            mEmptyView.setText(R.string.no_system_msg);
            mListView.setEmptyView(mEmptyView);
        }
        mListAdapter.notifyDataSetChanged();
    }

    private void getSystemMsg() {
        getDataHelper().sendGetMsgList(mHandler, AlipayHandlerMessageIDs.MSG_GETLIST, "100");
    }

    private Handler mHandler = new Handler() {

        @Override
        public void handleMessage(Message msg) {
            Responsor responsor = (Responsor) msg.obj;
            JSONObject jsonResponse = responsor.obj;
            boolean tResultOK = mMessageFilter.process(msg);
            switch (msg.what) {
                case AlipayHandlerMessageIDs.MSG_GETLIST:
                    loadMsg = true;
                    if (tResultOK) {
                        getMsgListFromNetFinish(jsonResponse);
                    } else {//获取信息失败
                        if (mMsgDatas.size() <= 0) {
                            mEmptyView.setText(R.string.msg_get_error);
                            mListView.setEmptyView(mEmptyView);
                        }
                    }
                    break;
                default:
                    break;

            }
            super.handleMessage(msg);
        }
    };

    private void getMsgListFromNetFinish(JSONObject jsonResponse) {
        if (jsonResponse == null)
            return;
        JSONArray msgList = null;
        JSONObject item = null;
        int msgCount = 0;
        MessageRecord msgRecord = null;
        try {
        	if(jsonResponse.has("msgCount")){
        		msgCount = jsonResponse.getInt("msgCount");
        		UserData userData = getUserData();
        		if(userData != null){
        			userData.setMsgCount(msgCount);
        		}
        	}
            if (!jsonResponse.has("msgEntityList")){
            	refresh();
                return;
            }
            msgList = jsonResponse.getJSONArray("msgEntityList");
            for (int i = 0; i < msgList.length(); ++i) {
                item = msgList.getJSONObject(i);
                msgRecord = new MessageRecord();
                msgRecord.setNew(true);
                msgRecord.fillData(item);
                mMsgDatas.add(msgRecord);
                saveMsg(msgRecord);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        refresh();
    }

    private void saveMsg(MessageRecord record) {
        mMessageData.saveNewMsg(record);
    }

    private class ViewHolder {
    	ImageView mTypeIcon;
        TextView mTitle;
        TextView mSubTitle;
    }

    private class DealListAdapter extends com.alipay.android.comon.component.ScrollMoreListAdapter {
        
    	HashMap <Integer, Integer> typeIcon = new HashMap <Integer, Integer>();
    	
    	public DealListAdapter(ListView listView, Context context, ArrayList<?> data) {
            super(listView, context, data);
            typeIcon.put(MessageRecord.TEMPLATE_TYPE_ACCOUNCEMENT, R.drawable.template_accouncement);
            typeIcon.put(MessageRecord.TEMPLATE_TYPE_BUSINESS, R.drawable.template_business);
            typeIcon.put(MessageRecord.TEMPLATE_TYPE_ACTIVITY, R.drawable.template_activity);
            typeIcon.put(MessageRecord.TEMPLATE_TYPE_OFF, R.drawable.template_off);
        }

        @Override
        public View getItemView(int position, View convertView, ViewGroup arg2) {
            if (convertView == null) {
                convertView = getLayoutInflater().inflate(R.layout.message_item, null);
                ViewHolder viewHolder = new ViewHolder();
                viewHolder.mTypeIcon = (ImageView)convertView.findViewById(R.id.type_icon);
                viewHolder.mTitle = (TextView) convertView.findViewById(R.id.title);
                viewHolder.mSubTitle = (TextView) convertView.findViewById(R.id.subtitle);
                convertView.setTag(viewHolder);
            }
            ViewHolder holder = (ViewHolder) convertView.getTag();

            
            SortableItem item = (SortableItem) mListDatas.get(position);
            if (item instanceof MessageRecord) {
            	MessageRecord msgItem = (MessageRecord) item;
                if (msgItem.isNew()) {
                    holder.mTitle.setText(Html.fromHtml("<b>" + msgItem.getTopic() + "</b>"));
                    holder.mTitle.setTextColor(getResources().getColor(R.color.text_gray));
                    holder.mSubTitle.setTextColor(getResources().getColor(R.color.TextColorGrayTwo));
                } else {
                    holder.mTitle.setText(msgItem.getTopic());
                    holder.mTitle.setTextColor(getResources().getColor(R.color.TextColorGrayTwo));
                    holder.mSubTitle.setTextColor(getResources().getColor(R.color.TextColorGrayTwo));
                }
                if(msgItem.getTemplateType() != MessageRecord.TEMPLATE_TYPE_UNKNOWN){
                	holder.mTypeIcon.setVisibility(View.VISIBLE);
                	holder.mTypeIcon.setBackgroundResource(typeIcon.get(msgItem.getTemplateType()));
                }else{
                	holder.mTypeIcon.setVisibility(View.GONE);
                }
                holder.mSubTitle.setText(msgItem.getDate());
                
            } 
            return convertView;
        }

        @Override
        protected boolean hasMore() {
        	return !(loadMsg);
        }

        @Override
        protected void itemClick(AdapterView<?> arg0, View arg1, int position, long arg3) {
            doItemClick(arg0, arg1, position, arg3);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case RESPONSE_CODE_EDITOR:
                case RESPONSE_CODE_MSG_DETAIL:
                    mMsgDatas.clear();
                    getSavedMsg();
                    break;
                default:
                    break;
            }
        }
    }

    private void doItemClick(AdapterView<?> arg0, View arg1, int position, long arg3) {
        if (mListAdapter.getItem(position) instanceof MessageRecord) {
            Intent intent = new Intent(MessageList.this, AlipaySystemMsgDetail.class);
            MessageRecord messageRecord = (MessageRecord) mListAdapter.getItem(position);
            intent.putExtra("msg", messageRecord);
            this.startActivityForResult(intent, RESPONSE_CODE_MSG_DETAIL);
        } 
    }

    @Override
    public void update(Observable observable, Object data) {
        AlipayApplication application = (AlipayApplication) getApplicationContext();
        AdvertData advertData = application.getAdvertData();
        refreshAd(advertData);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putBoolean("show", mShowAd);
    }
}
