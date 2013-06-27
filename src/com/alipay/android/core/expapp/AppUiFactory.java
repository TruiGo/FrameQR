package com.alipay.android.core.expapp;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONObject;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import android.graphics.Color;
import android.text.method.PasswordTransformationMethod;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.LinearLayout;

import com.alipay.android.appHall.component.UIAccountBox;
import com.alipay.android.appHall.component.UIButton;
import com.alipay.android.appHall.component.UICDTimer;
import com.alipay.android.appHall.component.UICheckBox;
import com.alipay.android.appHall.component.UIComboBox;
import com.alipay.android.appHall.component.UIContactBox;
import com.alipay.android.appHall.component.UIDigitPicker;
import com.alipay.android.appHall.component.UIGroup;
import com.alipay.android.appHall.component.UIImage;
import com.alipay.android.appHall.component.UIIncrementBox;
import com.alipay.android.appHall.component.UIInputBox;
import com.alipay.android.appHall.component.UILabel;
import com.alipay.android.appHall.component.UIListView;
import com.alipay.android.appHall.component.UIMonthPicker;
import com.alipay.android.appHall.component.UIMultiEditBox;
import com.alipay.android.appHall.component.UINumPicker;
import com.alipay.android.appHall.component.UIPickerEditBox;
import com.alipay.android.appHall.component.UIProgress;
import com.alipay.android.appHall.component.UIRichTextBox;
import com.alipay.android.appHall.component.UIRow;
import com.alipay.android.appHall.component.UISafeTokenBox;
import com.alipay.android.appHall.component.UIShowBox;
import com.alipay.android.appHall.component.UISubTab;
import com.alipay.android.appHall.component.UIWebBox;
import com.alipay.android.appHall.component.incrementitem.GetMoneyDataStructure;
import com.alipay.android.appHall.uiengine.NeedSaveListener;
import com.alipay.android.appHall.uiengine.UIInterface;
import com.alipay.android.util.JsonConvert;

public class AppUiFactory implements UiFactory {

    private Page pageContext;
    private ExpAppRuntime mRuntime;

    public AppUiFactory(Page pageContext) {
        this.pageContext = pageContext;
        mRuntime = pageContext.getEngine();
    }

    @Override
    public View makeElement(Node node, ViewGroup parent) {
        View view = null;

        String strType = node.getNodeName();
        if (strType.equalsIgnoreCase(AppUiElementType.AccountBox)) {
            view = getViewforAccountBox(node, (LinearLayout) parent);
        } else if (strType.equalsIgnoreCase(AppUiElementType.Button)) {
            view = getViewforButton(node, (LinearLayout) parent);
        } else if (strType.equalsIgnoreCase(AppUiElementType.InputBox)) {
            view = getViewforInputBox(node, (LinearLayout) parent);
        } else if (strType.equalsIgnoreCase(AppUiElementType.Label)) {
            view = getViewforLabel(node, (LinearLayout) parent);
        } else if (strType.equalsIgnoreCase(AppUiElementType.MultiEditBox)) {
            view = getViewforMultiEditBox(node, (LinearLayout) parent);
        } else if (strType.equalsIgnoreCase(AppUiElementType.CheckBox)) {
            view = getViewforCheckBox(node, (LinearLayout) parent);
        } else if (strType.equalsIgnoreCase(AppUiElementType.ContactBox)) {
            view = getViewforContactBox(node, (LinearLayout) parent);
        } else if (strType.equalsIgnoreCase(AppUiElementType.Row)) {
            view = getViewforRow(node, (LinearLayout) parent);
        } else if (strType.equalsIgnoreCase(AppUiElementType.Tab)) {
            view = getViewforTab(node, (LinearLayout) parent);
        } else if (strType.equalsIgnoreCase(AppUiElementType.IncrementBox)) {
            view = getViewforIncrementBox(node, (LinearLayout) parent);
        } else if (strType.equalsIgnoreCase(AppUiElementType.List)) {
            view = getViewforList(node, (LinearLayout) parent);
        } else if (strType.equalsIgnoreCase(AppUiElementType.ComboBox)) {
            view = getViewforComboBox(node, (LinearLayout) parent);
        } else if (strType.equalsIgnoreCase(AppUiElementType.DigitPicker)) {
            view = getViewforDigitPicker(node, (LinearLayout) parent);
        } else if (strType.equalsIgnoreCase(AppUiElementType.Picker)) {
            view = getViewforPicker(node, (LinearLayout) parent);
        } else if (strType.equalsIgnoreCase(AppUiElementType.CDTimer)) {
            view = getViewforCDTimer(node, (LinearLayout) parent);
        } else if (strType.equalsIgnoreCase(AppUiElementType.RichTextBox)) {
            view = getViewforRichTextBox(node, (LinearLayout) parent);
        } else if (strType.equalsIgnoreCase(AppUiElementType.MonthPicker)) {
            view = getViewforMonthPicker(node, (LinearLayout) parent);
        } else if (strType.equalsIgnoreCase(AppUiElementType.Image)) {
            view = getViewforImage(node, (LinearLayout) parent);
        } else if (strType.equalsIgnoreCase(AppUiElementType.PickerEditBox)) {
            view = getViewforPickerEditBox(node, (LinearLayout) parent);
        } else if (strType.equalsIgnoreCase(AppUiElementType.ShowBox)) {
            view = getViewforShowBox(node, (LinearLayout) parent);
        } else if (strType.equalsIgnoreCase(AppUiElementType.Group)) {
            view = getViewforGroup(node, (LinearLayout) parent);
        } else if (strType.equalsIgnoreCase(AppUiElementType.WebBox)) {
            view = getViewforWebBox(node, (LinearLayout) parent);
        } else if (strType.equalsIgnoreCase(AppUiElementType.Progress)) {
            view = getViewforProgress(node, (LinearLayout) parent);
        } else if (strType.equalsIgnoreCase(AppUiElementType.SafeTokenBox)) {
            view = getViewforSafeTokenBox(node, (LinearLayout) parent);
        }
        return view;
    }

    private View getViewforSafeTokenBox(Node node, LinearLayout parent) {
        UISafeTokenBox safeTokenBox = new UISafeTokenBox(pageContext);
        safeTokenBox.init(parent);

        NamedNodeMap attributes = node.getAttributes();
        setCommonAttributes(safeTokenBox, attributes);
        return safeTokenBox;
    }

    private View getViewforProgress(Node node, LinearLayout parent) {
        UIProgress progress = new UIProgress(pageContext);

        NamedNodeMap attributes = node.getAttributes();
        // [#style]
        int style = 1;
        try {
            Node styleN = attributes.getNamedItem(AppUiElementType.style);
            style = Integer.parseInt(styleN.getNodeValue());
        } catch (Exception e) {
            e.printStackTrace();
        }
        progress.init(parent, style);

        // [#progressmax]
        int progressmax = 0;
        try {
            Node max = attributes.getNamedItem(AppUiElementType.ProgressMax);
            progressmax = Integer.parseInt(max.getNodeValue());
        } catch (Exception e) {
            e.printStackTrace();
        }
        progress.set_Max(progressmax);

        // [#progressrepeat]
        String progressrepeat = "";
        try {
            Node repeat = attributes.getNamedItem(AppUiElementType.ProgressRepeat);
            progressrepeat = (String) repeat.getNodeValue();
            progress.setIsDoReset(progressrepeat.equals("true") ? true : false);
        } catch (Exception e) {
            e.printStackTrace();
        }

        // [#onFinished]
        String exp = "";
        try {
            Node expression = attributes.getNamedItem(AppUiElementType.ONFINISHED);
            exp = (String) expression.getNodeValue();
            if (exp.startsWith(AppUiElementType.Reference))
                exp = mRuntime.getValue(exp.replace(AppUiElementType.Reference, ""));
            progress.setExpression(exp);
        } catch (Exception e) {
            e.printStackTrace();
        }

        setCommonAttributes(progress, attributes);

        // progress.set_Progress(0);
        //
        return progress;
    }

