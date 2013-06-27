package com.alipay.android.util;

public class HideUtils {
	public static String hide(String account){
		String hideResult = account;
		if(account != null){
			if(account.contains("@")){//邮箱账户
				int index = account.indexOf("@");
				String beforeString  =  account.substring(0, index);
				String hehindString = account.substring(index, account.length());
				if(beforeString.length()>=3){
					hideResult = beforeString.substring(0, 3) + "*" + hehindString;
				}else {
					hideResult = beforeString + "*" + hehindString;
				}
			}else if(account.matches("1\\d{10}")){//手机账号
				String beforeString = account.substring(0, 3);
				String hehindString = account.substring(7, account.length());
				hideResult = beforeString + "****" + hehindString;
			}else {//姓名
				int nameLength  = account.length();
				hideResult = "*" + account.substring(1, nameLength);
			}
		}
		return hideResult;
	}
}
