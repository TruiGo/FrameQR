package com.alipay.android.appHall.component;

import java.util.HashMap;

import android.graphics.Color;
import android.text.Html;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.text.style.URLSpan;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.alipay.android.appHall.component.text.UBBPaser;
import com.alipay.android.appHall.uiengine.UIInterface;
import com.alipay.android.core.expapp.Page;

public class UIRichTextBox extends TextView implements UIInterface {

    private Page mPage;

    public UIRichTextBox(Page page) {
        super(page.getRawContext());
        this.setTextColor(Color.WHITE);
        mPage = page;
    }

    public void init(ViewGroup parent) {
        this.setLinksClickable(true);
        setMovementMethod(LinkMovementMethod.getInstance());

        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
            LayoutParams.FILL_PARENT, LayoutParams.WRAP_CONTENT);
        this.setLayoutParams(params);
        this.setTextColor(0xff000000);
        setLineSpacing(0, 1.2f);
    }

    @Override
    public String getSelectedIndex() {
        return null;
    }

    public void setVisible(boolean isVisible) {
        setVisibility(isVisible ? View.VISIBLE : View.GONE);
    }

    // true为visible，false为gone
    public boolean getVisible() {
        return getVisibility() == View.VISIBLE;
    }

    @Override
    public void setValue(Object value) {
        if(value!=null)
            set_Text(value.toString());
    }

    @Override
    public Object getValue() {
        return getText();
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
        LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) getLayoutParams();
        params.leftMargin = marginLeft;
    }

    @Override
    public int get_MarginLeft() {
        LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) getLayoutParams();
        return params.leftMargin;
    }

    @Override
    public void set_MarginRight(int marginRight) {
        LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) getLayoutParams();
        params.rightMargin = marginRight;
        
    }

    @Override
    public int get_MarginRight() {
        LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) getLayoutParams();
        return params.rightMargin;
    }

    @Override
    public void set_MarginTop(int marginTop) {
        LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) getLayoutParams();
        params.topMargin = marginTop;
    }

    @Override
    public int get_MarginTop() {
        LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) getLayoutParams();
        return params.topMargin;
    }

    @Override
    public void set_MarginBottom(int marginBottom) {
        LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) getLayoutParams();
        params.bottomMargin = marginBottom;
    }

    @Override
    public int get_MarginBottom() {
        LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) getLayoutParams();
        return params.bottomMargin;
    }

    @Override
    public void set_Width(int width) {
        LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) getLayoutParams();
        params.width = width;
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
    public String get_Tag() {
        return (String) this.getTag();
    }

    @Override
    public void set_Tag(String tag) {
        this.setTag(tag);
    }

    //-----------------------文本解析部分
    @Override
    public void set_Text(String text) {
        
        UBBPaser ubbPaser = new UBBPaser(text);
        setText(Html.fromHtml(ubbPaser.parse2Html()));

        //        ForegroundColorSpan
        if (this.getText() instanceof Spannable) {
            int end = text.length();
            Spannable sp = (Spannable) this.getText();
            SpannableStringBuilder style = new SpannableStringBuilder(sp);

            URLSpan[] urls = sp.getSpans(0, end, URLSpan.class);
            for (URLSpan url : urls) {
                if (url.getURL().startsWith("http")) {
                    continue;
                }

                if (url.getURL().startsWith("$")) {
                    ExpURLSpan expURLSpan = new ExpURLSpan(url.getURL());
                    style.removeSpan(url);
                    style.setSpan(expURLSpan, sp.getSpanStart(url), sp.getSpanEnd(url),
                        Spannable.SPAN_EXCLUSIVE_INCLUSIVE);
                }
            }
            setText(style);
        }
    }

    @Override
    public String get_Text() {
        return (String) getText();
    }


    @Override
    public LinearLayout.LayoutParams getAlipayLayoutParams() {
        return (android.widget.LinearLayout.LayoutParams) getLayoutParams();
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


    private class ExpURLSpan extends ClickableSpan {

        private String mUrl;

        ExpURLSpan(String url) {
            mUrl = url;
        }

        @Override
        public void onClick(View widget) {
            Object id = getTag()==null?"":getTag();
            mPage.interpreter("RichTextBox::"+id.toString(),mUrl);
        }
    }
}
