package com.alipay.android.ui.personlcard;

import java.io.File;
import java.io.FileInputStream;

import org.json.JSONObject;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import com.alipay.android.biz.CommonRespHandler;
import com.alipay.android.biz.PersonlCardBiz;
import com.alipay.android.client.util.AlipayDataStore;
import com.alipay.android.client.util.BaseHelper;
import com.alipay.android.common.data.UserData;
import com.alipay.android.comon.component.ProgressDiv;
import com.alipay.android.log.AlipayLogAgent;
import com.alipay.android.log.Constants;
import com.alipay.android.ui.framework.BaseViewController;
import com.alipay.android.ui.framework.RootController;
import com.alipay.android.util.Base64;
import com.alipay.android.util.JsonConvert;
import com.eg.android.AlipayGphone.R;

public class GetAvatarViewController extends BaseViewController {

	public static final String EXTRA_INPUT = "input-type";
	public static final String EXTRA_DATA = "data-url";

	public static final int PHOTO_PICK = 0;
	public static final int CAPTURE = 1;

	private BizAsyncTask loadTask;

	private RootController mRootController = null;

	private ProgressDiv progressDiv;

	private static final String IMAGE_UNSPECIFIED = "image/*";

	private CropImageView mCropImageView;
	private ImageButton titleRight;

	private AlipayDataStore dataStore;

	@Override
	public void onCreate() {
		super.onCreate();
		mRootController = getRootController();
		dataStore = new AlipayDataStore(mRootController);
		mView = LayoutInflater.from(mRootController).inflate(
				R.layout.crop_image, null, false);
		addView(mView, null);

		Bundle extras = mRootController.getIntent().getExtras();
		int input = extras.getInt(EXTRA_INPUT);

		loadAllVariables();

		Intent intent = null;
		switch (input) {
		case PHOTO_PICK:
			intent = new Intent(Intent.ACTION_GET_CONTENT);
			intent.setType(IMAGE_UNSPECIFIED);
			mRootController.startActivityForResult(intent, PHOTO_PICK);
			break;
		case CAPTURE:
			
			intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
			intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(new File(
					Environment.getExternalStorageDirectory(),
					"__tmp_avatar.jpg")));
			// intent.putExtra("return-data", true);
			mRootController.startActivityForResult(intent, CAPTURE);
			break;
		default:
			break;
		}

