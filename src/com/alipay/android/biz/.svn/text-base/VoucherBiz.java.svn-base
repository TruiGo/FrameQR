package com.alipay.android.biz;

import com.alipay.android.servicebeans.BaseServiceBean;
import com.alipay.android.servicebeans.ServiceBeanConfig;
import com.alipay.android.servicebeans.ServiceBeanFactory;
import com.alipay.android.ui.beanutil.VoucherSyncHelper;

public class VoucherBiz {
	private ServiceBeanFactory serviceFactory  = ServiceBeanFactory.getInstance();
	
	public String queryVoucherList(String status,String nextPage,String pageCount,String beginDate,String endDate){
		BaseServiceBean voucherListQuery = serviceFactory
	            .getBean(ServiceBeanConfig.BEAN_NAME_QUERYVOUCHERLIST);
		
		voucherListQuery.initParams(status,nextPage,pageCount,beginDate,endDate);
		return voucherListQuery.doX();
	}
	
	public String queryVoucherDetail(String voucherId, String voucherFrom,String outBizNo,String resolution,String userAgent){
		BaseServiceBean voucherDetailQuery = serviceFactory
				.getBean(ServiceBeanConfig.BEAN_NAME_QUERYVOUCHERDETAIL);
		
		voucherDetailQuery.initParams(voucherId,voucherFrom,outBizNo,resolution,userAgent);
		return voucherDetailQuery.doX();
	}
	
	public String getStoreList(String goodsId,String cityId,String voucherFrom,String currentPage,String pageCount,String queryType,String locationData){
		BaseServiceBean getStoreList = serviceFactory
				.getBean(ServiceBeanConfig.BEAN_NAME_GETSTORELIST);
		
		getStoreList.initParams(goodsId,cityId,voucherFrom,currentPage,pageCount,queryType,locationData);
		return getStoreList.doX();
	}
	
	public String cancelTrade(String tradeNo){
		BaseServiceBean voucherListQuery = serviceFactory
				.getBean(ServiceBeanConfig.BEAN_NAME_CANCELTRADE);
		
		voucherListQuery.initParams(tradeNo);
		return voucherListQuery.doX();
	}
	
	public String getVerifyRecordList(String voucherId,String voucherFrom,String outBizNo,String currentPage,String pageCount){
		BaseServiceBean verifyRecordsQuery = serviceFactory
				.getBean(ServiceBeanConfig.BEAN_NAME_GETVERIFYRECORDLIST);
		verifyRecordsQuery.initParams(voucherId,voucherFrom,outBizNo,currentPage,pageCount);
		return verifyRecordsQuery.doX();
	}
}
