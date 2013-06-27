/**
 * 
 */
package com.alipay.android.appHall.component.WebBox;

import java.util.Iterator;

import org.json.JSONObject;

import android.net.Uri;
import android.webkit.WebView;

import com.alipay.android.appHall.component.UIWebBox;
import com.alipay.android.core.Engine;
import com.alipay.android.core.expapp.ExpAppRuntime;
import com.alipay.android.core.expapp.Page;

/**
 * @author sanping.li
 *
 */
public class Scheme {
    private static final String ALIPAY_SCHEME = "alipay";
    private static final String ALIPAY_HOST   = "platformapi";
    private static final String ALIPAY_ACTION_EXP = "expression";
    private static final String ALIPAY_ACTION_PAY = "pay";
    private static final String ALIPAY_ACTION_GOHOME = "gohome";
    
    private static final String ALIPAY_PACKAGE = "pkg";

    private Page mPage;
//    private UIWebBox mWebBox;
    
    public Scheme(UIWebBox webBox){
        mPage = webBox.getPage();
//        mWebBox = webBox;
    }
    

    public boolean processScheme(WebView view, String url) throws Exception {
     // 部分浏览器对http response中的Location:alipay://pay跳转，会自动在前面增加http://www.okooo.com:80/alipay://pay，需要预处理此类现象。
        url = preprocessUrl(url);
        Uri uri = Uri.parse(url.toLowerCase());
        if(uri.getScheme().equals(ALIPAY_SCHEME)&&uri.getHost().equals(ALIPAY_HOST)){
            String action = uri.getPath();
            if(action.endsWith(ALIPAY_ACTION_EXP)){
                String rules = uri.getQueryParameter("rules");
                if (rules!=null&&rules.length()>0) {// rules
                    JSONObject jsonObject = new JSONObject(rules);
                    Iterator<?> iterator = jsonObject.keys();
                    while (iterator.hasNext()) {
                        String name = iterator.next().toString();
                        String val = jsonObject.opt(name).toString();
                        mPage.getEngine().addRule(name, val);
                    }
                }
                String exp = uri.getQueryParameter("exp");
                Object id = view.getTag()==null?"":view.getTag();
                mPage.interpreter("WebBox-exp::"+id.toString(),exp);
            }else if(action.endsWith(ALIPAY_ACTION_PAY)){
                String tradeNO = getParam(uri,"tradeno","@NULL");
                String partnerID = getParam(uri,"partnerid","@NULL");
                String bizType = getParam(uri,"biztype","@NULL");
                String bizSubType = getParam(uri,"bizsubtype","@NULL");
                String successUrl = getParam(uri,"successurl","");
                String failUrl = getParam(uri,"failurl","");
                mPage.getEngine().addValue("z"+tradeNO+"successurl", successUrl);
                mPage.getEngine().addValue("z"+tradeNO+"failurl", failUrl);
                Object id = view.getTag()==null?"":view.getTag();
                mPage.getEngine().addRule("z"+tradeNO, "{\"9000\":\"$ctrl_value_set("+id+",@z"+tradeNO+"successurl)\",\"*\":\"$ctrl_value_set("+id+",@z"+tradeNO+"failurl)\"}");
                if(successUrl.length()>0||failUrl.length()>0)
                    mPage.interpreter("WebBox-pay::"+id.toString(),"$do_pay("+tradeNO+","+partnerID+","+bizType+","+bizSubType+","+"z"+tradeNO+")");
                else
                    mPage.interpreter("WebBox-pay::"+id.toString(),"$do_pay("+tradeNO+","+partnerID+","+bizType+","+bizSubType+")");
            }else if(action.endsWith(ALIPAY_ACTION_GOHOME)){
                Object id = view.getTag()==null?"":view.getTag();
                mPage.interpreter("WebBox-exit::"+id.toString(),"$exit()");
            }
            return true;
        }else if(uri.getScheme().equals(ALIPAY_PACKAGE)){
            String path = uri.getHost()+uri.getPath();
            Engine engine = mPage.getEngine();
            String abPath = null;
            if(engine instanceof ExpAppRuntime){
                abPath = "file://"+((ExpAppRuntime)engine).getPath()+"res/"+path;
                view.loadUrl(abPath);
            }
        }
        return false;
    }
    
    private String getParam(Uri uri,String key,String def){
        String ret = uri.getQueryParameter(key);
        if(ret==null||ret.length()==0)
            ret=def;
        return ret;
    }

    private String preprocessUrl(String url) {
        if (url != null) {
            int index = url.indexOf(ALIPAY_SCHEME + "://");
            if (index != -1) {
                url = url.substring(index);
            }
        }

        return url;
    }
}
