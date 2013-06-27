package com.alipay.android.appHall.uiengine;

import android.widget.LinearLayout;


public interface UIInterface {
    void setVisible(boolean isVisible);
    boolean getVisible();

    void setValue(Object value);
    Object getValue();

    void setEnable(boolean isEnable);
    boolean getEnable();
    
    String getSelectedIndex();
    
    //
    void setIsSave(boolean isSave);
    boolean getIsSave();
    void set_MarginLeft(int marginLeft);
    int get_MarginLeft();
    void set_MarginRight(int marginRight);
    int get_MarginRight();
    void set_MarginBottom(int valueOf);
    int get_MarginBottom();
    void set_MarginTop(int valueOf);
    int get_MarginTop();
    
    void set_Width(int width);
    int get_Width();
    
    void set_Id(int id);
    int get_Id();
    
    void set_Text(String text);
    String get_Text();
    
    String get_Tag();
    void set_Tag(String tag);
    
    Object get_tag(int type);
    void set_Tag(int type,Object tag);
    
    LinearLayout.LayoutParams getAlipayLayoutParams();
    
}
