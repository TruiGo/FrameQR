package com.alipay.android.appHall.baseService;

import java.util.List;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.net.Uri;
import android.os.Handler;
import android.os.Message;

import com.alipay.android.appHall.common.Defines;
import com.alipay.android.client.AlipayAgentPay;
import com.alipay.android.client.AlipayApplication;
import com.alipay.android.client.Login;
import com.alipay.android.client.SchemeHandler;
import com.alipay.android.client.baseFunction.AlipayPhoneBinding;
import com.alipay.android.client.constant.AlipayHandlerMessageIDs;
import com.alipay.android.client.constant.Constant;
import com.alipay.android.client.util.BaseHelper;
import com.alipay.android.core.ActivityShell;

public class XmlAppService implements BaseServiceProvider {
    private String ruleId;
    private String expId;
    private ActivityShell mActivity;
    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            String targetId = msg.what + "";
            int resultCode = msg.arg1;
            Object params = msg.obj;
            mActivity.getEngine().callback(targetId, resultCode, params);
        }
    };

    public XmlAppService(ActivityShell activity) {
        super();
        mActivity = activity;
    }

    @Override
    public void pay(String tradeNO, String partnerID, String bizType, String bizSubType, String rule) {
        if (rule != null)
            ruleId = rule;

        // added by ZhanZhi
        // 因为条码相关业务免登录模式，所以此token不只仅仅是在login时获取到
        String extern_token = mActivity.getExtToken();

        BaseHelper.payDeal(mActivity, mHandler, null, tradeNO, extern_token, partnerID, bizType,
            bizSubType);
    }

    public void bindMobile(String ruleId) {
        //    	if(ruleId != null){
        //    		this.ruleId = ruleId;
        //    	}
        if (ruleId != null)
            expId = ruleId;
        Intent tIntent = new Intent(mActivity, AlipayPhoneBinding.class);
        mActivity.startActivityForResult(tIntent, Defines.REQUEST_CODE_BAIN_MOBILE);
    }

    @Override
    public void safeTokenInit(String rule) {
        if (rule != null)
            ruleId = rule;

        // added by ZhanZhi
        // 因为条码相关业务免登录模式，所以此token不只仅仅是在login时获取到
        String extern_token = mActivity.getExtToken();

        if (mActivity.getUserData() != null) {
            //向快捷支付服务请求本地认证账号列表
            DialogInterface.OnClickListener listner = new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {
                    if (AlipayApplication.mFinishPageFirst) {
                        AlipayApplication.mFinishPage = false;
                        AlipayApplication.mFinishPageFirst = false;
                        mActivity.getEngine().exit();
                    }
                }
            };

            BaseHelper.checkStatus(mActivity, mHandler,
                AlipayHandlerMessageIDs.SAFEPAY_CHECK_GET_AUTHLIST, null, "",
                Constant.SAFE_GET_AUTHLIST, 1, listner);
        } else {
            if (extern_token != null && extern_token.length() > 0)//有账号
                BaseHelper.safeTokenInit(mActivity, mHandler, null, extern_token, "otp_account");
            else
                BaseHelper.safeTokenInit(mActivity, mHandler, null, null, "otp_noaccount");
        }
    }

    @Override
    public void login(String rule) {
        if (rule != null)
            expId = rule;
        Intent intent = new Intent(mActivity, Login.class);
        intent.putExtra(Defines.REFERER, Defines.XML_APP);

        mActivity.startActivityForResult(intent, Constant.REQUEST_LOGIN_BACK);
    }

    @Override
    public void agentPay(String tradeNO, String partnerID, String bizType, String bizSubType) {
        Intent intent = new Intent(mActivity, AlipayAgentPay.class);
        intent.putExtra(Constant.RQF_TRADE_NO, tradeNO);
        intent.putExtra(Constant.RPF_BIZTYPE, bizType);

        mActivity.startActivityForResult(intent, Defines.REQUEST_CODE_AGENT_PAY);
    }

    @Override
    public String getRuleId() {
        return ruleId;
    }

    @Override
    public String getExpId() {
        return expId;
    }

    @Override
    public void captureBarcode(String previewHint, String rule, String curCodeType) {
        Intent intent = new Intent(mActivity, com.google.zxing.client.BarcodeCaptureActivity.class);
        intent.putExtra(Constant.BARCODE_CAPTURE_HINT, previewHint);
        intent.putExtra(Constant.BARCODE_CAPTURE_TYPE, curCodeType);

        mActivity.startActivityForResult(intent, Defines.REQUEST_CODE_QRCODE);
    }

    @Override
    public void openUrlBrowser(String url) {
        Uri uri = Uri.parse(url);

        if (uri.getScheme() != null
           &&(uri.getScheme().equalsIgnoreCase(SchemeHandler.SCHEME) || uri.getScheme()
                .equalsIgnoreCase(SchemeHandler.SCHEMES))) {
            new SchemeHandler(mActivity).execute(uri);
            return;
        }

        Intent it = new Intent(Intent.ACTION_VIEW, uri);

        PackageManager pm = mActivity.getPackageManager();
        List<ResolveInfo> listResolveInfo = pm.queryIntentActivities(it,
            PackageManager.MATCH_DEFAULT_ONLY);
        if (listResolveInfo != null) {
            int size = listResolveInfo.size();
            if (size > 0) {
                mActivity.startActivity(it);
            }
        }
        //		mActivity.finish();
    }

    public Handler getHandler() {
        return mHandler;
    }
}
