package com.alipay.android.client.baseFunction;

import java.util.Timer;
import java.util.TimerTask;

import org.json.JSONObject;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.alipay.android.client.MessageFilter;
import com.alipay.android.client.RootActivity;
import com.alipay.android.client.constant.AlipayHandlerMessageIDs;
import com.alipay.android.client.constant.Constant;
import com.alipay.android.client.util.Responsor;
import com.alipay.android.comon.component.ProgressDiv;
import com.alipay.android.util.Base64;
import com.eg.android.AlipayGphone.R;

public class AlipayGetPaymentPasswordSMSTransfer extends RootActivity implements OnClickListener {
	
	protected int restTime ;
	protected static final int TIMER_MESSAGE = 1;//倒计时的消息标记
//	private int resultOK = 100;

	private ScrollView mSMSTransferONRetrieveAndInputSmsCheckCodeView;
	private RelativeLayout mPrepareReplySMSLayout;
	private EditText mSMSTransferONSmsCheckCodeEditText;
	private ImageView mSMSTransferONSmsCheckCodeImage;
	private Button mGetSMSButton;
	
	private TextView mGetSMSResultNoticeView;
	private Button mRepeatSendButton;
	private Button mGoSettingPayPassWordButton;
	
	private String mcheckCode = null;
	private ProgressDiv mProgress=null;
	private Timer timer; 
	private MessageFilter mMessageFilter = new MessageFilter(this);
	private DisplayMetrics tDisplayMetrics = null;
	private Display tDisplay = null;
	
	private String rsaPK;
	private String rsaTS;
	
