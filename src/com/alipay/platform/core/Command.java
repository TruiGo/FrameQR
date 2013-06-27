/**
 * 命令
 */
package com.alipay.platform.core;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Queue;

import org.apache.http.Header;

import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;

/**
 * @author sanping.li@alipay.com
 *
 */
public class Command implements Cloneable{
    /**
     * 准备就绪
     */
    public static final int STATE_READY = 0x1;
    /**
     * 开始执行
     */
    public static final int STATE_STARTED = 0x2;
    /**
     * 下一个操作
     */
    public static final int STATE_NEXT = 0x3;
    /**
     * 取消操作
     */
    public static final int STATE_CANCELED = 0x4;
    /**
     * 完成操作
     */
    public static final int STATE_COMPLETED = 0x5;
    /**
     * 失败
     */
    public static final int STATE_FAILED = 0x6;

    /**
     * 读取
     */
    public static final int ACT_READ = 0;
    /**
     * 保存
     */
    public static final int ACT_SAVE = 8;
    /**
     * 删除
     */
    public static final int ACT_DELETE = 16;

    /**
     * 接口ID
     */
    private String mId;
    
    private String mOperator;

    /**
     * 操作队列：每个操作-CRUD,4个字节，第一个字节代表read,第二个字节代表write,第三个代表delete
     */
    private Queue<Integer> mActions;

    /**
     * 状态
     */
    private int mState;

    /**
     * 请求地址
     */
    private String mRequestUrl;
    /**
     * 请求参数
     */
    private ArrayList<String> mRequestParam;
    /**
     * 请求数据
     */
    private Object mRequestData;

    /**
     * 请求头
     */
    private ArrayList<Header> mRequestHeader;

    /**
     * 请求数据类型
     */
    private int mMimeType;

    /**
     * 响应码
     */
    private int mResponseCode;

    /**
     * 响应消息
     */
    private String mResponseMessage;
    
    /**
     * 响应数据
     */
    private Object mResponseData;

    /**
     * 响应的原始数据
     */
    private Object mResponseRawData;

    /**
     * 消息源，用来回调
     */
    private Messenger mCallBack;

    public Command(Messenger callBack, String id) {
        this.mCallBack = callBack;
        mActions = new LinkedList<Integer>();
        mId = id;
    }

    public String getmId() {
        return mId;
    }

    public int getAction() {
        return mActions.peek();
    }

    public void appendAction(int action) {
        mActions.offer(action);
    }

    public int getState() {
        return mState;
    }

    public synchronized void setState(Module module, int state) {
        switch (state) {
            case STATE_COMPLETED:
                mActions.remove();
                if (mActions.peek() != null) {//只用于数据存储
                    this.mState = STATE_NEXT;
                    mRequestData = mResponseData;
                    AlipayApp app = (AlipayApp) module.getContext().getApplicationContext();
                    app.getController().excute(this);
                } else {
                    this.mState = state;
                }
                break;
            default:
                this.mState = state;
                break;
        }
        
        callBack();
    }

    public String getRequestUrl() {
        return mRequestUrl;
    }

    public void setRequestUrl(String requestUrl) {
        this.mRequestUrl = requestUrl;
    }

    public ArrayList<String> getRequestParam() {
        return mRequestParam;
    }

    public void setRequestParam(ArrayList<String> requestParam) {
        mRequestParam = requestParam;
    }

    public ArrayList<Header> getRequestHeader() {
        return mRequestHeader;
    }

    public void setRequestHeader(ArrayList<Header> requestHeader) {
        mRequestHeader = requestHeader;
    }

    public Object getRequestData() {
        return mRequestData;
    }

    public void setRequestData(Object requestData) {
        this.mRequestData = requestData;
    }

    public int getResponseCode() {
        return mResponseCode;
    }

    public void setResponseCode(int responseCode) {
        this.mResponseCode = responseCode;
    }

    public String getResponseMessage() {
        return mResponseMessage;
    }

    public void setResponseMessage(String responseMessage) {
        this.mResponseMessage = responseMessage;
    }

    public Object getResponseData() {
        return mResponseData;
    }

    public void setResponseData(Object responseData) {
        mResponseData = responseData;
    }

    public int getMimeType() {
        return mMimeType;
    }

    public void setMimeType(int mimeType) {
        mMimeType = mimeType;
    }

    public Object getResponseRawData() {
        return mResponseRawData;
    }

    public void setResponseRawData(Object responseRawData) {
        mResponseRawData = responseRawData;
    }
    

    public String getOperator() {
        return mOperator;
    }

    public void setOperator(String mOperator) {
        this.mOperator = mOperator;
    }

    /**
     * 计算hash值，用于区分command
     */
    @Override
    public int hashCode() {
        return mRequestUrl.hashCode() * 31 + mRequestParam.hashCode();
    }

    public Object clone() {
        Command copy = null;
        try {
            copy = (Command) super.clone();
        } catch (CloneNotSupportedException e) {
            e.printStackTrace();
        }
        return copy;
    }
    
    private void callBack() {
        if (mCallBack == null)
            return;
        try {
            Message message = Message.obtain();
            message.obj = this.clone();
            mCallBack.send(message);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }
    
}
