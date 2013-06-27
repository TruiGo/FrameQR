package com.alipay.android.core.expapp.api.impl;

import com.alipay.android.core.expapp.Page;

/**
 * @author sanping.li
 *
 */
public class AstBlock extends AstNode {
	
	public AstBlock(int id) {
		super(id);
	}

	public AstBlock(ExpressionParser p, int id) {
		super(p, id);
	}


	@Override
	public void execute(Page page) {
		for(int i =0;i<getNumChildren();++i){
			getChild(i).execute(page);
		}
		setValue(getChild(getNumChildren()-1).getValue());
	}
}
