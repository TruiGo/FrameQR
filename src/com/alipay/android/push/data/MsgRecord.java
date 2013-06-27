package com.alipay.android.push.data;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.StringTokenizer;

import org.json.JSONObject;

import com.alipay.android.push.config.ConfigService;
import com.alipay.android.push.data.NotifierInfo;
import com.alipay.android.push.util.LogUtil;
import com.alipay.android.push.util.StringUtils;

import android.content.Context;

public abstract class MsgRecord {
	private static final String LOGTAG = LogUtil.makeLogTag(MsgRecord.class);
	
    private static String pushRoot = "push/";
    private static String pushMsg = "msg/";
    
    //expected dir
    // files/push/msg/pub_msg
    //                userid/mission_msg
    //                      /mission_msg2
    //                      /mission2_msg
    //                userid2/...

    private Context mContext;
    private String mUserId = "private";

    public MsgRecord(Context context) {
        mContext = context;
    }
    
    public abstract void setCurUserId(String userId);
    
    /**
     * used to get all of msg list belong to the user
     */
    public abstract String[] getMsgList();
    
    /**
     * used to check whether show and save the msg information
     */
    public abstract boolean isContainMsg(NotifierInfo msgInfo);
    
    /**
     * used to manager and save msg information files
     */
    public abstract void saveMsgRecord(NotifierInfo msgInfo);
    
    public abstract String getMinMsgid();
    public abstract String getMaxMsgid();

    
    protected String getMsgDir() {
        String strPackageName = mContext.getPackageName();

        String strRoot = "/data/data/" + strPackageName + "/files/";
        String strPush = strRoot + pushRoot;			//files/push/
        String strMsg = strPush + pushMsg;			    //files/push/msg/

        File fileDR = new File(strRoot);
        fileDR.mkdir();

        File fileDP = new File(strPush);
        fileDP.mkdir();

        File fileDM = new File(strMsg);
        fileDM.mkdir();

        return strMsg;
    }
}
