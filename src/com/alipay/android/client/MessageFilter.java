/*
 * Copyright 2009, Embedded General Inc.  All right reserved
 * 
 * draft:Robin
 * Modify: Roger
 * 
 */
package com.alipay.android.client;

/*
 *  Interface between UI and Transmission
 *  
 *  draft: dingding.bx.jiang@embeddedgeneral.com
 *  
 *  TODO: I think we should have a manager, because LogonId... are shared and we should maintain a simple status machine
 *  
 */

import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Handler;
import android.os.Message;

import com.alipay.android.appHall.common.CacheSet;
import com.alipay.android.appHall.component.accountbox.AlipayContact;
import com.alipay.android.client.baseFunction.AlipayAccountBindingChoice;
import com.alipay.android.client.baseFunction.AlipayDealDetailInfoActivity;
import com.alipay.android.client.baseFunction.AlipayGetLoginPassword;
import com.alipay.android.client.baseFunction.AlipayPhoneBinding;
import com.alipay.android.client.baseFunction.UpdateHelper;
import com.alipay.android.client.constant.AlipayHandlerMessageIDs;
import com.alipay.android.client.constant.Constant;
import com.alipay.android.client.manufacture.AppUpgradeInterceptor;
import com.alipay.android.client.manufacture.InterceptorFactory;
import com.alipay.android.client.util.BaseHelper;
import com.alipay.android.client.util.Responsor;
import com.alipay.android.comon.component.ProgressDiv;
import com.alipay.android.comon.component.StyleAlertDialog;
import com.alipay.android.ui.framework.RootController;
import com.alipay.android.ui.transfer.TransferRootControllerActivity;
import com.eg.android.AlipayGphone.R;

public class MessageFilter {
    public final String LOG_TAG = "MessageFilter";

    Activity mActivity;
    private Handler mHandler = null;
    boolean mOnlyShowErrorDialog = true;
    boolean mContinued = true;

    private static final int NOJOB = 0;
    private static final int OPENCLIENTPAYING_START = 1;
    private static final int OPENCLIENTPAYING_FINISH = 2;
    private int mJobType = NOJOB;
    private int mStatus = -1;
    /**
     * Variable for ProgressDialog when paying
     */
    private ProgressDiv mProgress = null;

    public MessageFilter(Activity theActivity) {
        mActivity = theActivity;

        commandHandler();
    }

    private boolean mbShowAlert = true;

    public boolean isShowAlert() {
        return mbShowAlert;
    }

    public void setShowAlert(boolean mShowAlert) {
        mbShowAlert = mShowAlert;
    }

