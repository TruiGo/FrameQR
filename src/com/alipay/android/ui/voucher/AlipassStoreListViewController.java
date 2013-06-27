package com.alipay.android.ui.voucher;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.alipay.android.appHall.Helper;
import com.alipay.android.ui.bean.AlipassInfo.Locations;
import com.alipay.android.ui.framework.BaseViewController;
import com.eg.android.AlipayGphone.R;

public class AlipassStoreListViewController extends BaseViewController{
	private ListView mStoreListView;
	private AlipassStoreListAdapter mStoreListAdapter;
	
	private List<Locations> mLocationList;
	@Override
	protected void onCreate() {
		mView  = LayoutInflater.from(getRootController()).inflate(R.layout.offline_store_list, null,false); 
		addView(mView, null);
		
		loadAllVariables();
		if(params != null){
			List<Locations> locationList = (List<Locations>) params;
			mLocationList.addAll(locationList);
		}
	}

	private void loadAllVariables() {
		mStoreListView = (ListView) mView.findViewById(R.id.storeListView);
		mLocationList = new ArrayList<Locations>();
		mStoreListAdapter = new AlipassStoreListAdapter(getRootController());
		mStoreListView.setAdapter(mStoreListAdapter);
	}
	
	private final class AlipassStoreListAdapter extends BaseAdapter{
		private Context mContext;
		private LayoutInflater mLayoutInflater;
		
		public AlipassStoreListAdapter(Context context) {
			this.mContext= context;
			mLayoutInflater = LayoutInflater.from(context);
		}

		@Override
		public int getCount() {
			return mLocationList.size();
		}

		@Override
		public Object getItem(int position) {
			return position;
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			ViewHolder viewHolder;
    		if(convertView == null){
    			convertView = mLayoutInflater.inflate(R.layout.store_item, null);
    			viewHolder = new ViewHolder();
    			viewHolder.nameText = (TextView) convertView.findViewById(R.id.storeName);
    			viewHolder.addressText = (TextView) convertView.findViewById(R.id.storeAddress);
    			viewHolder.phoneText = (TextView) convertView.findViewById(R.id.storePhone);
    			viewHolder.indicatorImage = (ImageView) convertView.findViewById(R.id.indicator);
    			convertView.setTag(viewHolder);
    		}else{
    			viewHolder =   (ViewHolder) convertView.getTag();
    		}
    		
    		int size = mLocationList.size();
    		if(position == 0 && size != 1){ //动态设置背景图片
    			convertView.setBackgroundResource(R.drawable.list_btn_top);
    		}else if(size != 1 && position == size -1){
    			convertView.setBackgroundResource(R.drawable.list_btn_f);
    		}else{
    			convertView.setBackgroundResource(R.drawable.list_btn_m);
    		}
    		
    		Locations location = (Locations) mLocationList.get(position);
    		viewHolder.nameText.setText(Helper.toDBC(location.getRelevantText()));
    		viewHolder.addressText.setText(Helper.toDBC(location.getAddr()));
    		String phone = location.getTel();//电话
    		viewHolder.phoneText.setText(Helper.toDBC("电话:"+phone));
    		if(phone == null || "".equals(phone) || "null".equals(phone))
    			viewHolder.phoneText.setVisibility(View.INVISIBLE);
    		else 
    			viewHolder.phoneText.setVisibility(View.VISIBLE);
    		
    		viewHolder.indicatorImage.setVisibility(View.GONE);
    		convertView.setEnabled(false);
    		return convertView;
		}
	}
	
	class ViewHolder{
		TextView nameText;
		TextView addressText;
		TextView phoneText;
		ImageView indicatorImage;
	}
}
