package com.alipay.android.appHall.appManager;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.alipay.android.appHall.Helper;
import com.alipay.android.appHall.common.BitmapDownloadListener;
import com.alipay.android.appHall.common.Defines;
import com.alipay.android.client.AlipayApplication;
import com.alipay.android.client.util.BaseHelper;
import com.alipay.android.core.MsgAction;
import com.alipay.android.log.Constants;
import com.alipay.android.net.http.DownloadListener;
import com.eg.android.AlipayGphone.R;

public class AppItemInfo implements DownloadListener {
    /**
     * 正常使用状态
     */
    public static final int STATE_NOMARL = 0;
    /**
     * 待安装
     */
    public static final int STATE_PRE_INSTALL = STATE_NOMARL + 1;
    /**
     * 安装中
     */
    public static final int STATE_INSTALLING = STATE_PRE_INSTALL + 1;
    /**
     * 待升级
     */
    public static final int STATE_PRE_UPGRADE = STATE_INSTALLING + 1;
    /**
     * 升级中
     */
    public static final int STATE_UPGRADEING = STATE_PRE_UPGRADE + 1;
    /**
     * 待卸载
     */
    public static final int STATE_PRE_DELETE = STATE_UPGRADEING + 1;
    /**
     * 已卸载
     */
    public static final int STATE_DELETEED = STATE_PRE_DELETE + 1;
    /**
     * 安装暂停
     */
    public static final int STATE_INSTALL_PAUSE = STATE_DELETEED + 1;
    /**
     * 升级暂停
     */
    public static final int STATE_UPGRADE_PAUSE = STATE_INSTALL_PAUSE + 1;

    public static final String OPREATIONFUNCID = "operationFuncId";

    /**
     * 鏈湴涓氬姟
     */
    public static final int TYPE_NATIVE = 0;
    /**
     * XML业务
     */
    public static final int TYPE_XML = 1;

    /**
     * 业务类型
     */
    private int mType;
    /**
     * 业务ID
     */
    private String mId;

    /**
     * 业务名字
     */
    private Object mName;
    /**
     * 状态
     */
    private int mState;
    /**
     * 操作的进度
     */
    private double mPercent;
    /**
     * 当前版本
     */
    private String mCurVersion;
    /**
     * 最新版本
     */
    private String mLastVersion;
    /**
     * 图标地址
     */
    private Object mIconUrl;
    /**
     * 下载地址
     */
    private String mUrl;
    /**
     * 排序
     */
    private int mOrder;
    /**
     * 最低Cpu主频
     */
    private int mMinCpu;
    /**
     * 安卓最小SDK版本
     */
    private int mMinSdkVersion;

    /**
     * 运营活动类型
     */
    private String mAdType;
    /**
     * 运营活动文字
     */
    private String mAdString;

    /**
     * 是否显示
     * 0 不显示
     * 1 显示
     */
    private int mShow = 1;

    /**
     * 业务列表数据
     */
    private HallData mHallData;

    /**
     * 图标
     */
    private Bitmap mIcon;

    /**
     * 图标下载
     */
    private BitmapDownloadListener mBitmapDownloadListener = new BitmapDownloadListener() {
        @Override
        public void onComplete(Bitmap bm) {
            if (mIcon == null) {
                mIcon = bm;
            } else {
                mIcon = bm;
                mHallData.refresh();
            }
        }
    };

    public AppItemInfo(HallData hallData, String id, int type) {
        mHallData = hallData;
        mId = id;
        mType = type;
        mName = "";
        mIconUrl = "";
        mState = STATE_NOMARL;
        mOrder = Integer.MAX_VALUE;
        mPercent = 0;
    }

    public AppItemInfo(HallData hallData, String id, int type, int isShow) {
        this(hallData, id, type);
        mShow = isShow;
    }

    public class ViewHolder {
        public ImageView mImage;
        public TextView mText;
        public TextView mAdText;
        public ImageView mAdImage;

        public ImageView mActionImage;
        public ImageView mPauseImage;
        public ProgressBar mProgressBar;
    }

