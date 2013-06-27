package com.alipay.android.client.baseFunction;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Handler;
import android.view.KeyEvent;

import com.alipay.android.client.AlipayApplication;
import com.alipay.android.client.exception.SystemExceptionHandler;
import com.alipay.android.client.util.AlipayDataStore;
import com.alipay.android.log.Constants;
import com.eg.android.AlipayGphone.R;

public class UpdateHelper
{
	static final String TAG = "update";
	File myTempFile;
	Activity myActivity;
	private ProgressDialog mProgress;
	Handler mHandler = new Handler();
	boolean mCanceled = false;
	boolean errorFlag = false;
	int errorStringId = R.string.FailSaveFile;
	String fileLocation;
	private long fileLength;
	AlipayApplication application;
	public UpdateHelper(Activity theActivity) 
	{
		myActivity = theActivity;
		application = (AlipayApplication) myActivity.getApplicationContext();
	}

	public void update(final String fileUrl)
	{
		startProcessDialog();

		new Thread(new Runnable() {
			public void run()
			{
				try
				{
					getDataSource2(fileUrl);
				}
				catch (Exception e)
				{
					log(e);
					mCanceled = true;
					application.setUserData(null);
					postErrorDialog(R.string.FailDownloadFile);
				}

				endProcess();
				if (mCanceled&&myTempFile!=null)
				{
					 myTempFile.delete();
				}
				else if (errorFlag == true || fileLocation == null)
				{
					// showErrorMessage(errorStringId);
					application.setUserData(null);
					postErrorDialog(R.string.FailDownloadFile);
				}
				else
				{
					InstallFile(new File(fileLocation));
				}
			}
		}).start();
	}

	void postErrorDialog(final int resourceId)
	{
		mHandler.post(new Runnable() {
			public void run()
			{
				try{
				showErrorMessage(resourceId);
				}catch(Exception e){
					e.printStackTrace();
				}
			}
		});
	}

	File createLocalFile(String fileUrl)
	{
		File localFile = null;

		String fileExt = fileUrl.substring(fileUrl.lastIndexOf(".") + 1,
				fileUrl.length()).toLowerCase();
		String fileName = fileUrl.substring(fileUrl.lastIndexOf("/") + 1,
				fileUrl.lastIndexOf("."));

		// try to create temp file
		try
		{
			localFile = File.createTempFile(fileName, "." + fileExt);

		}
		catch (IOException e1)
		{
			// TODO Auto-generated catch block
			e1.printStackTrace();
			localFile = null;
			// return;
		}

		if (localFile != null)
			return localFile;

		// try to create internal file.
		try
		{
			File dirFile = myActivity.getFilesDir();
			localFile = new File(dirFile.getPath() + "/" + fileName + "."
					+ fileExt);
			localFile.createNewFile();
		}
		catch (IOException e1)
		{
			// TODO Auto-generated catch block
			e1.printStackTrace();
			localFile = null;
		}

		return localFile;
	}

	void showErrorMessage(int strId)
	{
		if(myActivity!=null && !myActivity.isFinishing())
		{
			AlipayApplication application = (AlipayApplication) myActivity.getApplication();
			application.getDataHelper().showDialog(myActivity, 0, 
	        		myActivity.getResources().getString(R.string.ErrorString),
	        		myActivity.getResources().getString(strId),
	        		myActivity.getResources().getString(R.string.Ensure),new DialogInterface.OnClickListener() {
						@Override public void onClick(DialogInterface dialog,
								int which)
						{
							// TODO Auto-generated method stub
						}
					},
	        		null,null, null,null);
	       /* StyleAlertDialog dialog = new StyleAlertDialog(myActivity, 0, 
	        		myActivity.getResources().getString(R.string.ErrorString),
	        		myActivity.getResources().getString(strId),
	        		myActivity.getResources().getString(R.string.Ensure),new DialogInterface.OnClickListener() {
						@Override public void onClick(DialogInterface dialog,
								int which)
						{
							// TODO Auto-generated method stub
						}
					},
	        		null,null, null);
	            dialog.show();*/
			
//		AlertDialog.Builder tDialog = new AlertDialog.Builder(myActivity);
//		// tDialog.setIcon(R.drawable.quit_bg);
//		tDialog.setTitle(myActivity.getResources().getString(
//				R.string.ErrorString));
//		tDialog.setMessage(myActivity.getResources().getString(strId));
//		tDialog.setPositiveButton(R.string.Ensure,
//				new DialogInterface.OnClickListener() {
//					@Override public void onClick(DialogInterface dialog,
//							int which)
//					{
//						// TODO Auto-generated method stub
//					}
//				});
//		tDialog.show();
	}
	}

