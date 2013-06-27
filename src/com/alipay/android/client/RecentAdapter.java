/*
 * Copyright (C) 2008 Esmertec AG.
 * Copyright (C) 2008 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.alipay.android.client;

import java.util.ArrayList;

import android.content.Context;
import android.content.DialogInterface;
import android.database.Cursor;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.ResourceCursorAdapter;
import android.widget.TextView;

import com.alipay.android.client.util.DBHelper;
import com.alipay.android.client.util.TelephoneInfoHelper;
import com.alipay.android.common.data.UserInfo;
import com.alipay.android.comon.component.StyleAlertDialog;
import com.alipay.android.net.RequestMaker;
import com.alipay.android.net.http.HttpRequestMaker;
import com.alipay.platform.core.Command;
import com.alipay.platform.view.ActivityMediator;
import com.alipay.platform.view.OnDataChangeListener;
import com.eg.android.AlipayGphone.R;
public class RecentAdapter extends ResourceCursorAdapter implements OnDataChangeListener
{
	public static final String DELETE_USER_ID = "16";
    private RequestMaker mRequestMaker;
    
	View focusimage;
    public static final int ID = 0;
    public static final int NAME = 1;
    private final Context mContext;    
    private Login mLoginView;
    
    public RecentAdapter(Login login) 
    {
        super(login, R.layout.recent_filter_item, null, false /* no auto-requery */);

        mContext = login;
        mLoginView = login;
        
        mRequestMaker = new HttpRequestMaker(mContext,R.raw.interfaces);
    }

    @Override
    public final CharSequence convertToString(Cursor cursor)
    {
    	return cursor.getString(RecentAdapter.NAME);
    } 

    @Override
    public final void bindView(View view, Context context, Cursor cursor) 
    {
    	mLoginView.mLinearLayoutforDropDown = view;
    	
        TextView name = (TextView) view.findViewById(R.id.name);
        name.setText(cursor.getString(RecentAdapter.NAME));

        ImageView image = (ImageView) view.findViewById(R.id.DeleteImage);
        image.setTag(cursor.getString(RecentAdapter.NAME));
        image.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
            focusimage = v;
            StyleAlertDialog dialog = new StyleAlertDialog(v.getContext(), R.drawable.infoicon,
            		v.getContext().getResources().getString(R.string.WarngingString),
            		v.getContext().getResources().getString(R.string.DeleteAccountMessage),
            		v.getContext().getResources().getString(R.string.Ensure),new DialogInterface.OnClickListener() {
    					@Override public void onClick(DialogInterface dialog,
    							int which)
    					{
    						mLoginView.db.open(mContext);
    				        if(mLoginView.mTaobao) {        	
    				        	requestUpdateSettings(focusimage.getTag().toString(), mLoginView.LOGINTYPE_TAOBAO);
    				        	mLoginView.db.delete(focusimage.getTag().toString(), mLoginView.LOGINTYPE_TAOBAO, true);
    				        } else{
    				        	requestUpdateSettings(focusimage.getTag().toString(), mLoginView.LOGINTYPE_ALIPAY);
    				        	mLoginView.db.delete(focusimage.getTag().toString(), mLoginView.LOGINTYPE_ALIPAY, true);
    				     //   	mLoginView.db.delete(focusimage.getTag().toString(), mLoginView.LOGINTYPE_SAFEPAY);
    				        }

    				        mLoginView.onUserDelete();
    					}
    				},
    				v.getContext().getResources().getString(R.string.Cancel),null, null);
                    dialog.show();
            
//       		AlertDialog.Builder tDialog = new AlertDialog.Builder(v.getContext());
//    		tDialog.setIcon(R.drawable.infoicon);
//    		tDialog.setTitle(R.string.DeleteAccountTitle);
//    		tDialog.setMessage(R.string.DeleteAccountMessage);
//    		tDialog.setPositiveButton(R.string.Ensure,
//    				new DialogInterface.OnClickListener() {
//    					@Override public void onClick(DialogInterface dialog,
//    							int which)
//    					{
//    						mLoginView.db.open(mContext);
//    				        if(mLoginView.mTaobao) {        	
//    				        	requestUpdateSettings(focusimage.getTag().toString(), mLoginView.LOGINTYPE_TAOBAO);
//    				        	mLoginView.db.delete(focusimage.getTag().toString(), mLoginView.LOGINTYPE_TAOBAO);
//    				        } else{
//    				        	requestUpdateSettings(focusimage.getTag().toString(), mLoginView.LOGINTYPE_ALIPAY);
//    				        	mLoginView.db.delete(focusimage.getTag().toString(), mLoginView.LOGINTYPE_ALIPAY);
//    				     //   	mLoginView.db.delete(focusimage.getTag().toString(), mLoginView.LOGINTYPE_SAFEPAY);
//    				        }
//
//    				        mLoginView.onUserDelete();
//    					}
//    				});
//    		tDialog.setNegativeButton(R.string.Cancel,
//    				new DialogInterface.OnClickListener() {
//    					@Override public void onClick(DialogInterface dialog,
//    							int which)
//    					{
//    					}
//    				});
//    		tDialog.show();
            }
        });
    }

    @Override
    public Cursor runQueryOnBackgroundThread(CharSequence constraint) {
    	
        String cons = null;
        if (constraint != null)
            cons = constraint.toString();    	
        mLoginView.db.open(mContext);
        String type;
        if(mLoginView.mTaobao)        	
        	type = mLoginView.LOGINTYPE_TAOBAO;
        else
        	type = mLoginView.LOGINTYPE_ALIPAY;
        
        Cursor cursor= null;
        try{
    	if(null == constraint)
    	{
    		cursor = mLoginView.db.loadData(null, type, String.valueOf(DBHelper.loginUser_maxRows).toString());
    	}
    	else
    	{
    		cursor = mLoginView.db.loadData(cons + "%", type, String.valueOf(DBHelper.loginUser_maxRows).toString());	
    	}
    	}catch(Exception e){
    		e.printStackTrace();
    	}
    	
    	return cursor;
    	
    }
    
    private void requestUpdateSettings(String userName, String type){
    	//获取最新用户
		UserInfo firstUserInfo = mLoginView.db.getLastLoginUser(null);
		
		//获取当前用户
		UserInfo curUserInfo = mLoginView.db.getUser(userName, type);
	    String curUserId = curUserInfo.userId;
	    
	    //判断是否为最新用户
	    if (!userName.equals(firstUserInfo.userAccount) && !type.equals(firstUserInfo.type)) {
	    	//不是最新用户，不再上报服务端
	    	return;
	    }
		
	    ActivityMediator activityMediator = new ActivityMediator(this);
	    ArrayList<String> params = new ArrayList<String>();

	    params.add(curUserId);// userId
	    
	    TelephoneInfoHelper mTelephoneInfoHelper = TelephoneInfoHelper.getTelephoneHelper(mContext);
	    params.add(mTelephoneInfoHelper.getProductId());//ProductId
	    params.add(mTelephoneInfoHelper.getProductVersion());//ProductVersion
	    params.add("");// prevUserId
	    
	    AlipayApplication application = (AlipayApplication) mContext.getApplicationContext();
	    String sessionId = application.getConfigData().getSessionId();
	    params.add(sessionId);// sessionId
        params.add(mTelephoneInfoHelper.getClientID());// clientID
	    
	    activityMediator.sendCommand(DELETE_USER_ID,  "clean", mRequestMaker, params);

	}

	@Override
	public Context getContext() {
		// TODO Auto-generated method stub
		return RecentAdapter.this.mContext;
	}

	@Override
	public String getRuleId() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void onCancel(Command command) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onComplete(Command command) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onFail(Command command) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public boolean preCancel(Command command) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean preFail(Command command) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void setRuleId(String ruleId) {
		// TODO Auto-generated method stub
		
	}
}
