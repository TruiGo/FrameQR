package com.alipay.android.appHall.component;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Set;

import org.json.JSONObject;

import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.alipay.android.appHall.uiengine.UIInterface;
import com.alipay.android.appHall.uiengine.UIList;
import com.alipay.android.comon.component.StyleAlertDialog;
import com.alipay.android.core.expapp.Page;
import com.alipay.android.util.JsonConvert;
import com.alipay.android.util.LogUtil;
import com.eg.android.AlipayGphone.R;

public class UIListView extends LinearLayout implements UIInterface, UIList {

    /**
     * config数据，可以放到资源文件做升级
     */
    //    public static final int[] LAYOUT_ID = {R.layout.single_line_adapter,"/data/data/xxx/a.xml"};
    public static final int[] LAYOUT_ID = { R.layout.single_line_adapter,
                                           R.layout.getmoney_adapter, R.layout.single_line_adapter,
                                           R.layout.list_item_records,
                                           R.layout.phone_charge_list_item, R.layout.lottery_list };

    @SuppressWarnings("unchecked")
    public static final HashMap<String, Object>[] DATA_MAP = new HashMap[6];
    static {
        DATA_MAP[0] = new HashMap<String, Object>();
        DATA_MAP[0].put("text", R.id.single_line_content);
        DATA_MAP[0].put("image", R.id.single_line_image);

        DATA_MAP[1] = new HashMap<String, Object>();
        DATA_MAP[1].put("nameinfo", R.id.nameinfo);
        DATA_MAP[1].put("acountinfo", R.id.acountinfo);
        DATA_MAP[1].put("moneyinfo", R.id.moneyinfo);

        DATA_MAP[2] = new HashMap<String, Object>();
        DATA_MAP[2].put("text", R.id.single_line_content);

        DATA_MAP[3] = new HashMap<String, Object>();
        DATA_MAP[3].put("title", R.id.lottery_title);
        DATA_MAP[3].put("amount", R.id.lottery_amount);
        DATA_MAP[3].put("date", R.id.lottery_date);
        DATA_MAP[3].put("status", R.id.lottery_status);

        DATA_MAP[4] = new HashMap<String, Object>();
        DATA_MAP[4].put("tPhoneFee", R.id.PhoneFee);
        DATA_MAP[4].put("tPhoneReal", R.id.PhoneReal);
        DATA_MAP[4].put("tPhoneFlag", R.id.PhoneFlag);

        DATA_MAP[5] = new HashMap<String, Object>();
        DATA_MAP[5].put("red", R.id.red_ball);
        DATA_MAP[5].put("blue", R.id.blue_ball);
    }

    public static final int SINGLELINE_WITHIMAGE_ADAPTER = 0;
    public static final int GETMONEY_ADAPTER = 1;
    public static final int SINGLELINE_WITHOUTIMAGE_ADAPTER = 2;

    public static final int DOUBLELINES_ADAPTER = 3;
    public static final int PHONECHARGE_ADAPTER = 4;
    public static final int LOTTERY_ADAPTER = 5;

    public static final String EXPINDEX = "expindex";

    private ArrayList<Object> itemExps = null;

    private Context context;
    private String expression;
    private ListView listView;
    private UIListAdapter adapter;
    private HashMap<String, Object> dataAdapter; //数据适配，xml定义的
    private ArrayList<HashMap<String, Object>> arrayList;
    private Page mPage;
    private int mSelected;
    private TextView mEmptyView;
    private View topLine;
    private View bottomLine;

    public UIListView(Context context, Page page) {
        super(context);
        mPage = page;
        this.context = context;
        page.setScrollBarEnabled(false);
        page.getContent().setBackgroundResource(R.drawable.application_bg_white);
        this.setOrientation(VERTICAL);
    }
    
