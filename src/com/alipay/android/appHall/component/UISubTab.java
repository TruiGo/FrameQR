package com.alipay.android.appHall.component;

import java.util.HashMap;

import android.content.Context;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TabHost;
import android.widget.TabHost.OnTabChangeListener;
import android.widget.TabHost.TabContentFactory;

import com.alipay.android.appHall.uiengine.UIInterface;
import com.alipay.android.core.expapp.Page;
import com.eg.android.AlipayGphone.R;

public class UISubTab extends LinearLayout implements UIInterface, OnTabChangeListener {
    private Context context;
    private Page[] mPages;
    private TabHost tabHost;
    private boolean[] mIsRefresh;
    
    public UISubTab(Page pageContext) {
        super(pageContext.getRawContext());
        this.context = pageContext.getRawContext();
        pageContext.setScrollBarEnabled(false);
        pageContext.getContent().removeView(pageContext.getConfirmBar());
        pageContext.getView().findViewById(R.id.TitleShadow).setVisibility(View.GONE);
    }

    public void init(ViewGroup parent, String[] names,Page[] pages) {
        tabHost = (TabHost)LayoutInflater.from(context).inflate(R.layout.uitag,parent,false);//(TabHost) fakeLayout.findViewById(R.id.mtabhost);
        tabHost.setup();
        
        mPages = pages;
        mIsRefresh = new boolean[pages.length];

        TabContentFactory content = new TabContentFactory() {
            
            @Override
            public View createTabContent(String tag) {
                int index = Integer.parseInt(tag);
                
                View view =null;
                if(!mIsRefresh[index]){
                    view = mPages[index].getContent();
                    ((ViewGroup) view.getParent()).removeView(view);
                    mPages[index].setView(view);
                }
//                view = mPages[index].getView();
                
                mPages[index].onAppear();
                mIsRefresh[index] = true;
                
                return view;
            }
        };
        
        final ViewGroup Tabwidget = (ViewGroup)tabHost.findViewById(R.id.TypeButtonTitleContent);
        
        for(int i = 0;i<names.length;++i){
            tabHost.addTab(tabHost.newTabSpec(""+i)
                .setIndicator(null, null)
                .setContent(content));
            
            Button button = new Button(context);
            button.setTextColor(context.getResources().getColorStateList(R.drawable.tab_text_color));
            button.setGravity(Gravity.CENTER_HORIZONTAL|Gravity.CENTER_VERTICAL);
            button.setTextSize(18);
            button.setBackgroundResource(R.drawable.tab_selector);
            if(i==0){
                button.setSelected(true);
            }
            float scale = getContext().getResources().getDisplayMetrics().density;
            button.setPadding(0, (int)(10* scale), 0, (int)(12* scale));
            
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LayoutParams.FILL_PARENT,LayoutParams.WRAP_CONTENT);
            params.weight = 1;
            button.setLayoutParams(params);
            button.setText(names[i]);
            button.setTag(""+i);
            
            final int ii = i;
            button.setOnClickListener(new OnClickListener() {

                @Override
                public void onClick(View v) {
                    String tag = tabHost.getCurrentTabTag();
                    Button button = (Button) Tabwidget.findViewWithTag(tag);
                    if(!button.getTag().equals(""+ii))
                        button.setSelected(false);
                    tabHost.setCurrentTab(ii);
                }
            });
            Tabwidget.addView(button);
        }
                
