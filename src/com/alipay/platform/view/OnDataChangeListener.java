/**
 * 
 */
package com.alipay.platform.view;

import android.content.Context;

import com.alipay.platform.core.Command;

/**
 * @author sanping.li@alipay.com
 *
 */
public interface OnDataChangeListener {
	public boolean preCancel(Command command);
	public void onCancel(Command command);
	public boolean preFail(Command command);
	public void onFail(Command command);
	public void onComplete(Command command);
	
	public void setRuleId(String ruleId);
	public String getRuleId();
	
	public Context getContext();
}