    public void setEmptyContent(String emptyContent){
        mEmptyView = new TextView(context);
        mEmptyView.setText(emptyContent);
        mEmptyView.setTextColor(Color.BLACK);
        mEmptyView.setTextSize(16);
        mEmptyView.setGravity(Gravity.CENTER_HORIZONTAL);
        mEmptyView.setVisibility(View.GONE);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LayoutParams.FILL_PARENT,LayoutParams.WRAP_CONTENT);
        this.addView(mEmptyView,params);
    }

    public void init(ViewGroup parent, int adapterType) {
        arrayList = new ArrayList<HashMap<String, Object>>();

        listView = (ListView) LayoutInflater.from(context).inflate(R.layout.uilistview, parent,
            false);

        listView.setOnScrollListener(new OnScrollListener() {

            private boolean onMoreRequest;

            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {
                if (onMoreRequest&&scrollState==SCROLL_STATE_IDLE) {
                    onMoreRequest = false;
                    Object id = getTag()==null?"":getTag();
                    //去执行联网动作
                    mPage.getEngine().getCurrentPage().interpreter("list-moreRequest::"+id.toString(),
                        addMoreExpression);
                    LogUtil.logOnlyDebuggable("qq", addMoreExpression);
                }
            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount,
                                 int total) {
                if(firstVisibleItem+visibleItemCount==total&& listView.getCount() < totalItemCount){
                    onMoreRequest = true;
                }
            }
        });

        listView.setOnItemClickListener(new OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> arg0, View view, int position, long arg3) {
                mSelected = (Integer) view.getTag();
                HashMap<String, ?> hashMap = arrayList.get(mSelected);
                Object object = hashMap.get(EXPINDEX);
                String exp = expression;
                if (object != null && itemExps != null) {
                    int index = Integer.valueOf(object.toString().trim());
                    if (index >= 0 && index < itemExps.size())
                        exp = itemExps.get(index).toString().trim();
                }
                if (exp != null && exp.length() > 0){
                    Object id = getTag()==null?"":getTag();
                    mPage.interpreter("list-click::"+id.toString(),exp);
                }
            }
        });

        topLine = new View(context);
        topLine.setBackgroundDrawable(new ColorDrawable(Color.WHITE));
        topLine.setVisibility(View.GONE);
        LinearLayout.LayoutParams tParams = new LinearLayout.LayoutParams(LayoutParams.FILL_PARENT,1);
        this.addView(topLine,tParams);
        
        this.addView(listView);
        
        bottomLine = new View(context);
        bottomLine.setBackgroundDrawable(new ColorDrawable(Color.WHITE));
        bottomLine.setVisibility(View.GONE);
        LinearLayout.LayoutParams bParams = new LinearLayout.LayoutParams(LayoutParams.FILL_PARENT,1);
        this.addView(bottomLine,bParams);

