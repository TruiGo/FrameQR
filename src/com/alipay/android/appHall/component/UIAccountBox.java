package com.alipay.android.appHall.component;

import java.util.HashMap;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.text.Editable;
import android.text.InputType;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.LinearLayout;

import com.alipay.android.appHall.Helper;
import com.alipay.android.appHall.component.accountbox.AlipaySearchAccountActivity;
import com.alipay.android.appHall.component.accountbox.AlipaySetContentListener;
import com.alipay.android.appHall.component.accountbox.AlipayTypeListener;
import com.alipay.android.appHall.component.accountbox.SaveAccountListener;
import com.alipay.android.appHall.uiengine.InputCheckListener;
import com.alipay.android.appHall.uiengine.SendLogListener;
import com.alipay.android.appHall.uiengine.UIInterface;
import com.alipay.android.client.RootActivity;
import com.alipay.android.client.constant.Constant;
import com.alipay.android.client.util.AlipayDataStore;
import com.alipay.android.client.util.AlipayInputErrorCheck;
import com.alipay.android.client.util.BaseHelper;
import com.alipay.android.common.data.UserData;
import com.alipay.android.core.expapp.Page;
import com.eg.android.AlipayGphone.R;

public class UIAccountBox extends EditText implements UIInterface, InputCheckListener,
                                          SendLogListener, AlipaySetContentListener,
                                          SaveAccountListener, AlipayTypeListener {

    private Page mPage;
    //    private Context context;

    public final static String ACCOUNT_TYPE = "account_type";

    public final static String ISHIS = "isHIS";
    public final static String ISQR = "isQR";
    public final static String ISLN = "isLN";
    public final static String ISAA = "isAA";
    public final static String ISSURE = "isSURE";

    private String Account_Type = "";

    private boolean useLN;
    private boolean useQR;
    private boolean isSave;
    //    private String componentId;
    private EditText inputBox;

    //二维码账户
    private String mQRcodeAccount = "";
    boolean mAccountFromQR = false;

    private String mOnInputFinish;

    public UIAccountBox(Page page) {
        super(page.getRawContext());
        mPage = page;
        mPage.getEngine().setSaveAccountListener(this);
    }

    public void init(ViewGroup parent) {
        //        componentId = mPage.getEngine().getAppId() + "." + mPage.getPageName() + "." + get_Id();

        inputBox = (EditText) LayoutInflater.from(mPage.getRawContext()).inflate(
            R.layout.account_box, parent, false);
        this.setOnTouchListener(new OnTouchListener() {
            @Override
            public boolean onTouch(View arg0, MotionEvent arg1) {
                RootActivity activity = (RootActivity) mPage.getEngine().getContext();
                UserData data = activity.getUserData();
                if (data != null && this != null && arg1.getAction() == MotionEvent.ACTION_DOWN) {
                    popSelectAccountType();
                    return true;
                } else
                    return false;
            }
        });

        LinearLayout.LayoutParams layoutParams = (LinearLayout.LayoutParams) inputBox
            .getLayoutParams();
        this.setLayoutParams(Helper.copyLinearLayoutParams(layoutParams));
        this.setId(inputBox.getId());
        this.setPadding(inputBox.getPaddingLeft(), inputBox.getPaddingTop(),
            inputBox.getPaddingRight(), inputBox.getPaddingBottom());
        this.setSingleLine(true);
        this.setInputType(InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS);
        this.setTextColor(inputBox.getTextColors());
        this.setBackgroundDrawable(inputBox.getBackground());
        this.setTextSize(TypedValue.COMPLEX_UNIT_PX, inputBox.getTextSize());
        this.setHint(inputBox.getHint());
    }

    private void popSelectAccountType() {
        mPage.getEngine().setAlipaySetContentListener(this);
        mPage.getEngine().setAlipayTypeListener(this);
        Intent intentQRcode = new Intent((Activity) mPage.getRawContext(),
            AlipaySearchAccountActivity.class);

        intentQRcode.putExtra(ALIPAY_ACCOUNT, true);
        intentQRcode.putExtra(USEQR, useQR);
        intentQRcode.putExtra(USELN, useLN);

        intentQRcode.putExtra(Constant.PAY_TRADE_RECORD_CONTACT, this.get_Text().toString());

        ((Activity) mPage.getRawContext()).startActivityForResult(intentQRcode,
            Constant.REQUEST_GET_ACCOUNT);//以传递参数的方式跳转到下一个Activity
        Helper.hideInputPanel(mPage.getRawContext(), this);
    }

    public static final String ALIPAY_ACCOUNT = "alipay_account";
    public static final String USEQR = "useqr";
    public static final String USELN = "useln";

    public static final String[] ACCOUNT_TYPES = { ALIPAY_ACCOUNT, USEQR, USELN };

    public void setUseQR(boolean useQR) {
        this.useQR = useQR;
    }

    public boolean getUseQR() {
        return useQR;
    }

    public void setUseLN(boolean useLN) {
        this.useLN = useLN;
    }

    public boolean getUseLN() {
        return useLN;
    }

    public void setVisible(boolean isVisible) {
        setVisibility(isVisible ? View.VISIBLE : View.GONE);
    }

    public boolean getVisible() {
        return getVisibility() == View.VISIBLE;
    }

    private String account;

    @Override
    public void setValue(Object value) {
        if (value == null) {
            return;
        }
        if (value.toString().contains(Constant.STR_QRCODE_PREFIX)) {
            setText(mPage.getRawContext().getString(R.string.getAccountSuccess));
            account = value.toString().trim();
        } else {
            setText(value.toString());
            account = null;
        }

    }

    @Override
    public Object getValue() {
        // TODO Auto-generated method stub

        if (getText() == null) {
            return null;
        }

        String str = get_Text().trim();
        if (str != null && str.equals(mPage.getRawContext().getString(R.string.getAccountSuccess))) {
            if (account == null) {
                return null;
            }
            return account.trim();
        }
        if (str.indexOf("(") != -1) {
            int startIndex = str.indexOf("(");
            str = str.substring(startIndex + 1, str.length() - 1);
        }
        return str;
    }

    @Override
    public void setEnable(boolean enabled) {
        this.setEnabled(enabled);
    }

    @Override
    public boolean getEnable() {
        return isEnabled();
    }

    public void setIsSave(boolean isSave) {
        this.isSave = isSave;
    }

    public boolean getIsSave() {
        return isSave;
    }

    @Override
    public void set_Id(int id) {
        setId(id);
    }

    @Override
    public int get_Id() {
        return getId();
    }

    @Override
    public void set_Text(String text) {
        //        if (text == null) {
        //            return;
        //        }
        //        setText(text.trim());
        //        setSelection(text.trim().length());
    }

    @Override
    public String get_Text() {
        return (String) getText().toString();
    }

    @Override
    public String getSelectedIndex() {
        return null;
    }

    @Override
    public String get_Tag() {
        return (String) this.getTag();
    }

    @Override
    public void set_Tag(String tag) {
        this.setTag(tag);
    }

    @Override
    public void set_MarginLeft(int marginLeft) {
        LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) getLayoutParams();
        params.leftMargin = marginLeft;
    }

    @Override
    public int get_MarginLeft() {
        LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) getLayoutParams();
        return params.leftMargin;
    }

    @Override
    public void set_MarginRight(int marginRight) {
        LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) getLayoutParams();
        params.rightMargin = marginRight;
    }

    @Override
    public int get_MarginRight() {
        LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) getLayoutParams();
        return params.rightMargin;
    }

    @Override
    public void set_MarginTop(int marginTop) {
        LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) getLayoutParams();
        params.topMargin = marginTop;
    }

    @Override
    public int get_MarginTop() {
        LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) getLayoutParams();
        return params.topMargin;
    }

    @Override
    public void set_MarginBottom(int marginBottom) {
        LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) getLayoutParams();
        params.bottomMargin = marginBottom;
    }

    @Override
    public int get_MarginBottom() {
        LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) getLayoutParams();
        return params.bottomMargin;
    }

    @Override
    public int get_Width() {
        return getWidth();
    }

    public void set_Width(int width) {
        LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) getLayoutParams();
        params.width = width;
        setLayoutParams(params);
    }

    @Override
    public LinearLayout.LayoutParams getAlipayLayoutParams() {
        return (android.widget.LinearLayout.LayoutParams) getLayoutParams();
    }

    @Override
    public void sendLog() {
        if (this.statistics) {
            /*RootActivity activity = (RootActivity) mPage.getEngine().getContext();
            StorageStateInfo storageStateInfo = StorageStateInfo.getInstance();
            AlipayLogAgent.onEvent(mPage.getRawContext(), Constants.MONITORPOINT_EVENT_CONTACT,
                Account_Type, storageStateInfo.getValue(Constants.STORAGE_CURRENTVIEWID),
                storageStateInfo.getValue(Constants.STORAGE_APPID),
                storageStateInfo.getValue(Constants.STORAGE_APPVERSION),
                storageStateInfo.getValue(Constants.STORAGE_PRODUCTID), activity.getUserId(),
                //    				Constant.STR_USERID,
                storageStateInfo.getValue(Constants.STORAGE_PRODUCTVERSION),
                storageStateInfo.getValue(Constants.STORAGE_CLIENTID));*/
        }
    }

    @Override
    public boolean inputCheck() {
        if (getVisibility() == ViewGroup.GONE || !getEnable()) {
            return true;
        }

        String account = this.getText().toString();

        if (account != null && account.equals("已识别对方账户")) {
            return true;
        }

        int tResult = AlipayInputErrorCheck.CheckUserID(account);
        if (tResult != AlipayInputErrorCheck.NO_ERROR) {
            RootActivity activity = (RootActivity) mPage.getEngine().getContext();
            String warningMsg = "";
            if (tResult == AlipayInputErrorCheck.ERROR_INVALID_FORMAT) {
                warningMsg = getResources().getString(R.string.WarningInvalidAccountName);
            } else if (tResult == AlipayInputErrorCheck.ERROR_NULL_INPUT) {
                warningMsg = getResources().getString(R.string.NoEmptyAccountName);
            } else {
                warningMsg = "UNKNOWN_ERROR TYPE = " + tResult;
            }
            if (!warningMsg.equals("")) {
                activity.getDataHelper().showDialog((Activity) getContext(), R.drawable.infoicon,
                    getResources().getString(R.string.WarngingString), warningMsg,
                    getResources().getString(R.string.Ensure), null, null, null, null, null);
            }

            BaseHelper.recordWarningMsg(activity, warningMsg);
            return false;
        }
        return true;
    }

    @Override
    public void setMaxCharNum(String maxCharNum) {
    }

    @Override
    public void setMinCharNum(String minCharNum) {
    }

    @Override
    public String getDesc() {
        return null;
    }

    @Override
    public void setDesc(String desc) {
    }

    @Override
    public String getRegex() {
        return null;
    }

    @Override
    public void setRegex(String regex) {
    }

    @Override
    public void accountBoxSetContent(int resultCode, Object params) {
        if (resultCode == Activity.RESULT_OK) {
            selectAll();

            Intent data = (Intent) params;
            if (data != null && data.getData() != null)
                mQRcodeAccount = data.getData().toString();

            if (data != null)
                mAccountFromQR = data.getBooleanExtra(Constant.BARCODE_CAPTURE_TYPE, false);

            if (mAccountFromQR && mQRcodeAccount != null && !mQRcodeAccount.equals("")) {
                AlipayDataStore settings = new AlipayDataStore(mPage.getRawContext());
                Constant.STR_QRCODE_PREFIX = settings.getString(AlipayDataStore.QRCODE_PREFIX);

                if (mQRcodeAccount.contains(Constant.STR_QRCODE_PREFIX)) {
                    //二维码识别返回
                    setText(R.string.getAccountSuccess);
                    if (getText().toString().length() > 0) {
                        setSelection(getText().toString().length());
                    }
                    setValue(mQRcodeAccount);
                    Object id = getTag() == null ? "" : getTag();
                    mPage.interpreter("accountBox::" + id.toString(), mOnInputFinish);

                    //                  if(mAddtoContactsLayout.getVisibility()==View.VISIBLE){
                    //                      mAddtoContactsLayout.setVisibility(View.GONE);
                    //                  }
                } else {
                    //不是支付宝二维码
                    RootActivity activity = (RootActivity) mPage.getEngine().getContext();
                    activity.getDataHelper().showDialog((Activity) mPage.getRawContext(),
                        R.drawable.infoicon, getResources().getString(R.string.WarngingString),
                        getResources().getString(R.string.QrcodeCreateTradeWarning),
                        getResources().getString(R.string.Ensure),
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                //记录参数复位
                                setText("");
                                mAccountFromQR = false;
                                mQRcodeAccount = "";
                                Object id = getTag() == null ? "" : getTag();
                                mPage.interpreter("accountBox::" + id.toString(), mOnInputFinish);
                            }
                        }, null, null, null, null);
                }
            } else { //账户来自于联系人
                setText(mQRcodeAccount);
                setValue(mQRcodeAccount);
                setSelection(mQRcodeAccount.length());
                Object id = getTag() == null ? "" : getTag();
                mPage.interpreter("accountBox::" + id.toString(), mOnInputFinish);
                //              if (isContact) {
                //                  if(mAddtoContactsLayout.getVisibility()==View.VISIBLE){
                //                      mAddtoContactsLayout.setVisibility(View.GONE);
                //                  }
                //              }
            }

        } else if (resultCode == Activity.RESULT_CANCELED) {
            //用户返回，未获取到有效账户信息
        } else {
            //
        }
        if (isFocused())
            clearFocus();
    }

    @Override
    public void saveAccount() {
        //添加到本地联系人历史记录
        if (getValue().toString().startsWith(Constant.STR_QRCODE_PREFIX)) {
            return;
        }
        RootActivity activity = (RootActivity) mPage.getEngine().getContext();
        activity.getDataHelper().addUserIdToFile(activity, getValue().toString());
    }

    public boolean isNeedSave() {
        return !mAccountFromQR;
    }

    public String getOnInputFinish() {
        return mOnInputFinish;
    }

    public void setOnInputFinish(String onInputFinish) {
        mOnInputFinish = onInputFinish;
    }

    //tag for android 1.5
    HashMap<Integer, Object> mTags = new HashMap<Integer, Object>();

    @Override
    public Object get_tag(int type) {
        return mTags.get(type);
    }

    @Override
    public void set_Tag(int type, Object tag) {
        mTags.put(type, tag);
    }

    @Override
    public boolean Inputed() {
        Editable editable = getText();
        if (editable != null && editable.toString().trim().length() > 0)
            return true;
        return false;
    }

    private boolean statistics = false;

    public void setStatistics(String statistics) {
        if (statistics != null && "true".equals(statistics)) {
            this.statistics = true;
        } else if (statistics == null || "false".equals(statistics)) {
            this.statistics = false;
        }
    }

    public boolean getStatistics() {
        return this.statistics;
    }

    @Override
    public void setAlipayType(int resultCode, Object params) {
        if (resultCode == Activity.RESULT_OK) {
            selectAll();
            Intent data = null;
            if (params instanceof Intent)
                data = (Intent) params;

            if (data != null) {
                Account_Type = data.getStringExtra(UIAccountBox.ACCOUNT_TYPE);
            }
        }

    }
}
