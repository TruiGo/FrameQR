/**
 * 
 */
package com.alipay.android.common.data;

import java.io.Serializable;

/**
 * @author sanping.li
 *
 */
public enum TradeStatus implements Serializable {
    WAIT_BUYER_PAY("WAIT_BUYER_PAY"),
    WAIT_SELLER_CONFIRM_TRADE("WAIT_SELLER_CONFIRM_TRADE"),
    WAIT_SYS_CONFIRM_PAY("WAIT_SYS_CONFIRM_PAY"),
    WAIT_SELLER_SEND_GOODS("WAIT_SELLER_SEND_GOODS"),
    WAIT_BUYER_CONFIRM_GOODS("WAIT_BUYER_CONFIRM_GOODS"),
    WAIT_SYS_PAY_SELLER("WAIT_SYS_PAY_SELLER"),
    TRADE_FINISHED("TRADE_FINISHED"),
    TRADE_CLOSED("TRADE_CLOSED"),
    TRADE_REFUSE("TRADE_REFUSE"),
    TRADE_REFUSE_DEALING("TRADE_REFUSE_DEALING"),
    TRADE_CANCEL("TRADE_CANCEL"),
    TRADE_PENDING("TRADE_PENDING"),
    TRADE_SUCCESS("TRADE_SUCCESS"),
    BUYER_PRE_AUTH("BUYER_PRE_AUTH"),
    COD_WAIT_SELLER_SEND_GOODS("COD_WAIT_SELLER_SEND_GOODS"),
    COD_WAIT_BUYER_PAY("COD_WAIT_BUYER_PAY"),
    COD_WAIT_SYS_PAY_SELLER("COD_WAIT_SYS_PAY_SELLER"),
    PEERPAY_INIT("PEERPAY_INIT"),
    PEERPAY_CANCEL("PEERPAY_CANCEL"),
    PEERPAY_REFUSE("PEERPAY_REFUSE"),
    PEERPAY_ABORT("PEERPAY_ABORT"),
    PEERPAY_FAIL("PEERPAY_FAIL"),
    PEERPAY_SUCCESS("PEERPAY_SUCCESS"),
    INVALID(""),
    INIT("INIT");
    
    private TradeStatus(String name) {
        mStatus = name;
    }
    
    private String mStatus;

    public String getStatus() {
        return mStatus;
    }
    
}
