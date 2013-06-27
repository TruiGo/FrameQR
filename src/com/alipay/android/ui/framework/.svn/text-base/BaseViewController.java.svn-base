package com.alipay.android.ui.framework;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup.LayoutParams;

import com.alipay.android.comon.component.ProgressDiv;
import com.alipay.android.log.Constants;
import com.eg.android.AlipayGphone.R;

public abstract class BaseViewController{
    private RootController rootController;
    protected View mView;
    protected Object params;

    public void init(RootController rootController, Object params) {
        this.rootController = rootController;
        this.params = params;
    }
    
    public void onControllerInit() {
	}
    
    public String getString(int resId) {
    	return this.rootController.getString(resId);
    }
    
    public void doSimpleLog(String appID, Constants.BehaviourID behaviourID, String curViewID, String refViewID, String seed) {
    	rootController.doSimpleLog(appID, behaviourID, curViewID, refViewID, seed);
    }
    
    public void showDialog(int iconRes, String titleText,
			String MessageText, String btn1Msg,
			DialogInterface.OnClickListener btn1, String btn2Msg,
			DialogInterface.OnClickListener btn2, String btn3Msg,
			DialogInterface.OnClickListener btn3) {
    	rootController.getDataHelper().showDialog(
    			rootController, iconRes, titleText, MessageText, btn1Msg, btn1, btn2Msg, btn2, btn3Msg, btn3);
    }
    
    public void startActivity(Intent intent) {
    	getRootController().startActivity(intent);
    }
    
	public void alert(String alertString) {
		showDialog(R.drawable.infoicon, getString(R.string.WarngingString),
				alertString, getString(R.string.Ensure), null, null, null,
				null, null);
	}
    
    public void navigateTo(String viewName) {
    	rootController.navigateTo(viewName, null);
    }
    
    public void navigateTo(String viewName, Object params) {
    	rootController.navigateTo(viewName, params);
    }
    
    public BaseViewController pop() {
    	return rootController.pop();
    }
    
    public BaseViewController pop(int count) {
    	return rootController.pop(count);
    }

    public View findViewById(int id) {
        return this.rootController.findViewById(id);
    }

    public RootController getRootController() {
        return this.rootController;
    }

    public boolean onKeyDown(int keyCode, KeyEvent event) {
        return false;
    }

    protected void onCreate() {
    }

    protected void onResume() {
    	if(mView != null)
    		addView(mView, null);	
    }

    protected void addView(View view, LayoutParams params) {
        if (params != null)
            rootController.setContentView(view, params);
        else
            rootController.setContentView(view);
    }

    public void onPause() {
    }
    
    public void onStop() {
    }
    
    public void onStart(){
    }
    
	public void onDestroy() {
	}
    
    public class BizAsyncTask extends AsyncTask<String, Object, Object>{
        public String bizType;
        boolean useProgress = true;
        String loadingTip;//loading information

        public BizAsyncTask(String bizType) {
            super();
            this.bizType = bizType;
        }
        
        public BizAsyncTask(String bizType, boolean useProgress) {
            super();
            this.bizType = bizType;
            this.useProgress = useProgress;
        }
        
        public BizAsyncTask(String bizType, boolean useProgress,String loadingTip) {
            super();
            this.bizType = bizType;
            this.useProgress = useProgress;
            this.loadingTip = loadingTip;
        }

        public void onPublishProgress(Object... values){
        	publishProgress(values);
        }
        
        @Override
        protected void onProgressUpdate(Object... values) {
        	onUIUpdate(values);
        }
        
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            if (useProgress) {
            	if(loadingTip == null)
            		BaseViewController.this.showProgress();
            	else
            		BaseViewController.this.showProgress(loadingTip);
            }else{//不显示默认的对话框，自定义
            	onPreDoInbackgroud(bizType);
            }
        }

		@Override
        protected void onPostExecute(Object result) {
			super.onPostExecute(result);
            if (useProgress || autoDismiss) {
                BaseViewController.this.closeProgress();
            }
            
            BaseViewController.this.onPostExecute(this.bizType, result);
        }

        @Override
        protected Object doInBackground(String... arg0) {
            return BaseViewController.this.doInBackground(this.bizType, arg0);
        }
    }
    
    /**
     * Override this method to perform a computation on a background thread. The
     * specified parameters are the parameters passed to {@link #execute}
     * by the caller of this task.
     *
     * This method can call {@link #publishProgress} to publish updates
     * on the UI thread.
     *
     * @param bizType The additional flag to identify the task.
     * @param params  The parameters of the task.
     *
     * @return A result, defined by the subclass of this task.
     *
     * @see #onPreExecute()
     * @see #onPostExecute
     * @see #publishProgress
     */
    protected Object doInBackground(String bizType, String... params) {
        return null;
    }
    
    /**
     * Runs on the UI thread after {@link #doInBackground}. The
     * specified result is the value returned by {@link #doInBackground}
     * or null if the task was cancelled or an exception occured.
     *
     * @param bizType The additional flag to identify the task.
     * @param result  The result of the operation computed by {@link #doInBackground}.
     *
     * @see #onPreExecute
     * @see #doInBackground
     */
    protected void onPostExecute(String bizType, Object result) {
    } 
    
    protected ProgressDiv mProgress;
    protected boolean autoDismiss;
    public void showProgress() {
    	showProgress(rootController.getText(R.string.processing)+"");
    }
    
    /**
     * 能够自动消失的对话框
     */
    public void showProgress(boolean autoDismiss) {
    	this.autoDismiss = autoDismiss;
    	showProgress();
    }
    
    /**
     * 带自定义loading提示的对话框
     * @param loadingTip
     */
    public void showProgress(String loadingTip) {
    	if(mProgress == null || !mProgress.isShowing()){
    		try {
    			mProgress = new ProgressDiv(this.rootController);
    			mProgress.setTitle(null);
    			mProgress.setMessage(loadingTip);
    			mProgress.setIndeterminate(false);
    			mProgress.setCancelable(false);
    			mProgress.show();
    		} catch (Exception e) {
    			e.printStackTrace();
    		}
    	}
    }

    public void closeProgress() {
        try {
            if ((mProgress != null && mProgress.isShowing())){
                mProgress.dismiss();
            	mProgress = null;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    public void onActivityResult(int requestCode, int resultCode, Intent data){
    }
    
    /**
     * 异步之前自定义操作,需要将useProgress置为false
     * @param bizType 
     */
    protected void onPreDoInbackgroud(String bizType) {
	}
    
    protected void onUIUpdate(Object... values) {
    }
    
    public void intentCallBack(Object params) {
    	
    }

	public void onNewIntent() {
		
	}
}