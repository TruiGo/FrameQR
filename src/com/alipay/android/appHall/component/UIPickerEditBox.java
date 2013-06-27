package com.alipay.android.appHall.component;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONException;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.database.DataSetObservable;
import android.database.DataSetObserver;
import android.graphics.Color;
import android.text.InputFilter;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AutoCompleteTextView;
import android.widget.BaseAdapter;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;
import android.widget.Toast;

import com.alipay.android.appHall.CacheManager;
import com.alipay.android.appHall.Helper;
import com.alipay.android.appHall.uiengine.InputCheckListener;
import com.alipay.android.appHall.uiengine.UIInterface;
import com.alipay.android.comon.component.StyleAlertDialog;
import com.alipay.android.core.expapp.Page;
import com.alipay.android.util.JsonConvert;
import com.eg.android.AlipayGphone.R;

public class UIPickerEditBox extends LinearLayout implements UIInterface,
		OnItemClickListener, InputCheckListener ,OnEditorActionListener{

	private Page mPage;

	private ImageView mEditImage = null;

	private pickerEditContentAdapter adapter = null;

	AutoCompleteTextView mEditContent = null;

	public ArrayList<Object> saveDataArrayList;

	private FrameLayout layout = null;

	public UIPickerEditBox(Page page) {
		super(page.getRawContext());
		mPage = page;
	}

	private String componentId;

	public void init(ViewGroup parent) {
		componentId = mPage.getEngine().getPkgId() + "." + mPage.getPageName()
				+ "." + get_Id();
		String value = CacheManager.getInstance(mPage.getRawContext()).get(
				componentId);
		if (value != null && value != "") {
			try {
				saveDataArrayList = JsonConvert
						.Json2Array(new JSONArray(value));
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

		layout = (FrameLayout) LayoutInflater.from(mPage.getRawContext())
				.inflate(R.layout.uipickeditbox, parent, false);

		mEditImage = (ImageView) layout.findViewById(R.id.editImage);
		mEditImage.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				mEditContent.setThreshold(100);
				Helper.hideInputPanel(mPage.getRawContext(), UIPickerEditBox.this);
				mEditContent.showDropDown();
				mEditContent.requestFocus();
			}
		});

		mEditContent = (AutoCompleteTextView) layout
				.findViewById(R.id.editContent);

		mEditContent.setOnItemClickListener(this);

		mEditImage.measure(MeasureSpec.UNSPECIFIED, MeasureSpec.UNSPECIFIED);
		mEditContent.measure(MeasureSpec.UNSPECIFIED, MeasureSpec.UNSPECIFIED);
		mEditContent.setTextColor(Color.BLACK);

		this.addView(layout,Helper.copyLinearLayoutParams((LayoutParams) layout.getLayoutParams()));

		mEditContent.setText("");
		mEditContent.setThreshold(0);
		mEditContent.setOnEditorActionListener(this);
	}

	public void setDataFromXML(ArrayList<Object> arrayList) {
		if (saveDataArrayList != null) {
			List<HashMap<String, Object>> temparray = new ArrayList<HashMap<String, Object>>();
			for (int i = 0; i < saveDataArrayList.size(); i++) {
				HashMap<String, Object> hm = new HashMap<String, Object>();
				hm.put(String.valueOf(i), saveDataArrayList.get(i));
				temparray.add(hm);
			}
			adapter = new pickerEditContentAdapter(mPage.getRawContext(),
					temparray, componentId);
			mEditContent.setAdapter(adapter);
			return;
		}
		int len = arrayList.size() > saveNum ? saveNum
				: arrayList.size();
		saveDataArrayList = new ArrayList<Object>();
		for (int i = 0; i < len; i++) {
			saveDataArrayList.add(arrayList.get(i));
		}

		List<HashMap<String, Object>> temparray = new ArrayList<HashMap<String, Object>>();
		for (int i = 0; i < len; i++) {
			HashMap<String, Object> hm = new HashMap<String, Object>();
			hm.put(String.valueOf(i), arrayList.get(i));
			temparray.add(hm);
		}

		String value = JsonConvert.Array2Json(saveDataArrayList);
		CacheManager cm = CacheManager.getInstance(mPage.getRawContext());
		cm.put2Local(componentId, value);
		adapter = new pickerEditContentAdapter(mPage.getRawContext(),
				temparray, componentId);
		mEditContent.setAdapter(adapter);
	}

	private int saveNum = 0;

	public void setSaveNum(int saveNum) {
		this.saveNum = saveNum;
	}

	public int getSaveNum() {
		return saveNum;
	}
	
	private int maxCharNum = -1;

    public void setMaxCharNum(String maxCharNum) {
        this.maxCharNum = Integer.valueOf(maxCharNum);
        if (this.maxCharNum != -1) {
        	mEditContent.setFilters(new InputFilter[] { new InputFilter.LengthFilter(this.maxCharNum) });
        }
    }

	public void setVisible(boolean isVisible) {
		setVisibility(isVisible ? View.VISIBLE : View.GONE);
	}

	public boolean getVisible() {
		return getVisibility() == View.VISIBLE;
	}

	@Override
	public void setValue(Object value) {
		if (value == null || value.toString().trim() == "") {
			return;
		}
		mEditContent.setText(value.toString());
	}

	@Override
	public Object getValue() {
		return mEditContent.getText().toString();
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

	@Override
	public String getSelectedIndex() {
		return null;
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
		LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) layout
				.getLayoutParams();
		params.leftMargin = marginLeft;
		layout.setLayoutParams(params);
	}

	@Override
	public int get_MarginLeft() {
		LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) layout
				.getLayoutParams();
		return params.leftMargin;
	}

	@Override
	public void set_MarginRight(int marginRight) {
		LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) layout
				.getLayoutParams();
		params.rightMargin = marginRight;
		layout.setLayoutParams(params);
	}

	@Override
	public int get_MarginRight() {
		LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) layout
				.getLayoutParams();
		return params.rightMargin;
	}

	@Override
	public void set_MarginTop(int marginTop) {
		LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) layout
				.getLayoutParams();
		params.topMargin = marginTop;
		layout.setLayoutParams(params);
	}

	@Override
	public int get_MarginTop() {
		LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) layout
				.getLayoutParams();
		return params.topMargin;
	}

	@Override
	public void set_MarginBottom(int marginBottom) {
		LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) layout
				.getLayoutParams();
		params.bottomMargin = marginBottom;
		layout.setLayoutParams(params);
	}

	@Override
	public int get_MarginBottom() {
		LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) layout
				.getLayoutParams();
		return params.bottomMargin;
	}

	// ----------------------------设置宽度
	private boolean isChange = true;

	private int width = ViewGroup.LayoutParams.FILL_PARENT;

	public void set_Width(int width) {
		isChange = true;
		this.width = width;
		changedWidth();
	}

	private void changedWidth() {
		ViewGroup.LayoutParams params = (ViewGroup.LayoutParams) layout
				.getLayoutParams();
		params.width = this.width;
		layout.setLayoutParams(params);
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
		if (text == null || text.toString().trim() == "") {
			return;
		}
		mEditContent.setText(text.toString());
	}

	@Override
	public String get_Text() {
		return mEditContent.getText().toString().trim();
	}

	@Override
	public String get_Tag() {
		return (String) this.getTag();
	}

	@Override
	public void set_Tag(String tag) {
		this.setTag(tag);
	}

	public void setHint(String hint) {
		mEditContent.setHint(hint);
	}

	public String getHint() {
		return (String) mEditContent.getHint();
	}

	@Override
	public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
		// 获取当前view，得到真正的值 设置进去
		TextView textView = (TextView) arg1.findViewById(R.id.name);
		mEditContent.setThreshold(100);
		mEditContent.setText(textView.getText());
		mEditContent.setSelection(textView.getText().length());
	}
	
	
    public boolean inputCheck() {
    	CacheManager cm = CacheManager.getInstance(mPage.getRawContext());
    	String value = cm.get(componentId);
		if (value == null) {
			saveDataArrayList = new ArrayList<Object>();
			saveDataArrayList.add(mEditContent.getText().toString());
		} else {
			try {
				saveDataArrayList = JsonConvert
						.Json2Array(new JSONArray(value));
			} catch (JSONException e) {
			}
			
			String tempString = mEditContent.getText().toString().trim();
			for (int i = 0; i < saveDataArrayList.size(); i++) {
				if (tempString==null || tempString.length()<=0||tempString.equals(saveDataArrayList.get(i))) {
					return true;
				}
			}
			
			ArrayList<Object> tempArrayList = new ArrayList<Object>();
						
			tempArrayList.add(mEditContent.getText().toString());
			if (saveDataArrayList.size() == saveNum) {
				for (int i = 0; i < saveNum - 1; i++) {
					tempArrayList.add(saveDataArrayList.get(i));
				}
			} else if (saveDataArrayList.size() < saveNum) {
				for (int i = 0; i < saveDataArrayList.size(); i++) {
					tempArrayList.add(saveDataArrayList.get(i));
				}
			}
			saveDataArrayList = tempArrayList;
		}
		value = JsonConvert.Array2Json(saveDataArrayList);
		cm.put2Local(componentId, value);
		
		return true;
	}

