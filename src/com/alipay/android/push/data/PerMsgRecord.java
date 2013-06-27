package com.alipay.android.push.data;

import java.io.File;
import java.io.FileOutputStream;
import java.util.Arrays;

import com.alipay.android.push.data.NotifierInfo;
import com.alipay.android.push.util.LogUtil;
import com.alipay.android.push.util.StringUtils;

import android.content.Context;

public class PerMsgRecord extends MsgRecord {
	private static final String LOGTAG = LogUtil.makeLogTag(PerMsgRecord.class);
	private static final int MAX_PER_MSG = 20;

    private String mUserId = "private";
    
    public PerMsgRecord(Context context) {
		super(context);
		// TODO Auto-generated constructor stub
	}

	@Override
	public void setCurUserId(String userId) {
		// TODO Auto-generated method stub
	}
	
	private String getPerMsgDir() {
    	String usrDir = getMsgDir() + mUserId + "/";
    	
    	File fileDM = new File(usrDir);
        fileDM.mkdir();
        
    	return usrDir;
    }

	@Override
	public String[] getMsgList() {
		String[] fileList = null;
    	
    	String usrDir = getPerMsgDir();
    	File file = new File(usrDir);
    	if (file.isDirectory()) {
    		fileList = file.list();
    		if (fileList != null) {
    			//做个排序
    			Arrays.sort(fileList);
    			LogUtil.LogOut(3, LOGTAG, "getMsgList() "+fileList.length +" fileList="+StringUtils.arrayToString(fileList));
    		}
    	} else {
    		LogUtil.LogOut(2, LOGTAG, "getMsgList() usrDir can't find!");
    	}
    	
    	return fileList;
	}
	

    @Override
    public boolean isContainMsg(NotifierInfo noteInfo) {
    	boolean ret = false;
    	
    	MsgInfo msgInfo = noteInfo.getMsgInfo();
    	String newPerMsgId = msgInfo.getMissionId() + "_" + msgInfo.getPerMsgId();
    	LogUtil.LogOut(3, LOGTAG, "isContainMsg() newPerMsgId="+newPerMsgId);
    	
    	String[] nowMsgList = getMsgList();
    	if (nowMsgList != null) {
    		ret = StringUtils.isContain(nowMsgList, newPerMsgId);
    	}
    	LogUtil.LogOut(3, LOGTAG, "isContainMsg() ret="+ret);
    	
    	return ret;
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
    
    @Override
    public void saveMsgRecord(NotifierInfo msgInfo) {
    	String[] sortPerList = getMsgList();
    	LogUtil.LogOut(4, LOGTAG, "saveMsgRecord() msg:"+msgInfo.getTitle());
    	
    	if (sortPerList != null && (sortPerList.length >= MAX_PER_MSG)) {
			//replace oldest msg info with current per msg info
    		// firstly get the oldest file name and delete it
    		File temp = new File(getPerMsgDir(), sortPerList[0]);
    		boolean success = temp.delete();
            if(!success) {
            	LogUtil.LogOut(2, LOGTAG, "saveMsgRecord() fail to delete file:"+sortPerList[0]);
            }
            
            // then save new msg info
    	} else {
    		//there is still place to put the per msg record
    		//so just save this msg info
    	}
    	
    	saveMsgInfo(msgInfo);
    }
    
    /**
     * used to save the personal notifier information
     */
    private void saveMsgInfo(NotifierInfo noteInfo) {
    	String usrDir = getPerMsgDir();
    	MsgInfo msgInfo = noteInfo.getMsgInfo();
    	
    	try {
    		String fileName = msgInfo.getMissionId() + "_" + msgInfo.getPerMsgId();
    		LogUtil.LogOut(4, LOGTAG, "saveMsgInfo() newPerMsgName:"+fileName);
    		
    		File file = new File(usrDir, String.valueOf(fileName));
            if (!file.exists())
                file.createNewFile();
            
            FileOutputStream outputStream = new FileOutputStream(file);
            outputStream.write(noteInfo.toJson().toString().getBytes());
            outputStream.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
