/*
       Licensed to the Apache Software Foundation (ASF) under one
       or more contributor license agreements.  See the NOTICE file
       distributed with this work for additional information
       regarding copyright ownership.  The ASF licenses this file
       to you under the Apache License, Version 2.0 (the
       "License"); you may not use this file except in compliance
       with the License.  You may obtain a copy of the License at

         http://www.apache.org/licenses/LICENSE-2.0

       Unless required by applicable law or agreed to in writing,
       software distributed under the License is distributed on an
       "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
       KIND, either express or implied.  See the License for the
       specific language governing permissions and limitations
       under the License.
*/
package org.apache.cordova;

import java.io.File;

import org.apache.cordova.api.LOG;

import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.graphics.Bitmap;
import android.net.Uri;
import android.net.http.SslError;
import android.webkit.HttpAuthHandler;
import android.webkit.SslErrorHandler;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.alipay.android.core.webapp.Page;
import com.eg.android.AlipayGphone.R;

/**
 * This class is the WebViewClient that implements callbacks for our web view.
 */
public class CordovaWebViewClient extends WebViewClient {
    private static final String TAG = "Cordova";

    private static final String INJECT_JS = "common/_inject_.js";

    private Page mPage;

    /**
     * Constructor.
     * 
     * @param ctx
     */
    public CordovaWebViewClient(Page page) {
        mPage = page;
    }

    /**
     * Give the host application a chance to take over the control when a new url 
     * is about to be loaded in the current WebView.
     * 
     * @param view          The WebView that is initiating the callback.
     * @param url           The url to be loaded.
     * @return              true to override, false for default behavior
     */
    @Override
    public boolean shouldOverrideUrlLoading(WebView view, String url) {
    	mPage.bindBackButton(false);
        if ((mPage.getPluginManager() != null)
            && mPage.getPluginManager().onOverrideUrlLoading(url)) {
        }

        // If dialing phone (tel:5551212)
        else if (url.startsWith(WebView.SCHEME_TEL)) {
            try {
                Intent intent = new Intent(Intent.ACTION_DIAL);
                intent.setData(Uri.parse(url));
                mPage.getContext().startActivity(intent);
            } catch (android.content.ActivityNotFoundException e) {
                LOG.e(TAG, "Error dialing " + url + ": " + e.toString());
            }
        }

        // If displaying map (geo:0,0?q=address)
        else if (url.startsWith("geo:")) {
            try {
                Intent intent = new Intent(Intent.ACTION_VIEW);
                intent.setData(Uri.parse(url));
                mPage.getContext().startActivity(intent);
            } catch (android.content.ActivityNotFoundException e) {
                LOG.e(TAG, "Error showing map " + url + ": " + e.toString());
            }
        }

        // If sending email (mailto:abc@corp.com)
        else if (url.startsWith(WebView.SCHEME_MAILTO)) {
            try {
                Intent intent = new Intent(Intent.ACTION_VIEW);
                intent.setData(Uri.parse(url));
                mPage.getContext().startActivity(intent);
            } catch (android.content.ActivityNotFoundException e) {
                LOG.e(TAG, "Error sending email " + url + ": " + e.toString());
            }
        }

        // If sms:5551212?body=This is the message
        else if (url.startsWith("sms:")) {
            try {
                Intent intent = new Intent(Intent.ACTION_VIEW);

                // Get address
                String address = null;
                int parmIndex = url.indexOf('?');
                if (parmIndex == -1) {
                    address = url.substring(4);
                } else {
                    address = url.substring(4, parmIndex);

                    // If body, then set sms body
                    Uri uri = Uri.parse(url);
                    String query = uri.getQuery();
                    if (query != null) {
                        if (query.startsWith("body=")) {
                            intent.putExtra("sms_body", query.substring(5));
                        }
                    }
                }
                intent.setData(Uri.parse("sms:" + address));
                intent.putExtra("address", address);
                intent.setType("vnd.android-dir/mms-sms");
                mPage.getContext().startActivity(intent);
            } catch (android.content.ActivityNotFoundException e) {
                LOG.e(TAG, "Error sending sms " + url + ":" + e.toString());
            }
        }

        // All else
        else {

            // If our app or file:, then load into a new Cordova webview container by starting a new instance of our activity.
            // Our app continues to run.  When BACK is pressed, our app is redisplayed.
            if (url.startsWith("file://") || !url.startsWith("load://")
                || !url.startsWith("alipays://") || !url.startsWith("alipay://")/*|| url.indexOf(this.ctx.baseUrl) == 0*/
                || mPage.getRunTime().isUrlWhiteListed(url)) {
                mPage.loadUrlIntoView(url);
            }

            // If not our application, let default viewer handle
            else {
                try {
                    Intent intent = new Intent(Intent.ACTION_VIEW);
                    intent.setData(Uri.parse(url));
                    mPage.getContext().startActivity(intent);
                } catch (android.content.ActivityNotFoundException e) {
                    LOG.e(TAG, "Error loading url " + url, e);
                }
            }
        }
        return true;
    }

    /**
     * On received http auth request.
     * The method reacts on all registered authentication tokens. There is one and only one authentication token for any host + realm combination 
     * 
     * @param view
     *            the view
     * @param handler
     *            the handler
     * @param host
     *            the host
     * @param realm
     *            the realm
     */
    @Override
    public void onReceivedHttpAuthRequest(WebView view, HttpAuthHandler handler, String host,
                                          String realm) {

        // get the authentication token
        AuthenticationToken token = mPage.getRunTime().getAuthenticationToken(host, realm);

        if (token != null) {
            handler.proceed(token.getUserName(), token.getPassword());
        }
    }

