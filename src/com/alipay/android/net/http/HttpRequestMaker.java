/**
 * 
 */
package com.alipay.android.net.http;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.json.JSONObject;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import android.content.Context;

import com.alipay.android.net.RequestMaker;
import com.alipay.android.util.LogUtil;
import com.alipay.platform.core.Command;

/**
 * @author sanping.li
 *
 */
public class HttpRequestMaker implements RequestMaker {
    public static final String       VERSION  = "version";
    public static final String       ROOT     = "InterfaceList";
    public static final String       ITEM     = "Interface";
    public static final String       PARAM    = "Param";
    public static final String       ATTR_ID  = "id";
    public static final String       ATTR_URL = "url";
    public static final String       ATTR_RA  = "relAccount";

    private HashMap<String, Request> mDcits;
    private String                   mVersion;
    private Context mContext;

    public HttpRequestMaker(Context context,String cfgPath) {
        mContext = context;
        mDcits = new HashMap<String, Request>();
        
        try {
            InputStream inputStream = new FileInputStream(new File(cfgPath));
            init(inputStream);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    

    public HttpRequestMaker(Context context,int cfgId) {
        mContext = context;
        mDcits = new HashMap<String, Request>();
        
        try{
            InputStream inputStream = context.getResources().openRawResource(cfgId);
            init(inputStream);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }



    private void init(InputStream inputStream) throws Exception {
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = factory.newDocumentBuilder();
        Document dom = builder.parse(inputStream);

        Element root = dom.getDocumentElement();
        mVersion = root.getAttribute(VERSION);

        Request request = null;
        NodeList nodeList = root.getChildNodes();
        for (int i = 0; i < nodeList.getLength(); i++) {
            Node item = nodeList.item(i);
            if (item.getNodeType() != Node.ELEMENT_NODE)
                continue;
            String id = ((Element) item).getAttribute(ATTR_ID);
            String url = ((Element) item).getAttribute(ATTR_URL);
            request = new Request(url);
            String relAccount = ((Element) item).getAttribute(ATTR_RA);
            request.setRelAccount(relAccount.equals("true"));
            NodeList params = item.getChildNodes();
            for (int j = 0; j < params.getLength(); j++) {
                Node param = params.item(j);
                if (param.getNodeType() != Node.ELEMENT_NODE)
                    continue;
                if (param.getNodeName().equals(PARAM))
                    request.setPostData(param.getFirstChild().getNodeValue());
            }
            mDcits.put(id, request);
        }
    }

    @Override
    public void makeRequest(Command command) {
        String id = command.getmId();
        if(id == null){
            return;
        }
        
        Request request = mDcits.get(id);
        if(request==null){
           return; 
        }
        
        String url = request.getUrl(mContext);
        if("13".equals(id)){
        	url = url.replace("https", "http").replace("mobilecs.htm", "mtk.htm");
        }
        command.setRequestUrl(url);
        ArrayList<String> params = command.getRequestParam();
        String post = request.getPostData();
        StringBuffer sb = new StringBuffer(post);
        int cursor = 0;
        String tmp = null;
        int argCount = HttpUtils.occurrenceOf(sb.toString(), "@");
        for (int i = 0; i < argCount; ++i) {
            String replace = "@" + (i+1);
            int index = sb.indexOf(replace,cursor);
            tmp = params.get(i);
            
            if(tmp == null){
            	LogUtil.logOnlyDebuggable("HttpRequestMaker", "===============>index= "+index+";replace= "+replace+";temp == null");
                tmp="";
            }
            sb.replace(index, index+replace.length(), tmp);
            cursor = index+tmp.length();
//            post = post.replace("@" + (i+1), params.get(i));
        }

        String refinedInfo = appendAuthInfo(sb.toString(), params);
        command.setRequestData(refinedInfo);
    }

    private String appendAuthInfo(String info, ArrayList<String> params) {
        String refinedInfo = info;
        try {
            JSONObject obj = new JSONObject(info);
            String sessionId = params.get(params.size() - 2);
            if (sessionId != null)
                obj.put("sessionId", sessionId);

            String clientID = params.get(params.size() - 1);
            if (clientID != null)
                obj.put("clientID", clientID);

            refinedInfo = obj.toString();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return refinedInfo;
    }

    @Override
    public String getVersion() {
        return mVersion;
    }
    
    @Override
    public boolean relAccount(String id){
        Request request = mDcits.get(id);
        if(request==null){
           return false; 
        }
        
        return request.getRelAccount();
    }

}
