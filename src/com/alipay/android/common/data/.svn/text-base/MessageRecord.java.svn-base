/**
 * 
 */
package com.alipay.android.common.data;

import java.io.Serializable;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * @author sanping.li
 *
 */
public class MessageRecord extends SortableItem implements Serializable{
    /**
     * 
     */
    private static final long serialVersionUID = 1L;
    /**
     * 纯文本
     */
    public static final int TYPE_TEXT = 1;
    /**
     * html
     */
    public static final int TYPE_HTML = 2;
    /**
     * URL
     */
    public static final int TYPE_URL = 3;
    /**
     * 未知的消息模板类型（主要是区别增加消息模板之前的旧系统消息）
     */
    public static final int TEMPLATE_TYPE_UNKNOWN = -1;
    /**
     * 公告信息
     */
    public static final int TEMPLATE_TYPE_ACCOUNCEMENT = 0;
    /**
     * 业务消息
     */
    public static final int TEMPLATE_TYPE_BUSINESS = 1;
    /**
     * 活动消息
     */
    public static final int TEMPLATE_TYPE_ACTIVITY = 2;
    /**
     * 核销消息
     */
    public static final int TEMPLATE_TYPE_OFF = 3;
    
    /**
     * 信息ID
     */
    private String mId;
    /**
     * 标题
     */
    private String mTopic;
    /**
     * 日期
     */
    private String mDate;
    /**
     * 内容
     */
    private String mContent;
    /**
     * 是否新消息
     */
    private boolean mIsNew;
    /**
     * 消息类型
     */
    private int mType;
    /**
     * 消息模板类型
     */
    private int templateType = TEMPLATE_TYPE_UNKNOWN;
    
    public String getId() {
        return mId;
    }
    public void setId(String id) {
        mId = id;
    }
    public String getTopic() {
        return mTopic;
    }
    public void setmTopic(String topic) {
        mTopic = topic;
    }
    public String getDate() {
        return mDate;
    }
    public void setmDate(String date) {
        mDate = date;
    }
    public String getContent() {
        return mContent;
    }
    public void setmContent(String content) {
        mContent = content;
    }
    public boolean isNew() {
        return mIsNew;
    }
    public void setNew(boolean isNew) {
        mIsNew = isNew;
    }
    
    public int getType() {
        return mType;
    }
    
    public int getTemplateType() {
		return templateType;
	}
    
	public void setTemplateType(int templateType) {
		this.templateType = templateType;
	}
	public MessageRecord fillData(JSONObject jsonObject){
        mId = jsonObject.optString("msgId");
        mTopic = jsonObject.optString("msgTopic");
        mDate = jsonObject.optString("msgDate");
        mContent = jsonObject.optString("msgContent");
        mType = jsonObject.optInt("msgType");
        if(jsonObject.has("templateType")){
        	templateType = jsonObject.optInt("templateType");
        }
        
        return this;
    }
    
    public JSONObject toJson() throws JSONException{
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("msgId", mId);
        jsonObject.put("msgTopic", mTopic);
        jsonObject.put("msgDate", mDate);
        jsonObject.put("msgContent", mContent);
        jsonObject.put("msgType", mType);
        jsonObject.put("templateType", templateType);
        return jsonObject;
    }
    
    @Override
    public String getSortKey() {
        return mId;
    }
}
