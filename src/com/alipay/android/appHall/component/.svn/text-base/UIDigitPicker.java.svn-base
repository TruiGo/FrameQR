/**
 * 
 */
package com.alipay.android.appHall.component;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Random;

import android.app.Activity;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.TableLayout;
import android.widget.ToggleButton;

import com.alipay.android.appHall.Helper;
import com.alipay.android.appHall.uiengine.Computable;
import com.alipay.android.appHall.uiengine.InputCheckListener;
import com.alipay.android.appHall.uiengine.UIInterface;
import com.alipay.android.client.RootActivity;
import com.alipay.android.client.util.BaseHelper;
import com.alipay.android.core.expapp.Page;
import com.eg.android.AlipayGphone.R;

/**
 * 数字选择器
 * @author sanping.li
 *
 */
public class UIDigitPicker extends LinearLayout implements UIInterface, Computable,
                                               InputCheckListener {
    public static final String TYPE_MACHINE_ENTITY = "entity";
    public static final String TYPE_MACHINE_GROUP = "group";
    public static final int TYPE_RED_BALL = 1;
    public static final int TYPE_BLUE_BALL = 2;
    /**
     * 一行球的个数
     */
    private static final int NUMS_ONE_LINE = 9;
    /**
     * 备选数字
     */
    private ArrayList<String> alterNums;
    /**
     * 最小选择数量
     */
    private int mMin;
    /**
     * 最多选择数量
     */
    private int mMax;
    /**
     * 每注数量
     */
    private int mNum;
    /**
     * 机选是否去除重复
     */
    private boolean distinct;

    private Random random = new Random();

    /**
     * 选择的数字
     */
    private ArrayList<String> mValue;

    private String desc;

    private LinearLayout linearLayout;
    
    private Page mPage;

    private CompoundButton.OnCheckedChangeListener onCheckedChangeListener = new CompoundButton.OnCheckedChangeListener() {
        @Override
        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
            if (isChecked) {
                if (mValue.size() >= mMax)
                    buttonView.setChecked(false);
                else
                    mValue.add(buttonView.getText().toString());
            } else
                mValue.remove(buttonView.getText().toString());
        }
    };

    public UIDigitPicker(Page page) {
        super(page.getEngine().getContext());
        mPage = page;
        mValue = new ArrayList<String>();
    }

    /**
     * 初始化
     * @param parent 
     * @param type 类型，球的类型
     * @param name 名称：红球，篮球
     * @param alternatives 备选数字
     * @param min 最少
     * @param max 最多
     * @param randNum 随机
     * @param dis 是否可重复
     */
    public void init(ViewGroup parent, int type, String alternatives, String min, String max,
                     String randNum, String dis) {
        List<String> s = Arrays.asList(alternatives.split(","));
        if (s != null) {
            alterNums = new ArrayList<String>();
            alterNums.addAll(s);
        }
        mMin = Integer.parseInt(min);
        mMax = Integer.parseInt(max);
        mNum = Integer.parseInt(randNum);
        distinct = !dis.equals("false");

        linearLayout = (LinearLayout) LayoutInflater.from(getContext()).inflate(R.layout.digit_ssq,
            parent, false);
        TableLayout tableLayout = (TableLayout) linearLayout.findViewById(R.id.content);
        addBalls(tableLayout, type);

        this.addView(linearLayout,Helper.copyLinearLayoutParams((LayoutParams) linearLayout.getLayoutParams()));
    }

    /**
     * 添加球
     */
    private void addBalls(TableLayout tableLayout, int type) {
        int num = (alterNums.size() + NUMS_ONE_LINE - 1) / NUMS_ONE_LINE;
        LinearLayout line = null;
        ToggleButton toggle = null;
        int index = 0;
        for (int i = 0; i < num; ++i) {
            line = new LinearLayout(getContext());
            for (int j = 0; j < NUMS_ONE_LINE; ++j) {
                if (index < alterNums.size()) {
                    toggle = new ToggleButton(getContext());
                    switch (type) {
                        case TYPE_RED_BALL:
                            toggle.setBackgroundResource(R.drawable.ssq_ball_red);
                            break;
                        case TYPE_BLUE_BALL:
                            toggle.setBackgroundResource(R.drawable.ssq_ball_blue);
                            break;
                    }
                    toggle.setTextOn(alterNums.get(index));
                    toggle.setTextOff(alterNums.get(index));
                    toggle.setTag(alterNums.get(index));
                    toggle.setTextColor(0xFFFFFFFF);
                    toggle.setChecked(false);
                    toggle.setOnCheckedChangeListener(onCheckedChangeListener);

                    LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                        LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
                    params.setMargins(2, 2, 2, 2);
                    line.addView(toggle, params);
                    index++;
                }
            }
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
            params.gravity = Gravity.CENTER_HORIZONTAL;
            tableLayout.addView(line, params);
        }
    }

    /**
     * 清空状态
     */
    private void clear() {
        ToggleButton toggle = null;
        ArrayList<String> array = new ArrayList<String>(mValue);
        ArrayList<String> indexs = new ArrayList<String>(mValue);
        mValue = array;
        for (String str : indexs) {
            toggle = (ToggleButton) linearLayout.findViewWithTag(str);
            toggle.setChecked(false);
        }
    }

    /**
     * 机选
     * @param num 
     */
    private ArrayList<String> randomGen(int num) {
        ArrayList<String> alls = new ArrayList<String>();
        alls.addAll(alterNums);

        ArrayList<String> ret = new ArrayList<String>();
        int all = alls.size();
        for (int i = 0; i < num; i++) {
            int seli = random.nextInt(all);
            ret.add(alls.get(seli));
            if (distinct) {
                alls.remove(seli);
                all = alls.size();
            }
        }
        return ret;
    }

    /**
     * 
     * 机选多注
     */
    private ArrayList<ArrayList<String>> randomGroup(int n) {
        ArrayList<ArrayList<String>> ret = new ArrayList<ArrayList<String>>();
        for (int i = 0; i < n; ++i) {
            ret.add(randomGen(mNum));
        }
        return ret;
    }

    @Override
    public boolean inputCheck() {
        if (getVisibility()==ViewGroup.GONE || !getEnable()) {
            return true;
        }

        RootActivity activity = (RootActivity) mPage.getEngine().getContext();
        if (mValue.size() < mMin || mValue.size() > mMax) {
            activity.getDataHelper().showDialog((Activity) getContext(), R.drawable.infoicon,
                getResources().getString(R.string.WarngingString), desc,
                getResources().getString(R.string.Ensure), null, null, null, null, null);
            
            BaseHelper.recordWarningMsg(activity, desc);
            return false;
        } else
            return true;
    }

    @Override
    public Object compute(ArrayList<Object> params) {
        if (params != null && params.size() == 2) {
            String type = (String) params.get(0);
            int num = Integer.parseInt((String) params.get(1));
            if (type.equals(TYPE_MACHINE_GROUP)) {
                ArrayList<ArrayList<String>> arrayList = randomGroup(num);
                ArrayList<String> sels = arrayList.get(arrayList.size() - 1);
                setState(sels);
                return arrayList;
            } else if (type.equals(TYPE_MACHINE_ENTITY)) {
                ArrayList<String> sels = randomGen(num);
                setState(sels);
                return sels;
            }
        }
        return null;
    }

    private void setState(ArrayList<String> sels) {
        clear();
        ToggleButton toggle = null;
        for (String str : sels) {
            toggle = (ToggleButton) linearLayout.findViewWithTag(str);
            toggle.setChecked(true);
        }
        mValue = sels;
    }

    @Override
    public void reset() {
        clear();
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
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
        // TODO Auto-generated method stub

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
        return null;
    }

    @Override
    public void setIsSave(boolean isSave) {
        //        throw new UnsupportedOperationException("setIsSave(boolean) is not supported in UIInterface");
    }

    @Override
    public boolean getIsSave() {
        return false;
    }

    @Override
    public void set_MarginLeft(int marginLeft) {
        MarginLayoutParams params = (MarginLayoutParams) linearLayout.getLayoutParams();
        params.leftMargin = marginLeft - 5;
    }

    @Override
    public int get_MarginLeft() {
        MarginLayoutParams params = (MarginLayoutParams) linearLayout.getLayoutParams();
        return params.leftMargin;
    }

    @Override
    public void set_MarginRight(int marginRight) {
        MarginLayoutParams params = (MarginLayoutParams) linearLayout.getLayoutParams();
        params.rightMargin = marginRight;
    }

    @Override
    public int get_MarginRight() {
        MarginLayoutParams params = (MarginLayoutParams) linearLayout.getLayoutParams();
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
        //        throw new UnsupportedOperationException("set_Text(String) is not supported in UIInterface");
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

    @Override
    public void setMaxCharNum(String maxCharNum) {
    }

    @Override
    public void setMinCharNum(String minCharNum) {
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
    
    @Override
    public boolean Inputed() {
        if(mValue!=null&&mValue.size()>0)
            return true;
        return false;
    }
}
