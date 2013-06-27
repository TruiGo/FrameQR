package com.alipay.android.push.data;

import org.json.JSONException;
import org.json.JSONObject;

import com.alipay.android.push.util.LogUtil;

import android.os.Parcel;
import android.os.Parcelable;

public class NotifierInfo implements Parcelable  {
	private String id = "";
	private String style = "0";
	private String title = "";
	private String content = "";
	private String uri = "";
	private String bizType = "";
    
	private MsgInfo msgInfo = new MsgInfo();

	public NotifierInfo() {
        this.id = "";
        this.title = "";
        this.content = "";
        this.uri = "";
        this.style = "0";
        this.bizType = "";
        
        this.msgInfo = new MsgInfo();
    }
	
	public NotifierInfo(Parcel source) {
		LogUtil.LogOut(4, "NotifierInfo", "NotifierInfo.Parcel...");
		
		this.id = source.readString();
		this.title = source.readString();
		this.content = source.readString(); 
		this.uri = source.readString(); 
		this.style = source.readString(); 
		this.bizType = source.readString(); 
		
		this.msgInfo = (MsgInfo) source.readValue(MsgInfo.class.getClassLoader());  
	 }  


	public String getId() {
		return id;  
	} 
	
	public void setId(String id) {
		this.id = id;  
	}
	
	public String getTitle() {
		return title;  
	} 
	
	public void setTitle(String title) {
		this.title = title;  
	} 
	
	public String getContent() {
		return content;  
	} 
	
	public void setContent(String content) {
		this.content = content;  
	} 
	
	public String getUri() {
		return uri;  
	} 
	
	public void setUri(String uri) {
		this.uri = uri;  
	}
	
	public String getStyle() {
		return style;  
	} 
	
	public void setStyle(String style) {
		this.style = style;  
	}
	
	public String getBizType() {
		return bizType;  
	} 
	
	public void setBizType(String bizType) {
		this.bizType = bizType;  
	}
	
	public MsgInfo getMsgInfo() {
		return msgInfo; 
	}
	
	public void setMsgInfo(MsgInfo info) {
		this.msgInfo = info;
	}  


    
    // just only for personal message
    public JSONObject toJson() throws JSONException{
        JSONObject jsonObject = new JSONObject();
        
        jsonObject.put("msgId", msgInfo.getPerMsgId());
        jsonObject.put("msgTitle", title);
        jsonObject.put("msgContent", content);
        
        //考虑安全，暂时不本地保存appdata
//        jsonObject.put("msgAppdata", uri);
//        jsonObject.put("msgBiztype", bizType);
//        jsonObject.put("msgStyle", style);
        
        return jsonObject;
    }



	@Override
	public int describeContents() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public void writeToParcel(Parcel dest, int arg1) {
		LogUtil.LogOut(4, "NotifierInfo", "NotifierInfo.writeToParcel...");
		
		// TODO Auto-generated method stub
		dest.writeString(id);  
		dest.writeString(title);
		dest.writeString(content);  
		dest.writeString(uri);
		dest.writeString(style);  
		dest.writeString(bizType);
		
		LogUtil.LogOut(4, "NotifierInfo", "NotifierInfo.writeToParcel msgInfo.");
		dest.writeValue(msgInfo);  
	}
	
	public static final Parcelable.Creator<NotifierInfo> CREATOR = new Parcelable.Creator<NotifierInfo>() {  
		
		@Override
		public NotifierInfo createFromParcel(Parcel source) {  
			LogUtil.LogOut(4, "NotifierInfo", "Creator.createFromParcel...");
			return new NotifierInfo(source);
		}
		
		@Override
		public NotifierInfo[] newArray(int size) {
			return new NotifierInfo[size]; 
			// throw new UnsupportedOperationException();
		}
	}; 
}