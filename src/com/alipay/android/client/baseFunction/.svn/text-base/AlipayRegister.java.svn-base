/**
 * 
 */
package com.alipay.android.client.baseFunction;

import java.util.ArrayList;
import java.util.HashMap;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.text.Html;
import android.text.InputType;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;

import com.alipay.android.appHall.Helper;
import com.alipay.android.appHall.bussiness.CallBackFilter;
import com.alipay.android.appHall.common.Defines;
import com.alipay.android.client.Login;
import com.alipay.android.client.RootActivity;
import com.alipay.android.client.constant.Constant;
import com.alipay.android.client.util.AlipayInputErrorCheck;
import com.alipay.android.client.util.BaseHelper;
import com.alipay.android.comon.component.EditTextHasNullChecker;
import com.alipay.android.comon.component.EditTextWithButton;
import com.alipay.android.comon.component.ProgressDiv;
import com.alipay.android.comon.component.StyleAlertDialog;
import com.alipay.android.log.AlipayLogAgent;
import com.alipay.android.log.Constants;
import com.alipay.android.log.StorageStateInfo;
import com.alipay.android.net.RequestMaker;
import com.alipay.android.net.http.HttpRequestMaker;
import com.alipay.android.util.Base64;
import com.alipay.android.util.SecureUtil;
import com.alipay.platform.core.Command;
import com.alipay.platform.view.ActivityMediator;
import com.alipay.platform.view.OnDataChangeListener;
import com.eg.android.AlipayGphone.R;
import com.taobao.statistic.TBS;

/**
 * @author chengkuang
 *
 */
public class AlipayRegister extends RootActivity implements OnDataChangeListener {
    @SuppressWarnings("unused")
    private static final String TAG = "AlipyRegister";

    public static final String PRE_REGISTER = "14";
    public static final String REGISTER = "15";

    private ProgressDiv mProgress = null;
    public static Activity mActivity = null;
    private WebView mRegisterView = null;

    /**
     * status
     */
    private final int GET_IMAGE = 1;
    private final int REGISTER_OK = 3;

    @SuppressWarnings("unused")
    private int mErrorType = GET_IMAGE;

    private TextView mTitleName = null;
    private TextView mRegisterProtocol;
    private Button mRegisterButton = null;
    private ImageView mImageView = null;
    private EditText mPhone = null;
    private EditText mName = null;
    private EditText mLoginPassword = null;
    private EditText mPayPassword = null;
    private EditText mCheckCode = null;
    private CheckBox mClick = null;
    private CheckBox mPasswordCheck = null;

    private String mPhoneText = null; //电话号码
    private String mNameText = null; //用户姓名
    private String mLoginPasswordText = null; //登录密码
    private String mPayPasswordText = null; //支付密码

    private Spinner mSpinner = null;//证件下拉框
    private EditTextWithButton mCardNum = null; //证件号码

    private String mCheckCodeText = null; //验证砄1�7

    private Display tDisplay = null;
    private DisplayMetrics tDisplayMetrics = null;
    private RequestMaker mRequestMaker;
    private CallBackFilter mBackFilter;

    private StorageStateInfo storageStateInfo;
    EditTextHasNullChecker editTextHasNullChecker = new EditTextHasNullChecker();

    private ProgressBar mProgressBar;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        /*storageStateInfo = StorageStateInfo.getInstance();
        AlipayLogAgent.onPageJump(this, storageStateInfo.getValue(Constants.STORAGE_CURRENTVIEWID),
            Constants.REGISTERVIEW, storageStateInfo.getValue(Constants.STORAGE_APPID),
            storageStateInfo.getValue(Constants.STORAGE_APPVERSION),
            storageStateInfo.getValue(Constants.STORAGE_PRODUCTID), getUserId(),
            storageStateInfo.getValue(Constants.STORAGE_PRODUCTVERSION),
            storageStateInfo.getValue(Constants.STORAGE_CLIENTID));*/

        mRequestMaker = new HttpRequestMaker(getApplicationContext(), R.raw.interfaces);
        mBackFilter = new CallBackFilter(this);

