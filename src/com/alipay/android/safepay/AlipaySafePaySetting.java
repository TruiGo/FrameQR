/**
 * 
 */
package com.alipay.android.safepay;

import java.io.File;
import java.util.HashMap;

import org.json.JSONObject;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.alipay.android.client.AlipayAccountManager;
import com.alipay.android.client.Login;
import com.alipay.android.client.MessageFilter;
import com.alipay.android.client.RootActivity;
import com.alipay.android.client.constant.AlipayHandlerMessageIDs;
import com.alipay.android.client.constant.Constant;
import com.alipay.android.client.util.AlipayInputErrorCheck;
import com.alipay.android.client.util.BaseHelper;
import com.alipay.android.client.util.DBHelper;
import com.alipay.android.client.util.Responsor;
import com.alipay.android.client.util.Utilz;
import com.alipay.android.common.data.UserInfo;
import com.alipay.android.comon.component.ProgressDiv;
import com.alipay.android.comon.component.StyleAlertDialog;
import com.alipay.android.log.Constants;
import com.eg.android.AlipayGphone.R;

/**
 * @author chengkuang
 * 快捷支付设置
 * 此屏蔽代码不做删除
 */
public class AlipaySafePaySetting extends RootActivity
{
	
	private TextView mTitleText = null;
	private TextView mNoPWPayNumTextView = null;
	private TextView mNoPWPayMemoTextView = null;
	private TextView mLastVersionId = null;
	
//	private CheckBox mPayButton = null; //支付开关
	private CheckBox mNoPWPayButton = null; //无密支付开关
//	private CheckBox mSafePayButton = null; // 关闭在其他终端的快捷支付
	
//	private RelativeLayout mNoPWPayLayout = null;//无密支付layout
//	private RelativeLayout mSafePayLayout = null;//关闭已使用此账户的终端layout
	
	private LinearLayout mSafePaySettingLayout = null;//无密支付layout
	private LinearLayout mNOInstallLayoutt = null;//关闭已使用此账户的终端layout
	
	private Button mInstallSafeButton = null;
	private boolean isSave = false; //是否已经点击过save
//	private String mUserStatus = "";//快捷支付功能开关状态
	private String mPayType = "";//无密开关
	private String mPayQuota = "";//无密额度
	private String mPayTypeMemo = "";//无密备注

	MobileSecurePayHelper mspHelper = new MobileSecurePayHelper();;
	private ProgressDiv mProgress=null;
	HashMap<String, String> myResponse = new HashMap<String, String>();
//	JSONObject myJsonObject = null;
	private MessageFilter mMessageFilter = new MessageFilter(this);
	private Handler mHandler = new Handler()
	{
		public void handleMessage(Message msg)
		{
			showResult(msg);			
			super.handleMessage(msg);
		}
	};
	
	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		this.setContentView(R.layout.alipay_safepay_setting);	
		
		loadAllVariables();
	}
	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		if(isSave)
		{
			if(mspHelper.isMobile_spExist(this))
			{
				isSave = false;
				getSettingStatus();
				if(mSafePaySettingLayout!=null)
				{
					mSafePaySettingLayout.setVisibility(View.VISIBLE);
				}
				if(mNOInstallLayoutt!=null)
				{
					mNOInstallLayoutt.setVisibility(View.GONE);
				}
				
			}
		}
		else
		{
			isSave = false;
		}
	}
	
	private void loadAllVariables(){

		Intent intent = getIntent();
		mSafePaySettingLayout = (LinearLayout)findViewById(R.id.SafePaySettingLayout);
		mNOInstallLayoutt = (LinearLayout)findViewById(R.id.NOInstallLayout);
		//set title
		mTitleText = (TextView)findViewById(R.id.title_text);
		mTitleText.setText(R.string.NoPWPay);
		
		if(intent.getBooleanExtra(Constant.INSTALLSAFEPAY,false))
		{
			mSafePaySettingLayout.setVisibility(View.VISIBLE);
			mNOInstallLayoutt.setVisibility(View.GONE);
		}
		else
		{
			mSafePaySettingLayout.setVisibility(View.GONE);
			mNOInstallLayoutt.setVisibility(View.VISIBLE);
			
			File cacheDir 	 = this.getCacheDir();
			final String cachePath = cacheDir.getAbsolutePath() + "/temp.apk";
			if(!mspHelper.isMobile_spExist(this))
			{
				mspHelper.retrieveApkFromAssets(this, "mobile_sp.apk", cachePath);	
			}
			PackageManager pm = this.getPackageManager();
			PackageInfo apkInfo = pm.getPackageArchiveInfo(cachePath, PackageManager.GET_META_DATA);
			if(apkInfo.versionName != null)
			{
				mLastVersionId = (TextView)findViewById(R.id.LastVersionId);
				mLastVersionId.setText(mLastVersionId.getText()+apkInfo.versionName);
			}
			mInstallSafeButton = (Button)findViewById(R.id.InstallSafeButton);
			mInstallSafeButton.setOnClickListener(new OnClickListener()
			{
				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					isSave = true;
					Intent intent = mspHelper.installSafePay(cachePath);
					AlipaySafePaySetting.this.startActivity(intent);
//					mspHelper.showInstallConfirmDialog(AlipaySafePaySetting.this,cachePath);
//					mspHelper.detectMobile_sp(AlipaySafePaySetting.this);
				}

			});		
			//获取快捷支付版本号
			
			
			
		}
