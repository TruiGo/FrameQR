package com.alipay.android.client;

import org.json.JSONObject;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TextView;

import com.alipay.android.client.constant.Constant;
import com.alipay.android.client.util.Responsor;
import com.alipay.android.comon.component.ProgressDiv;
import com.alipay.android.log.AlipayLogAgent;
import com.alipay.android.log.Constants;
import com.alipay.android.log.StorageStateInfo;
import com.alipay.android.util.LogUtil;
import com.eg.android.AlipayGphone.R;

/**
 * 代付首界面
 * @author ZhanZhi
 *
 */

public class AlipayAgentPay extends RootActivity implements OnClickListener {
    /**
     * Tag for logcat
     */
    private static final String LOG_TAG = "AlipayAgentPay";

    /**
     * Variables for About View
     */
    private TextView mTitleName = null;
    private View mOnePerson = null;
    private View mAnyLink = null;
    private TextView mTxGoodsDes = null;
    private TextView mTxSellAccount = null;
    private TextView mTxDealAmount = null;

    private static boolean isLoaded = false;
    private static String mTradeNo = null;
    private static String mBizType = null;

    private infoItem mDetailInfo = null;

    private static final int MSG_AGENT_CHECK_DEALINFO = 1100;

    public static Activity mActivity = null;
    private ProgressDiv mProgress = null;

    private StorageStateInfo storageStateInfo;
    private boolean isDetailActivity = false;
    private String curViewID;
    private String curAppID;
    private String curAppVersion;

    private Handler mHandler = new Handler() {
        public void handleMessage(Message msg) {
            LogUtil.logOnlyDebuggable(LOG_TAG, "handleMessage msg:" + msg.what);

            Responsor responsor;
            JSONObject jsonResponse;

            switch (msg.what) {
                case MSG_AGENT_CHECK_DEALINFO:

                    if (mProgress != null) {
                        mProgress.dismiss();
                        mProgress = null;
                    }

                    responsor = (Responsor) msg.obj;
                    jsonResponse = responsor.obj;
                    boolean tResultOK = false;

                    tResultOK = new MessageFilter(AlipayAgentPay.this).process(msg);
                    if ((tResultOK) && (!getDataHelper().isCanceled())) {
                        ParseDetailItem(jsonResponse, mDetailInfo);

                        //更新UI中的TextView信息
                        updateDealInfo();
                    }

                    break;
            }
        }
    };

    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mActivity = this;

        setContentView(R.layout.alipay_agent_pay_320_480);

        loadAllVariables();

        Intent intent = getIntent();
        mTradeNo = intent.getStringExtra(Constant.RQF_TRADE_NO);
        mBizType = intent.getStringExtra(Constant.RPF_BIZTYPE);
        isDetailActivity = intent.getBooleanExtra("isDetailActivity", false);

        mDetailInfo = new infoItem();

