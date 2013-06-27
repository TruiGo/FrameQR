package com.alipay.android.core.expapp.api.impl;

import java.util.Vector;

import com.alipay.android.core.expapp.Page;

/**
 * @author sanping.li
 *
 */
public class AstArgumentList extends AstNode {
  
	public AstArgumentList(int id) {
		super(id);
	}

	public AstArgumentList(ExpressionParser p, int id) {
		super(p, id);
	}

	@SuppressWarnings("unchecked")
    @Override
	public void execute(Page page) {
		@SuppressWarnings("rawtypes")
        Vector params = new Vector();
		for(int i =0;i<getNumChildren();++i){
			getChild(i).execute(page);
			params.addElement(getChild(i).getValue());
		}
		setValue(params);
	}

}
