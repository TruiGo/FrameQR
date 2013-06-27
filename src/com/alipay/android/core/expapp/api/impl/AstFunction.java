package com.alipay.android.core.expapp.api.impl;

import java.util.Vector;

import com.alipay.android.core.expapp.Page;
import com.alipay.android.core.expapp.api.Lib;

/**
 * @author sanping.li
 *
 */
public class AstFunction extends AstNode {
    private String mExpressionId;
	/**
	 * 函数名
	 */
	private String name;
	/**
	 * 函数的参数列表
	 */
	@SuppressWarnings("rawtypes")
    private Vector arguments;
	
	private Lib lib;
  
	public AstFunction(int id) {
		super(id);
		lib = new InternalLib();
	}

	public AstFunction(ExpressionParser p, int id) {
		super(p, id);
		lib = new InternalLib();
	}

	public String getName(){
		return name;
	}
	
	@SuppressWarnings("rawtypes")
    public Vector getArgs(){
		return arguments;
	}
	
	public String getExpressionId() {
        return mExpressionId;
    }

    public void setExpressionId(String expressionId) {
        mExpressionId = expressionId;
    }

	@SuppressWarnings("rawtypes")
    @Override
	public void execute(Page page) {
		for(int i =0;i<getNumChildren();++i){
			getChild(i).execute(page);
		}
		name = (String) getChild(0).getValue();
		if(getNumChildren()>1)
		    arguments = (Vector) getChild(1).getValue();
		
		Object object = lib.invoke(this, page);
		if(object!=null)
			setValue(object);
	}

}
