package com.alipay.android.appHall.appManager;

import java.io.File;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import android.content.Context;
import android.os.Build;

import com.alipay.android.appHall.common.Defines;
import com.alipay.android.appHall.security.CertHandler;
import com.alipay.android.client.util.Utilz;
import com.alipay.android.core.Style;
import com.alipay.android.net.http.HttpClient;

public class XmlAppManager implements AppManager {
    private static final String CACHE_PATH = "cache";

    private Context mContext;
    private HallData mAppHall;

    private String mAppsPath;

    private String[] mFiles;

    public XmlAppManager(HallData appHall) {
        super();
        this.mContext = appHall.getContext();
        mAppHall = appHall;
        this.mAppsPath = (this.mContext.getFilesDir().getAbsolutePath() + Defines.Apps);
        File fileApps = new File(this.mAppsPath);
        mFiles = fileApps.list();
    }

    @Override
    public void install(final AppItemInfo appItemInfo) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                synchronized (appItemInfo) {
                    // Get the cacheDir.
                    File cacheDir = mContext.getCacheDir();
                    String tempfile = cacheDir.getAbsolutePath() + "/" + appItemInfo.getId()
                                      + ".amr";
                    HttpClient.urlDownloadToFile(appItemInfo.getUrl(), tempfile, true, appItemInfo,
                        mContext);
                }
            }
        }).start();
    }

    @Override
    public boolean release(AppItemInfo appItemInfo) {
        boolean installState = false;
        try {
            // Get the cacheDir.
            File cacheDir = this.mContext.getCacheDir();
            String tempfile = cacheDir.getAbsolutePath() + "/" + appItemInfo.getId() + ".amr";
            // Unzip.
            ZipHelper.unZip(tempfile, this.mAppsPath + appItemInfo.getId() + "/");
            installState = true;

            // Delete the temp file.
            File file = new File(tempfile);
            file.delete();

            appItemInfo.setIconUrl(getAppAttribute(Defines.icon, appItemInfo.getId()));
            appItemInfo.setName(getAppAttribute(Defines.name, appItemInfo.getId()));
            appItemInfo.setCurVersion(appItemInfo.getLastVersion());
        } catch (Exception e) {
            e.printStackTrace();
            //            uninstall(appItemInfo);
            FileHelper.delete(this.mAppsPath + appItemInfo.getId() + "/");
        }
        mAppHall.setAppConfig(appItemInfo.getId(), appItemInfo);

        return installState;
    }

    @Override
    public boolean installFromInputStream(AppItemInfo appItemInfo, InputStream isSrc) {
        boolean installState = true;

        try {
            // Unzip.
            //data/data/<package>/1000001/
            ZipHelper.unZip(isSrc, this.mAppsPath + appItemInfo.getId() + "/");

            appItemInfo.setIconUrl(getAppAttribute(Defines.icon, appItemInfo.getId()));
            appItemInfo.setName(getAppAttribute(Defines.name, appItemInfo.getId()));
        } catch (Exception e) {
            e.printStackTrace();
            installState = false;
        }
        mAppHall.setAppConfig(appItemInfo.getId(), appItemInfo);

        return installState;
    }

    @Override
    public void upgrade(AppItemInfo appItemInfo) {
        delete(appItemInfo);
        //        @SuppressWarnings("unused")
        //        boolean deleteState = FileHelper.delete(appPath);

        //
        //  install the new pkg.
        install(appItemInfo);
    }

    private void delete(AppItemInfo appItemInfo) {
        String appPath = this.mAppsPath + appItemInfo.getId();

        //
        // remove the old pkg first.
        File file = new File(appPath);
        if (file.exists() && file.isDirectory()) {
            File[] files = file.listFiles();
            for (File f : files) {
                if (!f.getName().equals(CACHE_PATH)) {
                    deleteFile(f);
                }
            }
        }
    }

    @Override
    public boolean uninstall(AppItemInfo appItemInfo) {
        String appPath = this.mAppsPath + appItemInfo.getId();

        //
        // remove the old pkg first.
        boolean uninstallState = FileHelper.delete(appPath);
        if (uninstallState) {
            appItemInfo.setState(AppItemInfo.STATE_DELETEED);
            mAppHall.setAppConfig(appItemInfo.getId(), appItemInfo);
        }
        return uninstallState;
    }

    private void deleteFile(File file) {
        if (file.isDirectory()) {
            File[] files = file.listFiles();
            for (File f : files) {
                deleteFile(f);
            }
        }
        file.delete();
    }

    @Override
    public String getAppList(HashMap<String, AppItemInfo> apps) {
        JSONArray appArray = new JSONArray();
        Collection<AppItemInfo> collection = apps.values();
        JSONObject appInfo = null;
        Style style = mAppHall.getStyle();
        for (AppItemInfo itemInfo : collection) {
            if (itemInfo.getState() == AppItemInfo.STATE_DELETEED
                || itemInfo.getState() == AppItemInfo.STATE_PRE_INSTALL
                || itemInfo.getState() == AppItemInfo.STATE_INSTALL_PAUSE)
                continue;//过滤掉卸载的

            if (itemInfo.getType() == AppItemInfo.TYPE_XML
                && itemInfo.getState() == AppItemInfo.STATE_NOMARL
                && (itemInfo.getName() == null || itemInfo.getIconUrl() == null || CertHandler
                    .verify(mAppsPath + itemInfo.getId(),
                        style.getIcon(itemInfo.getId(), itemInfo.getIconUrl().toString())
                            .substring((mAppsPath + itemInfo.getId() + "/").length())) == null)) {
                uninstall(itemInfo);
                continue;
            }

            try {
                appInfo = new JSONObject();
                appInfo.put(Defines.funcId, itemInfo.getId());
                appInfo.put(Defines.version, itemInfo.getCurVersion());
                appInfo.put(Defines.position, itemInfo.getOrder());
                appArray.put(appInfo);
            } catch (JSONException e) {
                //e.printStackTrace();
            }
        }
        return appArray.length() == 0 ? "[{}]" : appArray.toString();
    }

    @Override
    public boolean isAppExisted(String appId) {
        boolean isAppExisted = false;
        if (mFiles != null) {
            for (String file : mFiles) {
                if (file.equalsIgnoreCase(appId)) {
                    isAppExisted = true;
                    break;
                }
            }
        }
        return isAppExisted;
    }

    @Override
    public ArrayList<AppItemInfo> getAppListInfo(HashMap<String, AppItemInfo> apps) {
        ArrayList<AppItemInfo> appArray = new ArrayList<AppItemInfo>();
        Collection<AppItemInfo> collection = apps.values();
        Style style = mAppHall.getStyle();
        for (AppItemInfo itemInfo : collection) {
            if (itemInfo.getState() == AppItemInfo.STATE_DELETEED || itemInfo.isShow() == 0)
                continue;
            if (itemInfo.getType() == AppItemInfo.TYPE_NATIVE) {
                //            	if(!"09999992".equalsIgnoreCase(itemInfo.getId()) && !"09999993".equalsIgnoreCase(itemInfo.getId())){//过滤本地业务
                appArray.add(itemInfo);
                //                }
                continue;
            }

            if (!(itemInfo.getName() == null || itemInfo.getIconUrl() == null || (itemInfo
                .getState() == AppItemInfo.STATE_NOMARL && CertHandler.verify(
                mAppsPath + itemInfo.getId(),
                style.getIcon(itemInfo.getId(), itemInfo.getIconUrl().toString()).substring(
                    (mAppsPath + itemInfo.getId() + "/").length())) == null))) {
                appArray.add(itemInfo);
            } else {
                //                uninstall(itemInfo);
            }
        }

        return appArray;
    }

    @Override
    public String getAppVersion(String appId) {
        AppItemInfo itemInfo = mAppHall.getAppConfig(appId);
        if (itemInfo != null)
            return itemInfo.getCurVersion();//getAppAttribute(Defines.version, appId);
        else
            return null;
    }

    @Override
    public int getAppPosition(String appId) {
        AppItemInfo itemInfo = mAppHall.getAppConfig(appId);
        if (itemInfo != null)
            return mAppHall.getAppConfig(appId).getOrder();
        else
            return Integer.MAX_VALUE;
    }

    @Override
    public String getAppIcon(String appId) {
        return getAppAttribute(Defines.icon, appId);
    }

    @Override
    public String getAppName(String appId) {
        return getAppAttribute(Defines.name, appId);
    }

    private String getAppAttribute(String attribute, String appId) {
        String appAttribute = "";
        DocumentBuilderFactory dbfactory = DocumentBuilderFactory.newInstance();
        try {
            DocumentBuilder docBuilder = dbfactory.newDocumentBuilder();
            Document doc = docBuilder.parse(CertHandler.verify(mAppsPath + appId,
                Defines.ManifestName));

            Element root = doc.getDocumentElement();
            Element entry = (Element) root.getElementsByTagName(attribute).item(0);
            appAttribute = entry.getFirstChild().getNodeValue();
        } catch (Exception e) {
            //            e.printStackTrace();
        }

        return appAttribute;
    }

    @Override
    public void resume(AppItemInfo appItemInfo) {
        install(appItemInfo);
    }

    @Override
    public boolean isAppNeed2Install(AppItemInfo configInfo) {
        boolean bNeed2Install = true;

        if (configInfo == null)
            return false;

        if (Utilz.getCPUFrequencyMax() > 0 && Utilz.getCPUFrequencyMax() < configInfo.getMinCpu())
            return false;

        if (!satisfyMinSdkVersion(configInfo.getMinSdkVersion()))
            return false;

        // App is Existed?
        AppItemInfo installedAppConfigInfo = mAppHall.getAppConfig(configInfo.getId());//Helper.getConfigInfo(isConfig, configInfo.funcId);

        // App is Out Of Date?
        if (installedAppConfigInfo != null
            && (installedAppConfigInfo.getState() == AppItemInfo.STATE_DELETEED || configInfo
                .getCurVersion().compareToIgnoreCase(installedAppConfigInfo.getCurVersion()) <= 0)) {
            bNeed2Install = false;
        } else if (installedAppConfigInfo != null) {//升级前先卸载
            delete(installedAppConfigInfo);
        }

        return bNeed2Install;
    }

    private boolean satisfyMinSdkVersion(int minSdkVersion) {
        return Integer.valueOf(Build.VERSION.SDK).intValue() >= minSdkVersion;
    }
}
