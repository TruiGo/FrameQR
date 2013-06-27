package com.alipay.android.ui.bean;

import java.util.ArrayList;

import com.alipay.android.client.baseFunction.AlipayDetailInfo;
/**
 * 请求消费记录返回结果集
 * @author caidie.wang
 *
 */
public class BillListInfo {
	/**
	 * 总页数
	 */
	private int pageCount;
	/**
	 * 目前第几页
	 */
	private int page;
	/**
	 * 总数据条数
	 */
	private int totalCount;
	/**
	 * 是否是等待处理交易
	 */
	private int isWaitPay;
	public static final int WAIT_PAY = 0;
	public static final int ALL = 1;
	/**
	 * 起止日期开始时间
	 */
	private String startDateTime = null;
	/**
	 * 起止日期结束时间
	 */
	private String endDateTime = null;
	/**
	 * 联合查询起始行号
	 */
	private String startRowNum = null;
	/**
	 * 列表集合
	 */
	private ArrayList<AlipayDetailInfo> tardeLists = null;
	/**
	 * 当前获取到得交易条数
	 */
	private int mCount;
    /**
     * 每页交易条数
     */
    private int mPageSize = 10;
    /**
     * 待处理交易条数
     */
    private int waitPayBillCount;
    /**
     * 待缴费条数
     */
    private int lifePayCount;
    /**
     * 是否显示模块分割线
     */
    private boolean isShowDivide;
    
    /**
     * 是否需要获取更多
     * @return
     */
    public boolean hasMore(){
    	return page < ((totalCount <= mPageSize) ? 1 : (totalCount / mPageSize) + 1)/*mTotalPage*/ && mCount < totalCount;
    }
	
	public int getmCount() {
		return mCount;
	}

	public void setmCount(int mCount) {
		this.mCount = mCount;
	}
	
	public void mCountAdd1(){
		mCount++;
	}

	public int getmPageSize() {
		return mPageSize;
	}

	public void setmPageSize(int mPageSize) {
		this.mPageSize = mPageSize;
	}

	public int getPageCount() {
		return pageCount;
	}
	public void setPageCount(int pageCount) {
		this.pageCount = pageCount;
	}
	public int getPage() {
		return page;
	}
	public void setPage(int page) {
		this.page = page;
	}
	public int getTotalCount() {
		return totalCount;
	}
	public void setTotalCount(int totalCount) {
		this.totalCount = totalCount;
	}
	public int getIsWaitPay() {
		return isWaitPay;
	}
	public void setIsWaitPay(int isWaitPay) {
		this.isWaitPay = isWaitPay;
	}
	public String getStartDateTime() {
		return startDateTime;
	}
	public void setStartDateTime(String startDateTime) {
		this.startDateTime = startDateTime;
	}
	public String getEndDateTime() {
		return endDateTime;
	}
	public void setEndDateTime(String endDateTime) {
		this.endDateTime = endDateTime;
	}
	public String getStartRowNum() {
		return startRowNum;
	}
	public void setStartRowNum(String startRowNum) {
		this.startRowNum = startRowNum;
	}
	public ArrayList<AlipayDetailInfo> getTardeLists() {
		return tardeLists;
	}
	public void setTardeLists(ArrayList<AlipayDetailInfo> tardeLists) {
		this.tardeLists = tardeLists;
	}

	public int getWaitPayBillCount() {
		return waitPayBillCount;
	}

	public void setWaitPayBillCount(int waitPayBillCount) {
		this.waitPayBillCount = waitPayBillCount;
	}
	
	public int getLifePayCount() {
		return lifePayCount;
	}

	public void setLifePayCount(int lifePayCount) {
		this.lifePayCount = lifePayCount;
	}
	
	public boolean isShowDivide() {
		return isShowDivide;
	}

	public void setShowDivide(boolean isShowDivide) {
		this.isShowDivide = isShowDivide;
	}
	
}

