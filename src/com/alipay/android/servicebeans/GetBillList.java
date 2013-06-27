package com.alipay.android.servicebeans;

import org.json.JSONException;
import org.json.JSONObject;
/**
 * 获取账单列表
 * @author caidie.wang
 *
 */
public class GetBillList extends BaseServiceBean{
	
	public static String BIZ_TAG = GetCardList.class.getName();
	/**
	 * 账单类型
	 * 0、全部账单
	 * 1、公共事业缴费账单
	 * 2、交易账单
	 */
	private int bizType ;
	public static final int allBill = 0;
	public static final int pubBill = 1;
	public static final int tardeBill = 2;
	/**
	 * 查询交易时间范围
	 * 默认为3个月
	 */
	private String timeRange = "3m";
	/**
	 * 查询交易类型
	 * all,processing,tradeSuc,tradeFail,waitPay,confirmGoods,transfer
	 * 可以 组合 waitPay|confirmGoods 
	 */
	private String queryTradeType = null;
	public static final String TRADE_TYPE_ALL = "all";
	public static final String TRADE_TYPE_PROCESSING = "processing";
	public static final String TRADE_TYPE_TRADESUC = "tradeSuc";
	public static final String TRADE_TYPE_TRADEFAIL = "tradeFail";
	public static final String TRADE_TYPE_WAITPAY = "waitPay";
	public static final String TRADE_TYPE_CONFIRMGOODS = "confirmGoods";
	public static final String TRADE_TYPE_TRANSFER = "transfer";
	/**
	 * 交易角色 
	 */
	private String userRole = "";
	public static final String BUYER = "buyer";
	public static final String SELLER = "seller";
	/**
	 * 下一页 
	 * 默认为第一页
	 */
	private int nextPage ;
	/**
	 * 每页显示的条数
	 * 默认为5条
	 */
	private int pageCount ;
	/**
	 * 联合查询起始行号
	 */
	private String startRowNum = null;
	
	public GetBillList(){
		operationType = ServiceBeanConstants.GET_BILL_LIST;
	}
	@Override
	public void initParams(String... taskParams) {
		super.initParams(taskParams);
		nextPage = Integer.parseInt(taskParams[0]);
		startRowNum = taskParams[1];
		bizType = Integer.parseInt(taskParams[2]);
		queryTradeType = taskParams[3];
		userRole = taskParams[4];
		pageCount = Integer.parseInt(taskParams[5]);
		timeRange = taskParams[6];
	}
	@Override
	protected String buildRequestAsString() {
		JSONObject requestJson = prepareRequest();
		try {
			requestJson.put(ServiceBeanConstants.NEXT_PAGE, nextPage);
			requestJson.put(ServiceBeanConstants.START_ROW_NUM, startRowNum);
			requestJson.put(ServiceBeanConstants.BIZ_TYPE, bizType);
			requestJson.put(ServiceBeanConstants.QUERY_TRADE_TYPE,queryTradeType);
			requestJson.put(ServiceBeanConstants.USER_ROLE,userRole);
			requestJson.put(ServiceBeanConstants.PAGE_COUNT,pageCount);
			requestJson.put(ServiceBeanConstants.TIME_RANGE,timeRange);
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return requestJson.toString();
	}
	
	

}
