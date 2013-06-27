package com.alipay.android.appHall.component;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.alipay.android.appHall.uiengine.UIInterface;
import com.eg.android.AlipayGphone.R;

public class UIDatePicker extends Button implements UIInterface, OnClickListener {

    private Context context;
    private View pickerLayout;
    private AlertDialog.Builder tDialog;

    public UIDatePicker(Context context) {
        super(context);
        this.context = context;
        this.setBackgroundResource(R.drawable.datepicker_btn);
    }

    public void init(ViewGroup parent) {
        this.setOnClickListener(this);
    }

    public void setVisible(boolean isVisible) {
        setVisibility(isVisible ? View.VISIBLE : View.GONE);
    }

    // true为visible，false为gone
    public boolean getVisible() {
        return getVisibility() == View.VISIBLE;
    }

    @Override
    public void setValue(Object value) {
        setText((CharSequence) value);
    }

    @Override
    public Object getValue() {
        return getText();
    }

    @Override
    public String getSelectedIndex() {
        return null;
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
    public void set_Width(int width) {
        LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) getLayoutParams();
        params.width = width;
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
        setText(text);
    }

    @Override
    public String get_Text() {
        return (String) getText();
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
    public void onClick(View v) {
        pickerLayout = LayoutInflater.from(context).inflate(R.layout.uidatepicker, null, false);
        TextView currentTime = (TextView) pickerLayout.findViewById(R.id.currentTime);
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy年MM月份");
        Date curDate = new Date(System.currentTimeMillis());
        String time = formatter.format(curDate);
        currentTime.setText(time);

        boolean isError = false;
        final DatePicker datePicker = (DatePicker) pickerLayout.findViewById(R.id.datePicker1);

        try {
            ViewGroup group = (ViewGroup) datePicker.getChildAt(0);
            View dayView = (View) group.getChildAt(2);
            dayView.setVisibility(View.GONE);
        } catch (Exception e) {
            isError = true;
        }

        if (isError) {
            Toast toast = Toast.makeText(context, "无法获取当前时间", Toast.LENGTH_LONG);
            toast.setGravity(Gravity.CENTER, 0, 0);
            toast.show();
            return;
        }

        tDialog = new AlertDialog.Builder(context);
        tDialog.setView(pickerLayout);
        tDialog.setPositiveButton("确定", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                //这里添加返回数据的方法
                int mYear = datePicker.getYear();
                int mMonth = datePicker.getMonth() + 1;
                UIDatePicker.this.setText(mYear + "年" + mMonth + "月份");
                dialog.dismiss();
            }
        });
        tDialog.setNegativeButton("取消", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        tDialog.show();
    }

    @Override
    public LinearLayout.LayoutParams getAlipayLayoutParams() {
        return (android.widget.LinearLayout.LayoutParams) getLayoutParams();
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
}
