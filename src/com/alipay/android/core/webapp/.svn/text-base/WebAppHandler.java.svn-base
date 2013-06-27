/**
 * 
 */
package com.alipay.android.core.webapp;

import android.net.Uri;

/**
 * @author sanping.li
 *
 */
public class WebAppHandler {
    private Page mPage;

    public WebAppHandler(Page page) {
        mPage = page;
    }

    public void start(String url, String params) {
//        url= "http://votbar.sinaapp.com/w/index.html";
        if (url.startsWith("http") || url.startsWith("https")) {
            ;
        } else {
            //int port = mPage.getRunTime().getWebServer().getPort();
            if (url.startsWith("file://") && url.contains(mPage.getRunTime().getPath())) {
                /*url = "http://127.0.0.1:"+port+ url.substring(7)*/;
            }else{
                //url = "http://127.0.0.1:"+port+ "/www/" + url;
                url = "file://" + mPage.getRunTime().getPath() + "/www/" + url;
            }
        }
        String str = url;
        if (params != null && params.length() > 0) {//如果参数不为空
            Uri uri = Uri.parse(url);
            String query = uri.getQuery();
            if(query!=null&&query.length()>0){
                query += ("&"+params);
            }else{
                query = params;
            }
            str = uri.getScheme()+"://"+uri.getAuthority()+uri.getPath()+"?"+query;//重新组装url
            if(uri.getFragment()!=null&&uri.getFragment().length()>0){
                str += ("#"+uri.getFragment());
            }
        }
        mPage.loadUrlIntoView(str);
    }
}
