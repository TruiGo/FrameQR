package com.alipay.android.client;

import java.util.Observable;
import java.util.Observer;

import org.json.JSONObject;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.InputType;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.view.animation.Animation;
import android.view.inputmethod.EditorInfo;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.alipay.android.appHall.Helper;
import com.alipay.android.appHall.common.Defines;
import com.alipay.android.appHall.uiengine.AnimRelativeLayout;
import com.alipay.android.biz.CheckPasswordBiz;
import com.alipay.android.biz.LoginBiz;
import com.alipay.android.client.baseFunction.AlipayGetLoginPassword;
import com.alipay.android.client.baseFunction.AlipayGetPaymentPassword;
import com.alipay.android.client.baseFunction.AlipayGetPaymentPasswordSMSTransfer;
import com.alipay.android.client.baseFunction.AlipayModifyPassword;
import com.alipay.android.client.baseFunction.AlipayPhoneBinding;
import com.alipay.android.client.baseFunction.MessageList;
import com.alipay.android.client.baseFunction.More;
import com.alipay.android.client.constant.AlipayHandlerMessageIDs;
import com.alipay.android.client.constant.Constant;
import com.alipay.android.client.util.AlipayInputErrorCheck;
import com.alipay.android.client.util.BaseHelper;
import com.alipay.android.client.util.DBHelper;
import com.alipay.android.client.util.DataHelper;
import com.alipay.android.client.util.Responsor;
import com.alipay.android.client.util.Utilz;
import com.alipay.android.common.data.MessageData;
import com.alipay.android.common.data.UserData;
import com.alipay.android.common.data.UserInfo;
import com.alipay.android.comon.component.ProgressDiv;
import com.alipay.android.comon.component.StyleAlertDialog;
import com.alipay.android.log.AlipayLogAgent;
import com.alipay.android.log.Constants;
import com.alipay.android.safepay.AlipaySafePaySetting;
import com.alipay.android.safepay.MobileSecurePayHelper;
import com.alipay.android.security.RSA;
import com.alipay.android.ui.card.CardManagerRootControllerActivity;
import com.alipay.android.ui.personlcard.PersonlCardRootControllerActivity;
import com.alipay.android.ui.withdraw.WithdrawRootControllerActivity;
import com.alipay.android.util.JsonConvert;
import com.eg.android.AlipayGphone.R;
import com.google.zxing.client.FinishListener;

