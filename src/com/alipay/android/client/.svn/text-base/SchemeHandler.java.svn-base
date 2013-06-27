package com.alipay.android.client;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.widget.Toast;

import com.alipay.android.client.baseFunction.AlipayDealDetailInfoActivity;
import com.alipay.android.client.baseFunction.More;
import com.alipay.android.client.constant.Constant;
import com.alipay.android.core.MsgAction;
import com.alipay.android.ui.bill.BillManagerRootControllerActivity;
import com.alipay.android.ui.pushwebView.PushWebViewRootControllerActivity;
import com.alipay.android.ui.voucher.VoucherDetailActivity;
import com.alipay.android.ui.voucher.VoucherIndexActivity;
import com.eg.android.AlipayGphone.R;

public class SchemeHandler {
    public static final String PARAM = "param";
	public static final String SCHEMES = "alipays";
    public static final String SCHEME = "alipay";
    public static final String SCHEMEQR= "alipayqr";

    private static final String ACTION_START_APP = "startapp";
    private static final String ACTION_OPEN_URL = "openurl";
    private static final String ACTION_ADD_ALIPASS = "addalipass";
    
    public static final String PARAM_URL = "url";
    public static final String PARAM_TITLE = "title";
    
    private static final String PARAM_APP_ID = "appId";
    private static final String PARAM_VIEW_ID = "viewId";
    private static final String PARAM_SOURCE_ID = "sourceId";
    private static final String PARAM_VERSION  = "version";
    private static final String PARAM_APP_MOBILENO  = "mobileNo";
    private static final String PARAM_APP_USERNAME  = "userName";

    private Context mContext;

    public SchemeHandler(Context context) {
        mContext = context;
    }

    public void execute(Uri uri) {
        if (uri == null || Constant.mSafePayIsRunning)
            return;
        if (uri.getScheme() == null || !(uri.getScheme().equalsIgnoreCase(SCHEME)||uri.getScheme().equalsIgnoreCase(SCHEMES)||uri.getScheme().equalsIgnoreCase(SCHEMEQR)))
            return;
        AlipayApplication application = (AlipayApplication) mContext.getApplicationContext();
        Uri externUri = Uri.parse(Uri.decode(uri.toString()));
        String action = externUri.getPath().substring(1);
        String id = getAppId(externUri);
        String source = getAppSourceId(externUri);
        String version = getAppVersion(externUri);
        String viewId = getViewId(externUri);
        if (action.equalsIgnoreCase(ACTION_START_APP)) {
            if (id == null)
                return;
            if(!checkVersion(application,version)){
                Toast.makeText(mContext, R.string.upgrade_for_scheme, Toast.LENGTH_LONG).show();
                return;
            }
            if (id.equalsIgnoreCase("09999985")) {//消费记录
            	Intent intent = new Intent();
            	if(viewId == null || "".equals(viewId))
            		intent.setClass(mContext, BillManagerRootControllerActivity.class);
            	else if(viewId != null && "d".equals(viewId)){//消费记录详情
            		intent.setClass(mContext, AlipayDealDetailInfoActivity.class);
            		intent.putExtra(PARAM, externUri.getQuery());
            	}
            	
                mContext.startActivity(intent);
            } else if (id.equalsIgnoreCase("29999993")) {//菜单
                Intent intent = new Intent();
                intent.setClass(mContext, More.class);
                mContext.startActivity(intent);
            }else if(id.equalsIgnoreCase("09999989")){
            	 Intent intent = new Intent();
                 intent.setClass(mContext, VoucherIndexActivity.class);
                 mContext.startActivity(intent);
            }else {
                application.getMBus().sendMsg("", id, MsgAction.ACT_LAUNCH, externUri.getQuery());
            }
        }else if(action.equalsIgnoreCase(ACTION_OPEN_URL)){
        	String url = getParamsUrl(externUri);
			String title = getParamsTitle(externUri);
			if (url != null && url.length()!=0 && title != null && url.length()!=0) {
				Intent intent = new Intent();
				intent.putExtra(PARAM_URL, url);
				intent.putExtra(PARAM_TITLE, title);
				intent.setClass(mContext,
						PushWebViewRootControllerActivity.class);
				mContext.startActivity(intent);
			}
		}else if(action.equalsIgnoreCase(ACTION_ADD_ALIPASS)){
			Intent intent = new Intent();
			if(id.equalsIgnoreCase("09999987")){
                 intent.setClass(mContext, VoucherDetailActivity.class);
			}
			intent.putExtra(PARAM, externUri.getQuery());
			mContext.startActivity(intent);
		}
    }
    
    public String getParamsUrl(Uri uri){
    	return uri.getQueryParameter(PARAM_URL);
    }
    
    public String getParamsTitle(Uri uri){
    	return uri.getQueryParameter(PARAM_TITLE);
    }
    
    public String getParams(Uri uri){
    	return uri.getQuery();
    }
    
    public String getAppId(Uri uri){
        String ret = uri.getQueryParameter(Constant.SAID);
        if(ret==null){
            ret = uri.getQueryParameter(PARAM_APP_ID);
        }
    	return ret;
    }
    
    public String getViewId(Uri uri){
    	return uri.getQueryParameter(PARAM_VIEW_ID);
    }
    
    public String getAppVersion(Uri uri){
        String ret = uri.getQueryParameter(Constant.CLIENTVERSION);
        if(ret==null){
            ret = uri.getQueryParameter(PARAM_VERSION);
        }else{
            ret = "\""+ret+"-\"";
        }
    	return ret;
    }
    
    public String getAppSourceId(Uri uri){
    	return uri.getQueryParameter(PARAM_SOURCE_ID);
    }
    
    public String getAppMobileNo(Uri uri){
    	return uri.getQueryParameter(PARAM_APP_MOBILENO);
    }
    
    public String getUserName(Uri uri){
    	return uri.getQueryParameter(PARAM_APP_USERNAME);
    }

    private boolean checkVersion(AlipayApplication application,String version) {
        if(version==null)
            return true;
        String ver = application.getConfigData().getProductVersion();
        String paramVer = version.substring(1, version.length()-1);
        String[] vers = null;
        if(paramVer.contains("-")){
            int index = paramVer.indexOf("-");
            String minVer = paramVer.substring(0, index);
            if(minVer.length()<=0){
                minVer=ver;
            }
            String maxVer = paramVer.substring(index+1);
            if(maxVer.length()<=0){
                maxVer=ver;
            }
            if(ver.compareToIgnoreCase(minVer)>=0&&ver.compareToIgnoreCase(maxVer)<=0)
                return true;
        }else if(paramVer.contains(",")){
            vers = paramVer.split(",");
            for(String s:vers){
                if(s.equalsIgnoreCase(ver)){
                    return true;
                }
            }
        }
        return false;
    }
}
