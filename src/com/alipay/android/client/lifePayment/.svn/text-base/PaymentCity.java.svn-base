package com.alipay.android.client.lifePayment;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Vector;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.graphics.PixelFormat;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.alipay.android.client.RootActivity;
import com.alipay.android.client.constant.Constant;
import com.eg.android.AlipayGphone.R;

/**
 * 选择缴费城市Activity
 * 
 * @author caidie.wang
 * 
 */
public class PaymentCity extends RootActivity {

	private TextView mTitleView = null;// 选择缴费城市Title

	ListView cityList; // 左侧ListView
	private BaseAdapter adapter; // ListView适配器
	private static final String SORT_KEY = "sort_key";// 适配器存数数据key
	private static final String CITY = "city";// 适配器存数数据key

	private static final String IP_CITY = "定位城市";
	HashMap<String, Integer> alphaIndexer; // 字母索引集合
	String[] sections;// 与alphaIndexer相对应城市数组
	private String[] mCityList = null;// 需要ListView显示的城市列表

	private PaymentCityLetterListView letterListView;// 右侧字母索引
	public Vector<String> letterStrArray = null;// 右侧显示字母索引列表集合
	TextView overlay;// 触摸字母索引显示弹出显示该字母

	OverlayThread overlayThread;
	Handler handler;// 触摸索引弹出字母处理器

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.payment_city_layout);
		// title
		mTitleView = (TextView) findViewById(R.id.title_text);
		mTitleView.setText(R.string.SelectChargeCity);
		// 城市列表
		cityList = (ListView) findViewById(R.id.list_view);
		cityList.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				TextView clickTextView = (TextView) view.findViewById(R.id.name);
				String clickCity = (String) clickTextView.getText();
				Intent intent = new Intent();
				intent.putExtra(Constant.CLICK_CITY, clickCity);
				PaymentCity.this.setResult(RESULT_OK, intent);
				PaymentCity.this.finish();
			}
		});
		// 字母索引列表
		letterListView = (PaymentCityLetterListView) findViewById(R.id.MyLetterListView01);
		letterListView.setOnTouchingLetterChangedListener(new LetterListViewListener(this));

		alphaIndexer = new HashMap<String, Integer>();
		handler = new Handler();
		overlayThread = new OverlayThread();
		initOverlay();// 初始化覆盖层
		prepareData();// 准备数据
	}

	@Override
	protected void onDestroy() {
		WindowManager windowManager = (WindowManager) this
				.getSystemService(Context.WINDOW_SERVICE);
		windowManager.removeView(overlay);

		super.onDestroy();
	}

	/**
	 * mCityList的第一项为定位城市的信息 mCityList[0]为空，则没有定位到的城市信息
	 */
	private void prepareData() {
		Intent intent = getIntent();
		mCityList = intent.getStringArrayExtra(Constant.PAYMENT_CITY_LIST);
		if (mCityList == null) {
			return;
		}
		List<ContentValues> list = new ArrayList<ContentValues>();
		
		Arrays.sort(mCityList,1,mCityList.length,new CityListDataComparator());
		
		for(int i=0;i<mCityList.length;i++){
			ContentValues cv = new ContentValues();
			if (i == 0 && !mCityList[0].equals("")) {
				cv.put(CITY, mCityList[0]);
				cv.put(SORT_KEY, IP_CITY);
			} else if (i == 0 && mCityList[0].equals("")) {
				cv.put(CITY, mCityList[0]);
				cv.put(SORT_KEY, "");
			} else {
				cv.put(CITY, mCityList[i]);
				cv.put(SORT_KEY,
						ChineseToPy.getSinglePy(mCityList[i].substring(0, 1)));
			}
			list.add(cv);
		}
		if (list.size() > 0) {
			setAdapter(list);
		}
		letterListView.setShowStrArray(letterStrArray);
	}
	/**
	 * 城市列表排序比较器
	 * @author caidie.wang
	 *
	 */
	class CityListDataComparator implements Comparator<String>{

		@Override
		public int compare(String first, String second) {
			return ChineseToPy.getSinglePy(first.substring(0, 1)).compareTo(
						ChineseToPy.getSinglePy(second.substring(0, 1)));
		}
		
	} 

	@Override
	protected void onResume() {
		super.onResume();
	}

	/**
	 * 给左侧cityList设置适配器
	 * 
	 * @param list
	 */
	private void setAdapter(List<ContentValues> list) {
		adapter = new ListAdapter(this, list);
		cityList.setAdapter(adapter);
	}

	/**
	 * 定制CityList适配器
	 * 
	 * @author caidie.wang
	 * 
	 */
	private class ListAdapter extends BaseAdapter {
		private LayoutInflater inflater;
		private List<ContentValues> list;

		public ListAdapter(Context context, List<ContentValues> list) {
			this.inflater = LayoutInflater.from(context);
			this.list = list;
			alphaIndexer = new HashMap<String, Integer>();
			sections = new String[list.size()];
			letterStrArray = new Vector<String>();
			if (!(list.get(0).getAsString(CITY)).equals("")) {
				alphaIndexer.put(IP_CITY, 0);
				sections[0] = IP_CITY;
			}

			for (int i = 1; i < list.size(); i++) {
				// 当前城市首字母
				String currentStr = list.get(i).getAsString(SORT_KEY);
				// 前一个城市首字母
				String previewStr = (i - 1) >= 0 ? list.get(i - 1).getAsString(
						SORT_KEY) : " ";
				if (!previewStr.equals(currentStr)) {
					String name = list.get(i).getAsString(SORT_KEY);
					alphaIndexer.put(name, i);
					sections[i] = name;
					letterStrArray.addElement(name);
				}
			}
		}

		@Override
		public int getCount() {
			return list.size();
		}

		@Override
		public Object getItem(int position) {
			return list.get(position);
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			ViewHolder holder;
			if (convertView == null) {
				convertView = inflater.inflate(R.layout.payment_city_list_item,
						null);
				holder = new ViewHolder();
				holder.alpha = (TextView) convertView.findViewById(R.id.alpha);
				holder.cityName = (TextView) convertView
						.findViewById(R.id.name);
				holder.gap = (View) convertView.findViewById(R.id.gap);
				convertView.setTag(holder);
			} else {
				holder = (ViewHolder) convertView.getTag();
			}
			ContentValues cv = list.get(position);
			if (position == 0 && !(cv.getAsString(CITY)).equals("")) {
				holder.alpha.setVisibility(View.VISIBLE);
				holder.alpha.setText(list.get(position).getAsString(SORT_KEY));
				holder.cityName.setText(cv.getAsString(CITY));
			} else if (position == 0 && (cv.getAsString(CITY)).equals("")) {
				holder.alpha.setVisibility(View.GONE);
				holder.cityName.setVisibility(View.GONE);
			} else {
				holder.cityName.setVisibility(View.VISIBLE);
				holder.cityName.setText(cv.getAsString(CITY));
				String currentStr = list.get(position).getAsString(SORT_KEY);
				String previewStr = (position - 1) >= 0 ? list
						.get(position - 1).getAsString(SORT_KEY) : " ";
				if (!previewStr.equals(currentStr)) {
					holder.alpha.setVisibility(View.VISIBLE);
					holder.alpha.setText(currentStr);
				} else {
					holder.gap.setVisibility(View.VISIBLE);
					holder.alpha.setVisibility(View.GONE);
				}
			}

			return convertView;
		}

		private class ViewHolder {
			TextView alpha;
			TextView cityName;
			View gap;
		}

	}

	/**
	 * 初始化滑动覆盖层
	 */
	private void initOverlay() {
		LayoutInflater inflater = LayoutInflater.from(this);
		overlay = (TextView) inflater.inflate(R.layout.payment_city_overlay,
				null);
		overlay.setVisibility(View.INVISIBLE);
		WindowManager.LayoutParams lp = new WindowManager.LayoutParams(
				LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT,
				WindowManager.LayoutParams.TYPE_APPLICATION,
				WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE
						| WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
				PixelFormat.TRANSLUCENT);
		WindowManager windowManager = (WindowManager) this
				.getSystemService(Context.WINDOW_SERVICE);
		windowManager.addView(overlay, lp);
	}

	private class OverlayThread implements Runnable {
		@Override
		public void run() {
			overlay.setVisibility(View.GONE);
		}
	}

}