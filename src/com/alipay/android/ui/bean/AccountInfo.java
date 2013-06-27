package com.alipay.android.ui.bean;

public class AccountInfo {
    /** 未找到用户信息 */ 
    public static final String N = "N";
    /** 正常状态 */  
    public static final String T = "T"; 
    /** 账户被冻结 */  
    public static final String B = "B";
    /** 未激活用户 */  
    public static final String W = "W";
    /** 快速注册用户 */  
    public static final String Q = "Q";
    /** 账户被注销 */  
    public static final String C = "C"; 
    
    /**未绑定*/
    public static final String NOT_BINDING = "NOT_BINDING"; 
    /**多个绑定*/
    public static final String MULTI_BINDING = "MULTI_BINDING"; 
    /**一对一绑定*/
    public static final String SINGLE_BINDING = "SINGLE_BINDING"; 
    /**有手机号为登录名(已激活)的T账号*/
    public static final String MOBILE_LOGON_ENABLE = "MOBILE_LOGON_ENABLE"; 
}
