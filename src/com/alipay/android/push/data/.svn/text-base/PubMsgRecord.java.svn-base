package com.alipay.android.push.data;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.Arrays;

import com.alipay.android.push.data.NotifierInfo;
import com.alipay.android.push.util.LogUtil;
import com.alipay.android.push.util.StringUtils;

import android.content.Context;

public class PubMsgRecord extends MsgRecord {
	private static final String LOGTAG = LogUtil.makeLogTag(PubMsgRecord.class);
	private static final int MAX_PUB_MSG = 20;

    private static String pubMsg = "pub_msg_list";
    
    public PubMsgRecord(Context context) {
		super(context);
		// TODO Auto-generated constructor stub
	}
    
    @Override
	public void setCurUserId(String userId) {
		// TODO Auto-generated method stub
	}

	@Override
	public String[] getMsgList() {
		String[] sortPubList = null;
    	
    	String pubMsgName = getMsgDir() + pubMsg;		// files/push/msg/pub_msg
    	File file = new File(pubMsgName);
    	
        if (file.exists() && file.length() > 0) {
        	try {
        		// 获取其中的id标示列表
	        	FileInputStream inputStream = new FileInputStream(file);
	        	byte[] bs;
	            bs = new byte[inputStream.available()];
	            inputStream.read(bs);
	            
	            String listMsgId = new String(bs, "utf-8");
	            LogUtil.LogOut(4, LOGTAG, "getMsgList() listMsgId:"+listMsgId);
	            
	            if(listMsgId.trim().length() > 0) {
	            	sortPubList = StringUtils.strToArray(listMsgId);
	            	Arrays.sort(sortPubList);
	            }
	            inputStream.close();
        	} catch (Exception e) {
                e.printStackTrace();
            }
        } else {
        	// can't find pubMsglist file.
        	LogUtil.LogOut(3, LOGTAG, "getMsgList() pubMsgName isn't find!");
        }

        return sortPubList;
	}

	@Override
	public boolean isContainMsg(NotifierInfo noteInfo) {
		boolean ret = false;

		MsgInfo msgInfo = noteInfo.getMsgInfo();
		LogUtil.LogOut(3, LOGTAG, "isContainMsg() newPubMsgId="+msgInfo.getPubMsgId());
    	
    	String[] nowMsgList = getMsgList();
    	if (nowMsgList != null) {
    		ret = StringUtils.isContain(nowMsgList, msgInfo.getPubMsgId());
    	}
    	
    	return ret;
	}

    private String[] updatePubMsgList(String pubMsgId) {
    	String[] newPubList = null;
    	String[] sortPubList = getMsgList();
    	
    	if (sortPubList != null && sortPubList.length > 0) {
    		if (sortPubList.length >= MAX_PUB_MSG) {
    			//replace min msgid with current pub msgid
        		sortPubList[0] = pubMsgId;
        		newPubList = sortPubList;
    		} else {
    			//still has place to put cur pub msgid
    			newPubList = StringUtils.arrayAppend(sortPubList, pubMsgId);
    		}
    	} else {
    		//no record before
    		newPubList = new String[1]; 
    		newPubList[0] = pubMsgId;
    	}
    	LogUtil.LogOut(3, LOGTAG, "updatePubMsgList() pubMsgId="+pubMsgId
    			+ ", newPubList:"+newPubList.toString());
    	
    	return newPubList;
    }

	@Override
	public void saveMsgRecord(NotifierInfo noteInfo) {
		MsgInfo msgInfo = noteInfo.getMsgInfo();
		String pubMsgId = msgInfo.getPubMsgId();
		LogUtil.LogOut(3, LOGTAG, "savePubMsgRecord() pubMsgId="+pubMsgId);
		
    	if (pubMsgId != null && pubMsgId.length() > 0) {
    		//转换为以','分隔的字符串
	    	String listMsgId = StringUtils.arrayToString(updatePubMsgList(pubMsgId));
	    	LogUtil.LogOut(4, LOGTAG, "savePubMsgRecord() listMsgId:"+listMsgId);
	    	
	    	try {
		    	String pubMsgName = getMsgDir() + pubMsg;		// files/push/msg/pub_msg
		    	File file = new File(pubMsgName);
		    	if (!file.exists()) {
		            file.createNewFile();
		    	}
		    	FileOutputStream outputStream = new FileOutputStream(file);
		    	outputStream.write(listMsgId.toString().getBytes());
		    	outputStream.close();
	    	} catch (Exception e) {
	            e.printStackTrace();
	            LogUtil.LogOut(3, LOGTAG, "savePubMsgRecord() encounter exception!");
	        }
    	} else {
    		LogUtil.LogOut(2, LOGTAG, "savePubMsgRecord() error!");
    	}
	}

	@Override
	public String getMinMsgid() {
		String mixPubMsgId = "";
    	String[] sortPubList = getMsgList();
    	if (sortPubList != null && sortPubList.length > 0) {
    		mixPubMsgId = sortPubList[0];
    	}
    	return mixPubMsgId;
	}

	@Override
	public String getMaxMsgid() {
		String maxPubMsgId = "";
    	String[] sortPubList = getMsgList();
    	if (sortPubList != null && sortPubList.length > 0) {
    		maxPubMsgId = sortPubList[sortPubList.length-1];
    	}
    	return maxPubMsgId;
	}

}
