/**
 * 
 */
package com.alipay.android.core;

import java.util.Arrays;
import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;

import com.alipay.android.appHall.appManager.HallData;
import com.alipay.android.bizapp.CCR.CCRActivity;
import com.alipay.android.client.AlipayApplication;
import com.alipay.android.client.AlipayBarcodeDisplay;
import com.alipay.android.client.AlipaySystemMsgDetail;
import com.alipay.android.client.LifePayHistoryActivity;
import com.alipay.android.client.Login;
import com.alipay.android.client.Main;
import com.alipay.android.client.SubItemPucPayActivity;
import com.alipay.android.client.baseFunction.AlipayDealDetailInfoActivity;
import com.alipay.android.client.baseFunction.MessageList;
import com.alipay.android.client.constant.Constant;
import com.alipay.android.client.webapp.webActivity;
import com.alipay.android.log.AlipayLogAgent;
import com.alipay.android.log.Constants;
import com.alipay.android.ui.bill.BillManagerRootControllerActivity;
import com.alipay.android.ui.quickpay.QuickPayRootControllerActivity;
import com.alipay.android.ui.transfer.TransferRootControllerActivity;
import com.alipay.android.ui.voucher.VoucherDetailActivity;
import com.alipay.android.ui.voucher.VoucherIndexActivity;
import com.eg.android.AlipayGphone.R;

/**
 * @author sanping.li
 * 加载本地业务，历史遗留的程序
 *
 */
public class NativeLoader {
    private AlipayApplication mContext;
    private Engine mSource;
    private String mPkgId;

    public NativeLoader(Context context, Engine source, String pkgId) {
        mContext = (AlipayApplication) context.getApplicationContext();
        mSource = source;
        mPkgId = pkgId;
    }