    public boolean process(Message msg) {
        AlipayApplication application = (AlipayApplication) mActivity.getApplicationContext();
        Responsor responsor = (Responsor) msg.obj;
        updateMemoString(responsor);

        mStatus = responsor.status;
        String errorText = responsor.memo;

        mContinued = true;

        if (mStatus == 100) {
            // has nothing to process here.
            return true;
        } else {
            mContinued = false;
            mOnlyShowErrorDialog = true;
            if (mStatus == 200) {
                // TIMEOUT���ر����л�����ص�¼ҳ�档
                backToLogin(R.drawable.infoicon,
                    mActivity.getResources().getString(R.string.WarngingString), errorText);
            } else if (mStatus == 111) {
                // ��ͨ�ͻ���֧��
                enableClientPaying(R.drawable.infoicon,
                    mActivity.getResources().getString(R.string.WarngingString), errorText);
            } else if (mStatus == 112) {
                // �ֻ��
                enablePhineBinding(R.drawable.infoicon,
                    mActivity.getResources().getString(R.string.WarngingString), errorText);
            } else if ((mStatus == 202) || (mStatus == 203)) {
                // 203Ϊ�������°汾���ɰ汾���ܴ���ҵ�����
                updateMethod(responsor, R.drawable.infoicon,
                    mActivity.getResources().getString(R.string.WarngingString), errorText);
            } else if ((mStatus == 608) || (mStatus == 113)) {
                // ����������
                backToHome(responsor, R.drawable.infoicon,
                    mActivity.getResources().getString(R.string.WarngingString), errorText);
            } else if (mStatus == 201) {
                // ��ʾ�޸���
                showNoUpdate(errorText);
            } else if (mStatus == 411) {
                JSONObject tJson = responsor.obj;
                String tLoginId = null;
                try {
                    tLoginId = tJson.getString(Constant.RQF_LOGON_ID);
                } catch (JSONException e) {

                }
                mOnlyShowErrorDialog = false;
                Intent tIntent = new Intent(mActivity, AlipayUserInfoSupplement.class);
                if (tLoginId != null && !tLoginId.equals("")) {
                    tIntent.setData(Uri.parse(tLoginId));
                }
                //				mActivity.startActivityForResult(new Intent(mActivity,AlipayUserInfoSupplement.class), Constant.REQUEST_CODE);
                mActivity.startActivity(tIntent);
            } else if (mStatus == 116) {
                mOnlyShowErrorDialog = false;
                //				mActivity.startActivityForResult(new Intent(mActivity,AlipayUserInfoSupplement.class), Constant.REQUEST_CODE);
                mActivity.startActivity(new Intent(mActivity, AlipayAccountBindingChoice.class));
            }else if(mStatus == 2050){//2050
            	//提现跳转到短信验证码校验界面
            	mOnlyShowErrorDialog = false;
            	return mContinued;
            }
            if (mOnlyShowErrorDialog && !application.getDataHelper().isCanceled()) {
                String tTitle = "";
                int iconID = R.drawable.erroricon;

                if (mStatus == 101 || mStatus == 119) {
                    iconID = R.drawable.infoicon;
                    tTitle = mActivity.getResources().getString(R.string.WarngingString);
                } else if (mStatus == 1) {
                    tTitle = mActivity.getResources().getString(R.string.ErrorString);
                    errorText = mActivity.getResources().getString(R.string.WarningDataCheck);
                } else {
                    tTitle = mActivity.getResources().getString(R.string.ErrorString);
                }
                if (mStatus > 2000) {
                    iconID = R.drawable.infoicon;
                    tTitle = mActivity.getResources().getString(R.string.WarngingString);
                }
                try {
                    if (!mActivity.isFinishing() && isShowAlert()
                        && msg.what != AlipayHandlerMessageIDs.DEAL_QUERY_TRADLIST) {
                        application.getDataHelper().showDialog(mActivity, iconID, tTitle,
                            errorText, mActivity.getResources().getString(R.string.Ensure),
                            new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    // TODO Auto-generated method stub
                                    ensureBtnCheck();
                                }
                            }, null, null, null, null);

                    }
                } catch (Exception e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }

        }