	private void jumpToCancel()
	{
		if(myActivity != null && !myActivity.isFinishing())
		{
			try{
				AlipayApplication application = (AlipayApplication) myActivity.getApplication();
				application.getDataHelper().showDialog(myActivity, 0, "", myActivity.getResources().getString(R.string.CancelDownload),
						myActivity.getResources().getString(R.string.Yes), 
						new DialogInterface.OnClickListener() {
							@Override public void onClick(DialogInterface dialog,
									int which)
							{
								// TODO Auto-generated method stub
								mCanceled = true;
								endProcess();

							}
						}, myActivity.getResources().getString(R.string.No),new DialogInterface.OnClickListener() {
							@Override public void onClick(DialogInterface dialog, int which)
							{
								// TODO Auto-generated method stub
							}
						}, null, null);
			}
			catch(Exception e)
			{

			}
		}
	}

	private void startProcessDialog()
	{
		if(myActivity!=null && !myActivity.isFinishing())
		{
		mProgress = new ProgressDialog(myActivity);
		mProgress.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
		mProgress.setIndeterminate(false);
		mProgress.setMessage(myActivity.getResources().getString(
				R.string.DownloadProcessing));
		mProgress.setCancelable(false);

		mProgress.setOnKeyListener(new DialogInterface.OnKeyListener() {
			@Override public boolean onKey(DialogInterface dialog, int keyCode,
					KeyEvent event)
			{
				// TODO Auto-generated method stub
				if (keyCode == KeyEvent.KEYCODE_BACK&&event.getAction()==KeyEvent.ACTION_DOWN)
				{
					jumpToCancel();
					return false;
				}
				return true;
			}
		});
		}

	}

	private void setProcessStep(int length)
	{
		if(myActivity!=null && !myActivity.isFinishing())
	{
		if (length != -1)
		{
			mProgress.setMax(length / 1024);
		}
		
		mHandler.post(new Runnable() {
			public void run()
			{
				mProgress.show();
			}
		});
		}

	}

	private void setProcess(int processValue)
	{
		if(myActivity!=null && !myActivity.isFinishing())
		{
		mProgress.setProgress(processValue / 1024);
		}
	}

	private void endProcess()
	{
		if(myActivity!=null && !myActivity.isFinishing())
		{
		mProgress.dismiss();
		}
	}

	private void saveStream(InputStream is)
	{
		FileOutputStream fos;
		try
		{
			// fos = new FileOutputStream(myTempFile);
			fos = myActivity.openFileOutput("alipay.apk",
					Context.MODE_WORLD_READABLE);
			fileLocation = myActivity.getFilesDir().getPath() + "/"
					+ "alipay.apk";
			// SaveUrl(myTempFile.getPath());

			SaveUrl(fileLocation);

			byte buf[] = new byte[5120];
			int total = 0;
			do
			{
				int numread = is.read(buf);
				if (numread <= 0)
				{
					break;
				}

				fos.write(buf, 0, numread);
				total += numread;
				setProcess(total);

			}
			while (!mCanceled);

			is.close();
		}
		catch (FileNotFoundException e)
		{
            //network Exception to be log
            SystemExceptionHandler.getInstance().saveConnInfoToFile(e, Constants.MONITORPOINT_CONNECTERR);
			e.printStackTrace();
			errorFlag = true;
			
		}
		catch (Exception ex)
		{
            //network Exception to be log
            SystemExceptionHandler.getInstance().saveConnInfoToFile(ex, Constants.MONITORPOINT_CONNECTERR);
			errorFlag = true;
		}
		finally
		{
			try
			{
				is.close();
			}
			catch (IOException e)
			{
	            //network Exception to be log
	            SystemExceptionHandler.getInstance().saveConnInfoToFile(e, Constants.MONITORPOINT_CONNECTERR);
			}
		}
	}

