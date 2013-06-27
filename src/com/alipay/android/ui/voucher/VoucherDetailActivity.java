package com.alipay.android.ui.voucher;

import android.net.Uri;
import android.os.Bundle;

import com.alipay.android.client.SchemeHandler;
import com.alipay.android.core.ParamString;
import com.alipay.android.log.Constants.BehaviourID;
import com.alipay.android.ui.bean.Voucher;
import com.alipay.android.ui.framework.RootController;

public class VoucherDetailActivity extends RootController{
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		String params = getIntent().getStringExtra(SchemeHandler.PARAM);
		Voucher selectedVoucher = getIntent().getParcelableExtra("voucher");
		ParamString paramStr = new ParamString(params);
		String path = paramStr.getValue("path");
		Uri pathUri = null;
		if(path != null)
			pathUri = Uri.parse(path);
		if(params != null){
			Voucher voucher = new Voucher();
			if(pathUri != null && pathUri.getScheme() != null && pathUri.getScheme().startsWith("content")){
				voucher.setMode(Voucher.Mode.PREVIEW);
				navigateTo("CouponDetailView",voucher);
				doSimpleLog("alipass", BehaviourID.CLICKED, "ticketDetails", "dingding", "putInAlipay");
			}else{
				String voucherId = paramStr.getValue("voucherid");
				String voucherFrom = paramStr.getValue("voucherfrom");
				String outBizNo = paramStr.getValue("outbizno");
				
				voucher.setVoucherFrom(voucherFrom);
				voucher.setVoucherId(voucherId);
				voucher.setOutBizNo(outBizNo);
				
				navigateTo("VoucherDetailView",voucher);
			}
		} else if (selectedVoucher != null) {
			if (selectedVoucher.getMode() == null)
				navigateTo("VoucherDetailView", selectedVoucher);
			else
				navigateTo("CouponDetailView", selectedVoucher);
		}
	}
	
	@Override
	protected String getControllerClassPath() {
		return VoucherDetailActivity.class.getPackage().getName();
	}
}
