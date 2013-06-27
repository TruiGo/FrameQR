package com.alipay.android.appHall.component;

import java.util.ArrayList;
import java.util.HashMap;

import org.json.JSONObject;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.database.DataSetObserver;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.Spinner;
import android.widget.SpinnerAdapter;
import android.widget.TextView;

import com.alipay.android.appHall.uiengine.UIInterface;
import com.alipay.android.appHall.uiengine.UIList;
import com.alipay.android.core.expapp.Page;
import com.alipay.android.util.JsonConvert;
import com.eg.android.AlipayGphone.R;

public class UIComboBox extends LinearLayout implements UIInterface, UIList {

    private ArrayList<Object> itemExps = null;

    private Spinner mSpinner;
    private String expression;
    private ArrayList<Object> data;
    private ComboBoxAdapter arrayAdapter;

    private HashMap<String, Object> dataAdapter; //数据适配，xml定义的

    private Page mPage;

    public UIComboBox(Page page) {
        super(page.getRawContext());
        mPage = page;
    }

    public void init(ViewGroup parent) {
        data = new ArrayList<Object>();
        mSpinner = new ComboBox(mPage.getRawContext());
        mSpinner.setBackgroundResource(R.drawable.alipay_dropdown_button);

        this.addView(mSpinner);

        LinearLayout.LayoutParams LinearLayoutParams = new LinearLayout.LayoutParams(
            LayoutParams.FILL_PARENT, LayoutParams.WRAP_CONTENT);
        this.setLayoutParams(LinearLayoutParams);

        LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) mSpinner.getLayoutParams();
        params.leftMargin = 15;
        params.rightMargin = 15;
        params.topMargin = 10;
        params.bottomMargin = 0;
        params.height = ViewGroup.LayoutParams.WRAP_CONTENT;
        mSpinner.setEnabled(false);
    }

    public ArrayList<Object> getData() {
        return data;
    }

    @Override
    public void setData(ArrayList<Object> value) {
        if (value == null && data.size() == 0) {
            mSpinner.setEnabled(false);
            return;
        }
        mSpinner.setEnabled(true);
        data.clear();
        data.addAll(value);
        arrayAdapter = new ComboBoxAdapter(data);
        mSpinner.setAdapter(arrayAdapter);
        mSpinner.setSelection(0, true);
        mSpinner.setOnItemSelectedListener(mSpinnerListener);
    }

    @Override
    public void addData(ArrayList<Object> value) {
        //        if (value == null&&data.size() == 0) {
        //            mSpinner.setEnabled(false);
        //            return;
        //        }
        //        mSpinner.setEnabled(true);
        //        
        //        data.addAll(value);
        //        arrayAdapter = new ComboBoxAdapter(data);
        //        mSpinner.setAdapter(arrayAdapter);
        //        mSpinner.setSelection(0, true);
        //        mSpinner.setOnItemSelectedListener(mSpinnerListener);
    }

    public void setDataAdapter(String adapter) {
        try {
            JSONObject jsonObject = new JSONObject(adapter);
            dataAdapter = JsonConvert.Json2Map(jsonObject);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    class ComboBox extends Spinner {
        private ComboBox(Context context) {
            super(context);
        }

        public void onClick(DialogInterface dialog, int position) {
            mSpinner.setSelection(position, true);
            String exp = getExpression();

            Object dataItem = data.get(position);
            if (dataItem instanceof HashMap<?, ?> && dataAdapter != null) {
                @SuppressWarnings("unchecked")
                Object o = ((HashMap<String, Object>) dataItem).get(dataAdapter.get(EXPINDEX));
                if (o != null && itemExps != null) {
                    int expIndex = Integer.valueOf((String) o);
                    Object str = itemExps.get(expIndex);
                    if (str != null && str.toString().trim().length() > 0) {
                        exp = str.toString().trim();
                    }
                }
            }

            Object id = getTag()==null?"":getTag();
            mPage.interpreter("combobox::"+id.toString(),exp);
            dialog.dismiss();
        }

        @Override
        public boolean performClick() {
            Context context = getContext();
            final DropDownAdapter adapter = new DropDownAdapter(mSpinner.getAdapter());

            AlertDialog.Builder builder = new AlertDialog.Builder(context);
            if (mSpinner.getPrompt() != null) {
                builder.setTitle(mSpinner.getPrompt());
            }
            builder.setSingleChoiceItems(adapter, mSpinner.getSelectedItemPosition(), mSpinner)
                .show();
            return true;
        }
    }

    class ComboBoxAdapter implements SpinnerAdapter {
        private ArrayList<Object> mData;

        public ComboBoxAdapter(ArrayList<Object> data) {
            mData = data;
        }

        public HashMap<String, Object> getDataAdapter() {
            return dataAdapter;
        };

        public ArrayList<Object> getdata() {
            if (mData.size() == 0) {
                return null;
            }
            return mData;
        }

        @Override
        public int getCount() {
            return mData.size();
        }

        @Override
        public Object getItem(int position) {
            if (mData.size() == 0) {
                return null;
            }
            return mData.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public int getItemViewType(int position) {
            return 0;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            if (mData.size() == 0) {
                return null;
            }
            if (convertView == null) {
                convertView = new TextView(mPage.getRawContext());//LayoutInflater.from(context).inflate(android.R.layout.simple_list_item_1, null);
            }
            TextView text = (TextView) convertView;//.findViewById(android.R.id.text1);
            text.setTextSize(16);
            text.setTextColor(getContext().getResources().getColor(R.color.TextColorBlack));
            Object object = mData.get(position);
            if (object instanceof HashMap<?, ?> && dataAdapter != null) {
                Object o = ((HashMap<?, ?>) object).get(dataAdapter.get(TEXT));
                if (o != null)
                    text.setText(o.toString());
            } else
                text.setText(object.toString());
            text.setEllipsize(TextUtils.TruncateAt.valueOf("END"));
            text.setLines(1);
            return convertView;
        }

        @Override
        public int getViewTypeCount() {
            return 1;
        }

        @Override
        public boolean hasStableIds() {
            return false;
        }

        @Override
        public boolean isEmpty() {
            return false;
        }

        @Override
        public void registerDataSetObserver(DataSetObserver observer) {
            // TODO Auto-generated method stub

        }

        @Override
        public void unregisterDataSetObserver(DataSetObserver observer) {
            // TODO Auto-generated method stub

        }

        public boolean isEnabled(int position) {
            if (mData.size() == 0) {
                return false;
            }

            Object object = mData.get(position);
            Object o = null;
            if (object instanceof HashMap<?, ?> && dataAdapter != null) {
                o = ((HashMap<?, ?>) object).get(dataAdapter.get(STATE));
            }
            if (o != null && DISABLE.equals(o)) {
                return false;
            }
            return true;
        }

        @Override
        public View getDropDownView(int position, View convertView, ViewGroup parent) {
            if (mData.size() == 0) {
                return null;
            }
            if (convertView == null) {
                convertView = LayoutInflater.from(mPage.getRawContext()).inflate(
                    android.R.layout.simple_spinner_dropdown_item, null);
                convertView.setBackgroundResource(R.drawable.combobox_item_bg);
            }
            TextView text = (TextView) convertView.findViewById(android.R.id.text1);
            Object object = mData.get(position);
            if (object instanceof HashMap<?, ?> && dataAdapter != null) {
                text.setText(((HashMap<?, ?>) object).get(dataAdapter.get(TEXT)).toString());

                object = ((HashMap<?, ?>) object).get(dataAdapter.get(STATE));
                if (object != null && DISABLE.equals(object)) {
                    text.setTextColor(getContext().getResources().getColor(R.color.TextColorGray));
                }

            } else
                text.setText(object.toString());
            text.setEllipsize(TextUtils.TruncateAt.valueOf("END"));
            return convertView;
        }
    }

    /**
     * <p>Wrapper class for an Adapter. Transforms the embedded Adapter instance
     * into a ListAdapter.</p>
     */
    private static class DropDownAdapter implements ListAdapter, SpinnerAdapter {
        private SpinnerAdapter mAdapter;

        /**
         * <p>Creates a new ListAddapter wrapper for the specified adapter.</p>
         *
         * @param adapter the Adapter to transform into a ListAdapter
         */

        public DropDownAdapter(SpinnerAdapter adapter) {
            this.mAdapter = adapter;
        }

        public int getCount() {
            return mAdapter == null ? 0 : mAdapter.getCount();
        }

        public Object getItem(int position) {
            return mAdapter == null ? null : mAdapter.getItem(position);
        }

        public long getItemId(int position) {
            return mAdapter == null ? -1 : mAdapter.getItemId(position);
        }

        public View getView(int position, View convertView, ViewGroup parent) {
            return getDropDownView(position, convertView, parent);
        }

        public View getDropDownView(int position, View convertView, ViewGroup parent) {
            return mAdapter.getDropDownView(position, convertView, parent);
        }

        public boolean hasStableIds() {
            return mAdapter != null && mAdapter.hasStableIds();
        }

        public void registerDataSetObserver(DataSetObserver observer) {
            if (mAdapter != null) {
                mAdapter.registerDataSetObserver(observer);
            }
        }

        public void unregisterDataSetObserver(DataSetObserver observer) {
            if (mAdapter != null) {
                mAdapter.unregisterDataSetObserver(observer);
            }
        }

        /**
         * <p>Always returns false.</p>
         *
         * @return false
         */
        public boolean areAllItemsEnabled() {
            return false;
        }

        /**
         * <p>Always returns false.</p>
         *
         * @return false
         */
        public boolean isEnabled(int position) {
            return ((ComboBoxAdapter) mAdapter).isEnabled(position);
        }

        public int getItemViewType(int position) {
            return 0;
        }

        public int getViewTypeCount() {
            return 1;
        }

        public boolean isEmpty() {
            return getCount() == 0;
        }
    }

    public boolean isSave() {
        return isSave;
    }

    public void setSave(boolean isSave) {
        this.isSave = isSave;
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
        // TODO Auto-generated method stub
        int n = data.size();
        for (int i = 0; i < n; i++) {
            Object object = data.get(i);
            if (object instanceof HashMap<?, ?>) {
                if (value.toString().equals(
                    ((HashMap<?, ?>) object).get(dataAdapter.get(VALUE)).toString())) {
                    if (i == mSpinner.getSelectedItemPosition()) {
                        return;
                    }
                    mSpinner.setSelection(i);
                    break;
                }
            } else {
                if (value.toString().equals(object.toString())) {
                    if (i == mSpinner.getSelectedItemPosition()) {
                        return;
                    }
                    mSpinner.setSelection(i);
                    break;
                }
            }
        }
    }

    @Override
    public Object getValue() {
        if (data.size() == 0) {
            return null;
        }
        Object object = data.get(mSpinner.getSelectedItemPosition());
        if (object instanceof HashMap<?, ?> && dataAdapter != null)
            return ((HashMap<?, ?>) object).get(dataAdapter.get(VALUE)).toString();
        else
            return object.toString();
    }

    @Override
    public void setEnable(boolean enabled) {
        mSpinner.setEnabled(enabled);
    }

    @Override
    public boolean getEnable() {
        // TODO Auto-generated method stub
        return mSpinner.isEnabled();
    }

    Spinner.OnItemSelectedListener mSpinnerListener = new Spinner.OnItemSelectedListener() {
        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        }

        public void onNothingSelected(AdapterView<?> arg0) {
        }
    };

    // ----------------------------设置宽度
    private boolean isChange = true;
    private int width = ViewGroup.LayoutParams.FILL_PARENT;

    public void set_Width(int width) {
        isChange = true;
        this.width = width;
        changedWidth();
    }

    private void changedWidth() {
        ViewGroup.LayoutParams params = (ViewGroup.LayoutParams) mSpinner.getLayoutParams();
        params.width = this.width;
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
        LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) mSpinner.getLayoutParams();
        params.leftMargin = marginLeft;
    }

    @Override
    public int get_MarginLeft() {
        LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) mSpinner.getLayoutParams();
        return params.leftMargin;
    }

    @Override
    public void set_MarginRight(int marginRight) {
        LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) mSpinner.getLayoutParams();
        params.rightMargin = marginRight;
    }

    @Override
    public int get_MarginRight() {
        LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) mSpinner.getLayoutParams();
        return params.rightMargin;
    }

    @Override
    public void set_MarginTop(int marginTop) {
        LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) mSpinner.getLayoutParams();
        params.topMargin = marginTop;
    }

    @Override
    public int get_MarginTop() {
        LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) mSpinner.getLayoutParams();
        return params.topMargin;
    }

    @Override
    public void set_MarginBottom(int marginBottom) {
        LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) mSpinner.getLayoutParams();
        params.bottomMargin = marginBottom;
    }

    @Override
    public int get_MarginBottom() {
        LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) mSpinner.getLayoutParams();
        return params.bottomMargin;
    }

    @Override
    public int get_Width() {
        return getWidth();
    }

    //-----------------------------对于margin bug的特殊处理
    private boolean isfirst = true;

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        LinearLayout.LayoutParams paramsIn = (LinearLayout.LayoutParams) mSpinner.getLayoutParams();
        LinearLayout.LayoutParams paramsOut = (LinearLayout.LayoutParams) getLayoutParams();
        if ((paramsOut.leftMargin != 0 || paramsOut.rightMargin != 0 || paramsOut.topMargin != 0 || paramsOut.bottomMargin != 0)
            && isfirst) {
            calMargin(paramsIn, paramsOut);
            isfirst = false;
        }

        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }

    private void calMargin(LinearLayout.LayoutParams paramsIn, LinearLayout.LayoutParams paramsOut) {
        paramsIn.leftMargin = paramsIn.leftMargin / 2;
        paramsIn.rightMargin = paramsIn.rightMargin / 2;
        paramsIn.topMargin = paramsIn.topMargin / 2;
        paramsIn.bottomMargin = paramsIn.bottomMargin / 2;
    }

    //------------------------------------

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
        if (data.size() == 0) {
            return null;
        }
        return (String) data.get(mSpinner.getSelectedItemPosition());
    }

    @Override
    public String getSelectedIndex() {
        return String.valueOf(mSpinner.getSelectedItemPosition());
    }

    @Override
    public String get_Tag() {
        return (String) this.getTag();
    }

    @Override
    public void set_Tag(String tag) {
        this.setTag(tag);
    }

    public void set_Hint(String value) {
        mSpinner.setPrompt(value);
    }

    @Override
    public void setItemExps(ArrayList<Object> itemExps) {
        if (this.itemExps == null && itemExps != null) {
            this.itemExps = itemExps;
        }
    }

    @Override
    public String getItemId() {
        if (data.size() == 0) {
            return null;
        }
        Object object = data.get(mSpinner.getSelectedItemPosition());
        if (object instanceof HashMap<?, ?> && dataAdapter != null)
            return ((HashMap<?, ?>) object).get(dataAdapter.get(VALUE)).toString();
        else
            return object.toString();
    }

    @Override
    public void setTotal(int total) {
    }

    @Override
    public int getCurrentPage() {
        return 0;
    }

    @Override
    public int getPageSize() {
        return 0;
    }

    @Override
    public void clearData() {
        if (data != null)
            data.clear();
        invalidate();
    }

    //    private String desExp;

    @Override
    public void setDelExp(String desExp) {
        //        this.desExp = desExp;
    }

    //    private boolean deletable;

    @Override
    public void setDeletable(String deletable) {
        //        if (deletable == null || deletable.length() < 1) {
        //            return;
        //        }
        //        String tempDel = deletable.trim();
        //        this.deletable = "true".equals(tempDel) ? true : false;
    }

    @Override
    public LinearLayout.LayoutParams getAlipayLayoutParams() {
        return (android.widget.LinearLayout.LayoutParams) mSpinner.getLayoutParams();
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