        return mContinued;
    }

    private void ensureBtnCheck() {
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
        } else if (mStatus == 920) {
            if (AlipayContact.class.getCanonicalName().equalsIgnoreCase(className)) {
                mActivity.setResult(Activity.RESULT_OK);
                mActivity.finish();
            }
        }

        else if (mStatus == 199 || mStatus == 714 || mStatus == 715 || mStatus == 716
                 || mStatus == 720 || mStatus == 721 || mStatus == 722 || mStatus == 723) //代付申请错误处理
        {
            if (AlipayAgentSomeone.class.getCanonicalName().equalsIgnoreCase(className)
                || AlipayAgentAnylink.class.getCanonicalName().equalsIgnoreCase(className)) {
                mActivity.setResult(Activity.RESULT_FIRST_USER);
                mActivity.finish();
            }
        } else if (mStatus == 119) {
            if (SubItemCardActivity.class.getCanonicalName().equalsIgnoreCase(className)) {
                mActivity.finish();
                //                networkAbnormalProcess(0);
            }
        } else if (mStatus == 125 || mStatus == 127 || mStatus == 130) {
            mActivity.finish();
        }

    }

    private void showNoUpdate(String MessageText) {
        mOnlyShowErrorDialog = false;
        AlipayApplication application = (AlipayApplication) mActivity.getApplicationContext();
        try {
        	application.getDataHelper().showDialog(mActivity, 0, mActivity.getResources()
                    .getString(R.string.WarngingString), MessageText, mActivity.getResources()
                    .getString(R.string.Ensure), null, null, null, null,null);
           /* StyleAlertDialog dialog = new StyleAlertDialog(mActivity, 0, mActivity.getResources()
                .getString(R.string.WarngingString), MessageText, mActivity.getResources()
                .getString(R.string.Ensure), null, null, null, null);
            dialog.show();*/

            //            AlertDialog.Builder tDialog = new AlertDialog.Builder(mActivity);
            //            tDialog.setTitle(mActivity.getString(R.string.WarngingString));
            //            tDialog.setMessage(MessageText);
            //            tDialog.setPositiveButton(R.string.Ensure, null);
            //            tDialog.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void updateMemoString(Responsor responsor) {
        int status = responsor.status;
        String memo = responsor.memo;
        //		String type = responsor.type;

        if (status == 0) {
            memo = mActivity.getResources().getString(R.string.CheckNetwork);
        } else if (status == 411) {
            memo = mActivity.getResources().getString(R.string.MEMO_411);
        }

        responsor.memo = memo;

    }

    private void enableClientPaying(int iconRes, String titleText, String MessageText) {
        mOnlyShowErrorDialog = false;

        StyleAlertDialog dialog = new StyleAlertDialog(mActivity, iconRes, titleText, MessageText,
            mActivity.getResources().getString(R.string.Yes),
            new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    // TODO Auto-generated method stub
                    mJobType = OPENCLIENTPAYING_START;
                    cmdToServer();
                }
            }, mActivity.getResources().getString(R.string.No),
            new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    // TODO Auto-generated method stub
                    /*
                     * Intent tIntent = new Intent(mActivity,
                     * AlipayMain.class); mActivity.startActivity(tIntent);
                     * mActivity.finish();
                     */
                    //AlipayMain.startMain(mActivity);
                    //						BaseHelper.showDesktop(mActivity);
                }
            }, null);
        dialog.show();

        //		AlertDialog.Builder tDialog = new AlertDialog.Builder(mActivity);
        //		tDialog.setIcon(iconRes);
        //		tDialog.setTitle(titleText);
        //		tDialog.setMessage(MessageText);
        //		tDialog.setPositiveButton(R.string.Yes,
        //				new DialogInterface.OnClickListener() {
        //					@Override public void onClick(DialogInterface dialog,
        //							int which)
        //					{
        //						// TODO Auto-generated method stub
        //						mJobType = OPENCLIENTPAYING_START;
        //						cmdToServer();
        //					}
        //				});
        //		tDialog.setNegativeButton(R.string.No,
        //				new DialogInterface.OnClickListener() {
        //					@Override public void onClick(DialogInterface dialog,
        //							int which)
        //					{
        //						// TODO Auto-generated method stub
        //						/*
        //						 * Intent tIntent = new Intent(mActivity,
        //						 * AlipayMain.class); mActivity.startActivity(tIntent);
        //						 * mActivity.finish();
        //						 */
        //						//AlipayMain.startMain(mActivity);
        ////						BaseHelper.showDesktop(mActivity);
        //					}
        //				});
        //		tDialog.show();

    }

    private void enablePhineBinding(int iconRes, String titleText, String MessageText) {
        mOnlyShowErrorDialog = false;

        StyleAlertDialog dialog = new StyleAlertDialog(mActivity, iconRes, titleText, MessageText,
            mActivity.getResources().getString(R.string.Ensure),
            new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    // TODO Auto-generated method stub
                    BaseHelper.showDesktop(mActivity);
                    //						AlipayPhoneBinding.startPhoneBinding(mActivity);
                }
            }, null, null, null);
        dialog.show();

        //		AlertDialog.Builder tDialog = new AlertDialog.Builder(mActivity);
        //		tDialog.setIcon(iconRes);
        //		tDialog.setTitle(titleText);
        //		tDialog.setMessage(MessageText);
        //		tDialog.setPositiveButton(R.string.Ensure,
        //				new DialogInterface.OnClickListener() {
        //					@Override public void onClick(DialogInterface dialog,
        //							int which)
        //					{
        //						// TODO Auto-generated method stub
        //						BaseHelper.showDesktop(mActivity);
        ////						AlipayPhoneBinding.startPhoneBinding(mActivity);
        //					}
        //				});
        ////		tDialog.setNegativeButton(R.string.No,
        ////				new DialogInterface.OnClickListener() {
        ////					@Override public void onClick(DialogInterface dialog,
        ////							int which)
        ////					{
        ////						// TODO Auto-generated method stub
        ////
        ////						/*
        ////						 * Intent tIntent = new Intent(mActivity,
        ////						 * AlipayMain.class); mActivity.startActivity(tIntent);
        ////						 * mActivity.finish();
        ////						 */
        ////						//AlipayMain.startMain(mActivity);
        ////						BaseHelper.showDesktop(mActivity);
        ////					}
        ////				});
        //		tDialog.show();
    }

    private void updateMethod(Responsor responsor, int iconRes, String titleText, String MessageText) {
        mOnlyShowErrorDialog = false;

        String channel = CacheSet.getInstance(mActivity).getString(Constant.CHANNELS);
        AppUpgradeInterceptor upgradeInterceptor = InterceptorFactory.getInstance()
            .getUpgradeInterceptor(channel);
        if (upgradeInterceptor != null) {
            String upgradeRemind = upgradeInterceptor.execUpgrade(mActivity);
            MessageText = upgradeRemind;
        }

        final Responsor tResponsor = responsor;
        try {
            if (!mActivity.isFinishing() && isShowAlert()) {
                StyleAlertDialog dialog = new StyleAlertDialog(mActivity, iconRes, titleText,
                    MessageText, mActivity.getResources().getString(R.string.Yes),
                    new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            // TODO Auto-generated method stub
                            JSONObject jsonObject = tResponsor.obj;
                            String url = jsonObject.optString(Constant.RPF_DOWNLOAD_URL);
                            UpdateHelper myUpdator = new UpdateHelper(mActivity);
                            myUpdator.update(url);
                        }
                    }, mActivity.getResources().getString(R.string.No),
                    new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            // TODO Auto-generated method stub
                            if (tResponsor.status == 202) {
                                mContinued = true;
                            }
                        }
                    }, null);
                dialog.show();

                //				AlertDialog.Builder tDialog = new AlertDialog.Builder(mActivity);
                //				tDialog.setIcon(iconRes);
                //				tDialog.setTitle(titleText);
                //				tDialog.setMessage(MessageText);
                //				tDialog.setPositiveButton(R.string.Yes,
                //						new DialogInterface.OnClickListener() {
                //					@Override public void onClick(DialogInterface dialog,
                //							int which)
                //					{
                //						// TODO Auto-generated method stub
                //						JSONObject jsonObject = tResponsor.obj;
                //						String url = jsonObject.optString(Constant.RPF_DOWNLOAD_URL);
                //						UpdateHelper myUpdator = new UpdateHelper(mActivity);
                //						myUpdator.update(url);
                //					}
                //				});
                //				tDialog.setNegativeButton(R.string.No,
                //						new DialogInterface.OnClickListener() {
                //					@Override public void onClick(DialogInterface dialog,
                //							int which)
                //					{
                //						// TODO Auto-generated method stub
                //						if (tResponsor.status == 202)
                //						{
                //							mContinued = true;
                //						}
                //					}
                //				});
                //				tDialog.show();
            }
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    private void backToLogin(int iconRes, String titleText, String MessageText) {
        final AlipayApplication application = (AlipayApplication) mActivity.getApplicationContext();
        mOnlyShowErrorDialog = false;
        
        String className = mActivity.getComponentName().getClassName();
        if (/*application.getUserData() == null
            && */(SubItemPucPayActivity.class.getCanonicalName().equalsIgnoreCase(className) 
            	)) {
            application.logoutUser();
            Intent tIntent = new Intent(mActivity, Login.class);
            tIntent.setData(Uri.parse(className));
            mActivity.startActivityForResult(tIntent, Constant.REQUEST_LOGIN_BACK);
        } else {
            try {
                if (!mActivity.isFinishing() && isShowAlert()) {
                	application.getDataHelper().showDialog(mActivity, iconRes, titleText, MessageText, 
                			mActivity.getResources().getString(R.string.Ensure), 
                			new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                	application.logoutUser();
                                    Intent tIntent = new Intent(mActivity, Login.class);
                                    if(mActivity.getClass() == TransferRootControllerActivity.class){
                                    	tIntent.setData(Uri.parse(mActivity.getComponentName().getClassName()));
                                    	((RootController)mActivity).pop2FirstView();
                                    }
                                    if (mActivity!=null&&!mActivity.isFinishing()&& !(mActivity instanceof Main)&&!(mActivity instanceof AlipayAccountManager)) {
                                    	mActivity.finish();
                                    	tIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
									}
                                    
                                    mActivity.startActivityForResult(tIntent, Constant.REQUEST_LOGIN_BACK);
                                }
                            }, null, null, null, null);
                	application.getDataHelper().setDialogCancelable(false);
                	
                    /*StyleAlertDialog dialog = new StyleAlertDialog(mActivity, iconRes, titleText,
                        MessageText, mActivity.getResources().getString(R.string.Ensure),
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                // TODO Auto-generated method stub
                                Intent tIntent = new Intent(mActivity, Login.class);
                                if(mActivity.getClass() == TransferRootControllerActivity.class){
                                	tIntent.setData(Uri.parse(mActivity.getComponentName().getClassName()));
                                	((RootController)mActivity).pop2FirstView();
                                }
                                mActivity.startActivityForResult(tIntent, Constant.REQUEST_LOGIN_BACK);
                                //
                                if (mActivity.getClass() == Main.class
                                    || mActivity.getClass() == AppHallActivity.class
                                    || mActivity.getClass() == More.class
                                    || mActivity.getClass() == MessageList.class
                                    || mActivity.getClass() == RecordsList.class
                                    || mActivity.getClass() == TransferRootControllerActivity.class) {
                                    
                                } else
                                    mActivity.finish();
                            }
                        }, null, null, null);
                    dialog.getDialog().setCancelable(false);
                    dialog.show();*/

                    //				AlertDialog.Builder tDialog = new AlertDialog.Builder(mActivity);
                    //				tDialog.setIcon(iconRes);
                    //				tDialog.setTitle(titleText);
                    //				tDialog.setMessage(MessageText);
                    //				tDialog.setPositiveButton(R.string.Ensure,
                    //						new DialogInterface.OnClickListener() {
                    //					@Override public void onClick(DialogInterface dialog,
                    //							int which)
                    //					{
                    //						// TODO Auto-generated method stub
                    //						Intent tIntent = new Intent(mActivity, Login.class);
                    //						mActivity.startActivity(tIntent);
                    //						mActivity.finish();
                    //					}
                    //				});
                    //				tDialog.show();
                }
            } catch (Exception e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
    }

    private void backToHome(Responsor responsor, int iconRes, String titleText, String MessageText) {
        mOnlyShowErrorDialog = false;

        try {
            if (!mActivity.isFinishing() && isShowAlert()) {
                final Responsor tResponsor = responsor;
                //		AlertDialog.Builder tDialog = new AlertDialog.Builder(mActivity);

                StyleAlertDialog dialog = null;

                if (tResponsor.status == 608) {
                    //			tDialog.setIcon(iconRes);
                    //			tDialog.setTitle(titleText);

                    dialog = new StyleAlertDialog(mActivity, iconRes, titleText, MessageText,
                        mActivity.getResources().getString(R.string.Ensure),
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                // TODO Auto-generated method stub
                                /*
                                 * Intent tIntent = new Intent(mActivity,
                                 * AlipayMain.class); mActivity.startActivity(tIntent);
                                 * mActivity.finish();
                                 */
                                //AlipayMain.startMain(mActivity);
                                if (mStatus == 608) {
                                    BaseHelper.showDesktop(mActivity);
                                }
                            }
                        }, null, null, null);
                } else if (tResponsor.status == 113) {
                    //			tDialog.setIcon(R.drawable.infoicon);
                    //			tDialog.setTitle(mActivity.getResources().getString(
                    //					R.string.WarngingString));

                    dialog = new StyleAlertDialog(mActivity, R.drawable.infoicon, mActivity
                        .getResources().getString(R.string.WarngingString), MessageText, mActivity
                        .getResources().getString(R.string.Ensure),
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                // TODO Auto-generated method stub
                                /*
                                 * Intent tIntent = new Intent(mActivity,
                                 * AlipayMain.class); mActivity.startActivity(tIntent);
                                 * mActivity.finish();
                                 */
                                //AlipayMain.startMain(mActivity);
                                if (mStatus == 608) {
                                    BaseHelper.showDesktop(mActivity);
                                }
                            }
                        }, null, null, null);

                }
                //		tDialog.setMessage(MessageText);
                //		tDialog.setPositiveButton(R.string.Ensure,
                //				new DialogInterface.OnClickListener() {
                //					@Override public void onClick(DialogInterface dialog,
                //							int which)
                //					{
                //						// TODO Auto-generated method stub
                //						/*
                //						 * Intent tIntent = new Intent(mActivity,
                //						 * AlipayMain.class); mActivity.startActivity(tIntent);
                //						 * mActivity.finish();
                //						 */
                //						//AlipayMain.startMain(mActivity);
                //						if(mStatus == 608)
                //						{
                //							BaseHelper.showDesktop(mActivity);
                //						}						
                //					}
                //				});
                if (dialog != null) {
                    dialog.show();
                }
                //		tDialog.show();
            }
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    // CMD TO SERVER
    private void cmdToServer() {
        AlipayApplication application = (AlipayApplication) mActivity.getApplicationContext();
        switch (mJobType) {
            case OPENCLIENTPAYING_START:
                application.getDataHelper().sendMobilePay(mHandler,
                    AlipayHandlerMessageIDs.MOBILEPAY_FINISH);
                if (mProgress == null) {
                    mProgress = application.getDataHelper().showProgressDialogWithCancelButton(
                        mActivity, null, mActivity.getResources().getString(R.string.PleaseWait),
                        false, true, application.getDataHelper().cancelListener,
                        application.getDataHelper().cancelBtnListener);
                }
                break;
        }
    }

    private void commandHandler() {
        mHandler = new Handler() {
            public void handleMessage(Message msg) {
                showResult(msg);
                switch (msg.what) {
                    case AlipayHandlerMessageIDs.MOBILEPAY_FINISH:
                        resultForMobilePay(msg);
                        break;
                }
                super.handleMessage(msg);
            }
        };
    }

    private void showResult(Message msg) {
        boolean tResultOK = false;
        boolean needDismissProcessDialog = true;

        tResultOK = this.process(msg);

        if (tResultOK) {
            if (mJobType == OPENCLIENTPAYING_START) {
                mJobType = OPENCLIENTPAYING_FINISH;
            }
        }

        if (needDismissProcessDialog) {
            if (mProgress != null) {
                mProgress.dismiss();
                mProgress = null;
            }
        }
    }

    // result process.
    protected void resultForMobilePay(Message msg) {
        Responsor responsor = (Responsor) msg.obj;
        String memo = responsor.memo;
        // TODO Auto-generated method stub
        if (mJobType == OPENCLIENTPAYING_FINISH) {
            // Log.i(LOG_TAG, "open mobile pay ok!");
            /*
             * myHelper.showErrorDialog(mActivity, R.drawable.infoicon,
             * mActivity.getResources().getString(R.string.WarngingString),
             * memo, mActivity.getResources().getString(R.string.Ensure), null,
             * null, null, null, null);
             */
            mOnlyShowErrorDialog = false;

            //			AlertDialog.Builder tDialog = new AlertDialog.Builder(mActivity);
            //			tDialog.setIcon(R.drawable.infoicon);
            //			tDialog.setTitle(mActivity.getResources().getString(
            //					R.string.WarngingString));
            //			tDialog.setMessage(memo);
            //			tDialog.setPositiveButton(R.string.Ensure,
            //					new DialogInterface.OnClickListener() {
            //						@Override public void onClick(DialogInterface dialog,
            //								int which)
            //						{
            //							// TODO Auto-generated method stub
            //							/*
            //							 * Intent tIntent = new Intent(mActivity,
            //							 * AlipayMain.class);
            //							 * mActivity.startActivity(tIntent);
            //							 * mActivity.finish();
            //							 */
            //							//AlipayMain.startMain(mActivity);
            //						}
            //					});
            //			tDialog.show();

            StyleAlertDialog dialog = new StyleAlertDialog(mActivity, R.drawable.infoicon,
                mActivity.getResources().getString(R.string.WarngingString), memo, mActivity
                    .getResources().getString(R.string.Ensure),
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // TODO Auto-generated method stub
                        /*
                         * Intent tIntent = new Intent(mActivity,
                         * AlipayMain.class);
                         * mActivity.startActivity(tIntent);
                         * mActivity.finish();
                         */
                        //AlipayMain.startMain(mActivity);
                    }
                }, null, null, null);
            dialog.show();

            mJobType = NOJOB;
        } else {
            // Log.i(LOG_TAG, "open mobile pay failed!");
            mJobType = NOJOB;
        }
    }

    private void networkAbnormalProcess(final int delayTime) {
        //		new Thread(new Runnable(){ 
        //			@Override public void run(){
        //				// Check the current activity. if not login or main. need back
        //				// to main.
        //				String className = mActivity.getComponentName().getClassName();
        //				if(!AppHallActivity.class.getCanonicalName().equalsIgnoreCase(className)&&
        //				        !Login.class.getCanonicalName().equalsIgnoreCase(className)&&
        //				        !AlipayRegister.class.getCanonicalName().equalsIgnoreCase(className)&&
        //				        !AlipayAccountManage.class.getCanonicalName().equalsIgnoreCase(className)&&
        //				        !SubItemDealQueryActivity.class.getCanonicalName().equalsIgnoreCase(className)&&
        //				        !AlipayAgentSomeone.class.getCanonicalName().equalsIgnoreCase(className))
        //				{
        //					if(SubItemPucPayActivity.class.getCanonicalName().equalsIgnoreCase(className)
        //					        )
        //					{
        //						if(Constant.isLogin){
        //							if(AlipayNavigationTabActivity.sRef != null){
        //								AlipayNavigationTabActivity.sRef.finish();
        //							}
        //							Intent intent = new Intent(mActivity, AlipayNavigationTabActivity.class);
        //							intent.putExtra(Defines.CURRENT_TAB, Defines.TAB_INDEX_ALIPAY);
        //							mActivity.startActivity(intent);
        //						}
        //					}
        //					BaseHelper.showDesktop(mActivity);
        //				}
        //
        //			}
        //		}).start();
    }
}