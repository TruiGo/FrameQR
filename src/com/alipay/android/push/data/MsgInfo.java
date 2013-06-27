package com.alipay.android.push.data;

import java.io.Serializable;

public class MsgInfo implements Serializable { 

	private static final long serialVersionUID = 1L;


	public MsgInfo() 
    {
        super();
        
        this.pubMsgId = "";
        this.perMsgId = "";
        this.msgMissionId = "";
    }

    private String pubMsgId;
    private String perMsgId;
    private String msgMissionId;
    
    
    public String getPubMsgId() {
		return pubMsgId;  
	} 
	
	public void setPubMsgId(String id) {
		this.pubMsgId = id;  
	}
	
	public String getPerMsgId() {
		return perMsgId;  
	} 
	
	public void setPerMsgId(String id) {
		this.perMsgId = id;  
	} 
	
	public String getMissionId() {
		return msgMissionId;  
	} 
	
	public void setMissionId(String mission) {
		this.msgMissionId = mission;  
	} 
}