//		mNoPWPayLayout = (RelativeLayout)findViewById(R.id.NoPWPayLayout);
//		mSafePayLayout = (RelativeLayout)findViewById(R.id.SafePayLayout);
//		mNoPWTextView = 	(TextView)findViewById(R.id.NoPWTextView);
		mNoPWPayNumTextView = 	(TextView)findViewById(R.id.NoPWPayNumTextView);
		mNoPWPayMemoTextView = 	(TextView)findViewById(R.id.NoPWPayMemoTextView);

//		mAlertDialog = new AlertDialog.Builder(AlipaySafePaySetting.this).setIcon(R.drawable.infoicon).setTitle(
//				R.string.AccountPayPassWordHint).setView(mDialog).setCancelable(false)
//				.setPositiveButton(R.string.Ensure,
//						new DialogInterface.OnClickListener() {
//					public void onClick(DialogInterface dialog, int whichButton) {
//						//按键事件
//						sendToServer(mPassword.getText().toString(),mPayType);
//					}
//				})
//				.setNegativeButton(R.string.Cancel, new DialogInterface.OnClickListener() {
//					public void onClick(DialogInterface dialog, int whichButton) {
//						//按键事件
////						if(mNoPWPayButton.isChecked())
////						{
////							mNoPWPayButton.setChecked(false);
////						}
////						else
////						{
////							mNoPWPayButton.setChecked(true);
////						}
//						setCheckBox();
//					}
//				}).create();
//		mPayButton = (CheckBox)findViewById(R.id.PayButton);
//
//		mPayButton.setOnClickListener(new OnClickListener()
//		{
//			@Override
//			public void onClick(View v) {
//				// TODO Auto-generated method stub
//				if(mPayButton.isChecked())
//				{
//					displayPayControl(true);
//					mNoPWTextView.setTextColor(getResources().getColor(R.color.TextColorBlack));
//					mUserStatus = "00";
//					mNoPWPayButton.setEnabled(true);
//					
//				}
//				else
//				{
//					displayPayControl(false);
//					mUserStatus = "01";
//					mNoPWPayButton.setEnabled(false);
//					mNoPWTextView.setTextColor(getResources().getColor(R.color.TextColorGrayTwo));
//				}
//			}			
//		});
		mNoPWPayButton = (CheckBox)findViewById(R.id.NoPWPayButton);
		mNoPWPayButton.setOnClickListener(new OnClickListener()
		{

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				if(mNoPWPayButton.isChecked())
				{
					mPayType = "00";
				}
				else
				{
					mPayType = "01";
				}
				
				showDialog();
			}

		});
		if(mspHelper.isMobile_spExist(this))
		{
			getSettingStatus();
		}
//		mSafePayButton = (CheckBox)findViewById(R.id.SafePayButton);
//		mSafePayButton.setOnClickListener(new OnClickListener()
//		{
//
//			@Override
//			public void onClick(View v) {
//				// TODO Auto-generated method stub
//				if(!mSafePayButton.isChecked())
//				{
//					mSafePayLayout.setVisibility(View.GONE);
//				}
//			}
//			
//		});		
//		mSave = (Button)findViewById(R.id.SaveButton);
//		mSave.setOnClickListener(new OnClickListener()
//		{
//			@Override
//			public void onClick(View v) {
//				// TODO Auto-generated method stub
//				if(mspHelper.isMobile_spExist(AlipaySafePaySetting.this))//如果已安装快捷支付
//				{
//					sendToServer("",mUserStatus,mPayType);
//				}
//				else
//				{
//					if(mPayButton.isChecked())
//					{
//						isSave = true;
//						mspHelper.detectMobile_sp(AlipaySafePaySetting.this);
//					}
//					else
//					{
//						finish();
//					}
//				}
//			}
//
//		});		
		
//		Button cancelBtn = (Button)findViewById(R.id.CancelButton);
//		cancelBtn.setOnClickListener(new OnClickListener()
//		{
//			@Override
//			public void onClick(View v) {
//				// TODO Auto-generated method stub				
//				finish();
//					
//			}
//
//		});	
		
