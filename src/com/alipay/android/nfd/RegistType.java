package com.alipay.android.nfd;

public enum RegistType {
 
    /**蓝牙*/
    BlueTooth(0x00000001, "蓝牙"),

    /**同一局域网*/
    LANWifi(0x0000002, "同一局域网"),

    /**wifi热点*/
    WifiHot(0x0000004, "wifi热点"),

    /**LBS*/
    LBSTYPE(0x0000008, "LBS"),

    /**错误的类型*/
    WRONG(0x00000010, "错误的类型");
    
    private int code;

    private String desc;
    private RegistType(int code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    public static RegistType getCertificateType(int code) {
        for (RegistType cert : RegistType.values()) {
            if (cert.getCode()==code) {
                return cert;
            }
        }
        return RegistType.WRONG;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }
}
