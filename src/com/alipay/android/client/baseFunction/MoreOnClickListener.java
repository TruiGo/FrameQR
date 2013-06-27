package com.alipay.android.client.baseFunction;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Handler;

import com.alipay.android.appHall.common.Defines;
import com.alipay.android.client.AlipayApplication;
import com.alipay.android.client.Login;
import com.alipay.android.client.Main;
import com.alipay.android.client.RootActivity;
import com.alipay.android.client.advert.AdvertDetail;
import com.alipay.android.client.constant.AlipayHandlerMessageIDs;
import com.alipay.android.client.constant.Constant;
import com.alipay.android.client.util.BaseHelper;
import com.alipay.android.client.util.DBHelper;
import com.alipay.android.client.util.DataHelper;
import com.alipay.android.common.data.AdvertData;
import com.alipay.android.common.data.UserInfo;
import com.eg.android.AlipayGphone.R;

public class MoreOnClickListener {
    public static void onClick(int menuId, RootActivity context, DataHelper datahelper,
                               Handler handler) {
        switch (menuId) {
           case R.id.home:
        	   if (!(context instanceof Main)) {
        	        AlipayApplication application = (AlipayApplication) context.getApplicationContext();
        	        application.getMBus().destroy();
        	        Intent tIntent = new Intent(context, Main.class);
        	        tIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        	        context.startActivity(tIntent);
        	   }
        	   break;
            case R.id.menuLogout:
                jumpToLoginActivity(context);
                break;
            case R.id.setting_title:
            	jumpToSettingActivity(context);
            	break;
            case R.id.share:
                BaseHelper.Share(context, Constant.SHARE_APP, null);
                break;

            case R.id.help:
                jumpToHelp(context);
                break;

            case R.id.about:
                jumpToAbout(context);
                break;

//            case R.id.exitApp:
//                BaseHelper.exitProcess(context);
//                break;

            case R.id.update:
                /*StorageStateInfo storageStateInfo = StorageStateInfo.getInstance();
                //记录主动更新
                AlipayLogAgent.onEvent(context, Constants.MONITORPOINT_EVENT_CHECKUPDATE, "", "",
                    "", "", storageStateInfo.getValue(Constants.STORAGE_PRODUCTID),
                    context.getUserId(),
                    storageStateInfo.getValue(Constants.STORAGE_PRODUCTVERSION),
                    storageStateInfo.getValue(Constants.STORAGE_CLIENTID));*/

                try {
                    MoreOperationListener listener = (MoreOperationListener) context;
                    listener.startAsyncOperation();
                } catch (Exception e) {

                }
                jumpToUpdate(datahelper, handler);
                break;

            case R.id.userFeedback:
                jumpToFeedback(context);
                break;
            case R.id.recommend_app:
                jumpToRecommendApp(context);
                break;
            default:
                break;
        }
    }

    private static void jumpToSettingActivity(RootActivity context) {
    	Intent tIntent = new Intent(context, SettingActivity.class);
        context.startActivity(tIntent);
	}

	private static void jumpToLoginActivity(final RootActivity context) {
        if (context != null && !context.isFinishing()) {
        	context.getDataHelper().showDialog(context, R.drawable.infoicon, 
        			context.getResources().getString(R.string.WarngingString), 
        			context.getResources().getString(R.string.WantToLogOut), 
        			context.getResources().getString(R.string.Yes), 
        			new DialogInterface.OnClickListener() {

                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            //                        APHttpClient.clearSessionCookie();
                            Constant.GAS_JSON = null;
                            Constant.WATER_JSON = null;
                            Constant.ELECTRIC_JSON = null;
                            Constant.COMMUN_JSON = null;

                            if (context.getUserData() != null) {
                                BaseHelper.exitProcessOnClickListener.clearUserCache(context);
                                context.logoutUser();
                            }
                            
                            DBHelper db = new DBHelper(context);
                			UserInfo userInfo = db.getLastLoginUser(null);
                			if (null != userInfo) {
                				//重置登陆密码和自动登陆
                				db.resetRsaPassword(userInfo.userAccount, userInfo.type);
                				if (Constant.AUTOLOGIN_YES == db.getAutoLogin(userInfo.userAccount, userInfo.type)) {
                					//这里不删除自动登陆记录，只是将自动登陆状态改为unknow状态
                					db.saveAutoLogin(userInfo.userAccount, userInfo.type, Constant.AUTOLOGIN_UNKNOW);
                				}
                			}
                			db.close();
                            
                            Intent tIntent = new Intent(context, Login.class);
                            tIntent.putExtra(Defines.NextActivity, Main.class.getName());
                            tIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                            context.startActivity(tIntent);
                            
                            if (!(context instanceof Main)) {
                            	context.finish();
							}
                        }
                    }, context.getResources().getString(R.string.No), null, null, null);
        }
    }

    private static void jumpToHelp(final Activity context) {
        Intent tIntent = new Intent(context, AlipayHelp.class);
        context.startActivity(tIntent);
    }

    private static void jumpToAbout(final Activity context) {
        Intent tIntent = new Intent(context, AlipayAbout.class);
        context.startActivity(tIntent);
    }

    private static void jumpToFeedback(final Activity context) {
        Intent tIntent = new Intent(context, AlipayFeedBack.class);
        context.startActivity(tIntent);
    }

    private static void jumpToUpdate(DataHelper datehelper, Handler handler) {
        datehelper.sendUpdate(handler, AlipayHandlerMessageIDs.UPDATE);
    }

    private static void jumpToRecommendApp(final RootActivity context) {
        AlipayApplication application = (AlipayApplication) context.getApplicationContext();
        AdvertData advertData = application.getAdvertData();
        Intent intent = new Intent(context, AdvertDetail.class);
        intent.putExtra("url", advertData.getAppAd());
        intent.putExtra("title", context.getString(R.string.RecommendApp));
        context.startActivity(intent);

       /* StorageStateInfo storageStateInfo = StorageStateInfo.getInstance();
        AlipayLogAgent.onPageJump(context, 
           Constants.MOREVIEW,
           Constants.RECOMMANDVIEW, //jumpViewId
           "",//appId 
           "", //appVersion
            storageStateInfo.getValue(Constants.STORAGE_PRODUCTID),
            context.getUserId(),
//          Constant.STR_USERID,
            storageStateInfo.getValue(Constants.STORAGE_PRODUCTVERSION),
            storageStateInfo.getValue(Constants.STORAGE_CLIENTID));*/
    }
}
