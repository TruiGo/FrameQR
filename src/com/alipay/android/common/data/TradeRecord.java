/**
 * 
 */
package com.alipay.android.common.data;

import org.json.JSONArray;
import org.json.JSONObject;

import com.alipay.android.client.constant.Constant;

/**
 * @author sanping.li
 *
 */
public class TradeRecord extends SortableItem{
    /**
     * 订单号
     */
    private String mTradeNO;
    /**
     * 外部订单号，催卖家发货
     */
    private String mOutTradeNO;
    /**
     * 业务类型
     */
    private String mBizType;
    /**
     * 订单交易类型
     */
    private String mTradeType;
    /**
     * 订单创建时间
     */
    private String mTradeTime;
    /**
     * 订单操作类型：买/卖
     */
    private Action mAct;
    /**
     * 角色：买-0/卖-1/帮别人代付-2
     */
    private Role mBuyer;
    /**
     * 订单是否支付：未支付-0/已支付-1
     */
    private boolean mPaid;
    /**
     * 订单完成后的金额
     */
    private String mTradeMoney;
    /**
     * 订单未支付的金额
     */
    private String mPayMoney;
    /**
     * 订单状态类型
     */
    private TradeStatus mTradeStatus;
    /**
     * 订单状态描述
     */
    private String mTradeStatusMemo;
    /**
     * 返回结果
     */
    private String mResultStatus;
    /**
     * 订单商品名
     */
    private String mGoodsName;
    /**
     * partner
     */
    private String mPartnerName;
    /**
     * 卖家名字
     */
    private String mSellerName;
    /**
     * 卖家账号
     */
    private String mSellerLoginId;
    
    /**
     * 代付状态
     */
    private String mPeerPayStatus;
    private String mPeerPayStatusMemo;
    private String mPeerPayAccount;
    private String mPeerPayName;
    
    /**
     * 物流
     */
    private String mLogisticsId;
    private String mLogisticsNo;
    private String mLogisticsType;
    private String mLogisticsFee;
    private String mLogisticsName;
    private String mLogisticsPhone;
    private String mBuyerAddress;
    private String mLogisticsMemo;
    private JSONArray mLogisticsStatus;
    
    private String mPointInfo;
    
    /**
     * 操作枚举
     */
    public static enum Action{
        BUY("买入"),
        SELL("卖出"),
        INVALID("");
        
        private Action(String name){
            mName = name;
        }
        private String mName;
        
        public static Action getByName(String name) {
            if(name==null||name.length()<=0)
                return Action.INVALID;
            for(Action a:Action.values()){
                if(a.mName.equalsIgnoreCase(name))
                    return a;
            }
            return Action.INVALID;
        }
    }
    
    public static enum Role{
        BUYER("0"),
        SELLER("1"),
        PEER("2"),
        INVALID("");
        
        private Role(String val) {
            mVal = val;
        }
        private String mVal;

        public static Role getByValue(String val) {
            if(val==null||val.length()<=0)
                return Role.INVALID;
            for(Role a:Role.values()){
                if(a.mVal.equalsIgnoreCase(val))
                    return a;
            }
            return Role.INVALID;
        }

        public String getVal() {
            return mVal;
        }
        
    }
    
    public void fillData(JSONObject data){
        mTradeNO = data.optString("tradeNO");
        mOutTradeNO = data.optString("outTradeNO");
        mBizType = data.optString("bizType");
        mTradeType = data.optString("tradeType");
        mTradeTime = data.optString("tradeTime");
        mAct = Action.getByName(data.optString("act"));
        mBuyer = Role.getByValue(data.optString("buyer"));
        mPaid = data.optString("paid").equals("1");
        mTradeMoney = data.optString("tradeMoney");
        mPayMoney = data.optString("payMoney");
        mTradeStatus = TradeStatus.valueOf(data.optString("tradeStatus"));
        mResultStatus = data.optString("tradeStatus");
        mTradeStatusMemo = data.optString("tradeStatusMemo");
        mGoodsName = data.optString("goodsName");
        mPartnerName = data.optString("partnerName");
        mSellerName = data.optString("sellerName");
        mSellerLoginId = data.optString("SellerLoginId");

        mPeerPayStatus = data.optString(Constant.RPF_PEERPAY_STATUS);
        mPeerPayStatusMemo = data.optString(Constant.RPF_PEERPAY_STATUSMEMO);
        mPeerPayAccount = data.optString(Constant.RPF_PEERPAY_ACCOUNT);
        mPeerPayName = data.optString(Constant.RPF_PEERPAY_NAME);
    }

    public String getTradeNO() {
        return mTradeNO;
    }

    public String getOutTradeNO() {
        return mOutTradeNO;
    }

    public String getBizType() {
        return mBizType;
    }

    public String getTradeType() {
        return mTradeType;
    }

    public String getTradeTime() {
        return mTradeTime;
    }

    public Action getAct() {
        return mAct;
    }

    public Role getBuyer() {
        return mBuyer;
    }

    public boolean isPaid() {
        return mPaid;
    }

    public String getTradeMoney() {
        return mTradeMoney;
    }

    public String getPayMoney() {
        return mPayMoney;
    }

    public TradeStatus getTradeStatus() {
        return mTradeStatus;
    }

    public String getTradeStatusMemo() {
        if(mBuyer==Role.PEER&&mTradeStatus==TradeStatus.PEERPAY_INIT){
            return "等待代付" + "(" + mTradeStatusMemo + ")";
        }else if(mBuyer==Role.BUYER&&mPeerPayStatus!=null&&mPeerPayStatus.length()>0){
            return mTradeStatusMemo+"("+mPeerPayStatusMemo+")";
        }
        return mTradeStatusMemo;
    }

    public String getGoodsName() {
        return mGoodsName;
    }

    public String getPartnerName() {
        return mPartnerName;
    }

    public String getSellerName() {
        return mSellerName;
    }

    public String getSellerLoginId() {
        return mSellerLoginId;
    }

    public String getPeerPayStatus() {
        return mPeerPayStatus;
    }

    public String getPeerPayStatusMemo() {
        return mPeerPayStatusMemo;
    }

    public String getPeerPayAccount() {
        return mPeerPayAccount;
    }

    public String getPeerPayName() {
        return mPeerPayName;
    }

    public String getLogisticsId() {
        return mLogisticsId;
    }

    public String getLogisticsNo() {
        return mLogisticsNo;
    }

    public String getLogisticsType() {
        return mLogisticsType;
    }

    public String getLogisticsFee() {
        return mLogisticsFee;
    }

    public String getLogisticsName() {
        return mLogisticsName;
    }

    public String getLogisticsPhone() {
        return mLogisticsPhone;
    }

    public String getBuyerAddress() {
        return mBuyerAddress;
    }

    public String getLogisticsMemo() {
        return mLogisticsMemo;
    }
    
    public String getResultStatus() {
    	return mResultStatus;
    }

    public JSONArray getLogisticsStatus() {
        return mLogisticsStatus;
    }

    public String getPointInfo() {
        return mPointInfo;
    }

    @Override
    public String getSortKey() {
        return mTradeTime;
    }
}
