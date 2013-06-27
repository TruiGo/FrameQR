package com.alipay.android.client.manufacture;

public class InterceptorFactory {
	private static InterceptorFactory instance = null;
	//single instance
	private InterceptorFactory(){};
	
	
	public static InterceptorFactory getInstance(){
		if(instance == null){
			instance = new InterceptorFactory();
		}
		return instance;
	}
	
	/**
	 * 创建程序启动时的拦截器
	 * @param channel 厂商信息
	 * @return 程序启动时拦截器
	 */
	public AppLaunchInterceptor getLaunchInterceptor(String channel){
		AppLaunchInterceptor appLaunchInterceptor = null;
		if(ManuConstants.ZTENC.equals(channel)){
			appLaunchInterceptor = new ZtencLaunchInterceptor();
		}
		return appLaunchInterceptor;
	}
	
	/**
	 * 应用程序升级覆盖安装
	 * @param channel 厂商信息
	 * @return 进行操作的拦截器
	 */
	public AppUpgradeInterceptor getUpgradeInterceptor(String channel){
		AppUpgradeInterceptor appUpgradeInterceptor = null;
		if(ManuConstants.ZTENC.equals(channel)){
			appUpgradeInterceptor = new ZtencUpgradeInterceptor();
		}
		return appUpgradeInterceptor;
	}
}
