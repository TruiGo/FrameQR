/**
 * 
 */
package com.alipay.android.comon.component;

import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.ListView;

import com.alipay.android.client.util.BaseHelper;
import com.eg.android.AlipayGphone.R;

/**
 * @author sanping.li
 *
 */
public abstract class ScrollMoreListAdapter extends BaseAdapter implements OnItemClickListener {
    protected Context mContext;
    private ListView mListView;
    private View mFootView;
    private boolean mHasFooter;
    private MoreListener mMoreListener;
    protected List<?> mListDatas;

    public ScrollMoreListAdapter(ListView listView, Context context, List<?> data) {
        mListView = listView;
        mContext = context;
        mListDatas = data;
        init();
    }

    @Override
    public int getCount() {
        if (mListDatas == null)
            return 0;
        return mListDatas.size();
    }

    @Override
    public Object getItem(int position) {
        return mListDatas.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        return getItemView(position, convertView, parent);
    }

    @Override
    public void onItemClick(AdapterView<?> arg0, View arg1, int position, long arg3) {
        itemClick(arg0, arg1, position, arg3);
    }

    private void init() {
        mFootView = LayoutInflater.from(mContext).inflate(R.layout.list_more, null);
        mFootView.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                reset();
                if (mMoreListener != null)
                    mMoreListener.onMore();
            }
        });
        addFooterView();
    }

    /**
     * 获取更多失败
     */
    public void getMorefailed() {
        View failView = mFootView.findViewById(R.id.list_more_fail);
        View loadingView = mFootView.findViewById(R.id.list_more_loading);
        failView.setVisibility(View.VISIBLE);
        loadingView.setVisibility(View.INVISIBLE);
    }

    public void reset() {
        View failView = mFootView.findViewById(R.id.list_more_fail);
        View loadingView = mFootView.findViewById(R.id.list_more_loading);
        failView.setVisibility(View.INVISIBLE);
        loadingView.setVisibility(View.VISIBLE);
    }

    @Override
    public int getItemViewType(int position) {
        return 0;
    }

    @Override
    public int getViewTypeCount() {
        return 1;
    }

    public MoreListener getMoreListener() {
        return mMoreListener;
    }

    public void setMoreListener(MoreListener moreListener) {
        mMoreListener = moreListener;
    }

    public interface MoreListener {
        public void onMore();
    }

    @Override
    public void notifyDataSetChanged() {
        if (hasMore()&&!mHasFooter) {
            addFooterView();
        } else if(!hasMore()&&mHasFooter){
            removeFooterView();
        }
        super.notifyDataSetChanged();
    }

	public void removeFooterView() {
		mListView.removeFooterView(mFootView);
		mHasFooter = false;
	}

	public void addFooterView() {
		removeFooterView();
		mListView.addFooterView(mFootView);
		mHasFooter = true;
	}
    
    /**
     * 添加分割线
     */
    public void addDivideAtStart(View convertView){
    	addDivide(convertView,0);
    }
    /**
     * 在指定位置添加分割线
     * @param convertView
     * @param index
     */
    public void addDivide(View convertView,int index){
    	if (convertView instanceof ViewGroup) {
			ViewGroup convertViewGroup = (ViewGroup) convertView;
			if(convertViewGroup.findViewById(R.id.ListDivide) == null){
				View divideView = LayoutInflater.from(mContext).inflate(R.layout.list_divide, convertViewGroup, false);
				BaseHelper.fixBackgroundRepeat(divideView);
				convertViewGroup.addView(divideView,index);
			}
		}
    }
    /**
     * 删除分割线
     * @param convertView
     */
    public void removeDivide(View convertView){
    	if (convertView instanceof ViewGroup) {
			ViewGroup convertViewGroup = (ViewGroup) convertView;
			View divideView = convertViewGroup.findViewById(R.id.ListDivide);
			if(divideView != null){
				convertViewGroup.removeView(divideView);
			}
		}
    }

    protected abstract boolean hasMore();

    protected abstract View getItemView(int position, View convertView, ViewGroup parent);

    protected abstract void itemClick(AdapterView<?> arg0, View arg1, int position, long arg3);

}
