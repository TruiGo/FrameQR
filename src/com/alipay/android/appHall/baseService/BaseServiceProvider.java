package com.alipay.android.appHall.baseService;


public interface BaseServiceProvider {
    void pay(String tradeNO, String partnerID, String bizType,String bizSubType,String rule);
    void safeTokenInit(String rule);
    void login(String ruleId);
    void agentPay(String tradeNO, String partnerID, String bizType,String bizSubType);
    void captureBarcode(String previewHint, String rule, String curCodeType);
    void openUrlBrowser(String url);
    void bindMobile(String ruleId);
    public String getRuleId();
    public String getExpId();
}
