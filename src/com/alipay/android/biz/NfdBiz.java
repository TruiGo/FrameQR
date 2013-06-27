package com.alipay.android.biz;

import com.alipay.android.servicebeans.BaseServiceBean;
import com.alipay.android.servicebeans.ServiceBeanConfig;
import com.alipay.android.servicebeans.ServiceBeanFactory;

public class NfdBiz {
    
    private ServiceBeanFactory serviceFactory = ServiceBeanFactory.getInstance();
    /**
     * 上报位置
     * @param longitude 经度
     * @param latitude 纬度
     * @param cellId 基站id
     * @param params 上报的json串
     * @return
     */
    public boolean reportNFDLBSInfo(String longitude, String latitude,String params,String cellId,String bizType) {
        BaseServiceBean lbsLocationInfo = serviceFactory
            .getBean(ServiceBeanConfig.BEAN_NAME_REPORTLBSLOCATIONINFO);
        // 业务联网参数设置
        lbsLocationInfo.initParams(longitude, latitude, params,cellId,bizType);
        lbsLocationInfo.doX();
        return CommonRespHandler.filter(lbsLocationInfo);
    }

    /**
     * 使用lbs获取近场发现的参与者
     * @param longitude 经度
     * @param latitude 纬度
     * @param cellId 基站id
     * @return 近场发现的结果集
     */
    public String getNFDLBSInfo(String longitude, String latitude,String cellId,String bizType) {
        BaseServiceBean discoveryLBSInfo = serviceFactory
            .getBean(ServiceBeanConfig.BEAN_NAME_GETDISCOVERYLBSINFO);
        // 业务联网参数设置
        discoveryLBSInfo.initParams(longitude, latitude,cellId,bizType);
        String responseStr = discoveryLBSInfo.doX();
        if (CommonRespHandler.filter(discoveryLBSInfo)) {
            return responseStr;
        } else {
            return "";
        }
    }
    
    public boolean detectNetWorkStatus(String longitude, String latitude,String cellId,String bizType) {
        BaseServiceBean discoveryLBSInfo = serviceFactory
            .getBean(ServiceBeanConfig.BEAN_NAME_GETDISCOVERYLBSINFO);
        // 业务联网参数设置
        discoveryLBSInfo.initParams(longitude, latitude,cellId,bizType);
        discoveryLBSInfo.doX();
        return !("0".equals(discoveryLBSInfo.resultStatus));
    }
}