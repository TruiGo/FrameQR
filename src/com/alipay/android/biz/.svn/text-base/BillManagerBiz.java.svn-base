package com.alipay.android.biz;

import com.alipay.android.servicebeans.BaseServiceBean;
import com.alipay.android.servicebeans.GetBillList;
import com.alipay.android.servicebeans.ServiceBeanConfig;
import com.alipay.android.servicebeans.ServiceBeanFactory;

/**
 * 账单管理业务逻辑类
 * @author caidie.wang
 *
 */
public class BillManagerBiz {
	
	private ServiceBeanFactory serviceBeanFactory = null;
	
	public BillManagerBiz(){
		serviceBeanFactory = ServiceBeanFactory.getInstance();
	}
	/**
	 * 卡宝一期账单查询
	 * @param nextPage
	 * @param pageCount
	 * @param startRowNum
	 * @return
	 */
	public String getBillList(String nextPage,String startRowNum){
		return getBillList(nextPage,startRowNum,String.valueOf(GetBillList.allBill),GetBillList.TRADE_TYPE_ALL,"","10","3m");
	}
	/**
	 * 账单查询
	 * @param bizType
	 * @param queryTradeType
	 * @param userRole
	 * @param nextPage
	 * @param pageCount
	 * @param startRowNum
	 * @param timeRange
	 * @return
	 */
	public String getBillList(String nextPage,String startRowNum,String bizType,String queryTradeType,String userRole,String pageCount,String timeRange){
		BaseServiceBean getBillList = serviceBeanFactory.getBean(ServiceBeanConfig.GET_BILL_LIST);
		getBillList.initParams(nextPage,startRowNum,bizType,queryTradeType,userRole,pageCount,timeRange);
		return getBillList.doX();
	}

}
