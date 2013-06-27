package com.alipay.android.ui.beanutil;

import java.util.Comparator;

import com.alipay.android.ui.bean.Voucher;

public class VoucherComparator implements Comparator<Voucher>{

	@Override
	public int compare(Voucher lhs, Voucher rhs) {
		Long lhsGmt = lhs.getGmtCreate();
		Long rhsGmt = rhs.getGmtCreate();
		
		/*if(lhsGmt == null && rhsGmt != null){
			return 1;
		}else if(lhsGmt != null && rhsGmt == null){
			return -1;
		}else if(lhsGmt == null && rhsGmt == null){
			return 0;
		}*/
		
		return rhsGmt.compareTo(lhsGmt);
	}
}