//		if(tBundle != null)
//		{
//			mRsaPK = tBundle.getString(Constant.RPF_RSA_PK);
//			mRsaTS = tBundle.getString(Constant.RPF_RSA_TS);
//			mUserStatus = tBundle.getString(Constant.RQF_USERSTATUS);
//			mPayType = tBundle.getString(Constant.RQF_PAYTYPE);
//			//快捷支付功能开关状态
//			if(mUserStatus.equals("00"))//00开通01关闭
//			{
//				mPayButton.setChecked(true);
//				displayPayControl(true);
//				//无密开关
//				if(mPayType.equals("00"))//00无密 01有密
//				{
//					mNoPWPayButton.setChecked(true);
//				}
//				else
//				{
//					mNoPWPayButton.setChecked(false);
//				}
//			}
//			else
//			{
//				mPayButton.setChecked(false);
//				displayPayControl(false);
//			}
			/*String tmp = tBundle.getString(Constant.RPF_OPENEDNO);
			if(tmp != null || tmp.equals("0"))//00开通01关闭
			{
				mSafePayLayout.setVisibility(View.GONE);
			}
			else
			{
				mSafePayLayout.setVisibility(View.VISIBLE);
				mSafePayButton.setChecked(true);
				String info = mSafePayInfoText.getText().toString();
				info = replaceString(info,"#",Constant.STR_ACCOUNT);
				mSafePayInfoText.setText(replaceString(info,"%",tmp));
			}*/