		// 防止扫描头像出现手势情况
		mRootController.countMeNotTemporary(true);
	}
	
	

	private ImageView titleShadow;

	private void loadAllVariables() {

		titleShadow = (ImageView) findViewById(R.id.TitleShadow);
		titleShadow.setVisibility(View.GONE);
		titleRight = (ImageButton) findViewById(R.id.title_right);
		titleRight.setVisibility(View.VISIBLE);
		titleRight.setImageResource(R.drawable.crop_save_btn);
		titleRight.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				
				AlipayLogAgent.writeLog(mRootController, Constants.BehaviourID.CLICKED, null, null,
	                    Constants.WALLETACCOUNT, null, Constants.MYNAMECARD, Constants.SAVEFACEVIEW,
	                    Constants.SAVEICON, "");

				// TODO 再这里调用接口进行上传头像

				Bitmap bitmap = mCropImageView.getCropBitmap();
				String url = "";
				try {
					url = mCropImageView.writeBitmap(bitmap);
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

				String uploadHeadImg_base64 = genBase64Img(url);
				int index = url.lastIndexOf("/");
				String fileName = "";
				if (index != -1) {
					fileName = url.substring(index + 1);
					uploadHeadImg(uploadHeadImg_base64, fileName);
				}

				// try {
				// Bitmap bitmap = mCropImageView.getCropBitmap();
				// String url = mCropImageView.writeBitmap(bitmap);
				// Intent intent = new Intent();
				// intent.putExtra(GetAvatarViewController.EXTRA_DATA, url);
				// mRootController.setResult(Activity.RESULT_OK, intent);
				// mCropImageView.recycle();
				// mRootController.finish();
				// } catch (Exception e) {
				// e.printStackTrace();
				// }
			}
		});
		mCropImageView = (CropImageView) findViewById(R.id.crop);
		mCropImageView.setActivity((GetAvatarActivity) mRootController);
		imageRepeat();
	}

	private String genBase64Img(String path) {
		byte[] data = null;
		try {
			File file = new File(path);
			if (file != null && file.exists()) {
				FileInputStream fis = new FileInputStream(file);
				if (fis != null) {
					int len = fis.available();
					data = new byte[len];
					while (fis.read(data) != -1) {
					}
				}
			}
		} catch (Exception e) {
		}
		String ret = "";
		if (data != null) {
			ret = Base64.encode(data);
		}

		return ret;
	}

	private void imageRepeat() {
		BaseHelper.fixBackgroundRepeat(mCropImageView);

	}
	
	@Override
    protected void onResume() {
        super.onResume();
        mRootController.countMeNotTemporary(true);
    }

	@Override
	public void onDestroy() {
		super.onDestroy();
		if (mCropImageView != null) {
			mCropImageView.recycle();
		}
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (resultCode == Activity.RESULT_OK) {
			switch (requestCode) {
			case PHOTO_PICK:
				Uri uri = data.getData();
				mCropImageView.setBitmap(getRealPathFromURI(uri));
				mCropImageView.invalidate();
				break;
			case CAPTURE:
				mCropImageView.setBitmap(Uri.fromFile(
						new File(Environment.getExternalStorageDirectory(),
								"__tmp_avatar.jpg")).getPath());
				mCropImageView.invalidate();
				break;
			default:
				break;
			}
		} else {
			mRootController.setResult(Activity.RESULT_CANCELED);
			mCropImageView.recycle();
			delTmpImgIfExist();
			mRootController.finish();
		}
	}
	
	private void delTmpImgIfExist(){
		File delFile = new File(mCropImageView.tmp_avatarDirStr+"tmp.jpg");
		if (delFile.exists()) {
			delFile.delete();
		}
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		// TODO Auto-generated method stub
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			AlipayLogAgent.writeLog(mRootController, Constants.BehaviourID.CLICKED, null, null,
                    Constants.WALLETACCOUNT, null, null, Constants.SAVEFACEVIEW,
                    Constants.BACKICON, "");
			mRootController.setResult(Activity.RESULT_CANCELED);
			mCropImageView.recycle();
			delTmpImgIfExist();
			mRootController.finish();
		}
		return super.onKeyDown(keyCode, event);
	}

	@SuppressWarnings("deprecation")
	private String getRealPathFromURI(Uri contentUri) {
		String[] proj = { MediaStore.Images.Media.DATA };
		Cursor cursor = mRootController.managedQuery(contentUri, proj, null,
				null, null);
		if (cursor == null)
			return null;

		int column_index = cursor
				.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
		cursor.moveToFirst();
		return cursor.getString(column_index);
	}

	@Override
	protected void onPreDoInbackgroud(String bizType) {
		// TODO Auto-generated method stub
		super.onPreDoInbackgroud(bizType);
		if (progressDiv == null) {
			progressDiv = getRootController().getDataHelper()
					.showProgressDialogWithoutCancelButton(mRootController,
							null, "头像上传中...", false, false, null, null);
		}
	}

	@Override
	protected Object doInBackground(String bizType, String... params) {
		if (BIZTYPE_UPLOADHEADIMG.equals(bizType)) {
			return new PersonlCardBiz().uploadHeadImg(params[0], params[1]);
		}
		return super.doInBackground(bizType, params);
	}

	@Override
	protected void onPostExecute(String bizType, Object result) {
		// TODO Auto-generated method stub
		// if (BIZTYPE_QUERYNAMECARD.equals(bizType)) {
		// JSONObject responseJson = JsonConvert
		// .convertString2Json((String) result);
		//
		// if (CommonRespHandler.filterBizResponse(getRootController(),
		// responseJson)) {
		// populateUI();
		// } else {
		// // 失败情况的处理
		//
		// }
		// }

		if (BIZTYPE_UPLOADHEADIMG.equals(bizType)) {
			// TODO 上传图片完成的处理
			if (progressDiv != null && progressDiv.isShowing()) {
				progressDiv.dismiss();
				progressDiv = null;
			}

			JSONObject responseJson = JsonConvert
					.convertString2Json((String) result);
			if (CommonRespHandler.filterBizResponse(getRootController(),
					responseJson)) {
				Toast.makeText(mRootController, "头像上传成功", Toast.LENGTH_SHORT)
						.show();
				String userHeadImgPath = responseJson
						.optString("userHeadImgPath");
				int index = userHeadImgPath.lastIndexOf("/");
				if (index != -1) {
					curAvatarName = userHeadImgPath.substring(index + 1);
				}
				if (curAvatarName != null && !"".equals(curAvatarName)) {
					dataStore.putString(
							mRootController.getUserId() + ":"
									+ AlipayDataStore.LASTLOGINUSERAVTARPATH,
							getUserAvtarFileStr(mRootController,
									mRootController.getUserId()));
					UserData userData = mRootController.getUserData();
					if (userData != null) {
						userData.setUserAvtarPath(getUserAvtarFileStr(
								mRootController, mRootController.getUserId()));
					}
				}
				try {

					delDir(new File(getUserAvtarDirStr(mRootController,
							mRootController.getUserId())));

					File file = new File(getUserAvtarDirStr(mRootController,
							mRootController.getUserId()) + "__tmp_avatar.jpg");
					if (file.exists()) {
						file.renameTo(new File(getUserAvtarFileStr(
								mRootController, mRootController.getUserId())));
					}
					file = new File(getUserAvtarDirStr(mRootController,
							mRootController.getUserId() + "__tmp_avatar.jpg"));
					if (file.exists()) {
						file.delete();
					}
					// 成功返回名片界面，更新头像
					mRootController.setResult(Activity.RESULT_OK);
					mRootController.finish();
				} catch (Exception e) {
				}

			} else {
				// TODO 失败分两种情况来处理
				Toast.makeText(mRootController, "头像上传失败", Toast.LENGTH_SHORT)
						.show();
			}
		}

		super.onPostExecute(bizType, result);
	}

	private String curAvatarName;// 这个服务端返回的路径当中的名称

	private String getUserAvtarFileStr(Context context, String userId) {
		return getUserAvtarDirStr(context, userId) + curAvatarName + ".jpg";
	}

	private String getUserAvtarDirStr(Context context, String userId) {
		return context.getFilesDir() + File.separator + "userdata"
				+ File.separator + "useravatar" + File.separator + userId
				+ File.separator;
	}

	private boolean delDir(File dir) {
		if (dir == null || !dir.exists() || dir.isFile()) {
			return false;
		}
		for (File file : dir.listFiles()) {
			String name = file.getName();
			if (file.isFile() && name != null
					&& name.indexOf("__tmp_avatar") == -1) {
				file.delete();
			} else if (file.isDirectory()) {
				delDir(file);// 递归
			}
		}
		return true;
	}

	private final static String BIZTYPE_UPLOADHEADIMG = "biztype_uploadHeadImg";

	private void uploadHeadImg(String uploadHeadImg_base64, String fileName) {
		if (loadTask != null
				&& loadTask.getStatus() != AsyncTask.Status.FINISHED)
			loadTask.cancel(true);
		loadTask = new BizAsyncTask(BIZTYPE_UPLOADHEADIMG, false);
		loadTask.execute(uploadHeadImg_base64, fileName);
	}

}
