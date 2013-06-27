/**
 * 
 */
package com.alipay.android.core;

import java.util.HashMap;
import java.util.Map;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import com.alipay.android.appHall.common.Defines;
import com.alipay.android.appHall.security.CertHandler;
import com.alipay.android.core.expapp.AppUiElementType;
import com.alipay.android.log.Constants;
import com.alipay.android.log.StorageStateInfo;
import com.alipay.android.util.LogUtil;

/**
 * @author sanping.li
 *
 */
public class ManifestDoc {
    final static String ORSPLIT = "\\|";

    private String mPath;
    private Map<String, Object> mContents;

    public ManifestDoc(String path) {
        mPath = path;
        mContents = new HashMap<String, Object>();
        init();
    }

    private void init() {
        DocumentBuilderFactory dbfactory = DocumentBuilderFactory.newInstance();
        Document doc = null;
        try {
            DocumentBuilder docBuilder = dbfactory.newDocumentBuilder();
            doc = docBuilder.parse(CertHandler.verify(mPath, Defines.ManifestName));
        } catch (Exception e) {
            LogUtil.logContainerDebuggable(CertHandler.TAG,
                "Parser Manifest.xml error: " + e.getMessage());
            e.printStackTrace();
            return;
        }
        Element root = doc.getDocumentElement();

        //处理requisite
        Element requisiteElement = (Element) root.getElementsByTagName("requisite").item(0);
        String requisite = null;
        if (requisiteElement.hasChildNodes()) {
            requisite = requisiteElement.getFirstChild().getNodeValue();
        }
        if (requisite != null && requisite.length() > 0) {
            if (requisite.indexOf(ORSPLIT) == -1) {
                mContents.put("requisite", requisite);
            } else {
                String[] requisiteArray = requisite.split(ORSPLIT);
                mContents.put("requisite", requisiteArray);
            }
        } else {
            mContents.put("requisite", "");
        }
        //处理样式
        Element styleElement = (Element) root.getElementsByTagName("style").item(0);
        if(styleElement!=null&&styleElement.getFirstChild()!=null){
            mContents.put("style", styleElement.getFirstChild().getNodeValue());
        }else{
            mContents.put("style", "");
        }

        //处理minSdkVersion
        Element minSdkVersion = (Element) root.getElementsByTagName("uses-sdk").item(0);
        String version = minSdkVersion.getAttribute("minSdkVersion");
        mContents.put("uses-sdk", version);

        //处理version
        Element ver = (Element) root.getElementsByTagName("version").item(0);
        String v = ver.getFirstChild().getNodeValue();
        mContents.put("version", v);
        StorageStateInfo.getInstance().putValue(Constants.STORAGE_APPVERSION, v);

        //处理entry
        Element entry = (Element) root.getElementsByTagName("entry").item(0);
        String entryLayout = entry.getFirstChild().getNodeValue();
        //all "entry" attributes in Manifest.xml are added to XmlAppRunTime.manifests 
        mContents.put("entry", entryLayout);

        //处理layouts
        Element layouts = (Element) root.getElementsByTagName("layouts").item(0);
        if (layouts != null) {
            HashMap<String, String> lays = new HashMap<String, String>();
            NodeList children = layouts.getChildNodes();
            //all "layouts" attributes in Manifest.xml are added to XmlAppRunTime.layouts
            for (int i = 0; i < children.getLength(); i++) {
                Node item = children.item(i);
                if (item.getNodeType() != Node.ELEMENT_NODE)
                    continue;
                NamedNodeMap attributes = item.getAttributes();
                Node id = attributes.getNamedItem(AppUiElementType.id);
                String nodeValue = id.getNodeValue();
                String path = attributes.getNamedItem(AppUiElementType.src).getNodeValue();
                lays.put(nodeValue.toLowerCase(), path);
            }
            mContents.put("layouts", lays);
        }
        

        //处理Type
        Element typeElement = (Element) root.getElementsByTagName("app-type").item(0);
        if(typeElement!=null&&typeElement.getFirstChild()!=null){
            mContents.put("type", typeElement.getFirstChild().getNodeValue());
        }else{
            mContents.put("type", "");
        }
    }

    public Object getMinSdkVersion() {
        return getManifest("uses-sdk");
    }

    public Object getRequisite() {
        return getManifest("requisite");
    }

    public Object getType() {
        return getManifest("type");
    }

    public Object getManifest(String key) {
        if (!mContents.containsKey(key)) {
            LogUtil.logContainerDebuggable("MANIFEST", "Can't find manifest property : key = "
                                                       + key);
            return null;
        }
        return mContents.get(key.toLowerCase());
    }
}