	public void getDataSource2(String strPath)
	{
		File f = new File(myActivity.getFilesDir().getPath() + "/"
				+ "alipay.apk");
		if(f.exists())
		{
			f.delete();
		}
		DefaultHttpClient m_httpclient;
		HttpGet m_httppost;
		m_httpclient = new DefaultHttpClient();
		m_httppost = new HttpGet(strPath);
        HttpParams httpParams = m_httpclient.getParams();  
        //设置网络超时参数  
        HttpConnectionParams.setConnectionTimeout(httpParams, 20 * 1000);  
        HttpConnectionParams.setSoTimeout(httpParams, 45 * 1000);  
		try
		{
			HttpResponse response = m_httpclient.execute(m_httppost);
			if (response.getStatusLine().getStatusCode() != 200)
			{
				errorFlag = true;
			}
			else
			{
				HttpEntity entity = response.getEntity();
				fileLength = entity.getContentLength();
				log("download file size " + fileLength);

				setProcessStep((int) fileLength);

				InputStream content = entity.getContent();

				saveStream(content);
			}

		}
		catch (ClientProtocolException e)
		{
            //network Exception to be log
            SystemExceptionHandler.getInstance().saveConnInfoToFile(e, Constants.MONITORPOINT_CONNECTERR);
			e.printStackTrace();
			errorFlag = true;
			errorStringId = R.string.FailDownloadFile;

		}
		catch (IOException e)
		{
            //network Exception to be log
            SystemExceptionHandler.getInstance().saveConnInfoToFile(e, Constants.MONITORPOINT_CONNECTERR);
			e.printStackTrace();
			errorFlag = true;
		}
		catch (Exception e)
		{
            //network Exception to be log
            SystemExceptionHandler.getInstance().saveConnInfoToFile(e, Constants.MONITORPOINT_CONNECTERR);
			e.printStackTrace();
			errorFlag = true;
		}
	}

	void SaveUrl(String url)
	{
		AlipayDataStore settings = new AlipayDataStore(myActivity);
		settings.putString(AlipayDataStore.SAVED_URL, url);
		log("save as " + url);
	}

	private void InstallFile(File f)
	{
		mProgress.dismiss();
		if(f.length()<fileLength)
		{
			return;
		}
		try
		{
			String command = "chmod " + 777 + " " + f.getPath();
			Runtime runtime = Runtime.getRuntime();
			runtime.exec(command);
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}
		Intent intent = new Intent();
		intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		intent.setAction(android.content.Intent.ACTION_VIEW);
		
		intent.setDataAndType(Uri.fromFile(f), "application/vnd.android.package-archive");
		myActivity.startActivity(intent);
		
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
		//
		// 部分机型，安装过程中，要先退出自身进程，才能安装成功。
//		BaseHelper.exitProcessSilently(myActivity);
        
        //防止下载后安装，但用户单击取消情况下的问题
//        application.destroy();
//        myActivity.finish();
		android.os.Process.killProcess(android.os.Process.myPid());
	}

	private static void log(Throwable t)
	{
//		Log.d(TAG, "[BookImporter] " + t.getMessage(), t);
	}

	private static void log(String s)
	{
//		Log.d(TAG, "[BookImporter] " + s);
	}
}
