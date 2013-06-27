package com.alipay.android.client;

import android.content.Context;
import android.os.Handler;
import android.os.Message;

import com.alipay.android.client.constant.AlipayHandlerMessageIDs;
import com.alipay.android.client.util.DataHelper;

public class PromotionHelper {
//	private Context mContext;
	
    public void init(Context context) {
//    	mContext = context;
        AlipayApplication application = (AlipayApplication) context.getApplicationContext();
        DataHelper myHelper = application.getDataHelper();
        myHelper.sendActive(mHandler, AlipayHandlerMessageIDs.MSG_ACTIVE,context);
    }
    
    /**
     * 报活后的处理
     */
    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
        	super.handleMessage(msg);
        	
        	//memo="",
        	//json={"playing":"3","resultStatus":100,"sessionId":"FF7BCC95500BB6091DD8E781C5BA0FFC.1-1","serialVersionUID":-8216049486162061508}
        	//status=100
        }
    };
}