public class AlipayAccountManager extends RootActivity implements
		OnItemClickListener, Observer {

	public static final String LOG_TAG = "AlipayAccountManage";
	
	private ImageButton moreImage;

	private ScrollView SV_FullScreen_MyAccountScrollView = null;
	private RelativeLayout RL_FullScreen_SetPasswordLayout = null;

	private TextView mPOINTBALANCE = null;
	private TextView mREDBAG = null;
	private TextView mBINGPHONE = null;

	private RelativeLayout RL_ItemLayout1 = null;
	private RelativeLayout RL_ItemLayout2 = null;
	private RelativeLayout RL_ItemLayout3 = null;
	private RelativeLayout RL_ItemLayout4 = null;
	private RelativeLayout RL_ItemLayout5 = null;
	private RelativeLayout RL_ItemLayout6 = null;
	
	private DataHelper myHelper = null;
	private ProgressDiv mProgress = null;
	private MobileSecurePayHelper mspHelper = null;
	private MessageFilter mMessageFilter = null;
	private JSONObject mMyjsonResp = null;
	private AlipayApplication application;
	private Bitmap indexBitmap;
	private int eventType = 0;
	private Intent mIntent = null;
	
	private Animation translateAnimation;
	
	private Handler mHandler = new Handler() {
		public void handleMessage(Message msg) {

			if (msg.what == R.id.encode_succeeded) {
				// QRBitmap = (Bitmap) msg.obj;
			} else if (msg.what == R.id.encode_failed) {
				showErrorMessage(R.string.msg_encode_barcode_failed);
			} else

			if (msg.what == AlipayHandlerMessageIDs.RQF_PAY) {
				payingFinish2(msg);
			} else if (msg.what == AlipayHandlerMessageIDs.CHECKCODEIMAGE) {
				handleGetPayPassMsg(msg);
			}
			// else {
			// showResult(msg);
			// }
			super.handleMessage(msg);
		}
	};
	

	private void showErrorMessage(int message) {

		StyleAlertDialog dialog = new StyleAlertDialog(this, 0, "",
				getResources().getString(R.string.message), getResources()
						.getString(R.string.button_ok),
				new FinishListener(this), null, null, new FinishListener(this));
		dialog.show();

		// AlertDialog.Builder builder = new AlertDialog.Builder(this);
		// builder.setMessage(message);
		// builder.setPositiveButton(R.string.button_ok, new
		// FinishListener(this));
		// builder.setOnCancelListener(new FinishListener(this));
		// builder.show();
	}

	private ImageView user_avatar;// 头像
	private ImageView plugImg;
	private ImageView rotateImg;

	private TextView mRealName;
	private TextView mAccountName;
	private TextView mBalanceHead;
	private TextView mBalanceEnd;
	private TextView mBalanceYuan;
	private LinearLayout mBankCardLayout;  //bankCard
	private TextView mBankCardNumber;
	private LinearLayout mSystemMessageLayout;// 设置点击事件
	private TextView mSystemMessageNumber;
	private AnimationData animationData;
	private String from;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.alipay_accountmanager_new);
		loadAllVariables();
		
		UserData userData = getUserData();
		if (userData != null) {
			update(userData);
		}
		
		from = getIntent().getStringExtra("pageFrom");
		if(animationData != null && "MainPage".equals(from))
			showAnimation();
	}

	private void showAnimation() {
	    AnimRelativeLayout animFrameLayout = (AnimRelativeLayout) findViewById(R.id.animcontent);
	    animFrameLayout.setAnimData(animationData);
	    animFrameLayout.setTargetWidget(R.id.AccountLayout);
	    animFrameLayout.setMovedWidget(R.id.FullScreen_MyAccountScrollView);
	    translateAnimation=animFrameLayout.playInAnim(null);
	}
	
	private TextView mTitle;
	private void setTopTitle(int resid) {
		if (null == mTitle) {
			mTitle = (TextView)findViewById(R.id.title_text);
		}
		
		mTitle.setText(resid);
	}

	private void loadAllVariables() {
		application = (AlipayApplication) getApplicationContext();
		animationData = application.getAnimationData();

		setTopTitle(R.string.MyAccount);
		
		moreImage = (ImageButton)findViewById(R.id.title_right);
		moreImage.setVisibility(View.VISIBLE);
		moreImage.setBackgroundResource(R.drawable.moresetting);
		moreImage.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Intent intent = new Intent();
		        intent.setClass(AlipayAccountManager.this, More.class);
		        Uri uri = getIntent().getData();
		    	if(uri != null)
		    		intent.putExtra("params", uri.toString());
		    	AlipayAccountManager.this.startActivity(intent);
			}
		});
		
		mspHelper = new MobileSecurePayHelper();
		mBankCardLayout = (LinearLayout) findViewById(R.id.bankCard);
        mBankCardLayout.setOnClickListener(new OnClickListener() {
        	@Override
			public void onClick(View v) {
                Intent tIntent = new Intent(AlipayAccountManager.this, CardManagerRootControllerActivity.class);
                startActivity(tIntent);
                AlipayLogAgent.writeLog(AlipayAccountManager.this, Constants.BehaviourID.CLICKED, null, null, Constants.WALLETACCOUNT, null, Constants.BANKCARDLIST, Constants.ACCOUNTHOME, Constants.BANKCARDICON);
			}
		});
		mBankCardNumber = (TextView) findViewById(R.id.bankCardNumber);
		mSystemMessageNumber = (TextView) findViewById(R.id.systemMessageNumber);
		mSystemMessageLayout = (LinearLayout) findViewById(R.id.systemMessage);
		mSystemMessageLayout.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				jumpToMessageList(AlipayAccountManager.this);
				AlipayLogAgent.writeLog(AlipayAccountManager.this, Constants.BehaviourID.CLICKED, null, null, Constants.WALLETACCOUNT, null, Constants.MSGLIST, Constants.ACCOUNTHOME, Constants.MSGICON);
			}
		});
		mAccountName = (TextView) findViewById(R.id.user_account);
		mRealName = (TextView) findViewById(R.id.realName);
		mBalanceHead = (TextView) findViewById(R.id.user_balance_head);
		mBalanceEnd= (TextView) findViewById(R.id.user_balance_end);
		 mBalanceYuan = (TextView) findViewById(R.id.yuan);
		user_avatar = (ImageView) findViewById(R.id.user_avatar);
		plugImg = (ImageView)findViewById(R.id.plugImg);
		rotateImg = (ImageView)findViewById(R.id.rotateImg);
		user_avatar.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				if (AlipayAccountManager.this.getUserData() != null) {
					Intent intent = new Intent(AlipayAccountManager.this,
							PersonlCardRootControllerActivity.class);
					intent.putExtra("pageFrom", "AccountManager");
					startActivity(intent);
					
					
					UserData userData = getUserData();
					if (userData!=null) {
						AlipayLogAgent.writeLog(AlipayAccountManager.this,
								Constants.BehaviourID.CLICKED, null, null,
								Constants.WALLETACCOUNT, null, Constants.MYNAMECARD,
								Constants.ACCOUNTHOME, Constants.FACEBOOKICON);
					}
				}
			}
		});
		
		rotateImg.setVisibility(View.INVISIBLE);
		UserData userData = getUserData();
		if (userData!=null) {
			
			rotateImg.setVisibility(View.VISIBLE);
			
			Bitmap bitmap = userData.getUserAvtar();
			if (bitmap != null) {
				user_avatar.setImageBitmap(bitmap);
				plugImg.setVisibility(View.INVISIBLE);
			}else{
				plugImg.setVisibility(View.VISIBLE);
			}
			
			MessageData mMessageData = application.getMessageData();
            int remindNum = userData.getMsgCount() + mMessageData.getUnreadMsgCount();
			
			mBankCardNumber.setText(userData.getCardCount());
			if (remindNum>99) {
            	mSystemMessageNumber.setText("N");
			}else{
				mSystemMessageNumber.setText(remindNum + "");
			}
			if (remindNum>0) {
            	mSystemMessageNumber.setVisibility(View.VISIBLE);
            	
			}else{
            	mSystemMessageNumber.setVisibility(View.INVISIBLE);
			}
		}

		SV_FullScreen_MyAccountScrollView = (ScrollView) findViewById(R.id.FullScreen_MyAccountScrollView);
		RL_FullScreen_SetPasswordLayout = (RelativeLayout) findViewById(R.id.FullScreen_SetPasswordLayout);

		// TODO:设置初始值
		mPOINTBALANCE = (TextView) findViewById(R.id.value2);
		mREDBAG = (TextView) findViewById(R.id.value3);
		mBINGPHONE = (TextView) findViewById(R.id.value1);

		// 充值
		RL_ItemLayout1 = (RelativeLayout) findViewById(R.id.ItemLayout1);
		RL_ItemLayout1.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if (mspHelper.detectMobile_sp(AlipayAccountManager.this, 1)) {
					// 进入充值流程
					openProcessDialog(getResources().getString(
							R.string.PleaseWait));
					BaseHelper.payDeal(AlipayAccountManager.this, mHandler,
							mProgress, "", getExtToken(), "", "deposit", "");
				}
			}
		});
		// 提现
		RL_ItemLayout2 = (RelativeLayout) findViewById(R.id.ItemLayout2);
		RL_ItemLayout2.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				// Intent tIntent = new Intent(AlipayAccountManager.this,
				// SubItemAccountOutputActivity.class);
				Intent tIntent = new Intent(AlipayAccountManager.this,
						WithdrawRootControllerActivity.class);
				startActivity(tIntent);
			}

		});
		// 卡通
		RL_ItemLayout3 = (RelativeLayout) findViewById(R.id.ItemLayout3);
		RL_ItemLayout3.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
                Intent tIntent = new Intent(AlipayAccountManager.this, CardManagerRootControllerActivity.class);
                startActivity(tIntent);
			}

		});
		// TODO 集分宝界面
		 RL_ItemLayout4 = (RelativeLayout) findViewById(R.id.ItemLayout4);
		 RL_ItemLayout4.setOnClickListener(new OnClickListener() {
		 @Override
		 public void onClick(View v) {
			 Intent intent = new Intent();
			 intent.setClass(AlipayAccountManager.this, SubItemScoreListActivity.class);
			 startActivity(intent);
		 }
		
		 });
		// 密码设置界面
		RL_ItemLayout5 = (RelativeLayout) findViewById(R.id.ItemLayout5);
		RL_ItemLayout5.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				//埋点
				doSimpleLog(Constants.WALLETACCOUNT,
						Constants.BehaviourID.CLICKED,
						Constants.VIEWID_PwdMngHome, 
						Constants.ACCOUNTHOME, 
						Constants.Seed_PwdMng);
				
				setTopTitle(R.string.Sub_ItemPasswordSetting);
				SV_FullScreen_MyAccountScrollView.setVisibility(View.GONE);
				if (null != moreImage) {
					moreImage.setVisibility(View.GONE);
				}
				
				RL_FullScreen_SetPasswordLayout.setVisibility(View.VISIBLE);
				refreshPatternSetting();
				/*
				 * if(mScrollView != null && mScrollView.getVisibility() ==
				 * View.VISIBLE) { getAccountInfo(); }
				 */
			}

		});
		
		//退出账号
		RL_ItemLayout6 = (RelativeLayout) findViewById(R.id.ItemLayout6);
		RL_ItemLayout6.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
		        if (!AlipayAccountManager.this.isFinishing()) {
		        	getDataHelper().showDialog(AlipayAccountManager.this, R.drawable.infoicon, 
		        			getResources().getString(R.string.WarngingString), 
		        			getResources().getString(R.string.WantToLogOut), 
		        			getResources().getString(R.string.Yes), 
		        			new DialogInterface.OnClickListener() {

		                        @Override
		                        public void onClick(DialogInterface dialog, int which) {
		                        	doLogout(true);
		                        }
		                    }, getResources().getString(R.string.No), null, null, null);
		        }
			}

		});

		myHelper = getDataHelper();

		mMessageFilter = new MessageFilter(this);
		
