package com.alipay.android.core.expapp.api;

import com.alipay.android.core.expapp.Page;

/**
 * @author sanping.li
 * AST节点接口
 *
 */
public interface Node {

	/**
	 *  节点创建完成之后，准备添加子节点
	 */
	public void open();

	/**
	 * 当子节点添加完之后，关闭
	 */
	public void close();

	/** 
	 * 存取父节点
	 */
	public void setParent(Node n);
	public Node getParent();

	/**
	 *  添加子节点
	 */
	public void addChild(Node n, int i);

	/**
	 * 获取子节点，从左至右
	 */
	public Node getChild(int i);

	/**
	 * 获取子节点数目
	 */
	public int getNumChildren();

	/**
	 * 存取值
	 */
	public Object getValue();
	public void setValue(Object value);
  
  	/**
	 * 执行
	 */
	public void execute(Page page);
	
	/**
	 * for debug
	 * 弹出栈
	 */
	public void dump(String prefix);
}
