/**
 * 
 */
package com.alipay.android.appHall.bussiness;

import java.util.HashMap;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;

import com.alipay.android.appHall.common.Defines;
import com.alipay.android.client.AlipayAgentAnylink;
import com.alipay.android.client.AlipayAgentSomeone;
import com.alipay.android.client.AlipayUserInfoSupplement;
import com.alipay.android.client.Login;
import com.alipay.android.client.RootActivity;
import com.alipay.android.client.SubItemCardActivity;
import com.alipay.android.client.baseFunction.AlipayAccountBindingChoice;
import com.alipay.android.client.baseFunction.AlipayDealDetailInfoActivity;
import com.alipay.android.client.baseFunction.UpdateHelper;
import com.alipay.android.client.constant.Constant;
import com.alipay.android.client.util.BaseHelper;
import com.alipay.android.client.util.Utilz;
import com.eg.android.AlipayGphone.R;

/**
 * @author sanping.li
 *
 */
public class CallBackFilter {
    private RootActivity mActivity;
    int mStatus;

    public CallBackFilter(RootActivity activity) {
        mActivity = activity;
    }

    public boolean processCommand(HashMap<String, Object> map) {
    	//快捷支付结果处理
    	Object pay_result = map.get(Constant.RQ_PAY);
    	if(pay_result != null){
    		Object resultStatus = map.get(Defines.resultStatus);
    		if(resultStatus == null){
    			return true;
    		}else{
    			return !(Utilz.parseInt(resultStatus.toString()) == 9000);
    		}
    	}else{
    		mStatus = Utilz.parseInt(map.get(Defines.resultStatus).toString());
    	}
        
        String errorText = null;
        try {
        	Object memoObj = map.get(Defines.memo);
        	if(memoObj != null){
        		errorText = memoObj.toString();
        	}
        } catch (Exception e) {
            e.printStackTrace();
        }

        switch (mStatus) {
            case 100: {
                String string = (String) map.get(Constant.RPF_SESSION_ID);
                mActivity.setSessionId(string);
                }
                return false;

            case 200:
                //登陆超时
                backToLogin(errorText);
                return true;
            case 202:
            case 203:
                // 更新
                updateMethod(map, errorText, 203 == mStatus);
                return true;
            case 201:
                // 检测更新
                showNoUpdate(errorText);
                return true;
            case 411:
                //补全资料
                String tLoginId = (String) map.get(Constant.RQF_LOGON_ID);
                Intent tIntent = new Intent(mActivity, AlipayUserInfoSupplement.class);
                if (tLoginId != null && !tLoginId.equals("")) {
                    tIntent.setData(Uri.parse(tLoginId));
                }
                mActivity.startActivity(tIntent);
                return true;
            case 116:
            	String string = (String) map.get(Constant.RPF_SESSION_ID);
                mActivity.setSessionId(string);
            	mActivity.getDataHelper().showDialog(mActivity,R.drawable.infoicon,mActivity.getResources().getString(R.string.WarngingString), 
            			mActivity.getString(R.string.TaoBaoBindAlipayTip), mActivity.getResources().getString(R.string.Ensure),new DialogInterface.OnClickListener() {
							@Override
							public void onClick(DialogInterface dialog,
									int which) {
								mActivity.startActivity(new Intent(mActivity, AlipayAccountBindingChoice.class));
							}
						}, null, null, null, null);
                
                return true;
            case 129:
            	//密码错误5次，账户被锁
            	mActivity.getDataHelper().showDialog(mActivity, R.drawable.erroricon, mActivity.getString(R.string.ErrorString), errorText,
            			mActivity.getString(R.string.GetLoginPassword), new DialogInterface.OnClickListener() {
							@Override
							public void onClick(DialogInterface dialog, int which) {
	                    		if (mActivity instanceof Login) {
	                    			//找回登录密码
									((Login) mActivity).jumpToRetrieve();
								}
							}
						}, 
            			mActivity.getString(R.string.DialogCancel), new DialogInterface.OnClickListener() {
							@Override
							public void onClick(DialogInterface dialog, int which) {
	                    		if (mActivity instanceof Login) {
	                    			//跳转到首页
									((Login) mActivity).doAfterFailedtoLogin();
								}
							}
						}, null, null);
				
            	return true;
            default:
                String tTitle = mActivity.getResources().getString(R.string.ErrorString);
                String error = errorText;
                int iconID = R.drawable.erroricon;

                if (mStatus == 101) {
                    iconID = R.drawable.infoicon;
                    tTitle = mActivity.getResources().getString(R.string.WarngingString);
                } else if (mStatus == 1) {
                    error = mActivity.getResources().getString(R.string.WarningDataCheck);
                } else if (mStatus > 2000) {
                    iconID = R.drawable.infoicon;
                    tTitle = mActivity.getResources().getString(R.string.WarngingString);
                } else if(mStatus == 0){
                	error = mActivity.getResources().getString(R.string.CheckNetwork);
                }
                try {
                    mActivity.getDataHelper().showDialog(mActivity, iconID, tTitle, error, mActivity.getResources()
                        .getString(R.string.Ensure), new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                        	//wrong password
                        	if(mStatus == 103 || mStatus == 302){
                        		if (mActivity instanceof Login) {
									((Login) mActivity).clearPassword();
								}
                        	}else{
                        		ensureBtnCheck(mStatus);
                        	}
                        	
                    		if (mActivity instanceof Login) {
								((Login) mActivity).showLogin();
							}
                        }
                    }, null, null, null, null);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                return true;
        }
    }

    private void ensureBtnCheck(int mStatus) {
        String className = mActivity.getComponentName().getClassName();

        if ((mStatus == 0) || (mStatus == 1)) {
            networkAbnormalProcess(0);
        } else if (mStatus == 501 || mStatus == 901 || mStatus == 302 || mStatus == 199) //代付申请失败
        {
            if (AlipayDealDetailInfoActivity.class.getCanonicalName().equalsIgnoreCase(className)) {
                mActivity.setResult(Activity.RESULT_FIRST_USER);
                mActivity.finish();
            }
        } else if (mStatus == 604) //查询交易详情出错
        {
            if (AlipayDealDetailInfoActivity.class.getCanonicalName().equalsIgnoreCase(className)) {
                mActivity.setResult(Activity.RESULT_FIRST_USER);
                mActivity.finish();
            }
        } else if (mStatus == 199 || mStatus == 714 || mStatus == 715 || mStatus == 716
                   || mStatus == 720 || mStatus == 721 || mStatus == 722 || mStatus == 723) //代付申请错误处理
        {
            if (AlipayAgentSomeone.class.getCanonicalName().equalsIgnoreCase(className)
                || AlipayAgentAnylink.class.getCanonicalName().equalsIgnoreCase(className)) {
                mActivity.setResult(Activity.RESULT_FIRST_USER);
                mActivity.finish();
            }
        } else if (mStatus == 119) {
            if (SubItemCardActivity.class.getCanonicalName().equalsIgnoreCase(className)) {
                networkAbnormalProcess(0);
            }
        } else if (mStatus == 125) {
            mActivity.finish();
        }

    }

    private void backToLogin(String memo) {
        mActivity.getDataHelper().showDialog(mActivity, R.drawable.infoicon, mActivity.getResources()
            .getString(R.string.WarngingString), memo, mActivity.getResources().getString(R.string.Ensure),new DialogInterface.OnClickListener() {
            @Override public void onClick(DialogInterface dialog,
                    int which)
            {
                Intent tIntent = new Intent(mActivity, Login.class);
                tIntent.putExtra("", mActivity.getClass());
                mActivity.startActivityForResult(tIntent, Constant.REQUEST_LOGIN_BACK);
            }
        }, null, null, null, null);
        mActivity.getDataHelper().setDialogCancelable(false);
    }

    private void updateMethod(final HashMap<String, Object> map, String MessageText, final boolean isForce) {
    	
    	mActivity.getDataHelper().showDialog(mActivity, R.drawable.infoicon,
    			mActivity.getResources().getString(R.string.WarngingString),MessageText,
    			mActivity.getResources().getString(R.string.Update), new DialogInterface.OnClickListener() {
    	            @Override
    	            public void onClick(DialogInterface dialog, int which) {
    	                Object object = map.get(Constant.RPF_DOWNLOAD_URL);
    	                if (object != null) {
    	                    String url = (String) object;
    	                    UpdateHelper myUpdator = new UpdateHelper(mActivity);
    	                    myUpdator.update(url);
    	                }
    	            }
    	        }, 
    	        mActivity.getResources().getString(isForce ? R.string.MenuExit : R.string.next_time), 
    	        new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						if (isForce) {
							//强制升级退出客户端
							BaseHelper.exitProcessSilently(mActivity);
						}
					}
				}, null, null);

