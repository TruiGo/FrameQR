package com.alipay.android.ui.framework;

import java.util.EmptyStackException;
import java.util.Stack;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.KeyEvent;

import com.alipay.android.client.Login;
import com.alipay.android.client.RootActivity;
import com.alipay.android.client.constant.Constant;
import com.alipay.android.ui.transfer.TransferViewController;

public abstract class RootController extends RootActivity {
    final static String CONTROLLER_TAG = "Controller";
    protected boolean needLogin;
    protected String loginBackParam;
    
    // The stack for BaseViewController.
    protected Stack<BaseViewController> mControllerStack = new Stack<BaseViewController>();
    
    // Class path for sub controller.
    protected abstract String getControllerClassPath();
    
    // Called when the activity is first created.
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        if(needLogin && getUserData() == null && savedInstanceState == null){
			Intent loginIntent = new Intent(this,Login.class);
			if(loginBackParam != null)
				loginIntent.setData(Uri.parse(loginBackParam));
            startActivityForResult(loginIntent, Constant.REQUEST_LOGIN_BACK);
		}
    }
    
    @Override
    protected void onStart() {
    	if(isNotEmptyStack()){
			mControllerStack.peek().onStart();
		}
    	super.onStart();
    }
    
    @Override
    protected void onStop() {
    	if(isNotEmptyStack()){
			mControllerStack.peek().onStop();
		}
    	super.onStop();
    }
    
    @Override
    protected void onPause() {
    	if(isNotEmptyStack()){
			mControllerStack.peek().onPause();
		}
    	super.onPause();
    }
    
    @Override
	protected void onDestroy() {
    	if(isNotEmptyStack()){
			mControllerStack.peek().onDestroy();
		}
		super.onDestroy();
	}
    
    private boolean isNotEmptyStack() {
		return !mControllerStack.isEmpty() && mControllerStack.peek() != null;
	}
    
    /**
     * 界面出现之前进行的操作
     * @return true继续操作，false终止
     */
    protected void onControllerInit() {
    	try {
			BaseViewController current = topController();
			if (current != null) {
				current.onControllerInit();
			}
		} catch (EmptyStackException e) {
			e.printStackTrace();
		}
	}

	@Override
    protected void onResume() {
    	super.onResume();
    	try {
			BaseViewController current = topController();
			if (current != null) {
				current.onResume();
			}
		} catch (EmptyStackException e) {
			e.printStackTrace();
		}
    }

	public BaseViewController topController() {
		if(!mControllerStack.isEmpty())
			return mControllerStack.peek();
		else
			return null;
	}
    
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        //super.onKeyDown(keyCode, event);

        boolean result = false;
		try {
			if(topController() != null)
				result = topController().onKeyDown(keyCode, event);
		} catch (Exception e) {
			e.printStackTrace();
		}
        if (!result && keyCode == KeyEvent.KEYCODE_BACK) {
    		pop();
            return true;
        }else
        	return result;
        
    }
    
    @Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if(isNotEmptyStack()){
			mControllerStack.peek().onActivityResult(requestCode, resultCode, data);
		}
		super.onActivityResult(requestCode, resultCode, data);
	}

    /**
     * 导航到一个新的视图界面。
     *
     * @param viewName 目标视图的逻辑名称。
     * @param params   要传递给目标视图的参数对象。
     *
     */
    public void navigateTo(String viewName, Object params){
        try {
            // 控制器类的构成规则
            // <com.alipay.android.ui.login> + <.> + <LoginView> + CONTROLLER_TAG
            Class<?> clazz = Class.forName(getControllerClassPath() + "." + viewName + CONTROLLER_TAG);
            BaseViewController controller = (BaseViewController)clazz.newInstance();
            controller.init(this, params);
            mControllerStack.push(controller);
            
            controller.onCreate();
            onControllerInit();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    public void navigateTo(String viewName) {
        navigateTo(viewName, null);
    }

    /**
     * 从当前视图堆栈中，弹出栈顶元素。
     *
     * @return 当前视图中的栈顶元素。
     */
    public BaseViewController pop() {
    	BaseViewController temp = null;
    	if(!mControllerStack.isEmpty()){
    		temp = mControllerStack.pop();
	        temp.onDestroy();
    	}
        
        try {
            BaseViewController current = topController();
            if (current != null) {
                current.onResume();
            }else{
            	finish();
            }
        } catch (EmptyStackException e) {
        	e.printStackTrace();
        }
        
        return temp;
    }

    /**
     * 从当前视图堆栈中，弹出count个栈顶元素。
     *
     * @return 当前视图中的栈顶元素。
     */
    public BaseViewController pop(int count) {
    	BaseViewController temp = null;
    	while (!mControllerStack.isEmpty() && count > 0){
    		temp = mControllerStack.pop();
	        temp.onDestroy();
	        count--;
    	}
        
        try {
            BaseViewController current = topController();
            if (current != null) {
                current.onResume();
            }else{
            	finish();
            }
        } catch (EmptyStackException e) {
        	e.printStackTrace();
        }
        
        return temp;
    }
    
    /**
     * 跳转到第一个视图
     */
    public void pop2FirstView(){
    	int pageSize = mControllerStack.size();
    	if(pageSize <= 1 )
    		return;
    	
    	for(int i = 0 ; i < pageSize -1;i++){
    		BaseViewController temp = mControllerStack.pop();
            temp.onDestroy();
    	}
        
        try {
            BaseViewController current = topController();
            if (current != null) {
                if (current instanceof TransferViewController) {//转账界面恢复界面到初始状态
					TransferViewController transferIndexView = (TransferViewController) current;
					transferIndexView.resetSubTab();
					return;
				}
                
                current.onResume();
            }else
            	finish();
        } catch (EmptyStackException e) {
        	e.printStackTrace();
            this.finish();
        }
    }
    
    public void clearStack(){
    	mControllerStack.clear();
    }
    
    public int stackSize(){
    	return mControllerStack.size();
    }
}