    /**
     * 获取界面为应用中心
     */
    public View getViewForAppCenter(Activity activity, View convertView) {
        if (convertView == null) {
            convertView = activity.getLayoutInflater().inflate(R.layout.subitem_layout_appcenter,
                null);
            ViewHolder viewHolder = new ViewHolder();
            viewHolder.mImage = (ImageView) convertView.findViewById(R.id.SubItemIcon);
            viewHolder.mText = (TextView) convertView.findViewById(R.id.SubItemText);
            viewHolder.mAdText = (TextView) convertView.findViewById(R.id.AdvertText);
            viewHolder.mAdImage = (ImageView) convertView.findViewById(R.id.advertImage);
            viewHolder.mActionImage = (ImageView) convertView.findViewById(R.id.icon_preaction);
            viewHolder.mPauseImage = (ImageView) convertView.findViewById(R.id.icon_pause);
            viewHolder.mProgressBar = (ProgressBar) convertView.findViewById(R.id.progressbar);

            ImageView overlay = (ImageView) convertView.findViewById(R.id.overlay);
            BaseHelper.fixBackgroundRepeat(overlay);
            convertView.setTag(viewHolder);
        }

        final ViewHolder holder = (ViewHolder) convertView.getTag();
        //clear ad status 
        holder.mAdText.setVisibility(View.GONE);
        holder.mAdImage.setVisibility(View.GONE);
        holder.mActionImage.setVisibility(View.INVISIBLE);
        holder.mPauseImage.setVisibility(View.INVISIBLE);
        holder.mProgressBar.setVisibility(View.INVISIBLE);
        ImageView subItemIconBg = (ImageView) convertView.findViewById(R.id.SubItemIconBg);

        if (mId == null) {
            holder.mImage.setVisibility(View.INVISIBLE);
            holder.mText.setVisibility(View.INVISIBLE);
            subItemIconBg.setVisibility(View.INVISIBLE);
            return convertView;
        } else {
            subItemIconBg.setVisibility(View.VISIBLE);
            holder.mImage.setVisibility(View.VISIBLE);
            holder.mText.setVisibility(View.VISIBLE);
        }

        if (mType == TYPE_NATIVE) {
            try {
                // for native apps.
                holder.mText.setText(Integer.parseInt(mName.toString()));
                holder.mImage.setImageResource(Integer.parseInt(mIconUrl.toString()));
            } catch (NumberFormatException e) {
                e.printStackTrace();
            }

        } else if (mType == TYPE_XML) {
            holder.mText.setText(mName.toString());
            if (mIcon == null) {
                mIcon = Helper.bitmapFromUriString(activity,
                    mHallData.getStyle().getIcon(mId, mIconUrl.toString()),
                    mBitmapDownloadListener, R.drawable.app_default);
            }
            holder.mImage.setImageBitmap(mIcon);
            switch (mState) {
                case STATE_INSTALL_PAUSE:
                case STATE_UPGRADE_PAUSE:
                case STATE_INSTALLING:
                case STATE_UPGRADEING:
                    holder.mPauseImage.setVisibility(View.VISIBLE);
                    if (mState == STATE_INSTALL_PAUSE || mState == STATE_UPGRADE_PAUSE) {
                        holder.mPauseImage.setImageResource(R.drawable.resume_icon);
                    } else {
                        holder.mPauseImage.setImageResource(R.drawable.pause_icon);
                    }
                    holder.mProgressBar.setVisibility(View.VISIBLE);
                    if (mPercent <= 0)
                        holder.mProgressBar.setIndeterminate(true);
                    else {
                        holder.mProgressBar.setIndeterminate(false);
                        holder.mProgressBar.setProgress((int) (mPercent * 100));
                    }
                    break;
                case STATE_PRE_DELETE:
                    holder.mActionImage.setVisibility(View.VISIBLE);
                    holder.mActionImage.setImageResource(R.drawable.offline_label_appshortcut);
                    break;
                case STATE_PRE_INSTALL:
                    holder.mActionImage.setVisibility(View.VISIBLE);
                    holder.mActionImage.setImageResource(R.drawable.setup_label_appshortcut);
                    break;
                case STATE_PRE_UPGRADE:
                    holder.mActionImage.setVisibility(View.VISIBLE);
                    holder.mActionImage.setImageResource(R.drawable.updates_label_appshortcut);
                    break;
                case STATE_DELETEED:
                    break;
                default:
                    break;
            }
        }

        //运营活动
        if (mAdType != null) {
            if (mAdType.equalsIgnoreCase(Defines.label)) {
                holder.mAdText.setVisibility(View.VISIBLE);
                holder.mAdText.setText(mAdString);
            } else if (mAdType.equalsIgnoreCase(Defines.hot)) {
                holder.mAdImage.setVisibility(View.VISIBLE);
                holder.mAdImage.setImageResource(R.drawable.icon_hot);
            } else if (mAdType.equalsIgnoreCase(Defines.news)) {
                holder.mAdImage.setVisibility(View.VISIBLE);
                holder.mAdImage.setImageResource(R.drawable.icon_new);
            }
        }

        return convertView;
    }

