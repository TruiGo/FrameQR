package com.alipay.android.common.data;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.ArrayList;

import org.json.JSONObject;

import android.content.Context;

import com.alipay.android.client.AlipayApplication;

/**
 * @author sanping.li
 *  为了兼容，使用文件保存，已读未读保存不同文件夹
 */
public class MessageData {
    private static String strDir = "Msg/";
    private static String strDirUnread = "Unread/";
    private static String strDirReaded = "readed/";

    private Context mContext;

    public MessageData(Context context) {
        mContext = context;
    }

    private String getUnreadMsgDir() {
        String strPath = getMsgDir();
        String strPathUnread = strPath + strDirUnread;
        File fileD = new File(strPathUnread);
        fileD.mkdir();

        return strPathUnread;
    }

    private String getReadedMsgDir() {
        String strPath = getMsgDir();
        String strPathReaded = strPath + strDirReaded;
        File fileD = new File(strPathReaded);
        fileD.mkdir();

        return strPathReaded;
    }

    private String getMsgDir() {
        AlipayApplication application = (AlipayApplication) mContext.getApplicationContext();
        UserData userData = application.getUserData();
        if (userData == null)
            return null;
        String strUserId = userData.getUserId();
        String strPackageName = mContext.getPackageName();

        String strPath0 = "/data/data/" + strPackageName + "/files/";
        String strPath1 = strPath0 + strDir;
        String strPath2 = strPath1 + strUserId + "/";

        File fileD0 = new File(strPath0);
        /*boolean bsuc = */fileD0.mkdir();

        File fileD1 = new File(strPath1);
        /*bsuc = */fileD1.mkdir();

        File fileD2 = new File(strPath2);
        fileD2.mkdir();

        return strPath2;
    }

    /**
     * 获取所有消息
     */
    public ArrayList<MessageRecord> getAllMsg() {
        ArrayList<MessageRecord> reads = readReadedMsg();
        ArrayList<MessageRecord> unReads = readUnreadMsg();
        if (reads != null) {
            if (unReads != null)
                reads.addAll(unReads);
            return reads;
        }
        return unReads;
    }

    /**
     * 保存消息
     */
    public void saveNewMsg(MessageRecord messageRecord) {
        if (!messageRecord.isNew())
            return;
        String strPath = getUnreadMsgDir();
        try {
            writeMsg(messageRecord, strPath);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 读消息，未读->已读
     */
    public void readedMsg(MessageRecord messageRecord) {
        String unReadPath = getUnreadMsgDir();
        File file = new File(unReadPath, String.valueOf(messageRecord.getId()));
        if (file.exists()) {
            String readPath = getReadedMsgDir();
            File toTile = new File(readPath, String.valueOf(messageRecord.getId()));
            file.renameTo(toTile);
        } else {
            saveOldMsg(messageRecord);
        }
    }

    /**
     * 读取保存的已读消息
     */
    public ArrayList<MessageRecord> readReadedMsg() {
        String strPath = getReadedMsgDir();
        return readMsgs(strPath, false);//已读
    }

    /**
     * 读取保存的未读消息
     */
    public ArrayList<MessageRecord> readUnreadMsg() {
        String strPath = getUnreadMsgDir();
        return readMsgs(strPath, true);//未读
    }
    
    /**
     * 获取未读消息条数
     * @return
     */
    public int getUnreadMsgCount(){
    	int count = 0;
    	ArrayList<MessageRecord> unReadMessage = readUnreadMsg();
    	if(unReadMessage != null){
    		count = unReadMessage.size();
    	}
    	return count;
    }

    /**
     * 删除消息
     */
    public void deleteMsg(MessageRecord messageRecord) {
        String strPath = null;
        if (messageRecord.isNew()) {
            strPath = getUnreadMsgDir();
        } else {
            strPath = getReadedMsgDir();
        }
        File file = new File(strPath, String.valueOf(messageRecord.getId()));
        file.delete();
    }

    private void saveOldMsg(MessageRecord messageRecord) {
        if (messageRecord.isNew())
            return;
        String strPath = getReadedMsgDir();
        try {
            writeMsg(messageRecord, strPath);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private ArrayList<MessageRecord> readMsgs(String strPath, boolean isNew) {
        ArrayList<MessageRecord> records = null;
        File file = new File(strPath);
        if (file.exists() && file.isDirectory()) {
            File[] files = file.listFiles();
            records = new ArrayList<MessageRecord>();
            MessageRecord messageRecord = null;
            for (File f : files) {
                String id = f.getName();
                messageRecord = new MessageRecord();
                messageRecord.setId(id);
                messageRecord.setNew(isNew);
                try {
                    readMsg(messageRecord, f);
                    records.add(messageRecord);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
        return records;
    }

    /**
     * 写消息
     * @throws Exception 
     */
    private void writeMsg(MessageRecord messageRecord, String strPath) throws Exception {
        File file = new File(strPath, String.valueOf(messageRecord.getId()));
        if (!file.exists())
            file.createNewFile();
        FileOutputStream outputStream = new FileOutputStream(file);
        outputStream.write(messageRecord.toJson().toString().getBytes());
    }

    /**
     * 读消息
     * @throws Exception 
     */
    private void readMsg(MessageRecord messageRecord, File file) throws Exception {
        FileInputStream inputStream = new FileInputStream(file);
        byte[] bs;
        bs = new byte[inputStream.available()];
        inputStream.read(bs);
        JSONObject jsonObject = new JSONObject(new String(bs, "utf-8"));
        messageRecord.fillData(jsonObject);
    }
    
	public MessageRecord getMessageById(String messageId) {
		ArrayList<MessageRecord> messages = getAllMsg();
		if(messages != null)
			for(MessageRecord message : messages){
				if(messageId.equals(message.getId()))
					return message;
			}
		
		return null;
	}
}
