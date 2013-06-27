/**
 * 
 */
package com.alipay.android.core.expapp.api.impl;

import com.alipay.android.core.expapp.Page;
import com.alipay.android.core.expapp.api.Interpreter;
import com.alipay.android.core.expapp.api.Node;

/**
 * @author sanping.li
 *
 */
public class ExpressionInterpreter implements Interpreter {
	private Page mPage;
	
	public ExpressionInterpreter(Page page) {
	    mPage = page;
	}

	@Override
	public Object excute(String id,String expression) {
	    try{
    		ExpressionParser parser = new ExpressionParser(id,expression);
    		Node rootNode = parser.parser();
    		
    		rootNode.execute(mPage);
    		return rootNode.getValue();
	    }catch (Exception e) {
	        e.printStackTrace();
            return null;
        }
	}

}
