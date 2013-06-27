package com.alipay.android.bizapp;

import java.util.Stack;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnKeyListener;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.Html;
import android.view.KeyEvent;
import android.view.View;

import com.alipay.android.client.AlipayApplication;
import com.alipay.android.client.Login;
import com.alipay.android.client.Main;
import com.alipay.android.client.RootActivity;
import com.alipay.android.client.SubItemPucPayActivity;
import com.alipay.android.client.constant.Constant;
import com.alipay.android.comon.component.ProgressDiv;
import com.alipay.android.log.AlipayLogAgent;
import com.alipay.android.log.Constants;
import com.alipay.android.log.StorageStateInfo;
import com.eg.android.AlipayGphone.R;

public abstract class BaseAppActivity extends RootActivity implements BizCallBack{
	public Stack<View> mPageViewList=null;
	ProgressDiv mProgressDialog = null;
	BasePage mActivePage = null;
	
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		mPageViewList = new Stack<View>();
	}
	/*
	 * 将视图压入栈中
	 * 
	 */
	public void pushViewToStack(View view, BasePage basePage, boolean toTop){
		if(toTop){
			mPageViewList.clear();
		}
		mActivePage = basePage;
		mPageViewList.push(view);
		basePage.onResume();
	}
	/*
	 * 将视图弹出栈，并返回最后一个视图
	 * 
	 */
	public View popViewStack(){
		if(mPageViewList.size()>0){
			mPageViewList.pop();
			if(mPageViewList.size()>0)
				return mPageViewList.lastElement();
			else 
				return null;
		}
		return null;
	}
	
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		boolean bRet = false;
		if(mActivePage!=null)
        	bRet = mActivePage.onKeyDown(keyCode, event);
        if (!bRet&&keyCode == KeyEvent.KEYCODE_BACK) {
        	View backView = popViewStack();
        	if(backView!=null){
        		setContentView(backView);
        	}else{
//        		gotoAppHallActivity();
        		finish();
        	}
        	bRet = true;
        }
        return bRet;
    }
	public ProgressDiv showProgressDialog(Context context, CharSequence title, CharSequence message) {
        try {
        	mProgressDialog = new ProgressDiv(context);
            mProgressDialog.setTitle(title);
            mProgressDialog.setMessage(message);
            mProgressDialog.setIndeterminate(false);
            mProgressDialog.setCancelable(false);
            mProgressDialog.show();
            return mProgressDialog;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
	
	public void dismissProgressDialog(){
		if(mProgressDialog!=null){
			mProgressDialog.dismiss();
			mProgressDialog = null;
		}
	}
	public void updateProgressDialog(CharSequence title, CharSequence message){
		if(mProgressDialog!=null&&mProgressDialog.isShowing()){
			mProgressDialog.setTitle(title);
	        mProgressDialog.setMessage(message);
	        mProgressDialog.show();
		}
	}
	
	/**
	 * 新的客户端中对对话框样式有统一的定义，此方法将不再使用。
	 * */
	public void showDialog(int iconRes, String titleText, String MessageText,
            String btn1Msg, DialogInterface.OnClickListener btn1, String btn2Msg,
            DialogInterface.OnClickListener btn2, String btn3Msg,
            DialogInterface.OnClickListener btn3) {
        // Log.i(TAG, "show error message.");
        try {
            if (!isFinishing()) {
                AlertDialog.Builder tDialog = new AlertDialog.Builder(this);
                if (iconRes != -1) {
                    tDialog.setIcon(iconRes);
                }

                tDialog.setTitle(titleText);
                tDialog.setMessage(Html.fromHtml(MessageText));

                if (btn1Msg != null) {
                    tDialog.setPositiveButton(btn1Msg, btn1);
                    tDialog.setCancelable(false);
                }
                if (btn2Msg != null) {
                    tDialog.setNeutralButton(btn2Msg, btn2);
                }
                if (btn3Msg != null) {
                    tDialog.setNegativeButton(btn3Msg, btn3);
                }
                tDialog.setOnKeyListener(new OnKeyListener() {

                    @Override
                    public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event) {
                        if (keyCode == KeyEvent.KEYCODE_SEARCH) {
                            return true;
                        }
                        return false;
                    }

                });
                tDialog.show();
            }
        } catch (Exception e) {

        }
    }
	public abstract void loadPages();
	public abstract void JumptoPage(int id, boolean toTop);
	
	public void jumpPageLog(String viewId){
		/*StorageStateInfo storageStateInfo = StorageStateInfo.getInstance();
		AlipayLogAgent.onPageJump(this, 
          		 storageStateInfo.getValue(Constants.STORAGE_CURRENTVIEWID),
          		 	 viewId,
            		 storageStateInfo.getValue(Constants.STORAGE_APPID), //appID
            		 storageStateInfo.getValue(Constants.STORAGE_APPVERSION), //AppVersionID
            		 storageStateInfo.getValue(Constants.STORAGE_PRODUCTID),
            		 getUserId(),
	         		 storageStateInfo.getValue(Constants.STORAGE_PRODUCTVERSION),
	         		 storageStateInfo.getValue(Constants.STORAGE_CLIENTID));*/
	}
	
	public void backKeyLog(String viewId){
		/*StorageStateInfo storageStateInfo = StorageStateInfo.getInstance();
		AlipayLogAgent.onEvent(this, 
    			Constants.MONITORPOINT_EVENT_VIEWRETURN, 
    			"", 
    			viewId, 
    			storageStateInfo.getValue(Constants.STORAGE_APPID), 
    			storageStateInfo.getValue(Constants.STORAGE_APPVERSION),
    			storageStateInfo.getValue(Constants.STORAGE_PRODUCTID),
    			getUserId(),
       		 	storageStateInfo.getValue(Constants.STORAGE_PRODUCTVERSION),
       		 	storageStateInfo.getValue(Constants.STORAGE_CLIENTID));*/
	}
	
	public void backToLogin(int iconRes, String titleText, String MessageText)
	{
	    AlipayApplication application = (AlipayApplication) this.getApplicationContext();
		application.logoutUser();
		
		String className = this.getComponentName().getClassName();
		if(application.getUserData()==null && (SubItemPucPayActivity.class.getCanonicalName().equalsIgnoreCase(className) 
				))
		{
			Intent tIntent = new Intent(this, Login.class);
			tIntent.setData(Uri.parse(className));
			this.startActivityForResult(tIntent, Constant.REQUEST_LOGIN_BACK);
		}
		else
		{
			try 
			{
				if ( !this.isFinishing())
				{
					application.getDataHelper().showDialog(this, iconRes, titleText, MessageText, getResources().getString(R.string.Ensure),new DialogInterface.OnClickListener() {
									@Override public void onClick(DialogInterface dialog,
											int which)
									{
										// TODO Auto-generated method stub
										Intent tIntent = new Intent(BaseAppActivity.this, Login.class);
										BaseAppActivity.this.startActivityForResult(tIntent, Constant.REQUEST_LOGIN_BACK);
										BaseAppActivity.this.finish();
									}
								}, null, null, null, null);
					
			        /*StyleAlertDialog dialog = new StyleAlertDialog(this, iconRes, titleText,MessageText,
			        		 getResources().getString(R.string.Ensure),new DialogInterface.OnClickListener() {
									@Override public void onClick(DialogInterface dialog,
											int which)
									{
										// TODO Auto-generated method stub
										Intent tIntent = new Intent(BaseAppActivity.this, Login.class);
										BaseAppActivity.this.startActivityForResult(tIntent, Constant.REQUEST_LOGIN_BACK);
										BaseAppActivity.this.finish();
									}
								},null,null, null);*/
					application.getDataHelper().setDialogCancelable(false);
			        	/*dialog.getDialog().setCancelable(false);
			            dialog.show();*/
					
//					AlertDialog.Builder tDialog = new AlertDialog.Builder(this);
//					tDialog.setIcon(iconRes);
//					tDialog.setTitle(titleText);
//					tDialog.setMessage(MessageText);
//					tDialog.setPositiveButton(R.string.Ensure,
//							new DialogInterface.OnClickListener() {
//						@Override public void onClick(DialogInterface dialog,
//								int which)
//						{
//							// TODO Auto-generated method stub
//							Intent tIntent = new Intent(BaseAppActivity.this, Login.class);
//							BaseAppActivity.this.startActivity(tIntent);
//							BaseAppActivity.this.finish();
//						}
//					});
//					tDialog.show();
				}
			} 
			catch (Exception e) 
			{
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
	
	/**
	 * 跳转到AppHallActivity
	 */
	public void gotoAppHallActivity() {
		Intent intent = new Intent(this, Main.class);
		intent.setAction(Intent.ACTION_MAIN);
		intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
		startActivity(intent);
		finish();
	}
}