    public boolean load(String params) {
        Intent tIntent = null;
        String jumpViewId = "";
        int index = findIndex(mPkgId);
        if (index == -1)
            return false;
        final Activity activity = mContext.getActivity();
    	int name = HallData.mLagacyAppsName[index];
    	
    	ParamString paramString = new ParamString(params);
    	String viewId = paramString.getValue("viewId");
         
        switch (name) {
//        // nfc 入口
//    		case R.string.nfcEntrance:
//	    		if (mContext.getUserData() != null) {
//	    			tIntent = new Intent();
//	        		tIntent.setAction(Intent.ACTION_MAIN);
//	        		tIntent.setClassName("com.alipay.android.app", "com.alipay.android.app.activity.Manager");
//	            } else {
//	                    Intent intent = new Intent(activity, Login.class);
//	                    activity.startActivity(intent);
//	                    return true;
//	            }
//	    		break;
	        
            case R.string.WaterRate:
            	tIntent = setPucIntent(tIntent, activity, viewId);
                tIntent.putExtra(Constant.PUBLIC_PAY_URL, Constant.URL_WATER_RATE);
                tIntent.putExtra(Constant.JUMP_HISTORY_FROM, Constant.JUMP_HISTORY_FROM_HALL);
                jumpViewId = Constants.WATERINPUTVIEW;
                break;

            case R.string.PowerRate:
            	tIntent = setPucIntent(tIntent, activity, viewId);
                tIntent.putExtra(Constant.PUBLIC_PAY_URL, Constant.URL_POWER_RATE);
                tIntent.putExtra(Constant.JUMP_HISTORY_FROM, Constant.JUMP_HISTORY_FROM_HALL);
                jumpViewId = Constants.ELECTRICITYINPUTVIEW;
                break;

            case R.string.GasRate:
            	tIntent = setPucIntent(tIntent, activity, viewId);
                tIntent.putExtra(Constant.PUBLIC_PAY_URL, Constant.URL_GAS_RATE);
                tIntent.putExtra(Constant.JUMP_HISTORY_FROM, Constant.JUMP_HISTORY_FROM_HALL);
                jumpViewId = Constants.GASINPUTVIEW;
                break;

            case R.string.PhoneAndWideBand:
            	tIntent = setPucIntent(tIntent, activity, viewId);
                tIntent.putExtra(Constant.PUBLIC_PAY_URL, Constant.URL_COMMUN_RATE);
                tIntent.putExtra(Constant.JUMP_HISTORY_FROM, Constant.JUMP_HISTORY_FROM_HALL);
                jumpViewId = Constants.WIDELINEINPUTVIEW;
                break;
            case R.string.BarcodePay:
                //直接进入到金额输入页面
                tIntent = new Intent(activity, AlipayBarcodeDisplay.class);
                jumpViewId = Constants.BARCODEVIEW;
                break;
            case R.string.app_lottery:
                if (mContext.getUserData() != null) {
                    tIntent = new Intent(activity, webActivity.class);
                    tIntent.putExtra(webActivity.WEBAPP_URL, Constant.getLotteryUrl(activity)
                                                             + "?sessionId="
                                                             + mContext.getConfigData()
                                                                 .getSessionId());
                } else {
                    tIntent = new Intent(activity, Login.class);
                    tIntent.putExtra(Constant.OP_RETURNURL, webActivity.WEBAPP);
                }
                tIntent.putExtra(webActivity.WEBAPP_NAME, activity.getString(R.string.app_lottery));
                break;
            case R.string.CCRApp:
                tIntent = new Intent(activity, CCRActivity.class);
                break;
            case R.string.super_transfer:
        		tIntent = new Intent(activity,TransferRootControllerActivity.class);
                break;
            case R.string.voucherIndex:
            	tIntent = new Intent(activity,VoucherIndexActivity.class);
            	break;
            case R.string.voucherDetail:
            	tIntent = new Intent(activity,VoucherDetailActivity.class);
            	break;
            case R.string.recordListView:
            	tIntent = new Intent(activity, BillManagerRootControllerActivity.class);
            	break;
		    case R.string.quickpay:
            	tIntent = new Intent(activity, QuickPayRootControllerActivity.class);
            	break;
            	
            case R.string.ConsumptionRecordDetailTitle:
            	tIntent = new Intent(activity, AlipayDealDetailInfoActivity.class);
            	break;
            case R.string.SystemMsg:
            	if(viewId == null || "".equals(viewId)){//系统消息
            		tIntent = new Intent(activity, MessageList.class);
            	}else if(viewId != null && "d".equals(viewId)){//系统消息详情
            		tIntent = new Intent(activity, AlipaySystemMsgDetail.class);
            	}
            	break;
            default:
                return false;
        }
        if (params != null && mSource != null) {
            params += "&_source_" + mSource.getPkgId();
        } else if (mSource != null) {
            params = "_source_" + mSource.getPkgId();
        }
        tIntent.putExtra("param", params);

        if (activity instanceof Main) {
        	Main mainActivity = (Main)activity;
        	mainActivity.SetRefreshUserStatus(false);
        }
        activity.startActivity(tIntent);
        //彩票和信用卡还款应用在onCreate方法中单独处理
        if (R.string.app_lottery != name) {
        	if (!mPkgId.equals("09999989")&&!mPkgId.equals("09999988")&&!mPkgId.equals("09999999")) {
        		AlipayLogAgent.writeLog(mContext.getActivity(), Constants.BehaviourID.BIZLAUNCHED, null, null, mPkgId, mSource == null ? "" : mSource.getPkgVersion(), mPkgId+"Home", Constants.APPCENTER, mPkgId+"Icon", null);
			}
        }
        return true;
    }
    

	private Intent setPucIntent(Intent tIntent, final Activity activity, String viewId) {
		if(viewId == null || "".equals(viewId)){
			tIntent = new Intent(activity, LifePayHistoryActivity.class);
		}else if(viewId != null && "i".equals(viewId)){
			tIntent = new Intent(activity, SubItemPucPayActivity.class);
		}
		return tIntent;
	}

    private int findIndex(String id) {
        for (int i = 0; i < HallData.mLagacyAppsId.length; ++i) {
            String str = HallData.mLagacyAppsId[i];
            if (str.equalsIgnoreCase(id)) {
                return i;
            }
        }
        return -1;
    }
}
