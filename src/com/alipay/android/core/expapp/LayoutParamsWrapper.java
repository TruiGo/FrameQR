package com.alipay.android.core.expapp;

import android.content.Context;
import android.view.Gravity;
import android.view.ViewGroup;

public class LayoutParamsWrapper {

    public static final String FILL_PARENT = "fill_parent";
    public static final String WRAP_CONTENT = "wrap_content";
    
    public static final String LEFT = "left";
    public static final String RIGHT = "right";
    public static final String TOP = "top";
    public static final String BOTTOM = "bottom";
    public static final String CENTER = "center";
    public static final String CENTER_VERTICAL = "center_vertical";
    public static final String CENTER_HORIZONTAL = "center_horizontal";

    public static int widthValueFromString(Context context,String value) {
        int iValue = ViewGroup.LayoutParams.FILL_PARENT;
        if (value.equalsIgnoreCase(LayoutParamsWrapper.FILL_PARENT)) {
            ;
        } else if (value.equalsIgnoreCase(LayoutParamsWrapper.WRAP_CONTENT)) {
            iValue = ViewGroup.LayoutParams.WRAP_CONTENT;
        } else if(value.endsWith("dp")){
            iValue = Integer.valueOf(value.replace("dp", "")).intValue();
            float scale = context.getResources().getDisplayMetrics().density;
            iValue = (int) ((iValue*scale)+0.5f);
        } else if(value.endsWith("sp")){
            iValue = Integer.valueOf(value.replace("sp", "")).intValue();
            float scale = context.getResources().getDisplayMetrics().density;
            float fontW = (16*scale)+0.5f;
            iValue *= fontW;
        }

        return iValue;
    }

    public static int fontSizeValueFromString(String value) {
        
        int fontSizeType = 16;
        
        fontSizeType = Integer.valueOf(value.replace("dp", "")).intValue();
        return fontSizeType;
    }

    public static int gravityValueFromString(String value) {
        int gravity = Gravity.LEFT;

        if (value.equalsIgnoreCase(LayoutParamsWrapper.LEFT)) {
            ;
        } else if (value.equalsIgnoreCase(LayoutParamsWrapper.RIGHT)) {
            gravity = Gravity.RIGHT;
        } else if (value.equalsIgnoreCase(LayoutParamsWrapper.TOP)) {
            gravity = Gravity.TOP;
        } else if (value.equalsIgnoreCase(LayoutParamsWrapper.BOTTOM)) {
            gravity = Gravity.BOTTOM;
        } else if (value.equalsIgnoreCase(LayoutParamsWrapper.CENTER)) {
            gravity = Gravity.CENTER;
        } else if (value.equalsIgnoreCase(LayoutParamsWrapper.CENTER_VERTICAL)) {
            gravity = Gravity.CENTER_VERTICAL;
        } else if (value.equalsIgnoreCase(LayoutParamsWrapper.CENTER_HORIZONTAL)) {
            gravity = Gravity.CENTER_HORIZONTAL;
        }
        
        return gravity;
    }
}