    @Override
    public void onPageStarted(WebView view, String url, Bitmap favicon) {
        LOG.d("WebAppLog", "load start   :" + System.currentTimeMillis());
        //mPage.getRunTime().getWebServer().addInjectUrl(url);
        if (url.startsWith("http://")||url.startsWith("https://")) {
            String msg = mPage.getRunTime().getResources().getString(R.string.PleaseWait);
            mPage.getRunTime().openProgress(mPage.getContext(),msg,true);
        }
        // Clear history so history.back() doesn't do anything.  
        // So we can reinit() native side CallbackServer & PluginManager.
        //        view.clearHistory(); 
        //        this.doClearHistory = true;
    }

    /**
     * Notify the host application that a page has finished loading.
     * 
     * @param view          The webview initiating the callback.
     * @param url           The url of the page.
     */
    @Override
    public void onPageFinished(WebView view, String url) {
        super.onPageFinished(view, url);
        LOG.d("WebAppLog", mPage.getAppView().copyBackForwardList().getCurrentIndex() + "/"
                           + mPage.getAppView().copyBackForwardList().getSize() + "load finish  :"
                           + System.currentTimeMillis());
        /**
         * Because of a timing issue we need to clear this history in onPageFinished as well as 
         * onPageStarted. However we only want to do this if the doClearHistory boolean is set to 
         * true. You see when you load a url with a # in it which is common in jQuery applications
         * onPageStared is not called. Clearing the history at that point would break jQuery apps.
         */
        //        if (this.doClearHistory) {
        //            view.clearHistory();
        //            this.doClearHistory = false;
        //        }

        mPage.setLoadFinish(true);
        mPage.getRunTime().closeProgress();
        // Try firing the onNativeReady event in JS. If it fails because the JS is
        // not loaded yet then just set a flag so that the onNativeReady can be fired
        // from the JS side when the JS gets to that code.
        if (!url.equals("about:blank") && !mPage.isError()) {
            int port = mPage.getRunTime().getWebServer().getPort();
            mPage
                .getAppView()
                .loadUrl(
                    "javascript:var oHead = document.getElementsByTagName('HEAD').item(0); var oScript= document.createElement(\"script\"); oScript.type = \"text/javascript\"; oScript.src = \"http://127.0.0.1:"
                            + port
                            + "/AWebApp.js\";if(typeof cordova === 'undefined'){oHead.appendChild(oScript);}");

            File f = new File(mPage.getRunTime().getPath() + INJECT_JS);
            if (f.exists()) {
                mPage
                    .getAppView()
                    .loadUrl(
                        "javascript:var oHead = document.getElementsByTagName('HEAD').item(0); var oScript= document.createElement(\"script\"); oScript.type = \"text/javascript\"; oScript.src = \"http://127.0.0.1:"
                                + port + "/" + INJECT_JS + "\";oHead.appendChild(oScript);");
            }
            //            }
            //            ctx.appView.loadUrl("javascript:try{ cordova.require('cordova/channel').onNativeReady.fire();}catch(e){_nativeReady = true;}");
            mPage.postMessage("onNativeReady", null);
        }

        // Shutdown if blank loaded
        //        if (url.equals("about:blank")) {
        //           mPage.endActivity();
        //        }
    }

    /**
     * Report an error to the host application. These errors are unrecoverable (i.e. the main resource is unavailable). 
     * The errorCode parameter corresponds to one of the ERROR_* constants.
     *
     * @param view          The WebView that is initiating the callback.
     * @param errorCode     The error code corresponding to an ERROR_* value.
     * @param description   A String describing the error.
     * @param failingUrl    The url that failed to load. 
     */
    @Override
    public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
        LOG.d(TAG, "DroidGap: GapViewClient.onReceivedError: Error code=%s Description=%s URL=%s",
            errorCode, description, failingUrl);

        mPage.setLoadFinish(true);
        mPage.getRunTime().closeProgress();
        // Handle error
        mPage.onReceivedError(errorCode, description, failingUrl);
    }

    public void onReceivedSslError(WebView view, SslErrorHandler handler, SslError error) {
        mPage.getRunTime().closeProgress();

        final String packageName = mPage.getContext().getPackageName();
        final PackageManager pm = mPage.getContext().getPackageManager();
        ApplicationInfo appInfo;
        try {
            appInfo = pm.getApplicationInfo(packageName, PackageManager.GET_META_DATA);
            if ((appInfo.flags & ApplicationInfo.FLAG_DEBUGGABLE) != 0) {
                // debug = true
                handler.proceed();
                return;
            } else {
                // debug = false
                handler.cancel();
                //                super.onReceivedSslError(view, handler, error);    
            }
        } catch (NameNotFoundException e) {
            // When it doubt, lock it out!
            handler.cancel();
            //            super.onReceivedSslError(view, handler, error);
        }
    }

    @Override
    public void doUpdateVisitedHistory(WebView view, String url, boolean isReload) {
        /* 
         * If you do a document.location.href the url does not get pushed on the stack
         * so we do a check here to see if the url should be pushed.
         */
        //        if (!this.ctx.peekAtUrlStack().equals(url)) {
        //            this.ctx.pushUrl(url);
        //        }
        super.doUpdateVisitedHistory(view, url, isReload);
    }
}
