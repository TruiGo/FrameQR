package com.alipay.android.client.lifePayment;

import java.io.Serializable;

import org.json.JSONObject;

public class HistoryBillInfo implements Serializable {
	public String billKey = "";
	public String ownerName = "";
	public String chargeCompanyName = "";
	public String city = "";
	public String isReminded = "";
	public String remindDate = "";
	public String  originalData = "";//原始数据。
	public String chargeCompanySname = ""; //公司名称缩写
}