    private View getViewforWebBox(Node node, LinearLayout parent) {
        UIWebBox webBox = new UIWebBox(pageContext);
        //
        // Get all attributes of this node.
        NamedNodeMap attributes = node.getAttributes();

        String url = null;
        try {
            Node urlNode = attributes.getNamedItem(AppUiElementType.url);
            url = urlNode.getNodeValue();
        } catch (Exception e) {
            //			e.printStackTrace();
        }

        webBox.init(parent, url);
        setCommonAttributes(webBox, attributes);

        // [#value]
        String value = null;
        try {
            Node data = attributes.getNamedItem(AppUiElementType.Value);
            value = data.getNodeValue();
        } catch (Exception e) {
            //			e.printStackTrace();
        }
        webBox.setValue(value);

        return webBox;
    }

    private View getViewforGroup(Node node, LinearLayout parent) {
        UIGroup group = new UIGroup(pageContext);
        //
        // Get all attributes of this node.
        NamedNodeMap attributes = node.getAttributes();

        // [#style]
        int style = 0;
        boolean scroll = false;

        try {
            Node styleN = attributes.getNamedItem(AppUiElementType.style);
            style = Integer.parseInt(styleN.getNodeValue());
        } catch (Exception e) {
            e.printStackTrace();
        }

        // [#scroll]
        try {
            Node scrollN = attributes.getNamedItem(AppUiElementType.scroll);
            scroll = scrollN.getNodeValue().equalsIgnoreCase("true");
        } catch (Exception e) {
            e.printStackTrace();
        }

        group.init(parent, style, scroll);
        setCommonAttributes(group, attributes);

        //
        // Recursively make elements.
        try {
            NodeList childrens = node.getChildNodes();
            for (int i = 0; i < childrens.getLength(); i++) {
                Node childNode = childrens.item(i);

                try {
                    View child = this.makeElement(childNode, group);
                    if (child != null) {
                        group.addView(child);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return group;
    }

    private View getViewforPickerEditBox(Node node, LinearLayout parent) {
        UIPickerEditBox pickerBox = new UIPickerEditBox(pageContext);
        pickerBox.init(parent);
        // Get all attributes of this node.
        NamedNodeMap attributes = node.getAttributes();

        try {
            Node saveNumNode = attributes.getNamedItem(AppUiElementType.saveNum);
            String saveNumString = saveNumNode.getNodeValue();

            if (saveNumString.startsWith(AppUiElementType.Reference))
                saveNumString = mRuntime.getValue(saveNumString.replace(AppUiElementType.Reference,
                    ""));
            int saveNum = Integer.valueOf(saveNumString);
            pickerBox.setSaveNum(saveNum);
        } catch (Exception e) {
            e.printStackTrace();
        }

        // setdefault
        try {
            ArrayList<Object> data = null;
            Node dataNode = attributes.getNamedItem(AppUiElementType.Data);
            String value = dataNode.getNodeValue();
            if (value.startsWith(AppUiElementType.Reference))
                value = mRuntime.getValue(value.replace(AppUiElementType.Reference, ""));
            data = JsonConvert.Json2Array(new JSONArray(value));
            pickerBox.setDataFromXML(data);
        } catch (Exception e) {
            e.printStackTrace();
        }

        // [#maxCharNum]
        try {
            Node hint = attributes.getNamedItem(AppUiElementType.maxCharNum);
            String value = hint.getNodeValue();
            pickerBox.setMaxCharNum(value);
        } catch (Exception e) {
            e.printStackTrace();
        }

        setCommonAttributes(pickerBox, attributes);

        // deletable
        try {
            Node value = attributes.getNamedItem(AppUiElementType.Deletable);
            String deletable = value.getNodeValue();
            if (deletable.startsWith(AppUiElementType.Reference))
                deletable = mRuntime.getValue(deletable.replace(AppUiElementType.Reference, ""));
            pickerBox.setDeletable(deletable);
        } catch (Exception e) {
            e.printStackTrace();
        }

        // [#width]
        try {
            Node width = attributes.getNamedItem(AppUiElementType.width);
            String value = width.getNodeValue();
            pickerBox.set_Width(LayoutParamsWrapper.widthValueFromString(
                pageContext.getRawContext(), value));
        } catch (Exception e) {
            e.printStackTrace();
        }

        // [#hint]
        try {
            Node hint = attributes.getNamedItem(AppUiElementType.hint);
            String value = hint.getNodeValue();
            if (value.startsWith(AppUiElementType.Reference))
                value = mRuntime.getValue(value.replace(AppUiElementType.Reference, ""));

            pickerBox.setHint(value);
        } catch (Exception e) {
            e.printStackTrace();
        }

        if (pickerBox.getIsSave()) {
            this.pageContext.addInputCheckListener(pickerBox);
        }

        return pickerBox;
    }

    private View getViewforImage(Node node, LinearLayout parent) {
        UIImage image = new UIImage(pageContext);
        image.init(parent);
        //
        // Get all attributes of this node.
        NamedNodeMap attributes = node.getAttributes();
        setCommonAttributes(image, attributes);

        // [#width]
        try {
            Node width = attributes.getNamedItem(AppUiElementType.width);
            String value = width.getNodeValue();
            image.set_Width(LayoutParamsWrapper.widthValueFromString(pageContext.getRawContext(),
                value));
        } catch (Exception e) {
            e.printStackTrace();
        }

        // [#height]
        try {
            Node height = attributes.getNamedItem(AppUiElementType.height);
            String value = height.getNodeValue();
            image.set_Height(LayoutParamsWrapper.widthValueFromString(pageContext.getRawContext(),
                value));
        } catch (Exception e) {
            e.printStackTrace();
        }

        // [#onClick]
        // set the expression string for this image.
        try {
            Node onClick = attributes.getNamedItem(AppUiElementType.onClick);
            String value = onClick.getNodeValue();
            if (value.startsWith(AppUiElementType.Reference))
                value = mRuntime.getValue(value.replace(AppUiElementType.Reference, ""));

            image.setExpression(value);
        } catch (Exception e) {
            e.printStackTrace();
        }

        // [#image]
        try {
            Node img = attributes.getNamedItem(AppUiElementType.image);
            String value = img.getNodeValue();
            image.setData(value);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return image;
    }

    private View getViewforRichTextBox(Node node, LinearLayout parent) {
        UIRichTextBox richTextBox = new UIRichTextBox(pageContext);
        richTextBox.init(parent);
        //
        // Get all attributes of this node.
        NamedNodeMap attributes = node.getAttributes();
        setCommonAttributes(richTextBox, attributes);

        // [#fontSize]
        try {
            Node fontSize = attributes.getNamedItem(AppUiElementType.fontSize);
            String value = fontSize.getNodeValue();
            float fsize = Float.valueOf(LayoutParamsWrapper.fontSizeValueFromString(value));
            richTextBox.setTextSize(fsize);
        } catch (Exception e) {
            e.printStackTrace();
        }

        // [#fontColor]
        try {
            Node fontColor = attributes.getNamedItem(AppUiElementType.fontColor);
            String value = fontColor.getNodeValue();
            richTextBox.setTextColor(Color.parseColor(value));
        } catch (Exception e) {
            e.printStackTrace();
        }

        // [#gravity]
        try {
            Node gravity = attributes.getNamedItem(AppUiElementType.gravity);
            String value = gravity.getNodeValue();
            richTextBox.setGravity(LayoutParamsWrapper.gravityValueFromString(value));
        } catch (Exception e) {
            // e.printStackTrace();
        }

        return richTextBox;
    }

    private View getViewforMonthPicker(Node node, LinearLayout parent) {
        UIMonthPicker monthPicker = new UIMonthPicker(this.pageContext.getRawContext());
        monthPicker.init(parent);
        //
        // Get all attributes of this node.
        NamedNodeMap attributes = node.getAttributes();
        setCommonAttributes(monthPicker, attributes);

        // [#hint]
        try {
            Node hint = attributes.getNamedItem(AppUiElementType.hint);
            String value = hint.getNodeValue();
            if (value.startsWith(AppUiElementType.Reference))
                value = mRuntime.getValue(value.replace(AppUiElementType.Reference, ""));

            monthPicker.setHint(value);
        } catch (Exception e) {
            e.printStackTrace();
        }

        // [#gravity]
        try {
            Node gravity = attributes.getNamedItem(AppUiElementType.gravity);
            String value = gravity.getNodeValue();
            monthPicker.setGravity(LayoutParamsWrapper.gravityValueFromString(value));
        } catch (Exception e) {
            // e.printStackTrace();
        }

        return monthPicker;
    }

    private View getViewforShowBox(Node node, LinearLayout parent) {
        UIShowBox showBox = new UIShowBox(pageContext);
        showBox.init(parent);
        // Get all attributes of this node.
        NamedNodeMap attributes = node.getAttributes();
        setCommonAttributes(showBox, attributes);

        // [#exps]
        try {
            Node exps = attributes.getNamedItem(AppUiElementType.Exps);
            String value = exps.getNodeValue();
            if (value.startsWith(AppUiElementType.Reference))
                value = mRuntime.getValue(value.replace(AppUiElementType.Reference, ""));
            JSONArray array = new JSONArray(value);
            showBox.setItemExps(JsonConvert.Json2Array(array));
        } catch (Exception e) {
            e.printStackTrace();
        }

        // [#height]
        try {
            Node height = attributes.getNamedItem(AppUiElementType.height);
            String value = height.getNodeValue();
            showBox.set_Height(LayoutParamsWrapper.widthValueFromString(
                pageContext.getRawContext(), value));
        } catch (Exception e) {
            // e.printStackTrace();
        }

        // value
        try {
            Node nodeValue = attributes.getNamedItem(AppUiElementType.Value);
            String value = nodeValue.getNodeValue();
            if (value.startsWith(AppUiElementType.Reference))
                value = mRuntime.getValue(value.replace(AppUiElementType.Reference, ""));
            JSONArray array = new JSONArray(value);
            showBox.setValue(JsonConvert.Json2Array(array));

        } catch (Exception e) {
            e.printStackTrace();
        }

        return showBox;
    }

    private View getViewforCDTimer(Node node, LinearLayout parent) {
        UICDTimer cdTimer = new UICDTimer(this.pageContext.getRawContext(), pageContext);
        //
        // Get all attributes of this node.
        NamedNodeMap attributes = node.getAttributes();

        // [#hint]
        try {
            Node hint = attributes.getNamedItem(AppUiElementType.hint);
            String value = hint.getNodeValue();
            if (value.startsWith(AppUiElementType.Reference))
                value = mRuntime.getValue(value.replace(AppUiElementType.Reference, ""));

            cdTimer.init(parent, value);
        } catch (Exception e) {
            e.printStackTrace();
        }

        setCommonAttributes(cdTimer, attributes);

        // [#time]
        try {
            Node hint = attributes.getNamedItem(AppUiElementType.time);
            if (hint != null) {
                String value = hint.getNodeValue();
                if (value.startsWith(AppUiElementType.Reference))
                    value = mRuntime.getValue(value.replace(AppUiElementType.Reference, ""));

                if (isExpression(value)) {
                    cdTimer.set_Tag(AppUiElementType.key_for_text, value);
                    this.pageContext.add2delayGetValueGroup(cdTimer);
                }
                cdTimer.setValue(value);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        // [#fontSize]
        try {
            Node fontSize = attributes.getNamedItem(AppUiElementType.fontSize);
            String value = fontSize.getNodeValue();
            float fsize = Float.valueOf(LayoutParamsWrapper.fontSizeValueFromString(value));
            cdTimer.setTextSize(fsize);
        } catch (Exception e) {
            e.printStackTrace();
        }

        // [#fontColor]
        try {
            Node fontColor = attributes.getNamedItem(AppUiElementType.fontColor);
            String value = fontColor.getNodeValue();
            cdTimer.setTextColor(Color.parseColor(value));
        } catch (Exception e) {
            e.printStackTrace();
        }

        try {
            // onFinished
            Node onFinished = attributes.getNamedItem(AppUiElementType.onFinished);
            String value = onFinished.getNodeValue();
            if (value.startsWith(AppUiElementType.Reference))
                value = mRuntime.getValue(value.replace(AppUiElementType.Reference, ""));
            cdTimer.setExpression(value);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return cdTimer;
    }

    private View getViewforPicker(Node node, LinearLayout parent) {
        UINumPicker numPicker = new UINumPicker(this.pageContext.getRawContext());
        //
        // Get all attributes of this node.
        NamedNodeMap attributes = node.getAttributes();
        // [#alt]
        try {
            Node hint = attributes.getNamedItem(AppUiElementType.Data);
            String alt = hint.getNodeValue();
            if (alt.startsWith(AppUiElementType.Reference))
                alt = mRuntime.getValue(alt.replace(AppUiElementType.Reference, ""));

            hint = attributes.getNamedItem(AppUiElementType.title);
            String title = hint.getNodeValue();
            if (title.startsWith(AppUiElementType.Reference))
                title = mRuntime.getValue(title.replace(AppUiElementType.Reference, ""));

            numPicker.init(parent, alt, title);
        } catch (Exception e) {
            e.printStackTrace();
        }

        setCommonAttributes(numPicker, attributes);

        // [#text]
        try {
            Node hint = attributes.getNamedItem(AppUiElementType.text);
            String text = hint.getNodeValue();
            if (text.startsWith(AppUiElementType.Reference))
                text = mRuntime.getValue(text.replace(AppUiElementType.Reference, ""));
            numPicker.setText(text);
        } catch (Exception e) {
            e.printStackTrace();
        }

        // set the expression string for this UINumPicker.
        try {
            Node onClick = attributes.getNamedItem(AppUiElementType.onItemSelected);
            String value = onClick.getNodeValue();
            if (value.startsWith(AppUiElementType.Reference))
                value = mRuntime.getValue(value.replace(AppUiElementType.Reference, ""));

            numPicker.setExpression(value);
        } catch (Exception e) {
            e.printStackTrace();
        }

        numPicker.setListener(this.pageContext.getOnClickListener());
        return numPicker;
    }

    private View getViewforDigitPicker(Node node, LinearLayout parent) {
        UIDigitPicker digitPicker = new UIDigitPicker(this.pageContext);
        //
        // Get all attributes of this node.
        NamedNodeMap attributes = node.getAttributes();

        // [#min,max...]
        int type = 0;
        String alt = null;
        String min = null;
        String max = null;
        String num = null;
        String dis = "false";
        try {
            // type
            Node hint = attributes.getNamedItem(AppUiElementType.type);
            type = Integer.parseInt(hint.getNodeValue());
        } catch (Exception e) {
            e.printStackTrace();
        }

        try {
            // alt
            Node hint = attributes.getNamedItem(AppUiElementType.Alternatives);
            alt = hint.getNodeValue();
            if (alt.startsWith(AppUiElementType.Reference))
                alt = mRuntime.getValue(alt.replace(AppUiElementType.Reference, ""));
        } catch (Exception e) {
            e.printStackTrace();
        }

        try {
            // min
            Node hint = attributes.getNamedItem(AppUiElementType.MinNum);
            min = hint.getNodeValue();
            if (min.startsWith(AppUiElementType.Reference))
                min = mRuntime.getValue(min.replace(AppUiElementType.Reference, ""));
        } catch (Exception e) {
            e.printStackTrace();
        }

        try {
            // max
            Node hint = attributes.getNamedItem(AppUiElementType.MaxNum);
            max = hint.getNodeValue();
            if (max.startsWith(AppUiElementType.Reference))
                max = mRuntime.getValue(max.replace(AppUiElementType.Reference, ""));
        } catch (Exception e) {
            e.printStackTrace();
        }

        try {
            // Num
            Node hint = attributes.getNamedItem(AppUiElementType.PerNum);
            num = hint.getNodeValue();
            if (num.startsWith(AppUiElementType.Reference))
                num = mRuntime.getValue(num.replace(AppUiElementType.Reference, ""));
        } catch (Exception e) {
            e.printStackTrace();
        }

        try {
            // dis
            Node hint = attributes.getNamedItem(AppUiElementType.Distinguish);
            dis = hint.getNodeValue();
            if (dis.startsWith(AppUiElementType.Reference))
                dis = mRuntime.getValue(dis.replace(AppUiElementType.Reference, ""));
        } catch (Exception e) {
            e.printStackTrace();
        }

        digitPicker.init(parent, type, alt, min, max, num, dis);

        setCommonAttributes(digitPicker, attributes);
        this.pageContext.addInputCheckListener(digitPicker);

        try {
            // [#desc] ????
            Node hint = attributes.getNamedItem(AppUiElementType.desc);
            String value = hint.getNodeValue();
            if (value.startsWith(AppUiElementType.Reference))
                value = mRuntime.getValue(value.replace(AppUiElementType.Reference, ""));
            digitPicker.setDesc(value);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return digitPicker;
    }

    private View getViewforComboBox(Node node, LinearLayout parent) {
        UIComboBox comboBox = new UIComboBox(pageContext);
        comboBox.init(parent);
        //
        // Get all attributes of this node.
        NamedNodeMap attributes = node.getAttributes();
        setCommonAttributes(comboBox, attributes);

        try {
            // hint
            Node hintNode = attributes.getNamedItem(AppUiElementType.hint);
            String hint = null;
            hint = hintNode.getNodeValue();
            if (hint.startsWith(AppUiElementType.Reference))
                hint = mRuntime.getValue(hint.replace(AppUiElementType.Reference, ""));
            comboBox.set_Hint(hint);
        } catch (Exception e) {
            e.printStackTrace();
        }

        try {
            // adapter
            Node adapterStr = attributes.getNamedItem(AppUiElementType.adapter);
            String adapter = null;
            adapter = adapterStr.getNodeValue();
            if (adapter.startsWith(AppUiElementType.Reference))
                adapter = mRuntime.getValue(adapter.replace(AppUiElementType.Reference, ""));
            comboBox.setDataAdapter(adapter);
        } catch (Exception e) {
            e.printStackTrace();
        }

        // [#type & adapter & value]
        Node hint = null;
        ArrayList<Object> data = null;

        try {
            // data
            hint = attributes.getNamedItem(AppUiElementType.Data);
            String value = hint.getNodeValue();
            if (value.startsWith(AppUiElementType.Reference))
                value = mRuntime.getValue(value.replace(AppUiElementType.Reference, ""));

            // Add this element to the delayGetValueGroup of the page.
            if (isExpression(value)) {
                comboBox.set_Tag(AppUiElementType.key_for_text, value);
                this.pageContext.add2delayGetValueGroup(comboBox);
            } else {
                data = JsonConvert.Json2Array(new JSONArray(value));
                comboBox.setData(data);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        try {
            // itemexps
            hint = attributes.getNamedItem(AppUiElementType.Exps);
            String value = hint.getNodeValue();
            if (value.startsWith(AppUiElementType.Reference))
                value = mRuntime.getValue(value.replace(AppUiElementType.Reference, ""));

            data = JsonConvert.Json2Array(new JSONArray(value));
            comboBox.setItemExps(data);
        } catch (Exception e) {
            e.printStackTrace();
        }

        try {
            // default value
            hint = attributes.getNamedItem(AppUiElementType.Value);
            String value = hint.getNodeValue();
            if (value.startsWith(AppUiElementType.Reference))
                value = mRuntime.getValue(value.replace(AppUiElementType.Reference, ""));

            if (isExpression(value)) {
                comboBox.set_Tag(AppUiElementType.key_for_comboBox_value, value);
            } else {
                comboBox.setValue(value);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        // [#onItemSelected]
        try {
            hint = attributes.getNamedItem(AppUiElementType.onItemSelected);
            String value = hint.getNodeValue();
            if (value.startsWith(AppUiElementType.Reference))
                value = mRuntime.getValue(value.replace(AppUiElementType.Reference, ""));

            comboBox.setExpression(value);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return comboBox;
    }

    private View getViewforList(Node node, LinearLayout parent) {
        UIListView listView = new UIListView(this.pageContext.getRawContext(), pageContext);
        //
        // Get all attributes of this node.
        NamedNodeMap attributes = node.getAttributes();

        // [#type & adapter & value]
        Node hint = null;
        int type = -1;
        String adapter = null;
        ArrayList<Object> data = null;

        try {
            // type
            hint = attributes.getNamedItem(AppUiElementType.type);
            type = Integer.parseInt(hint.getNodeValue());
        } catch (Exception e) {
            e.printStackTrace();
        }

        listView.init(parent, type);
        setCommonAttributes(listView, attributes);

        try {
            // adapter
            hint = attributes.getNamedItem(AppUiElementType.adapter);
            if (hint != null) {
                adapter = hint.getNodeValue();
                if (adapter != null && adapter.startsWith(AppUiElementType.Reference))
                    adapter = mRuntime.getValue(adapter.replace(AppUiElementType.Reference, ""));
                listView.setDataAdapter(adapter);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        try {
            // data
            hint = attributes.getNamedItem(AppUiElementType.Data);
            if (hint != null) {
                String value = hint.getNodeValue();
                if (value != null && value.startsWith(AppUiElementType.Reference))
                    value = mRuntime.getValue(value.replace(AppUiElementType.Reference, ""));

                // Add this element to the delayGetValueGroup of the page.
                if (isExpression(value)) {
                    listView.set_Tag(AppUiElementType.key_for_text, value);
                    this.pageContext.add2delayGetValueGroup(listView);
                } else {
                    data = JsonConvert.Json2Array(new JSONArray(value));
                    listView.addData(data);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        try {
            // emptyMsg
            hint = attributes.getNamedItem(AppUiElementType.emptyMsg);
            if (hint != null) {
                String value = hint.getNodeValue();
                if (value != null && value.startsWith(AppUiElementType.Reference))
                    value = mRuntime.getValue(value.replace(AppUiElementType.Reference, ""));

                listView.setEmptyContent(value);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        try {
            // itemexps
            hint = attributes.getNamedItem(AppUiElementType.Exps);
            if (hint != null) {
                String value = hint.getNodeValue();
                if (value != null && value.startsWith(AppUiElementType.Reference))
                    value = mRuntime.getValue(value.replace(AppUiElementType.Reference, ""));

                data = JsonConvert.Json2Array(new JSONArray(value));
                listView.setItemExps(data);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        try {
            // value
            hint = attributes.getNamedItem(AppUiElementType.Value);
            if (hint != null) {
                String value = hint.getNodeValue();
                if (value != null && value.startsWith(AppUiElementType.Reference))
                    value = mRuntime.getValue(value.replace(AppUiElementType.Reference, ""));

                if (isExpression(value)) {
                    listView.set_Tag(AppUiElementType.key_for_comboBox_value, value);
                } else {
                    listView.setValue(value);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        try {
            // onItemClick
            hint = attributes.getNamedItem(AppUiElementType.onItemSelected);
            if (hint != null) {
                adapter = hint.getNodeValue();
                if (adapter != null && adapter.startsWith(AppUiElementType.Reference))
                    adapter = mRuntime.getValue(adapter.replace(AppUiElementType.Reference, ""));
                listView.setExpression(adapter);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        try {
            // deletable
            hint = attributes.getNamedItem(AppUiElementType.deletable);
            if (hint != null) {
                adapter = hint.getNodeValue();
                if (adapter != null && adapter.startsWith(AppUiElementType.Reference))
                    adapter = mRuntime.getValue(adapter.replace(AppUiElementType.Reference, ""));
                listView.setDeletable(adapter);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        try {
            // delExp
            hint = attributes.getNamedItem(AppUiElementType.delExp);
            if (hint != null) {
                adapter = hint.getNodeValue();
                if (adapter.startsWith(AppUiElementType.Reference))
                    adapter = mRuntime.getValue(adapter.replace(AppUiElementType.Reference, ""));
                listView.setDelExp(adapter);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        try {
            // onMoreRequested
            Node str = attributes.getNamedItem(AppUiElementType.onMoreRequested);
            if (str != null) {
                String addMoreExpression = str.getNodeValue();
                if (addMoreExpression != null
                    && addMoreExpression.startsWith(AppUiElementType.Reference))
                    addMoreExpression = mRuntime.getValue(addMoreExpression.replace(
                        AppUiElementType.Reference, ""));
                listView.setAddMoreExpression(addMoreExpression);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return listView;
    }

    private View getViewforIncrementBox(Node node, LinearLayout parent) {
        UIIncrementBox incrementBox = new UIIncrementBox(pageContext);
        //
        // Get all attributes of this node.
        NamedNodeMap attributes = node.getAttributes();

        // [#type & value]
        try {
            // type
            Node hint = attributes.getNamedItem(AppUiElementType.type);
            int type = Integer.parseInt(hint.getNodeValue());

            hint = attributes.getNamedItem(AppUiElementType.Value);
            String value = hint.getNodeValue();
            if (value.startsWith(AppUiElementType.Reference))
                value = mRuntime.getValue(value.replace(AppUiElementType.Reference, ""));

            JSONObject jsonArray = new JSONObject(value);
            String exp = (String) jsonArray.opt(AppUiElementType.IS_CONTAIN_SELF);
            boolean isContainSelf = ((String) pageContext.interpreter("IncrementBox-isContainSelf",
                exp)).equals("true");
            exp = (String) jsonArray.opt(AppUiElementType.GET_MONEYPEOPLE_COUNT);
            int getMoneypeopleCount = Integer.parseInt((String) pageContext.interpreter(
                "IncrementBox-getMoneypeopleCount", exp));
            exp = (String) jsonArray.opt(AppUiElementType.GET_MONEY_TOTAL_COUNT);
            double getMoneyTotalCount = Double.parseDouble((String) pageContext.interpreter(
                "IncrementBox-getMoneyTotalCount", exp));
            GetMoneyDataStructure dataStructure = new GetMoneyDataStructure(isContainSelf,
                getMoneypeopleCount, getMoneyTotalCount);

            incrementBox.init(parent, type, dataStructure);
        } catch (Exception e) {
            e.printStackTrace();
        }
        setCommonAttributes(incrementBox, attributes);

        pageContext.addInputCheckListener(incrementBox);
        //		incrementBox.addTextChangedListener(this.pageContext.getInputBoxIsNullListener());

        return incrementBox;
    }

    private View getViewforTab(Node node, LinearLayout parent) {
        UISubTab subTab = new UISubTab(this.pageContext);
        //
        // Get all attributes of this node.
        NamedNodeMap attributes = node.getAttributes();

        // [#value]
        try {
            Node hint = attributes.getNamedItem(AppUiElementType.Value);
            String value = hint.getNodeValue();
            if (value.startsWith(AppUiElementType.Reference))
                value = mRuntime.getValue(value.replace(AppUiElementType.Reference, ""));

            JSONArray jsonArray = new JSONArray(value);
            JSONObject one = null;
            String[] icons = new String[jsonArray.length()];
            String[] names = new String[jsonArray.length()];
            Page[] pages = new Page[jsonArray.length()];
            for (int i = 0; i < jsonArray.length(); ++i) {
                one = jsonArray.optJSONObject(i);
                icons[i] = (String) one.opt("icon");
                names[i] = (String) one.opt("text");

                if (names[i].startsWith(AppUiElementType.Reference))
                    names[i] = mRuntime.getValue(names[i].replace(AppUiElementType.Reference, ""));

                pages[i] = mRuntime.getAppHandler().createPage((String) one.opt("url"));
            }

            subTab.init(parent, names, pages);
        } catch (Exception e) {
            e.printStackTrace();
        }
        setCommonAttributes(subTab, attributes);

        return subTab;
    }

    private View getViewforRow(Node node, LinearLayout parent) {
        UIRow row = new UIRow(this.pageContext.getRawContext());
        // LinearLayout row = (LinearLayout)
        // LayoutInflater.from(this.pageContext.getRawContext())
        // .inflate(R.layout.row, parent, false);

        row.init(parent);
        //
        // Get all attributes of this node.
        NamedNodeMap attributes = node.getAttributes();
        setCommonAttributes(row, attributes);

        int grav = Gravity.LEFT;
        // [#gravity]
        try {
            Node gravity = attributes.getNamedItem(AppUiElementType.gravity);
            String value = gravity.getNodeValue();
            grav = LayoutParamsWrapper.gravityValueFromString(value);
            row.setGravity(grav);
        } catch (Exception e) {
            // e.printStackTrace();
        }
        //
        // Recursively make elements.
        try {
            NodeList childrens = node.getChildNodes();
            for (int i = 0; i < childrens.getLength(); i++) {
                Node childNode = childrens.item(i);

                try {
                    View child = this.makeElement(childNode, row);
                    if (child != null) {
                        LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) ((UIInterface) child)
                            .getAlipayLayoutParams();
                        // params.gravity = grav;
                        if (params.width == LayoutParams.FILL_PARENT)
                            params.weight = 1;
                        row.addView(child, params);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return row;
    }

    private View getViewforContactBox(Node node, LinearLayout parent) {
        UIContactBox contactBox = new UIContactBox(this.pageContext);
        contactBox.init(parent);

        // Get all attributes of this node.
        NamedNodeMap attributes = node.getAttributes();
        setCommonAttributes(contactBox, attributes);

        // [#width]
        try {
            Node width = attributes.getNamedItem(AppUiElementType.width);
            String value = width.getNodeValue();
            contactBox.set_Width(LayoutParamsWrapper.widthValueFromString(
                pageContext.getRawContext(), value));
        } catch (Exception e) {
            e.printStackTrace();
        }

        // [#hint]
        try {
            Node hint = attributes.getNamedItem(AppUiElementType.hint);
            String value = hint.getNodeValue();
            if (value.startsWith(AppUiElementType.Reference))
                value = mRuntime.getValue(value.replace(AppUiElementType.Reference, ""));

            contactBox.setHint(value);
        } catch (Exception e) {
            e.printStackTrace();
        }

        this.pageContext.addInputCheckListener(contactBox);

        // [#value]
        // default value
        try {
            Node valueNode = attributes.getNamedItem(AppUiElementType.Value);
            String value = valueNode.getNodeValue();
            if (value.startsWith(AppUiElementType.Reference))
                value = mRuntime.getValue(value.replace(AppUiElementType.Reference, ""));

            contactBox.setValue(value);
        } catch (Exception e) {
            e.printStackTrace();
        }

        contactBox.getEditContent().addTextChangedListener(
            this.pageContext.getInputBoxIsNullListener());
        if (contactBox.getEnable())
            this.pageContext.getInputBoxIsNullListener().addNeedCheckView(contactBox);

        return contactBox;
    }

    private View getViewforCheckBox(Node node, LinearLayout parent) {
        UICheckBox checkBox = new UICheckBox(pageContext);
        checkBox.init(parent);

        //
        // Get all attributes of this node.
        NamedNodeMap attributes = node.getAttributes();
        setCommonAttributes(checkBox, attributes);

        //
        // [#text]
        String text = prepareForText(attributes, checkBox);
        if (text != null) {
            checkBox.setText(text);
        }

        // [#fontSize]
        try {
            Node fontSize = attributes.getNamedItem(AppUiElementType.fontSize);
            String value = fontSize.getNodeValue();
            checkBox.setTextSize(LayoutParamsWrapper.fontSizeValueFromString(value));
        } catch (Exception e) {
            e.printStackTrace();
        }

        // [#fontColor]
        try {
            Node fontColor = attributes.getNamedItem(AppUiElementType.fontColor);
            String value = fontColor.getNodeValue();
            checkBox.setTextColor(Color.parseColor(value));
        } catch (Exception e) {
            e.printStackTrace();
        }

        // [#width]
        try {
            Node width = attributes.getNamedItem(AppUiElementType.width);
            String value = width.getNodeValue();
            checkBox.set_Width(LayoutParamsWrapper.widthValueFromString(
                pageContext.getRawContext(), value));
        } catch (Exception e) {
            e.printStackTrace();
        }

        // [#isChecked]
        try {
            Node checked = attributes.getNamedItem(AppUiElementType.checked);
            String value = checked.getNodeValue();
            checkBox.setChecked(value.equalsIgnoreCase(AppUiElementType.TRUE));
        } catch (Exception e) {
            e.printStackTrace();
        }

        // [#onCheckedChange]
        // set the expression string for this view.
        try {
            Node onCheckedChange = attributes.getNamedItem(AppUiElementType.onCheckedChange);
            String value = onCheckedChange.getNodeValue();
            if (value.startsWith(AppUiElementType.Reference))
                value = mRuntime.getValue(value.replace(AppUiElementType.Reference, ""));

            checkBox.setExpression(value);
        } catch (Exception e) {
            e.printStackTrace();
        }

        checkBox.setOnCheckedChangeListener(this.pageContext.getOnCheckedChangeListener());

        return checkBox;
    }

    private View getViewforMultiEditBox(Node node, LinearLayout parent) {
        UIMultiEditBox multiEditBox = new UIMultiEditBox(pageContext);
        multiEditBox.init(parent);

        //
        // Get all attributes of this node.
        NamedNodeMap attributes = node.getAttributes();
        setCommonAttributes(multiEditBox, attributes);
        this.pageContext.addInputCheckListener(multiEditBox);

        //
        // [#text]
        String text = prepareForText(attributes, multiEditBox);
        if (text != null) {
            multiEditBox.setText(text);
        }

        // [#minCharNum]
        try {
            Node hint = attributes.getNamedItem(AppUiElementType.minCharNum);
            String value = hint.getNodeValue();
            multiEditBox.setMinCharNum(value);
        } catch (Exception e) {
            e.printStackTrace();
        }

        // [#maxCharNum]
        try {
            Node hint = attributes.getNamedItem(AppUiElementType.maxCharNum);
            String value = hint.getNodeValue();
            multiEditBox.setMaxCharNum(value);
        } catch (Exception e) {
            e.printStackTrace();
        }

        // [#regex]
        try {
            Node disable = attributes.getNamedItem(AppUiElementType.regex);
            String value = disable.getNodeValue();
            if (value.startsWith(AppUiElementType.Reference))
                value = mRuntime.getValue(value.replace(AppUiElementType.Reference, ""));
            multiEditBox.setRegex(value);
        } catch (Exception e) {
            e.printStackTrace();
        }

        // [#hint]
        try {
            Node hint = attributes.getNamedItem(AppUiElementType.hint);
            String value = hint.getNodeValue();
            if (value.startsWith(AppUiElementType.Reference))
                value = mRuntime.getValue(value.replace(AppUiElementType.Reference, ""));

            multiEditBox.setHint(value);
        } catch (Exception e) {
            e.printStackTrace();
        }

        // [#isSave]
        boolean isSave = prepareForIsSave(attributes);
        multiEditBox.setSaveEnabled(isSave);

        // [#width]
        try {
            Node width = attributes.getNamedItem(AppUiElementType.width);
            String value = width.getNodeValue();
            multiEditBox.setWidth(LayoutParamsWrapper.widthValueFromString(
                pageContext.getRawContext(), value));
        } catch (Exception e) {
            e.printStackTrace();
        }

        // [#emptydesc]
        try {
            Node emptyDesc = attributes.getNamedItem(AppUiElementType.emptyDesc);
            String value = emptyDesc.getNodeValue();
            if (value.startsWith(AppUiElementType.Reference))
                value = mRuntime.getValue(value.replace(AppUiElementType.Reference, ""));
            multiEditBox.setEmptyDesc(value);
        } catch (Exception e) {
            e.printStackTrace();
        }

        // [#desc]
        try {
            Node desc = attributes.getNamedItem(AppUiElementType.desc);
            String value = desc.getNodeValue();
            if (value.startsWith(AppUiElementType.Reference))
                value = mRuntime.getValue(value.replace(AppUiElementType.Reference, ""));
            multiEditBox.setDesc(value);
        } catch (Exception e) {
            e.printStackTrace();
        }

        multiEditBox.addTextChangedListener(this.pageContext.getInputBoxIsNullListener());
        if (multiEditBox.getMinCharNum() != 0 && multiEditBox.getEnable()) {
            this.pageContext.getInputBoxIsNullListener().addNeedCheckView(multiEditBox);
        }

        return multiEditBox;
    }

    private boolean prepareForIsSave(NamedNodeMap attributes) {
        boolean isSave = false;
        try {
            Node isSaveNode = attributes.getNamedItem(AppUiElementType.isSave);
            String value = isSaveNode.getNodeValue();
            if (value.startsWith(AppUiElementType.Reference))
                value = mRuntime.getValue(value.replace(AppUiElementType.Reference, ""));

            isSave = value.equalsIgnoreCase(AppUiElementType.TRUE);
        } catch (Exception e) {
            // e.printStackTrace();
        }

        return isSave;
    }

    private View getViewforLabel(Node node, LinearLayout parent) {
        UILabel label = new UILabel(this.pageContext.getRawContext());
        label.init((ViewGroup) parent);

        //
        // Get all attributes of this node.
        NamedNodeMap attributes = node.getAttributes();
        setCommonAttributes(label, attributes);

        // [#fontSize]
        try {
            Node fontSize = attributes.getNamedItem(AppUiElementType.fontSize);
            String value = fontSize.getNodeValue();
            float fsize = Float.valueOf(LayoutParamsWrapper.fontSizeValueFromString(value));
            label.setTextSize(fsize);
        } catch (Exception e) {
            e.printStackTrace();
        }

        // [#fontColor]
        try {
            Node fontColor = attributes.getNamedItem(AppUiElementType.fontColor);
            String value = fontColor.getNodeValue();
            label.setTextColor(Color.parseColor(value));
        } catch (Exception e) {
            e.printStackTrace();
        }

        // [#lines]
        try {
            Node linesValue = attributes.getNamedItem(AppUiElementType.lines);
            if (linesValue != null) {
                String lines = linesValue.getNodeValue();
                label.set_Lines(Integer.valueOf(lines));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        // [#gravity]
        try {
            Node gravity = attributes.getNamedItem(AppUiElementType.gravity);
            String value = gravity.getNodeValue();
            label.setGravity(LayoutParamsWrapper.gravityValueFromString(value));
        } catch (Exception e) {
            // e.printStackTrace();
        }

        return label;
    }

    private View getViewforInputBox(Node node, LinearLayout parent) {
        UIInputBox inputBox = new UIInputBox(pageContext);
        inputBox.init(parent);

        //
        // Get all attributes of this node.
        NamedNodeMap attributes = node.getAttributes();
        setCommonAttributes(inputBox, attributes);

        // [#type]
        try {
            Node type = attributes.getNamedItem(AppUiElementType.type);
            String value = type.getNodeValue();
            if (value.startsWith(AppUiElementType.Reference))
                value = mRuntime.getValue(value.replace(AppUiElementType.Reference, ""));
            if (value != null && !"".equals(value))
                inputBox.getEditText().setInputType(InputTypeWrapper.inputTypeFromString(value));
            if (value.equals("password")) {
                inputBox.getEditText().setTransformationMethod(
                    PasswordTransformationMethod.getInstance());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        // [#maxCharNum]
        try {
            Node hint = attributes.getNamedItem(AppUiElementType.maxCharNum);
            String value = hint.getNodeValue();
            inputBox.setMaxCharNum(value);
        } catch (Exception e) {
            e.printStackTrace();
        }
        // [#minCharNum]
        try {
            Node hint = attributes.getNamedItem(AppUiElementType.minCharNum);
            String value = hint.getNodeValue();
            inputBox.setMinCharNum(value);
        } catch (Exception e) {
            e.printStackTrace();
        }

        // [#hint]
        try {
            Node hint = attributes.getNamedItem(AppUiElementType.hint);
            String value = hint.getNodeValue();
            if (value.startsWith(AppUiElementType.Reference))
                value = mRuntime.getValue(value.replace(AppUiElementType.Reference, ""));

            inputBox.getEditText().setHint(value);
        } catch (Exception e) {
            e.printStackTrace();
        }

        // [#emptydesc]
        try {
            Node emptyDesc = attributes.getNamedItem(AppUiElementType.emptyDesc);
            String value = emptyDesc.getNodeValue();
            if (value.startsWith(AppUiElementType.Reference))
                value = mRuntime.getValue(value.replace(AppUiElementType.Reference, ""));
            inputBox.setEmptyDesc(value);
        } catch (Exception e) {
            e.printStackTrace();
        }

        // [#desc]
        try {
            Node desc = attributes.getNamedItem(AppUiElementType.desc);
            String value = desc.getNodeValue();
            if (value.startsWith(AppUiElementType.Reference))
                value = mRuntime.getValue(value.replace(AppUiElementType.Reference, ""));
            inputBox.setDesc(value);
        } catch (Exception e) {
            e.printStackTrace();
        }

        // [#regex]
        try {
            Node disable = attributes.getNamedItem(AppUiElementType.regex);
            String value = disable.getNodeValue();
            if (value.startsWith(AppUiElementType.Reference))
                value = mRuntime.getValue(value.replace(AppUiElementType.Reference, ""));
            inputBox.setRegex(value);
        } catch (Exception e) {
            e.printStackTrace();
        }

        if (inputBox.getRegex() != null && inputBox.getRegex() != "") {
            this.pageContext.addInputCheckListener(inputBox);
        }

        // [#inputName]
        try {
            Node inputName = attributes.getNamedItem(AppUiElementType.inputName);
            String value = inputName.getNodeValue();
            if (value.startsWith(AppUiElementType.Reference))
                value = mRuntime.getValue(value.replace(AppUiElementType.Reference, ""));
            inputBox.setInputName(value);
        } catch (Exception e) {
            e.printStackTrace();
        }

        // [#unitVisiable]
        try {
            Node unitVisiable = attributes.getNamedItem(AppUiElementType.unit);
            String value = unitVisiable.getNodeValue();
            if (value.startsWith(AppUiElementType.Reference))
                value = mRuntime.getValue(value.replace(AppUiElementType.Reference, ""));
            inputBox.setUnit(value);
        } catch (Exception e) {
            e.printStackTrace();
        }

        inputBox.getEditText().addTextChangedListener(this.pageContext.getInputBoxIsNullListener());
        if (inputBox.getMinCharNum() != 0 && inputBox.getEnable()) {
            this.pageContext.getInputBoxIsNullListener().addNeedCheckView(inputBox);
        }

        return inputBox;
    }

    private String prepareForText(NamedNodeMap attributes, UIInterface view) {
        String refinedText = null;

        //
        // [#text] Set the text string for this view.It may be come form raw
        // text, indirect text, direct expression
        // or direct expression.
        try {
            Node text = attributes.getNamedItem(AppUiElementType.text);
            String value = text.getNodeValue();
            if (value.startsWith(AppUiElementType.Reference))
                value = mRuntime.getValue(value.replace(AppUiElementType.Reference, ""));

            // Add this element to the delayGetValueGroup of the page.
            if (isExpression(value)) {
                view.set_Tag(AppUiElementType.key_for_text, value);
                this.pageContext.add2delayGetValueGroup(view);
            } else {
                refinedText = value;
            }
        } catch (Exception e) {
            // e.printStackTrace();
        }

        return refinedText;
    }

    private void setCommonAttributes(UIInterface viewInterface, NamedNodeMap attributes) {
        //
        // [#id]
        // set the id(use as Tag) for this view.
        try {
            Node id = attributes.getNamedItem(AppUiElementType.id);
            String value = id.getNodeValue();
            if (value.startsWith(AppUiElementType.Reference))
                value = mRuntime.getValue(value.replace(AppUiElementType.Reference, ""));

            viewInterface.set_Tag(value);
        } catch (Exception e) {
            e.printStackTrace();
        }

        //
        // [#text]
        String text = prepareForText(attributes, viewInterface);
        if (text != null) {
            viewInterface.setValue(text);
        }

        //
        // [#isVisible]
        try {
            Node visible = attributes.getNamedItem(AppUiElementType.visible);
            String value = visible.getNodeValue();
            viewInterface.setVisible(value.equalsIgnoreCase(AppUiElementType.TRUE));
        } catch (Exception e) {
            // e.printStackTrace();
        }

        //
        // [#isSave]
        try {
            boolean isSave = prepareForIsSave(attributes);
            viewInterface.setIsSave(isSave);
            if (isSave) {
                this.pageContext.addNeedSaveListener((NeedSaveListener) viewInterface);
            }
        } catch (Exception e) {
            // TODO: handle exception
        }

        //
        // [#width]
        try {
            Node width = attributes.getNamedItem(AppUiElementType.width);
            if (width != null) {
                String value = width.getNodeValue();
                viewInterface.set_Width(LayoutParamsWrapper.widthValueFromString(
                    pageContext.getRawContext(), value));
            }
        } catch (Exception e) {
            // e.printStackTrace();
        }

        //
        // [#marginLeft]
        try {
            Node marginLeft = attributes.getNamedItem(AppUiElementType.marginLeft);
            if (marginLeft != null) {
                String value = marginLeft.getNodeValue();
                viewInterface.set_MarginLeft(Integer.valueOf(LayoutParamsWrapper
                    .widthValueFromString(pageContext.getRawContext(), value)));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        //
        // [#marginRight]
        try {
            Node marginRight = attributes.getNamedItem(AppUiElementType.marginRight);
            String value = marginRight.getNodeValue();
            viewInterface.set_MarginRight(Integer.valueOf(LayoutParamsWrapper.widthValueFromString(
                pageContext.getRawContext(), value)));
        } catch (Exception e) {
            // e.printStackTrace();
        }

        //
        // [#marginTop]
        try {
            Node marginTop = attributes.getNamedItem(AppUiElementType.marginTop);
            String value = marginTop.getNodeValue();
            viewInterface.set_MarginTop(Integer.valueOf(LayoutParamsWrapper.widthValueFromString(
                pageContext.getRawContext(), value)));
        } catch (Exception e) {
            // e.printStackTrace();
        }

        //
        // [#marginBottom] ????
        try {
            Node marginBottom = attributes.getNamedItem(AppUiElementType.marginBottom);
            String value = marginBottom.getNodeValue();
            viewInterface.set_MarginBottom(Integer.valueOf(LayoutParamsWrapper
                .widthValueFromString(pageContext.getRawContext(), value)));
        } catch (Exception e) {
            // e.printStackTrace();
        }

        //
        // [#enable]
        try {
            Node enable = attributes.getNamedItem(AppUiElementType.enable);
            String value = enable.getNodeValue();
            viewInterface.setEnable(value.equalsIgnoreCase(AppUiElementType.TRUE) ? true : false);
        } catch (Exception e) {
            // e.printStackTrace();
        }
    }

    private View getViewforAccountBox(Node node, LinearLayout parent) {
        UIAccountBox accountBox = new UIAccountBox(this.pageContext);
        accountBox.init(parent);

        // Get all attributes of this node.
        NamedNodeMap attributes = node.getAttributes();
        setCommonAttributes(accountBox, attributes);

        this.pageContext.addInputCheckListener(accountBox);
        this.pageContext.addSendLogListener(accountBox);

        // [#useQR]
        try {
            Node QR = attributes.getNamedItem(AppUiElementType.useQR);
            String useQR = QR.getNodeValue();
            if (useQR.startsWith(AppUiElementType.Reference))
                useQR = mRuntime.getValue(useQR.replace(AppUiElementType.Reference, ""));

            accountBox.setUseQR(useQR.equalsIgnoreCase(AppUiElementType.TRUE));
        } catch (Exception e) {
            e.printStackTrace();
        }

        // [#useLN]
        try {
            Node LN = attributes.getNamedItem(AppUiElementType.useLN);
            String useLN = LN.getNodeValue();
            if (useLN.startsWith(AppUiElementType.Reference))
                useLN = mRuntime.getValue(useLN.replace(AppUiElementType.Reference, ""));

            accountBox.setUseLN(useLN.equalsIgnoreCase(AppUiElementType.TRUE));
        } catch (Exception e) {
            e.printStackTrace();
        }

        // [#hint]
        try {
            Node hint = attributes.getNamedItem(AppUiElementType.hint);
            String hin = hint.getNodeValue();
            if (hin.startsWith(AppUiElementType.Reference))
                hin = mRuntime.getValue(hin.replace(AppUiElementType.Reference, ""));

            accountBox.setHint(hin);
        } catch (Exception e) {
            e.printStackTrace();
        }

        // [#statistics]
        try {
            Node statisticsNode = attributes.getNamedItem(AppUiElementType.statistics);
            String statisticsStr = statisticsNode.getNodeValue();
            if (statisticsStr.startsWith(AppUiElementType.Reference))
                statisticsStr = mRuntime.getValue(statisticsStr.replace(AppUiElementType.Reference,
                    ""));

            accountBox.setStatistics(statisticsStr);
        } catch (Exception e) {
            e.printStackTrace();
        }

        // [#onInputFinish]
        try {
            Node hint = attributes.getNamedItem(AppUiElementType.onInputFinish);
            String onInputFinish = hint.getNodeValue();
            if (onInputFinish.startsWith(AppUiElementType.Reference))
                onInputFinish = mRuntime.getValue(onInputFinish.replace(AppUiElementType.Reference,
                    ""));

            accountBox.setOnInputFinish(onInputFinish);
        } catch (Exception e) {
            e.printStackTrace();
        }

        accountBox.addTextChangedListener(this.pageContext.getInputBoxIsNullListener());
        if (accountBox.getEnable()) {
            this.pageContext.getInputBoxIsNullListener().addNeedCheckView(accountBox);
        }

        return accountBox;
    }

    private View getViewforButton(Node node, LinearLayout parent) {
        UIButton button = new UIButton(this.pageContext.getRawContext());

        // Get all attributes of this node.
        NamedNodeMap attributes = node.getAttributes();

        try {
            Node buttonClass = attributes.getNamedItem(AppUiElementType.classs);
            String value = buttonClass.getNodeValue();
            if (value.startsWith(AppUiElementType.Reference))
                value = mRuntime.getValue(value.replace(AppUiElementType.Reference, ""));
            button.init((ViewGroup) parent, value);
        } catch (Exception e) {
            e.printStackTrace();
        }

        setCommonAttributes(button, attributes);

        // set the text string for this button.It may be come form raw text,
        // indirect text, direct expression
        // or direct expression.
        try {
            Node text = attributes.getNamedItem(AppUiElementType.text);
            String value = text.getNodeValue();
            if (value.startsWith(AppUiElementType.Reference))
                value = mRuntime.getValue(value.replace(AppUiElementType.Reference, ""));

            if (isExpression(value)) {
                // add this element to the delayGetValueGroup of the page.
                button.set_Tag(AppUiElementType.key_for_text, value);
                this.pageContext.add2delayGetValueGroup(button);
            } else {
                // set value here !why set it at init() again???
                button.setText(value);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        // set the type string for this button.
        try {
            Node type = attributes.getNamedItem(AppUiElementType.type);
            if (type != null) {
                String value = type.getNodeValue();
                if (value != null && value.startsWith(AppUiElementType.Reference))
                    value = mRuntime.getValue(value.replace(AppUiElementType.Reference, ""));
                button.setButtonType(value);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        // set the expression string for this button.
        try {
            Node onClick = attributes.getNamedItem(AppUiElementType.onClick);
            String value = onClick.getNodeValue();
            if (value.startsWith(AppUiElementType.Reference))
                value = mRuntime.getValue(value.replace(AppUiElementType.Reference, ""));

            button.setExpression(value);
        } catch (Exception e) {
            e.printStackTrace();
        }

        // set monitor
        try {
            Node monitor = attributes.getNamedItem(AppUiElementType.Monitor);
            if (monitor != null) {
                String value = monitor.getNodeValue();
                if (value.startsWith(AppUiElementType.Reference))
                    value = mRuntime.getValue(value.replace(AppUiElementType.Reference, ""));

                button.setMonitor(value);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        Node enable = attributes.getNamedItem(AppUiElementType.enable);
        if ("submit".equals(button.getButtonType())
            && (enable == null || (enable != null && !enable.getNodeValue().equalsIgnoreCase(
                "false"))))
            this.pageContext.getInputBoxIsNullListener().addNeedEnabledButton(button);

        button.setOnClickListener(this.pageContext.getOnClickListener());
        return button;
    }

    private boolean isExpression(String value) {
        return value.startsWith(AppUiElementType.expressionTag);
    }
}
