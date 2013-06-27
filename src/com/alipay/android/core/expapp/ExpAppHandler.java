package com.alipay.android.core.expapp;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import android.app.Activity;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.widget.LinearLayout;

import com.alipay.android.appHall.security.CertHandler;
import com.alipay.android.appHall.uiengine.UIInterface;
import com.alipay.android.client.AlipayApplication;
import com.alipay.android.util.LogUtil;

public class ExpAppHandler {
    public final static String RES_FILE_PATH = "res/res.xml";
    public final static String orSplit = "\\|";
    public final static String interfaceName = "res/interface.xml";
    public final static String LAYOUT_DIR_PATH = "layout/";

    private ExpAppRuntime mRuntime;

    public ExpAppHandler(ExpAppRuntime runtime) {
        mRuntime = runtime;
    }

    public void start(String viewId) {
        //验证合法性
        if (!mRuntime.getCertHandler().init() || !prepareDocForRes()) {
            mRuntime.exit();
            return;
        }
        // Make the entry page for adding elements.
        loadPage(viewId == null ? (String) mRuntime.getManifest("entry") : viewId);
    }

    private void setConfirmBar(Document doc, Page page) {
        try {
            Element root = doc.getDocumentElement();
            Element confirmBar = (Element) root.getElementsByTagName("ConfirmBar").item(0);
            NodeList childrens = confirmBar.getChildNodes();
            int grav = Gravity.LEFT;
            for (int i = 0; i < childrens.getLength(); i++) {
                Node childNode = childrens.item(i);
                try {
                    UiFactory uiFactory = new AppUiFactory(page);
                    View child = uiFactory.makeElement(childNode, page.getConfirmBar());
                    if (child != null) {
                        LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) ((UIInterface) child)
                            .getAlipayLayoutParams();
                        params.gravity |= grav;
                        if (params.width == LayoutParams.FILL_PARENT)
                            params.weight = 1;
                        page.addConfirmBarElement(child, params);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void setFormPanelContent(Document doc, Page page) {
        try {
            Element root = doc.getDocumentElement();

            Element formPanel = (Element) root.getElementsByTagName("FormPanel").item(0);
            NodeList childrens = formPanel.getChildNodes();
            for (int i = 0; i < childrens.getLength(); i++) {
                Node childNode = childrens.item(i);

                try {
                    UiFactory uiFactory = new AppUiFactory(page);
                    View child = uiFactory.makeElement(childNode, page.getFormPanel());
                    page.addElement(child);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private String getPageTitle(Document doc) {
        String pageTitle = null;

        try {
            Element root = doc.getDocumentElement();
            pageTitle = root.getAttribute(AppUiElementType.title);
            if (pageTitle.startsWith(AppUiElementType.Reference))
                pageTitle = mRuntime.getValue(pageTitle.replace(AppUiElementType.Reference, ""));
        } catch (Exception e) {
            e.printStackTrace();
        }

        return pageTitle;
    }

    public void loadPage(String pageId) {
        Page page = createPage(pageId);

        AlipayApplication application = (AlipayApplication) mRuntime.getContext().getApplicationContext();
        application.setActivity((Activity) mRuntime.getContext());
        mRuntime.push2Stack(page);
        page.preAppear();
        page.onAppear();
        if (!page.getInputBoxIsNullListener().hasWaitInputView()) {
            page.getInputBoxIsNullListener().setEnable();
        }
    }

    public Page createPage(String pageId) {
        Page page = mRuntime.createPage(pageId);
        Document doc = documentFormPageId(pageId);

        // set the page title.
        String pageTitle = getPageTitle(doc);
        page.setPageTitle(pageTitle);

        // set the content of form panel.
        setFormPanelContent(doc, page);

        // set the content of the confirm bar.
        setConfirmBar(doc, page);

        // call preAppear().
        String preAppearString = this.getPreAppearString(doc);
        page.setPreAppear(preAppearString);

        // call onAppear().
        String onAppearString = this.getOnAppearString(doc);
        page.setOnAppearString(onAppearString);

        // call onRefresh().
        String onRefreshString = this.getOnRefreshString(doc);
        page.setOnRefresh(onRefreshString);
        //        page.onRefresh();

        String onCancelString = getOnCancelString(doc);
        page.setOnCancel(onCancelString);

        boolean cancelCheck = getCancelCheck(doc);
        page.setCancelCheckInput(cancelCheck);
        return page;
    }

    private String getPreAppearString(Document doc) {
        String value = null;

        try {
            Element root = doc.getDocumentElement();
            value = root.getAttribute(AppUiElementType.preAppear);
            if (value.startsWith(AppUiElementType.Reference))
                value = mRuntime.getValue(value.replace(AppUiElementType.Reference, ""));
        } catch (Exception e) {
            e.printStackTrace();
        }

        return value;
    }

    private String getOnAppearString(Document doc) {
        String value = null;

        try {
            Element root = doc.getDocumentElement();
            value = root.getAttribute(AppUiElementType.onAppear);
            if (value.startsWith(AppUiElementType.Reference))
                value = mRuntime.getValue(value.replace(AppUiElementType.Reference, ""));
        } catch (Exception e) {
            e.printStackTrace();
        }

        return value;
    }

    private boolean getCancelCheck(Document doc) {
        boolean ret = false;

        try {
            Element root = doc.getDocumentElement();
            String value = root.getAttribute(AppUiElementType.CancelCheckInput);
            if (value.startsWith(AppUiElementType.Reference))
                value = mRuntime.getValue(value.replace(AppUiElementType.Reference, ""));
            ret = value.equalsIgnoreCase(AppUiElementType.TRUE);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return ret;
    }

    private String getOnRefreshString(Document doc) {
        String value = null;

        try {
            Element root = doc.getDocumentElement();
            value = root.getAttribute(AppUiElementType.onRefresh);
            if (value.startsWith(AppUiElementType.Reference))
                value = mRuntime.getValue(value.replace(AppUiElementType.Reference, ""));
        } catch (Exception e) {
            e.printStackTrace();
        }

        return value;
    }

    private String getOnCancelString(Document doc) {
        String value = null;

        try {
            Element root = doc.getDocumentElement();
            value = root.getAttribute(AppUiElementType.OnCancel);
            if (value.startsWith(AppUiElementType.Reference))
                value = mRuntime.getValue(value.replace(AppUiElementType.Reference, ""));
        } catch (Exception e) {
            e.printStackTrace();
        }

        return value;
    }

    private Document documentFormPageId(String pageId) {
        Document doc = null;
        DocumentBuilderFactory dbfactory = DocumentBuilderFactory.newInstance();
        try {
            DocumentBuilder docBuilder = dbfactory.newDocumentBuilder();
            doc = docBuilder.parse(mRuntime.getFile(mRuntime.getPath() + LAYOUT_DIR_PATH
                                                    + mRuntime.pagePathFromId(pageId)));
        } catch (Exception e) {
            e.printStackTrace();
        }

        return doc;
    }

    /**
     * 解析res.xml文件,并将解析内容添加到XmlAppRuntime.rules中
     */
    private boolean prepareDocForRes() {
        DocumentBuilderFactory dbfactory = DocumentBuilderFactory.newInstance();
        Document docforRes = null;
        try {
            DocumentBuilder docBuilder = dbfactory.newDocumentBuilder();
            docforRes = docBuilder.parse(mRuntime.getFile(mRuntime.getPath() + RES_FILE_PATH));
        } catch (Exception e) {
            LogUtil.logContainerDebuggable(CertHandler.TAG,
                "Parser Res.xml error: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
        Element root = docforRes.getDocumentElement();
        NodeList children = root.getChildNodes();
        for (int i = 0; i < children.getLength(); i++) {
            try {
                Node item = children.item(i);
                NamedNodeMap attributes = item.getAttributes();
                if (attributes == null)
                    continue;
                Node id = attributes.getNamedItem(AppUiElementType.id);
                String nodeValue = id.getNodeValue();
                if(item.getFirstChild()==null)
                    continue;
                String rawValue = item.getFirstChild().getNodeValue();
                if (item.getNodeName().equalsIgnoreCase("Rule")) {
                    mRuntime.addRule(nodeValue.toLowerCase(), rawValue);
                } else {
                    mRuntime.addValue(nodeValue.toLowerCase(), rawValue);
                }
            } catch (Exception e) {
                LogUtil.logContainerDebuggable(CertHandler.TAG,
                    "Parser Res.xml error: " + e.getMessage());
                //e.printStackTrace();
                return false;
            }
        }
        return true;
    }
}
