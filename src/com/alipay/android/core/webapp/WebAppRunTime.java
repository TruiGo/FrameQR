/**
 * 
 */
package com.alipay.android.core.webapp;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Stack;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.cordova.AuthenticationToken;
import org.apache.cordova.api.IPlugin;
import org.apache.cordova.api.LOG;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.res.AssetManager;
import android.content.res.Resources;
import android.os.Bundle;
import android.util.Base64;
import android.view.KeyEvent;

import com.alipay.android.client.AlipayApplication;
import com.alipay.android.client.RootActivity;
import com.alipay.android.comon.component.ProgressDiv;
import com.alipay.android.core.ActivityShell;
import com.alipay.android.core.MBus;
import com.alipay.android.core.MsgAction;
import com.alipay.android.core.ParamString;
import com.alipay.android.core.Runtime;

/**
 * @author sanping.li
 *
 */
public class WebAppRunTime extends Runtime {

    public static String TAG = "WebAppRunTime";

    private String mFrom;
    // The webview for our app
    private ArrayList<Pattern> whiteList = new ArrayList<Pattern>();
    private HashMap<String, Boolean> whiteListCache = new HashMap<String, Boolean>();
    //    protected ProgressBar mProgressBar;
    private ProgressDiv mProgress;// 进度条

    private Stack<Page> pages = new Stack<Page>();

    // Plugin to call when activity result is received
    private IPlugin activityResultCallback = null;

    /** The authorization tokens. */
    private Hashtable<String, AuthenticationToken> authenticationTokens = new Hashtable<String, AuthenticationToken>();

    private WebServer mWebServer;

    private String mPopParam;

    /**
     * Sets the authentication token.
     * 
     * @param authenticationToken
     *            the authentication token
     * @param host
     *            the host
     * @param realm
     *            the realm
     */
    public void setAuthenticationToken(AuthenticationToken authenticationToken, String host,
                                       String realm) {

        if (host == null) {
            host = "";
        }

        if (realm == null) {
            realm = "";
        }

        authenticationTokens.put(host.concat(realm), authenticationToken);
    }

    /**
     * Removes the authentication token.
     * 
     * @param host
     *            the host
     * @param realm
     *            the realm
     * @return the authentication token or null if did not exist
     */
    public AuthenticationToken removeAuthenticationToken(String host, String realm) {
        return authenticationTokens.remove(host.concat(realm));
    }

    /**
     * Gets the authentication token.
     * 
     * In order it tries:
     * 1- host + realm
     * 2- host
     * 3- realm
     * 4- no host, no realm
     * 
     * @param host
     *            the host
     * @param realm
     *            the realm
     * @return the authentication token
     */
    public AuthenticationToken getAuthenticationToken(String host, String realm) {
        AuthenticationToken token = null;

        token = authenticationTokens.get(host.concat(realm));

        if (token == null) {
            // try with just the host
            token = authenticationTokens.get(host);

            // Try the realm
            if (token == null) {
                token = authenticationTokens.get(realm);
            }

            // if no host found, just query for default
            if (token == null) {
                token = authenticationTokens.get("");
            }
        }

        return token;
    }

    public String getPopParam() {
        return mPopParam;
    }

    public void setPopParam(String popParam) {
        mPopParam = popParam;
    }

    public WebServer getWebServer() {
        return mWebServer;
    }

    /**
     * Clear all authentication tokens.
     */
    public void clearAuthenticationTokens() {
        authenticationTokens.clear();
    }

    @Override
    public void handleMsg(String sourceId, int action, String params) {
        // TODO Auto-generated method stub

    }

    @Override
    public void callback(String targetId, int result, Object params) {
        IPlugin callback = this.activityResultCallback;
        if (callback != null) {
            Intent intent = null;
            if (params != null && params instanceof Intent) {
                intent = (Intent) params;
            } else if (params != null) {
                intent = new Intent();
                intent.putExtra("params", params.toString());
            }
            callback.onActivityResult(Integer.parseInt(targetId), result, intent);
        }
    }