    /**
     * 获取界面为快捷应用
     */
    public View getViewForShortcut(Activity activity, View convertView, int i) {
        final ViewHolder holder = new ViewHolder();
        holder.mImage = (ImageView) convertView.findViewById(R.id.SubItemIcon);
        holder.mActionImage = (ImageView) convertView.findViewById(R.id.icon_preaction);
        holder.mPauseImage = (ImageView) convertView.findViewById(R.id.icon_pause);
        holder.mProgressBar = (ProgressBar) convertView.findViewById(R.id.progressbar);
        //clear ad status 
        holder.mActionImage.setVisibility(View.INVISIBLE);
        holder.mPauseImage.setVisibility(View.INVISIBLE);
        holder.mProgressBar.setVisibility(View.INVISIBLE);

        ImageView overlay = (ImageView) convertView.findViewById(R.id.overlay);
        overlay.setVisibility(View.INVISIBLE);
        
        if (mType == TYPE_NATIVE) {
            try {
                // for native apps.
                if (i == 1) {
                    holder.mImage.setImageResource(R.drawable.homepage_app6);
                } else if (i == 0) {
                    holder.mImage.setImageResource(R.drawable.homepage_app0);
                }
                //                holder.mImage.setImageResource(Integer.parseInt(mIconUrl.toString()));
            } catch (NumberFormatException e) {
                e.printStackTrace();
            }

        } else if (mType == TYPE_XML) {

            if (mIcon == null) {
                mIcon = Helper.bitmapFromUriString(activity,
                    mHallData.getStyle().getIcon(mId, mIconUrl.toString()),
                    mBitmapDownloadListener, R.drawable.app_default);
            }
            if (i == 2) {
                holder.mImage.setImageResource(R.drawable.homepage_app2);
            } else if (i == 3) {
                holder.mImage.setImageResource(R.drawable.homepage_app3);
            }

            //            holder.mImage.setImageBitmap(mIcon);
            switch (mState) {
                case STATE_INSTALL_PAUSE:
                case STATE_UPGRADE_PAUSE:
                case STATE_INSTALLING:
                case STATE_UPGRADEING:
                    holder.mPauseImage.setVisibility(View.VISIBLE);
                    if (mState == STATE_INSTALL_PAUSE || mState == STATE_UPGRADE_PAUSE) {
                        holder.mPauseImage.setImageResource(R.drawable.resume_icon);
                    } else {
                        holder.mPauseImage.setImageResource(R.drawable.pause_icon);
                    }
                    holder.mProgressBar.setVisibility(View.VISIBLE);
                    if (mPercent <= 0)
                        holder.mProgressBar.setIndeterminate(true);
                    else {
                        holder.mProgressBar.setIndeterminate(false);
                        holder.mProgressBar.setProgress((int) (mPercent * 100));
                    }
                    break;
                case STATE_PRE_DELETE:
                    holder.mActionImage.setVisibility(View.VISIBLE);
                    holder.mActionImage.setImageResource(R.drawable.offline_label_appshortcut);
                    break;
                case STATE_PRE_INSTALL:
                    holder.mActionImage.setVisibility(View.VISIBLE);
                    holder.mActionImage.setImageResource(R.drawable.setup_label_appshortcut);
                    break;
                case STATE_PRE_UPGRADE:
                    holder.mActionImage.setVisibility(View.VISIBLE);
                    holder.mActionImage.setImageResource(R.drawable.updates_label_appshortcut);
                    break;
                case STATE_DELETEED:
                    break;
                default:
                    break;
            }
        }

        return convertView;
    }

    public static final int FROM_APPCTETER = 0;
    public static final int FROM_HOMEPAGE = 1;
    public static final int FROM_OTHERAPP = 2;
    public static final int FROM_WEBVIEW = 3;
    public static final int FROM_PAYTOOLS = 4;

    /**
     * 点击
     */
    public void onClick(Context activity, String param, int fromType) {
        switch (mType) {
            case TYPE_XML:
                xmlClick(activity, param, fromType);
                break;

            case TYPE_NATIVE:
                nativeClick(activity, param, fromType);
                break;
            default:
                break;
        }
    }

    private void nativeClick(Context activity, String param, int fromType) {
        ((AlipayApplication) activity.getApplicationContext()).getMBus().sendMsg("", mId,
            MsgAction.ACT_LAUNCH, param);
    }

