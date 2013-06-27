package com.alipay.android.push.packetListener;

import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

import com.alipay.android.push.PacketIDFilter;
import com.alipay.android.push.XmppManager;
import com.alipay.android.push.connection.PacketFilter;
import com.alipay.android.push.connection.SmackConfiguration;
import com.alipay.android.push.packet.Packet;
import com.alipay.android.push.packet.PacketConstants;
import com.alipay.android.push.packet.PacketFactory;
import com.alipay.android.push.util.ConnectParamConstant;
import com.alipay.android.push.util.Constants;
import com.alipay.android.push.util.DataHelper;
import com.alipay.android.push.util.LogUtil;
import com.alipay.android.push.util.Notifier;
import com.alipay.android.push.data.MsgInfo;
import com.alipay.android.push.data.NotifierInfo;
import com.alipay.android.push.util.record.RecordtoFile;

public class NotificationPacketListener implements PacketListener{
	private static final String LOGTAG = LogUtil.makeLogTag(NotificationPacketListener.class);
	
	private static final String NOTIFICATION_ID = "1127";
	
	private SharedPreferences settings;
	
	private final XmppManager xmppManager;

    public NotificationPacketListener(XmppManager xmppManager) {
        this.xmppManager = xmppManager;
    }

	public void processPacket(Packet packet) {
		String jsonString="";
		String msgId="0";
		String msgMissionId="0";
		String msgTitle="";
		String msgBody="";
		String msgStyle="";
		String uri="";
		
		String latestMsgId="-1";
		String latestMissionId="-1";
		
		//此处仅处理业务类packet，故用filter过滤
		PacketFilter packetFilter = new PacketIDFilter(PacketConstants.MSG_PUSH_MSGDATA);
		
		if (packetFilter.accept(packet)) {
			//收到通知消息
			SmackConfiguration.setLastConnectedTime(System.currentTimeMillis());
			xmppManager.saveLastConnectedTime(System.currentTimeMillis());
			
			LogUtil.LogOut(3, LOGTAG,"NotificationPacketListener.processPacket()...");
			
			DataHelper dataHelper = new DataHelper(xmppManager.getContext());
			MsgInfo msgInfo = null;
			NotifierInfo noteInfo = null;
			
			jsonString = packet.getData();
			
			if (jsonString != null && jsonString.length()>0) {
				try {
					//提取协议数据内容
					JSONObject jsonObject = new JSONObject(jsonString);
					
					noteInfo = dataHelper.handlePushMsg(jsonObject, false);
					dataHelper.showMsgDetail(noteInfo, Constants.ACTION_SHOW_NOTIFICATION);
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
					//停止后续的动作
					return;
				}
			}
			
			Notifier notifier = new Notifier(xmppManager.getContext());
			if (notifier.isNotificationEnabled()) {
				Packet respPacket;
				try  {
					respPacket = PacketFactory.getPacket(xmppManager.getProtoVer());
					respPacket.setMsgId(PacketConstants.MSG_PUSH_MSGDATA);
					respPacket.setMsgType(PacketConstants.MSG_PUSH_TYPE_RESPONSE);
				} catch (Exception e)  {
					e.printStackTrace();
					return;
				} 

				//发送响应
				try {
					JSONObject dataResp = new JSONObject();
					
					msgInfo = noteInfo.getMsgInfo();
					
					if(xmppManager.getUsername().length() > 0){
						dataResp.put(ConnectParamConstant.USERID, xmppManager.getUsername());
					}else{
						dataResp.put(ConnectParamConstant.USERID, "");
					}
					dataResp.put(ConnectParamConstant.NOTIFICATION_ID, msgInfo.getPerMsgId());
					dataResp.put(ConnectParamConstant.NOTIFICATION_MISSIONID, msgInfo.getMissionId());

					respPacket.setData(dataResp.toString());
					
					LogUtil.LogOut(3, LOGTAG, "processPacket() respPacket will be sent. dataResp:"+dataResp.toString());
					xmppManager.getConnection().sendPacket(respPacket);
				} catch (Exception e) {
					e.printStackTrace();
				}
				
				RecordtoFile.recordPushInfo(RecordtoFile.REASON_XMPP_RECV, RecordtoFile.ACTIONT_UNKNOWN,
						System.currentTimeMillis(), 
						RecordtoFile.REASON_XMPP_SEND, 
						System.currentTimeMillis()+1*5*1000,
						"NotificationPacketListener_processPacket",
						1);
			}
		}
	}
}
