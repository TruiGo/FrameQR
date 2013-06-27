package com.alipay.android.ui.voucher;

import java.util.List;

import com.alipay.android.ui.bean.VerifyRecord;

/**
 * 封装核销记录显示需要的数据
 * @author wb-wuxiaofan
 *
 */
public class VerifyRecordData {
	private List<VerifyRecord> verifyRecordList;
	private int totalTimes;
	private int canUsedTime;
	private String voucherFrom;
	private String voucherId;
	private String outBizNo;

	public VerifyRecordData() {
	}
	
	public VerifyRecordData(List<VerifyRecord> verifyRecordList, int totalTimes, int canUsedTime) {
		super();
		this.verifyRecordList = verifyRecordList;
		this.totalTimes = totalTimes;
		this.canUsedTime = canUsedTime;
	}
	public List<VerifyRecord> getVerifyRecordList() {
		return verifyRecordList;
	}
	public void setVerifyRecordList(List<VerifyRecord> verifyRecordList) {
		this.verifyRecordList = verifyRecordList;
	}

	public int getTotalTimes() {
		return totalTimes;
	}

	public void setTotalTimes(int totalTimes) {
		this.totalTimes = totalTimes;
	}

	public int getCanUsedTime() {
		return canUsedTime;
	}

	public void setCanUsedTime(int canUsedTime) {
		this.canUsedTime = canUsedTime;
	}

	public String getVoucherFrom() {
		return voucherFrom;
	}

	public void setVoucherFrom(String voucherFrom) {
		this.voucherFrom = voucherFrom;
	}

	public String getVoucherId() {
		return voucherId;
	}

	public void setVoucherId(String voucherId) {
		this.voucherId = voucherId;
	}

	public String getOutBizNo() {
		return outBizNo;
	}

	public void setOutBizNo(String outBizNo) {
		this.outBizNo = outBizNo;
	}
}