//	@Override
//	public void doSave(CacheManager cm) {
//		String value = cm.get(componentId);
//		if (value == null) {
//			saveDataArrayList = new ArrayList<Object>();
//			saveDataArrayList.add(mEditContent.getText().toString());
//		} else {
//			try {
//				saveDataArrayList = JsonConvert
//						.Json2Array(new JSONArray(value));
//			} catch (JSONException e) {
//			}
//			
//			String tempString = mEditContent.getText().toString();
//			for (int i = 0; i < saveDataArrayList.size(); i++) {
//				if (tempString!=null && tempString.length()>0&&tempString.equals(saveDataArrayList.get(i))) {
//					return;
//				} 
//			}
//			
//			ArrayList<Object> tempArrayList = new ArrayList<Object>();
//						
//			tempArrayList.add(mEditContent.getText().toString());
//			if (saveDataArrayList.size() == saveNum) {
//				for (int i = 0; i < saveNum - 1; i++) {
//					tempArrayList.add(saveDataArrayList.get(i));
//				}
//			} else if (saveDataArrayList.size() < saveNum) {
//				for (int i = 0; i < saveDataArrayList.size(); i++) {
//					tempArrayList.add(saveDataArrayList.get(i));
//				}
//			}
//			saveDataArrayList = tempArrayList;
//		}
//		value = JsonConvert.Array2Json(saveDataArrayList);
//		cm.put2Local(componentId, value);
//	}

	@Override
	public LinearLayout.LayoutParams getAlipayLayoutParams() {
		return (android.widget.LinearLayout.LayoutParams) mEditContent
				.getLayoutParams();
	}

	// tag for android 1.5
	HashMap<Integer, Object> mTags = new HashMap<Integer, Object>();

	@Override
	public Object get_tag(int type) {
		return mTags.get(type);
	}

	@Override
	public void set_Tag(int type, Object tag) {
		mTags.put(type, tag);
	}

	private boolean deletable;

	public void setDeletable(String deletable) {
		if ("true".equals(deletable)) {
			this.deletable = true;
			return;
		}
		this.deletable = false;
	}

	class pickerEditContentAdapter extends BaseAdapter implements Filterable {

		private String componentId;

		private final DataSetObservable mObservable = new DataSetObservable();

		private Filter filter;

		@SuppressWarnings("unchecked")
		public pickerEditContentAdapter(Context context,
				List<? extends Map<String, ?>> data, String componentId) {
			this.data = (ArrayList<HashMap<String, Object>>) data;
			this.context = context;
			this.componentId = componentId;
		}

		private Context context;

		private ArrayList<HashMap<String, Object>> data;

		View focusimage;

		int i = 0;

		@Override
		public void registerDataSetObserver(DataSetObserver observer) {
			mObservable.registerObserver(observer);
		}

		@Override
		public void unregisterDataSetObserver(DataSetObserver observer) {
			mObservable.unregisterObserver(observer);
		}

		public void dataChanged() {
			mObservable.notifyChanged();
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			View view = null;
			if (convertView != null) {
				view = convertView;
			} else {
				view = ((Activity) context).getLayoutInflater().inflate(
						R.layout.recent_filter_item_for_picker, parent, false);
			}
			TextView textView = (TextView) view.findViewById(R.id.name);
			if (data != null && data.size() != 0) {
				textView.setText((String) data.get(position).get(position + ""));
			}

			final int index = position;

			ImageView image = (ImageView) view.findViewById(R.id.DeleteImage);
			if (!deletable && image != null) {
				image.setVisibility(View.GONE);
			}
			image.setOnClickListener(new OnClickListener() {
				public void onClick(View v) {
					focusimage = v;
			        StyleAlertDialog dialog = new StyleAlertDialog(v.getContext(), R.drawable.infoicon,
			        		v.getContext().getResources().getString(R.string.DeleteAccountTitle),
			        		v.getContext().getResources().getString(R.string.DeleteAccountMessage),
			        		v.getContext().getResources().getString(R.string.Ensure),new DialogInterface.OnClickListener() {
								@Override
								public void onClick(DialogInterface dialog,
										int which) {
									// 点击确定删除内容
									try {
										deleteItemContent(index);
										Toast.makeText(context, "删除成功",
												Toast.LENGTH_SHORT).show();
									} catch (Exception e) {
										e.printStackTrace();
									}
								}
							},
							v.getContext().getResources().getString(R.string.Cancel),new DialogInterface.OnClickListener() {
								@Override
								public void onClick(DialogInterface dialog,
										int which) {
									Toast.makeText(context, "取消",
											Toast.LENGTH_SHORT).show();
								}
							}, null);
			            dialog.show();
					
//					AlertDialog.Builder tDialog = new AlertDialog.Builder(v
//							.getContext());
//					tDialog.setIcon(R.drawable.infoicon);
//					tDialog.setTitle(R.string.DeleteAccountTitle);
//					tDialog.setMessage(R.string.DeleteAccountMessage);
//					tDialog.setPositiveButton(R.string.Ensure,
//							new DialogInterface.OnClickListener() {
//								@Override
//								public void onClick(DialogInterface dialog,
//										int which) {
//									// 点击确定删除内容
//									try {
//										deleteItemContent(index);
//										Toast.makeText(context, "删除成功",
//												Toast.LENGTH_SHORT).show();
//									} catch (Exception e) {
//										e.printStackTrace();
//									}
//								}
//							});
//					tDialog.setNegativeButton(R.string.Cancel,
//							new DialogInterface.OnClickListener() {
//								@Override
//								public void onClick(DialogInterface dialog,
//										int which) {
//									Toast.makeText(context, "取消",
//											Toast.LENGTH_SHORT).show();
//								}
//							});
//					tDialog.show();
				}
			});
			return view;
		}

		private void deleteItemContent(int which) throws JSONException {
			CacheManager cm = CacheManager.getInstance(context);
			String value = cm.get(componentId);
			ArrayList<Object> arrayList = null;
			arrayList = JsonConvert.Json2Array(new JSONArray(value));
			arrayList.remove(which);

			List<HashMap<String, Object>> temparray = new ArrayList<HashMap<String, Object>>();
			for (int i = 0; i < arrayList.size(); i++) {
				HashMap<String, Object> hm = new HashMap<String, Object>();
				hm.put(String.valueOf(i), arrayList.get(i));
				temparray.add(hm);
			}
			data = (ArrayList<HashMap<String, Object>>) temparray;
			dataChanged();
			value = JsonConvert.Array2Json(arrayList);
			cm.put2Local(componentId, value);
		}

		public int getItemHeight() {
			LinearLayout parent = new LinearLayout(context);
			View view = LayoutInflater.from(context).inflate(
					R.layout.recent_filter_item, parent, false);
			return view.getLayoutParams().height;
		}

		@Override
		public int getCount() {
			if (data == null) {
				return 0;
			}
			return data.size();
		}

		@Override
		public Object getItem(int position) {
			if (data == null) {
				return null;
			}
			HashMap<String, Object> hm = data.get(position);
			return hm.get(String.valueOf(position));
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		public Filter getFilter() {
			if (filter == null) {
				filter = new Filter() {

					@Override
					public CharSequence convertResultToString(Object resultValue) {
						// TODO Auto-generated method stub
						return super.convertResultToString(resultValue);
					}

					@Override
					protected void publishResults(CharSequence constraint,
							FilterResults results) {
						// TODO Auto-generated method stub

					}

					@Override
					protected FilterResults performFiltering(
							CharSequence constraint) {
						// TODO Auto-generated method stub
						return null;
					}
				};
			}
			return filter;
		}
	}

	@Override
	public void setMinCharNum(String minCharNum) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public String getDesc() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void setDesc(String desc) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public String getRegex() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void setRegex(String regex) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public boolean Inputed() {
		// TODO Auto-generated method stub
		return false;
	}

    @Override
    public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
        return false;
    }
}
