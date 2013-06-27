package com.alipay.android.client.baseFunction;

import java.util.Observable;
import java.util.Observer;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.alipay.android.client.AlipayApplication;
import com.alipay.android.client.Main;
import com.alipay.android.client.MessageFilter;
import com.alipay.android.client.RootActivity;
import com.alipay.android.client.constant.Constant;
import com.alipay.android.common.data.AdvertData;
import com.alipay.android.comon.component.ProgressDiv;
import com.eg.android.AlipayGphone.R;

public class More extends RootActivity implements OnClickListener, MoreOperationListener, Observer,
                                      TabItemCallback {

    private TextView mSetting;//设置

    private TextView mUserFeedback;
    private TextView mShare;
    private TextView mRecommendApp;
    private TextView mUpdate;
    private TextView mAbout;
    private TextView mHelp;
    private TextView mTitleView;
    //	private ToggleButton mLoginSettingToggleButton;
    private LinearLayout mSwitchLogin;
    private LinearLayout mExitApp;
    private MessageFilter mMessageFilter = new MessageFilter(this);
    private ProgressDiv mProgress = null;

    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.more);
        loadAllVariables();
    }

    @Override
    protected void onResume() {
        // TODO Auto-generated method stub
        super.onResume();
        initData();
    }

    private Handler mHandler = new Handler() {
        public void handleMessage(Message msg) {
            showResult(msg);
            super.handleMessage(msg);
        }
    };

    private void showResult(Message msg) {
        boolean tResultOK = false;
        boolean needDismissProcessDialog = true;

        tResultOK = mMessageFilter.process(msg);
        if ((tResultOK) && (!getDataHelper().isCanceled())) {
            ;
        }

        if (needDismissProcessDialog) {
            if (mProgress != null) {
                mProgress.dismiss();
                mProgress = null;
            }
        }
    }

    private void loadAllVariables() {

        mSetting = (TextView) findViewById(R.id.setting_title);
        mSetting.setOnClickListener(this);
        mUserFeedback = (TextView) findViewById(R.id.userFeedback);
        mUserFeedback.setOnClickListener(this);
        mShare = (TextView) findViewById(R.id.share);
        mShare.setOnClickListener(this);
        mRecommendApp = (TextView) findViewById(R.id.recommend_app);
        mRecommendApp.setOnClickListener(this);
        AlipayApplication application = (AlipayApplication) getApplicationContext();
        AdvertData advertData = application.getAdvertData();
        advertData.addObserver(this);
        refresh(advertData);
        mUpdate = (TextView) findViewById(R.id.update);
        mUpdate.setOnClickListener(this);
        mAbout = (TextView) findViewById(R.id.about);
        mAbout.setOnClickListener(this);
        mHelp = (TextView) findViewById(R.id.help);
        mHelp.setOnClickListener(this);
        mTitleView = (TextView) findViewById(R.id.title_text);

        //    	//登陆模式，目前暂时无此设置项
        //    	mLoginSettingToggleButton = (ToggleButton) findViewById(R.id.loginSettingToggleButton);
        //    	mLoginSettingToggleButton.setOnClickListener(this);

        //注销功能已经移到我的账户中
//        mSwitchLogin = (LinearLayout) findViewById(R.id.switchLogin);
//        mSwitchLogin.setOnClickListener(this);
        //mExitApp = (LinearLayout) findViewById(R.id.exitApp);
        //mExitApp.setOnClickListener(this);
    }

    private void refresh(AdvertData advertData) {
        if (null == mRecommendApp) {
        	return ;
        }
        
        if (advertData.getAppAd() == null) {
        	mRecommendApp.setVisibility(View.GONE);
        } else {
        	mRecommendApp.setVisibility(View.VISIBLE);
        }
    }

    private void initData() {
        mTitleView.setText(R.string.AppMore);
    }

    @Override
    public void onClick(View v) {
        MoreOnClickListener.onClick(v.getId(), this, getDataHelper(), mHandler);
    }

    @Override
    public void startAsyncOperation() {
        // TODO Auto-generated method stub
        if (mProgress == null) {
            mProgress = getDataHelper().showProgressDialogWithoutCancelButton(this, null,
                this.getString(R.string.GettingInfo), false, true, null, null);
        }
    }

    @Override
    public void update(Observable observable, Object data) {
        AlipayApplication application = (AlipayApplication) getApplicationContext();
        AdvertData advertData = application.getAdvertData();
        refresh(advertData);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        // TODO Auto-generated method stub
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == Constant.REQUEST_LOGIN_BACK) {
            if (resultCode == RESULT_OK) {
                Intent intent = new Intent(this, Main.class);
                intent.setAction(Intent.ACTION_MAIN);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
            }
        }
    }

    @Override
    public void newUri(String params) {
        // TODO Auto-generated method stub

    }

}
