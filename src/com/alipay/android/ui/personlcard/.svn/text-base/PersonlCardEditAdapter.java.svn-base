package com.alipay.android.ui.personlcard;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.alipay.android.ui.framework.RootController;
import com.eg.android.AlipayGphone.R;

public class PersonlCardEditAdapter extends BaseAdapter implements
		OnItemClickListener {

	private static final int EDITMENU_MAKEPHOTO = 0;
	private static final int EDITMENU_NATIVIIMAGE = 1;

	static final int[] itemIds = new int[] { EDITMENU_MAKEPHOTO,
			EDITMENU_NATIVIIMAGE };
	static final String[] itemNames = new String[] { "拍头像", "从相册选择" };
	static final int[] itemIcons = new int[] { R.drawable.makephono,
			R.drawable.localimage };

	private static final int PHOTOCROP = 0;

	private RootController mRootController;
	private AlertDialog avatarDialog;

	PersonlCardEditAdapter(Activity mActivity) {
		mRootController = (RootController) mActivity;
	}
	
	public void setDialog(AlertDialog avatarDialog){
		this.avatarDialog = avatarDialog;
	}

	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return itemNames.length;
	}

	@Override
	public Object getItem(int arg0) {
		// TODO Auto-generated method stub
		return arg0;
	}

	@Override
	public long getItemId(int arg0) {
		// TODO Auto-generated method stub
		return arg0;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		final MenuItem item;
		if (convertView == null) {
			item = new MenuItem();
			convertView = LayoutInflater.from(
					mRootController.getApplicationContext()).inflate(
					R.layout.personl_edit_menu, null);
			item.icon = (ImageView) convertView
					.findViewById(R.id.menuItem_Icon);
			item.name = (TextView) convertView.findViewById(R.id.menuItem_Name);
			convertView.setTag(item);
		} else {
			item = (MenuItem) convertView.getTag();
		}

		item.icon.setImageResource(itemIcons[itemIds[position]]);
		item.name.setVisibility(View.VISIBLE);
		item.name.setText(itemNames[itemIds[position]]);
		return convertView;
	}

	@Override
	public void onItemClick(AdapterView<?> arg0, View arg1, int position,
			long arg3) {
		// TODO Auto-generated method stub
		if (avatarDialog != null) {
			avatarDialog.dismiss();
		}

		Intent intent = new Intent();
		intent.setClass(mRootController, GetAvatarActivity.class);
		switch (position) {
		case EDITMENU_MAKEPHOTO:
			if (!isSDCardExite()) {
				Toast.makeText(mRootController, "存储卡已拔出，拍照获取头像功能将暂时不可用", Toast.LENGTH_SHORT).show();
				return;
			}
			intent.putExtra(GetAvatarViewController.EXTRA_INPUT,
					GetAvatarViewController.CAPTURE);
			mRootController.startActivityForResult(intent, PHOTOCROP);
			break;
		case EDITMENU_NATIVIIMAGE:
			intent.putExtra(GetAvatarViewController.EXTRA_INPUT,
					GetAvatarViewController.PHOTO_PICK);
			mRootController.startActivityForResult(intent, PHOTOCROP);
			break;
		// case EDITMENU_SHAREIMAGE:
		// shareDialog.show();
		// break;
		default:
			break;
		}
	}
	
	private boolean isSDCardExite()
	{
	    if(android.os.Environment.getExternalStorageState().equals(android.os.Environment.MEDIA_MOUNTED))
	       return true;
	   else
	       return false;
	}

	public class MenuItem {
		ImageView icon;
		TextView name;
	}

}
