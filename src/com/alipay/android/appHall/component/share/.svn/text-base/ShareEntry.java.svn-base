package com.alipay.android.appHall.component.share;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.Color;
import android.view.Gravity;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Toast;

import com.eg.android.AlipayGphone.R;

public class ShareEntry {
    public static void Share(Activity context, String title, String content) {
        Intent it = new Intent(Intent.ACTION_SEND);
        it.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        it.putExtra(Intent.EXTRA_SUBJECT, title);
        it.setType("text/plain");
        shareData(context, it, content);
    }

    public static void shareData(Activity activity, Intent shareintent,String content) {
        try {
            //获取程序列表
            PackageManager pm = activity.getPackageManager();

            List<ResolveInfo> listResolveInfo = pm.queryIntentActivities(shareintent,
                PackageManager.MATCH_DEFAULT_ONLY);
            ArrayList<HashMap<String, Object>> shareappdata = new ArrayList<HashMap<String, Object>>();
            if (listResolveInfo != null) {
                int size = listResolveInfo.size();

                for (int i = 0; i < size + 1; i++) {
                    HashMap<String, Object> shareappdatatemp = new HashMap<String, Object>();
                    if (i == size) {
                        shareappdatatemp.put(ShareUtilListAdapter.APP_ICONS, activity.getResources()
                            .getDrawable(R.drawable.copy));
                        shareappdatatemp.put(ShareUtilListAdapter.APP_LABLE, activity.getResources()
                            .getString(R.string.Copy_to_share));
                        shareappdatatemp.put(ShareUtilListAdapter.SHARE_COPY, activity
                            .getResources().getString(R.string.Copy_by_qq));
                        //        
                        shareappdatatemp.put(ShareUtilListAdapter.SEND_CONTENT, content);
                    } else {
                        ResolveInfo temp = (ResolveInfo) listResolveInfo.get(i);
                        //去除蓝牙程序
                        if (temp.activityInfo.packageName.toLowerCase().equals(
                            ShareUtilListAdapter.APP_PACKAGENAME_BLUETOOTH))
                            continue;
                        shareappdatatemp.put(ShareUtilListAdapter.APP_ICONS, temp.loadIcon(pm));
                        shareappdatatemp.put(ShareUtilListAdapter.APP_LABLE, temp.loadLabel(pm));
                        shareappdatatemp.put(ShareUtilListAdapter.APP_PACKAGENAME,
                            temp.activityInfo.applicationInfo.packageName);
                        shareappdatatemp.put(ShareUtilListAdapter.APP_NAME, temp.activityInfo.name);
                        shareappdatatemp.put(ShareUtilListAdapter.SEND_CONTENT, content);
                    }
                    shareappdata.add(shareappdatatemp);
                }
            }
            if (shareappdata.size() <= 0) {
                Toast toast = Toast.makeText(activity, "无分享软件", 5000);
                toast.setGravity(Gravity.CENTER, 0, 0);
                toast.show();
                return;
            }
            ListView ShareListView = new ListView(activity);

            AlertDialog dialog = new AlertDialog.Builder(activity).setTitle(
                activity.getResources().getString(R.string.share_choose))
                .setView(ShareListView).create();
            SimpleAdapter shareSimpleAdapter = new ShareUtilListAdapter(activity, shareappdata,
                shareintent, dialog);
            ShareListView.setBackgroundColor(Color.WHITE);
            ShareListView.setDividerHeight(1);
            ShareListView.setCacheColorHint(Color.WHITE);
            ShareListView.setAdapter(shareSimpleAdapter);
            ShareListView.setOnItemClickListener((OnItemClickListener) shareSimpleAdapter);

            dialog.show();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