//        listView.addHeaderView(new View(getContext()));
//        listView.addFooterView(new View(getContext()));
//        listView.setDivider(new ColorDrawable(Color.WHITE));
        listView.setDividerHeight(1);

        adapter = new UIListAdapter(context, LAYOUT_ID[adapterType], DATA_MAP[adapterType],
            arrayList);
        listView.setAdapter(adapter);
        calPageSize();
    }

    private void calPageSize() {
        int itemH = adapter.getItemHeight();
        if (pageSize <= 0) {
            int displayH = getContext().getResources().getDisplayMetrics().heightPixels;
            pageSize = ((int) (Math.ceil((double)displayH /(itemH*5))))*5;
        }
    }

    @Override
    public ArrayList<?> getData() {
        return arrayList;
    }

    public void setExpression(String expression) {
        this.expression = expression;
    }

    public String getExpression() {
        return expression;
    }

    public void setDataAdapter(String adapter) {
        try {
            JSONObject jsonObject = new JSONObject(adapter);
            dataAdapter = JsonConvert.Json2Map(jsonObject);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void setVisible(boolean isVisible) {
        setVisibility(isVisible ? View.VISIBLE : View.GONE);
    }

    public boolean getVisible() {
        return getVisibility() == View.VISIBLE;
    }

    @SuppressWarnings("unchecked")
    @Override
    public void setValue(Object value) {
        if (value != null && value instanceof ArrayList<?>) {
            addData((ArrayList<Object>) value);
        } else {
            addData(null);
        }
    }

    @Override
    public Object getValue() {
        return null;
    }

    @SuppressWarnings("unchecked")
    @Override
    public void setData(ArrayList<Object> data) {
        if (!isAdd) {
            arrayList.clear();
        }
        if (data == null) {
            return;
        }
        
        if(data.size()>0){
            topLine.setVisibility(View.VISIBLE);
            bottomLine.setVisibility(View.VISIBLE);
        }

        HashMap<String, Object> item = null;
        for (Object o : data) {
            if (dataAdapter != null && dataAdapter.size() > 0) {
                item = new HashMap<String, Object>();
                Set<String> set = dataAdapter.keySet();
                for (String str : set) {
                    String key = (String) dataAdapter.get(str);
                    if (o instanceof String)
                        item.put(str, (String) o);
                    else
                        item.put(str, ((HashMap<String, Object>) o).get(key));
                }
                arrayList.add(item);
            } else {
                arrayList.add((HashMap<String, Object>) o);
            }
        }

        if (adapter != null) {
            adapter.dataChanged();
            return;
        } 

    }

    private boolean isAdd;

    @Override
    public void addData(ArrayList<Object> data) {
        isAdd = true;
        setData(data);
        if(data!=null)
            currentPage++;
        isAdd = false;
    }

    @Override
    public void setEnable(boolean enabled) {
        setEnable(enabled);
    }

    @Override
    public boolean getEnable() {
        return isEnabled();
    }

    // ----------------------------设置宽度
    private boolean isChange = true;
    private int width = ViewGroup.LayoutParams.FILL_PARENT;

    // getWidth方法应该和设置的宽度是否一样，待确认
    public void set_Width(int width) {
        isChange = true;
        this.width = width;
        changedWidth();
    }

    private void changedWidth() {
        ViewGroup.LayoutParams params = (ViewGroup.LayoutParams) findViewById(R.id.uilistview)
            .getLayoutParams();
        params.width = this.width;
    }

    @Override
    protected void onWindowVisibilityChanged(int visibility) {
        super.onWindowVisibilityChanged(visibility);
        LinearLayout.LayoutParams params = (LayoutParams) getLayoutParams();
        params.height = LayoutParams.FILL_PARENT;
        params.weight = 1;
        
        if (isChange) {
            changedWidth();
        }
        
        isChange = false;
        // 最后测试动态更改宽度
        requestLayout();
    }

    @Override
    public String getSelectedIndex() {
        return mSelected + "";//listView.getSelectedItemPosition() + "";
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
        LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) findViewById(R.id.uilistview)
            .getLayoutParams();
        params.leftMargin = marginLeft;
    }

    @Override
    public int get_MarginLeft() {
        LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) findViewById(R.id.uilistview)
            .getLayoutParams();
        return params.leftMargin;
    }

    @Override
    public void set_MarginRight(int marginRight) {
        LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) findViewById(R.id.uilistview)
            .getLayoutParams();
        params.rightMargin = marginRight;
    }

    @Override
    public int get_MarginRight() {
        LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) findViewById(R.id.uilistview)
            .getLayoutParams();
        return params.rightMargin;
    }

    @Override
    public void set_MarginTop(int marginTop) {
        LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) topLine/*findViewById(R.id.uilistview)*/
            .getLayoutParams();
        params.topMargin = marginTop;
    }

    @Override
    public int get_MarginTop() {
        LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) topLine/*findViewById(R.id.uilistview)*/
            .getLayoutParams();
        return params.topMargin;
    }

    @Override
    public void set_MarginBottom(int marginBottom) {
        LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) bottomLine/*findViewById(R.id.uilistview)*/
            .getLayoutParams();
        params.bottomMargin = marginBottom;
    }

    @Override
    public int get_MarginBottom() {
        LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) bottomLine/*findViewById(R.id.uilistview)*/
            .getLayoutParams();
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
    public String getItemId() {
        return null;
    }

    private String delExp;

    @Override
    public void setDelExp(String delExp) {
        this.delExp = delExp;
    }

    private int totalItemCount;
    private int currentPage;
    private int pageSize;
    private String addMoreExpression;

    @Override
    public void setTotal(int totalItemCount) {
        this.totalItemCount = totalItemCount;
        if(totalItemCount==0){
            listView.setEmptyView(mEmptyView);
        }
    }

    @Override
    public int getCurrentPage() {
        return currentPage + 1;
    }

    @Override
    public int getPageSize() {
        return pageSize;
    }

    @Override
    public void clearData() {
        if (adapter != null && arrayList != null) {
            arrayList.clear();
            adapter.dataChanged();
        }
    }

    public void setAddMoreExpression(String addMoreExpression) {
        this.addMoreExpression = addMoreExpression;
    }

    public String getAddMoreExpression() {
        return addMoreExpression;
    }

    @Override
    public void setItemExps(ArrayList<Object> itemExps) {
        if (this.itemExps == null && itemExps != null) {
            this.itemExps = itemExps;
        }
    }

    private boolean deletable;

    @Override
    public void setDeletable(String dele) {
        if (dele == null || dele.length() < 1) {
            return;
        }
        String tempDel = dele.trim();
        this.deletable = "true".equals(tempDel) ? true : false;

        if (deletable) {
            listView.setOnItemLongClickListener(new OnItemLongClickListener() {
                @Override
                public boolean onItemLongClick(AdapterView<?> adapterview, final View view,
                                               int position, long arg3) {
                	
                    StyleAlertDialog dialog = new StyleAlertDialog(context, 0, 
                        context.getResources().getString(R.string.WarngingString),context.getResources().getString(R.string.uilistView_delete_item_msg),
                    		context.getResources().getString(R.string.Yes),new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    //删除此项
                                    mSelected = (Integer) view.getTag();
                                    arrayList.remove(mSelected);
                                    adapter.dataChanged();
                                    if (delExp != null && delExp.length() > 0) {
                                        Object id = getTag()==null?"":getTag();
                                        mPage.interpreter("list-delete::"+id.toString(),delExp);
                                    }
                                }
                            },context.getResources().getString(R.string.No),null, null);
                        dialog.show();
                	
//                    AlertDialog.Builder dialog = new AlertDialog.Builder(context);
//
//                    dialog.setTitle("确定要删除此项？");
//
//                    dialog.setPositiveButton(R.string.Yes, new DialogInterface.OnClickListener() {
//                        @Override
//                        public void onClick(DialogInterface dialog, int which) {
//                            //删除此项
//                            mSelected = (Integer) view.getTag();
//                            arrayList.remove(mSelected);
//                            adapter.dataChanged();
//                            if (delExp != null && delExp.length() > 0) {
//                                Object id = getTag()==null?"":getTag();
//                                mPage.interpreter("list-delete::"+id.toString(),delExp);
//                            }
//                            dialog.dismiss();
//                        }
//                    });
//                    dialog.setNegativeButton(R.string.No, new DialogInterface.OnClickListener() {
//                        @Override
//                        public void onClick(DialogInterface dialog, int which) {
//                            dialog.dismiss();
//                        }
//                    });
//
//                    dialog.show();

                    return true;
                }
            });
        }

    }

    @Override
    public LinearLayout.LayoutParams getAlipayLayoutParams() {
        return (LinearLayout.LayoutParams) findViewById(R.id.uilistview).getLayoutParams();
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