        isLoaded = false;
        storageStateInfo = StorageStateInfo.getInstance();
        //从交易详情页面跳转到代付界面
        if (isDetailActivity) {
            curViewID = Constants.TRADEDETAILSVIEW;
            curAppID = "";
            curAppVersion = "";
        } else {
            curViewID = storageStateInfo.getValue(Constants.STORAGE_CURRENTVIEWID);
            curAppID = storageStateInfo.getValue(Constants.STORAGE_APPID); //appID
            curAppVersion = storageStateInfo.getValue(Constants.STORAGE_APPVERSION); //appVersion
        }
        /* AlipayLogAgent.onPageJump(this, 
        		 curViewID,
         		 Constants.SUBSTITUTEPAYHOMEVIEW, 
         		 curAppID, //appID
         		 curAppVersion, //appVersion
         		 storageStateInfo.getValue(Constants.STORAGE_PRODUCTID),
         		 getUserId(),
        		 storageStateInfo.getValue(Constants.STORAGE_PRODUCTVERSION),
        		 storageStateInfo.getValue(Constants.STORAGE_CLIENTID));*/
    }

    @Override
    protected void onStart() {
        super.onStart();
        LogUtil.logAnyTime(LOG_TAG, "onStart() called");

        if (!isLoaded) {
            getDealInfo();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        isLoaded = false;
        LogUtil.logAnyTime(LOG_TAG, "onDestroy() called");
    }

    private void loadAllVariables() {
        // Display Item Name.
        mTitleName = (TextView) findViewById(R.id.title_text);
        mTitleName.setText(R.string.AgentPayTitle);

        mOnePerson = (View) findViewById(R.id.AgentSomeonePart);
        mOnePerson.setOnClickListener(this);
        mAnyLink = (View) findViewById(R.id.AgentAnyLinkPart);
        mAnyLink.setOnClickListener(this);

        mTxGoodsDes = (TextView) findViewById(R.id.GoodsDes);
        mTxSellAccount = (TextView) findViewById(R.id.ReceiverAccount);
        mTxDealAmount = (TextView) findViewById(R.id.DealAmountValue);
    }

    private void getDealInfo() {
        //向服务端请求交易信息，根据交易单号

        getDataHelper()
            .sendQueryTradeDetail(mHandler, MSG_AGENT_CHECK_DEALINFO, mTradeNo, mBizType);

        openProcessDialog(getString(R.string.AgentPayGetTradeInfo));
    }

    private void ParseDetailItem(JSONObject obj, infoItem tItem) {
        if (tItem == null) {
            return;
        }

        System.out.println("parse " + obj.toString());

        //		tItem.tYourName=dataHelper.mDefaultValueMap.get(Constant.RPF_USER_NAME);

        tItem.tSellerAccount = obj.optString(Constant.RPF_SELLER_ACCOUNT);
        tItem.tGoodsName = obj.optString(Constant.RPF_GOODSNAME);
        tItem.tPayMoney = obj.optString(Constant.RPF_PAYMONEY);
    }

    private void updateDealInfo() {
        //更新交易信息
        mTxGoodsDes.setText(mDetailInfo.tGoodsName);
        mTxSellAccount.setText(mDetailInfo.tSellerAccount);
        mTxDealAmount.setText(mDetailInfo.tPayMoney);

        isLoaded = true;
    }

    private void openProcessDialog(String msg) {
        if (mProgress == null) {
            mProgress = getDataHelper().showProgressDialogWithCancelButton(this, null, msg, false,
                false, null, null);
        }
    }

    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            if (curAppID != null && !"".equals(curAppID)) {
                /*AlipayLogAgent.onEvent(this,
                		Constants.MONITORPOINT_EVENT_VIEWRETURN, 
                		"",
                		Constants.SUBSTITUTEPAYHOMEVIEW,
                		curAppID,
                		curAppVersion,
                		storageStateInfo.getValue(Constants.STORAGE_PRODUCTID),
                		getUserId(),
                		storageStateInfo.getValue(Constants.STORAGE_PRODUCTVERSION),
                		storageStateInfo.getValue(Constants.STORAGE_CLIENTID));*/
            }

            LogUtil.logAnyTime(LOG_TAG, "onKeyDown KEYCODE_BACK!");
            mActivity.setResult(Activity.RESULT_CANCELED);
            this.finish();
        } else if (keyCode == KeyEvent.KEYCODE_SEARCH) {
            //捕获此事件，但不做处理
            return true;
        }

        return false;
    }

    @Override
    public void onClick(View v) {
        // TODO Auto-generated method stub
        Intent tIntent = null;
        tIntent = new Intent();
        tIntent.putExtra(Constant.RQF_BIZNO, mTradeNo);
        tIntent.putExtra(Constant.RQF_BIZTYPE, mBizType);

        StorageStateInfo storageStateInfo = StorageStateInfo.getInstance();
        if (mOnePerson.getId() == v.getId()) { //指定人代付
            LogUtil.logAnyTime(LOG_TAG, "onClick mOnePerson");

            /*AlipayLogAgent.onPageJump(this, 
            		 Constants.SUBSTITUTEPAYHOMEVIEW,
             		 Constants.ONEPERSONSUBSTITUTEPAYVIEW, 
             		 "", 
             		 "", 
             		 storageStateInfo.getValue(Constants.STORAGE_PRODUCTID),
                     getUserId(),
             		 storageStateInfo.getValue(Constants.STORAGE_PRODUCTVERSION),
             		 storageStateInfo.getValue(Constants.STORAGE_CLIENTID));*/

            tIntent.setClass(AlipayAgentPay.this, AlipayAgentSomeone.class);
            startActivityForResult(tIntent, Constant.REQUEST_CODE);
        } else if (mAnyLink.getId() == v.getId()) { //链接代付
            LogUtil.logAnyTime(LOG_TAG, "onClick mAnyLink");

            /*AlipayLogAgent.onPageJump(this, 
            		Constants.SUBSTITUTEPAYHOMEVIEW,
             		 Constants.ANYBODYSUBSTITUTEPAYVIEW, 
             		 "", 
             		 "", 
             		 storageStateInfo.getValue(Constants.STORAGE_PRODUCTID),
                     getUserId(),
             		 storageStateInfo.getValue(Constants.STORAGE_PRODUCTVERSION),
             		storageStateInfo.getValue(Constants.STORAGE_CLIENTID));*/

            tIntent.setClass(AlipayAgentPay.this, AlipayAgentAnylink.class);
            startActivityForResult(tIntent, Constant.REQUEST_CODE_TWO);
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode) {
            case Constant.REQUEST_CODE:
                // 指定人代付返回
                if (resultCode == Activity.RESULT_OK) {
                    //设置指定人代付成功，返回到上一页面——某个应用确认付款页面
                    ((AlipayApplication) getApplicationContext()).setRecordsRefresh(true);
                    mActivity.setResult(Activity.RESULT_OK);
                    mActivity.finish();
                } else if (resultCode == Activity.RESULT_FIRST_USER) { //	某些原因，和服务端交互失败
                    //失败，回到消费记录页面
                    mActivity.setResult(Activity.RESULT_CANCELED);
                    mActivity.finish();
                } else {
                    //取消，仍保留在当前的代付发起页面
                }
                break;

            case Constant.REQUEST_CODE_TWO:
                // 代付链接返回
                if (resultCode == Activity.RESULT_OK) { //	分享链接成功
                    //共享代付链接成功，返回到上一页面——某个应用确认付款页面
                    mActivity.setResult(Activity.RESULT_OK);
                    mActivity.finish();
                } else if (resultCode == Activity.RESULT_CANCELED) { //	分享链接被取消
                    //取消成功，仍保留在当前的代付发起页面
                } else if (resultCode == Activity.RESULT_FIRST_USER) { //	某些原因，和服务端交互失败
                    //失败，回到消费记录页面
                    mActivity.setResult(Activity.RESULT_CANCELED);
                    mActivity.finish();
                }
                break;
        }
    }

    private class infoItem {
        //		// 
        //		int resultType=0;		// integer of the trade status.
        //		int resultIsBuyer=0;
        //		
        //		String resultGoodsName="";
        //		String resultTradeMoney="";
        //		
        //		int tPaid=0;

        String tGoodsName = "";
        //		String tYourName="";
        String tSellerAccount = "";
        String tPayMoney = "";
    }

}