//		mManageInfo = new ArrayList<String>();
//		mManageList = (ListView) findViewById(R.id.AccountManageListViewCanvas);
//		mManageInfo.add(getString(R.string.ModifyLoginPassword));
//		mManageInfo.add(getString(R.string.ModifyPayPassword));
//		mManageInfo.add(getString(R.string.GetPayPassword));
//		mManageInfo.add(getString(R.string.NoInputPayPassword));
//		mManageList.setAdapter(new ItemAdapter(this, mManageInfo));
//		mManageList.setOnItemClickListener(this);
		loadPasswordSetting();
		
	}

	private void loadPasswordSetting() {
		TextView modifyPayPassword = (TextView)findViewById(R.id.modifyPayPassword);
		modifyPayPassword.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				onItemClick(null, null, 1, -1);
				
			}
		});
		TextView getPayPassword = (TextView)findViewById(R.id.getPayPassword);
		getPayPassword.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				onItemClick(null, null, 2, -1);
			}
		});
		TextView modifyLoginPassword = (TextView)findViewById(R.id.modifyLoginPassword);
		modifyLoginPassword.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				onItemClick(null, null, 0, -1);
			}
		});
		TextView noInputPayPassword = (TextView)findViewById(R.id.noInputPayPassword);
		noInputPayPassword.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				onItemClick(null, null, 3, -1);
			}
		});

		
		refreshPatternSetting();
	}
	
	private View mPatternPassword = null;
	private View mPatternCheckBox = null;
	private TextView mResetPatternPassword = null;
	//private View mPatternPasswordLine = null;
	private boolean mPatternEnabled = false;
	
	private void refreshPatternSetting() {
		if (null == mPatternPassword) {
			mPatternPassword = findViewById(R.id.patternPasswordBG);
//			mPatternPasswordLayout.setOnClickListener(new View.OnClickListener() {
//				
//				@Override
//				public void onClick(View v) {
//					onItemClick(null, null, 4, -1);
//				}
//			});
		}
		
		if (null == mPatternCheckBox) {
			mPatternCheckBox = findViewById(R.id.patternToggle);
			mPatternCheckBox.setOnClickListener(new View.OnClickListener() {
				
				@Override
				public void onClick(View view) {
					onItemClick(null, null, 4, -1);
				}
			});
		}
		
		boolean patternEnabled = false;
		UserData userData = getUserData();
		if (null != userData && null != userData.getUserPattern()) {
			patternEnabled = true;
		}

		if (patternEnabled) {
			if (null != mResetPatternPassword && View.VISIBLE == mResetPatternPassword.getVisibility()) {
				//nothing for now
			}
			else {
				if (null == mResetPatternPassword) {
					mResetPatternPassword = (TextView)findViewById(R.id.resetPatternPassword);
					mResetPatternPassword.setOnClickListener(new View.OnClickListener() {
						
						@Override
						public void onClick(View v) {
							onItemClick(null, null, 5, -1);
						}
					});
				}
				mResetPatternPassword.setVisibility(View.VISIBLE);
				
//				if (null == mPatternPasswordLine) {
//					mPatternPasswordLine = findViewById(R.id.patternPasswordLine);
//				}
//				mPatternPasswordLine.setVisibility(View.VISIBLE);
				
				if (!mPatternEnabled) {
					mPatternPassword.setBackgroundResource(R.drawable.list_btn_top);
//					Drawable patternOpenDrawable = getResources().getDrawable(R.drawable.pattern_open);
//					patternOpenDrawable.setBounds(0, 0, patternOpenDrawable.getIntrinsicWidth(), patternOpenDrawable.getIntrinsicHeight());
////					mPatternPassword.setCompoundDrawables(null, null, patternOpenDrawable, null);
//					mPatternToggleButton.setBackgroundDrawable(patternOpenDrawable);
					mPatternCheckBox.setBackgroundResource(R.drawable.pattern_checkbox_selected);
				}
			}

		}
		else {
			mPatternPassword.setBackgroundResource(R.drawable.list_btn_single);
//			Drawable patternClosedDrawable = getResources().getDrawable(R.drawable.pattern_closed);
//			patternClosedDrawable.setBounds(0, 0, patternClosedDrawable.getIntrinsicWidth(), patternClosedDrawable.getIntrinsicHeight());
////			mPatternPassword.setCompoundDrawables(null, null, patternClosedDrawable, null);
//			mPatternToggleButton.setBackgroundDrawable(patternClosedDrawable);
			
			mPatternCheckBox.setBackgroundResource(R.drawable.pattern_checkbox);
			
			if (null != mResetPatternPassword && View.VISIBLE == mResetPatternPassword.getVisibility()) {
				mResetPatternPassword.setVisibility(View.GONE);
			}
			
//			if (null != mPatternPasswordLine && View.VISIBLE == mPatternPasswordLine.getVisibility()) {
//				mPatternPasswordLine.setVisibility(View.GONE);
//			}
		}
		
		mPatternEnabled = patternEnabled;
	}

	protected void jumpToMessageList(Context context) {
		Intent intent = new Intent(AlipayAccountManager.this, MessageList.class);
		context.startActivity(intent);
	}

	@Override
	public void onResume() {
		super.onResume();
		UserData userData = getUserData();
		if (userData != null) {
			int status = userData.requestData(this, getDataHelper());
			if (status == UserData.REQUEST_OK) {
				update(userData);
			}
		}
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		UserData userData = getUserData();
		if (userData != null) {
			userData.deleteObserver(this);
		}
		
		if(indexBitmap != null && !indexBitmap.isRecycled()){
			indexBitmap.recycle();
			indexBitmap = null;
		}
	}

	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			if (SV_FullScreen_MyAccountScrollView.getVisibility() == View.GONE
					&& RL_FullScreen_SetPasswordLayout.getVisibility() == View.VISIBLE) {
				// 如果是密码设置列表界面就返回到账户界面
				setTopTitle(R.string.MyAccount);
				SV_FullScreen_MyAccountScrollView.setVisibility(View.VISIBLE);
				if (null != moreImage) {
					moreImage.setVisibility(View.VISIBLE);
				}
				
				RL_FullScreen_SetPasswordLayout.setVisibility(View.GONE);
			} else if (RL_FullScreen_SetPasswordLayout.getVisibility() == View.GONE
					&& SV_FullScreen_MyAccountScrollView.getVisibility() == View.VISIBLE) {
				if(animationData != null && "MainPage".equals(from)){
					if(translateAnimation != null && !translateAnimation.hasEnded())
						return true;
					hideAnimation();
				}else
					return super.onKeyDown(keyCode, event);
			}
			return true;
		}
		return false;
	}

	private void hideAnimation() {
        AnimRelativeLayout animFrameLayout = (AnimRelativeLayout) findViewById(R.id.animcontent);
        translateAnimation = animFrameLayout.playOutAnim(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
            }
            
            @Override
            public void onAnimationRepeat(Animation animation) {
            }
            
            @Override
            public void onAnimationEnd(Animation animation) {
                translateAnimation = null;
                finish();
            }
        });
	}

	public static final int MODIFY_PASSWORD = 10;
	public static final int MODIFY_PASSWORD_SUCCESS = 100;

	private boolean mIsTaoBaoAccount;
	@Override
	public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
