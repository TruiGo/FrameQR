package com.alipay.android.servicebeans;

import android.content.Context;

public class ServiceBeanFactory {
	private Context mContext;
	private static ServiceBeanFactory serviceBeanFactory;
	
	public static synchronized ServiceBeanFactory getInstance(){
		if(serviceBeanFactory == null){
			serviceBeanFactory = new ServiceBeanFactory();
		}
		return serviceBeanFactory;
	}
	
	public void initContext(Context context){
		this.mContext = context;
	}
	
	public Context getContext(){
		return mContext;
	}
	
    public BaseServiceBean getBean(String name) {
        try {
            // ServiceBean类的构成规则
            // <com.alipay.android.webservices> + <.> + <GetRSAPKey>
            String classPath = ServiceBeanFactory.class.getPackage().getName();
            Class<?> clazz = Class.forName(classPath + "." + name);
            BaseServiceBean baseServiceBean = (BaseServiceBean) clazz.newInstance();
            baseServiceBean.setContext(mContext);
            return baseServiceBean;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}
