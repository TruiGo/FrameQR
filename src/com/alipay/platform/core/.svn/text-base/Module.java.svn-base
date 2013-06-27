/**
 * 控制器模块
 */
package com.alipay.platform.core;

import android.content.Context;

/**
 * @author sanping.li@alipay.com
 *
 */
public interface Module {
	/**
	 * 主存储
	 */
	public static final int CTR_CACHE		= 0x1;
	/**
	 * 数据库
	 */
	public static final int CTR_PERSISTENCE	= 0x1<<1;
	/**
	 * 包资源
	 */
	public static final int CTR_RES			= 0x1<<2;
	/**
	 * 文件
	 */
	public static final int CTR_FILE		= 0x1<<3;
	/**
	 * 网络
	 */
	public static final int CTR_NET			= 0x1<<4;
	
	/**
	 * 模块类型
	 */
	public int getType();
	/**
	 * 模块名称
	 */
	public String getName();
	/**
	 * 上下文
	 */
	public Context getContext();
}
