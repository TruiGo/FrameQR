package com.alipay.android.core.webapp;

import java.util.HashMap;

import org.apache.cordova.CallbackServer;
import org.apache.cordova.CordovaChromeClient;
import org.apache.cordova.CordovaWebViewClient;
import org.apache.cordova.LinearLayoutSoftKeyboardDetect;
import org.apache.cordova.api.LOG;
import org.apache.cordova.api.PluginManager;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.webkit.DownloadListener;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.alipay.android.client.RootActivity;
import com.alipay.android.comon.component.StyleAlertDialog;
import com.alipay.android.util.LogUtil;
import com.eg.android.AlipayGphone.R;

public class Page {
    public static String TAG = "Page";

    private static final String STYLE_NO_TITLE_BAR = "noTitleBar";

    private WebAppRunTime runTime;
    private Activity mActivity;
    private WebAppHandler mAppHandler;

    private String mUrl;

    public WebView appView;

    protected WebViewClient webViewClient;

    private static int ACTIVITY_STARTING = 0;
    private static int ACTIVITY_RUNNING = 1;
    private static int ACTIVITY_EXITING = 2;
    private int activityState = 0; // 0=starting, 1=running (after 1st resume), 2=shutting down

    // LoadUrl timeout value in msec (default of 45 sec)
    protected int loadUrlTimeoutValue = 45000;

    private boolean mMultiTask = true;

    private TitleBar mTitleBar;
    public boolean bound = false;
    private String mBackAction;

    public CallbackServer callbackServer;
    public PluginManager pluginManager;

    private boolean mError;
    private boolean mLoadFinish;

    public Page(Activity activity, WebAppRunTime appRunTime, String url, String style) {
        mActivity = activity;
        runTime = appRunTime;

        mActivity.setContentView(R.layout.web_app);
        LinearLayoutSoftKeyboardDetect root = (LinearLayoutSoftKeyboardDetect) mActivity
            .findViewById(R.id.content);
        root.setApp(this);
        this.appView = (WebView) mActivity.findViewById(R.id.webview);
        if(runTime.getPkgId().equalsIgnoreCase("80000001")&&!(url.startsWith("http") || url.startsWith("https")))
            this.appView.setBackgroundColor(Color.TRANSPARENT);

        mTitleBar = new TitleBar(this);
        // Setup the hardware volume controls to handle volume control
        //        activity.setVolumeControlStream(AudioManager.STREAM_MUSIC);

        mUrl = url;
        if (style.equalsIgnoreCase(STYLE_NO_TITLE_BAR)) {
            mTitleBar.hide();
        }

        init();
    }

    public void start(String params) {
        mAppHandler.start(mUrl, params);
    }

    public String getUrl() {
        return mUrl;
    }

    public void setContext(Activity activity) {
        mActivity = activity;
    }

    public Activity getContext() {
        return mActivity;
    }

    public void bindBackButton(boolean override) {
        bound = override;
    }

    public boolean isBackButtonBound() {
        return bound;
    }

    public WebView getAppView() {
        return appView;
    }

    public WebAppRunTime getRunTime() {
        return runTime;
    }

    public PluginManager getPluginManager() {
        return pluginManager;
    }

    public TitleBar getTitleBar() {
        return mTitleBar;
    }

    public boolean isError() {
        return mError;
    }

    public void runOnUiThread(Runnable runnable) {
        RootActivity activity = (RootActivity) getContext();
        activity.runOnUiThread(runnable);
    }

