package com.alipay.android.bizapp;

import android.app.Activity;

import com.alipay.android.bizapp.CCR.BizCCR;

public class BizFactory {
	public static BaseBiz creatBizObject(int type, Activity context){
		if(type == BizType.BizCCR){
			return new BizCCR(context);
		}
		return null;
	}
}
