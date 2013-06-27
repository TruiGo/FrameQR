/**
 * 
 */
package com.alipay.android.appHall.component;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Set;

import android.content.Context;
import android.database.DataSetObservable;
import android.database.DataSetObserver;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.TextView;

import com.eg.android.AlipayGphone.R;

/**
 * @author sanping.li
 *
 */
public class UIListAdapter implements ListAdapter {
//    public static final int POSITION = 0x10000000;

    private final DataSetObservable mObservable = new DataSetObservable();
    
    private Context mContext;
    private Object mLayout;
    private HashMap<String, Object> mDataMap;
    private ArrayList<HashMap<String, Object>> mDatas;
    
    
    
    public UIListAdapter(Context context, Object layout, HashMap<String, Object> dataMap,
                         ArrayList<HashMap<String, Object>> datas) {
        mContext = context;
        mLayout = layout;
        mDataMap = dataMap;
        mDatas = datas;
    }

    @Override
    public int getCount() {
        return mDatas.size();
    }

    @Override
    public Object getItem(int position) {
        return mDatas.get(position);
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
        if(convertView==null){
            if(mLayout instanceof Integer)
                convertView = LayoutInflater.from(mContext).inflate((Integer) mLayout, parent, false);
            else//从包外资源载入
                ;
        }
        
        Set<String> map = mDataMap.keySet();
        View view = null;
        for(String str:map){
            Object data = mDatas.get(position).get(str);
            Object viewId = mDataMap.get(str);
            if(viewId instanceof Integer)
//            viewId = R.id.getMoney_adapter;
                view = convertView.findViewById((Integer) viewId);
            else if(viewId instanceof String)//tag形式
                view = convertView.findViewWithTag(viewId);
            
            if(view instanceof TextView)
                ((TextView)view).setText(data.toString());
            else if(view instanceof ImageView)
                ((ImageView)view).setImageDrawable((Drawable)data);
        }
        
        convertView.setTag(position);
        
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
        return mDatas.isEmpty();
    }

    @Override
    public void registerDataSetObserver(DataSetObserver observer) {
        mObservable.registerObserver(observer);
    }

    @Override
    public void unregisterDataSetObserver(DataSetObserver observer) {
        mObservable.unregisterObserver(observer);
    }

    @Override
    public boolean areAllItemsEnabled() {
        return true;
    }

    @Override
    public boolean isEnabled(int position) {
        return true;
    }
    
    public void dataChanged(){
        mObservable.notifyChanged();
    }

    public int getItemHeight(){
        LinearLayout parent = new LinearLayout(mContext);
        View view = null;
        if(mLayout instanceof Integer)
            view = LayoutInflater.from(mContext).inflate((Integer) mLayout, parent, false);
        else//从包外资源载入
            ;
        if (mLayout instanceof Integer) {
            return view.findViewById(R.id.list_item).getLayoutParams().height;
        } else{
            return -1;
        }
    } 
}