        storageStateInfo = StorageStateInfo.getInstance();
        setContentView(R.layout.alipay_register_320_480);
        mActivity = this;
        getCheckImage();
        loadAllVariables();
    }


    private void loadAllVariables() {
        tDisplay = this.getWindowManager().getDefaultDisplay();
        tDisplayMetrics = new DisplayMetrics();
        tDisplay.getMetrics(tDisplayMetrics);
        mRegisterView = (WebView) findViewById(R.id.registerWebView);
        mRegisterView.setVisibility(View.GONE);
        mProgressBar = (ProgressBar) findViewById(R.id.ProgressBar);
        mRegisterView.setWebChromeClient(new WebChromeClient() {
            @Override
            public void onProgressChanged(WebView view, int newProgress) {
                if (newProgress >= 0&&newProgress<100){
                    mProgressBar.setVisibility(View.VISIBLE);
                    mProgressBar.setProgress(newProgress);
                }else
                    mProgressBar.setVisibility(View.GONE);
            }
        });
        mRegisterView.setWebViewClient(new WebViewClient(){
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                return super.shouldOverrideUrlLoading(view, url);
            }});
        mTitleName = (TextView) findViewById(R.id.title_text);
        mTitleName.setText(R.string.RegisterTitle);

        mPhone = (EditText) findViewById(R.id.content);
        mPhone.addTextChangedListener(editTextHasNullChecker);
        editTextHasNullChecker.addNeedCheckView(mPhone);

        mName = ((EditTextWithButton) findViewById(R.id.AlipayRegisterName)).getEtContent();
        mName.addTextChangedListener(editTextHasNullChecker);
        editTextHasNullChecker.addNeedCheckView(mName);

        mLoginPassword = ((EditTextWithButton) findViewById(R.id.RegisterLoginPasswordEditText))
            .getEtContent();
        mLoginPassword.addTextChangedListener(editTextHasNullChecker);
        editTextHasNullChecker.addNeedCheckView(mLoginPassword);

        mPayPassword = ((EditTextWithButton) findViewById(R.id.AlipayRegisterPayPassword))
            .getEtContent();
        mPayPassword.addTextChangedListener(editTextHasNullChecker);
        editTextHasNullChecker.addNeedCheckView(mPayPassword);

        mLoginPassword.setInputType(InputType.TYPE_CLASS_TEXT
                                    | InputType.TYPE_TEXT_VARIATION_PASSWORD);
        mPayPassword.setInputType(InputType.TYPE_CLASS_TEXT
                                  | InputType.TYPE_TEXT_VARIATION_PASSWORD);

        mSpinner = (Spinner) findViewById(R.id.AlipayCardType);
        mSpinner.setBackgroundResource(R.drawable.alipay_dropdown_button);
        mSpinner.setAdapter(getSpinnerAdapter());

        mCardNum = (EditTextWithButton) findViewById(R.id.AlipayRegisterCardNum);
        mCardNum.getEtContent().addTextChangedListener(editTextHasNullChecker);
        editTextHasNullChecker.addNeedCheckView(mCardNum.getEtContent());

        mCheckCode = ((EditTextWithButton) findViewById(R.id.RegisterCheckPanel)).getEtContent();
        mCheckCode.addTextChangedListener(editTextHasNullChecker);
        editTextHasNullChecker.addNeedCheckView(mCheckCode);

        mImageView = (ImageView) findViewById(R.id.icon);
        mClick = (CheckBox) findViewById(R.id.OkCheckBox);
        mPasswordCheck = (CheckBox) findViewById(R.id.PasswordCheckBox);
        mPasswordCheck.setOnCheckedChangeListener(new OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    mLoginPassword.setInputType(InputType.TYPE_CLASS_TEXT
                                                | InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
                    mPayPassword.setInputType(InputType.TYPE_CLASS_TEXT
                                              | InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
                } else {
                    mLoginPassword.setInputType(InputType.TYPE_CLASS_TEXT
                                                | InputType.TYPE_TEXT_VARIATION_PASSWORD);
                    mPayPassword.setInputType(InputType.TYPE_CLASS_TEXT
                                              | InputType.TYPE_TEXT_VARIATION_PASSWORD);
                }
                mLoginPassword.setSelection(mLoginPassword.getText().length());
                mPayPassword.setSelection(mPayPassword.getText().length());
            }

        });

        mRegisterButton = (Button) findViewById(R.id.AlipayRegisterButton);
        editTextHasNullChecker.addNeedEnabledButton(mRegisterButton);

        mRegisterButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View arg0) {
                checkItem();
            }
        });
        //		mProtocolButton = (Button)findViewById(R.id.AlipayRegisterProtocolButton);
        mRegisterProtocol = (TextView) findViewById(R.id.ReadProtoco);
        mRegisterProtocol.setText(Html.fromHtml("<u>" + getString(R.string.ReadProtocol) + "</u>"));
        mRegisterProtocol.setTextColor(getResources().getColorStateList(
            R.drawable.alipay_link_text_colors));
        mRegisterProtocol.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View arg0) {
                // TODO Auto-generated method stub
            	Helper.hideInput(AlipayRegister.this, mCardNum);
                mRegisterView.setVisibility(View.VISIBLE);
                mRegisterView.loadUrl(Constant.getAgreementURL(AlipayRegister.this));
            }
        });
    }

    private final static String[] cardTypes = { "身份证", "护照", "港澳台居民大陆通行证", "军官证" };
    private final static String[] cardTypesIds = { "idcard", "passport", "gatPassport",
                                                  "militaryCard" };

    private ArrayAdapter<String> getSpinnerAdapter() {
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
                R.layout.spinner_item, cardTypes);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        return adapter;
    }

    private void checkItem() {
        mPhoneText = this.mPhone.getText().toString();
        mNameText = this.mName.getText().toString();
        mLoginPasswordText = this.mLoginPassword.getText().toString();
        mPayPasswordText = this.mPayPassword.getText().toString();
        mCheckCodeText = this.mCheckCode.getText().toString();

        // check phonenumber
        int tResult = AlipayInputErrorCheck.checkPhoneNumber(mPhoneText);
        if (tResult != AlipayInputErrorCheck.NO_ERROR) {
            // check error.
            String warningMsg;
            if (tResult == AlipayInputErrorCheck.ERROR_INVALID_FORMAT) {
                warningMsg = getResources().getString(R.string.WarningPhoneNumberFormatError);
            } else if (tResult == AlipayInputErrorCheck.ERROR_NULL_INPUT) {
                warningMsg = getResources().getString(R.string.PhoneNOEmpty);
            } else {
                warningMsg = "UNKNOWN_ERROR TYPE = " + tResult;
                //    			Log.i(LOG_TAG, warningMsg);
            }

            BaseHelper.recordWarningMsg(this, warningMsg, Constants.REGISTERVIEW);

            getDataHelper().showDialog(this, R.drawable.infoicon,
                getResources().getString(R.string.WarngingString), warningMsg,
                getResources().getString(R.string.Ensure), null, null, null, null, null);
            return;
        }

        //check name
        tResult = AlipayInputErrorCheck.CheckRealName(mNameText);
        if (tResult != AlipayInputErrorCheck.NO_ERROR) {
            // check error.
            String warningMsg;
            if (tResult == AlipayInputErrorCheck.ERROR_INVALID_FORMAT) {
                warningMsg = getResources().getString(R.string.WarningInvalidRealName);
            } else if (tResult == AlipayInputErrorCheck.ERROR_NULL_INPUT) {
                warningMsg = getResources().getString(R.string.WarningRealNameEmpty);
            } else if (tResult == AlipayInputErrorCheck.ERROR_OUTOF_RANGE) {
                warningMsg = getResources().getString(R.string.WarningRealNameOutRange);
            } else {
                warningMsg = "UNKNOWN_ERROR TYPE = " + tResult;
                //    			Log.i(LOG_TAG, warningMsg);
            }

            BaseHelper.recordWarningMsg(this, warningMsg, Constants.REGISTERVIEW);

            getDataHelper().showDialog(this, R.drawable.infoicon,
                getResources().getString(R.string.WarngingString), warningMsg,
                getResources().getString(R.string.Ensure), null, null, null, null, null);
            return;
        }
        //check login password
        tResult = AlipayInputErrorCheck.checkPayingPasswordSet(mLoginPasswordText);
        if (tResult != AlipayInputErrorCheck.NO_ERROR) {
            // check error.
            String warningMsg;
            if (tResult == AlipayInputErrorCheck.ERROR_INVALID_FORMAT) {
                warningMsg = getResources().getString(R.string.WarningInvalidLoginPassword);
            } else if (tResult == AlipayInputErrorCheck.ERROR_NULL_INPUT) {
                warningMsg = getResources().getString(R.string.NoEmptyPassword);
            } else {
                warningMsg = "UNKNOWN_ERROR TYPE = " + tResult;
                //    			Log.i(LOG_TAG, warningMsg);
            }
            BaseHelper.recordWarningMsg(this, warningMsg, Constants.REGISTERVIEW);

            getDataHelper().showDialog(this, R.drawable.infoicon,
                getResources().getString(R.string.WarngingString), warningMsg,
                getResources().getString(R.string.Ensure), null, null, null, null, null);
            return;
        }
        //check pay password
        tResult = AlipayInputErrorCheck.checkPayingPasswordSet(mPayPasswordText);
        if (tResult != AlipayInputErrorCheck.NO_ERROR) {
            // check error.
            String warningMsg;
            if (tResult == AlipayInputErrorCheck.ERROR_INVALID_FORMAT) {
                warningMsg = getResources().getString(R.string.WarningInvalidLoginPassword);
            } else if (tResult == AlipayInputErrorCheck.ERROR_NULL_INPUT) {
                warningMsg = getResources().getString(R.string.NoEmptyPassword);
            } else {
                warningMsg = "UNKNOWN_ERROR TYPE = " + tResult;
                //    			Log.i(LOG_TAG, warningMsg);
            }
            BaseHelper.recordWarningMsg(this, warningMsg, Constants.REGISTERVIEW);

            getDataHelper().showDialog(this, R.drawable.infoicon,
                getResources().getString(R.string.WarngingString), warningMsg,
                getResources().getString(R.string.Ensure), null, null, null, null, null);
            return;
        }
        //		if(mPayPasswordText.equals(mLoginPasswordText))
        //		{
        //			myHelper.showErrorDialog(this, 
        //					R.drawable.infoicon, 
        //					getResources().getString(R.string.WarngingString), getResources().getString(R.string.RegisterPayIsNotLogin),
        //					getResources().getString(R.string.Ensure), null,
        //					null, null,
        //					null, null);
        //			return;
        //		}
        //check check code
        tResult = AlipayInputErrorCheck.CheckCheckcode(mCheckCodeText);
        if (tResult != AlipayInputErrorCheck.NO_ERROR) {
            // check error.
            String warningMsg;
            if (tResult == AlipayInputErrorCheck.ERROR_INVALID_FORMAT) {
                warningMsg = getResources().getString(R.string.RegisterCheckFormateWrong);
            } else if (tResult == AlipayInputErrorCheck.ERROR_NULL_INPUT) {
                warningMsg = getResources().getString(R.string.RegisterCheckNotNull);
            } else if (tResult == AlipayInputErrorCheck.ERROR_OUTOF_RANGE) {
                warningMsg = getResources().getString(R.string.RegisterCheckIsError);
            } else {
                warningMsg = "UNKNOWN_ERROR TYPE = " + tResult;
                //    			Log.i(LOG_TAG, warningMsg);
            }

            BaseHelper.recordWarningMsg(this, warningMsg, Constants.REGISTERVIEW);
            getDataHelper().showDialog(this, R.drawable.infoicon,
                getResources().getString(R.string.WarngingString), warningMsg,
                getResources().getString(R.string.Ensure), null, null, null, null, null);
            return;
        }

        if (!mClick.isChecked()) {
            //add dialog 
            getDataHelper().showDialog(AlipayRegister.this, R.drawable.infoicon,
                getResources().getString(R.string.WarngingString),
                getResources().getString(R.string.RegisterCheckBoxNotSelected),
                getResources().getString(R.string.Ensure), null, null, null, null, null);
            BaseHelper.recordWarningMsg(this,
                getResources().getString(R.string.RegisterCheckBoxNotSelected),
                Constants.REGISTERVIEW);
            return;
        }

        mErrorType = REGISTER_OK;

        ActivityMediator activityMediator = new ActivityMediator(this);
        ArrayList<String> params = new ArrayList<String>();
        params.add(mPhoneText);
        params.add(mNameText);
        params.add(mCheckCodeText);

        String rsaPk = getPublicKey();
        String rsaTs = getTimeStamp();

        String loginPassword = SecureUtil.encrpt(mLoginPasswordText, rsaPk, rsaTs);
        params.add(loginPassword);

        String payPassword = SecureUtil.encrpt(mPayPasswordText, rsaPk, rsaTs);
        params.add(payPassword);

        String cardTypeId = cardTypesIds[mSpinner.getSelectedItemPosition()];
        params.add(cardTypeId);

        String cardNum = mCardNum.getText();

        if (cardNum == null || cardNum.length() <= 0) {
            getDataHelper().showDialog(this, R.drawable.infoicon,
                getResources().getString(R.string.WarngingString), "证件号码不能为空",
                getResources().getString(R.string.Ensure), null, null, null, null, null);
            return;

        }

        params.add(cardNum);
        params.add(getConfigData().getProductId());

        String sessionId = getSessionId();
        params.add(sessionId);
        params.add(getConfigData().getClientId());
        activityMediator.sendCommand(REGISTER, "clean", mRequestMaker, params);

        //myHelper.sendRegister(mHandler,AlipayHandlerMessageIDs.REGISTER,mPhoneText,mNameText,mLoginPasswordText,mPayPasswordText,mCheckCodeText);
        if (mProgress == null) {
            mProgress = getDataHelper().showProgressDialogWithCancelButton(this, null,
                getResources().getString(R.string.RegisterGetImage), false, true,
                getDataHelper().cancelListener, getDataHelper().cancelBtnListener);
        }
    }

    private void getCheckImage() {
        ActivityMediator activityMediator = new ActivityMediator(this);
        ArrayList<String> params = new ArrayList<String>();
        String sessionId = getSessionId();
        params.add(getConfigData().getProductId());
        params.add(getConfigData().getProductVersion());
        params.add(sessionId);
        params.add(getConfigData().getClientId());
        activityMediator.sendCommand(PRE_REGISTER, "clean", mRequestMaker, params);

        //myHelper.sendPreRegister(mHandler, AlipayHandlerMessageIDs.PRE_REGISTER);
        mErrorType = GET_IMAGE;
        if (mProgress == null) {
            mProgress = getDataHelper().showProgressDialogWithCancelButton(this, null,
                getResources().getString(R.string.RegisterGetImage), false, true,
                getDataHelper().cancelListener, getDataHelper().cancelBtnListener);
        }
    }

    private void getImageSuccess(HashMap<String, Object> hmResponse) {
        byte[] b = Base64.decode(hmResponse.get(Constant.RPF_CHECK_CODE).toString());
        if (b != null && b.length > 0) {
            mImageView.setImageBitmap(BitmapFactory.decodeByteArray(b, 0, b.length));
            if (tDisplayMetrics.widthPixels > 320 && tDisplayMetrics.heightPixels > 480) {
                LinearLayout.LayoutParams m = new LinearLayout.LayoutParams(mImageView
                    .getDrawable().getMinimumWidth() * 3 / 2, mImageView.getDrawable()
                    .getMinimumHeight() * 3 / 2);
                m.gravity = Gravity.CENTER_VERTICAL;
                mImageView.setLayoutParams(m);
            }
        } else {
            //			mErrorType = GET_IMAGE;
            getDataHelper().showDialog(AlipayRegister.this, R.drawable.infoicon,
                getResources().getString(R.string.WarngingString),
                getResources().getString(R.string.RegisterCheckAgain),
                getResources().getString(R.string.Ensure), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        getCheckImage();
                    }
                }, null, null, null, null);
        }
    }

    DialogInterface.OnClickListener btnClickGoBack = new DialogInterface.OnClickListener() {
        @Override
        public void onClick(DialogInterface dialog, int which) {
            AlipayRegister.this.finish();
        }
    };

    private void jumpToHome() {
        this.finish();
        //		Intent tIntent = new Intent(AlipayRegister.this,AppHallActivity.class );
        //    	startActivity(tIntent);

    }

    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            if (mRegisterView.getVisibility() == View.VISIBLE) {
                mRegisterView.loadData("", "text/html", "utf-8");
                mRegisterView.setVisibility(View.GONE);
                return true;
            } else {
                StyleAlertDialog dialog = new StyleAlertDialog(this, R.drawable.infoicon,
                    getResources().getString(R.string.infotitle), getResources().getString(
                        R.string.backGetinfo), getResources().getString(R.string.Ensure),
                    new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            jumpToHome();
                        }

                    }, getResources().getString(R.string.Cancel), null, null);
                dialog.show();
                return true;

            }
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    public boolean preCancel(Command command) {
        return false;
    }

    @Override
    public void onCancel(Command command) {
    }

    @Override
    public boolean preFail(Command command) {
        return false;
    }

    @Override
    public void onFail(Command command) {
        closeProgress();

        String content = command.getResponseMessage();
        if (command.getmId().equals(PRE_REGISTER)) {
            getDataHelper().showDialog(AlipayRegister.this, R.drawable.erroricon,
                getString(R.string.Error), content, getString(R.string.Ensure), btnClickGoBack,
                null, null, null, null);
        } else {
            BaseHelper.showDialog(AlipayRegister.this, getString(R.string.Error), content,
                R.drawable.erroricon);
        }
    }

    @SuppressWarnings("unchecked")
    @Override
    public void onComplete(Command command) {
        //
        HashMap<String, Object> hmResponse = (HashMap<String, Object>) command.getResponseData();

        if (!mBackFilter.processCommand(hmResponse)) {
            closeProgress();

            if (command.getmId().equals(PRE_REGISTER)) {
                String string = (String) hmResponse.get(Constant.RPF_RSA_PK);
                setPublicKey(string);

                string = (String) hmResponse.get(Constant.RPF_RSA_TS);
                setTimeStamp(string);

                getImageSuccess(hmResponse);
            } else if (command.getmId().equals(REGISTER)) {
                /*AlipayLogAgent.onPageJump(this, Constants.REGISTERVIEW,
                    Constants.GETREGISTERSMSVIEW,
                    storageStateInfo.getValue(Constants.STORAGE_APPID),
                    storageStateInfo.getValue(Constants.STORAGE_APPVERSION),
                    storageStateInfo.getValue(Constants.STORAGE_PRODUCTID), getUserId(),
                    storageStateInfo.getValue(Constants.STORAGE_PRODUCTVERSION),
                    storageStateInfo.getValue(Constants.STORAGE_CLIENTID));*/

                getDataHelper().showDialog(AlipayRegister.this, R.drawable.infoicon,
                    getResources().getString(R.string.RegisterSuccess),
                    hmResponse.get(Defines.memo).toString(),
                    getResources().getString(R.string.Ensure), btnClickGoBack, null, null, null,
                    null);
				if (!Constant.isDebug(this)) {
					try {
						if (mName != null
								&& mName.getText().toString().length() > 0) {
							TBS.userRegister(mName.getText().toString());
						}
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			}
        } else {
            if (command.getmId().equals(REGISTER)) {
                getCheckImage();

                mLoginPassword.setText("");
                mPayPassword.setText("");
                mCheckCode.setText("");
            } else
                closeProgress();
        }
    }

    @Override
    public void setRuleId(String ruleId) {
    }

    @Override
    public String getRuleId() {
        return null;
    }

    @Override
    public Context getContext() {
        return this;
    }

    void closeProgress() {
        try {
            mProgress.dismiss();
            mProgress = null;
        } catch (Exception e) {
            //e.printStackTrace();
        }
    }
}