//        AlertDialog.Builder tDialog = new AlertDialog.Builder(mActivity);
//        tDialog.setIcon(R.drawable.infoicon);
//        tDialog.setTitle(mActivity.getResources().getString(R.string.WarngingString));
//        tDialog.setMessage(Html.fromHtml(MessageText));
//        tDialog.setPositiveButton(R.string.Yes, new DialogInterface.OnClickListener() {
//            @Override
//            public void onClick(DialogInterface dialog, int which) {
//                Object object = map.get(Constant.RPF_DOWNLOAD_URL);
//                if (object != null) {
//                    String url = (String) object;
//                    UpdateHelper myUpdator = new UpdateHelper(mActivity);
//                    myUpdator.update(url);
//                }
//            }
//        });
//        tDialog.setNegativeButton(R.string.No, new DialogInterface.OnClickListener() {
//            @Override
//            public void onClick(DialogInterface dialog, int which) {
//
//            }
//        });
//        tDialog.show();
    }

    private void showNoUpdate(String MessageText) {
    	
       	mActivity.getDataHelper().showDialog(mActivity, 0,
    			mActivity.getResources().getString(R.string.WarngingString),MessageText,
    			mActivity.getResources().getString(R.string.Ensure), null, 
    	        null, null, null, null);

    	
    	
//        AlertDialog.Builder tDialog = new AlertDialog.Builder(mActivity);
//        tDialog.setTitle(mActivity.getString(R.string.WarngingString));
//        tDialog.setMessage(Html.fromHtml(MessageText));
//        tDialog.setPositiveButton(R.string.Ensure, null);
//        tDialog.show();
    }

    private void networkAbnormalProcess(final int delayTime) {
        //        new Thread(new Runnable() 
        //        {
        //            @Override public void run(){
        //                // Check the current activity. if not login or main. need back
        //                // to main.
        //                String className = mActivity.getComponentName().getClassName();
        //                if(!AppHallActivity.class.getCanonicalName().equalsIgnoreCase(className)&&
        //                        !Login.class.getCanonicalName().equalsIgnoreCase(className)&&
        //                        !AlipayRegister.class.getCanonicalName().equalsIgnoreCase(className)&&
        //                        !AlipayAccountManage.class.getCanonicalName().equalsIgnoreCase(className)&&
        //                        !SubItemDealQueryActivity.class.getCanonicalName().equalsIgnoreCase(className)&&
        //                        !AlipayAgentSomeone.class.getCanonicalName().equalsIgnoreCase(className))
        //                {
        //                    if(SubItemPucPayActivity.class.getCanonicalName().equalsIgnoreCase(className)
        //                            )
        //                    {
        //                        if(Constant.isLogin){
        //                            if(AlipayNavigationTabActivity.sRef != null){
        //                                AlipayNavigationTabActivity.sRef.finish();
        //                            }
        //                            Intent intent = new Intent(mActivity, AlipayNavigationTabActivity.class);
        //                            intent.putExtra(Defines.CURRENT_TAB, Defines.TAB_INDEX_ALIPAY);
        //                            mActivity.startActivity(intent);
        //                        }
        //                    }
        //                    BaseHelper.showDesktop(mActivity);
        //                }
        //
        //            }
        //        }).start();
    }
}
