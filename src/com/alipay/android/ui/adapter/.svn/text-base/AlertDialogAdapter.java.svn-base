package com.alipay.android.ui.adapter;

import java.util.ArrayList;
import java.util.List;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.alipay.android.client.RootActivity;
import com.eg.android.AlipayGphone.R;

public class AlertDialogAdapter extends BaseAdapter{
	private LayoutInflater mLayoutInflater;
	private List<String> mListData = new ArrayList<String>();
	RootActivity mContext;
	int mDefaultChecked =0;
	int defaultCheckedHashCode=0;
	public AlertDialogAdapter(RootActivity context,List<String> data){
		mLayoutInflater = LayoutInflater.from(context);
		mListData.clear();
    	mListData.addAll(data);
    	if(!data.isEmpty()){
    		defaultCheckedHashCode = mListData.get(0).hashCode();
    	}
	}
	
	@Override
    public int getCount() {
        // TODO Auto-generated method stub
        return mListData.size();
    }

    @Override
    public Object getItem(int position) {
        // TODO Auto-generated method stub
        return mListData.get(position);
    }

    @Override
    public long getItemId(int position) {
        // TODO Auto-generated method stub
        return position;
    }

	public int setDefaultCheckedPosition(int defalutCheckPostion){
		if(defalutCheckPostion>0&&defalutCheckPostion<getCount()){
			mDefaultChecked = defalutCheckPostion;
		}else{
			mDefaultChecked =0;
		}
		defaultCheckedHashCode = mListData.get(mDefaultChecked).hashCode();
		return mDefaultChecked;
	}
	
    @Override
	public View getView(int position, View convertView, ViewGroup parent) {
    	ViewHolder viewHolder;
		if (convertView == null) { // 1，当第一次加载ListView控件时 convertView为空
			convertView = mLayoutInflater.inflate(
					R.layout.ccr_select_teleponeperators, null); // 所以当ListView控件没有滑动时都会执行这条语句
			viewHolder = new ViewHolder();
			viewHolder.tv = (TextView) convertView.findViewById(R.id.text1);
			viewHolder.iv = (ImageView) convertView.findViewById(R.id.list_item_indicator);
			convertView.setTag(viewHolder);
		} else {
			viewHolder = (ViewHolder) convertView.getTag();
			
		}
		
		if(defaultCheckedHashCode == mListData.get(position).hashCode()){
			viewHolder.iv.setImageState(new int[] { android.R.attr.state_checked },true);
		}else{
			viewHolder.iv.setImageState(new int[]{}, true);
		}
		
		viewHolder.tv.setText(mListData.get(position));
		viewHolder.iv.setId(position);
		return convertView;
    	/*convertView = mLayoutInflater.inflate(R.layout.ccr_select_teleponeperators, null); 
    	TextView tv = (TextView)convertView.findViewById(R.id.text1);
    	tv.setText(mListData.get(position));
    	ImageView indicator = (ImageView)convertView.findViewById(R.id.list_item_indicator);
    	if(position == mDefaultChecked){
    		indicator.setImageState(new int[]{android.R.attr.state_checked}, true);
        }
    	indicator.setId(position);
        return convertView;*/
	}

	private class ViewHolder {
		public TextView tv;
		public ImageView iv;
	}
}