	protected Handler mHandler = new Handler(){

		@Override
		public void handleMessage(Message msg) {
			
			 switch (msg.what)
			 {
					case TIMER_MESSAGE:
						showTimerResult();
						break;
					case AlipayHandlerMessageIDs.GET_PAYPSW_SMSTRANSFER_SENDSMS:
						showSendSMSResult(msg);
		                break;
					case AlipayHandlerMessageIDs.GET_PAYPSW_SMSTRANSFER_PRESETPWD:
						showPreSetPSWResult(msg);
						break;
					default:
						break;
			 }
			
			
			
			super.handleMessage(msg);
		}
		
	};
	
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.alipay_getpasswerd_smstransfer_320_480);
		loadAllVariables();
		
		intRSAData(savedInstanceState);
	}

	public void loadAllVariables()
	{
		tDisplay = this.getWindowManager().getDefaultDisplay();
		tDisplayMetrics = new DisplayMetrics();
        tDisplay.getMetrics(tDisplayMetrics);
		TextView title = (TextView)findViewById(R.id.title_text);	
		title.setText(getResources().getString(R.string.GetPayPassword));
		
		mSMSTransferONRetrieveAndInputSmsCheckCodeView = (ScrollView) findViewById(R.id.SMSTransferONRetrieveAndInputSmsCheckCodeView);
		mPrepareReplySMSLayout = (RelativeLayout) findViewById(R.id.PrepareReplySMSLayout);
		mSMSTransferONSmsCheckCodeEditText = (EditText) findViewById(R.id.SMSTransferONSmsCheckCodeEditText);
		mSMSTransferONSmsCheckCodeEditText.addTextChangedListener(mWatcher);
		mSMSTransferONSmsCheckCodeImage = (ImageView) findViewById(R.id.SMSTransferONSmsCheckCodeImage);
		mGetSMSButton = (Button) findViewById(R.id.GetSMSButton);
		mGetSMSButton.setOnClickListener(this);
		

		 
		mGetSMSResultNoticeView = (TextView) findViewById(R.id.GetSMSResultNoticeView);
		mRepeatSendButton = (Button) findViewById(R.id.RepeatSendButton);
		mRepeatSendButton.setOnClickListener(this);
		mGoSettingPayPassWordButton = (Button) findViewById(R.id.GoSettingPayPassWordButton);
		mGoSettingPayPassWordButton.setOnClickListener(this);
		
		
		Intent tIntent = getIntent();
		if(tIntent.getStringExtra("rsaPK") != null && tIntent.getStringExtra("rsaTS") != null){
			rsaPK = tIntent.getStringExtra("rsaPK");
			rsaTS = tIntent.getStringExtra("rsaTS");
		}
		
        if (getUserData().isCertificate()) {
            restTime = Integer.valueOf(tIntent.getStringExtra(Constant.RPF_SMSTRANS_SENDSMS_RESTTIME));
            changeNextView();
        }else{
            mcheckCode = tIntent.getStringExtra(Constant.RPF_CHECK_CODE); 
            doSetImageCheckCode();
        }
	}
	
	private void intRSAData(Bundle savedInstanceState) {
		if(savedInstanceState != null){
        	String savedRsaPK = savedInstanceState.getString("rsaPK");
        	String savedRsaTS = savedInstanceState.getString("rsaTS");
        	
        	if(savedRsaPK != null){
        		rsaPK = savedRsaPK;
        	}
        	if(savedRsaTS != null){
        		rsaTS = savedRsaTS;
        	}
        }
	}

	@Override
	protected void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		if(rsaPK != null && rsaTS != null){
			outState.putString("rsaPK", rsaPK);
			outState.putString("rsaTS", rsaTS);
		}
	}
	
	/*
	 * 处理点击“设置密码”后连网返回的数据（是否回复短信）。
	 */
	private void showPreSetPSWResult(Message msg)
	{
		closeProgress();
		Responsor responsor = (Responsor)msg.obj;
		boolean tResultOK = false;
		tResultOK = mMessageFilter.process(msg);
	
		if(!(responsor == null) && tResultOK && (!getDataHelper().isCanceled()))//返回成功说明用户已经回复短信
		{
             Intent mIntent = new Intent(AlipayGetPaymentPasswordSMSTransfer.this,AlipayGetPaymentPassword.class);
             mIntent.putExtra("isFromSMSTransfer", true);//告诉下一个Activity是来自短信上行流程
             mIntent.putExtra("rsaPK", rsaPK);
             mIntent.putExtra("rsaTS", rsaTS);
             
             AlipayGetPaymentPasswordSMSTransfer.this.startActivity(mIntent);
             AlipayGetPaymentPasswordSMSTransfer.this.finish();
		}
	}
	/*
	 * 请求服务器下发短信时，服务器返回的数据。
	 */
	private void showSendSMSResult(Message msg)
	{    
		closeProgress();
		Responsor responsor = (Responsor)msg.obj;
		boolean tResultOK = false;
		tResultOK = mMessageFilter.process(msg);
	
		if(!(responsor == null) && tResultOK && (!getDataHelper().isCanceled()))
		{    
			JSONObject mMyjsonResp = responsor.obj;
			restTime = mMyjsonResp.optInt(Constant.RPF_SMSTRANS_SENDSMS_RESTTIME);
			changeNextView();
		}
	}
	
	private void showTimerResult()
	{
		 if(mRepeatSendButton != null)
         {
			 mRepeatSendButton.setText("("+restTime--+")秒后"+getString(R.string.GetPassTipViewTwoInfo4));
			 mRepeatSendButton.setTextColor(getResources().getColor(R.color.TextColorGray));
//             if(number < 0)
//             {
//                 throw new IllegalStateException("number should not less than 0");
//             }
             if(restTime == 0)
             {
             	mRepeatSendButton.setEnabled(true);
             	mRepeatSendButton.setText(getString(R.string.GetPassTipViewTwoInfo2));
             	mRepeatSendButton.setTextColor(getResources().getColor(R.color.ButtonColorYellow));
                 stopTimer();
             }   
         }
	}
	
    private void doSetImageCheckCode()
    {    
		byte[] b = Base64.decode(mcheckCode);
		if(b!=null && b.length>0)
		{   
			mSMSTransferONSmsCheckCodeImage.setImageBitmap(BitmapFactory.decodeByteArray(b ,0,b.length));
			
			
			
			if(tDisplayMetrics.widthPixels>320 && tDisplayMetrics.heightPixels>480)
			{
				RelativeLayout.LayoutParams m = new RelativeLayout.LayoutParams(mSMSTransferONSmsCheckCodeImage.getDrawable().getMinimumWidth()*3/2,mSMSTransferONSmsCheckCodeImage.getDrawable().getMinimumHeight()*3/2);
				m.topMargin = 20;
				m.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
				mSMSTransferONSmsCheckCodeImage.setLayoutParams(m);
			}else{
				RelativeLayout.LayoutParams m = new RelativeLayout.LayoutParams(mSMSTransferONSmsCheckCodeImage.getDrawable().getMinimumWidth(),mSMSTransferONSmsCheckCodeImage.getDrawable().getMinimumHeight());
				m.topMargin = 10;
				m.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
				mSMSTransferONSmsCheckCodeImage.setLayoutParams(m);
			}
		} 
		else
		{
			showDialog(getResources().getString(R.string.RegisterCheckAgain));
		}
    }
    
    /*
	 * 请求服务器下发短信。
	 */
    public void doGetSMS()
    {   
    	
    	String checkCode = mSMSTransferONSmsCheckCodeEditText.getText().toString();
    	getDataHelper().sendCheckCodeGetSMS(mHandler, AlipayHandlerMessageIDs.GET_PAYPSW_SMSTRANSFER_SENDSMS, 
    			                     checkCode, Constant.OP_SMSTRANS_SEDSMS);
    	openProcessDialog(getResources().getString(R.string.RegisterGetImage));
    }
    
    /*
	 * 切换页面。
	 */
    private void changeNextView()
    {
		mSMSTransferONRetrieveAndInputSmsCheckCodeView.setVisibility(View.GONE);
		mPrepareReplySMSLayout.setVisibility(View.VISIBLE);
		
		String  sendSMSResultNotice = getString(R.string.GetSMSResultNoticeTextFront) 
		                 + getMobileNo().substring(0,3)
		                 + "****"
		                 + getMobileNo().substring(7)
		                 + getString(R.string.GetSMSResultNoticeTextBehind);
	
		mGetSMSResultNoticeView.setText(sendSMSResultNotice);
		mRepeatSendButton.setEnabled(false);
		startTimer();
    }
    private TextWatcher mWatcher = new TextWatcher()
    {
        @Override
        public void afterTextChanged(Editable s)
        {
            
        }
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count,
                int after)
        {
        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before,
                int count)
        {
            if(s.length()>0)
            {
                //已经xml中设置了该button的背景。不需要在这设置了
//                mValidateSmsCheckCodeButton.setBackgroundResource(R.drawable.alipay_button_main);
            	mGetSMSButton.setEnabled(true);
            }
            else
            {
              //已经xml中设置了该button的背景。不需要在这设置了
//                mValidateSmsCheckCodeButton.setBackgroundResource(android.R.drawable.btn_default);
            	mGetSMSButton.setEnabled(false);
            }
        }

    };
    
	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.GetSMSButton:
	        InputMethodManager imm =(InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);  
	        imm.hideSoftInputFromWindow(this.getCurrentFocus().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);  
			doGetSMS();
			break;
		case R.id.RepeatSendButton:
			doGetSMS();
			mRepeatSendButton.setEnabled(false);
			break;
		case R.id.GoSettingPayPassWordButton:
			openProcessDialog(getResources().getString(R.string.RegisterGetImage));
			getDataHelper().sendPreSettingPayPSW(mHandler, AlipayHandlerMessageIDs.GET_PAYPSW_SMSTRANSFER_PRESETPWD, Constant.OP_SMSTRANS_PRESETPSW);
			break;
		default:
			break;
		}
		
	}
	
	 protected DialogInterface.OnClickListener giveUpGetPayPSWEnsure= new DialogInterface.OnClickListener()
	    {
	        @Override
	        public void onClick(DialogInterface dialog, int which) {
	        	stopTimer();
	        	AlipayGetPaymentPasswordSMSTransfer.this.finish();
	        }
	    };

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK)
		{
		   if(mPrepareReplySMSLayout!=null && mPrepareReplySMSLayout.getVisibility() == View.VISIBLE)
			{
		       getDataHelper().showDialog(AlipayGetPaymentPasswordSMSTransfer.this, 
						R.drawable.infoicon, 
						getResources().getString(R.string.WarngingString),
						getResources().getString(R.string.WarningGiveUpGetPayPSW),
						getResources().getString(R.string.Ensure), giveUpGetPayPSWEnsure,
						getResources().getString(R.string.Cancel), null,
				        null, null);
			}
			else if(mSMSTransferONRetrieveAndInputSmsCheckCodeView!=null && mSMSTransferONRetrieveAndInputSmsCheckCodeView.getVisibility() == View.VISIBLE)
			{
				this.finish();
			}
			return true;
		}
		return false;
		
	}
    private void showDialog(String msg)
    {
        getDataHelper().showDialog(AlipayGetPaymentPasswordSMSTransfer.this, 
				R.drawable.infoicon, 
				getResources().getString(R.string.WarngingString),msg,
				getResources().getString(R.string.Ensure), null,
		null, null,
		null, null);
    }
    
	private void openProcessDialog(String msg)
	{
		if (mProgress == null)
		{
			mProgress = getDataHelper().showProgressDialogWithCancelButton(this, null, 
					msg, 
					false, true, 
					getDataHelper().cancelListener,
					getDataHelper().cancelBtnListener);
		}
	}
	
	private void closeProgress()
	{
		if (mProgress != null)
		{
			mProgress.dismiss();
			mProgress=null;
		}		
	}  
	//倒计时控制 
    protected void stopTimer()
    {
        if(timer != null)
        {
            timer.cancel(); //停止timer
            timer = null;
        }
    }	
    
    protected void startTimer()
    {
        timer = new Timer();
        timer.schedule(
            new TimerTask()
            {     
                public void run() 
                {   
                    Message message = new Message();       
                    message.what = TIMER_MESSAGE;       
                    mHandler.sendMessage(message);     
                }  
            }, 0,1000);
    }
    
}
