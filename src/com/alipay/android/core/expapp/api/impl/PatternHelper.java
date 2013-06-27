package com.alipay.android.core.expapp.api.impl;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class PatternHelper {
    public static String patternMatches(String regex, String content) {
        Pattern pattern = Pattern.compile(regex);
        Matcher isNum = pattern.matcher(content);
        
        return isNum.matches() ? "true" : "false";
    }
}
