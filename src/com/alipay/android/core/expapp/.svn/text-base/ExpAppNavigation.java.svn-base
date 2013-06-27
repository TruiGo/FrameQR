package com.alipay.android.core.expapp;

import java.util.Stack;

import android.app.Activity;
import android.view.View;

import com.alipay.android.client.RootActivity;
import com.alipay.android.log.AlipayLogAgent;
import com.alipay.android.log.Constants;
import com.alipay.android.log.StorageStateInfo;

public class ExpAppNavigation {
    private Stack<NormalPage> mPageStack;
    private ExpAppRuntime mRuntime;

    public ExpAppNavigation(ExpAppRuntime runtime) {
        mRuntime = runtime;
        mPageStack = new Stack<NormalPage>();
    }

    public int getLocation(String viewId) {
        int ret = -1;
        Page page = null;
        for (int i = mPageStack.size() - 1; i >= 0; --i) {
            page = mPageStack.get(i);
            if (page.getPageName().equalsIgnoreCase(viewId)) {
                ret = mPageStack.size() - i;
                break;
            }
        }
        return ret;
    }

    public Page push2Stack(Page page) {
        // push the new page into the stack.
        this.addPage((NormalPage) page);
        this.mPageStack.push((NormalPage) page);
        return page;
    }
    
    public void pop4Stack(int num) {
        // remove the top page of the stack first.
        if (!this.mPageStack.empty() && num < mPageStack.size()) {
            RootActivity activity = (RootActivity) mRuntime.getContext();
           /* StorageStateInfo storageStateInfo = StorageStateInfo.getInstance();
            AlipayLogAgent.onEvent(activity,
                    Constants.MONITORPOINT_EVENT_VIEWRETURN, 
                    "",
                    getCurrentPage() == null ? "" : getCurrentPage().getRealPageName(),
                            storageStateInfo.getValue(Constants.STORAGE_APPID),
                            storageStateInfo.getValue(Constants.STORAGE_APPVERSION),
                            storageStateInfo.getValue(Constants.STORAGE_PRODUCTID),
                            activity.getUserId(),
                            storageStateInfo.getValue(Constants.STORAGE_PRODUCTVERSION),
                            storageStateInfo.getValue(Constants.STORAGE_CLIENTID));*/
            
            this.mPageStack.pop();
            // NormalPage top = this.mPageStack.pop();
            // NormalPageFrame.this.getPageFrame().removeView(top.mPage);

            for (int i = 1; i < num; ++i)
                this.mPageStack.pop();

            NormalPage toBeTop = this.mPageStack.peek();
            toBeTop.onRefresh();
            this.addPage(toBeTop);
        } else {
            mRuntime.exit();
        }
    }

    private void addPage(NormalPage page) {
        Activity activity = (Activity) mRuntime.getContext();
        View tempPage = page.mPage;
        activity.setContentView(tempPage);
    }
    

    public Page createPage(final String pageName) {
        // create a new page.
        Page page = new NormalPage(mRuntime);
        page.setPageName(pageName);

        return page;
    }

    public Page getCurrentPage() {
        if (mPageStack.empty())
            return null;
        return mPageStack.peek();
    }

    public boolean isEmpty(){
        return mPageStack.isEmpty();
    }
    
    public NormalPage peek4Statck(){
        return mPageStack.peek();
    }
}