    /**
     * Initialize web container with web view objects.
     * 
     */
    public void init() {
        LOG.d(TAG, "DroidGap.init()");

        mAppHandler = new WebAppHandler(this);
        this.webViewClient = new CordovaWebViewClient(this);
        this.appView.setWebChromeClient(new CordovaChromeClient(this));
        this.appView.setWebViewClient(webViewClient);

        this.appView.setInitialScale(0);
        this.appView.setVerticalScrollBarEnabled(false);
        this.appView.requestFocusFromTouch();
//        this.appView.clearCache(true);
        
        String databasePath = getContext().getDir("database", Context.MODE_PRIVATE).getAbsolutePath();
        WebSettings settings = this.appView.getSettings();
        // Enable JavaScript
        settings.setJavaScriptEnabled(true);
        // Enable database
        settings.setDatabaseEnabled(true);
        settings.setDatabasePath(databasePath);
        // Enable DOM storage
        settings.setDomStorageEnabled(true);
        
        settings.setJavaScriptCanOpenWindowsAutomatically(true);
        settings.setCacheMode(WebSettings.LOAD_NORMAL);
        settings.setAllowFileAccess(true);
        settings.setUserAgentString("AlipayWebApp/" + org.apache.cordova.Device.cordovaVersion
                                    + ";" + settings.getUserAgentString());

        String proxyHost = android.net.Proxy.getDefaultHost();
        int proxyPort = android.net.Proxy.getDefaultPort();
        if (proxyHost != null && proxyHost.length() > 0 && proxyPort > 0) {
            try {
                LogUtil.logAnyTime("WebAppLog", "System proxy:"+proxyHost+":"+proxyPort);
                ProxyUtil.setProxy(mActivity,"",0);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        if(Build.VERSION.SDK_INT < 17){//4.2以上的版本不支持该方法
//            WebView.disablePlatformNotifications();
            //Set the nav dump for HTC
            settings.setNavDump(true);
        }

        // Enable built-in geolocation
        settings.setGeolocationEnabled(true);
        settings.setGeolocationDatabasePath(databasePath);

        this.appView.setDownloadListener(new DownloadListener() {
            public void onDownloadStart(String url, String userAgent, String contentDisposition,
                                        final String mimetype, long contentLength) {
                if (mimetype.equalsIgnoreCase("application/vnd.android.package-archive")) {
                    Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
                    getContext().startActivity(intent);
                }
            }
        });

        // Create plugin manager
        this.pluginManager = new PluginManager(this.appView, this);
    }
    
    public boolean keyDown(int keyCode, KeyEvent event) {
        if (activityState == ACTIVITY_EXITING)
            return false;
        // If back key
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            if ((mBackAction != null && mBackAction.length() > 0) || bound) {
                if (mBackAction != null && mBackAction.length() > 0) {
                    sendJavascript(mBackAction);
                }
                // If back key is bound, then send event to JavaScript
                if (this.bound) {
                    this.appView.loadUrl("javascript:cordova.fireDocumentEvent('backbutton');");
                }
            } else {
                this.appView.stopLoading();
                backHistory();
            }
            return true;
        }

        // If menu key
        else if (keyCode == KeyEvent.KEYCODE_MENU) {
            this.appView.loadUrl("javascript:cordova.fireDocumentEvent('menubutton');");
            return false;
        }

        // If search key
        else if (keyCode == KeyEvent.KEYCODE_SEARCH) {
            this.appView.loadUrl("javascript:cordova.fireDocumentEvent('searchbutton');");
            return true;
        }

        return false;
    }

    public void setBackAction(String backAction) {
        mBackAction = backAction;
    }

    public void sendJavascript(String statement) {
        //We need to check for the null case on the Kindle Fire beacuse it changes the width and height on load
        if (this.callbackServer != null)
            this.callbackServer.sendJavascript(statement);
    }

    public CallbackServer getCallbackServer() {
        return callbackServer;
    }

    public void addService(String serviceType, String className) {
        if (this.pluginManager != null) {
            this.pluginManager.addService(serviceType, className);
        }
    }

    public void resume() {
        if (this.activityState == ACTIVITY_STARTING) {
            this.activityState = ACTIVITY_RUNNING;
            return;
        }

        //relod localstorage, Send frompop event to JavaScript,Send resume event to JavaScript
        this.appView
            .loadUrl("javascript:try{alipay.localStorage.reLoad(function(){try{cordova.require('cordova/channel').onFrompop.fire();console.log('onFrompop.fire.');}catch(e){console.log('exception firing frompop event from native');};try{cordova.require('cordova/channel').onResume.fire();}catch(ex){console.log('exception firing resume event from native');};});}catch(exx){console.log('alipay localstorage reload error')}");

        // Forward to plugins
        if (this.pluginManager != null) {
            this.pluginManager.onResume(mMultiTask);
        }

        // If app doesn't want to run in background
        if (!mMultiTask) {
            // Resume JavaScript timers (including setInterval)
            this.appView.resumeTimers();
        }
    }

    public void pause() {
        // Don't process pause if shutting down, since onDestroy() will be called
        if (this.activityState == ACTIVITY_EXITING) {
            return;
        }

        // Send pause event to JavaScript
        this.appView
            .loadUrl("javascript:try{cordova.require('cordova/channel').onPause.fire();}catch(e){console.log('exception firing pause event from native');};");

        // Forward to plugins
        if (this.pluginManager != null) {
            this.pluginManager.onPause(mMultiTask);
        }

        // If app doesn't want to run in background
        if (!mMultiTask) {
            // Pause JavaScript timers (including setInterval)
            this.appView.pauseTimers();
        }
        this.appView.freeMemory();
    }

    public void destroy() {
        // Send destroy event to JavaScript
        this.appView
            .loadUrl("javascript:try{cordova.require('cordova/channel').onDestroy.fire();}catch(e){console.log('exception firing destroy event from native');};");

        // Load blank page so that JavaScript onunload is called
        this.appView.loadUrl("about:blank");

        // Forward to plugins
        if (this.pluginManager != null) {
            this.pluginManager.onDestroy();
        }
    }

    public void newIntent(Intent intent) {
        //Forward to plugins
        if (this.pluginManager != null) {
            this.pluginManager.onNewIntent(intent);
        }
    }

    /**
     * Send a message to all plugins. 
     * 
     * @param id            The message id
     * @param data          The message data
     */
    public void postMessage(String id, Object data) {

        // Forward to plugins
        if (this.pluginManager != null) {
            this.pluginManager.postMessage(id, data);
        }
    }

    /**
     * Load the specified URL in the Cordova webview or a new browser instance.
     * 
     * NOTE: If openExternal is false, only URLs listed in whitelist can be loaded.
     *
     * @param url           The url to load.
     * @param openExternal  Load url in browser instead of Cordova webview.
     * @param clearHistory  Clear the history stack, so new page becomes top of history
     * @param params        DroidGap parameters for new app
     */
    public void showWebPage(String url, boolean openExternal, boolean clearHistory,
                            HashMap<String, Object> params) { //throws android.content.ActivityNotFoundException {
        LOG.d(TAG, "showWebPage(%s, %b, %b, HashMap", url, openExternal, clearHistory);

        // If clearing history
        if (clearHistory) {
            this.clearHistory();
        }

        // If loading into our webview
        if (!openExternal) {

            // Make sure url is in whitelist
            if (url.startsWith("file://") || !url.startsWith("load://")
                || runTime.isUrlWhiteListed(url)) {
                // TODO: What about params?
                // Load new URL
                loadUrlIntoView(url);
            }
            // Load in default viewer if not
            else {
                LOG.w(
                    TAG,
                    "showWebPage: Cannot load URL into webview since it is not in white list.  Loading into browser instead. (URL="
                            + url + ")");
                try {
                    Intent intent = new Intent(Intent.ACTION_VIEW);
                    intent.setData(Uri.parse(url));
                    getContext().startActivity(intent);
                } catch (android.content.ActivityNotFoundException e) {
                    LOG.e(TAG, "Error loading url " + url, e);
                }
            }
        }

        // Load in default view intent
        else {
            try {
                Intent intent = new Intent(Intent.ACTION_VIEW);
                intent.setData(Uri.parse(url));
                getContext().startActivity(intent);
            } catch (android.content.ActivityNotFoundException e) {
                LOG.e(TAG, "Error loading url " + url, e);
            }
        }
    }

    /**
     * Load the url into the webview.
     * 
     * @param url
     */
    public void loadUrlIntoView(final String url) {
        if (!url.startsWith("javascript:")) {
            LOG.d(TAG, "DroidGap.loadUrl(%s)", url);
        }
        // Load URL on UI thread
        getContext().runOnUiThread(new Runnable() {
            public void run() {

                // Track URLs loaded instead of using appView history
                //                me.urls.push(url);
                //                me.appView.clearHistory();

                // Create callback server and plugin manager
                if (callbackServer == null) {
                    callbackServer = new CallbackServer();
                    callbackServer.init(url);
                } else {
                    callbackServer.reinit(url);
                }
                pluginManager.init();

                // Create a timeout timer for loadUrl
                Runnable runnable = new Runnable() {
                    public void run() {
                        try {
                            synchronized (this) {
                                wait(loadUrlTimeoutValue);
                            }
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }

                        // If timeout, then stop loading and handle error
                        if (!mLoadFinish) {
                            getContext().runOnUiThread(new Runnable() {
                                public void run() {
                                    appView.stopLoading();
                                    LOG.e(TAG, "DroidGap: TIMEOUT ERROR! - calling webViewClient");
                                    webViewClient.onReceivedError(appView, -6,
                                        "The connection to the server was unsuccessful.", url);
                                }
                            });
                        }
                    }
                };
                Thread thread = new Thread(runnable);
                thread.start();
                appView.loadUrl(url);
            }
        });
    }

    /**
     * Report an error to the host application. These errors are unrecoverable (i.e. the main resource is unavailable). 
     * The errorCode parameter corresponds to one of the ERROR_* constants.
     *
     * @param errorCode    The error code corresponding to an ERROR_* value.
     * @param description  A String describing the error.
     * @param failingUrl   The url that failed to load. 
     */
    public void onReceivedError(final int errorCode, final String description,
                                final String failingUrl) {
        getContext().runOnUiThread(new Runnable() {
            public void run() {
                mError = true;
                appView.setVisibility(View.GONE);
                displayError("Application Error", description + " (" + failingUrl + ")", "OK");
            }
        });
    }

    /**
     * Display an error dialog and optionally exit application.
     * 
     * @param title
     * @param message
     * @param button
     * @param exit
     */
    public void displayError(final String title, final String message, final String button) {
        getContext().runOnUiThread(new Runnable() {
            public void run() {
                StyleAlertDialog alertDialog = new StyleAlertDialog(getContext(),
                    R.drawable.erroricon, getContext().getString(R.string.Error), getContext()
                        .getString(R.string.webapp_error), getContext().getString(R.string.Ensure),
                    new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                            mError = false;
                            backHistory();
                        }
                    }, null, null, null);
                alertDialog.show();
            }
        });
    }

    public void setLoadFinish(boolean loadFinish) {
        mLoadFinish = loadFinish;
    }

    public void endActivity() {
        activityState = ACTIVITY_EXITING;
        RootActivity activity = (RootActivity) getContext();
        activity.finish();
    }

    public void backHistory() {
        // Check webview first to see if there is a history
        // This is needed to support curPage#diffLink, since they are added to appView's history, but not our history url array (JQMobile behavior)
        if (this.appView.canGoBack()) {
            this.appView.goBack();
            return;
        }
        endActivity();
    }

    /**
     * Clear the resource cache.
     */
    public void clearCache() {
        appView.clearCache(true);
    }

    /**
     * Clear web history in this web view.
     */
    public void clearHistory() {
        appView.clearHistory();
    }

    public void cancelLoadUrl() {
        appView.stopLoading();
    }

    public void restore(Bundle bundle) {
        appView.restoreState(bundle);
    }

    public void saveState(Bundle bundle) {
        appView.saveState(bundle);
    }
}