//		Intent tIntent = null;
		switch (arg2) {
		case 0:
			eventType = Constant.MODIFYLOGINPASSWORD;
			modifyPassword();
			break;
		case 1:
			eventType = Constant.MODIFYPAYPASSWORD;
			modifyPassword();
			break;
		case 2:
			eventType = Constant.GETPAYMENTPASSWWORD;
			if (getMobileNo().length() <= 0) {
//				myHelper.showDialog(AlipayAccountManager.this, R.drawable.infoicon,
//						getResources().getString(R.string.WarngingString), getResources().getString(R.string.NotBindPhoneNoError),
//						getResources().getString(R.string.AccountBindingPhone), new DialogInterface.OnClickListener() {
//							@Override
//							public void onClick(DialogInterface dialog, int which) {
//								mIntent = new Intent(AlipayAccountManager.this, AlipayPhoneBinding.class);
//								mIntent.putExtra(Constant.RQF_LOGIN_ACCOUNT, AlipayAccountManager.this.getUserData().getAccountName());
//								startActivityForResult(mIntent, Constant.REQUEST_PHONE_BINDDING);
//							}
//						},getResources().getString(R.string.Cancel),null, null, null);
				myHelper.showDialog(this, R.drawable.infoicon, getResources().getString(R.string.WarngingString),
						getResources().getString(R.string.NotBindPhoneNo), getResources().getString(R.string.Ensure), null, null, null, null, null);
			} else {
				getPaymentPasswword();
				// 点击“找回支付密码”就请求服务器，来获取开关状态和密码
			}
			break;
		case 3:
			MobileSecurePayHelper mspHelper = new MobileSecurePayHelper();
			Intent tIntent = new Intent(AlipayAccountManager.this, AlipaySafePaySetting.class);
			if (mspHelper.isMobile_spExist(AlipayAccountManager.this)) {
				tIntent.putExtra(Constant.INSTALLSAFEPAY, true);
			} else {
				tIntent.putExtra(Constant.INSTALLSAFEPAY, false);
			}

			startActivity(tIntent);
			break;
		case 5:
		case 4:
			// 埋点
			doSimpleLog(Constants.WALLETACCOUNT, Constants.BehaviourID.CLICKED, Constants.VIEWID_InputLoginPwdView, Constants.VIEWID_PwdMngHome,
					Constants.Seed_SetGesture);

			final boolean cancelMyPattern = (4 == arg2 && mPatternEnabled) ? true : false;
			// custom dialog
			// AlertDialog.Builder builder = new AlertDialog.Builder(this);
			// final Dialog dialog = builder.create();
			// dialog.show();
			final Dialog dialog = new Dialog(this, R.style.dialog);
			dialog.show();
			LayoutInflater inflater = dialog.getLayoutInflater();
			View contentView = inflater.inflate(R.layout.editalertdialog, null);
			FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(Integer.parseInt(getConfigData().getScreenWidth()) - 60,
					LayoutParams.WRAP_CONTENT);

			dialog.setContentView(contentView, params);

			DBHelper db = new DBHelper(AlipayAccountManager.this);
			UserInfo userInfo = db.getLastLoginUser(null);
			mIsTaoBaoAccount = false;
			if (null != userInfo) {
				if (Login.LOGINTYPE_TAOBAO.equals(userInfo.type)) {
					mIsTaoBaoAccount = true;
				}
			}
			db.close();

			final TextView description = (TextView) contentView.findViewById(R.id.description);
			description.setText(getString(mIsTaoBaoAccount ? R.string.patterInputTaobaoLoginPassword : R.string.patterInputAlipayLoginPassword));

			final Button button2 = (Build.VERSION.RELEASE.compareTo("4.0") > 0 && !StyleAlertDialog.mReverseButton) ? (Button) contentView
					.findViewById(R.id.button1) : (Button) contentView.findViewById(R.id.button2);

			button2.setText(getString(R.string.Cancel));
			button2.setOnClickListener(new View.OnClickListener() {

				@Override
				public void onClick(View v) {
					dialog.dismiss();
				}
			});

			final EditText editText = (EditText) contentView.findViewById(R.id.edittext);
			editText.setHint(mIsTaoBaoAccount ? R.string.pattern_inputLoginPassword_taobao : R.string.pattern_inputLoginPassword_alipay);
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

			final Button button1 = (Build.VERSION.RELEASE.compareTo("4.0") > 0 && !StyleAlertDialog.mReverseButton) ? (Button) contentView
					.findViewById(R.id.button2) : (Button) contentView.findViewById(R.id.button1);

			button1.setText(getString(R.string.Ensure));
			button1.setOnClickListener(new View.OnClickListener() {

				@Override
				public void onClick(View v) {
					// 埋点
					doSimpleLog(Constants.WALLETACCOUNT, Constants.BehaviourID.CLICKED, Constants.VIEWID_SetGestureView,
							Constants.VIEWID_InputLoginPwdView, Constants.Seed_ConfirmButton);

					String password = editText.getText().toString();
					int result = AlipayInputErrorCheck.checkLoginPassword(password);
					if (result != AlipayInputErrorCheck.NO_ERROR) {
						// check error.
						String warningMsg;
						if (result == AlipayInputErrorCheck.ERROR_INVALID_FORMAT) {
							warningMsg = getResources().getString(R.string.WarningInvalidLoginPassword);
						} else if (result == AlipayInputErrorCheck.ERROR_NULL_INPUT) {
							warningMsg = getResources().getString(R.string.NoEmptyPassword);
						} else {
							warningMsg = "UNKNOWN_ERROR TYPE = " + result;
							// Log.i(LOG_TAG, warningMsg);
						}

						message.setVisibility(View.VISIBLE);
						description.setVisibility(View.GONE);
						message.setText(warningMsg);
					} else {
						message.setVisibility(View.GONE);

						password = Helper.toDBC(password);
						new LoginPasswordCheckTask(new onLoginPasswordCheckedListener() {

							@Override
							public void onLoginPasswordChecked(int statusCode, String defaultErrorText) {

								AlipayAccountManager.this.closeProgress();

								if (100 == statusCode) {
									if (cancelMyPattern) {
										cancelPattern();
									} else {
										Intent intent = new Intent(AlipayAccountManager.this, PatternLockActivity.class);
										intent.putExtra(PatternLockActivity.PATTERNLOCKTYPE, PatternLockActivity.PATTERNLOCK_SET);
										startActivityForResult(intent, Constant.REQUEST_PATTERNSETTING_BACK);
									}
									dialog.dismiss();

									return;
								} else if (200 == statusCode) {
									// 登陆超时
									dialog.dismiss();

									AlipayAccountManager.this.getDataHelper().showDialog(AlipayAccountManager.this, R.drawable.infoicon,
											getResources().getString(R.string.WarngingString), defaultErrorText,
											getResources().getString(R.string.Ensure), new DialogInterface.OnClickListener() {
												@Override
												public void onClick(DialogInterface dialog, int which) {
													Intent intent = new Intent(AlipayAccountManager.this, Login.class);
													startActivity(intent);

													justLogOut();
												}
											}, null, null, null, null);
								}
								// taobao 199 alipay 104
								else if (199 == statusCode || 184 == statusCode) {
									// 连续输错5次，账户被锁
									dialog.dismiss();

									alertAfterAccountLocked(defaultErrorText);

									return;
								}
								// 密码错误：清空密码
								else if (183 == statusCode) {
									editText.setText(null);
								}

								String error = null;
								if (0 == statusCode) {
									error = AlipayAccountManager.this.getResources().getString(R.string.CheckNetwork);
								} else {
									error = defaultErrorText;
								}

								message.setVisibility(View.VISIBLE);
								description.setVisibility(View.GONE);
								message.setText(error);
							}
						}).execute(password);

						openProcessDialog(getString(R.string.PleaseWait));
					}
				}
			});

			button2.setBackgroundResource(R.drawable.popwindow_btn_right);
			button2.setTextColor(getResources().getColorStateList(R.drawable.sub_btn_color));
			button1.setBackgroundResource(R.drawable.popwindow_btn_left);
			button1.setTextColor(getResources().getColorStateList(R.drawable.main_btn_color));

			editText.setOnKeyListener(new View.OnKeyListener() {

				@Override
				public boolean onKey(View v, int keyCode, KeyEvent event) {
					if (66 == keyCode) {
						if (button1.isClickable()) {
							button1.performClick();
						}
					}
					return false;
				}
			});
			break;
		}
	}

	private void getPaymentPasswword() {
		Intent tIntent;
		if (getUserData() != null) {
			if(getUserData().isCertificate()){
				tIntent = new Intent(AlipayAccountManager.this, AlipayGetPaymentPassword.class);
				startActivity(tIntent);
			}else{
				doGetSwitchImageCheckCode();
			}
		} 
	}
	
	private void modifyPassword(){
		if(getMobileNo().length() <= 0){
			myHelper.showDialog(AlipayAccountManager.this, R.drawable.infoicon,
					getResources().getString(R.string.WarngingString), getResources().getString(R.string.NotBindPhoneNoError),
					getResources().getString(R.string.AccountBindingPhone), new DialogInterface.OnClickListener() {
						@Override
						public void onClick(DialogInterface dialog, int which) {
							mIntent = new Intent(AlipayAccountManager.this, AlipayPhoneBinding.class);
							mIntent.putExtra(Constant.RQF_LOGIN_ACCOUNT, AlipayAccountManager.this.getUserData().getAccountName());
							startActivityForResult(mIntent, Constant.REQUEST_PHONE_BINDDING);
						}
					},getResources().getString(R.string.Cancel),null, null, null);
		}else{
			mIntent  = new Intent(AlipayAccountManager.this, AlipayModifyPassword.class);
			if(Constant.MODIFYLOGINPASSWORD == eventType){
				mIntent.setData(Uri.parse(getString(R.string.ModifyLoginPassword)));
				mIntent.putExtra(Constant.RQF_PASSWORDTYPE, Constant.MODIFY_STYLE_LOGIN);
				startActivityForResult(mIntent, MODIFY_PASSWORD);
			}else if(Constant.MODIFYPAYPASSWORD == eventType){
				mIntent.setData(Uri.parse(getString(R.string.ModifyPayPassword)));
				mIntent.putExtra(Constant.RQF_PASSWORDTYPE, Constant.MODIFY_STYLE_TRADE);
				startActivity(mIntent);
			}
		}

	}
	
	private void cancelPattern() {
		getUserData().setUserPattern(null);

		//删除pattern
		DBHelper db = new DBHelper(AlipayAccountManager.this);
		UserInfo userInfo = db.getLastLoginUser(null);
		if (null != userInfo) {
			db.deletePattern(userInfo.userAccount, userInfo.type);
		}
		db.close();
		
		refreshPatternSetting();
	}
	
    private void alertAfterAccountLocked(String error) {
		getDataHelper().showDialog(AlipayAccountManager.this, R.drawable.erroricon, getString(R.string.ErrorString), error,
				getString(R.string.switchAccount), new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						doLogout(true);
					}
				}, getResources().getString(R.string.GetLoginPassword), new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {

						Intent intent = null;
						if (mIsTaoBaoAccount) {
							intent = new Intent(AlipayAccountManager.this, WebViewActivity.class);
					        intent.putExtra(WebViewActivity.WEBAPP_URL, "http://wap.taobao.com/reg/gp.htm");
					        intent.putExtra(WebViewActivity.WEBAPP_NAME, getString(R.string.Retrieve)); 
						}
						else {
							intent = new Intent(AlipayAccountManager.this, AlipayGetLoginPassword.class);
					        intent.putExtra(Constant.LOGINACCOUNT_CACHE, AlipayAccountManager.this.getUserData().getAccountName()); 
						}
						
						intent.putExtra(Defines.NextActivity, Login.class.getName());
				      
				        startActivity(intent);
				        
				        doLogout(false);
					}
				}, null, null);
    }

    private void justLogOut() {
        Constant.GAS_JSON = null;
        Constant.WATER_JSON = null;
        Constant.ELECTRIC_JSON = null;
        Constant.COMMUN_JSON = null;

        if (getUserData() != null) {
            BaseHelper.exitProcessOnClickListener.clearUserCache(AlipayAccountManager.this);
            logoutUser();
        }
    }
	private void doLogout(boolean go2LoginAfterLogout) {
		justLogOut();
        
        DBHelper db = new DBHelper(AlipayAccountManager.this);
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
        
		if (go2LoginAfterLogout) {
	        Intent tIntent = new Intent(AlipayAccountManager.this, Login.class);
	        //tIntent.putExtra(Defines.NextActivity, MainActivity.class.getName());
	        tIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
	        startActivity(tIntent);
		}

        finish();
	}
    

	/**
	 * 得到图片校验码
	 */
	private void doGetSwitchImageCheckCode() {
		myHelper.sendReqGetSwitchAndCheckCode(mHandler,
				AlipayHandlerMessageIDs.CHECKCODEIMAGE);
		openProcessDialog(getResources().getString(R.string.RegisterGetImage));
	}

	private void openProcessDialog(String msg) {
		if (mProgress == null) {
			mProgress = myHelper.showProgressDialogWithCancelButton(this, null,
					msg, false, true, myHelper.cancelListener,
					myHelper.cancelBtnListener);
		}
	}

	/*
	 * 处理点击“找回支付密码”后服务器返回的信息
	 */
	private void handleGetPayPassMsg(Message msg) {
		closeProgress();
		Intent tIntent = null;
		Responsor responsor = (Responsor) msg.obj;

		boolean tResultOK = false;
		tResultOK = mMessageFilter.process(msg);
		if (!(responsor == null) && tResultOK && (!myHelper.isCanceled())) {
			mMyjsonResp = responsor.obj;
			String isSmsSwitch = mMyjsonResp
					.optString(Constant.RPF_SMSTRANS_SENDSMS_SMSSWITCH);
			String mcheckCode = mMyjsonResp.optString(Constant.RPF_CHECK_CODE);// 开关状态为关闭时将得不到验证码
			if ("true".equals(isSmsSwitch)) {
				tIntent = new Intent(AlipayAccountManager.this,
						AlipayGetPaymentPasswordSMSTransfer.class);
				tIntent.putExtra(Constant.RPF_CHECK_CODE, mcheckCode);
			} else/* if (isSmsSwitch.equals("false")) */{
				tIntent = new Intent(AlipayAccountManager.this,
						AlipayGetPaymentPassword.class);
			}
			startActivity(tIntent);
		}
	}

	private void closeProgress() {
		if (mProgress != null) {
			mProgress.dismiss();
			mProgress = null;
		}
	}

	public void update(UserData userData) {
		if (null == userData) {
			return;
		}

		if (userData.isCertificate()) {
			mRealName.setText(getRealName());
			mRealName.setBackgroundResource(0);
		} else {
			if (null!=getRealName()&&""!=getRealName()) {
        		mRealName.setText(getRealName());
			}else{
				mRealName.setText("你好");
			}
		}
		mAccountName.setText(userData.getAccountName());
		if (userData.getAvailableBalance()==null||userData.getAvailableBalance()=="") {
			mBalanceHead.setText("");
			mBalanceYuan.setVisibility(View.INVISIBLE);
		}else{
			String balance = userData.getAvailableBalance();
			
			if (balance.length()>11) {
				balance = balance.substring(0,8)+"...";
				mBalanceHead.setText(balance);
			}else{
				int pointIndex = balance.indexOf(".");
	        	if (pointIndex == -1) {
	        		mBalanceHead.setText(balance);
				}else{
					String balanceHead = balance.substring(0, pointIndex+1);
					String balanceEnd = balance.substring(pointIndex+1);
					mBalanceHead.setText(balanceHead);
					mBalanceEnd.setText(balanceEnd);
				}
			}
			
			mBalanceYuan.setVisibility(View.INVISIBLE);
		}
		
		if (userData!=null) {
			MessageData mMessageData = application.getMessageData();
            int remindNum = userData.getMsgCount() + mMessageData.getUnreadMsgCount();
			
			mBankCardNumber.setText(userData.getCardCount());
			if (remindNum>99) {
            	mSystemMessageNumber.setText("N");
			}else{
				mSystemMessageNumber.setText(remindNum + "");
			}
			
			if (remindNum>0) {
            	mSystemMessageNumber.setVisibility(View.VISIBLE);
            	
			}else{
            	mSystemMessageNumber.setVisibility(View.INVISIBLE);
			}
		}
		
		mBankCardNumber.setText(userData.getCardCount());

		Bitmap bitmap = userData.getUserAvtar();
		if (bitmap != null) {
			user_avatar.setImageBitmap(bitmap);
			plugImg.setVisibility(View.INVISIBLE);
		} else {
			user_avatar.setImageResource(R.drawable.default_avatar);
			plugImg.setVisibility(View.VISIBLE);
		}
		

		if (userData.getPointBalance()==null||userData.getPointBalance()=="") {
			mPOINTBALANCE.setVisibility(View.INVISIBLE);
		}else{
			mPOINTBALANCE.setVisibility(View.VISIBLE);
			mPOINTBALANCE.setText(userData.getPointBalance()+"个");
		}
		
		if (userData.getRedBag()==null||userData.getRedBag()=="") {
			mREDBAG.setVisibility(View.INVISIBLE);
		}else{
			mREDBAG.setVisibility(View.VISIBLE);
			mREDBAG.setText(userData.getRedBag()+"元");
		}
		
		
		String showBindPhoneInfo = userData.getMobileNo();
		if (showBindPhoneInfo != null && showBindPhoneInfo.length() > 10) {
			showBindPhoneInfo = BaseHelper
					.setStarForPhoneNumber(showBindPhoneInfo);
		} else {
			showBindPhoneInfo = "未绑定";
		}
		mBINGPHONE.setText(showBindPhoneInfo);
	}


	private void payingFinish2(Message msg) {
		closeProgress();
	}

	public void onActivityResult(int requestCode, int resultCode, Intent intent) {
		switch (requestCode) {
		case Constant.REQUEST_PHONE_BINDDING:
			if (resultCode == RESULT_OK) {
				
				switch (eventType) {
				
					case Constant.MODIFYLOGINPASSWORD:
					case Constant.MODIFYPAYPASSWORD:
						modifyPassword();
						break;
						
					case Constant.GETPAYMENTPASSWWORD:
						getPaymentPasswword();
						break;
					default:
						break;
				}
				// 正常返回
				// 得到绑定的手机号，更新此全局变量
//				String phone = BaseHelper.setStarForPhoneNumber(getUserData()
//						.getMobileNo());
				// 设置手机气泡要显示绑定的手机号码；更改手机图片的触发结果（可以在监听器里面完成编码实现）
			} 
			break;
		case Constant.REQUEST_PATTERNSETTING_BACK:
			refreshPatternSetting();
			break;
		case MODIFY_PASSWORD:
			if (resultCode == MODIFY_PASSWORD_SUCCESS) {
				AlipayAccountManager.this.finish();
			}
			break;
		}
	}

	@Override
	public void update(Observable observable, Object data) {
		update(getUserData());
	}
	interface onLoginPasswordCheckedListener {
		void onLoginPasswordChecked(final int statusCode, final String defaultErrorText);
	}
	
    public class LoginPasswordCheckTask extends AsyncTask<String, Object, Integer>{

        private int mStatus = 0;
        private String mMemoText = null;
        private onLoginPasswordCheckedListener mCheckedListener;

        public LoginPasswordCheckTask(onLoginPasswordCheckedListener l) {
            super();
            mCheckedListener = l;
        }

        public void onPublishProgress(Object... values){
        	publishProgress(values);
        }
        
        @Override
        protected void onProgressUpdate(Object... values) {
        	super.onPreExecute();
        }
        
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

		@Override
        protected void onPostExecute(Integer result) {
			if (null != mCheckedListener) {
				mCheckedListener.onLoginPasswordChecked(result, mMemoText);
			}
        }

        @Override
        protected Integer doInBackground(String... params) {
        	mStatus = 0;
        	mMemoText = null;
        	if (null == params || 0 == params.length) {
        		return mStatus;
        	}

        	DBHelper db = new DBHelper(AlipayAccountManager.this);
        	UserInfo userInfo = db.getLastLoginUser(null);
        	db.close();
        	if (null == userInfo) {
        		return mStatus;
        	}

        	CheckPasswordBiz checkPassBiz = new CheckPasswordBiz();
//        	String response = loginBiz.getRSAKey();
//        	JSONObject responseJson = JsonConvert.convertString2Json(response);
//        	if (processCommand(responseJson)) {
//        		return mStatus;
//        	}
//
//        	String publicKey = responseJson.optString(Constant.RPF_RSA_PK);
//        	String timeStamp = responseJson.optString(Constant.RPF_RSA_TS);
//			if (null != publicKey && null != timeStamp) {
//				String password = params[0];
//				password = RSA.encrypt(password, publicKey);
//				
//				ConfigData configData = AlipayAccountManager.this.getConfigData();
//				response = loginBiz.doSimplelogin(userInfo.userAccount, password, userInfo.type, 
//						configData.getUserAgent(), configData.getProductId(), configData.getProductVersion(), 
//						configData.getScreenWidth(), configData.getScreenHeight());
//				responseJson = JsonConvert.convertString2Json(response);
//				processCommand(responseJson);
//			}
        	
			String password = params[0];
			if (Utilz.isEmpty(password)) {
				return mStatus;
			}
			
        	String key = getPublicKey();
        	if (null == key) {
            	String response = new LoginBiz().getRSAKey();
            	JSONObject responseJson = JsonConvert.convertString2Json(response);
            	if (processCommand(responseJson)) {
            		return mStatus;
            	}
    
            	key = responseJson.optString(Constant.RPF_RSA_PK);
        	}

			password = RSA.encrypt(password, key);
			
			String response = checkPassBiz.check(userInfo.userAccount, password, userInfo.type, "login");
			JSONObject responseJson = JsonConvert.convertString2Json(response);
			processCommand(responseJson);
			
        	return mStatus;
        }
        
        public boolean processCommand(JSONObject jsonObject) {
        	if (null == jsonObject) {
        		//AlipayAccountManager.this.showError(0, null);
        		mStatus = 0;
        		return true;
        	}
        	
        	mStatus = Utilz.parseInt(jsonObject.optString(Defines.resultStatus));
        	mMemoText = jsonObject.optString(Defines.memo);

            switch (mStatus) {
                case 100: {
                    String string = jsonObject.optString(Constant.RPF_SESSION_ID);
                    AlipayAccountManager.this.setSessionId(string);
                    }
                    return false;

                case 200:
                    //登陆超时
                    //backToLogin();
                    return false;
                case 202:
                case 203:
                    // 更新
                    //updateMethod(map, errorText);
                    return false;
                case 201:
                    // 检测更新
                    //showNoUpdate(errorText);
                    return false;
                case 411:
                    //补全资料
//                    String tLoginId = (String) map.get(Constant.RQF_LOGON_ID);
//                    Intent tIntent = new Intent(mActivity, AlipayUserInfoSupplement.class);
//                    if (tLoginId != null && !tLoginId.equals("")) {
//                        tIntent.setData(Uri.parse(tLoginId));
//                    }
//                    mActivity.startActivity(tIntent);
                    return false;
                case 116:
//                	mActivity.getDataHelper().showDialog(mActivity,R.drawable.infoicon,mActivity.getResources().getString(R.string.WarngingString), 
//                			mActivity.getString(R.string.TaoBaoBindAlipayTip), mActivity.getResources().getString(R.string.Ensure),new DialogInterface.OnClickListener() {
//    							@Override
//    							public void onClick(DialogInterface dialog,
//    									int which) {
//    								mActivity.startActivity(new Intent(mActivity, AlipayAccountBindingChoice.class));
//    							}
//    						}, null, null, null, null);
                    
                    return false;
                case 129:
                	return false;
                default:
                	//AlipayAccountManager.this.showError(mStatus, errorText);
                	//AlipayAccountManager.this.setLoginErrorText(errorText);
                    return false;
            }
        }
    }

}
