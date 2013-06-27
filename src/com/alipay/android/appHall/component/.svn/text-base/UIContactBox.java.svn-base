package com.alipay.android.appHall.component;

import java.util.ArrayList;
import java.util.HashMap;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.text.Editable;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.alipay.android.appHall.Helper;
import com.alipay.android.appHall.component.accountbox.AlipaySetContentListener;
import com.alipay.android.appHall.uiengine.InputCheckListener;
import com.alipay.android.appHall.uiengine.UIInterface;
import com.alipay.android.client.RootActivity;
import com.alipay.android.client.util.AlipayInputErrorCheck;
import com.alipay.android.client.util.BaseHelper;
import com.alipay.android.client.util.LephoneConstant;
import com.alipay.android.core.expapp.Page;
import com.eg.android.AlipayGphone.R;

public class UIContactBox extends LinearLayout implements UIInterface, InputCheckListener,
                                              AlipaySetContentListener {

    private Page mPage;
    private ImageView payContactearch;
    private EditText inputBox;
    private String expression;

    public UIContactBox(Page page) {
        super(page.getEngine().getContext());
        mPage = page;
    }

    public void init(ViewGroup parent) {

        RelativeLayout layout = (RelativeLayout) LayoutInflater
            .from(mPage.getEngine().getContext()).inflate(R.layout.contact_box, parent, false);
        this.addView(layout,Helper.copyLinearLayoutParams((LayoutParams) layout.getLayoutParams()));

        inputBox = (EditText) findViewById(R.id.UIPaySendSMSEditText2);

        inputBox.setOnKeyListener(new OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                // TODO Auto-generated method stub
                if (event.getAction() == (KeyEvent.ACTION_DOWN)) {
                    if (keyCode == KeyEvent.KEYCODE_ENTER) {
                        // 隐藏软键盘
                        Helper.hideInputPanel(mPage.getEngine().getContext(), UIContactBox.this);
                        return true;
                    }
                }
                return false;
            }
        });

        payContactearch = (ImageView) findViewById(R.id.UIPaySMSAccountSearch2);
        payContactearch.setOnClickListener(new OnClickListener() {
            public void onClick(View arg0) {
                mPage.getEngine().setAlipaySetContentListener(UIContactBox.this);
                Helper.hideInputPanel(mPage.getEngine().getContext(), (mPage.getEngine())
                    .getCurrentPage().getFormPanel());
                BaseHelper.doPhoneContact((Activity) mPage.getEngine().getContext());
            }
        });
    }

    public void setExpression(String expression) {
        this.expression = expression;
    }

    public String getExpression() {
        return expression;
    }

    public void setVisible(boolean isVisible) {
        setVisibility(isVisible ? View.VISIBLE : View.GONE);
    }

    public boolean getVisible() {
        return getVisibility() == View.VISIBLE;
    }

    @Override
    public void setValue(Object value) {
        if (value == null) {
            return;
        }
        inputBox.setText(value.toString());
        inputBox.setSelection(value.toString().length());
    }

    @Override
    public Object getValue() {
        // TODO Auto-generated method stub
        return inputBox.getText().toString();
    }

    @Override
    public void setEnable(boolean enabled) {
        this.setEnabled(enabled);
    }

    @Override
    public boolean getEnable() {
        // TODO Auto-generated method stub
        return isEnabled();
    }

    // ----------------------------设置宽度
    private boolean isChange = true;
    private int width = ViewGroup.LayoutParams.FILL_PARENT;

    // getWidth方法应该和设置的宽度是否一样，待确认
    public void set_Width(int width) {
        //        isChange = true;
        //        this.width = width;
        //        changedWidth();
    }

    private void changedWidth() {
        ViewGroup.LayoutParams params = (ViewGroup.LayoutParams) findViewById(
            R.id.UIPaySendSMSInput2).getLayoutParams();
        params.width = this.width;
        findViewById(R.id.UIPaySendSMSInput2).setLayoutParams(params);
    }

    @Override
    protected void onWindowVisibilityChanged(int visibility) {
        super.onWindowVisibilityChanged(visibility);
        if (isChange) {
            changedWidth();
        }
        isChange = false;
        // 最后测试动态更改宽度
        requestLayout();
    }

    public void setHint(String value) {
        inputBox.setHint(value);
    }

    @Override
    public String getSelectedIndex() {
        return null;
    }

    private boolean isSave;

    @Override
    public void setIsSave(boolean isSave) {
        this.isSave = isSave;
    }

    @Override
    public boolean getIsSave() {
        return isSave;
    }

    @Override
    public void set_MarginLeft(int marginLeft) {
        LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) findViewById(
            R.id.UIPaySendSMSInput2).getLayoutParams();
        params.leftMargin = marginLeft;
        findViewById(R.id.UIPaySendSMSInput2).setLayoutParams(params);
    }

    @Override
    public int get_MarginLeft() {
        LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) findViewById(
            R.id.UIPaySendSMSInput2).getLayoutParams();
        return params.leftMargin;
    }

    @Override
    public void set_MarginRight(int marginRight) {
        LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) findViewById(
            R.id.UIPaySendSMSInput2).getLayoutParams();
        params.rightMargin = marginRight;
        findViewById(R.id.UIPaySendSMSInput2).setLayoutParams(params);
    }

    @Override
    public int get_MarginRight() {
        LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) findViewById(
            R.id.UIPaySendSMSInput2).getLayoutParams();
        return params.rightMargin;
    }

    @Override
    public void set_MarginTop(int marginTop) {
        LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) findViewById(
            R.id.UIPaySendSMSInput2).getLayoutParams();
        params.topMargin = marginTop;
        findViewById(R.id.UIPaySendSMSInput2).setLayoutParams(params);
    }

    @Override
    public int get_MarginTop() {
        LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) findViewById(
            R.id.UIPaySendSMSInput2).getLayoutParams();
        return params.topMargin;
    }

    @Override
    public void set_MarginBottom(int marginBottom) {
        LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) findViewById(
            R.id.UIPaySendSMSInput2).getLayoutParams();
        params.bottomMargin = marginBottom;
        findViewById(R.id.UIPaySendSMSInput2).setLayoutParams(params);
    }

    @Override
    public int get_MarginBottom() {
        LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) findViewById(
            R.id.UIPaySendSMSInput2).getLayoutParams();
        return params.bottomMargin;
    }

    @Override
    public int get_Width() {
        return getWidth();
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
    }

    @Override
    public String get_Text() {
        return inputBox.getText().toString();
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
    public boolean inputCheck() {
        if (getVisibility() == ViewGroup.GONE || !getEnable()) {
            return true;
        }
        RootActivity activity = (RootActivity) mPage.getEngine().getContext();
        String tString = inputBox.getText().toString();
        // check PhoneNumber
        int tResult = AlipayInputErrorCheck.checkPhoneNumber(tString);
        if (tResult != AlipayInputErrorCheck.NO_ERROR) {
            // check error.
            String warningMsg;

            if (tResult == AlipayInputErrorCheck.ERROR_INVALID_FORMAT) {
                warningMsg = getResources().getString(R.string.WarningPhoneNumberFormatError);
            } else if (tResult == AlipayInputErrorCheck.ERROR_NULL_INPUT) {
                warningMsg = getResources().getString(R.string.PhoneNOEmpty);
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
    public LinearLayout.LayoutParams getAlipayLayoutParams() {
        return (LinearLayout.LayoutParams) findViewById(R.id.UIPaySendSMSInput2).getLayoutParams();
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

    private String[] mPhoneString = null;

    @Override
    public void accountBoxSetContent(int ret, Object params) {
        Intent localContacts = (Intent) params;
        RootActivity activity = (RootActivity) mPage.getEngine().getContext();

        if (localContacts == null) {
            return;
        }
        if (!LephoneConstant.isLephone()) {
            inputBox.setText("");
            ArrayList<String> tPhone = BaseHelper.getContactPhoneNo(localContacts.getData(),
                (Activity) mPage.getEngine().getContext());

            if (tPhone.size() > 1) {

                mPhoneString = new String[tPhone.size()];
                for (int i = 0; i < tPhone.size(); i++) {
                    mPhoneString[i] = tPhone.get(i);
                }

                new AlertDialog.Builder(mPage.getEngine().getContext())
                    .setTitle(R.string.SelectContact)
                    .setSingleChoiceItems(mPhoneString, 0, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            inputBox.setText(mPhoneString[which]);
                            inputBox.setSelection(mPhoneString[which].length());
                            dialog.dismiss();
                        }

                    }).show();
            } else if (tPhone.size() == 1) {
                inputBox.setText(tPhone.get(0));
                inputBox.setSelection(tPhone.get(0).length());
            } else {
                inputBox.setText("");
                inputBox.setHint(mPage.getEngine().getContext()
                    .getString(R.string.NoContactPhoneNo));
            }
        } else if (LephoneConstant.isLephone()) {
            try {
                Uri contactData = localContacts.getData();
                Cursor c = activity.managedQuery(contactData, null, null, null, null);
                if (c.moveToFirst()) {
                    String number = c.getString(c.getColumnIndexOrThrow("data1"));
                    inputBox.setText(number);
                }
            } catch (Exception e) {
                inputBox.setText("");
                inputBox.setHint(mPage.getEngine().getContext()
                    .getString(R.string.NoContactPhoneNo));
            }
        }

    }

    public EditText getEditContent() {
        return inputBox;
    }

    @Override
    public boolean Inputed() {
        Editable editable = inputBox.getText();
        if (editable != null && editable.toString().trim().length() > 0)
            return true;
        return false;
    }
}