    @Override
    public void exit() {
        Page page = null;
        while (!pages.isEmpty()) {
            page = pages.peek();
            page.endActivity();
            pages.pop();
        }
        mWebServer.stop();
    }

    @Override
    public void create(String params, Bundle bundle) {
        LOG.d(TAG, "DroidGap.onCreate():" + params);
        ActivityShell activityShell = (ActivityShell) mContext;
        Page page = (Page) activityShell.getObject();
        if (page!=null&&bundle != null) {
            page.setContext(activityShell);
            page.restore(bundle);
            return;
        }

        init();

        ParamString paramString = new ParamString(params);
        String entry = (String) mManifestDoc.getManifest("entry");
        String style = (String) mManifestDoc.getManifest("style");
//        String version = (String)mManifestDoc.getManifest("version");

        String str = paramString.getValue("entry");
        if (str != null && str.length() > 0) {
            entry = new String(Base64.decode(str, Base64.DEFAULT));
            paramString.deleteValue("entry");
        }
        str = paramString.getValue("_source_");
        if (str != null && str.length() > 0)
            mFrom = str;

        page = new Page(activityShell, this, entry, style);
        activityShell.setObject(page);
        pages.push(page);
        page.start(paramString.toString());
        /*if ("80000001".equals(mPkgId)) {
        	AlipayLogAgent.writeLog(mContext, Constants.BehaviourID.BIZLAUNCHED, null, null, mPkgId, version, null, Constants.HOMEAPP1ICON, mPkgId+"Icon", null);
		}else{
			AlipayLogAgent.writeLog(mContext, Constants.BehaviourID.BIZLAUNCHED, null, null, mPkgId, version, null, Constants.HOMEAPPSHOW, mPkgId+"Icon", null);
		}*/
    }

    private void init() {
        if (mWebServer == null) {
            // Load Cordova configuration:
            //      white list of allowed URLs
            //      debug setting
            loadConfiguration();

            mWebServer = new WebServer(this);
            mWebServer.start();
        }
    }

    public String getFrom() {
        return mFrom;
    }

    @Override
    public void start(Context context) {
        // TODO Auto-generated method stub

    }

    @Override
    public void reStart(Context context) {

    }

    @Override
    public void resume(Context context) {
        ActivityShell activityShell = (ActivityShell) context;
        Page page = (Page) activityShell.getObject();
        page.resume();
    }

    @Override
    public void pause(Context context) {
        ActivityShell activityShell = (ActivityShell) context;
        Page page = (Page) activityShell.getObject();
        page.pause();
    }

    @Override
    public void stop(Context context) {
        // TODO Auto-generated method stub

    }

    @Override
    public void destroy(Context context) {
        ActivityShell activityShell = (ActivityShell) context;
        Page page = (Page) activityShell.getObject();
        page.destroy();
        pages.remove(page);
        if (pages.isEmpty()) {
            end();
        }
    }

    @Override
    public void saveState(Context context,Bundle bundle) {
        ActivityShell activityShell = (ActivityShell) context;
        Page page = (Page) activityShell.getObject();
        page.saveState(bundle);
    }

    @Override
    public void newIntent(Context context,Intent intent) {
        ActivityShell activityShell = (ActivityShell) context;
        Page page = (Page) activityShell.getObject();
        page.newIntent(intent);
    }

    @Override
    public boolean keyDown(Context context,int keyCode, KeyEvent event) {
        ActivityShell activityShell = (ActivityShell) context;
        Page page = (Page) activityShell.getObject();
        return page.keyDown(keyCode, event);
    }

    public void startActivityForResult(Activity activity,IPlugin command, Intent intent, int requestCode) {
        this.activityResultCallback = command;
        // Start activity
        activity.startActivityForResult(intent, requestCode);
    }

    public void setActivityResultCallback(IPlugin plugin) {
        this.activityResultCallback = plugin;
    }