        tabHost.setOnTabChangedListener(this);
        addView(tabHost);
    }

    @Override
    public android.view.ViewGroup.LayoutParams getLayoutParams() {
        // TODO Auto-generated method stub
        return getAlipayLayoutParams();
    }
    
    public void setVisible(boolean isVisible) {
        setVisibility(isVisible ? View.VISIBLE : View.GONE);
    }

    public boolean getVisible() {
        return getVisibility() == View.VISIBLE;
    }

    // ----------------------------设置宽度
    private boolean isChange = true;
    private int     width    = ViewGroup.LayoutParams.FILL_PARENT;

    public void set_Width(int width) {
        isChange = true;
        this.width = width;
        changedWidth();
    }

    private void changedWidth() {
        ViewGroup.LayoutParams params = (ViewGroup.LayoutParams) findViewById(R.id.mtabhost).getLayoutParams();
        params.width = this.width;
    }

    @Override
    protected void onWindowVisibilityChanged(int visibility) {
        super.onWindowVisibilityChanged(visibility);
        if (isChange) {
            changedWidth();
        }
        isChange = false;
        // 最后测试动态更改宽度
        requestLayout();
    }

    @Override
    public void setValue(Object value) {
        // TODO Auto-generated method stub
    }

    @Override
    public Object getValue() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public void setEnable(boolean enabled) {
        this.setEnabled(enabled);
    }

    @Override
    public boolean getEnable() {
        // TODO Auto-generated method stub
        return isEnabled();
    }

    @Override
    public String getSelectedIndex() {
        return null;
    }
    
    private boolean isSave;

    @Override
    public void setIsSave(boolean isSave) {
        this.isSave = isSave;
    }

    @Override
    public boolean getIsSave() {
        return isSave;
    }

    @Override
    public void set_MarginLeft(int marginLeft) {
        LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) findViewById(R.id.mtabhost).getLayoutParams();
        params.leftMargin = marginLeft;
    }

    @Override
    public int get_MarginLeft() {
        LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) findViewById(R.id.mtabhost).getLayoutParams();
        return params.leftMargin;
    }

    @Override
    public void set_MarginRight(int marginRight) {
        LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) findViewById(R.id.mtabhost).getLayoutParams();
        params.rightMargin = marginRight;
    }

    @Override
    public int get_MarginRight() {
        LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) findViewById(R.id.mtabhost).getLayoutParams();
        return params.rightMargin;
    }
    
    @Override
    public void set_MarginTop(int marginTop) {
        LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) findViewById(
            R.id.mtabhost).getLayoutParams();
        params.topMargin = marginTop;
    }

    @Override
    public int get_MarginTop() {
        LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) findViewById(
            R.id.mtabhost).getLayoutParams();
        return params.topMargin;
    }
    
    @Override
    public void set_MarginBottom(int marginBottom) {
        LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) findViewById(
            R.id.mtabhost).getLayoutParams();
        params.bottomMargin = marginBottom;
    }

    @Override
    public int get_MarginBottom() {
        LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) findViewById(
            R.id.mtabhost).getLayoutParams();
        return params.bottomMargin;
    }

    @Override
    public int get_Width() {
        return getWidth();
    }

    @Override
    public void set_Id(int id) {
        setId(id);
    }

    @Override
    public int get_Id() {
        return getId();
    }

    @Override
    public void set_Text(String text) {
    }

    @Override
    public String get_Text() {
        return null;
    }

    @Override
    public String get_Tag() {
        return (String) this.getTag();
    }

    @Override
    public void set_Tag(String tag) {
        this.setTag(tag);
    }

    @Override
    public void onTabChanged(String tag) {
        ViewGroup Tabwidget = (ViewGroup)tabHost.findViewById(R.id.TypeButtonTitleOther);
        Button button = (Button) Tabwidget.findViewWithTag(tag);
        button.setSelected(true);
        
        int index = Integer.parseInt(tag);
        if(mIsRefresh[index])
            mPages[index].onRefresh();
    }

    @Override
    public LinearLayout.LayoutParams getAlipayLayoutParams() {
        return (LinearLayout.LayoutParams) findViewById(
            R.id.mtabhost).getLayoutParams();
    }
    

    //tag for android 1.5
    HashMap<Integer, Object> mTags = new HashMap<Integer, Object>();
    @Override
    public Object get_tag(int type) {
        return mTags.get(type);
    }

    @Override
    public void set_Tag(int type, Object tag) {
        mTags.put(type, tag);
    }

	public Page getCurrentPage() {
		// TODO Auto-generated method stub
		return mPages[tabHost.getCurrentTab()];
	}
}
