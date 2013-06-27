/**
 * 
 */
package com.alipay.android.appHall.component;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.view.ViewGroup.MarginLayoutParams;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.ListAdapter;

import com.eg.android.AlipayGphone.R;

/**
 * @author sanping.li
 *
 */
public class UINumPicker extends UIButton {
    /**
     * 待选数字
     */
    private String[] mAlts;
    /**
     * 对话框标题
     */
    private String mTitle;

    private String mValue;
    private String mExpression;
    private ListAdapter mListAdapter;
    private OnClickListener listener;
    private int mCheckedItem;

    private OnClickListener onClickListener = new OnClickListener() {

        @Override
        public void onClick(View v) {
            AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
            builder.setTitle(mTitle);
            mListAdapter = new ArrayAdapter<String>(getContext(),
                android.R.layout.simple_spinner_dropdown_item, mAlts);
            builder.setSingleChoiceItems(mListAdapter, mCheckedItem, DialogOnClickListener).show();
            //new NumPicker(getContext()).show();
        }
    };

   private DialogInterface.OnClickListener DialogOnClickListener = new DialogInterface.OnClickListener() {
        @Override
        public void onClick(DialogInterface dialog, int which) {
            mValue = (String) mListAdapter.getItem(which);
            mCheckedItem = which;
            listener.onClick(UINumPicker.this);
            dialog.dismiss();
        }
    };

    public UINumPicker(Context context) {
        super(context);
    }

    public void init(ViewGroup parent, String alt, String title) {
        mAlts = alt.split(",");
        mTitle = title;

        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
            LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
        layoutParams.leftMargin = 15;
        layoutParams.rightMargin = 15;
        this.setLayoutParams(layoutParams);
        this.setOnClickListener(onClickListener);

        this.setTextColor(getContext().getResources().getColor(R.color.ButtonColorBlue));
        this.setBackgroundResource(R.drawable.button_small);
        this.setTextSize(19);
        float scale = getContext().getResources().getDisplayMetrics().density;
        int size = (int) (5 * scale+0.5f); 
        this.setPadding(size, size, size, size);
    }

    public void setExpression(String expression) {
        mExpression = expression;
    }

    public String getExpression() {
        return mExpression;
    }

    public OnClickListener getListener() {
        return listener;
    }

    public void setListener(OnClickListener listener) {
        this.listener = listener;
    }

    //-----------------------UI的接口-------------------------------
    @Override
    public void setVisible(boolean isVisible) {
        setVisibility(isVisible ? View.VISIBLE : View.GONE);
    }

    @Override
    public boolean getVisible() {
        return getVisibility() == View.VISIBLE;
    }

    @Override
    public void setValue(Object value) {

    }

    @Override
    public Object getValue() {
        return mValue;
    }

    @Override
    public void setEnable(boolean isEnable) {
        setEnabled(isEnable);
    }

    @Override
    public boolean getEnable() {
        return isEnabled();
    }

    @Override
    public String getSelectedIndex() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public void setIsSave(boolean isSave) {

    }

    @Override
    public boolean getIsSave() {
        return false;
    }

    @Override
    public void set_MarginLeft(int marginLeft) {
        MarginLayoutParams params = (MarginLayoutParams) getLayoutParams();
        params.leftMargin = marginLeft;
        setLayoutParams(params);
    }

    @Override
    public int get_MarginLeft() {
        MarginLayoutParams params = (MarginLayoutParams) getLayoutParams();
        return params.leftMargin;
    }

    @Override
    public void set_MarginRight(int marginRight) {
        MarginLayoutParams params = (MarginLayoutParams) getLayoutParams();
        params.rightMargin = marginRight;
        setLayoutParams(params);
    }

    @Override
    public int get_MarginRight() {
        MarginLayoutParams params = (MarginLayoutParams) getLayoutParams();
        return params.rightMargin;
    }

    @Override
    public void set_MarginTop(int marginTop) {
        LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) getLayoutParams();
        params.topMargin = marginTop;
        setLayoutParams(params);
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
        setLayoutParams(params);
    }

    @Override
    public int get_MarginBottom() {
        LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) getLayoutParams();
        return params.bottomMargin;
    }

    private boolean isChange = true;
    private int width = ViewGroup.LayoutParams.FILL_PARENT;

    public void set_Width(int width) {
        isChange = true;
        this.width = width;
        changedWidth();
    }

    private void changedWidth() {
        ViewGroup.LayoutParams params = (ViewGroup.LayoutParams) getLayoutParams();
        params.width = this.width;
        setLayoutParams(params);
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
        return null;
    }

    @Override
    public String get_Tag() {
        return (String) getTag();
    }

    @Override
    public void set_Tag(String tag) {
        setTag(tag);
    }

}