    /**
     * Load Cordova configuration from res/xml/cordova.xml.
     * Approved list of URLs that can be loaded into DroidGap
     *      <access origin="http://server regexp" subdomains="true" />
     * Log level: ERROR, WARN, INFO, DEBUG, VERBOSE (default=ERROR)
     *      <log level="DEBUG" />
     */
    private void loadConfiguration() {
        addWhiteListEntry("http://127.0.0.1*", false);
        LOG.setLogLevel("DEBUG");
    }

    /**
     * Add entry to approved list of URLs (whitelist)
     * 
     * @param origin        URL regular expression to allow
     * @param subdomains    T=include all subdomains under origin
     */
    private void addWhiteListEntry(String origin, boolean subdomains) {
        try {
            // Unlimited access to network resources
            if (origin.compareTo("*") == 0) {
                LOG.d(TAG, "Unlimited access to network resources");
                whiteList.add(Pattern.compile(".*"));
            } else { // specific access
                // check if subdomains should be included
                // TODO: we should not add more domains if * has already been added
                if (subdomains) {
                    // XXX making it stupid friendly for people who forget to include protocol/SSL
                    if (origin.startsWith("http")) {
                        whiteList.add(Pattern.compile(origin.replaceFirst("https?://",
                            "^https?://(.*\\.)?")));
                    } else {
                        whiteList.add(Pattern.compile("^https?://(.*\\.)?" + origin));
                    }
                    LOG.d(TAG, "Origin to allow with subdomains: %s", origin);
                } else {
                    // XXX making it stupid friendly for people who forget to include protocol/SSL
                    if (origin.startsWith("http")) {
                        whiteList.add(Pattern.compile(origin
                            .replaceFirst("https?://", "^https?://")));
                    } else {
                        whiteList.add(Pattern.compile("^https?://" + origin));
                    }
                    LOG.d(TAG, "Origin to allow: %s", origin);
                }
            }
        } catch (Exception e) {
            LOG.d(TAG, "Failed to add origin %s", origin);
        }
    }

    /**
     * Determine if URL is in approved list of URLs to load.
     * 
     * @param url
     * @return
     */
    public boolean isUrlWhiteListed(String url) {

        // Check to see if we have matched url previously
        if (whiteListCache.get(url) != null) {
            return true;
        }

        // Look for match in white list
        Iterator<Pattern> pit = whiteList.iterator();
        while (pit.hasNext()) {
            Pattern p = pit.next();
            Matcher m = p.matcher(url);

            // If match found, then cache it to speed up subsequent comparisons
            if (m.find()) {
                whiteListCache.put(url, true);
                return true;
            }
        }
        return false;
    }

    public void end() {
        mWebServer.stop();
        AlipayApplication application = (AlipayApplication) mContext.getApplicationContext();
        MBus mBus = application.getMBus();
        mBus.sendMsg(mPkgId, null, MsgAction.ACT_EXIT, null);
    }

    public Resources getResources() {
        return mContext.getResources();
    }

    public String getPackageName() {
        return mContext.getPackageName();
    }

    public AssetManager getAssets() {
        return mContext.getAssets();
    }

    public void openProgress(Activity context,String msg,boolean cancel) {
        RootActivity activity = (RootActivity) context;
        if (mProgress == null || !mProgress.isShowing()) {
            mProgress = activity.getDataHelper().showProgressDialogWithCancelButton(activity, "",
                msg, false, true, activity.getDataHelper().cancelListener,
                activity.getDataHelper().cancelBtnListener);
        } else {
            mProgress.setMessage(msg);
        }
        if(mProgress!=null)
            mProgress.setCancelable(cancel);
    }

    public void closeProgress() {
        if (mProgress != null) {
            mProgress.dismiss();
            mProgress = null;
        }
    }

    public void backToRoot() {
        Page page = null;
        while (pages.size()>1) {//留下最后一个
            page = pages.pop();
            page.endActivity();
        }
    }

}
