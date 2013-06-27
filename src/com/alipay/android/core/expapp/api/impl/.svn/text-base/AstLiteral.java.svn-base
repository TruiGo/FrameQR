package com.alipay.android.core.expapp.api.impl;

import com.alipay.android.core.expapp.Page;

/**
 * @author sanping.li
 *
 */
public class AstLiteral extends AstNode {
  
	public AstLiteral(int id) {
		super(id);
	}

	public AstLiteral(ExpressionParser p, int id) {
		super(p, id);
	}

	@Override
	public void execute(Page page) {
	    if(((String)getValue()).equalsIgnoreCase("@NULL"))//把@null关键字当做空字符串
	        setValue("");
	}

}