//		}
//		else
//		{
//			mPayButton.setChecked(false);
////			mSafePayLayout.setVisibility(View.GONE);
//			displayPayControl(false);
//		}	
	}
	
	private void showDialog() {
    	final Dialog dialog = new Dialog(this,R.style.dialog);
    	dialog.show();
        LayoutInflater inflater = dialog.getLayoutInflater();
        View contentView = inflater.inflate(R.layout.editalertdialog, null);
		FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(Integer.parseInt(getConfigData().getScreenWidth())-60, LayoutParams.WRAP_CONTENT);

        dialog.setContentView(contentView,params);

        
        final TextView description = (TextView) contentView.findViewById(R.id.description);
        description.setText(getString(R.string.InputPWsafe));

        final Button button2 = (Build.VERSION.RELEASE.compareTo("4.0") > 0 && !StyleAlertDialog.mReverseButton) ?
        (Button) contentView.findViewById(R.id.button1) :
        (Button) contentView.findViewById(R.id.button2);
        
        button2.setText(getString(R.string.Cancel));
        button2.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				dialog.dismiss();
				setCheckBox();
			}
		});
        
        final EditText editText = (EditText) contentView.findViewById(R.id.edittext);
        editText.setHint(R.string.PaymentPassword);
        editText.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
        editText.setOnFocusChangeListener(new View.OnFocusChangeListener() {
			
			@Override
			public void onFocusChange(View v, boolean hasFocus) {
				if (hasFocus) {
					Utilz.showInputMethod(v, false);
				}
			}
		});
        editText.setImeOptions(EditorInfo.IME_ACTION_DONE);
        
        final TextView message = (TextView) contentView.findViewById(R.id.message);
        message.setText(getString(R.string.AliapyAccountTip) + getAccountName(true));
        
        final Button button1 = (Build.VERSION.RELEASE.compareTo("4.0") > 0 && !StyleAlertDialog.mReverseButton) ?
        (Button) contentView.findViewById(R.id.button2) :
        (Button) contentView.findViewById(R.id.button1);

        button1.setText(getString(R.string.Ensure));
        button1.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				//按键事件
				sendToServer(editText.getText().toString(), mPayType);
			}
        });
        
        button2.setBackgroundResource(R.drawable.popwindow_btn_right);
        button2.setTextColor(getResources().getColorStateList(R.drawable.sub_btn_color));
        button1.setBackgroundResource(R.drawable.popwindow_btn_left);
        button1.setTextColor(getResources().getColorStateList(R.drawable.main_btn_color));
	}
	
	private void setCheckBox()
	{
		if(mNoPWPayButton.isChecked())
		{
			mNoPWPayButton.setChecked(false);
		}
		else
		{
			mNoPWPayButton.setChecked(true);
		}
	}
	/**
	 * 获取快捷支付设置信息
	 */
	private void getSettingStatus()
	{
	    getDataHelper().sendQueryPaySettings(mHandler, AlipayHandlerMessageIDs.QUERYPAYSETTINGS);
		if(mProgress == null){
		    mProgress = getDataHelper().showProgressDialogWithCancelButton(this, null, 
					getResources().getString(R.string.GetSettingStatusInfo), 
					false, true, 
					getDataHelper().cancelListener,
					getDataHelper().cancelBtnListener);
		}
	}

	private void showResult(Message msg) {
    	Responsor responsor = (Responsor)msg.obj;
    	JSONObject obj = responsor.obj;
    	boolean tResultOK = false;
    	
    	tResultOK = mMessageFilter.process(msg);
    	
    	if ((tResultOK) && (!getDataHelper().isCanceled()))
    	{
//    		myJsonObject = obj;
    		switch (msg.what)
    		{
	    		case AlipayHandlerMessageIDs.QUERYPAYSETTINGS:
				{
					parseSettingStatus(obj);
					updateUI();
					break;
				}
	    		case AlipayHandlerMessageIDs.MODIFYPAYSETTINGS:
				{
//					myHelper.showErrorDialog(this, 
//							R.drawable.ok, 
//							getResources().getString(R.string.WarngingString),responsor.memo,
//							getResources().getString(R.string.Ensure), new DialogInterface.OnClickListener(){
//						@Override
//						public void onClick(DialogInterface dialog, int which)
//						{
//							// TODO Auto-generated method stub
//							
//						}
//					},
//					null, null,
//					null, null);
//					break;
					Toast.makeText(AlipaySafePaySetting.this, responsor.memo, 1000).show();
					finish();
				}
    		}		
    	}
    	else
    	{
    		setCheckBox();
    	}
    	if (mProgress != null){
    		mProgress.dismiss();
    		mProgress=null;
    	}
    }
	
	/**
	 *解析设置obj
	 * @param obj
	 */
	private void parseSettingStatus(JSONObject obj)
	{
		if(obj != null)
		{
//			mUserStatus = obj.optString(Constant.RQF_USERSTATUS);
			mPayType = obj.optString(Constant.RQF_PAYTYPE);
			mPayQuota = obj.optString(Constant.RQF_PAYQUOTA);
			mPayTypeMemo = obj.optString(Constant.RQF_PAYTYPEMEMO);
//			mRsaPK = obj.optString(Constant.RPF_RSA_PK);
//			mRsaTS = obj.optString(Constant.RPF_RSA_TS);
			
			getConfigData().setPublicKey(obj.optString(Constant.RPF_RSA_PK));
			getConfigData().setTimeStamp(obj.optString(Constant.RPF_RSA_TS));
		}
	}
	
	private void updateUI()
	{
//		if(mUserStatus.equals("00"))
//		{
//			mPayButton.setChecked(true);
//			mNoPWPayButton.setEnabled(true);
//			mNoPWTextView.setTextColor(getResources().getColor(R.color.TextColorBlack));
			if(mPayType.equals("00"))
			{
				mNoPWPayButton.setChecked(true);
			}
			else
			{
				mNoPWPayButton.setChecked(false);
			}
			mNoPWPayNumTextView.setText(mPayQuota);
			mNoPWPayMemoTextView.setText(mPayTypeMemo);
			
//		}
//		else
//		{
//			mPayButton.setChecked(false);
//			mNoPWPayButton.setEnabled(false);
//			mNoPWTextView.setTextColor(getResources().getColor(R.color.TextColorGrayTwo));
//		}
		
			
	}
	
	public String replaceString(String str,String letter,String str2)
	{
		return str.replace(letter, str2);
	}
	
	/**
	 * 修改设置
	 * @param passWord
	 * @param userStatus
	 * @param payType
	 */
	private void sendToServer(String passWord,String payType)
	{
		int tResult = AlipayInputErrorCheck.checkLoginPassword(passWord);
		if (tResult != AlipayInputErrorCheck.NO_ERROR)
		{
			// check error.
			String warningMsg;
			if (tResult == AlipayInputErrorCheck.ERROR_INVALID_FORMAT)
			{
				warningMsg = getResources().getString(
						R.string.WarningInvalidLoginPassword);
			}
			else if (tResult == AlipayInputErrorCheck.ERROR_NULL_INPUT)
			{
				warningMsg = getResources().getString(R.string.NoEmptyPassword);
			}
			else
			{
				warningMsg = "UNKNOWN_ERROR TYPE = " + tResult;
				// Log.i(LOG_TAG, warningMsg);
			}
			getDataHelper().showDialog(this, R.drawable.infoicon, getResources()
					.getString(R.string.WarngingString), warningMsg,
					getResources().getString(R.string.Ensure), null, null,
					null, null, null);
			setCheckBox();
			BaseHelper.recordWarningMsg(this, warningMsg,Constants.SAFEPAYSETTINGVIEW);
			return;
		}

		
		getDataHelper().sendModifyPaySettings(mHandler, AlipayHandlerMessageIDs.MODIFYPAYSETTINGS,passWord,payType);
		if(mProgress == null){
		    mProgress = getDataHelper().showProgressDialogWithCancelButton(this, null, 
					getResources().getString(R.string.GetSettingStatusInfo), 
					false, true, 
					getDataHelper().cancelListener,
					getDataHelper().cancelBtnListener);
		}
	}

}
