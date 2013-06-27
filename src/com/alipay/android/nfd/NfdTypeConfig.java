package com.alipay.android.nfd;


public class NfdTypeConfig {
    private String category;

    /**
     * 名字
     */
    private String Name;
    /**
     * 值
     */
    private String value;
    
    public String getValue() {
        return value;
    }
    public void setValue(String value) {
        this.value = value;
    }
    public String getName() {
        return Name;
    }
    public void setName(String name) {
        Name = name;
    }
    public String getCategory() {
        return category;
    }
    public void setCategory(String category) {
        this.category = category;
    }
}