    private void xmlClick(Context activity, String param, int fromType) {

        switch (mState) {
            case STATE_NOMARL:
            	if ("10000007".equals(mId)) {
            		Constants.firstOpenCM = true;
					Constants.paipaiStep1Start = System.currentTimeMillis();
            
					
//				Intent intent = new Intent();
//            	intent.setClass(activity, PaipaiHelperActivity.class);
//            	intent.putExtra(PaipaiHelperActivity.PAIPAI_ID, mId);
//            	intent.putExtra(PaipaiHelperActivity.PAIPAI_PARAM, param);
//            	activity.startActivity(intent);
//            	return;
            	
				}
            	
            	
            	
                ((AlipayApplication) activity.getApplicationContext()).getMBus().sendMsg("", mId,
                    MsgAction.ACT_LAUNCH, param);
                break;
            case STATE_INSTALLING:
            case STATE_UPGRADEING:
                mHallData.doPause(mId);
                break;
            case STATE_INSTALL_PAUSE:
            case STATE_UPGRADE_PAUSE:
                mHallData.doResume(mId);
                break;
            case STATE_PRE_INSTALL:
                mHallData.doInstall(mId);
                break;
            case STATE_PRE_UPGRADE:
                mHallData.doUpgrade(mId);
                break;
            case STATE_PRE_DELETE:
                mHallData.doDelete(mId);
                break;
            default:
                break;
        }
    }

    public Object getName() {
        return mName;
    }

    public String getNameText() {
        if (mType == TYPE_NATIVE) {
            return mHallData.getContext().getResources()
                .getString(Integer.parseInt(mName.toString()));
        } else if (mType == TYPE_XML) {
            return mName.toString();
        } else {
            return "";
        }
    }

    public void setName(Object name) {
        mName = name;
    }

    public int isShow() {
        return mShow;
    }

    public void setShow(int show) {
        this.mShow = show;
    }

    public int getState() {
        return mState;
    }

    public void setState(int state) {
        mState = state;
    }

    public Object getIconUrl() {
        return mIconUrl;
    }

    public void setIconUrl(Object iconUrl) {
        mIconUrl = iconUrl;
    }

    public void resetIcon() {
        if(mIcon != null){
            mIcon.recycle();
            mIcon = null;
        }
    }

    public int getOrder() {
        return mOrder;
    }

    public void setOrder(int order) {
        mOrder = order;
    }

    public int getMinCpu() {
        return mMinCpu;
    }

    public void setMinCpu(int minCpu) {
        mMinCpu = minCpu;
    }

    public int getMinSdkVersion() {
        return mMinSdkVersion;
    }

    public void setMinSdkVersion(int minSdkVersion) {
        mMinSdkVersion = minSdkVersion;
    }

    public String getId() {
        return mId;
    }

    public String getUrl() {
        return mUrl;
    }

    public void setUrl(String url) {
        mUrl = url;
    }

    public String getCurVersion() {
        return mCurVersion;
    }

    public void setCurVersion(String curVersion) {
        mCurVersion = curVersion;
    }

    public String getLastVersion() {
        return mLastVersion;
    }

    public void setLastVersion(String lastVersion) {
        mLastVersion = lastVersion;
    }

    public int getType() {
        return mType;
    }

    public void setAdType(String adType) {
        mAdType = adType;
    }

    public void setAdString(String adString) {
        mAdString = adString;
    }

    public double getPercent() {
        return mPercent;
    }

    public void setPercent(double percent) {
        mPercent = percent;
    }

    public void setHallData(HallData hallData) {
        mHallData = hallData;
    }

    @Override
    public void updatePercent(double percent) {
        if (percent >= 1) {
            boolean ret = mHallData.getAppManager().release(this);
            if (ret) {
                setState(AppItemInfo.STATE_NOMARL);
                mHallData.updateApps(this);
                mHallData.removeAppFromCache(mId);
                mPercent = 0;
            }
        } else if (percent <= 0) {
            if (mState == STATE_INSTALLING) {
                mState = STATE_INSTALL_PAUSE;
            } else if (mState == STATE_UPGRADEING) {
                mState = STATE_UPGRADE_PAUSE;
            }
            mPercent = 0;
        } else
            mPercent = percent;
        mHallData.refresh();
    }

    @Override
    public int getStatus() {
        switch (mState) {
            case STATE_INSTALL_PAUSE:
            case STATE_UPGRADE_PAUSE:
                return Defines.PAUSED;
            case STATE_INSTALLING:
            case STATE_UPGRADEING:
                return Defines.INSTALLING;
            default:
                return Defines.INVALID;
        }
    }

}
